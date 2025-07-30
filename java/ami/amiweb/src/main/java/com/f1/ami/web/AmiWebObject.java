package com.f1.ami.web;

import java.util.Map;
import java.util.Set;

import com.f1.base.CalcFrame;
import com.f1.base.ToStringable;

public interface AmiWebObject extends ToStringable, CalcFrame, Map<String, Object> {

	public Object getParam(String param);

	//globally unique: combination of center id and getId()
	public long getUniqueId();

	public String getObjectId();

	//unique within the center
	public long getId();

	public String getTypeName();

	public void fill(Map<String, Object> sink);
	public void fill(CalcFrame sink);

	//NOT SUPPORTED
	public Set<Map.Entry<String, Object>> entrySet();

	//NOT SUPPORTED
	public Set<String> keySet();
}
