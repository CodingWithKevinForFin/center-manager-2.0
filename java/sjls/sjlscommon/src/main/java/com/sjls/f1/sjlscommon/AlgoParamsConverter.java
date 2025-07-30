package com.sjls.f1.sjlscommon;

import java.io.IOException;

import com.f1.utils.converter.bytes.AbstractCustomByteArrayConverter;
import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;
import com.sjls.algos.eo.common.AlgoParamsIS;
import com.sjls.algos.eo.common.AlgoParamsTWAP;
import com.sjls.algos.eo.common.AlgoParamsVWAP;
import com.sjls.algos.eo.common.IAlgoParams;

public class AlgoParamsConverter extends AbstractCustomByteArrayConverter<IAlgoParams> {

    public final static String IS_ALGO = "AlgoParamsIS";
    public final static String VWAP_ALGO = "AlgoParamsVWAP";
    public final static String TWAP_ALGO = "AlgoParamsTWAP";
    
    public AlgoParamsConverter() {
        super(IAlgoParams.class, IAlgoParams.class.getSimpleName());
    }

    
    @Override
    public void write(final IAlgoParams algoParams, final ToByteArrayConverterSession session) throws IOException {
        if (algoParams instanceof AlgoParamsIS) {
            session.getConverter().write( IS_ALGO, session);
            new AlgoParamsConverterIS().write((AlgoParamsIS)algoParams, session);
        }
        else if (algoParams instanceof AlgoParamsVWAP) {
            session.getConverter().write( VWAP_ALGO, session);
            new AlgoParamsConverterVWAP().write((AlgoParamsVWAP)algoParams, session);
        }
        else if (algoParams instanceof AlgoParamsTWAP) {
            session.getConverter().write( TWAP_ALGO, session);
            new AlgoParamsConverterTWAP().write((AlgoParamsTWAP)algoParams, session);
        }
        else {
            throw new IOException("AlgoParamsConverter.write(): Unknown class ["+algoParams.getClass().getCanonicalName()+"]");
        }
    }
    
    

    
    @Override
    public IAlgoParams read(final FromByteArrayConverterSession session) throws IOException {
        final ObjectToByteArrayConverter converter = session.getConverter();

        final String algo = (String) converter.read(session);
        if(algo == null) return null;
        //
        if (algo.equals(IS_ALGO)) {
            final AlgoParamsIS algoParams = new AlgoParamsIS();
            new AlgoParamsConverterIS().read(algoParams, session);
            return algoParams;
        }
        //
        if (algo.equals(VWAP_ALGO)) {
            final AlgoParamsVWAP algoParams = new AlgoParamsVWAP();
            new AlgoParamsConverterVWAP().read(algoParams, session);
            return algoParams;
        }
        //
        if (algo.equals(TWAP_ALGO)) {
            final AlgoParamsTWAP algoParams = new AlgoParamsTWAP();
            new AlgoParamsConverterTWAP().read(algoParams, session);
            return algoParams;
        }
        //
        throw new IOException("AlgoParamsConverter.read(): Unknown class ["+algo+"]");
    }

}
