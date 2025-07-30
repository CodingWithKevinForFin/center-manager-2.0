package com.f1.ami.web;

import static com.f1.ami.amicommon.AmiConsts.TABLE_PARAM_C;
import static com.f1.ami.amicommon.AmiConsts.TABLE_PARAM_CENTER;
import static com.f1.ami.amicommon.AmiConsts.TABLE_PARAM_DATA;
import static com.f1.ami.amicommon.AmiConsts.TABLE_PARAM_E;
import static com.f1.ami.amicommon.AmiConsts.TABLE_PARAM_I;
import static com.f1.ami.amicommon.AmiConsts.TABLE_PARAM_ID;
import static com.f1.ami.amicommon.AmiConsts.TABLE_PARAM_M;
import static com.f1.ami.amicommon.AmiConsts.TABLE_PARAM_P;
import static com.f1.ami.amicommon.AmiConsts.TABLE_PARAM_T;
import static com.f1.ami.amicommon.AmiConsts.TABLE_PARAM_V;
import static com.f1.ami.amicommon.AmiConsts.TABLE_PARAM_W;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterPassToRelayRequest;
import com.f1.ami.amicommon.msg.AmiCenterPassToRelayResponse;
import com.f1.ami.amicommon.msg.AmiRelayResponse;
import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandRequest;
import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandResponse;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.web.AmiWebCommandWrapper.Separator;
import com.f1.ami.web.AmiWebWhereClause.WhereClause;
import com.f1.ami.web.amiscript.AmiWebScriptRunner;
import com.f1.ami.web.amiscript.AmiWebScriptRunnerListener;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.portlets.AmiWebDmUtils;
import com.f1.ami.web.dm.portlets.AmiWebDmUtils.ParsedWhere;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Table;
import com.f1.base.Action;
import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.DateMillis;
import com.f1.base.IterableAndSize;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.container.ResultMessage;
import com.f1.http.HttpRequestResponse;
import com.f1.suite.web.HttpRequestAction;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.PortletManagerListener;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.FastTableEditListener;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.WebColumnEditConfig;
import com.f1.suite.web.portal.impl.json.JsonTreePortlet;
import com.f1.suite.web.table.WebCellFormatter;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.WebTableColumnContextMenuFactory;
import com.f1.suite.web.table.WebTableColumnContextMenuListener;
import com.f1.suite.web.table.WebTableListener;
import com.f1.suite.web.table.WebTableTooltipFactory;
import com.f1.suite.web.table.fast.BasicQuickFilterAutocompleteManager;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.fast.QuickFilterAutocompleteManager;
import com.f1.suite.web.table.impl.BasicWebColumn;
import com.f1.suite.web.table.impl.NumberWebCellFormatter;
import com.f1.suite.web.table.impl.PercentWebCellFormatter;
import com.f1.suite.web.table.impl.WebCellStyleAdvancedWrapperFormatter;
import com.f1.suite.web.table.impl.WebCellStyleWrapperFormatter;
import com.f1.suite.web.table.impl.WebTableFilteredInFilter;
import com.f1.suite.web.tree.impl.ArrangeColumnsPortlet;
import com.f1.suite.web.tree.impl.FastWebColumn;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.StringFormatException;
import com.f1.utils.TableHelper;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Byte;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.sql.aggs.AggregateFactory;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorCast;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorConst;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorRef;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorWithDependencies;
import com.f1.utils.structs.table.derived.DerivedColumn;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.DerivedTable;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.EmptyCalcFrameStack;

