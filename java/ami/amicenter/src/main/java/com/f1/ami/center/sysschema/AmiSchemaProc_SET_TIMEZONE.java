package com.f1.ami.center.sysschema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.ami.center.AmiCenterUtils;
import com.f1.ami.center.procs.AmiAbstractStoredProc;
import com.f1.ami.center.procs.AmiStoredProcBindingImpl;
import com.f1.ami.center.procs.AmiStoredProcRequest;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.utils.SH;
import com.f1.utils.sql.TableReturn;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiSchemaProc_SET_TIMEZONE extends AmiAbstractStoredProc {

	private static final String TYPE = "__SYSTEM";
	private static final String NAME = "__SET_TIMEZONE";

	private static final List<AmiFactoryOption> __ARGUMENTS = new ArrayList<AmiFactoryOption>();
	static {
		__ARGUMENTS.add(new AmiFactoryOption("timezone", String.class, true));
	}

	public AmiSchemaProc_SET_TIMEZONE(AmiImdbImpl imdb, CalcFrameStack sf) {
		imdb.getObjectsManager()
				.addAmiStoredProcBinding(new AmiStoredProcBindingImpl(imdb, NAME, this, TYPE, Collections.EMPTY_MAP, Collections.EMPTY_MAP, AmiTableUtils.DEFTYPE_SYSTEM), sf);
	}
	@Override
	public FlowControl execute(AmiStoredProcRequest request, CalcFrameStack sf) throws Exception {
		List<Object> arguments = request.getArguments();

		String timezoneid = (String) arguments.get(0);
		try {
			//			AmiImdbSession session = AmiCenterUtils.getSession(sf);
			//			session.setTimezone(timezoneid);
			AmiImdbSession service = AmiCenterUtils.getService(sf);
			service.setTimezone(timezoneid);
		} catch (NoSuchElementException e) {
			BasicTable bt = new BasicTable(String.class, "Message");
			bt.setTitle("SET_TIMEZONE_ERROR");
			bt.getRows().addRow(e.getLocalizedMessage());
			return new TableReturn(bt);
		} catch (Exception e) {
			BasicTable bt = new BasicTable(String.class, "Message");
			bt.setTitle("SET_TIMEZONE_ERROR");
			final String lines[];
			lines = SH.splitLines(SH.printStackTrace(e));
			for (int i = 0; i < lines.length; i++)
				bt.getRows().addRow(lines[i]);
			return new TableReturn(bt);
		}

		return null;
	}

	@Override
	protected void onStartup(CalcFrameStack sf) {
	}

	@Override
	public List<AmiFactoryOption> getArguments() {
		return __ARGUMENTS;
	}
}
