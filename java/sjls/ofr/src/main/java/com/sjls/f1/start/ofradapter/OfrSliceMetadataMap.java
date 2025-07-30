package com.sjls.f1.start.ofradapter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.f1.pofo.oms.ChildOrderRequest;


public class OfrSliceMetadataMap {
    public final static Logger m_logger = Logger.getLogger(OfrSliceMetadataMap.class);
    
    private final Map<OfrSliceId, SliceMetadata> m_metadataMap = new ConcurrentHashMap<OfrSliceId, SliceMetadata>();
    private final Map<OmsSliceId, OfrSliceId>    m_omsToOfr = new ConcurrentHashMap<OmsSliceId, OfrSliceId>();
    private final Map<OfrSliceId, OmsSliceId>    m_ofrToOms = new ConcurrentHashMap<OfrSliceId, OmsSliceId>();

    public OfrSliceId getClOrdId(final OfrSliceId tag41) {
        final SliceMetadata metadata = m_metadataMap.get(tag41);
        return metadata==null ? null : metadata.newSliceId;
    }
    
    public String getClOrdIdStr(final OfrSliceId tag41) {
        final OfrSliceId sliceId =  getClOrdId(tag41);
        return sliceId==null ? null : sliceId.toString();
    }


    public SliceMetadata getSliceMetadata(final OfrSliceId sliceId) {
        return m_metadataMap.get(sliceId);
    }

    

    public void add(final SliceMetadata sliceMetadata) {
        m_metadataMap.put(sliceMetadata.ofrSliceId, sliceMetadata);
    }

    
    public void storeCxlRplInfo(final String blockID, final ChildOrderRequest child) {
        final OfrSliceId tag41 = new OfrSliceId(child.getOrigRequestId());
        final OfrSliceId tag11 = new OfrSliceId(child.getRequestId());
        final SliceMetadata sliceMetadata = getSliceMetadata(tag41);
        if (sliceMetadata==null) {
            m_logger.error(String.format("storeCxlRplInfo(): BlockID=[%s] SliceId=[%s] Cannot locate Slice MetaData!!", blockID, tag41)); 
        }
        else {
            sliceMetadata.newSliceId = tag11;  //OrigClOrdID --> ClOrdID
        }
        add(new SliceMetadata(tag11, blockID)); //This 'new' slice also needs to be recorded!
    }

    

    public String getBlockId(final OfrSliceId ofrId) {
        final SliceMetadata smd = ofrId==null ? null : getSliceMetadata(ofrId);
        return smd==null ? null : smd.blockId;
    }

    public String getBlockId(final OmsSliceId omsSliceId) {
        return getBlockId(m_omsToOfr.get(omsSliceId));
    }

    

    public void mapOfrIdToOmsId(final OmsSliceId omsSliceId, final OfrSliceId ofrSliceId) {
        m_omsToOfr.put(omsSliceId, ofrSliceId);
        m_ofrToOms.put(ofrSliceId, omsSliceId);
    }

    
    public OfrSliceId getOfrIdFor(final OmsSliceId omsSliceId) {
        return m_omsToOfr.get(omsSliceId);
    }

    public OmsSliceId getOmsIdFor(final OfrSliceId ofrSliceId) {
        return m_ofrToOms.get(ofrSliceId);
    }

    
    /**
     * Keep some information on Ofr slices
     * @author Olu Emuleomo
     *
     */
    static class SliceMetadata {
        public final OfrSliceId ofrSliceId;
        public final String blockId;
        public OmsSliceId omsId;

        /** This is the 'next' slice ID created as a result of Cxl/Rpl */
        public OfrSliceId newSliceId;

        /**
         * Create Slicemetadata object with ofrSliceId and it's blockId
         * @param ofrSliceId
         * @param blockId
         */
        public SliceMetadata(final OfrSliceId ofrSliceId, final String blockId) {
            this.ofrSliceId = ofrSliceId;
            this.blockId = blockId;
        }
    }
}