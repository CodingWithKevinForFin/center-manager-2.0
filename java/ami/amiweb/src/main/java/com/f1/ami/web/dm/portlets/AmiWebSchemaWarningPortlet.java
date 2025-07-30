package com.f1.ami.web.dm.portlets;

import java.util.ArrayList;
import java.util.Map;

import com.f1.ami.web.AmiWebDifferPortlet;
import com.f1.ami.web.AmiWebEditSchemaPortlet;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletFieldTitleListener;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.utils.CH;

public class AmiWebSchemaWarningPortlet extends GridPortlet implements FormPortletFieldTitleListener, FormPortletListener {

	final private FormPortlet mainForm;
	final private FormPortletButton submitButton;
	final private AmiWebEditSchemaPortlet owner;
	private ArrayList<AmiWebSchemaComprisonWarning> warnings;
	private boolean hadsubmit;

	public AmiWebSchemaWarningPortlet(PortletConfig config, ArrayList<AmiWebSchemaComprisonWarning> warnings, AmiWebEditSchemaPortlet amiWebEditSchemaPortlet) {
		super(config);
		this.warnings = warnings;
		this.owner = amiWebEditSchemaPortlet;
		this.mainForm = (new FormPortlet(generateConfig()));
		this.mainForm.getFormPortletStyle().setLabelsWidth(270);
		this.mainForm.addFormPortletListener(this);
		for (AmiWebSchemaComprisonWarning warning : warnings) {
			this.mainForm.addField(warning.getField());
		}

		this.mainForm.addTitleListener(this);
		this.submitButton = this.mainForm.addButton(new FormPortletButton("Apply Changes").addHotKey("Enter"));
		HtmlPortlet header = new HtmlPortlet(generateConfig());
		header.setHtml("Update the datamodel to match the schema from your test query results? <BR><I>(Hint: click on the <b>bold</b> messages below to see changes):");
		header.setCssStyle("_fm=center|_bg=#e2e2e2|style.padding=15px|_fs=13");
		this.addChild(header, 0, 0);
		this.addChild(this.mainForm, 0, 1);
		this.setRowSize(0, 70);
	}

	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		if (KeyEvent.ENTER.equals(keyEvent.getKey()))
			if (this.mainForm.onUserKeyEvent(keyEvent))
				return true;
		return super.onUserKeyEvent(keyEvent);
	}

	@Override
	public void onTitleClicked(FormPortlet target, FormPortletField field, int mouseX, int mouseY) {
		AmiWebSchemaComprisonWarning warning = (AmiWebSchemaComprisonWarning) field.getCorrelationData();
		AmiWebDifferPortlet diff = new AmiWebDifferPortlet(generateConfig());
		AmiWebDmTableSchema nuw = warning.getTableSchemaNuw();
		AmiWebDmTableSchema exs = warning.getTableSchemaExisting();
		diff.setText(toString(nuw), toString(exs));
		diff.setTitles("Test Result", "Current Schema");
		getManager().showDialog("Diff for " + warning.getTableSchemaName(), diff, 600, 800);
	}

	private String toString(AmiWebDmTableSchema t) {
		if (t == null)
			return "";
		StringBuilder sb = new StringBuilder();
		for (String key : CH.sort(t.getClassTypes().getVarKeys())) {
			sb.append(" ").append(this.owner.getService().getMethodFactory().forType(t.getClassTypes().getType(key))).append(" ").append(key).append("\n");
		}
		return sb.toString();
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.submitButton) {
			submit();
		}
	}

	private void submit() {
		FormPortletField field;
		this.owner.applyWarningActions(warnings);
		hadsubmit = true;
		//		owner.setWarningChangesApplied(true);
		//		owner.refreshTestDmSchema();
		//		owner.setWarningChangesApplied(false);
		close();
	}
	@Override
	public void onClosed() {
		super.onClosed();
		if (!hadsubmit) {
			for (AmiWebSchemaComprisonWarning i : warnings)
				i.setActionSelected(AmiWebSchemaComprisonWarning.ACTION_DO_NOTHING);
			this.owner.applyWarningActions(warnings);
		}

	};
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {

	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}

}
