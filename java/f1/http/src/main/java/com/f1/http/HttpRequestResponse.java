package com.f1.http;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.f1.utils.FastPrintStream;

public interface HttpRequestResponse {

	int HTTP_400_BAD_REQUEST = 400;
	int HTTP_401_UNAUTHORIZED = 401;
	int HTTP_403_FORBIDDEN = 403;
	int HTTP_404_NOT_FOUND = 404;
	int HTTP_200_OK = 200;
	int HTTP_204_NO_CONTENT = 204;
	int HTTP_500_SERVICE_ERROR = 500;
	int HTTP_503_SERVICE_UNAVAILABLE = 503;
	int HTTP_304_NOT_MODIFIED = 304;
	int HTTP_206_PARTAIL_CONTENT = 206;

	String GET = "GET";
	String POST = "POST";
	String HEAD = "HEAD";
	String OPTIONS = "OPTIONS";

	// Request
	public Map<String, String> getHeader();

	public Map<String, String> getParams();

	public String getContextPath();

	public String getQueryString();

	public String getRequestUri();

	public Map<String, String> getCookies();

	public String getMethod();

	public long getIfModifiedSince();

	public Map<String, Object> getAttributes();

	// Response
	public FastPrintStream getOutputStream();

	public void setResponseType(int type);

	public void setContentTypeAsBytes(byte[] contentType);

	public void setContentType(String contentType);

	public void setLastModified(long lastModified);

	public void putResponseHeader(String key, String value);
	public String getResponseHeader(String key);

	public void putCookie(String key, String value, String optionalDomain, long optionalExpires, String additionalOptions);

	public void sendRedirect(String url);

	public void forward(String url) throws IOException;

	public HttpSession getSession(boolean createIfMissing);

	public HttpServer getHttpServer();

	public Object findAttribute(String key);

	public List<Object> getParamAsList(String key);

	public String getResponseType();

	public boolean getIsSecure();

	public int getPort();

	public String getHost();

	public String getRequestUrl();

	public String getRemoteHost();

	public int getRemotePort();

	public void setResponseAsyncMode();
	public boolean getResponseAsyncMode();
	public boolean respondNow(boolean sendInThisThread);

	void setResponseType(byte[] type);

	void setKeepAlive(boolean keepAlive);

	String getDescription();

	int getRequestContentLength();

	byte[] getRequestContentBuffer();

	public void getRequestContentBuffer(StringBuilder sink);

	boolean getIsMultipart();

	String getCacheControl();

	void setCacheControl(String cacheControl);

	public String genCspNonce();

}
