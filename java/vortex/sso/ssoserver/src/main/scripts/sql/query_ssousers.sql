SELECT 
  id,
  expires,
  now,
  user_name as "userName",
  first_name as "firstName",
  last_name as "lastName",
  phone_number as "phoneNumber",
  password,
  email,
  company,
  max_bad_attempts as "maxBadAttempts",
  reset_question as "resetQuestion",
  reset_answer as "resetAnswer",
  status,
  group_id as "groupId",
  revision as "revision",
  encoding_algorithm as "encodingAlgorithm"
FROM 
  SsoUser where active 
