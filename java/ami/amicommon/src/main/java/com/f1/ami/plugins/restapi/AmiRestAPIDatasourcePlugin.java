package com.f1.ami.plugins.restapi;

import java.util.Map;

import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiDatasourcePlugin;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.PropertyController;

public class AmiRestAPIDatasourcePlugin implements AmiDatasourcePlugin {

	public static final String DATASOURCE_TYPE = "RESTAPI";
	private static final String DATASOURCE_DESC = "RestAPI";

	private static final Map<String, Object> OPERATORS_MAP = CH.m(AmiDatasourcePlugin.OPERATOR_KEY_EQUAL_TO, "==", AmiDatasourcePlugin.OPERATOR_KEY_NOT_EQUAL_TO, "!=",
			AmiDatasourcePlugin.OPERATOR_KEY_LESS_THAN, "<", AmiDatasourcePlugin.OPERATOR_KEY_GREATER_THAN, ">", AmiDatasourcePlugin.OPERATOR_KEY_LESS_THAN_OR_EQUAL_TO, "<=",
			AmiDatasourcePlugin.OPERATOR_KEY_GREATER_THAN_OR_EQUAL_TO, ">=");
	private static final Map<String, Object> WHERE_SYNTAX_MAP = CH.m(AmiDatasourcePlugin.WHERE_SYNTAX_PREFIX, "((", AmiDatasourcePlugin.WHERE_SYNTAX_JOIN, ") or (",
			AmiDatasourcePlugin.WHERE_SYNTAX_SUFFIX, "))", AmiDatasourcePlugin.WHERE_SYNTAX_TRUE, "true", AmiDatasourcePlugin.WHERE_SYNTAX_FALSE, "false");

	private static final Map<String, Object> HELP_MAP = CH.m(AmiDatasourcePlugin.HELP_URL, "http://domain/restApi", AmiDatasourcePlugin.HELP_GENERAL, "Rest API general help");

	private static final Map<String, String> OPTIONS_MAP = AmiRestAPIDatasourceAdapter.buildOptions();

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
		return new AmiRestAPIDatasourceAdapter();
	}

	@Override
	public String getDatasourceIcon() {
		return "restapi.svg";
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
