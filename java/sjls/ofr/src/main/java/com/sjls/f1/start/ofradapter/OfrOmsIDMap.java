package com.sjls.f1.start.ofradapter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* Would like to use a bidimap, but apache bidimap looks wierd! */
public class OfrOmsIDMap {
    final private Map<String, String> m_OfrToOmsId = new ConcurrentHashMap<String, String>();
    final private Map<String, String> m_OmsToOfrId = new ConcurrentHashMap<String, String>();
    
    /** Map OfrId --> OmsId and vice-versa*/
    public synchronized void mapOfrIdToOmsId(final String ofrId, final String omsId) {
        m_OfrToOmsId.put(ofrId, omsId);
        m_OmsToOfrId.put(omsId,  ofrId);
    }
    
    
    /**
     * Returns an OMS Id given an ofrId
     * @param ofrId
     * @return
     */
    public String getByOfrID(final String ofrId) {
        return m_OfrToOmsId.get(ofrId);
    }
    
    /**
     * Returns an OFR Id given a OMS Id
     * @param omsId
     * @return
     */
    public String getByOmsID(final String omsId) {
        return m_OmsToOfrId.get(omsId);
    }

}
