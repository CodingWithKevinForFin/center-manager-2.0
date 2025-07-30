package com.f1.ami.amicommon.messaging;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import com.f1.utils.FastBufferedInputStream;
import com.f1.utils.FastBufferedOutputStream;
import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.converter.bytes.BasicFromByteArrayConverterSession;
import com.f1.utils.converter.bytes.BasicToByteArrayConverterSession;
import com.f1.utils.converter.bytes.ClassToByteArrayConverter_Legacy;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;

public class SimpleMessagingServerConnection implements Runnable {
	public static final int SERVER_VERSION_0 = 0xf3; // Version 0 and Version 1 have the same server version code due to legacy reasons
	public static final int SERVER_VERSION_1 = 0xf3;
	public static final int SERVER_VERSION_2 = 0xf4;

	private static final Logger log = LH.get();

	final private DataInputStream in;
	final private FastBufferedOutputStream out;
	final private SimpleMessagingServerConnectionHandler handler;
	final private ObjectToByteArrayConverter converter;
	final private BasicToByteArrayConverterSession toSession;
	final private BasicFromByteArrayConverterSession fromSession;

	private static final ObjectToByteArrayConverter CONVERTER_LEGACY_0 = new ObjectToByteArrayConverter(true);
	static {
		CONVERTER_LEGACY_0.replaceConverter(new ClassToByteArrayConverter_Legacy());
	}

	final private Socket socket;
	final private String description;
	final private int serverVersionCode;
	final private int version;

	public SimpleMessagingServerConnection(ObjectToByteArrayConverter converter, Socket socket, SimpleMessagingServerConnectionHandler handler, String description, int version)
			throws IOException {
		this.socket = socket;
		this.description = description;
		this.in = new DataInputStream(new FastBufferedInputStream(socket.getInputStream()));
		this.out = new FastBufferedOutputStream(socket.getOutputStream());
		this.version = version;
		switch (this.version) {
			case 2:
				this.serverVersionCode = SERVER_VERSION_2;
				this.converter = converter;
				this.toSession = new BasicToByteArrayConverterSession(this.converter, new FastByteArrayDataOutputStream(), false);
				this.fromSession = new BasicFromByteArrayConverterSession(this.converter, new FastByteArrayDataInputStream(OH.EMPTY_BYTE_ARRAY));
				break;
			case 1:
				this.serverVersionCode = SERVER_VERSION_1;
				this.converter = converter;
				this.toSession = new BasicToByteArrayConverterSession(this.converter, new FastByteArrayDataOutputStream(), false);
				this.fromSession = new BasicFromByteArrayConverterSession(this.converter, new FastByteArrayDataInputStream(OH.EMPTY_BYTE_ARRAY));
				this.toSession.setLegacyMode();
				this.fromSession.setLegacyMode();
				break;
			case 0:
				this.serverVersionCode = SERVER_VERSION_0;
				this.converter = CONVERTER_LEGACY_0;
				this.toSession = new BasicToByteArrayConverterSession(this.converter, new FastByteArrayDataOutputStream(), false);
				this.fromSession = new BasicFromByteArrayConverterSession(this.converter, new FastByteArrayDataInputStream(OH.EMPTY_BYTE_ARRAY));
				this.toSession.setLegacyMode();
				this.fromSession.setLegacyMode();
				break;
			default:
				IOH.close(this.socket);
				throw new RuntimeException("Invalid protocol subversion: " + version);
		}
		this.out.writeByte(0x3f);
		this.out.writeByte(this.serverVersionCode);
		this.out.flush();
		this.handler = handler;
	}

	public void run() {
		try {
			int b1 = this.in.readByte() & 0xff;
			int b2 = this.in.readByte() & 0xff;
			if (b1 != 0x1f || b2 != 0xf1) {
				close();
				throw new RuntimeException("Invalid protocol for " + description);
			}
		} catch (IOException e1) {
			close();
			throw new RuntimeException("IO Error while validating protocol", e1);
		}
		while (true) {
			int msgSize;
			try {
				msgSize = this.in.readInt();
			} catch (Exception e) {
				LH.info(log, "Connection remotely closed from ", this.socket.getRemoteSocketAddress());
				break;
			}
			try {
				byte[] request = new byte[msgSize];
				this.in.readFully(request);
				byte response[] = this.processRequestBytes(request);
				this.out.writeInt(response.length);
				this.out.write(response);
				if (this.handler.keepOpen()) {
					this.out.writeByte(0);
					this.out.flush();
				} else {
					this.out.flush();
					break;
				}
			} catch (EOFException e) {
				LH.info(log, "Connection closed from ", this.socket.getRemoteSocketAddress(), e);
				break;
			} catch (Exception e) {
				LH.warning(log, "Error processing request from ", this.socket.getRemoteSocketAddress(), e);
				break;
			}
		}
		close();
	}
	private void close() {
		IOH.close(this.socket);
		try {
			this.handler.onClosed();
		} catch (Exception e) {
			LH.info(log, "Error handling onclosed from ", this.handler, e);
		}
	}

	public byte[] processRequestBytes(byte[] requestBytes) throws IOException {
		((FastByteArrayDataOutputStream) toSession.getStream()).reset();
		FastByteArrayDataInputStream stream = (FastByteArrayDataInputStream) fromSession.getStream();
		stream.reset(requestBytes);
		Object request = converter.read(fromSession);
		Object response = handler.processRequest(request);

		converter.write(response, toSession);
		byte[] responseBytes = ((FastByteArrayDataOutputStream) toSession.getStream()).toByteArray();
		return responseBytes;
	}

}
