package com.f1.ami.web;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.client.AmiClient;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.text.SimpleFastTextPortlet;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.EH;
import com.f1.utils.FastBufferedWriter;
import com.f1.utils.FastByteArrayOutputStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.StreamPiper;
import com.f1.utils.casters.Caster_File;

public class AmiWebUploadDataPortlet extends GridPortlet implements FormPortletListener, AmiWebSpecialPortlet {

	static final private Logger log = LH.get();

	private FormPortlet fm;
	private FormPortlet fm2;
	private FormPortletTextField hostField;
	private FormPortletTextField portField;
	private FormPortletTextAreaField dataField;
	private FormPortletButton uploadButton;
	private FormPortletCheckboxField dismissField;

	public AmiWebUploadDataPortlet(PortletConfig config) {
		super(config);
		File sampleFile = config.getPortletManager().getTools().getOptional(AmiWebProperties.PROPERTY_AMI_SAMPLEDATA_FILE, Caster_File.INSTANCE);
		String sampleText = "L|I=\"Simulator\"\n";
		if (sampleFile != null) {
			try {
				sampleText = IOH.readText(sampleFile, true);
			} catch (IOException e) {
				LH.warning(log, "error reading sample data (file based on property ami.sampledata.file): ", e);
			}
		}
		this.fm = addChild(new FormPortlet(generateConfig()), 0, 0).getFormPortletStyle().setLabelsWidth(10);
		this.fm2 = addChild(new FormPortlet(generateConfig()), 0, 1);
		fm.addField(new FormPortletTitleField("AMI Backend Host Name"));
		hostField = fm.addField(new FormPortletTextField("")).setValue(EH.getLocalHost()).setWidth(200);
		fm.addField(new FormPortletTitleField("AMI Backend Host Port"));
		portField = fm.addField(new FormPortletTextField("")).setValue(SH.toString(AmiClient.DEFAULT_PORT)).setWidth(100);
		fm.addField(new FormPortletTitleField("Data To Upload:"));
		dataField = fm.addField(new FormPortletTextAreaField("")).setValue(sampleText);
		dataField.setCssStyle("style.fontSize=12px");
		dismissField = fm2.addField(new FormPortletCheckboxField("Keep this dialog open after submit.")).setValue(false);
		uploadButton = fm2.addButton(new FormPortletButton("Upload Data"));
		setRowSize(1, 75);
		setSuggestedSize(1000, 800);
		fm2.addFormPortletListener(this);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if ("close".equals(button.getCorrelationData())) {
			portlet.getParent().close();
			if (!dismissField.getValue())
				close();
		} else if (button == uploadButton) {
			try {
				String portStr = portField.getValue();
				int port;
				try {
					port = SH.parseInt(portStr);
				} catch (Exception e) {
					getManager().showAlert("Invalid port, must be a number");
					return;
				}
				Socket socket = new Socket(hostField.getValue(), port);
				FastBufferedWriter out = new FastBufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				FastByteArrayOutputStream buf = new FastByteArrayOutputStream();
				Thread th = new Thread(new StreamPiper(socket.getInputStream(), buf, 10240));
				th.start();
				out.write(dataField.getValue() + "\nx\n");
				out.flush();
				th.join(1000);
				th.interrupt();
				socket.close();
				String results = buf.toString();
				int errors = 0;
				int lines = 0;
				SimpleFastTextPortlet body = new SimpleFastTextPortlet(generateConfig());
				StringBuilder resultsHtml = new StringBuilder();
				resultsHtml.append("<span style='font-family:courier'>");
				resultsHtml.append("<span>");
				boolean inError = false;
				for (String line : SH.splitLines(results)) {
					if (SH.isnt(line))
						continue;
					int pos = line.indexOf("|S=") + 3;
					boolean error = pos == -1 || pos >= line.length() || line.charAt(pos) != '0';
					if (error)
						errors++;
					if (error != inError) {
						resultsHtml.append(error ? "</span><span style='color:red'>" : "</span><span>");
						inError = error;
					}
					lines++;
					body.appendLine(SH.toString(lines), line, error ? "_fg=red" : "");
					WebHelper.escapeHtml(line, resultsHtml);
					resultsHtml.append("<BR>");
				}
				resultsHtml.append("</span>");
				resultsHtml.append("</span>");
				GridPortlet dialog = new GridPortlet(generateConfig());
				HtmlPortlet top = dialog.addChild(new HtmlPortlet(generateConfig()), 0, 0);
				if (errors > 0)
					top.setHtml("<Center><B>Data sent.<BR> <span style='color:red'>" + errors + " Error(s) detected in response</span>");
				else
					top.setHtml("<Center><B>Response");
				dialog.addChild(body, 0, 1);

				FormPortlet fp = dialog.addChild(new FormPortlet(generateConfig()), 0, 2);
				fp.addFormPortletListener(this);
				fp.addButton(new FormPortletButton("Close").setCorrelationData("close"));
				dialog.setRowSize(0, 50);
				dialog.setRowSize(2, 50);
				getManager().showDialog("Ami data sent", dialog, 800, 500);
			} catch (Exception e) {
				LH.warning(log, "Error sending data to ", hostField.getValue(), ":", portField.getValue(), e);
				getManager().showAlert("Error sending data: " + e.getMessage());
			}
		}
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		dataField.setHeight(Math.max(100, height - 200));
	}

	public static class Builder extends AbstractPortletBuilder<AmiWebUploadDataPortlet> {

		public static final String ID = "VortexWebAmiUpladDataPortlet";

		public Builder() {
			super(AmiWebUploadDataPortlet.class);
		}

		@Override
		public AmiWebUploadDataPortlet buildPortlet(PortletConfig portletConfig) {
			return new AmiWebUploadDataPortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Ami Upload Data";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

}
