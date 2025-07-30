package com.f1.utils.encrypt.azurevault;

import java.io.UnsupportedEncodingException;
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

public class AzureVaultRestDecrypter implements Decrypter {
	public static final Logger log = LH.get();
	private static final String AZURE_AUTH = "AzureAuth";
	private static final String AUTH_TOKEN = "Token";
	private static final String AUTH_MANAGED_IDENTITY = "Identity";

	private static final String KEY_ACCESS_TOKEN = "access_token";
	private static final String KEY_EXPIRES_IN = "expires_in";
	private static final String KEY_VALUE = "value";

	private final String keyName;
	private final String keyVersion;
	private final String apiVersion;
	private final String alg;
	private final StringBuilder sb;
	private final String urlBase;
	private final String charset;
	private final String useAuth;
	private Map<String, Object> tokenMap;
	private String azureBearerToken;
	private boolean debug;

	public AzureVaultRestDecrypter() {
		this.sb = new StringBuilder();
		this.urlBase = getenv("AzureUrlBase");//= "https://wafraami.vault.azure.net/";
		this.useAuth = getenv(AZURE_AUTH);
		this.getAuthToken();

		this.keyName = getenv("AzureDecrypterKeyName");
		this.keyVersion = getenv("AzureDecrypterKeyVersion");
		this.apiVersion = getenvNoThrow("AzureDecrypterApiVersion", "7.2");
		this.alg = getenv("AzureDecrypterAlg");
		this.charset = getenvNoThrow("AzureDecrypterCharset", "UTF-8");// See link for encodings https://docs.oracle.com/javase/6/docs/technotes/guides/intl/encoding.doc.html
		this.debug = "true".equals(getenvNoThrow("AzureDebug"));

	}

	public void getAuthToken() {
		if (AUTH_TOKEN.equals(this.useAuth)) {
			this.azureBearerToken = getenvNoThrow("AzureBearerToken");
		} else if (AUTH_MANAGED_IDENTITY.equals(this.useAuth)) {
			String apiVersion = getenvNoThrow("AzureIdentityApiVersion", "2018-02-01");
			String resource = getenvNoThrow("AzureIdentityResource", "https://management.azure.com/");
			String objectId = getenvNoThrow("AzureIdentityObjectId");
			String clientId = getenvNoThrow("AzureIdentityClientId");
			String miResId = getenvNoThrow("AzureIdentityMiResId");
			this.tokenMap = getRest_ManagedIdentityToken(apiVersion, resource, objectId, clientId, miResId, sb);
			this.azureBearerToken = CH.getOrThrow(Caster_String.INSTANCE, this.tokenMap, KEY_ACCESS_TOKEN);
		} else
			throw new RuntimeException("Please provide -DAzureAuth equal to Token, ClientSecret or Identity");

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
			throw new RuntimeException("Must Specify -D" + string + " when using the AzureVaultRestDecrypter plugin");
		return r;
	}
	public static void main(String[] args) {
		AzureVaultRestDecrypter mydecrypter = new AzureVaultRestDecrypter();

		String test = "mytext";
		String enc = mydecrypter.encryptString(test);
		System.out.println(enc);
		String dec = mydecrypter.decryptString(enc);
		System.out.println(dec);
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
		Map<String, Object> result = this.getRest_decrypt(this.keyName, this.keyVersion, this.apiVersion, this.alg, encrypted);
		String decrypted = CH.getOrNoThrow(Caster_String.INSTANCE, result, KEY_VALUE, null);
		if (decrypted == null)
			return null;

		byte[] decoded = EncoderUtils.decode64(decrypted);
		return decoded;
	}

	public String encryptString(String text) {
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
		String encoded = EncoderUtils.encode64(bytes);
		Map<String, Object> result = this.getRest_encrypt(this.keyName, this.keyVersion, this.apiVersion, this.alg, encoded);
		String encrypted = CH.getOrNoThrow(Caster_String.INSTANCE, result, KEY_VALUE, null);
		return encrypted;
	}

	public byte[] encrypt(String text) {
		if (text == null)
			return null;
		String encString = this.encryptString(text);
		if (encString == null)
			return null;
		return encString.getBytes();
	}

