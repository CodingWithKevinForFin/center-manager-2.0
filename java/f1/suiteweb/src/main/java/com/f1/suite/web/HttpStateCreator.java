package com.f1.suite.web;

import com.f1.container.Partition;
import com.f1.http.HttpRequestResponse;

public interface HttpStateCreator {
	public WebState createState(HttpRequestResponse req, Partition partition, WebStatesManager wsm, String pgid);

	public int getAcquireLockTimeoutSeconds();

	public String nextPgId();
}
