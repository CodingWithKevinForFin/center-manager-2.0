package com.f1.utils.converter.bytes;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

import com.f1.utils.structs.table.columnar.ColumnarTable;

public class BytesConverterTest2 {

	public static void main(String a[]) throws IOException {
		ObjectToByteArrayConverter c = new ObjectToByteArrayConverter();
		BytesConverterMyTester m = null;

		HashMap m2 = new HashMap();
		m2.put("bd100", new BigDecimal(100));
		m2.put("bd100.20", new BigDecimal("100.20"));
		m2.put("big!.20", new BigDecimal("123456789012345678901234567890.123456789012345678901234567890"));
		ColumnarTable ct = new ColumnarTable();
		ct.addColumnWithValues(String.class, "fname", new Object[] { "Robert", "Dave", "Steve" }, new long[1], true);
		ct.addColumnWithValues(String.class, "lname", new Object[] { "Cooke", "Smith", "Johnson" }, new long[1], true);
		ct.addColumnWithValues(Integer.class, "age", new int[] { 36, 44, 17 }, new long[1], true);
		ct.addColumnWithValues(Double.class, "grade", new double[] { 4, 1, 32.4 }, new long[1], true);
		ct.set(2, "age", null);
		byte[] data = c.object2Bytes(ct);
		ColumnarTable ct2 = (ColumnarTable) c.bytes2Object(data);

		System.out.println(ct2);

	}
}
