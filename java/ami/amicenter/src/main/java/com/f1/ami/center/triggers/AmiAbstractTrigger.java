package com.f1.ami.center.triggers;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.f1.ami.center.table.AmiImdb;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public abstract class AmiAbstractTrigger implements AmiTrigger {

	private AmiImdbImpl imdb;
	private AmiTriggerBinding binding;

	@Override
	final public void startup(AmiImdb imdb, AmiTriggerBinding binding, CalcFrameStack sf) {
		this.imdb = (AmiImdbImpl) imdb;
		this.binding = binding;
		onStartup(sf);
	}

	abstract protected void onStartup(CalcFrameStack sf);

	@Override
	public boolean onUpdating(AmiTable table, AmiRow row, AmiPreparedRow updatingTo, CalcFrameStack sf) {
		return onUpdating(table, row, sf);
	}

	public AmiImdbImpl getImdb() {
		return imdb;
	}

	@Override
	public AmiTriggerBinding getBinding() {
		return binding;
	}

	@Override
	public boolean onInserting(AmiTable table, AmiRow row, CalcFrameStack sf) {
		return true;
	}
	@Override
	public void onInserted(AmiTable table, AmiRow row, CalcFrameStack sf) {
	}

	@Override
	public void onInitialized(CalcFrameStack sf) {
	}

	@Override
	public boolean onUpdating(AmiTable table, AmiRow row, CalcFrameStack sf) {
		return true;
	}

	@Override
	public void onUpdated(AmiTable table, AmiRow row, CalcFrameStack sf) {
	}

	@Override
	public boolean onDeleting(AmiTable table, AmiRow row, CalcFrameStack sf) {
		return true;
	}

	@Override
	public void onSchemaChanged(AmiImdb imdb, CalcFrameStack sf) {
	}

	@Override
	public void onEnabled(boolean enable, CalcFrameStack sf) {

	}

	@Override
	public boolean isSupported(byte type) {
		return true;
	}

	@Override
	public Set<String> getLockedTables() {
		return Collections.EMPTY_SET;
	}

	@Override
	public List<String> getBindingTables() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public void onClosed() {
	}

	@Override
	public void onUpdatingRejected(AmiTable table, AmiRow row) {
	}

}
