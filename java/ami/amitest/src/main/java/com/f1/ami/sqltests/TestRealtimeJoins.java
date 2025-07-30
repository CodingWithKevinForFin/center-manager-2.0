package com.f1.ami.sqltests;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.Tuple3;
import com.f1.utils.structs.table.BasicTable;

public class TestRealtimeJoins {

	private static final long SEED = 5297L;
	private static final Random RNG = new Random(SEED);
	private static final int NUMBER_OF_GROUPS = 30; // Do not exceed Short.MAX_VALUE

	private static final String JOIN_TYPE_LEFT = "left";
	private static final String JOIN_TYPE_RIGHT = "right";
	private static final String JOIN_TYPE_OUTER_ONLY = "outer only";
	private static final String JOIN_TYPE_RIGHT_ONLY = "right only";
	private static final String JOIN_TYPE_LEFT_ONLY = "left only";
	private static final String JOIN_TYPE_OUTER = "outer";
	private static final String JOIN_TYPE_INNER = "inner";
	private static final List<String> JOIN_TYPES = CH.l(JOIN_TYPE_LEFT, JOIN_TYPE_RIGHT, JOIN_TYPE_OUTER_ONLY, JOIN_TYPE_RIGHT_ONLY, JOIN_TYPE_LEFT_ONLY, JOIN_TYPE_OUTER,
			JOIN_TYPE_INNER);
	private static final File root = new File("/tmp/realtimetests");
	private static final List<String> writtenFiles = new ArrayList<String>();

	private static final String TYPE_COLUMN = "__COLUMN";
	private static final String TYPE_COMMAND = "__COMMAND";
	private static final String TYPE_CONNECTION = "__CONNECTION";
	private static final String TYPE_DATASOURCE = "__DATASOURCE";
	private static final String TYPE_DATASOURCE_TYPE = "__DATASOURCE_TYPE";
	private static final String TYPE_GUI = "__GUI";
	private static final String TYPE_INDEX = "__INDEX";
	private static final String TYPE_PLUGIN = "__PLUGIN";
	private static final String TYPE_PROCEDURE = "__PROCEDURE";
	private static final String TYPE_PROPERTY = "__PROPERTY";
	private static final String TYPE_RELAY = "__RELAY";
	private static final String TYPE_RESOURCE = "__RESOURCE";
	private static final String TYPE_TABLE = "__TABLE";
	private static final String TYPE_TIMER = "__TIMER";
	private static final String TYPE_TRIGGER = "__TRIGGER";

	private static final String COL_HEADER_TABLE = "TableName";
	private static final String COL_HEADER_TRIGGER = "TriggerName";

	private static final String KEYWORD_TABLE = "public table";
	private static final String KEYWORD_TRIGGER = "trigger";

	private static final String DEFAULT_NAME_SOURCE = "source";
	private static final String DEFAULT_NAME_TARGET = "target";
	private static final String DEFAULT_NAME_COMPARISON = "comparison";

	private static final String CREATE_PUBLIC_TABLE_PREFIX = "create public table ";

	private static final byte SOURCE_INSERT = 0;
	private static final byte SOURCE_UPDATE = 1;
	private static final byte SOURCE_DELETE = 2;

	private static final Map<String, String> TYPE_2_COL_HEADER = new HashMap<String, String>();
	static {
		TYPE_2_COL_HEADER.put(TYPE_TABLE, COL_HEADER_TABLE);
		TYPE_2_COL_HEADER.put(TYPE_TRIGGER, COL_HEADER_TRIGGER);
	}
	private static final Map<String, String> TYPE_2_KEYWORD = new HashMap<String, String>();
	static {
		TYPE_2_KEYWORD.put(TYPE_TABLE, KEYWORD_TABLE);
		TYPE_2_KEYWORD.put(TYPE_TRIGGER, KEYWORD_TRIGGER);
	}

	private static final byte TRIGGER_TYPE_AMISCRIPT = 0;
	private static final byte TRIGGER_TYPE_AGGREGATE = 1;
	private static final byte TRIGGER_TYPE_PROJECTION = 2;
	private static final byte TRIGGER_TYPE_JOIN = 3;
	private static final String TRIGGER_KEYWORD_AMISCRIPT = "AMISCRIPT";
	private static final String TRIGGER_KEYWORD_AGGREGATE = "AGGREGATE";
	private static final String TRIGGER_KEYWORD_PROJECTION = "PROJECTION";
	private static final String TRIGGER_KEYWORD_JOIN = "JOIN";

	private final static Table tableA = new BasicTable(Double.class, "x", Double.class, "y", Double.class, "z");
	static {
		tableA.setTitle("tableA");
		tableA.getRows().addRow(null, 5d, 8d);
		tableA.getRows().addRow(4d, 11d, 11d);
		tableA.getRows().addRow(9d, 7d, null);
		tableA.getRows().addRow(6d, 13d, 33d);
		tableA.getRows().addRow(3d, null, 17d);
		tableA.getRows().addRow(4d, null, 99d);
		tableA.getRows().addRow(1d, 1d, 23d);
		tableA.getRows().addRow(null, 7d, 90d);
		tableA.getRows().addRow(1d, 5d, 50d);
	}
	private final static Table tableC = new BasicTable(Double.class, "a", Double.class, "b", Double.class, "c");
	static {
		tableC.setTitle("tableC");
		tableC.getRows().addRow(null, 2.0, 6.0);
		tableC.getRows().addRow(11.0, null, 8.0);
		tableC.getRows().addRow(9.0, 8.0, 2.0);
		tableC.getRows().addRow(9.0, 3.0, 5.0);
		tableC.getRows().addRow(4.0, 3.0, null);
	}
	public static final String AGG_FUNC_SUM = "sum";
	public static final String AGG_FUNC_MIN = "min";
	public static final String AGG_FUNC_MAX = "max";
	public static final String AGG_FUNC_COUNT = "count";
	public static final String AGG_FUNC_COUNT_UNIQUE = "countUnique";
	public static final String AGG_FUNC_CAT = "cat";
	public static final String AGG_FUNC_AVG = "avg";
	public static final String AGG_FUNC_VAR = "var";
	public static final String AGG_FUNC_VAR_S = "varS";
	public static final String AGG_FUNC_STDEV = "stdev";
	public static final String AGG_FUNC_STDEV_S = "stdevS";
	public static final String AGG_FUNC_FIRST = "first";
	public static final String AGG_FUNC_LAST = "last";
	public static final String AGG_FUNC_COVAR = "covar";
	public static final String AGG_FUNC_COVAR_S = "covarS";
	public static final String AGG_FUNC_COR = "cor";
	public static final String AGG_FUNC_BETA = "beta";

	public static final String TYPE_KEYWORD_INT = "int";
	public static final String TYPE_KEYWORD_DOUBLE = "double";
	public static final String TYPE_KEYWORD_STRING = "String";

	private static final String[] symbols;

	static {
		final String[] capitals = { "A", "B", "C", "D", "E", "F", "G" };
		symbols = new String[capitals.length * capitals.length];

		for (int i = 0; i < capitals.length; i++) {
			for (int j = 0; j < capitals.length; j++) {
				symbols[i * capitals.length + j] = capitals[i] + capitals[j];
			}
		}
	}

	public static final Map<String, Integer> FUNCTIONS_2_NUM_ARGS = new HashMap<String, Integer>();
	static {
		FUNCTIONS_2_NUM_ARGS.put(AGG_FUNC_SUM, 1);
		FUNCTIONS_2_NUM_ARGS.put(AGG_FUNC_MIN, 1);
		FUNCTIONS_2_NUM_ARGS.put(AGG_FUNC_MAX, 1);
		FUNCTIONS_2_NUM_ARGS.put(AGG_FUNC_COUNT, 1);
		FUNCTIONS_2_NUM_ARGS.put(AGG_FUNC_COUNT_UNIQUE, 1);
		FUNCTIONS_2_NUM_ARGS.put(AGG_FUNC_CAT, 1); // In this file, cat effectively has one argument b/c only one column, other two arguments are hard-coded
		FUNCTIONS_2_NUM_ARGS.put(AGG_FUNC_AVG, 1);
		FUNCTIONS_2_NUM_ARGS.put(AGG_FUNC_VAR, 1);
		FUNCTIONS_2_NUM_ARGS.put(AGG_FUNC_VAR_S, 1);
		FUNCTIONS_2_NUM_ARGS.put(AGG_FUNC_STDEV, 1);
		FUNCTIONS_2_NUM_ARGS.put(AGG_FUNC_STDEV_S, 1);
		FUNCTIONS_2_NUM_ARGS.put(AGG_FUNC_FIRST, 1);
		FUNCTIONS_2_NUM_ARGS.put(AGG_FUNC_LAST, 1);
		FUNCTIONS_2_NUM_ARGS.put(AGG_FUNC_COVAR, 2);
		FUNCTIONS_2_NUM_ARGS.put(AGG_FUNC_COVAR_S, 2);
		FUNCTIONS_2_NUM_ARGS.put(AGG_FUNC_COR, 2);
		FUNCTIONS_2_NUM_ARGS.put(AGG_FUNC_BETA, 2);
	}
	public static final Map<String, String> FUNCTIONS_2_RETURN_TYPE = new HashMap<String, String>();
	static {
		FUNCTIONS_2_RETURN_TYPE.put(AGG_FUNC_SUM, TYPE_KEYWORD_DOUBLE);
		FUNCTIONS_2_RETURN_TYPE.put(AGG_FUNC_MIN, TYPE_KEYWORD_DOUBLE);
		FUNCTIONS_2_RETURN_TYPE.put(AGG_FUNC_MAX, TYPE_KEYWORD_DOUBLE);
		FUNCTIONS_2_RETURN_TYPE.put(AGG_FUNC_COUNT, TYPE_KEYWORD_INT);
		FUNCTIONS_2_RETURN_TYPE.put(AGG_FUNC_COUNT_UNIQUE, TYPE_KEYWORD_INT);
		FUNCTIONS_2_RETURN_TYPE.put(AGG_FUNC_CAT, TYPE_KEYWORD_STRING);
		FUNCTIONS_2_RETURN_TYPE.put(AGG_FUNC_AVG, TYPE_KEYWORD_DOUBLE);
		FUNCTIONS_2_RETURN_TYPE.put(AGG_FUNC_VAR, TYPE_KEYWORD_DOUBLE);
		FUNCTIONS_2_RETURN_TYPE.put(AGG_FUNC_VAR_S, TYPE_KEYWORD_DOUBLE);
		FUNCTIONS_2_RETURN_TYPE.put(AGG_FUNC_STDEV, TYPE_KEYWORD_DOUBLE);
		FUNCTIONS_2_RETURN_TYPE.put(AGG_FUNC_STDEV_S, TYPE_KEYWORD_DOUBLE);
		FUNCTIONS_2_RETURN_TYPE.put(AGG_FUNC_FIRST, TYPE_KEYWORD_DOUBLE);
		FUNCTIONS_2_RETURN_TYPE.put(AGG_FUNC_LAST, TYPE_KEYWORD_DOUBLE);
		FUNCTIONS_2_RETURN_TYPE.put(AGG_FUNC_COVAR, TYPE_KEYWORD_DOUBLE);
		FUNCTIONS_2_RETURN_TYPE.put(AGG_FUNC_COVAR_S, TYPE_KEYWORD_DOUBLE);
		FUNCTIONS_2_RETURN_TYPE.put(AGG_FUNC_COR, TYPE_KEYWORD_DOUBLE);
		FUNCTIONS_2_RETURN_TYPE.put(AGG_FUNC_BETA, TYPE_KEYWORD_DOUBLE);
	}

	private static final Set<Double> UNDEF_VALS = CH.s(Double.POSITIVE_INFINITY, null, Double.NEGATIVE_INFINITY, Double.NaN);

