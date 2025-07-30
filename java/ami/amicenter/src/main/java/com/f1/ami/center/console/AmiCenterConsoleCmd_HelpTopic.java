package com.f1.ami.center.console;

public class AmiCenterConsoleCmd_HelpTopic extends AmiCenterConsoleCmd {

	private String longHelp;
	public AmiCenterConsoleCmd_HelpTopic(String topic, String shortHelp, String longHelp) {
		super("help " + topic, "print help on " + shortHelp);
		this.longHelp = longHelp;
	}

	@Override
	public void process(AmiCenterConsoleClient client, String cmd, String[] cmdParts) {
		client.getOutputStream().append(longHelp);
	}
}
