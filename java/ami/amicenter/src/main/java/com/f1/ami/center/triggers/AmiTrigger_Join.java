package com.f1.ami.center.triggers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.center.sysschema.AmiSchema;
import com.f1.ami.center.table.AmiColumnImpl;
import com.f1.ami.center.table.AmiImdb;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiImdbScriptManager;
import com.f1.ami.center.table.AmiPreparedRowImpl;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.table.AmiTableImpl;
import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.base.LongIterator;
import com.f1.base.NameSpaceIdentifier;
import com.f1.base.Row;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.CasterManager;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.HasherMap.Entry;
import com.f1.utils.concurrent.IdentityCompactSet;
import com.f1.utils.impl.ArrayHasher;
import com.f1.utils.sql.SqlDerivedCellParser;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.ExpressionNode;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.string.node.VariableNode;
import com.f1.utils.string.sqlnode.SqlColumnsNode;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorCast;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorMath;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorRef;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.NamespaceCalcTypesImpl;
import com.f1.utils.structs.table.stack.BasicCalcTypes;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.CalcTypesStack;
import com.f1.utils.structs.table.stack.ChildCalcTypesStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableStackFramePool;

public class AmiTrigger_Join extends AmiAbstractTrigger {

	private static final Logger log = LH.get();
	private String[] targetColumns;
	private int[] targetColumnPos;
	private DerivedCellCalculator[] targetCalcs;
	private HasherMap<Object[], LeftAndRight> keys2Lar = new HasherMap<Object[], AmiTrigger_Join.LeftAndRight>(ArrayHasher.INSTANCE);
	private LongKeyMap<Set<LeftRightRow>> left2targets = new LongKeyMap<Set<LeftRightRow>>();
	private LongKeyMap<Set<LeftRightRow>> rght2targets = new LongKeyMap<Set<LeftRightRow>>();
	private DerivedCellCalculator[] leftKeys, rghtKeys;
	private Object[] tmpKey, updatingKey;
	private AmiTableImpl leftTable;
	private AmiTableImpl rghtTable;
	private AmiTableImpl targetTable;
	private boolean includeOuterLeft;
	private boolean includeOuterRight;
	private boolean includeInner;
	private boolean inTrigger;
	private String dependenciesDef;
	private Set<String> lockedTables;
	private boolean targetTableNeedsRebuild;
	private List<String> bindingTables;
	private ReusableStackFramePool stackFramePool;

