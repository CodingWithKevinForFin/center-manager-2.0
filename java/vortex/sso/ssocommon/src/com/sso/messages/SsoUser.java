package com.sso.messages;

import com.f1.base.Lockable;
import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.SS.US")
public interface SsoUser extends PartialMessage, Lockable {

	byte ENCODING_PLAIN = 1;
	byte ENCODING_CHECKSUM64 = 2;

	byte STATUS_ENABLED = 0;
	byte STATUS_DISABLED = 1;
	byte STATUS_LOCKED = 2;

	byte PID_ID = 1;
	byte PID_REVISION = 2;
	byte PID_NOW = 3;
	byte PID_EXPIRES = 5;
	byte PID_USER_NAME = 6;
	byte PID_FIRST_NAME = 7;
	byte PID_LAST_NAME = 8;
	byte PID_PHONE_NUMBER = 9;
	byte PID_PASSWORD = 10;
	byte PID_EMAIL = 11;
	byte PID_COMPANY = 12;
	byte PID_RESET_QUESTION = 13;
	byte PID_RESET_ANSWER = 14;
	byte PID_STATUS = 15;
	byte PID_ENCODING_ALGORITHM = 16;
	byte PID_MAX_BAD_ATTEMPTS = 17;
	byte PID_GROUP_ID = 18;

	@PID(PID_ID)
	public long getId();
	public void setId(long id);

	@PID(PID_REVISION)
	public int getRevision();
	public void setRevision(int revision);

	@PID(PID_NOW)
	public long getNow();
	public void setNow(long now);

	@PID(PID_EXPIRES)
	public long getExpires();
	public void setExpires(long expires);

	@PID(PID_USER_NAME)
	public String getUserName();
	public void setUserName(String userName);

	@PID(PID_FIRST_NAME)
	public String getFirstName();
	public void setFirstName(String firstName);

	@PID(PID_LAST_NAME)
	public String getLastName();
	public void setLastName(String lastName);

	@PID(PID_PHONE_NUMBER)
	public String getPhoneNumber();
	public void setPhoneNumber(String phoneNumber);

	@PID(PID_PASSWORD)
	public String getPassword();
	public void setPassword(String password);

	@PID(PID_EMAIL)
	public String getEmail();
	public void setEmail(String email);

	@PID(PID_COMPANY)
	public String getCompany();
	public void setCompany(String company);

	@PID(PID_RESET_QUESTION)
	public String getResetQuestion();
	public void setResetQuestion(String requestQuestion);

	@PID(PID_RESET_ANSWER)
	public String getResetAnswer();
	public void setResetAnswer(String resetAnswer);

	@PID(PID_STATUS)
	public byte getStatus();
	public void setStatus(byte enabled);

	@PID(PID_ENCODING_ALGORITHM)
	public byte getEncodingAlgorithm();
	public void setEncodingAlgorithm(byte encodingAlgorithm);

	@PID(PID_MAX_BAD_ATTEMPTS)
	public int getMaxBadAttempts();
	public void setMaxBadAttempts(int maxBadAttempts);

	@PID(PID_GROUP_ID)
	public long getGroupId();
	public void setGroupId(long groupId);

	public SsoUser clone();

}
