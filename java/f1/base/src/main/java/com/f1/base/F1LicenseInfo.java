package com.f1.base;

/**
 * License information, as it pertains to f1 library
 * 
 */
public class F1LicenseInfo {

	private static StringBuilder licenseText = null;
	public static String getLicenseApp() {
		return "UNKNOWN_LicenseApp                                                                                                                                                                                                                                                               "
				.trim();
	}
	public static String getLicenseInstance() {
		return "UNKNOWN_LicenseInstance                                                                                                                                                                                                                                                               "
				.trim();
	}
	public static String getLicenseHost() {
		return "UNKNOWN_LicenseHost                                                                                                                                                                                                                                                               "
				.trim();
	}
	public static String getLicenseStartDate() {
		return "UNKNOWN_LicenseStartDate                                                                                                                                                                                                                                                               "
				.trim();
	}
	public static String getLicenseEndDate() {
		return "UNKNOWN_LicenseEndDate                                                                                                                                                                                                                                                               "
				.trim();
	}
	public static String getLicenseText() {
		return licenseText == null ? "" : licenseText.toString();
	}
}
