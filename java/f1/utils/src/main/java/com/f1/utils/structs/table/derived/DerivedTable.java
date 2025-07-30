package com.f1.utils.structs.table.derived;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.table.BasicColumn;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ChildCalcTypesStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class DerivedTable extends BasicTable {

	private static final Logger log = LH.get();
	private DerivedColumn[] derivedColumns = new DerivedColumn[0];//all derived columns, null indicates non-derived
	private int[] derivedColumnLocations = OH.EMPTY_INT_ARRAY;//locations of all derived columns
	private int[][] dependentColumns = new int[0][0];//null if no dependencies on column
	private ReusableCalcFrameStack stackframe;

	public DerivedTable(CalcFrameStack sf, Class col1Class, String col1Id, Object... moreColumns) {
		super(col1Class, col1Id, moreColumns);
		this.stackframe = new ReusableCalcFrameStack(sf);
		updateDerivedColumns();

	}

	public DerivedTable(CalcFrameStack sf, Class<?>[] colTypes, String[] columnIds, int size) {
		super(colTypes, columnIds, size);
		this.stackframe = new ReusableCalcFrameStack(sf);
		updateDerivedColumns();
	}

	public DerivedTable(CalcFrameStack sf, Class<?>[] colTypes, String[] columnIds) {
		super(colTypes, columnIds);
		this.stackframe = new ReusableCalcFrameStack(sf);
		updateDerivedColumns();
	}

	public DerivedTable(CalcFrameStack sf, Column[] columns) {
		super(columns);
		this.stackframe = new ReusableCalcFrameStack(sf);
		updateDerivedColumns();
	}

	public DerivedTable(CalcFrameStack sf, String[] columnIds) {
		super(columnIds);
		this.stackframe = new ReusableCalcFrameStack(sf);
		updateDerivedColumns();
	}

	public DerivedTable(CalcFrameStack sf, Table source) {
		super(source.getColumns(), source.getSize());
		this.stackframe = new ReusableCalcFrameStack(sf);
		updateDerivedColumns();
		setTitle(source.getTitle());
		TableList rows = getRows();
		for (Row row : source.getRows())
			rows.add(newRow(row.getValues()));
	}

	public DerivedTable(CalcFrameStack sf) {
		super();
		this.stackframe = new ReusableCalcFrameStack(sf);
		updateDerivedColumns();
	}
	public <T> DerivedColumn addDerivedColumn(int location, String id, DerivedCellCalculator calc) {
		DerivedColumn column = new DerivedColumn(this, uid++, location, id, calc);
		addColumn(location, column, DerivedRow.NOT_CACHED);
		return column;
	}
	public <T> DerivedColumn addDerivedColumn(String id, DerivedCellCalculator calc) {
		return addDerivedColumn(getColumnsCount(), id, calc);
	}
	@Override
	protected void addColumn(int location, BasicColumn col, Object defaultValue) {
		super.addColumn(location, col, defaultValue);
		updateDerivedColumns();
	}
	@Override
	public DerivedRow newRow(Object... values) {
		int colsCount = getColumnsCount();
		if (colsCount == values.length) {
			return newDerivedRow(values, true);
		} else if (values.length == colsCount - this.derivedColumnLocations.length) {
			Object[] values2 = new Object[colsCount];
			for (int i = 0, pos = 0; i < colsCount; i++)
				values2[i] = this.derivedColumns[i] == null ? values[pos++] : DerivedRow.NOT_CACHED;
			return newDerivedRow(values2, false);
		} else if (colsCount == values.length) {
			return newDerivedRow(values, true);
		} else {
			if (this.derivedColumnLocations.length == 0)
				throw new RuntimeException("incorrect number of cells, should be " + colsCount + ", not " + values.length);
			else
				throw new RuntimeException("incorrect number of cells, should be " + colsCount + " or " + (colsCount - this.derivedColumnLocations.length));
		}
	}

	protected DerivedRow newDerivedRow(Object[] values, boolean b) {
		return new DerivedRow(this, uid++, values, b);
	}

	@Override
	public void renameColumn(int location, String newId) {
		Column col = getColumnAt(location);
		if (col.getId().equals(newId))
			return;
		if (dependentColumns[location] != null)
			throw new RuntimeException("Can not remove dependent column: " + newId);
		super.renameColumn(location, newId);
	}
	@Override
	public void removeColumn(int location) {
		if (dependentColumns[location] != null) {
			debugDependencies();
			throw new RuntimeException("Can not remove dependent column: " + getColumnAt(location).getId());
		}
		super.removeColumn(location);
		updateDerivedColumns();
	}

	public void debugDependencies() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.getColumnsCount(); i++) {
			Column column = this.getColumnAt(i);
			sb.append(i).append(": ").append(column).append(SH.NEWLINE);
			int[] dl = this.dependentColumns[i];
			if (AH.isntEmpty(dl))
				for (int d : dl) {
					sb.append("  ==> ").append(d).append(SH.NEWLINE);
				}
		}
		LH.info(log, "Colummn for " + getTitle() + ": ", sb);
	}

	public boolean hadDependentColumns(String columnId) {
		return dependentColumns[getColumn(columnId).getLocation()] != null;
	}
	public int getDependentColumnsCount(String columnId) {
		int[] deps = dependentColumns[getColumn(columnId).getLocation()];
		return deps == null ? 0 : deps.length;
	}

	private void updateDerivedColumns() {
		int colsCount = getColumnsCount();
		derivedColumns = new DerivedColumn[colsCount];
		int cnt = 0;
		BasicMultiMap.Set<Integer, Integer> dependents = new BasicMultiMap.Set<Integer, Integer>();
		Set<Integer> dependencies = new HashSet<Integer>();
		for (final Column d : getColumns()) {
			if (d instanceof DerivedColumn) {
				final DerivedColumn dc = (DerivedColumn) d;
				derivedColumns[d.getLocation()] = dc;
				final DerivedCellCalculator calc = dc.getCalculator();
				dependencies.clear();
				initColumns(calc, getColumnsMap(), dependencies);
				//				calc.initColumns(getColumnsMap());
				//				calc.getDependencies(dependencies);
				//				getDependencies(calc, dependencies);
				for (Integer i : dependencies)
					dependents.putMulti(i, d.getLocation());
				cnt++;
			}
		}
		this.derivedColumnLocations = new int[cnt];
		this.dependentColumns = new int[colsCount][];
		for (int j = 0, i = 0; i < colsCount; i++) {
			if (this.derivedColumns[i] != null)
				this.derivedColumnLocations[j++] = i;
			Set<Integer> dep = dependents.get(i);
			if (dep != null)
				this.dependentColumns[i] = AH.toArrayInt(dep);
		}
	}

	private void initColumns(DerivedCellCalculator calc, Map<String, Column> columnsMap, Set<Integer> dependenciesSink) {
		if (calc instanceof DerivedCellCalculatorRef && !calc.isConst()) {
			DerivedCellCalculatorRef ref = (DerivedCellCalculatorRef) calc;
			int loc = CH.getOrThrow(columnsMap, (String) ref.getId()).getLocation();
			ref.setLoc(loc);
			dependenciesSink.add(loc);
		}
		for (int i = 0, l = calc.getInnerCalcsCount(); i < l; i++)
			initColumns(calc.getInnerCalcAt(i), columnsMap, dependenciesSink);

	}

	@Override
	public DerivedRow newEmptyRow() {
		return newDerivedRow(new Object[getColumnsCount()], true);
	}

	/**
	 * @param columnLocation
	 * @return null if not derived
	 */
	public DerivedColumn getDerivedColumn(int columnLocation) {
		return derivedColumns[columnLocation];
	}

	public int[] getDerivedColumns() {
		return derivedColumnLocations;
	}

	public int[] getDependentColumns(int i) {
		return dependentColumns[i];
	}

	//	public DerivedCellCalculator createDerivedCellCalculator(DerivedCellParser parser, String expression, MethodFactoryManager methodFactory) {
	//		return parser.toCalc(expression, this.getColumnTypesMapping(), methodFactory);
	//	}
	//	public DerivedCellCalculator createDerivedCellCalculator(DerivedCellParser parser, Node expression, MethodFactoryManager methodFactory) {
	//		return parser.toCalcFromNode(expression, this.getColumnTypesMapping(), methodFactory);
	//	}

	public DerivedColumn addDerivedColumn(DerivedCellParser parser, String id, String expression, MethodFactoryManager methodFactory) {
		return addDerivedColumn(id, parser.toCalc(expression, new ChildCalcTypesStack(this.stackframe, true, this.getColumnTypesMapping(), methodFactory)));
	}

	public ReusableCalcFrameStack getStackFrame() {
		return this.stackframe;
	}

}
