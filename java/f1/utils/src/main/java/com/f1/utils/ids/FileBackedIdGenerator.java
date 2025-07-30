package com.f1.utils.ids;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;

public class FileBackedIdGenerator implements IdGenerator<Long> {

	private File file;

	public static final Logger log = Logger.getLogger(FileBackedIdGenerator.class.getName());

	public FileBackedIdGenerator(File file) throws IOException {
		this.file = file;
		if (file.exists())
			readFile();
		else
			writeFile(0L);
		LH.info(log, "Using id generating file located at:", IOH.getFullPath(file));
	}

	private long readFile() throws IOException {
		try {
			return Long.parseLong(IOH.readText(file));
		} catch (Exception e) {
			throw new IOException("Error parsing id file: " + IOH.getFullPath(file), e);
		}
	}

	private void writeFile(long l) throws IOException {
		IOH.writeText(file, SH.toString(l));
	}

	@Override
	public Long createNextId() {
		ArrayList<Long> t = new ArrayList<Long>(1);
		createNextIds(1, t);
		return t.get(0);
	}

	@Override
	public void createNextIds(int count, Collection<? super Long> sink) {
		try {
			long start;
			synchronized (this) {
				start = readFile();
				writeFile(start + count);
			}
			for (int i = 0; i < count; i++)
				sink.add(start + i);
		} catch (IOException e) {
			throw new RuntimeException("Exception getting next from file", e);
		}
	}

	public static void main(String a[]) throws IOException {
		BatchIdGenerator id = new BatchIdGenerator(new FileBackedIdGenerator(new File("id.txt")), 10);
		for (int i = 0; i < 25; i++)
			System.out.println(id.createNextId());
	}

	public static class Factory implements com.f1.base.Factory<String, FileBackedIdGenerator> {

		final private File parent;

		public Factory(File parent_) {
			parent = parent_;
		}

		@Override
		public FileBackedIdGenerator get(String key) {
			try {
				return new FileBackedIdGenerator(new File(parent, key));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	}

}
