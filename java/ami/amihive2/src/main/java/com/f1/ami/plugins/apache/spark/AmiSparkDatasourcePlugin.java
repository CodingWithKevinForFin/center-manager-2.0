package com.f1.ami.plugins.apache.spark;

import java.util.Map;

import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiDatasourcePlugin;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.PropertyController;

public class AmiSparkDatasourcePlugin implements AmiDatasourcePlugin {
	private static final String DATASOURCE_TYPE = "SPARK";
	private static final String DATASOURCE_DESC = "Apache Spark (Hive2)";

	private static final Map<String, Object> OPERATORS_MAP = CH.m(AmiDatasourcePlugin.OPERATOR_KEY_EQUAL_TO, "=", AmiDatasourcePlugin.OPERATOR_KEY_NOT_EQUAL_TO, "!=",
			AmiDatasourcePlugin.OPERATOR_KEY_LESS_THAN, "<", AmiDatasourcePlugin.OPERATOR_KEY_GREATER_THAN, ">", AmiDatasourcePlugin.OPERATOR_KEY_LESS_THAN_OR_EQUAL_TO, "<=",
			AmiDatasourcePlugin.OPERATOR_KEY_GREATER_THAN_OR_EQUAL_TO, ">=");
	private static final Map<String, Object> WHERE_SYNTAX_MAP = CH.m(AmiDatasourcePlugin.WHERE_SYNTAX_PREFIX, "((", AmiDatasourcePlugin.WHERE_SYNTAX_JOIN, ") or (",
			AmiDatasourcePlugin.WHERE_SYNTAX_SUFFIX, "))", AmiDatasourcePlugin.WHERE_SYNTAX_TRUE, "TRUE", AmiDatasourcePlugin.WHERE_SYNTAX_FALSE, "FALSE");
	private static final Map<String, Object> HELP_MAP = CH.m(AmiDatasourcePlugin.HELP_URL, "server_address:port_number/database_name;", AmiDatasourcePlugin.HELP_GENERAL,
			"spark general help");

	private static final Map<String, String> OPTIONS_MAP = AmiSparkDatasourceAdapter.buildOptions();

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
		return new AmiSparkDatasourceAdapter();
	}

	@Override
	public String getDatasourceIcon() {
		return "spark.png";
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
