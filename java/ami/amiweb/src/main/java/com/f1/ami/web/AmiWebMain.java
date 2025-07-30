package com.f1.ami.web;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.zip.Deflater;

import com.f1.ami.amicommon.AmiCenterDefinition;
import com.f1.ami.amicommon.AmiCommonProperties;
import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiEncrypter;
import com.f1.ami.amicommon.AmiProcessStatsLogger;
import com.f1.ami.amicommon.AmiScmPlugin;
import com.f1.ami.amicommon.AmiStartup;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.centerclient.AmiCenterClientInboundProcessor;
import com.f1.ami.amicommon.centerclient.AmiCenterClientOutboundProcessor;
import com.f1.ami.amicommon.centerclient.AmiCenterClientState;
import com.f1.ami.amicommon.centerclient.AmiCenterClientStats;
import com.f1.ami.amicommon.customobjects.AmiScriptClassPluginWrapper;
import com.f1.ami.amicommon.msg.AmiCenterGetResourceRequest;
import com.f1.ami.amicommon.msg.AmiCenterGetResourceResponse;
import com.f1.ami.amicommon.msg.AmiCenterRequest;
import com.f1.ami.amicommon.msg.AmiWebLoginRequest;
import com.f1.ami.amicommon.msg.AmiWebLoginResponse;
import com.f1.ami.amicommon.rest.AmiRestServer;
import com.f1.ami.client.AmiCenterClient;
import com.f1.ami.client.AmiCenterClientConnection;
import com.f1.ami.web.amiscript.AmiWebFileSystem;
import com.f1.ami.web.auth.AmiWebHttpStateCreator;
import com.f1.ami.web.auth.AmiWebLoginHttpHandler;
import com.f1.ami.web.headless.AmiWebHeadlessManager;
import com.f1.ami.web.headless.AmiWebModCountHttpHandler;
import com.f1.ami.web.headless.AmiWebOwnHeadlessHttpHandler;
import com.f1.ami.web.pages.AmiWebPages;
import com.f1.ami.web.rt.AmiWebRealtimeProcessorPlugin_BPIPE;
import com.f1.ami.web.rt.AmiWebRealtimeProcessorPlugin_Decorate;
import com.f1.ami.web.rt.AmiWebRealtimeProcessorPlugin_Limit;
import com.f1.base.Message;
import com.f1.bootstrap.Bootstrap;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.ContainerTools;
import com.f1.container.RequestOutputPort;
import com.f1.container.Suite;
import com.f1.container.impl.BasicContainer;
import com.f1.http.HttpHandler;
import com.f1.http.HttpSessionManager;
import com.f1.http.HttpUtils;
import com.f1.http.handler.FileHttpHandler;
import com.f1.http.handler.FileSystemHttpHandler;
import com.f1.http.handler.JspCompiler;
import com.f1.http.handler.JspFileHttpHandler;
import com.f1.http.handler.JspHttpHandler;
import com.f1.msgdirect.MsgDirectConnection;
import com.f1.msgdirect.MsgDirectConnectionConfiguration;
import com.f1.msgdirect.MsgDirectTopicConfiguration;
import com.f1.pdf.Caster_PdfText;
import com.f1.suite.utils.msg.MsgSuite;
import com.f1.suite.web.EndPortalHttpHandler;
import com.f1.suite.web.HttpWebSuite;
import com.f1.suite.web.PortalHttpHandler;
import com.f1.suite.web.StartPortalHttpHandler;
import com.f1.suite.web.portal.impl.BasicPortletBackendSuite;
import com.f1.suite.web.portal.impl.BasicPortletManager;
import com.f1.utils.CasterManager;
import com.f1.utils.Cksum;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.FastThreadPool;
import com.f1.utils.encrypt.EncoderUtils;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.stack.MutableCalcFrame;

public class AmiWebMain {

	public static final String DEFAULT_CSP = "img-src 'self' https://*.mapbox.com data: w3.org/svg/2000; default-src https://*.mapbox.com 'self' 'unsafe-inline' 'unsafe-eval' blob:;font-src 'self' data:";

	static {
		CasterManager.addCaster(Caster_PdfText.INSTANCE);
	}

	public static final String AMI_IMAGES_THREAD_POOL_NAME = "AMI_IMAGES";

	private static Logger log = LH.get();

	public static void main(String[] a) throws IOException, SQLException {
		ContainerBootstrap bs = new ContainerBootstrap(AmiWebMain.class, a);
		bs.setProperty("f1.appname", "AmiWeb");
		bs.setProperty("f1.logfilename", "AmiWeb");
		bs.setProperty("f1.autocoded.disabled", "false");
		bs.setProperty("f1.threadpool.agressive", "false");
		bs.setProperty("sso.key.portlet.layout", "portletlayout_ami");
		bs.setProperty("sso.namespace", "AMI");
		bs.setProperty("ami.style.files", "data/styles/*.amistyle.json");
		AmiStartup.startupAmi(bs, "ami_amiweb");
		main2(bs);
	}

