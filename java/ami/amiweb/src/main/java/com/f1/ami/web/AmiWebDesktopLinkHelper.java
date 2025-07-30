package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmLinkImpl;
import com.f1.ami.web.dm.AmiWebDmManager;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.ami.web.dm.portlets.AmiWebDmAddLinkPortlet;
import com.f1.ami.web.dm.portlets.AmiWebDmUtils;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletListener;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;

public class AmiWebDesktopLinkHelper implements PortletListener {

	private static final String PREFIX_LINK = "link_";
	private AmiWebDesktopPortlet desktop;
	private String linkPrefix;
	private String linkingTargetDmAliasDotName;
	private String linkingTargetPortletId;
	private String linkingToPortletId;
	private AmiWebService service;
	private PortletManager manager;
	private AmiWebDmAddLinkPortlet linkDialog;
	private WebMenu chooseDatasourceMenu;

	public AmiWebDesktopLinkHelper(AmiWebDesktopPortlet desktop, String callbackPrefix) {
		this.linkPrefix = callbackPrefix + PREFIX_LINK;
		this.desktop = desktop;
		this.service = desktop.getService();
		this.manager = desktop.getManager();
	}
	public void buildStartLinkMenu(WebMenu headMenu, Portlet portlet) {
		if (portlet instanceof AmiWebPortlet) {
			AmiWebPortlet amiPortlet = (AmiWebPortlet) portlet;

			Collection<AmiWebDmLink> existing = amiPortlet.getDmLinksToThisPortlet();
			if (portlet instanceof AmiWebRealtimePortlet) {
				headMenu.add(new BasicWebMenuLink("Add Relationship", true, linkPrefix + "add_").setBackgroundImage(AmiWebConsts.ICON_ADD));
			} else if (portlet instanceof AmiWebDmPortlet) {
				Set<String> dmNames = ((AmiWebDmPortlet) portlet).getUsedDmAliasDotNames();
				if (dmNames.size() == 1) {
					headMenu.add(new BasicWebMenuLink("Add Relationship", true, linkPrefix + "add_" + CH.first(dmNames)).setBackgroundImage(AmiWebConsts.ICON_ADD));
				} else if (dmNames.size() > 1) {
					BasicWebMenu t = new BasicWebMenu("Add Relationship To", true).setBackgroundImage(AmiWebConsts.ICON_ADD);
					headMenu.add(t);
					for (String dmName : CH.sort(dmNames)) {
						t.add(new BasicWebMenuLink(dmName, true, linkPrefix + "add_" + dmName));
					}
				}
			}
			if (existing.isEmpty()) {
				headMenu.add(new BasicWebMenuLink("Edit Relationship", false, linkPrefix + "edit_"));
				headMenu.add(new BasicWebMenuLink("Remove Relationship", false, linkPrefix + "remove_"));
				//			} else if (existing.size() == 1) {
				//				AmiWebDmLink first = CH.first(existing);
				//				headMenu.add(new BasicWebMenuLink("Edit Relationship", true, linkPrefix + "edit_" + first.getLinkUid()));
				//				headMenu.add(new BasicWebMenuLink("Remove Relationship", true, linkPrefix + "remove_" + first.getLinkUid()));
			} else {
				BasicWebMenu edt = new BasicWebMenu("Edit Relationship", true);
				BasicWebMenu rem = new BasicWebMenu("Remove Relationship", true);
				for (AmiWebDmLink i : existing) {
					final String description;
					if (i.isTransient()) {
						description = i.getRelationshipId() + " (TRANSIENT) (" + i.getSourcePanelAliasDotId() + "-->" + i.getTargetPanelAliasDotId() + ")";
						edt.add(new BasicWebMenuLink(description, false, linkPrefix + "edit_" + i.getLinkUid()));
					} else {
						description = i.getRelationshipId() + "(" + i.getSourcePanelAliasDotId() + "-->" + i.getTargetPanelAliasDotId() + ")";
						edt.add(new BasicWebMenuLink(description, true, linkPrefix + "edit_" + i.getLinkUid()));
					}
					rem.add(new BasicWebMenuLink(description, true, linkPrefix + "remove_" + i.getLinkUid()));
				}
				headMenu.add(edt);
				headMenu.add(rem);
			}
		}
	}
	public boolean handleMenuItem(String id, String currentPortletId) {
		if (!id.startsWith(this.linkPrefix))
			return false;
		id = SH.stripPrefix(id, this.linkPrefix, true);
		if (id.startsWith("add_")) {
			this.linkingTargetDmAliasDotName = SH.stripPrefix(id, "add_", true);
			if (this.linkingTargetDmAliasDotName.isEmpty())
				this.linkingTargetDmAliasDotName = null;
			this.linkingTargetPortletId = currentPortletId;
			new JsFunction(desktop.getManager().getPendingJs(), null, "amiStartLink").addParamQuoted(currentPortletId).end();
			desktop.updateDashboard();
		} else if (id.startsWith("remove_")) {
			String linkid = SH.stripPrefix(id, "remove_", true);
			ConfirmDialogPortlet cDP = new ConfirmDialogPortlet(this.desktop.generateConfig(), "Are you sure you want to remove this relationship?",
					ConfirmDialogPortlet.TYPE_OK_CANCEL, this.desktop).setCallback("_remove_link_" + linkid);
			this.desktop.getManager().showDialog("Remove Relationship", cDP);
		} else if (id.startsWith("edit_")) {
			String linkId = SH.stripPrefix(id, "edit_", true);
			AmiWebDmLink link = service.getDmManager().getDmLink(linkId);
			showEditRelationship(link);
		} else if (id.startsWith("choose_source_")) {
			String t = SH.stripPrefix(id, "choose_source_", true);
			String dmName = SH.beforeFirst(t, '!');
			String dmTableName = SH.afterFirst(t, '!');
			AmiWebPortlet targetPortlet = (AmiWebPortlet) manager.getPortlet(linkingTargetPortletId);
			AmiWebPortlet sourcePortlet = (AmiWebPortlet) manager.getPortlet(currentPortletId);
			AmiWebDm dm = service.getDmManager().getDmByAliasDotName(dmName);
			String alias = "";
			AmiWebDmLinkImpl link = new AmiWebDmLinkImpl(this.service.getDmManager(), alias, null, null, (byte) 0);
			link.setSourceDm(dm.getAmiLayoutFullAliasDotId(), dmTableName);
			link.setSourcePanelAliasDotId(sourcePortlet.getAmiLayoutFullAliasDotId());
			link.setTargetPanelAliasDotId(targetPortlet.getAmiLayoutFullAliasDotId());
			link.setTargetDm(this.linkingTargetDmAliasDotName);
			showEditRelationship(link);
		} else
			throw new RuntimeException("bad callback: " + id);
		return true;
	}
	public static com.f1.base.CalcTypes getTargetVars(AmiWebDmLink link, AmiWebPortlet portlet) {
		if (portlet instanceof AmiWebRealtimePortlet) {
			AmiWebLinkableVarsPortlet rt = (AmiWebLinkableVarsPortlet) portlet;
			return rt.getLinkableVars();
		} else if (portlet instanceof AmiWebDmPortlet) {
			AmiWebDm dm = portlet.getService().getDmManager().getDmByAliasDotName(link.getTargetDmAliasDotName());
			if (dm == null)
				throw new RuntimeException("Relationship references missing target datamodel: " + link.getTargetDmAliasDotName());
			return AmiWebDmUtils.getTargetTypes(dm.getRequestInSchema());
		} else
			return null;
	}
	public static com.f1.base.CalcTypes getSourceVars(AmiWebDmLink link, AmiWebPortlet portlet) {
		if (portlet instanceof AmiWebLinkableVarsPortlet) {
			AmiWebLinkableVarsPortlet rt = (AmiWebLinkableVarsPortlet) portlet;
			return rt.getLinkableVars();
		} else if (portlet instanceof AmiWebDmPortlet) {
			AmiWebDmTableSchema table = link.getSourceTable();
			if (table == null)
				return EmptyCalcTypes.INSTANCE;
			return table.getClassTypes();
		}
		return null;
	}
	public void resetLinkingTarget() {
		this.linkingTargetPortletId = null;
		this.linkingTargetDmAliasDotName = null;
		this.linkingToPortletId = null;
		if (this.linkDialog != null) {
			AmiWebDmAddLinkPortlet t = this.linkDialog;
			this.linkDialog = null;
			t.close();
		}
	}
	@Override
	public void onPortletAdded(Portlet newPortlet) {
	}
	@Override
	public void onSocketConnected(PortletSocket initiator, PortletSocket remoteSocket) {
	}
	@Override
	public void onSocketDisconnected(PortletSocket initiator, PortletSocket remoteSocket) {
	}
	@Override
	public void onPortletParentChanged(Portlet newPortlet, PortletContainer oldParent) {
	}
	@Override
	public void onJavascriptQueued(Portlet portlet) {
	}
	@Override
	public void onPortletRenamed(Portlet portlet, String oldName, String newName) {
	}
	@Override
	public void onLocationChanged(Portlet portlet) {
	}

