package com.sjls.f1.start.ofradapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.f1.pofo.oms.ChildOrderRequest;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.Order;
import com.sjls.algos.eo.common.ExecTransType;
import com.sjls.algos.eo.common.ExecType;
import com.sjls.algos.eo.common.IExecutionReportMsg;
import com.sjls.algos.eo.common.IFIX42Msg;
import com.sjls.algos.eo.common.OrderStatus;
import com.sjls.algos.eo.common.OrderType;
import com.sjls.algos.eo.common.Side;
import com.sjls.algos.eo.common.TimeInForce;

public class ExecutionReport implements IExecutionReportMsg {
    public static String CVS_ID = "$Id: ExecutionReport.java,v 1.1.1.1 2014/02/07 20:55:02 olu Exp $";
    public final static Logger m_logger = Logger.getLogger(ExecutionReport.class);
    
    private final Execution exec;
    private final String ticker;
    private final String blockId;
    private final String broker;
    private final String venue;
    private final String clOrdId;
    private final String origRequestId;
    private final OrderStatus status;
    private final Side side;
    private final int orderQty;
    private final OrderType orderType;
    private final double limitPx;
    private final TimeInForce tif;
    private final int totExecQty;
    private final double totValue;
    private final String lastMkt;
    private final Date transactTime;
    private final ExecType execType;
    private final String text;
    private final Integer rejReason;
    private final int cumQty;
    private final int leavesQty;
    private final int execTransType;
    private final String execID;
    private final String execRefID;
    
    public ExecutionReport(
            final Order order, 
            final Execution exec, 
            final ExecType execType, 
            final OrderStatus status,
            final String origClOrdID,
            final String clOrdID) {
        if(m_logger.isDebugEnabled()) {
            m_logger.debug(String.format("ExecutionReport.ctor(): RawOrder==>%s<==, Exec==>%s<==, ExecType=[%s], OrderStatus=[%s], OrigClOrdID=[%s], ClOrdID=[%s]",
                    order, exec, execType, status, origClOrdID, clOrdID));
        }
        this.exec = exec;
        this.execType = execType;
        this.status = status;
        this.origRequestId = origClOrdID;
        this.clOrdId = clOrdID;
        //
        this.ticker = order.getSymbol();
        this.blockId = order.getOrderGroupId();
        this.broker = order.getSessionName();
        this.venue = order.getDestination();
        this.side = OFRAdapterUtil.toSJLSide(order.getSide());
        this.orderQty = order.getOrderQty();
        this.limitPx = order.getLimitPx();
        this.totExecQty = order.getTotalExecQty();
        this.totValue = order.getTotalExecValue();
        this.tif = OFRAdapterUtil.toSJLTimeInForce(order.getTimeInForce());
        this.orderType = OFRAdapterUtil.toSJLOrderType(order.getOrderType());
        this.leavesQty = isOpen(order) ? order.getOrderQty() - order.getTotalExecQty() : 0;
        this.cumQty = order.getTotalExecQty();
        this.text = order.getText();
        this.rejReason = null;
        if(exec==null) {
            this.lastMkt = null;
            this.execTransType = 0;
            this.execID = null;
            this.execRefID = null;
        }
        else {
            //this.lastMkt = exec.getLastMkt();
            this.lastMkt = extractTag(30, exec.getPassThruTags());
            this.execTransType = exec.getExecTransType();
            this.execID = exec.getId();
            this.execRefID = exec.getExecRefID();
        }
        this.transactTime = exec == null ? new Date() : exec.getExecTime().toDate();
    }


    public ExecutionReport(final Order order, final Execution exec, final OmsAction action) {
        this(order, 
             exec, 
             OFRAdapterUtil.toSJLExecType(order, exec),
             action==OmsAction.CHILD_REPLACE_SUCCEEDED ? OrderStatus.Replaced : OFRAdapterUtil.toSJLOrderStatus(order.getOrderStatus()),
                     getTag41(order, exec, action),
                     order.getRequestId());
    }
    
    
    
    public ExecutionReport(final Order order, final OmsAction action) {
        this(order, null, action);
    }

    /**
     * Construct a 'Order Rejected' Execution Report
     * @param order
     * @param req
     */
    public ExecutionReport(final Order order, final ChildOrderRequest req, final int rejectReason, final String text) {
        if(m_logger.isDebugEnabled()) {
            m_logger.debug(String.format("ExecutionReport.ctor(): RawOrder==>%s<==, ChildOrderRequest==>%s<==, RejReason=[%s], Text=[%s]",
                    order, req, rejectReason, text));
        }
        this.exec = null;
        this.execType = ExecType.Rejected;
        this.status = OrderStatus.Rejected;
        this.origRequestId = null;
        this.clOrdId = req.getRequestId();
        //
        this.ticker = order.getSymbol();
        this.blockId = order.getOrderGroupId();
        this.broker = order.getSessionName();
        this.venue = req.getDestination();
        this.side = OFRAdapterUtil.toSJLSide(req.getSide());
        this.orderQty = req.getOrderQty();
        this.limitPx = req.getLimitPx();
        this.totExecQty = 0;
        this.totValue = 0;
        this.tif = OFRAdapterUtil.toSJLTimeInForce(req.getTimeInForce());
        this.orderType = OFRAdapterUtil.toSJLOrderType(req.getOrderType());
        this.cumQty = 0;
        this.leavesQty = 0;
        this.lastMkt = null;
        this.text = text;
        this.rejReason = rejectReason;
        this.execTransType = 0;
        this.execID = null;
        this.execRefID = null;
        this.transactTime = new Date();
    }

    
    private static String getTag41(final Order o, final Execution exec2, final OmsAction action) {
        switch(action) {
        case REPLACE_CHILD_ORDER:
        case CHILD_REPLACE_SUCCEEDED:
        case CHILD_REPLACE_REJECTED:
            return o.getOrigRequestId();
        default:
            return null;
        }
    }
    
    
    private static boolean isOpen(final Order order) {
        final OrderStatus status = OFRAdapterUtil.toSJLOrderStatus(order.getOrderStatus());
        return (status==OrderStatus.Canceled || 
           status==OrderStatus.Rejected ||
           status==OrderStatus.Canceled ||
           status==OrderStatus.DoneForDay)==false;
    }

