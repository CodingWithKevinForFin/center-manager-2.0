package com.f1.ami.web.dm.portlets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.msg.AmiCenterManageDatasourceRequest;
import com.f1.ami.amicommon.msg.AmiCenterManageDatasourceResponse;
import com.f1.ami.web.AmiWebChooseDataForm;
import com.f1.ami.web.AmiWebConsts;
import com.f1.ami.web.AmiWebDatasourceWrapper;
import com.f1.ami.web.AmiWebLayoutFile;
import com.f1.ami.web.AmiWebManager;
import com.f1.ami.web.AmiWebObject;
import com.f1.ami.web.AmiWebObjects;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.ami.web.AmiWebProcessorImportExportPortlet;
import com.f1.ami.web.AmiWebRealtimePortlet;
import com.f1.ami.web.AmiWebRealtimeProcessor;
import com.f1.ami.web.AmiWebRealtimeProcessorPlugin;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.AmiWebViewConfigurationPortlet;
import com.f1.ami.web.AmiWebViewObjectsPortlet;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.ami.web.filter.AmiWebFilterPortlet;
import com.f1.ami.web.filter.AmiWebFilterSettingsPortlet;
import com.f1.ami.web.graph.AmiWebGraphNode;
import com.f1.ami.web.graph.AmiWebGraphNode_Datamodel;
import com.f1.ami.web.graph.AmiWebGraphNode_Datasource;
import com.f1.ami.web.graph.AmiWebGraphNode_Panel;
import com.f1.ami.web.graph.AmiWebGraphNode_Realtime;
import com.f1.ami.web.rt.AmiWebRealtimeProcessorImportExportPortlet;
import com.f1.ami.web.rt.AmiWebRealtimeProcessorPlugin_BPIPE;
import com.f1.base.Action;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.container.ResultMessage;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.BackendResponseListener;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.structs.table.stack.BasicCalcTypes;

public class AmiWebDmSmartGraphMenu {
	private static final Logger log = LH.get();

