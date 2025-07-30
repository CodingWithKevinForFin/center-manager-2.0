package com.f1.ami.webmanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.msg.AmiFileMessage;
import com.f1.ami.amicommon.msg.AmiWebManagerGetFileRequest;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;

public class AmiWebManagerController {

	private static final String PERMISSION_DENIED = "Permission Denied (" + AmiWebManagerProperties.PROPERTY_AMI_WEBMANAGER_MAPPING_STRICT + "): ";

	private static final Logger log = LH.get();

	private static final Comparator<? super Tuple2<String, String>> COMPARATOR = new Comparator<Tuple2<String, String>>() {

		@Override
		public int compare(Tuple2<String, String> o1, Tuple2<String, String> o2) {
			return -OH.compare(o1.getA().length(), o2.getA().length());
		}
	};

	private final String pwdActual;
	private final String pwdLogical;
	private final Tuple2<String, String>[] mappings;

	private final boolean strict;

	public AmiWebManagerController(boolean strict, String pwdLogical, String pwdActual, Map<String, String> logical2actual) {
		pwdLogical = trimTrailingSlash(SH.trim(pwdLogical));
		pwdActual = IOH.getFullPath(new File(SH.trim(pwdActual)));

		this.pwdActual = pwdActual;
		this.pwdLogical = pwdLogical;
		this.strict = strict;
		logical2actual = new LinkedHashMap<String, String>(logical2actual);

		String existingPwdActual = logical2actual.get(pwdLogical);
		if (existingPwdActual != null && OH.ne(pwdActual, existingPwdActual))
			throw new RuntimeException("supplied path of working directory conflicts with mount points: " + pwdLogical + " ==> " + existingPwdActual + " vs " + pwdActual);
		logical2actual.put(pwdLogical, pwdActual);

		int n = 0;
		Set<String> existing = new HashSet<String>();
		this.mappings = new Tuple2[logical2actual.size()];
		LH.info(log, "Relative paths will be mapped to :  ", pwdActual);
		for (Entry<String, String> e : logical2actual.entrySet()) {
			String key = trimTrailingSlash(SH.trim(e.getKey()));
			String target = IOH.getFullPath(new File(SH.trim(e.getValue())));
			if (!existing.add(key))
				throw new RuntimeException("Duplicate mapping point: " + key);
			this.mappings[n++] = new Tuple2<String, String>(key, target);
			LH.info(log, "Established Root Mapping: ", key, " -> ", target);
		}
		Arrays.sort(this.mappings, COMPARATOR);
	}
	static private String trimTrailingSlash(String value) {
		while (value.endsWith("/."))
			value = value.substring(0, value.length() - 2);
		while (value.endsWith("/") && value.length() > 1)
			value = value.substring(0, value.length() - 1);
		while (value.indexOf("//") != -1)
			value = SH.replaceAll(value, "//", "/");
		while (value.indexOf("/./") != -1)
			value = SH.replaceAll(value, "/./", "/");
		return value;
	}
	public AmiWebManagerFile newFile(String a, String b) throws IOException {
		if (a == null)
			return newFile(b);
		return newFile(a + '/' + b);
	}
	public AmiWebManagerFile newFile(String fullPath) throws IOException {
		String path = SH.replaceAll(fullPath, '\\', '/');
		if (EH.isWindows()) {
			if (path.startsWith("/") && path.length() > 2 && path.charAt(2) == ':')
				path = path.substring(1);//replace /z:/ with z:/
			if (path.length() > 1 && path.charAt(1) == ':') {
				char driveLetter = path.charAt(0);
				if (Character.isLowerCase(driveLetter))
					path = SH.toUpperCase(driveLetter) + path.substring(1);//make sure drive letter is upper case
			}
		}
		path = trimTrailingSlash(path);
		if (path.startsWith("~")) {
			if (strict)
				throw new IOException(PERMISSION_DENIED + fullPath);
			else
				return new AmiWebManagerFile(new File(path), "~", path);
		}
		while (path.startsWith("./"))
			path = path.substring(2);
		if (path.startsWith("..") && strict)
			throw new IOException(PERMISSION_DENIED + fullPath);
		for (Tuple2<String, String> mapping : mappings)
			if (isMount(path, mapping.getA())) {
				String remaining = path.substring(mapping.getA().length());
				if (strict) {
					String canonical = IOH.getCanonical(remaining);
					if (canonical.startsWith("/.."))
						throw new IOException(PERMISSION_DENIED + path);
				}
				return new AmiWebManagerFile(new File(mapping.getB() + remaining), mapping.getA(), trimTrailingSlash(path));
			}
		if (path.startsWith("/"))
			throw new IOException("Mapping not found for: " + fullPath);
		return new AmiWebManagerFile(new File(IOH.getCanonical(this.pwdActual + '/' + path)), this.pwdLogical, this.pwdLogical + '/' + trimTrailingSlash(path));
	}
	static public boolean isMount(String file, String mountPath) {
		if (file != null && file.startsWith(mountPath))
			return mountPath.length() == file.length() || file.charAt(mountPath.length()) == '/' || mountPath.equals("/");
		return false;
	}
	public AmiWebManagerFile[] listRoots() {
		AmiWebManagerFile[] r = new AmiWebManagerFile[mappings.length];
		for (int i = 0; i < this.mappings.length; i++)
			r[i] = new AmiWebManagerFile(new File(this.mappings[i].getB()), null, this.mappings[i].getA());
		return r;
	}