	@Override
	public void onPortletClosed(Portlet oldPortlet) {
		if (oldPortlet == this.linkDialog) {
			this.linkDialog = null;
			resetLinkingTarget();
			desktop.updateDashboard();
		}
	}
	public boolean isLinking() {
		return this.linkingTargetPortletId != null;
	}
	public void buildAmiLinkingJs(StringBuilder pendingJs) {
		if (this.linkingTargetPortletId != null && this.linkingToPortletId != null) {
			new JsFunction(pendingJs, null, "amiOverlayForLinking").addParamQuoted(linkingTargetPortletId).addParamQuoted("Target")
					.addParamQuoted("className=ami_desktop_link_target").end();
			new JsFunction(pendingJs, null, "amiOverlayForLinking").addParamQuoted(linkingToPortletId).addParamQuoted("Source").addParamQuoted("className=ami_desktop_link_source")
					.end();
			new JsFunction(pendingJs, null, "amiDrawLinking").addParamQuoted(linkingTargetPortletId).addParamQuoted(linkingToPortletId).end();
		} else if (this.linkingTargetPortletId == null && this.linkingToPortletId == null) {
			new JsFunction(pendingJs, null, "amiDrawLinking").addParam(null).addParam(null).end();
		}

	}
	public boolean handlerCallback(String callback, Map<String, String> attributes) {
		String id = CH.getOrThrow(Caster_String.INSTANCE, attributes, "id");
		Portlet portlet = manager.getPortlet(id);
		if (this.linkingTargetPortletId != null && this.linkDialog == null) {
			this.linkingToPortletId = portlet.getPortletId();
			if (portlet instanceof AmiWebPortlet && !(portlet instanceof AmiWebAbstractContainerPortlet)) {
				Portlet t = manager.getPortlet(linkingTargetPortletId);
				String alias = "";//TODO:??
				if (t instanceof AmiWebPortlet) {
					AmiWebPortlet targetPortlet = (AmiWebPortlet) t;
					AmiWebPortlet sourcePortlet = (AmiWebPortlet) portlet;
					AmiWebDmManager dm = service.getDmManager();
					if (sourcePortlet == targetPortlet) {
						this.manager.showAlert("Cannot link panel to itself");
						this.linkingTargetPortletId = null;
						this.linkingToPortletId = null;
					} else if (targetPortlet instanceof AmiWebRealtimePortlet && sourcePortlet instanceof AmiWebLinkableVarsPortlet) {
						if (sourcePortlet == targetPortlet) {
							manager.showAlert("Cannot link realtime panel to itself");
							this.linkingTargetPortletId = null;
							this.linkingToPortletId = null;
						} else {
							AmiWebDmLinkImpl link = new AmiWebDmLinkImpl(dm, alias, null, null, (short) 0);
							link.setSourcePanelAliasDotId(sourcePortlet.getAmiLayoutFullAliasDotId());
							link.setTargetPanelAliasDotId(targetPortlet.getAmiLayoutFullAliasDotId());
							showEditRelationship(link);
						}
					} else if (sourcePortlet instanceof AmiWebLinkableVarsPortlet) {
						AmiWebDmLinkImpl link = new AmiWebDmLinkImpl(dm, alias, null, "Show " + targetPortlet.getAmiPanelId(), (short) 0);
						link.setTargetDm(this.linkingTargetDmAliasDotName);
						link.setSourcePanelAliasDotId(sourcePortlet.getAmiLayoutFullAliasDotId());
						link.setTargetPanelAliasDotId(targetPortlet.getAmiLayoutFullAliasDotId());
						showEditRelationship(link);
					} else if (sourcePortlet instanceof AmiWebDmPortlet) {
						Collection<AmiWebDmTableSchema> ids = AmiWebUtils.getUsedTableSchemas((AmiWebDmPortlet) sourcePortlet);
						if (ids.size() == 1) {
							AmiWebDmTableSchema first = CH.first(ids);
							AmiWebDmLinkImpl link = new AmiWebDmLinkImpl(dm, alias, null, "Show " + targetPortlet.getAmiPanelId(), (short) 0);
							link.setSourceDm(first.getDm().getAmiLayoutFullAliasDotId(), first.getName());
							link.setTargetDm(this.linkingTargetDmAliasDotName);
							link.setSourcePanelAliasDotId(sourcePortlet.getAmiLayoutFullAliasDotId());
							link.setTargetPanelAliasDotId(targetPortlet.getAmiLayoutFullAliasDotId());
							showEditRelationship(link);
						} else if (ids.size() > 1) {

							WebMenu menu = new BasicWebMenu("Choose Data Model", true);
							if (portlet instanceof AmiWebQueryFormPortlet) {

							}
							menu.add(new BasicWebMenuLink("Choose Source Data Model", false, "").setCssStyle(AmiWebConsts.TITLE_CSS));
							menu.add(new BasicWebMenuDivider());
							for (AmiWebDmTableSchema dmid : ids) {
								menu.add(new BasicWebMenuLink(dmid.getDm().getAmiLayoutFullAliasDotId() + " --> " + dmid.getName(), true,
										this.linkPrefix + "choose_source_" + dmid.getDm().getAmiLayoutFullAliasDotId() + "!" + dmid.getName()));
							}
							manager.showContextMenu(menu, desktop);
							this.chooseDatasourceMenu = menu;
						} else {
							manager.showAlert("Source Panel must have at least one data model");
						}
					}
				}
			} else {
				manager.showAlert("Cannot link to this type of portlet");
				this.linkingTargetPortletId = null;
				this.linkingToPortletId = null;
			}
			new JsFunction(manager.getPendingJs(), null, "amiStopLink").addParam(null).end();
			desktop.updateDashboard();
			return true;
		}
		return false;

	}
	public void handleMenuDismissed() {
		if (this.chooseDatasourceMenu != null) {
			this.chooseDatasourceMenu = null;
			resetLinkingTarget();
			desktop.updateDashboard();
		}

	}
	public static List<AmiWebDmLink> getLinksIncludeAll(AmiWebDesktopPortlet desktop, Portlet portlet) {
		if (!(portlet instanceof PortletContainer))
			return Collections.EMPTY_LIST;
		Collection<AmiWebPortlet> amiPortlets = PortletHelper.findPortletsByType((PortletContainer) portlet, AmiWebPortlet.class);
		Set<AmiWebDmLink> r = new HashSet<AmiWebDmLink>();

		for (AmiWebPortlet i : amiPortlets) {
			for (AmiWebDmLink link : i.getDmLinksFromThisPortlet()) {
				r.add(link);
			}
			for (AmiWebDmLink link : i.getDmLinksToThisPortlet()) {
				r.add(link);
			}
		}
		List<AmiWebDmLink> sink = new ArrayList<AmiWebDmLink>();
		CH.addAll(sink, r);
		return sink;
	}
	//	public AmiWebDmAddLinkPortlet showEditRelationship(AmiWebDmLink link) {
	//		return showEditRelationship(link, link.getSourcePanel(), link.getTargetPanel());
	//	}
	public AmiWebDmAddLinkPortlet showEditRelationship(AmiWebDmLink link) {
		AmiWebPortlet sourcePortlet = link.getSourcePanelNoThrow();
		if (sourcePortlet == null) {
			this.desktop.getManager().showAlert("Can not edit Relationship because Source Panel is unlinked");
			return null;
		}
		AmiWebPortlet targetPortlet = link.getTargetPanelNoThrow();
		if (targetPortlet == null) {
			this.desktop.getManager().showAlert("Can not edit Relationship because Source Panel is unlinked");
			return null;
		}
		if (this.linkDialog != null)
			this.linkDialog.close();
		AmiWebDatasourceWrapper targetDs = null;
		// Try to get the used datasource in target portlet
		if (targetPortlet instanceof AmiWebDmPortlet) {
			AmiWebDmPortlet targetDmPortlet = (AmiWebDmPortlet) targetPortlet;
			Set<String> usedDmAliasDotNames = targetDmPortlet.getUsedDmAliasDotNames();
			if (usedDmAliasDotNames.size() > 0) {
				String firstUsedDm = usedDmAliasDotNames.iterator().next();
				AmiWebDmsImpl dm = service.getDmManager().getDmByAliasDotName(firstUsedDm);
				if (dm != null) {
					List<AmiWebDatasourceWrapper> sink = new ArrayList<AmiWebDatasourceWrapper>();
					AmiWebDmUtils.getUnderlyingDatasources(manager, dm, sink);
					if (sink.size() > 0)
						targetDs = sink.get(0);
				}
			}
		}
		this.linkDialog = new AmiWebDmAddLinkPortlet(service.getPortletManager().generateConfig(), link, getSourceVars(link, sourcePortlet), getTargetVars(link, targetPortlet),
				targetDs);
		this.linkDialog.addPortletListener(this);
		service.getPortletManager().showDialog("Edit Relationship", this.linkDialog, -1, -1, false);
		return this.linkDialog;
	}
	public AmiWebDmAddLinkPortlet getCurrentLinkDialog() {
		return this.linkDialog;
	}
}