	// https://docs.microsoft.com/en-us/azure/active-directory/managed-identities-azure-resources/how-to-use-vm-token
	private Map<String, Object> getRest_ManagedIdentityToken(String apiVersion, String resource, String objectId, String clientId, String miResId, StringBuilder sb) {
		SH.clear(sb);
		// 1 HTTP Method
		byte httpMethod = RestHelper.HTTP_GET;

		// 2 Build URL
		String urlBase = "http://169.254.169.254/metadata/identity";
		String urlExt = "/oauth2/token?api-version=";
		sb.append(urlBase).append(urlExt).append(apiVersion).append("&resource=").append(resource);

		// 3 HTTP Headers
		Map<String, String> httpHeaders = new HashMap<String, String>();
		httpHeaders.put("Metadata", "true");

		// 4 HTTP Body
		Map<String, String> bodyParamsMap = new HashMap<String, String>();
		if (SH.is(objectId))
			bodyParamsMap.put("object_id", objectId);
		if (SH.is(clientId))
			bodyParamsMap.put("client_id", clientId);
		if (SH.is(miResId))
			bodyParamsMap.put("mi_res_id", miResId);
		String bodyParams = SH.joinMap("&", "=", bodyParamsMap);

		sb.append(bodyParams);
		String urlString = sb.toString();
		SH.clear(sb);

		boolean ignoreCerts = false;
		int timeout = -1;
		Map<String, List<String>> returnHeadersSink = new LinkedHashMap<String, List<String>>();
		byte[] data = RestHelper.sendRestRequest(httpMethod, urlString, httpHeaders, bodyParams, ignoreCerts, timeout, returnHeadersSink, this.debug);

		return (Map<String, Object>) RestHelper.parseRestResponse(data, debug);

	}

	private Map<String, Object> getRest_encrypt(String keyName, String keyVersion, String apiVersion, String algo, String value) {
		SH.clear(sb);
		// 1 HTTP Method
		byte httpMethod = RestHelper.HTTP_POST;

		// 2 Build URL
		//	urlExt = "/keys/${keyName}/${keyVersion}/decrypt?api-version=7.2";
		sb.append(urlBase).append("keys/").append(keyName).append('/').append(keyVersion).append("/encrypt?api-version=").append(apiVersion);
		String urlString = sb.toString();
		SH.clear(sb);

		// 3 HTTP Headers
		Map<String, String> httpHeaders = new HashMap<String, String>();
		RestHelper.addBearerAuthentication(httpHeaders, this.azureBearerToken);
		RestHelper.addContentType(httpHeaders, RestHelper.CONTENT_TYPE_JSON);

		// 4 HTTP Body
		Map<String, String> bodyParamsMap = new HashMap<String, String>();
		bodyParamsMap.put("alg", algo);
		bodyParamsMap.put("value", value);
		String bodyParams = ObjectToJsonConverter.INSTANCE_CLEAN.objectToString(bodyParamsMap);
		//		System.out.println(bodyParams);

		boolean ignoreCerts = false;
		int timeout = -1;
		Map<String, List<String>> returnHeadersSink = new LinkedHashMap<String, List<String>>();
		byte[] data = RestHelper.sendRestRequest(httpMethod, urlString, httpHeaders, bodyParams, ignoreCerts, timeout, returnHeadersSink, this.debug);

		return (Map<String, Object>) RestHelper.parseRestResponse(data, debug);
	}
	private Map<String, Object> getRest_decrypt(String keyName, String keyVersion, String apiVersion, String algo, String value) {
		SH.clear(sb);
		// 1 HTTP Method
		byte httpMethod = RestHelper.HTTP_POST;

		// 2 Build URL
		//	urlExt = "/keys/${keyName}/${keyVersion}/decrypt?api-version=7.2";
		sb.append(urlBase).append("keys/").append(keyName).append('/').append(keyVersion).append("/decrypt?api-version=").append(apiVersion);
		String urlString = sb.toString();
		SH.clear(sb);

		// 3 HTTP Headers
		Map<String, String> httpHeaders = new HashMap<String, String>();
		RestHelper.addBearerAuthentication(httpHeaders, this.azureBearerToken);
		RestHelper.addContentType(httpHeaders, RestHelper.CONTENT_TYPE_JSON);

		// 4 HTTP Body
		Map<String, String> bodyParamsMap = new HashMap<String, String>();
		bodyParamsMap.put("alg", algo);
		bodyParamsMap.put("value", value);
		String bodyParams = ObjectToJsonConverter.INSTANCE_CLEAN.objectToString(bodyParamsMap);
		//		System.out.println(bodyParams);

		boolean ignoreCerts = false;
		int timeout = -1;
		Map<String, List<String>> returnHeadersSink = new LinkedHashMap<String, List<String>>();
		byte[] data = RestHelper.sendRestRequest(httpMethod, urlString, httpHeaders, bodyParams, ignoreCerts, timeout, returnHeadersSink, this.debug);

		return (Map<String, Object>) RestHelper.parseRestResponse(data, debug);
	}
}
