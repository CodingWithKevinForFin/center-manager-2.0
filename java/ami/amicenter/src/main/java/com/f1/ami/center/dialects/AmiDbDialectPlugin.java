package com.f1.ami.center.dialects;

import com.f1.ami.amicommon.AmiPlugin;

public interface AmiDbDialectPlugin extends AmiPlugin {

	public AmiDbDialect createDialectInstance();

}
