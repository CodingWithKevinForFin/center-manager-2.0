package com.f1.bootstrap;

import com.f1.utils.RH;
import com.f1.utils.SH;

public class BootStrapper {

	private static final String OPTION_F1_BOOTSTRAP_MAIN = "f1.bootstrap.main";
	private static final String OPTION_F1_BOOTSTRAP_AFTER = "f1.bootstrap.after";

	public static void main(String args[]) {
		final String mainClassName = System.getProperty(OPTION_F1_BOOTSTRAP_MAIN);
		final String after = System.getProperty(OPTION_F1_BOOTSTRAP_AFTER);
		if (mainClassName == null)
			throw new RuntimeException("You must supply argument -D" + OPTION_F1_BOOTSTRAP_MAIN + "=<class_with_public_static_void_main>");
		final Class<?> mainClass = RH.getClass(mainClassName);
		if (after == null) {
			ContainerBootstrap bs = new ContainerBootstrap(mainClass, args);
			bs.startup();
		}
		System.err.println("bootstrapping main: " + mainClass.getName() + " with options: " + SH.join(' ', args));
		RH.invokeStaticMethod(mainClass, "main", new Object[] { args });
		System.err.println("bootstrapping out: " + mainClass.getName() + " with options: " + SH.join(' ', args));
		if (after != null) {
			ContainerBootstrap bs = new ContainerBootstrap(mainClass, args);
			bs.startup();
		}
	}
}
