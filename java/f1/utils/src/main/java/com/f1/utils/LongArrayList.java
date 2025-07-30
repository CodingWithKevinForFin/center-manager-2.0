package com.f1.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import com.f1.base.LongIterator;
import com.f1.base.ToStringable;

public class LongArrayList implements List<Long>, ToStringable {

	private long[] array;
	private int size;

	public LongArrayList() {
		array = OH.EMPTY_LONG_ARRAY;
	}
	public LongArrayList(long[] array) {
		this.array = array.clone();
		this.size = array.length;
	}
	public LongArrayList(long[] array, int start, int end) {
		this.size = end - start;
		this.array = new long[Math.max(size, 1)];
		System.arraycopy(array, start, this.array, 0, size);
	}
	public LongArrayList(int initialCapacity) {
		this.array = new long[initialCapacity];
	}
	public LongArrayList(LongArrayList d) {
		this(d.array, 0, d.size);
	}
	@Override
	public int size() {
		return size;
	}

	public LongArrayList setSize(int size) {
		if (size < 0)
			throw new IndexOutOfBoundsException(SH.toString(size));
		ensureCapacity(size);
		this.size = size;
		return this;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}
	public boolean isntEmpty() {
		return size != 0;
	}

	@Override
	public boolean contains(Object o) {
		if (o instanceof Long) {
			long l = (Long) o;
			for (int i = 0; i < size; i++)
				if (array[i] == l)
					return true;
		}
		return false;
	}

	@Override
	public LongIterator iterator() {
		return new LongArrayIterator();
	}

	@Override
	public Object[] toArray() {
		throw new ToDoException();
	}

	public long[] toLongArray() {
		if (size == 0)
			return OH.EMPTY_LONG_ARRAY;
		return Arrays.copyOf(array, size);
	}
	public int[] toIntArray() {
		if (size == 0)
			return OH.EMPTY_INT_ARRAY;
		final int[] r = new int[size];
		for (int i = 0; i < size; i++)
			r[i] = (int) array[i];
		return r;
	}

	public short[] toShortArray() {
		if (size == 0)
			return OH.EMPTY_SHORT_ARRAY;
		final short[] r = new short[size];
		for (int i = 0; i < size; i++)
			r[i] = (short) array[i];
		return r;
	}

	public byte[] toByteArray() {
		if (size == 0)
			return OH.EMPTY_BYTE_ARRAY;
		final byte[] r = new byte[size];
		for (int i = 0; i < size; i++)
			r[i] = (byte) array[i];
		return r;
	}
	public boolean[] toBooleanArray() {
		if (size == 0)
			return OH.EMPTY_BOOLEAN_ARRAY;
		final boolean[] r = new boolean[size];
		for (int i = 0; i < size; i++)
			r[i] = array[i] != 0;
		return r;
	}
	public char[] toCharArray() {
		if (size == 0)
			return OH.EMPTY_CHAR_ARRAY;
		final char[] r = new char[size];
		for (int i = 0; i < size; i++)
			r[i] = (char) array[i];
		return r;
	}
	public float[] toFloatArray() {
		if (size == 0)
			return OH.EMPTY_FLOAT_ARRAY;
		final float[] r = new float[size];
		for (int i = 0; i < size; i++)
			r[i] = (float) array[i];
		return r;
	}
	public double[] toDoubleArray() {
		if (size == 0)
			return OH.EMPTY_DOUBLE_ARRAY;
		final double[] r = new double[size];
		for (int i = 0; i < size; i++)
			r[i] = (double) array[i];
		return r;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new ToDoException();
	}

	@Override
	public boolean add(Long e) {
		return add((long) e);
	}

	public boolean add(long e) {
		ensureCapacity(size + 1);
		array[size++] = e;
		return true;
	}
	public boolean addAll(long[] values) {
		if (values.length == 0)
			return false;
		ensureCapacity(size + values.length);
		System.arraycopy(values, 0, array, size, values.length);
		size += values.length;
		return true;
	}

	@Override
	public boolean remove(Object o) {
		if (o instanceof Long)
			return removeLong((long) (Long) o);
		return false;
	}

