package com.f1.ami.center.hdb.qry;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.f1.ami.center.hdb.AmiHdbPartition;
import com.f1.utils.CH;
import com.f1.utils.structs.Tuple2;

public class AmiHdbResultSet_WrapIndex implements AmiHdbResultSet {
	final private String indexColumn;
	private Iterator<Tuple2<AmiHdbPartition, int[]>> iterator;
	private Comparable key;
	private AmiHdbPartition currentPartition;
	private int[] currentRows;

	public AmiHdbResultSet_WrapIndex(String indexColumn) {
		this.indexColumn = indexColumn;
	}
	@Override
	public AmiHdbPartition getCurrentPartition() {
		return this.currentPartition;
	}

	@Override
	public String getIndexColumn() {
		return this.indexColumn;
	}

	@Override
	public void init(List<AmiHdbPartition> partitions) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean next(int limit) {
		while (iterator.hasNext()) {
			Tuple2<AmiHdbPartition, int[]> t = iterator.next();
			this.currentPartition = t.getA();
			this.currentRows = t.getB();
			return true;
		}
		this.currentRows = null;
		this.currentPartition = null;
		return false;
	}

	@Override
	public int nextOnlyCount() {
		while (iterator.hasNext()) {
			Tuple2<AmiHdbPartition, int[]> t = iterator.next();
			this.currentPartition = t.getA();
			this.currentRows = t.getB();
			return this.currentRows.length;
		}
		this.currentRows = null;
		this.currentPartition = null;
		return 0;
	}

	@Override
	public Map<Comparable, int[]> getKeys() {
		return CH.m(this.key, this.currentRows);
	}
	public Comparable getCurrentKey() {
		return this.key;
	}
	public void reset(Comparable key2, List<Tuple2<AmiHdbPartition, int[]>> value) {
		this.currentRows = null;
		this.currentPartition = null;
		this.iterator = value.iterator();
		this.key = key2;
	}
	@Override
	public Comparable getIndexValueAt(int i) {
		return this.key;
	}
	@Override
	public int getCurrentRowAt(int i) {
		return this.currentRows[i];
	}
	@Override
	public int getCurrentRowsCount() {
		return this.currentRows.length;
	}

}
