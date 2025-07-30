/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.codegen;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.utils.EH;
import com.f1.utils.LH;

public class ClassPathModifier {
	private static final Logger log = Logger.getLogger(ClassPathModifier.class.getName());
	private static final Class[] PARAMETERS = new Class[] { URL.class };
	private static final Set<URI> added = new HashSet<URI>();
	private static Set<File> addedFiles = new HashSet<File>();

	static {
		for (String s : EH.getJavaClassPath()) {
			addedFiles.add(new File(s));
		}
		for (String s : EH.getJavaClassPathAbsolute())
			addedFiles.add(new File(s));
	}

	public static void addFile(final File file) throws IOException {
		if (addedFiles.contains(file))// already added!
			return;
		addUri(file.toURI());
		addedFiles.add(file);
	}

	synchronized public static void addUri(final URI u) throws IOException {
		if (added.contains(u))
			return;
		try {
			final ClassLoader cl = ClassPathModifier.class.getClassLoader();
			if (!(cl instanceof URLClassLoader)) {
				File t = new File(u.toURL().getFile());
				for (File f : addedFiles)
					if (f.equals(t)) {
						addedFiles.add(t);
						added.add(u);
						return;
					}
				System.err.println("You must manually add to your classpath: " + t);
				EH.systemExit(-1);
			}
			final Method method = cl.getClass().getDeclaredMethod("addURL", PARAMETERS);
			method.setAccessible(true);
			method.invoke(cl, u.toURL());
			added.add(u);
		} catch (final Exception e) {
			try {
				final ClassLoader cl = ClassLoader.getSystemClassLoader();
				final Method method = URLClassLoader.class.getDeclaredMethod("addURL", PARAMETERS);
				method.setAccessible(true);
				method.invoke(cl, u.toURL());
				added.add(u);
			} catch (final Exception e2) {
				LH.severe(log, "Attempt 1 failed while Error adding url to classpath: ", u, e);
				LH.severe(log, "Attempt 2 failed while Error adding url to classpath: ", u, e2);
				//				throw new IOException("Error adding url to classpath: " + u, e);
			}
		}
	}

}
