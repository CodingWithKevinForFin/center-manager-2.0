package com.f1.ami.relay.fh;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.ami.relay.AmiRelayIn;
import com.f1.ami.relay.AmiRelayProperties;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;

public class AmiClientSocketFH extends AmiFHBase {
	private static final Logger log = LH.get();
	private volatile boolean running;
	private Thread acceptorThread;
	private int serverPort;

	public static String PROP_SERVER_PORT = "port";
	public static String PROP_SERVER_HOST = "host";
	public static String PROP_RETRY_PERIOD = "retryPeriod";

	private boolean startedServerPort = false;
	private boolean amiPortWaitForServer = false;
	private File serverPortKeystoreFile;
	private String serverPortKeystorePassword;
	private String serverHost;
	private AmiSocketFH feedhandler;
	private long retryPeriodMillis;

	@Override
	public void init(int id, String name, PropertyController sysProps, PropertyController props, AmiRelayIn amiServer) {
		super.init(id, name, sysProps, props, amiServer);

		this.serverPort = props.getRequired(PROP_SERVER_PORT, Caster_Integer.PRIMITIVE);
		this.serverHost = props.getRequired(PROP_SERVER_HOST, Caster_String.INSTANCE);
		this.serverHost = props.getRequired(PROP_SERVER_HOST, Caster_String.INSTANCE);
		this.retryPeriodMillis = SH.parseDurationTo(props.getOptional(PROP_RETRY_PERIOD, "5 SECONDS"), TimeUnit.MILLISECONDS);
		OH.assertGt(this.retryPeriodMillis, 0, PROP_RETRY_PERIOD);
		this.serverPortKeystoreFile = props.getOptional(AmiRelayProperties.OPTION_AMI_PORT_KEYSTORE_FILE, File.class);
		if (this.serverPortKeystoreFile == null)
			this.serverPortKeystoreFile = sysProps.getOptional(AmiRelayProperties.OPTION_AMI_PORT_KEYSTORE_FILE, File.class);
		this.serverPortKeystorePassword = props.getOptional(AmiRelayProperties.OPTION_AMI_PORT_KEYSTORE_PASSWORD, String.class);
		if (this.serverPortKeystorePassword == null)
			this.serverPortKeystorePassword = sysProps.getOptional(AmiRelayProperties.OPTION_AMI_PORT_KEYSTORE_PASSWORD, String.class);
		this.amiPortWaitForServer = sysProps.getOptional(AmiRelayProperties.OPTION_AMI_PORT_WAIT_FOR_CENTER, Boolean.FALSE);
		this.setConnectionTime(this.getManager().getTools().getNow());
	}

	@Override
	public void start() {
		super.start();
		if (this.serverPort != -1 && !startedServerPort && !this.amiPortWaitForServer)
			startClientPort();
		registerCommands();
		onStartFinish(true);
	}

	private void startClientPort() {
		this.startedServerPort = true;
		this.running = true;
		this.acceptorThread = this.getManager().getThreadFactory().newThread(new Acceptor());
		this.acceptorThread.start();
		log.info("To access AMI server connection: telnet " + EH.getLocalHost() + " " + this.serverPort);
	}
	@Override
	public void onCenterConnected(String centerId) {
		if (this.serverPort != -1 && !startedServerPort)
			startClientPort();
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
			onStopFinish(true);
		} catch (Exception e) {
			LH.log(log, Level.WARNING, "Error closing ami server port ", serverPort, e);
			onStopFinish(false);
		}

	}

	private class Acceptor implements Runnable {

		@Override
		public void run() {
			boolean first = true;
			while (running) {
				final Socket clientSocket;
				try {
					if (serverPortKeystoreFile != null)
						clientSocket = IOH.openSSLClientSocketWithReason(serverHost, serverPort, serverPortKeystoreFile, serverPortKeystorePassword, "AMI SSL Client Socket");
					else
						clientSocket = IOH.openClientSocketWithReason(serverHost, serverPort, "AMI Client Socket");
					first = true;
				} catch (IOException e) {
					if (first) {
						LH.info(log, "Could not connect to: " + serverHost + ":" + serverPort, ", will retry in ", retryPeriodMillis, " millis");
						first = false;
					}
					OH.sleep(retryPeriodMillis);
					continue;
				}
				AmiSocketFH fh;
				try {
					LH.info(log, "For AmiClientSocketFH, Connected to: ", serverHost, ":", serverPort);
					fh = new AmiSocketFH(clientSocket, getManager().getTools().getNow());
					fh.setThread(Thread.currentThread());
					getManager().initAndStartFH(fh, "DirectSocketFH");
				} catch (Throwable e) {
					LH.warning(log, "Error instantiating ami client: ", e);
					continue;
				}
				feedhandler = fh;
				try {
					feedhandler.run();
					LH.info(log, "For AmiClientSocketFH, Disconnect from: ", serverHost, ":", serverPort);
					feedhandler = null;
				} catch (Throwable e) {
					LH.warning(log, "Error instantiating ami client: ", e);
					continue;
				}
			}
		}
	}
}
