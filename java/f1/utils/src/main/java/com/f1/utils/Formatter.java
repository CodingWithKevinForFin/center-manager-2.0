/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.io.IOException;
import java.io.Writer;

import com.f1.base.Getter;

public interface Formatter extends Cloneable, Getter<Object, String> {

	void format(Object value, StringBuilder sb);

	String format(Object value);

	void format(Object value, Writer out) throws IOException;

	public Formatter clone();

	public boolean canFormat(Object obj);

	public boolean canParse(String text);

	public Object parse(String text);

	public String getPattern();

}