	@Override
	protected void onStartup(CalcFrameStack sf) {
		this.stackFramePool = getImdb().getState().getStackFramePool();
		build(sf);
	}
	protected void build(CalcTypesStack cfs) {

		if (this.getBinding().getTableNamesCount() != 3)
			throw new RuntimeException("JOIN trigger must be on exactly three tables (left table, right table)");

		final AmiImdbImpl db = (AmiImdbImpl) this.getImdb();
		final AmiTriggerBinding binding = this.getBinding();
		final AmiImdbScriptManager sm = db.getScriptManager();
		final SqlProcessor sqlProcessor = sm.getSqlProcessor();
		final SqlExpressionParser ep = sqlProcessor.getExpressionParser();

		AmiTableImpl leftTable = db.getAmiTable(binding.getTableNameAt(0));
		AmiTableImpl rghtTable = db.getAmiTable(binding.getTableNameAt(1));
		AmiTableImpl targetTable = db.getAmiTable(binding.getTableNameAt(2));
		db.assertNotLockedByTrigger(this, targetTable.getName());

		NamespaceCalcTypesImpl variables = new NamespaceCalcTypesImpl();
		com.f1.utils.structs.table.stack.BasicCalcTypes leftVars = new com.f1.utils.structs.table.stack.BasicCalcTypes();
		com.f1.utils.structs.table.stack.BasicCalcTypes rghtVars = new com.f1.utils.structs.table.stack.BasicCalcTypes();
		CalcTypes leftMapping = leftTable.getTable().getColumnTypesMapping();
		CalcTypes rghtMapping = rghtTable.getTable().getColumnTypesMapping();
		variables.addNamespace(leftTable.getName(), leftMapping);
		variables.addNamespace(rghtTable.getName(), rghtMapping);
		for (String i : leftMapping.getVarKeys()) {
			Class<?> type = leftMapping.getType(i);
			variables.putType(i, type);
			//			String key = leftTable.getName() + "." + i;
			//			variables.putType(key, type);
			leftVars.putType(i, type);
			//			leftVars.putType(key, type);
		}
		for (String i : rghtMapping.getVarKeys()) {
			Class<?> type = rghtMapping.getType(i);
			variables.putType(i, type);
			//			String key = rghtTable.getName() + "." + i;
			//			variables.putType(key, type);
			rghtVars.putType(i, type);
			//			rghtVars.putType(key, type);
		}
		final CellParser cp = new CellParser(sqlProcessor, leftTable, rghtTable, leftVars, rghtVars);

		final boolean includeOuterLeft;
		final boolean includeOuterRight;
		final boolean includeInner;
		{//TYPE
			String type = binding.getOption(Caster_String.INSTANCE, "type", "INNER");
			type = type.toUpperCase().trim().replace(" +", " ");
			if ("LEFT".equals(type)) {
				includeInner = true;
				includeOuterLeft = true;
				includeOuterRight = false;
			} else if ("LEFT ONLY".equals(type)) {
				includeInner = false;
				includeOuterLeft = true;
				includeOuterRight = false;
			} else if ("RIGHT".equals(type)) {
				includeInner = true;
				includeOuterLeft = false;
				includeOuterRight = true;
			} else if ("RIGHT ONLY".equals(type)) {
				includeInner = false;
				includeOuterLeft = false;
				includeOuterRight = true;
			} else if ("INNER".equals(type)) {
				includeInner = true;
				includeOuterLeft = false;
				includeOuterRight = false;
			} else if ("OUTER".equals(type)) {
				includeInner = true;
				includeOuterLeft = true;
				includeOuterRight = true;
			} else if ("OUTER ONLY".equals(type)) {
				includeInner = false;
				includeOuterLeft = true;
				includeOuterRight = true;
			} else
				throw new RuntimeException("Invalid value for TYPE option: " + type + " (must be either INNER, LEFT, RIGHT, OUTER, LEFT ONLY, RIGHT ONLY or OUTER ONLY)");
		}

		final DerivedCellCalculator leftIndexKeys[];
		final DerivedCellCalculator rghtIndexKeys[];
		ChildCalcTypesStack context = new ChildCalcTypesStack(cfs, variables, sm.getMethodFactory());
		{//ON
			String on = binding.getOption(Caster_String.INSTANCE, "on", null);

			DerivedCellCalculator onCalc = cp.toCalc(on, context);
			List<Tuple2<DerivedCellCalculator, DerivedCellCalculator>> sink = new ArrayList<Tuple2<DerivedCellCalculator, DerivedCellCalculator>>();
			DerivedCellCalculator extra = toAndsForIndex(onCalc, sink, leftVars, rghtVars, leftTable.getName(), rghtTable.getName());
			if (extra != null || sink.isEmpty())
				throw new RuntimeException("ON option must be of the form: leftColumn==rightColumn [&& leftColumn==rightColumn ...]");
			leftIndexKeys = new DerivedCellCalculator[sink.size()];
			rghtIndexKeys = new DerivedCellCalculator[sink.size()];
			int pos = 0;
			for (Tuple2<DerivedCellCalculator, DerivedCellCalculator> i : sink) {
				DerivedCellCalculator l = (DerivedCellCalculatorRef) i.getA();
				DerivedCellCalculator r = (DerivedCellCalculatorRef) i.getB();
				if (l.getReturnType() != r.getReturnType()) {
					Class<?> type = OH.getWidest(l.getReturnType(), r.getReturnType());
					if (l.getReturnType() != type)
						l = new DerivedCellCalculatorCast(l.getPosition(), type, l, CasterManager.getCaster(type));
					if (r.getReturnType() != type)
						r = new DerivedCellCalculatorCast(r.getPosition(), type, r, CasterManager.getCaster(type));
				}
				leftIndexKeys[pos] = l;
				rghtIndexKeys[pos] = r;
				pos++;
			}
		}
		String[] targetColumns;
		int[] targetColumnPos;
		DerivedCellCalculator[] targetCalcs;

		{//SELECTS
			String assignments = binding.getOption(Caster_String.INSTANCE, "selects", null);
			SqlColumnsNode node1 = ep.parseSqlColumnsNdoe(SqlExpressionParser.ID_SELECT, assignments);
			//			Node[] cols = node1.columns;
			targetColumns = new String[node1.getColumnsCount()];
			targetColumnPos = new int[node1.getColumnsCount()];
			targetCalcs = new DerivedCellCalculator[node1.getColumnsCount()];
			for (int i = 0; i < node1.getColumnsCount(); i++) {
				Node col = node1.getColumnAt(i);
				String targetName;
				OperationNode op;
				try {
					op = (OperationNode) col;
					VariableNode vn = (VariableNode) op.getLeft();
					targetName = vn.getVarname();
				} catch (Exception e) {
					throw new RuntimeException("selects option should be in the form: targetColumn=sourceColumn", e);
				}
				AmiColumnImpl<?> colm = targetTable.getColumnNoThrow(targetName);
				if (colm == null)
					throw new RuntimeException("selects option has unknown assignment column: " + targetName);
				DerivedCellCalculator calc = cp.toCalc(op.getRight(), context);
				targetColumns[i] = colm.getName();
				targetColumnPos[i] = colm.getLocation();
				targetCalcs[i] = calc;
			}
		}

		this.leftKeys = leftIndexKeys;
		this.rghtKeys = rghtIndexKeys;
		this.tmpKey = new Object[leftKeys.length];
		this.updatingKey = new Object[leftKeys.length];
		this.targetCalcs = targetCalcs;
		this.targetColumnPos = targetColumnPos;
		this.targetColumns = targetColumns;
		this.keys2Lar.clear();
		this.left2targets.clear();
		this.rght2targets.clear();
		this.leftTable = leftTable;
		this.rghtTable = rghtTable;
		this.bindingTables = CH.l(leftTable.getName(), rghtTable.getName(), targetTable.getName());
		this.targetTable = targetTable;
		this.includeOuterLeft = includeOuterLeft;
		this.includeOuterRight = includeOuterRight;
		this.includeInner = includeInner;
		this.lockedTables = Collections.singleton(this.targetTable.getName());
		this.dependenciesDef = getDependenciesDef(this.getImdb(), leftTable, rghtTable, targetTable);
		this.targetTableNeedsRebuild = true;
	}

