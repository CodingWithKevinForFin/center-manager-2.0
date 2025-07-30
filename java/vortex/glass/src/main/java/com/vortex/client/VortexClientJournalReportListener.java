package com.vortex.client;

import com.f1.vortexcommon.msg.eye.VortexEyeJournalReport;

public interface VortexClientJournalReportListener {
	public void onJournalReport(VortexEyeJournalReport report);
}
