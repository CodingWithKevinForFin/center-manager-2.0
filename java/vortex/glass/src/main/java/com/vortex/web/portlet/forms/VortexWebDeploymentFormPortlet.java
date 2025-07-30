package com.vortex.web.portlet.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.base.Action;
import com.f1.base.Getter;
import com.f1.container.ResultMessage;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.TabManager;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
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
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageDeploymentRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageDeploymentResponse;
import com.vortex.client.VortexClientBuildProcedure;
import com.vortex.client.VortexClientDeployment;
import com.vortex.client.VortexClientDeploymentSet;
import com.vortex.client.VortexClientMachine;
import com.vortex.web.VortexWebEyeService;
import com.vortex.web.portlet.forms.VortexWebMachineSelectionFormPortlet.Selection;

public class VortexWebDeploymentFormPortlet extends VortexWebMetadataFormPortlet implements FormPortletContextMenuListener, FormPortletContextMenuFactory {
	final private VortexWebEyeService service;

	final public FormPortletButton submitButton;
	private Long deploymentId;
	final public FormPortletTextField targetMuidField;
	final public FormPortletSelectField<Long> procField;
	final public FormPortletSelectField<Long> deploymentSetField;
	final public FormPortletSelectField<Boolean> shouldCopyField;
	final public FormPortletTextField targetDirectory;
	final public FormPortletTextField targetUser;
	final public FormPortletTextField startScript;
	final public FormPortletTextField descriptionField;
	final public FormPortletTextField stopScript;
	final public FormPortletTextField installScript;
	final public FormPortletTextField uninstallScript;
	final public FormPortletTextField verifyScriptCommand;
	final public FormPortletTextField autoDeleteFiles;
	final public FormPortletTextField logDirectories;
	final public FormPortletTextField scriptsDir;
	final public FormPortletTextField props;

	final private FormPortletButtonField generatedPropertiesField;
	final private FormPortletButtonField envVarsField;
	//final private FormPortletTextAreaField verifyScript;

	private PropertiesForm propertiesForm;
	private String[] muids = new String[0];

	private boolean editMode = false;

	//final private FormPortletTextAreaField backupPaths;
	//final private FormPortletSelectField<Long> backupDestination;

