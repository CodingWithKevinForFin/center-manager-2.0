package com.f1.ami.web.charts;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.AmiWebConsts;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebFormulas;
import com.f1.ami.web.AmiWebOverrideValue;
import com.f1.ami.web.AmiWebScriptManagerForLayout;
import com.f1.ami.web.charts.AmiWebChartSeries.Grouping;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.base.Table;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.math.PrimitiveMath;
import com.f1.utils.math.PrimitiveMathManager;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public abstract class AmiWebChartFormula implements AmiWebFormula {

	private static final Logger log = LH.get();
	public static final byte TYPE_NUMBER = 1;
	public static final byte TYPE_STRING = 2;
	public static final byte TYPE_COLOR = 3;
	public static final byte TYPE_SHAPE = 4;
	public static final byte TYPE_CONST = 5;
	public static final byte TYPE_BOOLEAN = 6;
	public static final byte TYPE_POSITION = 7;
	public static final byte TYPE_FONT = 8;
	public static final byte TYPE_LINE_TYPE = 9;
	public static final byte TYPE_SORTABLE = 10;
	public static final byte TYPE_AXIS = 11;
	final private String name;
	final private String label;
	final private byte type;
	protected final AmiWebOverrideValue<String> value = new AmiWebOverrideValue<String>(null);
	private DerivedCellCalculator calc;
	private Object constValue;
	private boolean isConst = true;
	private boolean minMaxCached = false;
	private Number minCache;
	private Number maxCache;
	private Set<Object> uniqueCache = new LinkedHashSet<Object>();
	private boolean hidden;
	private String labelGroup;
	private AmiWebChartSeries series;
	private boolean isXBound = false;
	private boolean isYBound = false;
	private DerivedCellCalculator currentCalc;
	private Exception exception;
	private Exception overrideException;
	private Set<String> usedConstVars = new HasherSet<String>();

	public AmiWebChartFormula(AmiWebChartSeries series, String labelGroup, String name, String label, byte type) {
		super();
		this.series = series;
		this.labelGroup = labelGroup;
		this.name = name;
		this.label = label;
		this.type = type;
	}

	public Object getConfiguration() {
		return this.value.getValue(false);
	}

	public String getLabelGroup() {
		return this.labelGroup;
	}
	public boolean getIsHidden() {
		return this.hidden;
	}
	public AmiWebChartFormula setHidden() {
		this.hidden = true;
		return this;
	}
	public void clearCache() {
		this.minMaxCached = false;
		this.uniqueCache.clear();
	}

	public String getName() {
		return name;
	}
	public String getLabel() {
		return label;
	}
	public String getValue() {
		return value.get();
	}
	final public boolean clearOverride() {
		if (this.value.clearOverride()) {
			setValue(value.get(), null, false);
			return true;
		}
		return false;
	}
	final public AmiWebChartFormula setValueOverride(String text) {
		return setValue(text, null, true);
	}
	final public AmiWebChartFormula setValue(String text) {
		return setValue(text, null, false);
	}
	final public AmiWebChartFormula setValue(String text, Table table) {
		return setValue(text, table, false);
	}
	final private AmiWebChartFormula setValue(String text, Table table, boolean isOverride) {
		// NOTE: The table input variable is ONLY used for the Style Preview window that appears when creating a new layout. 
		// Otherwise, should use setValue(String)
		if (SH.isnt(text)) {
			this.calc = null;
			this.constValue = null;
			this.isConst = true;
			this.value.setValue(null, isOverride);
			return this;
		}
		this.series.flagFormulasChanged();
		AmiWebScriptManagerForLayout sm = this.series.getPortlet().getScriptManager();
		com.f1.base.CalcTypes variables;
		if (table == null) {
			variables = getVariables(this.series.getDataModelSchema());
		} else {
			variables = getVariables(table);
		}
		this.usedConstVars.clear();
		this.calc = sm.toAggCalc(text, variables, this.series.getMethodFactory(), series.getLayer(), this.usedConstVars);
		if (this.calc.isConst()) {
			this.isConst = true;
			this.constValue = this.calc.get(null);
		} else {
			this.isConst = false;
			this.constValue = null;
		}
		this.value.setValue(text, isOverride);
		// check whether value is changed;
		//		System.out.println(this.value);
		return this;
	}

	public Class<?> getReturnType() {
		return this.calc == null ? null : this.calc.getReturnType();
	}
	public boolean isReturnTypeBoolean() {
		return (this.type == TYPE_BOOLEAN);
	}
	public boolean isReturnTypeSortable() {
		return (this.type == TYPE_SORTABLE);
	}
	public boolean isReturnTypeColor() {
		return (this.type == TYPE_COLOR);
	}

	public boolean isReturnTypeNumber() {
		return (this.type == TYPE_NUMBER);
	}

	private com.f1.base.CalcTypes getVariables(AmiWebDmTableSchema model) {
		return getVariables(model, null);
	}
	private com.f1.base.CalcTypes getVariables(Table table) {
		// NOTE: This function is ONLY used for the Style Preview window that appears when creating a new layout. 
		// Otherwise, should use getVariables(AmiWebDmTableSchema)
		return getVariables(null, table);
	}
	private com.f1.base.CalcTypes getVariables(AmiWebDmTableSchema model, Table table) {
		return this.series.getThis().getFormulaVarTypes(this);
		//		if (model != null)
		//			return new com.f1.utils.TypesTuple(false, AmiWebChartSeries.VARTYPES, model.getClassTypes());
		//		else if (table != null) {
		//			List<Column> cols = table.getColumns();
		//			com.f1.utils.BasicTypes colMap = new com.f1.utils.BasicTypes();
		//			String id;
		//			for (Column c : cols) {
		//				id = (String) c.getId();
		//				if (!AmiConsts.TABLE_PARAM_DATA.equals(id) && !AmiConsts.TABLE_PARAM_ID.equals(id)) {
		//					colMap.put(id, c.getType());
		//				}
		//			}
		//			return new com.f1.utils.TypesTuple(false, AmiWebChartSeries.VARTYPES, colMap);
		//		} else
		//			return new com.f1.utils.TypesTuple(false, AmiWebChartSeries.VARTYPES);
	}
	public boolean isConst() {
		return isConst;
	}
	public boolean testValue(StringBuilder sb) {
		return this.testValue(getValue(), sb);
	}
	public boolean testValue(String text, StringBuilder sb) {
		return testValue(text, sb, null);
	}
	public boolean testValue(String text, StringBuilder sb, Table table) {
		if (SH.isnt(text))
			return true;
		AmiWebScriptManagerForLayout sm = this.series.getPortlet().getScriptManager();
		com.f1.base.CalcTypes variables;
		variables = table == null ? getVariables(this.series.getDataModelSchema()) : getVariables(table);
		if (type == TYPE_AXIS)
			variables = AmiWebChartAxisPortlet.VARTYPES;
		try {
			calc = sm.toAggCalc(text, variables, this.series.getMethodFactory(), series.getLayer(), null);
			switch (type) {
				case TYPE_COLOR:
					break;
				case TYPE_BOOLEAN:
					if (!Boolean.class.isAssignableFrom(calc.getReturnType())) {
						sb.append("Error in ").append(getFullLabel()).append(" return type must be boolean, not ");
						sb.append(sm.forType(calc.getReturnType()));
						return false;
					}
					break;
				case TYPE_SHAPE:
				case TYPE_POSITION:
					if (!String.class.isAssignableFrom(calc.getReturnType())) {
						sb.append("Error in ").append(getFullLabel()).append(" return type must be a string, not ");
						sb.append(sm.forType(calc.getReturnType()));
						return false;
					}
					break;
				case TYPE_NUMBER:
					if (!Number.class.isAssignableFrom(calc.getReturnType())) {
						sb.append("Error in ").append(getFullLabel()).append(" return type must be a number, not ");
						sb.append(sm.forType(calc.getReturnType()));
						return false;
					}
					break;
				case TYPE_SORTABLE:
					if (!Comparable.class.isAssignableFrom(calc.getReturnType())) {
						sb.append("Error in ").append(getFullLabel()).append(" return type must be sortable, not ");
						sb.append(sm.forType(calc.getReturnType()));
						return false;
					}
					break;
				case TYPE_CONST:
					if (!calc.isConst()) {
						sb.append("Error in ").append(getFullLabel()).append(" must be a constant");
						return false;
					}
					break;
				case TYPE_STRING:
					break;
			}
		} catch (Exception e) {
			LH.warning(log, e);
			sb.append("Syntax error for ").append(getFullLabel()).append(" ").append(e.getMessage());
			return false;
		}
		return true;
	}
	@Override
	public Class<?> getFormulaReturnType() {
		switch (type) {
			case TYPE_BOOLEAN:
				return Boolean.class;
			case TYPE_SHAPE:
			case TYPE_POSITION:
			case TYPE_FONT:
				return String.class;
			case TYPE_NUMBER:
				return Number.class;
			case TYPE_SORTABLE:
				return Comparable.class;
			case TYPE_CONST:
			case TYPE_STRING:
			case TYPE_COLOR:
			case TYPE_AXIS:
			case TYPE_LINE_TYPE:
				return Object.class;
			default:
				throw new RuntimeException("unknown type: " + type);
		}
	}
	public String getFullLabel() {
		return this.labelGroup + " --> " + label;
	}

	public Object getDataInner(ReusableCalcFrameStack sf) {
		if (this.calc == null)
			return null;
		return this.calc.get(sf);
	}
	public Object getData(ReusableCalcFrameStack rsf) {
		if (this.calc == null)
			return null;
		if (this.series.hasAggregates()) {
			String val = AmiUtils.s(OH.noNull(this.series.getNameFormula().getDataInner(rsf), ""));
			Grouping list = this.series.getUserSelectedGroupings().get(val);
			this.series.resetAggregatesForRows(list.getOrigRows(), rsf);
		}
		return (Object) this.calc.get(rsf);
	}

	public void getDependencies(Set<Object> types) {
		DerivedHelper.getDependencyIds(this.calc, types);
	}
	public byte getType() {
		return type;
	}
	public Object getConstValue() {
		return constValue;
	}

	public Number getMin() {
		buildMinMaxCache();
		return minCache;
	}
	public Number getMax() {
		buildMinMaxCache();
		return maxCache;
	}
	private void buildMinMaxCache() {
		if (minMaxCached)
			return;
		minCache = maxCache = null;
		PrimitiveMath<Number> pm = PrimitiveMathManager.INSTANCE.getNoThrow(this.getReturnType());
		if (pm != null) {
			Number min = null;
			Number max = null;
			for (Grouping g : this.series.getUserSelectedGroupings().values()) {
				for (Object value : g.getValuesForFormula(name)) {
					if (value instanceof Number && MH.isNumber((Number) value)) {
						min = OH.minAvoidNull(pm, min, (Number) value);
						max = OH.maxAvoidNull(pm, max, (Number) value);
					}
				}
			}
			this.minCache = min;
			this.maxCache = max;
			this.minMaxCached = true;
		} else {
			double min = Double.NaN;
			double max = Double.NaN;
			for (Grouping g : this.series.getUserSelectedGroupings().values()) {
				for (Object value : g.getValuesForFormula(name)) {
					if (value instanceof Number && MH.isNumber((Number) value)) {
						min = MH.minAvoidNan(min, ((Number) value).doubleValue());
						max = MH.maxAvoidNan(max, ((Number) value).doubleValue());
					}
				}
			}
			this.minCache = min == min ? min : null;
			this.maxCache = max == max ? max : null;
			this.minMaxCached = true;
		}
	}
	public Set<Object> getUniqueValues() {
		uniqueCache.clear();
		for (Grouping g : this.series.getUserSelectedGroupings().values()) {
			uniqueCache.addAll(g.getValuesForFormula(name));
		}
		return uniqueCache;
	}
	public Class<?> getVariableReturnType(String varname) {
		if (SH.isnt(varname)) {
			return null;
		}
		AmiWebScriptManagerForLayout sm = series.getPortlet().getScriptManager();
		com.f1.base.CalcTypes variables = getVariables(series.getDataModelSchema());
		if (variables.getType(varname) == null) {
			return null;
		}
		return sm.toAggCalc(varname, variables, series.getMethodFactory(), series.getLayer(), null).getReturnType();
	}

	@Override
	public String toString() {
		return this.name + " = " + this.value;
	}
	public DerivedCellCalculator getCalc() {
		return this.calc;
	}

	public void init(Object object) {
		setValue((String) object);
	}

	public AmiWebChartFormula setXBound() {
		this.isXBound = true;
		return this;
	}
	public AmiWebChartFormula setYBound() {
		this.isYBound = true;
		return this;
	}

	public boolean isXBound() {
		return this.isXBound;
	}
	public boolean isYBound() {
		return this.isYBound;
	}

	@Override
	public DerivedCellCalculator getFormulaCalc() {
		return this.calc;
	}

	@Override
	public String getFormulaId() {
		return this.name;
	}

	@Override
	public AmiWebFormulas getOwnerFormulas() {
		return this.series;
	}

	@Override
	public String getFormula(boolean override) {
		return this.value.getValue(override);
	}

	@Override
	public void setFormula(String amiscript, boolean override) {
		setValue(amiscript, null, override);
		recompileFormula();

	}

	@Override
	public String getFormulaConfig() {
		return getFormula(false);
	}

	@Override
	public void initFormula(String value) {
		setFormula(value, true);
	}

	@Override
	public void recompileFormula() {
		this.exception = null;
		if (this.value.get() == null && !this.hasFormulaOverride() && this.currentCalc == null)
			return;
		DerivedCellCalculator old = this.currentCalc;
		this.usedConstVars.clear();
		try {
			this.calc = series.parseFormula(this, this.getFormula(false), this.usedConstVars);
		} catch (Exception e) {
			this.exception = e;
		}
		if (value.isOverride()) {
			try {
				this.currentCalc = series.parseFormula(this, this.getFormula(true), this.usedConstVars);
			} catch (Exception e) {
				this.overrideException = e;
				this.currentCalc = null;
			}
		} else {
			this.currentCalc = calc;
		}
		if (currentCalc != null)
			this.constValue = currentCalc.isConst() ? currentCalc.get(null) : null;
		if (!DerivedHelper.areSame(old, currentCalc))
			this.getOwnerFormulas().fireOnFormulaChanged(this, old, currentCalc);

	}

	@Override
	public Exception testFormula(String str) {
		try {
			series.parseFormula(this, str, null);
		} catch (Exception e) {
			return e;
		}
		return null;
	}
	@Override
	public DerivedCellCalculator toCalc(String str, Set<String> usedVarConstsSink) {
		return series.parseFormula(this, str, usedVarConstsSink);
	}

	@Override
	public Exception getFormulaError(boolean override) {
		return value.isOverride() && override ? this.overrideException : this.exception;
	}

	@Override
	public boolean hasFormulaOverride() {
		return this.value.isOverride();
	}

	@Override
	public void clearFormulaOverride() {
		if (this.value.clearOverride()) {
			setValue(this.value.get(), null, false);
			recompileFormula();
		}
	}

	@Override
	public String getAri() {
		return this.getOwnerFormulas().getThis().getAri() + AmiWebConsts.FORMULA_PREFIX_DELIM + this.getFormulaId();
	}

	@Override
	public byte getFormulaType() {
		return FORMULA_TYPE_AGG;
	}

	@Override
	public Set<String> getUsedConstVars() {
		return this.usedConstVars;
	}
}
