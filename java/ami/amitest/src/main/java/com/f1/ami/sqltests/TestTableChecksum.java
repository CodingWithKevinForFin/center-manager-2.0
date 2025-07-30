package com.f1.ami.sqltests;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.utils.IOH;
import com.f1.utils.SH;

public class TestTableChecksum {
	private static final String SQL_FILE_EXT = ".sql";
	private static final String CKSUM_FILE_EXT = ".cksum";
	private final String urlPrefix;
	private final String adminUser;
	private final String adminPwd;
	private final AmiTestHelper testHelper;
	private final AmiTestHelper adminHelper;
	private final String outFolder;

	public TestTableChecksum(String urlPrefix, String user, String pwd, String outFolder) throws ClassNotFoundException, SQLException {
		this.urlPrefix = urlPrefix;
		this.adminUser = user;
		this.adminPwd = pwd;
		if (outFolder == null)
			this.outFolder = "";
		else {
			outFolder = SH.replaceAll(outFolder, '\\', '/');
			if (!SH.endsWith(outFolder, '/'))
				outFolder = outFolder + '/';
			this.outFolder = outFolder;
		}
		testHelper = new AmiTestHelper();
		adminHelper = new AmiTestHelper();
		adminHelper.openConnection(urlPrefix, adminUser, adminPwd);
	}

	/**
	 * Validates AMI DB not empty
	 * 
	 * @return true if DB doesn't have user objects
	 * @throws SQLException
	 */
	public boolean validateInitilConditions() throws SQLException {
		if (!adminHelper.isAmiDBEmpty()) {
			System.err.println("AMI DB not empty");
			System.err.println("Aborting...");
			return false;
		}
		return true;
	}

	private void cleanup() throws SQLException {
		adminHelper.dropUserObjects();
	}

	/**
	 * Main method, do iterate thru the list of SQL tuples, execute and compare the resulting tables checksum against the checksum file
	 * 
	 * @param fileName
	 *            the input file name without extension. It should be pair of files available.
	 * @throws SQLException
	 * @throws IOException
	 * @throws ParseException
	 * @throws ClassNotFoundException
	 */
	public void compare(String fileName) throws SQLException, IOException, ParseException, ClassNotFoundException {
		String inSQLFileName = fileName + SQL_FILE_EXT;
		String inCksumFileName = fileName + CKSUM_FILE_EXT;
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("Using input data file pair: " + fileName + ".*");

		TupleDataParser tp = new TupleDataParser(inSQLFileName);
		ChecksumDataParser resultParser = new ChecksumDataParser(inCksumFileName);

		while (true) {
			TupleDataParser.Tuple tuple = tp.next();
			if (tuple == null) // no more data
				break;

			Map<String, Long> checksumMap = resultParser.next();
			if (checksumMap == null) { // no more checksum data
				break;
			}

			System.out.println("Sequence #" + tuple.sequence);

			tuple = checkForAdminUser(tuple);

			// Perform SQL operations
			if (tuple.user != null) {
				testHelper.openConnection(urlPrefix, tuple.user, tuple.password);
			}
			if (testHelper.isClosed()) { // No user provided in SQL file - will use admin
				testHelper.openConnection(urlPrefix, adminUser, adminPwd);
			}

			if (checksumMap.size() > 0) {
				List<String> tableNames = null;
				try {
					testHelper.executeSQL(tuple.sql);
					tableNames = adminHelper.getUserTableNames();
				} catch (SQLException e) {
					System.err.println("> " + tuple.sql);
					System.err.println("Test failed");
					e.printStackTrace();
					continue;
				}
				for (String t : tableNames) {
					System.out.println("Table: " + t);
					compareTableChecksum(t, checksumMap);
				}
			} else {
				boolean sts = testHelper.executeSQLNegative(tuple.sql);
				if (sts) {
					System.out.println("Negative test OK");
				} else {
					System.err.println("Negative test failed");
				}
			}
		}

		// Drop all user objects
		cleanup();
	}

	private TupleDataParser.Tuple checkForAdminUser(TupleDataParser.Tuple tuple) {
		if (tuple.user != null && tuple.user.equals("admin")) {
			tuple.user = adminUser;
			tuple.password = adminPwd;
		}
		return tuple;
	}

