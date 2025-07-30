SELECT 
    a.id,
    a.revision,
    a.now as now,
    a.deployment_set_id as deploymentSetId,
    a.procedure_id as procedureId,
    a.target_machine_uid as targetMachineUid,
    a.target_user as targetUser,
    a.target_directory as targetDirectory,
    a.generated_properties_file as generatedPropertiesFile,
    a.start_Script_file as startScriptFile,
    a.stop_script_file as stopScriptFile,
    a.properties as properties,
    a.scripts_directory as scriptsDirectory,
    a.description as description
FROM  Deployments a WHERE a.id in (?{ids}) LIMIT ?{lim}
