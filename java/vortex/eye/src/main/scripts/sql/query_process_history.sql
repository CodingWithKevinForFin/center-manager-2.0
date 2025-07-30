SELECT 
  ProcessStats.process_id as 'id',
  ProcessStats.now as 'now',
  ProcessStats.memory as 'memory',
  ProcessStats.cpu_percent as 'cpuPercent'
FROM ProcessStats WHERE process_id in (?{ids}) LIMIT ?{lim}
