package com.f1.vortexglass;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import com.f1.base.Message;
import com.f1.bootstrap.Bootstrap;
import com.f1.bootstrap.ContainerBootstrap;
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
import com.f1.povo.f1app.F1AppInstance;
import com.f1.povo.f1app.inspect.F1AppInspectionEntity;
import com.f1.suite.utils.ClassRoutingProcessor;
import com.f1.suite.utils.msg.MsgSuite;
import com.f1.suite.web.HttpWebSuite;
import com.f1.suite.web.PortalHttpHandler;
import com.f1.suite.web.PortalHttpStateCreator;
import com.f1.suite.web.portal.impl.BasicPortletBackendSuite;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_File;
import com.f1.vortexcommon.msg.agent.TestTrackDeltas;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentChangesRequest;
import com.f1.vortexcommon.msg.eye.VortexBuildProcedure;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageExpectationRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeQueryHistoryRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeSnapshotRequest;
import com.sso.messages.SsoRequest;
import com.vortex.ssoweb.LoginHttpHandler;
import com.vortex.ssoweb.ResetPassword2HttpHandler;
import com.vortex.ssoweb.ResetPasswordHttpHandler;
import com.vortex.ssoweb.SsoPortletManagerFactory;
import com.vortex.web.VortexPortalHttpHandler;

//import com.vortex.web.TestTrackPortalHttpHandler;

public class VortexGlass {

	public static final String OPTION_VORTEXEYE_GLASS_PORT = "vortex.eye.glass.port";
	public static final String OPTION_VORTEXEYE_GLASS_SSL_KEYSTORE_FILE = "vortex.eye.glass.ssl.keystore.file";
	public static final String OPTION_VORTEXEYE_GLASS_SSL_KEYSTORE_PASS = "vortex.eye.glass.ssl.keystore.password";
	public static final String OPTION_VORTEXEYE_GLASS_SSL_PORT = "vortex.eye.glass.ssl.port";

	private static final String PORTAL_BASE_URL = "/portal";
	private static final String INDEX_HTM = "index.htm";
	private static final String PORTAL = "portal";
	public static final String OMS_FIXCLIENT = "OMS_FIXCLIENT";
	public static final String OMS_REQUEST = "OMS_REQUEST";
	public static final String OMS = "OMS";
	private static final String HTTP_PORT = "http.port";
	private static final String HTTPS_PORT = "https.port";
	private static final String CON = "con";
	private static final String CONNECTION1 = "connection1";
	private static final String SRC_MAIN_CONFIG = "./src/main/config";
	public static final String OPTION_SSO_PORT = "sso.port";
	public static final String OPTION_SSO_SSL_PORT = "sso.ssl.port";
	public static final String OPTION_SSO_HOST = "sso.host";
	public static final String OPTION_VORTEXEYE_PORT = "vortex.eye.port";
	public static final String OPTION_VORTEXEYE_HOST = "vortex.eye.host";

