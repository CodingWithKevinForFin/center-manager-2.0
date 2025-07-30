package com.f1.http.handler;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import com.f1.codegen.CodeCompiler;
import com.f1.http.HttpHandler;
import com.f1.http.HttpRequestResponse;
import com.f1.utils.CachedFile;
import com.f1.utils.CachedResource;
import com.f1.utils.IOH;
import com.f1.utils.LH;

public class JspHttpHandler extends FileSystemHttpHandler {
	private static final Logger log = Logger.getLogger(JspHttpHandler.class.getName());

	protected final JspCompiler jspCompiler;

	public JspHttpHandler(boolean isResource, File base, String baseUrl, long cacheTimeMs, String indexPage, CodeCompiler compiler) {
		this(isResource, base, baseUrl, cacheTimeMs, indexPage, false, new JspCompiler(compiler));
	}
	public JspHttpHandler(boolean isResource, File base, String baseUrl, long cacheTimeMs, String indexPage, JspCompiler compiler) {
		this(isResource, base, baseUrl, cacheTimeMs, indexPage, false, compiler);
	}
	public JspHttpHandler(boolean isResource, File base, String baseUrl, long cacheTimeMs, String indexPage, boolean redirectToIndexPage, JspCompiler jspCompiler) {
		super(isResource, base, baseUrl, cacheTimeMs, indexPage, redirectToIndexPage);
		this.jspCompiler = jspCompiler;
	}
	@Override
	public void handle(HttpRequestResponse request) throws IOException {
		FileInstance f = getFileInstance(request);
		if (f == REDIRECT_PLACEHOLDER)
			return;
		JspFileInstance file = (JspFileInstance) f;
		if (file == null) {
			onFileNotFound(request);
			return;
		}
		file.handle(request);
	}

	@Override
	protected FileInstance newFileInstance(File file, byte[] mimeType) {
		if (isResource)
			return new JspFileInstance(new CachedResource(file.toString().replace('\\', '/'), cacheTime), mimeType);
		else
			return new JspFileInstance(new CachedFile(file, cacheTime), mimeType);
	}

	protected class JspFileInstance extends FileInstance {

		private String processedData;
		protected HttpHandler compiledHandler;
		private int i = 0;
		protected String fullClassName = "";

		public JspFileInstance(CachedFile file, byte[] mimeType) {
			super(file, mimeType);
		}

		public void handle(HttpRequestResponse request) {
			if (changed())
				process();
			if (compiledHandler == null)
				throw new RuntimeException("jsp not compiled");
			try {
				compiledHandler.handle(request);
			} catch (Exception e) {
				LH.warning(log, "Error handling jsp: ", IOH.getFullPath(jspCompiler.getSourceFile(fullClassName)), e);
				throw new RuntimeException("Could not build JSP", e);
			}
			request.setContentTypeAsBytes(mimeType);
		}

		public JspFileInstance(CachedResource file, byte[] mimeType) {
			super(file, mimeType);
		}

		public void process() {
			this.fullClassName = jspCompiler.getFullClassName(getName(), ++i);
			String text = getText();
			HttpHandler ch = jspCompiler.compile(text, fullClassName);
			if (ch != null) {
				this.compiledHandler = ch;
				this.processedData = getText();
			}
		}
		public boolean changed() {
			return processedData != getText();
		}

	}

}
