SET GLOBAL max_allowed_packet=1024*1024*1000;

CREATE DATABASE testtrack;
alter database testtrack charset=latin1;

CREATE user 'testtrack_rw'@'%' identified by 'rw123';
grant select,insert,update,delete,usage ON testtrack.* to 'testtrack_rw'@'%';

CREATE user 'testtrack_r'@'localhost' identified by 'r123';
grant select,usage ON testtrack.* to 'testtrack_r'@'%';

USE testtrack;


CREATE TABLE MachineInstance
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  now                     BIGINT NOT NULL,
  hostname                VARCHAR(255),
  machine_uid             VARCHAR(32),
  machine_start_time      BIGINT,
  os_version              VARCHAR(255),
  os_name                 VARCHAR(255),
  os_architecture         VARCHAR(255),
  metadata                VARCHAR(2048),
  cpu_count               TINYINT UNSIGNED
);

CREATE INDEX MachineInstance_id on MachineInstance(id);
CREATE INDEX MachineInstance_now on MachineInstance(now);
CREATE INDEX MachineInstance_active on MachineInstance(active);

CREATE TABLE MachineInstanceStats
(
  active                  BOOLEAN NOT NULL,
  machine_instance_id     BIGINT NOT NULL,
  now                     BIGINT NOT NULL,
  system_load_avg         DECIMAL(8,4),
  total_memory            BIGINT,
  used_memory             BIGINT,
  total_swap_memory       BIGINT,
  used_swap_memory        BIGINT
);

CREATE INDEX MachineInstanceStats_miid on MachineInstanceStats(machine_instance_id);
CREATE INDEX MachineInstanceStats_now on MachineInstanceStats(now);
CREATE INDEX MachineInstanceStats_active on MachineInstanceStats(active);


CREATE TABLE NetConnectionInstance
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  now                     BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  machine_instance_id     BIGINT NOT NULL,
  local_host              VARCHAR(128),
  local_port              SMALLINT UNSIGNED,
  foreign_host            VARCHAR(128),
  foreign_port            SMALLINT UNSIGNED,
  local_appname           VARCHAR(128),
  local_pid               VARCHAR(32),
  state                   TINYINT UNSIGNED 
);

CREATE INDEX NetConnectionInstance_id on NetConnectionInstance(id);
CREATE INDEX NetConnectionInstance_active on NetConnectionInstance(active);

CREATE TABLE FileSystemInstance
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  now                     BIGINT NOT NULL,
  fs_type                 VARCHAR(64),
  machine_instance_id     BIGINT,
  name                    VARCHAR(64)
);

CREATE INDEX FileSystemInstance_id on FileSystemInstance(id);
CREATE INDEX FileSystemInstance_miid on FileSystemInstance(machine_instance_id);
CREATE INDEX FileSystemInstance_now on FileSystemInstance(now);
CREATE INDEX FileSystemInstance_active on FileSystemInstance(active);

CREATE TABLE FileSystemStats
(
  active                  BOOLEAN NOT NULL,
  file_system_instance_id BIGINT NOT NULL,
  now                     BIGINT NOT NULL,
  total_space             BIGINT,
  usable_space            BIGINT,
  free_space              BIGINT
);

CREATE INDEX FileSystemStats_fsiid on FileSystemStats(file_system_instance_id);
CREATE INDEX FileSystemStats_now on FileSystemStats(now);
CREATE INDEX FileSystemStats_active on FileSystemStats(active);


CREATE TABLE FileInstance
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  now                     BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  machine_instance_id     BIGINT NOT NULL,
  parent_id               BIGINT,
  mask                    TINYINT ,
  modified_time           BIGINT,
  path                    VARCHAR(8000),
  checksum                BIGINT,
  size                    INT UNSIGNED,
  data_id                 BIGINT,
  name                    VARCHAR(8000)
);

