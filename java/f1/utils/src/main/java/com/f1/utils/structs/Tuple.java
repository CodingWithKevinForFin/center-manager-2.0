package com.f1.utils.structs;

public interface Tuple extends Cloneable, Iterable<Object> {

	public Object getAt(int index);

	public int getSize();

	public void setAt(int index, Object obj);

	public Tuple clone();

}

