package com.f1.ami.web;

import java.util.Map;

import com.f1.base.Action;

//Allow sending of messages across services (aka user session)
public interface AmiWebServiceAction extends Action {

	String ACTION_RELOAD_LAYOUT = "RELOAD_LAYOUT";

	public void setAction(String action);
	public String getAction();

	public void setArguments(Map action);
	public Map getArguments();
}