	public static void main(String args[]) throws ClassNotFoundException, SQLException, IOException {
		Class.forName("com.f1.ami.amidb.jdbc.AmiDbJdbcDriver");
		//		testQuery("test1", "select * from gravity;");
		//		testQuery("test2", "create public table tblD as select * from tableA, tableC;", tableA, tableC);

		//		boolean testTriggerAggregate = testTriggerAggregate();
		//		boolean testTriggerProjection = testTriggerProjection();

		//		boolean testTriggerJoin = testTriggerJoin();
		//		boolean testTriggerLeftJoin = testTriggerLeftJoin();
		//		boolean testTriggerRightJoin = testTriggerRightJoin();
		//		boolean testTriggerOuterJoin = testTriggerOuterJoin();
		//		boolean testTriggerLeftOnlyJoin = testTriggerLeftOnlyJoin();
		//		boolean testTriggerRightOnlyJoin = testTriggerRightOnlyJoin();
		//		boolean testTriggerOuterOnlyJoin = testTriggerOuterOnlyJoin();
		//		boolean testTriggerAggCascaded = testTriggerAggregateCascaded();
		//		boolean testTriggerAggregateOneGroup = testTriggerAggregateOneGroup();
		//		boolean testTriggerAggMulti = testTriggerAggregateMultipleTriggersSameSource();
		//		boolean testTriggerProjMultiTriggerTarget = testTriggerProjectionSingleTargetMultipleTriggers();
		//		boolean testTriggerJoinCascadedGeneric = testTriggerJoinCascadedGeneric(JOIN_TYPE_LEFT, JOIN_TYPE_LEFT, JOIN_TYPE_RIGHT_ONLY);
		//		boolean testTriggerJoinCascadedAll = testTriggerJoinCascadedAll();
		//		boolean testTriggerAggregateArithmetic = testTriggerAggregateArithmetic();
		//		boolean testTriggerProjectionCascaded = testTriggerProjectionCascaded();
		//		boolean testTriggerAggregateAllFunctions = testTriggerAggregateAllFunctions(); ///
		//		boolean testGroupByReference = testTriggerGroupByReference();
		//		boolean testNoNullInsert = testNoNullInsert();
		//		boolean testNoNullUpdate = testNoNullUpdate();
		System.out.println("@@@@@@@@@@@@@@@@@");
		//		System.out.println("projection: " + testTriggerProjection);
		//		System.out.println("aggregate: " + testTriggerAggregate);
		//		System.out.println("join: " + testTriggerJoin);
		//		System.out.println("left join: " + testTriggerLeftJoin);
		//		System.out.println("right join: " + testTriggerRightJoin);
		//		System.out.println("outer join: " + testTriggerOuterJoin);
		//		System.out.println("left only join: " + testTriggerLeftOnlyJoin);
		//		System.out.println("right only join: " + testTriggerRightOnlyJoin);
		//		System.out.println("outer only join: " + testTriggerOuterOnlyJoin);
		//		System.out.println("agg cascaded: " + testTriggerAggCascaded);
		//		System.out.println("agg one group: " + testTriggerAggregateOneGroup);
		//		System.out.println("agg multi: " + testTriggerAggMulti);
		//		System.out.println("proj single target multi trigger: " + testTriggerProjMultiTriggerTarget);
		//		System.out.println(testInsertIntoTargetProjection());
		System.out.println("$$$$$$$$$$$$$$$$");
		//		System.out.println("join cascaded: " + testTriggerJoinCascadedGeneric);
		//		System.out.println("join cascaded all: " + testTriggerJoinCascadedAll);
		//
		//		System.out.println("agg arithmetic: " + testTriggerAggregateArithmetic);
		//		System.out.println("proj casc: " + testTriggerProjectionCascaded);
		//		System.out.println("all: " + testTriggerAggregateAllFunctions); ///
		//		System.out.println("group-by reference: " + testGroupByReference);
		//		System.out.println("NoNull insert: " + testNoNullInsert);
		//		System.out.println("NoNull update: " + testNoNullUpdate);

		//////////////////////////////

		int numAggRows = 100;
		int numTests = 20;
		//		boolean testTriggerAggregateSingleFuncSum = testTriggerAggregateSingleFunc(AGG_FUNC_SUM, numAggRows, 1);
		//		boolean testTriggerAggregateSingleFuncMin = testTriggerAggregateSingleFunc(AGG_FUNC_MIN, numAggRows, 1);
		//		boolean testTriggerAggregateSingleFuncMax = testTriggerAggregateSingleFunc(AGG_FUNC_MAX, numAggRows, 1);
		//		boolean testTriggerAggregateSingleFuncCount = testTriggerAggregateSingleFunc(AGG_FUNC_COUNT, numAggRows, 1);
		//		boolean testTriggerAggregateSingleFuncCountUnique = testTriggerAggregateSingleFunc(AGG_FUNC_COUNT_UNIQUE, numAggRows, 1);
		//		boolean testTriggerAggregateSingleFuncCat = testTriggerAggregateSingleFunc(AGG_FUNC_CAT, numAggRows, 1);
		//		boolean testTriggerAggregateSingleFuncAvg = testTriggerAggregateSingleFunc(AGG_FUNC_AVG, numAggRows, 1);
		//		boolean testTriggerAggregateSingleFuncVar = testTriggerAggregateSingleFunc(AGG_FUNC_VAR, numAggRows, 1);
		//		boolean testTriggerAggregateSingleFuncVarS = testTriggerAggregateSingleFunc(AGG_FUNC_VAR_S, numAggRows, 1);
		//		boolean testTriggerAggregateSingleFuncStdev = testTriggerAggregateSingleFunc(AGG_FUNC_STDEV, numAggRows, 1);
		//		boolean testTriggerAggregateSingleFuncStdevS = testTriggerAggregateSingleFunc(AGG_FUNC_STDEV_S, numAggRows, 1);
		//		boolean testTriggerAggregateSingleFuncFirst = testTriggerAggregateSingleFunc(AGG_FUNC_FIRST, numAggRows, 1);
		//		boolean testTriggerAggregateSingleFuncLast = testTriggerAggregateSingleFunc(AGG_FUNC_LAST, numAggRows, 1);
		//		boolean testTriggerAggregateSingleFuncCovar = testTriggerAggregateSingleFunc(AGG_FUNC_COVAR, numAggRows, 1);
		//		boolean testTriggerAggregateSingleFuncCovarS = testTriggerAggregateSingleFunc(AGG_FUNC_COVAR_S, numAggRows, 1);
		//		boolean testTriggerAggregateSingleFuncCor = testTriggerAggregateSingleFunc(AGG_FUNC_COR, 15, 1);
		//		boolean testTriggerAggregateSingleFuncBeta = testTriggerAggregateSingleFunc(AGG_FUNC_BETA, numAggRows, 1);

		//		boolean[] sumResults = runMultipleAggTests(numAggRows, numTests, AGG_FUNC_SUM);
		//		boolean[] minResults = runMultipleAggTests(numAggRows, numTests, AGG_FUNC_MIN);
		//		boolean[] maxResults = runMultipleAggTests(numAggRows, numTests, AGG_FUNC_MAX);
		//		boolean[] countResults = runMultipleAggTests(numAggRows, numTests, AGG_FUNC_COUNT);
		//		boolean[] countUniqueResults = runMultipleAggTests(numAggRows, numTests, AGG_FUNC_COUNT_UNIQUE);
		//		boolean[] catResults = runMultipleAggTests(numAggRows, numTests, AGG_FUNC_CAT);
		//		boolean[] avgResults = runMultipleAggTests(numAggRows, numTests, AGG_FUNC_AVG);
		//		boolean[] varResults = runMultipleAggTests(numAggRows, numTests, AGG_FUNC_VAR);
		//		boolean[] varSResults = runMultipleAggTests(numAggRows, numTests, AGG_FUNC_VAR_S);
		//		boolean[] stdevResults = runMultipleAggTests(numAggRows, numTests, AGG_FUNC_STDEV);
		//		boolean[] stdevSResults = runMultipleAggTests(numAggRows, numTests, AGG_FUNC_STDEV_S);
		//		boolean[] firstResults = runMultipleAggTests(numAggRows, numTests, AGG_FUNC_FIRST);
		//		boolean[] lastResults = runMultipleAggTests(numAggRows, numTests, AGG_FUNC_LAST);
		//		boolean[] covarResults = runMultipleAggTests(numAggRows, numTests, AGG_FUNC_COVAR);
		//		boolean[] covarSResults = runMultipleAggTests(numAggRows, numTests, AGG_FUNC_COVAR_S);
		//		boolean[] corResults = runMultipleAggTests(numAggRows, numTests, AGG_FUNC_COR);
		//		boolean[] betaResults = runMultipleAggTests(numAggRows, numTests, AGG_FUNC_BETA);

		//		System.out.println("sumResults         : " + allTrue(sumResults) + " : " + Arrays.toString(sumResults));
		//		System.out.println("minResults         : " + allTrue(minResults) + " : " + Arrays.toString(minResults));
		//		System.out.println("maxResults         : " + allTrue(maxResults) + " : " + Arrays.toString(maxResults));
		//		System.out.println("countResults       : " + allTrue(countResults) + " : " + Arrays.toString(countResults));
		//		System.out.println("countUniqueResults : " + allTrue(countUniqueResults) + " : " + Arrays.toString(countUniqueResults));
		//		System.out.println("catResults         : " + allTrue(catResults) + " : " + Arrays.toString(catResults));
		//		System.out.println("avgResults         : " + allTrue(avgResults) + " : " + Arrays.toString(avgResults));
		//		System.out.println("varResults         : " + allTrue(varResults) + " : " + Arrays.toString(varResults));
		//		System.out.println("varSResults        : " + allTrue(varSResults) + " : " + Arrays.toString(varSResults));
		//		System.out.println("stdevResults       : " + allTrue(stdevResults) + " : " + Arrays.toString(stdevResults));
		//		System.out.println("stdevSResults      : " + allTrue(stdevSResults) + " : " + Arrays.toString(stdevSResults));
		//		System.out.println("firstResults       : " + allTrue(firstResults) + " : " + Arrays.toString(firstResults));
		//		System.out.println("lastResults        : " + allTrue(lastResults) + " : " + Arrays.toString(lastResults));
		//		System.out.println("covarResults       : " + allTrue(covarResults) + " : " + Arrays.toString(covarResults));
		//		System.out.println("covarSResults      : " + allTrue(covarSResults) + " : " + Arrays.toString(covarSResults));
		//		System.out.println("corResults         : " + allTrue(corResults) + " : " + Arrays.toString(corResults));
		//		System.out.println("betaResults        : " + allTrue(betaResults) + " : " + Arrays.toString(betaResults));
		System.out.println("???????????????????????");
		//		System.out.println("sum : " + testTriggerAggregateSingleFuncSum);
		//		System.out.println("min : " + testTriggerAggregateSingleFuncMin);
		//		System.out.println("max : " + testTriggerAggregateSingleFuncMax);
		//		System.out.println("count : " + testTriggerAggregateSingleFuncCount);
		//		System.out.println("countUnique : " + testTriggerAggregateSingleFuncCountUnique);
		//		System.out.println("cat : " + testTriggerAggregateSingleFuncCat);
		//		System.out.println("avg : " + testTriggerAggregateSingleFuncAvg);
		//		System.out.println("var : " + testTriggerAggregateSingleFuncVar);
		//		System.out.println("varS : " + testTriggerAggregateSingleFuncVarS);
		//		System.out.println("stdev : " + testTriggerAggregateSingleFuncStdev);
		//		System.out.println("stdevS : " + testTriggerAggregateSingleFuncStdevS);
		//		System.out.println("first : " + testTriggerAggregateSingleFuncFirst);
		//		System.out.println("last : " + testTriggerAggregateSingleFuncLast);
		//		System.out.println("covar : " + testTriggerAggregateSingleFuncCovar);
		//		System.out.println("covarS : " + testTriggerAggregateSingleFuncCovarS);
		//		System.out.println("cor : " + testTriggerAggregateSingleFuncCor);
		//		System.out.println("beta : " + testTriggerAggregateSingleFuncBeta);

		//		Table test = new BasicTable(Double.class, "x");
		//		test.setTitle("test");
		//		test.getRows().addRow(Double.NaN);
		//		test.getRows().addRow((Double) null);
		//		System.out.println(test);

		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^");
		//		System.out.println("unique index: " + testIndexUnique());
		//		System.out.println("primary index: " + testIndexPrimary());
		boolean testTriggerJoineOneTablePrimaryLeftJoinLeftPrimary = testTriggerJoineOneTablePrimaryLeftJoin(true);
		boolean testTriggerJoineOneTablePrimaryRightJoinLeftPrimary = testTriggerJoineOneTablePrimaryRightJoin(true);
		boolean testTriggerJoineOneTablePrimaryOuterJoinLeftPrimary = testTriggerJoineOneTablePrimaryOuterJoin(true);
		boolean testTriggerJoineOneTablePrimaryLeftOnlyJoinLeftPrimary = testTriggerJoineOneTablePrimaryLeftOnlyJoin(true);
		boolean testTriggerJoineOneTablePrimaryRightOnlyJoinLeftPrimary = testTriggerJoineOneTablePrimaryRightOnlyJoin(true);
		boolean testTriggerJoineOneTablePrimaryOuterOnlyJoinLeftPrimary = testTriggerJoineOneTablePrimaryOuterOnlyJoin(true);
		boolean testTriggerJoineOneTablePrimaryInnerJoinLeftPrimary = testTriggerJoineOneTablePrimaryInnerJoin(true);
		boolean testTriggerJoineOneTablePrimaryLeftJoinRightPrimary = testTriggerJoineOneTablePrimaryLeftJoin(false);
		boolean testTriggerJoineOneTablePrimaryRightJoinRightPrimary = testTriggerJoineOneTablePrimaryRightJoin(false);
		boolean testTriggerJoineOneTablePrimaryOuterJoinRightPrimary = testTriggerJoineOneTablePrimaryOuterJoin(false);
		boolean testTriggerJoineOneTablePrimaryLeftOnlyJoinRightPrimary = testTriggerJoineOneTablePrimaryLeftOnlyJoin(false);
		boolean testTriggerJoineOneTablePrimaryRightOnlyJoinRightPrimary = testTriggerJoineOneTablePrimaryRightOnlyJoin(false);
		boolean testTriggerJoineOneTablePrimaryOuterOnlyJoinRightPrimary = testTriggerJoineOneTablePrimaryOuterOnlyJoin(false);
		boolean testTriggerJoineOneTablePrimaryInnerJoinRightPrimary = testTriggerJoineOneTablePrimaryInnerJoin(false);
		System.out.println("left join primary key (left primary): " + testTriggerJoineOneTablePrimaryLeftJoinLeftPrimary);
		System.out.println("left only join primary key (left primary): " + testTriggerJoineOneTablePrimaryLeftOnlyJoinLeftPrimary);
		System.out.println("right join primary key (left primary): " + testTriggerJoineOneTablePrimaryRightJoinLeftPrimary);
		System.out.println("right only join primary key (left primary): " + testTriggerJoineOneTablePrimaryRightOnlyJoinLeftPrimary);
		System.out.println("outer join primary key (left primary): " + testTriggerJoineOneTablePrimaryOuterJoinLeftPrimary);
		System.out.println("outer only join primary key (left primary): " + testTriggerJoineOneTablePrimaryOuterOnlyJoinLeftPrimary);
		System.out.println("inner join primary key (left primary): " + testTriggerJoineOneTablePrimaryInnerJoinLeftPrimary);
		System.out.println("left join primary key (right primary): " + testTriggerJoineOneTablePrimaryLeftJoinRightPrimary);
		System.out.println("left only join primary key (right primary): " + testTriggerJoineOneTablePrimaryLeftOnlyJoinRightPrimary);
		System.out.println("right join primary key (right primary): " + testTriggerJoineOneTablePrimaryRightJoinRightPrimary);
		System.out.println("right only join primary key (right primary): " + testTriggerJoineOneTablePrimaryRightOnlyJoinRightPrimary);
		System.out.println("outer join primary key (right primary): " + testTriggerJoineOneTablePrimaryOuterJoinRightPrimary);
		System.out.println("outer only join primary key (right primary): " + testTriggerJoineOneTablePrimaryOuterOnlyJoinRightPrimary);
		System.out.println("inner join primary key (right primary): " + testTriggerJoineOneTablePrimaryInnerJoinRightPrimary);
	}
	private static boolean allTrue(boolean[] x) {
		for (int i = 0; i < x.length; i++)
			if (!x[i])
				return false;
		return true;
	}
	private static boolean[] runMultipleAggTests(int numAggRows, int numTests, String aggFunc) throws ClassNotFoundException, SQLException {
		int n;
		int[] rowCounts = new int[numTests];
		boolean[] results = new boolean[numTests];
		for (int i = 0; i < numTests; i++) {
			n = (int) ((((double) i) / numTests) * numAggRows);
			rowCounts[i] = n;
			results[i] = testTriggerAggregateSingleFunc(aggFunc, n, 1);
		}
		//		System.out.println("rowCounts : " + Arrays.toString(rowCounts));
		//		System.out.println("results : " + Arrays.toString(results));
		return results;
	}
	private static Table getTables() throws ClassNotFoundException, SQLException {
		return getObjects(TYPE_TABLE);
	}
	private static Table getTriggers() throws ClassNotFoundException, SQLException {
		return getObjects(TYPE_TRIGGER);
	}
	private static Table getObjects(String type) throws ClassNotFoundException, SQLException {
		return resultSetToTable(executeQuery("select * from " + type));
	}
	private static Table resultSetToTable(ResultSet rs) throws SQLException, ClassNotFoundException {
		final ResultSetMetaData metaData = rs.getMetaData();
		final int columnCount = metaData.getColumnCount();
		BasicTable basic = new BasicTable();
		basic.setTitle(metaData.getTableName(1));
		for (int i = 1; i <= columnCount; i++) {
			basic.addColumn(Class.forName(metaData.getColumnClassName(i)), metaData.getColumnName(i));
		}
		Row row;
		while (rs.next()) {
			row = basic.newEmptyRow();
			for (int i = 1; i <= columnCount; i++) {
				row.putAt(i - 1, rs.getObject(i));
			}
			basic.getRows().add(row);
		}
		return basic;
	}
	private static void createInputTables(Table... tables) throws SQLException {
		StringBuilder query = new StringBuilder();
		Table t;
		int numTables = tables.length;
		Column col;
		TableList rows;
		String title;
		Row row;
		int numCols;
		for (int i = 0; i < numTables; i++) {
			t = tables[i];
			title = t.getTitle();
			query.append("create public table " + title + " (");
			numCols = t.getColumnsCount();
			for (int j = 0; j < numCols; j++) {
				col = t.getColumnAt(j);
				query.append(col.getId() + " " + col.getType().getSimpleName());
				if (j < numCols - 1) {
					query.append(", ");
				}
			}
			query.append(");" + SH.NEWLINE);
			rows = t.getRows();
			for (int r = 0; r < rows.size(); r++) {
				query.append("insert into " + title + " values (");
				row = t.getRows().get(r);
				for (int c = 0; c < numCols; c++) {
					query.append(row.getAt(c));
					if (c < numCols - 1) {
						query.append(", ");
					}
				}
				query.append(");" + SH.NEWLINE);
			}
		}
		//		System.out.println(query);
		executeQuery(query.toString());
	}
	private static void dropTables(List<String> tables) throws SQLException {
		dropObjects(tables, TYPE_TABLE);
	}
	private static void dropTriggers(List<String> triggers) throws SQLException {
		dropObjects(triggers, TYPE_TRIGGER);
	}
	private static void dropObjects(List<String> objects, String type) throws SQLException {
		StringBuilder query = new StringBuilder();
		String keyword = TYPE_2_KEYWORD.get(type);
		for (int i = 0; i < objects.size(); i++) {
			query.append("drop " + keyword + " " + objects.get(i) + ";" + SH.NEWLINE);
		}
		executeQuery(query.toString());
	}
	private static void dropAddedTables(Table beforeTable, Table afterTable) throws ClassNotFoundException, SQLException {
		dropTables(getNewTableNames(beforeTable, afterTable));
	}
	private static void dropAddedTriggers(Table beforeTable, Table afterTable) throws ClassNotFoundException, SQLException {
		dropTriggers(getNewTriggerNames(beforeTable, afterTable));
	}
	private static Table getTable(String title) throws ClassNotFoundException, SQLException {
		return resultSetToTable(executeQuery("select * from " + title + ";"));
	}
	private static List<String> getNewTableNames(Table beforeTable, Table afterTable) throws ClassNotFoundException, SQLException {
		return getNewObjectNames(beforeTable, afterTable, TYPE_TABLE);
	}
	private static List<String> getNewTriggerNames(Table beforeTable, Table afterTable) throws ClassNotFoundException, SQLException {
		return getNewObjectNames(beforeTable, afterTable, TYPE_TRIGGER);
	}
	private static List<String> getNewObjectNames(Table beforeTable, Table afterTable, String objectType) throws ClassNotFoundException, SQLException {
		List<String> newTableNames = new ArrayList<String>();
		int sizeBefore = beforeTable.getSize();
		int sizeAfter = afterTable.getSize();
		if (sizeAfter <= sizeBefore) {
			return newTableNames;
		}
		TableList beforeRows = beforeTable.getRows();
		TableList afterRows = afterTable.getRows();
		String beforeName;
		String afterName;
		boolean isInBoth = false;
		String colHeader = TYPE_2_COL_HEADER.get(objectType);
		for (int a = 0; a < sizeAfter; a++) {
			afterName = (String) afterRows.get(a).get(colHeader);
			beforeLoop: for (int b = 0; b < sizeBefore; b++) {
				beforeName = (String) beforeRows.get(b).get(colHeader);
				if (beforeName.equals(afterName)) {
					isInBoth = true;
					break beforeLoop;
				}
			}
			if (!isInBoth) {
				newTableNames.add(afterName);
			}
			isInBoth = false;
		}
		return newTableNames;
	}
	private static void testQuery(String testName, String query, Table... inputTables) throws IOException, SQLException, ClassNotFoundException {
		testQuery(root, testName, query, inputTables);
	}
	private static void testQuery(File outputDir, String testName, String query, Table... inputTables) throws IOException, SQLException, ClassNotFoundException {
		Table beforeTables = getTables();
		createInputTables(inputTables);
		StringBuilder sb = new StringBuilder();
		sb.append("****TEST_INPUT_QUERY****").append(SH.NEWLINE);
		sb.append(query).append(SH.NEWLINE);
		sb.append("****TEST_INPUT_TABLES****").append(SH.NEWLINE);
		for (int i = 0; i < inputTables.length; i++) {
			sb.append(inputTables[i]).append(SH.NEWLINE);
		}
		sb.append("****TEST_RESULT****").append(SH.NEWLINE);
		final ResultSet rs = executeQuery(query);
		sb.append(resultSetToTable(rs).toString()).append(SH.NEWLINE);
		sb.append("****TEST_RESULT_TABLES****").append(SH.NEWLINE);
		Table afterTables = getTables();
		List<String> addedTables = getNewTableNames(beforeTables, afterTables);
		for (int i = 0; i < addedTables.size(); i++) {
			sb.append(getTable(addedTables.get(i))).append(SH.NEWLINE);
		}
		dropTables(addedTables);

		// Compare files
		String existing = "";
		String nuw = sb.toString();
		Package pck = TestRealtimeJoins.class.getPackage();
		try {
			existing = IOH.readText(pck, testName + ".sqltest");
		} catch (Exception e) {
			IOH.ensureDir(outputDir);
			File file = new File(outputDir, testName + ".sqltest");
			IOH.writeText(file, nuw);
			System.out.println(testName + " WROTE FILE");
			writtenFiles.add(testName);
			//			throw new RuntimeException("wrote file " + IOH.getFullPath(file));
		}
		//		Assert.assertEquals(existing, nuw);
		if (!SH.equals(existing, nuw)) {
			System.out.println("=== FAILURE: " + testName + " ===");
		}
	}

