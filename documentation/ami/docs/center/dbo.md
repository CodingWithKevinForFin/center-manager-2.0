# Database Objects

## Overview 

The `DataBaseObject` (DBO) is a class in the AMI Center allowing for custom objects (backed by java code) to be added to the database schema. This is similar to adding tables, procedures, timers, etc., and shares similar AMIScript methods.

## AMIScript Syntax for DBOs

```amiscript
CREATE DBO ... OF TYPE … USE callback_... = “….”
DROP DBO ...
DESCRIBE DBO ...
SHOW DBOS
SHOW [FULL] DBO …
ENABLE DBO ...
DISABLE DBO ...
RENAME DBO ... TO …
ALTER DBO ... USE callback_... = "..."
```

## Properties

DBOs will not only be displayed when using the command `SHOW DBOS`,  but are also visible when calling `SHOW VARS`. There are 3 properties that make DBOs special:  


1.	When creating a new DBO, it gets added to the managed schema. This means it's available the next time the database is started up.
2.	DBOs have names and types like other objects, and like objects, can be mutated. For example, a DBO named `myFirstDbo`, can call `myFirstDbo.getClassName()` to return the type of the DBO. In this way they are identical to `ami.web.amiscript.custom.classes`.
3.	DBOs can have callbacks which can be defined at runtime using custom AMIScript. These callbacks are thread safe.  

!!!Note 
	The initial intent is that a DBO will represent 2-way communication message buses.


## Example: A Timer Based DBO

### Setup


Enabling DBOs is similar to using other custom Java Plugins in AMI. First, add this option to your `local.properties` file:

```
ami.db.dbo.plugins=com.f1.ami.center.dbo.SimpleTimerDboPlugin
```

Then add these 2 classes. The first is a factory for defining what methods and callbacks are available and also creating instances of the `SimpleTimer`. The second is the implementation of the SimpleTimer in a thread-safe manner.

```java
//FACTORY
package com.f1.ami.center.dbo;
import com.f1.utils.structs.table.derived.ParamsDefinition;
public class SimpleTimerDboPlugin extends AmiDboFactory_Reflection {
	public SimpleTimerDboPlugin() {
		super(SimpleTimerDbo.class);
		addCallback(new ParamsDefinition("onPeriodChanged", Object.class, "Integer old,Integer nuw"));
		addCallback(new ParamsDefinition("onTimer", Object.class, "Long now,Integer count"));
	}
}
//Implementation
package com.f1.ami.center.dbo;
import java.util.Map;
import com.f1.ami.amicommon.customobjects.AmiScriptAccessible;
import com.f1.utils.CH;
import com.f1.utils.OH;
@AmiScriptAccessible(name = "SimpleTimer")
public class SimpleTimerDbo extends Thread implements AmiDbo {
	private int sleep = 1000;
	private AmiDboBinding binding;
	private boolean closed = false;
	@AmiScriptAccessible(name = "setPeriod", params = { "millis" })
	public void setPeriod(int n) {
		if (n <= 0)
			throw new RuntimeException("Bad value");
		if (n != this.sleep)
			this.binding.executeCallbackNoThrow("onPeriodChanged", (Map) CH.m("old", sleep, "nuw", n));
		this.sleep = n;
	}
	@AmiScriptAccessible(name = "getPeriod")
	public int getPeriod() {
		return this.sleep;
	}
	@Override
	public void run() {
		int n = 0;
		while (!closed) {
			OH.sleep(Math.max(this.sleep, 1));
			this.binding.executeCallbackNoThrow("onTimer",  (Map) CH.m("now", System.currentTimeMillis(), "count", n));
			n++;
		}
	}
	@Override
	public void startAmiDbo(AmiDboBinding peer) {
		this.binding = peer;
		start();
	}
	@Override
	public void closeAmiDbo() {
		this.closed = true;
	}
	@Override
	public AmiDboBinding getAmiDboBinding() {
		return this.binding;
	}
	@Override
	public String toString() {
		return "SimpleTimer[Period=" + this.sleep + "]";
	}
}

```

### Sample Instructions

You can now implement an instance of DBO of `SimpleTimer` described above. Here are some sample instructions and the given outputs in the AMIDB Shell Tool:  


### Show Methods

