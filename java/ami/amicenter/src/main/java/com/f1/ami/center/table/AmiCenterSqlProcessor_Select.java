package com.f1.ami.center.table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.ami.center.hdb.AmiHdbColumn;
import com.f1.ami.center.hdb.AmiHdbSqlFlowControl_Select;
import com.f1.ami.center.hdb.AmiHdbTable;
import com.f1.ami.center.hdb.AmiHdbTableRep;
import com.f1.ami.center.hdb.AmiHdbUtils;
import com.f1.ami.center.hdb.qry.AmiHdbQueryImpl;
import com.f1.ami.center.hdb.qry.AmiHdbQuery_Compare;
import com.f1.ami.center.hdb.qry.AmiHdbQuery_Eq;
import com.f1.ami.center.hdb.qry.AmiHdbQuery_Ge;
import com.f1.ami.center.hdb.qry.AmiHdbQuery_Gt;
import com.f1.ami.center.hdb.qry.AmiHdbQuery_In;
import com.f1.ami.center.hdb.qry.AmiHdbQuery_JRegex;
import com.f1.ami.center.hdb.qry.AmiHdbQuery_Le;
import com.f1.ami.center.hdb.qry.AmiHdbQuery_Lt;
import com.f1.ami.center.hdb.qry.AmiHdbQuery_Ne;
import com.f1.ami.center.hdb.qry.AmiHdbQuery_NotJRegex;
import com.f1.ami.center.hdb.qry.AmiHdbQuery_Regex;
import com.f1.base.Table;
import com.f1.utils.AH;
import com.f1.utils.OH;
import com.f1.utils.sql.DerivedCellCalculatorSql;
import com.f1.utils.sql.DerivedCellCalculator_SqlIn;
import com.f1.utils.sql.DerivedCellCalculator_SqlInSingle;
import com.f1.utils.sql.DerivedCellCalculator_SqlInnerSelect;
import com.f1.utils.sql.DerivedCellCalculator_SqlInnerSelectSingle;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.sql.SqlProcessor_Select;
import com.f1.utils.sql.aggs.AggregateFactory;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.string.sqlnode.OnNode;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorConst;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorMath;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorOr;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorRef;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.CalcTypesStack;

public class AmiCenterSqlProcessor_Select extends SqlProcessor_Select {

	public AmiCenterSqlProcessor_Select(SqlProcessor owner) {
		super(owner);
	}

	@Override
	protected FlowControl project(DerivedCellCalculatorSql query, Map<String, Table> asTables, String[] names, DerivedCellCalculator[] selectCalcs, int joinType,
			DerivedCellCalculator join, DerivedCellCalculator joinNearest, DerivedCellCalculator where, DerivedCellCalculator[] groupByCalcs, DerivedCellCalculator having,
			OnNode[] unpacks, DerivedCellCalculator[] orderByCalcs, boolean[] orderByAsc, int limitOffset, int limit, CalcFrameStack sf, CalcTypesStack context) {
		for (Entry<String, Table> i : asTables.entrySet())
			if (i.getValue() instanceof AmiHdbTableRep) {
				if (asTables.size() == 1) {
					return projectHistorical(query, (AmiHdbTableRep) i.getValue(), i.getKey(), names, selectCalcs, where, groupByCalcs, having, unpacks, orderByCalcs, orderByAsc,
							limitOffset, limit, sf, context);
				} else
					throw new RuntimeException("JOIN not supported for HISTORICAL tables");
			}
		return super.project(query, asTables, names, selectCalcs, joinType, join, joinNearest, where, groupByCalcs, having, unpacks, orderByCalcs, orderByAsc, limitOffset, limit,
				sf, context);
	}
	private FlowControl projectHistorical(DerivedCellCalculatorSql query, AmiHdbTableRep table, String tableAsName, String[] names, DerivedCellCalculator[] selectCalcs,
			DerivedCellCalculator where, DerivedCellCalculator[] groupby, DerivedCellCalculator having, OnNode[] unpacks, DerivedCellCalculator[] orderByCalcs,
			boolean[] orderByAsc, int limitOffset, int limit, CalcFrameStack sf, CalcTypesStack context) {
		AggregateFactory factory2 = (AggregateFactory) context.getFactory();
		;
		if (AH.isntEmpty(unpacks))
			throw new ExpressionParserException(unpacks[0].getPosition(), "UNPACK not supported for HISTORICAL tables");
		if (groupby == null && factory2.getAggregatesCount() > 0) {
			groupby = new DerivedCellCalculator[] { new DerivedCellCalculatorConst(0, true) };
		}
		processInnerQueries(where, sf);

		return new AmiHdbSqlFlowControl_Select(query, table, tableAsName, names, selectCalcs, where, groupby, having, orderByCalcs, orderByAsc, limitOffset, limit, factory2,
				DerivedHelper.toFrame(sf), sf.getSqlPlanListener());

	}

