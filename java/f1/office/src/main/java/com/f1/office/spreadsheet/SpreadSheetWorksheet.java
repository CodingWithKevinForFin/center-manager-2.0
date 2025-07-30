package com.f1.office.spreadsheet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.Column;
import com.f1.base.Table;
import com.f1.utils.AH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.XlsxHelper;
import com.f1.utils.structs.table.stack.BasicCalcTypes;

public class SpreadSheetWorksheet extends SpreadSheetWorksheetBase {

	private static final int DEFAULT_COL_WIDTH = 60;
	public static final int MAX_ROW_COUNT = 1048576;
	private int tableId;
	private String tableRid;
	private Table table;
	private List<Integer> columnWidths;
	private SpreadSheetResource_CellXfs headerStyle;
	//	final private SpreadSheetWorkbook workbook;
	private List<SpreadSheetResource_CellXfs> cellStyles;
	private int width;
	private int height;

	private BasicCalcTypes underlyingTypes;
	private Map<Integer, String> formulas;
	public Map<Integer, String> parsedFormulas;
	private int pinnedColumnsCount;

	public SpreadSheetWorksheet(SpreadSheetWorkbook workbook, int tableId, int sheetId, String name, Table table, final BasicCalcTypes underlyingTypes) {
		super(workbook, sheetId, name);
		OH.assertLe(table.getSize(), MAX_ROW_COUNT, "Max table size in limits ");
		this.columnWidths = new ArrayList<Integer>(table.getColumnsCount());
		AH.fill(columnWidths, -1);

		this.tableId = tableId;
		this.tableRid = "rId" + tableId;
		this.table = table;
		this.width = this.table.getColumnsCount();
		this.height = this.table.getSize();
		this.cellStyles = new ArrayList<SpreadSheetResource_CellXfs>(this.width * this.height);
		for (int i = 0; i < this.width * this.height; ++i)
			cellStyles.add(null);
		//		this.workbook = workbook;
		this.underlyingTypes = underlyingTypes;
		this.formulas = new HashMap<Integer, String>();
		this.parsedFormulas = new HashMap<Integer, String>();
	}
	public SpreadSheetResource_CellXfs getStyle(int x, int y) {
		OH.assertBetweenExcluding(x, 0, this.width);
		OH.assertBetweenExcluding(y, 0, this.height);
		return this.cellStyles.get(x + y * this.width);
	}
	public void setStyle(int x, int y, SpreadSheetResource_CellXfs style) {
		OH.assertBetweenExcluding(x, 0, this.width);
		OH.assertBetweenExcluding(y, 0, this.height);
		this.cellStyles.set(x + y * this.width, style);
	}
	public String getTableFileName() {
		return "tables/table" + tableId + ".xml";
	}
	public String getTableRid() {
		return tableRid;
	}
	public String getTitle() {
		return name;
	}
	public String getTableId() {
		return SH.toString(tableId);
	}
	public Table getTable() {
		return this.table;
	}
	public void setColumnWidthPx(int position, int width) {
		this.columnWidths.add(position, width);
	}
	public int getColumnWidthPx(int position) {
		return this.columnWidths.get(position);
	}
	public void setHeaderStyle(SpreadSheetResource_CellXfs style) {
		this.headerStyle = style;
	}
	public SpreadSheetResource_CellXfs getHeaderStyle() {
		return this.headerStyle;
	}

	public int getPinnedColumnsCount() {
		return pinnedColumnsCount;
	}

	//Call to generate row based formulas
	public void parseFormulas() {
		parsedFormulas.clear();
		Set<Integer> keys = formulas.keySet();
		List<Column> cols = table.getColumns();
		for (int key : keys) {
			String f = formulas.get(key);

			for (final Column c : cols) {
				final String colName = "`" + c.getId().toString() + "`";
				if (SH.indexOf(f, colName, 0) != -1) {
					String newPos = XlsxHelper.getExcelPosition(c.getLocation());
					f = SH.replaceAll(f, colName, newPos + "#");
				}
			}
			parsedFormulas.put(key, f);
		}
	}

	public void setPinnedColumnsCount(int pinnedColumnsCount) {
		OH.assertBetweenExcluding(pinnedColumnsCount, 0, this.width);
		this.pinnedColumnsCount = pinnedColumnsCount;
	}

	public void addColumn(int position, String colName, String formula) {
		table.addColumn(position, Integer.class, colName, 0);
		List<Integer> keys = new ArrayList<Integer>(formulas.keySet());
		Collections.sort(keys);
		for (int i = keys.size() - 1; i >= 0; --i) {
			int key = keys.get(i);
			if (key >= position) {
				formulas.put(key + 1, formulas.get(key));
				formulas.remove(key);
			} else
				break;
		}
		this.width += 1;
		for (int i = 0; i < height; ++i) {
			this.cellStyles.add(position + i * this.width, null);
		}

		formulas.put(position, formula);
		columnWidths.add(position, DEFAULT_COL_WIDTH);
		underlyingTypes.putType(colName, Integer.class);
	}
}
