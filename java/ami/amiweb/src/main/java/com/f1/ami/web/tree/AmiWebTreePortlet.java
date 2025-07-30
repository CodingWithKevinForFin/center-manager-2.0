package com.f1.ami.web.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.web.AmiWebAbstractPortlet;
import com.f1.ami.web.AmiWebAbstractTablePortlet;
import com.f1.ami.web.AmiWebConsts;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebFormatterManager;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebFormulas;
import com.f1.ami.web.AmiWebProperties;
import com.f1.ami.web.AmiWebSearchColumnsPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebStyledScrollbarPortlet;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.AmiWebWhereClause;
import com.f1.ami.web.AmiWebWhereClause.WhereClause;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.portlets.AmiWebDmUtils;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Treegrid;
import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.base.IntIterator;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.table.WebCellFormatter;
import com.f1.suite.web.table.WebTreeListener;
import com.f1.suite.web.tree.WebTreeColumnContextMenuListener;
import com.f1.suite.web.tree.WebTreeColumnMenuFactory;
import com.f1.suite.web.tree.WebTreeContextMenuFactory;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeManager;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.WebTreeNodeListener;
import com.f1.suite.web.tree.impl.ArrangeColumnsPortlet;
import com.f1.suite.web.tree.impl.BasicWebTreeManager;
import com.f1.suite.web.tree.impl.FastWebColumn;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.suite.web.tree.impl.WebTreeFilteredInFilter;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Byte;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.sql.aggs.AggCalculator;
import com.f1.utils.sql.aggs.AggregateFactory;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.IndexedList;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.BasicCalcFrame;
import com.f1.utils.structs.table.stack.BasicCalcTypes;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.CalcFrameTuple2;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;

