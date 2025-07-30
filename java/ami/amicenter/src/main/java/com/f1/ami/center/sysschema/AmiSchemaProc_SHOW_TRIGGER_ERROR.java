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
import com.f1.ami.center.triggers.AmiTriggerBindingImpl;
import com.f1.base.DateMillis;
import com.f1.utils.SH;
import com.f1.utils.sql.TableReturn;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiSchemaProc_SHOW_TRIGGER_ERROR extends AmiAbstractStoredProc {

	private static final String TYPE = "__SYSTEM";
	private static final String NAME = "__SHOW_TRIGGER_ERROR";
	private static final List<AmiFactoryOption> __ARGUMENTS = new ArrayList<AmiFactoryOption>();
	static {
		__ARGUMENTS.add(new AmiFactoryOption("TriggerName", String.class, true));
	}
	private AmiImdbImpl imdb;

	public AmiSchemaProc_SHOW_TRIGGER_ERROR(AmiImdbImpl imdb, CalcFrameStack sf) {
		this.imdb = imdb;
		this.imdb.getObjectsManager()
				.addAmiStoredProcBinding(new AmiStoredProcBindingImpl(this.imdb, NAME, this, TYPE, Collections.EMPTY_MAP, Collections.EMPTY_MAP, AmiTableUtils.DEFTYPE_SYSTEM), sf);
	}
	@Override
	public TableReturn execute(AmiStoredProcRequest request, CalcFrameStack sf) throws Exception {
		List<Object> arguments = request.getArguments();

		String name = (String) arguments.get(0);
		AmiTriggerBindingImpl trigger = this.imdb.getObjectsManager().getAmiTriggerBinding(name);
		if (trigger == null)
			throw new RuntimeException("TRIGGER not found: '" + name + "'");
		BasicTable bt = new BasicTable(DateMillis.class, "Time", String.class, "Error");
		bt.setTitle("TRIGGER_ERROR_" + name);
		Exception e = trigger.getLastException();
		if (e != null) {
			final String lines[];
			if (e instanceof ExpressionParserException) {
				String s = ((ExpressionParserException) e).toLegibleString();
				lines = SH.splitLines(s);
			} else
				lines = SH.splitLines(SH.printStackTrace(e));
			for (int i = 0; i < lines.length; i++)
				bt.getRows().addRow(i == 0 ? trigger.getLastExceptionTime() : null, lines[i]);
		}
		return new TableReturn(bt);
	}

	@Override
	public List<AmiFactoryOption> getArguments() {
		return __ARGUMENTS;
	}

	@Override
	protected void onStartup(CalcFrameStack sf) {
	}

}
