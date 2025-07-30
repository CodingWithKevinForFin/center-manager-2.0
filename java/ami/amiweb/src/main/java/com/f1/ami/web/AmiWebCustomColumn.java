package com.f1.ami.web;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.suite.web.portal.impl.WebColumnEditConfig;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Byte;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Object;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;

public class AmiWebCustomColumn implements WebColumnEditConfig, AmiWebDomObject, AmiWebFormulasListener {
	public static final String CONFIG_DESCRIPTION = "de";
	public static final String CONFIG_EDIT_OPTIONS = "eo";
	public static final String CONFIG_EDIT_OPTIONS_FORMULA = "eof";
	public static final String CONFIG_CLICKABLE = "cl";
	public static final String CONFIG_EDIT_TYPE = "et";
	public static final String CONFIG_EDIT_ID = "ei";
	public static final String CONFIG_SORT = "sr";
	public static final String CONFIG_HEADER_STYLE = "hs";
	public static final String CONFIG_TARGET_FORMULA = "tf";
	public static final String CONFIG_STYLE_FORMULA = "sy";
	public static final String CONFIG_BACKGROUND_FORMULA = "bg";
	public static final String CONFIG_COLOR_FORMULA = "fg";
	public static final String CONFIG_FORMULA = "fm";
	public static final String CONFIG_PRECISION = "pc";
	public static final String CONFIG_TYPE = "tp";
	public static final String CONFIG_TITLE = "tl";
	public static final String CONFIG_ID = "id";
	public static final String CONFIG_DISABLE_FUTURE_DATES = "dfd";
	public static final String CONFIG_ENABLE_LAST_N_DAYS = "lnd";
	public static final String CONFIG_ONECLICK = "oc";
	public static final String CONFIG_FIXED_WIDTH = "fw";
	public static final String CONFIG_WIDTH = "width";//add new field 
	public static final String CONFIG_TOOLTIP = "tooltip";

	final private String columnId;
	final private AmiWebOverrideValue<Byte> type = new AmiWebOverrideValue<Byte>((byte) 0);
	final private AmiWebOverrideValue<Integer> precision = new AmiWebOverrideValue<Integer>(0); // -2 = User Default (AmiConsts.DEFAULT)
	final private AmiWebFormula displayFormula;
	final private AmiWebFormula sortFormula;
	final private AmiWebFormula colorFormula;
	final private AmiWebFormula backgroundColorFormula;
	final private AmiWebFormula styleFormula;
	final private AmiWebFormula tooltipFormula;
	final private String oneClick;
	private boolean fixedWidth;
	private Class dataType;
	private String description;
	private List<String> editSelectOptions;
	private String editOptionsFormula;
	private boolean disableFutureDays = false;
	private int enableLastNDays;
	private String ari;
	private int colWidth = -1;//init to -1
	final private AmiWebAbstractTablePortlet table;
	private boolean isTransient;
	private String amiLayoutFullAliasDotId;
	private String amiLayoutFullAlias;

	public final static String KEY_DISPLAY = "display";
	public final static String KEY_SORTING = "sorting";
	public final static String KEY_FORMAT = "format";
	public final static String KEY_TITLE = "title";
	public final static String KEY_HEADER_SYTLE = "headerStyle";
	public final static String KEY_DESCRIPTION = "description";
	public final static String KEY_POSITION = "position";
	public final static String KEY_STYLE = "style";
	public final static String KEY_FG_CL = "fgCl";
	public final static String KEY_BG_CL = "bgCl";
	private AmiWebOverrideValue<Byte> editType = new AmiWebOverrideValue<Byte>((byte) 0);
	private AmiWebOverrideValue<String> editId = new AmiWebOverrideValue<String>("");
	private AmiWebOverrideValue<String> title = new AmiWebOverrideValue<String>("");
	private AmiWebOverrideValue<String> headerStyle = new AmiWebOverrideValue<String>("");
	private AmiWebOverrideValue<String> headerStyleExpression = new AmiWebOverrideValue<String>("");
	private AmiWebOverrideValue<String> clickable = new AmiWebOverrideValue<String>("");