	public static void main2(ContainerBootstrap bs) throws IOException {
		AmiWebMain main = AmiWebMain.create(bs);
		main.start();
	}

	private static AmiWebMain create(ContainerBootstrap bs) throws IOException {
		if (INSTANCE == null)
			INSTANCE = new AmiWebMain(bs);
		return INSTANCE;
	}

	private void start() throws IOException {
		AmiRestServer restServer = AmiRestServer.get(c);
		if (restServer != null) {
			boolean rowp = this.props.getOptional(AmiCommonProperties.PROPERTY_REST_ON_WEB_PORT, Boolean.FALSE);
			if (rowp) {
				restServer.setServer(this.httpServer);
				restServer.setRestSessionAuth(this.httpServer);
			}
		}
		bs.startupContainer(c);
		final AmiWebFile webResourcesRoot;
		try {
			webResourcesRoot = portletManagerFactory.getResourcesRoot(fileSystem);
		} catch (Exception e) {
			throw new RuntimeException("Error starting Web because Webmanager did not respond", e);
		}
		webResourcesRoot.mkdir();
		this.globalResourceCache.setRoot(webResourcesRoot);
		this.initLicenseMessage();
		this.initAmiStatsLogger();
		this.portletManagerFactory.initCloudDirectory(this.fileSystem);
		this.headlessManager.init();
	}

	private static AmiWebMain INSTANCE;

	private ContainerBootstrap bs;
	private PropertyController props;
	private BasicContainer c;
	private Suite rs;
	private ContainerTools tools;
	private BasicPortletBackendSuite backendSuite;
	private JspCompiler jspCompiler;

	private String httpPort;
	private String httpPortBindAddr;
	private Integer httpsPort;
	private String httpsPortBindAddr;

	private AmiWebHttpServer httpServer;
	private ConcurrentMap<String, Object> httpServerAttributes;
	private HttpWebSuite suite;

	private MutableCalcFrame amiScriptProperties;
	private AmiWebHttpStateCreator creator;
	private AmiWebPortletManagerFactory portletManagerFactory;
	private AmiWebGlobalResourceCache globalResourceCache;

	private AmiWebManagerClient wmc;
	private AmiCenterDefinition[] centerInstances;
	private AmiCenterClientState[] caches;
	private RequestOutputPort<AmiCenterGetResourceRequest, AmiCenterGetResourceResponse> resourceOutputPort;

	private AmiWebSSOPlugin ssoPlugin;
	private boolean usingSSO;
	private AmiWebLoginHttpHandler loginHandler;
	private HttpHandler logoutHttpHandler;
	private HttpHandler redirectLogoutHttpHandler;
	private HttpHandler ssoResponseHandler;

	private String loginLogo;
	private HttpHandler indexPageHttpHandler;
	private FileHttpHandler loginLogoHttpHandler;

	private PortalHttpHandler portalProcessor;
	private AmiWebRunHttpHandler runHandler;
	private AmiWebDynmicImagehandler dynamicImageHandler;
	private AmiWebOwnHeadlessHttpHandler ownHeadlessHandler;
	private StartPortalHttpHandler sph;
	private EndPortalHttpHandler endPortalHandler;
	private AmiWebModCountHttpHandler modCountHandler;
	private AmiWebResourceHttpHandler webResourceHandler;

	private AmiWebHeadlessManager headlessManager;

	private AmiWebFileSystem fileSystem;

	private AmiWebMain(ContainerBootstrap bs) throws IOException {
		if (System.getProperty("java.awt.headless") == null)
			System.setProperty("java.awt.headless", "true");
		this.bs = bs;
		this.props = bs.getProperties();

		// Prepare container
		this.c = new BasicContainer();
		this.c.setName("AmiWeb");
		this.c.getDispatchController().setDefaultFutureTimeoutMs(10000);
		this.bs.prepareContainer(c);

		this.rs = c.getRootSuite();
		this.tools = c.getTools();
		this.backendSuite = new BasicPortletBackendSuite();
		this.rs.addChildren(backendSuite);
		this.jspCompiler = new JspCompiler(bs.getCompiler());

		this.initAmiScriptProperties();
		this.initWebManager();
		this.initHttpServer();
		this.initImageExecutor();
		this.initAmiCenterConnections();
		this.initAmiPortletsManagerFactory();
		this.initAuthSSO();
		this.initWebPlugins();
		this.initFileSystemCache();
		this.initHttpHandlers();
		this.addHttpHandlers();
		this.initFontsManager();
		this.initAmiWebConsole();
		this.initHeadlessManager();
	}

