package com.f1.ami.amicommon;

import com.f1.utils.casters.Caster_String;

public class AmiCaster_String extends Caster_String {

	public static final AmiCaster_String INSTANCE = new AmiCaster_String();

	@Override
	protected String castInner(Object o, boolean throwExceptionOnError) {
		return AmiUtils.sJson(o);
	}

}
