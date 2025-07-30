# Tools

The Center has several tools to aid developers as they build tables, indexes, triggers, timers, procedures, and methods. These tools are:

- `SHOW`: Returns information on a group of objects e.g. `SHOW TABLES`
- `DESCRIBE`: Returns the definition of a given object
- `DIAGNOSE`: Returns the memory usage of a given table
- `DISABLE` and `ENABLE`: Tools for turning timers & triggers on/off
- `SETLOCAL`: Sets local variables for a given session, e.g: returned table limits

## SHOW

### Overview

Provides a table listing all records and relevant Metadata of a particular type with optional filtering and sorting. Note, for tables, only PUBLIC tables are listed.

```amiscript
SHOW object_type [WHERE where_expr ] [ORDER BY expr [ASC|DESC] [, expr [ASC|DESC] ...]]
```

`object_type` can be any of `TABLES`, `COLUMNS`, `COMMANDS`, `DATASOURCES`, `DATASOURCE_TYPES`, `PROCEDURES`, `PROPERTIES`, `RESOURCES`, `TRIGGERS`, `TIMERS`, `CONNECTIONS`, `RELAYS`, `PLUGINS`, `SESSIONS`, `VARS`, `PROCESSES`, `METHODS`

Expressions contained in square brackets are supplied as optional arguments. 

#### Example

This example will return all tables whose name contains "data" and will order the results by the number of columns in the table. 

```amiscript
SHOW TABLES WHERE TableName =~ "data" ORDER BY ColumnsCount
```

### SHOW TABLE

Provides a table listing all columns and relevant metadata of a particular table with optional filtering and sorting:

```amiscript
SHOW TABLE table_expr  [WHERE where_expr ] [ORDER BY expr [ASC|DESC] [, expr [ASC|DESC]  ...]]
```

#### Example

This example will return all columns for the `myorders` table, alphabetized by name. 

```amiscript
SHOW TABLE myorders order by ColumnName
```

### Center Replication

You can also show tables from other connected centers (via center replication). To see what centers you have connected, in AMIDB run: `SHOW CENTERS`. Then:

```amiscript
SHOW TABLE some_table FROM connected_center
```

## DESCRIBE

### Overview

Returns a table with a column titled "SQL" containing the AMI SQL statements necessary to reconstruct the supplied table, trigger, timer, procedure, or index.

```amiscript
DESCRIBE TABLE table_name
DESCRIBE TRIGGER trigger_name
DESCRIBE TIMER timer_name
DESCRIBE PROCEDURE procedure_name
DESCRIBE INDEX index_name ON table_name
```

### Example

This example will return the create statement that can be used to create the `__COLUMN` table and its pk index. 

```amiscript
DESCRIBE TABLE __COLUMN;
```

### Center Replication

You can also describe tables and other objects from other connected centers (via center replication). To see what centers you have connected, in AMIDB run: `SHOW CENTERS`. Then:

```amiscript
DESCRIBE TABLE some_table FROM connected_center
```

This will return the AMI SQL query to create that table in the current center. 

## DIAGNOSE

### Overview

Returns a table providing details on the approximate memory footprint of tables, columns and indexes.

``` amiscript
DIAGNOSE TABLE table_name [,table_name ...]
DIAGNOSE COLUMN column_name on table_name [,column_name on table_name ...]
DIAGNOSE INDEX index_name ON table_name [,index_name on table_name ...]
```

#### Example

This example will return the memory used by the `__COLUMN` table

``` amiscript
DIAGNOSE TABLE __COLUMN;
```

## DISABLE and ENABLE

### Overview

Used to disable/enable triggers and timers by name. Disabled triggers and timers will **not** get executed when they otherwise would. 

By default, when triggers and timers are created, they are enabled. Check the enabled/disabled status using `#!amiscript SHOW TRIGGERS` and `#!amiscript SHOW TIMERS`.

```amiscript
DISABLE TRIGGER trigger_name
ENABLE TRIGGER trigger_name

DISABLE TIMER trigger_name
ENABLE TIMER trigger_name
```
#### Example

This example will disable the trigger named `my_trigger` and enable the timer `my_timer`:

```amiscript
DISABLE TRIGGER my_trigger;
ENABLE TIMER my_timer;
```

## SETLOCAL

### Overview

Controls local variables for the command line session. These local variables dictate behavior of the command line interface. Run `SETLOCAL` to see the list of variables that can be changed. Note, this command cannot be run from the AMIDB Shell Tool.

```amiscript
SETLOCAL varname = value
```

### Variables

| Variables                                                                                       | Description                                                                                                                                                                                                                                                                                                                                                         |
|-------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `limit`             | The default limit to apply to queries (similar to the limit field in the datamodel), `-1` means no default limit                                                                                                                                                                                                                                                      |
| `max_print_chars`   | Total number of characters to print to the console for a query result. This prevents from very large query results overwhelming terminal                                                                                                                                                                                                                            |
| `multiline`         | If set to `"off"` then pressing enter will automatically execute the user entered. If set to `"on"` then pressing enter will progress to a new line for additional input.  Pressing enter twice in succession will execute the users entered text.                                                                                                                         |
| `show_plan`         | If set to `"off"` the engine will not show the query plan, this is more efficient as some overhead is necessary to gather and display this importation. If set to `"on"` then the engine will produce verbose output on the steps and time taking for each step in the query. This can be useful for optimizing your queries.                                                                                                                   |
| `timeout`           | Amount of time in milliseconds that the console will hang waiting for a response. It must be a positive number                                                                                                                                                                                                                                                      |
| `unprintable_chars` | Determines how to print unprintable ASCII characters, for example 0x01 (SOH). If set to `"marker"` then an upside down question mark (¿) is printed in place of any unprintable chars (default). If set to `"show"` the raw, unprintable, is sent to stdout. If set to `"hide"` then nothing is printed. If set to `"show_code"` then an upside down question mark (¿) is printed followed by the chars 4 digit hexcode is printed. For example, the SOH would be printed as:  ¿0001                                                                                                             |
| `datetime_format`   | The format to use when printing UTC and UTCN columns. If set to empty string, the Raw long value (unix epoch) is printed. The default is `"YYYY-MM-DD HH:mm:ss.SSS.z"`                                                                                                                                                                                              |
| `timezone`          | Works in conjunction with datetime_format, to determine local time for display. For example *EST5EDT* would be used for New York. Default is *UTC*                                                                                                                                                                                                                  |
| `string_template`   | Should commands interpret string templates (default is off). If set to `"on"` then `${...}` will be evaluated and replaced with the contents. If set to `"off"` then `${...}` will be treated as a literal.                                                                                                                                                                                                                            |
| `password_encrypt`  | If set to `"on"` then *login* command should be supplied with an encrypted password. The password will be decrypted using the key stored on the AMI server.  Note, use `strEncrypt(...)` method to get the encrypted version of a plain text password. Ex: `select strEncrypt("demopass");`. If set to `"off"` then login command should be supplied with plain text password.                                                                                                                                                                                                       |

**Example**

```amiscript
setlocal timeout=30000
```

