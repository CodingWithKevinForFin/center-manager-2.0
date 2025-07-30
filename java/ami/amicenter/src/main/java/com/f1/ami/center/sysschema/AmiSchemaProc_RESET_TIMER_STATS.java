package com.f1.ami.center.sysschema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.ami.center.procs.AmiAbstractStoredProc;
import com.f1.ami.center.procs.AmiStoredProcBindingImpl;
import com.f1.ami.center.procs.AmiStoredProcRequest;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.ami.center.timers.AmiTimerBindingImpl;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiSchemaProc_RESET_TIMER_STATS extends AmiAbstractStoredProc {

	private static final String TYPE = "__SYSTEM";
	private static final String NAME = "__RESET_TIMER_STATS";
	private static final List<AmiFactoryOption> __ARGUMENTS = new ArrayList<AmiFactoryOption>();
	static {
		__ARGUMENTS.add(new AmiFactoryOption("TimerName", String.class, true));
		__ARGUMENTS.add(new AmiFactoryOption("ExecutedStats", Boolean.class, true));
		__ARGUMENTS.add(new AmiFactoryOption("ErrorStats", Boolean.class, true));
	}
	private AmiImdbImpl imdb;

	public AmiSchemaProc_RESET_TIMER_STATS(AmiImdbImpl imdb, CalcFrameStack sf) {
		this.imdb = imdb;
		this.imdb.getObjectsManager()
				.addAmiStoredProcBinding(new AmiStoredProcBindingImpl(this.imdb, NAME, this, TYPE, Collections.EMPTY_MAP, Collections.EMPTY_MAP, AmiTableUtils.DEFTYPE_SYSTEM), sf);
	}
	@Override
	public FlowControl execute(AmiStoredProcRequest request, CalcFrameStack sf) throws Exception {
		List<Object> arguments = request.getArguments();

		String name = (String) arguments.get(0);
		AmiTimerBindingImpl timer = this.imdb.getObjectsManager().getAmiTimerBinding(name);
		if (timer == null)
			throw new RuntimeException("TIMER not found: '" + name + "'");
		Boolean executedCount = (Boolean) arguments.get(1);
		Boolean errorsCount = (Boolean) arguments.get(2);
		timer.clearCount(Boolean.TRUE.equals(executedCount), Boolean.TRUE.equals(errorsCount));
		return null;
	}

	@Override
	public List<AmiFactoryOption> getArguments() {
		return __ARGUMENTS;
	}

	@Override
	protected void onStartup(CalcFrameStack sf) {
	}

}