	public static void main(String[] a) throws IOException, SQLException {

		//BOOTSTRAPPING

		ContainerBootstrap bs = new ContainerBootstrap(VortexGlass.class, a);
		bs.setConfigDirProperty(SRC_MAIN_CONFIG);

		//LOGGING

		bs.setLoggingOverrideProperty("info");
		//bs.setLoggingOverrideProperty("verbose");
		//bs.setLogLevel(Level.FINE, Level.FINE, OmsPortalHttpHandler.class);
		//bs.setLogLevel(Level.FINE, Level.FINE, VortexGlassPortalHttpHandler.class);
		//bs.setLogPerformanceLevel(Level.INFO);

		bs.startup();
		bs.registerMessagesInPackages(F1AppInstance.class.getPackage());
		bs.registerMessagesInPackages(TestTrackDeltas.class.getPackage());
		bs.registerMessagesInPackages(VortexAgentEntity.class.getPackage());
		bs.registerMessagesInPackages(VortexBuildProcedure.class.getPackage());

		//PROPERTIES 
		PropertyController props = bs.getProperties();
		int httpPort = props.getOptional(HTTP_PORT, 9095);

		//	final String omsHost = props.getRequired(OMS_HOST);
		//	final int omsPort = props.getRequired(OMS_PORT, Integer.class);

		final String vortexEyeHost = props.getRequired(OPTION_VORTEXEYE_HOST, String.class);
		final int vortexEyePort = props.getRequired(OPTION_VORTEXEYE_PORT, Integer.class);

		BasicContainer c = new BasicContainer();
		bs.registerMessagesInPackages("com.f1.pofo", "com.sso.messages", VortexAgentChangesRequest.class.getPackage().getName());
		bs.registerMessagesInPackages(VortexEyeManageExpectationRequest.class.getPackage());
		bs.registerMessagesInPackages(F1AppInspectionEntity.class.getPackage());

		bs.prepareContainer(c);

		Suite rs = c.getRootSuite();
		MsgDirectConnectionConfiguration ssoConnConfig = new MsgDirectConnectionConfiguration(CONNECTION1);
		MsgDirectConnection connection = new MsgDirectConnection(ssoConnConfig);

		BasicPortletBackendSuite backendSuite = new BasicPortletBackendSuite();
		//DropcopyCaptureSuite dropcopyCaptureSuite = new DropcopyCaptureSuite();
		rs.addChildren(backendSuite);//, dropcopyCaptureSuite);

		bs.registerConsoleObject(CON, new MsgConsole(connection));

		//Vortex Eye

		connection.addTopic(new MsgDirectTopicConfiguration("server.to.gui", vortexEyeHost, vortexEyePort));
		connection.addTopic(new MsgDirectTopicConfiguration("gui.to.server", vortexEyeHost, vortexEyePort));

		String guiPartition = "SNAPSHOT_PARTITION";

		ClassRoutingProcessor<Message> appRouter = new ClassRoutingProcessor<Message>(Message.class);
		appRouter.bindToPartition(guiPartition);
		BasicHttpServer httpServer = new BasicHttpServer();
		httpServer.addServerSocket(new HttpServerSocket(httpPort));
		httpServer.getAttributes().put("vortex_enabled", bs.getProperties().getOptional("vortex.enabled", true));
		httpServer.getHttpSessionManager().setSessionReaperPeriodMs(1000 * bs.getProperties().getOptional("web.session.check.period.seconds", 60));
		httpServer.getHttpSessionManager().setDefaultSessionTimeoutPeriodMs(1000 * bs.getProperties().getOptional("web.session.timeout.seconds", 300));
		final File store = props.getOptional("keystore.file", Caster_File.INSTANCE);
		if (store != null) {
			final String pass = props.getRequired("keystore.password");
			final int secureHttpPort = props.getRequired(HTTPS_PORT, Integer.class);
			HttpServerSocket ssocket = new HttpServerSocket(secureHttpPort, store, pass);
			httpServer.addServerSocket(ssocket);
		}
		HttpWebSuite suite = new HttpWebSuite(httpServer);
		MsgSuite snapshotSuite = new MsgSuite(guiPartition, connection, "server.to.gui", "gui.to.server", Bootstrap.getProcessUid());

		rs.addChildren(snapshotSuite, appRouter);

		rs.wire(backendSuite.newRequestOutputPort("Vortex", VortexEyeSnapshotRequest.class), snapshotSuite.outboundInputPort, true);
		rs.wire(backendSuite.newRequestOutputPort("AgentHistory", VortexEyeQueryHistoryRequest.class), snapshotSuite.outboundInputPort, true);
		rs.wire(backendSuite.newOutputPort("AgentUpdate", VortexEyeQueryHistoryRequest.class), snapshotSuite.outboundInputPort, true);
		rs.wire(snapshotSuite.inboundOutputPort, backendSuite.fromBackendInputPort, true);
		rs.wire(snapshotSuite.statusPort, backendSuite.fromBackendInputPort, true);
		c.getDispatchController().setDefaultFutureTimeoutMs(10000);

		rs.addChild(suite);
		PortalHttpStateCreator httpStateCreator = new PortalHttpStateCreator();
		VortexGlassPortletManagerFactory portletManagerFactory = new VortexGlassPortletManagerFactory(c.getTools());
		LoginHttpHandler loginHandler = suite.addHttpHandler("/login$", new LoginHttpHandler(httpStateCreator, portletManagerFactory));
		ResetPasswordHttpHandler resetHandler = suite.addHttpHandler("/reset1$", new ResetPasswordHttpHandler(httpStateCreator));
		ResetPassword2HttpHandler reset2Handler = suite.addHttpHandler("/reset2$", new ResetPassword2HttpHandler(httpStateCreator));

		//SSO
		final String[] ssoHosts = SH.trimStrings(SH.split(',', props.getOptional(OPTION_SSO_HOST)));
		final SsoPortletManagerFactory sso;
		if (AH.isEmpty(ssoHosts)) {
			System.out.println("Running in demo mode, please specify a sso host at property: " + OPTION_SSO_HOST);
			DemoSsoProcessor demoSsoProcessor = new DemoSsoProcessor();
			rs.addChild(demoSsoProcessor);
			loginHandler.setShouldEncode(false);
			rs.wire(suite.exposeOutputPort(loginHandler.loginPort), demoSsoProcessor, true);
			rs.wire(suite.exposeOutputPort(resetHandler.loginPort), demoSsoProcessor, true);
			rs.wire(suite.exposeOutputPort(reset2Handler.loginPort), demoSsoProcessor, true);
			sso = null;
		} else {
			final int[] ssoPorts = AH
					.toArrayInt(OH.castAll(CH.l(SH.trimStrings(SH.split(',', props.getRequired(OPTION_SSO_PORT)))), new ArrayList<Integer>(), Integer.class, true));
			MsgDirectTopicConfiguration ssoToClientConf = new MsgDirectTopicConfiguration("sso.to.client", ssoHosts, ssoPorts);
			MsgDirectTopicConfiguration clientToSsoConf = new MsgDirectTopicConfiguration("client.to.sso", ssoHosts, ssoPorts);
			MsgDirectTopicConfiguration ssoToBroadcastConf = new MsgDirectTopicConfiguration("sso.to.broadcast", ssoHosts, ssoPorts);

			final int[] sslPorts = AH.toArrayInt(OH.castAll(CH.l(SH.trimStrings(SH.split(',', props.getOptional(OPTION_SSO_SSL_PORT)))), new ArrayList<Integer>(), Integer.class,
					true));
			if (sslPorts.length > 0) {
				String sso_store = props.getRequired("sso.keystore.file");
				String pass = props.getRequired("sso.keystore.password");
				ssoConnConfig.setKeystore(new File(sso_store), pass);
				ssoToClientConf.setSslPorts(sslPorts);
				clientToSsoConf.setSslPorts(sslPorts);
				ssoToBroadcastConf.setSslPorts(sslPorts);
			}

			connection.addTopic(ssoToClientConf);
			connection.addTopic(clientToSsoConf);

			MsgSuite ssoMsgSuite = new MsgSuite("SSOMSGSUITE", connection, "sso.to.client", "client.to.sso", Bootstrap.getProcessUid());
			rs.addChildren(ssoMsgSuite);
			rs.wire(suite.exposeOutputPort(loginHandler.loginPort), ssoMsgSuite.outboundInputPort, true);
			rs.wire(suite.exposeOutputPort(resetHandler.loginPort), ssoMsgSuite.outboundInputPort, true);
			rs.wire(suite.exposeOutputPort(reset2Handler.loginPort), ssoMsgSuite.outboundInputPort, true);
			connection.addTopic(ssoToBroadcastConf);
			MsgSuite ssoBroadcastMsgSuite = new MsgSuite(guiPartition, connection, "sso.to.broadcast", null, Bootstrap.getProcessUid());
			rs.addChild(ssoBroadcastMsgSuite);
			rs.wire(backendSuite.newRequestOutputPort("SSO", SsoRequest.class), ssoMsgSuite.outboundInputPort, true);
			rs.wire(ssoMsgSuite.inboundOutputPort, backendSuite.fromBackendInputPort, true);
			rs.wire(ssoBroadcastMsgSuite.inboundOutputPort, backendSuite.fromBackendInputPort, true);
			sso = new SsoPortletManagerFactory(c.getTools());
			sso.setPortletBackend(backendSuite);
			portletManagerFactory.setSsoHandler(sso);
		}

		//	OmsPortalHttpHandler oms = new OmsPortalHttpHandler(null);
		VortexPortalHttpHandler vortexEye = new VortexPortalHttpHandler(c.getTools());//null, httpStateCreator);
		portletManagerFactory.setTestTrackHandler(vortexEye);
		PortalHttpHandler portalProcessor = suite.addHttpHandler("\\/portal\\/portal.ajax", new PortalHttpHandler(httpStateCreator));
		portletManagerFactory.setCallback("/portal/portal.ajax");
		portletManagerFactory.setPortletBackend(backendSuite);
		//	oms.setSessionNames(sessionNames);
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