	//	protected static BasicWebMenu createContextMenu(AmiWebDmSmartGraphPortlet datamodeler, Node node, int button, boolean shft) {
	//		if (button != 2)
	//			return null;
	//
	//		IterableAndSize<Node> nodes = datamodeler.getGraph().getSelectedNodes();
	//		List<AmiWebGraphNode> nodes2 = new ArrayList<AmiWebGraphNode>(nodes.size());
	//		if (nodes.size() >= 1) {
	//			CH.first(nodes).setSelected(true);
	//			datamodeler.setContextNode(node);
	//		}
	//		boolean hasExplore = false;
	//		for (Node data : nodes) {
	//			AmiWebGraphNode n = AmiWebDmSmartGraphPortlet.getData(data);
	//			if (n == null)
	//				return null;
	//			nodes2.add(n);
	//			if (!hasExplore && datamodeler.canExplore(n)) {
	//				hasExplore = true;
	//			}
	//		}
	//		BasicWebMenu menu = createContextMenu(datamodeler.getService(), nodes2);
	//		if (hasExplore)
	//			menu.add(0, new BasicWebMenuLink("Explore", true, "explore"));
	//		return menu;
	//	}
	protected static BasicWebMenu createContextMenu(AmiWebService service, List<AmiWebGraphNode<?>> selectedNodesList, boolean allowModification) {
		BasicWebMenu menu = new BasicWebMenu();
		boolean hasTransient = false;
		for (AmiWebGraphNode data : selectedNodesList) {
			if (!(data instanceof AmiWebGraphNode_Realtime) && data.getInner() == null) {
				// undefined dm (AMI creates an undefined dm based on panel config)
				// note that panel can have no data (not connected to any dm) but still be defined
				return menu;
			}
			if (data.isTransient()) {
				hasTransient = true;
				break;
			}

			if (data.getType() == AmiWebGraphNode.TYPE_DATAMODEL) {
				AmiWebDmsImpl dms = ((AmiWebGraphNode_Datamodel) data).getInner();
				if (dms.isTransient()) {
					hasTransient = true;
					break;
				}
			}
		}

		if (hasTransient)
			menu.add(new BasicWebMenuLink("* TRANSIENT", false, "").setCssStyle(AmiWebConsts.TITLE_CSS2));
		int selectedCount = selectedNodesList.size();
		boolean allRt = isAllRT(selectedNodesList);

		if (selectedCount == 0 && allowModification) {
			menu.addChild(new BasicWebMenuLink("Add Datasource", true, "add_ds"));
			menu.addChild(new BasicWebMenuLink("Add Datamodel", true, "add_dm"));
			menu.addChild(new BasicWebMenuLink("Export All Datamodels", true, "exportall"));
			menu.addChild(new BasicWebMenuLink("Import Datamodel", true, "import_dm"));
			menu.addChild(new BasicWebMenuLink("Import Processor", true, "import_pr"));
			BasicWebMenu rp = new BasicWebMenu("Add Realtime Processor", !hasTransient);
			menu.addChild(rp);
			final List<String> defaultProcessors = CH.l("LIMIT", "DECORATE");
			for (Map.Entry<String, AmiWebRealtimeProcessorPlugin> e : service.getWebManagers().getProcessorPlugins().entrySet()) {
				if (!e.getValue().canSupportCount(0))
					continue;
				final String pluginID = e.getValue().getPluginId();
				if (!defaultProcessors.contains(pluginID))
					rp.addChild(new BasicWebMenuLink(e.getValue().getPluginId(), true, "processor_add_" + e.getValue().getPluginId()));
			}
			rp.addChild(new BasicWebMenuLink("BPIPE", true, "add_bpipe"));

		} else if (selectedCount == 1) {
			AmiWebGraphNode data = selectedNodesList.get(0);
			byte type = data.getType();

			if (AmiWebGraphNode.TYPE_DATAMODEL == type) {
				AmiWebDmsImpl dm = ((AmiWebGraphNode_Datamodel) data).getInner();
				if (allowModification) {
					menu.addChild(new BasicWebMenuLink("Add Datamodel", !hasTransient, "add_dm"));
					menu.addChild(new BasicWebMenuLink("Add Table / Visualization / Form", !hasTransient, "add_viz"));
					menu.addChild(new BasicWebMenuLink("Add Filter", !hasTransient, "add_filt"));
					menu.addChild(new BasicWebMenuLink("Delete Datamodel", true, "delete_dm"));
					menu.addChild(new BasicWebMenuLink("Edit Datamodel", true, "edit"));
					menu.addChild(new BasicWebMenuLink("Copy Datamodel", true, "copy"));
					menu.addChild(new BasicWebMenuLink("Import/Export Datamodel", true, "import_export"));
				}

				if (allowModification && dm != null && dm.getMaxRequeryMs() != 0)
					menu.addChild(new BasicWebMenuLink(isAllPaused(selectedNodesList) ? "Play &#9658;" : "Pause &#10074;&#10074;", true, "pp"));
				if (service.getScmAdapter() != null) {
					menu.addChild(new BasicWebMenuLink("Show History", !hasTransient, "show_dm_history"));
				}
			} else if (AmiWebGraphNode.TYPE_DATASOURCE == type && allowModification) {
				AmiWebDatasourceWrapper ds = ((AmiWebGraphNode_Datasource) data).getInner();

				if (ds == null && !AmiConsts.DATASOURCE_NAME_AMI.equals(data.getId())) {
					menu.addChild(new BasicWebMenuLink("Edit Datasource", true, "ds_edit"));
					menu.addChild(new BasicWebMenuLink("Delete Undefined Datasource", true, "ds_delete"));
				} else {
					if (!AmiConsts.DATASOURCE_ADAPTER_NAME_AMI.equals(ds.getAdapter())) {
						menu.addChild(new BasicWebMenuLink("Edit Datasource", true, "ds_edit"));
						menu.addChild(new BasicWebMenuLink("Delete Datasource", true, "ds_delete"));
					}
					menu.addChild(new BasicWebMenuLink("Add Datamodel to " + ds.getName(), !hasTransient, "add_dm"));
					menu.addChild(new BasicWebMenuLink("Copy Datasource", true, "ds_copy"));
				}
			} else if (AmiWebGraphNode.TYPE_PANEL == type) {
				AmiWebPortlet p = (AmiWebPortlet) data.getInner();
				if (p.isRealtime())
					menu.addChild(new BasicWebMenuLink("Add Datamodel", !hasTransient, "add_dm"));
				if (service.getScmAdapter() != null)
					menu.addChild(new BasicWebMenuLink("Show History", true, "show_pnl_history"));
			} else if (AmiWebGraphNode.TYPE_FEED == type && allowModification) {
				menu.addChild(new BasicWebMenuLink("Add Datamodel", !hasTransient, "add_dm"));
			} else if (AmiWebGraphNode.TYPE_PROCESSOR == type) {
				if (allowModification) {
					menu.addChild(new BasicWebMenuLink("Add Datamodel", !hasTransient, "add_dm"));
					menu.addChild(new BasicWebMenuLink("Delete Processor", true, "delete_pr"));
					menu.addChild(new BasicWebMenuLink("Edit processor", true, "edit_pr"));
				}
				menu.addChild(new BasicWebMenuLink("Export Processor", true, "export_pr"));
			}
		} else if (selectedCount > 1 && allowModification) {
			boolean allDs = nodesAllOfType(selectedNodesList, AmiWebGraphNode.TYPE_DATASOURCE);
			if (allDs) {
				if (isAllBadDs(selectedNodesList)) {
					menu.addChild(new BasicWebMenuLink("Delete Undefined Datasources", true, "ds_delete"));
				} else {
					menu.addChild(new BasicWebMenuLink("Add Datamodel", !hasTransient, "add_dm"));
				}
			} else if (isAllDMOrDsOrRT(selectedNodesList)) {
				menu.addChild(new BasicWebMenuLink("Add Datamodel", !hasTransient, "add_dm"));
			}
			if (isAllDM(selectedNodesList)) {
				menu.addChild(new BasicWebMenuLink("Delete Datamodel(s)", true, "delete_dm"));
				boolean allSelectedPaused = isAllPaused(selectedNodesList);
				if (isAllAutorequery(selectedNodesList))
					menu.addChild(new BasicWebMenuLink(allSelectedPaused ? "Play &#9658;" : "Pause &#10074;&#10074;", true, "pause_selected_" + allSelectedPaused));
			}
			if (isAllPR(selectedNodesList)) {
				menu.addChild(new BasicWebMenuLink("Delete processor", true, "delete_pr"));
			}
		}
		if (selectedCount > 0) {
			if (hasViewData(selectedNodesList)) {
				if (sAllHasFilter(selectedNodesList)) {
					BasicWebMenu viewMenu = new BasicWebMenu("View Data", true);
					viewMenu.add(new BasicWebMenuLink("Before Filters", true, "view_data_before"));
					viewMenu.add(new BasicWebMenuLink("After Filters", true, "view_data_after"));
					menu.add(viewMenu);
				} else
					menu.addChild(new BasicWebMenuLink("View Data", true, "view_data"));
			}
			if (allowModification && allRt) {
				menu.addChild(new BasicWebMenuLink("Add Realtime Table / Visualization", !hasTransient, "add_rt_viz"));
				BasicWebMenu ep = new BasicWebMenu("Add Realtime Processor", !hasTransient);
				for (Entry<String, AmiWebRealtimeProcessorPlugin> i : service.getWebManagers().getProcessorPlugins().entrySet()) {
					ep.add(new BasicWebMenuLink(i.getValue().getDescription(), i.getValue().canSupportCount(selectedNodesList.size()), "add_ep_" + i.getValue().getPluginId()));
				}
				if (ep.getChildrenCount() > 0)
					menu.add(ep);
			}
		}
		return menu;
	}
	public static boolean isAllRT(List<AmiWebGraphNode<?>> selectedNodesList) {
		for (AmiWebGraphNode data : selectedNodesList)
			if (data == null || data.getRealtimeId() == null)
				return false;
		return true;
	}
	public static boolean isAllAutorequery(List<AmiWebGraphNode<?>> selectedNodesList) {
		for (AmiWebGraphNode data : selectedNodesList) {
			if (data.getType() == AmiWebGraphNode.TYPE_DATAMODEL) {
				AmiWebDmsImpl dm = ((AmiWebGraphNode_Datamodel) data).getInner();
				if (dm.getMaxRequeryMs() > 0)
					return true;
			}
		}
		return false;
	}
	public static boolean isAllPaused(List<AmiWebGraphNode<?>> selectedNodesList) {
		for (AmiWebGraphNode data : selectedNodesList) {
			if (data.getType() == AmiWebGraphNode.TYPE_DATAMODEL) {
				AmiWebDmsImpl dm = ((AmiWebGraphNode_Datamodel) data).getInner();
				if (dm.isPlaying())
					return false;
			}
		}
		return true;
	}
	public static boolean isAllDMOrDsOrRT(List<AmiWebGraphNode<?>> selectedNodesList) {
		for (AmiWebGraphNode data : selectedNodesList) {
			switch (data.getType()) {
				case AmiWebGraphNode.TYPE_DATAMODEL:
				case AmiWebGraphNode.TYPE_DATASOURCE:
				case AmiWebGraphNode.TYPE_FEED:
				case AmiWebGraphNode.TYPE_PROCESSOR:
					break;
				case AmiWebGraphNode.TYPE_PANEL:
					AmiWebPortlet portlet = (AmiWebPortlet) data.getInner();
					if (portlet.isRealtime())
						break;
				default:
					return false;
			}
		}
		return true;
	}