	/**
	 * Compare table per-column checksums with the checksum result data
	 * 
	 * @param tableName
	 *            table name
	 * @param checksumMap
	 *            checksums map in form "table.col->checksum"
	 * @throws SQLException
	 */
	private void compareTableChecksum(String tableName, Map<String, Long> checksumMap) throws SQLException {
		boolean failed = false;
		List<String> columnNames = adminHelper.getColumnNames(tableName);
		for (String col : columnNames) {
			long checksum = adminHelper.getChecksum(tableName, col);
			Long expectedChecksum = checksumMap.get(tableName + "." + col);
			if (expectedChecksum == null) {
				System.err.println("Checksum file doesn't have data for table " + tableName + ", column " + col);
				failed = true;
			} else if (checksum != expectedChecksum) {
				System.err.print("Checksum mismatch for table " + tableName + ", column " + col);
				System.err.println(", expected: " + SH.toHex(expectedChecksum) + " but got: " + SH.toHex(checksum));
				failed = true;
			}
		}

		// All table columns checksums are OK
		if (failed) {
			System.err.println("Table " + tableName + " checksum failed");
		} else {
			System.out.println("Table " + tableName + " checksum OK");
		}
	}

	/**
	 * Produces checksum files for the test SQL statements
	 * 
	 * @param fileName
	 *            the test file name
	 * @throws SQLException
	 * @throws IOException
	 * @throws ParseException
	 * @throws ClassNotFoundException
	 */
	public void produceChecksumFiles(String fileName) throws SQLException, IOException, ParseException, ClassNotFoundException {
		String inSQLFileName = fileName + SQL_FILE_EXT;
		File f = new File(inSQLFileName);
		String directory = SH.replaceAll(f.getParentFile().getAbsolutePath(), '\\', '/') + '/';
		String sqlFileName = f.getName();
		IOH.ensureDir(new File(directory + this.outFolder));

		String outCksumFileName = directory + this.outFolder + sqlFileName + CKSUM_FILE_EXT + ".2";
		String outSQLFileName = directory + this.outFolder + sqlFileName + SQL_FILE_EXT + ".2";

		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("Using input data file: " + inSQLFileName);
		System.out.println("Checksum output data file: " + outCksumFileName);

		TupleDataParser tp = new TupleDataParser(inSQLFileName);
		ChecksumDataWriter resultWriter = new ChecksumDataWriter(outCksumFileName);
		TupleDataWriter tupleWriter = new TupleDataWriter(outSQLFileName);

		while (true) {
			TupleDataParser.Tuple tuple = tp.next();
			if (tuple == null) // no more data
				break;

			tupleWriter.writeText(tuple.comment);
			if (tuple.user != null) { // we have user/password for the tuple
				tupleWriter.writeUser(tuple.user, tuple.password);
			}
			tupleWriter.writeText(tuple.text).writeSeparator();

			System.out.println("Sequence #" + tuple.sequence);

			tuple = checkForAdminUser(tuple);

			// Perform SQL operations
			if (tuple.user != null) {
				testHelper.openConnection(urlPrefix, tuple.user, tuple.password);
			}
			if (testHelper.isClosed()) { // No user provided in SQL file - will use admin
				testHelper.openConnection(urlPrefix, adminUser, adminPwd);
			}

			try {
				testHelper.executeSQL(tuple.sql);
			} catch (SQLException e) {
				System.err.println("> " + tuple.sql);
				resultWriter.writeHeader(tuple.comment).writeNegativeIndicator().writeSeparator();
				continue;
			}

			List<String> tableNames = adminHelper.getUserTableNames();

			resultWriter.writeHeader(tuple.comment);
			for (String t : tableNames) {
				System.out.println("Table: " + t);
				writeTableChecksum(t, resultWriter);
			}
			resultWriter.writeSeparator();
		}

		// Drop all user objects
		cleanup();
		resultWriter.close();
		tupleWriter.close();
	}

	/**
	 * Produces column checksums for a table
	 * 
	 * @param tableName
	 *            table name
	 * @param resultWriter
	 *            the checksum file writer
	 * @throws SQLException
	 * @throws IOException
	 */
	private void writeTableChecksum(String tableName, ChecksumDataWriter resultWriter) throws SQLException, IOException {
		List<String> columnNames = adminHelper.getColumnNames(tableName);
		Map<String, Long> checksumMap = new HashMap<String, Long>();
		for (String col : columnNames) {
			long checksum = adminHelper.getChecksum(tableName, col);
			checksumMap.put(col, checksum);
		}
		resultWriter.writeTableChecksum(tableName, checksumMap);
	}

