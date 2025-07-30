/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs;

import com.f1.base.Lockable;
import com.f1.base.LockedException;
import com.f1.base.ToStringable;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class Tuple4<A, B, C, D> implements Tuple, Comparable<Tuple4<A, B, C, D>>, Lockable, ToStringable {

	private boolean locked = false;
	private A a;
	private B b;
	private C c;
	private D d;

	public Tuple4(A a, B b, C c, D d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	public Tuple4() {
	}

	public A getA() {
		return a;
	}

	public B getB() {
		return b;
	}

	public C getC() {
		return c;
	}
	public D getD() {
		return d;
	}

	//singles
	public void setA(A a) {
		LockedException.assertNotLocked(this);
		this.a = a;
	}
	public void setB(B b) {
		LockedException.assertNotLocked(this);
		this.b = b;
	}
	public void setC(C c) {
		LockedException.assertNotLocked(this);
		this.c = c;
	}
	public void setD(D d) {
		LockedException.assertNotLocked(this);
		this.d = d;
	}

	//pairs
	public void setAB(A a, B b) {
		LockedException.assertNotLocked(this);
		this.a = a;
		this.b = b;
	}
	public void setAC(A a, C c) {
		LockedException.assertNotLocked(this);
		this.a = a;
		this.c = c;
	}
	public void setAD(A a, D d) {
		LockedException.assertNotLocked(this);
		this.a = a;
		this.d = d;
	}
	public void setBC(B b, C c) {
		LockedException.assertNotLocked(this);
		this.b = b;
		this.c = c;
	}
	public void setBD(B b, D d) {
		LockedException.assertNotLocked(this);
		this.b = b;
		this.d = d;
	}
	public void setCD(C c, D d) {
		LockedException.assertNotLocked(this);
		this.c = c;
		this.d = d;
	}

	//triples
	public void setABC(A a, B b, C c) {
		LockedException.assertNotLocked(this);
		this.a = a;
		this.b = b;
		this.c = c;
	}
	public void setABD(A a, B b, D d) {
		LockedException.assertNotLocked(this);
		this.a = a;
		this.b = b;
		this.d = d;
	}
	public void setACD(A a, C c, D d) {
		LockedException.assertNotLocked(this);
		this.a = a;
		this.c = c;
		this.d = d;
	}
	public void setBCD(B b, C c, D d) {
		LockedException.assertNotLocked(this);
		this.b = b;
		this.c = c;
		this.d = d;
	}

	//Quadruples
	public void setABCD(A a, B b, C c, D d) {
		LockedException.assertNotLocked(this);
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	@Override
	public int hashCode() {
		return OH.hashCode(a, b, c, d);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tuple4<?, ?, ?, ?> other = (Tuple4<?, ?, ?, ?>) obj;
		return OH.eq(a, other.a) && OH.eq(b, other.b) && OH.eq(c, other.c) && OH.eq(d, other.d);
	}

	@Override
	public int compareTo(Tuple4<A, B, C, D> o) {
		int r = OH.compare((Comparable<A>) a, (Comparable<A>) o.a);
		if (r != 0)
			return r;
		r = OH.compare((Comparable<B>) b, (Comparable<B>) o.b);
		if (r != 0)
			return r;
		r = OH.compare((Comparable<C>) c, (Comparable<C>) o.c);
		if (r != 0)
			return r;
		return OH.compare((Comparable<D>) d, (Comparable<C>) o.d);
	}

	@Override
	public String toString() {
		return "[" + a + "," + b + "," + c + "," + d + "]";
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
			case 3:
				return getD();
			default:
				throw new IndexOutOfBoundsException("not between 0 and 3: " + index);
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
			case 3:
				setD((D) value);
				break;
			default:
				throw new IndexOutOfBoundsException("not between 0 and 3: " + index);
		}

	}

	@Override
	public TupleIterator iterator() {
		return new TupleIterator(this);
	}

	@Override
	public Tuple4<A, B, C, D> clone() {
		try {
			return (Tuple4<A, B, C, D>) super.clone();
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
		sb.append(',');
		SH.s(d, sb);
		return sb.append(']');
	}
}
