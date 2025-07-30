SELECT 
    id,
    revision,
    now,
    name,
    build_machine_uid as buildMachineUid,
    template_user as templateUser,
    template_command as templateCommand,
    template_stdin as templateStdin,
    template_result_file as templateResultFile,
    template_result_verify_file as templateResultVerifyFile,
    template_result_name as templateResultName,
    metadata as '(STRINGMAP)metadata',
    template_result_version as templateResultVersion
FROM  BuildProcedures where active
