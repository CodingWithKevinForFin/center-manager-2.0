package com.f1.povo.f1app.reqres;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.povo.f1app.F1AppEntity;

@VID("F1.FA.CHR")
public interface F1AppChangesResponse extends F1AppResponse {

	byte PID_ADDED = 1;
	@PID(PID_ADDED)
	public void setAdded(List<F1AppEntity> added);
	public List<F1AppEntity> getAdded();

	byte PID_REMOVED = 2;
	@PID(PID_REMOVED)
	public void setRemoved(long[] removed);
	public long[] getRemoved();

	byte PID_UPDATED = 3;
	@PID(PID_UPDATED)
	public void setUpdated(byte[] updated);
	public byte[] getUpdated();

}
