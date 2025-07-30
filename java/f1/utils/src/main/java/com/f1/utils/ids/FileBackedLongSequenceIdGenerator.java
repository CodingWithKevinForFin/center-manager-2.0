package com.f1.utils.ids;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;

public class FileBackedLongSequenceIdGenerator implements LongSequenceIdGenerator {

	private File file;

	public static final Logger log = Logger.getLogger(FileBackedLongSequenceIdGenerator.class.getName());

	public FileBackedLongSequenceIdGenerator(File file) throws IOException {
		this.file = file;
		if (file.exists())
			readFile();
		else
			writeFile(0L);
		LH.info(log, "Using id generating file located at:", IOH.getFullPath(file));
	}

	private long readFile() throws IOException {
		try {
			String num = IOH.readText(file);
			if (SH.isnt(num)) {
				LH.severe(log, "File is empty, resetting to zero: ", IOH.getFullPath(this.file));
				writeFile(0L);
				return 0L;
			}
			num = SH.trim(num);
			return Long.parseLong(num);
		} catch (Exception e) {
			throw new IOException("Error parsing id file: " + IOH.getFullPath(file), e);
		}
	}

	private void writeFile(long l) throws IOException {
		IOH.writeText(file, SH.toString(l));
	}

	@Override
	public Long createNextId() {
		return createNextLongId();
	}

	@Override
	public long createNextLongIds(int count) {
		try {
			long start;
			synchronized (this) {
				start = readFile();
				writeFile(start + count);
			}
			return start;
		} catch (IOException e) {
			throw new RuntimeException("Exception getting next from file", e);
		}
	}

	@Override
	public long createNextLongId() {
		return createNextLongIds(1);
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

	public static class Factory implements com.f1.base.Factory<String, FileBackedLongSequenceIdGenerator> {

		final private File parent;

		public Factory(File parent_) {
			parent = parent_;
		}

		@Override
		public FileBackedLongSequenceIdGenerator get(String key) {
			try {
				return new FileBackedLongSequenceIdGenerator(new File(parent, key));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	}

}