public abstract class AmiWebAbstractTablePortlet extends AmiWebAbstractPortlet implements WebContextMenuFactory, PortletManagerListener, WebTableColumnContextMenuFactory,
		WebTableColumnContextMenuListener, ConfirmDialogListener, WebTableListener, WebContextMenuListener, AmiWebStyledScrollbarPortlet, FastTableEditListener,
		QuickFilterAutocompleteManager, AmiWebFormulasListener, WebTableTooltipFactory, AmiWebDesktopListener {

	public static final ParamsDefinition CALLBACK_DEF_ONFILTERCHANGING = new ParamsDefinition("onFilterChanging", Object.class, "");
	public static final ParamsDefinition CALLBACK_DEF_ONEDIT = new ParamsDefinition("onEdit", Object.class, "com.f1.base.Table vals,com.f1.base.Table oldVals");
	public static final ParamsDefinition CALLBACK_DEF_ONCOLUMNSSIZED = new ParamsDefinition("onColumnsSized", Object.class, "");
	public static final ParamsDefinition CALLBACK_DEF_ONCOLUMNSARRANGED = new ParamsDefinition("onColumnsArranged", Object.class, "");
	public static final ParamsDefinition CALLBACK_DEF_ONCELLCLICKED = new ParamsDefinition("onCellClicked", String.class, "String column,Object val,java.util.Map rowvals");
	public static final ParamsDefinition CALLBACK_DEF_ONSELECTED = new ParamsDefinition("onSelected", Object.class, "");
	public static final ParamsDefinition CALLBACK_DEF_ONQUICKFILTERTYPING = new ParamsDefinition("onQuickFilterTyping", List.class,
			"com.f1.ami.web.AmiWebCustomColumn column,String userText,Integer suggestedLimit");
	public static final ParamsDefinition CALLBACK_DEF_ONSCROLL = new ParamsDefinition("onScroll", Object.class,
			"Integer viewTop,Integer viewHeight,Long contentWidth,Long contentHeight");
	public static final ParamsDefinition CALLBACK_DEF_ONBEFOREEDIT = new ParamsDefinition("onBeforeEdit", Object.class, "");

	static {
		CALLBACK_DEF_ONFILTERCHANGING.addDesc("Called when the filter is changing, this includes the search bar and column level filters");
		CALLBACK_DEF_ONEDIT.addDesc("Called after a user edits fields");
		CALLBACK_DEF_ONEDIT.addParamDesc(0, "the new values that reflect changes from user's edits");
		CALLBACK_DEF_ONEDIT.addParamDesc(1, "the old values prior to the user's edits");
		CALLBACK_DEF_ONCOLUMNSSIZED.addDesc("Called when the size (width) of a column has changed");
		CALLBACK_DEF_ONCOLUMNSARRANGED.addDesc("Called when the arrangement of columns has changed, this includes hiding/showing columns");
		CALLBACK_DEF_ONCELLCLICKED.addDesc("Called when the user has clicked on a cell");
		CALLBACK_DEF_ONCELLCLICKED.addParamDesc(0, "the title of the column of the cell the user clicked in");
		CALLBACK_DEF_ONCELLCLICKED.addParamDesc(1, "the value of the cell the user clicked in");
		CALLBACK_DEF_ONCELLCLICKED.addParamDesc(1, "the values in the row the user clicked on. (keys are the column titles, values are the cell values)");
		CALLBACK_DEF_ONSELECTED.addDesc("Called when the selection status of a row (or rows) has changed");
		CALLBACK_DEF_ONQUICKFILTERTYPING.addParamDesc(0, "The column header that the user has typed in");
		CALLBACK_DEF_ONQUICKFILTERTYPING.addParamDesc(1, "The text that the user has typed");
		CALLBACK_DEF_ONQUICKFILTERTYPING.addParamDesc(2, "The max number of entries that should be returned");
		CALLBACK_DEF_ONQUICKFILTERTYPING.addDesc("If a list is returned, the returned values are used for the autocompletion. If null is returned, default implementation is used");
		CALLBACK_DEF_ONSCROLL.addParamDesc(0, "Height above the visible content");
		CALLBACK_DEF_ONSCROLL.addParamDesc(1, "Height of the visible content");
		CALLBACK_DEF_ONSCROLL.addParamDesc(2, "Width of the total content");
		CALLBACK_DEF_ONSCROLL.addParamDesc(3, "Height of the total content");
		CALLBACK_DEF_ONSCROLL.addDesc("Called when table is being scrolled.");
		CALLBACK_DEF_ONBEFOREEDIT.addDesc("Called before table eiditing takes place. Returning false aborts editing.");
	}

	public static final byte EDIT_OFF = 0;
	public static final byte EDIT_SINGLE = 1;
	public static final byte EDIT_MULTI = 2;
	private static final String URI_PREFIX_AMI_COMMAND = "ami:";
	private static final String URI_PREFIX_AMI_QUERY = "ami_query:";
	private static final String URI_PREFIX_AMI_HTTP = "http";

	private static final Logger log = LH.get();
	protected boolean buildingSnapshot;
	private AmiWebFormula rowBackgroundColor;
	private AmiWebFormula rowTextColor;

	private int currentTimeLocation;
	private DerivedTable derivedTable;
	protected SmartTable rawDataTable;
	private Boolean isDebug;

	private Map<String, String> reservedColumnNames = new HashMap<String, String>();
	final private Map<String, Column> variableColumns = new HashMap<String, Column>();

	private long nextCheckTimeMs = 0L;
	private boolean isCurrentTimeInUse;
	private Map<AmiRelayRunAmiCommandRequest, Row> cmdRequestToRow = new HashMap<AmiRelayRunAmiCommandRequest, Row>();
	//	protected AmiWebAddObjectColumnFormPortlet addAmiObjectPortlet;
	private int rowHeight;

	protected FastTablePortlet tablePortlet;
	private String bgColor;
	private String selectColor;
	private String activeSelectColor;
	private String greyBarColor;
	private String menuFontColor;
	private String menuBarColor;
	private String searchBarColor;
	private String searchBarFontColor;
	private String searchFieldBorderColor;
	private String searchButtonsColor;
	private Object defaultFontColor;
	private String filteredColumnBgColor;
	private String filteredColumnFontColor;
	private String titleBarColor;
	private String titleBarFontColor;
	private String gripColor;
	private String trackColor;
	private String trackButtonColor;
	private String scrollBorderColor;
	private Integer scrollBarWidth;
	private Integer cellBottomDivider;
	private Integer cellRightDivider;
	private String verticalAlign;
	private String scrollIconsColor;
	private String cellBorderColor;
	private String searchBarDivColor;
	private boolean rollupEnabled = false;
	private boolean showCommandMenuItems = true;

	public AmiWebAbstractTablePortlet(PortletConfig config) {
		super(config);
		super.setFontSize(13);
		this.tablePortlet = new FastTablePortlet(generateConfig(), null);
		this.tablePortlet.setMaxShowValuesForFilterDialog(
				getManager().getTools().getOptional(AmiWebProperties.PROPERTY_AMI_WEB_FILTER_DIALOG_MAX_OPTIONS, this.tablePortlet.getMaxShowValuesForFilterDialog()));
		this.setChild(this.tablePortlet);
		String[] ids = getTableIds();
		Class[] types = getTableTypes();
		DerivedTable derivedTable = new DerivedTable(getStackFrame(), types, ids);
		this.derivedTable = initTable(derivedTable);
		if (this instanceof AmiWebAggregateObjectTablePortlet) {
			AggregateFactory methodFactory = getScriptManager().createAggregateFactory();
			this.formulas.setAggregateFactory(methodFactory);
			this.rowBackgroundColor = this.formulas.addFormulaAggRt("rowBackgroundColor", String.class);
			this.rowTextColor = this.formulas.addFormulaAggRt("rowColor", String.class);
		} else {
			this.rowBackgroundColor = this.formulas.addFormula("rowBackgroundColor", String.class);
			this.rowTextColor = this.formulas.addFormula("rowColor", String.class);
		}
		this.where = new AmiWebWhereClause(this);
		this.formulas.addFormulasListener(this);
		this.tablePortlet.setQuickfilterAutocompleteManager(this);
		this.currentTimeLocation = derivedTable.getColumnIds().contains(TABLE_PARAM_W) ? derivedTable.getColumn(TABLE_PARAM_W).getLocation() : -1;
		FastWebTable t = getTable();
		if (t != null) {
			for (int i = 0; i < ids.length; i++) {
				String id = ids[i];
				if (!id.startsWith("!")) {
					putSpecialVariable(id, derivedTable.getColumn(id).getType());
					WebColumn col = getTable().getColumnNoThrow(id);
					if (col != null)
						reservedColumnNames.put(id, col.getColumnName());
				}
			}
			t.setMenuFactory(this);
			t.setColumnMenuFactory(this);
			t.addColumnMenuListener(this);
		}
		isDebug = getService().isDebug();
		this.tablePortlet.getTable().addWebTableListener(this);
		this.tablePortlet.getTable().addMenuListener(this);
		this.initTableStyle();
		this.getStylePeer().initStyle();
		this.tablePortlet.setDialogStyle(getService().getUserDialogStyleManager());
		this.tablePortlet.setFormStyle(getService().getUserFormStyleManager());
		this.tablePortlet.setMenuStyle(getService().getDesktop().getMenuStyle());
		this.tablePortlet.setTooltipFactory(this);
	}
	protected String[] getTableIds() {
		return new String[] { TABLE_PARAM_CENTER, TABLE_PARAM_ID, TABLE_PARAM_DATA, TABLE_PARAM_M, TABLE_PARAM_W, TABLE_PARAM_C, TABLE_PARAM_V, TABLE_PARAM_T, TABLE_PARAM_I,
				TABLE_PARAM_E, TABLE_PARAM_P };
	}
	protected Class[] getTableTypes() {
		return new Class[] { String.class, Long.class, Object.class, Long.class, Long.class, Long.class, Integer.class, String.class, String.class, Long.class, String.class };
	}
	public FastWebTable getTable() {
		return tablePortlet.getTable();
	}
	protected DerivedTable initTable(DerivedTable derivedTable) {
		derivedTable.setTitle("Ami Objects");
		SmartTable st = new BasicSmartTable(derivedTable);
		AmiWebFormatterManager fm = getService().getFormatterManager();
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());
		table.addColumn(false, "AMI-ID", TABLE_PARAM_ID, fm.getBasicFormatter());
		table.addColumn(false, "AMI-Center", TABLE_PARAM_CENTER, fm.getBasicFormatter());
		table.addColumn(false, "Params", TABLE_PARAM_DATA, fm.getParamsWebCellFormatter()).setWidth(250);
		table.addColumn(false, "Modified Time", TABLE_PARAM_M, fm.getDateTimeSecsWebCellFormatter());
		table.addColumn(false, "Created Time", TABLE_PARAM_C, fm.getDateTimeSecsWebCellFormatter());
		table.addColumn(false, "Revision", TABLE_PARAM_V, fm.getIntegerWebCellFormatter());
		table.addColumn(false, "Current Time", TABLE_PARAM_W, fm.getDateTimeSecsWebCellFormatter());
		this.tablePortlet.setTable(table);
		FastWebTable t = getTable();
		if (t != null) {
			t.addColumn(false, "Type", TABLE_PARAM_T, fm.getBasicFormatter());
			t.addColumn(false, "Application", TABLE_PARAM_P, fm.getBasicFormatter());
			t.addColumn(false, "Expires Time", TABLE_PARAM_E, fm.getDateTimeSecsWebCellFormatter());
			t.addColumn(false, "Object", TABLE_PARAM_I, fm.getBasicFormatter()).setCssColumn("bold");
		}
		return derivedTable;
	}

	//	protected final String getType(AmiObject data) {
	//		return data.getManager().getAmiKeyStringFromPool(((AmiObject) data).getType());
	//	}

	@Override
	public void onClosed() {
		if (isCurrentTimeInUse)
			getManager().removePortletManagerListener(this);
		super.onClosed();
	}

	@Override
	public WebMenu createMenu(WebTable table) {
		List<WebMenuItem> entries = new ArrayList<WebMenuItem>();
		BasicMultiMap.List<String, String> title2portletId = new BasicMultiMap.List<String, String>();
		int selectedCnt = this.getTable().getSelectedRows().size();
		if (selectedCnt > 0) {
			if (SH.is(this.editContextMenuTitle) && (this.editMode == EDIT_MULTI || (this.editMode == EDIT_SINGLE && selectedCnt == 1)))
				entries.add(new BasicWebMenuLink(this.editContextMenuTitle, true, "edit_rows"));
			for (AmiWebDmLink link : getDmLinksFromThisPortlet()) {
				if (link.isRunOnSelect() || link.isRunOnDoubleClick() || link.isRunOnAmiScript())
					continue;
				for (String s : SH.split('|', link.getTitle())) {
					title2portletId.putMulti(SH.trimWhitespace(s), link.getLinkUid());
				}
			}

		}
		for (String s : CH.sort(title2portletId.keySet())) {
			entries.add(new BasicWebMenuLink(s, true, SH.join('_', title2portletId.get(s), new StringBuilder("query_")).toString()));
		}

		BasicWebMenu m = new BasicWebMenu("", true, entries);
		addCommandMenuItems(m, table);
		m.sort();

		addCustomMenuItems(m);
		if (this.rollupEnabled) {
			entries.add(new BasicWebMenuLink("Summarize selected", table.getSelectedRows().size() != 0, "rollup_selected"));
			entries.add(new BasicWebMenuLink("Summarize all", true, "rollup_all"));
		}
		return m;
	}

	protected void addCommandMenuItems(BasicWebMenu m, WebTable table) {
		if (!getShowCommandMenuItems()) {
			return;
		}
		IterableAndSize<AmiWebCommandWrapper> commands = getService().getSystemObjectsManager().getAmiCommands();
		List<Row> rows2 = getSelectedRowsForCommand(table);
		if (commands.size() > 0) {
			HashMap<String, BasicWebMenu> ns2m = new HashMap<String, BasicWebMenu>();
			ns2m.put("", m);
			StringBuilder sb = new StringBuilder();
			StringBuilder sbn = new StringBuilder();
			com.f1.base.CalcTypes tableTypes = getColumnTypesForCommand(table);
			for (AmiWebCommandWrapper def : commands) {
				BasicWebMenu pm = m;

				if (def.isCallbackClick() && def.matchesFilteredAndWhere(getPortletVarTypes(), getPortletVars(), tableTypes, rows2, this)) {

					SH.clear(sb);
					SH.clear(sbn);

					sb.append(def.getTitle());

					int idx = sb.lastIndexOf(".");
					if (idx >= 0) {
						sbn.append(sb, idx + 1, sb.length());
						sb.setLength(idx);
						pm = getMenu(sb, pm, ns2m);
					} else
						sbn.append(sb);

					if (def.getStyle().separator == Separator.TOP || def.getStyle().separator == Separator.BOTH)
						pm.add(new BasicWebMenuDivider(def.getPriority()));

					pm.add(new BasicWebMenuLink(sbn.toString(), def.matchesEnabled(tableTypes, rows2, this), "cmd_" + def.getObject().getId(), def.getPriority()));

					if (def.getStyle().separator == Separator.BOTTOM || def.getStyle().separator == Separator.BOTH)
						pm.add(new BasicWebMenuDivider(def.getPriority()));
				}
			}
		}
	}

	private BasicWebMenu getMenu(StringBuilder sb, BasicWebMenu pm, HashMap<String, BasicWebMenu> m) {
		BasicWebMenu menu = m.get(sb.toString());
		if (menu == null) {
			int idx = sb.lastIndexOf(".");
			String n;
			if (idx >= 0) {
				n = sb.substring(idx + 1);
				sb.setLength(idx);
				pm = getMenu(sb, pm, m);
			} else
				n = sb.toString();

			menu = new BasicWebMenu(n, true);
			pm.add(menu);
			m.put(n, menu);
			return menu;
		}

		return menu;
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {

		Action action = result.getActionNoThrowable();
		if (action instanceof AmiCenterPassToRelayResponse) {
			AmiRelayResponse agentResponse = ((AmiCenterPassToRelayResponse) action).getAgentResponse();
			if (agentResponse instanceof AmiRelayRunAmiCommandResponse) {
				AmiRelayRunAmiCommandRequest cmdRequest = (AmiRelayRunAmiCommandRequest) ((AmiCenterPassToRelayRequest) result.getRequestMessage().getAction()).getAgentRequest();
				AmiRelayRunAmiCommandResponse cmdResponse = (AmiRelayRunAmiCommandResponse) agentResponse;
				int code = cmdResponse.getStatusCode();
				if (this.tablePortlet.isEditing()) {
					if (code == AmiRelayRunAmiCommandResponse.STATUS_OKAY) {
						this.tablePortlet.finishEdit();
						this.onEditFinished();
					}
				} else {
					Row row = this.cmdRequestToRow.remove(cmdRequest);
					if (code == AmiRelayRunAmiCommandResponse.STATUS_UPDATE_RECORD && agentResponse.getOk() && !isRealtime() && row != null) {
						Map<String, Object> sink = new HashMap<String, Object>();
						if (cmdResponse.getParams() != null)
							sink.putAll(cmdResponse.getParams());
						SmartTable table = getTable().getTable();
						for (Column e : getVariableColumns()) {
							Object value = sink.get(e.getId());
							if (value == null && !sink.containsKey(e.getId()))
								continue;
							int location = e.getLocation();
							try {
								value = table.getColumnAt(location).getTypeCaster().castNoThrow(value);
							} catch (Exception e2) {
								LH.warning(log, "Error casting data: ", value, e2);
							}
							row.putAt(location, value);
						}
					}
				}
			}
		}
		super.onBackendResponse(result);
	}

	@Override
	public boolean onAmiContextMenu(String action) {
		if (super.onAmiContextMenu(action)) {
			return true;
		} else {
			this.onContextMenu(getTable(), action);
			return true;
		}
	}
	@Override
	public void onContextMenu(WebTable table, String action) {
		// Set dimensions for table summary window 
		int summaryHeight;
		int summaryBaseWidth = 101;
		int summaryWidth = summaryBaseWidth * (table.getVisibleColumnsCount() + 1); // Add 1 to account for extra column with "Sum, Minimum, Maximum ..."
		if (summaryWidth > 1000) {
			summaryWidth = 1000;
			summaryHeight = 198;
		} else
			summaryHeight = 183;

		if (table.getSelectedRows().size() > 0 && action.startsWith("query_")) {
			String remotePortletIds = SH.stripPrefix(action, "query_", true);
			for (String t : SH.split('_', remotePortletIds)) {
				AmiWebDmLink link = getService().getDmManager().getDmLink(t);
				AmiWebDmUtils.sendRequest(getService(), link);
			}
		} else if (action.equals("rollup_selected")) {
			getManager()
					.showDialog("Summary of " + table.getSelectedRows().size() + " of " + table.getRowsCount() + " rows",
							new AmiWebTableAnalyticsPortlet(generateConfig(), this.tablePortlet, false), summaryWidth, summaryHeight)
					.setStyle(getService().getUserDialogStyleManager());
		} else if (action.equals("edit_rows") && !isEditingBlockedByOnBeforeEdit()) {
			onUserEditStart();
		} else if (action.equals("rollup_all")) {
			getManager().showDialog("Summary of all " + table.getRowsCount() + " rows", new AmiWebTableAnalyticsPortlet(generateConfig(), this.tablePortlet, true), summaryWidth,
					summaryHeight).setStyle(getService().getUserDialogStyleManager());
		} else if (action.equals("addcol")) {
			AmiWebAddObjectColumnFormPortlet addAmiObjectPortlet = newAddAmiObjectColumnFormPortlet(generateConfig(), this, getTable().getVisibleColumnsCount(), null);
			if (addAmiObjectPortlet != null)
				getManager().showDialog(addAmiObjectPortlet.getTitle(), addAmiObjectPortlet);
		} else if (action.startsWith("cmd_")) {
			long id = SH.parseLong(action, "cmd_".length(), action.length(), 10);
			AmiWebCommandWrapper cmd = getService().getSystemObjectsManager().getAmiCommand(id);
			if (cmd == null) {
				getManager().showAlert("command no longer available");
				return;
			}
			this.callCommandOnSelected(cmd);
		} else if (action.startsWith("remcol_")) {
			String colId = SH.stripPrefix(action, "remcol_", true);
			WebColumn column = getTable().getColumn(colId);
			ConfirmDialogPortlet p = new ConfirmDialogPortlet(generateConfig(), "Are you sure you want to delete the column:<BR><B>" + column.getColumnName(),
					ConfirmDialogPortlet.TYPE_OK_CUSTOM);
			p.addButton("delete_column", "Delete");
			p.addButton(ConfirmDialogPortlet.ID_NO, "Cancel");
			p.addDialogListener(this);
			p.setCorrelationData(column.getColumnId());
			getManager().showDialog("Delete Column", p);
		} else if (action.startsWith("editcol_")) {
			String colId = SH.stripPrefix(action, "editcol_", true);
			WebColumn column = getTable().getColumn(colId);
			AmiWebCustomColumn col = getCustomDisplayColumn(column.getColumnId());
			if (!checkCanRemoveCustomColumnById(col.getColumnId()))
				return;
			if (getTable().getFilteredInColumns().contains(column.getColumnId())) {
				getManager().showAlert("You can not edit columns with active filters");
				return;
			}
			AmiWebAddObjectColumnFormPortlet addAmiObjectPortlet = newAddAmiObjectColumnFormPortlet(generateConfig(), this, table.getColumnPosition(column.getColumnId()), col);
			if (addAmiObjectPortlet != null)
				getManager().showDialog(addAmiObjectPortlet.getTitle(), addAmiObjectPortlet);
		} else if ("__arrange".equals(action)) {
			getManager().showDialog("Arrange Columns", new ArrangeColumnsPortlet(generateConfig(), this.tablePortlet.getTable()));
		} else if (isCustomContextMenuAction(action)) {
			processCustomContextMenuAction(action);
		}
	}
	public boolean checkCanRemoveCustomColumnById(String colId) {
		if (getTable().getFilteredInColumns().contains(colId)) {
			getTable().setFilteredIn(colId, (Set) null);

		} else if (getTable().getTable().getKeepSorting() && getTable().getSortedColumnIds().contains(colId)) {
			getTable().sortRows(colId, true, false, false);
		}

		return true;
	}

	protected void removeUnusedVariableColumns() {
		Set<Object> sink = new HashSet<Object>();
		if (this.rowBackgroundColorColumnId != null) {
			sink.add(this.rowBackgroundColorColumnId);
			Column col = getTable().getTable().getColumn(this.rowBackgroundColorColumnId);
			if (col instanceof DerivedColumn)
				DerivedHelper.getDependencyIds(((DerivedColumn) col).getCalculator(), sink);
		}
		if (this.rowTextColorColumnId != null) {
			sink.add(this.rowTextColorColumnId);
			Column col = getTable().getTable().getColumn(this.rowTextColorColumnId);
			if (col instanceof DerivedColumn)
				DerivedHelper.getDependencyIds(((DerivedColumn) col).getCalculator(), sink);
		}
		StringBuilder tmp = new StringBuilder();
		Set<Object> sink2 = new HashSet<Object>();
		for (AmiWebDmLink i : getDmLinksFromThisPortlet()) {
			com.f1.base.CalcTypes target = AmiWebDmUtils.getTarget(getService(), i);
			for (String varname : i.getWhereClauseVarNames()) {
				ParsedWhere calc = new AmiWebDmUtils.ParsedWhere(getService(), target, i, varname, getService().getDebugManager(), tmp);
				if (calc.calc != null)
					DerivedHelper.getDependencyIds(calc.calc, sink2);
			}
		}
		for (Object o : sink2) {
			String s = (String) o;
			if (SH.startsWith(s, AmiWebDmUtils.VARPREFIX_SOURCE))
				sink.add(SH.stripPrefix(s, AmiWebDmUtils.VARPREFIX_SOURCE, true));
		}
		for (String id : getTable().getColumnIds())
			for (String i : getTable().getColumn(id).getTableColumns()) {
				if (sink.add(i)) {
					Column col = getTable().getTable().getColumn(i);
					if (col instanceof DerivedColumn)
						DerivedHelper.getDependencyIds(((DerivedColumn) col).getCalculator(), sink);
				}
			}
		Set<String> toRemove = CH.comm(this.getTable().getTable().getColumnIds(), (Set) sink, true, false, false);
		for (String k : getSpecialVariables().getVarKeys())
			toRemove.remove(k);
		toRemove.remove(AmiConsts.TABLE_PARAM_DATA);
		//		toRemove.remove(AmiConsts.TABLE_PARAM_PARAMS);

		//remove derivatives first
		for (String o : toRemove)
			if (o.startsWith("!") && !o.toString().startsWith("#"))
				getTable().getTable().removeColumn(o);
		for (Object oo : toRemove) {
			String o = (String) oo;
			if (!o.startsWith("!") && !o.startsWith("#")) {
				this.removeVariableColumn(o);
				getTable().getTable().removeColumn(o);
			}
		}
		for (Object o : toRemove)
			if (!this.customDisplayColumnIds.containsKey(o.toString())) {
				removeUserDefinedVariable((String) o);
			}
		updateCurrentTimeInUseFlag();
		this.getTable().setRowBgColorFormula(this.rowBackgroundColorColumnId);
		this.getTable().setRowTxColorFormula(this.rowTextColorColumnId);
	}

	public String addDerivedColumn(DerivedCellCalculator formula, com.f1.base.CalcTypes varTypes) {
		SmartTable st = getTable().getTable();
		HashSet<String> sink = new HashSet<String>();
		HashSet<String> transColumns = new HashSet<String>();
		DerivedHelper.getDependencyIds(formula, (Set) sink);
		for (String k : st.getColumnIds()) {
			sink.remove(k);
		}
		for (String k : getSpecialVariables().getVarKeys())
			sink.remove(k);
		for (String k : sink) {
			if (this.customDisplayColumnIds.containsKey(k) && this.getCustomDisplayColumn(k).isTransient()) {
				transColumns.add(k);
			}
		}
		sink.removeAll(transColumns);
		for (String newVar : sink) {
			Class<?> type = varTypes.getType(newVar);
			if (type == null && newVar.startsWith("$"))
				type = String.class;
			Column col = st.addColumn(type, newVar);
			this.addVariableColumn(newVar.toString(), col);
			this.putUserDefinedVariable(newVar, type);
		}
		for (Column col : st.getColumns()) {
			if (col instanceof DerivedColumn) {
				if (OH.eq(((DerivedColumn) col).getCalculator(), formula))
					return (String) col.getId();
			} else if (OH.eq(formula, new DerivedCellCalculatorRef(-1, col.getType(), col.getId())))
				return (String) col.getId();
		}
		String id = TableHelper.generateId(st.getColumnIds(), "!");
		st.addDerivedColumn(id, formula);
		return id;
	}

	public String getColumnTitleFor(String i) {
		for (String id : getTable().getColumnIds()) {
			WebColumn col = getTable().getColumn(id);
			if (col.getTableColumns().length == 1 && OH.eq(col.getTableColumns()[0], i))
				return col.getColumnName();
		}
		return i;
	}

	public String getSpecialVariableTitleFor(String name) {
		return CH.getOr(reservedColumnNames, name, name);
	}

	public void setAmiTitle(String title, boolean isOverride) {
		super.setAmiTitle(title, isOverride);
		this.tablePortlet.setTableTitle(title);
	}
	public void setAmiDownloadName(String downloadName) {
		this.downloadName = downloadName;
		this.tablePortlet.setDownloadName(downloadName);
	}

	public String getAmiDownloadName() {
		return this.downloadName;
	}
	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		super.init(configuration, origToNewIdMapping, sb);
		FastWebTable table = this.tablePortlet.getTable();
		this.currentTimeUpdateFrequencyMs = CH.getOr(Caster_Integer.INSTANCE, configuration, "curtimeUpdateFrequency", 1000);
		this.rollupEnabled = CH.getOr(Caster_Boolean.INSTANCE, configuration, "rollupEnabled", this.rollupEnabled);
		this.getTable().setVisibleColumnsLimit(CH.getOr(Caster_Integer.PRIMITIVE, configuration, "vclim", -1));
		this.editMode = CH.getOr(Caster_Byte.INSTANCE, configuration, "editMode", EDIT_OFF);
		this.editViaDoubleClick = CH.getOr(Caster_Boolean.INSTANCE, configuration, "editDblClk", this.editViaDoubleClick);
		this.editContextMenuTitle = CH.getOr(Caster_String.INSTANCE, configuration, "editMenuTitle", this.editContextMenuTitle);
		this.editCommandId = CH.getOr(Caster_String.INSTANCE, configuration, "editCommandId", this.editCommandId);
		this.editAppName = CH.getOr(Caster_String.INSTANCE, configuration, "editAppName", this.editAppName);
		String editAmiScript = CH.getOr(Caster_String.INSTANCE, configuration, "editAmiScript", null);
		if (editAmiScript != null)
			this.callbacks.setAmiScriptCallbackNoCompile("onEdit", editAmiScript);
		final com.f1.utils.structs.table.stack.BasicCalcTypes varTypes;
		//I believe this is redundant from initTable -- rob
		//		Map<String, String> vtypes = (Map<String, String>) CH.getOr(Caster_Simple.OBJECT, configuration, "varTypes", null);
		//		if (vtypes != null) {
		//			varTypes = new com.f1.utils.structs.table.stack.BasicCalcTypes();
		//			for (Entry<String, String> e : vtypes.entrySet())
		//				varTypes.putType(e.getKey(), AmiWebUtils.saveCodeToType(getService(), e.getValue()));
		//			setUserDefinedVariables(varTypes);
		//		}

		if (configuration.containsKey("visibleColumns")) {//backwards compatibility
			List<Map<String, Object>> visibleColumns = (List<Map<String, Object>>) CH.getOrThrow(Caster_Simple.OBJECT, configuration, "visibleColumns");
			initSpecialColumns(configuration, visibleColumns);
			initAmiTable(configuration, origToNewIdMapping, sb, true);
			List<Map<String, Object>> hiddenColumns = (List<Map<String, Object>>) CH.getOrThrow(Caster_Simple.OBJECT, configuration, "hiddenColumns");
			this.tablePortlet.init(configuration, sb, visibleColumns, hiddenColumns);
		} else {
			List<Map<String, Object>> visibleColumns = new ArrayList<Map<String, Object>>();
			initSpecialColumns(configuration, visibleColumns);
			initAmiTable(configuration, origToNewIdMapping, sb, false);
			final Map<String, Object> filtersMap = (Map<String, Object>) configuration.get("filters");
			if (filtersMap != null) {
				for (Entry<String, Object> e : filtersMap.entrySet()) {
					Object val = e.getValue();
					if (val instanceof List) {
						//LEGACY
						table.setFilteredIn(e.getKey(), new HashSet<String>((List) val));
					} else {
						Map<String, Object> m = (Map<String, Object>) val;
						List<String> filterValues = CH.l((Collection<String>) CH.getOrThrow(Caster_Simple.OBJECT, m, "v"));
						boolean keep = CH.getOrThrow(Caster_Boolean.INSTANCE, m, "k");
						boolean pattern = CH.getOr(Caster_Boolean.INSTANCE, m, "p", Boolean.FALSE);
						boolean includeNulls = CH.getOrThrow(Caster_Boolean.INSTANCE, m, "n");
						String min = CH.getOrThrow(Caster_String.INSTANCE, m, "i");
						String max = CH.getOrThrow(Caster_String.INSTANCE, m, "x");
						boolean mini = CH.getOr(Caster_Boolean.INSTANCE, m, "ii", Boolean.FALSE);
						boolean maxi = CH.getOr(Caster_Boolean.INSTANCE, m, "xi", Boolean.FALSE);
						if (table.getColumnIds().contains(e.getKey())) //only set filter on the column if the column exists
							table.setFilteredIn(e.getKey(), new HashSet<String>(filterValues), keep, includeNulls, pattern, min, mini, max, maxi);
					}
				}
			}
			List<Map<String, String>> sorting = (List<Map<String, String>>) CH.getOr(Caster_Simple.OBJECT, configuration, "sorting", null);
			if (CH.isntEmpty(sorting)) {
				boolean first = true;
				for (Map<String, String> m : sorting) {
					String id = CH.getOrThrow(Caster_String.INSTANCE, m, "id");
					String order = CH.getOrThrow(Caster_String.INSTANCE, m, "order");
					WebColumn column = table.getColumnNoThrow(id);
					if (column != null) {
						table.sortRows(column.getColumnId(), "asc".equals(order), true, !first);
						first = false;
					}
				}
			}
			List<Map<String, Object>> customCols = (List<Map<String, Object>>) CH.getOr(Caster_Simple.OBJECT, configuration, "amiCols", null);
			for (Map<String, Object> column : customCols) {
				String id = CH.getOrThrow(Caster_String.INSTANCE, column, "id");
				int width = CH.getOrThrow(Caster_Integer.INSTANCE, column, "width");
				WebColumn col = table.getColumnNoThrow(id);
				if (col != null) {
					col.setWidth(width);
					if (column.containsKey("location"))
						visibleColumns.add(column);
					else
						table.hideColumn(id);
				} else {
					if (!"!params".equals(id))
						sb.append("column not found: " + id + "<BR>");
				}
			}
			Collections.sort(visibleColumns, new FastTablePortlet.LocationComparator());
			int location = 0;
			for (Map<String, Object> column : visibleColumns) {
				String id = CH.getOrThrow(Caster_String.INSTANCE, column, "id");
				table.showColumn(id, location++);
			}
			table.setPinnedColumnsCount(CH.getOr(Caster_Integer.INSTANCE, configuration, "pinCnt", 0));
		}

		this.where.init(configuration);
		updateCurrentTimeInUseFlag();
		setAmiDownloadName((String) configuration.get("downloadName"));
	}
	protected void initSpecialColumns(Map<String, Object> configuration, List<Map<String, Object>> visibleColumns) {

	}
	protected void initAmiTable(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb, boolean needsVarReplacementForBackwardsCompatibility) {
		List<Map<String, Object>> customCols = (List<Map<String, Object>>) CH.getOr(Caster_Simple.OBJECT, configuration, "amiCols", null);

		//determine next id
		//		int maxId = -1;
		//		for (Map<String, Object> m : customCols) {
		//			String id = CH.getOr(Caster_String.INSTANCE, m, "id", null);
		//			if (id.startsWith("!d")) {
		//				String num = SH.stripPrefix(id, "!d", false);
		//				if (num.indexOf('!') != -1)
		//					continue;
		//				int n = Integer.parseInt(num);
		//				if (n > maxId)
		//					maxId = n;
		//			}
		//		}

		final com.f1.utils.structs.table.stack.BasicCalcTypes varTypes = AmiWebUtils.fromVarTypesConfiguration(getService(), (Map<String, String>) configuration.get("varTypes"));
		//		Map<String, String> vtypes = (Map<String, String>) CH.getOr(Caster_Simple.OBJECT, configuration, "varTypes", null);
		//		varTypes = new com.f1.utils.BasicTypes(vtypes.size());
		//		for (Entry<String, String> e : vtypes.entrySet())
		//			varTypes.put(e.getKey(), AmiWebUtils.toClass(AmiWebUtils.saveCodeToType(e.getValue())));
		setUserDefinedVariables(varTypes);
		varTypes.putAll(getSpecialVariables());
		//		Map<String, String> fms = new HashMap<String, String>();
		//		if (needsVarReplacementForBackwardsCompatibility) {
		//			for (Map<String, Object> m : customCols) {
		//				AmiWebCustomColumn col = new AmiWebCustomColumn(m);
		//				fms.put(col.getColumnId(), col.getFormula());
		//			}
		//		}
		//		System.out.println("name: " + fms);
		for (Map<String, Object> m : customCols) {
			if (m.containsKey("tp"))
				try {
					AmiWebCustomColumn col = new AmiWebCustomColumn(this, m, this instanceof AmiWebAggregateObjectTablePortlet);

					//backwards compatibility
					String targetFormula = CH.getOr(Caster_String.INSTANCE, m, AmiWebCustomColumn.CONFIG_TARGET_FORMULA, null);
					if (targetFormula != null) {
						String script = getAmiScriptCallbacks().getAmiScriptCallback("onCellClicked");
						if (script == null)
							script = "";
						script += "if(column==\"" + col.getTitle(true) + "\"){\n  " + SH.replaceAll(targetFormula, '\n', "  \n") + "\n}\n";
						getAmiScriptCallbacks().setAmiScriptCallbackNoCompile("onCellClicked", script);
					}
					addCustomColumn(col, sb, -1, null, varTypes, false);
				} catch (Exception e) {
					LH.warning(log, "Error with custom column", m, e);
				}
		}
		//		setRowTextColor(CH.getOr(Caster_String.INSTANCE, configuration, "rtxc", null), false);
		//		setRowBackgroundColor(CH.getOr(Caster_String.INSTANCE, configuration, "rbgc", null), false);
		this.rowTextColor.initFormula(CH.getOr(Caster_String.INSTANCE, configuration, "rtxc", null));
		this.rowBackgroundColor.initFormula(CH.getOr(Caster_String.INSTANCE, configuration, "rbgc", null));
		//		this.setRowBackgroundColor(rowBackgroundColor2, toFormula(rowBackgroundColor2, sb, "row background color", varTypes), varTypes);
		//		this.setRowTextColor(rowTextColor2, toFormula(rowTextColor2, sb, "row text color", varTypes), varTypes);
		this.resetWhere();
		this.showCommandMenuItems = CH.getOr(Caster_Boolean.PRIMITIVE, configuration, "showCommandMenu", true);
		this.setScrollToBottomOnAppend(CH.getOr(Caster_Boolean.PRIMITIVE, configuration, "scrollToBottomOnAppend", false));
	}

	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = new HashMap<String, Object>();
		FastWebTable table = this.tablePortlet.getTable();
		r.put("pinCnt", table.getPinnedColumnsCount());

		if (table.isKeepSorting()) {
			final List<Map<String, String>> sortingColumns = new ArrayList<Map<String, String>>();
			for (Entry<String, Boolean> i : table.getSortedColumns()) {
				Map<String, String> m = new HashMap<String, String>();
				m.put("id", i.getKey());
				m.put("order", i.getValue() ? "asc" : "dsc");
				sortingColumns.add(m);
			}
			r.put("sorting", sortingColumns);
		}
		final Map<String, Map<String, Object>> filtersMap = new HashMap<String, Map<String, Object>>();
		for (String filteredInColumn : table.getFilteredInColumns()) {
			WebTableFilteredInFilter f = table.getFiltererdIn(filteredInColumn);
			if (f != null) {
				Map<String, Object> m = CH.m("v", new HashSet<String>(f.getValues()), "k", f.getKeep(), "p", f.getIsPattern(), "n", f.getIncludeNull(), "i", f.getMin(), "x",
						f.getMax(), "ii", f.getMinInclusive(), "xi", f.getMaxInclusive());
				filtersMap.put(filteredInColumn, m);
			}
		}
		r.put("filters", filtersMap);
		r.putAll(super.getConfiguration());
		r.put("curtimeUpdateFrequency", this.getCurrentTimeUpdateFrequencyMs());
		AmiWebUtils.putSkipEmpty(r, "rtxc", this.rowTextColor.getFormulaConfig());
		AmiWebUtils.putSkipEmpty(r, "rbgc", this.rowBackgroundColor.getFormulaConfig());
		r.put("editMode", this.editMode);
		r.put("editDblClk", this.editViaDoubleClick);
		AmiWebUtils.putSkipEmpty(r, "editMenuTitle", this.editContextMenuTitle);
		AmiWebUtils.putSkipEmpty(r, "editCommandId", this.editCommandId);
		AmiWebUtils.putSkipEmpty(r, "editAppName", this.editAppName);
		where.getConfiguration(r);
		Map<String, String> vtypes = new HashMap<String, String>();
		//		AmiWebUtils.toVarTypesConfiguration(getService(), this.getAmiLayoutFullAlias(), this.getUserDefinedVariables(), vtypes);
		AmiWebUtils.toVarTypesConfiguration(getService(), this.getAmiLayoutFullAlias(), this.getUsedVariables(), vtypes);
		r.put("varTypes", vtypes);
		r.put("rollupEnabled", this.rollupEnabled);
		r.put("showCommandMenu", this.showCommandMenuItems);
		r.put("scrollToBottomOnAppend", this.getTable().getScrollToBottomOnAppend());
		Set<String> special = CH.s(this.getTableIds());
		if (this.getTable().getVisibleColumnsLimit() != -1) // or > -1 ?
			r.put("vclim", this.getTable().getVisibleColumnsLimit());

		List<Map<String, Object>> customCols = new ArrayList<Map<String, Object>>(getCustomDisplayColumnIds().size());
		for (int i = 0; i < table.getVisibleColumnsCount(); i++) {
			WebColumn col = table.getVisibleColumn(i);
			AmiWebCustomColumn amiCol = getCustomDisplayColumn(col.getColumnId());
			boolean isTransientCol = amiCol != null && amiCol.isTransient();
			boolean isInvalidCol = amiCol == null && !special.contains(col.getColumnId());
			if (isTransientCol || isInvalidCol)
				continue;
			Map<String, Object> map = amiCol == null ? new HashMap<String, Object>() : amiCol.getColumnConfig();
			map.put("width", col.getWidth());
			map.put("id", col.getColumnId());
			map.put("location", i);
			customCols.add(map);
		}
		for (int i = 0; i < table.getHiddenColumnsCount(); i++) {
			WebColumn col = table.getHiddenColumn(i);
			AmiWebCustomColumn amiCol = getCustomDisplayColumn(col.getColumnId());
			boolean isTransientCol = amiCol != null && amiCol.isTransient();
			boolean isInvalidCol = amiCol == null && !special.contains(col.getColumnId());
			if (isTransientCol || isInvalidCol)
				continue;
			Map<String, Object> map = amiCol == null ? new HashMap<String, Object>() : amiCol.getColumnConfig();
			map.put("width", col.getWidth());
			map.put("id", col.getColumnId());
			customCols.add(map);
		}
		r.put("amiCols", customCols);
		CH.putNoNull(r, "downloadName", this.downloadName);
		return r;
	}

	public WebColumn findColumnByTitle(String title) {
		for (String id : this.getTable().getColumnIds()) {
			WebColumn r = this.getTable().getColumn(id);
			if (r.getColumnName().equals(title))
				return r;
		}
		return null;
	}
	//	final public AmiWebCustomColumn addCustomColumn(Map<String, Object> m, com.f1.base.Types varTypes, int columnLocation, AmiWebCustomColumn replacing,
	//			StringBuilder errorSink, boolean populateValues) {
	//		AmiWebCustomColumn col = new AmiWebCustomColumn(this, m);
	//		return addCustomColumn(col, errorSink, columnLocation, replacing, varTypes, populateValues);
	//	}
	public boolean addCustomColumn(AmiWebCustomColumn col, StringBuilder errorSink, int columnLocation, AmiWebCustomColumn replacing, com.f1.base.CalcTypes varTypes,
			boolean populateValues) {
		String titleCol = col.getTitle(true);

		if (SH.isnt(col.getColumnId())) {
			errorSink.append("Column Id required");
			return false;
		}
		if (replacing != null) {
			if (OH.ne(replacing.getColumnId(), col.getColumnId())) {
				errorSink.append("Mismatching Column Id: '").append(col.getColumnId()).append("' vs '").append("'").append(replacing.getColumnId()).append("'");
				return false;
			}
		} else if (this.customDisplayColumnIds.containsKey(col.getColumnId())) {
			errorSink.append("Duplicate Column Id: '").append(col.getColumnId()).append("'");
			return false;
		}

		if (col.getEditType(true) != AmiWebCustomColumn.EDIT_DISABLED) {
			for (String i : this.getCustomDisplayColumnIds()) {
				AmiWebCustomColumn c = this.getCustomDisplayColumn(i);
				if (c == replacing)
					continue;
				if (c.getEditType(true) != AmiWebCustomColumn.EDIT_DISABLED && OH.eq(c.getEditId(true), col.getEditId(true))) {
					errorSink.append("Duplicate Edit Id: '").append(c.getEditId(true)).append("'");
					return false;
				}
			}
		}
		if (SH.isnt(titleCol)) {
			errorSink.append("Column `" + col.getColumnId() + "` requires a title");
			return false;
		} else {
			if ((replacing == null || OH.ne(replacing.getTitle(true), col.getTitle(true))) && findColumnByTitle(titleCol) != null) {
				errorSink.append("Column title already exists, rename the column titled: `").append(titleCol).append('`');
				return false;
			}
		}

		WebCellFormatter formatter = getFormatter(col);
		StringBuilder styleConsts = new StringBuilder();
		List<Tuple2<String, DerivedCellCalculator>> calcs = new ArrayList<Tuple2<String, DerivedCellCalculator>>();
		Set<String> usedConstVarsSink = new HashSet<String>();
		if (!processStyle("_fg", "Color", col.getColorFormula(), calcs, styleConsts, errorSink, usedConstVarsSink))
			return false;
		if (!processStyle("_bg", "Background Color", col.getBackgroundColorFormula(), calcs, styleConsts, errorSink, usedConstVarsSink))
			return false;
		if (!processStyle("_fm", "Style", col.getStyleFormula(), calcs, styleConsts, errorSink, usedConstVarsSink))
			return false;
		//		if (SH.is(col.getTargetFormula())) {
		//			if (getService().getScriptManager().parseAmiScript(this.getService().getDebugManager(), this, col.getTargetFormula(), getClass(), varTypes, errorSink,
		//					AmiDebugMessage.TGT_COLUMN_ACTION, titleCol, this.getAmiLayoutFullAlias()) == null)
		//				return null;
		//		}
		List<String> ids = new ArrayList<String>();
		int width = -1;
		if (replacing != null)
			width = getTable().getColumn(replacing.getColumnId()).getWidth();
		else if (col.getColumnWidth() != -1)
			width = col.getColumnWidth();
		int sblen = errorSink.length();

		//		String formula = col.getDisplayFormula().getFormula(true);
		DerivedCellCalculator calc = toFormula(col.getDisplayFormula(), errorSink, "Display", usedConstVarsSink);//toFormula(formula, errorSink, "Display", varTypes);
		//		String sortFormula = col.getSortFormula().getFormula(true);

		DerivedCellCalculator sortCalc = toFormula(col.getSortFormula(), errorSink, "Sort", usedConstVarsSink);
		col.getTooltipFormula().recompileFormula();
		if (sortCalc != null && !Comparable.class.isAssignableFrom(sortCalc.getReturnType()))
			errorSink.append("Sorting formula does not return sortable values");
		if (errorSink.length() > sblen)
			return false;
		//		if (calc == null) {
		//			errorSink.append("Formula required");
		//			return false;
		//		}
		if (calc == null) {
			switch (col.getType().get()) {
				case AmiWebUtils.CUSTOM_COL_TYPE_PROGRESS:
				case AmiWebUtils.CUSTOM_COL_TYPE_NUMERIC:
				case AmiWebUtils.CUSTOM_COL_TYPE_PRICE:
				case AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_SEC:
				case AmiWebUtils.CUSTOM_COL_TYPE_DATE:
				case AmiWebUtils.CUSTOM_COL_TYPE_TIME_SEC:
				case AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_MILLIS:
				case AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_MICROS:
				case AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_NANOS:
					calc = new DerivedCellCalculatorConst(0, null, Number.class);
					break;
				case AmiWebUtils.CUSTOM_COL_TYPE_CHECKBOX:
					calc = new DerivedCellCalculatorConst(0, null, Boolean.class);
					break;
				case AmiWebUtils.CUSTOM_COL_TYPE_MASKED:
				default:
					calc = new DerivedCellCalculatorConst(0, null, String.class);
			}
		}
		if (calc.getReturnType() == Object.class)
			calc = new DerivedCellCalculatorCast(0, String.class, calc, getScriptManager().getMethodFactory().getCaster(String.class));
		Class<?> type = calc.getReturnType();
		if (!verifyFormatType(this.getAmiLayoutFullAliasDotId(), col.getColumnId(), col.getType().get(), type, errorSink))
			return false;
		if (populateValues)
			this.clearRows();
		if (replacing != null) {
			if (replacing.getColumnId().equals(getTable().getTable().getExternalFilterIndexColumnId()))
				getTable().setExternalFilterIndex(null, null);
		}
		final String id = addDerivedColumn(calc, varTypes);
		ids.add(id);
		if (sortCalc != null) {
			final String sortId = addDerivedColumn(sortCalc, varTypes);
			ids.add(sortId);
		}
		if (calcs.size() > 0) {
			String styles[] = new String[calcs.size()];
			int i = 0;
			for (Tuple2<String, DerivedCellCalculator> e : calcs) {
				ids.add(addDerivedColumn(e.getB(), varTypes));
				styles[i++] = e.getA();
			}
			formatter = new WebCellStyleAdvancedWrapperFormatter(formatter, sortCalc != null, styleConsts.toString(), styles);
		} else if (styleConsts.length() > 0) {
			formatter = new WebCellStyleWrapperFormatter(formatter, sortCalc != null, styleConsts.toString());
		} else if (sortCalc != null) {
			formatter = new WebCellStyleWrapperFormatter(formatter, sortCalc != null, "");
		}

		final String colId = col.getColumnId();
		//		if (replacing != null) {
		//			if (OH.ne(colId, replacing.getColumnId()) && getCustomDisplayColumnIds().contains(colId)) {
		//				errorSink.append("Column Id already exists: ").append(colId);
		//			}
		//		} else if (col.getColumnId() != null) {
		//			colId = col.getColumnId();
		//			if (getCustomDisplayColumnIds().contains(colId)) {
		//				errorSink.append("Column Id already exists: ").append(colId);
		//				return;
		//			}
		//		} else
		//			colId = SH.getNextId(AmiWebUtils.toPrettyVarName(titleCol, "col_"), getCustomDisplayColumnIds());

		final BasicWebColumn webColumn;
		if (width != -1)
			webColumn = new BasicWebColumn(this.getTable(), colId, getService().cleanHtml(titleCol), width, formatter, AH.toArray(ids, String.class));
		else
			webColumn = new BasicWebColumn(this.getTable(), colId, getService().cleanHtml(titleCol), formatter, AH.toArray(ids, String.class));
		if (SH.is(col.getHeaderStyleExpression(true)))
			webColumn.setHeaderStyle(col.getHeaderStyleExpression(true));
		if (replacing != null) {
			this.getTable().updateColumn(webColumn, replacing.getColumnId());
		} else {
			// TODO:Review this is a fix for creating a visualization on FEED's  TODO: no data shows up when you test
			WebColumn existing = this.getTable().getColumnNoThrow(webColumn.getColumnId());
			if (existing == null)
				this.getTable().addHiddenColumn(webColumn);
			if (columnLocation != -1)
				this.getTable().showColumn(colId, columnLocation);
		}
		switch (col.getType().get()) {
			case AmiWebUtils.CUSTOM_COL_TYPE_SPARK_LINE:
				webColumn.setJsFormatterType("spark_line");
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_HTML:
				webColumn.setJsFormatterType("html");
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_CHECKBOX:
				webColumn.setJsFormatterType("checkbox");
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_MASKED:
				webColumn.setJsFormatterType("mask");
				break;
		}

		final BasicWebColumn column = (BasicWebColumn) this.getTable().getColumn(colId);
		if (width != -1)
			column.setWidth(width);
		else {
			width = formatter.getDefaultWidth();
			width = Math.max(getManager().getPortletMetrics().getWidth(titleCol), width);
			column.setWidth(width);
		}

		if (SH.is(col.getClickable(true)) || col.getType().get() == AmiWebUtils.CUSTOM_COL_TYPE_JSON) {
			column.setIsClickable(true);
		}
		if (SH.is(col.getOneClick())) {
			column.setIsOneClick(true);
		}
		column.setFixedWidth(col.isFixedWidth());
		column.setHasHover(SH.is(col.getTooltipFormula().getFormula(true)));
		//		Map<String, Object> columnConfig = col.getColumnConfig();
		//		columnConfig.put(AmiWebCustomColumn.CONFIG_ID, colId);
		//		columnConfig.put(AmiWebCustomColumn.CONFIG_FORMULA, formula);

		//		final AmiWebCustomColumn r = new AmiWebCustomColumn(this, columnConfig);//.setDataType(calc.getReturnType());

		onUserChangingColumnSchema();
		if (replacing != null) {
			customDisplayColumnIds.remove(replacing.getColumnId());
		}
		String cid = col.getColumnId();
		customDisplayColumnIds.put(cid, col);
		if (replacing == null)
			col.addToDomManager();
		else {
			getService().getDomObjectsManager().fireAriChanged(col, replacing.getAri());
		}
		updateCurrentTimeInUseFlag();
		if (replacing != null)
			removeUnusedVariableColumns();
		onWebColumnAdded(webColumn);
		return true;
	}
	private WebCellFormatter getFormatter(AmiWebCustomColumn col) {
		WebCellFormatter r = getFormatter(getService(), col.getType().get(), col.getPrecision().get());
		//TODO: THIS SEEMS SILLY TO ME
		if (col.getType().get() == AmiWebUtils.CUSTOM_COL_TYPE_PROGRESS) {
			if (SH.isnt(col.getBackgroundColorFormula())) {
				Map<String, Object> m = col.getColumnConfig();
				m.put(AmiWebCustomColumn.CONFIG_BACKGROUND_FORMULA, "\"#77EE77\"");
				System.out.println("SHOULD NOT HAPPEN");
				col = new AmiWebCustomColumn(this, m, false);
			}
		}
		return r;
	}
	static public boolean verifyFormatType(String panelId, String colName, Byte formatType, Class<?> type, StringBuilder errorSink) {
		String prefix = "Formula error in (column) <b>" + colName + "</b> of (panel) <b>" + panelId + "</b>:";
		switch (formatType) {
			case AmiWebUtils.CUSTOM_COL_TYPE_PROGRESS:
				if (!Double.class.isAssignableFrom(type) && !Float.class.isAssignableFrom(type)) {
					errorSink.append(prefix).append(" Formula must return <b>Float or Double</b>, not <b>").append(type.getSimpleName()).append("</b>");
					return false;
				}
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_NUMERIC:
			case AmiWebUtils.CUSTOM_COL_TYPE_PRICE:
				if (!Number.class.isAssignableFrom(type)) {
					errorSink.append(prefix).append(" Formula must return <b>Number</b>, not <b>").append(type.getSimpleName()).append("</b>");
					return false;
				}
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_SEC:
			case AmiWebUtils.CUSTOM_COL_TYPE_DATE:
			case AmiWebUtils.CUSTOM_COL_TYPE_TIME_SEC:
			case AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_MILLIS:
			case AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_MICROS:
			case AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_NANOS:
			case AmiWebUtils.CUSTOM_COL_TYPE_TIME:
			case AmiWebUtils.CUSTOM_COL_TYPE_TIME_MICROS:
			case AmiWebUtils.CUSTOM_COL_TYPE_TIME_MILLIS:
			case AmiWebUtils.CUSTOM_COL_TYPE_TIME_NANOS:
				if (!Number.class.isAssignableFrom(type) && !DateMillis.class.isAssignableFrom(type) && !DateMillis.class.isAssignableFrom(type)) {
					errorSink.append(prefix).append(" When using Date / Time formatting, formula must return <b>Long</b>, not <b>").append(type.getSimpleName()).append("</b>");
					return false;
				}
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_CHECKBOX:
				if (!Boolean.class.isAssignableFrom(type)) {
					errorSink.append(prefix).append(" For supplied formatting, formula must return <b>Boolean</b>, not <b>").append(type.getSimpleName()).append("</b>");
					return false;
				}
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_MASKED:
				if (!String.class.isAssignableFrom(type)) {
					errorSink.append(prefix).append(" For supplied formatting, formula must return <b>String</b>, not b>").append(type.getSimpleName()).append("</b>");
					return false;
				}
				break;
			default:
				break;
		}
		return true;
	}
	public static WebCellFormatter getFormatter(AmiWebService service, byte type, int precision) {
		WebCellFormatter formatter;
		AmiWebFormatterManager fm = service.getFormatterManager();
		switch (type) {
			case AmiWebUtils.CUSTOM_COL_TYPE_NUMERIC:
				formatter = new NumberWebCellFormatter(fm.getDecimalFormatter(precision));
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_PROGRESS:
				formatter = new PercentWebCellFormatter(fm.getPercentFormatter(precision)).setCssClass("white");
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_PRICE:
				formatter = new NumberWebCellFormatter(fm.getPriceFormatter(precision));
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_PERCENT:
				formatter = new NumberWebCellFormatter(fm.getPercentFormatter(precision));
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_HTML:
				formatter = fm.getHtmlWebCellFormatter();
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_JSON:
				formatter = fm.getDddFormatter();
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_TIME:
				formatter = fm.getTimeWebCellFormatter();
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME:
				formatter = fm.getDateTimeWebCellFormatter();
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_SEC:
				formatter = fm.getDateTimeSecsWebCellFormatter();
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_MILLIS:
				formatter = fm.getDateTimeMillisWebCellFormatter();
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_MICROS:
				formatter = fm.getDateTimeMicrosWebCellFormatter();
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_NANOS:
				formatter = fm.getDateTimeNanosWebCellFormatter();
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_DATE:
				formatter = fm.getDateWebCellFormatter();
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_TIME_SEC:
				formatter = fm.getTimeSecsWebCellFormatter();
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_TIME_MILLIS:
				formatter = fm.getTimeMillisWebCellFormatter();
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_TIME_MICROS:
				formatter = fm.getTimeMicrosWebCellFormatter();
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_TIME_NANOS:
				formatter = fm.getTimeNanosWebCellFormatter();
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_IMAGE:
				formatter = fm.getImageWebCellFormatter();
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_CHECKBOX:
				formatter = fm.getCheckboxWebCellFormatter();
				break;
			case AmiWebUtils.CUSTOM_COL_TYPE_MASKED:
				formatter = fm.getMaskedWebCellFormatter();
				break;
			default:
				formatter = fm.getBasicFormatter();
				break;
		}
		return formatter;
	}
	protected void onWebColumnAdded(BasicWebColumn webColumn) {
	}
	protected void removeCustomColumnById(String id) {
		onUserChangingColumnSchema();
		removeCustomDisplayColumn(id);
		getTable().removeColumn(id);
		removeUnusedVariableColumns();
	}
	protected void onUserChangingColumnSchema() {
	}
	public boolean processStyle(String styleDef, String description, AmiWebFormula colorFormula, List<Tuple2<String, DerivedCellCalculator>> calcs, StringBuilder styleConsts,
			StringBuilder errorSink, Set<String> usedConstVarsSink) {
		int sblen = errorSink.length();
		DerivedCellCalculator colorCalc = toFormula(colorFormula, errorSink, description, usedConstVarsSink);
		if (errorSink.length() > sblen)
			return false;
		else if (colorCalc == null)
			return true;
		else if (colorCalc.getReturnType() != String.class)
			return OH.FALSE(errorSink.append(description).append(" must evaluate to a string, not: ").append(colorCalc.getReturnType().getSimpleName()));
		else if (DerivedHelper.findFirst(colorCalc, DerivedCellCalculatorWithDependencies.class) == null)
			SH.appendWithDelim('|', styleDef, styleConsts).append('=').append((String) colorCalc.get(EmptyCalcFrameStack.INSTANCE));
		else
			calcs.add(new Tuple2<String, DerivedCellCalculator>(styleDef, colorCalc));
		return true;
	}

	public DerivedCellCalculator toFormula(AmiWebFormula formula, StringBuilder sb, String description, Set<String> usedConstVarsSink) {
		formula.recompileFormula();
		DerivedCellCalculator r = formula.getFormulaCalc();
		if (r == null)
			return null;
		return r;
	}

	public Set<String> getDependentVars(String columnId) {
		WebColumn col = getTable().getColumn(columnId);
		Set<Object> sink = new HashSet();
		for (int i : col.getTableColumnLocations()) {
			Column col2 = getTable().getTable().getColumnAt(i);
			if (col2 instanceof DerivedColumn) {
				DerivedHelper.getDependencyIds(((DerivedColumn) col2).getCalculator(), sink);
			} else
				sink.add(col2.getId());

		}
		return (Set) sink;
	}
	@Override
	public void onFrontendCalled(PortletManager manager, Map<String, String> attributes, HttpRequestAction action) {
		long now = getManager().getNow();
		if (now >= nextCheckTimeMs) {
			now = MH.roundBy(now, getCurrentTimeUpdateFrequencyMs(), MH.ROUND_DOWN);
			nextCheckTimeMs = now + getCurrentTimeUpdateFrequencyMs();
			processCurrentTime(now);
		}
	}

	private int currentTimeUpdateFrequencyMs = 1000;
	private Column[] variableColumnsarray;

	public int getCurrentTimeUpdateFrequencyMs() {
		return currentTimeUpdateFrequencyMs;
	}

	private void processCurrentTime(long now) {
		if (!isCurrentTimeInUse)
			return;
		for (Row row : CH.l(this.getTable().getTable().getRows()))
			row.putAt(this.currentTimeLocation, now);
		for (Row row : CH.l(this.getTable().getFilteredRows()))
			row.putAt(this.currentTimeLocation, now);
	}

	private void updateCurrentTimeInUseFlag() {
		boolean inUse = columnHasDependencyOnCurrentTime(this.rowTextColorColumnId) || columnHasDependencyOnCurrentTime(this.rowBackgroundColorColumnId);
		for (String columnId : this.getTable().getFilteredInColumns()) {
			for (String col : this.getTable().getColumn(columnId).getTableColumns()) {
				if (columnHasDependencyOnCurrentTime(col)) {
					inUse = true;
					break;
				}
			}
		}
		if (!inUse) {
			for (int i = 0, l = this.getTable().getVisibleColumnsCount(); i < l; i++) {
				WebColumn col = this.getTable().getVisibleColumn(i);
				for (String columnId : col.getTableColumns()) {
					if (columnHasDependencyOnCurrentTime(columnId)) {
						inUse = true;
						break;
					}
				}
			}
		}
		if (this.isCurrentTimeInUse == inUse)
			return;
		this.isCurrentTimeInUse = inUse;
		if (this.isCurrentTimeInUse)
			getManager().addPortletManagerListener(this);
		else
			getManager().removePortletManagerListener(this);
		this.nextCheckTimeMs = 0L;

	}
	public void setCurrentTimeUpdateFrequency(int ms) {
		this.currentTimeUpdateFrequencyMs = ms;
		this.nextCheckTimeMs = 0;
	}

	protected boolean columnHasDependencyOnCurrentTime(String columnId) {
		if (columnId == null)
			return false;
		if ("W".equals(columnId))
			return true;
		if (this.currentTimeLocation == -1)
			return false;
		Column tableColumn = getTable().getTable().getColumn(columnId);
		if (tableColumn instanceof DerivedColumn) {
			//			Set<Integer> sink = new HashSet<Integer>();
			DerivedCellCalculator calc = ((DerivedColumn) tableColumn).getCalculator();
			Set<Object> sink = DerivedHelper.getDependencyIds(calc);
			//			calc.getDependency(sink);
			if (sink.contains("W"))
				return true;
		}
		return false;
	}

	@Override
	public void onColumnContextMenu(WebTable table, WebColumn column, String action) {
		getService().getSecurityModel().assertPermitted(this, action,
				"_col_search,__arrange,__autosizeall,debug,_col_reset,override_precision_,override_type_,override_format_*,override_precision_*,download_*");
		if ("debug".equals(action)) {
			StringBuilder sb = new StringBuilder();
			debug(sb);
			LH.info(log, "DEBUG OUTPUT: " + SH.NEWLINE, sb);
		} else if ("addleft".equals(action)) {
			AmiWebAddObjectColumnFormPortlet addAmiObjectPortlet = newAddAmiObjectColumnFormPortlet(generateConfig(), this, table.getColumnPosition(column.getColumnId()), null);
			if (addAmiObjectPortlet != null) {
				getManager().showDialog(addAmiObjectPortlet.getTitle(), addAmiObjectPortlet);
			}
			//			AmiCenterGetAmiSchemaRequest req = nw(AmiCenterGetAmiSchemaRequest.class);
			//			getService().sendRequestToBackend(getPortletId(), req);
		} else if ("addright".equals(action)) {
			if (column == null)
				column = getTable().getVisibleColumn(getTable().getVisibleColumnsCount() - 1);
			int columnPosition = column == null ? 0 : table.getColumnPosition(column.getColumnId()) + 1;
			AmiWebAddObjectColumnFormPortlet addAmiObjectPortlet = newAddAmiObjectColumnFormPortlet(generateConfig(), this, columnPosition, null);
			if (addAmiObjectPortlet != null) {
				getManager().showDialog(addAmiObjectPortlet.getTitle(), addAmiObjectPortlet);
			}
			//			AmiCenterGetAmiSchemaRequest req = nw(AmiCenterGetAmiSchemaRequest.class);
			//			getService().sendRequestToBackend(getPortletId(), req);
		} else if ("copy".equals(action)) {
			AmiWebCustomColumn col = getCustomDisplayColumn(column.getColumnId());
			AmiWebAddObjectColumnFormPortlet addAmiObjectPortlet = newAddAmiObjectColumnFormPortlet(generateConfig(), this, table.getColumnPosition(column.getColumnId()), col);
			if (addAmiObjectPortlet != null) {
				addAmiObjectPortlet.setCopyColumn(true);
				getManager().showDialog(addAmiObjectPortlet.getTitle(), addAmiObjectPortlet);
			}
			//			AmiCenterGetAmiSchemaRequest req = nw(AmiCenterGetAmiSchemaRequest.class);
			//			getService().sendRequestToBackend(getPortletId(), req);
		} else if ("_col_search".equals(action)) {
			getManager().showDialog("Search Columns",
					new AmiWebSearchColumnsPortlet(generateConfig(), (FastWebColumns) table, (FastWebColumn) column).setStyle(this.tablePortlet.getFormStyle()),
					AmiWebSearchColumnsPortlet.DIALOG_W, AmiWebSearchColumnsPortlet.DIALOG_H).setStyle(this.tablePortlet.getDialogStyle());
		} else if ("edit".equals(action)) {

			AmiWebCustomColumn col = getCustomDisplayColumn(column.getColumnId());
			if (!checkCanRemoveCustomColumnById(col.getColumnId()))
				return;
			if (canEditColumn(col)) {
				if (getTable().getFilteredInColumns().contains(col.getColumnId())) {
					getManager().showAlert("You can not edit columns with active filters");
					return;
				}

				AmiWebAddObjectColumnFormPortlet addAmiObjectPortlet = newAddAmiObjectColumnFormPortlet(generateConfig(), this, table.getColumnPosition(column.getColumnId()), col);
				if (addAmiObjectPortlet != null) {
					getManager().showDialog(addAmiObjectPortlet.getTitle(), addAmiObjectPortlet);
				}
				//				AmiCenterGetAmiSchemaRequest req = nw(AmiCenterGetAmiSchemaRequest.class);
				//				getService().sendRequestToBackend(getPortletId(), req);
			}
		} else if ("delete".equals(action)) {
			AmiWebCustomColumn col = getCustomDisplayColumn(column.getColumnId());
			if (canEditColumn(col)) {
				if (!checkCanRemoveCustomColumnById(col.getColumnId()))
					return;
				ConfirmDialogPortlet p = new ConfirmDialogPortlet(generateConfig(), "Are you sure you want to delete the column:<BR><B>" + column.getColumnName(),
						ConfirmDialogPortlet.TYPE_OK_CUSTOM);
				p.addButton("delete_column", "Delete");
				p.addButton(ConfirmDialogPortlet.ID_NO, "Cancel");
				p.addDialogListener(this);
				p.setCorrelationData(column.getColumnId());
				getManager().showDialog("Delete Column", p);
			}
		} else if ("_col_reset".equals(action)) {
			this.applyUserPref(this.getDefaultPref());
		} else if (SH.startsWith(action, "override_precision_")) {
			int val = Integer.parseInt(SH.stripPrefix(action, "override_precision_", true));
			BasicWebColumn bc = (BasicWebColumn) column;
			AmiWebCustomColumn cc = getCustomDisplayColumn(column.getColumnId());
			if (val == -1) {
				if (cc.getPrecision().clearOverride()) {
					updateFormatter(bc, cc);
					getTable().refresh();
				}
			} else if (cc.getPrecision().setOverride(val)) {
				updateFormatter(bc, cc);
				getTable().refresh();
			}
		} else if (SH.startsWith(action, "override_format")) {
			int val = Integer.parseInt(SH.stripPrefix(action, "override_format_", true));
			BasicWebColumn bc = (BasicWebColumn) column;
			AmiWebCustomColumn cc = getCustomDisplayColumn(column.getColumnId());
			if (val == -1) {
				if (cc.getType().clearOverride()) {
					updateFormatter(bc, cc);
					getTable().refresh();
				}
			} else if (cc.getType().setOverride((byte) val)) {
				updateFormatter(bc, cc);
				getTable().refresh();
			}
		}
	}

	public boolean canEditColumn(AmiWebCustomColumn col) {
		return true;
	}
	public AmiWebAddObjectColumnFormPortlet newAddAmiObjectColumnFormPortlet(PortletConfig config, AmiWebAbstractTablePortlet portlet, int columnPosition, AmiWebCustomColumn col) {
		return new AmiWebAddObjectColumnFormPortlet(config, portlet, columnPosition, col);
	}
	public void debug(StringBuilder sb) {
		FastWebTable t = getTable();
		BasicTable t2 = new BasicTable();
		t2.setTitle("Visible Table");
		for (int i = 0; i < getTable().getVisibleColumnsCount(); i++)
			t2.addColumn(String.class, t.getVisibleColumn(i).getColumnId());
		for (int i = 0; i < getTable().getHiddenColumnsCount(); i++)
			t2.addColumn(String.class, t.getHiddenColumn(i).getColumnId());
		t2.getRows().addRow(new Object[t2.getColumnsCount()]);
		t2.getRows().addRow(new Object[t2.getColumnsCount()]);
		t2.getRows().addRow(new Object[t2.getColumnsCount()]);
		for (String i : t2.getColumnIds()) {
			WebColumn col = t.getColumn((String) i);
			t2.set(0, i, col.getColumnName());
			t2.set(1, i, Arrays.toString(col.getTableColumns()));
			t2.set(2, i, SH.stripSuffix(OH.getSimpleClassName(col.getCellFormatter()), "WebCellFormatter", false));
		}
		BasicTable t3 = new BasicTable(t.getTable());
		TableHelper.toString(t2, "", MH.clearBits(TableHelper.SHOW_ALL, TableHelper.SHOW_TYPES | TableHelper.SHOW_HEADER_BREAK), sb);
		if (t3.getColumnIds().contains(AmiConsts.TABLE_PARAM_DATA))
			for (Row row : t3.getRows())
				row.put(AmiConsts.TABLE_PARAM_DATA, "<debug>");
		//		if (t3.getColumnIds().contains(AmiConsts.TABLE_PARAM_PARAMS))
		//			for (Row row : t3.getRows())
		//				row.put(AmiConsts.TABLE_PARAM_PARAMS, m);
		sb.append(SH.NEWLINE);
		for (String s : AH.a(AmiConsts.TABLE_PARAM_W, AmiConsts.TABLE_PARAM_M, AmiConsts.TABLE_PARAM_DATA, AmiConsts.TABLE_PARAM_ID))
			if (t3.getColumnIds().contains(s))
				t3.removeColumn(s);
		TableHelper.toString(t3, "", TableHelper.SHOW_ALL, sb);
		String hc = SH.toString(Math.abs(t3.hashCode()), 62);
		getManager().showAlert("Table Hashcode: " + hc);
		sb.append(SH.NEWLINE);
		sb.append("Resulting Hashcode='").append(hc).append("'").append(SH.NEWLINE);

	}
	@Override
	public WebMenu createColumnMenu(WebTable table, WebMenu defaultMenu) {
		defaultMenu.setStyle(this.getService().getDesktop().getMenuStyle());
		if (isDebug)
			defaultMenu.add(new BasicWebMenuLink("debug", true, "debug"));
		if (inEditMode()) {
			defaultMenu.add(new BasicWebMenuLink("Add Column...", true, "addright").setCssStyle("className=ami_edit_menu"));
		}
		return defaultMenu;
	}
	@Override
	public WebMenu createColumnMenu(WebTable table, WebColumn column, WebMenu defaultMenu) {
		defaultMenu.setStyle(this.getService().getDesktop().getMenuStyle());
		defaultMenu.add(new BasicWebMenuLink("Search Columns <I>(Alt+r)</I>...", true, "_col_search"));
		defaultMenu.add(new BasicWebMenuLink("Reset All Columns To Defaults", true, "_col_reset"));
		AmiWebCustomColumn cc = getCustomDisplayColumn(column.getColumnId());
		if (cc != null) {
			switch (cc.getType().get()) {
				case AmiWebUtils.CUSTOM_COL_TYPE_NUMERIC:
				case AmiWebUtils.CUSTOM_COL_TYPE_PRICE:
				case AmiWebUtils.CUSTOM_COL_TYPE_PERCENT:
				case AmiWebUtils.CUSTOM_COL_TYPE_PROGRESS: {
					BasicWebMenu m = new BasicWebMenu("Decimals", true);
					int p;
					if (cc.getPrecision().isOverride()) {
						m.add(new BasicWebMenuLink("Default", true, "override_precision_-1").setCssStyle(""));
						p = cc.getPrecision().getOverride();
					} else {
						m.add(new BasicWebMenuLink("Default", true, "override_precision_-1").setCssStyle("className=ami_menu_checked"));
						p = Integer.MIN_VALUE;
					}
					for (int i = 0; i <= 8; i++)
						m.add(new BasicWebMenuLink(SH.toString(i), true, "override_precision_" + i).setCssStyle(i == p ? "className=ami_menu_checked" : ""));
					defaultMenu.add(m);
					break;
				}
				case AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_MICROS:
				case AmiWebUtils.CUSTOM_COL_TYPE_TIME_MICROS:
				case AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_NANOS:
				case AmiWebUtils.CUSTOM_COL_TYPE_TIME_NANOS:
				case AmiWebUtils.CUSTOM_COL_TYPE_DATE:
				case AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME:
				case AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_MILLIS:
				case AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_SEC:
				case AmiWebUtils.CUSTOM_COL_TYPE_TIME:
				case AmiWebUtils.CUSTOM_COL_TYPE_TIME_MILLIS:
				case AmiWebUtils.CUSTOM_COL_TYPE_TIME_SEC: {
					byte[] types;
					switch (cc.getType().get()) {
						case AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_MICROS:
						case AmiWebUtils.CUSTOM_COL_TYPE_TIME_MICROS:
							types = new byte[] { AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_MICROS, AmiWebUtils.CUSTOM_COL_TYPE_TIME_MICROS };
							break;
						case AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_NANOS:
						case AmiWebUtils.CUSTOM_COL_TYPE_TIME_NANOS:
							types = new byte[] { AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_NANOS, AmiWebUtils.CUSTOM_COL_TYPE_TIME_NANOS };
							break;
						default:
							types = new byte[] { AmiWebUtils.CUSTOM_COL_TYPE_DATE, AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME, AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_MILLIS,
									AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_SEC, AmiWebUtils.CUSTOM_COL_TYPE_TIME, AmiWebUtils.CUSTOM_COL_TYPE_TIME_MILLIS,
									AmiWebUtils.CUSTOM_COL_TYPE_TIME_SEC };
							break;

					}
					BasicWebMenu m = new BasicWebMenu("Format", true);
					int p;
					if (cc.getType().isOverride()) {
						m.add(new BasicWebMenuLink("Default", true, "override_format_-1").setCssStyle(""));
						p = cc.getType().getOverride();
					} else {
						m.add(new BasicWebMenuLink("Default", true, "override_format_-1").setCssStyle("className=ami_menu_checked"));
						p = Integer.MIN_VALUE;
					}
					for (byte i : types)
						m.add(new BasicWebMenuLink(AmiWebUtils.CUSTOM_COL_DESCRIPTIONS.get(i), true, "override_format_" + i)
								.setCssStyle(i == p ? "className=ami_menu_checked" : ""));
					defaultMenu.add(m);
					break;
				}
			}

		}
		if (inEditMode()) {
			defaultMenu.add(new BasicWebMenuDivider());
			boolean cust = getCustomDisplayColumnIds().contains(column.getColumnId());
			if (cust) {
				defaultMenu.add(new BasicWebMenuLink("Edit Column...", true, "edit").setCssStyle("className=ami_edit_menu"));
				defaultMenu.add(new BasicWebMenuLink("Copy Column...", true, "copy").setCssStyle("className=ami_edit_menu"));
			} else {
				defaultMenu.add(new BasicWebMenuLink("Edit Column...", false, "edit"));
				defaultMenu.add(new BasicWebMenuLink("Copy Column...", false, "copy"));
			}
			defaultMenu.add(new BasicWebMenuLink("Add Column to Right... ", true, "addright").setCssStyle("className=ami_edit_menu"));
			defaultMenu.add(new BasicWebMenuLink("Add Column to Left... ", true, "addleft").setCssStyle("className=ami_edit_menu"));
			defaultMenu.add(new BasicWebMenuDivider());
			if (cust)
				defaultMenu.add(new BasicWebMenuLink("Delete Column...", true, "delete").setCssStyle("className=ami_edit_menu"));
			else
				defaultMenu.add(new BasicWebMenuLink("Delete Column...", false, "delete"));
		}
		if (cc != null && cc.getDescription() != null) {
			String bgColor = getService().getDesktop().getHelpBgColor();
			String fgColor = getService().getDesktop().getHelpFontColor();
			if (bgColor == null)
				bgColor = "#ffffcc";
			if (fgColor == null)
				fgColor = "#000000";
			int maxWidth = 250;
			int fontSize = 13;
			defaultMenu.add(new BasicWebMenuDivider());
			StringBuilder sb = new StringBuilder();
			sb.append("<span style='display:block;max-width:").append(maxWidth).append("px;white-space:normal;")
					.append("word-wrap:break-word;padding-top:4px;padding-bottom:4px;'>");
			sb.append(cc.getDescription());
			sb.append("</span>");
			BasicWebMenuLink link = new BasicWebMenuLink(sb.toString(), false, null);
			link.setCssStyle("className=ami_help_menu|_bg=" + bgColor + "|_fg=" + fgColor + "|_fs=" + SH.toString(fontSize));
			defaultMenu.add(link);
		}
		return defaultMenu;
	}
	protected boolean inEditMode() {
		if (isReadonlyLayout())
			return false;
		AmiWebDesktopPortlet desktop = PortletHelper.findParentByType(this, AmiWebDesktopPortlet.class);
		return desktop == null || desktop.getInEditMode();
	}
	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		super.onButton(source, id);
		if (id.equals("delete_column")) {
			String column = (String) source.getCorrelationData();
			if (!checkCanRemoveCustomColumnById(column))
				return true;
			removeCustomColumnById(column);
			getTable().fireOnColumnsArranged();
		}
		return true;
	}

	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
		AmiWebCustomColumn column = getCustomDisplayColumn(col.getColumnId());
		if (column != null) {
			Object data = row.get(col.getTableColumns()[0]);
			if (column.getType().get() == AmiWebUtils.CUSTOM_COL_TYPE_JSON) {
				JsonTreePortlet jtp = new JsonTreePortlet(generateConfig());
				try {
					if (data != null) {
						jtp.setJson(SH.s(data));
						getManager().showDialog("Json", jtp);
					}
				} catch (StringFormatException e) {
					Throwable cause = e.getCause();
					if (cause instanceof ExpressionParserException)
						getManager().showAlert("Invalid Json " + cause.getMessage(), cause);
					else
						getManager().showAlert("Invalid Json " + e.getMessage(), e);
				}
			}
			if (this.callbacks.isImplemented("onCellClicked")) {
				HashMap<String, Object> row2 = new HashMap<String, Object>();
				for (String c : this.getTable().getColumnIds()) {
					WebColumn c3 = table.getColumn(c);
					if (!TABLE_PARAM_DATA.equals(c3.getColumnName())) {
						Object data2 = row.getAt(c3.getTableColumnLocations()[0]);
						row2.put(c3.getColumnName(), data2);
					}
				}
				Object result = this.callbacks.execute("onCellClicked", col.getColumnName(), data, row2);
				String val = AmiUtils.snn(result, "null");
				//Run 'ami:' commands
				if (SH.startsWith(val, URI_PREFIX_AMI_COMMAND, 0)) {
					AmiRelayRunAmiCommandRequest msg = processAmiCommandUrl(val, row.get(AmiConsts.TABLE_PARAM_DATA, CASTER_WEB_OBJECT), row);
					cmdRequestToRow.put(msg, row);
				}
				//Run 'ami_query:' queries
				else if (SH.startsWith(val, URI_PREFIX_AMI_QUERY, 0)) {
					String url = SH.stripPrefix(val, URI_PREFIX_AMI_QUERY, true);
					runAmiLink(url);
				}
				//Opens links in a new webpage.
				else if (SH.startsWithIgnoreCase(val, URI_PREFIX_AMI_HTTP, 0)) {
					int width = (int) (getManager().getRoot().getWidth() * .9);
					int height = (int) (getManager().getRoot().getHeight() * .9);
					new JsFunction(getManager().getPendingJs(), "window", "open").addParamQuoted(val).addParamQuoted("_blank")
							.addParamQuoted("width=" + width + ",height=" + height).end();
				}
			}
		}
		flagPendingAjax();
	}
	@Override
	final public boolean runAmiLink(String query) {
		//syntax:   ami_query:query1,query2,query3
		Set<String> titlesToFind = SH.splitToSet(",", query);
		for (AmiWebDmLink link : getDmLinksFromThisPortlet()) {
			if (SH.isnt(link.getTitle()))
				continue;
			Set<String> titles = SH.splitToSet("|", link.getTitle());
			if (!CH.containsAny(titles, titlesToFind))
				continue;
			AmiWebDmUtils.sendRequest(getService(), link);
		}
		return false;

	}
	@Override
	final public boolean runAmiLinkId(String query) {
		//syntax:   ami_query:query1,query2,query3
		//Queries any links with relationshipIds found in both the query and in the panel.  
		Set<String> idsToFind = SH.splitToSet(",", query);
		for (AmiWebDmLink link : getDmLinksFromThisPortlet()) {
			if (SH.isnt(link.getRelationshipId()))
				continue;
			Set<String> ids = SH.splitToSet("|", link.getRelationshipId());
			if (!CH.containsAny(ids, idsToFind))
				continue;
			AmiWebDmUtils.sendRequest(getService(), link);
		}
		return false;
	}

	private AmiRelayRunAmiCommandRequest processAmiCommandUrl(String origUrl, AmiWebObject amiWebEntity, Row row) {
		//syntax:   ami:appName/commandId?p1=v1&p2=v2&p3=v3
		String url = SH.stripPrefix(origUrl, URI_PREFIX_AMI_COMMAND, true);
		String appName = SH.beforeFirst(url, '/', null);
		if (appName == null) {
			getManager().showAlert("invalid ami url format (should be ami:appname/commandId?p1=v2&v2=v2...):  " + origUrl);
			return null;
		}
		url = SH.afterFirst(url, '/', url);
		String cmdId = SH.beforeFirst(url, '?');

		Collection<AmiWebCommandWrapper> cmds = getService().getSystemObjectsManager().getCommandsByAppNameCmdId(appName, cmdId);
		if (cmds.size() == 0) {
			getManager().showAlert("Command id / app name combination not found: cmd=" + cmdId + ",  appId=" + appName);
			return null;
		}

		if (cmds.size() > 1) {
			if (getService().getDebugManager().shouldDebug(AmiDebugMessage.SEVERITY_WARNING))
				getService().getDebugManager().addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_WARNING, AmiDebugMessage.TYPE_METHOD, this.getAri(), null,
						"Warning more than one app or command registered to the app or command", CH.m("app", appName, "cmdId", cmdId), null));
		}

		AmiWebCommandWrapper cmd = CH.first(cmds);
		String params = SH.afterFirst(url, '?', null);
		Map<String, Object> arguments = (Map) (params == null ? null : SH.splitToMap('&', '=', '\\', params));
		AmiRelayRunAmiCommandRequest msg = getService().sendCommandToBackEnd(this.getPortletId(), cmd, arguments, 120000, null,
				amiWebEntity == null ? null : new AmiWebObject[] { amiWebEntity }, row == null ? null : new Row[] { row });
		return msg;
	}
	@Override
	public boolean isRealtime() {
		return true;
	}

	public void clearRows() {
		this.tablePortlet.clearRows();
	}

	public DerivedTable getDerivedTable() {
		return derivedTable;
	}

	@Override
	public void populateConfigMenu(WebMenu headMenu) {
		headMenu.add(new BasicWebMenuLink("Add Column...", true, "addcol").setBackgroundImage(AmiWebConsts.ICON_ADD));
		BasicWebMenu rem = new BasicWebMenu("Remove Column", getCustomDisplayColumnIds().size() > 0).setBackgroundImage(AmiWebConsts.ICON_DELETE);
		BasicWebMenu edit = new BasicWebMenu("Edit Column", getCustomDisplayColumnIds().size() > 0);
		headMenu.add(edit);
		headMenu.add(rem);
		for (String colid : getCustomDisplayColumnIds()) {
			AmiWebCustomColumn i = getCustomDisplayColumn(colid);
			edit.add(new BasicWebMenuLink(i.getTitle(true), true, "editcol_" + i.getColumnId()));
			rem.add(new BasicWebMenuLink(i.getTitle(true), true, "remcol_" + i.getColumnId()));
		}
		edit.sort();
		rem.sort();
		headMenu.add(new BasicWebMenuLink("Arrange Columns...", true, "__arrange"));
	}
	public com.f1.base.CalcTypes getUsedVariables() {
		com.f1.utils.structs.table.stack.BasicCalcTypes r = new com.f1.utils.structs.table.stack.BasicCalcTypes();
		CalcTypes vars = AmiWebUtils.getAvailableVariables(getService(), this);
		for (String s : this.getCustomDisplayColumnIds())
			AmiWebUtils.getUsedVars(this.getCustomDisplayColumn(s).getFormulas(), vars, r);
		AmiWebUtils.getUsedVars(this.getFormulas(), vars, r);
		return r;
	}

	public Collection<WebColumn> getAvailableColumnVariables() {
		List<WebColumn> r = new ArrayList<WebColumn>(getTable().getColumnIds().size());
		for (String id : getTable().getColumnIds()) {
			if (!id.startsWith("!"))
				r.add(getTable().getColumn(id));
		}
		return r;
	}
	//	public String replaceVarsWithTitlesInFormula(String varprefix, String formula) {
	//		Node node = getService().getScriptManager().getParser(this.getAmiLayoutFullAlias()).getExpressionParser().parse(formula);
	//		replaceVarsWithTitlesInFormula(varprefix, node);
	//		return node.toString();
	//	}
	//	private void replaceVarsWithTitlesInFormula(String varprefix, Node node) {
	//		if (node instanceof VariableNode) {
	//			VariableNode varnode = (VariableNode) node;
	//			if (varnode.varname.startsWith(varprefix)) {
	//				String varname = SH.stripPrefix(varnode.varname, varprefix, true);
	//				WebColumn col = getTable().getColumn(varname);
	//				if (col == null)
	//					throw new ExpressionParserException(node.getPosition(), "Unknown top level variable: " + varnode.varname);
	//				varname = AmiWebUtils.toValidVarname(varprefix + col.getColumnName());
	//				varnode.varname = varname;
	//			}
	//		} else if (node instanceof OperationNode) {
	//			replaceVarsWithTitlesInFormula(varprefix, ((OperationNode) node).left);
	//			replaceVarsWithTitlesInFormula(varprefix, ((OperationNode) node).right);
	//		} else if (node instanceof MethodNode) {
	//			MethodNode mn = (MethodNode) node;
	//			for (Node param : mn.params)
	//				replaceVarsWithTitlesInFormula(varprefix, param);
	//		} else if (node instanceof ExpressionNode) {
	//			replaceVarsWithTitlesInFormula(varprefix, ((ExpressionNode) node).value);
	//		}
	//	}

	//	public String replaceTitlesWithVarsInFormula(String varprefix, String formula) {
	//		Node node = getService().getScriptManager().getParser(this.getAmiLayoutFullAlias()).getExpressionParser().parse(formula);
	//		replaceTitlesWithVarsInFormula(varprefix, node);
	//		return node.toString();
	//	}
	//	private void replaceTitlesWithVarsInFormula(String varprefix, Node node) {
	//		if (node instanceof VariableNode) {
	//			VariableNode varnode = (VariableNode) node;
	//			String varname = AmiWebUtils.fromValidVarname(node.getPosition(), varnode.varname);
	//			if (varname.startsWith(varprefix)) {
	//				varname = SH.stripPrefix(varname, varprefix, true);
	//				WebColumn col = findColumnByTitle(varname);
	//				if (col == null)
	//					throw new ExpressionParserException(node.getPosition(), "Unknown top level variable: " + varnode.varname);
	//
	//				varname = col.getColumnId();
	//				varnode.varname = varprefix + varname;
	//			}
	//		} else if (node instanceof OperationNode) {
	//			replaceTitlesWithVarsInFormula(varprefix, ((OperationNode) node).left);
	//			replaceTitlesWithVarsInFormula(varprefix, ((OperationNode) node).right);
	//		} else if (node instanceof MethodNode) {
	//			MethodNode mn = (MethodNode) node;
	//			for (Node param : mn.params)
	//				replaceTitlesWithVarsInFormula(varprefix, param);
	//		} else if (node instanceof ExpressionNode) {
	//			replaceTitlesWithVarsInFormula(varprefix, ((ExpressionNode) node).value);
	//		}
	//	}

	@Override
	public void setFontSize(Integer fontSize) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_FONT_SIZE, fontSize);
		int calcRowHeight = Math.max(fontSize + 5, this.rowHeight);
		this.tablePortlet.addOption(FastTablePortlet.OPTION_ROW_HEIGHT, calcRowHeight);
		this.tablePortlet.setFontSize(fontSize);
		super.setFontSize(fontSize);
	}

	@Override
	public void setFontFamily(String fontFamily) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_FONT_FAMILY, fontFamily);
		this.tablePortlet.setFontFamily(fontFamily);
		super.setFontFamily(fontFamily);
	}

	public Integer getScrollBarWidth() {
		return scrollBarWidth == null ? FastTablePortlet.DEFAULT_SCROLLBAR_WIDTH : scrollBarWidth;
	}

	public void setScrollBarWidth(Integer scrollBarWidth) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_SCROLL_BAR_WIDTH, scrollBarWidth);
		this.scrollBarWidth = scrollBarWidth;
	}

	public void setBgColor(String bgColor) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_BACKGROUND_STYLE, bgColor == null ? null : ("_bg=" + bgColor));
		this.bgColor = bgColor;
	}
	public String getBgColor() {
		return this.bgColor;
	}

	@Override
	public void clearAmiData() {
		this.cmdRequestToRow.clear();
		this.clearRows();
	}

	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		if (keyEvent.isJustAltKey() && "r".equals(keyEvent.getKey())) {
			getManager()
					.showDialog("Search Columns",
							new AmiWebSearchColumnsPortlet(generateConfig(), (FastWebColumns) this.tablePortlet.getTable(), (FastWebColumn) null)
									.setStyle(this.tablePortlet.getFormStyle()),
							AmiWebSearchColumnsPortlet.DIALOG_W, AmiWebSearchColumnsPortlet.DIALOG_H)
					.setStyle(this.tablePortlet.getDialogStyle());

		} else if (keyEvent.isJustShiftKey() && "Enter".equals(keyEvent.getKey())) {
			if (this.editMode != AmiWebAbstractTablePortlet.EDIT_OFF && !isEditingBlockedByOnBeforeEdit())
				onUserEditStart();
		}
		return super.onUserKeyEvent(keyEvent);
	}

	public void setRowHeight(int height) {
		this.rowHeight = height;
		int calcRowHeight = Math.max(getFontSize() + 5, this.rowHeight);
		this.tablePortlet.addOption(FastTablePortlet.OPTION_ROW_HEIGHT, calcRowHeight);
	}

	public Column[] getVariableColumns() {
		if (variableColumnsarray == null)
			variableColumnsarray = this.variableColumns.values().toArray(new Column[this.variableColumns.size()]);
		return this.variableColumnsarray;
	}
	public Column getVariableColumn(String name) {
		return variableColumns.get(name);
	}
	public int getVariableColumnsCount() {
		return variableColumns.size();
	}
	public Column removeVariableColumn(String name) {
		Column r = variableColumns.remove(name);
		if (r != null)
			this.variableColumnsarray = null;
		return r;
	}
	public void addVariableColumn(String name, Column column) {
		variableColumns.put(name, column);
		this.variableColumnsarray = null;
	}
	@Override
	public String getPanelType() {
		return "table";
	}

	public void setSelectColor(String selectColor) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_SELECTED_BG, selectColor);
		this.selectColor = selectColor;
	}
	public String getSelectColor() {
		return this.selectColor;
	}
	public void setActiveSelectColor(String activeSelectColor) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_ACTIVE_BG, activeSelectColor);
		this.activeSelectColor = activeSelectColor;
	}
	public String getActiveSelectColor() {
		return this.activeSelectColor;
	}

	public void setGreyBarColor(String color) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_GREY_BAR_COLOR, color);
		this.greyBarColor = color;
	}
	public String getGreyBarColor() {
		return this.greyBarColor;
	}

	public void setMenuFontColor(String color) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_MENU_FONT_COLOR, color);
		this.menuFontColor = color;
	}
	public String getMenuFontColor() {
		return this.menuFontColor;
	}

	public void setMenuBarColor(String color) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_MENU_BAR_COLOR, color);
		this.menuBarColor = color;
	}
	public String getMenuBarColor() {
		return this.menuBarColor;
	}

	public void setSearchBarColor(String color) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_SEARCH_BAR_COLOR, color);
		this.searchBarColor = color;
	}
	public String getSearchBarColor() {
		return this.searchBarColor;
	}

	public void setSearchFieldBorderColor(String color) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_SEARCH_FIELD_BORDER_COLOR, color);
		this.searchFieldBorderColor = color;
	}
	public String getSearchFieldBorderColor() {
		return this.searchFieldBorderColor;
	}

	public void setSearchBarFontColor(String color) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_SEARCH_BAR_FONT_COLOR, color);
		this.searchBarFontColor = color;
	}
	public String getSearchBarFontColor() {
		return this.searchBarFontColor;
	}

	public void setSearchButtonsColor(String searchButtonsColor) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_SEARCH_BUTTONS_COLOR, searchButtonsColor);
		this.searchButtonsColor = searchButtonsColor;
	}
	public String getSearchButtonsColor() {
		return searchButtonsColor;
	}

	public void setDefaultFontColor(String color) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_DEFAULT_FONT_COLOR, color);
		this.defaultFontColor = color;
	}
	public String getDefaultFontColor() {
		return (String) this.defaultFontColor;
	}
	public void setFilteredColumnBgColor(String color) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_FILTERED_COLUMN_BG_COLOR, color);
		this.filteredColumnBgColor = color;
	}
	public String getFilteredColumnBgColor() {
		return (String) this.filteredColumnBgColor;
	}
	public void setFilteredColumnFontColor(String color) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_FILTERED_COLUMN_FONT_COLOR, color);
		this.filteredColumnFontColor = color;
	}
	public String getFilteredColumnFontColor() {
		return (String) this.filteredColumnFontColor;
	}
	public int getRowHeight() {
		return this.rowHeight;
	}
	public String getTitleBarColor() {
		return titleBarColor;
	}
	public String getTitleBarFontColor() {
		return titleBarFontColor;
	}
	public void setTitleBarColor(String color) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_TITLE_BAR_COLOR, color);
		this.titleBarColor = color;
	}
	public void setTitleBarFontColor(String color) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_TITLE_BAR_FONT_COLOR, color);
		this.titleBarFontColor = color;
	}

	public String getScrollGripColor() {
		return this.gripColor;
	}
	public void setScrollGripColor(String color) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_GRIP_COLOR, color);
		this.gripColor = color;
	}
	public String getScrollTrackColor() {
		return this.trackColor;
	}
	public void setScrollTrackColor(String color) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_TRACK_COLOR, color);
		this.trackColor = color;
	}
	public void setScrollButtonColor(String color) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_TRACK_BUTTON_COLOR, color);
		this.trackButtonColor = color;
	}
	public String getScrollButtonColor() {
		return trackButtonColor;
	}

	public String getScrollIconsColor() {
		return scrollIconsColor;
	}
	public void setScrollIconsColor(String scrollIconsColor) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_SCROLL_ICONS_COLOR, scrollIconsColor);
		this.scrollIconsColor = scrollIconsColor;
	}
	@Override
	public String getScrollBorderColor() {
		return this.scrollBorderColor;
	}
	@Override
	public void setScrollBorderColor(String color) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_SCROLL_BORDER_COLOR, color);
		this.scrollBorderColor = color;
	}

	@Override
	public Integer getScrollBarRadius() {
		return this.scrollBarRadius;
	}

	public void setSearchBarDivColor(String color) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_SEARCH_BAR_DIV_COLOR, color);
		this.searchBarDivColor = color;
	}
	public String getSearchBarDivColor() {
		return this.searchBarDivColor;
	}
	public String getCellBorderColor() {
		return this.cellBorderColor;
	}

	public void setCellBorderColor(String color) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_CELL_BORDER_COLOR, color);
		this.cellBorderColor = color;
	}
	public void setVerticalAlign(String verticalAlign) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_VERTICAL_ALIGN, verticalAlign);
		this.verticalAlign = verticalAlign;
	}
	public void setCellBottomDivider(Integer size) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_CELL_BOTTOM_DIVIDER, size);
		this.cellBottomDivider = size;
	}
	public void setCellRightDivider(Integer size) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_CELL_RIGHT_DIVIDER, size);
		this.cellRightDivider = size;
	}
	public void setCellPaddingHorizontal(Integer size) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_CELL_PADDING_HORIZONTAL, size);
	}

	private boolean menuBarHidden;
	private boolean quickColumnFilterHidden;
	private int quickColumnFilterHeight;
	private String quickColumnFilterBgCl;
	private String quickColumnFilterFontCl;
	private int quickColumnFontSize;
	private String quickColumnFilterBdrCl;
	private boolean titleDividerHidden;
	private int headerFontSize;
	private int headerRowHeight;
	private Integer scrollBarRadius;

	private static final Caster<AmiWebObject> CASTER_WEB_OBJECT = OH.getCaster(AmiWebObject.class);
	private static final String FORMULA_EDIT_OPTIONS = "edit_options";

	private boolean callCommandOnSelected(AmiWebCommandWrapper cmd) {
		List<Row> rowList = getSelectedRowsForCommand(getTable());
		Row[] rows = rowList.toArray(new Row[rowList.size()]);
		AmiWebObject[] wos = new AmiWebObject[rows.length];

		com.f1.base.CalcTypes tableTypes = getColumnTypesForCommand(getTable());
		if (!cmd.isCallbackClick() || !cmd.matchesFilteredAndWhere(getPortletVarTypes(), getPortletVars(), tableTypes, rowList, this)) {

			getManager().showAlert("command no longer available for selection");
			return false;
		}

		for (int i = 0; i < rows.length; i++) {
			Row row = rows[i];
			wos[i] = row == null || row.getType(AmiConsts.TABLE_PARAM_DATA) == null ? null : row.get(AmiConsts.TABLE_PARAM_DATA, CASTER_WEB_OBJECT);
		}

		AmiWebUtils.showRunCommandDialog(this, getService(), cmd, wos, rows, this);
		return true;
	}
	public boolean callCommandOnSelected(String appName, String cmdId) {
		Collection<AmiWebCommandWrapper> cmds = getService().getSystemObjectsManager().getCommandsByAppNameCmdId(appName, cmdId);
		if (cmds.size() == 0) {
			getManager().showAlert("command no longer available");
			return false;
		}
		if (cmds.size() > 1) {
			if (getService().getDebugManager().shouldDebug(AmiDebugMessage.SEVERITY_WARNING))
				getService().getDebugManager().addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_WARNING, AmiDebugMessage.TYPE_CALLBACK, this.getAri(), "onSelected",
						"Warning more than one app or command registered to the app or command", CH.m("app", appName, "cmdId", cmdId), null));
		}
		AmiWebCommandWrapper cmd = CH.first(cmds);

		return callCommandOnSelected(cmd);
	}
	public void setMenuBarHidden(boolean isHidden) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_MENU_BAR_HIDDEN, isHidden);
		this.menuBarHidden = isHidden;
	}
	public Boolean getMenuHidden() {
		return this.menuBarHidden;
	}
	public void setQuickColumnFilterHidden(boolean isHidden) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_QUICK_COLUMN_FILTER_HIDDEN, isHidden);
		this.quickColumnFilterHidden = isHidden;
	}
	public Boolean getQuickColumnFilterHidden() {
		return this.quickColumnFilterHidden;
	}
	public void setQuickColumnFilterHeight(Integer quickColFilterHeight) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_QUICK_COLUMN_FILTER_HEIGHT, quickColFilterHeight);
		this.quickColumnFilterHeight = quickColFilterHeight;
	}
	public int getQuickColumnFilterHeight() {
		return this.quickColumnFilterHeight;
	}
	public void setQuickColumnFilterBgCl(String color) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_QUICK_COLUMN_FILTER_BG_CL, color);
		this.quickColumnFilterBgCl = color;
	}
	public String getQuickColumnFilterBgCl() {
		return this.quickColumnFilterBgCl;
	}
	public void setQuickColumnFilterFontCl(String color) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_QUICK_COLUMN_FILTER_FONT_CL, color);
		this.quickColumnFilterFontCl = color;
	}
	public String getQuickColumnFilterFontCl() {
		return this.quickColumnFilterFontCl;
	}
	public void setQuickColumnFilterFontSize(int fontSz) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_QUICK_COLUMN_FILTER_FONT_SZ, fontSz);
		this.quickColumnFontSize = fontSz;
	}
	public int getQuickColumnFilterFontSize() {
		return this.quickColumnFontSize;
	}
	public void setQuickColumnFilterBdrCl(String color) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_QUICK_COLUMN_FILTER_BDR_CL, color);
		this.quickColumnFilterBdrCl = color;
	}
	public String getQuickColumnFilterBdrCl() {
		return this.quickColumnFilterBdrCl;
	}
	public boolean isTitleDividerHidden() {
		return titleDividerHidden;
	}
	public void setTitleDividerHidden(boolean isHidden) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_TITLE_DIVIDER_HIDDEN, isHidden);
		this.titleDividerHidden = isHidden;

	}

	@Override
	public void setScrollBarRadius(Integer borderRadius) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_SCROLL_BAR_RADIUS, borderRadius);
		this.scrollBarRadius = borderRadius;
	}

	@Override
	public boolean putPortletVar(String key, Object value, Class type) {
		boolean b = super.putPortletVar(key, value, type);
		return b;

	}

	public void initTableStyle() {
		flagPendingAjax();
	}
	private void setHeaderFontSize(Integer headerFontSize) {
		this.headerFontSize = headerFontSize;
		if (this.headerRowHeight == 0) {
			return; // avoid calculation if header is not shown
		}
		int calcHeaderRowHeight = Math.max(this.headerRowHeight, this.headerFontSize + 5); // ensure bigger font size doesn't get clipped
		this.tablePortlet.addOption(FastTablePortlet.OPTION_HEADER_ROW_HEIGHT, calcHeaderRowHeight);
		this.tablePortlet.setHeaderFontSize(headerFontSize);
		this.tablePortlet.addOption(FastTablePortlet.OPTION_HEADER_FONT_SIZE, headerFontSize);
	}
	private void setHeaderRowHeight(Integer headerRowHeight) {
		this.headerRowHeight = headerRowHeight;
		if (this.headerRowHeight == 0) {// avoid still seeing the fonts if row height is 0
			this.tablePortlet.addOption(FastTablePortlet.OPTION_HEADER_FONT_SIZE, 0);
			this.tablePortlet.addOption(FastTablePortlet.OPTION_HEADER_ROW_HEIGHT, this.headerRowHeight);
		} else if (this.headerRowHeight > 0) {
			setHeaderFontSize(this.headerFontSize); // restore font size
		}
	}
	private boolean parseBooleanOption(Object style) {
		return Caster_Boolean.PRIMITIVE.cast(style);
	}

	private Integer parseIntegerOption(Object style) {
		return Caster_Integer.INSTANCE.cast(style);
	}

	public com.f1.base.CalcTypes getSpecialVariables() {
		return specialVariables;
	}
	protected Class<?> putSpecialVariable(String key, Class<?> type) {
		return specialVariables.putType(key, type);
	}

	private com.f1.utils.structs.table.stack.BasicCalcTypes specialVariables = new com.f1.utils.structs.table.stack.BasicCalcTypes();

	public Set<String> getCustomDisplayColumnIds() {
		return customDisplayColumnIds.keySet();
	}

	public AmiWebCustomColumn getCustomDisplayColumn(String id) {
		return customDisplayColumnIds.get(id);
	}

	public AmiWebCustomColumn removeCustomDisplayColumn(String id) {
		AmiWebCustomColumn r = customDisplayColumnIds.remove(id);
		r.removeFromDomManager();
		return r;
	}
	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		List<AmiWebDomObject> r = super.getChildDomObjects();
		r.addAll(this.customDisplayColumnIds.values());
		return r;
	}

	@Override
	public void updateAri() {
		super.updateAri();
		for (AmiWebCustomColumn i : this.customDisplayColumnIds.values())
			i.updateAri();
	}

	private HasherMap<String, AmiWebCustomColumn> customDisplayColumnIds = new HasherMap<String, AmiWebCustomColumn>();

	@Override
	public Table getSelectableRows(AmiWebDmLink link, byte type) {

		Table values = new BasicTable(getTable().getTable().getColumns());
		if (type != NONE) {
			if (type == SELECTED)
				for (Row row : getTable().getSelectedRows())
					values.getRows().addRow(row.getValuesCloned());
			else if (type == ALL)
				for (Row row : getTable().getRows())
					values.getRows().addRow(row.getValuesCloned());
		}
		//		if (values.getColumnsMap().containsKey(AmiConsts.TABLE_PARAM_PARAMS))
		//			values.removeColumn(AmiConsts.TABLE_PARAM_PARAMS);
		if (values.getColumnsMap().containsKey(AmiConsts.TABLE_PARAM_DATA))
			values.removeColumn(AmiConsts.TABLE_PARAM_DATA);
		return values;
	}

	@Override
	public boolean hasSelectedRows(AmiWebDmLink link) {
		return getTable().hasSelectedRows();
	}
	protected Table getValuesForLink(List<Row> sel) {
		if (sel == null)
			return getTable().getTable();
		Table values = new BasicTable(getTable().getTable().getColumns());
		for (Row row : sel)
			values.getRows().addRow(row.getValuesCloned());
		if (values.getColumnsMap().containsKey("!params"))
			values.removeColumn("!params");
		return values;
	}

	@Override
	public void onScroll(int viewTop, int viewPortHeight, long contentWidth, long contentHeight) {
		if (this.callbacks.isImplemented("onScroll")) {
			Object[] args = { viewTop, viewPortHeight, contentWidth, contentHeight };
			this.callbacks.executeArgs("onScroll", args);
		}
	}
	@Override
	final public void onSelectedChanged(FastWebTable fastWebTable) {
		handleOnSelectedChanged();
		this.callbacks.execute("onSelected");
	}
	protected void handleOnSelectedChanged() {
		onAmiRowsChanged();
		updateChildTables(getManager().getCallbackTarget() == this.tablePortlet);
	}
	@Override
	public void onNoSelectedChanged(FastWebTable fastWebTable) {
		//if nothing was selected and that has changed and there's a relationship that changes on no selected Changed, we need to re-eval relationships
		for (AmiWebDmLink i : getDmLinksFromThisPortlet())
			if (MH.anyBits(i.getOptions(), AmiWebDmLink.OPTION_EMPTYSEL_ALLSEL)) {
				handleOnSelectedChanged();
				return;
			}
	}

	protected boolean amiRowsChanged = false;
	private String rowBackgroundColorColumnId;
	private String rowTextColorColumnId;
	private byte editMode = EDIT_OFF;
	private boolean editViaDoubleClick = true;
	private String editContextMenuTitle = "Edit Row(s)";
	private String editCommandId = null;
	private String editAppName = null;
	//	private String editAmiScript = null;
	//	private String defaultWhereFilter;
	//	private String currentWhereFilter;
	//	private WhereClause currentWhereFilterCalc;
	private String downloadName;

	final protected void onAmiRowsChanged() {
		if (buildingSnapshot)
			return;
		flagPendingAjax();
		amiRowsChanged = true;
		return;
	}
	protected boolean updateChildTables(boolean isUserAction) {
		if (!amiRowsChanged)
			return false;
		amiRowsChanged = false;
		for (AmiWebDmLink link : getDmLinksFromThisPortlet()) {
			if (link.isRunOnSelect()) {
				AmiWebDmUtils.sendRequest(getService(), link);
			}
		}
		return true;

	}
	@Override
	public void clearUserSelection() {
		getTable().setSelectedRows(OH.EMPTY_INT_ARRAY);
	}
	protected com.f1.base.CalcTypes getColumnTypesForCommand(WebTable table) {
		return getTable().getTable().getColumnTypesMapping();
	}
	protected List<Row> getSelectedRowsForCommand(WebTable table) {
		return table.getSelectedRows();
	}
	@Override
	public void getUsedColors(Set<String> sink) {
		AmiWebUtils.getColors(this.bgColor, sink);
		AmiWebUtils.getColors(this.cellBorderColor, sink);
		AmiWebUtils.getColors(this.gripColor, sink);
		AmiWebUtils.getColors(this.trackButtonColor, sink);
		AmiWebUtils.getColors(this.trackColor, sink);
		AmiWebUtils.getColors(this.activeSelectColor, sink);
		AmiWebUtils.getColors(this.filteredColumnBgColor, sink);
		AmiWebUtils.getColors(this.filteredColumnFontColor, sink);
		AmiWebUtils.getColors(this.greyBarColor, sink);
		AmiWebUtils.getColors(this.menuBarColor, sink);
		AmiWebUtils.getColors(this.menuFontColor, sink);
		AmiWebUtils.getColors(this.searchBarColor, sink);
		AmiWebUtils.getColors(this.searchBarFontColor, sink);
		AmiWebUtils.getColors(this.selectColor, sink);
		AmiWebUtils.getColors(this.titleBarColor, sink);
		AmiWebUtils.getColors(this.titleBarFontColor, sink);
		for (AmiWebCustomColumn i : this.customDisplayColumnIds.values()) {
			AmiWebUtils.getColors(i.getBackgroundColorFormula().getFormula(false), sink);
			AmiWebUtils.getColors(i.getColorFormula().getFormula(false), sink);
		}
	}
	public FastTablePortlet getTablePortlet() {
		return this.tablePortlet;
	}

	public boolean isRollupEnabled() {
		return this.rollupEnabled;
	}
	public void setRollupEnabled(boolean rollupEnabled) {
		this.rollupEnabled = rollupEnabled;
	}
	@Override
	public void onCellMousedown(WebTable table, Row row, WebColumn col) {
	}
	@Override
	public String getStyleType() {
		return AmiWebStyleTypeImpl_Table.TYPE_TABLE;
	}
	@Override
	public void onStyleValueChanged(short key, Object old, Object value) {
		super.onStyleValueChanged(key, old, value);
		switch (key) {
			case AmiWebStyleConsts.CODE_BG_CL:
				setBgColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_GRAYBAR_CL:
				setGreyBarColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_FONT_CL:
				setDefaultFontColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_CELL_BDR_CL:
				setCellBorderColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_CELL_BTM_PX:
				setCellBottomDivider(parseIntegerOption(value));
				break;
			case AmiWebStyleConsts.CODE_CELL_RT_PX:
				setCellRightDivider(parseIntegerOption(value));
				break;
			case AmiWebStyleConsts.CODE_VT_ALIGN:
				setVerticalAlign((String) value);
				break;
			case AmiWebStyleConsts.CODE_HEADER_BG_CL:
				setMenuBarColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_HEADER_FONT_CL:
				setMenuFontColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_HEADER_HT:
				if (value != null)
					setHeaderRowHeight(parseIntegerOption(value));
				break;
			case AmiWebStyleConsts.CODE_HEADER_FONT_SZ:
				if (value != null)
					setHeaderFontSize(parseIntegerOption(value));
				break;
			case AmiWebStyleConsts.CODE_HEADER_DIV_HIDE:
				if (value != null)
					setTitleDividerHidden(parseBooleanOption(value));
				break;
			case AmiWebStyleConsts.CODE_COLUMN_FILTER_HIDE:
				if (value != null)
					setQuickColumnFilterHidden(parseBooleanOption(value));
				break;
			case AmiWebStyleConsts.CODE_COLUMN_FILTER_HT:
				if (value != null)
					setQuickColumnFilterHeight(parseIntegerOption(value));
				break;
			case AmiWebStyleConsts.CODE_COLUMN_FILTER_BG_CL:
				setQuickColumnFilterBgCl((String) value);
				break;
			case AmiWebStyleConsts.CODE_COLUMN_FILTER_FONT_CL:
				setQuickColumnFilterFontCl((String) value);
				break;
			case AmiWebStyleConsts.CODE_COLUMN_FILTER_FONT_SZ:
				setQuickColumnFilterFontSize(parseIntegerOption(value));
				break;
			case AmiWebStyleConsts.CODE_COLUMN_FILTER_BDR_CL:
				setQuickColumnFilterBdrCl((String) value);
				break;
			case AmiWebStyleConsts.CODE_SEL_CL:
				setSelectColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_ACT_CL:
				setActiveSelectColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_SEARCH_HIDE:
				if (value != null)
					setMenuBarHidden(parseBooleanOption(value));
				break;
			case AmiWebStyleConsts.CODE_SEARCH_BG_CL:
				setTitleBarColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_SEARCH_FONT_CL:
				setTitleBarFontColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_SEARCH_FLD_CL:
				setSearchBarColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_SEARCH_FLD_FONT_CL:
				setSearchBarFontColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_SEARCH_BTNS_CL:
				setSearchButtonsColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_SEARCH_BAR_DIV_CL:
				setSearchBarDivColor(Caster_String.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_SEARCH_FLD_BDR_CL:
				setSearchFieldBorderColor(Caster_String.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_FILT_BG_CL:
				setFilteredColumnBgColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_FILT_FONT_CL:
				setFilteredColumnFontColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_ROW_HT:
				if (value != null)
					setRowHeight(parseIntegerOption(value));
				break;
			case AmiWebStyleConsts.CODE_FONT_SZ:
				if (value != null)
					setFontSize(parseIntegerOption(value));
				break;
			case AmiWebStyleConsts.CODE_FONT_FAM:
				if (value != null)
					setFontFamily((String) value);
				break;
			case AmiWebStyleConsts.CODE_CELL_PAD_HT:
				setCellPaddingHorizontal(parseIntegerOption(value));
				break;
			case AmiWebStyleConsts.CODE_FLASH_UP_CL:
				this.tablePortlet.addOption(FastTablePortlet.OPTION_FLASH_UP_COLOR, Caster_String.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_FLASH_DN_CL:
				this.tablePortlet.addOption(FastTablePortlet.OPTION_FLASH_DN_COLOR, Caster_String.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_FLASH_MILLIS:
				this.tablePortlet.addOption(FastTablePortlet.OPTION_FLASH_MS, Caster_String.INSTANCE.cast(value));
				break;
		}
	}
	@Override
	public void onUserDblclick(FastWebColumns table, String action, Map<String, String> properties) {
		if ("callRelationship".equals(action)) {
			for (AmiWebDmLink link : getDmLinksFromThisPortlet()) {
				if (MH.allBits(link.getOptions(), AmiWebDmLink.OPTION_ON_USER_DBL_CLICK)) {
					AmiWebDmUtils.sendRequest(getService(), link);
				}
			}
			if (this.editViaDoubleClick) {
				int selectedCount = this.tablePortlet.getTable().getSelectedRows().size();
				if (!isEditingBlockedByOnBeforeEdit() && ((selectedCount > 0 && this.editMode == EDIT_MULTI) || (this.editMode == EDIT_SINGLE && selectedCount == 1)))
					onUserEditStart();
			}
		}
	}
	public boolean isEditingBlockedByOnBeforeEdit() {
		Object returnVal = this.callbacks.execute("onBeforeEdit");
		if (returnVal instanceof Boolean) {
			boolean editingAllowed = Caster_Boolean.PRIMITIVE.cast(returnVal);
			if (!editingAllowed)
				return true;
		}
		return false;
	}
	public void setRowBackgroundColor(String formula, boolean override) {
		this.rowBackgroundColor.setFormula(formula, override);
	}
	public void setRowTextColor(String formula, boolean override) {
		this.rowTextColor.setFormula(formula, override);
	}
	public AmiWebFormula getRowTextColor() {
		return this.rowTextColor;
	}
	public AmiWebFormula getRowBackgroundColor() {
		return this.rowBackgroundColor;
	}
	public boolean getShowCommandMenuItems() {
		return showCommandMenuItems;
	}
	public void setShowCommandMenuItems(boolean showCommandMenuItems) {
		this.showCommandMenuItems = showCommandMenuItems;
	}
	public void setScrollToBottomOnAppend(boolean value) {
		this.tablePortlet.getTable().setScrollToBottomOnAppend(value);
	}
	public boolean getScrollToBottomOnAppend() {
		return this.tablePortlet.getTable().getScrollToBottomOnAppend();
	}

	@Override
	public Map<String, Object> getUserPref() {
		Map<String, Object> r = super.getUserPref();
		{
			List<Map<String, Object>> columns = new ArrayList<Map<String, Object>>();
			for (int i = 0, l = this.getTable().getVisibleColumnsCount(); i < l; i++) {
				WebColumn visibleColumn = this.getTable().getVisibleColumn(i);
				AmiWebCustomColumn amicol = getCustomDisplayColumn(visibleColumn.getColumnId());
				visibleColumn.getWidth();
				LinkedHashMap<String, Object> m = CH.m(new LinkedHashMap<String, Object>(), "name", visibleColumn.getColumnName(), "width", visibleColumn.getWidth());
				if (amicol != null) {
					if (amicol.getPrecision().getOverride() != null)
						m.put("precision", amicol.getPrecision().getOverride());
					if (amicol.getType().getOverride() != null)
						m.put("format", AmiWebUtils.CUSTOM_COL_NAMES.getValue(amicol.getType().getOverride()));
				}
				columns.add(m);
			}
			r.put("VisibleColumns", columns);
		}

		if (!getTable().getSortedColumnIds().isEmpty()) {
			r.put("KeepSorting", getTable().isKeepSorting());
			List<Map<String, Object>> sorting = new ArrayList<Map<String, Object>>();
			Iterable<Entry<String, Boolean>> sortedColumns = this.getTable().getSortedColumns();
			for (Entry<String, Boolean> i : sortedColumns) {
				WebColumn col = this.getTable().getColumn(i.getKey());
				sorting.add(CH.m(new LinkedHashMap<String, Object>(), "name", col.getColumnName(), "type", i.getValue() ? "asc" : "des"));
			}
			r.put("Sorting", sorting);
		}
		Set<String> filteredInColumns = getTable().getFilteredInColumns();
		if (!filteredInColumns.isEmpty()) {
			Map<String, Map<String, Object>> filtersMap = new LinkedHashMap<String, Map<String, Object>>();
			for (String filteredInColumn : filteredInColumns) {
				WebColumn col = this.getTable().getColumn(filteredInColumn);
				WebTableFilteredInFilter f = getTable().getFiltererdIn(filteredInColumn);
				if (f != null) {
					Map<String, Object> m = new HashMap();
					Set<String> values = new HashSet<String>(f.getValues());
					if (f.getIncludeNull())
						values.add(null);
					m.put("values", values);
					if (!f.getKeep())
						m.put("hide", true);
					if (f.getIsPattern())
						m.put("pattern", true);
					if (f.getMin() != null)
						m.put("min", f.getMin());
					if (f.getMax() != null)
						m.put("max", f.getMax());
					m.put("maxInclusive", f.getMaxInclusive());
					m.put("minInclusive", f.getMinInclusive());
					filtersMap.put(col.getColumnName(), m);

				}
			}
			r.put("Filters", filtersMap);
		}
		r.put("PinnedColumnCount", this.getTable().getPinnedColumnsCount());
		return r;
	}
	@Override
	public void applyUserPref(Map<String, Object> values) {
		// these are the user preference columns
		List<Map> columns = (List<Map>) values.get("VisibleColumns");
		// these are the original, full columns
		FastWebTable fastColumns = this.getTable();
		fastColumns.setPinnedColumnsCount(0);
		Map<String, String> columnsByName = new HashMap<String, String>();
		// put orig column names, id to a map
		for (String col : fastColumns.getColumnIds()) {
			WebColumn col2 = fastColumns.getColumn(col);
			columnsByName.put(col2.getColumnName(), col2.getColumnId());
		}
		int firstColumn = 0;
		int cnt = fastColumns.getVisibleColumnsCount() + firstColumn;
		List<String> columnIds = new ArrayList<String>();
		// why not just loop through visibleColumns
		// add all visible column ids to a list
		for (int i = firstColumn; i < cnt; i++) {
			FastWebColumn col = fastColumns.getVisibleColumn(i);
			columnIds.add(col.getColumnId().toString());
		}
		for (int i = 0; i < columnIds.size(); i++)
			fastColumns.hideColumn(columnIds.get(i));
		int i = 0;
		// loop through userpref columns
		for (Map column : columns) {
			String name = CH.getOrThrow(Caster_String.INSTANCE, column, "name");
			int width = (int) CH.getOrThrow(Caster_Integer.INSTANCE, column, "width");
			Integer precision = CH.getOr(Caster_Integer.INSTANCE, column, "precision", null);
			String format = CH.getOr(Caster_String.INSTANCE, column, "format", null);
			// here col is id
			String col = columnsByName.get(name);
			if (col != null) {
				BasicWebColumn column2 = (BasicWebColumn) fastColumns.getColumn(col);
				column2.setWidth(width);
				AmiWebCustomColumn amicol = getCustomDisplayColumn(column2.getColumnId());
				if (amicol != null) {
					if (precision != null) {
						if (amicol.getPrecision().setOverride(precision))
							updateFormatter(column2, amicol);
					}
					if (format != null) {
						if (amicol.getType().setOverride(AmiWebUtils.CUSTOM_COL_NAMES.getKey(format)))
							updateFormatter(column2, amicol);
					}
				}
				fastColumns.showColumn(col, i++);
			}
		}

		Boolean keepSorting = CH.getOr(Caster_Boolean.INSTANCE, values, "KeepSorting", null);
		if (keepSorting != null) {
			List<Map> sorting = (List<Map>) values.get("Sorting");
			boolean first = true;
			for (Map sort : sorting) {
				String name = CH.getOrThrow(Caster_String.INSTANCE, sort, "name");
				String type = CH.getOrThrow(Caster_String.INSTANCE, sort, "type");
				String col = columnsByName.get(name);
				if (col != null) {
					this.getTable().sortRows(col, "asc".equals(type), keepSorting, !first);
					first = false;
				}
			}
		} else {
			this.getTable().clearSort();
		}
		Set<String> existingFilters = new HashSet<String>(this.getTable().getFilteredInColumns());
		Map<String, Map<String, Object>> filteredIn = (Map) values.get("Filters");
		if (CH.isntEmpty(filteredIn)) {
			for (Entry<String, Map<String, Object>> filteredInColumn : filteredIn.entrySet()) {
				String columnName = filteredInColumn.getKey();
				String col = columnsByName.get(columnName);
				if (col != null) {
					Map<String, Object> filter = filteredInColumn.getValue();
					Set<String> fvalues = new HashSet<String>((Collection) filter.get("values"));
					boolean includeNull = fvalues.contains(null);
					if (includeNull)
						fvalues.remove(null);
					String min = CH.getOr(Caster_String.INSTANCE, filter, "min", null);
					String max = CH.getOr(Caster_String.INSTANCE, filter, "max", null);
					String minInclusive = CH.getOr(Caster_String.INSTANCE, filter, "minInclusive", null);
					String maxInclusive = CH.getOr(Caster_String.INSTANCE, filter, "maxInclusive", null);
					boolean hide = Boolean.TRUE.equals(CH.getOr(Caster_Boolean.INSTANCE, filter, "hide", null));
					boolean pattern = Boolean.TRUE.equals(CH.getOr(Caster_Boolean.INSTANCE, filter, "pattern", null));
					getTable().setFilteredIn(col, fvalues, !hide, includeNull, pattern, min, minInclusive == "true", max, maxInclusive == "true");

					existingFilters.remove(col);
				}
			}
		}
		for (String col : existingFilters) {
			getTable().setFilteredIn(col, (Set) null);
		}

		fastColumns.setPinnedColumnsCount(CH.getOr(Caster_Integer.INSTANCE, values, "PinnedColumnCount", 0));
		fastColumns.fireOnColumnsArranged();
		super.applyUserPref(values);
	}
	private void updateFormatter(BasicWebColumn column2, AmiWebCustomColumn amicol) {
		WebCellFormatter cf = column2.getCellFormatter();
		WebCellFormatter newFormatter = getFormatter(amicol);
		if (cf instanceof WebCellStyleAdvancedWrapperFormatter) {
			WebCellStyleAdvancedWrapperFormatter cf2 = (WebCellStyleAdvancedWrapperFormatter) cf;
			cf2.setFormattter(newFormatter);
		} else if (cf instanceof WebCellStyleWrapperFormatter) {
			WebCellStyleWrapperFormatter cf2 = (WebCellStyleWrapperFormatter) cf;
			cf2.setFormattter(newFormatter);
		} else
			column2.setCellFormatter(newFormatter);
	}
	public void setEditMode(byte editMode) {
		this.editMode = editMode;
	}
	public byte getEditMode() {
		return this.editMode;
	}
	public boolean getEditViaDoubleClick() {
		return editViaDoubleClick;
	}
	public void setEditViaDoubleClick(boolean editViaDoubleClick) {
		this.editViaDoubleClick = editViaDoubleClick;
	}
	public String getEditContextMenuTitle() {
		return editContextMenuTitle;
	}
	public void setEditContextMenuTitle(String editContextMenuTitle) {
		this.editContextMenuTitle = editContextMenuTitle;
	}
	private void onUserEditStart() {
		if (!this.tablePortlet.isEditing())
			this.tablePortlet.startEdit(this.getTable().getSelectedRows(), this.customDisplayColumnIds, this);
	}
	public void startEditRows(List<Row> rows) {
		this.tablePortlet.startEdit(rows, this.customDisplayColumnIds, this);
	}
	@Override
	public Object getEditOptions(WebColumnEditConfig cfg, Row row) {
		return getScriptManager().parseAndExecuteAmiScript(cfg.getEditOptionFormula(), null, row, getService().getDebugManager(), AmiDebugMessage.TYPE_FORMULA, this,
				FORMULA_EDIT_OPTIONS);
	}
	protected abstract void onEditFinished();

	@Override
	public void onTableEditComplete(Table origTable, Table editedTable, FastTablePortlet owner, StringBuilder errorSink) {
		if (errorSink.length() > 0) {
			this.tablePortlet.finishEdit();
			onEditFinished();
			getManager().showAlert(errorSink.toString());
			return;
		}
		if (editedTable.getSize() == 0) {
			this.tablePortlet.finishEdit();
			onEditFinished();
			return;
		}
		this.callbacks.execute("onEdit", editedTable, origTable);
		if (SH.is(this.editAppName) && SH.is(this.editCommandId != null)) {
			Collection<AmiWebCommandWrapper> cmds = getService().getSystemObjectsManager().getCommandsByAppNameCmdId(this.editAppName, this.editCommandId);
			if (cmds.size() == 0) {
				getManager().showAlert("Could not process edit,backend application::cmdId not registered: " + this.editAppName + "::" + this.editCommandId);
				this.tablePortlet.finishEdit();
				onEditFinished();

			} else {
				if (cmds.size() > 1) {
					if (getService().getDebugManager().shouldDebug(AmiDebugMessage.SEVERITY_WARNING))
						getService().getDebugManager().addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_WARNING, AmiDebugMessage.TYPE_CALLBACK, this.getAri(), "onEdit",
								"Warning more than one app or command registered to the app or command", CH.m("app", editAppName, "cmdId", editCommandId), null));
				}
				AmiWebCommandWrapper cmd = CH.first(cmds);
				getService().sendCommandToBackEnd(cmd, editedTable, getService().getDefaultTimeoutMs(), this);
			}
		} else {
			this.tablePortlet.finishEdit();
			onEditFinished();
		}
	}

	@Override
	public void onTableEditAbort(FastTablePortlet owner) {
		onEditFinished();
	}
	public String getEditCommandId() {
		return editCommandId;
	}
	public void setEditCommandId(String editCommandId) {
		this.editCommandId = editCommandId;
	}
	public String getEditAppName() {
		return editAppName;
	}
	public void setEditAppName(String editAppName) {
		this.editAppName = editAppName;
	}

	//	public String getEditAmiScript() {
	//		return editAmiScript;
	//	}
	//	public void setEditAmiScript(String editAmiScript) {
	//		this.editAmiScript = editAmiScript;
	//	}
	public String getDefaultWhereFilter() {
		return this.where.getDefaultWhereFilter();
	}
	public String getCurrentRuntimeFilter() {
		return this.where.getCurrentRuntimeFilter();
	}

	public WhereClause compileWhereFilter(String v, StringBuilder errorSink) {
		return this.where.compileWhereFilter(v, errorSink, getUnderlyingVarTypes());
	}

	private AmiWebWhereClause where;

	//	public void setDefaultWhereFilter(String v) {
	//		this.where.setDefaultWhereFilter(v);
	//	}
	public void setCurrentRuntimeFilter(String v, boolean t) {
		this.where.setCurrentRuntimeFilter(v, t);
	}

	protected void onWhereFormulaChanged() {
	}

	protected boolean hasWhereFilter() {
		return where.hasWhereFilter();
	}
	public boolean meetsWhereFilter(CalcFrame values) {
		return where.meetsWhereFilter(values, getStackFrame());
	}
	abstract public com.f1.base.CalcTypes getUnderlyingVarTypes();

	public boolean resetWhere() {
		boolean r = where.resetWhere();
		if (r)
			onWhereFormulaChanged();
		return r;
	}

	@Override
	public String getAmiScriptClassName() {
		return "TablePanel";
	}
	@Override
	public void onColumnsArranged(WebTable table) {
		updateCurrentTimeInUseFlag();
		this.callbacks.execute("onColumnsArranged");
	}
	@Override
	public void onColumnsSized(WebTable table) {
		this.callbacks.execute("onColumnsSized");
	}
	@Override
	public void onPageLoading(PortletManager basicPortletManager, Map<String, String> attributes, HttpRequestResponse action) {
	}

	@Override
	public void onInit(PortletManager manager, Map<String, Object> configuration, String rootId) {
	}

	@Override
	public void onPageRefreshed(PortletManager basicPortletManager) {
	}

	@Override
	public void onMetadataChanged(PortletManager basicPortletManager) {
	}

	@Override
	public void onPortletManagerClosed() {
	}

	@Override
	public void onBackendCalled(PortletManager manager, Action action) {
	}
	@Override
	public void onFilterChanging(WebTable fastWebTable) {
		if (isInitDone())
			this.callbacks.execute("onFilterChanging");
	}

	private BasicQuickFilterAutocompleteManager innerQuickFilter = new BasicQuickFilterAutocompleteManager();
	private QuickFilterCallbackHandler quickFilterCallbackHandler;
	private Boolean scrollBarHideArrows = false;
	private String scrollBarCornerColor;

	@Override
	public void onQuickFilterUserAction(FastTablePortlet t, String columnId, String val, int limit) {
		AmiWebCustomColumn column = getCustomDisplayColumn(columnId);
		if (this.getAmiScriptCallbacks().isImplemented(CALLBACK_DEF_ONQUICKFILTERTYPING.getMethodName())) {
			AmiWebScriptRunner rr = this.getAmiScriptCallbacks().executeRunner(CALLBACK_DEF_ONQUICKFILTERTYPING.getMethodName(), column, val, limit);
			rr.runStep();
			if (rr.getState() == AmiWebScriptRunner.STATE_REQUEST_SENT) {
				if (this.quickFilterCallbackHandler != null)
					this.quickFilterCallbackHandler.runner.halt();
				this.quickFilterCallbackHandler = new QuickFilterCallbackHandler(rr, columnId, val, limit);
				rr.addListener(this.quickFilterCallbackHandler);
				return;
			} else {
				Object r = rr.getReturnValue();
				if (r instanceof List) {
					t.callSetAutocomplete(columnId, toMap((List) r, limit));
					return;
				}
			}
		}
		this.innerQuickFilter.onQuickFilterUserAction(t, columnId, val, limit);
	}

	private class QuickFilterCallbackHandler implements AmiWebScriptRunnerListener {

		final private String columnId;
		final private int limit;
		private AmiWebScriptRunner runner;

		public QuickFilterCallbackHandler(AmiWebScriptRunner runner, String columnId, String val, int limit) {
			this.runner = runner;
			this.columnId = columnId;
			this.limit = limit;
		}

		@Override
		public void onScriptRunStateChanged(AmiWebScriptRunner amiWebScriptRunner, byte oldState, byte state) {
			if (state == AmiWebScriptRunner.STATE_DONE) {
				Object r = amiWebScriptRunner.getReturnValue();
				if (r instanceof List) {
					Map<String, String> m = toMap((List) r, limit);
					getTablePortlet().callSetAutocomplete(columnId, m);
				}
				quickFilterCallbackHandler = null;
			}
		}
	}

	static Map<String, String> toMap(List l, int limit) {
		Map<String, String> r = new HashMap<String, String>();
		for (Object o : (List) l) {
			String s = AmiUtils.s(o);
			if (s != null)
				r.put(s, s);
			if (r.size() == limit)
				break;
		}
		return r;
	}

	@Override
	public void onFormulaChanged(AmiWebFormula formula, DerivedCellCalculator old, DerivedCellCalculator nuw) {
		if (this.rowBackgroundColor == formula) {
			DerivedCellCalculator calc = formula.getFormulaCalc();
			this.rowBackgroundColorColumnId = calc == null ? null : addDerivedColumn(calc, getFormulaVarTypes(formula));
			this.getTable().setRowBgColorFormula(this.rowBackgroundColorColumnId);
			removeUnusedVariableColumns();
		} else if (this.rowTextColor == formula) {
			DerivedCellCalculator calc = formula.getFormulaCalc();
			this.rowTextColorColumnId = calc == null ? null : addDerivedColumn(calc, getFormulaVarTypes(formula));
			this.getTable().setRowTxColorFormula(this.rowTextColorColumnId);
			removeUnusedVariableColumns();
		}
	}

	@Override
	public void removeFromDomManager() {
		for (AmiWebCustomColumn i : this.customDisplayColumnIds.values())
			i.removeFromDomManager();
		super.removeFromDomManager();
	}

	@Override
	public void setScrollBarHideArrows(Boolean hide) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_SCROLL_BAR_HIDE_ARROWS, hide);
		this.scrollBarHideArrows = hide;
	}

	@Override
	public void setScrollBarCornerColor(String color) {
		this.tablePortlet.addOption(FastTablePortlet.OPTION_SCROLL_BAR_CORNER_COLOR, color);
		this.scrollBarCornerColor = color;
	}
	@Override
	public Boolean getScrollBarHideArrows() {
		return this.scrollBarHideArrows;
	}
	@Override
	public String getScrollBarCornerColor() {
		return this.scrollBarCornerColor;
	}

	@Override
	public void close() {
		super.close();
	}
	public void createCustomCol(String colId) {
		Column c = this.getTable().getTable().getColumn(colId);
		Map<String, Object> columnConfig = new HashMap<String, Object>();
		CH.putNoNull(columnConfig, AmiWebCustomColumn.CONFIG_ID, c.getId());
		CH.putNoNull(columnConfig, AmiWebCustomColumn.CONFIG_TITLE, c.getId().toString());
		CH.putNoNull(columnConfig, AmiWebCustomColumn.CONFIG_TYPE, 3);
		CH.putNoNull(columnConfig, AmiWebCustomColumn.CONFIG_FORMULA, c.getId());
		AmiWebCustomColumn col = new AmiWebCustomColumn(this, columnConfig, false);
		col.addToDomManager(); // everything in the GUI is a dom object, transient is no different - Rob
		col.setTransient(true);
		customDisplayColumnIds.put(colId, col);
	}

	@Override
	public String createTooltip(WebColumn col, Row row) {
		if (col == null)
			return null;
		final AmiWebCustomColumn customCol = this.customDisplayColumnIds.get(col.getColumnId());
		if (customCol == null)
			return null;
		final AmiWebFormula tooltip = customCol.getTooltipFormula();
		if (tooltip == null)
			return null;
		try {
			final DerivedCellCalculator calc = tooltip.getFormulaCalc();
			if (calc == null)
				return null;
			Object o = calc.get(getStackFrame().reset(row));
			return SH.toString(o);
		} catch (Exception e) {
			LH.severe(log, "Failed to create tooltip for: " + col.getColumnName() + ". Exception: ", e);
			return null;
		}
	}

	@Override
	public void onEditModeChanged(AmiWebDesktopPortlet amiWebDesktopPortlet, boolean inEditMode) {
		this.getTable().flagColumnFixedWidth();
	}

	public void setFixedColumn(String colId, boolean fix) {
		this.getTable().flagColumnFixedWidth();
		AmiWebCustomColumn col = getCustomDisplayColumn(colId);
		col.setIsFixedWidth(fix);
		((BasicWebColumn) (col.getWebColumn())).setFixedWidth(fix);
	}

	@Override
	public boolean isColumnTooltipSet(WebColumn col) {
		if (col == null)
			return false;
		final AmiWebCustomColumn customCol = this.customDisplayColumnIds.get(col.getColumnId());
		if (customCol == null)
			return false;
		final AmiWebFormula tooltip = customCol.getTooltipFormula();
		if (tooltip == null)
			return false;
		final DerivedCellCalculator calc = tooltip.getFormulaCalc();
		if (calc == null)
			return false;
		return true;
	}

}
