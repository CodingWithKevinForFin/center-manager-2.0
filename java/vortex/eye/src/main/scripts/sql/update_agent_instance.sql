UPDATE AgentInstance set active=false,disconnected_time=?{now},disconnected_reason='client_disconnected' where id=?{id} and active;
UPDATE AgentRequest set active=false,response_time=?{now},response_details='client_disconnected' where id=?{id} and active;
