package com.sso.messages;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.SS.QQ")
public interface QuerySsoUserRequest extends SsoRequest {

	public byte FIELD_USER_NAME = 1;
	public byte FIELD_ID = 2;
	public byte FIELD_EMAIL = 3;

	public byte STATUS_ALL = 0;
	public byte STATUS_ONLY_ENABLED = 1;
	public byte STATUS_ONLY_DISABLED = 2;

	
	byte PID_SEARCH_EXPRESSION=2;
	byte PID_IS_PATTERN=3;
	byte PID_SEARCH_FIELD=4;
	byte PID_STATUS_FILTER=5;
	byte PID_INCLUDE_ATTRIBUTES=6;
	
	@PID(PID_SEARCH_EXPRESSION)
	public String getSearchExpression();
	public void setSearchExpression(String name);

	@PID(PID_IS_PATTERN)
	public boolean getIsPattern();
	public void setIsPattern(boolean isPattern);

	@PID(PID_SEARCH_FIELD)
	public byte getSearchField();
	public void setSearchField(byte field);

	@PID(PID_STATUS_FILTER)
	public byte getStatusFilter();
	public void setStatusFilter(byte status);

	@PID(PID_INCLUDE_ATTRIBUTES)
	public boolean getIncludeAttributes();
	public void setIncludeAttributes(boolean attributes);

}
