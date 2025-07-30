package com.f1.ami.center.sysschema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.ami.center.hdb.AmiHdb;
import com.f1.ami.center.procs.AmiAbstractStoredProc;
import com.f1.ami.center.procs.AmiStoredProcBindingImpl;
import com.f1.ami.center.procs.AmiStoredProcRequest;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiSchemaProc_MARK_HISTORICAL_PARTITION_FOR_APPEND extends AmiAbstractStoredProc {

	private static final String TYPE = "__SYSTEM";
	private static final String NAME = "__MARK_HISTORICAL_PARTITION_FOR_APPEND";
	private static final List<AmiFactoryOption> __ARGUMENTS = new ArrayList<AmiFactoryOption>();
	static {
		__ARGUMENTS.add(new AmiFactoryOption("TableName", String.class, true));
		__ARGUMENTS.add(new AmiFactoryOption("PartitionID", Integer.class, true));
	}
	final private AmiImdbImpl imdb;
	final private AmiHdb hdb;

	public AmiSchemaProc_MARK_HISTORICAL_PARTITION_FOR_APPEND(AmiImdbImpl imdb, CalcFrameStack sf) {
		this.imdb = imdb;
		this.hdb = imdb.getState().getHdb();
		this.imdb.getObjectsManager()
				.addAmiStoredProcBinding(new AmiStoredProcBindingImpl(this.imdb, NAME, this, TYPE, Collections.EMPTY_MAP, Collections.EMPTY_MAP, AmiTableUtils.DEFTYPE_SYSTEM), sf);
	}
	@Override
	public FlowControl execute(AmiStoredProcRequest request, CalcFrameStack sf) throws Exception {
		List<Object> arguments = request.getArguments();
		String name = (String) arguments.get(0);
		Integer pid = (Integer) arguments.get(1);
		hdb.markPartitionForAppend(name, pid, sf.getTimeoutController());
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
