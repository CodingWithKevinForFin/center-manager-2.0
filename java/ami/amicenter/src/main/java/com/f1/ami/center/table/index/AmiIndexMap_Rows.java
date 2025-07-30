package com.f1.ami.center.table.index;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.EmptyIterable;
import com.f1.utils.OH;
import com.f1.utils.structs.IntKeyMap;

final public class AmiIndexMap_Rows implements AmiIndexMap {

	AmiRowImpl single = null;
	private IntKeyMap<AmiRowImpl> values;
	public final AmiIndexMap[] maps;//don't need to store the root map, so this array is one less that the keys
	public final Comparable[] keys;

	@Override
	public long getMemorySize() {
		long r = EH.ADDRESS_SIZE * 4;
		r += (keys.length) * EH.ADDRESS_SIZE + EH.ESTIMATED_GC_OVERHEAD;
		r += (maps.length) * EH.ADDRESS_SIZE + EH.ESTIMATED_GC_OVERHEAD;
		if (values != null)
			r += EH.ESTIMATED_GC_OVERHEAD + values.getMemorySize();
		return r;
	}
	public AmiIndexMap_Rows(AmiIndexMap[] parents, Comparable keys[], AmiRowImpl single) {
		this.maps = parents;
		this.keys = keys;
		this.single = single;
	}

	@Override
	public void removeIndex(Comparable key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public AmiIndexMap getIndex(Comparable value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putIndex(Comparable key, AmiIndexMap value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isIndexEmpty() {
		throw new UnsupportedOperationException();
	}

	public void put(AmiRowImpl row) {
		if (values == null) {
			values = new IntKeyMap<AmiRowImpl>();
			values.put(row.getUid(), row);
			values.put(single.getUid(), single);
			single = null;
		} else
			values.put(row.getUid(), row);

	}

	//true if empty
	public boolean remove(int uid) {
		if (single != null) {
			OH.assertEq(uid, single.getUid());
			return true;
		} else {
			values.remove(uid);
			return values.size() == 0;
		}
	}

	public int fill(List<AmiRow> sink, int limit, AmiQueryScanner scanner) {
		if (single != null) {
			if (limit > 0 && (scanner == null || scanner.matches(single)))
				sink.add(single);
			return 1;
		}
		if (scanner != null) {
			int added = 0;
			for (AmiRowImpl row : values.values()) {
				if (!scanner.matches(row))
					continue;
				sink.add(row);
				if (++added == limit)
					break;
			}
			return added;
		} else {
			int size = Math.min(limit, values.size());
			Iterator<AmiRowImpl> it = values.valuesIterator();
			limit -= size;
			while (size-- > 0)
				sink.add(it.next());
			return size;
		}
	}

	@Override
	public Iterable<Comparable> getKeysForDebug() {
		return CH.l(this.keys);
	}

	public AmiRowImpl getSingleValue() {
		return this.single;
	}
	@Override
	public boolean getRows(AmiQueryFinder finder, AmiQueryFinderVisitor visitor) {
		if (single != null)
			return visitor.add(single);
		for (AmiRowImpl row : values.values())
			if (!visitor.add(row))
				return false;
		return true;
	}
	@Override
	public Iterable<AmiIndexMap> getValuesForDebug() {
		return EmptyIterable.INSTANCE;
	}
	@Override
	public int getKeysCount() {
		return single != null ? 1 : this.values.size();
	}

	public Iterable<AmiRowImpl> getValues() {
		return single != null ? Collections.singleton(single) : this.values.values();
	}

}