	public static boolean isAllDM(List<AmiWebGraphNode<?>> selectedNodesList) {
		for (AmiWebGraphNode<?> data : selectedNodesList) {
			if (data.getType() != AmiWebGraphNode.TYPE_DATAMODEL) {
				return false;
			}
		}
		return true;
	}
	public static boolean isAllPR(List<AmiWebGraphNode<?>> selectedNodesList) {
		for (AmiWebGraphNode<?> data : selectedNodesList) {
			if (data.getType() != AmiWebGraphNode.TYPE_PROCESSOR) {
				return false;
			}
		}
		return true;
	}
	public static boolean isAllBadDs(List<AmiWebGraphNode<?>> nodes) {
		for (AmiWebGraphNode<?> data : nodes) {
			if (data.getType() != AmiWebGraphNode.TYPE_DATASOURCE)
				return false;
			AmiWebDatasourceWrapper dm = ((AmiWebGraphNode_Datasource) data).getInner();
			if (dm != null)
				return false;
		}
		return true;
	}
	public static boolean sAllHasFilter(List<AmiWebGraphNode<?>> nodes) {
		for (AmiWebGraphNode<?> data : nodes) {
			if (data.getType() == AmiWebGraphNode.TYPE_DATAMODEL) {
				AmiWebDmsImpl dm = ((AmiWebGraphNode_Datamodel) data).getInner();
				if (dm != null && dm.getFilters().size() > 0) {
					continue;
				}
			}
			return false;
		}
		return true;
	}
	public static boolean hasViewData(List<AmiWebGraphNode<?>> selectedNodesList) {
		for (AmiWebGraphNode<?> data : selectedNodesList) {
			switch (data.getType()) {
				case AmiWebGraphNode.TYPE_DATAMODEL:
				case AmiWebGraphNode.TYPE_FEED:
				case AmiWebGraphNode.TYPE_PROCESSOR:
					break;
				case AmiWebGraphNode.TYPE_PANEL:
					AmiWebPortlet portlet = (AmiWebPortlet) data.getInner();
					if (portlet.isRealtime())
						break;
				default:
					return false;
			}
		}
		return true;
	}
	public static boolean nodesAllOfType(List<AmiWebGraphNode<?>> nodes, byte nodeType) {
		for (AmiWebGraphNode<?> n : nodes)
			if (n.getType() != nodeType)
				return false;
		return true;
	}

