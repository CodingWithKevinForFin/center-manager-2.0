package com.f1.ami.center.hdb.events;

import com.f1.ami.amicommon.msg.AmiCenterResponse;
import com.f1.ami.center.hdb.AmiHdbSqlFlowControl;
import com.f1.base.PID;

public interface AmiHdbResponse extends AmiCenterResponse {

	@PID(1)
	public AmiHdbSqlFlowControl getSqlFlowControl();
	public void setSqlFlowControl(AmiHdbSqlFlowControl fc);

}
