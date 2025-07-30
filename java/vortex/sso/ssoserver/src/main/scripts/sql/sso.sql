CREATE DATABASE sso;


CREATE user 'sso_rw'@'%' identified by 'rw123';
grant select,insert,update,delete,usage ON sso.* to 'sso_rw'@'%';

CREATE user 'sso_rw'@'hammer.3forge.net' identified by 'rw123';
grant select,insert,update,delete,usage ON sso.* to 'sso_rw'@'hammer.3forge.net';

CREATE user 'sso_r'@'localhost' identified by 'r123';
grant select,usage ON testtrack.* to 'sso_r'@'%';

USE sso;

CREATE TABLE SsoUser
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  group_id                BIGINT NOT NULL, 
  now                     BIGINT NOT NULL,
  expires                 BIGINT NOT NULL,
  revision                SMALLINT UNSIGNED,
  user_name               VARCHAR(64),
  first_name              VARCHAR(64),
  last_name               VARCHAR(64),
  phone_number            VARCHAR(20),
  password                VARCHAR(64),
  email                   VARCHAR(256),
  company                 VARCHAR(128),
  reset_question          VARCHAR(128),
  reset_answer            VARCHAR(64),
  max_bad_attempts        TINYINT,
  status                  TINYINT NOT NULL,
  encoding_algorithm      TINYINT NOT NULL
);

create index SsoUser_id on SsoUser(id);
create index SsoUser_email on SsoUser(email);

CREATE TABLE SsoUserEvent
(
  ssouser_id              BIGINT NOT NULL,
  now                     BIGINT NOT NULL,
  supplied_email          VARCHAR(256),
  supplied_user_name      VARCHAR(256),
  name_space              VARCHAR(64),
  event_type              TINYINT
);

create index SsoUserEvent_suid on SsoUserEvent(ssouser_id);

--CREATE TABLE SsoUserAttribute
--(
--  
--active                  BOOLEAN,
--id                      BIGINT,
--now                     BIGINT,
--revision                SMALLINT UNSIGNED,
--ssouser_id              BIGINT,
--name_space              VARCHAR(64),
--attribute_key           VARCHAR(64),
--attribute_value         VARCHAR(10000),
--attribute_type          TINYINT
--);

--create index SsoUserAttribute_id on SsoUserAttribute(id);
--create index SsoUserAttribute_suid on SsoUserAttribute(ssouser_id);

CREATE TABLE SsoGroupAttribute
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  now                     BIGINT NOT NULL,
  revision                SMALLINT UNSIGNED NOT NULL,
  ssogroup_id             BIGINT NOT NULL,
  attribute_key           VARCHAR(64) NOT NULL,
  attribute_value         LONGTEXT,
  attribute_type          TINYINT NOT NULL
);

create index SsoGroupAttribute_id on SsoGroupAttribute(id);
create index SsoGroupAttribute_suid on SsoGroupAttribute(ssouser_id);

CREATE TABLE SsoGroup
(
  active                  BOOLEAN NOT NULL,
  id                      BIGINT NOT NULL,
  now                     BIGINT NOT NULL,
  group_type              SMALLINT UNSIGNED,
  revision                SMALLINT UNSIGNED NOT NULL,
  name                    VARCHAR(64)
);
 
 
 create index SsoGroup_id on SsoGroup(id);
 create index SsoGroup_name on SsoGroup(name);
 
 CREATE TABLE SsoGroupMembers
 (
   active                  BOOLEAN NOT NULL,
   id                      BIGINT NOT NULL,
   now                     BIGINT NOT NULL,
   group_id                BIGINT NOT NULL,
   member_id               BIGINT NOT NULL,
   revision                SMALLINT UNSIGNED NOT NULL
 );
 
 create index SsoGroupMembers_id on SsoUser(id);
 create index SsoGroupMembers_member_id on SsoGroupMembers(member_id);
 create index SsoGroupMembers_group_id on SsoGroupMembers(group_id);

 CREATE TABLE SsoUpdateEvents
 (
   id                      BIGINT NOT NULL,
   member_id               BIGINT NOT NULL,
   now                     BIGINT NOT NULL,
   event_type              SMALLINT NOT NULL,
   message                 VARCHAR(256),
   ok                      BOOLEAN NOT NULL,
   session                 VARCHAR(64),
   name                    VARCHAR(64),
   namespace               VARCHAR(256),
   client_location         VARCHAR(512)
 );
 
 --CREATE TABLE Product
 --(
-- active                  BOOLEAN,
-- id                      BIGINT,
-- now                     BIGINT,
-- revision                SMALLINT UNSIGNED,
-- name                    VARCHAR(64)
-- );
-- 
-- 
-- create index Product_id on Product(id);
-- create index Product_name on Product(name);
-- 
-- CREATE TABLE Entitlement
-- (
-- active                  BOOLEAN,
-- id                      BIGINT,
-- revision                SMALLINT UNSIGNED,
-- ssouser_id              VARCHAR(64),
-- product_id              VARCHAR(64),
-- priority                SMALLINT,
-- Allow                   BOOLEAN,
-- Action                  VARCHAR(64),
-- Parameters              VARCHAR(1024)
-- );
-- 
-- create index Entitlement_id on SsoUser(id);
-- create index Entitlement_ssouser_id on Entitlement(ssouser_id);
-- create index Entitlement_product_id on Entitlement(product_id);

CREATE TABLE Id_Fountains
(
  namespace               VARCHAR(64),
  next_id                 BIGINT
);

COMMIT;
