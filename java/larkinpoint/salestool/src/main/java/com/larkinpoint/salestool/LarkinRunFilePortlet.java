package com.larkinpoint.salestool;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.larkinpoint.messages.LoadFileMessage;

public class LarkinRunFilePortlet extends FormPortlet {

	private FormPortletTextField fileNameField;
	private FormPortletSelectField<Byte> fileTypeField;
	private FormPortletButton runButton;

	public LarkinRunFilePortlet(PortletConfig config) {
		super(config);

		addField(this.fileNameField = new FormPortletTextField("file name:").setWidth(400));
		addField(this.fileTypeField = new FormPortletSelectField<Byte>(Byte.class, "Choose File Type:"));

		fileTypeField.addOption(LoadFileMessage.TYPE_SECURITY_PRICES, "Underlying Prices");
		fileTypeField.addOption(LoadFileMessage.TYPE_CURRENCY, "Currency Files");
		fileTypeField.addOption(LoadFileMessage.TYPE_SYMBOL_NAMES, "Underlying Definitions");
		fileTypeField.addOption(LoadFileMessage.TYPE_DAILY_OPTION, "Daily Option Prices");
		fileTypeField.addOption(LoadFileMessage.TYPE_EXCHANGE_DATA, "Exchanges Info");
		fileTypeField.addOption(LoadFileMessage.TYPE_HIST_VOL, "Historical Vol files");
		fileTypeField.addOption(LoadFileMessage.TYPE_INDEX_DIV, "Index Dividends");
		fileTypeField.addOption(LoadFileMessage.TYPE_STD_OPTIONS, "STD Options");
		fileTypeField.addOption(LoadFileMessage.TYPE_VOL_SURFACE, "Vol Surfaces");
		fileTypeField.addOption(LoadFileMessage.TYPE_ZERO_CURVES, "Zero Curves");

		addButton(runButton = new FormPortletButton("load files"));
		//	int wh = getSuggestedHeight(getManager().getPortletMetrics());
		//int ww = getSuggestedWidth(getManager().getPortletMetrics());

	}
	@Override
	public void onUserPressedButton(FormPortletButton button) {
		if (button == this.runButton) {
			LoadFileMessage msg = nw(LoadFileMessage.class);
			//msg.setGetFiles((byte) 1);
			msg.setLoadFilename(fileNameField.getValue());
			msg.setLoadFiletype(fileTypeField.getValue());
			getManager().sendMessageToBackend("LARKIN", msg);

		} else
			super.onUserPressedButton(button);
	}

	public static class Builder extends AbstractPortletBuilder<LarkinRunFilePortlet> {

		private static final String ID = "loadFile";

		public Builder() {
			super(LarkinRunFilePortlet.class);
		}

		@Override
		public LarkinRunFilePortlet buildPortlet(PortletConfig portletConfig) {
			LarkinRunFilePortlet portlet = new LarkinRunFilePortlet(portletConfig);
			return portlet;
		}

		@Override
		public String getPortletBuilderName() {
			return "File Loader";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

}
