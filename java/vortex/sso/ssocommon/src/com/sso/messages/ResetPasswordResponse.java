package com.sso.messages;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.SS.ER")
public interface ResetPasswordResponse extends SsoResponse {

	byte STATUS_USER_NOT_FOUND = 1;
	byte STATUS_NEED_ANSWER = 2;
	byte STATUS_ANSWER_WRONG = 3;
	byte STATUS_PASSWORD_RESET = 4;
	int STATUS_USER_DISABLED = 5;
	int STATUS_INTERNAL_ERROR = 6;

	
	byte PID_RESET_QUESTION=3;
	byte PID_STATUS=4;
	
	@PID(PID_RESET_QUESTION)
	public String getResetQuestion();
	public void setResetQuestion(String resetPassword);

	@PID(PID_STATUS)
	public int getStatus();
	public void setStatus(int status);

}
