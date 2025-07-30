package com.f1.suite.web.tree.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletMultiSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletMultiSelectField.Option;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.style.PortletStyleManager;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.IndexedList;

public class ArrangeColumnsPortlet extends GridPortlet implements FormPortletListener, FormPortletContextMenuListener {

	private FastWebColumns fastColumns;
	private static final String TOP_BUTTON_TEXT = "<svg xmlns='http://www.w3.org/2000/svg' width='14px' height='14px' viewBox='0 -8 32 32' fill='currentColor'> <path d='M4 2v-4h24v4H4ZM16 30V14l-6 6-4-4 10-10 10 10-4 4-6-6v16H16Z'/></svg>";
	private static final String BOTTOM_BUTTON_TEXT = "<svg transform=\"scale(-1 -1)\" xmlns='http://www.w3.org/2000/svg' width='14px' height='14px' viewBox='0 -8 32 32' fill='currentColor'> <path d='M4 2v-4h24v4H4ZM16 30V14l-6 6-4-4 10-10 10 10-4 4-6-6v16H16Z'/></svg>";
	private static final String UP_BUTTON_TEXT = "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"24\" height=\"24\" viewBox=\"4 -5 28 28\" fill='currentColor'><path d=\"M12 2L6 8h5v8h2V8h5z\"></path></svg>";
	private static final String DOWN_BUTTON_TEXT = "<svg transform=\"scale(1 -1)\" xmlns=\"http://www.w3.org/2000/svg\" width=\"24\" height=\"24\" viewBox=\"4 -5 28 28\" fill='currentColor'><path d=\"M12 2L6 8h5v8h2V8h5z\"></path></svg>";
	private static final String LEFT_BUTTON_TEXT = "<svg transform=\"rotate(-90 0 0)\" xmlns=\"http://www.w3.org/2000/svg\" width=\"24\" height=\"24\" viewBox=\"-1 -5 28 28\" fill='currentColor'><path d=\"M12 2L6 8h5v8h2V8h5z\"></path></svg>";
	private static final String RIGHT_BUTTON_TEXT = "<svg transform=\"rotate(90 0 0)\" xmlns=\"http://www.w3.org/2000/svg\" width=\"24\" height=\"24\" viewBox=\"-3 -5 28 28\" fill='currentColor'><path d=\"M12 2L6 8h5v8h2V8h5z\"></path></svg>";
	private FormPortletButton close;
	private FormPortlet visibleColumnsPortlet;
	private FormPortletMultiSelectField<String> visibleColumnsField;
	private FormPortlet hiddenRowsPortlet;
	private FormPortletMultiSelectField<String> hiddenColumnsField;
	private FormPortlet arrangeButtonsPortlet;
	private IndexedList<String, Option<String>> sortedOptions;
	private FormPortletButton apply;
	private FormPortlet leftButtonPortlet;
	private FormPortlet rightArrangePortlet;
	private FormPortlet actionButtonsPortlet;
	private FormPortletTitleField visibleTitleField;
	private FormPortletTitleField hiddenTitleField;
	private FormPortlet searchFormPortlet;
	private FormPortletTextField searchField;
	private FormPortletTextField searchFieldText;
	private PortletStyleManager_Form formStyle;
	private PortletStyleManager styleManager;
	private FormPortletButtonField topButton;
	private FormPortletButtonField upButton;
	private FormPortletButtonField downButton;
	private FormPortletField<?> bottomButton;
	private FormPortletField<?> leftButton;
	private FormPortletField<?> rightButton;