	//Constructor for AmiWebChooseDataForm, AmiWebTreemapPortlet, AmiWebVizwiz_Table
	public AmiWebCustomColumn(AmiWebAbstractTablePortlet table, String id, String title, byte type, int precision, String formula, String colorFormula,
			String backgroundColorFormula, String styleFormula, String headerStyle, String sort, boolean isAggregate, String tooltip, boolean fixedWidth) {
		this(table, id, title, type, precision, formula, colorFormula, backgroundColorFormula, styleFormula, headerStyle, sort, null, EDIT_DISABLED, null, null, null, null,
				isAggregate, tooltip, null, false, 0, fixedWidth);
	}
	//Constructor for AmiWebAbstractTablePortlet 
	public AmiWebCustomColumn(AmiWebAbstractTablePortlet table, String id, String title, byte type, int precision, String formula, String colorFormula,
			String backgroundColorFormula, String styleFormula, String headerStyle, String sort, String editId, byte editType, List<String> editOptions, String editOptionsFormula,
			String clickable, String oneClick, boolean isAggregate, String tooltip, String description, boolean disableFutureDays, int enableLastNDays, boolean fixedWidth) {
		this.formulas = new AmiWebFormulasImpl(this);
		this.formulas.setAggregateTable(table.getFormulas().getAggregateTable());
		this.table = table;
		this.columnId = id;
		updateAri();
		byte at = isAggregate ? AmiWebFormula.FORMULA_TYPE_AGG_RT : AmiWebFormula.FORMULA_TYPE_NORMAL;
		this.displayFormula = this.formulas.addFormula("display", at, Object.class);
		this.sortFormula = this.formulas.addFormula("sort", at, Comparable.class);
		this.colorFormula = this.formulas.addFormula("color", at, String.class);
		this.backgroundColorFormula = this.formulas.addFormula("backgroundColor", at, String.class);
		this.styleFormula = this.formulas.addFormula("style", at, String.class);
		this.tooltipFormula = this.formulas.addFormula("tooltip", at, Object.class);
		this.clickable.set(clickable, true);
		this.oneClick = oneClick;
		this.fixedWidth = fixedWidth;
		this.title.set(title, true);
		this.type.set(type, true);
		this.precision.set(precision, true);
		this.displayFormula.setFormula(formula, false);
		this.sortFormula.setFormula(SH.toNull(sort), false);
		this.colorFormula.setFormula(SH.toNull(colorFormula), false);
		this.backgroundColorFormula.setFormula(SH.toNull(backgroundColorFormula), false);
		this.styleFormula.setFormula(SH.toNull(styleFormula), false);
		this.headerStyle.set(headerStyle, true);
		this.headerStyleExpression.set(AmiWebUtils.toCssExpression(this.headerStyle.getValue(true)), true);
		this.editType.set(editType, true);
		this.editId.set(editId, true);
		this.tooltipFormula.setFormula(SH.toNull(tooltip), false);
		this.editSelectOptions = editOptions;
		this.editOptionsFormula = editOptionsFormula;
		this.formulas.addFormulasListener(this);
		this.setDescription(description);
		this.setDisableFutureDays(disableFutureDays);
		this.setEnableLastNDays(enableLastNDays);

		updateDataType();
	}
	private void updateDataType() {
		this.dataType = displayFormula.getFormulaCalc() == null ? Object.class : displayFormula.getFormulaCalc().getReturnType();
	}
	//Constructor for AmiWebAbstractTablePortlet 
	public AmiWebCustomColumn(AmiWebAbstractTablePortlet table, Map<String, Object> m, boolean isAggregate) {
		this.table = table;
		this.formulas = new AmiWebFormulasImpl(this);
		this.formulas.setAggregateTable(table.getFormulas().getAggregateTable());
		byte at = isAggregate ? AmiWebFormula.FORMULA_TYPE_AGG_RT : AmiWebFormula.FORMULA_TYPE_NORMAL;
		this.displayFormula = this.formulas.addFormula("display", at, Object.class);
		this.sortFormula = this.formulas.addFormula("sort", at, Comparable.class);
		this.colorFormula = this.formulas.addFormula("color", at, String.class);
		this.backgroundColorFormula = this.formulas.addFormula("backgroundColor", at, String.class);
		this.styleFormula = this.formulas.addFormula("style", at, String.class);
		this.tooltipFormula = this.formulas.addFormula("tooltip", at, Object.class);
		this.columnId = CH.getOr(Caster_String.INSTANCE, m, CONFIG_ID, null);
		updateAri();
		this.title.set(CH.getOr(Caster_String.INSTANCE, m, CONFIG_TITLE, null), true);
		String type = CH.getOr(Caster_String.INSTANCE, m, CONFIG_TYPE, null);
		Byte b = AmiWebUtils.CUSTOM_COL_NAMES.getKey(type);
		this.type.set(b != null ? b : SH.parseByte(type), true);
		this.precision.set(CH.getOr(Caster_Integer.INSTANCE, m, CONFIG_PRECISION, AmiConsts.DEFAULT), true);
		this.displayFormula.initFormula(CH.getOr(Caster_String.INSTANCE, m, CONFIG_FORMULA, null));
		this.colorFormula.initFormula(SH.toNull(CH.getOr(Caster_String.INSTANCE, m, CONFIG_COLOR_FORMULA, null)));
		this.backgroundColorFormula.initFormula(SH.toNull(CH.getOr(Caster_String.INSTANCE, m, CONFIG_BACKGROUND_FORMULA, null)));
		this.styleFormula.initFormula(SH.toNull(CH.getOr(Caster_String.INSTANCE, m, CONFIG_STYLE_FORMULA, null)));
		this.headerStyle.set(CH.getOr(Caster_String.INSTANCE, m, CONFIG_HEADER_STYLE, null), true);
		this.headerStyleExpression.set(AmiWebUtils.toCssExpression(this.headerStyle.getValue(true)), true);
		this.tooltipFormula.initFormula(CH.getOr(Caster_String.INSTANCE, m, CONFIG_TOOLTIP, null));
		this.sortFormula.initFormula(SH.toNull(CH.getOr(Caster_String.INSTANCE, m, CONFIG_SORT, null)));
		this.editId.set(CH.getOr(Caster_String.INSTANCE, m, CONFIG_EDIT_ID, null), true);
		this.editType.set(CH.getOr(Caster_Byte.INSTANCE, m, CONFIG_EDIT_TYPE, AmiWebCustomColumn.EDIT_DISABLED), true);
		this.editSelectOptions = (List<String>) CH.getOr(Caster_Object.INSTANCE, m, CONFIG_EDIT_OPTIONS, null);
		this.editOptionsFormula = CH.getOr(Caster_String.INSTANCE, m, CONFIG_EDIT_OPTIONS_FORMULA, null);
		this.disableFutureDays = (CH.getOr(Caster_Boolean.INSTANCE, m, CONFIG_DISABLE_FUTURE_DATES, false));
		this.enableLastNDays = CH.getOr(Caster_Integer.INSTANCE, m, CONFIG_ENABLE_LAST_N_DAYS, 0);
		String targetFormula = CH.getOr(Caster_String.INSTANCE, m, CONFIG_TARGET_FORMULA, null);
		if (SH.is(targetFormula)) {
			this.clickable.set("m", true);
		} else
			this.clickable.set(CH.getOr(Caster_String.INSTANCE, m, CONFIG_CLICKABLE, null), true);
		String description = CH.getOr(Caster_String.INSTANCE, m, CONFIG_DESCRIPTION, null);
		this.oneClick = CH.getOr(Caster_String.INSTANCE, m, CONFIG_ONECLICK, null);
		this.fixedWidth = CH.getOr(Caster_Boolean.INSTANCE, m, CONFIG_FIXED_WIDTH, false);
		this.colWidth = CH.getOr(Caster_Integer.INSTANCE, m, CONFIG_WIDTH, -1);//add new field init, if not exists, dflt is -1
		//Backwards compatibility
		if (!m.containsKey(CONFIG_EDIT_OPTIONS_FORMULA)) {
			if (this.editSelectOptions != null) {
				this.editOptionsFormula = SH.doubleQuote(SH.join(",", this.editSelectOptions));
			}
		}
		this.setDescription(description);
		this.formulas.addFormulasListener(this);
		updateDataType();
	}
	public Map<String, Object> getColumnConfig() {
		HashMap<String, Object> columnConfig = new HashMap<String, Object>();
		CH.putNoNull(columnConfig, AmiWebCustomColumn.CONFIG_ID, this.getColumnId());
		CH.putNoNull(columnConfig, AmiWebCustomColumn.CONFIG_TITLE, this.title.getValue());
		CH.putNoNull(columnConfig, AmiWebCustomColumn.CONFIG_TYPE, AmiWebUtils.CUSTOM_COL_NAMES.getValue(this.getType().getValue()));
		if (OH.ne(this.getPrecision().getValue(), (Integer) AmiConsts.DEFAULT))
			CH.putNoNull(columnConfig, AmiWebCustomColumn.CONFIG_PRECISION, this.getPrecision().getValue());
		CH.putNoNull(columnConfig, AmiWebCustomColumn.CONFIG_FORMULA, this.displayFormula.getFormulaConfig());
		CH.putNoNull(columnConfig, AmiWebCustomColumn.CONFIG_COLOR_FORMULA, this.colorFormula.getFormulaConfig());
		CH.putNoNull(columnConfig, AmiWebCustomColumn.CONFIG_BACKGROUND_FORMULA, this.backgroundColorFormula.getFormulaConfig());
		CH.putNoNull(columnConfig, AmiWebCustomColumn.CONFIG_STYLE_FORMULA, this.styleFormula.getFormulaConfig());
		AmiWebUtils.putSkipEmpty(columnConfig, AmiWebCustomColumn.CONFIG_HEADER_STYLE, this.headerStyle.getValue());
		CH.putNoNull(columnConfig, AmiWebCustomColumn.CONFIG_SORT, this.sortFormula.getFormulaConfig());
		AmiWebUtils.putSkipEmpty(columnConfig, AmiWebCustomColumn.CONFIG_EDIT_ID, this.editId.getValue());
		if (this.getEditType(false) != AmiWebCustomColumn.EDIT_DISABLED)
			CH.putNoNull(columnConfig, AmiWebCustomColumn.CONFIG_EDIT_TYPE, this.editType.getValue());
		AmiWebUtils.putSkipEmpty(columnConfig, AmiWebCustomColumn.CONFIG_EDIT_OPTIONS, this.getEditSelectOptions()); // Old
		AmiWebUtils.putSkipEmpty(columnConfig, AmiWebCustomColumn.CONFIG_EDIT_OPTIONS_FORMULA, this.getEditOptionFormula());
		CH.putNoNull(columnConfig, AmiWebCustomColumn.CONFIG_DESCRIPTION, this.getDescription());
		CH.putNoNull(columnConfig, AmiWebCustomColumn.CONFIG_CLICKABLE, this.clickable.getValue());
		CH.putNoNull(columnConfig, AmiWebCustomColumn.CONFIG_ONECLICK, this.getOneClick());
		if (isFixedWidth())
			CH.putNoNull(columnConfig, AmiWebCustomColumn.CONFIG_FIXED_WIDTH, true);
		if (this.getDisableFutureDays())
			CH.putNoNull(columnConfig, AmiWebCustomColumn.CONFIG_DISABLE_FUTURE_DATES, this.getDisableFutureDays());
		if (this.enableLastNDays != 0)
			CH.putNoNull(columnConfig, AmiWebCustomColumn.CONFIG_ENABLE_LAST_N_DAYS, this.enableLastNDays);
		if (this.colWidth != -1)
			CH.putNoNull(columnConfig, AmiWebCustomColumn.CONFIG_WIDTH, this.colWidth);//add 
		CH.putNoNull(columnConfig, AmiWebCustomColumn.CONFIG_TOOLTIP, this.getTooltipFormula().getFormulaConfig());
		return columnConfig;
	}

