package com.f1.ami.center.hdb.qry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.f1.ami.center.hdb.AmiHdbIndex;
import com.f1.ami.center.hdb.AmiHdbPartition;
import com.f1.ami.center.hdb.idx.AmiHdbIndexEntry;
import com.f1.ami.center.hdb.idx.AmiHdbPartitionIndex;
import com.f1.utils.EmptyIterator;
import com.f1.utils.IntArrayList;
import com.f1.utils.OH;
import com.f1.utils.impl.FastArrayList;

public class AmiHdbResultSet_Scan implements AmiHdbResultSet {

	private Iterator<AmiHdbPartition> partitions;
	private AmiHdbPartition table;
	private IntArrayList rows = new IntArrayList();
	private AmiHdbPartition currentPartition;
	private FastArrayList<Comparable> symValues = new FastArrayList<Comparable>(10);
	private Iterator<AmiHdbIndexEntry> currentIterator;
	private AmiHdbIndex index;
	private AmiHdbQueryPart comp;
	private boolean asc;

	public AmiHdbResultSet_Scan(AmiHdbIndex indexForIndex, AmiHdbQueryPart comp) {
		this.currentIterator = EmptyIterator.INSTANCE;
		this.index = indexForIndex;
		this.comp = comp;
	}

	@Override
	public boolean next(int limit) {
		while (this.partitions.hasNext()) {
			this.currentPartition = this.partitions.next();
			this.rows.clear();
			this.symValues.setSizeNoCheck(0);
			if (limit <= 0)
				break;
			AmiHdbPartitionIndex idx = currentPartition.getIndexByName(index.getName());
			Iterable<Comparable> values = idx.getKeys();
			for (Comparable val : values) {
				if (limit <= 0)
					break;
				if (comp == null || comp.matches(val)) {
					int[] i = currentPartition.getIndexByName(index.getName()).find(val);
					OH.assertGt(i.length, 0);
					limit -= AmiHdbResultSet_In.addEntries(rows, symValues, limit, val, i);
				}
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
			this.rows.clear();
			this.symValues.setSizeNoCheck(0);
			AmiHdbPartitionIndex idx = currentPartition.getIndexByName(index.getName());
			Iterable<Comparable> values = idx.getKeys();
			int n = 0;
			for (Comparable val : values) {
				if (comp.matches(val)) {
					int[] i = currentPartition.getIndexByName(index.getName()).find(val);
					OH.assertGt(i.length, 0);
					n += i.length;
				}
			}
			if (n > 0)
				return n;
		}
		rows.clear();
		return 0;
	}

	@Override
	public void init(List<AmiHdbPartition> partitions2) {
		this.partitions = partitions2.iterator();
	}

	@Override
	public Map<Comparable, int[]> getKeys() {
		AmiHdbPartitionIndex idx = currentPartition.getIndexByName(index.getName());
		Iterable<Comparable> values = idx.getKeys();
		Map<Comparable, int[]> r = new HashMap<Comparable, int[]>();
		for (Comparable val : values) {
			if (comp == null || comp.matches(val)) {
				int[] i = currentPartition.getIndexByName(index.getName()).find(val);
				r.put(val, i);
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
		return this.rows.getInt(i);
	}

	@Override
	public int getCurrentRowsCount() {
		return this.rows.size();
	}
}
