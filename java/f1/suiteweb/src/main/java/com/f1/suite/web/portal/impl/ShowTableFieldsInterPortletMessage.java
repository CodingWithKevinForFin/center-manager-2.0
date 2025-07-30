package com.f1.suite.web.portal.impl;

import java.util.List;

import com.f1.suite.web.portal.InterPortletMessage;

public class ShowTableFieldsInterPortletMessage implements InterPortletMessage {

	public final List<TableRow> rows;

	public ShowTableFieldsInterPortletMessage(List<TableRow> rows) {
		this.rows = rows;
	}

	public static class TableRow {
		final public String name;
		final public List<TableRowField> fields;

		public TableRow(String name, List<TableRowField> fields) {
			this.name = name;
			this.fields = fields;
		}

	}

	public static class TableRowField {
		public final String name;
		public final String value;
		public final boolean visible;

		public TableRowField(String name, String value, boolean visible) {
			this.name = name;
			this.value = value;
			this.visible = visible;
		}
	}

}
