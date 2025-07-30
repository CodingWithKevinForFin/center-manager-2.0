package com.f1.utils;

import java.util.Arrays;
import java.util.logging.Logger;

import com.f1.base.ToStringable;

/**
 * Stores the strings in a continuous char array, meaning very compact storage and far fewer objects. Please note the <i>get(int i)</i> method needs to create and a new String each
 * time (assuming the associated string value is not null or blank). A defragmentation strategy is used for data that becomes sparse due to updates and deletes.
 * <P>
 * Two limits: <BR>
 * (a) Max single string size is 62766 chars <BR>
 * (b) All strings are stored in a single char array, limiting the total amount to about 2^15 chars, which is about 4GB
 * 
 * @author rcooke
 * 
 */
public class StringArrayList_Chars implements ToStringable {

	private static final Logger log = LH.get();
	private static int NULL = -1;
	private static int EMPTY = -2;
	public static final int MAX_LENGTH = Short.MAX_VALUE - 1;
	private int[] pointers = new int[32];//-1 indicates null, otherwise, address of header within data field
	private char[] data = new char[1024];//header is single char and indicates length (not including header), neg value indicates length of unused bytes (including header)
	private int nextPointer = 0;
	private int size;
	private int unusedChars = 0;
	private final StringBuilder tmp = new StringBuilder();

	public StringArrayList_Chars() {
	}
	public void add(CharSequence cs) {
		if (cs == null) {
			ensureSpaceForNewPointer();
			pointers[size++] = NULL;
			return;
		}
		int len = cs.length();
		if (len == 0) {
			ensureSpaceForNewPointer();
			pointers[size++] = EMPTY;
			return;
		}
		int index = size;
		verifyLength(len);
		ensureCharsCapacity(len + 1);
		ensureSpaceForNewPointer();
		nextPointer = fill(cs, pointers[index] = nextPointer);
		size++;
	}
	public void add(int index, CharSequence cs) {
		if (index == size) {
			add(cs);
			return;
		}
		validateIndex(index);
		if (cs == null) {
			insertPointerAt(index, NULL);
		} else {
			int len = cs.length();
			if (len == 0) {
				insertPointerAt(index, EMPTY);
			} else {
				verifyLength(len);
				ensureCharsCapacity(len + 1);
				insertPointerAt(index, nextPointer);
				nextPointer = fill(cs, nextPointer);
			}
		}
		size++;
	}
	private void insertPointerAt(int index, int value) {
		ensureSpaceForNewPointer();
		System.arraycopy(pointers, index, pointers, index + 1, size - index);
		pointers[index] = value;
	}
	private int fill(CharSequence cs, int pos) {
		final int len = cs.length();
		data[pos++] = (char) (len + 1);
		for (int i = 0; i < len; i++)
			data[pos++] = cs.charAt(i);
		return pos;
	}