	private static ResultSet executeQueryLineByLine(String query) throws SQLException, ClassNotFoundException {
		return executeQueryLineByLine(query, false, false, null, null, null);
	}
	private static ResultSet executeQueryLineByLine(String query, boolean showLines, boolean showTables, List<String> sourceNames, List<String> targetNames,
			List<String> compareQueries) throws SQLException, ClassNotFoundException {
		int numSources = sourceNames == null ? 0 : sourceNames.size();
		int numTargets = targetNames == null ? 0 : targetNames.size();
		int numCompares = compareQueries == null ? 0 : compareQueries.size();
		boolean seenBadComparison = false;
		if (numTargets != numCompares) {
			throw new IllegalArgumentException("Number of target table names must match number of comparison queries");
		}
		String[] lines = SH.split(SH.NEWLINE, query);
		ResultSet rs = null;
		for (int i = 0; i < lines.length; i++) {
			if (showLines)
				System.out.println(lines[i]);
			rs = executeQuery(lines[i]);
			if (showTables) {
				for (int s = 0; s < numSources; s++) {
					System.out.println(getTable(sourceNames.get(s)));
				}
			}
			targetCompareLoop: for (int t = 0; t < numTargets; t++) {
				if (showTables) {
					System.out.println(getTable(targetNames.get(t)));
				}
				if (seenBadComparison) {
					continue targetCompareLoop;
				}
				//			}
				//			for (int c = 0; c < numCompares; c++) {
				String createClause = compareQueries.get(t);
				String name = getTableNameFromCreateClause(createClause);
				// Create comparison table
				executeQuery(createClause);
				if (!checkTablesSame(targetNames.get(t), name)) {
					System.out.println(" === TABLES DIFFER === ");
					System.out.println("Executed queries: ");
					for (int j = 0; j <= i; j++) {
						System.out.println(lines[j]);
					}
					System.out.println("Line number: " + i);
					for (int s = 0; s < numSources; s++) {
						System.out.println(getTable(sourceNames.get(s)));
					}
					System.out.println(getTable(targetNames.get(t)));
					System.out.println(getTable(name));
					System.out.println("Comparison query: " + compareQueries.get(t));
					System.out.println(" ===================== ");
					seenBadComparison = true;
				}
				dropTables(CH.l(name));
			}
		}
		return rs;
	}

