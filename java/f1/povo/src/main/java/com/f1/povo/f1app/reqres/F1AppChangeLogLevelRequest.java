package com.f1.povo.f1app.reqres;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.CLLQ")
public interface F1AppChangeLogLevelRequest extends F1AppRequest {

	byte PID_LOGGER_IDS = 10;
	@PID(PID_LOGGER_IDS)
	public List<String> getLoggerIds();
	public void setLoggerIds(List<String> id);

	byte PID_SINK_IDS = 11;
	@PID(PID_SINK_IDS)
	public List<String> getSinkIds();
	public void setSinkIds(List<String> ids);

	byte PID_LEVEL = 12;
	@PID(PID_LEVEL)
	public int getLevel();
	public void setLevel(int id);

}
