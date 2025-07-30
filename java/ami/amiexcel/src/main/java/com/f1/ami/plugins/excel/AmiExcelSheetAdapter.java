package com.f1.ami.plugins.excel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterQuery;
import com.f1.ami.amicommon.msg.AmiDatasourceColumn;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Table;
import com.f1.base.Valued;
import com.f1.container.ContainerTools;
import com.f1.utils.SH;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.sql.Tableset;
import com.f1.utils.sql.TablesetImpl;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

public class AmiExcelSheetAdapter {

	private ContainerTools tools;
	private final Sheet sheet;
	private File rootFile;
	private String relativeFile;
	private StringBuilder sb;

	public AmiExcelSheetAdapter(ContainerTools tools, File rootFile, String relativeFile, Sheet sheet) {
		this.rootFile = rootFile;
		this.relativeFile = relativeFile;
		this.sheet = sheet;
		this.tools = tools;
		this.sb = new StringBuilder();
	}

	public String getName() {
		return this.rootFile.getName() + ":" + sheet.getSheetName();
	}
	public String getPath() {
		return this.rootFile.getPath() + ":" + sheet.getSheetName();
	}
	public String getRelativePath() {
		String sheetName = sheet.getSheetName();
		sb.append(this.relativeFile).append(':').append(SH.escapeAny(sheetName, " ;\\/".toCharArray(), '\\'));
		String relPath = sb.toString();
		SH.clear(sb);
		return relPath;
	}
	@Deprecated
	public AmiDatasourceTable getSchema(Set<String> names) throws AmiDatasourceException {
		AmiDatasourceTable table = getTableSchema();
		prepareTableSchema(table, names);
		Map<String, String> options = new HashMap<String, String>();
		String customQuery = getCustomQuery(table, options);
		table.setCustomQuery(customQuery);
		return table;
	}

	public void getSchema(AmiDatasourceTable table, Set<String> names) throws AmiDatasourceException {
		table.setColumns(getColumnSchema());
		prepareTableSchema(table, names);
		Map<String, String> options = new HashMap<String, String>();
		String customQuery = getCustomQuery(table, options);
		table.setCustomQuery(customQuery);
	}

	public Table processQuery(Map<String, Table> request, int requestIndex, int limit, AmiDatasourceTracker debugSink, TimeoutController timeout) throws AmiDatasourceException {
		//Create the query to be used by processor
		Table queryTable = request.get("QUERY");
		Table whereTable = request.get("WHERES");

		String name = (String) "table" + queryTable.get(requestIndex, "name");

		// Get the columns to select upon
		String query = (String) queryTable.get(requestIndex, "select");
		String selectPart = SH.beforeFirst(query, " FROM ");
		selectPart = SH.afterFirst(selectPart, "SELECT ");
		selectPart = SH.trim(selectPart);

		String wherePart = SH.afterFirst(query, " WHERE ");

		String wheresVar;
		if (whereTable.getRows().size() == 0)
			wheresVar = "";
		else
			wheresVar = (String) whereTable.get(0, "wheres");
		wherePart = SH.replaceAll(wherePart, "${WHERE}", wheresVar);

		StringBuilder newQuery = new StringBuilder();
		newQuery.append("SELECT ");
		newQuery.append(selectPart);
		newQuery.append(" FROM ");
		newQuery.append(name);
		if (!SH.equals(wherePart, "")) {
			newQuery.append(" WHERE ");
			newQuery.append(wherePart);
		}
		//		if (limit != 0) {
		//			newQuery.append(" LIMIT ");
		//			newQuery.append(limit);
		//		}

		Table table = readSheet();
		SqlProcessor processor = new SqlProcessor();
		Tableset tableset = new TablesetImpl();
		tableset.putTable(name, table);
		Table table2 = processor.process(newQuery.toString(), new TopCalcFrameStack(tableset, limit, timeout, AmiUtils.METHOD_FACTORY, EmptyCalcFrame.INSTANCE));
		return table2;
	}

