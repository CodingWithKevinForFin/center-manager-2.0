package com.f1.utils.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.Caster;
import com.f1.base.Table;
import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorSqlCast implements DerivedCellCalculator {

	final private Class<?> type;
	final private int position;
	final private DerivedCellCalculator right;
	final private Caster caster;

	public DerivedCellCalculatorSqlCast(int position, Class type, DerivedCellCalculator right, Caster caster) {
		this.type = type;
		this.position = position;
		this.right = right;
		this.caster = caster;
	}

	@Override
	public Object get(CalcFrameStack lcvs) {
		Object o = right.get(lcvs);
		return run(lcvs, o);
	}

	private Object run(CalcFrameStack lcvs, Object o) {
		if (o instanceof FlowControl) {
			if (o instanceof TableReturn) {
				TableReturn tr = (TableReturn) o;
				if (tr.getTables().size() > 0)
					o = tr.getTables().get(0);
				else if (tr.getReturnType() != null)
					o = tr.getReturnValue();
				else
					o = null;

			}
			if (o instanceof FlowControlPause)
				return DerivedHelper.onFlowControl((FlowControlPause) o, this, lcvs, 0, null);
		}
		if (o instanceof Table) {
			Table t = (Table) o;
			if (type == Table.class)
				return t;
			int rowsCount = t.getSize();
			int colsCount = t.getColumnsCount();
			if (type == List.class) {
				if (colsCount == 1) {
					List r = new ArrayList(rowsCount);
					for (int i = 0; i < rowsCount; i++)
						r.add(t.getAt(i, 0));
					return r;
				}
			} else if (type == Set.class) {
				if (colsCount == 1) {
					HashSet r = new LinkedHashSet(rowsCount);
					for (int i = 0; i < rowsCount; i++)
						r.add(t.getAt(i, 0));
					return r;
				}
			} else if (type == Map.class) {
				if (rowsCount == 1)
					return new HashMap(t.getRows().get(0));
			} else {
				if (rowsCount == 1 && colsCount == 1) {
					return this.caster.cast(t.getAt(0, 0), false, false);
				}
			}
		}
		return caster.cast(o, false, false);
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append("((").append(type.getName()).append(")");
		return right.toString(sink).append(")");
	}

	@Override
	public Class<?> getReturnType() {
		return this.type;
	}

	@Override
	public int getPosition() {
		return this.position;
	}

	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculatorSqlCast(position, type, right.copy(), caster);
	}

	@Override
	public boolean isConst() {
		return this.right.isConst();
	}

	@Override
	public boolean isReadOnly() {
		return this.right.isReadOnly();
	}

	@Override
	public int getInnerCalcsCount() {
		return 1;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return this.right;
	}

	@Override
	public Object resume(PauseStack paused) {
		return run(paused.getLcvs(), paused.getNext().resume());
	}

	@Override
	public boolean isPausable() {
		return false;
	}
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other.getClass() != this.getClass())
			return false;
		DerivedCellCalculatorSqlCast o = (DerivedCellCalculatorSqlCast) other;
		return OH.eq(this.type, o.type) && right.isSame(o.right);
	}

}