	public void remove(int index) {
		validateIndex(index);
		size--;
		if (size == 0) {
			this.unusedChars = 0;
			this.nextPointer = 0;
			return;
		} else {
			int pos = pointers[index];
			if (index != size)
				System.arraycopy(pointers, index + 1, pointers, index, size - index);
			if (pos >= 0) {//isn't null or empty
				int len = data[pos];
				data[pos] = (char) -len;
				if (pos + len == nextPointer)
					nextPointer = pos;//this was the last element, so just move nextPointer to this id
				else
					this.unusedChars += len;
			}
		}
	}
	public void set(int index, CharSequence cs) {
		validateIndex(index);
		int pos = pointers[index];
		if (cs == null) {
			if (pos >= 0) {//wasn't null or empty, now it is null
				int oldLen = data[pos];
				this.unusedChars += oldLen;
				data[pos] = (char) -oldLen;
			}
			pointers[index] = NULL;
			return;
		}
		int newLen = cs.length();
		if (newLen == 0) {
			if (pos >= 0) {//wasn't null or empty, now it is empty
				int oldLen = data[pos];
				this.unusedChars += oldLen;
				data[pos] = (char) -oldLen;
			}
			pointers[index] = EMPTY;
			return;
		}
		int newTotLen = newLen + 1;
		verifyLength(newLen);
		if (pos < 0) {//was null or empty, now it isn't
			ensureCharsCapacity(newTotLen);
			nextPointer = fill(cs, pointers[index] = nextPointer);
			return;
		}
		int oldTotLen = toSignedInt(data[pos]);
		int moreNeeded = newTotLen - oldTotLen;
		if (moreNeeded == 0) {//same length, just update data
			pos++;
			for (int i = 0; i < newLen; i++)
				data[pos++] = cs.charAt(i);
			return;
		} else if (moreNeeded < 0) {//update data and record that remaining is unused
			pos = fill(cs, pos);
			data[pos++] = (char) moreNeeded;
			this.unusedChars -= moreNeeded;
			return;
		}

		//let's see how many empty blocks after this current block we can gobble up
		for (int cursor = pos + oldTotLen;;) {
			if (cursor == nextPointer) {//we walked to last element
				this.unusedChars -= nextPointer - (pos + oldTotLen);
				nextPointer = pos;
				pointers[index] = -1;
				ensureCharsCapacity(newTotLen);
				nextPointer = fill(cs, pointers[index] = nextPointer);
				return;
			}
			final int t = toSignedInt(data[cursor]);
			if (t >= 0) {//this block has data, so we don't have enough space via gobbling.. Add new block to end
				pointers[index] = -1;
				data[pos] = (char) -oldTotLen;
				this.unusedChars += oldTotLen;
				ensureCharsCapacity(newTotLen);
				nextPointer = fill(cs, pointers[index] = nextPointer);
				return;
			}
			cursor -= t;
			if (cursor - pos >= newTotLen) {
				pos = fill(cs, pos);
				this.unusedChars -= moreNeeded;
				if (cursor - pos > 0) {
					data[pos] = (char) -(cursor - pos);
				}
				return;
			}

		}
	}
	public boolean isEqual(int index, CharSequence value) {
		validateIndex(index);
		if (value == null)
			return pointers[index] == NULL;
		int pos = pointers[index];
		if (value == null)
			return pos == NULL;
		if (pos == EMPTY)
			return value.length() == 0;
		int len = data[pos++] - 1;
		if (len != value.length())
			return false;
		if (value.charAt(0) != data[pos])
			return false;
		while (--len > 0)
			if (value.charAt(len) != data[pos + len])
				return false;
		return true;
	}
	public boolean isNull(int index) {
		validateIndex(index);
		return pointers[index] == NULL;
	}
	public StringBuilder get(int index, StringBuilder sink) {
		validateIndex(index);
		int pos = pointers[index];
		if (pos == -1)
			return null;
		if (pos == -2)
			return sink;
		int len = data[pos++] - 1;
		sink.ensureCapacity(len);
		int end = len + pos;
		while (pos < end)
			sink.append(data[pos++]);
		return sink;
	}
	public String get(int index) {
		validateIndex(index);
		int pos = pointers[index];
		if (pos < 0)
			return pos == NULL ? null : "";
		int len = data[pos++] - 1;
		return new String(data, pos, len);
	}
	private void defrag(int newArraySize) {
		if (unusedChars == 0)
			return;
		long start = System.currentTimeMillis();
		char[] tmp = new char[newArraySize];
		int tmpPointer = 0;
		for (int i = 0; i < size; i++) {
			int t = this.pointers[i];
			if (t >= 0) {//isn't null or empty
				int len = toSignedInt(this.data[t]);
				System.arraycopy(this.data, t, tmp, tmpPointer, len);
				pointers[i] = tmpPointer;
				tmpPointer += len;
			}
		}
		long dur = System.currentTimeMillis() - start;
		LH.info(log, "Defragged " + this.size + " elements from " + this.nextPointer + "/" + this.data.length + " ==> " + tmpPointer + "/" + tmp.length + " bytes in " + dur
				+ " millis");
		this.data = tmp;
		this.nextPointer = tmpPointer;
		this.unusedChars = 0;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}
	private void validateIndex(int index) {
		if (index >= size)
			throw new IndexOutOfBoundsException(index + " >= " + size);
	}
	private static int toSignedInt(char c) {
		if (c > Short.MAX_VALUE)
			return c - 65536;
		return c;
	}
	private boolean ensureCharsCapacity(int len) {
		int requiredSize = this.nextPointer + len;
		if (requiredSize <= data.length)
			return false;
		else {
			if (this.unusedChars * 5L > this.nextPointer) {//at least 20% is unused, so lets defrag
				final int newSize;
				if (this.unusedChars * 3 > this.nextPointer)
					newSize = Math.max(data.length, requiredSize - this.unusedChars);//at least 33% is unused, so no need to grow array
				else
					newSize = MH.getArrayGrowth(data.length, requiredSize);
				defrag(newSize);
			} else {
				int newSize = MH.getArrayGrowth(data.length, requiredSize);
				data = Arrays.copyOf(data, newSize);
			}
		}
		return true;
	}
	private void verifyLength(int len) {
		if (len > MAX_LENGTH)
			throw new IllegalArgumentException("max size is " + MAX_LENGTH + " chars: " + len);
	}
	private void ensureSpaceForNewPointer() {
		if (pointers.length <= size)
			pointers = Arrays.copyOf(pointers, MH.getArrayGrowth(pointers.length, size + 1));
	}

	public void verify() {
		int cursor = 0;
		int sz = 0, uu = 0;
		for (;;) {
			if (cursor == this.nextPointer)
				break;
			int len = toSignedInt(this.data[cursor]);
			if (len < 0) {
				cursor -= len;
				uu -= len;
			} else if (len > 0) {
				cursor += len;
				sz++;
			} else
				throw new RuntimeException("bad header: " + len);
		}
		for (int i = 0; i < this.size; i++) {
			int t = this.pointers[i];
			if (t == -1 || t == -2)
				sz++;
			else
				OH.assertGt(this.data[t], 0);
		}
		OH.assertEq(sz, this.size);
		OH.assertEq(uu, this.unusedChars);
	}
	public int size() {
		return this.size;
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.ensureCapacity(this.nextPointer - this.unusedChars + size + 2);
		sink.append('[');
		for (int i = 0; i < this.size; i++) {
			if (i > 0)
				sink.append(", ");
			if (get(i, sink) == null)
				sink.append("null");
		}
		sink.append(']');
		return sink;
	}
	public void clear() {
		this.nextPointer = 0;
		this.size = 0;
		this.unusedChars = 0;
	}

	public static void main(String a[]) {
		StringArrayList_Chars sal = new StringArrayList_Chars();
		for (int i = 0; i < 10 * 1000 * 1000; i++) {
			if (i % 1000000 == 0)
				System.out.println(i);
			sal.add(new String(GuidHelper.getGuid(62).substring(20)));
		}
		System.gc();
		OH.sleep(1000);
		System.out.println(EH.getTotalMemory());
		System.out.println(EH.getFreeMemory());
		System.out.println(sal.size());
	}
	public void ensureCapacity(int size) {
		if (pointers.length <= size)
			pointers = Arrays.copyOf(pointers, MH.getArrayGrowth(pointers.length, size));
	}
	public long getDataSize() {
		return this.data.length;
	}
}