	public Table processQuery(String tname, AmiCenterQuery amiQuery, AmiDatasourceTracker debugSink, TimeoutController timeout) throws AmiDatasourceException {
		//Create the query to be used by processor

		int limit = amiQuery.getLimit();
		// Get the columns to select upon
		String query = amiQuery.getQuery();
		String selectPart = SH.beforeFirst(query, " FROM ");
		selectPart = SH.afterFirst(selectPart, "SELECT ");
		selectPart = SH.trim(selectPart);

		String wherePart = SH.afterFirst(query, " WHERE ");

		Set<String> names = new HashSet<String>();
		String validName = SH.getNextId(AmiUtils.toValidVarName(tname), names);

		StringBuilder newQuery = new StringBuilder();
		newQuery.append("SELECT ");
		newQuery.append(selectPart);
		newQuery.append(" FROM ");
		newQuery.append(validName);
		if (!SH.equals(wherePart, "")) {
			newQuery.append(" WHERE ");
			newQuery.append(wherePart);
		}

		Table table = readSheet();
		SqlProcessor processor = new SqlProcessor();
		Tableset tableset = new TablesetImpl();
		tableset.putTable(validName, table);
		Table table2 = processor.process(newQuery.toString(), new TopCalcFrameStack(tableset, limit, timeout, AmiUtils.METHOD_FACTORY, EmptyCalcFrame.INSTANCE));
		return table2;
	}
	protected <T extends Valued> Table readSheet() throws AmiDatasourceException {
		Table result = new BasicTable();

		// Initialize Columns
		// Get range of columns in the sheet
		int firstCol = getFirstColNum(sheet);
		int colCount = getColCount(sheet);

		// Add appropriate columns to the result table
		if (colCount == -1)
			return result;

		final Map<String, ExcelColumn> excelCols = getRawColumnSchema();
		Set<String> names = new HashSet<String>();
		for (ExcelColumn col : excelCols.values()) {
			String name = SH.getNextId(AmiUtils.toValidVarName(col.getName()), names);
			result.addColumn(col.convertCellTypeToJavaType(col.getType()), name);

		}

		int first = getFirstRowNum(sheet);
		int count = getRowCount(sheet);
		int length = first + count;
		first = hasHeader(sheet) ? first + 1 : first;

		for (int i = first; i < length; i++) {
			ArrayList<Object> values = new ArrayList<Object>();
			Row r = sheet.getRow(i);
			if (r == null) {
				continue;
			}
			for (int j = firstCol; j < (firstCol + colCount); j++) {
				Cell c = r.getCell(j);
				if (c == null) {
					values.add(null);
					continue;
				}

				switch (c.getCellType()) {
					case BLANK:
						values.add(null);
						break;
					case BOOLEAN:
						values.add(c.getBooleanCellValue());
						break;
					case ERROR:
						values.add(c.getErrorCellValue());
						break;
					case FORMULA:
						double d;
						try {
							d = c.getNumericCellValue();
							values.add(d);
						} catch (IllegalStateException e) {
							values.add(c.getCellFormula());
						}
						break;
					case NUMERIC:
						values.add(c.getNumericCellValue());
						break;
					case STRING:
						values.add(c.getStringCellValue());
						break;
					default:
						values.add(null);
						break;
				}
			}
			result.getRows().addRow(values.toArray());
		}
		result.setTitle(getName());
		return result;
	}
	private void prepareTableSchema(AmiDatasourceTable table, Set<String> names) {
		//		String name = SH.getNextId(AmiUtils.toValidVarName(table.getName()), names);
		//		table.setName(name);
		Set<String> colNames = new HashSet<String>();
		prepareColumnSchema(table.getColumns(), colNames);
	}

