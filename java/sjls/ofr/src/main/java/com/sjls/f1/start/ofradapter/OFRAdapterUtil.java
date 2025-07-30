package com.sjls.f1.start.ofradapter;

import com.f1.pofo.fix.OrdStatus;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.Order;
import com.f1.pofo.oms.OrderType;
import com.f1.pofo.oms.Side;
import com.f1.pofo.oms.TimeInForce;
import com.f1.utils.MH;
import com.sjls.algos.eo.common.AlgoPrice;
import com.sjls.algos.eo.common.ExecType;
import com.sjls.algos.eo.common.OrderStatus;

public class OFRAdapterUtil {

    public static String CVS_ID = "$Id: OFRAdapterUtil.java,v 1.1.1.1 2014/02/07 20:55:02 olu Exp $";

    public static Side to3ForgeSide(com.sjls.algos.eo.common.Side side) {
        switch(side){
        case Buy: return Side.BUY;
        case Sell: return Side.SELL;
        case SellShort: return Side.SHORT_SELL;
        }
        return null;
    }

    public static OrderType to3ForgeOrderType(final AlgoPrice price) {
        if (price.getPegType()==AlgoPrice.PegType.NO_PEG){
            if (price.isMktOrder()) return OrderType.MARKET;
            else return OrderType.LIMIT;
        }
        return OrderType.PEGGED;
    }

    public static TimeInForce to3ForgeTIF(com.sjls.algos.eo.common.TimeInForce timeInForce) {
        switch(timeInForce){
        case DAY: return TimeInForce.DAY;
        case ON_CLOSE:return TimeInForce.ON_CLOSE;
        case ON_OPEN: return TimeInForce.ON_OPEN;
        case IOC: return TimeInForce.IOC;
        case FOK:return TimeInForce.FILL_OR_KILL;
        }
        return null;
    }

    
    
    
    public static com.sjls.algos.eo.common.OrderType toSJLOrderType(final com.f1.pofo.oms.OrderType ordType) {
        switch (ordType) {
        case LIMIT:
            return com.sjls.algos.eo.common.OrderType.Limit;
        case MARKET:
            return com.sjls.algos.eo.common.OrderType.Market;
        case PEGGED:
            break;
        case STOP:
            break;
        case STOP_LIMIT:
            break;
        default:
            break;
        }
        return null;
    }

    public static com.sjls.algos.eo.common.TimeInForce toSJLTimeInForce(final com.f1.pofo.oms.TimeInForce tif) {
        switch (tif) {
        case DAY:
            return com.sjls.algos.eo.common.TimeInForce.DAY;
        case ON_CLOSE:
            return com.sjls.algos.eo.common.TimeInForce.ON_CLOSE;
        case ON_OPEN:
            return com.sjls.algos.eo.common.TimeInForce.ON_OPEN;
        case IOC:
            return com.sjls.algos.eo.common.TimeInForce.IOC;
        case FILL_OR_KILL:
            return com.sjls.algos.eo.common.TimeInForce.FOK;
        case GTC:
            break;
        case GTD:
            break;
        default:
            break;
        }
        return null;
    }
    

    public static com.sjls.algos.eo.common.Side toSJLSide(final com.f1.pofo.oms.Side side) {
        switch (side) {
        case BUY:
            return com.sjls.algos.eo.common.Side.Buy;
        case SELL:
            return com.sjls.algos.eo.common.Side.Sell;
        case SHORT_SELL:
            return com.sjls.algos.eo.common.Side.SellShort;
        case SHORT_SELL_EXEMPT:
            return com.sjls.algos.eo.common.Side.SellShort;
        case BUY_MINUS:
            return com.sjls.algos.eo.common.Side.BuyToCover;
        }
        return null;
    }
    

    public static OrderStatus toSJLOrderStatus(final int status) {
        final int bit = MH.indexOfLastBitSet(status);
        final OrdStatus fixstatus = OrdStatus.get(bit);
        switch (fixstatus) {
        case ACKED:
            return OrderStatus.New;
        case CANCELLED:
            return OrderStatus.Canceled;
        case REJECTED:
            return OrderStatus.Rejected;
        case PENDING_ACK:
            return OrderStatus.PdgNew;
        case PENDING_CXL:
            return OrderStatus.PdgCancel;
        case PARTIAL:
            return OrderStatus.PartiallyFilled;
        case FILLED:
            return OrderStatus.Filled;
        case REPLACED:
            return OrderStatus.Replaced;
        case PENDING_RPL:
            return OrderStatus.PdgReplace;
        default:
            break;
        }
        return null;
    }

    
    public static ExecType toSJLExecType(final Order o, final Execution exec) {
        if(exec != null) {
            return o.getOrderQty() == o.getTotalExecQty() ? ExecType.Fill : ExecType.PartialFill;
        } 
        // Not a fill or a partial fill
        final int status = o.getOrderStatus();
        if (OrdStatus.PENDING_CXL.isSet(status)) {
            return ExecType.PdgCancel;
        } 
        else if (OrdStatus.PENDING_RPL.isSet(status)) {
            return ExecType.PdgReplace;
        } 
        else if (OrdStatus.CANCELLED.isSet(status)) {
            return ExecType.Canceled;
        } 
        else if (OrdStatus.REPLACED.isSet(status)) {
            return ExecType.Replace;
        } 
        else if (OrdStatus.REJECTED.isSet(status)) {
            return ExecType.Rejected;
        } 
        else if (OrdStatus.PENDING_ACK.isSet(status)) {
            return ExecType.PdgNew;
        } 
        else if (OrdStatus.ACKED.isSet(status)) {
            return ExecType.New;
        } 
        else if (OrdStatus.FILLED.isSet(status)) {
            return ExecType.Fill;
        }
        else return null;
    }
}
