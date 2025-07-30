package com.f1.ami.amicommon.msg;

import java.util.Map;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.WM.PSFQ")
public interface AmiWebManagerProcessSpecialFileRequest extends AmiCenterRequest {

	@PID(1)
	String getFileName();
	public void setFileName(String fileName);

	@PID(2)
	Map<String, ?> getOptions();
	public void setOptions(Map<String, ?> fileName);

	@PID(3)
	String getInstruction();//See AmiSpecialFileProcessor.java
	public void setInstruction(String fileName);

}
