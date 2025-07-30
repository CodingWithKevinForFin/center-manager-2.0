package com.f1.ami.amicommon;

import java.util.LinkedHashMap;
import java.util.Map;

import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amiscript.AmiCalcFrameStack;
import com.f1.base.Password;
import com.f1.container.ContainerTools;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.FlowControlThrow;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public abstract class AmiFlowControlPauseSql extends AmiFlowControlPause {

	protected FlowControlThrow error;
	final private String datasourceName;
	final private String datasourceUrl;
	final private String datasourceUsername;
	final private Password datasourcePassword;
	final private String datasourcePasswordEnc;
	final private String datasourceOptions;
	final private String datasourceRelay;
	final private String datasourceAdapter;
	final private boolean allowSqlInjection;
	final private int limit;
	final private int timeout;
	private LinkedHashMap<String, Object> directives;
	final private byte permissions;

	public AmiFlowControlPauseSql(DerivedCellCalculator position, Map<String, DerivedCellCalculator> use, CalcFrameStack sf) {
		super(position);
		String datasourceName = null;
		String datasourceUrl = null;
		String datasourceUsername = null;
		Password datasourcePassword = null;
		String datasourcePasswordEnc = null;
		String datasourceOptions = null;
		String datasourceRelay = null;
		String datasourceAdapter = null;
		int limit = AmiConsts.DEFAULT;
		int timeout = AmiConsts.DEFAULT;
		byte permissions = AmiCenterQueryDsRequest.PERMISSIONS_FULL;
		boolean allowSqlInjection = false;
		if (use != null) {
			for (String s : use.keySet()) {
				DerivedCellCalculator value = use.get(s);
				Object sval = value.get(sf);
				if (s.startsWith("_")) {
					if (this.directives == null)
						this.directives = new LinkedHashMap<String, Object>();
					this.directives.put(s.substring(1), sval);
				} else {
					String varname = sval.toString();
					s = s.toUpperCase();
					if ("DATASOURCE".equals(s) || "DS".equals(s))
						datasourceName = varname;
					else if ("LIMIT".equals(s)) {
						limit = OH.noNull(Caster_Integer.INSTANCE.cast(sval), AmiConsts.DEFAULT);
					} else if ("TIMEOUT".equals(s))
						timeout = OH.noNull(Caster_Integer.INSTANCE.cast(sval), AmiConsts.DEFAULT);
					else if ("DS_URL".equals(s))
						datasourceUrl = varname;
					else if ("DS_USERNAME".equals(s))
						datasourceUsername = varname;
					else if ("DS_PASSWORD".equals(s))
						datasourcePassword = AmiUtils.castOptionStrict(Password.class, sval, value.getPosition(), s);
					else if ("DS_PASSWORD_ENC".equals(s))
						datasourcePasswordEnc = varname;
					else if ("DS_OPTIONS".equals(s))
						datasourceOptions = varname;
					else if ("DS_RELAY".equals(s))
						datasourceRelay = varname;
					else if ("DS_ADAPTER".equals(s))
						datasourceAdapter = varname;
					else if ("STRING_TEMPLATE".equals(s))
						allowSqlInjection = "on".equalsIgnoreCase(varname);
					else if ("PERMISSIONS".equals(s))
						permissions = AmiUtils.parseDbPermissions(varname);
					else
						throw new ExpressionParserException(value.getPosition() - s.length(), "Unknown Option: '" + s + "'");
				}
			}
		}
		if (datasourceAdapter != null && datasourceName != null)
			throw new ExpressionParserException(use.get("DS_ADAPTER").getPosition(), "DS_ADAPTER and DS are mutually exclusive");
		if (datasourcePasswordEnc != null && datasourcePassword != null)
			throw new ExpressionParserException(use.get("DS_PASSWORD").getPosition(), "DS_PASSWORD and DS_PASSWORD_ENC are mutually exclusive");
		AmiCalcFrameStack ei = AmiUtils.getExecuteInstance2(sf);
		if (ei != null) {
			if (datasourceAdapter == null && datasourceName == null)
				datasourceName = ei.getDefaultDatasource();
		}
		if (limit == AmiConsts.DEFAULT)
			limit = sf.getLimit();
		TimeoutController tc = sf.getTimeoutController();
		int remaining = AmiUtils.toTimeout(timeout, tc == null ? 60000 : tc.throwIfTimedout());
		this.datasourceName = datasourceName;
		this.datasourceUrl = datasourceUrl;
		this.datasourceUsername = datasourceUsername;
		this.datasourcePassword = datasourcePassword;
		this.datasourcePasswordEnc = datasourcePasswordEnc;
		this.datasourceOptions = datasourceOptions;
		this.datasourceRelay = datasourceRelay;
		this.datasourceAdapter = datasourceAdapter;
		this.allowSqlInjection = allowSqlInjection;
		this.permissions = permissions;
		this.limit = limit;
		this.timeout = remaining;
	}

	public AmiCenterQueryDsRequest createRequest(ContainerTools tools) {
		AmiCenterQueryDsRequest r = tools.nw(AmiCenterQueryDsRequest.class);
		r.setLimit(this.limit);
		r.setTimeoutMs(this.timeout);
		r.setDatasourceName(datasourceName);
		r.setDatasourceOverrideUrl(this.datasourceUrl);
		r.setDatasourceOverrideUsername(this.datasourceUsername);
		r.setDatasourceOverridePassword(this.datasourcePassword);
		r.setDatasourceOverridePasswordEnc(this.datasourcePasswordEnc);
		r.setDatasourceOverrideOptions(this.datasourceOptions);
		r.setDatasourceOverrideRelay(this.datasourceRelay);
		r.setDatasourceOverrideAdapter(this.datasourceAdapter);
		r.setAllowSqlInjection(this.allowSqlInjection);
		r.setPermissions((byte) (this.permissions & permissions));//take the reduced set of permissions
		r.setDirectives(this.directives);
		return r;
	}

	public String getDatasourceName() {
		return this.datasourceName;
	}

}
