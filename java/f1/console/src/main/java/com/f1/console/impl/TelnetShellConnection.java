package com.f1.console.impl;

import static com.f1.console.impl.TelnetConstants.TELNET_DO;
import static com.f1.console.impl.TelnetConstants.TELNET_DONT;
import static com.f1.console.impl.TelnetConstants.TELNET_IAC;
import static com.f1.console.impl.TelnetConstants.TELNET_OPTIONAL;
import static com.f1.console.impl.TelnetConstants.TELNET_OPTION_ECHO;
import static com.f1.console.impl.TelnetConstants.TELNET_OPTION_LMODE;
import static com.f1.console.impl.TelnetConstants.TELNET_OPTION_SGA;
import static com.f1.console.impl.TelnetConstants.TELNET_OPTION_TTYPE;
import static com.f1.console.impl.TelnetConstants.TELNET_SB;
import static com.f1.console.impl.TelnetConstants.TELNET_SE;
import static com.f1.console.impl.TelnetConstants.TELNET_WILL;
import static com.f1.console.impl.TelnetConstants.TELNET_WONT;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PushbackInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.f1.console.impl.shell.ShellAutoCompleter;
import com.f1.console.impl.shell.UserShell;
import com.f1.utils.AH;
import com.f1.utils.CheckedRuntimeException;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class TelnetShellConnection {

	private static final Logger log = LH.get();
	private PushbackInputStream in;
	private OutputStream out;
	private boolean lineMode;

	private final UserShell shell;

	public TelnetShellConnection(InputStream in, OutputStream out, List<String> history, ShellAutoCompleter telnetAutoCompleter, String prompt) {
		this.in = new PushbackInputStream(in, 1000);
		this.shell = new UserShell(this.in, out, history, telnetAutoCompleter, prompt);
		this.out = new BufferedOutputStream(out, 1024);

		// send negotiation
		try {
			iac(out, TELNET_WILL, TELNET_OPTION_ECHO);
			byte[] result;
			try {
				result = readIac(this.in, true);
			} catch (Exception e) {
				result = new byte[] { 0, 0 };
			}
			if (result == null || (result[0] != TelnetConstants.TELNET_WILL && result[0] != TelnetConstants.TELNET_DO)) {
				// if the client can't even support echo, then lets assume its a dumb client
				lineMode = false;
			} else {
				iac(out, TELNET_WILL, TELNET_OPTION_SGA);
				iac(out, TELNET_WONT, TELNET_OPTION_TTYPE);
				iac(out, TELNET_WONT, TELNET_OPTION_LMODE);
				lineMode = true;
			}
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}
	public String readLine() {
		if (lineMode)
			return shell.readLine();
		else
			return shell.readLineNoIteract();
	}
	public String readLineNoThrow(boolean printStackTraceToStdout) {
		try {
			if (lineMode)
				return shell.readLine();
			else
				return shell.readLineNoIteract();
		} catch (Exception e) {
			if (e instanceof CheckedRuntimeException) {
				CheckedRuntimeException cre = (CheckedRuntimeException) e;
				if (cre.getCheckedException() instanceof IOException) {
					LH.info(log, "Connection unexpectedly closed for ", this.shell.getPrompt(), ": " + e.getMessage());
					return null;
				}
			}
			LH.info(log, e);
			if (printStackTraceToStdout)
				try {
					PrintStream ps = new PrintStream(out);
					SH.printStackTrace("", "  ", e, ps);
				} catch (Exception e2) {
				}
			return null;
		}
	}
	private static void iac(OutputStream out, byte doDontWillWont, byte option) throws IOException {
		out.write(TELNET_IAC);
		out.write(doDontWillWont);
		out.write(option);
		out.flush();

	}

	/**
	 * 
	 * @param in
	 *            the stream to read
	 * @return first byte is the option, rest are the payload
	 * @throws IOException
	 */
	private static byte[] readIac(PushbackInputStream in, boolean needsIAC) throws IOException {
		int cmd = in.read();
		if (needsIAC) {
			if (cmd != 255) {
				in.unread(cmd);
				return null;
			}
			cmd = in.read();
		}
		if (cmd == -1)
			return null;
		switch ((byte) cmd) {
			case TELNET_WILL:
			case TELNET_WONT:
			case TELNET_DO:
			case TELNET_DONT:
			case TELNET_OPTIONAL:
				int option = in.read();
				if (option == -1)
					return null;
				return new byte[] { (byte) cmd, (byte) option };
			case TELNET_SB:
				ArrayList<Byte> bytes = new ArrayList<Byte>();
				bytes.add((byte) cmd);
				int c;
				while ((byte) (c = in.read()) != TELNET_IAC)
					if (c == -1)
						return null;
					else
						bytes.add((byte) c);
				byte se = (byte) in.read();
				if (se != TELNET_SE)
					throw new RuntimeException("expecting SE after IAC, not " + se);
				return AH.toArrayByte(bytes);
			default:
				throw new RuntimeException("invalid command: " + cmd);
		}
	}
	public UserShell getUserShell() {
		return this.shell;
	}

	public static void main(String a[]) throws Exception {
		Socket s = new Socket("hammer", 4433);
		InputStream in2 = s.getInputStream();
		for (;;) {
			System.out.println(in2.read());
		}
	}

}
