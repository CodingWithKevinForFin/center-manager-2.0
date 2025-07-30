SELECT 
  a.id as 'id',
  a.revision as 'revision',
  a.name as 'name',
  a.mac as 'mac',
  a.broadcast as 'broadcast',
  a.transmission_details as 'transmissionDetails',
  a.mtu as 'mtu',
  a.state as 'state',
  a.now as 'now',
  a.machine_instance_id as 'machineInstanceId',
  b.rx_packets as 'rxPackets',
  b.rx_errors  as 'rxErrors',
  b.rx_dropped as 'rxDropped',
  b.rx_overrun as 'rxOverrun',
  b.rx_multicast as 'rxMulticast',
  b.tx_packets as 'txPackets',
  b.tx_errors  as 'txErrors',
  b.tx_dropped as 'txDropped',
  b.tx_carrier as 'txCarrier',
  b.tx_collsns as 'txCollsns'
FROM NetLinkInstance a JOIN NetLinkStats b ON a.id=b.net_link_instance_id
WHERE machine_instance_id in (?{ids}) LIMIT ?{lim}
