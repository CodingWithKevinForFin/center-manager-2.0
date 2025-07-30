package com.f1.pofo.mktdata;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.Transient;
import com.f1.base.VID;
import com.f1.pofo.refdata.Security;

@VID("F1.MK.1Q")
public interface LevelOneData extends PartialMessage {

	/**
	 * the subscription failed
	 */
	static byte STATUS_REJECTED = 1;

	/**
	 * this is a level one event (quote or trade).
	 */
	static byte STATUS_L1EVENT = 2;

	/**
	 * this is an open trade
	 */
	static byte STATUS_OPENED = 3;

	/**
	 * the security has been halted
	 */
	static byte STATUS_HALTED = 4;

	/**
	 * 
	 * @return pointer to the security(optional)
	 */
	@Transient
	@PID(1)
	public Security getSecurity();

	public void setSecurity(Security security);

	/**
	 * @return the securities id. See {@link Security#getSecurityId()}
	 */
	@PID(2)
	public int getSecurityRefId();

	public void setSecurityRefId(int securityRefId);

	/**
	 * @return currency of the quote or trade
	 */
	@PID(3)
	public String getCurrency();

	public void setCurrency(String currency);

	/**
	 * @return ask price
	 */
	@PID(4)
	public double getAskPrice();

	public void setAskPrice(double askPrice);

	/**
	 * @return ask size of shares
	 */
	@PID(5)
	public int getAskSize();

	public void setAskSize(int askSize);

	/**
	 * @return the market maker id of the ask
	 */
	@PID(6)
	public String getAskMarketMakerId();

	public void setAskMarketMakerId(String askMarketMakerId);

	/**
	 * @return the time of the ask in millis
	 */
	@PID(7)
	public long getAskTime();

	public void setAskTime(long askSize);

	/**
	 * @return bid price
	 */
	@PID(8)
	public double getBidPrice();

	public void setBidPrice(double bidPrice);

	/**
	 * @return bid size of shares
	 */
	@PID(9)
	public int getBidSize();

	public void setBidSize(int bidSize);

	/**
	 * @return the market maker id of the bid
	 */
	@PID(10)
	public String getBidMarketMakerId();

	public void setBidMarketMakerId(String bidMarketMakerId);

	/**
	 * @return the time of the bid in millis
	 */
	@PID(11)
	public long getBidTime();

	public void setBidTime(long askSize);

	/**
	 * @return trade price (if this was a trade)
	 */
	@PID(12)
	public double getLastPrice();

	public void setLastPrice(double lastPrice);

	/**
	 * @return number of shares executed (if this was a trade)
	 */
	@PID(13)
	public int getLastSize();

	public void setLastSize(int lastSize);

	/**
	 * @return time of execution (if this was a trade)
	 */
	@PID(14)
	public long getLastTime();

	public void setLastTime(long askSize);

	/**
	 * @return market maker id for the execution (if this was a trade)
	 */
	@PID(15)
	public String getLastMarketMakerId();

	public void setLastMarketMakerId(String bidMarketMakerId);

	/**
	 * @return status See various status constants.
	 */
	@PID(16)
	public byte getLastStatus();

	public void setLastStatus(byte status);

	/**
	 * @return total volume traded so far
	 */
	@PID(17)
	public long getVolume();

	public void setVolume(long volumn);

	/**
	 * @return the highest executed price for the period
	 */
	@PID(18)
	public double getHighPrice();

	public void setHighPrice(double highPrice);

	/**
	 * @return the lowest executed price for the period
	 */
	@PID(19)
	public double getLowPrice();

	public void setLowPrice(double lowPrice);

	/**
	 * @return the openeing price for the period
	 */
	@PID(20)
	public double getOpenPrice();

	public void setOpenPrice(double openPrice);

}
