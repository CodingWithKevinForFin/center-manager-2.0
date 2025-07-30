package com.f1.utils.rest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.FileMagic;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.encrypt.EncoderUtils;

public class RestHelper {
	public static final Logger log = LH.get();

	public static final byte HTTP_UNKNOWN_METHOD = -1;
	public static final byte HTTP_GET = 0;
	public static final byte HTTP_POST = 1;
	public static final byte HTTP_PUT = 2;
	public static final byte HTTP_DELETE = 3;
	public static final byte HTTP_HEAD = 4;
	public static final byte HTTP_PATCH = 5;
	public static final byte HTTP_CONNECT = 6;
	public static final byte HTTP_TRACE = 7;
	public static final byte HTTP_OPTIONS = 8;
	public static final String CONTENT_TYPE_TEXTPLAIN = "text/plain";
	public static final String CONTENT_TYPE_JSON = "application/json";
	public static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";

	public static Map<String, String> addBasicAuthentication(Map<String, String> httpHeaders, String username, char[] password) {
		String creds = username + ":" + new String(password);
		String auth = "Basic " + EncoderUtils.encode64(creds.getBytes());
		httpHeaders.put("Authorization", auth);
		return httpHeaders;
	}

	public static Map<String, String> addBearerAuthentication(Map<String, String> httpHeaders, String bearerToken) {
		String auth = "Bearer " + bearerToken;
		httpHeaders.put("Authorization", auth);
		return httpHeaders;
	}
	public static Map<String, String> addApiKeyAuthentication(Map<String, String> httpHeaders, String apiKey) {
		String auth = "Apikey " + apiKey;
		httpHeaders.put("Authorization", auth);
		return httpHeaders;
	}

	public static Map<String, String> addContentType(Map<String, String> httpHeaders, String type) {
		httpHeaders.put("Content-Type", type);
		return httpHeaders;
	}

	public static byte toHttpMethodType(String method) {
		String umethod = SH.toUpperCase(method);
		if (SH.equals(umethod, "GET")) {
			return RestHelper.HTTP_GET;
		} else if (SH.equals(umethod, "POST")) {
			return RestHelper.HTTP_POST;
		} else if (SH.equals(umethod, "PUT")) {
			return RestHelper.HTTP_PUT;
		} else if (SH.equals(umethod, "DELETE")) {
			return RestHelper.HTTP_DELETE;
		} else if (SH.equals(umethod, "HEAD")) {
			return RestHelper.HTTP_HEAD;
		} else if (SH.equals(umethod, "PATCH")) {
			return RestHelper.HTTP_PATCH;
		} else if (SH.equals(umethod, "CONNECT")) {
			return RestHelper.HTTP_CONNECT;
		} else if (SH.equals(umethod, "TRACE")) {
			return RestHelper.HTTP_TRACE;
		} else if (SH.equals(umethod, "OPTIONS")) {
			return RestHelper.HTTP_OPTIONS;
		} else {
			return RestHelper.HTTP_UNKNOWN_METHOD;
		}
	}
	
	public static byte[] sendRestRequestHandleRedirect(String httpMethod, String baseUrl, String urlExtension, String urlParam, Map<String, String> httpHeaders, String bodyParams, boolean ignoreCerts, int timeout,
			Map<String, List<String>> returnHeadersSink, boolean debug, boolean redirectFollowHttpMethod, boolean redirectFollowAuthHeader, boolean redirectPersistCookies) {
		if (debug) {
			LH.warning(log, "Rest: HTTP/s METHOD: " + httpMethod);
			LH.warning(log, "Rest: HTTP/s URL: " + baseUrl + urlExtension + urlParam);
			LH.warning(log, "Rest: HTTP/s HEADERS: " + ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(httpHeaders));
			LH.warning(log, "Rest: HTTP/s PARAMS: " + bodyParams);
			LH.warning(log, "Rest: HTTP/s IGNORE_CERTS: " + ignoreCerts);
		}

		byte[] result = null;
		
		try {
			result = IOH.doHttpHandleRedirect(SH.toUpperCase(httpMethod), baseUrl, urlExtension, urlParam, httpHeaders, bodyParams == null ? null : bodyParams.getBytes(), 
					returnHeadersSink, ignoreCerts, timeout, redirectFollowHttpMethod, redirectFollowAuthHeader, redirectPersistCookies);
		} catch (MalformedURLException e) {
			if (debug)
				e.printStackTrace(System.err);
			LH.warning(log, "Could not create rest request: ", e);
		} catch (IOException e) {
			if (debug)
				e.printStackTrace(System.err);
			LH.warning(log, "Exception occured when sending rest request: ", e);
		}
		if (debug) {
			LH.warning(log, "Rest: HTTP/s Result: " + (result == null ? null : new String(result)));
			LH.warning(log, "Rest: HTTP/s ReturnHeaders: " + ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(returnHeadersSink));
		}
		return result;
	}
	
