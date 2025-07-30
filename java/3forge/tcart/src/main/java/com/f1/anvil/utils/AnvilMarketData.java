package com.f1.anvil.utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import com.f1.anvil.loader.AnvilFileLoaderManager;
import com.f1.base.Factory;
import com.f1.utils.CopyOnWriteHashMap;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.TimeOfDay;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.impl.CharSequenceHasher;

public class AnvilMarketData implements Factory<Map, Map> {
	public static final String SERVICE_NAME = "AnvilMarketData";

	private static final int LOG_PERIOD = 1000000;
	private static final Logger log = LH.get(AnvilMarketData.class);
	//public static AnvilMarketData INSTANCE = new AnvilMarketData();
	public ConcurrentMap<String, AnvilMarketDataSymbol> data = new ConcurrentHashMap<String, AnvilMarketDataSymbol>();
	public ConcurrentMap<CharSequence, Short> exchangesByName = new CopyOnWriteHashMap<CharSequence, Short>((Factory) this);
	public ConcurrentMap<CharSequence, Short> currenciesByName = new CopyOnWriteHashMap<CharSequence, Short>((Factory) this);
	//public ConcurrentMap<String, AnvilMarketDataFx> fxByName = new ConcurrentHashMap<String, AnvilMarketDataFx>();
	private String[] exchanges = new String[Short.MAX_VALUE];
	private String[] currencies = new String[Short.MAX_VALUE];
	private String[] exchangesAsAsk = new String[Short.MAX_VALUE];
	private String[] exchangesAsBid = new String[Short.MAX_VALUE];
	private AtomicInteger nextExId = new AtomicInteger(1);
	private AtomicInteger nextCurId = new AtomicInteger(1);
	private volatile int tradeCount;
	private volatile int nbbosCount;

	final private String spotCurrency;

	final private short spotCurrencyId;

	private String defaultOpenTime;
	private String defaultCloseTime;

	public AnvilMarketData(PropertyController properties) {
		this.spotCurrency = properties.getOptional(AnvilFileLoaderManager.OPTION_ANVIL_SPOT_CURRENCY, "USD");
		this.defaultOpenTime = properties.getOptional(AnvilFileLoaderManager.OPTION_ANVIL_OPEN_TIME, "09:30:00 EST5EDT");
		this.defaultCloseTime = properties.getOptional(AnvilFileLoaderManager.OPTION_ANVIL_CLOSE_TIME, "16:00:00 EST5EDT");
		this.spotCurrencyId = (short) nextCurId.getAndIncrement();
	}
	public AnvilMarketDataMap createLocalMarketDataMap() {
		return new AnvilMarketDataMap(this);
	}

	protected AnvilMarketDataSymbol getMarketData(String symbol) {
		AnvilMarketDataSymbol r = data.get(symbol);
		if (r == null) {
			data.putIfAbsent(symbol, new AnvilMarketDataSymbol(this, symbol, this.spotCurrency, new TimeOfDay(defaultOpenTime), new TimeOfDay(defaultCloseTime)));
			r = data.get(symbol);
		}
		return r;
	}

	//	protected AnvilMarketDataFx getFxData(String pair) {
	//		AnvilMarketDataFx r = fxByName.get(pair);
	//		if (r == null) {
	//			fxByName.putIfAbsent(pair, new AnvilMarketDataFx(this, pair));
	//			r = fxByName.get(pair);
	//		}
	//		return r;
	//	}
	public String getExString(short s) {
		return exchanges[s];
	}
	public String getExStringAsAsk(short s) {
		return exchangesAsAsk[s];
	}
	public String getExStringAsBid(short s) {
		return exchangesAsBid[s];
	}

	public short getCurrencyId(CharSequence currencyName) {
		if (SH.equals(this.spotCurrency, currencyName))
			return this.spotCurrencyId;
		Short r = currenciesByName.get(currencyName);
		if (r == null) {
			synchronized (this) {
				r = currenciesByName.get(currencyName);
				if (r == null) {
					short id = (short) nextCurId.getAndIncrement();
					String str = currencyName.toString();
					currenciesByName.putIfAbsent(str, id);
					exchanges[id] = str;
				}
			}
			r = currenciesByName.get(currencyName);

		}
		return r.shortValue();
	}

