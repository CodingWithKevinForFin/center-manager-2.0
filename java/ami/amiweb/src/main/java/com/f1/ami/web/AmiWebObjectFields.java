package com.f1.ami.web;

import com.f1.base.ToStringable;

public interface AmiWebObjectFields extends ToStringable {

	public int getChangesCount();
	public String getChangeField(int n);
	public Object getOldValue(int n);

}