	public VortexWebDeploymentFormPortlet(PortletConfig config) {
		super(config, "deployment.jpg");
		setIconToAdd();
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		addButton(this.submitButton = new FormPortletButton("Create Deployment"));

		addField(new FormPortletTitleField("Deployment Associations"));
		addField(this.descriptionField = new FormPortletTextField("Description")).setValue("").setWidth(300);
		addField(this.deploymentSetField = new FormPortletSelectField<Long>(Long.class, "Deployment Set"));
		for (VortexClientDeploymentSet i : service.getAgentManager().getDeploymentSets())
			this.deploymentSetField.addOption((i.getData().getId()), i.getData().getName());

		addField(this.procField = new FormPortletSelectField<Long>(Long.class, "Build Procedure"));
		for (VortexClientBuildProcedure i : service.getAgentManager().getBuildProcedures())
			this.procField.addOption(i.getData().getId(), i.getData().getName());

		addField(new FormPortletTitleField("Deployment Target"));
		addField(this.targetMuidField = new FormPortletTextField("Target Machine").setHasButton(true)).setWidth(FormPortletTextField.WIDTH_STRETCH);
		addField(this.targetDirectory = new FormPortletTextField("Target Directory")).setValue("/opt").setWidth(FormPortletTextField.WIDTH_STRETCH);
		addField(this.targetUser = new FormPortletTextField("Target user")).setValue("root");

		addField(new FormPortletTitleField("Deployment Scripts"));
		addField(this.startScript = new FormPortletTextField("Start script path")).setValue("scripts/start.sh").setWidth(FormPortletTextField.WIDTH_STRETCH);
		addField(this.stopScript = new FormPortletTextField("Stop script path")).setValue("scripts/stop.sh").setWidth(FormPortletTextField.WIDTH_STRETCH);
		addField(this.installScript = new FormPortletTextField("Install script path")).setValue("").setWidth(FormPortletTextField.WIDTH_STRETCH);
		addField(this.uninstallScript = new FormPortletTextField("Uninstall script path")).setValue("").setWidth(FormPortletTextField.WIDTH_STRETCH);
		addField(this.verifyScriptCommand = new FormPortletTextField("Verify script path")).setValue("").setWidth(FormPortletTextField.WIDTH_STRETCH);
		addField(this.autoDeleteFiles = new FormPortletTextField("Auto-delete File(s)")).setHelp("comma delimited list of files or directories that will be removed on deployment")
				.setValue("").setWidth(FormPortletTextField.WIDTH_STRETCH);
		addField(this.logDirectories = new FormPortletTextField("log directory(s)")).setHelp("comma delimited list of log directories").setValue("")
				.setWidth(FormPortletTextField.WIDTH_STRETCH);
		addField(this.scriptsDir = new FormPortletTextField("Scripts Directory")).setValue("scripts/").setWidth(FormPortletTextField.WIDTH_STRETCH);
		//addField(this.propsField = new FormPortletTextAreaField("Properties")).setValue("");
		addField(new FormPortletTitleField("Deployment Generated data"));
		addField(this.generatedPropertiesField = new FormPortletButtonField("Generated Files")).setValue("");
		addField(this.envVarsField = new FormPortletButtonField("Env Vars")).setValue("");
		addField(this.props = new FormPortletTextField("generated properties file")).setValue("config/local.properties").setWidth(400);

		//addField(this.verifyScript = new FormPortletTextAreaField("Verify script_file contents")).setValue("");

		this.formPortlet.addMenuListener(this);
		this.formPortlet.setMenuFactory(this);
		//addField(this.backupPaths = new FormPortletTextAreaField("Backup Paths")).setValue("");

		//addField(this.backupDestination = new FormPortletSelectField<Long>(Long.class, "Backup Destination"));
		//this.backupDestination.addOption(0L, "<no backup destination>");
		//for (VortexClientBackupDestination backup : service.getAgentManager().getBackupDestinations()) {
		//VortexClientMachine machine = service.getAgentManager().getAgentMachineByUid(backup.getData().getDestinationMachineUid());
		//if (machine != null)
		//this.backupDestination.addOption(backup.getId(), backup.getData().getName() + " [" + machine.getHostName() + ":" + backup.getData().getDestinationPath() + "]");
		//}

		initMetadataFormFields(VortexAgentEntity.TYPE_DEPLOYMENT);

		this.shouldCopyField = new FormPortletSelectField<Boolean>(Boolean.class, "Copy to new Deployment Set").addOption(Boolean.TRUE, "Yes, copy").addOption(Boolean.FALSE,
				"No, do not copy");
	}
	@Override
	protected void onUserPressedButton(FormPortletButton button) {
		if (button == submitButton) {
			if (this.procField.getOptionsCount() == 0) {
				getManager().showAlert("Must have at least one build procedure defined before creating a deployment");
				return;
			}
			if (deploymentId == null && !validateTargetUnique()) {
				getManager().showAlert("Target Machine and directory combination already exists. ");
				return;
			}

			VortexDeployment[] dps = toDeployment();
			if (AH.isEmpty(dps))
				return;

			VortexEyeManageDeploymentRequest request = nw(VortexEyeManageDeploymentRequest.class);

			request.setDeployment(dps[0]);
			service.sendRequestToBackend(getPortletId(), request);
		}

		super.onUserPressedButton(button);
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		VortexEyeManageDeploymentResponse response = (VortexEyeManageDeploymentResponse) result.getAction();
		if (response.getOk())
			close();
		else {
			getManager().showAlert(response.getMessage());
		}
	}

