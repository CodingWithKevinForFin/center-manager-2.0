package com.f1.encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Pattern;
import java.util.zip.Adler32;

import javax.crypto.Cipher;

import com.f1.utils.AH;
import com.f1.utils.Cksum;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.SearchPath;
import com.f1.utils.encrypt.TextBasedSecureRandom;
import com.f1.utils.structs.Tuple2;

public class AuthLoader extends SecureRandom implements RSAPublicKey, RSAPrivateKey, Runnable {

	private static final String RSA = "RSA/ECB/PKCS1PADDING";
	private static final String ERROR_PREFIX = "[F1 LICENSE ERR] ";
	private static final String WARN_PREFIX = "[F1 LICENSE WRN] ";
	static String APP = "APP____________________________________________________________________________________________";
	private static boolean init = false;
	private static String[] props;
	private static String licenseText;

	public static void main(String a[]) throws Exception {
		go();
	}

	public synchronized static String[] go() {
		System.out.print("[F1_VER_3]");
		System.out.flush();
		try {
			if (init) {
				error("bad state");
				exit();
			}
			init = true;
			String expectedApp = APP.replaceAll(" ", "");
			String expectedHost = EH.getLocalHost();
			Set<String> licenseFiles = new LinkedHashSet<String>();
			addOptions(licenseFiles, System.getProperty("f1.license.file"));
			addOptions(licenseFiles, System.getenv("F1_LICENSE_FILE"));
			addOptionsFromFiles(licenseFiles, System.getenv("F1_LICENSE_PROPERTY_FILE"));
			addOptionsFromFiles(licenseFiles, System.getProperty("f1.license.property.file"));
			licenseFiles.add("f1license.txt");
			Calendar cal = GregorianCalendar.getInstance();
			String now = cal.get(Calendar.YEAR) + s(cal.get(Calendar.MONTH) + 1) + s(cal.get(Calendar.DAY_OF_MONTH));
			boolean dev = "dev".equals(System.getProperty("f1.license.mode")) || ("dev".equals(System.getenv("F1_LICENSE_MODE")));
			String licenseFileOption = null;
			for (String s : licenseFiles) {
				File file = new File(s);
				if (file.isFile()) {
					if (file.canRead()) {
						licenseFileOption = s;
						break;
					}
					System.err.println("F1 License file not readable, skipping: " + IOH.getFullPath(file));
				}
			}
			if (licenseFileOption != null && dev) {
				dev = false;
			}
			if ("*".equals(expectedApp) && System.getProperty("f1.license.expectedapp") != null) {
				expectedApp = System.getProperty("f1.license.expectedapp");
			}

			boolean secure = !dev && !"*".equals(expectedApp);
			String partMagicKey;
			String partApp;
			String partInstance;
			String partHost;
			String partStart;
			String partEnd;
			File licenseFile;
			if (dev) {
				partMagicKey = "3FKEY";
				partApp = System.getProperty("f1.license.application", expectedApp);
				partInstance = System.getProperty("f1.license.instance", "DEV");
				partHost = expectedHost;
				partStart = now;
				partEnd = now;
				licenseFile = null;
			} else if (expectedApp.endsWith("_Unlocked")) {
				partMagicKey = "3FKEY";
				partApp = expectedApp;
				partInstance = "Unlocked";
				partHost = expectedHost;
				partStart = now;
				partEnd = "29991231";
				licenseFile = null;
				secure = false;
			} else {
				if (licenseFileOption == null && !dev) {
					error(" F1 License File not found. To use an existing license file either:");
					error("      (1) Set the system property to: -Df1.license.file=<path_to_license_file>");
					error(" -or- (2) Set the environment var to:   F1_LICENSE_FILE=<path_to_license_file>");
					error(" -or- (3) Set the environment var to:   F1_LICENSE_PROPERTY_FILE=path to file containing line with f1.license.file=<path_to_license_file>");
					error(" -or- (4) Set the system property to: -Df1.license.property.file=path to file containing line with f1.license.file=<path_to_license_file>");
					boolean first = true;
					for (String s : licenseFiles) {
						if (first)
							error(" -or- (5) Deposit F1 License file at: " + IOH.getFullPath(new File(s)));
						else
							error("                               or at: " + IOH.getFullPath(new File(s)));
						first = false;
					}
					error("");
					error(" If you don't have a license file, you can run in demo mode, in which case the application will exit after two hours");
					error("      (1) Set the system property to: -Df1.license.mode=dev");
					error(" -or- (2) Set the environment var to:   F1_LICENSE_MODE=dev");
					error("");
					error(" To create a license file, please enter the below configuration at http://3forge.com");
					error("       Application: " + expectedApp);
					error("              Host: " + expectedHost);
					error("");
					error("Note: Files names should either be absolute or relative to: '" + EH.getPwd() + "'");
					exit();
					return null;
				}
				licenseFile = new File(licenseFileOption);
				if (!licenseFile.isFile()) {
					error(" File not found: " + IOH.getFullPath(licenseFile));
					error(" If you need to create the license file, please use the following configuration:");
					error("       Application: " + expectedApp);
					error("              Host: " + expectedHost);
					error("");
					exit();
				}
				int i = 1;
				StringBuilder sb = new StringBuilder();
				sb.append("dome").append(' ');
				sb.append("sort").append(' ');
				sb.append("of").append(' ');
				sb.append("key").append(' ');
				sb.append("GENERATING").append(' ');
				sb.append("TEXt").append(' ');
				sb.append("that some").append(i++).append(' ');
				sb.append("AUght ").append(i).append(' ');
				sb.append("B").append(' ');
				sb.append("unABle ").append(i).append(' ');
				sb.append("guess").append(' ');
				char[] chars = sb.toString().toLowerCase().toCharArray();
				chars[0] = 'S';
				chars[19] = 'N';
				chars[24] = Character.toUpperCase(chars[24]);
				Key key = generateKey2(new String(chars).trim() + "!#$").getB();
				licenseText = IOH.readText(licenseFile);
				licenseText = SH.replaceAll(licenseText, '\n', "");
				licenseText = SH.replaceAll(licenseText, '\r', "");
				licenseText = licenseText.trim();
				licenseText = SH.stripSuffix(licenseText, "\n", false);
				if (SH.isnt(licenseText)) {
					error(" Invalid format. File is empty: " + IOH.getFullPath(licenseFile));
					exit();
				}
				String prefix = SH.beforeLast(licenseText, '|');
				String actualCertKey = SH.afterLast(licenseText, '|');
				String parts[] = SH.split('|', prefix);
				if (parts.length < 6 || SH.isnt(actualCertKey)) {
					error(" Invalid format. File unexpected EOF: " + IOH.getFullPath(licenseFile));
					exit();
				}
				partMagicKey = parts[0];
				partApp = parts[1];
				partInstance = parts[2];
				partHost = parts[3];
				partStart = parts[4];
				partEnd = parts[5];
				boolean oldMethod = "3FKEY".equals(partMagicKey);
				if (oldMethod) {
					String certKey = checkSumString(encrypt(key, prefix.getBytes(), false));
					if ((secure && !certKey.equals(actualCertKey)) || parts.length != 6) {
						if (actualCertKey.equals(checkSumString(encrypt(key, (new String(chars).trim()).getBytes(), false)))) {
							secure = false;
						} else {
							error(" Invalid format. File tampered with: " + IOH.getFullPath(licenseFile));
							exit();
						}
					}
				} else {
					byte[] t = encrypt(key, prefix.getBytes(), false);
					String certKey = checkSumString(t, 16);
					if ((secure && !isSalt(certKey, actualCertKey, 8) || parts.length != 6)) {

						if (actualCertKey.equals(checkSumString(encrypt(key, (new String(chars).trim()).getBytes(), false)))) {
							secure = false;
						} else {
							error(" Invalid format. File tampered with: " + IOH.getFullPath(licenseFile));
							exit();
						}
					}
				}

				if (secure) {
					if (!partMagicKey.equals("3FKEY") && !partMagicKey.equals("3FORGE_KEY")) {
						error(" Invalid magic key. File tampered with: " + IOH.getFullPath(licenseFile));
						exit();
					}
					boolean exit = false;
					if (partStart.compareTo(now) > 0) {
						error(" Date invalid: " + now);
						error(" Exiting.... Please visit 3forge.com or email support@3forge.com for customer support.");
						exit = true;
					}
					if (!"*".equals(expectedApp) && !OH.in(expectedApp, SH.split(',', partApp))) {
						error(" Invalid app name in license file: " + IOH.getFullPath(licenseFile));
						error("     Supplied: " + partApp);
						exit = true;
					}
					if (SH.isnt(partInstance)) {
						error(" Instance name required in license file: " + IOH.getFullPath(licenseFile));
						exit = true;
					}
					if (partHost.equals(".*")) {

					} else if (partHost.matches("~.*\\w{2}.*\\w{2}.*")) {
						Pattern pattern = null;
						try {
							pattern = Pattern.compile(partHost.substring(1));
						} catch (Exception e) {
							error(" Invalid pattern syntax in license file: " + IOH.getFullPath(licenseFile));
							error("     Supplied: " + partHost);
							exit = true;
						}
						if (pattern != null && !pattern.matcher(expectedHost).matches()) {
							error(" Invalid host name pattern in license file: " + IOH.getFullPath(licenseFile));
							error("     Supplied: " + partHost);
							exit = true;
						}

					} else if (!OH.in(expectedHost, SH.split(',', partHost))) {
						error(" Invalid host name in license file: " + IOH.getFullPath(licenseFile));
						error("     Supplied: " + partHost);
						exit = true;
					}

					if (partEnd.compareTo(now) < 0) {
						cal.add(Calendar.DAY_OF_YEAR, -10);
						String tenDaysAgo = cal.get(Calendar.YEAR) + s(cal.get(Calendar.MONTH) + 1) + s(cal.get(Calendar.DAY_OF_MONTH));
						if (partEnd.compareTo(tenDaysAgo) < 0) {
							error(" License expired on " + partEnd + " and the 10 day grace has ended: " + IOH.getFullPath(licenseFile));
							error(" Exiting.... Please visit 3forge.com for contact information on customer support.");
							exit = true;
						} else {
							System.err.println("[3FORGE.COM LICENSE KEY WARNING] license expired on " + partEnd
									+ ". Please renew before the 10 day grace period ends. Current License File: " + IOH.getFullPath(licenseFile));
							System.err.print("[3FORGE.COM LICENSE KEY WARNING] Visit 3forge.com. Sleeping for 30 seconds");
							for (int j = 0; j < 30; j++) {
								System.err.print('.');
								OH.sleep(1000);
							}
							System.err.println();
						}
					}
					if (exit) {
						System.err.println(
								"[F1 LICENSE ERR]  Please visit http://3forge.com for contact information. If you have an account, please login and select the Licenses tab.");
						error(" When creating the license file, please use the following configuration:");
						error("       Application: " + expectedApp);
						error("              Host: " + expectedHost);
						error("   (File should be deposited at " + IOH.getFullPath(licenseFile) + ")");
						error("");
						exit();
					}
				}
			}
			props = new String[] { expectedApp, partInstance, expectedHost, partStart, partEnd, partApp, partHost };
			if (systemClassLoader instanceof URLClassLoader) {
				URL[] urls = ((URLClassLoader) systemClassLoader).getURLs();
				for (URL url : urls) {
					File file = new File(SH.decodeUrl(url.getFile()));
					if (file.isDirectory()) {
						processDirectory(file, expectedApp);
					} else if (file.isFile() && file.getName().endsWith(".jar")) {

						processJar(file, expectedApp);
					}
				}
			} else {
				String path = System.getProperty("java.class.path");
				String urls[];
				if (path == null)
					urls = OH.EMPTY_STRING_ARRAY;
				else
					urls = SH.split(File.pathSeparator, path);
				for (String url : urls) {
					File file = new File(url);
					if (file.isDirectory()) {
						processDirectory(file, expectedApp);
					} else if (file.isFile() && file.getName().endsWith(".jar")) {

						processJar(file, expectedApp);
					}
				}
			}
			if (dev) {
				if (definedClasses > 0) {
					System.err.println("Okay. Running in F1 License dev mode.  ** WILL EXIT IN TWO HOURS **");
				} else {
					System.err.println("Running in F1 License dev mode.  ** WILL EXIT IN TWO HOURS **");
				}
				for (String s : licenseFiles)
					System.err.println("  >> YOU CAN PLACE F1 LICENSE FILE FILE AT: " + IOH.getFullPath(new File(s)));
			} else if (secure) {
				if (definedClasses > 0) {
					System.out.println("Okay. Accepted F1 License file. Expires on " + partEnd + ": " + IOH.getFullPath(licenseFile));
				} else {
					System.out.println("Accepted F1 License File. Expires on " + partEnd + ": " + IOH.getFullPath(licenseFile));
				}
			} else {
				if (definedClasses > 0) {
					if (licenseFile != null)
						System.out.println("Okay. Accepted F1 License file: " + IOH.getFullPath(licenseFile));
					else
						System.out.println("Okay. F1 License Unlocked");
				} else {
					if (licenseFile != null)
						System.out.println("Accepted F1 License File: " + IOH.getFullPath(licenseFile));
					else
						System.out.println("F1 License Unlocked");
				}
			}
			Thread thread = new Thread(new AuthLoader(dev ? "dev" : "prod"), "Thread");
			thread.setDaemon(true);
			thread.start();

			Class<?> clazz = null;
			try {
				clazz = Class.forName("com.f1.container.impl.dispatching.RootPartitionActionRunner");
			} catch (Exception e) {
			}
			if (clazz != null) {
				boolean found = false;
				for (Field f : clazz.getDeclaredFields()) {
					if (f.getType() == Thread.class) {
						f.setAccessible(true);
						f.set(null, thread);
						f.setAccessible(false);
						found = true;
						break;
					}
				}
				if (!found) {
					error("Could not bind thread");
					exit();
				}
			}
			return props;
		} catch (Exception e) {
			error("Internal  Error: " + e);
			printException(e);
			exit();
			return null;
		}
	}
	private static void addOptionsFromFiles(Set<String> licenseFiles, String propertyFileNames) {
		if (SH.is(propertyFileNames)) {
			for (String file : SH.trimStrings(SH.split(',', propertyFileNames))) {
				File f = new File(SH.trim(file));
				if (!f.isFile() || !f.canRead())
					continue;
				try {
					int count = 0;
					for (String line : SH.splitLines(IOH.readText(f)))
						if (line.startsWith("f1.license.file=")) {
							licenseFiles.add(SH.afterFirst(line, '='));
							count++;
						}
				} catch (Exception e) {
					System.out.println("Error reading prpoerty file from " + IOH.getFullPath(f));
					printException(e);
				}
			}
		}

	}
	private static void addOptions(Collection<String> licenseFiles, String list) {
		if (SH.is(list))
			for (String file : SH.trimStrings(SH.split(',', list)))
				if (SH.is(file))
					licenseFiles.add(file);
	}

