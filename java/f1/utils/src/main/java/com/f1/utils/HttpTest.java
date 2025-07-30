package com.f1.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpTest extends OutputStream {

	private PrintStream inner2;
	private OutputStream inner;

	public HttpTest(OutputStream outputStream, PrintStream outputStream2) {
		this.inner = outputStream;
		this.inner2 = outputStream2;
	}

	public static void main(String t[]) throws IOException {
		ServerSocket ss = new ServerSocket(9090);
		while (true) {
			try {
				Socket s = ss.accept();
				Socket s2 = new Socket("localhost", 8080);
				System.out.println("accepted");
				new Thread(new StreamPiper(s.getInputStream(), new HttpTest(s2.getOutputStream(), System.err), 1024)).start();
				new Thread(new StreamPiper(s2.getInputStream(), new HttpTest(s.getOutputStream(), System.out), 1024)).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void write(int arg0) throws IOException {
		inner.write(arg0);
		inner2.write(arg0);
		inner.flush();
		inner2.flush();
	}

	private static void processRequest(InputStream inputStream, OutputStream outputStream) throws IOException {
		LineNumberReader reader = new LineNumberReader(new InputStreamReader(inputStream));
		String s = null;
		String url = null;
		while ((s = reader.readLine()) != null) {
			if (SH.isnt(s))
				break;
			if (s.startsWith("GET ")) {
				url = s.substring(4, s.lastIndexOf(' '));
			}
		}
		byte[] data = IOH.readData(new File("/home/rcooke/tmp" + url));
		PrintStream out = new PrintStream(outputStream);
		out.println("HTTP/1.1 200 OK");
		out.println("Date: Fri, 18 May 2012 19:52:04 GMT");
		out.println("Server: Apache");
		out.println("Last-Modified: Fri, 06 Aug 2010 21:48:01 GMT");
		out.println("ETag: \"9a20a-153-48d2e9eea2640\"");
		out.println("Accept-Ranges: bytes");
		out.println("Content-Length: " + data.length);
		out.println("Keep-Alive: timeout=2, max=100");
		out.println("");
		out.write(data);
		IOH.close(out);
		IOH.close(reader);
	}
}
