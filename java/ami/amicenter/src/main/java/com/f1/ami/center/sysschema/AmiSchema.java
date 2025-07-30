package com.f1.ami.center.sysschema;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.center.AmiCenterProperties;
import com.f1.ami.center.AmiCenterUtils;
import com.f1.ami.center.dbo.AmiDboBindingImpl;
import com.f1.ami.center.hdb.AmiHdb;
import com.f1.ami.center.hdb.AmiHdbSchema_Column;
import com.f1.ami.center.hdb.AmiHdbSchema_Index;
import com.f1.ami.center.hdb.AmiHdbSchema_Table;
import com.f1.ami.center.procs.AmiStoredProcBindingImpl;
import com.f1.ami.center.table.AmiCenterSqlProcessorMutator;
import com.f1.ami.center.table.AmiColumnImpl;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiImdbObjectsManager;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.center.table.AmiIndex;
import com.f1.ami.center.table.AmiIndexImpl;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTableDef;
import com.f1.ami.center.table.AmiTableImpl;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.ami.center.table.persist.AmiTablePersisterBinding;
import com.f1.ami.center.timers.AmiTimerBindingImpl;
import com.f1.ami.center.triggers.AmiTriggerBindingImpl;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.Tuple3;
import com.f1.utils.structs.table.derived.DeclaredMethodFactory;
import com.f1.utils.structs.table.derived.MethodFactory;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiSchema {
	private static final Logger log = LH.get();

	private static final char[] DOUBLEQUOTE = new char[] { '"' };

	public static final String MANAGED_FILE_HEADER = "/* DO NOT MODIFY THIS FILE WHILE AMI IS RUNNING, CONTENTS ARE MODIFIED BY AMI  */";

	private static final Comparator<MethodFactory> FACTORY_COMPARATOR = new Comparator<MethodFactory>() {

		@Override
		public int compare(MethodFactory o1, MethodFactory o2) {
			ParamsDefinition d1 = o1.getDefinition();
			ParamsDefinition d2 = o2.getDefinition();
			int r = OH.compare(d1.getMethodName(), d2.getMethodName());
			if (r != 0)
				return r;
			int min = Math.min(d1.getParamsCount(), d2.getParamsCount());
			for (int i = 0; i < min; i++) {
				r = OH.compare(d1.getParamName(i), d2.getParamName(i));
				if (r != 0)
					return r;
			}
			return OH.compare(d1.getParamsCount(), d2.getParamsCount());
		}
	};

	public final AmiSchema_COMMAND __COMMAND;
	public final AmiSchema_DATASOURCE_TYPE __DATASOURCE_TYPE;
	public final AmiSchema_DATASOURCE __DATASOURCE;
	public final AmiSchema_RESOURCE __RESOURCE;
	public final AmiSchema_TABLE __TABLE;
	public final AmiSchemaProc_ADD_DATASOURCE __PROC_ADD_DATASOURCE;
	public final AmiSchemaProc_REMOVE_DATASOURCE __PROC_REMOVE_DATASOURCE;
	public final AmiSchemaProc_RESET_TIMER_STATS __PROC_RESET_TIMER_STATS;
	public final AmiSchemaProc_RESET_TRIGGER_STATS __PROC_RESET_TRIGGER_STATS;
	public final AmiSchemaProc_SCHEDULE_TIMER __PROC_SCHEDULE_TIMER;
	public final AmiSchemaProc_SHOW_TIMER_ERROR __PROC_SHOW_TIMER_ERROR;
	public final AmiSchemaProc_SHOW_TRIGGER_ERROR __PROC_SHOW_TRIGGER_ERROR;
	public final AmiSchemaProc_ADD_CENTER __PROC_ADD_REPLICATION_SOURCE;
	public final AmiSchemaProc_REMOVE_CENTER __PROC_REMOVE_REPLICATION_SOURCE;
	public final AmiSchemaProc_ADD_REPLICATION __PROC_ADD_REPLICATION;
	public final AmiSchemaProc_REMOVE_REPLICATION __PROC_REMOVE_REPLICATION;
	public final AmiSchemaProc_OPTIMIZE_HISTORICAL_TABLE __OPTIMIZE_HIST_TABLE;
	public final AmiSchemaProc_MARK_HISTORICAL_PARTITION_FOR_APPEND __MARK_HIST_PARTITION_FOR_APPEND;
	public final AmiSchemaProc_SET_TIMEZONE __PROC_SET_TIMEZONE;
	public final AmiSchemaProc_GET_TIMEZONE __PROC_GET_TIMEZONE;
	public final AmiSchema_COLUMN __COLUMN;
	public final AmiSchema_INDEX __INDEX;
	public final AmiSchema_TRIGGER __TRIGGER;
	public final AmiSchema_PROCEDURE __PROCEDURE;
	public final AmiSchema_PROPERTY __PROPERTY;
	public final AmiSchema_TIMER __TIMER;
	public final AmiSchema_PLUGIN __PLUGIN;
	public final AmiSchema_CONNECTION __CONNECTION;
	public final AmiSchema_RELAY __RELAY;
	public final AmiSchema_STATS __STATS;
	public final AmiSchema_REPLICATION __REPLICATION;
	public final AmiSchema_CENTER __CENTER;
	public final AmiSchema_DBO __DBO;
	final private AmiImdbImpl imdb;

	final private AmiImdbObjectsManager dbo;
	final private AmiHdb hdb;

	public AmiSchema(AmiImdbImpl imdb, AmiImdbSession session) {
		this.imdb = imdb;
		this.hdb = imdb.getState().getHdb();
		this.dbo = this.imdb.getObjectsManager();
		CalcFrameStack sf = imdb.getState().getReusableTopStackFrame();
		this.__COMMAND = new AmiSchema_COMMAND(imdb, sf);
		this.__DATASOURCE_TYPE = new AmiSchema_DATASOURCE_TYPE(imdb, sf);
		this.__DATASOURCE = new AmiSchema_DATASOURCE(imdb, sf);
		this.__RESOURCE = new AmiSchema_RESOURCE(imdb, sf);
		this.__TABLE = new AmiSchema_TABLE(imdb, sf);
		this.__COLUMN = new AmiSchema_COLUMN(imdb, sf);
		this.__INDEX = new AmiSchema_INDEX(imdb, sf);
		this.__TRIGGER = new AmiSchema_TRIGGER(imdb, sf);
		this.__PROCEDURE = new AmiSchema_PROCEDURE(imdb, sf);
		this.__PROPERTY = new AmiSchema_PROPERTY(imdb, sf);
		this.__TIMER = new AmiSchema_TIMER(imdb, sf);
		this.__PLUGIN = new AmiSchema_PLUGIN(imdb, sf);
		this.__CONNECTION = new AmiSchema_CONNECTION(imdb, sf);
		this.__RELAY = new AmiSchema_RELAY(imdb, sf);
		this.__STATS = new AmiSchema_STATS(imdb, sf);
		this.__REPLICATION = new AmiSchema_REPLICATION(imdb, sf);
		this.__CENTER = new AmiSchema_CENTER(imdb, sf);
		this.__DBO = new AmiSchema_DBO(imdb, sf);
		this.__PROC_ADD_DATASOURCE = new AmiSchemaProc_ADD_DATASOURCE(imdb, sf);
		this.__PROC_ADD_REPLICATION_SOURCE = new AmiSchemaProc_ADD_CENTER(imdb, sf);
		this.__PROC_ADD_REPLICATION = new AmiSchemaProc_ADD_REPLICATION(imdb, sf);
		this.__PROC_REMOVE_REPLICATION = new AmiSchemaProc_REMOVE_REPLICATION(imdb, sf);
		this.__PROC_REMOVE_REPLICATION_SOURCE = new AmiSchemaProc_REMOVE_CENTER(imdb, sf);
		this.__PROC_REMOVE_DATASOURCE = new AmiSchemaProc_REMOVE_DATASOURCE(imdb, sf);
		this.__PROC_SCHEDULE_TIMER = new AmiSchemaProc_SCHEDULE_TIMER(imdb, sf);
		this.__PROC_RESET_TIMER_STATS = new AmiSchemaProc_RESET_TIMER_STATS(imdb, sf);
		this.__PROC_RESET_TRIGGER_STATS = new AmiSchemaProc_RESET_TRIGGER_STATS(imdb, sf);
		this.__PROC_SHOW_TIMER_ERROR = new AmiSchemaProc_SHOW_TIMER_ERROR(imdb, sf);
		this.__PROC_SHOW_TRIGGER_ERROR = new AmiSchemaProc_SHOW_TRIGGER_ERROR(imdb, sf);
		this.__OPTIMIZE_HIST_TABLE = new AmiSchemaProc_OPTIMIZE_HISTORICAL_TABLE(imdb, sf);
		this.__MARK_HIST_PARTITION_FOR_APPEND = new AmiSchemaProc_MARK_HISTORICAL_PARTITION_FOR_APPEND(imdb, sf);
		this.__PROC_SET_TIMEZONE = new AmiSchemaProc_SET_TIMEZONE(imdb, sf);
		this.__PROC_GET_TIMEZONE = new AmiSchemaProc_GET_TIMEZONE(imdb, sf);
	}

	public void buildSystemTables(CalcFrameStack sf) {
		Map<String, AmiRow> timersByName = this.__TIMER.getRowsByName();
		Map<String, AmiRow> triggersByName = this.__TRIGGER.getRowsByTriggerName();
		Map<String, AmiRow> tablesByName = this.__TABLE.getRowsByName();
		Map<String, AmiRow> procsByName = this.__PROCEDURE.getRowsByName();
		Map<String, AmiRow> dbosByName = this.__DBO.getRowsByDboName();
		Map<Tuple2<String, String>, AmiRow> columsByName = this.__COLUMN.getRowsByTableNameColumnName();
		Map<Tuple3<String, String, Integer>, AmiRow> indexesByName = this.__INDEX.getRowsByTableNameIndexNameIndexPosition();
		for (String tableName : this.imdb.getAmiTableNamesSorted()) {
			AmiRow existingTable = tablesByName.remove(tableName);
			final AmiTableImpl table = (AmiTableImpl) this.imdb.getAmiTable(tableName);
			final AmiTablePersisterBinding persister = table.getPersister();
			final String persisterEngine = persister == null ? null : persister.getPersisterType();
			this.__TABLE.addRow(existingTable, tableName, table.getIsBroadCast(), table.getRefreshPeriod(), persisterEngine, table.getOnUndefinedColumn(), table.getDefType(),
					table.getInitialCapacity(), sf);

			StringBuilder sink = new StringBuilder();
			for (int colpos = 0; colpos < table.getColumnsCount(); colpos++) {
				final AmiColumnImpl<?> col = table.getColumnAt(colpos);
				String columnName = col.getName();
				AmiRow existingColumn = columsByName.remove(new Tuple2<String, String>(tableName, columnName));
				final String dataType = AmiTableUtils.toStringForDataType(col.getAmiType());
				sink.setLength(0);
				getColumnOptions(col, sink, false);
				this.__COLUMN.addRow(existingColumn, tableName, columnName, dataType, sink.length() == 0 ? null : sink.toString(), !col.getAllowNull(), colpos, table.getDefType(),
						sf);
			}
			for (AmiIndexImpl index : table.getIndexes()) {
				String indexName = index.getName();
				for (int indexPos = 0; indexPos < index.getColumnsCount(); indexPos++) {
					String columnName = index.getColumn(indexPos).getName();
					AmiRow existingIndex = indexesByName.remove(new Tuple3<String, String, Integer>(tableName, indexName, indexPos));
					String constraint = AmiTableUtils.toStringForIndexConstraintType(index.getConstraintType());
					this.__INDEX.addRow(existingIndex, tableName, columnName, indexName, AmiTableUtils.toStringForIndexType(index.getIndexTypeAt(indexPos)), indexPos, constraint,
							index.getDefType(), index.getAutoGenType(), sf);
				}
			}
		}
		for (String tableName : this.hdb.getTablesSorted()) {
			AmiHdbSchema_Table table = this.hdb.getTableSchema(tableName);
			AmiRow existingTable = tablesByName.remove(tableName);
			this.__TABLE.addRow(existingTable, tableName, false, 0, "HISTORICAL", AmiTableDef.ON_UNDEFINED_COLUMN_IGNORE, table.getDefType(), 0, sf);

			StringBuilder sink = new StringBuilder();
			for (int colpos = 0; colpos < table.getColumns().length; colpos++) {
				final AmiHdbSchema_Column col = table.getColumns()[colpos];
				String columnName = col.getName();
				AmiRow existingColumn = columsByName.remove(new Tuple2<String, String>(tableName, columnName));
				final String dataType = AmiTableUtils.toStringForDataType(col.getAmiType());
				sink.setLength(0);
				this.__COLUMN.addRow(existingColumn, tableName, columnName, dataType, sink.length() == 0 ? null : sink.toString(), true, colpos, table.getDefType(), sf);
			}
			for (AmiHdbSchema_Index index : table.getIndexes()) {
				String indexName = index.getName();
				//				for (int indexPos = 0; indexPos < index.getColumnsCount(); indexPos++) {
				String columnName = index.getColumnName();
				AmiRow existingIndex = indexesByName.remove(new Tuple3<String, String, Integer>(tableName, indexName, 0));
				String constraint = AmiTableUtils.toStringForIndexConstraintType(AmiIndex.CONSTRAINT_TYPE_NONE);
				this.__INDEX.addRow(existingIndex, tableName, columnName, indexName, "SORT", 0, constraint, index.getDefType(), AmiIndex.AUTOGEN_NONE, sf);
				//				}
			}
		}
		for (AmiTriggerBindingImpl col : this.dbo.getAmiTriggerBindings()) {
			String triggerName = col.getTriggerName();
			AmiRow existingTrigger = triggersByName.remove(triggerName);
			StringBuilder buf = new StringBuilder();
			useOptionsToString(col.getOptionsStrings(), buf);
			this.__TRIGGER.addRow(existingTrigger, col.getTableNames(), triggerName, col.getTriggerType(), col.getPriority(), buf.toString(), col.getDefType(), col.getIsEnabled(),
					sf);
		}

		for (String timerName : CH.sort(this.imdb.getAmiTimerNamesSorted())) {
			AmiTimerBindingImpl timer = this.dbo.getAmiTimerBinding(timerName);
			AmiRow existing = timersByName.remove(timerName);
			StringBuilder buf = new StringBuilder();
			useOptionsToString(timer.getOptionsStrings(), buf);
			this.__TIMER.addRow(existing, timerName, timer.getTimerType(), timer.getPriority(), timer.getSchedule(), buf.toString(), timer.getLastRunTime(), timer.getNextRunTime(),
					timer.getDefType(), timer.getIsEnabled(), sf);
		}
		for (String storedProcName : CH.sort(this.imdb.getAmiStoredProcNamesSorted())) {
			AmiStoredProcBindingImpl sp = this.dbo.getAmiStoredProcBinding(storedProcName);
			AmiRow existing = procsByName.remove(storedProcName);
			StringBuilder buf = new StringBuilder();
			useOptionsToString(sp.getOptionsStrings(), buf);
			String returnType = sp.getReturnTypeString();
			String arguments = sp.getArgumentsString();
			this.__PROCEDURE.addRow(existing, storedProcName, sp.getStoredProcType(), returnType, arguments, buf.toString(), sp.getDefType(), sf);
		}
		for (String dboName : CH.sort(this.imdb.getAmiDboNamesSorted())) {
			AmiDboBindingImpl dbo = this.dbo.getAmiDboBinding(dboName);
			AmiRow existing = dbosByName.remove(dboName);
			StringBuilder buf = new StringBuilder();
			useOptionsToString(dbo.getOptionsStrings(), buf);
			this.__DBO.addRow(existing, dboName, dbo.getDboType(), dbo.getPriority(), buf.toString(), dbo.getDefType(), dbo.getIsEnabled(), sf);
		}

		for (AmiRow row : triggersByName.values())
			this.__TRIGGER.table.removeAmiRow(row, sf);
		for (AmiRow row : tablesByName.values())
			this.__TABLE.table.removeAmiRow(row, sf);
		for (AmiRow row : procsByName.values())
			this.__PROCEDURE.table.removeAmiRow(row, sf);
		for (AmiRow row : columsByName.values())
			this.__COLUMN.table.removeAmiRow(row, sf);
		for (AmiRow row : indexesByName.values())
			this.__INDEX.table.removeAmiRow(row, sf);
		for (AmiRow row : timersByName.values())
			this.__TIMER.table.removeAmiRow(row, sf);
		for (AmiRow row : dbosByName.values())
			this.__DBO.table.removeAmiRow(row, sf);
	}

	public void writeManagedSchemaFile(CalcFrameStack sf) {
		StringBuilder sql = new StringBuilder();
		MethodFactoryManager mf = this.imdb.getScriptManager().getManagedMethodFactory();
		List<MethodFactory> sink = new ArrayList<MethodFactory>();
		mf.getAllMethodFactories(sink);
		Collections.sort(sink, FACTORY_COMPARATOR);
		sql.append(MANAGED_FILE_HEADER).append(SH.NEWLINE).append(SH.NEWLINE);
		sql.append("/*CUSTOM METHODS*/").append(SH.NEWLINE).append(SH.NEWLINE);
		if (sink.size() > 0) {
			sql.append("CREATE METHOD {");
			for (MethodFactory i : sink) {
				sql.append(SH.NEWLINE);
				DeclaredMethodFactory dmf = (DeclaredMethodFactory) i;
				dmf.getText(this.imdb.getAmiScriptMethodFactory(), sql);
				sql.append(SH.NEWLINE);
			}
			sql.append("}");
		}
		sql.append(SH.NEWLINE);
		sql.append(SH.NEWLINE);
		sql.append("/*TABLES AND INDEXES*/").append(SH.NEWLINE).append(SH.NEWLINE);
		for (String tableName : this.imdb.getAmiTableNamesSorted()) {
			final AmiTableImpl table = (AmiTableImpl) this.imdb.getAmiTable(tableName);
			if (table.getDefType() == AmiTableUtils.DEFTYPE_USER) {
				generateCreateSql(table, sql);
				for (AmiIndexImpl i : table.getIndexes())
					generateCreateSql(i, sql);
				for (int i = 0; i < table.getTriggersCount(); i++) {
					AmiTriggerBindingImpl trigger = table.getTriggerAt(i);
					if (trigger.getTableNamesCount() == 1)
						generateCreateSql(trigger, sql);
				}
				sql.append(SH.NEWLINE);
				sql.append(SH.NEWLINE);
			}
		}
		for (String table : this.hdb.getTablesSorted())
			sql.append(this.hdb.getTableSchema(table).getSqlDef());
		sql.append(SH.NEWLINE).append("/*TIMERS*/").append(SH.NEWLINE);
		for (String timerName : CH.sort(this.imdb.getAmiTimerNamesSorted())) {
			AmiTimerBindingImpl timer = this.dbo.getAmiTimerBinding(timerName);
			if (timer.getDefType() == AmiTableUtils.DEFTYPE_USER) {
				generateCreateSql(timer, sql);
				sql.append(SH.NEWLINE);
			}
		}

		sql.append(SH.NEWLINE).append("/*PROCEDURES*/").append(SH.NEWLINE);
		for (String timerName : CH.sort(this.imdb.getAmiStoredProcNamesSorted())) {
			AmiStoredProcBindingImpl sp = this.dbo.getAmiStoredProcBinding(timerName);
			if (sp.getDefType() == AmiTableUtils.DEFTYPE_USER) {
				generateCreateSql(sp, sql);
				sql.append(SH.NEWLINE);
			}
		}
		sql.append(SH.NEWLINE).append("/*MULTI-TABLE TRIGGERS*/").append(SH.NEWLINE);
		for (String triggerName : this.dbo.getAmiTriggerNamesSorted()) {
			AmiTriggerBindingImpl trigger = this.dbo.getAmiTriggerBinding(triggerName);
			if (trigger.getTableNamesCount() != 1) {
				if (trigger.getDefType() == AmiTableUtils.DEFTYPE_USER) {
					generateCreateSql(trigger, sql);
					sql.append(SH.NEWLINE);
				}
			}
		}
		sql.append(SH.NEWLINE).append("/*DBOS*/").append(SH.NEWLINE);
		for (String dboName : this.dbo.getAmiDboNamesSorted()) {
			AmiDboBindingImpl dbo = this.dbo.getAmiDboBinding(dboName);
			if (dbo.getDefType() == AmiTableUtils.DEFTYPE_USER) {
				generateCreateSql(dbo, sql);
				sql.append(SH.NEWLINE);
			}
		}

		File managedFile = this.imdb.getState().getTools().getOptional(AmiCenterProperties.PROPERTY_AMI_SCHEMA_MANAGED_FILE, new File("data/managed_schema.amisql"));
		try {
			IOH.ensureDir(managedFile.getParentFile());
			IOH.writeText(managedFile, sql.toString());
			LH.info(log, "Updated " + IOH.getFullPath(managedFile));
		} catch (IOException e) {
			AmiCenterUtils.getSession(sf).onWarning("SCHEMA_ERROR", null, null, "WRITE_FILE", "Failed to Write to: " + IOH.getFullPath(managedFile), null, e);
			throw new RuntimeException("Error preparing schema file: " + IOH.getFullPath(managedFile), e);
		}
	}

	public void generateCreateSql_method(MethodFactoryManager mf, String methodSignature, int namePos, int[] posArray, StringBuilder sql) {
		String beforeArgs = SH.beforeFirst(methodSignature, '(');
		String[] args = SH.split(',', SH.stripSuffix(SH.afterFirst(methodSignature, '('), ")", true));
		Class[] argClasses = new Class[args.length];
		for (int i = 0; i < args.length; i++) {
			String arg = SH.beforeFirst(SH.trim(args[i]), ' ');
			argClasses[i] = mf.forNameNoThrow(arg);
			if (argClasses[i] == null)
				throw new ExpressionParserException(posArray[i], "Unknown class: " + arg);
		}
		MethodFactory sink = mf.getMethodFactory(beforeArgs, argClasses);
		if (sink == null)
			throw new ExpressionParserException(namePos, "Method not found: " + methodSignature);
		sql.append("CREATE METHOD {");
		sql.append(SH.NEWLINE);
		if (sink instanceof DeclaredMethodFactory) {
			DeclaredMethodFactory dmf = (DeclaredMethodFactory) sink;
			dmf.getText(this.imdb.getAmiScriptMethodFactory(), sql);
			sql.append(SH.NEWLINE);
		} else {//for SYSTEM methods, do "CREATE METHOD{ abs(Number) OFTYPE SYSTEM}
			sql.append(methodSignature);
			sql.append(" OFTYPE _SYSTEM");
		}
		sql.append("}");

	}

	public void generateCreateSql(AmiIndexImpl index, StringBuilder sql) {
		sql.append("CREATE INDEX ");
		AmiUtils.escapeVarName(index.getName(), sql);
		sql.append(" ON ");
		AmiUtils.escapeVarName(index.getTable().getName(), sql);
		sql.append("(");
		for (int i = 0; i < index.getColumnsCount(); i++) {
			AmiColumnImpl<?> col = index.getColumn(i);
			byte type = index.getIndexTypeAt(i);
			if (i > 0)
				sql.append(',');
			AmiUtils.escapeVarName(col.getName(), sql);
			sql.append(' ').append(AmiTableUtils.toStringForIndexType(type));
		}

		sql.append(") USE ");
		generateUseOptionSql(AmiCenterSqlProcessorMutator.OPTION_CONSTRAINT, AmiTableUtils.toStringForIndexConstraintType(index.getConstraintType()), sql);
		if (index.getAutoGenType() != AmiIndex.AUTOGEN_NONE)
			generateUseOptionSql(AmiCenterSqlProcessorMutator.OPTION_AUTOGEN, AmiTableUtils.toStringForIndexAutoGenType(index.getAutoGenType()), sql);
		sql.append(';').append(SH.NEWLINE);
	}
	public void generateCreateSql(AmiDboBindingImpl dbo, StringBuilder sql) {
		sql.append("CREATE DBO ");
		AmiUtils.escapeVarName(dbo.getDboName(), sql);
		sql.append(" OFTYPE ").append(dbo.getDboType());
		sql.append(" PRIORITY ").append(dbo.getPriority());

		if (!dbo.getOptionsStrings().isEmpty())
			useOptionsToString(dbo.getOptionsStrings(), sql.append(" USE "));
		sql.append(';').append(SH.NEWLINE);
		if (!dbo.getIsEnabled()) {
			sql.append("DISABLE DBO ");
			AmiUtils.escape(dbo.getDboName(), sql);
			sql.append(';').append(SH.NEWLINE);
		}
	}

	private void toUseOptions(AmiDboBindingImpl dbo, StringBuilder sql) {
	}
	public void generateCreateSql(AmiTriggerBindingImpl trigger, StringBuilder sql) {
		sql.append("CREATE TRIGGER ");
		AmiUtils.escapeVarName(trigger.getTriggerName(), sql);
		sql.append(" OFTYPE ").append(trigger.getTriggerType()).append(" ON");
		for (int i = 0; i < trigger.getTableNamesCount(); i++)
			AmiUtils.escapeVarName(trigger.getTableNameAt(i), sql.append(i == 0 ? ' ' : ','));
		sql.append(" PRIORITY ").append(trigger.getPriority());
		if (!trigger.getOptionsStrings().isEmpty())
			useOptionsToString(trigger.getOptionsStrings(), sql.append(" USE "));
		sql.append(';').append(SH.NEWLINE);
		if (!trigger.getIsEnabled()) {
			sql.append("DISABLE TRIGGER ");
			AmiUtils.escape(trigger.getTriggerName(), sql);
			sql.append(';').append(SH.NEWLINE);
		}
	}
	public void generateCreateSql(AmiTimerBindingImpl timer, StringBuilder sql) {
		sql.append("CREATE TIMER ");
		AmiUtils.escapeVarName(timer.getTimerName(), sql);
		sql.append(" OFTYPE ").append(timer.getTimerType()).append(" ON \"").append(timer.getSchedule()).append("\" PRIORITY ").append(timer.getPriority());
		if (!timer.getOptionsStrings().isEmpty())
			useOptionsToString(timer.getOptionsStrings(), sql.append(" USE "));
		sql.append(';').append(SH.NEWLINE);
		if (!timer.getIsEnabled()) {
			sql.append("DISABLE TIMER ");
			AmiUtils.escapeVarName(timer.getTimerName(), sql);
			sql.append(';').append(SH.NEWLINE);
		}

	}
	public void generateCreateSql(AmiStoredProcBindingImpl sp, StringBuilder sql) {
		sql.append("CREATE PROCEDURE ");
		AmiUtils.escapeVarName(sp.getStoredProcName(), sql);
		sql.append(" OFTYPE ").append(sp.getStoredProcType());
		if (!sp.getOptionsStrings().isEmpty())
			useOptionsToString(sp.getOptionsStrings(), sql.append(" USE "));
		sql.append(';').append(SH.NEWLINE);
	}

	static public StringBuilder useOptionsToString(Map<String, String> options, StringBuilder sql) {
		if (options.size() > 0) {
			boolean first = true;
			for (Entry<String, String> e : options.entrySet()) {
				if (first)
					first = false;
				else
					sql.append(' ');
				String value = e.getValue();
				if (value != null && value.indexOf("\n") != -1 && value.indexOf("\"\"\"") == -1 && value.indexOf("${") == -1) {
					sql.append("\n");
					sql.append(e.getKey()).append("=");
					sql.append("\"\"\"").append(value).append("\"\"\"\n");
				} else {
					sql.append(e.getKey()).append("=");
					sql.append('"');
					SH.toStringEncode(value, sql);
					sql.append('"');
				}
			}
		}
		return sql;
	}
	public void generateCreateSql(AmiTableImpl table, StringBuilder sql) {
		int indent = sql.length();
		sql.append("CREATE PUBLIC TABLE ");
		AmiUtils.escapeVarName(table.getName(), sql);
		sql.append("(");
		indent = sql.length() - indent;
		for (int i = 0; i < table.getColumnsCount(); i++) {
			AmiColumnImpl<?> col = table.getColumnAt(i);
			if (i > 0)
				sql.append(',');
			if (i % 5 == 0 && i != 0) {
				sql.append(SH.NEWLINE);
				SH.repeat(' ', indent, sql);
			}
			AmiUtils.escapeVarName(col.getName(), sql);
			sql.append(' ').append(AmiTableUtils.toStringForDataType(col.getAmiType()));
			getColumnOptions(col, sql, true);
		}
		sql.append(") USE ");
		AmiTablePersisterBinding persister = table.getPersister();
		if (persister != null) {
			generateUseOptionSql(AmiCenterSqlProcessorMutator.OPTION_PERSIST_ENGINE, persister.getPersisterType(), sql);
			for (Entry<String, String> i : persister.getOptionsStrings().entrySet())
				generateUseOptionSql(i.getKey(), SH.toString(i.getValue()), sql);
		}
		generateUseOptionSql(AmiCenterSqlProcessorMutator.OPTION_NOBROADCAST, SH.toString(!table.getIsBroadCast()), sql);
		generateUseOptionSql(AmiCenterSqlProcessorMutator.OPTION_REFRESH_PERIOD_MS, SH.toString(table.getRefreshPeriod()), sql);
		generateUseOptionSql(AmiCenterSqlProcessorMutator.OPTION_ON_UNDEF_COLUMN, AmiTableUtils.toStringForOnUndefColType(table.getOnUndefinedColumn()), sql);
		generateUseOptionSql(AmiCenterSqlProcessorMutator.OPTION_INITIAL_CAPACITY, SH.toString(table.getInitialCapacity()), sql);
		sql.append(';').append(SH.NEWLINE);
	}

	public static void getColumnOptions(AmiColumnImpl<?> col, StringBuilder sql, boolean includeNoNull) {
		if (CH.isntEmpty(col.getOptions())) {
			for (Entry<String, String> e : col.getOptions().entrySet()) {
				if (!includeNoNull && "NoNull".contentEquals(e.getKey()))
					continue;
				if (sql.length() > 0)
					sql.append(' ');
				sql.append(e.getKey());
				if (OH.ne("true", e.getValue())) {
					sql.append("=\"");
					SH.escape(e.getValue(), '"', '\\', sql).append("\"");
				}
			}

		}
	}
	public static StringBuilder generateUseOptionSql(String key, String value, StringBuilder sql) {
		if (value == null || "false".equals(value))//this is the default
			return sql;
		if (!SH.endsWith(sql, ' '))
			sql.append(' ');
		if ("true".equals(value))
			return sql.append(key);
		sql.append(key).append('=');
		SH.quote('"', value, sql);
		return sql;
	}
}
