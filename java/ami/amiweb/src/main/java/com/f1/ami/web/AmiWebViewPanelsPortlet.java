package com.f1.ami.web;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.web.dm.portlets.AmiWebPortletDefParser;
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
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.impl.IdentityHasher;
import com.f1.utils.structs.table.BasicTable;

public class AmiWebViewPanelsPortlet extends AmiWebScmBasePortlet
		implements WebContextMenuFactory, WebContextMenuListener, ConfirmDialogListener, AmiWebHiddenPanelsListener, AmiWebPanelsListener {

	public static final String COLUMN_OWNING_LAYOUT = "Owning Layout";
	public static final String COLUMN_PERMISSION = "Permissions";
	public static final String COLUMN_PANEL_ID = "Panel Id";
	public static final String COLUMN_PANEL_VISIBILITY = "Linking";
	public static final String COLUMN_PANEL_TYPE = "Type";

	public static final String STATUS_TRANSIENT = "Read";
	public static final String STATUS_LAYOUT_READONLY = "Read";
	public static final String STATUS_LAYOUT_WRITABLE = "Write";
	public static final String STATUS_PANEL_VISIBLE = "Linked";
	public static final String STATUS_PANEL_HIDDEN = "Unlinked";

	private static final Logger log = LH.get();

	private AmiWebService service;
	private AmiWebViewObjectsPortlet objectsPortlet;
	private FastTablePortlet fastTable;
	private TableListenable basic;
	private Map<String, Row> portletIdToRow = new HashMap<String, Row>(); // for visible panels.
	private Map<String, Row> panelIdToRow = new HashMap<String, Row>(); // for hidden panels.
	private Map<Row, AmiWebPortlet> rowToVisiblePanels = new HasherMap<Row, AmiWebPortlet>(IdentityHasher.INSTANCE);
	private Map<Row, AmiWebPortletDef> rowToHiddenPanels = new HasherMap<Row, AmiWebPortletDef>(IdentityHasher.INSTANCE);
	private int panelsCount;

	public AmiWebViewPanelsPortlet(PortletConfig config, AmiWebService service, AmiWebViewObjectsPortlet otp) {
		super(config);

		this.service = service;
		this.objectsPortlet = otp;
		TableListenable basicTable = new BasicTable(new String[] { COLUMN_OWNING_LAYOUT, COLUMN_PANEL_ID, COLUMN_PANEL_TYPE, COLUMN_PANEL_VISIBILITY, COLUMN_PERMISSION });
		BasicWebCellFormatter formatter = new BasicWebCellFormatter();

		this.basic = basicTable;
		this.fastTable = new FastTablePortlet(generateConfig(), this.basic, "Panel(s)");
		otp.addTableOptions(fastTable);

		this.fastTable.getTable().setMenuFactory(this);
		this.fastTable.getTable().addMenuListener(this);
		this.fastTable.getTable().addColumn(true, COLUMN_OWNING_LAYOUT, COLUMN_OWNING_LAYOUT, formatter).setWidth(300);
		this.fastTable.getTable().addColumn(true, COLUMN_PANEL_ID, COLUMN_PANEL_ID, formatter).setWidth(100);
		this.fastTable.getTable().addColumn(true, COLUMN_PANEL_TYPE, COLUMN_PANEL_TYPE, formatter).setWidth(100);
		this.fastTable.getTable().addColumn(true, COLUMN_PANEL_VISIBILITY, COLUMN_PANEL_VISIBILITY, formatter).setWidth(100);
		this.fastTable.getTable().addColumn(true, COLUMN_PERMISSION, COLUMN_PERMISSION, formatter).setWidth(100);

		for (AmiWebAliasPortlet visiblePanel : AmiWebUtils.getVisiblePanels(this.service)) {
			Object[] row = newEmptyRow();
			populateRowForVisiblePanel(row, (AmiWebPortlet) visiblePanel);
		}

		for (AmiWebPortletDef hiddenPanel : AmiWebUtils.getHiddenPanels(this.service, true)) {
			Object[] row = newEmptyRow();
			populateRowForHiddenPanel(row, hiddenPanel);
		}
		addChild(this.fastTable, 0, 0);
		this.service.getLayoutFilesManager().addHiddenPanelListener(this);
		this.service.addAmiWebPanelsListener(this);
		this.setRowSize(1, 40);
	}
	private Object[] newEmptyRow() {
		return new Object[basic.getColumnsCount()];
	}
	private void populateRowForVisiblePanel(Object[] r, AmiWebPortlet portlet) {
		AmiWebLayoutFile lf = service.getLayoutFilesManager().getLayoutByFullAlias(portlet.getAmiLayoutFullAlias());

		if (lf.getFullAlias().equals(AmiWebLayoutHelper.DEFAULT_ROOT_ALIAS))
			putNoFire(r, COLUMN_OWNING_LAYOUT, "<root>");
		else
			putNoFire(r, COLUMN_OWNING_LAYOUT, portlet.getAmiLayoutFullAlias());

		if (portlet.isTransient())
			putNoFire(r, COLUMN_PERMISSION, STATUS_TRANSIENT);
		else if (portlet.isReadonlyLayout())
			putNoFire(r, COLUMN_PERMISSION, STATUS_LAYOUT_READONLY);
		else
			putNoFire(r, COLUMN_PERMISSION, STATUS_LAYOUT_WRITABLE);

		putNoFire(r, COLUMN_PANEL_ID, portlet.getAmiLayoutFullAliasDotId());
		putNoFire(r, COLUMN_PANEL_VISIBILITY, STATUS_PANEL_VISIBLE);
		putNoFire(r, COLUMN_PANEL_TYPE, this.service.formatPortletBuilderIdToPanelType(portlet.getPortletConfig().getBuilderId()));

		Row row = fastTable.addRow(r);
		this.portletIdToRow.put(portlet.getPortletId(), row);
		this.rowToVisiblePanels.put(row, portlet);
		this.panelsCount++;
	}
	private void putNoFire(Object r[], String id, Object value) {
		r[this.basic.getColumn(id).getLocation()] = value;
	}
	private void populateRowForHiddenPanel(Object[] r, AmiWebPortletDef def) {
		AmiWebLayoutFile lf = def.getLayoutFile();
		if (lf.getFullAlias().equals(AmiWebLayoutHelper.DEFAULT_ROOT_ALIAS))
			putNoFire(r, COLUMN_OWNING_LAYOUT, "<root>");
		else
			putNoFire(r, COLUMN_OWNING_LAYOUT, lf.getFullAlias());
		if (lf.isReadonly())
			putNoFire(r, COLUMN_PERMISSION, STATUS_LAYOUT_READONLY);
		else
			putNoFire(r, COLUMN_PERMISSION, STATUS_LAYOUT_WRITABLE);
		putNoFire(r, COLUMN_PANEL_ID, def.getFullAdn());
		putNoFire(r, COLUMN_PANEL_VISIBILITY, STATUS_PANEL_HIDDEN);
		putNoFire(r, COLUMN_PANEL_TYPE, this.service.formatPortletBuilderIdToPanelType(def.getBuilderId()));
		Row row = this.fastTable.addRow(r);

		this.panelIdToRow.put(def.getAmiPanelId(), row);
		this.rowToHiddenPanels.put(row, def);
		this.panelsCount++;
	}
	public int getPanelsCount() {
		return this.panelsCount;
	}
	public void setPanelsCount(int count) {
		this.panelsCount = count;
	}
	public void resetPanelsCount() {
		setPanelsCount(0);
	}
	public Row getRowByPortletId(String portletId) {
		return this.portletIdToRow.get(portletId);
	}
	public Row getRowByPanelId(String panelId) {
		return this.panelIdToRow.get(panelId);
	}
	private Row getSelectedRow(WebTable table) {
		if (table.getSelectedRows().size() > 0)
			return table.getSelectedRows().get(0);
		else
			return null;
	}
	private boolean isPanelVisible(Row r) {
		return r.get(COLUMN_PANEL_VISIBILITY).equals(STATUS_PANEL_VISIBLE);
	}
	public FastTablePortlet getTablePortlet() {
		return this.fastTable;
	}
	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
		Row r = getSelectedRow(this.fastTable.getTable());
		if (r != null && isPanelVisible(r)) {
			AmiWebUtils.ensureVisibleWithDivider(this.rowToVisiblePanels.get(r), .60, true);
		}
	}
	@Override
	public WebMenu createMenu(WebTable table) {
		BasicWebMenu menu = new BasicWebMenu();
		if (this.fastTable.getTable().getSelectedRows().size() == 1) {
			Row r = getSelectedRow(this.fastTable.getTable());
			if (r != null && isPanelVisible(r)) {
				menu.add(new BasicWebMenuLink("Delete Panel", true, "delete_panel"));
				menu.add(new BasicWebMenuLink("Unlink Panel", true, "unlink_panel"));
				menu.add(new BasicWebMenuLink("Show Parent", true, "show_parent"));
			} else if (r != null && !isPanelVisible(r))
				menu.add(new BasicWebMenuLink("Delete Hidden Panel", true, "delete_hidden_panel"));

			menu.add(new BasicWebMenuLink("Show Configuration", true, AmiWebViewObjectsPortlet.ACTION_SHOW_COFIGURATION));
			if (objectsPortlet.getScmAdapter() != null)
				menu.add(new BasicWebMenuLink("Show History", true, AmiWebViewObjectsPortlet.ACTION_SHOW_HISTORY));
		} else if (this.fastTable.getTable().getSelectedRows().size() == 2) {
			menu.add(new BasicWebMenuLink("Diff Configurations", true, AmiWebViewObjectsPortlet.ACTION_DIFF_CONFIGURATIONS));
		}
		return menu;
	}
	@Override
	public void onContextMenu(WebTable table, String action) {
		Row r = getSelectedRow(this.fastTable.getTable());
		if (r != null) {
			if (action.equals("delete_panel"))
				onUserDeletePortlet(this.rowToVisiblePanels.get(r));
			else if (action.equals("unlink_panel"))
				onUserHidePortlet(this.rowToVisiblePanels.get(r));
			else if (action.equals("delete_hidden_panel"))
				onUserDeleteHiddenPanel(this.rowToHiddenPanels.get(r));
			else if (action.equals(AmiWebViewObjectsPortlet.ACTION_SHOW_COFIGURATION)) {
				boolean isHidden = r.get(COLUMN_PANEL_VISIBILITY).equals(STATUS_PANEL_HIDDEN) ? true : false;
				if (isHidden) {
					AmiWebPortletDef pd = this.rowToHiddenPanels.get(r);
					AmiWebUtils.showConfiguration(service, service.getLayoutFilesManager().toJson(pd.getPortletConfig()), pd.getFullAdn(), objectsPortlet.getEscapedSearchText());
				} else {
					AmiWebPortlet wp = this.rowToVisiblePanels.get(r);
					AmiWebUtils.showConfiguration(service, service.getLayoutFilesManager().toJson(wp.getConfiguration()), wp.getAmiLayoutFullAliasDotId(),
							objectsPortlet.getEscapedSearchText());
				}
			} else if (action.equals(AmiWebViewObjectsPortlet.ACTION_DIFF_CONFIGURATIONS)) {
				Row r1 = table.getSelectedRows().get(0);
				Row r2 = table.getSelectedRows().get(1);
				boolean isHidden1 = r1.get(COLUMN_PANEL_VISIBILITY).equals(STATUS_PANEL_HIDDEN) ? true : false;
				boolean isHidden2 = r2.get(COLUMN_PANEL_VISIBILITY).equals(STATUS_PANEL_HIDDEN) ? true : false;
				if (isHidden1 && isHidden2) {
					AmiWebPortletDef pd1 = this.rowToHiddenPanels.get(r1);
					AmiWebPortletDef pd2 = this.rowToHiddenPanels.get(r2);
					AmiWebUtils.diffConfigurations(service, service.getLayoutFilesManager().toJson(pd1.getPortletConfig()),
							service.getLayoutFilesManager().toJson(pd2.getPortletConfig()), pd1.getFullAdn(), pd2.getFullAdn(), objectsPortlet.getEscapedSearchText());
				} else if (!isHidden1 && !isHidden2) {
					AmiWebAliasPortlet ap1 = this.rowToVisiblePanels.get(r1);
					AmiWebAliasPortlet ap2 = this.rowToVisiblePanels.get(r2);
					AmiWebUtils.diffConfigurations(service, service.getLayoutFilesManager().toJson((ap1.getConfiguration())),
							service.getLayoutFilesManager().toJson(ap2.getConfiguration()), ap1.getAmiLayoutFullAliasDotId(), ap2.getAmiLayoutFullAliasDotId(),
							objectsPortlet.getEscapedSearchText());
				} else if (isHidden1) {
					AmiWebPortletDef pd = this.rowToHiddenPanels.get(r1);
					AmiWebAliasPortlet ap = this.rowToVisiblePanels.get(r2);
					AmiWebUtils.diffConfigurations(service, service.getLayoutFilesManager().toJson(pd.getPortletConfig()),
							service.getLayoutFilesManager().toJson(ap.getConfiguration()), pd.getFullAdn(), ap.getAmiLayoutFullAliasDotId(), objectsPortlet.getEscapedSearchText());
				} else {
					AmiWebAliasPortlet ap = this.rowToVisiblePanels.get(r1);
					AmiWebPortletDef pd = this.rowToHiddenPanels.get(r2);
					AmiWebUtils.diffConfigurations(service, service.getLayoutFilesManager().toJson((ap.getConfiguration())),
							service.getLayoutFilesManager().toJson(pd.getPortletConfig()), ap.getAmiLayoutFullAliasDotId(), pd.getFullAdn(), objectsPortlet.getEscapedSearchText());
				}
			} else if (action.equals(AmiWebViewObjectsPortlet.ACTION_SHOW_HISTORY)) {
				boolean isHidden = r.get(COLUMN_PANEL_VISIBILITY).equals(STATUS_PANEL_HIDDEN) ? true : false;
				if (isHidden) {
					AmiWebPortletDef pd = this.rowToHiddenPanels.get(r);
					String fileName = pd.getLayoutFile().getAbsoluteLocation();
					AmiWebPortletDefParser parser = new AmiWebPortletDefParser(service, pd);
					this.objectsPortlet.getHistoryPortlet().setObjectDefParser(parser);
					this.objectsPortlet.showObjectHistory(fileName);
				} else {
					AmiWebPortlet wp = this.rowToVisiblePanels.get(r);
					String fileName = service.getLayoutFilesManager().getLayoutByFullAlias(wp.getAmiLayoutFullAlias()).getAbsoluteLocation();
					AmiWebPortletDefParser parser = new AmiWebPortletDefParser(service, wp);
					this.objectsPortlet.getHistoryPortlet().setObjectDefParser(parser);
					this.objectsPortlet.showObjectHistory(fileName);
				}
			} else if ("show_parent".equals(action)) { // only available for visible panels
				AmiWebPortlet wp = this.rowToVisiblePanels.get(r);
				PortletHelper.ensureVisible(wp.getAmiParent());
			}
		}

	}
	public void onUserDeletePortlet(AmiWebPortlet portlet) {
		if (portlet.isReadonlyLayout()) {
			getManager().showAlert("Can not delete readonly panel");
			return;
		}
		ConfirmDialogPortlet dialog = new ConfirmDialogPortlet(generateConfig(), "Are you sure you want to delete '<B>" + portlet.getAmiLayoutFullAliasDotId() + "</B>' panel?",
				ConfirmDialogPortlet.TYPE_OK_CANCEL);
		dialog.addButton(ConfirmDialogPortlet.ID_YES, "Delete");

		dialog.setCallback("delete_panel2");
		dialog.setCorrelationData(portlet);
		dialog.addDialogListener(this);
		getManager().showDialog("Delete Panel", dialog);
	}
	public void onUserHidePortlet(AmiWebPortlet portlet) {
		ConfirmDialogPortlet dialog = new ConfirmDialogPortlet(generateConfig(), "Are you sure you want to unlink '<B>" + portlet.getAmiLayoutFullAliasDotId() + "</B>' panel?",
				ConfirmDialogPortlet.TYPE_OK_CANCEL);
		dialog.addButton(ConfirmDialogPortlet.ID_YES, "Unlink");

		dialog.setCallback("hide_panel2");
		dialog.setCorrelationData(portlet);
		dialog.addDialogListener(this);
		getManager().showDialog("Unlink Panel", dialog);

	}
	private void onUserDeleteHiddenPanel(AmiWebPortletDef def) {
		if (def.getLayoutFile().isReadonly()) {
			getManager().showAlert("Can not delete readonly hidden panel");
			return;
		}
		ConfirmDialogPortlet dialog = new ConfirmDialogPortlet(generateConfig(), "Are you sure you want to delete '<B>" + def.getFullAdn() + "</B>' hidden panel?",
				ConfirmDialogPortlet.TYPE_OK_CANCEL);
		dialog.addButton(ConfirmDialogPortlet.ID_YES, "Delete");

		dialog.setCallback("delete_hidden_panel2");
		dialog.setCorrelationData(def);
		dialog.addDialogListener(this);
		getManager().showDialog("Delete Hidden Panel", dialog);
	}
	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if (source.getCallback().equals("delete_panel2") && id.equals(ConfirmDialogPortlet.ID_YES)) {
			try {
				AmiWebPortlet portlet = (AmiWebPortlet) source.getCorrelationData();
				this.service.getDesktop().deletePanel(portlet.getPortletId(), true);
			} catch (Exception e) {
				LH.info(log, "Exception deleting panel " + e);
			}
		} else if (source.getCallback().equals("hide_panel2") && id.equals(ConfirmDialogPortlet.ID_YES)) {
			try {
				AmiWebPortlet portlet = (AmiWebPortlet) source.getCorrelationData();
				this.service.getDesktop().deletePanel(portlet.getPortletId(), false);
			} catch (Exception e) {
				LH.info(log, "Exception hiding panel " + e);
			}
		} else if (source.getCallback().equals("delete_hidden_panel2") && id.equals(ConfirmDialogPortlet.ID_YES)) {
			try {
				AmiWebPortletDef def = (AmiWebPortletDef) source.getCorrelationData();
				AmiWebPortletDef parent = def.getLayoutFile().findHiddenParent(def.getAmiPanelId());
				def.getLayoutFile().onShowPanel(def.getAmiPanelId());
				// delete panel if immediate container panel is a divider.
				if (parent != null && parent.getBuilderId() == AmiWebDividerPortlet.Builder.ID) {
					String removedHiddenDivider = CH.removeOrThrow(parent.getLayoutFile().getHiddenPanelIds(), parent.getAmiPanelId());
					if (SH.is(removedHiddenDivider))
						this.service.getLayoutFilesManager().fireOnHiddenPanelRemoved(parent.getLayoutFile(), parent);
				}
			} catch (Exception e) {
				LH.info(log, "Exception deleting hidden panel " + e);
			}

		}
		source.closeDialog();
		return true;
	}
	@Override
	public void onClosed() {
		this.fastTable.getTable().setMenuFactory(null);
		this.fastTable.getTable().removeMenuListener(this);
		this.service.getLayoutFilesManager().removeHiddenPanelListener(this);
		this.service.removeAmiWebPanelsListener(this);
		super.onClosed();
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
	@Override
	public void onNoSelectedChanged(FastWebTable fastWebTable) {
	}
	@Override
	public void onHiddenPanelIdChanged(AmiWebLayoutFile amiWebLayoutFile, String oldPanelId, String newPanelId) {
	}
	@Override
	public void onAmiWebPanelAdded(AmiWebPortlet portlet) {
		Object[] r = newEmptyRow();
		populateRowForVisiblePanel(r, portlet);
		this.objectsPortlet.updateTabsTitle();
	}
	@Override
	public void onAmiWebPanelRemoved(AmiWebPortlet portlet, boolean isHide) {
		Row r = this.portletIdToRow.get(portlet.getPortletId());
		if (r != null) {
			this.fastTable.removeRow(this.portletIdToRow.get(portlet.getPortletId()));
			this.portletIdToRow.remove(portlet.getPortletId());
			this.rowToVisiblePanels.remove(this.portletIdToRow.get(portlet.getPortletId()));
			this.panelsCount--;
			this.objectsPortlet.updateTabsTitle();
		}
	}

	@Override
	public void onAmiWebPanelLocationChanged(AmiWebPortlet portlet) {
	}
	@Override
	public void onAmiWebPanelIdChanged(AmiWebPortlet portlet, String oldAdn, String newAdn) {
		Row r = this.portletIdToRow.get(portlet.getPortletId());
		if (r != null)
			r.put(COLUMN_PANEL_ID, newAdn);
	}
	@Override
	public void onHiddenPanelRemoved(AmiWebLayoutFile amiWebLayoutFile, AmiWebPortletDef def) {
		Row r = this.panelIdToRow.get(def.getAmiPanelId());
		if (r != null) {
			this.fastTable.removeRow(r);
			this.panelIdToRow.remove(def.getAmiPanelId());
			this.panelsCount--;
			this.objectsPortlet.updateTabsTitle();
		}
	}
	@Override
	public void onHiddenPanelAdded(AmiWebLayoutFile amiWebLayoutFile, AmiWebPortletDef def) {
		Object[] r = newEmptyRow();
		populateRowForHiddenPanel(r, def);
		this.objectsPortlet.updateTabsTitle();
	}
	@Override
	public void onScroll(int viewTop, int viewPortHeight, long contentWidth, long contentHeight) {
	}
}
