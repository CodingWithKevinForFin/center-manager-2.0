package com.f1.ami.center.table.index;

import java.util.Comparator;
import java.util.TreeMap;

import com.f1.utils.EH;
import com.f1.utils.structs.ComparableComparator;

final public class AmiIndexMap_Tree extends TreeMap<Comparable, AmiIndexMap> implements AmiIndexMap {

	public AmiIndexMap_Tree() {
		super((Comparator) ComparableComparator.INSTANCE);
	}

	@Override
	public void removeIndex(Comparable key) {
		this.remove(key);
	}

	@Override
	public AmiIndexMap getIndex(Comparable value) {
		return get(value);
	}

	@Override
	public void putIndex(Comparable key, AmiIndexMap value) {
		put(key, value);
	}

	@Override
	public boolean isIndexEmpty() {
		return isEmpty();
	}

	@Override
	public Iterable<Comparable> getKeysForDebug() {
		return super.keySet();
	}

	@Override
	public boolean getRows(AmiQueryFinder finder, AmiQueryFinderVisitor visitor) {
		return finder.getRows(this, visitor);
	}
	@Override
	public long getMemorySize() {
		long r = 5 * 4 + 5 * EH.ADDRESS_SIZE;
		for (AmiIndexMap i : this.values())
			r += i.getMemorySize();
		r += this.size() * (EH.ADDRESS_SIZE * 5 + EH.ESTIMATED_GC_OVERHEAD * 2);
		return r;
	}
	@Override
	public Iterable<AmiIndexMap> getValuesForDebug() {
		return super.values();
	}

	@Override
	public int getKeysCount() {
		return super.size();
	}
}