CREATE INDEX FileInstance_id on FileInstance(id);
CREATE INDEX FileInstance_now on FileInstance(now);
CREATE INDEX FileInstance_active on FileInstance(active);
CREATE INDEX FileInstance_miid on FileInstance(machine_instance_id);
CREATE INDEX FileInstance_checksum on FileInstance(checksum);

CREATE TABLE BackupFileInstance
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  now                     BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  machine_instance_id     BIGINT NOT NULL,
  backup_id               BIGINT,
  mask                    SMALLINT,
  modified_time           BIGINT,
  path                    VARCHAR(8000),
  checksum                BIGINT,
  size                    INT UNSIGNED,
  data_vvid               BIGINT
);

CREATE INDEX BackupFileInstance_active on BackupFileInstance(active);
CREATE INDEX BackupFileInstance_id on BackupFileInstance(id);

CREATE TABLE FileData
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  checksum                BIGINT,
  size                    INT UNSIGNED,
  is_text                 BOOLEAN,
  data                    LONGBLOB
);

CREATE INDEX FileData_id on FileData(id);
CREATE INDEX FileData_active on FileData(active);
CREATE INDEX FileData_checksum on FileData(checksum);


CREATE TABLE AgentInstance
(
  active                  BOOLEAN NOT NULL,
  machine_instance_id     BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  id                      BIGINT NOT NULL,
  now                     BIGINT NOT NULL,
  start_time              BIGINT,
  agent_version           VARCHAR(64),
  connected_time          BIGINT,
  process_uid             VARCHAR(255),
  disconnected_time       BIGINT,
  remote_host             VARCHAR(255),
  remote_port             SMALLINT UNSIGNED,
  disconnected_reason     VARCHAR(64),
  f1_license_expires      VARCHAR(64)
);


CREATE INDEX AgentInstance_miid on AgentInstance(machine_instance_id);
CREATE INDEX AgentInstance_id on AgentInstance(id);
CREATE INDEX AgentInstance_active on AgentInstance(active);

CREATE TABLE AgentRequest
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  agent_id                BIGINT NOT NULL,
  request_type            TINYINT UNSIGNED,
  request_time            BIGINT,
  remote_request_time     BIGINT,
  remote_response_time    BIGINT,
  response_time           BIGINT,
  request_details         VARCHAR(1000),
  response_details        VARCHAR(1000)
);

CREATE INDEX AgentRequest_active ON AgentRequest(active);
CREATE INDEX AgentRequest_id ON AgentRequest(id);
CREATE INDEX AgentRequest_aid ON AgentRequest(agent_id);

CREATE TABLE ProcessInstance
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  now                     BIGINT NOT NULL,
  machine_instance_id     BIGINT NOT NULL,
  start_time              BIGINT,
  end_time                BIGINT,
  user_name               VARCHAR(64),
  pid                     VARCHAR(32),
  parent_pid              VARCHAR(32),
  command                 VARCHAR(10000)
);

CREATE INDEX ProcessInstance_id on ProcessInstance(id);
CREATE INDEX ProcessInstance_active on ProcessInstance(active);


CREATE TABLE ProcessStats
(
  active                  BOOLEAN NOT NULL,
  process_id              BIGINT NOT NULL,
  now                     BIGINT NOT NULL,
  memory                  BIGINT,
  cpu_percent             DECIMAL(8,4)
);

CREATE INDEX ProcessStats_pidactive on ProcessStats(process_id,active);

CREATE TABLE NetLinkInstance
(
  active                  BOOLEAN NOT NULL,
  now                     BIGINT NOT NULL,
  id                      BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  machine_instance_id     BIGINT NOT NULL,
  mac                     VARCHAR(255),
  broadcast               VARCHAR(255),
  transmission_details    VARCHAR(1024),
  mtu                     INT UNSIGNED,
  name                    VARCHAR(255),
  state                   SMALLINT 
);

