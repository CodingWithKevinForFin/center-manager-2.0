package com.f1.utils.ids;

public interface NamespaceIdGenerator<T> {
	public IdGenerator<T> getIdGenerator(String nameSpace);

	public T createNextId(String nameSpace);
}
