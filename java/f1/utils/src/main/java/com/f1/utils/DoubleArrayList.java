package com.f1.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import com.f1.base.DoubleIterable;
import com.f1.base.DoubleIterator;
import com.f1.base.ToStringable;

public class DoubleArrayList implements List<Double>, ToStringable, DoubleIterable {

	private double[] array;
	private int size;

	public DoubleArrayList() {
		array = OH.EMPTY_DOUBLE_ARRAY;
	}
	public DoubleArrayList(int initialCapacity) throws IllegalArgumentException {
		if (initialCapacity <= 0)
			throw new IllegalArgumentException("initial capacity must be greater than 0");
		this.array = new double[initialCapacity];
	}

	public DoubleArrayList(double[] array) {
		this.array = array.clone();
		this.size = array.length;
	}
	public DoubleArrayList(double[] array, int start, int end) throws IllegalArgumentException {
		if (end < start)
			throw new IllegalArgumentException("End must be greater than or equal to start");
		this.size = end - start;
		this.array = new double[Math.max(size, 1)];
		System.arraycopy(array, start, this.array, 0, size);
	}

	public DoubleArrayList(DoubleArrayList d) {
		this(d.array, 0, d.size);
	}
	@Override
	public int size() {
		return size;
	}

	public DoubleArrayList setSize(int size) {
		if (size < 0)
			throw new IndexOutOfBoundsException("Should not set size as negative");
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
		if (o instanceof Double) {
			double l = (Double) o;
			for (int i = 0; i < size; i++)
				if (array[i] == l)
					return true;
		}
		return false;
	}

	@Override
	public DoubleIterator iterator() {
		return new DoubleArrayIterator();
	}

	@Override
	public Object[] toArray() {
		throw new ToDoException();
	}

	public double[] toDoubleArray() {
		if (size <= 0)
			return OH.EMPTY_DOUBLE_ARRAY;
		return Arrays.copyOf(array, size);
	}
	public int[] toIntArray() {
		if (size <= 0)
			return OH.EMPTY_INT_ARRAY;
		final int[] r = new int[size];
		for (int i = 0; i < size; i++)
			r[i] = (int) array[i];
		return r;
	}

	public short[] toShortArray() {
		if (size <= 0)
			return OH.EMPTY_SHORT_ARRAY;
		final short[] r = new short[size];
		for (int i = 0; i < size; i++)
			r[i] = (short) array[i];
		return r;
	}

	public byte[] toByteArray() {
		if (size <= 0)
			return OH.EMPTY_BYTE_ARRAY;
		final byte[] r = new byte[size];
		for (int i = 0; i < size; i++)
			r[i] = (byte) array[i];
		return r;
	}
	public boolean[] toBooleanArray() {
		if (size <= 0)
			return OH.EMPTY_BOOLEAN_ARRAY;
		final boolean[] r = new boolean[size];
		for (int i = 0; i < size; i++)
			r[i] = array[i] != 0;
		return r;
	}
	public char[] toCharArray() {
		if (size <= 0)
			return OH.EMPTY_CHAR_ARRAY;
		final char[] r = new char[size];
		for (int i = 0; i < size; i++)
			r[i] = (char) array[i];
		return r;
	}
	public float[] toFloatArray() {
		if (size <= 0)
			return OH.EMPTY_FLOAT_ARRAY;
		final float[] r = new float[size];
		for (int i = 0; i < size; i++)
			r[i] = (float) array[i];
		return r;
	}
	public long[] toLongArray() {
		if (size <= 0)
			return OH.EMPTY_LONG_ARRAY;
		final long[] r = new long[size];
		for (int i = 0; i < size; i++)
			r[i] = (long) array[i];
		return r;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new ToDoException();
	}

	@Override
	public boolean add(Double e) {
		return add((double) e);
	}

	public boolean add(double e) {
		ensureCapacity(size + 1);
		array[size++] = e;
		return true;
	}
	public boolean addAll(double[] values) {
		if (values.length == 0)
			return false;
		ensureCapacity(size + values.length);
		System.arraycopy(values, 0, array, size, values.length);
		size += values.length;
		return true;
	}

