package com.f1.ami.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletMultiSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.tree.impl.FastWebColumn;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.utils.SH;

public class AmiWebSearchColumnsPortlet extends FormPortlet implements FormPortletListener, AmiWebLockedPermissiblePortlet {
	private FastWebColumns table;
	private FastWebColumn userColumn;
	private HashMap<String, FastWebColumn> columns;
	private HashMap<String, FastWebColumn> filteredColumns;
	private FormPortletTextField searchBar;
	private FormPortletMultiSelectField<String> resultsField;
	private FormPortletCheckboxField remainOpen;
	private FormPortletButton addRightBtn;
	private FormPortletButton addLeftBtn;
	private FormPortletButton canelBtn;
	private FormPortletButton snapBtn;
	private int referenceLocation;
	private final int offsetForFastWebTrees;
	public static int DIALOG_W = 500;
	public static int DIALOG_H = 380;

	public AmiWebSearchColumnsPortlet(PortletConfig config, FastWebColumns table, FastWebColumn column) {
		super(config);
		// Inits
		this.table = table;
		this.userColumn = column;
		this.columns = new HashMap<String, FastWebColumn>();
		this.filteredColumns = new HashMap<String, FastWebColumn>();
		this.addFormPortletListener(this);
		setStyle(AmiWebUtils.getService(getManager()).getUserFormStyleManager());

		// Init Form
		this.addField(new FormPortletTitleField("Search"));
		this.searchBar = this.addField(new FormPortletTextField(""));
		this.addField(new FormPortletTitleField("Results"));
		this.resultsField = this.addField(new FormPortletMultiSelectField<String>(String.class, ""));
		this.addField(new FormPortletTitleField("Keep this dialog open"));
		this.searchBar.setWidth(300);
		this.searchBar.focus();
		this.resultsField.setSize(120);
		this.resultsField.setWidth(300);

		// Init Buttons
		this.remainOpen = this.addField(new FormPortletCheckboxField(""));
		this.addLeftBtn = this.addButton(new FormPortletButton("Add to Left"));
		this.addRightBtn = this.addButton(new FormPortletButton("Add to Right"));
		this.snapBtn = this.addButton(new FormPortletButton("Snap"));
		this.canelBtn = this.addButton(new FormPortletButton("Close"));

		// Custom logic for FWT's
		if (table instanceof FastWebTree) {
			offsetForFastWebTrees = 1;
		} else {
			offsetForFastWebTrees = 0;
		}

		// Get column location (to add before or after)
		if (column == null)
			this.referenceLocation = 0;
		else
			this.referenceLocation = table.getColumnPosition(column.getColumnId()) - offsetForFastWebTrees;

		// Init Columns
		int visibleColumnsCount = table.getVisibleColumnsCount();
		int hiddenColumnsCount = table.getHiddenColumnsCount();
		for (int i = offsetForFastWebTrees; i < (visibleColumnsCount + offsetForFastWebTrees); i++) {
			FastWebColumn currentColumn = table.getVisibleColumn(i);
			columns.put(SH.toString(i), currentColumn);
		}

		for (int i = 0; i < (hiddenColumnsCount); i++) {
			FastWebColumn currentColumn = table.getHiddenColumn(i);
			columns.put(SH.toString(i + visibleColumnsCount), currentColumn);
		}

		// Propagate Results Field
		Map<String, String> namesToKeys = new TreeMap<String, String>(SH.COMPARATOR_CASEINSENSITIVE_STRING);
		for (String key : columns.keySet()) {
			namesToKeys.put(columns.get(key).getColumnName(), key);
		}
		for (Entry<String, String> key : namesToKeys.entrySet())
			resultsField.addOption(key.getValue(), key.getKey());

	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button.equals(this.canelBtn)) {
			this.close();
			return;
		} else if (button.equals(this.snapBtn)) {
			if (resultsField.getSelectedValueKeys().size() == 1) {
				FastWebColumn column = null;
				for (String key : resultsField.getSelectedValueKeys()) {
					column = columns.get(key);
				}
				if (column != null) {
					table.snapToColumn(column.getColumnId());
				}
			}
			this.close();
			return;
		} else if (button.equals(addLeftBtn) || button.equals(addRightBtn)) {
			boolean isLimitExceeded = this.table.getVisibleColumnsLimit() != -1
					&& (this.table.getVisibleColumnsCount() + getHiddenColCountFromSelected() > this.table.getVisibleColumnsLimit());
			if (isLimitExceeded) {
				getManager().showAlert("Could not add column(s). Column visibility limit is set to " + this.table.getVisibleColumnsLimit() + " and will be exceeded.");
			} else {
				for (String key : resultsField.getSelectedValueKeys()) {
					FastWebColumn column = columns.get(key);
					int targetLocation = table.getColumnPosition(column.getColumnId());
					table.hideColumn(column.getColumnId().toString());
					if (button.equals(this.addLeftBtn)) {
						addColumntoLeft(column, referenceLocation, targetLocation);
					} else {
						addColumntoRight(column, referenceLocation, targetLocation);
					}
				}
				table.fireOnColumnsArranged();
			}
		}
		if (!this.remainOpen.getBooleanValue()) {
			this.close();
		}
	}
	private int getHiddenColCountFromSelected() {
		int count = 0;
		for (String key : resultsField.getSelectedValueKeys()) {
			FastWebColumn column = columns.get(key);
			int targetLocation = table.getColumnPosition(column.getColumnId());
			if (table.getVisibleColumn(targetLocation) == null)
				count++;
		}
		return count;
	}
	private void addColumntoRight(FastWebColumn column, int referenceLocation, int targetLocation) {
		//If targetLocation <= referenceLocation, add to the reference location otherwise add to the reference location + 1 
		if ((targetLocation <= (referenceLocation + offsetForFastWebTrees)) && targetLocation >= 0) {
			// Adds at the position
			table.showColumn(column.getColumnId().toString(), referenceLocation);
		} else {
			// Adds after the position
			table.showColumn(column.getColumnId().toString(), referenceLocation + 1);
		}
	}
	private void addColumntoLeft(FastWebColumn column, int referenceLocation, int targetLocation) {
		//If targetLocation >= referenceLocation, add to the reference location, otherwise add to the referenceLocation - 1
		if ((targetLocation < (referenceLocation + offsetForFastWebTrees)) && targetLocation >= 0) {
			// Adds before the position
			table.showColumn(column.getColumnId().toString(), referenceLocation - 1);
		} else {
			// Adds at the position
			table.showColumn(column.getColumnId().toString(), referenceLocation);
		}
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field instanceof FormPortletTextField) {
			resultsField.clear();
			filteredColumns.clear();
			if (field.getValue() != null) {
				for (String key : columns.keySet()) {
					String columnName = columns.get(key).getColumnName();
					if (SH.indexOfIgnoreCase(columnName, (String) field.getValue(), 0) != -1) {
						filteredColumns.put(key, columns.get(key));
					}
				}

				for (String key : filteredColumns.keySet()) {
					resultsField.addOption(key, filteredColumns.get(key).getColumnName());
				}
			}
		}
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		if (keycode == 13) {
			resultsField.clear();
			filteredColumns.clear();
			if (field.getValue() != null) {
				for (String key : columns.keySet()) {
					String columnName = columns.get(key).getColumnName();
					if (SH.indexOfIgnoreCase(columnName, (String) field.getValue(), 0) != -1) {
						filteredColumns.put(key, columns.get(key));
					}
				}

				for (String key : filteredColumns.keySet()) {
					resultsField.addOption(key, filteredColumns.get(key).getColumnName());
				}
			}
		}
	}

}
