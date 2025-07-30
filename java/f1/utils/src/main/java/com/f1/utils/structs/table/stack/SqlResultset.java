package com.f1.utils.structs.table.stack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.f1.base.Table;
import com.f1.utils.CH;
import com.f1.utils.sql.TableReturn;

public class SqlResultset {

	private List<Table> tables = null;
	private List<Object> generatedKeys = null;
	private long rowsEffected;

	public void appendTable(TableReturn r) {
		rowsEffected += r.getRowsEffected();
		if (CH.isntEmpty(r.getTables())) {
			if (tables == null)
				tables = new ArrayList<Table>(r.getTables());
			else
				tables.addAll(r.getTables());
		}
		if (CH.isntEmpty(r.getGenerateKeys())) {
			if (generatedKeys == null)
				generatedKeys = new ArrayList<Object>(r.getGenerateKeys());
			else
				generatedKeys.addAll(r.getGenerateKeys());
		}
	}

	public List<Table> getTables() {
		return tables == null ? Collections.EMPTY_LIST : tables;
	}

	public long getRowsEffected() {
		return rowsEffected;
	}

	public List<Object> getGenerateKeys() {
		return generatedKeys;
	}

	public void reset() {
		tables = null;
		generatedKeys = null;
		rowsEffected = 0;
	}

}
