# Custom Center Objects

AMI supports the use of Java plugins to extend functionality. To write your own custom objects that perform actions on AMI center, we supply the following factories:

1.      Custom Triggers
2.      Custom Procedures
3.      Custom Timers
4.      Custom Persistence Factories

See below for guides on implementing each factory in Java.

## Custom Triggers

### Overview

AMI's in-memory database is a comprehensive and realtime SQL storage engine that can be extended using Java Plugins. The trigger plugin is a factory used to create triggers as defined in the AMIDB schema. 

### Example 

```amiscript
CREATE TRIGGER mytrigger OFTYPE MyRiskCalc ON myTable USE myoption="some_value"
```

The above sample command will cause AMI to:

1.  Look for a registered `AmiTriggerFactory` with the id `MyRiskCalc`.  
1.  Call `newTrigger()`  on the factory.
1.  Call `startup(...)` on the returned, newly generated trigger. Note that the startup will contain the necessary bindings:
    -   Trigger name (ex: `mytrigger`)
    -   Options (ex: `myoption=some_value`)
    -   Target tables (ex: `myTable`)

### Java interface

```java
com.f1.ami.center.triggers.AmiTriggerFactory
```

### Properties

```
ami.db.trigger.plugins=comma_delimited_list_of_fully_qualified_java_class_names
```

### Example

Java Code:

``` java
package com.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.ami.center.triggers.AmiTrigger;
import com.f1.ami.center.triggers.AmiTriggerFactory;
import com.f1.anvil.triggers.AnvilTriggerOrdersBySymSide;
import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;

public class TestTriggerFactory implements AmiTriggerFactory {
        private List<AmiFactoryOption> options = new ArrayList<AmiFactoryOption>();

        @Override
        public Collection<AmiFactoryOption> getAllowedOptions() {
                return options;
        }

        @Override
        public void init(ContainerTools tools, PropertyController props) {
        }

        @Override
        public String getPluginId() {
                return "TESTTRIGGER";
        }

        @Override
        public AmiTrigger newTrigger() {
                return new TestTrigger();
        }
}

package com.demo;

import com.f1.ami.center.table.AmiPreparedQuery;
import com.f1.ami.center.table.AmiPreparedQueryCompareClause;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.triggers.AmiAbstractTrigger;

public class TestTrigger extends AmiAbstractTrigger {

        @Override
        public void onStartup() {
                // TOD Auto-generated method stub
        }

        @Override
        public void onInserted(AmiTable table, AmiRow row) {
                // TODO Auto-generated method stub
        }

        @Override
        public boolean onInserting(AmiTable table, AmiRow row) {
                // TODO Auto-generated method stub
                return super.onInserting(table, row);
        }

        @Override
        public void onUpdated(AmiTable table, AmiRow row) {
                // TODO Auto-generated method stub
        }

        @Override
        public boolean onUpdating(AmiTable table, AmiRow row) {
                // TODO Auto-generated method stub
                return super.onUpdating(table, row);
        }

        @Override
        public boolean onDeleting(AmiTable table, AmiRow row) {
                // TODO Auto-generated method stub
                return super.onDeleting(table, row);
        }
} 
```

Configuration:

```
ami.db.trigger.plugins=com.demo.TestTriggerFactory
```

## Custom Stored Procedure

### Overview

AMI's in-memory database is a comprehensive and realtime SQL storage engine that can be extended using Java Plugins.  The stored procedure plugin is a factory used to create stored procedures as defined in the imdb schema. Below we'll go through an example.

Consider the Ami Script example:                                                                                                                                      

```amiscript
CREATE PROCEDURE myproc OFTYPE MyCustProc USE myoption="some_value"
```

The above sample command will cause AMI to:

1.  Look for a registered AmiStoredProcFactory with the id `MyCustProc`.  
2.  Call `newStoredProc()`  on the factory.
3.  Call `startup(...)` on the returned, newly generated storedproc. Note that the startup will contain the necessary bindings:
    -   Procedure name (ex: `myproc`)
    -   Options (ex: `myoption=some_value`)
    -   Target tables (ex: `myTable`)

### Java interface

```java
com.f1.ami.center.procs.AmiStoredProcFactory
```

### Properties

```
ami.db.procedure.plugins=comma_delimited_list_of_fully_qualified_java_class_names
```

For example:

```
ami.db.procedure.plugins=com.demo.TestProcFactory
```

## Custom Timer

### Overview

AMI's in-memory database is a comprehensive and realtime SQL storage engine that can be extended using Java Plugins.  The timer plugin is a factory used to create timers as defined in the imdb schema. Below we'll go through an example.

Consider the Ami Script example:

```amiscript
CREATE TIMER mytimer OFTYPE MyStartupTimer ON "0 0 0 0 MON-FRI UTC " USE myoption="some_value"
```

The above sample command will cause AMI to:

