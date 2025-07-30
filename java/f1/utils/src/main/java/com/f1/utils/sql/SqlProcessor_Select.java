package com.f1.utils.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.sql.SqlProcessorUtils.GroupByDerivedCellParser;
import com.f1.utils.sql.aggs.AggregateFactory;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.JavaExpressionParser;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.string.node.VariableNode;
import com.f1.utils.string.sqlnode.AsNode;
import com.f1.utils.string.sqlnode.OnNode;
import com.f1.utils.string.sqlnode.SqlColumnsNode;
import com.f1.utils.string.sqlnode.SqlNode;
import com.f1.utils.string.sqlnode.SqlUnionNode;
import com.f1.utils.string.sqlnode.WhereNode;
import com.f1.utils.string.sqlnode.WildCharNode;
import com.f1.utils.structs.table.derived.BasicDerivedCellParser;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorRef;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.derived.NamespaceCalcTypesImpl;
import com.f1.utils.structs.table.stack.BasicCalcTypes;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.CalcTypesStack;
import com.f1.utils.structs.table.stack.ChildCalcTypesStack;
import com.f1.utils.structs.table.stack.TablesCalcFrame;

public class SqlProcessor_Select {

	final private SqlProcessor owner;

	public SqlProcessor_Select(SqlProcessor sqlProcessor) {
		this.owner = sqlProcessor;
	}

	//	public Object processSelect(CalcFrameStack sf, DerivedCellCalculatorSql query, SqlColumnsNode select, Map<String, Table> innerTables) {
	//		SelectClause sc = buildSelectClause(select);
	//		Object o = this.owner.processSelect(query, sc, sf, innerTables);
	//		if (o instanceof DerivedCellCalculatorSqlFlowControl) {
	//			DerivedCellCalculatorSqlFlowControl fc = ((DerivedCellCalculatorSqlFlowControl) o);
	//			if (sc.union != null) {
	//				List<Table> results = new ArrayList<Table>();
	//				List<Boolean> byNames = new ArrayList<Boolean>();
	//				byNames.add(false);
	//				for (SelectClause i = sc; i.union != null; i = i.union)
	//					byNames.add(i.unionByName);
	//				fc.attachResponseHandler(new SqlProcessorResponseHandler_Union(sf, query, this.owner, results, byNames, sc.union));
	//			}
	//			return fc;
	//		}
	//
	//		Table r = (Table) o;
	//		//		boolean byName = sc.unionByName;
	//		//		sc = sc.union;
	//		//		if (sc != null) {//we need to do unions
	//		//			List<Table> results = new ArrayList<Table>();
	//		//			List<Boolean> byNames = new ArrayList<Boolean>();
	//		//			byNames.add(Boolean.FALSE);
	//		//			results.add(r);
	//		//			int columnsCount = r.getColumnsCount();
	//		//			while (sc != null) {
	//		//				Object o2 = this.owner.processSelect(query, sc, sf, innerTables);
	//		//				if (o2 instanceof DerivedCellCalculatorSqlFlowControl) {
	//		//					DerivedCellCalculatorSqlFlowControl fc = ((DerivedCellCalculatorSqlFlowControl) o2);
	//		//					fc.attachResponseHandler(new SqlProcessorResponseHandler_Union(sf, query, this.owner, results, byNames, sc.union));
	//		//					return fc;
	//		//				}
	//		//				Table t2 = (Table) o2;//TODO:
	//		//				results.add(t2);
	//		//				byNames.add(byName);
	//		//				if (!byName && t2.getColumnsCount() != columnsCount)
	//		//					throw new ExpressionParserException(sc.select.getPosition(), "All selects participating in union must have same number of columns. First table has "
	//		//							+ columnsCount + " column(s), table #" + results.size() + " has " + t2.getColumnsCount() + " column(s)");
	//		//				byName = sc.unionByName;
	//		//				sc = sc.union;
	//		//			}
	//		//			r = unionTables(results, byNames, sf);
	//		//		}
	//		return r;
	//
	//	}

