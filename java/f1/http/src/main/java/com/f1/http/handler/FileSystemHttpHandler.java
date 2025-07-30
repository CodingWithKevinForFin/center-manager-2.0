package com.f1.http.handler;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpUtils;
import com.f1.utils.CachedFile;
import com.f1.utils.CachedResource;
import com.f1.utils.ContentType;
import com.f1.utils.CopyOnWriteHashMap;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;

public class FileSystemHttpHandler extends AbstractHttpHandler {
	private static final Logger log = Logger.getLogger(FileSystemHttpHandler.class.getName());
	protected static final FileInstance REDIRECT_PLACEHOLDER = new FileInstance((CachedFile) null, (byte[]) null);

	private final ConcurrentMap<String, FileInstance> files = new CopyOnWriteHashMap<String, FileInstance>();
	private final String baseUrl;
	private final File base;
	protected long cacheTime;
	private final String indexPage;
	protected final boolean isResource;
	private String defaultExtension;
	private String periodPlusDefaultException;
	private final boolean redirectToIndex;

	public FileSystemHttpHandler(boolean isResource, File base, String baseUrl, long cacheTimeMs, String indexPage) {
		this(isResource, base, baseUrl, cacheTimeMs, indexPage, false);

	}
	public FileSystemHttpHandler(boolean isResource, File base, String baseUrl, long cacheTimeMs, String indexPage, boolean redirectToIndex) {
		this.isResource = isResource;
		this.baseUrl = baseUrl;
		this.base = base;
		this.cacheTime = cacheTimeMs;
		this.indexPage = indexPage;
		this.redirectToIndex = redirectToIndex;
		//		this.putOverrideResponseHeader(HttpUtils.CONTENT_SECURITY_POLICY, HttpUtils.CSP_STRICT_WEB);
	}

	final protected FileInstance getFileInstance(HttpRequestResponse request) throws IOException {
		String uri = request.getRequestUri();
		if (this.getDefaultExtension() != null && !uri.endsWith("/")) {
			int i = IOH.getFileExtensionOffset(uri);
			if (i == -1)
				uri += periodPlusDefaultException;
		}
		if (SH.indexOf(uri, "..", 0) != -1) {
			if (uri.startsWith("../") || SH.indexOf(uri, "/../", 0) != 1 || uri.endsWith("/..")) {
				LH.info(log, "Security risk.. Rejecting attempt to access parent directory: ", uri);
				return null;
			}
		}
		if (SH.startsWith(uri, '~')) {
			LH.info(log, "Security risk.. Rejecting attempt to access home directory: ", uri);
			return null;
		}
		FileInstance file = files.get(uri);
		if (file == null) {
			if (isResource) {
				if (uri.endsWith("/")) {
					if (redirectToIndex)
						request.sendRedirect(join(uri, indexPage));
					else
						request.forward(join(uri, indexPage));
					return REDIRECT_PLACEHOLDER;
				}
				byte[] mimeType = ContentType.getTypeByFileExtension(SH.afterLast(uri, '.'), ContentType.BINARY).getMimeTypeAsBytes();
				File resource = new File(base, uri.substring(baseUrl.length()));
				//				System.out.println("File exists ? " + resource.exists() + " Path: " + resource.getAbsolutePath());
				FileInstance resourceFile = newFileInstance(resource, mimeType);
				if (resourceFile.cachedResource.getData().getText() == null) {
					if (log.isLoggable(Level.INFO))
						LH.info(log, "resource not found:", resource);
					return null;
				} else {
					files.put(uri, resourceFile);
					return resourceFile;
				}
			} else {
				File f = new File(base, uri.substring(baseUrl.length()));
				if (uri.endsWith("/") && f.isDirectory()) {
					File f2 = new File(f, indexPage);
					if (f2.exists()) {
						if (redirectToIndex)
							request.sendRedirect(join(uri, indexPage));
						else
							request.forward(join(uri, indexPage));
						return REDIRECT_PLACEHOLDER;
					}
					return null;

				} else if (f.isFile()) {
					byte[] mimeType = ContentType.getTypeByFileExtension(SH.afterLast(uri, '.')).getMimeTypeAsBytes();
					files.put(uri, file = newFileInstance(f, mimeType));
					return file;
				} else {
					if (log.isLoggable(Level.INFO))
						LH.info(log, "resource not found:", IOH.getFullPath(f));
					return null;
				}
			}
		} else
			return file;
	}

