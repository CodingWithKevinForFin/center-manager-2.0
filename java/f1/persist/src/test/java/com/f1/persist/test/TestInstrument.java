package com.f1.persist.test;

import com.f1.base.Message;
import com.f1.base.ValuedListenable;

public interface TestInstrument extends Message, ValuedListenable {

	public String getName();

	public void setName(String name);

	public String getExchange();

	public void setExchange(String exchange);
}
