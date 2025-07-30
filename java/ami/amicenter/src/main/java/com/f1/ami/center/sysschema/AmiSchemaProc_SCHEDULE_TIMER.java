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
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiSchemaProc_SCHEDULE_TIMER extends AmiAbstractStoredProc {

	private static final String TYPE = "__SYSTEM";
	private static final String NAME = "__SCHEDULE_TIMER";
	private static final List<AmiFactoryOption> __ARGUMENTS = new ArrayList<AmiFactoryOption>();
	static {
		__ARGUMENTS.add(new AmiFactoryOption("TimerName", String.class, true));
		__ARGUMENTS.add(new AmiFactoryOption("DelayMillis", Long.class, true));
	}
	private AmiImdbImpl imdb;

	public AmiSchemaProc_SCHEDULE_TIMER(AmiImdbImpl imdb, CalcFrameStack sf) {
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
		if (!timer.getIsEnabled())
			throw new RuntimeException("TIMER is not enabled, run: ENABLE TIMER " + name);
		Long delta = Caster_Long.INSTANCE.cast(arguments.get(1), false, false);
		if (delta != null)
			timer.setNextRunTime(System.currentTimeMillis() + delta);
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
