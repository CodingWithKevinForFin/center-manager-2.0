package com.f1.sample;

import com.f1.base.Message;
import com.f1.base.DateNanos;
import com.f1.base.ValuedListenable;

public interface SampleAccount extends ValuedListenable, Message {

	public String getId();
	public void setId(String id);

	public String getName();
	public void setName(String name);

	public DateNanos getCreatedOn();
	public void setCreatedOn(DateNanos createdOn);

	public DateNanos getModifiedOn();
	public void setModifiedOn(DateNanos createdOn);

	public int getAccessCount();
	public void setAccessCount(int accesses);

}
