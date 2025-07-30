package com.f1.ami.center.dialects;

import java.util.LinkedHashSet;
import java.util.logging.Logger;

import com.f1.base.Table;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.impl.StringCharReader;

public class AmiDbDialect_HibernateSql implements AmiDbDialect {
	final static private Logger log = LH.get();

	private static final String DROP = "drop";
	private static final String CREATE = "create";
	private static final String INSERT = "insert";
	private static final String SELECT = "select";
	private static final String UPDATE = "update";
	private static final String DELETE = "delete";
	private static final String ALTER = "alter";
	private static final String AS = "as";
	private static final String ON = "on";
	private static final String FROM = "from";
	private static final String WHERE = "where";
	private static final String GROUP = "group";
	private static final String HAVING = "having";
	private static final String JOIN = "join";
	private static final String OUTER = "outer";
	private static final String INNER = "inner";
	private static final String FULL = "full";
	private static final String LEFT = "left";
	private static final String RIGHT = "right";
	private static final String REPLACE_EQ_REGEX = "(?<=[a-zA-Z0-9_'\"`\\s])=(?=[a-zA-Z0-9_'\"`\\s])";
	private static final LinkedHashSet<String> KEYWORDS_AFTER_FROM = new LinkedHashSet<String>();
	static {
		KEYWORDS_AFTER_FROM.add(WHERE);
		KEYWORDS_AFTER_FROM.add(GROUP);
		KEYWORDS_AFTER_FROM.add(HAVING);
		KEYWORDS_AFTER_FROM.add(JOIN);
		KEYWORDS_AFTER_FROM.add(FULL);
		KEYWORDS_AFTER_FROM.add(OUTER);
		KEYWORDS_AFTER_FROM.add(INNER);
		KEYWORDS_AFTER_FROM.add(LEFT);
		KEYWORDS_AFTER_FROM.add(RIGHT);
	}
	private StringBuilder sbParsed;
	private StringBuilder sb;
	private StringCharReader scr;
	private boolean debug;

	private static final char SPACE = ' ';
	public static final String OPERATOR_EQUALS = "==";

	public AmiDbDialect_HibernateSql(PropertyController props) {
		this.debug = props.getOptional(PROPERTY_DEBUG, false);
		this.sb = new StringBuilder();
		this.sbParsed = new StringBuilder();
		this.scr = new StringCharReader();
	}

	@Override
	public String prepareQuery(String statement) {
		if (this.debug)
			LH.info(log, "AmiDbDialect Hibernate Preparing query:  " + statement);
		SH.clear(sbParsed);
		SH.clear(sb);
		scr.reset(SH.trim(statement));
		// Whitespace
		scr.readWhileAny(StringCharReader.WHITE_SPACE, sb);
		sbParsed.append(SH.toStringAndClear(sb));

		// Select Update Delete Drop
		scr.readUntilAny(StringCharReader.WHITE_SPACE, true, '\\', sb);
		String sqlCommand = SH.toStringAndClear(sb); // select update delete
		sbParsed.append(sqlCommand);

		if (SH.equalsIgnoreCase(sqlCommand, DROP))
			return statement;
		else if (SH.equalsIgnoreCase(sqlCommand, CREATE))
			return statement;
		else if (SH.equalsIgnoreCase(sqlCommand, INSERT))
			return statement;
		else if (SH.equalsIgnoreCase(sqlCommand, SELECT))
			prepareSelect();
		else if (SH.equalsIgnoreCase(sqlCommand, UPDATE))
			prepareUpdate();
		else if (SH.equalsIgnoreCase(sqlCommand, DELETE))
			prepareDelete();
		else if (SH.equalsIgnoreCase(sqlCommand, ALTER))
			prepareAlter();

		scr.readUntil(StringCharReader.EOF, sb);
		sbParsed.append(SH.toStringAndClear(sb));
		if (this.debug)
			LH.info(log, "AmiDbDialect Hibernate transformed query:  " + sbParsed.toString());
		return SH.toStringAndClear(sbParsed);
		//		return statement;
	}

