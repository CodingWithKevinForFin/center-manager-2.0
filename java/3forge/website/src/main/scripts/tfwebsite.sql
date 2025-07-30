SET GLOBAL max_allowed_packet=1024*1024*1000;

CREATE DATABASE tfwebsite;
alter database tfwebsite charset=latin1;

CREATE user 'tfwebsite_rw'@'%' identified by 'rw123';
grant select,insert,update,delete,usage ON tfwebsite.* to 'tfwebsite_rw'@'%';

CREATE user 'tfwebsite_r'@'localhost' identified by 'r123';
grant select,usage ON tfwebsite.* to 'tfwebsite_r'@'%';

USE tfwebsite;

CREATE TABLE User(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  revision                INT UNSIGNED NOT NULL,
  created_on              BIGINT NOT NULL,
  mondified_on            BIGINT NOT NULL,
  enabled                 BOOLEAN,
  status                  TINYINT,
  license_expires_date    INT,
  license_days_length     INT,
  trial_expires_on        BIGINT,
  audit_id                BIGINT,
  username                VARCHAR(255),
  audit                   VARCHAR(16),
  first_name              VARCHAR(64),
  last_name               VARCHAR(64),
  company                 VARCHAR(64),
  phone                   VARCHAR(20),
  email                   VARCHAR(255),
  password                VARCHAR(255),
  verify_guid             VARCHAR(255),
  forgot_guid             VARCHAR(255),
  role                    VARCHAR(255),
  intended_use            VARCHAR(500),
  license_apps            VARCHAR(1024),
  license_instances       VARCHAR(1024)
);
Create INDEX username  on User(username);
Create INDEX vguid  on User(verify_guid(8));
Create INDEX fguid  on User(forgot_guid(8));

CREATE TABLE Audit(
  id                      BIGINT NOT NULL,
  created_on              BIGINT NOT NULL,
  user_id                 BIGINT,
  username                VARCHAR(255),
  session_id              VARCHAR(32),
  remote_addr             VARCHAR(255),
  audit                   VARCHAR(32),
  description             VARCHAR(255)
);

CREATE TABLE Id_Fountains
(
  namespace               VARCHAR(64),
  next_id                 BIGINT
);

CREATE TABLE Press_Release
(
	id                    BIGINT NOT NULL,
	created_on            BIGINT NOT NULL,
	email                 VARCHAR(255),
	first_name            VARCHAR(255),
	last_name             VARCHAR(255),
	company               VARCHAR(255),
	phone                 VARCHAR(255),
	contact_phone         BOOLEAN,
	contact_email         BOOLEAN
);

CREATE TABLE Applicants {
    id BIGINT not null auto_increment,
    jobtitle VARCHAR(64),
    fname VARCHAR(64),
    lname VARCHAR(64),
    email VARCHAR(255),
    phone VARCHAR(20),
    app_resume MEDIUMBLOB,
    cover_letter MEDIUMBLOB,
    pronoun VARCHAR(64),
    pref_fname VARCHAR(64),
    hear_forge VARCHAR(255),
    forge_family VARCHAR(64),
    office_location VARCHAR(255), 
    previous_work VARCHAR(64),
    sponsorship VARCHAR(64),
    timestamp BIGINT(20)
};