package com.f1.ami.center.hdb.qry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.center.hdb.AmiHdbIndex;
import com.f1.ami.center.hdb.AmiHdbPartition;
import com.f1.utils.IntArrayList;
import com.f1.utils.impl.FastArrayList;

public class AmiHdbResultSet_In implements AmiHdbResultSet {

	private Iterator<AmiHdbPartition> partitions;
	private AmiHdbPartition table;
	private IntArrayList rows = new IntArrayList();
	private FastArrayList<Comparable> symValues = new FastArrayList<Comparable>(10);
	private AmiHdbPartition currentPartition;
	private Iterable<Comparable> values;
	private AmiHdbIndex index;

	public AmiHdbResultSet_In(AmiHdbIndex index, Set<Comparable> values) {
		this.index = index;
		this.values = values;
	}

	@Override
	public boolean next(int limit) {
		while (this.partitions.hasNext()) {
			this.currentPartition = this.partitions.next();
			this.rows.clear();
			this.symValues.setSizeNoCheck(0);
			for (Iterator<Comparable> i = values.iterator(); i.hasNext() && limit > 0;) {
				Comparable value = i.next();
				int[] find = this.currentPartition.getIndexByName(index.getName()).find(value);
				limit -= addEntries(rows, symValues, limit, value, find);
			}
			if (this.rows.size() > 0)
				return true;
		}
		rows.clear();
		return false;
	}

	public static int addEntries(IntArrayList rowsSink, FastArrayList<Comparable> valSink, int remainingNeeded, Comparable value, int[] rows) {
		if (rows.length > remainingNeeded) {
			rowsSink.addAll(rows, 0, remainingNeeded);
			for (int n = 0; n < remainingNeeded; n++)
				valSink.add(value);
			return remainingNeeded;
		} else {
			rowsSink.addAll(rows);
			for (int n = 0; n < rows.length; n++)
				valSink.add(value);
			return rows.length;
		}
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
			int n = 0;
			for (Iterator<Comparable> i = values.iterator(); i.hasNext();) {
				Comparable value = i.next();
				int[] find = this.currentPartition.getIndexByName(index.getName()).find(value);
				n += find.length;

			}
			if (n > 0)
				return n;
		}
		return 0;
	}

	@Override
	public void init(List<AmiHdbPartition> partitions2) {
		this.partitions = partitions2.iterator();
	}

	@Override
	public Map<Comparable, int[]> getKeys() {
		Map<Comparable, int[]> r = new HashMap<Comparable, int[]>();
		for (Comparable i : values) {
			int[] t = this.currentPartition.getIndexByName(index.getName()).find(i);
			if (t.length > 0)
				r.put(i, t);
		}
		return r;
	}

	@Override
	public Comparable getIndexValueAt(int i) {
		return this.symValues.get(i);
	}

	@Override
	public int getCurrentRowAt(int i) {
		return this.rows.getInt(i);
	}

	@Override
	public int getCurrentRowsCount() {
		return this.rows.size();
	}

}
