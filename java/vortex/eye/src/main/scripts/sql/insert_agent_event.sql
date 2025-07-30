UPDATE AgentEvents set active=false where id=?{id} and active;
INSERT INTO AgentEvents
(
    active,               
	id,                    
	agent_id,             
	machine_instance_id,   
	revision,              
	severity,              
	subtype,               
	now,            
	status,                
	message 
) VALUES (
    ?{active},               
	?{id},                    
	?{agent_id},             
	?{machine_instance_id},   
	?{revision},              
	?{severity},              
	?{subtype},               
	?{now},            
	?{status},                
	?{message} 
);          