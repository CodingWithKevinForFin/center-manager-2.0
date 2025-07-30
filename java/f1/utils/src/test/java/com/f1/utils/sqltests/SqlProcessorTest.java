package com.f1.utils.sqltests;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.junit.After;
import org.junit.Test;

import com.f1.base.Bytes;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.sql.SqlPlanListener;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.sql.TableReturn;
import com.f1.utils.sql.Tableset;
import com.f1.utils.sql.TablesetImpl;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.BasicMethodFactory;
import com.f1.utils.structs.table.derived.DerivedCellTimeoutController;
import com.f1.utils.structs.table.derived.FlowControlThrow;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.MutableCalcFrame;
import com.f1.utils.structs.table.stack.SqlResultset;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

public class SqlProcessorTest {

	private static final String QUERY_RECEIVED_TOKEN = "QUERY_RECEIVED : ";
	private static final String TEST_RESULT_GLOBAL_VARS_TOKEN = "****TEST_RESULT_GLOBAL_VARS****";
	private static final String TEST_QUERY_PLAN_TOKEN = "****TEST_QUERY_PLAN****";
	private final static SqlProcessor sp = new SqlProcessor();
	private static BasicMethodFactory mf = new BasicMethodFactory();
	static {
		mf.addVarType("INT", Integer.class);
		mf.addVarType("LONG", Long.class);
		mf.addVarType("STRING", String.class);
		mf.addVarType("FLOAT", Float.class);
		mf.addVarType("DOUBLE", Double.class);
		mf.addVarType("UTC", DateMillis.class);
		mf.addVarType("UTCN", DateNanos.class);
		mf.addVarType("BOOLEAN", Boolean.class);
		mf.addVarType("BINARY", Bytes.class);
		mf.addVarType("CHARACTER", Character.class);
		mf.addVarType("BYTE", Byte.class);
		mf.addVarType("SHORT", Short.class);
		mf.addVarType("BIGINTEGER", BigInteger.class);
		mf.addVarType("BIGDECIMAL", BigDecimal.class);
	}
	protected final static File root = new File("/tmp/tests");
	private final static Tableset testTableset = newTableset();
	private final static Table tableA = new ColumnarTable(Double.class, "x", Double.class, "y", Double.class, "z");
	static {
		tableA.setTitle("tableA");
		tableA.getRows().addRow(4d, 5d, 8d);
		tableA.getRows().addRow(4d, 11d, 11d);
		tableA.getRows().addRow(9d, 7d, 0d);
		tableA.getRows().addRow(6d, 13d, 33d);
		tableA.getRows().addRow(3d, 8d, 17d);
		tableA.getRows().addRow(4d, 17d, 99d);
		tableA.getRows().addRow(1d, 1d, 23d);
		tableA.getRows().addRow(1d, 7d, 90d);
		tableA.getRows().addRow(1d, 5d, 50d);
	}
	private final static Table tableE = new ColumnarTable(String.class, "abc", String.class, "def", String.class, "xyz");
	static {
		tableE.setTitle("tableE");
		tableE.getRows().addRow("a|b|c", "d,e,f", "x%y%z");
	}
	private final static Table tableF = new ColumnarTable(Double.class, "x", Double.class, "b", Double.class, "z");
	static {
		tableF.setTitle("tableF");
		tableF.getRows().addRow(1.0, 2.0, 6.0);
		tableF.getRows().addRow(11.0, 0.0, 8.0);
		tableF.getRows().addRow(9.0, 8.0, 2.0);
		tableF.getRows().addRow(9.0, 3.0, 5.0);
		tableF.getRows().addRow(4.0, 3.0, 2.0);
	}
	private final static Table analyzeInput = new ColumnarTable(Integer.class, "n", Double.class, "x", Double.class, "y");
	static {
		analyzeInput.setTitle("analyzeInput");
		analyzeInput.getRows().addRow(0, 4.0, -3.4);
		analyzeInput.getRows().addRow(1, 8.1, null);
		analyzeInput.getRows().addRow(2, 9.8, -5.6);
		analyzeInput.getRows().addRow(3, -1.9, 4.9);
		analyzeInput.getRows().addRow(4, 2.3, -9.0);
		analyzeInput.getRows().addRow(5, null, 0.1);
		analyzeInput.getRows().addRow(6, 5.7, 8.2);
		analyzeInput.getRows().addRow(7, -2.8, 7.3);
		analyzeInput.getRows().addRow(8, null, 1.2);
		analyzeInput.getRows().addRow(9, -0.3, 2.8);
		analyzeInput.getRows().addRow(10, 1.8, 3.4);
		analyzeInput.getRows().addRow(11, 6.7, 2.3);
		analyzeInput.getRows().addRow(12, -7.2, null);
		analyzeInput.getRows().addRow(13, -2.1, 5.9);
		analyzeInput.getRows().addRow(14, 1.5, 6.0);
		analyzeInput.getRows().addRow(15, 0.0, 9.1);
		analyzeInput.getRows().addRow(16, 9.9, -0.2);
		analyzeInput.getRows().addRow(17, null, -8.3);
		analyzeInput.getRows().addRow(18, -4.4, 5.8);
		analyzeInput.getRows().addRow(19, 2.8, 6.7);
		analyzeInput.getRows().addRow(20, 7.7, null);
	}
	private final static Table analyzeInputNullTimes = new ColumnarTable(Integer.class, "n", Double.class, "x", Double.class, "y");
	static {
		analyzeInputNullTimes.setTitle("analyzeInputNullTimes");
		analyzeInputNullTimes.getRows().addRow(0, 4.0, -3.4);
		analyzeInputNullTimes.getRows().addRow(null, 8.1, null);
		analyzeInputNullTimes.getRows().addRow(null, 9.8, -5.6);
		analyzeInputNullTimes.getRows().addRow(3, -1.9, 4.9);
		analyzeInputNullTimes.getRows().addRow(4, 2.3, -9.0);
		analyzeInputNullTimes.getRows().addRow(5, null, 0.1);
		analyzeInputNullTimes.getRows().addRow(6, 5.7, 8.2);
		analyzeInputNullTimes.getRows().addRow(null, -2.8, 7.3);
		analyzeInputNullTimes.getRows().addRow(8, null, 1.2);
		analyzeInputNullTimes.getRows().addRow(9, -0.3, 2.8);
		analyzeInputNullTimes.getRows().addRow(null, 1.8, 3.4);
		analyzeInputNullTimes.getRows().addRow(11, 6.7, 2.3);
		analyzeInputNullTimes.getRows().addRow(null, -7.2, null);
		analyzeInputNullTimes.getRows().addRow(13, -2.1, 5.9);
		analyzeInputNullTimes.getRows().addRow(14, 1.5, 6.0);
		analyzeInputNullTimes.getRows().addRow(null, 0.0, 9.1);
		analyzeInputNullTimes.getRows().addRow(16, 9.9, -0.2);
		analyzeInputNullTimes.getRows().addRow(17, null, -8.3);
		analyzeInputNullTimes.getRows().addRow(null, -4.4, 5.8);
		analyzeInputNullTimes.getRows().addRow(19, 2.8, 6.7);
		analyzeInputNullTimes.getRows().addRow(null, 7.7, null);
	}
	private final static Table analyzeInputDuplicateTimes = new ColumnarTable(Integer.class, "n", Double.class, "x", Double.class, "y");
	static {
		analyzeInputDuplicateTimes.setTitle("analyzeInputDuplicateTimes");
		analyzeInputDuplicateTimes.getRows().addRow(0, 4.0, -3.4);
		analyzeInputDuplicateTimes.getRows().addRow(0, 8.1, null);
		analyzeInputDuplicateTimes.getRows().addRow(0, 9.8, -5.6);
		analyzeInputDuplicateTimes.getRows().addRow(3, -1.9, 4.9);
		analyzeInputDuplicateTimes.getRows().addRow(4, 2.3, -9.0);
		analyzeInputDuplicateTimes.getRows().addRow(5, null, 0.1);
		analyzeInputDuplicateTimes.getRows().addRow(6, 5.7, 8.2);
		analyzeInputDuplicateTimes.getRows().addRow(6, -2.8, 7.3);
		analyzeInputDuplicateTimes.getRows().addRow(8, null, 1.2);
		analyzeInputDuplicateTimes.getRows().addRow(9, -0.3, 2.8);
		analyzeInputDuplicateTimes.getRows().addRow(10, 1.8, 3.4);
		analyzeInputDuplicateTimes.getRows().addRow(13, 6.7, 2.3);
		analyzeInputDuplicateTimes.getRows().addRow(13, -7.2, null);
		analyzeInputDuplicateTimes.getRows().addRow(13, -2.1, 5.9);
		analyzeInputDuplicateTimes.getRows().addRow(14, 1.5, 6.0);
		analyzeInputDuplicateTimes.getRows().addRow(15, 0.0, 9.1);
		analyzeInputDuplicateTimes.getRows().addRow(16, 9.9, -0.2);
		analyzeInputDuplicateTimes.getRows().addRow(17, null, -8.3);
		analyzeInputDuplicateTimes.getRows().addRow(19, -4.4, 5.8);
		analyzeInputDuplicateTimes.getRows().addRow(19, 2.8, 6.7);
		analyzeInputDuplicateTimes.getRows().addRow(19, 7.7, null);
	}
	private final static Table analyzeInputMarked = new ColumnarTable(Integer.class, "n", Double.class, "x", Double.class, "y", Integer.class, "g");
	static {
		analyzeInputMarked.setTitle("input_marked");
		TableList rows = analyzeInput.getRows();
		TableList markedRows = analyzeInputMarked.getRows();
		Integer n;
		Double x, y;
		for (Row row : rows) {
			n = Caster_Integer.INSTANCE.cast(row.get("n"));
			x = Caster_Double.INSTANCE.cast(row.get("x"));
			y = Caster_Double.INSTANCE.cast(row.get("y"));
			markedRows.addRow(n, x, y, 0);
			markedRows.addRow(n, x, y, 1);
			markedRows.addRow(n, x, y, 2);
			markedRows.addRow(n, x, y, 3);
			markedRows.addRow(n, x, y, 4);
		}
	}
	private final static Table analyzeInputWithBool = new ColumnarTable(Integer.class, "n", Double.class, "x", Boolean.class, "b");
	static {
		analyzeInputWithBool.setTitle("analyzeInputWithBool");
		analyzeInputWithBool.getRows().addRow(0, 4.0, false);
		analyzeInputWithBool.getRows().addRow(1, 8.1, false);
		analyzeInputWithBool.getRows().addRow(2, 9.8, false);
		analyzeInputWithBool.getRows().addRow(3, -1.9, false);
		analyzeInputWithBool.getRows().addRow(4, 2.3, false);
		analyzeInputWithBool.getRows().addRow(5, null, false);
		analyzeInputWithBool.getRows().addRow(6, 5.7, false);
		analyzeInputWithBool.getRows().addRow(7, -2.8, false);
		analyzeInputWithBool.getRows().addRow(8, null, false);
		analyzeInputWithBool.getRows().addRow(9, -0.3, false);
		analyzeInputWithBool.getRows().addRow(10, 1.8, true);
		analyzeInputWithBool.getRows().addRow(11, 6.7, false);
		analyzeInputWithBool.getRows().addRow(12, -7.2, true);
		analyzeInputWithBool.getRows().addRow(13, -2.1, false);
		analyzeInputWithBool.getRows().addRow(14, 1.5, true);
		analyzeInputWithBool.getRows().addRow(15, 0.0, false);
		analyzeInputWithBool.getRows().addRow(16, 9.9, true);
		analyzeInputWithBool.getRows().addRow(17, null, false);
		analyzeInputWithBool.getRows().addRow(18, -4.4, true);
		analyzeInputWithBool.getRows().addRow(19, 2.8, false);
		analyzeInputWithBool.getRows().addRow(20, 7.7, true);
	}
	private final static Table tableC = new ColumnarTable(Double.class, "a", Double.class, "b", Double.class, "c");
	static {
		tableC.setTitle("tableC");
		tableC.getRows().addRow(1.0, 2.0, 6.0);
		tableC.getRows().addRow(11.0, 0.0, 8.0);
		tableC.getRows().addRow(9.0, 8.0, 2.0);
		tableC.getRows().addRow(9.0, 3.0, 5.0);
		tableC.getRows().addRow(4.0, 3.0, 2.0);
	}
	private final static double eTest = 7.0;
	private final static Table tableD = new ColumnarTable(Double.class, "d", Double.class, "e", Double.class, "f");
	static {
		tableD.setTitle("tableD");
		tableD.getRows().addRow(9.0, 23.0, 44.0);
		tableD.getRows().addRow(1.0, eTest, 6.0);
		tableD.getRows().addRow(1.0, 5.0, 9.0);
		tableD.getRows().addRow(0.0, 1.0, 2.0);
	}
	private final static Table nullTableA = new ColumnarTable(Double.class, "a", Double.class, "b", Double.class, "c");
	static {
		nullTableA.setTitle("nullTableA");
		nullTableA.getRows().addRow(null, null, null);
		nullTableA.getRows().addRow(null, null, null);
		nullTableA.getRows().addRow(null, null, null);
		nullTableA.getRows().addRow(null, null, null);
	}
	private final static Table nullTableB = new ColumnarTable(Double.class, "d", Double.class, "e");
	static {
		nullTableB.setTitle("nullTableB");
		nullTableB.getRows().addRow(null, null);
		nullTableB.getRows().addRow(null, null);
		nullTableB.getRows().addRow(null, null);
	}
	private final static Table t = new ColumnarTable(Double.class, "v", Double.class, "w");
	static {
		t.setTitle("t");
		t.getRows().addRow(4d, 5d);
		t.getRows().addRow(4d, 11d);
		t.getRows().addRow(9d, 7d);
		t.getRows().addRow(6d, 13d);
		t.getRows().addRow(3d, 8d);
		t.getRows().addRow(4d, 17d);
		t.getRows().addRow(1d, 1d);
	}
	private final static Table t2 = new ColumnarTable(Double.class, "v", Double.class, "w");
	static {
		t2.setTitle("t2");
		t2.getRows().addRow(4d, 5d);
		t2.getRows().addRow(4d, 11d);
		t2.getRows().addRow(7d, 7d);
		t2.getRows().addRow(6d, 13d);
		t2.getRows().addRow(3d, 8d);
		t2.getRows().addRow(4d, 17d);
		t2.getRows().addRow(1d, 1d);
	}
	private final static Table t3 = new ColumnarTable(Double.class, "v", String.class, "w", String.class, "s");
	static {
		t3.setTitle("t3");
		t3.getRows().addRow(1d, "what", "this,that,these,those");
		t3.getRows().addRow(2d, "where;asdf", "what,when");
		t3.getRows().addRow(3d, "when;why", null);
	}
	private final static Table sampleInput = new ColumnarTable(String.class, "category", Double.class, "x", Double.class, "y", Double.class, "z");
	static {
		sampleInput.setTitle("input");
		sampleInput.getRows().addRow("A", -4.2, 5.5, 8.8);
		sampleInput.getRows().addRow("B", 4.0, 11.1, null);
		sampleInput.getRows().addRow("A", 0.9, -0.8, 0.4);
		sampleInput.getRows().addRow("A", null, -4.7, 33.4);
		sampleInput.getRows().addRow("C", -3.3, null, 16.1);
		sampleInput.getRows().addRow("C", 4.9, -17.1, -99.0);
		sampleInput.getRows().addRow("B", 1.2, 1.3, -23.4);
		sampleInput.getRows().addRow("B", null, -7.7, 90.1);
		sampleInput.getRows().addRow("C", 4.2, 5.0, null);
		sampleInput.getRows().addRow("A", 9.9, 5.0, 34.5);
		sampleInput.getRows().addRow("B", -3.4, 3.2, 22.2);
		sampleInput.getRows().addRow("C", -0.8, 8.9, null);
		sampleInput.getRows().addRow("C", null, -9.8, null);
		sampleInput.getRows().addRow("D", null, null, null);
	}
	private final static Table analyzePartitionsInput = new ColumnarTable(Integer.class, "g", Integer.class, "n", Double.class, "x", Double.class, "y");
	static {
		analyzePartitionsInput.setTitle("analyze_partitions_input");
		analyzePartitionsInput.getRows().addRow(1, 1, 4d, 5d);
		analyzePartitionsInput.getRows().addRow(1, 2, 4d, 2d);
		analyzePartitionsInput.getRows().addRow(1, 3, 4d, 3d);
		analyzePartitionsInput.getRows().addRow(1, 4, 4d, 0d);
		analyzePartitionsInput.getRows().addRow(1, 5, 4d, 8d);

		analyzePartitionsInput.getRows().addRow(2, 1, 7d, 5d);
		analyzePartitionsInput.getRows().addRow(2, 2, 7d, 0d);
		analyzePartitionsInput.getRows().addRow(2, 3, 7d, 0d);
		analyzePartitionsInput.getRows().addRow(2, 4, 7d, 2d);
		analyzePartitionsInput.getRows().addRow(2, 5, 7d, 3d);

		analyzePartitionsInput.getRows().addRow(3, 1, 8d, 3d);
		analyzePartitionsInput.getRows().addRow(3, 2, 8d, 1d);
		analyzePartitionsInput.getRows().addRow(3, 3, 8d, 2d);
		analyzePartitionsInput.getRows().addRow(3, 4, 8d, 8d);
		analyzePartitionsInput.getRows().addRow(3, 5, 8d, 9d);

		analyzePartitionsInput.getRows().addRow(4, 1, 0d, 2d);
		analyzePartitionsInput.getRows().addRow(4, 2, 0d, 3d);
		analyzePartitionsInput.getRows().addRow(4, 3, 0d, 0d);
		analyzePartitionsInput.getRows().addRow(4, 4, 0d, 2d);
		analyzePartitionsInput.getRows().addRow(4, 5, 0d, 2d);

		analyzePartitionsInput.getRows().addRow(5, 1, 3d, 3d);
		analyzePartitionsInput.getRows().addRow(5, 2, 3d, 8d);
		analyzePartitionsInput.getRows().addRow(5, 3, 3d, 5d);
		analyzePartitionsInput.getRows().addRow(5, 4, 3d, 6d);
		analyzePartitionsInput.getRows().addRow(5, 5, 3d, 7d);
	}
	private final static Table sA = new ColumnarTable(String.class, "nm", Double.class, "x", Integer.class, "y");
	static {
		sA.setTitle("sA");
		sA.getRows().addRow("B", 5d, null);
		sA.getRows().addRow("A", null, 11);
		sA.getRows().addRow(null, 7d, 0);
	}
	private final static Table sB = new ColumnarTable(String.class, "nm", Double.class, "x", Integer.class, "y");
	static {
		sB.setTitle("sB");
		sB.getRows().addRow("B", 2.0, null);
		sB.getRows().addRow("A", null, 8);
	}
	private final static Table sC = new ColumnarTable(String.class, "nm", Double.class, "x", Integer.class, "y");
	static {
		sC.setTitle("sC");
		sC.getRows().addRow("A", null, 44);
		sC.getRows().addRow("B", 7.0, 6);
		sC.getRows().addRow(null, 5.0, null);
	}
	private final static Table sD = new ColumnarTable(Double.class, "x");
	private final static Table sE = new ColumnarTable(Double.class, "x");
	private final static Table sF = new ColumnarTable(Double.class, "x");
	private final static Table sDnulls = new ColumnarTable(Double.class, "x");
	private final static Table sEnulls = new ColumnarTable(Double.class, "x");
	private final static Table sFnulls = new ColumnarTable(Double.class, "x");
	static {
		sD.setTitle("sD");
		sE.setTitle("sE");
		sF.setTitle("sF");
		sDnulls.setTitle("sDnulls");
		sEnulls.setTitle("sEnulls");
		sFnulls.setTitle("sFnulls");
		for (double i = 0; i < 4; i++) {
			sD.getRows().addRow(i);
			sE.getRows().addRow(i + 2);
			sF.getRows().addRow(i - 2);
			sDnulls.getRows().addRow(i == 1 ? null : i);
			sEnulls.getRows().addRow(i + 2 == 5 ? null : i + 2);
			sFnulls.getRows().addRow(i - 2 == -1 ? null : i - 2);
		}
	}
	private final static Table sine = new ColumnarTable(Integer.class, "n", Double.class, "x");
	private final static Table cosine = new ColumnarTable(Integer.class, "n", Double.class, "x");
	private final static int trigFuncLen = 100;
	private final static double trigFuncDiscrete2ContinuousTime = 2 * Math.PI / trigFuncLen;
	static {
		sine.setTitle("sine");
		cosine.setTitle("cosine");
		for (int i = 0; i < trigFuncLen; i++) {
			sine.getRows().addRow(i, Math.sin(2 * Math.PI * i / trigFuncLen));
			cosine.getRows().addRow(i, Math.cos(2 * Math.PI * i / trigFuncLen));
		}
	}
	private final static Random rngSeeded = new Random(123);
	private static Table smallLowHighCardA = new ColumnarTable();
	private static Table smallLowHighCardB = new ColumnarTable();
	static {
		smallLowHighCardA.addColumn(Integer.class, "mostly_zeros");
		smallLowHighCardA.addColumn(Integer.class, "ones");
		smallLowHighCardB.addColumn(Integer.class, "mostly_zeros");
		smallLowHighCardB.addColumn(Integer.class, "ones");
		smallLowHighCardA.setTitle("A");
		smallLowHighCardB.setTitle("B");
		Row rowA;
		Row rowB;
		for (int i = 0; i < 10; i++) {
			rowA = smallLowHighCardA.newEmptyRow();
			rowB = smallLowHighCardB.newEmptyRow();
			rowA.putAt(0, rngSeeded.nextInt(10) == 0 ? 1 : 0);
			rowB.putAt(0, rngSeeded.nextInt(10) == 0 ? 1 : 0);
			rowA.putAt(1, 1);
			rowB.putAt(1, 1);
			smallLowHighCardA.getRows().add(rowA);
			smallLowHighCardB.getRows().add(rowB);
		}
	}
	private final static Table keywordTestTable = new ColumnarTable(Integer.class, "x", Integer.class, "y", Integer.class, "z");
	static {
		keywordTestTable.setTitle("ktt");
		keywordTestTable.getRows().addRow(1, 4, 7);
		keywordTestTable.getRows().addRow(2, 5, 8);
		keywordTestTable.getRows().addRow(3, 6, 9);
	}
	private final static Table empty = new ColumnarTable(Integer.class, "n", Double.class, "x", Double.class, "y", Double.class, "z");
	private final static Table empty2 = new ColumnarTable(Integer.class, "n", Double.class, "x", Double.class, "y", Double.class, "z");
	static {
		empty.setTitle("empty");
		empty2.setTitle("empty2");
	}
	private final static Table groups = new ColumnarTable(Integer.class, "g");
	static {
		groups.setTitle("groups");
		for (int i = 0; i < 5; i++) {
			groups.getRows().addRow(i);
		}
	}
	private final static Table limitTest = new ColumnarTable(Integer.class, "m", Integer.class, "n");
	static {
		limitTest.setTitle("limitTest");
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 20; j++) {
				limitTest.getRows().addRow(i, j);
			}
		}
	}
	private final static Table tA = new ColumnarTable(Integer.class, "n", Double.class, "x", Double.class, "y");
	private final static Table tB = new ColumnarTable(Integer.class, "n", Double.class, "x", Double.class, "y");
	private final static Table tC = new ColumnarTable(Integer.class, "n", Double.class, "x", Double.class, "y");
	static {
		tA.setTitle("tA");
		tB.setTitle("tB");
		tC.setTitle("tC");
		int N = 5;
		for (int i = 0; i < 10; i++) {
			tA.getRows().addRow(i, (double) rngSeeded.nextInt(N), (double) rngSeeded.nextInt(N));
			tB.getRows().addRow(i, (double) rngSeeded.nextInt(N), (double) rngSeeded.nextInt(N));
			tC.getRows().addRow(i, (double) rngSeeded.nextInt(N), (double) rngSeeded.nextInt(N));
		}
	}
	private final static Table allTypesTbl = new ColumnarTable(Integer.class, "intCol", Long.class, "longCol", Float.class, "floatCol", Double.class, "doubleCol", Boolean.class,
			"booleanCol", Character.class, "charCol", Byte.class, "byteCol", Short.class, "shortCol");
	static {
		allTypesTbl.setTitle("allTypesTbl");
		allTypesTbl.getRows().addRow(1, 123L, 12.3f, 12.3, true, 'c', (byte) 123, (short) 123);
		allTypesTbl.getRows().addRow(null, null, null, null, null, null, null, null);
		allTypesTbl.getRows().addRow(789, 234543342532452436L, 423.234253f, 98708.235432, false, 'd', (byte) 1234, (short) 1234);
	}
	private final static Table groupByMulti = new ColumnarTable(Integer.class, "m", Integer.class, "n", Integer.class, "k", Double.class, "x", Double.class, "y");
	static {
		groupByMulti.setTitle("groupByMulti");
		int N = 100;
		int maxBucket = 4;
		for (int i = 0; i < N; i++) {
			groupByMulti.getRows().addRow(rngSeeded.nextInt(maxBucket), rngSeeded.nextInt(maxBucket), rngSeeded.nextInt(maxBucket), rngSeeded.nextDouble(), rngSeeded.nextDouble());
		}
	}
	private final static Table tD = new ColumnarTable(Integer.class, "n", Double.class, "x", Double.class, "y");
	private final static Table tE = new ColumnarTable(Integer.class, "n", Double.class, "x", Double.class, "y");
	private final static Table tF = new ColumnarTable(Integer.class, "n", Double.class, "x", Double.class, "y");
	static {
		tD.setTitle("tD");
		tE.setTitle("tE");
		tF.setTitle("tF");
		int N = 5;
		for (int i = 0; i < 30; i++) {
			tD.getRows().addRow(i, (double) rngSeeded.nextInt(N), (double) rngSeeded.nextInt(N));
			tE.getRows().addRow(i, (double) rngSeeded.nextInt(N), (double) rngSeeded.nextInt(N));
			tF.getRows().addRow(i, (double) rngSeeded.nextInt(N), (double) rngSeeded.nextInt(N));
		}
	}
	private final static Table nearestLarge = new ColumnarTable(Long.class, "n");
	private final static Table nearestSmall = new ColumnarTable(Long.class, "n");
	static {
		Long nullLong = null;
		nearestLarge.setTitle("large");
		nearestSmall.setTitle("small");
		nearestLarge.getRows().addRow(nullLong);
		nearestLarge.getRows().addRow(0L);
		nearestLarge.getRows().addRow(1L);
		nearestLarge.getRows().addRow(2L);
		nearestLarge.getRows().addRow(3L);
		nearestLarge.getRows().addRow(4L);
		nearestLarge.getRows().addRow(5L);
		nearestLarge.getRows().addRow(6L);
		nearestLarge.getRows().addRow(7L);
		nearestLarge.getRows().addRow(8L);
		nearestLarge.getRows().addRow(9L);
		nearestLarge.getRows().addRow(10L);
		nearestLarge.getRows().addRow(11L);
		nearestLarge.getRows().addRow(12L);
		nearestLarge.getRows().addRow(13L);
		nearestLarge.getRows().addRow(14L);

		nearestSmall.getRows().addRow(nullLong);
		nearestSmall.getRows().addRow(0L);
		nearestSmall.getRows().addRow(3L);
		nearestSmall.getRows().addRow(6L);
		nearestSmall.getRows().addRow(9L);
		nearestSmall.getRows().addRow(12L);
	}
	private final static Table nearestLargeId = new ColumnarTable(Integer.class, "id", Long.class, "n");
	private final static Table nearestSmallId = new ColumnarTable(Integer.class, "id", Long.class, "n");
	static {
		Long nullLong = null;
		nearestLargeId.setTitle("large");
		nearestSmallId.setTitle("small");
		int numIds = 5;
		for (int i = 0; i < numIds; i++) {
			nearestLargeId.getRows().addRow(i, nullLong);
			for (long l = 0; l < 15; l++) {
				nearestLargeId.getRows().addRow(i, l);
			}
		}
		for (int i = 0; i > -numIds; i--) {
			nearestSmallId.getRows().addRow(i, nullLong);
			for (long l = 0; l < 15; l += 3) {
				nearestSmallId.getRows().addRow(i, l);
			}
		}
	}
	private static final String EMPTY_TABLE_INSERT_TEST_VALS = "(2.3), (5.6), (0.9), (1.4), (1), (2), (0), (3f), (3F), (3d), (3D), (3l), (3L), (\'c\'), (\'z\'), (\"xyz\");";
	private final static Table intsEmpty = new ColumnarTable(Integer.class, "x");
	private final static Table longsEmpty = new ColumnarTable(Long.class, "x");
	private final static Table floatsEmpty = new ColumnarTable(Float.class, "x");
	private final static Table doublesEmpty = new ColumnarTable(Double.class, "x");
	private final static Table booleansEmpty = new ColumnarTable(Boolean.class, "x");
	private final static Table charsEmpty = new ColumnarTable(Character.class, "x");
	private final static Table stringsEmpty = new ColumnarTable(String.class, "x");
	private final static Table bytesEmpty = new ColumnarTable(Byte.class, "x");
	private final static Table shortsEmpty = new ColumnarTable(Short.class, "x");
	static {
		intsEmpty.setTitle("intsEmpty");
		longsEmpty.setTitle("longsEmpty");
		floatsEmpty.setTitle("floatsEmpty");
		doublesEmpty.setTitle("doublesEmpty");
		booleansEmpty.setTitle("booleansEmpty");
		charsEmpty.setTitle("charsEmpty");
		stringsEmpty.setTitle("stringsEmpty");
		bytesEmpty.setTitle("bytesEmpty");
		shortsEmpty.setTitle("shortsEmpty");
	}

	// limit test on prepare clause.
	private final static Table prep = new ColumnarTable(String.class, "fname", String.class, "lname", Integer.class, "age");
	static {
		prep.setTitle("prep");
		prep.getRows().addRow("mir", "ahmed", 23);
		prep.getRows().addRow("peter", "sibirzeff", 34);
		prep.getRows().addRow("george", "lin", 26);
		prep.getRows().addRow("david", "lee", 35);
		prep.getRows().addRow("marc", "weinstein", 48);
		prep.getRows().addRow("robert", "cooke", 27);
		prep.getRows().addRow("bill", "cooke", 39);
	}

	private final static Map<String, Object> globalVars = new LinkedHashMap<String, Object>();

	public static void main(String a[]) throws IOException {
		try {
			IOH.ensureDir(root);

			Table A = new ColumnarTable(Integer.class, "n");
			Table B = new ColumnarTable(Integer.class, "n");
			generateNearestJoinTest("id", "id", JOIN_TYPE_LEFT, false);
			generateNearestJoinTest("id", "id", JOIN_TYPE_LEFT, true);
			generateNearestJoinTest("id", "id", JOIN_TYPE_INNER, false);
			generateNearestJoinTest("id", "id", JOIN_TYPE_INNER, true);
			generateNearestJoinTest("id", "id", JOIN_TYPE_LEFT_ONLY, false);
			generateNearestJoinTest("id", "id", JOIN_TYPE_LEFT_ONLY, true);

		} catch (ExpressionParserException e) {
			System.out.println("At " + e.getPosition() + " in : " + e.getExpression());
			System.err.println(SH.printStackTrace(e));
		}
	}
	private static void clearTestEnvironment() {
		globalVars.clear();
		testTableset.clearTables();
	}
	private void runMiscTests() throws IOException {

		// END STRUCTURED TESTS // 

		testTableset.putTable(t);
		testTableset.putTable(t2);
		testTableset.putTable(t3);
		globalVars.put("abc", 3);
		globalVars.put("n", 3);
		globalVars.put("s", "t2");
		testTableset.removeTable("t7");

		globalVars.put("n", 7);
		clearTestEnvironment();
	}

	private static String JOIN_TYPE_INNER = "join";
	private static String JOIN_TYPE_LEFT = "left join";
	private static String JOIN_TYPE_LEFT_ONLY = "left only join";
	private static String JOIN_TYPE_RIGHT = "right join";
	private static String JOIN_TYPE_RIGHT_ONLY = "right only join";
	private static String JOIN_TYPE_OUTER = "outer join";
	private static String JOIN_TYPE_OUTER_ONLY = "outer only join";

	private static void generateNearestJoinTest(String leftJoinColName, String rightJoinColName, String joinType, boolean joinOnIds) throws IOException {

		if (joinOnIds) {
			testTableset.putTable(nearestLargeId);
			testTableset.putTable(nearestSmallId);
		} else {
			testTableset.putTable(nearestLarge);
			testTableset.putTable(nearestSmall);
		}
		String testName = "test_sql_nearest_join_" + SH.replaceAll(JOIN_TYPE_INNER.equals(joinType) ? "inner" : SH.beforeFirst(joinType, " join"), " ", "_")
				+ (joinOnIds ? "_ids" : "");

		String joinClause = joinOnIds ? "large." + leftJoinColName + " == small." + rightJoinColName : "true";
		boolean isRightJoin = JOIN_TYPE_RIGHT.equals(joinType) || JOIN_TYPE_RIGHT_ONLY.equals(joinType);
		String query = "create table gte" + (isRightJoin ? "S" : "L") + " as select * from large " + joinType + " small on " + joinClause + " nearest large.n >= small.n order by "
				+ (joinOnIds ? "large." + leftJoinColName + ", " : "") + "large.n;" + SH.NEWLINE +

				"create table gt" + (isRightJoin ? "S" : "L") + " as select * from large " + joinType + " small on " + joinClause + " nearest large.n > small.n order by "
				+ (joinOnIds ? "large." + leftJoinColName + ", " : "") + "large.n;" + SH.NEWLINE +

				"create table lte" + (isRightJoin ? "S" : "L") + " as select * from large " + joinType + " small on " + joinClause + " nearest large.n <= small.n order by "
				+ (joinOnIds ? "large." + leftJoinColName + ", " : "") + "large.n;" + SH.NEWLINE +

				"create table lt" + (isRightJoin ? "S" : "L") + " as select * from large " + joinType + " small on " + joinClause + " nearest large.n < small.n order by "
				+ (joinOnIds ? "large." + leftJoinColName + ", " : "") + "large.n;" + SH.NEWLINE +

				"create table gte" + (isRightJoin ? "L" : "S") + " as select * from small " + joinType + " large on " + joinClause + " nearest small.n >= large.n order by "
				+ (joinOnIds ? "small." + leftJoinColName + ", " : "") + "small.n;" + SH.NEWLINE +

				"create table gt" + (isRightJoin ? "L" : "S") + " as select * from small " + joinType + " large on " + joinClause + " nearest small.n > large.n order by "
				+ (joinOnIds ? "small." + leftJoinColName + ", " : "") + "small.n;" + SH.NEWLINE +

				"create table lte" + (isRightJoin ? "L" : "S") + " as select * from small " + joinType + " large on " + joinClause + " nearest small.n <= large.n order by "
				+ (joinOnIds ? "small." + leftJoinColName + ", " : "") + "small.n;" + SH.NEWLINE +

				"create table lt" + (isRightJoin ? "L" : "S") + " as select * from small " + joinType + " large on " + joinClause + " nearest small.n < large.n order by "
				+ (joinOnIds ? "small." + leftJoinColName + ", " : "") + "small.n;";

		test(root, testName, sp, query, testTableset, newVarset());
	}
	private static void generateUpdateDeleteJoinLimitTest(Table left, Table right, String leftJoinColName, String rightJoinColName, boolean update, Integer skip, int max,
			String joinType, String whereClause, boolean exceedLimit) throws IOException {
		testTableset.putTable("A", left);
		testTableset.putTable("B", right);

		boolean noWhereClause = SH.isnt(whereClause);
		boolean noSkipDelete = skip == null;
		String query = (update ? "update" : "delete from") + " A " + joinType + " B on A." + leftJoinColName + " == B." + rightJoinColName
				+ (update ? " set " + leftJoinColName + "=999 " : " ") + (noWhereClause ? "" : whereClause) + " limit " + (noSkipDelete ? "" : skip + ", ") + max + ";";
		String testName = "test_sql_" + (update ? "update" : "delete") + "_" + SH.replaceAll((JOIN_TYPE_INNER.equals(joinType) ? "inner_join" : joinType), " ", "_") + "_limit"
				+ (noSkipDelete ? "" : "_skip") + (noWhereClause ? "" : "_where_clause") + (exceedLimit ? "_exceed_limit" : "");
		test(root, testName, sp, query, testTableset, newVarset());
	}
	//	}
	@Test
	public void testEquality() throws IOException {
		test(root, "test_equality", sp,
				"select 1==1 as t1,1000==1000L as t2,10d==10 as t3,10f==10 as t5,10==\"10\" as t6,10d==\"10.0\" as t7,10f==\"10.0\" as t8,\"true\"==true as t9,\"true\"==false as t10,\"null\"==null as t11",
				testTableset, globalVars);
	}
	@Test
	public void testOrs() throws IOException {
		testTableset.putTable(t);
		test(root, "test_ors", sp, "create table out as select * from t where v==3 || v==4 || v==5 || v==6 || v==7 || v==1.0 || v==5 || v==15 || v==\"9.0\" || v==null",
				testTableset, globalVars);
	}
	@Test
	public void testPrepareClauseWithLimit() throws IOException {
		testTableset.putTable(prep);
		String testSqlString = "{create public table preptest1 as prepare fname, lname, age from prep limit;"
				+ "create public table preptest2 as prepare fname, lname, age from prep limit 0;" + "create public table preptest3 as prepare fname, lname, age from prep limit 3;"
				+ "create public table preptest4 as prepare fname, lname, age from prep limit 2, 0;"
				+ "create public table preptest5 as prepare fname, lname, age from prep limit 2, 1;"
				+ "create public table preptest6 as prepare fname, lname, age from prep limit 4, 3;"
				+ "create public table preptest7 as prepare fname, lname, age from prep limit 4, 4;}";
		test(root, "test_prepare_with_limit", sp, testSqlString, testTableset, globalVars);
	}
	@Test
	public void testMisc13() throws IOException {
		test(root, "test13", sp, "{create table ts1 (x double);create table ts2 (x double);for(int i=1;i<10;i++) insert into ts2 values(i);}", testTableset, globalVars);
	}
	@Test
	public void testMisc12() throws IOException {
		testTableset.putTable(t3);
		globalVars.put("n", 3);
		globalVars.put("abc", 3);
		test(root, "test12", sp, "{select n=v as t from t3 where v==abc;}", testTableset, globalVars);
	}
	@Test
	public void testMisc11() throws IOException {
		testTableset.putTable(t3);
		globalVars.put("n", 3);
		test(root, "test11", sp, "select v as t from t3 where (v) in (1,n)", testTableset, globalVars);
	}
	@Test
	public void testMisc10() throws IOException {
		testTableset.putTable(t3);
		test(root, "test10", sp, "{select v as t from t3 where (v) in (select v+1 from t3);}", testTableset, globalVars);
	}
	@Test
	public void testMisc9() throws IOException {
		// TODO: This test gives incorrect results
		testTableset.putTable(t2);
		globalVars.put("s", "t2");
		test(root, "test9", sp, "select * from ${s} where true;", testTableset, globalVars);
	}
	@Test
	public void testComments() throws IOException {
		test(root, "test_comments", sp,
				"{\n // Single-line comment \n int x = 3; \n int y = 4; // End of line comment \n int z = 5; \n /*\n * Block \n* Comment \n */ \n return x * y * z;}",
				newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testSqlMisc7() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_misc7", sp, "{select * from tableA where x in (1,3);}", testTableset, globalVars);
	}
	@Test
	public void testSqlMisc6() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_misc6", sp, "{select x as t from tableA where x in (1,3);}", testTableset, globalVars);
	}
	@Test
	public void testSqlMisc5() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_misc5", sp, "{select x as t from tableA where (x) in (1,3);}", testTableset, globalVars);
	}
	@Test
	public void testSqlMisc4() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(tableC);
		test(root, "test_sql_misc4", sp, "{select y as t from tableA where (y) in (select a+1 from tableC);}", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlMisc3() throws IOException {
		testTableset.putTable(tableA);
		globalVars.put("n", 3);
		test(root, "test_sql_misc3", sp, "{select x as t from tableA where (x) in (1,n);}", testTableset, globalVars);
	}
	@Test
	public void testSqlMisc2() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(tableC);
		test(root, "test_sql_misc2", sp, "{select y as t from tableA where (y) in (select a+1 from tableC);}", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlMisc1() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(tableF);
		test(root, "test_sql_misc1", sp, "{select y as t from tableA where (y) in (select b-1 from tableF);}", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlSelectStrTemplate() throws IOException {
		// TODO: incorrect result
		testTableset.putTable(tableA);
		test(root, "test_sql_select_str_template", sp, "String s=\"tableA\";select * from ${s} where true;", testTableset, new HashMap<String, Object>()); // THIS WORKS IN AMI BUT NOT IN TEST ENVIRONMENT
	}
	@Test
	public void testSqlJoinOuterOnlyArithmetic() throws IOException {
		testTableset.putTable(tableC);
		testTableset.putTable(tableD);
		test(root, "test_sql_join_outer_only_arithmetic", sp,
				"SELECT a + f AS sum, a - f AS diff, a * f AS prod, a / f AS ratio, a % f AS rem FROM tableC OUTER ONLY JOIN tableD ON tableD.d==tableC.a", testTableset,
				new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinLeftOnlyArithmetic() throws IOException {
		testTableset.putTable(tableC);
		testTableset.putTable(tableD);
		test(root, "test_sql_join_left_only_arithmetic", sp,
				"SELECT a + f AS sum, a - f AS diff, a * f AS prod, a / f AS ratio, a % f AS rem FROM tableC LEFT ONLY JOIN tableD ON tableD.d==tableC.a", testTableset,
				new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinRightOnlyArithmetic() throws IOException {
		testTableset.putTable(tableC);
		testTableset.putTable(tableD);
		test(root, "test_sql_join_right_only_arithmetic", sp,
				"SELECT a + f AS sum, a - f AS diff, a * f AS prod, a / f AS ratio, a % f AS rem FROM tableC RIGHT ONLY JOIN tableD ON tableD.d==tableC.a", testTableset,
				new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinOuterArithmetic() throws IOException {
		testTableset.putTable(tableC);
		testTableset.putTable(tableD);
		test(root, "test_sql_join_outer_arithmetic", sp,
				"SELECT a + f AS sum, a - f AS diff, a * f AS prod, a / f AS ratio, a % f AS rem FROM tableC OUTER JOIN tableD ON tableD.d==tableC.a", testTableset,
				new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinLeftArithmetic() throws IOException {
		testTableset.putTable(tableC);
		testTableset.putTable(tableD);
		test(root, "test_sql_join_left_arithmetic", sp,
				"SELECT a + f AS sum, a - f AS diff, a * f AS prod, a / f AS ratio, a % f AS rem FROM tableC LEFT JOIN tableD ON tableD.d==tableC.a", testTableset,
				new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinRightArithmetic() throws IOException {
		testTableset.putTable(tableC);
		testTableset.putTable(tableD);
		test(root, "test_sql_join_right_arithmetic", sp,
				"SELECT a + f AS sum, a - f AS diff, a * f AS prod, a / f AS ratio, a % f AS rem FROM tableC RIGHT JOIN tableD ON tableD.d==tableC.a", testTableset,
				new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinArithmetic() throws IOException {
		testTableset.putTable(tableC);
		testTableset.putTable(tableD);
		test(root, "test_sql_join_arithmetic", sp, "SELECT a + f AS sum, a - f AS diff, a * f AS prod, a / f AS ratio, a % f AS rem FROM tableC JOIN tableD ON tableD.d==tableC.a",
				testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinExcludeFirstRow() throws IOException {
		testTableset.putTable(tableC);
		testTableset.putTable(tableD);
		test(root, "test_sql_join_exclude_first_row", sp, "SELECT * FROM tableC JOIN tableD ON tableD.d==tableC.a WHERE e != " + eTest, testTableset,
				new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinOuterOnlySelect() throws IOException {
		testTableset.putTable(tableC);
		testTableset.putTable(tableD);
		test(root, "test_sql_join_outer_only_select", sp, "SELECT * FROM tableC OUTER ONLY JOIN tableD ON tableD.d==tableC.a", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinLeftOnlySelect() throws IOException {
		testTableset.putTable(tableC);
		testTableset.putTable(tableD);
		test(root, "test_sql_join_left_only_select", sp, "SELECT * FROM tableC LEFT  ONLY JOIN tableD ON tableD.d==tableC.a", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinRightOnlySelect() throws IOException {
		testTableset.putTable(tableC);
		testTableset.putTable(tableD);
		test(root, "test_sql_join_right_only_select", sp, "SELECT * FROM tableC RIGHT ONLY JOIN tableD ON tableD.d==tableC.a", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinOuterSelect() throws IOException {
		testTableset.putTable(tableC);
		testTableset.putTable(tableD);
		test(root, "test_sql_join_outer_select", sp, "SELECT * FROM tableC OUTER      JOIN tableD ON tableD.d==tableC.a", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinLeftSelect() throws IOException {
		testTableset.putTable(tableC);
		testTableset.putTable(tableD);
		test(root, "test_sql_join_left_select", sp, "SELECT * FROM tableC LEFT       JOIN tableD ON tableD.d==tableC.a", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinRightSelect() throws IOException {
		testTableset.putTable(tableC);
		testTableset.putTable(tableD);
		test(root, "test_sql_join_right_select", sp, "SELECT * FROM tableC RIGHT      JOIN tableD ON tableD.d==tableC.a", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinSelect() throws IOException {
		testTableset.putTable(tableC);
		testTableset.putTable(tableD);
		test(root, "test_sql_join_select", sp, "SELECT * FROM tableC            JOIN tableD ON tableD.d==tableC.a", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinOuterOnlyAll() throws IOException {
		testTableset.putTable(tableC);
		testTableset.putTable(tableD);
		test(root, "test_sql_join_outer_only_all", sp, "SELECT * FROM tableC OUTER ONLY JOIN tableD ON true", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinOuterOnlyAll2() throws IOException {
		testTableset.putTable(tableC);
		testTableset.putTable(tableD);
		test(root, "test_sql_join_outer_only_all2", sp, "SELECT * FROM tableD OUTER ONLY JOIN tableC ON true", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinLeftOnlyAll() throws IOException {
		testTableset.putTable(tableC);
		testTableset.putTable(tableD);
		test(root, "test_sql_join_left_only_all", sp, "SELECT * FROM tableC LEFT  ONLY JOIN tableD ON true", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinRightOnlyAll() throws IOException {
		testTableset.putTable(tableC);
		testTableset.putTable(tableD);
		test(root, "test_sql_join_right_only_all", sp, "SELECT * FROM tableC RIGHT ONLY JOIN tableD ON true", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinOuterAll() throws IOException {
		testTableset.putTable(tableC);
		testTableset.putTable(tableD);
		test(root, "test_sql_join_outer_all", sp, "SELECT * FROM tableC OUTER      JOIN tableD ON true", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinLeftAll() throws IOException {
		testTableset.putTable(tableC);
		testTableset.putTable(tableD);
		test(root, "test_sql_join_left_all", sp, "SELECT * FROM tableC LEFT       JOIN tableD ON true", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinRightAll() throws IOException {
		testTableset.putTable(tableC);
		testTableset.putTable(tableD);
		test(root, "test_sql_join_right_all", sp, "SELECT * FROM tableC RIGHT      JOIN tableD ON true", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinAll() throws IOException {
		testTableset.putTable(tableC);
		testTableset.putTable(tableD);
		test(root, "test_sql_join_all", sp, "SELECT * FROM tableC            JOIN tableD ON true", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlLeftJoinCascaded() throws IOException {
		Table Orders = new ColumnarTable(String.class, "orderID", String.class, "productID");
		Orders.getRows().addRow("oid1", "p1");
		Orders.getRows().addRow("oid2", "p1");
		Orders.getRows().addRow("oid3", "p2");
		Orders.getRows().addRow("oid4", "p2");
		Orders.getRows().addRow("oid5", "p3");
		Table Products = new ColumnarTable(String.class, "bloombergID", String.class, "productID", String.class, "description");
		Products.getRows().addRow("bloom1", "p1", "product1");
		Products.getRows().addRow("bloom2", "p2", "product2");
		Products.getRows().addRow("bloom3", "p3", "product3");
		Products.getRows().addRow("bloom4", "p4", "product4");
		Table MarketData = new ColumnarTable(String.class, "bloombergID", Double.class, "bid", Double.class, "ask");
		MarketData.getRows().addRow("bloom1", 1.15d, 1.16d);
		MarketData.getRows().addRow("bloom2", 2.15d, 2.16d);
		MarketData.getRows().addRow("bloom3", 3.15d, 3.16d);
		MarketData.getRows().addRow("bloom4", 4.15d, 4.16d);
		MarketData.getRows().addRow("bloom5", 5.15d, 5.16d);
		putTable("Orders", Orders);
		putTable("Products", Products);
		putTable("MarketData", MarketData);
		test(root, "test_sql_left_join_cascaded", sp,
				"CREATE TABLE out AS SELECT t.orderID AS orderID, t.bloombergID AS bloombergID, t.description AS description, m.bid AS bid, m.ask AS ask from (SELECT * FROM Orders AS o LEFT JOIN Products AS p ON o.productID == p.productID) AS t LEFT JOIN MarketData AS m ON t.bloombergID == m.bloombergID;",
				testTableset, newVarset());
	}
	@Test
	public void testSqlSelectNotNull() throws IOException {
		testTableset.putTable(nullTableA);
		test(root, "test_sql_select_not_null", sp, "SELECT * FROM nullTableA WHERE a != null", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlSelectNull() throws IOException {
		testTableset.putTable(nullTableA);
		test(root, "test_sql_select_null", sp, "SELECT * FROM nullTableA WHERE a == null", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinAllNull() throws IOException {
		testTableset.putTable(nullTableA);
		testTableset.putTable(nullTableB);
		test(root, "test_sql_join_all_null", sp, "SELECT * FROM nullTableA JOIN nullTableB ON true", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAnalyze() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_analyze", sp,
				"ANALYZE n, sum(win.x), min(win.x), max(win.x), count(win.x), countUnique(win.x), cat(win.x, \"@\", 3), avg(win.x), var(win.x), varS(win.x), stdev(win.x), stdevS(win.x), first(win.x), last(win.x), covar(win.x, win.y), covarS(win.x, win.y), cor(win.x, win.y), beta(win.x, win.y) FROM analyzeInput WINDOW win ON n - 4 <= win.n && win.n < n + 4;",
				testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAnalyzeGroups() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_analyze_groups", sp,
				"create table groups(g int); int nGroups = 5; for (int i = 0; i < nGroups; i++) { insert into groups values(i); } create table input_marked as select * from analyzeInput, groups; create table test2 as analyze  g,  n,  sum(win.x), min(win.x), max(win.x), count(win.x), countUnique(win.x), cat(win.x, \"@\", 3), avg(win.x), var(win.x), varS(win.x), stdev(win.x), stdevS(win.x), first(win.x), last(win.x), covar(win.x, win.y), covarS(win.x, win.y), cor(win.x, win.y), beta(win.x, win.y), avg(win.x) as avg2 from input_marked window win on n - g <= win.n && win.n < n + g partition by g;",
				testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAnalyzeNullTimes() throws IOException {
		testTableset.putTable(analyzeInputNullTimes);
		test(root, "test_sql_analyze_null_times", sp,
				"create table testNullTimesAnalyze as analyze n, sum(win.x), min(win.x), max(win.x), count(win.x), countUnique(win.x), cat(win.x, \"@\", 3), avg(win.x), var(win.x), varS(win.x), stdev(win.x), stdevS(win.x), first(win.x), last(win.x), covar(win.x, win.y), covarS(win.x, win.y), cor(win.x, win.y), beta(win.x, win.y) from analyzeInputNullTimes window win on n - 4 <= win.n && win.n < n + 4;",
				testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAnalyzeDuplicateTimes() throws IOException {
		testTableset.putTable(analyzeInputDuplicateTimes);
		test(root, "test_sql_analyze_duplicate_times", sp,
				"create table testDuplicateTimesAnalyze as analyze n, sum(win.x), min(win.x), max(win.x), count(win.x), countUnique(win.x), cat(win.x, \"@\", 3), avg(win.x), var(win.x), varS(win.x), stdev(win.x), stdevS(win.x), first(win.x), last(win.x), covar(win.x, win.y), covarS(win.x, win.y), cor(win.x, win.y), beta(win.x, win.y) from analyzeInputDuplicateTimes window win on n - 4 <= win.n && win.n < n + 4;",
				testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAnalyzeLtLt() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_analyze_lt_lt", sp, "analyze n, avg(win.x), var(win.y), covar(win.x, win.y) from analyzeInput window win on n - 4 < win.n && win.n < n + 4;",
				testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAnalyzeLtLte() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_analyze_lt_lte", sp, "analyze n, avg(win.x), var(win.y), covar(win.x, win.y) from analyzeInput window win on n - 4 < win.n && win.n <= n + 4;",
				testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAnalyzeLteLt() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_analyze_lte_lt", sp, "analyze n, avg(win.x), var(win.y), covar(win.x, win.y) from analyzeInput window win on n - 4 <= win.n && win.n < n + 4;",
				testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAnalyzeLteLte() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_analyze_lte_lte", sp, "analyze n, avg(win.x), var(win.y), covar(win.x, win.y) from analyzeInput window win on n - 4 <= win.n && win.n <= n + 4;",
				testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAnalyzeGtGt() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_analyze_gt_gt", sp, "analyze n, avg(win.x), var(win.y), covar(win.x, win.y) from analyzeInput window win on n + 4 > win.n && win.n > n - 4;",
				testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAnalyzeGtGte() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_analyze_gt_gte", sp, "analyze n, avg(win.x), var(win.y), covar(win.x, win.y) from analyzeInput window win on n + 4 > win.n && win.n >= n - 4;",
				testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAnalyzeGteGt() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_analyze_gte_gt", sp, "analyze n, avg(win.x), var(win.y), covar(win.x, win.y) from analyzeInput window win on n + 4 >= win.n && win.n > n - 4;",
				testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAnalyzeGteGte() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_analyze_gte_gte", sp, "analyze n, avg(win.x), var(win.y), covar(win.x, win.y) from analyzeInput window win on n + 4 >= win.n && win.n >= n - 4;",
				testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAnalyze2Winows() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_analyze_2_windows", sp,
				"analyze n, avg(win.x), var(win.y), covar(win.x, win.y) , covar(win2.x, win2.y) from analyzeInput window win on n + 4 >= win.n && win.n >= n - 4 window win2 on n + 6 >= win2.n && win2.n >= n - 6;",
				testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAnalyze2Winows2() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_analyze_2_windows2", sp,
				"analyze n, avg(win.x), var(win.y), covar(win.x, win.y) , covar(win2.x, win2.y) from analyzeInput window win on n + 4 >= win.n && win.n >= n - 4 partition by false order by true window win2 on n + 6 >= win2.n && win2.n >= n - 6;",
				testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAnalyzeEq1() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_analyze_eq1", sp, "analyze n, x, sum(win.x) as sum_x from analyzeInput window win on win.n == n;", testTableset, newVarset());
	}
	@Test
	public void testSqlAnalyzeEq2() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_analyze_eq2", sp, "analyze n, x, sum(win.x) as sum_x from analyzeInput window win on n == win.n;", testTableset, newVarset());
	}
	@Test
	public void testSqlAnalyzePartitions() throws IOException {
		testTableset.putTable(analyzePartitionsInput);
		test(root, "test_sql_analyze_partitions", sp,
				"create table test as analyze g, n, x, y, sum(win.x), sum(win.y) from analyze_partitions_input window win on n - 3 < win.n && win.n <= n partition by g;",
				testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAggBeta() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_agg_beta", sp, "SELECT beta(x,y) FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAggCor() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_agg_cor", sp, "SELECT cor(x,y) FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAggCovar() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_agg_covar", sp, "SELECT covar(x,y) FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAggCovarS() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_agg_covarS", sp, "SELECT covarS(x,y) FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAggLast() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_agg_last", sp, "SELECT last(x) FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAggFirst() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_agg_first", sp, "SELECT first(x) FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAggStdev() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_agg_stdev", sp, "SELECT stdev(x) FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAggStdevS() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_agg_stdevS", sp, "SELECT stdevS(x) FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAggVar() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_agg_var", sp, "SELECT var(x) FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAggVarS() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_agg_varS", sp, "SELECT varS(x) FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAggAvg() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_agg_avg", sp, "SELECT avg(x) FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAggCat() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_agg_cat", sp, "SELECT cat(x, \"|\", 5) FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAggCountUnique() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_agg_countUnique", sp, "SELECT countUnique(x) FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAggCount() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_agg_count", sp, "SELECT count(x) FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAggMax() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_agg_max", sp, "SELECT max(x) FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAggMin() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_agg_min", sp, "SELECT min(x) FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAggSum() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_agg_sum", sp, "SELECT sum(x) FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlUnpack() throws IOException {
		testTableset.putTable(tableE);
		test(root, "test_sql_unpack", sp, "SELECT * FROM tableE UNPACK abc ON \"|\", def ON \"#\", xyz ON \"%\";", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlInClause() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_in_clause", sp, "SELECT * FROM tableA WHERE x IN (9, 3, 4);", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlGroupByHaving() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_group_by_having", sp, "SELECT count(x), * FROM tableA GROUP BY x HAVING x < 5;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlGroupBy() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_group_by", sp, "SELECT count(x), * FROM tableA GROUP BY x;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlPrepareUnique() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_prepare_unique", sp, "PREPARE unique(x) FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlPrepareUrank() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_prepare_urank", sp, "PREPARE urank(x) FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlPrepareRank() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_prepare_rank", sp, "PREPARE rank(x) FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlPrepareDnorm() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_prepare_dnorm", sp, "PREPARE dnorm(x) FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlPrepareNorm() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_prepare_norm", sp, "PREPARE norm(x) FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlPrepareCount() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_prepare_count", sp, "PREPARE count(x) FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlPrepareStack() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_prepare_stack", sp, "PREPARE stack(x) FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlPrepareOrderByPartitionBy2() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_prepare_order_by_partition_by2", sp, "PREPARE * FROM tableA ORDER BY z PARTITION BY x;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlPrepareOrderByPartitionBy() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_prepare_order_by_partition_by", sp, "PREPARE * FROM tableA ORDER BY x PARTITION BY x;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlPreparePartitionBy() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_prepare_partition_by", sp, "PREPARE * FROM tableA PARTITION BY x;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlPrepareOrderBy() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_prepare_order_by", sp, "PREPARE * FROM tableA ORDER BY x;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlDropTable() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_drop_table", sp, "CREATE TABLE dummy AS SELECT * FROM tableA; DROP TABLE dummy;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlRenameTable() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_rename_table", sp, "CREATE TABLE output AS SELECT * FROM tableA; RENAME TABLE output TO OutPUt;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAlterModify() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_alter_modify", sp, "CREATE TABLE output AS SELECT * FROM tableA; ALTER TABLE output MODIFY z AS z INT;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAlterDrop() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_alter_drop", sp, "CREATE TABLE output AS SELECT * FROM tableA; ALTER TABLE output DROP y;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAlterRename() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_alter_rename", sp, "CREATE TABLE output AS SELECT * FROM tableA; ALTER TABLE output RENAME x TO a;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAlterAdd() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_alter_add", sp, "CREATE TABLE output AS SELECT * FROM tableA; ALTER TABLE output ADD w long;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlUpdate() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_update", sp, "CREATE TABLE output AS SELECT * FROM tableA; UPDATE output SET x = x * x;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlDeleteFrom() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_delete_from", sp, "CREATE TABLE output AS SELECT * FROM tableA; DELETE FROM output WHERE x % 2 == 0;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlInsertFrom() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_insert_from", sp, "CREATE TABLE output(a int, b int, c int); INSERT INTO output (a,c) FROM SELECT x,z FROM tableA;", testTableset,
				new HashMap<String, Object>());
	}
	@Test
	public void testSqlInsertIntoForLoop() throws IOException {
		test(root, "test_sql_insert_into_for_loop", sp, "int i;\nCREATE TABLE output(a int, b int, c int);\nINSERT INTO output FOR i = 0 to 10 STEP 2 VALUES (i, 2 * i, 3 * i);",
				testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlInsertIntoMultiple() throws IOException {
		test(root, "test_sql_insert_into_multiple", sp, "CREATE TABLE output(a int, b int, c int); INSERT INTO output VALUES (1, 2, 3), (4, 5, 6), (7, 8, 9);", testTableset,
				new HashMap<String, Object>());
	}
	@Test
	public void testSqlInsertInto() throws IOException {
		test(root, "test_sql_insert_into", sp, "CREATE TABLE output(a int, b int, c int); INSERT INTO output VALUES (1, 2, 3);", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlCreateFromTable() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_create_from_table", sp, "CREATE TABLE output AS SELECT * FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlCreateBlank() throws IOException {
		test(root, "test_sql_create_blank", sp, "CREATE TABLE output(a int, b int, c int);", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlSelectWhereNotNull() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_select_where_not_null", sp, "SELECT * FROM tableA WHERE null != null;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlSelectWhereNull() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_select_where_null", sp, "SELECT * FROM tableA WHERE null == null;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlLimit() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_limit", sp, "SELECT * FROM tableA LIMIT 3;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlSelectAll() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_select_all", sp, "SELECT * FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlSelect() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_select", sp, "SELECT x, z FROM tableA;", testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testTemplateNull() throws IOException {
		test(root, "test_template_null", sp, "{String s = null; return \"result: ${s}123\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testTemplateString() throws IOException {
		test(root, "test_template_string", sp, "{String s = \"xyz\"; return \"result: ${s}\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testTemplateSqlInject3() throws IOException {
		test(root, "test_template_sql_inject3", sp, "{int x = 5; return \"result: `${x}`\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testTemplateSqlInject2() throws IOException {
		test(root, "test_template_sql_inject2", sp, "{int x = 5; return \"result: \\\"${x}\\\"\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testTemplateSqlInject1() throws IOException {
		test(root, "test_template_sql_inject1", sp, "{int x = 5; return \"result: '${x}'\";}", newTableset(), new HashMap<String, Object>());
	}
	// TODO: test this once it's working: 	
	//			test(root, "test_template_literal_dollar_sign", sp, "{return \"test\\${stilltext}text\";}",  newTableset(), new HashMap<String, Object>());
	@Test
	public void testTemplate() throws IOException {
		test(root, "test_template", sp, "{int x = 5; return \"result: ${x}\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testFuncDef() throws IOException {
		test(root, "test_func_def", sp,
				"{\n double power(Double x, Integer exponent) { \n Double r = x; \n while (exponent-- > 1) { \n r *= x; \n } \n return r;\n }; return power(2.0, 5);}",
				newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testWhileContinue() throws IOException {
		test(root, "test_while_continue", sp, "{\n int x = 0; while (true) { x++; if (x < 10) continue; break;} return x;\n  }", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testWhileBreak() throws IOException {
		test(root, "test_while_break", sp, "{int x = 0; while (true) { x++; if (x > 10) break;} return x; }", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testWhileNoBrackets() throws IOException {
		test(root, "test_while_no_brackets", sp, "{int x = 0; while (x < 10) x++; return x; }", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testWhile() throws IOException {
		test(root, "test_while", sp, "{int x = 0; while (x < 10) { x++;} return x; }", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testDoWhile() throws IOException {
		Table t = new ColumnarTable(Integer.class, "x");
		putTable("t", t);
		test(root, "test_do_while", sp, "int x = 10; do { x-=2; insert into t values (x);} while (x > 0);", testTableset, newVarset());
	}
	@Test
	public void testForContinue() throws IOException {
		test(root, "test_for_continue", sp, "{int x = 0; for (;;) { x++; if (x < 10) continue; break;} return x; }", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testForBreak() throws IOException {
		test(root, "test_for_break", sp, "{int x = 0; for (;;) { x++; if (x > 10) break;} return x; }", newTableset(), new HashMap<String, Object>());
	}
	//@Test
	//public void testForEach() throws IOException {
	//	Table t = new ColumnarTable(Integer.class, "n");
	//	putTable("t", t);
	//	test(root, "test_for_each", sp, "{List nums = new List(2,3,5,7,11,13,17,19); for (int n:nums) { insert into t values (n); }}", testTableset, newVarset());
	//}
	@Test
	public void testForNoBrackets() throws IOException {
		test(root, "test_for_no_brackets", sp, "{int x = 0; for (int i = 0; i < 10; i++) x++; return x;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testFor() throws IOException {
		test(root, "test_for", sp, "{int x = 0; int y = 1; int z; for (int i = 0; i < 10; i++) {z = y; y = x + y; x = z;} return y;}", newTableset(),
				new HashMap<String, Object>());
	}
	@Test
	public void testIfElseFalseNoBrackets() throws IOException {
		test(root, "test_if_else_false_no_brackets", sp, "{if (false) return \"t\"; else return \"f\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testIfElseTrueNoBrackets() throws IOException {
		test(root, "test_if_else_true_no_brackets", sp, "{if (true) return \"t\"; else return \"f\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testIfElseFalse() throws IOException {
		test(root, "test_if_else_false", sp, "{if (false) { return \"t\";} else { return \"f\";}}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testIfElseTrue() throws IOException {
		test(root, "test_if_else_true", sp, "{if (true) { return \"t\";} else { return \"f\";}}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testIfVarFalse() throws IOException {
		test(root, "test_if_var_false", sp, "{int x = 5; boolean b = false; if (b) { x = 8;} return x;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testIfVarTrue() throws IOException {
		test(root, "test_if_var_true", sp, "{int x = 5; boolean b = true; if (b) { x = 8;} return x;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testIfFalse() throws IOException {
		test(root, "test_if_false", sp, "{int x = 5; if (false) { x = 8;} return x;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testIfTrue() throws IOException {
		test(root, "test_if_true", sp, "{int x = 5; if (true) { x = 8;} return x;}", newTableset(), new HashMap<String, Object>());
	}
	// TODO: Make test functions for these once they start working:
	//			test(root, "test_lit_long", sp, "{return 12345678910l;}",  newTableset(), new HashMap<String, Object>());
	//			test(root, "test_lit_long_capital", sp, "{return 12345678910L;}",  newTableset(), new HashMap<String, Object>());
	//			test(root, "test_lit_infinity", sp, "{return infinity;}",  newTableset(), new HashMap<String, Object>());
	//			test(root, "test_lit_infinity_neg", sp, "{return -infinity;}",  newTableset(), new HashMap<String, Object>());
	//			test(root, "test_lit_nan", sp, "{return NaN;}",  newTableset(), new HashMap<String, Object>());
	//			test(root, "test_lit_nan_neg", sp, "{return -NaN;}",  newTableset(), new HashMap<String, Object>());
	//			test(root, "test_lit_long_neg", sp, "{return -12345678910l;}",  newTableset(), new HashMap<String, Object>());
	//			test(root, "test_lit_long_capital_neg", sp, "{return -12345678910L;}",  newTableset(), new HashMap<String, Object>());
	//			test(root, "test_lit_hex_neg", sp, "{return -0xF5;}",  newTableset(), new HashMap<String, Object>());
	//			test(root, "test_lit_hex_capital_neg", sp, "{return -0XF5;}",  newTableset(), new HashMap<String, Object>());
	@Test
	public void testStrMultiLine() throws IOException {
		test(root, "test_str_multi_line", sp, "{return \"abc \\\n xyz\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testStr() throws IOException {
		test(root, "test_str", sp, "{return \"abc\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testStrConcat() throws IOException {
		test(root, "test_str_concat", sp, "{return \"abc\" + \"xyz\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitFloatCapitalNeg() throws IOException {
		test(root, "test_lit_float_capital_neg", sp, "{return -16F;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitFloatNeg() throws IOException {
		test(root, "test_lit_float_neg", sp, "{return -16f;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitDoubleCapitalNeg() throws IOException {
		test(root, "test_lit_double_capital_neg", sp, "{return -16D;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitDoubleNeg() throws IOException {
		test(root, "test_lit_double_neg", sp, "{return -16d;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitHexCapital() throws IOException {
		test(root, "test_lit_hex_capital", sp, "{return 0XF5;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitHex() throws IOException {
		test(root, "test_lit_hex", sp, "{return 0xF5;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitFloatCapital() throws IOException {
		test(root, "test_lit_float_capital", sp, "{return 16F;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitFloat() throws IOException {
		test(root, "test_lit_float", sp, "{return 16f;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitDoubleCapital() throws IOException {
		test(root, "test_lit_double_capital", sp, "{return 16D;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitDouble() throws IOException {
		test(root, "test_lit_double", sp, "{return 16d;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitFalse() throws IOException {
		test(root, "test_lit_false", sp, "{return false;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitTrue() throws IOException {
		test(root, "test_lit_true", sp, "{return true;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitNull() throws IOException {
		test(root, "test_lit_null", sp, "{return null;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitUnicode7() throws IOException {
		test(root, "test_lit_unicode7", sp, "{return \"\\u0021\\u003F\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitUnicode6() throws IOException {
		test(root, "test_lit_unicode6", sp, "{return \"\\u0021\" + \"\\u003F\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitUnicode5() throws IOException {
		test(root, "test_lit_unicode5", sp, "{return \"\\u03BB\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitUnicode4() throws IOException {
		test(root, "test_lit_unicode4", sp, "{return \"\\u0025\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitUnicode3() throws IOException {
		test(root, "test_lit_unicode3", sp, "{return \"\\u0024\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitUnicode2() throws IOException {
		test(root, "test_lit_unicode2", sp, "{return \"\\u0023\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitUnicode1() throws IOException {
		test(root, "test_lit_unicode1", sp, "{return \"\\u0021\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitSciNot8() throws IOException {
		test(root, "test_lit_sci_not8", sp, "{return -4.2e-3;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitSciNot7() throws IOException {
		test(root, "test_lit_sci_not7", sp, "{return -4.2e3;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitSciNot6() throws IOException {
		test(root, "test_lit_sci_not6", sp, "{return 4.2e-3;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitSciNot5() throws IOException {
		test(root, "test_lit_sci_not5", sp, "{return 4.2e3;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitSciNot4() throws IOException {
		test(root, "test_lit_sci_not4", sp, "{return -4e-3;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitSciNot3() throws IOException {
		test(root, "test_lit_sci_not3", sp, "{return -4e3;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitSciNot2() throws IOException {
		test(root, "test_lit_sci_not2", sp, "{return 4e-3;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitSciNot1() throws IOException {
		test(root, "test_lit_sci_not1", sp, "{return 4e3;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitIntHex() throws IOException {
		test(root, "test_lit_int_hex", sp, "{return 0x17;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLitIntDec() throws IOException {
		test(root, "test_lit_int_dec", sp, "{return 17;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testReturnBrackets() throws IOException {
		test(root, "test_return_brackets", sp, "{{return 17;}}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex1() throws IOException {
		test(root, "test_regex1", sp, "{return \"abc\" =~ \"abc\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex2() throws IOException {
		test(root, "test_regex2", sp, "{return \"abc\" =~ \"xyz\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex3() throws IOException {
		test(root, "test_regex3", sp, "{return \"abc\" !~ \"abc\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex4() throws IOException {
		test(root, "test_regex4", sp, "{return \"abc\" !~ \"xyz\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex5() throws IOException {
		test(root, "test_regex5", sp, "{return \"abc123xyz\" =~ \"\\\\d\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex6() throws IOException {
		test(root, "test_regex6", sp, "{return \"define \\\"123\\\"\" =~ \"\\\\d\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex7() throws IOException {
		test(root, "test_regex7", sp, "{return \"var g = 123;\" =~ \"\\\\d\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex8() throws IOException {
		test(root, "test_regex8", sp, "{return \"cat.\" =~ \".\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex9() throws IOException {
		test(root, "test_regex9", sp, "{return \"896.\" =~ \".\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex10() throws IOException {
		test(root, "test_regex10", sp, "{return \"?=+.\" =~ \".\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex11() throws IOException {
		test(root, "test_regex11", sp, "{return \"abc1\" =~ \".\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex12() throws IOException {
		test(root, "test_regex12", sp, "{return \"cat.\" =~ \"\\\\.\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex13() throws IOException {
		test(root, "test_regex13", sp, "{return \"896.\" =~ \"\\\\.\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex14() throws IOException {
		test(root, "test_regex14", sp, "{return \"?=+.\" =~ \"\\\\.\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex15() throws IOException {
		test(root, "test_regex15", sp, "{return \"abc1\" =~ \"\\\\.\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex16() throws IOException {
		test(root, "test_regex16", sp, "{return \"can\" =~ \"[cmf]\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex17() throws IOException {
		test(root, "test_regex17", sp, "{return \"man\" =~ \"[cmf]\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex18() throws IOException {
		test(root, "test_regex18", sp, "{return \"fan\" =~ \"[cmf]\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex19() throws IOException {
		test(root, "test_regex19", sp, "{return \"dan\" =~ \"[cmf]\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex20() throws IOException {
		test(root, "test_regex20", sp, "{return \"ran\" =~ \"[cmf]\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex21() throws IOException {
		test(root, "test_regex21", sp, "{return \"pan\" =~ \"[cmf]\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex22() throws IOException {
		test(root, "test_regex22", sp, "{return \"hog\" =~ \"[^bog]\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex23() throws IOException {
		test(root, "test_regex23", sp, "{return \"dog\" =~ \"[^bog]\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex24() throws IOException {
		test(root, "test_regex24", sp, "{return \"bog\" =~ \"[^bog]\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex25() throws IOException {
		test(root, "test_regex25", sp, "{return \"abcdefg12345\" =~ \"\\\\w\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex26() throws IOException {
		test(root, "test_regex26", sp, "{return  \"!@#$%^&*()\"=~ \"\\\\w\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex27() throws IOException {
		test(root, "test_regex27", sp, "{return \"Ana\" =~ \"[A-Z]\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex28() throws IOException {
		test(root, "test_regex28", sp, "{return \"Bob\" =~ \"[A-Z]\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex29() throws IOException {
		test(root, "test_regex29", sp, "{return \"Cpc\" =~ \"[A-Z]\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex30() throws IOException {
		test(root, "test_regex30", sp, "{return \"aax\" =~ \"[A-Z]\";}", newTableset(), new HashMap<String, Object>()); // WRONG
	}
	@Test
	public void testRegex31() throws IOException {
		test(root, "test_regex31", sp, "{return \"bby\" =~ \"[A-Z]\";}", newTableset(), new HashMap<String, Object>()); // WRONG
	}
	@Test
	public void testRegex32() throws IOException {
		test(root, "test_regex32", sp, "{return \"ccz\" =~ \"[A-Z]\";}", newTableset(), new HashMap<String, Object>()); // WRONG
	}
	@Test
	public void testRegex33() throws IOException {
		test(root, "test_regex33", sp, "{return \"wazzzzzup\" =~ \"z{3,5}\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex34() throws IOException {
		test(root, "test_regex34", sp, "{return \"wazzzup\" =~ \"z{3,5}\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex35() throws IOException {
		test(root, "test_regex35", sp, "{return \"wazup\" =~ \"z{3,5}\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex36() throws IOException {
		test(root, "test_regex36", sp, "{return \"wazzzzzup\" =~ \"[abc]{3,5}\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex37() throws IOException {
		test(root, "test_regex37", sp, "{return \"wazzzzzup\" =~ \"[xyz]{3,5}\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex38() throws IOException {
		test(root, "test_regex38", sp, "{return \"wazzzzzup\" =~ \".{3,5}\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex39() throws IOException {
		test(root, "test_regex39", sp, "{return \"aaaabcc\" =~ \"c+\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex40() throws IOException {
		test(root, "test_regex40", sp, "{return \"aabbbbc\" =~ \"c+\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex41() throws IOException {
		test(root, "test_regex41", sp, "{return \"aacc\" =~ \"c+\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex42() throws IOException {
		test(root, "test_regex42", sp, "{return \"a\" =~ \"c+\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex43() throws IOException {
		test(root, "test_regex43", sp, "{return \"xyz\" =~ \"x?z\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex44() throws IOException {
		test(root, "test_regex44", sp, "{return \"xz\" =~ \"x?z\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex45() throws IOException {
		test(root, "test_regex45", sp, "{return \"abc\" =~ \"x?z\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex46() throws IOException {
		test(root, "test_regex46", sp, "{return \"1.  abc\" =~ \"1\\\\.\\\\s\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex47() throws IOException {
		test(root, "test_regex47", sp, "{return \"1.     abc\" =~ \"1\\\\.\\\\s\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex48() throws IOException {
		test(root, "test_regex48", sp, "{return \"1.   abc\" =~ \"1\\\\.\\\\s\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex49() throws IOException {
		test(root, "test_regex49", sp, "{return \"1.abc\" =~ \"1\\\\.\\\\s\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex50() throws IOException {
		test(root, "test_regex50", sp, "{return \"Mission: successful\" =~ \"^Mission\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex51() throws IOException {
		test(root, "test_regex51", sp, "{return \"Last Mission: unsuccessful\" =~ \"^Mission\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex52() throws IOException {
		test(root, "test_regex52", sp, "{return \"Next Mission: successful upon capture of target\" =~ \"^Mission\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex53() throws IOException {
		test(root, "test_regex53", sp, "{return \"file_record_transcript.pdf\" =~ \"^(.+)\\\\.pdf$\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex54() throws IOException {
		test(root, "test_regex54", sp, "{return \"file_07241999.pdf\" =~ \"^(.+)\\\\.pdf$\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex55() throws IOException {
		test(root, "test_regex55", sp, "{return \"testfile_fake.pdf.tmp\" =~ \"^(.+)\\\\.pdf$\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex56() throws IOException {
		test(root, "test_regex56", sp, "{return \"Jan 1987\" =~ \"(\\\\w+\\\\s(\\\\d+))\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex57() throws IOException {
		test(root, "test_regex57", sp, "{return \"May 1969\" =~ \"(\\\\w+\\\\s(\\\\d+))\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex58() throws IOException {
		test(root, "test_regex58", sp, "{return \"Aug 2011\" =~ \"(\\\\w+\\\\s(\\\\d+))\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex59() throws IOException {
		test(root, "test_regex59", sp, "{return \"1280x720\" =~ \"(\\\\d+)x(\\\\d+)\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex60() throws IOException {
		test(root, "test_regex60", sp, "{return \"1920x1600\" =~ \"(\\\\d+)x(\\\\d+)\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex61() throws IOException {
		test(root, "test_regex61", sp, "{return \"1024x768\" =~ \"(\\\\d+)x(\\\\d+)\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex62() throws IOException {
		test(root, "test_regex62", sp, "{return \"I love cats\" =~ \"I love (cats|dogs)\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex63() throws IOException {
		test(root, "test_regex63", sp, "{return \"I love dogs\" =~ \"I love (cats|dogs)\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex64() throws IOException {
		test(root, "test_regex64", sp, "{return \"I love logs\" =~ \"I love (cats|dogs)\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testRegex65() throws IOException {
		test(root, "test_regex65", sp, "{return \"I love cogs\" =~ \"I love (cats|dogs)\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testObjectOrientedNew() throws IOException {
		test(root, "test_oo_new", sp, "{return new Integer(5);}", newTableset(), new HashMap<String, Object>());
	}
	// TODO: Make tests for these when they are working: 
	//			test(root, "test_bitwise_OR", sp, "{return 3 | 5;}",  newTableset(), new HashMap<String, Object>());
	//			test(root, "test_bitwise_AND", sp, "{return 3 & 5;}",  newTableset(), new HashMap<String, Object>());
	//			test(root, "test_bitwise_XOR", sp, "{return 3 ^ 5;}",  newTableset(), new HashMap<String, Object>());
	//			test(root, "test_bitwise_COMP", sp, "{return 3;}",  newTableset(), new HashMap<String, Object>());
	@Test
	public void testBitwiseUrightCrossNeg() throws IOException {
		test(root, "test_bitwise_uright_cross_neg", sp, "{return -44 >>> 3;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testBitwiseRightCrossNeg() throws IOException {
		test(root, "test_bitwise_right_cross_neg", sp, "{return -44 >> 3;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testBitwiseUrightCrossPos() throws IOException {
		test(root, "test_bitwise_uright_cross_pos", sp, "{return 44 >>> 3;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testBitwiseRightCrossPos() throws IOException {
		test(root, "test_bitwise_right_cross_pos", sp, "{return 44 >> 3;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testBitwiseUright() throws IOException {
		test(root, "test_bitwise_uright", sp, "{return 44 >>> 2;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testBitwiseLeft() throws IOException {
		test(root, "test_bitwise_left", sp, "{return 44 << 2;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testBitwiseRight() throws IOException {
		test(root, "test_bitwise_right", sp, "{return 44 >> 2;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLteEdge() throws IOException {
		test(root, "test_lte_edge", sp, "{return 5 <= 5;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLteFalse() throws IOException {
		test(root, "test_lte_false", sp, "{return 5 <= 3;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLteTrue() throws IOException {
		test(root, "test_lte_true", sp, "{return 3 <= 5;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testGteEdge() throws IOException {
		test(root, "test_gte_edge", sp, "{return 5 >= 5;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testGteFalse() throws IOException {
		test(root, "test_gte_false", sp, "{return 3 >= 5;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testGteTrue() throws IOException {
		test(root, "test_gte_true", sp, "{return 5 >= 3;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLtEdge() throws IOException {
		test(root, "test_lt_edge", sp, "{return 5 < 5;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLtFalse() throws IOException {
		test(root, "test_lt_false", sp, "{return 5 < 3;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testLtTrue() throws IOException {
		test(root, "test_lt_true", sp, "{return 3 < 5;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testGtEdge() throws IOException {
		test(root, "test_gt_edge", sp, "{return 5 > 5;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testGtFalse() throws IOException {
		test(root, "test_gt_false", sp, "{return 3 > 5;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testGtTrue() throws IOException {
		test(root, "test_gt_true", sp, "{return 5 > 3;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testBoolNotTrue() throws IOException {
		test(root, "test_bool_NOT_true", sp, "{return !false;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testBoolNotFalse() throws IOException {
		test(root, "test_bool_NOT_false", sp, "{return !true;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testBoolNeqNull() throws IOException {
		test(root, "test_bool_NEQ_null", sp, "{return null != null;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testBoolNeq11() throws IOException {
		test(root, "test_bool_NEQ_11", sp, "{return true != true;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testBoolNeq10() throws IOException {
		test(root, "test_bool_NEQ_10", sp, "{return true != false;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testBoolNeq01() throws IOException {
		test(root, "test_bool_NEQ_01", sp, "{return false != true;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testBoolNeq00() throws IOException {
		test(root, "test_bool_NEQ_00", sp, "{return false != false;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testBoolEqNull() throws IOException {
		test(root, "test_bool_EQ_null", sp, "{return null == null;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testBoolEq11() throws IOException {
		test(root, "test_bool_EQ_11", sp, "{return true == true;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testBoolEq10() throws IOException {
		test(root, "test_bool_EQ_10", sp, "{return true == false;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testBoolEq01() throws IOException {
		test(root, "test_bool_EQ_01", sp, "{return false == true;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testBoolEq00() throws IOException {
		test(root, "test_bool_EQ_00", sp, "{return false == false;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testBoolAnd11() throws IOException {
		test(root, "test_bool_AND_11", sp, "{return true && true;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testBoolAnd10() throws IOException {
		test(root, "test_bool_AND_10", sp, "{return true && false;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testBoolAnd01() throws IOException {
		test(root, "test_bool_AND_01", sp, "{return false && true;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testBoolAnd00() throws IOException {
		test(root, "test_bool_AND_00", sp, "{return false && false;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testBoolOr11() throws IOException {
		test(root, "test_bool_OR_11", sp, "{return true || true;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testBoolOr10() throws IOException {
		test(root, "test_bool_OR_10", sp, "{return true || false;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testBoolOr01() throws IOException {
		test(root, "test_bool_OR_01", sp, "{return false || true;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testBoolOr00() throws IOException {
		test(root, "test_bool_OR_00", sp, "{return false || false;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testTernaryCondFalse() throws IOException {
		test(root, "test_ternary_cond_false", sp, "{return false ? \"t\" : \"f\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testTernaryCondTrue() throws IOException {
		test(root, "test_ternary_cond_true", sp, "{return true ? \"t\" : \"f\";}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testAssignModDouble() throws IOException {
		test(root, "test_assign_mod_double", sp, "{double i = 14.7; return i %= 4.8;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testAssignDivDouble() throws IOException {
		test(root, "test_assign_div_double", sp, "{double i = 12.7; return i /= 4.8;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testAssignMulDouble() throws IOException {
		test(root, "test_assign_mul_double", sp, "{double i = 12.7; return i *= 4.8;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testAssignSubDouble() throws IOException {
		test(root, "test_assign_sub_double", sp, "{double i = 12.7; return i -= 4.8;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testAssignAddDouble() throws IOException {
		test(root, "test_assign_add_double", sp, "{double i = 12.7; return i += 4.8;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testAssignModInt() throws IOException {
		test(root, "test_assign_mod_int", sp, "{int i = 14; return i %= 4;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testAssignDivInt() throws IOException {
		test(root, "test_assign_div_int", sp, "{int i = 12; return i /= 4;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testAssignMulInt() throws IOException {
		test(root, "test_assign_mul_int", sp, "{int i = 12; return i *= 4;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testAssignSubInt() throws IOException {
		test(root, "test_assign_sub_int", sp, "{int i = 12; return i -= 4;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testAssignAddInt() throws IOException {
		test(root, "test_assign_add_int", sp, "{int i = 12; return i += 4;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testAssign() throws IOException {
		test(root, "test_assign", sp, "{int x = 12; int y = 5; y = x; return y;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testPemdas() throws IOException {
		test(root, "test_pemdas", sp, "{return (4.2 * 5.1 + 3.9) * (8.6 / 5.5 - 7.7) / (-7.3 * 9.4 - 4.8);}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testModDouble() throws IOException {
		test(root, "test_mod_double", sp, "{return 14.7 % 4.8;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testDivZeroDouble() throws IOException {
		test(root, "test_div_zero_double", sp, "{return 12.7 / 0.0;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testDivDouble() throws IOException {
		test(root, "test_div_double", sp, "{return 12.7 / 4.8;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testMulDouble() throws IOException {
		test(root, "test_mul_double", sp, "{return 12.7 * 4.8;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testSubDouble() throws IOException {
		test(root, "test_sub_double", sp, "{return 12.7 - 4.8;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testAddDouble() throws IOException {
		test(root, "test_add_double", sp, "{return 12.7 + 4.8;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testModInt() throws IOException {
		test(root, "test_mod_int", sp, "{return 14 % 4;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testDivZeroInt() throws IOException {
		test(root, "test_div_zero_int", sp, "{return 12 / 0;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testDivInt() throws IOException {
		test(root, "test_div_int", sp, "{return 12 / 4;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testMulInt() throws IOException {
		test(root, "test_mul_int", sp, "{return 12 * 4;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testSubInt() throws IOException {
		test(root, "test_sub_int", sp, "{return 12 - 4;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testAddInt() throws IOException {
		test(root, "test_add_int", sp, "{return 12 + 4;}", newTableset(), new HashMap<String, Object>());
	}
	@Test
	public void testDecPrefix() throws IOException {
		globalVars.put("i", 1);
		test(root, "test_dec_prefix", sp, "{return --i;}", newTableset(), globalVars);
	}
	@Test
	public void testDecSuffix() throws IOException {
		globalVars.put("i", 2);
		test(root, "test_dec_suffix", sp, "{return i--;}", newTableset(), globalVars);
	}
	@Test
	public void testIncPrefix() throws IOException {
		globalVars.put("i", 1);
		test(root, "test_inc_prefix", sp, "{return ++i;}", newTableset(), globalVars);
	}
	@Test
	public void testIncSuffix() throws IOException {
		globalVars.put("i", 0);
		test(root, "test_inc_suffix", sp, "{return i++;}", newTableset(), globalVars);
	}
	@Test
	public void testPrepare() throws IOException {
		testTableset.putTable(sampleInput);
		test(root, "test_prepare", sp, "{prepare x, y, z, stack(x), count(y), norm(z), stack(x * y), norm(x - y * z), x - offset(y, -1) * offset(z, 1) as calc from input;}",
				testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testGroupBy() throws IOException {
		testTableset.putTable(sampleInput);
		test(root, "test_group_by", sp,
				"{select category, sum(x), min(y), max(z), count(x), countUnique(y), cat(z, \":\", 4), catUnique(z, \":\"), avg(y), avgExp(x, 0.5, true), avgGauss(y, 1), var(z), varS(z), stdev(x), stdevS(x), first(y), last(z), covar(x, y), covarS(x, y), cor(y, z), beta(z, x), median(z), cksumAgg(x), percentileCont(x, 0.6), percentileDisc(y, 0.7), sum(x * y), avg(y + x * z) from input group by category;}",
				testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAggMultipleArguments() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_agg_multiple_arguments", sp, "select cor(x, y), cor(x, z), covar(x, y), covar(x, z), covarS(x, y), covarS(x, z), beta(x, y), beta(x, z) from tableA;",
				testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAggSinCos() throws IOException {
		testTableset.putTable(sine);
		testTableset.putTable(cosine);
		test(root, "test_sql_agg_sin_cos", sp,
				"select cor(sine.x, sine.x) as autocor_sin, cor(cosine.x, cosine.x) as autocor_cos, cor(cosine.x, sine.x) as cor_cos_sin, covar(sine.x, sine.x) as var_sin, covar(cosine.x, cosine.x) as var_cos, covar(cosine.x, sine.x) as covar_cos_sin, beta(sine.x, sine.x) as beta_sin_sin, beta(cosine.x, cosine.x) as beta_cos_cos, beta(cosine.x, sine.x) as beta_cos_sin from cosine, sine where cosine.n==sine.n",
				testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlPrepareSineDeriv() throws IOException {
		testTableset.putTable(sine);
		testTableset.putTable(cosine);
		test(root, "test_sql_prepare_sine_deriv", sp, "create table trig_deriv as prepare n, x as sin, (x - offset(x, -1)) / (" + trigFuncDiscrete2ContinuousTime
				+ " * (n - offset(n, -1))) as dy_dx from sine;\ncreate table trig_deriv_compare as select cosine.n as n, cosine.x as cos, trig_deriv.dy_dx as dy_dx, cosine.x - trig_deriv.dy_dx as diff from cosine, trig_deriv where cosine.n == trig_deriv.n;\ncreate table trig_deriv_compare as select *, diff * diff < 1e-3 as same from trig_deriv_compare;",
				testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testMultipleTableJoins() throws IOException {
		testTableset.putTable(sA);
		testTableset.putTable(sB);
		testTableset.putTable(sC);
		test(root, "test_multiple_table_joins", sp,

				"create table testABC as select * from sA, sB, sC;\n" +

						"create table testCAB as select * from sC, sA, sB;\n" +

						"create table testBCA as select * from sB, sC, sA;\n" +

						"create table testCBA as select * from sC, sB, sA;\n" +

						"create table testACB as select * from sA, sC, sB;\n" +

						"create table testBAC as select * from sB, sA, sC;\n" +

						"create table testABCfilt as select * from sA, sB, sC where sA.nm == sB.nm && sA.nm == sC.nm && sB.nm == sC.nm;\n" +

						"create table testCABfilt as select * from sC, sA, sB where sA.nm == sB.nm && sA.nm == sC.nm && sB.nm == sC.nm;\n" +

						"create table testBCAfilt as select * from sB, sC, sA where sA.nm == sB.nm && sA.nm == sC.nm && sB.nm == sC.nm;\n" +

						"create table testCBAfilt as select * from sC, sB, sA where sA.nm == sB.nm && sA.nm == sC.nm && sB.nm == sC.nm;\n" +

						"create table testACBfilt as select * from sA, sC, sB where sA.nm == sB.nm && sA.nm == sC.nm && sB.nm == sC.nm;\n" +

						"create table testBACfilt as select * from sB, sA, sC where sA.nm == sB.nm && sA.nm == sC.nm && sB.nm == sC.nm;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testMultipleTableJoinsAllCombos() throws IOException {
		Table A = new ColumnarTable();
		Table B = new ColumnarTable();
		Table C = new ColumnarTable();
		A.addColumn(Integer.class, "x");
		A.addColumn(Integer.class, "y");
		B.addColumn(Integer.class, "x");
		B.addColumn(Integer.class, "y");
		C.addColumn(Integer.class, "x");
		C.addColumn(Integer.class, "y");
		A.setTitle("A");
		B.setTitle("B");
		C.setTitle("C");
		A.getRows().addRow(0, 0);
		A.getRows().addRow(0, 1);
		A.getRows().addRow(1, 0);
		A.getRows().addRow(1, 1);
		B.getRows().addRow(0, 0);
		B.getRows().addRow(0, 1);
		B.getRows().addRow(1, 0);
		B.getRows().addRow(1, 1);
		C.getRows().addRow(0, 0);
		C.getRows().addRow(0, 1);
		C.getRows().addRow(1, 0);
		C.getRows().addRow(1, 1);

		testTableset.putTable(A);
		testTableset.putTable(B);
		testTableset.putTable(C);
		test(root, "test_multiple_table_joins_all_combos", sp,

				"create table ABC as select * from A, B, C where A.x == B.x && B.y == C.y && A.y == C.x;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testMultipleTableJoinsSelCol() throws IOException {
		testTableset.putTable(sA);
		testTableset.putTable(sB);
		testTableset.putTable(sC);
		test(root, "test_multiple_table_joins_sel_col", sp,

				"create table testABC as select sA.x, sC.y, sB.nm, sB.x from sA, sB, sC;\n" +

						"create table testCAB as select sA.x, sC.y, sB.nm, sB.x from sC, sA, sB;\n" +

						"create table testBCA as select sA.x, sC.y, sB.nm, sB.x from sB, sC, sA;\n" +

						"create table testCBA as select sA.x, sC.y, sB.nm, sB.x from sC, sB, sA;\n" +

						"create table testACB as select sA.x, sC.y, sB.nm, sB.x from sA, sC, sB;\n" +

						"create table testBAC as select sA.x, sC.y, sB.nm, sB.x from sB, sA, sC;\n" +

						"create table testABCfilt as select sA.x, sC.y, sB.nm, sB.x from sA, sB, sC where sA.nm == sB.nm && sA.nm == sC.nm && sB.nm == sC.nm;\n" +

						"create table testCABfilt as select sA.x, sC.y, sB.nm, sB.x from sC, sA, sB where sA.nm == sB.nm && sA.nm == sC.nm && sB.nm == sC.nm;\n" +

						"create table testBCAfilt as select sA.x, sC.y, sB.nm, sB.x from sB, sC, sA where sA.nm == sB.nm && sA.nm == sC.nm && sB.nm == sC.nm;\n" +

						"create table testCBAfilt as select sA.x, sC.y, sB.nm, sB.x from sC, sB, sA where sA.nm == sB.nm && sA.nm == sC.nm && sB.nm == sC.nm;\n" +

						"create table testACBfilt as select sA.x, sC.y, sB.nm, sB.x from sA, sC, sB where sA.nm == sB.nm && sA.nm == sC.nm && sB.nm == sC.nm;\n" +

						"create table testBACfilt as select sA.x, sC.y, sB.nm, sB.x from sB, sA, sC where sA.nm == sB.nm && sA.nm == sC.nm && sB.nm == sC.nm;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testMultipleTableJoinsCompareConst() throws IOException {
		testTableset.putTable(sD);
		testTableset.putTable(sE);
		testTableset.putTable(sF);
		test(root, "test_multiple_table_joins_compare_const", sp,

				"create table testDEFlt as select * from sD, sE, sF where sD.x < 2;\n" +

						"create table testDEFlte as select * from sD, sE, sF where sD.x <= 2;\n" +

						"create table testDEFgt as select * from sD, sE, sF where sD.x > 2;\n" +

						"create table testDEFgte as select * from sD, sE, sF where sD.x >= 2;	"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testMultipleTableJoinsCompareCols() throws IOException {
		testTableset.putTable(sD);
		testTableset.putTable(sE);
		testTableset.putTable(sF);
		test(root, "test_multiple_table_joins_compare_cols", sp,

				"create table testDEFlt as select * from sD, sE, sF where sD.x < sE.x;\n" +

						"create table testDEFlte as select * from sD, sE, sF where sD.x <= sE.x;\n" +

						"create table testDEFgt as select * from sD, sE, sF where sD.x > sE.x;\n" +

						"create table testDEFgte as select * from sD, sE, sF where sD.x >= sE.x;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testMultipleTableJoinsCompareConstsInner() throws IOException {
		testTableset.putTable(sD);
		testTableset.putTable(sE);
		testTableset.putTable(sF);
		test(root, "test_multiple_table_joins_compare_consts_inner", sp,

				"create table testDEFltlt as select * from sD, sE, sF where 0 < sD.x && sD.x < 2;\n" +

						"create table testDEFltlte as select * from sD, sE, sF where 0 < sD.x && sD.x <= 2;\n" +

						"create table testDEFltelt as select * from sD, sE, sF where 0 <= sD.x && sD.x < 2;\n" +

						"create table testDEFltelte as select * from sD, sE, sF where 0 <= sD.x && sD.x <= 2;\n" +

						"create table testDEFgtgt as select * from sD, sE, sF where 2 > sD.x && sD.x > 0;\n" +

						"create table testDEFgtgte as select * from sD, sE, sF where 2 > sD.x && sD.x >= 0;\n" +

						"create table testDEFgtegt as select * from sD, sE, sF where 2 >= sD.x && sD.x > 0;\n" +

						"create table testDEFgtegte as select * from sD, sE, sF where 2 >= sD.x && sD.x >= 0;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testMultipleTableJoinsCompareConstsOuter() throws IOException {
		testTableset.putTable(sD);
		testTableset.putTable(sE);
		testTableset.putTable(sF);
		test(root, "test_multiple_table_joins_compare_consts_outer", sp,

				"create table testDEFltgt as select * from sD, sE, sF where sD.x < 0 || sD.x > 2;\n" +

						"create table testDEFltgte as select * from sD, sE, sF where sD.x < 0 || sD.x >= 2;\n" +

						"create table testDEFltegt as select * from sD, sE, sF where sD.x <= 0 || sD.x > 2;\n" +

						"create table testDEFltegte as select * from sD, sE, sF where sD.x <= 0 || sD.x >= 2;\n" +

						"create table testDEFgtlt as select * from sD, sE, sF where sD.x > 2 || sD.x < 0;\n" +

						"create table testDEFgtlte as select * from sD, sE, sF where sD.x > 2 || sD.x <= 0;\n" +

						"create table testDEFgtelt as select * from sD, sE, sF where sD.x >= 2 || sD.x < 0;\n" +

						"create table testDEFgtelte as select * from sD, sE, sF where sD.x >= 2 || sD.x <= 0;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testMultipleTableJoinsAggs() throws IOException {
		testTableset.putTable(sD);
		testTableset.putTable(sE);
		testTableset.putTable(sF);
		test(root, "test_multiple_table_joins_aggs", sp,

				"create table sumDEF as select sum(sD.x) as d, sum(sE.x) as e, sum(sF.x) as f from sD, sE, sF;\n" +

						"create table minDEF as select min(sD.x) as d, min(sE.x) as e, min(sF.x) as f from sD, sE, sF;\n" +

						"create table maxDEF as select max(sD.x) as d, max(sE.x) as e, max(sF.x) as f from sD, sE, sF;\n" +

						"create table countDEF as select count(sD.x) as d, count(sE.x) as e, count(sF.x) as f from sD, sE, sF;\n" +

						"create table countUniqueDEF as select countUnique(sD.x) as d, countUnique(sE.x) as e, countUnique(sF.x) as f from sD, sE, sF;\n" +

						"create table avgDEF as select avg(sD.x) as d, avg(sE.x) as e, avg(sF.x) as f from sD, sE, sF;\n" +

						"create table varDEF as select var(sD.x) as d, var(sE.x) as e, var(sF.x) as f from sD, sE, sF;\n" +

						"create table varSDEF as select varS(sD.x) as d, varS(sE.x) as e, varS(sF.x) as f from sD, sE, sF;\n" +

						"create table stdevDEF as select stdev(sD.x) as d, stdev(sE.x) as e, stdev(sF.x) as f from sD, sE, sF;\n" +

						"create table stdevSDEF as select stdevS(sD.x) as d, stdevS(sE.x) as e, stdevS(sF.x) as f from sD, sE, sF;\n" +

						"create table firstDEF as select first(sD.x) as d, first(sE.x) as e, first(sF.x) as f from sD, sE, sF;\n" +

						"create table lastDEF as select last(sD.x) as d, last(sE.x) as e, last(sF.x) as f from sD, sE, sF;\n" +

						"create table covarDEF as select covar(sD.x, sE.x) as d, covar(sE.x, sF.x) as e, covar(sF.x, sD.x) as f from sD, sE, sF;\n" +

						"create table covarSDEF as select covarS(sD.x, sE.x) as d, covarS(sE.x, sF.x) as e, covarS(sF.x, sD.x) as f from sD, sE, sF;\n" +

						"create table corDEF as select cor(sD.x, sE.x) as d, cor(sE.x, sF.x) as e, cor(sF.x, sD.x) as f from sD, sE, sF;\n" +

						"create table betaDEF as select beta(sD.x, sE.x) as d, beta(sE.x, sF.x) as e, beta(sF.x, sD.x) as f from sD, sE, sF;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testMultipleTableJoinsAggsNulls() throws IOException {
		testTableset.putTable(sDnulls);
		testTableset.putTable(sEnulls);
		testTableset.putTable(sFnulls);
		test(root, "test_multiple_table_joins_aggs_nulls", sp,

				"create table sumDEF as select sum(sDnulls.x) as d, sum(sEnulls.x) as e, sum(sFnulls.x) as f from sDnulls, sEnulls, sFnulls;\n" +

						"create table minDEF as select min(sDnulls.x) as d, min(sEnulls.x) as e, min(sFnulls.x) as f from sDnulls, sEnulls, sFnulls;\n" +

						"create table maxDEF as select max(sDnulls.x) as d, max(sEnulls.x) as e, max(sFnulls.x) as f from sDnulls, sEnulls, sFnulls;\n" +

						"create table countDEF as select count(sDnulls.x) as d, count(sEnulls.x) as e, count(sFnulls.x) as f from sDnulls, sEnulls, sFnulls;\n" +

						"create table countUniqueDEF as select countUnique(sDnulls.x) as d, countUnique(sEnulls.x) as e, countUnique(sFnulls.x) as f from sDnulls, sEnulls, sFnulls;\n"
						+

						"create table avgDEF as select avg(sDnulls.x) as d, avg(sEnulls.x) as e, avg(sFnulls.x) as f from sDnulls, sEnulls, sFnulls;\n" +

						"create table varDEF as select var(sDnulls.x) as d, var(sEnulls.x) as e, var(sFnulls.x) as f from sDnulls, sEnulls, sFnulls;\n" +

						"create table varSDEF as select varS(sDnulls.x) as d, varS(sEnulls.x) as e, varS(sFnulls.x) as f from sDnulls, sEnulls, sFnulls;\n" +

						"create table stdevDEF as select stdev(sDnulls.x) as d, stdev(sEnulls.x) as e, stdev(sFnulls.x) as f from sDnulls, sEnulls, sFnulls;\n" +

						"create table stdevSDEF as select stdevS(sDnulls.x) as d, stdevS(sEnulls.x) as e, stdevS(sFnulls.x) as f from sDnulls, sEnulls, sFnulls;\n" +

						"create table firstDEF as select first(sDnulls.x) as d, first(sEnulls.x) as e, first(sFnulls.x) as f from sDnulls, sEnulls, sFnulls;\n" +

						"create table lastDEF as select last(sDnulls.x) as d, last(sEnulls.x) as e, last(sFnulls.x) as f from sDnulls, sEnulls, sFnulls;\n" +

						"create table covarDEF as select covar(sDnulls.x, sEnulls.x) as d, covar(sEnulls.x, sFnulls.x) as e, covar(sFnulls.x, sDnulls.x) as f from sDnulls, sEnulls, sFnulls;\n"
						+

						"create table covarSDEF as select covarS(sDnulls.x, sEnulls.x) as d, covarS(sEnulls.x, sFnulls.x) as e, covarS(sFnulls.x, sDnulls.x) as f from sDnulls, sEnulls, sFnulls;\n"
						+

						"create table corDEF as select cor(sDnulls.x, sEnulls.x) as d, cor(sEnulls.x, sFnulls.x) as e, cor(sFnulls.x, sDnulls.x) as f from sDnulls, sEnulls, sFnulls;\n"
						+

						"create table betaDEF as select beta(sDnulls.x, sEnulls.x) as d, beta(sEnulls.x, sFnulls.x) as e, beta(sFnulls.x, sDnulls.x) as f from sDnulls, sEnulls, sFnulls;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testVarInWhereClause1() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_var_in_where_clause1", sp,

				"double d = 5;\n" +

						"select * from tableA where tableA.x < d;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testVarInWhereClause2() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_var_in_where_clause2", sp,

				"double d = 5;\n" +

						"select * from tableA where tableA.x < ${d};"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testVarInWhereClause3() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(tableF);
		test(root, "test_var_in_where_clause3", sp,

				"double d = 5;\n" +

						"select * from tableA, tableF where tableA.x < d;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testVarInWhereClause4() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(tableF);
		test(root, "test_var_in_where_clause4", sp,

				"double d = 5;\n" +

						"select * from tableA, tableF where tableA.x < ${d};"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlPrefilter() throws IOException {
		testTableset.putTable(smallLowHighCardA);
		testTableset.putTable(smallLowHighCardB);
		test(root, "test_sql_prefilter", sp,

				"select * from A, B where A.ones == B.ones && A.mostly_zeros == 1 && B.mostly_zeros == 1;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlPrefilterDifferentOrders1() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(tableF);
		test(root, "test_sql_prefilter_different_orders1", sp,

				"select * from tableA, tableF where tableA.x == 4 && tableA.y == 5 && tableF.x == 1;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlPrefilterDifferentOrders2() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(tableF);
		test(root, "test_sql_prefilter_different_orders2", sp,

				"select * from tableA, tableF where tableF.x == 1 && tableA.x == 4 && tableA.y == 5;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlPrefilterDifferentOrders3() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(tableF);
		test(root, "test_sql_prefilter_different_orders3", sp,

				"select * from tableA, tableF where tableA.y == 5 && tableF.x == 1 && tableA.x == 4;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlPrefilterDifferentOrders4() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(tableF);
		test(root, "test_sql_prefilter_different_orders4", sp,

				"select * from tableA, tableF where tableF.x == 1 && tableA.y == 5 && tableA.x == 4;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlPrefilterDifferentOrders5() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(tableF);
		test(root, "test_sql_prefilter_different_orders5", sp,

				"select * from tableA, tableF where tableA.x == 4 && tableF.x == 1 && tableA.y == 5;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlPrefilterDifferentOrders6() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(tableF);
		test(root, "test_sql_prefilter_different_orders6", sp,

				"select * from tableA, tableF where tableA.y == 5 && tableA.x == 4 && tableF.x == 1;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlBackTickCreateTableAs() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_back_tick_create_table_as", sp,

				"create table `order` as select * from tableA;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlBackTickSelectColumn() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_back_tick_select_column", sp,

				"select `x` from tableA;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlBackTickSelectColumnsMultiple() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_back_tick_select_columns_multiple", sp,

				"select `x`, `y`, `z` from tableA;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlBackTickSelectColumnsExpression() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_back_tick_select_columns_expression", sp,

				"select `x` + `y` * `z` from tableA;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlBackTick1() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_back_tick1", sp,

				"select sum(`z`) from tableA;"

				, testTableset, new HashMap<String, Object>());
	}

	@Test
	public void testSqlBackTick2() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_back_tick2", sp,

				"select sum(`z`) from tableA group by `x`;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlBackTick3() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_back_tick3", sp,

				"select `x` as `a` from tableA;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlBackTick4() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_back_tick4", sp,

				"select `x` from tableA order by `y`;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlBackTick5() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_back_tick5", sp,

				"select `x` as `a`, `y` as `b` from tableA order by `b`;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlBackTick6() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_back_tick6", sp,

				"select `x` as `a`, `y` as `b` from tableA order by b;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlBackTick7() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_back_tick7", sp,

				"select `x` as `a`, `y` as `b` from tableA order by `y`;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlBackTick8() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_back_tick8", sp,

				"select * from `tableA`;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlBackTick9() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_back_tick9", sp,

				"create table d1 as select sum(`z`) as `sz`, avg(`y`) as `ay` from tableA group by `x` order by `ay`;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlBackTick10() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_back_tick10", sp,

				"select * from tableA where `x` > 5;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlBackTick11() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_back_tick11", sp,

				"select * from tableA where `x` < `y`;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlBackTick12() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_back_tick12", sp,

				"select * from tableA where `x` + 0.5 * `y` > 5;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlBackTick13() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_back_tick13", sp,

				"create table d1 as analyze sum(win.`x`) from analyzeInput window win on `n` - 3 < win.`n` && win.`n` <= `n` + 3;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlBackTick14() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_back_tick14", sp,

				"create table d2 as prepare stack(`x`), offset(`y`, -2) from analyzeInput;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlBackTick15() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_back_tick15", sp,

				"create table `t` (`x` double, `y` double);\ninsert into `t` values (3, 2);"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlBackTick16() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_back_tick16", sp,

				"int n = 3;\nselect * from tableA limit `n`;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlBackTick17() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_back_tick17", sp,

				"int n = 3;\nint m = 2;\nselect * from tableA limit `m`, `n`;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlBackTick18() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_back_tick18", sp,

				"int n = 3;\nselect * from tableA limit `n` - 1, `n`;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlBackTick19() throws IOException {
		test(root, "test_sql_back_tick19", sp,

				"create table t (x double);\ndrop table `t`;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlLimit1() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_limit1", sp,

				"int n = 3;\nselect * from tableA limit n;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlLimit2() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_limit2", sp,

				"int n = 3;\nint m = 2;\nselect * from tableA limit m, n;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlLimit3() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_limit3", sp,

				"int n = 3;\nselect * from tableA limit n - 1, n;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlLimit4() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_limit4", sp,

				"select * from tableA limit 3;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlLimit5() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_limit5", sp,

				"select * from tableA limit 2, 3;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlLimit6() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_limit6", sp,

				"select * from tableA limit 3 - 1, 3;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlEmptyTable1() throws IOException {
		testTableset.putTable(empty);
		test(root, "test_sql_empty_table1", sp,

				"select * from empty;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlEmptyTable2() throws IOException {
		testTableset.putTable(empty);
		test(root, "test_sql_empty_table2", sp,

				"select sum(x) from empty group by y;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlEmptyTable3() throws IOException {
		testTableset.putTable(empty);
		test(root, "test_sql_empty_table3", sp,

				"analyze sum(win.x) from empty window win on y - 3 < win.y && win.y < y + 3;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlEmptyTable4() throws IOException {
		testTableset.putTable(empty);
		test(root, "test_sql_empty_table4", sp,

				"analyze sum(win.x) from empty window win on y - 3 < win.y && win.y < y + 3 partition by y;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlEmptyTable5() throws IOException {
		testTableset.putTable(empty);
		test(root, "test_sql_empty_table5", sp,

				"prepare stack(x), offset(y, -2) from empty;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlEmptyTable6() throws IOException {
		testTableset.putTable(empty);
		testTableset.putTable(empty2);
		test(root, "test_sql_empty_table6", sp,

				"select * from empty join empty2 on true;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlEmptyTable7() throws IOException {
		testTableset.putTable(empty);
		test(root, "test_sql_empty_table7", sp,

				"select * from empty order by x;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlEmptyTable8() throws IOException {
		testTableset.putTable(empty);
		test(root, "test_sql_empty_table8", sp,

				"prepare stack(x) from empty partition by x;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlEmptyTable10() throws IOException {
		testTableset.putTable(empty);
		test(root, "test_sql_empty_table10", sp,

				"prepare stack(x) from empty order by z partition by y;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlEmptyTable11() throws IOException {
		testTableset.putTable(empty);
		test(root, "test_sql_empty_table11", sp,

				"select count(*) from empty;"

				, testTableset, new HashMap<String, Object>());
	}

	@Test
	public void testSqlGroupByLimit1() throws IOException {
		testTableset.putTable(analyzeInputMarked);
		test(root, "test_sql_group_by_limit1", sp,

				"select * from input_marked;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlGroupByLimit2() throws IOException {
		testTableset.putTable(analyzeInputMarked);
		test(root, "test_sql_group_by_limit2", sp,

				"select sum(x) from input_marked group by n order by n;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlGroupByLimit3() throws IOException {
		testTableset.putTable(analyzeInputMarked);
		test(root, "test_sql_group_by_limit3", sp,

				"select * from input_marked limit 6;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlGroupByLimit4() throws IOException {
		testTableset.putTable(analyzeInputMarked);
		test(root, "test_sql_group_by_limit4", sp,

				"select sum(x) from input_marked group by n order by n limit 6;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlGroupByLimit5() throws IOException {
		testTableset.putTable(analyzeInputMarked);
		test(root, "test_sql_group_by_limit5", sp,

				"select * from input_marked limit 6, 5;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlGroupByLimit6() throws IOException {
		testTableset.putTable(analyzeInputMarked);
		test(root, "test_sql_group_by_limit6", sp,

				"select sum(x) from input_marked group by n order by n limit 6, 3;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlLimit7() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_limit7", sp,

				"select * from analyzeInput limit -1;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlLimit8() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_limit8", sp,

				"select * from analyzeInput limit 0, 4;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlLimit9() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_limit9", sp,

				"select * from analyzeInput limit 4, -2;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlLimit10() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_limit10", sp,

				"select * from analyzeInput limit 25;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlLimit11() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_limit11", sp,

				"select * from analyzeInput limit 18, 25;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlLimit12() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_limit12", sp,

				"create table t as select * from analyzeInput; update t set x=y limit 9;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlLimit13() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_limit13", sp,

				"select * from analyzeInput limit ;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlLimit14() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_limit14", sp,

				"create table t as select * from analyzeInput; update t set x=y where n % 2 == 0 limit 9;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlLimit15() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_limit15", sp,

				"create table t as select * from analyzeInput; delete from t limit 9;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlLimit16() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_limit16", sp,

				"create table t as select * from analyzeInput; delete from t where n % 2 == 0 limit 9;"

				, testTableset, new HashMap<String, Object>());
	}

	@Test
	public void testSqlLimit17() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_limit17", sp,

				"create table t as select * from analyzeInput; update t set x=y limit 4, 9;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlLimit18() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_limit18", sp,

				"create table t as select * from analyzeInput union select * from analyzeInput; update t set x=y where n % 2 == 0 limit 4, 9;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlLimit19() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_limit19", sp,

				"create table t as select * from analyzeInput; delete from t limit 4, 9;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlLimit20() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_limit20", sp,

				"create table t as select * from analyzeInput union select * from analyzeInput; delete from t where n % 2 == 0 limit 4, 9;"

				, testTableset, new HashMap<String, Object>());
	}

	@Test
	public void testSqlGroupBy2() throws IOException {
		testTableset.putTable(analyzeInputMarked);
		test(root, "test_sql_group_by2", sp,

				"select sum(x), avg(y) from input_marked group by n == 2;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlMisc8() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_misc8", sp,

				"select x, *, * from analyzeInput;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlGroupBy3() throws IOException {
		testTableset.putTable(analyzeInputMarked);
		testTableset.putTable(groups);
		test(root, "test_sql_group_by3", sp,

				"select n, sum(x), avg(y), sum(x + y) from input_marked, groups group by n order by n;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlOrderBy1() throws IOException {
		testTableset.putTable(analyzeInput);
		testTableset.putTable(groups);
		test(root, "test_sql_order_by1", sp,

				"select * from analyzeInput, groups order by n;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlOrderBy2() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_order_by2", sp,

				"select * from analyzeInput order by x, y;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlOrderBy3() throws IOException {
		testTableset.putTable(analyzeInput);
		testTableset.putTable(groups);
		test(root, "test_sql_order_by3", sp,

				"select * from analyzeInput, groups order by g, n;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlOrderBy4() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_order_by4", sp,

				"select * from analyzeInput order by x asc;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlOrderBy5() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_order_by5", sp,

				"select * from analyzeInput order by x desc;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlOrderBy6() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_order_by6", sp,

				"select * from analyzeInput order by x asc, y desc;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlMisc9() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_misc9", sp,

				"select n + max(n) from analyzeInput;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlMisc10() throws IOException {
		testTableset.putTable(analyzeInputMarked);
		test(root, "test_sql_misc10", sp,

				"select n + max(n) from input_marked group by g;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlOrderyByLimit1() throws IOException {
		testTableset.putTable(limitTest);
		test(root, "test_sql_order_by_limit1", sp,

				"select sum(n) from limitTest order by n;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlOrderyByLimit2() throws IOException {
		testTableset.putTable(limitTest);
		test(root, "test_sql_order_by_limit2", sp,

				"select sum(n) from limitTest order by n limit 6;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlOrderyByLimit3() throws IOException {
		testTableset.putTable(limitTest);
		test(root, "test_sql_order_by_limit3", sp,

				"select sum(n) from limitTest order by n limit 6, 3;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlOrderyByLimit4() throws IOException {
		testTableset.putTable(limitTest);
		test(root, "test_sql_order_by_limit4", sp,

				"select sum(n) from limitTest group by m order by n limit 6;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlOrderyByLimit5() throws IOException {
		testTableset.putTable(limitTest);
		test(root, "test_sql_order_by_limit5", sp,

				"select sum(n) from limitTest group by m order by n limit 6, 3;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAlias1() throws IOException {
		testTableset.putTable(analyzeInputMarked);
		test(root, "test_sql_alias1", sp,

				"select n, x, y, g as g2 from input_marked order by g2;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAlias2() throws IOException {
		testTableset.putTable(analyzeInputMarked);
		test(root, "test_sql_alias2", sp,

				"select sum(n), g as g2 from input_marked group by n;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAlias3() throws IOException {
		testTableset.putTable(analyzeInputMarked);
		test(root, "test_sql_alias3", sp,

				"select sum(n), g as g2 from input_marked group by g having g2 < 3;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlAlias4() throws IOException {
		testTableset.putTable(analyzeInputMarked);
		test(root, "test_sql_alias4", sp,

				"select sum(n), g as g2 from input_marked group by g having g < 3;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlInsertIntoFrom1() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(tableF);
		test(root, "test_sql_insert_into_from1", sp,

				"create table A as select * from tableA;\n" +

						"create table F as select * from tableF;\n" +

						"insert into A from select * from F;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlInsertIntoFrom2() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(tableF);
		test(root, "test_sql_insert_into_from2", sp,

				"create table A as select * from tableA;\n" +

						"create table F as select * from tableF;\n" +

						"insert into A select * from F;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlGroupByCompositeOperation() throws IOException {
		testTableset.putTable(analyzeInput);
		testTableset.putTable(tableA);
		test(root, "test_sql_group_by_composite_operation", sp,

				"select sum(analyzeInput.x + tableA.y) * count(tableA.z + analyzeInput.y) as o from analyzeInput, tableA group by n;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinEmpty1() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(empty);
		test(root, "test_sql_join_empty1", sp,

				"SELECT * FROM tableA OUTER ONLY JOIN empty ON empty.x == tableA.x;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinEmpty2() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(empty);
		test(root, "test_sql_join_empty2", sp,

				"SELECT * FROM tableA LEFT  ONLY JOIN empty ON empty.x == tableA.x;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinEmpty3() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(empty);
		test(root, "test_sql_join_empty3", sp,

				"SELECT * FROM tableA RIGHT ONLY JOIN empty ON empty.x == tableA.x;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinEmpty4() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(empty);
		test(root, "test_sql_join_empty4", sp,

				"SELECT * FROM tableA OUTER      JOIN empty ON empty.x == tableA.x;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinEmpty5() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(empty);
		test(root, "test_sql_join_empty5", sp,

				"SELECT * FROM tableA LEFT       JOIN empty ON empty.x == tableA.x;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinEmpty6() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(empty);
		test(root, "test_sql_join_empty6", sp,

				"SELECT * FROM tableA RIGHT      JOIN empty ON empty.x == tableA.x;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinEmpty7() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(empty);
		test(root, "test_sql_join_empty7", sp,

				"SELECT * FROM tableA            JOIN empty ON empty.x == tableA.x;"

				, testTableset, new HashMap<String, Object>());
	}

	@Test
	public void testSqlJoinEmpty8() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(empty);
		test(root, "test_sql_join_empty8", sp,

				"SELECT * FROM empty OUTER ONLY JOIN tableA ON tableA.x == empty.x;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinEmpty9() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(empty);
		test(root, "test_sql_join_empty9", sp,

				"SELECT * FROM empty LEFT  ONLY JOIN tableA ON tableA.x == empty.x;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinEmpty10() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(empty);
		test(root, "test_sql_join_empty10", sp,

				"SELECT * FROM empty RIGHT ONLY JOIN tableA ON tableA.x == empty.x;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinEmpty11() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(empty);
		test(root, "test_sql_join_empty11", sp,

				"SELECT * FROM empty OUTER      JOIN tableA ON tableA.x == empty.x;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinEmpty12() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(empty);
		test(root, "test_sql_join_empty12", sp,

				"SELECT * FROM empty LEFT       JOIN tableA ON tableA.x == empty.x;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinEmpty13() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(empty);
		test(root, "test_sql_join_empty13", sp,

				"SELECT * FROM empty RIGHT      JOIN tableA ON tableA.x == empty.x;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinEmpty14() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(empty);
		test(root, "test_sql_join_empty14", sp,

				"SELECT * FROM empty            JOIN tableA ON tableA.x == empty.x;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testComparisons() throws IOException {
		test(root, "test_comparisons", sp,

				"{\n" +

						"int result = 0;\n" +

						"if (7 != 7L) { result = 1; }\n" +

						"else if (7 != 7.0) { result = 2; }\n" +

						"else if (7 != 7d) { result = 3; }\n" +

						"else if (7 != \"7\") { result = 4; }\n" +

						"else if (true != \"true\") { result = 5; };\n" +

						"return result;\n"

						+ "}"

				, testTableset, new HashMap<String, Object>());

	}
	@Test
	public void testSqlRightJoinDifferentColumnTypes() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_right_join_different_column_types", sp,

				"select * from tableA right join analyzeInput on tableA.z == analyzeInput.n;"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlLeftJoinDifferentColumnTypes() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_left_join_different_column_types", sp,

				"select * from tableA left join analyzeInput on tableA.z == analyzeInput.n;"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlRightOnlyJoinDifferentColumnTypes() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_right_only_join_different_column_types", sp,

				"select * from tableA right only join analyzeInput on tableA.z == analyzeInput.n;"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlLeftOnlyJoinDifferentColumnTypes() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_left_only_join_different_column_types", sp,

				"select * from tableA left only join analyzeInput on tableA.z == analyzeInput.n;"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlJoinDifferentColumnTypes() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_join_different_column_types", sp,

				"select * from tableA join analyzeInput on tableA.z == analyzeInput.n;"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlOuterJoinDifferentColumnTypes() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_outer_join_different_column_types", sp,

				"select * from tableA outer join analyzeInput on tableA.z == analyzeInput.n;"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlOuterOnlyJoinDifferentColumnTypes() throws IOException {
		testTableset.putTable(tableA);
		testTableset.putTable(analyzeInput);
		test(root, "test_sql_outer_only_join_different_column_types", sp,

				"select * from tableA outer only join analyzeInput on tableA.z == analyzeInput.n;"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlInsertInts() throws IOException {
		testTableset.putTable(intsEmpty);
		test(root, "test_sql_insert_ints", sp,

				"insert into intsEmpty values " + "(2.3), (5.6), (0.9), (1.4), (1), (2), (0), (3f), (3F), (3d), (3D), (3l), (3L);"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlInsertLongs() throws IOException {
		testTableset.putTable(longsEmpty);
		test(root, "test_sql_insert_longs", sp,

				"insert into longsEmpty values " + "(2.3), (5.6), (0.9), (1.4), (1), (2), (0), (3f), (3F), (3d), (3D), (3l), (3L);"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlInsertBytes() throws IOException {
		testTableset.putTable(bytesEmpty);
		test(root, "test_sql_insert_bytes", sp,

				"insert into bytesEmpty values " + "(2.3), (5.6), (0.9), (1.4), (1), (2), (0), (3f), (3F), (3d), (3D), (3l), (3L);"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlInsertShort() throws IOException {
		testTableset.putTable(shortsEmpty);
		test(root, "test_sql_insert_shorts", sp,

				"insert into shortsEmpty values " + "(2.3), (5.6), (0.9), (1.4), (1), (2), (0), (3f), (3F), (3d), (3D), (3l), (3L);"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlInsertFloats() throws IOException {
		testTableset.putTable(floatsEmpty);
		test(root, "test_sql_insert_floats", sp,

				"insert into floatsEmpty values " + "(2.3), (5.6), (0.9), (1.4), (1), (2), (0), (3f), (3F), (3d), (3D), (3l), (3L);"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlInsertDoubles() throws IOException {
		testTableset.putTable(doublesEmpty);
		test(root, "test_sql_insert_doubles", sp,

				"insert into doublesEmpty values " + "(2.3), (5.6), (0.9), (1.4), (1), (2), (0), (3f), (3F), (3d), (3D), (3l), (3L);"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlInsertBooleans() throws IOException {
		testTableset.putTable(booleansEmpty);
		test(root, "test_sql_insert_booleans", sp,

				"insert into booleansEmpty values " + "(true), (false), (\"true\"), (\"false\"), (null);"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlInsertChars() throws IOException {
		testTableset.putTable(charsEmpty);
		test(root, "test_sql_insert_chars", sp,

				"insert into charsEmpty values " + "(\'c\'), (\'z\'), (\'0\');"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlInsertStrings() throws IOException {
		testTableset.putTable(stringsEmpty);
		test(root, "test_sql_insert_strings", sp,

				"insert into stringsEmpty values " + "(2.3), (5.6), (0.9), (1.4), (1), (2), (0), (3f), (3F), (3d), (3D), (3l), (3L), (\'c\'), (\'z\'), (\"xyz\");"

				, testTableset, newVarset());
	}
	@Test
	public void testUpdateAllTypes() throws IOException {
		Table allTypes = new ColumnarTable(Integer.class, "id", Integer.class, "intCol", Long.class, "longCol", Double.class, "doubleCol", Float.class, "floatCol", Character.class,
				"charCol", String.class, "stringCol", Boolean.class, "booleanCol", Byte.class, "byteCol", Short.class, "shortCol");
		putTable("allTypes", allTypes);
		test(root, "test_sql_update_all_types", sp,

				"insert into allTypes values (0, 1, 1L, 1.2, 1.2, 'c', \"s\", true, 2, 3);\n"

						+ "update allTypes set intCol=null, longCol=null, doubleCol=null, floatCol=null, charCol=null, stringCol=null, booleanCol=null, byteCol=null, shortCol=null where id == 0;\n"

				, testTableset, newVarset());
	}
	private void putTable(String title, Table t) {
		if (OH.eq(t.getTitle(), title))
			testTableset.putTable(title, t);
		else
			testTableset.putTable(title, new ColumnarTable(t));
	}
	@Test
	public void testSqlCountStar() throws IOException {
		putTable("input", sampleInput);
		test(root, "test_sql_count_star", sp, "select count(*) from input where category == \"A\";", testTableset, newVarset());
	}
	@Test
	public void testMultipleAssignment() throws IOException {
		test(root, "test_multiple_assignment", sp, "{int x = 1, y = 2, z = 3;}", testTableset, newVarset());
	}
	@Test
	public void testMultiJoin() throws IOException {
		putTable("tA", tA);
		putTable("tB", tB);
		test(root, "test_multi_join", sp, "select * from tA, tB where tA.n == tB.n && tA.x == tB.y", testTableset, newVarset());
	}
	@Test
	public void testUpdateJoin() throws IOException {
		putTable("tA", tA);
		putTable("tB", tB);
		test(root, "test_update_join", sp, "create table tA_copy as select * from tA; update tA_copy join tB on tA_copy.x == tB.y set x=7d where tA_copy.n == tB.n;", testTableset,
				newVarset());
	}
	@Test
	public void testSqlSelectInvalidConstCast() throws IOException {
		putTable("tA", tA);
		test(root, "test_sql_select_invalid_const_cast", sp, "select (char) \"asdf\" from tA", testTableset, newVarset());
	}
	@Test
	public void testSqlUnionNulls() throws IOException {
		putTable("tA", tA);
		putTable("tB", tB);
		test(root, "test_sql_union_nulls", sp, "select x, (double) null from tA union select (int) null, y from tB;", testTableset, newVarset());
	}
	@Test
	public void testSqlAlterDoubleToString() throws IOException {
		putTable("tA", tA);
		test(root, "test_sql_alter_double_to_string", sp, "create table tA_copy as select * from tA; alter table tA_copy modify x as x String;", testTableset, newVarset());
	}
	@Test
	public void testSqlAlterDoubleToString2() throws IOException {
		putTable("tA", tA);
		test(root, "test_sql_alter_double_to_string2", sp, "create table tA_copy as select * from tA; alter table tA_copy modify x as xs String;", testTableset, newVarset());
	}
	@Test
	public void testSqlAlterAllTypesToString() throws IOException {
		testTableset.putTable(allTypesTbl);
		test(root, "test_sql_alter_all_types_to_string", sp,
				"create table allTypesTbl_copy as select * from allTypesTbl; alter table allTypesTbl_copy modify intCol as intCol String, modify longCol as longCol String, modify doubleCol as doubleCol String, modify charCol as charCol String, modify floatCol as floatCol String, modify booleanCol as booleanCol String, modify byteCol as byteCol String, modify shortCol as shortCol String;",
				testTableset, newVarset());
	}
	@Test
	public void testSqlJoinMultipleCols() throws IOException {
		testTableset.putTable(tA);
		testTableset.putTable(tB);
		testTableset.putTable(tC);
		test(root, "test_sql_join_multiple_cols", sp,

				"select * from tA, tB, tC where tA.n == tB.n && tB.n == tC.n;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlJoinMultipleCols2() throws IOException {
		testTableset.putTable(tA);
		testTableset.putTable(tB);
		test(root, "test_sql_join_multiple_cols2", sp,

				"select * from tA join tB on tA.n == tB.n && tA.x == tB.y;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlLeftJoinMultipleCols() throws IOException {
		testTableset.putTable(tA);
		testTableset.putTable(tB);
		test(root, "test_sql_left_join_multiple_cols", sp,

				"select * from tA left join tB on tA.n == tB.n && tA.x == tB.y;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlRightJoinMultipleCols() throws IOException {
		testTableset.putTable(tA);
		testTableset.putTable(tB);
		test(root, "test_sql_right_join_multiple_cols", sp,

				"select * from tA right join tB on tA.n == tB.n && tA.x == tB.y;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlOuterJoinMultipleCols() throws IOException {
		testTableset.putTable(tA);
		testTableset.putTable(tB);
		test(root, "test_sql_outer_join_multiple_cols", sp,

				"select * from tA outer join tB on tA.n == tB.n && tA.x == tB.y;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlLeftOnlyJoinMultipleCols() throws IOException {
		testTableset.putTable(tA);
		testTableset.putTable(tB);
		test(root, "test_sql_left_only_join_multiple_cols", sp,

				"select * from tA left only join tB on tA.n == tB.n && tA.x == tB.y;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlRightOnlyJoinMultipleCols() throws IOException {
		testTableset.putTable(tA);
		testTableset.putTable(tB);
		test(root, "test_sql_right_only_join_multiple_cols", sp,

				"select * from tA right only join tB on tA.n == tB.n && tA.x == tB.y;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlOuterOnlyJoinMultipleCols() throws IOException {
		testTableset.putTable(tA);
		testTableset.putTable(tB);
		test(root, "test_sql_outer_only_join_multiple_cols", sp,

				"select * from tA outer only join tB on tA.n == tB.n && tA.x == tB.y;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlGroupByMultipleCols() throws IOException {
		testTableset.putTable(groupByMulti);
		test(root, "test_sql_group_by_multiple_cols", sp,

				"select m, n, k, sum(x), avg(y) from groupByMulti group by m, n, k;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlGroupByDerived() throws IOException {
		testTableset.putTable(groupByMulti);
		test(root, "test_sql_group_by_derived_cols", sp,

				"select m/2,min(m),max(m),count(m) from groupByMulti group by m/2;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlGroupByBackref() throws IOException {
		testTableset.putTable(groupByMulti);
		test(root, "test_sql_group_by_backref_cols", sp,

				"select m/2 as blah,min(m),max(m),count(m) from groupByMulti group by blah;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlUnion() throws IOException {
		testTableset.putTable(tA);
		testTableset.putTable(tB);
		testTableset.putTable(tC);
		test(root, "test_sql_union", sp,

				"select * from tA union select * from tB union select * from tC union select x, n, y from tB union select y, x, n from tA;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlMultiUnionLimits() throws IOException {
		testTableset.putTable(tD);
		testTableset.putTable(tE);
		testTableset.putTable(tF);
		test(root, "test_sql_multi_union_limits", sp,

				"select * from tD limit 10 union select * from tE limit 8, 4 union select y, x, n from tF where n % 2 == 0 limit 10 union select * from tE where n % 2 == 0 limit 5, 9;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlMultiUnionLimits2() throws IOException {
		testTableset.putTable(tD);
		testTableset.putTable(tE);
		testTableset.putTable(tF);
		test(root, "test_sql_multi_union_limits2", sp,

				"int m = 7; int n = 4; create table u2 as select * from tD limit -3 union select * from tF limit n union select * from tF limit m, n union select * from tE limit 3, -5;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlMultiUnionGroupBy() throws IOException {
		testTableset.putTable(groupByMulti);
		test(root, "test_sql_multi_union_group_by", sp,

				"select m, sum(n) as n, sum(k) as k, avg(x) as x, sum(y) as y from groupByMulti group by m union " +

						"select m, n, sum(k), avg(x) as x, sum(y) as y from groupByMulti group by m, n union " +

						"select m, n, k, avg(x) as x, sum(y) as y from groupByMulti group by m, n, k union " +

						"select m, sum(n) as n, sum(k) as k, avg(x) as x, sum(y) as y from groupByMulti where m % 2 == 0 group by m union " +

						"select m, n, sum(k), avg(x) as x, sum(y) as y from groupByMulti where m % 2 == 0 group by m, n union " +

						"select m, n, k, avg(x) as x, sum(y) as y from groupByMulti where m % 2 == 0 group by m, n, k union " +

						"select m, sum(n) as n, sum(k) as k, avg(x) as x, sum(y) as y from groupByMulti where m % 2 == 0 group by m having x < 0.5 union " +

						"select m, n, sum(k), avg(x) as x, sum(y) as y from groupByMulti where m % 2 == 0 group by m, n having x < 0.5 union " +

						"select m, n, k, avg(x) as x, sum(y) as y from groupByMulti where m % 2 == 0 group by m, n, k having x < 0.5;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlDerivedTable() throws IOException {
		testTableset.putTable(tA);
		test(root, "test_sql_derived_table", sp,

				"select * from (select * from tA where n % 2 == 0) where n % 3 == 0;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlDerivedJoin() throws IOException {
		testTableset.putTable(tA);
		testTableset.putTable(tB);
		testTableset.putTable(tC);
		test(root, "test_sql_derived_join", sp,

				"select * from (select tA.x as x from tA join tB on tA.x == tB.y) as tX join (select tC.y as y from tB join tC on tB.x == tC.y) as tY on tX.x == tY.y;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlDerivedUnion() throws IOException {
		testTableset.putTable(tA);
		testTableset.putTable(tB);
		testTableset.putTable(tC);
		test(root, "test_sql_derived_union", sp,

				"select * from (select * from tA union select * from tB) union select * from (select * from tB union select * from tC);"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlDerivedJoin2() throws IOException {
		testTableset.putTable(tA);
		testTableset.putTable(tB);
		testTableset.putTable(tC);
		test(root, "test_sql_derived_join2", sp,

				"select tA.n as n, tA.x as Ax, tA.y as Ay, tB.x as Bx, tB.y as By, tC.x as Cx, tC.y as Cy from (select * from tA) as tA, (select * from tB) as tB, (select * from tC) as tC where tA.n == tB.n && tB.n == tC.n;"

				, testTableset, new HashMap<String, Object>());
	}
	@Test
	public void testSqlNearestJoinLeft() throws IOException {
		generateNearestJoinTest("id", "id", JOIN_TYPE_LEFT, false);
	}
	@Test
	public void testSqlNearestJoinLeftOnly() throws IOException {
		generateNearestJoinTest("id", "id", JOIN_TYPE_LEFT_ONLY, false);
	}
	@Test
	public void testSqlNearestJoinRight() throws IOException {
		generateNearestJoinTest("id", "id", JOIN_TYPE_RIGHT, false);
	}
	@Test
	public void testSqlNearestJoinRightOnly() throws IOException {
		generateNearestJoinTest("id", "id", JOIN_TYPE_RIGHT_ONLY, false);
	}
	@Test
	public void testSqlNearestJoinOuter() throws IOException {
		generateNearestJoinTest("id", "id", JOIN_TYPE_OUTER, false);
	}
	@Test
	public void testSqlNearestJoinOuterOnly() throws IOException {
		generateNearestJoinTest("id", "id", JOIN_TYPE_OUTER_ONLY, false);
	}
	@Test
	public void testSqlNearestJoinInner() throws IOException {
		generateNearestJoinTest("id", "id", JOIN_TYPE_INNER, false);
	}
	@Test
	public void testSqlNearestJoinLeftIds() throws IOException {
		generateNearestJoinTest("id", "id", JOIN_TYPE_LEFT, true);
	}
	@Test
	public void testSqlNearestJoinLeftOnlyIds() throws IOException {
		generateNearestJoinTest("id", "id", JOIN_TYPE_LEFT_ONLY, true);
	}
	@Test
	public void testSqlNearestJoinRightIds() throws IOException {
		generateNearestJoinTest("id", "id", JOIN_TYPE_RIGHT, true);
	}
	@Test
	public void testSqlNearestJoinRightOnlyIds() throws IOException {
		generateNearestJoinTest("id", "id", JOIN_TYPE_RIGHT_ONLY, true);
	}
	@Test
	public void testSqlNearestJoinOuterIds() throws IOException {
		generateNearestJoinTest("id", "id", JOIN_TYPE_OUTER, true);
	}
	@Test
	public void testSqlNearestJoinOuterOnlyIds() throws IOException {
		generateNearestJoinTest("id", "id", JOIN_TYPE_OUTER_ONLY, true);
	}
	@Test
	public void testSqlNearestJoinInnerIds() throws IOException {
		generateNearestJoinTest("id", "id", JOIN_TYPE_INNER, true);
	}
	@Test
	public void testSqlJoinsNullPostFilter() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n", Double.class, "x");
		Table B = new ColumnarTable(Integer.class, "n", Double.class, "x");
		A.getRows().addRow(0, 2.3);
		A.getRows().addRow(1, 4.4);
		A.getRows().addRow(2, 0.5);
		A.getRows().addRow(3, 7.8);
		B.getRows().addRow(0, 6.3);
		B.getRows().addRow(1, 4.0);
		B.getRows().addRow(7, 3.3);
		putTable("A", A);
		putTable("B", B);
		test(root, "test_sql_joins_null_post_filter", sp,

				"create table l as select * from A left join B on A.n == B.n where B.n == null;" + SH.NEWLINE +

						"create table lo as select * from A left only join B on A.n == B.n where B.n == null;" + SH.NEWLINE +

						"create table r as select * from B right join A on B.n == A.n where B.n == null;" + SH.NEWLINE +

						"create table ro as select * from B right only join A on B.n == A.n where B.n == null;" + SH.NEWLINE +

						"create table o as select * from A outer join B on A.n == B.n where A.n == null || B.n == null;" + SH.NEWLINE +

						"create table oo as select * from A outer only join B on A.n == B.n where A.n == null || B.n == null;"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlJoinsEqPostFilter() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n", Integer.class, "x", Integer.class, "y");
		Table B = new ColumnarTable(Integer.class, "n", Integer.class, "x", Integer.class, "y");
		A.getRows().addRow(-1, 0, 1);
		A.getRows().addRow(0, 0, 0);
		A.getRows().addRow(1, 0, 1);
		A.getRows().addRow(2, 1, 0);
		A.getRows().addRow(3, 1, 1);
		A.getRows().addRow(4, 0, 0);

		B.getRows().addRow(1, 0, 0);
		B.getRows().addRow(2, 0, 1);
		B.getRows().addRow(3, 1, 0);
		B.getRows().addRow(4, 1, 1);
		B.getRows().addRow(5, 0, 0);
		B.getRows().addRow(6, 0, 1);

		putTable("A", A);
		putTable("B", B);

		test(root, "test_sql_joins_eq_post_filter", sp,

				"create table la as select * from A left join B on A.n == B.n where A.x == A.y;" + SH.NEWLINE +

						"create table loa as select * from A left only join B on A.n == B.n where A.x == A.y;" + SH.NEWLINE +

						"create table ra as select * from A right join B on A.n == B.n where A.x == A.y;" + SH.NEWLINE +

						"create table roa as select * from A right only join B on A.n == B.n where A.x == A.y;" + SH.NEWLINE +

						"create table oa as select * from A outer join B on A.n == B.n where A.x == A.y;" + SH.NEWLINE +

						"create table ooa as select * from A outer only join B on A.n == B.n where A.x == A.y;" + SH.NEWLINE +

						"create table ia as select * from A join B on A.n == B.n where A.x == A.y;" + SH.NEWLINE +

						"create table lb as select * from A left join B on A.n == B.n where B.x == B.y;" + SH.NEWLINE +

						"create table lob as select * from A left only join B on A.n == B.n where B.x == B.y;" + SH.NEWLINE +

						"create table rb as select * from A right join B on A.n == B.n where B.x == B.y;" + SH.NEWLINE +

						"create table rob as select * from A right only join B on A.n == B.n where B.x == B.y;" + SH.NEWLINE +

						"create table ob as select * from A outer join B on A.n == B.n where B.x == B.y;" + SH.NEWLINE +

						"create table oob as select * from A outer only join B on A.n == B.n where B.x == B.y;" + SH.NEWLINE +

						"create table ib as select * from A join B on A.n == B.n where B.x == B.y;"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlJoinsNeqPostFilter() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n", Integer.class, "x", Integer.class, "y");
		Table B = new ColumnarTable(Integer.class, "n", Integer.class, "x", Integer.class, "y");
		A.getRows().addRow(-1, 0, 1);
		A.getRows().addRow(0, 0, 0);
		A.getRows().addRow(1, 0, 1);
		A.getRows().addRow(2, 1, 0);
		A.getRows().addRow(3, 1, 1);
		A.getRows().addRow(4, 0, 0);

		B.getRows().addRow(1, 0, 0);
		B.getRows().addRow(2, 0, 1);
		B.getRows().addRow(3, 1, 0);
		B.getRows().addRow(4, 1, 1);
		B.getRows().addRow(5, 0, 0);
		B.getRows().addRow(6, 0, 1);

		putTable("A", A);
		putTable("B", B);

		test(root, "test_sql_joins_neq_post_filter", sp,

				"create table la as select * from A left join B on A.n == B.n where A.x != A.y;" + SH.NEWLINE +

						"create table loa as select * from A left only join B on A.n == B.n where A.x != A.y;" + SH.NEWLINE +

						"create table ra as select * from A right join B on A.n == B.n where A.x != A.y;" + SH.NEWLINE +

						"create table roa as select * from A right only join B on A.n == B.n where A.x != A.y;" + SH.NEWLINE +

						"create table oa as select * from A outer join B on A.n == B.n where A.x != A.y;" + SH.NEWLINE +

						"create table ooa as select * from A outer only join B on A.n == B.n where A.x != A.y;" + SH.NEWLINE +

						"create table ia as select * from A join B on A.n == B.n where A.x != A.y;" + SH.NEWLINE +

						"create table lb as select * from A left join B on A.n == B.n where B.x != B.y;" + SH.NEWLINE +

						"create table lob as select * from A left only join B on A.n == B.n where B.x != B.y;" + SH.NEWLINE +

						"create table rb as select * from A right join B on A.n == B.n where B.x != B.y;" + SH.NEWLINE +

						"create table rob as select * from A right only join B on A.n == B.n where B.x != B.y;" + SH.NEWLINE +

						"create table ob as select * from A outer join B on A.n == B.n where B.x != B.y;" + SH.NEWLINE +

						"create table oob as select * from A outer only join B on A.n == B.n where B.x != B.y;" + SH.NEWLINE +

						"create table ib as select * from A join B on A.n == B.n where B.x != B.y;"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlDeleteInnerJoin() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		Integer nullInt = null;
		A.getRows().addRow(nullInt);
		A.getRows().addRow(0);
		A.getRows().addRow(1);
		A.getRows().addRow(2);
		A.getRows().addRow(3);
		A.getRows().addRow(4);

		B.getRows().addRow(nullInt);
		B.getRows().addRow(1);
		B.getRows().addRow(2);
		B.getRows().addRow(3);
		B.getRows().addRow(4);
		B.getRows().addRow(5);

		putTable("A", A);
		putTable("B", B);

		test(root, "test_sql_delete_inner_join", sp,

				"delete from A join B on A.n == B.n"

				, testTableset, newVarset());

	}
	@Test
	public void testSqlDeleteLeftJoin() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		Integer nullInt = null;
		A.getRows().addRow(nullInt);
		A.getRows().addRow(0);
		A.getRows().addRow(1);
		A.getRows().addRow(2);
		A.getRows().addRow(3);
		A.getRows().addRow(4);

		B.getRows().addRow(nullInt);
		B.getRows().addRow(1);
		B.getRows().addRow(2);
		B.getRows().addRow(3);
		B.getRows().addRow(4);
		B.getRows().addRow(5);

		putTable("A", A);
		putTable("B", B);

		test(root, "test_sql_delete_left_join", sp,

				"delete from A left join B on A.n == B.n"

				, testTableset, newVarset());

	}
	@Test
	public void testSqlDeleteLeftOnlyJoin() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		Integer nullInt = null;
		A.getRows().addRow(nullInt);
		A.getRows().addRow(0);
		A.getRows().addRow(1);
		A.getRows().addRow(2);
		A.getRows().addRow(3);
		A.getRows().addRow(4);

		B.getRows().addRow(nullInt);
		B.getRows().addRow(1);
		B.getRows().addRow(2);
		B.getRows().addRow(3);
		B.getRows().addRow(4);
		B.getRows().addRow(5);

		putTable("A", A);
		putTable("B", B);

		test(root, "test_sql_delete_left_only_join", sp,

				"delete from A left only join B on A.n == B.n"

				, testTableset, newVarset());

	}
	@Test
	public void testSqlUpdateInnerJoin() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		Integer nullInt = null;
		A.getRows().addRow(nullInt);
		A.getRows().addRow(0);
		A.getRows().addRow(1);
		A.getRows().addRow(2);
		A.getRows().addRow(3);
		A.getRows().addRow(4);

		B.getRows().addRow(nullInt);
		B.getRows().addRow(1);
		B.getRows().addRow(2);
		B.getRows().addRow(3);
		B.getRows().addRow(4);
		B.getRows().addRow(5);

		putTable("A", A);
		putTable("B", B);

		test(root, "test_sql_update_inner_join", sp,

				"update A join B on A.n == B.n set n=999"

				, testTableset, newVarset());

	}
	@Test
	public void testSqlUpdateLeftJoin() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		Integer nullInt = null;
		A.getRows().addRow(nullInt);
		A.getRows().addRow(0);
		A.getRows().addRow(1);
		A.getRows().addRow(2);
		A.getRows().addRow(3);
		A.getRows().addRow(4);

		B.getRows().addRow(nullInt);
		B.getRows().addRow(1);
		B.getRows().addRow(2);
		B.getRows().addRow(3);
		B.getRows().addRow(4);
		B.getRows().addRow(5);

		putTable("A", A);
		putTable("B", B);

		test(root, "test_sql_update_left_join", sp,

				"update A left join B on A.n == B.n set n=999"

				, testTableset, newVarset());

	}
	@Test
	public void testSqlUpdateLeftOnlyJoin() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		Integer nullInt = null;
		A.getRows().addRow(nullInt);
		A.getRows().addRow(0);
		A.getRows().addRow(1);
		A.getRows().addRow(2);
		A.getRows().addRow(3);
		A.getRows().addRow(4);

		B.getRows().addRow(nullInt);
		B.getRows().addRow(1);
		B.getRows().addRow(2);
		B.getRows().addRow(3);
		B.getRows().addRow(4);
		B.getRows().addRow(5);

		putTable("A", A);
		putTable("B", B);

		test(root, "test_sql_update_left_only_join", sp,

				"update A left only join B on A.n == B.n set n=999"

				, testTableset, newVarset());

	}

	/////////////////////// BEGIN UPDATE JOIN LIMIT TESTS ///////////////////////
	@Test
	public void testSqlUpdateInnerJoinLimit() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		for (int i = 0; i < 15; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + 5);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", true, null, 5, JOIN_TYPE_INNER, null, false);
	}
	@Test
	public void testSqlUpdateLeftJoinLimit() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		for (int i = 0; i < 15; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + 5);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", true, null, 5, JOIN_TYPE_LEFT, null, false);
	}
	@Test
	public void testSqlUpdateLeftOnlyJoinLimit() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		for (int i = 0; i < 15; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + 5);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", true, null, 3, JOIN_TYPE_LEFT_ONLY, null, false);
	}
	@Test
	public void testSqlUpdateInnerJoinLimitSkip() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		for (int i = 0; i < 15; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + 5);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", true, 3, 5, JOIN_TYPE_INNER, null, false);
	}
	@Test
	public void testSqlUpdateLeftJoinLimitSkip() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		for (int i = 0; i < 15; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + 5);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", true, 3, 5, JOIN_TYPE_LEFT, null, false);
	}
	@Test
	public void testSqlUpdateLeftOnlyJoinLimitSkip() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		for (int i = 0; i < 15; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + 5);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", true, 3, 5, JOIN_TYPE_LEFT_ONLY, null, false);
	}
	@Test
	public void testSqlUpdateInnerJoinLimitWhereClause() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		for (int i = 0; i < 15; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + 5);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", true, null, 5, JOIN_TYPE_INNER, " where n % 2 == 0", false);
	}
	@Test
	public void testSqlUpdateLeftJoinLimitWhereClause() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		for (int i = 0; i < 15; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + 5);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", true, null, 5, JOIN_TYPE_LEFT, " where n % 2 == 0", false);
	}
	@Test
	public void testSqlUpdateLeftOnlyJoinLimitWhereClause() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		for (int i = 0; i < 15; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + 5);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", true, null, 5, JOIN_TYPE_LEFT_ONLY, " where n % 2 == 0", false);
	}
	@Test
	public void testSqlUpdateInnerJoinLimitSkipWhereClause() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		for (int i = 0; i < 15; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + 5);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", true, 3, 5, JOIN_TYPE_INNER, " where n % 2 == 0", false);
	}
	@Test
	public void testSqlUpdateLeftJoinLimitSkipWhereClause() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		for (int i = 0; i < 15; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + 5);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", true, 3, 5, JOIN_TYPE_LEFT, " where n % 2 == 0", false);
	}
	@Test
	public void testSqlUpdateLeftOnlyJoinLimitSkipWhereClause() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		for (int i = 0; i < 15; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + 5);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", true, 1, 5, JOIN_TYPE_LEFT_ONLY, " where n % 2 == 0", false);
	}
	@Test
	public void testSqlUpdateInnerJoinExceedLimit() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		int totalRows = 15;
		int rightOffset = 5;
		for (int i = 0; i < totalRows; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + rightOffset);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", true, null, totalRows - rightOffset + 2, JOIN_TYPE_INNER, null, true);
	}
	@Test
	public void testSqlUpdateLeftJoinExceedLimit() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		int totalRows = 15;
		int rightOffset = 5;
		for (int i = 0; i < totalRows; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + rightOffset);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", true, null, totalRows - rightOffset + 2, JOIN_TYPE_LEFT, null, true);
	}
	@Test
	public void testSqlUpdateLeftOnlyJoinExceedLimit() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		int totalRows = 15;
		int rightOffset = 5;
		for (int i = 0; i < totalRows; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + rightOffset);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", true, null, totalRows - rightOffset + 2, JOIN_TYPE_LEFT_ONLY, null, true);
	}
	/////////////////////// BEGIN UPDATE JOIN LIMIT TESTS ///////////////////////

	/////////////////////// BEGIN DELETE JOIN LIMIT TESTS ///////////////////////
	@Test
	public void testSqlDeleteInnerJoinLimit() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		for (int i = 0; i < 15; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + 5);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", false, null, 5, JOIN_TYPE_INNER, null, false);
	}
	@Test
	public void testSqlDeleteLeftJoinLimit() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		for (int i = 0; i < 15; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + 5);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", false, null, 5, JOIN_TYPE_LEFT, null, false);
	}
	@Test
	public void testSqlDeleteLeftOnlyJoinLimit() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		for (int i = 0; i < 15; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + 5);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", false, null, 3, JOIN_TYPE_LEFT_ONLY, null, false);
	}
	@Test
	public void testSqlDeleteInnerJoinLimitSkip() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		for (int i = 0; i < 15; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + 5);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", false, 3, 5, JOIN_TYPE_INNER, null, false);
	}
	@Test
	public void testSqlDeleteLeftJoinLimitSkip() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		for (int i = 0; i < 15; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + 5);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", false, 3, 5, JOIN_TYPE_LEFT, null, false);
	}
	@Test
	public void testSqlDeleteLeftOnlyJoinLimitSkip() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		for (int i = 0; i < 15; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + 5);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", false, 3, 5, JOIN_TYPE_LEFT_ONLY, null, false);
	}
	@Test
	public void testSqlDeleteInnerJoinLimitWhereClause() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		for (int i = 0; i < 15; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + 5);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", false, null, 5, JOIN_TYPE_INNER, " where n % 2 == 0", false);
	}
	@Test
	public void testSqlDeleteLeftJoinLimitWhereClause() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		for (int i = 0; i < 15; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + 5);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", false, null, 5, JOIN_TYPE_LEFT, " where n % 2 == 0", false);
	}
	@Test
	public void testSqlDeleteLeftOnlyJoinLimitWhereClause() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		for (int i = 0; i < 15; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + 5);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", false, null, 5, JOIN_TYPE_LEFT_ONLY, " where n % 2 == 0", false);
	}
	@Test
	public void testSqlDeleteInnerJoinLimitSkipWhereClause() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		for (int i = 0; i < 15; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + 5);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", false, 3, 5, JOIN_TYPE_INNER, " where n % 2 == 0", false);
	}
	@Test
	public void testSqlDeleteLeftJoinLimitSkipWhereClause() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		for (int i = 0; i < 15; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + 5);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", false, 3, 5, JOIN_TYPE_LEFT, " where n % 2 == 0", false);
	}
	@Test
	public void testSqlDeleteLeftOnlyJoinLimitSkipWhereClause() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		for (int i = 0; i < 15; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + 5);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", false, 1, 5, JOIN_TYPE_LEFT_ONLY, " where n % 2 == 0", false);
	}
	@Test
	public void testSqlDeleteInnerJoinExceedLimit() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		int totalRows = 15;
		int rightOffset = 5;
		for (int i = 0; i < totalRows; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + rightOffset);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", false, null, totalRows - rightOffset + 2, JOIN_TYPE_INNER, null, true);
	}
	@Test
	public void testSqlDeleteLeftJoinExceedLimit() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		int totalRows = 15;
		int rightOffset = 5;
		for (int i = 0; i < totalRows; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + rightOffset);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", false, null, totalRows - rightOffset + 2, JOIN_TYPE_LEFT, null, true);
	}
	@Test
	public void testSqlDeleteLeftOnlyJoinExceedLimit() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		int totalRows = 15;
		int rightOffset = 5;
		for (int i = 0; i < totalRows; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + rightOffset);
		}
		generateUpdateDeleteJoinLimitTest(A, B, "n", "n", false, null, totalRows - rightOffset + 2, JOIN_TYPE_LEFT_ONLY, null, true);
	}
	/////////////////////// BEGIN DELETE JOIN LIMIT TESTS ///////////////////////

	@Test
	public void testSqlInClauseMultiCol() throws IOException {
		Table A = new ColumnarTable(Integer.class, "m", Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "m", Integer.class, "n");
		Integer nullInt = null;
		A.getRows().addRow(nullInt, nullInt);
		for (int i = 0; i < 20; i++) {
			A.getRows().addRow(i, 2 * i);
		}
		for (int i = 0; i < 10; i++) {
			B.getRows().addRow(i, 2 * i);
		}
		for (int i = 0; i < 5; i++) {
			B.getRows().addRow(100 * i, 200 * i);
		}

		putTable("A", A);
		putTable("B", B);

		test(root, "test_sql_in_clause_multi_col", sp,

				"create table const_no_prefixes as select * from A where (m, n) in ((0,0), (3,6), (7, 14), (100, 300));" + SH.NEWLINE +

						"create table const_prefixes as select * from A where (A.m, A.n) in ((0,0), (3,6), (7, 14), (100, 300));" + SH.NEWLINE +

						"create table sel_no_prefixes as select * from A where (m, n) in (select * from B where m % 2 == 0);" + SH.NEWLINE +

						"create table sel_prefixes as select * from A where (A.m, A.n) in (select * from B where m % 2 == 0);"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlDeleteLeftJoinNeq() throws IOException {
		Table A = new ColumnarTable(Integer.class, "n");
		Table B = new ColumnarTable(Integer.class, "n");
		Integer nullInt = null;
		for (int i = 0; i < 5; i++) {
			A.getRows().addRow(i);
			B.getRows().addRow(i + 2);
		}

		putTable("A", A);
		putTable("B", B);

		test(root, "test_sql_delete_left_join_neq", sp,

				"delete from A left join B on A.n != B.n;"

				, testTableset, newVarset());
	}

	@Test
	public void testSqlAnalyzeStar() throws IOException {
		putTable("analyzeInput", analyzeInput);
		test(root, "test_sql_analyze_star", sp,

				"ANALYZE *, n, sum(win.x) FROM analyzeInput WINDOW win ON n - 4 <= win.n && win.n < n + 4;"

				, testTableset, newVarset());
	}

	@Test
	public void testSqlExceptSelect() throws IOException {
		putTable("t", analyzeInputNullTimes);
		test(root, "test_sql_except_select", sp,

				"select x, * except (n, y), x from t;"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlExceptAnalyze() throws IOException {
		putTable("t", analyzeInput);
		test(root, "test_sql_except_analyze", sp,

				"analyze sum(win.x), * except (n, y) from t window win on n - 4 <= win.n && win.n < n + 4;"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlExceptPrepare() throws IOException {
		putTable("t", analyzeInputNullTimes);
		test(root, "test_sql_except_prepare", sp,

				"prepare stack(n), * except (n, y) from t"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlInClauseExpression() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_in_clause_expression", sp,

				"select * from tableA where (x + y) in (select z from tableA);"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlInClauseConsts() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_in_clause_consts", sp,

				"select * from tableA where (x, y) in (3, 8);"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlInClauseExpressionConsts() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_in_clause_expression_consts", sp,

				"select * from tableA where (x + y, x * y) in (9, 20);"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlInClauseExpressionNot() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_in_clause_expression_not", sp,

				"select * from tableA where (x + y) not in (select z from tableA);"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlInClauseConstsNot() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_in_clause_consts_not", sp,

				"select * from tableA where (x, y) not in (3, 8);"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlInClauseExpressionConstsNot() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_in_clause_expression_consts_not", sp,

				"select * from tableA where (x + y, x * y) not in (9, 20);"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlParentheses() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_parentheses", sp,

				"select (x*y) from tableA"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlNestePrep() throws IOException {
		testTableset.putTable(tableA);
		test(root, "test_sql_nested_prep", sp,

				"prepare x,stack(x),stack(offset(x,-1)),stack(offset(x,1)) from tableA"

				, testTableset, newVarset());
	}
	@Test
	public void testSqlUpdateColInWhereClause() throws IOException {
		Table t = new ColumnarTable(String.class, "col");
		String nullVal = null;
		t.getRows().addRow(nullVal);
		t.getRows().addRow("");
		t.getRows().addRow("zzz");
		t.getRows().addRow("abc");
		t.getRows().addRow("a123");
		t.getRows().addRow("xyz");
		t.getRows().addRow("123");
		t.getRows().addRow("123");
		t.getRows().addRow("yy");
		t.getRows().addRow("abcd");
		t.getRows().addRow("123");
		t.getRows().addRow("abcx");
		putTable("t", t);
		test(root, "test_sql_update_col_in_where_clause", sp,

				"update t set col=\"abcd\" where t.col == \"123\""

				, testTableset, newVarset());
	}
	@Test
	public void testSqlDeleteColInWhereClause() throws IOException {
		Table t = new ColumnarTable(String.class, "col");
		String nullVal = null;
		t.getRows().addRow(nullVal);
		t.getRows().addRow("");
		t.getRows().addRow("zzz");
		t.getRows().addRow("abc");
		t.getRows().addRow("a123");
		t.getRows().addRow("xyz");
		t.getRows().addRow("123");
		t.getRows().addRow("123");
		t.getRows().addRow("yy");
		t.getRows().addRow("abcd");
		t.getRows().addRow("123");
		t.getRows().addRow("abcx");
		putTable("t", t);
		test(root, "test_sql_delete_col_in_where_clause", sp,

				"delete from t where t.col == \"123\""

				, testTableset, newVarset());
	}

	@Test
	public void testAggMultiJoinGroupBy() throws IOException {
		Tableset tableset = newTableset();
		Table A = new ColumnarTable(String.class, "cat", String.class, "item");
		Table B = new ColumnarTable(String.class, "item", String.class, "cur", Double.class, "px");
		Table C = new ColumnarTable(String.class, "pid", String.class, "cust", String.class, "item");
		Table D = new ColumnarTable(String.class, "cust", String.class, "name", Integer.class, "age");
		Table E = new ColumnarTable(String.class, "cur", Double.class, "rate");

		//ItemCategory
		A.getRows().addRow("clothing", "item1");
		A.getRows().addRow("clothing", "item4");
		A.getRows().addRow("electronics", "item2");
		A.getRows().addRow("kitchen", "item3");
		A.getRows().addRow("kitchen", "item5");
		//Items
		B.getRows().addRow("item1", "USD", 22.40);
		B.getRows().addRow("item2", "EUR", 30.50);
		B.getRows().addRow("item3", "JPY", 10000d);
		B.getRows().addRow("item4", "CAD", 25d);
		B.getRows().addRow("item5", "USD", 10.00);
		//Purchases
		C.getRows().addRow("p1", "1", "item1");
		C.getRows().addRow("p2", "1", "item1");
		C.getRows().addRow("p3", "2", "item1");
		C.getRows().addRow("p4", "2", "item2");
		C.getRows().addRow("p5", "3", "item3");
		C.getRows().addRow("p6", "4", "item4");
		C.getRows().addRow("p7", "4", "item2");
		//Customers
		D.getRows().addRow("1", "James", 24);
		D.getRows().addRow("2", "Jackie", 32);
		D.getRows().addRow("3", "Jennifer", 20);
		D.getRows().addRow("4", "Jasmine", 37);
		//Conversion
		E.getRows().addRow("USD", 1d);
		E.getRows().addRow("EUR", 1.09);
		E.getRows().addRow("JPY", 0.0091);
		E.getRows().addRow("CAD", 0.75);
		E.getRows().addRow("KOR", 0.10);

		tableset.putTable("A", A);
		tableset.putTable("B", B);
		tableset.putTable("C", C);
		tableset.putTable("D", D);
		tableset.putTable("E", E);
		String testName = "test_multiple_table_joins_aggs_groupby";

		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE j AS SELECT ");
		sb.append("D.name, ");
		sb.append("A.cat, ");
		sb.append("sum( B.px * E.rate ) as totalPurchased");
		sb.append(" FROM A, B, C, D, E ");
		sb.append(" WHERE ");
		sb.append("A.item == B.item ").append(" and ");
		sb.append("B.item == C.item ").append(" and ");
		sb.append("C.cust == D.cust ").append(" and ");
		sb.append("B.cur == E.cur ").append(" and ");
		sb.append("true");
		sb.append(" GROUP BY ");
		sb.append("D.cust, A.cat;");
		test(root, testName, sp, sb.toString(), tableset, newVarset());
	}
	@Test
	public void testThrowCatchFalse() throws IOException {
		test(root, "test_throw_catch_false", sp, "{\n" + "  Integer i = 21;\n" + "  if (i > 20) { throw \"Value ${i} exceeds limit\"; }"
				+ "  else { return \"Value ${i} is within limit, don't throw\"; }\n" + "} catch(String s) {\n" + "  return s;\n" + "}", newTableset(), newVarset());
	}
	@Test
	public void testThrowCatchTrue() throws IOException {
		test(root, "test_throw_catch_true", sp, "{\n" + "  Integer i = 15;\n" + "  if (i > 20) { throw \"Value ${i} exceeds limit\"; }"
				+ "  else { return \"Value ${i} is within limit, don't throw\"; }\n" + "} catch(String s) {\n" + "  return s;\n" + "}", newTableset(), newVarset());
	}
	@Test
	public void testIfNotExists() throws IOException {
		Table x = new ColumnarTable(Integer.class, "x");
		x.getRows().addRow(0);
		putTable("x", x);
		test(root, "test_if_not_exists", sp, "{create table if not exists x (x int); create table if not exists y (y int); insert into x values(1); insert into y values(1); }",
				testTableset, newVarset());
	}
	//@Test
	//public void testIfExists() throws IOException {
	//	Table x = new ColumnarTable(Integer.class, "x");
	//	Table y = new ColumnarTable(Integer.class, "y");
	//	x.getRows().addRow(0);
	//	y.getRows().addRow(0);
	//	putTable("x", x);
	//	putTable("y", y);
	//	test(root, "test_if_exists", sp, "{drop table if exists x, y;}", testTableset, newVarset());
	//}
	@Test
	public void testOrClause00() throws IOException {
		test(root, "test_or_clause_00", sp, "{int a = 10; return (a == 10 || a == 20);}", newTableset(), newVarset());
	}
	@Test
	public void testOrClause01() throws IOException {
		test(root, "test_or_clause_01", sp, "{int a = 10; return (a == \"10\" || a == 10L || a == 10d || a == 10.0);}", newTableset(), newVarset());
	}
	@Test
	public void testOrClause02() throws IOException {
		test(root, "test_or_clause_02", sp, "{long a = 10; return (a == \"10\" || a == 10L || a == 10d || a == 10.0);}", newTableset(), newVarset());
	}
	@Test
	public void testOrClause03() throws IOException {
		test(root, "test_or_clause_03", sp, "{float a = 10; return (a == \"10\" || a == 10L || a == 10d || a == 10.0);}", newTableset(), newVarset());
	}
	@Test
	public void testOrClause04() throws IOException {
		test(root, "test_or_clause_04", sp, "{double a = 10; return (a == \"10\" || a == 10L || a == 10d || a == 10.0);}", newTableset(), newVarset());
	}
	@Test
	public void testOrClause05() throws IOException {
		test(root, "test_or_clause_05", sp, "{string a = \"10\"; return (a == \"10\" || a == 10L || a == 10d || a == 10.0);}", newTableset(), newVarset());
	}
	@Test
	public void testOrs01() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_ors_01", sp, "create table out as select * from analyzeInput where x < 0.0 || x > 8.0 || x == null", testTableset, globalVars);
	}
	@Test
	public void testOrs02() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_ors_02", sp, "create table out as select * from analyzeInput where x < 0.0 || x > 8.0 || x == null || y < 0.0 || y > 8.0 || y == null", testTableset,
				globalVars);
	}
	@Test
	public void testOrs03() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_ors_03", sp, "create table out as select * from analyzeInput where n == -1 || n == 0 || n == 1 || n == 2 || n == 0", testTableset, globalVars);
	}
	@Test
	public void testOrs04() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_ors_04", sp,
				"create table out as select * from analyzeInput where n == 0 || n == 1 || n == 2 || n == 3 || n == 4 || n == 5 || n == 6 || n == 7 || n == 8 || n == 9 || n == 10 || n == 11 || n == 12 || n == 13 || n == 14 || n == 15 || n == 16 || n == 17 || n == 18 || n == 19 || n == 20 || n == 21 ",
				testTableset, globalVars);
	}
	@Test
	public void testOrs05() throws IOException {
		testTableset.putTable(sampleInput);
		test(root, "test_ors_05", sp, "create table out as select * from input where x == null || y == null || z == \"null\"", testTableset, globalVars);
	}
	@Test
	public void testOrs06() throws IOException {
		testTableset.putTable(analyzeInput);
		test(root, "test_ors_06", sp,
				"create table out as select * from analyzeInput where n == 0 || n == \"1\" || n == 2.0D || n >= \"7\" || x == 1.8 || x == 0 || y == 6 || y == 7 || y == 5.800 || y == 6.7D",
				testTableset, globalVars);
	}
	@Test
	public void testOrs07() throws IOException {
		testTableset.putTable(t);
		test(root, "test_ors_07", sp,
				"create table out as select * from t where (((((((((v==3 || v==4) || v==5) || v==6) || v==7) || v==1.0) || v==5) || v==15) || v==\"9.0\") || v==null)",
				testTableset, globalVars);
	}
	@Test
	public void testOrs08() throws IOException {
		testTableset.putTable(t);
		test(root, "test_ors_08", sp, "create table out as select * from t where (((((((v==3 || v==5) || v==6) || v==7) || v==1.0) || v==5) || v==15) || v==\"9.0\") || v==null",
				testTableset, globalVars);
	}
	@Test
	public void testOrs09() throws IOException {
		testTableset.putTable(t);
		test(root, "test_ors_09", sp, "create table out as select * from t where (v==3 || (v==5 || (v==6 || (v==7 || (v==1.0 || (v==5 || (v==15 || (v==\"9.0\" || v==null))))))))",
				testTableset, globalVars);
	}
	@Test
	public void testOrs10() throws IOException {
		testTableset.putTable(analyzeInputWithBool);
		test(root, "test_ors_10", sp, "create table out as select * from analyzeInputWithBool where n <= 5 || n >= 10 || x < 0.0 || x > 5.0 || b == true;", testTableset,
				globalVars);
	}
	@Test
	public void testOrs11() throws IOException {
		testTableset.putTable(analyzeInputWithBool);
		test(root, "test_ors_11", sp, "create table out as select * from analyzeInputWithBool where n <= 5 || n >= 15 || x < 0.0 || b == false;", testTableset, globalVars);
	}
	@Test
	public void testSelectedTypes00() throws IOException {
		Table t = new ColumnarTable(String.class, "s", Integer.class, "i", Double.class, "d", Long.class, "l");
		t.getRows().addRow("1", 1, 1.0, 1L);
		t.getRows().addRow("2", 2, 2.0, 2L);
		t.getRows().addRow("3", 3, 3.0, 3L);
		putTable("t", t);
		test(root, "test_selected_types_00", sp,
				"select (true ? s : i) as c1, (false ? s : i) as c2, (true ? s : d) as c3, (false ? s : d) as c4, (true ? s : l) as c4, (false ? s : l) as c5 from t", testTableset,
				newVarset());
	}
	@Test
	public void testSelectedTypes01() throws IOException {
		Table t = new ColumnarTable(String.class, "s", Integer.class, "i", Double.class, "d", Long.class, "l");
		t.getRows().addRow("1", 1, 1.0, 1L);
		t.getRows().addRow("2", 2, 2.0, 2L);
		t.getRows().addRow("3", 3, 3.0, 3L);
		putTable("t", t);
		test(root, "test_selected_types_01", sp,
				"select (true ? d : s) as c1, (false ? d : s) as c2, (true ? d : i) as c3, (false ? d : i) as c4, (true ? d : l) as c4, (false ? d : l) as c5 from t", testTableset,
				newVarset());
	}
	@Test
	public void testSelectedTypes02() throws IOException {
		Table t = new ColumnarTable(String.class, "s");
		t.getRows().addRow("3");
		t.getRows().addRow("5");
		t.getRows().addRow("6");
		t.getRows().addRow("9");
		putTable("t", t);
		test(root, "test_selected_types_02", sp, "select (double) s > 5 from t", testTableset, newVarset());
	}
	@Test
	public void testColumnNames00() throws IOException {
		Table t = new ColumnarTable(String.class, "Name With Spaces");
		t.getRows().addRow("x");
		putTable("t", t);
		test(root, "test_column_names_00", sp, "select * from t; create table tAgg as select count(`Name With Spaces`) from t;", testTableset, newVarset());
	}
	@Test
	public void testColumnNames01() throws IOException {
		Table t = new ColumnarTable(String.class, "_Name_With_Weird_Chars_3$@#$%_^%&*()|");
		t.getRows().addRow("x");
		putTable("t", t);
		test(root, "test_column_names_01", sp, "select * from t; create table tAgg as select count(`_Name_With_Weird_Chars_3$@#$%_^%&*()|`) from t;", testTableset, newVarset());
	}
	@Test
	public void testStrTemplate00() throws IOException {
		test(root, "test_str_template_00", sp, "{return \"test${1+2}\";}", newTableset(), newVarset());
	}
	@Test
	public void testStrTemplate01() throws IOException {
		test(root, "test_str_template_01", sp, "{return \"test${1.0 + 2}\";}", newTableset(), newVarset());
	}
	@Test
	public void testStrTemplate02() throws IOException {
		test(root, "test_str_template_02", sp, "{int a = 42; return \"test${a}\";}", newTableset(), newVarset());
	}
	@Test
	public void testStrTemplate03() throws IOException {
		test(root, "test_str_template_03", sp, "{int a = 42; return \"test${a * 2 + 5}\";}", newTableset(), newVarset());
	}
	//@Test
	//public void testBigInteger() throws IOException {
	//	test(root, "test_big_integer", sp, "{BigInteger a = 12345678987654321; BigInteger b = 98765432123456789; BigInteger c = a + b; return c;}", newTableset(), newVarset());
	//}
	//Amiscript functions
	//@Test
	//public void testAbs() throws IOException {
	//	Table t = new ColumnarTable(Integer.class, "d");
	//	putTable("t", t);
	//	test(root, "test_abs", sp, "{Integer i = 5; Long l = 1234; Float f = 3.14; Double d = 3.14159d; i = -5; l = -1234; f = -3.14; d = -3.14159; insert into t values (i)); }",
	//			testTableset, newVarset());
	//}
	//@Test
	//public void testFormatDate() throws IOException {
	//	test(root, "test_format_date", sp, "{long datetime = 1609417292000L; string s = formatDate(datetime, \"dd/MM/YYYT HH:mm:ss\"); return s; }", newTableset(), newVarset());
	//}

	public static Tableset newTableset() {
		return new TablesetImpl();
	}
	public static HashMap<String, Object> newVarset() {
		return new HashMap<String, Object>();
	}

	private static List<String> writtenFiles = new ArrayList<String>();

	@After
	public void teardown() {
		clearTestEnvironment();
		if (!writtenFiles.isEmpty()) {
			String s = writtenFiles.toString();
			writtenFiles.clear();
			throw new RuntimeException("Written files: " + s);
		}
	}

	synchronized protected static void test(File base, String testName, SqlProcessor sp, String string, Tableset ts, Map<String, Object> gv) throws IOException {
		StringBuilder sb = new StringBuilder();
		SqlProcessorTestPlanListenerImpl pl = new SqlProcessorTestPlanListenerImpl();
		sb.append("****TEST_INPUT_QUERY****").append(SH.NEWLINE);
		sb.append(string).append(SH.NEWLINE);
		sb.append("****TEST_INPUT_GLOBAL_VARS****").append(SH.NEWLINE);
		HashMap<String, Object> inputMap = new LinkedHashMap<String, Object>(gv);
		inputMap.remove("tableset");
		sb.append(inputMap).append(SH.NEWLINE);
		sb.append("****TEST_INPUT_TABLES****").append(SH.NEWLINE);
		for (String s : ts.getTableNames()) {
			sb.append(s).append(SH.NEWLINE);
			sb.append(ts.getTable(s)).append(SH.NEWLINE);
		}
		sb.append("****TEST_RESULT****").append(SH.NEWLINE);
		Object result = processSql(sp, string, ts, gv, pl, new DerivedCellTimeoutController(1000000));
		//		if (result instanceof FlowControlReturn) {
		//			result = ((FlowControlReturn) result).getReturnValue();
		if (result instanceof FlowControlThrow) {
			result = ((FlowControlThrow) result).getThrownValue();
			if (result instanceof Throwable)
				result = SH.printStackTrace((Throwable) result);
		}
		sb.append(result).append(SH.NEWLINE);
		String start = pl.getStart();
		String steps = pl.getSteps();
		String end = OH.getSimpleClassName(pl.getEnd());

		StringBuilder plan = new StringBuilder();
		plan.append(TEST_QUERY_PLAN_TOKEN).append(SH.NEWLINE);
		plan.append(QUERY_RECEIVED_TOKEN).append(SH.NEWLINE);
		plan.append(start);
		plan.append("\n\n");
		plan.append(steps);
		plan.append("QUERY_COMPLETED : \n");
		plan.append(end);
		plan.append(SH.NEWLINE);
		plan.append(SH.NEWLINE);
		sb.append(plan).append(SH.NEWLINE);
		sb.append(TEST_RESULT_GLOBAL_VARS_TOKEN).append(SH.NEWLINE);
		HashMap<String, Object> resultMap = new LinkedHashMap<String, Object>(gv);
		resultMap.remove("tableset");
		sb.append(resultMap).append(SH.NEWLINE);
		sb.append("****TEST_RESULT_TABLES****").append(SH.NEWLINE);
		for (String s : ts.getTableNames()) {
			sb.append(s).append(SH.NEWLINE);
			sb.append(ts.getTable(s)).append(SH.NEWLINE);
		}
		String nuw = sb.toString();
		//		nuw = SH.replaceAll(nuw, "!null", "|null");

		Package pck = SqlProcessorTest.class.getPackage();
		String existing;
		try {
			existing = IOH.readText(pck, testName + ".sqltest");
		} catch (Exception e) {
			IOH.ensureDir(base);
			File file = new File(base, testName + ".sqltest");
			IOH.writeText(file, sb.toString());
			System.out.println(testName + " WROTE FILE");
			writtenFiles.add(IOH.getFullPath(file));
			throw new RuntimeException("wrote file " + IOH.getFullPath(file) + " (please copy to " + pck + " package)", e);
		}
		boolean useQueryPlan = true;
		try {
			existing = SH.replaceAll(existing, SH.NEWLINE, "\n");
			nuw = SH.replaceAll(nuw, SH.NEWLINE, "\n");
			if (useQueryPlan) {
				Assert.assertEquals(existing, nuw);
			} else {

				Assert.assertEquals(removeQueryPlan(existing), removeQueryPlan(nuw));
			}
		} catch (AssertionFailedError e) {
			//System.out.println(existing);
			//System.out.println(nuw);
			String x = "File_Does_not_match: " + testName + ".sqltest";
			System.out.println(x);
			e.initCause(new RuntimeException(x));
			throw e;
		}
	}
	private static String removeQueryPlan(String sqltest) {
		return SH.beforeFirst(sqltest, TEST_QUERY_PLAN_TOKEN) + TEST_RESULT_GLOBAL_VARS_TOKEN + SH.afterLast(sqltest, TEST_RESULT_GLOBAL_VARS_TOKEN);
	}
	public static Object processSql(SqlProcessor sp, String string, Tableset ts, SqlPlanListener planListener, DerivedCellTimeoutController timeoutController) {
		return processSql(sp, string, ts, new HashMap<String, Object>(), planListener, timeoutController);
	}
	public static Object processSql(SqlProcessor sp, String string, Tableset ts, Map<String, Object> gv, SqlPlanListener planListener,
			DerivedCellTimeoutController timeoutController) {
		MutableCalcFrame gl = new MutableCalcFrame();
		for (Entry<String, Object> e : gv.entrySet())
			gl.putTypeValue(e.getKey(), e.getValue().getClass(), e.getValue());
		SqlResultset resultSet = new SqlResultset();
		Object o = sp.processSql(string, new TopCalcFrameStack(CalcFrameStack.DEFAULT_STACK_LIMIT, ts, SqlProcessor.NO_LIMIT, timeoutController, planListener, null, gl,
				EmptyCalcFrame.INSTANCE, mf, EmptyCalcFrame.INSTANCE, resultSet));
		Object r;
		if (o instanceof TableReturn) {
			TableReturn tr = (TableReturn) o;
			if (tr.getReturnValue() != null)
				r = tr.getReturnValue();
			else if (!tr.getTables().isEmpty())
				r = tr.getTables().get(0);
			else if (tr.getRowsEffected() > 0)
				r = tr.getRowsEffected();
			else {
				return null;
			}
		} else {
			if (o == null && resultSet.getTables().size() == 1)
				r = resultSet.getTables().get(0);
			else if (o == null && resultSet.getRowsEffected() > 0)
				r = resultSet.getRowsEffected();
			else
				r = o;
		}
		gv.clear();
		for (String i : gl.getVarKeys())
			gv.put(i, gl.getValue(i));
		return r;
	}

}