	public static Table unionTables(List<Table> results, List<Boolean> byNames, CalcFrameStack sf) {
		Table r = results.get(0);
		int columnsCount = r.getColumnsCount();
		Class[] types = new Class[columnsCount];
		boolean changed = false;
		boolean needsCast = false;
		for (int i = 0; i < columnsCount; i++) {
			Column targetCol = r.getColumnAt(i);
			Class<?> orig = types[i] = targetCol.getType();
			for (int j = 1; j < results.size(); j++) {
				Class<?> type;
				if (byNames.get(j).booleanValue()) {
					Column col = results.get(j).getColumnsMap().get(targetCol.getId());
					if (col == null)
						continue;
					type = col.getType();
				} else
					type = results.get(j).getColumnAt(i).getType();
				needsCast = needsCast || type != orig;
				types[i] = SqlProcessorUtils.getWidest(types[i], type);
			}
			changed = changed || orig != types[i];
		}
		int first = 1;
		if (changed) {//cant use the first table as the sink
			first = 0;
			String name = r.getTitle();
			r = SqlProjector.newBasicTable(types, TableHelper.getColumnNamesArray(r));
			r.setTitle(name);
		}
		Caster<?>[] casters = OH.getAllCasters(types);
		TableList rows = r.getRows();
		for (int j = first; j < results.size(); j++) {
			if (byNames.get(j).booleanValue()) {
				Table toInsert = (Table) results.get(j);
				Map<String, Column> c2 = toInsert.getColumnsMap();
				int matchingColumnsCount = 0;
				int mappingIn[] = new int[columnsCount];
				int mappingOut[] = new int[columnsCount];
				Caster<?> casters2[] = new Caster[columnsCount];
				for (int i = 0; i < r.getColumnsCount(); i++) {
					Column outCol = r.getColumnAt(i);
					Column inCol = c2.get(outCol.getId());
					if (inCol != null) {
						mappingIn[matchingColumnsCount] = inCol.getLocation();
						mappingOut[matchingColumnsCount] = outCol.getLocation();
						casters2[matchingColumnsCount] = outCol.getTypeCaster();
						matchingColumnsCount++;
					}
				}
				for (Row sourceRow : results.get(j).getRows()) {
					Object[] row = new Object[columnsCount];
					for (int i = 0; i < matchingColumnsCount; i++) {
						Object value = sourceRow.getAt(mappingIn[i]);
						int x = mappingOut[i];
						row[x] = casters2[i].cast(value, false, false);
					}
					rows.addRow(row);
				}
			} else {
				for (Row row : results.get(j).getRows()) {
					Object[] values = row.getValuesCloned();
					if (needsCast)
						for (int i = 0; i < columnsCount; i++)
							values[i] = casters[i].cast(values[i]);
					rows.addRow(values);
				}
			}
		}
		if (sf.getSqlPlanListener() != null)
			sf.getSqlPlanListener().onStep("UNION", "Combined " + results.size() + " Tables into " + r.getSize() + " rows");
		return r;
	}

	public static void applyTable(SelectClause sc, NamespaceCalcTypesImpl vars, Set<String> ambiguosColumns, String tableName, String asTableName, Table table) {
		if (table == null)
			throw new ExpressionParserException(sc.getFrom().getPosition(), "Unknown table: " + tableName);
		//		if (asTables.put(asTableName, table) != null)
		//			throw new ExpressionParserException(sc.getFrom().getPosition(), "Duplicate table alias: " + asTableName);
		for (int i = 0, n = table.getColumnsCount(); i < n; i++) {
			Column t = table.getColumnAt(i);
			String key = (String) t.getId();
			Class<?> type = t.getType();
			if (vars.getType(key) != null)
				ambiguosColumns.add(key);
			else
				vars.putType(key, type);
		}
		vars.addNamespace(asTableName, table.getColumnTypesMapping());
	}