	public static void processInnerQueries(DerivedCellCalculator where, CalcFrameStack sf) {
		List<DerivedCellCalculator> sink = new ArrayList<DerivedCellCalculator>();
		DerivedHelper.find(where, DerivedCellCalculator_SqlInnerSelectSingle.class, sink);
		DerivedHelper.find(where, DerivedCellCalculator_SqlInnerSelect.class, sink);
		for (DerivedCellCalculator i : sink)
			i.get(sf);
	}

	public static void fillQuery(AmiHdbQueryImpl q, DerivedCellCalculator where, CalcFrameStack sf) {
		List<DerivedCellCalculator> sink = new ArrayList<DerivedCellCalculator>();
		String tablename = q.getTable().getName();
		reduce(where, sink);
		q.clearParts();
		DerivedCellCalculator remaining = null;
		AmiHdbTable ht = q.getTable();
		DerivedCellCalculator[] ands = sink.toArray(new DerivedCellCalculator[sink.size()]);
		outer: for (DerivedCellCalculator dcc : ands) {
			if (dcc instanceof DerivedCellCalculatorMath) {
				DerivedCellCalculatorMath math = (DerivedCellCalculatorMath) dcc;
				if (math.getOperationType() == DerivedCellCalculatorMath.TYPE_BOOL_OR) {
					AmiHdbQuery_In t = toInClause(new DerivedCellCalculator[] { math.getLeft(), math.getRight() }, ht);
					if (t != null) {
						q.addPart(t);
						continue;
					}
				} else if (math.getLeft() instanceof DerivedCellCalculatorRef && math.getRight().isConst()) {
					String colName = AmiHdbUtils.getId((DerivedCellCalculatorRef) math.getLeft(), tablename);
					Comparable value = (Comparable) (math.getRight()).get(null);
					AmiHdbQuery_Compare opcode = getOperation(math, ht.getColumn(colName), value);
					if (opcode != null) {
						q.addPart(opcode);
						continue;
					}
				} else if (math.getRight() instanceof DerivedCellCalculatorRef && math.getLeft().isConst()) {
					String colName = AmiHdbUtils.getId((DerivedCellCalculatorRef) math.getRight(), tablename);
					Comparable value = (Comparable) (math.getLeft()).get(null);
					AmiHdbQuery_Compare opcode = getOperation(math, ht.getColumn(colName), value);
					if (opcode != null) {
						q.addPart(opcode);
						continue;
					}
				}
			} else if (dcc instanceof DerivedCellCalculator_SqlInSingle) {
				DerivedCellCalculator_SqlInSingle in = (DerivedCellCalculator_SqlInSingle) dcc;
				if (in.getLeft() instanceof DerivedCellCalculatorRef) {
					Set<Comparable> values = new HashSet<Comparable>();
					for (DerivedCellCalculator i : in.getInValues()) {
						if (i instanceof DerivedCellCalculatorRef)
							AmiCenterSqlProcessorMutator.applyConstIfAvailable(sf, ((DerivedCellCalculatorRef) i), ht.getTable().getColumnIds());
						if (i.isConst()) {
							values.add((Comparable) i.get(null));
							continue;
						} else {
							values = null;
							break;
						}
					}
					if (values != null) {
						q.addPart(new AmiHdbQuery_In(ht.getColumn(AmiHdbUtils.getId((DerivedCellCalculatorRef) in.getLeft(), tablename)), values));
						continue;
					}
				}
			} else if (dcc instanceof DerivedCellCalculator_SqlIn) {
				DerivedCellCalculator_SqlIn in = (DerivedCellCalculator_SqlIn) dcc;
				for (int n = 0; n < in.getLeft().length; n++) {
					if (in.getLeft()[n] instanceof DerivedCellCalculatorRef) {
						Set<Comparable> values = new HashSet<Comparable>();
						for (DerivedCellCalculator d[] : in.getInValues()) {
							DerivedCellCalculator i = d[n];
							if (i instanceof DerivedCellCalculatorRef)
								AmiCenterSqlProcessorMutator.applyConstIfAvailable(sf, ((DerivedCellCalculatorRef) i), ht.getTable().getColumnIds());
							if (i.isConst()) {
								values.add((Comparable) i.get(null));
								continue;
							} else {
								values = null;
								break;
							}
						}
						if (values != null) {
							String id = AmiHdbUtils.getId((DerivedCellCalculatorRef) in.getLeft()[n], tablename);
							q.addPart(new AmiHdbQuery_In(ht.getColumn(id), values));
							continue;
						}
					}
				}
			} else if (dcc instanceof DerivedCellCalculator_SqlInnerSelectSingle) {
				DerivedCellCalculator_SqlInnerSelectSingle in = (DerivedCellCalculator_SqlInnerSelectSingle) dcc;
				dcc.get(sf);
				if (in.getLeft() instanceof DerivedCellCalculatorRef) {
					DerivedCellCalculatorRef left = (DerivedCellCalculatorRef) in.getLeft();
					q.addPart(new AmiHdbQuery_In(ht.getColumn(AmiHdbUtils.getId(left, tablename)), (Set) in.getInValues()));
					continue;
				}
			} else if (dcc instanceof DerivedCellCalculator_SqlInnerSelect) {
				DerivedCellCalculator_SqlInnerSelect in = (DerivedCellCalculator_SqlInnerSelect) dcc;
				dcc.get(sf);
				for (int n = 0; n < in.getLeft().length; n++) {
					if (in.getLeft()[n] instanceof DerivedCellCalculatorRef) {
						DerivedCellCalculatorRef left = (DerivedCellCalculatorRef) in.getLeft()[n];
						Set<Object[]> t = in.getInValues();
						Set<Object> vals = new HashSet<Object>();
						for (Object[] row : t)
							vals.add(row[n]);
						q.addPart(new AmiHdbQuery_In(ht.getColumn(AmiHdbUtils.getId(left, tablename)), (Set) vals));
						continue;
					}
				}
			} else if (dcc instanceof DerivedCellCalculatorOr) {
				DerivedCellCalculatorOr or = (DerivedCellCalculatorOr) dcc;
				AmiHdbQuery_In t = toInClause(or.getParams(), ht);
				if (t != null) {
					q.addPart(t);
					continue;
				}
			}
			if (remaining == null)
				remaining = dcc;
			else
				remaining = DerivedCellCalculatorMath.valueOf(0, OperationNode.OP_AMP_AMP, remaining, dcc);
			q.setAdditionalClause(remaining);
		}

	}

