package com.f1.omsweb;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import com.f1.base.Message;
import com.f1.bootstrap.Bootstrap;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.RequestMessage;
import com.f1.container.Suite;
import com.f1.container.impl.BasicContainer;
import com.f1.container.impl.ContainerHelper;
import com.f1.fixomsclient.OmsClientSuite;
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
import com.f1.suite.web.PortalHttpHandler;
import com.f1.suite.web.PortalHttpStateCreator;
import com.f1.suite.web.portal.impl.BasicPortletBackendSuite;
import com.f1.suite.web.portal.impl.DropcopyCaptureSuite;
import com.f1.utils.PropertyController;
import com.vortex.ssoweb.LoginHttpHandler;

public class Oms3fWebMain {

	private static final String PORTAL_BASE_URL = "/portal";
	private static final String INDEX_HTM = "index.htm";
	private static final String PORTAL = "portal";
	public static final String OMS_FIXCLIENT = "OMS_FIXCLIENT";
	public static final String OMS_REQUEST = "OMS_REQUEST";
	public static final String OMS = "OMS";

	private static final String HTTP_PORT = "http.port";
	private static final String HTTPS_PORT = "https.port";
	private static final String OMSCLIENT_ORDEREXECS = "OMSCLIENT_ORDEREXECS";
	private static final String TOFRONTEND = "TOFRONTEND";
	private static final String OMSCLIENTFIX = "OMSCLIENTFIX";
	private static final String OMSCOMMAND = "OMSCOMMAND";
	private static final String OMSCLIENT_CONNECTION = "OMSCLIENT_CONNECTION";
	private static final String CON = "con";
	private static final String OMS_OFR_RESPONSE = "oms.ofr.response";
	private static final String OFR_OMS_REQUEST = "ofr.oms.request";
	private static final String OMS_CLIENTFIX = "oms.clientfix";
	private static final String FE_ADMIN_INCOMING = "fe.admin.incoming";
	private static final String FE_DELTAS_OUTGOING = "fe.deltas.outgoing";
	private static final String FE_SNAPSHOT_RESPONSE = "fe.snapshot.response";
	private static final String FE_SNAPSHOT_REQUEST = "fe.snapshot.request";
	private static final String CONNECTION1 = "connection1";
	private static final String OMS_PORT = "oms.port";
	private static final String OMS_HOST = "oms.host";

	private static final String SRC_MAIN_CONFIG = "./src/main/config";

	public static final String OPTION_SSO_PORT = "sso.port";
	public static final String OPTION_SSO_HOST = "sso.host";

