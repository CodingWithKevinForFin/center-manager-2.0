update NetLinkInstance set active=false where active and machine_instance_id=?{machine_instance_id};
update NetConnectionInstance set active=false where active and machine_instance_id=?{machine_instance_id};
update NetAddressInstance set active=false where active and machine_instance_id=?{machine_instance_id};
update ProcessInstance set active=false where active and machine_instance_id=?{machine_instance_id};
update FileSystemInstance set active=false where active and machine_instance_id=?{machine_instance_id};