	/**
	 * Do the lookup for the input files in "user.dir"/data directory. Only pairs with <.sql>/<.cksum> selected
	 * 
	 * @return list of file names without the extension
	 * @throws IOException
	 */
	public static List<String> inputFilesLookup() throws IOException {
		String rootDir = System.getProperty("user.dir");
		Set<String> sqlFiles = new HashSet<String>();
		Set<String> cksumFiles = new HashSet<String>();

		// List all the files for the directory
		String inputDataDir = rootDir + "/data";
		File dir = new File(inputDataDir);
		File[] files = dir.listFiles();
		for (File file : files) {
			if (!file.isDirectory()) {
				String fileName = file.getCanonicalPath();
				if (fileName.endsWith(SQL_FILE_EXT)) {
					sqlFiles.add(fileName.substring(0, fileName.length() - SQL_FILE_EXT.length()));
				} else if (fileName.endsWith(CKSUM_FILE_EXT)) {
					cksumFiles.add(fileName.substring(0, fileName.length() - CKSUM_FILE_EXT.length()));
				}
			}
		}

		// Compile and validate the outcome
		List<String> fileList = new ArrayList<String>();
		for (String s : sqlFiles) {
			if (cksumFiles.contains(s)) { // make sure we have matching pairs
				fileList.add(s);
			}
		}
		return fileList;
	}

	/**
	 * Do the lookup for the SQL input files in "user.dir"/data directory
	 * 
	 * @return list of file names without the extension
	 * @throws IOException
	 */
	public static List<String> inputSQLFilesLookup() throws IOException {
		String rootDir = System.getProperty("user.dir");
		List<String> sqlFiles = new ArrayList<String>();

		// List all the files for the directory
		String inputDataDir = rootDir + "/data";
		File dir = new File(inputDataDir);
		File[] files = dir.listFiles();
		for (File file : files) {
			if (!file.isDirectory()) {
				String fileName = file.getCanonicalPath();
				if (fileName.endsWith(SQL_FILE_EXT)) {
					sqlFiles.add(fileName.substring(0, fileName.length() - SQL_FILE_EXT.length()));
				}
			}
		}
		return sqlFiles;
	}

	public static void main(String args[]) throws ClassNotFoundException, SQLException, IOException, ParseException {
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("Requires arguments: -url, -user, -pwd, Optional Arguments: -gen, -outFolder");
		System.out.println("\t-url - the jdbc url of the ami instance ex: jdbc:amisql:localhost:3280");
		System.out.println("\t-user - the default user for amidb ex: demo");
		System.out.println("\t-pwd - the password ex: demo123");
		System.out.println("\t-gen (optional) - true or false (default is false), true will generate sql and cksum files");
		System.out.println("\t-outFolder (optional) - (if gen is enabled) target directory to write files to relative to the data directory in this project");
		System.out.println("-----------------------------------------------------------------------------");
		
		ArgumentParser parser = new ArgumentParser(args);
		String urlPrefix = parser.getRequiredOption("-url");
		String user = parser.getRequiredOption("-user");
		String pwd = parser.getRequiredOption("-pwd");
		String genChecksum = parser.getOption("-gen");
		String outFolder = parser.getOption("-outFolder");
		boolean generateFlag = ((genChecksum != null) && genChecksum.equalsIgnoreCase("true"));

		TestTableChecksum tableChecksum = new TestTableChecksum(urlPrefix, user, pwd, outFolder);
		if (!tableChecksum.validateInitilConditions())
			return;

		if (!generateFlag) {
			List<String> inputFiles = inputFilesLookup();

			// Iterate thru all the input pairs
			for (String s : inputFiles) {
				tableChecksum.compare(s);
			}
			System.out.println("-----------------------------------------------------------------------------");
			System.out.println("Test completed");
		} else { // Generate the checksum file
			List<String> inputFiles = inputSQLFilesLookup();

			// Iterate thru all the input SQL files
			System.out.println("Creating checksum files");
			for (String s : inputFiles) {
				tableChecksum.produceChecksumFiles(s);
			}
			System.out.println("-----------------------------------------------------------------------------");
			System.out.println("Checksum creation completed");
		}
	}
}
