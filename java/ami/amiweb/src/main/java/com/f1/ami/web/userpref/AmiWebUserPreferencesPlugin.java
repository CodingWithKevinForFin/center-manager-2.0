package com.f1.ami.web.userpref;

import com.f1.ami.amicommon.AmiPlugin;
import com.f1.ami.web.datafilter.AmiWebDataSession;

public interface AmiWebUserPreferencesPlugin extends AmiPlugin {

	/**
	 * This method gets called once on login for each user.
	 * 
	 * @param session
	 *            - the user's session, which includes variables declared for that user.
	 * @return a filter to control what data the user can see, or null if the user should not have any data controls applied.
	 */
	AmiWebUserPreferencesStorage createUserPreferencesStorage(AmiWebDataSession session);
}
