package com.vortex.ssoweb;

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
import com.sso.messages.CreateSsoGroupRequest;
import com.sso.messages.CreateSsoUserRequest;
import com.sso.messages.QuerySsoGroupRequest;
import com.sso.messages.QuerySsoHistoryRequest;
import com.sso.messages.QuerySsoUserRequest;
import com.sso.messages.ResetPasswordRequest;
import com.sso.messages.UpdateSsoGroupRequest;
import com.sso.messages.UpdateSsoUserRequest;

public class SsoWebMain {

	public static final String OPTION_SSO_PORT = "sso.port";
	public static final String OPTION_SSO_HOST = "sso.host";
	public static final String OPTION_DB_LIMIT = "db.limit";

	public static void main(String a[]) throws IOException {
		ContainerBootstrap bs = new ContainerBootstrap(SsoWebMain.class, a);
		bs.setConfigDirProperty("./src/main/config");
		bs.setLoggingOverrideProperty("info");
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
		httpServer.addServerSocket(new HttpServerSocket(9093));
		HttpWebSuite suite = new HttpWebSuite(httpServer);
		rs.addChild(suite);

		PortalHttpStateCreator httpStateCreator = new PortalHttpStateCreator();
		BasicPortletBackendSuite backend = new BasicPortletBackendSuite();
		SsoPortletManagerFactory portletManagerFactory = new SsoPortletManagerFactory(c.getTools(), httpServer.getHttpSessionManager().getDefaultFormatter());
		LoginHttpHandler loginHandler = suite.addHttpHandler("/login", new LoginHttpHandler(httpStateCreator, portletManagerFactory));
		suite.addChild(backend);
		PortalHttpHandler portalProcessor = suite.addHttpHandler("\\/portal\\/portal.ajax", new PortalHttpHandler(httpStateCreator));
		suite.addHttpHandler("\\/portal\\/*.js*", new JspHttpHandler(true, new File("portal"), "/portal", 1000, "index.htm", bs.getCompiler()));
		suite.addHttpHandler("\\/portal\\/*", new FileSystemHttpHandler(true, new File("portal"), "/portal", 1000, "index.htm"));
		suite.addHttpHandler("*.htm", new JspHttpHandler(true, new File("ssoweb"), "/", 1000, "index.htm", bs.getCompiler()));
		suite.addHttpHandler("*", new FileSystemHttpHandler(true, new File("ssoweb"), "/", 1000, "index.htm"));

		final String ssoHost = props.getRequired(OPTION_SSO_HOST);
		final int ssoPort = props.getRequired(OPTION_SSO_PORT, Integer.class);

		MsgDirectConnection connection = new MsgDirectConnection(new BasicMsgConnectionConfiguration("name"));
		connection.addTopic(new MsgDirectTopicConfiguration("sso.to.client", ssoHost, ssoPort));
		connection.addTopic(new MsgDirectTopicConfiguration("client.to.sso", ssoHost, ssoPort));
		connection.addTopic(new MsgDirectTopicConfiguration("sso.to.broadcast", ssoHost, ssoPort));

		String guiPartition = "SNAPSHOT_PARTITION";

		ClassRoutingProcessor<Message> appRouter = new ClassRoutingProcessor<Message>(Message.class);
		appRouter.bindToPartition(guiPartition);
		MsgSuite ssoMsgSuite = new MsgSuite("SSOMSGSUITE", connection, "sso.to.client", "client.to.sso", Bootstrap.getProcessUid());
		MsgSuite broadcastSuite = new MsgSuite(guiPartition, connection, "sso.to.broadcast", null, Bootstrap.getProcessUid());
		suite.addChildren(ssoMsgSuite, broadcastSuite, appRouter);
		suite.wire(loginHandler.loginPort, ssoMsgSuite.outboundInputPort, true);
		suite.wire(backend.newRequestOutputPort("CreateSsoUser", CreateSsoUserRequest.class), ssoMsgSuite.outboundInputPort, true);
		suite.wire(backend.newRequestOutputPort("CreateSsoGroup", CreateSsoGroupRequest.class), ssoMsgSuite.outboundInputPort, true);
		suite.wire(backend.newRequestOutputPort("ResetSsoPassword", ResetPasswordRequest.class), ssoMsgSuite.outboundInputPort, true);
		suite.wire(backend.newRequestOutputPort("UpdateSsoUser", UpdateSsoUserRequest.class), ssoMsgSuite.outboundInputPort, true);
		suite.wire(backend.newRequestOutputPort("UpdateSsoGroup", UpdateSsoGroupRequest.class), ssoMsgSuite.outboundInputPort, true);
		//		suite.wire(backend.newRequestOutputPort("LoginSsoUser", LoginSsoUserRequest.class), ssoMsgSuite.outboundInputPort, true);
		suite.wire(backend.newRequestOutputPort("QuerySsoUser", QuerySsoUserRequest.class), ssoMsgSuite.outboundInputPort, true);
		suite.wire(backend.newRequestOutputPort("QuerySsoHistory", QuerySsoHistoryRequest.class), ssoMsgSuite.outboundInputPort, true);
		suite.wire(backend.newRequestOutputPort("QuerySsoGroup", QuerySsoGroupRequest.class), ssoMsgSuite.outboundInputPort, true);
		suite.wire(ssoMsgSuite.inboundOutputPort, backend.fromBackendInputPort, true);
		suite.wire(broadcastSuite.inboundOutputPort, backend.fromBackendInputPort, true);

		//		suite.wire(snapshotSuite.inboundOutputPort, backend.fromBackendInputPort, true);
		c.getDispatchController().setDefaultFutureTimeoutMs(10000);
		((Port) c.getChild("BasicSuiteController/rootSuite/HttpWebSuite/SsoPortalHttpHandler/MessageRequestOutputPort")).setConnectionOptional(true);
		((Port) c.getChild("BasicSuiteController/rootSuite/HttpWebSuite/BasicPortletBackendSuite/TimestampedMessageOutputPort")).setConnectionOptional(true);
		bs.startupContainer(c);
	}
}
