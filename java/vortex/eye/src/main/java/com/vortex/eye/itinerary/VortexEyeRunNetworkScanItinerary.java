package com.vortex.eye.itinerary;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.povo.standard.RunnableRequestMessage;
import com.f1.povo.standard.TextMessage;
import com.f1.utils.AH;
import com.f1.utils.IOH;
import com.f1.utils.IpAndPortScanner;
import com.f1.utils.IpAndPortScanner.Result;
import com.f1.utils.MH;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.VortexEyeNetworkScan;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunNetworkScanRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunNetworkScanResponse;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;

public class VortexEyeRunNetworkScanItinerary extends AbstractVortexEyeItinerary<VortexEyeRunNetworkScanRequest> {

	private static final int THREAD_COUNT = 25;
	private static final int PORT_TIMEOUT = 250;
	private static final int TIMEOUT_MS = 30000;
	private static final int MAX_BATCH_SIZE = THREAD_COUNT * 3;
	private VortexEyeRunNetworkScanResponse r;
	private IpAndPortScanner currentScanner;
	private int currentPosition;
	private int ipsToCheck[];
	private List<Result> results = new ArrayList<Result>();

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		this.r = getState().nw(VortexEyeRunNetworkScanResponse.class);
		VortexEyeRunNetworkScanRequest req = this.getInitialRequest().getAction();
		this.ipsToCheck = req.getIp4s();
		//int range = 1 + req.getEndIp4() - req.getStartIp4();
		int maxRange = 256 * 256;
		if (ipsToCheck.length > maxRange) {
			r.setMessage("Port range too large, max is " + maxRange + " IP addresses. Attemping: " + currentPosition + " ips");
			return STATUS_COMPLETE;
		}
		currentPosition = 0;

		requestScan(worker);
		return STATUS_ACTIVE;
	}
	private boolean requestScan(VortexEyeItineraryWorker worker) {
		final VortexEyeRunNetworkScanRequest req = this.getInitialRequest().getAction();
		final int start = this.currentPosition;
		final int end = Math.min(this.currentPosition + MAX_BATCH_SIZE, this.ipsToCheck.length - 1);
		if (start > end)
			return false;
		final Executor ex = getState().getPartition().getContainer().getThreadPoolController();
		currentScanner = new IpAndPortScanner(AH.subarray(ipsToCheck, start, end - start + 1), req.getPortsToScan(), THREAD_COUNT, MH.clip(req.getTimeoutMs(), PORT_TIMEOUT, 5000),
				ex);
		this.currentPosition = end + 1;
		final RunnableRequestMessage rm = getState().nw(RunnableRequestMessage.class);
		rm.setTimeoutMs(TIMEOUT_MS);
		rm.setPartitionId("ScanNetwork");
		rm.setRunnable(currentScanner);
		worker.sendRunnable(this, rm);
		return true;
	}

	@Override
	public byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker) {
		TextMessage txt = (TextMessage) result.getAction();
		if (txt.getText() != null) {
			r.setMessage(txt.getText());
			return STATUS_COMPLETE;
		} else {
			results.addAll(this.currentScanner.getResults());
			final VortexEyeRunNetworkScanRequest req = this.getInitialRequest().getAction();
			double progress = ((double) currentPosition) / this.ipsToCheck.length;
			if (requestScan(worker)) {
				ResultMessage<VortexEyeRunNetworkScanResponse> r2 = getState().nw(ResultMessage.class);
				r2.setAction(getState().nw(VortexEyeRunNetworkScanResponse.class));
				r2.getAction().setOk(true);
				r2.getAction().setProgress(progress);
				r2.getAction().setMessage(results.size() + " address(es) found");
				r2.setIsIntermediateResult(true);
				getTools().getContainer().getDispatchController().reply(null, getInitialRequest(), r2, null);
				return STATUS_ACTIVE;
			} else {
				return STATUS_COMPLETE;
			}
		}
	}
	@Override
	public Message endJourney(VortexEyeItineraryWorker worker) {
		r.setOk(true);
		List<VortexEyeNetworkScan> results2 = new ArrayList<VortexEyeNetworkScan>(results.size());
		for (Result i : results) {
			VortexEyeNetworkScan result = getState().nw(VortexEyeNetworkScan.class);
			result.setHostname(i.getHostname());
			result.setIp4(i.getIp());
			result.setPortsFound(i.getPortsFound());
			result.setPingable(i.isReachable());
			results2.add(result);
		}
		r.setResults(results2);
		return r;
	}

	@Override
	protected void populateAuditEvent(VortexEyeRunNetworkScanRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		sink.setEventType(VortexEyeClientEvent.TYPE_NETWORK_SCAN);
		int min = MH.mini(action.getIp4s());
		int max = MH.maxi(action.getIp4s());
		sink.getParams().put("TOP_IP", IOH.formatIp(IOH.intToIp4(min)));
		sink.getParams().put("BOT_IP", IOH.formatIp(IOH.intToIp4(max)));
		sink.getParams().put("COUNT", SH.toString(action.getIp4s().length));
		auditList(sink, "PORTS", action.getPortsToScan());
	}

}
