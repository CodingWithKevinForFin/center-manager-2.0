package com.f1.utils;

import java.util.Iterator;
import com.f1.base.Pointer;
import com.f1.utils.structs.Tuple;
import com.f1.utils.structs.TupleIterator;

public class BasicPointer<T> implements Pointer<T>, Tuple {

	public BasicPointer(T value) {
		this.value = value;
	}

	public BasicPointer() {
	}

	private T value;

	@Override
	public T get() {
		return value;
	}

	@Override
	public T put(T value) {
		final T r = this.value;
		this.value = value;
		return r;
	}

	@Override
	public String toString() {
		return SH.toString(value);
	}

	@Override
	public Iterator<Object> iterator() {
		return new TupleIterator(this);
	}

	@Override
	public Object getAt(int index) {
		if (index != 0)
			throw new IndexOutOfBoundsException(SH.toString(index));
		return value;
	}

	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public void setAt(int index, Object obj) {
		if (index != 0)
			throw new IndexOutOfBoundsException(SH.toString(index));
		this.value = (T) obj;
	}

	@Override
	public BasicPointer<T> clone() {
		try {
			return (BasicPointer<T>) super.clone();
		} catch (CloneNotSupportedException e_) {
			throw OH.toRuntime(e_);
		}
	}

}

