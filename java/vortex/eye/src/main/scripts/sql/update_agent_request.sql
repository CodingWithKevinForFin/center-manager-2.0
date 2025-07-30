UPDATE AgentRequest SET 
  active=false,
  response_time=?{response_time},
  remote_request_time=?{remote_request_time},
  remote_response_time=?{remote_response_time},
  response_details=?{response_details}
WHERE
  id=?{id}