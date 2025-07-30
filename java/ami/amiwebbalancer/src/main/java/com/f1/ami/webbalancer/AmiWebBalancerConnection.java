package com.f1.ami.webbalancer;

import java.io.IOException;
import java.net.Socket;

import javax.net.ssl.SSLSocket;

import com.f1.ami.webbalancer.AmiWebBalancerFastHttpRequestResponse.Header;
import com.f1.utils.IOH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.CountingOutputStream;

public class AmiWebBalancerConnection implements Runnable {
	public static final String SERVER_NAME = "3forge Ultrafast Load Balancer";
	final private AmiWebBalancerClientSession session;
	final private Socket clientSocket;
	private Socket serverSocket;
	private CountingOutputStream clientCounter;
	private CountingOutputStream serverCounter;
	private long requestNanos;
	private long requestCount;
	private long starttime;
	private AmiWebBalancerServerInstance hostPort;

	public AmiWebBalancerConnection(AmiWebBalancerClientSession session, int connectionId, Socket socket) {
		this.clientSocket = socket;
		this.session = session;
		this.starttime = System.currentTimeMillis();
	}

	public AmiWebBalancerClientSession getSession() {
		return this.session;
	}

	@Override
	public void run() {
		final AmiWebBalancerFastHttpRequestResponse clientSide, serverSide;
		try {
			this.clientSocket.setKeepAlive(true);
			IOH.optimize(this.clientSocket);
			clientSide = new AmiWebBalancerFastHttpRequestResponse(this.clientCounter = new CountingOutputStream(this.clientSocket.getOutputStream()),
					this.clientSocket.getInputStream());
		} catch (IOException e1) {
			return;
		}
		this.hostPort = session.getOrResolveServerHostPort();
		if (hostPort == null) {
			sendErrorMessage(clientSide);
			IOH.close(this.clientSocket);
			return;
		}
		try {
			serverSocket = hostPort.newSocket();
			this.serverSocket.setKeepAlive(true);
			IOH.optimize(this.serverSocket);
			serverSide = new AmiWebBalancerFastHttpRequestResponse(this.serverCounter = new CountingOutputStream(this.serverSocket.getOutputStream()),
					serverSocket.getInputStream());
		} catch (Exception e) {
			return;
		}
		session.onOpened(this);
		String clientIp = IOH.getRemoteHostName(this.clientSocket);
		for (;;) {
			try {
				clientSide.reset();
				if (!clientSide.readRequest())
					break;
				serverSide.setRequestMethod(clientSide.getRequestMethod());
				serverSide.setRequestPath(clientSide.getRequestPath());
				serverSide.setRequestData(clientSide.getRequestData());
				{
					Header headerIn = clientSide.getRequestHeaders();
					Header headerOut = serverSide.getRequestHeaders();
					for (int i = 0; i < headerIn.getCount(); i++) {
						String key = headerIn.getKey(i);
						if ("Host".equals(key))
							key = "X-Forward-Host";
						else if (SH.startsWith(key, "X-Forward-"))
							key = "X-Forward-" + key;
						headerOut.add(key, headerIn.getVal(i));
					}
					headerOut.add("X-Forwarded-Proto", serverSide.getRequestProtocol());
					headerOut.add("X-Forwarded-For", clientIp);
					headerOut.add("Host", hostPort.getHostPort());
				}
				try {
					long start = System.nanoTime();
					serverSide.writeRequest();
					serverSide.reset();
					if (!serverSide.readResponse()) {
						sendErrorMessage(clientSide);
						break;
					}
					long end = System.nanoTime();
					this.requestNanos += end - start;
					this.requestCount++;
				} catch (Exception e) {
					sendErrorMessage(clientSide);
					break;
				}
				clientSide.setResponseData(serverSide.getResponseData());
				clientSide.setResponseType(serverSide.getResponseType());
				{
					Header headerIn = serverSide.getResponseHeaders();
					Header headerOut = clientSide.getResponseHeaders();
					String origServer = null;
					for (int i = 0; i < headerIn.getCount(); i++) {
						String key = headerIn.getKey(i);
						String val = headerIn.getVal(i);
						if ("Server".equals(key) || key.endsWith("Orig-Server"))
							origServer = val;
						else
							headerOut.add(key, headerIn.getVal(i));
					}
					if (origServer == null)
						headerOut.add("Server", SERVER_NAME);
					else
						headerOut.add("Server", SERVER_NAME + " --> " + origServer);
				}
				clientSide.writeResponse();
			} catch (IOException e) {
				break;
			}
		}
		IOH.close(this.clientSocket);
		IOH.close(this.serverSocket);
		session.onClosed(this);
	}

	private void sendErrorMessage(final AmiWebBalancerFastHttpRequestResponse clientSide) {
		try {
			clientSide.setResponseType("HTTP/1.1 503 Service Unavailable");
			String body = "<html><body><span style='color:blue'>" + SERVER_NAME
					+ "</span><P>Error code <B><span style='color:red'>100 - No Web Servers Available\n<script>setTimeout(function() {location.reload()}, 5000);</script>";
			clientSide.getResponseHeaders().add("Content-Length", SH.toString(body.length()));
			clientSide.getResponseHeaders().add("Content-Type", "text/html;charset=utf-8htm");
			clientSide.setResponseData(body.getBytes());
			clientSide.writeResponse();
		} catch (Exception e) {
		} finally {
		}
		return;
	}

	public long getBytesToClient() {
		return this.clientCounter == null ? 0 : this.clientCounter.getCount();
	}

	public long getBytesToServer() {
		return this.serverCounter == null ? 0 : this.serverCounter.getCount();
	}

	public void close() {
		IOH.close(this.clientSocket);
		IOH.close(this.serverSocket);
	}

	public long getRequestCounts() {
		return this.requestCount;
	}

	public long getRequestNanos() {
		return this.requestNanos;
	}

	public String getClientAddres() {
		return this.session.getClientAddress();
	}

	public String getServerURL() {
		return this.hostPort == null ? null : this.hostPort.getHostPort();
	}

	public long getStartTime() {
		return starttime;
	}

	public boolean getClientSecure() {
		return this.clientSocket instanceof SSLSocket;
	}

}