	static private AmiHdbQuery_In toInClause(DerivedCellCalculator[] parts, AmiHdbTable ht) {
		String colNameCommon = null;
		HashSet<Comparable> invals = null;
		for (DerivedCellCalculator dcc : parts) {
			if (!(dcc instanceof DerivedCellCalculatorMath))
				return null;
			DerivedCellCalculatorMath math = (DerivedCellCalculatorMath) dcc;
			if (math.getOperationNodeCode() != OperationNode.OP_EQ_EQ)
				return null;
			String colName;
			Comparable value;
			if (math.getLeft() instanceof DerivedCellCalculatorRef && math.getRight().isConst()) {
				colName = (String) ((DerivedCellCalculatorRef) math.getLeft()).getId();
				value = (Comparable) (math.getRight()).get(null);
			} else if (math.getRight() instanceof DerivedCellCalculatorRef && math.getLeft().isConst()) {
				colName = (String) ((DerivedCellCalculatorRef) math.getRight()).getId();
				value = (Comparable) (math.getLeft()).get(null);
			} else
				return null;
			if (colNameCommon == null)
				colNameCommon = colName;
			else if (OH.ne(colNameCommon, colName))
				return null;
			if (invals == null)
				invals = new HashSet<Comparable>();
			invals.add(value);
		}
		return new AmiHdbQuery_In(ht.getColumn(colNameCommon), invals);
	}

