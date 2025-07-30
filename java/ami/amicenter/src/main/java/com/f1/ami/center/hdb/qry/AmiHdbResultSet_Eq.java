package com.f1.ami.center.hdb.qry;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.f1.ami.center.hdb.AmiHdbIndex;
import com.f1.ami.center.hdb.AmiHdbPartition;
import com.f1.utils.IntArrayList;
import com.f1.utils.impl.FastArrayList;

public class AmiHdbResultSet_Eq implements AmiHdbResultSet {

	private Iterator<AmiHdbPartition> partitions;
	private AmiHdbPartition table;
	private IntArrayList rows = new IntArrayList();
	private AmiHdbPartition currentPartition;
	private FastArrayList<Comparable> symValues = new FastArrayList<Comparable>(10);
	private AmiHdbIndex index;
	private Comparable val;

	public AmiHdbResultSet_Eq(AmiHdbIndex indexForIndex, Comparable val) {
		this.index = indexForIndex;
		this.val = val;
	}

	@Override
	public boolean next(int limit) {
		while (this.partitions.hasNext()) {
			this.currentPartition = this.partitions.next();
			this.rows.clear();
			this.symValues.setSizeNoCheck(0);
			if (limit == 0)
				break;
			int[] i = currentPartition.getIndexByName(index.getName()).find(val);
			if (i.length > 0) {
				limit -= AmiHdbResultSet_In.addEntries(rows, symValues, limit, val, i);
				return true;
			}
		}
		this.rows.clear();
		return false;

	}

	@Override
	public AmiHdbPartition getCurrentPartition() {
		return this.currentPartition;
	}

	@Override
	public String getIndexColumn() {
		return this.index.getColumn().getName();
	}

	@Override
	public int nextOnlyCount() {
		while (this.partitions.hasNext()) {
			this.currentPartition = this.partitions.next();
			this.rows.clear();
			this.symValues.setSizeNoCheck(0);
			int[] i = currentPartition.getIndexByName(index.getName()).find(val);
			if (i.length > 0)
				return i.length;
		}
		this.rows.clear();
		return 0;
	}

	@Override
	public void init(List<AmiHdbPartition> partitions2) {
		this.partitions = partitions2.iterator();
	}
	@Override
	public Map<Comparable, int[]> getKeys() {
		int[] i = currentPartition.getIndexByName(index.getName()).find(val);
		if (i.length > 0) {
			Map<Comparable, int[]> r = new HashMap<Comparable, int[]>();
			r.put(val, i);
			return r;
		} else
			return Collections.EMPTY_MAP;

	}

	@Override
	public Comparable getIndexValueAt(int i) {
		return this.symValues.get(i);
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
