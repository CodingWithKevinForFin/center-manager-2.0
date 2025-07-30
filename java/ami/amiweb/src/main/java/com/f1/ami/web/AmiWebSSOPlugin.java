package com.f1.ami.web;

import com.f1.ami.amicommon.AmiPlugin;
import com.f1.ami.web.auth.AmiAuthUser;
import com.f1.http.HttpRequestResponse;
import com.f1.suite.web.HttpRequestAction;

/**
 * 1) Set the sso.plugin.class property to point to an implementation of this class <BR>
 * 2) Set the ami.web.index.html.file property the URL that is associated with buildAuthRequest(...) method<BR>
 * 3) getExpectedResponsePath() must return URL associated with processResponse(...) method<BR>
 * 4) When users access the index file (from step 1) buildAuthRequest(..) is called and the user's browser is redirected to the returned URL (usually the IDP)<BR>
 * 5) After the IDP has authenticated, the IDP should then redirect the user's browser to the getExpectedResposnePath() URL (from step 2)<BR>
 * 6) processResponse(...) is called and should return an AmiUathUser that will be passed to the AMI dashboard<BR>
 */
public interface AmiWebSSOPlugin extends AmiPlugin {

	/**
	 * 
	 * Use the ami.web.index.html.file to associate the URL that will cause this method to be invoked. This method should inspect the HTTP request and formulate a fully qualified
	 * URL that will be sent to the IDP.
	 * <P>
	 * Note, this method should return the URL as specified in getExpectedResponsePath() if it's determined,based on the supplied request that the user is already authenticated
	 * 
	 * @param req
	 *            the http request
	 * @return a URL that the user's browser will be redirected to
	 */
	String buildAuthRequest(HttpRequestResponse req) throws Exception;

	/**
	 * @return the URL that the processResponse(...) method is associated with. This method is called once at startup. Hence, return value is really a constant
	 */
	String getExpectedResponsePath();
	/**
	 * @return the path for the logout page
	 */
	String getLogoutRedirectPath();

	/**
	 * 
	 * @param req
	 *            the http request
	 * @return null if not allowed. See com.f1.ami.web.auth.BasicAmiAuthUser for convenience class
	 * @throws Exception
	 *             if there was an error, the user will not be permitted to login
	 */
	AmiAuthUser processResponse(HttpRequestAction req) throws Exception;

	/**
	 * 
	 * @param req
	 *            the http request
	 * @return a Logout URL that the user's browser will be redirected to
	 * @throws Exception
	 */
	String handleLogout(HttpRequestResponse req) throws Exception;

}
