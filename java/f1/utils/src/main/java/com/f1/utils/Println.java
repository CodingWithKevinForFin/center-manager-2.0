package com.f1.utils;

import java.io.Closeable;

public interface Println extends Appendable, Closeable {

	@Override
	Println append(CharSequence csq);
	@Override
	Println append(CharSequence csq, int start, int end);
	@Override
	Println append(char c);

	void println(String sb);
	void print(String sb);
	Println println(CharSequence sb);
	Println print(CharSequence sb);
	Println println(Object sb);
	Println print(Object sb);
	Println println();

	void flush();
}
