package com.f1.utils.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.f1.base.Caster;
import com.f1.utils.AH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.impl.ArrayHasher;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculator_SqlIn implements DerivedCellCalculator {

	final private DerivedCellCalculator[] left;
	final private int position;
	final private DerivedCellCalculator[][] values;
	final private DerivedCellCalculator[][] varValues;
	private Set<Object[]> rowsSet = new HasherSet<Object[]>(ArrayHasher.INSTANCE);
	final private Object[] tmp;
	final private Class<?>[] types;
	final private Caster<?>[] casters;

	public DerivedCellCalculator_SqlIn(int position, DerivedCellCalculator[] left, DerivedCellCalculator[][] values, Class[] types) {
		this.position = position;
		this.left = left;

		this.values = values;
		tmp = new Object[left.length];
		this.types = types;
		this.casters = OH.getAllCasters(types);
		List<DerivedCellCalculator[]> varValues = null;
		outer: for (DerivedCellCalculator d[] : values) {
			for (DerivedCellCalculator dcc : d) {
				if (!dcc.isConst()) {
					if (varValues == null)
						varValues = new ArrayList<DerivedCellCalculator[]>();
					varValues.add(d);
					continue outer;
				}
			}
			Object o[] = new Object[d.length];
			for (int i = 0; i < d.length; i++)
				o[i] = this.casters[i].cast(d[i].get(null));
			rowsSet.add(o);
		}
		if (varValues != null)
			this.varValues = AH.toArray(varValues, DerivedCellCalculator[].class);
		else
			this.varValues = null;
	}

	@Override
	public Object get(CalcFrameStack sf) {
		for (int i = 0; i < tmp.length; i++)
			tmp[i] = this.casters[i].cast(this.left[i].get(sf));
		if (rowsSet.contains(tmp))
			return true;
		if (varValues != null) {
			outer: for (DerivedCellCalculator[] d : varValues) {
				for (int i = 0; i < d.length; i++)
					if (!OH.eq(this.casters[i].cast(d[i].get(sf)), tmp[i]))
						continue outer;
				return true;
			}
		}
		return false;
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append('(');
		boolean first = true;
		for (DerivedCellCalculator i : left) {
			if (first)
				first = false;
			else
				sink.append(',');
			i.toString(sink);
		}
		sink.append(") in (");
		first = true;
		for (DerivedCellCalculator[] i : values) {
			if (first)
				first = false;
			else
				sink.append(',');
			sink.append("(");
			sink.append(SH.join(',', i));
			sink.append(")");
		}
		sink.append(")");
		//TODO
		return sink;
	}

	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public Class<?> getReturnType() {
		return Boolean.class;
	}

	@Override
	public int getPosition() {
		return this.position;
	}

	@Override
	public DerivedCellCalculator copy() {
		DerivedCellCalculator[] leftCopies = left.clone();
		for (int i = 0; i < leftCopies.length; i++)
			leftCopies[i] = leftCopies[i].copy();
		return new DerivedCellCalculator_SqlIn(position, leftCopies, values, types);
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	public DerivedCellCalculator[] getLeft() {
		return this.left;
	}

	public DerivedCellCalculator[][] getInValues() {
		return this.values;
	}

	@Override
	public int getInnerCalcsCount() {
		return left.length + left.length * values.length;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int m) {
		return m < left.length ? left[m] : values[(m / left.length) - 1][m % left.length];

	}
	@Override
	public Object resume(PauseStack paused) {
		throw new IllegalStateException();
	}
	@Override
	public boolean isPausable() {
		return false;
	}

	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other.getClass() != this.getClass())
			return false;
		DerivedCellCalculator_SqlIn o = (DerivedCellCalculator_SqlIn) other;
		if (!AH.eq(types, o.types))
			return false;
		if (!DerivedHelper.areSame(left, o.left))
			return false;
		if (!DerivedHelper.areSame(values, o.values))
			return false;
		if (!DerivedHelper.areSame(varValues, o.varValues))
			return false;
		return true;
	}

}
