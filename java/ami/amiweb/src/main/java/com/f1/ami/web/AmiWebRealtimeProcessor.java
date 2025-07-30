package com.f1.ami.web;

import java.util.Map;

public interface AmiWebRealtimeProcessor extends AmiWebRealtimeObjectManager, AmiWebRealtimeObjectListener, AmiWebDomObject {

	Map<String, Object> getConfiguration();
	void init(String alias, Map<String, Object> configuration);

	public String getName();
	public String getAdn();
	public String getAlias();
	public String getType();

	public void setAdn(String adn);
	void rebuild();
	void close();

	//Object managers that use the output of this processor(outgoing data)
	//	public Set<String> getUpperRealtimeObjectManagers();

}