```
AMIDB-DEV> show methods where TargetType=="SimpleTimer"
+---------------------------------------------------------------------------------+
|                                     METHODS                                     |
+-----------+----------+----------+-------------------------------------+---------+
|TargetType |MethodName|ReturnType|Definition                           |DefinedBy|
|String     |String    |String    |String                               |String   |
+-----------+----------+----------+-------------------------------------+---------+
|SimpleTimer|getPeriod |Integer   |SimpleTimer.getPeriod()              |SYSTEM   |
|SimpleTimer|setPeriod |Object    |SimpleTimer.setPeriod(Integer millis)|SYSTEM   |
+-----------+----------+----------+-------------------------------------+---------+
```

### Show Plugins

```
AMIDB-DEV> show full plugins where PluginType=="DBO"
+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
|                                                                                                PLUGINS                                                                                                 |
+-----------+----------+--------------------------------------+------------------------------------------------------------------------------------------------------------------------------------------+
|PluginName |PluginType|ClassType                             |Arguments                                                                                                                                 |
|String     |String    |String                                |String                                                                                                                                    |
+-----------+----------+--------------------------------------+------------------------------------------------------------------------------------------------------------------------------------------+
|SimpleTimer|DBO       |com.f1.ami.center.AmiDboFactoryWrapper|callback_onPeriodChanged String  /*onPeriodChanged(Integer old,Integer nuw)*/,callback_onTimer String  /*onTimer(Long now,Integer count)*/|
+-----------+----------+--------------------------------------+------------------------------------------------------------------------------------------------------------------------------------------+
```

### Show Variables (Including DBOs)

```
AMIDB-DEV> CREATE PUBLIC TABLE test(value Integer)

AMIDB-DEV> create dbo mytimer oftype SimpleTimer use callback_onTimer="insert into test values(count)";


AMIDB-DEV> show vars;
+---------------------------------------------------------+
|                          VARS                           |
+-----------+-----------+------------------------+--------+
|Name       |Type       |Value                   |Readonly|
|String     |String     |String                  |Boolean |
+-----------+-----------+------------------------+--------+
|__SESSIONID|long       |8                       |true    |
|__USERNAME |String     |demo                    |true    |
|mytimer    |SimpleTimer|SimpleTimer[Period=1000]|true    |
+-----------+-----------+------------------------+--------+
```

### Getters and Setters

```
AMIDB-DEV> mytimer.setPeriod(1234);
(AFFECTED 0 ROWS, EXECUTED IN 0.288 MILLISECONDS)
AMIDB-DEV> mytimer.getPeriod();
(Integer)1234
(AFFECTED 0 ROWS, 1 VALUE, EXECUTED IN 0.24 MILLISECONDS)

AMIDB-DEV> show dbo mytimer;
+----------------------------------------------------------------------------------------------------------------------------------------------------------+
|                                                                           DBO                                                                            |
+--------+-----------------------------------------------+------------------------------+-------------+-----------+------------------+-----------+---------+
|Category|Description                                    |AmiScript                     |ExecutedCount|MillisSpent|AvgMillisSpent    |ErrorsCount|LastError|
|String  |String                                         |String                        |Long         |Double     |Double            |Long       |String   |
+--------+-----------------------------------------------+------------------------------+-------------+-----------+------------------+-----------+---------+
|METHOD  |Object setPeriod(Integer p1)                   !null                          |1            |0.0408     |0.0408            |0          !null     |
|METHOD  |Integer getPeriod()                            !null                          |1            |0.0147     |0.0147            |0          !null     |
|CALLBACK|Object onPeriodChanged(Integer old,Integer nuw)!null                          |0            |0.0        |NaN               |0          !null     |
|CALLBACK|Object onTimer(long now,Integer count)         |insert into test values(count)|46           |9.3913     |0.2041586956521739|0          !null     |
+--------+-----------------------------------------------+------------------------------+-------------+-----------+------------------+-----------+---------+

```

### Add a Value Manually (Alter)

```
AMIDB-DEV> alter dbo mytimer use callback_onTimer="insert into test values(this.getPeriod())";
(AFFECTED 0 ROWS, EXECUTED IN 7.264 MILLISECONDS)

AMIDB-DEV> truncate test;//wait a few seconds

AMIDB-DEV> select * from test;
+-------+
| test  |
+-------+
|value  |
|Integer|
+-------+
|1234   |
|1234   |
|1234   |
|1234   |
+-------+
```

### Disable 

```
AMIDB-DEV> disable dbo mytimer;
(AFFECTED 0 ROWS, EXECUTED IN 2.201 MILLISECONDS)

AMIDB-DEV> mytimer.getPeriod();// this will return null because the dbo is disabled
(Integer)null
(AFFECTED 0 ROWS, 1 VALUE, EXECUTED IN 0.13 MILLISECONDS)
```