	private void initHttpHandlers() {
		if (SH.is(this.loginLogo))
			this.loginLogoHttpHandler = new FileHttpHandler(false, loginLogo, 1000);
		runHandler = new AmiWebRunHttpHandler(null, tools);
		dynamicImageHandler = new AmiWebDynmicImagehandler(creator);
		ownHeadlessHandler = new AmiWebOwnHeadlessHttpHandler();
		sph = new StartPortalHttpHandler(creator, portletManagerFactory, AmiWebPages.URL_PORTALS);
		sph.setLogoutUrl(AmiWebPages.URL_LOGOUT);
		endPortalHandler = new EndPortalHttpHandler(AmiWebPages.URL_PORTALS);
		modCountHandler = new AmiWebModCountHttpHandler();
		webResourceHandler = new AmiWebResourceHttpHandler(resourceOutputPort, globalResourceCache);
	}

	private void addHttpHandlers() {
		if (this.loginLogoHttpHandler != null)
			suite.addHttpHandlerStrict(AmiWebPages.URL_CUSTOM_LOGO, this.loginLogoHttpHandler);

		suite.addHttpHandlerStrict(AmiWebPages.URL_FAVICON_ICO, new FileHttpHandler(true, AmiWebPages.FILE_FAVICON_ICO, 100000));
		suite.addHttpHandlerStrict(AmiWebPages.URL_RUN, runHandler);
		suite.addHttpHandlerStrict(AmiWebPages.URL_DYNAMIC_IMAGE, dynamicImageHandler);
		suite.addHttpHandlerStrict(AmiWebPages.URL_OWN_HEADLESS, ownHeadlessHandler);
		suite.addHttpHandlerStrict(BasicPortletManager.URL_AJAXL, portalProcessor);
		suite.addHttpHandlerStrict(BasicPortletManager.URL_START, sph);
		suite.addHttpHandlerStrict(BasicPortletManager.URL_END, endPortalHandler);
		suite.addHttpHandlerStrict(AmiWebPages.URL_MODCOUNT, modCountHandler);
		suite.addHttpHandlerStrict(AmiWebPages.URL_RESOURCES + "/*", webResourceHandler);
		if (!usingSSO) {
			suite.addHttpHandlerStrict(AmiWebPages.URL_LOGOUT, logoutHttpHandler);
			suite.addHttpHandlerStrict(AmiWebPages.URL_HELLO, indexPageHttpHandler);
			suite.addHttpHandlerStrict(AmiWebPages.URL_LOGIN, loginHandler);
			suite.addHttpHandlerStrict(AmiWebPages.URL_GOODBYE, new JspFileHttpHandler(true, new File(AmiWebPages.PAGE_GOODBYE), 1000, jspCompiler));
		} else if (usingSSO) {
			String expectedResponsePath = ssoPlugin.getExpectedResponsePath();
			String logoutRedirectPath = ssoPlugin.getLogoutRedirectPath();

			suite.addHttpHandlerStrict(AmiWebPages.URL_LOGOUT, logoutHttpHandler);
			if (logoutRedirectPath != null) {
				suite.addHttpHandlerStrict(prefixForwardSlash(logoutRedirectPath) + "/*", redirectLogoutHttpHandler);
				suite.addHttpHandlerStrict(AmiWebPages.URL_GOODBYE, logoutHttpHandler);
			} else {
				suite.addHttpHandlerStrict(AmiWebPages.URL_GOODBYE, new JspFileHttpHandler(true, new File(AmiWebPages.PAGE_GOODBYE), 1000, jspCompiler));
			}
			suite.addHttpHandlerStrict(prefixForwardSlash(expectedResponsePath), ssoResponseHandler);
			suite.addHttpHandlerStrict(AmiWebPages.URL_HELLO, indexPageHttpHandler);
		}
		suite.addHttpHandlerStrict(BasicPortletManager.URL_PORTAL, new JspFileHttpHandler(true, new File(AmiWebPages.PAGE_PORTAL), 1000, jspCompiler));
		suite.addHttpHandlerStrict(AmiWebPages.URL_PORTALS, new JspFileHttpHandler(true, new File(AmiWebPages.PAGE_PORTALS), 1000, jspCompiler));
		suite.addHttpHandlerStrict(BasicPortletManager.URL_CUSTOM_CSS, new FileHttpHandler(true, AmiWebPages.PAGE_CUSTOM_CSS, 1000));
		mapDirectory(suite, AmiWebPages.DIR_AMIWEB, AmiWebPages.DIR_AMIWEB, AmiWebPages.URL_HELLO, jspCompiler);
		mapDirectory(suite, AmiWebPages.DIR_PORTAL, AmiWebPages.DIR_PORTAL, AmiWebPages.URL_HELLO, jspCompiler);
		mapDirectory(suite, "", AmiWebPages.DIR_PORTAL, AmiWebPages.URL_HELLO, jspCompiler);
		//		suite.addHttpHandlerStrict("*", new FileSystemHttpHandler(true, new File(DIR_AMIWEB), "/", 1000, AmiWebPages.URL_HELLO, true));
	}

