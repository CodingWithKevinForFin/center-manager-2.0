package com.sjls.f1.sjlscommon;

import java.io.IOException;

import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;
import com.sjls.algos.eo.common.AlgoParamsVWAP;

/**
 * Class to handle serialization/deserialization for AlgoParamsVWAP
 * @author Olu Emuleomo 2012-05-09
 *
 */
public class AlgoParamsConverterVWAP extends AlgoParamsConverterCommon {
    
    public void write(final AlgoParamsVWAP algoParams, final ToByteArrayConverterSession session) throws IOException {
        super.write(algoParams, session);
        session.getConverter().write( new Boolean(algoParams.isProfileTiltEnabled()), session );
    }

    
    public void read(final AlgoParamsVWAP algoParams, final FromByteArrayConverterSession session) throws IOException {
        super.read(algoParams, session);
        algoParams.setProfileTiltEnabled((Boolean) session.getConverter().read(session));
    }
}
