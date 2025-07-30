package com.f1.ami.plugins.grpc;

import java.util.Map;

import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiDatasourcePlugin;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.PropertyController;

public class AmiGRPCDatasourcePlugin implements AmiDatasourcePlugin {

	private static final String DATASOURCE_TYPE = "gRPC";
	private static final String DATASOURCE_DESC = "gRPC Datasource Adapter";
	private static final Map<String, Object> OPERATORS_MAP = CH.emptyMap(String.class, Object.class);
	private static final Map<String, Object> WHERE_SYNTAX_MAP = CH.emptyMap(String.class, Object.class);;
	private static final Map<String, Object> HELP_MAP = CH.m(AmiDatasourcePlugin.HELP_URL, "server_address:port_number", AmiDatasourcePlugin.HELP_GENERAL, "ignite general help");

	private static final Map<String, String> OPTIONS_MAP = AmiGRPCDatasourceAdapter.buildOptions();

	@Override
	public void init(ContainerTools tools, PropertyController props) {
		
	}

	@Override
	public String getPluginId() {
		return DATASOURCE_TYPE;
	}

	@Override
	public AmiDatasourceAdapter createDatasourceAdapter() {
		return new AmiGRPCDatasourceAdapter();
	}

	@Override
	public String getDatasourceDescription() {
		return DATASOURCE_DESC;
	}

	@Override
	public String getDatasourceIcon() {
		return "grpc.svg";
	}

	@Override
	public String getDatasourceQuoteType() {
		return "'";
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
