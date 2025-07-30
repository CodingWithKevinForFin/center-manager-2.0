package com.f1.utils.casters;

import java.io.File;

import com.f1.utils.AbstractCaster;

public class Caster_File extends AbstractCaster<File> {

	public static final Caster_File INSTANCE = new Caster_File();

	public Caster_File() {
		super(File.class);
	}

	@Override
	protected File castInner(Object o, boolean throwExceptionOnError) {
		return new File(o.toString());
	}

}