public abstract class AmiWebTreePortlet extends AmiWebAbstractPortlet implements WebTreeNodeListener, WebTreeContextMenuListener, WebTreeContextMenuFactory,
		WebTreeColumnMenuFactory, WebTreeColumnContextMenuListener, AmiWebStyledScrollbarPortlet, WebTreeListener {
	// can use below when we introduce auto size: if header row height is not in default value, then changing font size shouldn't update header height
	//	private static final int DEFAULT_HEADER_ROW_HEIGHT = 18;
	public static final ParamsDefinition CALLBACK_DEF_ONCOLUMNSSIZED = new ParamsDefinition("onColumnsSized", Object.class, "");
	public static final ParamsDefinition CALLBACK_DEF_ONCOLUMNSARRANGED = new ParamsDefinition("onColumnsArranged", Object.class, "");
	public static final ParamsDefinition CALLBACK_DEF_ONSELECTED = new ParamsDefinition("onSelected", Object.class, "");
	public static final ParamsDefinition CALLBACK_DEF_ONFILTERCHANGING = new ParamsDefinition("onFilterChanging", Object.class, "");

	static {
		CALLBACK_DEF_ONFILTERCHANGING.addDesc("Called when the filter is changing, this includes the search bar and column level filters");
		CALLBACK_DEF_ONCOLUMNSSIZED.addDesc("Called when the size (width) of a column has changed");
		CALLBACK_DEF_ONCOLUMNSARRANGED.addDesc("Called when the arrangement of columns has changed, this includes hiding/showing columns");
		CALLBACK_DEF_ONSELECTED.addDesc("Called when the selection status of a row (or rows) has changed");
	}

	private static final Logger log = LH.get();
	private static final Comparator<Map> LOCATION_COMPARATOR = new Comparator<Map>() {

		@Override
		public int compare(Map o1, Map o2) {
			int loc1 = (int) CH.getOr(Caster_Integer.INSTANCE, o1, "location", -1);
			int loc2 = (int) CH.getOr(Caster_Integer.INSTANCE, o2, "location", -1);
			return OH.compare(loc1, loc2);
		}
	};

	protected final IndexedList<String, AmiWebTreeGroupBy> groupbyFormulas = new BasicIndexedList<String, AmiWebTreeGroupBy>();
	protected FastWebTree tree;
	protected WebTreeManager treeManager;
	protected String rootName;
	private FastTreePortlet treePortlet;
	private String bgColor;
	private String grayBarColor;
	private String fontColor;
	private String cellBorderColor;
	private Integer rowHeight;
	private String fontFamily;

	private boolean hideHeaderDivider;
	private boolean hideHeaderBar;
	private String columnHeaderBgColor;
	private String columnHeaderFontColor;
	private String searchBarBgColor;
	private String searchBarFieldBgColor;
	private String searchBarFieldFgColor;
	private String searchFieldBorderColor;
	private String searchButtonsColor;
	private String filteredHeaderFontColor;
	private String filteredHeaderBgColor;
	private boolean searchBarHidden;
	private Integer scrollBarWidth;
	private String gripColor;
	private String trackColor;
	private String scrollButtonColor;
	private String scrollIconsColor;
	private String scrollBorderColor;
	private String selectColor;
	private String activeSelectColor;
	protected boolean amiSelectionChanged;
	protected Map<String, AmiWebTreeColumn> columnsByAmiId = new HashMap<String, AmiWebTreeColumn>();
	protected IntKeyMap<AmiWebTreeColumn> columnsById = new IntKeyMap<AmiWebTreeColumn>();
	protected final AggregateFactory aggregateFactory;
	private final AmiWebWhereClause where = new AmiWebWhereClause(this);
	private final BasicCalcFrame tmpValues = new BasicCalcFrame(VAR_TYPES);
	private final Map.Entry<String, Object> tmpValue_CHECKED = tmpValues.getOrCreateEntry(VAR_CHECKED);
	private final Map.Entry<String, Object> tmpValue_EXPANDED = tmpValues.getOrCreateEntry(VAR_EXPANDED);
	private final Map.Entry<String, Object> tmpValue_SIZE = tmpValues.getOrCreateEntry(VAR_SIZE);
	private final Map.Entry<String, Object> tmpValue_RSIZE = tmpValues.getOrCreateEntry(VAR_RSIZE);
	private final Map.Entry<String, Object> tmpValue_LEAVES = tmpValues.getOrCreateEntry(VAR_LEAVES);
	private final Map.Entry<String, Object> tmpValue_DEPTH = tmpValues.getOrCreateEntry(VAR_DEPTH);
	//	private final MapsBackedMap<String, Object> tmpValuesMap = new MapsBackedMap<String, Object>(false, tmpValues, Collections.EMPTY_MAP);
	private final CalcFrameTuple2 tmpValuesMap = new CalcFrameTuple2(tmpValues, EmptyCalcFrame.INSTANCE);
	private final AmiWebTreePortletFormatter treePortletFormatter;
	private Integer scrollBarRadius;
	private Boolean scrollBarHideArrows = false;

	//Ref count
	private final Map<AggCalculator, Integer> aggRefs = new HashMap<AggCalculator, Integer>();

	public AmiWebTreePortlet(PortletConfig config) {
		super(config);
		this.treePortlet = new FastTreePortlet(generateConfig());
		this.treePortlet.setMaxShowValuesForFilterDialog(
				getManager().getTools().getOptional(AmiWebProperties.PROPERTY_AMI_WEB_FILTER_DIALOG_MAX_OPTIONS, this.treePortlet.getMaxShowValuesForFilterDialog()));
		this.tree = this.treePortlet.getTree();
		this.tree.addListener(this);
		this.treeManager = tree.getTreeManager();
		this.tree.setRootLevelVisible(false);
		this.treeManager.addListener(this);
		this.tree.addMenuContextListener(this);
		this.tree.setContextMenuFactory(this);
		this.treePortlet.addOption(FastTreePortlet.OPTION_BACKGROUND_STYLE, "_bg=" + this.bgColor);
		this.treePortlet.addOption(FastTreePortlet.OPTION_FONT_STYLE, "_fg=" + this.getFontColor());
		this.treePortlet.addOption(FastTreePortlet.OPTION_FONT_FAMILY, "_fm=" + this.getFontFamily());
		this.treePortlet.addOption(FastTreePortlet.OPTION_HIDE_CHECKBOXES, true);
		this.tree.setRowHeight(18);
		this.tree.setHeaderRowHeight(18);
		this.tree.setLeftPaddingPx(4);
		this.tree.setTopPaddingPx(4);
		this.aggregateFactory = this.getScriptManager().createAggregateFactory();
		this.treePortletFormatter = new AmiWebTreePortletFormatter(this);
		this.tree.setFormatter(this.treePortletFormatter);
		this.tree.getTreeColumn().setFormatter(this.treePortletFormatter);
		this.tree.setRowFormatter(this.treePortletFormatter);
		this.tree.setColumnMenuFactory(this);
		this.tree.addColumnMenuListener(this);
		setChild(this.treePortlet);
		this.getStylePeer().initStyle();
		this.treePortlet.setDialogStyle(getService().getUserDialogStyleManager());
		this.treePortlet.setFormStyle(getService().getUserFormStyleManager());
	}

	@Override
	public void clearAmiData() {
		treeManager.clear();
		if (this.rootName != null) {
			treeManager.getRoot().setName(this.rootName);
		}
	}

	@Override
	public String getPanelType() {
		return "tree";
	}

	public com.f1.base.CalcTypes getUsedVariables() {
		CalcTypes vars = AmiWebUtils.getAvailableVariables(getService(), this);
		BasicCalcTypes r = new BasicCalcTypes();
		for (String i : this.getColumnAmiIds()) {
			AmiWebTreeColumn col = this.getColumnByAmiId(i);
			if (!col.isTransient())
				AmiWebUtils.getUsedVars(col.getFormulas(), vars, r);
		}
		for (AmiWebTreeGroupBy i : this.getGroupbyFormulas().values()) {
			if (!i.isTransient())
				AmiWebUtils.getUsedVars(i.getFormulas(), vars, r);
		}
		AmiWebUtils.getUsedVars(getFormulas(), vars, r);
		return r;
	}

	@Override
	public void clearUserSelection() {
	}

	public void putSort(Map<String, Object> sink) {
		FastWebTree origTree = getTree();
		if (!origTree.getSortedColumnIds().isEmpty()) {
			sink.put("KeepSorting", origTree.isKeepSorting());
			List<LinkedHashMap<String, Object>> sorting = new ArrayList<LinkedHashMap<String, Object>>();
			Iterable<Entry<Integer, Boolean>> sortedColumns = origTree.getSortedColumns();
			for (Entry<Integer, Boolean> i : sortedColumns) {
				FastWebTreeColumn col = origTree.getColumn(i.getKey());
				sorting.add(CH.m(new LinkedHashMap<String, Object>(), "name", col.getColumnName(), "type", i.getValue() ? "asc" : "des"));
			}
			sink.put("Sorting", sorting);
		}
	}

	public void applySort() {
		LinkedHashMap<Integer, Integer> toSort = getTree().getToSort();
		for (Entry<Integer, Integer> e : toSort.entrySet()) {
			Integer mask = e.getValue();
			boolean ascend = (mask & FastWebTree.ASCEND) == FastWebTree.ASCEND;
			boolean keepSort = (mask & FastWebTree.KEEP_SORT) == FastWebTree.KEEP_SORT;
			boolean add = (mask & FastWebTree.ADD) == FastWebTree.ADD;
			// init -> store sort info -> load data -> sort -> load pref? -> sort again
			tree.sortRows(e.getKey(), ascend, keepSort, add);
		}
	}

	public void prepareSort(Map<String, Object> sink, Map<String, Integer> columnsByName) {
		FastWebTree origTree = getTree();
		origTree.clearPendingSort();
		FastWebTreeColumn groupingColumn = origTree.getTreeColumn();
		Boolean keepSorting = CH.getOr(Caster_Boolean.INSTANCE, sink, "KeepSorting", null);
		if (keepSorting != null) {
			origTree.setKeepSorting(keepSorting);
			List<LinkedHashMap<String, Object>> sorting = (List<LinkedHashMap<String, Object>>) sink.get("Sorting");
			boolean add = false;
			for (LinkedHashMap<String, Object> sort : sorting) {
				String name = CH.getOrThrow(Caster_String.INSTANCE, sort, "name");
				String type = CH.getOrThrow(Caster_String.INSTANCE, sort, "type");
				Integer col = columnsByName.get(name);
				if (name == groupingColumn.getColumnName())
					col = groupingColumn.getColumnId();
				if (col != null) {
					int f = "asc".equals(type) ? MH.setBits(0, FastWebTree.ASCEND, true) : 0;
					int s = keepSorting ? MH.setBits(0, FastWebTree.KEEP_SORT, true) : 0;
					int t = add ? MH.setBits(0, FastWebTree.ADD, true) : 0;
					origTree.getToSort().put(col, f | s | t);
					add = true;
				}
			}
		} else {
			origTree.clearSort();
		}
	}

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		List<Map> formulas = new ArrayList<Map>();
		for (AmiWebTreeGroupBy i : this.groupbyFormulas.values()) {
			if (i.isTransient())
				continue;
			AmiWebTreeGroupByFormatter formatter = i.getFormatter();
			formulas.add(CH.mSkipNull("id", i.getAmiId(), "l", i.isLeaf(), "f", i.getGroupby(false), "pg", i.getParentGroup(false), "d", formatter.getDescription(false), "o",
					formatter.getOrderBy(false), "s", formatter.getStyle(false), "c", formatter.getColor(false), "b", formatter.getBackgroundColor(false), "rs",
					i.getRowStyle(false), "rc", i.getRowColor(false), "rb", i.getRowBackgroundColor(false)));
		}
		// filters
		HashMap<Integer, HashMap<String, Object>> filters = new HashMap<Integer, HashMap<String, Object>>();
		Set<Integer> filteredInColumns = this.tree.getFilteredInColumns();
		for (Integer colInd : filteredInColumns) {
			WebTreeFilteredInFilter filteredIn = this.tree.getFiltererdIn(colInd);
			if (!filteredIn.isEmpty()) {
				// 'a>50','b>60' in the filter gives you two elements in this set
				HashMap<String, Object> m = new HashMap<String, Object>();
				filteredIn.getConfig(m);
				filters.put(colInd, m);
			}
		}
		r.put("filters", filters);
		// sorting
		putSort(r);
		List<Map<String, Object>> vcols = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> hcols = new ArrayList<Map<String, Object>>();
		for (int i = 1; i < this.tree.getVisibleColumnsCount() + 1; i++) {
			Map<String, Object> col2cfg = col2cfg(this.tree.getVisibleColumn(i));
			if (col2cfg != null)
				col2cfg.put("location", i);
			CH.addSkipNull(vcols, col2cfg);
		}
		for (int i = 0; i < this.tree.getHiddenColumnsCount(); i++)
			CH.addSkipNull(hcols, col2cfg(this.tree.getHiddenColumn(i)));
		r.put("formulas", formulas);
		r.put("vcols", vcols);
		r.put("hcols", hcols);
		r.put("pins", this.tree.getPinnedColumnsCount());
		r.put("tcolw", this.tree.getTreeColumn().getWidth());
		r.put("tcoln", this.tree.getTreeColumn().getColumnName());
		r.put("rn", this.rootName);
		r.put("sm", this.getSelectionMode());
		Map<String, String> vtypes = new HashMap<String, String>();
		AmiWebUtils.toVarTypesConfiguration(getService(), this.getAmiLayoutFullAlias(), getUsedVariables(), vtypes);
		where.getConfiguration(r);
		this.treePortletFormatter.getConfiguration(r);
		r.put("varTypes", vtypes);
		if (this.getTree().getVisibleColumnsLimit() != -1)
			r.put("vclim", this.getTree().getVisibleColumnsLimit());
		return r;
	}
	private Map<String, Object> col2cfg(FastWebTreeColumn col) {
		AmiWebTreeColumn c = this.columnsById.get(col.getColumnId());
		if (c.isTransient())
			return null;
		AmiWebTreeGroupByFormatter f = c.getFormatter();
		Map<String, Object> r = CH.mSkipNull("i", col.getColumnId(), "id", c.getAmiId(), "n", col.getColumnName(), "w", col.getWidth(), "d", f.getDescription(false), "c",
				f.getColor(false), "b", f.getBackgroundColor(false), "y", AmiWebUtils.CUSTOM_COL_NAMES.getValue(c.getFormatterType()), "s", f.getStyle(false), "o",
				f.getOrderBy(false), "h", col.getHelp(), "hs", c.getHeaderStyle());
		CH.putExcept(r, "pc", c.getDecimals(), AmiConsts.DEFAULT);
		return r;
	}

	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		super.init(configuration, origToNewIdMapping, sb);
		List<Map> formulas = (List<Map>) configuration.get("formulas");
		com.f1.utils.structs.table.stack.BasicCalcTypes varTypes = AmiWebUtils.fromVarTypesConfiguration(getService(), (Map<String, String>) configuration.get("varTypes"));
		if (varTypes != null) {
			setUserDefinedVariables(varTypes);
		}
		if (formulas != null) {
			for (Map m : formulas) {
				boolean isLeaf = (boolean) CH.getOr(Caster_Boolean.INSTANCE, m, "l", Boolean.FALSE);
				String f = (String) m.get("f");
				String parentGroup = CH.getOr(Caster_String.INSTANCE, m, "pg", null);
				String description = (String) m.get("d");
				String style = (String) m.get("s");
				String color = (String) m.get("c");
				String bg = (String) m.get("b");
				String orderBy = (String) m.get("o");
				String rowStyle = (String) m.get("rs");
				String rowColor = (String) m.get("rc");
				String rowBg = (String) m.get("rb");
				String id = (String) m.get("id");
				if (id == null) {
					//backwards compatibility
					String s = description;
					if (s == null)
						s = f;
					id = SH.getNextId(AmiWebUtils.toPrettyVarName(s, "grouping"), this.groupbyFormulas.keySet());
				}
				AmiWebTreeGroupBy t = new AmiWebTreeGroupBy(id, this);
				if (t.setFormula(isLeaf, f, parentGroup, description, orderBy, style, color, bg, rowStyle, rowColor, rowBg)) {
					this.groupbyFormulas.add(t.getAmiId(), t);
					t.addToDomManager();
				}
			}
		}

		this.tree.getTreeColumn().setWidth(CH.getOr(Caster_Integer.INSTANCE, configuration, "tcolw", 200));
		this.tree.getTreeColumn().setColumnName(CH.getOr(Caster_String.INSTANCE, configuration, "tcoln", ""));
		List<Map> vcols = (List<Map>) CH.getOr(Caster_Simple.OBJECT, configuration, "vcols", Collections.EMPTY_LIST);
		List<Map> hcols = (List<Map>) CH.getOr(Caster_Simple.OBJECT, configuration, "hcols", Collections.EMPTY_LIST);
		// add columns
		vcols = CH.sort(vcols, LOCATION_COMPARATOR);
		for (Map<String, Object> col : vcols)
			this.addColumnFormulaFromConfig(col, true, sb);
		for (Map<String, Object> col : hcols)
			this.addColumnFormulaFromConfig(col, false, sb);
		// apply filters
		HashMap<Object, HashMap<String, Object>> filters = (HashMap<Object, HashMap<String, Object>>) CH.getOr(Caster_Simple.OBJECT, configuration, "filters",
				new LinkedHashMap<Object, HashMap<String, Object>>());//for recursive trees, filters are disabled, CH.getOr() should return dflt, as an empty linkedHashmap 
		for (Object t : filters.keySet()) {
			HashMap<String, Object> filterVals = filters.get(t);
			List<String> fv = CH.l((Collection<String>) CH.getOrThrow(Caster_Simple.OBJECT, filterVals, "value"));
			String min = (String) CH.getOr(filterVals, "min", "");
			HashSet<String> s = new HashSet<String>(fv);
			String max = (String) CH.getOr(filterVals, "max", "");
			Boolean isMinInclusive = OH.cast(CH.getOr(filterVals, "isMinInclusive", "false"), boolean.class);
			Boolean isMaxInclusive = OH.cast(CH.getOr(filterVals, "isMaxInclusive", "false"), boolean.class);
			Boolean isPattern = OH.cast(CH.getOr(filterVals, "isPattern", "false"), boolean.class);
			Boolean includeNull = OH.cast(CH.getOr(filterVals, "includeNull", "false"), boolean.class);
			Boolean isKeep = OH.cast(CH.getOr(filterVals, "isKeep", "true"), boolean.class);
			// at this point the tree has no data
			// another thread will run the dm and populate the table later, so we are just setting
			this.tree.setFilteredInNoRun(OH.cast(t, Integer.class), s, isKeep, includeNull, isPattern, min, isMinInclusive, max, isMaxInclusive);
		}
		Map<String, Integer> columnsByName = new HashMap<String, Integer>();
		IntIterator it = tree.getColumnIds();
		while (it.hasNext()) {
			FastWebTreeColumn col2 = tree.getColumn(it.next());
			columnsByName.put(col2.getColumnName(), col2.getColumnId());
		}
		prepareSort(configuration, columnsByName);
		setRootName(CH.getOr(Caster_String.INSTANCE, configuration, "rn", null));
		setSelectionMode(CH.getOr(Caster_Byte.PRIMITIVE, configuration, "sm", FastWebTree.SELECTION_MODE_STANDARD));
		this.tree.setPinnedColumnsCount(CH.getOr(Caster_Integer.INSTANCE, configuration, "pins", 0));
		where.init(configuration);
		this.where.resetWhere();///this.getClassTypes());
		this.treePortletFormatter.init(configuration, sb);
		this.getTree().setVisibleColumnsLimit(CH.getOr(Caster_Integer.PRIMITIVE, configuration, "vclim", -1));
		this.flagRebuildCalcs();
		this.disableFilteringInRecursiveTrees();
	}
	private FastWebTreeColumn addColumnFormulaFromConfig(Map<String, Object> col, boolean visible, StringBuilder errorSink) {
		int i = CH.getOrThrow(Caster_Integer.INSTANCE, col, "i");
		int width = CH.getOrThrow(Caster_Integer.INSTANCE, col, "w");
		String name = CH.getOrThrow(Caster_String.INSTANCE, col, "n");
		String id = CH.getOr(Caster_String.INSTANCE, col, "id", null);
		String help = CH.getOrNoThrow(Caster_String.INSTANCE, col, "h", null);
		String headerStyle = CH.getOr(Caster_String.INSTANCE, col, "hs", null);//add
		String description = CH.getOr(Caster_String.INSTANCE, col, "d", null);
		String style = CH.getOr(Caster_String.INSTANCE, col, "s", null);
		String color = CH.getOr(Caster_String.INSTANCE, col, "c", null);
		String orderBy = CH.getOr(Caster_String.INSTANCE, col, "o", null);
		String bg = CH.getOr(Caster_String.INSTANCE, col, "b", null);
		String typeString = CH.getOr(Caster_String.INSTANCE, col, "y", null);
		Byte type;

		int decimals = CH.getOr(Caster_Integer.INSTANCE, col, "pc", AmiConsts.DEFAULT);
		//start backwards compatibility
		if (typeString == null) {
			type = CH.getOr(Caster_Byte.INSTANCE, col, "t", (byte) 0);
			if (type == AmiWebUtils.CUSTOM_COL_TYPE_TEXT)
				type = AmiWebUtils.CUSTOM_COL_TYPE_HTML;
		} else if (SH.areBetween(typeString, '0', '9')) {
			type = SH.parseByte(typeString);
		} else {
			type = AmiWebUtils.CUSTOM_COL_NAMES.getKey(typeString);
		}
		if (id == null) {
			id = "col_" + i;
		}
		//end backwards compatibility

		int pos = visible ? tree.getVisibleColumnsCount() : -1;
		FastWebTreeColumn c = addColumnFormula(i, id, name, description, orderBy, style, color, bg, type, decimals, pos, errorSink, help, headerStyle, false);
		c.setWidth(width);
		return c;
	}
	public FastTreePortlet getFastTreePortlet() {
		return this.treePortlet;
	}

	@Override
	public void populateConfigMenu(WebMenu headMenu) {
		headMenu.add(new BasicWebMenuLink("Groupings...", true, "groupings"));
		headMenu.add(new BasicWebMenuLink("Add Column...", true, "addcol").setBackgroundImage(AmiWebConsts.ICON_ADD));
		BasicWebMenu rem = new BasicWebMenu("Remove Column", this.tree.getColumnsCount() > 0);
		BasicWebMenu edit = new BasicWebMenu("Edit Column", this.tree.getColumnsCount() > 0);

		for (int i = 0; i < this.tree.getVisibleColumnsCount(); i++) {
			FastWebTreeColumn col = this.tree.getVisibleColumn(i + 1);
			rem.add(new BasicWebMenuLink(col.getColumnName(), true, "remcol_" + col.getColumnId()));
			edit.add(new BasicWebMenuLink(col.getColumnName(), true, "editcol_" + col.getColumnId()));
		}
		for (int i = 0; i < this.tree.getHiddenColumnsCount(); i++) {
			FastWebTreeColumn col = this.tree.getHiddenColumn(i);
			rem.add(new BasicWebMenuLink(col.getColumnName(), true, "remcol_" + col.getColumnId()));
			edit.add(new BasicWebMenuLink(col.getColumnName(), true, "editcol_" + col.getColumnId()));
		}
		headMenu.add(rem);
		headMenu.add(edit);
		headMenu.add(new BasicWebMenuLink("Arrange Columns...", this.tree.getColumnsCount() > 0, "arrange"));
	}

	@Override
	public boolean onAmiContextMenu(String id) {
		if ("groupings".equals(id)) {
			AmiWebTreeEditGroupingsPortlet dialog = new AmiWebTreeEditGroupingsPortlet(generateConfig(), this);
			getManager().showDialog("Treegrid Settings", dialog);
			return true;
		} else if ("addcol".equals(id)) {
			getManager().showDialog("Add Treegrid Column", new AmiWebTreeEditColumnPortlet(this, null, this.tree.getVisibleColumnsCount(), false));
			return true;
		} else if (id.startsWith("editcol_")) {
			int colId = SH.parseInt(SH.stripPrefix(id, "editcol_", true));
			AmiWebTreeColumn amiWebTreeColumn = this.columnsById.get(colId);
			getManager().showDialog("Edit Treegrid Column", new AmiWebTreeEditColumnPortlet(this, amiWebTreeColumn, 0, false));
			if (amiWebTreeColumn.isTransient())
				getManager().showAlert("You're editing a TRANSIENT object. This means your changes will not be saved in the layout");
			return true;
		} else if (id.startsWith("remcol_")) {
			int colId = SH.parseInt(SH.stripPrefix(id, "remcol_", true));
			FastWebTreeColumn col = this.tree.getColumn(colId);
			getManager().showDialog("Edit Treegrid Column",
					new ConfirmDialogPortlet(generateConfig(), "Remove <B>" + WebHelper.escapeHtml(col.getColumnName()) + "</B>?", ConfirmDialogPortlet.TYPE_OK_CANCEL, this)
							.setCallback("DELETE_COL").setCorrelationData(colId));
			return true;
		} else if ("arrange".equals(id)) {
			getManager().showDialog("Arrange Columns", (new ArrangeColumnsPortlet(generateConfig(), this.tree)), 880, 328);
			return true;
		} else
			return super.onAmiContextMenu(id);
	}
	// could retire this if showSettingsPortlet works.
	//	protected abstract Portlet newSettingsPortlet();

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if ("DELETE_COL".equals(source.getCallback())) {
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				int colid = (Integer) source.getCorrelationData();
				removeColumn(colid);
			}
			return true;
		}
		return super.onButton(source, id);
	}

	public void removeTransientColumn(int id) {
		this.getColumnFormatter(id);
		final AmiWebTreeColumn col = this.getColumnFormatter(id);
		if (col == null)
			throw new NoSuchElementException("Can not remove tree column: " + id);
		if (!col.isTransient())
			return;
		this.removeColumn(id);
	}

	private void removeColumn(int colid) {
		final AmiWebTreeColumn col = this.getColumnFormatter(colid);
		if (col == null)
			throw new NoSuchElementException("Can not remove tree column: " + colid);
		final List<AggCalculator> aggs = new ArrayList<AggCalculator>();
		final AmiWebFormulas formulas = col.getFormulas();
		for (String s : formulas.getFormulaIds())
			DerivedHelper.find(formulas.getFormula(s).getFormulaCalc(), AggCalculator.class, aggs);
		for (final AggCalculator calc : aggs)
			this.removeAggCalcRef(calc);

		int colID = col.getColumnId();
		this.tree.removeColumn(colID);
		this.columnsByAmiId.remove(col.getAmiId());
		this.columnsById.remove(colID);
		col.removeFromDomManager();
		this.rebuildCalcs();
	}

	public AmiWebTreeGroupBy getFormula(int i) {
		return this.groupbyFormulas.getAt(i);
	}
	public AmiWebTreeGroupBy getGroupBy(String amiId) {
		return this.groupbyFormulas.getNoThrow(amiId);
	}
	public int getFormulasCount() {
		return this.groupbyFormulas.getSize();
	}

	public void setFormulas(List<AmiWebTreeGroupBy> formulas) {
		IdentityHashSet<AmiWebTreeGroupBy> existing = new IdentityHashSet<AmiWebTreeGroupBy>();
		CH.addAll(existing, this.groupbyFormulas.values());
		IdentityHashSet<AmiWebTreeGroupBy> nuw = new IdentityHashSet<AmiWebTreeGroupBy>(formulas);
		for (AmiWebTreeGroupBy i : CH.comm(existing, nuw, true, false, false)) {
			i.removeFromDomManager();
		}

		this.groupbyFormulas.clear();
		for (int i = 0; i < formulas.size(); i++) {
			AmiWebTreeGroupBy gb = formulas.get(i);
			if (gb.isLeaf() && i != formulas.size() - 1)
				throw new IllegalArgumentException();
			this.groupbyFormulas.add(gb.getAmiId(), gb);
		}
		for (AmiWebTreeGroupBy i : CH.comm(existing, nuw, false, true, false))
			i.addToDomManager();
		//		clearAmiData();
		//		this.rebuildCalcs();
		//		this.rebuildAmiData();
	}

	protected void rebuildCalcs() {
		// If we are forcing an update on the formulas all we need to do is get dependencies
		Set<Object> used = new HasherSet<Object>();
		for (AmiWebTreeColumn c : this.columnsById.values()) {
			AmiWebTreeGroupByFormatter i = c.getFormatter();
			i.getDependencies(used);

		}
		for (AmiWebTreeGroupBy i : this.groupbyFormulas.values()) {
			AmiWebTreeGroupByFormatter f = i.getFormatter();
			i.getDependencies(used);
		}
		this.treePortletFormatter.getDependencies((Set) used);
		BasicCalcTypes usedTypes = new BasicCalcTypes();
		for (Object o : used) {
			String s = (String) o;
			if (VAR_TYPES.getType(s) != null)
				continue;
			Class<?> type = getClassTypes().getType((String) s);
			if (type == null)
				type = getUserDefinedVariables().getType((String) s);
			usedTypes.putType((String) s, type);
		}
		setUserDefinedVariables(usedTypes);
	}

	public String getRootName() {
		return rootName;
	}
	public void setRootName(String rootName) {
		if (SH.isnt(rootName))
			rootName = null;
		if (OH.eq(this.rootName, rootName))
			return;
		if (rootName == null)
			this.tree.setRootLevelVisible(false);
		else {
			if (this.rootName == null)
				this.tree.setRootLevelVisible(true);
			this.treeManager.getRoot().setName(rootName);
		}
		this.rootName = rootName;
	}

	private void updateAmiSelection() {
		this.getAmiScriptCallbacks().execute(CALLBACK_DEF_ONSELECTED.getMethodName());
		for (AmiWebDmLink link : getDmLinksFromThisPortlet()) {
			if (link.isRunOnSelect()) {
				AmiWebDmUtils.sendRequest(getService(), link);
			}
		}
	}

	@Override
	public void onContextMenu(FastWebTree tree, String action) {
		if (action.startsWith("query_")) {
			String remotePortletIds = SH.stripPrefix(action, "query_", true);
			for (String t : SH.split('_', remotePortletIds)) {
				AmiWebDmLink link = getService().getDmManager().getDmLink(t);
				AmiWebDmUtils.sendRequest(getService(), link);
			}
		} else if (isCustomContextMenuAction(action)) {
			processCustomContextMenuAction(action);
		}
	}

	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode node) {
		if (node != null) {
			if (node.getChildrenCount() > 0 && !node.getHasCheckbox())
				node.setIsExpanded(!node.getIsExpanded());
		}
	}

	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {
		flagPendingAjax();
		resetCurrentNode();
		amiSelectionChanged = true;
	}

	public byte getSelectionMode() {
		return this.tree.getSelectionMode();
	}

	public void setSelectionMode(byte selectionMode) {
		this.tree.setSelectionMode(selectionMode);
	}

	public String getBgColor() {
		return bgColor;
	}

	public void setBgColor(String bgColor) {
		this.treePortlet.addOption(FastTreePortlet.OPTION_BACKGROUND_STYLE, "_bg=" + bgColor);
		this.bgColor = bgColor;
	}
	public void setGrayBarColor(String grayBarColor) {
		treePortlet.addOption(FastTreePortlet.OPTION_GRAY_BAR_STYLE, "_bg=" + grayBarColor);
		this.grayBarColor = grayBarColor;
	}

	public String getFontColor() {
		return fontColor;
	}

	public void setFontColor(String fontColor) {
		this.treePortlet.addOption(FastTreePortlet.OPTION_FONT_STYLE, "_fg=" + fontColor);
		this.fontColor = fontColor;
	}

	public String getSearchBarBgColor() {
		return searchBarBgColor;
	}

	public void setSearchBarBgColor(String searchBarBgColor) {
		this.searchBarBgColor = searchBarBgColor;
		this.treePortlet.addOption(FastTreePortlet.OPTION_SEARCH_BAR_BG_STYLE, "_bg=" + searchBarBgColor);
	}

	public String getSearchBarFieldBgColor() {
		return searchBarFieldBgColor;
	}

	public void setSearchBarFieldBgColor(String searchBarFieldBgColor) {
		this.treePortlet.addOption(FastTreePortlet.OPTION_SEARCH_BAR_FIELD_BG_STYLE, "_bg=" + searchBarFieldBgColor);
		this.searchBarFieldBgColor = searchBarFieldBgColor;
	}

	public String getSearchBarFieldFgColor() {
		return searchBarFieldFgColor;
	}

	public void setSearchBarFieldFgColor(String searchBarFieldFgColor) {
		this.treePortlet.addOption(FastTreePortlet.OPTION_SEARCH_BAR_FIELD_FG_STYLE, "_fg=" + searchBarFieldFgColor);
		this.searchBarFieldFgColor = searchBarFieldFgColor;
	}

	public String getSearchFieldBorderColor() {
		return this.searchFieldBorderColor;
	}

	public void setSearchFieldBorderColor(String color) {
		this.treePortlet.addOption(FastTreePortlet.OPTION_SEARCH_FIELD_BORDER_COLOR, color);
		this.searchFieldBorderColor = color;
	}

	public String getSearchButtonsColor() {
		return searchButtonsColor;
	}

	public void setSearchButtonsColor(String searchButtonsColor) {
		this.treePortlet.addOption(FastTreePortlet.OPTION_SEARCH_BUTTONS_COLOR, searchButtonsColor);
		this.searchButtonsColor = searchButtonsColor;
	}

	public boolean isSearchBarHidden() {
		return searchBarHidden;
	}

	public void setSearchBarHidden(Boolean searchBarHidden) {
		if (searchBarHidden == null)
			return;
		this.searchBarHidden = searchBarHidden;
		this.treePortlet.addOption(FastTreePortlet.OPTION_SEARCH_BAR_HIDDEN, searchBarHidden);
	}

	public Integer getScrollBarWidth() {
		return scrollBarWidth;
	}

	public void setScrollBarWidth(Integer scrollBarWidth) {
		this.scrollBarWidth = scrollBarWidth;
		this.treePortlet.addOption(FastTreePortlet.OPTION_SCROLL_BAR_WIDTH, scrollBarWidth);
	}

	public String getScrollGripColor() {
		return gripColor;
	}

	@Override
	public Integer getScrollBarRadius() {
		return this.scrollBarRadius;
	}

	@Override
	public void setScrollBarRadius(Integer scrollBarRadius) {
		this.treePortlet.addOption(FastTreePortlet.OPTION_SCROLL_BAR_RADIUS, scrollBarRadius);
		this.scrollBarRadius = scrollBarRadius;
	}

	public void setScrollGripColor(String gripColor) {
		this.treePortlet.addOption(FastTreePortlet.OPTION_GRIP_COLOR, "_bg=" + gripColor);
		this.gripColor = gripColor;
	}

	public String getScrollTrackColor() {
		return trackColor;
	}

	public void setScrollTrackColor(String trackColor) {
		this.treePortlet.addOption(FastTreePortlet.OPTION_TRACK_COLOR, "_bg=" + trackColor);
		this.trackColor = trackColor;
	}
	public String getScrollButtonColor() {
		return scrollButtonColor;
	}

	public void setScrollButtonColor(String scrollButtonColor) {
		treePortlet.addOption(FastTreePortlet.OPTION_SCROLL_BUTTON_COLOR, "_bg=" + scrollButtonColor);
		this.scrollButtonColor = scrollButtonColor;
	}

	public String getScrollIconsColor() {
		return scrollIconsColor;
	}

	public void setScrollIconsColor(String scrollIconsColor) {
		treePortlet.addOption(FastTreePortlet.OPTION_SCROLL_ICONS_COLOR, scrollIconsColor);
		this.scrollIconsColor = scrollIconsColor;
	}
	@Override
	public String getScrollBorderColor() {
		return scrollBorderColor;
	}
	@Override
	public void setScrollBorderColor(String color) {
		treePortlet.addOption(FastTreePortlet.OPTION_SCROLL_BORDER_COLOR, color);
		this.scrollBorderColor = color;
	}
	private boolean parseBooleanOption(Object style) {
		return Caster_Boolean.PRIMITIVE.cast(style);
	}
	private Integer parseIntegerOption(Object style) {
		return Caster_Integer.INSTANCE.cast(style);
	}
	@Override
	public void onAmiInitDone() {
		super.onAmiInitDone();
	}

	public boolean hasRecursiveGroupings() {
		for (int i = 0; i < this.groupbyFormulas.getSize(); i++) {
			AmiWebTreeGroupBy groupBy = this.groupbyFormulas.getAt(i);
			if (groupBy.getIsRecursive())
				return true;
		}
		return false;
	}
	public boolean disableFilteringInRecursiveTrees() {
		boolean hasRecursiveGroupings = this.hasRecursiveGroupings();
		if (hasRecursiveGroupings) {
			setQuickColumnFilterHidden(true);
		} else {
			if (this.getQuickColumnFilterHidden() == true) {
				Object oldValue = this.getStylePeer().resolveValue(AmiWebStyleTypeImpl_Treegrid.TYPE_TREEGRID, AmiWebStyleConsts.CODE_COLUMN_FILTER_HIDE);
				if (OH.eq(oldValue, false))
					this.onStyleValueChanged(AmiWebStyleConsts.CODE_COLUMN_FILTER_HIDE, true, false);

			}
		}
		return hasRecursiveGroupings;
	}
	@Override
	public void onStyleValueChanged(short key, Object old, Object value) {
		super.onStyleValueChanged(key, old, value);
		switch (key) {
			case AmiWebStyleConsts.CODE_BG_CL:
				setBgColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_GRAYBAR_CL:
				setGrayBarColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_FONT_CL:
				setFontColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_CELL_BDR_CL:
				setCellBorderColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_CELL_BTM_PX:
				setCellBottomDivider(parseIntegerOption(value));
				break;
			case AmiWebStyleConsts.CODE_HEADER_HT:
				setHeaderRowHeight(parseIntegerOption(value));
				break;
			case AmiWebStyleConsts.CODE_HEADER_FONT_SZ:
				setHeaderFontSize(parseIntegerOption(value));
				break;
			case AmiWebStyleConsts.CODE_ROW_HT:
				setRowHeight(parseIntegerOption(value));
				break;
			case AmiWebStyleConsts.CODE_FONT_SZ:
				setFontSize(parseIntegerOption(value));
				break;
			case AmiWebStyleConsts.CODE_CELL_RT_PX:
				setCellRightDivider(parseIntegerOption(value));
				break;
			case AmiWebStyleConsts.CODE_VT_ALIGN:
				setVerticalAlign((String) value);
				break;
			case AmiWebStyleConsts.CODE_HEADER_DIV_HIDE:
				setHideHeaderDivider(Caster_Boolean.PRIMITIVE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_HEADER_BAR_HIDE:
				setHideHeaderBar(Caster_Boolean.PRIMITIVE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_HEADER_BG_CL:
				setColumnHeaderBgColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_HEADER_FONT_CL:
				setColumnHeaderFontColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_SEARCH_HIDE:
				setSearchBarHidden(Caster_Boolean.PRIMITIVE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_SEARCH_BG_CL:
				setSearchBarBgColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_SEARCH_FLD_BG_CL:
				setSearchBarFieldBgColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_SEARCH_FLD_FONT_CL:
				setSearchBarFieldFgColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_SEARCH_FLD_BDR_CL:
				setSearchFieldBorderColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_SEARCH_BTNS_CL:
				setSearchButtonsColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_FILT_BG_CL:
				setFilteredHeaderBgColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_FILT_FONT_CL:
				setFilteredHeaderFontColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_SCROLL_WD:
				setScrollBarWidth(parseIntegerOption(value));
				break;
			case (AmiWebStyleConsts.CODE_SCROLL_TRACK_CL):
				setScrollTrackColor((String) value);
				break;
			case (AmiWebStyleConsts.CODE_SCROLL_GRIP_CL):
				setScrollGripColor((String) value);
				break;
			case (AmiWebStyleConsts.CODE_SCROLL_BTN_CL):
				setScrollButtonColor((String) value);
				break;
			case (AmiWebStyleConsts.CODE_SCROLL_ICONS_CL):
				setScrollIconsColor((String) value);
				break;
			case (AmiWebStyleConsts.CODE_SCROLL_BDR_CL):
				setScrollBorderColor((String) value);
				break;
			case (AmiWebStyleConsts.CODE_SCROLL_BAR_CORNER_CL):
				setScrollBarCornerColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_FONT_FAM:
				setFontFamily((String) value);
				break;
			case AmiWebStyleConsts.CODE_CELL_PAD_HT:
				setCellPaddingHorizontal(parseIntegerOption(value));
				break;
			//Quick Filter
			case AmiWebStyleConsts.CODE_COLUMN_FILTER_HIDE:
				if (this.hasRecursiveGroupings()) {
					setQuickColumnFilterHidden(true);
					break;
				}
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
			//End Quick Filter

			case AmiWebStyleConsts.CODE_SEL_CL:
				setSelectColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_ACT_CL:
				setActiveSelectColor((String) value);
				break;
			case AmiWebStyleConsts.CODE_FLASH_UP_CL:
				this.treePortlet.addOption(FastTreePortlet.OPTION_FLASH_UP_COLOR, Caster_String.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_FLASH_DN_CL:
				this.treePortlet.addOption(FastTreePortlet.OPTION_FLASH_DN_COLOR, Caster_String.INSTANCE.cast(value));
				break;
			case AmiWebStyleConsts.CODE_FLASH_MILLIS:
				this.treePortlet.addOption(FastTreePortlet.OPTION_FLASH_MS, Caster_String.INSTANCE.cast(value));
				break;

		}
	}
	@Override
	public void drainJavascript() {
		super.drainJavascript();
		if (amiSelectionChanged) {
			amiSelectionChanged = false;
			updateAmiSelection();
		}
	}

	public FastWebTreeColumn addTransientColumn(int i, String amiId, String name, String description, String orderBy, String style, String color, String bgColor, byte type,
			int decimals, int position, StringBuilder errorSink, String help, String headerStyle) {
		final FastWebTreeColumn c = this.addColumnFormula(i, amiId, name, description, orderBy, style, color, bgColor, type, decimals, position, errorSink, help, headerStyle,
				true);
		if (c == null)
			return null;
		final AmiWebTreeColumn t = this.columnsById.get(c.getColumnId());
		t.setTransient(true);
		return c;
	}

	public FastWebTreeColumn addColumnFormula(int i, String amiId, String name, String description, String orderBy, String style, String color, String bgColor, byte type,
			int decimals, int position, StringBuilder errorSink, String help, String headerStyle, boolean rebuildCalcs) {
		name = SH.trim(name);
		help = SH.trim(help);
		if (SH.isnt(name)) {
			errorSink.append("Column Header required");
			return null;
		}
		for (FastWebTreeColumn cf : this.tree.getColumns()) {
			if (OH.eq(name, cf.getColumnName())) {
				if (cf.getColumnId() != i) {
					errorSink.append("Column header already exists");
					return null;
				}
				break;
			}
		}
		final FastWebTreeColumn r;
		AmiWebService service = AmiWebUtils.getService(getManager());
		WebCellFormatter formatter;
		AmiWebFormatterManager fm = service.getFormatterManager();
		formatter = AmiWebAbstractTablePortlet.getFormatter(this.getService(), type, decimals);
		AmiWebTreeColumn t = this.columnsById.get(i);
		AmiWebTreeGroupByFormatter f = t == null ? null : t.getFormatter();
		String cssExpression = AmiWebUtils.toCssExpression(headerStyle);//add
		if (f == null) {
			if (SH.isnt(amiId))
				amiId = SH.getNextId(AmiWebUtils.toPrettyVarName(name, "col_0"), this.columnsByAmiId.keySet(), 2);
			else if (this.columnsByAmiId.containsKey(amiId)) {
				amiId = SH.getNextId(amiId, this.columnsByAmiId.keySet());
			}
			if (i == -1)
				i = tree.getNextColumnId();
			final AmiWebTreeColumn c = new AmiWebTreeColumn(i, amiId, this, type, decimals);
			f = c.getFormatter();
			DerivedCellCalculator descriptionCalc = f.getCalcNoThrow(description);
			if (descriptionCalc != null) {
				Class<?> ct = descriptionCalc.getReturnType();
				if (!AmiWebAbstractTablePortlet.verifyFormatType(this.getAmiLayoutFullAliasDotId(), amiId, type, ct, errorSink))
					return null;
			}
			f.setColumnId(i);
			f.setFormula(description, orderBy, style, color, bgColor);
			final List<AggCalculator> aggs = new ArrayList<AggCalculator>();
			final AmiWebFormulas formulas = f.getFormulas();
			for (String s : formulas.getFormulaIds())
				DerivedHelper.find(formulas.getFormula(s).getFormulaCalc(), AggCalculator.class, aggs);
			for (final AggCalculator calc : aggs)
				this.addAggCalcRef(calc);
			f.setInnerFormatter(formatter);
			r = new FastWebTreeColumn(i, f, name, help, false);
			r.setHeaderStyle(cssExpression);//add
			c.setColumn(r);
			c.setHeaderStyleExpression(cssExpression);
			c.setHeaderStyle(headerStyle);
			if (r.getColumnCssClass() == null)
				r.setColumnCssClass(fm.getBasicFormatter().getDefaultColumnCssClass());
			tree.addColumnAt(position >= 0, r, position == -1 ? 0 : position);
			r.setFormatter(r.getFormatter());
			r.setJsFormatterType(setType(type));
			this.columnsByAmiId.put(c.getAmiId(), c);
			this.columnsById.put(c.getColumnId(), c);
			c.addToDomManager();
		} else {
			if (OH.ne(amiId, t.getAmiId())) {
				if (this.columnsByAmiId.containsKey(amiId)) {
					errorSink.append("Duplicate id: " + amiId);
					return null;
				}
			}
			DerivedCellCalculator oldCalc = f.getCalcNoThrow(f.getDescription(false));
			DerivedCellCalculator descriptionCalc = f.getCalcNoThrow(description);
			if (descriptionCalc != null) {
				Class<?> ct = descriptionCalc.getReturnType();
				if (!AmiWebAbstractTablePortlet.verifyFormatType(this.getAmiLayoutFullAliasDotId(), amiId, type, ct, errorSink))
					return null;
				if (descriptionCalc instanceof AggCalculator)
					this.addAggCalcRef((AggCalculator) descriptionCalc);
			}
			if (oldCalc != null && oldCalc instanceof AggCalculator)
				this.removeAggCalcRef((AggCalculator) oldCalc);
			f.setFormula(description, orderBy, style, color, bgColor);
			f.setInnerFormatter(formatter);
			r = t.getColumn();

			position = Math.min(position, tree.getVisibleColumnsCount());
			int t1 = tree.getColumnPosition(r.getColumnId());
			if (position == -1) {
				tree.hideColumn(r.getColumnId());
			} else if (t1 < position) {
				tree.showColumn(r.getColumnId(), position - 1);
			} else {
				tree.showColumn(r.getColumnId(), position);
			}
			r.setColumnName(name);
			r.setHelp(help);
			r.setHeaderStyle(cssExpression);//add
			t.setHeaderStyleExpression(cssExpression);//add
			t.setHeaderStyle(headerStyle);
			if (r.getColumnCssClass() == null)
				r.setColumnCssClass(fm.getBasicFormatter().getDefaultColumnCssClass());
			r.setFormatter(r.getFormatter());
			r.setJsFormatterType(setType(type));
			if (OH.ne(amiId, t.getAmiId())) {
				this.columnsByAmiId.remove(t.getAmiId());
				t.setAmiId(amiId);
				this.columnsByAmiId.put(t.getAmiId(), t);
			}
			t.setFormatterType(type);
			t.setDecimals(decimals);
		}
		if (rebuildCalcs) {
			this.flagRebuildCalcs();
			f.onRebuildCalcs();
		}
		return r;
	}

	static public String setType(byte type) {
		switch (type) {
			case AmiWebUtils.CUSTOM_COL_TYPE_SPARK_LINE:
				return "spark_line";
			case AmiWebUtils.CUSTOM_COL_TYPE_IMAGE:
				return "image";
			case AmiWebUtils.CUSTOM_COL_TYPE_HTML:
				return "html";
			case AmiWebUtils.CUSTOM_COL_TYPE_TEXT:
				return "text";
			case AmiWebUtils.CUSTOM_COL_TYPE_CHECKBOX:
				return "checkbox";
			default:
				return "";//TODO:throw exception
		}
	}

	public FastWebTree getTree() {
		return this.tree;
	}

	public static final String VAR_CHECKED = "__CHECKED";
	public static final String VAR_EXPANDED = "__EXPANDED";
	public static final String VAR_SIZE = "__SIZE";

	public static final String VAR_RSIZE = "__RSIZE";
	public static final String VAR_LEAVES = "__LEAVES";
	public static final String VAR_DEPTH = "__DEPTH";

	public static final BasicCalcTypes VAR_TYPES = new BasicCalcTypes();
	static {
		VAR_TYPES.putType(VAR_CHECKED, Integer.class);
		VAR_TYPES.putType(VAR_EXPANDED, Boolean.class);
		VAR_TYPES.putType(VAR_SIZE, Integer.class);
		VAR_TYPES.putType(VAR_RSIZE, Integer.class);
		VAR_TYPES.putType(VAR_LEAVES, Integer.class);
		VAR_TYPES.putType(VAR_DEPTH, Integer.class);
	}
	public static final Integer TWO = 2;
	public static final Integer ONE = 1;
	public static final Integer ZERO = 0;

	@Override
	public WebMenu createColumnMenu(FastWebTree tree, FastWebTreeColumn column, WebMenu defaultMenu) {
		if (column == null) {
			return defaultMenu;
		}
		if (!this.hasRecursiveGroupings()) {
			//Originally in FastWebTree, event handler in there too
			boolean filtered = this.getTree().getFilteredInColumns().contains(column.getColumnId());
			boolean hasFilter = !this.getTree().getFilteredInColumns().isEmpty();
			defaultMenu.add(new BasicWebMenuLink("Filter...", true, "__filter"));
			defaultMenu.add(new BasicWebMenuLink("Clear Filter", filtered, "__clearfilter"));
			defaultMenu.add(new BasicWebMenuLink("Clear All Filters", hasFilter, "__clearAllFilter"));
		}

		defaultMenu.setStyle(this.getService().getDesktop().getMenuStyle());
		defaultMenu.add(new BasicWebMenuLink("Arrange Columns...", this.tree.getColumnsCount() > 0, "arrange"));
		if (column != tree.getTreeColumn())
			defaultMenu.add(new BasicWebMenuLink("Hide This Column", true, "__hide"));
		defaultMenu.add(new BasicWebMenuLink("Search Columns...", this.tree.getColumnsCount() > 0 && column != null, "search"));
		if (inEditMode()) {
			if (column == tree.getTreeColumn()) {
				defaultMenu.add(new BasicWebMenuDivider());
				defaultMenu.add(new BasicWebMenuLink("Groupings...", true, "groupings").setCssStyle("className=ami_edit_menu"));
				defaultMenu.add(new BasicWebMenuLink("Add Column to Right... ", true, "addrightcol").setCssStyle("className=ami_edit_menu"));
			} else {
				boolean cust = true;
				if (column != null) {
					if (cust) {
						defaultMenu.add(new BasicWebMenuDivider());
						defaultMenu.add(new BasicWebMenuLink("Edit Column...", true, "editcol").setCssStyle("className=ami_edit_menu"));
						defaultMenu.add(new BasicWebMenuLink("Copy Column...", true, "copycol").setCssStyle("className=ami_edit_menu"));
					} else {
						defaultMenu.add(new BasicWebMenuLink("Edit Column...", false, "editcol"));
						defaultMenu.add(new BasicWebMenuLink("Copy Column...", false, "copycol"));
					}
					defaultMenu.add(new BasicWebMenuLink("Add Column to Right... ", true, "addrightcol").setCssStyle("className=ami_edit_menu"));
					defaultMenu.add(new BasicWebMenuLink("Add Column to Left... ", true, "addleftcol").setCssStyle("className=ami_edit_menu"));
					defaultMenu.add(new BasicWebMenuDivider());
					if (cust)
						defaultMenu.add(new BasicWebMenuLink("Delete Column...", true, "remcol").setCssStyle("className=ami_edit_menu"));
					else
						defaultMenu.add(new BasicWebMenuLink("Delete Column...", false, "remcol"));
				} else {
					defaultMenu.add(new BasicWebMenuLink("Add Column...", true, "addrightcol").setCssStyle("className=ami_edit_menu"));
				}

			}
		}
		if (column != null && SH.is(column.getHelp())) {
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
			sb.append(column.getHelp());
			sb.append("</span>");
			BasicWebMenuLink link = new BasicWebMenuLink(sb.toString(), false, null);
			link.setCssStyle("className=ami_help_menu|_bg=" + bgColor + "|_fg=" + fgColor + "|_fs=" + SH.toString(fontSize));
			defaultMenu.add(link);

		}
		return defaultMenu;
	}
	@Override
	public void onColumnContextMenu(FastWebTree table, FastWebTreeColumn column, String id) {
		if ("groupings".equals(id)) {
			AmiWebTreeEditGroupingsPortlet dialog = new AmiWebTreeEditGroupingsPortlet(generateConfig(), this);
			getManager().showDialog("Groupings Settings", dialog);
		} else if (id.equals("editcol")) {
			int colId = column.getColumnId();
			AmiWebTreeColumn amiWebTreeColumn = this.columnsById.get(colId);
			getManager().showDialog("Edit Tree Column", new AmiWebTreeEditColumnPortlet(this, amiWebTreeColumn, 0, false));
			if (amiWebTreeColumn.isTransient())
				getManager().showAlert("You're editing a TRANSIENT object. This means your changes will not be saved in the layout");
		} else if (id.equals("remcol")) {
			int colId = column.getColumnId();
			FastWebTreeColumn col = this.tree.getColumn(colId);
			getManager().showDialog("Edit Tree Column",
					new ConfirmDialogPortlet(generateConfig(), "Remove <B>" + WebHelper.escapeHtml(col.getColumnName()) + "</B>?", ConfirmDialogPortlet.TYPE_OK_CANCEL, this)
							.setCallback("DELETE_COL").setCorrelationData(colId));
		} else if (id.equals("addrightcol")) {
			if (column == null) {
				getManager().showDialog("Add Tree Column", new AmiWebTreeEditColumnPortlet(this, null, this.tree.getVisibleColumnsCount(), false));
			} else {
				int colId = column.getColumnId();
				getManager().showDialog("Add Tree Column", new AmiWebTreeEditColumnPortlet(this, null, this.tree.getColumnPosition(colId), false));
			}
		} else if (id.equals("addleftcol")) {
			int colId = column.getColumnId();
			getManager().showDialog("Add Tree Column", new AmiWebTreeEditColumnPortlet(this, null, this.tree.getColumnPosition(colId) - 1, false));
		} else if ("arrange".equals(id)) {
			getManager().showDialog("Arrange Columns", (new ArrangeColumnsPortlet(generateConfig(), this.tree, column)).setFormStyle(getService().getUserFormStyleManager()))
					.setStyle(getService().getUserDialogStyleManager());
		} else if ("search".equals(id)) {
			getManager().showDialog("Search Columns", new AmiWebSearchColumnsPortlet(generateConfig(), (FastWebColumns) tree, (FastWebColumn) column),
					AmiWebSearchColumnsPortlet.DIALOG_W, AmiWebSearchColumnsPortlet.DIALOG_H).setStyle(getService().getUserDialogStyleManager());
		} else if ("addcolumn".equals(id)) {
			getManager().showDialog("Add Column", new AmiWebTreeEditColumnPortlet(this, null, 0, false));
		} else if ("copycol".equals(id)) {
			int colId = column.getColumnId();
			getManager().showDialog("Edit Tree Column", new AmiWebTreeEditColumnPortlet(this, this.columnsById.get(colId), 0, true));
		}

	}

	private void addAggCalcRef(final AggCalculator calc) {
		int cnt = 1;
		if (aggRefs.containsKey(calc)) {
			cnt = aggRefs.get(calc) + 1;
		}
		aggRefs.put(calc, cnt);
	}

	private void removeAggCalcRef(final AggCalculator calc) {
		Integer cnt = aggRefs.get(calc);
		if (cnt == null) {
			log.warning("Unhandled calc ref: " + calc);
			return;
		}
		cnt -= 1;
		if (cnt == 0) {
			aggRefs.remove(calc);
			this.aggregateFactory.removeAggregate(calc);
		} else {
			aggRefs.put(calc, cnt);
		}
	}

	public void removeGroupingAt(Integer groupingPosition) {
		AmiWebTreeGroupBy t = groupbyFormulas.removeAt(groupingPosition);
		t.removeFromDomManager();
		this.flagRebuildCalcs();
		this.disableFilteringInRecursiveTrees();
	}
	public void removeGrouping(String groupingId) {
		AmiWebTreeGroupBy t = groupbyFormulas.remove(groupingId);
		t.removeFromDomManager();
		this.flagRebuildCalcs();
		this.disableFilteringInRecursiveTrees();
	}

	public void addGroupingColumn(String grouping, String display, String parentFormula, Integer groupingPosition) {
		if (groupingPosition == null)
			groupingPosition = groupbyFormulas.getSize();
		if (display == null)
			display = "";
		if (parentFormula == null)
			parentFormula = "";

		// equivalent to OptionsPortlet
		String amiId = getNextGroupingId(grouping);
		AmiWebTreeGroupBy groupBy = new AmiWebTreeGroupBy(amiId, this);
		FormulaWrapper wrapper = new FormulaWrapper(groupBy, this.getFormulasCount());
		if (!wrapper.getFormula().setFormula(groupBy.isLeaf(), grouping, parentFormula, display, null, null, null, null, null, null, null)) {
			getManager().showAlert("something wrong adding formula");
			return;
		}

		// equivalent to applySettings
		List<AmiWebTreeGroupBy> list = new ArrayList<AmiWebTreeGroupBy>();
		list.addAll(groupbyFormulas.valueList());
		list.add(groupingPosition, groupBy);
		this.setFormulas(list);
		this.flagRebuildCalcs();
		this.disableFilteringInRecursiveTrees();

	}
	private String getNextGroupingId(String groupby) {
		return SH.getNextId(AmiWebUtils.toPrettyVarName(groupby, "grouping"), this.groupbyFormulas.keySet());

	}
	public String getGrayBarColor() {
		return grayBarColor;
	}

	public boolean isHideHeaderDivider() {
		return hideHeaderDivider;
	}

	public boolean isHideHeaderBar() {
		return hideHeaderBar;
	}

	public void setHideHeaderDivider(Boolean hideHeaderDivider) {
		this.hideHeaderDivider = hideHeaderDivider;
		treePortlet.addOption(FastTreePortlet.OPTION_HEADER_DIVIDER_HIDDEN, hideHeaderDivider);
	}
	public void setHideHeaderBar(Boolean hideHeaderBar) {
		this.hideHeaderBar = hideHeaderBar;
		treePortlet.addOption(FastTreePortlet.OPTION_HEADER_BAR_HIDDEN, hideHeaderBar);
	}

	public String getColumnHeaderBgColor() {
		return columnHeaderBgColor;
	}

	public void setColumnHeaderBgColor(String columnHeaderBgColor) {
		treePortlet.addOption(FastTreePortlet.OPTION_COLUMN_HEADER_BG_COLOR, "_bg=" + columnHeaderBgColor);
		this.columnHeaderBgColor = columnHeaderBgColor;
	}

	public String getColumnHeaderFontColor() {
		return columnHeaderFontColor;
	}

	public void setColumnHeaderFontColor(String columnHeaderFontColor) {
		treePortlet.addOption(FastTreePortlet.OPTION_COLUMN_HEADER_FONT_COLOR, "_fg=" + columnHeaderFontColor);
		this.columnHeaderFontColor = columnHeaderFontColor;
	}

	public String getFilteredHeaderFontColor() {
		return filteredHeaderFontColor;
	}

	public void setFilteredHeaderFontColor(String filteredHeaderFontColor) {
		treePortlet.addOption(FastTreePortlet.OPTION_FILTERED_FONT_COLOR, "_fg=" + filteredHeaderFontColor);
		this.filteredHeaderFontColor = filteredHeaderFontColor;
	}
	public String getFilteredHeaderBgColor() {
		return filteredHeaderBgColor;
	}

	public void setFilteredHeaderBgColor(String filteredHeaderBgColor) {
		treePortlet.addOption(FastTreePortlet.OPTION_FILTERED_COLUMN_BG, "_bg=" + filteredHeaderBgColor);
		this.filteredHeaderBgColor = filteredHeaderBgColor;
	}

	public String getCellBorderColor() {
		return cellBorderColor;
	}

	public void setCellBorderColor(String cellBorderColor) {
		this.cellBorderColor = cellBorderColor;
		treePortlet.addOption(FastTreePortlet.OPTION_CELL_BORDER_STYLE, cellBorderColor);
	}
	public void setVerticalAlign(String verticalAlign) {
		this.treePortlet.addOption(FastTreePortlet.OPTION_VERTICAL_ALIGN, verticalAlign);
	}
	public void setCellBottomDivider(Integer size) {
		this.treePortlet.addOption(FastTreePortlet.OPTION_CELL_BOTTOM_DIVIDER, size);
	}
	public void setCellRightDivider(Integer size) {
		this.treePortlet.addOption(FastTreePortlet.OPTION_CELL_RIGHT_DIVIDER, size);
	}

	public void setCellPaddingHorizontal(Integer size) {
		this.treePortlet.addOption(FastTablePortlet.OPTION_CELL_PADDING_HORIZONTAL, size);
	}

	private void setHeaderFontSize(Integer headerFontSize) {
		// we don't have auto-sizing option (column) in tree yet...
		//		Integer headerSize = OH.cast(this.treePortlet.getOption(FastTreePortlet.OPTION_HEADER_ROW_HEIGHT), Integer.class);
		//		if (headerSize == DEFAULT_HEADER_ROW_HEIGHT) {
		//			if (headerFontSize < 13) {
		//				this.treePortlet.addOption(FastTreePortlet.OPTION_HEADER_ROW_HEIGHT, 18);
		//				this.treePortlet.getTree().setHeaderRowHeight(18);
		//			} else {
		//				this.treePortlet.addOption(FastTreePortlet.OPTION_HEADER_ROW_HEIGHT, headerFontSize + 5);
		//				this.treePortlet.getTree().setHeaderRowHeight(headerFontSize + 5);
		//			}
		//		}

		this.treePortlet.addOption(FastTreePortlet.OPTION_HEADER_FONT_SIZE, headerFontSize);
	}
	private void setHeaderRowHeight(Integer headerRowHeight) {
		this.treePortlet.addOption(FastTreePortlet.OPTION_HEADER_ROW_HEIGHT, headerRowHeight);
		if (headerRowHeight != null)
			this.treePortlet.getTree().setHeaderRowHeight(headerRowHeight);
	}

	@Override
	public void setFontSize(Integer fontSize) {
		this.treePortlet.addOption(FastTreePortlet.OPTION_FONT_SIZE, fontSize);
		super.setFontSize(fontSize);
		onFontRowHeightChanged();
	}

	@Override
	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
		this.treePortlet.addOption(FastTreePortlet.OPTION_FONT_FAMILY, "_fm=" + fontFamily);
		super.setFontFamily(fontFamily);
	}

	public String getFontFamily() {
		return this.fontFamily;
	}
	public void setRowHeight(Integer height) {
		this.rowHeight = height;
		onFontRowHeightChanged();
	}

	//Quick filter
	private boolean quickColumnFilterHidden;
	private int quickColumnFilterHeight;
	private String quickColumnFilterBgCl;
	private String quickColumnFilterFontCl;
	private int quickColumnFontSize;
	private String quickColumnFilterBdrCl;

	public void setQuickColumnFilterHidden(boolean isHidden) {
		this.treePortlet.addOption(FastTablePortlet.OPTION_QUICK_COLUMN_FILTER_HIDDEN, isHidden);
		this.quickColumnFilterHidden = isHidden;
	}
	public Boolean getQuickColumnFilterHidden() {
		return this.quickColumnFilterHidden;
	}
	public void setQuickColumnFilterHeight(Integer quickColFilterHeight) {
		this.treePortlet.addOption(FastTablePortlet.OPTION_QUICK_COLUMN_FILTER_HEIGHT, quickColFilterHeight);
		this.quickColumnFilterHeight = quickColFilterHeight;
	}
	public int getQuickColumnFilterHeight() {
		return this.quickColumnFilterHeight;
	}
	public void setQuickColumnFilterBgCl(String color) {
		this.treePortlet.addOption(FastTablePortlet.OPTION_QUICK_COLUMN_FILTER_BG_CL, color);
		this.quickColumnFilterBgCl = color;
	}
	public String getQuickColumnFilterBgCl() {
		return this.quickColumnFilterBgCl;
	}
	public void setQuickColumnFilterFontCl(String color) {
		this.treePortlet.addOption(FastTablePortlet.OPTION_QUICK_COLUMN_FILTER_FONT_CL, color);
		this.quickColumnFilterFontCl = color;
	}
	public String getQuickColumnFilterFontCl() {
		return this.quickColumnFilterFontCl;
	}
	public void setQuickColumnFilterFontSize(int fontSz) {
		this.treePortlet.addOption(FastTablePortlet.OPTION_QUICK_COLUMN_FILTER_FONT_SZ, fontSz);
		this.quickColumnFontSize = fontSz;
	}
	public int getQuickColumnFilterFontSize() {
		return this.quickColumnFontSize;
	}
	public void setQuickColumnFilterBdrCl(String color) {
		this.treePortlet.addOption(FastTablePortlet.OPTION_QUICK_COLUMN_FILTER_BDR_CL, color);
		this.quickColumnFilterBdrCl = color;
	}
	public String getQuickColumnFilterBdrCl() {
		return this.quickColumnFilterBdrCl;
	}
	//End Quick filter

	private void onFontRowHeightChanged() {
		if (this.rowHeight != null && this.getFontSize() != null) {
			int calcRowHeight = Math.max(this.getFontSize() + 5, this.rowHeight);
			this.treePortlet.addOption(FastTreePortlet.OPTION_ROW_HEIGHT, calcRowHeight);
			this.treePortlet.getTree().setRowHeight(calcRowHeight);
		}
	}

	@Override
	public void getUsedColors(Set<String> sink) {
		AmiWebUtils.getColors(this.bgColor, sink);
		AmiWebUtils.getColors(this.fontColor, sink);
		AmiWebUtils.getColors(this.columnHeaderBgColor, sink);
		AmiWebUtils.getColors(this.cellBorderColor, sink);
		AmiWebUtils.getColors(this.columnHeaderFontColor, sink);
		AmiWebUtils.getColors(this.filteredHeaderBgColor, sink);
		AmiWebUtils.getColors(this.filteredHeaderFontColor, sink);
		AmiWebUtils.getColors(this.grayBarColor, sink);
		AmiWebUtils.getColors(this.gripColor, sink);
		AmiWebUtils.getColors(this.scrollButtonColor, sink);
		AmiWebUtils.getColors(this.searchBarBgColor, sink);
		AmiWebUtils.getColors(this.searchBarFieldFgColor, sink);
		//		IterableAndSize<FastWebTreeColumn> columns = this.getTree().getColumns();
		for (AmiWebTreeColumn i : this.columnsByAmiId.values()) {
			AmiWebTreeGroupByFormatter c = i.getFormatter();
			AmiWebUtils.getColors(c.getBackgroundColor(false), sink);
			AmiWebUtils.getColors(c.getColor(false), sink);
		}
		for (AmiWebTreeGroupBy i : this.groupbyFormulas.values()) {
			AmiWebTreeGroupByFormatter c = i.getFormatter();
			AmiWebUtils.getColors(i.getRowBackgroundColor(false), sink);
			AmiWebUtils.getColors(i.getRowColor(false), sink);
			AmiWebUtils.getColors(c.getBackgroundColor(false), sink);
			AmiWebUtils.getColors(c.getColor(false), sink);
		}
	}
	@Override
	public void onCellMousedown(FastWebTree tree, WebTreeNode row, FastWebTreeColumn col) {
	}

	public AmiWebTreeColumn getColumnFormatter(int id) {
		return this.columnsById.get(id);
	}
	@Override
	public String getStyleType() {
		return AmiWebStyleTypeImpl_Treegrid.TYPE_TREEGRID;

	}

	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
		if ("callRelationship".equals(action)) {
			for (AmiWebDmLink link : getDmLinksFromThisPortlet()) {
				if (MH.allBits(link.getOptions(), AmiWebDmLink.OPTION_ON_USER_DBL_CLICK)) {
					AmiWebDmUtils.sendRequest(getService(), link);
				}
			}
		}

	}

	@Override
	public WebMenu createMenu(FastWebTree fastWebTree, List<WebTreeNode> selected) {
		BasicWebMenu m = new BasicWebMenu();
		addCustomMenuItems(m);
		if (selected.size() > 0) {
			BasicMultiMap.List<String, String> title2portletId = new BasicMultiMap.List<String, String>();
			for (AmiWebDmLink link : getDmLinksFromThisPortlet()) {
				if (link.isRunOnSelect() || link.isRunOnAmiScript() || link.isRunOnDoubleClick())
					continue;
				for (String s : SH.split('|', link.getTitle())) {
					title2portletId.putMulti(SH.trimWhitespace(s), link.getLinkUid());
				}
			}
			for (String s : CH.sort(title2portletId.keySet())) {
				m.add(new BasicWebMenuLink(s, true, SH.join('_', title2portletId.get(s), new StringBuilder("query_")).toString()));
			}
		}
		return m;
	}

	@Deprecated
	@Override
	public boolean formatNode(WebTreeNode node, StringBuilder sink) {
		return false;
	}

	public FastWebTreeColumn findColumnByName(String columnName) {
		for (FastWebTreeColumn col : this.getTree().getColumns()) {
			if (col.getColumnName().equals(columnName))
				return col;
		}
		return null;
	}

	public int findColumnIdByName(String colName) {
		for (FastWebTreeColumn col : this.getTree().getColumns()) {
			if (col.getColumnName().equals(colName))
				return col.getColumnId();
		}
		return -1;
	}

	abstract public com.f1.base.CalcTypes getClassTypes();

	public int getAggColumnsCacheSize() {
		return this.aggregateFactory.getAggregatesCount();
	}

	final protected CalcFrame getTmpValuesMap() {
		return this.tmpValuesMap;
	}

	final protected void setCurrentNode(WebTreeNode node) {
		if (this.currentNode == node)
			return;
		this.currentNode = node;
		CalcFrame lcvs;
		try {
			lcvs = onCurrentNodeChanged(node);
		} catch (Exception e) {
			LH.warning(log, logMe(), "Critical error: ", e);
			lcvs = emptyCalcFrame();
		}
		tmpValuesMap.setFrame1(lcvs);
		tmpValue_CHECKED.setValue(node.getChecked() ? TWO : node.getAllChildrenCheckedCount() > 0 ? ONE : ZERO);
		tmpValue_EXPANDED.setValue(node.getIsExpanded());
		tmpValue_SIZE.setValue(OH.valueOf(node.getChildrenCount()));
		if (node.getDepth() == 1) {
			tmpValue_LEAVES.setValue(node.getAllChildrenCount() - node.getChildrenCount() - 1);
		} else {
			tmpValue_LEAVES.setValue(node.getChildrenCount());
		}
		tmpValue_RSIZE.setValue(OH.valueOf(node.getAllChildrenCount() - 1));
		tmpValue_DEPTH.setValue(OH.valueOf(node.getDepth()));

	}

	protected abstract CalcFrame onCurrentNodeChanged(WebTreeNode node);

	public WhereClause compileWhereFilter(String value, StringBuilder errorSink) {
		return this.where.compileWhereFilter(value, errorSink, this.getClassTypes());
	}

	//	public void setDefaultWhereFilter(String value) {
	//		this.where.setDefaultWhereFilter(value);
	//	}

	public void setCurrentRuntimeFilter(String value, boolean override) {
		this.where.setCurrentRuntimeFilter(value, override);
	}

	public abstract void onWhereFormulaChanged();

	public String getDefaultWhereFilter() {
		return this.where.getDefaultWhereFilter();
	}

	public String getCurrentRuntimeFilter() {
		return this.where.getCurrentRuntimeFilter();
	}

	public boolean shouldKeep(CalcFrame o) {
		return this.where.meetsWhereFilter(o, getStackFrame());
	}
	@Override
	public void onNodeAdded(WebTreeNode node) {
		resetCurrentNode();

	}

	protected WebTreeNode currentNode;
	private String scrollBarCornerColor;

	protected void resetCurrentNode() {
		this.currentNode = null;
	}

	@Override
	public void onNodeRemoved(WebTreeNode node) {
		resetCurrentNode();

	}

	@Override
	public void onStyleChanged(WebTreeNode node) {
		resetCurrentNode();

	}

	@Override
	public void onExpanded(WebTreeNode node) {
		resetCurrentNode();
	}

	@Override
	public void onFilteredChanged(WebTreeNode child, boolean isFiltered) {
		resetCurrentNode();
	}

	@Override
	public void onCheckedChanged(WebTreeNode node) {
		flagPendingAjax();
		resetCurrentNode();
		amiSelectionChanged = true;
	}

	public AmiWebTreePortletFormatter getTreePortletFormatter() {
		return this.treePortletFormatter;
	}

	public boolean resetWhere() {
		boolean r = this.where.resetWhere();
		if (r)
			onWhereFormulaChanged();
		return r;
	}

	public void setSelectColor(String selectColor) {
		this.treePortlet.addOption(FastTablePortlet.OPTION_SELECTED_BG, selectColor);
		this.selectColor = selectColor;
	}
	public String getSelectColor() {
		return this.selectColor;
	}
	public void setActiveSelectColor(String activeSelectColor) {
		this.treePortlet.addOption(FastTablePortlet.OPTION_ACTIVE_BG, activeSelectColor);
		this.activeSelectColor = activeSelectColor;
	}
	public String getActiveSelectColor() {
		return this.activeSelectColor;
	}

	public AmiWebTreeColumn getColumnByAmiId(String columnId) {
		return this.columnsByAmiId.get(columnId);
	}

	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		List<AmiWebDomObject> r = super.getChildDomObjects();
		CH.addAll(r, this.groupbyFormulas.values());
		r.addAll(this.columnsByAmiId.values());
		return r;
	}

	@Override
	public void updateAri() {
		super.updateAri();
		for (AmiWebTreeGroupBy i : this.groupbyFormulas.values())
			i.updateAri();
		for (AmiWebTreeColumn i : this.columnsByAmiId.values())
			i.updateAri();
	}

	@Override
	public void onColumnsArranged(FastWebTree fastWebTable) {
		this.callbacks.execute(CALLBACK_DEF_ONCOLUMNSARRANGED.getMethodName());
	}

	@Override
	public void onColumnsSized(FastWebTree fastWebTable) {
		this.callbacks.execute(CALLBACK_DEF_ONCOLUMNSSIZED.getMethodName());
	}

	@Override
	public void onFilterChanging(FastWebTree fastWebTable) {
		if (isInitDone())
			this.callbacks.execute(CALLBACK_DEF_ONFILTERCHANGING.getMethodName());
	}

	public void flagRebuildCalcs() {
		clearAmiData();
		this.rebuildCalcs();
		this.rebuildAmiData();
		this.flagPendingAjax();
	}
	@Override
	public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula f) {
		com.f1.base.CalcTypes r = super.getFormulaVarTypes(f);
		return new com.f1.utils.structs.table.stack.CalcTypesTuple2(r, VAR_TYPES);
	}
	@Override
	public void removeFromDomManager() {
		for (AmiWebTreeGroupBy i : this.groupbyFormulas.values())
			i.removeFromDomManager();
		for (AmiWebTreeColumn i : this.columnsByAmiId.values())
			i.removeFromDomManager();
		super.removeFromDomManager();
	}

	public boolean hasLeafGrouping() {
		int last = this.groupbyFormulas.getSize() - 1;
		return this.groupbyFormulas.getAt(last).isLeaf();
	}

	@Override
	public Map<String, Object> getUserPref() {
		Map<String, Object> r = super.getUserPref();
		FastWebTree origTree = getTree();
		putSort(r);

		{
			// column ordering and width
			List<Map<String, Object>> columns = new ArrayList<Map<String, Object>>();
			for (int i = 0; i <= origTree.getVisibleColumnsCount(); i++) {
				FastWebTreeColumn col = origTree.getVisibleColumn(i);
				LinkedHashMap<String, Object> m = CH.m(new LinkedHashMap<String, Object>(), "name", col.getColumnName(), "width", col.getWidth());
				columns.add(m);
			}
			r.put("VisibleColumns", columns);
		}

		Set<Integer> filteredInColumns = origTree.getFilteredInColumns();
		if (!filteredInColumns.isEmpty()) {
			Map<String, Map<String, Object>> filtersMap = new LinkedHashMap<String, Map<String, Object>>();
			for (Integer filteredInColumn : filteredInColumns) {
				FastWebTreeColumn col = origTree.getColumn(filteredInColumn);
				WebTreeFilteredInFilter f = origTree.getFiltererdIn(filteredInColumn);
				if (f != null) {
					Map<String, Object> m = new HashMap<String, Object>();
					Set<String> values = new HashSet<String>(f.getValues());
					m.put("values", values);
					if (!f.getKeep()) {
						m.put("hide", true);
					}
					if (f.getIsPattern()) {
						m.put("pattern", true);
					}
					String min = f.getMin();
					if (min != null) {
						m.put("min", min);
					}
					String max = f.getMax();
					if (max != null) {
						m.put("max", max);
					}
					m.put("maxInclusive", f.getMaxInclusive());
					m.put("minInclusive", f.getMinInclusive());
					filtersMap.put(col.getColumnName(), m);
				}
			}
			r.put("Filters", filtersMap);
		}
		r.put("PinnedColumnCount", origTree.getPinnedColumnsCount());
		return r;
	}
	@Override
	public void applyUserPref(Map<String, Object> values) {
		FastWebTree origTree = getTree();
		Map<String, Integer> columnsByName = new HashMap<String, Integer>();
		IntIterator it = origTree.getColumnIds();
		while (it.hasNext()) {
			FastWebTreeColumn col2 = origTree.getColumn(it.next());
			columnsByName.put(col2.getColumnName(), col2.getColumnId());
		}
		prepareSort(values, columnsByName);
		applySort();

		{
			// column ordering and width
			List<Map> visibleCols = (List<Map>) values.get("VisibleColumns");
			if (visibleCols != null) {
				List<Integer> visibleColIds = new ArrayList<Integer>();
				for (int i = 0; i <= origTree.getVisibleColumnsCount(); i++) {
					FastWebTreeColumn col = origTree.getVisibleColumn(i);
					visibleColIds.add(col.getColumnId());
				}
				for (Integer id : visibleColIds) {
					if (id == origTree.getTreeColumn().getColumnId())
						continue;
					origTree.hideColumn(id);
				}

				int loc = 0;
				for (Map prefCol : visibleCols) {
					String name = CH.getOrThrow(Caster_String.INSTANCE, prefCol, "name");
					int width = (int) CH.getOrThrow(Caster_Integer.INSTANCE, prefCol, "width");
					Integer colId = columnsByName.get(name);
					if (colId == null) { // grouping col, only set width, showCol is not necessary
						origTree.getTreeColumn().setWidth(width);
						continue;
					}
					origTree.showColumn(colId, loc);
					FastWebTreeColumn treeCol = origTree.getColumn(colId);
					treeCol.setWidth(width);
					loc++;
				}
			}
		}

		Set<Integer> existingFilters = new HashSet<Integer>(origTree.getFilteredInColumns());
		Map<String, Map<String, Object>> filteredIn = (Map) values.get("Filters");
		if (CH.isntEmpty(filteredIn)) {
			FastWebTreeColumn groupingColumn = origTree.getTreeColumn();
			for (Entry<String, Map<String, Object>> filteredInColumn : filteredIn.entrySet()) {
				String columnName = filteredInColumn.getKey();
				Integer colID = columnsByName.get(columnName);
				if (columnName == groupingColumn.getColumnName())
					colID = groupingColumn.getColumnId();
				if (colID != null) {
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
					origTree.setFilteredIn(colID, fvalues, !hide, includeNull, pattern, min, minInclusive == "true", max, maxInclusive == "true");

					existingFilters.remove(colID);
				}
			}
		}

		for (Integer col : existingFilters) {
			origTree.setFilteredIn(col, (Set<String>) null);
		}

		origTree.setPinnedColumnsCount(CH.getOr(Caster_Integer.INSTANCE, values, "PinnedColumnCount", 0));
		origTree.fireOnColumnsArranged();
		super.applyUserPref(values);
	}

	@Override
	public void setScrollBarHideArrows(Boolean hide) {
		this.treePortlet.addOption(FastTreePortlet.OPTION_SCROLL_BAR_HIDE_ARROWS, hide);
		this.scrollBarHideArrows = hide;
	}

	@Override
	public void setScrollBarCornerColor(String color) {
		this.treePortlet.addOption(FastTreePortlet.OPTION_SCROLL_BAR_CORNER_COLOR, color);
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

	public IndexedList<String, AmiWebTreeGroupBy> getGroupbyFormulas() {
		return groupbyFormulas;
	}

	public Set<String> getColumnAmiIds() {
		return this.columnsByAmiId.keySet();
	}

	public BasicWebTreeManager getBasicWebTreeManager() {
		return (BasicWebTreeManager) this.treeManager;
	}

	@Override
	public void close() {
		super.close();
	}

	public CalcFrameStack getTmpValuesStackFrame() {
		return getStackFrame().reset(this.getTmpValuesMap());
	}

}
