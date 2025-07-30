package com.f1.ami.amicommon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.f1.utils.AbstractCaster;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public class AmiCaster_List extends AbstractCaster<List> {

	public static final AmiCaster_List INSTANCE = new AmiCaster_List();

	public AmiCaster_List() {
		super(List.class);
	}

	@Override
	protected List castInner(Object o, boolean throwExceptionOnError) {
		if (o instanceof CharSequence) {
			try {
				Object r = ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject((CharSequence) o);
				return r instanceof List ? (List) r : null;
			} catch (Exception e) {
			}
		} else if (o instanceof Collection)
			return new ArrayList((Collection) o);
		return null;
	}

}
