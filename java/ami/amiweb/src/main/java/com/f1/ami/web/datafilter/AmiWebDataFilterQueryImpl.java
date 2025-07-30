package com.f1.ami.web.datafilter;

import java.util.Collections;
import java.util.Map;

import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.base.Password;
import com.f1.utils.OH;

public class AmiWebDataFilterQueryImpl implements AmiWebDataFilterQuery {

	final private String datasourceName;
	final private Map<String, Object> userDirectives;
	final private String query;
	final private String datasourceOverrideUrl;
	final private String datasourceOverrideUsername;
	final private Password datasourceOverridePassword;
	final private String datasourceOverridePasswordEnc;
	final private String datasourceOverrideOptions;
	final private String datasourceOverrideRelay;
	final private String datasourceOverrideAdapter;

	public AmiWebDataFilterQueryImpl(AmiCenterQueryDsRequest request) {
		this.datasourceName = request.getDatasourceName();
		this.datasourceOverrideUrl = request.getDatasourceOverrideUrl();
		this.datasourceOverrideUsername = request.getDatasourceOverrideUsername();
		this.datasourceOverridePassword = request.getDatasourceOverridePassword();
		this.datasourceOverridePasswordEnc = request.getDatasourceOverridePasswordEnc();
		this.datasourceOverrideOptions = request.getDatasourceOverrideOptions();
		this.datasourceOverrideRelay = request.getDatasourceOverrideRelay();
		this.datasourceOverrideAdapter = request.getDatasourceOverrideAdapter();
		this.userDirectives = request.getDirectives() == null ? Collections.EMPTY_MAP : Collections.unmodifiableMap(request.getDirectives());
		this.query = request.getQuery();
	}

	public AmiWebDataFilterQueryImpl(String datasourceName, Map<String, Object> userDirectives, String query) {
		this.datasourceName = datasourceName;
		this.userDirectives = userDirectives == null ? Collections.EMPTY_MAP : Collections.unmodifiableMap(userDirectives);
		this.query = query;
		datasourceOverrideUrl = null;
		datasourceOverrideUsername = null;
		datasourceOverridePassword = null;
		datasourceOverridePasswordEnc = null;
		datasourceOverrideOptions = null;
		datasourceOverrideRelay = null;
		datasourceOverrideAdapter = null;
	}

	public AmiWebDataFilterQueryImpl(String datasourceName, Map<String, Object> userDirectives, String query, String datasourceOverrideUrl, String datasourceOverrideUsername,
			Password datasourceOverridePassword, String datasourceOverridePasswordEnc, String datasourceOverrideOptions, String datasourceOverrideRelay,
			String datasourceOverrideAdapter) {
		super();
		this.datasourceName = datasourceName;
		this.userDirectives = userDirectives;
		this.query = query;
		this.datasourceOverrideUrl = datasourceOverrideUrl;
		this.datasourceOverrideUsername = datasourceOverrideUsername;
		this.datasourceOverridePassword = datasourceOverridePassword;
		this.datasourceOverridePasswordEnc = datasourceOverridePasswordEnc;
		this.datasourceOverrideOptions = datasourceOverrideOptions;
		this.datasourceOverrideRelay = datasourceOverrideRelay;
		this.datasourceOverrideAdapter = datasourceOverrideAdapter;
	}

	@Override
	public String getDatasourceName() {
		return this.datasourceName;
	}

	@Override
	public String getQuery() {
		return this.query;
	}

	@Override
	public Map<String, Object> getUseDirectives() {
		return this.userDirectives;
	}

	@Override
	public String toString() {
		return "[datasourceName=" + datasourceName + ", userDirectives=" + userDirectives + ", query=" + query + "]";
	}

	@Override
	public int hashCode() {
		return OH.hashCode(datasourceName, query, userDirectives);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj == null || getClass() != obj.getClass())
			return false;
		final AmiWebDataFilterQueryImpl other = (AmiWebDataFilterQueryImpl) obj;
		return OH.eq(datasourceName, other.datasourceName) || OH.eq(query, other.query) || OH.eq(userDirectives, other.userDirectives);
	}
	@Override
	public String getDatasourceOverrideUrl() {
		return this.datasourceOverrideUrl;
	}
	@Override
	public String getDatasourceOverrideUsername() {
		return this.datasourceOverrideUsername;
	}
	@Override
	public Password getDatasourceOverridePassword() {
		return this.datasourceOverridePassword;
	}
	@Override
	public String getDatasourceOverridePasswordEnc() {
		return this.datasourceOverridePasswordEnc;
	}
	@Override
	public String getDatasourceOverrideOptions() {
		return this.datasourceOverrideOptions;
	}
	@Override
	public String getDatasourceOverrideRelay() {
		return this.datasourceOverrideRelay;
	}
	@Override
	public String getDatasourceOverrideAdapter() {
		return this.datasourceOverrideAdapter;
	}
	@Override
	public boolean hasOverrides() {
		return datasourceOverrideUrl != null || datasourceOverrideUsername != null || datasourceOverridePassword != null || datasourceOverridePasswordEnc != null
				|| datasourceOverrideOptions != null || datasourceOverrideRelay != null || datasourceOverrideAdapter != null;
	}

}
