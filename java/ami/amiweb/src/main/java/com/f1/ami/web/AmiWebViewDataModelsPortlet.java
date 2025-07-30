package com.f1.ami.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmDefParser;
import com.f1.ami.web.dm.AmiWebDmFilter;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmManager;
import com.f1.ami.web.dm.AmiWebDmManagerImpl;
import com.f1.ami.web.dm.AmiWebDmManagerListener;
import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.ami.web.scm.AmiWebScmBasePortlet;
import com.f1.base.Row;
import com.f1.base.TableListenable;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
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
import com.f1.utils.SH;
import com.f1.utils.structs.table.BasicTable;

public class AmiWebViewDataModelsPortlet extends AmiWebScmBasePortlet implements WebContextMenuFactory, WebContextMenuListener, AmiWebDmManagerListener, ConfirmDialogListener {

	public static final String COLUMN_OWNING_LAYOUT = "Owning Layout";
	public static final String COLUMN_PERMISSION = "Permissions";
	public static final String COLUMN_DM_ADN = "Datamodel Id";
	public static final String COLUMN_DM_TYPE = "Type";
	public static final String COLUMN_AUTO_RUN = "Auto Run";
	public static final String COLUMN_AUTO_REQUERY = "Auto Requery (s)";
	public static final String COLUMN_CONFLATE_QUERY = "Conflate Query (s)";
	public static final String COLUMN_LIMIT = "Limit";
	public static final String COLUMN_TIMEOUT = "Timeout (sec)";
	public static final String COLUMN_DATASOURCE = "Datasource";
	public static final String COLUMN_DATAMODELS = "Datamodels";
	public static final String COLUMN_KEEP_OUTPUT = "Keep Output";
	public static final String COLUMN_DYNAMIC_DM = "Dynamic DM";
	public static final String COLUMN_SUBSCRIPTIONS = "Subscriptions";

	public static final String STATUS_LAYOUT_TRANSIENT = "Transient";
	public static final String STATUS_LAYOUT_READONLY = "Read";
	public static final String STATUS_LAYOUT_WRITABLE = "Write";

	private static final Logger log = LH.get();

	private AmiWebService service;
	private AmiWebViewObjectsPortlet objectsPortlet;
	private FastTablePortlet fastTable;
	private TableListenable basic;
	final private Map<String, Row> dmAdnToRow = new HashMap<String, Row>();
	//	final private Map<String, AmiWebDmLayoutNode> dmAdnToLayoutNode = new HashMap<String, AmiWebDmLayoutNode>();
	private int dmCount;

