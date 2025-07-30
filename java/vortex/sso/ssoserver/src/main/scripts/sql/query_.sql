SELECT 
  id,
  expires,
  user_name as "userName",
  first_name as "firstName",
  last_name as "lastName",
  phone_number as "phoneNumber",
  password,
  email,
  company,
  reset_question as "resetQuestion",
  reset_answer as "resetAnswer",
  status,
  encoding_algorithm as "encodingAlgorithm",
  max_bad_attempts as "maxBadAttempts"
FROM 
  SsoUser where user_name=?{user_name} or email=?{email}
