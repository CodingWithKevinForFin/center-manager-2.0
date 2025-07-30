package com.f1.ami.web.auth;

import java.util.Map;

/**
 * 
 * See {@link BasicAmiAuthUser} for a default implementation
 */
public interface AmiAuthUser {

	//true or false
	public static final String PROPERTY_ISADMIN = "ISADMIN";

	//true or false
	public static final String PROPERTY_ISDEV = "ISDEV";

	//number of concurrent sessions, does not include headless sessions
	public static final String PROPERTY_MAXSESSIONS = "MAXSESSIONS";

	//Comma delimited list of SOURCE:PATTERN 
	//    'SOURCE' is either: CLOUD,LOCAL,SHARED,ABSOLUTE,*
	//    'PATTERN' is a text pattern
	//    For backwards compatiblitiy, if source is not included, then defaults to CLOUD
	//
	//    Example:  CLOUD:layout1.ami,LOCAL:*.ami
	//    Example:  *:* 
	public static final String PROPERTY_LAYOUTS = "LAYOUTS";

	//Format is SOURCE:NAME
	//     for backwards compatibility, if source is not included then defaults to CLOUD
	public static final String PROPERTY_DEFAULT_LAYOUT = "DEFAULT_LAYOUT";//String

	public static final String PROPERTY_SSO_SESSION = "SSO_SESSION";//Instanceof AmiSsoSession

	/**
	 * @return the username (as supplied in login) of this user
	 */
	public String getUserName();
	/**
	 * @return custom set of attributes associated with this user's profile
	 */
	public Map<String, Object> getAuthAttributes();
	//	public Collection<AmiAuthAttribute> getAuthAttributes();
}
