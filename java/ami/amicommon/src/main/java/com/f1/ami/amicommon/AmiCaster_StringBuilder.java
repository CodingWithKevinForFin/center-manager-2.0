package com.f1.ami.amicommon;

import com.f1.utils.casters.Caster_StringBuilder;

public class AmiCaster_StringBuilder extends Caster_StringBuilder {

	public static final AmiCaster_StringBuilder INSTANCE = new AmiCaster_StringBuilder();

	@Override
	protected StringBuilder castInner(Object o, boolean throwExceptionOnError) {
		return AmiUtils.s(o, new StringBuilder());
	}

}
