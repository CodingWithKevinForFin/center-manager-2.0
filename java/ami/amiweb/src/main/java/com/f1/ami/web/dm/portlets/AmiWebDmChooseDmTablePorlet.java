package com.f1.ami.web.dm.portlets;

import java.util.List;
import java.util.Map;

import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.AmiWebDmTablesetSchema;
import com.f1.ami.web.graph.AmiWebGraphNode;
import com.f1.ami.web.graph.AmiWebGraphNode_Datamodel;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.utils.CH;

public class AmiWebDmChooseDmTablePorlet extends GridPortlet implements ConfirmDialogListener, FormPortletListener, AmiWebDmTreeListener {

	public interface ChooseDmListener {

		void onDmSelected(AmiWebDmTableSchema selectedDmTable);

	}

	private AmiWebDmTreePortlet dmPortlet;
	private AmiWebDmTablesetSchema selectedDm;
	private AmiWebDmTableSchema selectedDmTable;
	private ChooseDmListener listener;
	private FormPortletButton selectButton;
	private boolean allowNoSelection;

	public AmiWebDmChooseDmTablePorlet(PortletConfig config, String dmName, ChooseDmListener listener, boolean allowModifications, String baseAlias) {
		super(config);
		AmiWebService service = AmiWebUtils.getService(this.getManager());
		this.dmPortlet = new AmiWebDmTreePortlet(generateConfig(), service, baseAlias, allowModifications, dmName);
		FormPortlet buttonsForm = new FormPortlet(generateConfig());
		buttonsForm.addFormPortletListener(this);
		this.selectButton = buttonsForm.addButton(new FormPortletButton("Next"));
		this.addChild(dmPortlet);
		this.addChild(buttonsForm, 0, 1);
		this.setRowSize(1, 35);
		this.dmPortlet.setOverrideDblClick(this);
		this.dmPortlet.expandDatamodels();
		this.listener = listener;
	}
	public AmiWebDmChooseDmTablePorlet(PortletConfig config, String dmName, ChooseDmListener listener, String baseAlias) {
		this(config, dmName, listener, true, baseAlias);
	}

	@Override
	public void onDoubleClicked(List<AmiWebGraphNode<?>> nodes) {
		if (nodes.size() != 1)
			return;
		onDmSelected(nodes);
	}

	private void onDmSelected(List<AmiWebGraphNode<?>> nodes) {
		AmiWebGraphNode data = CH.first(nodes);
		byte type = data.getType();
		if (type != AmiWebGraphNode.TYPE_DATAMODEL) {
			getManager().showAlert("Please select a datamodel");
			return;
		}
		this.selectedDm = ((AmiWebGraphNode_Datamodel) data).getInner().getResponseOutSchema();
		FormPortletSelectField<String> input = new FormPortletSelectField<String>(String.class, "&nbsp;");
		for (String s : selectedDm.getTableNamesSorted())
			input.addOption(s, s);
		ConfirmDialogPortlet confirmDialog;
		if (input.getOptionsCount() == 0) {
			getManager().showAlert("Datamodel does not have any tables. Please select a datamodel with at least one table");
			return;
		} else if (input.getOptionsCount() != 1)
			confirmDialog = new ConfirmDialogPortlet(generateConfig(),
					"The '<B>" + selectedDm.getDatamodel().getDmName() + "</B>' datamodel has " + input.getOptionsCount() + " tables. Please choose one:",
					ConfirmDialogPortlet.TYPE_OK_CANCEL, this, input);
		else
			confirmDialog = new ConfirmDialogPortlet(generateConfig(), "Select " + selectedDm.getDatamodel().getDmName() + " Datamodel?", ConfirmDialogPortlet.TYPE_OK_CANCEL, this,
					null);
		confirmDialog.setCallback("SELECT_DMTABLE");
		getManager().showDialog("Select Table", confirmDialog);

	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if ("SELECT_DMTABLE".equals(source.getCallback())) {
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				if (source.getInputField() != null)
					this.selectedDmTable = selectedDm.getTable((String) source.getInputFieldValue());
				else
					this.selectedDmTable = selectedDm.getTable(CH.first(selectedDm.getTableNamesSorted()));
				this.close();
				this.listener.onDmSelected(this.selectedDmTable);
			}
		} else if ("CONFIRM_NO_DM".equals(source.getCallback())) {
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				this.close();
				this.listener.onDmSelected(null);
			}
		}
		return true;
	}

	public AmiWebDmTableSchema getSelectedDmTable() {
		return this.selectedDmTable;
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.selectButton) {
			List<AmiWebGraphNode<?>> nodes = this.dmPortlet.getSelectedNodes();
			if (nodes.size() > 1) {
				getManager().showAlert("Please select only one datamodel");
				return;
			} else if (nodes.size() == 0) {
				if (allowNoSelection) {
					getManager().showDialog("Confirm",
							new ConfirmDialogPortlet(generateConfig(), "You have not selected any datamodel. Continue?", ConfirmDialogPortlet.TYPE_OK_CANCEL, this)
									.setCallback("CONFIRM_NO_DM"));
				} else
					getManager().showAlert("Please select a datamodel first");
				return;
			}
			onDmSelected(nodes);
		}
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}
	public boolean isAllowNoSelection() {
		return allowNoSelection;
	}
	public void setAllowNoSelection(boolean allowNoSelection) {
		this.allowNoSelection = allowNoSelection;
	}

}