	@Override
	public Table prepareResult(Table r) {
		// TODO Auto-generated method stub
		return r;
	}
	private void prepareAlter() {
		scr.readUntil(StringCharReader.EOF, sb);
		String alter = SH.toStringAndClear(sb);
		alter = SH.replaceAll(alter, "not null", "NoNull");
		sbParsed.append(alter);
	}
	private void prepareUpdate() {
		// Whitespace
		scr.readWhileAny(StringCharReader.WHITE_SPACE, sb);
		sbParsed.append(SH.toStringAndClear(sb));

		// TableName
		scr.readUntilAny(StringCharReader.WHITE_SPACE, true, '\\', sb);
		String tableName = SH.toStringAndClear(sb); // select update delete
		sbParsed.append(tableName);

		// Update Columns
		scr.setCaseInsensitive(true);
		scr.readUntilSequence(WHERE, sb);
		scr.setCaseInsensitive(false);

		boolean hasWhere = SH.is(sb);
		if (!hasWhere) {
			// Read until next white space
			scr.readUntilAny(StringCharReader.WHITE_SPACE, true, sb);
		}

		boolean hasUpdateColumns = SH.is(sb);
		if (hasUpdateColumns) {
			String updateColumns = SH.toStringAndClear(sb);
			sbParsed.append(updateColumns);
		}

		// WHERE
		if (hasWhere) {
			// Whitespace
			scr.readWhileAny(StringCharReader.WHITE_SPACE, sb);
			sbParsed.append(SH.toStringAndClear(sb));

			scr.setCaseInsensitive(true);
			scr.expectSequence(WHERE);
			scr.setCaseInsensitive(false);
			sbParsed.append(WHERE);

			scr.readUntil(StringCharReader.EOF, sb);
			String afterWhere = SH.toStringAndClear(sb);
			sbParsed.append(afterWhere.replaceAll(REPLACE_EQ_REGEX, OPERATOR_EQUALS));
		}
	}

	private void prepareDelete() {
		// Whitespace
		scr.readWhileAny(StringCharReader.WHITE_SPACE, sb);
		sbParsed.append(SH.toStringAndClear(sb));

		prepareFrom();
		/*
		// From
		scr.setCaseInsensitive(true);
		scr.expectSequence(FROM);
		scr.setCaseInsensitive(false);
		sbParsed.append(FROM);
		
		// Whitespace
		scr.readWhileAny(StringCharReader.WHITE_SPACE, sb);
		sbParsed.append(SH.toStringAndClear(sb));
		
		// TableName
		scr.readUntilAny(StringCharReader.WHITE_SPACE, true, '\\', sb);
		String tableName = SH.toStringAndClear(sb);
		sbParsed.append(tableName);
		*/

		// After From
		scr.readUntil(StringCharReader.EOF, sb);
		String afterFrom = SH.toStringAndClear(sb);
		sbParsed.append(afterFrom.replaceAll(REPLACE_EQ_REGEX, OPERATOR_EQUALS));

		// No longer needed
		/*
		
		// Has Where 
		scr.setCaseInsensitive(true);
		scr.readUntilSequence(WHERE, sb);
		scr.setCaseInsensitive(false);
		
		boolean hasWhere = SH.length(sb) > 0;
		
		if (!hasWhere) {
			// Read until next white space
			scr.readUntilAny(StringCharReader.WHITE_SPACE, true, sb);
		}
		
		// WHERE
		if (hasWhere) {
			// Whitespace
			scr.readWhileAny(StringCharReader.WHITE_SPACE, sb);
			sbParsed.append(SH.toStringAndClear(sb));
		
			// WHERE
			scr.setCaseInsensitive(true);
			scr.expectSequence(WHERE);
			scr.setCaseInsensitive(false);
			sbParsed.append(WHERE);
		
			scr.readUntil(StringCharReader.EOF, sb);
			String afterWhere = SH.toStringAndClear(sb);
			sbParsed.append(afterWhere.replaceAll(REPLACE_EQ_REGEX, OPERATOR_EQUALS));
		}
		*/
	}

