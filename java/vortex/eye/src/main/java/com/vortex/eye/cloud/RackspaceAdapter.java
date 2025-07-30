package com.vortex.eye.cloud;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import com.f1.container.impl.AbstractContainerScope;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.vortexcommon.msg.eye.VortexEyeCloudInterface;
import com.f1.vortexcommon.msg.eye.VortextEyeCloudMachineInfo;

public class RackspaceAdapter implements CloudAdapter {

	//String str = "eGbq9/2hcZsRlr1JV1PiRackspace Management Interface20010308143725";
	//String key = "QHOvchm/40czXhJ1OxfxK7jDHr3t";
	//System.out.println(hash );
	public static final String DATETIME_LONG_FULL_FORMAT = "yyyyMMddHHmmss";

	public static void main(String a[]) throws Exception {

		String userName = "rcooke";
		String apiKey = "5a36bbae283bf92d71ba07c82e71f86b";
		System.out.println(new RackspaceAdapter().getIps(userName, apiKey));
	}

	public List<String> getIps(String userName, String apiKey) throws IOException {
		String input = "{\"auth\":{\"RAX-KSKEY:apiKeyCredentials\":{\"username\":\"" + userName + "\", \"apiKey\":\"" + apiKey + "\"}}}\"";
		byte[] data = get("https://identity.api.rackspacecloud.com/v2.0/tokens", "POST", CH.m(new HashMap<String, String>(), "Content-Type", "application/json"), input.getBytes());
		ObjectToJsonConverter converter = new ObjectToJsonConverter();
		Map<String, String> urlsToTenantIds = new HashMap<String, String>();
		String id = null;
		{
			Object obj = converter.bytes2Object(data);
			Object access = ((Map) obj).get("access");
			Object token = ((Map) access).get("token");
			id = (String) ((Map) token).get("id");
			Object serviceCatalog = ((Map) access).get("serviceCatalog");
			for (Object o : (List) serviceCatalog) {
				Map m = (Map) o;
				if ("cloudServersOpenStack".equals(m.get("name"))) {
					List<Map> endpoints = (List<Map>) m.get("endpoints");
					for (Map endpoint : endpoints) {
						String tenantId = (String) endpoint.get("tenantId");
						String publicUrl = (String) endpoint.get("publicURL");
						urlsToTenantIds.put(publicUrl, tenantId);
					}
					break;
				}
			}
		}

		List<String> ips = new ArrayList<String>();
		// curl -s https://ord.servers.api.rackspacecloud.com/v2/837850/servers/detail -H "X-Auth-Token: 0e40c7a05f4244b0bf287223a89154f6"
		for (Map.Entry<String, String> e : urlsToTenantIds.entrySet()) {
			byte[] val = get(e.getKey() + "/servers/detail", "GET", CH.m(new HashMap<String, String>(), "X-Auth-Token", id), null);
			Object obj = converter.bytes2Object(val);
			List<Map> servers = (List<Map>) ((Map) obj).get("servers");
			if (CH.isEmpty(servers))
				continue;
			for (Map server : servers) {
				Map addresses = (Map) server.get("addresses");
				List<Map> publec = (List) addresses.get("public");
				for (Map addr : publec) {
					if (((Number) addr.get("version")).intValue() == 4)
						ips.add((String) addr.get("addr"));
				}
			}

		}
		return ips;
	}

	public static byte[] get(String url, String method, Map<String, String> headers, byte[] input) throws IOException {
		URL url2 = new URL(url);
		HttpsURLConnection conn = (HttpsURLConnection) url2.openConnection();
		try {
			conn.setRequestMethod(method);
			conn.setDoInput(true);
			conn.setDoOutput(AH.isntEmpty(input));
			if (CH.isntEmpty(headers))
				for (Map.Entry<String, String> e : headers.entrySet())
					conn.setRequestProperty(e.getKey(), e.getValue());

			conn.connect();
			if (AH.isntEmpty(input))
				conn.getOutputStream().write(input);
			byte[] data = IOH.readData(conn.getInputStream());
			return data;
		} finally {
		}
	}

	@Override
	public List<String> getMachinesInCloud(VortexEyeCloudInterface ci) throws Exception {
		Map<String, String> params = ci.getParameters();
		return getIps(params.get("username"), params.get("apikey"));
	}

	@Override
	public List<VortextEyeCloudMachineInfo> getMachineInfoList(AbstractContainerScope scope, VortexEyeCloudInterface ci) throws Exception {
		return null;
	}

	@Override
	public void stopMachine(VortextEyeCloudMachineInfo mi, VortexEyeCloudInterface ci) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startMachine(VortextEyeCloudMachineInfo mi, VortexEyeCloudInterface ci) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startMoreLikeThis(VortextEyeCloudMachineInfo mi, VortexEyeCloudInterface ci, String name, int numberOfInstances) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void terminateMachine(VortextEyeCloudMachineInfo mi, VortexEyeCloudInterface ci) throws Exception {
		// TODO Auto-generated method stub

	}

}
