package com.f1.testing.cmdtester;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.f1.bootstrap.ContainerBootstrap;
import com.f1.console.impl.ShowThreadsConsoleService;
import com.f1.console.impl.StdConsole;
import com.f1.console.impl.TelnetOutputStream;
import com.f1.console.impl.TelnetShellConnection;
import com.f1.console.impl.shell.UserShellCtrlBreakListener;
import com.f1.container.Container;
import com.f1.container.impl.BasicContainer;
import com.f1.utils.ArgParser;
import com.f1.utils.ArgParser.Arguments;
import com.f1.utils.CH;
import com.f1.utils.CharReader;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.casters.Caster_File;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.structs.table.BasicTable;

public class CmdTesterMain {

	private Integer serverPort;
	private Map<String, CmdTester> commands = new TreeMap<String, CmdTester>();

	private FileOutputStream stdoutFile;
	private boolean tty;
	private SimpleDateFormat sdf;
	private File historyPath;
	private List<String> history;
	private Container container;
	private ContainerBootstrap bs;
	private File path;

	public static void main(String a[]) throws Exception {

		ContainerBootstrap bs = new ContainerBootstrap(CmdTesterMain.class, a);
		bs.setF1MonitoringEnabledProperty(false);
		System.setProperty("property.f1.logging.mode", "warning");
		Container c = new BasicContainer();
		bs.startup();
		bs.startupContainer(c);
		CmdTesterMain cs = new CmdTesterMain();
		cs.init(a, bs, c);
		cs.go();
	}
	public ContainerBootstrap getBootstrap() {
		return this.bs;
	}

	public void init(String[] a, ContainerBootstrap bs, Container c) throws Exception {

		this.container = c;
		this.bs = bs;
		this.sdf = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
		ArgParser argp = new ArgParser("Smart Finance Data Loader");
		argp.addSwitchRequired("d", "datadir", "*", "Path to store data files");
		argp.addSwitchOptional("r", "raw", "*", "use raw input instead of interative mode");
		argp.addSwitchOptional("p", "port", "*", "server port to open for telnet connection");
		Arguments props = argp.parseNoThrow(a);
		if (props == null)
			return;
		this.path = props.getRequired("d", File.class);
		IOH.ensureDir(path);
		File db = props.getOptional("db", Caster_File.INSTANCE);
		this.serverPort = props.getOptional("p", Caster_Integer.INSTANCE);
		tty = props.getOptional("r") == null;

		this.historyPath = new File(path, "history.txt");
		this.history = new ArrayList<String>();
		if (historyPath.isFile())
			for (String line : SH.splitLines(IOH.readText(historyPath)))
				history.add(SH.afterFirst(line, ' '));

		registerCommand(new CmdTester_SqlAddTable());
		registerCommand(new CmdTester_Sql());
		registerCommand(new CmdTester_SqlShow());
	}

	public void go() throws IOException {

		this.sdf = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");

		StdConsole console = null;
		LineNumberReader reader = new LineNumberReader(new InputStreamReader(System.in));

		TelnetShellConnection telnet = null;
		if (this.serverPort != null) {
			ServerSocket ss = new ServerSocket(this.serverPort);
			System.out.println("### LISTENING FOR TELNET CONNECTION ON: " + this.serverPort);
			Socket socket = ss.accept();
			telnet = new TelnetShellConnection(socket.getInputStream(), socket.getOutputStream(), history, null, "cmdtester>");
			System.out.println("### CONNECTION ACCEPTED FROM: " + socket.getRemoteSocketAddress());
			System.setOut(new PrintStream(new TelnetOutputStream(socket.getOutputStream())));
			System.setErr(new PrintStream(new TelnetOutputStream(socket.getOutputStream())));
			telnet.getUserShell().addCtrlBreakListener(new CtrlBreakListener());
			ss.close();
		} else if (tty) {
			console = new StdConsole(history, null, "cmdtester> ");
			console.getUserShell().addCtrlBreakListener(new CtrlBreakListener());
		}

		while (true) {
			try {
				String line = console == null ? telnet == null ? reader.readLine() : telnet.readLine() : console.readLineWithPrompt();
				if (line == null && console == null)
					return;
				if (stdoutFile != null) {
					System.out.flush();
					stdoutFile.write(("USER INPUT: " + line + SH.NEWLINE).getBytes());
					stdoutFile.flush();
				}
				boolean async = false;
				if (line.endsWith("&")) {
					async = true;
					line = line.substring(0, line.length() - 1);
				}
				String[] parts = splitLine(line);
				if ("EXIT".equals(line)) {
					System.out.println("good bye");
					System.exit(0);
				}
				if (parts.length == 0)
					continue;
				String cmd = parts[0];
				IOH.appendText(historyPath, sdf.format(new Date()) + ">> " + line + SH.NEWLINE);

				CmdTester t = commands.get(cmd);
				if (t != null) {
					Arguments arguments = t.getArgumentParser().parseNoThrow(parts);
					if (arguments == null) {
						//System.out.println("#### " + SH.NEWLINE + t.getArgumentParser().toLegibleString());
						continue;
					}
					CommandRunner cr = new CommandRunner(t, arguments);
					if (async) {
						System.out.println("#### running async: " + line);
						new Thread(cr).start();
					} else
						cr.run();
				} else if (cmd.equals("EXIT")) {
					System.out.println("#### Goodbye");
					System.exit(0);

				} else if (cmd.startsWith("INFO")) {
				} else if (cmd.startsWith("HISTORY")) {
					System.out.println(IOH.readText(historyPath));
				} else if (cmd.startsWith("HELP")) {
					if (parts.length > 1) {
						CmdTester c = commands.get(parts[1]);
						if (c != null) {
							System.out.println();
							System.out.println(c.getDescription());
							System.out.println();
							System.out.println(c.getArgumentParser().toLegibleString());
							continue;
						}
					}
					System.out.println("#### COMMANDS:");
					System.out.println("  INFO");
					System.out.println("  HELP");
					System.out.println("  EXIT");
					System.out.println("  EXEC dbcommand");
					System.out.println("  QUERY dbcommand");
					System.out.println("  HISTORY");
					BasicTable table = new BasicTable(new String[] { "Command", "arguments", "description" });
					for (CmdTester i : this.commands.values()) {
						table.getRows().addRow(i.getName(), SH.join(' ', i.getArgumentParser().getOptions()), i.getDescription());
					}
					System.out.println(TableHelper.toString(table, "  ", TableHelper.SHOW_ALL_BUT_TYPES));
				} else {
					System.out.println("#### Unknown command, run HELP for valid commands");
					continue;
				}
			} catch (Exception e) {
				System.out.println(SH.toString(e));
			}

		}

	}
	private static String[] splitLine(String line) {
		List<String> r = new ArrayList<String>();
		StringCharReader reader = new StringCharReader(line);
		StringBuilder buf = new StringBuilder();
		for (;;) {
			SH.clear(buf);
			reader.skip(' ');
			if (reader.peakOrEof() == CharReader.EOF)
				return r.toArray(new String[r.size()]);
			else if (reader.peak() == '"') {
				reader.expect('"');
				reader.readUntilSkipEscaped('"', '\\', buf);
				reader.expect('"');
			} else {
				reader.readUntil(' ', buf);
			}
			r.add(buf.toString());
		}
	}

