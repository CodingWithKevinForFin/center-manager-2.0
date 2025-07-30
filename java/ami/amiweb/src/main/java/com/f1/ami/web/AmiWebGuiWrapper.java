package com.f1.ami.web;


public class AmiWebGuiWrapper {
	public AmiWebGuiWrapper(AmiWebObject gui) {
		this.gui = gui;

	}

	AmiWebObject gui;
	AmiWebObject obj;

	public String getType() {
		return (String) gui.getParam("Type");
	}
	public String getName() {
		return (String) gui.getParam("Name");
	}
	public int getX() {
		return (Integer) gui.getParam("X");
	}
	public int getY() {
		return (Integer) gui.getParam("Y");
	}

	public AmiWebObject getGui() {
		return this.gui;
	}
	protected void bindToSystemObject(AmiWebObject entity) {
		this.obj = entity;
	}
	public AmiWebObject getObject() {
		return this.obj;
	}

}
