package com.f1.anvil.utils;

import java.util.Map;

import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.impl.CharSequenceHasher;

public class AnvilMarketDataMap {

	public Map<CharSequence, AnvilMarketDataSymbol> data = new HasherMap<CharSequence, AnvilMarketDataSymbol>(CharSequenceHasher.INSTANCE);
	private AnvilMarketData marketdata;

	public AnvilMarketDataMap(AnvilMarketData marketdata) {
		this.marketdata = marketdata;
	}

	int cnt = 0;
	public AnvilMarketDataSymbol getMarketData(CharSequence cs) {
		AnvilMarketDataSymbol r = data.get(cs);
		if (r == null) {
			final String string = cs.toString();
			r = this.marketdata.getMarketData(string);
			data.put(string, r);
		}
		return r;
	}

}
