# Timeouts

## Timeout expired while waiting for read lock

### Overview

This issue typically happens when there is a read request while AMIDB is processing a write request. AmiDB is designed to allow multiple reads but only single write at any given time. So while the write request is being processed, the incoming read request will be queued and when the timeout value is reached, it outputs the timeout exception.

### Recommendation

The most common cause is a slow running Timer or Trigger. A short term solution is to increase the timeout value so the read threads can wait longer. However, the long term solution would be to carefully inspect and optimize the code that is doing the write so it is performant enough to avoid the timeout issue in the first place. There are a few approaches we can use to identify the problematic Timer/Trigger:

1.  Use AMIDB diagnostic queries ([diagnose tableName](../center/tools.md#diagnose), [show timers](../center/tools.md#realtime-tools), show triggers) to find Timers/Triggers with higher average or max run time

1.  Review the AmiOne.log by hand to see what Timer/Trigger was running when the timeout expired

1.  Use the [log viewer](./logs.md#log-viewer-layout) dashboard to see the runtime for each Timer/Trigger. After setting up the dashboard and uploading the AmiOne.log file, navigate to the Timers and Triggers tabs and look for Timers/Triggers with significantly higher Total Run Time or significantly higher Run Time when the timeout expired

Once you have identified the slow running Timer or Trigger review the code to see what could be causing the slowness. Some common causes of inefficient Timers/Triggers include:

-   Calling large Update/Insert queries

-   Calling lots of Select/Insert/Update/Delete queries without an index on the columns in the WHERE clause

-   Calling expensive stored procedures