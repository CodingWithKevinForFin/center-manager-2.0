package com.f1.suite.web.tree.impl;

import java.util.List;
import java.util.Set;

import com.f1.base.Row;
import com.f1.suite.web.portal.impl.form.FormPortletMultiSelectField;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.CopyPortlet;
import com.f1.utils.SH;

public class CopyableTableImpl implements Copyable {
	private FastWebTable table;

	public CopyableTableImpl(FastWebTable table) {
		this.table = table;
	}

	@Override
	public String getColumnDelimiter() {
		return table.getColumnDelimiter();
	}

	@Override
	public String getRowDelimiter() {
		return table.getRowDelimiter();
	}

	@Override
	public String getHeaderOptions() {
		return table.getHeaderOptions();
	}

	@Override
	public String getInlineDelimiter() {
		return table.getInlineDelimiter();
	}

	@Override
	public String getInlineEnclosed() {
		return table.getInlineEnclosed();
	}

	@Override
	public void setInlineDelimiter(String value) {
		table.setInlineDelimiter(value);
	}

	@Override
	public void setInlineEnclosed(String value) {
		table.setInlineEnclosed(value);
	}

	@Override
	public void setRowDelimiter(String value) {
		table.setRowDelimiter(value);
	}

	@Override
	public void setColumnDelimiter(String value) {
		table.setColumnDelimiter(value);
	}

	@Override
	public void setHeaderOptions(String value) {
		table.setHeaderOptions(value);
	}

	@Override
	public String getTitle() {
		return table.getTable().getTitle();
	}

	@Override
	public int getVisibleColumnsCount() {
		return table.getVisibleColumnsCount();
	}

	@Override
	public String getColumnName(int i) {
		return table.getVisibleColumn(i).getColumnName();
	}

	@Override
	public String getColumnId(int i) {
		return table.getVisibleColumn(i).getColumnId();
	}

	@Override
	public int getHiddenColumnsCount() {
		return table.getHiddenColumnsCount();
	}

	@Override
	public String getHiddenColumnName(int i) {
		return table.getHiddenColumn(i).getColumnName();
	}

	@Override
	public String getHiddenColumnId(int i) {
		return table.getHiddenColumn(i).getColumnId();
	}

	@Override
	public WebColumn getColumn(String columnId) {
		return table.getColumn(columnId);
	}

	@Override
	public void populateVisibleColumnsField(FormPortletMultiSelectField<String> visColumnsField) {
		for (int i = 0; i < table.getVisibleColumnsCount(); i++) {
			String name = table.getVisibleColumn(i).getColumnName();
			visColumnsField.addOption(table.getVisibleColumn(i).getColumnId(), name);
		}
	}

	@Override
	public void populateHiddenColumnsField(FormPortletMultiSelectField<String> hColumnsField) {
		for (int i = 0; i < table.getHiddenColumnsCount(); i++) {
			String name = table.getHiddenColumn(i).getColumnName();
			hColumnsField.addOption(table.getHiddenColumn(i).getColumnId(), name);
		}
	}
	@Override
	public void populateTextArea(CopyPortlet copyPortlet) {
		StringBuilder sb = new StringBuilder();
		List<Row> rowsToCopy = copyPortlet.getIsDownload() ? table.getRows() : table.getSelectedRows();
		if (copyPortlet.getVisibleColumnsField().getValue().size() == 0 && copyPortlet.getHiddenColumnsField().getValue().size() == 0)
			return;
		boolean tooBig = false;
		if (rowsToCopy.size() > 1000) {
			copyPortlet.getMainTitleField().setValue("Selected too many rows...  MUST DOWNLOAD");
			copyPortlet.getDownloadPrepField().setValue("");
			tooBig = true;
		} else {
			copyPortlet.getMainTitleField().setValue("Selected Values");
		}
		if ("hot".equals(copyPortlet.getHeaderOptionsField().getValue()))
			prepareHeader(sb, copyPortlet);
		if ("inline".equals(copyPortlet.getHeaderOptionsField().getValue()))
			prepareInlineText(sb, tooBig, copyPortlet, rowsToCopy);
		else
			prepareStandardText(sb, tooBig, copyPortlet, rowsToCopy);
		copyPortlet.getDownloadPrepField().setValue(sb.toString());
		int length = copyPortlet.getDownloadPrepField().getValue().length();
		copyPortlet.getDownloadPrepField().setSelection(0, length);
	}

	private void prepareStandardText(StringBuilder sb, boolean tooBig, CopyPortlet copyPortlet, List<Row> rowsToCopy) {
		int selHColumnCount = copyPortlet.getHiddenColumnsField().getValue().size();
		String delim = SH.toStringDecode(copyPortlet.getColDelField().getValue());
		String rDelim = SH.toStringDecode(copyPortlet.getRowDelField().getValue());
		Set<String> selectedColumns = copyPortlet.getVisibleColumnsField().selected;
		int numberSelected = selectedColumns.size();
		int hiddenColumnCounter = 0;
		int rowCounter = 0;
		int selectedColumnCnt = 0;

		for (Row row : rowsToCopy) {
			if (tooBig && rowCounter == 3)
				break;
			hiddenColumnCounter = 0;
			selectedColumnCnt = 0;

			for (int i = 0; i < table.getVisibleColumnsCount(); i++) {
				if (selectedColumnCnt == numberSelected)
					break;
				WebColumn col = table.getVisibleColumn(i);
				if (selectedColumns.contains(col.getColumnId())) {
					Object value = getData(col, row);
					sb.append(value);
					selectedColumnCnt++;
					if (!(selectedColumnCnt == numberSelected && selHColumnCount == 0)) {
						sb.append(delim);
					}
				}
			}

			for (String columnId : copyPortlet.getHiddenColumnsField().selected) {
				WebColumn column = table.getColumn(columnId);
				Object value = getData(column, row);
				sb.append(value);
				hiddenColumnCounter += 1;
				if (hiddenColumnCounter != selHColumnCount)
					sb.append(delim);
			}

			rowCounter += 1;
			sb.append(rDelim);
		}
	}

