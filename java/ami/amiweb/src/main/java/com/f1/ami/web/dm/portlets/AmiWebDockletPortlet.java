package com.f1.ami.web.dm.portlets;

import java.util.Map;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletDivField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.utils.MH;

public class AmiWebDockletPortlet extends GridPortlet implements FormPortletListener {
	private final FormPortlet form;
	private final GridPortlet innerGrid;
	private final FormPortletDivField background;
	private final FormPortletDivField header;
	private final FormPortletToggleButtonsField<Byte> toggle;
	private final FormPortletSelectField<Byte> selectField;
	private final FormPortletNumericRangeField input;
	private final FormPortletTitleField tip;
	private String enabledIconCssClass;
	private String disabledIconCssClass;
	private String helpTip;
	private String dockletTitle;
	private boolean hasInput;

	protected static final byte OPTION_OFF = 0;
	protected static final byte OPTION_ON_ALWAYS = 1;
	protected static final byte OPTION_ON_ONCE = 2;

	public AmiWebDockletPortlet(PortletConfig config, String dockletTitle, boolean hasInput) {
		this(config, dockletTitle, hasInput, false);
	}
	public AmiWebDockletPortlet(PortletConfig config, String dockletTitle, boolean hasInput, boolean hasSelectField) {
		super(config);
		this.hasInput = hasInput;
		this.form = new FormPortlet(generateConfig());

		this.innerGrid = new GridPortlet(generateConfig());
		addChild(this.innerGrid, 0, 0, 1, 1);
		this.innerGrid.addChild(this.form, 0, 0, 1, 1);
		initFields: {
			int fieldSpacingPx = 10;
			int fieldHeightPx = 25;
			background: {
				this.background = new FormPortletDivField("");
				this.background.setCssStyle("style.border=2px solid #959595|_bg=#e2e2e2");
				this.background.setLabelHidden(true);
				this.background.setWidthPx(131);
				this.background.setHeightPx(250);
				this.background.setLeftPosPx(0);
				this.background.setTopPosPx(0);
				this.form.addField(this.background);
			}
			header: {
				this.header = new FormPortletDivField("");
				this.header.setLabelHidden(true);
				this.header.setWidthPx(131);
				this.header.setHeightPx(140);
				this.header.setTopPosPx(5);
				this.header.setLeftPosPx(0);
				this.form.addField(this.header);
			}
			int optionFieldTopPx = 135;
			if (!hasSelectField)
				toggle: {
					this.toggle = new FormPortletToggleButtonsField<Byte>(Byte.class, "");
					this.toggle.setLabelHidden(true);
					this.toggle.addOption(OPTION_ON_ALWAYS, "On");
					this.toggle.addOption(OPTION_OFF, "Off");
					this.toggle.setButtonStyle("style.minWidth=40px");
					this.toggle.setWidthPx(88).setHeightPx(fieldHeightPx).setTopPosPx(optionFieldTopPx).setLeftPosPx(21);
					this.form.addField(this.toggle);
					this.selectField = null;
				}
			else
				selectField: {
					this.selectField = new FormPortletSelectField<Byte>(Byte.class, "");
					this.selectField.setLabelHidden(true);
					this.selectField.addOption(OPTION_OFF, "Off");
					this.selectField.addOption(OPTION_ON_ALWAYS, "Always");
					this.selectField.addOption(OPTION_ON_ONCE, "Once");
					this.selectField.setWidthPx(88).setHeightPx(fieldHeightPx).setTopPosPx(optionFieldTopPx).setLeftPosPx(21);
					this.form.addField(this.selectField);
					this.toggle = null;
				}
			tip_input: {
				this.tip = new FormPortletTitleField("   ");
				this.tip.setLabelHidden(true);
				this.form.addField(this.tip);
				if (this.hasInput) {
					this.input = new FormPortletNumericRangeField("");
					this.input.setLabelHidden(true);
					this.input.setSliderHidden(true);
					this.input.setRange(0, 100);
					this.input.setValue(0);
					this.input.setDecimals(0);
					this.input.setWidthPx(100).setHeightPx(fieldHeightPx).setTopPosPx(this.toggle.getTopPosPx() + this.toggle.getHeightPx() + fieldSpacingPx).setLeftPosPx(15);
					this.input.setCssStyle("type=number|_cna=ami_docklet_input");
					this.form.addField(this.input);
					this.tip.setWidthPx(60).setHeightPx(fieldHeightPx).setTopPosPx(this.input.getTopPosPx() + this.input.getHeightPx() + fieldSpacingPx).setLeftPosPx(35);
				} else {
					this.input = null;
					this.tip.setWidthPx(60).setHeightPx(fieldHeightPx).setTopPosPx(optionFieldTopPx + 2 * (fieldHeightPx + fieldSpacingPx)).centerHorizontally();
				}
			}
		}
		this.setDockletTitleNoFire(dockletTitle);
		this.updateHeaderHtmlLayout();
		updateLowerFormLayout();
		this.form.addFormPortletListener(this);
	}
	public AmiWebDockletPortlet(PortletConfig config) {
		this(config, "", true);
	}

