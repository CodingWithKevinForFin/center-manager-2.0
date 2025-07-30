package com.f1.tcartsim.verify.main;

import java.io.IOException;
import java.util.logging.Logger;

import com.f1.bootstrap.Bootstrap;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;

public class TcartVerifyMain {
	private static final Logger log = LH.get();
	public static final String SRC_MAIN_CONFIG = "./src/main/config";

	public static void main(String a[]) throws IOException {
		Bootstrap bs = new Bootstrap(TcartVerifyMain.class, a);
		bs.setConfigDirProperty(SRC_MAIN_CONFIG);
		bs.setLoggingOverrideProperty("info");
		PropertyController p = bs.getProperties();

		AnvilData abtest = new AnvilData(p);
		bs.startup();
		bs.registerConsoleObject("abtest", abtest);
		abtest.run();
		bs.keepAlive();
	}

}
