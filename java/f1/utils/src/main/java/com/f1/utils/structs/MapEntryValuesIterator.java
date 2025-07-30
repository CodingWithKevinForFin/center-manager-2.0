package com.f1.utils.structs;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.base.IterableAndSize;
import com.f1.utils.SH;

public class MapEntryValuesIterator<V> implements Iterator<V> {

	final private Iterator<? extends Entry<?, V>> inner;

	public MapEntryValuesIterator(Iterator<? extends Map.Entry<?, V>> inner) {
		this.inner = inner;
	}
	@Override
	public boolean hasNext() {
		return inner.hasNext();
	}

	@Override
	public V next() {
		return inner.next().getValue();
	}

	@Override
	public void remove() {
		inner.remove();
	}

	public static class Iterable<V> implements IterableAndSize<V> {

		private IterableAndSize<? extends Entry<?, V>> inner;
		public Iterable(IterableAndSize<? extends Map.Entry<?, V>> inner) {
			this.inner = inner;
		}
		@Override
		public Iterator<V> iterator() {
			return new MapEntryValuesIterator<V>(inner.iterator());
		}
		@Override
		public int size() {
			return inner.size();
		}

		public String toString() {
			if (inner.size() == 0)
				return "{}";
			if (inner.size() == 1)
				return "{" + this.iterator().next() + "}";
			StringBuilder sb = new StringBuilder();
			sb.append('{');
			SH.join(',', this, sb);
			sb.append('}');
			return sb.toString();
		}

	}

	public static class IterableCollection<V> implements IterableAndSize<V> {

		private Collection<? extends Entry<?, V>> inner;
		public IterableCollection(Collection<? extends Map.Entry<?, V>> inner) {
			this.inner = inner;
		}
		@Override
		public Iterator<V> iterator() {
			return new MapEntryValuesIterator<V>(inner.iterator());
		}
		@Override
		public int size() {
			return inner.size();
		}

	}

	public static <V> Iterable<V> toIterable(IterableAndSize<? extends Map.Entry<?, V>> t) {
		return new Iterable<V>(t);
	}

}
