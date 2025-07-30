package com.vortex.client;

import java.util.logging.Logger;

import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentProcess;

public class VortexClientProcess extends VortexClientMachineEntity<VortexAgentProcess> {
	private static final Logger log = Logger.getLogger(VortexClientProcess.class.getName());
	final private LongKeyMap<VortexClientNetConnection> connections = new LongKeyMap<VortexClientNetConnection>();
	final private int pid;
	final private String name;

	public VortexClientProcess(VortexAgentProcess data) {
		super(VortexAgentEntity.TYPE_PROCESS, data);
		this.pid = data.getPid() == null ? -1 : Integer.parseInt(data.getPid());
		this.name = parseCommand(data.getCommand());
	}

	public int getPid() {
		return pid;
	}

	public void addConnection(VortexClientNetConnection connection) {
		this.connections.put(connection.getId(), connection);
	}

	public void removeConnection(VortexClientNetConnection r) {
		this.connections.remove(r.getId());
	}

	public Iterable<VortexClientNetConnection> getConnections() {
		return connections.values();
	}

	static public String parseCommand(String command) {
		try {
			if (command == null) {
				return command;
			} else if (command.length() > 0 && command.charAt(0) == '"') {//is windows
				int endQuote = command.indexOf('"', 1);
				if (endQuote == -1)
					return command;
				int start = SH.indexOfLast(command, endQuote, SH.SLASHES);
				return start == -1 ? command.substring(1, endQuote) : command.substring(start + 1, endQuote);
			} else if (SH.startsWith(command, '[')) {
				return command;
			} else {
				int firstSpace = command.indexOf(' ');
				if (firstSpace != -1) {
					int start = SH.indexOfLast(command, firstSpace, SH.SLASHES);
					String cmd = command.substring(start == -1 ? 0 : start + 1, firstSpace);
					if ("java".equals(cmd)) {
						return "java ... " + parseJavaArguments(command.substring(firstSpace + 1));
					} else {
						if (cmd.endsWith(":"))
							return command;
						else
							return cmd;
					}
				} else {
					int start = SH.indexOfLast(command, firstSpace == -1 ? command.length() : firstSpace, SH.SLASHES);
					return start == -1 ? command : command.substring(start + 1);
				}
			}
		} catch (Exception e) {
			LH.warning(log, "Error parsing: ", command, e);
			return command;
		}
	}

	public static String parseJavaArguments(String arguments) {
		String[] parts = SH.splitContinous(' ', arguments);
		if (parts.length < 2)
			return arguments;
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			if (SH.startsWith(part, '-')) {
				if ("-cp".equals(part) || "-classpath".equals(part)) {
					i++;
					continue;
				} else if ("-jar".equals(part) && i + 1 < parts.length) {
					return parts[i + 1];
				} else if (part.startsWith("-Df1.bootstrap.main=")) {
					return part.substring("-Df1.pootstrap.main=".length());
				}

			} else
				return part;
		}
		return arguments;
	}
	public String getName() {
		return name;
	}

	public boolean hasConnections() {
		return !connections.isEmpty();
	}

}
