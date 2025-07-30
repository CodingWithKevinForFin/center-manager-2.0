package com.f1.ami.plugins.redis;

import java.util.Map;

import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiDatasourcePlugin;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.PropertyController;

public class AmiRedisDatasourcePlugin2 implements AmiDatasourcePlugin {

	private static final String DATASOURCE_TYPE = "Redis";
	private static final String DATASOURCE_DESC = "Redis CData";
	private static final Map<String, Object> OPERATORS_MAP = CH.m(AmiDatasourcePlugin.OPERATOR_KEY_EQUAL_TO, "=", AmiDatasourcePlugin.OPERATOR_KEY_NOT_EQUAL_TO, "!=",
			AmiDatasourcePlugin.OPERATOR_KEY_LESS_THAN, "<", AmiDatasourcePlugin.OPERATOR_KEY_GREATER_THAN, ">", AmiDatasourcePlugin.OPERATOR_KEY_LESS_THAN_OR_EQUAL_TO, "<=",
			AmiDatasourcePlugin.OPERATOR_KEY_GREATER_THAN_OR_EQUAL_TO, ">=");
	private static final String OPERATOR_KEY_AND = "AND";
	private static final String OPERATOR_KEY_OR = "OR";
	private static final Map<String, Object> WHERE_SYNTAX_MAP = CH.m(AmiDatasourcePlugin.WHERE_SYNTAX_PREFIX, "((", AmiDatasourcePlugin.WHERE_SYNTAX_JOIN, ") or (",
			AmiDatasourcePlugin.WHERE_SYNTAX_SUFFIX, "))", AmiDatasourcePlugin.WHERE_SYNTAX_TRUE, "1", AmiDatasourcePlugin.WHERE_SYNTAX_FALSE, "0");
	private static final Map<String, Object> HELP_MAP = CH.m(AmiDatasourcePlugin.HELP_URL, "Server=[address];Port=[port];AuthScheme=[None|Password];",
			AmiDatasourcePlugin.HELP_GENERAL, "redis general help");
	private static final Map<String, String> OPTIONS_MAP = AmiRedisDatasourceAdapter.buildOptions();

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
		return new AmiRedisDatasourceAdapter2();
	}

	@Override
	public String getDatasourceIcon() {
		return "redis.png";
	}

	@Override
	public String getDatasourceQuoteType() {
		//		return "\'";
		return "'"; // <- should be valid not sure if escape is necessary
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

	public Map<String, String> getAvailableOptions() {
		return OPTIONS_MAP;
	}

}
