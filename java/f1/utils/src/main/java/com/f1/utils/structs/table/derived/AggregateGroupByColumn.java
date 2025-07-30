package com.f1.utils.structs.table.derived;

import com.f1.base.Table;
import com.f1.utils.structs.table.BasicColumn;

public class AggregateGroupByColumn extends BasicColumn {

	private int innerColumnLocation;
	private String innerColumnId;
	private boolean enabled = true;

	public AggregateGroupByColumn(Table table, int uid, int location, Class type, String id, String innerColumnId) {
		super(table, uid, location, type, id);
		this.innerColumnId = innerColumnId;
		innerColumnLocation = -1;
	}

	public int getInnerColumnLocation() {
		return innerColumnLocation;
	}

	public void setInnerColumnLocation(int groupedColumnLocation) {
		this.innerColumnLocation = groupedColumnLocation;
	}

	public String getInnerColumnId() {
		return innerColumnId;
	}

	public StringBuilder toCalcString(StringBuilder sb) {
		return sb.append("groupby(").append(innerColumnId).append(')');
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean getEnabled() {
		return enabled;
	}

}