CREATE INDEX NetLinkInstance_now on NetLinkInstance(now);
CREATE INDEX NetLinkInstance_miid on NetLinkInstance(machine_instance_id);
CREATE INDEX NetLinkInstance_active on NetLinkInstance(active);

CREATE TABLE NetLinkStats
(
  active                  BOOLEAN NOT NULL,
  net_link_instance_id    BIGINT NOT NULL,
  now                     BIGINT NOT NULL,
  rx_packets              BIGINT,
  rx_errors               BIGINT,
  rx_dropped              BIGINT,
  rx_overrun              BIGINT,
  rx_multicast            BIGINT,
  tx_packets              BIGINT,
  tx_errors               BIGINT,
  tx_dropped              BIGINT,
  tx_carrier              BIGINT,
  tx_collsns              BIGINT
);

create index NetLinkStats_nlidactive on NetLinkStats(net_link_instance_id,active);


CREATE TABLE NetAddressInstance
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  now                     BIGINT NOT NULL,
  machine_instance_id     BIGINT NOT NULL,
  link_name               VARCHAR(255),
  address_type            TINYINT UNSIGNED,
  scope                   TINYINT UNSIGNED,
  address                 VARCHAR(255),
  broadcast               VARCHAR(255)
);

CREATE INDEX NetAddressInstance_id on NetAddressInstance(id);
CREATE INDEX NetAddressInstance_active on NetAddressInstance(active);

CREATE TABLE DbDatabase
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  now                     BIGINT NOT NULL,
  name                    VARCHAR(256),
  db_server_id            BIGINT
);

CREATE INDEX DbDatabase_active on DbDatabase(active);
CREATE INDEX DbDatabase_id on DbDatabase(id);
CREATE INDEX DbDatabase_now on DbDatabase(now);
CREATE INDEX DbDatabase_name on DbDatabase(name);
CREATE INDEX DbDatabase_sid on DbDatabase(db_server_id);

CREATE TABLE DbServer(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  now                     BIGINT NOT NULL,
  machine_uid             VARCHAR(32),
  description             VARCHAR(2048),
  db_type                 TINYINT UNSIGNED,
  url                     VARCHAR(256),
  psw                     VARCHAR(256),
  server_port             SMALLINT UNSIGNED,
  metadata                VARCHAR(2048),
  hints                   VARCHAR(2048)
);

CREATE INDEX DbServer_active on DbServer(active);
CREATE INDEX DbServer_id on DbServer(id);

CREATE TABLE DbServerStatus
(
  active                  BOOLEAN NOT NULL,
  now                     BIGINT NOT NULL,
  dbserver_id             BIGINT NOT NULL,
  status                  TINYINT,
  inspected_time          BIGINT,
  message                 VARCHAR(2048)
);

CREATE INDEX DbServerStatus_active on DbServerStatus(active);

CREATE TABLE DbTable
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  now                     BIGINT NOT NULL,
  db_database_id          BIGINT NOT NULL,
  name                    VARCHAR(256),
  description             VARCHAR(2048),
  comments                VARCHAR(2048),
  create_time             BIGINT
);

CREATE INDEX DbTable_active on DbTable(active);
CREATE INDEX DbTable_id on DbTable(id);
CREATE INDEX DbTable_now on DbTable(now);
CREATE INDEX DbTable_name on DbTable(name);
CREATE INDEX DbTable_ddid on DbTable(db_database_id);

CREATE TABLE DbColumn
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  now                     BIGINT NOT NULL,
  db_table_id             BIGINT NOT NULL,
  name                    VARCHAR(256),
  description             VARCHAR(2048),
  comments                VARCHAR(2048),
  mask                    TINYINT,
  size                    BIGINT,
  numeric_precision       TINYINT,
  numeric_scale           TINYINT,
  permissible_values      VARCHAR(10000),
  position                TINYINT,
  data_type               TINYINT
);

