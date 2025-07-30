package com.f1.refdata;

import java.util.Date;
import java.util.Set;

import com.f1.base.DateNanos;
import com.f1.pofo.refdata.Exchange;
import com.f1.pofo.refdata.Fundamentals;
import com.f1.pofo.refdata.Security;
import com.f1.utils.structs.IntSet;

public interface RefDataManager {

	/**
	 * 
	 * @param securityId
	 *            see {@link Security#getSecurityId()}
	 * @return may return null, if security does not exist
	 */
	Security getSecurity(int securityId);

	/**
	 * 
	 * @param exchangeId
	 *            see {@link Exchange#getExchangeId()}
	 * @return may return null, if exchange does not exist
	 */
	Exchange getExchange(int exchangeId);

	/**
	 * @param fundamentalsId
	 *            see {@link Fundamentals#getFundamentalsId()}
	 * @return may return null, if fundamentals does not exist
	 */
	Fundamentals getFundamentals(int fundamentalsId);

	/**
	 * will return a collections of securitiesids for use with {@link #getSecurity(int)}. Securities matching <B>any</B> of the supplied params will be included
	 * 
	 * @param symbol
	 *            if not null, include securities with matching symbol
	 * @param ric
	 *            if not null, include securities with matching ric
	 * @param cusip
	 *            if not null, include securities with matching cusip
	 * @param sedol
	 *            if not null, include securities with matching sedol
	 * @param isin
	 *            if not null, include securities with matching isin
	 * @param optionalAsOf
	 *            if not null, The time that the security was valid for. If null, only return currently valid symbols
	 * @return never returns null, returns a collection of security ids
	 * */
	IntSet findSecurity(String symbol, String ric, String cusip, String sedol, String isin, DateNanos optionalAsOf);

	/**
	 * will return a collections of securitiesids for use with {@link #getSecurity(int)}.
	 * 
	 * @see Security#getVendorSymbologies()
	 * 
	 * @param vendor
	 *            the vendor name
	 * @param symbol
	 *            the vendor specific vendor name
	 * @param optionalAsOf
	 *            if not null, The time that the security was valid for. If null, only return currently valid symbols
	 * @return never returns null, returns a collection of security ids
	 */
	IntSet findSecurityByVendorSymbology(String vendor, String symbol, DateNanos optionalAsOf);

	/**
	 * 
	 * @return A collection of valid vendors, for use in vendor parameter of {@link #findSecurityByVendorSymbology(String, String, Date)}
	 */
	Set<String> getVendorSymbologyTypes();

	public int getSecuritiesCount();

	/**
	 * Add a listener to be notified of changes to this ref data manager
	 * 
	 * @param listener
	 */
	//
	void addRefDataListener(RefDataListener listener);

	/**
	 * Remove an existing listener added using addRefDataListener()..
	 * 
	 * @param listener
	 */
	void removeRefDataListener(RefDataListener listener);

}
