package com.f1.ami.client;

import java.io.IOException;
import java.net.Socket;

public interface AmiClientAsServerFactory {

	void onClient(Socket socket, AmiClient client) throws IOException;//Usually, you'll calls client.start(socket,loginid,options) add a listener to the client at this point

}
