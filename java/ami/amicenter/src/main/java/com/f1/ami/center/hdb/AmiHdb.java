package com.f1.ami.center.hdb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiRelayObjectMessage;
import com.f1.ami.center.AmiCenterProperties;
import com.f1.ami.center.AmiCenterState;
import com.f1.ami.center.AmiCenterUtils;
import com.f1.ami.center.hdb.events.AmiHdbTableState;
import com.f1.ami.center.hdb.idx.AmiHdbPartitionIndex;
import com.f1.ami.center.hdb.qry.AmiHdbQueryImpl;
import com.f1.ami.center.table.AmiCenterSqlProcessorMutator;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.container.OutputPort;
import com.f1.container.Partition;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.Node;
import com.f1.utils.structs.table.columnar.ColumnarRow;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorRef;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.EmptyCalcFrameStack;

public class AmiHdb {

	private static final Logger log = LH.get();

	final private Map<String, AmiHdbTable> tables = new TreeMap<String, AmiHdbTable>(SH.COMPARATOR_CASEINSENSITIVE_STRING);
	final private AmiCenterState state;
	final private File topDir;
	final private int maxOpenCount;
	private OutputPort<AmiRelayObjectMessage> output;
	//	final private ObjectGeneratorForClass<AmiHdbRtEvent> rtEventFactory;

	final private int diskBlockSize;

	public AmiHdb(AmiCenterState state) {
		this.state = state;
		ContainerTools tools = this.state.getTools();
		maxOpenCount = tools.getOptional(AmiCenterProperties.PROPERTY_AMI_HDB_FILEHANDLES_MAX, 32);
		topDir = tools.getOptional(AmiCenterProperties.PROPERTY_AMI_HDB_ROOT_DIR, new File("hdb"));
		diskBlockSize = tools.getOptional(AmiCenterProperties.PROPERTY_AMI_HDB_BLOCKSIZE, 8192);
	}

	public void setRtEventPort(OutputPort<AmiRelayObjectMessage> out) {
		this.output = out;
	}

