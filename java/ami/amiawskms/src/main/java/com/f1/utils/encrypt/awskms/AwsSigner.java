package com.f1.utils.encrypt.awskms;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class AwsSigner {
	public static final String SCHEME = "AWS4";
	public static final String ALGORITHM = "HMAC-SHA256";
	public static final String TERMINATOR = "aws4_request";
	private final String method;
	private final String service;
	private final String host;
	private final String region;
	private final String endpoint;

	public AwsSigner(String method, String service, String host, String region, String endpoint, String amz_target, String accessKey, String secretKey) {
		this.method = method;
		this.service = service;
		this.host = host;
		this.region = region;
		this.endpoint = endpoint;
	}

	private static String getTimeStamp() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));//server timezone
		return dateFormat.format(new Date());
	}
	private static String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));//server timezone
		return dateFormat.format(new Date());
	}

	public String computeSignature(Map<String, String> headers, Map<String, String> queryParameters, String bodyHash, String awsAccessKey, String awsSecretKey) {
		// first get the date and time for the subsequent request, and convert
		// to ISO 8601 format for use in signature generation
		try {
			String dateTimeStamp = getTimeStamp();

			// update the headers with required 'x-amz-date' and 'host' values
			headers.put("x-amz-date", dateTimeStamp);

			headers.put("Host", this.host);

			// canonicalize the headers; we need the set of header names as well as the
			// names and values to go into the signature process
			String canonicalizedHeaderNames = getCanonicalizeHeaderNames(headers);
			String canonicalizedHeaders = getCanonicalizedHeaderString(headers);

			// if any query string parameters have been supplied, canonicalize them
			String canonicalizedQueryParameters = getCanonicalizedQueryString(queryParameters);

			// canonicalize the various components of the request
			String canonicalRequest = null;
			canonicalRequest = getCanonicalRequest(new URL(this.endpoint), this.method, canonicalizedQueryParameters, canonicalizedHeaderNames, canonicalizedHeaders, bodyHash);
			//        System.out.println("--------- Canonical request --------");
			//        System.out.println(canonicalRequest);
			//        System.out.println("------------------------------------");

			// construct the string to be signed
			String dateStamp = getDate();
			String scope = dateStamp + "/" + this.region + "/" + this.service + "/" + "aws4_request";
			String stringToSign = getStringToSign(this.SCHEME, this.ALGORITHM, dateTimeStamp, scope, canonicalRequest);
			//        System.out.println("--------- String to sign -----------");
			//        System.out.println(stringToSign);
			//        System.out.println("------------------------------------");

			// compute the signing key
			byte[] kSecret = (SCHEME + awsSecretKey).getBytes();
			byte[] kDate = sign(dateStamp, kSecret, "HmacSHA256");
			byte[] kRegion = sign(this.region, kDate, "HmacSHA256");
			byte[] kService = sign(this.service, kRegion, "HmacSHA256");
			byte[] kSigning = sign(TERMINATOR, kService, "HmacSHA256");
			byte[] signature = sign(stringToSign, kSigning, "HmacSHA256");

			String credentialsAuthorizationHeader = "Credential=" + awsAccessKey + "/" + scope;
			String signedHeadersAuthorizationHeader = "SignedHeaders=" + canonicalizedHeaderNames;
			String signatureAuthorizationHeader = "Signature=" + AwsKmsRestDecrypter.toHex(signature);

			String authorizationHeader = SCHEME + "-" + ALGORITHM + " " + credentialsAuthorizationHeader + ", " + signedHeadersAuthorizationHeader + ", "
					+ signatureAuthorizationHeader;

			return authorizationHeader;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns the canonical collection of header names that will be included in the signature. For AWS4, all header names must be included in the process in sorted canonicalized
	 * order.
	 */
	protected static String getCanonicalizeHeaderNames(Map<String, String> headers) {
		List<String> sortedHeaders = new ArrayList<String>();
		sortedHeaders.addAll(headers.keySet());
		Collections.sort(sortedHeaders, String.CASE_INSENSITIVE_ORDER);

		StringBuilder buffer = new StringBuilder();
		for (String header : sortedHeaders) {
			if (buffer.length() > 0)
				buffer.append(";");
			buffer.append(header.toLowerCase());
		}

		return buffer.toString();
	}

	/**
	 * Computes the canonical headers with values for the request. For AWS4, all headers must be included in the signing process.
	 */
	protected static String getCanonicalizedHeaderString(Map<String, String> headers) {
		if (headers == null || headers.isEmpty()) {
			return "";
		}

		// step1: sort the headers by case-insensitive order
		List<String> sortedHeaders = new ArrayList<String>();
		sortedHeaders.addAll(headers.keySet());
		Collections.sort(sortedHeaders, String.CASE_INSENSITIVE_ORDER);

		// step2: form the canonical header:value entries in sorted order.
		// Multiple white spaces in the values should be compressed to a single
		// space.
		StringBuilder buffer = new StringBuilder();
		for (String key : sortedHeaders) {
			buffer.append(key.toLowerCase().replaceAll("\\s+", " ") + ":" + headers.get(key).replaceAll("\\s+", " "));
			buffer.append("\n");
		}

		return buffer.toString();
	}

	/**
	 * Examines the specified query string parameters and returns a canonicalized form.
	 * <p>
	 * The canonicalized query string is formed by first sorting all the query string parameters, then URI encoding both the key and value and then joining them, in order,
	 * separating key value pairs with an '&'.
	 *
	 * @param parameters
	 *            The query string parameters to be canonicalized.
	 *
	 * @return A canonicalized form for the specified query string parameters.
	 */
	public static String getCanonicalizedQueryString(Map<String, String> parameters) {
		if (parameters == null || parameters.isEmpty()) {
			return "";
		}

		SortedMap<String, String> sorted = new TreeMap<String, String>();

		Iterator<Map.Entry<String, String>> pairs = parameters.entrySet().iterator();
		while (pairs.hasNext()) {
			Map.Entry<String, String> pair = pairs.next();
			String key = pair.getKey();
			String value = pair.getValue();
			sorted.put(urlEncode(key, false), urlEncode(value, false));
		}

		StringBuilder builder = new StringBuilder();
		pairs = sorted.entrySet().iterator();
		while (pairs.hasNext()) {
			Map.Entry<String, String> pair = pairs.next();
			builder.append(pair.getKey());
			builder.append("=");
			builder.append(pair.getValue());
			if (pairs.hasNext()) {
				builder.append("&");
			}
		}

		return builder.toString();
	}

	/**
	 * Returns the canonical request string to go into the signer process; this consists of several canonical sub-parts.
	 * 
	 * @return
	 */
	protected static String getCanonicalRequest(URL endpoint, String httpMethod, String queryParameters, String canonicalizedHeaderNames, String canonicalizedHeaders,
			String bodyHash) {
		String canonicalRequest = httpMethod + "\n" + getCanonicalizedResourcePath(endpoint) + "\n" + queryParameters + "\n" + canonicalizedHeaders + "\n"
				+ canonicalizedHeaderNames + "\n" + bodyHash;
		return canonicalRequest;
	}

	/**
	 * Returns the canonicalized resource path for the service endpoint.
	 */
	protected static String getCanonicalizedResourcePath(URL endpoint) {
		if (endpoint == null) {
			return "/";
		}
		String path = endpoint.getPath();
		if (path == null || path.isEmpty()) {
			return "/";
		}

		String encodedPath = urlEncode(path, true);
		if (encodedPath.startsWith("/")) {
			return encodedPath;
		} else {
			return "/".concat(encodedPath);
		}
	}

	protected static String getStringToSign(String scheme, String algorithm, String dateTime, String scope, String canonicalRequest) {
		String stringToSign = scheme + "-" + algorithm + "\n" + dateTime + "\n" + scope + "\n" + AwsKmsRestDecrypter.toHex(hash(canonicalRequest));
		return stringToSign;
	}

	/**
	 * Hashes the string contents (assumed to be UTF-8) using the SHA-256 algorithm.
	 */
	public static byte[] hash(String text) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(text.getBytes("UTF-8"));
			return md.digest();
		} catch (Exception e) {
			throw new RuntimeException("Unable to compute hash while signing request: " + e.getMessage(), e);
		}
	}

	protected static byte[] sign(String stringData, byte[] key, String algorithm) {
		try {
			byte[] data = stringData.getBytes("UTF-8");
			Mac mac = Mac.getInstance(algorithm);
			mac.init(new SecretKeySpec(key, algorithm));
			return mac.doFinal(data);
		} catch (Exception e) {
			throw new RuntimeException("Unable to calculate a request signature: " + e.getMessage(), e);
		}
	}

	public static String urlEncode(String url, boolean keepPathSlash) {
		String encoded;
		try {
			encoded = URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("UTF-8 encoding is not supported.", e);
		}
		if (keepPathSlash) {
			encoded = encoded.replace("%2F", "/");
		}
		return encoded;
	}
}
