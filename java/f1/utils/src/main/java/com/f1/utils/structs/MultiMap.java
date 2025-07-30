/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs;

import java.util.Collection;
import java.util.Map;

public interface MultiMap<KEY, VAL, COL extends Collection<VAL>> extends Map<KEY, COL> {

	public void putMulti(Map<KEY, VAL> map);

	public COL putMulti(KEY key, VAL val);

	public void putAllMulti(KEY key, Collection<VAL> val);
	public void putAllMulti(MultiMap<KEY, VAL, COL> val);

	public VAL getMulti(KEY key);

	public boolean removeMulti(KEY key, VAL val);

	public boolean removeMultiAndKeyIfEmpty(KEY key, VAL val);

	public Iterable<VAL> valuesMulti();

	MultiMap<KEY, VAL, COL> deepClone();

}
