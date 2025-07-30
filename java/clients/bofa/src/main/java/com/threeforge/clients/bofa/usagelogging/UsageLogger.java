package com.threeforge.clients.bofa.usagelogging;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.customobjects.AmiScriptAccessible;
import com.f1.ami.web.AmiWebScriptManagerForLayout;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.auth.AmiSsoSession;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.concurrent.FastThreadPool;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.rest.RestHelper;

@AmiScriptAccessible(name = "UsageLogger")
public class UsageLogger {
	private static Logger log = LH.get();
	private static String PROPERTY_BOFAUSAGE = "ami.amiscript.bofausage.";
	private static String PROPERTY_USAGE_URL = "url";
	private static String PROPERTY_USAGE_ENDPOINT = "endpoint";
	private static String PROPERTY_USAGE_DEBUG = "debug";
	private static String PROPERTY_USAGE_ENV = "env";

	//	private Thread thread;
	private AmiSsoSession session;
	private String application;
	private AmiWebService service;
	private String env;
	private String url;
	private String usageEndpoint;
	private boolean debug;
	private static FastThreadPool threadPool;
	static {
		threadPool = new FastThreadPool(10, "BOFA");
		threadPool.setFastExecuteMode(false);
		threadPool.start();
		//		UsageLogger.initProperties();
	}

	public static void main(String[] args) throws MalformedURLException {
	}
	@AmiScriptAccessible(name = "UsageLogger", params = { "session" })
	public UsageLogger(AmiWebService service) throws Exception {
		try {
			this.service = service;
			this.session = this.service.getVarsManager().getSsoSession();
			String layout = this.service.getAmiLayoutFullAlias();
			AmiWebScriptManagerForLayout scriptManager = this.service.getScriptManager(layout);

			Map<String, Object> layoutVariables = scriptManager.getLayoutVariableValues();
			this.application = (String) CH.getOrThrow(layoutVariables, "_APP",
					"Required: UsageLogger requires the layout have the `_APP` variable set, this is the layout description");

			PropertyController props = this.service.getPortletManager().getTools().getSubPropertyController(PROPERTY_BOFAUSAGE);
			this.url = props.getRequired(UsageLogger.PROPERTY_USAGE_URL);
			this.env = props.getRequired(UsageLogger.PROPERTY_USAGE_ENV);
			this.usageEndpoint = props.getOptional(UsageLogger.PROPERTY_USAGE_ENDPOINT, "/api/UsageAction");
			this.debug = props.getOptional(UsageLogger.PROPERTY_USAGE_DEBUG, false);
		} catch (Exception e) {
			LH.warning(log, "Error initializing UsageLogger ", e);
			throw e;
		}

	}

	@AmiScriptAccessible(name = "getEnv", params = {})
	public String getEnv() {
		return this.env;
	}
	@AmiScriptAccessible(name = "getApplication", params = {})
	public String getApplication() {
		return this.application;
	}

	@AmiScriptAccessible(name = "logUsageAction", params = { "component", "feature", "primaryAttribute", "message", "attributes", "cookies", "useToken", "returnHeadersSink" })
	public void logUsageAction(String component, String feature, String primaryAttribute, String message, Map<String, String> attributes, String cookie, boolean useToken,
			Map myReturnHeadersSink) {
		this.logUsageActionHelperJob(component, feature, primaryAttribute, message, attributes, null, cookie, useToken, myReturnHeadersSink);
	}

	@AmiScriptAccessible(name = "logUsageActionHeaders", params = { "component", "feature", "primaryAttribute", "message", "attributes", "headers", "useToken",
			"returnHeadersSink" })
	public void logUsageAction(String component, String feature, String primaryAttribute, String message, Map<String, String> attributes, Map headers, boolean useToken,
			Map myReturnHeadersSink) {
		this.logUsageActionHelperJob(component, feature, primaryAttribute, message, attributes, headers, null, useToken, myReturnHeadersSink);
	}

	@AmiScriptAccessible(name = "logUsageAction", params = { "component", "feature", "primaryAttribute", "message", "attributes" })
	public void logUsageAction(String component, String feature, String primaryAttribute, String message, Map<String, String> attributes) {
		this.logUsageActionHelperJob(component, feature, primaryAttribute, message, attributes, null, null, true, null);
	}
	private void logUsageActionHelperJob(String component, String feature, String primaryAttribute, String message, Map<String, String> attributes, Map headers, String cookie,
			boolean useToken, Map myReturnHeadersSink) {
		long currentTime = EH.currentTimeMillis();
		LogRequest r = new LogRequest(this.url, this.usageEndpoint, this.env, this.application, component, feature, primaryAttribute, message, attributes, headers, cookie,
				useToken, myReturnHeadersSink, currentTime, debug);
		UsageLogger.threadPool.execute(r);
	}

