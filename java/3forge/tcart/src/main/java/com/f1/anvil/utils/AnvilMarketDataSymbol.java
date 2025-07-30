package com.f1.anvil.utils;

import java.util.Arrays;

import com.f1.utils.AH;
import com.f1.utils.MH;
import com.f1.utils.TimeOfDay;

public class AnvilMarketDataSymbol {

	private static final int DEFAULT_SIZE = 1000;
	//private static final int OPEN_TIME = (int) SH.parseTime("09:30:00.000");
	private AnvilMarketData marketData;

	private long inTradeQueueTime = -1;
	private long inNbboQueueTime = -1;
	private TimeOfDay openTime;
	private TimeOfDay closeTime;

	public AnvilMarketDataSymbol(AnvilMarketData anvilMarketData, String symbol, String defaultCurrency, TimeOfDay openTime, TimeOfDay closeTime) {
		this.symbol = symbol;
		this.defaultCurrency = defaultCurrency;
		this.marketData = anvilMarketData;
		this.openTime = openTime;
		this.closeTime = closeTime;
	}

	private long volume;
	private float low, high;
	private double value;
	private float open = Float.NaN;
	private boolean sentOpen = false;
	private boolean isFirst = true;
	private final String symbol;
	final private String defaultCurrency;
	volatile private long currentNbboTime;
	volatile private int nbboCount;
	volatile private long[] nbboTime = new long[DEFAULT_SIZE];
	volatile private short[] nbboEx = new short[DEFAULT_SIZE];
	volatile private float[] nbboBidPx = new float[DEFAULT_SIZE];
	volatile private float[] nbboAskPx = new float[DEFAULT_SIZE];

	volatile private long currentTradeTime;
	volatile private int tradeCount;
	volatile private long[] tradeTime = new long[DEFAULT_SIZE];
	volatile private short[] tradeEx = new short[DEFAULT_SIZE];
	volatile private int[] tradeSize = new int[DEFAULT_SIZE];
	volatile private float[] tradePx = new float[DEFAULT_SIZE];
	volatile private long[] tradeAggVol = new long[DEFAULT_SIZE];
	volatile private double[] tradeAggVal = new double[DEFAULT_SIZE];
	volatile private double[] tradeAggHigh = new double[DEFAULT_SIZE];
	volatile private double[] tradeAggLow = new double[DEFAULT_SIZE];
	private float currentTradePx;
	private int currentTradeSize;
	private short currentTradeEx;
	private short currentNbboEx;
	private float currentNbboBidPx;
	private float currentNbboAskPx;
	private short fxCacheCurrency = -1;
	private double fxCacheRate;

	public void setOpenTime(TimeOfDay timeOfDay) {
		this.openTime = timeOfDay;
	}

	public TimeOfDay getOpenTime() {
		return this.openTime;
	}
	public TimeOfDay getCloseTime() {
		return this.closeTime;
	}

	public boolean addTrade(long time, CharSequence ex, int size, float px, CharSequence currency) {
		final double fxRate = getRate(currency);
		px *= fxRate;
		if (time < currentTradeTime)
			return false;
		if (tradeCount == tradeTime.length) {
			int grow = this.tradeTime.length << 1;
			this.tradeTime = Arrays.copyOf(this.tradeTime, grow);
			this.tradeEx = Arrays.copyOf(this.tradeEx, grow);
			this.tradeSize = Arrays.copyOf(this.tradeSize, grow);
			this.tradePx = Arrays.copyOf(this.tradePx, grow);
			this.tradeAggVol = Arrays.copyOf(this.tradeAggVol, grow);
			this.tradeAggVal = Arrays.copyOf(this.tradeAggVal, grow);
			this.tradeAggHigh = Arrays.copyOf(this.tradeAggHigh, grow);
			this.tradeAggLow = Arrays.copyOf(this.tradeAggLow, grow);
		}
		this.currentTradeTime = this.tradeTime[tradeCount] = time;
		this.currentTradeEx = this.tradeEx[tradeCount] = this.marketData.getExId(ex);
		this.currentTradeSize = this.tradeSize[tradeCount] = size;
		this.currentTradePx = this.tradePx[tradeCount] = px;
		this.marketData.incTrades();
		if (open != open && this.openTime.isGe(time))
			open = px;
		if (isFirst) {
			isFirst = false;
			low = high = px;
			volume = size;
			value = size * px;
		} else {
			if (px < low)
				low = px;
			else if (px > high)
				high = px;
			volume += size;
			value += size * (double) px;
		}
		this.tradeAggVal[tradeCount] = value;
		this.tradeAggVol[tradeCount] = volume;
		this.tradeAggHigh[tradeCount] = high;
		this.tradeAggLow[tradeCount] = low;
		this.tradeCount++;

		if (inTradeQueueTime == -1) {
			this.marketData.addToTradeQueue(this);
			inTradeQueueTime = time;
		}
		this.marketData.setCurrentTradesTime(time);
		return true;
	}

