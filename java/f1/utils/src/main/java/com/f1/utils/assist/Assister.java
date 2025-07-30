/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.assist;

import java.util.IdentityHashMap;

import com.f1.utils.IndentedStringBuildable;

public interface Assister<T> {

	public Object getNestedValue(T o, String path, boolean throwOnError);

	void toString(T o, StringBuilder sb, IdentityHashMap<Object, Object> visited);

	void toLegibleString(T o, IndentedStringBuildable sb, IdentityHashMap<Object, Object> visited, int maxLength);

	void toJson(T o, StringBuilder sb);

	T clone(T o, IdentityHashMap<Object, Object> visited);

	Object toMapList(T o, boolean storeNulls, String keyForClassNameOrNull);

}
