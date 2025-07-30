package com.f1.pofo.refdata;

import java.util.Map;

import com.f1.base.Message;
import com.f1.base.VID;

@VID("F1.RD.ES")
public interface ExchangeSegment extends Message {

	/**
	 * @return a collection of sessions and there types.
	 */
	public Map<MarketSessionType, ExchangeMarketSession> getMarketSessions();

	public void setMarketSessions(Map<MarketSessionType, ExchangeMarketSession> session);

	/**
	 * @return the id uniquely representing this segment
	 */
	public String getSegmentId();

	public void setSegmentId(String segmentId);
}
