package com.f1.console;

import java.io.InputStream;
import java.io.OutputStream;

public interface ConsoleServer {
	boolean shutdown();

	boolean isRunning();

	ConsoleManager getManager();

	ConsoleConnection createConnection(InputStream userTextStream, OutputStream resultStream);

}
