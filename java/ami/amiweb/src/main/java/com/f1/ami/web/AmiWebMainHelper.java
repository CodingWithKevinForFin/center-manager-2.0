package com.f1.ami.web;

import java.io.File;
import java.io.IOException;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.container.ContainerTools;
import com.f1.http.impl.BasicHttpServer;
import com.f1.http.impl.HttpServerSocket;
import com.f1.http.impl.PortForwardHttpHandler;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.ServerSocketEntitlements;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.encrypt.EncoderUtils;

public class AmiWebMainHelper {

	public static void initHttpServerSockets(ContainerTools tools, PropertyController props, BasicHttpServer httpServer) throws IOException {
		final Integer httpPort = props.getOptional(AmiWebProperties.PROPERTY_HTTP_PORT, Caster_Integer.INSTANCE);
		final String httpPortBindAddr = props.getOptional(AmiWebProperties.PROPERTY_HTTP_PORT_BINDADDR, Caster_String.INSTANCE);
		final Integer httpsPort = props.getOptional(AmiWebProperties.PROPERTY_HTTPS_PORT, Caster_Integer.INSTANCE);
		final String httpsPortBindAddr = props.getOptional(AmiWebProperties.PROPERTY_HTTPS_PORT_BINDADDR, Caster_String.INSTANCE);
		if (httpPort == null && httpsPort == null)
			throw new RuntimeException("Must specify either: " + AmiWebProperties.PROPERTY_HTTP_PORT + " or " + AmiWebProperties.PROPERTY_HTTPS_PORT);

		final HttpServerSocket ssocket;
		if (httpsPort != null) {
			ssocket = AmiWebMainHelper.createHttpsServerSocket(tools, props, httpsPortBindAddr, httpsPort);
			httpServer.addServerSocket(ssocket);
		} else
			ssocket = null;
		if (httpPort != null) {
			final ServerSocketEntitlements sse = AmiUtils.parseWhiteList(tools, props, AmiWebProperties.PROPERTY_HTTP_PORT_WHITELIST);
			final HttpServerSocket socket = new HttpServerSocket(httpPortBindAddr, sse, httpPort);
			if (httpsPort == null)
				httpServer.addServerSocket(socket);
			else
				PortForwardHttpHandler.forward(socket, ssocket.getPort(), httpServer.getThreadPool());
		}
	}

	private static HttpServerSocket createHttpsServerSocket(ContainerTools tools, PropertyController props, String httpsPortBindAddr, Integer httpsPort) {
		final ServerSocketEntitlements sse = AmiUtils.parseWhiteList(tools, props, AmiWebProperties.PROPERTY_HTTPS_PORT_WHITELIST);
		final String pass = props.getRequired(AmiWebProperties.PROPERTY_HTTPS_KEYSTORE_PASSWORD);
		final String contents = props.getOptional(AmiWebProperties.PROPERTY_HTTPS_KEYSTORE_CONTENTS, String.class);
		final HttpServerSocket ssocket;
		if (SH.is(contents)) {
			byte[] bytes = EncoderUtils.decodeCert(contents);
			ssocket = new HttpServerSocket(httpsPortBindAddr, sse, httpsPort, bytes, pass);
		} else {
			final File store = props.getRequired(AmiWebProperties.PROPERTY_HTTPS_KEYSTORE_FILE, File.class);
			ssocket = new HttpServerSocket(httpsPortBindAddr, sse, httpsPort, store, pass);
		}
		return ssocket;
	}
}
