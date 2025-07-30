# Shell & SSH

## Overview

The AMI Shell and SSH Command Datasource Adapters are highly configurable adapters designed to execute shell commands and capture the stdout, stderr and exitcode. There are a number of directives which can be used to control how the command is executed, including setting environment variables and supplying data to be passed to stdin. The adapter processes the output from the command. Each line (delineated by a Line feed) is considered independently for parsing. Note the `#!amiscript EXECUTE <sql>` clause supports the full AMI sql language.

Please note, that running the command will produce 3 tables:

-   Stdout - Contains the contents of standard out
-   Stderr - Contains the contents from standard err
-   exitCode - Contains the executed code of the process

(You can limit which tables are returned using the `_capture` directive)

Generally speaking, the parser can handle **four** (**4**) different methods of parsing:

### Delimited list or ordered fields

Example data and query:  

```
11232|1000|123.20
12412|8900|430.90
```

``` amiscript
CREATE TABLE mytable AS USE _cmd="my_cmd" _delim="|"
_fields="String account, Integer qty, Double px"
EXECUTE SELECT * FROM cmd
```

### Key value pairs

Example data and query:

```
account=11232|quantity=1000|price=123.20
account=12412|quantity=8900|price=430.90
```

``` amiscript
CREATE TABLE mytable AS USE _cmd="my_cmd" _delim="|" _equals="="
_fields="String account, Integer qty, Double px"
EXECUTE SELECT * FROM cmd
```

### Pattern Capture

Example data and query:

```
Account 11232 has 1000 shares at $123.20 px
Account 12412 has 8900 shares at $430.90 px
```

``` amiscript
CREATE TABLE mytable AS USE _cmd="my_cmd"
_fields="String account, Integer qty, Double px"
_pattern="account,qty,px=Account (.*) has (.*) shares at \\$(.*) px"
EXECUTE SELECT * FROM cmd
```

### Raw Line

