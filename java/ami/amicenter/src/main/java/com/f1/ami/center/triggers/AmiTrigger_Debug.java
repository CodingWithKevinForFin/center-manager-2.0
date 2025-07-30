package com.f1.ami.center.triggers;

import java.util.logging.Logger;

import com.f1.ami.center.table.AmiImdb;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.utils.LH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiTrigger_Debug extends AmiAbstractTrigger {

	private Logger log = LH.get();
	private boolean oni;
	private boolean onu;
	private boolean ond;
	private String triggerName;

	public AmiTrigger_Debug() {
	}

	@Override
	public boolean onInserting(AmiTable table, AmiRow row, CalcFrameStack sf) {
		debug("onInserting", table, row);
		return oni;
	}

	@Override
	public void onInserted(AmiTable table, AmiRow row, CalcFrameStack sf) {
		debug("onInsert", table, row);

	}

	@Override
	public boolean onUpdating(AmiTable table, AmiRow row, CalcFrameStack sf) {
		debug("onUpdating", table, row);
		return onu;
	}

	@Override
	public void onUpdated(AmiTable table, AmiRow row, CalcFrameStack sf) {
		debug("onUpdated", table, row);

	}

	@Override
	public boolean onDeleting(AmiTable table, AmiRow row, CalcFrameStack sf) {
		debug("onDelete", table, row);
		return ond;
	}

	private void debug(String string, AmiTable table, AmiRow row) {
		LH.info(log, table.getName(), "::", triggerName, " ", string, " ==> ", row);
	}

	@Override
	public boolean onUpdating(AmiTable table, AmiRow row, AmiPreparedRow updatingTo, CalcFrameStack sf) {
		LH.info(log, table.getName(), "::", triggerName, " ", "onUpdating ", " ==> ", row, " to ", updatingTo);
		return onu;
	}
	@Override
	public void onSchemaChanged(AmiImdb imdb, CalcFrameStack sf) {
		LH.info(log, "::", triggerName, " SCHEMA_CHANGED");
	}

	@Override
	protected void onStartup(CalcFrameStack sf) {
		boolean oni = getBinding().getOption(Caster_Boolean.INSTANCE, "returnOnInsert", Boolean.TRUE);
		boolean onu = getBinding().getOption(Caster_Boolean.INSTANCE, "returnOnUpdate", Boolean.TRUE);
		boolean ond = getBinding().getOption(Caster_Boolean.INSTANCE, "returnOnDelete", Boolean.TRUE);
		this.triggerName = getBinding().getTriggerName();
		this.oni = oni;
		this.onu = onu;
		this.ond = ond;
		LH.info(log, "Trigger started up");
	}
}
