package com.f1.ami.web.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebAbstractPortlet;
import com.f1.ami.web.AmiWebAbstractPortletBuilder;
import com.f1.ami.web.AmiWebDmPortlet;
import com.f1.ami.web.AmiWebDmPortletBuilder;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebFormulasListener;
import com.f1.ami.web.AmiWebOverrideValue;
import com.f1.ami.web.AmiWebPanelSettingsPortlet;
import com.f1.ami.web.AmiWebStyledScrollbarPortlet;
import com.f1.ami.web.AmiWebUsedDmSingleton;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmError;
import com.f1.ami.web.dm.AmiWebDmFilter;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmManager;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.portlets.AmiWebDmUtils;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyledPortlet;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Filter;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Byte;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.impl.RowHasher;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class AmiWebFilterPortlet extends AmiWebAbstractPortlet
		implements AmiWebDmPortlet, AmiWebDmFilter, AmiWebStyledPortlet, AmiWebStyledScrollbarPortlet, AmiWebFormulasListener {

	public static final byte DISPLAY_TYPE_CHECKBOXES = 1;
	public static final byte DISPLAY_TYPE_SELECT_SINGLE = 2;
	public static final byte DISPLAY_TYPE_RANGE = 3;
	public static final byte DISPLAY_TYPE_SEARCH = 4;
	public static final byte DISPLAY_TYPE_RANGE_SLIDER = 5;
	public static final byte DISPLAY_TYPE_MULTICHECKBOX = 6;
	public static final byte DISPLAY_TYPE_RADIOS = 7;

	public static final String VARNAME_VALUE = "value";
	final private AmiWebUsedDmSingleton dmSingleton;
	private AmiWebFilterFormPortlet formPortlet;
	private boolean isApplyToSourceTable = true;
	private boolean clearOnRequery = true;
	private IntKeyMap<AmiWebFilterLink> links = new IntKeyMap<AmiWebFilterLink>();
	private Class<?> dataType;

	private Table table;
	private AmiWebFormula displayFormula;
	private AmiWebFormula colorFormula;
	private AmiWebFormula sortFormula;
	private Integer scrollBorderRadius;

	private static final Logger log = LH.get();

	private AmiWebOverrideValue<Integer> maxOptions = new AmiWebOverrideValue<Integer>(500);
	private AmiWebOverrideValue<Byte> displayType = new AmiWebOverrideValue<Byte>((byte) 0);

	public AmiWebFilterPortlet(PortletConfig config) {
		super(config);
		this.displayFormula = this.formulas.addFormula("display", Object.class);
		this.colorFormula = this.formulas.addFormula("color", String.class);
		this.sortFormula = this.formulas.addFormula("sort", Comparable.class);
		this.dmSingleton = new AmiWebUsedDmSingleton(this.getService().getDmManager(), this);
		this.formulas.addFormulasListener(this);
		getStylePeer().initStyle();
	}

	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		this.displayFormula.initFormula(CH.getOr(Caster_String.INSTANCE, configuration, "fdis", null));
		this.colorFormula.initFormula(CH.getOr(Caster_String.INSTANCE, configuration, "fclr", null));
		this.sortFormula.initFormula(CH.getOr(Caster_String.INSTANCE, configuration, "fsrt", null));
		this.isApplyToSourceTable = CH.getOr(Caster_Boolean.INSTANCE, configuration, "apply", this.isApplyToSourceTable);
		this.clearOnRequery = CH.getOr(Caster_Boolean.INSTANCE, configuration, "clearOnRequery", true);
		List<Map<String, Object>> linksList = (List<Map<String, Object>>) CH.getOr(Caster_Simple.OBJECT, configuration, "links", Collections.EMPTY_LIST);
		for (Map<String, Object> i : linksList) {
			AmiWebFilterLink link = new AmiWebFilterLink(i, this);
			this.links.put(link.getId(), link);
			AmiWebDm t = getService().getDmManager().getDmByAliasDotName(link.getTargetDmAliasDotName());
			if (t != null)
				t.addFilter(link);
		}

		dmSingleton.init(getAmiLayoutFullAlias(), configuration);
		AmiWebDmManager dmManager = getService().getDmManager();
		AmiWebDm existing = dmManager.getDmByAliasDotName(dmSingleton.getDmAliasDotName());
		if (existing != null)
			existing.addFilter(this);
		setDisplayType(CH.getOr(Caster_Byte.INSTANCE, configuration, "dt", DISPLAY_TYPE_CHECKBOXES), false);
		dmManager.onFilterDependencyChanged(this, dmSingleton.getDmAliasDotName(), dmSingleton.getDmTableName(), true);
		for (AmiWebFilterLink i : this.getLinks().values()) {
			dmManager.onFilterDependencyChanged(this, i.getTargetDmAliasDotName(), i.getDmTableName(), true);
		}

		setMaxOptions(CH.getOr(Caster_Integer.INSTANCE, configuration, "maxcb", needMaxOptions() ? 500 : -1), false);
		super.init(configuration, origToNewIdMapping, sb);
	}

	@Override
	public void onAmiInitDone() {
		super.onAmiInitDone();
		this.onDmDataBeforeFilterChanged(null);
	}

	@Override
	public void setAmiTitle(String title, boolean isOverride) {
		super.setAmiTitle(title, isOverride);
		if (this.formPortlet != null)
			this.formPortlet.setFilterTitle(title);
	}

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		dmSingleton.getConfiguration(getAmiLayoutFullAlias(), r);
		CH.putNoNull(r, "fdis", this.displayFormula.getFormulaConfig());
		CH.putNoNull(r, "fclr", this.colorFormula.getFormulaConfig());
		CH.putNoNull(r, "fsrt", this.sortFormula.getFormulaConfig());
		CH.putNoNull(r, "apply", this.isApplyToSourceTable);
		CH.putNoNull(r, "dt", this.getDisplayType(false));
		CH.putNoNull(r, "maxcb", this.getMaxOptions(false));
		List<Map<String, Object>> links = new ArrayList<Map<String, Object>>();
		for (AmiWebFilterLink i : this.links.values())
			links.add(i.getConfiguration());
		r.put("links", links);
		r.put("clearOnRequery", this.clearOnRequery);
		return r;
	}
	@Override
	public String getConfigMenuTitle() {
		return "Filter";
	}

	@Override
	public boolean getIsFreeFloatingPortlet() {
		return true;
	}

	public static class Builder extends AmiWebAbstractPortletBuilder<AmiWebFilterPortlet> implements AmiWebDmPortletBuilder<AmiWebFilterPortlet> {

		public static final String OLD_ID = "filter";
		public static final String ID = "amifilter";

		public Builder() {
			super(AmiWebFilterPortlet.class);
		}

		@Override
		public AmiWebFilterPortlet buildPortlet(PortletConfig portletConfig) {
			AmiWebFilterPortlet r = new AmiWebFilterPortlet(portletConfig);
			return r;
		}

		@Override
		public String getPortletBuilderName() {
			return "Filter";
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

	@Override
	public void clearAmiData() {
	}

	@Override
	public String getPanelType() {
		return "filter";
	}

	@Override
	public void clearUserSelection() {
		this.formPortlet.clearSelectedRows();
		onValuesChanged();
	}

	@Override
	public boolean isRealtime() {
		return false;
	}

	@Override
	public String getStyleType() {
		return AmiWebStyleTypeImpl_Filter.TYPE_FILTER;
	}
	@Override
	public void onStyleValueChanged(short key, Object old, Object nuw) {
		super.onStyleValueChanged(key, old, nuw);
		if (this.formPortlet == null)
			return;
		switch (key) {
			case AmiWebStyleConsts.CODE_BG_CL:
				this.formPortlet.setBackgroundColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_FLD_FONT_CL:
				this.formPortlet.setFieldsFontColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_FLD_BG_CL:
				this.formPortlet.setFieldsBackgroundColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_FONT_CL:
				this.formPortlet.setFontColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_FONT_SZ:
				this.formPortlet.setFontSize(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_TXT_ALIGN:
				this.formPortlet.setTitleAlignment(Caster_String.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_FONT_FAM:
				this.formPortlet.setFontFamily((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_TITLE_FONT_CL:
				this.formPortlet.setTitleFontColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_TITLE_FONT_SZ:
				this.formPortlet.setTitleFontSize(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_TITLE_FONT_FAM:
				this.formPortlet.setTitleFontFamily(Caster_String.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_BOLD:
				this.formPortlet.setTitleBold(Caster_Boolean.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_ITALIC:
				this.formPortlet.setTitleItalic(Caster_Boolean.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_UNDERLINE:
				this.formPortlet.setTitleUnderline(Caster_Boolean.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_FLD_BDR_CL:
				this.formPortlet.setFieldBorderColor((String) nuw);
				break;
		}
	}

	@Override
	public void getUsedColors(Set<String> sink) {
		AmiWebUtils.getColors(this.formPortlet.getBackgroundColor(), sink);
		AmiWebUtils.getColors(this.formPortlet.getFieldsFontColor(), sink);
		AmiWebUtils.getColors(this.formPortlet.getFieldsBackgroundColor(), sink);
		AmiWebUtils.getColors(this.formPortlet.getFontColor(), sink);
		AmiWebUtils.getColors(this.formPortlet.getTitleFontColor(), sink);
		AmiWebUtils.getColors(this.formPortlet.getFieldBorderColor(), sink);
	}

	public String getFormatExpression(boolean override) {
		return this.displayFormula.getFormula(override);
	}
	public String getColorExpression(boolean override) {
		return this.colorFormula.getFormula(override);
	}
	public String getSortExpression(boolean override) {
		return this.sortFormula.getFormula(override);
	}

	public boolean setExpressions(String displayExpression, String sortExpression, String colorExpression, IntKeyMap<AmiWebFilterLink> links, StringBuilder sb) {
		//		AmiWebDmTableSchema dm = this.getDm();
		AmiWebUtils.testFormula(this.displayFormula, displayExpression, sb);
		AmiWebUtils.testFormula(this.sortFormula, sortExpression, sb);
		AmiWebUtils.testFormula(this.colorFormula, colorExpression, sb);
		if (sb.length() > 0)
			return false;
		//		DerivedCellCalculator dsExp = parse(dm, displayExpression, sb, Comparable.class, FORMULA_DISPLAY);
		//		DerivedCellCalculator coExp = parse(dm, colorExpression, sb, String.class, FORMULA_COLOR);
		//		DerivedCellCalculator soExp = parse(dm, sortExpression, sb, Comparable.class, FORMULA_SORT);
		//		Set<Object> distinctOnVars = new HashSet<Object>();
		//		DerivedHelper.getDependencyIds(dsExp, distinctOnVars);
		//		DerivedHelper.getDependencyIds(coExp, distinctOnVars);
		//		if (soExp != null) {
		//			if (!Comparable.class.isAssignableFrom(soExp.getReturnType())) {
		//				sb.append("Sort field formula must return values that can sorted");
		//				return false;
		//			}
		//			Set<Object> notAvailable = CH.comm(distinctOnVars, DerivedHelper.getDependencyIds(soExp), false, true, false);
		//			if (!notAvailable.isEmpty()) {
		//				sb.append("Sort field can only use variables participating in Display and/or Color fields.  Can not reference: " + SH.join(',', notAvailable));
		//				return false;
		//			}
		//		}
		AmiWebDmManager dmManager = getService().getDmManager();
		for (AmiWebFilterLink i : this.links.values()) {
			AmiWebDm t = getService().getDmManager().getDmByAliasDotName(i.getTargetDmAliasDotName());
			if (t != null) {
				t.removeFilter(i);
				dmManager.onFilterDependencyChanged(this, i.getTargetDmAliasDotName(), i.getDmTableName(), false);
			}
		}
		this.links.clear();
		this.links.addAll(links);
		for (AmiWebFilterLink i : this.links.values()) {
			AmiWebDm t = getService().getDmManager().getDmByAliasDotName(i.getTargetDmAliasDotName());
			if (t != null) {
				t.addFilter(i);
				dmManager.onFilterDependencyChanged(this, i.getTargetDmAliasDotName(), i.getDmTableName(), true);
			}
		}
		this.displayFormula.setFormula(displayExpression, false);
		this.sortFormula.setFormula(sortExpression, false);
		this.colorFormula.setFormula(colorExpression, false);
		this.formPortlet.setOptions("", Collections.EMPTY_LIST);
		return true;
	}

	public HasherSet<Row> getSelectedRows() {
		return formPortlet == null ? null : this.formPortlet.getSelectedRows();
	}

	//	private DerivedCellCalculator parse(AmiWebDmTableSchema dm, String ex, StringBuilder sb, Class returnType, String formulaId) {
	//		if (SH.isnt(ex))
	//			return null;
	//		DerivedCellCalculator t = getScriptManager().parseAmiScript(ex, dm.getClassTypes(), sb, this.getService().getDebugManager(), AmiDebugMessage.TYPE_FORMULA, this, formulaId,
	//				false);
	//
	//		if (t == null)
	//			return null;
	//		if (!returnType.isAssignableFrom(t.getReturnType())) {
	//			if (sb != null)
	//				sb.append(formulaId).append(" must evaluate to a ").append(returnType.getSimpleName());
	//			return null;
	//		}
	//		return t;
	//
	//	}
	@Override
	public void onDmDataChanged(AmiWebDm datamodel) {
	}
	@Override
	public void onDmDataBeforeFilterChanged(AmiWebDm datamodel) {
		AmiWebDmTableSchema dm = this.getDm();
		if (dm == null)
			return;
		String title = this.getAmiTitle(false);
		DerivedCellCalculator dsExp = displayFormula.getFormulaCalc();//parse(dm, displayExpression, null, Comparable.class, FORMULA_DISPLAY);
		DerivedCellCalculator soExp = sortFormula.getFormulaCalc();//parse(dm, sortExpression, null, Comparable.class, FORMULA_SORT);
		DerivedCellCalculator coExp = colorFormula.getFormulaCalc();//parse(dm, colorExpression, null, String.class, FORMULA_COLOR);
		Set<Object> distinctOnVars = new HashSet<Object>();
		DerivedHelper.getDependencyIds(dsExp, distinctOnVars);
		DerivedHelper.getDependencyIds(coExp, distinctOnVars);

		if (SH.isnt(title)) {
			StringBuilder sb = new StringBuilder();
			for (Object s : distinctOnVars) {
				if (sb.length() > 0)
					sb.append(" - ");
				AmiWebUtils.toPrettyName((String) s, sb);
			}
			title = sb.toString();

		}
		if (dm.getDm() == null || dsExp == null) {
			this.formPortlet.setOptions(title, Collections.EMPTY_LIST);
			this.table = null;
			return;
		}
		// populate options
		this.table = dm.getDm().getResponseTablesetBeforeFilter().getTableNoThrow(dm.getName());
		if (table != null) {
			ReusableCalcFrameStack sf = getStackFrame();
			try {
				LinkedHashMap<Tuple2<String, String>, Option> options = new LinkedHashMap<Tuple2<String, String>, Option>();
				Tuple2<String, String> tmp = new Tuple2<String, String>();
				for (Row row : table.getRows()) {
					sf.reset(row);
					Comparable<?> display = (Comparable<?>) eval(dsExp, sf);
					String displayTxt = SH.s(display);
					String color = (String) eval(coExp, sf);
					tmp.setAB(displayTxt, color);
					Option existing = options.get(tmp);
					if (existing == null) {
						Comparable<?> sort = (Comparable<?>) eval(soExp, sf);
						if (sort == null)
							sort = display;
						options.put(tmp, existing = new Option(tmp, display, sort, (String) eval(coExp, sf)));
						tmp = new Tuple2<String, String>();
					}
					existing.rows.add(row);
				}
				this.formPortlet.setOptions(title, CH.sort(options.values()));
			} catch (Exception e) {
				LH.warning(log, "Error evaluating for filter: ", this.toDerivedString(), e);
				this.formPortlet.setOptions("", new ArrayList<AmiWebFilterPortlet.Option>());
				this.formPortlet.clearSelectedRows();
			}
		} else {
			this.formPortlet.setOptions(title, Collections.EMPTY_LIST);
		}
		//		getDm().getDm().reprocessFilters(getDm().getName());
		if (this.clearOnRequery) {
			this.formPortlet.clearSelectedRows();
		}
	}
	private Object eval(DerivedCellCalculator c, ReusableCalcFrameStack sf) {
		return c == null ? null : c.get(sf);
	}

	public static class Option implements Comparable<Option> {
		final private String display;
		final private Comparable<?> sort;
		final private String style;
		final private HasherSet<Row> rows = new HasherSet<Row>(RowHasher.INSTANCE);
		final private Tuple2<String, String> key;

		public Option(Tuple2<String, String> key, Comparable<?> display, Comparable<?> sort, String color) {
			this.key = key;
			this.key.lock();
			this.display = SH.s(display);
			this.sort = sort;
			if (color != null) {
				this.style = "_fg=" + color;
			} else
				this.style = null;
		}
		@Override
		public int compareTo(Option o) {
			int r = OH.compare(sort, o.sort);
			if (r == 0)
				r = OH.compare(display, o.display);
			if (r == 0)
				r = OH.compare(style, o.style);
			return r;
		}
		@Override
		public int hashCode() {
			return OH.hashCode(display, style, sort);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Option) {
				Option o = (Option) obj;
				return OH.eq(display, o.display) && OH.eq(style, o.style) && OH.eq(sort, o.sort);
			} else
				return false;
		}
		public String getDisplay() {
			return display;
		}
		public Comparable<?> getSort() {
			return sort;
		}
		public String getStyle() {
			return style;
		}
		public HasherSet<Row> getRows() {
			return rows;
		}
		public Tuple2<String, String> getKey() {
			return key;
		}

	}

	@Override
	public void onDmError(AmiWebDm datamodel, AmiWebDmError error) {

	}

	@Override
	public void onDmRunningQuery(AmiWebDm datamodel, boolean isRequery) {

	}

	@Override
	public boolean hasVisiblePortletForDm(AmiWebDm datamodel) {
		return getVisible();
	}

	public void setUsedDatamodel(String dmName, String tableName) {
		String oldName = this.dmSingleton.getDmAliasDotName();
		String oldTableName = this.dmSingleton.getDmTableName();
		AmiWebDmManager dmManager = getService().getDmManager();
		if (this.isApplyToSourceTable) {
			AmiWebDm existing = dmManager.getDmByAliasDotName(oldName);
			if (existing != null)
				existing.removeFilter(this);
		}
		//		 below adds filter object to dashBoard
		this.dmSingleton.setUsedDm(dmName, tableName);
		if (this.isApplyToSourceTable) {
			AmiWebDm existing = dmManager.getDmByAliasDotName(dmName);
			if (existing != null)
				existing.addFilter(this);
		}
		dmManager.onFilterDependencyChanged(this, oldName, oldTableName, false);
		dmManager.onFilterDependencyChanged(this, dmName, tableName, true);
	}

	public AmiWebDmTableSchema getDm() {
		return this.dmSingleton.getDmTableSchema();
	}
	public boolean getIsApplyToSourceTable() {
		return isApplyToSourceTable;
	}
	public void setIsApplyToSourceTable(boolean b) {
		isApplyToSourceTable = b;
	}
	protected Table getValuesForLink(Collection<Row> sel) {
		AmiWebDmTableSchema t = getDm();
		Table r = t.newEmptyTable();
		for (Row i : sel)
			r.getRows().addRow(i.getValuesCloned());
		return r;
	}
	@Override
	public Table getSelectableRows(AmiWebDmLink link, byte type) {
		AmiWebDmTableSchema t = getDm();
		switch (type) {
			case ALL:
				return this.table;
			case NONE: {
				if (this.table == null)
					return null;
				Table r = new ColumnarTable(this.table.getColumns());
				return r;
			}
			case SELECTED: {
				if (this.table == null)
					return null;
				Table r = new ColumnarTable(this.table.getColumns());
				t.getDm().getResponseTableset().getTable(t.getName());
				if (this.getSelectedRows() != null)
					for (Row i : this.getSelectedRows())
						r.getRows().addRow(i.getValuesCloned());
				return r;
			}
			default:
				throw new RuntimeException("Bad type: " + type);
		}
	}

	@Override
	public boolean hasSelectedRows(AmiWebDmLink link) {
		return this.getSelectedRows() != null;
	}

	protected void onValuesChanged() {
		CalcFrameStack sf = getStackFrame();
		if (this.isApplyToSourceTable) {
			AmiWebDmTableSchema dm = getDm();
			if (dm == null)
				return;
			dm.getDm().reprocessFilters(dm.getName());
			AmiWebDmManager dmm = this.getService().getDmManager();
			for (AmiWebFilterLink i : this.links.values()) {
				AmiWebDm t = dmm.getDmByAliasDotName(i.getTargetDmAliasDotName());
				if (t != null)
					t.reprocessFilters(i.getDmTableName());
			}
		}
		for (AmiWebDmLink link : getDmLinksFromThisPortlet()) {
			if (link.isRunOnSelect()) {
				AmiWebDmUtils.sendRequest(getService(), link);
			}
		}
	}
	@Override
	public String getTargetDmAliasDotName() {
		return this.dmSingleton.getDmAliasDotName();
	}
	@Override
	public String getTargetTableName() {
		return this.dmSingleton.getDmTableName();
	}
	@Override
	public Table filter(Table tbl) {
		Set<Row> selectedRows = this.getSelectedRows();
		if (selectedRows == null) {
			return tbl;
		}
		if (selectedRows.isEmpty()) {
			tbl.clear();
			return tbl;
		}
		List<Row> keep = new ArrayList<Row>();
		TableList rows = tbl.getRows();
		for (Row r : rows)
			if (selectedRows.contains(r))
				keep.add(r);
		rows.clear();
		rows.addAll(keep);
		return tbl;
	}

	public IntKeyMap<AmiWebFilterLink> getLinks() {
		return this.links;
	}
	public byte getDisplayType(boolean isOverride) {
		return this.displayType.getValue(isOverride);
	}

	public boolean setDataType(Class<?> type) {
		boolean validType = this.formPortlet.isValidDataType(type);
		if (validType) {
			this.dataType = type;
		}
		return validType;
	}
	public Class<?> getDataType() {
		return this.dataType;
	}

	public void setDisplayType(byte type, boolean isOverride) {
		this.displayType.setValue(type, isOverride);
		if (this.formPortlet != null) {
			removeInnerPortlet();
		}
		this.formPortlet = createForm(type);
		this.formPortlet.setFilterTitle(this.getAmiTitle(true));
		this.getManager().onPortletAdded(this.formPortlet);
		this.setChild(this.formPortlet);
		getStylePeer().initStyle(null); // carry over style settings
		this.onDmDataBeforeFilterChanged(null);

	}
	private AmiWebFilterFormPortlet createForm(byte type) {
		switch (type) {
			case DISPLAY_TYPE_CHECKBOXES:
				return new AmiWebFilterFormPortlet_Checkboxes(generateConfig(), this);
			case DISPLAY_TYPE_RANGE:
				return new AmiWebFilterFormPortlet_Range(generateConfig(), this);
			case DISPLAY_TYPE_SEARCH:
				return new AmiWebFilterFormPortlet_Search(generateConfig(), this);
			case DISPLAY_TYPE_SELECT_SINGLE:
				return new AmiWebFilterFormPortlet_Select(generateConfig(), this);
			case DISPLAY_TYPE_RANGE_SLIDER:
				return new AmiWebFilterFormPortlet_RangeSlider(generateConfig(), this);
			case DISPLAY_TYPE_MULTICHECKBOX:
				return new AmiWebFilterFormPortlet_MultiCheckbox(generateConfig(), this);
			case DISPLAY_TYPE_RADIOS:
				return new AmiWebFilterFormPortlet_Radios(generateConfig(), this);
			default:
				throw new NoSuchElementException(SH.toString(type));
		}
	}

	@Override
	public void onClosed() {
		if (!this.getService().getLayoutFilesManager().getIsLayoutClosing()) {
			AmiWebDmManager dmManager = getService().getDmManager();
			CalcFrameStack sf = getStackFrame();
			if (this.isApplyToSourceTable) {
				AmiWebDm existing = dmManager.getDmByAliasDotName(this.dmSingleton.getDmAliasDotName());
				if (existing != null) {
					existing.removeFilter(this);
					existing.reprocessFilters(this.dmSingleton.getDmTableName());
				}
			}
			for (AmiWebFilterLink i : this.links.values()) {
				AmiWebDm t = dmManager.getDmByAliasDotName(i.getTargetDmAliasDotName());
				if (t != null) {
					t.removeFilter(i);
					t.reprocessFilters(i.getTargetTableName());
				}
			}
			super.onClosed();
		}
	}
	@Override
	public String getScrollGripColor() {
		if (this.formPortlet == null)
			return null;
		return this.formPortlet.getFormPortletStyle().getScrollGripColor();
	}
	@Override
	public void setScrollGripColor(String scrollGripColor) {
		if (this.formPortlet == null)
			return;
		this.formPortlet.getFormPortletStyle().setScrollGripColor(scrollGripColor);
	}
	@Override
	public String getScrollTrackColor() {
		if (this.formPortlet == null)
			return null;
		return this.formPortlet.getFormPortletStyle().getScrollTrackColor();
	}
	@Override
	public void setScrollTrackColor(String scrollTrackColor) {
		if (this.formPortlet == null)
			return;
		this.formPortlet.getFormPortletStyle().setScrollTrackColor(scrollTrackColor);
	}
	@Override
	public String getScrollButtonColor() {
		if (this.formPortlet == null)
			return null;
		return this.formPortlet.getFormPortletStyle().getScrollButtonColor();
	}
	@Override
	public void setScrollButtonColor(String scrollButtonColor) {
		if (this.formPortlet == null)
			return;
		this.formPortlet.getFormPortletStyle().setScrollButtonColor(scrollButtonColor);
	}

	@Override
	public void setScrollBarRadius(Integer scrollBarRadius) {
		if (this.formPortlet == null)
			return;
		this.formPortlet.getFormPortletStyle().setScrollBarRadius(scrollBarRadius);
		this.scrollBorderRadius = scrollBarRadius;
	}

	@Override
	public Integer getScrollBarRadius() {
		return this.scrollBorderRadius;
	}
	@Override
	public String getScrollIconsColor() {
		if (this.formPortlet == null)
			return null;
		return this.formPortlet.getFormPortletStyle().getScrollIconsColor();
	}
	@Override
	public void setScrollIconsColor(String scrollIconsColor) {
		if (this.formPortlet == null)
			return;
		this.formPortlet.getFormPortletStyle().setScrollIconsColor(scrollIconsColor);
	}
	@Override
	public String getScrollBorderColor() {
		if (this.formPortlet == null)
			return null;
		return this.formPortlet.getFormPortletStyle().getScrollBorderColor();
	}
	@Override
	public void setScrollBorderColor(String color) {
		if (this.formPortlet == null)
			return;
		this.formPortlet.getFormPortletStyle().setScrollBorderColor(color);
	}
	@Override
	public Integer getScrollBarWidth() {
		if (this.formPortlet == null)
			return null;
		return this.formPortlet.getFormPortletStyle().getScrollBarWidth();
	}
	@Override
	public void setScrollBarWidth(Integer scrollBarWidth) {
		if (this.formPortlet == null)
			return;
		this.formPortlet.getFormPortletStyle().setScrollBarWidth(scrollBarWidth);
	}
	public boolean getClearOnRequery() {
		return clearOnRequery;
	}
	public void setClearOnRequery(boolean clearOnRequery) {
		this.clearOnRequery = clearOnRequery;
	}
	@Override
	public Set<String> getUsedDmVariables(String dmAliasDotName, String dmTable, Set<String> r) {
		if (this.dmSingleton.matches(dmAliasDotName, dmTable)) {
			AmiWebDmTableSchema dm = this.getDm();
			//			StringBuilder sb = new StringBuilder();
			//			DerivedCellCalculator dsExp = parse(dm, displayExpression, sb, Comparable.class, FORMULA_DISPLAY);
			//			DerivedCellCalculator coExp = parse(dm, colorExpression, sb, String.class, FORMULA_COLOR);
			//			DerivedCellCalculator soExp = parse(dm, sortExpression, sb, Comparable.class, FORMULA_SORT);
			DerivedHelper.getDependencyIds(displayFormula.getFormulaCalc(), (Set) r);
			DerivedHelper.getDependencyIds(colorFormula.getFormulaCalc(), (Set) r);
			DerivedHelper.getDependencyIds(sortFormula.getFormulaCalc(), (Set) r);
			for (AmiWebFilterLink i : this.links.values())
				i.getDependencies((Set) r, dm.getClassTypes());
		}
		return r;
	}
	protected AmiWebFilterFormPortlet getForm() {
		return this.formPortlet;
	}

	public boolean needMaxOptions() {
		return this.formPortlet instanceof AmiWebFilterFormPortlet_Checkboxes || this.formPortlet instanceof AmiWebFilterFormPortlet_MultiCheckbox
				|| this.formPortlet instanceof AmiWebFilterFormPortlet_Radios ? true : false;
	}

	public void setMaxOptions(int max, boolean isOverride) {
		if (needMaxOptions()) {
			maxOptions.setValue(max, isOverride);
			this.onDmDataBeforeFilterChanged(null);
		}
	}

	public int getMaxOptions(boolean isOverride) {
		return this.maxOptions.getValue(isOverride);
	}
	@Override
	public Set<String> getUsedDmAliasDotNames() {
		return this.dmSingleton.getUsedDmAliasDotNames();
	}
	@Override
	public Set<String> getUsedDmTables(String dmAliasDotName) {
		return this.dmSingleton.getUsedDmTables(dmAliasDotName);
	}
	@Override
	public void onDmNameChanged(String oldAliasDotName, AmiWebDm dm) {
		this.dmSingleton.onDmNameChanged(oldAliasDotName, dm);
	}
	@Override
	public AmiWebFilterPortlet getSourcePanel() {
		return this;
	}
	@Override
	public AmiWebPanelSettingsPortlet showSettingsPortlet() {
		return new AmiWebFilterSettingsPortlet(generateConfig(), this);
	}
	@Override
	public void onFormulaChanged(AmiWebFormula formula, DerivedCellCalculator old, DerivedCellCalculator nuw) {
		if (isInitDone())
			onDmDataBeforeFilterChanged(null);
	}
	@Override
	public void setScrollBarHideArrows(Boolean hide) {
		if (this.formPortlet == null)
			return;
		this.formPortlet.getFormPortletStyle().setScrollBarHideArrows(hide);
	}

	@Override
	public Boolean getScrollBarHideArrows() {
		if (this.formPortlet == null)
			return null;
		return this.formPortlet.getFormPortletStyle().getScrollBarHideArrows();
	}

	@Override
	public void setScrollBarCornerColor(String color) {
		if (this.formPortlet == null)
			return;
		this.formPortlet.getFormPortletStyle().setScrollBarCornerColor(color);
	}
	@Override
	public String getScrollBarCornerColor() {
		return this.formPortlet.getFormPortletStyle().getScrollBarCornerColor();
	}

	@Override
	public void populateConfigMenu(WebMenu headMenu) {
	}

	public void setFormatExpressionOverride(String formula) {
		this.displayFormula.setFormula(formula, true);
	}
	public void setColorExpressionOverride(String formula) {
		this.colorFormula.setFormula(formula, true);
	}
	public void setSortExpressionOverride(String formula) {
		this.sortFormula.setFormula(formula, true);
	}

}
