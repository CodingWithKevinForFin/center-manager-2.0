package com.f1.persist.sinks;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import com.f1.persist.PersistSink;
import com.f1.utils.OH;

public class InputStreamPersistSink extends PipedInputStream implements PersistSink {

	private PipedOutputStream outputStream;

	public InputStreamPersistSink(int bufferSize) {
		super(bufferSize);
		try {
			outputStream = new PipedOutputStream(this);
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}

	}

	@Override
	public void write(byte[] data) {
		try {
			outputStream.write(data);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

}
