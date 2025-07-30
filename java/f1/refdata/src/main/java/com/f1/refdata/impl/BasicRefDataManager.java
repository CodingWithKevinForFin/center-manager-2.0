package com.f1.refdata.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.base.DateNanos;
import com.f1.pofo.refdata.Exchange;
import com.f1.pofo.refdata.Fundamentals;
import com.f1.pofo.refdata.Security;
import com.f1.refdata.RefDataListener;
import com.f1.refdata.RefDataManager;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.IntKeyMap.Node;
import com.f1.utils.structs.IntSet;

public class BasicRefDataManager implements RefDataManager {

	final private IntKeyMap<Security> securitiesById = new IntKeyMap<Security>();
	final private IntKeyMap<Exchange> exchangesById = new IntKeyMap<Exchange>();
	final private IntKeyMap<Fundamentals> fundamentalsById = new IntKeyMap<Fundamentals>();
	final private List<RefDataListener> listeners = new ArrayList<RefDataListener>();
	final private BasicMultiMap.IntSet<String> securitiesBySymbol = new BasicMultiMap.IntSet<String>();
	final private BasicMultiMap.IntSet<String> securitiesByRic = new BasicMultiMap.IntSet<String>();
	final private BasicMultiMap.IntSet<String> securitiesByCusip = new BasicMultiMap.IntSet<String>();
	final private BasicMultiMap.IntSet<String> securitiesBySedol = new BasicMultiMap.IntSet<String>();
	final private BasicMultiMap.IntSet<String> securitiesByIsin = new BasicMultiMap.IntSet<String>();
	final private HasherMap<String, BasicMultiMap.IntSet<String>> securitiesByVendorSymbol = new HasherMap<String, BasicMultiMap.IntSet<String>>();
	final private DateNanos minAsOf;
	final private DateNanos maxAsOf;

	public BasicRefDataManager(DateNanos minAsOf, DateNanos maxAsOf) {
		this.minAsOf = minAsOf;
		this.maxAsOf = maxAsOf;
	}

	@Override
	public Security getSecurity(int securityId) {
		return securitiesById.get(securityId);
	}

	@Override
	public Exchange getExchange(int exchangeId) {
		return exchangesById.get(exchangeId);
	}

	@Override
	public Fundamentals getFundamentals(int fundamentalsId) {
		return fundamentalsById.get(fundamentalsId);
	}

	@Override
	public IntSet findSecurity(String symbol, String ric, String cusip, String sedol, String isin, DateNanos optionalAsOf) {
		if (optionalAsOf != null)
			OH.assertBetween(optionalAsOf, minAsOf, maxAsOf);
		final IntSet ids = new IntSet();
		addAll(ids, securitiesBySymbol, symbol);
		addAll(ids, securitiesByRic, ric);
		addAll(ids, securitiesByCusip, cusip);
		addAll(ids, securitiesBySedol, sedol);
		addAll(ids, securitiesByIsin, isin);
		return ids;
	}

	private void addAll(IntSet sink, BasicMultiMap.IntSet<String> m, String key) {
		if (key == null || m == null)
			return;
		final IntSet i = m.get(key);
		if (i != null)
			sink.addAll(i);
	}

	@Override
	public IntSet findSecurityByVendorSymbology(String vendor, String symbol, DateNanos optionalAsOf) {
		if (optionalAsOf != null)
			OH.assertBetween(optionalAsOf, minAsOf, maxAsOf);
		final IntSet ids = new IntSet();
		addAll(ids, securitiesByVendorSymbol.get(vendor), symbol);
		return ids;
	}

