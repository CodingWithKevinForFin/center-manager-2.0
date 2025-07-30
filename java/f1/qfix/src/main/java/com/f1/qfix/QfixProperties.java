package com.f1.qfix;

public interface QfixProperties {

	/**
	 * the fix tag to be treated as the target broker (default is 115)
	 */
	String OPTION_FIX_TARGET_BROKER_TAG = "com.f1.fix.target.broker.tag";

	/**
	 * comma separated list of fix tags to retain on client reports
	 */
	String OPTION_FIX_CLIENT_REPORT_PASSTHRU = "com.f1.fix.ClientReportPassThru";

	/**
	 * name of the outbound fix session
	 */
	String OPTION_FIX_OUTBOUND_SESSION = "com.f1.fix.OutboundSession";
}
