package com.sjls.f1.start.ofr.reuters;

import com.reuters.rfa.common.*;
import com.reuters.rfa.dictionary.FieldDictionary;
import com.reuters.rfa.omm.*;
import com.reuters.rfa.rdm.RDMInstrument;
import com.reuters.rfa.rdm.RDMMsgTypes;
import com.reuters.rfa.rdm.RDMUser;
import com.reuters.rfa.session.omm.OMMConsumer;
import com.reuters.rfa.session.omm.OMMItemEvent;
import com.reuters.rfa.session.omm.OMMItemIntSpec;
import com.sjls.algos.eo.common.ITickData;
import com.sjls.algos.eo.common.PrimaryMarket;
import com.sjls.f1.start.ofr.IMarketDataListener;
import com.sjls.f1.start.ofr.IMarketDataManager;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class BasicReutersConsumer implements IMarketDataManager, Client, Runnable {
    final private Logger m_logger = Logger.getLogger(BasicReutersConsumer.class);
    public final static String CVS_ID = "$Id: BasicReutersConsumer.java,v 1.3 2014/04/25 14:58:10 nhudson Exp $";
    final private EventQueue eventQueue;
    final private OMMConsumer ommConsumer;
    final private OMMPool pool;
    final private Map<String, Handle> itemHandlers = new HashMap<String, Handle>();

    private Handle m_loginHandle;
    private final List<IMarketDataListener> listeners = new CopyOnWriteArrayList<IMarketDataListener>();
    private final String serviceName;
    private boolean running;
    private final Thread thread;
    private final ReutersLevelOneConverter converter;
    private volatile boolean m_isConnected = false;

    public BasicReutersConsumer(OMMConsumer ommConsumer, String serviceName, FieldDictionary dictionary) {
        Context.initialize(); //copied from RMDS example
        this.eventQueue = EventQueue.create("eventQueue");
        this.pool = OMMPool.create();
        this.ommConsumer = ommConsumer;
        this.serviceName = serviceName;
        this.running = true;
        this.converter = new ReutersLevelOneConverter();
        thread = new Thread(this, "BasicReutersConsumer");
        thread.setDaemon(true);
        thread.start();
    }

    public void login(final String username) {
        if (m_loginHandle != null) throw new IllegalStateException("login handle already established");
        //
        m_logger.info(String.format("login(): Username=[%s]", username));
        final OMMItemIntSpec ommItemIntSpec = new OMMItemIntSpec();
        ommItemIntSpec.setMsg( encodeLoginReqMsg(username) );
        m_loginHandle = ommConsumer.registerClient(eventQueue, ommItemIntSpec, this, null);
    }


    private OMMMsg encodeLoginReqMsg(String username)
    {
        final String application = "256";
        String position = "1.1.1.1/net";
        try { 
            position = InetAddress.getLocalHost().getHostAddress() + "/" + InetAddress.getLocalHost().getHostName(); 
        }  catch( Exception e ) {
            m_logger.warn(e.getMessage(), e);
        }

        final OMMEncoder encoder = pool.acquireEncoder();
        encoder.initialize(OMMTypes.MSG, 500);
        final OMMMsg msg = pool.acquireMsg();
        msg.setMsgType(OMMMsg.MsgType.REQUEST);
        msg.setMsgModelType(RDMMsgTypes.LOGIN);
        msg.setIndicationFlags(OMMMsg.Indication.REFRESH);
        msg.setAttribInfo(null, username, RDMUser.NameType.USER_NAME);

        encoder.encodeMsgInit(msg, OMMTypes.ELEMENT_LIST, OMMTypes.NO_DATA);
        encoder.encodeElementListInit(OMMElementList.HAS_STANDARD_DATA, (short)0, (short) 0);
        encoder.encodeElementEntryInit(RDMUser.Attrib.ApplicationId, OMMTypes.ASCII_STRING);
        encoder.encodeString(application, OMMTypes.ASCII_STRING);
        encoder.encodeElementEntryInit(RDMUser.Attrib.Position, OMMTypes.ASCII_STRING);
        encoder.encodeString(position, OMMTypes.ASCII_STRING);
        encoder.encodeElementEntryInit(RDMUser.Attrib.Role, OMMTypes.UINT);
        encoder.encodeUInt((long)RDMUser.Role.CONSUMER);
        encoder.encodeAggregateComplete();

        //Get the encoded message from the encoder
        final OMMMsg encMsg = (OMMMsg)encoder.getEncodedObject();

        //Release the message that own by the application
        pool.releaseMsg(msg);

        return encMsg; //return the encoded message
    }

    
    public void logout() {
        m_logger.info("logout()");
        //
        if (m_loginHandle != null) {
            ommConsumer.unregisterClient(m_loginHandle);
            m_loginHandle = null;
        }
        synchronized (itemHandlers) {
            for (Handle itemHandle : itemHandlers.values())
                ommConsumer.unregisterClient(itemHandle);
            itemHandlers.clear();
        }
    }

    public void stop() {
        m_logger.info("stop()");
        logout();
        running = false;
        thread.interrupt();
    }

    
    @Override
    public void processEvent(final Event event) {
        final OMMItemEvent ie = (OMMItemEvent) event;
        final OMMMsg respMsg = ie.getMsg();

        if ((respMsg.getMsgType() == OMMMsg.MsgType.STATUS_RESP) && (respMsg.has(OMMMsg.HAS_STATE))) {
            final byte streamState = respMsg.getState().getStreamState();
            final byte dataState   = respMsg.getState().getDataState();
            if(streamState == OMMState.Stream.OPEN && dataState == OMMState.Data.OK) {
                m_logger.info("STREAM OPEN, DATA OK");
                onLogin(respMsg);
                m_isConnected = true;
            }
            else if(streamState == OMMState.Stream.OPEN && dataState == OMMState.Data.SUSPECT) {
                final String reason = "STREAM OPEN, DATA SUSPECT";
                m_logger.error(reason);
                if(m_isConnected){
                    onDisconnect(reason);  //since connected, this is disconnect
                }
                else{
                    onLoginFailure(respMsg); //since not connected, this is login failure
                }
                m_isConnected = false;
            }
            //else if((streamState == OMMState.Stream.CLOSED) &&(streamState == OMMState.Stream.CLOSED_RECOVER) && (dataState == OMMState.Data.SUSPECT)) { TODO: 3forge bug!! --olu E. 2014-02-24
            else if((streamState == OMMState.Stream.CLOSED || streamState == OMMState.Stream.CLOSED_RECOVER) && dataState == OMMState.Data.SUSPECT) {
                final String reason = "STREAM CLOSED, DATA SUSPECT";
                m_logger.error(reason);
                if(m_isConnected){
                    onDisconnect(reason); //since connected, this is disconnect
                }
                else{                    
                    onLoginFailure(respMsg); //since not connected, this is login failure
                }
                m_isConnected = false;
            }
            else{
                m_logger.info("Unknown status");//according to RFAJ_DeveloperGuide.pdf, section 9.8.3 (page 288), this should not happen.
            }
        }
    }
    

    private void fireOnItem(final OMMMsg event, final String symbol, final String ric, final PrimaryMarket primaryMarket) {
        final ITickData tickData[] = converter.convert(event, symbol, ric, primaryMarket);
        if(tickData != null) {
            for (IMarketDataListener l : listeners) {
                l.onMarketData(tickData);
            }
        }
    }

    
    private void fireOnCompletion(ComplEvent event, String itemName) {
        for (IMarketDataListener l : listeners) {
            l.onSubscriptionFailure();
        }
    }

    private void onDisconnect(final String reason) {
        for (IMarketDataListener l : listeners) {
            l.onDisconnect(reason, false); //null i.e. dont know the reason. dont propagate to ofr
        }
    }

    private void onLogin(OMMMsg event) {
        for (IMarketDataListener l : listeners){
            m_logger.info("firing l.onLogin for listener "+l+", for OMMMsg event "+event);
            l.onLogin();
        }
    }

    private void onLoginFailure(OMMMsg respMsg) {
        for (IMarketDataListener l : listeners){
            m_logger.info("firing l.onLoginFailure for listener "+l+", for OMMMsg event "+ReutersMessageParser.toString(respMsg));
            l.onLoginFailure();
        }
    }

    public void unsubscribe(String itemName) {
        m_logger.info("TEST:unsubscribe");
        synchronized (itemHandlers) {
            Handle itemHandler = itemHandlers.remove(itemName);
            if (itemHandler == null)
                return;
            ommConsumer.unregisterClient(itemHandler);
        }
    }

    public Set<String> getSubscribedNames() {
        synchronized (itemHandlers) {
            return new HashSet<String>(itemHandlers.keySet());
        }
    }


    /**
     * Subscribe to Symbol, RIC
     * @param symbol
     * @param ric
     */
    public void subscribe(final String symbol, final String ric, final PrimaryMarket primaryMkt) {
        m_logger.info(String.format("Subscribing to market data, symbol=%s, RIC=%s", symbol, ric));
        if(ric == null || ric.trim().isEmpty()) {
            throw new RuntimeException(String.format("BasicReutersConsumer.subscribe(): Invalid RIC [%s] for symbol [%s]", ric, symbol));
        }

        //TODO: Should we allow multiple subscriptions or ignore any duplicate subscriptions?? Is there any harm to multiple subscriptions?? --Olu E. 02/22/2013.
        final OMMMsg ommmsg = pool.acquireMsg();
        try {
            ommmsg.setMsgType(OMMMsg.MsgType.STREAMING_REQ);
            ommmsg.setMsgModelType(RDMMsgTypes.MARKET_PRICE);
            ommmsg.setPriority((byte) 1, 1);
            ommmsg.setAssociatedMetaInfo(m_loginHandle);
            ommmsg.setAttribInfo(serviceName, ric, RDMInstrument.NameType.RIC);
            final OMMItemIntSpec ommItemIntSpec = new OMMItemIntSpec();
            ommItemIntSpec.setMsg(ommmsg);
            synchronized (itemHandlers) {
                if (! itemHandlers.containsKey(ric)) {
                    Handle itemHandle = ommConsumer.registerClient(eventQueue, ommItemIntSpec, new ItemNameClient(symbol, ric, primaryMkt), null);
                    itemHandlers.put(ric, itemHandle);
                }
            }
        } finally {
            pool.releaseMsg(ommmsg);
        }
    }


    private class ItemNameClient implements Client {
        final private String symbol;
        final private String ric;
        final private PrimaryMarket primaryMarket;


        public ItemNameClient(String symbol, String ric, PrimaryMarket primaryMarket) {
            this.symbol = symbol;
            this.ric = ric;
            this.primaryMarket = primaryMarket;
        }

        @Override
        public void processEvent(final Event event) {
            if (event.getType() == Event.COMPLETION_EVENT) {
                m_logger.info("Receive a COMPLETION_EVENT, source=" +event.getEventSource().getEventSourceName()+", queue="+event.getEventQueue().getDispatchableName()+", toString="+ event.toString());
                m_logger.info("Receive a COMPLETION_EVENT, " + event.getHandle());
                fireOnCompletion((ComplEvent)event, ric);
            }

            if (event.getType() == Event.OMM_ITEM_EVENT) {
                OMMItemEvent ie = (OMMItemEvent) event;
                OMMMsg respMsg = ie.getMsg();
                if (m_logger.isTraceEnabled())  {
                    m_logger.trace(String.format("processEvent(): symbol=[%s], RIC=[%s], ==>%s<==", symbol, ric, ReutersMessageParser.toString(respMsg)));
                }
                fireOnItem(respMsg, symbol, ric, primaryMarket);
            }
        }
    }



    public void addListener(IMarketDataListener listener) {
        listeners.add(listener);
    }

    public void removeListener(IMarketDataListener listener) {
        listeners.remove(listener);
    }

    public void run() {
        while (running) {
            try {
                eventQueue.dispatch(1000);
            } catch (DispatchException de) {
                m_logger.error("EventQueue has been deactivated"+de.getMessage(), de);
                running=false;
            }
        }
    }


    public boolean isConnected() {
        return m_isConnected;
    }

}
