package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collections;
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
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.IndexedList;

public class AmiWebArrangeTabsEndUserPortlet extends GridPortlet implements FormPortletListener, FormPortletContextMenuListener, AmiWebLockedPermissiblePortlet {

	private AmiWebTabPortlet tabs;
	private static final String TOP_BUTTON_TEXT = "&#11245;";
	private static final String BOTTOM_BUTTON_TEXT = "&#11247;";
	private static final String UP_BUTTON_TEXT = "&#129033;";
	private static final String DOWN_BUTTON_TEXT = "&#129035;";
	private FormPortletButton close;
	private FormPortlet visibleTabsPortlet;
	private FormPortletMultiSelectField<String> visibleTabsField;
	private FormPortlet arrangeButtonsPortlet;
	private IndexedList<String, Option<String>> sortedOptions;
	private FormPortletButton apply;
	private FormPortlet leftButtonPortlet;
	private FormPortlet rightArragePortlet;
	private FormPortlet actionButtonsPortlet;
	private FormPortletTitleField visibleTitleField;
	private FormPortletTitleField hiddenTitleField;
	private FormPortlet searchFormPortlet;
	private FormPortletTextField searchField;
	private FormPortletTextField searchFieldText;

	public AmiWebArrangeTabsEndUserPortlet(PortletConfig config, AmiWebTabPortlet tabs, AmiWebTabEntry selectTab) {
		super(config);
		this.tabs = tabs;
		sortedOptions = new BasicIndexedList<String, Option<String>>();
		prepareFormPortlets();
		if (selectTab != null) {
			this.visibleTabsField.setValueNoThrow(CH.s(selectTab.getId()));
		}
		prepareGrid();
	}
	public AmiWebArrangeTabsEndUserPortlet(PortletConfig config, AmiWebTabPortlet tabs) {
		this(config, tabs, null);
	}

	public AmiWebTabPortlet getFastWebTab() {
		return this.tabs;
	}

	private void prepareGrid() {
		this.searchFormPortlet = new FormPortlet(generateConfig());
		this.searchField = new FormPortletTextField("search");
		this.searchField.setTitle("Search:");
		searchField.setTopPosPx(10).setRightPosPx(85).setHeightPx(20).setWidthPx(200);
		this.searchFieldText = new FormPortletTextField("");
		this.searchFieldText.setTitle("");
		this.searchFieldText.setDisabled(true);
		this.searchFieldText.setCssStyle("_bg=white|_br=0px solid white");
		searchFieldText.setTopPosPx(10).setRightPosPx(20).setHeightPx(20).setWidthPx(65);
		searchFormPortlet.addField(searchFieldText);
		searchFormPortlet.addField(searchField);
		searchFormPortlet.addFormPortletListener(this);
		this.addChild(searchFormPortlet, 0, 0, 2, 1);
		this.addChild(leftButtonPortlet, 0, 1);
		this.addChild(visibleTabsPortlet, 1, 1);
		//		this.addChild(arrangeButtonsPortlet, 2, 1);
		this.addChild(actionButtonsPortlet, 0, 2, 2, 1);
		this.setColSize(0, 45);
		//		this.setColSize(2, 60);
		this.setRowSize(0, 40);
		this.setRowSize(2, 50);
		setSize(200, 800);
		int maxHeight = getManager().getRoot().getHeight();
		int cnt2 = 15 * (5 + this.visibleTabsField.getOptionKeys().size());
		int selectHeight = cnt2 + 100;
		setSuggestedSize(480, Math.max(350, Math.min(selectHeight, maxHeight - 100)));
	}

