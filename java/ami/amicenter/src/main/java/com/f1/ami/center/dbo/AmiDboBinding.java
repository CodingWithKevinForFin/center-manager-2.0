package com.f1.ami.center.dbo;

import java.util.List;
import java.util.Map;

import com.f1.base.Caster;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public interface AmiDboBinding {

	public boolean isCallbackImplemented(String callbackName);
	public Object executeCallback(String callbackName, Map<String, Object> params);
	public Object executeCallbackNoThrow(String callbackName, Map<String, Object> params);
	public Map<String, Object> getOptions();
	public String getOption(String string, String defaultValue);
	public String getOption(String key);
	public <T> T getOption(Caster<T> caster, String string, T defaultValue);
	public <T> T getOption(Caster<T> caster, String key);
	public List<ParamsDefinition> getCallbackDefinitions();
	public String getDboName();
	public String getDboType();
	byte getDefType();
	int getPriority();
	boolean getIsEnabled();
}
