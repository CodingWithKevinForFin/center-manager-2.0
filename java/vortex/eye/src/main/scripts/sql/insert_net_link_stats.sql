UPDATE NetLinkStats SET active=false where active=true AND net_link_instance_id=?{id};
INSERT INTO NetLinkStats
(
  active,
  net_link_instance_id,
  now,
  rx_packets,
  rx_errors,
  rx_dropped,
  rx_overrun,
  rx_multicast,
  tx_packets,
  tx_errors,
  tx_dropped,
  tx_carrier,
  tx_collsns
) VALUES (
  true,
  ?{id},
  ?{now},
  ?{rx_packets},
  ?{rx_errors},
  ?{rx_dropped},
  ?{rx_overrun},
  ?{rx_multicast},
  ?{tx_packets},
  ?{tx_errors},
  ?{tx_dropped},
  ?{tx_carrier},
  ?{tx_collsns}
);