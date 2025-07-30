package com.f1.ami.amicommon.rest;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.f1.ami.amicommon.AmiCommonProperties;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.auth.AmiAuthResponse;
import com.f1.ami.web.auth.AmiAuthUser;
import com.f1.ami.web.auth.AmiAuthenticatorPlugin;
import com.f1.ami.web.auth.BasicAmiAuthResponse;
import com.f1.base.Password;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.ContainerTools;
import com.f1.container.impl.BasicContainer;
import com.f1.container.impl.SimpleContainerTools;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpServer;
import com.f1.http.HttpUtils;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.handler.FileHttpHandler;
import com.f1.http.handler.RedirectHandler;
import com.f1.http.impl.BasicHttpServer;
import com.f1.http.impl.HttpServerSocket;
import com.f1.http.impl.PortForwardHttpHandler;
import com.f1.utils.CH;
import com.f1.utils.ContentType;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.ServerSocketEntitlements;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.FastThreadPool;
import com.f1.utils.encrypt.EncoderUtils;

public class AmiRestServer extends AbstractHttpHandler {

	public static final String URL_FAVICON_ICO = "/favicon.ico";
	public static final String FILE_FAVICON_ICO = "portal/rsc/favicon.ico";
	public static final String SERVICE_ID = "AMI_REST_SERVER";

	public static AmiRestServer get(BasicContainer c) {
		return (AmiRestServer) c.getServices().getServiceNoThrow(SERVICE_ID);
	}

	private static final String REST_PREFIX = "/3forge_rest";
	private Map<String, AmiRestPlugin> handlers = new HashMap<String, AmiRestPlugin>();
	private boolean showErrors = true;
	private AmiAuthenticatorPlugin authenticator;
	private AmiRestSessionAuth sessionAuth;
	private boolean showEndpoints;
	private PropertyController props;
	private SimpleContainerTools tools;
	private HttpServer server;

	//	public static AmiRestServer start(ContainerBootstrap cb, HttpServer server, AmiRestSessionAuth auth) throws IOException {
	//		if (server == null) {
	//			server = new BasicHttpServer();
	//			final Integer httpPort = props.getOptional(AmiCommonProperties.PROPERTY_REST_HTTP_PORT, Caster_Integer.INSTANCE);
	//			final Integer httpsPort = props.getOptional(AmiCommonProperties.PROPERTY_REST_HTTPS_PORT, Caster_Integer.INSTANCE);
	//			if (httpPort == null && httpsPort == null)
	//				return null;
	//			initHttpServerSockets(tools, props, server);
	//			server.start();
	//		}
	//		return new AmiRestServer(tools, props, server, auth);
	//	}
	static public AmiRestServer create(ContainerBootstrap cb) {
		PropertyController props = cb.getProperties();
		final Integer httpPort = props.getOptional(AmiCommonProperties.PROPERTY_REST_HTTP_PORT, Caster_Integer.INSTANCE);
		final Integer httpsPort = props.getOptional(AmiCommonProperties.PROPERTY_REST_HTTPS_PORT, Caster_Integer.INSTANCE);
		final Boolean onWeb = props.getOptional(AmiCommonProperties.PROPERTY_REST_ON_WEB_PORT, Boolean.FALSE);
		if (httpPort == null && httpsPort == null && !onWeb.booleanValue())
			return null;
		return new AmiRestServer(cb);
	}
	private AmiRestServer(ContainerBootstrap cb) {
		this.tools = new SimpleContainerTools(cb.getProperties());
		this.props = cb.getProperties();
		this.authenticator = AmiUtils.loadAuthenticatorPlugin(tools, AmiCommonProperties.PROPERTY_REST_AUTH_PLUGIN_CLASS, "Ami REST Authenticator Plugin");
		this.showErrors = props.getOptional(AmiCommonProperties.PROPERTY_REST_SHOW_ERRORS, Boolean.TRUE);
		this.showEndpoints = props.getOptional(AmiCommonProperties.PROPERTY_REST_SHOW_ENDPOINTS, Boolean.TRUE);
		Map<String, AmiRestPlugin> plugins = AmiUtils.loadPlugins(tools, AmiCommonProperties.PROPERTY_REST_PLUGINS, "rest plugins", AmiRestPlugin.class);
		for (AmiRestPlugin i : plugins.values())
			handlers.put("/" + i.getEndpoint(), i);

	}

