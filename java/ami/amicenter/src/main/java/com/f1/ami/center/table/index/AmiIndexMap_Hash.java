package com.f1.ami.center.table.index;

import java.util.HashMap;

import com.f1.utils.EH;

public class AmiIndexMap_Hash extends HashMap<Comparable, AmiIndexMap> implements AmiIndexMap {

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
		long r = 5 * 4 + 2 * EH.ADDRESS_SIZE;
		for (AmiIndexMap i : this.values())
			r += i.getMemorySize();
		r += this.size() * (EH.ADDRESS_SIZE * 4 + EH.ESTIMATED_GC_OVERHEAD * 2);
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
