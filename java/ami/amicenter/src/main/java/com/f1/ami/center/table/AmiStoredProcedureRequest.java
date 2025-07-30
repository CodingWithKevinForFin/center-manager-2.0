package com.f1.ami.center.table;

import java.util.Map;
import java.util.Set;

import com.f1.base.Table;
import com.f1.utils.CH;

public class AmiStoredProcedureRequest {

	final private Map<String, Table> request;
	final private Map<String, Object> debug;
	final private int limit;
	public AmiStoredProcedureRequest(Map<String, Table> request, Map<String, Object> debug, int limit) {
		this.request = request;
		this.debug = debug;
		this.limit = limit;
	}

	public int getLimit() {
		return this.limit;
	}
	public Set<String> getTableNames() {
		return this.request.keySet();
	}
	public Table getTable(String name) {
		return CH.getOrThrow(request, name, "Request Table not found: ");
	}
	public Table getTableNoThrow(String name) {
		return this.request.get(name);
	}

	public boolean isDebug() {
		return debug != null;
	}

	public void debug(String key, Object value) {
		if (debug == null)
			throw new IllegalStateException("Debug is off");
	}

}
