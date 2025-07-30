package com.sjls.f1.sjlscommon;

import java.io.IOException;

import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;
import com.sjls.algos.eo.common.AlgoParamsIS;

/**
 * Class to handle serialization/deserialization for AlgoParamsIS
 * @author Olu Emuleomo 2012-05-09
 *
 */
public class AlgoParamsConverterIS extends AlgoParamsConverterCommon {
    
    public void write(final AlgoParamsIS algoParams, final ToByteArrayConverterSession session) throws IOException {
        super.write(algoParams, session);
        session.getConverter().write( new Integer(algoParams.getRiskTolerance()), session);
    }

    
    public void read(final AlgoParamsIS algoParams, final FromByteArrayConverterSession session) throws IOException {
        super.read(algoParams, session);
        algoParams.setRiskTolerance((Integer) session.getConverter().read(session));
    }
}
