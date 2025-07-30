package com.f1.ami.web.auth;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.ThreeForgeWebsiteAuthProperties;
import com.f1.container.ContainerTools;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public class ThreeForgeWebsiteAuth implements AmiAuthenticatorPlugin {
	public static Logger log = LH.get();
	private String authHost;
	private int authPort;

	public void init(ContainerTools tools, PropertyController props) {
		this.authHost = props.getRequired(ThreeForgeWebsiteAuthProperties.PROPERTY_EXTERNAL_AUTH_HOST);
		this.authPort = Caster_Integer.PRIMITIVE.cast(props.getRequired(ThreeForgeWebsiteAuthProperties.PROPERTY_EXTERNAL_AUTH_PORT));
	}

	public String getPluginId() {
		return "ThreeForgeWebsiteAuth";
	}
	public AmiAuthResponse authenticate(String namespace, String location, String user, String password) {
		try {
			LH.warning(log, "Authenicating user " + user);
			String requestUrl = this.authHost + ":" + this.authPort + "/authenticateExternal";
			URL url = new URL(requestUrl);
			String parameters = "username=" + user + "&password=" + password + "&signin="; // the appName params will be filled in the handler for website.
			byte[] data = parameters.getBytes();
			Map<String, List<String>> returnHeadersSink = new HashMap<String, List<String>>();
			byte[] responseBytes = IOH.doPost(url, null, data, returnHeadersSink, true, -1);
			String responseJsonStr = new String(responseBytes);
			Map response = (Map) ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject(responseJsonStr);
			int responseStatusCode = (Integer) response.get("status_code");
			if (SH.is(responseStatusCode)) {
				int resStatCode = responseStatusCode;
				if (externalCode2ThisCode(resStatCode) == AmiAuthResponse.STATUS_OKAY) {
					final Map<String, Object> attributes = new HashMap<String, Object>();
					attributes.put("ISDEV", "true");
					attributes.put("ISADMIN", "true");
					LH.warning(log, "Authentication succeeded ", requestUrl, " User: ", user + " Response: " + response);
					return new BasicAmiAuthResponse(AmiAuthResponse.STATUS_OKAY, null, new BasicAmiAuthUser(user, attributes));
				} else {
					LH.warning(log, "Authentication failed ", requestUrl, " User: ", user + " Response: " + response);
					return new BasicAmiAuthResponse(externalCode2ThisCode(resStatCode), null, null);
				}
			} else {
				LH.warning(log, "Authentication failed ", requestUrl, " User: ", user + " Response: " + response);
				return new BasicAmiAuthResponse(AmiAuthResponse.STATUS_GENERAL_ERROR, null, null);
			}
		} catch (Exception e) {
			LH.warning(log, "Exception authentication user server ", e);
			return new BasicAmiAuthResponse(AmiAuthResponse.STATUS_GENERAL_ERROR, null, null);
		}

	}
	public static byte externalCode2ThisCode(int responseCodeFromExternalAuth) {
		switch (responseCodeFromExternalAuth) {
			case 3:
				return AmiAuthResponse.STATUS_ACCOUNT_LOCKED;
			case 6:
				return AmiAuthResponse.STATUS_BAD_CREDENTIALS;
			case 5:
				return AmiAuthResponse.STATUS_BAD_PASSWORD;
			case 1:
			case 4:
			case 10:
				return AmiAuthResponse.STATUS_BAD_USERNAME;
			case 8:
				return AmiAuthResponse.STATUS_OKAY;
			default:
				return AmiAuthResponse.STATUS_GENERAL_ERROR;
		}
	}
}
