package com.f1.stringmaker;


public interface StringMakerFormatter {

	void append(Object value, String format, String args, StringMakerSession basicStringMakerSession);

}
