package com.f1.ami.web.style;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.AmiWebDesktopPortlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletColorField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.structs.BasicIndexedList;

public class AmiWebStyleVarPortlet extends GridPortlet implements FormPortletListener {

	final private FormPortlet form;
	private AmiWebStyle style;
	final private FormPortletButtonField addButton;
	final private FormPortletButtonField arrangeButton;
	final private List<Row> rows = new ArrayList<Row>();

	private class Row {
		FormPortletTextField nameField = new FormPortletTextField("");
		FormPortletColorField colorField = new FormPortletColorField("");
		FormPortletButtonField deleteField = new FormPortletButtonField("").setValue("Delete");
		private int pos;

		public Row(int pos, String name, String color) {
			nameField.setLeftTopWidthHeightPx(25, 0, 165, 20);
			colorField.setLeftTopWidthHeightPx(200, 0, 110, 20);
			colorField.setAlphaEnabled(true);
			deleteField.setLeftTopWidthHeightPx(320, 0, 100, 20);
			colorField.setAllowNull(false);
			nameField.setValue(SH.stripPrefix(name, "$", false));
			nameField.setTitle("$");
			colorField.setValue(color);
			colorField.setTitle("= ");
			deleteField.setCorrelationData(this);
			colorField.setCorrelationData(this);
			nameField.setCorrelationData(this);
			nameField.setDisabled(style.getReadOnly());
			colorField.setDisabled(style.getReadOnly());
			deleteField.setDisabled(style.getReadOnly());
			form.addField(this.nameField);
			form.addField(this.colorField);
			form.addField(this.deleteField);
			setPos(pos);
		}

		public void setPos(int pos) {
			this.pos = pos;
			int n = 50 + pos * 25;
			nameField.setTopPosPx(n);
			colorField.setTopPosPx(n);
			deleteField.setTopPosPx(n);
		}

		public void removeFromForm() {
			form.removeFieldNoThrow(nameField);
			form.removeFieldNoThrow(colorField);
			form.removeFieldNoThrow(deleteField);
		}

		public String getName() {
			return "$" + SH.trim(nameField.getValue());
		}
	}

	public AmiWebStyleVarPortlet(PortletConfig config, AmiWebDesktopPortlet desktop) {
		super(config);
		form = new FormPortlet(generateConfig());
		this.addButton = new FormPortletButtonField("").setValue("Add New Color");
		this.arrangeButton = new FormPortletButtonField("").setValue("Arrange Colors");
		this.addChild(form);
		FormPortletField nameTitle = this.form.addField(new FormPortletTitleField("").setValue("Name")).setCssStyle("_fm=underline");
		FormPortletField colorTitle = this.form.addField(new FormPortletTitleField("").setValue("Color")).setCssStyle("_fm=underline");
		nameTitle.setLeftTopWidthHeightPx(10, 25, 180, 20);
		colorTitle.setLeftTopWidthHeightPx(200, 25, 100, 20);
		this.form.addFormPortletListener(this);
	}

	public void setStyle(AmiWebStyle style) {
		this.style = style;
		rebuild();
	}

	public void rebuild() {
		for (int i = 0; i < this.rows.size(); i++)
			this.rows.get(i).removeFromForm();
		this.rows.clear();
		AmiWebStyleVars vars = this.style.getVars();
		Iterator<Entry<String, String>> iter = vars.getColorIterator();
		while (iter.hasNext()) {
			Entry<String, String> i = iter.next();
			this.rows.add(new Row(rows.size(), i.getKey(), i.getValue()));
		}
		updateButtonsPos();
	}

	private void updateButtonsPos() {
		if (style.getReadOnly()) {
			this.form.removeFieldNoThrow(this.addButton);
			this.form.removeFieldNoThrow(this.arrangeButton);
		} else {
			this.form.addFieldNoThrow(this.addButton);
			this.form.addFieldNoThrow(this.arrangeButton);
			this.addButton.setLeftTopWidthHeightPx(200, 50 + rows.size() * 25, 100, 20);
			this.arrangeButton.setLeftTopWidthHeightPx(200, 75 + rows.size() * 25, 100, 20);
		}
	}

	public void sortRows(List<String> newRowOrder) {
		this.rows.sort(new RowComparator(newRowOrder));
		for (int i = 0; i < rows.size(); i++)
			this.rows.get(i).setPos(i);
	}

	private class RowComparator implements Comparator<Row> {
		List<String> keyOrder;

		public RowComparator(List<String> keyOrder) {
			this.keyOrder = keyOrder;
		}
		@Override
		public int compare(Row o1, Row o2) {
			return this.keyOrder.indexOf(Integer.toString(o1.pos)) - this.keyOrder.indexOf(Integer.toString(o2.pos));
		}
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.addButton) {
			Row e = new Row(rows.size(), "", null);
			this.rows.add(e);
			updateButtonsPos();
		} else if (field == this.arrangeButton) {
			List<String> rowList = new ArrayList<String>();
			for (Row r : this.rows)
				rowList.add(r.nameField.getValue());
			getManager().showDialog("Arrange Variables", new AmiWebStyleArrangeVarsPortlet(generateConfig(), this, rowList));
		} else if (field.getCorrelationData() instanceof Row) {
			Row row = (Row) field.getCorrelationData();
			if (field == row.deleteField) {
				String name = row.getName();
				if (this.style.getVars().isColorUsed(name)) {
					getManager().showAlert("Can not remove Color because it is being used: " + name);
					return;
				}
				row.removeFromForm();
				int pos = row.pos;
				this.rows.remove(pos);
				for (int i = pos; i < rows.size(); i++)
					this.rows.get(i).setPos(i);
				updateButtonsPos();
			} else if (field == row.colorField) {
				String name = row.getName();
				if (AmiUtils.isValidVariableName(SH.stripPrefix(name, "$", false), false, false)) {
					this.style.getVars().addColor(name, row.colorField.getValue());
				}
			}
		}
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
	}

	public boolean apply(StringBuilder errorSink) {
		BasicIndexedList<String, Row> rows = new BasicIndexedList<String, AmiWebStyleVarPortlet.Row>();
		for (int i = 0; i < this.rows.size(); i++) {
			Row row = this.rows.get(i);
			String name = row.getName();
			if (!AmiUtils.isValidVariableName(SH.stripPrefix(name, "$", false), false, false)) {
				errorSink.append("Invalid Variable Name: " + name);
				row.nameField.focus();
				return false;
			}
			if (rows.containsKey(name)) {
				errorSink.append("Duplicate Variable Name: " + name);
				row.nameField.focus();
				return false;
			}
			rows.add(name, row);
		}
		Set<String> remove = CH.comm(rows.keySet(), this.style.getVars().getColorKeys(), false, true, false);
		for (String s : remove)
			if (this.style.getVars().isColorUsed(s)) {
				errorSink.append("Can not remove color because it is being used: " + s);
				return false;
			}
		for (String s : remove)
			this.style.getVars().removeColor(s);
		for (Entry<String, Row> e : rows.entrySet())
			this.style.getVars().addColor(e.getKey(), e.getValue().colorField.getValue());
		ArrayList<String> rowsList = new ArrayList<String>();
		Iterator<Entry<String, AmiWebStyleVarPortlet.Row>> iter = rows.iterator();
		while (iter.hasNext())
			rowsList.add(iter.next().getKey());
		this.style.getVars().arrange(rowsList);
		return true;
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

}
