package com.f1.ami.center.console;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

import com.f1.ami.amicommon.msg.AmiCenterRequest;
import com.f1.ami.amicommon.msg.AmiCenterResponse;
import com.f1.ami.center.AmiCenterProperties;
import com.f1.ami.center.AmiCenterState;
import com.f1.ami.center.dialects.AmiDbDialectPlugin;
import com.f1.ami.web.auth.AmiAuthenticatorPlugin;
import com.f1.container.ContainerTools;
import com.f1.container.RequestOutputPort;
import com.f1.utils.BasicLocaleFormatter;
import com.f1.utils.CircularList;
import com.f1.utils.EH;
import com.f1.utils.FastBufferedReader;
import com.f1.utils.GuidHelper;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.ServerSocketAcceptor;
import com.f1.utils.ServerSocketEntitlements;

public class AmiCenterConsole extends ServerSocketAcceptor {

	private static final Logger log = LH.get();
	private static final char ACTION_STARTUP = 'S';
	private static final char ACTION_COMMAND = 'C';
	private static final char ACTION_LOGIN = 'I';
	private static final char ACTION_LOGOUT = 'O';
	private Executor executor;
	private List<AmiCenterConsoleClient> clients = new ArrayList<AmiCenterConsoleClient>();
	private ContainerTools tools;
	private File historyDir;
	private File historyFile;
	final private Map<String, CircularList<String>> history = new HashMap<String, CircularList<String>>();
	private int maxHistorySize;
	private final RequestOutputPort<AmiCenterRequest, AmiCenterResponse> itineraryPort;
	private Map<String, AmiDbDialectPlugin> dialects;
	private boolean historyIsSecure;

	public AmiCenterConsole(Executor executor, String portBindAddr, ServerSocketEntitlements ssEntitlements, int port, File keystore, String keystorePassword, AmiCenterState s,
			ContainerTools tools, RequestOutputPort<AmiCenterRequest, AmiCenterResponse> iport, AmiAuthenticatorPlugin authenticator, Map<String, AmiDbDialectPlugin> dialects) {
		super(portBindAddr, ssEntitlements, port, keystore, keystorePassword);
		this.dialects = dialects;
		this.authenticator = authenticator;
		this.tools = tools;
		this.executor = executor;
		this.state = s;
		String promptSuffix = this.tools.getOptional(AmiCenterProperties.PROPERTY_AMI_DB_CONSOLE_POMPT, "");
		this.prompt = "AMIDB" + promptSuffix;
		this.historyDir = this.tools.getOptional(AmiCenterProperties.PROPERTY_AMI_CONSOLE_HISTORY_DIR, new File("./history"));
		this.maxHistorySize = this.tools.getOptional(AmiCenterProperties.PROPERTY_AMI_CONSOLE_HISTORY_MAX_LINES_COUNT, 10000);
		this.historyFile = new File(this.historyDir, "ami_console.history");
		this.itineraryPort = iport;
		LH.info(log, "History File: ", IOH.getFullPath(this.historyFile));
		readHistory();
	}

	//File Format:  timestamp|action|session|user|text
	//Action is either: S, I, O
	private void readHistory() {
		try {
			IOH.ensureDir(this.historyDir);
			if (this.historyFile.canRead()) {
				StringBuilder sink = new StringBuilder();
				FastBufferedReader reader;
				reader = new FastBufferedReader(new FileReader(this.historyFile));
				while (reader.readLine(SH.clear(sink))) {
					try {
						int actionIndex = SH.indexOf(sink, '|', 0) + 1;
						switch (sink.charAt(actionIndex)) {
							case ACTION_COMMAND: {
								int startGuid = SH.indexOf(sink, '|', actionIndex) + 1;
								if (startGuid == 0)
									throw new RuntimeException("syntax error");
								int startUser = SH.indexOf(sink, '|', startGuid) + 1;
								if (startUser == 0)
									throw new RuntimeException("syntax error");
								int startCmd = SH.indexOf(sink, '|', startUser) + 1;
								if (startCmd == 0)
									throw new RuntimeException("syntax error");
								String user = sink.substring(startUser, startCmd - 1);
								String cmd = sink.substring(startCmd);
								if (SH.is(cmd) && SH.is(user)) {
									CircularList<String> list = history.get(user);
									if (list == null)
										history.put(user, list = new CircularList<String>());
									list.add(cmd);
									while (list.size() > this.maxHistorySize)
										list.remove(0);
								}
								break;
							}
							case ACTION_STARTUP:
							case ACTION_LOGIN:
							case ACTION_LOGOUT:
								continue;
							default:
								throw new RuntimeException("Unknown Action type");
						}
					} catch (Exception e) {
						LH.warning(log, "Error processing history file line: ", IOH.getFullPath(this.historyFile), " ==> ", sink, e);
					}
				}
				IOH.close(reader);
			}
			this.historyWriter = new FileWriter(this.historyFile, true);
			writeToHistory(ACTION_STARTUP, EH.getProcessUid(), "", "PID=" + EH.getPid());
		} catch (Exception e) {
			LH.warning(log, "Error processing history file: ", IOH.getFullPath(this.historyFile), e);
		}
	}
	public void start() throws IOException {
		super.start();
		System.out.println("To access this AMIDB via command line: telnet " + EH.getLocalHost() + " " + getServerLocalPort());
	}
	public AmiCenterConsole(Executor executor, String portBindAddr, ServerSocketEntitlements ssEntitlements, int port, AmiCenterState s, ContainerTools tools,
			RequestOutputPort<AmiCenterRequest, AmiCenterResponse> ip, AmiAuthenticatorPlugin auth, Map<String, AmiDbDialectPlugin> dialects) {
		this(executor, portBindAddr, ssEntitlements, port, null, null, s, tools, ip, auth, dialects);
	}

