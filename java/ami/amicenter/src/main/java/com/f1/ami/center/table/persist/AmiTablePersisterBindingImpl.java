package com.f1.ami.center.table.persist;

import java.util.Map;

import com.f1.utils.CH;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiTablePersisterBindingImpl implements AmiTablePersisterBinding {

	final private AmiTablePersister persister;
	final private byte defType;
	final private String persisterType;
	final private Map<String, Object> options;
	final private Map<String, String> optionsMap;

	public AmiTablePersisterBindingImpl(AmiTablePersister persister, String persisterType, Map<String, Object> persisterOptions, Map<String, String> optionsMap, byte defType) {
		this.persister = persister;
		this.persisterType = persisterType;
		this.options = persisterOptions;
		this.optionsMap = optionsMap;
		this.defType = defType;
	}

	@Override
	public AmiTablePersister getPersister() {
		return persister;
	}

	@Override
	public byte getDefType() {
		return defType;
	}

	@Override
	public String getPersisterType() {
		return persisterType;
	}

	@Override
	public Map<String, Object> getOptions() {
		return options;
	}

	@Override
	public Map<String, String> getOptionsStrings() {
		return optionsMap;
	}
	@Override
	public <T> T getOption(Class<T> castType, String key) {
		return CH.getOrThrow(castType, this.options, key);
	}
	@Override
	public <T> T getOption(Class<T> castType, String key, T defaultValue) {
		return CH.getOr(castType, this.options, key, defaultValue);
	}

	@Override
	public void onTableRename(String oldName, String name, CalcFrameStack sf) {
		this.persister.onTableRename(oldName, name, sf);
	}

}