	public static void main(String[] a) throws IOException, SQLException {
		ContainerBootstrap bs = new ContainerBootstrap(Oms3fWebMain.class, a);

		bs.setConfigDirProperty(SRC_MAIN_CONFIG);

		//LOGGING

		//bs.setLoggingOverrideProperty(INFO);
		bs.setLoggingOverrideProperty("info");
		//bs.setLogLevel(Level.FINE, Level.FINE, OmsPortalHttpHandler.class);
		//bs.setLogLevel(Level.FINE, Level.FINE, OmsPortalHttpHandler.class);
		//bs.setLogLevel(Level.FINE, Level.FINE, DemoPortalHttpHandler.class);
		//bs.setLogPerformanceLevel(Level.INFO);

		bs.startup();
		//	bs.registerMessagesInPackages(F1AppInstance.class.getPackage());
		//bs.registerMessagesInPackages(TestTrackDeltas.class.getPackage());
		//	bs.registerMessagesInPackages(VortexAgentEntity.class.getPackage());
		//bs.registerMessagesInPackages(VortexBuildProcedure.class.getPackage());

		//PROPERTIES 
		PropertyController props = bs.getProperties();
		int httpPort = props.getOptional(HTTP_PORT, 9095);

		final String omsHost = props.getRequired(OMS_HOST);
		final int omsPort = props.getRequired(OMS_PORT, Integer.class);

		final String ssoHost = props.getRequired(OPTION_SSO_HOST);
		final int ssoPort = props.getRequired(OPTION_SSO_PORT, Integer.class);

		BasicContainer c = new BasicContainer();
		//bs.registerMessagesInPackages("com.f1.pofo", "com.sso.messages", com.f1.povo.vortex.agent.reqres.VortexAgentChangesRequest.class.getPackage().getName());
		//bs.registerMessagesInPackages(VortexEyeManageExpectationRequest.class.getPackage());
		bs.registerMessagesInPackages(F1AppInspectionEntity.class.getPackage());

		bs.prepareContainer(c);

		Suite rs = c.getRootSuite();
		MsgDirectConnection connection = new MsgDirectConnection(new MsgDirectConnectionConfiguration(CONNECTION1));
		connection.addTopic(new MsgDirectTopicConfiguration(FE_SNAPSHOT_REQUEST, omsHost, omsPort, FE_SNAPSHOT_REQUEST));
		connection.addTopic(new MsgDirectTopicConfiguration(FE_SNAPSHOT_RESPONSE, omsHost, omsPort, FE_SNAPSHOT_RESPONSE));
		connection.addTopic(new MsgDirectTopicConfiguration(FE_DELTAS_OUTGOING, omsHost, omsPort, FE_DELTAS_OUTGOING));
		connection.addTopic(new MsgDirectTopicConfiguration(FE_ADMIN_INCOMING, omsHost, omsPort, FE_ADMIN_INCOMING));
		connection.addTopic(new MsgDirectTopicConfiguration(OMS_CLIENTFIX, omsHost, omsPort, OMS_CLIENTFIX));

		// Send and receive OMS actions from OMS
		connection.addTopic(new MsgDirectTopicConfiguration(OFR_OMS_REQUEST, omsHost, omsPort));
		connection.addTopic(new MsgDirectTopicConfiguration(OMS_OFR_RESPONSE, omsHost, omsPort));

		BasicPortletBackendSuite backendSuite = new BasicPortletBackendSuite();
		DropcopyCaptureSuite dropcopyCaptureSuite = new DropcopyCaptureSuite();
		rs.addChildren(backendSuite, dropcopyCaptureSuite);

		//OMS
		bs.registerConsoleObject(CON, new MsgConsole(connection));

		final MsgSuite detlasMsgSuite = new MsgSuite(OMSCLIENT_CONNECTION, connection, FE_DELTAS_OUTGOING, null);
		final MsgSuite omsCommandSuite = new MsgSuite(OMSCOMMAND, connection, OMS_OFR_RESPONSE, OFR_OMS_REQUEST);
		final MsgSuite omsClientFixSuite = new MsgSuite(OMSCLIENTFIX, connection, null, OMS_CLIENTFIX);
		final MsgSuite snapshotMsgSuite = new MsgSuite(TOFRONTEND, connection, FE_SNAPSHOT_RESPONSE, FE_SNAPSHOT_REQUEST);

		OmsClientSuite omsClientSuite = new OmsClientSuite(true);
		//	omsClientSuite.applyPartitionResolver(new BasicPartitionResolver<Action>(Action.class, OMSCLIENT_ORDEREXECS), true, true);
		rs.addChildren(detlasMsgSuite, omsClientSuite, snapshotMsgSuite, omsCommandSuite, omsClientFixSuite);
		ContainerHelper.wireCast(rs, detlasMsgSuite.inboundOutputPort, omsClientSuite.notificationInputPort, true);
		rs.wire(omsClientSuite.snapshotRequestOutputPort, snapshotMsgSuite.getOutboundInputPort(), true);
		rs.wire(snapshotMsgSuite.statusPort, omsClientSuite.statusInputPort, true);
		rs.exposeInputPortAsOutput(omsClientSuite.snapshotRequestInputPort, true);

		//TESTTRACK

		connection.addTopic(new MsgDirectTopicConfiguration("sso.to.client", ssoHost, ssoPort));
		connection.addTopic(new MsgDirectTopicConfiguration("client.to.sso", ssoHost, ssoPort));

		String guiPartition = "SNAPSHOT_PARTITION";

		ClassRoutingProcessor<Message> appRouter = new ClassRoutingProcessor<Message>(Message.class);
		appRouter.bindToPartition(guiPartition);
		BasicHttpServer httpServer = new BasicHttpServer();
		httpServer.addServerSocket(new HttpServerSocket(httpPort));
		final File store = props.getOptional("keystore.file", File.class);
		if (store != null) {
			final String pass = props.getRequired("keystore.password");
			final int secureHttpPort = props.getRequired(HTTPS_PORT, Integer.class);
			HttpServerSocket ssocket = new HttpServerSocket(secureHttpPort, store, pass);
			httpServer.addServerSocket(ssocket);
		}
		HttpWebSuite suite = new HttpWebSuite(httpServer);
		MsgSuite ssoMsgSuite = new MsgSuite("SSOMSGSUITE", connection, "sso.to.client", "client.to.sso", Bootstrap.getProcessUid());
		//	MsgSuite snapshotSuite = new MsgSuite(guiPartition, connection, "server.to.gui", "gui.to.server", Bootstrap.getProcessUid());
		//	rs.addChildren(ssoMsgSuite, snapshotSuite, appRouter);
		rs.addChildren(ssoMsgSuite, appRouter);

		//	rs.wire(backendSuite.newRequestOutputPort("TestTrack", VortexEyeSnapshotRequest.class), snapshotSuite.outboundInputPort, true);
		//	rs.wire(backendSuite.newRequestOutputPort("AgentHistory", VortexEyeQueryHistoryRequest.class), snapshotSuite.outboundInputPort, true);
		//	rs.wire(backendSuite.newOutputPort("AgentUpdate", VortexEyeQueryHistoryRequest.class), snapshotSuite.outboundInputPort, true);
		//rs.wire(snapshotSuite.inboundOutputPort, backendSuite.fromBackendInputPort, true);
		//rs.wire(snapshotSuite.statusPort, backendSuite.fromBackendInputPort, true);

		rs.addChild(suite);
		PortalHttpStateCreator stateCreator = new PortalHttpStateCreator();
		OmsPortletManagerFactory portletManagerFactory = new OmsPortletManagerFactory(c.getTools(), httpServer.getHttpSessionManager().getDefaultFormatter());
		LoginHttpHandler loginHandler = suite.addHttpHandler("/login", new LoginHttpHandler(stateCreator, portletManagerFactory));
		rs.wire(suite.exposeOutputPort(loginHandler.loginPort), ssoMsgSuite.outboundInputPort, true);
		rs.wire(omsClientSuite.clientNotificationOutputPort, backendSuite.fromBackendInputPort, true);
		rs.wire(backendSuite.newRequestOutputPort(OMS, Message.class), omsClientSuite.getLocalSnapshotPort, true);
		rs.wire(backendSuite.newRequestOutputPort(OMS_REQUEST, Message.class), omsCommandSuite.outboundInputPort, true);
		rs.wire(backendSuite.newOutputPort(OMS_FIXCLIENT, Message.class), omsClientFixSuite.outboundInputPort, true);
		rs.wire(backendSuite.newRequestOutputPort("AUDIT", Message.class), dropcopyCaptureSuite.getDropcopysRequestPort, true);
		rs.wire(backendSuite.toBackendDropCopy, dropcopyCaptureSuite.onActionInputPort, true);
		rs.wire(dropcopyCaptureSuite.onActionOutputPort, backendSuite.fromBackendInputPort, true);

		//SSO
		connection.addTopic(new MsgDirectTopicConfiguration("sso.to.broadcast", ssoHost, ssoPort));
		MsgSuite ssoBroadcastMsgSuite = new MsgSuite(guiPartition, connection, "sso.to.broadcast", null, Bootstrap.getProcessUid());
		rs.addChild(ssoBroadcastMsgSuite);

		rs.wire(backendSuite.newRequestOutputPort("SSO", RequestMessage.class), ssoMsgSuite.outboundInputPort, true);
		rs.wire(ssoMsgSuite.inboundOutputPort, backendSuite.fromBackendInputPort, true);
		rs.wire(ssoBroadcastMsgSuite.inboundOutputPort, backendSuite.fromBackendInputPort, true);

		//SsoPortalHttpHandler sso = new SsoPortalHttpHandler(c.getTools(), backendSuite);
		//portletManagerFactory.setSsoHandler(sso);
		PortalHttpHandler portalProcessor = suite.addHttpHandler("\\/portal\\/portal.ajax", new PortalHttpHandler(stateCreator));
		portletManagerFactory.setPortletBackend(backendSuite);
		//sso.setPortletBackend(backendSuite);
		String sessionNames = props.getRequired("session.names", String.class);
		portletManagerFactory.setSessionNames(sessionNames);
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
