package com.f1.ami.center.hdb.qry;

import java.util.LinkedHashSet;
import java.util.Set;

import com.f1.ami.center.hdb.AmiHdbColumn;
import com.f1.base.Caster;
import com.f1.utils.SH;
import com.f1.utils.sql.DerivedCellCalculator_SqlInSingle;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorConst;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorRef;

public class AmiHdbQuery_In implements AmiHdbQueryPart {

	final private AmiHdbColumn column;
	final private Set<Comparable> values;

	public AmiHdbQuery_In(AmiHdbColumn amiHistoryColumn, Set<Comparable> values) {
		this.column = amiHistoryColumn;
		LinkedHashSet<Comparable> t = new LinkedHashSet<Comparable>();
		Caster<? extends Comparable> c = this.column.getTypeCaster();
		for (Comparable i : values)
			t.add(c.castNoThrow(i));
		this.values = t;
	}

	@Override
	public AmiHdbColumn getColumn() {
		return column;
	}

	public Set<Comparable> getValues() {
		return values;
	}

	@Override
	public int getScore() {
		return SCORE_IN;
	}

	@Override
	public boolean matches(Comparable val) {
		return values.contains(column.getTypeCaster().castNoThrow(val));
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append(column).append(" IN (");
		SH.join(",", this.values, sink);
		return sink.append(")");
	}

	@Override
	public DerivedCellCalculator toDcc() {
		DerivedCellCalculatorRef ref = new DerivedCellCalculatorRef(0, column.getType(), column.getId());
		DerivedCellCalculator[] vals = new DerivedCellCalculatorRef[values.size()];
		int i = 0;
		for (Comparable v : values)
			vals[i++] = new DerivedCellCalculatorConst(0, v);
		return new DerivedCellCalculator_SqlInSingle(0, ref, vals, ref.getReturnType());
	}

}