	public ArrangeColumnsPortlet(PortletConfig config, FastWebColumns fastColumns, FastWebColumn selectedColumn) {
		super(config);
		this.fastColumns = fastColumns;
		styleManager = getManager().getStyleManager();
		formStyle = styleManager.getFormStyle();
		sortedOptions = new BasicIndexedList<String, Option<String>>();
		prepareFormPortlets();
		if (selectedColumn != null) {
			this.visibleColumnsField.setValue(CH.s(selectedColumn.getColumnId().toString()));
		}
		prepareGrid();
		prepareStyle();
	}
	private void prepareStyle() {
		if (formStyle.isUseDefaultStyling())
			return;
		this.searchField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=" + formStyle.getDefaultFormFontFam());
		this.searchField.setBgColor(formStyle.getDefaultFormBgColor());
		this.searchField.setBorderColor(formStyle.getFormBorderColor());
		this.searchField.setFontColor(formStyle.getDefaultFormFontColor());
		this.searchField.setFieldFontFamily(formStyle.getDefaultFormFontFam());
		this.searchFieldText.setBgColor(formStyle.getDefaultFormBgColor());
		this.searchFieldText.setBorderColor(formStyle.getFormBorderColor());
		this.searchFieldText.setFontColor(formStyle.getDefaultFormFontColor());
		this.searchFieldText.setFieldFontFamily(formStyle.getDefaultFormFontFam());
		this.visibleTitleField.setCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=" + formStyle.getDefaultFormFontFam());
		this.visibleColumnsField.setUseCustomScrollbar(true);
		this.visibleColumnsField.setFieldFontFamily(formStyle.getDefaultFormFontFam());
		// font color
		this.visibleColumnsField.setFontColor(formStyle.getDefaultFormFontColor());
		// field bg color
		this.visibleColumnsField.setBgColor(formStyle.getDefaultFormBgColor());
		this.visibleColumnsField.setBorderColor(formStyle.getFormBorderColor());
		this.hiddenTitleField.setFieldFontFamily(formStyle.getDefaultFormFontFam());
		this.hiddenTitleField.setCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=" + formStyle.getDefaultFormFontFam() + "|style.font-weight=bold");
		this.hiddenColumnsField.setUseCustomScrollbar(true);
		this.hiddenColumnsField.setFieldFontFamily(formStyle.getDefaultFormFontFam());
		this.hiddenColumnsField.setBgColor(formStyle.getDefaultFormBgColor());
		this.hiddenColumnsField.setFontColor(formStyle.getDefaultFormFontColor());
		this.hiddenColumnsField.setBorderColor(formStyle.getFormBorderColor());

		this.upButton.setCssStyle("style.background=" + this.formStyle.getFormButtonBackgroundColor() + "|style.color=" + formStyle.getFormButtonFontColor() + "|_fs="
				+ formStyle.getDefaultFormFontFam() + "|style.top-color=" + formStyle.getDefaultFormButtonTopColor() + "|style.border-left-color="
				+ formStyle.getDefaultFormButtonTopColor() + "|style.border-right-color=" + formStyle.getDefaultFormButtonBottomColor() + "|style.border-bottom-color="
				+ formStyle.getDefaultFormButtonBottomColor());

		this.downButton.setCssStyle("style.background=" + this.formStyle.getFormButtonBackgroundColor() + "|style.color=" + formStyle.getFormButtonFontColor() + "|_fs="
				+ formStyle.getDefaultFormFontFam() + "|style.top-color=" + formStyle.getDefaultFormButtonTopColor() + "|style.border-left-color="
				+ formStyle.getDefaultFormButtonTopColor() + "|style.border-right-color=" + formStyle.getDefaultFormButtonBottomColor() + "|style.border-bottom-color="
				+ formStyle.getDefaultFormButtonBottomColor());

		this.topButton.setCssStyle("style.background=" + this.formStyle.getFormButtonBackgroundColor() + "|style.color=" + formStyle.getFormButtonFontColor() + "|_fs="
				+ formStyle.getDefaultFormFontFam() + "|style.top-color=" + formStyle.getDefaultFormButtonTopColor() + "|style.border-left-color="
				+ formStyle.getDefaultFormButtonTopColor() + "|style.border-right-color=" + formStyle.getDefaultFormButtonBottomColor() + "|style.border-bottom-color="
				+ formStyle.getDefaultFormButtonBottomColor());

		this.bottomButton.setCssStyle("style.background=" + this.formStyle.getFormButtonBackgroundColor() + "|style.color=" + formStyle.getFormButtonFontColor() + "|_fs="
				+ formStyle.getDefaultFormFontFam() + "|style.top-color=" + formStyle.getDefaultFormButtonTopColor() + "|style.border-left-color="
				+ formStyle.getDefaultFormButtonTopColor() + "|style.border-right-color=" + formStyle.getDefaultFormButtonBottomColor() + "|style.border-bottom-color="
				+ formStyle.getDefaultFormButtonBottomColor());

		this.leftButton.setCssStyle("style.background=" + this.formStyle.getFormButtonBackgroundColor() + "|style.color=" + formStyle.getFormButtonFontColor() + "|_fs="
				+ formStyle.getDefaultFormFontFam() + "|style.top-color=" + formStyle.getDefaultFormButtonTopColor() + "|style.border-left-color="
				+ formStyle.getDefaultFormButtonTopColor() + "|style.border-right-color=" + formStyle.getDefaultFormButtonBottomColor() + "|style.border-bottom-color="
				+ formStyle.getDefaultFormButtonBottomColor());

		this.rightButton.setCssStyle("style.background=" + this.formStyle.getFormButtonBackgroundColor() + "|style.color=" + formStyle.getFormButtonFontColor() + "|_fs="
				+ formStyle.getDefaultFormFontFam() + "|style.top-color=" + formStyle.getDefaultFormButtonTopColor() + "|style.border-left-color="
				+ formStyle.getDefaultFormButtonTopColor() + "|style.border-right-color=" + formStyle.getDefaultFormButtonBottomColor() + "|style.border-bottom-color="
				+ formStyle.getDefaultFormButtonBottomColor());

	}
	public ArrangeColumnsPortlet(PortletConfig config, FastWebColumns fastColumns) {
		this(config, fastColumns, null);
	}

