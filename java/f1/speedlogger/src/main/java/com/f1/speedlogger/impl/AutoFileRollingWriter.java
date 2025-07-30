/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.f1.utils.IOH;

public class AutoFileRollingWriter extends Writer {
	private File currentFile;
	private FileWriter inner;
	private long size;
	private final long maxFileSize;
	private final int maxFilesCount;
	private final String fileName;

	public AutoFileRollingWriter(long maxFileSize, int maxFilesCount, String fileName, boolean append) throws IOException {
		this.size = 0;
		if (maxFileSize <= 0)
			throw new NegativeArraySizeException("max file size: " + maxFileSize);
		if (maxFilesCount <= 0)
			throw new NegativeArraySizeException("max files count: " + maxFilesCount);
		this.maxFileSize = maxFileSize;
		this.maxFilesCount = maxFilesCount;
		this.fileName = fileName;
		File f = new File(fileName);
		if (!f.getAbsoluteFile().getParentFile().exists()) {
			IOH.ensureDir(f.getAbsoluteFile().getParentFile());
		}
		if (!append)
			rollFiles();
		else {
			size = f.length();
			inner = new FileWriter(f, true);
			currentFile = f;
		}
	}

	private boolean rollingHasFailed = false;

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		try {
			if ((size += len) >= maxFileSize && !rollingHasFailed) {
				rollFiles();
			}
		} catch (RollFileException e) {
			rollingHasFailed = true;
			try {
				inner = new FileWriter(currentFile, false);
				inner.write(cbuf, off, len);
				System.err.println("WRN: An error occured while rolling log files. Continuing to write to existing file. Internal message: " + e.getMessage());
			} catch (Exception e2) {
				e2.printStackTrace(System.err);
				System.err.println("SVR: Error occured while trying to recover from file rolling issue");
				throw e;
			}
		}

		inner.write(cbuf, off, len);
	}
	private void rollFiles() throws IOException {
		if (inner != null)
			inner.close();
		File f = SpeedLoggerUtils.roleFile(fileName, maxFilesCount);
		try {
			currentFile = f;
			inner = new FileWriter(currentFile, false);
		} catch (Exception e) {
			throw new IOException("error opening file for writing: " + f, e);
		}
		size = 0;
	}

	@Override
	public void close() throws IOException {
		inner.close();
		inner = null;

	}

	@Override
	public void flush() throws IOException {
		inner.flush();
	}

	public FileWriter getInner() {
		return inner;
	}

	public long getSize() {
		return size;
	}

	public long getMaxFileSize() {
		return maxFileSize;
	}

	public int getMaxFilesCount() {
		return maxFilesCount;
	}

	public String getFileName() {
		return fileName;
	}

}
