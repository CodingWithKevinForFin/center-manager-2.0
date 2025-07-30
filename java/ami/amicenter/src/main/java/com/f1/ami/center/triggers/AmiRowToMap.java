package com.f1.ami.center.triggers;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.f1.ami.center.table.AmiRow;
import com.f1.utils.structs.Tuple2;

public class AmiRowToMap implements Map<String, Object> {

	public class AmiRowToEntryIterator implements Iterator<Entry<String, Object>> {

		private Tuple2<String, Object> entry = new Tuple2<String, Object>();//this is reused
		private int pos = 0;

		@Override
		public boolean hasNext() {
			return pos < row.size();
		}

		@Override
		public Entry<String, Object> next() {
			entry.setAB(row.getAmiTable().getColunNameAt(pos), row.getComparable(pos));
			pos++;
			return this.entry;
		}

	}

	private AmiRow row;
	private final AmiRowToEntrySet set = new AmiRowToEntrySet();

	public void setRow(AmiRow row) {
		this.row = row;
	}

	public int size() {
		return row.size();
	}

	public boolean isEmpty() {
		return row.isEmpty();
	}

	public boolean containsKey(Object key) {
		return row.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return row.containsValue(value);
	}

	public Object get(Object key) {
		return row.get(key);
	}

	public Collection<Object> values() {
		return row.values();
	}

	public boolean equals(Object o) {
		return row.equals(o);
	}

	public int hashCode() {
		return row.hashCode();
	}

	@Override
	public Object put(String key, Object value) {
		return null;
	}

	@Override
	public Object remove(Object key) {
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
	}

	@Override
	public void clear() {
	}

	@Override
	public Set<String> keySet() {
		return (Set) row.keySet();
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return set;
	}
	@Override
	public String toString() {
		return row.toString();
	}

	public class AmiRowToEntrySet implements Set<Entry<String, Object>> {

		@Override
		public int size() {
			return row.size();
		}

		@Override
		public boolean isEmpty() {
			return row.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return row.containsKey(o);
		}

		@Override
		public Iterator<Entry<String, Object>> iterator() {
			return new AmiRowToEntryIterator();
		}

		@Override
		public Object[] toArray() {
			throw new UnsupportedOperationException();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean add(Entry<String, Object> e) {
			return false;
		}

		@Override
		public boolean remove(Object o) {
			return false;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for (Object o : c)
				if (!row.containsKey(o))
					return false;
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends Entry<String, Object>> c) {
			return false;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return false;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return false;
		}

		@Override
		public void clear() {
		}

	}

}
