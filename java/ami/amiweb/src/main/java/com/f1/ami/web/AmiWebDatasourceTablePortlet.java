package com.f1.ami.web;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmError;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmListener;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.ami.web.dm.portlets.AmiWebDmUtils;
import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.table.WebCellFormatter;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.suite.web.table.impl.BasicWebColumn;
import com.f1.suite.web.table.impl.NumberWebCellFormatter;
import com.f1.suite.web.table.impl.WebCellStyleWrapperFormatter;
import com.f1.utils.CH;
import com.f1.utils.IntArrayList;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.sql.Tableset;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedColumn;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.DerivedRow;
import com.f1.utils.structs.table.derived.DerivedTable;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class AmiWebDatasourceTablePortlet extends AmiWebAbstractTablePortlet implements AmiWebDmListener, AmiWebDmPortlet {

	private static final Logger log = LH.get();

	public static final String DYNAMIC_ENABLED_REARRANGE = "rearrange";
	public static final String DYNAMIC_ENABLED = "true";
	public static final String DYNAMIC_OFF = "false";

	private static final String FORMULA_CORRELATION_ID = "correlation_id";

	private AmiWebFormula correlationId;

	final private AmiWebUsedDmSingleton dmSingleton;

	private boolean editRerunDatamodel = true;

	private boolean editUpdatesInPlace = false;
	private boolean clearOnDataStale = true;

	public AmiWebDatasourceTablePortlet(PortletConfig config) {
		super(config);
		this.correlationId = formulas.addFormula("correlationId", Object.class);
		this.dmSingleton = new AmiWebUsedDmSingleton(getService().getDmManager(), this);
	}

	public static class Builder extends AmiWebAbstractPortletBuilder<AmiWebDatasourceTablePortlet> implements AmiWebDmPortletBuilder<AmiWebDatasourceTablePortlet> {

		public static final String OLD_ID2 = "VortexWebAmiStaticObjectTablePortlet";
		public static final String OLD_ID = "VortexWebDatasourceTablePortlet";
		public static final String ID = "amistatictable";

		public Builder() {
			super(AmiWebDatasourceTablePortlet.class);
		}

		@Override
		public AmiWebDatasourceTablePortlet buildPortlet(PortletConfig portletConfig) {
			AmiWebDatasourceTablePortlet r = new AmiWebDatasourceTablePortlet(portletConfig);
			return r;
		}

		@Override
		public String getPortletBuilderName() {
			return "Datasource Table";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}
		@Override
		public List<String> extractUsedDmAndTables(Map<String, Object> portletConfig) {
			return AmiWebUsedDmSingleton.extractUsedDmAndTables(portletConfig);
		}

		@Override
		public void replaceUsedDmAndTable(Map<String, Object> portletConfig, int position, String name) {
			AmiWebUsedDmSingleton.replaceUsedDmAndTable(portletConfig, position, name);
		}

	}

	private boolean displayLastRuntime = true;

	private String isDynamicColumns = DYNAMIC_OFF;

	public boolean getDisplayLastRuntime() {
		return this.displayLastRuntime;
	}
	public void setDisplayLastRuntime(boolean displayLastRuntime) {
		this.displayLastRuntime = displayLastRuntime;
	}

	@Override
	protected DerivedTable initTable(DerivedTable derivedTable) {
		derivedTable.setTitle("Objects");
		SmartTable st = new BasicSmartTable(derivedTable);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());
		table.addColumn(false, "AMI-ID", AmiConsts.TABLE_PARAM_ID, getService().getFormatterManager().getBasicFormatter());
		this.tablePortlet.setTable(table);
		return derivedTable;
	}
	protected String[] getTableIds() {
		return new String[] { AmiConsts.TABLE_PARAM_ID, AmiConsts.TABLE_PARAM_DATA };
	}
	protected Class[] getTableTypes() {
		return new Class[] { Long.class, Object.class };
	}

	private void addRowsFromTable(Table tbl) {
		addRowsFromTable(tbl, tbl.getRows(), null);
	}

	private Set<String> dynamicColumns = new HashSet<String>();

	private void addRowsFromTable(Table tbl, List<Row> rows, List<Row> existing) {
		FastWebTable webTable = getTable();
		boolean ks = webTable.isKeepSorting();
		if (ks)
			webTable.pauseSort(true);
		int colCount = tbl.getColumnsCount();
		int[] mapping = new int[colCount];
		Caster<?>[] casters = new Caster<?>[colCount];
		if (!DYNAMIC_OFF.equals(isDynamicColumns)) {
			Map<String, Boolean> existingSorts = new LinkedHashMap<String, Boolean>();
			String existingSearch = webTable.getSearch();
			if (existingSearch != null)
				webTable.setSearch(null);
			for (Entry<String, Boolean> t : webTable.getSortedColumns())
				existingSorts.put(t.getKey(), t.getValue());
			for (String id : CH.l(dynamicColumns)) {
				if (!tbl.getColumnIds().contains(id)) {
					webTable.removeColumn(id);
					this.dynamicColumns.remove(id);
					this.getCustomDisplayColumnIds().remove(id);
				}
			}

			if (DYNAMIC_ENABLED_REARRANGE.equals(isDynamicColumns)) {
				while (webTable.getVisibleColumnsCount() > 0)
					webTable.hideColumn(webTable.getVisibleColumn(webTable.getVisibleColumnsCount() - 1).getColumnId());
				for (int i = 0; i < colCount; i++) {
					Column srcCol = tbl.getColumnAt(i);
					String srcColId = srcCol.getId();
					if (this.getCustomDisplayColumnIds().contains(srcCol.getId()) == false) {
						Column col2 = webTable.getTable().addColumn(srcCol.getType(), srcCol.getId());
						mapping[i] = col2.getLocation();
						casters[i] = col2.getTypeCaster();
						BasicWebColumn col = webTable.addColumn(true, (String) col2.getId(), col2.getId(), createDynamicColumn(col2.getType()));
						webTable.showColumn((String) col.getColumnId());
						dynamicColumns.add((String) srcCol.getId());
						this.createCustomCol((String) srcCol.getId());
					} else {
						if (webTable.getColumnIds().contains(srcColId)) {//webTable.getColumnIds() contains all the columns: visible cols + hidden cols
							WebColumn col = webTable.getColumn(srcColId);
							boolean found = false;
							for (String ucol : col.getTableColumns()) {
								if (tbl.getColumnIds().contains(ucol) || ucol.toString().startsWith("!")) {
									found = true;
									break;
								}
							}
							if (!found)
								webTable.hideColumn(srcColId);
							else
								webTable.showColumn(srcColId);
						}
					}
				}
			} else {
				for (int i = 0; i < colCount; i++) {
					Column srcCol = tbl.getColumnAt(i);
					if (this.getCustomDisplayColumnIds().contains(srcCol.getId()) == false) {
						Column col2 = webTable.getTable().addColumn(srcCol.getType(), srcCol.getId());
						mapping[i] = col2.getLocation();
						casters[i] = col2.getTypeCaster();
						BasicWebColumn col = webTable.addColumn(true, (String) col2.getId(), col2.getId(), createDynamicColumn(col2.getType()));
						dynamicColumns.add((String) srcCol.getId());
						this.createCustomCol((String) srcCol.getId());
					}
				}
				for (String columnId : webTable.getColumnIds()) {
					WebColumn col = webTable.getColumn(columnId);
					boolean found = false;
					for (Object ucol : col.getTableColumns()) {
						if (tbl.getColumnIds().contains(ucol) || ucol.toString().startsWith("!")) {
							found = true;
							break;
						}
					}
					if (!found)
						webTable.hideColumn(columnId);
					else
						webTable.showColumn(columnId);
				}
			}

			for (String s : CH.l(webTable.getFilteredInColumns())) {
				WebColumn col = webTable.getColumnNoThrow(s);
				if (col == null || webTable.getColumnPosition(s) < 0)
					webTable.setFilteredIn(s, (Set) null);
			}
			for (Entry<String, Boolean> e : existingSorts.entrySet()) {
				WebColumn col = webTable.getColumnNoThrow(e.getKey());
				if (col == null || webTable.getColumnPosition(e.getKey()) < 0) {
					webTable.clearSort();
					break;
				}
			}
			if (existingSearch != null)
				webTable.setSearch(existingSearch);
			removeUnusedVariableColumns();
		}

		Map<String, Column> tableCols = webTable.getTable().getColumnsMap();

		for (int i = 0; i < colCount; i++) {
			Column srcCol = tbl.getColumnAt(i);
			Column tgtCol = tableCols.get(srcCol.getId());
			if (tgtCol == null) {
				mapping[i] = -1;
			} else {
				mapping[i] = tgtCol.getLocation();
				casters[i] = tgtCol.getTypeCaster();
			}
		}
		long start = System.currentTimeMillis();
		final IntArrayList srcWithCast = new IntArrayList();
		final IntArrayList tgtWithCast = new IntArrayList();
		final ArrayList<Caster> cast = new ArrayList<Caster>();
		final IntArrayList srcWithoutCast = new IntArrayList();
		final IntArrayList tgtWithoutCast = new IntArrayList();

		for (int i = 0; i < colCount; i++) {
			int pos = mapping[i];
			if (pos != -1) {
				if (tbl.getColumnAt(i).getType() == casters[i].getCastToClass()) {
					srcWithoutCast.add(i);
					tgtWithoutCast.add(pos);
				} else {
					srcWithCast.add(i);
					tgtWithCast.add(pos);
					cast.add(casters[i]);
				}
			}
		}

		final int[] swc = srcWithCast.toIntArray();
		final int[] twc = tgtWithCast.toIntArray();
		final Caster[] c = cast.toArray(new Caster[cast.size()]);
		final int[] swoc = srcWithoutCast.toIntArray();
		final int[] twoc = tgtWithoutCast.toIntArray();
		if (existing == null) {
			for (int j = 0; j < rows.size(); j++) {
				Row row = rows.get(j);
				if (!meetsWhereFilter(row))
					continue;
				Object[] vals = new Object[tableCols.size()];
				for (int i = 0; i < swoc.length; i++)
					vals[twoc[i]] = row.getAt(swoc[i]);
				for (int i = 0; i < swc.length; i++)
					vals[twc[i]] = c[i].cast(row.getAt(swc[i]), false, false);
				this.tablePortlet.addRow(vals);
			}
		} else {
			for (int j = 0; j < rows.size(); j++) {
				Row row = rows.get(j);
				if (!meetsWhereFilter(row))
					continue;
				Row row2 = existing.get(j);
				if (row2 == null) {
					Object[] vals = new Object[tableCols.size()];
					for (int i = 0; i < swoc.length; i++)
						vals[twoc[i]] = row.getAt(swoc[i]);
					for (int i = 0; i < swc.length; i++)
						vals[twc[i]] = c[i].cast(row.getAt(swc[i]), false, false);
					this.tablePortlet.addRow(vals);
				} else {
					if (editUpdatesInPlace) {
						for (int i = 0; i < swoc.length; i++)
							((DerivedRow) row2).removeCache(twoc[i]);
						for (int i = 0; i < swc.length; i++)
							((DerivedRow) row2).removeCache(swc[i]);
					}
					for (int i = 0; i < swoc.length; i++) {
						row2.putAt(twoc[i], row.getAt(swoc[i]));
					}
					for (int i = 0; i < swc.length; i++) {
						row2.putAt(twc[i], c[i].cast(row.getAt(swc[i]), false, false));
					}
				}
			}
		}

		if (log.isLoggable(Level.FINE))
			LH.fine(log, toDerivedString(), " Processed ", rows.size(), " x ", tbl.getColumns().size(), " of data in ", (System.currentTimeMillis() - start), " millis. ",
					swc.length, " columns need cast and ", swoc.length, " columns are direct.");
		if (ks)
			webTable.pauseSort(false);
	}

	private BasicWebCellFormatter createDynamicColumn(Class<?> clazz) {
		AmiWebFormatterManager fm = getService().getFormatterManager();
		if (OH.isFloat(clazz) || clazz == BigDecimal.class) {
			return fm.getDecimalWebCellFormatter();
		} else if (Date.class.isAssignableFrom(clazz) || DateMillis.class.isAssignableFrom(clazz)) {
			return fm.getDateTimeMillisWebCellFormatter();
		} else if (DateNanos.class.isAssignableFrom(clazz)) {
			return fm.getDateTimeNanosWebCellFormatter();
		} else if (Number.class.isAssignableFrom(clazz)) {
			return fm.getIntegerWebCellFormatter();
		} else {
			return fm.getBasicFormatter();
		}

	}

	private List<String> getSchemaWarnings() {

		final FastWebTable t = getTable();
		final SmartTable t2 = getTable().getTable();
		if (getDm() == null)
			return CH.l("Datamodel/Table not found:" + this.dmSingleton.getDmAliasDotName());
		final com.f1.base.CalcTypes dmTypes = getDm().getClassTypes();
		final List<String> r = new ArrayList<String>();
		AmiWebScriptManagerForLayout sm = getScriptManager();
		for (String columnId : t.getColumnIds()) {
			final Set<String> vars = getDependentVars(columnId);
			for (String name : vars) {
				if (OH.eq("D", name))
					continue;
				final Class<?> vrType = t2.getColumn(name).getType();
				final Class<?> dmType = dmTypes.getType(name);
				String columnName = t.getColumn(columnId).getColumnName();
				if (dmType == null) {
					r.add("Column '<B>" + columnName + "</B>': uses '" + name + "' which is not defined in underlying datamodel");
				} else if (vrType != dmType)
					r.add("Column '<B>" + columnName + "</B>': uses '" + name + "' as a " + sm.forType(vrType) + " but in the datamodel it is a " + sm.forType(dmType));
			}
		}
		return r;
	}
	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {

		dmSingleton.init(getAmiLayoutFullAlias(), configuration);
		super.init(configuration, origToNewIdMapping, sb);
		this.clearOnDataStale = CH.getOrNoThrow(Caster_Boolean.INSTANCE, configuration, "cods", true); // If null or not in configuration true
		this.correlationId.initFormula(CH.getOrNoThrow(Caster_String.INSTANCE, configuration, "corId", null));
		this.editRerunDatamodel = CH.getOr(Caster_Boolean.INSTANCE, configuration, "editRerunDM", this.editRerunDatamodel);
		this.editUpdatesInPlace = CH.getOr(Caster_Boolean.INSTANCE, configuration, "editInplace", this.editUpdatesInPlace);
		this.displayLastRuntime = CH.getOr(Caster_Boolean.INSTANCE, configuration, "showLastRuntime", true);
		this.isDynamicColumns = CH.getOr(Caster_String.INSTANCE, configuration, "dynamicColumns", DYNAMIC_OFF);

	}
	@Override
	public Map<String, Object> getConfiguration() {
		final Map<String, Object> r = super.getConfiguration();
		if (this.clearOnDataStale == false) //Only put in configuration if false
			r.put("cods", this.clearOnDataStale);
		this.dmSingleton.getConfiguration(getAmiLayoutFullAlias(), r);
		AmiWebUtils.putSkipEmpty(r, "corId", correlationId.getFormulaConfig());
		r.put("editRerunDM", this.editRerunDatamodel);
		r.put("editInplace", this.editUpdatesInPlace);
		r.put("showLastRuntime", this.displayLastRuntime);
		r.put("dynamicColumns", this.isDynamicColumns);
		return r;
	}

	@Override
	public String getConfigMenuTitle() {
		return "Table";
	}

	@Override
	public void populateConfigMenu(WebMenu headMenu) {
		List<String> warnings = getSchemaWarnings();
		if (warnings.size() > 0)
			headMenu.add(new BasicWebMenuLink("View " + warnings.size() + " Schema Warning(s)", true, "warnings").setCssStyle("className=ami_warning_menu"));
		super.populateConfigMenu(headMenu);
	}
	@Override
	public boolean onAmiContextMenu(String action) {
		if ("warnings".equals(action)) {
			getManager().showDialog("Warnings", new HtmlPortlet(generateConfig(), SH.join("<P>", getSchemaWarnings())), 600, 400);
			return true;
		} else
			return super.onAmiContextMenu(action);
	}

	@Override
	public boolean addCustomColumn(AmiWebCustomColumn col, StringBuilder errorSink, int columnLocation, AmiWebCustomColumn replacing, com.f1.base.CalcTypes varTypes,
			boolean populateValues) {
		boolean r = super.addCustomColumn(col, errorSink, columnLocation, replacing, varTypes, populateValues);
		clearAmiData();
		if (isInitDone()) {
			AmiWebDm dm = this.dmSingleton.getDm();
			if (dm != null)
				onDmDataChanged(dm);
		}
		return r;
	}

	private void onDmData(Table table) {
		addRowsFromTable(table);
	}

	@Override
	protected void onEditFinished() {
		AmiWebDm dm = this.dmSingleton.getDm();
		if (dm != null) {
			if (this.editRerunDatamodel)
				dm.processRequest(dm.getRequestTableset(), dm.getDmManager().getService().getDebugManager());
		}
	}
	@Override
	public void onDmDataChanged(AmiWebDm datamodel) {
		if (this.tablePortlet.isEditing())
			return;
		showWaitingSplash(false);
		Tableset tableSet = datamodel.getResponseTableset();
		Table table = tableSet.getTableNoThrow(this.dmSingleton.getDmTableName());
		if (this.correlationId.getFormulaCalc() == null) {
			clearAmiData();
			if (table != null)
				onDmData(table);
		} else if (table != null) {
			//			DerivedCellCalculator expression = getScriptManager().parseAmiScript(correlationId, getTable().getTable().getColumnTypesMapping(), null,
			//					this.getService().getDebugManager(), AmiDebugMessage.TYPE_FORMULA, this, FORMULA_CORRELATION_ID, false);
			DerivedCellCalculator expression = correlationId.getFormulaCalc();
			Map<Object, Row> cur = new HashMap<Object, Row>();
			List<Row> rows2 = getTable().getTable().getRows();
			Iterable<Row> filtered = getTable().getTable().getFiltered();
			if (filtered.iterator().hasNext())
				rows2 = CH.l(new ArrayList<Row>(rows2), filtered);
			ReusableCalcFrameStack sf = getStackFrame();
			for (Row row : rows2) {
				Object value = expression.get(sf.reset(row));
				Row dup = cur.put(value, row);
				if (dup != null) {
					if (getService().getDebugManager().shouldDebug(AmiDebugMessage.SEVERITY_WARNING)) {
						try {
							Table t = new BasicTable(getTable().getTable().getColumns());
							t.getRows().addRow(row.getValues().clone());
							t.getRows().addRow(dup.getValues().clone());
							for (int i = 0; i < t.getColumnsCount(); i++)
								if (t.getColumnAt(i).getId().toString().startsWith("!"))
									t.removeColumn(i--);
							getService().getDebugManager()
									.addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_WARNING, AmiDebugMessage.TYPE_FORMULA, this.getAri(), FORMULA_CORRELATION_ID,
											"Correlation expression is not unique",
											CH.m("Example Duplicate value", "Expression: " + correlationId + "\nvalue: " + value + " \nrow1 and row2:\n" + t,
													"correlation Id Expression", correlationId.getFormula(true)),
											null));
						} catch (Exception e) {
							LH.warning(log, logMe(), " Error building correlationId error message", e);
						}
					}
					clearAmiData();
					onDmData(table);
					return;
				}
			}
			List<Row> updateFrom = new ArrayList<Row>();
			List<Row> updateTo = new ArrayList<Row>();
			for (Row row : table.getRows()) {
				Object value = expression.get(sf.reset(row));
				if (!meetsWhereFilter(row))
					continue;
				Row existing = cur.remove(value);
				updateFrom.add(row);
				updateTo.add(existing);
			}
			for (Row t : cur.values())
				getTable().getTable().removeRow(t);
			addRowsFromTable(table, updateFrom, updateTo);
		}
		if (datamodel instanceof AmiWebDmsImpl) {
			if (this.displayLastRuntime) {
				this.tablePortlet.setDisplayTimeFormatted(
						AmiWebUtils.getService(getManager()).getFormatterManager().getDatetimeSecsFormatter().format(((AmiWebDmsImpl) datamodel).getLastQueryEndTimeMillis()));
			} else {
				this.tablePortlet.setDisplayTimeFormatted(null);
			}
		}

		onAmiRowsChanged();
	}
	final public void setUsedDatamodel(String dmName, String tableName) {
		this.dmSingleton.setUsedDm(dmName, tableName);
		for (AmiWebDmLink i : getDmLinksFromThisPortlet())
			i.setSourceDm(dmName, tableName);
		for (AmiWebDmLink i : getDmLinksToThisPortlet())
			i.setTargetDm(dmName);
	}
	@Override
	public boolean isRealtime() {
		return false;
	}

	@Override
	protected void handleOnSelectedChanged() {
		buildingSnapshot = false;
		super.handleOnSelectedChanged();
	}
	@Override
	public void drainJavascript() {
		super.drainJavascript();
		updateChildTables(false);
	}
	@Override
	public void onContextMenu(WebTable table, String action) {
		if (table.getSelectedRows().size() > 0 && action.startsWith("query_")) {
			String remotePortletIds = SH.stripPrefix(action, "query_", true);
			for (String t : SH.split('_', remotePortletIds)) {
				AmiWebDmLink link = getService().getDmManager().getDmLink(t);
				AmiWebDmUtils.sendRequest(getService(), link);
			}
		} else
			super.onContextMenu(table, action);
	}

	public String getCorrelationId(boolean override) {
		return correlationId.getFormula(override);
	}
	public void setCorrelationId(String correlationId, boolean override) {
		this.correlationId.setFormula(correlationId, override);
	}

	@Override
	public boolean hasVisiblePortletForDm(AmiWebDm datamodel) {
		return this.dmSingleton.matches(datamodel) && getVisible();
	}
	@Override
	public void onDmRunningQuery(AmiWebDm datamodel, boolean isRequery) {
		if (SH.isnt(this.getCorrelationId(true)) && !isRequery) {
			if (this.tablePortlet.isEditing())
				return;
			if (this.clearOnDataStale)
				clearRows();
			showWaitingSplash(true);
		}
	}
	public AmiWebDmTableSchema getDm() {
		return this.dmSingleton.getDmTableSchema();
	}
	@Override
	public void onDmError(AmiWebDm datamodel, AmiWebDmError error) {
		clearRows();
		showWaitingSplash(false);
	}
	@Override
	public void onDmDataBeforeFilterChanged(AmiWebDm datamodel) {
	}
	@Override
	public Set<String> getUsedDmVariables(String dmAliasDotName, String dmTable, Set<String> r) {
		if (this.dmSingleton.matches(dmAliasDotName, dmTable)) {
			FastWebTable table = this.getTable();
			SmartTable st = table.getTable();
			for (String i : table.getColumnIds())
				for (String id : table.getColumn(i).getTableColumns())
					addDependencies(st.getColumn(id), r);
			addDependencies(table.getRowBackgroundColorColumn(), r);
			addDependencies(table.getRowTxColorColumn(), r);
		}
		return r;
	}
	private void addDependencies(Column column, Set<String> r) {
		if (column instanceof DerivedColumn)
			DerivedHelper.getDependencyIds(((DerivedColumn) column).getCalculator(), (Set) r);
		else if (column != null)
			r.add((String) column.getId());
	}
	public boolean getEditRerunDatamodel() {
		return this.editRerunDatamodel;
	}
	public void setEditRerunDatamodel(boolean b) {
		this.editRerunDatamodel = b;
	}
	public AmiWebDm getDatamodel() {
		return this.dmSingleton.getDm();
	}
	@Override
	public void onEditCell(int x, int y, String v) {
		if (editUpdatesInPlace) {
			final WebColumn pos = this.getTable().getVisibleColumn(x);
			final String[] cols = pos.getTableColumns();
			final Column col = this.getTable().getTable().getColumn(cols[0]);
			WebCellFormatter f = pos.getCellFormatter();
			if (f instanceof WebCellStyleWrapperFormatter)
				f = ((WebCellStyleWrapperFormatter) f).getInner();
			final Object v2;
			if (f instanceof NumberWebCellFormatter) {
				NumberWebCellFormatter f2 = (NumberWebCellFormatter) f;
				try {
					v2 = SH.isEmpty(v) ? null : f2.getFormatter().parse(v);
				} catch (Exception e) {
					return;
				}
			} else
				v2 = v;
			final Object cast = col.getTypeCaster().cast(v2, false, false);
			if (y < this.getTable().getRowsCount())
				this.getTable().getRow(y).putAt(col.getLocation(), cast);
		}

	}
	public boolean getEditUpdatesInPlace() {
		return editUpdatesInPlace;
	}
	public void setEditUpdatesInPlace(boolean editUpdatesInPlace) {
		this.editUpdatesInPlace = editUpdatesInPlace;
	}
	@Override
	protected void onWhereFormulaChanged() {
		AmiWebDm dm = getService().getDmManager().getDmByAliasDotName(this.dmSingleton.getDmAliasDotName());
		if (dm != null)
			onDmDataChanged(dm);
	}
	@Override
	public com.f1.base.CalcTypes getUnderlyingVarTypes() {
		return this.dmSingleton.getTableSchema();
	}
	@Override
	public void onAmiInitDone() {
		super.onAmiInitDone();
		resetWhere();
		AmiWebDm dm = getService().getDmManager().getDmByAliasDotName(this.dmSingleton.getDmAliasDotName());
		if (dm != null)
			onDmDataChanged(dm);
	}
	public String getIsDynamicColumns() {
		return this.isDynamicColumns;
	}
	public void setIsDynamicColumns(String isDynamic) {
		if (this.isDynamicColumns == isDynamic)
			return;
		this.isDynamicColumns = isDynamic;

		if (DYNAMIC_OFF.equals(isDynamic)) {
			FastWebTable webTable = getTable();
			for (String id : CH.l(dynamicColumns)) {
				webTable.removeColumn(id);
				this.dynamicColumns.remove(id);
				this.getCustomDisplayColumnIds().remove(id);
			}
			removeUnusedVariableColumns();
		}

	}
	@Override
	public Set<String> getUsedDmAliasDotNames() {
		return this.dmSingleton.getUsedDmAliasDotNames();
	}
	@Override
	public Set<String> getUsedDmTables(String aliasDotName) {
		return this.dmSingleton.getUsedDmTables(aliasDotName);
	}
	@Override
	public void onDmNameChanged(String oldAliasDotName, AmiWebDm dm) {
		this.dmSingleton.onDmNameChanged(oldAliasDotName, dm);
	}

	@Override
	public AmiWebAddObjectColumnFormPortlet newAddAmiObjectColumnFormPortlet(PortletConfig config, AmiWebAbstractTablePortlet portlet, int columnPosition, AmiWebCustomColumn col) {
		if (!DYNAMIC_OFF.equals(this.isDynamicColumns)) {
			getManager().showAlert("Cannot edit/create custom columns with dynamic columns enabled. <BR> (<i>Settings -> Auto-update Columns to Match Datamodel -> Disabled</i>)");
			return null;
		}
		if (col != null && col.isTransient() == true) {
			getManager().showAlert("Cannot edit transient columns through wizard");
			return null;
		}
		return super.newAddAmiObjectColumnFormPortlet(config, portlet, columnPosition, col);
	}

	public boolean isClearOnDataStale() {
		return clearOnDataStale;
	}

	public void setClearOnDataStale(boolean clearOnDataStale) {
		this.clearOnDataStale = clearOnDataStale;
	}
	@Override
	public AmiWebPanelSettingsPortlet showSettingsPortlet() {
		return new AmiWebStaticTableSettingsPortlet(generateConfig(), this);
	}

	@Override
	public void onFormulaChanged(AmiWebFormula formula, DerivedCellCalculator old, DerivedCellCalculator nuw) {
		super.onFormulaChanged(formula, old, nuw);
		if (formula == this.correlationId && isInitDone()) {
			clearAmiData();
			onDmData(this.dmSingleton.getTable());
		}
	}

}
