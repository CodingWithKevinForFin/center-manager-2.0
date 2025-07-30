package com.f1.ami.amicommon;

import java.util.Map;

import com.f1.utils.AbstractCaster;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public class AmiCaster_Map extends AbstractCaster<Map> {
	public static final AmiCaster_Map INSTANCE = new AmiCaster_Map();

	public AmiCaster_Map() {
		super(Map.class);
	}

	@Override
	protected Map castInner(Object o, boolean throwExceptionOnError) {
		if (o instanceof CharSequence) {
			try {
				Object r = ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject((CharSequence) o);
				return r instanceof Map ? (Map) r : null;
			} catch (Exception e) {
			}
		}
		return null;
	}

}
