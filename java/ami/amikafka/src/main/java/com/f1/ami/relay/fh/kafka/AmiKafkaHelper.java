package com.f1.ami.relay.fh.kafka;

import java.util.Map;

import com.f1.utils.OH;

public class AmiKafkaHelper {
	public boolean parseMessage(Object value, Map<String, Object> parts, StringBuilder errorSink) {
		return true;
	}
	
	// converts any object to its particular type for AMI.
	// If it is not one of the primitive types, it returns a readable string representation
	public Object toReadable(Object v) {
		if (v instanceof Number) {
			if (OH.isFloat(v.getClass()))
				v = ((Number) v).doubleValue();
			else if (!(v instanceof Integer || v instanceof Long || v instanceof Short || v instanceof Byte))
				v = ((Number) v).longValue();
		} else if (v != null && !(v instanceof Boolean)) {
			v = v.toString();
		}
		return v;
	}
}