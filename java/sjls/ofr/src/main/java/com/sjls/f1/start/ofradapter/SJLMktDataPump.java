package com.sjls.f1.start.ofradapter;


import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.sjls.algos.eo.common.EOException;
import com.sjls.algos.eo.common.IExecutionOptimizer;
import com.sjls.algos.eo.common.QuoteData;
import com.sjls.algos.eo.common.TradeData;
import com.sjls.algos.eo.core.PSStockFile;
import com.sjls.algos.eo.core.PSStockFile.Rec;
import com.sjls.algos.eo.utils.DirectoryIterator;
import com.sjls.algos.eo.utils.TimerPool;
import com.sjls.tcm.pretrade.util.FileLoadUtil;


/**
 * Class to supply fake mkt data to OFR
 * @author Olu Emuleomo 2012-07-14
 *
 */
public class SJLMktDataPump {

    public final static String CVS_ID = "$Id: SJLMktDataPump.java,v 1.2 2014/02/07 22:46:00 olu Exp $";
    public final static Logger m_logger = Logger.getLogger(SJLMktDataPump.class);    

    private final static String MARKETDATA_FILE = "MARKETDATA_FILE";

    private final TimerPool m_timer = new TimerPool();
    private final IExecutionOptimizer m_execnOptimzer;
    private List<String> m_mktdataUniverse = null;

    private PSStockFile m_stockFile;
    
    private static DateTime NINE_29_50 = new DateTime().withTime(9, 29, 50, 0);
    private static DateTime NINE_30 = new DateTime().withTime(9, 30, 0, 0);
    private static DateTime NINE_31 = new DateTime().withTime(9, 31, 0, 0);
    private static DateTime NINE_32 = new DateTime().withTime(9, 32, 0, 0);
    private static DateTime NINE_35 = new DateTime().withTime(9, 35, 0, 0);


