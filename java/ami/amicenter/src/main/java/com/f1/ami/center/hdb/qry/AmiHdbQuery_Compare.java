package com.f1.ami.center.hdb.qry;

import com.f1.ami.center.hdb.AmiHdbColumn;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorConst;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorMath;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorRef;

public abstract class AmiHdbQuery_Compare implements AmiHdbQueryPart {
	static final public byte EQ = 0;
	static final public byte GE = 2;
	static final public byte GT = 3;
	static final public byte LT = 4;
	static final public byte LE = 5;
	static final public byte NE = 6;
	static final public byte TT = 7;//MATCH ~~
	static final public byte ET = 8;//Regex =~
	static final public byte NT = 9;//Regex Not eq !~

	final protected AmiHdbColumn column;
	final protected Comparable<?> value;

	public AmiHdbQuery_Compare(AmiHdbColumn column, Comparable<?> value) {
		this.column = column;
		this.value = column.getTypeCaster().castNoThrow(value);
	}

	@Override
	final public AmiHdbColumn getColumn() {
		return column;
	}

	final public Comparable<?> getValue() {
		return value;
	}

	@Override
	final public boolean matches(Comparable val) {
		val = this.column.getTypeCaster().castNoThrow(val);
		return matchesInner(val);
	}

	@Override
	final public String toString() {
		return toString(new StringBuilder()).toString();
	}
	@Override
	final public StringBuilder toString(StringBuilder sink) {
		sink.append(column);
		sink.append(' ');
		sink.append(OperationNode.toString(getOperatorNodeCode()));
		sink.append(' ');
		return sink.append(value);
	}

	@Override
	final public DerivedCellCalculator toDcc() {
		return DerivedCellCalculatorMath.valueOf(0, getOperatorNodeCode(), new DerivedCellCalculatorRef(0, column.getType(), column.getId()),
				new DerivedCellCalculatorConst(0, this.value));
	}

	@Override
	public abstract int getScore();
	protected abstract boolean matchesInner(Comparable val);
	protected abstract byte getOperatorNodeCode();
	public abstract byte getType();
}