	private static ResultSet executeQuery(String query) throws SQLException {
		ResultSet rs = null;
		try {
			rs = TestConnection.getConnection().createStatement().executeQuery(query);
		} catch (SQLException e) {
			System.out.println(" === SQL EXCEPTION === ");
			System.out.println(e);
			e.printStackTrace();
			System.out.println("BEGIN QUERY");
			System.out.println(query);
			System.out.println("END QUERY");
		}
		return rs;
	}

	//	private static boolean testTriggerBasicOnInsertingScript() throws ClassNotFoundException, SQLException {
	//		String sourceQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_SOURCE + " (x double);";
	//		String targetQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_TARGET + " (y double);";
	//		String triggerQuery = "create trigger test_agg oftype aggregate on " + DEFAULT_NAME_SOURCE + ", " + DEFAULT_NAME_TARGET
	//				+ " use groupBys=\"g=g\" selects=\"cnt=count(1), sum=sum(x)\";";
	//		String comparisonQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_COMPARISON + " as select g, count(1) as cnt, sum(x) as sum from " + DEFAULT_NAME_SOURCE + " group by g;";
	//		return testTrigger(1000, CH.l(sourceQuery), CH.l(targetQuery), CH.l(triggerQuery), CH.l(comparisonQuery));
	//	}

	// AGGREGATE // 

	private static boolean testTriggerAggregate() throws ClassNotFoundException, SQLException {
		//		String sourceQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_SOURCE + " (g int, x double, y double);";
		//		String targetQuery = CREATE_PUBLIC_TABLE_PREFIX
		//				+ DEFAULT_NAME_TARGET
		//				+ " (g int, cnt int, sum double, min double, max double, countUnique int, cat String, avg double, var double, varS double, stdev double, stdevS double, first double, last double, covar double, covarS double, cor double, beta double);";
		//		String triggerQuery = "create trigger test_trig oftype aggregate on "
		//				+ DEFAULT_NAME_SOURCE
		//				+ ", "
		//				+ DEFAULT_NAME_TARGET
		//				+ " use groupBys=\"g=g\" selects=\"cnt=count(1), sum=sum(x), min=min(x), max=max(x), countUnique=countUnique(x), cat=cat(x, \\\"&\\\", 3), avg=avg(x), var=var(x), varS=varS(x), stdev=stdev(x), stdevS=stdevS(x), first=first(x), last=last(x), covar=covar(x, y), covarS=covarS(x, y), cor=cor(x, y), beta=beta(x, y)\";";
		//		String comparisonQuery = CREATE_PUBLIC_TABLE_PREFIX
		//				+ DEFAULT_NAME_COMPARISON
		//				+ " as select g, count(1) as cnt, sum(x) as sum, min(x) as min, max(x) as max, countUnique(x) as countUnique, cat(x, \"&\", 3) as cat, avg(x) as avg, var(x) as var, varS(x) as varS, stdev(x) as stdev, stdevS(x) as stdevS, first(x) as first, last(x) as last, covar(x, y) as covar, covarS(x, y) as covarS, cor(x, y) as cor, beta(x, y) as beta from "
		//				+ DEFAULT_NAME_SOURCE + " group by g;";
		//		return testTrigger(1000, CH.l(sourceQuery), CH.l(targetQuery), CH.l(triggerQuery), CH.l(comparisonQuery));
		String sourceQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_SOURCE + " (g int, x double, y double);";
		String targetQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_TARGET
				+ " (g int, cnt int, sum double, min double, max double, countUnique int, avg double, var double, stdev double, covar double, covarS double, cor double, beta double);";
		String triggerQuery = "create trigger test_trig oftype aggregate on " + DEFAULT_NAME_SOURCE + ", " + DEFAULT_NAME_TARGET
				+ " use groupBys=\"g=g\" selects=\"cnt=count(1), sum=sum(x), min=min(x), max=max(x), countUnique=countUnique(x), avg=avg(x), var=var(x), stdev=stdev(x), covar=covar(x, y), covarS=covarS(x, y), cor=cor(x, y), beta=beta(x, y)\";";
		String comparisonQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_COMPARISON
				+ " as select g, count(1) as cnt, sum(x) as sum, min(x) as min, max(x) as max, countUnique(x) as countUnique, avg(x) as avg, var(x) as var, stdev(x) as stdev, covar(x, y) as covar, covarS(x, y) as covarS, cor(x, y) as cor, beta(x, y) as beta from "
				+ DEFAULT_NAME_SOURCE + " group by g;";
		return testTrigger(1000, CH.l(sourceQuery), CH.l(targetQuery), CH.l(triggerQuery), CH.l(comparisonQuery));
	}
	private static boolean testTriggerAggregateAllFunctions() throws ClassNotFoundException, SQLException {
		Map<String, Boolean> results = new HashMap<String, Boolean>();
		boolean r;
		boolean final_result = true;
		for (String f : FUNCTIONS_2_NUM_ARGS.keySet()) {
			r = testTriggerAggregateSingleFunc(f);
			results.put(f, r);
			final_result &= r;
		}
		for (String f : results.keySet()) {
			System.out.println(f + " : " + results.get(f));
		}
		return final_result;
	}
	private static boolean testTriggerAggregateSingleFunc(String function) throws ClassNotFoundException, SQLException {
		return testTriggerAggregateSingleFunc(function, 1000, NUMBER_OF_GROUPS);
	}
	private static boolean testTriggerAggregateSingleFunc(String function, int numOperations, int numGroups) throws ClassNotFoundException, SQLException {
		StringBuilder sourceQuery = new StringBuilder();
		sourceQuery.append(CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_SOURCE + " (g int, ");
		String targetQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_TARGET + " (g int, " + function + " " + FUNCTIONS_2_RETURN_TYPE.get(function) + ");";
		String[] funcArgs = new String[FUNCTIONS_2_NUM_ARGS.get(function)];
		String col;
		for (int i = 0; i < funcArgs.length; i++) {
			col = "x" + i;
			funcArgs[i] = col;
			sourceQuery.append(col + " double");
			if (i < funcArgs.length - 1) {
				sourceQuery.append(", ");
			} else {
				sourceQuery.append(");");
			}
		}
		String triggerQuery = "create trigger test_trig oftype aggregate on " + DEFAULT_NAME_SOURCE + ", " + DEFAULT_NAME_TARGET + " use groupBys=\"g=g\" selects=\"" + function
				+ "=" + generateAggFunctionString(function, true, funcArgs) + "\";";
		String comparisonQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_COMPARISON + " as select g, " + generateAggFunctionString(function, false, funcArgs) + " as " + function
				+ " from " + DEFAULT_NAME_SOURCE + " group by g;";
		//		System.out.println(sourceQuery);
		//		System.out.println(targetQuery);
		//		System.out.println(triggerQuery);
		//		System.out.println(comparisonQuery);
		return testTrigger(numOperations, CH.l(sourceQuery.toString()), CH.l(targetQuery), CH.l(triggerQuery), CH.l(comparisonQuery), numGroups, false, true);
	}
	private static boolean testTriggerAggregateArithmetic() throws ClassNotFoundException, SQLException {
		String sourceQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_SOURCE + " (x double);";
		String targetQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_TARGET + " (dummy boolean, a double, m double);";
		String triggerQuery = "create trigger test_agg_arith oftype aggregate on " + DEFAULT_NAME_SOURCE + ", " + DEFAULT_NAME_TARGET
				+ " use groupBys=\"dummy=true\" selects=\"a=count(1) + sum(x), m=count(1) * sum(x)\";";
		String comparisonQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_COMPARISON + " as select true as dummy, count(1) + sum(x) as a, count(1) * sum(x) as m from "
				+ DEFAULT_NAME_SOURCE + ";";
		return testTrigger(1000, CH.l(sourceQuery), CH.l(targetQuery), CH.l(triggerQuery), CH.l(comparisonQuery));
	}
	private static boolean testTriggerAggregateOneGroup() throws ClassNotFoundException, SQLException {
		String sourceQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_SOURCE + " (g int, x double);";
		String targetQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_TARGET + " (dummy boolean, z1 int, z2 double);";
		String triggerQuery = "create trigger test_agg_one_group oftype aggregate on " + DEFAULT_NAME_SOURCE + ", " + DEFAULT_NAME_TARGET
				+ " use groupBys=\"dummy=true\" selects=\"z1=count(1), z2=sum(x)\";";
		String comparisonQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_COMPARISON + " as select true as dummy, count(1) as z1, sum(x) as z2 from " + DEFAULT_NAME_SOURCE + ";";
		return testTrigger(1000, CH.l(sourceQuery), CH.l(targetQuery), CH.l(triggerQuery), CH.l(comparisonQuery));
	}
	private static boolean testTriggerAggregateCascaded() throws ClassNotFoundException, SQLException {
		String sourceQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_SOURCE + " (g int, x double);";
		String targetQuery1 = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_TARGET + "1 (g int, y1 int, y2 double);";
		String targetQuery2 = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_TARGET + "2 (dummy boolean, z1 int, z2 double);";
		String triggerQuery1 = "create trigger test_agg_casc1 oftype aggregate on " + DEFAULT_NAME_SOURCE + ", " + DEFAULT_NAME_TARGET
				+ "1 use groupBys=\"g=g\" selects=\"y1=count(1), y2=sum(x)\";";
		String triggerQuery2 = "create trigger test_agg_casc2 oftype aggregate on " + DEFAULT_NAME_TARGET + "1, " + DEFAULT_NAME_TARGET
				+ "2 use groupBys=\"dummy=true\" selects=\"z1=count(1), z2=sum(y2)\";";
		String comparisonQuery1 = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_COMPARISON + "1 as select g, count(1) as y1, sum(x) as y2 from " + DEFAULT_NAME_SOURCE + " group by g;";
		String comparisonQuery2 = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_COMPARISON + "2 as select true as dummy, count(1) as z1, sum(y2) as z2 from " + DEFAULT_NAME_COMPARISON
				+ "1;";
		return testTrigger(1000, CH.l(sourceQuery), CH.l(targetQuery1, targetQuery2), CH.l(triggerQuery1, triggerQuery2), CH.l(comparisonQuery1, comparisonQuery2));
	}
	private static boolean testTriggerAggregateMultipleTriggersSameSource() throws ClassNotFoundException, SQLException {
		String sourceQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_SOURCE + " (g int, x double);";
		String targetQuery1 = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_TARGET + "1 (g int, y double);";
		String targetQuery2 = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_TARGET + "2 (g int, y double);";
		String triggerQuery1 = "create trigger test_agg1 oftype aggregate on " + DEFAULT_NAME_SOURCE + ", " + DEFAULT_NAME_TARGET
				+ "1 use groupBys=\"g=g\" selects=\"y=count(1)\";";
		String triggerQuery2 = "create trigger test_agg2 oftype aggregate on " + DEFAULT_NAME_SOURCE + ", " + DEFAULT_NAME_TARGET + "2 use groupBys=\"g=g\" selects=\"y=sum(x)\";";
		String comparisonQuery1 = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_COMPARISON + "1 as select g, count(1) as y from " + DEFAULT_NAME_SOURCE + " group by g;";
		String comparisonQuery2 = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_COMPARISON + "2 as select g, sum(x) as y from " + DEFAULT_NAME_SOURCE + " group by g;";
		return testTrigger(1000, CH.l(sourceQuery), CH.l(targetQuery1, targetQuery2), CH.l(triggerQuery1, triggerQuery2), CH.l(comparisonQuery1, comparisonQuery2));
	}
	private static boolean testTriggerGroupByReference() throws ClassNotFoundException, SQLException {
		String sourceQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_SOURCE + " (g int, x double);";
		String targetQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_TARGET + " (g int, sum double);";
		String triggerQuery = "create trigger test_trig oftype aggregate on " + DEFAULT_NAME_SOURCE + ", " + DEFAULT_NAME_TARGET + " use groupBys=\"g=g\" selects=\"sum=sum(x)\";";
		String comparisonQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_COMPARISON + " as select g, sum(x) as sum from " + DEFAULT_NAME_SOURCE + " group by g;";
		return testTrigger(1000, CH.l(sourceQuery), CH.l(targetQuery), CH.l(triggerQuery), CH.l(comparisonQuery), NUMBER_OF_GROUPS, false, true);
		//		return testTrigger(1000, CH.l(sourceQuery), CH.l(targetQuery), CH.l(triggerQuery), CH.l(comparisonQuery));
	}

