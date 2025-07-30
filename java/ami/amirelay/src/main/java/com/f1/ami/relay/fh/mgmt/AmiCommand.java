package com.f1.ami.relay.fh.mgmt;

import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandRequest;
import com.f1.ami.relay.AmiRelayServer;
import com.f1.utils.PropertyController;

public interface AmiCommand {
	public void init(PropertyController props);
	public void exec(AmiRelayServer server, AmiCommandManager fhMgr, AmiRelayRunAmiCommandRequest action, StringBuilder msgSink) throws Exception;
	public String getId();
	public String getTitle();
	public String getHelp();
	public int getPermissionLevel();
	public String getWhereClause();
	public String getFilterClause();
	public String getArgs();
	public String getEnabledClause();
	public int getPriority();
	public String getStyle();
	public String getSelectMode();
	public String getFields();
	public int getCallbacksMask();
	public String getScript();
}
