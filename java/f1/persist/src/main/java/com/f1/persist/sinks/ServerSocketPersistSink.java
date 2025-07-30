package com.f1.persist.sinks;

import java.io.EOFException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import com.f1.persist.PersistSink;
import com.f1.utils.OH;

public class ServerSocketPersistSink implements PersistSink {

	private OutputStream out;
	private InputStream in;
	private int cnt = 0;

	public ServerSocketPersistSink(OutputStream out, InputStream in) {
		this.out = out;
		this.in = in;
	}

	@Override
	public void write(byte[] data) {
		try {
			out.write(data);
			out.flush();
			cnt++;
			byte b = (byte) in.read();
			if (b != (byte) (cnt % 100))
				throw new EOFException(b + "!=" + (cnt % 100));
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

}
