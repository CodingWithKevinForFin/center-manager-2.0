package com.f1.suite.web.tree.impl;

import java.util.List;

import com.f1.suite.web.portal.impl.form.FormPortletMultiSelectField;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.impl.CopyPortlet;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.utils.SH;

public class CopyableTreeImpl implements Copyable {
	private FastWebTree tree;

	public CopyableTreeImpl(FastWebTree tree) {
		this.tree = tree;
	}
	@Override
	public String getColumnDelimiter() {
		return tree.getColumnDelimiter();
	}

	@Override
	public String getRowDelimiter() {
		return tree.getRowDelimiter();
	}

	@Override
	public String getHeaderOptions() {
		return tree.getHeaderOptions();
	}

	@Override
	public String getInlineDelimiter() {
		return tree.getInlineDelimiter();
	}

	@Override
	public String getInlineEnclosed() {
		return tree.getInlineEnclosed();
	}

	@Override
	public void setInlineDelimiter(String value) {
		tree.setInlineDelimiter(value);
	}

	@Override
	public void setInlineEnclosed(String value) {
		tree.setInlineEnclosed(value);
	}

	@Override
	public void setRowDelimiter(String value) {
		tree.setRowDelimiter(value);
	}

	@Override
	public void setColumnDelimiter(String value) {
		tree.setColumnDelimiter(value);
	}

	@Override
	public void setHeaderOptions(String value) {
		tree.setHeaderOptions(value);
	}

	@Override
	public String getTitle() {
		return "tree";
	}

	@Override
	public int getVisibleColumnsCount() {
		return tree.getVisibleColumnsCount();
	}

	@Override
	public String getColumnName(int i) {
		return tree.getVisibleColumn(i).getColumnName();
	}

	@Override
	public String getColumnId(int i) {
		return tree.getVisibleColumn(i).getColumnId().toString();
	}

	@Override
	public int getHiddenColumnsCount() {
		return tree.getHiddenColumnsCount();
	}

	@Override
	public String getHiddenColumnName(int i) {
		return tree.getHiddenColumn(i).getColumnName();
	}

	@Override
	public String getHiddenColumnId(int i) {
		return tree.getHiddenColumn(i).getColumnId().toString();
	}

	@Override
	public WebColumn getColumn(String columnId) {
		return null;
	}

	@Override
	public void populateVisibleColumnsField(FormPortletMultiSelectField<String> visColumnsField) {
		for (int i = 0; i <= tree.getVisibleColumnsCount(); i++) {
			FastWebTreeColumn col = tree.getVisibleColumn(i);
			visColumnsField.addOption(SH.toString(i), col.getColumnName());
		}
	}

	@Override
	public void populateHiddenColumnsField(FormPortletMultiSelectField<String> hColumnsField) {
		for (int i = 0; i < tree.getHiddenColumnsCount(); i++) {
			FastWebTreeColumn col = tree.getHiddenColumn(i);
			hColumnsField.addOption(SH.toString(i), col.getColumnName());
		}
	}

