package com.larkinpoint.salestool;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import com.f1.base.Message;
import com.f1.bootstrap.Bootstrap;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.RequestMessage;
import com.f1.container.Suite;
import com.f1.container.impl.BasicContainer;
import com.f1.http.handler.FileSystemHttpHandler;
import com.f1.http.handler.JspHttpHandler;
import com.f1.http.impl.BasicHttpServer;
import com.f1.http.impl.HttpServerSocket;
import com.f1.msg.impl.MsgConsole;
import com.f1.msgdirect.MsgDirectConnection;
import com.f1.msgdirect.MsgDirectConnectionConfiguration;
import com.f1.msgdirect.MsgDirectTopicConfiguration;
import com.f1.povo.f1app.inspect.F1AppInspectionEntity;
import com.f1.suite.utils.ClassRoutingProcessor;
import com.f1.suite.utils.msg.MsgSuite;
import com.f1.suite.web.HttpWebSuite;
import com.f1.suite.web.PortalHttpStateCreator;
import com.f1.suite.web.portal.impl.BasicPortletBackendSuite;
import com.f1.utils.PropertyController;
import com.vortex.ssoweb.LoginHttpHandler;
import com.vortex.ssoweb.SsoPortalHttpHandler;

//import com.vortex.web.TestTrackPortalHttpHandler;

public class LarkinPointMain {
	private static final String PORTAL_BASE_URL = "/portal";
	private static final String INDEX_HTM = "index.htm";
	private static final String PORTAL = "portal";
	public static final String OMS_FIXCLIENT = "OMS_FIXCLIENT";
	public static final String OMS_REQUEST = "OMS_REQUEST";
	public static final String OMS = "OMS";

	private static final String HTTP_PORT = "http.port";
	private static final String HTTPS_PORT = "https.port";

	private static final String TOFRONTEND = "TOFRONTEND";

	private static final String CON = "con";

	private static final String FE_ADMIN_INCOMING = "fe.admin.incoming";
	private static final String FE_DELTAS_OUTGOING = "fe.deltas.outgoing";
	private static final String FE_SNAPSHOT_RESPONSE = "fe.snapshot.response";
	private static final String FE_SNAPSHOT_REQUEST = "fe.snapshot.request";
	private static final String CONNECTION1 = "connection1";

	private static final String SRC_MAIN_CONFIG = "./src/main/config";

	public static final String OPTION_SSO_PORT = "sso.port";
	public static final String OPTION_SSO_HOST = "sso.host";
	public static final String OPTION_ANALYTICS_PORT = "analytics.port";
	public static final String OPTION_ANALYTICS_HOST = "analytics.host";