	@Override
	public boolean remove(Object o) {
		if (o instanceof Double)
			return removeDouble((double) (Double) o);
		return false;
	}

	public boolean removeDouble(double o) {
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
	public boolean addAll(Collection<? extends Double> c) {
		int numMoved = c.size();
		if (numMoved == 0)
			return false;
		ensureCapacity(size + numMoved);
		if (c instanceof DoubleArrayList) {
			DoubleArrayList lal = (DoubleArrayList) c;
			System.arraycopy(lal.array, 0, array, size, numMoved);
			size += numMoved;
			return true;
		}
		final Iterator<? extends Double> i = c.iterator();
		if (i instanceof DoubleIterator) {
			final DoubleIterator li = (DoubleIterator) i;
			while (li.hasNext())
				array[size++] = li.nextDouble();
		} else
			while (i.hasNext())
				array[size++] = i.next();
		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends Double> c) {
		if (index < 0 || index > size)
			throw new IndexOutOfBoundsException();
		int numMoved = c.size();
		if (numMoved == 0)
			return false;
		ensureCapacity(size + numMoved);
		System.arraycopy(array, index, array, index + numMoved, numMoved);
		if (c instanceof DoubleArrayList) {
			DoubleArrayList lal = (DoubleArrayList) c;
			System.arraycopy(lal.array, 0, array, index, numMoved);
			size += numMoved;
			return true;
		}
		final Iterator<? extends Double> i = c.iterator();
		if (i instanceof DoubleIterator) {
			final DoubleIterator li = (DoubleIterator) i;
			while (li.hasNext())
				array[index++] = li.nextDouble();
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
	public Double get(int index) {
		return getDouble(index);
	}
	public double getDouble(int index) {
		if (index < 0 || index > size)
			throw new IndexOutOfBoundsException(index + " for array of size " + size);
		return array[index];
	}

	@Override
	public Double set(int index, Double element) {
		Double r = get(index);
		set(index, (double) element);
		return r;
	}

	public void set(int index, double element) {
		array[index] = element;
	}

	@Override
	public void add(int index, Double element) {
		add(index, (double) element);
	}

	public void add(int index, double element) {
		if (index < 0 || index > size)
			throw new IndexOutOfBoundsException(index + " for array of size " + size);
		ensureCapacity(size + 1);
		System.arraycopy(array, index, array, index + 1, size - index);
		size++;
		array[index] = element;
	}

	@Override
	public Double remove(int index) {
		if (index < 0 || index >= size)
			throw new IndexOutOfBoundsException(index + " for array of size " + size);
		double oldValue = array[index];

		if (--size > index)
			System.arraycopy(array, index + 1, array, index, size - index);

		return oldValue;
	}

	@Override
	public int indexOf(Object o) {
		return (o instanceof Double) ? indexOf((double) (Double) o) : -1;
	}

	public int indexOf(double v) {
		for (int i = 0; i < size; i++) {
			if (array[i] == v)
				return i;
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		return (o instanceof Double) ? lastIndexOf((double) (Double) o) : -1;
	}

	public int lastIndexOf(double v) {
		for (int i = size - 1; i >= 0; i--)
			if (array[i] == v)
				return i;
		return -1;
	}

	@Override
	public ListIterator<Double> listIterator() {
		return new DoubleArrayIterator();
	}

	@Override
	public ListIterator<Double> listIterator(int index) {
		throw new ToDoException();
	}

	@Override
	public List<Double> subList(int fromIndex, int toIndex) {
		throw new ToDoException();
	}

	private class DoubleArrayIterator implements DoubleIterator, ListIterator<Double> {
		int loc = 0;

		@Override
		public double nextDouble() {
			if (loc >= size)
				throw new IndexOutOfBoundsException();
			return array[loc++];
		}

		@Override
		public boolean hasNext() {
			return loc < size;
		}

		@Override
		public Double next() {
			if (loc >= size)
				throw new IndexOutOfBoundsException();
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
		public Double previous() {
			if (loc <= 0)
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
		public void set(Double e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(Double e) {
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
		if (size <= 0)
			return "[]";
		return toString(new StringBuilder()).toString();
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		if (size <= 0)
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