	@Override
	public void populateTextArea(CopyPortlet copyPortlet) {
		FormPortletMultiSelectField<String> visColumns = copyPortlet.getVisibleColumnsField();
		FormPortletMultiSelectField<String> hiddenColumns = copyPortlet.getHiddenColumnsField();
		if (visColumns.getValue().size() == 0 && hiddenColumns.getValue().size() == 0) {
			copyPortlet.getDownloadPrepField().setValue("");
			return;
		}
		List<WebTreeNode> rowsToCopy = copyPortlet.getIsDownload() ? tree.getVisibleNodes() : tree.getTreeManager().getSelectedNodes();
		StringBuilder sb = new StringBuilder();
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
	private void prepareInlineText(StringBuilder sb, boolean tooBig, CopyPortlet copyPortlet, List<WebTreeNode> rowsToCopy) {
		FormPortletMultiSelectField<String> visColumns = copyPortlet.getVisibleColumnsField();
		FormPortletMultiSelectField<String> hiddenColumns = copyPortlet.getHiddenColumnsField();
		String inlineDelimiter = copyPortlet.getInlineDelimiterField().getValue();
		String enclosedBy = copyPortlet.getInlineEnclosedField().getValue();
		int i;
		int j = 0;
		String delim = SH.toStringDecode(copyPortlet.getColDelField().getValue());
		String rDelim = SH.toStringDecode(copyPortlet.getRowDelField().getValue());
		String iDelim = SH.toStringDecode(copyPortlet.getInlineDelimiterField().getValue());
		String iEnclosed = SH.toStringDecode(copyPortlet.getInlineEnclosedField().getValue());
		String encloseChar = copyPortlet.getInlineEnclosedField().getValue();
		for (WebTreeNode node : rowsToCopy) {
			i = 1;
			if (tooBig && j == 3)
				break;
			for (String id : visColumns.getValue()) {
				FastWebTreeColumn col = tree.getVisibleColumn(Integer.parseInt(id));
				sb.append(col.getColumnName() + inlineDelimiter);
				col.getFormatter().formatToText(node, sb);
				if (!(i == visColumns.getValue().size() && hiddenColumns.getValue().size() == 0))
					sb.append(delim);
				i++;
			}
			i = 1;
			for (String id : hiddenColumns.getValue()) {
				FastWebTreeColumn col = tree.getHiddenColumn(Integer.parseInt(id));
				sb.append(col.getColumnName() + inlineDelimiter);
				col.getFormatter().formatToText(node, sb);
				if (!(i == hiddenColumns.getValue().size()))
					sb.append(delim);
				i++;
			}
			j++;
			sb.append(rDelim);
		}

	}
	private void prepareStandardText(StringBuilder sb, boolean tooBig, CopyPortlet copyPortlet, List<WebTreeNode> rowsToCopy) {
		FormPortletMultiSelectField<String> visColumns = copyPortlet.getVisibleColumnsField();
		FormPortletMultiSelectField<String> hiddenColumns = copyPortlet.getHiddenColumnsField();
		int i;
		int j = 0;
		String delim = SH.toStringDecode(copyPortlet.getColDelField().getValue());
		String rDelim = SH.toStringDecode(copyPortlet.getRowDelField().getValue());
		for (WebTreeNode node : rowsToCopy) {
			i = 1;
			if (tooBig && j == 3)
				break;
			for (String id : visColumns.getValue()) {
				FastWebTreeColumn col = tree.getVisibleColumn(Integer.parseInt(id));
				col.getFormatter().formatToText(node, sb);
				if (!(i == visColumns.getValue().size() && hiddenColumns.getValue().size() == 0))
					sb.append(delim);
				i++;
			}
			i = 1;
			for (String id : hiddenColumns.getValue()) {
				FastWebTreeColumn col = tree.getHiddenColumn(Integer.parseInt(id));
				col.getFormatter().formatToText(node, sb);
				if (!(i == hiddenColumns.getValue().size()))
					sb.append(delim);
				i++;
			}
			j++;
			sb.append(rDelim);
		}

	}
	private void prepareHeader(StringBuilder sb, CopyPortlet copyPortlet) {
		int i = 1;
		String delim = SH.toStringDecode(copyPortlet.getColDelField().getValue());
		String rDelim = SH.toStringDecode(copyPortlet.getRowDelField().getValue());
		for (String id : copyPortlet.getVisibleColumnsField().getValue()) {
			FastWebTreeColumn col = tree.getVisibleColumn(Integer.parseInt(id));
			sb.append(col.getColumnName());
			if (!(i == copyPortlet.getVisibleColumnsField().getValue().size() && copyPortlet.getHiddenColumnsField().getValue().size() == 0))
				sb.append(delim);
			i++;
		}
		sb.append(rDelim);
	}
	@Override
	public void saveTableCopyOptions(CopyPortlet copyPortlet) {
		tree.setInlineDelimiter(copyPortlet.getInlineDelimiterField().getValue());
		tree.setInlineEnclosed(copyPortlet.getInlineEnclosedField().getValue());
		tree.setColumnDelimiter(copyPortlet.getColDelField().getValue());
		tree.setRowDelimiter(copyPortlet.getRowDelField().getValue());
		tree.setHeaderOptions(copyPortlet.getHeaderOptionsField().getValue());
	}

	@Override
	public byte[] prepareDownload(CopyPortlet copyPortlet) {
		List<WebTreeNode> rowsToCopy = copyPortlet.getIsDownload() ? tree.getVisibleNodes() : tree.getTreeManager().getSelectedNodes();
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
