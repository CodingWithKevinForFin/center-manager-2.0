package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.Table;
import com.f1.base.VID;

@VID("F1.VE.PSFR")
public interface AmiWebManagerProcessSpecialFileResponse extends AmiCenterResponse {

	@PID(1)
	Table getResults();
	public void setResults(Table file);

}
