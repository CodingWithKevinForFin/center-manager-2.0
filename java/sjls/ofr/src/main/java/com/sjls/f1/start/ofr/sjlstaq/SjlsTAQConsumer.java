package com.sjls.f1.start.ofr.sjlstaq;

import com.reuters.rfa.common.*;
import com.sjls.algos.eo.common.ITickData;
import com.sjls.algos.eo.common.PrimaryMarket;
import com.sjls.algos.eo.common.QuoteData;
import com.sjls.algos.eo.common.TradeData;
import com.sjls.f1.start.ofr.IMarketDataListener;
import com.sjls.f1.start.ofr.IMarketDataManager;
import com.sjls.sjlstaq.client.ITAQEventListner;
import com.sjls.sjlstaq.client.ITAQServerHandle;
import com.sjls.sjlstaq.client.QuoteClient;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class SjlsTAQConsumer implements IMarketDataManager, ITAQEventListner {
    final private Logger m_logger = Logger.getLogger(SjlsTAQConsumer.class);
    public final static String CVS_ID = "$Id: SjlsTAQConsumer.java,v 1.5 2014/09/19 17:25:42 olu Exp $";
    final private Map<String, Handle> itemHandlers = new HashMap<String, Handle>();

    private final List<IMarketDataListener> m_listeners = new CopyOnWriteArrayList<IMarketDataListener>();
    private boolean m_isConnected = false;
    private final ITAQServerHandle m_server;
    private Map<String, PrimaryMarket> m_primaryMktMap = new ConcurrentHashMap<String, PrimaryMarket>();


    public SjlsTAQConsumer(final String host, final int port) throws UnknownHostException, IOException {
        m_server = QuoteClient.connect(host, port, this);
    }


    public Set<String> getSubscribedNames() {
        synchronized (itemHandlers) {
            return new HashSet<String>(itemHandlers.keySet());
        }
    }


    /**
     * Subscribe to Symbol, RIC
     * @param symbol
     * @param ric
     * @throws Exception 
     */
    @Override
    public void subscribe(final String symbol, final String ric, final PrimaryMarket primaryMkt) throws Exception {
        m_logger.info(String.format("Subscribing to market data, symbol=%s, RIC=%s", symbol, ric)); //RIC is actually not used!
        m_primaryMktMap.put(symbol, primaryMkt); //record the primary mkt
        m_server.subscribe(symbol, new DateTime());
    }



    @Override
    public void addListener(IMarketDataListener listener) {
        m_listeners.add(listener);
    }

    public void removeListener(IMarketDataListener listener) {
        m_listeners.remove(listener);
    }


    public boolean isConnected() {
        return m_isConnected;
    }



    @Override
    public void onQuote(com.sjls.sjlstaq.IQuoteData q) {
        if (m_logger.isTraceEnabled()) m_logger.trace(String.format("Got Quote: %s", q));
        //
        final QuoteData quote = new QuoteData(q.getTicker(), new DateTime(q.getTimestamp()));
        quote.setBidPrice(q.getBidPrice());
        quote.setBidSize(q.getBidSize());
        quote.setAskPrice(q.getAskPrice());
        quote.setAskSize(q.getAskSize());
        publish(new ITickData[]{quote});
    }


    @Override
    public void onTrade(final com.sjls.sjlstaq.ITradeData t) {
        if (m_logger.isTraceEnabled()) m_logger.trace(String.format("Got Trade: %s", t));
        //
        final PrimaryMarket primaryMkt = m_primaryMktMap.get(t.getTicker());
        final TradeData trade = new TradeData(t.getTicker(), t.getTimestamp());
        trade.setTradePrice(t.getTradePrice());
        trade.setTradeSize(t.getTradeSize());
        trade.setAccumShares(t.getAccumShares());
        trade.setStatusCode(t.getStatusCode());
        if(t.getStatusCode() != null && t.getStatusCode().contains("O")) { //Then, this is an opening trade!
            m_logger.info("Got Trade with OPENING condition code " + t);
            trade.setOpeningTradeFlag(true);
        }
        //
        trade.setTradeVenue(t.getTradeVenue());
        boolean pmFlag = false;
        final String tradeVenue = t.getTradeVenue();
        if(tradeVenue!=null && primaryMkt!=null) {
            switch(primaryMkt) {
                case NYSE:   pmFlag = tradeVenue.equals("NYS");
                break;
                case NASDAQ: pmFlag = tradeVenue.equals("NAS");
                break;
                case ARCA:   pmFlag = tradeVenue.equals("PSE");
                break;
                case AMEX:   pmFlag = tradeVenue.equals("ASE");
                break;
            }
        }
        trade.setPrimaryMktFlag(pmFlag);
        trade.setRule201Flag(false); //NOTE: For now...need to fix Olu E.
        publish(new ITickData[]{trade});
    }


    @Override
    public void onError(final String ticker, final String errMsg) {
        m_logger.error(String.format("Ticker %s: %s", ticker, errMsg));
    }
    
    @Override
    public void onDisconnect(final String errMsg) {
        for(IMarketDataListener l : m_listeners) {
            l.onDisconnect(errMsg, true); //we want this disconnect propagated
        }
    }
    
    
    private void publish(final ITickData[] tq) {
        for(IMarketDataListener l : m_listeners) {
            l.onMarketData(tq);
        }
    }
}
