package com.f1.ami.web.dm;

import com.f1.ami.web.AmiWebObject;
import com.f1.base.Caster;
import com.f1.base.Table;
import com.f1.utils.OH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.table.columnar.ColumnarColumn;
import com.f1.utils.structs.table.columnar.ColumnarRow;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.columnar.ReadonlyTable;

public class AmiWebRtTable {

	private ColumnarTable table;
	final private LongKeyMap<ColumnarRow> rows = new LongKeyMap<ColumnarRow>();
	final private String name;
	private ReadonlyTable roTable;

	public AmiWebRtTable(String name, com.f1.base.CalcTypes cols) {
		this.name = name;
		if (cols.isVarsEmpty())
			this.table = new ColumnarTable();
		else {
			int size = cols.getVarsCount();
			Class<?>[] types = new Class[size];
			String[] names = new String[size];
			Caster<?>[] casters = new Caster[size];
			int n = 0;
			for (String i : cols.getVarKeys()) {
				Class<?> type = cols.getType(i);
				names[n] = i;
				types[n] = type;
				casters[n] = OH.getCaster(type);
				n++;
			}
			this.table = new ColumnarTable(types, names);
		}
		this.table.setTitle(name);
		this.roTable = new ReadonlyTable(this.table);
	}

	public void updateRow(AmiWebObject row) {
		addRow(row);
	}
	public void addRow(AmiWebObject row) {
		ColumnarRow existing = rows.get(row.getUniqueId());
		if (existing != null) {
			for (int i = 0; i < table.getColumnsCount(); i++) {
				ColumnarColumn col = table.getColumnAt(i);
				Object val = col.getTypeCaster().cast(row.get(col.getId()));
				existing.putAt(i, val);
			}
		} else {
			existing = table.newEmptyRow();
			for (int i = 0; i < table.getColumnsCount(); i++) {
				ColumnarColumn col = table.getColumnAt(i);
				Object val = col.getTypeCaster().cast(row.get(col.getId()));
				existing.putAt(i, val);
			}
			table.getRows().add(existing);
			rows.put(row.getUniqueId(), existing);
		}
	}
	public void removeRow(AmiWebObject row) {
		ColumnarRow existing = rows.remove(row.getUniqueId());
		if (existing != null)
			table.removeRow(existing);
	}

	public ColumnarTable getTable() {
		return table;
	}
	public Table getReadonlyTable() {
		return roTable;
	}

	public String getName() {
		return this.name;
	}
	public void clear() {
		this.table.clear();
		this.rows.clear();
	}

}