	public String getClickable(boolean isOverride) {
		return clickable.getValue(isOverride);
	}
	public void setClickable(String clickable, boolean isOverride) {
		this.clickable.setValue(clickable, isOverride);
	}
	public String getTitle(boolean isOverride) {
		return title.getValue(isOverride);
	}
	public void setTitle(String newTitle, boolean isOverride) {
		this.title.setValue(newTitle, isOverride);
	}

	public AmiWebOverrideValue<Byte> getType() {
		return type;
	}
	public Byte getType(boolean isOverride) {
		return type.getValue(isOverride);
	}
	public void setType(Byte b, boolean isOverride) {
		this.type.setValue(b, isOverride);
	}

	public AmiWebOverrideValue<Integer> getPrecision() {
		return precision;
	}

	public AmiWebFormula getDisplayFormula() {
		return displayFormula;
	}

	public AmiWebFormula getColorFormula() {
		return colorFormula;
	}

	public AmiWebFormula getBackgroundColorFormula() {
		return backgroundColorFormula;
	}

	public AmiWebFormula getStyleFormula() {
		return styleFormula;
	}

	public AmiWebFormula getTooltipFormula() {
		return this.tooltipFormula;
	}

	public String getColumnId() {
		return columnId;
	}

	public Class getDataType() {
		return dataType;
	}