	@Override
	public void onInitialized(CalcFrameStack sf) {
		rebuildTargetTable(sf);
	}

	@Override
	public List<String> getBindingTables() {
		return this.bindingTables;
	}
	private void rebuildTargetTable(CalcFrameStack sf) {
		if (!targetTableNeedsRebuild)
			return;
		this.targetTableNeedsRebuild = false;
		final long startNanos = System.nanoTime();
		try {
			inTrigger = true;
			this.targetTable.clearRows(sf);
			for (Row i : leftTable.getTable().getRows()) {
				getKey((AmiRowImpl) i, tmpKey, true, sf);
				addSourceRow((AmiRowImpl) i, tmpKey, true);
			}
			for (Row i : rghtTable.getTable().getRows()) {
				getKey((AmiRowImpl) i, tmpKey, false, sf);
				addSourceRow((AmiRowImpl) i, tmpKey, false);
			}
			for (LeftAndRight i : this.keys2Lar.values()) {
				if (i.rght.isEmpty()) {
					if (includeOuterLeft)
						for (AmiRowImpl row1 : i.left.values())
							addTargetRow(row1, null, sf);
				} else if (i.left.isEmpty()) {
					if (includeOuterRight)
						for (AmiRowImpl row2 : i.rght.values())
							addTargetRow(null, row2, sf);
				} else {
					if (includeInner)
						for (AmiRowImpl row1 : i.left.values())
							for (AmiRowImpl row2 : i.rght.values())
								addTargetRow(row1, row2, sf);
				}
			}
			LH.info(log, "Rebuilt JOIN trigger '", this.getBinding().getTriggerName(), "' in ", (System.nanoTime() - startNanos) / 1000, " micros");
		} finally {
			inTrigger = false;
		}
	}