	public short getExId(CharSequence exchange) {
		Short r = exchangesByName.get(exchange);
		if (r == null) {
			synchronized (this) {
				r = exchangesByName.get(exchange);
				if (r == null) {
					short id = (short) nextExId.getAndIncrement();
					String str = exchange.toString();
					exchangesByName.putIfAbsent(str, id);
					exchanges[id] = str;
					int pos = str.indexOf('/');
					if (pos == -1) {
						exchangesAsAsk[id] = str;
						exchangesAsBid[id] = str;
					} else {
						exchangesAsAsk[id] = str.substring(0, pos);
						exchangesAsBid[id] = str.substring(pos + 1);
					}
				}
			}
			r = exchangesByName.get(exchange);
		}
		return r.shortValue();
	}

	@Override
	public Map get(Map key) {
		HasherMap r = new HasherMap(CharSequenceHasher.INSTANCE);
		if (key != null)
			r.putAll(key);
		return r;
	}

	public void incTrades() {
		if (++this.tradeCount % LOG_PERIOD == 0)
			LH.info(log, "Processed Trades:", tradeCount);

	}
	public void incNbbos() {
		if (++this.nbbosCount % LOG_PERIOD == 0)
			LH.info(log, "Processed Nbbo: ", tradeCount);
	}

	private static final Comparator<AnvilMarketDataSymbol> TRADES_TIME_COMPARATOR = new Comparator<AnvilMarketDataSymbol>() {

		@Override
		public int compare(AnvilMarketDataSymbol o1, AnvilMarketDataSymbol o2) {
			return OH.compare(o1.getCurrentTradeTime(), o2.getCurrentTradeTime());
		}
	};
	private static final Comparator<AnvilMarketDataSymbol> NBBOS_TIME_COMPARATOR = new Comparator<AnvilMarketDataSymbol>() {

		@Override
		public int compare(AnvilMarketDataSymbol o1, AnvilMarketDataSymbol o2) {
			return OH.compare(o1.getCurrentNbboTime(), o2.getCurrentNbboTime());
		}
	};

	private AnvilMarketDataSymbol[] tradeQueue = new AnvilMarketDataSymbol[1000];
	private int tradeQueueSize;

	public void addToTradeQueue(AnvilMarketDataSymbol mds) {
		if (tradeQueueSize == this.tradeQueue.length)
			this.tradeQueue = Arrays.copyOf(tradeQueue, tradeQueueSize << 1);
		this.tradeQueue[tradeQueueSize++] = mds;
	}

	private AnvilMarketDataSymbol[] nbboQueue = new AnvilMarketDataSymbol[1000];
	private int nbboQueueSize;

	private volatile long currentNbbosTime;
	private volatile long currenTradesTime;
	public void addToNbboQueue(AnvilMarketDataSymbol mds) {
		if (nbboQueueSize == this.nbboQueue.length)
			this.nbboQueue = Arrays.copyOf(nbboQueue, nbboQueueSize << 1);
		this.nbboQueue[nbboQueueSize++] = mds;
	}

	public void getTrades(List<AnvilMarketDataSymbol> buf) {
		Arrays.sort(tradeQueue, 0, tradeQueueSize, TRADES_TIME_COMPARATOR);
		for (int i = 0; i < tradeQueueSize; i++) {
			buf.add(tradeQueue[i]);
			tradeQueue[i].onTradeRemovedFromBuffer();
			tradeQueue[i] = null;
		}
		tradeQueueSize = 0;
	}
	public void getNbbos(List<AnvilMarketDataSymbol> buf) {
		Arrays.sort(nbboQueue, 0, nbboQueueSize, NBBOS_TIME_COMPARATOR);
		for (int i = 0; i < nbboQueueSize; i++) {
			buf.add(nbboQueue[i]);
			nbboQueue[i].onNbboRemovedFromBuffer();
			nbboQueue[i] = null;
		}
		nbboQueueSize = 0;
	}

	public int getTradeCount() {
		return this.tradeCount;
	}

	public int getNbboCount() {
		return this.nbbosCount;
	}

