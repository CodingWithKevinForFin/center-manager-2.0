package com.f1.ami.web.realtimetree;

import java.util.HashSet;
import java.util.Set;

import com.f1.ami.web.tree.AmiWebTreeSettingsPortlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletMultiSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.CH;
import com.f1.utils.SH;

public class AmiWebRealtimeTreeEditSettingsPortlet extends AmiWebTreeSettingsPortlet<AmiWebRealtimeTreePortlet> {

	private FormPortletMultiSelectField<String> typesField;

	public AmiWebRealtimeTreeEditSettingsPortlet(PortletConfig config, AmiWebRealtimeTreePortlet treegrid) {
		super(config, treegrid);

		FormPortlet settingsForm = getSettingsForm();

		// Change data type - feed
		settingsForm.addField(new FormPortletTitleField("Types"));
		typesField = settingsForm.addField(new FormPortletMultiSelectField<String>(String.class, "").setSize(300).setWidth(400));

		Set<String> types = new HashSet<String>();
		types.addAll(treegrid.getLowerRealtimeIds());
		for (String s : CH.sort(types, SH.COMPARATOR_CASEINSENSITIVE_STRING))
			typesField.addOption(s, s);

		for (String key : treegrid.getService().getWebManagers().getAllTableTypes(this.treegrid.getAmiLayoutFullAlias())) {
			if (this.typesField.containsOption(key))
				continue;
			this.typesField.addOption(key, key);
		}
		typesField.setValue(treegrid.getLowerRealtimeIds());

	}

	@Override
	protected boolean verifyChanges() {
		Set<String> selected = typesField.getValue();
		if (CH.isEmpty(selected)) {
			getManager().showAlert("Must select atleast one type");
			return false;
		}
		return super.verifyChanges();
	}

	@Override
	protected void submitChanges() {
		Set<String> selected = typesField.getValue();
		if (CH.isEmpty(selected)) {
			getManager().showAlert("Must select atleast one type");
			return;
		}

		super.submitChanges();
		treegrid.setDataTypes(selected);
		treegrid.clearAmiData();
		treegrid.rebuildAmiData();
	}

}
