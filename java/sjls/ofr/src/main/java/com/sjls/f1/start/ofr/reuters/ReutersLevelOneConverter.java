package com.sjls.f1.start.ofr.reuters;

import com.reuters.rfa.omm.OMMData;
import com.reuters.rfa.omm.OMMEntry;
import com.reuters.rfa.omm.OMMFieldEntry;
import com.reuters.rfa.omm.OMMIterable;
import com.reuters.rfa.omm.OMMMsg;
import com.reuters.rfa.omm.OMMNumeric;
import com.reuters.rfa.omm.OMMTypes;
import com.sjls.algos.eo.common.ITickData;
import com.sjls.algos.eo.common.PrimaryMarket;
import com.sjls.algos.eo.common.QuoteData;
import com.sjls.algos.eo.common.TradeData;
import org.apache.log4j.Logger;

import java.util.Iterator;

public class ReutersLevelOneConverter {
    private  final static Logger m_logger = Logger.getLogger(ReutersLevelOneConverter.class);
    public final static String CVS_ID = "$Id: ReutersLevelOneConverter.java,v 1.5 2014/09/10 14:10:21 nhudson Exp $";

    
    public ITickData[] convert(final OMMMsg event, final String symbol, final String ricCode, final PrimaryMarket primaryMkt) {
        final OMMData payload = event.getPayload();

        if (payload == null || ! (payload instanceof OMMIterable))  return null; //nothing of interest here
        //
        //Log the tick. Snapshot info will always be printed
        if(event.getMsgType()==OMMMsg.MsgType.REFRESH_RESP) { //Snapshot event
            m_logger.info(String.format("Snapshot msg: convert(%s), RIC=%s,  ==>%s<==", symbol, ricCode, ReutersMessageParser.toString(event)));
        }
        else {
            if(m_logger.isTraceEnabled()) m_logger.trace(String.format("convert(%s): RIC=%s, ==>%s<==", symbol, ricCode, ReutersMessageParser.toString(event)));
        }

        TradeData trade = null;
        QuoteData quote = null;
        Double openPx = null;
        String openExchID = null;
        StringBuilder irregBldr = null;
        String rule201Str = null;
        int irgVol = 0;
        double irgPrice = 0;
        String irgCond = null;
        String irgExchID = null;
        
        
        final Iterator<OMMEntry> payLoadIter = ((OMMIterable) payload).iterator();
        while(payLoadIter.hasNext()) {
            OMMEntry entry = payLoadIter.next();
            if (entry.getType()==OMMTypes.FIELD_ENTRY) {
                final OMMFieldEntry fe = (OMMFieldEntry) entry;
                switch(fe.getFieldId()){
                case 6: //TRDPRC_1   "LAST"
                    final double tradePx = toDouble(fe);
                    if(tradePx > 0) {
                        if(trade==null) trade=new TradeData(symbol);
                        trade.setTradePrice(tradePx);
                    }
                    break;
                case 19: //OPEN_PRC   "OPENING PRICE"
                    openPx = toDouble(fe);
                    break;
                case 22: //BID        "BID"
                    if(quote==null) quote = new QuoteData(symbol);
                    quote.setBidPrice(toDouble(fe));
                    break;
                case 25: //ASK        "ASK"
                    if(quote==null) quote = new QuoteData(symbol);
                    quote.setAskPrice(toDouble(fe));
                    break;
                case 30: //BIDSIZE    "BID SIZE"
                    if(quote==null) quote = new QuoteData(symbol);
                    quote.setBidSize((int)toLong(fe));
                    break;
                case 31: //ASKSIZE    "ASK SIZE"
                    if(quote==null) quote = new QuoteData(symbol);
                    quote.setAskSize((int) toLong(fe));
                    break;
                case 32: //ACVOL_1    "VOL ACCUMULATED"
                    if(trade==null) trade=new TradeData(symbol);
                    trade.setAccumShares(toLong(fe));
                    break;
                case 44: //TRDXID_1
                    if(trade==null) trade=new TradeData(symbol);
                    trade.setTradeVenue( ReutersMessageParser.enumToString(fe));
                    break;
                case 178: //TRDVOL_1   "TRADE VOL"
                    if(trade==null) trade=new TradeData(symbol);
                    trade.setTradeSize((int) toLong(fe));
                    break;
                case 4756: //LSTSALCOND
                    if(trade==null) trade=new TradeData(symbol);
                    trade.setStatusCode(toString(fe));
                    break;
                case 199: //OPENXID
                    openExchID = ReutersMessageParser.enumToString(fe);
                    break;
                case 3853: //TRDTIM_MS  TODO
                    //double dbl = ((OMMNumeric)fe.getData()).toDouble();
                    break;
                case 3855: //QUOTIM_MS  TODO
                    //double dbl = ((OMMNumeric)fe.getData()).toDouble();
                    break;
                case 372: //IRGPRC
                    irgPrice = toDouble(fe);
                    if(irgPrice > 0) {
                        if(irregBldr==null) irregBldr=new StringBuilder(symbol);
                        irregBldr.append(" IRGPRC=").append(irgPrice);
                    }
                    break;
                case 373: //IRGVOL
                    irgVol = (int) toLong(fe);
                    if(irgVol > 0) {
                        if(irregBldr==null) irregBldr=new StringBuilder(symbol);
                        irregBldr.append(" IRGVOL=").append(irgVol);
                    }
                    break;
                case 374: //IRGCOND
                    irgCond = trim(ReutersMessageParser.enumToString(fe));
                    if(irgCond != null && irgCond.length() > 0) {
                        if(irregBldr==null) irregBldr=new StringBuilder(symbol);
                        irregBldr.append(" IRGCOND=").append(irgCond);
                    }
                    break;
                case 1018: //IRGXID
                    irgExchID = trim(ReutersMessageParser.enumToString(fe));
                    if(irgExchID != null && irgExchID.length() > 0) {
                        if(irregBldr==null) irregBldr=new StringBuilder(symbol);
                        irregBldr.append(" IRGXID=").append(irgExchID);
                    }
                    break;
                case 1044: //GV4_FLAG
                    rule201Str = trim(toString(fe));
                    if(rule201Str != null && rule201Str.length() > 0) {
                        if(irregBldr==null) irregBldr=new StringBuilder(symbol);
                        irregBldr.append(" GV4_FLAG=").append(rule201Str);
                    }
                    break;
                }
            }
        }


        if(trade!=null) {
            trade.setRIC(ricCode);
            //
            //check for ODD lots!
            if(trade.getTradeSize() == 0 && irgVol > 0) {
                trade.setTradeSize(irgVol);
                if(irgPrice > 0) trade.setTradePrice(irgPrice);
                if(irgExchID != null) trade.setTradeVenue(irgExchID);
            }
            //
            boolean pmFlag = false;
            final String tradeVenue = trade.getTradeVenue();
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

            if(trade.getStatusCode() != null && trade.getStatusCode().contains("O")) { //Then, this is an opening trade!
                if(openPx != null && openPx > 0) trade.setTradePrice(openPx);
                if(trade.getTradeVenue()==null) trade.setTradeVenue(openExchID);
                trade.setOpeningTradeFlag(true);
                m_logger.info("Got Trade with OPENING condition code " + trade);
            }

            if (m_logger.isTraceEnabled()) m_logger.trace(String.format("Got Trade: %s", trade));

            if(irregBldr != null) irregBldr.append(" from Trade: ").append(trade.toString());
        }

        if(quote != null) {
            quote.setRIC(ricCode);
            if (m_logger.isTraceEnabled()) m_logger.trace(String.format("Got Quote: %s", quote));
            if(irregBldr != null) irregBldr.append(" from Quote: ").append(quote.toString());
        }
        
        if(rule201Str != null) {
            if(trade==null) {
                m_logger.warn(String.format("Got Rule201 string:[%s] on a quote!", rule201Str));
            }
            else {
                trade.setRule201Flag(rule201Str.equals("E")||rule201Str.equals("C"));
                m_logger.info(String.format("Got Rule201 string:[%s]. Trade==>%s", rule201Str, trade));
            }
        }

        if(irregBldr != null) {
            if(m_logger.isDebugEnabled()) m_logger.debug("Got Irregular Field: " + irregBldr.toString());
        }

        return new ITickData[]{trade, quote};
    }
    
    
    private static double toDouble(final OMMFieldEntry fe) {
        return ((OMMNumeric)fe.getData(OMMTypes.REAL)).toDouble();
    }
    
    private static long toLong(final OMMFieldEntry fe) {
        return ((OMMNumeric)fe.getData(OMMTypes.REAL)).toLong();
    }
    private static String toString(final OMMFieldEntry fe) {
        //return ((OMMDataBuffer)fe.getData()).toString();
        return fe.getData().toString();
    }
    
    /** Trim the input string if not null. Otherwise return null */
    private static String trim(final String str) {
        return str == null ? null : str.trim();
    }
    
}
