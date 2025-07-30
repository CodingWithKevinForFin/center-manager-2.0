package com.vortex.web.messages;

import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.utils.structs.LongSet;

public class VortexBackupIdInterPortletMessage implements InterPortletMessage {

	private LongSet backupIds;

	public VortexBackupIdInterPortletMessage(LongSet backupIds) {
		this.backupIds = backupIds;
	}

	public LongSet getBackupIds() {
		return backupIds;
	}

}
