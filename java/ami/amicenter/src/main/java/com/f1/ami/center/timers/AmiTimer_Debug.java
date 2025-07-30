package com.f1.ami.center.timers;

import java.util.Date;
import java.util.logging.Logger;

import com.f1.ami.center.table.AmiCenterProcess;
import com.f1.ami.center.table.AmiImdb;
import com.f1.utils.LH;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiTimer_Debug extends AmiAbstractTimer {

	private static final Logger log = LH.get();
	private String name;

	@Override
	public boolean onTimer(long scheduledTime, AmiCenterProcess process, CalcFrameStack sf) {
		LH.info(log, getBinding().getTimerName() + " onTimer: " + new Date(scheduledTime));
		return true;
	}

	@Override
	public void onSchemaChanged(AmiImdb imdb, CalcFrameStack sf) {
		LH.info(log, getBinding().getTimerName() + " onSchemaChanged");
	}

	@Override
	protected void onStartup(CalcFrameStack sf) {
	}
}
