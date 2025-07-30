package com.f1.ami.webbalancer;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiStartup;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.webbalancer.serverselector.AmiWebBalancerServerSelectorPlugin;
import com.f1.ami.webbalancer.serverselector.AmiWebBalancerServerSelectorPlugin_Stats;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.ContainerTools;
import com.f1.container.impl.ContainerToolsWrapper;
import com.f1.utils.CachedFile;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.ServerSocketEntitlements;
import com.f1.utils.concurrent.NamedThreadFactory;
import com.f1.utils.concurrent.SimpleExecutor;

public class AmiWebBalancerMain {

	private static final String DEFAULT_TEST_URL = "/3forge_rest/stats?display=json";
	private static final Logger log = LH.get();
	private static final String DEFAULT_TEST_PERIOD = "5 seconds";

	public static void main(String a[]) {
		ContainerBootstrap bs = new ContainerBootstrap(AmiWebBalancerMain.class, a);
		bs.setProperty("f1.appname", "AmiWebBalancer");
		bs.setProperty("f1.logfilename", "AmiWebBalancer");
		bs.setProperty("f1.autocoded.disabled", "false");
		bs.setProperty("f1.threadpool.agressive", "false");
		AmiStartup.startupAmi(bs, "ami_amiwebbalancer");
		main2(bs);
		AmiStartup.startupComplete(bs);
	}

