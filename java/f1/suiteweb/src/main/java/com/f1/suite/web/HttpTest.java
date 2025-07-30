package com.f1.suite.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentMap;

import com.f1.utils.CopyOnWriteHashMap;
import com.f1.utils.IOH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.FastThreadPool;

public class HttpTest implements Runnable {

	static ConcurrentMap<String, byte[]> files = new CopyOnWriteHashMap<String, byte[]>();
	private Socket s;

	public HttpTest(Socket s) {

		this.s = s;
	}

	public static void main(String t[]) throws IOException {
		FastThreadPool tp = new FastThreadPool(110, "T");
		tp.start();
		ServerSocket ss = new ServerSocket(9090);
		while (true) {
			try {
				Socket s = ss.accept();
				tp.execute(new HttpTest(s));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		try {
			processRequest(s.getInputStream(), s.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		IOH.close(s);
	}

	private static void processRequest(InputStream inputStream, OutputStream outputStream) throws IOException {
		inputStream = new BufferedInputStream(inputStream, 10000);
		outputStream = new BufferedOutputStream(outputStream, 10000);
		LineNumberReader reader = new LineNumberReader(new InputStreamReader(inputStream));
		PrintStream out = new PrintStream(outputStream);
		String s = null;
		String url = null;
		try {
			while (true) {
				boolean first = true;
				while ((s = reader.readLine()) != null) {
					if (SH.isnt(s)) {
						if (first)
							return;
						break;
					}
					first = false;
					if (s.startsWith("GET ")) {
						url = s.substring(4, s.lastIndexOf(' '));
					}
				}
				url = "/home/rcooke/tmp/" + url;
				byte[] data = files.get(url);
				if (data == null) {
					try {
						data = IOH.readData(new File(url));
					} catch (FileNotFoundException e) {
						out.println("HTTP/1.1 404 Not Found");
						out.println();
						continue;
					}
				}
				files.put(url, data);
				out.println("HTTP/1.1 200 OK");
				out.println("Date: Fri, 18 May 2012 19:52:04 GMT");
				out.println("Server: f1");
				out.println("Last-Modified: Fri, 06 Aug 2010 21:48:01 GMT");
				out.println("ETag: \"9a20a-153-48d2e9eea2640\"");
				out.println("Accept-Ranges: bytes");
				out.println("Content-Length: " + data.length);
				out.println("cache-request-directive: no-cache");
				out.println("Keep-Alive: timeout=2, max=100");
				out.println("Connection: keep-alive");
				out.println("Content-Type: " + com.f1.utils.ContentType.getTypeByFileExtension(SH.afterLast(url, '.')).getMimeType());
				out.println("");
				out.write(data);
				out.flush();
			}
		} finally {
			IOH.close(out);
			IOH.close(reader);
		}
	}

}
