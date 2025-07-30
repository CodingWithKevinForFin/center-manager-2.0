package com.f1.utils.structs;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.f1.base.ToStringable;
import com.f1.base.ValuedHashCodeGenerator;
import com.f1.utils.MH;
import com.f1.utils.SH;
import com.f1.utils.ToDoException;

public class CompactLongKeyMap<T> implements Iterable<T>, ToStringable {
	private BigArray<T> values;
	private long size;
	private long mask;
	private final KeyGetter<? super T> keyGetter;
	private long minSize;

	public CompactLongKeyMap(String description, KeyGetter<? super T> getter, long size) {
		this.keyGetter = getter;
		long i = MH.clip(MH.getPowerOfTwoUpper(size) << 2, 16, Long.MAX_VALUE);
		this.minSize = i;
		values = BigArray.newBigArray(i);
		mask = i - 1;
	}

	public void clear() {
		if (size > 0) {
			this.size = 0;
			if (this.values.getSize() == this.minSize)
				this.values.fill(null);
			else
				this.values = BigArray.newBigArray(this.minSize);
			this.mask = this.minSize - 1;
		}
	}

	public T put(T value) {
		if (value == null)
			throw new ToDoException("Support nulls");
		final long key = keyGetter.getKey(value);
		long pos = hash(key);
		T r = values.get(pos);
		while (r != null) {
			if (keyGetter.getKey(r) == key) {
				values.set(pos, value);
				return r;
			}
			pos = pos(pos + 1);
			r = values.get(pos);
		}
		values.set(pos, value);
		if (++size >= values.getSize() >> 2)
			rehash(values.getSize() << 1);
		return r;
	}
	private void put2(T value) {
		long key = keyGetter.getKey(value);
		long pos = hash(key);
		while (values.get(pos) != null)
			pos = pos(pos + 1);
		values.set(pos, value);
	}
	private void rehash(long newSize) {
		BigArray<T> old = values;
		this.values = BigArray.newBigArray(newSize);
		mask = newSize - 1;
		for (long n = 0, s = old.getSize(); n < s; n++) {
			T value = old.get(n);
			if (value != null)
				put2(value);
		}
	}
	private final long pos(long i) {
		return i & mask;
	}
	public T get(long key) {
		long pos = hash(key);
		T r = values.get(pos);
		while (r != null) {
			if (keyGetter.getKey(r) == key)
				break;
			pos = pos(pos + 1);
			r = values.get(pos);
		}
		return r;
	}

	public T remove(long key) {
		long pos = hash(key);

		T r = values.get(pos);
		for (;;) {
			if (r == null) {
				return null;
			} else if (keyGetter.getKey(r) == key) {
				values.set(pos, null);
				break;
			} else {
				pos = pos(pos + 1);
				r = values.get(pos);
			}
		}
		if (--size <= values.getSize() >> 4 && values.getSize() > minSize) {
			rehash(values.getSize() >> 1);
		} else {
			long empty = pos;
			for (;;) {
				pos = pos(pos + 1);
				T value = values.get(pos);
				if (value == null)
					break;
				long correctPos = hash(keyGetter.getKey(value));
				if (correctPos == pos)
					continue;
				if (pos > empty) {
					if (correctPos <= empty || correctPos > pos) {
						values.set(empty, value);
						values.set(pos, null);
						empty = pos;
					}
				} else {
					if (correctPos > pos && correctPos <= empty) {
						values.set(empty, value);
						values.set(pos, null);
						empty = pos;
					}
				}
			}
		}
		return r;
	}

	private long hash(long i) {
		long n = ValuedHashCodeGenerator.rand(i);
		n += ValuedHashCodeGenerator.rand(i >> 16);
		n += ValuedHashCodeGenerator.rand(i >> 32);
		n += ValuedHashCodeGenerator.rand(i >> 48);
		return pos(n);
	}

	static public interface KeyGetter<T> {
		long getKey(T object);
	}

	public void debug() {
	}

	@Override
	public Iterator<T> iterator() {
		return new Iter();
	}

	public String toString() {
		if (size == 0)
			return "{}";
		return toString(new StringBuilder()).toString();

	}
	public StringBuilder toString(StringBuilder sb) {
		sb.append('{');
		long pos = 0;
		while (pos < values.getSize()) {
			T val = values.get(pos++);
			if (val != null) {
				SH.s(val, sb.append(getKey(val)).append('='));
				break;
			}
		}
		while (pos < values.getSize()) {
			T val = values.get(pos++);
			if (val != null)
				SH.s(val, sb.append(", ").append(getKey(val)).append('='));
		}
		sb.append('}');
		return sb;

	}

	public class Iter implements Iterator<T> {

		long pos = 0;

		public Iter() {
			while (pos < values.getSize() && values.get(pos) == null)
				pos++;
		}

		@Override
		public boolean hasNext() {
			return pos < values.getSize();
		}

		@Override
		public T next() throws NoSuchElementException {
			T r = values.get(pos++);
			while (pos < values.getSize() && values.get(pos) == null)
				pos++;
			return r;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	public long size() {
		return size;
	}

	public long getKey(T value) {
		return keyGetter.getKey(value);
	}

	public T[] toArray(T[] sink) {

		if (size > Integer.MAX_VALUE)
			throw new IndexOutOfBoundsException("too big for array: " + size);
		if (sink.length < size)
			throw new IndexOutOfBoundsException(sink.length + " < " + size);
		for (int in = 0, out = 0, count = (int) size; count > 0; in++) {
			T value = values.get(in);
			if (value == null)
				continue;
			sink[out++] = value;
			count--;
		}
		return sink;
	}

	public void ensureCapacity(long size) {
		long i = MH.clip(MH.getPowerOfTwoUpper(size) << 2, 16, Long.MAX_VALUE);
		if (i > this.values.getSize())
			rehash(i);
	}



}
