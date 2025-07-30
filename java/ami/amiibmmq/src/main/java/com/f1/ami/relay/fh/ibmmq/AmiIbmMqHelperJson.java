package com.f1.ami.relay.fh.ibmmq;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AmiIbmMqHelperJson extends AmiIbmMqHelper {

	// parses message received and puts it into the parts map
	@SuppressWarnings("unchecked")
	public boolean parseMessage(Object value, Map<String, Object> parts, StringBuilder errorSink) {
		Map<String, Object> toParse = null;
		try {
			toParse = (Map<String, Object>) (new ObjectMapper()).readValue((String) value, HashMap.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (toParse == null)
			return false;

		Map<String, Object> map = toParse;
		try {
			for (Map.Entry<String, Object> field : map.entrySet()) {
				Object fieldValue = field.getValue();
				parts.put(field.getKey(), toReadable(fieldValue));
			}
		} catch (Exception e) {
			errorSink.append(e.getMessage());
			return false;
		}
		return true;
	}

}