package com.f1.ami.web.auth;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.base.F1LicenseInfo;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Short;
import com.f1.utils.structs.Tuple2;

public class AmiAuthManager {
	private static final Logger log = Logger.getLogger("USER_LICENSE_MAMAGER");

	public static final AmiAuthManager INSTANCE = new AmiAuthManager();

	private AmiAuthManager() {
		if (F1LicenseInfo.getLicenseInstance().indexOf("=") == -1)
			maxUsers = 1000;
		else {
			Map<String, String> map = SH.splitToMap(',', '=', F1LicenseInfo.getLicenseInstance());
			maxUsers = CH.getOr(Caster_Short.INSTANCE, map, "USERS", (short) 1000);
		}
	}

	private final short maxUsers;
	private HashSet<Tuple2<String, String>> usersCopy = new LinkedHashSet<Tuple2<String, String>>();
	private final HashSet<Tuple2<String, String>> users = new LinkedHashSet<Tuple2<String, String>>();

	public boolean addUser(String host, String username) {
		host = SH.beforeLast(host, ':', host);
		if (host == null || username == null)
			return false;
		Tuple2<String, String> t = new Tuple2<String, String>(host, username);
		t.lock();
		if (usersCopy.contains(t))
			return true;
		int size;
		synchronized (users) {
			if (users.contains(t))
				return true;
			if (users.size() >= maxUsers) {
				LH.warning(log, "USER_COUNT_EXCEEDED ", maxUsers, ": ", username, " (", host, ")");
				return false;
			}
			users.add(t);
			usersCopy = new LinkedHashSet<Tuple2<String, String>>(users);
			size = users.size();
		}

		LH.info(log, "USER ", size, "/", maxUsers, ": ", username, " (", host, ")");

		return true;
	}

	public Set<Tuple2<String, String>> getUsers() {
		return Collections.unmodifiableSet(usersCopy);
	}

	public short getMaxUsers() {
		return this.maxUsers;
	}

	public short getUsersCount() {
		return (short) this.usersCopy.size();
	}

}
