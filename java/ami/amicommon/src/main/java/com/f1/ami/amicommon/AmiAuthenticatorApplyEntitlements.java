package com.f1.ami.amicommon;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.f1.ami.web.auth.AmiAuthResponse;
import com.f1.ami.web.auth.AmiAuthUser;
import com.f1.ami.web.auth.AmiAuthenticatorFileBacked;
import com.f1.ami.web.auth.AmiAuthenticatorPlugin;
import com.f1.ami.web.auth.BasicAmiAuthResponse;
import com.f1.ami.web.auth.BasicAmiAuthUser;
import com.f1.base.Password;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.CachedFile;
import com.f1.utils.PropertyController;
import com.f1.utils.structs.Tuple2;

public class AmiAuthenticatorApplyEntitlements implements AmiAuthenticatorPlugin {

	final private AmiAuthenticatorPlugin inner;
	final private CachedFile file;
	final private boolean required;
	final private boolean force;
	final private boolean skip;

	public AmiAuthenticatorApplyEntitlements(ContainerTools tools, PropertyController props, AmiAuthenticatorPlugin inner) {
		String mode = tools.getOptionalEnum(AmiCommonProperties.PROPERTY_USERS_ACCESS_FILE_FOR_ENTITLEMENTS, "off", "on", "required", "force", "required_force");
		this.inner = inner;
		if ("off".equals(mode)) {
			force = false;
			required = false;
			skip = true;
		} else if ("on".equals(mode)) {
			force = false;
			required = false;
			skip = false;
		} else if ("force".equals(mode)) {
			force = true;
			required = false;
			skip = false;
		} else if ("required".equals(mode)) {
			force = false;
			required = true;
			skip = false;
		} else if ("required_force".equals(mode)) {
			force = true;
			required = true;
			skip = false;
		} else
			throw new RuntimeException("bad mode: " + mode);
		File path = props.getOptional(AmiCommonProperties.PROPERTY_USERS_ACCESS_FILE, File.class);
		if (path == null)
			path = tools.getOptional(AmiCommonProperties.PROPERTY_USERS_ACCESS_FILE, new File(AmiAuthenticatorFileBacked.DATA_ACCESS_TXT));
		this.file = new CachedFile(path, 1000);
	}

	@Override
	public void init(ContainerTools tools, PropertyController props) {
		inner.init(tools, props);
	}

	@Override
	public String getPluginId() {
		return this.inner.getPluginId();
	}

	@Override
	public AmiAuthResponse authenticate(String namespace, String location, String username, String password) {
		final AmiAuthResponse res = inner.authenticate(namespace, location, username, password);
		if (skip || res == null || res.getStatus() != AmiAuthResponse.STATUS_OKAY)
			return res;
		final AmiAuthUser user = res.getUser();
		final AmiAuthUser user2 = process(user);
		if (user2 == null)
			return new BasicAmiAuthResponse(AmiAuthResponse.STATUS_BAD_USERNAME, "User not found in user access file", user);
		return new BasicAmiAuthResponse(res.getStatus(), res.getMessage(), user2);
	}

	public AmiAuthUser process(AmiAuthUser user) {
		if (skip || user == null)
			return user;
		final Tuple2<Password, Map<String, Object>> fileEntry = AmiAuthenticatorFileBacked.findUser(file, user.getUserName());
		if (fileEntry == null) {
			if (required)
				return null;
		} else if (CH.isntEmpty(fileEntry.getB())) {
			final Map<String, Object> values = new HashMap<String, Object>();
			if (CH.isntEmpty(user.getAuthAttributes()))
				values.putAll(user.getAuthAttributes());
			if (force)
				values.putAll(fileEntry.getB());
			else
				CH.putAllMissing(values, fileEntry.getB());
			return new BasicAmiAuthUser(user.getUserName(), values);
		}
		return user;
	}

}