	public static void main2(ContainerBootstrap bs) {

		PropertyController pc = bs.getProperties();
		ContainerTools ct = new ContainerToolsWrapper(pc);
		try {
			Integer httpServerPort = pc.getOptional(AmiWebBalancerProperties.PROPERTY_AMI_WEBBALANCER_HTTP_PORT, Integer.class);
			Integer httpsServerPort = pc.getOptional(AmiWebBalancerProperties.PROPERTY_AMI_WEBBALANCER_HTTPS_PORT, Integer.class);
			if (httpServerPort == null && httpsServerPort == null)
				throw new RuntimeException("Must supply one of the properties: " + AmiWebBalancerProperties.PROPERTY_AMI_WEBBALANCER_HTTP_PORT + " and/or "
						+ AmiWebBalancerProperties.PROPERTY_AMI_WEBBALANCER_HTTPS_PORT);

			final String testUrl = pc.getOptional(AmiWebBalancerProperties.PROPERTY_AMI_WEBBALANCER_SERVER_TEST_URL, DEFAULT_TEST_URL);
			final int testPort = pc.getOptional(AmiWebBalancerProperties.PROPERTY_AMI_WEBBALANCER_SERVER_TEST_URL_PORT, -1);
			final boolean testUrlSecure;
			if (testPort != -1) {
				testUrlSecure = pc.getRequired(AmiWebBalancerProperties.PROPERTY_AMI_WEBBALANCER_SERVER_TEST_URL_SECURE, Boolean.class);
			} else
				testUrlSecure = false;
			long testPeriod = SH.parseDurationTo(pc.getOptional(AmiWebBalancerProperties.PROPERTY_AMI_WEBBALANCER_SERVER_TEST_URL_PERIOD, DEFAULT_TEST_PERIOD),
					TimeUnit.MILLISECONDS);
			if (testPeriod < AmiWebBalancerServerInstance.MIN_TEST_PERIOD)
				throw new RuntimeException("Value for " + AmiWebBalancerProperties.PROPERTY_AMI_WEBBALANCER_SERVER_TEST_URL_PERIOD + " too small: " + testPeriod + " < "
						+ AmiWebBalancerServerInstance.MIN_TEST_PERIOD);

			File serversFile = pc.getRequired(AmiWebBalancerProperties.PROPERTY_AMI_WEBBALANCER_ROUTES_FILE, File.class);
			File sessionsFile = pc.getOptional(AmiWebBalancerProperties.PROPERTY_AMI_WEBBALANCER_SESSIONS_FILE, new File("persist/webbalancer.sessions"));
			long serverCheckPeriodMillis = SH.parseDurationTo(pc.getOptional(AmiWebBalancerProperties.PROPERTY_AMI_WEBBALANCER_SERVER_ALIVE_CHECK_PERIOD, "5 SECONDS"),
					TimeUnit.MILLISECONDS);
			long sessionTimeout = SH.parseDurationTo(pc.getOptional(AmiWebBalancerProperties.PROPERTY_AMI_WEBBALANCER_SESSION_TIMEOUT_PERIOD, "1 MINUTE"), TimeUnit.MILLISECONDS);
			if (!IOH.isFile(serversFile) || !serversFile.canRead())
				try {
					IOH.writeText(serversFile, IOH.readTextFromResource("webbalancer.routes.template"));
					LH.info(log, "Created default routing file at " + IOH.getFullPath(serversFile));
				} catch (Exception e) {
					LH.info(log, "Could not create default relay routes file at ", IOH.getFullPath(serversFile), ": ", e.getMessage());
				}
			if (!IOH.isFile(serversFile) || !serversFile.canRead())
				throw new RuntimeException(
						"Property " + AmiWebBalancerProperties.PROPERTY_AMI_WEBBALANCER_ROUTES_FILE + " refers to missing/unreadable File: " + IOH.getFullPath(serversFile));
			if (!IOH.isFile(sessionsFile)) {
				IOH.ensureDir(sessionsFile.getParentFile());
				LH.info(log, "Creating sessions file at ", IOH.getFullPath(sessionsFile));
				IOH.writeText(sessionsFile, "");
			}
			long checkFilesFrequency = SH.parseDurationTo(pc.getOptional(AmiWebBalancerProperties.PROPERTY_AMI_WEBBALANCER_CHECK_SESSIONS_PERIOD, "5 seconds"),
					TimeUnit.MILLISECONDS);
			CachedFile serversFileCache = new CachedFile(serversFile, checkFilesFrequency);
			bs.getUseAggressiveThreadPool();
			NamedThreadFactory ntf = new NamedThreadFactory("WEBBALANCER-", true);
			AmiWebBalancerServerSelectorPlugin serverSelector = AmiUtils.loadPlugin(ct, AmiWebBalancerProperties.PROPERTY_AMI_WEBBALANCER_SERVER_SELECTOR_PLUGIN_CLASS,
					"Server Selector", AmiWebBalancerServerSelectorPlugin.class);
			if (serverSelector == null) {
				serverSelector = new AmiWebBalancerServerSelectorPlugin_Stats();
				serverSelector.init(ct, pc);
			}

			AmiWebBalancerServer balancer = new AmiWebBalancerServer(new SimpleExecutor(ntf), serversFileCache, sessionsFile, checkFilesFrequency, serverCheckPeriodMillis,
					sessionTimeout, testUrl, testPeriod, testPort, testUrlSecure, serverSelector);
			bs.registerConsoleObject("amiWebBalancer", new AmiWebBalancerConsole(balancer));
			if (httpServerPort != null) {
				final ServerSocketEntitlements sse = AmiUtils.parseWhiteList(ct, pc, AmiWebBalancerProperties.PROPERTY_AMI_WEBBALANCER_HTTP_PORT_WHITELIST);
				String bindAddr = pc.getOptional(AmiWebBalancerProperties.PROPERTY_AMI_WEBBALANCER_HTTP_PORT_BINDADDR, String.class);
				new AmiWebBalancerClientAcceptor(balancer, bindAddr, sse, httpServerPort).start();
			}
			if (httpsServerPort != null) {
				final ServerSocketEntitlements sse = AmiUtils.parseWhiteList(ct, pc, AmiWebBalancerProperties.PROPERTY_AMI_WEBBALANCER_HTTPS_PORT_WHITELIST);
				String bindAddr = pc.getOptional(AmiWebBalancerProperties.PROPERTY_AMI_WEBBALANCER_HTTPS_PORT_BINDADDR, String.class);
				final String pass = pc.getRequired(AmiWebBalancerProperties.PROPERTY_AMI_WEBBALANCER_HTTPS_KEYSTORE_PASSWORD);
				final File store = pc.getRequired(AmiWebBalancerProperties.PROPERTY_AMI_WEBBALANCER_HTTPS_KEYSTORE_FILE, File.class);
				new AmiWebBalancerClientAcceptor(balancer, bindAddr, sse, httpsServerPort, store, pass).start();
			}
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}

}
