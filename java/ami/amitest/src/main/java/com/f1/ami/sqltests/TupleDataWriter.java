package com.f1.ami.sqltests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class TupleDataWriter {
	private final BufferedWriter bufferWriter;

	/**
	 * Create BufferedWriter for the output file
	 * 
	 * @param outFileName
	 * @throws IOException
	 */
	public TupleDataWriter(String outFileName) throws IOException {
		bufferWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outFileName))));
	}

	/**
	 * Put the separator (empty line)
	 * 
	 * @return reference to itself
	 * @throws IOException
	 */
	public TupleDataWriter writeSeparator() throws IOException {
		bufferWriter.newLine();
		return this;
	}

	/**
	 * Put the tuple in output file
	 * 
	 * @param header
	 * @return reference to itself
	 * @throws IOException
	 */
	public TupleDataWriter writeText(String header) throws IOException {
		bufferWriter.write(header);
		return this;
	}

	/**
	 * Put the user/password in output file
	 * 
	 * @param user
	 * @param password
	 * @return reference to itself
	 * @throws IOException
	 */
	public TupleDataWriter writeUser(String user, String password) throws IOException {

		String line = user.equals("admin") ? (":admin") : (":" + user + "|" + password);
		bufferWriter.write(line);
		bufferWriter.newLine();
		return this;
	}

	/**
	 * Close the output stream
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		bufferWriter.close();
	}
}
