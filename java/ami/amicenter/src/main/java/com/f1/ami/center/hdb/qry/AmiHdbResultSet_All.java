package com.f1.ami.center.hdb.qry;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.f1.ami.center.hdb.AmiHdbPartition;
import com.f1.utils.IntArrayList;

public class AmiHdbResultSet_All implements AmiHdbResultSet {

	private Iterator<AmiHdbPartition> partitions;
	private IntArrayList rows = new IntArrayList();
	private AmiHdbPartition currentPartition;

	@Override
	public boolean next(int n) {
		while (this.partitions.hasNext()) {
			this.currentPartition = this.partitions.next();
			this.rows.clear();
			int rowCount = Math.min(currentPartition.getRowCount(), n);
			if (rowCount > 0) {
				this.rows.ensureCapacity(rowCount);
				for (int i = 0; i < rowCount; i++)
					this.rows.add(i);
				return true;
			}
		}
		rows.clear();
		return false;
	}

	@Override
	public AmiHdbPartition getCurrentPartition() {
		return this.currentPartition;
	}

	@Override
	public String getIndexColumn() {
		return null;
	}

	@Override
	public int nextOnlyCount() {
		while (this.partitions.hasNext()) {
			this.currentPartition = this.partitions.next();
			this.rows.clear();
			int rowCount = currentPartition.getRowCount();
			if (rowCount > 0)
				return rowCount;
		}
		return 0;
	}

	@Override
	public void init(List<AmiHdbPartition> partitions) {
		this.partitions = partitions.iterator();
	}

	@Override
	public Map<Comparable, int[]> getKeys() {
		return null;
	}
	@Override
	public Comparable getIndexValueAt(int i) {
		return null;
	}

	@Override
	public int getCurrentRowAt(int i) {
		return this.rows.get(i);
	}

	@Override
	public int getCurrentRowsCount() {
		return this.rows.size();
	}
}
