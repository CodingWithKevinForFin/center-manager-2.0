package com.f1.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.f1.base.Table;
import com.f1.utils.structs.table.BasicTable;

public class CsvHelperTests {

	@Test
	public void test1() {
		BasicTable t = new BasicTable(new Class[] { String.class, String.class, String.class }, new String[] { "Name", "Age", "dob" });
		t.getRows().addRow("rob", "abc", "this\"test");
		t.getRows().addRow("rob", "43,100", "05-03-1978");
		t.getRows().addRow("rob", "", "05-03-1978");
		test(t, true);
	}

	public static void test(Table t1, boolean incHeader) {
		StringBuilder sb = new StringBuilder();
		CsvHelper.toCsv(t1, incHeader, sb, null);
		Table t2 = CsvHelper.parseCsv(sb, incHeader);
		System.out.println(t1);
		System.out.println(sb);
		System.out.println(t2);
		int size = t1.getSize();
		assertEquals(size, t2.getSize());
		int columnsCount = t1.getColumnsCount();
		assertEquals(columnsCount, t2.getColumnsCount());
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < columnsCount; x++) {
				String v1 = OH.toString(t1.getAt(y, x));
				String v2 = (String) t1.getAt(y, x);
				if (v1 == null)
					assertEquals("", v2);
				else
					assertEquals(v1, v2);
			}
		}

	}
}