	private static AmiHdbQuery_Compare getOperation(DerivedCellCalculatorMath code, AmiHdbColumn column, Comparable value) {
		byte math = code.getOperationType();
		switch (math) {
			case DerivedCellCalculatorMath.TYPE_MATH_EQ:
			case DerivedCellCalculatorMath.TYPE_STRING_EQ:
			case DerivedCellCalculatorMath.TYPE_BOOL_EQ:
			case DerivedCellCalculatorMath.TYPE_OBJECT_EQ:
				return new AmiHdbQuery_Eq(column, value);
			case DerivedCellCalculatorMath.TYPE_MATH_LT:
			case DerivedCellCalculatorMath.TYPE_STRING_LT:
				return new AmiHdbQuery_Lt(column, value);
			case DerivedCellCalculatorMath.TYPE_MATH_GT:
			case DerivedCellCalculatorMath.TYPE_STRING_GT:
				return new AmiHdbQuery_Gt(column, value);
			case DerivedCellCalculatorMath.TYPE_MATH_LE:
			case DerivedCellCalculatorMath.TYPE_STRING_LE:
				return new AmiHdbQuery_Le(column, value);
			case DerivedCellCalculatorMath.TYPE_MATH_GE:
			case DerivedCellCalculatorMath.TYPE_STRING_GE:
				return new AmiHdbQuery_Ge(column, value);
			case DerivedCellCalculatorMath.TYPE_MATH_NE:
			case DerivedCellCalculatorMath.TYPE_STRING_NE:
			case DerivedCellCalculatorMath.TYPE_BOOL_NE:
			case DerivedCellCalculatorMath.TYPE_OBJECT_NE:
				return new AmiHdbQuery_Ne(column, value);
			case DerivedCellCalculatorMath.TYPE_STRING_REGEX:
				return new AmiHdbQuery_Regex(column, value);
			case DerivedCellCalculatorMath.TYPE_STRING_NOT_JREGEX:
				return new AmiHdbQuery_NotJRegex(column, value);
			case DerivedCellCalculatorMath.TYPE_STRING_JREGEX:
				return new AmiHdbQuery_JRegex(column, value);
			case DerivedCellCalculatorMath.TYPE_CONST_FALSE:
			case DerivedCellCalculatorMath.TYPE_BOOL_OR:
			case DerivedCellCalculatorMath.TYPE_BOOL_AND:
			default:
				return null;
		}
	}
	static public void reduce(DerivedCellCalculator calc, List<DerivedCellCalculator> sink) {
		if (calc instanceof DerivedCellCalculatorMath) {
			final DerivedCellCalculatorMath c = (DerivedCellCalculatorMath) calc;
			if (c.getOperationType() == DerivedCellCalculatorMath.TYPE_BOOL_AND) {
				reduce(c.getLeft(), sink);
				reduce(c.getRight(), sink);
				return;
			}
		}
		sink.add(calc);
	}
}
