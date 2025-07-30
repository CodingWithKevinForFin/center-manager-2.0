package com.f1.ami.web;

public interface AmiWebGuiServiceAdapterPeer {

	//JavaAdapter -> AmiScript
	public Object executeAmiScriptCallback(String amiscriptMethodName, Object[] args);

	//JavaAdapter -> javascript
	public void executeJavascriptCallback(String javascriptMethodName, Object[] args);

	public AmiWebService getService();
}
