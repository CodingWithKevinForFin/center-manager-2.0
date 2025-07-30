package com.f1.pofo.refdata;

import java.util.Set;

import com.f1.base.DateNanos;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

/**
 * request for reference data. Optionally supply a combination of fields. For
 * example, you could populate just isin, or populate both isin and cusip to
 * narrow search results.
 * 
 */
@VID("F1.RD.FS")
public interface RefDataRequestMessage extends PartialMessage {

	/**
	 * @return see {@link Security#getSymbol()}
	 */
	String getSymbol();

	public void setSymbol(String symbol);

	/**
	 * @return see {@link Security#getRic()}
	 */
	String getRic();

	public void setRic(String ric);

	/**
	 * @return see {@link Security#getCusip()}
	 */
	String getCusip();

	public void setCusip(String cusip);

	/**
	 * @return see {@link Security#getSedol()}
	 */
	String getSedol();

	public void setSedol(String sedol);

	/**
	 * @return see {@link Security#getIsin()}
	 */
	String getIsin();

	public void setIsin(String isin);

	/**
	 * @return at what time the data should have existed.
	 */
	DateNanos getAsOf();

	public void setAsOf(DateNanos asOf);

	/**
	 * @return list of exchanges to get. see {@link Exchange#getExchangeId()}
	 */
	public Set<Integer> getExchangeIds();

	public void setExchangeIds(Set<Integer> exchangeId);

	/**
	 * @return list of securities to get. see {@link Security#getSecurityId()()}
	 */
	public Set<Integer> getSecurityIds();

	public void setSecurityIds(Set<Integer> exchangeId);

	/**
	 * @return list of fundamentals to get. see
	 *         {@link Fundamentals#getFundamentalsId()}
	 */
	public Set<Integer> getFundamentalIds();

	public void setFundamentalIds(Set<Integer> exchangeId);
}
