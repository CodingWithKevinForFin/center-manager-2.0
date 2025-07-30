package com.f1.ami.web;

import com.f1.ami.web.dm.AmiWebDmsImpl;

public class AmiWebDevTools {

	private AmiWebService service;

	public AmiWebDevTools(AmiWebService service) {
		this.service = service;
	}

	public void openDatamodelEditor(String string) {
		AmiWebDmsImpl dm = this.service.getDmManager().getDmByAliasDotName(string);
		if (dm != null)
			AmiWebUtils.showEditDmPortlet(this.service, dm, "Edit Datamodel");
	}

}
