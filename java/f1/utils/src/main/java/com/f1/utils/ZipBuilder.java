package com.f1.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipBuilder {

	private ZipOutputStream stream;
	private ByteArrayOutputStream buffer;
	private byte data[] = null;

	public ZipBuilder() {
		this.stream = new ZipOutputStream(this.buffer = new ByteArrayOutputStream());
	}

	public void append(String fileName, byte[] data, long lastModifiedTimeMillis) {
		if (this.data != null)
			throw new IllegalStateException("already closed");
		OH.assertNotNull(data, "data");
		OH.assertNotNull(data, "fileName");
		ZipEntry entry = new ZipEntry(fileName);
		entry.setTime(lastModifiedTimeMillis);
		try {
			this.stream.putNextEntry(entry);
			this.stream.write(data);
			this.stream.closeEntry();
			this.stream.flush();
		} catch (IOException e) {
			throw new RuntimeException("Error adding " + fileName + " with " + data.length + " byte(s)", e);
		}
	}

	public byte[] build() {
		if (this.data == null)
			try {
				stream.flush();
				stream.finish();
				data = buffer.toByteArray();
				stream.close();
				this.stream = null;
				this.buffer = null;
			} catch (IOException e) {
				throw OH.toRuntime(e);
			}
		return data;
	}
	public void setLevel(int level) {
		if (this.data != null)
			throw new IllegalStateException("already closed");
		this.stream.setLevel(level);
	}

	public boolean isBuilt() {
		return this.data != null;
	}

}
