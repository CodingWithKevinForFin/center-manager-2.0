package com.f1.ami.webmanager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.f1.ami.amicommon.AmiStartup;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiWebManagerGetFileRequest;
import com.f1.ami.amicommon.msg.AmiWebManagerGetFileResponse;
import com.f1.ami.amicommon.msg.AmiWebManagerListRootsRequest;
import com.f1.ami.amicommon.msg.AmiWebManagerListRootsResponse;
import com.f1.ami.amicommon.msg.AmiWebManagerProcessSpecialFileRequest;
import com.f1.ami.amicommon.msg.AmiWebManagerProcessSpecialFileResponse;
import com.f1.ami.amicommon.msg.AmiWebManagerPutFileRequest;
import com.f1.ami.amicommon.msg.AmiWebManagerPutFileResponse;
import com.f1.base.Message;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.Suite;
import com.f1.container.impl.BasicContainer;
import com.f1.msgdirect.MsgDirectConnection;
import com.f1.msgdirect.MsgDirectConnectionConfiguration;
import com.f1.msgdirect.MsgDirectTopicConfiguration;
import com.f1.suite.utils.ClassRoutingProcessor;
import com.f1.suite.utils.msg.MsgSuite;
import com.f1.utils.EH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.ServerSocketEntitlements;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.encrypt.EncoderUtils;

public class AmiWebManagerMain {

	public static void main(String a[]) throws IOException {
		ContainerBootstrap bs = new ContainerBootstrap(AmiWebManagerMain.class, a);
		bs.setProperty("f1.appname", "AmiWebManager");
		bs.setProperty("f1.logfilename", "AmiWebManager");
		bs.setProperty("f1.autocoded.disabled", "false");
		bs.setProperty("f1.threadpool.agressive", "false");
		AmiStartup.startupAmi(bs, "ami_webmanager");
		main2(bs);
	}

