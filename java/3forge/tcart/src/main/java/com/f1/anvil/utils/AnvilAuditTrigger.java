package com.f1.anvil.utils;

import com.f1.ami.center.table.AmiColumn;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.triggers.AmiAbstractTrigger;
import com.f1.container.ContainerTools;
import com.f1.utils.structs.table.derived.StackFrame;

public class AnvilAuditTrigger extends AmiAbstractTrigger {

	private String timeColumnName;
	private String targetTableName;
	private String sourceTableName;
	private AnvilTableCopier copier;
	private boolean copyCurrentToo;
	private String statusColumnName;
	private AmiColumn statusColumn;
	private String statusInsertValue = "I";
	private String statusUpdateValue = "U";
	private String statusDeleteValue = "D";

	@Override
	public void onStartup(AmiImdbSession session) {
		ContainerTools props = getImdb().getTools();
		this.timeColumnName = props.getOptional("target.time.column");
		this.statusColumnName = props.getOptional("target.status.column");
		this.targetTableName = props.getRequired("target.table");
		this.sourceTableName = props.getRequired("source.table");
		this.statusInsertValue = props.getOptional("status.insert.value", this.statusInsertValue);
		this.statusUpdateValue = props.getOptional("status.update.value", this.statusUpdateValue);
		this.statusDeleteValue = props.getOptional("status.delete.value", this.statusDeleteValue);
		this.copyCurrentToo = props.getRequired("copy.current.too", boolean.class);
		AmiTable target = getImdb().getAmiTable(this.targetTableName);
		AmiTable source = getImdb().getAmiTable(this.sourceTableName);
		this.copier = new AnvilTableCopier(source, target, this.timeColumnName == null ? null : target.getColumn(this.timeColumnName));
		this.statusColumn = this.statusColumnName == null ? null : target.getColumn(this.statusColumnName);
	}

	private void copy(AmiRow row, String action, AmiImdbSession session) {
		if (this.statusColumn != null)
			this.copier.getTargetSinkRomw().setString(statusColumn, action);
		this.copier.copy(row, getImdb().getNow(), session);
	}

	@Override
	public void onInserted(AmiTable table, AmiRow row, AmiImdbSession session, StackFrame sf) {
		if (copyCurrentToo)
			copy(row, this.statusInsertValue, session);
	}

	@Override
	public boolean onUpdating(AmiTable table, AmiRow row, AmiImdbSession session, StackFrame sf) {
		if (!copyCurrentToo)
			copy(row, this.statusUpdateValue, session);
		return true;
	}
	@Override
	public void onUpdated(AmiTable table, AmiRow row, AmiImdbSession session, StackFrame sf) {
		if (copyCurrentToo)
			copy(row, this.statusUpdateValue, session);
	}

	@Override
	public boolean onDeleting(AmiTable table, AmiRow row, AmiImdbSession session, StackFrame sf) {
		//if (!copyCurrentToo)
		copy(row, this.statusDeleteValue, session);
		return true;
	}

}
