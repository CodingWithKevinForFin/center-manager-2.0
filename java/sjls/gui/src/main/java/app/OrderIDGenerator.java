package app;


public class OrderIDGenerator {
    private static OrderIDGenerator m_instance = new OrderIDGenerator();
    
    private int m_nextID = 1;
    
    public static OrderIDGenerator getInstance() {
	return m_instance;
    }
    
    public synchronized String genNewID(final String reqPool) {
	return "jpm_eo_" + reqPool + "-" + m_nextID++;
    }
}
