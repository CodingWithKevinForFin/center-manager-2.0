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

public class AmiSchemaProc_ADD_DATASOURCE extends AmiAbstractStoredProc {

	private static final String TYPE = "__SYSTEM";
	private static final String NAME = "__ADD_DATASOURCE";
	private static final List<AmiFactoryOption> __ARGUMENTS = new ArrayList<AmiFactoryOption>();
	static {
		__ARGUMENTS.add(new AmiFactoryOption("Name", String.class, true));
		__ARGUMENTS.add(new AmiFactoryOption("DatasourceType", String.class, true));
		__ARGUMENTS.add(new AmiFactoryOption("URL", String.class, true));
		__ARGUMENTS.add(new AmiFactoryOption("Username", String.class, false));
		__ARGUMENTS.add(new AmiFactoryOption("Password", String.class, false));
		__ARGUMENTS.add(new AmiFactoryOption("Options", String.class, false));
		__ARGUMENTS.add(new AmiFactoryOption("RelayId", String.class, false));
		__ARGUMENTS.add(new AmiFactoryOption("PermittedOverrides", String.class, false));
	}
	private AmiImdbImpl imdb;

	public AmiSchemaProc_ADD_DATASOURCE(AmiImdbImpl imdb, CalcFrameStack sf) {
		this.imdb = imdb;
		this.imdb.getObjectsManager()
				.addAmiStoredProcBinding(new AmiStoredProcBindingImpl(this.imdb, NAME, this, TYPE, Collections.EMPTY_MAP, Collections.EMPTY_MAP, AmiTableUtils.DEFTYPE_SYSTEM), sf);
	}
	@Override
	public FlowControl execute(AmiStoredProcRequest request, CalcFrameStack sf) throws Exception {
		List<Object> arguments = request.getArguments();

		String name = (String) arguments.get(0);
		String adapter = (String) arguments.get(1);
		String url = (String) arguments.get(2);
		String username = (String) arguments.get(3);
		String password = (String) arguments.get(4);
		String options = (String) arguments.get(5);
		String relayId = (String) arguments.get(6);
		String permittedOverrides = (String) arguments.get(7);
		AmiSchema_DATASOURCE __DATASOURCE = imdb.getSystemSchema().__DATASOURCE;
		String encryptedPassword = imdb.getState().encrypt(password);
		if (__DATASOURCE.fastLookup.getByName(name) != null)
			throw new RuntimeException("Name already exists: '" + name + "'");
		if (imdb.getSystemSchema().__DATASOURCE_TYPE.findById(adapter) == null)
			throw new RuntimeException("DATASOURCE_TYPE does not exists: '" + adapter + "'");
		__DATASOURCE.addDatasource(-1L, adapter, name, options, encryptedPassword, url, username, false, null, relayId, permittedOverrides, sf);
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