	final public FlowControl processSelect(DerivedCellCalculatorSql query, SelectClause sc, CalcFrameStack sf, Map<String, Table> asTables) {
		AsNode[] fromTables = sc.getTables();
		NamespaceCalcTypesImpl vars = new NamespaceCalcTypesImpl();
		Set<String> ambiguosColumns = fromTables.length < 2 ? Collections.EMPTY_SET : new HashSet<String>();

		String[] tableNames = new String[asTables.size()];
		Table[] tables = new Table[asTables.size()];
		int q = 0;
		for (Entry<String, Table> i : asTables.entrySet()) {
			applyTable(sc, vars, ambiguosColumns, i.getValue().getTitle(), i.getKey(), i.getValue());
			tableNames[q] = i.getKey();
			tables[q] = i.getValue();
			q++;
		}
		AsNode[] columns = sc.getSelects();
		final BasicDerivedCellParser dcp = this.owner.getParser();
		final AggregateFactory factory;
		DerivedCellCalculator[] groupByCalcs;
		Node[] groupBys = sc.getGroupBys();
		final CalcTypesStack context2;
		TablesCalcFrame tf = new TablesCalcFrame(tableNames, tables);
		if (groupBys != null) {
			groupByCalcs = new DerivedCellCalculator[groupBys.length];
			GroupByDerivedCellParser gdcp = new GroupByDerivedCellParser(null, this.owner, sc);
			factory = new AggregateFactory(sf.getFactory());
			context2 = new ChildCalcTypesStack(sf, true, vars, factory);
			for (int i = 0; i < groupBys.length; i++) {
				Node node = groupBys[i];
				groupByCalcs[i] = gdcp.toCalc(node, context2);
				Set<Object> comm = DerivedHelper.getDependencyIdsInSet(groupByCalcs[i], ambiguosColumns);
				if (!comm.isEmpty())
					throw new ExpressionParserException(node.getPosition(), "Ambiguous column in GROUP BY clause: " + CH.first(comm));
			}
			if (!factory.getAggregates().isEmpty())
				throw new ExpressionParserException(sc.getGroupBy().getPosition(), "Aggregates not allowed in GROUP BY clause: " + CH.first(factory.getAggregates()));
			DerivedHelper.replaceVarsWithConsts(groupByCalcs, sf, tf);
		} else {
			groupByCalcs = null;
			factory = new AggregateFactory(sf.getFactory());
			context2 = new ChildCalcTypesStack(sf, true, vars, factory);
		}
		int limitOffset = 0;
		int limit = -1;
		if (sc.getLimits() != null) {
			limit = sc.getLimits().getLimit(dcp, sf);
			limitOffset = sc.getLimits().getLimitOffset(dcp, sf);
		} else
			limit = sf.getLimit();

		DerivedCellCalculator where = sc.getWhere() == null ? null : dcp.toCalc(sc.getWhere().getCondition(), context2);
		if (where != null) {
			if (Boolean.class != where.getReturnType())
				throw new ExpressionParserException(sc.getWhere().getPosition(), "WHERE clause must evaulate to boolean, not: " + where.getReturnType().getSimpleName());
			if (!ambiguosColumns.isEmpty()) {
				Set<Object> comm = DerivedHelper.getDependencyIdsInSet(where, ambiguosColumns);
				if (!comm.isEmpty())
					throw new ExpressionParserException(sc.getWhere().getPosition(), "Ambiguous column in WHERE clause: " + CH.first(comm));
			}
			DerivedHelper.replaceVarsWithConsts(where, sf, tf);
		}
		int joinType = sc.getJoinType();

		DerivedCellCalculator join;
		if (sc.getJoin() != null) {
			join = dcp.toCalc(sc.getJoin(), context2);
			if (Boolean.class != join.getReturnType())
				throw new ExpressionParserException(sc.getJoin().getPosition(), "ON clause must evaulate to boolean, not: " + join.getReturnType().getSimpleName());
			if (!ambiguosColumns.isEmpty()) {
				Set<Object> comm = DerivedHelper.getDependencyIdsInSet(join, ambiguosColumns);
				if (!comm.isEmpty())
					throw new ExpressionParserException(sc.getJoin().getPosition(), "Ambiguous column in ON clause: " + CH.first(comm));
			}
			DerivedHelper.replaceVarsWithConsts(join, sf, tf);
		} else
			join = null;
		DerivedCellCalculator joinNearest;
		if (sc.getJoinNearest() != null) {
			joinNearest = dcp.toCalc(sc.getJoinNearest(), context2);
			if (Boolean.class != joinNearest.getReturnType())
				throw new ExpressionParserException(sc.getJoin().getPosition(), "ON clause must evaulate to boolean, not: " + joinNearest.getReturnType().getSimpleName());
			if (!ambiguosColumns.isEmpty()) {
				Set<Object> comm = DerivedHelper.getDependencyIdsInSet(joinNearest, ambiguosColumns);
				if (!comm.isEmpty())
					throw new ExpressionParserException(sc.getJoinNearest().getPosition(), "Ambiguous column in ON clause: " + CH.first(comm));
			}
			DerivedHelper.replaceVarsWithConsts(joinNearest, sf, tf);
		} else
			joinNearest = null;

		//Expand stars

		columns = expandWildCards(columns, asTables);

		DerivedCellCalculator[] selectCalcs = new DerivedCellCalculator[columns.length];
		String[] names = new String[columns.length];
		for (int i = 0; i < columns.length; i++) {
			AsNode node = columns[i];
			names[i] = node.getAs().toString();
			try {
				selectCalcs[i] = dcp.toCalc(node.getValue(), context2);
			} catch (Exception e) {
				throw new ExpressionParserException(node.getPosition(), e.getMessage(), e);
			}
			if (!ambiguosColumns.isEmpty()) {
				Set<Object> comm = DerivedHelper.getDependencyIdsInSet(selectCalcs[i], ambiguosColumns);
				if (!comm.isEmpty())
					throw new ExpressionParserException(node.getPosition(), "Ambiguous column in select clause: " + CH.first(comm));
			}
		}

		BasicCalcTypes finalTypes = new BasicCalcTypes();
		for (int n = 0; n < names.length; n++)
			finalTypes.putType(names[n], selectCalcs[n].getReturnType());
		final ChildCalcTypesStack context3 = new ChildCalcTypesStack(context2, finalTypes);
		DerivedCellCalculator having = sc.getHaving() == null ? null : dcp.toCalc(sc.getHaving().getColumnAt(0), context3);
		if (having != null) {
			DerivedHelper.replaceVarsWithConsts(having, sf, tf);
			if (Boolean.class != having.getReturnType())
				throw new ExpressionParserException(sc.getHaving().getPosition(), "having clause must evaulate to boolean, not: " + having.getReturnType().getSimpleName());
			if (!ambiguosColumns.isEmpty()) {
				Set<Object> comm = DerivedHelper.getDependencyIdsInSet(having, ambiguosColumns);
				if (!comm.isEmpty())
					throw new ExpressionParserException(sc.getHaving().getPosition(), "Ambiguous column in having clause: " + CH.first(comm));
			}
		}

		Node[] orderBys = sc.getOrderBys();
		DerivedCellCalculator[] orderByCalcs;
		if (orderBys != null) {
			orderByCalcs = new DerivedCellCalculator[orderBys.length];
			for (int i = 0; i < orderBys.length; i++) {
				Node node = orderBys[i];
				DerivedCellCalculator calc = null;
				if (node instanceof VariableNode) {
					String name = ((VariableNode) node).getVarname();
					int pos = AH.indexOf(name, names);
					if (pos != -1)
						calc = new DerivedCellCalculatorRef(node.getPosition(), selectCalcs[pos].getReturnType(), name);
				}
				if (calc == null) {
					calc = dcp.toCalc(node, context2);
				}

				orderByCalcs[i] = calc;
				Set<Object> comm = DerivedHelper.getDependencyIdsInSet(calc, ambiguosColumns);
				if (!comm.isEmpty())
					throw new ExpressionParserException(node.getPosition(), "Ambiguous column in order by clause: " + CH.first(comm));
			}
			DerivedHelper.replaceVarsWithConsts(orderByCalcs, sf, tf);
		} else {
			orderByCalcs = null;
		}

		if (sc.getUnpacks() != null) {
			Set<String> namesSet = CH.s(names);
			for (OnNode on : sc.getUnpacks()) {
				String col = on.getValue().toString();
				if (!namesSet.contains(col))
					throw new ExpressionParserException(on.getPosition(), "unknown column in UNPACK clause: " + col);
			}
		}

		FlowControl r = project(query, asTables, names, selectCalcs, joinType, join, joinNearest, where, groupByCalcs, having, sc.getUnpacks(), orderByCalcs, sc.getOrderByAsc(),
				limitOffset, limit, sf, context2);
		return r;
	}
	protected FlowControl project(DerivedCellCalculatorSql query, Map<String, Table> asTables, String[] names, DerivedCellCalculator[] selectCalcs, int joinType,
			DerivedCellCalculator join, DerivedCellCalculator joinNearest, DerivedCellCalculator where, DerivedCellCalculator[] groupByCalcs, DerivedCellCalculator having,
			OnNode[] unpacks, DerivedCellCalculator[] orderByCalcs, boolean[] orderByAsc, int limitOffset, int limit, CalcFrameStack sf, CalcTypesStack context) {
		Table t = SqlProjector.project(query, asTables, names, selectCalcs, joinType, join, joinNearest, where, groupByCalcs, having, unpacks, orderByCalcs, orderByAsc,
				limitOffset, limit, this.owner, sf, context);
		return new TableReturn(t);
	}

