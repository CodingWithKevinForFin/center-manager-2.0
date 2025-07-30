package com.f1.ami.amicommon.functions;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.Caster;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebFunctionSwitch extends AbstractMethodDerivedCellCalculator {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("switch", Object.class, "Object key,Object defaultValue,Object ... keyValueChoices");

	static {
		VERIFIER.addDesc(
				"Given a mapping of key/value pairs and a supplied key, returns the associated value, or a defaultValue if the supplied key does not exist in the provided mapping.");
		VERIFIER.addParamDesc(0, "the key used to look up a value in the map");
		VERIFIER.addParamDesc(1, "the defaultValue to return if the key is not found in supplied keyValueChoices");
		VERIFIER.addParamDesc(2, "An even number of arguments ordered such that: key1,value2,key2,value2,....  KEYS MUST EVALUATE TO CONSTANT EXPRESSIONS AND BE UNIQUE");
		VERIFIER.addExample(0, "NA", 0, "ZERO", 1, "ONE", 2, "TWO", null, "NULL");
		VERIFIER.addExample(2, "NA", 0, "ZERO", 1, "ONE", 2, "TWO", null, "NULL");
		VERIFIER.addExample(7, "NA", 0, "ZERO", 1, "ONE", 2, "TWO", null, "NULL");
		VERIFIER.addExample(null, "NA", 0, "ZERO", 1, "ONE", 2, "TWO", null, "NULL");
	}

	final private DerivedCellCalculator keyParam;
	final private DerivedCellCalculator defaultParam;
	final private Class<?> valType;
	final private Caster<?> keyCaster;
	final private Map<Object, DerivedCellCalculator> values;
	final private DerivedCellCalculator constValueParam;

	public AmiWebFunctionSwitch(int position, DerivedCellCalculator[] params) {
		super(position, params);

		VERIFIER.verify(this, false);
		keyParam = params[0];
		defaultParam = params[1];
		if (params.length % 2 != 0)
			throw new ExpressionParserException(position, "In key/value pairs there is a dangling key without a value");
		Class<?> valType = defaultParam.getReturnType() == Object.class ? null : defaultParam.getReturnType();
		Class<?> keyType = null;
		for (int i = 2; i < params.length; i += 2) {
			DerivedCellCalculator key = params[i];
			DerivedCellCalculator val = params[i + 1];
			if (!key.isConst())
				throw new ExpressionParserException(key.getPosition(), "Key must be a constant at argument " + i);
			keyType = OH.getWidestIgnoreNull(key.getReturnType(), keyType);
			if (val.getReturnType() != Object.class || !DerivedHelper.isNull(val))
				valType = OH.getWidestIgnoreNull(val.getReturnType(), valType);
		}
		if (keyType == null)
			keyType = Object.class;
		if (valType == null)
			valType = Object.class;
		this.valType = valType;
		this.keyCaster = OH.getCaster(keyType);

		Map<Object, DerivedCellCalculator> values = new HashMap<Object, DerivedCellCalculator>();
		for (int i = 2; i < params.length; i += 2) {
			DerivedCellCalculator key = params[i];
			DerivedCellCalculator val = params[i + 1];
			Object keyCast = this.keyCaster.cast(key.get(null));
			if (values.containsKey(keyCast))
				throw new ExpressionParserException(params[i].getPosition(), "Duplicate key at argument " + i + ": " + keyCast);
			values.put(keyCast, val);
		}
		if (keyParam.isConst()) {
			Object t = this.keyCaster.cast(keyParam.get(null));
			this.constValueParam = CH.getOr(values, t, defaultParam);
			this.values = null;
		} else if (values.isEmpty()) {
			this.constValueParam = defaultParam;
			this.values = null;
		} else {
			this.constValueParam = null;
			this.values = values;
		}
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}
	@Override
	public boolean isConst() {
		return (this.constValueParam != null && this.constValueParam.isConst()) || super.isConst();
	}
	@Override
	public Class<?> getReturnType() {
		return this.valType;
	}

	@Override
	public Object get(CalcFrameStack key) {
		if (this.constValueParam != null) {
			Object o = this.constValueParam.get(key);
			if (o instanceof FlowControlPause)
				DerivedHelper.onFlowControl((FlowControlPause) o, this, key, 0, null);
			return o;
		} else {
			Object o = keyParam.get(key);
			if (o instanceof FlowControlPause)
				DerivedHelper.onFlowControl((FlowControlPause) o, this, key, 1, null);
			DerivedCellCalculator dcc = CH.getOr(values, this.keyCaster.cast(o, false, false), defaultParam);
			Object p2 = dcc.get(key);
			if (p2 instanceof FlowControlPause)
				DerivedHelper.onFlowControl((FlowControlPause) o, this, key, 2, null);
			return p2;
		}
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator[] params2) {
		return new AmiWebFunctionSwitch(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionSwitch(position, calcs);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

	@Override
	final public Object resume(PauseStack paused) {
		Object o = paused.getNext().resume();
		if (o instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) o, this, paused.getLcvs(), paused.getState(), paused.getAttachment());
		if (paused.getState() == 0) {
			return o;
		} else if (paused.getState() == 1) {
			DerivedCellCalculator dcc = CH.getOr(values, this.keyCaster.cast(o), defaultParam);
			Object p2 = dcc.get(paused.getLcvs());
			if (p2 instanceof FlowControlPause)
				DerivedHelper.onFlowControl((FlowControlPause) o, this, paused.getLcvs(), 2, dcc);
			return p2;
		} else {//state==2
			return o;
		}
	}
	@Override
	public boolean isPausable() {
		return false;
	}

}