	protected void setCurrentNbbosTime(long time) {
		this.currentNbbosTime = time;
	}

	protected void setCurrentTradesTime(long time) {
		this.currenTradesTime = time;
	}
	public long getCurrentNbbosTime() {
		return this.currentNbbosTime;
	}

	public long getCurrentTradesTime() {
		return this.currenTradesTime;
	}

	double[] fxRates = new double[64 * 64];
	//private MapInMap<String, String, Double> fxRate = new MapInMap<String, String, Double>();
	private CopyOnWriteHashMap<String, Double> spotRateCache = new CopyOnWriteHashMap<String, Double>();
	public double getSpotValue(double value, String currency) {
		return value * getFxSpotRate(currency);
	}
	public double getFxSpotRate(CharSequence currency) {
		if (SH.equals(this.spotCurrency, currency))
			return 1d;
		return getFxSpotRate(getCurrencyId(currency));
	}
	public double getFxSpotRate(short currency2) {
		if (currency2 == this.spotCurrencyId)
			return 1d;
		double r = getFxRate(currency2, this.spotCurrencyId);
		if (r != 0)
			return r;
		else
			return 1;
		//		
		//		
		//		Double r = spotRateCache.get(currency2);
		//		if (r != null)
		//			return r.doubleValue();
		//		synchronized (fxRate) {
		//			r = getCurrencyToCurrencyRate(this.spotCurrency, currency2);
		//			spotRateCache.put(currency2, r);
		//		}
		//		if (Double.isNaN(r.doubleValue()))
		//			return 1d;//THIS IS BAD
		//		else
		//			return r.doubleValue();
	}
	//	public double getCurrencyToCurrencyRate(String base, String counter) {
	//		if (base.equals(counter))
	//			return 1d;
	//		synchronized (fxRate) {
	//			Double r = fxRate.getMulti(base, counter);
	//			if (r != null)
	//				return r;
	//			r = fxRate.getMulti(counter, base);
	//			if (r != null)
	//				return 1d / r.doubleValue();
	//			Map<String, Double> t = fxRate.get(counter);
	//			if (t != null) {
	//				for (Entry<String, Double> i : t.entrySet()) {
	//					double r2 = getSpotToCurrencyRate(i.getKey());
	//					if (!Double.isNaN(r2))
	//						return r2 / i.getValue().doubleValue();
	//					break;
	//				}
	//			}
	//			return Double.NaN;
	//		}
	//	}
	public void addFxRate(String base, String counter, double rate) {
		addFxRate(getCurrencyId(base), getCurrencyId(counter), rate);
	}
	public double getFxRate(short base, short counter) {
		return fxRates[(base << 6) | counter];
	}

	public void addFxRate(short base, short counter, double rate) {
		if (base == counter) {
			if (rate == 1d)
				return;
			throw new RuntimeException("Invalid pair: " + base + "/" + counter + "=" + rate);
		}
		fxRates[(base << 6) | counter] = rate;
		fxRates[(counter << 6) | base] = 1d / rate;
		for (AnvilMarketDataSymbol i : this.data.values())
			i.clearFxRateCache();
		//		synchronized (fxRate) {
		//			fxRate.putMulti(base, counter, rate);
		//			spotRateCache.clear();
		//		}
	}
	public static void main(String a[]) {
		AnvilMarketData t = new AnvilMarketData(null);//"USD", "09:30:00 EST5EDT");
		t.addFxRate("USD", "JPY", 113.66);
		t.addFxRate("BGP", "JPY", 165.03);
		//		System.out.println(t.getCurrencyToCurrencyRate("USD", "JPY"));
		//		System.out.println(t.getCurrencyToCurrencyRate("BGP", "JPY"));
		//		System.out.println(t.getCurrencyToCurrencyRate("USD", "BGP"));
		System.out.println();
		System.out.println(t.getSpotValue(50, "USD"));
		System.out.println(t.getSpotValue(200, "JPY"));
		System.out.println(t.getSpotValue(40, "BGP"));
		//System.out.println(t.getSpotToCurrencyRate("USD"));
		//System.out.println(t.getSpotToCurrencyRate("JPY"));
		//System.out.println(t.getSpotToCurrencyRate("BGP"));

	}
}
