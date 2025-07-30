package com.f1.ami.web;

import java.util.logging.Logger;

import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.base.CalcFrame;
import com.f1.base.DateMillis;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.sql.TablesetImpl;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;

public class AmiWebTimedEvent_Calc extends AmiWebTimedEvent {

	private static final Logger log = LH.get();
	final private DerivedCellCalculator calc;
	final private String layoutAlias;
	final private String script;
	private Integer limit;
	private Integer timeout;
	private String defaultDs;

	public AmiWebTimedEvent_Calc(String layoutAlias, String script, DerivedCellCalculator calc) {
		this.calc = calc;
		this.script = script;
		this.layoutAlias = layoutAlias;
	}
	public AmiWebTimedEvent_Calc(String layoutAlias, String script, int timeout, int limit, String defaultDs, DerivedCellCalculator calc) {
		this.calc = calc;
		this.script = script;
		this.layoutAlias = layoutAlias;
		this.limit = limit;
		this.timeout = timeout;
		this.defaultDs = defaultDs;
	}

	@Override
	public String toString() {
		return "AmiWebTimedEvent[id=" + getId() + "  time=" + DateMillis.format(getTime()) + ", calc='" + calc + "']";
	}

	@Override
	public void execute(AmiWebService s) {
		StringBuilder errorSink = new StringBuilder();
		AmiWebScriptManagerForLayout sm = s.getScriptManager(this.layoutAlias);
		CalcFrame calcFrame = null;
		try {
			Object retValue = sm.executeAmiScript(this.script, errorSink, this.calc, calcFrame, s.getDebugManager(), AmiDebugMessage.TYPE_EVENT_EXECUTED, sm.getFile(),
					"TimedEvent-" + getId(), new TablesetImpl(), this.timeout == null ? s.getDefaultTimeoutMs() : this.timeout,
					this.limit == null ? s.getDefaultLimit() : this.limit, this.defaultDs);
			if (s.getDebugManager().shouldDebug(AmiDebugMessage.SEVERITY_INFO) && !s.getScriptManager().getShouldDebugExecutedAmiScript())
				s.getDebugManager().addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_INFO, AmiDebugMessage.TYPE_EVENT_EXECUTED, s.getAri(), null, "Event " + getId(),
						CH.m("return value", retValue, "Event Id", getId() + " executed", "Warnings", errorSink.toString()), null));
		} catch (Throwable t) {
			LH.warning(log, s.getPortletManager().describeUser() + " ==> timed event at '" + new DateMillis(getTime()).toLegibleString() + "' '" + calc + "' failed: ", t);
			if (s.getDebugManager().shouldDebug(AmiDebugMessage.SEVERITY_WARNING))
				s.getDebugManager().addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_INFO, AmiDebugMessage.TYPE_EVENT_EXECUTED, s.getAri(), null, "Event " + getId(),
						CH.m("Event Id", this.getId(), "Warnings", errorSink.toString()), t));
		}
		if (errorSink.length() > 0)
			LH.warning(log, s.getPortletManager().describeUser() + " ==> timed event at '" + new DateMillis(getTime()).toLegibleString() + "' '" + calc + "' generated warning: ",
					errorSink);

	}

	@Override
	public String describe() {
		return calc.toString();
	}

}