	public boolean addNbbo(long time, CharSequence ex, float bidPx, float askPx, CharSequence currency) {
		final double fxRate = getRate(currency);
		bidPx *= fxRate;
		askPx *= fxRate;
		if (currency == null)
			currency = defaultCurrency;
		if (time < currentNbboTime)
			return false;
		if (bidPx <= 0f || askPx <= 0f) {
			if (nbboCount == 0)
				return false;
			if (bidPx <= 0f)
				bidPx = this.currentNbboBidPx;
			if (askPx <= 0f)
				askPx = this.currentNbboAskPx;
		}
		if (nbboCount == nbboTime.length) {
			int grow = this.nbboTime.length << 1;
			this.nbboTime = Arrays.copyOf(this.nbboTime, grow);
			this.nbboEx = Arrays.copyOf(this.nbboEx, grow);
			this.nbboBidPx = Arrays.copyOf(this.nbboBidPx, grow);
			this.nbboAskPx = Arrays.copyOf(this.nbboAskPx, grow);
		}
		this.currentNbboTime = this.nbboTime[nbboCount] = time;
		this.currentNbboEx = this.nbboEx[nbboCount] = this.marketData.getExId(ex);
		this.currentNbboBidPx = this.nbboBidPx[nbboCount] = bidPx;
		this.currentNbboAskPx = this.nbboAskPx[nbboCount] = askPx;
		this.nbboCount++;
		this.marketData.incNbbos();
		if (inNbboQueueTime == -1) {
			this.marketData.addToNbboQueue(this);
			inNbboQueueTime = time;
		}
		this.marketData.setCurrentNbbosTime(time);
		return true;
	}
	private double getRate(CharSequence currency) {
		if (currency == null)
			currency = this.defaultCurrency;
		short t = marketData.getCurrencyId(currency);
		if (t == this.fxCacheCurrency)
			return this.fxCacheRate;
		double rate = marketData.getFxSpotRate(currency);
		if (MH.isntNumber(rate))
			rate = 1;
		this.fxCacheCurrency = t;
		this.fxCacheRate = rate;
		return rate;
	}
	public long getNbboTimeAtPos(int pos) {
		return this.nbboTime[pos];
	}
	public String getNbboExAtPos(int pos) {
		return this.marketData.getExString(this.nbboEx[pos]);

	}
	public float getNbboBidPxAtPos(int pos) {
		return this.nbboBidPx[pos];

	}
	public float getNbboAskPxAtPos(int pos) {

		return this.nbboAskPx[pos];
	}

	public long getTradeTimeAtPos(int pos) {
		return tradeTime[pos];

	}
	public String getTradeExAtPos(int pos) {
		return this.marketData.getExString(this.nbboEx[pos]);
	}
	public int getTradeSizeAtPos(int pos) {
		return tradeSize[pos];

	}
	public float getTradePxAtPos(int pos) {
		return tradePx[pos];
	}
	public double getTradeAggVal(int pos) {
		return tradeAggVal[pos];
	}
	public void getTradeAggVals(int startInc, int endEx, double[] sink, int sinkStart) {
		System.arraycopy(this.tradeAggVal, startInc, sink, sinkStart, endEx - startInc);
	}
	public void getTradeAggVols(int startInc, int endEx, long[] sink, int sinkStart) {
		System.arraycopy(this.tradeAggVol, startInc, sink, sinkStart, endEx - startInc);
	}
	public void getTradePxs(int startInc, int endEx, float[] sink, int sinkStart) {
		System.arraycopy(this.tradePx, startInc, sink, sinkStart, endEx - startInc);
	}
	public void getTradeSizes(int startInc, int endEx, int[] sink, int sinkStart) {
		System.arraycopy(this.tradeSize, startInc, sink, sinkStart, endEx - startInc);
	}
	public void getTradeTimes(int startInc, int endEx, long[] sink, int sinkStart) {
		System.arraycopy(this.tradeTime, startInc, sink, sinkStart, endEx - startInc);
	}
	public long getTradeAggVol(int pos) {
		return tradeAggVol[pos];
	}
	public double getTradeAggHigh(int pos) {
		return tradeAggLow[pos];
	}
	public double getTradeAggLow(int pos) {
		return tradeAggHigh[pos];
	}