	private void initFontsManager() {
		AmiWebFontsManager fonts = new AmiWebFontsManager(tools);
		portletManagerFactory.setFontManager(fonts);
	}
	private void initAmiWebConsole() {
		bs.registerConsoleObject("amiWebServer", new AmiWebConsole(httpServer, c.getPartitionController()));
	}
	private void initAmiStatsLogger() {
		AmiProcessStatsLogger.INSTANCE.registerBootstrap(c);
		AmiProcessStatsLogger.INSTANCE.addLogger(new AmiWebHttpServerLogger(httpServer, caches));
	}
	private void initHeadlessManager() throws IOException {
		this.headlessManager = new AmiWebHeadlessManager(httpServer, portletManagerFactory, creator);
		c.getServices().putService(AmiWebHeadlessManager.SERVICE_ID, headlessManager);
	}
	private void initFileSystemCache() {
		this.fileSystem = portletManagerFactory.createFileSystem();
		this.globalResourceCache = new AmiWebGlobalResourceCache(props, fileSystem);
		c.getServices().putService(AmiWebGlobalResourceCache.SERVICE_ID, globalResourceCache);
	}

	private void initAmiScriptProperties() {
		PropertyController properties = props.getSubPropertyController(AmiWebProperties.PREFIX_AMISCRIPT_VARIABLE);
		this.amiScriptProperties = new MutableCalcFrame();
		for (String varName : properties.getKeys()) {
			String value = properties.getRequired(varName);
			Tuple2<Class<?>, Object> r = AmiUtils.toAmiscriptVariable(value, "Property amiscript.variable.", varName);
			if (!AmiUtils.isValidVariableName(varName, false, false))
				throw new RuntimeException("Invalid variable name: " + varName);
			amiScriptProperties.putTypeValue(varName, r.getA(), r.getB());
		}
	}

	private void initWebManager() throws IOException {
		MsgDirectConnectionConfiguration ssoConnConfig = new MsgDirectConnectionConfiguration("ssoConnection");
		final String amiWebManagerHost = props.getOptional(AmiWebProperties.PROPERTY_AMI_WEBMANAGER_HOST);
		if (amiWebManagerHost != null) {
			final int amiWebManagerPort = props.getOptional(AmiWebProperties.PROPERTY_AMI_WEBMANAGER_PORT, 3260);
			final long timeout = SH.parseDurationTo(props.getOptional(AmiWebProperties.PROPERTY_AMI_WEBMANAGER_TIMEOUT, "10 SECONDS"), TimeUnit.MILLISECONDS);

			String sslKeyFileName = props.getOptional(AmiWebProperties.PROPERTY_AMI_WEBMANAGER_SSL_KEYSTORE_FILE);
			String sslKeyText = props.getOptional(AmiWebProperties.PROPERTY_AMI_WEBMANAGER_SSL_KEYSTORE_TEXT_BASE64);
			String sslKeyPassword = props.getOptional(AmiWebProperties.PROPERTY_AMI_WEBMANAGER_SSL_KEYSTORE_PASSWORD);

			byte[] sslKeyData;
			File sslKeyFile;
			if (SH.is(sslKeyText)) {
				if (SH.is(sslKeyFileName))
					throw new RuntimeException(AmiWebProperties.PROPERTY_AMI_WEBMANAGER_SSL_KEYSTORE_TEXT_BASE64 + " and "
							+ AmiWebProperties.PROPERTY_AMI_WEBMANAGER_SSL_KEYSTORE_FILE + " are mutually exclusive");
				sslKeyData = EncoderUtils.decodeCert(sslKeyText);
				sslKeyFile = null;
				ssoConnConfig.setKeystore(sslKeyData, sslKeyPassword);
				ssoConnConfig.setForceSsl(true);
			} else if (SH.is(sslKeyFileName)) {
				sslKeyData = null;
				sslKeyFile = new File(sslKeyFileName);
				ssoConnConfig.setKeystore(sslKeyFile, sslKeyPassword);
				ssoConnConfig.setForceSsl(true);
			} else {
				sslKeyData = null;
				sslKeyFile = null;
			}

			MsgDirectConnection wmconnection = new MsgDirectConnection(ssoConnConfig);
			wmconnection.addTopic(new MsgDirectTopicConfiguration("webmanager.to.web", amiWebManagerHost, amiWebManagerPort));
			wmconnection.addTopic(new MsgDirectTopicConfiguration("web.to.webmanager", amiWebManagerHost, amiWebManagerPort));
			MsgSuite webManagerSuite = new MsgSuite("WEBMANAGERCLIENT", wmconnection, "webmanager.to.web", "web.to.webmanager", Bootstrap.getProcessUid());
			rs.addChildren(webManagerSuite);
			RequestOutputPort<Message, Message> wmport = webManagerSuite.exposeInputPortAsOutput(webManagerSuite.getOutboundRequestInputPort(), true);
			this.wmc = new AmiWebManagerClient(wmport, amiWebManagerHost + ":" + amiWebManagerPort, timeout);
		} else
			this.wmc = null;

	}

