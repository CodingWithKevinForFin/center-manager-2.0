/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.io.File;
import java.io.IOException;

/**
 * Used to cache the contents of a file. Data can read as bytes or text. After creating the file, a call to {@link #getData()} will return a cached instance representing data in
 * the file now.
 * 
 * The Cache inner class will represent a snapshot at the time of it's instantiation. Even if the contents in the file change or the file is deleted / created the
 * {@link Cache#getText()} and {@link Cache#getBytes()} will always return the same value. The only thing that will change is {@link Cache#isOld()} will start to return true. To
 * ensure you have an updated cache either call {@link CachedFile#getData()} or {@link Cache#getUpdated()}.
 */
public class CachedFile {

	private static final long STABLE_DELAY_MS = 10;

	private final File file;
	private final long checkFrequencyMs;
	private volatile long lastModified = -2;
	private volatile Cache cache;
	private long lastCheck;

	public CachedFile(File file, long checkFrequencyMs) {
		if (file == null)
			throw new NullPointerException("file");

		this.file = file;
		this.checkFrequencyMs = checkFrequencyMs;
	}

	public File getFile() {
		return file;
	}

	public Cache getData() {
		try {
			clearIfOld();
			if (lastModified == -1)
				return cache;
			if (cache != null)
				return cache;
			for (int cnt = 1; cnt < 50; cnt++) {
				byte[] data = IOH.readData(file);
				if (data != null)
					cache = new Cache(data);
				clearIfOld();
				if (cache != null || lastModified == -1)
					return cache;
				else
					OH.sleep(cnt);// wait for file steady state;
			}
			throw new RuntimeException("seems that file is not steady: " + this);
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}

	private void clearIfOld() {
		if (checkFrequencyMs > 0) {
			long now = EH.currentTimeMillis();
			if (now < checkFrequencyMs + lastCheck)
				return;
			lastCheck = now;
		}
		long modified = getModifiedMs();

		// it has been loaded already, but the last modification was too
		// recent...
		if (lastModified != -2 && EH.currentTimeMillis() - modified < STABLE_DELAY_MS)
			return;
		if (modified != lastModified) {
			lastModified = modified;
			cache = lastModified == -1 ? new Cache(null) : null;
		}
	}

	public long getModifiedMs() {
		if (!file.isFile())
			return -1;
		return file.lastModified();
	}

	@Override
	public String toString() {
		return file.toString();

	}

	public class Cache {
		byte dataCache[];
		String textCache;

		private Cache(byte[] bytes) {
			this.dataCache = bytes;
			this.textCache = null;
		}
		private Cache(byte[] bytes, String text) {
			this.dataCache = bytes;
			this.textCache = text;
		}

		public byte[] getBytes() {
			return dataCache;
		}

		public String getText() {
			if (textCache == null && dataCache != null)
				textCache = IOH.toString(dataCache);
			return textCache;
		}

		public boolean isOld() {
			return this != getData();
		}

		public Cache getUpdated() {
			return getData();
		}

		public boolean exists() {
			return dataCache != null;
		}

		public CachedFile getFileCached() {
			return CachedFile.this;
		}
	}

	public void writeText(String text) throws IOException {
		byte[] bytes = text.getBytes();
		IOH.writeText(file, text);
		this.cache = new Cache(bytes, text);
		this.lastCheck = EH.currentTimeMillis();
		this.lastModified = getModifiedMs();
	}
	public void writeData(byte[] bytes) throws IOException {
		IOH.writeData(file, bytes);
		this.cache = new Cache(bytes);
		this.lastCheck = EH.currentTimeMillis();
		this.lastModified = getModifiedMs();
	}

}
