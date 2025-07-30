package com.f1.ami.web.rt;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.f1.ami.web.AmiWebManagers;
import com.f1.ami.web.AmiWebMenuUtils;
import com.f1.ami.web.AmiWebRealtimeProcessor;
import com.f1.ami.web.AmiWebScriptManagerForLayout;
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

public class AmiWebRealtimeProcessorWizard_BPIPE extends GridPortlet implements FormPortletListener, FormPortletContextMenuListener, FormPortletContextMenuFactory {

	FormPortletTextField appNameField;
	FormPortletTextField hostField;
	FormPortletTextField sessionIDField;
	FormPortletTextField portField;
	FormPortletTextField reconnectField;
	FormPortletTextField authField;
	FormPortletTextField optionsField;
	FormPortletCheckboxField keepOutputField;

	private AmiWebService service;
	private FormPortlet form;
	private FormPortletButton cancelButton;
	private FormPortletButton submitButton;
	private FormPortletTextField nameField;
	private FormPortletSelectField<String> aliasField;
	private String lowerId;
	private AmiWebRealtimeProcessor_BPIPE target;

	public AmiWebRealtimeProcessorWizard_BPIPE(PortletConfig config, AmiWebRealtimeProcessor target) {
		this(config);
		this.target = (AmiWebRealtimeProcessor_BPIPE) target;
		this.nameField.setValue(this.target.getName());
		this.aliasField.setValue(this.target.getAlias());
		this.optionsField.setValue(this.target.getOptions());
		this.keepOutputField.setValue(this.target.getKeepOutput());
	}

	public AmiWebRealtimeProcessorWizard_BPIPE(PortletConfig config) {
		super(config);
		this.service = AmiWebUtils.getService(this.getManager());
		this.form = new FormPortlet(generateConfig());
		this.nameField = new FormPortletTextField("Processor Id");
		Set<String> names = new HashSet<String>();
		for (AmiWebRealtimeProcessor i : service.getWebManagers().getRealtimeProcessors())
			names.add(SH.afterFirst(i.getRealtimeId(), ':'));
		String name = SH.getNextId("BPIPE", names, 2);
		this.nameField.setValue(name);
		this.aliasField = new FormPortletSelectField<String>(String.class, "Owning Layout");
		Set<String> t = new HashSet<String>();
		for (String s : this.service.getLayoutFilesManager().getAvailableAliasesDown(""))
			this.aliasField.addOption(s, AmiWebUtils.formatLayoutAlias(s));
		this.form.addField(this.nameField);
		this.form.addField(this.aliasField);
		this.optionsField = new FormPortletTextField("Options");
		this.keepOutputField = new FormPortletCheckboxField("Keep Output");
		this.form.addField(this.optionsField);
		this.form.addField(this.keepOutputField);
		this.keepOutputField.setValue(true); // true on default
		form.addButton(this.cancelButton = new FormPortletButton("Cancel"));
		form.addButton(this.submitButton = new FormPortletButton("Submit"));
		this.form.addFormPortletListener(this);
		this.form.addMenuListener(this);
		this.form.setMenuFactory(this);
		this.addChild(this.form);
		setSuggestedSize(800, 500);
	}

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
			AmiWebScriptManagerForLayout sm = service.getScriptManager(alias);
			if (!SH.startsWith(lowerId, AmiWebManagers.FEED)) {
				String adn = AmiWebUtils.ari2adn(lowerId);
				if (!AmiWebUtils.isParentAliasOrSame(alias, adn)) {
					getManager().showAlert("Processor can not be in " + adn + " layout, because it would not have visibility to: " + lowerId);
					return;
				}
			}
			String name = SH.trim(this.nameField.getValue());
			Boolean keepOutput = this.keepOutputField.getValue();
			String options = this.optionsField.getValue();
			String adn = AmiWebUtils.getFullAlias(alias, name);
			String currentName = null;
			if (target != null)
				currentName = AmiWebManagers.PROCESSOR + target.getName();
			HashSet<String> toCheckNames = new HashSet<>();
			toCheckNames.addAll(this.service.getWebManagers().getAllTableTypes(""));

			if (currentName != null && toCheckNames.contains(currentName))
				toCheckNames.remove(currentName);

			if (toCheckNames.contains(AmiWebManagers.PROCESSOR + adn)) {
				getManager().showAlert("Processor name already exists");
				return;
			}

			if (this.target != null) {
				this.target.setAdn(adn);
				this.target.setOptions(options);
				this.target.setKeepOutput(keepOutput);
				this.target.rebuild();
			} else {
				AmiWebRealtimeProcessor_BPIPE p = new AmiWebRealtimeProcessor_BPIPE(service, alias, options, keepOutput);
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
