package com.f1.ami.center.procs;

import java.util.List;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.ami.center.table.AmiImdb;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public interface AmiStoredProc {

	public FlowControl execute(AmiStoredProcRequest arguments, CalcFrameStack sf) throws Exception;
	public void startup(AmiImdb imdb, AmiStoredProcBinding binding, CalcFrameStack sf);

	public List<AmiFactoryOption> getArguments();
	public Class getReturnType();

	public void onSchemaChanged(AmiImdbImpl db, CalcFrameStack sf);

}
