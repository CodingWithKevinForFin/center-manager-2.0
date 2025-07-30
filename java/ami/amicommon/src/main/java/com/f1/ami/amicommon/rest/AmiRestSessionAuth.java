package com.f1.ami.amicommon.rest;

import com.f1.ami.web.auth.AmiAuthUser;
import com.f1.http.HttpRequestResponse;

public interface AmiRestSessionAuth {

	AmiAuthUser getUser(HttpRequestResponse req);

}
