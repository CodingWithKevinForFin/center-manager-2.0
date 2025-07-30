package com.f1.ami.web.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletMultiSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;

public class AmiWebArrangeCustomContextMenuItemsPortlet extends GridPortlet implements FormPortletListener, FormPortletContextMenuListener {

	private static final String TOP_BUTTON_TEXT = "&#x23EB;";
	private static final String BOTTOM_BUTTON_TEXT = "&#x23EC;";
	private static final String UP_BUTTON_TEXT = "&#x25B2;";
	private static final String DOWN_BUTTON_TEXT = "&#x25BC;";
	private final FormPortletButtonField topButton = new FormPortletButtonField("");
	private final FormPortletButtonField bottomButton = new FormPortletButtonField("");
	private final FormPortletButtonField upButton = new FormPortletButtonField("");
	private final FormPortletButtonField downButton = new FormPortletButtonField("");

	private final AmiWebCustomContextMenuManager menu;
	private final List<String> ids;
	private final FormPortlet buttonsForm;
	private final FormPortlet listForm;
	private final FormPortlet bottomButtonsForm;
	private final FormPortletMultiSelectField<String> itemsField = new FormPortletMultiSelectField<String>(String.class, "");

	private final FormPortletButton applyButton = new FormPortletButton("Apply");
	private final FormPortletButton cancelButton = new FormPortletButton("Cancel");
	private AmiWebCustomContextMenu parent;

	public AmiWebArrangeCustomContextMenuItemsPortlet(PortletConfig config, AmiWebCustomContextMenuManager menu, AmiWebCustomContextMenu parent) {
		super(config);
		this.menu = menu;
		this.ids = new ArrayList<String>(parent.getChildrenCount());
		for (int i = 0; i < parent.getChildrenCount(); i++)
			ids.add(parent.getChildItemAt(i).getId());
		this.parent = parent;

		// Build buttons portlet
		this.buttonsForm = new FormPortlet(generateConfig());
		int buttonDimPx = 25;
		int buttonSpacingPx = 25;
		int buttonsFormWidth = 60;
		int buttonsFormHeight = 300;
		this.buttonsForm.addField(this.topButton);
		this.buttonsForm.addField(this.upButton);
		this.buttonsForm.addField(this.downButton);
		this.buttonsForm.addField(this.bottomButton);
		this.buttonsForm.setSize(buttonsFormWidth, buttonsFormHeight);
		this.topButton.setValue(TOP_BUTTON_TEXT);
		this.topButton.setTopPosPx(buttonSpacingPx + 20);
		this.topButton.setWidthPx(buttonDimPx);
		this.topButton.setHeightPx(buttonDimPx);
		this.topButton.centerHorizontally();
		this.upButton.setValue(UP_BUTTON_TEXT);
		this.upButton.setTopPosPx(this.topButton.getTopPosPx() + buttonDimPx + buttonSpacingPx);
		this.upButton.setWidthPx(buttonDimPx);
		this.upButton.setHeightPx(buttonDimPx);
		this.upButton.centerHorizontally();
		this.downButton.setValue(DOWN_BUTTON_TEXT);
		this.downButton.setTopPosPx(this.upButton.getTopPosPx() + buttonDimPx + buttonSpacingPx);
		this.downButton.setWidthPx(buttonDimPx);
		this.downButton.setHeightPx(buttonDimPx);
		this.downButton.centerHorizontally();
		this.bottomButton.setValue(BOTTOM_BUTTON_TEXT);
		this.bottomButton.setTopPosPx(this.downButton.getTopPosPx() + buttonDimPx + buttonSpacingPx);
		this.bottomButton.setWidthPx(buttonDimPx);
		this.bottomButton.setHeightPx(buttonDimPx);
		this.bottomButton.centerHorizontally();

		// Build multiselect list portlet
		this.listForm = new FormPortlet(generateConfig());
		FormPortletTitleField itemsTitleField = new FormPortletTitleField("Menu Items");
		itemsTitleField.setLeftPosPx(0);
		itemsTitleField.setTopPosPx(5);
		itemsTitleField.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		itemsTitleField.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		this.listForm.addField(itemsTitleField);
		this.listForm.addField(this.itemsField);
		this.itemsField.setTopPosPx(30);
		this.itemsField.setBottomPosPx(20);
		this.itemsField.setLeftPosPx(0);
		this.itemsField.setRightPosPx(20);
		for (int i = 0; i < ids.size(); i++) {
			AmiWebCustomContextMenu childItem = this.menu.getMenu(ids.get(i));
			this.itemsField.addOption(childItem.getId(), childItem.getId());
		}

		// Build bottom buttons portlet
		this.bottomButtonsForm = new FormPortlet(generateConfig());
		this.bottomButtonsForm.addButton(this.applyButton);
		this.bottomButtonsForm.addButton(this.cancelButton);

		addChild(this.buttonsForm, 0, 0);
		addChild(this.listForm, 1, 0);
		addChild(this.bottomButtonsForm, 0, 1, 2, 1);

		setColSize(0, buttonsFormWidth);
		setRowSize(1, 45);
		setSuggestedSize(400, buttonsFormHeight);

		this.buttonsForm.addMenuListener(this);
		this.listForm.addFormPortletListener(this);
		this.bottomButtonsForm.addFormPortletListener(this);
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		if (node == this.upButton) {
			this.itemsField.moveSelectedUp();
		} else if (node == this.downButton) {
			this.itemsField.moveSelectedDown();
		} else if (node == this.topButton) {
			this.itemsField.moveSelectedTop();
		} else if (node == this.bottomButton) {
			this.itemsField.moveSelectedBottom();
		}
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.applyButton) {
			List<String> reorderedIds = new ArrayList<String>();
			for (int i = 0; i < this.itemsField.getSize(); i++) {
				reorderedIds.add(this.itemsField.getOptionAt(i).getKey());
			}
			this.parent.reorderChildren(reorderedIds);

		}
		close();
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {

	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}
}
