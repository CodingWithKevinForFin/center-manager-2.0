package com.f1.ami.webbalancer.serverselector;

import com.f1.ami.amicommon.AmiPlugin;
import com.f1.ami.webbalancer.AmiWebBalancerFastHttpRequestResponse;
import com.f1.ami.webbalancer.AmiWebBalancerServerInstance;

public interface AmiWebBalancerServerSelectorPlugin<T extends AmiWebBalancerServerTestUrlResults> extends AmiPlugin {
	public T processHealthStats(AmiWebBalancerServerInstance amiWebBalancerServerInstance, AmiWebBalancerFastHttpRequestResponse rr);
	public boolean canAcceptMoreClients(T i);

	//If left is preferred to right, return 1.  If right is preferred to left return -1, else return 0;
	public int compare(T leftTestResults, T rightTestResults, double leftWeighting, double rightWeighting);
}