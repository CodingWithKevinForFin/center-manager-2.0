package com.f1.ami.center.console;

public class AmiCenterConsoleCmd_Quit extends AmiCenterConsoleCmd {

	public AmiCenterConsoleCmd_Quit() {
		super("quit", "exit this command line interface");
	}

	@Override
	public void process(AmiCenterConsoleClient client, String cmd, String[] cmdParts) {
		client.close();
	}

}
