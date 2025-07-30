package com.f1.ami.webbalancer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import com.f1.utils.FastBufferedInputStream;
import com.f1.utils.FastBufferedOutputStream;
import com.f1.utils.FastPrintStream;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiWebBalancerFastHttpRequestResponse {
	private static final byte[] NEW_LINE = "\r\n".getBytes();

	final private FastPrintStream out;
	final private InputStream in;
	private byte[] tmpData = new byte[32];
	final private StringBuilder buf = new StringBuilder();

	//REQUEST
	private String requestProtocol = "HTTP/1.1";
	private String requestPath;
	private String requestMethod;
	private Header requestHeaders = new Header();
	private byte[] requestData;

	//RESPONSE
	private Header responseHeaders = new Header();
	private String responseType;
	private byte[] responseData;

	public AmiWebBalancerFastHttpRequestResponse(OutputStream out, InputStream in) {
		this.out = new FastPrintStream(new FastBufferedOutputStream(out));
		this.in = new FastBufferedInputStream(in);
	}

	public String getRequestMethod() {
		return this.requestMethod;
	}
	public void setRequestMethod(String method) {
		this.requestMethod = method;
	}

	public String getRequestPath() {
		return this.requestPath;
	}
	public void setRequestPath(String path) {
		this.requestPath = path;
	}

	public byte[] getRequestData() {
		return this.requestData;
	}
	public void setRequestData(byte[] data) {
		this.requestData = data;
	}

	public String getRequestProtocol() {
		return this.requestProtocol;
	}
	public void setRequestProtocol(String t) {
		this.requestProtocol = t;
	}

	public Header getRequestHeaders() {
		return this.requestHeaders;
	}

	public String getResponseType() {
		return this.responseType;
	}
	public void setResponseType(String s) {
		this.responseType = s;
	}

	public byte[] getResponseData() {
		return responseData;
	}
	public void setResponseData(byte[] data) {
		responseData = data;
	}

	public Header getResponseHeaders() {
		return this.responseHeaders;
	}

	public void reset() {
		this.requestHeaders.reset();
		this.requestData = null;
		this.requestPath = null;
		this.requestMethod = null;
		this.responseHeaders.reset();
		this.requestMethod = null;
		this.responseData = null;
		this.responseType = null;
	}

	public boolean readRequest() throws IOException {
		if (!readUntil((byte) ' ', SH.clear(buf)))
			return false;
		this.requestMethod = SH.toStringAndClear(buf);
		readUntil((byte) ' ', SH.clear(buf));
		this.requestPath = SH.toStringAndClear(buf);
		readUntil((byte) '\n', SH.clear(buf));
		this.requestProtocol = SH.toStringAndClear(buf);
		String contentLength = null;
		while (readUntil((byte) ':', SH.clear(buf))) {
			String key = SH.trim(buf);
			readUntil((byte) '\n', SH.clear(buf));
			String value = SH.trim(buf);
			this.requestHeaders.add(key, value);
			if ("Content-Length".equals(key))
				contentLength = value;
		}
		if (contentLength != null) {
			int postContentLength;
			try {
				postContentLength = Integer.parseInt(contentLength);
			} catch (Exception e) {
				throw new RuntimeException("invalid content-Length for POST: " + contentLength, e);
			}
			this.requestData = IOH.readData(this.in, postContentLength);
		}
		return true;
	}
	public void writeRequest() throws IOException {
		out.append(requestMethod);
		out.write((byte) ' ');
		write(requestPath);
		out.write((byte) ' ');
		out.append(requestProtocol);
		out.write(NEW_LINE);
		String requestContentLength = null;
		for (int i = 0; i < requestHeaders.getCount(); i++) {
			String key = requestHeaders.getKey(i);
			write(key);
			out.write((byte) ':');
			String val = requestHeaders.getVal(i);
			write(val);
			out.write(NEW_LINE);
			if ("Content-Length".equals(key))
				requestContentLength = val;
		}
		out.print("\r\n");
		if (requestData != null || requestContentLength != null) {
			int n = SH.parseInt(requestContentLength);
			OH.assertEq(requestData.length, n);
			out.write(requestData);
		}
		out.flush();
	}
	public boolean readResponse() throws IOException {
		if (!readUntil((byte) '\n', buf))
			return false;
		this.responseType = SH.toStringAndClear(buf);
		String contentLength = null;
		while (readUntil((byte) ':', SH.clear(buf))) {
			String key = SH.trim(buf);
			readUntil((byte) '\n', SH.clear(buf));
			String value = SH.trim(buf);
			responseHeaders.add(key, value);
			if ("Content-Length".equals(key))
				contentLength = value;
		}
		if (contentLength != null) {
			int postContentLength;
			try {
				postContentLength = Integer.parseInt(contentLength);
			} catch (Exception e) {
				throw new RuntimeException("invalid content-Length for POST: " + contentLength, e);
			}
			this.responseData = IOH.readData(this.in, postContentLength);
		}
		return true;
	}

	public void writeResponse() throws IOException {
		out.append(responseType);
		out.write(NEW_LINE);
		for (int i = 0; i < responseHeaders.getCount(); i++) {
			write(responseHeaders.getKey(i));
			out.write((byte) ':');
			write(responseHeaders.getVal(i));
			out.write(NEW_LINE);
		}
		out.write(NEW_LINE);
		if (responseData != null)
			out.write(responseData);
		out.flush();
	}

	private boolean readUntil(byte c, StringBuilder sb) throws IOException {
		while (true) {
			int c2 = in.read();
			if (c2 == -1)
				return false;
			if (c2 == c)
				return true;
			else if (c2 == SH.BYTE_CR)
				continue;
			else if (c2 == SH.BYTE_NEWLINE)
				return false;
			sb.append((char) c2);
		}
	}

	private void write(CharSequence format) throws IOException {
		int len = format.length();
		if (tmpData.length < len)
			tmpData = new byte[len];
		SH.writeUTF(format, out, tmpData);
	}

	public static class Header {
		private int count;
		private String[] keys = new String[10];
		private String[] vals = new String[10];

		public int getCount() {
			return this.count;
		}
		public void reset() {
			this.count = 0;
		}
		public String getKey(int i) {
			return this.keys[i];
		}
		public String getVal(int i) {
			return this.vals[i];
		}
		public void add(String key, String value) {
			if (count == keys.length) {
				keys = Arrays.copyOf(keys, keys.length * 2);
				vals = Arrays.copyOf(vals, vals.length * 2);
			}
			keys[count] = key;
			vals[count] = value;
			count++;
		}
		public void addAll(Header h) {
			for (int i = 0; i < h.getCount(); i++)
				add(h.getKey(i), h.getVal(i));
		}
		public String find(String string) {
			for (int i = 0; i < count; i++) {
				if (OH.eq(string, keys[i]))
					return vals[i];
			}
			return null;
		}
	}

}
