/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web;

import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import com.f1.utils.CachedResource;
import com.f1.utils.CopyOnWriteHashMap;

public class BasicWebResourceManager implements WebResourceManager {
	private static final Logger log = Logger.getLogger(BasicWebResourceManager.class.getName());

	private ConcurrentMap<String, CachedResource> data = new CopyOnWriteHashMap<String, CachedResource>();
	final private long cacheTimeMs;
	final private String resourceBase;

	public BasicWebResourceManager(String resourceBase, long cacheTimeMs) {
		this.cacheTimeMs = cacheTimeMs;
		this.resourceBase = resourceBase;
	}

	@Override
	public boolean isResourcePattern(String url) {
		return !url.endsWith(".jsp") && !url.endsWith(".ajax");
	}

	@Override
	public byte[] getResource(String url) {
		CachedResource r = data.get(url);
		if (r == null)
			data.put(url, r = new CachedResource(resourceBase + "/" + url, cacheTimeMs));
		byte[] bytes = r.getData().getBytes();
		return bytes;
	}

}
