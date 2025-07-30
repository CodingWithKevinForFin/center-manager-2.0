package com.f1.ami.amicommon.messaging;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import javax.net.ssl.SSLSocket;

import com.f1.utils.FastBufferedInputStream;
import com.f1.utils.FastBufferedOutputStream;
import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.converter.bytes.BasicFromByteArrayConverterSession;
import com.f1.utils.converter.bytes.BasicToByteArrayConverterSession;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;

public class SimpleMessagingClientConnection implements Closeable {

	final private int port;
	final private String host;
	final private Socket socket;
	final private DataInputStream in;
	final private FastBufferedOutputStream out;
	final private ObjectToByteArrayConverter converter;
	final private BasicToByteArrayConverterSession toSession;
	final private BasicFromByteArrayConverterSession fromSession;
	final private int timeoutMs;
	final private int serverVersionCode;
	
	public SimpleMessagingClientConnection(ObjectToByteArrayConverter converter, String host, int port, boolean isSSL, int timeoutMs, String sslKeystore, String sslKeystorePass) throws IOException {
		this.socket = isSSL ? IOH.openSSLClientSocketWithReason(host, port, new File(sslKeystore), sslKeystorePass, null) : new Socket(host, port);
		this.host = host;
		this.port = port;
		this.timeoutMs = timeoutMs;
		this.converter = converter;
		this.toSession = new BasicToByteArrayConverterSession(this.converter, new FastByteArrayDataOutputStream(), false);
		this.fromSession = new BasicFromByteArrayConverterSession(this.converter, new FastByteArrayDataInputStream(OH.EMPTY_BYTE_ARRAY));
		this.in = new DataInputStream(new FastBufferedInputStream(socket.getInputStream()));
		this.out = new FastBufferedOutputStream(socket.getOutputStream());
		long start = System.currentTimeMillis();
		while (!isSSL && this.in.available() == 0) //SSL port not necessarily returns non-zero
			if (System.currentTimeMillis() - start > this.timeoutMs) {
				IOH.close(this.socket);
				throw new RuntimeException("Invalid protocol, timeout waiting for response header");
			}

		int b1 = this.in.readByte() & 0xff;
		this.serverVersionCode = this.in.readByte() & 0xff;
		if (b1 != 0x3f) {
			IOH.close(this.socket);
			throw new RuntimeException("Invalid protocol");
		}
		switch (this.serverVersionCode) {
			case SimpleMessagingServerConnection.SERVER_VERSION_1:
				this.toSession.setLegacyMode();
				this.fromSession.setLegacyMode();
				break;
			case SimpleMessagingServerConnection.SERVER_VERSION_2:
				break;
			default:
				IOH.close(this.socket);
				throw new RuntimeException("Invalid protocol subversion (ami.db.jdbc.protocol.version property): " + this.serverVersionCode);
		}
		this.out.writeByte(0x1f);
		this.out.writeByte(0xf1);
	}

	public boolean isOpen() {
		return !socket.isClosed();
	}

	private byte[] send(byte[] request) throws IOException {
		this.out.writeInt(request.length);
		this.out.write(request);
		this.out.flush();
		int size;
		try {
			size = this.in.readInt();
		} catch (EOFException e) {
			IOH.close(socket);
			return null;
		}
		byte[] response = new byte[size];
		this.in.readFully(response);
		try {
			this.in.readByte();
		} catch (EOFException e) {
			IOH.close(socket);
		}
		return response;
	}

	public Object sendObject(Object request) throws IOException {
		((FastByteArrayDataOutputStream) toSession.getStream()).reset();
		converter.write(request, toSession);
		byte[] requestBytes = ((FastByteArrayDataOutputStream) toSession.getStream()).toByteArray();
		byte[] resposneBytes = send(requestBytes);
		if (resposneBytes == null)
			return null;

		FastByteArrayDataInputStream stream = (FastByteArrayDataInputStream) fromSession.getStream();
		stream.reset(resposneBytes);
		return converter.read(fromSession);
	}

	public void close() {
		IOH.close(this.socket);
		IOH.close(this.in);
		IOH.close(this.out);
	}

	public String getHost() {
		return this.host;
	}
	public int getPort() {
		return this.port;
	}
}