	private void initImageExecutor() throws IOException {
		int imageThreads = props.getOptional(AmiWebProperties.PROPERTY_AMI_CHART_THREADING_THREAD_POOL_SIZE, 100);
		FastThreadPool executor = new FastThreadPool(imageThreads, "IMAGE");
		c.getThreadPoolController().putThreadPool(AmiWebMain.AMI_IMAGES_THREAD_POOL_NAME, executor);
		executor.start();
	}

	private void initHttpServerTermsConditions() throws IOException {
		String termsAndConditionsFileName = props.getOptional(AmiWebProperties.PROPERTY_AMI_LOGIN_PAGE_TERMS_AND_CONDITIONS_FILE);
		if (SH.is(termsAndConditionsFileName)) {
			File tac = new File(termsAndConditionsFileName);
			IOH.ensureReadable(tac);
			String terms = IOH.readText(tac);
			httpServerAttributes.put("termsText", terms);
			String tacsig = AmiUtils.toValidVarName(tac.getName()) + "_" + Cksum.cksum(terms.getBytes());
			httpServerAttributes.put("termsSignature", tacsig);
			LH.info(log, "TERMS AND CONDITIONS FILE '", IOH.getFullPath(tac), "' SIGNATURE: '", tacsig, "', CONTENTS:", SH.NEWLINE, "========", SH.NEWLINE, terms, SH.NEWLINE,
					"========");
		}
	}

	private void initHttpServer() throws IOException {
		this.httpServer = new AmiWebHttpServer();
		this.httpServerAttributes = httpServer.getAttributes();

		/* 
		 * Terms, Conditions & License
		 */
		this.initHttpServerTermsConditions();

		HttpSessionManager httpSessionManager = httpServer.getHttpSessionManager();

		/*
		 * Get Properties
		 */
		final String httpPort = props.getOptional(AmiWebProperties.PROPERTY_HTTP_PORT, Caster_String.INSTANCE);
		int httpThreads = props.getOptional(AmiWebProperties.PROPERTY_AMI_WEB_HTTP_CONNECTIONS_MAX, 100);
		int httpTimeout = props.getOptional(AmiWebProperties.PROPERTY_AMI_WEB_HTTP_CONNECTIONS_TIMEOUT_MS, 300 * 1000);
		String hostname = props.getOptional(AmiWebProperties.PROPERTY_HTTP_HOSTNAME);
		String httpMethods = props.getOptional(AmiWebProperties.PROPERTY_HTTP_ALLOW_METHODS, "GET,POST");
		String contentSecurityPolicy = props.getOptional(AmiWebProperties.PROPERTY_AMI_CONTENT_SECURITY_POLICY, AmiWebMain.DEFAULT_CSP);
		String messageMaxSessions = props.getOptional(AmiWebProperties.PROPERTY_AMI_WEB_MESSAGE_MAX_SESSIONS, "Session limit reached, please choose a current session below");
		String sameSite = props.getOptional(AmiWebProperties.PROPERTY_AMI_ALLOW_SAME_SITE);
		boolean allowSiteFramed = props.getOptional(AmiWebProperties.PROPERTY_AMI_ALLOW_SITE_FRAMED, Boolean.TRUE);
		String loginTitleSuffix = props.getOptional(AmiWebProperties.PROPERTY_AMI_LOGIN_PAGE_TITLE, "");
		String defaultUser = props.getOptional(AmiWebProperties.PROPERTY_AMI_LOGIN_DEFAULT_USER, "");
		String defaultPass = props.getOptional(AmiWebProperties.PROPERTY_AMI_LOGIN_DEFAULT_PASS, "");
		boolean loginPageAnimated = props.getOptional(AmiWebProperties.PROPERTY_AMI_LOGIN_PAGE_ANIMATED, Boolean.TRUE);
		String cookieName = props.getOptional(AmiWebProperties.PROPERTY_AMI_SESSION_COOKIE_NAME);
		int amiSessionCheckPeriod = props.getOptional(AmiWebProperties.PROPERTY_AMI_SESSION_CHECK_PERIOD_SECONDS, 60);
		long amiSessionTimeout = props.getOptional(AmiWebProperties.PROPERTY_AMI_SESSION_TIMEOUT_SECONDS, 300);
		String splashScreenInfoHtml = props.getOptional(AmiWebProperties.PROPERTY_AMI_WEB_SPLASHSCREEN_INFO_HTML);
		this.loginLogo = props.getOptional(AmiWebProperties.PROPERTY_AMI_WEB_LOGIN_PAGE_LOGO);

		//props.getOptional(AmiWebProperties.PROPERTY_AMI_WEB_LOGOUT_PAGE, DEFAULT_LOGOUT_PAGE);

		httpServer.setThreadPool(new FastThreadPool(httpThreads, "http"));
		httpServer.setConnectionTimeout(httpTimeout);

		// HTTP Security Policy
		AmiWebHttpServerSecurityPolicy httpSecurityPolicy = new AmiWebHttpServerSecurityPolicy(hostname, httpMethods);
		httpServer.setSecurityPolicy(httpSecurityPolicy);

		// HTTP CSP
		if (SH.is(contentSecurityPolicy))
			httpServer.putDefaultResponseHeader(HttpUtils.CONTENT_SECURITY_POLICY, contentSecurityPolicy);

		// HTTP Same Site
		if (!allowSiteFramed) {
			httpServer.putGlobalResponseHeader("X-Frame-Options", "DENY");
			if (sameSite == null)
				sameSite = "Strict";
		} else {
			if (sameSite == null && httpPort == null) // TODO: Http port why not https port
				sameSite = "None";
		}

		httpServer.putGlobalResponseHeader("X-Content-Type-Options", "nosniff");
		httpServer.putGlobalResponseHeader("X-XSS-Protection", "1; mode=block");

		/*
		 * Http Server attributes
		 */
		httpServerAttributes.put(HttpWebSuite.ATTRIBUTE_LOGGED_OUT_URL, AmiWebPages.URL_GOODBYE);

		if (SH.is(loginTitleSuffix))
			loginTitleSuffix = " - " + loginTitleSuffix.trim();
		else
			loginTitleSuffix = " AMI";
		httpServerAttributes.put("webLoginTitleSuffix", loginTitleSuffix);
		httpServerAttributes.put("webLoginDefaultUser", defaultUser);
		httpServerAttributes.put("webLoginDefaultPass", defaultPass); // Why ???
		httpServerAttributes.put("messageMaxSessions", messageMaxSessions);
		httpServerAttributes.put("login_animated", loginPageAnimated);
		httpServerAttributes.put("loginPageLogo", loginLogo);
		if (SH.is(splashScreenInfoHtml))
			httpServerAttributes.put("splashScreenInfoHtml", splashScreenInfoHtml);
		httpServerAttributes.put("indexPage", AmiWebPages.URL_HELLO);
		/*
		 * Http Server Config
		 */
		if (sameSite != null)
			httpSessionManager.setAdditionalCookieOptions("SameSite=" + sameSite + "; Secure");
		if (SH.is(cookieName))
			httpSessionManager.setCookieName(cookieName);

		httpSessionManager.setSessionReaperPeriodMs(1000 * amiSessionCheckPeriod);
		httpSessionManager.setDefaultSessionTimeoutPeriodMs(1000 * amiSessionTimeout);

		// Init HttpServer Sockets and WebSuite
		AmiWebMainHelper.initHttpServerSockets(tools, props, httpServer);
		this.suite = new HttpWebSuite(httpServer);
		rs.addChild(suite);

	}

