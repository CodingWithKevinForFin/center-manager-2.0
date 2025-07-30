package com.f1.website;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("TF.WS.APL")
public interface TfWebsiteApplicant extends TfWebsiteObject {

	@PID(38)
	void setjobtitle(String jobtitle);
	String getjobtitle();

	@PID(39)
	void setfname(String fname);
	String getfname();

	@PID(40)
	void setlname(String lname);
	String getlname();

	@PID(41)
	void setemail(String email);
	String getemail();

	@PID(42)
	void setphone(String phone);
	String getphone();

	@PID(43)
	void setapp_resume(byte[] app_resume);
	byte[] getapp_resume();

	@PID(44)
	void setcover_letter(byte[] cover_letter);
	byte[] getcover_letter();

	@PID(45)
	void setpronoun(String pronoun);
	String getpronoun();

	@PID(46)
	void setpref_fname(String pref_fname);
	String getpref_fname();

	@PID(47)
	void sethear_forge(String hear_forge);
	String gethear_forge();

	@PID(48)
	void setforge_family(String forge_family);
	String getforge_family();

	@PID(49)
	void setoffice_location(String office_location);
	String getoffice_location();

	@PID(50)
	void setprevious_work(String previous_work);
	String getprevious_work();

	@PID(51)
	void setsponsorship(String sponsorship);
	String getsponsorship();

	@PID(52)
	void settimestamp(long createdOn);
	long gettimestamp();

}
