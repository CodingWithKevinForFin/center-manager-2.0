/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs;

import java.util.Map;

import com.f1.base.ToStringable;
import com.f1.utils.SH;

public class MapEntry<K, V> implements Map.Entry<K, V>, ToStringable {

	private V value;
	private K key;

	public MapEntry(K key, V value) {
		this.value = value;
		this.key = key;
	}
	public MapEntry(Map.Entry<K, V> e) {
		this.value = e.getValue();
		this.key = e.getKey();
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		V oldValue = this.value;
		this.value = value;
		return oldValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MapEntry other = (MapEntry) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();

	}

	@Override
	public StringBuilder toString(StringBuilder sb) {
		sb.append('[');
		SH.s(key, sb);
		sb.append(',');
		SH.s(value, sb);
		return sb.append(']');
	}

}
