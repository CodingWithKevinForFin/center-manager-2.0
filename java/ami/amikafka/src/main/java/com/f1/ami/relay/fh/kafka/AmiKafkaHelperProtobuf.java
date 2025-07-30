package com.f1.ami.relay.fh.kafka;

import java.util.Map;
import java.util.Map.Entry;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

public class AmiKafkaHelperProtobuf extends AmiKafkaHelper {
	// parses message received and puts it into the parts map
	@Override
	public boolean parseMessage(Object value, Map<String, Object> parts, StringBuilder errorSink) {
		// casting to a protobuf message
		Message p = (Message) value;
		// converting all fields to be 'readable' and inserting them into the map
		for (Entry<FieldDescriptor, Object> e : p.getAllFields().entrySet()) {
			Object v = e.getValue();
			v = toReadable(v);
			parts.put(e.getKey().getName(), v);
		}
		return true;
	}
}
