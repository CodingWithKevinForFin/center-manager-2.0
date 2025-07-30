package com.f1.ami.relay.fh.ibmmq;

import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class AmiIbmMqHelperXml extends AmiIbmMqHelper {

	// Flatten the nested map
	private void flattenMap(Map<String, Object> source, Map<String, Object> target, String prefix) {
		for (Entry<String, Object> entry : source.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			String newKey = prefix.isEmpty() ? key : prefix + "_" + key;

			if (value instanceof Map) {
				// If the value is a Map, recurse into it
				flattenMap((Map<String, Object>) value, target, newKey);
			} else {
				// Otherwise, put the value in the target map
				target.put(newKey, toReadable(value));
			}
		}
	}

	// parses message received and puts it into the parts map
	@SuppressWarnings("unchecked")
	@Override
	public boolean parseMessage(Object value, Map<String, Object> parts, StringBuilder errorSink) {
		// casting the polled data to a JSON map
		XmlMapper xmlMapper = new XmlMapper();

		try {
			Map<String, Object> deserializedJson = xmlMapper.readValue(value.toString(), Map.class);
			for (Entry<String, Object> field : deserializedJson.entrySet()) {
				flattenMap(deserializedJson, parts, "");
			}
		} catch (Exception e) {
			errorSink.append(e.getMessage());
			return false;
		}
		return true;
	}

}