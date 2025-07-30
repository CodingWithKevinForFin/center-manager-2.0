package com.f1.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class PortForwarderSecure implements Runnable {
	private static final String TLS = System.getProperty("f1.sslcontext.default", "TLSv1.2");

	public class Piper implements Runnable {

		private OutputStream out;
		private InputStream in;
		private int tot;
		private boolean direction;

		public Piper(OutputStream out, InputStream in, boolean b) {
			this.out = out;
			this.in = in;
			this.direction = b;
		}

		@Override
		public void run() {
			byte[] buf = new byte[8192];
			try {
				for (;;) {
					int len = in.read(buf, 0, 8192);
					//System.out.println("READ " + direction + ": " + len);
					if (len == -1)
						break;
					out.write(buf, 0, len);
					this.tot += len;
				}
			} catch (Exception e) {
			}
			close(in);
			close(out);
		}

	}

	//TO CREATE A SECURE TUNNEL:
	//keytool -genkeypair -deststoretype pkcs12 -keyalg RSA -alias 3forge_server -keystore server.jks -validity 1000 -keysize 2048 -keypass serverpass -storepass serverpass -dname "cn=Client"
	//keytool -export -alias 3forge_server -storepass serverpass -file server.cer -storetype pkcs12 -keystore server.jks
	//keytool -genkey -deststoretype pkcs12  -alias 3forge_client -keyalg RSA -keypass clientpass -storepass clientpass -keystore client.jks  -dname "cn=Client"
	//keytool -import -v -trustcacerts -alias 3forge_server -file server.cer -keystore client.jks -keypass clientpass -storepass clientpass -noprompt

	//Client on Local:           java PortForwardSecure 1234 remote.host 2345 @ client.jks@clientpass
	//Server on remote.host:     java PortForwardSecure 2345 target.host 1234 server.jks@serverpass @
	public static void main(String a[]) throws IOException, UnrecoverableKeyException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
		int sourcePort = Integer.parseInt(a[0]);
		String host = a[1];
		int target = Integer.parseInt(a[2]);
		String serverCert = a[3];
		String clientCert = a[4];
		System.out.println("Forwarding " + sourcePort + " to " + host + ":" + target);
		final ServerSocket ss;
		SSLContext serverContext = createSslSocketFactory(serverCert);
		SSLContext clientContext = createSslSocketFactory(clientCert);
		ss = serverContext == null ? new ServerSocket(sourcePort) : serverContext.getServerSocketFactory().createServerSocket(sourcePort);
		try {
			int session = 0;
			for (;;) {
				Socket s = ss.accept();
				new Thread(new PortForwarderSecure(session++, s, host, target, clientContext)).start();
			}
		} finally {
			ss.close();
		}
	}

	private Socket socket;
	private String host;
	private int target;
	private int session;
	private SSLContext cs;

	public PortForwarderSecure(int session, Socket socket, String host, int target, SSLContext cs) {
		this.socket = socket;
		this.host = host;
		this.target = target;
		this.session = session;
		this.cs = cs;
	}

	@Override
	public void run() {
		Socket socket2 = null;
		try {
			String name = socket.getRemoteSocketAddress().toString();
			println("Recieved socket from " + name);
			if (cs == null)
				socket2 = new Socket(this.host, this.target);
			else
				socket2 = cs.getSocketFactory().createSocket(this.host, this.target);
			println("Established socket to " + socket2.getRemoteSocketAddress());
			Piper p1 = new Piper(socket.getOutputStream(), socket2.getInputStream(), true);
			Piper p2 = new Piper(socket2.getOutputStream(), socket.getInputStream(), false);
			Thread thread1 = new Thread(p1);
			Thread thread2 = new Thread(p2);
			thread1.start();
			thread2.start();
			thread1.join();
			thread2.join();
			println("Sockets closed from " + name + " (up: " + p1.tot + "/dn: " + p2.tot + ")");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally {
			close(socket);
			close(socket2);
		}
	}

	private void println(String string) {
		System.out.println("SESSION-" + session + " " + string);
	}

	private static void close(Socket in2) {
		try {
			in2.close();
		} catch (Exception e) {
		}
	}
	private static void close(Closeable in2) {
		try {
			in2.close();
		} catch (Exception e) {
		}
	}
	static private SSLContext createSslSocketFactory(String fileAtPassword)
			throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, KeyManagementException {
		fileAtPassword = fileAtPassword.trim();
		if ("@".equals(fileAtPassword))
			return null;
		int n = fileAtPassword.indexOf('@');
		if (n == -1)
			throw new IllegalArgumentException("For Certificate expectiong File@Password: " + fileAtPassword);
		String fileName = fileAtPassword.substring(0, n);
		String password = fileAtPassword.substring(n + 1);
		FileInputStream inputStream = new FileInputStream(new File(fileName));
		try {
			final char[] keystorePasswordChars = password.toCharArray();
			final SSLContext sslContext = SSLContext.getInstance(TLS);
			final KeyManagerFactory km = KeyManagerFactory.getInstance("SunX509");
			final KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(inputStream, keystorePasswordChars);
			km.init(ks, keystorePasswordChars);
			final TrustManagerFactory tm = TrustManagerFactory.getInstance("SunX509");
			tm.init(ks);
			sslContext.init(km.getKeyManagers(), tm.getTrustManagers(), null);
			return sslContext;
		} finally {
			close(inputStream);
		}
	}
}