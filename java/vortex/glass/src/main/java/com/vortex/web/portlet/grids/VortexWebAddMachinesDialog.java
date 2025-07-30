package com.vortex.web.portlet.grids;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.f1.base.Action;
import com.f1.base.Row;
import com.f1.base.TableListenable;
import com.f1.container.ResultMessage;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.visual.ProgressBarPortlet;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.suite.web.table.impl.MapWebCellFormatter;
import com.f1.suite.web.table.impl.ToggleButtonCellFormatter;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.GuidHelper;
import com.f1.utils.IOH;
import com.f1.utils.IntArrayList;
import com.f1.utils.SH;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.vortexcommon.msg.eye.VortexEyeNetworkScan;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeGetEyeInfoRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeGetEyeInfoResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeInstallAgentRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeInstallAgentResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunCloudInterfaceActionRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunCloudInterfaceActionResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunNetworkScanRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunNetworkScanResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunShellCommandRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunShellCommandResponse;
import com.vortex.client.VortexClientCloudInterface;
import com.vortex.client.VortexClientEntity;
import com.vortex.client.VortexClientMachine;
import com.vortex.client.VortexClientMachineListener;
import com.vortex.client.VortexClientNetAddress;
import com.vortex.client.VortexClientUtils;
import com.vortex.web.VortexWebEyeService;

