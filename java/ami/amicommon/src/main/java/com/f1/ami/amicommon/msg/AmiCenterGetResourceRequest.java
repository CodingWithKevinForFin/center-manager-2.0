package com.f1.ami.amicommon.msg;

import java.util.List;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.GREQ")
public interface AmiCenterGetResourceRequest extends Message {

	@PID(1)
	public List<String> getPaths();
	public void setPaths(List<String> name);

}
