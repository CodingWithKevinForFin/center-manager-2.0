package com.f1.utils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class FastSafeFile implements Closeable {

	//Where is the start of data, L is length of data and D is the data. Format is:   S......LDDDDDDD........
	//The strategy is that after data length (L) and data (D) are succesfully written to disk then the start (S) is atomically updated to point to position of L

	final private File file;
	private byte[] data;
	private String text;
	private int start;
	private int maxTextLength;
	private RandomAccessFile raf;

	public FastSafeFile(File location, int maxTextLength) throws IOException {
		OH.assertLe(maxTextLength, 255, "max text length");
		this.maxTextLength = maxTextLength;
		this.file = location;
		if (file.exists()) {
			byte[] data = IOH.readData(file);
			this.start = data[0] & 0xff;
			int length = data[start];
			this.data = AH.subarray(data, start + 1, length);
		} else {
			IOH.writeData(this.file, new byte[maxTextLength * 2 + 3]);
		}
		long paddingRequired = maxTextLength * 2 + 3 - this.file.length();
		this.raf = new RandomAccessFile(this.file, "rw");
		if (paddingRequired > 0) {
			raf.seek(raf.length());
			raf.write(new byte[(int) paddingRequired]);
		}
	}

	public File getFile() {
		return this.file;
	}

	public byte[] getData() {
		return data;
	}

	public String getText() {
		if (text == null && data != null)
			text = new String(data);
		return text;
	}

	public void setText(String text) throws IOException {
		if (text == null)
			throw new NullPointerException("text");
		if (OH.eq(text, this.text))
			return;
		setData(text.getBytes());
		this.text = text;
	}
	public void setData(byte[] data) throws IOException {
		if (raf == null)
			throw new IOException("Already closed");
		if (Arrays.equals(this.data, data))
			return;
		if (data.length > this.maxTextLength)
			throw new NullPointerException("text exceeds " + this.maxTextLength + " max char(s): " + data.length);
		int pos = this.start == 1 ? (2 + maxTextLength) : 1;
		this.raf.seek(pos);
		this.raf.write(data.length);
		this.raf.write(data);
		this.raf.seek(0);
		this.raf.write(pos);
		start = pos;
		this.data = data.clone();
		text = null;

	}

	@Override
	public void close() {
		IOH.close(this.raf);
		this.raf = null;
	}

	public static void main(String a[]) throws IOException {
		for (int i = 0; i < 100; i++) {
			FastSafeFile txt = new FastSafeFile(new File("/tmp/FastSafeFile.dat"), 10);
			System.out.println(txt.getText());
			txt.setText(SH.rightAlign('0', SH.toString(i), 11, true));
			txt.close();
		}
	}

}
