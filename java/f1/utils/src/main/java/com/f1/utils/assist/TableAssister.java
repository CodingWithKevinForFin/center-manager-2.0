/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.assist;

import java.util.IdentityHashMap;
import java.util.List;

import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.utils.IndentedStringBuildable;
import com.f1.utils.TableHelper;
import com.f1.utils.ToDoException;
import com.f1.utils.structs.table.BasicTable;

public class TableAssister implements Assister<Table> {
	final private RootAssister rootAssister;

	public TableAssister(RootAssister rootAssister) {
		this.rootAssister = rootAssister;
	}

	@Override
	public Object getNestedValue(Table o, String value, boolean throwOnError) {
		if ("rows".equals(value))
			return o.getRows();
		else if ("columns".equals(value))
			return o.getColumns();
		else if ("size".equals(value))
			return o.getSize();
		else if ("title".equals(value))
			return o.getTitle();
		else
			return null;
	}

	@Override
	public void toString(Table o, StringBuilder sb, IdentityHashMap<Object, Object> visited) {
		TableHelper.toString(o, "", TableHelper.SHOW_ALL, sb);
	}

	@Override
	public void toLegibleString(Table o, IndentedStringBuildable sb, IdentityHashMap<Object, Object> visited, int maxlength) {
		sb.setLength(sb.length());//TODO: why???
		sb.dontIndent();
		TableHelper.toString(o, sb.getIndentString(), TableHelper.SHOW_ALL, sb.getInner(), maxlength - sb.length());
	}

	@Override
	public Table clone(Table o, IdentityHashMap<Object, Object> visited) {
		return new BasicTable(o);
	}

	@Override
	public void toJson(Table o, StringBuilder sb) {
		List<Column> columns = o.getColumns();
		int columnsCount = columns.size();
		sb.append('[');
		TableList rows = o.getRows();
		int rowsCount = rows.size();
		for (int y = 0; y < rowsCount; y++) {
			Row row = rows.get(y);
			if (y > 0)
				sb.append(',');
			sb.append('{');
			for (int x = 0; x < columnsCount; x++) {
				if (x > 0)
					sb.append(',');
				sb.append(columns.get(x).getId()).append(':');
				rootAssister.toJson(row.getAt(x), sb);
			}
			sb.append('}');

		}
		sb.append(']');
	}

	@Override
	public Object toMapList(Table o_, boolean storeNulls_, String keyForClassNameOrNull_) {
		throw new ToDoException();
	}
}
