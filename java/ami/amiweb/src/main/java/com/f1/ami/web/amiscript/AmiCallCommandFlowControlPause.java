package com.f1.ami.web.amiscript;

import com.f1.ami.amicommon.msg.AmiCenterPassToRelayRequest;
import com.f1.ami.amicommon.msg.AmiCenterPassToRelayResponse;
import com.f1.ami.web.AmiWebCommandWrapper;
import com.f1.base.Table;
import com.f1.utils.sql.Tableset;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.FlowControlPause;

public class AmiCallCommandFlowControlPause extends FlowControlPause {

	private AmiWebCommandWrapper cmd;
	private Table table;
	private int timeout;
	private AmiCenterPassToRelayResponse response;
	private AmiCenterPassToRelayRequest request;

	public AmiCallCommandFlowControlPause(AmiWebCommandWrapper cmd, Table table, int timeout, DerivedCellCalculator position) {
		super(position);
		this.cmd = cmd;
		this.table = table;
		this.timeout = timeout;
	}

	public AmiWebCommandWrapper getCommand() {
		return this.cmd;
	}

	public Table getTable() {
		return this.table;
	}

	public int getTimeout() {
		return this.timeout;
	}

	public void processResponse(AmiCenterPassToRelayResponse response, Tableset ts) {
		this.response = response;
	}

	@Override
	public Object resume() {
		return super.resume();
	}

	public AmiCenterPassToRelayResponse getCommandResponse() {
		return this.response;
	}
	public AmiCenterPassToRelayRequest getCommandRequest() {
		return this.request;
	}

	public void setRequest(AmiCenterPassToRelayRequest request) {
		this.request = request;
	}

}