	public static byte[] sendRestRequest(String httpMethod, String urlString, Map<String, String> httpHeaders, String bodyParams, boolean ignoreCerts, int timeout,
			Map<String, List<String>> returnHeadersSink, boolean debug) {
		if (debug) {
			LH.warning(log, "Rest: HTTP/s METHOD: " + httpMethod);
			LH.warning(log, "Rest: HTTP/s URL: " + urlString);
			LH.warning(log, "Rest: HTTP/s HEADERS: " + ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(httpHeaders));
			LH.warning(log, "Rest: HTTP/s PARAMS: " + bodyParams);
			LH.warning(log, "Rest: HTTP/s IGNORE_CERTS: " + ignoreCerts);
		}

		byte[] result = null;
		URL url = null;
		try {
			url = new URL(urlString);
			result = IOH.doHttp(SH.toUpperCase(httpMethod), url, httpHeaders, bodyParams == null ? null : bodyParams.getBytes(), returnHeadersSink, ignoreCerts, timeout);
		} catch (MalformedURLException e) {
			if (debug)
				e.printStackTrace(System.err);
			LH.warning(log, "Could not create rest request: ", e);
		} catch (IOException e) {
			if (debug)
				e.printStackTrace(System.err);
			LH.warning(log, "Exception occured when sending rest request: ", e);
		}
		if (debug) {
			LH.warning(log, "Rest: HTTP/s Result: " + (result == null ? null : new String(result)));
			LH.warning(log, "Rest: HTTP/s ReturnHeaders: " + ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(returnHeadersSink));
		}
		return result;
	}

	// See the other sendRestRequest, this only support get and post
	public static byte[] sendRestRequest(byte httpMethod, String urlString, Map<String, String> httpHeaders, String bodyParams, boolean ignoreCerts, int timeout,
			Map<String, List<String>> returnHeadersSink, boolean debug) {

		if (debug) {
			LH.warning(log, "Rest: HTTP/s METHOD: " + httpMethod);
			LH.warning(log, "Rest: HTTP/s URL: " + urlString);
			LH.warning(log, "Rest: HTTP/s HEADERS: " + ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(httpHeaders));
			LH.warning(log, "Rest: HTTP/s PARAMS: " + bodyParams);
			LH.warning(log, "Rest: HTTP/s IGNORE_CERTS: " + ignoreCerts);
		}

		byte[] result = null;
		URL url = null;
		try {
			url = new URL(urlString);
			switch (httpMethod) {
				case HTTP_GET:
					result = IOH.doGet(url, httpHeaders, returnHeadersSink, ignoreCerts, timeout);
					break;
				case HTTP_POST:
					result = IOH.doPost(url, httpHeaders, bodyParams.getBytes(), returnHeadersSink, ignoreCerts, timeout);
					break;
				default:
					throw new UnsupportedOperationException();
			}

		} catch (MalformedURLException e) {
			if (debug)
				e.printStackTrace(System.err);
			LH.warning(log, "Could not create rest request: ", e);
		} catch (IOException e) {
			if (debug)
				e.printStackTrace(System.err);
			LH.warning(log, "Exception occured when sending rest request: ", e);
		}
		if (debug) {
			LH.warning(log, "Rest: HTTP/s Result: " + (result == null ? null : new String(result)));
			LH.warning(log, "Rest: HTTP/s ReturnHeaders: " + ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(returnHeadersSink));
		}
		return result;
	}

	public static boolean isResponseJson(Map<String, List<String>> responseHeaders) {
		if (!responseHeaders.containsKey("Content-Type"))
			return false;
		List<String> contentType = responseHeaders.get("Content-Type");
		String responseContentType = SH.toLowerCase(ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(contentType));
		return SH.indexOf(responseContentType, "json", 0) > -1;
	}
	public static Object parseRestResponse(byte[] data, boolean debug) {
		return parseRestResponse(data, true, debug);
	}
	public static Object parseRestResponse(byte[] data, boolean resultIsJson, boolean debug) {
		Object r = null;
		byte[] readData = null;
		try {
			// Unzip result if needed
			if (data != null) {
				int type = FileMagic.getType(data);
				switch (type) {
					case FileMagic.FILE_TYPE_GZIP_COMPRESSED_DATA: {
						GZIPInputStream in = new GZIPInputStream(new FastByteArrayDataInputStream(data));
						readData = IOH.readData(in);
						break;
					}
					case FileMagic.FILE_TYPE_DEFLATE_COMPRESSED_DATA: {
						InflaterInputStream in = new InflaterInputStream(new FastByteArrayDataInputStream(data));
						readData = IOH.readData(in);
						break;
					}
					default:
						readData = data;
				}

				if (readData != null) {
					if (resultIsJson)
						r = ObjectToJsonConverter.INSTANCE_COMPACT.bytes2Object(readData);
					else
						r = new String(readData);
				}
			}
		} catch (IOException e) {
			if (debug)
				e.printStackTrace(System.err);
			LH.warning(log, "Exception occured when parsing rest response: ", e);
		}
		return r;
	}

}
