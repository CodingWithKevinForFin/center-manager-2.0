package com.f1.ami.amicommon.msg;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.LRR")
public interface AmiWebManagerListRootsResponse extends AmiCenterResponse {

	@PID(1)
	List<AmiFileMessage> getFiles();
	public void setFiles(List<AmiFileMessage> file);

}
