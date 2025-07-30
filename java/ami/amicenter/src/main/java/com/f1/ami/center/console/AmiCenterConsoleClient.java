package com.f1.ami.center.console;

import java.io.IOException;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsResponse;
import com.f1.ami.amicommon.msg.AmiCenterRequest;
import com.f1.ami.amicommon.msg.AmiCenterResponse;
import com.f1.ami.center.AmiCenterState;
import com.f1.ami.center.dialects.AmiDbDialect;
import com.f1.ami.center.dialects.AmiDbDialectPlugin;
import com.f1.ami.center.table.AmiImdbScriptManager;
import com.f1.console.impl.TelnetInputStream;
import com.f1.console.impl.TelnetOutputStream;
import com.f1.console.impl.TelnetShellConnection;
import com.f1.console.impl.shell.ShellAutoCompleter;
import com.f1.console.impl.shell.ShellAutoCompletion;
import com.f1.console.impl.shell.UserShellCtrlBreakListener;
import com.f1.container.ContainerTools;
import com.f1.container.ResultActionFuture;
import com.f1.container.ResultMessage;
import com.f1.container.exceptions.ContainerInterruptedException;
import com.f1.container.exceptions.ContainerTimeoutException;
import com.f1.utils.AsciiPrintln;
import com.f1.utils.CH;
import com.f1.utils.DateFormatNano;
import com.f1.utils.EH;
import com.f1.utils.Formatter;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.Println;
import com.f1.utils.SH;
import com.f1.utils.formatter.BasicNumberFormatter;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.FlowControlThrow;

public class AmiCenterConsoleClient implements Runnable, UserShellCtrlBreakListener, ShellAutoCompleter {

	final private TelnetShellConnection console;
	final private Socket socket;
	final private AsciiPrintln out;
	private volatile Thread thread;
	final private AmiCenterConsole manager;
	final private String sessionUid;
	private String username;

	private List<AmiCenterConsoleCmd> commands = new ArrayList<AmiCenterConsoleCmd>();
	private boolean isClosed = false;
	private long querySessionId;
	private String password;
	private int multiLineCount;
	private TelnetOutputStream telnetOut;
	private TelnetInputStream telnetIn;
	private byte permissions;

	public AmiCenterConsoleClient(Socket socket, AmiCenterConsole manager, String sessionGuid) throws IOException {
		this.socket = socket;
		this.manager = manager;
		this.sessionUid = sessionGuid;
		this.telnetOut = new TelnetOutputStream(socket.getOutputStream());
		this.telnetIn = new TelnetInputStream(socket.getInputStream());
		this.prompt = manager.getPrompt();
		this.console = new TelnetShellConnection(telnetIn, telnetOut, null, this, prompt + "> ");
		this.out = new AsciiPrintln(telnetOut);
		AmiCenterState state = manager.getState();
		AmiImdbScriptManager service = state.getScriptManager();
		addLocalSetting("multiline", "off", String.class);
		addLocalSetting("unprintable_chars", "marker", String.class);
		addLocalSetting("dialect", "", String.class);
		addLocalSetting("datetime_format", "yyyy-MM-dd HH:mm:ss.SSS z", String.class);
		addLocalSetting("timezone", service.getTimezoneId(), String.class);
		addLocalSetting("number_format", "", String.class);
		addLocalSetting("decimal_format", "", String.class);
		addLocalSetting("console_timeout_multiplier", "1.5", Double.class);
		commands.add(new AmiCenterConsoleCmd_Quit());
		commands.add(new AmiCenterConsoleCmd_Login());
		commands.add(new AmiCenterConsoleCmd_Help());
		commands.add(new AmiCenterConsoleCmd_ShowHistory());
		commands.add(new AmiCenterConsoleCmd_Sleep());
		commands.add(new AmiCenterConsoleCmd_SetLocal());
		commands.add(new AmiCenterConsoleCmd_HelpTopic("amisql", "amisql language", "Please visit https://docs.3forge.com/mediawiki/AMI_Realtime_Database\n"));
		commands.add(new AmiCenterConsoleCmd_Sql());
		for (AmiCenterConsoleCmd i : commands)
			i.init(this);
	}
	public AmiCenterConsole getManager() {
		return this.manager;
	}

