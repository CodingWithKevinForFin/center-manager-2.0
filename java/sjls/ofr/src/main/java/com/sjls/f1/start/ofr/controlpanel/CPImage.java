package com.sjls.f1.start.ofr.controlpanel;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import com.google.protobuf.Message;



public class CPImage implements IBlockOrdersSnapshotProvider {
    public static String CVS_ID = "$Id: CPImage.java,v 1.1.1.1 2014/02/07 20:55:02 olu Exp $";
    public final static Logger m_logger = Logger.getLogger(CPImage.class);

    private final ConcurrentHashMap<String, BlockOrder> m_map = new ConcurrentHashMap<String, BlockOrder>();
    private final IMsgPublisher m_publisher;

    public CPImage(final IMsgPublisher cpServer) {
        m_publisher = cpServer;
    }


    public void onNewOrder(final com.f1.pofo.oms.Order order) {
        try {
            final BlockOrder blockOrder = new BlockOrder(order);
            m_map.put(blockOrder.getBlockId(), blockOrder);
            if(m_logger.isDebugEnabled()) {
                m_logger.debug(String.format("Received New pofo order [%s]", order));
            }
        }
        catch(Exception e) {
            m_logger.error(String.format("onNewOrder(): Error [%s] while processing order [%s]",  e.getMessage(), order), e);
        }
    }

    
    /** 
     * This is the runnable that is run whenever we need to publish updates 
     */
    public TimerTask getRefreshTask() {
        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    doRun();
                }
                catch(Exception e) {
                    m_logger.error(e.getMessage(), e);
                }
            }

            private void doRun() {
                for (BlockOrder block : m_map.values()) {
                    m_publisher.publish(block.getAndResetUpdates());
                }
                if(m_logger.isDebugEnabled()) m_logger.debug("Just published msgs to CP...");
            }
        };
        return task;
    }


    public Collection<BlockOrder> getAllBlocks() {
        return m_map.values();
    }


    /**
     * Returns all OFR block data as list of protobuf msgs
     */
    @Override
    public List<Message> getSnapShot() {
        final List<Message> list = new LinkedList<Message>();
        for (BlockOrder block : m_map.values()) {
            list.addAll(block.snapshot());
        }
        return list;
    }


    
    public BlockOrder getBlockOrder(final String blockID) {
        final BlockOrder block = m_map.get(blockID);
        if(block == null) {
            throw new RuntimeException(String.format("Cannot locate block [%s]", blockID));
        }
        return block;
    }
}
