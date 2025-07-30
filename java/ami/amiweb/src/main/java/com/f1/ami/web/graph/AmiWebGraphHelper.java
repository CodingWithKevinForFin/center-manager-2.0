package com.f1.ami.web.graph;

import java.util.Collection;
import java.util.Collections;

import com.f1.ami.web.AmiWebChooseDataForm;
import com.f1.ami.web.AmiWebDatasourceWrapper;
import com.f1.ami.web.AmiWebRealtimeObjectManager;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.utils.CH;

public class AmiWebGraphHelper {

	public static boolean openEditor(AmiWebService service, AmiWebGraphNode gn) {
		if (gn.getInner() == null)
			return false;
		switch (gn.getType()) {
			case AmiWebGraphNode.TYPE_DATAMODEL: {
				AmiWebGraphNode_Datamodel dm = (AmiWebGraphNode_Datamodel) gn;
				AmiWebUtils.showEditDmPortlet(service, dm.getInner(), "Edit Datamodel");
				return true;
			}
			case AmiWebGraphNode.TYPE_PANEL: {
				AmiWebGraphNode_Panel pn = (AmiWebGraphNode_Panel) gn;
				Portlet portlet = service.getPortletManager().getPortlet(pn.getInner().getPortletId());
				PortletHelper.ensureVisible(portlet);
				if (service.getDesktop().getInEditMode()) {
					int x = PortletHelper.getAbsoluteLeft(portlet) + portlet.getWidth() / 2;
					int y = PortletHelper.getAbsoluteTop(portlet) + portlet.getHeight() / 2;
					service.getDesktop().showContextMenuForPortlet(portlet.getPortletId(), x, y);
				}
				return true;
			}
			case AmiWebGraphNode.TYPE_DATASOURCE: {
				if (service.getDesktop().getInEditMode()) {
					AmiWebGraphNode_Datasource ds = (AmiWebGraphNode_Datasource) gn;
					AmiWebDatasourceWrapper datasource = ds.getInner();
					if (datasource != null) {
						AmiWebUtils.showEditDmPortlet(service, CH.l(datasource), Collections.EMPTY_LIST, Collections.EMPTY_LIST, "Add Datamodel");
					}
				}
				return true;
			}
			case AmiWebGraphNode.TYPE_LINK: {
				AmiWebGraphNode_Link ds = (AmiWebGraphNode_Link) gn;
				AmiWebDmLink link = ds.getInner();
				if (service.getDesktop().getInEditMode())
					service.getDesktop().getLinkHelper().showEditRelationship(link);
				return true;
			}
			case AmiWebGraphNode.TYPE_PROCESSOR:
			case AmiWebGraphNode.TYPE_FEED: {
				if (service.getDesktop().getInEditMode()) {
					AmiWebGraphNode_Realtime ds = (AmiWebGraphNode_Realtime) gn;
					AmiWebRealtimeObjectManager datasource = ds.getInner();
					if (datasource != null) {
						AmiWebChooseDataForm newEditor = new AmiWebChooseDataForm(service.getPortletManager().generateConfig(), service.getDesktop(), null, true);
						newEditor.setTypes(CH.l(ds.getId()));
						service.getPortletManager().showDialog("Create Realtime Panel", newEditor);
					}
				}
				return true;
			}
		}
		return false;

	}

	public static void getNeighboors(AmiWebGraphNode<?> data, Collection<AmiWebGraphNode<?>> sink, boolean sources, boolean targets) {
		if (!sources && !targets)
			return;
		switch (data.getType()) {
			case AmiWebGraphNode.TYPE_DATAMODEL: {
				AmiWebGraphNode_Datamodel n = (AmiWebGraphNode_Datamodel) data;
				if (sources) {
					sink.addAll(n.getSourceDatamodels().values());
					sink.addAll(n.getSourceDatasources().values());
					sink.addAll(n.getSourceFilterPanels().values());
					sink.addAll(n.getSourceLinks().values());
					sink.addAll(n.getSourceRealtimes().values());
				}
				if (targets) {
					sink.addAll(n.getTargetDatamodels().values());
					sink.addAll(n.getTargetPanels().values());
					sink.addAll(n.getTargetRealtimes().values());
				}
				break;
			}
			case AmiWebGraphNode.TYPE_DATASOURCE: {
				AmiWebGraphNode_Datasource n = (AmiWebGraphNode_Datasource) data;
				if (targets) {
					sink.addAll(n.getTargetDatamodels().values());
				}
				break;
			}
			case AmiWebGraphNode.TYPE_PANEL: {
				AmiWebGraphNode_Panel n = (AmiWebGraphNode_Panel) data;
				if (sources) {
					sink.addAll(n.getSourceDatamodels().values());
					sink.addAll(n.getSourceRealtimes().values());
					sink.addAll(n.getSourceLinks().values());
				}
				if (targets) {
					sink.addAll(n.getTargetRealtimes().values());
					sink.addAll(n.getTargetLinks().values());
					sink.addAll(n.getTargetFilterDatamodels().values());
				}
				break;
			}
			case AmiWebGraphNode.TYPE_LINK: {
				AmiWebGraphNode_Link n = (AmiWebGraphNode_Link) data;
				if (sources) {
					sink.add(n.getSourcePanel());
				}
				if (targets) {
					if (n.getTargetDm() != null)
						sink.add(n.getTargetDm());
					if (n.getTargetPanel() != null)
						sink.add(n.getTargetPanel());
				}
				break;
			}
			case AmiWebGraphNode.TYPE_FEED:
			case AmiWebGraphNode.TYPE_PROCESSOR: {
				AmiWebGraphNode_Realtime n = (AmiWebGraphNode_Realtime) data;
				if (sources) {
					sink.addAll(n.getSourceRealtimes().values());
				}
				if (targets) {
					sink.addAll(n.getTargetRealtimes().values());
				}
				break;
			}
		}
	}

	public static boolean isDefined(AmiWebGraphNode node) {
		if (node instanceof AmiWebGraphNode_Datamodel || node instanceof AmiWebGraphNode_Datasource) {
			return node.getInner() != null;
		}
		return true;
	}
}
