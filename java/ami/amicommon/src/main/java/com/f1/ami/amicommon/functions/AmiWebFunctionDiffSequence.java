package com.f1.ami.amicommon.functions;

import java.util.Set;

import com.f1.base.Column;
import com.f1.base.Mapping;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.Hasher;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.diff.SequenceDiffer;
import com.f1.utils.diff.SequenceDiffer.Block;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculatorN;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionDiffSequence extends AbstractMethodDerivedCellCalculatorN {

	private final static ParamsDefinition VERIFIER = new ParamsDefinition("diffSequence", Table.class,
			"com.f1.base.Table l,com.f1.base.Table r,String lcol,String rcol,java.util.Map options");
	static {
		VERIFIER.addDesc("Returns a Table with a combination of all columns.");
		VERIFIER.addParamDesc(0, "left table");
		VERIFIER.addParamDesc(1, "right table");
		VERIFIER.addParamDesc(2, "column from left table to diff");
		VERIFIER.addParamDesc(3, "column from right table to diff");
		VERIFIER.addParamDesc(4, "place holder for options");
	}

	public AmiWebFunctionDiffSequence(int position, DerivedCellCalculator[] params) {
		super(position, params);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object values[]) {
		Table l = (Table) values[0];
		Table r = (Table) values[1];
		String lColStr = (String) values[2];
		String rColStr = (String) values[3];
		Column lCol = l.getColumnsMap().get(lColStr);
		if (lCol == null)
			return null;
		Column rCol = r.getColumnsMap().get(rColStr);
		if (rCol == null)
			return null;
		int totSize = l.getColumnsCount() + r.getColumnsCount();
		String[] ids = new String[totSize + 1];
		Class<?>[] types = new Class[totSize + 1];
		int pos = 0;
		ids[0] = "diff_block";
		types[0] = Integer.class;
		pos++;
		Set<String> used = new HasherSet<String>();
		for (Column col : l.getColumns()) {
			types[pos] = col.getType();
			String id = (String) col.getId();
			used.add(id);
			ids[pos] = id;
			pos++;
		}
		for (Column col : r.getColumns()) {
			types[pos] = col.getType();
			String id = (String) col.getId();
			if (used.contains(id))
				id = r.getTitle() + "_" + id;
			id = SH.getNextId(id, used, 2);
			used.add(id);
			ids[pos] = id;
			pos++;
		}
		Table out = new ColumnarTable(types, ids);
		SequenceDiffer<Row> diff = new SequenceDiffer<Row>(new Comparator(l, lCol.getLocation(), rCol.getLocation()), l.getRows().toRowsArray(), r.getRows().toRowsArray());
		int blockNum = 0;
		for (Block<Row> block : diff.getBlocks()) {
			for (int n = 0; n < block.getMaxCount(); n++)
				add(blockNum, out, block.getLeftOrNull(n), block.getRightOrNull(n));
			blockNum++;
		}
		return out;
	}
	@Override
	protected Object shortCircuit(int i, Object val) {
		return val == null ? null : KEEP_GOING;
	}

	private void add(int blockNum, Table out, Row left, Row right) {
		Row row = out.newEmptyRow();
		row.putAt(0, blockNum);
		if (left != null)
			for (int i = 0; i < left.size(); i++)
				row.putAt(i + 1, left.getAt(i));
		if (right != null) {
			int start = row.size() - right.size();
			for (int i = 0; i < right.size(); i++)
				row.putAt(start + i, right.getAt(i));
		}
		out.getRows().add(row);
	}

	private static class Comparator implements Hasher<Row> {

		private int loc1;
		private int loc2;
		private Table leftTable;

		public Comparator(Table l, int loc1, int loc2) {
			this.loc1 = loc1;
			this.loc2 = loc2;
			this.leftTable = l;
		}

		@Override
		public boolean areEqual(Row left, Row right) {
			return OH.eq(getVal(left), getVal(right));
		}

		private Object getVal(Row row) {
			if (row.getTable() == leftTable)
				return row.getAt(loc1);
			else
				return row.getAt(loc2);
		}

		@Override
		public int hashcode(Row o) {
			return OH.hashCode(getVal(o));
		}

	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator[] params2) {
		return new AmiWebFunctionDiffSequence(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionDiffSequence(position, calcs);
		}

	}

}