	private void prepareFrom() {
		// From
		scr.setCaseInsensitive(true);
		scr.expectSequence(FROM);
		scr.setCaseInsensitive(false);
		sbParsed.append(FROM);

		// Whitespace
		scr.readWhileAny(StringCharReader.WHITE_SPACE, sb);
		sbParsed.append(SH.toStringAndClear(sb));

		// TableName
		scr.readUntilAny(StringCharReader.WHITE_SPACE, true, '\\', sb);
		String tableName = SH.toStringAndClear(sb);
		sbParsed.append(tableName);

		// Whitespace
		scr.readWhileAny(StringCharReader.WHITE_SPACE, sb);
		sbParsed.append(SH.toStringAndClear(sb));

		scr.setCaseInsensitive(true);
		boolean hasAs = scr.peakSequence(AS);
		scr.setCaseInsensitive(false);
		if (hasAs) {
			scr.setCaseInsensitive(true);
			scr.expectSequence(AS);
			scr.setCaseInsensitive(false);
			sbParsed.append(AS);

			// Whitespace
			scr.readWhileAny(StringCharReader.WHITE_SPACE, sb);
			sbParsed.append(SH.toStringAndClear(sb));

			// Table Alias
			scr.readUntilAny(StringCharReader.WHITE_SPACE, true, sb);
			sbParsed.append(SH.toStringAndClear(sb));
		} else {
			scr.mark();
			//			scr.readWhileAny(StringCharReader.WHITE_SPACE, sb);
			scr.readUntilAny(StringCharReader.WHITE_SPACE, true, sb);
			String nextWord = SH.toStringAndClear(sb);
			if (SH.is(nextWord) && !KEYWORDS_AFTER_FROM.contains(SH.toLowerCase(nextWord))) {
				// Table Alias
				sbParsed.append(AS).append(SPACE).append(nextWord);
			} else {
				scr.returnToMark();
			}
		}
	}