	public VortexDeployment[] toDeployment() {
		if (hasField(shouldCopyField) && !shouldCopyField.getValue())
			return null;

		VortexDeployment[] dps = new VortexDeployment[this.muids.length];
		for (int i = 0; i < this.muids.length; i++) {
			VortexDeployment deployment = nw(VortexDeployment.class);
			dps[i] = deployment;
			deployment.setProcedureId((procField.getValue()));
			deployment.setTargetMachineUid(muids[i]);
			deployment.setTargetDirectory(targetDirectory.getValue());
			deployment.setTargetUser(targetUser.getValue());
			deployment.setGeneratedPropertiesFile(props.getValue());
			deployment.setStartScriptFile(startScript.getValue());
			deployment.setStopScriptFile(stopScript.getValue());
			deployment.setScriptsDirectory(scriptsDir.getValue());
			deployment.setDescription(descriptionField.getValue());
			//deployment.setProperties((String) generatedPropertiesField.getCorrelationData());
			deployment.setGeneratedFiles((Map<String, String>) generatedPropertiesField.getCorrelationData());
			deployment.setEnvVars((String) envVarsField.getCorrelationData());
			deployment.setDeploymentSetId((deploymentSetField.getValue()));
			deployment.setInstallScriptFile(installScript.getValue());
			deployment.setUninstallScriptFile(uninstallScript.getValue());
			deployment.setVerifyScriptFile(verifyScriptCommand.getValue());
			deployment.setAutoDeleteFiles(autoDeleteFiles.getValue());
			deployment.setLogDirectories(logDirectories.getValue());
			//TODO: options
			//deployment.setBackupPaths(backupPaths.getValue());
			//deployment.setBackupDestinationId(this.backupDestination.getValue());

			if (this.deploymentId != null) {
				deployment.setId(this.deploymentId);
			}
			if (!populateMetadata(deployment))
				return null;
		}

		return dps;
	}
	@Override
	protected void onUserChangedValue(FormPortletField<?> field, Map<String, String> attributes) {
		super.onUserChangedValue(field, attributes);
		if (hasField(shouldCopyField)) {
			if (shouldCopyField.getValue()) {
				//addFieldNoThrow(this.deploymentSetField);
				addFieldNoThrow(this.procField);
				addFieldNoThrow(this.targetMuidField);
				addFieldNoThrow(this.targetDirectory);
				addFieldNoThrow(this.targetUser);
				addFieldNoThrow(this.startScript);
				addFieldNoThrow(this.stopScript);
				addFieldNoThrow(this.generatedPropertiesField);
				addFieldNoThrow(this.props);
			} else {
				//removeFieldNoThrow(this.deploymentSetField);
				removeFieldNoThrow(this.procField);
				removeFieldNoThrow(this.targetMuidField);
				removeFieldNoThrow(this.targetDirectory);
				removeFieldNoThrow(this.targetUser);
				removeFieldNoThrow(this.startScript);
				removeFieldNoThrow(this.stopScript);
				removeFieldNoThrow(this.generatedPropertiesField);
				removeFieldNoThrow(this.props);
			}
		}
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebDeploymentFormPortlet> {

		public static final String ID = "DeploymentFormPortlet";

		public Builder() {
			super(VortexWebDeploymentFormPortlet.class);
			setUserCreatable(false);
		}

		@Override
		public VortexWebDeploymentFormPortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebDeploymentFormPortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Build Procedure Form Portlet";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	//@Override
	//public int getSuggestedHeight(PortletMetrics pm) {
	//return 450;
	//}

	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 600;
	}

	public void setDeploymentToEdit(VortexClientDeployment bp) {
		this.editMode = true;
		VortexDeployment data = bp.getData();
		setDeploymentToCopy(bp);
		this.deploymentId = data.getId();
		setIconToEdit();
		this.submitButton.setName("Update deployment");
	}
	public void setDeploymentToCopy(VortexClientDeployment bp) {
		VortexDeployment data = bp.getData();

		this.setMuid(data.getTargetMachineUid());
		this.procField.setValue((data.getProcedureId()));
		this.deploymentSetField.setValue((data.getDeploymentSetId()));
		this.targetDirectory.setValue(data.getTargetDirectory());
		this.targetUser.setValue(data.getTargetUser());
		this.startScript.setValue(data.getStartScriptFile());
		this.stopScript.setValue(data.getStopScriptFile());
		this.installScript.setValue(data.getInstallScriptFile());
		this.uninstallScript.setValue(data.getUninstallScriptFile());
		this.verifyScriptCommand.setValue(data.getVerifyScriptFile());
		this.autoDeleteFiles.setValue(data.getAutoDeleteFiles());
		this.logDirectories.setValue(data.getLogDirectories());
		//TODO: options
		this.scriptsDir.setValue(data.getScriptsDirectory());
		this.descriptionField.setValue(data.getDescription());

		Map<String, String> generatedFiles = data.getGeneratedFiles();
		if (generatedFiles == null)
			generatedFiles = new HashMap<String, String>();

		//TODO: delete
		if (SH.is(data.getProperties())) {
			Map<String, StringBuilder> sink = new HashMap<String, StringBuilder>();
			parseProperties(data.getProperties(), data.getGeneratedPropertiesFile(), sink);
			for (Entry<String, StringBuilder> e : sink.entrySet())
				generatedFiles.put(e.getKey(), e.getValue().toString());
		}

		this.generatedPropertiesField.setValue(generatedFiles.size() + " file(s)...");
		this.generatedPropertiesField.setCorrelationData(generatedFiles);
		this.envVarsField.setValue(SH.trimArray(SH.splitLines(data.getEnvVars())).length + " variable(s)...");
		this.envVarsField.setCorrelationData(data.getEnvVars());
		this.props.setValue(data.getGeneratedPropertiesFile());
		setIconToCopy();
		populateMetadataFormFields(bp.getData());

	}
	public void setDeploymentSetId(long dsid) {
		this.deploymentSetField.setValue((dsid));
	}
	//public void setDeploymentToCopyFromSet(VortexClientDeployment deployment) {
	//setDeploymentToCopy(deployment);
	//}

	public boolean validateTargetUnique() {
		String muid = this.targetMuidField.getValue();
		String dir = this.targetDirectory.getValue();
		for (VortexClientDeployment wdep : service.getAgentManager().getDeployments()) {
			VortexDeployment dep = wdep.getData();
			if (OH.eq(muid, dep.getTargetMachineUid()) && OH.eq(dir, dep.getTargetDirectory()))
				return false;
		}
		return true;
	}
	public void disableDeploymentSetField() {
		removeField(this.deploymentSetField);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if ("props".equals(button.getId())) {
			//String prop = OH.toString(CH.first(this.propertiesForm.getFormFields()).getValue());
			//this.propsField.setValue(SH.is(prop) ? prop.length() + " char(s)" : "-- no properties--");
			//this.propsField.setCorrelationData(prop);
			Map<String, String> fileNames = new HashMap<String, String>();
			for (Tab tab : this.propertiesForm.tabsPortlet.getTabs()) {
				FormPortlet fp = (FormPortlet) tab.getPortlet();
				String fileName = fp.getField("name", String.class).getValue();
				String data = fp.getField("data", String.class).getValue();
				if (SH.isnt(fileName)) {
					getManager().showAlert("Filename missing");
					return;
				}
				if (fileNames.containsKey(fileName)) {
					getManager().showAlert("Duplicate Filename: " + fileName);
					return;
				}
				fileNames.put(fileName, data);
				//String[] lines = SH.splitLines(data);
				//for (String line : lines) {
				//if (line.startsWith(">"))
				//sb.append('>');
				//sb.append(line).append(SH.CHAR_NEWLINE);
				//}
				//sb.append(SH.CHAR_NEWLINE);
			}
			//String props = sb.toString();
			this.generatedPropertiesField.setCorrelationData(fileNames);
			this.generatedPropertiesField.setValue(fileNames.size() + " file(s)...");
			this.propertiesForm.close();
			propertiesForm = null;
		} else if ("env_var".equals(button.getId())) {
			String val = portlet.getField("var", String.class).getValue();
			this.envVarsField.setValue(SH.trimArray(SH.splitLines(val)).length + " variable(s)...");
			this.envVarsField.setCorrelationData(val);
			portlet.close();
		}
		super.onButtonPressed(portlet, button);
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		if ("button_clicked".equals(action)) {
			if (node == this.envVarsField) {
				FormPortlet fp = new FormPortlet(generateConfig());
				fp.addField(new FormPortletTextAreaField("variables").setId("var").setHeight(500)).setValue((String) this.envVarsField.getCorrelationData());
				fp.addButton(new FormPortletButton("Submit").setId("env_var"));
				fp.addFormPortletListener(this);
				getManager().showDialog("Edit Environment Variables", fp, 700, 620);
			} else if (node == this.generatedPropertiesField) {
				List<Tuple2<String, String>> files = new ArrayList<Tuple2<String, String>>();
				Map<String, String> genereratedFiles = (Map<String, String>) generatedPropertiesField.getCorrelationData();
				this.propertiesForm = new PropertiesForm(generateConfig(), genereratedFiles);
				propertiesForm.buttonsPortlet.addFormPortletListener(this);
				getManager().showDialog("Properties", propertiesForm, 700, 620);
			}
		}
	}

	public static class PropertiesForm extends GridPortlet implements TabManager, FormPortletListener, ConfirmDialogListener {

		public FormPortlet buttonsPortlet;
		public TabPortlet tabsPortlet;
		private HtmlPortlet headerPortlet;

		public PropertiesForm(PortletConfig config, Map<String, String> files) {
			super(config);
			this.headerPortlet = new HtmlPortlet(generateConfig(), "", "comment_header");
			this.buttonsPortlet = new FormPortlet(generateConfig());
			this.tabsPortlet = new TabPortlet(generateConfig());
			this.tabsPortlet.getTabPortletStyle().setAddButtonText("Add File");
			this.tabsPortlet.setTabManager(this);
			for (Entry<String, String> file : files.entrySet()) {
				addFileTab(file.getKey(), file.getValue());
			}
			headerPortlet.setHtml("<div style=\"width:100%;height:100%;background-image:url('rsc/headers/" + "deployment_config.jpg"
					+ "');background-repeat:no-repeat;background-position:center;text-align:center;padding:5px 5px\"></div>");
			buttonsPortlet.addButton(new FormPortletButton("submit").setId("props"));
			addChild(headerPortlet, 0, 0);
			setRowSize(0, 120);
			addChild(tabsPortlet, 0, 1);
			setRowSize(1, 460);
			addChild(buttonsPortlet, 0, 2);
		}
		private Tab addFileTab(String name, String data) {
			FormPortlet form = new FormPortlet(generateConfig());
			form.setLabelsWidth(100);
			form.addField(new FormPortletTextField("Full File Name").setId("name")).setValue(name).setWidth(FormPortletTextField.WIDTH_STRETCH);
			form.addField(new FormPortletTextAreaField("File Contents").setId("data")).setValue(data).setHeight(400);
			form.addFormPortletListener(this);
			Tab r = tabsPortlet.addChild(SH.afterLast(name, '/'), form);
			getManager().onPortletAdded(form);
			return r;
		}
		public void onUserDeleteTab(TabPortlet tabPortlet, Tab tab) {
			ConfirmDialogPortlet deletePortlet = new ConfirmDialogPortlet(generateConfig(), "Delete file '" + tab.getTitle() + "' from config?", ConfirmDialogPortlet.TYPE_YES_NO,
					this);
			deletePortlet.setCorrelationData(tab.getLocation());
			getManager().showDialog("Delete tab", deletePortlet);
		}
		@Override
		public void onUserAddTab(TabPortlet tabPortlet) {
			this.tabsPortlet.setActiveTab(addFileTab("newfile" + tabPortlet.getChildrenCount(), "").getPortlet());
		}
		@Override
		public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		}
		@Override
		public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
			if ("name".equals(field.getId())) {
				String fileName = (String) field.getValue();
				Tab tab = tabsPortlet.getTabForPortlet(portlet);
				tab.setTitle(SH.afterLast(fileName, '/'));
			}
		}

