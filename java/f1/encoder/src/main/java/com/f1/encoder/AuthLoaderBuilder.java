package com.f1.encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.f1.utils.AH;
import com.f1.utils.IOH;
import com.f1.utils.SH;

public class AuthLoaderBuilder {

	static private final byte[] APP_BYTES = AuthLoader.APP.getBytes();

	public static void main(String a[]) throws IOException {

		System.out.println("Running AuthLoaderBuilder");
		if (a.length < 2) {
			System.err.println("Arguments:  appname outputfile");
			return;
		}
		String appName = a[0];
		if ("\\*".equals(appName))
			appName = "*";
		String outputFile = a[1];
		URLClassLoader CL = (URLClassLoader) AuthLoader.class.getClassLoader();
		String name = AuthLoader.class.getName().replace('.', '/') + ".class";
		File f = null;
		byte[] data = null;
		OUTER: for (URL url : CL.getURLs()) {
			if (url.getFile().endsWith(".jar")) {
				try {
					JarInputStream jis = new JarInputStream(new FileInputStream(url.getFile()));
					for (;;) {
						JarEntry je = jis.getNextJarEntry();
						if (je == null)
							break;
						if (je.getName().equals(name)) {
							data = IOH.readData(jis);
							System.out.println("Pulling data from: " + je);
							break OUTER;
						}
					}
					IOH.close(jis);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
			File file = new File(url.getFile(), name);
			if (file.exists()) {
				data = IOH.readData(file);
				System.out.println("Pulling data from: " + file);
				break;
			}
		}
		if (data == null)
			throw new FileNotFoundException(name);
		System.out.println("Raw data is " + data.length + " byte(s): " + IOH.checkSumBsdLong(data));
		byte[] appBytes = SH.leftAlign(' ', appName, APP_BYTES.length, false).getBytes();
		int start = AH.indexOf(data, APP_BYTES, 0);
		System.arraycopy(appBytes, 0, data, start, appBytes.length);
		for (int i = 0; i < data.length / 2; i++) {
			int j = data.length - i - 1;
			byte tmp = data[i];
			data[i] = (byte) (data[j] ^ 123);
			data[j] = (byte) (tmp ^ 123);
		}

		IOH.writeData(new File(outputFile), data);
		System.out.println("For app=" + appName + " Wrote " + data.length + " byte(s) to: " + outputFile);
	}

	private static void replace(byte[] data, String find, String replace) {
		if (replace.length() > find.length())
			replace = replace.substring(0, find.length());
		int i = AH.indexOf(data, find.getBytes(), 0);
		if (i == -1)
			throw new RuntimeException("not found: " + find);
		byte[] appBytes = SH.leftAlign(' ', replace, find.length(), false).getBytes();
		System.arraycopy(appBytes, 0, data, i, appBytes.length);
	}

}
