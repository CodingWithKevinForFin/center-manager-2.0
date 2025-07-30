package com.f1.utils;

import com.f1.base.Caster;

public abstract class AbstractCaster<C> implements Caster<C> {

	final private Class<C> castToClass;
	final private String simpleName;

	final public Class<C> getCastToClass() {
		return this.castToClass;
	}

	public AbstractCaster(Class<C> castTo) {
		this.castToClass = castTo;
		this.simpleName = castTo.getSimpleName();
	}

	public C cast(Object o, boolean required, String description) {
		try {
			return cast(o);
		} catch (Exception e) {
			throw new RuntimeException(description + " is not a " + getCastToClass().getName(), e);
		}
	}

	/**
	 * not required, but throws on cast error
	 */
	public final C cast(Object o) {
		if (o == null)
			return null;
		if (this.castToClass == o.getClass() || this.castToClass.isInstance(o))
			return (C) o;
		try {
			return castInner(o, true);
		} catch (Exception e) {
			throw new DetailedException("auto-cast failed", e).set("value", o).set("cast from class", this.castToClass).set("cast to class", this.castToClass);
		}
	}

	/**
	 * not required, don't through
	 */
	public final C castNoThrow(Object o) {
		if (o == null)
			return null;
		if (this.castToClass == o.getClass() || this.castToClass.isInstance(o))
			return (C) o;
		try {
			return castInner(o, false);
		} catch (Exception e) {
			return null;
		}
	}
	/*
	 * throw if can't cast
	 */
	public final C cast(Object o, boolean required) {
		if (o == null) {
			if (!required)
				return null;
			throw new NullPointerException();
		}
		if (this.castToClass == o.getClass() || this.castToClass.isInstance(o))
			return (C) o;
		try {
			return castInner(o, true);
		} catch (Exception e) {
			throw new DetailedException("auto-cast failed", e).set("value", o).set("cast from class", o.getClass()).set("cast to class", this.castToClass);
		}
	}
	public final C castOr(Object o, C dflt) {
		C r = castNoThrow(o);
		return r == null ? dflt : r;
	}

	public final C cast(Object o, boolean required, boolean throwExceptionOnError) {
		if (o == null) {
			if (!required || !throwExceptionOnError)
				return null;
			throw new NullPointerException();
		}
		final Class<?> srcClass = o.getClass();
		final Class<C> dstClass = getCastToClass();
		if (dstClass == srcClass || dstClass.isAssignableFrom(srcClass))
			return (C) o;
		try {
			return castInner(o, throwExceptionOnError);
		} catch (Exception e) {
			if (throwExceptionOnError)
				throw new DetailedException("auto-cast failed", e).set("value", o).set("cast from class", srcClass).set("cast to class", dstClass);
			else
				return null;
		}
	}
	protected abstract C castInner(Object o, boolean throwExceptionOnError);

	public final String getName() {
		return getCastToClass().getName();
	}

	@Override
	public int hashCode() {
		return getCastToClass().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && obj.getClass() == this.getClass() && OH.eq(this.getCastToClass(), ((Caster<C>) obj).getCastToClass());
	}

	@Override
	public String getSimpleName() {
		return this.simpleName;
	}
}
