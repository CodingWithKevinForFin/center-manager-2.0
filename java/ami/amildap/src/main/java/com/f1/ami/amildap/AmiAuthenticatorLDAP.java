package com.f1.ami.amildap;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.web.auth.AmiAuthResponse;
import com.f1.ami.web.auth.AmiAuthenticatorPlugin;
import com.f1.ami.web.auth.BasicAmiAuthResponse;
import com.f1.container.ContainerTools;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;

public class AmiAuthenticatorLDAP implements AmiAuthenticatorPlugin {
	private static final String LDAP_ATTRIBUTE_PROP = "ldap.attributes";
	private static final Logger log = LH.get();
	private List<String> attrs;
	private final OpenLDAPHandler handler = new OpenLDAPHandler();

	@Override
	public void init(ContainerTools tools, PropertyController props) {
		try {
			handler.initConnect(tools);
		} catch (Exception e) {
			LH.warning(log, "Connection not established: ", e);
		}
		String attrStr = tools.getOptional(LDAP_ATTRIBUTE_PROP);
		if (SH.is(attrStr))
			this.attrs = SH.splitToList(",", attrStr);
		else
			this.attrs = Collections.emptyList();
	}

	@Override
	public AmiAuthResponse authenticate(String namespace, String location, String user, String password) {
		final LDAPConnectionPool adminPool = handler.getAdminPool();
		final LDAPConnectionPool userPool = handler.getUserPool();
		LDAPConnection connection = null;
		try {
			connection = adminPool.getConnection();
		} catch (LDAPException le) {
			ResultCode resultCode = le.getResultCode();
			LH.warning(log, "Error grabbing connection from pool: ", le);
			LH.warning(log, "Result code: ", resultCode.intValue(), " (", resultCode.getName(), ")");
			return new BasicAmiAuthResponse.GeneralError();
		}

		String requestDN = handler.getUserAttr(connection, user, "dn");
		if (SH.isEmpty(requestDN)) {
			adminPool.releaseDefunctConnection(connection);
			return new BasicAmiAuthResponse.BadUsername();
		}

		LDAPConnection userConnection = null;
		try {
			userConnection = userPool.getConnection();
			userConnection.bind(requestDN, password);
			final Map<String, Object> attributes = new HashMap<String, Object>();
			Filter filter = Filter.createPresenceFilter("objectClass");
			SearchRequest searchReq = new SearchRequest(requestDN, SearchScope.BASE, filter);
			searchReq.setAttributes(this.attrs.isEmpty() ? null : this.attrs);
			SearchResult searchRes = userConnection.search(searchReq);
			List<SearchResultEntry> searchEntries = searchRes.getSearchEntries();
			if (!this.attrs.isEmpty())
				for (SearchResultEntry entry : searchEntries)
					for (String attr : this.attrs)
						attributes.put("amiscript.variable." + attr, entry.getAttributeValue(attr));

			return new BasicAmiAuthResponse.Okay(user, attributes);
		} catch (LDAPException le) {
			ResultCode resultCode = le.getResultCode();
			LH.warning(log, "Error fulfilling bind request: ", le.getMessage());
			LH.warning(log, "Result code: ", resultCode.intValue(), " (", resultCode.getName(), ")");
			if (SH.is(le.getMatchedDN()))
				LH.info(log, "Matched DN: ", le.getMatchedDN());

			if (le.getReferralURLs() != null)
				for (final String url : le.getReferralURLs())
					LH.info(log, "Referral URL: ", url);

			return new BasicAmiAuthResponse.BadPassword();
		} finally {
			if (userConnection != null)
				userPool.releaseDefunctConnection(userConnection);
			adminPool.releaseConnection(connection);
		}
	}

	@Override
	public String getPluginId() {
		return "AmiAuthenticatorLDAP";
	}
}
