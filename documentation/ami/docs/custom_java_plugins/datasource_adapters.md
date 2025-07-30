# Custom Datasource Adapter 

For datasources that AMI does not [natively support](../supported_software.md), you may use the custom datasource adapter functionality provided to create your own adapter. 

## Overview

You can build your own datasource adapter for any datasource that we do not support out of the box. 

There are two parts to building a custom datasource adapter: 

1. A datasource plugin that is responsible for configuring the user interface in GUI. 
2. A datasource adapter that handles the data. 

Each adapter can optionally support the following functionality:

-   Providing a list of available tables
-   Providing for a sample of data for a given table
-   Running a query (Downloading data into AMI)
-   Uploading data from AMI into the datasource

## Requirements 

You will need to set up a custom plugin project following the general instructions [here](../custom_java_plugins/index.md/#custom-plugin). 

For a datsource adapter, you will need to write both the plugin and the adapter to provide the GUI component in AMI.

### Java Interface

```java
com.f1.ami.amicommon.AmiDatasourcePlugin
com.f1.ami.amicommon.AmiDatasourceAdapter
```

### Properties

```
ami.datasource.plugins=comma_delimited_list_of_fully_qualified_java_class_names
```

## Example

Java Code:

``` java
package com.demo;

import java.util.Map;

import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiDatasourcePlugin;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.PropertyController;

public class TestDatasourcePlugin implements AmiDatasourcePlugin {
	private static final Map<String, Object> OPERATORS_MAP = CH.m(AmiDatasourcePlugin.OPERATOR_KEY_EQUAL_TO, "=", AmiDatasourcePlugin.OPERATOR_KEY_NOT_EQUAL_TO, "!=",
			AmiDatasourcePlugin.OPERATOR_KEY_LESS_THAN, "<", AmiDatasourcePlugin.OPERATOR_KEY_GREATER_THAN, ">", AmiDatasourcePlugin.OPERATOR_KEY_LESS_THAN_OR_EQUAL_TO, "<=",
			AmiDatasourcePlugin.OPERATOR_KEY_GREATER_THAN_OR_EQUAL_TO, ">=");
	private static final Map<String, Object> WHERE_SYNTAX_MAP = CH.m(AmiDatasourcePlugin.WHERE_SYNTAX_PREFIX, "((", AmiDatasourcePlugin.WHERE_SYNTAX_JOIN, ") or (",
			AmiDatasourcePlugin.WHERE_SYNTAX_SUFFIX, "))", AmiDatasourcePlugin.WHERE_SYNTAX_TRUE, "true", AmiDatasourcePlugin.WHERE_SYNTAX_FALSE, "false");
	private static final Map<String, Object> HELP_MAP = CH.m(AmiDatasourcePlugin.HELP_URL, "sample url help", AmiDatasourcePlugin.HELP_GENERAL, "sample general help");

	private static final Map<String, String> OPTIONS_MAP = AmiSampleDatasourceAdapter.buildOptions();

	@Override
	public void init(ContainerTools tools, PropertyController props) {
	}

	@Override
	public String getPluginId() {
		return "SAMPLE";
	}

	@Override
	public String getDatasourceDescription() {
		return "Sample";
	}

	@Override
	public AmiDatasourceAdapter createDatasourceAdapter() {
		return new AmiSampleDatasourceAdapter();
	}

	@Override
	public String getDatasourceIcon() {
		return null;
	}

	@Override
	public String getDatasourceQuoteType() {
		return "\"";
	}

	@Override
	public Map<String, Object> getDatasourceOperators() {
		return OPERATORS_MAP;
	}

	@Override
	public Map<String, Object> getDatasourceWhereClauseSyntax() {
		return WHERE_SYNTAX_MAP;
	}

	@Override
	public Map<String, Object> getDatasourceHelp() {
		return HELP_MAP;
	}

	@Override
	public Map<String, String> getAvailableOptions() {
		return OPTIONS_MAP;
	}
}

package com.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.AmiServiceLocator;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterQuery;
import com.f1.ami.amicommon.msg.AmiCenterQueryResult;
import com.f1.ami.amicommon.msg.AmiCenterUpload;
import com.f1.ami.amicommon.msg.AmiDatasourceColumn;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.TimeoutController;

public class TestDatasourceAdapter implements AmiDatasourceAdapter {

	public static Map<String, String> buildOptions() {
		return new HashMap<String, String>();
	}

	private AmiServiceLocator serviceLocator;
	private ContainerTools tools;

	@Override
	public void init(ContainerTools tools, AmiServiceLocator serviceLocator) throws AmiDatasourceException {
		this.serviceLocator = serviceLocator;
		this.tools = tools;
	}

	@Override
	public List<AmiDatasourceTable> getTables(AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		List<AmiDatasourceTable> r = new ArrayList<AmiDatasourceTable>(1);
		AmiDatasourceTable sample = tools.nw(AmiDatasourceTable.class);

		String url = serviceLocator.getUrl();
		char[] pass = serviceLocator.getPassword();
		String user = serviceLocator.getUsername();
		//normally, you'd connect to underlying store at this point and query for the schema.

		//for each table in the schema {
		sample.setName("ASampleTableInTheDatabase");
		String defaultQueryForTable = "SELECT * FROM " + sample.getName() + " WHERE ${WHERE}";
		sample.setCustomQuery(defaultQueryForTable);
		r.add(sample);

		return r;
	}

	@Override
	public List<AmiDatasourceTable> getPreviewData(List<AmiDatasourceTable> tables, int previewCount, AmiDatasourceTracker debugSink, TimeoutController tc)
			throws AmiDatasourceException {
		String url = serviceLocator.getUrl();
		char[] pass = serviceLocator.getPassword();
		String user = serviceLocator.getUsername();
		//normally, you'd connect to underlying store at this point and query for the schema.

		//for each table in the schema
		for (int i = 0; i < tables.size(); i++) {
			AmiDatasourceTable table = tables.get(i);

			List<AmiDatasourceColumn> columns = new ArrayList<AmiDatasourceColumn>();
			table.setName("ASampleTableInTheDatabase");
			columns.add(newCol(String.class, "SampleName"));
			columns.add(newCol(Long.class, "SampleLong"));
			columns.add(newCol(Integer.class, "SampleInt"));
			columns.add(newCol(Double.class, "SampleDouble"));
			columns.add(newCol(Float.class, "SampleFloat"));
			columns.add(newCol(Boolean.class, "SampleBoolean"));
			table.setColumns(columns);
		}
		return tables;
	}

	private AmiDatasourceColumn newCol(Class<?> clazz, String name) {
		AmiDatasourceColumn col = tools.nw(AmiDatasourceColumn.class);
		col.setName("SampleName");
		col.setType(AmiUtils.getTypeForClass(clazz, AmiDatasourceColumn.TYPE_UNKNOWN));
		return col;
	}
	public String getName() {
		return this.serviceLocator.getTargetName();
	}

	//The paradigm for the query is a collection of well defined tables in and a collection of uniquely named tables out.
	@Override
	public void processQuery(AmiCenterQuery query, AmiCenterQueryResult resultSink, AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		System.out.println("AmiSampleDatasource: " + query);

		String url = serviceLocator.getUrl();
		char[] pass = serviceLocator.getPassword();
		String user = serviceLocator.getUsername();
		//normally, you'd connect to underlying store at this point. and store that connection in a member variable to support the cancelQuery() call

		//extract the input query and related fields from the well-known schema

		final List<Table> response = new ArrayList<Table>();
		String select = query.getQuery();

		//build the query
		System.out.println("AmiSampleDatasource: Running Sample Query:\n " + query);

		debugSink.onQuery(select);//useful for end users running in debug/test mode, so they can see the raw query (and time) it was run

		//build the responding table 
		final BasicTable responseTable = new BasicTable();
		//			responseTable.setTitle(query.getResponseTableName());
		responseTable.addColumn(String.class, "SampleName");
		responseTable.addColumn(Long.class, "SampleLong");
		responseTable.addColumn(Double.class, "SampleDouble");
		responseTable.addColumn(Boolean.class, "SampleBoolean");

		//add the rows of data to the table, normally this would be a for look, while looking, make sure you don't exceed limit argument
		responseTable.getRows().addRow("Fred", 40L, 32.4d, true);

		debugSink.onQueryResult(select, responseTable);//useful for users running in debug/test mode. So they can see the raw response
		response.add(responseTable);
		resultSink.setTables(response);
	}
	@Override
	public boolean cancelQuery() {
		//Called from another thread if user clicks the infamous cancel button (should close connections at this point)
		return false;
	}

	@Override
	public void processUpload(AmiCenterUpload upload, AmiCenterQueryResult results, AmiDatasourceTracker tracker) throws AmiDatasourceException {
		throw new AmiDatasourceException(AmiDatasourceException.UNSUPPORTED_OPERATION_ERROR, "Upload to datasource");
	}
	@Override
	public AmiServiceLocator getServiceLocator() {
		return this.serviceLocator;
	}

}
```

Configuration:

```
ami.datasource.plugins=com.demo.TestDatasourcePlugin
```