		@Override
		public void onUserRenamedTab(TabPortlet tabPortlet, Tab tab, String newName) {
			tab.setTitle(SH.afterLast(newName, '/'));
			FormPortlet form = (FormPortlet) tab.getPortlet();
			form.getField("name", String.class).setValue(newName);
		}
		@Override
		public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

		}
		@Override
		public boolean onButton(ConfirmDialogPortlet source, String id) {
			Integer tabIndex = (Integer) source.getCorrelationData();
			if (tabIndex != null && ConfirmDialogPortlet.ID_YES.equals(id))
				tabsPortlet.removeChild(tabsPortlet.getTabAtLocation(tabIndex).getPortlet().getPortletId());
			return true;
		}
		@Override
		public void onUserMenu(TabPortlet tabPortlet, Tab tab, String menuId) {
			onUserDeleteTab(tabPortlet, tab);
		}
		@Override
		public WebMenu createMenu(TabPortlet tabPortlet, Tab tab) {
			return new BasicWebMenu(new BasicWebMenuLink("Delete", true, "delete"));
		}

	}

	private void parseProperties(String properties, String defaultFileName, Map<String, StringBuilder> sink) {
		if (SH.isnt(properties))
			return;
		String currentFile = defaultFileName;
		StringBuilder sb = sink.get(currentFile);
		if (sb == null)
			sink.put(currentFile, sb = new StringBuilder());
		String lines[] = SH.splitLines(properties);
		for (String line : lines) {
			if (SH.startsWith(line, '>')) {
				if (line.startsWith(">>"))//escape
					sb.append(line, 1, line.length()).append(SH.NEWLINE);
				else {
					currentFile = line.substring(1);
					sb = sink.get(currentFile);
					if (sb == null)
						sink.put(currentFile, sb = new StringBuilder());
				}

			} else {
				sb.append(line).append(SH.NEWLINE);
			}
		}
		if (SH.isnt(sink.get(defaultFileName)))
			sink.remove(defaultFileName);
	}

	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		if (field == this.targetMuidField) {
			VortexWebMachineSelectionFormPortlet p = new VortexWebMachineSelectionFormPortlet(generateConfig(), this.editMode ? Selection.Single : Selection.Multi) {
				@Override
				public void onSelection(String[] selectedMUIds) {
					setMuid(selectedMUIds);
				}
			};
			getManager().showDialog("hosts", p);
		}

		return null;
	}

	private final Getter<String, String> muidHostGetter = new Getter<String, String>() {

		@Override
		public String get(String key) {
			for (VortexClientMachine i : service.getAgentManager().getAgentMachinesSortedByHostname())
				if (SH.equals(i.getData().getMachineUid(), key)) {
					return i.getHostName();
				}

			return key;
		}
	};

	private void setMuid(String[] muids) {
		if (AH.isntEmpty(muids) && muids.length == 1)
			setMuid(muids[0]);
		else {
			this.targetMuidField.setValue(SH.join(", ", CH.l((Collection<String>) CH.l(muids), muidHostGetter)));
			this.muids = muids;
			fireChanged();
		}
	}

	public void setMuid(String muid) {
		this.muids = new String[] { muid };
		this.targetMuidField.setValue(muidHostGetter.get(muid));
		fireChanged();
	}
}
