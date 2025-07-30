package com.f1.utils;

import java.io.DataOutput;
import java.io.IOException;
import java.io.UTFDataFormatException;

public interface FastDataOutput extends DataOutput {

	void write(short b[], int off, int len) throws IOException;
	void write(int b[], int off, int len) throws IOException;
	void write(long b[], int off, int len) throws IOException;
	void write(float b[], int off, int len) throws IOException;
	void write(double b[], int off, int len) throws IOException;
	void write(char b[], int off, int len) throws IOException;
	void write(boolean b[], int off, int len) throws IOException;
	void writeUTFSupportLarge(CharSequence str) throws UTFDataFormatException, IOException;
	void writeInt3(int i) throws IOException;
	void writeLong5(long i) throws IOException;
	void writeLong6(long i) throws IOException;
	void writeLong7(long i) throws IOException;
	void flush() throws IOException;

}
