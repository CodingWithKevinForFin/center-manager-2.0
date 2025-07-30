package com.f1.ami.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmFilter;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmLinkDefParser;
import com.f1.ami.web.dm.AmiWebDmManager;
import com.f1.ami.web.dm.AmiWebDmManagerImpl;
import com.f1.ami.web.dm.AmiWebDmManagerListener;
import com.f1.ami.web.scm.AmiWebScmBasePortlet;
import com.f1.base.Row;
import com.f1.base.TableListenable;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.table.BasicTable;

public class AmiWebViewRelationshipsPortlet extends AmiWebScmBasePortlet
		implements WebContextMenuFactory, WebContextMenuListener, AmiWebDmManagerListener, ConfirmDialogListener, AmiWebHiddenPanelsListener, AmiWebPanelsListener {

	public static final String COLUMN_OWNING_LAYOUT = "Owning Layout";
	public static final String COLUMN_PERMISSION = "Permissions";
	public static final String COLUMN_REL_ID = "Relationship Id";
	public static final String COLUMN_SOURCE_PANEL_ID = "Source Panel Id";
	public static final String COLUMN_TARGET_PANEL_ID = "Target Panel Id";
	public static final String COLUMN_SOURCE_PANEL_VISIBILITY = "Source Panel Linking";
	public static final String COLUMN_TARGET_PANEL_VISIBILITY = "Target Panel Linking";
	public static final String COLUMN_RELATIONSHIP = "Relationship";
	public static final String COLUMN_LINK = "Link";
	public static final String COLUMN_REL_BEHAVIOR = "Run On";

	public static final String STATUS_PANEL_HIDDEN = "Unlinked";
	public static final String STATUS_PANEL_VISIBLE = "Linked";
	public static final String STATUS_TRANSIENT = "Transient";
	public static final String STATUS_LAYOUT_READONLY = "Read";
	public static final String STATUS_LAYOUT_WRITABLE = "Write";

	private static final Logger log = LH.get();
	private AmiWebService service;
	private AmiWebViewObjectsPortlet objectsPortlet;
	private FastTablePortlet fastTable;
	private TableListenable basic;
	private final Map<String, Row> linkUid2Row;
	private final BasicMultiMap.Set<String, AmiWebDmLink> panelId2DmLinks = new BasicMultiMap.Set<String, AmiWebDmLink>();
	private final AmiWebDesktopLinkHelper linkHelper;
	private int relCount;

	public AmiWebViewRelationshipsPortlet(PortletConfig config, AmiWebService service, AmiWebViewObjectsPortlet otp) {
		super(config);
		this.service = service;
		this.objectsPortlet = otp;
		this.linkUid2Row = new HashMap<String, Row>();
		this.linkHelper = new AmiWebDesktopLinkHelper(this.service.getDesktop(), "");

		TableListenable basicTable = new BasicTable(new String[] { COLUMN_OWNING_LAYOUT, COLUMN_REL_ID, COLUMN_SOURCE_PANEL_ID, COLUMN_TARGET_PANEL_ID, COLUMN_PERMISSION,
				COLUMN_SOURCE_PANEL_VISIBILITY, COLUMN_TARGET_PANEL_VISIBILITY, COLUMN_REL_BEHAVIOR, COLUMN_RELATIONSHIP, COLUMN_LINK }); // TODO: refactor to use hasher map.
		BasicWebCellFormatter formatter = new BasicWebCellFormatter();

		this.basic = basicTable;
		this.fastTable = new FastTablePortlet(generateConfig(), this.basic, "Relationship(s)");
		otp.addTableOptions(fastTable);

		this.fastTable.getTable().setMenuFactory(this);
		this.fastTable.getTable().addMenuListener(this);
		this.fastTable.getTable().addColumn(true, COLUMN_OWNING_LAYOUT, COLUMN_OWNING_LAYOUT, formatter).setWidth(150);
		this.fastTable.getTable().addColumn(true, COLUMN_REL_ID, COLUMN_REL_ID, formatter).setWidth(100);
		this.fastTable.getTable().addColumn(true, COLUMN_SOURCE_PANEL_ID, COLUMN_SOURCE_PANEL_ID, formatter).setWidth(100);
		this.fastTable.getTable().addColumn(true, COLUMN_TARGET_PANEL_ID, COLUMN_TARGET_PANEL_ID, formatter).setWidth(100);
		this.fastTable.getTable().addColumn(true, COLUMN_PERMISSION, COLUMN_PERMISSION, formatter).setWidth(100);
		this.fastTable.getTable().addColumn(true, COLUMN_SOURCE_PANEL_VISIBILITY, COLUMN_SOURCE_PANEL_VISIBILITY, formatter).setWidth(150);
		this.fastTable.getTable().addColumn(true, COLUMN_TARGET_PANEL_VISIBILITY, COLUMN_TARGET_PANEL_VISIBILITY, formatter).setWidth(150);
		this.fastTable.getTable().addColumn(true, COLUMN_REL_BEHAVIOR, COLUMN_REL_BEHAVIOR, formatter).setWidth(120);
		this.fastTable.getTable().addColumn(true, COLUMN_RELATIONSHIP, COLUMN_RELATIONSHIP, formatter).setWidth(400);

		for (AmiWebDmLink link : this.service.getDmManager().getDmLinks()) {
			Object[] row = newEmptyRow();
			populateRow(row, link);
		}
		this.fastTable.getTable().addColumn(false, COLUMN_LINK, COLUMN_LINK, formatter);

		addChild(this.fastTable, 0, 0);
		this.service.getDmManager().addDmManagerListener(this);
		this.service.getLayoutFilesManager().addHiddenPanelListener(this);
		this.service.addAmiWebPanelsListener(this);
		this.setRowSize(1, 40);
	}
	private Object[] newEmptyRow() {
		return new Object[basic.getColumnsCount()];
	}

	private void putNoFire(Object[] r, String id, Object value) {
		r[basic.getColumn(id).getLocation()] = value;
	}
	public int getRelCount() {
		return this.relCount;
	}
	public void setRelCount(int newCount) {
		this.relCount = newCount;
	}
	public void resetRelCount() {
		setRelCount(0);
	}
	private void populateRow(Object[] r, AmiWebDmLink link) {
		AmiWebLayoutFile lf = service.getLayoutFilesManager().getLayoutByFullAlias(link.getAmiLayoutFullAlias());
		if (lf.getFullAlias().equals(AmiWebLayoutHelper.DEFAULT_ROOT_ALIAS))
			putNoFire(r, COLUMN_OWNING_LAYOUT, "<root>");
		else
			putNoFire(r, COLUMN_OWNING_LAYOUT, link.getAmiLayoutFullAlias());

		if (lf.isTransient())
			putNoFire(r, COLUMN_PERMISSION, STATUS_TRANSIENT);
		else if (lf.isReadonly())
			putNoFire(r, COLUMN_PERMISSION, STATUS_LAYOUT_READONLY);
		else
			putNoFire(r, COLUMN_PERMISSION, STATUS_LAYOUT_WRITABLE);

		putNoFire(r, COLUMN_REL_ID, link.getRelationshipId());
		putNoFire(r, COLUMN_SOURCE_PANEL_ID, link.getSourcePanelAliasDotId());
		putNoFire(r, COLUMN_TARGET_PANEL_ID, link.getTargetPanelAliasDotId());

		AmiWebPortlet sourcePanel = link.getSourcePanelNoThrow();
		AmiWebPortlet targetPanel = link.getTargetPanelNoThrow();

		// visibility
		if (sourcePanel != null)
			putNoFire(r, COLUMN_SOURCE_PANEL_VISIBILITY, STATUS_PANEL_VISIBLE);
		else
			putNoFire(r, COLUMN_SOURCE_PANEL_VISIBILITY, STATUS_PANEL_HIDDEN);

		if (targetPanel != null)
			putNoFire(r, COLUMN_TARGET_PANEL_VISIBILITY, STATUS_PANEL_VISIBLE);
		else
			putNoFire(r, COLUMN_TARGET_PANEL_VISIBILITY, STATUS_PANEL_HIDDEN);

		putNoFire(r, COLUMN_RELATIONSHIP, link.getSourcePanelAliasDotId() + "-->" + link.getTargetPanelAliasDotId());

		//behavior
		if (link.isRunOnSelect())
			putNoFire(r, COLUMN_REL_BEHAVIOR, "User select");
		else if (link.isRunOnRightClickMenu())
			putNoFire(r, COLUMN_REL_BEHAVIOR, "Right click menu");
		else if (link.isRunOnDoubleClick())
			putNoFire(r, COLUMN_REL_BEHAVIOR, "Double click");
		else if (link.isRunOnAmiScript())
			putNoFire(r, COLUMN_REL_BEHAVIOR, "AmiScript only");
		else
			putNoFire(r, COLUMN_REL_BEHAVIOR, "Unknown behavior");

		putNoFire(r, COLUMN_LINK, link);

		Row row = this.fastTable.addRow(r);
		this.panelId2DmLinks.putMulti(link.getSourcePanelAliasDotId(), link);
		this.panelId2DmLinks.putMulti(link.getTargetPanelAliasDotId(), link);
		this.linkUid2Row.put(link.getLinkUid(), row);
		this.relCount++;
	}
	public Row getRowByLinkUid(String uid) {
		return this.linkUid2Row.get(uid);
	}
	public FastTablePortlet getTablePortlet() {
		return this.fastTable;
	}
	@Override
	public void onClosed() {
		this.fastTable.getTable().setMenuFactory(null);
		this.fastTable.getTable().removeMenuListener(this);
		this.service.getLayoutFilesManager().removeHiddenPanelListener(this);
		this.service.removeAmiWebPanelsListener(this);
		this.service.getDmManager().removeDmManagerListener(this);
		super.onClosed();
	}
	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
		Row r = getSelectedRow(this.fastTable.getTable());
		if (r != null) {
			AmiWebDmLink link = (AmiWebDmLink) r.get(COLUMN_LINK);
			if (link.getSourcePanelNoThrow() != null && link.getTargetPanelNoThrow() != null)
				linkHelper.showEditRelationship(link);
			else
				getManager().showAlert("Both panels need to be visible in order to bring up the relationship editor.");

		}
	}
	public Row getSelectedRow(WebTable table) {
		if (table.getSelectedRows().size() > 0)
			return table.getSelectedRows().get(0);
		else
			return null;
	}
	@Override
	public void onDmLinkAdded(AmiWebDmManager amiWebDmManagerImpl, AmiWebDmLink link) {
		Object[] r = newEmptyRow();
		populateRow(r, link);
		this.objectsPortlet.updateTabsTitle();
	}
	@Override
	public void onDmLinkRemoved(AmiWebDmManager amiWebDmManagerImpl, AmiWebDmLink link) {
		this.fastTable.removeRow(this.linkUid2Row.get(link.getLinkUid()));
		this.linkUid2Row.remove(link.getLinkUid());

		String sourcePanelAdn = link.getSourcePanelAliasDotId();
		String targetPanelAdn = link.getTargetPanelAliasDotId();
		this.panelId2DmLinks.removeMulti(sourcePanelAdn, link);
		this.panelId2DmLinks.removeMulti(targetPanelAdn, link);
		this.relCount--;
		this.objectsPortlet.updateTabsTitle();
	}
	@Override
	public void onContextMenu(WebTable table, String action) {
		Row r = getSelectedRow(this.fastTable.getTable());
		if (r != null) {
			if (action.equals("remove_link")) {
				ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(generateConfig(), "Are you sure you want to remove the relationship?", ConfirmDialogPortlet.TYPE_YES_NO);
				cdp.setCallback("remove_link2");
				cdp.addDialogListener(this);
				getManager().showDialog("Confirm Delete", cdp);
			} else if (action.equals("show_editor"))
				onUserDblclick(null, null, null);
			else if (action.equals(AmiWebViewObjectsPortlet.ACTION_SHOW_COFIGURATION)) {
				AmiWebDmLink link = (AmiWebDmLink) r.get(COLUMN_LINK);
				AmiWebUtils.showConfiguration(service, service.getLayoutFilesManager().toJson(link.getConfiguration()), link.getAmiLayoutFullAliasDotId(),
						objectsPortlet.getEscapedSearchText());
			} else if (action.equals(AmiWebViewObjectsPortlet.ACTION_DIFF_CONFIGURATIONS)) {
				Row r1 = table.getSelectedRows().get(0);
				Row r2 = table.getSelectedRows().get(1);
				AmiWebDmLink leftLink = (AmiWebDmLink) r1.get(COLUMN_LINK);
				AmiWebDmLink rightLink = (AmiWebDmLink) r2.get(COLUMN_LINK);
				AmiWebUtils.diffConfigurations(service, service.getLayoutFilesManager().toJson(leftLink.getConfiguration()),
						service.getLayoutFilesManager().toJson(rightLink.getConfiguration()), leftLink.getAmiLayoutFullAliasDotId(), rightLink.getAmiLayoutFullAliasDotId(),
						objectsPortlet.getEscapedSearchText());
			} else if (action.equals(AmiWebViewObjectsPortlet.ACTION_SHOW_HISTORY)) {
				AmiWebDmLink link = (AmiWebDmLink) r.get(COLUMN_LINK);
				String fileName = service.getLayoutFilesManager().getLayoutByFullAlias(link.getAmiLayoutFullAlias()).getAbsoluteLocation();
				AmiWebDmLinkDefParser linkParser = new AmiWebDmLinkDefParser(service, link);
				this.objectsPortlet.getHistoryPortlet().setObjectDefParser(linkParser);
				this.objectsPortlet.showObjectHistory(fileName);
			} else if ("show_src_panel".equals(action)) {
				AmiWebDmLink link = (AmiWebDmLink) r.get(COLUMN_LINK);
				PortletHelper.ensureVisible((link.getSourcePanel()));
			} else if ("show_target_panel".equals(action)) {
				AmiWebDmLink link = (AmiWebDmLink) r.get(COLUMN_LINK);
				PortletHelper.ensureVisible((link.getTargetPanel()));
			}
		}
	}
	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		Row r = getSelectedRow(this.fastTable.getTable());
		if (source.getCallback().equals("remove_link2") && id.equals(ConfirmDialogPortlet.ID_YES)) {
			try {
				if (r != null) {
					AmiWebDmLink linkToRemove = (AmiWebDmLink) r.get(COLUMN_LINK);
					service.getDmManager().removeDmLink(linkToRemove.getLinkUid());
					onDmLinkRemoved(service.getDmManager(), linkToRemove);
				}
			} catch (Exception e) {
				LH.info(log, "Exception deleting relationship " + e);
				source.closeDialog();
				return false;
			}
		}
		source.closeDialog();
		return true;
	}
	@Override
	public WebMenu createMenu(WebTable table) {
		BasicWebMenu menu = new BasicWebMenu();
		if (this.fastTable.getTable().getSelectedRows().size() == 1) {
			Row r = getSelectedRow(this.fastTable.getTable());
			AmiWebDmLink link = (AmiWebDmLink) r.get(COLUMN_LINK);
			menu.add(new BasicWebMenuLink("Remove Relationship", isRelationshipFromReadonlyLayout(link) ? false : true, "remove_link"));
			menu.add(new BasicWebMenuLink("Edit Relationship", true, "show_editor"));
			boolean sv = link.getSourcePanelNoThrow() != null;
			boolean tv = link.getTargetPanelNoThrow() != null;
			menu.add(new BasicWebMenuLink(sv ? "Show Source Panel" : "Show Source Panel (Hidden)", sv ? true : false, "show_src_panel"));
			menu.add(new BasicWebMenuLink(tv ? "Show Target Panel" : "Show Target Panel (Hidden)", tv ? true : false, "show_target_panel"));
			menu.add(new BasicWebMenuLink("Show Configuration", true, AmiWebViewObjectsPortlet.ACTION_SHOW_COFIGURATION));
			if (objectsPortlet.getScmAdapter() != null)
				menu.add(new BasicWebMenuLink("Show History", true, AmiWebViewObjectsPortlet.ACTION_SHOW_HISTORY));
		} else if (this.fastTable.getTable().getSelectedRows().size() == 2) {
			menu.add(new BasicWebMenuLink("Diff Configurations", true, AmiWebViewObjectsPortlet.ACTION_DIFF_CONFIGURATIONS));
		}
		return menu;
	}
	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
	}
	@Override
	public void onCellMousedown(WebTable table, Row row, WebColumn col) {
	}
	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
		this.objectsPortlet.clearHistoryTable();
	}

	private boolean isRelationshipFromReadonlyLayout(AmiWebDmLink link) {
		return service.getLayoutFilesManager().getLayoutByFullAlias(link.getAmiLayoutFullAlias()).isReadonly();
	}
	@Override
	public void onDmUpdated(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm dm) {
	}
	@Override
	public void onDmAdded(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm dm) {
	}
	@Override
	public void onDmRemoved(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm dm) {
	}
	@Override
	public void onDmNameChanged(AmiWebDmManager amiWebDmManagerImpl, String oldAliasDotName, AmiWebDm dm) {
	}
	@Override
	public void onDmDependencyAdded(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm upper, AmiWebDm lower) {
	}
	@Override
	public void onDmDependencyRemoved(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm upper, AmiWebDm lower) {
	}
	@Override
	public void onDmManagerInitDone() {
	}
	@Override
	public void onDmDependencyAdded(AmiWebDmManager manager, AmiWebDmPortlet target, String dmName, String tableName) {
	}
	@Override
	public void onDmDependencyRemoved(AmiWebDmManagerImpl amiWebDmManagerImpl, AmiWebDmPortlet target, String dmName, String tableName) {
	}
	@Override
	public void onFilterDependencyAdded(AmiWebDmManagerImpl amiWebDmManagerImpl, AmiWebDmFilter target, String dmName, String tableName) {
	}
	@Override
	public void onFilterDependencyRemoved(AmiWebDmManagerImpl amiWebDmManagerImpl, AmiWebDmFilter target, String dmName, String tableName) {
	}
	@Override
	public void onNoSelectedChanged(FastWebTable fastWebTable) {
	}
	@Override
	public void onAmiWebPanelAdded(AmiWebPortlet portlet) {
		Set<AmiWebDmLink> hiddenLinks = this.panelId2DmLinks.get(portlet.getAmiLayoutFullAliasDotId());
		if (hiddenLinks != null) {
			for (AmiWebDmLink link : hiddenLinks) {
				Row r = this.linkUid2Row.get(link.getLinkUid());
				if (r != null) {
					AmiWebPortlet sourcePanel = link.getSourcePanelNoThrow();
					AmiWebPortlet targetPanel = link.getTargetPanelNoThrow();

					if (sourcePanel != null)
						r.put(COLUMN_SOURCE_PANEL_VISIBILITY, STATUS_PANEL_VISIBLE);

					if (targetPanel != null)
						r.put(COLUMN_TARGET_PANEL_VISIBILITY, STATUS_PANEL_VISIBLE);
				}
			}
		}

	}
	@Override
	public void onAmiWebPanelRemoved(AmiWebPortlet portlet, boolean isHide) {
		if (isHide) {
			if (portlet.getDmLinksFromThisPortlet().size() != 0) { // source
				for (AmiWebDmLink link : portlet.getDmLinksFromThisPortlet()) {
					Row r = this.linkUid2Row.get(link.getLinkUid());
					r.put(COLUMN_SOURCE_PANEL_VISIBILITY, STATUS_PANEL_HIDDEN);
					this.panelId2DmLinks.putMulti(portlet.getAmiLayoutFullAliasDotId(), link);
				}
			}
			if (portlet.getDmLinksToThisPortlet().size() != 0) { // target
				for (AmiWebDmLink link : portlet.getDmLinksToThisPortlet()) {
					Row r = this.linkUid2Row.get(link.getLinkUid());
					r.put(COLUMN_TARGET_PANEL_VISIBILITY, STATUS_PANEL_HIDDEN);
					this.panelId2DmLinks.putMulti(portlet.getAmiLayoutFullAliasDotId(), link);
				}
			}
		} else {
			// deleted: check if the panel has links, if so need to delete entry(ies) from map.
			if (portlet.getDmLinksFromThisPortlet().size() != 0) { // source
				for (AmiWebDmLink link : portlet.getDmLinksFromThisPortlet())
					this.panelId2DmLinks.removeMulti(portlet.getAmiLayoutFullAliasDotId(), link);
			}
			if (portlet.getDmLinksToThisPortlet().size() != 0) { // target
				for (AmiWebDmLink link : portlet.getDmLinksToThisPortlet())
					this.panelId2DmLinks.removeMulti(portlet.getAmiLayoutFullAliasDotId(), link);
			}
		}
	}
	@Override
	public void onAmiWebPanelLocationChanged(AmiWebPortlet portlet) {
	}
	@Override
	public void onAmiWebPanelIdChanged(AmiWebPortlet portlet, String oldAdn, String newAdn) {
		Set<AmiWebDmLink> tempLinks = this.panelId2DmLinks.remove(oldAdn);
		if (tempLinks != null) {
			this.panelId2DmLinks.put(newAdn, tempLinks);
			// reflect name change in row(s).
			for (AmiWebDmLink link : tempLinks) {
				AmiWebPortlet sourcePanel = link.getSourcePanelNoThrow();
				AmiWebPortlet targetPanel = link.getTargetPanelNoThrow();
				if (sourcePanel != null && OH.eq(sourcePanel, portlet))
					this.linkUid2Row.get(link.getLinkUid()).put(COLUMN_SOURCE_PANEL_ID, newAdn);
				if (targetPanel != null && OH.eq(targetPanel, portlet))
					this.linkUid2Row.get(link.getLinkUid()).put(COLUMN_TARGET_PANEL_ID, newAdn);
			}
		}
	}
	@Override
	public void onHiddenPanelIdChanged(AmiWebLayoutFile amiWebLayoutFile, String oldPanelId, String newPanelId) {
	}
	@Override
	public void onHiddenPanelRemoved(AmiWebLayoutFile amiWebLayoutFile, AmiWebPortletDef def) {
		Set<AmiWebDmLink> links = this.panelId2DmLinks.get(def.getFullAdn());
		if (links != null) {
			for (AmiWebDmLink link : links) {
				Row r = this.linkUid2Row.get(link.getLinkUid());
				if (r != null) {
					if (def.getFullAdn().equals(link.getSourcePanelAliasDotId()))
						r.put(COLUMN_SOURCE_PANEL_VISIBILITY, STATUS_PANEL_VISIBLE);
					if (def.getFullAdn().equals(link.getTargetPanelAliasDotId()))
						r.put(COLUMN_TARGET_PANEL_VISIBILITY, STATUS_PANEL_VISIBLE);
				}
			}
		}
	}
	@Override
	public void onHiddenPanelAdded(AmiWebLayoutFile amiWebLayoutFile, AmiWebPortletDef def) {
	}
	@Override
	public void onScroll(int viewTop, int viewPortHeight, long contentWidth, long contentHeight) {
	}
}