	private void initLicenseMessage() {
		String messageLicenseExpires = props.getOptional(AmiWebProperties.PROPERTY_AMI_WEB_MESSAGE_LICENSE_EXPIRES, "WARNING: License Expires soon: ${LICENSE_EXPIRES_DATE}");
		messageLicenseExpires = SH.replaceAll(messageLicenseExpires, "${LICENSE_EXPIRES_DATE}", (String) httpServerAttributes.get(HttpWebSuite.ATTRIBUTE_F1_LICENSE_TERM_TIME));
		httpServerAttributes.put("messageLicenseExpires", messageLicenseExpires);
	}

	private void initPortalProcessorAndDebugging() {
		int compressionLevel = tools.getOptional(AmiWebProperties.PROPERTY_AMI_AJAX_COMPRESSION_LEVEL, Deflater.DEFAULT_COMPRESSION);
		int compressionMinSize = tools.getOptional(AmiWebProperties.PROPERTY_AMI_AJAX_COMPRESSION_MIN_SIZE_BYTES, PortalHttpHandler.DEFAULT_COMPRESSION_LEVEL);
		String debug = props.getOptionalEnum(AmiWebProperties.PROPERTY_AMI_WEB_HTTP_DEBUG, "off", "on", "verbose", "all");

		if (compressionLevel != Deflater.DEFAULT_COMPRESSION)
			OH.assertBetween(compressionLevel, 0, 9, AmiWebProperties.PROPERTY_AMI_AJAX_COMPRESSION_LEVEL);

		this.portalProcessor = new PortalHttpHandler(creator, compressionLevel, compressionMinSize);
		httpServer.getHttpSessionManager().addListener(portalProcessor);
		if ("on".equals(debug))
			portalProcessor.setDebug(true);
		else if ("verbose".equals(debug)) {
			httpServer.setDebugPolling(false);
			httpServer.setDebugging(true, props.getOptional(AmiWebProperties.PROPERTY_AMI_WEB_HTTP_DEBUG_MAX_BYTES, 10240));
		} else if ("all".equals(debug)) {
			httpServer.setDebugPolling(true);
			httpServer.setDebugging(true, props.getOptional(AmiWebProperties.PROPERTY_AMI_WEB_HTTP_DEBUG_MAX_BYTES, 10240));
		}

		httpServer.setHttpResponseWarnMs(props.getOptional(AmiWebProperties.PROPERTY_AMI_WEB_HTTP_SLOW_RESPONSE_WARN_MS, 5000));
		httpServer.setHttpResponseWarnLogRequestSize(props.getOptional(AmiWebProperties.PROPERTY_AMI_WEB_HTTP_SLOW_RESPONSE_WARN_LOG_REQUEST_SIZE, 1024), "password=");
	}