	static public void onMenuItem(AmiWebService service, String id, List<AmiWebGraphNode<?>> nodes) {
		AmiWebGraphNode<?> first = CH.first(nodes);
		PortletManager manager = service.getPortletManager();
		if (id.equals("add_dm")) {
			List<AmiWebDatasourceWrapper> dsList = new ArrayList<AmiWebDatasourceWrapper>();
			List<AmiWebDmsImpl> dmList = new ArrayList<AmiWebDmsImpl>();
			List<String> rtList = new ArrayList<String>();
			for (AmiWebGraphNode<?> data : nodes) {
				if (data.getType() == AmiWebGraphNode.TYPE_DATASOURCE)
					dsList.add(((AmiWebGraphNode_Datasource) data).getInner());
				else if (data.getType() == AmiWebGraphNode.TYPE_DATAMODEL)
					dmList.add(((AmiWebGraphNode_Datamodel) data).getInner());
				else if (data.getType() == AmiWebGraphNode.TYPE_PROCESSOR || data.getType() == AmiWebGraphNode.TYPE_FEED)
					rtList.add(((AmiWebGraphNode_Realtime) data).getId());
				else if (data.getType() == AmiWebGraphNode.TYPE_PANEL && data.getRealtimeId() != null)
					rtList.add(data.getRealtimeId());
			}
			AmiWebUtils.showEditDmPortlet(service, dsList, dmList, rtList, "Add Datamodel");
		} else if (id.equals("delete_pr")) {
			Map<String, AmiWebRealtimeProcessor> toDelete = new HashMap<String, AmiWebRealtimeProcessor>();
			boolean allTransient = true;
			for (AmiWebGraphNode data1 : nodes) {
				AmiWebGraphNode_Realtime data = (AmiWebGraphNode_Realtime) data1;
				AmiWebRealtimeProcessor inner = (AmiWebRealtimeProcessor) data.getInner();
				AmiWebLayoutFile layout = service.getLayoutFilesManager().getLayoutByFullAlias(inner.getAlias());
				if (layout.isReadonly()) {
					manager.showAlert("Processor is readonly: " + data.getLabel());
					return;
				}
				toDelete.put(data.getId(), inner);
				if (!inner.isTransient())
					allTransient = false;
				if (data.hasDependencies()) {
					manager.showAlert("Cannot delete processor <B>" + data.getId() + "</B> because it has dependencies");
					return;
				}
			}
			ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(manager.generateConfig(), "Delete " + toDelete.size() + " selected processors(s)?",
					ConfirmDialogPortlet.TYPE_YES_NO, new DialogListener(service, id, nodes)).setCallback("DELETE_PR").setCorrelationData(toDelete);
			if (allTransient)
				cdp.fireYesButton();
			else
				manager.showDialog("Delete Processor", cdp);

		} else if (id.equals("edit_pr")) {
			AmiWebGraphNode<?> data = CH.first(nodes);
			AmiWebRealtimeProcessor rto = (AmiWebRealtimeProcessor) data.getInner();
			String pluginId = rto.getType();
			AmiWebRealtimeProcessorPlugin plugin = service.getWebManagers().getProcessorPlugins().get(pluginId);
			plugin.starEditWizard(service, rto);

		} else if (id.equals("delete_dm")) {
			Map<String, AmiWebDm> toDelete = new HashMap<String, AmiWebDm>();
			boolean allTransient = true;
			for (AmiWebGraphNode data1 : nodes) {
				AmiWebGraphNode_Datamodel data = (AmiWebGraphNode_Datamodel) data1;
				if (data.getInner().isReadonlyLayout()) {
					manager.showAlert("Datamodel is readonly: " + data.getInner().getDmName());
					return;
				}
				toDelete.put(data.getInner().getAmiLayoutFullAliasDotId(), data.getInner());
				if (!data.getInner().isTransient())
					allTransient = false;
				if (data.hasDependencies()) {
					manager.showAlert("Cannot delete datamodel <B>" + data.getId() + "</B> because it has dependencies");
					return;
				}
			}
			ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(manager.generateConfig(), "Delete " + toDelete.size() + " selected datamodel(s)?", ConfirmDialogPortlet.TYPE_YES_NO,
					new DialogListener(service, id, nodes)).setCallback("DELETE_DM").setCorrelationData(toDelete);
			if (allTransient)
				cdp.fireYesButton();
			else
				manager.showDialog("Delete Datamodel", cdp);

		} else if (id.equals("view_data")) {
			viewData(nodes, manager, AmiWebDmViewDataPortlet.RESPONSEDATA_DEFAULT);
		} else if (id.equals("view_data_after")) {
			viewData(nodes, manager, AmiWebDmViewDataPortlet.RESPONSEDATA_AFTER_FILTER);
		} else if (id.equals("view_data_before")) {
			viewData(nodes, manager, AmiWebDmViewDataPortlet.RESPONSEDATA_BEFORE_FILTER);
		} else if (id.equals("edit")) {
			AmiWebGraphNode data = first;
			if (data.getType() == data.TYPE_DATAMODEL) {
				AmiWebUtils.showEditDmPortlet(service, ((AmiWebGraphNode_Datamodel) data).getInner(), "Edit Datamodel");
			}
		} else if (id.equals("pp")) {
			for (AmiWebGraphNode d : nodes) {
				AmiWebDm dm = (AmiWebDm) d.getInner();
				dm.setIsPlay(!dm.isPlaying());
				//				AmiWebDmLayoutNode pos = d.layoutNode;
				//				applyStyle(n, pos);
			}

		} else if (id.equals("copy")) {
			AmiWebGraphNode data = first;
			if (data.getType() == AmiWebGraphNode.TYPE_DATAMODEL) {
				AmiWebDmsImpl dm = ((AmiWebGraphNode_Datamodel) data).getInner();
				AmiWebAddPanelPortlet window = new AmiWebAddPanelPortlet(service.getPortletManager().generateConfig(), dm, true);
				//				AmiWebEditDmPortlet window = new AmiWebEditDmPortlet(manager.generateConfig(), dm.getAmiLayoutFullAlias());
				//				window.setDatamodel(dm, true);
				manager.showDialog("Copy DMS", window);
			}
		} else if (id.equals("exportall")) {
			AmiWebViewConfigurationPortlet p = new AmiWebViewConfigurationPortlet(manager.generateConfig());
			p.setConfiguration(service.getDmManager().getConfiguration(""));//TODO: support choosing alias
			manager.showDialog("Import/Export Datamodel", p);
		} else if (id.equals("import_export")) {
			AmiWebGraphNode data = first;
			if (data.getType() == AmiWebGraphNode.TYPE_DATAMODEL) {
				manager.showDialog("Import/Export Datamodel", new AmiWebDmImportExportPortlet(manager.generateConfig(), ((AmiWebGraphNode_Datamodel) data).getInner()));
			}
		} else if (id.equals("export_pr")) {
			AmiWebGraphNode data = first;
			if (data.getType() == AmiWebGraphNode.TYPE_PROCESSOR)
				manager.showDialog("Import/Export Processor", new AmiWebProcessorImportExportPortlet(manager.generateConfig(), (AmiWebRealtimeProcessor) data.getInner()));
		} else if (id.equals("import_dm")) {
			manager.showDialog("Import Datamodel", new AmiWebDmImportExportPortlet(manager.generateConfig(), null));
		} else if (id.equals("import_pr")) {
			manager.showDialog("Import Realtime Processor", new AmiWebRealtimeProcessorImportExportPortlet(manager.generateConfig()));
		} else if (id.equals("add_bpipe")) {
			AmiWebRealtimeProcessorPlugin plugin = service.getWebManagers().getProcessorPlugins().get("BPIPE");
			AmiWebRealtimeProcessorPlugin_BPIPE b = (AmiWebRealtimeProcessorPlugin_BPIPE) plugin;
			b.startWizard(service);
		} else if (id.startsWith("processor_add_")) {
			String processorID = SH.stripPrefix(id, "processor_add_", true);
			AmiWebRealtimeProcessorPlugin plugin = service.getWebManagers().getProcessorPlugins().get(processorID);
			plugin.startWizard(service, null);
		} else if (id.startsWith("pause_selected_")) {
			boolean play = Boolean.parseBoolean(SH.stripPrefix(id, "pause_selected_", true));
			for (AmiWebGraphNode data : nodes) {
				AmiWebGraphNode_Datamodel data2 = (AmiWebGraphNode_Datamodel) data;
				data2.getInner().setIsPlay(play);
			}
			//			graph.clearSelected();
			//		} else if (id.equals("select_dm")) {
			//			List<Node> nodes = new ArrayList<Node>();
			//			for (Node node : graph.getSelectedNodes()) {
			//				AmiWebGraphNode d = getData(node);
			//				if (d.getType() == AmiWebGraphNode.TYPE_DATAMODEL) {
			//					nodes.add(node);
			//				}
			//			}
			//			graph.clearSelected();
			//			setSelectedNodes(nodes, true);
			//		} else if (id.equals("select_ds")) {
			//			List<Node> nodes = new ArrayList<Node>();
			//			for (Node node : graph.getSelectedNodes()) {
			//				AmiWebGraphNode d = getData(node);
			//				if (d.getType() == AmiWebGraphNode.TYPE_DATASOURCE)
			//					nodes.add(node);
			//			}
			//			graph.clearSelected();
			//			setSelectedNodes(nodes, true);
			//		} else if (id.equals("select_pt")) {
			//			List<Node> nodes2 = new ArrayList<Node>();
			//			for (Node node : nodes) {
			//				AmiWebGraphNode d = getData(node);
			//				if (d.getType() == AmiWebGraphNode.TYPE_PANEL)
			//					nodes.add(node);
			//			}
			//			graph.clearSelected();
			//			setSelectedNodes(nodes, true);
			//		} else if (id.startsWith("ph_")) {
			//			GraphPortletHelper.onArrangeMenuItem(id, this.graph.getSelectedNodesList());
		} else if ("add_ds".equals(id)) {
			AmiWebDatasourceWrapper ds = null;
			if (nodes.size() > 0) {
				AmiWebGraphNode data = nodes.get(0);
				if (data.getType() == AmiWebGraphNode.TYPE_DATASOURCE)
					ds = ((AmiWebGraphNode_Datasource) data).getInner();
			}
			manager.showDialog("Attach Datasource", new AmiWebDmAddEditDatasourcePortlet(manager.generateConfig(), ds, false, false));
		} else if ("ds_edit".equals(id)) {
			if (nodes.size() == 1) {
				AmiWebDatasourceWrapper ds = null;
				AmiWebGraphNode data = first;
				if (data.getType() == AmiWebGraphNode.TYPE_DATASOURCE)
					ds = ((AmiWebGraphNode_Datasource) data).getInner();
				manager.showDialog("Edit Datasource", new AmiWebDmAddEditDatasourcePortlet(manager.generateConfig(), ds, true, false));
			}
		} else if ("ds_copy".equals(id)) {
			AmiWebDatasourceWrapper ds = null;
			if (nodes.size() > 0) {
				AmiWebGraphNode data = nodes.get(0);
				if (data.getType() == AmiWebGraphNode.TYPE_DATASOURCE)
					ds = ((AmiWebGraphNode_Datasource) data).getInner();
			}
			manager.showDialog("Copy Datasource", new AmiWebDmAddEditDatasourcePortlet(manager.generateConfig(), ds, false, true));
		} else if ("ds_delete".equals(id)) {
			for (AmiWebGraphNode n : nodes) {
				AmiWebGraphNode_Datasource data = (AmiWebGraphNode_Datasource) n;
				if (data.hasDependencies()) { // Check that datasource has no children
					AmiWebDatasourceWrapper ds = data.getInner();
					manager.showAlert("Cannot delete datasource " + (ds == null ? (data == null ? "" : "<B>" + data.getLabel() + "</B> ") : "<B>" + ds.getName() + "</B> ")
							+ "because it is referenced by at least one datamodel");
					return;
				}
			}
			ConfirmDialogPortlet dialog = new ConfirmDialogPortlet(manager.generateConfig(), "Are you sure you want to delete the selected datasource(s)?",
					ConfirmDialogPortlet.TYPE_YES_NO);
			dialog.setCallback("confirm_delete");
			dialog.addDialogListener(new DialogListener(service, id, nodes));
			dialog.updateButton(ConfirmDialogPortlet.ID_YES, "Delete");
			manager.showDialog("Confirmation", dialog);
		} else if ("add_viz".equals(id)) {
			AmiWebGraphNode data = first;
			if (data.getType() == AmiWebGraphNode.TYPE_DATAMODEL) {
				AmiWebDm dm = ((AmiWebGraphNode_Datamodel) data).getInner();
				service.getDesktop().showAddVisualizationPortlet(dm);
			}
		} else if ("add_rt_viz".equals(id)) {
			List<String> ids = new ArrayList<String>(nodes.size());
			for (AmiWebGraphNode data : nodes)
				if (data.getRealtimeId() != null)
					ids.add(data.getRealtimeId());
			AmiWebChooseDataForm p = new AmiWebChooseDataForm(manager.generateConfig(), service.getDesktop(), null, true);
			p.setTypes(ids);
			manager.showDialog("Choose Ami Data", p);
		} else if ("add_filt".equals(id)) {
			AmiWebGraphNode data = first;
			if (data.getType() == AmiWebGraphNode.TYPE_DATAMODEL) {
				AmiWebDm dm = ((AmiWebGraphNode_Datamodel) data).getInner();
				List<String> tableNamesSorted = dm.getResponseOutSchema().getTableNamesSorted();
				ConfirmDialogPortlet confirm;
				if (tableNamesSorted.size() == 1) {
					confirm = new ConfirmDialogPortlet(manager.generateConfig(), "Select " + dm.getDmName() + " Datamodel?", ConfirmDialogPortlet.TYPE_OK_CANCEL,
							new DialogListener(service, id, nodes), null);
					AmiWebFilterPortlet filter = (AmiWebFilterPortlet) service.getDesktop().newPortlet(AmiWebFilterPortlet.Builder.ID,
							service.getDesktop().getDesktop().getAmiLayoutFullAlias());
					filter.setUsedDatamodel(dm.getAmiLayoutFullAliasDotId(), CH.first(tableNamesSorted));
					manager.showDialog("Create Filter", filter.showSettingsPortlet());
				} else {
					FormPortletSelectField<String> selectTableField = new FormPortletSelectField<String>(String.class, "&nbsp;");
					for (String table : tableNamesSorted) {
						selectTableField.addOption(table, table);
					}
					confirm = new ConfirmDialogPortlet(manager.generateConfig(),
							"The '<B>" + dm.getDmName() + "</B>' datamodel has " + tableNamesSorted.size() + " tables. Please choose one:", ConfirmDialogPortlet.TYPE_OK_CANCEL,
							new DialogListener(service, id, nodes), selectTableField);
					confirm.setCallback("SELECT_DM_TABLE");
					manager.showDialog("Select " + (tableNamesSorted.size() == 1 ? "Datamodel" : "Table"), confirm);
				}
			}
		} else if ("show_dm_history".equals(id)) {
			AmiWebGraphNode_Datamodel data = (AmiWebGraphNode_Datamodel) first;
			service.getDesktop().showViewObjectsPortlet();
			AmiWebViewObjectsPortlet op = service.getDesktop().getSpecialPortlet(AmiWebViewObjectsPortlet.class);
			AmiWebDm dm = data.getInner();
			Row rowOnDatamodelsTable = op.getDatamodelsPortlet().getRowByDmAdn(dm.getAmiLayoutFullAliasDotId());
			op.getTabsContainer().setActiveTab(op.getDatamodelsPortlet());
			op.getDatamodelsPortlet().getTablePortlet().getTable().setSelectedRows(new int[] { rowOnDatamodelsTable.getLocation() });
			op.getDatamodelsPortlet().onContextMenu(op.getDatamodelsPortlet().getTablePortlet().getTable(), AmiWebViewObjectsPortlet.ACTION_SHOW_HISTORY);
		} else if ("show_pnl_history".equals(id)) {
			AmiWebGraphNode_Panel data = (AmiWebGraphNode_Panel) first;
			service.getDesktop().showViewObjectsPortlet();
			AmiWebViewObjectsPortlet op = service.getDesktop().getSpecialPortlet(AmiWebViewObjectsPortlet.class);
			AmiWebPortlet panel = data.getInner();
			Row rowOnPanelsPortlet = op.getPanelsPortlet().getRowByPortletId(panel.getPortletId());
			op.getTabsContainer().setActiveTab(op.getPanelsPortlet());
			op.getPanelsPortlet().getTablePortlet().getTable().setSelectedRows(new int[] { rowOnPanelsPortlet.getLocation() });
			op.getPanelsPortlet().onContextMenu(op.getPanelsPortlet().getTablePortlet().getTable(), AmiWebViewObjectsPortlet.ACTION_SHOW_HISTORY);
		} else if (SH.startsWith(id, "add_ep_")) {
			String pluginId = SH.stripPrefix(id, "add_ep_", true);
			Set<String> entries = new HasherSet<String>();
			for (AmiWebGraphNode i : nodes)
				if (i != null && i.getRealtimeId() != null)
					entries.add(i.getRealtimeId());
			AmiWebRealtimeProcessorPlugin plugin = service.getWebManagers().getProcessorPlugins().get(pluginId);
			plugin.startWizard(service, entries);
		}
	}
	private static void viewData(List<AmiWebGraphNode<?>> nodes, PortletManager manager, byte mode) {
		List<AmiWebDm> datamodels = new ArrayList<AmiWebDm>();
		List<Table> tables = new ArrayList<Table>();
		List<String> missing = new ArrayList<String>();
		for (int i = 0; i < nodes.size(); i++) {
			AmiWebGraphNode<?> d = nodes.get(i);
			switch (d.getType()) {
				case AmiWebGraphNode.TYPE_DATAMODEL:
					datamodels.add(((AmiWebGraphNode_Datamodel) d).getInner());
					break;
				case AmiWebGraphNode.TYPE_FEED: {
					AmiWebGraphNode_Realtime rt = (AmiWebGraphNode_Realtime) d;
					if (rt.getInner() == null) {
						String tableName = SH.afterFirst(rt.getId(), ':');
						BasicCalcTypes schema = rt.getManager().getService().getSystemObjectsManager().getTableSchema(tableName);
						List<AmiWebObject> rows = new ArrayList<AmiWebObject>();

						for (AmiWebManager wm : rt.getManager().getService().getWebManagers().getManagers()) {
							AmiWebObjects rows1 = wm.getAmiObjectsByTypeOrNull(tableName);
							if (rows1 != null)
								CH.addAll(rows, rows1.getAmiObjects());
						}
						if (schema == null)
							missing.add(rt.getId());
						else {
							Table table = AmiWebUtils.toTable(schema, rows);
							String label = rt.getLabel();
							table.setTitle(label);
							tables.add(table);
						}
					} else {
						Table table = AmiWebUtils.toTable(rt.getInner());
						String label = rt.getLabel();
						String desc = rt.getDescription();
						if (SH.is(desc))
							label += " (" + desc + ")";
						table.setTitle(label);
						tables.add(table);
					}
					break;
				}
				case AmiWebGraphNode.TYPE_PROCESSOR: {
					AmiWebGraphNode_Realtime rt = (AmiWebGraphNode_Realtime) d;
					if (rt.getInner() == null)
						missing.add(rt.getId());
					else {
						Table table = AmiWebUtils.toTable(rt.getInner());
						String label = rt.getLabel();
						String desc = rt.getDescription();
						if (SH.is(desc))
							label += " (" + desc + ")";
						table.setTitle(label);
						tables.add(table);
					}
					break;
				}
				case AmiWebGraphNode.TYPE_PANEL: {
					AmiWebGraphNode_Panel rt = (AmiWebGraphNode_Panel) d;
					AmiWebPortlet panel = rt.getInner();
					if (panel instanceof AmiWebRealtimePortlet)
						tables.add(AmiWebUtils.toTable((AmiWebRealtimePortlet) panel));
					break;
				}
			}
		}
		if (tables.size() > 0 || datamodels.size() > 0) {
			AmiWebDmViewDataPortlet window = new AmiWebDmViewDataPortlet(manager.generateConfig(), datamodels, null, mode);
			for (Table i : tables)
				window.addTable(i);
			manager.showDialog("View Data", window);
		}
		if (missing.size() > 0)
			manager.showAlert("The following realtime objects do not have live subscriptions, so data is not available:<BR><B>" + SH.join(", ", missing));
	}

