package com.f1.utils.ftp.client;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;

public class FtpUtils {

	private static final Map<String, Byte> TYPES = new HashMap<String, Byte>();
	private static final Logger log = LH.get();

	public static final int SYNC_OPTION_IGNORE_MODIFIED_TIME = 1;
	public static final int SYNC_OPTION_RECURSE = 2;
	public static final int SYNC_OPTION_REMOVE_LOCAL_FILES_NOT_ON_SERVER = 4;
	public static final int SYNC_OPTION_CONTINUE_ON_ERROR = 8;

	static {
		CH.m(TYPES, "pdir", FtpFile.TYPE_PARENT_DIR, "cdir", FtpFile.TYPE_CHILD_DIR, "file", FtpFile.TYPE_FILE, "dir", FtpFile.TYPE_CHILD_DIR);
	}

	public static FtpFile parseFile(String line, DateFormat dateParser) {
		FtpFile r = new FtpFile();
		String[] parts = SH.split(';', line);
		r.setName(AH.last(parts).substring(1));
		for (int i = 0; i < parts.length - 1; i++) {
			String part = parts[i];
			if (part.length() == 0)
				continue;
			switch (part.charAt(0)) {
				case 'T':
				case 't': {
					String value = SH.stripPrefixIgnoreCase(part, "Type=", false);
					if (value != part)
						r.setType(parseFileType(value));
					break;
				}
				case 'S':
				case 's': {
					String value = SH.stripPrefixIgnoreCase(part, "Size=", false);
					if (value != part)
						r.setSize(SH.parseLong(value));
					break;
				}
				case 'M':
				case 'm': {
					String value = SH.stripPrefixIgnoreCase(part, "Modify=", false);
					if (OH.ne(value, part))
						try {
							r.setDate(dateParser.parse(value).getTime());
						} catch (ParseException e) {
							//ignore bad dates
						}
					break;
				}
				case 'P':
				case 'p': {
					String value = SH.stripPrefixIgnoreCase(part, "Perm=", false);
					if (OH.ne(value, part)) {
						r.setPermissions(value);
					}
					break;
				}

			}
		}
		return r;
	}

	public static byte parseFileType(String type) {
		Byte r = TYPES.get(type);
		return r == null ? FtpFile.TYPE_UNKNOWN : r.byteValue();
	}

	public static void syncFilesFrom(FtpClient client, String clientPath, TextMatcher fileMask, File destinationDirectory, int options, List<File> changedFilesSync)
			throws IOException {
		final boolean ignoreModified = MH.allBits(options, SYNC_OPTION_IGNORE_MODIFIED_TIME);
		final boolean recurse = MH.allBits(options, SYNC_OPTION_RECURSE);
		final boolean removeLocally = MH.allBits(options, SYNC_OPTION_REMOVE_LOCAL_FILES_NOT_ON_SERVER);
		final boolean continueOnError = MH.allBits(options, SYNC_OPTION_CONTINUE_ON_ERROR);
		if (!IOH.isDirectory(destinationDirectory))
			throw new RuntimeException("directory not found: " + IOH.getFullPath(destinationDirectory));

		List<FtpFile> files = client.listFiles(clientPath);
		for (FtpFile file : files) {
			if (file.getType() == FtpFile.TYPE_FILE) {
				try {
					File localFile = new File(destinationDirectory, file.getName());
					if (localFile.exists() && localFile.length() == file.getSize() && (localFile.lastModified() == file.getDate() || ignoreModified))
						continue;
					LH.info(log, Level.INFO, " syncing ftp file: " + IOH.getFullPath(localFile));
					byte[] data = client.getBinary(file.getName());
					if (data.length != file.getSize())
						throw new FtpException("bad file size for " + file.getName() + ", expected: " + file.getSize() + ", actual: " + data.length);
					changedFilesSync.add(localFile);
					IOH.writeData(localFile, data);
					if (!localFile.setLastModified(file.getDate()) && !continueOnError) {
						throw new IOException("Unable to set last modified");
					}
				} catch (FtpException e) {
					if (!continueOnError)
						throw e;
					else
						log.info("Error processing file " + file.getName() + ": " + e.getMessage());
				}
			}
		}
		if (recurse) {
			for (FtpFile file : files) {
				if (file.getType() == FtpFile.TYPE_CHILD_DIR) {
					if (!".".equals(file.getName())) {
						File localDir = new File(destinationDirectory, file.getName());
						if (IOH.isFile(localDir)) {
							LH.info(log, Level.WARNING, "can not sync ftp directory, local file exists: " + IOH.getFullPath(localDir));
							continue;
						} else if (!IOH.isDirectory(localDir)) {
							IOH.ensureDir(localDir);
							changedFilesSync.add(localDir);
						}
						client.cd(file.getName());
						syncFilesFrom(client, clientPath, fileMask, localDir, options, changedFilesSync);
						client.cdUp();
					}
				}
			}
		}
		if (removeLocally) {
			final Set<String> remoteFileNames = new HashSet<String>(files.size());
			for (FtpFile file : files)
				remoteFileNames.add(file.getName());
			File[] listFiles = destinationDirectory.listFiles();
			if (listFiles != null) {
				for (File localFile : listFiles)
					if (localFile.isFile() && !remoteFileNames.contains(localFile.getName())) {
						IOH.delete(localFile);
						changedFilesSync.add(localFile);
					}
			}

		}
	}
	public static void main(String a[]) throws IOException {
		FtpClient ftp = new FtpClient("192.168.3.21", 21, "test", "123456");
		List<File> changed = new ArrayList<File>();
		syncFilesFrom(ftp, null, SH.m("*"), new File("/home/rcooke/ftpsync"), SYNC_OPTION_RECURSE | SYNC_OPTION_REMOVE_LOCAL_FILES_NOT_ON_SERVER, changed);
		for (File file : changed)
			if (file.exists())
				System.out.println("+ " + IOH.getFullPath(file));
			else
				System.out.println("- " + IOH.getFullPath(file));

	}
}
