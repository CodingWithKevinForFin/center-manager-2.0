package com.sjls.f1.sjlscommon;

import java.io.IOException;

import org.joda.time.DateTime;

import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;
import com.sjls.algos.eo.common.AlgoParams;
import com.sjls.algos.eo.common.Amount;

public class AlgoParamsConverterCommon {

    /**
     * Write objects from AlgoParams into the stream session
     * @param algoParams
     * @param session
     * @throws IOException
     */
    protected void write(final AlgoParams algoParams, final ToByteArrayConverterSession session) throws IOException {
        final ObjectToByteArrayConverter converter = session.getConverter();
        converter.write(algoParams.getStartTime(), session);
        converter.write(algoParams.getEndTime(), session);
        converter.write(algoParams.getParticipateOnOpen(), session);
        converter.write(algoParams.getParticipateOnClose(), session);
        converter.write(algoParams.getOnOpenAmt(), session);
        converter.write(algoParams.getLowerPct(), session);
        converter.write(algoParams.getUpperPct(), session);
        converter.write(algoParams.getPaused(), session);
        converter.write(algoParams.getHardUpperPRIndicator(), session);
    }

    
    /**
     * Read Objects from the stream session and store them into AlgoParams
     * @param algoParams
     * @param session
     * @throws IOException
     */
    protected void read(final AlgoParams algoParams, final FromByteArrayConverterSession session) throws IOException {
        final ObjectToByteArrayConverter converter = session.getConverter();
        
        algoParams.setStartTime((DateTime) converter.read(session));
        algoParams.setEndTime((DateTime) converter.read(session));
        //
        final Boolean pOnOpen = (Boolean) converter.read(session);
        if(pOnOpen != null) algoParams.setParticipateOnOpen(pOnOpen);
        final Boolean pOnClose = (Boolean) converter.read(session);
        if(pOnClose != null) algoParams.setParticipateOnClose(pOnClose);
        //
        algoParams.setOnOpenAmt((Amount) converter.read(session));
        algoParams.setLowerPct((Double) converter.read(session));
        algoParams.setUpperPct((Double) converter.read(session));
        //
        final Boolean isPaused = (Boolean) converter.read(session);
        if(isPaused != null) algoParams.setIsPaused((Boolean) isPaused);
        //
        algoParams.setHardUpperPRIndicator((Boolean) converter.read(session));
    }

}
