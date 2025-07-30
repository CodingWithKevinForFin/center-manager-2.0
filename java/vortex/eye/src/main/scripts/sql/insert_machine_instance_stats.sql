UPDATE MachineInstanceStats SET active=false where active=true AND machine_instance_id=?{machine_instance_id};
INSERT INTO MachineInstanceStats
(
  active,
  machine_instance_id,
  now,
  system_load_avg,
  total_memory,
  used_memory,
  total_swap_memory,
  used_swap_memory
) values (
  true,
  ?{machine_instance_id},
  ?{now},
  ?{system_load_avg},
  ?{total_memory},
  ?{used_memory},
  ?{total_swap_memory},
  ?{used_swap_memory}
);

