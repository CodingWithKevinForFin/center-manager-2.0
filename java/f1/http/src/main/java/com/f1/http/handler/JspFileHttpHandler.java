package com.f1.http.handler;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.utils.CachedFile;
import com.f1.utils.CachedResource;
import com.f1.utils.ContentType;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;

public class JspFileHttpHandler extends JspHttpHandler {

	private static final Logger log = LH.get();
	private File base;
	private JspFileInstance fileInstance;

	public JspFileHttpHandler(boolean isResource, File base, long cacheTimeMs, JspCompiler jspCompiler) {
		super(isResource, base, null, cacheTimeMs, null, false, jspCompiler);
		byte[] mimeType = ContentType.getTypeByFileExtension(SH.afterLast(base.getName(), '.')).getMimeTypeAsBytes();
		if (isResource) {
			String resource = base.toString().replace('\\', '/');
			this.fileInstance = new JspFileInstance(new CachedResource(resource, cacheTime), mimeType);
			if (this.fileInstance.getData() == null)
				throw new RuntimeException("Resource not found: " + resource);
		} else {
			this.fileInstance = new JspFileInstance(new CachedFile(base, cacheTime), mimeType);
			if (this.fileInstance.getData() == null)
				throw new RuntimeException("File not found: " + IOH.getFullPath(base));
		}
	}
	@Override
	public void handle(HttpRequestResponse request) throws IOException {
		this.fileInstance.handle(request);
	}

}
