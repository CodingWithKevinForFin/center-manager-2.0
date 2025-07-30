package com.vortex.web;

import java.io.File;
import java.io.IOException;

import com.f1.base.Message;
import com.f1.bootstrap.Bootstrap;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.Port;
import com.f1.container.Suite;
import com.f1.container.impl.BasicContainer;
import com.f1.http.handler.FileSystemHttpHandler;
import com.f1.http.handler.JspHttpHandler;
import com.f1.http.impl.BasicHttpServer;
import com.f1.http.impl.HttpServerSocket;
import com.f1.msg.impl.BasicMsgConnectionConfiguration;
import com.f1.msgdirect.MsgDirectConnection;
import com.f1.msgdirect.MsgDirectTopicConfiguration;
import com.f1.suite.utils.ClassRoutingProcessor;
import com.f1.suite.utils.msg.MsgSuite;
import com.f1.suite.web.HttpWebSuite;
import com.f1.suite.web.PortalHttpHandler;
import com.f1.suite.web.PortalHttpStateCreator;
import com.f1.suite.web.WebStateGenerator;
import com.f1.suite.web.portal.impl.BasicPortletBackendSuite;
import com.f1.utils.PropertyController;
import com.f1.utils.concurrent.FastThreadPool;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeQueryHistoryRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeSnapshotRequest;
import com.vortex.ssoweb.LoginHttpHandler;

public class VortexWebMain {

	public static final String OPTION_SSO_PORT = "sso.port";
	public static final String OPTION_SSO_HOST = "sso.host";
	public static final String OPTION_DB_LIMIT = "db.limit";

	public static void main(String a[]) throws IOException {
		ContainerBootstrap bs = new ContainerBootstrap(VortexWebMain.class, a);
		bs.setConfigDirProperty("./src/main/config");
		bs.setLoggingOverrideProperty("warning");
		//bs.setLogPerformanceLevel(Level.INFO);
		BasicContainer c = new BasicContainer();
		c.getPartitionController().registerStateGenerator(new WebStateGenerator());
		bs.startup();
		bs.registerMessagesInPackages("com.f1.povo.sso");
		bs.registerMessagesInPackages("com.f1.povo.app");
		bs.prepareContainer(c);
		Suite rs = c.getRootSuite();
		PropertyController props = bs.getProperties();
		FastThreadPool threadPool = new FastThreadPool(10, "Http");
		threadPool.start();
		BasicHttpServer httpServer = new BasicHttpServer();
		// httpServer.setDebugging(true);
		httpServer.addServerSocket(new HttpServerSocket(props.getOptional("http.port", 9092)));
		HttpWebSuite suite = new HttpWebSuite(httpServer);
		rs.addChild(suite);
		String host = props.getRequired("vortex.eye.host", String.class);
		int port = props.getRequired("vortex.eye.port", Integer.class);

		PortalHttpStateCreator creator = new PortalHttpStateCreator();
		VortexPortalHttpHandler portletManagerFactory = new VortexPortalHttpHandler(c.getTools());
		LoginHttpHandler loginHandler = suite.addHttpHandler("/login", new LoginHttpHandler(creator, portletManagerFactory));
		BasicPortletBackendSuite backend = new BasicPortletBackendSuite();
		suite.addChild(backend);
		PortalHttpHandler portalProcessor = suite.addHttpHandler("\\/portal\\/portal.ajax", new PortalHttpHandler(creator));
		// suite.wire(portalProcessor.loopbackPort, portalProcessor, false);
		// suite.wire(portalProcessor.loopbackPort, portalProcessor, false);
		suite.addHttpHandler("\\/portal\\/*.js*", new JspHttpHandler(true, new File("portal"), "/portal", 1000, "index.htm", bs.getCompiler()));
		suite.addHttpHandler("\\/portal\\/*", new FileSystemHttpHandler(true, new File("portal"), "/portal", 1000, "index.htm"));
		suite.addHttpHandler("*.htm", new JspHttpHandler(true, new File("vortexweb"), "/", 1000, "index.htm", bs.getCompiler()));
		suite.addHttpHandler("*", new FileSystemHttpHandler(true, new File("vortexweb"), "/", 1000, "index.htm"));

		final String ssoHost = props.getRequired(OPTION_SSO_HOST);
		final int ssoPort = props.getRequired(OPTION_SSO_PORT, Integer.class);

		MsgDirectConnection connection = new MsgDirectConnection(new BasicMsgConnectionConfiguration("name"));
		connection.addTopic(new MsgDirectTopicConfiguration("sso.to.client", ssoHost, ssoPort));
		connection.addTopic(new MsgDirectTopicConfiguration("client.to.sso", ssoHost, ssoPort));
		connection.addTopic(new MsgDirectTopicConfiguration("server.to.gui", host, port));
		connection.addTopic(new MsgDirectTopicConfiguration("gui.to.server", host, port));

		String guiPartition = "SNAPSHOT_PARTITION";

		ClassRoutingProcessor<Message> appRouter = new ClassRoutingProcessor<Message>(Message.class);
		appRouter.bindToPartition(guiPartition);

		MsgSuite ssoMsgSuite = new MsgSuite("SSOMSGSUITE", connection, "sso.to.client", "client.to.sso", Bootstrap.getProcessUid());
		MsgSuite snapshotSuite = new MsgSuite(guiPartition, connection, "server.to.gui", "gui.to.server", Bootstrap.getProcessUid());
		suite.addChildren(ssoMsgSuite, snapshotSuite, appRouter);
		//, webProcessor);
		suite.wire(loginHandler.loginPort, ssoMsgSuite.outboundInputPort, true);

		suite.wire(backend.newRequestOutputPort("VortexEye", VortexEyeSnapshotRequest.class), snapshotSuite.outboundInputPort, true);
		suite.wire(backend.newRequestOutputPort("AgentHistory", VortexEyeQueryHistoryRequest.class), snapshotSuite.outboundInputPort, true);
		suite.wire(backend.newOutputPort("AgentUpdate", VortexEyeQueryHistoryRequest.class), snapshotSuite.outboundInputPort, true);
		suite.wire(snapshotSuite.inboundOutputPort, backend.fromBackendInputPort, true);

		c.getDispatchController().setDefaultFutureTimeoutMs(10000);

		((Port) c.getChild("BasicSuiteController/rootSuite/HttpWebSuite/VortexPortalHttpHandler/MessageRequestOutputPort")).setConnectionOptional(true);
		((Port) c.getChild("BasicSuiteController/rootSuite/HttpWebSuite/BasicPortletBackendSuite/TimestampedMessageOutputPort")).setConnectionOptional(true);
		bs.startupContainer(c);
	}
}