	public String getDescription() {
		return description;
	}

	public AmiWebCustomColumn setDescription(String description) {
		if (SH.isnt(description))
			this.description = null;
		else
			this.description = description;
		return this;
	}

	public String getHeaderStyle(boolean isOverride) {
		return headerStyle.getValue(isOverride);
	}

	public void setHeaderStyle(String headerStyle, boolean isOverride) {
		this.headerStyle.setValue(headerStyle, isOverride);
	}

	public String getHeaderStyleExpression(boolean isOverride) {
		return this.headerStyleExpression.getValue(isOverride);
	}

	public void setHeaderStyleExpression(boolean isOverride) {
		this.headerStyleExpression.set(AmiWebUtils.toCssExpression(this.headerStyle.getValue(isOverride)), isOverride);
	}

	public AmiWebFormula getSortFormula() {
		return sortFormula;
	}

	@Override
	public List<String> getEditSelectOptions() {
		return this.editSelectOptions;
	}

	public byte getEditType(boolean isOverride) {
		return this.editType.getValue(isOverride);
	}
	public String getEditId(boolean isOverride) {
		return this.editId.getValue(isOverride);
	}
	@Override
	public String getEditOptionFormula() {
		return editOptionsFormula;
	}
	public void setEditOptionFormula(String editOptionFormula) {
		this.editOptionsFormula = editOptionFormula;
	}
	public void setEditType(byte editType, boolean isOverride) {
		this.editType.setValue(editType, isOverride);
	}
	public void setEditId(String editId, boolean isOverride) {
		this.editId.setValue(editId, isOverride);
	}
	public boolean getDisableFutureDays() {
		return disableFutureDays;
	}
	public void setDisableFutureDays(boolean disableFutureDays) {
		this.disableFutureDays = disableFutureDays;
	}
	public int getEnableLastNDays() {
		return this.enableLastNDays;
	}
	public void setEnableLastNDays(int enableLastNDays) {
		this.enableLastNDays = enableLastNDays;
	}
	public AmiWebAbstractTablePortlet getTable() {
		return this.table;
	}

