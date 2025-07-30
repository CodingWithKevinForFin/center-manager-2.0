package com.f1.povo.f1app.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.povo.f1app.F1AppChanges;

@VID("F1.FA.SSR")
public interface F1AppSnapshotResponse extends F1AppResponse {

	@PID(1)
	public void setSnapshot(F1AppChanges changes);
	public F1AppChanges getSnapshot();

}
