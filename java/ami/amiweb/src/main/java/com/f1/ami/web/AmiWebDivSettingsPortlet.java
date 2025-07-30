package com.f1.ami.web;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;

public class AmiWebDivSettingsPortlet extends AmiWebPanelSettingsPortlet {

	private final AmiWebDividerPortlet dividerPortlet;
	private final FormPortletToggleButtonsField<Boolean> lockedField;
	private final FormPortletToggleButtonsField<Byte> alignField;
	private final FormPortletToggleButtonsField<Byte> snapField;
	private final FormPortletNumericRangeField unsnapMinPctField;
	//	private final FormPortletNumericRangeField defaultOffsetPctField = new FormPortletNumericRangeField("Default Offset (%):", 0, 100, 0);

	public AmiWebDivSettingsPortlet(PortletConfig config, AmiWebDividerPortlet portlet) {
		super(config, portlet);
		this.dividerPortlet = portlet;
		this.lockedField = new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Lock Divider: ");
		this.lockedField.addOption(Boolean.FALSE, "Unlocked");
		this.lockedField.addOption(Boolean.TRUE, "Locked");
		this.lockedField.setHelp("<b>Unlocked</b>: user can adjust divider.<br><b>Locked</b>: divider stays at its current location; user cannot adjust divider.");

		this.alignField = new FormPortletToggleButtonsField<Byte>(Byte.class, "Align Divider: ");
		this.alignField.addOption(AmiWebDividerPortlet.ALIGN_RATIO, "Ratio");
		this.alignField.addOption(AmiWebDividerPortlet.ALIGN_START, "Left/Top");
		this.alignField.addOption(AmiWebDividerPortlet.ALIGN_END, "Right/Bottom");
		this.alignField.setHelp(
				"<b>Ratio</b>: left and right panel will stay the same ratio when window or browser size decreases.<br><b>Left/Top</b>: honors left/top panel's width/height when window or browser's width/height decreases.<br><b>Right/Bottom</b>: honors right/bottom panel's width/height when window or browser's width/height decreases.");

		this.snapField = new FormPortletToggleButtonsField<Byte>(Byte.class, "Double-click Snaps To: ");
		this.snapField.addOption(AmiWebDividerPortlet.SNAP_SETTING_NONE, "None");
		this.snapField.addOption(AmiWebDividerPortlet.SNAP_SETTING_START, "Left/Top");
		this.snapField.addOption(AmiWebDividerPortlet.SNAP_SETTING_END, "Right/Bottom");
		this.unsnapMinPctField = new FormPortletNumericRangeField("Unsnap Min %: ").setRange(0, 100);

		this.lockedField.setValue(portlet.getIsLocked(false));
		this.alignField.setValue(portlet.getAlign(false));
		this.snapField.setValue(portlet.getSnapSetting(false));
		this.unsnapMinPctField.setValue(portlet.getUnsnapMinPct(false) * 100);
		this.unsnapMinPctField.setHelp(
				"This is the <b>minimum</b> percentage of space that the hidden panel will receive once unsnapped.<br> In other words, if it is set to 30, then once the user unsnaps the divider, it will return at least 30% of the total space to the hidden panel. <br> By default it returns to the previous position before the snap.");

		getTitleField().setVisible(false);
		getSettingsForm().addField(this.lockedField);
		getSettingsForm().addField(this.alignField);
		getSettingsForm().addField(this.snapField);
		getSettingsForm().addField(this.unsnapMinPctField);

	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == getSubmitButton()) {
			// TODO we should check for same value before setting...
			this.dividerPortlet.setIsLocked(this.lockedField.getValue(), false);
			this.dividerPortlet.setAlign(this.alignField.getValue(), false);
			this.dividerPortlet.setSnapSetting(this.snapField.getValue(), false);
			this.dividerPortlet.setUnsnapMinPct(this.unsnapMinPctField.getValue() / 100d, false);
			this.dividerPortlet.getService().getDesktop().flagUpdateWindowLinks();
			// subtract from size
			this.dividerPortlet.getInnerContainer().setCurrentAsDefault();
		}
		super.onButtonPressed(portlet, button);
	}
}