CREATE INDEX DbColumn_active on DbColumn(active);
CREATE INDEX DbColumn_id on DbColumn(id);
CREATE INDEX DbColumn_now on DbColumn(now);
CREATE INDEX DbColumn_name on DbColumn(name);
CREATE INDEX DbColumn_dtid on DbColumn(db_table_id);

CREATE TABLE DbObject
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  now                     BIGINT NOT NULL,
  db_database_id          BIGINT NOT NULL,
  name                    VARCHAR(256),
  definition              VARCHAR(20000),
  object_type             TINYINT
);
CREATE INDEX DbObject_active on DbObject(active);
CREATE INDEX DbObject_id on DbObject(id);
CREATE INDEX DbObject_now on DbObject(now);
CREATE INDEX DbObject_name on DbObject(name);
CREATE INDEX DbObject_ddid on DbObject(db_database_id);

CREATE TABLE DbPrivilege
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  now                     BIGINT NOT NULL,
  db_database_id          BIGINT NOT NULL,
  table_name              VARCHAR(255),
  user_name               VARCHAR(256),
  privilege_type          INT
);
CREATE INDEX DbPrivilege_active on DbPrivilege(active);
CREATE INDEX DbPrivilege_id on DbPrivilege(id);
CREATE INDEX DbPrivilege_now on DbPrivilege(now);
CREATE INDEX DbPrivilege_ddid on DbPrivilege(db_database_id);

CREATE TABLE TailFile
(
  id                      BIGINT NOT NULL,
  machine_instance_id     BIGINT NOT NULL,
  name                    VARCHAR(8000)
);
CREATE INDEX TailFile_id on TailFile(id);
CREATE INDEX TailFile_miid on TailFile(machine_instance_id);



CREATE TABLE TailFileEvent
(
   id                     BIGINT NOT NULL,
   event_type             TINYINT,
   file_position          BIGINT,
   tail_file_id           BIGINT,
   checksum               INT,
   data                   BLOB,
   now                    BIGINT NOT NULL
);
CREATE INDEX TailFileEvent_checksum on TailFileEvent(checksum);
CREATE INDEX TailFileEvent_tfid on TailFileEvent(tail_file_id);
CREATE INDEX TailFileEvent_now on TailFileEvent(now);


CREATE TABLE Id_Fountains
(
  namespace               VARCHAR(64),
  next_id                 BIGINT
);

CREATE TABLE AgentMachineEventStats
(
	id                    BIGINT NOT NULL,
	machine_instance_id   BIGINT NOT NULL,
	now                   BIGINT NOT NULL,
	level                 TINYINT,
	start_time            BIGINT,
	end_time              BIGINT,
	duration              VARCHAR(64),
	not_ended             VARCHAR(256),
	source                VARCHAR(256),
	host                  VARCHAR(256),
	name                  VARCHAR(256),
	user_name             VARCHAR(256),
	message               VARCHAR(2048)
);	

CREATE INDEX AgentMachineEventStats_mid on AgentMachineEventStats(machine_instance_id);

CREATE TABLE JobSchedules
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  now                     BIGINT NOT NULL,
  user_name               VARCHAR(128),
  machine_instance_id     BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  second                  VARCHAR(64),
  minute                  VARCHAR(64),
  hour                    VARCHAR(64),
  day_of_month            VARCHAR(64),
  month                   VARCHAR(64),
  day_of_week             VARCHAR(64),
  timezone     	          VARCHAR(64),
  command                 VARCHAR(256)
);

CREATE INDEX JobSchedule_id on JobSchedules(id);
CREATE INDEX JobSchedule_miid on JobSchedules(machine_instance_id);

CREATE TABLE AgentEvents
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  agent_id                BIGINT NOT NULL,
  machine_instance_id     BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  severity                TINYINT,
  subtype                 INT,
  now                     BIGINT NOT NULL,
  status                  INT,
  message                 VARCHAR(2048)
);

