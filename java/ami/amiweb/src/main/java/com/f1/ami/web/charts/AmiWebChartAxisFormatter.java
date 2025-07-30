package com.f1.ami.web.charts;

import com.f1.ami.web.AmiWebFormulaImpl;
import com.f1.ami.web.AmiWebFormulas;
import com.f1.ami.web.AmiWebOverrideValue;
import com.f1.ami.web.AmiWebScriptManagerForLayout;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public abstract class AmiWebChartAxisFormatter extends AmiWebFormulaImpl {
	public static final byte AXIS_DATA_TYPE_UNKNOWN = 0;
	public static final byte AXIS_DATA_TYPE_CONST = 1;
	public static final byte AXIS_DATA_TYPE_WHOLE = 2;
	public static final byte AXIS_DATA_TYPE_REAL = 3;
	public static final byte AXIS_DATA_TYPE_UTC = 4;
	public static final byte AXIS_DATA_TYPE_UTCN = 5;
	private static final String FORMULA_ID = "format";
	protected DerivedCellCalculator formatter;
	protected final AmiWebChartAxisPortlet axis;
	private AmiWebOverrideValue<String> formula = new AmiWebOverrideValue<String>(null);
	final private ReusableCalcFrameStack stackFrame;

	public AmiWebChartAxisFormatter(AmiWebChartAxisPortlet axis, AmiWebFormulas owner) {
		super(FORMULA_ID, owner, FORMULA_TYPE_NORMAL, Object.class);
		this.axis = axis;
		this.stackFrame = axis.getChart().getStackFrame();
	}

	protected ReusableCalcFrameStack getStackFrame() {
		return this.stackFrame;
	}

	abstract public String format(Number val);
	abstract public double calcUnitSize(Number d, Number diff, byte axisDataType);
	abstract public Number roundUp(Number max, Number size);
	abstract public Number roundDown(Number min, Number size);
	abstract public double calcMinorUnitSize(Number majorUnit);
	abstract public void useDefaultFormatter(Number majorUnit, Number minValue, Number maxValue, byte axisDataType);
	abstract public String formatExact(Number val);

	final protected void onFormatChanged(String numberFormula) {
		this.setFormula(numberFormula, false);
	}

	@Override
	public String getFormula(boolean override) {
		return this.formula.getValue(override);
	}

	@Override
	public void setFormula(String amiscript, boolean override) {
		if (this.formula.setValue(amiscript, override)) {
			if (this.axis.getChart().isInitDone()) {
				AmiWebScriptManagerForLayout sm = this.axis.getChart().getScriptManager();
				this.formatter = sm.toCalc(amiscript, AmiWebChartAxisPortlet.VARTYPES, this.axis, getUsedConstVars());
				this.axis.flagViewStale();
			}
		}
	}

	@Override
	public DerivedCellCalculator getFormulaCalc() {
		return formatter;
	}

	@Override
	public String getFormulaId() {
		return FORMULA_ID;
	}

	@Override
	public AmiWebFormulas getOwnerFormulas() {
		return this.axis.getFormulas();
	}

	@Override
	public String getFormulaConfig() {
		return getFormula(false);
	}

	@Override
	public void initFormula(String value) {
		setFormula(value, false);
	}
}
