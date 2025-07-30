package com.f1.utils.sqltests;

import java.util.Random;
import java.util.Set;

import org.junit.After;
import org.junit.Test;

import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.CH;
import com.f1.utils.sql.SqlPlanListener;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.sql.Tableset;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.DerivedCellTimeoutController;

public class SqlProcessorTestTimed {

	/**
	 * 
	 * Test mode is set by the RUN_AVERAGE variable.
	 * 
	 * Average Mode - Generate estimated running times for each query by averaging a number of run times. (RUN_AVERAGE == true)
	 * 
	 * Check Mode - Check that the runtime for each query does not exceed the estimated run time multiplied by TOLERANCE_FACTOR. (RUN_AVERAGE == false)
	 * 
	 */

	private static final double TOLERANCE_FACTOR = 1.7; // In Check Mode, the estimated runtime is multiplied by this factor and then compared with the new recorded runtime.
	private final static Tableset TABLESET = SqlProcessorTest.newTableset();
	private final static SqlProcessor sp = new SqlProcessor();
	private final static Random rng = new Random();
	private final static Random rngSeeded = new Random(123);
	private final static boolean VERBOSE = true; // Print information about query runtimes when true
	private final static boolean RUN_AVERAGE = false;

	public static void main(String[] args) {
	}

	private static Table highCardA = new BasicTable();
	private static Table highCardB = new BasicTable();
	static {
		highCardA.addColumn(Integer.class, "col0");
		highCardA.addColumn(Integer.class, "col1");
		highCardB.addColumn(Integer.class, "col0");
		highCardB.addColumn(Integer.class, "col1");
		highCardA.setTitle("A");
		highCardB.setTitle("B");
		Row rowA;
		Row rowB;
		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < 100; j++) {
				rowA = highCardA.newEmptyRow();
				rowB = highCardB.newEmptyRow();
				rowA.putAt(0, i);
				rowA.putAt(1, j);
				rowB.putAt(0, i);
				rowB.putAt(1, j);
				highCardA.getRows().addRow(rowA.getValues());
				highCardB.getRows().addRow(rowB.getValues());
			}
		}
	}
	private static Table lowHighCardA = new BasicTable();
	private static Table lowHighCardB = new BasicTable();
	static {
		lowHighCardA.addColumn(Integer.class, "mostly_zeros");
		lowHighCardA.addColumn(Integer.class, "ones");
		lowHighCardB.addColumn(Integer.class, "mostly_zeros");
		lowHighCardB.addColumn(Integer.class, "ones");
		lowHighCardA.setTitle("A");
		lowHighCardB.setTitle("B");
		Row rowA;
		Row rowB;
		for (int i = 0; i < 1000; i++) {
			rowA = lowHighCardA.newEmptyRow();
			rowB = lowHighCardB.newEmptyRow();
			rowA.putAt(0, rngSeeded.nextInt(100) == 0 ? 1 : 0);
			rowB.putAt(0, rngSeeded.nextInt(100) == 0 ? 1 : 0);
			rowA.putAt(1, 1);
			rowB.putAt(1, 1);
			lowHighCardA.getRows().add(rowA);
			lowHighCardB.getRows().add(rowB);
		}
	}

	private static Table createBigTable(String title, int numRows, int numCols) {
		BasicTable table = new BasicTable();
		for (int j = 0; j < numCols; j++) {
			table.addColumn(Double.class, "col" + j);
		}
		table.setTitle(title);
		Row row;
		for (int i = 0; i < numRows; i++) {
			row = table.newEmptyRow();
			for (int j = 0; j < numCols; j++) {
				row.putAt(j, rng.nextDouble());
			}
			table.getRows().addRow(row.getValues());
		}
		return table;
	}
	private static void clearTestEnvironment() {
		TABLESET.clearTables();
	}
	@After
	public void teardown() {
		clearTestEnvironment();
	}
	@Test
	public void testJoin3Way() {
		String outputTblNm = "ABC";
		Table A, B, C;
		TABLESET.putTable(A = createBigTable("A", 10, 3));
		TABLESET.putTable(B = createBigTable("B", 1000, 3));
		TABLESET.putTable(C = createBigTable("C", 100, 3));
		runTimedTest("create table " + outputTblNm + " as select * from A, B, C;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 30, 0.4804313513400001, 10);
		checkRowCount(outputTblNm, CH.s(A, B, C));
	}
	@Test
	public void testJoin3WayEq() {
		TABLESET.putTable(createBigTable("A", 1000, 3));
		TABLESET.putTable(createBigTable("B", 1000, 3));
		TABLESET.putTable(createBigTable("C", 1000, 3));
		runTimedTest("select * from A, B, C where A.col0 == B.col1 && B.col1 == C.col2;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 30, 0.003798191640000001, 10);
	}
	@Test
	public void testJoin2WayEq() {
		TABLESET.putTable(createBigTable("A", 1000, 3));
		TABLESET.putTable(createBigTable("B", 1000, 3));
		runTimedTest("select * from A, B where A.col0 == B.col0;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 30, 0.007778029100000001, 10);
	}
	@Test
	public void testJoin8WayEq() {
		TABLESET.putTable(createBigTable("A", 1000, 8));
		TABLESET.putTable(createBigTable("B", 1000, 8));
		TABLESET.putTable(createBigTable("C", 1000, 8));
		TABLESET.putTable(createBigTable("D", 1000, 8));
		TABLESET.putTable(createBigTable("E", 1000, 8));
		TABLESET.putTable(createBigTable("F", 1000, 8));
		TABLESET.putTable(createBigTable("G", 1000, 8));
		TABLESET.putTable(createBigTable("H", 1000, 8));
		runTimedTest("select * from A, B, C, D, E, F, G, H where " +

				"A.col0 == B.col1 && " +

				"B.col1 == C.col2 && " +

				"C.col2 == D.col3 && " +

				"D.col3 == E.col4 && " +

				"E.col4 == F.col5 && " +

				"F.col5 == G.col6 && " +

				"G.col6 == H.col7 && " +

				"H.col7 == A.col0;",

				sp, TABLESET, VERBOSE, RUN_AVERAGE, 1000, 0.003798191640000001, 1000);

	}
	@Test
	public void testJoin3WayComp() {
		TABLESET.putTable(createBigTable("A", 10, 3));
		TABLESET.putTable(createBigTable("B", 1000, 3));
		TABLESET.putTable(createBigTable("C", 100, 3));
		runTimedTest("select * from A, B, C where A.col0 <= B.col1 && B.col1 != C.col2;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 30, 0.4058939844999999, 10);
	}
	@Test
	public void testIntraTableConst() {
		TABLESET.putTable(createBigTable("A", 10000, 3));
		runTimedTest("select * from A where col0 < 0.3;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 30, 0.013323185033333332, 10);
	}
	@Test
	public void testIntraTableCol() {
		TABLESET.putTable(createBigTable("A", 10000, 3));
		runTimedTest("select * from A where col0 < col1;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 30, 0.011374157100000002, 10);
	}
	@Test
	public void testInterTableNEq() {
		TABLESET.putTable(createBigTable("A", 1000, 3));
		TABLESET.putTable(createBigTable("B", 1000, 3));
		runTimedTest("select * from A, B where A.col0 != B.col1;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 30, 0.4773927789333332, 10);
	}
	@Test
	public void testInterTableLt() {
		TABLESET.putTable(createBigTable("A", 1000, 3));
		TABLESET.putTable(createBigTable("B", 1000, 3));
		runTimedTest("select * from A, B where A.col0 < B.col1;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 30, 0.32010615886666666, 10);
	}
	@Test
	public void testInterTableLte() {
		TABLESET.putTable(createBigTable("A", 1000, 3));
		TABLESET.putTable(createBigTable("B", 1000, 3));
		runTimedTest("select * from A, B where A.col0 <= B.col1;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 30, 0.3293800283666667, 10);
	}
	@Test
	public void testInterTableGt() {
		TABLESET.putTable(createBigTable("A", 1000, 3));
		TABLESET.putTable(createBigTable("B", 1000, 3));
		runTimedTest("select * from A, B where A.col0 > B.col1;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 30, 0.31978066086666673, 10);
	}
	@Test
	public void testInterTableGte() {
		TABLESET.putTable(createBigTable("A", 1000, 3));
		TABLESET.putTable(createBigTable("B", 1000, 3));
		runTimedTest("select * from A, B where A.col0 >= B.col1;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 30, 0.3290638144666666, 10);
	}
	@Test
	public void testInterTableLtLt() {
		TABLESET.putTable(createBigTable("A", 1000, 3));
		TABLESET.putTable(createBigTable("B", 1000, 3));
		runTimedTest("select * from A, B where A.col0 < B.col1 && B.col1 < A.col2;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 30, 0.28576527129999996, 10);
	}
	@Test
	public void testInterTableLtLte() {
		TABLESET.putTable(createBigTable("A", 1000, 3));
		TABLESET.putTable(createBigTable("B", 1000, 3));
		runTimedTest("select * from A, B where A.col0 < B.col1 && B.col1 <= A.col2;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 30, 0.29373586413333336, 10);
	}
	@Test
	public void testInterTableLteLt() {
		TABLESET.putTable(createBigTable("A", 1000, 3));
		TABLESET.putTable(createBigTable("B", 1000, 3));
		runTimedTest("select * from A, B where A.col0 <= B.col1 && B.col1 < A.col2;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 30, 0.29631704256666663, 10);
	}
	@Test
	public void testInterTableLteLte() {
		TABLESET.putTable(createBigTable("A", 1000, 3));
		TABLESET.putTable(createBigTable("B", 1000, 3));
		runTimedTest("select * from A, B where A.col0 <= B.col1 && B.col1 <= A.col2;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 30, 0.29733984650000006, 10);
	}
	@Test
	public void testInterTableGtGt() {
		TABLESET.putTable(createBigTable("A", 1000, 3));
		TABLESET.putTable(createBigTable("B", 1000, 3));
		runTimedTest("select * from A, B where A.col0 > B.col1 && B.col1 > A.col2;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 30, 0.30451505913333327, 10);
	}
	@Test
	public void testInterTableGtGte() {
		TABLESET.putTable(createBigTable("A", 1000, 3));
		TABLESET.putTable(createBigTable("B", 1000, 3));
		runTimedTest("select * from A, B where A.col0 > B.col1 && B.col1 >= A.col2;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 30, 0.2970568703, 10);
	}
	@Test
	public void testInterTableGteGt() {
		TABLESET.putTable(createBigTable("A", 1000, 3));
		TABLESET.putTable(createBigTable("B", 1000, 3));
		runTimedTest("select * from A, B where A.col0 >= B.col1 && B.col1 > A.col2;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 30, 0.29880908030000003, 10);
	}
	@Test
	public void testInterTableGteGte() {
		TABLESET.putTable(createBigTable("A", 1000, 3));
		TABLESET.putTable(createBigTable("B", 1000, 3));
		runTimedTest("select * from A, B where A.col0 >= B.col1 && B.col1 >= A.col2;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 30, 0.29536344270000003, 10);
	}
	@Test
	public void test2ColJoinRand() {
		TABLESET.putTable(createBigTable("A", 10000, 3));
		TABLESET.putTable(createBigTable("B", 10000, 3));
		runTimedTest("select * from A, B where A.col0 == B.col0 && B.col1 == A.col1;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 1000, 0.006167064562000005, 1000);
	}
	@Test
	public void test2ColJoinCardinality() {
		TABLESET.putTable(highCardA);
		TABLESET.putTable(highCardB);
		runTimedTest("select * from A, B where A.col0 == B.col0 && A.col1 == B.col1;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 200, 0.014208527530000006, 200);
	}
	@Test
	public void test2ColJoinCardinality2() {
		TABLESET.putTable(highCardA);
		TABLESET.putTable(highCardB);
		runTimedTest("select * from A, B where B.col0 == A.col0 && B.col1 == A.col1;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 200, 0.01393803886, 200);
	}
	@Test
	public void test2ColJoinCardinality3() {
		TABLESET.putTable(highCardA);
		TABLESET.putTable(highCardB);
		runTimedTest("select * from A, B where A.col1 == B.col1 && A.col0 == B.col0;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 200, 0.013743673854999994, 200);
	}
	@Test
	public void test2ColJoinCardinality4() {
		TABLESET.putTable(highCardA);
		TABLESET.putTable(highCardB);
		runTimedTest("select * from A, B where B.col1 == A.col1 && B.col0 == A.col0;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 200, 0.014066824350000002, 200);
	}
	@Test
	public void testMultiJoinLargeA() {
		testMultiTableQuery("A", 1000, 100, false, 500, 500, 0.01909878890199999);
	}
	@Test
	public void testMultiJoinLargeB() {
		testMultiTableQuery("B", 1000, 100, false, 500, 500, 0.018671900302000007);
	}
	@Test
	public void testMultiJoinLargeC() {
		testMultiTableQuery("C", 1000, 100, false, 500, 500, 0.019328190158);
	}
	@Test
	public void testMultiJoinLargeD() {
		testMultiTableQuery("D", 1000, 100, false, 500, 500, 0.01985549545600003);
	}
	@Test
	public void testMultiJoinLargeE() {
		testMultiTableQuery("E", 1000, 100, false, 500, 500, 0.019131351725999994);
	}
	@Test
	public void testMultiJoinLargeF() {
		testMultiTableQuery("F", 1000, 100, false, 500, 500, 0.01882825742200002);
	}
	@Test
	public void testMultiJoinLargeG() {
		testMultiTableQuery("G", 1000, 100, false, 500, 500, 0.01814928419599999);
	}
	@Test
	public void testMultiJoinSmallA() {
		testMultiTableQuery("A", 1000, 100, true, 200, 15, 0.17376939930999996);
	}
	@Test
	public void testMultiJoinSmallB() {
		testMultiTableQuery("B", 1000, 100, true, 200, 15, 0.18291862942499987);
	}
	@Test
	public void testMultiJoinSmallC() {
		testMultiTableQuery("C", 1000, 100, true, 200, 15, 0.18833968144499988);
	}
	@Test
	public void testMultiJoinSmallD() {
		testMultiTableQuery("D", 1000, 100, true, 200, 15, 0.19744934147500004);
	}
	@Test
	public void testMultiJoinSmallE() {
		testMultiTableQuery("E", 1000, 100, true, 200, 15, 0.19052799705);
	}
	@Test
	public void testMultiJoinSmallF() {
		testMultiTableQuery("F", 1000, 100, true, 200, 15, 0.19724233559999985);
	}
	@Test
	public void testMultiJoinSmallG() {
		testMultiTableQuery("G", 1000, 100, true, 200, 15, 0.19993047689000004);
	}
	@Test
	public void testPreFilter1() {
		TABLESET.putTable(lowHighCardA);
		TABLESET.putTable(lowHighCardB);

		// Output: 1000000 records
		runTimedTest("select * from A, B where A.ones == B.ones;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 100, 0.187460308, 100);
	}
	@Test
	public void testPreFilter2() {
		TABLESET.putTable(lowHighCardA);
		TABLESET.putTable(lowHighCardB);

		// Output: 1000000 records 
		runTimedTest("select * from A, B where A.ones == 1 && B.ones == 1;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 100, 0.198438942, 100);
	}
	@Test
	public void testPreFilter3() {
		TABLESET.putTable(lowHighCardA);
		TABLESET.putTable(lowHighCardB);

		// Output: 90 records 
		runTimedTest("select * from A, B where A.ones == B.ones && A.mostly_zeros == 1 && B.mostly_zeros == 1;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 100, 0.002468354, 100);
	}
	@Test
	public void testPreFilter4() {
		TABLESET.putTable(lowHighCardA);
		TABLESET.putTable(lowHighCardB);

		// Output: 90 records 
		runTimedTest("select * from A, B where A.mostly_zeros == 1 && B.mostly_zeros == 1 && A.ones == B.ones;", sp, TABLESET, VERBOSE, RUN_AVERAGE, 100, 0.002416471, 100);
	}
	@Test
	public void testPrePreFilterTrue() {
		TABLESET.putTable(createBigTable("A", 10000, 3));
		runTimedTest("select * from A where true", sp, TABLESET, VERBOSE, RUN_AVERAGE, 100, 0.008181196580000005, 100);
	}
	@Test
	public void testPrePreFilterFalse() {
		TABLESET.putTable(createBigTable("A", 10000, 3));
		runTimedTest("select * from A where false", sp, TABLESET, VERBOSE, RUN_AVERAGE, 100, 0.002499578889999999, 100);
	}
	private static void runTimedTest(String sql, SqlProcessor sp, Tableset ts, boolean verbose, boolean average, int numTrialsAvg, double targetTime, int numTrialsWarmup) {
		if (average) {
			getAverageTime(numTrialsAvg, sql, sp, ts, verbose);
		} else {
			warmup(sql, sp, ts, numTrialsWarmup, false);
			SqlProcessorTestPlanListenerImpl pl = new SqlProcessorTestPlanListenerImpl();
			double actualTime = time(sql, sp, ts, verbose, verbose ? pl : null);
			if (verbose) {
				System.out.println("\nQUERY PLAN:\n");
				System.out.println(pl.getSteps());
			}
			if (actualTime > TOLERANCE_FACTOR * targetTime) {
				throw new RuntimeException("Query exceeds established test limit: Expected=" + targetTime + " sec, Actual=" + actualTime);
			}
		}
	}
	private static double time(String sql, SqlProcessor sp, Tableset ts, boolean verbose, SqlPlanListener planListener) {
		long start = System.nanoTime();
		SqlProcessorTest.processSql(sp, sql, ts, planListener, new DerivedCellTimeoutController(10000));
		long end = System.nanoTime();
		long intervalNanos = end - start;
		double intervalSeconds = ((double) intervalNanos) / 1000000000L;
		if (verbose) {
			System.out.println("\nThe query: \n");
			System.out.println("\t" + sql);
			System.out.println("\nran in " + intervalSeconds + " seconds (" + intervalNanos + " nanoseconds).");
		}
		return intervalSeconds;
	}
	private static void warmup(String sql, SqlProcessor sp, Tableset ts, int numTrials, boolean verbose) {
		for (int i = 0; i < numTrials; i++) {
			if (verbose)
				System.out.println("\nWARMUP TRIAL " + i + " OF " + numTrials + " (" + (i * 100.0) / numTrials + "% complete)");
			time(sql, sp, ts, false, null);
		}
	}
	private static double getAverageTime(int numTrials, String sql, SqlProcessor sp, Tableset ts, boolean verbose) {
		double sum = 0;
		SqlProcessorTestPlanListenerImpl pl = new SqlProcessorTestPlanListenerImpl();
		boolean lastTrial;
		for (int i = 0; i < numTrials; i++) {
			lastTrial = i == numTrials - 1;
			System.out.println("\nTRIAL " + i + " OF " + numTrials + " (" + (i * 100.0) / numTrials + "% complete)");
			sum += time(sql, sp, ts, false, lastTrial ? pl : null);
		}
		double avg = sum / numTrials;
		if (verbose) {
			System.out.println("\n\n=========RESULTS=========\n");
			System.out.println("QUERY: \n");
			System.out.println(sql);
			System.out.println("\nNUMBER OF TRIALS: " + numTrials);
			System.out.println("\nAVERAGE TIME:     " + avg);
			System.out.println("\nQUERY PLAN: \n");
			System.out.println(pl.getSteps());
			System.out.println("_________________________");
		}
		return avg;
	}
	public static void checkRowCount(String outputTableName, Set<Table> inputTables) {
		int outputRows = TABLESET.getTable(outputTableName).getSize();
		if (inputTables.size() == 0) {
			throw new RuntimeException("Must provide at least one input table for comparison.");
		} else {
			int inputRows = 1;
			for (Table t : inputTables) {
				inputRows *= t.getSize();
			}
			if (inputRows != outputRows) {
				throw new RuntimeException("\nIncorrect number of rows in output table.\n Input rows: " + inputRows + "\n Output rows: " + outputRows);
			}
		}
	}
	private static void testMultiTableQuery(String target, int smallTblSz, double factorSmall2Large, boolean smallTarget, int numTrialsAvg, int numTrialsWarmup,
			double targetTime) {

		//
		// Runs a join of multiple tables. Depending on the parameters, makes either one table
		// small and the rest large, or vice-versa. 
		// 
		// Join diagram: 
		//
		// 				G
		//				|
		// 		B - A - C - E
		//			|	|
		//			D	F
		//

		Set<String> tblNames = CH.s("A", "B", "C", "D", "E", "F", "G");
		if (!tblNames.remove(target)) {
			throw new RuntimeException("Provided table name not in set");
		}
		TABLESET.putTable(createBigTable(target, smallTarget ? smallTblSz : (int) factorSmall2Large * smallTblSz, 3));
		for (String nm : tblNames) {
			TABLESET.putTable(createBigTable(nm, smallTarget ? (int) factorSmall2Large * smallTblSz : smallTblSz, 3));
		}
		runTimedTest("select * from A, B, C, D, E, F, G where " +

				"A.col0 == B.col0 && " +

				"A.col0 == D.col0 && " +

				"A.col0 == C.col0 && " +

				"C.col0 == F.col0 && " +

				"C.col0 == G.col0 && " +

				"C.col0 == E.col0;"

				, sp, TABLESET, VERBOSE, RUN_AVERAGE, numTrialsAvg, targetTime, numTrialsWarmup);
	}
}
