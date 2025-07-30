package com.f1.ami.center.table;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.ami.amicommon.AmiFactoryPlugin;
import com.f1.ami.amicommon.AmiPlugin;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiDatasourceColumn;
import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.center.AmiCenterState;
import com.f1.ami.center.AmiCenterUtils;
import com.f1.ami.center.AmiDboFactoryWrapper;
import com.f1.ami.center.dbo.AmiDbo;
import com.f1.ami.center.dbo.AmiDboBindingImpl;
import com.f1.ami.center.dbo.AmiDboBindingImpl.Callback;
import com.f1.ami.center.dbo.AmiDboBindingImpl.Method;
import com.f1.ami.center.dbo.AmiDboMethodWrapper;
import com.f1.ami.center.hdb.AmiHdb;
import com.f1.ami.center.hdb.AmiHdbSchema_Column;
import com.f1.ami.center.hdb.AmiHdbSchema_Index;
import com.f1.ami.center.hdb.AmiHdbSchema_Table;
import com.f1.ami.center.hdb.AmiHdbTableRep;
import com.f1.ami.center.procs.AmiStoredProc;
import com.f1.ami.center.procs.AmiStoredProcBindingImpl;
import com.f1.ami.center.procs.AmiStoredProcFactory;
import com.f1.ami.center.replication.AmiCenterReplication;
import com.f1.ami.center.replication.AmiCenterReplicationCenter;
import com.f1.ami.center.replication.AmiCenterReplicationHelper;
import com.f1.ami.center.sysschema.AmiSchema;
import com.f1.ami.center.table.index.AmiIndexMap;
import com.f1.ami.center.table.index.AmiIndexMap_Hash;
import com.f1.ami.center.table.index.AmiIndexMap_Rows;
import com.f1.ami.center.table.index.AmiIndexMap_Series;
import com.f1.ami.center.table.index.AmiIndexMap_Tree;
import com.f1.ami.center.table.index.AmiQueryFinderVisitor;
import com.f1.ami.center.table.index.AmiQueryFinder_Comparator;
import com.f1.ami.center.table.persist.AmiTablePersister;
import com.f1.ami.center.table.persist.AmiTablePersisterBindingImpl;
import com.f1.ami.center.table.persist.AmiTablePersisterFactory;
import com.f1.ami.center.timers.AmiTimer;
import com.f1.ami.center.timers.AmiTimerBindingImpl;
import com.f1.ami.center.timers.AmiTimerFactory;
import com.f1.ami.center.triggers.AmiTrigger;
import com.f1.ami.center.triggers.AmiTriggerBindingImpl;
import com.f1.ami.center.triggers.AmiTriggerFactory;
import com.f1.base.Bytes;
import com.f1.base.CalcFrame;
import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.DateMillis;
import com.f1.base.Pointer;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.SingletonIterator;
import com.f1.utils.TableHelper;
import com.f1.utils.TextMatcher;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.sql.DerivedCellCalculator_SqlIn;
import com.f1.utils.sql.DerivedCellCalculator_SqlInSingle;
import com.f1.utils.sql.DerivedCellCalculator_SqlInnerSelect;
import com.f1.utils.sql.DerivedCellCalculator_SqlInnerSelectSingle;
import com.f1.utils.sql.SqlDerivedCellParser;
import com.f1.utils.sql.SqlPlanListener;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.sql.SqlProcessorTableMutator;
import com.f1.utils.sql.SqlProcessorTableMutatorImpl;
import com.f1.utils.sql.SqlProjector.TempIndex;
import com.f1.utils.sql.TableReturn;
import com.f1.utils.sql.Tableset;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.MethodNode;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.string.node.VariableNode;
import com.f1.utils.structs.LongSet;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.ColumnPositionMapping;
import com.f1.utils.structs.table.ColumnPositionMappingImpl;
import com.f1.utils.structs.table.columnar.ColumnarRow;
import com.f1.utils.structs.table.columnar.ColumnarRowFactory;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.BasicMethodFactory;
import com.f1.utils.structs.table.derived.DeclaredMethodFactory;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorConst;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorMath;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorRef;
import com.f1.utils.structs.table.derived.DerivedCellMemberMethod;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.derived.MethodFactory;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiCenterSqlProcessorMutator implements SqlProcessorTableMutator {

	private static final Logger log = LH.get();
	public static final String OPTION_PERSIST_ENGINE = "PersistEngine";
	public static final String OPTION_PERSIST_OPTIONS = "PersistOptions";
	public static final String OPTION_ON_UNDEF_COLUMN = "OnUndefColumn";
	public static final String OPTION_BROADCAST = "Broadcast";
	public static final String OPTION_NOBROADCAST = "NoBroadcast";
	public static final String OPTION_REFRESH_PERIOD_MS = "RefreshPeriodMs";
	public static final String OPTION_PERSIST_PREFIX = "persist_";
	public static final String OPTION_CONSTRAINT = "Constraint";
	public static final String OPTION_AUTOGEN = "AutoGen";
	public static final String OPTION_INITIAL_CAPACITY = "InitialCapacity";

	private static final String MODIFY_FAILED = "Security Violation: Can not directly modify SYSTEM table contents";
	final private SqlProcessorTableMutatorImpl inner;

	public AmiCenterSqlProcessorMutator(SqlProcessor processor) {
		inner = new SqlProcessorTableMutatorImpl(processor);
	}

	private SqlDerivedCellParser getParser() {
		return this.inner.getOwner().getParser();
	}
	//	private Map<String, Object> evaluateUseOptionsMap(Map<String, Node> options, DerivedCellParserContext context, SqlDerivedCellParser dcp) {
	private Map<String, Object> evaluateOptionsToMap(Map<String, Node> options, CalcFrameStack sf) {
		if (CH.isEmpty(options))
			return Collections.EMPTY_MAP;
		HashMap<String, Object> r = new HashMap<String, Object>(options.size());
		for (Entry<String, Node> s : options.entrySet()) {
			Node useNode = s.getValue();
			Object value = AmiUtils.getNodeValue(useNode, this.getParser(), sf);
			r.put(s.getKey(), value);
		}
		return r;
	}

	private Map<String, String> toStringMap2(Map<String, Object> optionsValues) {
		if (CH.isEmpty(optionsValues))
			return Collections.EMPTY_MAP;
		HashMap<String, String> r = new HashMap<String, String>(optionsValues.size());
		for (Entry<String, Object> s : optionsValues.entrySet()) {
			String value = Caster_String.INSTANCE.cast(s.getValue());
			r.put(s.getKey(), value);
		}
		return r;
	}

	@Override
	public Table processTableAdd(CalcFrameStack sf, String name, int namePos, String[] types, String[] names, Map<String, Node>[] colOptions, int[] colDefPos,
			Map<String, Node> useOptions, int scope, boolean ifNotExists) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		if (scope == SqlExpressionParser.ID_TEMPORARY || scope == SqlExpressionParser.ID_INVALID)
			return inner.processTableAdd(sf, name, namePos, types, names, colOptions, colDefPos, useOptions, scope, ifNotExists);
		if (session.getHdb().getTablesSorted().contains(name) || session.getImdb().getTableNoThrow(name) != null) {
			if (ifNotExists)
				return null;
			throw new ExpressionParserException(namePos, "Table already exists: " + name);
		}
		if (AmiUtils.isResevedTableName(name))
			throw new ExpressionParserException(namePos, "Security Violation: Table name is reserved: " + name);

		session.assertCanAlter();
		AmiCenterState state = session.getImdb().getState();
		long refreshPeriodMs = state.getDefaultTableRefreshPeriodMs();
		boolean broadcast = true;
		List<String> columnNames = new ArrayList<String>();
		Set<String> columnNamesSet = new HashSet<String>();
		List<Byte> columnTypes = new ArrayList<Byte>();
		String persisterName = null;
		byte onUndefColumn = AmiTableDef.ON_UNDEFINED_COLUMN_REJECT;
		int persisterOptionsPos = 0;
		int initialCapacity = 100;
		Map<String, Node> persisterOptions = null;
		Map<String, String> persisterOptionsAsString = null;
		if (useOptions != null) {
			Node pe = useOptions.get(OPTION_PERSIST_ENGINE);
			if (pe != null) {
				Object peVal = AmiUtils.getNodeValue(pe, this.getParser(), sf);
				persisterName = peVal.toString().toUpperCase();
				if ("historical".equalsIgnoreCase(persisterName))
					persisterName = "HISTORICAL";
				else if (SH.isnt(persisterName))
					persisterName = null;
				else if (!session.getImdb().getTablePersisterTypes().contains(persisterName))
					throw new ExpressionParserException(pe.getPosition(),
							"Unknown PersistEngine: '" + persisterName + "' registered persisters include: " + SH.join(",", session.getImdb().getTablePersisterTypes()));
			}
			//TODO: Change hdb processTableAdd?
			if ("HISTORICAL".equals(persisterName))
				return session.getHdb().processTableAdd(sf.getFactory(), name, namePos, types, names, colOptions, colDefPos, useOptions, scope, ifNotExists, sf);
			for (Entry<String, Node> s : useOptions.entrySet()) {
				String key = s.getKey();
				Node useNode = s.getValue();

				Object value = AmiUtils.getNodeValue(useNode, this.getParser(), sf);
				if (value == null)
					throw new ExpressionParserException(s.getValue().getPosition(), "Invalid value, must be non-null for key: " + key);

				else if (OPTION_REFRESH_PERIOD_MS.equalsIgnoreCase(key)) {
					Long t = Caster_Long.INSTANCE.cast(value, false, false);
					if (t == null || t < 0L)
						throw new ExpressionParserException(s.getValue().getPosition(), "Invalid value, must be number greater than 0: " + value);
					refreshPeriodMs = t;
				} else if (OPTION_BROADCAST.equalsIgnoreCase(key)) {
					Boolean t = Caster_Boolean.INSTANCE.cast(value, false, false);
					if (t == null)
						throw new ExpressionParserException(s.getValue().getPosition(), "Invalid value, must be true or false: " + value);
					broadcast = t;
				} else if (OPTION_NOBROADCAST.equalsIgnoreCase(key)) {
					Boolean t = Caster_Boolean.INSTANCE.cast(value, false, false);
					if (t == null)
						throw new ExpressionParserException(s.getValue().getPosition(), "Invalid value, must be true or false: " + value);
					broadcast = !t;
				} else if (OPTION_PERSIST_OPTIONS.equalsIgnoreCase(key) && SH.isnt(value)) {
					//legacy,skip this
				} else if (OPTION_ON_UNDEF_COLUMN.equalsIgnoreCase(key)) {
					String t = value.toString().toUpperCase();
					onUndefColumn = AmiTableUtils.parseOnUndefColType(t);
					if (onUndefColumn == -1)
						throw new ExpressionParserException(s.getValue().getPosition(), "Unknown OnUndefColumn: '" + t + "', should be either REJECT, IGNORE or ADD");
				} else if (OPTION_PERSIST_ENGINE.equalsIgnoreCase(key)) {
				} else if (OPTION_INITIAL_CAPACITY.equalsIgnoreCase(key)) {
					String t = value.toString().toUpperCase();
					try {
						initialCapacity = SH.parseInt(t);
					} catch (Exception e) {
						throw new ExpressionParserException(s.getValue().getPosition(), OPTION_INITIAL_CAPACITY + " is invalid number", e);
					}
					if (initialCapacity < 1)
						throw new ExpressionParserException(s.getValue().getPosition(), OPTION_INITIAL_CAPACITY + " must be positive number: " + initialCapacity);

				} else if (SH.startsWithIgnoreCase(key, OPTION_PERSIST_PREFIX)) {
					if (persisterOptions == null) {
						persisterOptions = new HasherMap<String, Node>();
						persisterOptionsAsString = new HasherMap<String, String>();
					}
					persisterOptionsAsString.put(key, value.toString());
					persisterOptions.put(key, s.getValue());
				} else
					throw new ExpressionParserException(s.getValue().getPosition(),
							"Unknown USE option '" + key + "', valid values are: RefreshPeriodMs, Broadcast, PersistEngine, OnUndefColumn,persist_<custom>");

			}
		}
		List<Map<String, String>> columnOptions = new ArrayList<Map<String, String>>();
		for (int i = 0; i < types.length; i++) {
			String colname = names[i];
			//			if (!AmiUtils.isValidVariableName(colname, false, false))
			//				throw new ExpressionParserException(colDefPos[i], "Invalid column name: " + colname);
			Map<String, Object> colOptionsMap = evaluateOptionsToMap(colOptions[i], sf);
			columnNames.add(colname);
			columnOptions.add(toStringMap2(colOptionsMap));

			if (!columnNamesSet.add(colname))
				throw new ExpressionParserException(colDefPos[i], "Duplicate column: " + colname);
			if (AmiConsts.TYPE_NAME_ENUM.equalsIgnoreCase(types[i])) {
				columnTypes.add(AmiTable.TYPE_ENUM);
			} else {
				byte typeForClass = parseType(sf.getFactory(), colDefPos[i], types[i]);
				if (typeForClass == AmiDatasourceColumn.TYPE_UNKNOWN)
					throw new ExpressionParserException(colDefPos[i], "Unsupported type for " + colname + ": " + types[i]);
				columnTypes.add(typeForClass);
			}
		}
		AmiTableDef tableDef = new AmiTableDef(getDefinedBy(sf), name, columnNames, columnTypes, columnOptions, refreshPeriodMs, initialCapacity, onUndefColumn, persisterName);

		tableDef.setNeverBroadcast(!broadcast);
		AmiTableImpl r;
		try {
			r = new AmiTableImpl(session.getImdb(), tableDef);
		} catch (Exception e) {
			throw new ExpressionParserException(colDefPos[0], "Could not create table: " + e.getMessage(), e);
		}
		if (persisterName != null) {
			if (persisterOptions == null) {
				persisterOptions = Collections.EMPTY_MAP;
				persisterOptionsAsString = Collections.EMPTY_MAP;
			}
			AmiTablePersisterFactory tablePersisterFactory = session.getImdb().getTablePersisterFactory(persisterName);
			// TODO  
			Map<String, Object> optionsMap = AmiUtils.processOptions(persisterOptionsPos, persisterOptions, tablePersisterFactory, this.getParser(), sf, true);
			AmiTablePersister p = tablePersisterFactory.newPersister(optionsMap);
			r.setPersister(new AmiTablePersisterBindingImpl(p, persisterName, optionsMap, persisterOptionsAsString, AmiTableUtils.DEFTYPE_USER));
		}
		session.getObjectsManager().addAmiTable(r, sf);
		return r.getTable();
	}

	@Override
	public Table processTableRemove(CalcFrameStack sf, String name, int tableNamePos, int scope, boolean ifExists) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		Table t = ifExists ? getTableIfExists(sf, name, scope) : getTable(sf, tableNamePos, name, scope);
		if (t instanceof AmiHdbTableRep) {
			session.assertCanAlter();
			session.getHdb().processTableRemove((AmiHdbTableRep) t, tableNamePos, sf);
			return t;
		}
		AmiTableImpl table = getAmiTable(t);
		if (table == null) {
			Table r = inner.processTableRemove(sf, name, tableNamePos, scope, ifExists);
			if (r == null && !ifExists) {
				if (session.getImdb().getAmiTable(name) != null)
					throw new ExpressionParserException(tableNamePos, "Table is PUBLIC, use DROP PUBLIC TABLE instead: " + name);
				throw new ExpressionParserException(tableNamePos, "Table not found: " + name);
			}
			return r;
		}
		session.assertCanAlter();
		ensurePermissions(sf, tableNamePos, table.getDefType());
		if (table.getTriggersCount() != 0)
			throw new ExpressionParserException(tableNamePos, "You must drop all depending triggers first, Ex: DROP TRIGGER " + table.getTriggerAt(0).getTriggerName());
		AmiTableImpl r2 = session.getObjectsManager().removeAmiTable(name, sf);
		return r2.getTable();
	}
	@Override
	public Table processTableRename(CalcFrameStack sf, int fromPos, String from, int toPos, String to, int scope) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		Table t = getTable(sf, fromPos, from, scope);
		if (t instanceof AmiHdbTableRep) {
			session.assertCanAlter();
			if (session.getImdb().getAmiTable(to) != null || session.getHdb().getTablesSorted().contains(to))
				throw new ExpressionParserException(fromPos, "Table already exists: " + to);
			session.getHdb().renameTable((AmiHdbTableRep) t, to, toPos, sf);
			return t;
		}
		AmiTableImpl table = getAmiTable(t);
		if (table == null)
			return this.inner.processTableRename(sf, fromPos, from, toPos, to, scope);
		session.assertCanAlter();
		if (session.getImdb().getAmiTable(to) != null || session.getHdb().getTablesSorted().contains(to))
			throw new ExpressionParserException(fromPos, "Table already exists: " + to);
		//		if (table == null) {
		//			if (tablesMap.getTableNames().contains(from))
		//				throw new ExpressionParserException(fromPos, "Table is not PUBLIC, use RENAME TABLE instead: " + from);
		//			throw new ExpressionParserException(fromPos, "Table not found: " + from);
		//		}
		session.getObjectsManager().renameTable(table, to, sf);
		return table.getTable();
	}

	@Override
	public TableReturn processRowAdds(CalcFrameStack sf, Table table, int tableNamePos, int positions[], Object[][] rows, boolean returnGeneratedIds) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		AmiTableImpl t = getAmiTable(table);
		if (t == null) {
			return inner.processRowAdds(sf, table, tableNamePos, positions, rows, returnGeneratedIds);
		}
		session.assertCanWrite();
		assertIsUser(t);
		AmiPreparedRow pr = t.createAmiPreparedRow();
		if (t.hasReservedColumns())
			positions = removeReservedPositions(t, positions, AmiColumnImpl.CAN_INSERT);
		t.getTable().ensureCapacity(rows.length + t.getRowsCount());
		int r = 0;
		if (returnGeneratedIds) {
			r = t.getRowsCount();
			AmiIndexImpl pi = t.getPrimaryIndex();
			List<Object> keys = new ArrayList<Object>();
			for (Object[] row : rows) {
				pr.reset();
				for (int i : positions)
					pr.putAt(i, row[i]);
				AmiRowImpl row2 = t.insertAmiRow(pr, true, true, sf);
				if (row2 != null) {
					if (pi != null)
						keys.add(pi.getColumn(0).getComparable(row2));
					else if (t.getReservedColumnId() != null)
						keys.add(t.getReservedColumnId().getComparable(row2));
					else
						keys.add(row2.getAmiId());
				}
			}
			r = t.getRowsCount() - r;
			return new TableReturn(r, keys);
		} else {
			for (Object[] row : rows) {
				pr.reset();
				for (int i : positions)
					pr.putAt(i, row[i]);
				if (t.insertAmiRow(pr, sf) != null)
					r++;
			}
			return new TableReturn(r);
		}
	}
	private AmiTableImpl getAmiTable(Table table) {
		if (table instanceof ColumnarTable) {
			ColumnarRowFactory rf = ((ColumnarTable) table).getRowFactory();
			return rf instanceof AmiTableImpl ? (AmiTableImpl) rf : null;
		}
		return null;
		//		AmiTableImpl r = (AmiTableImpl) db.getAmiTable(table.getTitle());
		//		return (r == null || r.getTable() != table) ? null : r;
	}

	@Override
	public int processRowRemoves(CalcFrameStack sf, Table table, List<Row> toDelete) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		AmiTableImpl t = getAmiTable(table);
		if (t == null)
			return inner.processRowRemoves(sf, table, toDelete);
		session.assertCanWrite();
		assertIsUser(t);
		for (int i = 0, l = toDelete.size(); i < l; i++) {
			if (i > 0 && i % 1000 == 0)
				LH.info(log, "Processing Large Remove on ", t.getName(), ": ", i, " / ", toDelete.size(), " complete");
			AmiRow row = (AmiRow) toDelete.get(i);
			t.removeAmiRow(row, sf);
		}
		return toDelete.size();
	}

	private void assertIsUser(AmiTable t) {
		if (!AmiTableUtils.isUserDefined(t.getDefType()))
			throw new ExpressionParserException(0, MODIFY_FAILED);

	}

	@Override
	public int processRowRemoveAll(CalcFrameStack sf, Table table) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		AmiTableImpl t = getAmiTable(table);
		if (t == null)
			return inner.processRowRemoveAll(sf, table);
		session.assertCanWrite();
		assertIsUser(t);
		int r = t.getTable().getSize();
		t.clearRows(sf);
		return r;
	}

	@Override
	public int processRowUpdate(CalcFrameStack sf, Table table, int tableNamePos, List<Row> toUpdate, int[] positions, Object[][] values) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		AmiTableImpl t = getAmiTable(table);
		if (t == null)
			return inner.processRowUpdate(sf, table, tableNamePos, toUpdate, positions, values);
		session.assertCanWrite();
		assertIsUser(t);
		if (t.hasReservedColumns())
			positions = flagReservedPositions(t, positions, AmiColumnImpl.CAN_UPDATE);
		AmiPreparedRow pr = t.createAmiPreparedRow();
		int changes = 0;
		for (int n = 0, l = toUpdate.size(); n < l; n++) {
			AmiRowImpl amirow = (AmiRowImpl) toUpdate.get(n);
			pr.reset();
			Object[] v = values[n];
			for (int i = 0; i < v.length; i++)
				if (positions[i] != -1)
					pr.putAt(positions[i], v[i]);
			if (t.updateAmiRow(amirow.getAmiId(), pr, sf) != null)
				changes++;
		}
		return changes;
	}
	private int[] removeReservedPositions(AmiTableImpl t, int[] positions, byte flag) {
		int toRemove = 0;
		for (int position : positions)
			if (!MH.anyBits(t.getColumnAt(position).getReservedType(), flag))
				toRemove++;
		if (toRemove == 0)
			return positions;
		int[] positions2 = new int[positions.length - toRemove];
		int i = 0;
		for (int position : positions)
			if (MH.anyBits(t.getColumnAt(position).getReservedType(), flag))
				positions2[i++] = position;
		return positions2;
	}
	private int[] flagReservedPositions(AmiTableImpl t, int[] positions, byte flag) {
		int toRemove = 0;
		for (int position : positions)
			if (!MH.anyBits(t.getColumnAt(position).getReservedType(), flag))
				toRemove++;
		if (toRemove == 0)
			return positions;
		int[] positions2 = new int[positions.length];
		int i = 0;
		for (int position : positions)
			if (MH.anyBits(t.getColumnAt(position).getReservedType(), flag))
				positions2[i++] = position;
			else
				positions2[i++] = -1;
		return positions2;
	}
	private ColumnPositionMapping removeReservedPositions(AmiTableImpl t, ColumnPositionMapping posMapping, byte flag) {
		int toRemove = 0;
		int posCount = posMapping.getPosCount();
		for (int n = 0; n < posCount; n++) {
			if (!MH.anyBits(t.getColumnAt(posMapping.getTargetPosAt(n)).getReservedType(), flag))
				toRemove++;
		}
		if (toRemove == 0)
			return posMapping;
		int[] targetPositions = new int[posCount - toRemove];
		int[] sourcePositions = new int[posCount - toRemove];
		int i = 0;
		for (int n = 0; n < posCount; n++) {
			int targetPosition = posMapping.getTargetPosAt(n);
			if (MH.anyBits(t.getColumnAt(targetPosition).getReservedType(), flag)) {
				targetPositions[i] = targetPosition;
				sourcePositions[i] = posMapping.getSourcePosAt(n);
				i++;
			}
		}
		return ColumnPositionMappingImpl.GET(sourcePositions, targetPositions);
	}

	@Override
	public void processIndexCreate(CalcFrameStack sf, String idxName, int idxNamePos, String tableName, int tableNamePos, String[] colNames, String[] colTypes, int[] colPos,
			Map<String, Node> useOptions, boolean ifNotExists, int scope) {
		if (scope == SqlExpressionParser.ID_VARIABLE) {
			this.inner.processIndexCreate(sf, idxName, idxNamePos, tableName, tableNamePos, colNames, colTypes, colPos, useOptions, ifNotExists, scope);
			return;
		}
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		session.assertCanAlter();
		if (AmiUtils.isResevedTableName(idxName))
			throw new ExpressionParserException(idxNamePos, "Security Violation: Index name is reserved: " + idxName);

		Table table = getTable(sf, tableNamePos, tableName, scope);
		if (colNames.length == 0)
			throw new ExpressionParserException(tableNamePos, "Empty column set");
		List<Byte> sorted = new ArrayList<Byte>(colNames.length);
		List<String> columns = new ArrayList<String>(colNames.length);
		for (int i = 0; i < colNames.length; i++) {
			String colName = colNames[i];
			String colType = colTypes[i];
			if (AH.indexOf(colName, colNames) != i)
				throw new ExpressionParserException(colPos[i], "Duplicate column reference: " + colName);
			Column col = table.getColumnsMap().get(colName);
			if (col == null)
				throw new ExpressionParserException(colPos[i], "Column not found for table " + tableName + ": " + colName);
			byte type = colType == null ? AmiIndex.TYPE_SORT : AmiTableUtils.parseIndexType(colType);
			if (type == -1)
				throw new ExpressionParserException(colPos[i], "Unknown index Type: " + colType + ": (valid types include: SORT,HASH,SERIES)");
			sorted.add(type);
			columns.add(colName);
		}
		if (table instanceof AmiHdbTableRep) {
			if (CH.isntEmpty(useOptions))
				throw new ExpressionParserException(idxNamePos, "INDEX on HISTORICAL table does not support USE option: " + CH.first(useOptions.entrySet()));
			if (colNames.length != 1)
				throw new ExpressionParserException(idxNamePos, "INDEX on HISTORICAL table requires exactly one column");
			String type = colTypes[0];
			if (SH.is(type) && !SH.equalsIgnoreCase("SORT", type))
				throw new ExpressionParserException(idxNamePos, "INDEX on HISTORICAL table only supports SORT");
			session.getHdb().addIndex((AmiHdbTableRep) table, getDefinedBy(sf), idxName, idxNamePos, columns.get(0), ifNotExists, sf);
		} else {
			AmiTableImpl amiTable = getAmiTable(table);
			if (amiTable == null)
				throw new ExpressionParserException(idxNamePos, "Can only create index on PUBLIC tables: " + idxName);
			ensureNotSystem(tableNamePos, amiTable);
			if (amiTable.getAmiIndexNoThrow(idxName) != null) {
				if (ifNotExists)
					return;
				throw new ExpressionParserException(idxNamePos, "Index already exists on table: " + idxName);
			}
			byte constraint = AmiIndex.CONSTRAINT_TYPE_NONE;
			byte autogen = AmiIndex.AUTOGEN_NONE;
			if (useOptions != null) {
				for (Entry<String, Node> s : useOptions.entrySet()) {
					String key = s.getKey();
					Object sval = AmiUtils.getNodeValue(s.getValue(), this.getParser(), sf);
					if (OPTION_CONSTRAINT.equalsIgnoreCase(key)) {
						String t = Caster_String.INSTANCE.cast(sval, false, false);
						constraint = AmiTableUtils.parseIndexConstraintType(t);
						if (constraint == -1)
							throw new ExpressionParserException(s.getValue().getPosition(), "Invalid value, must be NONE, UNIQUE or PRIMARY: " + sval);
					} else if (OPTION_AUTOGEN.equalsIgnoreCase(key)) {
						String t = Caster_String.INSTANCE.cast(sval, false, false);
						autogen = AmiTableUtils.parseIndexAutoGenType(t);
						if (autogen == -1)
							throw new ExpressionParserException(s.getValue().getPosition(), "Invalid value, must be NONE, INC or RAND: " + sval);
					} else
						throw new ExpressionParserException(s.getValue().getPosition(), "Unknown USE option, valid values are: Constraint");
				}
			}
			try {
				StringBuilder sink = new StringBuilder();
				if (!amiTable.addIndex(getDefinedBy(sf), idxName, columns, sorted, constraint, sink, autogen, sf))
					throw new ExpressionParserException(colPos[0], sink.toString());
			} catch (ExpressionParserException e) {
				throw e;
			} catch (Exception e) {
				throw new ExpressionParserException(idxNamePos, e.getMessage(), e);
			}
		}
		onSchemaChanged();
	}
	@Override
	public void processIndexRemove(CalcFrameStack sf, String tableName, int tableNamePos, String indexName, int indexNamePos, boolean ifExists, int scope) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		session.assertCanAlter();
		Table t = getTableIfExists(sf, tableName, scope);
		if (t == null) {
			if (ifExists)
				return;
			throw new ExpressionParserException(tableNamePos, "Table not found: " + tableName);
		}
		if (t instanceof AmiHdbTableRep) {
			//TODO: ensurePermissions(indexNamePos, index.getDefType(), tablesMap);
			session.getHdb().removeIndex((AmiHdbTableRep) t, indexName, indexNamePos, ifExists, sf);
			return;
		} else {
			AmiTable table = getAmiTable(t);
			ensureNotSystem(tableNamePos, table);
			AmiIndex index = table.getAmiIndexNoThrow(indexName);
			if (index == null) {
				if (ifExists)
					return;
				throw new ExpressionParserException(indexNamePos, "Index not found: " + indexName);
			}
			ensurePermissions(sf, indexNamePos, index.getDefType());
			table.removeIndex(indexName, sf);
		}
		onSchemaChanged();
	}

	private void onSchemaChanged() {
	}
	public byte getDefinedBy(CalcFrameStack sf) {
		return ((AmiImdbSession) sf.getTableset()).getDefinedBy();
	}
	@Override
	public List<Row> applyIndexes(CalcFrameStack sf, String asTableName, Table table, Pointer<DerivedCellCalculator> pWhereClause, int limit) {
		final SqlPlanListener planListener = sf.getSqlPlanListener();
		final AmiTableImpl amiTable = getAmiTable(table);
		DerivedCellCalculator whereClause = pWhereClause.get();
		if (amiTable == null)
			return this.inner.applyIndexes(sf, asTableName, table, pWhereClause, limit);
		if (amiTable.getIndexes().isEmpty() || table.getRows().isEmpty())
			return table.getRows();
		if (whereClause.isConst()) {
			Object constValue = whereClause.get(null);
			if (Boolean.TRUE.equals(constValue))
				return table.getRows();
			else
				return Collections.EMPTY_LIST;
		}
		final Or sink = new Or();
		if (!reduce(sf, whereClause, sink, table.getColumnIds()))
			return table.getRows();
		final List<DerivedCellCalculator[]> ands = sink.ands;
		if (ands.size() == 0)
			return table.getRows();
		AmiPreparedQueryImpl indexes[] = new AmiPreparedQueryImpl[ands.size()];
		for (int i = 0; i < indexes.length; i++) {
			DerivedCellCalculator[] dcc = ands.get(i);
			AmiPreparedQueryImpl index = findIndex(sf, dcc, amiTable, asTableName);
			if (index == null || index.getIndex() == null)
				return table.getRows();
			indexes[i] = index;
		}
		pWhereClause.put(new DerivedCellCalculatorConst(0, Boolean.TRUE, Boolean.class));
		if (indexes.length == 1) {
			List<AmiRow> r = new ArrayList<AmiRow>();
			AmiPreparedQueryImpl idx = indexes[0];
			amiTable.query(indexes[0], limit, r);
			if (planListener != null)
				planListener.onStep("AMI_INDEX", idx.getAmiTable().getName() + "::" + idx.getIndex().getName() + " reduced " + amiTable.getRowsCount() + " to " + r.size()
						+ " row(s): " + idx.findersToString(new StringBuilder()));
			return (List) r;
		} else {
			LongSet existing = new LongSet();
			List<AmiRow> r = new ArrayList<AmiRow>();
			List<AmiRow> t = new ArrayList<AmiRow>();
			for (int i = 0; i < indexes.length; i++) {
				t.clear();
				AmiPreparedQueryImpl idx = indexes[i];//TODO: create AmiPreparedQuery isntance that takes in 'existing' list and filters out directly, instead of getting all values and looping below
				amiTable.query(idx, Integer.MAX_VALUE, t);
				int rowCount = 0;
				for (int j = 0; j < t.size(); j++) {
					AmiRow row = t.get(j);
					if (existing.add(row.getAmiId())) {
						rowCount++;
						r.add(row);
					}
				}
				if (planListener != null)
					planListener.onStep("AMI_INDEX",
							idx.getAmiTable().getName() + "::" + idx.getIndex().getName() + " found  " + rowCount + " unique row(s): " + idx.findersToString(new StringBuilder()));
			}
			if (planListener != null)
				planListener.onStep("AMI_INDEX", indexes.length + " indexes reduced " + amiTable.getRowsCount() + " to " + r.size() + " row(s)");
			return (List) r;
		}
	}

	private AmiPreparedQueryImpl findIndex(CalcFrameStack sf, DerivedCellCalculator[] dccs, AmiTableImpl amiTable, String asTableName) {
		AmiPreparedQueryImpl pq = new AmiPreparedQueryImpl(amiTable);
		for (DerivedCellCalculator dcc : dccs) {
			if (dcc instanceof DerivedCellCalculatorMath) {
				DerivedCellCalculatorMath math = (DerivedCellCalculatorMath) dcc;
				if (!(math.getLeft() instanceof DerivedCellCalculatorRef))
					pq.addExpression(math);
				else if (!math.getRight().isConst())
					pq.addExpression(math);
				else {
					String colName = SH.toString(((DerivedCellCalculatorRef) math.getLeft()).getId());
					Comparable value = (Comparable) (math.getRight()).get(null);
					AmiColumnImpl<?> column = getColumn(asTableName, amiTable, colName);
					byte op = math.getOperationNodeCode();
					byte opcode = getOperation(math);
					if (opcode != -1) {
						AmiPreparedQueryCompareClause t = pq.addCompare(column, opcode);
						t.setValue(value);
					} else {
						TextMatcher p = DerivedCellCalculatorMath.toPattern(OH.toString(value), op, false);
						if (p == null)
							pq.addExpression(math);
						AmiPreparedQueryMatcherClause t = pq.addMatcher(column);
						t.setMatcher(p);
					}
				}
			} else if (dcc instanceof DerivedCellCalculator_SqlInSingle) {
				DerivedCellCalculator_SqlInSingle in = (DerivedCellCalculator_SqlInSingle) dcc;
				if (!(in.getLeft() instanceof DerivedCellCalculatorRef))
					return null;
				DerivedCellCalculatorRef ref = (DerivedCellCalculatorRef) in.getLeft();
				String colName = SH.toString(((DerivedCellCalculatorRef) ref).getId());
				AmiColumnImpl<?> column = getColumn(asTableName, amiTable, colName);
				Caster<?> caster = column.getColumn().getTypeCaster();
				AmiPreparedQueryInClause t = pq.addIn(column);
				Set<Comparable> values = new HashSet<Comparable>();
				for (DerivedCellCalculator row : in.getInValues()) {
					Object val = row.get(sf);
					if (val == null)
						values.add(null);
					else {
						val = caster.cast(val, false, false);
						if (val != null)
							values.add((Comparable) val);
					}
				}
				t.setValues(values);
			} else if (dcc instanceof DerivedCellCalculator_SqlInnerSelectSingle) {
				DerivedCellCalculator_SqlInnerSelectSingle in = (DerivedCellCalculator_SqlInnerSelectSingle) dcc;
				boolean needsGet = true;
				if (!(in.getLeft() instanceof DerivedCellCalculatorRef))
					return null;
				DerivedCellCalculatorRef ref = (DerivedCellCalculatorRef) in.getLeft();
				String colName = SH.toString(((DerivedCellCalculatorRef) ref).getId());
				AmiColumnImpl<?> column = getColumn(asTableName, amiTable, colName);
				Caster<?> caster = column.getColumn().getTypeCaster();
				AmiPreparedQueryInClause t = pq.addIn(column);
				Set<Comparable> values = new HashSet<Comparable>();
				if (needsGet) {
					in.evaluateInner(sf);
					needsGet = false;
				}
				for (Object val : in.getInValues()) {
					if (val == null)
						values.add(null);
					else {
						val = caster.cast(val, false, false);
						if (val != null)
							values.add((Comparable) val);
					}
				}
				t.setValues(values);
			} else if (dcc instanceof DerivedCellCalculator_SqlIn) {
				DerivedCellCalculator_SqlIn in = (DerivedCellCalculator_SqlIn) dcc;
				for (int n = 0; n < in.getLeft().length; n++) {
					if (!(in.getLeft()[n] instanceof DerivedCellCalculatorRef))
						return null;
					DerivedCellCalculatorRef ref = (DerivedCellCalculatorRef) in.getLeft()[n];
					String colName = SH.toString(((DerivedCellCalculatorRef) ref).getId());
					AmiColumnImpl<?> column = getColumn(asTableName, amiTable, colName);
					Caster<?> caster = column.getColumn().getTypeCaster();
					AmiPreparedQueryInClause t = pq.addIn(column);
					Set<Comparable> values = new HashSet<Comparable>();
					for (DerivedCellCalculator[] row : in.getInValues()) {
						Object val = row[n].get(sf);
						if (val == null)
							values.add(null);
						else {
							val = caster.cast(val, false, false);
							if (val != null)
								values.add((Comparable) val);
						}
					}
					t.setValues(values);
				}
			} else if (dcc instanceof DerivedCellCalculator_SqlInnerSelect) {
				DerivedCellCalculator_SqlInnerSelect in = (DerivedCellCalculator_SqlInnerSelect) dcc;
				boolean needsGet = true;
				for (int n = 0; n < in.getLeft().length; n++) {
					if (!(in.getLeft()[n] instanceof DerivedCellCalculatorRef))
						return null;
					DerivedCellCalculatorRef ref = (DerivedCellCalculatorRef) in.getLeft()[n];
					String colName = SH.toString(((DerivedCellCalculatorRef) ref).getId());
					AmiColumnImpl<?> column = getColumn(asTableName, amiTable, colName);
					Caster<?> caster = column.getColumn().getTypeCaster();
					AmiPreparedQueryInClause t = pq.addIn(column);
					Set<Comparable> values = new HashSet<Comparable>();
					if (needsGet) {
						in.evaluateInner(sf);
						needsGet = false;
					}
					for (Object[] row : in.getInValues()) {
						Object val = row[n];
						if (val == null)
							values.add(null);
						else {
							val = caster.cast(val, false, false);
							if (val != null)
								values.add((Comparable) val);
						}
					}
					t.setValues(values);
				}
			} else if (!dcc.isConst())
				pq.addExpression(dcc);
		}
		pq.collapseComparesToBetween();
		return pq;
	}
	private AmiColumnImpl<?> getColumn(String asName, AmiTableImpl amiTable, String colName) {
		int i = colName.indexOf('.');
		if (i != -1) {
			String name = amiTable.getName();
			if ((i == name.length() && colName.startsWith(name)) || (asName != null && i == asName.length() && colName.startsWith(asName)))
				colName = colName.substring(i + 1);
		}
		return amiTable.getColumn(colName);
	}

	public static class Or {
		List<DerivedCellCalculator[]> ands = new ArrayList<DerivedCellCalculator[]>();

		public Or add(DerivedCellCalculator[] calc) {
			ands.add(calc);
			return this;
		}
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (DerivedCellCalculator[] s : this.ands) {
				if (sb.length() > 0)
					sb.append(" OR ");
				SH.join("+", s, sb);
			}
			return sb.toString();
		}

		public List<DerivedCellCalculator[]> getAnds() {
			return this.ands;
		}
	}

	private static final boolean SUPPORT_IN_CLAUSE_REDUCTION = false;

	static public boolean reduce(CalcFrameStack sf, DerivedCellCalculator calc, Or sink, Set<String> tableVars) {
		if (calc instanceof DerivedCellCalculatorRef)
			applyConstIfAvailable(sf, ((DerivedCellCalculatorRef) calc), tableVars);
		if (calc instanceof DerivedCellCalculatorMath) {
			final DerivedCellCalculatorMath c = (DerivedCellCalculatorMath) calc;
			final DerivedCellCalculator left = c.getLeft();
			final DerivedCellCalculator rght = c.getRight();
			if (left instanceof DerivedCellCalculatorRef)
				applyConstIfAvailable(sf, (DerivedCellCalculatorRef) left, tableVars);
			if (rght instanceof DerivedCellCalculatorRef)
				applyConstIfAvailable(sf, (DerivedCellCalculatorRef) rght, tableVars);
			final byte op = c.getOperationNodeCode();
			switch (op) {
				case OperationNode.OP_PIPE_PIPE: {
					if (SUPPORT_IN_CLAUSE_REDUCTION) {
						DerivedCellCalculatorRef[] keys = extractKeysForInClause(c);
						if (keys != null)
							return reduce(sf, toInclause(keys, c), sink, tableVars);
					}
					if (!reduce(sf, left, sink, tableVars))
						return false;
					if (!reduce(sf, rght, sink, tableVars))
						return false;
					return true;
				}
				case OperationNode.OP_AMP_AMP: {
					Or leftOrs = new Or();
					Or rghtOrs = new Or();
					if (!reduce(sf, left, leftOrs, tableVars))
						return false;
					if (!reduce(sf, rght, rghtOrs, tableVars))
						return false;
					for (DerivedCellCalculator and1[] : leftOrs.ands)
						for (DerivedCellCalculator and2[] : rghtOrs.ands)
							sink.add(AH.appendArray(and1, and2));
					return true;
				}
				case OperationNode.OP_EQ_TILDE:
				case OperationNode.OP_BANG_TILDE:
				case OperationNode.OP_TILDE_TILDE: {
					if (left instanceof DerivedCellCalculatorRef && rght.isConst()) {
						sink.add(new DerivedCellCalculator[] { c });
						return true;
					}
					sink.add(new DerivedCellCalculator[] { c });
					return true;
				}
			}
			byte flip = flipOperation(op);
			if (flip == -1) {
				sink.add(new DerivedCellCalculator[] { c });
				return true;
			} else if (left instanceof DerivedCellCalculatorRef && rght.isConst()) {
				sink.add(new DerivedCellCalculator[] { c });
				return true;
			} else if (rght instanceof DerivedCellCalculatorRef && left.isConst()) {
				sink.add(new DerivedCellCalculator[] { DerivedCellCalculatorMath.valueOf(calc.getPosition(), flip, rght, left) });
				return true;
			}
			sink.add(new DerivedCellCalculator[] { c });
			return true;
		} else if (calc instanceof DerivedCellCalculatorRef && calc.getReturnType() == Boolean.class) {
			sink.add(new DerivedCellCalculator[] {
					DerivedCellCalculatorMath.valueOf(calc.getPosition(), OperationNode.OP_EQ_EQ, calc, new DerivedCellCalculatorConst(calc.getPosition(), Boolean.TRUE)) });
			return true;
		} else if (calc instanceof DerivedCellCalculator_SqlInSingle) {
			DerivedCellCalculator_SqlInSingle in = (DerivedCellCalculator_SqlInSingle) calc;
			boolean r = true;
			for (DerivedCellCalculator j : in.getInValues()) {
				if (j instanceof DerivedCellCalculatorRef)
					applyConstIfAvailable(sf, ((DerivedCellCalculatorRef) j), tableVars);
				r = r && j.isConst();
			}
			sink.add(new DerivedCellCalculator[] { in });
			return r;
		} else if (calc instanceof DerivedCellCalculator_SqlInnerSelectSingle) {
			DerivedCellCalculator_SqlInnerSelectSingle in = (DerivedCellCalculator_SqlInnerSelectSingle) calc;
			sink.add(new DerivedCellCalculator[] { in });
			return true;
		} else if (calc instanceof DerivedCellCalculator_SqlIn) {
			DerivedCellCalculator_SqlIn in = (DerivedCellCalculator_SqlIn) calc;
			boolean r = true;
			for (DerivedCellCalculator[] i : in.getInValues()) {
				for (DerivedCellCalculator j : i) {
					if (j instanceof DerivedCellCalculatorRef)
						applyConstIfAvailable(sf, ((DerivedCellCalculatorRef) j), tableVars);
					r = r && j.isConst();
				}
			}
			sink.add(new DerivedCellCalculator[] { in });
			return r;
		} else if (calc instanceof DerivedCellCalculator_SqlInnerSelect) {
			DerivedCellCalculator_SqlInnerSelect in = (DerivedCellCalculator_SqlInnerSelect) calc;
			sink.add(new DerivedCellCalculator[] { in });
			return true;
		} else
			return false;
	}
	public static boolean applyConstIfAvailable(CalcFrameStack sf, DerivedCellCalculatorRef ref, Set<String> tableVars) {
		//		Object id = ref.getId();
		//		Frame values = sf.getGlobalVars();
		//		if (values != null && values.getTypes().containsKey((String) id) && (tableVars == null || !tableVars.contains(id))) {
		//			ref.setConst(values.getValue((String) id));
		//			return true;
		//		}
		return false;
	}
	private static DerivedCellCalculator_SqlIn toInclause(DerivedCellCalculatorRef[] keys, DerivedCellCalculatorMath calc) {
		int length = keys.length;
		Class[] types = new Class[length];
		for (int i = 0; i < length; i++)
			types[i] = keys[i].getReturnType();
		List<DerivedCellCalculator[]> values = new ArrayList<DerivedCellCalculator[]>();
		extractValues(calc, length, values);
		DerivedCellCalculator_SqlIn r = new DerivedCellCalculator_SqlIn(calc.getPosition(), keys, AH.toArray(values, DerivedCellCalculator[].class), types);
		return r;
	}

	private static DerivedCellCalculator[] extractValues(DerivedCellCalculator calc, int length, List<DerivedCellCalculator[]> values) {
		DerivedCellCalculatorMath math = (DerivedCellCalculatorMath) calc;
		switch (math.getOperationNodeCode()) {
			case OperationNode.OP_EQ_EQ: {
				final DerivedCellCalculator left = ((DerivedCellCalculatorMath) calc).getLeft();
				final DerivedCellCalculator rght = ((DerivedCellCalculatorMath) calc).getRight();
				return new DerivedCellCalculator[] { (rght.isConst() ? rght : left) };
			}
			case OperationNode.OP_AMP_AMP: {
				return AH.appendArray(extractValues(math.getLeft(), length, values), extractValues(math.getRight(), length, values));
			}
			case OperationNode.OP_PIPE_PIPE: {
				DerivedCellCalculator[] t = extractValues(math.getLeft(), length, values);
				if (t != null)
					values.add(t);
				t = extractValues(math.getRight(), length, values);
				if (t != null)
					values.add(t);
				return null;
			}
		}
		throw new IllegalStateException(calc.toString());
	}
	static public DerivedCellCalculatorRef[] extractKeysForInClause(DerivedCellCalculator calc) {
		if (calc instanceof DerivedCellCalculatorMath) {
			DerivedCellCalculatorMath math = (DerivedCellCalculatorMath) calc;
			switch (math.getOperationNodeCode()) {
				case OperationNode.OP_EQ_EQ: {
					DerivedCellCalculator left = ((DerivedCellCalculatorMath) calc).getLeft();
					DerivedCellCalculator rght = ((DerivedCellCalculatorMath) calc).getRight();
					if (left instanceof DerivedCellCalculatorRef && rght.isConst()) {
						return new DerivedCellCalculatorRef[] { (DerivedCellCalculatorRef) left };
					} else if (rght instanceof DerivedCellCalculatorRef && left.isConst()) {
						return new DerivedCellCalculatorRef[] { (DerivedCellCalculatorRef) rght };
					}
				}
				case OperationNode.OP_AMP_AMP: {
					DerivedCellCalculatorRef[] left = extractKeysForInClause(math.getLeft());
					if (left == null)
						return null;
					DerivedCellCalculatorRef[] rght = extractKeysForInClause(math.getRight());
					if (rght == null)
						return null;
					return AH.appendArray(left, rght);
				}
				case OperationNode.OP_PIPE_PIPE: {
					DerivedCellCalculatorRef[] left = extractKeysForInClause(math.getLeft());
					if (left == null)
						return null;
					DerivedCellCalculatorRef[] rght = extractKeysForInClause(math.getRight());
					if (rght == null)
						return null;
					if (AH.eq(left, rght))
						return left;
				}
			}
		}
		return null;
	}

	private static byte getOperation(DerivedCellCalculatorMath code) {
		byte math = code.getOperationType();
		switch (math) {
			case DerivedCellCalculatorMath.TYPE_STRING_EQ:
			case DerivedCellCalculatorMath.TYPE_MATH_EQ:
			case DerivedCellCalculatorMath.TYPE_BOOL_EQ:
				if (code.getLeft().getReturnType() == Bytes.class || code.getRight().getReturnType() == Bytes.class)
					return -1;
				return AmiQueryFinder_Comparator.EQ;
			case DerivedCellCalculatorMath.TYPE_STRING_NE:
			case DerivedCellCalculatorMath.TYPE_MATH_NE:
			case DerivedCellCalculatorMath.TYPE_BOOL_NE:
				if (code.getLeft().getReturnType() == Bytes.class || code.getRight().getReturnType() == Bytes.class)
					return -1;
				return AmiQueryFinder_Comparator.NE;
			case DerivedCellCalculatorMath.TYPE_STRING_LT:
				return AmiQueryFinder_Comparator.LT;
			case DerivedCellCalculatorMath.TYPE_STRING_GT:
				return AmiQueryFinder_Comparator.GT;
			case DerivedCellCalculatorMath.TYPE_STRING_LE:
				return AmiQueryFinder_Comparator.LE;
			case DerivedCellCalculatorMath.TYPE_STRING_GE:
				return AmiQueryFinder_Comparator.GE;
			case DerivedCellCalculatorMath.TYPE_MATH_LT:
				return AmiQueryFinder_Comparator.LT;
			case DerivedCellCalculatorMath.TYPE_MATH_GT:
				return AmiQueryFinder_Comparator.GT;
			case DerivedCellCalculatorMath.TYPE_MATH_LE:
				return AmiQueryFinder_Comparator.LE;
			case DerivedCellCalculatorMath.TYPE_MATH_GE:
				return AmiQueryFinder_Comparator.GE;
			case DerivedCellCalculatorMath.TYPE_OBJECT_EQ:
				return AmiQueryFinder_Comparator.EQ;
			case DerivedCellCalculatorMath.TYPE_OBJECT_NE:
				return AmiQueryFinder_Comparator.NE;

			default:
				return -1;
		}
	}
	private static byte flipOperation(byte op) {
		switch (op) {
			case OperationNode.OP_EQ_EQ:
				return OperationNode.OP_EQ_EQ;
			case OperationNode.OP_BANG_EQ:
				return OperationNode.OP_BANG_EQ;
			case OperationNode.OP_LT_EQ:
				return OperationNode.OP_GT_EQ;
			case OperationNode.OP_GT_EQ:
				return OperationNode.OP_LT_EQ;
			case OperationNode.OP_LT:
				return OperationNode.OP_GT;
			case OperationNode.OP_GT:
				return OperationNode.OP_LT;
			default:
				return -1;
		}
	}
	@Override
	public void processTriggerCreate(CalcFrameStack sf, String triggerName, int triggerNamePos, String typeName, int typeNamePos, String tableName[], int tableNamePos[],
			int priority, Map<String, Node> useOptions, boolean ifNotExists) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		session.assertCanAlter();
		if (AmiUtils.isResevedTableName(triggerName))
			throw new ExpressionParserException(triggerNamePos, "Security Violation: Trigger name is reserved: " + triggerName);
		typeName = typeName.toUpperCase();
		AmiTriggerFactory factory = session.getImdb().getTriggerFactory(typeName);
		Set<String> visited = new HashSet<String>();
		if (session.getObjectsManager().getAmiTrigger(triggerName) != null) {
			if (ifNotExists)
				return;
			throw new ExpressionParserException(triggerNamePos, "Trigger already exists: " + triggerName);
		}
		for (int i = 0; i < tableName.length; i++) {
			String tname = tableName[i];
			if (!visited.add(tname))
				throw new ExpressionParserException(tableNamePos[i], "Duplicate Table Reference: " + tname);
			AmiTable amiTable = session.getImdb().getAmiTable(tname);
			if (amiTable != null)
				ensureNotSystem(tableNamePos[i], amiTable);
			if (session.getImdb().isStartupComplete()) {
				if (amiTable == null) {
					if (session.getTableNoThrow(tname) != null)
						throw new ExpressionParserException(tableNamePos[i], "Can only create triggers on PUBLIC Tables: " + tname);
					else
						throw new ExpressionParserException(tableNamePos[i], "Table not found: " + tname);
				}
			}
		}
		if (factory == null)
			throw new ExpressionParserException(typeNamePos, "Invalid trigger type '" + typeName + "' , existing trigger types: " + session.getImdb().getTriggerTypes());
		//TODO:
		Map<String, Object> useOptionsMap = AmiUtils.processOptions(typeNamePos, useOptions, factory, this.getParser(), sf, true);
		AmiTrigger trigger = factory.newTrigger();

		AmiTriggerBindingImpl tb = new AmiTriggerBindingImpl(triggerName, trigger, priority, tableName, typeName, useOptionsMap, toStringMap2(useOptionsMap), getDefinedBy(sf));
		if (session.getImdb().isStartupComplete()) {
			try {
				tb.startup(session.getImdb(), sf);
			} catch (Exception e) {
				if (SH.is(e.getMessage()))
					throw new ExpressionParserException(triggerNamePos, "For " + tb.getTriggerType() + ": " + e.getMessage(), e);
				else
					throw new ExpressionParserException(triggerNamePos, "For " + tb.getTriggerType() + ": Invalid trigger configuration", e);
			}
			session.getObjectsManager().addTrigger(tb, sf);
			session.getObjectsManager().bindTriggersToTables(tb, sf);
			session.getImdb().onSchemaChanged(sf);
			try {
				tb.onInitialized(sf);
			} catch (Exception e) {
				if (SH.is(e.getMessage()))
					throw new ExpressionParserException(triggerNamePos, "For " + tb.getTriggerType() + ": " + e.getMessage(), e);
				else
					throw new ExpressionParserException(triggerNamePos, "For " + tb.getTriggerType() + ": Failed onStartup", e);
			}
		} else
			session.getObjectsManager().addTrigger(tb, sf);
	}
	private Map<String, String> toStringMap(Map<String, VariableNode> useOptions) {
		HashMap<String, String> r = new HashMap<String, String>(useOptions.size());
		for (Entry<String, VariableNode> e : useOptions.entrySet())
			r.put(e.getKey(), e.getValue().getVarname());
		return r;
	}
	@Override
	public void processTriggerRemove(CalcFrameStack sf, String tableName, int tableNamePos, String triggerName, int triggerNamePos, boolean ifExists) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		session.assertCanAlter();
		AmiTriggerBindingImpl trigger = session.getObjectsManager().getAmiTriggerBinding(triggerName);
		if (trigger == null) {
			if (ifExists)
				return;
			throw new ExpressionParserException(triggerNamePos, "Trigger not found: " + triggerName);
		}
		ensurePermissions(sf, triggerNamePos, trigger.getDefType());
		session.getObjectsManager().removeAmiTrigger(triggerName, sf);
	}
	private void ensureNotSystem(int tableNamePos, AmiTable table) {
		byte defType = table.getDefType();
		if (defType == AmiTableUtils.DEFTYPE_SYSTEM)
			throw new ExpressionParserException(0, "Security Violation: Can not alter SYSTEM tables");
		if (defType == AmiTableUtils.DEFTYPE_AMI)
			throw new ExpressionParserException(0, "Security Violation: Can not alter AMI tables");

	}
	private void ensurePermissions(CalcFrameStack sf, int position, byte defType) {
		if (defType != getDefinedBy(sf))
			throw new ExpressionParserException(0, "Security Violation: Can not alter tables defined by: " + AmiTableUtils.toStringForDefType(defType));
	}

	@Override
	public void processTimerRemove(CalcFrameStack sf, String timerName, int timerNamePos, boolean ifExists) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		session.assertCanAlter();
		AmiTimerBindingImpl timer = session.getObjectsManager().getAmiTimerBinding(timerName);
		if (timer == null) {
			if (ifExists)
				return;
			throw new ExpressionParserException(timerNamePos, "Timer not found: " + timerName);
		}
		session.getObjectsManager().removeAmiTimer(timerName, sf);
	}

	@Override
	public void processTimerCreate(CalcFrameStack sf, String timerName, int timerNamePos, String typeName, int typeNamePos, int priority, String on, int onPos,
			Map<String, Node> useOptions, boolean ifNotExists) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		session.assertCanAlter();
		if (AmiUtils.isResevedTableName(timerName))
			throw new ExpressionParserException(timerNamePos, "Security Violation: Timer name is reserved: " + timerName);
		typeName = typeName.toUpperCase();
		AmiTimerFactory factory = session.getImdb().getTimerFactory(typeName);
		if (factory == null)
			throw new ExpressionParserException(typeNamePos, "Invalid timer type '" + typeName + "' , existing trigger types: " + session.getImdb().getTimerTypes());
		if (session.getImdb().getAmiTimer(timerName) != null) {
			if (ifNotExists)
				return;
			throw new ExpressionParserException(typeNamePos, "Timer already exists: " + timerName);
		}
		// TODO:
		Map<String, Object> useOptionsMap = AmiUtils.processOptions(typeNamePos, useOptions, factory, this.getParser(), sf, true);
		AmiTimer timer = factory.newTimer();
		AmiTimerBindingImpl tm;
		try {
			tm = new AmiTimerBindingImpl(timerName, timer, priority, on, typeName, useOptionsMap, toStringMap2(useOptionsMap), getDefinedBy(sf));
		} catch (Exception e) {
			throw new ExpressionParserException(onPos, "Timer Definition failed: " + e.getMessage(), e);
		}
		session.getObjectsManager().addAmiTimerBinding(tm, sf);
	}
	@Override
	public void processProcedureCreate(CalcFrameStack sf, String procedureName, int procedureNamePos, String typeName, int typeNamePos, Map<String, Node> useOptions,
			boolean ifNotExists) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		session.assertCanAlter();
		typeName = typeName.toUpperCase();
		if (AmiUtils.isResevedTableName(procedureName))
			throw new ExpressionParserException(procedureNamePos, "Security Violation: Procedure name is reserved: " + procedureName);
		AmiStoredProcFactory factory = session.getImdb().getStoredProcFactory(typeName);
		if (factory == null) {
			throw new ExpressionParserException(typeNamePos, "Invalid procedure type '" + typeName + "' , existing procedure types: " + session.getImdb().getStoredProcTypes());
		}
		if (session.getImdb().getAmiStoredProc(procedureName) != null) {
			if (ifNotExists)
				return;
			throw new ExpressionParserException(typeNamePos, "Procedure already exists: " + procedureName);
		}
		Map<String, Object> useOptionsMap = AmiUtils.processOptions(typeNamePos, useOptions, factory, this.getParser(), sf, true);
		AmiStoredProc sp = factory.newStoredProc();
		AmiStoredProcBindingImpl spb;
		try {
			spb = new AmiStoredProcBindingImpl(session.getImdb(), procedureName, sp, typeName, useOptionsMap, toStringMap2(useOptionsMap), getDefinedBy(sf));
			session.getObjectsManager().addAmiStoredProcBinding(spb, sf);
		} catch (Exception e) {
			throw new ExpressionParserException(typeNamePos, "Procedure Definition failed: " + e.getMessage(), e);
		}
		onSchemaChanged();
	}
	@Override
	public void processProcedureRemove(CalcFrameStack sf, String procedureName, int procedureNamePos, boolean ifExists) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		session.assertCanAlter();
		AmiStoredProcBindingImpl proc = session.getObjectsManager().getAmiStoredProcBinding(procedureName);
		if (proc == null) {
			if (ifExists)
				return;
			throw new ExpressionParserException(procedureNamePos, "Procedure not found: " + procedureName);
		}
		session.getObjectsManager().removeAmiStoredProc(procedureName, sf);
		onSchemaChanged();

	}
	@Override
	public FlowControl processCallProcedure(CalcFrameStack sf, String name, int namePos, Object[] params, int[] paramsPos, int limit, int limitOffset) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		AmiStoredProcBindingImpl proc = session.getObjectsManager().getAmiStoredProcBinding(name);
		session.assertCanRead();
		if (proc == null)
			throw new ExpressionParserException(namePos, "Procedure not found: " + name);
		try {
			AmiImdbSession tablesMap = (AmiImdbSession) sf.getTableset();
			return proc.execute(params, limit, limitOffset, tablesMap.getUsername(), tablesMap, sf);
		} catch (ExpressionParserException e) {
			throw new ExpressionParserException(e.getExpression(), e.getPosition(), "PROCEDURE '" + name + "' FAILED: " + e.getMessage(), e);
		} catch (Exception e) {
			throw new ExpressionParserException(namePos, "PROCEDURE '" + name + "': " + AmiUtils.toMessage(e), e);
		}
	}
	@Override
	public String processDescribe(CalcFrameStack sf, int type, int scope, String name, int namePos, String on, int onPos, String from, int fromPos, MethodNode mn) {
		AmiCenterReplicationCenter centerSource;
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		AmiImdbImpl db = session.getImdb();
		AmiImdbObjectsManager dbo = session.getObjectsManager();

		if (from != null) {
			centerSource = db.getReplicator().getCenter(from);
			if (centerSource == null)
				throw new ExpressionParserException(fromPos, "Center not found: " + from + " (Available Centers: " + SH.join(',', db.getReplicator().getCenterNames()) + ")");
		} else
			centerSource = null;
		StringBuilder r = new StringBuilder();
		AmiSchema systemSchema = db.getSystemSchema();
		session.assertCanRead();
		switch (type) {
			case SqlExpressionParser.ID_DBO: {
				if (centerSource != null)
					throw new ExpressionParserException(fromPos, "FROM not supported");
				if (scope != SqlExpressionParser.ID_INVALID && scope != SqlExpressionParser.ID_PUBLIC)
					throw new ExpressionParserException(namePos, "DBO not found: " + name);
				AmiDboBindingImpl t = dbo.getAmiDboBinding(name);
				if (t == null)
					throw new ExpressionParserException(namePos, "DBO not found: " + name);
				systemSchema.generateCreateSql(t, r);
				break;
			}
			case SqlExpressionParser.ID_TRIGGER: {
				if (centerSource != null)
					throw new ExpressionParserException(fromPos, "FROM not supported");
				if (scope != SqlExpressionParser.ID_INVALID && scope != SqlExpressionParser.ID_PUBLIC)
					throw new ExpressionParserException(namePos, "Trigger not found: " + name);
				AmiTriggerBindingImpl trigger = dbo.getAmiTriggerBinding(name);
				if (trigger == null)
					throw new ExpressionParserException(namePos, "Trigger not found: " + name);
				systemSchema.generateCreateSql(trigger, r);
				break;
			}
			case SqlExpressionParser.ID_METHOD: {//add logic for method
				if (centerSource != null)
					throw new ExpressionParserException(fromPos, "FROM not supported");
				MethodFactoryManager methodFactory = sf.getFactory();
				int[] posArray = new int[mn.getParamsToArray().length];
				for (int i = 0; i < mn.getParamsToArray().length; i++) {
					posArray[i] = mn.getParamAt(i).getPosition();
				}
				systemSchema.generateCreateSql_method(methodFactory, name, namePos, posArray, r);
				break;
			}
			case SqlExpressionParser.ID_INDEX: {
				if (scope != SqlExpressionParser.ID_INVALID && scope != SqlExpressionParser.ID_PUBLIC)
					throw new ExpressionParserException(namePos, "Index not found: " + name);
				if (on == null)
					throw new ExpressionParserException(namePos, "Expecting ON <tablename>");
				if (centerSource != null) {
					if (!AmiCenterReplicationHelper.describeIndex(on, name, centerSource.getIndexSchema(), r))
						throw new ExpressionParserException(namePos, "Index not found on Center '" + centerSource.getCenterName() + "': " + name);
					break;
				}
				AmiTable table = db.getAmiTable(on);
				if (table == null)
					throw new ExpressionParserException(onPos, "Table not found: " + on);
				AmiIndexImpl index = (AmiIndexImpl) table.getAmiIndexNoThrow(name);
				if (index == null)
					throw new ExpressionParserException(namePos, "Index not found: " + name);
				systemSchema.generateCreateSql(index, r);
				break;
			}
			case SqlExpressionParser.ID_PROCEDURE: {
				if (centerSource != null)
					throw new ExpressionParserException(fromPos, "FROM not supported");
				if (scope != SqlExpressionParser.ID_INVALID && scope != SqlExpressionParser.ID_PUBLIC)
					throw new ExpressionParserException(namePos, "Procedure not found: " + name);
				AmiStoredProcBindingImpl proc = dbo.getAmiStoredProcBinding(name);
				if (proc == null)
					throw new ExpressionParserException(namePos, "Procedure not found: " + name);
				systemSchema.generateCreateSql(proc, r);
				break;
			}
			case SqlExpressionParser.ID_TIMER: {
				if (centerSource != null)
					throw new ExpressionParserException(fromPos, "FROM not supported");
				if (scope != SqlExpressionParser.ID_INVALID && scope != SqlExpressionParser.ID_PUBLIC)
					throw new ExpressionParserException(namePos, "Timer not found: " + name);
				AmiTimerBindingImpl timer = dbo.getAmiTimerBinding(name);
				if (timer == null)
					throw new ExpressionParserException(namePos, "Timer not found: " + name);
				systemSchema.generateCreateSql(timer, r);
				break;
			}
			case SqlExpressionParser.ID_TABLE: {
				if (centerSource != null) {
					if (!AmiCenterReplicationHelper.describeTable(name, centerSource.getTableSchema(), centerSource.getColumnSchema(), centerSource.getIndexSchema(), r))
						throw new ExpressionParserException(namePos, "Table not found on Center '" + centerSource.getCenterName() + "': " + name);
					break;
				}
				Table t = getTable(sf, namePos, name, scope);
				if (t instanceof AmiHdbTableRep) {
					AmiHdbSchema_Table ht = session.getHdb().getTableSchema(t.getTitle());
					r.append(ht.getSqlDef());
					break;
				}
				AmiTableImpl table = getAmiTable(t);
				if (table != null) {
					systemSchema.generateCreateSql(table, r);
					for (AmiIndexImpl i : table.getIndexes())
						systemSchema.generateCreateSql(i, r);
					//					for (int i = 0; i < table.getTriggersCount(); i++)
					//						systemSchema.generateCreateSql(table.getTriggerAt(i), r);
				} else {
					return inner.processDescribe(sf, type, scope, name, namePos, on, onPos, from, fromPos, null);
				}
				break;
			}
			default:
				return inner.processDescribe(sf, type, scope, name, namePos, on, onPos, from, fromPos, null);
		}
		return r.toString();
	}
	@Override
	public Table processShow(CalcFrameStack sf, String targetType, int targetTypePos, int scope, boolean isFull, String name, int namePos, String from, int fromPos,
			MethodNode mn) {
		final MethodFactoryManager mf = sf.getFactory();
		final CalcFrame globalVars = sf.getGlobal();
		final AmiImdbSession session = AmiCenterUtils.getSession(sf);
		final AmiImdbImpl db = session.getImdb();
		final AmiHdb hdb = session.getHdb();
		session.assertCanRead();

		AmiCenterReplicationCenter centerSource;

		if (from != null) {
			centerSource = db.getReplicator().getCenter(from);
			if (centerSource == null)
				throw new ExpressionParserException(fromPos, "Center not found: " + from + " (Available Centers: " + SH.join(',', db.getReplicator().getCenterNames()) + ")");
		} else
			centerSource = null;
		if ("TABLES".equalsIgnoreCase(targetType)) {
			if (name != null)
				throw new ExpressionParserException(namePos, "Unexpected token: " + name);
			if (centerSource != null)
				return new BasicTable(centerSource.getTableSchema());
			BasicTable r = new BasicTable();
			r.setTitle("TABLES");
			r.addColumn(String.class, "TableName");
			r.addColumn(Boolean.class, "Broadcast");
			r.addColumn(Long.class, "RefreshPeriodMs");
			r.addColumn(String.class, "PersistEngine");
			r.addColumn(String.class, "OnUndefColumn");
			r.addColumn(String.class, "DefinedBy");
			r.addColumn(String.class, "Scope");
			r.addColumn(Long.class, "RowCount");
			r.addColumn(Integer.class, "ColumnsCount");
			if (scope == SqlExpressionParser.ID_INVALID || scope == SqlExpressionParser.ID_PUBLIC) {
				for (AmiTableImpl t : session.getImdb().getAmiTables()) {
					Table table = t.getTable();
					Row row = r.newEmptyRow();
					r.getRows().add(row);
					row.put("TableName", table.getTitle());
					row.put("Broadcast", t.getIsBroadCast());
					row.put("RefreshPeriodMs", t.getRefreshPeriod());
					row.put("PersistEngine", t.getPersisterTypeName());
					row.put("OnUndefColumn", AmiTableUtils.toStringForOnUndefColType(t.getOnUndefinedColumn()));
					row.put("DefinedBy", AmiTableUtils.toStringForDefType(t.getDefType()));
					row.put("Scope", "PUBLIC");
					row.put("RowCount", (long) table.getSize());
					row.put("ColumnsCount", table.getColumnsCount());
				}
				for (String tableName : hdb.getTablesSorted()) {
					Row row = r.newEmptyRow();
					r.getRows().add(row);
					AmiHdbSchema_Table table = hdb.getTableSchema(tableName);
					row.put("TableName", table.getName());
					row.put("DefinedBy", AmiTableUtils.toStringForDefType(table.getDefType()));
					row.put("PersistEngine", "HISTORICAL");
					row.put("RowCount", hdb.getRowsCount(tableName));
					row.put("ColumnsCount", hdb.getColumnsCount(tableName));
					row.put("Scope", "PUBLIC");
				}
			}
			if (scope == SqlExpressionParser.ID_INVALID || scope == SqlExpressionParser.ID_TEMPORARY)
				if (session.getLocalTableset() != null)
					for (String s : session.getLocalTableset().getTableNamesSorted()) {
						Table table = session.getLocalTableset().getTable(s);
						Row row = r.newEmptyRow();
						r.getRows().add(row);
						row.put("RowCount", (long) table.getSize());
						row.put("ColumnsCount", table.getColumnsCount());
						row.put("TableName", table.getTitle());
						row.put("Scope", "TEMPORARY");
					}
			if (scope == SqlExpressionParser.ID_INVALID || scope == SqlExpressionParser.ID_VARIABLE)
				for (String key : globalVars.getVarKeys()) {
					Object value = globalVars.getValue(key);
					if (value instanceof Table) {
						Table table = (Table) value;
						Row row = r.newEmptyRow();
						r.getRows().add(row);
						row.put("RowCount", (long) table.getSize());
						row.put("ColumnsCount", table.getColumnsCount());
						row.put("TableName", key);
						row.put("Scope", "VARIABLE");
					}
				}
			TableHelper.sort(r, "TableName", "DefinedBy");
			return r;
			//		} else if ("VARS".equalsIgnoreCase(targetType)) {
			//			if (name != null)
			//				throw new ExpressionParserException(namePos, "Unexpected token: " + name);
			//			if (from != null)
			//				throw new ExpressionParserException(fromPos, "Unexpected token: " + from);
			//			if (scope != SqlExpressionParser.ID_INVALID)
			//				throw new ExpressionParserException(targetTypePos, "Not expecting: " + SqlExpressionParser.toOperationString(scope));
			//			BasicTable r = new BasicTable();
			//			r.setTitle("VARS");
			//			r.addColumn(String.class, "Name");
			//			r.addColumn(String.class, "DeclaredType");
			//			r.addColumn(String.class, "Type");
			//			r.addColumn(String.class, "Value");
			//			r.addColumn(Boolean.class, "Readonly");
			//			CalcFrame consts = session.getConsts();
			//			for (String varname : consts.getVarKeys()) {
			//				Object rawvalue = consts.getValue(varname);
			//				String value = OH.toString(rawvalue);
			//				if (!isFull)
			//					value = SH.ddd(value, 80);
			//				r.getRows().addRow(varname, mf.forType(consts.getType(varname)), rawvalue == null ? null : mf.forType(rawvalue.getClass()), value, true);
			//			}
			//			CalcFrame vars = session.getVars();
			//			for (String varname : vars.getVarKeys()) {
			//				Object rawvalue = vars.getValue(varname);
			//				String value = OH.toString(rawvalue);
			//				if (!isFull)
			//					value = SH.ddd(value, 80);
			//				r.getRows().addRow(varname, mf.forType(vars.getType(varname)), rawvalue == null ? null : mf.forType(rawvalue.getClass()), value, false);
			//			}
			//			return r;
		} else if ("TABLE".equalsIgnoreCase(targetType)) {
			if (name == null)
				throw new ExpressionParserException(targetTypePos, "SHOW TABLE Expecting table name");
			if (centerSource != null) {
				Map<String, Row> schema = centerSource.getSchema(name);
				if (schema == null)
					throw new ExpressionParserException(targetTypePos, "Unknown table On Center '" + centerSource.getCenterName() + "': " + name);
				BasicTable r = new BasicTable(centerSource.getColumnSchema().getColumns());
				for (Row i : schema.values())
					r.getRows().addRow(i.getValuesCloned());
				r.setTitle(centerSource.getCenterName() + "." + name);
				TableHelper.sort(r, "Position");
				return r;
			}
			Table t = getTable(sf, targetTypePos, name, scope);
			AmiTableImpl table = getAmiTable(t);
			BasicTable r = new BasicTable(String.class, "ColumnName", String.class, "Type", Integer.class, "Position", Boolean.class, "NoNull", String.class, "Options");
			r.setTitle("COLUMNS");
			if (table != null) {
				for (int i = 0, l = table.getColumnsCount(); i < l; i++) {
					AmiColumnImpl<?> c = table.getColumnAt(i);
					String type = AmiTableUtils.toStringForDataType(c.getAmiType());
					r.getRows().addRow(c.getName(), type, i, !c.getAllowNull(), c.getOptionsString());
				}
			} else {
				for (int i = 0, l = t.getColumnsCount(); i < l; i++) {
					Column c = t.getColumnAt(i);
					String type = mf.forType(c.getType());
					r.getRows().addRow(c.getId(), type, i, true, null);
				}
			}
			return r;
		} else if ("COLUMNS".equalsIgnoreCase(targetType)) {
			if (name != null)
				throw new ExpressionParserException(namePos, "Unexpected token: " + name);
			if (centerSource != null)
				return new BasicTable(centerSource.getColumnSchema());
			BasicTable r = new BasicTable();
			r.addColumn(String.class, "TableName");
			r.addColumn(String.class, "Scope");
			r.addColumn(String.class, "ColumnName");
			r.addColumn(String.class, "DataType");
			r.addColumn(String.class, "Options");
			r.addColumn(Boolean.class, "NoNull");
			r.addColumn(Integer.class, "Position");
			r.addColumn(String.class, "DefinedBy");
			r.setTitle("COLUMNS");

			if (scope == SqlExpressionParser.ID_INVALID || scope == SqlExpressionParser.ID_PUBLIC) {
				StringBuilder buf = new StringBuilder();
				for (AmiTableImpl t : session.getImdb().getAmiTables()) {
					for (int i = 0; i < t.getColumnsCount(); i++) {
						AmiColumnImpl<?> c = t.getColumnAt(i);
						Row row = r.newEmptyRow();
						r.getRows().add(row);
						row.put("TableName", t.getName());
						row.put("Scope", "PUBLIC");
						row.put("ColumnName", c.getName());
						row.put("DataType", mf.forType(c.getType()));
						AmiSchema.getColumnOptions(c, SH.clear(buf), false);
						row.put("Options", buf.length() == 0 ? null : buf.toString());
						row.put("NoNull", !c.getAllowNull());
						row.put("Position", i);
						row.put("DefinedBy", AmiTableUtils.toStringForDefType(t.getDefType()));
					}
				}
				for (String tableName : hdb.getTablesSorted()) {
					AmiHdbSchema_Table t = hdb.getTableSchema(tableName);
					for (int i = 0; i < t.getColumns().length; i++) {
						AmiHdbSchema_Column c = t.getColumns()[i];
						Row row = r.newEmptyRow();
						r.getRows().add(row);
						row.put("TableName", t.getName());
						row.put("Scope", "PUBLIC");
						row.put("ColumnName", c.getName());
						row.put("DataType", mf.forType(c.getType()));
						row.put("NoNull", false);
						row.put("Position", i);
						row.put("DefinedBy", AmiTableUtils.toStringForDefType(t.getDefType()));
					}
				}
			}
			if (scope == SqlExpressionParser.ID_INVALID || scope == SqlExpressionParser.ID_TEMPORARY)
				if (session.getLocalTableset() != null)
					for (String s : session.getLocalTableset().getTableNamesSorted()) {
						Table t = session.getLocalTableset().getTable(s);
						for (int i = 0; i < t.getColumnsCount(); i++) {
							Column c = t.getColumnAt(i);
							Row row = r.newEmptyRow();
							r.getRows().add(row);
							row.put("TableName", t.getTitle());
							row.put("Scope", "TEMPORARY");
							row.put("ColumnName", c.getId());
							row.put("DataType", mf.forType(c.getType()));
							row.put("NoNull", false);
							row.put("Position", i);
							row.put("DefinedBy", "TEMPORARY");
						}
					}
			if (scope == SqlExpressionParser.ID_INVALID || scope == SqlExpressionParser.ID_VARIABLE)
				for (String key : globalVars.getVarKeys()) {
					Object value = globalVars.getValue(key);
					if (value instanceof Table) {
						Table t = (Table) value;
						for (int i = 0; i < t.getColumnsCount(); i++) {
							Column c = t.getColumnAt(i);
							Row row = r.newEmptyRow();
							r.getRows().add(row);
							row.put("TableName", key);
							row.put("Scope", "VARIABLE");
							row.put("ColumnName", c.getId());
							row.put("DataType", mf.forType(c.getType()));
							row.put("NoNull", false);
							row.put("Position", i);
							row.put("DefinedBy", "USER");
						}
					}
				}
			TableHelper.sort(r, "TableName", "DefinedBy", "ColumnName");
			return r;
		} else if ("INDEXES".equalsIgnoreCase(targetType)) {
			if (name != null)
				throw new ExpressionParserException(namePos, "Unexpected token: " + name);
			BasicTable r = new BasicTable();
			r.addColumn(String.class, "IndexName");
			r.addColumn(String.class, "TableName");
			r.addColumn(String.class, "ColumnName");
			r.addColumn(String.class, "IndexType");
			r.addColumn(Integer.class, "IndexPosition");
			r.addColumn(String.class, "Constraint");
			r.addColumn(String.class, "AutoGen");
			r.addColumn(String.class, "DefinedBy");
			r.addColumn(String.class, "Scope");
			if (scope == SqlExpressionParser.ID_INVALID || scope == SqlExpressionParser.ID_PUBLIC) {
				for (AmiTableImpl t : session.getImdb().getAmiTables()) {
					for (AmiIndexImpl i : t.getIndexes()) {
						for (int n = 0; n < i.getColumnsCount(); n++) {
							AmiColumnImpl c = i.getColumn(n);
							Row row = r.newEmptyRow();
							row.put("IndexName", i.getName());
							row.put("TableName", t.getName());
							row.put("ColumnName", c.getName());
							row.put("IndexType", AmiTableUtils.toStringForIndexType(i.getIndexTypeAt(n)));
							row.put("IndexPosition", n);
							row.put("Constraint", AmiTableUtils.toStringForIndexConstraintType(i.getConstraintType()));
							row.put("AutoGen", AmiTableUtils.toStringForIndexAutoGenType(i.getAutoGenType()));
							row.put("DefinedBy", AmiTableUtils.toStringForDefType(t.getDefType()));
							row.put("Scope", "PUBLIC");
							r.getRows().add(row);
						}

					}
				}
				for (String tableName : hdb.getTablesSorted()) {
					AmiHdbSchema_Table t = hdb.getTableSchema(tableName);
					for (AmiHdbSchema_Index i : t.getIndexes()) {
						Row row = r.newEmptyRow();
						row.put("IndexName", i.getName());
						row.put("TableName", t.getName());
						row.put("ColumnName", i.getColumnName());
						row.put("IndexType", "SORT");
						row.put("IndexPosition", 0);
						row.put("Constraint", "NONE");
						row.put("AutoGen", "NONE");
						row.put("DefinedBy", AmiTableUtils.toStringForDefType(t.getDefType()));
						row.put("Scope", "PUBLIC");
						r.getRows().add(row);

					}
				}
			}
			r.setTitle("INDEXES");
			return r;
		} else if ("COMMANDS".equalsIgnoreCase(targetType)) {
			if (name != null)
				throw new ExpressionParserException(namePos, "Unexpected token: " + name);
			if (from != null)
				throw new ExpressionParserException(fromPos, "Unexpected token: " + from);
			BasicTable r = new BasicTable(db.getSystemSchema().__COMMAND.table.getTable());
			if (scope != SqlExpressionParser.ID_INVALID && scope != SqlExpressionParser.ID_PUBLIC)
				r.clear();
			r.setTitle("COMMANDS");
			return r;
		} else if ("DATASOURCES".equalsIgnoreCase(targetType)) {
			if (name != null)
				throw new ExpressionParserException(namePos, "Unexpected token: " + name);
			if (from != null)
				throw new ExpressionParserException(fromPos, "Unexpected token: " + from);
			BasicTable r = new BasicTable(db.getSystemSchema().__DATASOURCE.table.getTable());
			if (!isFull)
				reduceColSize(r, "OP", 32);
			if (scope != SqlExpressionParser.ID_INVALID && scope != SqlExpressionParser.ID_PUBLIC)
				r.clear();
			r.setTitle("DATASOURCES");
			return r;
		} else if ("DATASOURCE_TYPES".equalsIgnoreCase(targetType)) {
			if (name != null)
				throw new ExpressionParserException(namePos, "Unexpected token: " + name);
			if (from != null)
				throw new ExpressionParserException(fromPos, "Unexpected token: " + from);
			BasicTable r = new BasicTable(db.getSystemSchema().__DATASOURCE_TYPE.table.getTable());
			if (!isFull)
				reduceColSize(r, "Properties", 32);
			if (scope != SqlExpressionParser.ID_INVALID && scope != SqlExpressionParser.ID_PUBLIC)
				r.clear();
			r.setTitle("DATASOURCE_TYPES");
			return r;
		} else if ("PROCEDURES".equalsIgnoreCase(targetType)) {
			if (name != null)
				throw new ExpressionParserException(namePos, "Unexpected token: " + name);
			if (from != null)
				throw new ExpressionParserException(fromPos, "Unexpected token: " + from);
			BasicTable r = new BasicTable(db.getSystemSchema().__PROCEDURE.table.getTable());
			r.addColumn(Long.class, "ExecutedCount");
			r.addColumn(Double.class, "MillisSpent");
			r.addColumn(Double.class, "AvgMillisSpent");
			r.addColumn(Long.class, "ErrorsCount");
			if (!isFull) {
				reduceColSize(r, "Arguments", 64);
				reduceColSize(r, "Options", 32);
			}
			for (Row row : r.getRows()) {
				AmiStoredProcBindingImpl i = db.getObjectsManager().getAmiStoredProcBinding(row.get("ProcedureName", Caster_String.INSTANCE));
				row.put("ExecutedCount", i.getStatsCount());
				row.put("MillisSpent", i.getStatsNanos() / 1000000d);
				row.put("AvgMillisSpent", (i.getStatsNanos() / 1000000d) / i.getStatsCount());
				row.put("ErrorsCount", i.getStatsErrors());
			}
			if (scope != SqlExpressionParser.ID_INVALID && scope != SqlExpressionParser.ID_PUBLIC)
				r.clear();
			r.setTitle("PROCEDURES");
			return r;
		} else if ("PROPERTIES".equalsIgnoreCase(targetType)) {
			if (name != null)
				throw new ExpressionParserException(namePos, "Unexpected token: " + name);
			if (from != null)
				throw new ExpressionParserException(fromPos, "Unexpected token: " + from);
			BasicTable r = new BasicTable(db.getSystemSchema().__PROPERTY.table.getTable());
			if (scope != SqlExpressionParser.ID_INVALID && scope != SqlExpressionParser.ID_PUBLIC)
				r.clear();
			r.setTitle("PROPERTIES");
			return r;
		} else if ("RESOURCES".equalsIgnoreCase(targetType)) {
			if (name != null)
				throw new ExpressionParserException(namePos, "Unexpected token: " + name);
			if (from != null)
				throw new ExpressionParserException(fromPos, "Unexpected token: " + from);
			BasicTable r = new BasicTable(db.getSystemSchema().__RESOURCE.table.getTable());
			if (scope != SqlExpressionParser.ID_INVALID && scope != SqlExpressionParser.ID_PUBLIC)
				r.clear();
			r.setTitle("RESOURCES");
			return r;
		} else if ("DBO".equalsIgnoreCase(targetType)) {
			if (name == null)
				throw new ExpressionParserException(targetTypePos, "SHOW DBO Expecting Dbo name");
			BasicTable r = new BasicTable();
			AmiDboBindingImpl dbo = db.getObjectsManager().getAmiDboBinding(name);
			if (dbo == null)
				throw new ExpressionParserException(targetTypePos, "Unknown DBO: " + name);
			r.setTitle("DBO");
			r.addColumn(String.class, "Category");
			r.addColumn(String.class, "Description");
			r.addColumn(String.class, "AmiScript");
			r.addColumn(Long.class, "ExecutedCount");
			r.addColumn(Double.class, "MillisSpent");
			r.addColumn(Double.class, "AvgMillisSpent");
			r.addColumn(Long.class, "ErrorsCount");
			r.addColumn(String.class, "LastError");
			for (int i = 0; i < dbo.getMethodsCount(); i++) {
				Method m = dbo.getMethodAt(i);
				double d = m.statNanos / 1000000d;
				r.getRows().addRow("METHOD", m.method.getParamsDefinition().toString(mf), null, m.statCount, d, d / m.statCount, m.statErrors, toErrorString(m.lastError));
			}
			for (Callback m : dbo.getCallbacks().values()) {
				double d = m.statNanos / 1000000d;
				r.getRows().addRow("CALLBACK", m.def.toString(mf), SH.trim(m.amiscript), m.statCount, d, d / m.statCount, m.statErrors, toErrorString(m.getLastException()));
			}
			if (!isFull)
				reduceColSize(r, "AmiScript", 32);
			return r;
		} else if ("DBOS".equalsIgnoreCase(targetType)) {
			if (name != null)
				throw new ExpressionParserException(namePos, "Unexpected token: " + name);
			if (from != null)
				throw new ExpressionParserException(fromPos, "Unexpected token: " + from);
			BasicTable r = new BasicTable(db.getSystemSchema().__DBO.table.getTable());
			r.setTitle("DBOS");
			r.addColumn(Long.class, "ExecutedCount");
			r.addColumn(Double.class, "MillisSpent");
			r.addColumn(Long.class, "ErrorsCount");
			r.addColumn(Long.class, "CompiledErrorsCount");
			for (Row row : r.getRows()) {
				AmiDboBindingImpl i = db.getObjectsManager().getAmiDboBinding(row.get("DboName", Caster_String.INSTANCE));
				double millisSpent = i.getStatsNanosTotal() / 1000000d;
				row.put("ExecutedCount", i.getStatsCountTotal());
				row.put("MillisSpent", millisSpent);
				row.put("ErrorsCount", i.getStatsErrorsTotal());
				row.put("CompiledErrorsCount", i.getStatsCompiledErrors());
			}
			return r;
		} else if ("DBO_PLUGIN".equalsIgnoreCase(targetType)) {
			if (name == null)
				throw new ExpressionParserException(namePos, "Expeting DBO PLUGIN TYPE (Registered types are: " + SH.join(',', db.getFactoriesManager().getDboTypes()) + ")");
			AmiDboFactoryWrapper dbo = db.getDboFactory(name);
			if (dbo == null)
				throw new ExpressionParserException(namePos, "Unknown DBO PLUGIN TYPE (Registered types are: " + SH.join(',', db.getFactoriesManager().getDboTypes()) + ")");
			BasicTable r = new BasicTable();
			r.setTitle("DBO_PLUGIN");
			r.addColumn(String.class, "Type");
			r.addColumn(String.class, "Name");
			r.addColumn(String.class, "Description");
			r.addColumn(String.class, "Required");
			for (AmiFactoryOption o : dbo.getAllowedOptions()) {
				r.getRows().addRow("USE Option", o.getName(), o.getHelp(), o.getRequired());
			}
			for (AmiDboMethodWrapper o : dbo.getMethods()) {
				r.getRows().addRow("Method", o.getMethodName(), o.getParamsDefinition().toString(mf), null);
			}
			return r;
		} else if ("TRIGGERS".equalsIgnoreCase(targetType)) {
			if (name != null)
				throw new ExpressionParserException(namePos, "Unexpected token: " + name);
			if (from != null)
				throw new ExpressionParserException(fromPos, "Unexpected token: " + from);
			BasicTable r = new BasicTable(db.getSystemSchema().__TRIGGER.table.getTable());
			if (!isFull)
				reduceColSize(r, "Options", 32);
			r.addColumn(Long.class, "ExecutedCount");
			r.addColumn(Double.class, "MillisSpent");
			r.addColumn(Double.class, "AvgMillisSpent");
			r.addColumn(Long.class, "ErrorsCount");
			r.addColumn(Long.class, "ReturnedFalseCount");
			for (Row row : r.getRows()) {
				AmiTriggerBindingImpl i = db.getObjectsManager().getAmiTriggerBinding(row.get("TriggerName", Caster_String.INSTANCE));
				double millisSpent = i.getStatsNanosTotal() / 1000000d;
				row.put("ExecutedCount", i.getStatsCountTotal());
				row.put("MillisSpent", millisSpent);
				row.put("AvgMillisSpent", millisSpent / i.getStatsCountTotal());
				row.put("ErrorsCount", i.getStatsErrorsTotal());
				row.put("ReturnedFalseCount", i.getStatsReturnedFalseTotal());
			}
			if (scope != SqlExpressionParser.ID_INVALID && scope != SqlExpressionParser.ID_PUBLIC)
				r.clear();
			r.setTitle("TRIGGERS");
			return r;
		} else if ("TIMERS".equalsIgnoreCase(targetType)) {
			if (name != null)
				throw new ExpressionParserException(namePos, "Unexpected token: " + name);
			if (from != null)
				throw new ExpressionParserException(fromPos, "Unexpected token: " + from);
			BasicTable r = new BasicTable(db.getSystemSchema().__TIMER.table.getTable());
			if (!isFull)
				reduceColSize(r, "Options", 32);
			r.addColumn(Long.class, "ExecutedCount");
			r.addColumn(Double.class, "MillisSpent");
			r.addColumn(Double.class, "AvgMillisSpent");
			r.addColumn(Long.class, "ErrorsCount");
			r.addColumn(Boolean.class, "Running");
			for (Row row : r.getRows()) {
				AmiTimerBindingImpl i = db.getObjectsManager().getAmiTimerBinding(row.get("TimerName", Caster_String.INSTANCE));
				row.put("ExecutedCount", i.getStatsCount());
				row.put("MillisSpent", i.getStatsNanos() / 1000000d);
				row.put("AvgMillisSpent", (i.getStatsNanos() / 1000000d) / i.getStatsCount());
				row.put("ErrorsCount", i.getStatsErrors());
				row.put("Running", i.getIsRunning());
			}
			if (scope != SqlExpressionParser.ID_INVALID && scope != SqlExpressionParser.ID_PUBLIC)
				r.clear();
			r.setTitle("TIMERS");
			return r;
		} else if ("CONNECTIONS".equalsIgnoreCase(targetType)) {
			if (name != null)
				throw new ExpressionParserException(namePos, "Unexpected token: " + name);
			if (from != null)
				throw new ExpressionParserException(fromPos, "Unexpected token: " + from);
			BasicTable r = new BasicTable(db.getSystemSchema().__CONNECTION.table.getTable());
			if (scope != SqlExpressionParser.ID_INVALID && scope != SqlExpressionParser.ID_PUBLIC)
				r.clear();
			r.setTitle("CONNECTIONS");
			return r;
		} else if ("RELAYS".equalsIgnoreCase(targetType)) {
			if (name != null)
				throw new ExpressionParserException(namePos, "Unexpected token: " + name);
			if (from != null)
				throw new ExpressionParserException(fromPos, "Unexpected token: " + from);
			BasicTable r = new BasicTable(db.getSystemSchema().__RELAY.table.getTable());
			if (scope != SqlExpressionParser.ID_INVALID && scope != SqlExpressionParser.ID_PUBLIC)
				r.clear();
			r.setTitle("RELAYS");
			return r;
		} else if ("PLUGINS".equalsIgnoreCase(targetType)) {
			if (name != null)
				throw new ExpressionParserException(namePos, "Unexpected token: " + name);
			if (from != null)
				throw new ExpressionParserException(fromPos, "Unexpected token: " + from);
			BasicTable r = new BasicTable(db.getSystemSchema().__PLUGIN.table.getTable());
			r.setTitle("PLUGINS");
			if (scope != SqlExpressionParser.ID_INVALID && scope != SqlExpressionParser.ID_PUBLIC)
				r.clear();
			else if (isFull) {
				MethodFactoryManager methodFactory = sf.getFactory();
				for (Row row : r.getRows()) {
					String pluginType = row.get("PluginType", Caster_String.INSTANCE);
					String pluginName = row.get("PluginName", Caster_String.INSTANCE);
					AmiPlugin plugin = db.getFactoriesManager().getPlugin(pluginType, pluginName);
					if (plugin instanceof AmiFactoryPlugin) {
						AmiFactoryPlugin fp = (AmiFactoryPlugin) plugin;
						String argumentsString = AmiUtils.descriptFactoryOptions(fp.getAllowedOptions(), methodFactory, true);
						row.put("Arguments", argumentsString);
					}
				}
			}
			return r;
		} else if ("PLUGIN_OPTIONS".equalsIgnoreCase(targetType)) {
			BasicTable r = new BasicTable();
			r.setTitle("PLUGIN_OPTIONS");
			r.addColumn(String.class, "PluginName");
			r.addColumn(String.class, "PluginType");
			r.addColumn(String.class, "Option");
			r.addColumn(String.class, "OptionType");
			r.addColumn(Boolean.class, "Required");
			r.addColumn(String.class, "Help");
			BasicTable r2 = new BasicTable(db.getSystemSchema().__PLUGIN.table.getTable());
			for (Row row : r2.getRows()) {
				String pluginType = row.get("PluginType", Caster_String.INSTANCE);
				String pluginName = row.get("PluginName", Caster_String.INSTANCE);
				AmiPlugin plugin = db.getFactoriesManager().getPlugin(pluginType, pluginName);
				if (plugin instanceof AmiFactoryPlugin) {
					AmiFactoryPlugin fp = (AmiFactoryPlugin) plugin;
					for (AmiFactoryOption i : fp.getAllowedOptions())
						r.getRows().addRow(pluginName, pluginType, i.getName(), mf.forType(i.getType()), i.getRequired(), i.getHelp());
				}
			}
			return r;
		} else if ("PLUGINS".equalsIgnoreCase(targetType)) {
			if (name != null)
				throw new ExpressionParserException(namePos, "Unexpected token: " + name);
			if (from != null)
				throw new ExpressionParserException(fromPos, "Unexpected token: " + from);
			BasicTable r = new BasicTable(db.getSystemSchema().__PLUGIN.table.getTable());
			r.setTitle("PLUGINS");
			if (scope != SqlExpressionParser.ID_INVALID && scope != SqlExpressionParser.ID_PUBLIC)
				r.clear();
			//			else if (isFull) {
			//				MethodFactoryManager methodFactory = sf.getFactory();
			//				for (Row row : r.getRows()) {
			//					String pluginType = row.get("PluginType", Caster_String.INSTANCE);
			//					String pluginName = row.get("PluginName", Caster_String.INSTANCE);
			//					AmiPlugin plugin = db.getFactoriesManager().getPlugin(pluginType, pluginName);
			//					if (plugin instanceof AmiFactoryPlugin) {
			//						AmiFactoryPlugin fp = (AmiFactoryPlugin) plugin;
			//						String argumentsString = AmiUtils.descriptFactoryOptions(fp.getAllowedOptions(), methodFactory, true);
			//						row.put("Arguments", argumentsString);
			//					}
			//				}
			//			}
			return r;
		} else if ("SESSIONS".equalsIgnoreCase(targetType)) {
			if (name != null)
				throw new ExpressionParserException(namePos, "Unexpected token: " + name);
			if (from != null)
				throw new ExpressionParserException(fromPos, "Unexpected token: " + from);
			AmiImdbSession[] sessions = session.getImdb().getSessionManager().getSessions();
			BasicTable r = new BasicTable(Long.class, "ID", String.class, "Me", Boolean.class, "Active", Boolean.class, "IsTemp", String.class, "DefinedBy", String.class, "User",
					DateMillis.class, "CreatedOn", DateMillis.class, "LastAccessed", Integer.class, "Tables", String.class, "Description");
			r.setTitle("SESSIONS");
			for (AmiImdbSession i : sessions) {
				long id = i.getSessionId();
				String deftype = AmiTableUtils.toStringForDefType(i.getDefinedBy());
				boolean isLocal = i == sf.getTableset();
				r.getRows().addRow(id, isLocal ? "**" : "", i.getIsLocked(), i.getIsTemporary(), deftype, i.getUsername(), new DateMillis(i.getCreatedTime()),
						new DateMillis(i.getLastUsedTime()), i.getTempTablesCount(), i.getDescription());

			}
			TableHelper.sort(r, "CreatedOn");
			if (scope != SqlExpressionParser.ID_INVALID && scope != SqlExpressionParser.ID_PUBLIC)
				r.clear();
			return r;
		} else if ("PROCESSES".equalsIgnoreCase(targetType)) {
			if (name != null)
				throw new ExpressionParserException(namePos, "Unexpected token: " + name);
			if (from != null)
				throw new ExpressionParserException(fromPos, "Unexpected token: " + from);
			AmiImdbSession[] sessions = session.getImdb().getSessionManager().getSessions();
			BasicTable r = new BasicTable(Long.class, "ID", Long.class, "ParentID", String.class, "Status", Long.class, "SessionID", DateMillis.class, "CreatedOn", String.class,
					"TargetDs", String.class, "TargetRelay", String.class, "Query");
			r.setTitle("PROCESSES");
			AmiCenterState state = db.getState();
			if (!state.getPartition().lockForRead(20000, TimeUnit.MILLISECONDS))
				throw new RuntimeException("Could not aquire lock");
			try {
				for (AmiCenterProcess i : state.getProcesses()) {
					long id = i.getProcessId();
					String query = i.getQuery();
					String dsName = i.getDsName();
					String dsRelayId = i.getDsRelayId();
					String step = i.getProcessStatus();
					long sessionId = i.getSessionId();
					long parentId = i.getParentProcessId();
					r.getRows().addRow(id, parentId > 0 ? parentId : null, step, sessionId > 0 ? sessionId : null, new DateMillis(i.getStartTime()), dsName, dsRelayId, query);
				}
			} finally {
				state.getPartition().unlockForRead();
			}
			if (scope != SqlExpressionParser.ID_INVALID && scope != SqlExpressionParser.ID_PUBLIC)
				r.clear();
			TableHelper.sort(r, "ID");
			return r;
		} else if ("CENTERS".equalsIgnoreCase(targetType)) {
			if (name != null)
				throw new ExpressionParserException(namePos, "Unexpected token: " + name);
			if (from != null)
				throw new ExpressionParserException(fromPos, "Unexpected token: " + from);
			BasicTable r = new BasicTable(db.getSystemSchema().__CENTER.table.getTable());
			if (scope != SqlExpressionParser.ID_INVALID && scope != SqlExpressionParser.ID_PUBLIC)
				r.clear();
			r.setTitle("CENTERS");
			r.addColumn(String.class, "Status");
			r.addColumn(Long.class, "ReplicatedTables");
			r.addColumn(Long.class, "ReplicatedRows");
			for (Row i : r.getRows()) {
				String rname = i.get("CenterName", String.class);
				AmiCenterReplicationCenter t = db.getReplicator().getCenter(rname);
				long repTables = 0;
				long repValues = 0;
				for (AmiCenterReplication rep : t.getReplications()) {
					repTables++;
					repValues += rep.getAmiIdMappings().size();
				}
				i.put("Status", t.getClientConnection().getCache().isConnected() ? "CONNECTED" : "NOT_CONNECTED");
				i.put("ReplicatedTables", repTables);
				i.put("ReplicatedRows", repValues);
			}
			return r;
		} else if ("REPLICATIONS".equalsIgnoreCase(targetType)) {
			if (name != null)
				throw new ExpressionParserException(namePos, "Unexpected token: " + name);
			BasicTable r = new BasicTable(db.getSystemSchema().__REPLICATION.table.getTable());
			if (scope != SqlExpressionParser.ID_INVALID && scope != SqlExpressionParser.ID_PUBLIC)
				r.clear();
			if (!isFull) {
				reduceColSize(r, "Mapping", 32);
				reduceColSize(r, "Options", 32);
			}
			r.setTitle("REPLICATIONS");
			r.addColumn(Long.class, "ReplicatedRows");
			r.addColumn(String.class, "Status");
			for (Row i : r.getRows()) {
				String tname = i.get("SourceTable", String.class);
				String rsname = i.get("SourceCenter", String.class);
				AmiCenterReplicationCenter t = db.getReplicator().getCenter(rsname);
				AmiCenterReplication replications = t.getReplication(tname);
				String status;
				if (replications.isInvalid()) {
					status = "TARGET_TABLE_NOT_FOUND";
				} else if (!t.getClientConnection().getCache().isConnected()) {
					status = "NOT_CONNECTED";
				} else if (!replications.isSourceTableExists()) {
					status = "SOURCE_TABLE_NOT_FOUND";
				} else
					status = "CONNECTED";
				i.put("Status", status);
				i.put("ReplicatedRows", (long) replications.getAmiIdMappings().size());
			}
			return r;
		} else if ("METHODS".equalsIgnoreCase(targetType)) {
			List<MethodFactory> sink = new ArrayList<MethodFactory>();
			db.getScriptManager().getMethodFactory().getAllMethodFactories(sink);
			sf.getFactory().getMethodFactories(sink);
			Set<MethodFactory> declared = new IdentityHashSet<MethodFactory>();
			Set<MethodFactory> managed = new IdentityHashSet<MethodFactory>();
			Set<MethodFactory> local = new IdentityHashSet<MethodFactory>();
			db.getScriptManager().getDeclaredMethodFactory().getMethodFactories(declared);
			db.getScriptManager().getManagedMethodFactory().getMethodFactories(managed);
			sf.getFactory().getMethodFactories(local);
			List<DerivedCellMemberMethod<Object>> sink2 = new ArrayList<DerivedCellMemberMethod<Object>>();
			mf.getMemberMethods(null, null, sink2);
			BasicTable r = new BasicTable(String.class, "TargetType", String.class, "MethodName", String.class, "ReturnType", String.class, "Definition", String.class,
					"DefinedBy");
			r.setTitle("METHODS");
			StringBuilder sb = new StringBuilder();
			for (MethodFactory i : sink) {
				ParamsDefinition definition = i.getDefinition();
				sb.append(definition.getMethodName()).append('(');
				for (int n = 0; n < definition.getParamsCount(); n++) {
					if (n > 0)
						sb.append(", ");
					sb.append(mf.forType(definition.getParamType(n))).append(' ').append(definition.getParamName(n));
				}
				sb.append(')');
				String user;
				if (local.contains(i))
					user = "TEMPORARY";
				else if (managed.contains(i))
					user = "USER";
				else if (declared.contains(i))
					user = "CONFIG";
				else
					user = "SYSTEM";
				r.getRows().addRow(null, definition.getMethodName(), definition.getReturnType() == null ? "Object" : mf.forType(definition.getReturnType()),
						SH.toStringAndClear(sb), user);
			}
			for (DerivedCellMemberMethod<Object> i : sink2) {
				String forType = mf.forType(i.getTargetType());
				if (i.getMethodName() == null)
					sb.append("new ").append(forType).append('(');
				else
					sb.append(forType).append('.').append(i.getMethodName()).append('(');
				for (int n = 0; n < i.getParamNames().length; n++) {
					if (n > 0)
						sb.append(", ");
					sb.append(mf.forType(n == i.getParamTypes().length ? i.getVarArgType() : i.getParamTypes()[n])).append(' ').append(i.getParamNames()[n]);

				}
				sb.append(')');
				r.getRows().addRow(forType, i.getMethodName() == null ? "<constructor>" : i.getMethodName(), i.getReturnType() == null ? "Object" : mf.forType(i.getReturnType()),
						SH.toStringAndClear(sb), "SYSTEM");
			}
			TableHelper.sort(r, "TargetType", "MethodName");
			return r;
		} else if ("METHOD".equalsIgnoreCase(targetType)) {
			String beforeArgs;
			Class[] argClasses;
			BasicTable r = new BasicTable(String.class, "Part", String.class, "Help");
			if (name == null || name.indexOf('(') == -1 || !name.endsWith(")"))
				throw new ExpressionParserException(namePos,
						"Should be in the form `class.method(argtype1,argtype2,...)` or `method(argtype1,argtype2,...)` or `new class(argtype1,argtype2)`");
			beforeArgs = SH.beforeFirst(name, '(');
			String[] args = SH.split(',', SH.stripSuffix(SH.afterFirst(name, '('), ")", true));
			argClasses = new Class[args.length];
			//store the position array of args[]
			int[] posArray = new int[args.length];
			for (int i = 0; i < args.length; i++) {
				posArray[i] = mn.getParamAt(i).getPosition();
			}
			for (int i = 0; i < args.length; i++) {
				String arg = SH.beforeFirst(SH.trim(args[i]), ' ');
				argClasses[i] = mf.forNameNoThrow(arg);
				if (argClasses[i] == null)
					throw new ExpressionParserException(posArray[i], "Unknown class: " + arg);
			}
			ParamsDefinition def;
			if (beforeArgs.startsWith("new ") || beforeArgs.indexOf('.') != -1) {
				String className;
				String method;
				if (beforeArgs.startsWith("new ")) {
					className = SH.trim(SH.stripPrefix(beforeArgs, "new ", true));
					method = null;
				} else {
					className = SH.beforeFirst(beforeArgs, '.');
					method = SH.afterFirst(beforeArgs, '.');
				}
				Class<?> clazz = mf.forNameNoThrow(className);
				if (clazz == null)
					throw new ExpressionParserException(namePos, "Unknown class: " + className);
				DerivedCellMemberMethod<Object> m = mf.findMemberMethod(clazz, method, argClasses);
				if (m == null)
					throw new ExpressionParserException(namePos, "Unknown method: " + name);
				def = m.getParamsDefinition();
				r.getRows().addRow("DEFINITION", m.toString(new StringBuilder(), db.getScriptManager().getMethodFactory()).toString());
				String[] pn = m.getParamNames();
				if (m instanceof AmiAbstractMemberMethod) {
					AmiAbstractMemberMethod am = (AmiAbstractMemberMethod) m;
					r.getRows().addRow("DESCRIPTION", am.getDescription());
					String[] pd = am.getParamDescriptions();
					for (int i = 0; i < pn.length; i++) {
						r.getRows().addRow("ARG" + i, pn[i] + " - " + pd[i]);
					}
				} else {
					for (int i = 0; i < pn.length; i++)
						r.getRows().addRow("ARG" + i, pn[i]);
				}
			} else {
				MethodFactory m = db.getScriptManager().getMethodFactory().getMethodFactory(beforeArgs, argClasses);
				if (m == null)
					throw new ExpressionParserException(namePos, "Unknown method: " + name);
				def = m.getDefinition();
				String dh = m.getDefinition().getDescriptionHtml();
				r.getRows().addRow("Signature", m.getDefinition().toString(db.getScriptManager().getMethodFactory(), new StringBuilder()).toString());
				if (m instanceof DeclaredMethodFactory) {
					String text = ((DeclaredMethodFactory) m).getText(db.getScriptManager().getMethodFactory());
					//					text = SH.afterFirst(text, '{');
					//					text = SH.beforeLast(text, '}');
					int n = 1;
					r.getRows().addRow("Body", SH.trim('\n', text));
				} else {
					r.getRows().addRow("Description", dh);
					for (int i = 0; i < m.getDefinition().getParamsCount(); i++)
						r.getRows().addRow("ARG" + i, m.getDefinition().getParamDescriptions()[i] + " - " + m.getDefinition().getParamNames()[i]);
				}
			}
			return r;
		} else if ("TABLE".equalsIgnoreCase(targetType) || "TABLES".equalsIgnoreCase(targetType) || "COLUMNS".equalsIgnoreCase(targetType) || "METHODS".equalsIgnoreCase(targetType)
				|| "VARS".equalsIgnoreCase(targetType))
			return inner.processShow(sf, targetType, targetTypePos, scope, isFull, name, namePos, from, fromPos, null);
		else
			throw new ExpressionParserException(targetTypePos,
					"Expecting Either: COLUMNS [FROM sourcename], COMMANDS, CONNECTIONS, DATASOURCES, DATASOURCE_TYPES, [FULL] DBO, DBO_PLUGIN, DBOS, INDEXES [FROM sourcename], METHOD, METHODS, PLUGINS, PLUGIN_OPTIONS, PROCEDURES, PROCESSES, PROPERTIES, RELAYS, REPLICATIONS, REPLICATION_SOURCES, RESOURCES, SESSIONS, TABLE,TABLES [FROM sourcename], [FULL] TIMERS, [FULL] TRIGGERS, VARS");

	}

	static private String toErrorString(Throwable e) {
		if (e == null)
			return null;
		if (e instanceof ExpressionParserException) {
			return ((ExpressionParserException) e).toLegibleString();
		} else
			return SH.printStackTrace(e);
	}

	private void reduceColSize(BasicTable r, String col, int size) {
		int loc = r.getColumn(col).getLocation();
		boolean truncated = false;
		for (Row row : r.getRows()) {
			String at = row.getAt(loc, Caster_String.INSTANCE);
			if (at != null) {
				at = SH.replaceAll(at, '\n', "");
				at = SH.replaceAll(at, '\n', "");
				if (at.length() > size) {
					row.putAt(loc, at.substring(0, size));
					truncated = true;
				}
			}
		}
		if (truncated)
			r.renameColumn(loc, col + "(Partial Text)");
	}
	@Override
	public Table processTableAdd(CalcFrameStack sf, String name, int namePosition, Table r, Map<String, Node> useOptions, int scope, boolean ifNotExists) {
		if (scope == SqlExpressionParser.ID_TEMPORARY || scope == SqlExpressionParser.ID_INVALID)
			return this.inner.processTableAdd(sf, name, namePosition, r, useOptions, scope, ifNotExists);
		//		if (hdb        .getTableNoThrow(name) != null || db.getTableNoThrow(name) != null) {
		//			if (ifNotExists)
		//				return null;
		//			throw new ExpressionParserException(namePosition, "Table already exists");
		//		}
		//		if (scope == SqlExpressionParser.ID_HISTORICAL) {
		//			AmiImdbSession session = AmiCenterUtils.getSession(tablesMap);
		//			session.assertCanAlter();
		//			if (ifNotExists && hdb        .getTableNoThrow(name) != null)
		//				return null;
		//			AmiHistoryTableWrapper t = this.hdb        .processTableAdd(sqlProcessor, name, namePosition, (AmiImdbSession) tablesMap, r, useOptions, scope, ifNotExists, session);
		//			//			try {
		//			//				t.getHistoricalTable().append(r);
		//			//			} catch (IOException e) {
		//			//				throw OH.toRuntime(e);
		//			//			}
		//			return t;
		//		} else if (scope != SqlExpressionParser.ID_PUBLIC)
		//			throw new ExpressionParserException(namePosition, "Invalid scope: " + SqlExpressionParser.toOperationString(scope));
		//		else {
		//		session.assertCanAlter();
		int colsCount = r.getColumnsCount();
		int[] colDefPos = new int[colsCount];
		Map<String, Node>[] colOptions = new Map[colsCount];
		String[] names = new String[colsCount];
		String[] types = new String[colsCount];
		for (int i = 0; i < colsCount; i++) {
			Column col = r.getColumnAt(i);
			colDefPos[i] = namePosition;
			colOptions[i] = null;
			names[i] = AmiUtils.toValidVarName((String) col.getId());
			if (col.getType() == BigInteger.class)
				types[i] = "Long";
			else if (col.getType() == BigDecimal.class)
				types[i] = "Double";
			else
				types[i] = sf.getFactory().forType(col.getType());
		}
		Table newTable = processTableAdd(sf, name, namePosition, types, names, colOptions, colDefPos, useOptions, scope, ifNotExists);
		if (newTable instanceof AmiHdbTableRep) {
			AmiCenterUtils.getSession(sf).getHdb().addRows((AmiHdbTableRep) newTable, r);
		} else {
			AmiImdbSession session = AmiCenterUtils.getSession(sf);
			AmiTableImpl newAmiTable = getAmiTable(newTable);

			AmiPreparedRowImpl pr = newAmiTable.createAmiPreparedRow();
			for (Row row : r.getRows()) {
				pr.reset();
				pr.setIgnoreWriteFailed(true);
				Object val;
				for (int i = 0; i < colsCount; i++) {
					val = row.getAt(i);
					if (val != null)
						pr.putAt(i, val);
				}
				newAmiTable.insertAmiRow(pr, sf);
			}
		}
		return newTable;
	}

	@Override
	public void processEnabled(CalcFrameStack sf, boolean enable, int position, String type, String name[], int namePosition[]) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		AmiImdbImpl db = session.getImdb();
		session.assertCanAlter();
		if ("TRIGGER".equals(type)) {
			for (int i = 0; i < name.length; i++) {
				AmiTriggerBindingImpl trigger = db.getObjectsManager().getAmiTriggerBinding(name[i]);
				if (trigger == null)
					throw new ExpressionParserException(namePosition[i], "Trigger not found: " + name[i]);
			}
			for (int i = 0; i < name.length; i++) {
				AmiTriggerBindingImpl trigger = db.getObjectsManager().getAmiTriggerBinding(name[i]);
				trigger.setIsEnabled(enable, sf);
			}
		} else if ("TIMER".equals(type)) {
			for (int i = 0; i < name.length; i++) {
				AmiTimerBindingImpl timer = db.getObjectsManager().getAmiTimerBinding(name[i]);
				if (timer == null)
					throw new ExpressionParserException(namePosition[i], "Timer not found: " + name[i]);
			}
			for (int i = 0; i < name.length; i++) {
				AmiTimerBindingImpl timer = db.getObjectsManager().getAmiTimerBinding(name[i]);
				timer.setIsEnabled(enable, sf);
			}
		} else if ("DBO".equals(type)) {
			for (int i = 0; i < name.length; i++) {
				AmiDboBindingImpl dbo = db.getObjectsManager().getAmiDboBinding(name[i]);
				if (dbo == null)
					throw new ExpressionParserException(namePosition[i], "Dbo not found: " + name[i]);
			}
			for (int i = 0; i < name.length; i++) {
				AmiDboBindingImpl dbo = db.getObjectsManager().getAmiDboBinding(name[i]);
				dbo.setIsEnabled(enable, sf);
			}
		} else
			throw new ExpressionParserException(position, "Expecting DBO, TRIGGER or TIMER: " + type);
	}
	//	@Override
	//	public Table processReturningTable(CalcFrameStack sf, Table r) {
	//		while (sf.getParent() != null)
	//			if (!sf.isParentVisible())
	//				return r;
	//			else
	//				sf = sf.getParent();
	//		AmiCenterUtils.getSession(sf).addReturnTable(r);
	//		//		Tableset tablesMap = sf.getTableset();
	//		//		if (tablesMap instanceof AmiImdbSession) {
	//		//			AmiImdbSession session = (AmiImdbSession) tablesMap;
	//		//			//			if (!session.isInStack(AmiTrigger.CALL))
	//		//			session.addReturnTable(r);
	//		//		}
	//		return r;
	//	}
	//	@Override
	//	public void processReturningRowsEffected(CalcFrameStack sf, long rowsEffected) {
	//		Tableset tablesMap = sf.getTableset();
	//		if (tablesMap instanceof AmiImdbSession) {
	//			AmiImdbSession session = (AmiImdbSession) tablesMap;
	//			session.addRowsEffected(rowsEffected);
	//		}
	//	}
	@Override
	public void processReturningTable(CalcFrameStack sf, TableReturn r) {
		//		if (r == null)
		//			return;
		//		List<Table> tables = r.getTables();
		//		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		//		for (int n = 0, l = tables.size(); n < l; n++) {
		//			session.addReturnTable(tables.get(n));
		//		}
		//		session.addRowsEffected(r.getRowsEffected());

	}

	@Override
	public void processTriggerRename(CalcFrameStack sf, int fromPos, String from, int toPos, String to, int scope) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		session.assertCanAlter();
		if (scope != SqlExpressionParser.ID_PUBLIC && scope != SqlExpressionParser.ID_INVALID)
			throw new ExpressionParserException(fromPos, "TRIGGER not found in scope: " + SqlExpressionParser.toOperationString(scope));
		if (session.getImdb().getAmiTrigger(from) == null)
			throw new ExpressionParserException(fromPos, "TRIGGER not found: " + from);
		if (session.getImdb().getAmiTrigger(to) != null)
			throw new ExpressionParserException(toPos, "TRIGGER already exists: " + to);
		session.getObjectsManager().renameTrigger(from, to, sf);
	}
	@Override
	public void processTimerRename(CalcFrameStack sf, int fromPos, String from, int toPos, String to, int scope) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		session.assertCanAlter();
		if (scope != SqlExpressionParser.ID_PUBLIC && scope != SqlExpressionParser.ID_INVALID)
			throw new ExpressionParserException(fromPos, "TIMER not found in scope: " + SqlExpressionParser.toOperationString(scope));
		if (session.getImdb().getAmiTimer(from) == null)
			throw new ExpressionParserException(fromPos, "TIMER not found: " + from);
		if (session.getImdb().getAmiTimer(to) != null)
			throw new ExpressionParserException(toPos, "TIMER already exists: " + to);
		session.getObjectsManager().renameTimer(from, to, sf);

	}
	@Override
	public void processProcedureRename(CalcFrameStack sf, int fromPos, String from, int toPos, String to, int scope) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		session.assertCanAlter();
		if (session.getImdb().getAmiStoredProc(from) == null)
			throw new ExpressionParserException(fromPos, "PROCEDURE not found: " + from);
		if (session.getImdb().getAmiStoredProc(to) != null)
			throw new ExpressionParserException(toPos, "PROCEDURE already exists: " + to);
		session.getObjectsManager().renameProcedure(from, to, sf);
	}
	@Override
	public Table processColumnAdd(CalcFrameStack sf, int tableNamePos, String tableName, int typePos, String type, String varname, int position, int scope,
			Map<String, Node> options, Object[] vals) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		Table t = getTable(sf, position, tableName, scope);
		Map<String, Object> optionsMap = evaluateOptionsToMap(options, sf);
		byte parsedType = parseType(sf.getFactory(), typePos, type);
		if (t instanceof AmiHdbTableRep) {
			session.assertCanAlter();
			//TODO:
			session.getHdb().addColumn((AmiHdbTableRep) t, type, parsedType, varname, position, optionsMap, typePos, options, sf);
			return t;
		}
		AmiTableImpl table = getAmiTable(t);
		if (table == null) {
			return this.inner.processColumnAdd(sf, tableNamePos, tableName, typePos, type, varname, position, scope, options, vals);
		} else {
			session.assertCanAlter();
			//			if (!AmiUtils.isValidVariableName(varname, false, false))
			//				throw new ExpressionParserException(typePos, "Invalid column name: " + varname);
			AmiColumnImpl<?> col;
			try {
				col = table.addColumn(position, parsedType, varname, toStringMap2(optionsMap), sf);
			} catch (Exception e) {
				throw new ExpressionParserException(tableNamePos, "Could not add column " + t.getTitle() + ": " + e.getMessage(), e);
			}
			Caster caster = col.getColumn().getTypeCaster();
			if (vals != null) {
				AmiPreparedRow pr = table.createAmiPreparedRow();
				for (int n = 0, l = vals.length; n < l; n++) {
					AmiRowImpl amirow = table.getAmiRowAt(n);
					pr.reset();
					Object v = caster.cast(vals[n]);
					pr.putAt(position, v);
					table.updateAmiRow(amirow.getAmiId(), pr, sf);
				}
			}
			session.getImdb().onSchemaChanged(sf);
			return t;
		}
	}
	private Map<String, String> toMapOfStrings(Map<String, VariableNode> options) {
		if (CH.isEmpty(options))
			return Collections.EMPTY_MAP;
		HashMap<String, String> r = new HashMap<String, String>(options.size());
		for (Entry<String, VariableNode> e : options.entrySet()) {
			r.put(e.getKey(), e.getValue().getVarname());
		}
		return r;
	}
	@Override
	public Table processColumnRemove(CalcFrameStack sf, int tableNamePos, String tableName, String colname, int colNamePos, int scope) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		Table t = getTable(sf, tableNamePos, tableName, scope);

		if (t instanceof AmiHdbTableRep) {
			session.assertCanAlter();
			session.getHdb().removeColumn((AmiHdbTableRep) t, colname, colNamePos, sf);
			return t;
		}
		AmiTableImpl r = getAmiTable(t);
		if (r != null) {
			session.assertCanAlter();
			r.removeColumn(colname, sf);
			session.getImdb().onSchemaChanged(sf);
			return r.getTable();
		}
		return this.inner.processColumnRemove(sf, tableNamePos, tableName, colname, colNamePos, scope);
	}

	@Override
	public Table processColumnChangeType(CalcFrameStack sf, int tableNamePos, String tableName, int location, Class<?> type, int newTypePos, String newType, String newName,
			Map<String, Node> options, int scope) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		Table r = getTable(sf, tableNamePos, tableName, scope);
		byte parsedType = newType == null ? -1 : parseType(sf.getFactory(), newTypePos, newType);
		if (r instanceof AmiHdbTableRep) {
			session.assertCanAlter();
			session.getHdb().alterColumn((AmiHdbTableRep) r, location, parsedType, newName, newTypePos, options, sf);
			return r;
		}
		AmiTableImpl table = getAmiTable(r);
		if (table == null) {
			return this.inner.processColumnChangeType(sf, tableNamePos, tableName, location, type, newTypePos, newType, newName, options, scope);
		} else {
			session.assertCanAlter();
			Map<String, String> optionsMap = toStringMap2(evaluateOptionsToMap(options, sf));
			try {
				if (table.changeColumn(location, parsedType, newName, optionsMap, sf))
					session.getImdb().onSchemaChanged(sf);
			} catch (Exception e) {
				throw new ExpressionParserException(newTypePos, e.getMessage(), e);
			}
			return table.getTable();
		}
	}

	public static byte parseType(MethodFactoryManager mf, int typePos, String type) {
		if (AmiConsts.TYPE_NAME_ENUM.equalsIgnoreCase(type)) {
			return AmiTable.TYPE_ENUM;
		} else {
			try {
				return AmiUtils.getTypeForClass(mf.forName(type), AmiDatasourceColumn.TYPE_UNKNOWN);
			} catch (ClassNotFoundException e) {
				throw new ExpressionParserException(typePos, "Invalid type: " + type);
			}
		}
	}

	@Override
	public TempIndex findIndex(CalcFrameStack sf, Table targetTable, String[] targetColumns, int targetTablePos, Table sourceTable, String[] sourceColumns, List<Row> targetRows) {
		//		AmiTableImpl table = targetTable instanceof AmiTableImpl ? (AmiTableImpl) targetTable : null;//session.getObjectsManager().getAmiTable(targetTable.getTitle());
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		AmiTableImpl table = session.getObjectsManager().getAmiTable(targetTable.getTitle());
		if (table == null || table.getTable() != targetTable)
			return null;

		if (table.getRowsCount() == targetRows.size() && targetColumns.length > 0) {
			if (targetColumns.length == 1) {
				AmiPreparedQueryImpl pq = new AmiPreparedQueryImpl(table);
				AmiColumnImpl<?> col = table.getColumn(targetColumns[0]);
				AmiPreparedQueryCompareClause eq = pq.addEq(col);
				if (pq.getIndex() != null)
					return new AmiTempIndexWrapper(targetTablePos, eq, pq, sourceTable.getColumn(sourceColumns[0]).getLocation());
			} else {
				AmiPreparedQueryCompareClause eq[] = new AmiPreparedQueryCompareClause[targetColumns.length];
				int sourceColumnLocations[] = new int[targetColumns.length];
				AmiPreparedQueryImpl pq = new AmiPreparedQueryImpl(table);
				for (int i = 0; i < targetColumns.length; i++) {
					AmiColumnImpl<?> col = table.getColumn(targetColumns[i]);
					eq[i] = pq.addEq(col);
					sourceColumnLocations[i] = sourceTable.getColumn(sourceColumns[i]).getLocation();
				}
				if (pq.getIndex() != null)
					return new AmiTempIndexWrapperMultiCol(targetTablePos, eq, pq, sourceColumnLocations);
			}
		}
		return inner.findIndex(sf, targetTable, targetColumns, targetTablePos, sourceTable, sourceColumns, targetRows);
	}

	public class AmiTempIndexWrapper implements TempIndex {

		final private AmiPreparedQueryImpl inner;
		final private List<AmiRow> sink = new ArrayList();
		final private AmiPreparedQueryCompareClause eq;
		final private int sourceColPos;
		final private int targetTablePos;
		final private AmiQueryFinderVisitor fv;

		public AmiTempIndexWrapper(int targetTablePos, AmiPreparedQueryCompareClause eq, AmiPreparedQueryImpl pq, int sourceColPos) {
			this.inner = pq;
			this.eq = eq;
			this.targetTablePos = targetTablePos;
			this.sourceColPos = sourceColPos;
			this.fv = new AmiQueryFinderVisitor(inner);
		}

		@Override
		public int getTargetTablePosition() {
			return this.targetTablePos;
		}

		@Override
		public List<Row> getRows(Row sourceRow) {
			sink.clear();
			eq.setValue((Comparable) sourceRow.getAt(sourceColPos));
			this.fv.find(sink, Integer.MAX_VALUE);
			return (List) sink;
		}

		@Override
		public int getUniqueValuesCount() {
			return 0;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public String toString() {
			return this.inner.toString();
		}

		@Override
		public Object getKey(Row sourceRow) {
			return sourceRow.getAt(sourceColPos);
		}

	}

	public class AmiTempIndexWrapperMultiCol implements TempIndex {

		final private AmiPreparedQueryImpl inner;
		final private List<AmiRow> sink = new ArrayList();
		final private AmiPreparedQueryCompareClause eq[];
		final private int sourceColPos[];
		final private int targetTablePos;
		final private AmiQueryFinderVisitor fv;

		public AmiTempIndexWrapperMultiCol(int targetTablePos, AmiPreparedQueryCompareClause[] eq, AmiPreparedQueryImpl pq, int sourceColPos[]) {
			this.inner = pq;
			this.eq = eq;
			this.targetTablePos = targetTablePos;
			this.sourceColPos = sourceColPos;
			this.fv = new AmiQueryFinderVisitor(inner);
		}

		@Override
		public int getTargetTablePosition() {
			return this.targetTablePos;
		}

		@Override
		public List<Row> getRows(Row sourceRow) {
			sink.clear();
			for (int i = 0; i < sourceColPos.length; i++)
				eq[i].setValue((Comparable) sourceRow.getAt(sourceColPos[i]));
			this.fv.find(sink, Integer.MAX_VALUE);
			return (List) sink;
		}

		@Override
		public int getUniqueValuesCount() {
			return 0;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public String toString() {
			return this.inner.toString();
		}

		@Override
		public Object getKey(Row sourceRow) {
			Object tmpKey[] = new Object[sourceColPos.length];
			for (int i = 0; i < tmpKey.length; i++)
				tmpKey[i] = sourceRow.getAt(sourceColPos[i]);
			return tmpKey;
		}

	}

	@Override
	public Table getTableIfExists(CalcFrameStack sf, String tableName, int scope) {
		Tableset tablesMap = sf.getTableset();
		if (tablesMap instanceof AmiImdbSession) {
			AmiImdbSession session = (AmiImdbSession) tablesMap;
			if (scope == SqlExpressionParser.ID_PUBLIC) {
				Table r = session.getPublicTableNoThrow(tableName);
				return r != null ? r : session.getHistoricalTableNoThrow(tableName);
			} else if (scope == SqlExpressionParser.ID_TEMPORARY) {
				return session.getLocalTableNoThrow(tableName);
			} else if (scope == SqlExpressionParser.ID_VARIABLE) {
				return session.getVariableTableNoThrow(tableName, sf.getGlobal());
			} else {
				Table r = session.getTableNoThrow(tableName);
				if (r != null)
					return r;
				Class<?> t = DerivedHelper.getType(sf, tableName);
				if (t != null && Table.class.isAssignableFrom(t))
					return (Table) DerivedHelper.getValue(sf, tableName);
				else
					return null;
			}
		} else
			return tablesMap.getTable(tableName);
	}

	@Override
	public Table getTable(CalcFrameStack sf, int position, String tableName, int scope) {
		Table r = getTableIfExists(sf, tableName, scope);
		if (r == null) {
			if (scope == SqlExpressionParser.ID_PUBLIC)
				throw new ExpressionParserException(position, "Unknown PUBLIC table: " + tableName);
			else if (scope == SqlExpressionParser.ID_TEMPORARY)
				throw new ExpressionParserException(position, "Unknown TEMPORARY table: " + tableName);
			else if (scope == SqlExpressionParser.ID_VARIABLE) {
				throw new ExpressionParserException(position, "Unknown VARIABLE table: " + tableName);
			} else {
				Class<?> t = DerivedHelper.getType(sf, tableName);
				if (t == null)
					throw new ExpressionParserException(position, "Unknown table: " + tableName);
				else if (Table.class.isAssignableFrom(t))
					throw new ExpressionParserException(position, "Table is null: " + tableName);
				else
					throw new ExpressionParserException(position, "Variable is not a table: " + tableName);
			}
		}
		return r;
	}

	@Override
	public Map processDiagnoseColumn(CalcFrameStack sf, int scope, Table table, Column column) {
		return this.inner.processDiagnoseColumn(sf, scope, table, column);
	}

	@Override
	public Set<String> getIndexes(CalcFrameStack sf, Table targetTable) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		AmiTableImpl table = session.getObjectsManager().getAmiTable(targetTable.getTitle());
		if (table == null)
			return inner.getIndexes(sf, targetTable);
		return table.getAmiIndexNames();
	}

	@Override
	public Map processDiagnoseIndex(CalcFrameStack sf, int scope, Table targetTable, int indexNamePos, String indexName) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		AmiTableImpl table = session.getObjectsManager().getAmiTable(targetTable.getTitle());
		if (table == null)
			return inner.processDiagnoseIndex(sf, scope, targetTable, indexNamePos, indexName);
		session.assertCanRead();
		AmiIndexImpl index = (AmiIndexImpl) table.getAmiIndex(indexName);
		long size = index.getMemorySize();

		int stats[] = new int[5];
		getStats(index.getRootMap(), stats);
		List<String> comments = new ArrayList<String>();
		if (stats[0] != 0)
			comments.add(stats[0] + " HASHMAP");
		if (stats[1] != 0)
			comments.add(stats[1] + " TREEMAP");
		if (stats[2] != 0)
			comments.add(stats[2] + " SERIES");
		if (stats[3] != 0)
			comments.add(stats[3] + " ROWPOINTER");
		if (stats[4] != 0)
			comments.add(stats[4] + " ROWMAP");
		int cardinality = stats[3] + stats[4];
		return CH.m("EST_MEMORY", size, "TYPE", "INDEX", "COUNT", targetTable.getSize(), "CARDINALITY", cardinality, "COMMENT", SH.join(" + ", comments));
	}

	private void getStats(AmiIndexMap m, int[] stats) {
		if (m instanceof AmiIndexMap_Hash)
			stats[0]++;
		else if (m instanceof AmiIndexMap_Tree)
			stats[1]++;
		else if (m instanceof AmiIndexMap_Series)
			stats[2]++;
		else if (m instanceof AmiIndexMap_Rows)
			stats[((AmiIndexMap_Rows) m).getSingleValue() != null ? 3 : 4]++;
		for (AmiIndexMap i : m.getValuesForDebug())
			getStats(i, stats);
	}

	@Override
	public void processMethodCreate(CalcFrameStack sf, int pos, List<MethodFactory> factories, boolean ifNotExists) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		session.assertCanAlter();
		AmiImdbScriptManager sm = session.getImdb().getScriptManager();
		final BasicMethodFactory mf;
		switch (session.getDefinedBy()) {
			case AmiTableUtils.DEFTYPE_USER:
				mf = sm.getManagedMethodFactory();
				break;
			case AmiTableUtils.DEFTYPE_CONFIG:
				mf = sm.getDeclaredMethodFactory();
				break;
			default:
				throw new RuntimeException("Bad DefinedBy: " + session.getDefinedBy());
		}
		for (MethodFactory factory : factories) {
			MethodFactory exists = sm.getMethodFactory().getMethodFactory(factory.getDefinition().getMethodName(), factory.getDefinition().getParamTypes());
			if (exists != null && !ifNotExists)
				throw new ExpressionParserException(pos, "Method already exists: " + factory.getDefinition().toString(sm.getMethodFactory()));
		}
		for (MethodFactory factory : factories) {
			MethodFactory exists = sm.getMethodFactory().getMethodFactory(factory.getDefinition().getMethodName(), factory.getDefinition().getParamTypes());
			if (exists == null)
				mf.addFactory(factory);
		}
		session.getImdb().onSchemaChanged(sf);
	}

	@Override
	public void processMethodDrop(CalcFrameStack sf, int pos, String methodName, Class[] types, boolean ifExists) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		session.assertCanAlter();
		AmiImdbScriptManager sm = session.getImdb().getScriptManager();
		MethodFactory exists = sm.getManagedMethodFactory().getMethodFactory(methodName, types);
		if (exists == null && !ifExists)
			throw new ExpressionParserException(pos, "Method not found");
		else if (exists != null)
			sm.getManagedMethodFactory().removeFactory(exists);
		session.getImdb().onSchemaChanged(sf);
		//		throw new ExpressionParserException(pos, "Method already exists: " + factory.getDefinition().toString(sm.getMethodFactory()));
	}

	@Override
	public void processDiagnoseTable(CalcFrameStack sf, int scope, Table table, ColumnarTable r) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		session.assertCanRead();
		if (table instanceof AmiHdbTableRep) {
			session.getHdb().diagnoseTable((AmiHdbTableRep) table, r);
			return;
		}
		this.inner.processDiagnoseTable(sf, scope, table, r);
	}

	@Override
	public TableReturn processRowAdds(CalcFrameStack sf, Table table, int tableNamePos, ColumnPositionMapping posMapping, int startRow, int rowsCount, Table values,
			boolean returnGeneratedIds) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		AmiTableImpl t = getAmiTable(table);
		if (t == null) {
			return inner.processRowAdds(sf, table, tableNamePos, posMapping, startRow, rowsCount, values, returnGeneratedIds);
		}
		session.assertCanWrite();
		assertIsUser(t);
		AmiPreparedRow pr = t.createAmiPreparedRow();
		if (t.hasReservedColumns()) {
			posMapping = removeReservedPositions(t, posMapping, AmiColumnImpl.CAN_INSERT);
		}
		t.getTable().ensureCapacity(rowsCount + t.getRowsCount());
		TableList rows = values.getRows();
		int colsCount = posMapping.getPosCount();
		AmiIndexImpl pi = t.getPrimaryIndex();
		int startSize = t.getRowsCount();
		int endRow = startRow + rowsCount;
		if (values instanceof ColumnarTable) {
			ColumnarTable colTable = (ColumnarTable) values;
			PreparedRowCopier[] copiers = new PreparedRowCopier[colsCount];
			for (int j = 0; j < colsCount; j++)
				copiers[j] = PreparedRowCopierManager.getRowCopier(colTable.getColumnAt(posMapping.getSourcePosAt(j)), t.getColumnAt(posMapping.getTargetPosAt(j)));
			if (returnGeneratedIds) {
				List<Object> keys = new ArrayList<Object>();
				for (int i = startRow; i < endRow; i++) {
					ColumnarRow row = (ColumnarRow) colTable.getRow(i);
					pr.reset();
					for (int j = 0; j < colsCount; j++)
						copiers[j].copy(row, pr);
					AmiRowImpl row2 = t.insertAmiRow(pr, sf);
					if (row2 != null) {
						if (pi != null)
							keys.add(pi.getColumn(0).getComparable(row2));
						else if (t.getReservedColumnId() != null)
							keys.add(t.getReservedColumnId().getComparable(row2));
						else
							keys.add(row2.getAmiId());
					}
				}
				return new TableReturn(t.getRowsCount() - startSize, keys);
			} else {
				for (int i = startRow; i < endRow; i++) {
					ColumnarRow row = (ColumnarRow) colTable.getRow(i);
					pr.reset();
					for (int j = 0; j < colsCount; j++)
						copiers[j].copy(row, pr);
					AmiRowImpl row2 = t.insertAmiRow(pr, sf);
				}
				return new TableReturn(t.getRowsCount() - startSize);
			}
		} else if (posMapping.isStraight() && !returnGeneratedIds) {//base case, make this fast!
			for (int i = startRow; i < endRow; i++) {
				Row row = rows.get(i);
				pr.reset();
				for (int j = 0; j < colsCount; j++)
					pr.putAt(j, row.getAt(j));
				t.insertAmiRow(pr, sf);
			}
			return new TableReturn(t.getRowsCount() - startSize);
		} else {
			if (returnGeneratedIds) {
				List<Object> keys = new ArrayList<Object>();
				for (int i = startRow; i < endRow; i++) {
					Row row = rows.get(i);
					pr.reset();
					for (int j = 0; j < colsCount; j++)
						pr.putAt(posMapping.getTargetPosAt(j), row.getAt(posMapping.getSourcePosAt(j)));
					AmiRowImpl row2 = t.insertAmiRow(pr, sf);
					if (row2 != null) {
						if (pi != null)
							keys.add(pi.getColumn(0).getComparable(row2));
						else if (t.getReservedColumnId() != null)
							keys.add(t.getReservedColumnId().getComparable(row2));
						else
							keys.add(row2.getAmiId());
					}
				}
				return new TableReturn(t.getRowsCount() - startSize, keys);
			} else {
				for (int i = startRow; i < endRow; i++) {
					Row row = rows.get(i);
					pr.reset();
					for (int j = 0; j < colsCount; j++)
						pr.putAt(posMapping.getTargetPosAt(j), row.getAt(posMapping.getSourcePosAt(j)));
					AmiRowImpl row2 = t.insertAmiRow(pr, sf);
				}
				return new TableReturn(t.getRowsCount() - startSize);
			}
		}
	}

	@Override
	public void processDboCreate(CalcFrameStack sf, String dboName, int dboNamePos, String typeName, int typeNamePos, int priority, Map<String, Node> useOptions,
			boolean ifNotExists) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		session.assertCanAlter();
		if (AmiUtils.isResevedTableName(dboName))
			throw new ExpressionParserException(dboNamePos, "Security Violation: Dbo name is reserved: " + dboName);
		AmiDboFactoryWrapper factory = session.getImdb().getDboFactory(typeName);
		Set<String> visited = new HashSet<String>();
		if (session.getObjectsManager().getAmiDbo(dboName) != null) {
			if (ifNotExists)
				return;
			throw new ExpressionParserException(dboNamePos, "Dbo already exists: " + dboName);
		}
		if (factory == null)
			throw new ExpressionParserException(typeNamePos, "Invalid dbo type '" + typeName + "' , existing dbo types: " + session.getImdb().getDboTypes());
		Map<String, Object> useOptionsMap = AmiUtils.processOptions(typeNamePos, useOptions, factory, this.getParser(), sf, true);
		AmiDbo dbo = factory.newDbo();

		AmiDboBindingImpl tb = new AmiDboBindingImpl(dboName, dbo, priority, factory, useOptionsMap, toStringMap2(useOptionsMap), getDefinedBy(sf));
		if (session.getImdb().isStartupComplete()) {
			try {
				tb.startup(session.getImdb(), sf);
			} catch (Exception e) {
				if (SH.is(e.getMessage()))
					throw new ExpressionParserException(dboNamePos, "For " + tb.getDboType() + ": " + e.getMessage(), e);
				else
					throw new ExpressionParserException(dboNamePos, "For " + tb.getDboType() + ": Invalid trigger configuration", e);
			}
			session.getObjectsManager().addDbo(tb, sf);
			session.getImdb().onSchemaChanged(sf);
		} else
			session.getObjectsManager().addDbo(tb, sf);

	}
	@Override
	public void processDboRename(CalcFrameStack sf, int fromPos, String from, int toPos, String to, int scope) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		session.assertCanAlter();
		if (scope != SqlExpressionParser.ID_PUBLIC && scope != SqlExpressionParser.ID_INVALID)
			throw new ExpressionParserException(fromPos, "DBO not found in scope: " + SqlExpressionParser.toOperationString(scope));
		if (session.getImdb().getAmiDbo(from) == null)
			throw new ExpressionParserException(fromPos, "DBO not found: " + from);
		if (session.getImdb().getAmiDbo(to) != null)
			throw new ExpressionParserException(toPos, "DBO already exists: " + to);
		session.getObjectsManager().renameDbo(from, to, sf);
	}

	@Override
	public void processDboRemove(CalcFrameStack sf, String dboName, int dboNamePos, boolean ifExists) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		session.assertCanAlter();
		AmiDboBindingImpl dbo = session.getObjectsManager().getAmiDboBinding(dboName);
		if (dbo == null) {
			if (ifExists)
				return;
			throw new ExpressionParserException(dboNamePos, "Dbo not found: " + dboName);
		}
		session.getObjectsManager().removeAmiDbo(dboName, sf);
	}

	@Override
	public void processAlterUseOptions(CalcFrameStack sf, int targetType, String name, int position, Map<String, Node> useOptions) {
		if (targetType == SqlExpressionParser.ID_DBO) {
			AmiImdbSession session = AmiCenterUtils.getSession(sf);
			session.assertCanAlter();
			AmiDboBindingImpl dbo = session.getObjectsManager().getAmiDboBinding(name);
			if (dbo == null)
				throw new ExpressionParserException(position, "DBO not found: " + name);
			Map<String, Object> useOptionsMap = AmiUtils.processOptions(position, useOptions, dbo.getFactory(), this.getParser(), sf, false);
			for (Entry<String, Object> entry : useOptionsMap.entrySet())
				dbo.updateOption(entry.getKey(), entry.getValue(), sf);
		} else
			throw new ExpressionParserException(position, "ALTER ... USE only supported for DBO types, not" + SqlExpressionParser.toOperationString(targetType));
	}

	@Override
	public Iterable<Row> findSortIndex(CalcFrameStack sf, Table table, String columnName, boolean asc) {
		AmiTableImpl t = getAmiTable(table);
		if (t == null) {
			return inner.findSortIndex(sf, table, columnName, asc);
		}
		AmiColumnImpl<?> col = t.getColumnNoThrow(columnName);
		if (col == null)
			return null;
		if (!t.isColumnLocationParticipatingInIndex(col.getLocation()))
			return null;
		for (int i = 0; i < t.getIndexesCount(); i++) {
			AmiIndexImpl idx = t.getIndexAt(i);
			if (idx.getColumnsCount() == 1 && idx.getColumn(0) == col && idx.getIndexTypeAt(0) == AmiIndex.TYPE_SORT) {
				AmiIndexMap_Tree tree = (AmiIndexMap_Tree) idx.getRootMap();
				return new TreeIterable(tree, asc);
			}
		}
		return null;
	}
	@Override
	public boolean hasIndex(CalcFrameStack sf, Table table, String columnName) {
		final AmiTableImpl t = getAmiTable(table);
		if (t == null)
			return inner.hasIndex(sf, table, columnName);
		final AmiColumnImpl<?> col = t.getColumnNoThrow(columnName);
		return col != null && t.isColumnLocationParticipatingInIndex(col.getLocation());
	}

	public static class TreeIterable implements Iterable<Row> {

		private AmiIndexMap_Tree tree;
		private boolean asc;

		public TreeIterable(AmiIndexMap_Tree tree, boolean asc) {
			this.tree = tree;
			this.asc = asc;
		}

		@Override
		public Iterator<Row> iterator() {
			return new TreeIterator(tree, asc);
		}

	}

	public static class TreeIterator implements Iterator<Row> {
		private SingletonIterator<AmiRowImpl> buf = new SingletonIterator<AmiRowImpl>(null);

		private Iterator<AmiIndexMap> iterator;
		private Iterator<AmiRowImpl> rowsCursor;
		private AmiRowImpl next;

		public TreeIterator(AmiIndexMap_Tree tree, boolean asc) {
			if (asc)
				this.iterator = tree.values().iterator();
			else
				this.iterator = tree.descendingMap().values().iterator();
			walk();
		}

		private void walk() {
			next = null;
			for (;;) {
				if (rowsCursor != null && rowsCursor.hasNext()) {
					next = rowsCursor.next();
					return;
				}
				if (!this.iterator.hasNext()) {
					next = null;
					return;
				}
				AmiIndexMap_Rows rows = (AmiIndexMap_Rows) this.iterator.next();
				if (rows.getKeysCount() == 1)
					rowsCursor = buf.reset(rows.getSingleValue());
				else
					rowsCursor = rows.getValues().iterator();
			}
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public Row next() {
			final AmiRowImpl r = next;
			walk();
			return r;
		}

	}

}
