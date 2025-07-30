package com.sjls.f1.sjlscommon;

import java.io.IOException;

import org.joda.time.DateTime;

import com.f1.utils.converter.bytes.AbstractCustomByteArrayConverter;
import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;
import com.sjls.algos.eo.common.AlertMsg;
import com.sjls.algos.eo.common.AlertMsg.AlertLevel;


public class AlertMsgConverter extends AbstractCustomByteArrayConverter<AlertMsg> {

    public AlertMsgConverter() {
        super(AlertMsg.class, AlertMsg.class.getSimpleName());
    }

    @Override
    public void write(final AlertMsg o, final ToByteArrayConverterSession session) throws IOException {
        final ObjectToByteArrayConverter converter = session.getConverter();
        converter.write(o.getTimeStamp(), session);
        converter.write(o.getBlockID(), session);
        converter.write(o.getSliceID(), session);
        converter.write(o.getAlertLevel(), session);
        converter.write(o.getMsg(), session);
    }

    @Override
    public AlertMsg read(final FromByteArrayConverterSession session) throws IOException {
        final ObjectToByteArrayConverter converter = session.getConverter();
        final DateTime ts = (DateTime) converter.read(session);
        final String blockID = (String) converter.read(session);
        final String sliceID = (String) converter.read(session);
        final AlertLevel alertLevel = (AlertLevel) converter.read(session);
        final String msg = (String) converter.read(session);
        //
        return new AlertMsg(ts, alertLevel, blockID, msg).setSliceID(sliceID);
    }
}
