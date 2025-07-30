package com.sjls.f1.start.ofradapter;

import com.f1.pofo.oms.Order;
import com.sjls.algos.eo.common.ICancelRejectedMsg;
import com.sjls.algos.eo.common.IFIX42Msg;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.joda.time.DateTime;
import com.sjls.algos.eo.common.OrderStatus;
import com.sjls.algos.eo.common.OrderType;
import com.sjls.algos.eo.common.Side;
import com.sjls.algos.eo.common.TimeInForce;




public class CancelRejectWrapper implements ICancelRejectedMsg {
    public static String CVS_ID = "$Id: CancelRejectWrapper.java,v 1.1.1.1 2014/02/07 20:55:02 olu Exp $";

    private final String m_ticker;
    private final String m_blockId;
    private final String m_broker;
    private final String m_venue;
    private final String m_clOrdID;
    private final String m_origClOrdID;
    private final OrderStatus m_status;
    private final Side m_side;
    private final int orderQty;
    private final OrderType orderType;
    private final double m_limitPx;
    private final TimeInForce m_tif;
    private final String m_text;
    private final String m_cxlRejReason;
    private final DateTime m_txnTime;


    /**
     * Create a CancelRejectedMsg
     * @param o
     * @param origClOrdID
     * @param clOrdID
     */
    public CancelRejectWrapper(final Order order, final Builder bldr) {
        m_origClOrdID = bldr.origClOrdID;
        m_clOrdID = bldr.clOrdID;
        m_text = bldr.text;
        m_cxlRejReason = bldr.cxlRejReason;
        m_ticker = order.getSymbol();
        m_blockId = order.getOrderGroupId();
        m_broker = order.getSessionName();
        m_venue = order.getDestination();
        m_status = OFRAdapterUtil.toSJLOrderStatus(order.getOrderStatus());
        m_side = OFRAdapterUtil.toSJLSide(order.getSide());
        orderQty = order.getOrderQty();
        m_limitPx = order.getLimitPx();
        m_tif = OFRAdapterUtil.toSJLTimeInForce(order.getTimeInForce());
        orderType = OFRAdapterUtil.toSJLOrderType(order.getOrderType());
        m_txnTime = new DateTime(); // inner2.getCreatedTime(); TODO: 3forge needs to pass this thru from FIX
    }


    @Override
    public String getTicker() {
        return m_ticker;
    }

    @Override
    public String getBlockID() {
        return m_blockId;
    }

    @Override
    public String getBroker() {
        return m_broker;
    }

    @Override
    public String getVenue() {
        return m_venue;
    }

    @Override
    public String getClOrdID() {
        return m_clOrdID;
    }



    @Override
    public String getOrigClOrdID() {
        return m_origClOrdID;
    }


    
    @Override
    public OrderStatus getOrderStatus() {
        return m_status;
    }

    
    @Override
    public Side getSide() {
        return m_side;
    }

    
    
    @Override
    public int getOrderQty() {
        return orderQty;
    }

    
    
    @Override
    public OrderType getOrdType() {
        return orderType;
    }

    @Override
    public double getPrice() {
        return m_limitPx;
    }
    
    
    @Override
    public TimeInForce getTimeInForce() {
        return m_tif;
    }

    @Override
    public String getCxlRejectReason() {
        return m_cxlRejReason;
    }
    
    @Override
    public String getText() {
        return m_text;
    }
    

    @Override
    public Date getTransactTime() {
        return m_txnTime.toDate();
    }


    public IFIX42Msg clone() {
        return this;
    }


    public String toString() {        
        final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return String.format("BlockID=[%s], clOrdId=[%s], OrigClOrdID=[%s], OrderStatus=[%s], ticker=[%s], side=[%s], orderQty=[%d], " +
                "orderType=[%s], Price=[%.4f], TIF=[%s], Broker=[%s], Venue=[%s],  " +
                "CxlRejectedReason=[%s], Text=[%s], TransactionTime=[%s]", 
                nullToEmpty(getBlockID()), nullToEmpty(getClOrdID()), nullToEmpty(getOrigClOrdID()), nullToEmpty(getOrderStatus()), nullToEmpty(getTicker()), nullToEmpty(getSide()), getOrderQty(),
                nullToEmpty(getOrdType()), getPrice(), nullToEmpty(getTimeInForce()), nullToEmpty(getBroker()), nullToEmpty(getVenue()),  
                nullToEmpty(getCxlRejectReason()),  nullToEmpty(getText()), fmt.format(getTransactTime()));
    }

    
    private static Object nullToEmpty(Object val) {
        return ((val == null) ? "" : val);
    }

    
    public static class Builder {
        public String clOrdID;
        public String origClOrdID;
        public String text;
        public String cxlRejReason;
    }
}
