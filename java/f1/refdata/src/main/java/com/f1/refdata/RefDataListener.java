package com.f1.refdata;

import com.f1.pofo.refdata.Exchange;
import com.f1.pofo.refdata.Fundamentals;
import com.f1.pofo.refdata.Security;

public interface RefDataListener {

	public void onSecurity(Security security);
	public void onFundamentals(Fundamentals fundamentals);
	public void onExchange(Exchange exchange);

	public void onSecurityRemoved(int securityId);
	public void onFundamentalsRemoved(int fundamentalId);
	public void onExchangeRemoved(int exchangeId);
}
