package com.f1.http.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpServer;
import com.f1.http.HttpSession;
import com.f1.utils.FastPrintStream;

public class IncludeHttpRequestResponse implements HttpRequestResponse {

	private HttpRequestResponse inner;
	private String dummyRequestUri;
	private FastPrintStream dummyOutputStream;
	private HashMap<String, Object> dummyAttributes;

	@Override
	public Map<String, String> getHeader() {
		return inner.getHeader();
	}

	@Override
	public Map<String, String> getParams() {
		return inner.getParams();
	}

	@Override
	public String getContextPath() {
		return inner.getContextPath();
	}

	@Override
	public String getQueryString() {
		return inner.getQueryString();
	}

	@Override
	public String getRequestUri() {
		return dummyRequestUri == null ? inner.getRequestUri() : dummyRequestUri;
	}

	@Override
	public Map<String, String> getCookies() {
		return inner.getCookies();
	}

	@Override
	public String getMethod() {
		return inner.getMethod();
	}

	@Override
	public long getIfModifiedSince() {
		return 0;
	}

	@Override
	public FastPrintStream getOutputStream() {
		return dummyOutputStream == null ? inner.getOutputStream() : dummyOutputStream;
	}

	@Override
	public void setResponseType(int type) {
		inner.setResponseType(type);
	}

	@Override
	public void setContentTypeAsBytes(byte[] contentType) {
	}

	@Override
	public void setContentType(String contentType) {
	}

	@Override
	public void setLastModified(long lastModified) {
	}

	@Override
	public void putResponseHeader(String key, String value) {
		inner.putResponseHeader(key, value);
	}

	@Override
	public void sendRedirect(String url) {
		inner.sendRedirect(url);
	}

	public IncludeHttpRequestResponse(HttpRequestResponse inner) {
		this.inner = inner;
	}

	@Override
	public void putCookie(String key, String value, String optionalDomain, long optionalExpires, String additionalOptions) {
		inner.putCookie(key, value, optionalDomain, optionalExpires, additionalOptions);
	}

	@Override
	public Map<String, Object> getAttributes() {
		return dummyAttributes != null ? dummyAttributes : inner.getAttributes();
	}

	@Override
	public HttpSession getSession(boolean createIfMissing) {
		return inner.getSession(createIfMissing);
	}

	@Override
	public HttpServer getHttpServer() {
		return inner.getHttpServer();
	}

	@Override
	public Object findAttribute(String key) {
		Object r = getAttributes().get(key);
		HttpSession session = getSession(false);
		if (r == null && session != null) {
			r = session.getAttributes().get(key);
		}
		if (r == null)
			r = getHttpServer().getAttributes().get(key);
		return r;

	}

	@Override
	public List<Object> getParamAsList(String key) {
		return inner.getParamAsList(key);
	}

	@Override
	public String getResponseType() {
		return inner.getResponseType();
	}

	@Override
	public boolean getIsSecure() {
		return inner.getIsSecure();
	}

	@Override
	public void forward(String url) throws IOException {
		inner.forward(url);
	}

	@Override
	public int getPort() {
		return inner.getPort();
	}

	@Override
	public String getHost() {
		return inner.getHost();
	}

	@Override
	public String getRequestUrl() {
		return inner.getRequestUrl();
	}

	public void setDummyRequestUri(String dummyRequestUri) {
		this.dummyRequestUri = dummyRequestUri;
	}

	public void setDummyOutputStream(FastPrintStream out) {
		this.dummyOutputStream = out;
	}

	public void setDummyAttributes(HashMap<String, Object> attributes) {
		this.dummyAttributes = attributes;

	}

	@Override
	public String getRemoteHost() {
		return inner.getRemoteHost();
	}

	@Override
	public int getRemotePort() {
		return inner.getRemotePort();
	}

	@Override
	public void setResponseAsyncMode() {
		inner.setResponseAsyncMode();
	}

	@Override
	public boolean getResponseAsyncMode() {
		return inner.getResponseAsyncMode();
	}

	@Override
	public boolean respondNow(boolean sendInThisThread) {
		return inner.respondNow(sendInThisThread);
	}

	@Override
	public void setResponseType(byte[] type) {
		inner.setResponseType(type);
	}

	@Override
	public void setKeepAlive(boolean keepAlive) {
		inner.setKeepAlive(keepAlive);
	}

	@Override
	public String getDescription() {
		return inner.getDescription();
	}

	@Override
	public int getRequestContentLength() {
		return inner.getRequestContentLength();
	}

	@Override
	public byte[] getRequestContentBuffer() {
		return inner.getRequestContentBuffer();
	}

	@Override
	public void getRequestContentBuffer(StringBuilder sink) {
		inner.getRequestContentBuffer(sink);
	}

	@Override
	public boolean getIsMultipart() {
		return inner.getIsMultipart();
	}

	@Override
	public String getCacheControl() {
		return inner.getCacheControl();
	}

	@Override
	public void setCacheControl(String cacheControl) {
		this.inner.setCacheControl(cacheControl);
	}

	@Override
	public String getResponseHeader(String key) {
		return this.inner.getResponseHeader(key);
	}

	@Override
	public String genCspNonce() {
		return this.inner.genCspNonce();
	}

}