	@Override
	public void run() {
		console.getUserShell().addCtrlBreakListener(this);
		this.thread = Thread.currentThread();
		this.manager.onLogin(this, this.socket.getRemoteSocketAddress().toString());
		while (!isClosed) {
			try {
				final String line;
				if (isMultiline) {
					StringBuilder sb = new StringBuilder();
					this.console.getUserShell().setPrompt(prompt + " 1> ");
					this.multiLineCount = 0;
					for (;;) {
						String linePart = console.readLineNoThrow(true);
						if (linePart == null) {
							close();
							return;
						}
						if (multiLineCount == 0)
							sb.setLength(0);
						if (SH.isnt(linePart)) {
							line = SH.trim(sb.toString());
							break;
						} else {
							if (sb.length() > 0)
								sb.append('\n');
							sb.append(linePart);
							this.multiLineCount++;
							int n = multiLineCount + 1;
							if (n < 10)
								this.console.getUserShell().setPrompt("      " + n + "> ");
							else if (n < 100)
								this.console.getUserShell().setPrompt("     " + n + "> ");
							else if (n < 1000)
								this.console.getUserShell().setPrompt("    " + n + "> ");
							else
								this.console.getUserShell().setPrompt("   " + n + "> ");
						}
					}
				} else {
					String linePart = console.readLineNoThrow(true);
					if (linePart == null) {
						close();
						return;
					}
					if (SH.isnt(linePart))
						continue;
					line = SH.trim(linePart);
				}
				String[] parts = SH.splitContinous(' ', line);
				AmiCenterConsoleCmd runCmd = null;
				for (int i = 0; i < this.commands.size(); i++) {
					AmiCenterConsoleCmd cmd = this.commands.get(i);
					if (cmd.matches(parts)) {
						runCmd = cmd;
						break;
					}
				}
				if (runCmd == null) {
					out.println("Unrecognized command(type HELP for help):  " + line);
				} else {
					runCmd.process(this, line, parts);
				}
				if (!(runCmd instanceof AmiCenterConsoleCmd_Login)) {
					this.manager.onCommand(this, line);
				} else if (this.getUsername() != null)
					this.console.getUserShell().setHistory(this.manager.getHistory(this.getUsername()));
				out.flush();
			} catch (Exception e) {
				SH.printStackTrace("", "  ", e, out);
			}
		}
	}
	@Override
	public void onCtrlBreakListenerDuringReadline(int code) {
		if (this.isMultiline) {
			this.multiLineCount = 0;
			this.console.getUserShell().setPrompt(prompt + " 1> ");
		}
	}
	public void close() {
		this.isClosed = true;
		this.thread = null;
		this.manager.onLogout(this);
		IOH.close(socket);
		terminateSession();
	}
	public boolean hasSession() {
		return this.querySessionId > 0;
	}
	public void terminateSession() {
		if (hasSession()) {
			AmiCenterQueryDsRequest request = getTools().nw(AmiCenterQueryDsRequest.class);
			request.setDatasourceName("AMI");
			request.setQuerySessionKeepAlive(false);
			request.setQuery(null);
			request.setPermissions(permissions);
			request.setType(AmiCenterQueryDsRequest.TYPE_QUERY);
			request.setOriginType(AmiCenterQueryDsRequest.ORIGIN_CMDLINE);
			request.setQuerySessionId(this.querySessionId);
			this.querySessionId = 0;
			sendToAmiStateNoResponse(request);
		}
	}

	@Override
	public void onCtrlBreakListener(int ctrlPressedCount, int code) {
		if (this.thread != null)
			thread.interrupt();
	}

	public String getSessionUid() {
		return this.sessionUid;
	}

	public String getUsername() {
		return this.username;
	}

