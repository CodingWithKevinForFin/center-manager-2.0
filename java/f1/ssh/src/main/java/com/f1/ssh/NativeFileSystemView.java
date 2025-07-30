package com.f1.ssh;

import java.io.File;

import org.apache.sshd.server.FileSystemView;
import org.apache.sshd.server.SshFile;
import org.apache.sshd.server.filesystem.NativeFileSystemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NativeFileSystemView implements FileSystemView {

	private final Logger LOG = LoggerFactory.getLogger(NativeFileSystemView.class);

	// the first and the last character will always be '/'
	// It is always with respect to the root directory.
	private String currDir;

	private String userName;

	private boolean caseInsensitive = false;

	/**
	 * Constructor - internal do not use directly, use
	 * {@link NativeFileSystemFactory} instead
	 */
	protected NativeFileSystemView(String userName) {
		this(userName, false);
	}

	/**
	 * Constructor - internal do not use directly, use
	 * {@link NativeFileSystemFactory} instead
	 */
	public NativeFileSystemView(String userName, boolean caseInsensitive) {
		if (userName == null) {
			throw new IllegalArgumentException("user can not be null");
		}

		this.caseInsensitive = caseInsensitive;

		currDir = System.getProperty("user.dir");
		this.userName = userName;

		// add last '/' if necessary
		LOG.debug("Native filesystem view created for user \"{}\" with root \"{}\"", userName, currDir);
	}

	/**
	 * Get file object.
	 */
	public SshFile getFile(String file) {
		return getFile(currDir, file);
	}

	public void setCurrDir(String currDir) {
		this.currDir = currDir;
	}

	public SshFile getFile(SshFile baseDir, String file) {
		return getFile(baseDir.getAbsolutePath(), file);
	}

	protected SshFile getFile(String dir, String file) {
		// get actual file object
		String physicalName = NativeSshFile.getPhysicalName("/", dir, file, caseInsensitive);
		File fileObj = new File(physicalName);

		// strip the root directory and return
		String userFileName = physicalName.substring("/".length() - 1);
		return new NativeSshFile(userFileName, fileObj, userName);
	}
}