	private void prepareColumnSchema(List<AmiDatasourceColumn> cols, Set<String> names) {
		for (int i = 0; i < cols.size(); i++) {
			AmiDatasourceColumn c = cols.get(i);
			String cname = SH.getNextId(AmiUtils.toValidVarName(c.getName()), names);
			c.setName(cname);
			names.add(cname);
		}
	}

	private AmiDatasourceTable getTableSchema() {
		AmiDatasourceTable table = tools.nw(AmiDatasourceTable.class);
		table.setName(getName());
		table.setColumns(getColumnSchema());
		return table;
	}

	private List<AmiDatasourceColumn> getColumnSchema() {
		Map<String, ExcelColumn> cols = this.getRawColumnSchema();
		List<AmiDatasourceColumn> sink = new ArrayList<AmiDatasourceColumn>(cols.size());

		for (ExcelColumn col : cols.values()) {
			AmiDatasourceColumn amiCol = tools.nw(AmiDatasourceColumn.class);
			amiCol.setName(col.getName());

			byte t = col.convertCellTypeToAmiDatasourceColumnType(col.getType());
			amiCol.setType(t);
			sink.add(amiCol);
		}
		return sink;
	}
	private Map<String, ExcelColumn> getRawColumnSchema() {
		Map<String, ExcelColumn> cols = new LinkedHashMap<String, ExcelColumn>();
		Row r = sheet.getRow(getFirstRowNum(sheet));

		if (r == null)
			return cols;

		int first = getFirstCellNum(r);
		int last = getLastCellNum(r);

		if (hasHeader(sheet)) {
			Row r2 = sheet.getRow(getFirstRowNum(sheet) + 1);
			for (int i = first; i < (last + 1); i++) {
				Cell c = r.getCell(i);
				if (c == null)
					continue;
				Cell cr2 = r2.getCell(i);
				//				int cellType;
				CellType cellType;
				if (cr2 == null)
					cellType = CellType.BLANK;
				else
					cellType = cr2.getCellType();
				ExcelColumn excelCol = new ExcelColumn(c.getStringCellValue(), cellType, i, i - first);
				cols.put(excelCol.getName(), excelCol);
			}
		} else {
			for (int i = first; i < (last + 1); i++) {
				Cell c = r.getCell(i);
				if (c == null)
					continue;

				ExcelColumn excelCol = new ExcelColumn("col" + i, c.getCellType(), i, i - first);
				cols.put(excelCol.getName(), excelCol);
			}
		}
		return cols;
	}
	private boolean hasHeader(Sheet sheet) {
		boolean check = true;
		int first = getFirstRowNum(sheet);
		int count = getRowCount(sheet);

		if (count <= 1) {
			check = false;
		}

		if (count >= 1 && check) {
			// TODO: if row == null;
			Row r = sheet.getRow(first);
			int firstCell = getFirstCellNum(r);
			int cellCount = getCellCount(r);

			for (int i = firstCell; i < (firstCell + cellCount); i++) {
				Cell c = r.getCell(i);
				if (c == null) {
					check = false;
					break;
				}
				if (c.getCellType() == CellType.BLANK) {
					continue;
				}
				if (r.getCell(i).getCellType() != CellType.STRING) {
					check = false;
					break;
				}
			}
		}
		if (count > 1 && check) {
			// TODO First has different type than second 
			// First has different color than second

		}
		return check;
	}
	private String getCustomQuery(AmiDatasourceTable table, Map<String, String> options) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		List<AmiDatasourceColumn> columns = table.getColumns();
		int size = columns.size();
		if (size == 0) {
			sb.append("*");
		} else {
			for (int i = 0; i < columns.size(); i++) {
				AmiDatasourceColumn column = columns.get(i);
				sb.append("(");
				sb.append(AmiUtils.toTypeName(column.getType()));
				sb.append(")");
				sb.append(column.getName());
				if (i != size - 1)
					sb.append(",");
			}
		}
		sb.append(" FROM ");
		sb.append(getRelativePath());
		sb.append(" ");
		sb.append("WHERE ${WHERE}");
		return sb.toString();
	}
	private int getFirstColNum(Sheet s) {
		int first = getFirstRowNum(s);
		int count = getRowCount(s);
		int firstCol = -1;
		for (int i = first; i < (first + count); i++) {
			Row r = s.getRow(i);
			if (r == null)
				continue;
			int fc = r.getFirstCellNum();
			firstCol = firstCol == -1 ? fc : (fc < firstCol && fc > -1) ? fc : firstCol;
		}
		return firstCol;
	}
	private int getColCount(Sheet s) {
		int first = getFirstRowNum(s);
		int count = getRowCount(s);
		int colCount = -1;
		for (int i = first; i < (first + count); i++) {
			Row r = s.getRow(i);
			if (r == null)
				continue;
			int cc = r.getLastCellNum() - r.getFirstCellNum();
			colCount = colCount == -1 ? cc : cc > colCount ? cc : colCount;
		}
		return colCount;
	}
	private int getFirstCellNum(Row r) {
		return r.getFirstCellNum();
	}
	private int getLastCellNum(Row r) {
		return r.getLastCellNum() - 1;
	}
	private int getCellCount(Row r) {
		return r.getLastCellNum() - r.getFirstCellNum();
	}
	private int getFirstRowNum(Sheet s) {
		return s.getFirstRowNum();
	}
	private int getRowCount(Sheet s) {
		return s.getLastRowNum() - s.getFirstRowNum() + 1;
	}

	public class ExcelColumn {
		private String name;
		private CellType type;
		private int sheetPosition;
		private int schemaIndex;
		private String columnName;

		public ExcelColumn(String name, CellType type, int i, int schemaIndex) {
			this.name = name;
			this.type = type;
			this.sheetPosition = i;
			this.schemaIndex = schemaIndex;
			this.columnName = toCol(i);
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
		public String getColumnName() {
			return columnName;
		}
		public CellType getType() {
			return type;
		}

		public int getPosition() {
			return sheetPosition;
		}

		public int getSchemaIndex() {
			return schemaIndex;
		}

		public byte convertCellTypeToAmiDatasourceColumnType(CellType i) {
			byte type = AmiDatasourceColumn.TYPE_UNKNOWN;
			switch (i) {
				case BLANK:
					type = AmiDatasourceColumn.TYPE_STRING;
					break;
				case BOOLEAN:
					type = AmiDatasourceColumn.TYPE_BOOLEAN;
					break;
				case FORMULA:
					type = AmiDatasourceColumn.TYPE_STRING;
					break;
				case NUMERIC:
					// TODO: needs more logic numbers can be doubles longs ints floats
					type = AmiDatasourceColumn.TYPE_DOUBLE;
					break;
				case STRING:
					type = AmiDatasourceColumn.TYPE_STRING;
					break;
				case ERROR:
					break;
				default:
					break;
			}

			return type;
		}

		public Class<?> convertCellTypeToJavaType(CellType i) {
			switch (i) {
				case BLANK:
				case FORMULA:
				case STRING:
					return String.class;
				case BOOLEAN:
					return Boolean.class;
				case NUMERIC:
					return Double.class;
				case ERROR:
					break;
				default:
					break;
			}
			return Object.class;
		}
	}

	StringBuilder tmp = new StringBuilder();

	/*
	 * Converts x, y to excel cell address
	 */
	private String toCol(int x) {
		tmp.setLength(0);
		toCol(x, tmp);
		return tmp.toString();
	}
	private void toCol(int x, StringBuilder sink) {
		if (x >= 26) {
			if (x >= 702)
				appendLetter((x / 676) - 1, sink);
			appendLetter(((x / 26) - 1), sink);
		}
		appendLetter(x, sink);
		sink.append("_");
	}

	private void appendLetter(int x, StringBuilder sink) {
		sink.append((char) ('A' + (x % 26)));
	}

}