	private void prepareSelect() {
		// Hack to support : select next_val as id_val from seq_id
		boolean isSeq = scr.expectSequenceNoThrow(" next_val as id_val ");
		if (isSeq) {
			sbParsed.append(" max(next_val) as id_val ");
			// From
			scr.setCaseInsensitive(true);
			scr.expectSequence(FROM);
			scr.setCaseInsensitive(false);
			sbParsed.append(FROM);

			// Whitespace
			scr.readWhileAny(StringCharReader.WHITE_SPACE, sb);
			sbParsed.append(SH.toStringAndClear(sb));

			// TableName
			scr.readUntilAny(StringCharReader.WHITE_SPACE, true, '\\', sb);
			String tableName = SH.toStringAndClear(sb);
			sbParsed.append(tableName);
			return;
		}

		// Whitespace
		scr.readWhileAny(StringCharReader.WHITE_SPACE, sb);
		sbParsed.append(SH.toStringAndClear(sb));

		// Columns
		scr.setCaseInsensitive(true);
		scr.readUntilSequence(FROM, sb);
		scr.setCaseInsensitive(false);
		String beforeFromColumns = SH.toStringAndClear(sb);
		sbParsed.append(beforeFromColumns);

		// Whitespace
		scr.readWhileAny(StringCharReader.WHITE_SPACE, sb);
		sbParsed.append(SH.toStringAndClear(sb));

		prepareFrom();
		/*
		// From
		scr.setCaseInsensitive(true);
		scr.expectSequence(FROM);
		scr.setCaseInsensitive(false);
		sbParsed.append(FROM);
		
		// Whitespace
		scr.readWhileAny(StringCharReader.WHITE_SPACE, sb);
		sbParsed.append(SH.toStringAndClear(sb));
		
		// TableName
		scr.readUntilAny(StringCharReader.WHITE_SPACE, true, '\\', sb);
		String tableName = SH.toStringAndClear(sb);
		sbParsed.append(tableName);
		
		//		// Whitespace
		//		scr.readWhileAny(StringCharReader.WHITE_SPACE, sb);
		//		sbParsed.append(SH.toStringAndClear(sb));
		
		// Has Where -> Table Alias
		scr.setCaseInsensitive(true);
		scr.readUntilSequence(WHERE, sb);
		scr.setCaseInsensitive(false);
		
		boolean hasWhere = SH.length(sb) > 0;
		
		if (!hasWhere) {
			// Whitespace
			scr.readWhileAny(StringCharReader.WHITE_SPACE, sb);
			SH.clear(sb);
			//			sbParsed.append(SH.toStringAndClear(sb));
			//			sbParsed.append(SPACE);
		
			// Read until next white space
			scr.readUntilAny(StringCharReader.WHITE_SPACE, true, sb);
		}
		
		boolean hasTableAlias = SH.is(sb);
		if (hasTableAlias) {
			String tableAlias = SH.trimStart(' ', SH.toStringAndClear(sb));
			sbParsed.append(SPACE).append(AS).append(SPACE);
			sbParsed.append(tableAlias);
		}
		// Has Where -> Table Alias
		*/
		// After From
		scr.readUntil(StringCharReader.EOF, sb);
		String afterFrom = SH.toStringAndClear(sb);
		sbParsed.append(afterFrom.replaceAll(REPLACE_EQ_REGEX, OPERATOR_EQUALS));

		// because of after from we no longer need below
		/*
		scr.setCaseInsensitive(true);
		boolean hasWhere = scr.peakSequence(WHERE);
		scr.setCaseInsensitive(false);
		
		// WHERE
		if (hasWhere) {
			// Whitespace
			scr.readWhileAny(StringCharReader.WHITE_SPACE, sb);
			sbParsed.append(SH.toStringAndClear(sb));
		
			// WHERE
			scr.setCaseInsensitive(true);
			scr.expectSequence(WHERE);
			scr.setCaseInsensitive(false);
			sbParsed.append(WHERE);
		
			scr.readUntil(StringCharReader.EOF, sb);
			String afterWhere = SH.toStringAndClear(sb);
			sbParsed.append(afterWhere.replaceAll(REPLACE_EQ_REGEX, OPERATOR_EQUALS));
		}
		*/

	}
	public static void main(String[] args) {
		//		String s = "hello world ";
		//		StringBuilder sb = new StringBuilder();
		//		StringCharReader scr = new StringCharReader();
		//		scr.reset(s);
		//		scr.setCaseInsensitive(true);
		//		System.out.println(scr.expectSequenceNoThrow(WHERE));
		//		scr.readUntilSequence(WHERE, sb);
		//		System.out.println(sb.toString());
		//		System.out.println(SH.is(sb));
		//		scr.readUntil(StringCharReader.EOF, sb);
		//		System.out.println(sb.toString());
		//		System.out.println(SH.is(sb));
		//
		//		AmiDbDialect_HibernateSql sql = new AmiDbDialect_HibernateSql();
		//		//		String statement = "select employee0_.id as id1_0_, employee0_.first_name as first_na2_0_, employee0_.last_name as last_nam3_0_, employee0_.salary as salary4_0_, employee0_.task_id as task_id5_0_ from EMPLOYEE employee0_";
		//		//		String statement = "select employee0_.id as id1_0_0_, employee0_.first_name as first_na2_0_0_, employee0_.last_name as last_nam3_0_0_, employee0_.salary as salary4_0_0_, employee0_.task_id as task_id5_0_0_ from EMPLOYEE employee0_ where employee0_.id=2";
		//		String statement = "select employee0_.id as id1_0_, employee0_.first_name as first_na2_0_, employee0_.last_name as last_nam3_0_, employee0_.salary as salary4_0_, employee0_.task_id as task_id5_0_ from EMPLOYEE where id=1";
		//		//		String statement = "delete from EMPLOYEE where id=2";
		//		String out = sql.prepareQuery(statement);
		//		System.out.println(out);

		String str = "asdf ==13l and `blah`=\"asdf'";
		//		String string = str.replaceAll("(?<=[a-zA-Z0-9_'\"\\s])=(?=[a-zA-Z0-9_'\"\\s])", "==");
		String string = str.replaceAll(REPLACE_EQ_REGEX, "==");
		System.out.println(string);
		System.out.println("6 example input 4".replaceAll("(?:\\d)(.*)(?:\\d)", "number$11"));

	}
}
