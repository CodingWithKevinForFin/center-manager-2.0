package com.f1.ami.center.triggers;

import com.f1.ami.center.table.AmiImdb;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public abstract class AmiAbstractTriggerPlugin implements AmiTriggerPlugin {

	private AmiImdb imdb;
	private PropertyController props;
	private String name;

	@Override
	public void init(ContainerTools tools, PropertyController props) {
		this.props = props;
	}

	public PropertyController getProperties() {
		return this.props;
	}

	public AmiImdb getImdb() {
		return this.imdb;
	}

	@Override
	public void startup(AmiImdb imdb, AmiTriggerBinding binding, CalcFrameStack sf) {
		this.imdb = imdb;
	}

	@Override
	public boolean onInserting(AmiTable table, AmiRow row, CalcFrameStack sf) {
		return true;
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
	public void onInserted(AmiTable table, AmiRow row, CalcFrameStack sf) {
	}

	@Override
	public boolean onUpdating(AmiTable table, AmiRow row, AmiPreparedRow updatingTo, CalcFrameStack sf) {
		return onUpdating(table, row, sf);
	}

}