	// JOIN // 

	private static boolean testTriggerJoin() throws ClassNotFoundException, SQLException {
		return testTriggerJoinGeneric(JOIN_TYPE_INNER);
	}
	private static boolean testTriggerOuterJoin() throws ClassNotFoundException, SQLException {
		return testTriggerJoinGeneric(JOIN_TYPE_OUTER);
	}
	private static boolean testTriggerLeftOnlyJoin() throws ClassNotFoundException, SQLException {
		return testTriggerJoinGeneric(JOIN_TYPE_LEFT_ONLY);
	}
	private static boolean testTriggerRightOnlyJoin() throws ClassNotFoundException, SQLException {
		return testTriggerJoinGeneric(JOIN_TYPE_RIGHT_ONLY);
	}
	private static boolean testTriggerOuterOnlyJoin() throws ClassNotFoundException, SQLException {
		return testTriggerJoinGeneric(JOIN_TYPE_OUTER_ONLY);
	}
	private static boolean testTriggerRightJoin() throws ClassNotFoundException, SQLException {
		return testTriggerJoinGeneric(JOIN_TYPE_RIGHT);
	}
	private static boolean testTriggerLeftJoin() throws ClassNotFoundException, SQLException {
		return testTriggerJoinGeneric(JOIN_TYPE_LEFT);
	}
	private static boolean testTriggerJoinGeneric(String joinType) throws ClassNotFoundException, SQLException {
		String leftName = "sourceLeft";
		String rightName = "sourceRight";
		String sourceQueryLeft = CREATE_PUBLIC_TABLE_PREFIX + leftName + " (x int, y int);";
		String sourceQueryRight = CREATE_PUBLIC_TABLE_PREFIX + rightName + " (x int, y int);";
		String targetQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_TARGET + " (lx int, ly int, rx int, ry int);";
		String triggerQuery = "create trigger test_left_join oftype join on " + leftName + ", " + rightName + ", " + DEFAULT_NAME_TARGET + " use type=\"" + joinType + "\" on=\""
				+ leftName + ".x==" + rightName + ".x\" selects=\"lx=" + leftName + ".x, ly=" + leftName + ".y, rx=" + rightName + ".x, ry=" + rightName + ".y\";";
		String comparisonQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_COMPARISON + " as select " + leftName + ".x as lx, " + leftName + ".y as ly, " + rightName + ".x as rx, "
				+ rightName + ".y as ry from " + leftName + " " + getStaticJoinKeyword(joinType) + " join " + rightName + " on " + leftName + ".x == " + rightName + ".x;";
		return testTrigger(1000, CH.l(sourceQueryLeft, sourceQueryRight), CH.l(targetQuery), CH.l(triggerQuery), CH.l(comparisonQuery));
	}
	private static String getStaticJoinKeyword(String joinType) {
		return JOIN_TYPE_INNER.equalsIgnoreCase(joinType) ? "" : joinType;
	}
	private static boolean testTriggerJoinCascadedAll() throws ClassNotFoundException, SQLException {
		List<Tuple3<String, String, String>> failures = new ArrayList<Tuple3<String, String, String>>();
		List<Tuple3<String, String, String>> successes = new ArrayList<Tuple3<String, String, String>>();
		int numTypes = JOIN_TYPES.size();
		String typeSrc1, typeSrc2, typeCascade;
		int numFailures = 0;
		for (int i = 0; i < numTypes; i++) {
			typeSrc1 = JOIN_TYPES.get(i);
			for (int j = 0; j < numTypes; j++) {
				typeSrc2 = JOIN_TYPES.get(j);
				for (int k = 0; k < numTypes; k++) {
					typeCascade = JOIN_TYPES.get(k);
					if (!testTriggerJoinCascadedGeneric(typeSrc1, typeSrc2, typeCascade)) {
						failures.add(new Tuple3<String, String, String>(typeSrc1, typeSrc2, typeCascade));
						numFailures++;
					} else {
						successes.add(new Tuple3<String, String, String>(typeSrc1, typeSrc2, typeCascade));
					}
				}
			}
		}
		if (numFailures == 0) {
			return true;
		} else {
			System.out.println("CASCADED JOIN FAILURES:");
			int numTrials = numTypes * numTypes * numTypes;
			System.out.println(numFailures + " of " + numTrials + " tests failed.");
			for (int i = 0; i < numFailures; i++) {
				System.out.println(failures.get(i));
			}
			System.out.println("SUCCESSES:");
			for (int i = 0; i < numTrials - numFailures; i++) {
				System.out.println(successes.get(i));
			}
			return false;
		}
	}
	private static boolean testTriggerJoinCascadedGeneric(String joinTypeSrc1, String joinTypeSrc2, String joinTypeCascade) throws ClassNotFoundException, SQLException {
		String s1 = "s1";
		String s2 = "s2";
		String s3 = "s3";
		String s4 = "s4";
		String t12 = "t12";
		String t34 = "t34";
		String t1234 = "t1234";
		String sourceQuery1 = CREATE_PUBLIC_TABLE_PREFIX + s1 + " (n int, x double);";
		String sourceQuery2 = CREATE_PUBLIC_TABLE_PREFIX + s2 + " (n int, x double);";
		String sourceQuery3 = CREATE_PUBLIC_TABLE_PREFIX + s3 + " (n int, x double);";
		String sourceQuery4 = CREATE_PUBLIC_TABLE_PREFIX + s4 + " (n int, x double);";
		String targetQuery12 = CREATE_PUBLIC_TABLE_PREFIX + t12 + " (n1 int, x1 double, n2 int, x2 double);";
		String targetQuery34 = CREATE_PUBLIC_TABLE_PREFIX + t34 + " (n3 int, x3 double, n4 int, x4 double);";
		String targetQuery1234 = CREATE_PUBLIC_TABLE_PREFIX + t1234 + " (n1 int, x1 double, n2 int, x2 double, n3 int, x3 double, n4 int, x4 double);";
		String triggerQuery12 = "create trigger join12 oftype join on " + s1 + ", " + s2 + ", " + t12 + " use type=\"" + joinTypeSrc1 + "\" on=\"" + s1 + ".n == " + s2
				+ ".n\" selects=\"n1=" + s1 + ".n, x1=" + s1 + ".x, n2=" + s2 + ".n, x2=" + s2 + ".x\";";
		String triggerQuery34 = "create trigger join34 oftype join on " + s3 + ", " + s4 + ", " + t34 + " use type=\"" + joinTypeSrc2 + "\" on=\"" + s3 + ".n == " + s4
				+ ".n\" selects=\"n3=" + s3 + ".n, x3=" + s3 + ".x, n4=" + s4 + ".n, x4=" + s4 + ".x\";";
		String triggerQuery1234 = "create trigger join1234 oftype join on " + t12 + ", " + t34 + ", " + t1234 + " use type=\"" + joinTypeCascade + "\" on=\"" + t12 + ".n1 == "
				+ t34 + ".n3 && " + t12 + ".n2 == " + t34 + ".n4\" selects=\"n1=" + t12 + ".n1, x1=" + t12 + ".x1, n2=" + t12 + ".n2, x2=" + t12 + ".x2, n3=" + t34 + ".n3, x3="
				+ t34 + ".x3, n4=" + t34 + ".n4, x4=" + t34 + ".x4\";";
		String c12 = "c12";
		String c34 = "c34";
		String comparisonQuery12 = CREATE_PUBLIC_TABLE_PREFIX + c12 + " as select " + s1 + ".n as n1, " + s1 + ".x as x1, " + s2 + ".n as n2, " + s2 + ".x as x2 from " + s1 + " "
				+ getStaticJoinKeyword(joinTypeSrc1) + " join " + s2 + " on " + s1 + ".n == " + s2 + ".n;";
		String comparisonQuery34 = CREATE_PUBLIC_TABLE_PREFIX + c34 + " as select " + s3 + ".n as n3, " + s3 + ".x as x3, " + s4 + ".n as n4, " + s4 + ".x as x4 from " + s3 + " "
				+ getStaticJoinKeyword(joinTypeSrc2) + " join " + s4 + " on " + s3 + ".n == " + s4 + ".n;";
		String c1234 = "c1234";
		String comparisonQuery1234 = CREATE_PUBLIC_TABLE_PREFIX + c1234 + " as select " + c12 + ".n1 as n1, " + c12 + ".x1 as x1, " + c12 + ".n2 as n2, " + c12 + ".x2 as x2, "
				+ c34 + ".n3 as n3, " + c34 + ".x3 as x3, " + c34 + ".n4 as n4, " + c34 + ".x4 as x4 from " + c12 + " " + getStaticJoinKeyword(joinTypeCascade) + " join " + c34
				+ " on " + c12 + ".n1 == " + c34 + ".n3 && " + c12 + ".n2 == " + c34 + ".n4;";
		return testTrigger(1000, CH.l(sourceQuery1, sourceQuery2, sourceQuery3, sourceQuery4), CH.l(targetQuery12, targetQuery34, targetQuery1234),
				CH.l(triggerQuery12, triggerQuery34, triggerQuery1234), CH.l(comparisonQuery12, comparisonQuery34, comparisonQuery1234));
	}

