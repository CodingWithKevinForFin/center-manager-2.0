package com.f1.ami.web.scm;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.ami.amicommon.AmiScmAdapter;
import com.f1.ami.amicommon.AmiScmException;
import com.f1.ami.amicommon.AmiScmPlugin;
import com.f1.ami.portlets.AmiWebHeaderPortlet;
import com.f1.ami.web.AmiWebConsts;
import com.f1.ami.web.AmiWebProperties;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.AmiWebVarsManager;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiWebScmSettingsPortlet extends GridPortlet
		implements FormPortletListener, FormPortletContextMenuListener, FormPortletContextMenuFactory, AmiWebFileBrowserPortletListener {

	public static final String NONE = AmiWebConsts.ENCRYPTED_NONE;
	public static final String PLAINTEXT = AmiWebConsts.ENCRYPTED_PLAINTEXT;
	public static final String ENCRYPTED = AmiWebConsts.ENCRYPTED_ENCRYPTED;
	final private AmiWebService service;
	final private FormPortlet form;
	final private FormPortletSelectField<String> typeField;
	final private FormPortletTextField urlField;
	final private FormPortletTextField clientField;
	final private FormPortletTextField usernameField;
	final private FormPortletTextField passwordField;
	final private FormPortletTextField optionsField;
	final private FormPortletButton cancelButton;
	final private FormPortletButton submitButton;
	final private FormPortletButton testButton;
	final private FormPortletTextField filePathField;
	final private FormPortletSelectField<String> savePasswordModeField;
	final private FormPortlet scmTypeForm;
	final private FormPortlet advancedForm;
	final private AmiWebHeaderPortlet helpHeader;
	final private FormPortlet buttonsForm;
	final private TabPortlet tabs;
	private String scmType;
	private AmiWebVarsManager store;
	private PortletStyleManager_Form formStyle;

	public AmiWebScmSettingsPortlet(PortletConfig config, AmiWebService service) {
		super(config);
		this.tabs = new TabPortlet(generateConfig());
		this.scmTypeForm = new FormPortlet(generateConfig());
		this.advancedForm = new FormPortlet(generateConfig());
		this.buttonsForm = new FormPortlet(generateConfig());
		this.form = new FormPortlet(generateConfig());
		this.helpHeader = new AmiWebHeaderPortlet(generateConfig());
		this.formStyle = getManager().getStyleManager().getFormStyle();
		String helpHeaderTitle = "Source Control Settings";
		this.helpHeader.setShowSearch(false);
		this.helpHeader.setLegendWidth(30);
		this.helpHeader.updateBlurbPortletLayout(helpHeaderTitle, "");
		this.helpHeader.setInformationHeaderHeight(75);
		this.helpHeader.setShowBar(false);

		this.addChild(this.helpHeader, 0, 0);
		this.addChild(this.scmTypeForm, 0, 1);
		this.addChild(this.tabs, 0, 2);
		this.addChild(this.buttonsForm, 0, 3);
		this.tabs.addChild("Settings", this.form);
		this.tabs.addChild("Advanced", this.advancedForm);
		this.setRowSize(0, this.helpHeader.getHeaderHeight());
		this.setRowSize(1, 58);
		this.setRowSize(3, 40);
		setSuggestedSize(800, 540);

		this.service = service;
		this.store = this.service.getVarsManager();
		this.tabs.setIsCustomizable(false);
		this.tabs.getTabPortletStyle().setBackgroundColor("#F0F0F0");
		this.form.setLabelsWidth(120);
		this.scmTypeForm.setLabelsWidth(120);
		this.scmTypeForm.getFormPortletStyle().setCssStyle("_bg=#F0F0F0");

		this.typeField = this.scmTypeForm.addField(new FormPortletSelectField<String>(String.class, "Type:"));
		this.typeField.getAbsLocationH().setStartPx(20);
		this.typeField.getAbsLocationH().setSizePx(200);
		this.typeField.getAbsLocationV().setStartPx(28);
		this.typeField.getAbsLocationV().setSizePx(25);
		this.typeField.setLabelSideAlignment(FormPortletField.LABEL_SIDE_ALIGN_START);
		this.typeField.setLabelSide(FormPortletField.LABEL_SIDE_TOP);

		this.urlField = this.form.addField(new FormPortletTextField("URL:"));
		this.clientField = this.form.addField(new FormPortletTextField("Client:"));
		this.usernameField = this.form.addField(new FormPortletTextField("Username:"));
		this.passwordField = this.form.addField(new FormPortletTextField("Personal Access Token:"));
		this.passwordField.setPassword(true);
		this.savePasswordModeField = this.form.addField(new FormPortletSelectField<String>(String.class, "Save Password:"));

		this.optionsField = new FormPortletTextField("Additional Options:");
		this.filePathField = this.form.addField(new FormPortletTextField("Project Path:")).setHasButton(true);

		this.buttonsForm.addFormPortletListener(this);
		this.scmTypeForm.addFormPortletListener(this);
		this.form.addMenuListener(this);
		this.form.setMenuFactory(this);
		this.testButton = this.buttonsForm.addButton(new FormPortletButton("Test"));
		this.submitButton = this.buttonsForm.addButton(new FormPortletButton("Submit"));
		this.cancelButton = this.buttonsForm.addButton(new FormPortletButton("Cancel"));

		this.filePathField.setWidth(FormPortletTextField.WIDTH_STRETCH);
		this.urlField.setWidth(FormPortletTextField.WIDTH_STRETCH);
		this.optionsField.setWidth(FormPortletTextField.WIDTH_STRETCH);
		Map<String, AmiScmPlugin> plugins = this.service.getScmPlugins();
		this.scmType = store.getSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_TYPE);
		this.typeField.clearOptions();
		this.typeField.addOption(null, "<No Source Control>");
		for (Entry<String, AmiScmPlugin> e : plugins.entrySet())
			this.typeField.addOption(e.getKey(), e.getValue().getScmDescription());
		this.savePasswordModeField.clearOptions();
		this.savePasswordModeField.addOption(ENCRYPTED, "Encrypted (Secure)");
		this.savePasswordModeField.addOption(PLAINTEXT, "Plain Text (Unsecure)");
		this.savePasswordModeField.addOption(NONE, "Not Saved (re-entered on login)");
		prepareStyle();
		onFieldValueChanged(this.form, this.typeField, null);
	}

	private void prepareStyle() {
		if (formStyle.isUseDefaultStyling())
			return;
		this.scmTypeForm.getFormPortletStyle().setCssStyle(service.getUserFormStyleManager().getFormStyle());
		this.scmTypeForm.getFormPortletStyle().setButtonPanelStyle(service.getUserFormStyleManager().getFormButtonPanelStyle());
		this.advancedForm.getFormPortletStyle().setCssStyle(service.getUserFormStyleManager().getFormStyle());
		this.advancedForm.getFormPortletStyle().setButtonPanelStyle(service.getUserFormStyleManager().getFormButtonPanelStyle());
		this.buttonsForm.getFormPortletStyle().setCssStyle(service.getUserFormStyleManager().getFormStyle());
		this.buttonsForm.getFormPortletStyle().setButtonPanelStyle(service.getUserFormStyleManager().getFormButtonPanelStyle());
		this.form.getFormPortletStyle().setCssStyle(service.getUserFormStyleManager().getFormStyle());
		this.form.getFormPortletStyle().setButtonPanelStyle(service.getUserFormStyleManager().getFormButtonPanelStyle());

		this.typeField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.typeField.setBgColor(formStyle.getDefaultFormBgColor());
		this.typeField.setBorderColor(formStyle.getFormBorderColor());
		this.typeField.setFontColor(formStyle.getDefaultFormFontColor());
		this.typeField.setFieldFontFamily(formStyle.getDefaultFormFontFam());

		this.urlField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.urlField.setBgColor(formStyle.getDefaultFormBgColor());
		this.urlField.setBorderColor(formStyle.getFormBorderColor());
		this.urlField.setFontColor(formStyle.getDefaultFormFontColor());
		this.urlField.setFieldFontFamily(formStyle.getDefaultFormFontFam());

		this.clientField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.clientField.setBgColor(formStyle.getDefaultFormBgColor());
		this.clientField.setBorderColor(formStyle.getFormBorderColor());
		this.clientField.setFontColor(formStyle.getDefaultFormFontColor());
		this.clientField.setFieldFontFamily(formStyle.getDefaultFormFontFam());

		this.usernameField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.usernameField.setBgColor(formStyle.getDefaultFormBgColor());
		this.usernameField.setBorderColor(formStyle.getFormBorderColor());
		this.usernameField.setFontColor(formStyle.getDefaultFormFontColor());
		this.usernameField.setFieldFontFamily(formStyle.getDefaultFormFontFam());

		this.passwordField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.passwordField.setBgColor(formStyle.getDefaultFormBgColor());
		this.passwordField.setBorderColor(formStyle.getFormBorderColor());
		this.passwordField.setFontColor(formStyle.getDefaultFormFontColor());
		this.passwordField.setFieldFontFamily(formStyle.getDefaultFormFontFam());

		this.savePasswordModeField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.savePasswordModeField.setBgColor(formStyle.getDefaultFormBgColor());
		this.savePasswordModeField.setBorderColor(formStyle.getFormBorderColor());
		this.savePasswordModeField.setFontColor(formStyle.getDefaultFormFontColor());
		this.savePasswordModeField.setFieldFontFamily(formStyle.getDefaultFormFontFam());

		this.optionsField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.optionsField.setBgColor(formStyle.getDefaultFormBgColor());
		this.optionsField.setBorderColor(formStyle.getFormBorderColor());
		this.optionsField.setFontColor(formStyle.getDefaultFormFontColor());
		this.optionsField.setFieldFontFamily(formStyle.getDefaultFormFontFam());

		this.filePathField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.filePathField.setBgColor(formStyle.getDefaultFormBgColor());
		this.filePathField.setBorderColor(formStyle.getFormBorderColor());
		this.filePathField.setFontColor(formStyle.getDefaultFormFontColor());
		this.filePathField.setFieldFontFamily(formStyle.getDefaultFormFontFam());
	}

	/**
	 * Retrieve previously set values from cache.
	 **/
	private void updateFields() {
		String type = this.scmType;
		Map<String, AmiScmPlugin> plugins = this.service.getScmPlugins();

		// Init options
		//		if (type == null) {
		//			this.typeField.clearOptions();
		//			this.typeField.addOption(null, "<No Source Control>");
		//			for (Entry<String, AmiScmPlugin> e : plugins.entrySet())
		//				this.typeField.addOption(e.getKey(), e.getValue().getScmDescription());
		//		}

		//		this.savePasswordModeField.clearOptions();
		//		this.savePasswordModeField.addOption(ENCRYPTED, "Encrypted (Secure)");
		//		this.savePasswordModeField.addOption(PLAINTEXT, "Plain Text (Unsecure)");
		//		this.savePasswordModeField.addOption(NONE, "Not Saved (re-entered on login)");

		// Retrieve values from cache
		//		String type = store.getSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_TYPE);
		String url = store.getSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_URL);
		String client = store.getSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_CLIENT);
		String username = store.getSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_USERNAME);
		String passwordSaveMode = store.getSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_SAVE_PASSWORD_MODE);
		String password = store.getSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_PASSWORD);
		String options = store.getSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_OPTIONS);
		String path = store.getSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_PATH);

		Map<String, String> optionsValues = SH.splitToMap(',', '=', '\\', OH.noNull(options, ""));
		if (SH.is(type)) {
			if (!this.typeField.setValueNoThrow(type))
				getManager().showAlert("SCM Plugin not found: " + type);
			else {
				this.updateHelp();
				this.advancedForm.clearFields();
				AmiScmPlugin amiScmPlugin = plugins.get(type);
				if (amiScmPlugin != null) {
					Map<String, Object> scmOptions = amiScmPlugin.getScmOptions();
					for (Entry<String, Object> e : scmOptions.entrySet()) {
						FormPortletTextField field = new FormPortletTextField(AmiWebUtils.toPrettyName(e.getKey()));
						field.setId(e.getKey());
						// add relevant advanced fields
						this.advancedForm.addField(field);
						field.setWidth(FormPortletField.WIDTH_STRETCH);
						Map m = (Map) e.getValue();
						String help = e.getKey() + " - " + m.get(AmiScmPlugin.OPTIONS_DESC);
						field.setHelp(help);
						boolean encrypt = (boolean) CH.getOr(m, AmiScmPlugin.OPTIONS_ENC, false);
						if (encrypt)
							field.setPassword(true);
						if (optionsValues.containsKey(e.getKey())) {
							String val = optionsValues.get(e.getKey());
							if (encrypt)
								val = service.getEncrypter().decrypt(val);
							field.setValue(val);
							optionsValues.remove(e.getKey());
						}
					}
					this.advancedForm.addField(this.optionsField);
					String additionalOptions = SH.joinMap(',', '=', '\\', optionsValues); // Comma delimited, equality via = and escaped via \
					this.optionsField.setValue(additionalOptions);
				}
			}
		}
		this.urlField.setValue(url);
		this.clientField.setValue(client);
		this.usernameField.setValue(username);
		String loadPassword;
		if (ENCRYPTED.equals(passwordSaveMode))
			loadPassword = service.getEncrypter().decrypt(password);
		else if (PLAINTEXT.equals(passwordSaveMode))
			loadPassword = password;
		else
			loadPassword = null;
		this.passwordField.setValue(loadPassword);
		this.savePasswordModeField.setValueNoThrow(passwordSaveMode);
		//		this.optionsField.setValue(options);
		this.filePathField.setValue(path);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (this.submitButton == button || this.testButton == button) {
			Map<String, AmiScmPlugin> plugins = this.service.getScmPlugins();
			String type = this.typeField.getValue();
			String path = SH.trim(this.filePathField.getValue());
			if (SH.isnt(path)) {
				getManager().showAlert("Base Path required");
				return;
			} else {
				//				File file = new File(path);
				//				if (!file.isDirectory() || !file.canRead()) {
				//					getManager().showAlert("Base Path not found: " + path);
				//					return;
				//				}
			}

			if (type == null) {
				// if there is a path but type is not set.
				if (this.submitButton == button) {
					// shouldn't remove the cache
					//										store.removeSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_TYPE);
					//					store.removeSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_URL);
					//					store.removeSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_CLIENT);
					//					store.removeSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_USERNAME);
					//					store.removeSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_SAVE_PASSWORD_MODE);
					//					store.removeSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_PASSWORD);
					//					store.removeSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_OPTIONS);
					store.putSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_PATH, path);
					service.setScmAdapter(null, null);
					close();
				} else {
					getManager().showAlert("Success. Found specified base path.");
				}
				return;
			}
			AmiScmPlugin plugin = plugins.get(type);
			if (plugin == null) {
				getManager().showAlert("You must choose a source control type first<BR>(Plugins added using the " + AmiWebProperties.PROPERTY_AMI_SCM_PLUGINS + " property)");
				return;
			}
			AmiScmAdapter adapter = plugin.createScmAdapter();
			String url = this.urlField.getValue();
			String client = this.clientField.getValue();
			String username = this.usernameField.getValue();
			String passwordSaveMode = this.savePasswordModeField.getValue();
			String password = this.passwordField.getValue();
			String additionalOptions = this.optionsField.getValue();
			Map<String, String> optionsMap = SH.splitToMap(',', '=', '\\', OH.noNull(additionalOptions, ""));
			for (String f : CH.sort(this.advancedForm.getFields())) {
				FormPortletField<?> field = this.advancedForm.getField(f);
				if (!(field instanceof FormPortletTextField))
					continue;
				if (field == this.optionsField)
					continue;
				FormPortletTextField textField = (FormPortletTextField) field;
				String value = textField.getValue();
				if (SH.is(value))
					optionsMap.put(f, value);
			}
			StringBuilder optionsBuilder = new StringBuilder();
			SH.joinMap(',', '=', '\\', optionsMap, optionsBuilder); // Comma delimited, equality via = and escaped via \
			//			if (SH.is(optionsBuilder) && SH.is(additionalOptions)) {
			//				optionsBuilder.append(',');
			//				optionsBuilder.append(additionalOptions);
			//			}

			String options = optionsBuilder.toString();
			SH.clear(optionsBuilder);

			try {
				adapter.init(this.getManager().getTools(), url, client, username, SH.toCharArray(password), path, options);
				List<String> files = adapter.getFileNames(path);
				getManager().showAlert("Success. Found " + files.size() + " files in " + plugin.getScmDescription() + " under specified base path.");
			} catch (AmiScmException e) {
				getManager().showAlert("SCM Connection Failed: <B>" + e.getMessage(), e);
				return;
			}
			if (this.submitButton == button) {
				String savePassword;
				if (ENCRYPTED.equals(passwordSaveMode))
					savePassword = service.getEncrypter().encrypt(password);
				else if (PLAINTEXT.equals(passwordSaveMode))
					savePassword = password;
				else
					savePassword = null;

				// Need to encrypt values before saving
				for (String f : CH.sort(this.advancedForm.getFields())) {
					FormPortletField<?> field = this.advancedForm.getField(f);
					if (!(field instanceof FormPortletTextField))
						continue;
					if (field == this.optionsField)
						continue;
					FormPortletTextField textField = (FormPortletTextField) field;
					if (textField.isPassword()) {
						String value = textField.getValue();
						if (SH.is(value))
							optionsMap.put(f, service.getEncrypter().encrypt(value));
					}
				}
				SH.joinMap(',', '=', '\\', optionsMap, optionsBuilder); // Comma delimited, equality via = and escaped via \
				options = optionsBuilder.toString();
				// saves inputs to cache
				store.putSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_TYPE, type);
				store.putSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_URL, url);
				store.putSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_CLIENT, client);
				store.putSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_USERNAME, username);
				store.putSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_SAVE_PASSWORD_MODE, passwordSaveMode);
				store.putSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_PASSWORD, savePassword);
				store.putSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_OPTIONS, options);
				store.putSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_PATH, path);
				service.setScmAdapter(path, adapter);
				close();
			}

		} else if (this.cancelButton == button) {
			close();
		}
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (this.typeField == field) {
			this.scmType = this.typeField.getValue();
			boolean disabled = this.typeField.getValue() == null;
			this.urlField.setDisabled(disabled);
			this.clientField.setDisabled(disabled);
			this.usernameField.setDisabled(disabled);
			this.savePasswordModeField.setDisabled(disabled);
			this.passwordField.setDisabled(disabled);
			this.optionsField.setDisabled(disabled);

			updateFields();
			for (String f : this.advancedForm.getFields()) {
				this.advancedForm.getField(f).setDisabled(disabled);
			}

			updateHelp();
		}

	}

	private void updateHelp() {
		String type = typeField.getValue();
		if (type != null) {
			Map<String, AmiScmPlugin> plugins = this.service.getScmPlugins();
			AmiScmPlugin amiScmPlugin = plugins.get(type);
			this.urlField.setHelp(amiScmPlugin.getScmHelp(AmiScmPlugin.URL_HELP));
			this.clientField.setHelp(amiScmPlugin.getScmHelp(AmiScmPlugin.CLIENT_HELP));
			this.usernameField.setHelp(amiScmPlugin.getScmHelp(AmiScmPlugin.USERNAME_HELP));
			this.optionsField.setHelp(amiScmPlugin.getScmHelp(AmiScmPlugin.OPTIONS_HELP));
			this.filePathField.setHelp(amiScmPlugin.getScmHelp(AmiScmPlugin.BASE_PATH_HELP));
		} else {
			this.urlField.setHelp(null);
			this.clientField.setHelp(null);
			this.usernameField.setHelp(null);
			this.optionsField.setHelp(null);
			this.filePathField.setHelp(null);
		}
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
	}

	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		if (field == this.filePathField) {
			getManager().showDialog("Base Path",
					new AmiWebFileBrowserPortlet(generateConfig(), this, this.filePathField.getValue(), AmiWebFileBrowserPortlet.TYPE_SELECT_DIR, "*"));
		}
		return null;
	}

	@Override
	public boolean onFileSelected(AmiWebFileBrowserPortlet target, String file) {
		this.filePathField.setValue(file);
		return true;
	}

}
