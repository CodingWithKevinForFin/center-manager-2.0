package com.sjls.f1.sjlscommon;

import java.io.IOException;

import com.f1.utils.converter.bytes.AbstractCustomByteArrayConverter;
import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;
import com.sjls.algos.eo.common.IStrategyUpdateMsg;
import com.sjls.algos.eo.common.IStrategyUpdateMsg.UpdateType;
import com.sjls.algos.eo.common.Speed;
import com.sjls.algos.eo.common.StrategyUpdateMsg;


public class IStrategyUpdateMsgConverter extends AbstractCustomByteArrayConverter<IStrategyUpdateMsg> {

    public IStrategyUpdateMsgConverter() {
        super(IStrategyUpdateMsg.class, IStrategyUpdateMsg.class.getSimpleName());
    }

    @Override
    public void write(final IStrategyUpdateMsg o, final ToByteArrayConverterSession session) throws IOException {
        ObjectToByteArrayConverter converter = session.getConverter();
        converter.write(o.getLimitPrice(), session);
        converter.write(o.getSpeed(), session);
        converter.write(o.getBlockID(), session);
        converter.write(o.getUpdateType(), session);
        converter.write(o.isAutotrade(), session);
        converter.write(o.mustCompleteByEOD(), session);
    }

    @Override
    public IStrategyUpdateMsg read(final FromByteArrayConverterSession session) throws IOException {
        ObjectToByteArrayConverter converter = session.getConverter();

        final double limitPrice = (Double) converter.read(session);
        final Speed speed = (Speed) converter.read(session);
        final String blockID = (String) converter.read(session);
        final UpdateType updateType = (UpdateType) converter.read(session);//
        final boolean autotrade = (Boolean) converter.read(session);
        final boolean tf = (Boolean) converter.read(session);
        //
        final StrategyUpdateMsg r=new StrategyUpdateMsg(blockID);
        r.setLimitPrice(limitPrice);
        r.setSpeed(speed);
        r.setUpdateType(updateType);
        r.setAutotrade(autotrade);
        r.setMustCompleteByEODFlag(tf);
        return r;
    }
}
