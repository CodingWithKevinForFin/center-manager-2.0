package com.f1.ami.amicommon.messaging;

public interface SimpleMessagingServerConnectionHandler {

	Object processRequest(Object request);

	boolean keepOpen();

	public void onClosed();

}
