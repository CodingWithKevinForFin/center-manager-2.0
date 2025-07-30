/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.io.File;
import java.net.URL;

/**
 * Used to cache the contents of a file. Data can read as bytes or text. After creating the file, a call to {@link #getData()} will return a cached instance representing data in
 * the file now.
 * 
 * The Cache inner class will represent a snapshot at the time of it's instatiation. Even if the contents in the file change or the file is deleted / created the
 * {@link Cache#getText()} and {@link Cache#getBytes()} will always return the same value. The only thing that will change is {@link Cache#isOld()} will start to return true. To
 * ensure you have an updated cache either call {@link CachedResource#getData()} or {@link Cache#getUpdated()}.
 */
public class CachedResource {

	final private String resourceName;
	final private static int STATE_MODIFIED_UNSET = -2;
	final private static int STATE_MODIFIED_RESOURCE_UNAVAILABLE = -1;
	private volatile long lastModified = STATE_MODIFIED_UNSET;
	private volatile Cache cache;
	private long checkFrequencyMs, lastCheck;
	private long STABLE_DELAY_MS = 10;
	private File file;
	private boolean isJar;

	public CachedResource(String resourceName, long checkFrequencyMs) {
		this.resourceName = resourceName;
		this.checkFrequencyMs = checkFrequencyMs;
	}

	public String getResourceName() {
		return resourceName;
	}

	public Cache getData() {
		try {
			clearIfOld();
			if (lastModified == -1)
				return cache;
			if (cache != null)
				return cache;
			for (int cnt = 1; cnt < 50; cnt++) {
				byte[] data = null;
				try {
					// TODO: (Minor) seems that updated jars are not picked up.. still caches!
					data = isJar ? IOH.readDataFromResource(resourceName) : IOH.readData(file); // TODO:
					if (data == null && isJar)
						data = IOH.readDataFromResource2(resourceName);
				} catch (Exception e) {

				}
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
		IF: if (file == null || !file.isFile()) {
			final URL url = CachedResource.class.getClassLoader().getResource(resourceName);
			if (url != null) {
				isJar = "jar".equals(url.getProtocol());
				file = new File(isJar ? SH.beforeFirst(SH.afterFirst(SH.decodeUrl(url.getPath()), ':'), '!') : SH.decodeUrl(url.getFile()));
				if (file.isFile())
					break IF;
			} else {
				final URL url2 = CachedResource.class.getResource("/resources/" + resourceName);
				if (url2 != null) {
					isJar = "jar".equals(url2.getProtocol());
					file = new File(isJar ? SH.beforeFirst(SH.afterFirst(SH.decodeUrl(url2.getPath()), ':'), '!') : SH.decodeUrl(url2.getFile()));
					if (file.isFile())
						break IF;
				}
			}
			file = null;
			return -1;
		}
		return file.lastModified();
	}
	@Override
	public String toString() {
		return resourceName;

	}

	public class Cache {
		byte dataCache[];
		String textCache;

		private Cache(byte[] bytes) {
			this.dataCache = bytes;
			this.textCache = null;
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

		public CachedResource getFileCached() {
			return CachedResource.this;
		}
	}

}
