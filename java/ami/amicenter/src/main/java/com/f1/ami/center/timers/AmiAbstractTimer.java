package com.f1.ami.center.timers;

import com.f1.ami.center.table.AmiImdb;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.container.ContainerTools;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public abstract class AmiAbstractTimer implements AmiTimer {

	private AmiImdbImpl imdb;
	private AmiTimerBinding binding;

	@Override
	public void startup(AmiImdb imdb, AmiTimerBinding binding, CalcFrameStack sf) {
		this.imdb = (AmiImdbImpl) imdb;
		this.binding = binding;
		onStartup(sf);
	}
	abstract protected void onStartup(CalcFrameStack sf);

	protected AmiImdbImpl getImdb() {
		return this.imdb;
	}
	protected AmiTimerBinding getBinding() {
		return this.binding;
	}

	protected ContainerTools getTools() {
		return this.imdb.getTools();
	}

	@Override
	public void onSchemaChanged(AmiImdb imdb, CalcFrameStack sf) {
	};

}