	private static boolean testTriggerJoineOneTablePrimaryLeftJoin(boolean leftPrimary) throws ClassNotFoundException, SQLException {
		return testTriggerJoinOneTablePrimaryGeneric(JOIN_TYPE_LEFT, leftPrimary);
	}
	private static boolean testTriggerJoineOneTablePrimaryRightJoin(boolean leftPrimary) throws ClassNotFoundException, SQLException {
		return testTriggerJoinOneTablePrimaryGeneric(JOIN_TYPE_RIGHT, leftPrimary);
	}
	private static boolean testTriggerJoineOneTablePrimaryOuterJoin(boolean leftPrimary) throws ClassNotFoundException, SQLException {
		return testTriggerJoinOneTablePrimaryGeneric(JOIN_TYPE_OUTER, leftPrimary);
	}
	private static boolean testTriggerJoineOneTablePrimaryLeftOnlyJoin(boolean leftPrimary) throws ClassNotFoundException, SQLException {
		return testTriggerJoinOneTablePrimaryGeneric(JOIN_TYPE_LEFT_ONLY, leftPrimary);
	}
	private static boolean testTriggerJoineOneTablePrimaryRightOnlyJoin(boolean leftPrimary) throws ClassNotFoundException, SQLException {
		return testTriggerJoinOneTablePrimaryGeneric(JOIN_TYPE_RIGHT_ONLY, leftPrimary);
	}
	private static boolean testTriggerJoineOneTablePrimaryOuterOnlyJoin(boolean leftPrimary) throws ClassNotFoundException, SQLException {
		return testTriggerJoinOneTablePrimaryGeneric(JOIN_TYPE_OUTER_ONLY, leftPrimary);
	}
	private static boolean testTriggerJoineOneTablePrimaryInnerJoin(boolean leftPrimary) throws ClassNotFoundException, SQLException {
		return testTriggerJoinOneTablePrimaryGeneric(JOIN_TYPE_INNER, leftPrimary);
	}
	private static boolean testTriggerJoinOneTablePrimaryGeneric(String joinType, boolean leftPrimary) throws ClassNotFoundException, SQLException {
		String primary = "primary";
		String duplicates = "duplicates";
		String primaryQuery = CREATE_PUBLIC_TABLE_PREFIX + primary + " (s  String, a int, b int, c int); create index primaryIndex on " + primary
				+ " (s HASH) USE CONSTRAINT=\"PRIMARY\";";
		String duplicatesQuery = CREATE_PUBLIC_TABLE_PREFIX + duplicates + " (s String, x int, y int, z int);";
		String targetQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_TARGET + " (s String, x int, y int, z int, a int, b int, c int);";
		String leftTableName = leftPrimary ? primary : duplicates;
		String rightTableName = leftPrimary ? duplicates : primary;
		String triggerQuery = "create trigger test_primary_join oftype join on " + leftTableName + ", " + rightTableName + ", " + DEFAULT_NAME_TARGET + " use type=\"" + joinType
				+ "\" on=\"" + duplicates + ".s==" + primary + ".s\" selects=\"s=primary.s, x=x, y=y, z=z, a=a, b=b, c=c\";";
		String comparisonQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_COMPARISON + " as select " + primary + ".s as s, x, y, z, a, b, c  from " + leftTableName + " "
				+ getStaticJoinKeyword(joinType) + " join " + rightTableName + " on " + duplicates + ".s==" + primary + ".s;";
		return testTrigger(10000, CH.l(duplicatesQuery, primaryQuery), CH.l(targetQuery), CH.l(triggerQuery), CH.l(comparisonQuery), NUMBER_OF_GROUPS, false, false);
	}

	// PROJECTION // 

	private static boolean testTriggerProjection() throws ClassNotFoundException, SQLException {
		String sourceName1 = DEFAULT_NAME_SOURCE + "1";
		String sourceName2 = DEFAULT_NAME_SOURCE + "2";
		String sourceName3 = DEFAULT_NAME_SOURCE + "3";
		String sourceQuery1 = CREATE_PUBLIC_TABLE_PREFIX + sourceName1 + " (x double);";
		String sourceQuery2 = CREATE_PUBLIC_TABLE_PREFIX + sourceName2 + " (x double);";
		String sourceQuery3 = CREATE_PUBLIC_TABLE_PREFIX + sourceName3 + " (x double);";
		String targetQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_TARGET + " (y double);";
		String triggerQuery = "create trigger test_proj oftype projection on " + sourceName1 + ", " + sourceName2 + ", " + sourceName3 + ", " + DEFAULT_NAME_TARGET
				+ " use selects=\"y=x\";";
		String comparisonQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_COMPARISON + " as select x as y from " + sourceName1 + " union select * from " + sourceName2
				+ " union select * from " + sourceName3 + ";";
		return testTrigger(1000, CH.l(sourceQuery1, sourceQuery2, sourceQuery3), CH.l(targetQuery), CH.l(triggerQuery), CH.l(comparisonQuery));
	}
	private static boolean testTriggerProjectionSingleTargetMultipleTriggers() throws ClassNotFoundException, SQLException {
		String sourceName1 = DEFAULT_NAME_SOURCE + "1";
		String sourceName2 = DEFAULT_NAME_SOURCE + "2";
		String sourceName3 = DEFAULT_NAME_SOURCE + "3";
		String sourceName4 = DEFAULT_NAME_SOURCE + "4";
		String sourceQuery1 = CREATE_PUBLIC_TABLE_PREFIX + sourceName1 + " (x double);";
		String sourceQuery2 = CREATE_PUBLIC_TABLE_PREFIX + sourceName2 + " (x double);";
		String sourceQuery3 = CREATE_PUBLIC_TABLE_PREFIX + sourceName3 + " (x double);";
		String sourceQuery4 = CREATE_PUBLIC_TABLE_PREFIX + sourceName4 + " (x double);";
		String targetQuery1 = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_TARGET + " (y double);";
		String triggerQuery1 = "create trigger test_proj1 oftype projection on " + sourceName1 + ", " + sourceName2 + ", " + DEFAULT_NAME_TARGET + " use selects=\"y=x\";";
		String triggerQuery2 = "create trigger test_proj2 oftype projection on " + sourceName3 + ", " + sourceName4 + ", " + DEFAULT_NAME_TARGET + " use selects=\"y=x\";";
		String comparisonQuery = CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_COMPARISON + " as select x as y from " + sourceName1 + " union select * from " + sourceName2
				+ " union select * from " + sourceName3 + " union select * from " + sourceName4 + ";";
		return testTrigger(1000, CH.l(sourceQuery1, sourceQuery2, sourceQuery3, sourceQuery4), CH.l(targetQuery1), CH.l(triggerQuery1, triggerQuery2), CH.l(comparisonQuery));
	}
	//	private static boolean testInsertIntoTargetProjection() throws ClassNotFoundException, SQLException {
	//		return testInsertIntoTargetGeneric("create trigger insert_into_proj oftype projection on " + DEFAULT_NAME_SOURCE + "1, " + DEFAULT_NAME_SOURCE + "2, "
	//				+ DEFAULT_NAME_TARGET + " use wheres=\"x > 0\" selects=\"y=x\"");
	//	}
	private static boolean testInsertIntoTargetProjection() throws ClassNotFoundException, SQLException {
		return testInsertIntoTargetGeneric(
				"create trigger test_insert_agg oftype aggregate on " + DEFAULT_NAME_SOURCE + "1, " + DEFAULT_NAME_TARGET + " use groupBys=\"m=n\" selects=\"y=sum(x)\"");
	}
	private static boolean testTriggerProjectionCascaded() throws ClassNotFoundException, SQLException {
		String s1 = "s1";
		String s2 = "s2";
		String s3 = "s3";
		String s4 = "s4";
		String t12 = "t12";
		String t34 = "t34";
		String t1234 = "t1234";
		String sourceQuery1 = CREATE_PUBLIC_TABLE_PREFIX + s1 + " (n int, x double);";
		String sourceQuery2 = CREATE_PUBLIC_TABLE_PREFIX + s2 + " (n int, x double);";
		String sourceQuery3 = CREATE_PUBLIC_TABLE_PREFIX + s3 + " (n int, x double);";
		String sourceQuery4 = CREATE_PUBLIC_TABLE_PREFIX + s4 + " (n int, x double);";
		String targetQuery12 = CREATE_PUBLIC_TABLE_PREFIX + t12 + " (m int, y double);";
		String targetQuery34 = CREATE_PUBLIC_TABLE_PREFIX + t34 + " (m int, y double);";
		String targetQuery1234 = CREATE_PUBLIC_TABLE_PREFIX + t1234 + " (p int, z double);";
		String triggerQuery12 = "create trigger proj12 oftype projection on " + s1 + ", " + s2 + ", " + t12 + " use wheres=\"n > 0\" selects=\"m=n, y=x\";";
		String triggerQuery34 = "create trigger proj34 oftype projection on " + s3 + ", " + s4 + ", " + t34 + " use wheres=\"n > 1\" selects=\"m=n, y=x\";";
		String triggerQuery1234 = "create trigger proj1234 oftype projection on " + t12 + ", " + t34 + ", " + t1234 + " use wheres=\"m > 2\" selects=\"p=m, z=y\";";
		String c12 = "c12";
		String c34 = "c34";
		String c1234 = "c1234";
		String comparisonQuery12 = CREATE_PUBLIC_TABLE_PREFIX + c12 + " as select n as m, x as y from " + s1 + " where n > 0 union select * from " + s2 + " where n > 0;";
		String comparisonQuery34 = CREATE_PUBLIC_TABLE_PREFIX + c34 + " as select n as m, x as y from " + s3 + " where n > 1 union select * from " + s4 + " where n > 1;";
		String comparisonQuery1234 = CREATE_PUBLIC_TABLE_PREFIX + c1234 + " as select m as p, y as z from " + c12 + " where m > 2 union select * from " + c34 + " where m > 2;";
		return testTrigger(30, CH.l(sourceQuery1, sourceQuery2, sourceQuery3, sourceQuery4), CH.l(targetQuery12, targetQuery34, targetQuery1234),
				CH.l(triggerQuery12, triggerQuery34, triggerQuery1234), CH.l(comparisonQuery12, comparisonQuery34, comparisonQuery1234));
	}

	// COMPOUND //

	// GENERIC // 

	private static boolean testInsertIntoTargetGeneric(String triggerQuery) throws ClassNotFoundException, SQLException {
		int numSourceOps = 100;
		int numTargetOps = 100;
		Table beforeTables = getTables();
		Table beforeTriggers = getTriggers();
		StringBuilder query = new StringBuilder();
		String sourceName1 = DEFAULT_NAME_SOURCE + "1";
		String sourceName2 = DEFAULT_NAME_SOURCE + "2";
		query.append(CREATE_PUBLIC_TABLE_PREFIX + sourceName1 + " (n int, x double);").append(SH.NEWLINE);
		query.append(CREATE_PUBLIC_TABLE_PREFIX + sourceName2 + " (n int, x double);").append(SH.NEWLINE);
		query.append(CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_TARGET + " (m int, y double);").append(SH.NEWLINE);
		query.append(triggerQuery).append(SH.NEWLINE);
		for (int i = 0; i < numSourceOps; i++) {
		}
		for (int i = 0; i < numTargetOps; i++) { // Attempt to modify target table (should fail)
			query.append("insert into " + DEFAULT_NAME_TARGET + " values (" + RNG.nextInt(10) + ", " + RNG.nextDouble() + ");").append(SH.NEWLINE);
		}
		query.append(CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_COMPARISON + " (m int, y double);").append(SH.NEWLINE);
		executeQuery(query.toString());
		boolean success = checkTablesSame(DEFAULT_NAME_TARGET, DEFAULT_NAME_COMPARISON);

		//
		Table afterTriggers = getTriggers();
		Table afterTables = getTables();
		dropAddedTriggers(beforeTriggers, afterTriggers);
		dropAddedTables(beforeTables, afterTables);
		return success;
	}

	// GENERAL // 

