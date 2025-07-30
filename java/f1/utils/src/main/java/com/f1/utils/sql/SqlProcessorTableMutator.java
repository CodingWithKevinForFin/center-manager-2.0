package com.f1.utils.sql;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.Column;
import com.f1.base.Pointer;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.sql.SqlProjector.TempIndex;
import com.f1.utils.string.Node;
import com.f1.utils.string.node.MethodNode;
import com.f1.utils.structs.table.ColumnPositionMapping;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.derived.MethodFactory;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public interface SqlProcessorTableMutator {

	public Table processTableAdd(CalcFrameStack sf, String name, int namePosition, Table r, Map<String, Node> useOptions, int scope, boolean ifNotExists);
	public Table processTableAdd(CalcFrameStack sf, String name, int namePosition, String[] types, String[] names, Map<String, Node>[] colOptions, int[] colDefPos,
			Map<String, Node> useOptions, int scope, boolean ifNotExists);
	public Table processTableRemove(CalcFrameStack sf, String name, int tableNamePos, int scope, boolean ifExists);
	public Table processTableRename(CalcFrameStack sf, int fromPos, String from, int toPos, String to, int scope);
	public void processTriggerRename(CalcFrameStack sf, int fromPos, String from, int toPos, String to, int scope);
	public void processTimerRename(CalcFrameStack sf, int fromPos, String from, int toPos, String to, int scope);
	public void processProcedureRename(CalcFrameStack sf, int fromPos, String from, int toPos, String to, int scope);

	public Table processColumnAdd(CalcFrameStack sf, int tableNamePos, String tableName, int typePos, String type, String varname, int colPosition, int scope,
			Map<String, Node> options, Object[] addVals);
	public Table processColumnRemove(CalcFrameStack sf, int tableNamePos, String tableName, String colname, int colNamePos, int scope);
	public Table processColumnChangeType(CalcFrameStack sf, int tableNamePos, String tableName, int location, Class<?> type, int newTypePos, String newType, String newName,
			Map<String, Node> options, int scope);

	public TableReturn processRowAdds(CalcFrameStack sf, Table table, int tableNamePos, ColumnPositionMapping posMapping, int startRow, int rows, Table values,
			boolean returnGeneratedIds);
	public TableReturn processRowAdds(CalcFrameStack sf, Table table, int tableNamePos, int[] positions, Object[][] rows, boolean returnGeneratedIds);
	public int processRowRemoves(CalcFrameStack sf, Table table, List<Row> toDelete);
	public int processRowUpdate(CalcFrameStack sf, Table table, int tableNamePos, List<Row> toUpdate, int[] positions, Object[][] values);
	public int processRowRemoveAll(CalcFrameStack sf, Table table);

	public void processIndexCreate(CalcFrameStack sf, String idxName, int idxNamePos, String tableName, int tableNamePos, String[] colName, String[] colType, int[] colPos,
			Map<String, Node> useOptions, boolean ifNotExists, int scope);
	public void processIndexRemove(CalcFrameStack sf, String tableName, int tableNamePos, String indexName, int indexNamePos, boolean ifExists, int scope);
	public List<Row> applyIndexes(CalcFrameStack sf, String asTableName, Table table, Pointer<DerivedCellCalculator> whereClause, int limit);
	public void processTriggerCreate(CalcFrameStack sf, String triggerName, int triggerNamePos, String typeName, int typeNamePos, String tableNames[], int tableNamesPos[],
			int priority, Map<String, Node> useOptions, boolean ifNotExists);
	public void processTriggerRemove(CalcFrameStack sf, String tableName, int tableNamePos, String triggerName, int triggerNamePos, boolean ifExists);

	public void processTimerRemove(CalcFrameStack sf, String timerName, int timerNamePos, boolean ifExists);
	public void processTimerCreate(CalcFrameStack sf, String timerName, int timerNamePos, String typeName, int typeNamePos, int priority, String on, int onPos,
			Map<String, Node> useOptions, boolean ifNotExists);

	public void processProcedureCreate(CalcFrameStack sf, String procedureName, int procedureNamePos, String typeName, int typeNamePos, Map<String, Node> useOptions,
			boolean ifNotExists);
	public void processProcedureRemove(CalcFrameStack sf, String procedureName, int procedureNamePos, boolean ifExists);

	public FlowControl processCallProcedure(CalcFrameStack sf, String name, int namePos, Object[] params, int[] paramsPos, int limitOffset, int limit);
	public String processDescribe(CalcFrameStack sf, int type, int scope, String name, int namePos, String on, int onPos, String from, int fromPos, MethodNode mn);

	public Table processShow(CalcFrameStack sf, String targetType, int targetTypePos, int scope, boolean full, String name, int namePos, String from, int fromPos, MethodNode mn);

	public void processEnabled(CalcFrameStack sf, boolean enable, int position, String type, String[] name, int[] namePosition);
	public void processReturningTable(CalcFrameStack sf, TableReturn r);
	public TempIndex findIndex(CalcFrameStack sf, Table targetTable, String[] targetColumns, int targetTablePos, Table sourceTable, String[] sourceColumns, List<Row> targetRows);
	public Set<String> getIndexes(CalcFrameStack sf, Table targetTable);
	public Table getTableIfExists(CalcFrameStack sf, String tableName, int scope);
	public Table getTable(CalcFrameStack sf, int position, String tableName, int scope);
	public void processDiagnoseTable(CalcFrameStack sf, int scope, Table table, ColumnarTable r);
	public Map processDiagnoseColumn(CalcFrameStack sf, int scope, Table table, Column i);
	public Map processDiagnoseIndex(CalcFrameStack sf, int scope, Table table, int indexPos, String indexName);
	public void processMethodCreate(CalcFrameStack sf, int pos, List<MethodFactory> sink, boolean ifNotExists);
	public void processMethodDrop(CalcFrameStack sf, int pos, String methodName, Class[] types, boolean ifExists);

	public void processDboCreate(CalcFrameStack sf, String dboName, int dboNamePos, String typeName, int typeNamePos, int priority, Map<String, Node> useOptions,
			boolean ifNotExists);
	public void processDboRemove(CalcFrameStack sf, String dboName, int dboNamePos, boolean ifExists);
	public void processDboRename(CalcFrameStack sf, int fromPos, String from, int toPos, String to, int scope);
	public void processAlterUseOptions(CalcFrameStack sf, int targetType, String name, int position, Map<String, Node> useOptions);
	public Iterable<Row> findSortIndex(CalcFrameStack sf, Table table, String columnName, boolean asc);
	public boolean hasIndex(CalcFrameStack sf, Table table, String columnName);
}
