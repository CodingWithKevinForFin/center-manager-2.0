package com.f1.ami.web;

import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiCenterDefinition;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmError;
import com.f1.ami.web.dm.AmiWebDmFilter;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmListener;
import com.f1.ami.web.dm.AmiWebDmManager;
import com.f1.ami.web.dm.AmiWebDmManagerImpl;
import com.f1.ami.web.dm.AmiWebDmManagerListener;
import com.f1.base.Table;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeManager;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.utils.LH;
import com.f1.utils.sql.Tableset;
import com.f1.utils.structs.table.BasicTable;

public class AmiWebDataViewPortlet extends GridPortlet implements FormPortletListener, WebTreeContextMenuListener, AmiWebDmManagerListener, AmiWebDmListener,
		AmiWebLockedPermissiblePortlet, AmiWebSpecialPortlet, AmiWebPanelsListener {
	static final private Logger log = LH.get();
	private FormPortlet form;
	private AmiWebService service;
	private FormPortletButton closeButton;
	private FastTablePortlet typsPool;
	private FastTablePortlet schmPool;
	private FastTablePortlet cntsPool;
	private FastTreePortlet schemaTree;
	private FastTablePortlet dmTables;
	private FastTablePortlet dms;
	private AmiWebMemoryUserUsagePortlet usagePortlet;
	private FastTablePortlet centers;

	public AmiWebDataViewPortlet(PortletConfig config) {
		super(config);
		this.service = (AmiWebService) getManager().getService(AmiWebService.ID);
		TabPortlet tabs = addChild(new TabPortlet(generateConfig()), 0, 0);
		form = addChild(new FormPortlet(generateConfig()), 0, 1);
		this.typsPool = new FastTablePortlet(generateConfig(), new BasicTable(new String[] { "cid", "type", "count", "consumers" }), "Local Real Time Objects");
		this.centers = new FastTablePortlet(generateConfig(), new BasicTable(new String[] { "Role", "Name", "URL", "Status", "Time", "Messages", "Objects" }), "Centers");
		schemaTree = new FastTreePortlet(generateConfig());
		schemaTree.setTitle("Center Schema(s)");
		schemaTree.getTree().setRootLevelVisible(false);
		schemaTree.getTree().addMenuContextListener(this);
		tabs.addChild("Centers", centers);
		tabs.addChild(schemaTree.getTitle(), schemaTree);
		schemaTree.addOption(FastTreePortlet.OPTION_HEADER_BAR_HIDDEN, "true");
		tabs.addChild(typsPool.getTitle(), typsPool);
		tabs.setIsCustomizable(false);
		this.dmTables = new FastTablePortlet(generateConfig(), new BasicTable(new Class[] { String.class, String.class, Integer.class, Integer.class, Integer.class },
				new String[] { "Datamodel", "Table", "Rows", "Columns", "Cells" }), "Data Model Tables");
		this.dms = new FastTablePortlet(generateConfig(),
				new BasicTable(new Class[] { String.class, Long.class, Long.class, Long.class, Long.class, Long.class, Long.class, Long.class, Long.class, Long.class, Long.class },
						new String[] { "Datamodel", "Cells", "Executed", "Errors", "Dur", "AvgDur", "MinDur", "MaxDur", "LastExec", "NextExec", "Requeries" }),
				"Data Models");
		tabs.addChild(dms.getTitle(), dms);
		tabs.addChild(dmTables.getTitle(), dmTables);
		this.usagePortlet = new AmiWebMemoryUserUsagePortlet(generateConfig(), service);
		tabs.addChild("Memory/User Usage", usagePortlet);
		AmiWebFormatterManager fm = service.getFormatterManager();
		typsPool.getTable().addColumn(true, "Type", "type", fm.getBasicFormatter()).setWidth(200);
		typsPool.getTable().addColumn(true, "Center Id", "cid", fm.getBasicFormatter());
		typsPool.getTable().addColumn(true, "Count", "count", fm.getIntegerWebCellFormatter());
		typsPool.getTable().addColumn(true, "Consuming Panels Count", "consumers", fm.getIntegerWebCellFormatter()).setWidth(150);
		dmTables.getTable().addColumn(true, "Datamodel", "Datamodel", fm.getBasicFormatter()).setWidth(150);
		dmTables.getTable().addColumn(true, "Table", "Table", fm.getBasicFormatter()).setWidth(150);
		dmTables.getTable().addColumn(true, "Cells", "Cells", fm.getIntegerWebCellFormatter()).setWidth(150).setCssColumn("bold");
		dmTables.getTable().addColumn(true, "Rows", "Rows", fm.getIntegerWebCellFormatter()).setWidth(150);
		dmTables.getTable().addColumn(true, "Columns", "Columns", fm.getIntegerWebCellFormatter()).setWidth(150);
		dmTables.getTable().sortRows("Cells", false, true, false);
		centers.getTable().addColumn(true, "Role", "Role", fm.getBasicFormatter()).setWidth(150);
		centers.getTable().addColumn(true, "Name", "Name", fm.getBasicFormatter()).setWidth(150);
		centers.getTable().addColumn(true, "URL", "URL", fm.getBasicFormatter()).setWidth(150);
		centers.getTable().addColumn(true, "Status", "Status", fm.getBasicFormatter()).setWidth(150);
		centers.getTable().addColumn(true, "Status Changed", "Time", fm.getTimeSecsWebCellFormatter()).setWidth(150);
		centers.getTable().addColumn(true, "Messages", "Messages", fm.getIntegerWebCellFormatter()).setWidth(150);
		centers.getTable().addColumn(true, "Realtime Objects", "Objects", fm.getIntegerWebCellFormatter()).setWidth(150);
		dms.getTable().addColumn(true, "Datamodel", "Datamodel", fm.getBasicFormatter()).setWidth(150);
		dms.getTable().addColumn(true, "Cells (cur)", "Cells", fm.getIntegerWebCellFormatter()).setWidth(80);
		dms.getTable().addColumn(true, "Executed", "Executed", fm.getIntegerWebCellFormatter()).setWidth(80);
		dms.getTable().addColumn(true, "ConsecutiveRequeries", "Requeries", fm.getIntegerWebCellFormatter()).setWidth(150);
		dms.getTable().addColumn(true, "Errors", "Errors", fm.getIntegerWebCellFormatter()).setWidth(80);
		dms.getTable().addColumn(true, "Duration(ms)", "Dur", fm.getIntegerWebCellFormatter()).setWidth(100).setCssColumn("bold");
		dms.getTable().addColumn(true, "MinDuration(ms)", "MinDur", fm.getIntegerWebCellFormatter()).setWidth(100).setCssColumn("bold");
		dms.getTable().addColumn(true, "MaxDuration(ms)", "MaxDur", fm.getIntegerWebCellFormatter()).setWidth(100).setCssColumn("bold");
		dms.getTable().addColumn(true, "AvgDuration(ms)", "AvgDur", fm.getIntegerWebCellFormatter()).setWidth(100).setCssColumn("bold");
		dms.getTable().addColumn(true, "LastExecutionTime", "LastExec", fm.getDateTimeMillisWebCellFormatter()).setWidth(150);
		dms.getTable().addColumn(true, "NextExecutionTime", "NextExec", fm.getDateTimeMillisWebCellFormatter()).setWidth(150);
		dms.getTable().sortRows("Dur", false, true, false);
		AmiWebDmManager dmm = service.getDmManager();
		for (AmiWebDm dm : dmm.getDatamodels())
			dm.addDmListener(this);
		dmm.addDmManagerListener(this);
		this.service.addAmiWebPanelsListener(this);
		this.typsPool.getTable().sortRows("type", true, true, false);
		form.addFormPortletListener(this);
		closeButton = form.addButton(new FormPortletButton("Close"));
		RootPortlet root = (RootPortlet) this.service.getPortletManager().getRoot();
		int width = (int) (root.getWidth() * 0.8);
		int height = (int) (root.getHeight() * 0.8);
		setSuggestedSize(width, height);
		setRowSize(1, this.form.getButtonPanelHeight());
		// Apply user styles
		this.typsPool.setDialogStyle(AmiWebUtils.getService(getManager()).getUserDialogStyleManager());
		this.dmTables.setDialogStyle(AmiWebUtils.getService(getManager()).getUserDialogStyleManager());
		this.dms.setDialogStyle(AmiWebUtils.getService(getManager()).getUserDialogStyleManager());
		this.typsPool.setFormStyle(AmiWebUtils.getService(getManager()).getUserFormStyleManager());
		this.dmTables.setFormStyle(AmiWebUtils.getService(getManager()).getUserFormStyleManager());
		this.dms.setFormStyle(AmiWebUtils.getService(getManager()).getUserFormStyleManager());
		this.form.setStyle(AmiWebUtils.getService(getManager()).getUserFormStyleManager());
		this.typsPool.addOption(FastTablePortlet.OPTION_TITLE_BAR_COLOR, "#6f6f6f");
		this.typsPool.addOption(FastTablePortlet.OPTION_TITLE_DIVIDER_HIDDEN, true);
		this.dmTables.addOption(FastTablePortlet.OPTION_TITLE_BAR_COLOR, "#6f6f6f");
		this.dmTables.addOption(FastTablePortlet.OPTION_TITLE_DIVIDER_HIDDEN, true);
		this.dms.addOption(FastTablePortlet.OPTION_TITLE_BAR_COLOR, "#6f6f6f");
		this.dms.addOption(FastTablePortlet.OPTION_TITLE_DIVIDER_HIDDEN, true);
		this.schemaTree.addOption(FastTreePortlet.OPTION_SEARCH_BUTTONS_COLOR, "#007608");
		for (AmiWebSnapshotManager sm : this.service.getWebManagers().getSnapshotManagers()) {
			final AmiWebManager wm = sm.getWebManager();
			final AmiCenterDefinition cd = wm.getCenterDef();
			//create table with following columns
			final boolean isPrimary = cd.isPrimary();
			final String name = cd.getName();
			final String url = cd.getPort() == -1 ? null : cd.getHost() + ":" + cd.getPort();
			String status = AmiWebSnapshotManager.formatConnectionState(sm.getConnectionState());//convert to string
			long time = sm.getConnectionStateChangedTime();
			long messagesReceived = sm.getMessagesStatistics();
			long objectsCount = wm.getObjectsCount();
			centers.addRow(cd.getPort() == -1 ? "WebStats" : (isPrimary ? "Primary" : "Additional"), name, url, status, time, messagesReceived, objectsCount);
		}
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == closeButton)
			close();
	}
	@Override
	public void close() {
		super.close();
		AmiWebDmManager dmm = service.getDmManager();
		for (AmiWebDm dm : dmm.getDatamodels())
			dm.removeDmListener(this);
		this.service.removeAmiWebPanelsListener(this);
		this.service.getDmManager().removeDmManagerListener(this);
	}
	public void refresh() {
		typsPool.clearRows();
		rebuildDmStats();
		for (AmiWebManager am : service.getWebManagers().getManagers()) {
			for (String type : am.getAmiObjectTypesBeingViewed()) {
				AmiWebObjects byType = am.getAmiObjectsByType(type);
				AmiWebRealtimeObjectManager abc = service.getWebManagers().getAmiObjectsByTypeIfExists(AmiWebManagers.FEED + type);
				final int cnt = abc == null ? 0 : abc.getUpperRealtimeIds().size();
				typsPool.addRow(am.getCenterName(), type, byType == null ? null : byType.size(), cnt);
			}
		}
		init();
	}
	private void rebuildDmStats() {
		dmTables.clearRows();
		dms.clearRows();
		AmiWebDmManager dmm = service.getDmManager();
		for (AmiWebDm dm : dmm.getDatamodels()) {
			final Tableset tables = dm.getResponseTableset();
			final String label = dm.getAmiLayoutFullAliasDotId();
			int totCells = 0;
			if (tables != null) {
				for (String tname : tables.getTableNames()) {
					Table i = tables.getTable(tname);
					final int rows = i.getSize();
					final int cols = i.getColumnsCount();
					totCells += rows * cols;
					dmTables.addRow(label, tname, rows, cols, rows * cols);
				}
			}
			dms.addRow(label, totCells, dm.getStatisticEvals(), dm.getStatisticErrors(), dm.getStatisticEvalsTimeMillis(), dm.getStatisticEvalsAvgTimeMillis(),
					dm.getStatisticEvalsMinTimeMillis(), dm.getStatisticEvalsMaxTimeMillis(), dm.getStatisticLastEvalTimeMillis(), dm.getStatisticNextEvalTimeMillis(),
					dm.getStatisticConsecutiveRequeriesCount());
		}
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}
	public void init() {
		WebTreeManager tm = schemaTree.getTreeManager();
		tm.clear();
		WebTreeNode root = tm.getRoot();
		AmiWebScriptManagerForLayout scriptManager = this.service.getScriptManager("");
		for (AmiWebManager s : service.getWebManagers().getManagers()) {
			WebTreeNode centerNode = tm.createNode(s.getCenterName() + " (" + s.getCenterDef().getHost() + ":" + s.getCenterDef().getPort() + ")", root, false)
					.setKey(s.getCenterName()).setIconCssStyle("_bgi=url(" + AmiWebConsts.ICON_DOT + ")");
			AmiWebSystemObjectsManager som = service.getWebManagers().getSystemObjectsManager(s.getCenterId());
			for (String tableName : som.getTableNames()) {
				if (tableName.startsWith("__"))
					continue;
				WebTreeNode tableNode = tm.createNode(tableName, centerNode, false).setKey(tableName).setIconCssStyle("_bgi=url(" + AmiWebConsts.ICON_DOT + ")");
				com.f1.utils.structs.table.stack.BasicCalcTypes tableSchema = som.getTableSchema(tableName);
				if (tableSchema != null)
					for (String columnName : tableSchema.getVarKeys()) {
						String type = scriptManager.forType(tableSchema.getType(columnName));
						WebTreeNode colNode = tm.createNode(columnName + " (" + type + ")", tableNode, false).setKey(columnName)
								.setIconCssStyle("_bgi=url(" + AmiWebConsts.ICON_DOT + ")");
					}
			}
		}
	}

	public static class Builder extends AbstractPortletBuilder<AmiWebDataViewPortlet> {
		public static final String ID = "VortexWebAmiDataViewPortlet";

		public Builder() {
			super(AmiWebDataViewPortlet.class);
		}
		@Override
		public AmiWebDataViewPortlet buildPortlet(PortletConfig portletConfig) {
			return new AmiWebDataViewPortlet(portletConfig);
		}
		@Override
		public String getPortletBuilderName() {
			return "Ami Data View";
		}
		@Override
		public String getPortletBuilderId() {
			return ID;
		}
	}

	@Override
	public void onContextMenu(FastWebTree tree, String action) {
	}
	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode node) {
		for (WebTreeNode i : tree.getSelected())
			if (i != node)
				i.setSelected(false);
	}
	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {
	}
	@Override
	public void onDmDataChanged(AmiWebDm datamodel) {
		rebuildDmStats();
	}
	@Override
	public boolean hasVisiblePortletForDm(AmiWebDm datamodel) {
		return false;
	}
	@Override
	public void onDmRunningQuery(AmiWebDm datamodel, boolean isRequery) {
	}
	@Override
	public void onDmError(AmiWebDm datamodel, AmiWebDmError error) {
	}
	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		if (KeyEvent.ENTER.equals(keyEvent.getKey())) {
			this.onButtonPressed(this.form, this.closeButton);
			return true;
		}
		return super.onUserKeyEvent(keyEvent);
	}
	@Override
	public void onCellMousedown(FastWebTree tree, WebTreeNode start, FastWebTreeColumn col) {
	}
	@Override
	public void onDmDataBeforeFilterChanged(AmiWebDm datamodel) {
	}
	@Override
	public void onDmNameChanged(String oldAliasDotName, AmiWebDm dm) {
		rebuildDmStats();
	}
	public void showMemoryStats() {
		PortletHelper.ensureVisible(this.usagePortlet);
	}
	public void showCenters() {
		PortletHelper.ensureVisible(this.centers);
	}
	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
	}
	@Override
	public void onDmUpdated(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm dm) {
		rebuildDmStats();
	}
	@Override
	public void onDmAdded(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm dm) {
		dm.addDmListener(this);
		rebuildDmStats();
	}
	@Override
	public void onDmRemoved(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm dm) {
		dm.removeDmListener(this);
		rebuildDmStats();
	}
	@Override
	public void onDmNameChanged(AmiWebDmManager amiWebDmManagerImpl, String oldAliasDotName, AmiWebDm dm) {
		rebuildDmStats();
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
	public void onAmiWebPanelAdded(AmiWebPortlet portlet) {
		rebuildDmStats();
	}
	@Override
	public void onAmiWebPanelRemoved(AmiWebPortlet portlet, boolean isHide) {
		rebuildDmStats();
	}
	@Override
	public void onAmiWebPanelLocationChanged(AmiWebPortlet portlet) {
		rebuildDmStats();
	}
	@Override
	public void onAmiWebPanelIdChanged(AmiWebPortlet portlet, String oldAdn, String newAdn) {
	}
}
