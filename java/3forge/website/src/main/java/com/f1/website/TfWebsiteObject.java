package com.f1.website;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("TF.WS.OBJ")
public interface TfWebsiteObject extends Message {
	long NULL_ID = -1;
	int REVISION_DELETED = Integer.MAX_VALUE;
	int REVISION_NEW = 0;
	byte PID_ID = 1;
	byte PID_CREATED_ON = 2;
	byte PID_REVISION = 3;
	byte PID_MODIFIED_ON = 4;

	@PID(1)
	public long getId();
	public void setId(long id);

	@PID(PID_CREATED_ON)
	public long getCreatedOn();
	public void setCreatedOn(long id);

	@PID(PID_REVISION)
	public int getRevision();
	public void setRevision(int now);

	@PID(PID_MODIFIED_ON)
	public long getModifiedOn();
	public void setModifiedOn(long id);

}
