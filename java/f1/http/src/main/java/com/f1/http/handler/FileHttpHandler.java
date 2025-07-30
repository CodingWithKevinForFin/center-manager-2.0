package com.f1.http.handler;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.impl.BasicHttpRequestResponse;
import com.f1.utils.CachedFile;
import com.f1.utils.CachedResource;
import com.f1.utils.ContentType;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;

public class FileHttpHandler extends AbstractHttpHandler {
	public static Logger log = Logger.getLogger(FileHttpHandler.class.getName());
	private String filePath;
	private CachedFile cachedFile;
	private byte[] mimeType;
	private CachedResource cachedResource;

	public FileHttpHandler(boolean isResource, String newFilePath, int cacheTimeMs) {
		this.filePath = newFilePath;
		if (isResource) {
			this.cachedResource = new CachedResource(newFilePath, cacheTimeMs);
			if (this.cachedResource.getData().getBytes() == null)
				throw new RuntimeException("Resource not found: " + newFilePath);
		} else {
			final File file = new File(getFilePath());
			this.cachedFile = new CachedFile(file, cacheTimeMs);
			if (this.cachedFile.getData().getBytes() == null)
				throw new RuntimeException("File not found: " + IOH.getFullPath(file));
		}
		this.mimeType = ContentType.getTypeByFileExtension(SH.afterLast(getFilePath(), '.'), ContentType.BINARY).getMimeTypeAsBytes();
	}
	public String getFilePath() {
		return this.filePath;
	}
	public CachedFile getCachedFile() {
		return this.cachedFile;
	}
	public byte[] getMimeType() {
		return this.mimeType;
	}
	@Override
	public void handle(HttpRequestResponse req) throws IOException {
		try {
			req.setContentTypeAsBytes(getMimeType());
			final byte[] bytes;
			if (cachedResource != null) {
				bytes = cachedResource.getData().getBytes();
			} else {
				bytes = cachedFile.getData().getBytes();
			}
			if (bytes == null)
				req.setResponseType(BasicHttpRequestResponse.BYTES_HTTP_404_NOT_FOUND);
			else
				req.getOutputStream().write(bytes);
		} catch (Exception e) {
			req.setResponseType(HttpRequestResponse.HTTP_404_NOT_FOUND);
			LH.warning(log, "Exception found getting custsom login image", e);
		}
	}
}
