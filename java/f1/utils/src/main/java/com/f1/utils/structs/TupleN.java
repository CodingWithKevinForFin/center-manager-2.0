/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs;

import java.util.Arrays;
import java.util.Iterator;
import com.f1.utils.OH;

public class TupleN implements Comparable<TupleN>, Tuple {

	final private Object values[];

	public TupleN(int size) {
		values = new Object[size];
	}

	public TupleN(Object[] values) {
		this.values = values;
	}

	public Object[] getValues() {
		return values;
	}

	@Override
	public int compareTo(TupleN o) {
		if (o == this)
			return 0;
		TupleN other = o;
		if (other.values == values)
			return 0;
		for (int i = 0; i < values.length; i++) {
			int r = OH.compare((Comparable) values[i], (Comparable) o.values[i]);
			if (r != 0)
				return r;
		}
		return 0;
	}

	@Override
	public String toString() {
		return Arrays.toString(values);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(values);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TupleN other = (TupleN) obj;
		if (!Arrays.equals(values, other.values))
			return false;
		return true;
	}

	@Override
	public Iterator<Object> iterator() {
		return new TupleIterator(this);
	}

	@Override
	public Object getAt(int index) {
		return values[index];
	}

	@Override
	public int getSize() {
		return values.length;
	}

	@Override
	public void setAt(int index, Object obj) {
		values[index] = obj;
	}

	@Override
	public TupleN clone() {
		try {
			return (TupleN) super.clone();
		} catch (CloneNotSupportedException e_) {
			throw OH.toRuntime(e_);
		}
	}
}
