UPDATE AgentInstance set active=false,revision=?{REVISION_DONE},disconnected_time=?{now},disconnected_reason='server_shutdown';
