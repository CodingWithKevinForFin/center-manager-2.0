package com.sso.messages;

import com.f1.base.PID;

public interface SendEmailToSsoUserRequest extends SsoRequest {

	byte PID_EMAIL = 5;
	byte PID_USER_NAME = 6;
	byte PID_USER_ID = 7;
	byte PID_BODY = 8;
	byte PID_SUBJECT = 9;
	byte PID_IS_HTML = 10;

	@PID(PID_EMAIL)
	public String getEmail();
	public void setEmail(String email);

	@PID(PID_USER_ID)
	public Long getUserId();
	public void setUserId(Long id);

	@PID(PID_USER_NAME)
	public String getUserName();
	public void setUserName(String id);

	@PID(PID_BODY)
	public String getBody();
	public void setBody(String body);

	@PID(PID_SUBJECT)
	public String getSubject();
	public void setSubject(String subject);

	@PID(PID_IS_HTML)
	public boolean getIsHtml();
	public void setIsHtml(boolean isHtml);
}
