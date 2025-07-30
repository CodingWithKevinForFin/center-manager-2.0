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
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiSchemaProc_REMOVE_REPLICATION extends AmiAbstractStoredProc {

	private static final String TYPE = "__SYSTEM";
	private static final String NAME = "__REMOVE_REPLICATION";
	private static final List<AmiFactoryOption> __ARGUMENTS = new ArrayList<AmiFactoryOption>();
	static {
		__ARGUMENTS.add(new AmiFactoryOption("Name", String.class, true));
	}
	private AmiImdbImpl imdb;

	public AmiSchemaProc_REMOVE_REPLICATION(AmiImdbImpl imdb, CalcFrameStack sf) {
		this.imdb = imdb;
		this.imdb.getObjectsManager()
				.addAmiStoredProcBinding(new AmiStoredProcBindingImpl(this.imdb, NAME, this, TYPE, Collections.EMPTY_MAP, Collections.EMPTY_MAP, AmiTableUtils.DEFTYPE_SYSTEM), sf);
	}
	@Override
	public FlowControl execute(AmiStoredProcRequest request, CalcFrameStack sf) throws Exception {
		List<Object> arguments = request.getArguments();
		String name = (String) arguments.get(0);
		imdb.getReplicator().removeReplication(name, sf);
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
