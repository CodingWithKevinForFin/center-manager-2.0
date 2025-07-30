package com.sjls.f1.start.ofradapter;

import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.Order;
import com.sjls.algos.eo.common.AlgoParams;
import com.sjls.algos.eo.common.EOUtils;
import com.sjls.algos.eo.common.IAlgoParams;
import com.sjls.algos.eo.common.IParentOrderEvent;
import com.sjls.algos.eo.common.LimitPrice;
import com.sjls.algos.eo.common.POEventType;
import com.sjls.algos.eo.common.Side;
import com.sjls.algos.eo.common.TimeInForce;

import java.util.Date;
import java.util.List;

public class ParentEventWrapper implements IParentOrderEvent {
    final Order m_inner;
    final POEventType m_eventType;
    final private int priorFilledQuantity;
    final private boolean m_isPaused;
    private static SJLSPassThruMapper sjlsPassThruMapper = new SJLSPassThruMapper();
    private final String m_routeToBrokerID;
    private final AlgoParams m_algoParams;

    public ParentEventWrapper(final Order inner, final OmsAction type,  final int priorFilledQuantity) throws Exception {
        this(inner, type, priorFilledQuantity, true); //true == ParentOrderAssumed paused by default. Olu E. 03/04/2013
    }

    public ParentEventWrapper(final Order inner, final OmsAction type, final int priorFilledQuantity, final boolean isPaused) throws Exception {
        this.priorFilledQuantity = priorFilledQuantity;
        switch (type) {
        case NEW_ORDER_RCVD:
            m_eventType = POEventType.NEW;
            break;
        case REPLACE_ORDER:
            m_eventType = POEventType.MODIFY;
            break;
        case CANCEL_ORDER:
            m_eventType = POEventType.CANCEL;
            break;
        default:
            throw new Exception(String.format("BlockID [%s]: Invalid Order EventType [%s]", inner.getId(), type));
        }
        m_inner = inner;
        m_isPaused = isPaused;
        m_routeToBrokerID = sjlsPassThruMapper.getRouteToBrokerID(inner.getPassThruTags());
        //
        m_algoParams = (AlgoParams) sjlsPassThruMapper.getAlgoParams(m_inner);
        m_algoParams.setIsPaused(m_isPaused);
        //NOTE special processing for START. Normally, MARKET orders are identified by LimitPrice==0.
        //However, for START, MARKET orders and limit price of zero are both regarded as limit price NOT specified
        //i.e. START will calculate the limit price
        //--Olu E. 2014-02-14
        if(m_inner.getOrderType() == com.f1.pofo.oms.OrderType.MARKET || m_inner.getLimitPx()<=0) {
            m_algoParams.setLimitPrice(null);
        }
        else {
            m_algoParams.setLimitPrice(new LimitPrice(m_inner.getLimitPx())); //LimitPx is an AlgoParam
        }
    }

    
    
    @Override
    /** Never returns null! */
    public IAlgoParams getAlgoParams() {
        return m_algoParams;
    }
    

    @Override
    public Date getBlockGenTime() {
        return m_inner.getCreatedTime().toDate();
    }

    @Override
    public String getBlockID() {
        return m_inner.getId();
    }

    @Override
    public long getBlockQty() {
        return m_inner.getOrderQty() - priorFilledQuantity;
    }

    @Override
    public String getBorrowLocateString() {
        return sjlsPassThruMapper.getBorrowLocateString(m_inner.getPassThruTags());
    }

    @Override
    public String getDeskID() {
        return sjlsPassThruMapper.getDeskID(m_inner.getPassThruTags());
    }

    @Override
    public POEventType getEventType() {
        return m_eventType;
    }

    /**
     * For now, Market Orders from the Buyside will mean absence of a Limit Price
     * The RulesEngine will then calculate a limit price for orders where the limit is not specified
     */
    @Override
    public LimitPrice getLimitPriceFromAlgoParams() {
        return getAlgoParams().getLimitPrice();
    }

    @Override
    public String getPM() {
        return sjlsPassThruMapper.getPM(m_inner.getPassThruTags());
    }

