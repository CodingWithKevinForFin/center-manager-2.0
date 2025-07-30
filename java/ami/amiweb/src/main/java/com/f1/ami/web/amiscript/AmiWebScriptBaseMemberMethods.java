package com.f1.ami.web.amiscript;

import java.util.ArrayList;
import java.util.List;

import com.f1.ami.amiscript.AmiScriptBaseMemberMethods;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public abstract class AmiWebScriptBaseMemberMethods<T> extends AmiScriptBaseMemberMethods<T> {

	private final List<ParamsDefinition> callbackDefinitions = new ArrayList<ParamsDefinition>();

	final protected void registerCallbackDefinition(ParamsDefinition callback) {
		this.callbackDefinitions.add(callback);
	}

	public AmiWebScriptBaseMemberMethods() {
		super();
	}

	public List<ParamsDefinition> getCallbackDefinitions() {
		return this.callbackDefinitions;
	}

}