	class LogRequest implements Runnable {

		private String component;
		private String feature;
		private String primaryAttribute;
		private String message;
		private Map<String, String> attributes;
		private String cookie;
		private boolean useToken;
		private Map myReturnHeadersSink;
		private boolean debug;
		private String usageEndpoint;
		private String url;
		private String application;
		private String env;
		private long currentTime;
		private Map headers;

		public LogRequest(String url, String usageEndpoint, String env, String application, String component, String feature, String primaryAttribute, String message,
				Map<String, String> attributes, Map headers, String cookie, boolean useToken, Map myReturnHeadersSink, long currentTime, boolean debug) {
			this.url = url;
			this.usageEndpoint = usageEndpoint;
			this.env = env;
			this.application = application;
			this.component = component;
			this.feature = feature;
			this.primaryAttribute = primaryAttribute;
			this.message = message;
			this.attributes = attributes;
			this.cookie = cookie;
			this.headers = headers;
			this.useToken = useToken;
			this.myReturnHeadersSink = myReturnHeadersSink;
			this.currentTime = currentTime;
			this.debug = debug;
			//TODO headers
		}

		public void run() {
			byte httpMethod = RestHelper.HTTP_POST;

			String endpoint = this.url + this.usageEndpoint;

			Map<String, String> httpHeaders = new HashMap<String, String>();
			if (this.headers != null)
				CH.putAllMissing(httpHeaders, this.headers);

			if (!httpHeaders.containsKey("Content-Type"))
				RestHelper.addContentType(httpHeaders, "application/json");

			if (!httpHeaders.containsKey("Accept"))
				httpHeaders.put("Accept", "application/json, text/plain, */*");

			if (useToken)
				RestHelper.addBearerAuthentication(httpHeaders, session.getAccessToken());
			if (SH.is(cookie))
				httpHeaders.put("Cookie", cookie);

			// 4 HTTP Body
			Map<String, Object> bodyParamsMap = new LinkedHashMap<String, Object>();
			bodyParamsMap.put("application", this.application);
			bodyParamsMap.put("component", component);
			bodyParamsMap.put("feature", feature);
			bodyParamsMap.put("primaryAttribute", primaryAttribute);
			bodyParamsMap.put("message", message);

			LinkedHashMap<String, String> finalAttributes = new LinkedHashMap<String, String>();
			if (CH.isntEmpty(attributes))
				CH.putAllMissing(finalAttributes, attributes);
			finalAttributes.put("ENV", SH.toUpperCase(this.env));
			finalAttributes.put("AMI_CURRENT_TIME", SH.s(this.currentTime));
			bodyParamsMap.put("attributes", finalAttributes);

			String bodyParams = ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(bodyParamsMap);

			boolean ignoreCerts = false;
			int timeout = -1;
			Map<String, List<String>> returnHeadersSink = myReturnHeadersSink == null ? new LinkedHashMap<String, List<String>>() : myReturnHeadersSink;
			try {
				if (this.debug) {
					LH.warning(log, "DEBUG: HTTP/s METHOD: " + httpMethod);
					LH.warning(log, "DEBUG: HTTP/s URL: " + endpoint);
					LH.warning(log, "DEBUG: HTTP/s HEADERS: " + ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(httpHeaders));
					LH.warning(log, "DEBUG: HTTP/s PARAMS: " + bodyParams);
					LH.warning(log, "DEBUG: HTTP/s IGNORE_CERTS: " + ignoreCerts);
				}
				byte[] data = RestHelper.sendRestRequest(httpMethod, endpoint, httpHeaders, bodyParams, ignoreCerts, timeout, returnHeadersSink, this.debug);

				Map<String, Object> myResult = (Map<String, Object>) RestHelper.parseRestResponse(data, this.debug);
				if (this.debug) {
					LH.warning(log, "DEBUG: HTTP/s Result: " + ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(myResult));
					LH.warning(log, "DEBUG: HTTP/s ReturnHeaders: " + ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(returnHeadersSink));
				}
				return;

			} catch (Exception e) {
				LH.warning(log, "Exception logging usage ", e);
				return;
			}

		}

	}

}