	private String join(String s1, String s2) {
		boolean ends = s1.endsWith("/");
		boolean starts = s2.startsWith("/");
		if (ends) {
			if (starts)
				return s1 + s2.substring(1);
			else
				return s1 + s2;
		} else if (starts)
			return s1 + s2;
		return s1 + '/' + s2;
	}
	private String joinPath(HttpRequestResponse request, String page) {
		return HttpUtils.buildUrl(request.getIsSecure(), request.getHost(), request.getPort(), page, request.getQueryString());
	}
	protected FileInstance newFileInstance(File file, byte[] mimeType) {
		if (isResource)
			return new FileInstance(new CachedResource(file.toString().replace('\\', '/'), cacheTime), mimeType);
		else
			return new FileInstance(new CachedFile(file, cacheTime), mimeType);
	}

	@Override
	public void handle(HttpRequestResponse request) throws IOException {
		try {
			super.handle(request);
			FileInstance file = getFileInstance(request);
			if (file == null) {
				onFileNotFound(request);
				return;
			} else if (file == REDIRECT_PLACEHOLDER) {
				return;
			} else if (file.getModifiedMs() == request.getIfModifiedSince() && !file.getName().endsWith("css")) {
				request.setResponseType(HttpRequestResponse.HTTP_304_NOT_MODIFIED);
			} else {
				writeOut(request, file);
			}
			request.setLastModified(file.getModifiedMs());
		} catch (Exception e) {
			LH.warning(log, "Error handling request: ", request.getRequestUri(), e);
		}
	}

	protected void onFileNotFound(HttpRequestResponse request) throws IOException {
		request.getHttpServer().onResourceNotFound(request);
	}

	protected void writeOut(HttpRequestResponse request, FileInstance file) throws IOException {
		request.setContentTypeAsBytes(file.mimeType);
		String rng = request.getHeader().get("range");
		if (rng != null) {
			if (SH.indexOf(rng, ',', 7) != -1) {
				LH.info(log, "mutliple ranges not supported: ", rng);
			} else {
				String range = SH.stripPrefix(rng, "bytes=", true);
				final int len = file.getData().length;
				final int start;//inclusive
				final int end;//inclusive
				if (range.endsWith("-")) { //nnnn-
					start = Integer.parseInt(range.substring(0, range.length() - 1));
					end = len - 1;
				} else if (range.startsWith("-")) { //-nnnnn
					start = Math.max(0, len - Integer.parseInt(range.substring(1)));
					end = len - 1;
				} else {
					int p = SH.indexOf(range, '-', 1);
					if (p == -1) {
						LH.info(log, "Malformatted range not supported: ", rng);
						request.getOutputStream().write(file.getData());
						return;
					}
					start = Integer.parseInt(range.substring(0, p));
					end = Integer.parseInt(range.substring(p + 1));
				}
				request.getOutputStream().write(file.getData(), start, end - start + 1);
				String range2 = start + "-" + end + "/" + len;
				request.putResponseHeader("Content-Range", "bytes " + range2);
				request.setResponseType(HttpRequestResponse.HTTP_206_PARTAIL_CONTENT);
				return;
			}
		}
		request.getOutputStream().write(file.getData());
	}

	public String getDefaultExtension() {
		return defaultExtension;
	}

	public FileSystemHttpHandler setDefaultExtension(String defaultExtension) {
		this.defaultExtension = defaultExtension;
		this.periodPlusDefaultException = this.defaultExtension == null ? null : ("." + this.defaultExtension);
		return this;
	}

	protected static class FileInstance {
		public final byte[] mimeType;
		public final CachedFile cachedFile;
		public final CachedResource cachedResource;

		public FileInstance(CachedFile cachedFile, byte[] mimeType) {
			this.cachedFile = cachedFile;
			this.mimeType = mimeType;
			this.cachedResource = null;
		}

		public byte[] getData() {
			return cachedFile != null ? cachedFile.getData().getBytes() : cachedResource.getData().getBytes();
		}

		protected String getText() {
			return cachedFile != null ? cachedFile.getData().getText() : cachedResource.getData().getText();
		}

		public long getModifiedMs() {
			return cachedFile != null ? cachedFile.getModifiedMs() : cachedResource.getModifiedMs();
		}

		public FileInstance(CachedResource cachedResource, byte[] mimeType) {
			this.cachedResource = cachedResource;
			this.mimeType = mimeType;
			this.cachedFile = null;
		}

		public String getName() {
			return cachedFile != null ? cachedFile.getFile().getName() : cachedResource.getResourceName();
		}

		@Override
		public String toString() {
			return getName();
		}
	}
}
