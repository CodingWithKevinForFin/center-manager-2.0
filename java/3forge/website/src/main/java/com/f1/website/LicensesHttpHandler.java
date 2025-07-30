package com.f1.website;

import java.util.concurrent.TimeUnit;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.utils.Formatter;
import com.f1.utils.LocaleFormatter;

public class LicensesHttpHandler extends AbstractSecureHttpHandler {

	@Override
	protected void service(HttpRequestResponse request, TfWebsiteManager config, WebsiteUser user) throws Exception {
		WebsiteUtils.populateLicenses(config, user);
		Formatter formatter = user.getFormatter().getDateFormatter(LocaleFormatter.DATE);
		HttpSession session = request.getSession(false);
		long now = System.currentTimeMillis();
		long expires = now + (user.getLicenseDaysLength() * TimeUnit.DAYS.toMillis(1));
		session.getAttributes().put("licenseCreatedOn", formatter.format(now));
		session.getAttributes().put("licenseExpiresOn", formatter.format(expires));
		request.forward("secure_licenses.htm");
	}

}