	private static void exit() {
		error(" Exiting with code 23: Please visit http://3forge.com");
		System.exit(23);

	}

	private static void declareLicenseFile(String name, byte[] data) throws Exception {
		String padding = SH.repeat(' ', 255);
		replace(data, "UNKNOWN_LicenseApp" + padding, props[0]);
		replace(data, "UNKNOWN_LicenseInstance" + padding, props[1]);
		replace(data, "UNKNOWN_LicenseHost" + padding, props[2]);
		replace(data, "UNKNOWN_LicenseStartDate" + padding, props[3]);
		replace(data, "UNKNOWN_LicenseEndDate" + padding, props[4]);
		defineClass(name, data);
		boolean found = false;
		if (licenseText != null) {
			for (Field f : Class.forName(name).getDeclaredFields()) {
				if (f.getType() == StringBuilder.class) {
					f.setAccessible(true);
					f.set(null, new StringBuilder(licenseText));
					found = true;
					break;
				}
			}
			if (!found) {
				error("could not bind licenseText");
				exit();
			}
		}
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

	private static void error(String string) {
		System.err.println(ERROR_PREFIX + string);
	}

	private static String s(int i) {
		return i < 10 ? "0" + i : "" + i;
	}

	private static ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
	private static int definedClasses = 0;

	private static void defineClass(String name, byte[] data) {
		definedClasses++;
		RH.invokeMethod(systemClassLoader, "defineClass", name, data, 0, data.length);
		try {
			Class.forName(name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void processDirectory(File dir, String appName) throws IOException {
		List<File> files = new SearchPath(dir).search("*.f1class$", SearchPath.OPTION_CONTINUE_AFTER_FIRST_FIND | SearchPath.OPTION_IS_PATTERN | SearchPath.OPTION_RECURSE);
		for (File file : files) {
			try {
				String className = SH.stripPrefix(file.toString(), dir.toString() + file.separatorChar, true);
				className = toClassName(className, true);
				byte[] data = IOH.readData(file);
				data = process(data, appName);
				defineClass(className, data);
			} catch (Exception e) {
				System.err.println("Error processing file '" + file);
				printException(e);
			}
		}
		List<File> files2 = new SearchPath(dir).search("F1LicenseInfo.class",
				SearchPath.OPTION_CONTINUE_AFTER_FIRST_FIND | SearchPath.OPTION_IS_PATTERN | SearchPath.OPTION_RECURSE);
		for (File file : files2) {
			try {
				String className = SH.stripPrefix(file.toString(), dir.toString() + file.separatorChar, true);
				className = toClassName(className, false);
				byte[] data = IOH.readData(file);
				declareLicenseFile(className, data);
			} catch (Exception e) {
				System.err.println("Error processing file '" + file);
				printException(e);
			}
		}
	}

	private static String toClassName(String className, boolean isF1Class) {
		className = SH.stripSuffix(className, isF1Class ? ".f1class" : ".class", true);
		className = SH.replaceAll(className, '/', '.');
		className = SH.replaceAll(className, '\\', '.');
		return className;
	}

	private static void processJar(File file, String appName) throws FileNotFoundException, IOException {
		JarInputStream jis = new JarInputStream(new FileInputStream(file));
		for (;;) {
			JarEntry je = jis.getNextJarEntry();
			if (je == null)
				break;
			try {
				if (je.getName().endsWith(".f1class")) {
					byte[] data = IOH.readData(jis);
					data = process(data, appName);
					defineClass(toClassName(je.getName(), true), data);
				} else if (je.getName().endsWith("/F1LicenseInfo.class")) {
					byte[] data = IOH.readData(jis);
					declareLicenseFile(toClassName(je.getName(), false), data);
				}
			} catch (Exception e) {
				System.err.println("Error with file entry '" + file + "::" + je.getName() + "' (" + je.getSize() + " bytes): appname==>'" + appName + "'");
				printException(e);
			}
		}
		IOH.close(jis);
	}

	private static void printException(Exception e) {
		if ("ok".equals(System.getProperty("f1.authloader.printstacktrace")))
			e.printStackTrace(System.err);
	}

	public static byte[] process(byte[] data, String appName) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 20; i++)
			sb.append(i).append(i * 2).append(appName);
		Tuple2<RSAPublicKey, RSAPrivateKey> k = generateKey(sb.toString());
		return decrypt(k.getA(), data);
	}

	private static final char DELIM = '|';

	static public byte[] encrypt(Key publicKey, byte[] data, boolean random) {
		if (data == null)
			throw new NullPointerException("data");
		if (publicKey == null)
			throw new NullPointerException("privateKey");
		try {
			Cipher cipher = Cipher.getInstance(RSA);
			if (random)
				cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			else
				cipher.init(Cipher.ENCRYPT_MODE, publicKey, new TextBasedSecureRandom(new String(publicKey.getEncoded())));
			int blocks = (data.length + 52) / 53;
			byte[] r = new byte[blocks * 64];
			int remaining = data.length, position = 0;
			for (int i = 0; i < blocks; i++) {
				position += cipher.doFinal(data, i * 53, Math.min(53, remaining), r, position);
				remaining -= 53;
			}
			return Arrays.copyOf(r, position);
		} catch (Exception e) {
			throw new RuntimeException("Error encrypting data for RSA key", e);
		}
	}

	static public byte[] decrypt(RSAPublicKey publicKey, byte[] data) {
		if (data == null)
			throw new NullPointerException("data");
		if (publicKey == null)
			throw new NullPointerException("publicKey");
		try {
			Cipher cipher = Cipher.getInstance(RSA);
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			int blocks = (data.length + 63) / 64;
			byte[] r = new byte[blocks * 53 + 11];
			int remaining = data.length, position = 0;
			for (int i = 0; i < blocks; i++) {
				position += cipher.doFinal(data, i * 64, Math.min(64, remaining), r, position);
				remaining -= 64;
			}
			return Arrays.copyOf(r, position);
		} catch (Exception e) {
			throw new RuntimeException("Error decrypting data for RSA private key: " + publicKeyToString(publicKey), e);
		}
	}

	public static Tuple2<RSAPublicKey, RSAPrivateKey> generateKey(String text) {
		KeyPairGenerator keyGen;
		try {
			keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(new RSAKeyGenParameterSpec(512, BigInteger.valueOf(65537)), new TextBasedSecureRandom(text));
			KeyPair pair = keyGen.generateKeyPair();
			return new Tuple2<RSAPublicKey, RSAPrivateKey>((RSAPublicKey) pair.getPublic(), (RSAPrivateKey) pair.getPrivate());
		} catch (Exception e) {
			throw new RuntimeException("error generating " + 512 + "-bit RSA key for text: " + SH.password(text), e);
		}
	}

	public static Tuple2<RSAPublicKey, RSAPrivateKey> generateKey2(String text) {
		KeyPairGenerator keyGen;
		try {
			keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(new RSAKeyGenParameterSpec(512, BigInteger.valueOf(65537)), new AuthLoader(text));
			KeyPair pair = keyGen.generateKeyPair();
			return new Tuple2<RSAPublicKey, RSAPrivateKey>((RSAPublicKey) pair.getPublic(), (RSAPrivateKey) pair.getPrivate());
		} catch (Exception e) {
			throw new RuntimeException("error generating " + 512 + "-bit RSA key for text: " + SH.password(text), e);
		}
	}

	final private String text;

	private int read = 0;

	public AuthLoader(String text) {
		this.text = text;
		this.algorithm = null;
		this.format = null;
		this.encoded = null;
		this.modulus = null;
		this.exponent = null;
	}

	@Override
	synchronized public void nextBytes(byte[] bytes) {

		if (bytes.length == 0)
			return;
		int bit = 0;
		for (;;) {
			char c = text.charAt(read++ % text.length());
			c += read / text.length();
			int j;
			if (OH.isBetween(c, 'a', 'z'))
				j = c - 'a';
			else if (OH.isBetween(c, 'A', 'Z'))
				j = c - 'A' + 26;
			else if (OH.isBetween(c, '0', '9'))
				j = c - '0' + 52;
			else if (c == ' ')
				j = 62;
			else
				j = 63;
			for (int k = 1; k < 64; k *= 2) {
				if ((j & k) != 0)
					bytes[bit / 8] ^= 1 << bit % 8;
				if (++bit == bytes.length * 8) {
					return;
				}
			}
		}
	}

	public static String publicKeyToString(RSAPublicKey key) {
		StringBuilder sb = new StringBuilder();
		sb.append(key.getAlgorithm());
		sb.append(DELIM);
		sb.append(key.getFormat());
		sb.append(DELIM);
		sb.append(key.getModulus().toString());
		sb.append(DELIM);
		sb.append(key.getPublicExponent().toString());
		sb.append(DELIM);
		SH.toHex(key.getEncoded(), sb);
		return sb.toString();
	}

	public static String privateKeyToString(RSAPrivateKey key) {
		StringBuilder sb = new StringBuilder();
		sb.append(key.getAlgorithm());
		sb.append(DELIM);
		sb.append(key.getFormat());
		sb.append(DELIM);
		sb.append(key.getModulus().toString());
		sb.append(DELIM);
		sb.append(key.getPrivateExponent().toString());
		sb.append(DELIM);
		SH.toHex(key.getEncoded(), sb);
		return sb.toString();
	}

	public static RSAPublicKey stringToPublicKey(String text) {
		final String[] parts = SH.split(DELIM, text);
		final String aglorithm = parts[0];
		final String format = parts[1];
		final BigInteger modulus = new BigInteger(parts[2]);
		final BigInteger publicExponent = new BigInteger(parts[3]);
		final byte[] encoded = SH.fromHex(parts[4]);
		return new AuthLoader(aglorithm, format, encoded, modulus, publicExponent);
	}

	public static RSAPrivateKey stringToPrivateKey(String text) {
		final String[] parts = SH.split(DELIM, text);
		final String aglorithm = parts[0];
		final String format = parts[1];
		final BigInteger modulus = new BigInteger(parts[2]);
		final BigInteger publicExponent = new BigInteger(parts[3]);
		final byte[] encoded = SH.fromHex(parts[4]);
		return new AuthLoader(aglorithm, format, encoded, modulus, publicExponent);
	}

	final private String algorithm;
	final private String format;
	final private byte[] encoded;
	final private BigInteger modulus;
	final private BigInteger exponent;

	public AuthLoader(String algorithm, String format, byte[] encoded, BigInteger modulus, BigInteger exponent) {
		this.text = null;
		this.algorithm = algorithm;
		this.format = format;
		this.encoded = encoded;
		this.modulus = modulus;
		this.exponent = exponent;
	}

	public AuthLoader(boolean dev) {
		this.text = null;
		this.algorithm = null;
		this.format = null;
		this.encoded = null;
		this.modulus = null;
		this.exponent = null;
	}

	@Override
	public String getAlgorithm() {
		return algorithm;
	}

	@Override
	public String getFormat() {
		return format;
	}

	@Override
	public byte[] getEncoded() {
		return encoded;
	}

	@Override
	public BigInteger getModulus() {
		return modulus;
	}

	public BigInteger getExponent() {
		return exponent;
	}

	@Override
	public BigInteger getPublicExponent() {
		return getExponent();
	}

	@Override
	public BigInteger getPrivateExponent() {
		return getExponent();
	}

	public static long checkSum(byte[] data) {
		final Adler32 adler32 = new Adler32();
		adler32.update(data);
		return MH.abs(adler32.getValue());
	}

	final private static int MAXLENGTH = SH.toString(Long.MAX_VALUE, 62).length();

	public static String checkSumString(byte[] data) {
		return SH.rightAlign('0', SH.toString(checkSum(data) | (1l << 62), 62), MAXLENGTH, true);
	}

	@Override
	public void run() {
		if (text.equals("dev")) {
			sleep(60 * 60000);
			System.err.println(WARN_PREFIX + "Running in dev mode, application will exit in 1 hour");
			sleep(30 * 60000);
			System.err.println(WARN_PREFIX + "Running in dev mode, application will exit in 30 minutes");
			sleep(25 * 60000);
			System.err.println(WARN_PREFIX + "Running in dev mode, application will exit in 5 minutes");
			sleep(4 * 60000);
			System.err.println(WARN_PREFIX + "Running in dev mode, application will exit in 1 minute");
			sleep(1 * 60000);
			new Thread(new AuthLoader("done")).start();
			sleep(1 * 60000);
			exit();
		} else if (text.equals("prod")) {
			while (true)
				try {
					Thread.sleep(1000000);
				} catch (InterruptedException e) {
					printException(e);
				}
		} else {
			System.err.println(WARN_PREFIX + "Dev mode time has expired");
			exit();
		}
	}

	private void sleep(long time) {
		final long end = System.currentTimeMillis() + time;
		for (;;) {
			final long remaining = end - System.currentTimeMillis();
			if (remaining <= 0)
				break;
			try {
				Thread.sleep(remaining);
			} catch (InterruptedException e) {
				printException(e);
			}
		}
	}

	private static boolean isSalt(String certKey, String certKeyWs, int len) {
		if (certKey.length() + len != certKeyWs.length())
			return false;
		long num = SH.parseLong(certKeyWs.substring(0, 1), 62);
		int chksum2 = SH.parseInt(certKeyWs.substring(1, 2), 62);
		certKeyWs = certKeyWs.substring(2);
		int chksum = Math.abs((int) Cksum.cksum(certKeyWs.getBytes())) % 62;
		if (chksum != chksum2)
			return false;
		certKey = SH.shuffle(new StringBuilder(certKey), new Random(num)).toString();
		for (int i = 0, l = certKey.length(), n = 0; i < l; i++, n++) {
			n = certKeyWs.indexOf(certKey.charAt(i), n);
			if (n == -1)
				return false;
		}
		return true;
	}

	static private String salt(String encrypt, int cnt) {
		SecureRandom sr = new SecureRandom();
		long t = sr.nextInt(62);
		StringBuilder sb = new StringBuilder(encrypt);
		SH.shuffle(sb, new Random(t));
		for (int i = 1; i < cnt; i++)
			sb.insert(sr.nextInt(sb.length() + 1), SH.toString(sr.nextInt(62), 62));
		sb.insert(0, SH.toString(t, 62));
		return sb.toString();
	}
	static private String checkSumString(byte[] encrypt, int length) {
		StringBuilder sb = new StringBuilder();
		long cksum = Cksum.cksum(encrypt);
		SH.toString(cksum, 62, sb);
		Random r = new Random(cksum);
		while (sb.length() < length)
			SH.toString((long) r.nextInt(62), 62, sb);
		sb.setLength(length);
		SH.shuffle(sb, r);
		return sb.toString();
	}
}