	private void addTargetRow(boolean isLeft, AmiRowImpl me, AmiRowImpl other, CalcFrameStack sf) {
		if (isLeft)
			addTargetRow(me, other, sf);
		else
			addTargetRow(other, me, sf);
	}
	private void addTargetRow(AmiRowImpl left, AmiRowImpl rght, CalcFrameStack sf) {
		LeftRightRow t = new LeftRightRow();
		t.left = left;
		t.right = rght;
		AmiPreparedRowImpl pr = this.targetTable.borrowPreparedRow();
		ReusableCalcFrameStack rsf = this.stackFramePool.borrow(sf, t);
		for (int i = 0; i < this.targetColumnPos.length; i++) {
			Object value = this.targetCalcs[i].get(rsf);
			pr.putAt(this.targetColumnPos[i], value);
		}
		this.stackFramePool.release(rsf);
		AmiRowImpl row = this.targetTable.insertAmiRow(pr, sf);
		t.target = row;
		if (left != null)
			addToTargetsMap(this.left2targets, left, t);
		if (rght != null)
			addToTargetsMap(this.rght2targets, rght, t);
	}
	private void addToTargetsMap(LongKeyMap<Set<LeftRightRow>> rght2targets2, AmiRowImpl source, LeftRightRow t) {
		com.f1.utils.structs.LongKeyMap.Node<Set<LeftRightRow>> node = rght2targets2.getNodeOrCreate(source.getAmiId());
		Set<LeftRightRow> list = node.getValue();
		if (list == null) {
			list = new IdentityCompactSet<LeftRightRow>();
			node.setValue(list);
		}
		list.add(t);
	}
	static public DerivedCellCalculator toAndsForIndex(DerivedCellCalculator calc, List<Tuple2<DerivedCellCalculator, DerivedCellCalculator>> sink,
			com.f1.utils.structs.table.stack.BasicCalcTypes leftVars, com.f1.utils.structs.table.stack.BasicCalcTypes rghtVars, String leftTableName, String rightTableName) {
		if (calc instanceof DerivedCellCalculatorMath) {
			final DerivedCellCalculatorMath c = (DerivedCellCalculatorMath) calc;
			if (c.getOperationNodeCode() == OperationNode.OP_AMP_AMP) {
				final DerivedCellCalculator left = c.getLeft();
				final DerivedCellCalculator rght = c.getRight();
				DerivedCellCalculator l = toAndsForIndex(left, sink, leftVars, rghtVars, leftTableName, rightTableName);
				DerivedCellCalculator r = toAndsForIndex(rght, sink, leftVars, rghtVars, leftTableName, rightTableName);
				if (l == null)
					return r;
				if (r == null)
					return l;
				if (l == left && r == rght)
					return c;
				return DerivedCellCalculatorMath.valueOf(calc.getPosition(), OperationNode.OP_AMP_AMP, l, r);
			} else if (c.getOperationNodeCode() == OperationNode.OP_EQ_EQ) {
				int l = getTableUsed(c.getLeft(), leftVars, rghtVars, leftTableName, rightTableName);
				int r = getTableUsed(c.getRight(), leftVars, rghtVars, leftTableName, rightTableName);
				if (l == 1 && r == 2) {
					sink.add(new Tuple2<DerivedCellCalculator, DerivedCellCalculator>(c.getLeft(), c.getRight()));
					return null;
				} else if (l == 2 && r == 1) {
					sink.add(new Tuple2<DerivedCellCalculator, DerivedCellCalculator>(c.getRight(), c.getLeft()));
					return null;
				}
			}
		}
		return calc;
	}

