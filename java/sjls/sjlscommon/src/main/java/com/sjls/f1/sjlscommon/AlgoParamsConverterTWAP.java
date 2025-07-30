package com.sjls.f1.sjlscommon;

import java.io.IOException;

import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;
import com.sjls.algos.eo.common.AlgoParamsTWAP;

/**
 * Class to handle serialization/deserialization for AlgoParamsTWAP
 * @author Olu Emuleomo 2012-05-09
 *
 */
public class AlgoParamsConverterTWAP extends AlgoParamsConverterCommon {
    
    public void write(final AlgoParamsTWAP algoParams, final ToByteArrayConverterSession session) throws IOException {
        super.write(algoParams, session);
        session.getConverter().write( new Boolean(algoParams.isProfileTiltEnabled()), session );
    }

    
    public void read(final AlgoParamsTWAP algoParams, final FromByteArrayConverterSession session) throws IOException {
        super.read(algoParams, session);
        algoParams.setProfileTiltEnabled((Boolean) session.getConverter().read(session));
    }
}
