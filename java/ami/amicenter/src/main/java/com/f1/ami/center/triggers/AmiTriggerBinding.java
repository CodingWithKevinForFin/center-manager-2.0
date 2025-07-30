package com.f1.ami.center.triggers;

import java.util.Map;

import com.f1.base.Caster;

public interface AmiTriggerBinding {

	public AmiTrigger getTrigger();
	public int getPriority();
	public int getTableNamesCount();
	public String getTableNameAt(int i);
	public int getTableNamesForBindingCount();
	public String getTableNameForBindingAt(int i);
	public String getTriggerName();
	public String getTriggerType();
	public byte getDefType();
	public Map<String, Object> getOptions();
	public <T> T getOption(Class<T> castType, String string);
	public <T> T getOption(Class<T> castType, String string, T defaultValue);
	public <T> T getOption(Caster<T> caster, String string, T defaultValue);
	boolean isSupported(byte type);
}
