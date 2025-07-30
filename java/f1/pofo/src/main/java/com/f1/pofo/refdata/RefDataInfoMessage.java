package com.f1.pofo.refdata;

import java.util.Map;

import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.RD.DI")
public interface RefDataInfoMessage extends PartialMessage {

	/**
	 * @return collection of associated exchanges based on query. Key is
	 *         exchange id
	 */
	public Map<Integer, Exchange> getExchanges();

	public void setExchanges(Map<Integer, Exchange> exchangeId);

	/**
	 * @return collection of associated securities based on query. Key is
	 *         securities id
	 */
	public Map<Integer, Security> getSecurities();

	public void setSecurities(Map<Integer, Security> securities);

	/**
	 * @return collection of associated fundamentals based on query. Key is
	 *         fundamentals id
	 */
	public Map<Integer, Fundamentals> getFundamentals();

	public void setFundamentals(Map<Integer, Fundamentals> fundamentals);
}
