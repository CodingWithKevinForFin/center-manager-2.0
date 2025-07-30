package com.f1.ami.web.auth;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.f1.base.Password;
import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;
import com.f1.utils.structs.Tuple3;

public class CachingAuthenticator implements AmiAuthenticatorPlugin {

	final private ConcurrentMap<Tuple3<String, String, String>, Tuple3<Long, Password, AmiAuthResponse>> authenticated = new ConcurrentHashMap<Tuple3<String, String, String>, Tuple3<Long, Password, AmiAuthResponse>>();
	final private AmiAuthenticatorPlugin inner;
	final private long cacheMillis;

	public CachingAuthenticator(AmiAuthenticatorPlugin inner, long cacheMillis) {
		this.cacheMillis = cacheMillis;
		this.inner = inner;
	}

	@Override
	public void init(ContainerTools tools, PropertyController props) {
		inner.init(tools, props);
	}

	@Override
	public String getPluginId() {
		return inner.getPluginId();
	}

	@Override
	public AmiAuthResponse authenticate(String namespace, String location, String user, String password) {
		if (cacheMillis <= 0)
			return inner.authenticate(namespace, location, user, password);
		final long now = System.currentTimeMillis();
		final Password pass = new Password(password);
		final Tuple3<String, String, String> key = new Tuple3<String, String, String>(namespace, location, user);
		final Tuple3<Long, Password, AmiAuthResponse> existing = authenticated.get(key);
		if (existing != null) {
			if (now < existing.getA() + cacheMillis && existing.getB().matches(pass))
				return existing.getC();
			else
				authenticated.remove(key);
		}
		final AmiAuthResponse r = inner.authenticate(namespace, location, user, password);
		if (r != null && r.getStatus() == AmiAuthResponse.STATUS_OKAY)
			authenticated.put(key, new Tuple3<Long, Password, AmiAuthResponse>(now, pass, r));
		return r;
	}

}