    @Override
    public String getTicker() {
        return ticker;
    }

    @Override
    public String getBlockID() {
        return blockId;
    }

    @Override
    public String getBroker() {
        return broker;
    }

    @Override
    public String getVenue() {
        return venue;
    }

    @Override
    public String getClOrdID() {
        return clOrdId;
    }


    @Override
    public String getOrigClOrdID() {
        switch (getOrderStatus()) {
        case Canceled:
        case PdgCancel:
            return clOrdId;
        default:
            return origRequestId;
        }
    }


    @Override
    public OrderStatus getOrderStatus() {
        return this.status;
    }
        
    
    
    @Override
    public Side getSide() {
        return this.side;
    }

    @Override
    public int getOrderQty() {
        return orderQty;
    }

    
    
    @Override
    public OrderType getOrdType() {
        return this.orderType;
    }

    @Override
    public double getPrice() {
        return limitPx;
    }

    @Override
    public TimeInForce getTimeInForce() {
        return this.tif;
    }
    
    
    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public Date getTransactTime() {
        return (Date)transactTime.clone();
    }

    @Override
    public double getAvgPrice() {
        if (totExecQty > 0)
            return totValue / totExecQty;
        return 0;
    }

    @Override
    public int getLastShares() {
        if (exec != null)
            return exec.getExecQty();
        return 0;
    }

    @Override
    public double getLastPrice() {
        if (exec != null)
            return exec.getExecPx();
        return 0;
    }

    @Override
    public int getCumQty() {
        return this.cumQty;
    }

    @Override
    public int getLeavesQty() {
        return this.leavesQty; // TODO: Adjust for status
    }

    @Override
    public String getOrderRejectReason() {
        return String.valueOf(this.rejReason);
    }

    @Override
    public ExecType getExecType() {
        return this.execType;
    }   
       
    @Override
    public String getExecID() {
        return this.execID;
    }   
    
    @Override
    public String getExecRefID() {
        return this.execRefID;
    }   
       

    @Override
    public String getLastMkt() {
        return lastMkt;
    }

    public IFIX42Msg clone() {
        return this;
    }

    private static String extractTag(final int tagNum, final Map<Integer, String> passThruTags) {
        return passThruTags==null ? null : passThruTags.get(tagNum);
    }

    
    @Override
    /**
     * Note. Only ExecTransType of New and Cancel are supported!!
     */
    public ExecTransType getExecTransType() {
        return execTransType==1 ? ExecTransType.Cancel : ExecTransType.New;
    }
    
    public String toString() {        
        final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return String.format("BlockID=[%s], clOrdId=[%s], OrigClOrdID=[%s], ExecType=[%s], OrderStatus=[%s], ticker=[%s], side=[%s], orderQty=[%d], " +
        		"orderType=[%s], Price=[%.4f], TIF=[%s], Broker=[%s], Venue=[%s], avgPx=[%.4f], lastShares=[%d], lastPx=[%.4f], lastMkt=[%s], " +
                "cumQty=[%d], leavesQty=[%d], RejectReason=[%s], Text=[%s], ExecID=[%s], ExecTransType=[%s], ExecRefID=[%s], TransactionTime=[%s], RawExec==>%s<==", 
                nullToEmpty(getBlockID()), nullToEmpty(getClOrdID()), nullToEmpty(getOrigClOrdID()), nullToEmpty(getExecType()), nullToEmpty(getOrderStatus()), nullToEmpty(getTicker()), nullToEmpty(getSide()), getOrderQty(),
                nullToEmpty(getOrdType()), getPrice(), nullToEmpty(getTimeInForce()), nullToEmpty(getBroker()), nullToEmpty(getVenue()), getAvgPrice(), getLastShares(), getLastPrice(), nullToEmpty(getLastMkt()), 
                getCumQty(), getLeavesQty(), nullToEmpty(getOrderRejectReason()), nullToEmpty(getText()), nullToEmpty(getExecID()), getExecTransType().name(), nullToEmpty(getExecRefID()), fmt.format(getTransactTime()), exec);
    }

    private static Object nullToEmpty(Object val) {
            return ((val == null) ? "" : val);
    }
}
