package com.f1.http.handler;

import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

import com.f1.http.HttpRequestResponse;
import com.f1.utils.CachedResource;
import com.f1.utils.ContentType;
import com.f1.utils.CopyOnWriteHashMap;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;

public class ResourceHttpHandler extends AbstractHttpHandler {

	private ConcurrentMap<String, FileInstance> files = new CopyOnWriteHashMap<String, FileInstance>();
	private String baseUrl;
	private String base;
	private TextMatcher matcher;
	private long cacheTime;
	private String indexPage;

	public ResourceHttpHandler(String base, long cacheTimeMs, String indexPage) {
		if (base.startsWith("/"))
			throw new IllegalArgumentException("resource path cannot be absolute: " + base);
		this.baseUrl = baseUrl;
		this.base = base;
		this.matcher = matcher;
		this.cacheTime = cacheTimeMs;
		this.indexPage = indexPage;
	}

	protected FileInstance getFileInstance(HttpRequestResponse request) {
		String uri = request.getRequestUri();
		FileInstance file = files.get(uri);
		if (file == null) {
			String f = SH.path('/', base, uri.substring(baseUrl.length()));
			if (f.endsWith("/")) {
				f = f + indexPage;
				byte[] mimeType = ContentType.getTypeByFileExtension(SH.afterLast(indexPage, '.')).getMimeTypeAsBytes();
				files.put(uri, file = newFileInstance(new CachedResource(f, cacheTime), mimeType));
			} else {
				byte[] mimeType = ContentType.getTypeByFileExtension(SH.afterLast(uri, '.')).getMimeTypeAsBytes();
				files.put(uri, file = newFileInstance(new CachedResource(f, cacheTime), mimeType));
			}
		}
		return file;
	}

	protected FileInstance newFileInstance(CachedResource cachedFile, byte[] mimeType) {
		return new FileInstance(cachedFile, mimeType);
	}

	@Override
	public void handle(HttpRequestResponse request) throws IOException {
		super.handle(request);
		FileInstance file = getFileInstance(request);
		if (file == null) {
			request.setResponseType(HttpRequestResponse.HTTP_404_NOT_FOUND);
		} else if (file.file.getModifiedMs() < request.getIfModifiedSince()) {
			request.setResponseType(HttpRequestResponse.HTTP_304_NOT_MODIFIED);
		} else {
			byte[] data = file.file.getData().getBytes();
			if (data == null) {
				request.setResponseType(HttpRequestResponse.HTTP_404_NOT_FOUND);
			} else {
				request.setContentTypeAsBytes(file.mimeType);
				request.getOutputStream().write(data);
			}
		}
	}

	protected static class FileInstance {
		public final byte[] mimeType;
		public final CachedResource file;

		public FileInstance(CachedResource file, byte[] mimeType) {
			this.file = file;
			this.mimeType = mimeType;
		}
	}
}
