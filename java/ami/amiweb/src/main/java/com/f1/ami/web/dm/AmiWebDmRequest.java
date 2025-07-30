package com.f1.ami.web.dm;

import java.util.HashMap;
import java.util.Map;

import com.f1.utils.concurrent.HasherMap;

public class AmiWebDmRequest {

	final private HasherMap<String, Object> variables = new HasherMap<String, Object>();

	public Object putVariable(String name, Object value) {
		return this.variables.put(name, value);
	}

	public HasherMap<String, Object> getVariables() {
		return this.variables;
	}

	public Map<String, Object> getVariablesForOnProcess(String defaultTrue) {
		final Object where = variables.get(AmiWebDmsImpl.WHERE);
		final HasherMap<String, Object> inputs = new HasherMap<String, Object>();
		inputs.put(AmiWebDmsImpl.WHERES, new HashMap<String, Object>(variables));
		inputs.put(AmiWebDmsImpl.WHERE, where == null && !variables.containsKey(AmiWebDmsImpl.WHERE) ? defaultTrue : where);
		return inputs;
	}
}
