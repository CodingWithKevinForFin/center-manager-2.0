/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.io.Closeable;
import java.io.DataInput;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ProtocolException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.impl.TextMatcherFactory;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.tar.TarEntry;
import com.f1.utils.tar.TarInputStream;

public class IOH {
	private static final String TLS = System.getProperty("f1.sslcontext.default", "TLSv1.2");
	public static final String HEADER_OPTION_F1_METHOD = "F1-Method";
	public static final String HEADER_OPTION_F1_HEADER = "F1-Header";

	public static final Comparator<? super File> MODIFIED_TIME_COMPARATOR = new ModifiedTimeComparator();
	public static final Logger log = Logger.getLogger(IOH.class.getName());
	private static final int DEFAULT_BACKLOG = 0;
	public static final HashSet<Character> INVALID_WINDOWS_CHARS = new HashSet<Character>(Arrays.asList('<', '>', ':', '"', '/', '\\', '|', '?', '*'));
	public static final HashSet<Character> INVALID_UNIX_CHARS = new HashSet<Character>(Arrays.asList('\000'));

	public static class ModifiedTimeComparator implements Comparator<File> {
		@Override
		public int compare(File o1, File o2) {
			return OH.compare(o1.lastModified(), o2.lastModified());
		}
	}

	public static void ensureEmptyDir(File directory) throws IOException {
		if (directory.isDirectory()) {
			File[] listFiles = directory.listFiles();
			if (listFiles != null) {
				for (File f : listFiles)
					deleteForce(f);
			}

		} else
			ensureDir(directory);
	}
	public static void ensureDir(File directory) throws IOException {
		if (directory.exists()) {
			if (directory.isDirectory())
				return;
			throw new IOException("Exists, but not a directory: " + IOH.getFullPath(directory));
		}
		LH.info(log, "Creating directory: " + IOH.getFullPath(directory));
		if (!directory.mkdirs())
			throw new IOException("could not create directory: " + IOH.getFullPath(directory));
	}

	public static boolean delete(File file) throws IOException {
		if (file == null || !file.exists())
			return false;
		LH.info(log, "Deleting: " + file);
		if (!file.delete())
			throw new IOException("delete failed: " + file);
		return true;
	}

	public static boolean deleteForce(File file) throws IOException {
		LH.info(log, "Deleting(forced): " + file);
		return deleteForce2(file);
	}

	private static boolean deleteForce2(File file) throws IOException {
		if (file == null || !file.exists())
			return false;
		if (!file.canWrite())
			file.setWritable(true);
		if (file.isDirectory()) {
			File[] listFiles = file.listFiles();
			if (listFiles != null) {
				for (File f : listFiles)
					deleteForce2(f);
			}
		}

		if (!file.delete())
			throw new IOException("delete failed: " + file);
		return true;
	}

	public static String readText(File file) throws IOException {
		return readText(file, false);
	}

	public static String readText(File file, boolean throwOnMissing) throws IOException {
		if (!file.exists()) {
			if (!throwOnMissing)
				return null;
			throw new FileNotFoundException(getFullPath(file));
		}
		return readText(file.toString(), new FileInputStream(file));
	}

	public static String readText(String description, InputStream stream) throws IOException {
		try {
			int len = stream.available();
			byte buf[] = new byte[Math.max(len, 1024)];
			FastByteArrayOutputStream out = new FastByteArrayOutputStream();
			pipe(stream, out, buf);
			if (log.isLoggable(Level.FINE))
				LH.fine(log, "Loaded ", len, " byte(s) from file: ", description);
			return toString(out.toByteArray());
		} finally {
			close(stream);
		}
	}

	public static String readText(InputStream stream) throws IOException {
		try {
			int len = stream.available();
			byte buf[] = new byte[Math.max(len, 1024)];
			FastByteArrayOutputStream out = new FastByteArrayOutputStream();
			pipe(stream, out, buf);
			return toString(out.toByteArray());
		} finally {
			close(stream);
		}
	}

	public static byte[] readData(String description, InputStream stream) throws IOException {
		try {
			int len = stream.available();
			byte buf[] = new byte[Math.max(len, 1024)];
			FastByteArrayOutputStream out = new FastByteArrayOutputStream();
			pipe(stream, out, buf);
			if (log.isLoggable(Level.FINE))
				LH.fine(log, "Loaded ", len, " byte(s) from file: ", description);
			return out.toByteArray();
		} finally {
			close(stream);
		}
	}

	public static void pipe(InputStream i, OutputStream o, byte tmp[]) throws IOException {
		pipe(i, o, tmp, false);
	}

	public static void pipe(InputStream i, OutputStream o, byte tmp[], boolean flush) throws IOException {
		if (tmp == null || tmp.length == 0)
			throw new IllegalArgumentException("empty array");
		int length;
		while ((length = i.read(tmp)) != -1) {
			o.write(tmp, 0, length);
			if (flush)
				o.flush();
		}

	}
	// returns number of bytes actually read before either EOF or count is reached
	public static int pipe(InputStream i, OutputStream o, byte tmp[], int count) throws IOException {
		if (tmp == null || tmp.length == 0)
			throw new IllegalArgumentException("empty array");
		int length;
		int remaining = count;
		while (remaining > 0 && (length = i.read(tmp, 0, Math.min(remaining, tmp.length))) != -1) {
			o.write(tmp, 0, length);
			remaining -= length;
		}
		return count - remaining;
	}

	public static void close(Socket t) {
		close(t, true);
	}

	public static void close(Closeable t) {
		close(t, true);
	}

	public static void close(ServerSocket serverSocket) {
		close(serverSocket, true);
	}

	public static void close(Closeable t, boolean logErrors) {
		try {
			if (t != null)
				t.close();
		} catch (Exception e) {
			if (logErrors)
				LH.warning(log, "Closing: ", t, e);
		}
	}

	public static void close(Statement t) {
		close(t, true);
	}
	public static void close(Statement t, boolean logErrors) {
		try {
			if (t != null)
				t.close();
		} catch (Exception e) {
			if (logErrors)
				LH.warning(log, "Closing: ", t, e);
		}

	}

	public static void close(Socket t, boolean logErrors) {
		try {
			if (t != null)
				t.close();
		} catch (Exception e) {
			if (logErrors)
				LH.warning(log, "Closing: ", t, e);
		}
	}

	public static void close(ServerSocket t, boolean logErrors) {
		try {
			if (t != null)
				t.close();
		} catch (Exception e) {
			if (logErrors)
				LH.warning(log, "Closing: ", t, e);
		}
	}

	public static void close(Connection t) {
		close(t, true);

	}

	public static void close(PreparedStatement t) {
		close(t, true);

	}

	public static void close(ResultSet t) {
		close(t, true);

	}

	public static void close(ResultSet t, boolean logErrors) {
		try {
			if (t != null)
				t.close();
		} catch (Exception e) {
			if (logErrors)
				LH.warning(log, "Closing: ", t, e);
		}
	}

	public static void close(Connection t, boolean logErrors) {
		try {
			if (t != null)
				t.close();
		} catch (Exception e) {
			if (logErrors)
				LH.warning(log, "Closing: ", t, e);
		}
	}

	public static void close(PreparedStatement t, boolean logErrors) {
		try {
			if (t != null)
				t.close();
		} catch (Exception e) {
			if (logErrors)
				LH.warning(log, "Closing: ", t, e);
		}
	}

	public static void writeText(File file, String text) throws IOException {
		FileWriter f = null;
		try {
			f = new FileWriter(file, false);
			f.write(text);
		} catch (Exception e) {
			throw new IOException("Could not write to " + IOH.getFullPath(file), e);
		} finally {
			close(f);
		}
	}

