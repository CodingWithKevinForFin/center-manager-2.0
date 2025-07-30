package com.f1.ami.center.console;

import com.f1.utils.SH;

public class AmiCenterConsoleCmd_Help extends AmiCenterConsoleCmd {

	public AmiCenterConsoleCmd_Help() {
		super("help", "print help on available commands.");
	}

	@Override
	public void process(AmiCenterConsoleClient client, String cmd, String[] cmdParts) {
		if (cmdParts.length > 1) {
			String cmdPart = cmdParts[1];
			client.getCommands();
		}
		StringBuilder sb = new StringBuilder();
		int len = 0;
		for (AmiCenterConsoleCmd c : client.getCommands())
			len = Math.max(len, c.getCommand().length());
		len += 2;
		for (AmiCenterConsoleCmd c : client.getCommands()) {
			SH.rightAlign(' ', c.getCommand(), len, false, sb);
			sb.append(" - ").append(c.getHelp()).append('\n');
		}
		client.getOutputStream().append(sb);
	}
}
