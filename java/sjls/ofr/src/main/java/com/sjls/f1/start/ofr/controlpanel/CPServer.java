package com.sjls.f1.start.ofr.controlpanel;


import java.io.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.net.*;

import org.apache.log4j.Logger;

import com.google.protobuf.Message;
import com.sjls.controlpanel.protobuf.io.DefaultMsgCodec;
import com.sjls.controlpanel.protobuf.io.IControPanelMsgListener;
import com.sjls.controlpanel.protobuf.io.IMsgCodec;
import com.sjls.f1.start.ofr.brokeralloc.BrokerAllocMgr;

/**
 * 
 * @author Olumide O. Emuleomo
 * Aug 1, 2013
 */
public class CPServer extends Thread implements IMsgPublisher
{
    public final static String CVS_ID = "$Id: CPServer.java,v 1.1.1.1 2014/02/07 20:55:02 olu Exp $";

    private static Logger m_logger = Logger.getLogger(CPServer.class);

    private final int m_port;
    private final IControPanelMsgListener m_msgListener;

    private final ConcurrentHashMap<String, CPInstance> m_cpInstances = new ConcurrentHashMap<String, CPInstance>();

    private IMsgCodec m_codec;

    private volatile IBlockOrdersSnapshotProvider m_snapShotProvider;
    private volatile BrokerAllocMgr m_brokerAllocMgr;


    public CPServer(final int adminPort, final IControPanelMsgListener msgListener) {
        super("CONTROLPANEL_SERVER");
        m_port = adminPort;
        m_msgListener = msgListener;
        m_codec = new DefaultMsgCodec(); //Codec to use
    }
    

    public int getPort() {
        return m_port;
    }

    
    public void run() {
        final String name = getName();
        ServerSocket serverSocket=null;
        try {
            serverSocket = new ServerSocket(m_port);
            String instanceName = null;
            final String waiting = "Accepting ControlPanel connections on port " + m_port;
            m_logger.info(waiting);
            while (true) {
                try {
                    final Socket skt = serverSocket.accept();
                    m_logger.info("A Control Panel connected: Socket is " + skt);
                    instanceName = String.format("%s:%s", skt.getInetAddress().getHostName(), skt.getPort());
                    final CPInstance cpInstance = new CPInstance(instanceName, skt, m_msgListener, m_codec, m_snapShotProvider, m_brokerAllocMgr);
                    m_cpInstances.put(cpInstance.getName(), cpInstance);
                    cpInstance.start();
                }
                catch (IOException io) {
                    m_logger.info("Could not startup CP Instance [" + instanceName + "]");
                    m_logger.error(io);
                }
            }
        }
        catch (Exception e) {
            m_logger.error(e.getMessage(), e);
            closeQuietly(serverSocket);
        }
        finally {
            m_logger.info("THREAD [" + name + "EXITTING!!!");
        }
    }


    private void closeQuietly(final ServerSocket skt) {
        try {  if(skt!=null) skt.close(); }
        catch (IOException e1) {
            m_logger.error(e1);
        }
    }
    
    
    
    @Override
    public void publish(final List<Message> msgs) {
        if(msgs==null || msgs.isEmpty()) return;
        //
        for(final Message protobufMsg : msgs) {
            publish(protobufMsg);
        }
    }
    
    
    @Override
    public void publish(final Message msg) {
        if(msg==null) return;
        //
        if(m_logger.isDebugEnabled()) {
            m_logger.debug(String.format("Publishing msg [%s] to all ContolPanels...==>[%s]<==", m_codec.getClassNameFor(msg), msg));
        }
        //
        try {
            final byte[] bytes = m_codec.encodeMsg(msg);
            for(CPInstance cp : m_cpInstances.values()) {
                cp.write(bytes);
                if(cp.hasIOError()) {
                    m_cpInstances.remove(cp.getName());
                    m_logger.info(String.format("ControlPanel instance [%s] removed becasue it has an IO error!", cp.getName()));
                }
            }
        }
        catch(Exception e) {
            m_logger.error(e.getMessage(), e);
        }
    }


    public void setSnapshotProvider(final IBlockOrdersSnapshotProvider provider) {
        m_snapShotProvider = provider;
    }


    public void setBrokerAllocMgr(final BrokerAllocMgr brokerAllocMgr) {
        m_brokerAllocMgr = brokerAllocMgr;        
    }
}
