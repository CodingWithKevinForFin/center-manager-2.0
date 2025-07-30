package com.f1.ami.web.form.field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.web.AmiWebEditAmiScriptCallbacksPortlet;
import com.f1.ami.web.AmiWebEditStylePortlet;
import com.f1.ami.web.AmiWebFormPortletAmiScriptField;
import com.f1.ami.web.AmiWebMenuUtils;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.form.AmiWebEditableFormPortletManager;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.AmiWebQueryFormPortletUtils;
import com.f1.ami.web.form.factory.AmiWebFormFieldFactory;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.RootPortletDialog;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletTextEditField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.suite.web.portal.impl.form.WebAbsoluteLocation;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;

public abstract class BaseEditFieldPortlet<T extends QueryField<?>> extends TabPortlet
		implements FormPortletListener, FormPortletContextMenuFactory, FormPortletContextMenuListener {

	private static final Logger log = LH.get();

	final protected FormPortletTextField nameField;
	final protected FormPortletTextField labelField;
	final protected AmiWebQueryFormPortlet queryFormPortlet;
	final protected FormPortletTextField dmExpressionField;
	final private FormPortletNumericRangeField leftPosPxField;
	final private FormPortletNumericRangeField rightPosPxField;
	final private FormPortletNumericRangeField topPosPxField;
	final private FormPortletNumericRangeField bottomPosPxField;
	final private FormPortletNumericRangeField leftPosPctField;
	final private FormPortletNumericRangeField rightPosPctField;
	final private FormPortletNumericRangeField topPosPctField;
	final private FormPortletNumericRangeField bottomPosPctField;
	final private FormPortletNumericRangeField widthPxField;
	final private FormPortletNumericRangeField heightPxField;
	final private FormPortletNumericRangeField widthPctField;
	final private FormPortletNumericRangeField heightPctField;
	final private FormPortletNumericRangeField offsetFromCenterHorizontalPctField;
	final private FormPortletNumericRangeField offsetFromCenterVerticalPctField;
	final private FormPortletToggleButtonsField<Byte> horizSettingsToggle;
	final private FormPortletToggleButtonsField<Byte> vertSettingsToggle;
	final protected static byte FIELD_ALIGN_LEFT_TOP = 0;
	final protected static byte FIELD_ALIGN_RIGHT_BOTTOM = 1;
	final protected static byte FIELD_ALIGN_CENTER = 2;
	final protected static byte FIELD_ALIGN_OUTER = 3;
	final protected static byte FIELD_ALIGN_ADVANCED = 4;
	final private Map<Byte, FormPortletNumericRangeField> horizPosFields;
	final private Map<Byte, FormPortletNumericRangeField> vertPosFields;
	final private static byte START_POS_PX = 0;
	final private static byte END_POS_PX = 1;
	final private static byte SIZE_PX = 2;
	final private static byte START_POS_PCT = 3;
	final private static byte END_POS_PCT = 4;
	final private static byte SIZE_PCT = 5;
	final private static byte CENTER_OFFSET_PCT = 6;
	protected String editId;
	protected T queryField;
	private AmiWebService service;
	private int queryFormWidth;
	private int queryFormHeight;
	private final FormPortletTitleField vertSettingsTitle;
	private final FormPortletTitleField overdefinedWarningHPx;
	private final FormPortletTitleField overdefinedWarningHPct;
	private final FormPortletTitleField overdefinedWarningVPx;
	private final FormPortletTitleField overdefinedWarningVPct;
	private final FormPortletTitleField underdefinedWarningH;
	private final FormPortletTitleField underdefinedWarningV;
	private final FormPortletCheckboxField vertStretchCheckbox;
	private final FormPortletCheckboxField horizStretchCheckbox;
	private final FormPortletTitleField tallBlankBackgroundTitle;
	private final FormPortletToggleButtonsField<Boolean> disableToggle;
	private static final int VERTICAL_POS_FIELD_SPACING_PX = 6;
	protected static final int COL1_HORIZONTAL_POS_PX = 164;
	protected static final int COL2_HORIZONTAL_POS_PX = 700;
	private static final int VERTICAL_SETTINGS_TITLE_TOP_POS_PX = 130;
	private static final int VERTICAL_SETTINGS_TOGGLE_TOP_POS_PX = VERTICAL_SETTINGS_TITLE_TOP_POS_PX + FormPortletField.DEFAULT_HEIGHT + 15;
	private static final int VERTICAL_SETTINGS_CHECKBOX_TOP_POS_PX = VERTICAL_SETTINGS_TOGGLE_TOP_POS_PX + 26 + 5;
	private static final int POS_SLIDER_TOP_POS1 = VERTICAL_SETTINGS_CHECKBOX_TOP_POS_PX + FormPortletField.DEFAULT_HEIGHT + VERTICAL_POS_FIELD_SPACING_PX + 7;
	private final List<FormPortletField<?>> requiredFields = new ArrayList<FormPortletField<?>>();
	private final AmiWebEditableFormPortletManager editableManager;
	private final FormPortlet settingsForm;
	private final AmiWebEditStylePortlet styleForm;
	private final FormPortlet helpForm;

	private final AmiWebFormPortletAmiScriptField helpField;
	private final Tab amiScripTab;
	private final AmiWebEditAmiScriptCallbacksPortlet amiScriptForm;
	private final AmiWebFormFieldFactory<T> factory;

	private boolean isTransient;

	private FormPortletToggleButtonsField<Boolean> visibilityToggle;

	public BaseEditFieldPortlet(AmiWebFormFieldFactory<T> factory, AmiWebQueryFormPortlet queryFormPortlet, int fieldX, int fieldY) {
		super(queryFormPortlet.generateConfig());
		this.service = queryFormPortlet.getService();
		this.factory = factory;
		this.settingsForm = new FormPortlet(generateConfig());
		if (!service.getScriptManager().getCallbackDefinitions(factory.getClassType()).isEmpty()) {
			this.amiScriptForm = new AmiWebEditAmiScriptCallbacksPortlet(generateConfig(), null);
		} else {
			this.amiScriptForm = null;
		}

		this.styleForm = new AmiWebEditStylePortlet(null, generateConfig(), factory.getStyleType().getName());
		this.styleForm.hideButtonsForm(true);
		this.helpForm = new FormPortlet(generateConfig());
		this.settingsForm.setMenuFactory(this);
		this.settingsForm.addMenuListener(this);
		this.queryFormPortlet = queryFormPortlet;
		this.editableManager = this.queryFormPortlet.getEditableForm().getEditableManager();
		setTitle("Query Form");
		this.labelField = this.settingsForm.addField(new FormPortletTextField("Label: "));
		this.nameField = this.settingsForm.addField(new FormPortletTextField("Variable Name: "));
		this.nameField.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.nameField.setHeightPx(25);
		this.nameField.setLeftPosPx(COL2_HORIZONTAL_POS_PX);
		this.nameField.setTopPosPx(6);
		this.dmExpressionField = this.settingsForm.addField(new FormPortletTextField("Default Value: "));
		this.dmExpressionField.setWidth(FormPortletField.WIDTH_STRETCH);
		this.dmExpressionField.setHasButton(true);
		this.disableToggle = this.settingsForm.addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Field Status:"));
		this.disableToggle.addOption(false, "Enabled");
		this.disableToggle.addOption(true, "Disabled");
		this.disableToggle.setHelp("A Disabled field is not interactable to the user.");
		this.visibilityToggle = this.settingsForm.addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Visibility:"));
		this.visibilityToggle.addOption(true, "Visible");
		this.visibilityToggle.addOption(false, "Hidden");
		this.visibilityToggle.setDefaultValue(true);
		this.visibilityToggle.setHelp("If set to Visible, use horizontal/vertical settings below to configure the absolute position of the field.");

		// Add a tall, blank title field to the background. This will ensure that 
		// any field-specific options appear below the horizontal settings and do 
		// not move as the number of horizontal settings changes. 
		this.settingsForm.addField(new FormPortletTitleField("Horizontal Settings"));
		this.tallBlankBackgroundTitle = this.settingsForm.addField(new FormPortletTitleField(""));
		this.tallBlankBackgroundTitle.setHeightPx(120);

		// Horizontal Settings
		this.horizSettingsToggle = this.settingsForm.addField(new FormPortletToggleButtonsField<Byte>(Byte.class, "Alignment:"));
		this.horizSettingsToggle.addOption(FIELD_ALIGN_LEFT_TOP, "Left");
		this.horizSettingsToggle.addOption(FIELD_ALIGN_CENTER, "Center");
		this.horizSettingsToggle.addOption(FIELD_ALIGN_RIGHT_BOTTOM, "Right");
		this.horizSettingsToggle.addOption(FIELD_ALIGN_OUTER, "Justify");
		this.horizSettingsToggle.addOption(FIELD_ALIGN_ADVANCED, "Advanced");
		this.horizSettingsToggle.setWidthPx(400);
		this.horizSettingsToggle.setHeightPx(25);
		this.horizSettingsToggle.setLeftPosPx(COL1_HORIZONTAL_POS_PX);
		this.horizSettingsToggle.setTopPosPx(VERTICAL_SETTINGS_TOGGLE_TOP_POS_PX);

		this.horizStretchCheckbox = this.settingsForm.addField(new FormPortletCheckboxField("Stretch:"));
		this.horizStretchCheckbox.setLeftPosPx(COL1_HORIZONTAL_POS_PX);
		this.horizStretchCheckbox.setTopPosPx(VERTICAL_SETTINGS_CHECKBOX_TOP_POS_PX);

		this.queryFormWidth = queryFormPortlet.getWidth();
		this.queryFormHeight = queryFormPortlet.getHeight();
		this.widthPxField = this.settingsForm.addField(new FormPortletNumericRangeField("Width (px):", 0, this.queryFormWidth, 0)).setNullable(false);
		this.widthPctField = this.settingsForm.addField(new FormPortletNumericRangeField("Width (%):", 0, 100, 0)).setNullable(false);
		this.leftPosPxField = this.settingsForm.addField(new FormPortletNumericRangeField("Left Position (px):", -this.queryFormWidth, this.queryFormWidth, 0)).setNullable(false);
		this.leftPosPctField = this.settingsForm.addField(new FormPortletNumericRangeField("Left Position (%):", 0, 100, 0)).setNullable(false);
		this.rightPosPxField = this.settingsForm.addField(new FormPortletNumericRangeField("Right Position (px):", -this.queryFormWidth, this.queryFormWidth, 0))
				.setNullable(false);
		this.rightPosPctField = this.settingsForm.addField(new FormPortletNumericRangeField("Right Position (%):", 0, 100, 0)).setNullable(false);
		this.offsetFromCenterHorizontalPctField = this.settingsForm.addField(new FormPortletNumericRangeField("Offset From Center (%):", -50, 50, 0)).setNullable(false);

		// Vertical Settings
		this.vertSettingsTitle = this.settingsForm.addField(new FormPortletTitleField("Vertical Settings"));
		this.vertStretchCheckbox = this.settingsForm.addField(new FormPortletCheckboxField("Stretch:"));
		this.vertSettingsToggle = this.settingsForm.addField(new FormPortletToggleButtonsField<Byte>(Byte.class, "Alignment:"));
		this.vertSettingsToggle.addOption(FIELD_ALIGN_LEFT_TOP, "Top");
		this.vertSettingsToggle.addOption(FIELD_ALIGN_CENTER, "Center");
		this.vertSettingsToggle.addOption(FIELD_ALIGN_RIGHT_BOTTOM, "Bottom");
		this.vertSettingsToggle.addOption(FIELD_ALIGN_OUTER, "Justify");
		this.vertSettingsToggle.addOption(FIELD_ALIGN_ADVANCED, "Advanced");
		this.heightPxField = this.settingsForm.addField(new FormPortletNumericRangeField("Height (px):", 0, this.queryFormHeight, 0)).setNullable(false);
		this.heightPctField = this.settingsForm.addField(new FormPortletNumericRangeField("Height (%):", 0, 100, 0)).setNullable(false);
		this.topPosPxField = this.settingsForm.addField(new FormPortletNumericRangeField("Top Position (px):", -this.queryFormHeight, this.queryFormHeight, 0)).setNullable(false);
		this.topPosPctField = this.settingsForm.addField(new FormPortletNumericRangeField("Top Position (%):", 0, 100, 0)).setNullable(false);
		this.bottomPosPxField = this.settingsForm.addField(new FormPortletNumericRangeField("Bottom Position (px):", -this.queryFormHeight, this.queryFormHeight, 0))
				.setNullable(false);
		this.bottomPosPctField = this.settingsForm.addField(new FormPortletNumericRangeField("Bottom Position (%):", 0, 100, 0)).setNullable(false);
		this.offsetFromCenterVerticalPctField = this.settingsForm.addField(new FormPortletNumericRangeField("Offset From Center (%):", -50, 50, 0)).setNullable(false);

		this.vertSettingsTitle.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.vertSettingsTitle.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		this.vertSettingsTitle.setLeftPosPx(COL2_HORIZONTAL_POS_PX);
		this.vertSettingsTitle.setTopPosPx(VERTICAL_SETTINGS_TITLE_TOP_POS_PX);
		this.vertStretchCheckbox.setLeftPosPx(COL2_HORIZONTAL_POS_PX);
		this.vertStretchCheckbox.setTopPosPx(VERTICAL_SETTINGS_CHECKBOX_TOP_POS_PX);
		this.vertSettingsToggle.setWidthPx(400);
		this.vertSettingsToggle.setHeightPx(25);
		this.vertSettingsToggle.setLeftPosPx(COL2_HORIZONTAL_POS_PX);
		this.vertSettingsToggle.setTopPosPx(VERTICAL_SETTINGS_TOGGLE_TOP_POS_PX);
		// TODO do these warning fields work?
		this.underdefinedWarningH = this.settingsForm.addField(new FormPortletTitleField("Under-defined"));
		this.underdefinedWarningV = this.settingsForm.addField(new FormPortletTitleField("Under-defined"));
		this.overdefinedWarningHPx = this.settingsForm.addField(new FormPortletTitleField("Over-defined"));
		this.overdefinedWarningHPct = this.settingsForm.addField(new FormPortletTitleField("Over-defined"));
		this.overdefinedWarningVPx = this.settingsForm.addField(new FormPortletTitleField("Over-defined"));
		this.overdefinedWarningVPct = this.settingsForm.addField(new FormPortletTitleField("Over-defined"));
		this.overdefinedWarningHPx.setHelp(
				"Horizontal setting (px) is over-defined when width, left position, and right position are all specified. Changing width when horizontal setting is over-defined will have no effect.");
		this.overdefinedWarningHPct.setHelp(
				"Horizontal setting (%) is over-defined when width, left position, and right position are all specified. Changing width when horizontal setting is over-defined will have no effect.");
		this.overdefinedWarningVPx.setHelp(
				"Vertical setting (px) is over-defined when height, top position, and bottom position are all specified. Changing height when vertical setting is over-defined will have no effect.");
		this.overdefinedWarningVPct.setHelp(
				"Vertical setting (%) is over-defined when height, top position, and bottom position are all specified. Changing height when vertical setting is over-defined will have no effect.");
		this.underdefinedWarningH.setHelp(
				"For a field's horizontal position to be defined, two of the following three options must be specified: width, left position, right position. These quantities can each be specified as either percentages of the form's width or as pixel counts. The field's horizontal position will not take effect unless it is fully defined.");
		this.underdefinedWarningV.setHelp(
				"For a field's vertical position to be defined, two of the following three options must be specified: height, top position, bottom position. These quantities can each be specified as either percentages of the form's height or as pixel counts. The field's vertical position will not take effect unless it is fully defined.");
		this.overdefinedWarningHPx.setCssStyle("style.color=red");
		this.overdefinedWarningHPct.setCssStyle("style.color=red");
		this.overdefinedWarningVPx.setCssStyle("style.color=red");
		this.overdefinedWarningVPct.setCssStyle("style.color=red");
		this.underdefinedWarningH.setCssStyle("style.color=blue");
		this.underdefinedWarningV.setCssStyle("style.color=blue");

		// Set dimensions of positioning fields 
		// Horizontal (px)
		this.widthPxField.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.widthPxField.setHeightPx(25);
		this.leftPosPxField.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.leftPosPxField.setHeightPx(25);
		this.rightPosPxField.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.rightPosPxField.setHeightPx(25);
		this.offsetFromCenterHorizontalPctField.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.offsetFromCenterHorizontalPctField.setHeightPx(25);
		// Horizontal (%)
		this.widthPctField.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.widthPctField.setHeightPx(25);
		this.leftPosPctField.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.leftPosPctField.setHeightPx(25);
		this.rightPosPctField.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.rightPosPctField.setHeightPx(25);
		// Vertical (px)
		this.heightPxField.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.heightPxField.setHeightPx(25);
		this.topPosPxField.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.topPosPxField.setHeightPx(25);
		this.bottomPosPxField.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.bottomPosPxField.setHeightPx(25);
		this.offsetFromCenterVerticalPctField.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.offsetFromCenterVerticalPctField.setHeightPx(25);
		// Vertical (%)
		this.heightPctField.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.heightPctField.setHeightPx(25);
		this.topPosPctField.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.topPosPctField.setHeightPx(25);
		this.bottomPosPctField.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.bottomPosPctField.setHeightPx(25);

		{
			this.horizPosFields = new HashMap<Byte, FormPortletNumericRangeField>();

			this.horizPosFields.put(START_POS_PX, this.leftPosPxField);
			this.horizPosFields.put(END_POS_PX, this.rightPosPxField);
			this.horizPosFields.put(SIZE_PX, this.widthPxField);
			this.horizPosFields.put(START_POS_PCT, this.leftPosPctField);
			this.horizPosFields.put(END_POS_PCT, this.rightPosPctField);
			this.horizPosFields.put(SIZE_PCT, this.widthPctField);
			this.horizPosFields.put(CENTER_OFFSET_PCT, this.offsetFromCenterHorizontalPctField);
		}
		{
			this.vertPosFields = new HashMap<Byte, FormPortletNumericRangeField>();

			this.vertPosFields.put(START_POS_PX, this.topPosPxField);
			this.vertPosFields.put(END_POS_PX, this.bottomPosPxField);
			this.vertPosFields.put(SIZE_PX, this.heightPxField);
			this.vertPosFields.put(START_POS_PCT, this.topPosPctField);
			this.vertPosFields.put(END_POS_PCT, this.bottomPosPctField);
			this.vertPosFields.put(SIZE_PCT, this.heightPctField);
			this.vertPosFields.put(CENTER_OFFSET_PCT, this.offsetFromCenterVerticalPctField);
		}

		// Position overdefined warning fields
		int verticalOverdefinedWarningFieldsHorizontalPosPx = 940;
		int horizontalOverdefinedWarningFieldsHorizontalPosPx = 400;

		this.overdefinedWarningHPx.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.overdefinedWarningHPx.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		this.overdefinedWarningHPx.setLeftPosPx(horizontalOverdefinedWarningFieldsHorizontalPosPx);
		this.overdefinedWarningHPx.setTopPosPx(this.heightPxField.getTopPosPx() + 5);

		this.overdefinedWarningHPct.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.overdefinedWarningHPct.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		this.overdefinedWarningHPct.setLeftPosPx(horizontalOverdefinedWarningFieldsHorizontalPosPx);
		this.overdefinedWarningHPct.setTopPosPx(this.overdefinedWarningHPx.getTopPosPx() + 25 + VERTICAL_POS_FIELD_SPACING_PX);

		this.overdefinedWarningVPx.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.overdefinedWarningVPx.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		this.overdefinedWarningVPx.setLeftPosPx(verticalOverdefinedWarningFieldsHorizontalPosPx);
		this.overdefinedWarningVPx.setTopPosPx(this.heightPxField.getTopPosPx() + 5);

		this.overdefinedWarningVPct.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.overdefinedWarningVPct.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		this.overdefinedWarningVPct.setLeftPosPx(verticalOverdefinedWarningFieldsHorizontalPosPx);
		this.overdefinedWarningVPct.setTopPosPx(this.overdefinedWarningVPx.getTopPosPx() + 25 + VERTICAL_POS_FIELD_SPACING_PX);

		this.underdefinedWarningH.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.underdefinedWarningH.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		this.underdefinedWarningH.setLeftPosPx(horizontalOverdefinedWarningFieldsHorizontalPosPx);
		this.underdefinedWarningH.setTopPosPx(this.heightPxField.getTopPosPx() + 5);

		this.underdefinedWarningV.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.underdefinedWarningV.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		this.underdefinedWarningV.setLeftPosPx(verticalOverdefinedWarningFieldsHorizontalPosPx);
		this.underdefinedWarningV.setTopPosPx(this.heightPxField.getTopPosPx() + 5);

		this.widthPxField.setLeftPosPx(COL1_HORIZONTAL_POS_PX);
		this.leftPosPxField.setLeftPosPx(COL1_HORIZONTAL_POS_PX);
		this.rightPosPxField.setLeftPosPx(COL1_HORIZONTAL_POS_PX);
		this.offsetFromCenterHorizontalPctField.setLeftPosPx(COL1_HORIZONTAL_POS_PX);
		this.widthPctField.setLeftPosPx(COL1_HORIZONTAL_POS_PX);
		this.leftPosPctField.setLeftPosPx(COL1_HORIZONTAL_POS_PX);
		this.rightPosPctField.setLeftPosPx(COL1_HORIZONTAL_POS_PX);

		this.heightPxField.setLeftPosPx(COL2_HORIZONTAL_POS_PX);
		this.topPosPxField.setLeftPosPx(COL2_HORIZONTAL_POS_PX);
		this.bottomPosPxField.setLeftPosPx(COL2_HORIZONTAL_POS_PX);
		this.offsetFromCenterVerticalPctField.setLeftPosPx(COL2_HORIZONTAL_POS_PX);
		this.heightPctField.setLeftPosPx(COL2_HORIZONTAL_POS_PX);
		this.topPosPctField.setLeftPosPx(COL2_HORIZONTAL_POS_PX);
		this.bottomPosPctField.setLeftPosPx(COL2_HORIZONTAL_POS_PX);

		if (fieldX == -1 && fieldY == -1) {
			this.leftPosPxField.setValue(FormPortletField.DEFAULT_LEFT_POS_PX);
			this.topPosPxField.setValue(FormPortletField.DEFAULT_PADDING_PX);
		} else {
			this.leftPosPxField.setValue(fieldX);
			this.topPosPxField.setValue(fieldY);
		}

		this.helpField = this.helpForm.addField(new AmiWebFormPortletAmiScriptField("", getManager(), queryFormPortlet.getAmiLayoutFullAlias()));
		this.helpField.setLabelHidden(true);
		this.helpField.setBottomPosPx(10);
		this.helpField.setLeftPosPx(10);
		this.helpField.setRightPosPx(10);
		this.helpField.setTopPosPx(10);

		updateWarningsFields();
		this.settingsForm.addFormPortletListener(this);
		this.settingsForm.setMenuFactory(this);
		this.helpForm.setMenuFactory(this);
		this.helpForm.addMenuListener(this);
		setIsCustomizable(false);
		addChild("Settings", this.settingsForm);
		if (this.amiScriptForm != null)
			this.amiScripTab = addChild("Ami Script", this.amiScriptForm);
		else
			this.amiScripTab = null;
		addChild("Field Style", this.styleForm);
		addChild("Help", this.helpForm);
		this.nameField.setValue(SH.getNextId(AmiUtils.toValidVarName(SH.replaceAll(SH.toLowerCase(factory.getUserLabel()), ' ', "")), this.queryFormPortlet.getQueryFieldNames()));
		this.labelField.setValue(SH.getNextId(factory.getUserLabel(), this.queryFormPortlet.getQueryFieldLabels()));
	}
	public Tab getAmiScriptTab() {
		return this.amiScripTab;
	}

	public AmiWebEditAmiScriptCallbacksPortlet getCallbacksEditor() {
		return this.amiScriptForm;
	}
	public void setFieldDimensions(int width, int height) {
		this.widthPxField.setValue(width);
		this.heightPxField.setValue(height);
		this.writeCommonToField(this.queryField);
	}

	public void readCommonFromField(QueryField<?> queryField) {
		if (this.amiScriptForm != null)
			this.amiScriptForm.setCallbacks(queryField.getAmiScriptCallbacks());
		FormPortletField<?> field = queryField.getField();
		this.horizStretchCheckbox.setValue(getInitialHorizontalStretchSetting(queryField));
		this.vertStretchCheckbox.setValue(getInitialVerticalStretchSetting(queryField));

		Byte hToggleValue = queryField != null && queryField.isHorizontalPosDefined() ? getInitialHorizontalPosSetting(queryField) : FIELD_ALIGN_LEFT_TOP;
		this.horizSettingsToggle.setValue(hToggleValue);
		Byte vToggleValue = queryField != null && queryField.isVerticalPosDefined() ? getInitialVerticalPosSetting(queryField) : FIELD_ALIGN_LEFT_TOP;
		this.vertSettingsToggle.setValue(vToggleValue);

		this.vertStretchCheckbox.setVisible(!vToggleValue.equals(FIELD_ALIGN_OUTER) && !vToggleValue.equals(FIELD_ALIGN_ADVANCED));
		this.horizStretchCheckbox.setVisible(!hToggleValue.equals(FIELD_ALIGN_OUTER) && !hToggleValue.equals(FIELD_ALIGN_ADVANCED));
		updateHorizontalPositionFields();
		updateVerticalPositionFields();
		this.nameField.setValue(queryField.getName());
		this.helpField.setValue(queryField.getHelp(false));
		this.labelField.setValue(queryField.getLabel(false));
		this.dmExpressionField.setValue(queryField.getDefaultValueFormula().getFormula(false));

		// set pixels
		if (WebAbsoluteLocation.is(queryField.getWidthPx()))
			this.widthPxField.setValue(queryField.getWidthPx());
		if (WebAbsoluteLocation.is(queryField.getHeightPx()))
			this.heightPxField.setValue(queryField.getHeightPx());
		if (WebAbsoluteLocation.is(queryField.getLeftPosPx()))
			this.leftPosPxField.setValue(queryField.getLeftPosPx());
		if (WebAbsoluteLocation.is(queryField.getRightPosPx()))
			this.rightPosPxField.setValue(queryField.getRightPosPx());
		if (WebAbsoluteLocation.is(queryField.getTopPosPx()))
			this.topPosPxField.setValue(queryField.getTopPosPx());
		if (WebAbsoluteLocation.is(queryField.getBottomPosPx()))
			this.bottomPosPxField.setValue(queryField.getBottomPosPx());

		// set percent
		if (WebAbsoluteLocation.is(queryField.getWidthPct()))
			this.widthPctField.setValue(queryField.getWidthPct() * 100);
		if (WebAbsoluteLocation.is(queryField.getHeightPct()))
			this.heightPctField.setValue(queryField.getHeightPct() * 100);
		if (WebAbsoluteLocation.is(queryField.getLeftPosPct()))
			this.leftPosPctField.setValue(queryField.getLeftPosPct() * 100);
		if (WebAbsoluteLocation.is(queryField.getRightPosPct()))
			this.rightPosPctField.setValue(queryField.getRightPosPct() * 100);
		if (WebAbsoluteLocation.is(queryField.getTopPosPct()))
			this.topPosPctField.setValue(queryField.getTopPosPct() * 100);
		if (WebAbsoluteLocation.is(queryField.getBottomPosPct()))
			this.bottomPosPctField.setValue(queryField.getBottomPosPct() * 100);
		if (WebAbsoluteLocation.is(queryField.getHorizontalOffsetFromCenterPct()))
			this.offsetFromCenterHorizontalPctField.setValue(queryField.getHorizontalOffsetFromCenterPct() * 100);
		if (WebAbsoluteLocation.is(queryField.getVerticalOffsetFromCenterPct()))
			this.offsetFromCenterVerticalPctField.setValue(queryField.getVerticalOffsetFromCenterPct() * 100);

		this.editId = field.getId();
		this.disableToggle.setValue(queryField.getDisabled());
		this.visibilityToggle.setValue(field.isVisible());
		this.isTransient = queryField.isTransient();
		showPositioningFields();
	}
	private void repositionHorizontalSettingsFields(byte setting) {
		repositionSettingsFields(setting, false);
	}
	private void repositionVerticalSettingsFields(byte setting) {
		repositionSettingsFields(setting, true);
	}
	private void repositionSettingsFields(byte setting, boolean isVertical) {
		Map<Byte, FormPortletNumericRangeField> fieldsMap = isVertical ? this.vertPosFields : this.horizPosFields;
		boolean stretch = isVertical ? this.vertStretchCheckbox.getBooleanValue() : this.horizStretchCheckbox.getBooleanValue();
		FormPortletNumericRangeField startPosPxField = fieldsMap.get(START_POS_PX);
		FormPortletNumericRangeField endPosPxField = fieldsMap.get(END_POS_PX);
		FormPortletNumericRangeField sizePxField = fieldsMap.get(SIZE_PX);
		FormPortletNumericRangeField startPosPctField = fieldsMap.get(START_POS_PCT);
		FormPortletNumericRangeField endPosPctField = fieldsMap.get(END_POS_PCT);
		FormPortletNumericRangeField sizePctField = fieldsMap.get(SIZE_PCT);
		FormPortletNumericRangeField centerOffsetPctField = fieldsMap.get(CENTER_OFFSET_PCT);
		if (!this.visibilityToggle.getValue().booleanValue()) { // Include in HTML -> Hidden
			sizePxField.setTopPosPx(VERTICAL_SETTINGS_TOGGLE_TOP_POS_PX);
		} else {
			switch (setting) {
				case FIELD_ALIGN_LEFT_TOP:
					if (stretch) { // Stretch mode
						startPosPxField.setTopPosPx(POS_SLIDER_TOP_POS1);
						endPosPctField.setTopPosPx(POS_SLIDER_TOP_POS1 + startPosPxField.getHeightPx() + VERTICAL_POS_FIELD_SPACING_PX);
					} else {
						sizePxField.setTopPosPx(POS_SLIDER_TOP_POS1);
						startPosPxField.setTopPosPx(POS_SLIDER_TOP_POS1 + sizePxField.getHeightPx() + VERTICAL_POS_FIELD_SPACING_PX);
					}
					break;
				case FIELD_ALIGN_RIGHT_BOTTOM:
					if (stretch) { // Stretch mode
						endPosPxField.setTopPosPx(POS_SLIDER_TOP_POS1);
						startPosPctField.setTopPosPx(POS_SLIDER_TOP_POS1 + endPosPxField.getHeightPx() + VERTICAL_POS_FIELD_SPACING_PX);
					} else {
						sizePxField.setTopPosPx(POS_SLIDER_TOP_POS1);
						endPosPxField.setTopPosPx(POS_SLIDER_TOP_POS1 + sizePxField.getHeightPx() + VERTICAL_POS_FIELD_SPACING_PX);
					}
					break;
				case FIELD_ALIGN_CENTER:
					if (stretch) { // Stretch mode
						startPosPctField.setTopPosPx(POS_SLIDER_TOP_POS1);
						endPosPctField.setTopPosPx(POS_SLIDER_TOP_POS1 + startPosPctField.getHeightPx() + VERTICAL_POS_FIELD_SPACING_PX);
					} else {
						sizePxField.setTopPosPx(POS_SLIDER_TOP_POS1);
						centerOffsetPctField.setTopPosPx(POS_SLIDER_TOP_POS1 + sizePxField.getHeightPx() + VERTICAL_POS_FIELD_SPACING_PX);
					}
					break;
				case FIELD_ALIGN_OUTER:
					startPosPxField.setTopPosPx(this.horizSettingsToggle.getTopPosPx() + this.horizSettingsToggle.getHeightPx() + VERTICAL_POS_FIELD_SPACING_PX + 7);
					endPosPxField.setTopPosPx(startPosPxField.getTopPosPx() + startPosPxField.getHeightPx() + VERTICAL_POS_FIELD_SPACING_PX);
					break;
				case FIELD_ALIGN_ADVANCED:
					sizePxField.setTopPosPx(this.horizSettingsToggle.getTopPosPx() + this.horizSettingsToggle.getHeightPx() + VERTICAL_POS_FIELD_SPACING_PX + 7);
					sizePctField.setTopPosPx(sizePxField.getTopPosPx() + sizePxField.getHeightPx() + VERTICAL_POS_FIELD_SPACING_PX);
					startPosPxField.setTopPosPx(sizePctField.getTopPosPx() + sizePctField.getHeightPx() + VERTICAL_POS_FIELD_SPACING_PX);
					startPosPctField.setTopPosPx(startPosPxField.getTopPosPx() + startPosPxField.getHeightPx() + VERTICAL_POS_FIELD_SPACING_PX);
					endPosPxField.setTopPosPx(startPosPctField.getTopPosPx() + startPosPctField.getHeightPx() + VERTICAL_POS_FIELD_SPACING_PX);
					endPosPctField.setTopPosPx(endPosPxField.getTopPosPx() + endPosPxField.getHeightPx() + VERTICAL_POS_FIELD_SPACING_PX);
					break;
			}
		}
	}
	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 1200;
	}
	public int getSuggestedHeight(PortletMetrics pm) {
		return 680;
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {

	}
	public boolean submit() {
		if (!verifyForSubmit())
			return false;
		if (this.nameField.getValue() == null || "".equals(nameField.getValue())) {
			this.nameField.setValue(SH.getNextId(SH.lowercaseFirstChar(SH.toCamelHumps(" ", AmiUtils.toValidVarName(this.labelField.getValue()), false)),
					this.queryFormPortlet.getQueryFieldNames(), 2));
		}
		Object value = this.queryField.getValue();
		this.writeCommonToField(this.queryField);
		this.writeToField(this.queryField);
		queryField.setTransient(this.isTransient);
		if (value != null) {
			try {
				queryField.setValue(value);
				queryField.getForm().putPortletVar(queryField.getName(), queryField.getValue(), queryField.getValueType());
			} catch (Exception e) { // might wanna change this message here...
				LH.warning(log, "TODO: must add a setValueNoThrow() to the base field", e);
			}
		}
		if (SH.is(queryField.getDefaultValueFormula().getFormula(false)))
			queryField.getForm().resetField(new StringBuilder(), queryField);
		return true;
	}
	public boolean verifyForSubmit() {
		if (this.amiScriptForm != null && !this.amiScriptForm.applyTo(this.queryField.getAmiScriptCallbacks(), null)) {
			this.setActiveTab(this.amiScripTab.getPortlet());
			return false;
		}
		String expression = this.dmExpressionField.getValue();
		if (SH.is(expression)) {
			StringBuilder errorSink = new StringBuilder();
			AmiWebQueryFormPortlet form = this.getField().getForm();
			com.f1.base.CalcTypes classTypes = AmiWebUtils.getAvailableVariables(this.service, form);
			// can change param to catch the exception here to set up for better error messages
			DerivedCellCalculator calc = form.getScriptManager().parseAmiScript(expression, classTypes, errorSink, this.service.getDebugManager(), AmiDebugMessage.TYPE_TEST,
					this.queryField, QueryField.FORMULA_DEFAULT_VALUE, false, null);
			if (calc == null) {
				getManager().showAlert("Invalid Default Value: " + errorSink);
				return false;
			}
		}
		// Prevent over- or under-definition
		if (isUnderdefined(false)) {
			getManager().showAlert("Horizontal field position is under-defined. Please fully specify position.");
			return false;
		} else if (isUnderdefined(true)) {
			getManager().showAlert("Vertical field position is under-defined. Please fully specify position.");
			return false;
		} else if (isOverdefined(false)) {
			getManager().showAlert("Horizontal field position is over-defined. Please specify fewer position parameters.");
			return false;
		} else if (isOverdefined(true)) {
			getManager().showAlert("Vertical field position is over-defined. Please specify fewer position parameters.");
			return false;
		}
		List<FormPortletField<?>> missingFields = getMissingRequiredFields();
		if (missingFields.size() != 0) {
			getManager().showAlert("Missing required field: <B>" + SH.beforeLast(missingFields.get(0).getTitle(), ":"));
			return false;
		}
		QueryField<?> q;
		for (FormPortletField<?> f : this.queryFormPortlet.getEditableForm().getFormFields()) {
			q = AmiWebQueryFormPortlet.getQueryField(f);
			if (this.queryField != null && f != this.queryField.getField() && q.getName().equals(this.nameField.getValue())) {
				getManager().showAlert("Variable name already in use: <B>" + this.nameField.getValue() + "</B>");
				return false;
			}
		}
		return true;
	}

	abstract public void readFromField(T queryField);
	abstract public void writeToField(T queryField);
	public void writeCommonToField(QueryField<?> queryField) {
		if (this.amiScriptForm != null) {
			this.amiScriptForm.exportTo(queryField.getAmiScriptCallbacks(), null);
		}

		queryField.getDefaultValueFormula().setFormula(this.getDmExpression(), false);
		queryField.setVarName(this.nameField.getValue());
		queryField.setHelp(this.helpField.getValue());
		queryField.setLabel(this.labelField.getValue());
		applyPositionAndDimensionsSingleAxis(queryField, false); // Horizontal Settings
		applyPositionAndDimensionsSingleAxis(queryField, true); // Vertical Settings

		AmiWebQueryFormPortletUtils.copyPositioningToQueryField(queryField.getField(), queryField);
		queryField.setDisabled(this.disableToggle.getValue().booleanValue());
		queryField.setVisible(this.visibilityToggle.getValue().booleanValue());
	}
	private void applyPositionAndDimensionsSingleAxis(QueryField<?> field, boolean isVertical) {
		if (field.getField().getForm() == null)
			return;
		Byte toggleValue = isVertical ? this.vertSettingsToggle.getValue() : this.horizSettingsToggle.getValue();
		Map<Byte, FormPortletNumericRangeField> fieldsMap = isVertical ? this.vertPosFields : this.horizPosFields;
		boolean stretch = isVertical ? this.vertStretchCheckbox.getBooleanValue() : this.horizStretchCheckbox.getBooleanValue();
		FormPortletNumericRangeField startPosPxField = fieldsMap.get(START_POS_PX);
		FormPortletNumericRangeField endPosPxField = fieldsMap.get(END_POS_PX);
		FormPortletNumericRangeField sizePxField = fieldsMap.get(SIZE_PX);
		FormPortletNumericRangeField startPosPctField = fieldsMap.get(START_POS_PCT);
		FormPortletNumericRangeField endPosPctField = fieldsMap.get(END_POS_PCT);
		FormPortletNumericRangeField sizePctField = fieldsMap.get(SIZE_PCT);
		FormPortletNumericRangeField centerOffsetPctField = fieldsMap.get(CENTER_OFFSET_PCT);
		WebAbsoluteLocation absLocation = isVertical ? field.getAbsLocationV() : field.getAbsLocationH();

		// Ensure field outer cache size is defined
		field.updateFormSize();

		switch (toggleValue) {
			case FIELD_ALIGN_LEFT_TOP:
				if (stretch) {
					absLocation.setStartPx(startPosPxField.getIntValue());
					absLocation.setEndPct(endPosPctField.getValue() / 100d);
				} else {
					absLocation.setStartPx(startPosPxField.getIntValue());
					absLocation.setSizePx(sizePxField.getIntValue());
				}
				break;
			case FIELD_ALIGN_RIGHT_BOTTOM:
				if (stretch) {
					absLocation.setEndPx(endPosPxField.getIntValue());
					absLocation.setStartPct(startPosPctField.getValue() / 100d);
				} else {
					absLocation.setEndPx(endPosPxField.getIntValue());
					absLocation.setSizePx(sizePxField.getIntValue());
				}
				break;
			case FIELD_ALIGN_CENTER:
				if (stretch) {
					absLocation.setStartPct(startPosPctField.getValue() / 100d);
					absLocation.setEndPct(endPosPctField.getValue() / 100d);
				} else {
					absLocation.setSizePx(sizePxField.getIntValue());
					absLocation.center();
					absLocation.setOffsetFromCenterPct(centerOffsetPctField.getValue() / 100d);
				}
				break;
			case FIELD_ALIGN_OUTER:
				absLocation.setStartPx(startPosPxField.getIntValue());
				absLocation.setEndPx(endPosPxField.getIntValue());
				break;
			case FIELD_ALIGN_ADVANCED:
				Integer startPosPx = startPosPxField.getIntValue();
				if (startPosPx != null)
					absLocation.setStartPx(startPosPx);
				Integer endPosPx = endPosPxField.getIntValue();
				if (endPosPx != null)
					absLocation.setEndPx(endPosPx);
				Integer sizePx = sizePxField.getIntValue();
				if (sizePx != null)
					absLocation.setSizePx(sizePx);
				Double startPosPct = startPosPctField.getValue();
				if (startPosPct != null)
					absLocation.setStartPct(startPosPct / 100d);
				Double endPosPct = endPosPctField.getValue();
				if (endPosPct != null)
					absLocation.setEndPct(endPosPct / 100d);
				Double sizePct = sizePctField.getValue();
				if (sizePct != null)
					absLocation.setSizePct(sizePct / 100d);
				break;
		}
	}
	private byte getInitialHorizontalPosSetting(QueryField<?> field) {
		if (field == null)
			return FIELD_ALIGN_LEFT_TOP;
		return fieldAlign2ToggleSetting(field.getHorizontalPosAlignment());
	}
	private byte getInitialVerticalPosSetting(QueryField<?> field) {
		if (field == null)
			return FIELD_ALIGN_LEFT_TOP;
		return fieldAlign2ToggleSetting(field.getVerticalPosAlignment());
	}
	private static byte fieldAlign2ToggleSetting(byte alignment) {
		if (alignment == WebAbsoluteLocation.ALIGN_START_LOCK || alignment == WebAbsoluteLocation.ALIGN_START_STRETCH) {
			return FIELD_ALIGN_LEFT_TOP;
		} else if (alignment == WebAbsoluteLocation.ALIGN_END_LOCK || alignment == WebAbsoluteLocation.ALIGN_END_STRETCH) {
			return FIELD_ALIGN_RIGHT_BOTTOM;
		} else if (alignment == WebAbsoluteLocation.ALIGN_CENTER_LOCK || alignment == WebAbsoluteLocation.ALIGN_CENTER_STRETCH) {
			return FIELD_ALIGN_CENTER;
		} else if (alignment == WebAbsoluteLocation.ALIGN_OUTER_STRETCH) {
			return FIELD_ALIGN_OUTER;
		} else {
			return FIELD_ALIGN_ADVANCED;
		}
	}
	private static byte toggleAndCheckboxSetting2FieldAlign(byte toggleValue, boolean checkboxValue) {
		switch (toggleValue) {
			case FIELD_ALIGN_LEFT_TOP:
				return checkboxValue ? WebAbsoluteLocation.ALIGN_START_STRETCH : WebAbsoluteLocation.ALIGN_START_LOCK;
			case FIELD_ALIGN_RIGHT_BOTTOM:
				return checkboxValue ? WebAbsoluteLocation.ALIGN_END_STRETCH : WebAbsoluteLocation.ALIGN_END_LOCK;
			case FIELD_ALIGN_CENTER:
				return checkboxValue ? WebAbsoluteLocation.ALIGN_CENTER_STRETCH : WebAbsoluteLocation.ALIGN_CENTER_LOCK;
			case FIELD_ALIGN_OUTER:
				return WebAbsoluteLocation.ALIGN_OUTER_STRETCH;
			case FIELD_ALIGN_ADVANCED:
				return WebAbsoluteLocation.ALIGN_ADVANCED;
			default:
				return WebAbsoluteLocation.ALIGN_START_LOCK;
		}
	}
	private boolean getInitialHorizontalStretchSetting(QueryField<?> field) {
		if (field == null)
			return false;
		return fieldAlign2StretchSetting(field.getHorizontalPosAlignment());
	}
	private boolean getInitialVerticalStretchSetting(QueryField<?> field) {
		if (field == null)
			return false;
		return fieldAlign2StretchSetting(field.getVerticalPosAlignment());
	}
	private boolean fieldAlign2StretchSetting(byte alignment) {
		return alignment == WebAbsoluteLocation.ALIGN_START_STRETCH || alignment == WebAbsoluteLocation.ALIGN_END_STRETCH || alignment == WebAbsoluteLocation.ALIGN_CENTER_STRETCH
				|| alignment == WebAbsoluteLocation.ALIGN_OUTER_STRETCH;
	}

	final public T createField() {
		T queryField = this.factory.createQueryField(this.queryFormPortlet);
		//		writeToField(queryField);
		writeCommonToField(queryField);
		if (this.editId == null) {
			queryFormPortlet.addQueryField(queryField, true);
			queryField.addToDomManager();
		} else {

			QueryField<?> removeField = queryFormPortlet.getFieldsById().get(this.editId);
			QueryField<?> newField = queryField;
			newField = queryFormPortlet.replaceQueryField(removeField.getId(), newField);

			removeField.removeFromDomManager();
			newField.addToDomManager();
		}
		return (T) queryField;
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (this.queryField == null) {
			return;
		}
		if (this.queryField.getField() != null) {
			if (field == this.labelField) {
				queryField.setLabel(this.labelField.getValue());
			}
			// Dimensions (px)
			else if (field == this.widthPxField) {
				if (!this.visibilityToggle.getValue().booleanValue()) {
					this.queryField.setRealizedWidthPx(toPx(this.widthPxField.getIntValue()));
				} else {
					this.queryField.setWidthPx(toPx(this.widthPxField.getIntValue()));
					if (this.horizSettingsToggle.getValue().equals(FIELD_ALIGN_CENTER)) {
						this.queryField.setHorizontalOffsetFromCenterPct(this.offsetFromCenterHorizontalPctField.getValue() / 100d);
					}
				}
			} else if (field == this.heightPxField) {
				if (!this.visibilityToggle.getValue().booleanValue()) {
					this.queryField.setRealizedHeightPx(toPx(this.heightPxField.getIntValue()));
				} else {
					this.queryField.setHeightPx(toPx(this.heightPxField.getIntValue()));
					if (this.vertSettingsToggle.getValue().equals(FIELD_ALIGN_CENTER)) {
						this.queryField.setVerticalOffsetFromCenterPct(this.offsetFromCenterVerticalPctField.getValue() / 100d);
					}
				}
			}
			// Dimensions (%)
			else if (field == this.widthPctField)
				this.queryField.setWidthPct(toPct(this.widthPctField.getValue()));
			else if (field == this.heightPctField)
				this.queryField.setHeightPct(toPct(this.heightPctField.getValue()));
			// Position (px)
			else if (field == this.leftPosPxField)
				this.queryField.setLeftPosPx(toPx(this.leftPosPxField.getIntValue()));
			else if (field == this.rightPosPxField)
				this.queryField.setRightPosPx(toPx(this.rightPosPxField.getIntValue()));
			else if (field == this.topPosPxField)
				this.queryField.setTopPosPx(toPx(this.topPosPxField.getIntValue()));
			else if (field == this.bottomPosPxField)
				this.queryField.setBottomPosPx(toPx(this.bottomPosPxField.getIntValue()));
			// Position (%)
			else if (field == this.leftPosPctField)
				this.queryField.setLeftPosPct(toPct(this.leftPosPctField.getValue()));
			else if (field == this.rightPosPctField)
				this.queryField.setRightPosPct(toPct(this.rightPosPctField.getValue()));
			else if (field == this.topPosPctField)
				this.queryField.setTopPosPct(toPct(this.topPosPctField.getValue()));
			else if (field == this.bottomPosPctField)
				this.queryField.setBottomPosPct(toPct(this.bottomPosPctField.getValue()));
			else if (field == this.offsetFromCenterHorizontalPctField)
				this.queryField.setHorizontalOffsetFromCenterPct(toPct(this.offsetFromCenterHorizontalPctField.getValue()));
			else if (field == this.offsetFromCenterVerticalPctField) {
				this.queryField.setVerticalOffsetFromCenterPct(toPct(this.offsetFromCenterVerticalPctField.getValue()));
			} else if (field == this.disableToggle) {
				this.queryField.setDisabled(this.disableToggle.getValue().booleanValue());
			}

			updateDialogArrow(this.queryField);
		}
		if (field == this.horizStretchCheckbox) {
			updateHorizontalPositionFields();
		} else if (field == this.vertStretchCheckbox) {
			updateVerticalPositionFields();
		}
		if (field == this.horizSettingsToggle) {
			Byte hToggleValue = this.horizSettingsToggle.getValue();
			this.horizStretchCheckbox.setVisible(!hToggleValue.equals(FIELD_ALIGN_OUTER) && !hToggleValue.equals(FIELD_ALIGN_ADVANCED));
			updateHorizontalPositionFields();
		} else if (field == this.vertSettingsToggle) {
			Byte vToggleValue = this.vertSettingsToggle.getValue();
			this.vertStretchCheckbox.setVisible(!vToggleValue.equals(FIELD_ALIGN_OUTER) && !vToggleValue.equals(FIELD_ALIGN_ADVANCED));
			updateVerticalPositionFields();
		} else if (field == this.visibilityToggle) {
			this.queryField.setVisible(this.visibilityToggle.getValue().booleanValue());
			showPositioningFields();
		}
		updateWarningsFields();
		this.editableManager.getOrCreateRect(this.queryField.getField());
		this.editableManager.updateRectangle(this.queryField);
	}

	private static int toPx(Integer intValue) {
		return intValue == null ? WebAbsoluteLocation.PX_NA : intValue;
	}
	private static double toPct(Double value) {
		return value == null ? WebAbsoluteLocation.PCT_NA : value / 100d;
	}

	private void showPositioningFields() {
		boolean isVisible = this.visibilityToggle.getValue().booleanValue();

		this.horizSettingsToggle.setVisible(isVisible);
		Byte hToggleValue = this.horizSettingsToggle.getValue();
		this.horizStretchCheckbox.setVisible(isVisible && !hToggleValue.equals(FIELD_ALIGN_OUTER) && !hToggleValue.equals(FIELD_ALIGN_ADVANCED));

		this.vertSettingsToggle.setVisible(isVisible);
		Byte vToggleValue = this.vertSettingsToggle.getValue();
		this.vertStretchCheckbox.setVisible(isVisible && !vToggleValue.equals(FIELD_ALIGN_OUTER) && !vToggleValue.equals(FIELD_ALIGN_ADVANCED));

		updateHorizontalPositionFields();
		updateVerticalPositionFields();
	}
	private void updateDialogArrow(T field) {
		// Update green arrow pointing to field
		if (isUnderdefined(true) || isUnderdefined(false)) {
			return;
		}
		Collection<RootPortletDialog> dialogs = PortletHelper.findParentByType(this, RootPortlet.class).getDialogs();
		if (dialogs.size() == 1) {
			for (RootPortletDialog d : dialogs) {
				AmiWebUtils.getService(getManager()).getDesktop().setDialogArrow(this.queryFormPortlet, d, field);
			}
		}
	}

	private void updateWarningsFields() {
		this.overdefinedWarningHPx.setVisible(this.widthPxField.getValue() != null && this.leftPosPxField.getValue() != null && this.rightPosPxField.getValue() != null);
		this.overdefinedWarningHPct.setVisible(this.widthPctField.getValue() != null && this.leftPosPctField.getValue() != null && this.rightPosPctField.getValue() != null);
		this.overdefinedWarningVPx.setVisible(this.heightPxField.getValue() != null && this.topPosPxField.getValue() != null && this.bottomPosPxField.getValue() != null);
		this.overdefinedWarningVPct.setVisible(this.heightPctField.getValue() != null && this.topPosPctField.getValue() != null && this.bottomPosPctField.getValue() != null);
		if (this.horizSettingsToggle.getValue().byteValue() == FIELD_ALIGN_ADVANCED) {
			this.underdefinedWarningH.setVisible(isUnderdefined(false));
		} else
			this.underdefinedWarningH.setVisible(false);
		if (this.vertSettingsToggle.getValue().byteValue() == FIELD_ALIGN_ADVANCED) {
			this.underdefinedWarningV.setVisible(isUnderdefined(true));
		} else
			this.underdefinedWarningV.setVisible(false);
	}
	private boolean isUnderdefined(boolean isVertical) {
		FormPortletToggleButtonsField<Byte> toggle = isVertical ? this.vertSettingsToggle : this.horizSettingsToggle;
		if (!toggle.getValue().equals(FIELD_ALIGN_ADVANCED)) {
			return false;
		}
		return getRealizedPositionSettingsCount(isVertical) < 2;
	}
	private boolean isOverdefined(boolean isVertical) {
		FormPortletToggleButtonsField<Byte> toggle = isVertical ? this.vertSettingsToggle : this.horizSettingsToggle;
		if (!toggle.getValue().equals(FIELD_ALIGN_ADVANCED)) {
			return false;
		}
		return getRealizedPositionSettingsCount(isVertical) > 2;
	}
	private int getRealizedPositionSettingsCount(boolean isVertical) {
		Map<Byte, FormPortletNumericRangeField> fieldsMap = isVertical ? this.vertPosFields : this.horizPosFields;
		int count = 0;
		if (fieldsMap.get(SIZE_PX).getValue() != null || fieldsMap.get(SIZE_PCT).getValue() != null)
			count++;
		if (fieldsMap.get(START_POS_PX).getValue() != null || fieldsMap.get(START_POS_PCT).getValue() != null)
			count++;
		if (fieldsMap.get(END_POS_PX).getValue() != null || fieldsMap.get(END_POS_PCT).getValue() != null)
			count++;
		return count;
	}
	protected void updateHorizontalPositionFields() {
		updatePositionFields(false);
	}
	protected void updateVerticalPositionFields() {
		updatePositionFields(true);
	}
	protected void updatePositionFields(boolean isVertical) {
		Map<Byte, FormPortletNumericRangeField> fieldsMap = isVertical ? this.vertPosFields : this.horizPosFields;
		WebAbsoluteLocation absLocation = null;
		if (this.queryField != null) {
			absLocation = isVertical ? this.queryField.getAbsLocationV() : this.queryField.getAbsLocationH();
		}
		FormPortletNumericRangeField startPosPxField = fieldsMap.get(START_POS_PX);
		FormPortletNumericRangeField endPosPxField = fieldsMap.get(END_POS_PX);
		FormPortletNumericRangeField sizePxField = fieldsMap.get(SIZE_PX);
		FormPortletNumericRangeField startPosPctField = fieldsMap.get(START_POS_PCT);
		FormPortletNumericRangeField endPosPctField = fieldsMap.get(END_POS_PCT);
		FormPortletNumericRangeField sizePctField = fieldsMap.get(SIZE_PCT);
		FormPortletNumericRangeField centerOffsetPctField = fieldsMap.get(CENTER_OFFSET_PCT);
		byte alignToggleValue = isVertical ? this.vertSettingsToggle.getValue().byteValue() : this.horizSettingsToggle.getValue().byteValue();
		boolean allowNull = alignToggleValue == FIELD_ALIGN_ADVANCED;
		sizePxField.setNullable(allowNull);
		sizePctField.setNullable(allowNull);
		startPosPxField.setNullable(allowNull);
		startPosPctField.setNullable(allowNull);
		endPosPxField.setNullable(allowNull);
		endPosPctField.setNullable(allowNull);

		boolean stretch = isVertical ? this.vertStretchCheckbox.getBooleanValue() : this.horizStretchCheckbox.getBooleanValue();
		int queryFormDim = isVertical ? this.queryFormHeight : this.queryFormWidth;

		if (this.queryField != null) {
			if (isVertical) {
				this.queryField.convertVerticalAlignment(toggleAndCheckboxSetting2FieldAlign(alignToggleValue, stretch));
			} else {
				this.queryField.convertHorizontalAlignment(toggleAndCheckboxSetting2FieldAlign(alignToggleValue, stretch));
			}
		}

		// Update positioning fields visibility, values, and positions
		if (isVertical) {
			updateVerticalPositioningFieldsVisibility(alignToggleValue, stretch);
		} else {
			updateHorizontalPositioningFieldsVisibility(alignToggleValue, stretch);
		}
		if (!this.visibilityToggle.getValue().booleanValue()) {
			if (absLocation != null) {
				sizePxField.setValue(absLocation.getRealizedSize());
			}
		} else {
			switch (alignToggleValue) {
				case FIELD_ALIGN_LEFT_TOP: // Left
					if (stretch) { // Left Stretch 
						startPosPxField.setRange(0, queryFormDim);

						if (absLocation != null) {
							startPosPxField.setValue(absLocation.getStartPx());
							endPosPctField.setValue(absLocation.getEndPct() * 100);
						}
						// unused fields
						endPosPxField.setValue(null);
						startPosPctField.setValue(null);
						sizePctField.setValue(null);
						sizePxField.setValue(null);
						centerOffsetPctField.setValue(null);
					} else { // Left anchored
						startPosPxField.setRange(0, queryFormDim);

						if (absLocation != null) {
							sizePxField.setValue(absLocation.getSizePx());
							startPosPxField.setValue(absLocation.getStartPx());
						}
						// unused fields
						endPosPxField.setValue(null);
						startPosPctField.setValue(null);
						endPosPctField.setValue(null);
						sizePctField.setValue(null);
						centerOffsetPctField.setValue(null);
					}
					break;
				case FIELD_ALIGN_RIGHT_BOTTOM: // Right
					if (stretch) { // Right stretch
						endPosPxField.setRange(0, queryFormDim);

						if (absLocation != null) {
							startPosPctField.setValue(absLocation.getStartPct() * 100);
							endPosPxField.setValue(absLocation.getEndPx());
						}
						// unused fields
						startPosPxField.setValue(null);
						sizePxField.setValue(null);
						sizePctField.setValue(null);
						endPosPctField.setValue(null);
						centerOffsetPctField.setValue(null);
					} else { // Right anchored
						endPosPxField.setRange(0, queryFormDim);

						if (absLocation != null) {
							sizePxField.setValue(absLocation.getSizePx());
							endPosPxField.setValue(absLocation.getEndPx());
						}
						// unused fields
						startPosPxField.setValue(null);
						sizePctField.setValue(null);
						startPosPctField.setValue(null);
						endPosPctField.setValue(null);
						centerOffsetPctField.setValue(null);
					}
					break;
				case FIELD_ALIGN_OUTER: // Stretch
					startPosPxField.setRange(0, queryFormDim);
					endPosPxField.setRange(0, queryFormDim);

					if (absLocation != null) {
						startPosPxField.setValue(absLocation.getStartPx());
						endPosPxField.setValue(absLocation.getEndPx());
					}
					// unused fields
					sizePxField.setValue(null);
					sizePctField.setValue(null);
					startPosPctField.setValue(null);
					endPosPctField.setValue(null);
					centerOffsetPctField.setValue(null);
					break;
				case FIELD_ALIGN_CENTER: // Center
					if (stretch) { // Center Stretch
						if (absLocation != null) {
							startPosPctField.setValue(absLocation.getStartPct() * 100);
							endPosPctField.setValue(absLocation.getEndPct() * 100);
						}
						// unused fields
						startPosPxField.setValue(null);
						sizePxField.setValue(null);
						sizePctField.setValue(null);
						endPosPxField.setValue(null);
						centerOffsetPctField.setValue(null);
					} else { // Center anchored
						if (absLocation != null) {
							sizePxField.setValue(absLocation.getSizePx());
							centerOffsetPctField.setValue(absLocation.getOffsetFromCenterPct() * 100);
						}
						// unused fields
						startPosPxField.setValue(null);
						startPosPctField.setValue(null);
						sizePctField.setValue(null);
						endPosPxField.setValue(null);
						endPosPctField.setValue(null);
					}
					break;
				case FIELD_ALIGN_ADVANCED: // Advanced
					startPosPxField.setRange(-queryFormDim, queryFormDim);
					endPosPxField.setRange(-queryFormDim, queryFormDim);

					if (absLocation != null) {
						double sizePx = absLocation.getSizePx();
						sizePxField.setValue((Double) (!WebAbsoluteLocation.is((int) sizePx) ? null : sizePx));
						double startPx = absLocation.getStartPx();
						startPosPxField.setValue((Double) (!WebAbsoluteLocation.is((int) startPx) ? null : startPx));
						double endPx = absLocation.getEndPx();
						endPosPxField.setValue((Double) (!WebAbsoluteLocation.is((int) endPx) ? null : endPx));
						double sizePct = absLocation.getSizePct();
						sizePctField.setValue(!WebAbsoluteLocation.is(sizePct) ? null : sizePct * 100);
						double startPct = absLocation.getStartPct();
						startPosPctField.setValue(!WebAbsoluteLocation.is(startPct) ? null : startPct * 100);
						double endPct = absLocation.getEndPct();
						endPosPctField.setValue(!WebAbsoluteLocation.is(endPct) ? null : endPct * 100);
					}
					break;
			}
		}
		if (isVertical) {
			repositionVerticalSettingsFields(alignToggleValue);
		} else {
			repositionHorizontalSettingsFields(alignToggleValue);
		}
	}
	private void updateHorizontalPositioningFieldsVisibility(byte alignToggleValue, boolean stretch) {
		updatePositioningFieldsVisibility(alignToggleValue, stretch, false);
	}
	private void updateVerticalPositioningFieldsVisibility(byte alignToggleValue, boolean stretch) {
		updatePositioningFieldsVisibility(alignToggleValue, stretch, true);
	}
	private void updatePositioningFieldsVisibility(byte alignToggleValue, boolean stretch, boolean isVertical) {
		Map<Byte, FormPortletNumericRangeField> fieldsMap = isVertical ? this.vertPosFields : this.horizPosFields;
		FormPortletNumericRangeField startPosPxField = fieldsMap.get(START_POS_PX);
		FormPortletNumericRangeField endPosPxField = fieldsMap.get(END_POS_PX);
		FormPortletNumericRangeField sizePxField = fieldsMap.get(SIZE_PX);
		FormPortletNumericRangeField startPosPctField = fieldsMap.get(START_POS_PCT);
		FormPortletNumericRangeField endPosPctField = fieldsMap.get(END_POS_PCT);
		FormPortletNumericRangeField sizePctField = fieldsMap.get(SIZE_PCT);
		FormPortletNumericRangeField centerOffsetPctField = fieldsMap.get(CENTER_OFFSET_PCT);
		if (!this.visibilityToggle.getValue().booleanValue()) { // Include in HTML -> Hidden
			sizePxField.setVisible(true);
			startPosPxField.setVisible(false);
			endPosPxField.setVisible(false);
			sizePctField.setVisible(false);
			startPosPctField.setVisible(false);
			endPosPctField.setVisible(false);
			centerOffsetPctField.setVisible(false);
		} else {
			switch (alignToggleValue) {
				case FIELD_ALIGN_LEFT_TOP: // Left
					if (stretch) { // Left Stretch 
						sizePxField.setVisible(false);
						startPosPxField.setVisible(true);
						endPosPxField.setVisible(false);
						sizePctField.setVisible(false);
						startPosPctField.setVisible(false);
						endPosPctField.setVisible(true);
						centerOffsetPctField.setVisible(false);
					} else { // Left anchored
						sizePxField.setVisible(true);
						startPosPxField.setVisible(true);
						endPosPxField.setVisible(false);
						sizePctField.setVisible(false);
						startPosPctField.setVisible(false);
						endPosPctField.setVisible(false);
						centerOffsetPctField.setVisible(false);
					}
					break;
				case FIELD_ALIGN_RIGHT_BOTTOM: // Right
					if (stretch) { // Right stretch
						sizePxField.setVisible(false);
						startPosPxField.setVisible(false);
						endPosPxField.setVisible(true);
						sizePctField.setVisible(false);
						startPosPctField.setVisible(true);
						endPosPctField.setVisible(false);
						centerOffsetPctField.setVisible(false);
					} else { // Right anchored
						sizePxField.setVisible(true);
						startPosPxField.setVisible(false);
						endPosPxField.setVisible(true);
						sizePctField.setVisible(false);
						startPosPctField.setVisible(false);
						endPosPctField.setVisible(false);
						centerOffsetPctField.setVisible(false);
					}
					break;
				case FIELD_ALIGN_OUTER: // Stretch
					sizePxField.setVisible(false);
					startPosPxField.setVisible(true);
					endPosPxField.setVisible(true);
					sizePctField.setVisible(false);
					startPosPctField.setVisible(false);
					endPosPctField.setVisible(false);
					centerOffsetPctField.setVisible(false);
					break;
				case FIELD_ALIGN_CENTER: // Center
					if (stretch) { // Center Stretch
						sizePxField.setVisible(false);
						startPosPxField.setVisible(false);
						endPosPxField.setVisible(false);
						sizePctField.setVisible(false);
						startPosPctField.setVisible(true);
						endPosPctField.setVisible(true);
						centerOffsetPctField.setVisible(false);
					} else { // Center anchored
						sizePxField.setVisible(true);
						startPosPxField.setVisible(false);
						endPosPxField.setVisible(false);
						sizePctField.setVisible(false);
						startPosPctField.setVisible(false);
						endPosPctField.setVisible(false);
						centerOffsetPctField.setVisible(true);
					}
					break;
				case FIELD_ALIGN_ADVANCED: // Advanced
					sizePxField.setVisible(true);
					startPosPxField.setVisible(true);
					endPosPxField.setVisible(true);
					sizePctField.setVisible(true);
					startPosPctField.setVisible(true);
					endPosPctField.setVisible(true);
					centerOffsetPctField.setVisible(false);
					break;
			}
		}
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}
	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		if (field == this.dmExpressionField) {
			BasicWebMenu r = new BasicWebMenu();
			AmiWebMenuUtils.createVariablesMenu(r, false, this.getField().getForm());
			return r;
		} else
			return null;
	}
	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		if (node == this.dmExpressionField) {
			AmiWebMenuUtils.processContextMenuAction(service, action, node);
			onFieldValueChanged(portlet, queryField.getField(), null);
		} else if (node instanceof FormPortletTextEditField) {
			AmiWebMenuUtils.processContextMenuAction(service, action, (FormPortletTextEditField) node);
		}
	}

	public List<FormPortletField<?>> getMissingRequiredFields() {
		List<FormPortletField<?>> missing = new ArrayList<FormPortletField<?>>();
		FormPortletField<?> f;
		for (int i = 0; i < this.requiredFields.size(); i++) {
			f = this.requiredFields.get(i);
			if (f.getValue() == null || "".equals(f.getValue())) {
				missing.add(f);
			}
		}
		return missing;
	}
	public List<FormPortletField<?>> getRequiredFields() {
		return requiredFields;
	}

	protected FormPortletNumericRangeField getWidthPxField() {
		return widthPxField;
	}
	protected FormPortletNumericRangeField getHeightPxField() {
		return heightPxField;
	}
	protected FormPortletToggleButtonsField<Byte> getHorizSettingsToggle() {
		return horizSettingsToggle;
	}
	protected FormPortletToggleButtonsField<Byte> getVertSettingsToggle() {
		return vertSettingsToggle;
	}

	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		if (KeyEvent.ESCAPE.equals(keyEvent.getKey()))
			return true;
		return super.onUserKeyEvent(keyEvent);
	}

	public T getField() {
		return queryField;
	}
	final public void setQueryField(T field) {
		this.queryField = field;
		this.queryField.getAmiScriptCallbacks().setThis(queryField);
		this.queryField.getAmiScriptCallbacks().setAmiLayoutAlias(this.queryFormPortlet.getAmiLayoutFullAlias());
		if (this.amiScriptForm != null) {
			//			this.amiScriptForm.setThis(this.queryField);
			this.amiScriptForm.setCallbacks(this.queryField.getAmiScriptCallbacks());
		}
		this.styleForm.setAmiWebStyle(queryField.getStylePeer());
	}
	public void clearField() {
		this.queryField = null;
		this.editId = null;
	}

	public String getVarNameFieldValue() {
		return this.nameField == null ? null : this.nameField.getValue();
	}

	public void setFieldName(String name) {
		nameField.setValue(name);
	}
	public String getFieldName() {
		return nameField.getValue();
	}
	public void setFieldLabel(String label) {
		labelField.setValue(label);
	}
	public String getFieldLabel() {
		return labelField.getValue();
	}
	public void setDmExpression(String dmExpression) {
		dmExpressionField.setValue(dmExpression);
	}
	public String getDmExpression() {
		return dmExpressionField.getValue();
	}
	public byte getHorizontalSettingsToggle() {
		return this.horizSettingsToggle.getValue();
	}
	public byte getVerticalSettingsToggle() {
		return this.vertSettingsToggle.getValue();
	}
	public boolean getHorizontalStretchCheckbox() {
		return this.horizStretchCheckbox.getValue();
	}
	public boolean getVerticalStretchCheckbox() {
		return this.vertStretchCheckbox.getValue();
	}
	public FormPortlet getSettingsForm() {
		return this.settingsForm;
	}
	public AmiWebFormFieldFactory<?> getFactory() {
		return this.factory;
	}

	@Override
	public void onClosed() {
		if (this.amiScriptForm != null)
			this.amiScriptForm.close();
		super.onClosed();
	}

	public AmiWebService getService() {
		return this.service;
	}
}
