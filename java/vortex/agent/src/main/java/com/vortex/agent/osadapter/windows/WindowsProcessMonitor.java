package com.vortex.agent.osadapter.windows;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class WindowsProcessMonitor {

	private static final Logger logger = Logger.getLogger(WindowsProcessMonitor.class.getName());

	private PrintStream outputStream;

	private LineNumberReader inputStream;

	public WindowsProcessMonitor(InputStream in, OutputStream out) {
		init(in, out);
	}

	private void init(InputStream in, OutputStream out) {
		try {
			this.outputStream = new PrintStream(out);
			this.inputStream = new LineNumberReader(new InputStreamReader(in));
		} catch (Exception e) {
			LH.warning(logger, "Exception on init: ", e);
		}
	}

	public List<String> runMachineEventStats(long onwards, byte level) {
		return runCommand("eventStats " + level + " " + onwards);
	}

	public List<String> runIpAddr() {
		return runCommand("ipAddr");
	}

	public List<String> runIpLink() {
		return runCommand("ipLink");
	}

	public List<String> runLsof() {
		return runCommand("netStat");
	}
	public List<String> runDf() {
		return runCommand("df");
	}

	public List<String> runPs() {
		return runCommand("ps");
	}

	public List<String> runFree() {
		return runCommand("free");
	}

	public List<String> runDetails() {
		return runCommand("details");
	}

	public List<String> runLastReboot() {
		return runCommand("lastReboot");
	}

	public String getMachineUid(String uId) {
		return runCommand("machineUid " + uId).get(0);
	}

	public String getHostName() {
		return runCommand("hostName").get(0);
	}

	private List<String> runCommand(String cmd) {
		LH.fine(logger, ">>> sending command to .net: '", cmd, "'");
		final List<String> list = new ArrayList<String>();
		try {
			this.outputStream.println(cmd);
			this.outputStream.flush();
			String line = null;
			boolean eoCall = false;
			do {
				line = this.inputStream.readLine();
				LH.fine(logger, "response from .net: '", line, "'");
				if (line == null)
					break;
				eoCall = line.equals("EOCall");
				if (!eoCall)
					list.add(line);
			} while (!eoCall);
			LH.fine(logger, "<<< end of response from .net");
		} catch (Exception e) {
			LH.warning(logger, "Exception running command: '", cmd, "'", e);
		}
		return list;
	}

	public void dispose() {
		LH.info(logger, "disposing");
		this.outputStream.println("q"); //quit
		this.outputStream.flush();
		OH.sleep(100);
		IOH.close(this.outputStream);
	}

	public static void main(String[] args) throws IOException {
		String val = System.getProperty("line.separator");
		IOH.writeText(new File("c:\\test.txt"), "val-" + val + "Next Line?");
	}

}
