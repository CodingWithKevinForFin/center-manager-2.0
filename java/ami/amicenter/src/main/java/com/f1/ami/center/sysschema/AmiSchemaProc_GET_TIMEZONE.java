package com.f1.ami.center.sysschema;

import java.util.Collections;
import java.util.List;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.ami.center.AmiCenterUtils;
import com.f1.ami.center.procs.AmiAbstractStoredProc;
import com.f1.ami.center.procs.AmiStoredProcBindingImpl;
import com.f1.ami.center.procs.AmiStoredProcRequest;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.utils.sql.TableReturn;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiSchemaProc_GET_TIMEZONE extends AmiAbstractStoredProc {

	private static final String TYPE = "__SYSTEM";
	private static final String NAME = "__GET_TIMEZONE";

	//	private static final List<AmiFactoryOption> __ARGUMENTS = new ArrayList<AmiFactoryOption>();
	//	static {
	//	}

	public AmiSchemaProc_GET_TIMEZONE(AmiImdbImpl imdb, CalcFrameStack sf) {
		imdb.getObjectsManager()
				.addAmiStoredProcBinding(new AmiStoredProcBindingImpl(imdb, NAME, this, TYPE, Collections.EMPTY_MAP, Collections.EMPTY_MAP, AmiTableUtils.DEFTYPE_SYSTEM), sf);
	}
	@Override
	public FlowControl execute(AmiStoredProcRequest request, CalcFrameStack sf) throws Exception {
		AmiImdbSession service = AmiCenterUtils.getService(sf);
		String tzid = service.getTimezoneId();
		//		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		//		String tzid = session.getTimezoneId();
		BasicTable bt = new BasicTable(String.class, "Timezone");
		bt.setTitle("System Timezone");
		bt.getRows().addRow(tzid);
		return new TableReturn(bt);
	}

	@Override
	protected void onStartup(CalcFrameStack sf) {
	}

	@Override
	public List<AmiFactoryOption> getArguments() {
		return Collections.EMPTY_LIST;
	}
}
