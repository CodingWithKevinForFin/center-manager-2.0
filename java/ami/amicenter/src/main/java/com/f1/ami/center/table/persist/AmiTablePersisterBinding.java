package com.f1.ami.center.table.persist;

import java.util.Map;

import com.f1.utils.structs.table.stack.CalcFrameStack;

public interface AmiTablePersisterBinding {
	public AmiTablePersister getPersister();
	public byte getDefType();
	public String getPersisterType();
	public Map<String, Object> getOptions();
	public Map<String, String> getOptionsStrings();
	public <T> T getOption(Class<T> castType, String string);
	public <T> T getOption(Class<T> castType, String string, T defaultValue);
	public void onTableRename(String oldName, String name, CalcFrameStack sf);
}