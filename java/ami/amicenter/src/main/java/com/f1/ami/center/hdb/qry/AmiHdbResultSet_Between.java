package com.f1.ami.center.hdb.qry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.f1.ami.center.hdb.AmiHdbIndex;
import com.f1.ami.center.hdb.AmiHdbPartition;
import com.f1.ami.center.hdb.idx.AmiHdbIndexEntry;
import com.f1.utils.EmptyIterator;
import com.f1.utils.IntArrayList;
import com.f1.utils.impl.FastArrayList;

public class AmiHdbResultSet_Between implements AmiHdbResultSet {

	private Iterator<AmiHdbPartition> partitions;
	private AmiHdbPartition table;
	private IntArrayList rows = new IntArrayList();
	private AmiHdbPartition currentPartition;
	private FastArrayList<Comparable> symValues = new FastArrayList<Comparable>(10);
	private boolean asc;
	private Comparable min;
	private Comparable max;
	private boolean minInc;
	private boolean maxInc;
	private Iterator<AmiHdbIndexEntry> currentIterator;
	private AmiHdbIndex index;

	public AmiHdbResultSet_Between(AmiHdbIndex indexForIndex, boolean asc, Comparable min, boolean minInc, Comparable max, boolean maxInc) {
		this.currentIterator = EmptyIterator.INSTANCE;
		this.index = indexForIndex;
		this.asc = asc;
		this.min = min;
		this.max = max;
		this.minInc = minInc;
		this.maxInc = maxInc;

	}

	@Override
	public boolean next(int limit) {
		while (this.partitions.hasNext()) {
			this.currentPartition = this.partitions.next();
			this.rows.clear();
			this.symValues.setSizeNoCheck(0);
			for (Iterator<AmiHdbIndexEntry> i = currentPartition.getIndexByName(index.getName()).findBetween(asc, min, minInc, max, maxInc); i.hasNext();) {
				AmiHdbIndexEntry entry = i.next();
				int[] value = entry.getValue();
				Comparable val = (Comparable) entry.getKey();
				limit -= AmiHdbResultSet_In.addEntries(rows, symValues, limit, val, value);
			}
			if (this.rows.size() > 0)
				return true;
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
		return this.index.getColumn().getName();
	}

	@Override
	public int nextOnlyCount() {
		while (this.partitions.hasNext()) {
			this.currentPartition = this.partitions.next();
			int n = 0;
			for (Iterator<AmiHdbIndexEntry> i = currentPartition.getIndexByName(index.getName()).findBetween(asc, min, minInc, max, maxInc); i.hasNext();)
				n += i.next().getValue().length;
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
		for (Iterator<AmiHdbIndexEntry> i = currentPartition.getIndexByName(index.getName()).findBetween(asc, min, minInc, max, maxInc); i.hasNext();) {
			AmiHdbIndexEntry entry = i.next();
			int[] value = entry.getValue();
			if (value.length > 0) {
				Comparable val = (Comparable) entry.getKey();
				r.put(val, value);
			}
		}
		return r;
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