public class VortexWebAddMachinesDialog extends GridPortlet implements FormPortletListener, WebContextMenuListener, WebContextMenuFactory, FormPortletContextMenuFactory,
		FormPortletContextMenuListener, VortexClientMachineListener {

	private static final byte STEP_SCAN = 1;
	private static final byte STEP_SELECT = 2;
	private static final byte STEP_CONFIG = 3;
	private static final byte STEP_VERIFY = 4;

	private static final byte OKAY = 0;
	private static final byte NOT_OKAY = 1;
	private static final byte WORKING = 2;
	private static final byte BAD = 3;

	private FormPortlet form;
	private FormPortletTextAreaField ipsField;
	//private FormPortletTextField stopIpField;
	private FormPortletTextField sshPortField;
	private VortexWebEyeService service;
	private ProgressBarPortlet progress;
	private FormPortlet buttonForm;
	private FormPortletButton nextButton;
	private FormPortletButton backButton;
	private GridPortlet scanGrid;
	private GridPortlet chooseAddressesGrid;
	private GridPortlet passwordGrid;
	private GridPortlet activeGrid;
	private byte phase;
	private FastTablePortlet machinesTablePortlet;
	private FastWebTable machinesTable;
	private FastWebTable verifyTable;
	private FormPortlet passwordForm;
	private FormPortlet keyForm;
	private FormPortlet pathForm;
	private FormPortlet userNameForm;
	private FormPortlet agentInterfaceForm;
	private FormPortlet agentVersionForm;
	private Map<Integer, HostConfig> configs = new TreeMap<Integer, HostConfig>();
	private GridPortlet verifyGrid;
	private FastTablePortlet verifyTablePortlet;
	private IdentityHashMap<VortexEyeRequest, Row> requestCorrelations = new IdentityHashMap<VortexEyeRequest, Row>();
	private HtmlPortlet headerPortlet;
	private VortexEyeGetEyeInfoResponse eyeInfo;
	private FormPortlet keyFormPortlet;
	private FormPortletButtonField activeKeyFormField;
	private IntKeyMap<Long> ipsToCloudInterface = new IntKeyMap<Long>();
	private Map<String, Row> deployUids2rows = new HashMap<String, Row>();

	public VortexWebAddMachinesDialog(PortletConfig config) {
		super(config);
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		this.form = new FormPortlet(generateConfig());
		this.buttonForm = new FormPortlet(generateConfig());
		this.buttonForm.addFormPortletListener(this);
		addChild(this.headerPortlet = new HtmlPortlet(generateConfig(), ""), 0, 0);
		nextButton = new FormPortletButton("");
		backButton = new FormPortletButton("");
		setSuggestedSize(1020, 700);

		form.setLabelsWidth(75);

		//startIpField = this.form.addField(new FormPortletTextField("Start IP").setValue("192.168.3.1"));
		//stopIpField = this.form.addField(new FormPortletTextField("End IP").setValue("192.168.3.255"));
		sshPortField = this.form.addField(new FormPortletTextField("Ssh Port").setValue("22"));
		this.ipsField = this.form.addField(new FormPortletTextAreaField("IPs")).setValue("192.168.1.0-192.168.5.255");
		this.form.addMenuListener(this);
		form.setMenuFactory(this);
		scanGrid = new GridPortlet(generateConfig());
		scanGrid.addChild(form, 0, 0);
		scanGrid.addChild(progress = new ProgressBarPortlet(generateConfig()), 0, 1);
		scanGrid.setRowSize(1, 40);
		this.service.getAgentManager().addMachineListener(this);

		MapWebCellFormatter<Byte> stateFormatter = new MapWebCellFormatter<Byte>(getManager().getTextFormatter());
		stateFormatter.addEntry(OKAY, "Yes", "_cna=portlet_icon_okay", "");
		stateFormatter.addEntry(NOT_OKAY, "No", "", "");
		stateFormatter.addEntry(BAD, "No", "_cna=portlet_icon_error", "");
		stateFormatter.addEntry(WORKING, "Working", "_cna=portlet_icon_wait", "");
		stateFormatter.setDefaultWidth(70).lockFormatter();
		{
			chooseAddressesGrid = new GridPortlet(generateConfig());
			BasicWebCellFormatter showButtonWebCellFormatter = new ToggleButtonCellFormatter("image_checkbox_checked", "image_checkbox", "Selected", "Not Selected")
					.setDefaultWidth(30).lockFormatter();
			TableListenable table = new BasicTable(new Object[] { "sel", "ip", "host", "ssh", "ping", "agent", "notactive", "data" });
			this.machinesTable = new FastWebTable(new BasicSmartTable(table), getManager().getTextFormatter());
			table.setTitle("Machines");
			machinesTable.addColumn(true, "Select", "sel", showButtonWebCellFormatter).setIsClickable(true);
			machinesTable.addColumn(true, "Ip Address", "ip", service.getBasicFormatter());
			machinesTable.addColumn(true, "Host Name", "host", service.getHostnameFormatter()).setWidth(200);
			machinesTable.addColumn(true, "Ssh Available", "ssh", stateFormatter).setWidth(100);
			machinesTable.addColumn(true, "Needs Agent", "agent", stateFormatter).setWidth(100);
			machinesTable.addColumn(true, "Agent Not Active", "notactive", stateFormatter).setWidth(100);
			machinesTable.addColumn(true, "Pingable", "ping", stateFormatter).setWidth(100);
			machinesTable.addMenuListener(this);
			machinesTable.setMenuFactory(this);
			machinesTablePortlet = new FastTablePortlet(generateConfig(), machinesTable);
			chooseAddressesGrid.addChild(machinesTablePortlet, 0, 0);
		}
		{
			passwordGrid = new GridPortlet(generateConfig());
			passwordForm = new FormPortlet(generateConfig());
			keyForm = new FormPortlet(generateConfig());
			userNameForm = new FormPortlet(generateConfig());
			agentInterfaceForm = new FormPortlet(generateConfig());
			agentVersionForm = new FormPortlet(generateConfig());
			pathForm = new FormPortlet(generateConfig());
			userNameForm.setLabelsWidth(300);
			pathForm.setLabelsWidth(0);
			passwordForm.setLabelsWidth(0);
			passwordForm.setMenuFactory(this);
			keyForm.setLabelsWidth(0);
			keyForm.setMenuFactory(this);
			agentInterfaceForm.setLabelsWidth(0);
			agentInterfaceForm.setMenuFactory(this);
			agentVersionForm.setLabelsWidth(0);
			agentVersionForm.setMenuFactory(this);
			pathForm.setMenuFactory(this);
			userNameForm.setMenuFactory(this);
			userNameForm.addMenuListener(this);
			passwordForm.addMenuListener(this);
			keyForm.addMenuListener(this);
			pathForm.addMenuListener(this);
			agentVersionForm.addMenuListener(this);
			agentInterfaceForm.addMenuListener(this);
			passwordGrid.addChild(new HtmlPortlet(generateConfig(), ""), 0, 0);
			passwordGrid.addChild(new HtmlPortlet(generateConfig(), "<center><u><b>User Name"), 1, 0);
			passwordGrid.addChild(new HtmlPortlet(generateConfig(), "<center><u><b>Password"), 2, 0);
			passwordGrid.addChild(new HtmlPortlet(generateConfig(), "<center><u><b>Key"), 3, 0);
			passwordGrid.addChild(new HtmlPortlet(generateConfig(), "<center><u><b>Eye Interface"), 4, 0);
			passwordGrid.addChild(new HtmlPortlet(generateConfig(), "<center><u><b>Agent Version"), 5, 0);
			passwordGrid.addChild(new HtmlPortlet(generateConfig(), "<center><u><b>Installation Target Path"), 6, 0);
			passwordGrid.addChild(userNameForm, 0, 1, 2, 1);
			passwordGrid.addChild(passwordForm, 2, 1);
			passwordGrid.addChild(keyForm, 3, 1);
			passwordGrid.addChild(agentInterfaceForm, 4, 1);
			passwordGrid.addChild(agentVersionForm, 5, 1);
			passwordGrid.addChild(pathForm, 6, 1);
			passwordGrid.setColSize(0, 300);
			passwordGrid.setColSize(1, 90);
			passwordGrid.setColSize(2, 90);
			passwordGrid.setColSize(3, 90);
			passwordGrid.setColSize(4, 160);
			passwordGrid.setColSize(5, 100);
			passwordGrid.setRowSize(0, 18);
		}
		{
			verifyGrid = new GridPortlet(generateConfig());
			TableListenable table = new BasicTable(new Object[] { "ip", "host", "os", "message", "status", "data" });
			this.verifyTable = new FastWebTable(new BasicSmartTable(table), getManager().getTextFormatter());
			table.setTitle("Machines");
			verifyTable.addColumn(true, "Ip Address", "ip", service.getBasicFormatter());
			verifyTable.addColumn(true, "Host Name", "host", service.getHostnameFormatter()).setWidth(150);
			verifyTable.addColumn(true, "Operating System", "os", service.getBasicFormatter()).setWidth(280);
			verifyTable.addColumn(true, "Status", "status", stateFormatter).setWidth(20);
			verifyTable.addColumn(true, "Messsage", "message", service.getBasicFormatter()).setWidth(150);
			verifyTable.addMenuListener(this);
			verifyTable.setMenuFactory(this);
			verifyTablePortlet = new FastTablePortlet(generateConfig(), verifyTable);
			verifyGrid.addChild(verifyTablePortlet, 0, 0);
		}

		addChild(buttonForm, 0, 2);
		setRowSize(0, 120);
		setRowSize(2, 40);
	}

	public void setIps(Iterable<String> ips) {
		this.ipsField.setValue(SH.join(SH.CHAR_NEWLINE, ips));
	}

	@Override
	protected void initJs() {
		if (this.eyeInfo == null)
			service.sendRequestToBackend(getPortletId(), nw(VortexEyeGetEyeInfoRequest.class));
		super.initJs();
	}

	@Override
	public void close() {
		service.getAgentManager().removeMachineListener(this);
		super.close();
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (portlet == this.keyFormPortlet) {
			FormPortletField<String> field = (FormPortletField<String>) this.keyFormPortlet.getField(CH.first(this.keyFormPortlet.getFields()));
			if (SH.isnt(field.getValue())) {
				this.activeKeyFormField.setValue("--add key--");
				this.activeKeyFormField.setCorrelationData(null);
			} else {
				this.activeKeyFormField.setValue(formatKey(field.getValue()));
				this.activeKeyFormField.setCorrelationData(field.getValue());
			}
			this.keyFormPortlet.close();
			this.activeKeyFormField = null;
			this.keyFormPortlet = null;
		}
		if (button.getName().equals("Close")) {
			close();
			return;
		}
		switch (phase) {
			case STEP_SCAN: {
				if (button == this.nextButton) {
					VortexEyeRunNetworkScanRequest req = nw(VortexEyeRunNetworkScanRequest.class);
					IntArrayList ips = new IntArrayList();
					for (String line : SH.splitLines(this.ipsField.getValue())) {
						for (String part : SH.split(",", line)) {
							part = part.trim();
							if (SH.isnt(part))
								continue;
							if (part.indexOf('-') != -1) {
								int start = IOH.ip4ToInt(IOH.parseIp4(SH.beforeFirst(part, '-').trim()));
								int end = IOH.ip4ToInt(IOH.parseIp4(SH.afterFirst(part, '-').trim()));
								for (int i = start; i <= end; i++)
									ips.add(i);
							} else {
								ips.add(IOH.ip4ToInt(IOH.parseIp4(part)));
							}
						}
					}
					req.setIp4s(ips.toIntArray());
					//req.setEndIp4(IOH.ip4ToInt(IOH.parseIp4(stopIpField.getValue())));
					req.setPortsToScan(new int[] { Integer.parseInt(sshPortField.getValue()) });
					this.service.sendRequestToBackend(getPortletId(), req);
					this.progress.setProgress(0);
					this.progress.setMessage("Scanning...");
					this.buttonForm.removeButton(nextButton);
				}
				break;
			}
			case STEP_SELECT: {
				if (button == this.backButton) {
					gotoPhase(STEP_SCAN);
					this.progress.setProgress(0);
					this.progress.setMessage("");
				} else if (button == this.nextButton) {
					List<VortexEyeNetworkScan> selected = new ArrayList<VortexEyeNetworkScan>();
					selected.clear();
					for (Row row : this.machinesTable.getTable().getRows()) {
						if (Boolean.TRUE.equals(row.get("sel", Boolean.class))) {
							selected.add(row.get("data", VortexEyeNetworkScan.class));
						}
					}
					if (selected.size() == 0) {
						getManager().showAlert("You must selected at least one machine");
						return;
					} else {
						this.passwordForm.clearFields();
						this.keyForm.clearFields();
						this.userNameForm.clearFields();
						this.agentInterfaceForm.clearFields();
						this.agentVersionForm.clearFields();
						this.userNameForm.clearFields();
						this.pathForm.clearFields();
						HashMap<Integer, HostConfig> oldConfigs = new HashMap<Integer, HostConfig>(configs);
						configs.clear();
						for (VortexEyeNetworkScan i : selected) {
							FormPortletTextField unForm = this.userNameForm.addField(new FormPortletTextField(formatName(i))).setHelp(IOH.formatIp(i.getIp4())).setWidth(80);
							FormPortletTextField pwForm = this.passwordForm.addField(new FormPortletTextField("")).setPassword(true).setWidth(80);
							FormPortletButtonField kyForm = this.keyForm.addField(new FormPortletButtonField("")).setValue("--add key--");
							FormPortletSelectField<String> aiForm = this.agentInterfaceForm.addField(new FormPortletSelectField<String>(String.class, ""));
							FormPortletSelectField<String> avForm = this.agentVersionForm.addField(new FormPortletSelectField<String>(String.class, ""));
							for (String av : eyeInfo.getAvailableAgentVersions())
								avForm.addOption(av, av);
							for (Entry<String, String> ai : eyeInfo.getAvailableAgentInterfaces().entrySet())
								aiForm.addOption(ai.getKey(), ai.getValue());
							FormPortletTextField pthForm = this.pathForm.addField(new FormPortletTextField("")).setValue(eyeInfo.getAgentDefaultTargetDirectory())
									.setWidth(FormPortletTextField.WIDTH_STRETCH);

							Long ciid = this.ipsToCloudInterface.get(i.getIp4());
							if (ciid != null) {
								VortexClientCloudInterface ci = service.getAgentManager().getCloudInterface(ciid);
								if (ci != null) {
									kyForm.setValue(formatKey(ci.getKey()));
									kyForm.setCorrelationData(ci.getKey());
									pwForm.setValue(ci.getPassword());
									unForm.setValue(ci.getData().getUserName());
								}
							}

							HostConfig old = oldConfigs.get(i.getIp4());
							configs.put(i.getIp4(), new HostConfig(i, unForm, pwForm, kyForm, aiForm, avForm, pthForm));
							if (old != null) {
								unForm.setValue(old.userNameForm.getValue());
								pwForm.setValue(old.passwordForm.getValue());
								pthForm.setValue(old.pathForm.getValue());
							}
						}
						gotoPhase(STEP_CONFIG);

					}
				}
				break;
			}
			case STEP_CONFIG: {
				if (button == this.backButton) {
					gotoPhase(STEP_SELECT);
				} else if (button == this.nextButton) {
					for (HostConfig i : this.configs.values()) {
						if (SH.isnt(i.passwordForm.getValue()) && SH.isnt(i.keyForm.getCorrelationData())) {
							getManager().showAlert("Password missing for " + i.describe());
						} else if (SH.isnt(i.userNameForm.getValue())) {
							getManager().showAlert("User name missing for " + i.describe());
						} else if (SH.isnt(i.pathForm.getValue())) {
							getManager().showAlert("Path missing for " + i.describe());
						} else
							continue;
						return;
					}
					this.verifyTablePortlet.clearRows();
					this.requestCorrelations.clear();
					for (HostConfig config : configs.values()) {
						Row row = this.verifyTablePortlet.addRow(IOH.formatIp(config.networkScan.getIp4()), config.networkScan.getHostname(), config.operatingSystem, "Logging In",
								WORKING, config);
						VortexEyeRunShellCommandRequest req = nw(VortexEyeRunShellCommandRequest.class);
						req.setTimeoutMs(10000);
						req.setCommands(CH.l("uname -s -r", "ls " + config.pathForm.getValue()));
						req.setStdins(null);
						req.setUsername(config.userNameForm.getValue());
						req.setPassword(VortexClientUtils.encryptString(config.passwordForm.getValue()));
						if (SH.is(config.keyForm.getCorrelationData()))
							req.setPublicKeyData(VortexClientUtils.encryptString((String) config.keyForm.getCorrelationData()));
						req.setHostName(IOH.formatIp(config.networkScan.getIp4()));
						this.requestCorrelations.put(req, row);
						service.sendRequestToBackend(getPortletId(), req);
					}
					verifyTable.sortRows("ip", false, false, true);

					gotoPhase(STEP_VERIFY);
				}
				break;
			}
			case STEP_VERIFY: {
				if (button == this.backButton) {
					gotoPhase(STEP_CONFIG);
				} else if (button == this.nextButton) {
					this.requestCorrelations.clear();
					this.deployUids2rows.clear();
					for (Row row : this.verifyTable.getTable().getRows())
						if (row.get("status", Byte.class) == OKAY) {
							row.put("status", WORKING);
							row.put("message", "Installing...");
							HostConfig config = row.get("data", HostConfig.class);
							VortexEyeInstallAgentRequest req = nw(VortexEyeInstallAgentRequest.class);
							req.setTargetPath(config.pathForm.getValue());
							req.setUsername(config.userNameForm.getValue());
							req.setAgentInterface(config.agentInterfaceForm.getValue());
							req.setAgentVersion(config.agentVersionForm.getValue());
							req.setPassword(VortexClientUtils.encryptString(config.passwordForm.getValue()));
							if (SH.is(config.keyForm.getCorrelationData())) {
								req.setPublicKeyData(VortexClientUtils.encryptString((String) config.keyForm.getCorrelationData()));
							}
							req.setDeployUid(GuidHelper.getGuid());
							this.deployUids2rows.put(req.getDeployUid(), row);
							req.setHostName(IOH.formatIp(config.networkScan.getIp4()));
							req.setPort(SH.parseInt(this.sshPortField.getValue()));
							this.requestCorrelations.put(req, row);
							service.sendRequestToBackend(getPortletId(), req);
						}
					this.buttonForm.clearButtons();

				}
			}
		}
	}

	private String formatKey(String value) {
		if (value == null)
			return "";
		value = value.trim();
		value = SH.replaceAll(value, '\n', "");
		value = SH.stripPrefix(value, "-----BEGIN RSA PRIVATE KEY-----", false);
		value = SH.stripSuffix(value, "-----END RSA PRIVATE KEY-----", false);
		value = SH.replaceAll(value, '=', "");
		value = value.trim();
		return SH.dddMiddle(value, 9);
	}

	private String formatName(VortexEyeNetworkScan i) {
		if (SH.isnt(i.getHostname()))
			return IOH.formatIp(i.getIp4());
		StringBuilder sb = new StringBuilder();
		SH.dddMiddle(i.getHostname(), 45, sb);
		return sb.toString();
	}

	static private class HostConfig {
		public String operatingSystem;
		final public VortexEyeNetworkScan networkScan;
		final public FormPortletTextField userNameForm;
		final public FormPortletTextField passwordForm;
		final public FormPortletTextField pathForm;
		final public FormPortletSelectField<String> agentInterfaceForm;
		final public FormPortletSelectField<String> agentVersionForm;
		final public FormPortletButtonField keyForm;
		public HostConfig(VortexEyeNetworkScan networkScan, FormPortletTextField usernameForm, FormPortletTextField passwordForm, FormPortletButtonField keyForm,
				FormPortletSelectField<String> agentInterfaceForm, FormPortletSelectField<String> avForm, FormPortletTextField pathForm) {
			this.networkScan = networkScan;
			this.userNameForm = usernameForm;
			this.passwordForm = passwordForm;
			this.keyForm = keyForm;
			this.agentInterfaceForm = agentInterfaceForm;
			this.agentVersionForm = avForm;
			this.pathForm = pathForm;
		}
		public String describe() {
			if (networkScan.getHostname() == null)
				return IOH.formatIp(networkScan.getIp4());
			else
				return IOH.formatIp(networkScan.getIp4()) + " (" + networkScan.getHostname() + ")";
		}
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		if (result.getAction() instanceof VortexEyeRunCloudInterfaceActionResponse) {
			VortexEyeRunCloudInterfaceActionResponse res = (VortexEyeRunCloudInterfaceActionResponse) result.getAction();
			VortexEyeRunCloudInterfaceActionRequest req = (VortexEyeRunCloudInterfaceActionRequest) result.getRequestMessage().getAction();
			for (String ipstr : res.getValues()) {
				this.ipsToCloudInterface.put(IOH.ip4ToInt(IOH.parseIp4(ipstr)), req.getCloudInterfaceId());
			}
			if (res.getOk()) {
				String ips = this.ipsField.getValue().trim();
				if (ips.length() > 0 && !ips.endsWith("\n"))
					ips += "\n";
				ips += SH.join(", ", res.getValues());
				this.ipsField.setValue(ips);
			} else {
				getManager().showAlert(res.getMessage());
			}
		} else if (result.getAction() instanceof VortexEyeGetEyeInfoResponse) {
			VortexEyeGetEyeInfoResponse res = (VortexEyeGetEyeInfoResponse) result.getAction();
			this.eyeInfo = res;
			gotoPhase(STEP_SCAN);
		} else if (result.getAction() instanceof VortexEyeInstallAgentResponse) {
			VortexEyeInstallAgentResponse res = (VortexEyeInstallAgentResponse) result.getAction();
			VortexEyeInstallAgentRequest req = (VortexEyeInstallAgentRequest) result.getRequestMessage().getAction();
			Row row = this.requestCorrelations.remove(req);
			if (res.getOk())
				row.put("message", "Installed, Waiting for connection");
			else {
				row.put("status", BAD);
				row.put("message", res.getMessage());
			}
			if (requestCorrelations.size() == 0) {
				this.buttonForm.addButton(backButton);
				this.buttonForm.addButton(nextButton);
				backButton.setName("<< Back");
				nextButton.setName("Close");
			}

		} else if (result.getAction() instanceof VortexEyeRunShellCommandResponse) {
			VortexEyeRunShellCommandResponse res = (VortexEyeRunShellCommandResponse) result.getAction();
			int exitCode = res.getExitCodes()[0];
			byte[] stdout = res.getStdouts().get(0);
			VortexEyeRunShellCommandRequest req = (VortexEyeRunShellCommandRequest) result.getRequestMessage().getAction();
			Row row = this.requestCorrelations.remove(req);
			HostConfig config = row.get("data", HostConfig.class);
			boolean ok = false;
			if (!res.getOk()) {
				row.put("message", res.getMessage());
				row.put("status", BAD);
			} else {
				switch (exitCode) {
					case 0:
						row.put("os", new String(stdout));
						int lsExitCode = res.getExitCodes()[1];
						if (lsExitCode == 0) {
							row.put("message", "Ready to install over existing");
							ok = true;
						} else {
							row.put("message", "Ready to Install");
							ok = true;
						}
						break;
					case VortexEyeRunShellCommandResponse.EXIT_CODE_AUTH_FAILED:
						row.put("message", "Login failed: " + req.getUsername());
						break;
					case VortexEyeRunShellCommandResponse.EXIT_CODE_UNKNOWN_HOST:
						row.put("message", "Bad host: " + req.getHostName());
						break;
				}
			}
			row.put("status", ok ? OKAY : BAD);

			if (requestCorrelations.size() == 0) {
				ok = true;
				for (Row row2 : this.verifyTable.getTable().getRows())
					if (row2.get("status", Byte.class) != OKAY) {
						ok = false;
						break;
					}

				if (ok) {
					this.buttonForm.addButton(backButton);
					this.buttonForm.addButton(nextButton);
					backButton.setName("<< Back");
					nextButton.setName("Install " + this.verifyTable.getRowsCount() + " Agent(s) >>");
					this.headerPortlet.setHtml("<center><img src='rsc/headers/deploy_agent_5.gif'/>");
				} else {
					this.buttonForm.addButton(backButton);
					backButton.setName("<< Go Back and Corrent Errors");
				}
			}
		} else if (result.getAction() instanceof VortexEyeRunNetworkScanResponse) {
			VortexEyeRunNetworkScanResponse res = (VortexEyeRunNetworkScanResponse) result.getAction();
			if (result.getIsIntermediateResult()) {
				progress.setMessage("Scanning... " + res.getMessage());
				progress.setProgress(res.getProgress());
			} else {
				progress.setProgress(1);
				progress.setMessage("Scan Complete. " + res.getResults().size() + " Address(es) Found.");
				if (res.getResults().size() == 0) {
					gotoPhase(STEP_SCAN);
					getManager().showAlert("No machines found in that IP range");
				} else {
					Map<String, Boolean> addresses = new HashMap<String, Boolean>();
					for (VortexClientMachine machine : this.service.getAgentManager().getAgentMachines()) {
						addresses.put(machine.getHostName(), machine.getIsRunning());
						for (VortexClientNetAddress address : machine.getNetAddresses()) {
							addresses.put(address.getData().getAddress(), machine.getIsRunning());
						}
					}
					addresses.remove(null);
					addresses.remove("");
					this.machinesTablePortlet.clearRows();
					for (VortexEyeNetworkScan row : res.getResults()) {
						String ip = IOH.formatIp(IOH.intToIp4(row.getIp4()));
						Boolean running = addresses.get(ip);
						if (running == null)
							running = addresses.get(row.getHostname());
						boolean needsAgent = (running == null);
						boolean agentNotRunning = needsAgent || Boolean.FALSE.equals(running);
						boolean ssh = AH.isntEmpty(row.getPortsFound());
						this.machinesTablePortlet.addRow(ssh ? (needsAgent) : null, ip, row.getHostname(), ssh ? OKAY : NOT_OKAY, row.getPingable() ? OKAY : NOT_OKAY,
								needsAgent ? OKAY : NOT_OKAY, agentNotRunning ? OKAY : NOT_OKAY, row);
					}
					machinesTable.sortRows("sel", false, false, false);
					machinesTable.sortRows("ip", false, false, true);
					gotoPhase(STEP_SELECT);
				}
			}
		}
	}
	private void gotoPhase(byte phase) {

		this.buttonForm.clearButtons();
		GridPortlet activeGrid = null;
		switch (phase) {
			case STEP_SCAN:
				activeGrid = scanGrid;
				this.buttonForm.addButton(nextButton);
				nextButton.setName("Scan >>");
				break;
			case STEP_SELECT:
				activeGrid = chooseAddressesGrid;
				this.buttonForm.addButton(backButton);
				this.buttonForm.addButton(nextButton);
				nextButton.setName("Next >>");
				backButton.setName("<< Back");
				break;
			case STEP_CONFIG:
				activeGrid = passwordGrid;
				this.buttonForm.addButton(backButton);
				this.buttonForm.addButton(nextButton);
				nextButton.setName("Next >>");
				backButton.setName("<< Back");
				break;
			case STEP_VERIFY:
				activeGrid = verifyGrid;
				break;
		}
		if (this.activeGrid != null)
			removeChild(this.activeGrid.getPortletId());
		if (activeGrid != null) {
			getManager().onPortletAdded(activeGrid);
			addChild(activeGrid, 0, 1);
		}
		this.phase = phase;
		this.headerPortlet.setHtml("<center><img src='rsc/headers/deploy_agent_" + (this.phase) + ".gif'/>");
		this.activeGrid = activeGrid;
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		if (table == this.machinesTable) {
			if ("select".equals(action)) {
				for (Row row : table.getSelectedRows())
					if (row.get("sel") != null)
						row.put("sel", true);
			} else if ("deselect".equals(action)) {
				for (Row row : table.getSelectedRows())
					if (row.get("sel") != null)
						row.put("sel", false);
			}
		}

	}
	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
		if (table == this.machinesTable) {
			Boolean val = row.get("sel", Boolean.class);
			if (val != null)
				row.put("sel", !val);
			else
				getManager().showAlert("Only machines that are listening on the ssh port and don't already have an agent may be selected");
		}

	}

	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
	}

	@Override
	public void onVisibleRowsChanged(FastWebTable fastWebTable) {
	}

	@Override
	public WebMenu createMenu(WebTable table) {
		if (table == this.machinesTable) {
			boolean showDeselect = false;
			boolean showSelect = false;
			for (Row row : table.getSelectedRows()) {
				Boolean val = row.get("sel", Boolean.class);
				if (val == null)
					return null;
				else if (val)
					showDeselect = true;
				else
					showSelect = true;
			}
			if (showDeselect || showSelect) {
				BasicWebMenu r = new BasicWebMenu();
				if (showSelect)
					r.addChild(new BasicWebMenuLink("Select", true, "select"));
				if (showDeselect)
					r.addChild(new BasicWebMenuLink("Deselect", true, "deselect"));
				return r;
			}
		}
		return null;
	}

	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		if (field == this.ipsField) {
			BasicWebMenu r = new BasicWebMenu();
			for (VortexClientCloudInterface ci : service.getAgentManager().getCloudInterfaces()) {
				r.addChild(new BasicWebMenuLink("Add Addresses from Cloud: " + ci.getData().getDescription(), true, "add_ci" + ci.getId()));
			}
			return r;
		}
		String description = null;
		if (formPortlet == this.passwordForm)
			description = "password";
		else if (formPortlet == this.userNameForm)
			description = "username";
		else if (formPortlet == this.agentInterfaceForm)
			description = "interface";
		else if (formPortlet == this.agentVersionForm)
			description = "version";
		else if (formPortlet == this.pathForm)
			description = "path";
		else if (formPortlet == this.keyForm)
			description = "key";
		else
			return null;
		BasicWebMenu r = new BasicWebMenu();
		if (formPortlet.getFields().size() > 1) {
			boolean above = false, below = false;
			if (formPortlet.getFieldLocation(field) > 0)
				above = true;
			if (formPortlet.getFieldLocation(field) < formPortlet.getFields().size() - 1)
				below = true;
			if (above)
				r.addChild(new BasicWebMenuLink("Fill above with this " + description, true, "fillup"));
			if (below)
				r.addChild(new BasicWebMenuLink("Fill below with this " + description, true, "filldn"));
			if (above && below)
				r.addChild(new BasicWebMenuLink("Fill all with this " + description, true, "fillall"));
		}
		return r;
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		if (action.equals("button_clicked")) {
			this.keyFormPortlet = new FormPortlet(generateConfig());
			FormPortletTextAreaField textField = keyFormPortlet.addField(new FormPortletTextAreaField("Key Contents:")).setHeight(500);
			keyFormPortlet.setLabelsWidth(50);
			keyFormPortlet.addButton(new FormPortletButton("Submit"));
			keyFormPortlet.addFormPortletListener(this);
			this.activeKeyFormField = (FormPortletButtonField) node;
			textField.setValue((String) this.activeKeyFormField.getCorrelationData());
			getManager().showDialog("Add Key", keyFormPortlet, 800, 800);
		} else if (action.startsWith("add_ci")) {
			long id = SH.parseLong(SH.stripPrefix(action, "add_ci", true));
			VortexEyeRunCloudInterfaceActionRequest req = nw(VortexEyeRunCloudInterfaceActionRequest.class);
			req.setAction(VortexEyeRunCloudInterfaceActionRequest.ACTION_GET_HOST_NAMES);
			req.setCloudInterfaceId(id);
			this.service.sendRequestToBackend(getPortletId(), req);
			return;
		}
		String value = (String) node.getValue();
		Object cd = node.getCorrelationData();
		int loc = portlet.getFieldLocation(node);
		if ("filldn".equals(action)) {
			for (String id : portlet.getFields()) {
				FormPortletField<String> f = (FormPortletField<String>) portlet.getField(id);
				if (portlet.getFieldLocation(f) > loc) {
					f.setValue(value);
					f.setCorrelationData(cd);
				}
			}
		} else if ("fillup".equals(action)) {
			for (String id : portlet.getFields()) {
				FormPortletField<String> f = (FormPortletField<String>) portlet.getField(id);
				if (portlet.getFieldLocation(f) < loc) {
					f.setValue(value);
					f.setCorrelationData(cd);
				}
			}
		} else if ("fillall".equals(action)) {
			for (String id : portlet.getFields()) {
				FormPortletField<String> f = (FormPortletField<String>) portlet.getField(id);
				f.setValue(value);
				f.setCorrelationData(cd);
			}
		}
	}
	@Override
	public void onMachineActive(VortexClientMachine machine) {
		onMachineAdded(machine);
	}
	@Override
	public void onMachineAdded(VortexClientMachine machine) {
		if (this.phase == STEP_VERIFY) {
			String uid = machine.getAgentDetail("deployuid");
			Row row = this.deployUids2rows.remove(uid);
			if (row != null) {
				row.put("status", OKAY);
				row.put("message", "Installed and Connected to eye");
				if (this.deployUids2rows.isEmpty())
					this.headerPortlet.setHtml("<center><img src='rsc/headers/deploy_agent_6.gif'/>");
			}
		}

	}
	@Override
	public void onMachineUpdated(VortexClientMachine machine) {
		if (this.phase == STEP_VERIFY) {
			String uid = machine.getAgentDetail("deployuid");
			Row row = this.deployUids2rows.remove(uid);
			if (row != null) {
				row.put("status", OKAY);
				row.put("message", "Installed and Connected to eye");
				if (this.deployUids2rows.isEmpty())
					this.headerPortlet.setHtml("<center><img src='rsc/headers/deploy_agent_6.gif'/>");
			}
		}
	}
	@Override
	public void onMachineStale(VortexClientMachine machine) {
	}
	@Override
	public void onMachineRemoved(VortexClientMachine machine) {
	}
	@Override
	public void onMachineEntityAdded(VortexClientEntity<?> node) {
	}
	@Override
	public void onMachineEntityUpdated(VortexClientEntity<?> node) {
	}
	@Override
	public void onMachineEntityRemoved(VortexClientEntity<?> node) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCellMousedown(WebTable table, Row row, WebColumn col) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNoSelectedChanged(FastWebTable fastWebTable) {
		// TODO Auto-generated method stub

	}
}
