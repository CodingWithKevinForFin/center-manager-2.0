package com.f1.sample;

import com.f1.base.Message;

public interface SampleResponseMessage extends Message {
	String getResponseText();

	void setResponseText(String text);

}