	//0==none,1=left,2=right,3=both
	static private int getTableUsed(DerivedCellCalculator dcc, BasicCalcTypes leftVars, com.f1.utils.structs.table.stack.BasicCalcTypes rghtVars, String leftTableName,
			String rightTableName) {
		Set<Object> tmpSet = DerivedHelper.getDependencyIds(dcc);
		int r = 0;
		for (Object s : tmpSet) {
			if (s instanceof NameSpaceIdentifier) {
				String name = ((NameSpaceIdentifier) s).getNamespace();
				if (OH.eq(leftTableName, name))
					r |= 1;
				if (OH.eq(rightTableName, name))
					r |= 2;
			} else {
				if (leftVars.getType((String) s) != null)
					r |= 1;
				if (rghtVars.getType((String) s) != null)
					r |= 2;
			}
		}
		return r;
	}

	public static class LeftAndRight {

		private LongKeyMap<AmiRowImpl> left = new LongKeyMap<AmiRowImpl>();
		private LongKeyMap<AmiRowImpl> rght = new LongKeyMap<AmiRowImpl>();

		public void put(Object r) {
		}
		public void put(AmiRowImpl row, boolean isLeft) {
			getMap(isLeft).put(row.getAmiId(), row);
		}
		public AmiRowImpl remove(long amiRowId, boolean isLeft) {
			return getMap(isLeft).remove(amiRowId);
		}
		private LongKeyMap<AmiRowImpl> getMap(boolean isLeft) {
			return isLeft ? left : rght;
		}
	}

	public void getKey(AmiRowImpl row, Object[] sink, boolean isLeft, CalcFrameStack sf) {
		ReusableCalcFrameStack rsf = this.stackFramePool.borrow(sf, row);
		if (isLeft)
			for (int i = 0; i < sink.length; i++)
				sink[i] = this.leftKeys[i].get(rsf);
		else
			for (int i = 0; i < sink.length; i++)
				sink[i] = this.rghtKeys[i].get(rsf);
		this.stackFramePool.release(rsf);
	}
	public LeftAndRight addSourceRow(AmiRowImpl row, Object[] tmpKey, boolean isLeft) {
		Entry<Object[], LeftAndRight> entry = keys2Lar.getOrCreateEntry(tmpKey);
		LeftAndRight lar = entry.getValue();
		if (lar == null) {
			entry.setValue(lar = new LeftAndRight());
			this.tmpKey = new Object[tmpKey.length];
		}
		lar.put(row, isLeft);
		return lar;
	}

	public LeftAndRight removeRow(AmiRowImpl row, Object key[], boolean isLeft) {
		LeftAndRight lar = keys2Lar.get(key);
		lar.remove(row.getAmiId(), isLeft);
		if (lar.left.isEmpty() && lar.rght.isEmpty())
			keys2Lar.remove(key);
		return lar;
	}

	public static class CellParser extends SqlDerivedCellParser {

		final private AmiTableImpl left, rght;
		final private com.f1.utils.structs.table.stack.BasicCalcTypes leftVars, rghtVars;

		public CellParser(SqlProcessor sqlProcessor, AmiTableImpl leftTable, AmiTableImpl rghtTable, com.f1.utils.structs.table.stack.BasicCalcTypes leftVars,
				BasicCalcTypes rghtVars) {
			super(sqlProcessor.getExpressionParser(), sqlProcessor);
			this.leftVars = leftVars;
			this.rghtVars = rghtVars;
			this.left = leftTable;
			this.rght = rghtTable;
		}

		@Override
		public DerivedCellCalculatorRef newDerivedCellCalculatorRef(int position, Class type, String varname) {
			final boolean isLeft = leftVars.getType(varname) != null;
			final boolean isRght = rghtVars.getType(varname) != null;
			if (!isLeft && !isRght)
				throw new ExpressionParserException(position, "Unknown column: " + varname);
			else if (isLeft && isRght)
				throw new ExpressionParserException(position, "ambigous column: " + varname);
			else if (isLeft)
				return new JoinTableRefLeft(position, left.getColumnLocation(varname), type, varname);
			else
				return new JoinTableRefRight(position, rght.getColumnLocation(varname), type, varname);
		}
		@Override
		public DerivedCellCalculatorRef newDerivedCellCalculatorRef(int position, Class type, NameSpaceIdentifier nsi) {
			final String varname = nsi.getVarName();
			final boolean isLeft = left.getName().equals(nsi.getNamespace()) && leftVars.getType(varname) != null;
			final boolean isRght = rght.getName().equals(nsi.getNamespace()) && rghtVars.getType(varname) != null;
			if (!isLeft && !isRght)
				throw new ExpressionParserException(position, "Unknown column: " + varname);
			else if (isLeft)
				return new JoinTableRefLeft(position, left.getColumnLocation(varname), type, nsi);
			else
				return new JoinTableRefRight(position, rght.getColumnLocation(varname), type, nsi);
		}
	}