	private List<Thread> runningThreads = new CopyOnWriteArrayList<Thread>();

	public class CommandRunner implements Runnable {

		private Arguments arguments;
		private CmdTester t;

		public CommandRunner(CmdTester t, Arguments arguments2) {
			this.t = t;
			this.arguments = arguments2;
		}

		@Override
		public void run() {
			final Thread thread = Thread.currentThread();
			long start, end;
			start = System.currentTimeMillis();
			try {
				runningThreads.add(thread);
				t.processCommand(CmdTesterMain.this, arguments);
			} catch (Exception e) {
				System.out.println(SH.printStackTrace(e));
			} finally {
				runningThreads.remove(thread);
				Thread.interrupted();
				end = System.currentTimeMillis();
				System.out.println("Completed in " + (end - start) + " ms");
			}
		}
	}

	private void registerCommand(CmdTester csc) {
		CH.putOrThrow(this.commands, csc.getName(), csc);
	}

	public class CtrlBreakListener implements UserShellCtrlBreakListener {

		@Override
		public void onCtrlBreakListener(int ctrlPressedCount, int code) {
			switch (code) {
				case KEYCODE_CTRL_C: {
					switch (ctrlPressedCount) {
						case 1:
							System.out.println("Press CTRL-C once more to cancel jobs");
							break;
						case 2:
							int cxl = 0, th = 0;
							for (Thread i : runningThreads) {
								i.interrupt();
								th++;
							}
							System.out.println("Interrupted " + th + " threads and " + cxl + " db statement(s)");
							break;
						case 3:
							System.out.println("Press CTRL-C once more to EXIT");
							break;
						case 4:
							System.exit(1);
							break;
					}
					break;
				}
				case KEYCODE_CTRL_P: {
					StringBuilder sb = new StringBuilder();
					sb.append("#### PRINTING STACK TRACE AT " + new Date()).append(SH.NEWLINE);
					for (Thread t : EH.getAllThreads()) {
						sb.append(ShowThreadsConsoleService.toString(t));//TODO: switch to sink
					}
					sb.append("#### END OF STACK TRACE AT " + new Date()).append(SH.NEWLINE);
					System.out.println(sb);
					break;
				}
			}
		}

		@Override
		public void onCtrlBreakListenerDuringReadline(int code) {
		}
	}

	private Map<String, Object> objects = new ConcurrentHashMap<String, Object>();
	private SqlProcessor sqlProcessor = new SqlProcessor();

	public CmdTesterMain() {
		//		BasicMethodFactory mf = (BasicMethodFactory) ;
		//		mf.addVarType("INT", Integer.class);
		//		mf.addVarType("LONG", Long.class);
		//		mf.addVarType("STRING", String.class);
		//		mf.addVarType("FLOAT", Float.class);
		//		mf.addVarType("DOUBLE", Double.class);
		//		mf.addVarType("UTC", DateMillis.class);
		//		mf.addVarType("BOOLEAN", boolean.class);
		//		mf.addVarType("BINARY", byte[].class);
	}

	public Map<String, Object> getObjects() {
		return this.objects;
	}
	public SqlProcessor getSqlProcessor() {
		return sqlProcessor;
	}
}
