package app;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.sjls.algos.eo.common.AlgoParams;
import com.sjls.algos.eo.common.AlgoParamsVWAP;
import com.sjls.algos.eo.common.IExecutionOptimizer;
import com.sjls.algos.eo.common.IParentOrderEvent;
import com.sjls.algos.eo.common.NewParentOrderEvent;
import com.sjls.algos.eo.common.ParentOrderMsg;
import com.sjls.algos.eo.common.QuoteData;
import com.sjls.algos.eo.common.Side;
import com.sjls.algos.eo.common.TimeInForce;
import com.sjls.algos.eo.common.TradeData;
import com.sjls.algos.eo.schedulers.PS.PSStockFile;
import com.sjls.algos.eo.utils.DateUtils;

public class TickAndOrderGenerator implements Runnable {

	private static Logger m_logger = Logger.getLogger(TickAndOrderGenerator.class);

	private final IExecutionOptimizer m_exectnOptimizer;
	private final String m_ticker;
	private volatile double m_bidPx;
	private final double m_spreadHint;

	private volatile int m_count = 0;
	private volatile int m_accumShares = 0;

	private boolean m_isPOrderSent = false;

	private final ParentOrderMsg m_poMsg; // Parent order to send after 1st quote!

	private volatile ScheduledFuture<?> m_future;

	private volatile boolean m_isOpeningTradeAlreadySent = false;

	private final double m_bidPx0;

	private boolean m_sendQuotes = true;

	public TickAndOrderGenerator(final IExecutionOptimizer exectnOptimizer, final String ticker, final double beginPx, final double spreadHint, final ParentOrderMsg poMsg,
			final int accumShares) {
		m_exectnOptimizer = exectnOptimizer;
		m_ticker = ticker;
		m_bidPx0 = m_bidPx = beginPx;
		m_spreadHint = Math.max(spreadHint / 100.0, 0.01);
		m_poMsg = poMsg;
		m_accumShares = accumShares;
	}

	public TickAndOrderGenerator(final IExecutionOptimizer exectnOptimizer, final String ticker, final double beginPx, final double spreadHint, final ParentOrderMsg poMsg) {
		this(exectnOptimizer, ticker, beginPx, spreadHint, poMsg, 0);
	}

	public TickAndOrderGenerator(final IExecutionOptimizer exectnOptimizer, final String ticker, final double beginPx, final double spreadHint, final ParentOrderMsg poMsg,
			final boolean isOrderSent) {
		this(exectnOptimizer, ticker, beginPx, spreadHint, poMsg, 0);
		m_isPOrderSent = isOrderSent;
	}

	@Override
	public void run() {
		if (!m_isPOrderSent) { // send Parent order after the 1st quote!
			// Now send the new event
			final NewParentOrderEvent poEvent = new NewParentOrderEvent(m_poMsg);
			m_logger.info("Sending new parent order to JpmEO ==>" + poEvent + "<==");
			try {
				m_exectnOptimizer.onParentOrderEvent(poEvent);
			} catch (Exception e) {
				m_logger.error(e.getMessage(), e);
			} finally {
				m_isPOrderSent = true;
			}
		}

		m_count++;
		m_bidPx = m_bidPx == 0 ? 0 : m_bidPx + (rand012() - 1) * Math.max(0.01, m_spreadHint / 4.0); // move up, down by max of 1/4 of spreadHint
		if (100.0 * Math.abs(m_bidPx - m_bidPx0) / m_bidPx0 > 5)
			m_bidPx = m_bidPx0; // dont drift more than 5%

		final double askPx = m_bidPx == 0 ? 0 : m_bidPx + (m_spreadHint * (10 - rand012()) / 10.0);
		//
		if (m_count > 0 && (m_count % 5 == 0)) { // Trade
			final TradeData data = new TradeData(m_ticker);
			data.setAccumShares(m_accumShares);
			data.setTradePrice((m_bidPx + askPx) / 2.0);
			data.setTradeSize(500 * rand123()); // trade size = 500, 1000 or 1500
			data.setTradeVenue("G");
			if (DateUtils.getCurrentTime().isAfter(DateUtils.getUSMarketOpenTime()) && (!m_isOpeningTradeAlreadySent)) {
				data.setOpeningTradeFlag(true);
				m_isOpeningTradeAlreadySent = true;
				data.setTradeSize(-1);
				m_logger.info("********OPENING TRADE SIZE set to [" + data.getTradeSize() + "]*************");
			}
			//

			if (m_logger.isDebugEnabled())
				m_logger.debug("Sending trade [" + data + "]");
			m_exectnOptimizer.onTrade(data);
			m_accumShares += data.getTradeSize();
			//
			if (m_logger.isDebugEnabled())
				m_logger.debug("Done. SENT trade [" + data + "]");
		} else { // Quote
			if (m_sendQuotes) {
				final QuoteData data = new QuoteData(m_ticker);
				data.setBidSize(100 * rand123());
				data.setAskSize(100 * rand123());
				data.setBidPrice(round2(m_bidPx));
				data.setAskPrice(round2(askPx));
				//
				if (m_logger.isDebugEnabled())
					m_logger.debug("Sending quote [" + data + "]");

				m_exectnOptimizer.onQuote(data);
				//
				if (m_logger.isDebugEnabled())
					m_logger.debug("Done. SENT quote [" + data + "]");
			}
		}
	}

