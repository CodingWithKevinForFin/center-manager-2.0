package com.f1.ami.web;

import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.sim.AmiSim;
import com.f1.ami.sim.AmiSimField;
import com.f1.ami.sim.AmiSimObject;
import com.f1.ami.sim.AmiSimType;
import com.f1.base.Action;
import com.f1.base.Row;
import com.f1.http.HttpRequestResponse;
import com.f1.suite.web.HttpRequestAction;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.PortletManagerListener;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.DesktopPortlet;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.utils.DetailedException;
import com.f1.utils.Formatter;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.SkipListDataEntry;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.BasicTable;

public class AmiWebSimulatorPortlet extends GridPortlet implements FormPortletListener, PortletManagerListener, AmiWebSpecialPortlet {

	private static final Logger log = LH.get();
	private FormPortlet fp;
	private AmiSim simulator;
	private FormPortletButton resetButton;
	private FormPortletButton startButton;
	private FormPortletTextField hostField;
	private FormPortletTextAreaField textField;
	private DividerPortlet div;
	private HtmlPortlet statusHtml;
	private FormPortletButton hideButton;
	private HtmlPortlet countsHtml;
	private FormPortletButton viewButton;
	private FormPortletTextField seedField;

	public AmiWebSimulatorPortlet(PortletConfig config) {
		super(config);
		fp = (FormPortlet) addChild(new FormPortlet(generateConfig()), 0, 0, 2, 1).getPortlet();
		statusHtml = addChild(new HtmlPortlet(generateConfig(), ""), 0, 1);
		countsHtml = addChild(new HtmlPortlet(generateConfig(), ""), 1, 1);
		setRowSize(1, 18);
		setColSize(0, 100);
		fp.getFormPortletStyle().setLabelsWidth(10);
		fp.addField(new FormPortletTitleField("AMI Backend Host & Port"));
		hostField = fp.addField(new FormPortletTextField("")).setWidth(200);
		fp.addField(new FormPortletTitleField("Random Number Seed"));
		seedField = fp.addField(new FormPortletTextField("").setValue("20101130")).setWidth(200);
		fp.addField(new FormPortletTitleField("Simulator Configuration"));
		textField = fp.addField(new FormPortletTextAreaField(""));
		textField.setCssStyle("style.fontSize=11px");
		this.startButton = fp.addButton(new FormPortletButton("Start"));
		this.resetButton = fp.addButton(new FormPortletButton("Reset"));
		this.viewButton = fp.addButton(new FormPortletButton("View Data"));
		this.hideButton = fp.addButton(new FormPortletButton("Hide"));
		this.fp.getFormPortletStyle().setButtonsSpacing(2);
		this.statusHtml.setCssStyle("_fm=left,courier,bold|_bg=#EEEEEE|style.border=1px solid #AAAAAA|style.borderWidth=1px 0px 0px 0px");
		this.countsHtml.setCssStyle("_fm=right,courier|_bg=#EEEEEE|style.border=1px solid #AAAAAA|style.borderWidth=1px 0px 0px 0px");
		this.simulator = new AmiSim();
		fp.addFormPortletListener(this);
		this.hostField.setValue(simulator.getRemoteHost() + ":" + SH.toString(simulator.getRemotePort()));
		setSuggestedSize(400, 1000);
		getManager().addPortletManagerListener(this);
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == hideButton) {
			((DesktopPortlet) getParent()).getWindow(getPortletId()).minimizeWindow();
		} else if (button == startButton) {
			this.userStarted = true;
			if (simulator.getStatus() == AmiSim.STATUS_CONNECTED) {
				simulator.start();
				this.startButton.setName("Pause");
			} else if (simulator.getStatus() == AmiSim.STATUS_NOT_CONNECTED) {
				this.simulator.setClock(getManager().getState().getPartition().getContainer().getServices().getClock());
				if (SH.isnt(seedField.getValue()))
					this.simulator.setRandomSeed(null);
				else
					this.simulator.setRandomSeed(SH.parseLong(seedField.getValue()));
				this.simulator.setRemoteHost(SH.beforeLast(hostField.getValue(), ':'));
				this.simulator.setRemotePort(SH.parseInt(SH.afterLast(hostField.getValue(), ':')));
				String text = this.textField.getValue();
				try {
					this.simulator.setConfig((Map<String, Object>) new ObjectToJsonConverter().stringToObject(text));
				} catch (Exception e) {
					String message = e.getMessage();
					if (e instanceof DetailedException)
						message = ((DetailedException) e).toLegibleString();
					ExpressionParserException ex = OH.findInnerException(e, ExpressionParserException.class);
					if (ex != null) {
						int pos = ex.getPosition();
						if (pos != -1) {
							this.textField.setCursorPosition(pos);
							String[] lines = SH.splitLines(text);
							Tuple2<Integer, Integer> p = SH.getLinePosition(text, pos);
							this.textField.getValue();
							int ln = p.getA();
							StringBuilder sb = new StringBuilder();
							sb.append("...<BR>");
							for (int i = Math.max(0, ln - 2); i < Math.min(lines.length, ln + 2); i++) {
								if (i == ln)
									sb.append("<B>").append(lines[i]).append("<BR></B>");
								else
									sb.append(lines[i]).append("<BR>");
							}
							sb.append("...<BR>");
							getManager().showAlert("Near <B>line " + (1 + ln) + "</B>:" + message + "<BR><BR><P> <div style='text-align:left;background:white'>" + sb + "</div>");
						} else
							getManager().showAlert(message);
					} else {
						getManager().showAlert(message);
					}
					LH.warning(log, e);
					return;
				}
				try {
					this.simulator.start();
				} catch (Exception e) {
					LH.warning(log, "Error connecting to ", hostField.getValue(), e);
					getManager().showAlert(e.getMessage());
				}
				this.textField.setDisabled(true);
				this.hostField.setDisabled(true);
				this.seedField.setDisabled(true);
				this.startButton.setName("Pause");
			} else {
				this.simulator.stop();
				this.textField.setDisabled(false);
				this.startButton.setName("Continue");
			}
		} else if (button == resetButton) {
			this.userStarted = true;
			if (this.simulator.getStatus() != AmiSim.STATUS_NOT_CONNECTED) {
				this.simulator.disconnect();
				this.textField.setDisabled(false);
				this.hostField.setDisabled(false);
				this.seedField.setDisabled(false);
				this.simulator.clear();
				this.startButton.setName("Start");
			} else if (this.simulator.getStatus() == AmiSim.STATUS_NOT_CONNECTED) {
				this.simulator.clear();
			}
		} else if (button == viewButton) {
			if (this.simulator.getStatus() == AmiSim.STATUS_RUNNING) {
				getManager().showAlert("Must pause before viewing data");
				return;
			}
			TabPortlet tabs = new TabPortlet(generateConfig());
			tabs.setIsCustomizable(false);
			for (String ids : simulator.getTypes()) {
				AmiSimType type = simulator.getType(ids);
				BasicTable table = new BasicTable(new String[] { AmiConsts.TABLE_PARAM_ID, "!exp", "!type" });
				table.setTitle(type.getType());
				for (Entry<String, AmiSimField<Object>> e : type.getFields().entrySet())
					table.addColumn(Object.class, e.getKey());
				FastTablePortlet ftable = new FastTablePortlet(generateConfig(), table, null);
				for (Entry<String, AmiSimField<Object>> e : type.getFields().entrySet())
					ftable.getTable().addColumn(true, e.getKey(), e.getKey(), new BasicWebCellFormatter());
				ftable.getTable().addColumn(true, "ID", AmiConsts.TABLE_PARAM_ID, new BasicWebCellFormatter());
				ftable.getTable().addColumn(true, "Expires", "!exp", new BasicWebCellFormatter());
				ftable.getTable().addColumn(false, "Type", "!type", new BasicWebCellFormatter());
				for (SkipListDataEntry<AmiSimObject> o : type.getObjects()) {
					AmiSimObject obj = o.getData();
					Row row = table.newEmptyRow();
					row.putAt(0, obj.getId());
					row.putAt(1, obj.getExpires());
					row.putAt(2, obj.getType());
					for (Entry<String, AmiSimField<Object>> e : type.getFields().entrySet()) {
						row.put(e.getKey(), obj.getParams().get(e.getKey()));
					}
					table.getRows().add(row);
				}
				tabs.addChild(type.getId(), ftable);
			}
			{
				BasicTable table = new BasicTable(new String[] { "msg" });
				FastTablePortlet ftable = new FastTablePortlet(generateConfig(), table, null);
				table.setTitle("Messages");
				ftable.getTable().addColumn(true, "", "msg", new BasicWebCellFormatter().addConditionalDefault("_fm=courier")).setWidth(2000);
				for (String msg : simulator.getRecentMessages()) {
					table.getRows().addRow(msg);
				}
				tabs.addChild("Messages Full (500k max)", ftable);
			}
			{
				BasicTable table = new BasicTable(new String[] { "msg" });
				FastTablePortlet ftable = new FastTablePortlet(generateConfig(), table, null);
				table.setTitle("Messages");
				ftable.getTable().addColumn(true, "", "msg", new BasicWebCellFormatter().addConditionalDefault("_fm=courier")).setWidth(2000);
				for (String msg : simulator.getRecentMessages()) {
					int i1 = msg.indexOf('#');
					int i2 = msg.indexOf('|');
					if (i1 == -1)
						table.getRows().addRow(msg);
					else
						table.getRows().addRow(msg.substring(0, i1) + msg.substring(i2));
				}
				tabs.addChild("Messages(500k max)", ftable);
			}
			getManager().showDialog("simulator data", tabs);
		}
	}

	public static class Builder extends AbstractPortletBuilder<AmiWebSimulatorPortlet> {

		public static final String ID = "VortexWebAmiSimulatorPortlet";

		public Builder() {
			super(AmiWebSimulatorPortlet.class);
		}

		@Override
		public AmiWebSimulatorPortlet buildPortlet(PortletConfig portletConfig) {
			return new AmiWebSimulatorPortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Ami Simulator";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
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
		textField.setHeight(Math.max(100, height - 200));
	}
	@Override
	public void onClosed() {
		if (simulator.getStatus() == AmiSim.STATUS_RUNNING) {
			this.simulator.disconnect();
		}
		super.onClosed();
	}

	private boolean userStarted = false;

	@Override
	public void onFrontendCalled(PortletManager manager, Map<String, String> attributes, HttpRequestAction action) {
		if (!userStarted)
			return;
		StringBuilder text = new StringBuilder();
		switch (this.simulator.getStatus()) {
			case AmiSim.STATUS_CONNECTED:
				text.append("Paused");
				break;
			case AmiSim.STATUS_NOT_CONNECTED:
				text.append("Disconnected");
				this.textField.setDisabled(false);
				this.hostField.setDisabled(false);
				this.seedField.setDisabled(false);
				this.startButton.setName("Start");
				this.userStarted = false;
				break;
			case AmiSim.STATUS_RUNNING:
				text.append("Running");
				break;
		}
		this.statusHtml.setHtml(text.toString());
		SH.clear(text);
		long messages = this.simulator.getMessagesCount();
		long objects = this.simulator.getObjectsCount();
		text.append("<span style='color:purple'>");
		Formatter nf = getManager().getLocaleFormatter().getNumberFormatter(0);
		nf.format(messages, text);
		text.append(" Messages&nbsp;&nbsp;");
		text.append("<span style='color:green'>");
		nf.format(objects, text);
		text.append(" Objects");
		this.countsHtml.setHtml(text.toString());
	}
	@Override
	public void onBackendCalled(PortletManager manager, Action action) {
	}
	@Override
	public void onInit(PortletManager manager, Map<String, Object> configuration, String rootId) {
	}
	@Override
	public void onPortletManagerClosed() {
	}
	@Override
	public void onPageRefreshed(PortletManager basicPortletManager) {
	}
	@Override
	public void onMetadataChanged(PortletManager basicPortletManager) {
	}
	@Override
	public void onPageLoading(PortletManager basicPortletManager, Map<String, String> attributes, HttpRequestResponse action) {

	}
}
