package com.f1.utils.converter.json2;

import com.f1.base.Lockable;

public interface JsonConverter<T> extends Lockable {

	boolean isCompatible(Class<?> type);

	public Class<T> getType();

	void objectToString(T o, ToJsonConverterSession session);

	Object stringToObject(FromJsonConverterSession session);

	boolean isLeaf();//returns true if this type does not have children

}
