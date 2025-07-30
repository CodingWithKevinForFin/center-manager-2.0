SELECT 
    id,
    revision,
    now,
    name,
    metadata as '(STRINGMAP)metadata',
    properties
FROM  DeploymentSets where active
