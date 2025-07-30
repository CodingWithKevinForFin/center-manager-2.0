/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

import java.io.DataOutput;

/**
 * A replacement for StringBuilder, because for somereason they make {@link StringBuilder} final.
 * 
 * @see StringBuilder
 */
public interface StringBuildable extends CharSequence, Appendable, DataOutput {

	public StringBuildable append(Object str);

	public StringBuildable append(String str);

	public StringBuildable append(StringBuffer sb);

	public StringBuildable append(CharSequence s);

	public StringBuildable append(CharSequence s, int start, int end);

	public StringBuildable append(char str[]);

	public StringBuildable append(char str[], int offset, int len);

	public StringBuildable append(boolean b);

	public StringBuildable append(char c);

	public StringBuildable append(int i);

	public StringBuildable append(long lng);

	public StringBuildable append(float f);

	public StringBuildable append(double d);

	public StringBuildable appendCodePoint(int codePoint);

	public StringBuildable delete(int start, int end);

	public StringBuildable deleteCharAt(int index);

	public StringBuildable replace(int start, int end, String str);

	public StringBuildable insert(int index, char str[], int offset, int len);

	public StringBuildable insert(int offset, Object obj);

	public StringBuildable insert(int offset, String str);

	public StringBuildable insert(int offset, char str[]);

	public StringBuildable insert(int dstOffset, CharSequence s);

	public StringBuildable insert(int dstOffset, CharSequence s, int start, int end);

	public StringBuildable insert(int offset, boolean b);

	public StringBuildable insert(int offset, char c);

	public StringBuildable insert(int offset, int i);

	public StringBuildable insert(int offset, long l);

	public StringBuildable insert(int offset, float f);

	public StringBuildable insert(int offset, double d);

	public int indexOf(String str);

	public int indexOf(String str, int fromIndex);

	public int lastIndexOf(String str);

	public int lastIndexOf(String str, int fromIndex);

	public StringBuildable reverse();

	public String toString();

	public void appendNewLine();

	public StringBuilder getInner();

}
