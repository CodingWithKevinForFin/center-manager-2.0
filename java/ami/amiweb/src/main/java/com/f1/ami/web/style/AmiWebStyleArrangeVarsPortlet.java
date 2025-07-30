package com.f1.ami.web.style;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.CH;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.IndexedList;

public class AmiWebStyleArrangeVarsPortlet extends GridPortlet implements FormPortletListener, FormPortletContextMenuListener {

	private List<String> rowList;
	private AmiWebStyleVarPortlet parent;
	private static final String TOP_BUTTON_TEXT = "&#11245;";
	private static final String BOTTOM_BUTTON_TEXT = "&#11247;";
	private static final String UP_BUTTON_TEXT = "&#129033;";
	private static final String DOWN_BUTTON_TEXT = "&#129035;";
	private FormPortletButton close;
	private FormPortlet varsPortlet;
	private FormPortletMultiSelectField<String> varsField;
	private FormPortlet arrangeButtonsPortlet;
	private IndexedList<String, Option<String>> sortedOptions;
	private FormPortletButton apply;
	private FormPortlet leftButtonPortlet;
	private FormPortlet rightArragePortlet;
	private FormPortlet actionButtonsPortlet;
	private FormPortletTitleField varsTitleField;

	public AmiWebStyleArrangeVarsPortlet(PortletConfig config, AmiWebStyleVarPortlet parent, List<String> rowList) {
		super(config);
		this.parent = parent;
		this.rowList = rowList;
		sortedOptions = new BasicIndexedList<String, Option<String>>();
		prepareFormPortlets();
		prepareGrid();
	}

	private void prepareGrid() {
		this.addChild(leftButtonPortlet, 0, 0);
		this.addChild(varsPortlet, 1, 0);
		this.addChild(actionButtonsPortlet, 0, 1, 2, 1);
		this.setColSize(0, 45);
		this.setRowSize(1, 50);
		setSize(200, 800);
		int maxHeight = getManager().getRoot().getHeight();
		int cnt2 = 15 * (5 + this.varsField.getOptionKeys().size());
		int selectHeight = cnt2 + 100;
		setSuggestedSize(480, Math.max(350, Math.min(selectHeight, maxHeight - 100)));
	}

	private void prepareFormPortlets() {
		this.varsPortlet = new FormPortlet(generateConfig());
		this.varsTitleField = varsPortlet.addField(new FormPortletTitleField("Variables"));
		this.varsField = varsPortlet.addField(new FormPortletMultiSelectField<String>(String.class, ""));
		this.varsField.setLeftPosPx(1);
		this.varsField.setRightPosPx(0);
		this.varsField.setTopPosPx(35);
		this.varsField.setBottomPosPx(10);
		varsPortlet.getFormPortletStyle().setLabelsWidth(1);

		this.arrangeButtonsPortlet = new FormPortlet(generateConfig());
		arrangeButtonsPortlet.getFormPortletStyle().setLabelsWidth(5);
		arrangeButtonsPortlet.addField(new FormPortletTitleField(""));
		arrangeButtonsPortlet.addField(new FormPortletTitleField(""));
		arrangeButtonsPortlet.addField(new FormPortletTitleField(""));
		arrangeButtonsPortlet.addField(new FormPortletTitleField(""));
		arrangeButtonsPortlet.addFormPortletListener(this);
		arrangeButtonsPortlet.addMenuListener(this);
		populateField();

		//		Are these needed?
		this.rightArragePortlet = new FormPortlet(generateConfig());
		rightArragePortlet.addField(new FormPortletTitleField(""));
		rightArragePortlet.addField(new FormPortletTitleField(""));
		rightArragePortlet.addField(new FormPortletTitleField(""));
		rightArragePortlet.addField(new FormPortletTitleField(""));
		rightArragePortlet.addMenuListener(this);
		rightArragePortlet.addFormPortletListener(this);
		rightArragePortlet.getFormPortletStyle().setLabelsWidth(5);

		this.actionButtonsPortlet = new FormPortlet(generateConfig());
		this.apply = actionButtonsPortlet.addButton(new FormPortletButton("Apply"));
		this.close = actionButtonsPortlet.addButton(new FormPortletButton("Cancel"));
		this.actionButtonsPortlet.addFormPortletListener(this);

		int buttonSpacingPx = 25;
		int buttonDimPx = 25;
		this.leftButtonPortlet = new FormPortlet(generateConfig());
		FormPortletButtonField topButton = (FormPortletButtonField) leftButtonPortlet.addField(new FormPortletButtonField("")).setValue(TOP_BUTTON_TEXT).setLeftPosPx(10)
				.setWidthPx(buttonDimPx).setTopPosPx(50).setHeightPx(buttonDimPx);
		FormPortletButtonField upButton = (FormPortletButtonField) leftButtonPortlet.addField(new FormPortletButtonField("")).setValue(UP_BUTTON_TEXT).setLeftPosPx(10)
				.setWidthPx(buttonDimPx).setTopPosPx(topButton.getTopPosPx() + topButton.getHeightPx() + buttonSpacingPx).setHeightPx(buttonDimPx);
		FormPortletButtonField downButton = (FormPortletButtonField) leftButtonPortlet.addField(new FormPortletButtonField("")).setValue(DOWN_BUTTON_TEXT).setLeftPosPx(10)
				.setWidthPx(buttonDimPx).setTopPosPx(upButton.getTopPosPx() + upButton.getHeightPx() + buttonSpacingPx).setHeightPx(buttonDimPx);
		leftButtonPortlet.addField(new FormPortletButtonField("")).setValue(BOTTOM_BUTTON_TEXT).setLeftPosPx(10).setWidthPx(buttonDimPx)
				.setTopPosPx(downButton.getTopPosPx() + downButton.getHeightPx() + buttonSpacingPx).setHeightPx(buttonDimPx);
		leftButtonPortlet.getFormPortletStyle().setLabelsWidth(4);

		leftButtonPortlet.addMenuListener(this);
	}