	@Override
	public ShellAutoCompletion autoComplete(String partialText) {
		return null;
		//		String[] parts = SH.splitContinous(' ', partialText);
		//		List<String> r = new ArrayList<String>();
		//		for (int i = 0; i < this.commands.size(); i++) {
		//			AmiCenterConsoleCmd cmd = this.commands.get(i);
		//			String remaining = cmd.getAutocomplete(parts);
		//			if (remaining != null)
		//				r.add(remaining);
		//		}
		//		if (r.size() == 0)
		//			return BasicShellAutoCompletion.EMPTY;
		//		else if (r.size() == 1 && r.get(0).indexOf('<') == -1)
		//			return new BasicShellAutoCompletion("", CH.first(r));
		//		else {
		//			StringBuilder sb = new StringBuilder();
		//			String t = null;
		//			for (String i : r)
		//				if (t == null)
		//					t = SH.beforeFirst(i, '<', i);
		//				else
		//					t = SH.commonPrefix(t, SH.beforeFirst(i, '<', i));
		//			if (t.length() == 0)
		//				for (String s : r)
		//					sb.append(" ").append(partialText).append(s).append('\n');
		//			return new BasicShellAutoCompletion(sb.toString(), t);
		//		}
		//
	}

	public List<AmiCenterConsoleCmd> getCommands() {
		return this.commands;
	}

	public Println getOutputStream() {
		return this.out;
	}

	public List<String> getHistory() {
		return this.console.getUserShell().getHistory();
	}

	public void sendToAmiStateNoResponse(AmiCenterRequest request) {
		request.setInvokedBy(getUsername());
		request.setRequestTime(System.currentTimeMillis());
		ResultActionFuture<AmiCenterResponse> future = manager.getItineraryPort().requestWithFuture(request, null);
	}
	public <T extends AmiCenterResponse> T sendToAmiState(AmiCenterRequest request, Class<T> returnType) {
		if (getUsername() == null) {
			out.println("Login required: login username <password>  (ex: login demo demo123)");
			return null;
		}
		request.setInvokedBy(getUsername());
		request.setRequestTime(System.currentTimeMillis());
		ResultActionFuture<AmiCenterResponse> future = manager.getItineraryPort().requestWithFuture(request, null);
		ResultMessage<AmiCenterResponse> response;
		int timeout = getLocalSetting("timeout", Integer.class);
		double timeout2 = getLocalSetting("console_timeout_multiplier", Double.class);
		int totalTime = (int) (timeout * timeout2);
		try {
			response = future.getResult(totalTime);
		} catch (ContainerInterruptedException e) {
			out.println("<Console Interrupted>");
			return null;
		} catch (ContainerTimeoutException e) {
			out.println("<Console timeout> (see setlocal timeout and console_timeout_multiplier)");
			return null;
		}
		AmiCenterQueryDsResponse action = (AmiCenterQueryDsResponse) response.getAction();
		if (querySessionId > 0 && action.getQuerySessionId() != querySessionId) {
			out.println("*** SESSION HAD EXPIRED AND TEMPORARY TABLES REMOVED. NEW SESSION CREATED ***");
		}
		if (!action.getOk()) {
			toErrorString(action, out);
			return (T) action;
		}
		return (T) action;
	}
	static public void toErrorString(AmiCenterQueryDsResponse action, Println out) {
		Exception exception = action.getException();
		if (exception != null) {
			if (exception instanceof ExpressionParserException)
				out.println(((ExpressionParserException) exception).toLegibleString());
			else if (exception instanceof AmiDatasourceException) {
				if (exception.getCause() != null)
					out.println(exception.getCause().getMessage());
				else
					out.println("DATASOURCE ERROR: " + ((AmiDatasourceException) exception).getMessage());
			} else if (exception instanceof FlowControlThrow) {
				out.println("Uncaught error: " + exception.toString());
			} else
				out.println(SH.printStackTrace(exception));
		} else if (SH.is(action.getMessage()))
			out.println(action.getMessage());
		else
			out.println("Unknown error");
	}
	public ContainerTools getTools() {
		return manager.getTools();
	}
	public long getQuerySessionId() {
		return this.querySessionId;
	}
	public void setQuerySessionId(long querySessionId) {
		this.querySessionId = querySessionId;
	}
	public String promptForPassword(String string) {
		return this.console.getUserShell().readPassword(string);
	}
	public void setUsernamePassword(String username, String password, byte permissions, Map<String, Object> sessionVariables, Map<String, Class> sessionVariableTypes) {
		this.sessionVariables = sessionVariables;
		this.sessionVariableTypes = sessionVariableTypes;
		this.username = username;
		this.password = password;
		this.permissions = permissions;
		this.querySessionId = 0;
	}
	public String getRemoteLocation() {
		return this.socket.getRemoteSocketAddress().toString();
	}