	private static double round2(final double val) {
		return Math.rint(val * 100d) / 100d;
	}

	public void cancel() {
		final IParentOrderEvent poEvent = new CancelParentOrderEvent(m_poMsg);
		m_logger.info("Sending CANCEL parent order to EO ==>" + poEvent + "<==");
		try {
			m_exectnOptimizer.onParentOrderEvent(poEvent);
			m_logger.info("Cancel SENT!");
			if (m_future != null)
				m_future.cancel(true);
		} catch (Exception e) {
			m_logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Randomly return 0, 1 or 2
	 * 
	 * @return
	 */
	private static int rand012() {
		return new java.util.Random().nextInt(3);
	}

	/**
	 * Randomly return 0, 1 or 2
	 * 
	 * @return
	 */
	private static int rand123() {
		return 1 + rand012();
	}

	public void setFuture(final ScheduledFuture<?> future) {
		m_future = future;
	}

	// newOrder("jpm1milla", exectnOptimizer, stockFile, "JPM", Side.Buy, 1000050)
	public static TickAndOrderGenerator newOrder(final String blockID, final IExecutionOptimizer exectnOptimizer, final PSStockFile stockFile, final String ticker,
			final Side side, final int blockQty) {
		final PSStockFile.Rec rec = stockFile.getStockRecByTicker(ticker);
		if (rec == null) {
			m_logger.error("Cannot locate ticker [" + ticker + "] in PS stockfile [" + stockFile.getFileName() + "]");
			return null;
		}
		final ParentOrderMsg jpm = genParentOrder(blockID, side, blockQty, ticker, 0);
		return new TickAndOrderGenerator(exectnOptimizer, ticker, rec.Q, rec.s, jpm);
	}

	/**
	 * Buy 50000 AAPL @ 270 (limit)
	 * 
	 * @param blockID
	 * @param side
	 * @param blockQty
	 * @param ticker
	 * @param limitPx
	 * @return
	 */
	private static ParentOrderMsg genParentOrder(final String blockID, final Side side, final int blockQty, final String ticker, final double limitPx) {
		final ParentOrderMsg poMsg = new ParentOrderMsg();
		poMsg.blockGenTime = new Date();
		poMsg.blockID = blockID;
		poMsg.side = side;
		poMsg.blockQty = blockQty;
		poMsg.securityID = ticker;
		poMsg.ticker = ticker;
		poMsg.limitPrice = limitPx;
		poMsg.borrowLocateString = null;
		poMsg.deskID = "desk1";
		poMsg.PM = "Denniss Kidd";
		poMsg.PMGroup = "group1";
		poMsg.restrictedBrokers = Arrays.asList("CANT", "WACH", "MSCO"); // dont send this order to these brokers
		poMsg.timeInForce = TimeInForce.DAY;
		final AlgoParams p = new AlgoParamsVWAP();
		p.setStartTime(new DateTime());
		p.setEndTime(DateUtils.getUSMarketCloseTime());
		// p.setRiskTolerance(3); // For now
		p.setLowerPct(null); // Use default!
		p.setUpperPct(null); // Use default
		// TODO: replace with onOpenAmount: p.setOpenPercent(null); // Use default
		p.setParticipateOnClose(true);
		p.setParticipateOnOpen(true);
		poMsg.algoParams = p;

		return poMsg;
	}

	public void setSendQuotes(final boolean tf) {
		m_sendQuotes = tf;
	}

}
