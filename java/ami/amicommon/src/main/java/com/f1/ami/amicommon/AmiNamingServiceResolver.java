package com.f1.ami.amicommon;

import java.net.UnknownHostException;

/**
 * 
 * 
 * allows the system to delegate service/hosts lookups by name... There can be many {@link AmiNamingServiceResolver}s but they will be "chained" and visited in the order they were
 * specified in the properties.
 */
public interface AmiNamingServiceResolver extends AmiPlugin {

	/**
	 * allows the system to delegate service/hosts lookups by name...i.e. in an enterprise system one may want to refer to a service by name vs. static host:port
	 * 
	 * @param locator
	 *            the user provided location of where the service is logically located
	 * @return null if this resolver can not resolve the given locator
	 * @throws UnknownHostException
	 *             if the locator is recognized for this resolver but is somehow invalid, remember that their may be multiple resolvers so just because your instance couldn't
	 *             resolve, others might (in which case return null). Only throw this exception when you <i>should</i> be able to resolve but can't for some external reason
	 */
	public AmiServiceLocator resolve(AmiServiceLocator locator) throws UnknownHostException;

	/**
	 * Return true if this resolver can resolver the locator. Note, This should return very quickly, as it operates in the main thread. If this returns true, then the
	 * {@link #resolve(AmiServiceLocator)} method is called (in an out-of-band thread)
	 * 
	 * @param locator
	 * @return true if the locator may need resolving.
	 * @throws UnknownHostException
	 */
	public boolean canResolve(AmiServiceLocator locator) throws UnknownHostException;

	/**
	 * 
	 * @return unique identifying name for this naming service resolver
	 */
	public String getPluginId();

}