1.  Look for a registered AmiStoredProcFactory with the id "MyStartupTimer".  
2.  Call `newTimer()`  on the factory.  
3.  Call `startup(...)` on the returned, newly generated storedproc. Note that the startup will contain the necessary bindings:
    -   Timer name (ex: `mytimer`)
    -   Options (ex: `myoption=some_value`)
    -   Schedule, Priority, etc.

### Java interface

```java
com.f1.ami.center.timers.AmiTimerFactory
```

### Properties

```
ami.db.timer.plugins=*comma_delimited_list_of_fully_qualified_java_class_names*
```

### Example

Java Code:

``` java
package com.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;
import com.f1.ami.center.timers.AmiTimerFactory;
import com.f1.ami.center.timers.AmiTimer;

public class TestTimerFactory implements AmiTimerFactory {

       private List<AmiFactoryOption> options = new ArrayList<AmiFactoryOption>();

       public TestTimerFactory() {
               // TODOAuto-generated method stub
       }

       @Override
       public void init(ContainerTools tools, PropertyController props) {
               // TODOAuto-generated method stub
       }

       @Override
       public Collection<AmiFactoryOption> getAllowedOptions() {
               return options;
       }

       @Override
       public AmiTimer newTimer() {
               return new TestTimer();
       }

       @Override
       public String getPluginId() {
               return "MyStartupTimer";
       }
}

package com.demo;

import com.f1.ami.center.table.AmiImdb;
import com.f1.ami.center.timers.AmiAbstractTimer;

public class TestTimer extends AmiAbstractTimer {

       @Override
       public void onTimer(long scheduledTime, AmiImdbSession sesssion, AmiCenterProcess process) {
               // TODOAuto-generated method stub
       }

       @Override
       protected void onStartup(AmiImdbSession timerSession) {
               // TODOAuto-generated method stub
       }
}
```

Configuration:

```
ami.db.timer.plugins=com.demo.TestTimerFactory
```

See the List of available AMI procedures for some useful procedures with timers.

## Custom Persistence Factory

### Overview

AMI's in-memory database is a comprehensive and realtime SQL storage engine that can be extended using Java Plugins.  The persistence plugin is a factory used to create table persister instances as defined in the imdb schema. Below we'll go through an example.

Consider the Ami Script example:

```amiscript
CREATE TABLE mytable(col1 int) USE PersistEngine="MyPersister" PersistOptions="myoption=some_val"
```

The above sample command will cause AMI to:

1.  Create a table (named `mytable`) with the specified columns (`col1`)
2.  Look for a registered AmiTablePersisterFactory with the id `MyPersister`.
3.  Call `newPersister()`  on the factory, passing in a map of supplied options (`myoption=some_val`)
4.  Call `init(...)` on the returned, newly generated persister.  

### Java interface

```java
com.f1.ami.center.table.persist.AmiTablePersisterFactory
```

### Properties

```
ami.db.persister.plugins=comma_delimited_list_of_fully_qualified_java_class_names
```

### Example

Java Code:

``` java
package com.demo;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;

public class TestPersisterFactory implements AmiTablePersisterFactory {

        @Override
        public void init(ContainerTools tools, PropertyController props) {
                // TODOAuto-generated method stub
        }

        @Override
        public AmiTablePersister newPersister(Map<String, Object> options) {
                return new TestPersister();
        }

        @Override
        public String getPluginId() {
                return "TESTPERSISTER";
        }

        @Override
        public Collection<AmiFactoryOption> getAllowedOptions() {
                return Collections.EMPTY_LIST;
        }

}

package com.demo;

import java.io.IOException;

import com.f1.ami.center.AmiSysCommandsUtils;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.ami.center.table.AmiTable;
import com.f1.utils.LH;

public class TestPersister implements AmiTablePersister {

        @Override
        public void init(AmiTable sink) {
                // TODOAuto-generated method stub      
        }

        @Override
        public void onRemoveRow(AmiRowImpl row) {
                // TODOAuto-generated method stub
        }

        @Override
        public void onAddRow(AmiRowImpl r) {
                // TODOAuto-generated method stub
        }

        @Override
        public void onRowUpdated(AmiRowImpl sink, long updatedColumns) {
                // TODOAuto-generated method stub
        }

        @Override
        public void loadTableFromPersist() {
                // TODOAuto-generated method stub
        }

        @Override
        public void saveTableToPersist() {
                // TODOAuto-generated method stub
        }

        @Override
        public void clear() {
                // TODOAuto-generated method stub
        }

        @Override
        public void flushChanges() {
                // TODOAuto-generated method stub
        }

        @Override
        public void drop() {
                // TODOAuto-generated method stub
        }

        @Override
        public void onTableRename(String oldName, String name) {
                // TODOAuto-generated method stub
        }
}
```

Configuration:

```
ami.db.persister.plugins=com.demo.TestPersisterFactory
```

