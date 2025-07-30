package com.f1.ami.web.amiscript;

import java.util.Map;
import java.util.TreeMap;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.auth.AmiSsoSession;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_SsoSession extends AmiWebScriptBaseMemberMethods<AmiSsoSession> {

	private AmiWebScriptMemberMethods_SsoSession() {
		super();
		addMethod(GET_ACCESS_TOKEN, "accessToken");
		addMethod(GET_PROVIDER, "provider");
		addMethod(GET_PROVIDER_URL, "providerUrl");
		addMethod(GET_PROPERTIES, "properties");
		addMethod(GET_PROPERTY);
	}

	private static final AmiAbstractMemberMethod<AmiSsoSession> GET_ACCESS_TOKEN = new AmiAbstractMemberMethod<AmiSsoSession>(AmiSsoSession.class, "getAccessToken", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiSsoSession targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getAccessToken();
		}

		@Override
		protected String getHelp() {
			return "Returns the Access Token issued by the SSO Provider. See accessToken() for convenience method to get current sso access token.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiSsoSession> GET_PROVIDER = new AmiAbstractMemberMethod<AmiSsoSession>(AmiSsoSession.class, "getProvider", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiSsoSession targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getProvider();
		}

		@Override
		protected String getHelp() {
			return "Returns the name of the provider, likely a company name.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiSsoSession> GET_PROVIDER_URL = new AmiAbstractMemberMethod<AmiSsoSession>(AmiSsoSession.class, "getProviderUrl", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiSsoSession targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getProviderUrl();
		}

		@Override
		protected String getHelp() {
			return "Returns the provider's access base URL.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiSsoSession> GET_PROPERTIES = new AmiAbstractMemberMethod<AmiSsoSession>(AmiSsoSession.class, "getProperties", Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiSsoSession targetObject, Object[] params, DerivedCellCalculator caller) {
			Map<String, Object> t = targetObject.getProperties();
			TreeMap<String, Object> r = new TreeMap<String, Object>();
			if (t != null)
				r.putAll(t);
			return r;
		}

		@Override
		protected String getHelp() {
			return "Returns a map of all custom properties, see getProperty(..) if you looking for a particular property.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiSsoSession> GET_PROPERTY = new AmiAbstractMemberMethod<AmiSsoSession>(AmiSsoSession.class, "getProperty", Object.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiSsoSession targetObject, Object[] params, DerivedCellCalculator caller) {
			Map<String, Object> t = targetObject.getProperties();
			Object key = params[0];
			if (t == null || key == null)
				return null;
			return t.get(key);
		}

		protected String[] buildParamNames() {
			return new String[] { "key" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key of the property to get" };
		}
		@Override
		protected String getHelp() {
			return "Returns a custom property, this will be faster than using getProperties() when looking for just a single property.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public Class<AmiSsoSession> getVarType() {
		return AmiSsoSession.class;
	}

	@Override
	public Class<AmiSsoSession> getVarDefaultImpl() {
		return AmiSsoSession.class;
	}

	@Override
	public String getVarTypeName() {
		return "SsoSession";
	}

	@Override
	public String getVarTypeDescription() {
		return "SSO Session";
	}

	public final static AmiWebScriptMemberMethods_SsoSession INSTANCE = new AmiWebScriptMemberMethods_SsoSession();
}
