package com.f1.ami.relay.fh.kafka;

import java.util.Map;
import java.util.Map.Entry;

import com.f1.utils.OH;

public class AmiKafkaHelperJson extends AmiKafkaHelper {

	// parses message received and puts it into the parts map
	@SuppressWarnings("unchecked")
	@Override
	public boolean parseMessage(Object value, Map<String, Object> parts, StringBuilder errorSink) {
		// casting the polled data to a JSON map
		Map<String, Object> deserializedJson = (Map<String, Object>) value;
		try {
			for (Entry<String, Object> field : deserializedJson.entrySet()) {
				Object fieldValue = field.getValue();
				// making each field 'readable'
				parts.put(field.getKey(), toReadable(fieldValue));
			}
		} catch (Exception e) {
			errorSink.append(e.getMessage());
			return false;
		}
		return true;
	}
}