	public void toFile(String invokedBy, AmiFileMessage sink, AmiWebManagerFile awFile, short options) throws IOException {
		File file = awFile.getFile();
		boolean includeFileNames = MH.anyBits(options, AmiWebManagerGetFileRequest.INCLUDE_FILE_NAMES);
		boolean includeFiles = MH.anyBits(options, AmiWebManagerGetFileRequest.INCLUDE_FILES);
		boolean includeText = MH.anyBits(options, AmiWebManagerGetFileRequest.INCLUDE_DATA);
		LH.info(log, "User ", invokedBy, " getting ", awFile, includeText ? " INCLUDE_TEXT" : "", includeFileNames ? " INCLUDE_CHILD_NAMES" : "",
				includeFiles ? " INCLUDE_CHILD_FILES" : "");
		File abf = file.getAbsoluteFile();
		if (abf != null) {
			sink.setAbsolutePath(awFile.getFullPath());
			sink.setParentPath(awFile.getParentFullPath());
		}
		sink.setFullPath(awFile.getFullPath());
		sink.setLastModified(file.lastModified());
		sink.setLength(file.length());
		sink.setName(file.getName());
		sink.setPath(file.getPath());
		short flags = 0;
		if (file.exists())
			flags |= AmiFileMessage.FLAG_EXISTS;
		if (file.isFile())
			flags |= AmiFileMessage.FLAG_IS_FILE;
		if (file.isHidden())
			flags |= AmiFileMessage.FLAG_IS_HIDDEN;
		if (file.isDirectory())
			flags |= AmiFileMessage.FLAG_IS_DIRECTORY;
		if (file.canExecute())
			flags |= AmiFileMessage.FLAG_CAN_EXECUTE;
		if (file.canRead())
			flags |= AmiFileMessage.FLAG_CAN_READ;
		if (file.canWrite())
			flags |= AmiFileMessage.FLAG_CAN_WRITE;
		sink.setFlags(flags);
		if (includeFiles) {
			List<AmiFileMessage> files = new ArrayList<AmiFileMessage>();
			for (AmiWebManagerFile i : awFile.listFiles()) {
				AmiFileMessage sink2 = (AmiFileMessage) sink.nw();
				toFile(invokedBy, sink2, i, (short) 0);
				files.add(sink2);
			}
			sink.setFiles(files);
		}
		if (file.isDirectory() && includeFileNames) {
			sink.setFileNames(file.list());
		}
		if (file.isFile() && includeText)
			if (IOH.isSymlink(file))
				sink.setData(IOH.readData(new File(IOH.getFullPath(file))));
			else
				sink.setData(IOH.readData(file));
	}

	public static void main(String a[]) throws IOException {
		//		AmiWebManagerController c = new AmiWebManagerController(true, "/home", (Map) CH.m("/test/123", "./test", "/test", "/home/rcooke/", "/", "/", "blah", "test.txt", "c:",
		//				"/home/share"));
		AmiWebManagerController c = new AmiWebManagerController(true, "/opt/packages/web_balancer_test/amiwebbalancer", "/blah", (Map) CH.m("/home/rcooke", "/home/rcooke/"));
		for (AmiWebManagerFile f : c.listRoots()) {
			System.out.println("ROOT fullpath:" + f);
		}
		test(c, ".");
		test(c, "/home/rcooke/");
		test(c, "/home/rcooke/this/is/a/test");
		test(c, "this/is/a/test");
		test(c, "/test/../george/sftp@3forge.com");
		test(c, "c:/files123.txt");
		test(c, "blah");
		test(c, "./blah");
		test(c, "rcooke/sftp@3forge.com");
		test(c, "/test/123/share");
		test(c, "/test/123/share/");
		test(c, "/test/123/share/../tmp");
		test(c, "/test/sftp@3forge.com");
		//						test(c, "../test/sftp@3forge.com");
		test(c, "../../test/sftp@3forge.com");
		test(c, "/home/george");
		test(c, "/home/george/asdf.txt");
	}
	private static void test(AmiWebManagerController c, String string) throws IOException {
		System.out.println();
		System.out.println();
		test(c, "", string);
	}
	private static void test(AmiWebManagerController c, String tab, String string) throws IOException {
		if (string == null)
			return;
		try {
			AmiWebManagerFile f = c.newFile(string);
			System.out.println(tab + "RESULT FOR " + string + " ==> " + f);
			test(c, tab + "  ", f.getParentFullPath());
		} catch (Exception e) {
			System.out.println(tab + "Error for " + string);
			e.printStackTrace(System.out);
		}
	}
}
