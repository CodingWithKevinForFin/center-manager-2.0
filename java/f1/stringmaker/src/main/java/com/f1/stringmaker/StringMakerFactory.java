package com.f1.stringmaker;

import com.f1.base.Factory;

public interface StringMakerFactory extends Factory<String, StringMaker> {

	public void setFormatterFactory(StringMakerFormatterFactory factory);
	public StringMakerFormatterFactory getFormatterFactory();

}