	private void initWebPlugins() {
		Map<String, AmiScriptClassPluginWrapper> amiScriptPlugins = new HashMap<String, AmiScriptClassPluginWrapper>();
		AmiWebPluginHelper.initAmiScriptPlugins(tools, amiScriptPlugins);

		Map<String, AmiScmPlugin> amiScmPlugins = new HashMap<String, AmiScmPlugin>();
		AmiWebPluginHelper.initScmPlugins(tools, amiScmPlugins);

		Map<String, AmiWebGuiServicePlugin> guiServicePlugins = new HashMap<String, AmiWebGuiServicePlugin>();
		AmiWebPluginHelper.initGuiServicePlugins(tools, guiServicePlugins);

		Map<String, AmiWebRealtimeProcessorPlugin> rtProcessorPlugins = new HashMap<String, AmiWebRealtimeProcessorPlugin>();
		rtProcessorPlugins.put(AmiWebRealtimeProcessorPlugin_Decorate.PLUGIN_ID, new AmiWebRealtimeProcessorPlugin_Decorate());
		rtProcessorPlugins.put(AmiWebRealtimeProcessorPlugin_Limit.PLUGIN_ID, new AmiWebRealtimeProcessorPlugin_Limit());
		rtProcessorPlugins.put(AmiWebRealtimeProcessorPlugin_BPIPE.PLUGIN_ID, new AmiWebRealtimeProcessorPlugin_BPIPE());
		AmiWebPluginHelper.initRealtimeProcessorsPlugin(tools, rtProcessorPlugins);

		portletManagerFactory.setDataFilterPlugin(AmiWebPluginHelper.initAmiDataFilterPlugin(tools));
		portletManagerFactory.setUserPreferencesPlugin(AmiWebPluginHelper.initAmiUserPreferencesStoragePlugin(tools));

		Map<String, AmiWebPanelPlugin> plugins = new TreeMap<String, AmiWebPanelPlugin>();
		AmiWebPluginHelper.initAmiPlugins(tools, plugins);

		this.initPortalProcessorAndDebugging();
		portletManagerFactory.setAmiScriptCustomClasslugins(amiScriptPlugins);
		portletManagerFactory.setAmiRealtimeProcessorPlugins(rtProcessorPlugins);

		AmiEncrypter encrypter = AmiUtils.initCertificate(tools);
		portletManagerFactory.setEncrypter(encrypter);
		c.getServices().putService(AmiConsts.SERVICE_ENCRYPTER, encrypter);

		AmiCenterClientStats webStats = new AmiCenterClientStats(tools);
		portletManagerFactory.setScmPlugins(amiScmPlugins);
		portletManagerFactory.setGuiServicePlugins(guiServicePlugins);
		portletManagerFactory.setWebStats(webStats);
		portletManagerFactory.addPlugins(plugins.values());

		portletManagerFactory.setPortletBackend(backendSuite);
	}

	private void initAmiPortletsManagerFactory() throws IOException {
		/*
		 * Requirements:
		 * amiCenterConnections
		 * amiWebManager 
		 * httpServer - sessionManager - default formatter
		 */
		int configRequestTimeoutSeconds = tools.getOptional(AmiWebProperties.PROPERTY_AMI_REQUEST_TIMEOUT_SECONDS, 180);
		this.creator = new AmiWebHttpStateCreator(configRequestTimeoutSeconds);
		this.portletManagerFactory = new AmiWebPortletManagerFactory(tools, wmc, httpServer.getHttpSessionManager().getDefaultFormatter());
		this.portletManagerFactory.setCenterDefinitions(centerInstances);
		this.portletManagerFactory.setCreator(creator);
		this.portletManagerFactory.setAmiScriptVariables(amiScriptProperties);

	}

