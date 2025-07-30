package com.f1.suite.web.portal.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.f1.base.Row;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletFieldAutoCompleteExtension;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletMultiSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextEditField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.suite.web.table.WebCellEnumFormatter;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.WebTableFilteredInFilter;
import com.f1.utils.CH;
import com.f1.utils.LocalToolkit;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class TableFilterPortlet extends GridPortlet implements FormPortletListener, FormPortletContextMenuListener, ConfirmDialogListener {

	private static final byte MASK_SHOW = 1;
	private static final byte MASK_PATTERN = 2;
	public static final int MAX_VALUES_COUNT = 200;

	//GUI Components
	private FormPortlet form;
	private FormPortletButton clearFilterButton;
	private FormPortletButton filterButton;
	private FormPortletButton cancelButton;
	private FormPortletMultiSelectField<String> selectField;
	private FormPortletSelectField<Byte> typeField;
	private FormPortletTextField minField;
	private FormPortletTextField maxField;
	private FormPortletCheckboxField includeNullField;
	private FormPortletTextField customValueField;
	private FormPortletFieldAutoCompleteExtension<FormPortletTextEditField> customValueAcExt;

	//Table and related data
	private Object valueField;
	private WebColumn column;
	private FastWebTable table;
	private int columnIndex;
	private FormPortletSelectField<Boolean> minInclusiveField;
	private FormPortletSelectField<Boolean> maxInclusiveField;

	public TableFilterPortlet(PortletConfig config, FastTablePortlet table, WebColumn column) {
		super(config);
		//Table and related data
		this.table = table.getTable();
		this.column = column;
		this.columnIndex = this.table.getColumnPosition(column.getColumnId());

		//GUI components
		this.form = addChild(new FormPortlet(generateConfig()), 0, 0);
		this.form.addFormPortletListener(this);

		this.typeField = new FormPortletSelectField<Byte>(Byte.class, "Filter By:");
		//.addOption(Boolean.TRUE, "Showing only the rows that...").addOption( Boolean.FALSE, "Hiding any rows that..."));
		this.typeField.addOption(MASK_SHOW, "Show rows that exactly match...");
		this.typeField.addOption((byte) (MASK_PATTERN | MASK_SHOW), "Show rows that pattern match...");
		this.typeField.addOption((byte) 0, "Hide rows that exactly match...");
		this.typeField.addOption(MASK_PATTERN, "Hide rows that pattern match...");
		this.typeField.setValue(MASK_SHOW);
		this.clearFilterButton = new FormPortletButton("Clear");
		this.filterButton = new FormPortletButton("Filter");
		this.cancelButton = new FormPortletButton("Cancel");
		FormPortletTitleField title = new FormPortletTitleField("");
		this.selectField = new FormPortletMultiSelectField<String>(String.class, "");
		this.selectField.setSize(170);
		this.selectField.setWidth(300);
		//		this.addButtonField = new FormPortletButtonField("").setValue("Add Custom Value");

		//Add test ac field
		this.customValueField = new FormPortletTextField("Custom Value:");
		this.customValueAcExt = new FormPortletFieldAutoCompleteExtension<FormPortletTextEditField>(customValueField);

		//Set suggestions
		//		List<String> suggestions = new ArrayList<String>();
		//		for (int i = 0; i < this.table.getRowsCount(); i++) {
		//			// Get the value from the column at i 
		//			String value = this.table.getValueAsText(this.table.getRow(i), columnIndex, new StringBuilder()).toString();
		//			suggestions.add(value);
		//		}

		this.includeNullField = new FormPortletCheckboxField("Include null:");
		FormPortletTitleField title2 = new FormPortletTitleField("Or are in the Range:");
		this.minInclusiveField = new FormPortletSelectField<Boolean>(Boolean.class, "At Least:").addOption(Boolean.TRUE, "Inclusive").addOption(Boolean.FALSE, "Exclusive");
		this.minField = new FormPortletTextField("At Least:");
		this.maxInclusiveField = new FormPortletSelectField<Boolean>(Boolean.class, "At Most:").addOption(Boolean.TRUE, "Inclusive").addOption(Boolean.FALSE, "Exclusive");
		this.maxField = new FormPortletTextField("At Most:");

		//Add fields
		this.form.addField(this.typeField);
		this.form.addButton(this.filterButton);
		this.form.addButton(this.clearFilterButton);
		this.form.addButton(this.cancelButton);
		this.form.addField(title);
		this.form.addField(this.selectField);
		//		this.form.addField(this.addButtonField);
		this.form.addField(this.customValueField);
		this.form.addField(title2);
		this.form.addField(this.minInclusiveField);
		this.form.addField(this.minField);
		this.form.addField(this.maxInclusiveField);
		this.form.addField(this.maxField);
		this.form.addField(this.includeNullField);
		this.form.addMenuListener(this);

		String columnId = column.getColumnId();
		int maxShowValuesCount = table.getMaxShowValuesForFilterDialog();
		WebTableFilteredInFilter filter = table.getTable().getFiltererdIn(columnId);
		Set<String> values = getValues(column, columnId, Integer.MAX_VALUE, filter);
		this.customValueAcExt.setSuggestions(new ArrayList<String>(values));

		//Get values

		String titleVal;
		// If there are more values than maxShowValuesCount then set the title to show what the max is
		if (values.size() > maxShowValuesCount) {
			titleVal = "Match Selected values(first " + maxShowValuesCount + " shown):";
			Iterator<String> it = values.iterator();
			Set<String> vals2 = new HashSet<String>();
			if (filter != null)//make sure we atleast include the prior filter items
				for (String i : filter.getValues())
					vals2.add(i);
			for (int i = 0; i < maxShowValuesCount; i++)
				vals2.add(it.next());
			for (String v : CH.sort(vals2))
				this.selectField.addOption(v, v);
		} else {
			titleVal = "Match Selected values:";
			//Add all the values to the select field
			for (String v : CH.sort(values))
				this.selectField.addOption(v, v);
		}
		title.setValue(titleVal);

		if (filter != null) {
			this.selectField.setValue(new HashSet<String>(filter.getValues()));
			this.maxField.setValue(filter.getMax());
			this.minField.setValue(filter.getMin());
			this.maxInclusiveField.setValue(filter.getMaxInclusive());
			this.minInclusiveField.setValue(filter.getMinInclusive());
			byte mask = 0;
			if (filter.getKeep())
				mask |= MASK_SHOW;
			if (filter.getIsPattern())
				mask |= MASK_PATTERN;
			this.typeField.setValue(mask);
			this.includeNullField.setValue(filter.getIncludeNull());
		}
		this.customValueField.focus();
		setSuggestedSize(500, 550);
	}
	private Set<String> getValues(WebColumn column, String columnId, int maxShowValuesCount, WebTableFilteredInFilter filter) {
		Set<String> values = new LinkedHashSet<String>();
		//Adds values from the filter if any
		if (filter != null)
			for (String value : filter.getValues())
				values.add(value);

		//Add values from the columnCellFormatter if any
		if (column.getCellFormatter() instanceof WebCellEnumFormatter) {
			WebCellEnumFormatter<?> enumFormatter = (WebCellEnumFormatter<?>) column.getCellFormatter();
			for (String value : enumFormatter.getEnumValuesAsText().values())
				values.add(value);
		}

		//Add values from the column til it reaches the max values to show
		StringBuilder tmp = new StringBuilder();
		for (int i = 0; i < this.table.getRowsCount(); i++) {
			if (values.size() >= maxShowValuesCount)
				break;
			// Get the value from the column at i 
			String value = this.table.getValueAsText(this.table.getRow(i), columnIndex, SH.clear(tmp)).toString();
			values.add(value);
		}
		SH.clear(tmp);

		if (filter != null && values.size() < maxShowValuesCount) {
			Set<String> allFilteres = this.table.getFilteredInColumns();
			if (allFilteres.size() > 1) {
				LocalToolkit tk = new LocalToolkit();
				WebTableFilteredInFilter[] otherFilters = new WebTableFilteredInFilter[allFilteres.size() - 1];
				int pos = 0;
				for (String s : allFilteres)
					if (OH.ne(columnId, s))
						otherFilters[pos++] = this.table.getFiltererdIn(s);
				outer: for (Row row : this.table.getFilteredRows()) {
					if (values.size() >= maxShowValuesCount)
						break;
					for (WebTableFilteredInFilter otherFilter : otherFilters)
						if (!otherFilter.shouldKeep(row, tk))
							continue outer;
					String value = this.table.getValueAsText(row, columnIndex, SH.clear(tmp)).toString();
					values.add(value);
				}
			} else {//there are no other filters
				for (Row row : this.table.getFilteredRows()) {
					if (values.size() >= maxShowValuesCount)
						break;
					String value = this.table.getValueAsText(row, columnIndex, SH.clear(tmp)).toString();
					values.add(value);
				}
			}
		}
		return values;
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.cancelButton)
			close();
		else if (button == this.filterButton) {
			addFilterValue();
			close();
		} else if (button == this.clearFilterButton) {
			table.setFilteredIn(column.getColumnId(), (Set) null);
			close();
		}

	}
	private void addFilterValue() {
		addCustomValueOption();
		Set<String> values = new HashSet<String>();
		values.addAll(this.selectField.getValue());
		byte value = this.typeField.getValue();
		this.table.setFilteredIn(this.column.getColumnId(), values, (value & MASK_SHOW) != 0, this.includeNullField.getBooleanValue(), (value & MASK_PATTERN) != 0,
				this.minField.getValue(), this.minInclusiveField.getValue(), this.maxField.getValue(), this.maxInclusiveField.getValue());
	}
	private void updateFilterValue() {
		Set<String> values = new HashSet<String>(this.selectField.getValue());
		values.clear();
		values.add(customValueField.getValue());
		byte value = this.typeField.getValue();
		this.table.setFilteredIn(this.column.getColumnId(), values, (value & MASK_SHOW) != 0, this.includeNullField.getBooleanValue(), (value & MASK_PATTERN) != 0, null, null);
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.selectField && "dblclick".equals(attributes.get("action"))) {
			addFilterValue();
			close();
		}
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		// TODO Auto-generated method stub
		if (field == this.customValueField) {
			//If enter is registered
			if (keycode == 13 && mask == 2) { //Shift enter
				addCustomValueOption();
			} else if (keycode == 13 && mask == 4) { //Alt enter
				addFilterValue();
				close();
			} else if (keycode == 13) {
				addFilterValue();
				close();
			}
		}

	}
	private void addCustomValueOption() {
		String value = customValueField.getValue();
		if (value == null)
			return;
		Set<String> keys = new HashSet<String>(this.selectField.getValue());
		if (keys.contains(value)) {
			this.customValueField.setValue("");
			return;
		}
		if (this.selectField.getOptionNoThrow(value) == null)
			this.selectField.addOption(value, value);
		keys.add(value);
		this.selectField.setValue(keys);
		this.customValueField.setValue("");
	}

	public void setValues(Set<String> values) {
		for (String e : values)
			this.selectField.addOption(e, e);
	}
	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		//		if (node == this.addButtonField) {
		//			getManager().showDialog("Add Custom Value to Filter",
		//					new ConfirmDialogPortlet(generateConfig(), "Add Custom Value", ConfirmDialogPortlet.TYPE_OK_CANCEL, this, new FormPortletTextField("")));
		//		}
	}
	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		//		if (ConfirmDialogPortlet.ID_YES.equals(id)) {
		//			String value = (String) source.getInputFieldValue();
		//			if (this.selectField.getOptionNoThrow(value) == null)
		//				this.selectField.addOption(value, value);
		//			Set<String> keys = new HashSet<String>(this.selectField.getValue());
		//			keys.add(value);
		//			this.selectField.setValue(keys);
		//		}
		return true;
	}
	public TableFilterPortlet setFormStyle(PortletStyleManager_Form styleManager) {
		this.form.setStyle(styleManager);
		return this;
	}
}
