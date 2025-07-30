package com.f1.ami.center.hdb;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.f1.utils.AH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.HasherMap.Entry;
import com.f1.utils.structs.ComparableComparator;

public class AmiHdbColumn_Partition extends AmiHdbColumn {

	private static final AmiHdbPartition[] EMPTY = new AmiHdbPartition[0];
	final private HasherMap<Comparable, AmiHdbPartition[]> partitions = new HasherMap<Comparable, AmiHdbPartition[]>();
	final private TreeMap<Comparable, AmiHdbPartition[]> partitionsSorted = new TreeMap<Comparable, AmiHdbPartition[]>((Comparator) ComparableComparator.INSTANCE);
	final private int partitionIndex;

	public AmiHdbColumn_Partition(int partitionIndex, AmiHdbTable table, int position, AmiHdbColumnDef col) {
		super(table, position, col);
		this.partitionIndex = partitionIndex;
	}

	public void addPartition(AmiHdbPartition partition) {
		final Comparable key = partition.getPartitionsKey()[this.partitionIndex];
		final Entry<Comparable, AmiHdbPartition[]> entry = partitions.getOrCreateEntry(key);
		AmiHdbPartition[] val = entry.getValue();
		if (val == null)
			entry.setValue(val = new AmiHdbPartition[] { partition });
		else
			entry.setValue(val = AH.append(val, partition));
		partitionsSorted.put(key, val);
	}
	public void removePartition(AmiHdbPartition partition) {
		final Comparable<?> key = partition.getPartitionsKey()[this.partitionIndex];
		AmiHdbPartition[] e = partitions.get(key);
		e = AH.remove(e, AH.indexOf(partition, e));
		if (e.length == 0) {
			partitions.remove(key);
			partitionsSorted.remove(key);
		} else {
			partitions.put(key, e);
			partitionsSorted.put(key, e);
		}
	}

	public AmiHdbPartition[] getPartitions(Comparable c) {
		AmiHdbPartition[] r = partitions.get(cast(c));
		if (r == null)
			return EMPTY;
		else
			return r;
	}

	public Iterator<Map.Entry<Comparable, AmiHdbPartition[]>> getPartitions(boolean ascending, Comparable min, boolean minInclusive, Comparable max, boolean maxInclusive) {

		final NavigableMap<Comparable, AmiHdbPartition[]> m;
		if (min != null) {
			if (max != null)
				m = partitionsSorted.subMap(cast(min), minInclusive, cast(max), maxInclusive);
			else
				m = partitionsSorted.tailMap(cast(min), minInclusive);
		} else {
			if (max != null)
				m = partitionsSorted.headMap(cast(max), maxInclusive);
			else
				m = this.partitionsSorted;
		}
		if (ascending)
			return m.entrySet().iterator();
		else
			return m.descendingMap().entrySet().iterator();
	}
	public Iterator<Map.Entry<Comparable, AmiHdbPartition[]>> getPartitions() {
		return partitionsSorted.entrySet().iterator();
	}

	public void onRowsCleared() {
		this.partitions.clear();
		this.partitionsSorted.clear();
	}

	public int getPartionIndex() {
		return this.partitionIndex;
	}

}
