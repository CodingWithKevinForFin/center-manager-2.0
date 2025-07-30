# Data Filter 

As part of AMI's [custom Java plugin](./index.md) functionality, data filters are highly customizable Java plugins that can be used to set user permissions for data access in AMI applications. 

## Overview

The data filter plugin controls what a given user can access based on some parameters, e.g; region. This stops users from accessing data they are not entitled to. 

Generally, the web filter is deployed one of two ways: 

1. Filtering real-time data as rows are updated.
2. Filtering the resulting table of a datamodel query. 

Typically, a data filter is invoked after some internal authentication system which will assign system variables to a user, such as region. The instructions for writing a custom entitlements plugin can be found [here](./entitlements.md).

!!! Note
    A data filter is initialized per *session* of AMI -- if multiple users are using the same AMI dashboard, each user will have a separate instance of the data filter applied to them.

## Requirements 

You will need to follow the general steps for setting up a [custom plugin](./index.md/#custom-plugin). Once your Java project has been set up, ensuring `autocode.jar` and `out.jar` are included in your build path; you will need to include the following factories in your project classes: 

### Java Interface

```java
com.f1.ami.web.datafilter.AmiWebDataFilterPlugin 
com.f1.ami.web.datafilter.AmiWebDataFilterinstance 
```

These factories contain all the methods to needed for creating custom code.  

### Properties 

You will also need to include the completed plugin in your AMI `local.properties`:

```
ami.web.data.filter.plugin.class=fully_qualified_class_name
``` 

## General Implementation

1. Use the `#!java com.f1.ami.web.auth.AmiAuthenticator` plugin to authenticate a user and return a set of variables that are assigned to the user's session. These variables will be used to set the flags for the data filter.

1. This user-session is passed into the `#!java com.f1.ami.web.datafilter.AmiWebDataFilterPlugin` which then returns a `#!java com.f1.ami.web.datafilter.AmiWebDataFilterinstance`. 

3. As data is passed from the backend to the frontend, the user's `AmiWebDataFilter` is called first which determines whether to suppress the data. 

Data filters can be applied to both real-time and static datamodel queries. The methods to implement are below.

### Realtime 

As data is streamed into AMI, individual records are transported to the frontend for display on a per-row basis. 

- Rows can be added using `#!java AmiWebDataFilter::evaluateNewRow(...)`.
- Rows can be updated using `#!java AmiWebDataFilter::evaluateUpdateRow(...)`.  

Deciding how rows are added or updated is determined by flags. The flags determines when the data filter is run and the output.

#### Visibility Flags

| Flag 		    | Behavior 																		|
|---------------|-------------------------------------------------------------------------------|
| `HIDE_ALWAYS` | The row is always hidden after the filter is run (the filter does not re-run) |
| `SHOW_ALWAYS` | The row is always shown after filter is run (the filter does not re-run)		|
| `HIDE`        | The row is hidden until updated, then the filter is re-run 					|
| `SHOW`        | The row is shown until updated, then the filter is re-run 					|

### Static Query Results
	
When the user invokes a query (generally via the `EXECUTE` command within a datamodel), a query object is constructed and sent to the backend for execution. Then, the backend responds with a table (or multiple tables) of data. 
	
- Query request is sent as a message via `#!java AmiWebDataFilter::evaluateQueryRequest(...)`.
- Apply the logic for filtering the response/output from the datamodel with `#!java AmiWebDataFilter::evaluateQueryResponse(...)` 

## Example

This example implements a simple data filter that checks if a user has an assigned "region" before determining whether to show or hide data. 

In `local.properties`, add the package and class names used in the data filter plugin java file to the corresponding property:

```
ami.web.data.filter.plugin.class=data_filter.SampleDataFilterPlugin
```

This requires two Java classes: the plugin adapter (which will be set in `local.properties`) and the functionality.

### Plugin Factory

```java
package data_filter;

import com.f1.ami.web.datafilter.AmiWebDataFilter;
import com.f1.ami.web.datafilter.AmiWebDataFilterPlugin;
import com.f1.ami.web.datafilter.AmiWebDataSession;
import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;

public class SampleDataFilterPlugin implements AmiWebDataFilterPlugin {


        @Override
        public AmiWebDataFilter createDataFilter(AmiWebDataSession session) {
                return new SampleDataFilter(session);
        }

        @Override
        public void init(ContainerTools tools, PropertyController props) {
        }

        @Override
        public String getPluginId() {
                return "DATAFILTER_PLUGIN";
        }
}
```

### Data Filter Factory

The implementation of the filter log itself is contained in a separate Java class that implements the actual `AmiWebDataFilter` factory. 

- Realtime feeds are filtered using `evaluateNewRow` and `evaluateUpdatedRow`.
- Datamodel quieres are filtered with `evaluateQueryRequest` and `evaluateQueryResponse`.

```java
package data_filter;

import com.f1.ami.web.AmiWebObject;
import com.f1.ami.web.datafilter.AmiWebDataFilter;
import com.f1.ami.web.datafilter.AmiWebDataFilterQuery;
import com.f1.ami.web.datafilter.AmiWebDataSession;
import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.utils.structs.table.columnar.ColumnarTable;

public class SampleDataFilter implements AmiWebDataFilter {

	private AmiWebDataSession userSession;
	private String allowedRegion;

	public SampleDataFilter(AmiWebDataSession session) {
			this.userSession = session; // Grabs the user session
			allowedRegion = (String) userSession.getVariableValue("region"); // Checks for the "region" variable, which would have been assigned by a data filter
			if(allowedRegion==null)
					throw new RuntimeException("no 'region' specified");
	}
			
	@Override
	public void onLogin() {
			//Code to implement when the user logs in;
	}

	@Override
	public void onLogout() {
			//Code to implement when the user logs out;
	}

	@Override
	public byte evaluateNewRow(AmiWebObject realtimeRow) {
			String region = (String) realtimeRow.getParam("region");
			return allowedRegion.equals(region) ? SHOW_ALWAYS : HIDE_ALWAYS;
	}

	@Override
	public byte evaluateUpdatedRow(AmiWebObject realtimeRow, byte currentStatus) {
			Object region = (String) realtimeRow.getParam("region");
			return allowedRegion.equals(region) ? SHOW_ALWAYS : HIDE_ALWAYS;
	}

	@Override
	public void evaluateQueryResponse(AmiWebDataFilterQuery query, ColumnarTable table) {
			Column regionColumn = table.getColumnsMap().get("region");
			if (regionColumn == null)
					return;
			for (int i = table.getSize() - 1; i >= 0; i--) {
					Row row = table.getRow(i);
					String region = row.getAt(regionColumn.getLocation(), String.class);
					if (!allowedRegion.equals(region))
							table.removeRow(row);
			}
	}
	
	@Override
	public AmiWebDataFilterQuery evaluateQueryRequest(AmiWebDataFilterQuery query) {
			return query;
	}

}
```

## Blank `WebDataFilter` Java File

```java
package com.f1.ami.web.datafilter;

import com.f1.ami.web.AmiWebObject;
import com.f1.ami.web.datafilter.AmiWebDataFilter;
import com.f1.ami.web.datafilter.AmiWebDataFilterQuery;
import com.f1.ami.web.datafilter.AmiWebDataSession;
import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.utils.structs.table.columnar.ColumnarTable;

public interface AmiWebDataFilter {
	public static final byte HIDE_ALWAYS = 1;
	public static final byte SHOW_ALWAYS = 2;
	public static final byte HIDE = 3;
	public static final byte SHOW = 4;

	/**
	 * Called the user logs
	 * 
	 */
	public void onLogin();

	/**
	 * Called when the user logs out
	 * 
	 */
	public void onLogout();

	/**
	 * Called when a new row is received from the Center. Note that during login, all rows for display will pass through this method
	 * 
	 * @param realtimeRow
	 *            the row to filter
	 * @return {@link #HIDE_ALWAYS} - The row will be hidden from the user, regardless of any subsequent updates to the row's data (less overhead)<BR>
	 *         {@link #HIDE} - The row will be hidden from the user, but future updates to the row's data will be re-evaluated via
	 *         {@link #evaluateUpdatedRow(AmiWebObject) }(greater overhead)<BR>
	 *         {@link #SHOW_ALWAYS} - The row will be visible to the user, regardless of any subsequent updates to the row's data (less overhead)<BR>
	 *         {@link #SHOW} - The row will be visible the user, but future updates to the row's data will be re-evaluated via {@link #evaluateUpdatedRow(AmiWebObject)} (greater
	 *         overhead)<BR>
	 * 
	 */
	public byte evaluateNewRow(AmiWebObject realtimeRow);

	/**
	 * Called when a new row is updated from the Center. Note that this is only called for realtimeRows whose prior evaluation either returned {@link #HIDE} or {@link #SHOW}
	 * 
	 * @param realtimeRow
	 *            the row to filter
	 * @param currentStatus
	 *            Either {@link #HIDE} or {@link #SHOW}
	 * @return {@link #HIDE_ALWAYS} - The row will be hidden from the user, regardless of any subsequent updates to the row's data (less overhead)<BR>
	 *         {@link #HIDE} - The row will be hidden from the user, but future updates to the row's data will be re-evaluated via {@link #evaluateUpdatedRow(AmiWebObject)}(greater
	 *         overhead)<BR>
	 *         {@link #SHOW_ALWAYS} - The row will be visible to the user, regardless of any subsequent updates to the row's data (less overhead)<BR>
	 *         {@link #SHOW} - The row will be visible the user, but future updates to the row's data will be re-evaluated via {@link #evaluateUpdatedRow(AmiWebObject)} (greater
	 *         overhead)<BR>
	 * 
	 */
	public byte evaluateUpdatedRow(AmiWebObject realtimeRow, byte currentStatus);

	/**
	 * This method is called after each EXECUTE completes. In the general case, the Rows of the Table of the will be evaluated and certain rows may be deleted.
	 * 
	 * @param query
	 *            - The query passed to the backend.
	 * @param table
	 *            - the resulting table from the query
	 */
	public void evaluateQueryResponse(AmiWebDataFilterQuery query, ColumnarTable table);

	/**
	 * Called before the request is sent to the backend. If the query is permitted as is, simply return the query param. To reject the query return null. Or create a new
	 * {@link AmiWebDataFilterQueryImpl} object and set the various parameters that should actually be executed
	 * 
	 * @param query
	 *            the query that the user would like to run
	 * @return the actually query to run, or null if the query should not be executed.
	 */
	AmiWebDataFilterQuery evaluateQueryRequest(AmiWebDataFilterQuery query);

}

```
