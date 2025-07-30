package com.f1.website;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("TF.WS.USR")
public interface TfWebsiteUser extends TfWebsiteObject {

	@PID(20)
	void setEnabled(boolean enabled);
	boolean getEnabled();

	@PID(21)
	void setUsername(String username);
	String getUsername();

	@PID(22)
	void setStatus(byte status);
	byte getStatus();

	@PID(23)
	void setFirstName(String firstName);
	String getFirstName();

	@PID(24)
	void setLastName(String lastName);
	String getLastName();

	@PID(25)
	void setCompany(String company);
	String getCompany();

	@PID(26)
	void setPhone(String phone);
	String getPhone();

	@PID(27)
	void setEmail(String email);
	String getEmail();

	@PID(28)
	void setLicenseApps(String licenseApps);
	String getLicenseApps();

	@PID(29)
	void setlicenseInstances(String licenseInstances);
	String getlicenseInstances();

	@PID(30)
	void setTrialExpiresOn(long trialExpiresOn);
	long getTrialExpiresOn();

	@PID(31)
	void setLicenseExpiresDate(int date);
	int getLicenseExpiresDate();

	@PID(32)
	void setLicenseDaysLength(int date);
	int getLicenseDaysLength();

	@PID(33)
	void setPassword(String password);
	String getPassword();

	@PID(34)
	void setVerifyGuid(String verifyGuid);
	String getVerifyGuid();

	@PID(35)
	void setRole(String role);
	String getRole();

	@PID(36)
	void setIntendedUse(String intendedUse);
	String getIntendedUse();

	@PID(37)
	void setForgotGuid(String forgot);
	String getForgotGuid();

}
