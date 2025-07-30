package com.f1.ami.web.rt;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.f1.ami.web.AmiWebManagers;
import com.f1.ami.web.AmiWebMenuUtils;
import com.f1.ami.web.AmiWebRealtimeProcessor;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.SH;

public class AmiWebRealtimeProcessorWizard_GRPC extends GridPortlet implements FormPortletListener, FormPortletContextMenuListener, FormPortletContextMenuFactory {

	FormPortletTextField urlField;
	FormPortletTextField stubClassField;
	FormPortletTextField metadataField;
	FormPortletTextField classPrefixField;
	FormPortletTextField primaryKeyField;
	FormPortletCheckboxField keepOutputField;
	FormPortletCheckboxField rerunOnFailureField;
	FormPortletCheckboxField rerunOnCompleteField;

	private AmiWebService service;
	private FormPortlet form;
	private FormPortletButton cancelButton;
	private FormPortletButton submitButton;
	private FormPortletTextField nameField;
	private FormPortletSelectField<String> aliasField;
	private String lowerId;
	private AmiWebRealtimeProcessor_GRPC target;

	public AmiWebRealtimeProcessorWizard_GRPC(PortletConfig config, AmiWebRealtimeProcessor target) {
		this(config);
		this.target = (AmiWebRealtimeProcessor_GRPC) target;
		this.nameField.setValue(this.target.getName());
		this.aliasField.setValue(this.target.getAlias());
		this.keepOutputField.setValue(this.target.getKeepOutput());
		this.urlField.setValue(this.target.getURL());
		this.stubClassField.setValue(this.target.getStubClass());
		this.metadataField.setValue(this.target.getMetadata());
		this.classPrefixField.setValue(this.target.getClassPrefix());
		this.rerunOnCompleteField.setValue(this.target.getRerunOnComplete());
		this.rerunOnFailureField.setValue(this.target.getRerunOnFailure());
		this.primaryKeyField.setValue(this.target.getPrimaryKey());
	}

	public AmiWebRealtimeProcessorWizard_GRPC(PortletConfig config) {
		super(config);
		this.service = AmiWebUtils.getService(this.getManager());
		this.form = new FormPortlet(generateConfig());
		this.nameField = new FormPortletTextField("Processor Id");
		this.urlField = new FormPortletTextField("Server URL");
		this.stubClassField = new FormPortletTextField("Stub Class");
		this.metadataField = new FormPortletTextField("Metadata");
		this.classPrefixField = new FormPortletTextField("Class Prefix");
		this.rerunOnFailureField = new FormPortletCheckboxField("Rerun On Failure");
		this.rerunOnCompleteField = new FormPortletCheckboxField("Rerun On Complete");
		this.primaryKeyField = new FormPortletTextField("Primary Key");

		Set<String> names = new HashSet<String>();
		for (AmiWebRealtimeProcessor i : service.getWebManagers().getRealtimeProcessors())
			names.add(SH.afterFirst(i.getRealtimeId(), ':'));
		String name = SH.getNextId("GRPC", names, 2);
		this.nameField.setValue(name);
		this.aliasField = new FormPortletSelectField<String>(String.class, "Owning Layout");
		for (String s : this.service.getLayoutFilesManager().getAvailableAliasesDown(""))
			this.aliasField.addOption(s, AmiWebUtils.formatLayoutAlias(s));
		this.form.addField(this.nameField);
		this.form.addField(this.aliasField);
		this.form.addField(this.primaryKeyField);
		this.keepOutputField = new FormPortletCheckboxField("Keep Output");
		this.form.addField(this.keepOutputField);
		this.form.addField(this.urlField);
		this.form.addField(this.stubClassField);
		this.form.addField(this.metadataField);
		this.form.addField(this.classPrefixField);
		this.form.addField(this.rerunOnFailureField);
		this.form.addField(this.rerunOnCompleteField);

		this.keepOutputField.setValue(true); // true on default
		form.addButton(this.cancelButton = new FormPortletButton("Cancel"));
		form.addButton(this.submitButton = new FormPortletButton("Submit"));
		this.form.addFormPortletListener(this);
		this.form.addMenuListener(this);
		this.form.setMenuFactory(this);
		this.addChild(this.form);
		setSuggestedSize(800, 500);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		AmiWebService service = this.service;
		AmiWebMenuUtils.processContextMenuAction(service, action, node);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.cancelButton)
			this.close();
		else if (button == this.submitButton) {
			String alias = aliasField.getValue();
			if (!SH.startsWith(lowerId, AmiWebManagers.FEED)) {
				String adn = AmiWebUtils.ari2adn(lowerId);
				if (!AmiWebUtils.isParentAliasOrSame(alias, adn)) {
					getManager().showAlert("Processor can not be in " + adn + " layout, because it would not have visibility to: " + lowerId);
					return;
				}
			}

			final String name = SH.trim(this.nameField.getValue());
			final Boolean keepOutput = this.keepOutputField.getValue();
			final String url = this.urlField.getValue();
			final String stubClass = this.stubClassField.getValue();
			final String classPrefix = this.classPrefixField.getValue();
			final String primaryKey = this.primaryKeyField.getValue();
			final String metadata = this.metadataField.getValue();
			final Boolean rerunOnFailure = this.rerunOnFailureField.getValue();
			final Boolean rerunOnComplete = this.rerunOnCompleteField.getValue();

			if (this.target != null) {
				String adn = AmiWebUtils.getFullAlias(alias, name);
				this.target.setAdn(adn);
				this.target.setKeepOutput(keepOutput);
				this.target.setURL(url);
				this.target.setClassPrefix(classPrefix);
				this.target.setMetadata(metadata);
				this.target.setRerunOnComplete(rerunOnComplete);
				this.target.setRerunOnFailure(rerunOnFailure);
				this.target.setStubClass(stubClass);
				this.target.setPrimaryKey(primaryKey);
				this.target.rebuild();
			} else {
				String adn = AmiWebUtils.getFullAlias(alias, name);
				if (this.service.getWebManagers().getAllTableTypes("").contains(AmiWebManagers.PROCESSOR + adn)) {
					getManager().showAlert("Processor name already exists");
					return;
				}
				final AmiWebRealtimeProcessor_GRPC p = new AmiWebRealtimeProcessor_GRPC(service, alias, keepOutput, url, stubClass, metadata, classPrefix, rerunOnComplete,
						rerunOnFailure, primaryKey);
				p.setAdn(adn);
				p.rebuild();
				service.getWebManagers().addProcessor(p);
			}
			close();
		}

	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}
	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		return null;
	}
}
