package com.f1.ami.plugins.mongo;

import java.util.Map;

import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiDatasourcePlugin;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.PropertyController;

public class AmiMongoDatasourcePlugin implements AmiDatasourcePlugin {

	private static final Map<String, Object> OPERATORS_MAP = CH.m(AmiDatasourcePlugin.OPERATOR_KEY_EQUAL_TO, "$eq", AmiDatasourcePlugin.OPERATOR_KEY_NOT_EQUAL_TO, "$ne",
			AmiDatasourcePlugin.OPERATOR_KEY_LESS_THAN, "$lt", AmiDatasourcePlugin.OPERATOR_KEY_GREATER_THAN, "$gt", AmiDatasourcePlugin.OPERATOR_KEY_LESS_THAN_OR_EQUAL_TO, "$lte",
			AmiDatasourcePlugin.OPERATOR_KEY_GREATER_THAN_OR_EQUAL_TO, "$gte");
	private static final Map<String, Object> WHERE_SYNTAX_MAP = CH.m(AmiDatasourcePlugin.WHERE_SYNTAX_PREFIX, "((", AmiDatasourcePlugin.WHERE_SYNTAX_JOIN, ") or (",
			AmiDatasourcePlugin.WHERE_SYNTAX_SUFFIX, "))", AmiDatasourcePlugin.WHERE_SYNTAX_TRUE, "true", AmiDatasourcePlugin.WHERE_SYNTAX_FALSE, "false");
	private static final Map<String, Object> HELP_MAP = CH.m(AmiDatasourcePlugin.HELP_URL, "server_address:port_number/database_name", AmiDatasourcePlugin.HELP_GENERAL,
			"mongo general help");

	private static final Map<String, String> OPTIONS_MAP = AmiMongoDatasourceAdapter.buildOptions();

	@Override
	public void init(ContainerTools tools, PropertyController props) {
	}

	@Override
	public String getPluginId() {
		return "MONGODB";
	}

	@Override
	public String getDatasourceDescription() {
		return "MongoDB";
	}

	@Override
	public AmiDatasourceAdapter createDatasourceAdapter() {
		return new AmiMongoDatasourceAdapter();
	}

	@Override
	public String getDatasourceIcon() {
		return "mongodb.svg";
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
