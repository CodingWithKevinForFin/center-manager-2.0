/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs;

import java.util.Map;

import com.f1.base.Lockable;
import com.f1.base.LockedException;
import com.f1.base.MappingEntry;
import com.f1.base.ToStringable;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class Tuple2<A, B> implements Comparable<Tuple2<A, B>>, Tuple, Map.Entry<A, B>, MappingEntry<A, B>, Lockable, ToStringable {

	private boolean locked = false;
	private A a;
	private B b;

	public Tuple2() {

	}

	public Tuple2(Map.Entry<A, B> entry) {
		this.a = entry.getKey();
		this.b = entry.getValue();
	}

	public Tuple2(A a, B b) {
		this.a = a;
		this.b = b;
	}

	public A getA() {
		return a;
	}

	public void setAB(A a, B b) {
		setA(a);
		setB(b);
	}

	public B getB() {
		return b;
	}

	public void setB(B b) {
		LockedException.assertNotLocked(this);
		this.b = b;
	}
	public void setA(A a) {
		LockedException.assertNotLocked(this);
		this.a = a;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((b == null) ? 0 : b.hashCode());
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
		Tuple2 other = (Tuple2) obj;
		if (a == null) {
			if (other.a != null)
				return false;
		} else if (!a.equals(other.a))
			return false;
		if (b == null) {
			if (other.b != null)
				return false;
		} else if (!b.equals(other.b))
			return false;
		return true;
	}

	@Override
	public int compareTo(Tuple2 o) {
		int r = OH.compare((Comparable) a, (Comparable) o.a);
		if (r != 0)
			return r;
		return OH.compare((Comparable) b, (Comparable) o.b);
	}

	@Override
	public String toString() {
		return "[" + a + "," + b + "]";
	}
	@Override
	public StringBuilder toString(StringBuilder sb) {
		sb.append('[');
		SH.s(a, sb);
		sb.append(',');
		SH.s(b, sb);
		return sb.append(']');
	}

	@Override
	public Tuple2<A, B> clone() {
		try {
			return (Tuple2<A, B>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw OH.toRuntime(e);
		}
	}

	public void clear() {
		a = null;
		b = null;
	}

	@Override
	public A getKey() {
		return getA();
	}

	@Override
	public B getValue() {
		return getB();
	}

	@Override
	public B setValue(B value) {
		B r = getB();
		setB(value);
		return r;
	}

	@Override
	public Object getAt(int index) {
		switch (index) {
			case 0:
				return getA();
			case 1:
				return getB();
			default:
				throw new IndexOutOfBoundsException("not between 0 and 1: " + index);
		}
	}

	@Override
	public int getSize() {
		return 2;
	}

	@Override
	public void setAt(int index, Object value) {
		switch (index) {
			case 0:
				setA((A) value);
				break;
			case 1:
				setB((B) value);
				break;
			default:
				throw new IndexOutOfBoundsException("not between 0 and 1: " + index);
		}

	}

	@Override
	public TupleIterator iterator() {
		return new TupleIterator(this);
	}

	@Override
	public void lock() {
		locked = true;
	}

	@Override
	public boolean isLocked() {
		return locked;
	}

	public static <A> A getA(Tuple2<A, ?> tuple2) {
		return tuple2 == null ? null : tuple2.getA();
	}
	public static <B> B getB(Tuple2<?, B> tuple2) {
		return tuple2 == null ? null : tuple2.getB();
	}

}
