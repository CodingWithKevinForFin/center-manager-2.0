package com.f1.ami.center.procs;

import java.util.Map;

import com.f1.base.Caster;

public interface AmiStoredProcBinding {
	public AmiStoredProc getStoredProc();
	public String getStoredProcName();
	public String getStoredProcType();
	public byte getDefType();
	public String getReturnTypeString();
	public String getArgumentsString();
	public Map<String, Object> getOptions();
	public <T> T getOption(Class<T> castType, String string);
	public <T> T getOption(Class<T> castType, String string, T defaultValue);
	public <T> T getOption(Caster<T> caster, String string, T defaultValue);
}