	private static boolean testNoNullInsert() throws ClassNotFoundException, SQLException {
		String sourceOperation = "insert into " + DEFAULT_NAME_SOURCE + " values (1, 5), (null, 3);\ninsert into " + DEFAULT_NAME_SOURCE + " values (null, 4);";
		String comparisonOperation = "insert into " + DEFAULT_NAME_COMPARISON + " values (1, 5);";
		return testNoNullGeneric(sourceOperation, comparisonOperation);
	}
	private static boolean testNoNullUpdate() throws ClassNotFoundException, SQLException {
		String sourceOperation = "insert into " + DEFAULT_NAME_SOURCE + " values (2, 3);\nupdate " + DEFAULT_NAME_SOURCE + " set x=null where x == 2;";
		String comparisonOperation = "insert into " + DEFAULT_NAME_COMPARISON + " values (2, 3);";
		return testNoNullGeneric(sourceOperation, comparisonOperation);
	}
	private static boolean testNoNullGeneric(String sourceOperation, String comparisonOperation) throws ClassNotFoundException, SQLException {
		Table beforeTables = getTables();
		StringBuilder query = new StringBuilder();
		query.append(CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_SOURCE + " (x double NoNull, y double);").append(SH.NEWLINE);
		query.append(sourceOperation).append(SH.NEWLINE);
		query.append(CREATE_PUBLIC_TABLE_PREFIX + DEFAULT_NAME_COMPARISON + " (x double, y double);").append(SH.NEWLINE);
		query.append(comparisonOperation).append(SH.NEWLINE);
		//		System.out.println(query);
		executeQuery(query.toString());
		boolean success = checkTablesSame(DEFAULT_NAME_SOURCE, DEFAULT_NAME_COMPARISON);
		if (!success) {
			printTable(DEFAULT_NAME_SOURCE);
			printTable(DEFAULT_NAME_COMPARISON);
		}
		Table afterTables = getTables();
		dropAddedTables(beforeTables, afterTables);
		return success;
	}
	private static boolean testIndexUnique() throws ClassNotFoundException, SQLException {
		Table beforeTables = getTables();
		StringBuilder query = new StringBuilder();
		query.append(CREATE_PUBLIC_TABLE_PREFIX + "uniqueTable (n int, x double);").append(SH.NEWLINE);
		query.append("create index idxUnique on uniqueTable (n) use constraint=\"UNIQUE\";");
		query.append(CREATE_PUBLIC_TABLE_PREFIX + "ordinaryTable (n int, x double);").append(SH.NEWLINE);
		query.append("insert into uniqueTable values (0, 2.3);").append(SH.NEWLINE);
		query.append("insert into uniqueTable values (0, 2.3);").append(SH.NEWLINE);
		query.append("insert into uniqueTable values (1, 2.3);").append(SH.NEWLINE);
		query.append("insert into uniqueTable values (0, 4.4);").append(SH.NEWLINE);
		query.append("insert into ordinaryTable values (0, 2.3);").append(SH.NEWLINE);
		query.append("insert into ordinaryTable values (1, 2.3);").append(SH.NEWLINE);
		executeQuery(query.toString());
		boolean success = checkTablesSame("uniqueTable", "ordinaryTable");
		Table afterTables = getTables();
		dropAddedTables(beforeTables, afterTables);
		return success;
	}
	private static boolean testIndexPrimary() throws ClassNotFoundException, SQLException {
		Table beforeTables = getTables();
		StringBuilder query = new StringBuilder();
		query.append(CREATE_PUBLIC_TABLE_PREFIX + "primaryTable (n int, x double);").append(SH.NEWLINE);
		query.append("create index idxPrimary on primaryTable (n) use constraint=\"PRIMARY\";");
		query.append(CREATE_PUBLIC_TABLE_PREFIX + "ordinaryTable (n int, x double);").append(SH.NEWLINE);
		query.append("insert into primaryTable values (0, 2.2);").append(SH.NEWLINE);
		query.append("insert into primaryTable values (1, 0.4);").append(SH.NEWLINE);
		query.append("insert into primaryTable values (2, 3.4);").append(SH.NEWLINE);
		query.append("insert into primaryTable values (1, 5.8);").append(SH.NEWLINE);
		query.append("insert into ordinaryTable values (0, 2.2);").append(SH.NEWLINE);
		query.append("insert into ordinaryTable values (1, 5.8);").append(SH.NEWLINE);
		query.append("insert into ordinaryTable values (2, 3.4);").append(SH.NEWLINE);
		executeQuery(query.toString());
		boolean success = checkTablesSame("primaryTable", "ordinaryTable");
		Table afterTables = getTables();
		dropAddedTables(beforeTables, afterTables);
		return success;
	}

	private static void printTable(String tableName) throws ClassNotFoundException, SQLException {
		System.out.println(getTable(tableName));
	}

