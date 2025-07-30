package com.f1.utils;

import java.io.DataInput;
import java.io.IOException;

public interface FastDataInput extends DataInput {

	short[] readFully(short b[], int off, int len) throws IOException;
	int[] readFully(int b[], int off, int len) throws IOException;
	long[] readFully(long b[], int off, int len) throws IOException;
	float[] readFully(float b[], int off, int len) throws IOException;
	double[] readFully(double b[], int off, int len) throws IOException;
	char[] readFully(char b[], int off, int len) throws IOException;
	boolean[] readFully(boolean b[], int off, int len) throws IOException;
	int readInt3() throws IOException;
	long readLong5() throws IOException;
	long readLong6() throws IOException;
	long readLong7() throws IOException;
	int available() throws IOException;
	void skipUTF() throws IOException;
	void skipBytesFully(int bytes) throws IOException;
}