CREATE TABLE AuditTrailRules
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  now                     BIGINT NOT NULL,
  name                    VARCHAR(256),
  revision                INT UNSIGNED NOT NULL,
  rule_type               SMALLINT,
  rules                   VARCHAR(10000)
);

CREATE INDEX AuditTrailRules_active on AuditTrailRules(active);


CREATE TABLE Expectations
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  now                     BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  name                    VARCHAR(128),
  metadata                VARCHAR(2048),
  machine_uid             VARCHAR(32),
  target_type             TINYINT,
  field_masks             VARCHAR(8000),
  tolerances              VARCHAR(8000),
  target_metadata         VARCHAR(8000)
) DEFAULT CHARSET=latin1;
CREATE INDEX Expectations_active on Expectations(active);

CREATE TABLE BuildProcedures
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  now                     BIGINT NOT NULL,
  metadata                VARCHAR(2048),
  name                    VARCHAR(128),
  build_machine_uid       VARCHAR(32),
  template_user           VARCHAR(64),
  template_command        VARCHAR(4096),
  template_stdin          VARCHAR(4096),
  template_result_file    VARCHAR(2048),
  template_result_verify_file VARCHAR(2048),
  template_result_name    VARCHAR(256),
  template_result_version VARCHAR(256)
);

CREATE INDEX BuildProcedures_active on BuildProcedures(active);

CREATE TABLE BuildResults
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  now                     BIGINT NOT NULL,
  name                    VARCHAR(128),
  metadata                VARCHAR(2048),
  version                 VARCHAR(128),
  file                    VARCHAR(2048),
  verify_file             VARCHAR(2048),
  procedure_id            BIGINT NOT NULL,
  procedure_revision      SMALLINT UNSIGNED,
  procedure_name          VARCHAR(256),
  build_variables         VARCHAR(4096),
  build_machine_uid       VARCHAR(32),
  build_user              VARCHAR(64),
  build_command           VARCHAR(4096),
  build_stdin             VARCHAR(4096),
  build_stdout            VARCHAR(1024),
  build_stderr            VARCHAR(1024),
  build_exitcode          INT,
  invoked_by              VARCHAR(128),
  data                    LONGBLOB,
  data_checksum           BIGINT,
  verify_data             LONGBLOB,
  verify_data_checksum    BIGINT,
  start_time              BIGINT,
  state                   TINYINT,
  
  build_stdout_vvid       BIGINT,
  build_stderr_vvid       BIGINT,
  verify_data_vvid        BIGINT,
  data_vvid               BIGINT,
  data_length             INT,
  verify_data_length      INT,
  build_stdout_length     INT,
  build_stderr_length     INT
);

CREATE INDEX BuildResults_active on BuildResults(active);
CREATE INDEX BuildResults_id on BuildResults(id);

CREATE TABLE DeploymentSets
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  now                     BIGINT NOT NULL,
  name                    VARCHAR(128),
  metadata                VARCHAR(2048),
  properties              TEXT
);

CREATE INDEX DeploymentSets_active on DeploymentSets(active);

CREATE TABLE Deployments
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  now                     BIGINT NOT NULL,
  metadata                VARCHAR(2048),
  deployment_set_id       BIGINT NOT NULL,
  procedure_id            BIGINT,
  target_machine_uid      VARCHAR(32),
  target_directory        VARCHAR(2048),
  generated_properties_file VARCHAR(2048),
  start_script_file       VARCHAR(2048),
  stop_script_file        VARCHAR(2048),
  install_script_file     VARCHAR(2048),
  uninstall_script_file   VARCHAR(2048),
  verify_script_file      VARCHAR(2048),
  options                 INT,
  rollback_directory      VARCHAR(2048),
  rollback_count          TINYINT,
  properties              TEXT,
  generated_files         TEXT,
  env_vars                TEXT,
  scripts_directory       VARCHAR(2048),
  description             VARCHAR(2048),
  target_user             VARCHAR(64),
  auto_delete_files       VARCHAR(8096),
  log_directories         VARCHAR(8096)
)DEFAULT CHARSET=latin1;

