package com.f1.utils;

import java.net.ServerSocket;
import java.net.Socket;

public interface ServerSocketEntitlements {

	/**
	 * @param serverSocket
	 * @param clientSocket
	 * @return null if entitled, otherwise an error message to present in logs
	 */
	public String getNotEntitledMessage(ServerSocket serverSocket, Socket clientSocket);

}
