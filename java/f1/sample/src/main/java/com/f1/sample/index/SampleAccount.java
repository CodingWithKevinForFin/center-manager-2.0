package com.f1.sample.index;

import com.f1.base.Message;

public interface SampleAccount extends Message {

	public int getAccountId();

	public void setAccountId(int id);

	public String getFirstName();

	public void setFirstName(String firstName);

	public String getLastName();

	public void setLastName(String lastName);

}