	public AmiWebViewDataModelsPortlet(PortletConfig config, AmiWebService service, AmiWebViewObjectsPortlet otp) {
		super(config);
		this.service = service;
		this.objectsPortlet = otp;
		TableListenable basicTable = new BasicTable(new String[] { COLUMN_OWNING_LAYOUT, COLUMN_DM_ADN, COLUMN_PERMISSION, COLUMN_DM_TYPE, COLUMN_AUTO_RUN, COLUMN_AUTO_REQUERY,
				COLUMN_CONFLATE_QUERY, COLUMN_LIMIT, COLUMN_TIMEOUT, COLUMN_DATASOURCE, COLUMN_DATAMODELS, COLUMN_KEEP_OUTPUT, COLUMN_DYNAMIC_DM, COLUMN_SUBSCRIPTIONS });
		BasicWebCellFormatter formatter = new BasicWebCellFormatter();

		this.basic = basicTable;
		this.fastTable = new FastTablePortlet(generateConfig(), this.basic, "Datamodel(s)");
		otp.addTableOptions(fastTable);

		this.fastTable.getTable().setMenuFactory(this);
		this.fastTable.getTable().addMenuListener(this);
		this.fastTable.getTable().addColumn(true, COLUMN_OWNING_LAYOUT, COLUMN_OWNING_LAYOUT, formatter).setWidth(100);
		this.fastTable.getTable().addColumn(true, COLUMN_DM_ADN, COLUMN_DM_ADN, formatter).setWidth(150);
		this.fastTable.getTable().addColumn(true, COLUMN_PERMISSION, COLUMN_PERMISSION, formatter).setWidth(80);
		this.fastTable.getTable().addColumn(true, COLUMN_DM_TYPE, COLUMN_DM_TYPE, formatter).setWidth(90);
		this.fastTable.getTable().addColumn(true, COLUMN_AUTO_RUN, COLUMN_AUTO_RUN, formatter).setWidth(90);
		this.fastTable.getTable().addColumn(true, COLUMN_AUTO_REQUERY, COLUMN_AUTO_REQUERY, formatter).setWidth(105);
		this.fastTable.getTable().addColumn(true, COLUMN_CONFLATE_QUERY, COLUMN_CONFLATE_QUERY, formatter).setWidth(125);
		this.fastTable.getTable().addColumn(true, COLUMN_LIMIT, COLUMN_LIMIT, formatter).setWidth(70);
		this.fastTable.getTable().addColumn(true, COLUMN_TIMEOUT, COLUMN_TIMEOUT, formatter).setWidth(100);
		this.fastTable.getTable().addColumn(true, COLUMN_DATASOURCE, COLUMN_DATASOURCE, formatter).setWidth(100);
		this.fastTable.getTable().addColumn(true, COLUMN_DATAMODELS, COLUMN_DATAMODELS, formatter).setWidth(150);
		this.fastTable.getTable().addColumn(true, COLUMN_KEEP_OUTPUT, COLUMN_KEEP_OUTPUT, formatter).setWidth(90);
		this.fastTable.getTable().addColumn(true, COLUMN_DYNAMIC_DM, COLUMN_DYNAMIC_DM, formatter).setWidth(90);
		this.fastTable.getTable().addColumn(true, COLUMN_SUBSCRIPTIONS, COLUMN_SUBSCRIPTIONS, formatter).setWidth(250);

		for (AmiWebDm dm : this.service.getDmManager().getDatamodels()) {
			Object[] row = newEmptyRow();
			populateRow(row, dm);
		}

		addChild(this.fastTable, 0, 0);
		this.service.getDmManager().addDmManagerListener(this);
		this.setRowSize(1, 40);

	}
	private Object[] newEmptyRow() {
		return new Object[basic.getColumnsCount()];
	}
	private void populateRow(Object[] r, AmiWebDm dm) {
		if (dm != null) {
			AmiWebLayoutFile lf = service.getLayoutFilesManager().getLayoutByFullAlias(dm.getAmiLayoutFullAlias());
			if (lf.getFullAlias().equals(AmiWebLayoutHelper.DEFAULT_ROOT_ALIAS))
				putNoFire(r, COLUMN_OWNING_LAYOUT, "<root>");
			else
				putNoFire(r, COLUMN_OWNING_LAYOUT, dm.getAmiLayoutFullAlias());

			if (dm.isTransient())
				putNoFire(r, COLUMN_PERMISSION, STATUS_LAYOUT_TRANSIENT);
			else if (dm.isReadonlyLayout())
				putNoFire(r, COLUMN_PERMISSION, STATUS_LAYOUT_READONLY);
			else
				putNoFire(r, COLUMN_PERMISSION, STATUS_LAYOUT_WRITABLE);

			putNoFire(r, COLUMN_DM_ADN, dm.getAmiLayoutFullAliasDotId());
			putNoFire(r, COLUMN_DM_TYPE, "Datamodel");
			putNoFire(r, COLUMN_AUTO_RUN, AmiWebDmsImpl.toQueryModeString(dm.getQueryOnMode()));
			putNoFire(r, COLUMN_AUTO_REQUERY, dm.getMaxRequeryMs() / 1000d);
			putNoFire(r, COLUMN_CONFLATE_QUERY, dm.getMinRequeryMs() / 1000d);
			putNoFire(r, COLUMN_LIMIT, dm.getCallback_OnProcess().getLimit() == AmiConsts.DEFAULT ? "default" : dm.getCallback_OnProcess().getLimit());
			putNoFire(r, COLUMN_TIMEOUT, dm.getCallback_OnProcess().getTimeoutMs() == AmiConsts.DEFAULT ? "default" : dm.getCallback_OnProcess().getTimeoutMs() / 1000d);
			putNoFire(r, COLUMN_DATASOURCE, dm.getDefaultDatasource());
			putNoFire(r, COLUMN_DATAMODELS, dm.getLowerDmAliasDotNames().size() == 0 ? "none" : dm.getLowerDmAliasDotNames());
			putNoFire(r, COLUMN_KEEP_OUTPUT, dm.getCallbacks().getCallback("onProcess").getKeepTablesetOnRerun());
			putNoFire(r, COLUMN_DYNAMIC_DM, dm.getCallbacks().getCallback("onProcess").getIsDynamicDatamodel());
			putNoFire(r, COLUMN_SUBSCRIPTIONS, dm.getConfiguration().get("subscribe"));

			Row row = fastTable.addRow(r);
			dmAdnToRow.put(dm.getAmiLayoutFullAliasDotId(), row);
			this.dmCount++;
		}
	}
	private void putNoFire(Object r[], String id, Object value) {
		r[this.basic.getColumn(id).getLocation()] = value;
	}

