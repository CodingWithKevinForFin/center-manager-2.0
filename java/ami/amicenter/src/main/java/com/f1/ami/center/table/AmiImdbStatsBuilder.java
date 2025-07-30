package com.f1.ami.center.table;

import java.util.LinkedHashMap;
import java.util.Map;

import com.f1.ami.center.procs.AmiStoredProcBindingImpl;
import com.f1.ami.center.timers.AmiTimerBindingImpl;
import com.f1.ami.center.triggers.AmiTriggerBindingImpl;
import com.f1.base.Table;
import com.f1.utils.TableHelper;
import com.f1.utils.structs.table.columnar.ColumnarTable;

public class AmiImdbStatsBuilder {

	public static Map<String, Table> getStats(AmiImdbImpl db) {
		Map<String, Table> r = new LinkedHashMap<String, Table>();

		ColumnarTable tables = new ColumnarTable(String.class, "TableName", Integer.class, "RowsCount", Integer.class, "ColumnsCount");

		ColumnarTable triggers = new ColumnarTable(String.class, "TriggerName", String.class, "TableName", String.class, "Type", Long.class, "ExecutedCount", Double.class,
				"MillisSpent", Double.class, "AvgMillisSpent", long.class, "ErrorsCount", long.class, "ReturnedFalseCount");

		ColumnarTable procs = new ColumnarTable(String.class, "ProcedureName", Long.class, "ExecutedCount", Double.class, "MillisSpent", Double.class, "AvgMillisSpent", long.class,
				"ErrorsCount");

		ColumnarTable timers = new ColumnarTable(String.class, "TimerName", Long.class, "ExecutedCount", Double.class, "MillisSpent", Double.class, "AvgMillisSpent", long.class,
				"ErrorsCount");

		tables.setTitle("__STATS_TABLE");
		triggers.setTitle("__STATS_TRIGGER");
		procs.setTitle("__STATS_PROCEDURE");
		timers.setTitle("__STATS_TIMER");
		for (AmiTable i : db.getAmiTables())
			tables.getRows().addRow(i.getName(), i.getRowsCount(), i.getColumnsCount());

		for (AmiStoredProcBindingImpl i : db.getObjectsManager().getAmiStoredProcBindings())
			procs.getRows().addRow(i.getStoredProcName(), i.getStatsCount(), i.getStatsNanos() / 1000000d, (i.getStatsNanos() / 1000000d) / i.getStatsCount(), i.getStatsErrors());

		for (AmiTriggerBindingImpl trigger : db.getObjectsManager().getAmiTriggerBindings())
			for (byte type : AmiTableUtils.TRIGGER_TYPES)
				if (trigger.isSupported(type))
					triggers.getRows().addRow(trigger.getTriggerName(), trigger.getTableNames(), AmiTableUtils.toStringForTriggerType(type), trigger.getStatsCount(type),
							trigger.getStatsNanos(type) / 1000000d, (trigger.getStatsNanos(type) / 1000000d) / trigger.getStatsCount(type), trigger.getStatsErrors(type),
							trigger.getStatsReturnedFalse(type));

		for (AmiTimerBindingImpl i : db.getObjectsManager().getAmiTimerBindings())
			timers.getRows().addRow(i.getTimerName(), i.getStatsCount(), i.getStatsNanos() / 1000000d, (i.getStatsNanos() / 1000000d) / i.getStatsCount(), i.getStatsErrors());

		TableHelper.sortDesc(tables, "RowsCount");
		TableHelper.sortDesc(procs, "MillisSpent");
		TableHelper.sortDesc(triggers, "MillisSpent");
		TableHelper.sortDesc(timers, "MillisSpent");
		r.put(triggers.getTitle(), triggers);
		r.put(timers.getTitle(), timers);
		return r;
	}
}