If you do not specify a `_fields, `_mapping nor `_pattern directive then the parser defaults to a simple row-per-line parser. A "line" column is generated containing the entire contents of each line from the command's output

``` amiscript
CREATE TABLE mytable AS USE _cmd="my_cmd" EXECUTE SELECT * FROM cmd
```

## Configuring the Adapter for first use

=== "Shell"

	1. Open the **datamodeler** (In Developer Mode -\> Menu Bar -\> Dashboard -\> Datamodeler)
	
	1. Choose the "**Add Datasource**" button
	
	1. Choose **Shell Command**
	
	1. In the Add Datasource dialog:
	
		**Name**: Supply a user defined Name, ex: `MyShell`
		
		**URL**: Full path to working directory, ex: `/home/myuser/files`, keep in mind that the path is on the machine running AMI, not necessarily your local desktop
	
	1. Click "Add Datasource" Button

=== "SSH"

	1. Open the **datamodeler** (In Developer Mode -\> Menu Bar -\> Dashboard -\> Datamodeler)
	
	1. Choose the "**Add Datasource**" button
	
	1. Choose **SSH Command**
	
	1. In the Add Datasource dialog:
	
		**Name**: Supply a user defined Name, ex: `MyShell`
		
		**URL**: *hostname* or *hostname:port*

		**Username**: the name of the ssh user to login as
		
		**Password**: the password of the ssh user to login as
		
		**Options**: See below, note when using multiple options they should be comma delimited

		- `authMode=keyboardInteractive` for servers requiring keyboard interactive authentication
		
		- `publicKeyFile=/path/to/key/file` to use a public/private key for authentication, note this is often `/your_home_dir/.ssh/id_rsa`
		
		- `useDumbPty=true` to request a dumb pty connection
	
	1. Click "Add Datasource" Button


**Running Commands Remotely**: You can execute commands on remote machines as well using an *AMI Relay*. First install an AMI relay on the machine that the command should be executed on ( See [../setup.md#running-components-independently] documentation for details on how to install an AMI relay).  Then in the Add Datasource wizard select the relay in the "Relay To Run On" dropdown.

## General Directives

### `_cmd`

**Syntax**

`_cmd="command to run"`

**Overview**

This directive controls the command to execute. **Required**

**Examples**

`_cmd="ls -lrt"` (execute ls -lrt)

`_cmd="ls | sort"` (execute ls and pipe that into sort)

`_cmd="dir /s"` (execute dir on a windows system)

### `_stdin`

**Syntax**

`_stdin="text to pipe into stdin"`

**Overview**

This directive is used to control what data is piped into the standard in (stdin) of the process to run.**Required**

**Example**

`_cmd="cat > out.txt" _stdin="hello world"` (will pipe "hello world" into out.txt)

### _capture

**Syntax**

`_capture="comma_delimited_list"` (default is stdout,stderr,exitCode)

**Overview**

This directive is used to control what output data from running the command is captured. It is a comma delimited list and the order determines what order the tables are returned in.  An empty list ("") indicates that nothing will be captured (the command is executed, and all output is ignored). Options include the following:

- stdout - Capture standard out from the process
- stderr - Capture standard error from the process
- exitCode - Capture the exit code from the process

**Examples**

`_capture="exitCode,stdout"` (the 1st table will contain the exit code, 2nd will contain stdout)

### `_fields`

**Syntax**

`_fields=col1_type col_name, col2_type col2_name, ...`

**Overview**

This directive controls the Column names that will be returned, along with their types.  The order in which they are defined is the same as the order in which they are returned.  If the column type is not supplied, the default is String. Special note on additional columns: If the line number (see [_linenum directive](#_linenum)) column is not supplied in the list, it will default to type integer and be added to the end of the table schema.  Columns defined in the Pattern (see [_pattern directive](#directives-for-pattern-capture)) but not defined in \_*fields* will be added to the end of the table schema.

Types should be one of: `String`, `Long`, `Integer`, `Boolean`, `Double`, `Float`, `UTC`

Column names must be valid variable names.

**Examples**

`_fields="String account,Long quantity"` (define two columns)

`_fields ="fname,lname,int age"` (define 3 columns, fname and lname default to String)

### `_env`

**Syntax**

`_env="key=value,key=value,..."` (Optional. Default is false)

**Overview**

This directive controls what environment variables are set when running a command

**Example**

`_env="name=Rob,Location=NY"`

### `_useHostEnv`

**Syntax**

`_useHostEnv=true|false` (Optional. Default is false)

**Overview**

If true, then the environment properties of the Ami process executing the command are passed to the shell.  Please note, that `_env` values can be used to override specific environment variables.

**Example**

`_useHostEnv="true"`

## Directives for parsing Delimited list of ordered Fields

`_cmd=command_to_execute` (Required, see [General Directives](#_cmd))

`_fields=col1_type col1_name, ...` (Required, see [General Directives](#_fields))

`_delim=delim_string` (Required)

`_conflateDelim=true|false` (Optional. Default is false)

`_quote=single_quote_char` (Optional)

`_escape=single_escape_char` (Optional)

The `_delim` indicates the char (or chars) used to separate each field (If `_conflateDelim` is true, then 1 or more consecutive delimiters are treated as a single delimiter). The `_fields` is an ordered list of types and field names for each of the delimited fields. If the `_quote` is supplied, then a field value starting with *quote* will be read until another *quote* char is found, meaning *delim*s within *quotes* will not be treated as delims.  If the `_escape` char is supplied then when an *escape* char is read, it is skipped and the following char is read as a literal.

**Examples**

```
_delim="|"
_fields="code,lname,int age"
_quote="'"
_escape="\\"
```

This defines a pattern such that:

```
11232-33|Smith|20
'1332|ABC'||30
Account\|112|Jones|18
```

Maps to:

| code         | lname | age |
|--------------|-------|-----|
| 11232-33     | Smith | 20  |
| 1332\|ABC    |       | 30  |
| Account\|112 | Jones | 18  |

## Directives for parsing Key Value Pairs

`_cmd=command_to_execute` (Required, see [General Directives](#_cmd))

`_fields=col1_type col1_name, ...` (Required, see [General Directives](#_fields))

`_delim=delim_string` (Required)

`_conflateDelim=true|false` (Optional. Default is false)

`_equals=single_equals_char` (Required)

`_mappings=from1=to1,from2=to2,...` (Optional)

`_quote=single_quote_char` (Optional)

`_escape=single_escape_char` (Optional)

The `_delim` indicates the char (or chars) used to separate each field (If `_conflateDelim` is true, then 1 or more consecutive delimiters are treated as a single delimiter). The `_quals` char is used to indicate the key/value separator. The `_fields` is an ordered list of types and field names for each of the delimited fields. If the `_quote` is supplied, then a field value starting with *quote* will be read until another *quote* char is found, meaning *delim*s within *quotes* will not be treated as delims.  If the `_escape` char is supplied then when an *escape* char is read, it is skipped and the following char is read as a literal.

The optional `_mappings` directive allows you to map keys within the output to field names specified in the `_fields` directive. This is useful when a output has key names that are not valid field names, or a file has multiple key names that should be used to populate the same column.

**Examples**

```
_delim="|"
_equals="="
_fields="code,lname,int age"
_mappings="20=code,21=lname,22=age"
_quote="'"
_escape="\\"
```

This defines a pattern such that:

```
code=11232-33|lname=Smith|age=20
code='1332|ABC'|age=30
20=Act\|112|21=J|22=18 (Note: this row will work due to the _mappings directive)
```

Maps to:

| code      | lname | age |
|-----------|-------|-----|
| 11232-33  | Smith | 20  |
| 1332\|ABC |       | 30  |
| Act\|112  | J     | 18  |

## Directives for Pattern Capture

`_cmd=command_to_execute` (Required, see [General Directives](#_cmd))

`_fields=col1_type col1_name, ...` (Optional, see [General Directives](#_fields))

`_pattern=col1_type col1_name, ...=regex_with_grouping` (Required)

The `_pattern` must start with a list of column names, followed by an equal sign (=) and then a regular expression with grouping (this is dubbed a column-to-pattern mapping).  The regular expression's first grouping value will be mapped to the first column, 2nd grouping to the second and so on.

If a column is already defined in the `_fields` directive, then it's preferred to not include the column type in the `_pattern` definition.

For multiple column-to-pattern mappings, use the \\n (new line) to separate each one.

**Example 1**

```
_pattern="fname,lname,int age=User (.*) (.*) is (.*) years old"
```

This defines a pattern such that:

```
User John Smith is 20 years old
User Bobby Boy is 30 years old
```

Maps to:

| fname | lname | age |
|-------|-------|-----|
| John  | Smith | 20  |
| Bobby | Boy   | 30  |

**Example 2**

```
_pattern="fname,lname,int age=User (.*) (.*) is (.*) years old\n lname,fname,int weight=Customer (.*),(.*) weighs (.*) pounds"
```

This defines two patterns such that:

```
User John Smith is 20 years old
User Boy,Bobby weighs 130 pounds
```

Maps to:

| fname | lname | age | weight |
|-------|-------|-----|--------|
| John  | Smith | 20  |        |
| Bobby | Boy   |     | 130    |

## Optional Line Number Directives

### `_skipLines`

**Syntax**

`_skipLines=number_of_lines`

**Overview**

This directive controls the number of lines to skip from the top of the file. This is useful for ignoring "junk" at the top of a file. If not supplied, then no lines are skipped. From a performance standpoint, skipping lines is highly efficient.

**Examples**

`_skipLines="0"`     (this is the default, don't skip any lines)

`_skipLines="1"`      (skip the first line, for example if there is a header)

### `_linenum`

**Syntax**

`_linenum=column_name`

**Overview**

This directive controls the name of the column that will contain the line number. If not supplied, the default is "linenum". Notes about the line number: The first line is line number 1, and skipped/filtered out lines are still considered in numbering. For example, if the `_skipLines=2`, then the first line will have a line number of 3.

**Examples**

`_linenum=""` (A line number column is not included in the table)

`_linenum="linenum"` (The column linenum will contain line numbers, this is the default)

`_linenum="rownum"` (The column rownum will contain line numbers)

## Optional Line Filtering Directives

### `_filterOut`

**Syntax**

`_filterOut=regex`

**Overview**

Any line that matches the supplied regular expression will be ignored. If not supplied, then no lines are filtered out. From a Performance standpoint, this is applied before other parsing is considered, so ignoring lines using a filter out directive is faster, as opposed to using a WHERE clause, for example.

**Examples**

`_filterOut="Test"` (ignore any lines containing the text Test)

`_filterOut="^Comment"` (ignore any lines starting with Comment)

`_filterOut="This|That"` (ignore any lines containing the text This or That)

### `_filterIn`

**Syntax**

`_filterIn=regex`

**Overview**

Only lines that match the supplied regular expression will be considered. If not supplied, then all lines are considered. From a Performance standpoint, this is applied before other parsing is considered, so narrowing down the lines considered using a filter in directive is faster, as opposed to using a WHERE clause, for example. If you use a grouping (..) inside the regular expression, then only the contents of the first grouping will be considered for parsing

**Examples**

`_filterIn="3Forge"` (ignore any lines that don't contain the word 3Forge)

`_filterIn="^Outgoing"` (ignore any lines that don't start with Outgoing)

`_filterIn="Data(.*)"` (ignore any lines that don't start with Data, and only consider  the text after the word Data for processing)