	public FastWebColumns getFastWebColumn() {
		return this.fastColumns;
	}

	public Set<String> getVisibleColumns() {
		return this.visibleColumnsField.getOptionKeys();
	}

	public Set<String> getHiddenColumns() {
		return this.hiddenColumnsField.getOptionKeys();
	}

	private void prepareGrid() {
		this.searchFormPortlet = new FormPortlet(generateConfig());
		this.searchField = new FormPortletTextField("search");
		this.searchField.setTitle("Search:");
		searchField.setTopPosPx(10).setRightPosPx(85).setHeightPx(20).setWidthPx(200);

		this.searchFieldText = new FormPortletTextField("");
		this.searchFieldText.setBorderWidth(0);

		this.searchFieldText.setDisabled(true);
		searchFieldText.setTopPosPx(10).setRightPosPx(20).setHeightPx(20).setWidthPx(65);
		searchFormPortlet.addField(searchFieldText);
		searchFormPortlet.addField(searchField);
		searchFormPortlet.addFormPortletListener(this);
		this.addChild(searchFormPortlet, 0, 0, 5, 1);
		this.addChild(leftButtonPortlet, 0, 1);
		this.addChild(visibleColumnsPortlet, 1, 1);
		this.addChild(arrangeButtonsPortlet, 2, 1);
		this.addChild(rightArrangePortlet, 3, 1);
		this.addChild(hiddenRowsPortlet, 4, 1);
		this.addChild(actionButtonsPortlet, 0, 2, 5, 1);
		this.setColSize(0, 45);
		this.setColSize(2, 60);
		this.setColSize(3, 60);
		this.setRowSize(0, 40);
		this.setRowSize(2, 50);
		setSize(200, 800);
		int maxHeight = getManager().getRoot().getHeight();
		int cnt = 15 * (5 + this.hiddenColumnsField.getOptionKeys().size());
		int cnt2 = 15 * (5 + this.visibleColumnsField.getOptionKeys().size());
		int selectHeight = Math.max(cnt, cnt2) + 100;
		setSuggestedSize(880, Math.max(350, Math.min(selectHeight, maxHeight - 100)));
	}

