CREATE DATABASE f1oms;

USE f1oms;

CREATE TABLE ParentOrders
(
  source_system         VARCHAR(128),
  order_group_id        VARCHAR(128),
  order_id              VARCHAR(128),
  request_id            VARCHAR(128),
  orig_request_id       VARCHAR(128),
  revision              INT,
  is_latest             CHAR(1),
  
  security_id           VARCHAR(128),
  id_type               VARCHAR(128),
  symbol                VARCHAR(128),
  account               VARCHAR(128),
  order_qty             DECIMAL(18,8),
  total_exec_qty        DECIMAL(18,8),
  total_exec_value      DECIMAL(18,8),
  limit_px              DECIMAL(18,8),
  side                  CHAR(1),
  order_type            CHAR(1),
  time_in_force         CHAR(1),
  exec_instructions     VARCHAR(10),
  destination           VARCHAR(128),
  created_time          DATETIME,
  updated_time          DATETIME,
  order_status          INT,
  session_name          VARCHAR(128),
  text                  VARCHAR(20000),
  pass_thru_tags        VARCHAR(20000),

  strategy              VARCHAR(128),
  start_time            VARCHAR(64),
  end_time              VARCHAR(64)
);

CREATE INDEX ParentOrders_order_id ON ParentOrders(order_id);
CREATE INDEX ParentOrders_order_group_id ON ParentOrders(order_group_id);

CREATE TABLE ChildOrders
(
  source_system         VARCHAR(128),
  order_group_id        VARCHAR(128),
  order_id              VARCHAR(128),
  external_order_id     VARCHAR(128),
  request_id            VARCHAR(128),
  revision              INT,
  is_latest             CHAR(1),
  
  security_id           VARCHAR(128),
  id_type               VARCHAR(128),
  symbol                VARCHAR(128),
  account               VARCHAR(128),
  orig_request_id       VARCHAR(128),
  order_qty             DECIMAL(18,8),
  total_exec_qty        DECIMAL(18,8),
  total_exec_value      DECIMAL(18,8),
  limit_px              DECIMAL(18,8),
  side                  CHAR(1),
  order_type            CHAR(1),
  time_in_force         CHAR(1),
  exec_instructions     VARCHAR(10),
  destination           VARCHAR(128),
  created_time          DATETIME,
  updated_time          DATETIME,
  order_status          INT,
  session_name          VARCHAR(128),
  text                  VARCHAR(20000),
  pass_thru_tags        VARCHAR(20000)
);

CREATE INDEX ChildOrders_order_id ON ChildOrders(order_id);
CREATE INDEX ChildOrders_order_group_id ON ChildOrders(order_group_id);

CREATE TABLE ParentExecutions
(
  source_system         VARCHAR(128),
  exec_group_id         VARCHAR(128),
  exec_id               VARCHAR(128),
  order_id              VARCHAR(128),
  order_revision        INT,
  revision              INT,
  is_latest             CHAR(1),

  exec_ref_id           VARCHAR(128),
  external_exec_id      VARCHAR(128),
  exec_time             DATETIME,
  exec_status           CHAR(1),
  last_mkt              VARCHAR(10),
  exec_broker           VARCHAR(10),
  contra_broker         VARCHAR(10),
  exec_qty              DECIMAL(18,8),
  exec_px               DECIMAL(18,8),
  pass_thru_tags        VARCHAR(20000)
);

CREATE INDEX ParentExecutions_exec_id ON ParentExecutions(exec_id);
CREATE INDEX ParentExecutions_exec_group_id ON ParentExecutions(exec_group_id);
CREATE INDEX ParentExecutions_order_id ON ParentExecutions(order_id);

CREATE TABLE ChildExecutions
(
  source_system         VARCHAR(128),
  exec_group_id         VARCHAR(128),
  exec_id               VARCHAR(128),
  order_id              VARCHAR(128),
  order_revision        INT,
  revision              INT,
  is_latest             CHAR(1),

  exec_ref_id           VARCHAR(128),
  external_exec_id      VARCHAR(128),
  exec_time             DATETIME,
  exec_status           CHAR(1),
  last_mkt              VARCHAR(10),
  exec_broker           VARCHAR(10),
  contra_broker         VARCHAR(10),
  exec_qty              DECIMAL(18,8),
  exec_px                DECIMAL(18,8),
  pass_thru_tags        VARCHAR(20000)
);

CREATE INDEX ChildExecutions_exec_id ON ChildExecutions(exec_id);
CREATE INDEX ChildExecutions_exec_group_id ON ChildExecutions(exec_group_id);
CREATE INDEX ChildExecutions_order_id ON ChildExecutions(order_id);

CREATE TABLE Id_Fountains
(
  namespace VARCHAR(64),
  next_id BIGINT
);




CREATE user 'f1oms_rw'@'localhost' identified by 'rw123';
grant select,insert,update,delete ON f1oms.* to 'f1oms_rw'@'%';

CREATE user 'f1oms_r'@'localhost' identified by 'r123';
grant select ON f1oms.* to 'f1oms_r'@'%';

COMMIT;