	private void onSchemaChanged(CalcFrameStack sf) {
		this.state.getAmiImdb().onSchemaChanged(sf);
	}
	public Table processTableAdd(MethodFactoryManager mf, String name, int namePos, String[] types, String[] names, Map<String, Node>[] colOptions, int[] colDefPos,
			Map<String, Node> useOptions, int scope, boolean ifNotExists, CalcFrameStack sf) {
		int maxOptimizeSeconds = -1;
		double minOptimizePct = 1;
		for (Entry<String, Node> s : useOptions.entrySet()) {
			Object sval = AmiUtils.getNodeValue(s.getValue(), this.state.getGlobalSession().getSqlProcessor().getParser(), sf);
			if (AmiHdbUtils.OPTION_MAX_OPTIMIZE_SECONDS.equalsIgnoreCase(s.getKey())) {
				try {
					maxOptimizeSeconds = SH.parseInt(sval.toString());
				} catch (Exception e) {
					throw new ExpressionParserException(s.getValue().getPosition(), AmiHdbUtils.OPTION_MAX_OPTIMIZE_SECONDS + " must be valid number: " + sval);
				}
				if (maxOptimizeSeconds < 0)
					throw new ExpressionParserException(s.getValue().getPosition(), AmiHdbUtils.OPTION_MAX_OPTIMIZE_SECONDS + " must not be negative: " + sval);
			} else if (AmiHdbUtils.OPTION_MIN_OPTIMIZE_PCT.equalsIgnoreCase(s.getKey())) {
				try {
					minOptimizePct = SH.parseDouble(sval.toString());
				} catch (Exception e) {
					throw new ExpressionParserException(s.getValue().getPosition(), AmiHdbUtils.OPTION_MIN_OPTIMIZE_PCT + " must be valid number: " + sval);
				}
				if (minOptimizePct < 0 || minOptimizePct > 1)
					throw new ExpressionParserException(s.getValue().getPosition(), AmiHdbUtils.OPTION_MIN_OPTIMIZE_PCT + " must be between 0 to 1: " + sval);
			} else if (!"PersistEngine".equalsIgnoreCase(s.getKey()))
				throw new ExpressionParserException(s.getValue().getPosition(), "HISTORICAL does not support USE option: " + s.getKey());
		}
		AmiHdbTable table = this.tables.get(name);
		if (table != null) {
			if (ifNotExists)
				return null;
			else
				throw new ExpressionParserException(namePos, "HISTORICAL Table already exists: " + name);
		}
		List<AmiHdbColumnDef> types2 = new ArrayList<AmiHdbColumnDef>(types.length);
		for (int i = 0; i < types.length; i++) {
			Map<String, Node> options = colOptions[i];
			byte type = AmiCenterSqlProcessorMutator.parseType(mf, colDefPos[i], types[i]);
			byte mode;
			if (options == null || options.size() == 0) {
				mode = AmiHdbUtils.getDefaultMode(type);
			} else if (options.size() > 1) {
				throw new ExpressionParserException(colDefPos[i], "Invalid option combination " + SH.toUpperCase(SH.join('+', options.keySet())) + " for column " + names[i]);
			} else {
				String option = SH.toUpperCase(CH.first(options.keySet()));
				mode = AmiHdbUtils.parseMode(option);
				if (mode == -1)
					throw new ExpressionParserException(colDefPos[i], "Unknown option '" + option + "' for column " + names[i]);
			}

			if (!AmiHdbUtils.isValidTypeMode(type, mode))
				throw new ExpressionParserException(colDefPos[i], "Invalid mode " + AmiHdbUtils.toStringForMode(mode) + " for " + types[i]);
			String colName = names[i];
			short colNameAmiKey = state.getAmiKeyId(colName);
			types2.add(new AmiHdbColumnDef(colNameAmiKey, colName, type, mode));
		}
		AmiHdbTable r;
		try {
			Partition p = state.getTools().getContainer().getPartitionController().getOrCreatePartition(AmiHdbTable.F1PARTITION_PREFIX + name);
			p.lockForWrite(AmiHdbTable.DEFAULT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
			AmiHdbTableState s;
			try {
				s = (AmiHdbTableState) p.getState(AmiHdbTableState.class);
				if (s == null)
					p.putState(s = new AmiHdbTableState());
				if (s.getTable() != null)
					throw new IllegalStateException("State already assigned to table: " + s.getTableName() + " ==> " + s.getTable());
				r = new AmiHdbTable(topDir, name, types2, Collections.EMPTY_MAP, AmiCenterUtils.getSession(sf).getDefinedBy(), maxOpenCount, maxOptimizeSeconds, this.diskBlockSize,
						minOptimizePct);
				s.setTable(r);
			} finally {
				p.unlockForWrite();
			}
			CH.putOrThrow(tables, name, r);
		} catch (Exception e) {
			throw new ExpressionParserException(namePos, e.getMessage(), e);
		}
		onSchemaChanged(sf);
		return r.getTable();

	}

	//	public AmiHdbTableRep processTableAdd(SqlProcessor sqlProcessor, String name, int namePosition, AmiImdbSession tablesMap, Table r, Map<String, VariableNode> useOptions,
	//			int scope, boolean ifNotExists, AmiImdbSession session) {
	//		for (Entry<String, VariableNode> s : useOptions.entrySet())
	//			if (!"PersistEngine".equalsIgnoreCase(s.getKey()))
	//				throw new ExpressionParserException(s.getValue().getPosition(), "HISTORICAL does not support USE option: " + s.getKey());
	//		AmiHdbTable table = this.tables.getTable(name);
	//		if (table != null) {
	//			if (ifNotExists)
	//				return null;
	//			else
	//				throw new ExpressionParserException(namePosition, "HISTORICAL Table already exists: " + name);
	//		}
	//		List<AmiHdbColumnDef> types2 = new ArrayList<AmiHdbColumnDef>(r.getColumnsCount());
	//		for (int i = 0; i < r.getColumnsCount(); i++) {
	//			Column col = r.getColumnAt(i);
	//			byte type = AmiUtils.getTypeForClass(col.getType());
	//			byte mode = AmiHdbUtils.getDefaultMode(type);
	//			types2.add(new AmiHdbColumnDef((String) col.getId(), type));
	//		}
	//		AmiHdbTable t;
	//		try {
	//			t = tables.addTable(name, types2, Collections.EMPTY_MAP, tablesMap.getDefinedBy());
	//			t.addRows(r);
	//		} catch (Exception e) {
	//			throw new ExpressionParserException(namePosition, e.getMessage(), e);
	//		}
	//		onSchemaChanged(session);
	//		return t.getTable();
	//	}

	//	public AmiHdbTable getTableNoThrow(String name) {
	//		return this.tables.getTable(name);
	//	}

	public Collection<String> getTablesSorted() {
		return CH.sort(this.tables.keySet());
	}

	public void addIndex(AmiHdbTableRep table, byte definedBy, String idxName, int idxNamePos, String column, boolean ifNotExists, CalcFrameStack sf) {
		AmiHdbTable hTable = ((AmiHdbTableRep) table).getHistoricalTable();
		hTable.lock(null);
		try {
			if (hTable.getIndexes().getNoThrow(idxName) != null) {
				if (ifNotExists)
					return;
				throw new ExpressionParserException(idxNamePos, "Index already exists on table: " + idxName);
			}
			hTable.addIndex(definedBy, idxName, column);
		} catch (Exception e) {
			throw new ExpressionParserException(idxNamePos, e.getMessage(), e);
		} finally {
			hTable.unlock();
		}
		onSchemaChanged(sf);
	}

	public void removeIndex(AmiHdbTableRep t, String indexName, int indexNamePos, boolean ifExists, CalcFrameStack sf) {
		AmiHdbTable hTable = t.getHistoricalTable();
		hTable.lock(null);
		try {
			AmiHdbIndex index = hTable.getIndexes().getNoThrow(indexName);
			if (index == null) {
				if (ifExists)
					return;
				throw new ExpressionParserException(indexNamePos, "Index not found: " + indexName);
			}
			hTable.removeIndex(indexName);
		} catch (Exception e) {
			throw new ExpressionParserException(indexNamePos, e.getMessage(), e);
		} finally {
			hTable.unlock();
		}
		onSchemaChanged(sf);
	}

	public void addColumn(AmiHdbTableRep t, String type, byte parsedType, String name, int position, Map<String, Object> optionsMap, int typePos, Map<String, Node> options,
			CalcFrameStack sf) {
		AmiHdbTable hTable = t.getHistoricalTable();
		hTable.lock(null);
		try {
			byte mode;
			if (options == null || options.size() == 0) {
				mode = AmiHdbUtils.getDefaultMode(parsedType);
			} else {
				String option = SH.toUpperCase(CH.first(options.keySet()));
				mode = AmiHdbUtils.parseMode(option);
				if (mode == -1)
					throw new ExpressionParserException(typePos, "Unknown option " + option);
				if (!AmiHdbUtils.isValidTypeMode(parsedType, mode))
					throw new ExpressionParserException(typePos, "Invalid mode " + AmiHdbUtils.toStringForMode(mode) + " for " + type);
			}
			short nameAmiKey = state.getAmiKeyId(name);
			hTable.addColumn(position, new AmiHdbColumnDef(nameAmiKey, name, parsedType, mode));
		} catch (Exception e) {
			throw new ExpressionParserException(typePos, e.getMessage(), e);
		} finally {
			hTable.unlock();
		}
		onSchemaChanged(sf);
	}
	public void removeColumn(AmiHdbTableRep table, String columnName, int columnNamePos, CalcFrameStack sf) {
		AmiHdbTable hTable = table.getHistoricalTable();
		hTable.lock(null);
		try {
			hTable.removeColumn(columnName);
		} catch (Exception e) {
			throw new ExpressionParserException(columnNamePos, e.getMessage(), e);
		} finally {
			hTable.unlock();
		}
		onSchemaChanged(sf);
	}

	public void processTableRemove(AmiHdbTableRep table, int tableNamePos, CalcFrameStack sf) {
		AmiHdbTable htable = ((AmiHdbTableRep) table).getHistoricalTable();
		htable.lock(null);
		try {
			htable.clearRows();
			htable.getDirectory().delete();
			this.tables.remove(htable.getName());
			state.getTools().getContainer().getPartitionController().removePartition(htable.getF1PartitionId());
		} catch (Exception e) {
			throw new ExpressionParserException(tableNamePos, e.getMessage(), e);
		} finally {
			htable.unlock();
		}
		onSchemaChanged(sf);

	}

	public void alterColumn(AmiHdbTableRep r, int location, byte parsedType, String newName, int newTypePos, Map<String, Node> options, CalcFrameStack sf) {
		AmiHdbTable hTable = r.getHistoricalTable();
		short newNameAmiKey = newName == null ? -1 : state.getAmiKeyId(newName);
		hTable.lock(null);
		try {
			byte mode;
			if (parsedType == -1) {
				AmiHdbColumn col = hTable.getColumnAt(location);
				parsedType = col.getAmiType();
				mode = col.getMode();
			} else {
				if (options == null || options.size() == 0) {
					mode = AmiHdbUtils.getDefaultMode(parsedType);
				} else {
					String option = SH.toUpperCase(CH.first(options.keySet()));
					mode = AmiHdbUtils.parseMode(option);
					if (mode == -1)
						throw new ExpressionParserException(newTypePos, "Unknown option " + option);
				}
			}
			hTable.alterColumn(location, parsedType, mode, newNameAmiKey, newName);
		} catch (Exception e) {
			throw new ExpressionParserException(newTypePos, e.getMessage(), e);
		} finally {
			hTable.unlock();
		}
		onSchemaChanged(sf);
	}

	//	public void flushPersisted() {
	//		try {
	//			this.tables.flushPersisted();
	//		} catch (Exception e) {
	//			LH.warning(log, "CRITICAL ISSUE, FLUSH FAILED: ", e);
	//		}
	//	}

	public void renameTable(AmiHdbTableRep t, String to, int toTableNamePos, CalcFrameStack sf) {
		AmiHdbTable table = t.getHistoricalTable();
		String oldName = table.getName();
		String oldPartitionid = table.getF1PartitionId();
		table.lock(null);
		try {
			table.renameTo(to);
			this.tables.remove(oldName);
			this.tables.put(table.getName(), table);
		} catch (Exception e) {
			throw new ExpressionParserException(toTableNamePos, e.getMessage(), e);
		} finally {
			table.unlock();
		}
		state.getTools().getContainer().getPartitionController().removePartition(oldPartitionid);
		Partition p = state.getTools().getContainer().getPartitionController().getOrCreatePartition(AmiHdbTable.F1PARTITION_PREFIX + to);
		p.lockForWrite(AmiHdbTable.DEFAULT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
		try {
			AmiHdbTableState s = (AmiHdbTableState) p.getState(AmiHdbTableState.class);
			if (s == null)
				p.putState(s = new AmiHdbTableState());
			s.setTable(table);
		} catch (Exception e) {
			throw new ExpressionParserException(toTableNamePos, e.getMessage(), e);
		} finally {
			p.unlockForWrite();
		}
		onSchemaChanged(sf);
	}

	public AmiHdbSchema_Table getTableSchema(String table) {
		return this.tables.get(table).getSqlSchemaThreadSafe();
	}

	public void markPartitionForAppend(String name, Integer pid, TimeoutController tc) {
		AmiHdbTable table = tables.get(name);
		if (table == null)
			throw new ExpressionParserException(0, "HISTORICAL Table not found: " + name);
		table.lock(tc);
		try {
			AmiHdbPartition partition = pid == null ? null : table.getPartitionById(pid);
			if (partition == null)
				throw new ExpressionParserException(0, "PARITION not not found: " + pid);
			if (!table.movePartitinToWritable(partition))
				throw new ExpressionParserException(0,
						"TABLE " + name + " already has PARITION IN_APPEND_MODE for parition key: [" + SH.join(",", (Object[]) partition.getPartitionsKey()) + "]");
		} finally {
			table.unlock();
		}
	}

	public void optimizeTable(String name, TimeoutController tc) {
		AmiHdbTable table = tables.get(name);
		if (table == null)
			throw new ExpressionParserException(0, "HISTORICAL Table not found: " + name);
		table.lock(tc);
		try {
			table.optimize();
		} finally {
			table.unlock();
		}

	}

	public AmiHdbTableRep getTableRepNoThrow(String name) {
		AmiHdbTable r = this.tables.get(name);
		return r == null ? null : r.getTable();
	}

	public void addRows(AmiHdbTableRep table, Table r) {
		AmiHdbTable htable = table.getHistoricalTable();
		htable.lock(null);
		try {
			htable.addRows(r);
		} finally {
			htable.unlock();
		}
	}

	public void diagnoseTable(AmiHdbTableRep table, ColumnarTable r) {
		AmiHdbTable htable = table.getHistoricalTable();
		htable.lock(null);
		try {
			long count = htable.getRowsCountThreadSafe();
			r.addColumn(Long.class, "COUNT");
			r.addColumn(Integer.class, "PID");
			r.addColumn(String.class, "PKEY");
			r.addColumn(String.class, "FILE");
			r.addColumn(String.class, "FILE2");
			r.addColumn(Long.class, "DISKSIZE");
			r.addColumn(String.class, "DATATYPE");
			r.addColumn(String.class, "STORAGE");
			r.addColumn(String.class, "COMMENTS");
			{
				ColumnarRow row = r.newEmptyRow();
				row.put("TABLE", htable.getName());
				row.put("NAME", htable.getName());
				row.put("TYPE", "TABLE");
				row.put("FILE", IOH.getFullPath(htable.getDirectory()));
				row.put("FILE2", "");
				row.put("COUNT", count);
				row.put("COMMENTS", "HISTORICAL");
				row.put("PID", null);
				row.put("PKEY", "");
				row.put("DISKSIZE", 0L);
				row.put("STORAGE", "DIRECTORY");
				r.getRows().add(row);
			}
			for (AmiHdbColumn i : htable.getColumns().values()) {
				ColumnarRow row = r.newEmptyRow();
				row.put("TABLE", htable.getName());
				row.put("TYPE", "COLUMN");
				row.put("NAME", i.getName());
				row.put("DATATYPE", AmiTableUtils.toStringForDataType(i.getAmiType()));
				row.put("STORAGE", AmiHdbUtils.toStringForMode(i.getMode()));
				row.put("COUNT", count);
				row.put("FILE", "");
				row.put("FILE2", "");
				row.put("DISKSIZE", 0L);
				row.put("COMMENTS", "HISTORICAL");
				row.put("PID", null);
				row.put("PKEY", "");
				r.getRows().add(row);
			}
			for (AmiHdbIndex i : htable.getIndexes().valueList()) {
				ColumnarRow row = r.newEmptyRow();
				row.put("TABLE", htable.getName());
				row.put("TYPE", "INDEX");
				row.put("NAME", i.getName());
				row.put("DATATYPE", AmiTableUtils.toStringForDataType(i.getColumn().getAmiType()));
				row.put("STORAGE", "BTREE");
				row.put("COUNT", count);
				row.put("FILE", "");
				row.put("FILE2", "");
				row.put("DISKSIZE", 0L);
				row.put("COMMENTS", "ON " + i.getColumn().getName());
				row.put("PID", null);
				row.put("PKEY", "");
				r.getRows().add(row);
			}
			for (AmiHdbPartition i : htable.getAllPartitions()) {
				String pkey = SH.join(",", (Object[]) i.getPartitionsKey());
				int pname = i.getId();//SH.rightAlign('0', SH.toString(i.getId()), 4, false);
				{
					ColumnarRow row = r.newEmptyRow();
					row.put("TABLE", htable.getName());
					row.put("TYPE", "PID");
					row.put("NAME", "");
					row.put("FILE", IOH.getFullPath(i.getDirectory()));
					row.put("FILE2", "");
					row.put("COUNT", (long) i.getRowCount());
					row.put("DISKSIZE", 0L);
					if (i.isInAppendMode())
						row.put("COMMENTS", "IN_APPEND_MODE");
					else
						row.put("COMMENTS", "");
					row.put("PID", pname);
					row.put("PKEY", pkey);
					row.put("STORAGE", "DIRECTORY");
					r.getRows().add(row);
				}
				for (AmiHdbPartitionColumn j : i.getColumns()) {
					{
						ColumnarRow row = r.newEmptyRow();
						row.put("TABLE", htable.getName());
						row.put("TYPE", "PCOLUMN");
						row.put("NAME", SH.toString(j.getName()));
						row.put("FILE", IOH.getFullPath(j.getColFile()));
						row.put("PID", pname);
						row.put("PKEY", pkey);
						if (j.isMissing()) {
							row.put("FILE", "");
							row.put("FILE2", "");
							row.put("COMMENTS", "FILE_MISSING");
						} else {
							if (j.getMarshaller().hasDataFile())
								row.put("FILE2", j.getDatFile().getName());
							else
								row.put("FILE2", "");
							row.put("COUNT", (long) j.getRowCount());
							row.put("DATATYPE", AmiTableUtils.toStringForDataType(j.getType()));
							row.put("STORAGE", AmiHdbUtils.toStringForMode(j.getMode()));
							row.put("DISKSIZE", j.getSizeOnDisk());
							if (!j.isOptimized())
								row.put("COMMENTS", "NOT_OPTIMIZED");
							else
								row.put("COMMENTS", "");
						}
						r.getRows().add(row);
					}
				}
				for (AmiHdbPartitionIndex idx : i.getIndexes()) {
					ColumnarRow row = r.newEmptyRow();
					row.put("TABLE", htable.getName());
					row.put("TYPE", "PINDEX");
					row.put("NAME", idx.getIndexName());
					row.put("DATATYPE", AmiTableUtils.toStringForDataType(idx.getColumnType()));
					row.put("STORAGE", "BTREE");
					row.put("COUNT", (long) i.getRowCount());
					row.put("FILE", IOH.getFullPath(idx.getFile()));
					row.put("FILE2", "");
					if (idx.isInMemory()) {
						row.put("DISKSIZE", 0L);
						row.put("COMMENTS", "IN_MEMORY");
					} else {
						row.put("DISKSIZE", idx.getFile().length());
						row.put("COMMENTS", "ON_DISK");
					}
					row.put("PID", pname);
					row.put("PKEY", pkey);
					r.getRows().add(row);
				}
			}
		} finally {
			htable.unlock();
		}
	}

	public long getRowsCount(String tableName) {
		return tables.get(tableName).getRowsCountThreadSafe();
	}
	public int getColumnsCount(String tableName) {
		return tables.get(tableName).getColumnsCountThreadSafe();
	}

	public boolean onRealtimeEvent(AmiRelayObjectMessage event) {
		AmiHdbTable table = this.tables.get(event.getType());
		if (table == null)
			return false;
		output.send(event, table.getF1PartitionId(), null);
		return true;
	}

	public Table getPreview(String name, int previewCount) {
		AmiHdbTable table = this.tables.get(name);
		table.lock(null);
		try {
			AmiHdbQueryImpl q = new AmiHdbQueryImpl(table);
			q.setLimit(0, 10);
			DerivedCellCalculator[] calcs = new DerivedCellCalculator[table.getColumnsCount()];
			String[] columnNames = new String[table.getColumnsCount()];
			for (int i = 0; i < columnNames.length; i++) {
				AmiHdbColumn columnAt = table.getColumnAt(i);
				columnNames[i] = columnAt.getName();
				calcs[i] = new DerivedCellCalculatorRef(0, columnAt.getType(), columnAt.getName());
			}
			q.setSelects(calcs, columnNames);
			ColumnarTable rs = q.query(EmptyCalcFrameStack.INSTANCE);
			return rs;
		} catch (IOException e) {
			throw OH.toRuntime(e);
		} finally {
			table.unlock();
		}
	}

	public void onStartupComplete() {
		for (AmiHdbTable amiHdbTable : this.tables.values()) {
			amiHdbTable.lock(null);
			try {
				amiHdbTable.optimize();
			} finally {
				amiHdbTable.unlock();
			}
		}
	}

}
