package com.f1.appia;

import com.f1.bootstrap.BootStrapper;
import com.javtech.appia.javatoolkit.middleware.Logger;

public class StartAppiaMain {

	public static void main(String a[]) {
		Logger.setLogClass(AppiaSpeedLogger.class);
		BootStrapper.main(a);
		Logger.getLogger("stupid.logger").warning("HERES A FAKE WARNING");
		System.err.println("TYPE OF LOGGER: " + Logger.getLogger("asdf"));
	}
}
