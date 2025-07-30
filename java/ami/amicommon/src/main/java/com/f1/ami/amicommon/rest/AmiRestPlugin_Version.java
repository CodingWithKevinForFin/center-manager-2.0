package com.f1.ami.amicommon.rest;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.AmiStartup;
import com.f1.ami.web.auth.AmiAuthUser;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.ContentType;
import com.f1.utils.EnvironmentDump;
import com.f1.utils.Labeler;
import com.f1.utils.PropertyController;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public class AmiRestPlugin_Version implements AmiRestPlugin {

	private ContainerTools tools;

	@Override
	public void init(ContainerTools tools, PropertyController props) {
		this.tools = tools;

	}

	@Override
	public String getPluginId() {
		return "REST_VERSION";
	}

	@Override
	public String getEndpoint() {
		return "version";
	}

	@Override
	public void handler(AmiRestRequest rr, AmiAuthUser user) {
		boolean b = CH.getOr(Caster_Boolean.INSTANCE, user.getAuthAttributes(), AmiAuthUser.PROPERTY_ISADMIN, Boolean.FALSE);
		if (!b) {
			rr.error("Permission Denied: ISADMIN required");
			return;
		}

		if (rr.isDisplayText()) {
			rr.println("3forge AMI Version: " + AmiStartup.BUILD_PROPERTY_VERSION);
			String text = EnvironmentDump.dump(null, null, this.tools);
			//			text = HttpUtils.escapeHtmlNewLineToBr(text);
			rr.println(text);
			rr.setContentType(ContentType.TEXT);
		} else {
			JsonEvnLogger sb = new JsonEvnLogger();
			EnvironmentDump.dump(null, null, this.tools, sb);
			rr.printJson(sb.values);
		}
	}

	public static class JsonEvnLogger implements EnvironmentDump.EnvLogger {

		public Map<String, Object> values = new LinkedHashMap<String, Object>();

		public JsonEvnLogger() {
		}

		public String toJson(boolean compact) {
			if (compact)
				return ObjectToJsonConverter.INSTANCE_COMPACT_SORTING.objectToString(values);
			else
				return ObjectToJsonConverter.INSTANCE_CLEAN_SORTING.objectToString(values);
		}

		@Override
		public void log(String label, Object value) {
			values.put(label, value);

		}

		@Override
		public void log(String label, Object[] value) {
			values.put(label, value);
		}

		@Override
		public void log(String label, Collection value) {
			values.put(label, value);
		}

		@Override
		public void log(String value) {
		}

		@Override
		public void logHeader() {
		}

		@Override
		public void logLabeler(String label, Labeler value) {
			Map<String, Object> m = new HashMap<String, Object>();
			for (String s : value.getLabels()) {
				List<String> items = value.getItems(s);
				if (items.size() == 1)
					m.put(s, items.get(0));
				else
					m.put(s, items);
			}
			values.put(label, m);
		}

	}

	@Override
	public boolean requiresAuth() {
		return true;
	}
}
