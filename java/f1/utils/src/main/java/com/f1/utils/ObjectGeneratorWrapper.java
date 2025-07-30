/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import com.f1.base.ObjectGenerator;
import com.f1.base.ObjectGeneratorForClass;

public class ObjectGeneratorWrapper implements ObjectGenerator {

	private ObjectGenerator inner;

	public ObjectGeneratorWrapper(ObjectGenerator inner) {
		super();
		if (inner == null)
			throw new NullPointerException("inner");
		this.inner = inner;
	}

	@Override
	public Object[] nw(Class<?>... classs) {
		return inner.nw(classs);
	}

	@Override
	public <C> C nw(Class<C> classs) {
		return inner.nw(classs);
	}

	@Override
	public <C> C nw(Class<C> classs, Object... constructorParameters) {
		return inner.nw(classs, constructorParameters);
	}

	@Override
	public <C> C nwCast(Class<C> classs, Class[] argumentTypes, Object[] constructorParameters) {
		return inner.nwCast(classs, argumentTypes, constructorParameters);
	}

	public void setInner(ObjectGenerator inner) {
		this.inner = inner;
	}

	public ObjectGenerator getInner() {
		return inner;
	}

	@Override
	public <C> ObjectGeneratorForClass<C> getGeneratorForClass(Class<C> clazz) {
		return inner.getGeneratorForClass(clazz);
	}

	@Override
	public Object get(Class<?> key) {
		return inner.get(key);
	}

}