CREATE INDEX Deployments_active on Deployments(active);

CREATE TABLE DeploymentStatuses
(
  active                  BOOLEAN NOT NULL,
  now                     BIGINT NOT NULL,
  deployment_id           BIGINT NOT NULL,
  status                  INT UNSIGNED,
  build_result_id         BIGINT,
  build_invoked_by        VARCHAR(64),
  running_pid             BIGINT,
  running_process_uid     VARCHAR(32),
  deployed_instance_id    BIGINT,
  message                 VARCHAR(2048)
);

CREATE INDEX DeploymentStatuses_depid_active on DeploymentStatuses(deployment_id,active);

CREATE TABLE BackupDestinations
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  now                     BIGINT NOT NULL,
  metadata                VARCHAR(2048),
  name                    VARCHAR(64),
  destination_path        VARCHAR(2048),
  destination_machine_uid VARCHAR(32)
);
CREATE INDEX BackupDestinations_active on BackupDestinations(active);

CREATE TABLE Backups
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  now                     BIGINT NOT NULL,
  metadata                VARCHAR(2048),
  deployment_id           BIGINT,
  description             VARCHAR(2048),
  source_machine_uid      VARCHAR(32),
  source_path             VARCHAR(2048),
  ignore_expression       VARCHAR(4096),
  options                 INT,
  backup_destination_id   BIGINT NOT NULL
);
CREATE INDEX Backups_active on Backups(active);

CREATE TABLE BackupStatuses
(
  active                  BOOLEAN NOT NULL,
  backup_id               BIGINT NOT NULL,
  invoked_by              VARCHAR(64),
  file_count              INT,
  ignored_file_count      INT,
  bytes_count             BIGINT,
  latest_modified_time    BIGINT,
  manifest_vvid           BIGINT,
  manifest_time           BIGINT,
  manifest_length         INT,
  now                     BIGINT NOT NULL,
  status                  TINYINT,
  message                 VARCHAR(2048)
);
CREATE INDEX BackupStatuses_active on BackupStatuses(active);

CREATE TABLE ScheduledTasks
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  now                     BIGINT NOT NULL,
  metadata                VARCHAR(2048),
  description             VARCHAR(2048),
  deployment_id           BIGINT,
  options                 INT,
  target_id               BIGINT,
  task_type               TINYINT,
  timezone                VARCHAR(64),
  hours                   INT UNSIGNED,
  minutes                 BIGINT UNSIGNED,
  seconds                 BIGINT UNSIGNED,
  weekdays                TINYINT UNSIGNED,
  month_in_years          SMALLINT UNSIGNED,
  week_in_months          TINYINT UNSIGNED,
  week_in_years           BIGINT UNSIGNED,
  day_in_months           INT UNSIGNED,
  day_of_week_in_months   TINYINT UNSIGNED,
  day_of_years            BIGINT  UNSIGNED,
  state                   TINYINT,
  command                 VARCHAR(2048),
  comments                VARCHAR(8192)
);
CREATE INDEX ScheduledTasks_active on ScheduledTasks(active);
  
CREATE TABLE ScheduledTaskStatuses
(
  active                  BOOLEAN NOT NULL,
  scheduled_task_id       BIGINT NOT NULL,
  invoked_by              VARCHAR(64),
  now                     BIGINT NOT NULL,
  status                  TINYINT,
  message                 VARCHAR(2048),
  next_runtime            BIGINT,
  last_runtime            BIGINT,
  run_count               BIGINT
);
CREATE INDEX ScheduledTaskStatuses_active on ScheduledTaskStatuses(active);