	private void initAmiCenterConnections() throws IOException {
		byte n = 0;
		this.centerInstances = AmiCenterDefinition.parse(tools);
		this.caches = new AmiCenterClientState[centerInstances.length];
		int maxBatchSize = tools.getOptional(AmiWebProperties.PROPERTY_AMI_WEB_MAX_ROWS_PER_SNAPSHOT, 100000);

		Set<String> precachedTypes = new HashSet<String>(AmiWebSystemObjectsManager.INTERESTED_TYPES);
		for (String s : SH.splitToList(",", tools.getOptional(AmiWebProperties.PROPERTY_AMI_WEB_PRECACHED_TABLES, ""))) {
			s = SH.trim(s);
			if (!precachedTypes.add(s))
				throw new RuntimeException("Duplicate table defined in " + AmiWebProperties.PROPERTY_AMI_WEB_PRECACHED_TABLES + ": " + s);
		}

		AmiCenterClient acc = new AmiCenterClient("!!AMI_WEB!!", c);
		for (AmiCenterDefinition center : centerInstances) {
			AmiWebGlobalCache cache2 = new AmiWebGlobalCache();
			AmiCenterClientConnection connection = acc.connect(center, cache2);
			connection.subscribe(precachedTypes);
			AmiCenterClientOutboundProcessor cacheOutboundProcessor = connection.getCacheOutboundProcessor();
			AmiCenterClientInboundProcessor cacheInboundProcessor = connection.getCacheInboundProcessor();
			caches[n++] = connection.getCache();
			rs.wire(backendSuite.newRequestOutputPort("AmiCenter" + center.getId(), AmiCenterRequest.class), cacheOutboundProcessor, true);
			rs.wire(cacheInboundProcessor.toUsers, backendSuite.fromBackendInputPort, true);
			rs.wire(cacheOutboundProcessor.toUsers, backendSuite.fromBackendInputPort, true);
			if (resourceOutputPort == null)
				resourceOutputPort = connection.getOutboundRequestPort();
		}
	}

	private void initAuthSSO() {
		this.ssoPlugin = AmiUtils.loadPlugin(tools, AmiWebProperties.PROPERTY_SSO_PLUGIN_CLASS, "SSO", AmiWebSSOPlugin.class);
		AmiWebEntitlementsPlugin entitlementsPlugin = AmiUtils.loadPlugin(tools, AmiWebProperties.PROPERTY_ENTITLEMENTS_PLUGIN_CLASS, "ENTITLEMENTS",
				AmiWebEntitlementsPlugin.class);
		if (ssoPlugin == null)
			ssoPlugin = AmiUtils.loadPlugin(tools, AmiWebProperties.PROPERTY_SAML_PLUGIN_CLASS, "SAML", AmiWebSamlPlugin.class);

		this.usingSSO = ssoPlugin != null;

		long loginTimeoutMs = props.getOptional(AmiWebProperties.PROPERTY_AMI_AUTH_TIMEOUT_MS, 5000);
		RequestOutputPort<AmiWebLoginRequest, AmiWebLoginResponse> loginPort = suite.newRequestOutputPort(AmiWebLoginRequest.class, AmiWebLoginResponse.class);

		AmiWebAuthProcessor demoSsoProcessor = new AmiWebAuthProcessor();
		rs.addChild(demoSsoProcessor);
		this.loginHandler = new AmiWebLoginHttpHandler(loginTimeoutMs, loginPort);
		if (!usingSSO) {
			rs.wire(suite.exposeOutputPort(loginHandler.loginPort), demoSsoProcessor, true);
			this.logoutHttpHandler = new AmiWebLogoutHttpHandler();
			this.loginHandler.setIndexPage(AmiWebPages.URL_HELLO);
			this.indexPageHttpHandler = new JspFileHttpHandler(true, new File(AmiWebPages.PAGE_HELLO), 1000, jspCompiler);
		} else if (usingSSO) {
			long retryMs = props.getOptional(AmiWebProperties.PROPERTY_AMI_AUTH_CONCURRENT_RETRY_MS, 500);
			String expectedResponsePath = ssoPlugin.getExpectedResponsePath();
			String logoutRedirectPath = ssoPlugin.getLogoutRedirectPath();
			LH.info(log, "SAML Login Response expected at: '", expectedResponsePath, "', sso logout redirect path expected at ", SH.quoteOrNull(logoutRedirectPath));
			if (logoutRedirectPath == null) {
				LH.info(log, "Single Logout is disabled, since sso redirect path is null");
				this.logoutHttpHandler = new AmiWebLogoutHttpHandler();
			} else {
				this.logoutHttpHandler = new AmiWebSSOLogoutHttpHandler(ssoPlugin);
				this.redirectLogoutHttpHandler = new AmiWebLogoutHttpHandler();
			}
			this.ssoResponseHandler = new AmiWebSSOResponseHandler(creator, c, portletManagerFactory, ssoPlugin, entitlementsPlugin, props);
			this.indexPageHttpHandler = new AmiWebSSORequestHandler(ssoPlugin, retryMs);
		}
	}

	private static void mapDirectory(HttpWebSuite suite, String dir, String dir2, String targetPage, JspCompiler jspCompiler) {
		suite.addHttpHandlerStrict(prefixForwardSlash(dir + "/*.js*"), new JspHttpHandler(true, new File(dir2), prefixForwardSlash(dir), 1000, targetPage, true, jspCompiler));
		suite.addHttpHandlerStrict(prefixForwardSlash(dir + "/*"), new FileSystemHttpHandler(true, new File(dir2), prefixForwardSlash(dir), 1000, targetPage, true));

	}

	private static String prefixForwardSlash(String s) {
		return SH.startsWith(s, '/') ? s : ('/' + s);
	}
}
