package com.f1.utils.encrypt.awskms;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.base.Decrypter;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.encrypt.EncoderUtils;
import com.f1.utils.rest.RestHelper;

public class AwsKmsRestDecrypter implements Decrypter {
	public static final Logger log = LH.get();

	private final String keyId;
	private final StringBuilder sb;
	private final String service;
	private final String host;
	private final String region;
	private final String endpoint;
	private final String amz_target_decrypt;
	private final String amz_target_encrypt;
	private final String accessKey;
	private final String secretKey;
	private final String charset;
	private boolean debug;

	private static final String KEY_VALUE = "Plaintext";

	public AwsKmsRestDecrypter() {
		this.sb = new StringBuilder();
		this.keyId = getenv("AwsKmsKeyId");
		this.service = "kms";
		this.host = getenv("AwsKmsHost");
		this.region = getenv("AwsKmsRegion");
		this.endpoint = getenv("AwsKmsEndpoint");
		this.amz_target_decrypt = "TrentService.Decrypt";
		this.amz_target_encrypt = "TrentService.Encrypt";
		this.accessKey = getenv("AwsKmsAccessKey");
		this.secretKey = getenv("AwsKmsSecret");
		this.charset = getenvNoThrow("AwsKmsDecrypterCharset", "UTF-8");// See link for encodings https://docs.oracle.com/javase/6/docs/technotes/guides/intl/encoding.doc.html
		this.debug = "true".equals(getenvNoThrow("AwsKmsDebug"));
	}

	static private String getenvNoThrow(String string, String dflt) {
		String r = System.getProperty(string);
		if (r == null)
			return dflt;
		return r;
	}
	static private String getenvNoThrow(String string) {
		String r = System.getProperty(string);
		return r;
	}

	static private String getenv(String string) {
		String r = System.getProperty(string);
		if (r == null)
			throw new RuntimeException("Must Specify -D" + string + " when using the AwsKmsRestDecrypter plugin");
		return r;
	}

	public static void main(String[] args) {
		System.out.println(toHex("HELLO".getBytes()));
		AwsKmsRestDecrypter mydecrypter = new AwsKmsRestDecrypter();

		String test = "VGhpcyBpcyBEYXkgMSBmb3IgdGhlIEludGVybmV0Cg==";
		String enc = mydecrypter.getEncrypted(test);
		System.out.println(enc);

		String decrypted = mydecrypter.decryptString(enc);
		System.out.println(decrypted);
	}

	@Override
	public String decryptString(String encrypted) {
		if (encrypted == null)
			return null;
		byte[] decrypted = this.decrypt(encrypted);
		String result = null;
		try {
			result = new String(decrypted, this.charset);
		} catch (UnsupportedEncodingException e) {
			if (debug)
				e.printStackTrace(System.err);
			LH.warning(log, "Exception occured when decoding response using charset", this.charset, " ", e);
		}
		return result;
	}

	@Override
	public byte[] decrypt(String encrypted) {
		if (encrypted == null)
			return null;
		Map<String, Object> result = this.getRest_decrypt(encrypted);
		String decrypted = CH.getOrNoThrow(Caster_String.INSTANCE, result, KEY_VALUE, null);
		if (decrypted == null)
			return null;

		byte[] decoded = EncoderUtils.decode64(decrypted);
		return decoded;
	}

