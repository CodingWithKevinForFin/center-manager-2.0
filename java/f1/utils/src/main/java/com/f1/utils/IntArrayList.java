package com.f1.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import com.f1.base.IntIterator;
import com.f1.base.ToStringable;

public class IntArrayList implements List<Integer>, ToStringable {

	private int[] array;
	private int size;

	public IntArrayList() {
		array = OH.EMPTY_INT_ARRAY;
	}
	public IntArrayList(int[] array) {
		this.array = array.clone();
		this.size = array.length;
	}
	public IntArrayList(int[] array, int start, int end) {
		this.size = end - start;
		this.array = new int[Math.max(size, 1)];
		System.arraycopy(array, start, this.array, 0, size);
	}
	public IntArrayList(int initialCapacity) {
		this.array = new int[initialCapacity];
	}
	public IntArrayList(IntArrayList d) {
		this(d.array, 0, d.size);
	}
	@Override
	public int size() {
		return size;
	}

	public IntArrayList setSize(int size) {
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

	@Override
	public boolean contains(Object o) {
		if (o instanceof Integer) {
			int l = (Integer) o;
			for (int i = 0; i < size; i++)
				if (array[i] == l)
					return true;
		}
		return false;
	}

	@Override
	public Iterator<Integer> iterator() {
		return new IntArrayIterator();
	}

	@Override
	public Object[] toArray() {
		throw new ToDoException();
	}

	public long[] toLongArray() {
		if (size == 0)
			return OH.EMPTY_LONG_ARRAY;
		final long[] r = new long[size];
		for (int i = 0; i < size; i++)
			r[i] = (long) array[i];
		return r;
	}
	public int[] toIntArray() {
		if (size == 0)
			return OH.EMPTY_INT_ARRAY;
		return Arrays.copyOf(array, size);
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
	public boolean add(Integer e) {
		return add((int) e);
	}

	public boolean add(int e) {
		ensureCapacity(size + 1);
		array[size++] = e;
		return true;
	}
	public boolean addAll(int[] values, int start, int length) {
		if (length <= 0)
			return false;
		ensureCapacity(size + length);
		System.arraycopy(values, start, array, size, length);
		size += length;
		return true;
	}
	public boolean addAll(int[] values) {
		if (values.length == 0)
			return false;
		ensureCapacity(size + values.length);
		System.arraycopy(values, 0, array, size, values.length);
		size += values.length;
		return true;
	}

	@Override
	public boolean remove(Object o) {
		if (o instanceof Integer)
			return removeInt((int) (Integer) o);
		return false;
	}

	public boolean removeInt(int o) {
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
	public boolean addAll(Collection<? extends Integer> c) {
		int numMoved = c.size();
		if (numMoved == 0)
			return false;
		ensureCapacity(size + numMoved);
		if (c instanceof IntArrayList) {
			IntArrayList lal = (IntArrayList) c;
			System.arraycopy(lal.array, 0, array, size, numMoved);
			size += numMoved;
			return true;
		}
		final Iterator<? extends Integer> i = c.iterator();
		if (i instanceof IntIterator) {
			final IntIterator li = (IntIterator) i;
			while (li.hasNext())
				array[size++] = li.nextInt();
		} else
			while (i.hasNext())
				array[size++] = i.next();
		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends Integer> c) {
		int numMoved = c.size();
		if (numMoved == 0)
			return false;
		ensureCapacity(size + numMoved);
		System.arraycopy(array, index, array, index + numMoved, numMoved);
		if (c instanceof IntArrayList) {
			IntArrayList lal = (IntArrayList) c;
			System.arraycopy(lal.array, 0, array, index, numMoved);
			size += numMoved;
			return true;
		}
		final Iterator<? extends Integer> i = c.iterator();
		if (i instanceof IntIterator) {
			final IntIterator li = (IntIterator) i;
			while (li.hasNext())
				array[index++] = li.nextInt();
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
	public Integer get(int index) {
		return getInt(index);
	}
	public int getInt(int index) {
		if (index < 0 || index > size)
			throw new IndexOutOfBoundsException(index + " for array of size " + size);
		return array[index];
	}

	@Override
	public Integer set(int index, Integer element) {
		Integer r = get(index);
		set(index, (int) element);
		return r;
	}

	public void set(int index, int element) {
		array[index] = element;
	}

	@Override
	public void add(int index, Integer element) {
		add(index, (int) element);
	}

	public void add(int index, int element) {
		if (index < 0 || index > size)
			throw new IndexOutOfBoundsException(index + " for array of size " + size);
		ensureCapacity(size + 1);
		System.arraycopy(array, index, array, index + 1, size - index);
		size++;
		array[index] = element;
	}

	@Override
	public Integer remove(int index) {
		if (index < 0 || index >= size)
			throw new IndexOutOfBoundsException(index + " for array of size " + size);
		int oldValue = array[index];

		if (--size > index)
			System.arraycopy(array, index + 1, array, index, size - index);

		return oldValue;
	}
	@Override
	public int indexOf(Object o) {
		return (o instanceof Integer) ? indexOf((int) (Integer) o) : -1;
	}

	public int indexOf(int v) {
		for (int i = 0; i < size; i++) {
			if (array[i] == v)
				return i;
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		return (o instanceof Integer) ? lastIndexOf((int) (Integer) o) : -1;
	}

	public int lastIndexOf(int v) {
		for (int i = size - 1; i >= 0; i--)
			if (array[i] == v)
				return i;
		return -1;
	}

	@Override
	public ListIterator<Integer> listIterator() {
		return new IntArrayIterator();
	}

	@Override
	public ListIterator<Integer> listIterator(int index) {
		throw new ToDoException();
	}

	@Override
	public List<Integer> subList(int fromIndex, int toIndex) {
		throw new ToDoException();
	}

	private class IntArrayIterator implements IntIterator, ListIterator<Integer> {
		int loc = 0;

		@Override
		public int nextInt() {
			if (loc >= size)
				throw new NoSuchElementException();
			return array[loc++];
		}

		@Override
		public boolean hasNext() {
			return loc < size;
		}

		@Override
		public Integer next() {
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
		public Integer previous() {
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
		public void set(Integer e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(Integer e) {
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
	public int removeAt(int index) {
		if (index < 0 || index >= size)
			throw new IndexOutOfBoundsException(index + " for array of size " + size);
		int oldValue = array[index];
		if (--size > index)
			System.arraycopy(array, index + 1, array, index, size - index);
		return oldValue;
	}
	public void incrementBy(int index, int j) {
		if (index < 0 || index > size)
			throw new IndexOutOfBoundsException(index + " for array of size " + size);
		this.array[index] += j;
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

	//Don't do dangerous stuff
	public int[] getInner() {
		return this.array;
	}
	public void removeAllAt(int[] toRemove) {
		if (toRemove.length == 0)
			return;
		Arrays.sort(toRemove);
		int n = 0;
		int remove = toRemove[n++];
		int wPos = remove;
		for (int rPos = remove; rPos < size; rPos++) {
			if (rPos != remove)
				this.array[wPos++] = this.array[rPos];
			else
				while (remove == rPos) {
					if (n == toRemove.length) {
						rPos++;
						System.arraycopy(this.array, rPos, this.array, wPos, this.size - rPos);
						this.size -= n;
						return;
					}
					remove = toRemove[n++];
				}
		}
		this.size -= n;
	}

}
