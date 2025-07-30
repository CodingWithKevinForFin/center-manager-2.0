package com.f1.utils.structs;

import com.f1.utils.OH;
import com.f1.utils.SH;

public class SkipListDataEntry<T> extends SkipListEntry {

	private T data;

	public SkipListDataEntry(T data) {
		this.data = data;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	void toString(StringBuilder sb, int tab) {
		SH.repeat(' ', tab, sb);
		sb.append(", offset=").append(offset);
		sb.append("]=");
		sb.append(data);
		sb.append(SH.NEWLINE);
	}

	public String toString() {
		return OH.toString(data);
	}

}