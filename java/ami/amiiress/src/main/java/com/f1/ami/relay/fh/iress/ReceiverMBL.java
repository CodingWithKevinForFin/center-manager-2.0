package com.f1.ami.relay.fh.iress;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.relay.fh.iress.AmiIressFH.Handler;
import com.f1.utils.LH;
import com.feedos.api.core.PolymorphicInstrumentCode;
import com.feedos.api.requests.Constants;
import com.feedos.api.requests.ListOfTagValue;
import com.feedos.api.requests.MBLDeltaRefresh;
import com.feedos.api.requests.MBLLayer;
import com.feedos.api.requests.MBLMaxVisibleDepth;
import com.feedos.api.requests.MBLOverlapRefresh;
import com.feedos.api.requests.MBLSnapshot;
import com.feedos.api.requests.OrderBookSide;
import com.feedos.api.requests.Receiver_Quotation_SubscribeInstrumentsMBL;

public class ReceiverMBL implements Receiver_Quotation_SubscribeInstrumentsMBL {
	private static final Logger Log = LH.get();
	private Handler messagehandler;
	private int depth;
	private static final String BUY = "BUY";
	private static final String SELL = "SELL";
	private static final String TABLE_NAME = "iressMBL";
	HashMap<Integer, ListOfTagValue> referentialData = new HashMap<>();
	HashMap<Integer, MBLSnapshot> snapshotTracker = new HashMap<>();

	public ReceiverMBL(Handler messageHandler, HashMap<Integer, ListOfTagValue> referentialData, int depth) {
		this.messagehandler = messageHandler;
		this.referentialData = referentialData;
		this.depth = depth;
	}

	public void OrderBookSide(OrderBookSide orderBookSide, MBLSnapshot snapshot, MBLLayer layer, String side) {
		if (orderBookSide == null)
			return;
		for (int i = 0; i < orderBookSide.getDepth(); i++) {
			Map<String, Object> m = new HashMap<>();
			m.put("Level", i);
			m.put("Price", orderBookSide.getPrice(i));
			m.put("Qty", orderBookSide.getQty(i));
			m.put("Symbol", referentialData.get(snapshot.getCode()).getTagByNumber(Constants.TAG_Symbol).get_string());
			m.put("Side", side);
			m.put("MarketTimestamp", layer.getTimestamps().getMarket());
			m.put("ServerTimestamp", layer.getTimestamps().getServer());
			messagehandler.sendMessage(TABLE_NAME, null, m, System.currentTimeMillis());
		}
	}

	public void getData(MBLSnapshot s) {
		MBLLayer layer = s.getLayerList().get(0);
		layer.setMaxVisibleDepth(depth);
		if (layer != null) {
			OrderBookSide(layer.getAskLimits(), s, layer, SELL);
			OrderBookSide(layer.getBidLimits(), s, layer, BUY);
		}
	}

	@Override
	public void quotNotifMBLDeltaRefresh(int subscriptionNum, Object user_context, MBLDeltaRefresh delta) {
		MBLSnapshot s = snapshotTracker.get(delta.getCode());
		s.update_with_MBLDeltaRefresh(delta);
		snapshotTracker.put(s.getCode(), s);
		getData(s);
	}

	@Override
	public void quotNotifMBLMaxVisibleDepth(int subscriptionNum, Object user_context, MBLMaxVisibleDepth maxDepth) {
		MBLSnapshot snapshot = snapshotTracker.get(maxDepth.getCode());
		snapshot.update_with_MBLMaxVisibleDepth(maxDepth);
		snapshotTracker.put(snapshot.getCode(), snapshot);
	}

	@Override
	public void quotNotifMBLOverlapRefresh(int arg0, Object arg1, MBLOverlapRefresh overlapRefresh) {
		MBLSnapshot snapshot = snapshotTracker.get(overlapRefresh.getCode());
		snapshot.update_with_MBLOverlapRefresh(overlapRefresh);
		snapshotTracker.put(snapshot.getCode(), snapshot);
		getData(snapshot);
	}

	@Override
	public void quotNotifMBLFullRefresh(int subscription_num, Object user_context, MBLSnapshot[] snapshots) {
		for (MBLSnapshot snapshot : snapshots) {
			if (snapshot.getCode() == 0) {
				continue;
			}
			snapshotTracker.put(snapshot.getCode(), snapshot);
			getData(snapshot);
		}
	}

	@Override
	public void quotSubscribeInstrumentsMBLResponse(int subscription_num, Object user_context, int rc, PolymorphicInstrumentCode[] internalCodes) {
		if (rc == Constants.RC_ERROR)
			LH.warning(Log, "Failed to get MBL response");
		else
			LH.info(Log, "MBL Subscription succesful ");
	}

	@Override
	public void quotSubscribeInstrumentsMBLUnsubNotif(int subscription_num, Object user_context, int arg2) {

	}
}