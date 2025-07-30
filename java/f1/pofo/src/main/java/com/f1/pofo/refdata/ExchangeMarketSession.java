package com.f1.pofo.refdata;

import com.f1.base.DayTime;
import com.f1.base.Message;
import com.f1.base.VID;

/**
 * represents a market (pre, regular, post) for a particular exchange segment
 * 
 * @author rcookekkw
 */
@VID("F1.RD.EM")
public interface ExchangeMarketSession extends Message {

	/**
	 * @return * the type of martket
	 */
	public MarketSessionType getType();
	public void setType(MarketSessionType type);

	/**
	 * @return when the market opens (timezone + time of day)
	 */
	public DayTime getMarketOpen();
	public void setMarketOpen(DayTime time);

	/**
	 * @return when the market closes (timezone + time of day)
	 */
	public DayTime getMarketClose();
	public void setMarketClose(DayTime time);

}
