package com.f1.utils;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.Valued;
import com.f1.base.ValuedSchema;

public class MapBackedValued extends AbstractValued {

	final private Map<String, Object> data;
	final private MapBackedValuedSchema schema;

	public MapBackedValued(Class<Valued> type, Map<String, Object> data) {
		Map<String, Object> data2 = new HashMap(data);
		for (Map.Entry<String, Object> e : data2.entrySet())
			e.setValue(OH.getClass(e.getValue()));
		this.schema = new MapBackedValuedSchema(type, (Map) data2);
		this.data = data;
	}

	@Override
	public ValuedSchema<Valued> askSchema() {
		return schema;
	}

	@Override
	public boolean putNoThrow(String name, Object value) {
		data.put(name, value);
		return true;
	}

	@Override
	public Object ask(String name) {
		return data.get(name);
	}

	@Override
	public boolean putNoThrow(byte pid_, Object value_) {
		return false;
	}

	@Override
	public Object ask(byte pid_) {
		throw new RuntimeException("pids not supported");
	}

	@Override
	public Object askAtPosition(int name) {
		throw new RuntimeException("position not supported");
	}

}