	@Override
	public void addRefDataListener(RefDataListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeRefDataListener(RefDataListener listener) {
		this.listeners.remove(listener);
	}

	public void addSecurity(Security security) {
		int id = security.getSecurityId();
		final Node<Security> node = securitiesById.getNodeOrCreate(id);
		final Security old = node.getValue();
		node.setValue(security);
		if (old == null) {
			final Map<String, String> vs = security.getVendorSymbologies();
			if (vs != null)
				for (Entry<String, String> e : vs.entrySet())
					updateMap(getSecuritiesByVendorMap(e.getKey()), null, e.getValue(), id);
			updateMap(securitiesBySymbol, null, security.getSymbol(), id);
			updateMap(securitiesByRic, null, security.getRic(), id);
			updateMap(securitiesByCusip, null, security.getCusip(), id);
			updateMap(securitiesBySedol, null, security.getSedol(), id);
			updateMap(securitiesByIsin, null, security.getIsin(), id);
		} else {
			final Map<String, String> oldVs = old.getVendorSymbologies();
			if (oldVs != null) {
				final Map<String, String> vs = OH.noNull(security.getVendorSymbologies(), Collections.EMPTY_MAP);
				for (String s : CH.comm(oldVs.keySet(), vs.keySet(), true, true, true))
					updateMap(getSecuritiesByVendorMap(s), oldVs.get(s), vs.get(s), id);
			}
			updateMap(securitiesBySymbol, old.getSymbol(), security.getSymbol(), id);
			updateMap(securitiesByRic, old.getRic(), security.getRic(), id);
			updateMap(securitiesByCusip, old.getCusip(), security.getCusip(), id);
			updateMap(securitiesBySedol, old.getSedol(), security.getSedol(), id);
			updateMap(securitiesByIsin, old.getIsin(), security.getIsin(), id);
		}
		for (RefDataListener l : listeners)
			l.onSecurity(security);

	}
	private BasicMultiMap.IntSet<String> getSecuritiesByVendorMap(String s) {
		Map.Entry<String, BasicMultiMap.IntSet<String>> e = securitiesByVendorSymbol.getOrCreateEntry(s);
		BasicMultiMap.IntSet<String> r = e.getValue();
		if (r == null)
			e.setValue(r = new BasicMultiMap.IntSet<String>());
		return r;
	}

	public void addExchange(Exchange exchange) {
		exchangesById.put(exchange.getExchangeId(), exchange);
		for (RefDataListener l : listeners)
			l.onExchange(exchange);
	}
	public void addFundamentals(Fundamentals fundamentals) {
		fundamentalsById.put(fundamentals.getFundamentalsId(), fundamentals);
		for (RefDataListener l : listeners)
			l.onFundamentals(fundamentals);
	}

	public boolean removeSecurity(int securityId) {
		Security security = securitiesById.remove(securityId);
		if (security == null)
			return false;
		securitiesBySymbol.removeMulti(security.getSymbol(), securityId);
		securitiesByRic.removeMulti(security.getRic(), securityId);
		securitiesByCusip.removeMulti(security.getCusip(), securityId);
		securitiesBySedol.removeMulti(security.getSedol(), securityId);
		securitiesByIsin.removeMulti(security.getIsin(), securityId);
		for (RefDataListener l : listeners)
			l.onSecurityRemoved(securityId);
		return true;
	}

	public boolean removeExchange(int exchangeId) {
		return exchangesById.remove(exchangeId) != null;
	}
	public boolean removeFundamentals(int fundamentals) {
		return fundamentalsById.remove(fundamentals) != null;
	}

	public Iterator<Security> getAllSecurities() {
		return securitiesById.valuesIterator();
	}
	public Iterator<Fundamentals> getAllFundamentals() {
		return fundamentalsById.valuesIterator();
	}
	public Iterator<Exchange> getAllExchanges() {
		return exchangesById.valuesIterator();
	}

	static private <K> void updateMap(BasicMultiMap.IntSet<K> map, K old, K nuw, int id) {
		if (OH.eq(old, nuw))
			return;
		if (old != null)
			map.removeMulti(old, id);
		if (nuw != null)
			map.putMulti(nuw, id);
	}

	@Override
	public Set<String> getVendorSymbologyTypes() {
		return securitiesByVendorSymbol.keySet();
	}

	@Override
	public int getSecuritiesCount() {
		return securitiesById.size();
	}

}
