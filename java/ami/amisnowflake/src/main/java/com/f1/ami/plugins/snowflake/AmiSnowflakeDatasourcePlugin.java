package com.f1.ami.plugins.snowflake;

import java.util.Map;

import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiDatasourcePlugin;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.PropertyController;

public class AmiSnowflakeDatasourcePlugin implements AmiDatasourcePlugin {
	//TODO: Check static variables for correctness
	private static final String DATASOURCE_TYPE = "SNOWFLAKE";
	private static final String DATASOURCE_DESC = "Snowflake JDBC";
	private static final Map<String, Object> OPERATORS_MAP = CH.m( //
			AmiDatasourcePlugin.OPERATOR_KEY_EQUAL_TO, "=", //
			AmiDatasourcePlugin.OPERATOR_KEY_NOT_EQUAL_TO, "!=", //
			AmiDatasourcePlugin.OPERATOR_KEY_LESS_THAN, "<", //
			AmiDatasourcePlugin.OPERATOR_KEY_GREATER_THAN, ">", //
			AmiDatasourcePlugin.OPERATOR_KEY_LESS_THAN_OR_EQUAL_TO, "<=", //
			AmiDatasourcePlugin.OPERATOR_KEY_GREATER_THAN_OR_EQUAL_TO, ">=" //
	);
	private static final Map<String, Object> WHERE_SYNTAX_MAP = CH.m( //
			AmiDatasourcePlugin.WHERE_SYNTAX_PREFIX, "((", //
			AmiDatasourcePlugin.WHERE_SYNTAX_JOIN, ") OR (", //
			AmiDatasourcePlugin.WHERE_SYNTAX_SUFFIX, "))", //
			AmiDatasourcePlugin.WHERE_SYNTAX_TRUE, "TRUE", //
			AmiDatasourcePlugin.WHERE_SYNTAX_FALSE, "FALSE" //
	);
	private static final Map<String, Object> HELP_MAP = CH.m(//
			AmiDatasourcePlugin.HELP_URL, "[account_identifier].snowflakecomputing.com/?[connection_params]", //
			AmiDatasourcePlugin.HELP_GENERAL, "Snowflake General Help" //
	);
	private static final Map<String, String> OPTIONS_MAP = AmiSnowflakeDatasourceAdapter.buildOptions();

	@Override
	public void init(ContainerTools tools, PropertyController props) {

	}

	@Override
	public String getPluginId() {
		return DATASOURCE_TYPE;
	}

	@Override
	public String getDatasourceDescription() {
		return DATASOURCE_DESC;
	}

	@Override
	public AmiDatasourceAdapter createDatasourceAdapter() {
		return new AmiSnowflakeDatasourceAdapter();
	}

	@Override
	public String getDatasourceIcon() {
		return "snowflake.png";
	}

	@Override
	public String getDatasourceQuoteType() {
		return "\'";
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
