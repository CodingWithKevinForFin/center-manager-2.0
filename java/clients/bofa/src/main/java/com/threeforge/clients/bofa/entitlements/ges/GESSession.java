package com.threeforge.clients.bofa.entitlements.ges;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.customobjects.AmiScriptAccessible;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.rest.RestHelper;

@AmiScriptAccessible(name = "BofaGES")
public class GESSession implements Runnable {
	private static final Logger log = LH.get();
	private int intervalSeconds;
	private String url;
	private String clientId;
	private String clientKey;
	private String subject;
	private String namespace;
	private boolean debug;
	private volatile boolean running = false;
	private Integer num = 0;
	private String entitlementType;
	// GES resources
	private List<String> roles;
	private List<String> groups;

	public GESSession(String url, String clientId, String clientKey, String subject, String namespace, int intervalSeconds, String entitlementType, boolean debug) {
		this.intervalSeconds = intervalSeconds;
		this.clientId = clientId;
		this.clientKey = clientKey;
		this.subject = subject;
		this.namespace = namespace;
		this.url = url;
		this.entitlementType = entitlementType;
		this.debug = debug;
		LH.info(log, "Creating GES Entitlement Polling session");
	}
	public void run() {
		this.running = true;
		while (running) {
			if (!this.running)
				break;
			runInner();
			OH.sleep(intervalSeconds * 1000);
		}
	}
	public void runInner() {
		try {
			LH.info(log, "Polling GES data for " + this.subject);
			if (pollGES())
				LH.info(log, "Polling GES sucessful for " + this.subject);
			else
				LH.info(log, "Polling GES failed for " + this.subject);
		} catch (Exception e) {
			LH.warning(log, "Exception polling for GES for ", this.subject, "url:", this.url);
		}
	}
	private void test() {
		num++;
	}
	@AmiScriptAccessible(name = "getNum", params = {})
	public int getNum() {
		return num;
	}
	@AmiScriptAccessible(name = "getUrl", params = {})
	public String getUrl() {
		return url;
	}
	@AmiScriptAccessible(name = "getClientId", params = {})
	public String getClientId() {
		return clientId;
	}
	@AmiScriptAccessible(name = "getClientKey", params = {})
	public String getClientKey() {
		return clientKey;
	}
	@AmiScriptAccessible(name = "getSubject", params = {})
	public String getSubject() {
		return subject;
	}
	@AmiScriptAccessible(name = "getNamespace", params = {})
	public String getNamespace() {
		return namespace;
	}
	@AmiScriptAccessible(name = "getEntitlementType", params = {})
	public String getEntitlementType() {
		return entitlementType;
	}
	public void setRunning(boolean running) {
		this.running = running;
	}
	@AmiScriptAccessible(name = "getRoles", params = {})
	public List<String> getRoles() {
		return this.roles;
	}
	@AmiScriptAccessible(name = "getGroups", params = {})
	public List<String> getGroups() {
		return this.groups;
	}
	private boolean pollGES() {
		try {
			refreshRoles(this.subject);
			refreshGroups(this.subject);
			return true;
		} catch (Exception e) {
			LH.warning(log, "Error polling GES service for " + subject + " url: " + this.url);
			return false;
		}
	}
	private void refreshRoles(String subject) {
		try {
			String url = this.url + "/Entitlements/" + this.namespace + "/" + subject + "/Roles";
			if (debug)
				LH.info(log, "Built url for getRoles: " + url);
			Map<String, String> httpHeaders = createHttpHeaders();
			if (debug)
				LH.info(log, "Sending REST request for getRoles: " + httpHeaders);
			List response = (List) sendRestRequest(url, httpHeaders);
			if (debug)
				LH.info(log, "Received response for getRoles: " + response);
			this.roles = response;
		} catch (Exception e) {
			LH.warning(log, "Error sending REST request for getRoles: " + e);
		}
	}
	private void refreshGroups(String subject) {
		try {
			String url = this.url + "/Entitlements/" + this.namespace + "/" + subject + "/Groups";
			if (debug)
				LH.info(log, "Built url for getGroups: " + url);
			Map<String, String> httpHeaders = createHttpHeaders();
			if (debug)
				LH.info(log, "Sending REST request for getGroups: " + httpHeaders);
			List response = (List) sendRestRequest(url, httpHeaders);
			if (debug)
				LH.info(log, "Received response for getGroups: " + response);
			this.groups = response;
		} catch (Exception e) {
			LH.warning(log, "Error sending REST request for getGroups: " + e);
		}
	}
	private Map<String, String> createHttpHeaders() {
		Map<String, String> httpHeaders = new HasherMap<String, String>();
		httpHeaders.put("X-GES-Client-Id", this.clientId);
		httpHeaders.put("X-GES-Client-Key", this.clientKey);
		httpHeaders.put("accept", "application/json");
		httpHeaders.put("Metadata", "true");
		return httpHeaders;
	}
	private Object sendRestRequest(String url, Map<String, String> httpHeaders) {
		boolean ignorecerts = false;
		int timeout = -1;
		Map<String, List<String>> returnHeadersSink = new LinkedHashMap<String, List<String>>();
		byte data[] = RestHelper.sendRestRequest(RestHelper.HTTP_GET, url, httpHeaders, null, ignorecerts, timeout, returnHeadersSink, this.debug);
		return RestHelper.parseRestResponse(data, debug);
	}
}