	public void setServer(HttpServer server) {
		this.server = server;
		server.addHttpHandlerStrict("/3forge_rest/*", this, true);
		server.addHttpHandlerStrict("/3forge_rest", this, true);
	}

	public void setRestSessionAuth(AmiRestSessionAuth auth) {
		this.sessionAuth = auth;
	}

	public static void initHttpServerSockets(ContainerTools tools, PropertyController props, HttpServer httpServer) throws IOException {
		final Integer httpPort = props.getOptional(AmiCommonProperties.PROPERTY_REST_HTTP_PORT, Caster_Integer.INSTANCE);
		final Integer httpsPort = props.getOptional(AmiCommonProperties.PROPERTY_REST_HTTPS_PORT, Caster_Integer.INSTANCE);
		if (httpPort == null && httpsPort == null)
			throw new RuntimeException("Must specify either: " + AmiCommonProperties.PROPERTY_REST_HTTP_PORT + " or " + AmiCommonProperties.PROPERTY_REST_HTTPS_PORT + " or "
					+ AmiCommonProperties.PROPERTY_REST_ON_WEB_PORT);
		final String httpsPortBindAddr = props.getOptional(AmiCommonProperties.PROPERTY_REST_HTTPS_PORT_BINDADDR, Caster_String.INSTANCE);
		final String httpPortBindAddr = props.getOptional(AmiCommonProperties.PROPERTY_REST_HTTP_PORT_BINDADDR, Caster_String.INSTANCE);

		final HttpServerSocket ssocket;
		if (httpsPort != null) {
			ssocket = createHttpsServerSocket(tools, props, httpsPortBindAddr, httpsPort);
			httpServer.addServerSocket(ssocket);
		} else
			ssocket = null;
		if (httpPort != null) {
			final ServerSocketEntitlements sse = AmiUtils.parseWhiteList(tools, props, AmiCommonProperties.PROPERTY_REST_HTTP_PORT_WHITELIST);
			HttpServerSocket socket = new HttpServerSocket(httpPortBindAddr, sse, httpPort);
			if (httpsPort == null)
				httpServer.addServerSocket(socket);
			else
				PortForwardHttpHandler.forward(socket, ssocket.getPort(), httpServer.getThreadPool());
		}
	}
	private static HttpServerSocket createHttpsServerSocket(ContainerTools tools, PropertyController props, String httpsPortBindAddr, Integer httpsPort) {
		final ServerSocketEntitlements sse = AmiUtils.parseWhiteList(tools, props, AmiCommonProperties.PROPERTY_REST_HTTPS_PORT_WHITELIST);
		final String pass = props.getRequired(AmiCommonProperties.PROPERTY_REST_HTTPS_KEYSTORE_PASSWORD);
		final String contents = props.getOptional(AmiCommonProperties.PROPERTY_REST_HTTPS_KEYSTORE_CONTENTS, String.class);
		final HttpServerSocket ssocket;
		if (SH.is(contents)) {
			byte[] bytes = EncoderUtils.decodeCert(contents);
			ssocket = new HttpServerSocket(httpsPortBindAddr, sse, httpsPort, bytes, pass);
		} else {
			final File store = props.getRequired(AmiCommonProperties.PROPERTY_REST_HTTPS_KEYSTORE_FILE, File.class);
			ssocket = new HttpServerSocket(httpsPortBindAddr, sse, httpsPort, store, pass);
		}
		return ssocket;
	}
	@Override
	public void handle(HttpRequestResponse req) throws IOException {
		super.handle(req);
		String requestUri = SH.stripPrefix(req.getRequestUri(), REST_PREFIX, false);
		if ("/".equals(requestUri) || "".contentEquals(requestUri)) {
			showEndpoints(new AmiRestRequest(req));
			req.setResponseType(HttpRequestResponse.HTTP_200_OK);
			return;
		}
		AmiRestPlugin handler = this.handlers.get(requestUri);
		if (handler == null) {
			if (SH.is(requestUri))
				req.getOutputStream().println("Endpoint not found: " + requestUri + "<P>");
			showEndpoints(new AmiRestRequest(req));
			req.setResponseType(HttpRequestResponse.HTTP_404_NOT_FOUND);
			return;
		}
		AmiAuthUser user = null;
		if (handler.requiresAuth()) {
			if (sessionAuth != null) {
				user = sessionAuth.getUser(req);
			}
			if (user == null) {
				String auth = req.getHeader().get("Authorization");
				if (auth == null) {
					req.putResponseHeader("WWW-Authenticate", "Basic");
					req.setResponseType(req.HTTP_401_UNAUTHORIZED);
					;
				} else {
					String afterFirst = SH.afterFirst(auth, "Basic ");
					String text = new String(EncoderUtils.decode64(afterFirst));
					String username = SH.beforeFirst(text, ':');
					Password password = new Password(SH.afterFirst(text, ':'));
					AmiAuthResponse aar = this.authenticator.authenticate(AmiAuthenticatorPlugin.NAMESPACE_AMIWEB_REST, req.getRemoteHost(), username, password.peekAndClear());
					switch (aar.getStatus()) {
						case AmiAuthResponse.STATUS_OKAY:
							break;
						case AmiAuthResponse.STATUS_SERVICE_DISABLED:
						case AmiAuthResponse.STATUS_USER_COUNT_EXCEEDED:
						case AmiAuthResponse.STATUS_ACCOUNT_LOCKED:
							req.getOutputStream().println(BasicAmiAuthResponse.toStringForStatus(aar.getStatus()));
							req.setResponseType(HttpRequestResponse.HTTP_403_FORBIDDEN);
							return;
						case AmiAuthResponse.STATUS_GENERAL_ERROR:
							req.getOutputStream().println(BasicAmiAuthResponse.toStringForStatus(aar.getStatus()));
							req.setResponseType(HttpRequestResponse.HTTP_500_SERVICE_ERROR);
							return;
						default:
							req.getOutputStream().println(BasicAmiAuthResponse.toStringForStatus(aar.getStatus()));
							req.setResponseType(HttpRequestResponse.HTTP_401_UNAUTHORIZED);
							return;
					}
					user = aar.getUser();
				}
			}
			if (user == null) {
				req.getOutputStream().println("Auth error");
				req.setResponseType(HttpRequestResponse.HTTP_401_UNAUTHORIZED);
				return;
			}

		}
		try {
			handler.handler(new AmiRestRequest(req), user);
			if (req.getResponseType() == null)
				req.setResponseType(HttpRequestResponse.HTTP_200_OK);
		} catch (Exception e) {
			req.getOutputStream().print("<B>UNKNOWN ERROR FROM AMI REST PLUGIN<P>");
			if (showErrors) {
				String str = SH.printStackTrace(e);
				str = HttpUtils.escapeHtmlNewLineToBr(str);
				req.getOutputStream().print(str);
			} else
				req.getOutputStream().println("Show Errors is disabled (" + AmiCommonProperties.PROPERTY_REST_SHOW_ERRORS + "=false)");

			req.setResponseType(HttpRequestResponse.HTTP_500_SERVICE_ERROR);
		}
	}
	private void showEndpoints(AmiRestRequest amiRestRequest) {
		if (amiRestRequest.isDisplayText()) {
			if (showEndpoints) {
				amiRestRequest.println("<BR>Available endpoints: ");
				for (String s : CH.sort(this.handlers.keySet()))
					amiRestRequest.println("<BR>&nbsp;&nbsp; <a href='" + REST_PREFIX + s + "'>" + REST_PREFIX + s + "</a>");
			} else
				amiRestRequest.println("Show endpoinds is disabled (" + AmiCommonProperties.PROPERTY_REST_SHOW_ENDPOINTS + "=false)");
			amiRestRequest.setContentType(ContentType.HTML);
		} else {

			Map o = new HashMap<String, String>();
			for (String s : CH.sort(this.handlers.keySet()))
				o.put(s, REST_PREFIX + "s");
			amiRestRequest.printJson(o);
		}
	}
	public <T extends AmiRestPlugin> T getPlugin(Class<T> clazz) {
		for (AmiRestPlugin i : this.handlers.values()) {
			if (clazz.isInstance(i))
				return clazz.cast(i);
		}
		return null;

	}
	public HttpServer getServer() {
		return this.server;
	}
	public void initHttpServer() throws IOException {
		BasicHttpServer server = new BasicHttpServer();
		FastThreadPool th = new FastThreadPool(8, "AMIREST");
		server.setThreadPool(th);
		server.addHttpHandlerStrict(URL_FAVICON_ICO, new FileHttpHandler(true, FILE_FAVICON_ICO, 100000), true);
		server.addHttpHandlerStrict("/", new RedirectHandler(REST_PREFIX), false);

		initHttpServerSockets(tools, props, server);
		setServer(server);
		th.start();
		server.start();
	}

}
