package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.AMIAC")
public interface AmiRelayCommandDefMessage extends AmiRelayMessage {

	public byte CALLBACK_USER_LOGIN = 1;
	public byte CALLBACK_USER_LOGOUT = 2;
	public int CALLBACK_USER_CLICK = 4;
	public int CALLBACK_NOW = 8;

	@PID(3)
	public String getCommandId();
	public void setCommandId(String id);

	@PID(6)
	void setLevel(int level);
	int getLevel();

	@PID(19)
	void setWhereClause(String categoryType);
	String getWhereClause();

	@PID(15)
	void setHelp(String help);
	String getHelp();

	@PID(16)
	void setArgumentsJson(String Arguments);
	String getArgumentsJson();

	@PID(18)
	String getTitle();
	public void setTitle(String commandTitle);

	@PID(20)
	public void setPriority(int priority);
	public int getPriority();

	@PID(21)
	public void setEnabledExpression(String enabledExpression);
	public String getEnabledExpression();

	@PID(22)
	public void setStyle(String style);
	public String getStyle();

	@PID(23)
	public void setSelectMode(String selectMode);
	public String getSelectMode();

	@PID(24)
	public void setFields(String fields);
	public String getFields();

	@PID(25)
	void setFilterClause(String filterClause);
	String getFilterClause();

	@PID(26)
	void setCallbacksMask(int callback);
	int getCallbacksMask();

	@PID(27)
	void setAmiScript(String script);
	String getAmiScript();
}
