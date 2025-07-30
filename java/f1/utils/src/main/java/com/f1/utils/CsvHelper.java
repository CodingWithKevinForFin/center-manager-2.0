package com.f1.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.structs.table.columnar.ColumnarTable;

public class CsvHelper {
	private static final int[] QUOTE = new int[] { '"', CharReader.EOF };

	public static StringBuilder toCsv(Table table, boolean incNames, StringBuilder sb, Formatter[] columnFormatters) {
		List<Column> columns = table.getColumns();
		int cols = columns.size();
		if (incNames) {
			for (int i = 0; i < cols; i++) {
				if (i > 0)
					sb.append(',');
				escapeCsv(columns.get(i).getId().toString(), sb);
			}
			sb.append("\r\n");
		}
		int columnFormattersSize = columnFormatters == null ? 0 : columnFormatters.length;
		for (int j = 0, s = table.getSize(); j < s; j++) {
			Row row = table.getRows().get(j);
			for (int i = 0; i < cols; i++) {
				if (i > 0)
					sb.append(',');
				Object val = row.getAt(i);
				String str = i >= columnFormattersSize ? OH.toString(val) : columnFormatters[i].format(val);
				escapeCsv(str, sb);
			}
			sb.append("\r\n");
		}
		return sb;
	}

	public static void escapeCsv(String text, StringBuilder sink) {
		if (text == null)
			return;
		if (text.indexOf(',') != -1 || text.indexOf('"') != -1 || text.indexOf('\n') != -1) {
			SH.replaceAll(text, '"', "\"\"", sink.append('"')).append('"');
		} else
			sink.append(text);
	}

	public static Table parseCsv(CharSequence str, boolean firstRowIsHeader) {
		List<String[]> cells = new ArrayList<String[]>();//null indicates linebreak
		StringCharReader sr = new StringCharReader(str);
		int maxColCount = 0;
		StringBuilder tmp = new StringBuilder();
		List<String> rowbuf = new ArrayList<String>();
		outer: for (;;) {
			int c = sr.readCharOrEof();
			switch (c) {
				case CharReader.EOF:
					if (tmp.length() > 0) {
						rowbuf.add(SH.toStringAndClear(tmp));
					}
					if (rowbuf.size() > 0) {
						maxColCount = Math.max(maxColCount, rowbuf.size());
						cells.add(AH.toArray(rowbuf, String.class));
					}
					rowbuf.clear();
					break outer;
				case '\r':
					continue;
				case '\n':
					rowbuf.add(SH.toStringAndClear(tmp));
					maxColCount = Math.max(maxColCount, rowbuf.size());
					cells.add(AH.toArray(rowbuf, String.class));
					rowbuf.clear();
					continue;
				case ',':
					rowbuf.add(SH.toStringAndClear(tmp));
					continue;
				case '"':
					sr.readUntilAny(QUOTE, tmp);
					while (sr.expectNoThrow('"') && sr.expectNoThrow('"')) {
						tmp.append('"');
						sr.readUntilAny(QUOTE, tmp);
					}
					continue;
				default:
					tmp.append((char) c);
			}
		}
		String[] colNames = new String[maxColCount];
		Class[] types = new Class[maxColCount];
		AH.fill(types, String.class);
		if (firstRowIsHeader && cells.size() > 0) {
			Set<String> existingNames = new HashSet<String>();
			String[] fr = cells.get(0);
			for (int i = 0; i < colNames.length; i++) {
				String name = SH.getNextId(AH.getOr(fr, i, "col1"), existingNames, 2);
				existingNames.add(name);
				colNames[i] = name;
			}
		} else {
			for (int i = 0; i < colNames.length; i++)
				colNames[i] = "col" + (i + 1);
		}
		Table r = new ColumnarTable(types, colNames);
		for (int y = firstRowIsHeader ? 1 : 0, s = cells.size(); y < s; y++) {
			String[] row = cells.get(y);
			Row trow = r.newEmptyRow();
			for (int x = 0; x < maxColCount; x++)
				trow.putAt(x, AH.getOr(row, x, null));
			r.getRows().add(trow);
		}
		return r;
	}

}