	private void populateField() {
		for (int i = 0; i < this.rowList.size(); i++)
			varsField.addOption(Integer.toString(i), this.rowList.get(i));
		for (Option<String> option : varsField.getOptions())
			sortedOptions.add(option.getKey(), option);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (this.close.equals(button)) {
			this.close();
		} else if (this.apply.equals(button)) {
			applyChanges();
			this.close();
		}
	}

	public void applyChanges() {
		this.parent.sortRows(CH.l(this.sortedOptions.keys()));
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {

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
				if (varsField.getSelectedValueKeys().contains(a.getKey())) {
					options.add(a.getKey(), a);
					flagOrderChanged = false;
				} else if (varsField.getSelectedValueKeys().contains(b.getKey())) {
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
			Set<String> selelectedKeys = new HashSet<String>(varsField.getValue());
			varsField.clear();
			for (int i = 0; i < sortedOptions.getSize(); i++) {
				varsField.addOption(sortedOptions.getAt(i));
			}
			varsField.setValue(selelectedKeys);
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
				if (varsField.getSelectedValueKeys().contains(a.getKey())) {
					options.add(a.getKey(), a);
					flagOrderChanged = false;
				} else if (varsField.getSelectedValueKeys().contains(b.getKey())) {
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
			Set<String> selelectedKeys = new HashSet<String>(varsField.getValue());
			varsField.clear();
			for (int i = options.getSize() - 1; i >= 0; i--) {
				sortedOptions.add(options.getKeyAt(i), options.getAt(i));
				varsField.addOption(options.getAt(i));
			}
			varsField.setValue(selelectedKeys);
		} else if (TOP_BUTTON_TEXT.equals(id)) {
			int size = sortedOptions.getSize();
			if (size <= 1) {
				return;
			}
			Set<String> selectedValueKeys = varsField.getSelectedValueKeys();
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
			Set<String> selelectedKeys = new HashSet<String>(varsField.getValue());
			varsField.clear();
			for (int i = 0; i < sortedOptions.getSize(); i++) {
				varsField.addOption(sortedOptions.getAt(i));
			}
			varsField.setValue(selelectedKeys);

		} else if (BOTTOM_BUTTON_TEXT.equals(id)) {
			int size = sortedOptions.getSize();
			if (size <= 1) {
				return;
			}
			Set<String> selectedValueKeys = varsField.getSelectedValueKeys();
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
			Set<String> selelectedKeys = new HashSet<String>(varsField.getValue());
			varsField.clear();
			for (int i = 0; i < sortedOptions.getSize(); i++) {
				varsField.addOption(sortedOptions.getAt(i));
			}
			varsField.setValue(selelectedKeys);
		}
	}

}
