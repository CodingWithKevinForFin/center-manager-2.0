package com.f1.ami.relay.fh;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.relay.AmiRelayIn;
import com.f1.ami.relay.AmiRelayProperties;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.ServerSocketAcceptor;
import com.f1.utils.ServerSocketEntitlements;
import com.f1.utils.casters.Caster_Integer;

public class AmiServerSocketFH extends AmiFHBase {
	private static final String PROPERTY_AMI_PORT = AmiRelayProperties.OPTION_AMI_PORT;
	private static final Logger log = LH.get();
	private ServerSocket serverSocket;
	private volatile boolean running;
	private Thread acceptorThread;
	private int serverPort;
	private String serverPortBindaddr;
	private ServerSocketEntitlements entitlements;

	public static String PROP_SERVER_PORT = "serverPort";

	private boolean startedServerPort = false;
	private boolean amiPortWaitForServer = false;
	private File serverPortKeystoreFile;
	private String serverPortKeystorePassword;

	@Override
	public void init(int id, String name, PropertyController sysProps, PropertyController props, AmiRelayIn amiServer) {
		super.init(id, name, sysProps, props, amiServer);

		Integer serverPort = props.getOptional(PROP_SERVER_PORT, Caster_Integer.PRIMITIVE);
		if (serverPort == null)
			serverPort = sysProps.getRequired(PROPERTY_AMI_PORT, int.class);
		this.serverPortKeystoreFile = props.getOptional(AmiRelayProperties.OPTION_AMI_PORT_KEYSTORE_FILE, File.class);
		if (this.serverPortKeystoreFile == null)
			this.serverPortKeystoreFile = sysProps.getOptional(AmiRelayProperties.OPTION_AMI_PORT_KEYSTORE_FILE, File.class);
		this.serverPortKeystorePassword = props.getOptional(AmiRelayProperties.OPTION_AMI_PORT_KEYSTORE_PASSWORD, String.class);
		if (this.serverPortKeystorePassword == null)
			this.serverPortKeystorePassword = sysProps.getOptional(AmiRelayProperties.OPTION_AMI_PORT_KEYSTORE_PASSWORD, String.class);
		this.serverPort = serverPort.intValue();
		this.serverPortBindaddr = props.getOptional(AmiRelayProperties.OPTION_AMI_PORT_BINDADDR);
		if (this.serverPortBindaddr == null)
			this.serverPortBindaddr = sysProps.getOptional(AmiRelayProperties.OPTION_AMI_PORT_BINDADDR);
		this.amiPortWaitForServer = sysProps.getOptional(AmiRelayProperties.OPTION_AMI_PORT_WAIT_FOR_CENTER, Boolean.FALSE);
		this.entitlements = AmiUtils.parseWhiteList(amiServer.getTools(), props, AmiRelayProperties.OPTION_AMI_PORT_WHITELIST);
		this.setConnectionTime(this.getManager().getTools().getNow());
	}

	@Override
	public void start() {
		super.start();
		if (this.serverPort != -1 && !startedServerPort && !this.amiPortWaitForServer)
			startServerPort();
		registerCommands();
		onStartFinish(true);
	}

	private void startServerPort() {
		this.startedServerPort = true;
		try {
			if (this.serverPortKeystoreFile != null)
				this.serverSocket = IOH.openSSLServerSocketWithReason(this.serverPortBindaddr, this.serverPort, this.serverPortKeystoreFile, this.serverPortKeystorePassword,
						"AMI SSL Server Socket");
			else
				this.serverSocket = IOH.openServerSocketWithReason(this.serverPortBindaddr, this.serverPort, "AMI Server Socket");
		} catch (IOException e) {
			throw new RuntimeException("Failed to start up AmiServerSocketFH, >>>> You may need to change property: " + PROPERTY_AMI_PORT + "=" + this.serverPort, e);
		}
		this.running = true;
		this.acceptorThread = this.getManager().getThreadFactory().newThread(new Acceptor());
		this.acceptorThread.start();
		log.info("To access AMI server connection: telnet " + EH.getLocalHost() + " " + this.serverPort);
	}
	@Override
	public void onCenterConnected(String centerId) {
		if (this.serverPort != -1 && !startedServerPort)
			startServerPort();
		super.onCenterConnected(centerId);
	}

	private void registerCommands() {

	}

	@Override
	public void stop() {
		super.stop();

		this.running = false;
		if (this.acceptorThread != null)
			this.acceptorThread.interrupt();
		try {
			if (this.serverSocket != null)
				this.serverSocket.close();
			onStopFinish(true);
		} catch (Exception e) {
			LH.log(log, Level.WARNING, "Error closing ami server port ", serverPort, e);
			onStopFinish(false);
		}

	}

	private class Acceptor implements Runnable {

		@Override
		public void run() {
			while (running) {
				try {
					Socket socket = serverSocket.accept();
					if (!ServerSocketAcceptor.handleEntitlements(entitlements, serverSocket, socket))
						continue;
					onNewClient(socket);
				} catch (IOException e) {
					if (running)
						LH.log(log, Level.WARNING, "Error accepting connection on server port ", serverPort, e);
				}
			}
		}
	}

	private void onNewClient(Socket clientSocket) throws IOException {
		AmiSocketFH fh = new AmiSocketFH(clientSocket, this.getManager().getTools().getNow());
		this.getManager().initAndStartFH(fh, "DirectSocketFH");
		//		this.sessions.put(fh.getId(), fh);
	}
}
