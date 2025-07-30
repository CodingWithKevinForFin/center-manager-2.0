package com.f1.ami.center.timers;

import com.f1.ami.center.table.AmiCenterProcess;
import com.f1.ami.center.table.AmiImdb;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public interface AmiTimer {

	/**
	 * Called once at startup, this is a good spot to grab needed tables, columns, indexes, etc that you will use at runtime.
	 * 
	 * @param imdb
	 */
	public void startup(AmiImdb imdb, AmiTimerBinding bindings, CalcFrameStack sf);
	public boolean onTimer(long scheduledTime, AmiCenterProcess process, CalcFrameStack sf);
	public void onSchemaChanged(AmiImdb imdb, CalcFrameStack sf);

}