	public WebColumn getWebColumn() {
		return getTable().getTable().getColumn(this.columnId);

	}
	@Override
	public String toString() {
		return toDerivedString();
	}
	@Override
	public String toDerivedString() {
		return getAri();
	}

	@Override
	public StringBuilder toDerivedString(StringBuilder sb) {
		return sb.append(getAri());
	}
	@Override
	public String getAri() {
		return this.ari;
	}
	@Override
	public void updateAri() {
		String oldAri = this.ari;
		this.amiLayoutFullAlias = this.table.getAmiLayoutFullAlias();
		this.amiLayoutFullAliasDotId = this.table.getAmiLayoutFullAliasDotId() + "?" + getDomLabel();
		this.ari = AmiWebDomObject.ARI_TYPE_COLUMN + ":" + this.amiLayoutFullAliasDotId;
		if (OH.ne(this.ari, oldAri)) {
			this.table.getService().getDomObjectsManager().fireAriChanged(this, oldAri);
		}
		FastWebTable fwt = this.table.getTable();
		fwt.setHtmlIdSelectorForColumn(this.getColumnId(), AmiWebUtils.toHtmlIdSelector(this));
	}
	@Override
	public String getAriType() {
		return ARI_TYPE_COLUMN;
	}
	@Override
	public String getDomLabel() {
		return getColumnId();
	}
	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		return Collections.EMPTY_LIST;
	}
	@Override
	public AmiWebDomObject getParentDomObject() {
		return this.table;
	}
	@Override
	public Class<?> getDomClassType() {
		return this.getClass();
	}
	@Override
	public Object getDomValue() {
		return this;
	}
	@Override
	public boolean isTransient() {
		return isTransient;
	}
	@Override
	public void setTransient(boolean isTransient) {
		this.isTransient = isTransient;
	}
	@Override
	public AmiWebAmiScriptCallbacks getAmiScriptCallbacks() {
		return null;
	}

	private boolean isManagedByDomManager = false;
	final private AmiWebFormulasImpl formulas;

	@Override
	public void addToDomManager() {
		if (this.isManagedByDomManager == false) {
			AmiWebService service = this.table.getService();
			service.getDomObjectsManager().addManagedDomObject(this);
			service.getDomObjectsManager().fireAdded(this);
			this.isManagedByDomManager = true;

		}
	}

	@Override
	public void removeFromDomManager() {
		AmiWebDomObjectsManager domObjectsManager = this.table.getService().getDomObjectsManager();
		for (AmiWebDomObject i : this.getChildDomObjects())
			domObjectsManager.fireRemoved(i);
		domObjectsManager.fireRemoved(this);

		if (this.isManagedByDomManager == true) {
			//Remove DomValues First

			//Remove Self
			AmiWebService service = this.table.getService();
			service.getDomObjectsManager().removeManagedDomObject(this);
			this.isManagedByDomManager = false;
		}
	}
	@Override
	public String getAmiLayoutFullAlias() {
		return this.amiLayoutFullAlias;
	}
	@Override
	public String getAmiLayoutFullAliasDotId() {
		return this.amiLayoutFullAliasDotId;
	}
	@Override
	public AmiWebFormulas getFormulas() {
		return this.formulas;
	}
	@Override
	public AmiWebService getService() {
		return this.table.getService();
	}
	@Override
	public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula f) {
		return this.table.getFormulaVarTypes(f);
	}
	@Override
	public void onFormulaChanged(AmiWebFormula formula, DerivedCellCalculator old, DerivedCellCalculator nuw) {
		if (formula == this.displayFormula)
			updateDataType();
		StringBuilder sb = new StringBuilder();
		int pos = this.table.getTable().getColumnPosition(this.columnId);
		if (this.table instanceof AmiWebAggregateObjectTablePortlet) {
			AmiWebAggregateObjectTablePortlet agg = (AmiWebAggregateObjectTablePortlet) this.table;
			if (agg.getGroupByColumnIds().contains(this.columnId)) {
				agg.addCustomColumnGroupBy(this, sb, pos, this, this.table.getFormulaVarTypes(formula), true);
				return;
			}
		}

		if (!inFire && this.table.getTable().getColumnNoThrow(this.getColumnId()) != null) {//only call if this column is already added to the table
			inFire = true;
			try {
				this.table.addCustomColumn(this, sb, pos, this, this.table.getFormulaVarTypes(formula), true);
			} finally {
				inFire = false;
			}
		}
	}

	private boolean inFire = false;

	public String getOneClick() {
		return oneClick;
	}
	public int getColumnWidth() {
		return this.colWidth;
	}
	@Override
	public String getEditId() {
		return this.getEditId(true);
	}
	@Override
	public byte getEditType() {
		return this.getEditType(true);
	}
	@Override
	public String objectToJson() {
		return DerivedHelper.toString(this);
	}

	public boolean isFixedWidth() {
		return fixedWidth;
	}

	public void setIsFixedWidth(boolean fix) {
		this.fixedWidth = fix;
	}

}
