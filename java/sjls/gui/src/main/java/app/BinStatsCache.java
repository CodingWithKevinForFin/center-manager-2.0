package app;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.sjls.algos.eo.common.IBinStatistics;

/**
 * A Cache BinStatistics Stores the binStats both by ticker (all bins for a ticker) and by bin (i.e. All tickers for a particular bin number)
 * 
 * @author Olu Emuleomo
 * @date 2010-05-03
 * 
 */
public class BinStatsCache {
	public static String CVS_ID = "$Id: BinStatsCache.java,v 1.1.1.1 2011/12/06 23:26:23 olu Exp $";

	private HashMap<Integer, HashMap<String, IBinStatistics>> m_byBinNum = new HashMap<Integer, HashMap<String, IBinStatistics>>();
	private HashMap<String, HashMap<Integer, IBinStatistics>> m_byTicker = new HashMap<String, HashMap<Integer, IBinStatistics>>();

	public synchronized void add(final IBinStatistics binStats) {
		storeByBinNum(binStats);
		storeByTicker(binStats);
	}

	private void storeByBinNum(final IBinStatistics binStats) {
		final int binNum = binStats.getBinNumber();
		HashMap<String, IBinStatistics> collOfTickers = m_byBinNum.get(binNum);
		if (collOfTickers == null) {
			collOfTickers = new HashMap<String, IBinStatistics>();
			m_byBinNum.put(binNum, collOfTickers);
		}
		collOfTickers.put(binStats.getTicker(), binStats);
	}

	private void storeByTicker(final IBinStatistics binStats) {
		final String ticker = binStats.getTicker();
		HashMap<Integer, IBinStatistics> collOfBins = m_byTicker.get(ticker);
		if (collOfBins == null) {
			collOfBins = new HashMap<Integer, IBinStatistics>();
			m_byTicker.put(ticker, collOfBins);
		}
		collOfBins.put(binStats.getBinNumber(), binStats);
	}

	/**
	 * Get the List of BinStatistics matching the binNumber May be an empty list. Never returns null
	 * 
	 * @param binNum
	 * @return
	 */
	public synchronized List<IBinStatistics> getByBin(final Integer binNum) {
		final LinkedList<IBinStatistics> list = new LinkedList<IBinStatistics>();
		final HashMap<String, IBinStatistics> collOfTickers = m_byBinNum.get(binNum);
		if (collOfTickers != null) {
			list.addAll(collOfTickers.values());
		}
		return list;
	}

	/**
	 * Get the List of BinStatistics matching the ticker May be an empty list. Never returns null
	 * 
	 * @param ticker
	 * @return
	 */
	public synchronized List<IBinStatistics> getByTicker(final String ticker) {
		final LinkedList<IBinStatistics> list = new LinkedList<IBinStatistics>();
		final HashMap<Integer, IBinStatistics> collOfBins = m_byTicker.get(ticker);
		if (collOfBins != null) {
			list.addAll(collOfBins.values());
		}
		return list;
	}

}
