package com.f1.utils.structs.table.derived;

import com.f1.base.Row;
import com.f1.base.TableList;
import com.f1.utils.SH;
import com.f1.utils.string.JavaExpressionParser;
import com.f1.utils.structs.table.stack.EmptyCalcFrameStack;

public class DerivedTableTests {

	public static void main(String a[]) {
		try {
			BasicDerivedCellParser parser = new BasicDerivedCellParser(new JavaExpressionParser());
			DerivedTable t = new DerivedTable(EmptyCalcFrameStack.INSTANCE);
			t.addColumn(Integer.class, "target");
			t.addColumn(Integer.class, "execQty");
			t.addColumn(Double.class, "value");

			TableList rows = t.getRows();
			Row row1 = rows.addRow(1000, 500, 50000);
			Row row2 = rows.addRow(2500, 1500, 10000);
			t.addDerivedColumn(parser, "remaining", "execQty - target", null);
			t.addDerivedColumn(parser, "avgPx", "value / execQty", null);
			t.addDerivedColumn(parser, "avgPxPennies", "avgPx * 100", null);
			t.addDerivedColumn(parser, "status", "execQty >= target ? \"FILLED\" : \"OPEN\"", null);
			t.addDerivedColumn(parser, "status2", "!(execQty >= target) ? \"FILLED\" : \"OPEN\"", null);
			t.addColumn(String.class, "name");
			Row row3 = rows.addRow(2500, 1500, 10000, "some name");

			System.out.println(t);
			row2.put("execQty", 2000);
			row2.put("value", 15000d);
			System.out.println(t);
			t.addDerivedColumn(parser, "calc", "avgPx * -12", null);
			row2.put("target", 2000);
			System.out.println(t);
			row2.put("target", 2000);
			System.out.println(t);
		} catch (Exception e) {
			System.err.println(SH.printStackTrace(e));
		}
	}

}