    @Override
    public String getPMGroup() {
        return sjlsPassThruMapper.getPMGroup(m_inner.getPassThruTags());
    }

    @Override
    public String getPMProduct() {
        return sjlsPassThruMapper.getPMProduct(m_inner.getPassThruTags());
    }

    @Override
    public String getPMStrategyCode() {
        return sjlsPassThruMapper.getPMStrategyCode(m_inner.getPassThruTags());
    }

    @Override
    public String getPMSubProduct() {
        return sjlsPassThruMapper.getPMSubProduct(m_inner.getPassThruTags());
    }

    @Override
    public String getPairLinkID() {
        return sjlsPassThruMapper.getPairLinkID(m_inner.getPassThruTags());
    }

    @Override
    public List<String> getRestrictedBrokers() {
        return sjlsPassThruMapper.getRestrictedBrokers(m_inner.getPassThruTags());
    }

    @Override
    public String getSecurityID() {
        return m_inner.getSecurityID();
    }

    @Override
    public Date getShortSettleDate() {
        return sjlsPassThruMapper.getShortSettleDate(m_inner.getPassThruTags());
    }

    @Override
    public Side getSide() {
        switch (m_inner.getSide()) {
        case BUY:
            return Side.Buy;
        case SELL:
            return Side.Sell;
        case SHORT_SELL:
            return Side.SellShort;
        case BUY_MINUS:
            return Side.BuyToCover;
        }
        return null;
    }

    @Override
    public String getTicker() {
        if (m_inner.getSymbolSfx() == null)
            return m_inner.getSymbol();
        else
            return m_inner.getSymbol() + "." + m_inner.getSymbolSfx();
    }

    @Override
    public TimeInForce getTimeInForce() {
        if(m_inner.getTimeInForce() != null) {
            switch (m_inner.getTimeInForce()) {
            case DAY:
                return TimeInForce.DAY;
            case FILL_OR_KILL:
                return TimeInForce.FOK;
            case GTC:
                return TimeInForce.GTC;
            case IOC:
                return TimeInForce.IOC;
            case ON_OPEN:
                return TimeInForce.ON_OPEN;
            case ON_CLOSE:
                return TimeInForce.ON_CLOSE;
            }
        }
        return TimeInForce.DAY;  //The default
    }


    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        final String SEP = "|";
        buf.append("blockID=").append(getBlockID()).append(SEP);
        buf.append("ticker=").append(getTicker()).append(SEP);
        buf.append("blockQty=").append(getBlockQty()).append(SEP);
        buf.append("limitPrice=").append(getLimitPriceFromAlgoParams()).append(SEP);
        buf.append("securityID=").append(getSecurityID()).append(SEP);
        buf.append("PM=").append(getPM()).append(SEP);
        buf.append("PMStrategyCode=").append(getPMStrategyCode()).append(SEP);
        buf.append("PMGroup=").append(getPMGroup()).append(SEP);
        buf.append("PMProduct=").append(getPMProduct()).append(SEP);
        buf.append("PMSubProduct=").append(getPMSubProduct()).append(SEP);
        buf.append("side=").append(getSide()).append(SEP);
        buf.append("timeInForce=").append(getTimeInForce()).append(SEP);
        buf.append("shortSettleDate=").append(EOUtils.toDateString(getShortSettleDate())).append(SEP);
        buf.append("deskID=").append(getDeskID()).append(SEP);
        buf.append("blockGenTime=").append(EOUtils.toDateTimeString(getBlockGenTime())).append(SEP);
        buf.append("borrowLocateString=").append(getBorrowLocateString()).append(SEP);
        buf.append("restrictedBrokers=").append(getRestrictedBrokers()).append(SEP);
        buf.append("pairLinkID=").append(getPairLinkID()).append(SEP);
        buf.append("RouteToBrokerID=").append(getRouteToBrokerID()).append(SEP);
        buf.append("AlgoParams**==>").append( getAlgoParams().toString(SEP)).append("<==**");
        return buf.toString();
    }

    @Override
    public String getRouteToBrokerID() {
        return m_routeToBrokerID;
    }

}