	public int getNbboPositionGe(long time) {
		return getGt(time - 1, this.nbboTime, this.nbboCount);
	}
	public int getNbboPositionGt(long time) {
		return getGt(time, this.nbboTime, this.nbboCount);
	}
	public int getNbboPositionLe(long time) {
		return getLt(time + 1, this.nbboTime, this.nbboCount);
	}
	public int getNbboPositionLt(long time) {
		return getLt(time, this.nbboTime, this.nbboCount);
	}
	public int getTradePositionGe(long time) {
		return getGt(time - 1, this.tradeTime, this.tradeCount);
	}
	public int getTradePositionGt(long time) {
		return getGt(time, this.tradeTime, this.tradeCount);
	}
	public int getTradePositionLe(long time) {
		return getLt(time + 1, this.tradeTime, this.tradeCount);
	}
	public int getTradePositionLt(long time) {
		return getLt(time, this.tradeTime, this.tradeCount);
	}

	public static int getGt(long time, long[] times, int cnt) {
		int r = AH.indexOfSortedGreaterThanEqualTo(time, times, cnt);
		if (r == -1)
			return -1;
		while (times[r] == time)
			if (++r == cnt)
				return -1;
		return r;
	}
	public static int getLt(long time, long[] times, int cnt) {
		int r = AH.indexOfSortedLessThanEqualTo(time, times, cnt);
		if (r == -1)
			return -1;
		while (times[r] == time)
			if (--r == -1)
				return -1;
		return r;
	}

	public long getCurrentTradeTime() {
		return this.currentTradeTime;
	}
	public long getCurrentNbboTime() {
		return this.currentNbboTime;
	}
	public long getCurrentTradeVolume() {
		return this.volume;
	}
	public float getCurrentTradeHigh() {
		return this.high;
	}
	public float getCurrentTradeLow() {
		return this.low;
	}
	public float getCurrentTradePx() {
		return this.currentTradePx;
	}
	public double getCurrentTradeValue() {
		return this.value;
	}
	public int getCurrentTradeSize() {
		return this.currentTradeSize;
	}

	public float getCurrentTradeOpen() {
		if (sentOpen || this.open != this.open)
			return Float.NaN;
		sentOpen = true;
		return this.open;
	}
	public float getTradeOpen() {
		return open;
	}

	public void onTradeRemovedFromBuffer() {
		this.inTradeQueueTime = -1;
	}
	public void onNbboRemovedFromBuffer() {
		this.inNbboQueueTime = -1;
	}

	public String getSymbol() {
		return this.symbol;
	}

	public CharSequence getCurrentTradeEx() {
		return this.marketData.getExString(this.currentTradeEx);
	}

	public float getCurrentNbboBidPx() {
		return this.currentNbboBidPx;
	}
	public float getCurrentNbboAskPx() {
		return this.currentNbboAskPx;
	}
	public String getCurrentNbboBidEx() {
		return this.marketData.getExStringAsBid(this.currentNbboEx);
	}
	public String getCurrentNbboAskEx() {
		return this.marketData.getExStringAsAsk(this.currentNbboEx);
	}
	public long getCurrentTime() {
		return this.currentNbboTime;
	}

	public int getCurrentTradeCount() {
		return this.tradeCount;
	}
	public int getCurrentNbboCount() {
		return this.nbboCount;
	}

	public void getNbboTimes(int startInc, int endEx, long[] sink, int sinkStart) {
		System.arraycopy(this.nbboTime, startInc, sink, sinkStart, endEx - startInc);
	}
	public void getNbboBidPxs(int startInc, int endEx, float[] sink, int sinkStart) {
		System.arraycopy(this.nbboBidPx, startInc, sink, sinkStart, endEx - startInc);
	}
	public void getNbboAskPxs(int startInc, int endEx, float[] sink, int sinkStart) {
		System.arraycopy(this.nbboAskPx, startInc, sink, sinkStart, endEx - startInc);
	}

	public void clearFxRateCache() {
		this.fxCacheCurrency = -1;
	}

}