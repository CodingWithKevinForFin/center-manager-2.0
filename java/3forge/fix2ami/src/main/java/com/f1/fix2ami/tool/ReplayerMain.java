package com.f1.fix2ami.tool;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

import com.f1.bootstrap.Bootstrap;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.dropCopy.DropCopyMain;
import com.f1.transportManagement.SessionManager;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;

import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.DefaultDataDictionaryProvider;
import quickfix.FieldConvertError;
import quickfix.Message;

public class ReplayerMain {
	private static final Logger log = Logger.getLogger(ReplayerMain.class.getName());
	public final static String ATTR_DROPCOPY_SESSION_NAME = "replayer.DropcopySessionName";
	public final static String ATTR_REPLACE_TRANSACT_TIME = "replayer.ReplaceTransactTime";
	public final static String ATTR_TEST_MSG_FILE = "replayer.TestMessageFile";
	public static final String ATTR_MSG_DELAY = "replayer.MessageDelayInMilli";
	public static final String ATTR_INITIAL_DELAY = "replayer.InitialDelayInMilli";

	public static void main(String args[]) throws IOException, ConfigError, FieldConvertError, InterruptedException {
		Bootstrap bs = new ContainerBootstrap(DropCopyMain.class, args);
		bs.setLoggingOverrideProperty("quiet");
		bs.setConfigDirProperty("./src/main/config/dropcopy");
		bs.startup();

		final PropertyController props = bs.getProperties();
		DataDictionary dictionary = null;
		final String dropcopySessionName = props.getOptional(ATTR_DROPCOPY_SESSION_NAME, "dropstream");
		final String fixXmlFile = props.getOptional("qfix." + dropcopySessionName + ".DataDictionary");
		if (null != fixXmlFile) {
			try {
				dictionary = new DataDictionary(fixXmlFile);
			} catch (ConfigError ce) {
				LH.warning(log, "FIX dictionary xml file error", ce);
				throw new IllegalStateException("failed to create FIX dictionary from xml file: " + fixXmlFile);
			}
		} else {
			final String version = props.getOptional("qfix." + dropcopySessionName + ".BeginString", "FIX.4.2");
			dictionary = new DefaultDataDictionaryProvider().getSessionDataDictionary(version);
		}
		final int msgDelayInMillis = Integer.parseInt(props.getOptional(ATTR_MSG_DELAY, "10000"));
		final int initialDelayInMilli = Integer.parseInt(props.getOptional(ATTR_INITIAL_DELAY, "15000"));

		SessionManager sessionManager = new SessionManager(bs.getProperties());
		sessionManager.start();

		LH.info(log, "Initial " + initialDelayInMilli + "ms delay before sending message at " + msgDelayInMillis + "ms interval.");
		ToolUtils.pause(initialDelayInMilli);

		final SessionManager.FixSessionContext dropCopyContext = sessionManager.getFixSessionContext(dropcopySessionName);
		final String filename = props.getOptional(ATTR_TEST_MSG_FILE, "data/junitFix.log");
		final boolean replaceTransactTime = Boolean.valueOf(props.getOptional(ATTR_REPLACE_TRANSACT_TIME, "false"));
		int i = 0;

		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			Message msg = null;
			while ((msg = ToolUtils.getAMsg(dictionary, reader, replaceTransactTime)) != null) {
				LH.info(log, "  message ", i++);
				dropCopyContext.getSenderFixSession().getSenderSession().send(msg);
				ToolUtils.pause(msgDelayInMillis);
			}
		} catch (FileNotFoundException fnf) {
			if (null == filename) {
				LH.warning(log, "TestMsgFile property is not set.");
			} else {
				LH.warning(log, "Test message file (" + filename + ") is missing.");
			}
		} catch (IOException io) {
			LH.warning(log, "Failed to read test message file.");
		}
		sessionManager.waitForComplete();
	}

}