	private String getDecrypted(String value) {
		try {
			String rvalue = null;
			Object o = this.getRest_decrypt(value);
			if (o instanceof Map) {
				Map<String, String> m = (Map<String, String>) o;
				rvalue = m.get(KEY_VALUE);
			}
			return rvalue;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getEncrypted(String value) {
		try {
			String rvalue = null;
			rvalue = encryptString(value);
			return rvalue;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String encryptString(String text) throws MalformedURLException {
		if (text == null)
			return null;
		byte[] bytes = null;
		try {
			bytes = text.getBytes(this.charset);
		} catch (UnsupportedEncodingException e) {
			if (debug)
				e.printStackTrace(System.err);
			LH.warning(log, "Exception occured when encoding text using charset", this.charset, " ", e);
		}
		if (bytes == null)
			return null;
		Map<String, Object> result = this.getRest_encrypt(text);
		String encrypted = CH.getOrNoThrow(Caster_String.INSTANCE, result, "CiphertextBlob", null);
		return encrypted;
	}

	private Map<String, Object> getRest_encrypt(String text) throws MalformedURLException {
		AwsSigner awsSigner = new AwsSigner("POST", this.service, this.host, this.region, this.endpoint, this.amz_target_encrypt, this.accessKey, this.secretKey);

		// 1 HTTP Body
		Map<String, String> bodyParams = new HashMap<String, String>();
		bodyParams.put("KeyId", this.keyId);
		bodyParams.put("Plaintext", text);
		String request_parameters = ObjectToJsonConverter.INSTANCE_CLEAN.objectToString(bodyParams);
		byte[] contentHash = AwsSigner.hash(request_parameters);
		String contentHashString = toHex(contentHash);

		SH.clear(sb);
		// 1 HTTP Method
		byte httpMethod = RestHelper.HTTP_POST;

		// 2 Build URL
		sb.append(this.endpoint);
		String urlString = sb.toString();
		SH.clear(sb);

		// 3 HTTP Headers
		Map<String, String> headers = new HashMap<String, String>();
		//        headers.put("content-length", "" + request_parameters.length());
		headers.put("X-Amz-Target", this.amz_target_encrypt);
		headers.put("content-type", "application/x-amz-json-1.1");
		String authorization_header = awsSigner.computeSignature(headers, null, contentHashString, this.accessKey, this.secretKey);
		headers.put("authorization", authorization_header);
		boolean ignoreCerts = false;
		int timeout = -1;
		Map<String, List<String>> returnHeadersSink = new LinkedHashMap<String, List<String>>();
		byte[] data = RestHelper.sendRestRequest(httpMethod, urlString, headers, request_parameters, ignoreCerts, timeout, returnHeadersSink, this.debug);

		return (Map<String, Object>) RestHelper.parseRestResponse(data, debug);
	}

	private Map<String, Object> getRest_decrypt(String value) {
		AwsSigner awsSigner = new AwsSigner("POST", this.service, this.host, this.region, this.endpoint, this.amz_target_decrypt, this.accessKey, this.secretKey);

		// 1 HTTP Body
		Map<String, String> bodyParams = new HashMap<String, String>();
		bodyParams.put("KeyId", this.keyId);
		bodyParams.put("CiphertextBlob", value);
		String request_parameters = ObjectToJsonConverter.INSTANCE_CLEAN.objectToString(bodyParams);
		byte[] contentHash = AwsSigner.hash(request_parameters);
		String contentHashString = toHex(contentHash);

		SH.clear(sb);
		// 1 HTTP Method
		byte httpMethod = RestHelper.HTTP_POST;

		// 2 Build URL
		sb.append(this.endpoint);
		String urlString = sb.toString();
		SH.clear(sb);

		// 3 HTTP Headers
		Map<String, String> headers = new HashMap<String, String>();
		//        headers.put("content-length", "" + request_parameters.length());
		headers.put("X-Amz-Target", this.amz_target_decrypt);
		headers.put("content-type", "application/x-amz-json-1.1");
		String authorization_header = awsSigner.computeSignature(headers, null, contentHashString, this.accessKey, this.secretKey);
		headers.put("authorization", authorization_header);
		boolean ignoreCerts = false;
		int timeout = -1;
		Map<String, List<String>> returnHeadersSink = new LinkedHashMap<String, List<String>>();
		byte[] data = RestHelper.sendRestRequest(httpMethod, urlString, headers, request_parameters, ignoreCerts, timeout, returnHeadersSink, this.debug);

		return (Map<String, Object>) RestHelper.parseRestResponse(data, debug);
	}

	private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

	protected static String toHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}
}
