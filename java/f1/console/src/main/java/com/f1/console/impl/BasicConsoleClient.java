package com.f1.console.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.f1.console.ConsoleClient;
import com.f1.console.ConsoleClientResult;
import com.f1.console.ConsoleConnection;
import com.f1.console.ConsoleServer;
import com.f1.utils.IOH;
import com.f1.utils.SH;

public class BasicConsoleClient implements ConsoleClient, Closeable {
	final private ConsoleClientResult header;

	// if socket
	final private Socket socket;
	final private PrintWriter out;
	final private BufferedReader in;

	// if loopback
	final private ByteArrayOutputStream resultBuffer;
	final private ConsoleConnection connection;

	InputStream i;

	public BasicConsoleClient(String host, int port) {
		try {
			this.socket = new Socket(host, port);
			OutputStream o;
			this.out = new PrintWriter(o = new TelnetOutputStream(this.socket.getOutputStream()));
			this.in = new BufferedReader(new InputStreamReader(i = new TelnetInputStream(this.socket.getInputStream())));
			this.resultBuffer = null;
			this.connection = null;
			o.write(TelnetConstants.TELNET_IAC);
			o.write(TelnetConstants.TELNET_DONT);
			o.write(TelnetConstants.TELNET_OPTION_ECHO);
			int c1 = i.read();
			int c2 = i.read();
			int c3 = i.read();
			o.flush();

		} catch (Exception e) {
			throw new RuntimeException("could not connect to console at: " + host + ":" + port, e);
		}
		this.header = consumeResponse();
	}

	public BasicConsoleClient(ConsoleServer loopback) {
		this.socket = null;
		this.out = null;
		this.in = null;
		resultBuffer = new ByteArrayOutputStream();
		this.connection = loopback.createConnection(null, resultBuffer);
		this.header = consumeResponse();
	}

	public ConsoleClientResult getHeader() {
		return header;
	}

	@Override
	public ConsoleClientResult execute(String command) {
		if (out == null)
			connection.processUserCommand(command);
		else {
			out.println(command);
			out.flush();
		}
		return consumeResponse();
	}

	private ConsoleClientResult consumeResponse() {
		List<String> comments = new ArrayList<String>();
		StringBuilder result = new StringBuilder();
		boolean hasError = false;
		BufferedReader input;
		if (in != null) {
			input = in;
		} else {
			input = new LineNumberReader(new InputStreamReader(new TelnetInputStream(new ByteArrayInputStream(resultBuffer.toByteArray()))));
			resultBuffer.reset();
		}
		try {
			while (true) {
				char c = (char) input.read();
				if (c == '>')
					break;
				String line = input.readLine();
				if (line == null) {
					return new BasicConsoleClientResult("", Collections.EMPTY_LIST, false);
				}
				if (line.isEmpty())
					continue;
				line = c + line;
				if (line.startsWith(" ")) {
					result.append(line.substring(1)).append(SH.NEWLINE);
				} else {
					final String type = extractLineType(line);
					comments.add(line);
					if (ConsoleConnection.COMMENT_EXECUTED.equals(type))
						continue;
					else if (ConsoleConnection.COMMENT_EXIT.equals(type)) {
						close();
						break;
					} else if (ConsoleConnection.COMMENT_ERROR.equals(type)) {
						hasError = true;
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("error reading line", e);
		}
		return new BasicConsoleClientResult(result.toString(), comments, hasError);
	}

	@Override
	public void close() {
		IOH.close(in);
		IOH.close(out);
		IOH.close(socket);
	}

	private static String extractLineType(String line) {
		line = line.trim();
		int i = line.indexOf(':');
		if (!SH.startsWith(line, '#') || i == -1)
			throw new RuntimeException("invalid line: " + line);
		return line.substring(1, i);
	}

}
