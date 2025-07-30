package com.f1.ami.amicommon;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.f1.utils.AbstractCaster;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public class AmiCaster_Set extends AbstractCaster<Set> {

	public static final AmiCaster_Set INSTANCE = new AmiCaster_Set();

	public AmiCaster_Set() {
		super(Set.class);
	}

	@Override
	protected Set castInner(Object o, boolean throwExceptionOnError) {
		if (o instanceof CharSequence) {
			try {
				Object r = ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject((CharSequence) o);
				return r instanceof Collection ? new LinkedHashSet((Collection) r) : null;
			} catch (Exception e) {
			}
		} else if (o instanceof Collection)
			return new LinkedHashSet((Collection) o);
		return null;
	}

}
