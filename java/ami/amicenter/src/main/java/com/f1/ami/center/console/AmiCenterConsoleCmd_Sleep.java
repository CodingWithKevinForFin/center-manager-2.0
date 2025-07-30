package com.f1.ami.center.console;

import com.f1.utils.OH;
import com.f1.utils.Println;

public class AmiCenterConsoleCmd_Sleep extends AmiCenterConsoleCmd {

	public AmiCenterConsoleCmd_Sleep() {
		super("sleep <millis>", "sleep for specified number of milliseconds");
	}

	@Override
	public void process(AmiCenterConsoleClient client, String cmd, String[] cmdParts) {
		Println out = client.getOutputStream();
		int timeoutMs;
		try {
			timeoutMs = Integer.parseInt(cmdParts[1]);
		} catch (Exception e) {
			out.println("Error running SLEEP command, invalid number: " + cmdParts[1]);
			return;
		}
		out.print("Sleeping " + timeoutMs + " milliseconds...");
		if (timeoutMs > 0)
			OH.sleep(timeoutMs);
		out.println("...Awake");
	}
}
