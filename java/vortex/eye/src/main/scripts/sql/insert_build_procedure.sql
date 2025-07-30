UPDATE BuildProcedures set active=false where active=true and id=?{id};
INSERT INTO BuildProcedures
(
  active,
  id,
  revision,
  now,
  name,
  build_machine_uid,
  template_user,
  template_command,
  template_stdin,
  template_result_name,
  template_result_verify_file,
  template_result_file,
  template_result_version,
  metadata
) VALUES (
  ?{active},
  ?{id},
  ?{revision},
  ?{now},
  ?{name},
  ?{build_machine_uid},
  ?{template_user},
  ?{template_command},
  ?{template_stdin},
  ?{template_result_name},
  ?{template_result_verify_file},
  ?{template_result_file},
  ?{template_result_version},
  ?{metadata}
);