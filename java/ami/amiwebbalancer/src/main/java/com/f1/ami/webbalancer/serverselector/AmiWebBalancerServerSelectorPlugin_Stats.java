package com.f1.ami.webbalancer.serverselector;

import java.util.Map;

import com.f1.ami.webbalancer.AmiWebBalancerFastHttpRequestResponse;
import com.f1.ami.webbalancer.AmiWebBalancerProperties;
import com.f1.ami.webbalancer.AmiWebBalancerServerInstance;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.ContentType;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public class AmiWebBalancerServerSelectorPlugin_Stats implements AmiWebBalancerServerSelectorPlugin<AmiWebBalancerServerTestUrlResults_Stats> {

	private int maxLogins;

	@Override
	public void init(ContainerTools tools, PropertyController props) {
		this.maxLogins = tools.getOptional(AmiWebBalancerProperties.PROPERTY_AMI_WEBBALANCER_MAX_LOGINS_PER_SERVER, -1);
	}

	@Override
	public String getPluginId() {
		return "WEBBALANCER_SERVERSELECTOR_STATS";
	}

	@Override
	public AmiWebBalancerServerTestUrlResults_Stats processHealthStats(AmiWebBalancerServerInstance serverInstance, AmiWebBalancerFastHttpRequestResponse rr) {
		if (rr.getResponseType().contains(" 200 ")) {
			String find = rr.getResponseHeaders().find("Content-Type");
			if (ContentType.JSON.getMimeType().equals(find)) {
				Map map = (Map) ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject(new String(rr.getResponseData()));
				Map vm = CH.getOrThrow(Map.class, map, "vm");
				Map web = CH.getOrThrow(Map.class, map, "web");
				Map vmraw = CH.getOrThrow(Map.class, map, "vmraw");
				double cpuPct = (double) CH.getOrThrow(Caster_Integer.INSTANCE, vm, "cpuPct");
				int logins = (int) CH.getOrThrow(Caster_Integer.INSTANCE, web, "logins");
				int sessions = (int) CH.getOrThrow(Caster_Integer.INSTANCE, web, "sessions");
				long memMax = (long) CH.getOrThrow(Caster_Long.INSTANCE, vmraw, "memMax");
				long memUsed = (long) CH.getOrThrow(Caster_Long.INSTANCE, vmraw, "memUsed");
				long memUsedAfterGc = (long) CH.getOrThrow(Caster_Long.INSTANCE, vmraw, "memUsedAfterGc");
				long startTime = (long) CH.getOrThrow(Caster_Long.INSTANCE, vmraw, "startTime");
				long threadsRunnable = (long) CH.getOrThrow(Caster_Long.INSTANCE, vmraw, "threadsRunnable");
				long threadsTotal = (long) CH.getOrThrow(Caster_Long.INSTANCE, vmraw, "threadsTotal");
				return new AmiWebBalancerServerTestUrlResults_Stats(serverInstance, cpuPct, logins, sessions, memMax, memUsed, memUsedAfterGc, startTime, threadsRunnable,
						threadsTotal);
			} else
				return new AmiWebBalancerServerTestUrlResults_Stats(serverInstance, true);
		} else {
			return new AmiWebBalancerServerTestUrlResults_Stats(serverInstance, false);
		}
	}

	@Override
	public boolean canAcceptMoreClients(AmiWebBalancerServerTestUrlResults_Stats i) {
		if (!i.isAlive())
			return false;
		if (this.maxLogins == -1)
			return true;
		else
			return i.getLogins() < this.maxLogins;
	}

	@Override
	public int compare(AmiWebBalancerServerTestUrlResults_Stats leftTestResults, AmiWebBalancerServerTestUrlResults_Stats rightTestResults, double leftWeighting,
			double rightWeighting) {
		final double left = leftTestResults.getLogins() / leftWeighting;
		final double right = rightTestResults.getLogins() / rightWeighting;
		if (left < right)
			return 1;
		else if (right > left)
			return -1;
		return OH.compare(leftWeighting, rightWeighting);
	}

}
