package com.f1.ami.relay.fh.kafka;

import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

public class AmiKafkaHelperAvro extends AmiKafkaHelper {
	// parses message received and puts it into the parts map
	@Override
	public boolean parseMessage(Object value, Map<String, Object> parts, StringBuilder errorSink) {

		// avro record
		GenericRecord record = (GenericRecord) value;
		Schema schema = record.getSchema();

		List<Schema.Field> fields = schema.getFields();

		for (Schema.Field field : fields) {
			Object fieldValue = record.get(field.name());
			// insert key value pairs into the map from the field
			parts.put(field.name(), toReadable(fieldValue));
		}
		return true;
	}
}