	public static class DialogListener implements ConfirmDialogListener, BackendResponseListener {

		final private String id;
		final private AmiWebService service;
		final private List<AmiWebGraphNode<?>> nodes;

		public DialogListener(AmiWebService service, String id, List<AmiWebGraphNode<?>> nodes) {
			this.id = id;
			this.service = service;
			this.nodes = nodes;
		}

		@Override
		public boolean onButton(ConfirmDialog source, String id) {
			PortletManager manager = service.getPortletManager();
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				if ("DELETE_DM".equals(source.getCallback())) {
					Map<String, AmiWebDm> todelete = (Map<String, AmiWebDm>) source.getCorrelationData();
					for (String s : todelete.keySet())
						this.service.getDmManager().removeDm(s);
				} else if ("DELETE_PR".equals(source.getCallback())) {
					Map<String, AmiWebRealtimeProcessor> todelete = (Map<String, AmiWebRealtimeProcessor>) source.getCorrelationData();
					for (String s : todelete.keySet())
						this.service.getWebManagers().removeProcessor(s);
				} else if ("confirm_delete".equals(source.getCallback())) {
					ConfirmDialogPortlet dialog = new ConfirmDialogPortlet(manager.generateConfig(),
							"Are you REALLY sure you want to delete the selected datasource?<BR>(This will break any layout or backend functionality that uses this datasource)",
							ConfirmDialogPortlet.TYPE_YES_NO);
					dialog.setCallback("reconfirm_delete");
					dialog.addDialogListener(this);
					dialog.updateButton(ConfirmDialogPortlet.ID_YES, "Yes, delete");
					manager.showDialog("Confirmation", dialog);
				} else if ("reconfirm_delete".equals(source.getCallback())) {
					if (nodes.size() == 1) {
						AmiWebGraphNode selData = nodes.get(0);
						AmiWebDatasourceWrapper dsw = ((AmiWebGraphNode_Datasource) selData).getInner();
						if (dsw != null) {
							AmiCenterManageDatasourceRequest req = service.getPortletManager().getTools().nw(AmiCenterManageDatasourceRequest.class);
							req.setId(dsw.getId());
							req.setName(dsw.getName());
							req.setAdapter(dsw.getAdapter());
							req.setUrl(dsw.getUrl());
							req.setUsername(dsw.getUser());
							req.setOptions(dsw.getOptions());
							req.setDelete(true);
							AmiWebUtils.getService(manager).sendRequestToBackend(this, req);
							//					} else {
							//						getLayoutManager().removeInvalidDatasourceByName(selData.getId());
						}
					} else { // Delete multiple undefined datasources
						//					for (Node n : selectedNodes) {
						//						getLayoutManager().removeInvalidDatasourceByName(getData(n).getId());
						//					}
					}
				} else if ("SELECT_DM_TABLE".equals(source.getCallback())) {
					AmiWebDm dm = ((AmiWebGraphNode_Datamodel) nodes.get(0)).getInner();
					AmiWebFilterPortlet filter = (AmiWebFilterPortlet) this.service.getDesktop().newPortlet(AmiWebFilterPortlet.Builder.ID,
							service.getDesktop().getDesktop().getAmiLayoutFullAlias());
					filter.setUsedDatamodel(dm.getAmiLayoutFullAliasDotId(),
							(String) (source.getInputField() == null ? CH.first(dm.getResponseOutSchema().getTableNamesSorted()) : source.getInputFieldValue())); // Need to make user choose a table
					manager.showDialog("Create Filter", new AmiWebFilterSettingsPortlet(manager.generateConfig(), filter));
				}
			}
			return true;
		}

		@Override
		public void onBackendResponse(ResultMessage<Action> result) {
			Action action = result.getAction();
			LH.info(log, "Backend action: " + action);
			if (action instanceof AmiCenterManageDatasourceResponse) {
				AmiCenterManageDatasourceResponse response = (AmiCenterManageDatasourceResponse) action;
				if (!response.getOk())
					service.getPortletManager().showAlert(response.getMessage(), response.getException());
			}
		}

	}
}