	private TreeMap<String, Object> localSettings = new TreeMap<String, Object>();
	private TreeMap<String, Class> localSettingTypes = new TreeMap<String, Class>();
	private boolean isMultiline = false;
	private AmiDbDialect dialect;
	private TimeZone timezone;
	private DateFormatNano datetimeFormat;
	private Map<String, String> directives = CH.m("ds", "chinook");
	private Formatter decimalFormat;
	private Formatter numberFormat;
	private Map<String, Object> sessionVariables;
	private Map<String, Class> sessionVariableTypes;
	private String prompt;

	public Set<String> getLocalSettings() {
		return localSettings.keySet();
	}

	public <T> T getLocalSetting(String key) {
		return (T) localSettings.get(key);
	}
	public <T> T getLocalSetting(String key, Class<T> type) {
		OH.assertEq(this.localSettingTypes.get(key), type);
		return (T) localSettings.get(key);
	}
	protected void addLocalSetting(String key, String val, Class type) {
		CH.putOrThrow(this.localSettingTypes, key, type);
		StringBuilder sink = new StringBuilder();
		if (!setLocalSetting(key, val, sink))
			throw new RuntimeException(sink.toString());
	}
	public boolean setLocalSetting(String key, String val, StringBuilder sink) {
		Class type = this.localSettingTypes.get(key);
		if (type == null) {
			sink.append("unknown setting");
			return false;
		}
		Object val2;
		try {
			val2 = OH.cast(val, type);
		} catch (Exception e) {
			sink.append("Value must be " + type.getName());
			return false;
		}
		if (!verifyLocalSetting(key, val2, sink))
			return false;
		for (AmiCenterConsoleCmd i : this.commands) {
			if (!i.verifyLocalSetting(key, val2, sink))
				return false;
		}
		localSettings.put(key, val2);
		onLocalSettingChanged(key, val2);
		for (AmiCenterConsoleCmd i : this.commands)
			i.onLocalSettingChanged(key, val2);
		sink.append(key).append("=").append(val2);
		return true;
	}
	private void onLocalSettingChanged(String key, Object val2) {
		if ("multiline".equals(key)) {
			this.isMultiline = "on".equalsIgnoreCase((String) val2);
			this.console.getUserShell().setPrompt(prompt + (this.isMultiline ? " 1> " : "> "));
		}
		if ("timezone".equals(key)) {
			this.timezone = EH.getTimeZone((String) val2);
			if (this.datetimeFormat != null)
				this.datetimeFormat.setTimeZone(this.timezone);
		}
		if ("datetime_format".equals(key)) {
			if (SH.isnt(val2))
				this.datetimeFormat = null;
			else {
				this.datetimeFormat = new DateFormatNano((String) val2);
				this.datetimeFormat.setTimeZone(this.timezone);
			}
		}
		if ("decimal_format".equals(key)) {
			if (SH.isnt(val2))
				this.decimalFormat = null;
			else {
				this.decimalFormat = new BasicNumberFormatter(new DecimalFormat((String) val2));
			}
		}
		if ("number_format".equals(key)) {
			if (SH.isnt(val2))
				this.numberFormat = null;
			else {
				this.numberFormat = new BasicNumberFormatter(new DecimalFormat((String) val2));
			}
		}
		if ("unprintable_chars".equals(key)) {
			if ("hide".equalsIgnoreCase((String) val2))
				this.out.setUnprintableMode(AsciiPrintln.OPTION_UNPRINTABLE_SKIP);
			else if ("show".equalsIgnoreCase((String) val2))
				this.out.setUnprintableMode(AsciiPrintln.OPTION_UNPRINTABLE_PRINT);
			else if ("marker".equalsIgnoreCase((String) val2))
				this.out.setUnprintableMode(AsciiPrintln.OPTION_UNPRINTABLE_MARKER);
			else if ("show_code".equalsIgnoreCase((String) val2))
				this.out.setUnprintableMode(AsciiPrintln.OPTION_UNPRINTABLE_CODE);
		}
		if ("dialect".equals(key)) {
			AmiDbDialectPlugin t = this.manager.getDialects().get((String) val2);
			this.dialect = t == null ? null : t.createDialectInstance();
		}
	}
	public Class getLocalSettingType(String key) {
		return this.localSettingTypes.get(key);
	}

