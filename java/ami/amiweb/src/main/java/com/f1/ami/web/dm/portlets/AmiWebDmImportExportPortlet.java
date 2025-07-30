package com.f1.ami.web.dm.portlets;

import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.AmiWebViewConfigurationPortlet;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmManager;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;

public class AmiWebDmImportExportPortlet extends AmiWebViewConfigurationPortlet implements FormPortletListener {
	private static final Logger log = LH.get();
	final AmiWebService service;
	final private AmiWebDmManager dmManager;
	final private AmiWebDm target;
	final private FormPortletButton submitButton = new FormPortletButton("Import");

	public AmiWebDmImportExportPortlet(PortletConfig config, AmiWebDm target) {
		super(config);
		this.target = target;

		if (target != null)
			this.setConfiguration(this.target.getConfiguration());
		this.formPortlet.addButton(this.submitButton, 0);
		if (target != null && (target.isTransient() || target.isReadonlyLayout()))
			this.submitButton.setEnabled(false);
		this.formPortlet.addButton(new FormPortletButton("Cancel"), 1);

		this.service = AmiWebUtils.getService(this.getManager());
		this.dmManager = this.service.getDmManager();
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.submitButton) {
			Map<String, Object> config = this.getConfiguration();
			try {
				String alias = "";//TODO:
				if (this.target == null) {
					// create a new dm
					StringBuilder sink = new StringBuilder();
					this.dmManager.importDms(alias, config, sink, false);
					if (SH.isntEmpty(sink))
						getManager().showAlert("Error importing datamodel(s). See More Details: " + sink);
					close();
				} else {
					// "update" dm (table data will reset during init)
					String dmLbl = Caster_String.INSTANCE.cast(config.get("lbl"));
					String oldAlias = target.getAmiLayoutFullAlias();
					if (!this.target.getDmName().equals(dmLbl)) {
						this.target.setAliasDotName(""); // TODO hacky way to force init to update dm name, not efficient...
						config.put("lbl", dmLbl);
					}
					// use original alias because you are updating the dm
					this.target.init(oldAlias, config, null, new StringBuilder());
					this.service.getDmLayoutManager().onDmUpdated(dmManager, this.target);
					close();
				}
			} catch (RuntimeException e) {
				LH.warning(log, "Error importing datamodel", e);
				getManager().showAlert("Error importing datamodel. See More Details", e);
			}
		} else {
			close();
		}
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		super.onFieldValueChanged(portlet, field, attributes);
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}

}
