UPDATE ParentExecutions set is_latest='N' where exec_id=?{exec_id} and is_latest='Y' AND source_system=?{source_system};
UPDATE ParentOrders set total_exec_qty=?{total_exec_qty}, total_exec_value=?{total_exec_value}, order_status=?{order_status} where order_id=?{order_id} and is_latest='Y' and revision=?{order_revision} AND source_system=?{source_system};
INSERT INTO ParentExecutions
(
  source_system,
  exec_group_id,
  exec_id,
  order_id,
  order_revision,
  revision,
  is_latest,
  exec_ref_id,
  external_exec_id,
  exec_time,
  exec_status,
  last_mkt,
  exec_broker,
  contra_broker,
  exec_qty,
  exec_px,
  pass_thru_tags
) values (
  ?{source_system},
  ?{exec_group_id},
  ?{exec_id},
  ?{order_id},
  ?{order_revision},
  ?{revision},
  'Y',
  ?{exec_ref_id},
  ?{external_exec_id},
  ?{exec_time},
  ?{exec_status},
  ?{last_mkt},
  ?{exec_broker},
  ?{contra_broker},
  ?{exec_qty},
  ?{exec_px},
  ?{pass_thru_tags}
)
