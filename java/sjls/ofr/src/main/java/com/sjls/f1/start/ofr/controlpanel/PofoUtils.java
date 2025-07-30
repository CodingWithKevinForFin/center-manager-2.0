package com.sjls.f1.start.ofr.controlpanel;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.f1.pofo.fix.OrdStatus;
import com.sjls.controlpanel.protobuf.messages.ControlPanelMsgs.*;

public class PofoUtils {
    public static String CVS_ID = "$Id: PofoUtils.java,v 1.1.1.1 2014/02/07 20:55:02 olu Exp $";

    public static List<FIXTag> toCustomTags(final Map<Integer, String> passThruTags) {
        final LinkedList<FIXTag> list = new LinkedList<FIXTag>();
        if(passThruTags != null) {
            for(Map.Entry<Integer, String> entry : passThruTags.entrySet()) {
                list.add(FIXTag.newBuilder().setTag(entry.getKey()).setValue(entry.getValue()).build());
            }
        }
        return list;
    }

    
    public static  LimitPriceMsg toLimitPriceMsg(final String blockId, final double limitPx) {
        final LimitPriceMsg.Builder bldr = LimitPriceMsg.newBuilder();
        bldr.setBlockId(blockId);
        bldr.setLimitPrice(limitPx);
        return bldr.build();
        
    }

    public static  Side toCPSide(final com.f1.pofo.oms.Side side) {
        if(side == com.f1.pofo.oms.Side.BUY) return Side.BUY;
        if(side == com.f1.pofo.oms.Side.SELL) return Side.SELL;
        if(side == com.f1.pofo.oms.Side.SHORT_SELL) return Side.SELLSHORT;
        throw new IllegalArgumentException(String.format("Unknown Side [%s]", side));
    }

    public static  OrderType toCPOrderType(final com.f1.pofo.oms.OrderType orderType) {
        if (orderType == com.f1.pofo.oms.OrderType.LIMIT)  return   OrderType.LIMIT;
        if (orderType == com.f1.pofo.oms.OrderType.MARKET)  return   OrderType.MARKET;
        throw new IllegalArgumentException(String.format("Unknown OrderType [%s]", orderType));
    }

    public static boolean isEmpty(final String str) {
        return str==null || str.isEmpty();
    }

    
    public static CPStatus getCPStatus(final com.f1.pofo.oms.Order order) {
        final int forgeStatus=order.getOrderStatus();
        CPStatus cpStatus = CPStatus.Pending; //by default
        if((forgeStatus & OrdStatus.PENDING_ACK.getIntMask())>0 ){
            cpStatus=CPStatus.Pending;
            if ((forgeStatus & OrdStatus.REJECTED.getIntMask())>0){
                cpStatus=CPStatus.Rejected;
            }
        }
        else if((forgeStatus & OrdStatus.ACKED.getIntMask())>0 ){
            if ((forgeStatus & OrdStatus.PARTIAL.getIntMask())>0){
                cpStatus=CPStatus.Idle;
            }
            if((forgeStatus & OrdStatus.PENDING_RPL.getIntMask())>0 ){
                cpStatus=CPStatus.Pending;
            }
            if((forgeStatus & OrdStatus.REPLACED.getIntMask())>0 ){
                cpStatus=CPStatus.Pending;
            }
            if((forgeStatus & OrdStatus.PENDING_CXL.getIntMask())>0 ){
                cpStatus=CPStatus.Pending;
            }
            if((forgeStatus & OrdStatus.CANCELLED.getIntMask())>0 ){
                cpStatus=CPStatus.Canceled;
            }
            if((forgeStatus & OrdStatus.FILLED.getIntMask())>0 ){
                cpStatus = CPStatus.Filled;
            }
        }
        else if((forgeStatus & OrdStatus.REJECTED.getIntMask())>0 ){
            cpStatus=CPStatus.Rejected;
        }
        return cpStatus;
    }

}
