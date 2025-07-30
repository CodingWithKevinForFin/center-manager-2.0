/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

public interface Buffer {

	public int getCapacity();

	public void setCapacity(int size);

	public int getCount();

	public void flush();

	public void reset();

}