	public static AsNode[] expandWildCards(AsNode[] columns, Map<String, Table> tables) {
		boolean hasWildCard = false;
		for (int i = 0; i < columns.length; i++) {
			Node val = columns[i].getValue();
			if (val instanceof WildCharNode) {
				hasWildCard = true;
				break;
			}
		}
		if (!hasWildCard)
			return columns;

		HasherSet<String> columnNames = new HasherSet<String>();
		for (int i = 0; i < columns.length; i++) {
			AsNode node = columns[i];
			if (node.getValue() instanceof WildCharNode)
				continue;
			String s = (String) ((VariableNode) node.getAs()).getVarname();
			if (!columnNames.add(s)) {
				s = SH.getNextId(s, columnNames, 2);
				columnNames.add(s);
			}
		}

		List<AsNode> columns2 = new ArrayList<AsNode>();
		for (int i = 0; i < columns.length; i++) {
			AsNode node = columns[i];
			final int pos = node.getPosition();
			if (!(node.getValue() instanceof WildCharNode)) {
				columns2.add(node);
			} else {
				WildCharNode wildCard = (WildCharNode) node.getValue();
				Set<String> excepts;
				if (wildCard.getExceptsCount() == 0)
					excepts = Collections.EMPTY_SET;
				else {
					excepts = new HashSet<String>(wildCard.getExceptsCount());
					for (int n = 0; n < wildCard.getExceptsCount(); n++)
						excepts.add(wildCard.getExceptAt(n).getVarname());
				}
				if (wildCard.getTableName() == null) {
					if (tables.size() == 1) {
						Entry<String, Table> e = CH.first(tables.entrySet());
						String tName = e.getKey();
						for (Column c : e.getValue().getColumns()) {
							String col = (String) c.getId();
							if (excepts.contains(col))
								continue;
							String colName = col;
							if (!columnNames.add(colName)) {
								colName = tName + '_' + colName;
								if (!columnNames.add(colName)) {
									colName = SH.getNextId(colName, columnNames, 2);
									columnNames.add(colName);
								}
							}
							columns2.add(new AsNode(pos, new VariableNode(pos, col), new VariableNode(pos, colName), false));
						}
					} else {
						for (Entry<String, Table> e : tables.entrySet()) {
							String tName = e.getKey();

							for (Column c : e.getValue().getColumns()) {
								String col = (String) c.getId();
								if (excepts.contains(col))
									continue;
								String colName = col;
								if (!columnNames.add(colName)) {
									colName = tName + '_' + colName;
									if (!columnNames.add(colName)) {
										colName = SH.getNextId(colName, columnNames, 2);
										columnNames.add(colName);
									}
								}
								columns2.add(new AsNode(pos, new OperationNode(pos, new VariableNode(pos, tName), new VariableNode(pos, col), OperationNode.OP_PERIOD),
										new VariableNode(pos, colName), false));
							}
						}
					}
				} else {
					String tName = wildCard.getTableName();
					Table table = tables.get(tName);
					if (table == null)
						throw new ExpressionParserException(node.getPosition(), "Unknown table name: " + tName);
					for (Column c : table.getColumns()) {
						String col = (String) c.getId();
						if (excepts.contains(col))
							continue;
						String colName = col;
						if (!columnNames.add(colName)) {
							colName = tName + '_' + colName;
							if (!columnNames.add(colName)) {
								colName = SH.getNextId(colName, columnNames, 2);
								columnNames.add(colName);
							}
						}
						columns2.add(new AsNode(pos, new OperationNode(pos, new VariableNode(pos, tName), new VariableNode(pos, col), OperationNode.OP_PERIOD),
								new VariableNode(pos, colName), false));
					}
				}
			}
		}

		return columns2.toArray(new AsNode[columns2.size()]);

	}
	public static SelectClause buildSelectClause(SqlColumnsNode select) {
		if (select.getOperation() != SqlExpressionParser.ID_SELECT)
			throw new ExpressionParserException(select.getPosition(), "Expecting SELECT");

		SqlColumnsNode from;
		if (select.getNext() == null) {
			return new SelectClause(select, null, null, null, null, null, null, null, null, null, null, null, false);
		} else if (SqlExpressionParser.castNextToSqlNodeNoThrow(select, SqlExpressionParser.ID_UNION) != null) {
			SqlUnionNode union = (SqlUnionNode) SqlExpressionParser.castNextToSqlNode(select, SqlExpressionParser.ID_UNION);
			SelectClause unionSelectClause = buildSelectClause(JavaExpressionParser.castNode(union.getNext(), SqlColumnsNode.class, "SELECT clause after UNION"));
			return new SelectClause(select, null, null, null, null, null, null, null, null, null, null, unionSelectClause, union.isByName());
		} else {
			from = (SqlColumnsNode) SqlExpressionParser.castNextToSqlNode(select, SqlExpressionParser.ID_FROM);
			if (from.getColumnsCount() < 1)
				throw new ExpressionParserException(select.getPosition(), "Expecting at least one table");
		}

		WhereNode where = null;
		SqlColumnsNode groupby = null;
		SqlColumnsNode having = null;
		SqlUnionNode union = null;
		SqlColumnsNode orderby = null;
		SqlColumnsNode limit = null;
		SqlColumnsNode join = null;
		SqlColumnsNode joinOn = null;
		SqlColumnsNode joinNearest = null;
		SqlColumnsNode unpack = null;
		Node node = from.getNext();
		while (node != null) {
			if (!(node instanceof SqlNode))
				throw new ExpressionParserException(node.getPosition(), "expecting sql statement (not " + node.getClass().getSimpleName() + ")");
			SqlNode sqlNode = (SqlNode) node;
			switch (sqlNode.getOperation()) {
				case SqlExpressionParser.ID_WHERE:
					if (where != null || groupby != null || having != null || union != null || orderby != null || limit != null)
						throw new ExpressionParserException(node.getPosition(), "Unexpected sql statement: " + SqlExpressionParser.toOperationString(sqlNode.getOperation()));
					where = (WhereNode) sqlNode;
					node = sqlNode.getNext();
					break;
				case SqlExpressionParser.ID_GROUPBY:
					if (groupby != null || having != null || union != null || orderby != null || limit != null)
						throw new ExpressionParserException(node.getPosition(), "Unexpected sql statement: " + SqlExpressionParser.toOperationString(sqlNode.getOperation()));
					groupby = JavaExpressionParser.castNode(sqlNode, SqlColumnsNode.class);
					node = sqlNode.getNext();
					break;
				case SqlExpressionParser.ID_HAVING:
					if (having != null || union != null || orderby != null || limit != null)
						throw new ExpressionParserException(node.getPosition(), "Unexpected sql statement: " + SqlExpressionParser.toOperationString(sqlNode.getOperation()));
					having = JavaExpressionParser.castNode(sqlNode, SqlColumnsNode.class);
					node = sqlNode.getNext();
					break;
				case SqlExpressionParser.ID_ORDERBY:
					if (orderby != null || limit != null || union != null)
						throw new ExpressionParserException(node.getPosition(), "Unexpected sql statement: " + SqlExpressionParser.toOperationString(sqlNode.getOperation()));
					orderby = JavaExpressionParser.castNode(sqlNode, SqlColumnsNode.class);
					node = sqlNode.getNext();
					break;
				case SqlExpressionParser.ID_LIMIT:
					if (limit != null || union != null)
						throw new ExpressionParserException(node.getPosition(), "Unexpected sql statement: " + SqlExpressionParser.toOperationString(sqlNode.getOperation()));
					limit = JavaExpressionParser.castNode(sqlNode, SqlColumnsNode.class);
					node = sqlNode.getNext();
					break;
				case SqlExpressionParser.ID_UNION:
					if (union != null)
						throw new ExpressionParserException(node.getPosition(), "Unexpected sql statement: " + SqlExpressionParser.toOperationString(sqlNode.getOperation()));
					union = JavaExpressionParser.castNode(sqlNode, SqlUnionNode.class);
					node = null;
					break;
				case SqlExpressionParser.ID_JOIN:
				case SqlExpressionParser.ID_LEFT_JOIN:
				case SqlExpressionParser.ID_RIGHT_JOIN:
				case SqlExpressionParser.ID_OUTER_JOIN:
				case SqlExpressionParser.ID_LEFT_ONLY_JOIN:
				case SqlExpressionParser.ID_RIGHT_ONLY_JOIN:
				case SqlExpressionParser.ID_OUTER_ONLY_JOIN: {
					if (join != null)
						throw new ExpressionParserException(node.getPosition(),
								"Multiple JOIN statements not supported: " + SqlExpressionParser.toOperationString(sqlNode.getOperation()));
					join = JavaExpressionParser.castNode(sqlNode, SqlColumnsNode.class);
					SqlNode next = JavaExpressionParser.castNode(join.getNext(), SqlColumnsNode.class);
					if (next != null && next.getOperation() == SqlExpressionParser.ID_ON) {
						joinOn = (SqlColumnsNode) next;
						if (joinOn.getColumnsCount() == 0 || joinOn.getColumnAt(0) == null)
							throw new ExpressionParserException(joinOn.getPosition(), "ON expecting clause");
						next = JavaExpressionParser.castNode(joinOn.getNext(), SqlNode.class);
					}
					if (next != null && next.getOperation() == SqlExpressionParser.ID_NEAREST) {
						joinNearest = (SqlColumnsNode) next;
						next = joinNearest.getNext() == null ? null : JavaExpressionParser.castNode(joinNearest.getNext(), SqlNode.class);
					}
					if (joinOn == null && joinNearest == null && next != null)
						throw new ExpressionParserException(next.getPosition(), "Expecting ON or NEAREST, not: " + SqlExpressionParser.toOperationString(sqlNode.getOperation()));
					node = next;
					break;
				}
				case SqlExpressionParser.ID_UNPACK:
					if (unpack != null)
						throw new ExpressionParserException(node.getPosition(), "Multiple Unpack not supported: " + SqlExpressionParser.toOperationString(sqlNode.getOperation()));
					unpack = JavaExpressionParser.castNode(sqlNode, SqlColumnsNode.class);
					node = sqlNode.getNext();
					break;
				default:
					throw new ExpressionParserException(node.getPosition(), "Unexpected sql statement: " + SqlExpressionParser.toOperationString(sqlNode.getOperation()));
			}
		}

		if (union == null) {
			return new SelectClause(select, from, join, joinOn, joinNearest, where, groupby, having, orderby, limit, unpack, null, false);
		} else {
			QueryClause unionSelectClause = SqlProcessor.buildQueryClause(union.getNext());
			return new SelectClause(select, from, join, joinOn, joinNearest, where, groupby, having, orderby, limit, unpack, unionSelectClause, union.isByName());
		}
	}

}