	private AmiCenterState state;

	@Override
	public String getDescription() {
		return getIsSecure() ? "Secure Ami Command Line Interface" : "AMI Command Line Interface";
	}

	@Override
	protected void accept(Socket socket) throws IOException {
		String sessionGuid = "SS-" + GuidHelper.getGuid(62);
		AmiCenterConsoleClient client = new AmiCenterConsoleClient(socket, this, sessionGuid);
		synchronized (this.clients) {
			this.clients.add(client);
		}
		executor.execute(client);
	}

	private final StringBuilder buf = new StringBuilder();
	private final SimpleDateFormat sdf = new SimpleDateFormat(BasicLocaleFormatter.DATETIME_FULL_FORMAT);
	private final Date tmpDate = new Date();
	private Writer historyWriter;
	private AmiAuthenticatorPlugin authenticator;
	final private String prompt;

	private void writeToHistory(char action, String sessionGuid, String user, String string) {
		if (this.historyWriter != null)
			synchronized (this.history) {
				SH.clear(buf);
				tmpDate.setTime(System.currentTimeMillis());
				buf.append(sdf.format(tmpDate));
				buf.append('|');
				buf.append(action);
				buf.append('|');
				buf.append(sessionGuid);
				buf.append('|');
				buf.append(user);
				buf.append('|');
				buf.append(string);
				buf.append(SH.NEWLINE);
				try {
					this.historyWriter.append(buf);
					this.historyWriter.flush();
				} catch (IOException e) {
					LH.warning(log, "Error appending to ", IOH.getFullPath(this.historyFile), " so closing ==> ", buf);
					IOH.close(this.historyWriter);
					this.historyWriter = null;
				}
			}
	}
	public List<String> getHistory(String user) {
		synchronized (this.history) {
			CircularList<String> r = this.history.get(user);
			return r == null ? null : new ArrayList<String>(r);
		}
	}

	public void onCommand(AmiCenterConsoleClient client, String lines) {
		String username = client.getUsername();
		if (SH.isnt(username))
			return;
		synchronized (this.history) {
			for (String line : SH.splitLines(lines)) {
				if (SH.isnt(line))
					continue;
				CircularList<String> list = history.get(username);
				if (list == null)
					history.put(username, list = new CircularList<String>());
				list.add(line);
				while (history.size() > this.maxHistorySize)
					history.remove(0);
				writeToHistory(ACTION_COMMAND, client.getSessionUid(), username, line);
			}
		}
	}
	public void onLogout(AmiCenterConsoleClient client) {
		writeToHistory(ACTION_LOGOUT, client.getSessionUid(), client.getUsername(), "");
	}
	public void onLogin(AmiCenterConsoleClient client, String remoteAddress) {
		writeToHistory(ACTION_LOGIN, client.getSessionUid(), client.getUsername(), SH.trim('/', remoteAddress));
	}

	public AmiCenterState getState() {
		return this.state;
	}

	public ContainerTools getTools() {
		return this.tools;
	}

	public RequestOutputPort<AmiCenterRequest, AmiCenterResponse> getItineraryPort() {
		return this.itineraryPort;
	}

	public AmiAuthenticatorPlugin getAuthenticator() {
		return this.authenticator;
	}

	public Map<String, AmiDbDialectPlugin> getDialects() {
		return this.dialects;
	}

	public String getPrompt() {
		return this.prompt;
	}

}
