package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.portlets.AmiWebDmUtils;
import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.utils.Hasher;
import com.f1.utils.LH;
import com.f1.utils.LocalToolkit;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.impl.ArrayHasher;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.Node;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.string.node.VariableNode;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.RowFilter;
import com.f1.utils.structs.table.derived.BasicDerivedCellParser;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorMath;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorRef;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorWrapper;
import com.f1.utils.structs.table.stack.BasicCalcTypes;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.CalcTypesStack;
import com.f1.utils.structs.table.stack.ChildCalcTypesStack;
import com.f1.utils.structs.table.stack.EmptyCalcFrameStack;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class AmiWebRealtimeTableCustomFilter extends BasicDerivedCellParser implements RowFilter, Hasher<Row> {
	static private final Logger log = LH.get();

	final private AmiWebService service;
	private Table source;
	final private HasherMap<String, Integer> sourceColumns = new HasherMap<String, Integer>();
	final private HasherMap<String, Class> sourceColumnTypes = new HasherMap<String, Class>();
	final private HasherMap<String, Class> targetColumnTypes = new HasherMap<String, Class>();
	final private HasherMap<Row, Object[]> sourceRows = new HasherMap<Row, Object[]>(this);
	final private HasherSet<Object[]> sourceValues = new HasherSet<Object[]>(ArrayHasher.INSTANCE);

	private int[] sourceColumnsPositions;

	Object[] currentSourceRow;
	private DerivedCellCalculator where;
	private String whereClause = null;
	private CalcTypes targetColumnsMap;
	private Boolean constValue;
	private FastWebTable targetMapping;

	private Map<Object, List<Object[]>> cachedIndexValues;
	private HasherMap<Object[], List<Object[]>> compositeIndexValues;

	private AmiWebDmLink lastLink;

	private String layoutAlias;

	public static final byte RESET_NOCHANGE = 0;
	public static final byte RESET_INDEX = 1;
	public static final byte RESET_INDEX_AND_FILTER = 3;

	public byte reset(AmiWebDmLink link, Table source, com.f1.base.CalcTypes mapping, FastWebTable targetMapping) {
		this.source = source;
		this.cachedIndexValues = null;
		boolean needsEval = true;
		if (lastLink == link) {
			outer: if (OH.eq(this.whereClause, getWhereClause(link))) {
				for (Entry<String, Class> i : targetColumnTypes.entrySet()) {
					final Class col = mapping.getType(i.getKey());
					if (col == null || !col.equals(i.getValue()))
						break outer;
				}
				for (Entry<String, Class> i : sourceColumnTypes.entrySet()) {
					final Column col = source.getColumnsMap().get(i.getKey());
					if (col == null || !col.getType().equals(i.getValue()))
						break outer;
				}
				for (Entry<String, Integer> s : sourceColumns.entrySet())
					if (sourceColumnsPositions[s.getValue()] != source.getColumn(s.getKey()).getLocation())
						break outer;
				needsEval = false;
			}
		}

		if (needsEval) {
			this.constValue = null;
			this.lastLink = link;
			targetColumnTypes.clear();
			sourceColumnTypes.clear();
			sourceColumns.clear();
			this.targetColumnsMap = new BasicCalcTypes(mapping);
			this.whereClause = getWhereClause(link);
			sourceColumnsPositions = null;
			if (SH.isnt(this.whereClause)) {
				constValue = Boolean.FALSE;
				return RESET_INDEX_AND_FILTER;
			}
			this.targetMapping = targetMapping;
			DerivedCellCalculator calc;
			try {
				calc = this.toCalc(getWhereClause(link),
						new ChildCalcTypesStack(EmptyCalcFrameStack.INSTANCE, true, EmptyCalcTypes.INSTANCE, service.getScriptManager(layoutAlias).getMethodFactory()));
			} catch (RuntimeException e) {
				resetDueToError(e);
				throw e;
			}

			List<Tuple2<DerivedCellCalculatorRefSource, DerivedCellCalculatorRef>> sink = new ArrayList<Tuple2<DerivedCellCalculatorRefSource, DerivedCellCalculatorRef>>();
			this.isSimple = determineIndex(calc, sink);
			if (sink.isEmpty()) {
				this.sourceKeys = null;
				this.targetKeys = null;
			} else {
				this.sourceKeys = new DerivedCellCalculatorRefSource[sink.size()];
				this.targetKeys = new DerivedCellCalculatorRef[sink.size()];
				for (int i = 0; i < sink.size(); i++) {
					this.sourceKeys[i] = sink.get(i).getA();
					this.targetKeys[i] = sink.get(i).getB();
				}
			}

			this.targetMapping = null;
			this.where = calc;
			if (this.where.isConst())
				this.constValue = (Boolean) this.where.get(null);
			targetColumnsMap = null;
			sourceColumnsPositions = new int[sourceColumns.size()];
			for (Entry<String, Integer> s : sourceColumns.entrySet())
				sourceColumnsPositions[s.getValue()] = source.getColumn(s.getKey()).getLocation();
			if (this.sourceColumnTypes.isEmpty() && this.targetColumnTypes.isEmpty()) {
				this.constValue = (Boolean) this.where.get(null);
				if (this.constValue == null)
					this.constValue = Boolean.FALSE;
			}

		} else if (this.constValue != null && this.targetKeys == null)
			return RESET_NOCHANGE;
		sourceRows.clear();
		for (Row row : source.getRows()) {
			Entry<Row, Object[]> entry = sourceRows.getOrCreateEntry(row);
			if (entry.getValue() != null)
				continue;
			Object[] value = new Object[this.sourceColumnsPositions.length];
			for (int i = 0; i < value.length; i++)
				value[i] = row.getAt(this.sourceColumnsPositions[i]);
			entry.setValue(value);
		}
		if (areSame(sourceRows.values(), this.sourceValues)) {
			sourceRows.clear();
			return needsEval ? RESET_INDEX_AND_FILTER : RESET_NOCHANGE;
		} else {
			sourceValues.clear();
			sourceValues.addAll(sourceRows.values());
			sourceRows.clear();
			return RESET_INDEX_AND_FILTER;
		}
	}
	private boolean areSame(Collection<Object[]> collection, HasherSet<Object[]> values2) {
		if (collection.size() != values2.size())
			return false;
		for (Object[] i : collection)
			if (!values2.contains(i))
				return false;
		return true;
	}
	public AmiWebRealtimeTableCustomFilter(AmiWebService service, String layoutAlias) {
		super(service.getScriptManager(layoutAlias).getExpressionParser());
		this.layoutAlias = layoutAlias;
		this.service = service;
	}
	private AmiWebRealtimeTableCustomFilter() {
		super(null);
		this.service = null;
	}

	public boolean shouldKeepAndCheckForIndex(Row row) {
		return shouldKeep(row, null);
	}
	@Override
	public boolean shouldKeep(Row row, LocalToolkit localToolkit) {
		return shouldKeep(row);
	}
	public boolean shouldKeep(CalcFrame row) {
		try {
			if (this.constValue != null)
				return this.constValue.booleanValue();
			ReusableCalcFrameStack rsf = new ReusableCalcFrameStack(EmptyCalcFrameStack.INSTANCE);
			if (targetKeys != null) {
				getValuesForIndex();
				List<Object[]> sv;
				if (this.compositeIndexValues != null) {
					int positions[] = new int[sourceKeys.length];
					for (int j = 0; j < sourceKeys.length; j++) {
						positions[j] = sourceKeys[j].sourceArrayPos;
					}
					Object values[] = this.compositeKeyBuffer;
					for (int j = 0; j < sourceKeys.length; j++)
						values[j] = row.getValue((String) targetKeys[j].getId());
					sv = this.compositeIndexValues.get(values);
				} else
					sv = this.cachedIndexValues.get(row.getValue(getIndex()));
				if (sv == null)
					return false;
				if (isSimple)
					return true;
				for (Object[] row2 : sv) {
					this.currentSourceRow = row2;
					if (Boolean.TRUE.equals(this.where.get(rsf.reset(row))))
						return true;
				}
			}
			for (Object[] row2 : sourceValues) {
				this.currentSourceRow = row2;
				if (Boolean.TRUE.equals(this.where.get(rsf.reset(row))))
					return true;
			}
			return false;
		} catch (Exception e) {
			resetDueToError(e);
			return false;
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("AMI_REALTIME:[ const=").append(constValue).append(" where: ");
		SH.s(where, sb);
		sb.append(" rows:");
		if (this.sourceValues != null) {
			for (Object[] i : this.sourceValues)
				SH.join(",", i, sb).append(" | ");
		}
		sb.append("] ");
		return sb.toString();
	}
	private void resetDueToError(Exception e) {
		this.constValue = Boolean.FALSE;
		LH.info(log, "Error procesing filter so resetting this filter", e);
		this.whereClause = null;
		this.targetKeys = null;
		this.sourceKeys = null;

	}
	@Override
	public DerivedCellCalculator processNode(Node node, CalcTypesStack context) {
		if (node instanceof VariableNode) {
			String varname = DerivedCellCalculatorRef.stripTicks(((VariableNode) node).getVarname());
			if (varname.startsWith(AmiWebDmUtils.VARPREFIX_TARGET)) {
				String varname2 = SH.stripPrefix(varname, AmiWebDmUtils.VARPREFIX_TARGET, true);
				final Class col;
				final String colId;
				if (targetMapping != null) {
					WebColumn col2 = null;
					for (String i : targetMapping.getColumnIds()) {
						WebColumn col3 = targetMapping.getColumn(i);
						if (varname2.equals(col3.getColumnName())) {
							col2 = col3;
							break;
						}
					}
					if (col2 == null)
						throw new ExpressionParserException(node.getPosition(), "Unknown source variable: " + varname);
					String mappedId = (String) col2.getTableColumns()[0];
					colId = mappedId;
					col = targetColumnsMap.getType(colId);
					if (col == null)
						throw new ExpressionParserException(node.getPosition(), "Unknown underlying column: " + varname + " --> " + mappedId);
				} else {
					colId = varname2;
					col = targetColumnsMap.getType(colId);
					if (col == null)
						throw new ExpressionParserException(node.getPosition(), "Unknown target variable: " + varname);
				}
				this.targetColumnTypes.put((String) colId, col);
				return new DerivedCellCalculatorRef(node.getPosition(), col, colId);
			} else if (varname.startsWith(AmiWebDmUtils.VARPREFIX_SOURCE)) {
				String varname2 = SH.stripPrefix(varname, AmiWebDmUtils.VARPREFIX_SOURCE, true);
				Column col = source.getColumnsMap().get(varname2);
				Entry<String, Integer> e = sourceColumns.getOrCreateEntry(varname2);
				int pos;
				if (e.getValue() == null) {
					e.setValue(pos = sourceColumns.size() - 1);
					sourceColumnTypes.put(varname2, col.getType());
				} else
					pos = e.getValue().intValue();

				if (col == null)
					throw new ExpressionParserException(node.getPosition(), "Unknown source variable: " + varname);
				return new DerivedCellCalculatorRefSource(node.getPosition(), col.getType(), varname2, pos);
			} else
				throw new ExpressionParserException(node.getPosition(), "Unknown variable: " + varname);
		} else {
			DerivedCellCalculator r = super.processNode(node, context);
			if (r instanceof DerivedCellCalculatorMath) {
				return new DerivedCellCalculatorWrapper(r);
			} else
				return r;
		}
	}

	public class DerivedCellCalculatorRefSource extends DerivedCellCalculatorRef {

		int sourceArrayPos = 0;

		public DerivedCellCalculatorRefSource(int position, Class<?> type, Object id, int sourceArrayPos) {
			super(position, type, id);
			this.sourceArrayPos = sourceArrayPos;
		}
		@Override
		public Object get(CalcFrameStack key) {
			return AmiWebRealtimeTableCustomFilter.this.currentSourceRow[sourceArrayPos];
		}
		@Override
		public boolean isSame(DerivedCellCalculator other) {
			if (!super.isSame(other))
				return false;
			DerivedCellCalculatorRefSource o = (DerivedCellCalculatorRefSource) other;
			return OH.eq(sourceArrayPos, o.sourceArrayPos);
		}

	}

	@Override
	public int hashcode(Row o) {
		int r = 0;
		for (int i : this.sourceColumnsPositions)
			r += OH.hashCode(o.getAt(i)) + r * 31;
		return r;
	}
	@Override
	public boolean areEqual(Row l, Row r) {
		for (int i : this.sourceColumnsPositions)
			if (OH.ne(l.getAt(i), r.getAt(i)))
				return false;
		return true;
	}
	public String getIndex() {
		return targetKeys == null ? null : (String) targetKeys[0].getId();
	}

	Object compositeKeyBuffer[];

	public Set<Object> getValuesForIndex() {
		if (cachedIndexValues == null) {
			try {
				if (sourceKeys != null) {
					int len = sourceKeys.length;
					int position = sourceKeys[0].sourceArrayPos;
					HasherMap<Object, List<Object[]>> r = new HasherMap<Object, List<Object[]>>();
					for (Object[] i : sourceValues) {
						{
							Object value = i[position];
							Entry<Object, List<Object[]>> entry = r.getOrCreateEntry(value);
							if (entry.getValue() == null)
								entry.setValue(new ArrayList<Object[]>());
							entry.getValue().add(i);
						}
					}
					cachedIndexValues = r;
					if (len > 1) {
						int positions[] = new int[len];
						for (int j = 0; j < len; j++) {
							positions[j] = sourceKeys[j].sourceArrayPos;
						}
						this.compositeIndexValues = new HasherMap<Object[], List<Object[]>>(ArrayHasher.INSTANCE);
						this.compositeKeyBuffer = new Object[len];
						for (Object[] i : sourceValues) {
							{
								Object[] values = new Object[len];
								for (int j = 0; j < len; j++)
									values[j] = i[positions[j]];
								Entry<Object[], List<Object[]>> entry = this.compositeIndexValues.getOrCreateEntry(values);
								if (entry.getValue() == null)
									entry.setValue(new ArrayList<Object[]>());
								entry.getValue().add(i);
							}
						}
					} else
						this.compositeIndexValues = null;
				} else
					this.cachedIndexValues = Collections.EMPTY_MAP;

			} catch (Exception e) {
				resetDueToError(e);
				this.cachedIndexValues = Collections.EMPTY_MAP;
			}
		}
		return cachedIndexValues.keySet();

	}

	private DerivedCellCalculatorRef targetKeys[];
	private DerivedCellCalculatorRefSource sourceKeys[];

	private boolean isSimple;

	private boolean determineIndex(DerivedCellCalculator calc, List<Tuple2<DerivedCellCalculatorRefSource, DerivedCellCalculatorRef>> srcTgtSink) {
		if (calc.isConst())
			return false;
		if (calc instanceof DerivedCellCalculatorWrapper) {
			DerivedCellCalculatorWrapper wrapper = (DerivedCellCalculatorWrapper) calc;
			final DerivedCellCalculatorMath c = (DerivedCellCalculatorMath) wrapper.getInner();
			final DerivedCellCalculator left = c.getLeft();
			final DerivedCellCalculator rght = c.getRight();
			final int op = c.getOperationNodeCode();
			if (OperationNode.OP_EQ_EQ == op && left.getReturnType() == rght.getReturnType()) {
				if (left.getClass() == DerivedCellCalculatorRefSource.class && rght.getClass() == DerivedCellCalculatorRef.class) {
					srcTgtSink.add(new Tuple2<AmiWebRealtimeTableCustomFilter.DerivedCellCalculatorRefSource, DerivedCellCalculatorRef>((DerivedCellCalculatorRefSource) left,
							(DerivedCellCalculatorRef) rght));
					return true;
				}
				if (rght.getClass() == DerivedCellCalculatorRefSource.class && left.getClass() == DerivedCellCalculatorRef.class) {
					srcTgtSink.add(new Tuple2<AmiWebRealtimeTableCustomFilter.DerivedCellCalculatorRefSource, DerivedCellCalculatorRef>((DerivedCellCalculatorRefSource) rght,
							(DerivedCellCalculatorRef) left));
					return true;
				}
			} else if (OperationNode.OP_AMP_AMP == op) {
				return determineIndex(left, srcTgtSink) && determineIndex(rght, srcTgtSink);
			}
		}
		return false;
	}
	public Object getConstValue() {
		return this.constValue;
	}

	public static final AmiWebRealtimeTableCustomFilter HIDE_ALL = new AmiWebRealtimeTableCustomFilter() {
		@Override
		public boolean shouldKeep(Row row, LocalToolkit localToolkit) {
			return false;
		}
		public String toString() {
			return "HIDE_ALL";
		};
	};
	public static final AmiWebRealtimeTableCustomFilter KEEP_ALL = new AmiWebRealtimeTableCustomFilter() {
		@Override
		public boolean shouldKeep(Row row, LocalToolkit localToolkit) {
			return true;
		}

		public String toString() {
			return "KEEP_ALL";
		};
	};

	public static String getWhereClause(AmiWebDmLink link) {
		if (link.getWhereClauseVarNames().contains("WHERE"))
			return link.getWhereClause("WHERE");
		return null;
	}

}
