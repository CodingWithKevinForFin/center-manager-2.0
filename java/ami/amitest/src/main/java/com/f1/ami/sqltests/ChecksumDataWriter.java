package com.f1.ami.sqltests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Set;

import com.f1.utils.SH;

public class ChecksumDataWriter {
	private final BufferedWriter bufferWriter;

	/**
	 * Create BufferedWriter for the output file
	 * 
	 * @param outFileName
	 *            output file name
	 * @throws FileNotFoundException
	 */
	public ChecksumDataWriter(String outFileName) throws FileNotFoundException {
		bufferWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outFileName))));
	}

	/**
	 * Write the checksum info to the file
	 * 
	 * @param tableName
	 *            table name
	 * @param checksums
	 *            checksums map in form "table.col->checksum"
	 * @return reference to itself
	 * @throws IOException
	 */
	public ChecksumDataWriter writeTableChecksum(String tableName, Map<String, Long> checksums) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(tableName).append(":");
		Set<Map.Entry<String, Long>> set = checksums.entrySet();
		int i = 0;
		for (Map.Entry<String, Long> e : set) {
			sb.append(e.getKey()).append("=").append(SH.toHex(e.getValue()));
			if (++i < set.size()) {
				sb.append(",");
			}
		}
		bufferWriter.write(sb.toString());
		bufferWriter.newLine();
		return this;
	}

	/**
	 * Put the separator (empty line)
	 * 
	 * @return reference to itself
	 * @throws IOException
	 */
	public ChecksumDataWriter writeSeparator() throws IOException {
		bufferWriter.newLine();
		return this;
	}

	public ChecksumDataWriter writeNegativeIndicator() throws IOException {
		bufferWriter.write("------------------");
		bufferWriter.newLine();
		return this;
	}

	/**
	 * Write the section header
	 * 
	 * @param header
	 *            the header
	 * @return reference to itself
	 * @throws IOException
	 */
	public ChecksumDataWriter writeHeader(String header) throws IOException {
		bufferWriter.write(header);
		return this;
	}

	/**
	 * Close the output stream
	 * 
	 * @return reference to itself
	 * @throws IOException
	 */
	public void close() throws IOException {
		bufferWriter.close();
	}
}
