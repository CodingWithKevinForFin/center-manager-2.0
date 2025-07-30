package com.f1.utils.structs.table.derived;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.base.Caster;
import com.f1.utils.AH;
import com.f1.utils.CasterManager;
import com.f1.utils.OH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.structs.FastSmallMap;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorOr implements DerivedCellCalculator, DerivedCellCalculatorWithDependencies {

	private static class InClause {
		private final String varName;
		private final Class varType;
		private final FastSmallMap<Class, Set<Object>> values;
		private boolean hasNull = false;
		private Caster[] casters;
		private Set<Object> builtValues[];

		public InClause(String varName, Class varType) {
			this.varName = varName;
			this.varType = varType;
			this.values = new FastSmallMap<Class, Set<Object>>();
		}

		public void addValue(Class type, Object value) {
			if (value == null)
				hasNull = true;
			else {
				Entry<Class, Set<Object>> t = values.getOrCreateEntry(type);
				Set<Object> set = t.getValue();
				if (set == null)
					t.setValue(set = new HashSet<Object>());
				set.add(value);
			}
		}

		public void build() {
			int pos = 0;
			this.builtValues = new Set[values.size()];
			this.casters = new Caster[values.size()];
			for (Entry<Class, Set<Object>> e : values.entrySet()) {
				Class c2 = e.getKey();
				Set<Object> values = new HashSet<Object>(e.getValue().size());

				if (CharSequence.class.isAssignableFrom(varType) || CharSequence.class.isAssignableFrom(c2)) {
					for (Object o : e.getValue())
						values.add(DerivedHelper.toString(o));
					this.casters[pos] = null;
				} else {
					this.casters[pos] = CasterManager.getCaster(OH.getWidest(varType, c2));
					for (Object o : e.getValue())
						values.add(casters[pos].cast(o, false, false));
				}
				this.builtValues[pos] = values;
				pos++;
			}
		}

		public boolean isIn(Object value) {
			if (value == null)
				return hasNull;
			for (int i = 0; i < casters.length; i++) {
				Caster caster = this.casters[i];
				if (builtValues[i].contains(caster == null ? DerivedHelper.toString(value) : caster.cast(value, false, false)))
					return Boolean.TRUE;
			}
			return false;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null || getClass() != obj.getClass())
				return false;
			final InClause other = (InClause) obj;
			return OH.eq(varName, other.varName) && OH.eq(varType, other.varType) && hasNull == other.hasNull && OH.eq(values, other.values) && AH.eq(casters, other.casters);
		}

	}

	private static final int MIN_IN_CLAUSE = 5;

	private DerivedCellCalculator[] params;
	private int position;
	private DerivedCellCalculator[] origParams;
	private InClause[] inVars;

	static public DerivedCellCalculator valueOf(int position, DerivedCellCalculator[] elements) {
		int count = 0;
		DerivedCellCalculator[] origElements = elements;
		FastSmallMap<String, InClause> inVars = null;
		for (int pos = 0; pos < elements.length; pos++) {
			DerivedCellCalculator i = elements[pos];
			if (i.getReturnType() != Boolean.class)
				throw new ExpressionParserException(i.getPosition(), "Invalid type for || clause: " + i.getReturnType());
			else if (i.isConst()) {
				if (toBoolean(i.get(null)))
					return new DerivedCellCalculatorConst(position, Boolean.TRUE, Boolean.class);
				if (origElements == elements)
					elements = elements.clone();
				elements[pos] = null;
			} else if (elements.length >= MIN_IN_CLAUSE && i instanceof DerivedCellCalculatorMath
					&& OperationNode.OP_EQ_EQ == (((DerivedCellCalculatorMath) i).getOperationNodeCode())) {
				DerivedCellCalculatorMath op = (DerivedCellCalculatorMath) i;
				DerivedCellCalculatorRef refCalc;
				DerivedCellCalculator valCalc;
				DerivedCellCalculator left = op.getLeft();
				DerivedCellCalculator right = op.getRight();
				if (left instanceof DerivedCellCalculatorRef && right.isConst()) {
					refCalc = (DerivedCellCalculatorRef) left;
					valCalc = right;
				} else if (right instanceof DerivedCellCalculatorRef && left.isConst()) {
					refCalc = (DerivedCellCalculatorRef) right;
					valCalc = left;
				} else {
					count++;
					continue;
				}
				if (inVars == null)
					inVars = new FastSmallMap<String, InClause>();
				final String name = (String) refCalc.getId();
				Entry<String, InClause> icv = inVars.getOrCreateEntry(name);
				InClause ic = icv.getValue();
				if (ic == null)
					icv.setValue(ic = new InClause(name, refCalc.getReturnType()));
				ic.addValue(valCalc.getReturnType(), valCalc.get(null));
				if (origElements == elements)
					elements = elements.clone();
				elements[pos] = null;
			} else
				count++;
		}
		if (inVars == null) {
			if (count == 0)
				return new DerivedCellCalculatorConst(position, Boolean.FALSE, Boolean.class);
			if (count == 1)
				for (DerivedCellCalculator i : elements)
					if (i != null)
						return i;
		}
		DerivedCellCalculator[] e2;
		if (count == 0) {
			e2 = DerivedHelper.EMPTY_ARRAY;
		} else if (count != elements.length) {
			e2 = new DerivedCellCalculator[count];
			int n = 0;
			for (DerivedCellCalculator i : elements) {
				if (i != null)
					e2[n++] = i;
				if (n == count)
					break;
			}
		} else
			e2 = elements;

		return new DerivedCellCalculatorOr(position, origElements, e2, inVars);
	}

	private DerivedCellCalculatorOr(int position, DerivedCellCalculator[] origParams, DerivedCellCalculator[] params, FastSmallMap<String, InClause> inVars2) {
		this.position = position;
		this.params = params;
		this.origParams = origParams;
		if (inVars2 == null) {
			this.inVars = null;
		} else {
			this.inVars = inVars2.valuesArray(InClause.class);
			for (InClause i : inVars)
				i.build();
		}
	}

	@Override
	public Object get(CalcFrameStack lcvs) {
		return eval(lcvs, 0);
	}
	public Object eval(CalcFrameStack lcvs, int n) {
		for (; n < params.length; n++) {
			DerivedCellCalculator i = params[n];
			Object object = i.get(lcvs);
			if (object instanceof FlowControlPause)
				return DerivedHelper.onFlowControl((FlowControlPause) object, this, lcvs, n, null);
			if (toBoolean(object))
				return Boolean.TRUE;
		}
		if (inVars != null) {
			for (InClause i : inVars)
				if (i.isIn(DerivedHelper.getValue(lcvs, i.varName)))
					return Boolean.TRUE;
		}
		return Boolean.FALSE;

	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append("(");
		for (int i = 0; i < origParams.length; i++) {
			if (i != 0)
				sink.append(" || ");
			origParams[i].toString(sink);
		}
		sink.append(")");

		return sink;
	}

	@Override
	public Set<Object> getDependencyIds(Set<Object> sink) {
		if (this.inVars != null)
			for (InClause i : this.inVars)
				sink.add(i.varName);
		return sink;
	}

	@Override
	public Class<?> getReturnType() {
		return Boolean.class;
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public DerivedCellCalculator copy() {
		return valueOf(position, DerivedHelper.copy(this.origParams));
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public boolean isReadOnly() {
		for (DerivedCellCalculator i : params)
			if (!i.isReadOnly())
				return false;
		return true;
	}

	@Override
	public int getInnerCalcsCount() {
		return this.params.length;
	}
	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return this.params[n];
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	private static boolean toBoolean(Object object) {
		return object == null ? false : ((Boolean) object).booleanValue();
	}

	public DerivedCellCalculator[] getParams() {
		return this.origParams;
	}

	@Override
	public Object resume(PauseStack paused) {
		Object r = paused.getNext().resume();
		if (r instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) r, this, paused.getLcvs(), paused.getState(), null);
		if (toBoolean(r))
			return Boolean.TRUE;
		return eval(paused.getLcvs(), paused.getState() + 1);
	}

	@Override
	public boolean isPausable() {
		return false;
	}
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other.getClass() != this.getClass())
			return false;
		else if (!DerivedHelper.childrenAreSame(this, other))
			return false;
		final DerivedCellCalculatorOr otherOr = (DerivedCellCalculatorOr) other;
		if (!AH.eq(this.inVars, otherOr.inVars))
			return false;
		return true;
	}

}