	public boolean removeLong(long o) {
		int i = indexOf(o);
		if (i == -1)
			return false;
		remove(i);
		return true;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c)
			if (!contains(o))
				return false;
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends Long> c) {
		int numMoved = c.size();
		if (numMoved == 0)
			return false;
		ensureCapacity(size + numMoved);
		if (c instanceof LongArrayList) {
			LongArrayList lal = (LongArrayList) c;
			System.arraycopy(lal.array, 0, array, size, numMoved);
			size += numMoved;
			return true;
		}
		final Iterator<? extends Long> i = c.iterator();
		if (i instanceof LongIterator) {
			final LongIterator li = (LongIterator) i;
			while (li.hasNext())
				array[size++] = li.nextLong();
		} else
			while (i.hasNext())
				array[size++] = i.next();
		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends Long> c) {
		int numMoved = c.size();
		if (numMoved == 0)
			return false;
		ensureCapacity(size + numMoved);
		System.arraycopy(array, index, array, index + numMoved, numMoved);
		if (c instanceof LongArrayList) {
			LongArrayList lal = (LongArrayList) c;
			System.arraycopy(lal.array, 0, array, index, numMoved);
			size += numMoved;
			return true;
		}
		final Iterator<? extends Long> i = c.iterator();
		if (i instanceof LongIterator) {
			final LongIterator li = (LongIterator) i;
			while (li.hasNext())
				array[index++] = li.nextLong();
		} else
			while (i.hasNext())
				array[index++] = i.next();
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new ToDoException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new ToDoException();
	}

	@Override
	public void clear() {
		size = 0;

	}

	@Override
	public Long get(int index) {
		return getLong(index);
	}
	public long getLong(int index) {
		if (index < 0 || index > size)
			throw new IndexOutOfBoundsException(index + " for array of size " + size);
		return array[index];
	}

	@Override
	public Long set(int index, Long element) {
		Long r = get(index);
		set(index, (long) element);
		return r;
	}

	public void set(int index, long element) {
		array[index] = element;
	}

	@Override
	public void add(int index, Long element) {
		add(index, (long) element);
	}

	public void add(int index, long element) {
		if (index < 0 || index > size)
			throw new IndexOutOfBoundsException(index + " for array of size " + size);
		ensureCapacity(size + 1);
		System.arraycopy(array, index, array, index + 1, size - index);
		size++;
		array[index] = element;
	}

	@Override
	public Long remove(int index) {
		if (index < 0 || index >= size)
			throw new IndexOutOfBoundsException(index + " for array of size " + size);
		long oldValue = array[index];

		if (--size > index)
			System.arraycopy(array, index + 1, array, index, size - index);

		return oldValue;
	}

	public long removeAt(int index) {
		if (index < 0 || index >= size)
			throw new IndexOutOfBoundsException(index + " for array of size " + size);
		long oldValue = array[index];

		if (--size > index)
			System.arraycopy(array, index + 1, array, index, size - index);

		return oldValue;
	}
	@Override
	public int indexOf(Object o) {
		return (o instanceof Long) ? indexOf((long) (Long) o) : -1;
	}

	public int indexOf(long v) {
		for (int i = 0; i < size; i++) {
			if (array[i] == v)
				return i;
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		return (o instanceof Long) ? lastIndexOf((long) (Long) o) : -1;
	}

	public int lastIndexOf(long v) {
		for (int i = size - 1; i >= 0; i--)
			if (array[i] == v)
				return i;
		return -1;
	}

	@Override
	public ListIterator<Long> listIterator() {
		return new LongArrayIterator();
	}

	@Override
	public ListIterator<Long> listIterator(int index) {
		throw new ToDoException();
	}

	@Override
	public List<Long> subList(int fromIndex, int toIndex) {
		throw new ToDoException();
	}

	private class LongArrayIterator implements LongIterator, ListIterator<Long> {
		int loc = 0;

		@Override
		public long nextLong() {
			if (loc >= size)
				throw new NoSuchElementException();
			return array[loc++];
		}

		@Override
		public boolean hasNext() {
			return loc < size;
		}

		@Override
		public Long next() {
			if (loc >= size)
				throw new NoSuchElementException();
			return array[loc++];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasPrevious() {
			return loc > 0;
		}

		@Override
		public Long previous() {
			if (loc == 0)
				throw new NoSuchElementException();
			return array[--loc];
		}

		@Override
		public int nextIndex() {
			return loc;
		}

		@Override
		public int previousIndex() {
			return loc - 1;
		}

		@Override
		public void set(Long e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(Long e) {
			throw new UnsupportedOperationException();
		}
	}

	public void ensureCapacity(int minCapacity) {
		int oldCapacity = array.length;
		if (minCapacity > oldCapacity) {
			if (oldCapacity < 10 && minCapacity < 10)
				minCapacity = 10;
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if (newCapacity < minCapacity)
				newCapacity = minCapacity;
			array = Arrays.copyOf(array, newCapacity);
		}
	}

	@Override
	public String toString() {
		if (size == 0)
			return "[]";
		return toString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		if (size == 0)
			return sink.append("[]");
		sink.append("[");
		final int last = size - 1;
		for (int i = 0; i < last; i++)
			sink.append(array[i]).append(", ");
		return sink.append(array[last]).append("]");
	}
	//returns true if data changed as a result
	public boolean isSorted() {
		return AH.isSorted(this.array, 0, this.size);
	}
	//returns true if data changed as a result
	public boolean sort() {
		if (isSorted())
			return false;
		Arrays.sort(this.array, 0, this.size);
		return true;
	}
}
