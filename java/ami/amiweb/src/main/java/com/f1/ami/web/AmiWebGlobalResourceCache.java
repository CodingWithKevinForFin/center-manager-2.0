package com.f1.ami.web;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.f1.ami.web.amiscript.AmiWebFileSystem;
import com.f1.utils.Cksum;
import com.f1.utils.ImageHelper;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.WebImage;

public class AmiWebGlobalResourceCache {

	public static final String SERVICE_ID = "GLOBAL_RESOURCE_CACHE";
	final private long cacheTtl;
	private AmiWebFile webRoot;
	final private AmiWebFileSystem fileSystem;

	final private ConcurrentHashMap<String, AmiWebResource> cache = new ConcurrentHashMap<String, AmiWebResource>();

	public AmiWebGlobalResourceCache(PropertyController props, AmiWebFileSystem fileSystem) {
		this.cacheTtl = SH.parseDurationTo(props.getOptional(AmiWebProperties.PROPERTY_AMI_WEB_RESOURCE_CACHE_TTL, "60 SECONDS"), TimeUnit.MILLISECONDS);
		this.fileSystem = fileSystem;
	}
	public void setRoot(AmiWebFile webRoot) {
		this.webRoot = webRoot;
	}

	public void clear() {
		this.cache.clear();
	}

	public AmiWebResource get(String name) {
		long now = System.currentTimeMillis();
		AmiWebResource r = this.cache.get(name);
		if (r != null) {
			if (now - r.getCachedAt() < cacheTtl)
				return r;
			AmiWebResource r2 = getUnderlying(now, name, r);
			if (r2 == null)
				this.cache.remove(name, r);
			else
				this.cache.put(name, r2);
			return r2;
		} else {
			AmiWebResource r2 = getUnderlying(now, name, null);
			if (r2 != null)
				this.cache.put(name, r2);
			return r2;
		}
	}

	private AmiWebResource getUnderlying(long now, String name, AmiWebResource existing) {
		AmiWebFile file = this.webRoot;
		for (String s : SH.split('/', name)) {
			file = this.fileSystem.getFile(file, s);
			if (!file.exists())
				break;
		}
		if (file.exists() && file.isFile()) {
			if (existing != null && existing.getModified() == file.lastModified() && file.length() == existing.getSize()) {
				existing.setCachedAt(now);
				return existing;
			}
			try {
				int w = -1, h = -1;
				byte[] data = file.readBytes();
				long cksum = Cksum.cksum(data);
				WebImage img = ImageHelper.readImage(file.getName(), data);
				if (img != null) {
					w = img.getWidth();
					h = img.getHeight();
				}
				return new AmiWebResource(now, false, name, data.length, file.lastModified(), cksum, w, h, data);
			} catch (IOException e) {
			}
		}
		return null;
	}

}
