package com.f1.ami.amicommon.messaging;


public interface SimpleMessagingServerConnectionHandlerFactory {

	public SimpleMessagingServerConnectionHandler newHandler(String remoteAddress);
}
