package com.f1.qfix;

import com.f1.base.Message;

public interface FixEvent extends Message {

	public String getSessionName();
	public void setSessionName(String name);

	public quickfix.Message getMessage();
	public void setMessage(quickfix.Message msg);
}
