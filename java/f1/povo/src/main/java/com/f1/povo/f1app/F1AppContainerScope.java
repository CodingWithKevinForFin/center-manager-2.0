package com.f1.povo.f1app;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.FCS")
public interface F1AppContainerScope extends F1AppEntity {

	long ROOT = -1;
	long NULL = -2;

	int TYPE_DISPATCHER = 1 << 1;
	int TYPE_SUITE = 1 << 2;
	int TYPE_SERVICES = 1 << 3;
	int TYPE_PROCESSOR = 1 << 4;
	int TYPE_CONTAINER = 1 << 5;
	int TYPE_TOOLS = 1 << 6;
	int TYPE_PARTITION_CONTROLLER = 1 << 7;
	int TYPE_PARTITION_GENERATOR = 1 << 8;
	int TYPE_PARTITION_RESOLVER = 1 << 9;
	int TYPE_PERSISTENCE_CONTROLLER = 1 << 10;
	int TYPE_STATE_GENERATOR = 1 << 11;
	int TYPE_SUITE_CONTROLLER = 1 << 12;
	int TYPE_THREADPOOL_CONTROLLER = 1 << 13;
	int TYPE_THREADSCOPE_CONTROLLER = 1 << 14;
	int TYPE_THROWABLE_HANDLER = 1 << 15;
	int TYPE_PORT = 1 << 16;
	int TYPE_PORT_INPUT = (1 << 17);
	int TYPE_PORT_OUTPUT = (1 << 18);
	int TYPE_PORT_REQUEST = (1 << 19);

	
	byte PID_CONTAINER_SCOPE_TYPE=1;
	byte PID_NAME=2;
	byte PID_PARENT_ID=3;
	byte PID_CONTAINER_SCOPE_ID=4;
	
	@PID(PID_CONTAINER_SCOPE_TYPE)
	public int getContainerScopeType();
	public void setContainerScopeType(int type);

	@PID(PID_NAME)
	public String getName();
	public void setName(String name);

	@PID(PID_PARENT_ID)
	public long getParentId();
	public void setParentId(long containerId);

	@PID(PID_CONTAINER_SCOPE_ID)
	public long getContainerScopeId();
	public void setContainerScopeId(long containerId);

}