	public static class JoinTableRefLeft extends DerivedCellCalculatorRef {

		final private int colPos;

		public JoinTableRefLeft(int position, int colPos, Class type, Object varname) {
			super(position, type, varname);
			this.colPos = colPos;
		}
		@Override
		public Object get(CalcFrameStack sf) {
			CalcFrame key = sf.getFrame();
			if (key instanceof LeftRightRow) {
				AmiRowImpl t = ((LeftRightRow) key).left;
				return t == null ? null : t.getAt(colPos);
			} else
				return ((AmiRow) key).getComparable(colPos);
		}
		@Override
		public boolean isSame(DerivedCellCalculator other) {
			if (!super.isSame(other))
				return false;
			JoinTableRefLeft o = (JoinTableRefLeft) other;
			return OH.eq(colPos, o.colPos);
		}
	}

	public static class JoinTableRefRight extends DerivedCellCalculatorRef {

		final private int colPos;

		public JoinTableRefRight(int position, int colPos, Class type, Object varname) {
			super(position, type, varname);
			this.colPos = colPos;
		}
		@Override
		public Object get(CalcFrameStack sf) {
			CalcFrame key = sf.getFrame();
			if (key instanceof LeftRightRow) {
				AmiRowImpl t = ((LeftRightRow) key).right;
				return t == null ? null : t.getAt(colPos);
			} else
				return ((AmiRow) key).getComparable(colPos);
		}
		@Override
		public boolean isSame(DerivedCellCalculator other) {
			if (!super.isSame(other))
				return false;
			JoinTableRefLeft o = (JoinTableRefLeft) other;
			return OH.eq(colPos, o.colPos);
		}
	}

	public static class LeftRightRow implements CalcFrame {
		public AmiRowImpl left, right, target;

		public void setRow(boolean isLeft, AmiRowImpl row) {
			if (isLeft)
				this.left = row;
			else
				this.right = row;
		}
		public AmiRowImpl getRow(boolean isLeft) {
			return isLeft ? left : right;
		}
		@Override
		public Object getValue(String key) {
			throw new UnsupportedOperationException();
		}
		@Override
		public Object putValue(String key, Object value) {
			throw new UnsupportedOperationException();
		}
		@Override
		public Class<?> getType(String key) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Iterable<String> getVarKeys() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isVarsEmpty() {
			throw new UnsupportedOperationException();
		}
		@Override
		public int getVarsCount() {
			throw new UnsupportedOperationException();
		}
	}

	public LongKeyMap<Set<LeftRightRow>> get2Targets(boolean isLeft) {
		return isLeft ? left2targets : this.rght2targets;
	}