	public static void main(String[] a) throws IOException, SQLException {

		//BOOTSTRAPPING

		ContainerBootstrap bs = new ContainerBootstrap(LarkinPointMain.class, a);
		bs.setConfigDirProperty(SRC_MAIN_CONFIG);

		//LOGGING

		//bs.setLoggingOverrideProperty("verbose");
		bs.setLoggingOverrideProperty("info");
		//bs.setLogLevel(Level.FINE, Level.FINE, OmsPortalHttpHandler.class);
		//bs.setLogLevel(Level.FINE, Level.FINE, DemoPortalHttpHandler.class);
		//bs.setLogPerformanceLevel(Level.INFO);

		bs.startup();

		//PROPERTIES 
		PropertyController props = bs.getProperties();
		int httpPort = props.getOptional(HTTP_PORT, 9095);
		int httpsPort = props.getOptional(HTTPS_PORT, 9096);

		//	final String omsHost = props.getRequired(OMS_HOST);
		//	final int omsPort = props.getRequired(OMS_PORT, Integer.class);

		final String ssoHost = props.getRequired(OPTION_SSO_HOST);
		final int ssoPort = props.getRequired(OPTION_SSO_PORT, Integer.class);
		final String analyticsHost = props.getRequired(OPTION_ANALYTICS_HOST, String.class);
		final int analyticsPort = props.getRequired(OPTION_ANALYTICS_PORT, Integer.class);

		BasicContainer c = new BasicContainer();
		bs.registerMessagesInPackages(F1AppInspectionEntity.class.getPackage());
		bs.registerMessagesInPackages("com.larkinpoint.messages");
		bs.prepareContainer(c);

		Suite rs = c.getRootSuite();
		MsgDirectConnection connection = new MsgDirectConnection(new MsgDirectConnectionConfiguration(CONNECTION1));

		BasicPortletBackendSuite backendSuite = new BasicPortletBackendSuite();
		rs.addChildren(backendSuite);

		//OMS
		bs.registerConsoleObject(CON, new MsgConsole(connection));

		//TESTTRACK

		connection.addTopic(new MsgDirectTopicConfiguration("sso.to.client", ssoHost, ssoPort));
		connection.addTopic(new MsgDirectTopicConfiguration("client.to.sso", ssoHost, ssoPort));
		connection.addTopic(new MsgDirectTopicConfiguration("server.to.gui", analyticsHost, analyticsPort));
		connection.addTopic(new MsgDirectTopicConfiguration("gui.to.server", analyticsHost, analyticsPort));

		String guiPartition = "SNAPSHOT_PARTITION";

		ClassRoutingProcessor<Message> appRouter = new ClassRoutingProcessor<Message>(Message.class);
		appRouter.bindToPartition(guiPartition);
		BasicHttpServer httpServer = new BasicHttpServer();
		httpServer.addServerSocket(new HttpServerSocket(httpPort));
		httpServer.getAttributes().put("index_title", props.getOptional("index_title", ""));

		final File store = props.getOptional("keystore.file", File.class);
		if (store != null) {
			final String pass = props.getRequired("keystore.password");
			final int secureHttpPort = props.getRequired(HTTPS_PORT, Integer.class);
			HttpServerSocket ssocket = new HttpServerSocket(secureHttpPort, store, pass);
			httpServer.addServerSocket(ssocket);
		}
		HttpWebSuite suite = new HttpWebSuite(httpServer);
		MsgSuite ssoMsgSuite = new MsgSuite("SSOMSGSUITE", connection, "sso.to.client", "client.to.sso", Bootstrap.getProcessUid());
		MsgSuite snapshotSuite = new MsgSuite(guiPartition, connection, "server.to.gui", "gui.to.server", Bootstrap.getProcessUid());
		rs.addChildren(ssoMsgSuite, snapshotSuite, appRouter);

		rs.wire(backendSuite.newRequestOutputPort("LARKIN", RequestMessage.class), snapshotSuite.outboundInputPort, true);
		rs.wire(backendSuite.newOutputPort("LARKIN", Message.class), snapshotSuite.outboundInputPort, true);
		rs.wire(snapshotSuite.inboundOutputPort, backendSuite.fromBackendInputPort, true);
		rs.wire(snapshotSuite.statusPort, backendSuite.fromBackendInputPort, true);

		rs.addChild(suite);
		PortalHttpStateCreator stateCreator = new PortalHttpStateCreator();
		LoginHttpHandler loginHandler = suite.addHttpHandler("/login", new LoginHttpHandler(stateCreator));
		//	suite.addHttpHandler("\\/portal\\/*.js*", new JspHttpHandler(true, new File("portal"), "/portal", 1000, "index.htm", bs.getCompiler()));
		//	suite.addHttpHandler("\\/portal\\/*", new FileSystemHttpHandler(true, new File("portal"), "/portal", 1000, "index.htm"));
		//	suite.addHttpHandler("*.htm", new JspHttpHandler(true, new File("vortexweb"), "/", 1000, "index.htm", bs.getCompiler()));
		//	suite.addHttpHandler("*", new FileSystemHttpHandler(true, new File("vortexweb"), "/", 1000, "index.htm"));
		rs.wire(suite.exposeOutputPort(loginHandler.loginPort), ssoMsgSuite.outboundInputPort, true);

		//SSO
		connection.addTopic(new MsgDirectTopicConfiguration("sso.to.broadcast", ssoHost, ssoPort));
		MsgSuite ssoBroadcastMsgSuite = new MsgSuite(guiPartition, connection, "sso.to.broadcast", null, Bootstrap.getProcessUid());
		rs.addChild(ssoBroadcastMsgSuite);
		rs.wire(backendSuite.newRequestOutputPort("SSO", RequestMessage.class), ssoMsgSuite.outboundInputPort, true);
		rs.wire(backendSuite.newRequestOutputPort("SSO", RequestMessage.class), ssoMsgSuite.outboundInputPort, true);
		rs.wire(ssoMsgSuite.inboundOutputPort, backendSuite.fromBackendInputPort, true);
		rs.wire(ssoBroadcastMsgSuite.inboundOutputPort, backendSuite.fromBackendInputPort, true);

		//	OmsPortalHttpHandler oms = new OmsPortalHttpHandler(null);
		SsoPortalHttpHandler sso = new SsoPortalHttpHandler(null, stateCreator);
		LarkinPointHttpHandler portalProcessor = suite.addHttpHandler("\\/portal\\/portal.ajax", new LarkinPointHttpHandler(sso, stateCreator));
		portalProcessor.setCallback("/portal/portal.ajax");
		portalProcessor.setPortletBackend(backendSuite);
		sso.setPortletBackend(backendSuite);
		suite.addHttpHandler("\\/portal\\/*.js*", new JspHttpHandler(true, new File(PORTAL), PORTAL_BASE_URL, 1000, INDEX_HTM, bs.getCompiler()));
		suite.addHttpHandler("\\/portal\\/*", new FileSystemHttpHandler(true, new File(PORTAL), PORTAL_BASE_URL, 1000, INDEX_HTM));
		suite.addHttpHandler("*.htm", new JspHttpHandler(true, new File("vortexweb"), "/", 1000, "index.htm", bs.getCompiler()));
		suite.addHttpHandler("*", new FileSystemHttpHandler(true, new File("vortexweb"), "/", 1000, "index.htm"));
		c.getDispatchController().setDefaultFutureTimeoutMs(10000);
		bs.startupContainer(c);

		//		suite.wire(snapshotSuite.inboundOutputPort, backendSuite.fromBackendInputPort, true);

		//((Port) c.getChild("BasicSuiteController/rootSuite/HttpWebSuite/SsoPortalHttpHandler/MessageRequestOutputPort")).setConnectionOptional(true);
		//((Port) c.getChild("BasicSuiteController/rootSuite/HttpWebSuite/BasicPortletBackendSuite/TimestampedMessageOutputPort")).setConnectionOptional(true);
	}
}
