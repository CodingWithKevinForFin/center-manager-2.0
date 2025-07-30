package com.f1.utils;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CircularList<T> extends AbstractList<T> {

	public CircularList() {
		this(10);
	}

	public CircularList(int capacity) {
		head = 0;
		size = 0;
		data = new Object[capacity];
		arrayLength = data.length;
	}

	public CircularList(int capacity, T[] data, int start, int length) {
		this(capacity);
		System.arraycopy(data, start, this.data, 0, length);
		head = 0;
		size = 0;
	}
	public CircularList(int capacity, CircularList<T> data, int start, int length) {
		this(capacity);
		if (length > 0)
			data.toArray(this.data, 0, start, length);
		head = 0;
		tail = size = length;
	}
	@Override
	public void clear() {
		head = size = tail = 0;
		AH.fill(data, null);
	}

	@Override
	public T get(int index) {
		if (index < 0 || index >= size)
			throw new IndexOutOfBoundsException("For List size " + size + " index: " + index);
		return (T) data[modUpperIndex(head + index)];
	}

	public T set(int index, T element) {
		if (index < 0 || index >= size)
			throw new IndexOutOfBoundsException("For List size " + size + " index: " + index);
		int x = modUpperIndex(head + index);
		T r = (T) data[x];
		data[x] = element;
		return r;
	};

	@Override
	public boolean add(T element) {
		growIfNecessary();
		data[tail] = element;
		tail = modUpperIndex(tail + 1);
		size++;
		return true;
	}
	@Override
	public void add(int index, T element) {
		growIfNecessary();
		if (index == size) {
			data[tail] = element;
			tail = modUpperIndex(tail + 1);
		} else if (index == 0) {
			head = modLowerIndex(head - 1);
			data[head] = element;
		} else {
			if (index < 0 || index > size)
				throw new IndexOutOfBoundsException("For list size " + size + " index: " + index);
			final int i = modUpperIndex(index + head);
			if (closerToTail(i)) {
				shift(i, tail, 1);
				data[i] = element;
				tail = modUpperIndex(tail + 1);
			} else {
				shift(head, i, -1);
				data[modLowerIndex(i - 1)] = element;
				head = modLowerIndex(head - 1);
			}
		}
		size++;
	}

	private boolean closerToTail(int i) {
		if (head < tail)
			return tail - 1 - i < i - head;
		else if (i <= tail)
			return i + arrayLength - head > tail - i - 1;
		else
			return i - head > (tail + arrayLength) - i - 1;
	}

	private void growIfNecessary() {
		if (size < arrayLength)
			return;
		if (head == 0) {
			this.data = Arrays.copyOf(this.data, data.length * 2);
			tail = size;
			arrayLength = this.data.length;
			return;
		}
		Object biggerValues[] = new Object[data.length * 2];
		if (head < tail) {
			System.arraycopy(data, head, biggerValues, 0, tail - head);
		} else {
			System.arraycopy(data, head, biggerValues, 0, arrayLength - head);
			System.arraycopy(data, 0, biggerValues, arrayLength - head, tail);
		}
		data = biggerValues;
		arrayLength = biggerValues.length;
		head = 0;
		tail = size;
	}

	public void assertTail() {
		if (tail != modUpperIndex(head + size))
			throw new RuntimeException();
	}

	public T remove(int index) {
		if (index >= size || index < 0)
			throw new IndexOutOfBoundsException("for list of size " + size + ": " + index);
		Object removed = get(index);
		if (index == 0) {
			head = modUpperIndex(head + 1);
		} else if (index == size - 1) {
			tail = modLowerIndex(tail - 1);
		} else {
			final int i = modUpperIndex(head + index);
			if (closerToTail(i)) {
				shift(i + 1, tail, -1);
				tail = modLowerIndex(tail - 1);
				data[tail] = null;
			} else {
				shift(head, i, 1);
				data[head] = null;
				head = modUpperIndex(head + 1);
			}
		}
		size--;
		return (T) removed;
	}

	private void shift(int first, int last, int distance) {
		if (distance != 1 && distance != -1)
			throw new IllegalArgumentException();
		if (first == last)
			return;
		if (last == 0)
			last = arrayLength;
		if (first > last && last != 0) {
			if (distance == 1) {
				System.arraycopy(data, 0, data, 1, last);
				data[0] = data[arrayLength - 1];
				System.arraycopy(data, first, data, first + 1, arrayLength - first - 1);
				data[first] = null;
			} else if (distance == -1) {
				System.arraycopy(data, first, data, first - 1, arrayLength - first);
				data[arrayLength - 1] = data[0];
				System.arraycopy(data, 1, data, 0, last - 1);
				data[last - 1] = null;
			}
		} else if (distance == -1 && first == 0) {
			data[arrayLength - 1] = data[0];
			System.arraycopy(data, 1, data, 0, last - 1);
			data[last - 1] = null;
		} else if (distance == 1 && last == arrayLength) {
			data[0] = data[arrayLength - 1];
			System.arraycopy(data, first, data, first + 1, last - first - 1);
			data[first] = null;
		} else if (distance == 1) {
			System.arraycopy(data, first, data, first + 1, last - first);
			data[first] = null;
		} else if (distance == -1) {
			System.arraycopy(data, first, data, first - 1, last - first);
			data[last - 1] = null;
		}
	}

	@Override
	public int size() {
		return size;
	}

	private int modLowerIndex(int value) {
		return value < 0 ? value + arrayLength : value;
	}

	private int modUpperIndex(int value) {
		return value >= arrayLength ? value - arrayLength : value;
	}

	@Override
	public boolean contains(Object o) {
		if (head < tail)
			return AH.indexOf(o, this.data, head, tail) != -1;
		else
			return AH.indexOf(o, this.data, 0, tail) != -1 || AH.indexOf(o, this.data, head, arrayLength) != -1;
	}
	@Override
	public int indexOf(Object o) {
		if (head < tail) {
			int r = AH.indexOf(o, this.data, head, tail);
			if (r != -1)
				r -= head;
			return r;
		} else {
			int r = AH.indexOf(o, this.data, head, arrayLength);
			if (r != -1)
				return r - head;
			r = AH.indexOf(o, this.data, 0, tail);
			if (r != -1)
				return r - head + arrayLength;
			return r;
		}
	}

	private int head, size, arrayLength;
	private int tail;
	private Object data[];

	@Override
	public Iterator<T> iterator() {
		return new CircularListIterator();
	}

	@Override
	public Object[] toArray() {
		Object[] r = new Object[size];
		toArray(r, 0, 0, size);
		return r;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		if (a.length < size)
			a = AH.newInstance(a, size);
		toArray(a, 0, 0, size);
		return a;
	}

	public void toArray(Object[] sink, int sinkPos, int start, int length) {
		if (start + length > size)
			throw new IndexOutOfBoundsException("for list of size " + size + ": " + start + " + " + length);
		if (head < tail) {
			System.arraycopy(data, head + start, sink, sinkPos, length);
		} else {
			int firstPortionSize = arrayLength - head;
			if (firstPortionSize <= start) {
				System.arraycopy(data, start - firstPortionSize, sink, sinkPos, length); //only copy tail
			} else if (firstPortionSize >= start + length) {
				System.arraycopy(data, head + start, sink, sinkPos, length); //only copy head
			} else {
				int firstCopyAmount = firstPortionSize - start;
				System.arraycopy(data, head + start, sink, sinkPos, firstCopyAmount); //copy head
				System.arraycopy(data, 0, sink, sinkPos + firstCopyAmount, length - firstCopyAmount); //copy tail

			}
		}
	}

	@Override
	public boolean remove(Object o) {
		int i = indexOf(o);
		if (i == -1)
			return false;
		remove(i);
		return true;
	}

	@Override
	public int lastIndexOf(Object o) {
		if (head < tail) {
			int r = AH.lastIndexOf(o, this.data, head, tail);
			if (r == -1)
				r += head;
			return r;
		} else {
			int r = AH.lastIndexOf(o, this.data, 0, tail);
			if (r != -1)
				return r - head + arrayLength;
			r = AH.lastIndexOf(o, this.data, head, arrayLength);
			if (r != -1)
				return r - head;
			return r;
		}
	}

	private class CircularListIterator implements Iterator<T> {

		private int pos = 0;
		@Override
		public boolean hasNext() {
			return pos < size;
		}

		@Override
		public T next() {
			if (pos == size)
				throw new NoSuchElementException();
			return (T) data[modUpperIndex(head + pos++)];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

}