	private boolean verifyLocalSetting(String key, Object value, StringBuilder sink) {
		if ("timezone".equals(key)) {
			if (EH.getTimeZoneNoThrow((String) value) == null) {
				sink.append("unknown timezone");
				return false;
			}
			return true;
		}
		if ("console_timeout_multiplier".equals(key)) {
			Double val = (Double) value;
			if (val < 0) {
				sink.append("console_timeout_multiplier must not be negative");
				return false;
			}
			return true;
		}

		if ("datetime_format".equals(key)) {
			try {
				if (SH.is(value))
					new DateFormatNano((String) value);
				return true;
			} catch (Exception e) {
				sink.append("Invalid format: " + e.getMessage());
				return false;
			}
		}
		if ("number_format".equals(key)) {
			try {
				if (SH.is(value))
					new BasicNumberFormatter(new DecimalFormat((String) value));
				return true;
			} catch (Exception e) {
				sink.append("Invalid format: " + e.getMessage());
				return false;
			}
		}
		if ("decimal_format".equals(key)) {
			try {
				if (SH.is(value))
					new BasicNumberFormatter(new DecimalFormat((String) value));
				return true;
			} catch (Exception e) {
				sink.append("Invalid format: " + e.getMessage());
				return false;
			}
		}
		if ("multiline".equals(key)) {
			if ("off".equalsIgnoreCase((String) value) || "on".equalsIgnoreCase((String) value))
				return true;
			sink.append("multiline must by 'on' or 'off'");
			return false;
		}
		if ("unprintable_chars".equals(key)) {
			if ("marker".equalsIgnoreCase((String) value) || "hide".equalsIgnoreCase((String) value) || "show_code".equalsIgnoreCase((String) value)
					|| "show".equalsIgnoreCase((String) value))
				return true;
			sink.append("unprintable_chars must by 'marker', 'hide' or 'show_code', 'show'");
			return false;
		}
		if ("dialect".equals(key)) {
			if (SH.isnt(value) || this.manager.getDialects().containsKey(value))
				return true;
			sink.append("dialect not found, valid dialects: " + SH.join(",", this.manager.getDialects().keySet()));
			return false;
		}
		return true;
	}
	public AmiDbDialect getDialect() {
		return this.dialect;
	}
	public byte getPermissions() {
		return this.permissions;
	}
	public DateFormatNano getDatetimeFormat() {
		return datetimeFormat;
	}
	public Formatter getDecimalFormat() {
		return decimalFormat;
	}
	public Formatter getNumberFormat() {
		return numberFormat;
	}
	public Map<String, Object> getSessionVariables() {
		return sessionVariables;
	}
	public Map<String, Class> getSessionVariableTypes() {
		return sessionVariableTypes;
	}

}