CREATE TABLE MetadataFields
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  now                     BIGINT NOT NULL,
  description             VARCHAR(2048),
  target_types            BIGINT,
  value_type              TINYINT,
  required                BOOLEAN,
  key_code                VARCHAR(8),
  title                   VARCHAR(64),
  max_length              TINYINT,
  enums                   VARCHAR(2048),
  max_value               BIGINT,
  min_value               BIGINT
);
CREATE INDEX MetadataFields_active on MetadataFields(active);



CREATE TABLE ClientEvents
(
  id                      BIGINT NOT NULL,
  now                     BIGINT NOT NULL,
  event_type              TINYINT NOT NULL,
  invoked_by              VARCHAR(64),
  target_machine_uid      VARCHAR(32),
  comment                 VARCHAR(2048),
  message                 VARCHAR(2048),
  params                  VARCHAR(10000)
);

CREATE INDEX ClientEvents_now on ClientEvents(now);

CREATE TABLE VortexVault
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  now                     BIGINT NOT NULL,
  checksum                BIGINT,
  softlink_vvid           BIGINT,
  data                    LONGBLOB,
  data_length             INT
);

CREATE INDEX VortexVault_id on VortexVault(id);
CREATE INDEX VortexVault_active on VortexVault(active);
CREATE INDEX VortexVault_composite on VortexVault(active,id,now,checksum,softlink_vvid,data_length);


CREATE TABLE CloudInterfaces
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  now                     BIGINT NOT NULL,
  description             VARCHAR(2048),
  user_name               VARCHAR(128),
  password                VARBINARY(8192),
  key_contents            VARBINARY(8192),
  key_type                TINYINT UNSIGNED,
  parameters              VARCHAR(2048),
  cloud_vendor_type       SMALLINT UNSIGNED
);
CREATE INDEX CloudInterface_active on CloudInterfaces(active);

CREATE TABLE AmiApplication
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  now                     BIGINT NOT NULL,
  machine_instance_id     BIGINT NOT NULL,
  connections_count       TINYINT UNSIGNED NOT NULL,
  host_ip                 VARCHAR(1024),
  app_id                  VARCHAR(128)
);
CREATE INDEX AmiApplication_id on AmiApplication(id);
CREATE INDEX AmiApplication_active on AmiApplication(active);

CREATE TABLE AmiApplicationParams(
  active                  BOOLEAN NOT NULL,
  ami_application_id      BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  now                     BIGINT NOT NULL,
  params_delta            VARCHAR(10000),
  params                  VARCHAR(10000)
);
CREATE INDEX AmiApplication_Params_idactive on AmiApplicationParams(ami_application_id,active);

CREATE TABLE AmiAlert
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  now                     BIGINT NOT NULL,
  ami_application_id      BIGINT NOT NULL,
  machine_instance_id     BIGINT NOT NULL,
  level                   INT UNSIGNED NOT NULL,
  alert_type              VARCHAR(128),
  alert_id                VARCHAR(128),
  expires                 BIGINT NOT NULL,
  assigned_to             VARCHAR(128)
);
CREATE INDEX AmiAlert_id on AmiAlert(id);
CREATE INDEX AmiAlert_active on AmiAlert(active);

CREATE TABLE AmiAlertParams(
  active                  BOOLEAN NOT NULL,
  ami_alert_id            BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  now                     BIGINT NOT NULL,
  params_delta            VARCHAR(10000),
  params                  VARCHAR(10000)
);
CREATE INDEX AmiAlert_Params_idactive on AmiAlertParams(ami_alert_id,active);

CREATE TABLE AmiObject
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  now                     BIGINT NOT NULL,
  ami_application_id      BIGINT NOT NULL,
  machine_instance_id     BIGINT NOT NULL,
  associated_alert_id     BIGINT,
  object_type             VARCHAR(128),
  object_id               VARCHAR(128),
  expires                 BIGINT NOT NULL
);
CREATE INDEX AmiObject_id on AmiObject(id);
CREATE INDEX AmiObject_active on AmiObject(active);

