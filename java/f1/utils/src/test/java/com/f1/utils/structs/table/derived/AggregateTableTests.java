package com.f1.utils.structs.table.derived;

import com.f1.base.Row;
import com.f1.base.TableList;
import com.f1.utils.SH;
import com.f1.utils.string.JavaExpressionParser;
import com.f1.utils.structs.table.stack.EmptyCalcFrameStack;

public class AggregateTableTests {

	public static void main(String a[]) {
		try {
			BasicDerivedCellParser parser = new BasicDerivedCellParser(new JavaExpressionParser());
			DerivedTable t = new DerivedTable(EmptyCalcFrameStack.INSTANCE);
			t.addColumn(String.class, "symbol");
			t.addColumn(Integer.class, "target");
			t.addColumn(Integer.class, "execQty");
			t.addColumn(Double.class, "value");

			TableList rows = t.getRows();
			Row row1 = rows.addRow("msft", 1000, 500, 50000);
			Row row2 = rows.addRow("msft", 2500, 1500, 10000);
			MethodFactoryManager mf = null;
			t.addDerivedColumn(parser, "remaining", "execQty - target", mf);
			t.addDerivedColumn(parser, "avgPx", "value / execQty", mf);
			t.addDerivedColumn(parser, "avgPxPennies", "avgPx * 100", mf);
			t.addDerivedColumn(parser, "status", "execQty >= target ? \"FILLED\" : \"OPEN\"", mf);
			t.addColumn(String.class, "name");
			Row row3 = rows.addRow("ibm", 2500, 1500, 10000, "some name");

			System.out.println(t);
			row2.put("execQty", 2000);
			row2.put("value", 15000d);
			System.out.println(t);
			t.addDerivedColumn(parser, "calc", "avgPx * -12", mf);
			row2.put("target", 2000);
			System.out.println(t);
			AggregateTable at = new AggregateTable(t, "symbol");
			at.addSumColumn("sum", "target");
			at.addCountColumn("cnt", "target");
			at.addMaxColumn("max", "target");
			at.addMinColumn("min", "target");
			at.addMinColumn("minAvgPx", "avgPx");
			at.addDerivedColumn(parser, "minPlusMax", "min + max", mf);
			at.addDerivedColumn(parser, "minTimeAvgPx", "min * minAvgPx", mf);
			System.out.println(at);
			Row row4 = rows.addRow("msft", 2500, 1500, 10000, "asdf");
			System.out.println(at);
			row2.put("target", 5000);
			System.out.println(at);
			row4.put("target", 5000);
			System.out.println("##############");
			System.out.println(at);
			System.out.println(t);
			t.getRows().remove(0);
			System.out.println("##############");
			System.out.println(at);
			System.out.println(t);
			t.getRows().remove(1);
			Row row5 = rows.addRow("msft", 250, 150, 10000, "asdf");
			System.out.println("##############");
			System.out.println(at);
			System.out.println(t);
			System.out.println("##############");
			row5.put("value", 500d);
			System.out.println(at);
			System.out.println(t);
		} catch (Exception e) {
			System.err.println(SH.printStackTrace(e));
		}
	}

}
