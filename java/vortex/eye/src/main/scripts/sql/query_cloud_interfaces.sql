select
  id,
  now,
  description,
  user_name as 'userName',
  password,
  key_contents as 'keyContents',
  key_type as 'keyType',
  cloud_vendor_type as 'cloudVendorType',
  parameters as '(STRINGMAP)parameters'
FROM CloudInterfaces where active