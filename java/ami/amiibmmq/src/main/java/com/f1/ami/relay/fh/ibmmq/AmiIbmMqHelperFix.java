package com.f1.ami.relay.fh.ibmmq;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.DefaultMessageFactory;
import quickfix.Field;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.field.ApplVerID;

public class AmiIbmMqHelperFix extends AmiIbmMqHelper {
	private void populateMapFromFieldMap(Map<String, String> map, FieldMap fieldMap, DataDictionary dict) {
		Iterator<Field<?>> fieldIterator = fieldMap.iterator();
		while (fieldIterator.hasNext()) {
			Field<?> field = fieldIterator.next();
			int tag = field.getTag();
			String fieldName = dict.getFieldName(tag);
			String value = null;
			try {
				value = fieldMap.getString(tag);
			} catch (FieldNotFound e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			map.put(fieldName != null ? fieldName : String.valueOf(tag), value);
		}

		if (fieldMap instanceof Message) {
			Message message = (Message) fieldMap;
			populateMapFromFieldMap(map, message.getHeader(), dict);
			populateMapFromFieldMap(map, message.getTrailer(), dict);
		}
	}

	@Override
	public boolean parseMessage(Object value, Map<String, Object> parts, StringBuilder errorSink) {
		Message messageFromMessageUtils = null;
		DefaultMessageFactory messageFactory = new DefaultMessageFactory(ApplVerID.FIX44);
		Map<String, String> messageMap = new HashMap<>();

		try {
			DataDictionary dict = new DataDictionary("FIX44.xml");
			messageFromMessageUtils = quickfix.MessageUtils.parse(messageFactory, null, (String) value);

			// Convert message fields to a HashMap
			populateMapFromFieldMap(messageMap, messageFromMessageUtils, dict);

			for (Entry<String, String> field : messageMap.entrySet()) {
				Object fieldValue = field.getValue();
				parts.put(field.getKey(), toReadable(fieldValue));
			}
		} catch (InvalidMessage | ConfigError e) {
			errorSink.append("Error parsing FIX message: ").append(e.getMessage()).append("\n");
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			errorSink.append(e.getMessage());
			return false;
		}
		return true;
	}

}