	private void prepareFormPortlets() {
		this.visibleColumnsPortlet = new FormPortlet(generateConfig());
		this.visibleTitleField = visibleColumnsPortlet.addField(new FormPortletTitleField("Visible Columns"));
		// field label color
		this.visibleColumnsField = visibleColumnsPortlet.addField(new FormPortletMultiSelectField<String>(String.class, ""));
		this.visibleColumnsField.setLeftPosPx(1);
		this.visibleColumnsField.setRightPosPx(0);
		this.visibleColumnsField.setTopPosPx(35);
		this.visibleColumnsField.setBottomPosPx(10);
		visibleColumnsPortlet.getFormPortletStyle().setLabelsWidth(1);

		this.hiddenRowsPortlet = new FormPortlet(generateConfig());
		this.hiddenTitleField = hiddenRowsPortlet.addField(new FormPortletTitleField("Hidden Columns"));
		this.hiddenColumnsField = hiddenRowsPortlet.addField(new FormPortletMultiSelectField<String>(String.class, ""));
		this.hiddenColumnsField.setLeftPosPx(4);
		this.hiddenColumnsField.setRightPosPx(20);
		this.hiddenColumnsField.setTopPosPx(35);
		this.hiddenColumnsField.setBottomPosPx(10);
		hiddenRowsPortlet.getFormPortletStyle().setLabelsWidth(4);

		this.arrangeButtonsPortlet = new FormPortlet(generateConfig());
		arrangeButtonsPortlet.getFormPortletStyle().setLabelsWidth(5);
		arrangeButtonsPortlet.addField(new FormPortletTitleField(""));
		arrangeButtonsPortlet.addField(new FormPortletTitleField(""));
		arrangeButtonsPortlet.addField(new FormPortletTitleField(""));
		arrangeButtonsPortlet.addField(new FormPortletTitleField(""));
		leftButton = arrangeButtonsPortlet.addField(new FormPortletButtonField("")).setValue(LEFT_BUTTON_TEXT).setLabelHidden(true).setHeightPx(25).setWidthPx(40);
		arrangeButtonsPortlet.addFormPortletListener(this);
		arrangeButtonsPortlet.addMenuListener(this);
		populateField();

		this.rightArrangePortlet = new FormPortlet(generateConfig());
		rightArrangePortlet.addField(new FormPortletTitleField(""));
		rightArrangePortlet.addField(new FormPortletTitleField(""));
		rightArrangePortlet.addField(new FormPortletTitleField(""));
		rightArrangePortlet.addField(new FormPortletTitleField(""));
		rightButton = rightArrangePortlet.addField(new FormPortletButtonField("")).setValue(RIGHT_BUTTON_TEXT).setLabelHidden(true).setHeightPx(25).setWidthPx(40);
		rightArrangePortlet.addMenuListener(this);
		rightArrangePortlet.addFormPortletListener(this);
		rightArrangePortlet.getFormPortletStyle().setLabelsWidth(5);

		this.actionButtonsPortlet = new FormPortlet(generateConfig());
		this.apply = actionButtonsPortlet.addButton(new FormPortletButton("Apply"));
		this.close = actionButtonsPortlet.addButton(new FormPortletButton("Cancel"));

		this.actionButtonsPortlet.addFormPortletListener(this);

		int buttonSpacingPx = 25;
		int buttonDimPx = 25;
		this.leftButtonPortlet = new FormPortlet(generateConfig());
		topButton = (FormPortletButtonField) leftButtonPortlet.addField(new FormPortletButtonField("")).setValue(TOP_BUTTON_TEXT).setLeftPosPx(10).setWidthPx(buttonDimPx)
				.setTopPosPx(50).setHeightPx(buttonDimPx);
		upButton = (FormPortletButtonField) leftButtonPortlet.addField(new FormPortletButtonField("")).setValue(UP_BUTTON_TEXT).setLeftPosPx(10).setWidthPx(buttonDimPx)
				.setTopPosPx(topButton.getTopPosPx() + topButton.getHeightPx() + buttonSpacingPx).setHeightPx(buttonDimPx);
		downButton = (FormPortletButtonField) leftButtonPortlet.addField(new FormPortletButtonField("")).setValue(DOWN_BUTTON_TEXT).setLeftPosPx(10).setWidthPx(buttonDimPx)
				.setTopPosPx(upButton.getTopPosPx() + upButton.getHeightPx() + buttonSpacingPx).setHeightPx(buttonDimPx);
		bottomButton = leftButtonPortlet.addField(new FormPortletButtonField("")).setValue(BOTTOM_BUTTON_TEXT).setLeftPosPx(10).setWidthPx(buttonDimPx)
				.setTopPosPx(downButton.getTopPosPx() + downButton.getHeightPx() + buttonSpacingPx).setHeightPx(buttonDimPx);
		leftButtonPortlet.getFormPortletStyle().setLabelsWidth(4);
		leftButtonPortlet.addMenuListener(this);

	}
	private void populateField() {
		int firstColumn = fastColumns instanceof FastWebTree ? 1 : 0;
		for (int i = firstColumn; i < fastColumns.getVisibleColumnsCount() + firstColumn; i++) {
			FastWebColumn col = fastColumns.getVisibleColumn(i);
			visibleColumnsField.addOption(col.getColumnId().toString(), col.getColumnName(), col.getIsGrouping() ? "_fm=bold" : null);
		}
		for (int i = 0; i < fastColumns.getHiddenColumnsCount(); i++) {
			FastWebColumn col = fastColumns.getHiddenColumn(i);
			hiddenColumnsField.addOption(col.getColumnId().toString(), col.getColumnName(), col.getIsGrouping() ? "_fm=bold" : null);
		}
		hiddenColumnsField.sortOptionsByName();
		for (Option<String> option : visibleColumnsField.getOptions()) {
			sortedOptions.add(option.getKey(), option);
		}
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (this.close.equals(button)) {
			this.close();
		} else if (this.apply.equals(button)) {
			if (fastColumns.getVisibleColumnsLimit() != -1) {
				boolean userReducedVisibleCols = this.visibleColumnsField.getOptionKeys().size() < fastColumns.getVisibleColumnsCount();
				boolean isLimitExceeded = this.visibleColumnsField.getOptionKeys().size() > fastColumns.getVisibleColumnsLimit();
				if (!userReducedVisibleCols && isLimitExceeded) {
					int extraVisibleCols = this.visibleColumnsField.getOptionKeys().size() - fastColumns.getVisibleColumnsLimit();
					getManager().showAlert(
							"Only " + fastColumns.getVisibleColumnsLimit() + " columns are set to be visible. Please remove " + extraVisibleCols + " visible column(s).");
					return;
				}
			}
			applyChanges();
			this.close();
		}
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.searchField) {
			String val = SH.trim(searchField.getValue());
			if (SH.isnt(val)) {
				this.visibleColumnsField.setValue(Collections.EMPTY_SET);
				this.hiddenColumnsField.setValue(Collections.EMPTY_SET);
				this.searchFieldText.setValue("");
			} else {
				Set<String> keys = new HashSet<String>();
				for (Option<String> option : this.visibleColumnsField.getOptions())
					if (SH.indexOfIgnoreCase(option.getName(), val, 0) != -1)
						keys.add(option.getKey());
				this.visibleColumnsField.setValue(keys);
				if (keys.size() > 0)
					this.visibleColumnsField.ensureSelectedVisible();
				int count = keys.size();
				keys = new HashSet<String>();
				for (Option<String> option : this.hiddenColumnsField.getOptions())
					if (SH.indexOfIgnoreCase(option.getName(), val, 0) != -1)
						keys.add(option.getKey());
				this.hiddenColumnsField.setValue(keys);
				if (keys.size() > 0)
					this.hiddenColumnsField.ensureSelectedVisible();
				count += keys.size();
				this.searchFieldText.setValue(count + " FOUND");
			}
		}
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		String id = (String) node.getValue();
		if (UP_BUTTON_TEXT.equals(id)) {
			IndexedList<String, Option<String>> options = new BasicIndexedList<String, Option<String>>();
			int size = sortedOptions.getSize();
			if (size == 1) {
				return;
			}
			Option<String> a = null;
			Option<String> b = null;
			boolean flagOrderChanged = false;
			for (int i = 0; i < size - 1; i++) {
				if (!flagOrderChanged) {
					a = sortedOptions.getAt(i);
				}
				b = sortedOptions.getAt(i + 1);
				if (visibleColumnsField.getSelectedValueKeys().contains(a.getKey())) {
					options.add(a.getKey(), a);
					flagOrderChanged = false;
				} else if (visibleColumnsField.getSelectedValueKeys().contains(b.getKey())) {
					options.add(b.getKey(), b);
					flagOrderChanged = true;
				} else {
					options.add(a.getKey(), a);
					flagOrderChanged = false;
				}
				if (i + 1 == size - 1)
					if (flagOrderChanged)
						options.add(a.getKey(), a);
					else
						options.add(b.getKey(), b);
			}
			sortedOptions = options;
			Set<String> selelectedKeys = new HashSet<String>(visibleColumnsField.getValue());
			visibleColumnsField.clear();
			for (int i = 0; i < sortedOptions.getSize(); i++) {
				visibleColumnsField.addOption(sortedOptions.getAt(i));
			}
			visibleColumnsField.setValue(selelectedKeys);
		} else if (DOWN_BUTTON_TEXT.equals(id)) {
			IndexedList<String, Option<String>> options = new BasicIndexedList<String, Option<String>>();
			int size = sortedOptions.getSize();
			if (size == 1) {
				return;
			}
			Option<String> a = null;
			Option<String> b = null;
			boolean flagOrderChanged = false;
			for (int i = size - 1; i > 0; i--) {
				if (!flagOrderChanged) {
					a = sortedOptions.getAt(i);
				}
				b = sortedOptions.getAt(i - 1);
				if (visibleColumnsField.getSelectedValueKeys().contains(a.getKey())) {
					options.add(a.getKey(), a);
					flagOrderChanged = false;
				} else if (visibleColumnsField.getSelectedValueKeys().contains(b.getKey())) {
					options.add(b.getKey(), b);
					flagOrderChanged = true;
				} else {
					options.add(a.getKey(), a);
					flagOrderChanged = false;
				}
				if (i - 1 == 0)
					if (flagOrderChanged)
						options.add(a.getKey(), a);
					else
						options.add(b.getKey(), b);
			}
			sortedOptions.clear();
			Set<String> selelectedKeys = new HashSet<String>(visibleColumnsField.getValue());
			visibleColumnsField.clear();
			for (int i = options.getSize() - 1; i >= 0; i--) {
				sortedOptions.add(options.getKeyAt(i), options.getAt(i));
				visibleColumnsField.addOption(options.getAt(i));
			}
			visibleColumnsField.setValue(selelectedKeys);
		} else if (RIGHT_BUTTON_TEXT.equals(id)) {
			Set<String> hSelectedKeys = new HashSet<String>(hiddenColumnsField.getValue());
			Set<String> vSelectedKeys = new HashSet<String>(visibleColumnsField.getValue());
			for (String key : vSelectedKeys) {
				hSelectedKeys.add(key);
			}
			for (String key : vSelectedKeys) {
				Option<String> option = visibleColumnsField.getOptionByKey(key);
				visibleColumnsField.removeOptionByKey(key);
				sortedOptions.remove(key);
				hiddenColumnsField.addOption(option);
			}
			hiddenColumnsField.setValue(hSelectedKeys);
		} else if (LEFT_BUTTON_TEXT.equals(id)) {
			Set<String> hSelectedKeys = new HashSet<String>(hiddenColumnsField.getValue());
			Set<String> vSelectedKeys = new HashSet<String>(visibleColumnsField.getValue());
			for (String key : hSelectedKeys) {
				vSelectedKeys.add(key);
			}
			for (String key : hSelectedKeys) {
				Option<String> option = hiddenColumnsField.getOptionByKey(key);
				hiddenColumnsField.removeOptionByKey(key);
				if (fastColumns.getFastWebColumn(key).getIsGrouping()) {
					visibleColumnsField.addOption(0, option);
					sortedOptions.add(option.getKey(), option, 0);
				} else {
					visibleColumnsField.addOption(option);
					sortedOptions.add(option.getKey(), option);
				}
			}
			visibleColumnsField.setValue(vSelectedKeys);
		} else if (TOP_BUTTON_TEXT.equals(id)) {
			int size = sortedOptions.getSize();
			if (size <= 1) {
				return;
			}
			Set<String> selectedValueKeys = visibleColumnsField.getSelectedValueKeys();
			int selectedSize = selectedValueKeys.size();
			if (size == selectedSize) {
				return;
			}

			IndexedList<String, Option<String>> options = new BasicIndexedList<String, Option<String>>();
			// Copy selected options
			for (int i = 0; i < size; i++) {
				Option<String> a = sortedOptions.getAt(i);
				if (selectedValueKeys.contains(a.getKey())) {
					options.add(a.getKey(), a);
				}
			}
			// Copy options that are not selected
			for (int i = 0; i < size; i++) {
				Option<String> a = sortedOptions.getAt(i);
				if (!selectedValueKeys.contains(a.getKey())) {
					options.add(a.getKey(), a);
				}
			}

			sortedOptions = options;
			Set<String> selelectedKeys = new HashSet<String>(visibleColumnsField.getValue());
			visibleColumnsField.clear();
			for (int i = 0; i < sortedOptions.getSize(); i++) {
				visibleColumnsField.addOption(sortedOptions.getAt(i));
			}
			visibleColumnsField.setValue(selelectedKeys);

		} else if (BOTTOM_BUTTON_TEXT.equals(id)) {
			int size = sortedOptions.getSize();
			if (size <= 1) {
				return;
			}
			Set<String> selectedValueKeys = visibleColumnsField.getSelectedValueKeys();
			int selectedSize = selectedValueKeys.size();
			if (size == selectedSize) {
				return;
			}

			IndexedList<String, Option<String>> options = new BasicIndexedList<String, Option<String>>();
			// Copy options that are not selected
			for (int i = 0; i < size; i++) {
				Option<String> a = sortedOptions.getAt(i);
				if (!selectedValueKeys.contains(a.getKey())) {
					options.add(a.getKey(), a);
				}
			}
			// Copy selected options
			for (int i = 0; i < size; i++) {
				Option<String> a = sortedOptions.getAt(i);
				if (selectedValueKeys.contains(a.getKey())) {
					options.add(a.getKey(), a);
				}
			}

			sortedOptions = options;
			Set<String> selelectedKeys = new HashSet<String>(visibleColumnsField.getValue());
			visibleColumnsField.clear();
			for (int i = 0; i < sortedOptions.getSize(); i++) {
				visibleColumnsField.addOption(sortedOptions.getAt(i));
			}
			visibleColumnsField.setValue(selelectedKeys);
		}
	}

	public void hideCloseButtons() {
		this.actionButtonsPortlet.removeButton(this.apply);
		this.actionButtonsPortlet.removeButton(this.close);

	}

	public void setVisibleListTitle(String string) {
		this.visibleTitleField.setValue(string);
	}
	public void setHiddenListTitle(String string) {
		this.hiddenTitleField.setValue(string);
	}

	public void applyChanges() {
		arrange(this.fastColumns, CH.l(this.sortedOptions.keys()));
	}

	public static List<String> getVisibleColumns(FastWebColumns fastColumns) {
		int firstColumn = fastColumns instanceof FastWebTree ? 1 : 0;
		int cnt = fastColumns.getVisibleColumnsCount() + firstColumn;
		List<String> columnIds = new ArrayList<String>();
		// collect all visible columns
		for (int i = firstColumn; i < cnt; i++) {
			FastWebColumn col = fastColumns.getVisibleColumn(i);
			columnIds.add(col.getColumnId().toString());
		}
		return columnIds;
	}

	public static void arrange(FastWebColumns fastColumns, List<String> sortedOptions) {
		int pinnedColumns = fastColumns.getPinnedColumnsCount();
		Object lastPinnedColumnId = null;
		if (pinnedColumns > 0)
			lastPinnedColumnId = fastColumns.getVisibleColumn(pinnedColumns - 1).getColumnId();

		Set<Object> oldPinned = new HashSet<Object>();
		for (int i = 0; i < pinnedColumns; i++)
			oldPinned.add(fastColumns.getVisibleColumn(i).getColumnId());
		fastColumns.setPinnedColumnsCount(0);
		// collect all visible columns
		List<String> columnIds = getVisibleColumns(fastColumns);
		// if current visible columns overlap the param columns, exit
		if (CH.areSame(columnIds.iterator(), sortedOptions.iterator()))
			return;
		// at this point, there is at least one column that we need to show but aren't showing
		// instead of figuring out the delta between the two, we brute force to ensure only the param
		// columns are showing:
		// hide the current columns
		for (int i = 0; i < columnIds.size(); i++)
			fastColumns.hideColumn(columnIds.get(i));
		int i = 0;
		// show all the param columns
		for (String option : sortedOptions) {
			fastColumns.showColumn(option, i);
			i++;
		}
		for (pinnedColumns = 0; pinnedColumns < fastColumns.getVisibleColumnsCount(); pinnedColumns++) {
			if (!oldPinned.contains(fastColumns.getVisibleColumn(pinnedColumns).getColumnId()))
				break;
		}

		if (pinnedColumns > 0 && lastPinnedColumnId != null) {
			int newPcc = fastColumns.getColumnPosition(lastPinnedColumnId);
			fastColumns.setPinnedColumnsCount(newPcc + 1);
		} else
			fastColumns.setPinnedColumnsCount(pinnedColumns);
		fastColumns.fireOnColumnsArranged();
	}
	public ArrangeColumnsPortlet setFormStyle(PortletStyleManager_Form styleManager) {
		if (this.formStyle.isUseDefaultStyling())
			return this;
		this.searchFormPortlet.setStyle(styleManager);
		this.leftButtonPortlet.setStyle(styleManager);
		this.arrangeButtonsPortlet.setStyle(styleManager);
		this.rightArrangePortlet.setStyle(styleManager);
		this.actionButtonsPortlet.setStyle(styleManager);
		this.visibleColumnsPortlet.setStyle(styleManager);
		this.hiddenRowsPortlet.setStyle(styleManager);
		this.arrangeButtonsPortlet.getFormPortletStyle().setLabelsWidth(5);
		this.rightArrangePortlet.getFormPortletStyle().setLabelsWidth(5);
		this.visibleColumnsPortlet.getFormPortletStyle().setLabelsWidth(1);
		this.hiddenRowsPortlet.getFormPortletStyle().setLabelsWidth(4);
		return this;
	}
}