CREATE TABLE AmiObjectParams(
  active                  BOOLEAN NOT NULL,
  ami_object_id           BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  now                     BIGINT NOT NULL,
  params_delta            VARCHAR(10000),
  params                  VARCHAR(10000)
);
CREATE INDEX AmiObject_Params_idactive on AmiObjectParams(ami_object_id,active);

ALTER TABLE AgentEvents ENGINE=MYISAM;
ALTER TABLE AgentInstance ENGINE=MYISAM;
ALTER TABLE AgentMachineEventStats ENGINE=MYISAM;
ALTER TABLE AgentRequest ENGINE=MYISAM;
ALTER TABLE AmiAlert ENGINE=MYISAM;
ALTER TABLE AmiAlertParams ENGINE=MYISAM;
ALTER TABLE AmiApplication ENGINE=MYISAM;
ALTER TABLE AmiApplicationParams ENGINE=MYISAM;
ALTER TABLE AmiObject ENGINE=MYISAM;
ALTER TABLE AmiObjectParams ENGINE=MYISAM;
ALTER TABLE AuditTrailRules ENGINE=MYISAM;
ALTER TABLE BackupDestinations ENGINE=MYISAM;
ALTER TABLE BackupFileInstance ENGINE=MYISAM;
ALTER TABLE BackupStatuses ENGINE=MYISAM;
ALTER TABLE Backups ENGINE=MYISAM;
ALTER TABLE BuildProcedures ENGINE=MYISAM;
ALTER TABLE BuildResults ENGINE=MYISAM;
ALTER TABLE ClientEvents ENGINE=MYISAM;
ALTER TABLE CloudInterfaces ENGINE=MYISAM;
ALTER TABLE DbColumn ENGINE=MYISAM;
ALTER TABLE DbDatabase ENGINE=MYISAM;
ALTER TABLE DbObject ENGINE=MYISAM;
ALTER TABLE DbPrivilege ENGINE=MYISAM;
ALTER TABLE DbServer ENGINE=MYISAM;
ALTER TABLE DbServerStatus ENGINE=MYISAM;
ALTER TABLE DbTable ENGINE=MYISAM;
ALTER TABLE DeploymentSets ENGINE=MYISAM;
ALTER TABLE DeploymentStatuses ENGINE=MYISAM;
ALTER TABLE Deployments ENGINE=MYISAM;
ALTER TABLE Expectations ENGINE=MYISAM;
ALTER TABLE FileData ENGINE=MYISAM;
ALTER TABLE FileInstance ENGINE=MYISAM;
ALTER TABLE FileSystemInstance ENGINE=MYISAM;
ALTER TABLE FileSystemStats ENGINE=MYISAM;
ALTER TABLE Id_Fountains ENGINE=MYISAM;
ALTER TABLE JobSchedules ENGINE=MYISAM;
ALTER TABLE MachineInstance ENGINE=MYISAM;
ALTER TABLE MachineInstanceStats ENGINE=MYISAM;
ALTER TABLE MetadataFields ENGINE=MYISAM;
ALTER TABLE NetAddressInstance ENGINE=MYISAM;
ALTER TABLE NetConnectionInstance ENGINE=MYISAM;
ALTER TABLE NetLinkInstance ENGINE=MYISAM;
ALTER TABLE NetLinkStats ENGINE=MYISAM;
ALTER TABLE ProcessInstance ENGINE=MYISAM;
ALTER TABLE ProcessStats ENGINE=MYISAM;
ALTER TABLE ScheduledTaskStatuses ENGINE=MYISAM;
ALTER TABLE ScheduledTasks ENGINE=MYISAM;
ALTER TABLE TailFile ENGINE=MYISAM; 
ALTER TABLE TailFileEvent ENGINE=MYISAM;
ALTER TABLE VortexVault ENGINE=MYISAM;



COMMIT;