	public static void writeData(File file, byte[] data) throws IOException {
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(data);
		} catch (Exception e) {
			throw new IOException("Could not write to " + IOH.getFullPath(file), e);
		} finally {
			close(out);
		}
	}
	public static void appendData(File file, byte[] data) throws IOException {
		OutputStream out = null;
		try {
			out = new FileOutputStream(file, true);
			out.write(data);
		} catch (Exception e) {
			throw new IOException("Could not write to " + IOH.getFullPath(file), e);
		} finally {
			close(out);
		}
	}
	public static void writeData(File file, byte[] data, int offset, int length) throws IOException {
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(data, offset, length);
		} catch (Exception e) {
			throw new IOException("Could not write to " + IOH.getFullPath(file), e);
		} finally {
			close(out);
		}
	}

	public static void appendText(File file, String text) throws IOException {
		FileWriter f = null;
		try {
			f = new FileWriter(file, true);
			f.write(text);
		} catch (Exception e) {
			throw new IOException("Could not write to " + IOH.getFullPath(file), e);
		} finally {
			close(f);
		}
	}

	public static String readTextFromResource(String file) throws IOException {
		InputStream stream = IOH.class.getClassLoader().getResourceAsStream(file);
		if (stream == null)
			return null;
		return readText(file, stream);
	}

	public static byte[] readDataFromResource(String file) throws IOException {
		InputStream stream = IOH.class.getClassLoader().getResourceAsStream(file);
		if (stream == null)
			return null;
		return readData(file, stream);
	}
	public static byte[] readDataFromResource2(String file) throws IOException {
		InputStream stream = IOH.class.getResourceAsStream("/resources/" + file);
		if (stream == null)
			return null;
		return readData(file, stream);
	}

	public static String readText(Class clazz, String fileExt) throws IOException {
		return readText(clazz.getPackage(), clazz.getSimpleName() + fileExt);
	}

	public static String readText(Package pakage, String fileName) throws IOException {
		String path = pakage.getName().replace('.', '/') + "/" + fileName;
		InputStream stream = IOH.class.getClassLoader().getResourceAsStream(path);
		if (stream == null)
			throw new FileNotFoundException("resource: " + path);
		return readText(path, stream);
	}

	public static byte[] readData(Package pakage, String fileName) throws IOException {
		String path = pakage.getName().replace('.', '/') + "/" + fileName;
		InputStream stream = IOH.class.getClassLoader().getResourceAsStream(path);
		if (stream == null)
			throw new FileNotFoundException("resource: " + path);
		return readData(path, stream);
	}

	public static byte[] readData(InputStream in, int length) throws IOException {
		byte r[] = new byte[length];
		readData(in, r, 0, length);
		return r;
	}

	public static void pipe(InputStream in, OutputStream out) throws IOException {
		pipe(in, out, new byte[2048], false);
	}

	public static void pipe(InputStream in, OutputStream out, boolean flush) throws IOException {
		pipe(in, out, new byte[2048], flush);
	}

	public static byte[] readData(InputStream in) throws IOException {
		FastByteArrayOutputStream buf = new FastByteArrayOutputStream();
		pipe(in, buf, false);
		return buf.toByteArray();
	}
	public static void readData(InputStream in, byte r[], int offset, int length) throws IOException {
		for (int i = 0; i < length;) {
			int t = in.read(r, i + offset, length - i);
			if (t < 0)
				throw new EOFException("eof on " + i + " bytes but expecting " + length);
			i += t;
		}
	}
	public static int readDataNoThrow(InputStream in, byte r[], int offset, int length) throws IOException {
		for (int i = 0; i < length;) {
			int t = in.read(r, i + offset, length - i);
			if (t < 0)
				return i;
			i += t;
		}
		return length;
	}

	public static void optimize(Socket socket) {
		try {
			socket.setTcpNoDelay(true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void optimize(ServerSocket serverSocket) {
		try {
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] readData(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		try {
			return readData(is, (int) file.length());
		} catch (IOException e) {
			throw new IOException("Error reading from file: " + getFullPath(file), e);
		} finally {
			close(is);
		}
	}
	public static void readData(File file, OutputStream sink) throws IOException {
		InputStream is = new FileInputStream(file);
		try {
			pipe(is, sink);
		} catch (IOException e) {
			throw new IOException("Error reading from file: " + getFullPath(file), e);
		} finally {
			close(is);
		}
	}
	public static byte[] readData(File file, long startOffset, int count) throws IOException {
		InputStream is = new FileInputStream(file);
		skip(is, startOffset);
		try {
			return readData(is, count);
		} finally {
			close(is);
		}
	}

	public static void commit(Connection t, boolean logErrors) {
		try {
			if (t != null)
				t.commit();
		} catch (Exception e) {
			if (logErrors)
				LH.warning(log, "Closing: ", t, e);
		}
	}

	public static ServerSocket openServerSocketWithReason(int port, String reason) throws IOException {
		return openServerSocketWithReason(null, port, reason);
	}
	public static ServerSocket openServerSocketWithReason(String bindAddr, int port, String reason) throws IOException {
		try {
			ServerSocket r = new ServerSocket(port, DEFAULT_BACKLOG, parseBindAddr(bindAddr));
			if (reason != null) {
				if (port == 0)
					LH.info(log, "Started server socket for ", reason, " on dynamic port ", r.getLocalPort(), logBinding(bindAddr));
				else
					LH.info(log, "Started server socket for ", reason, " on port ", port, logBinding(bindAddr));
			}
			return r;
		} catch (IOException e) {
			throw new IOException("error with " + reason + ": Could not start server socket at port " + port + logBinding(bindAddr), e);
		}
	}
	private static String logBinding(String bindAddr) {
		return SH.isnt(bindAddr) ? "" : (" bound to '" + bindAddr + "'");
	}
	private static InetAddress parseBindAddr(String bindAddr) throws IOException {
		try {
			return SH.isnt(bindAddr) ? null : InetAddress.getByName(bindAddr);
		} catch (Exception e) {
			throw new IOException("Could resolve address for binding: '" + bindAddr + "'", e);
		}
	}
	public static File[] splitFiles(String files) {
		return splitFiles(File.pathSeparatorChar, files);
	}

	public static File[] splitFiles(char delim, String files) {
		String[] filesArray = SH.split(delim, files);
		File[] r = new File[filesArray.length];
		for (int i = 0; i < filesArray.length; i++)
			r[i] = new File(filesArray[i]);
		return r;
	}

	public static File[] listFiles(File directory, boolean throwOnError) {

		if (directory == null || !directory.isDirectory()) {
			if (throwOnError)
				throw new RuntimeException("Not a directory: " + directory);
			else
				return OH.EMPTY_FILE_ARRAY;
		}
		File[] r = directory.listFiles();

		if (r == null) {
			if (throwOnError)
				throw new RuntimeException("directory returned null list:" + directory);
			else
				return OH.EMPTY_FILE_ARRAY;
		}
		return r;
	}

	static public String toUnixFormat(String pathname) {
		if (!EH.isWindows())
			return pathname;
		return SH.replaceAll(pathname, '\\', '/');
	}
	public static String toUnixFormatForce(String pathname) {
		return SH.replaceAll(pathname, '\\', '/');
	}

	public static void skip(InputStream in, long t) throws IOException {
		long n = t - in.skip(t);
		if (n == 0)
			return;
		int zeros = 0;
		while (n != 0) {
			long skipped = in.skip(n);
			if (skipped > 0) {
				zeros = 0;
				n -= skipped;
			} else if (zeros++ > 1000)
				throw new IOException("Could not skip required " + t + " byte(s), still " + n + " remaining");
		}
	}
	public static void skipBytes(FastDataInput in, int t) throws IOException {
		int n = t - in.skipBytes(t);
		if (n == 0)
			return;
		int zeros = 0;
		while (n != 0) {
			long skipped = in.skipBytes(n);
			if (skipped > 0) {
				zeros = 0;
				n -= skipped;
			} else if (zeros++ > 1000)
				throw new IOException("Could not skip required " + t + " byte(s), still " + n + " remaining");
		}
	}
	public static void skipBytes(RandomAccessFile in, long t) throws IOException {
		long n = t - in.skipBytes((int) Math.min(t, Integer.MAX_VALUE));
		if (n == 0)
			return;
		int zeros = 0;
		while (n != 0) {
			long skipped = in.skipBytes((int) Math.min(n, Integer.MAX_VALUE));
			if (skipped > 0) {
				n -= skipped;
				zeros = 0;
			} else if (zeros++ > 1000)
				throw new IOException("Could not skip required " + t + " byte(s), still " + n + " remaining");
		}
	}

	public static void skip(Reader in, long t) throws IOException {
		long n = t - in.skip(t);
		if (n == 0)
			return;
		int zeros = 0;
		while (n != 0) {
			long skipped = in.skip(n);
			if (skipped > 0) {
				zeros = 0;
				n -= skipped;
			} else if (zeros++ > 1000)
				throw new IOException("Could not skip required " + t + " byte(s), still " + n + " remaining");
		}
	}

	public static Socket openClientSocketWithReason(String hostName, int port, String reason) throws IOException {
		try {
			Socket r = new Socket(hostName, port);
			if (reason != null)
				LH.info(log, "Opened client socket for ", reason, " at ", hostName, ":", port);
			return r;
		} catch (IOException e) {
			throw new IOException("error with " + reason + ": Could not open client socket at: " + hostName + ":" + port, e);
		}
	}

	public static void renameOrThrow(File existingFile, File newFile) throws IOException {
		if (!existingFile.renameTo(newFile))
			throw new IOException("rename failed: " + getFullPath(existingFile) + " to " + getFullPath(newFile));
	}

	public static boolean isDirectory(File file) {
		return file != null && file.isDirectory();
	}
	public static boolean isntDirectory(File file) {
		return !isDirectory(file);
	}

	public static boolean isFile(File file) {
		return file != null && file.isFile();
	}
	public static boolean isntFile(File file) {
		return !isFile(file);
	}

	public static boolean isValidHostName(String name) {
		if (name == null || name.length() > 255 || name.length() < 1)
			return false;
		for (String part : SH.split('.', name)) {
			if (part.length() > 63 || part.length() < 1)
				return false;
			for (int i = 0, l = part.length(); i < l; i++) {
				char c = part.charAt(i);
				if (!OH.isBetween(c, 'a', 'z') && !OH.isBetween(c, 'A', 'Z') && !OH.isBetween(c, '0', '9') && !OH.inChars(c, '-', '_')) {
					return false;
				}
			}
		}
		return true;
	}

	// for java 7 and after
	//	public static boolean isValidPath(String path) {
	//		try {
	//			Paths.get(path);
	//		} catch (InvalidPathException ex) {
	//			return false;
	//		} catch (NullPointerException ex) {
	//			return false;
	//		}
	//		return true;
	//	}

	public static HashSet<Character> getInvalidCharsByOS() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			return INVALID_WINDOWS_CHARS;
		} else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
			return INVALID_UNIX_CHARS;
		} else {
			return new HashSet<Character>();
		}
	}

	public static boolean isValidFilename(String name) {
		if (name == null || name.isEmpty())
			return false;
		HashSet<Character> badChars = getInvalidCharsByOS();
		for (int i = 0; i < name.length(); i++) {
			if (badChars.contains(name.charAt(i)))
				return false;
		}
		return true;
	}

	public static String getFullPath(File file) {
		try {

			return file == null ? null : file.getCanonicalPath();
		} catch (IOException e) {
			return file.getAbsolutePath();
		}
	}

	/**
	 * Same behavior as ls or dir. If an existing file is supplied, then it is simply returned. If an existing directory is supplied, then all contained files are returned. If the
	 * path is a mask, then all files matching will be returned.
	 * 
	 * @param file
	 *            file,directory or mask to search by
	 * @return list of matching files
	 * @throws FileNotFoundException
	 *             if the supplied maks's directory does not exist
	 */
	public static File[] listFiles(File file) throws FileNotFoundException {
		if (file.exists())
			return file.isDirectory() ? file.listFiles() : new File[] { file };
		final File directory = file.getParentFile();
		final TextMatcher matcher = TextMatcherFactory.DEFAULT.toMatcher(file.getName());
		if (!directory.isDirectory())
			throw new FileNotFoundException("not a directory: " + getFullPath(file));

		final List<File> r = new ArrayList<File>();
		File[] listFiles = directory.listFiles();
		if (listFiles != null) {
			for (File f : listFiles)
				if (matcher.matches(f.getName()))
					r.add(f);
		}

		return r.isEmpty() ? OH.EMPTY_FILE_ARRAY : r.toArray(new File[r.size()]);
	}

	public static boolean isSymlink(File file) throws IOException {
		if (file == null)
			throw new NullPointerException("file");
		final File canon;
		if (file.getParent() == null)
			canon = file;
		else
			canon = new File(file.getParentFile().getCanonicalFile(), file.getName());
		return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
	}

	static public int checkSumBsd(InputStream is) throws IOException {
		int r = 0;
		for (int i; (i = is.read()) != -1;)
			r = ((r >> 1) + ((r & 1) << 15) + i) & 0xffff;
		return r;
	}
	static public int checkSumBsd(byte[] data) throws IOException {
		int r = 0;
		for (int n = 0; n < data.length; n++) {
			r = ((r >> 1) + ((r & 1) << 15) + MH.toUnsignedInt(data[n])) & 0xffff;
		}
		return r;
	}

	static public long checkSumBsdLong(InputStream is) throws IOException {
		long r = 0;
		for (int i; (i = is.read()) != -1;)
			r = applyChecksum64(r, i);
		return r;
	}
	static public long checkSumBsdLong(byte data[]) {
		return checkSumBsdLong(0, data);
	}
	static public long checkSumBsdLong(long r, byte data[]) {
		return checkSumBsdLong(r, data, 0, data.length);
	}
	static public long checkSumBsdLong(long r, byte data[], int start, int end) {
		for (int i = start; i < end; i++)
			r = applyChecksum64(r, 0xff & data[i]);
		return r;
	}

	static public int applyChecksum16(int currentChecksum, int data) {
		return ((currentChecksum >> 1) + ((currentChecksum & 1) << 15) + data) & 0xfff;
	}

	static public int applyChecksum32(int currentChecksum, int data) {

		return (currentChecksum >> 1) + ((currentChecksum & 1) << 31) + data;
	}

	static public long applyChecksum64(long currentChecksum, int data) {

		return (currentChecksum >> 1) + ((currentChecksum & 1) << 63) + data;
	}

	static public String getCanonical(String t) {
		StringBuilder sb = new StringBuilder();
		int skip = 0, i = t.length(), j = t.lastIndexOf('/', i - 1);
		if (j == i - 1) {
			sb.append('/');
		}
		boolean needsSlash = false;
		for (;;) {
			if (i - j == 3 && t.startsWith("..", j + 1)) {
				skip++;
			} else if (i - j != 1 && !(i - j == 2 && t.charAt(j + 1) == '.')) {
				if (skip > 0) {
					skip--;
				} else {
					if (needsSlash)
						sb.append('/');
					while (--i > j)
						sb.append(t.charAt(i));
					needsSlash = true;
				}
			}
			if (j == -1)
				break;
			i = j;
			j = t.lastIndexOf('/', i - 1);
		}
		while (skip-- > 0) {
			if (needsSlash)
				sb.append("/..");
			else {
				needsSlash = true;
				sb.append("..");
			}
		}
		if (i == 0 && needsSlash)
			sb.append('/');
		return sb.reverse().toString();

	}

	public static SSLServerSocket openSSLServerSocketWithReason(int port, File keystore, String keystorePassword, String reason) throws IOException {
		return openSSLServerSocketWithReason(null, port, keystore, keystorePassword, reason);
	}
	public static SSLServerSocket openSSLServerSocketWithReason(String bindAddr, int port, File keystore, String keystorePassword, String reason) throws IOException {
		if (!keystore.isFile())
			throw new IOException("keystore not found: " + getFullPath(keystore)
					+ "   ( you can run: keytool -genkeypair -alias myalias -keyalg RSA -validity 365 -keysize 1024 -keystore " + getFullPath(keystore) + " )");
		try {
			final SSLServerSocketFactory factory = createSslSocketFactory(new FileInputStream(keystore), keystorePassword).getServerSocketFactory();
			final SSLServerSocket ss = (SSLServerSocket) factory.createServerSocket(port, DEFAULT_BACKLOG, parseBindAddr(bindAddr));
			if (reason != null) {
				if (port == 0)
					LH.info(log, "Started secure server socket (keystore=", getFullPath(keystore), ") for ", reason, " on dynamic port ", ss.getLocalPort(), logBinding(bindAddr));
				else
					LH.info(log, "Started secure server socket (keystore=", getFullPath(keystore), ") for ", reason, " on port ", port, logBinding(bindAddr));
			}
			return ss;
		} catch (Exception e) {
			throw new IOException("error with " + reason + ": Could not start secure server socket (keystore=" + getFullPath(keystore) + ") at port " + port + logBinding(bindAddr),
					e);
		}

	}
	public static SSLSocket openSSLClientSocketWithReason(String hostname, int port, File keystore, String keystorePassword, String reason) throws IOException {
		if (!keystore.isFile())
			throw new IOException("keystore not found: " + getFullPath(keystore)
					+ "   ( you can run: keytool -genkeypair -alias myalias -keyalg RSA -validity 365 -keysize 1024 -keystore " + getFullPath(keystore) + " )");
		try {
			if (reason != null)
				LH.info(log, "Started secure client socket (keystore=", getFullPath(keystore), ") for ", reason, " at ", hostname, ":", port);
			final SSLSocketFactory factory = createSslSocketFactory(new FileInputStream(keystore), keystorePassword).getSocketFactory();
			final SSLSocket ss = (SSLSocket) factory.createSocket(hostname, port);
			return ss;
		} catch (Exception e) {
			throw new IOException("error with " + reason + ": Could not start secure client socket (keystore=" + getFullPath(keystore) + ") at port " + port, e);
		}

	}
	public static SSLServerSocket openSSLServerSocketWithReason(int port, String keystoreContents, String keystorePassword, String reason) throws IOException {
		return openSSLServerSocketWithReason(port, keystoreContents, keystorePassword, reason);
	}
	public static SSLServerSocket openSSLServerSocketWithReason(String bindAddr, int port, byte[] keystoreContents, String keystorePassword, String reason) throws IOException {
		if (SH.isnt(keystoreContents))
			throw new IOException("keystore contents empty: ( you can run: keytool -genkeypair -alias myalias -keyalg RSA -validity 365 -keysize 1024 -keystore /tmp/file.txt)");
		try {
			final SSLServerSocketFactory factory = createSslSocketFactory(new FastByteArrayInputStream(keystoreContents), keystorePassword).getServerSocketFactory();
			final SSLServerSocket ss = (SSLServerSocket) factory.createServerSocket(port, DEFAULT_BACKLOG, parseBindAddr(bindAddr));
			if (reason != null) {
				if (port == 0)
					LH.info(log, "Started secure server socket for ", reason, " on dynamic port ", ss.getLocalPort(), logBinding(bindAddr));
				else
					LH.info(log, "Started secure server socket for ", reason, " on port ", port, logBinding(bindAddr));
			}
			return ss;
		} catch (Exception e) {
			throw new IOException("error with " + reason + ": Could not start secure server socket  at port " + port + logBinding(bindAddr), e);
		}

	}
	public static SSLSocket openSSLClientSocketWithReason(String hostname, int port, byte[] keystoreContents, String keystorePassword, String reason) throws IOException {
		if (SH.isnt(keystoreContents))
			throw new IOException("keystore contents empty: ( you can run: keytool -genkeypair -alias myalias -keyalg RSA -validity 365 -keysize 1024 -keystore /tmp/file.txt)");
		try {
			final SSLSocketFactory factory = createSslSocketFactory(new FastByteArrayInputStream(keystoreContents), keystorePassword).getSocketFactory();
			final SSLSocket ss = (SSLSocket) factory.createSocket(hostname, port);
			if (reason != null)
				LH.info(log, "Started secure client socket for ", reason, " at ", hostname, ":", port);
			return ss;
		} catch (Exception e) {
			throw new IOException("error with " + reason + ": Could not start secure client socket at port " + port, e);
		}

	}

	static private SSLContext createSslSocketFactory(InputStream inputStream, String keystorePassword)
			throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, KeyManagementException {
		try {
			final char[] keystorePasswordChars = keystorePassword.toCharArray();
			LH.info(log, "Using SSLContext: ", TLS);
			final SSLContext sslContext = SSLContext.getInstance(TLS);
			final KeyManagerFactory km = KeyManagerFactory.getInstance("SunX509");
			final KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(inputStream, keystorePasswordChars);
			km.init(ks, keystorePasswordChars);
			final TrustManagerFactory tm = TrustManagerFactory.getInstance("SunX509");
			tm.init(ks);
			sslContext.init(km.getKeyManagers(), tm.getTrustManagers(), null);
			return sslContext;
		} finally {
			close(inputStream);
		}
	}

	public static String toString(byte[] bytes) {
		return bytes.length == 0 ? "" : toString(bytes, new StringBuilder(bytes.length)).toString();
	}
	public static StringBuilder toString(byte[] bytes, StringBuilder sb) {
		int len = bytes.length;
		if (len == 0)
			return sb;
		else if (len == 1)
			return sb.append((char) (bytes[0] & 0xff));
		int first = bytes[0];

		//00 00 fe ff -> UTF-32 Big-endian    (not supported)
		//ff fe 00 00 -> UTF-32 Little-endian (not supported)
		//fe ff       -> UTF-16 Big-endian    
		//ff fe       -> UTF-16 Little-endian (supported)
		//ef bb bf    -> UTF-8                (supported)

		switch (first) {
			case -2:
				if (len >= 2 && bytes[1] == -1) //fe ff
					return toStringUtf16BE(bytes, 2, sb);
				else
					break;
			case -1:
				if (len >= 4 && bytes[1] == -2 && bytes[2] == 0 && bytes[3] == 0) //ff fe 00 00
					return toStringUtf32BE(bytes, 4, sb);
				else if (len >= 2 && bytes[1] == -2) //ff fe
					return toStringUtf16LE(bytes, 4, sb);
				else
					break;
			case 0:
				if (len >= 4 && bytes[1] == 0 && bytes[2] == -2 && bytes[3] == -1) //00 00 fe ff
					return toStringUtf32BE(bytes, 4, sb);
				else
					break;
			case -17:
				if (len >= 3 && bytes[1] == -69 && bytes[2] == -65) //ef bb bf
					return toStringUtf8(bytes, 3, sb);
				else
					break;
		}
		return toStringUtf8(bytes, 0, sb);

	}

	private static StringBuilder toStringUtf32BE(byte[] bytes, int start, StringBuilder sb) {
		return sb.append("[UTF-32BE not supported]");
	}
	private static StringBuilder toStringUtf8(byte[] bytes, int start, StringBuilder sb) {
		for (int j = start; j < bytes.length;) {
			byte b = bytes[j++];
			if (b < 0) {//first bit set
				int i = b & 0xff;
				if ((i & 0x40) == 0 || j == bytes.length || (bytes[j] & 0xc0) != 0x80) {//first bit has signature 10xxxxx, or second doesn't have signature 10xxxxxx
					sb.append((char) (i));
				} else if ((i & 0x20) == 0) {//up to second bit set: 110xxxxx
					//code point format: 110xxxxx10xxxxxx
					int b2 = (bytes[j++] & 0x3f) + ((i & 0x1f) << 6);
					sb.append((char) b2);
				} else if ((i & 0x10) == 0) {//up to third  bit set: 1110xxxx
					//code point format: 110xxxxx10xxxxxx10xxxxxx
					int b2 = ((bytes[j++] & 0x3f) << 6) + (bytes[j++] & 0x3f) + ((i & 0x1f) << 12);
					sb.append((char) b2);
				} else if ((i & 0x8) == 0) {//up to fourth  bit set: 11110xxx
					sb.append("[UTF-8 4-byte sequence not supported: ").append(SH.toHex(i)).append(" ").append("]");
				} else {
					sb.append("[unknown UTF-8 Byte: ").append(SH.toHex(i)).append("]");
				}
			} else
				sb.append((char) b);
		}
		return sb;
	}
	private static StringBuilder toStringUtf16BE(byte[] bytes, int start, StringBuilder sb) {
		for (int j = start; j < bytes.length;) {
			int value = bytes[j++] & 0xff;
			if (j == bytes.length) {
				sb.append("[trailing extra byte in UTF-16BE sequence: ").append(SH.toHex(value)).append(']');
			} else {
				value = (value << 8) + (bytes[j++] & 0xff);
				if (value < 0xd800 || value >= 0xdfff || j >= bytes.length - 1) {
					sb.append((char) value);
				} else {
					int value2 = ((bytes[j++] & 0xff) << 8) + (bytes[j++] & 0xff);
					if (value2 < 0xdc00 || value2 >= 0xdfff) {
						sb.append("[invalid UTF-16BE sequence: ").append(SH.toHex(value)).append(" ").append(SH.toHex(value2)).append(']');
					}
					int codePoint = (value & 0x3ff) << 10 + value2 & 0x3ff;
					sb.append(Character.toChars(codePoint));
				}
			}
		}

		return sb;
	}

	private static StringBuilder toStringUtf16LE(byte[] bytes, int start, StringBuilder sb) {
		for (int j = start; j < bytes.length;) {
			int value = bytes[j++] & 0xff;
			if (j == bytes.length) {
				sb.append("[trailing extra byte in UTF-16LE sequence: ").append(SH.toHex(value)).append(']');
			} else {
				value = (value) + ((bytes[j++] & 0xff) << 8);
				if (value < 0xd800 || value >= 0xdfff || j >= bytes.length - 1) {
					sb.append((char) value);
				} else {
					int value2 = (bytes[j++] & 0xff) + ((bytes[j++] & 0xff) << 8);
					if (value2 < 0xdc00 || value2 >= 0xdfff) {
						sb.append("[invalid UTF-16LE sequence: ").append(SH.toHex(value)).append(" ").append(SH.toHex(value2)).append(']');
					}
					int codePoint = (value & 0x3ff) << 10 + value2 & 0x3ff;
					sb.append(Character.toChars(codePoint));
				}
			}
		}

		return sb;
	}

	public static byte[] readData(DataInput dis, int len) throws IOException {
		byte[] r = new byte[len];
		dis.readFully(r);
		return r;
	}

	public static File joinPaths(File parent, String... children) {
		if (children.length == 0)
			return parent;
		StringBuilder sb = new StringBuilder();
		sb.append(parent.toString());
		for (String child : children) {
			if (!SH.endsWith(sb, File.separatorChar) && !SH.startsWith(child, File.separatorChar))
				sb.append(File.separatorChar);
			sb.append(child);
		}
		return new File(sb.toString());
	}
	public static File joinPaths(String parent, String... children) {
		if (children.length == 0)
			return new File(parent);
		StringBuilder sb = new StringBuilder();
		sb.append(parent);
		for (String child : children) {
			if (!SH.endsWith(sb, File.separatorChar) && !SH.startsWith(child, File.separatorChar))
				sb.append(File.separatorChar);
			sb.append(child);
		}
		return new File(sb.toString());
	}

	public static boolean isAbsolutePath(String s) {
		if (s.startsWith("/") || s.startsWith("~/"))
			return true;
		if (s.length() > 1 && s.charAt(1) == ':' && OH.isBetween(Character.toUpperCase(s.charAt(0)), 'A', 'Z'))
			return true;
		return false;

	}

	public static String getRemoteHostName(Socket socket) {
		final InetAddress ia = socket.getInetAddress();
		return ia.getHostAddress();
	}

	public static void downloadFile(URL sourceUrl, File localFile) throws IOException {
		LH.info(log, "Downloading: " + sourceUrl + " ==> " + localFile);
		InputStream in = new FastBufferedInputStream(sourceUrl.openStream());
		FastBufferedOutputStream out = new FastBufferedOutputStream(new FileOutputStream(localFile));
		IOH.pipe(in, out);
		IOH.close(in);
		IOH.close(out);
	}
	public static byte[] download(URL sourceUrl) throws IOException {
		LH.info(log, "Downloading: " + sourceUrl);
		InputStream in = new FastBufferedInputStream(sourceUrl.openStream());
		FastByteArrayOutputStream out = new FastByteArrayOutputStream();
		IOH.pipe(in, out);
		IOH.close(in);
		IOH.close(out);
		return out.toByteArray();
	}

	public static byte[] doGet(URL sourceUrl, Map<String, String> headers, Map<String, List<String>> returnHeadersSink) throws IOException {
		return doGet(sourceUrl, headers, returnHeadersSink, false, -1);
	}
	public static byte[] doGet(URL sourceUrl, Map<String, String> headers, Map<String, List<String>> returnHeadersSink, boolean ignoreCerts, int timeout) throws IOException {
		return doHttp("GET", sourceUrl, headers, null, returnHeadersSink, ignoreCerts, timeout);
	}

	private static void addRequestProperty(URLConnection connection, String key, String value) throws ProtocolException {
		//		connection.addRequestProperty(key, value);
		if (HEADER_OPTION_F1_METHOD.equals(key)) {
			setRequestMethod((HttpURLConnection) connection, value);
		} else if (HEADER_OPTION_F1_HEADER.equals(key)) {
			connection.addRequestProperty(SH.beforeFirst(value, '='), SH.afterFirst(value, '='));
		} else
			connection.addRequestProperty(key, value);

	}

	public static byte[] doPost(URL sourceUrl, Map<String, String> headers, byte[] data, Map<String, List<String>> returnHeadersSink) throws IOException {
		return doPost(sourceUrl, headers, data, returnHeadersSink, false, -1);
	}
	public static byte[] doPost(URL sourceUrl, Map<String, String> headers, byte[] data, Map<String, List<String>> returnHeadersSink, boolean ignoreCerts, int timeout)
			throws IOException {
		return doHttp("POST", sourceUrl, headers, data, returnHeadersSink, ignoreCerts, timeout);
	}

	private static String cookieMapToString(Map<String, String> cookies) {
		StringBuilder sb = new StringBuilder();
		boolean firstCookie = true;
		for (Entry<String, String> cookie : cookies.entrySet()) {
			if (firstCookie)
				firstCookie = false;
			else
				sb.append("; ");
			sb.append(cookie.getKey());
			sb.append("=");
			sb.append(cookie.getValue());
		}
		return sb.toString();
	}

	private static Map<String, String> parseCookies(String cookieString) {
		Map<String, String> cookies = new HashMap<String, String>();
		String[] cookiePairList = cookieString.split(";");
		for (String cookiePair : cookiePairList) {
			if (SH.indexOf(cookiePair, "=", 0) != -1) {
				String cookieKey = SH.beforeFirst(cookiePair, "=");
				String cookieVal = SH.afterFirst(cookiePair, "=");
				if (cookieKey != null && cookieVal != null)
					cookies.put(cookieKey.trim(), cookieVal.trim());
			}
		}
		return cookies;
	}

	public static byte[] doHttpHandleRedirect(String requestMethod, String baseUrl, String urlExtension, String urlParams, final Map<String, String> headers, final byte[] data,
			final Map<String, List<String>> returnHeadersSink, final boolean ignoreCerts, final int timeout, final boolean redirectFollowHttpMethod,
			final boolean redirectFollowAuthHeader, final boolean redirectPersistCookies) throws IOException {
		InputStream in = null;
		OutputStream out = null;
		try {
			Map<String, String> cookies = new HashMap<String, String>();
			URL sourceUrl = new URL(baseUrl + urlExtension + urlParams);
			HttpURLConnection connection = null;
			//Recursively handle redirects
			int startTime = (int) System.currentTimeMillis();
			while (((int) System.currentTimeMillis() - startTime) < timeout) {
				connection = (HttpURLConnection) sourceUrl.openConnection();
				connection.setInstanceFollowRedirects(false);
				connection.setRequestMethod(requestMethod);
				//Default to GET after redirection
				if (!redirectFollowHttpMethod)
					requestMethod = "GET";

				if (timeout >= 0) {
					connection.setConnectTimeout(timeout);
					connection.setReadTimeout(timeout);
				}
				if (ignoreCerts && connection instanceof HttpsURLConnection)
					trustAllCertificates((HttpsURLConnection) connection);
				connection.setAllowUserInteraction(false);
				if (data != null || "POST".contentEquals(requestMethod))
					connection.setDoOutput(true);
				if (redirectPersistCookies && !cookies.isEmpty())
					headers.put("Cookie", cookieMapToString(cookies));
				if (CH.isntEmpty(headers))
					for (Entry<String, String> entry : headers.entrySet())
						addRequestProperty(connection, entry.getKey(), entry.getValue());
				connection.setDoInput(true);
				if (data != null) {
					out = connection.getOutputStream();
					out.write(data);
					out.flush();
				}
				final int responseCode = connection.getResponseCode();
				if (responseCode != HttpURLConnection.HTTP_OK) {
					if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM
							|| responseCode == HttpURLConnection.HTTP_SEE_OTHER) {

						String newUrlStr = connection.getHeaderField("Location");
						if (SH.isnt(newUrlStr)) {
							newUrlStr = connection.getHeaderField("location");
							if (SH.isnt(newUrlStr))
								throw new ConnectionIOException("Failed to get redirected Location field");
						}
						if (redirectPersistCookies) {
							Map<String, String> newCookies = parseCookies(connection.getHeaderField("Set-Cookie"));
							cookies.putAll(newCookies);
						}
						//Handle relative paths
						final boolean isRelative = SH.startsWith(newUrlStr, '/');
						final URL newUrl = isRelative ? new URL(baseUrl + newUrlStr) : new URL(newUrlStr);
						//Remove auth headers if going to a new host and redirectFollowAuthHeader is not set 
						if (!redirectFollowAuthHeader && !isRelative && CH.isntEmpty(headers) && !SH.equals(newUrl.getHost(), sourceUrl.getHost())) {
							headers.remove("authorization");
							headers.remove("Authorization");
						}
						sourceUrl = newUrl;
						continue;
					}
				}
				break;
			}

			try {
				in = new FastBufferedInputStream(connection.getInputStream());
			} catch (IOException e) {
				byte[] error;
				FastBufferedInputStream in2 = null;
				try {
					in2 = new FastBufferedInputStream(((HttpURLConnection) connection).getErrorStream());
					final FastByteArrayOutputStream buf = new FastByteArrayOutputStream();
					IOH.pipe(in2, buf);
					error = buf.toByteArray();
				} catch (IOException e2) {
					throw e;
				} finally {
					IOH.close(in2);
				}
				if (returnHeadersSink != null)
					returnHeadersSink.putAll(connection.getHeaderFields());

				throw new ConnectionIOException(error, e);
			}
			final FastByteArrayOutputStream buf = new FastByteArrayOutputStream();
			if (returnHeadersSink != null)
				returnHeadersSink.putAll(connection.getHeaderFields());
			IOH.pipe(in, buf);
			return buf.toByteArray();
		} finally {
			IOH.close(in);
			IOH.close(out);
		}
	}

	public static byte[] doHttp(String requestMethod, URL sourceUrl, Map<String, String> headers, byte[] data, Map<String, List<String>> returnHeadersSink, boolean ignoreCerts,
			int timeout) throws IOException {
		InputStream in = null;
		OutputStream out = null;
		try {
			final HttpURLConnection connection = (HttpURLConnection) sourceUrl.openConnection();
			connection.setRequestMethod(requestMethod);
			if (timeout >= 0) {
				connection.setConnectTimeout(timeout);
				connection.setReadTimeout(timeout);
			}
			if (ignoreCerts && connection instanceof HttpsURLConnection)
				trustAllCertificates((HttpsURLConnection) connection);
			connection.setAllowUserInteraction(false);
			if (data != null || "POST".contentEquals(requestMethod))
				connection.setDoOutput(true);
			if (CH.isntEmpty(headers))
				for (Entry<String, String> entry : headers.entrySet())
					addRequestProperty(connection, entry.getKey(), entry.getValue());
			connection.setDoInput(true);
			if (data != null) {
				out = connection.getOutputStream();
				out.write(data);
				out.flush();
			}
			try {
				in = new FastBufferedInputStream(connection.getInputStream());
			} catch (IOException e) {
				byte[] error;
				FastBufferedInputStream in2 = null;
				try {
					in2 = new FastBufferedInputStream(((HttpURLConnection) connection).getErrorStream());
					final FastByteArrayOutputStream buf = new FastByteArrayOutputStream();
					IOH.pipe(in2, buf);
					error = buf.toByteArray();
				} catch (IOException e2) {
					throw e;
				} finally {
					IOH.close(in2);
				}
				if (returnHeadersSink != null)
					returnHeadersSink.putAll(connection.getHeaderFields());

				throw new ConnectionIOException(error, e);
			}
			final FastByteArrayOutputStream buf = new FastByteArrayOutputStream();
			if (returnHeadersSink != null)
				returnHeadersSink.putAll(connection.getHeaderFields());
			IOH.pipe(in, buf);
			return buf.toByteArray();
		} finally {
			IOH.close(in);
			IOH.close(out);
		}
	}

	public static final int HTTP_TYPE_UNKNOWN = -1;
	public static final int HTTP_TYPE_RAW = 0;
	public static final int HTTP_TYPE_JSON = 1;
	public static final int HTTP_TYPE_TEXT = 2;
	private static final Map<String, Integer> HTTP_ID_TO_TYPE_MAP = CH.m( //
			"raw", HTTP_TYPE_RAW //
			, "json", HTTP_TYPE_JSON //
			, "text", HTTP_TYPE_TEXT //
	);

	public static int getHttpDataType(String typeId) {
		if (SH.isnt(typeId))
			return HTTP_TYPE_UNKNOWN;
		typeId = SH.toLowerCase(typeId);
		if (!HTTP_ID_TO_TYPE_MAP.containsKey(typeId))
			return HTTP_TYPE_UNKNOWN;
		else
			return (int) HTTP_ID_TO_TYPE_MAP.get(typeId);
	}
	public static Object parseHttpResponseData(byte[] data, int dataType) throws IOException {
		byte[] readData = null;
		if (data != null) {
			int type = FileMagic.getType(data);
			// Unzip result if needed
			switch (type) {
				case FileMagic.FILE_TYPE_GZIP_COMPRESSED_DATA: {
					GZIPInputStream in = new GZIPInputStream(new FastByteArrayDataInputStream(data));
					readData = IOH.readData(in);
					break;
				}
				case FileMagic.FILE_TYPE_DEFLATE_COMPRESSED_DATA: {
					InflaterInputStream in = new InflaterInputStream(new FastByteArrayDataInputStream(data));
					readData = IOH.readData(in);
					break;
				}
				default:
					readData = data;
			}

		}
		Object restResult = null;
		if (readData != null) {
			switch (dataType) {
				case HTTP_TYPE_TEXT:
					restResult = new String(readData);
					break;
				case HTTP_TYPE_RAW:
					restResult = readData;
					break;
				case HTTP_TYPE_UNKNOWN:
				case HTTP_TYPE_JSON:
				default:
					String str = SH.trim(new String(readData));
					if (SH.is(str))
						restResult = ObjectToJsonConverter.INSTANCE_COMPACT.stringToObject(str);
					break;
			}
		}
		return restResult;
	}

	static private String[] hackedRequestMethods = OH.EMPTY_STRING_ARRAY;

	private static void setRequestMethod(HttpURLConnection connection, String value) throws ProtocolException {
		if (AH.indexOf(value, hackedRequestMethods) == -1) {
			try {
				connection.setRequestMethod(value);
				return;
			} catch (ProtocolException e) {
				String[] methods = (String[]) RH.getStaticField(HttpURLConnection.class, "methods");
				if (AH.indexOf(value, methods) != -1)
					throw e;
				hackedRequestMethods = AH.append(hackedRequestMethods, value);
				LH.info(log, "Added '", value, "' to global list of valid hacked methods in HttpURLConnection: ", SH.join(',', hackedRequestMethods));
			}
		}
		RH.setField(connection, "method", value);
		if (RH.findFieldNoThrow(connection.getClass(), "delegate") != null)
			setRequestMethod((HttpURLConnection) RH.getField(connection, "delegate"), value);

	}

	public static List<File> expandFile(File sourceFile, File destination, boolean overwrite) throws IOException {
		if (!sourceFile.isFile())
			throw new FileNotFoundException("file not found: " + getFullPath(sourceFile));
		List<File> r = new ArrayList<File>();
		byte[] data = IOH.readData(sourceFile);
		int type = FileMagic.getType(data);
		switch (type) {
			case FileMagic.FILE_TYPE_GZIP_COMPRESSED_DATA: {
				final GZIPInputStream in = new GZIPInputStream(new FastByteArrayDataInputStream(data));
				try {
					if (!overwrite && destination.exists())
						throw new IOException("While expanding " + getFullPath(sourceFile) + ": File already exists: " + getFullPath(destination));
					final FastBufferedOutputStream out = new FastBufferedOutputStream(new FileOutputStream(destination));
					try {
						r.add(destination);
						pipe(in, out);
					} finally {
						close(out);
					}
				} finally {
					close(in);
				}
				break;
			}
			case FileMagic.FILE_TYPE_ZIP_ARCHIVE_DATA: {
				final ZipInputStream in = new ZipInputStream(new FastByteArrayDataInputStream(data));
				try {
					ZipEntry next;
					while ((next = in.getNextEntry()) != null) {
						final File outFile = joinPaths(destination, replaceBackSlashes(next.getName()));
						if (!overwrite && outFile.exists())
							throw new IOException("While expanding " + getFullPath(sourceFile) + ": File already exists: " + getFullPath(outFile));
						if (next.isDirectory()) {
							IOH.ensureDir(outFile);
						} else {
							IOH.ensureDir(outFile.getParentFile());
							final FastBufferedOutputStream out = new FastBufferedOutputStream(new FileOutputStream(outFile));
							try {
								pipe(in, out);
								r.add(outFile);
							} finally {
								close(out);
							}
						}
					}
				} finally {
					close(in);
				}
				break;
			}
			case FileMagic.FILE_TYPE_GNU_TAR_ARCHIVE:
			case FileMagic.FILE_TYPE_POSIX_TAR_ARCHIVE: {
				final TarInputStream in = new TarInputStream(new FastByteArrayDataInputStream(data));
				try {
					TarEntry next;
					while ((next = in.getNextEntry()) != null) {
						final File outFile = joinPaths(destination, replaceBackSlashes(next.getName()));
						try {
							if (!overwrite && outFile.exists())
								throw new IOException("While expanding " + getFullPath(sourceFile) + ": File already exists: " + getFullPath(outFile));
							if (next.isDirectory()) {
								IOH.ensureDir(outFile);
							} else {
								IOH.ensureDir(outFile.getParentFile());
								final FastBufferedOutputStream out = new FastBufferedOutputStream(new FileOutputStream(outFile));
								try {
									pipe(in, out);
									r.add(outFile);
								} finally {
									close(out);
								}
							}
						} catch (IOException e) {
							throw new IOException("Error with file: " + outFile, e);
						}
					}
				} finally {
					close(in);
				}
				break;
			}
			case FileMagic.FILE_TYPE_UNKNOWN: {
				break;
			}
			default:
				throw new RuntimeException("unknown magic type (" + type + ")  for file: " + IOH.getFullPath(sourceFile));
		}
		return r;
	}
	private static String replaceBackSlashes(String name) {
		return SH.replaceAll(name, '\\', '/');
	}

	public static void main2(String a[]) throws IOException {
		System.out.println(checkSumBsdLong("test123".getBytes()));
	}

	public static InputStream openFile(File file, int bufferSize) throws FileNotFoundException {
		final FileInputStream fis = new FileInputStream(file);
		if (bufferSize == 0)
			return fis;
		else
			return new FastBufferedInputStream(fis, bufferSize);
	}

	public static byte[] compress(byte[] data, int offset, int length, int level, boolean nowrap) {
		Deflater def = new Deflater(level, nowrap);
		def.setInput(data, offset, length);
		def.finish();
		byte[] out = new byte[10 + length / 4];
		ByteHelper.writeInt(data.length, out, 0);
		int pos = 4;
		for (;;) {
			int rlen = pos + def.deflate(out, pos, out.length - pos);
			if (rlen < out.length) {
				return Arrays.copyOf(out, rlen);
			}
			out = Arrays.copyOf(out, out.length * 2);
			pos = rlen;
		}
	}

	public static byte[] compressFastest(byte[] data) {
		return compress(data, 0, data.length, Deflater.BEST_SPEED, false);
	}
	public static byte[] compressSmallest(byte[] data) {
		return compress(data, 0, data.length, Deflater.BEST_COMPRESSION, false);
	}
	public static byte[] decompress(byte[] data) {
		return decompress(data, 0, data.length, false);
	}
	public static byte[] decompress(byte[] data, int offset, int length, boolean nowrap) {
		Inflater inf = new Inflater(nowrap);
		int len = ByteHelper.readInt(data, offset);
		inf.setInput(data, offset + 4, length - 4);
		byte[] out = new byte[len];
		try {
			inf.inflate(out, 0, len);
		} catch (Exception e) {
			throw new RuntimeException("bad data for deflation", e);
		}
		return out;
	}
	static public int ip4ToInt(byte ip[]) {
		return (MH.toUnsignedInt(ip[0]) << 24) + (MH.toUnsignedInt(ip[1]) << 16) + (MH.toUnsignedInt(ip[2]) << 8) + (MH.toUnsignedInt(ip[3]));
	}
	static public byte[] intToIp4(int ip) {
		return new byte[] { (byte) ((ip >> 24) & 0xFF), (byte) ((ip >> 16) & 0xFF), (byte) ((ip >> 8) & 0xFF), (byte) (ip & 0xFF) };
	}

	public static byte[] parseIp4(String ip) {
		String[] parts = SH.split('.', ip);
		if (parts.length != 4)
			throw new RuntimeException("invalid ip4: " + ip);
		int part0 = Integer.parseInt(parts[0]);
		int part1 = Integer.parseInt(parts[1]);
		int part2 = Integer.parseInt(parts[2]);
		int part3 = Integer.parseInt(parts[3]);
		if (part0 < 0 || part1 < 0 || part2 < 0 || part3 < 0 || part0 > 255 || part1 > 255 || part2 > 255 || part3 > 255)
			throw new RuntimeException("invalid ip4: " + ip);
		return new byte[] { (byte) part0, (byte) part1, (byte) part2, (byte) part3 };
	}

	public static String formatIp(byte[] ip4) {
		return formatIp(ip4, new StringBuilder()).toString();
	}
	public static StringBuilder formatIp(byte[] ip4, StringBuilder sink) {
		sink.append(MH.toUnsignedInt(ip4[0])).append('.');
		sink.append(MH.toUnsignedInt(ip4[1])).append('.');
		sink.append(MH.toUnsignedInt(ip4[2])).append('.');
		return sink.append(MH.toUnsignedInt(ip4[3]));
	}

	public static String formatIp(int ip4) {
		return formatIp(intToIp4(ip4));
	}

	public static void assertFileExists(File file, String fileDescription) {
		if (file == null || !file.isFile())
			throw new RuntimeException(" File not found: " + getFullPath(file) + "  (" + fileDescription + ")");
	}
	public static void assertDirExists(File file, String fileDescription) {
		if (file == null || !file.isDirectory())
			throw new RuntimeException(" Directory not found: " + getFullPath(file) + "  (" + fileDescription + ")");
	}

	public static StringBuilder formatIp(int ip4, StringBuilder sb) {
		return formatIp(intToIp4(ip4), sb);
	}

	public static class TrustAllManager implements javax.net.ssl.X509TrustManager, HostnameVerifier {
		private static final TrustAllManager INSTANCE = new TrustAllManager();
		private static final TrustAllManager[] ARRAY_INSTANCE = new TrustAllManager[] { INSTANCE };

		public static TrustAllManager[] getArrayInstance() {
			return TrustAllManager.ARRAY_INSTANCE;
		}

		@Override
		public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
		}
		@Override
		public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
		}
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	public static void trustAllCertificates(HttpsURLConnection conn) {
		try {
			final SSLContext sslContext = SSLContext.getInstance(TLS);
			sslContext.init(null, TrustAllManager.ARRAY_INSTANCE, new java.security.SecureRandom());
			final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
			conn.setSSLSocketFactory(sslSocketFactory);
			conn.setHostnameVerifier(TrustAllManager.INSTANCE);

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	public static void ensureReadable(File path) {
		if (!path.isFile())
			throw new RuntimeException("File not found: " + getFullPath(path));
		else if (!path.canRead())
			throw new RuntimeException("File exists, but not readable: " + getFullPath(path));
	}

	public static void moveForce(File src, File dst) {
		if (src.renameTo(dst))
			return;
		boolean r = src.canRead();
		boolean w = src.canWrite();
		src.setReadable(true);
		src.setWritable(true);
		if (src.renameTo(dst)) {
			src.setReadable(r);
			src.setWritable(w);
			return;
		}
		if (!src.getParentFile().canWrite()) {
			src.getParentFile().setWritable(true);
			if (src.renameTo(dst)) {
				src.setReadable(r);
				src.setWritable(w);
				src.getParentFile().setWritable(false);
				return;
			} else
				src.getParentFile().setWritable(false);
		}
		try {
			copy(src, dst, (int) MH.clip(src.length(), 1, 1024 * 1024), true);
			deleteForce(src);
			return;
		} catch (IOException e) {
			throw new RuntimeException("Could not move: " + getFullPath(src) + " ==> " + getFullPath(dst), e);
		}
	}

	public static void copy(File src, File dst, int blockSize, boolean copyAttributes) throws IOException {
		try {
			if (src.isDirectory()) {
				if (!dst.mkdir())
					throw new IOException("Could not create directory");
				String[] list = src.list();
				if (list != null) {
					for (String name : list) {
						copy(new File(src, name), new File(dst, name), blockSize, copyAttributes);
					}
				}

			} else {
				FastBufferedInputStream in = null;
				FastBufferedOutputStream out = null;
				try {
					in = new FastBufferedInputStream(new FileInputStream(src), blockSize);
					out = new FastBufferedOutputStream(new FileOutputStream(dst), blockSize);
					pipe(in, out, new byte[blockSize], false);
					if (copyAttributes) {
						dst.setExecutable(src.canExecute());
						dst.setReadable(src.canRead());
						dst.setWritable(src.canWrite());
						dst.setLastModified(src.lastModified());
					}
				} finally {
					IOH.close(in);
					IOH.close(out);
				}
			}
		} catch (Exception e) {
			throw new IOException("Error copying: " + getFullPath(src) + " ==> " + getFullPath(dst), e);
		}
	}

	public static long cksum(byte[] data) {
		return Cksum.cksum(data);
	}

	public static int getFileExtensionOffset(String uri) {
		if (SH.isnt(uri))
			return -1;
		int start = uri.indexOf('.');
		if (start == -1 || start == uri.length() - 1 || uri.indexOf('/', start) != -1 || uri.indexOf('\\', start) != -1)
			return -1;
		return start + 1;
	}

	public static char getSeparatorChar(String path) {
		if (SH.indexOf(path, File.separatorChar, 0) > -1)
			return File.separatorChar;
		else if ('\\' != File.separatorChar && SH.indexOfFirst(path, 0, '\\') > 0)
			return '\\';
		else if ('/' != File.separatorChar && SH.indexOfFirst(path, 0, '/') > 0)
			return '/';
		else
			return File.separatorChar;
	}
	public static String getFileExtension(String uri) {
		int start = getFileExtensionOffset(uri);
		return start == -1 ? null : uri.substring(start);
	}
	public static URL getURL(Package pakage, String fileName) {
		String path = pakage.getName().replace('.', '/') + "/" + fileName;
		URL stream = IOH.class.getClassLoader().getResource(path);
		return stream;
	}
	public static URL getURL(Class c) {
		return getURL(c.getPackage(), c.getSimpleName() + ".class");
	}

	public static class ZipEntryWithData {

	}

	public static Map<String, Tuple2<ZipEntry, byte[]>> unzip(ZipInputStream in) throws IOException {
		Map<String, Tuple2<ZipEntry, byte[]>> r = new HashMap<String, Tuple2<ZipEntry, byte[]>>();
		ZipEntry next;
		FastByteArrayDataOutputStream tmp = new FastByteArrayDataOutputStream();
		byte buf[] = new byte[2048];
		while ((next = in.getNextEntry()) != null) {
			byte[] data;
			if (next.isDirectory())
				data = null;
			else {
				IOH.pipe(in, tmp, buf);
				data = tmp.toByteArray();
				tmp.reset();
			}
			r.put(next.getName(), new Tuple2<ZipEntry, byte[]>(next, data));
		}
		return r;
	}
	public static Map<String, Tuple2<ZipEntry, byte[]>> unzip(byte[] zip) throws IOException {
		return unzip(new ZipInputStream(new FastByteArrayInputStream(zip)));
	}
	public static void archiveToZip(List<File> infiles, File outZipfile, boolean deleteAfter) throws FileNotFoundException, IOException {
		for (File f : infiles)
			if (!f.exists())
				throw new IOException("File not found: " + IOH.getFullPath(f));
		if (outZipfile.exists())
			throw new IOException("File already exists: " + IOH.getFullPath(outZipfile));
		final ZipOutputStream out = new ZipOutputStream(new FastBufferedOutputStream(new FileOutputStream(outZipfile), 10240));
		for (File f : infiles) {
			ZipEntry ze = new ZipEntry(f.getName());
			LH.info(log, "Archiving ", IOH.getFullPath(f), " ==> ", IOH.getFullPath(outZipfile));
			out.putNextEntry(ze);
			readData(f, out);
			out.closeEntry();
		}
		out.flush();
		out.close();
		if (deleteAfter)
			for (File f : infiles) {
				IOH.delete(f);
			}
	}

	public static byte[] deflate(byte[] data) {
		return deflate(data, Deflater.DEFLATED, true);
	}
	public static byte[] deflate(byte[] data, int level, boolean nowrap) {
		if (data == null)
			return null;
		try {
			final FastByteArrayOutputStream buf = new FastByteArrayOutputStream();
			DeflaterOutputStream deflaterStream = new DeflaterOutputStream(buf, new Deflater(level, nowrap));
			deflaterStream.write(data);
			deflaterStream.finish();
			byte[] r = buf.toByteArray();
			deflaterStream.close();
			return r;

		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}
	public static byte[] inflate(byte[] data) {
		return inflate(data, true);
	}
	public static byte[] inflate(byte[] data, boolean nowrap) {
		if (data == null)
			return null;
		try {
			final FastByteArrayInputStream buf = new FastByteArrayInputStream(data);
			InflaterInputStream s = new InflaterInputStream(buf, new Inflater(nowrap));
			byte[] r = readData(s);
			s.close();
			return r;
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}
	public static String getFullPathAndCheckSum(File f) {
		StringBuilder tmp = new StringBuilder();
		tmp.append(IOH.getFullPath(f));
		try {
			if (!f.exists())
				return tmp.append(" (missing)").toString();
			else if (!f.canRead())
				return tmp.append(" (unreadable)").toString();
			else if (f.isDirectory())
				return tmp.append(" (directory)").toString();
			byte[] data = IOH.readData(f);
			return tmp.append(" (cksum  ").append(IOH.cksum(data)).append(' ').append(data.length).append(')').toString();
		} catch (Exception e) {
			return tmp.append('(').append(OH.getSimpleClassName(e)).append(')').toString();
		}
	}

	public static String getFileDirectoryLinux(String path) {
		path = toUnixFormatForce(path);
		return SH.beforeLast(path, '/') + '/';
	}

	// Return the relative location of the right path to the left, assumes right is in a folder or subfolder the the left, returns null if right can't be found
	public static String getRelativePathLinuxSimple(String left, String right) {
		String l = toUnixFormatForce(left);
		String r = toUnixFormatForce(right);

		String dir = null;
		if (SH.endsWith(l, '/')) // Is folder, ex: /folder/
			dir = l;
		else if (SH.startsWith(r, l + '/')) { // Is folder2 ex: /abc/folder
			dir = l + '/';
		} else { // Is a file, ex: /abc/file
			dir = SH.beforeLast(l, '/');
		}

		if (!SH.startsWith(r, dir))
			return null;

		String afterRight = SH.afterFirst(r, dir);

		if (SH.startsWith(afterRight, '/'))
			afterRight = SH.substring(afterRight, 1, afterRight.length());

		return afterRight;

	}
	// This will attempt to get the relative location using `..`
	public static String getRelativePathLinux(String left, String right) {
		String l = toUnixFormatForce(left);
		String r = toUnixFormatForce(right);

		String common = SH.commonPrefix(l, r); //file/xyz/efg/aa/ //file/xyz/efg/111
		String afterLeft = SH.afterFirst(l, common);
		if (SH.startsWith(afterLeft, '/'))
			afterLeft = SH.substring(afterLeft, 1, afterLeft.length());
		String afterRight = SH.afterFirst(r, common);
		if (SH.startsWith(afterRight, '/'))
			afterRight = SH.substring(afterRight, 1, afterRight.length());
		int goUpDirCount = SH.is(afterLeft) ? SH.count('/', afterLeft) : 0;

		String relativeLoc = SH.repeat("../", goUpDirCount) + afterRight;
		return relativeLoc;

	}

	@Deprecated
	public static String toLinuxPath(String path) {
		return SH.replaceAll(path, '\\', '/');
	}
	public static File appendExtension(File f, String ext) {
		return new File(f.getParent(), f.getName() + ext);
	}

	//When joining two paths, make sure there is exactly one slash between then
	public static String join(String s1, String s2) {
		if ("".equals(s2))
			return s1;
		else if ("".equals(s1))
			return s2;
		boolean ends = s1.endsWith("/");
		boolean starts = s2.startsWith("/");
		if (ends) {
			if (starts)
				return s1 + s2.substring(1);
			else
				return s1 + s2;
		} else if (starts)
			return s1 + s2;
		return s1 + '/' + s2;
	}
	public static boolean isSecureChildPath(String uri) {
		if (SH.startsWith(uri, '~'))
			return false;
		if (SH.indexOf(uri, "..", 0) != -1)
			if (uri.startsWith("../") || SH.indexOf(uri, "/../", 0) != 1 || uri.endsWith("/.."))
				return false;
		return true;
	}
	
	public static boolean renameFile(File f, String newName) {
		// strips the path from file and appends the new name to the path
		String path = f.getAbsolutePath();
		int fns = path.lastIndexOf(getSeparatorChar(path));
		String newPath = SH.splice(path, fns + 1, f.getName().length(), newName);
		Path source = Paths.get(path);
		Path target = Paths.get(newPath);
		try {
			Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
			return true;
		} catch (IOException e) {
			LH.warning(log, "Unable to rename file: " + e.getCause().toString());
			return false;
		}
	}
}