	private void prepareFormPortlets() {
		this.visibleTabsPortlet = new FormPortlet(generateConfig());
		this.visibleTitleField = visibleTabsPortlet.addField(new FormPortletTitleField("Visible Tabs"));
		this.visibleTabsField = visibleTabsPortlet.addField(new FormPortletMultiSelectField<String>(String.class, ""));
		this.visibleTabsField.setLeftPosPx(1);
		this.visibleTabsField.setRightPosPx(0);
		this.visibleTabsField.setTopPosPx(35);
		this.visibleTabsField.setBottomPosPx(10);
		visibleTabsPortlet.getFormPortletStyle().setLabelsWidth(1);

		this.arrangeButtonsPortlet = new FormPortlet(generateConfig());
		arrangeButtonsPortlet.getFormPortletStyle().setLabelsWidth(5);
		arrangeButtonsPortlet.addField(new FormPortletTitleField(""));
		arrangeButtonsPortlet.addField(new FormPortletTitleField(""));
		arrangeButtonsPortlet.addField(new FormPortletTitleField(""));
		arrangeButtonsPortlet.addField(new FormPortletTitleField(""));
		arrangeButtonsPortlet.addFormPortletListener(this);
		arrangeButtonsPortlet.addMenuListener(this);
		populateField();

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
		boolean inEdit = this.tabs.getService().getDesktop().getInEditMode();
		for (int i = 0; i < tabs.getTabsCount(); i++) {
			AmiWebTabEntry tab = tabs.getTabAt(i);
			if (tab.getHidden(true)) {
				if (inEdit) {
					visibleTabsField.addOption(tab.getId(), tab.getTab().getTitle() + " (hidden)", "_fm=italic");
				} else
					continue;
			} else
				visibleTabsField.addOption(tab.getId(), tab.getTab().getTitle());
		}
		for (Option<String> option : visibleTabsField.getOptions()) {
			sortedOptions.add(option.getKey(), option);
		}
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

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.searchField) {
			String val = SH.trim(searchField.getValue());
			if (SH.isnt(val)) {
				this.visibleTabsField.setValue(Collections.EMPTY_SET);
				this.searchFieldText.setValue("");
			} else {
				Set<String> keys = new HashSet<String>();
				for (Option<String> option : this.visibleTabsField.getOptions())
					if (SH.indexOfIgnoreCase(option.getName(), val, 0) != -1)
						keys.add(option.getKey());
				this.visibleTabsField.setValue(keys);
				if (keys.size() > 0)
					this.visibleTabsField.ensureSelectedVisible();
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
				if (visibleTabsField.getSelectedValueKeys().contains(a.getKey())) {
					options.add(a.getKey(), a);
					flagOrderChanged = false;
				} else if (visibleTabsField.getSelectedValueKeys().contains(b.getKey())) {
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
			Set<String> selelectedKeys = new HashSet<String>(visibleTabsField.getValue());
			visibleTabsField.clear();
			for (int i = 0; i < sortedOptions.getSize(); i++) {
				visibleTabsField.addOption(sortedOptions.getAt(i));
			}
			visibleTabsField.setValue(selelectedKeys);
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
				if (visibleTabsField.getSelectedValueKeys().contains(a.getKey())) {
					options.add(a.getKey(), a);
					flagOrderChanged = false;
				} else if (visibleTabsField.getSelectedValueKeys().contains(b.getKey())) {
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
			Set<String> selelectedKeys = new HashSet<String>(visibleTabsField.getValue());
			visibleTabsField.clear();
			for (int i = options.getSize() - 1; i >= 0; i--) {
				sortedOptions.add(options.getKeyAt(i), options.getAt(i));
				visibleTabsField.addOption(options.getAt(i));
			}
			visibleTabsField.setValue(selelectedKeys);
		} else if (TOP_BUTTON_TEXT.equals(id)) {
			int size = sortedOptions.getSize();
			if (size <= 1) {
				return;
			}
			Set<String> selectedValueKeys = visibleTabsField.getSelectedValueKeys();
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
			Set<String> selelectedKeys = new HashSet<String>(visibleTabsField.getValue());
			visibleTabsField.clear();
			for (int i = 0; i < sortedOptions.getSize(); i++) {
				visibleTabsField.addOption(sortedOptions.getAt(i));
			}
			visibleTabsField.setValue(selelectedKeys);

		} else if (BOTTOM_BUTTON_TEXT.equals(id)) {
			int size = sortedOptions.getSize();
			if (size <= 1) {
				return;
			}
			Set<String> selectedValueKeys = visibleTabsField.getSelectedValueKeys();
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
			Set<String> selelectedKeys = new HashSet<String>(visibleTabsField.getValue());
			visibleTabsField.clear();
			for (int i = 0; i < sortedOptions.getSize(); i++) {
				visibleTabsField.addOption(sortedOptions.getAt(i));
			}
			visibleTabsField.setValue(selelectedKeys);
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
		arrange(this.tabs, CH.l(this.sortedOptions.keys()));
	}

	public List<String> getVisibleTabs(AmiWebTabPortlet tabs) {
		boolean inEdit = this.tabs.getService().getDesktop().getInEditMode();
		List<String> tabIds = new ArrayList<String>();
		for (int i = 0; i < tabs.getTabsCount(); i++) {
			AmiWebTabEntry col = tabs.getTabAt(i);
			if (!inEdit && col.getHidden(true))
				continue;
			tabIds.add(col.getId());
		}
		return tabIds;
	}

	public void arrange(AmiWebTabPortlet tabs, List<String> sortedOptions) {

		AmiWebTabEntry st = tabs.getSelectedTab();
		List<String> tabIds = getVisibleTabs(tabs);
		if (CH.areSame(tabIds.iterator(), sortedOptions.iterator()))
			return;
		int i = 0;
		boolean inEdit = this.tabs.getService().getDesktop().getInEditMode();
		List<String> all;
		if (!inEdit) {
			all = new ArrayList<String>(tabs.getTabsCount());
			for (int pos = 0; pos < tabs.getTabsCount(); pos++) {
				if (tabs.getTabAt(pos).getTab().isHidden())
					all.add(tabs.getTabAt(pos).getId());
				else {
					all.add(sortedOptions.get(i++));
				}
			}
		} else {
			all = sortedOptions;
		}
		i = 0;
		for (String s : all) {
			AmiWebTabEntry tab = tabs.getTabById(s);
			tab.setLocation(i++, true);
		}
		tabs.onTabLocationChanged(true);
		tabs.setSelectedTab(st);
	}
	public AmiWebArrangeTabsEndUserPortlet setFormStyle(PortletStyleManager_Form styleManager) {
		this.searchFormPortlet.setStyle(styleManager);
		this.leftButtonPortlet.setStyle(styleManager);
		this.arrangeButtonsPortlet.setStyle(styleManager);
		this.rightArragePortlet.setStyle(styleManager);
		this.actionButtonsPortlet.setStyle(styleManager);
		this.visibleTabsPortlet.setStyle(styleManager);
		this.arrangeButtonsPortlet.getFormPortletStyle().setLabelsWidth(5);
		this.rightArragePortlet.getFormPortletStyle().setLabelsWidth(5);
		this.visibleTabsPortlet.getFormPortletStyle().setLabelsWidth(1);
		return this;
	}
}
