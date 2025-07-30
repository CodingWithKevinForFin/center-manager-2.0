package com.f1.ami.web;

import java.util.Set;

import com.f1.base.IterableAndSize;
import com.f1.base.Mapping;

public interface AmiWebRealtimeObjectManager {

	public boolean removeAmiListener(AmiWebRealtimeObjectListener listener);
	public boolean addAmiListener(AmiWebRealtimeObjectListener listener);
	public boolean hasAmiListeners();
	//	public List<AmiWebRealtimeObjectListener> getListeners();
	public IterableAndSize<AmiWebObject> getAmiObjects();

	public com.f1.base.CalcTypes getRealtimeObjectschema();
	public com.f1.base.CalcTypes getRealtimeObjectsOutputSchema();

	public String getRealtimeId();//for everything but feeds, this matches the ARI

	public Set<String> getUpperRealtimeIds();//outputs
	public Set<String> getLowerRealtimeIds();//inputs

}
