package com.sjls.f1.sjlscommon;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.f1.utils.converter.bytes.AbstractCustomByteArrayConverter;
import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;
import com.sjls.algos.eo.common.IAlgoParams;
import com.sjls.algos.eo.common.IBinTradeData;
import com.sjls.algos.eo.common.IParentOrderStatusUpdateMsg;
import com.sjls.algos.eo.common.ITCMEstimate;
import com.sjls.algos.eo.common.IParentOrderStatusUpdateMsg.UpdateType;
import com.sjls.algos.eo.common.ParentOrderStatusUpdateMsg;

public class ParentOrderStatusUpdateMsgConverter extends AbstractCustomByteArrayConverter<IParentOrderStatusUpdateMsg> {

    public ParentOrderStatusUpdateMsgConverter() {
        super(IParentOrderStatusUpdateMsg.class, IParentOrderStatusUpdateMsg.class.getSimpleName());
    }

    @Override
    public void write(final IParentOrderStatusUpdateMsg o, final ToByteArrayConverterSession session) throws IOException {
        ObjectToByteArrayConverter converter = session.getConverter();
        converter.write(new Double(o.getLimitPrice()), session);
        converter.write(o.getAlgoParams(), session);
        converter.write(o.getBinTradeInfo(), session);
        converter.write(o.getBlockID(), session);
        converter.write(o.getTCMEstimate(), session);
        converter.write(o.getTradingPlan(), session);
        converter.write(o.getUpdateType(), session);
        converter.write(o.getRouteToBrokerID(), session);
        converter.write(o.getUserdata(), session);
    }

    @Override
    public IParentOrderStatusUpdateMsg read(final FromByteArrayConverterSession session) throws IOException {
        final ObjectToByteArrayConverter converter = session.getConverter();
        final Double limitPrice = (Double) converter.read(session);
        final IAlgoParams algoParams = (IAlgoParams) converter.read(session);
        final IBinTradeData binTradeInfo = (IBinTradeData) converter.read(session);
        final String blockID = (String) converter.read(session);
        final ITCMEstimate estimate = (ITCMEstimate) converter.read(session);
        final List<IBinTradeData> tradingPlan = (List<IBinTradeData>) converter.read(session);
        final UpdateType updateType = (UpdateType) converter.read(session);
        final String routeToBrokerID = (String) converter.read(session);
        final Map<String, Object> userData = (Map<String, Object>) converter.read(session);
        //
        final ParentOrderStatusUpdateMsg r = new ParentOrderStatusUpdateMsg(updateType, blockID, algoParams);
        r.setLimitPrice(limitPrice);
        r.setBinTradeInfo(binTradeInfo);
        r.setTCMEstimate(estimate);
        r.setTradingPlan(tradingPlan);
        r.setRouteToBrokerID(routeToBrokerID);
        if (userData != null) r.getUserdata().putAll(userData);
        return r;
    }
}
