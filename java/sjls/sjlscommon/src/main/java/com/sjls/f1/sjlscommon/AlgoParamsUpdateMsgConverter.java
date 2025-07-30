package com.sjls.f1.sjlscommon;


import java.io.IOException;

import com.f1.utils.converter.bytes.AbstractCustomByteArrayConverter;
import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;
import com.sjls.algos.eo.common.AlgoParamsUpdateMsg;
import com.sjls.algos.eo.common.IAlgoParams;

public class AlgoParamsUpdateMsgConverter extends AbstractCustomByteArrayConverter<AlgoParamsUpdateMsg> {
    public final static String CVS_ID = "$Id: AlgoParamsUpdateMsgConverter.java,v 1.1 2012/08/07 16:55:46 olu Exp $";
    
    public AlgoParamsUpdateMsgConverter() {
        super(AlgoParamsUpdateMsg.class, AlgoParamsUpdateMsg.class.getSimpleName());
    }

    @Override
    public void write(final AlgoParamsUpdateMsg o, final ToByteArrayConverterSession session) throws IOException {
        final ObjectToByteArrayConverter converter = session.getConverter();
        converter.write(o.getBlockID(), session);
        converter.write(o.getAlgoParams(), session);
    }

    @Override
    public AlgoParamsUpdateMsg read(final FromByteArrayConverterSession session) throws IOException {
        ObjectToByteArrayConverter converter = session.getConverter();
        final String blockID = (String) converter.read(session);
        final IAlgoParams params = (IAlgoParams) converter.read(session);//
        final AlgoParamsUpdateMsg r=new AlgoParamsUpdateMsg(blockID, params);
        return r;
    }
}
