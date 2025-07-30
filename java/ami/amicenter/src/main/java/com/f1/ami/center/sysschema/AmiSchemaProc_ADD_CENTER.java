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

public class AmiSchemaProc_ADD_CENTER extends AmiAbstractStoredProc {

	private static final String TYPE = "__SYSTEM";
	private static final String NAME = "__ADD_CENTER";
	private static final List<AmiFactoryOption> __ARGUMENTS = new ArrayList<AmiFactoryOption>();
	static {
		__ARGUMENTS.add(new AmiFactoryOption("CenterName", String.class, true));
		__ARGUMENTS.add(new AmiFactoryOption("Url", String.class, true));
		__ARGUMENTS.add(new AmiFactoryOption("CertFile", String.class, false));
		__ARGUMENTS.add(new AmiFactoryOption("Password", String.class, false));
	}
	private AmiImdbImpl imdb;

	public AmiSchemaProc_ADD_CENTER(AmiImdbImpl imdb, CalcFrameStack sf) {
		this.imdb = imdb;
		this.imdb.getObjectsManager()
				.addAmiStoredProcBinding(new AmiStoredProcBindingImpl(this.imdb, NAME, this, TYPE, Collections.EMPTY_MAP, Collections.EMPTY_MAP, AmiTableUtils.DEFTYPE_SYSTEM), sf);
	}
	@Override
	public FlowControl execute(AmiStoredProcRequest request, CalcFrameStack sf) throws Exception {
		List<Object> arguments = request.getArguments();

		String centerName = (String) arguments.get(0);
		String url = (String) arguments.get(1);
		String certFile = (String) arguments.get(2);
		String password = (String) arguments.get(3);
		imdb.getReplicator().addCenter(centerName, url, certFile, password, sf);
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
