package com.f1.ami.amicommon;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.f1.ami.amicommon.msg.AmiRelayOnConnectRequest;
import com.f1.ami.web.auth.AmiAuthenticatorPlugin;
import com.f1.base.Encrypter;
import com.f1.base.PasswordEncrypterManager;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.Container;
import com.f1.container.ContainerTools;
import com.f1.container.impl.ContainerToolsWrapper;
import com.f1.utils.CH;
import com.f1.utils.CasterManager;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;

public class AmiStartup {
	static {
		CasterManager.addCasterForce(AmiCaster_Set.INSTANCE);
		CasterManager.addCasterForce(AmiCaster_List.INSTANCE);
		CasterManager.addCasterForce(AmiCaster_Map.INSTANCE);
		CasterManager.addCasterForce(AmiCaster_String.INSTANCE);
		CasterManager.addCasterForce(AmiCaster_StringBuilder.INSTANCE);
	}
	public static final String SRC_MAIN_CONFIG = "./src/main/config";
	public static final String BUILD_PROPERTY_VERSION = "version";

	private static final Map<String, String> BUILD_PROPERTIES = new HashMap<String, String>();
	private static String BUILD_FILE_NAME;
	private static boolean BUILD_FILE_EXISTS;

	public static ContainerBootstrap startupAmi(Class main, String a[], String projectName) {
		return startupAmi(new ContainerBootstrap(main, a), projectName);
	}
	public static ContainerBootstrap startupAmi(ContainerBootstrap bs, String projectName) {
		processBuildTxt(projectName);
		bs.setProperty("ami.sampledata.file", "${f1.conf.dir}/../data/sample_data.txt");
		if (bs.getProperty("ami.shared.layouts.dir") == null)
			bs.setProperty("ami.shared.layouts.dir", "${f1.conf.dir}/../data/shared");
		bs.setConfigDirProperty(SRC_MAIN_CONFIG);
		bs.setInspectingModeProperty(false);
		bs.getContainers();
		bs.setConsoleAuthenticator(null);//prevent login during startup. new AmiConsoleAuthenticator(consoleAuthenticator));
		bs.setF1MonitoringEnabledProperty(false);
		bs.setTerminateFileProperty("${f1.conf.dir}/../." + bs.getMainClass().getSimpleName().toLowerCase() + ".prc");
		bs.startup();
		long statsPeriod = SH.parseDurationTo(bs.getProperties().getOptional(AmiCommonProperties.PROPERTY_AMI_AMILOG_STATS_PERIOD, "15 SECONDS"), TimeUnit.MILLISECONDS);
		AmiProcessStatsLogger.INSTANCE.setFrequency(statsPeriod);
		AmiProcessStatsLogger.startup();
		bs.registerMessagesInPackages(AmiRelayOnConnectRequest.class.getPackage());
		return bs;
	}

	private static void processBuildTxt(String projectName) {
		String file = projectName + ".build.txt";
		BUILD_FILE_NAME = file;
		try {
			String txt = IOH.readTextFromResource(file);
			if (txt == null) {
				System.out.println("Resource '" + file + "' not available.");
			} else {
				for (String line : SH.splitLines(txt))
					BUILD_PROPERTIES.put(SH.beforeFirst(line, '='), SH.afterFirst(line, '='));
				System.out.println("Resource '" + file + "' processed: " + SH.joinMap(',', '=', BUILD_PROPERTIES));
				BUILD_FILE_EXISTS = true;
			}
		} catch (Exception e) {
			System.out.println("Resource '" + file + "' not available: " + e);
		}
	}
	public static String getBuildProperty(String key) {
		return BUILD_PROPERTIES.get(key);
	}
	public static Set<String> getBuildProperties() {
		return BUILD_PROPERTIES.keySet();
	}

	public static void startupComplete(ContainerBootstrap bs) {
		Container container = CH.first(bs.getContainers());
		final ContainerTools tools = container != null ? container.getTools() : new ContainerToolsWrapper(bs.getProperties());
		AmiAuthenticatorPlugin consoleAuthenticator = AmiUtils.loadAuthenticatorPlugin(tools, AmiCommonProperties.PROPERTY_AMI_CONSOLE_AUTH_PLUGIN_CLASS,
				"Ami Console Authenticator Plugin");
		Encrypter passwordEncrypter = AmiUtils.loadEncrypter(tools, AmiCommonProperties.PROPERTY_AMI_PASSWORD_ENCRYPTER_CLASS, "Ami Password Encrypter");
		PasswordEncrypterManager.setEncrypter(passwordEncrypter);
		bs.getConsoleServer().getManager().setAuthenticator(new AmiConsoleAuthenticator(consoleAuthenticator));
		Logger log = LH.get();
		if (BUILD_FILE_EXISTS)
			LH.info(log, "Resource File '", BUILD_FILE_NAME + "' contains ", BUILD_PROPERTIES.size() + " entry(s)");
		else
			LH.info(log, "Resource File '", BUILD_FILE_NAME + "' not found");
		for (String key : CH.sort(BUILD_PROPERTIES.keySet()))
			LH.info(log, "Property: ", key, "=", getBuildProperty(key));
		bs.startupComplete();
		bs.keepAlive();
	}
}