	@Override
	public boolean onDeleting(AmiTable table, AmiRow row, CalcFrameStack sf) {
		if (table == targetTable)
			return inTrigger;
		try {
			this.inTrigger = true;
			final boolean isLeft = table == this.leftTable;
			AmiRowImpl arow = (AmiRowImpl) row;
			getKey(arow, tmpKey, isLeft, sf);
			processDelete(arow, tmpKey, isLeft, sf);
			return true;
		} finally {
			this.inTrigger = false;
		}
	}
	private void processDelete(AmiRowImpl arow, Object[] tmpKey, boolean isLeft, CalcFrameStack sf) {
		final LeftAndRight lar = removeRow(arow, tmpKey, isLeft);
		final boolean isEmptyNow = lar.getMap(isLeft).size() == 0;
		final boolean otherHasData = lar.getMap(!isLeft).size() > 0;
		final boolean includeMe = getInclude(isLeft);
		final boolean includeOther = getInclude(!isLeft);

		if (otherHasData) {
			if (includeInner) {
				if (includeOther && isEmptyNow) { //update me to null
					Set<LeftRightRow> targetRowsToUpdate = this.get2Targets(isLeft).remove(arow.getAmiId());
					for (LeftRightRow lrr : targetRowsToUpdate) {
						lrr.setRow(isLeft, null);
						updateRow(lrr, sf);
					}
				} else if (otherHasData) { //do matrix delete
					Set<LeftRightRow> targetRowsToRemove = this.get2Targets(isLeft).remove(arow.getAmiId());
					LongKeyMap<Set<LeftRightRow>> other2targets = get2Targets(!isLeft);
					for (LeftRightRow lrr : targetRowsToRemove) {
						AmiRowImpl otherRow = lrr.getRow(!isLeft);
						//						if (otherRow != null)
						other2targets.get(otherRow.getAmiId()).remove(lrr);
						targetTable.removeAmiRow(lrr.target, sf);
					}
				}
			} else if (isEmptyNow && includeOther) { //create outer record
				for (AmiRowImpl i : lar.getMap(!isLeft).values())
					this.addTargetRow(isLeft, null, i, sf);
			}
		} else if (includeMe) { //other is null, delete me
			Set<LeftRightRow> targetRowsToRemove = this.get2Targets(isLeft).remove(arow.getAmiId());
			for (LeftRightRow lrr : targetRowsToRemove)
				targetTable.removeAmiRow(lrr.target, sf);
		}
	}

	private boolean getInclude(boolean isLeft) {
		return isLeft ? includeOuterLeft : includeOuterRight;
	}

	@Override
	public boolean onInserting(AmiTable table, AmiRow row, CalcFrameStack sf) {
		if (table == targetTable && !inTrigger)
			return false;
		return super.onInserting(table, row, sf);
	}

