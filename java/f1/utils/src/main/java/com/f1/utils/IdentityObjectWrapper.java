/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

/**
 * This class can be used to create a 'key' for an object who's
 * {@link #hashCode()} and {@link #equals(Object)} methods adhear to the
 * identity of said object. The intent is that two {@link IdentityObjectWrapper}
 * instances will resolve to being equal(same hashcode and equals(...) returns
 * true) iff they both wrap the SAME object. <B>Note:two
 * {@link IdentityObjectWrapper}s wrapping different objects which are equal as
 * defined by {@link Object#equals(Object)} will still result in unequal
 * wrappers.
 * <P>
 * Note: Wrapping null is permissible.
 * <P>
 * Uses: This can be used to either wrap objects whose equals call is expensive
 * or unimplemented <BR>
 * 
 * @param <T>
 *            type of object being wrapped
 */
public final class IdentityObjectWrapper<T> implements Immutable {

	private final T inner;

	/**
	 * @param inner
	 *            may be null
	 */
	public IdentityObjectWrapper(T inner) {
		this.inner = inner;
	}

	/**
	 * @return the object that has been wrapped, may be null
	 */
	public T getInner() {
		return inner;
	}

	/**
	 * @return the identity hash code. see
	 *         {@link System#identityHashCode(Object)}
	 */
	@Override
	public int hashCode() {
		return System.identityHashCode(inner);
	}

	/**
	 * @return true iff o wraps same object as this (or if both wrap null)
	 */
	@Override
	public boolean equals(Object o) {
		return o == this || (o != null && o.getClass() == IdentityObjectWrapper.class && ((IdentityObjectWrapper) o).getInner() == inner);
	}

	/**
	 * @return {@link Object#toString()} of the wrapped object.
	 */
	@Override
	public String toString() {
		return SH.toString(inner);
	}
}

