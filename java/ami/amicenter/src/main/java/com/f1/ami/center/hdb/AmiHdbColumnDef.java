package com.f1.ami.center.hdb;

import com.f1.ami.center.table.AmiTableUtils;
import com.f1.base.Caster;
import com.f1.base.ToStringable;
import com.f1.utils.CasterManager;

public class AmiHdbColumnDef implements ToStringable {
	public static final byte MODE_USE_DEFAULT = 0;

	final private String name;
	final private byte amiType;
	final private byte mode;
	final private Caster<? extends Comparable> caster;
	final private Class<?> classType;

	private short amiKey;

	public AmiHdbColumnDef(short amikey, String name, byte type) {
		this(amikey, name, type, AmiHdbUtils.getDefaultMode(type));
	}
	public AmiHdbColumnDef(short amikey, String name, byte type, byte mode) {
		this.name = name;
		this.amiKey = amikey;
		this.amiType = type;
		this.mode = mode;
		this.classType = AmiTableUtils.getClassForValueType(this.amiType);
		this.caster = (Caster<? extends Comparable>) CasterManager.getCaster(classType);
	}

	public String getName() {
		return name;
	}

	public byte getMode() {
		return mode;
	}

	public byte getAmiType() {
		return amiType;
	}

	public short getAmiKey() {
		return amiKey;
	}

	public Class getTypeClass() {
		return this.classType;
	}

	public Caster<? extends Comparable> getCaster() {
		return this.caster;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		return sink.append(name);
	}

}
