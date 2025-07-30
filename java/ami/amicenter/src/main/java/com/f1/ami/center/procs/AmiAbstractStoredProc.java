package com.f1.ami.center.procs;

import java.util.Collections;
import java.util.List;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.ami.center.table.AmiImdb;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public abstract class AmiAbstractStoredProc implements AmiStoredProc {

	private AmiImdbImpl imdb;
	private AmiStoredProcBinding binding;

	@Override
	final public void startup(AmiImdb imdb, AmiStoredProcBinding binding, CalcFrameStack sf) {
		this.imdb = (AmiImdbImpl) imdb;
		this.binding = binding;
		onStartup(sf);
	}

	abstract protected void onStartup(CalcFrameStack sf);

	@Override
	public List<AmiFactoryOption> getArguments() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public Class getReturnType() {
		return Object.class;
	}

	@Override
	public void onSchemaChanged(AmiImdbImpl db, CalcFrameStack sf) {
	}

	protected AmiImdbImpl getImdb() {
		return this.imdb;
	}
	protected AmiStoredProcBinding getBinding() {
		return this.binding;
	}

	protected ContainerTools getTools() {
		return this.imdb.getTools();
	}

	protected static <T> Table toSingletonTable(String tableName, String columnName, Class<T> type, T value) {
		final Table r = new BasicTable(type, columnName);
		r.setTitle(tableName);
		final Row row = r.newEmptyRow();
		row.putAt(0, value);
		r.getRows().add(row);
		return r;
	}
}