    public SJLMktDataPump(final IExecutionOptimizer eo) throws EOException {
        m_execnOptimzer = eo;
        DirectoryIterator dataDirIter;
        try {
            dataDirIter = new DirectoryIterator(System.getProperty("DATA_DIR"));
            m_stockFile = new PSStockFile(dataDirIter);
            final String mktdataFile = System.getProperty(MARKETDATA_FILE);
            if (mktdataFile==null) throw new EOException("Need to define system property [" + MARKETDATA_FILE + "]");
            m_mktdataUniverse = readStockUniverse(mktdataFile);
        } catch (IOException e) {
            m_logger.error("Error while processing constructing SJLMktDataPumpw with: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }


    /**
     * Start pumping mkt data. We initially support only MSFT, ZVZZT and PRU
     */
    public void startWork() {   
        for(String ticker : m_mktdataUniverse){
            String trimmedTicker = ticker.replace("/", ".");
            Rec stock = m_stockFile.findStockRecByTicker(trimmedTicker);

            if(stock == null){
                m_logger.warn("******* SJLMktDataPump will not pump ==> " + trimmedTicker + " as it is not in the stock universe!");
                continue;
            }

            m_timer.scheduleAtFixedRate(
                    new FakeMktData(m_execnOptimzer, stock.ticker, (stock.ric!=null && stock.ric.length()>0 ? stock.ric : stock.ticker), stock.Q, (int)stock.s),
                    200, 
                    300);
        }
    }


    static class FakeMktData implements Runnable {
        private final IExecutionOptimizer m_exectnOptimizer;
        private final String m_ticker;
        private final String m_ric;
        private volatile double m_bidPx;
        private final double m_bidPx0;
        private final double m_spreadHint;
        private volatile int m_count = 0;
        private volatile int m_accumShares = 0;
        private volatile boolean m_isOpeningTradeAlreadySent = false;


        public FakeMktData(final IExecutionOptimizer eo, final String ticker, final String ric, final double initialBidPrice, final int spreadHintInCents) {
            m_exectnOptimizer = eo;
            m_ticker = ticker;
            m_ric = ric;
            m_bidPx0 = m_bidPx = initialBidPrice;
            m_spreadHint = Math.max(spreadHintInCents/100.0, 0.01);
        }

        @Override
        public void run() {
            try {
                doRun();
            }
            catch(Exception e) {
                m_logger.error(e.getMessage(), e);
            }
        }


        private void doRun() throws EOException {
            m_count++;
            m_bidPx = m_bidPx == 0 ? 0 : m_bidPx + (rand012()-1)*Math.max(0.01, m_spreadHint/4.0); //move up, down by max of 1/4 of spreadHint
            if ( 100.0*Math.abs(m_bidPx - m_bidPx0)/m_bidPx0 > 5) m_bidPx = m_bidPx0; //dont drift more than 5%

            final double askPx = m_bidPx == 0 ? 0 : m_bidPx + (m_spreadHint*(10-rand012())/10.0);
            //
            if ( m_count > 0  && (m_count%5 == 0) ) { //Trade
                final TradeData trade = new TradeData(m_ticker);
                trade.setRIC(m_ric);
                trade.setTradePrice((m_bidPx+askPx)/2.0);
                final TradeVenueInfo tvi = getTradeVenueInfo(m_ticker);
                trade.setTradeSize(tvi.tradeSize);
                trade.setTradeVenue(tvi.venue);
                trade.setPrimaryMktFlag(tvi.isPrimaryMkt);
                trade.setOpeningTradeFlag(tvi.isOpeningTrade);
                //
                m_accumShares += trade.getTradeSize();
                trade.setAccumShares(m_accumShares);
                if (m_logger.isDebugEnabled()) m_logger.debug("Sending trade [" + trade + "]");
                m_exectnOptimizer.onTrade(trade);
                //
                if (m_logger.isDebugEnabled()) m_logger.debug("Done. SENT trade [" + trade + "]");
            }
            else { //Quote
                final QuoteData data = new QuoteData(m_ticker);
                data.setRIC(m_ric);
                data.setBidSize(100*rand123());
                data.setAskSize(100*rand123());
                data.setBidPrice(round2(m_bidPx));
                data.setAskPrice(round2(askPx));    
                //
                if (m_logger.isDebugEnabled()) m_logger.debug("Sending quote [" + data + "]");
                //
                m_exectnOptimizer.onQuote(data);
                //
                if (m_logger.isDebugEnabled()) m_logger.debug("Done. SENT quote [" + data + "]");
            }
        }


        private TradeVenueInfo getTradeVenueInfo(final String ticker) {
            final TradeVenueInfo tvi = new TradeVenueInfo();
            final DateTime now = new DateTime();
            if(ticker.equals("MSFT") ) {
                tvi.isOpeningTrade = m_isOpeningTradeAlreadySent ? false : now.isAfter(NINE_30) && now.isBefore(NINE_31);
                tvi.tradeSize = tvi.isOpeningTrade ? 500000 : 500*rand123();
                tvi.isPrimaryMkt = true; //always trades on primary
                tvi.venue = "NAS";
            }
            else if(ticker.equals("IBM") ) {
                tvi.isOpeningTrade = m_isOpeningTradeAlreadySent ? false : now.isAfter(NINE_29_50) && now.isBefore(NINE_30);
                tvi.tradeSize = tvi.isOpeningTrade ? 600000 : 600*rand123();
                tvi.isPrimaryMkt = true; //always trades on primary
                tvi.venue = "NYS";
            }
            else if(ticker.equals("PRU") ) {
                tvi.isOpeningTrade = m_isOpeningTradeAlreadySent ? false : now.isAfter(NINE_32) && now.isBefore(NINE_32.plusMinutes(1));
                if(tvi.isOpeningTrade || rand0to9() < 7) {
                    tvi.isPrimaryMkt = true;
                }
                else {
                    tvi.isPrimaryMkt = false;
                }
                tvi.venue = tvi.isPrimaryMkt ? "NYS" : "THM";
                tvi.tradeSize = tvi.isOpeningTrade ? 200000 : 200*rand123();
            }
            else if(ticker.equals("ADBE") ) {
                tvi.isOpeningTrade = m_isOpeningTradeAlreadySent ? false : now.isAfter(NINE_30) && now.isBefore(NINE_31);
                tvi.isPrimaryMkt = false; //never trades on primary
                tvi.venue = "THM";
                tvi.tradeSize = tvi.isOpeningTrade ? 50000 : 100*rand123();
            }
            else if(ticker.equals("PSX") ) {
                tvi.isOpeningTrade = false;
                tvi.isPrimaryMkt = false; //never trades on primary
                tvi.venue = "THM";
                tvi.tradeSize = 100*rand123();
            }
            else if(ticker.equals("XOM") ) {
                tvi.isOpeningTrade = m_isOpeningTradeAlreadySent ? false : now.isAfter(NINE_35) && now.isBefore(NINE_35.plusMinutes(1));
                tvi.isPrimaryMkt = tvi.isOpeningTrade || m_isOpeningTradeAlreadySent || now.isAfter(NINE_35.plusMinutes(1));
                tvi.venue = tvi.isPrimaryMkt ? "NYS" : "THM";
                tvi.tradeSize = tvi.isOpeningTrade ? 1000000 : 700*rand123();
            }
            else if(ticker.equals("C") ) {
                tvi.isOpeningTrade = m_isOpeningTradeAlreadySent ? false : now.isAfter(NINE_30) && now.isBefore(NINE_31);
                tvi.isPrimaryMkt = tvi.isOpeningTrade ? false : (m_isOpeningTradeAlreadySent || now.isAfter(NINE_31)) && rand0to9() < 8;
                tvi.venue = tvi.isPrimaryMkt ? "NYS" : "THM";
                tvi.tradeSize = tvi.isOpeningTrade ? 2000000 : 100*rand123();
            }
            else if(ticker.equals("INTC") ) {
                tvi.isOpeningTrade = m_isOpeningTradeAlreadySent ? false : now.isAfter(NINE_31) && now.isBefore(NINE_32);
                tvi.isPrimaryMkt = tvi.isOpeningTrade ? false : (m_isOpeningTradeAlreadySent || now.isAfter(NINE_31)) && rand0to9() < 8;
                tvi.venue = tvi.isPrimaryMkt ? "NAS" : "THM";
                tvi.tradeSize = tvi.isOpeningTrade ? 2000000 : 100*rand123();
            }
            else {
                tvi.isOpeningTrade = m_isOpeningTradeAlreadySent ? false : now.isAfter(NINE_30) && now.isBefore(NINE_31);
                tvi.tradeSize = tvi.isOpeningTrade ? 10000*rand123() : 100*rand123();
                tvi.isPrimaryMkt = (tvi.isOpeningTrade || m_isOpeningTradeAlreadySent || now.isAfter(NINE_31)) && rand0to9() < 8; //mostly trades on primary
                tvi.venue = "THM";
            }
            //
            if(tvi.isOpeningTrade) {
                m_logger.info(String.format("%s: ********OPENING TRADE SIZE set to [%s]. Primary=[%s]**************", ticker, tvi.tradeSize, tvi.isPrimaryMkt));
                m_isOpeningTradeAlreadySent = true;
            }
            return tvi;
        }

    }

    /**
     * Randomly return 0...9 inclusive
     * @return
     */
    private static int rand0to9() {
        return new java.util.Random().nextInt(10);
    }
    
    
    /**
     * Randomly return 0, 1 or 2
     * @return
     */
    private static int rand012() {
        return new java.util.Random().nextInt(3);
    }

    /**
     * Randomly return 0, 1 or 2
     * @return
     */
    private static int rand123() {
        return 1 + rand012();
    }


    private static double round2(final double val) {
        return  Math.rint(val*100d)/100d;
    }

    
    /**
     * read the universe of stokcs to provide mkt data for
     * @param stockFile
     * @return
     * @throws IOException
     */
    public static List<String> readStockUniverse(final String stockFile) throws IOException {
        m_logger.info("Loading market data stock universe from file " + stockFile + " ...");
        List<String> orderStrings = FileLoadUtil.loadData(new File(stockFile));
        List<String> tickerList = new LinkedList<String>();
        for (String orderData : orderStrings) {
            if (orderData.startsWith("#"))
                continue;
            try {
                tickerList.add(orderData.split(",")[0]);
            } catch (Exception e) {
                e.printStackTrace();
                m_logger.error("Error while processing [" + orderData + "]  " + e.getMessage(), e);
            }
        }
        return tickerList;
    }

    private static class TradeVenueInfo {
        public int tradeSize;
        public String venue;
        public boolean isPrimaryMkt;
        public boolean isOpeningTrade;
    }

}
