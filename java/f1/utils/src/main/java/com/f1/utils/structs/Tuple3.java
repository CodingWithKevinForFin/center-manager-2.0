/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs;

import com.f1.base.Lockable;
import com.f1.base.LockedException;
import com.f1.base.ToStringable;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class Tuple3<A, B, C> implements Tuple, Comparable<Tuple3<A, B, C>>, Lockable, ToStringable {

	private boolean locked = false;
	private A a;
	private B b;
	private C c;

	public Tuple3(A a, B b, C c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	public Tuple3() {
	}

	public A getA() {
		return a;
	}

	public void setA(A a) {
		LockedException.assertNotLocked(this);
		this.a = a;
	}

	public B getB() {
		return b;
	}

	public void setB(B b) {
		LockedException.assertNotLocked(this);
		this.b = b;
	}

	public C getC() {
		return c;
	}

	public void setC(C c) {
		LockedException.assertNotLocked(this);
		this.c = c;
	}

	public void setABC(A a, B b, C c) {
		LockedException.assertNotLocked(this);
		this.a = a;
		this.b = b;
		this.c = c;
	}
	public void setAB(A a, B b) {
		LockedException.assertNotLocked(this);
		this.a = a;
		this.b = b;
	}
	public void setBC(B b, C c) {
		LockedException.assertNotLocked(this);
		this.b = b;
		this.c = c;
	}
	public void setAC(A a, C c) {
		LockedException.assertNotLocked(this);
		this.a = a;
		this.c = c;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		result = prime * result + ((c == null) ? 0 : c.hashCode());
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
		Tuple3 other = (Tuple3) obj;
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
		if (c == null) {
			if (other.c != null)
				return false;
		} else if (!c.equals(other.c))
			return false;
		return true;
	}

	@Override
	public int compareTo(Tuple3 o) {
		int r = OH.compare((Comparable) a, (Comparable) o.a);
		if (r != 0)
			return r;
		r = OH.compare((Comparable) b, (Comparable) o.b);
		if (r != 0)
			return r;
		return OH.compare((Comparable) c, (Comparable) o.c);
	}

	@Override
	public String toString() {
		return "[" + a + "," + b + "," + c + "]";
	}

	@Override
	public Object getAt(int index) {
		switch (index) {
			case 0:
				return getA();
			case 1:
				return getB();
			case 2:
				return getC();
			default:
				throw new IndexOutOfBoundsException("not between 0 and 2: " + index);
		}
	}

	@Override
	public int getSize() {
		return 3;
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
			case 2:
				setC((C) value);
				break;
			default:
				throw new IndexOutOfBoundsException("not between 0 and 2: " + index);
		}

	}

	@Override
	public TupleIterator iterator() {
		return new TupleIterator(this);
	}

	@Override
	public Tuple3<A, B, C> clone() {
		try {
			return (Tuple3<A, B, C>) super.clone();
		} catch (CloneNotSupportedException e_) {
			throw OH.toRuntime(e_);
		}
	}

	@Override
	public void lock() {
		locked = true;
	}

	@Override
	public boolean isLocked() {
		return locked;
	}

	@Override
	public StringBuilder toString(StringBuilder sb) {
		sb.append('[');
		SH.s(a, sb);
		sb.append(',');
		SH.s(b, sb);
		sb.append(',');
		SH.s(c, sb);
		return sb.append(']');
	}

}