	public FastTablePortlet getTablePortlet() {
		return this.fastTable;
	}
	public Row getRowByDmAdn(String adn) {
		return this.dmAdnToRow.get(adn);
	}
	public int getDmCount() {
		return this.dmCount;
	}
	public void setDmCount(int newCount) {
		this.dmCount = newCount;
	}
	public void resetDmCount() {
		setDmCount(0);
	}
	@Override
	public WebMenu createMenu(WebTable table) {
		BasicWebMenu menu = new BasicWebMenu();
		if (this.fastTable.getTable().getSelectedRows().size() == 1) {
			Row r = getSelectedRow(this.fastTable.getTable());
			if (r != null) {
				AmiWebDm dm = this.service.getDmManager().getDmByAliasDotName((String) r.get(COLUMN_DM_ADN));
				if (dm.isReadonlyLayout())
					menu.add(new BasicWebMenuLink("Remove Datamodel", false, "remove_dm"));
				else
					menu.add(new BasicWebMenuLink("Remove Datamodel", true, "remove_dm"));
				menu.add(new BasicWebMenuLink("Edit Datamodel", true, "show_editor"));
				menu.add(new BasicWebMenuLink("Show Configuration", true, AmiWebViewObjectsPortlet.ACTION_SHOW_COFIGURATION));
				if (objectsPortlet.getScmAdapter() != null)
					menu.add(new BasicWebMenuLink("Show History", true, AmiWebViewObjectsPortlet.ACTION_SHOW_HISTORY));
			}
		} else if (this.fastTable.getTable().getSelectedRows().size() == 2)
			menu.add(new BasicWebMenuLink("Diff Configurations", true, AmiWebViewObjectsPortlet.ACTION_DIFF_CONFIGURATIONS));
		return menu;
	}
	public Row getSelectedRow(WebTable table) {
		if (table.getSelectedRows().size() > 0)
			return table.getSelectedRows().get(0);
		else
			return null;
	}
	@Override
	public void onDmAdded(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm dm) {
		Object[] row = newEmptyRow();
		populateRow(row, dm);
		objectsPortlet.updateTabsTitle();
	}
	@Override
	public void onDmRemoved(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm dm) {
		Row r = this.dmAdnToRow.get(dm.getAmiLayoutFullAliasDotId());
		if (r != null) {
			this.fastTable.removeRow(r);
			this.dmAdnToRow.remove(r.get(COLUMN_DM_ADN));
			this.dmCount--;
			objectsPortlet.updateTabsTitle();
		}
	}
	@Override
	public void onContextMenu(WebTable table, String action) {
		Row r = getSelectedRow(table);
		if (r != null) {
			if ("remove_dm".equals(action)) {
				ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(generateConfig(), "Are you sure you want to remove the datamodel?", ConfirmDialogPortlet.TYPE_YES_NO);
				cdp.setCallback("remove_dm2");
				cdp.addDialogListener(this);
				getManager().showDialog("Confirm Delete", cdp);
			} else if ("show_editor".equals(action))
				AmiWebUtils.showEditDmPortlet(service, this.service.getDmManager().getDmByAliasDotName((String) r.get(COLUMN_DM_ADN)), "Edit Datamodel");
			else if (action.equals(AmiWebViewObjectsPortlet.ACTION_SHOW_COFIGURATION)) {
				AmiWebDm dm = this.service.getDmManager().getDmByAliasDotName((String) (r.get(COLUMN_DM_ADN)));
				AmiWebUtils.showConfiguration(service, service.getLayoutFilesManager().toJson(dm.getConfiguration()), dm.getAmiLayoutFullAliasDotId(),
						objectsPortlet.getEscapedSearchText());
			} else if (action.equals(AmiWebViewObjectsPortlet.ACTION_DIFF_CONFIGURATIONS)) {
				Row r1 = table.getSelectedRows().get(0);
				Row r2 = table.getSelectedRows().get(1);
				AmiWebDm leftDm = this.service.getDmManager().getDmByAliasDotName((String) (r1.get(COLUMN_DM_ADN)));
				AmiWebDm rightDm = this.service.getDmManager().getDmByAliasDotName((String) (r2.get(COLUMN_DM_ADN)));
				AmiWebUtils.diffConfigurations(service, service.getLayoutFilesManager().toJson(leftDm.getConfiguration()),
						service.getLayoutFilesManager().toJson(rightDm.getConfiguration()), leftDm.getAmiLayoutFullAliasDotId(), rightDm.getAmiLayoutFullAliasDotId(),
						objectsPortlet.getEscapedSearchText());
			} else if (action.equals(AmiWebViewObjectsPortlet.ACTION_SHOW_HISTORY)) {
				AmiWebDm dm = this.service.getDmManager().getDmByAliasDotName((String) (r.get(COLUMN_DM_ADN)));
				String fileName = service.getLayoutFilesManager().getLayoutByFullAlias(dm.getAmiLayoutFullAlias()).getAbsoluteLocation();
				AmiWebDmDefParser dmParser = new AmiWebDmDefParser(service, dm);
				objectsPortlet.getHistoryPortlet().setObjectDefParser(dmParser);
				this.objectsPortlet.showObjectHistory(fileName);
			}
		}
	}
	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		Row r = getSelectedRow(this.fastTable.getTable());
		if (source.getCallback().equals("remove_dm2") && id.equals(ConfirmDialogPortlet.ID_YES)) {
			if (r != null) {
				List<AmiWebDmPortlet> panelsForDmAliasDotName = this.service.getDmManager().getPanelsForDmAliasDotName((String) r.get(COLUMN_DM_ADN));
				if (panelsForDmAliasDotName.size() != 0) {
					getManager().showAlert("Cannot delete datamodel <B>" + this.service.getDmManager().getDmByAliasDotName((String) r.get(COLUMN_DM_ADN)).getDmName()
							+ "</B> is referenced by panel(s): <B>" + getReferencedPanelNames(panelsForDmAliasDotName) + "</B>");
					source.closeDialog();
					return false;
				}
				this.service.getDmManager().removeDm((String) r.get(COLUMN_DM_ADN));
				source.closeDialog();
				return true;
			}
		}
		source.closeDialog();
		return false;
	}
	private int getReferencedPanelCount(String dmAdn) {
		return this.service.getDmManager().getPanelsForDmAliasDotName(dmAdn).size();
	}
	private String getReferencedPanelNames(List<AmiWebDmPortlet> referencedPanels) {
		StringBuilder sink = new StringBuilder();
		sink.append("[");
		for (int i = 0; i < referencedPanels.size(); i++)
			sink = (i == referencedPanels.size() - 1) ? sink.append(referencedPanels.get(i).getAmiLayoutFullAliasDotId() + "]")
					: sink.append(referencedPanels.get(i).getAmiLayoutFullAliasDotId() + ", ");
		return sink.toString();
	}
	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
		Row r = getSelectedRow(this.fastTable.getTable());
		if (r != null)
			AmiWebUtils.showEditDmPortlet(this.service, this.service.getDmManager().getDmByAliasDotName((String) r.get(COLUMN_DM_ADN)), "Edit Datamodel");
	}
	@Override
	public void onClosed() {
		this.fastTable.getTable().setMenuFactory(null);
		this.fastTable.getTable().removeMenuListener(this);
		this.service.getDmManager().removeDmManagerListener(this);
		super.onClosed();
	}
	@Override
	public void onDmUpdated(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm dm) {
		Row r = this.dmAdnToRow.remove(dm.getAmiLayoutFullAliasDotId());
		if (r != null) {
			this.fastTable.removeRow(r);
			this.dmCount--;
			populateRow(newEmptyRow(), dm);
		}
	}

	@Override
	public void onDmNameChanged(AmiWebDmManager amiWebDmManagerImpl, String oldAliasDotName, AmiWebDm dm) {
		Row r = this.dmAdnToRow.remove(oldAliasDotName);
		r.put(COLUMN_DM_ADN, dm.getAmiLayoutFullAliasDotId());
		r.put(COLUMN_OWNING_LAYOUT, SH.isEmpty(dm.getAmiLayoutFullAlias()) ? "<root>" : dm.getAmiLayoutFullAlias());
		this.dmAdnToRow.put(dm.getAmiLayoutFullAliasDotId(), r);
	}
	@Override
	public void onDmDependencyAdded(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm upper, AmiWebDm lower) {
	}
	@Override
	public void onDmDependencyRemoved(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm upper, AmiWebDm lower) {
	}
	@Override
	public void onDmLinkAdded(AmiWebDmManager amiWebDmManagerImpl, AmiWebDmLink link) {
	}
	@Override
	public void onDmLinkRemoved(AmiWebDmManager amiWebDmManagerImpl, AmiWebDmLink link) {
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
	public void onScroll(int viewTop, int viewPortHeight, long contentWidth, long contentHeight) {
	}
}
