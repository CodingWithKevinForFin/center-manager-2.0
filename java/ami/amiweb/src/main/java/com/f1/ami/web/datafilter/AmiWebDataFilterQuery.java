package com.f1.ami.web.datafilter;

import java.util.Map;

import com.f1.base.Password;

public interface AmiWebDataFilterQuery {

	String getDatasourceName();
	String getDatasourceOverrideUrl();
	String getDatasourceOverrideUsername();
	Password getDatasourceOverridePassword();
	String getDatasourceOverridePasswordEnc();
	String getDatasourceOverrideOptions();
	String getDatasourceOverrideRelay();
	String getDatasourceOverrideAdapter();
	String getQuery();
	boolean hasOverrides();
	Map<String, Object> getUseDirectives();
}
