package com.f1.ami.web.dm.portlets;

import java.util.ArrayList;
import java.util.List;

import com.f1.ami.web.AmiWebChooseDataForm;
import com.f1.ami.web.AmiWebManagers;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebSpecialPortlet;
import com.f1.ami.web.graph.AmiWebGraphNode;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.WizardPortlet;

public class AmiWebAddRealtimePanelPortlet extends WizardPortlet implements AmiWebDmTreeListener, AmiWebSpecialPortlet {
	private AmiWebDmTreePortlet tree;
	private AmiWebChooseDataForm vizwiz;

	public AmiWebAddRealtimePanelPortlet(PortletConfig config, AmiWebService service, String targetPortletId, String baseAlias) {
		super(config);
		this.tree = new AmiWebDmTreePortlet(generateConfig(), service, baseAlias, true, null);
		this.tree.setOverrideDblClick(this);
		this.tree.expandRealtimes();
		this.vizwiz = new AmiWebChooseDataForm(generateConfig(), service.getDesktop(), targetPortletId, false);
		super.addPanel(this.tree);
		super.addPanel(this.vizwiz);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == super.getNextButton()) {
			if (this.getActivePortlet() == this.tree) {
				List<AmiWebGraphNode<?>> nodes = this.tree.getSelectedNodes();
				if (!AmiWebDmSmartGraphMenu.isAllRT(nodes)) {
					getManager().showAlert("Please only select realtime feeds, realtime panels or realtime processors");
					return;
				}
				List<String> names = new ArrayList<String>();
				for (AmiWebGraphNode<?> i : nodes) {
					switch (i.getType()) {
						case AmiWebGraphNode.TYPE_PANEL:
							names.add(AmiWebManagers.PANEL + i.getId());
							break;
						case AmiWebGraphNode.TYPE_PROCESSOR:
							//FALLTHROUGH
						case AmiWebGraphNode.TYPE_FEED:
							names.add(i.getId());
							break;
						default:
							throw new RuntimeException("Unsupported type for adding realtime visualizations: " + i.getType());
					}
				}
				this.vizwiz.setTypes(names);
			}
			if (this.getActivePortlet() == this.vizwiz) {
				if (!this.vizwiz.submit())
					return;
			}
		}
		super.onButtonPressed(portlet, button);
	}

	@Override
	public void onDoubleClicked(List<AmiWebGraphNode<?>> nodes) {
		if (!AmiWebDmSmartGraphMenu.isAllRT(nodes)) {
			getManager().showAlert("Please only select realtime feeds, realtime panels or realtime processors");
			return;
		}
		List<String> names = new ArrayList<String>();
		for (AmiWebGraphNode<?> i : nodes) {
			switch (i.getType()) {
				// add prefix for correct parse in setTypes()
				case AmiWebGraphNode.TYPE_PANEL:
					names.add(AmiWebManagers.PANEL + i.getId());
					break;
				case AmiWebGraphNode.TYPE_PROCESSOR:
					//FALLTHROUGH
				case AmiWebGraphNode.TYPE_FEED:
					// already has the prefix
					names.add(i.getId());
					break;
				default:
					throw new RuntimeException("Unsupported type for adding realtime visualizations: " + i.getType());
			}
		}
		this.vizwiz.setTypes(names);
		this.setActivePortlet(this.vizwiz);
	}

}