	private void updateHeaderHtmlLayout() {
		StringBuilder layout = new StringBuilder();
		layout.append("<div class=\"ami_docklet_item ami_docklet_icon ");
		layout.append(getIconCssClass());
		layout.append("\"></div>");
		layout.append("<div class=\"ami_docklet_item ami_docklet_title\">");
		layout.append(dockletTitle);
		layout.append("</div>");
		this.header.setValue(layout.toString());
	}
	private void updateLowerFormLayout() {
		if (this.hasInput) {
			this.input.setDisabled(!isEnabled());
			if (isEnabled()) {
				this.input.setCssStyle("type=number|_cn=ami_docklet_input|_cna=docklet_enabled");
			} else {
				this.input.setCssStyle("type=number|_cn=ami_docklet_input|_cna=docklet_disabled");
			}
		}
		tip.setCssStyle("_cna=ami_docklet_tip");
	}
	public boolean isEnabled() {
		if (this.toggle != null)
			return this.toggle.getValue() != OPTION_OFF;
		else
			return this.selectField.getValue() != OPTION_OFF;
	}
	public AmiWebDockletPortlet setEnabled(boolean enabled) {
		return setStatus(enabled ? OPTION_ON_ALWAYS : OPTION_OFF);
	}
	public AmiWebDockletPortlet setStatus(byte status) {
		if (this.toggle != null && status == this.toggle.getValue()) {
			return this;
		}
		if (this.selectField != null && status == this.selectField.getValue()) {
			return this;
		}
		if (this.toggle != null)
			this.toggle.setValue(status);
		else
			this.selectField.setValue(status);
		this.updateHeaderHtmlLayout();
		updateLowerFormLayout();
		return this;
	}
	public byte getStatus() {
		if (this.toggle != null) {
			return this.toggle.getValue();
		} else {
			return this.selectField.getValue();
		}
	}
	private AmiWebDockletPortlet setEnabledNoFire(byte status) {
		if (toggle != null) {
			toggle.setValueNoFire(status);
		} else {
			selectField.setValueNoFire(status);
		}
		if (input != null && (status == OPTION_ON_ALWAYS || status == OPTION_ON_ONCE) && !MH.between(input.getValue(), input.getMin(), input.getMax(), 0))
			input.setValue(input.getMin());
		this.updateHeaderHtmlLayout();
		updateLowerFormLayout();
		return this;
	}
	public double getValue() {
		return this.input.getValue();
	}

	public AmiWebDockletPortlet setValue(double value) {
		if (!isEnabled() || !hasInput)
			return this;
		if (value != getValue())
			this.input.setValue(value);
		return this;
	}
	public AmiWebDockletPortlet setValueNoFire(double value) {
		if (!isEnabled() || !hasInput)
			return this;
		input.setValueNoFire(value);
		return this;

	}

	public AmiWebDockletPortlet setRange(double min, double max) {
		if (hasInput) {
			input.setRange(min, max);
		}
		return this;
	}
	public AmiWebDockletPortlet setDecimals(int precision) {
		if (hasInput) {
			input.setDecimals(precision);
		}
		return this;
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {

	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.input) {
			setValueNoFire(input.getValue());
		} else if (field == this.toggle) {
			setEnabledNoFire(toggle.getValue());
		} else if (field == this.selectField) {
			setEnabledNoFire(selectField.getValue());
		}
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}
	public String getHelpTip() {
		return helpTip;
	}
	public AmiWebDockletPortlet setHelpTip(String helpTip) {
		tip.setHelp(helpTip);
		return this;
	}
	public String getDockletTitle() {
		return dockletTitle;
	}
	public AmiWebDockletPortlet setDockletTitle(String dockletTitle) {
		this.dockletTitle = dockletTitle;
		this.updateHeaderHtmlLayout();
		updateLowerFormLayout();
		return this;
	}
	public AmiWebDockletPortlet setDockletTitleNoFire(String dockletTitle) {
		this.dockletTitle = dockletTitle;
		return this;
	}
	public String getIconCssClass() {
		return isEnabled() ? enabledIconCssClass : disabledIconCssClass;
	}
	public String getEnabledIconCssClass() {
		return enabledIconCssClass;
	}
	public AmiWebDockletPortlet setEnabledIconCssClass(String iconCssClass) {
		this.enabledIconCssClass = iconCssClass;
		this.updateHeaderHtmlLayout();
		updateLowerFormLayout();
		return this;
	}
	public String getDisabledIconCssClass() {
		return disabledIconCssClass;
	}
	public AmiWebDockletPortlet setDisabledIconCssClass(String iconDisabledCssClass) {
		this.disabledIconCssClass = iconDisabledCssClass;
		this.updateHeaderHtmlLayout();
		updateLowerFormLayout();
		return this;
	}

}
