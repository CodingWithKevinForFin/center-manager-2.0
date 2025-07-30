package com.sjls.f1.start.ofradapter;

public class OfrSliceId {
    private final String m_id;
    
    public OfrSliceId(final String id) {
        m_id = id;
    }

    @Override
    public String toString() {
        return m_id;
    }
    
    @Override
    public int hashCode() {
        return m_id.hashCode();
    }
    
    @Override
    public boolean equals(final Object that) {
        return (that instanceof OfrSliceId) && that !=null && that.toString().equals(m_id);
    }
}