	public static void main2(ContainerBootstrap cb) throws IOException {
		final PropertyController props = cb.getProperties();
		BasicContainer container = new BasicContainer();
		container.setName("AmiWebManager");
		container.getDispatchController().setQueueTimeoutCheckFrequency(1);
		cb.prepareContainer(container);
		final int port = props.getRequired(AmiWebManagerProperties.PROPERTY_AMI_WEBMANAGER_PORT, Caster_Integer.INSTANCE);
		String portBindAddr = props.getOptional(AmiWebManagerProperties.PROPERTY_AMI_WEBMANAGER_PORT_BINDADDR);
		ServerSocketEntitlements entitlements = AmiUtils.parseWhiteList(container.getTools(), props, AmiWebManagerProperties.PROPERTY_AMI_WEBMANAGER_PORT_WHITELIST);

		MsgDirectConnectionConfiguration config = new MsgDirectConnectionConfiguration("ami_webmanager");

		String sslKeyFileName = props.getOptional(AmiWebManagerProperties.PROPERTY_AMI_WEBMANAGER_SSL_KEYSTORE_FILE);
		String sslKeyText = props.getOptional(AmiWebManagerProperties.PROPERTY_AMI_WEBMANAGER_SSL_KEYSTORE_TEXT_BASE64);
		String sslKeyPassword = props.getOptional(AmiWebManagerProperties.PROPERTY_AMI_WEBMANAGER_SSL_KEYSTORE_PASSWORD);

		byte[] sslKeyData;
		File sslKeyFile;
		if (SH.is(sslKeyText)) {
			if (SH.is(sslKeyFileName))
				throw new RuntimeException(AmiWebManagerProperties.PROPERTY_AMI_WEBMANAGER_SSL_KEYSTORE_TEXT_BASE64 + " and "
						+ AmiWebManagerProperties.PROPERTY_AMI_WEBMANAGER_SSL_KEYSTORE_FILE + " are mutually exclusive");
			sslKeyData = EncoderUtils.decodeCert(sslKeyText);
			sslKeyFile = null;
			config.setKeystore(sslKeyData, sslKeyPassword);
			config.setForceSsl(true);
		} else if (SH.is(sslKeyFileName)) {
			sslKeyData = null;
			sslKeyFile = new File(sslKeyFileName);
			config.setKeystore(sslKeyFile, sslKeyPassword);
			config.setForceSsl(true);
		} else {
			sslKeyData = null;
			sslKeyFile = null;
		}

		MsgDirectTopicConfiguration webManagerToWebConfig = new MsgDirectTopicConfiguration("webmanager.to.web", port).setServerBindAddress(portBindAddr)
				.setServerSocketEntitlements(entitlements);
		MsgDirectTopicConfiguration webToWebManagerConfig = new MsgDirectTopicConfiguration("web.to.webmanager", port).setServerBindAddress(portBindAddr)
				.setServerSocketEntitlements(entitlements);

		MsgDirectConnection connection = new MsgDirectConnection(config);
		connection.addTopic(webManagerToWebConfig);
		connection.addTopic(webToWebManagerConfig);

		Suite rs = container.getSuiteController().getRootSuite();
		MsgSuite msgSuite = new MsgSuite("MSG_WEBMANAGER", connection, "web.to.webmanager", "webmanager.to.web");
		ClassRoutingProcessor<Message> router = new ClassRoutingProcessor<Message>(Message.class);

		String pwd = props.getOptional(AmiWebManagerProperties.PROPERTY_AMI_WEBMANAGER_MAPPING_PWD, EH.getPwd());
		String roots = props.getOptional(AmiWebManagerProperties.PROPERTY_AMI_WEBMANAGER_MAPPING_DIRS);
		if (roots == null)
			roots = EH.isWindows() ? "C:/=C:/" : "/=/";
		boolean strict = props.getOptional(AmiWebManagerProperties.PROPERTY_AMI_WEBMANAGER_MAPPING_STRICT, Boolean.TRUE);
		String[] rootsList = SH.split(',', roots);
		String pwdLogical = SH.beforeFirst(pwd, '=', pwd);
		String pwdActual = SH.afterFirst(pwd, '=', pwd);
		Map<String, String> logical2actual = new HashMap<String, String>();
		for (String s : rootsList) {
			String l = SH.beforeFirst(s, '=', s);
			String a = SH.afterFirst(s, '=', s);
			logical2actual.put(l, a);
		}
		AmiWebManagerController manager = new AmiWebManagerController(strict, pwdLogical, pwdActual, logical2actual);
		AmiWebManagerGetFileProcessor getFileProcessor = new AmiWebManagerGetFileProcessor(manager);
		getFileProcessor.bindToPartition(null);
		AmiWebManagerPutFileProcessor putFileProcessor = new AmiWebManagerPutFileProcessor(manager);
		putFileProcessor.bindToPartition(null);
		AmiWebManagerListRootsProcessor listRootsProcessor = new AmiWebManagerListRootsProcessor(manager);
		listRootsProcessor.bindToPartition(null);
		AmiWebManagerProcessSpecialFileProcessor getSpecialProcessor = new AmiWebManagerProcessSpecialFileProcessor(manager);
		getSpecialProcessor.bindToPartition(null);

		rs.addChild(msgSuite);
		rs.addChild(router);
		rs.addChild(getFileProcessor);
		rs.addChild(putFileProcessor);
		rs.addChild(listRootsProcessor);
		rs.addChild(getSpecialProcessor);

		rs.wire(msgSuite.getInboundOutputPort(), router.getInputPort(), false);
		rs.wire(router.newRequestOutputPort(AmiWebManagerGetFileRequest.class, AmiWebManagerGetFileResponse.class), getFileProcessor.getInputPort(), true);
		rs.wire(router.newRequestOutputPort(AmiWebManagerPutFileRequest.class, AmiWebManagerPutFileResponse.class), putFileProcessor.getInputPort(), true);
		rs.wire(router.newRequestOutputPort(AmiWebManagerListRootsRequest.class, AmiWebManagerListRootsResponse.class), listRootsProcessor.getInputPort(), true);
		rs.wire(router.newRequestOutputPort(AmiWebManagerProcessSpecialFileRequest.class, AmiWebManagerProcessSpecialFileResponse.class), getSpecialProcessor.getInputPort(), true);
		cb.startupContainer(container);

	}
}
