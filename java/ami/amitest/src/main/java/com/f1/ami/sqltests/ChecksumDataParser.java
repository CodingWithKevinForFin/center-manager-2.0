package com.f1.ami.sqltests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.f1.utils.SH;

public class ChecksumDataParser {
	private final BufferedReader bufferedReader;

	/**
	 * Open BufferedReader for the reading checksum file
	 * 
	 * @param inFileName
	 *            file name
	 * @throws FileNotFoundException
	 */
	public ChecksumDataParser(String inFileName) throws FileNotFoundException {
		bufferedReader = new BufferedReader(new FileReader(new File(inFileName)));
	}

	/**
	 * Parse the next line from checksum file. Skip comments.
	 * 
	 * @return checksums map in form "table.col->checksum" or empty map if negative test. Null if no more lines
	 * @throws IOException
	 */
	public Map<String, Long> next() throws IOException {
		Map<String, Long> checksums = new HashMap<String, Long>();
		String line;
		boolean foundFirstNonEmpty = false;

		while ((line = bufferedReader.readLine()) != null) {
			line = SH.trimWhitespace(line);
			if (line.startsWith("//"))
				continue; // skips comments
			boolean isEmpty = SH.isEmpty(line);
			if (isEmpty && !foundFirstNonEmpty) {
				continue;
			} else if (!isEmpty) {
				if (line.startsWith("---")) {
					return checksums;
				}
				parseLine(line, checksums);
				foundFirstNonEmpty = true;
			} else if (isEmpty) {
				break;
			}
		}

		return checksums.size() > 0 ? checksums : null;
	}

	/**
	 * Parse the checksum file line in form "table:col1=hex,col2=hex,col3=hex"
	 * 
	 * @param line
	 *            checksum line
	 * @param checksums
	 *            map in form "table.col->checksum"
	 */
	private void parseLine(String line, Map<String, Long> checksums) {
		// Parse table and column checksums
		line = SH.trimWhitespace(line);
		String[] array = line.split("[:,]+");
		String tableName = array[0];
		for (int i = 1; i < array.length; i++) {
			String[] s = array[i].split("=");
			Long checksum = Long.decode(s[1]);
			checksums.put(tableName + "." + s[0], checksum);
		}
	}
}
