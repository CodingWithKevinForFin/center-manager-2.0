package com.f1.stringmaker;

public interface StringMakerFormatterFactory {

	public StringMakerFormatter getFormatter(String type, String args);
}