	private static boolean testTrigger(int numSourceOperations, List<String> sourceQueries, List<String> targetQueries, List<String> triggerQueries, List<String> comparisonQueries)
			throws ClassNotFoundException, SQLException {
		return testTrigger(numSourceOperations, sourceQueries, targetQueries, triggerQueries, comparisonQueries, NUMBER_OF_GROUPS, false, false);
	}
	private static boolean testTrigger(int numSourceOperations, List<String> sourceQueries, List<String> targetQueries, List<String> triggerQueries, List<String> comparisonQueries,
			int numGroups, boolean lineByLine, boolean showFinalResults) throws ClassNotFoundException, SQLException {
		//		return testTrigger(numSourceOperations, sourceQueries, targetQueries, triggerQueries, comparisonQueries, 3, 3, 1);
		//	}
		//	private static boolean testTrigger(int numSourceOperations, List<String> sourceQueries, List<String> targetQueries, List<String> triggerQueries,
		//			List<String> comparisonQueries, double insertBias, double updateBias, double deleteBias) throws ClassNotFoundException, SQLException {
		// Basic plan:
		// 1. Create source and target tables (empty). Also create any indexes on source tables.
		// 2. Create trigger between source and target
		// 3. Add random data to source table
		// 4. Create a third table that has the same operation(s) applied to it as the target table
		// 5. Sort and compare with target
		int numSourceTables = sourceQueries.size();
		int numTargetTables = targetQueries.size();
		int numTriggers = triggerQueries.size();
		int numComparisonTables = comparisonQueries.size();
		Table beforeTables = getTables();
		Table beforeTriggers = getTriggers();
		StringBuilder query = new StringBuilder();
		// 1
		// Create source tables(s)
		String[] sourceTableNames = new String[numSourceTables];
		Table[] sourceTables = new Table[numSourceTables];
		for (int i = 0; i < numSourceTables; i++) {
			query.append(sourceQueries.get(i)).append(SH.NEWLINE);
		}
		// Create target table(s)
		String[] targetTableNames = new String[numTargetTables];
		for (int i = 0; i < numTargetTables; i++) {
			query.append(targetQueries.get(i)).append(SH.NEWLINE);
			targetTableNames[i] = getTableNameFromCreateClause(targetQueries.get(i));
			query.append("delete from " + targetTableNames[i] + ";").append(SH.NEWLINE);
		}
		// 2 
		// Create trigger(s)
		for (int i = 0; i < numTriggers; i++) {
			query.append(triggerQueries.get(i)).append(SH.NEWLINE);
		}
		// 3
		// Fill source with random grouped data
		executeQuery(query.toString());
		for (int i = 0; i < numSourceTables; i++) {
			String tableName = getTableNameFromCreateClause(sourceQueries.get(i));
			sourceTableNames[i] = tableName;
			sourceTables[i] = getTable(tableName);
		}
		query.setLength(0);
		modifyTable(sourceTables, numSourceOperations, query, 5, 5, 1, numGroups);
		//		growTable(sourceTables, numSourceOperations, query);
		//		shrinkTable(sourceTables, numSourceOperations, query);
		// 4
		// Create table(s) by performing equivalent operations on source for comparison
		for (int i = 0; i < numComparisonTables; i++) {
			query.append(comparisonQueries.get(i)).append(SH.NEWLINE);
		}
		if (lineByLine) {
			executeQueryLineByLine(query.toString(), false, false, CH.l(sourceTableNames), CH.l(targetTableNames), CH.l(comparisonQueries));
		} else {
			executeQuery(query.toString());
		}
		for (int i = 0; i < numSourceTables; i++) {
			System.out.println(getTable(sourceTables[i].getTitle()));
		}
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		for (int i = 0; i < numSourceTables; i++) {
			System.out.println(resultSetToTable(executeQuery("select count(1) as cnt_" + sourceTableNames[i] + " from " + sourceTableNames[i] + ";")));
		}
		// 5
		// Compare to target
		String[] comparisonTableNames = new String[numComparisonTables];
		for (int i = 0; i < numComparisonTables; i++) {
			comparisonTableNames[i] = getTableNameFromCreateClause(comparisonQueries.get(i));
		}
		boolean success = true;
		for (int i = 0; i < numTargetTables; i++) {
			success = success && checkTablesSame(comparisonTableNames[i], targetTableNames[i]);
		}
		if (!success) {
			System.out.println("!!!!!!!FAILURE!!!!!!!");
			printTargetAndComparisonTables(numTargetTables, targetTableNames, comparisonTableNames);
		} else {
			System.out.println("!!!!!!!SUCCESS!!!!!!!");
			if (showFinalResults) {
				printSourceTables(numSourceTables, sourceTableNames);
				printTargetAndComparisonTables(numTargetTables, targetTableNames, comparisonTableNames);
			}
		}
		Table afterTriggers = getTriggers();
		dropAddedTriggers(beforeTriggers, afterTriggers);
		Table afterTables = getTables();
		dropAddedTables(beforeTables, afterTables);
		return success;
	}
	private static void printTargetAndComparisonTables(int numTargetTables, String[] targetTableNames, String[] comparisonTableNames) throws ClassNotFoundException, SQLException {
		for (int i = 0; i < numTargetTables; i++) {
			System.out.println(getTable(targetTableNames[i]));
			System.out.println(getTable(comparisonTableNames[i]));
		}
	}
	private static void printSourceTables(int numSourceTables, String[] sourceTableNames) throws ClassNotFoundException, SQLException {
		for (int i = 0; i < numSourceTables; i++) {
			System.out.println(getTable(sourceTableNames[i]));
		}
	}
	//	private static void growTable(Table[] tables, int numOperations, int numGroups, StringBuilder sink) throws ClassNotFoundException, SQLException {
	//		modifyTable(tables, numOperations, sink, 1, 1, 0.02, numGroups, -1, 1000);
	//	}
	//	private static void shrinkTable(Table[] tables, int numOperations, int numGroups, StringBuilder sink) throws ClassNotFoundException, SQLException {
	//		modifyTable(tables, numOperations, sink, 0.02, 1, 1, numGroups, 1000, -1);
	//	}
	// TODO: Incorporate ability to use a limit instead of specifying number of operations
	private static void modifyTable(Table[] tables, int numOperations, StringBuilder sink, double insertBias, double updateBias, double deleteBias, int numGroups)
			throws SQLException, ClassNotFoundException {
		modifyTable(tables, numOperations, sink, insertBias, updateBias, deleteBias, numGroups, -1, -1);
	}
	private static void modifyTable(Table[] tables, int numOperations, StringBuilder sink, double insertBias, double updateBias, double deleteBias, int numGroups, int minRows,
			int maxRows) throws SQLException, ClassNotFoundException {
		if (insertBias < 0 || updateBias < 0 || deleteBias < 0) {
			throw new IllegalArgumentException("Bias arguments must be non-negative");
		}
		boolean useMin = minRows >= 0;
		boolean useMax = maxRows >= 0;
		if (useMin && useMax && Math.abs(minRows) >= Math.abs(maxRows)) {
			throw new IllegalArgumentException("minRows must be less than maxRows");
		}
		double biasSum = insertBias + updateBias + deleteBias;
		double insertBiasNormalized;
		double updateBiasNormalized;
		if (biasSum == 0) {
			insertBiasNormalized = 1.0 / 3.0;
			updateBiasNormalized = insertBiasNormalized;
		} else {
			insertBiasNormalized = insertBias / biasSum;
			updateBiasNormalized = updateBias / biasSum;
		}
		Table table;
		double sample;
		double insertThreshold = insertBiasNormalized;
		double updateThreshold = insertBiasNormalized + updateBiasNormalized;
		int tblNum;
		int approxNumRows = 0;
		for (int i = 0; useMin || useMax || i < numOperations; i++) {
			tblNum = RNG.nextInt(tables.length);
			table = tables[tblNum];
			sample = RNG.nextDouble();
			if (sample < insertThreshold) {
				appendInsertQuery(table, numGroups, sink);
				if (useMax && approxNumRows > maxRows) {
					return;
				} else {
					approxNumRows++;
				}
			} else if (sample < updateThreshold) {
				appendUpdateQuery(table, numGroups, sink);
			} else {
				appendDeleteQuery(table, numGroups, sink);
				if (useMin && approxNumRows < minRows) {
					return;
				} else {
					int x = (approxNumRows) / numGroups;
					approxNumRows -= x;
				}
			}
		}
	}
	private static void appendDeleteQuery(Table table, int numGroups, StringBuilder sink) {
		String whereColName = null;
		Column col;
		int numSourceCols = table.getColumnsCount();
		sink.append("delete from " + table.getTitle() + " where ");
		for (int j = 0; j < numSourceCols; j++) { // Find first Integer column
			col = table.getColumnAt(j);
			if (col.getType() == Integer.class || col.getType() == Short.class) {
				whereColName = (String) col.getId();
			}
		}
		if (whereColName == null) { // If no Integer columns, find first Double column
			for (int j = 0; j < numSourceCols; j++) {
				col = table.getColumnAt(j);
				if (col.getType() == Double.class) {
					whereColName = (String) col.getId();
				}
			}
			double x = RNG.nextDouble();
			double y = RNG.nextDouble();
			double min = OH.min(x, y).doubleValue();
			double max = OH.max(x, y).doubleValue();
			sink.append(min + " <= " + whereColName + " && " + whereColName + " < " + max + ";").append(SH.NEWLINE);
		} else {
			sink.append(whereColName + " == " + RNG.nextInt(numGroups) + ";").append(SH.NEWLINE);
		}
	}
	private static void appendUpdateQuery(Table table, int numGroups, StringBuilder sink) {
		String whereColName = null;
		int numCols = table.getColumnsCount();
		Object entry;
		Column col;
		Class<?> colType;
		sink.append("update " + table.getTitle() + " set ");
		for (int j = 0; j < numCols; j++) {
			if (RNG.nextBoolean() && j < numCols - 1) {
				continue;
			}
			col = table.getColumnAt(j);
			colType = col.getType();
			if (colType == Integer.class || colType == Short.class) {
				entry = RNG.nextInt(numGroups);
				if (whereColName == null) { // Find first Integer column
					whereColName = (String) col.getId();
				}
			} else if (colType == Double.class) {
				entry = RNG.nextDouble();
				entry = ((Double) entry).doubleValue() < 0.2 ? null : entry;
			} else {
				entry = "\"" + symbols[RNG.nextInt(symbols.length)] + "\"";
			}
			sink.append(col.getId() + " = " + entry);
			if (j < numCols - 1) {
				sink.append(",");
			}
		}
		sink.append(" where ");
		if (whereColName == null) { // If no Integer columns, find first Double column
			for (int j = 0; j < numCols; j++) {
				col = table.getColumnAt(j);
				if (col.getType() == Double.class) {
					whereColName = (String) col.getId();
				}
			}
			double x = RNG.nextDouble();
			double y = RNG.nextDouble();
			double min = OH.min(x, y).doubleValue();
			double max = OH.max(x, y).doubleValue();
			sink.append(min + " <= " + whereColName + " && " + whereColName + " < " + max + ";").append(SH.NEWLINE);
		} else {
			sink.append(whereColName + " == " + RNG.nextInt(numGroups) + ";").append(SH.NEWLINE);
		}
	}
	private static void appendInsertQuery(Table table, int numGroups, StringBuilder sink) {
		Object entry;
		int numCols = table.getColumnsCount();
		sink.append("insert into " + table.getTitle() + " values (");
		Class<?> colType;
		for (int j = 0; j < numCols; j++) {
			colType = table.getColumnAt(j).getType();
			if (colType == Integer.class || colType == Short.class) {
				entry = RNG.nextInt(numGroups);
			} else if (colType == Double.class) {
				entry = RNG.nextDouble();
				entry = ((Double) entry).doubleValue() < 0.2 ? null : entry;
			} else {
				entry = "\"" + symbols[RNG.nextInt(symbols.length)] + "\"";
			}
			sink.append(entry);
			if (j < numCols - 1) {
				sink.append(", ");
			}
		}
		sink.append(");").append(SH.NEWLINE);
	}
	private static boolean checkTablesSame(String title1, String title2) throws ClassNotFoundException, SQLException {
		return checkTablesSame(title1, title2, 0.0001);
	}
	private static boolean checkTablesSame(String titleA, String titleB, double tol) throws ClassNotFoundException, SQLException {
		Table beforeTables = getTables();
		StringBuilder setup = new StringBuilder();
		Table tableA = getTable(titleA);
		Table tableB = getTable(titleB);
		List<Tuple2<String, Class<?>>> cols = new ArrayList<Tuple2<String, Class<?>>>();
		int numColsA = tableA.getColumnsCount();
		int numColsB = tableB.getColumnsCount();
		if (numColsA != numColsB) {
			System.out.println("DIFFERENT NUMBER OF COLUMNS: ");
			System.out.println(titleA + " : " + numColsA);
			System.out.println(titleB + " : " + numColsB);
			return false;
		}
		int numRowsA = tableA.getRows().size();
		int numRowsB = tableB.getRows().size();
		if (numRowsA != numRowsB) {
			System.out.println("DIFFERENT NUMBER OF ROWS: ");
			System.out.println(titleA + " : " + numRowsA);
			System.out.println(titleB + " : " + numRowsB);
			return false;
		}
		String colId;
		String colId2;
		Column col;
		for (int i = 0; i < numColsA; i++) { // Check if columns are the same, get list of column names and types
			col = tableA.getColumnAt(i);
			colId = (String) col.getId();
			colId2 = (String) tableB.getColumnAt(i).getId();
			if (!colId.equals(colId2)) {
				System.out.println("DIFFERENT COLUMN NAME AT POS " + i);
				System.out.println(titleA + " : " + colId);
				System.out.println(titleB + " : " + colId2);
				return false;
			}
			cols.add(new Tuple2<String, Class<?>>(colId, col.getType()));
		}
		if (checkTableEmpty(titleA)) { // Both tables empty (since we have checked that they have the same number of rows and that they have the same columns)
			return true;
		}
		StringBuilder colList = new StringBuilder();
		for (int i = 0; i < numColsA; i++) {
			colList.append(cols.get(i).getA());
			if (i < numColsA - 1) {
				colList.append(", ");
			}
		}
		setup.append("create public table " + titleA + "_sorted as select * from " + titleA + " order by ").append(colList).append(";").append(SH.NEWLINE);
		setup.append("create public table " + titleA + "_sorted2 as prepare count(1) as rowId, * from " + titleA + "_sorted;").append(SH.NEWLINE);
		setup.append("create public table " + titleB + "_sorted as select * from " + titleB + " order by ").append(colList).append(";").append(SH.NEWLINE);
		setup.append("create public table " + titleB + "_sorted2 as prepare count(1) as rowId, * from " + titleB + "_sorted;").append(SH.NEWLINE);
		setup.append("create public table comp1 as select ");
		Tuple2<String, Class<?>> c;
		Class<?> colType;
		for (int i = 0; i < numColsA; i++) {
			c = cols.get(i);
			colId = c.getA();
			colType = c.getB();
			if (colType == Double.class) {
				setup.append("(" + titleA + "_sorted2." + colId + " == " + titleB + "_sorted2." + colId + ") || (abs(" + titleA + "_sorted2." + colId + " - " + titleB + "_sorted2."
						+ colId + ") < " + tol + ") as " + colId);
			} else {
				setup.append(titleA + "_sorted2." + colId + " == " + titleB + "_sorted2." + colId + " as " + colId);
			}
			if (i < numColsA - 1) {
				setup.append(", ");
			}
		}
		setup.append(" from " + titleA + "_sorted2, " + titleB + "_sorted2 where " + titleA + "_sorted2.rowId == " + titleB + "_sorted2.rowId;").append(SH.NEWLINE);
		setup.append("create public table comp2 as select ");
		for (int i = 0; i < numColsA; i++) {
			colId = cols.get(i).getA();
			setup.append("min(" + colId + ")");
			if (i < numColsA - 1) {
				setup.append(" && ");
			}
		}
		setup.append(" as test from comp1;").append(SH.NEWLINE);
		//		System.out.println("**********************************************");
		//		System.out.println(setup);
		//		System.out.println(table1);
		//		System.out.println(table2);
		//		System.out.println("**********************************************");
		//		System.out.println("#########################");
		//		System.out.println(setup);
		//		System.out.println("#########################");
		executeQuery(setup.toString());
		Table output = getTable("comp2");
		//		System.out.println(table1);
		//		System.out.println(table2);
		//		System.out.println(output);
		Table afterTables = getTables();
		Boolean result = (Boolean) output.getAt(0, 0);
		if (result == null) {
			result = false;
		} else if (!result) {
			// Check tables again for undefined values
			Table sortedA = getTable(titleA + "_sorted");
			Table sortedB = getTable(titleB + "_sorted");
			//			System.out.println("sortedA");
			//			System.out.println(sortedA);
			//			System.out.println("sortedB");
			//			System.out.println(sortedB);
			Row rA, rB;
			TableList rowsA = sortedA.getRows();
			TableList rowsB = sortedB.getRows();
			result = true;
			rowsLoop: for (int i = 0; i < numRowsA; i++) {
				rA = rowsA.get(i);
				rB = rowsB.get(i);
				for (int j = 0; j < numColsA; j++) {
					if (!equal(rA.getAt(j), rB.getAt(j))) {
						result = false;
						break rowsLoop;
					}
				}
			}
		}
		//		return result == null ? false : result;
		dropAddedTables(beforeTables, afterTables);
		return result;
	}
	private static String getTableNameFromCreateClause(String createClause) {
		return SH.trimWhitespace(SH.beforeFirst(SH.afterFirst(createClause, CREATE_PUBLIC_TABLE_PREFIX), " "));
	}
	private static String getTriggerKeyword(String query) {
		String[] split = SH.split(" ", query);
		if (!"create".equalsIgnoreCase(split[0]) || !"trigger".equalsIgnoreCase(split[1]) || !"oftype".equalsIgnoreCase(split[3])) {
			throw new IllegalArgumentException("Improper create-trigger query: " + query);
		}
		return split[4];
	}
	private static boolean checkTableEmpty(String title) throws ClassNotFoundException, SQLException {
		return getTable(title).getRows().size() == 0;
	}
	private static String generateAggFunctionString(String function, boolean insideString, String... args) {
		if (FUNCTIONS_2_NUM_ARGS.get(function).intValue() != args.length) {
			throw new IllegalArgumentException("Incorrect number of arguments for aggregate function: " + function);
		}
		StringBuilder output = new StringBuilder();
		if (AGG_FUNC_CAT.equals(function)) {
			if (insideString)
				output.append(function + "(" + args[0] + ", \\\"&\\\", 3)");
			else
				output.append(function + "(" + args[0] + ", \"&\", 3)");
		} else {
			output.append(function + "(");
			for (int i = 0; i < args.length; i++) {
				output.append(args[i]);
				if (i < args.length - 1) {
					output.append(", ");
				}
			}
			output.append(")");
		}
		return output.toString();
	}
	private static boolean isUndefined(Number x) {
		return UNDEF_VALS.contains(x);
	}
	private static boolean equal(Object x, Object y) {
		if ((x == null || x instanceof Number) && (y == null || y instanceof Number)) {
			return OH.eq(x, y) || (isUndefined((Number) x) && isUndefined((Number) y));
		} else {
			return OH.eq(x, y);
		}
	}

	static class TestConnection {
		private static final String url = "jdbc:amisql:tongs:4290?username=demo&password=demo123";
		private static Connection connection;

		public static Connection getConnection() throws SQLException {
			try {
				if (connection == null) {
					connection = DriverManager.getConnection(url);
				}
			} catch (SQLException e) {
				//e.printStackTrace();
				throw e;
			}
			return connection;
		}

		public static ResultSet executeQuery(String query) throws SQLException {
			ResultSet rs = null;
			try {
				rs = getConnection().createStatement().executeQuery(query);
			} catch (SQLException e) {
				//e.printStackTrace();
				throw e;
			}
			return rs;
		}

	}

}
