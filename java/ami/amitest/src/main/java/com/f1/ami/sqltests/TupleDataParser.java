package com.f1.ami.sqltests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

import com.f1.utils.SH;

public class TupleDataParser {
	private final static String SEQUENCE_PREFIX = "// #";
	private final static String COMMENT_PREFIX = "//";
	private final static String USER_LOGIN_PREFIX = ":";
	private final BufferedReader bufferedReader;
	private final String fileName;
	private int counter;
	private int lineCounter;

	/**
	 * Open BufferedReader for the reading tuple SQL file
	 * 
	 * @param fileName
	 *            file name
	 * @throws IOException
	 */
	public TupleDataParser(String fileName) throws IOException {
		this.fileName = fileName;
		bufferedReader = new BufferedReader(new FileReader(new File(fileName)));
	}

	/**
	 * Parse the next line from the file.
	 * 
	 * @return the tuple object
	 * @throws IOException
	 * @throws ParseException
	 */
	public Tuple next() throws IOException, ParseException {
		String line;
		StringBuilder sink = new StringBuilder();
		StringBuilder comments = new StringBuilder();
		StringBuilder text = new StringBuilder();
		String sequence = "";
		UserInfo userinfo = null;

		// Write the test sequence number
		comments.append(SEQUENCE_PREFIX).append(++counter).append("\n");

		boolean foundFirstNonEmpty = false;
		while ((line = bufferedReader.readLine()) != null) {
			lineCounter++;
			line = SH.trimWhitespace(line);
			if (line.startsWith(SEQUENCE_PREFIX)) { // sequence
				sequence = line.substring(SEQUENCE_PREFIX.length());
				continue;
			} else if (line.startsWith(COMMENT_PREFIX)) { // Comments
				comments.append(line).append("\n");
				continue;
			} else if (line.startsWith(USER_LOGIN_PREFIX)) { // user|password
				userinfo = readUserAndPassword(line);
				continue;
			}

			boolean isEmpty = SH.isEmpty(line);
			if (isEmpty && !foundFirstNonEmpty) {
				continue;
			} else if (!isEmpty) {
				line = checkLineTerminator(line);
				sink.append(line);
				text.append(line).append("\n");
				foundFirstNonEmpty = true;
			} else if (isEmpty) {
				break;
			}
		}

		// add user login if it is part of the tuple
		if (sink.length() > 0) {
			Tuple tuple = new Tuple(sink.toString(), comments.toString(), text.toString(), sequence);
			if (userinfo != null) {
				tuple.addUserInfo(userinfo);
			}
			return tuple;
		}
		return null;
	}

	/**
	 * If the particular user/password combination defined for tuple - read it
	 * 
	 * @param line
	 * @return USerInfo class instance
	 * @throws ParseException
	 */
	private UserInfo readUserAndPassword(String line) throws ParseException {
		String[] array = line.split("[|:,]+");
		if (array.length > 2) {
			return new UserInfo(array[1], array[2]);
		} else if (array.length == 2) {
			if (array[1].equals("admin")) {
				return new UserInfo("admin", "admin");
			}
		}

		throw new ParseException("Invalid user/password", 0);
	}

	/**
	 * Validates for terminating semicolon
	 * 
	 * @param line
	 *            the line from SQL tuple files
	 * @return the same line
	 */
	private String checkLineTerminator(String line) {
		if (line.charAt(line.length() - 1) != ';') {
			System.err.println("No terminating semicolon at line %" + lineCounter + ", file: " + fileName);
			line = line + ";";
		}
		return line;
	}

	private static class UserInfo {
		String user;
		String password;

		UserInfo(String user, String password) {
			this.user = user;
			this.password = password;
		}
	};

	public static class Tuple {
		public String sql;
		public String text;
		public String comment;
		public String sequence;
		public String user;
		public String password;

		private Tuple(String sql, String comment, String text, String sequence) {
			this.sql = sql;
			this.comment = comment;
			this.text = text;
			this.sequence = sequence;
		}

		private void addUserInfo(UserInfo userinfo) {
			this.user = userinfo.user;
			this.password = userinfo.password;
		}
	}
}