	@Override
	public void onInserted(AmiTable table, AmiRow row, CalcFrameStack sf) {
		if (table == targetTable)
			return;
		try {
			this.inTrigger = true;
			AmiRowImpl arow = (AmiRowImpl) row;
			final boolean isLeft = table == this.leftTable;
			getKey(arow, tmpKey, isLeft, sf);
			processInsert(arow, tmpKey, isLeft, sf);
		} finally {
			this.inTrigger = false;
		}

	}
	public void processInsert(AmiRowImpl arow, Object[] tmpKey, boolean isLeft, CalcFrameStack sf) {
		final LeftAndRight lar = addSourceRow((AmiRowImpl) arow, tmpKey, isLeft);
		final boolean isFirst = lar.getMap(isLeft).size() == 1;
		final boolean otherHasData = lar.getMap(!isLeft).size() > 0;
		final boolean includeMe = getInclude(isLeft);
		final boolean includeOther = getInclude(!isLeft);

		if (otherHasData) {
			if (includeInner) {
				if (includeOther && isFirst) { //update existing record
					final LongKeyMap<Set<LeftRightRow>> other2targets = get2Targets(!isLeft);
					final LongKeyMap<Set<LeftRightRow>> totargets = get2Targets(isLeft);
					final Set<LeftRightRow> set = new IdentityCompactSet<LeftRightRow>();
					totargets.put(arow.getAmiId(), set);
					for (LongIterator li = lar.getMap(!isLeft).keyIterator(); li.hasNext();)
						for (LeftRightRow lrr : other2targets.get(li.nextLong())) {
							lrr.setRow(isLeft, arow);
							updateRow(lrr, sf);
							set.add(lrr);
						}
				} else { //do matrix join
					for (AmiRowImpl otherRow : lar.getMap(!isLeft).values())
						addTargetRow(isLeft, arow, otherRow, sf);
				}
			} else if (isFirst && includeOther) { //delete other
				for (LongIterator li = lar.getMap(!isLeft).keyIterator(); li.hasNext();)
					for (LeftRightRow lrr : this.get2Targets(!isLeft).remove(li.nextLong()))
						targetTable.removeAmiRow(lrr.target, sf);
			}
		} else if (includeMe) { //add row with other null
			addTargetRow(isLeft, arow, null, sf);
		}
	}
	@Override
	public boolean onUpdating(AmiTable table, AmiRow row, CalcFrameStack sf) {
		if (table == targetTable)
			return inTrigger;
		try {
			this.inTrigger = true;
			final boolean isLeft = table == this.leftTable;
			AmiRowImpl arow = (AmiRowImpl) row;
			getKey(arow, updatingKey, isLeft, sf);
			return true;
		} finally {
			this.inTrigger = false;
		}
	}
	@Override
	public void onUpdated(AmiTable table, AmiRow row, CalcFrameStack sf) {
		if (table == targetTable)
			return;
		try {
			this.inTrigger = true;
			final boolean isLeft = table == this.leftTable;
			AmiRowImpl arow = (AmiRowImpl) row;
			getKey(arow, tmpKey, isLeft, sf);
			LeftAndRight lar = keys2Lar.get(updatingKey);
			final boolean needsUpdate;
			if (AH.eq(updatingKey, tmpKey)) {//key did not change, just update
				needsUpdate = true;
			} else {
				if (lar.getMap(!isLeft).isEmpty()) {//we're updating a row that isn't joined to anything
					LeftAndRight other = keys2Lar.get(tmpKey);
					if (other == null) {
						if (lar.getMap(isLeft).size() == 1) {//this is the only row so we can keep the lar, just re insert with a different key
							keys2Lar.remove(updatingKey);
							keys2Lar.put(tmpKey, lar);
							tmpKey = new Object[tmpKey.length];
							needsUpdate = true;
						} else {//otherwise, remove the row from the current lar and create a new one
							lar.getMap(isLeft).remove(arow.getAmiId());
							lar = new LeftAndRight();
							lar.put(arow, isLeft);
							keys2Lar.put(tmpKey, lar);
							tmpKey = new Object[tmpKey.length];
							needsUpdate = true;
						}
					} else if (other.getMap(!isLeft).isEmpty()) {//we just need to move this row to the other,existing lar
						lar.getMap(isLeft).remove(arow.getAmiId());
						other.put(arow, isLeft);
						needsUpdate = true;
					} else
						needsUpdate = false;
				} else
					needsUpdate = false;
			}
			if (needsUpdate) {
				Set<LeftRightRow> set = get2Targets(isLeft).get(row.getAmiId());
				if (set != null)
					for (LeftRightRow lrr : set)
						updateRow(lrr, sf);
			} else {
				processDelete(arow, updatingKey, isLeft, sf);
				processInsert(arow, tmpKey, isLeft, sf);
			}
		} finally {
			this.inTrigger = false;
		}
	}
	private void updateRow(LeftRightRow lrr, CalcFrameStack sf) {
		AmiRowImpl target = lrr.target;
		if (target != null) {
			AmiPreparedRowImpl pr = targetTable.borrowPreparedRow();
			pr.reset();
			ReusableCalcFrameStack rsf = this.stackFramePool.borrow(sf, lrr);
			for (int i = 0; i < this.targetColumnPos.length; i++)
				pr.putAt(this.targetColumnPos[i], this.targetCalcs[i].get(rsf));
			this.stackFramePool.release(rsf);
			this.targetTable.updateAmiRow(target.getAmiId(), pr, sf);
		}
	}

	public static final String getDependenciesDef(AmiImdb imdb, AmiTableImpl... tables) {
		AmiSchema ss = ((AmiImdbImpl) imdb).getSystemSchema();
		StringBuilder sink = new StringBuilder();
		for (AmiTableImpl table : tables) {
			if (table == null)
				sink.append("null");
			else
				ss.generateCreateSql(table, sink);
		}
		return sink.toString();
	}
	@Override
	public void onSchemaChanged(AmiImdb imdb, CalcFrameStack sf) {
		String t = getDependenciesDef(imdb, leftTable, rghtTable, targetTable);
		if (OH.ne(this.dependenciesDef, t))
			build(sf);
		rebuildTargetTable(sf);
	}

	public Set<String> getLockedTables() {
		return lockedTables;
	}
	static private Node reduce(Node n) {
		while (n instanceof ExpressionNode)
			n = ((ExpressionNode) n).getValue();
		return n;
	}

}
