package com.f1.persist.sinks;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import com.f1.persist.PersistSink;
import com.f1.utils.OH;

public class OutputStreamPersistSink implements PersistSink {

	private OutputStream out;
	private int cnt = 0;

	public OutputStreamPersistSink(OutputStream out) {
		this.out = out;
	}

	@Override
	public void write(byte[] data) {
		try {
			out.write(data);
			out.flush();
		} catch (IOException e) {
			OH.toRuntime(e);
		}
	}

}
