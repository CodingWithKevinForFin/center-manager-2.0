package com.f1.ami.web;

import java.util.Arrays;

import com.f1.utils.OH;

public class AmiWebObjectFieldsImpl implements AmiWebObjectFields {

	private int changesCount;
	private String[] changeFields = new String[10];
	private Object[] oldValues = new Object[10];

	@Override
	public int getChangesCount() {
		return changesCount;
	}

	@Override
	public String getChangeField(int n) {
		return changeFields[n];
	}

	@Override
	public Object getOldValue(int n) {
		return oldValues[n];
	}

	public void addChange(String keyString, Object old) {
		if (changesCount == changeFields.length) {
			changeFields = Arrays.copyOf(changeFields, changesCount * 2);
			oldValues = Arrays.copyOf(oldValues, changesCount * 2);
		}
		changeFields[changesCount] = keyString;
		oldValues[changesCount] = old;
		changesCount++;
	}
	public boolean addChangeIfNotExists(String keyString, Object old) {
		for (int i = 0; i < changesCount; i++)
			if (OH.eq(this.changeFields[i], keyString))
				return false;
		addChange(keyString, old);
		return true;
	}

	public void clear() {
		changesCount = 0;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append("Changes[");
		for (int i = 0; i < changesCount; i++) {
			if (i > 0)
				sink.append(", ");
			sink.append(changeFields[i]);
		}
		sink.append("]");
		return sink;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	public void addChanges(AmiWebObjectFields fields) {
		for (int i = 0, n = fields.getChangesCount(); i < n; i++)
			addChange(fields.getChangeField(i), fields.getOldValue(n));
	}

}
