package com.sjls.f1.start.ofr.brokeralloc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sjls.algos.eo.utils.CSVFileIterator;
import com.sjls.algos.eo.utils.DirectoryIterator;
import com.sjls.controlpanel.protobuf.messages.ControlPanelMsgs.AllocationRec;
import com.sjls.controlpanel.protobuf.messages.ControlPanelMsgs.BrokerAllocationMsg;
import com.sjls.controlpanel.protobuf.messages.ControlPanelMsgs.DescrRec;

public class BrokerAllocMgr {
    public static String CVS_ID = "$Id: BrokerAllocMgr.java,v 1.2 2014/02/14 14:55:06 olu Exp $";
    public final static Logger m_logger = Logger.getLogger(BrokerAllocMgr.class);
    
    final static String BROKER_ALLOC = "BROKER_ALLOC_";
    final static String BROKER_DESCR = "BROKER_DESCR_";
    
    private final HashMap<String, Integer> m_allocMap= new HashMap<String, Integer>();
    private final HashMap<String, String> m_descrMap= new HashMap<String, String>();
    private final String m_dataDir;
    
    public BrokerAllocMgr(final String dataDir) throws IOException {
        m_dataDir = dataDir;
        final DirectoryIterator dirIter = new DirectoryIterator(dataDir);
        readBrokerAllocFile(dirIter);
        readBrokerDescrFile(dirIter);
    }
    
    
    private void readBrokerAllocFile(final DirectoryIterator dirIter) throws IOException {
        final int NUM_FLDS=2;
        final File file = dirIter.getLatestFile(BROKER_ALLOC);
        final String allocFname = file.getAbsolutePath();
        m_logger.info(String.format("Reading BROKER ALLOC file [%s]", allocFname) );
        
        final CSVFileIterator csvFile = new CSVFileIterator(file);

        String flds[];
        while ((flds = csvFile.getNext("|")) != null)
        {
            try {
                if (flds.length != NUM_FLDS) {
                    m_logger.error(String.format("BROKER_ALLOC file [%s]: Bad Line. Expected %d flds but saw [%d]. Line ignored.",  
                            file.getAbsolutePath(), NUM_FLDS, flds.length));
                }
                else {
                    final String broker = flds[0]==null ? null : flds[0].trim();
                    final String value = flds[1]==null ? null : flds[1].trim();
                    if(StringUtils.isBlank(broker)) {
                        m_logger.error(String.format("BROKER_ALLOC file [%s]: Bad Line. No broker!. Line ignored. [%s]",  
                                file.getAbsolutePath(), Arrays.toString(flds)));
                    }
                    else m_allocMap.put(broker,  StringUtils.isBlank(value) ? 0 : Integer.parseInt(value));
                }
            }
            catch(Exception e) {
                final String errStr = String.format("Bad line [%s] in BROKER ALLOC file (%s)!!==>", Arrays.toString(flds), file.getAbsolutePath());
                m_logger.error(errStr+e.getMessage(), e);
            }
        }
    }
    
    
    private void readBrokerDescrFile(final DirectoryIterator dirIter) throws IOException {
        final int NUM_FLDS=2;
        final File file = dirIter.getLatestFile(BROKER_DESCR);
        final String fname = file.getAbsolutePath();
        m_logger.info(String.format("Reading BROKER DESCR file [%s]", fname) );
        
        final CSVFileIterator csvFile = new CSVFileIterator(file);

        String flds[];
        while ((flds = csvFile.getNext("|")) != null)
        {
            try {
                if (flds.length != NUM_FLDS) {
                    m_logger.error(String.format("BROKER DESCR file [%s]: Bad Line. Expected %d flds but saw [%d]. Line ignored.",  
                            file.getAbsolutePath(), NUM_FLDS, flds.length));
                }
                else {
                    final String broker = flds[0]==null ? null : flds[0].trim();
                    final String descr = flds[1]==null ? null : flds[1].trim();
                    if(StringUtils.isBlank(broker)) {
                        m_logger.error(String.format("BROKER DESCR file [%s]: Bad Line. No broker!. Line ignored. [%s]",  
                                file.getAbsolutePath(), Arrays.toString(flds)));
                    }
                    else if(StringUtils.isBlank(descr)) {
                        m_logger.info(String.format("BROKER DESCR file [%s]: No description for Broker [%s]. Will skip....",  
                                file.getAbsolutePath(), broker));
                    }
                    else m_descrMap.put(broker, descr);
                }
            }
            catch(Exception e) {
                final String errStr = String.format("Bad line [%s] in BROKER DESCR file (%s)!!==>", Arrays.toString(flds), file.getAbsolutePath());
                m_logger.error(errStr+e.getMessage(), e);
            }
        }
    }
    
    
    
    public synchronized BrokerAllocationMsg getBrokerAllocationMsg() {
        final List<AllocationRec> recList = new LinkedList<AllocationRec>();
        for(final Entry<String, Integer> entry : m_allocMap.entrySet()) {
            recList.add(AllocationRec.newBuilder().setBroker(entry.getKey()).setAmount(entry.getValue()).build());
        }
        final List<DescrRec> descrList = new LinkedList<DescrRec>();
        for(final Entry<String, String> entry : m_descrMap.entrySet()) {
            descrList.add(DescrRec.newBuilder().setBroker(entry.getKey()).setDescr(entry.getValue()).build());
        }
        return BrokerAllocationMsg.newBuilder().addAllAllocationRec(recList).addAllDescrRec(descrList).build();
    }


    public synchronized void save(final List<AllocationRec> allocationRecList) throws FileNotFoundException {
        final String fname = String.format("%s%s%s%s.dat", 
                m_dataDir, 
                File.separator,
                BROKER_ALLOC,
                new SimpleDateFormat("yyyyMMdd").format(new Date()));
        final PrintWriter writer = new PrintWriter(fname);
        for(AllocationRec rec : allocationRecList) {
            m_allocMap.put(rec.getBroker(), rec.getAmount());
            writer.println(String.format("%s|%s", rec.getBroker(), rec.getAmount()));
        }
        writer.close();
    }
}