	private void prepareInlineText(StringBuilder sb, boolean tooBig, CopyPortlet copyPortlet, List<Row> rowsToCopy) {
		int hC = 0;
		int vC = 0;
		int r = 0;
		int selHColumnCount = copyPortlet.getHiddenColumnsField().getValue().size();
		int selRowsCount = rowsToCopy.size();
		String delim = SH.toStringDecode(copyPortlet.getColDelField().getValue());
		String rDelim = SH.toStringDecode(copyPortlet.getRowDelField().getValue());
		String iDelim = SH.toStringDecode(copyPortlet.getInlineDelimiterField().getValue());
		String iEnclosed = SH.toStringDecode(copyPortlet.getInlineEnclosedField().getValue());
		char encloseChar = copyPortlet.getInlineEnclosedField().getValue().charAt(0);

		for (Row row : rowsToCopy) {
			if (tooBig && r == 3)
				break;
			hC = 0;
			vC = 0;
			Set<String> selectedColumns = copyPortlet.getVisibleColumnsField().selected;
			int numberSelected = selectedColumns.size();
			int s = 0;
			for (int i = 0; i < table.getVisibleColumnsCount(); i++) {
				if (s == numberSelected)
					continue;
				WebColumn col = table.getVisibleColumn(i);
				if (selectedColumns.contains(col.getColumnId())) {
					String colName = col.getColumnName();
					Object value = getData(col, row);
					sb.append(colName).append(iDelim).append(value);
					s++;
					if (!(s == numberSelected && selHColumnCount == 0)) {
						sb.append(delim);
					}
				}
			}
			for (String columnId : copyPortlet.getHiddenColumnsField().selected) {
				WebColumn column = table.getColumn(columnId);
				Object value = getData(column, row);
				if (value instanceof String) {
					sb.append(column.getColumnName()).append(iDelim).append(iEnclosed);
					SH.escape((String) value, encloseChar, '\\', sb);
					sb.append(iEnclosed);
				} else
					sb.append(column.getColumnName()).append(iDelim).append(value);
				hC += 1;
				if (hC != selHColumnCount)
					sb.append(delim);
			}
			r += 1;
			if (r != selRowsCount)
				sb.append(rDelim);
		}
	}

	private Object getData(WebColumn column, Row row) {
		Object r = column.getData(row);
		if (r instanceof Object[])
			return ((Object[]) r)[0];
		return r;
	}

	private void prepareHeader(StringBuilder sb, CopyPortlet copyPortlet) {
		int hC = 0;
		int selHColumnCount = copyPortlet.getHiddenColumnsField().getValue().size();
		String colDelim = SH.toStringDecode(copyPortlet.getColDelField().getValue());
		Set<String> selectedColumns = copyPortlet.getVisibleColumnsField().selected;
		int numberSelected = selectedColumns.size();
		int s = 0;
		for (int i = 0; i < table.getVisibleColumnsCount(); i++) {
			if (s == numberSelected)
				continue;
			WebColumn col = table.getVisibleColumn(i);
			if (selectedColumns.contains(col.getColumnId())) {
				String colName = col.getColumnName();
				sb.append(colName);
				s++;
				if (!(s == numberSelected && selHColumnCount == 0)) {
					sb.append(colDelim);
				}
			}
		}
		for (String columnId : copyPortlet.getHiddenColumnsField().selected) {
			WebColumn column = table.getColumn(columnId);
			sb.append(column.getColumnName());
			hC += 1;
			if (hC != selHColumnCount)
				sb.append(colDelim);
		}
		sb.append(SH.toStringDecode(copyPortlet.getRowDelField().getValue()));
	}

	@Override
	public void saveTableCopyOptions(CopyPortlet copyPortlet) {
		table.setInlineDelimiter(copyPortlet.getInlineDelimiterField().getValue());
		table.setInlineEnclosed(copyPortlet.getInlineEnclosedField().getValue());
		table.setRowDelimiter(copyPortlet.getRowDelField().getValue());
		table.setColumnDelimiter(copyPortlet.getColDelField().getValue());
		table.setHeaderOptions(copyPortlet.getHeaderOptionsField().getValue());

	}

	@Override
	public byte[] prepareDownload(CopyPortlet copyPortlet) {
		List<Row> rowsToCopy = copyPortlet.getIsDownload() ? table.getRows() : table.getSelectedRows();
		if (copyPortlet.getVisibleColumnsField().getValue().size() == 0 && copyPortlet.getHiddenColumnsField().getValue().size() == 0)
			return null;
		StringBuilder sb = new StringBuilder();
		if ("hot".equals(copyPortlet.getHeaderOptionsField().getValue()))
			prepareHeader(sb, copyPortlet);
		if ("inline".equals(copyPortlet.getHeaderOptionsField().getValue()))
			prepareInlineText(sb, false, copyPortlet, rowsToCopy);
		else
			prepareStandardText(sb, false, copyPortlet, rowsToCopy);
		return sb.toString().getBytes();
	}
}
