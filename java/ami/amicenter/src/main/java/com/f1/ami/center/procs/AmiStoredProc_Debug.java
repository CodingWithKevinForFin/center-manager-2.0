package com.f1.ami.center.procs;

import java.util.List;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.sql.TableReturn;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;

public class AmiStoredProc_Debug extends AmiAbstractStoredProc {

	private static final Logger log = LH.get();
	private List<AmiFactoryOption> arguments = CH.l(new AmiFactoryOption("test", String.class), new AmiFactoryOption("test2", Integer.class, true),
			new AmiFactoryOption("test3", Double.class, false));
	private String name;

	public AmiStoredProc_Debug() {
	}

	@Override
	public FlowControl execute(AmiStoredProcRequest arguments, CalcFrameStack sf) throws Exception {
		getImdb().getScriptManager().executeSql((String) arguments.getArguments().get(0), EmptyCalcFrame.INSTANCE, null, AmiConsts.DEFAULT, null, sf);
		LH.info(log, name + " :Ran with arguments: " + SH.join(',', arguments));
		return new TableReturn(toSingletonTable("Result", "Result", String.class, "OKAY"));
	}

	@Override
	public List<AmiFactoryOption> getArguments() {
		return arguments;
	}

	@Override
	public Class getReturnType() {
		return String.class;
	}

	@Override
	protected void onStartup(CalcFrameStack sf) {
		this.name = getBinding().getStoredProcName();
	}

}
