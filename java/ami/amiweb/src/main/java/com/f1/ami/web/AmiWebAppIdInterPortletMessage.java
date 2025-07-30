package com.f1.ami.web;

import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.utils.structs.LongSet;

public class AmiWebAppIdInterPortletMessage implements InterPortletMessage {

	private LongSet appIds;

	public AmiWebAppIdInterPortletMessage(LongSet appIds) {
		this.appIds = appIds;
	}

	public LongSet getAppIds() {
		return appIds;
	}

}
