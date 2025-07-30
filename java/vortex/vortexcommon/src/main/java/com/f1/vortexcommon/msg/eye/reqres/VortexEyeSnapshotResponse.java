package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.VortexEyeChanges;

@VID("F1.VE.SR")
public interface VortexEyeSnapshotResponse extends VortexEyeResponse {

	@PID(1)
	public VortexEyeChanges getSnapshot();
	public void setSnapshot(VortexEyeChanges changes);

	@PID(2)
	public void setTotalObjectsCount(int totalObjects);
	public int getTotalObjectsCount();

	//@PID(1)
	//public void setVortexEntities(List<VortexEntity> snapshot);
	//public List<VortexEntity> getVortexEntities();

	//@PID(3)
	//public List<F1AppEntity> getF1AppEntities();
	//public void setF1AppEntities(List<F1AppEntity> f1Apps);

}
