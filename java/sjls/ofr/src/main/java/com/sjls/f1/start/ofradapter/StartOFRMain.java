package com.sjls.f1.start.ofradapter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.WriterAppender;
import org.joda.time.DateTime;

import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.Container;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.reuters.rfa.common.Context;
import com.reuters.rfa.common.EventSource;
import com.reuters.rfa.config.ConfigDb;
import com.reuters.rfa.dictionary.FieldDictionary;
import com.reuters.rfa.session.Session;
import com.reuters.rfa.session.omm.OMMConsumer;
import com.sjls.algos.eo.common.IEMSServices;
import com.sjls.algos.eo.core.PSStockFile;
import com.sjls.algos.eo.utils.DirectoryIterator;
import com.sjls.f1.start.ofr.brokeralloc.BrokerAllocMgr;
import com.sjls.f1.start.ofr.reuters.BasicReutersConsumer;
import com.sjls.f1.start.ofr.reuters.ReutersMessageParser;
import com.sjls.f1.start.ofr.sjlstaq.SjlsTAQConsumer;

/*
 * -Dlog4j.configuratorClass=com.f1.speedloggerLog4j.Log4jSpeedLoggerManager
 * -Dlog4j.configuration=com/f1/speedloggerLog4j/Log4jSpeedLoggerManager.class
 * -Djava.util.logging.manager=com.f1.speedlogger.sun.SunSpeedLoggerLogManager
 * -Dproperty.sjls.dir=<path to sjls datadir>
 *
 */
public class StartOFRMain {

	public final static String CVS_ID = "$Id: StartOFRMain.java,v 1.7 2014/12/02 18:46:13 olu Exp $";
	public final static Logger m_logger = Logger.getLogger(StartOFRMain.class);

	/**
	 * port of the oms exhaust where snapshots and deltas of orders / executions will be sent
	 */
	public static final String OMS_CONNECTION_PORT = "oms.port";

	/**
	 * host of the oms exhaust where snapshots and deltas of orders / executions will be sent
	 */
	public static final String OMS_CONNECTION_HOST = "oms.host";

	/**
	 * We currently support 3 providers, REUTERS, BLOOMBERG, SJLS_TAQSERVER and MKTDATA_PUMP
	 */
	public static final String REUTERS = "reuters";
	public static final String BLOOMBERG = "bloomberg";
	public static final String SJLSTAQ = "sjlstaq";
	public static final String MKTDATA_PUMP = "mktdata_pump";

	public static final String MARKET_DATA_PROVIDER = "marketdata.provider";

	public static final String OPTION_BLOOMBERG_HOST = "bloomberg.host";
	public static final String OPTION_BLOOMBERG_PORT = "bloomberg.port";
	public static final String OPTION_BLOOMBERG_RECONNECT_DELAY = "bloomberg.reconnectdelay";
	public static final String OPTION_BLOOMBERG_RECONNECT_DURATION = "bloomberg.reconnectduration";
	public static final String OPTION_BLOOMBERG_IS_UTC = "bloomberg.isutc";

	public static final String OPTION_SJLSTAQ_HOST = "sjlstaq.host";
	public static final String OPTION_SJLSTAQ_PORT = "sjlstaq.port";

	/**
	 * system name to be assigned to generated orders. (must match system name in oms config)
	 */
	public static final String OPTION_SYSTEM_NAME = "systemname";

	/**
	 * number of ids to batch (for example, if 1000 then it will fetch a new batch of ids from the file system every 1000 ids)
	 */
	public static final String OPTION_IDFOUNTAIN_BATCHSIZE = "idfountain.batchsize";
	/**
	 * the directory where files containing last ids will be stored
	 */
	public static final String OPTION_IDFOUNTAIN_DIR = "idfountain.dir";
	public static final String OPTION_REFDATA_HOST = "refdata.host";
	public static final String OPTION_REFDATA_PORT = "refdata.port";

	/**
	 * the location of the enum file, likely called: RDMFieldDictionary
	 */
	public static final String OPTION_REUTERS_FIELDS_FILE = "reuters.fields.file";

	/**
	 * the location of the enum file , likely called: enumtype.def
	 */
	public static final String OPTION_REUTERS_ENUMS_FILE = "reuters.enums.file";

	/**
	 * the reuters dacs id (the username supplied by reuters)
	 */
	public static final String OPTION_REUTERS_DACSID = "reuters.dacsid";

	/**
	 * the service name for the reuters system (delayed = dIDN_RDF or regular = IDN_RDF)
	 */
	public static final String OPTION_REUTERS_SERVICE = "reuters.service";

	/**
	 * the host and port of the refdata server(note: currently the oms is hosting the refdata internally). Should be in the form host:port
	 */
	public static final String OPTIONS_REFDATA_URL = "refdata.url";
	private final static String EXECUTION_OPTIMIZER_CLASSNAME = "EXECUTION_OPTIMIZER_CLASSNAME";

	public static final String SJLS_DAYSTOLICENSE = "sjls.daystolicense";
	public static final String SJLS_SHORTSELLCAPABLE = "sjls.shortsellcapable";
	private static final String ROUTE_TO_BROKER_TAG = "ROUTE_TO_BROKER_TAG";
	private static final String ROUTE_TO_BROKER_IGNORE_VALUE = "ROUTE_TO_BROKER_IGNORE_VALUE";

	/**
	 * Server port for the control panel
	 */
	public static final String CONTROLPANEL_PORT = "controlpanel.port";

	final static Timer timer = new Timer();
	private static String m_dataDir;

	public static void main(String args[]) throws Exception {
		// /////////////////////////////////////
		// //////////////// INIT ///////////////

		initLog4j();

		m_logger.info(String.format("StartOFRMain: [%s] OFRAdaptor CVS_ID=[%s]", Version.getVersion(), OfrAdapter.CVS_ID));

		// Bootstraping...
		final ContainerBootstrap cam = new ContainerBootstrap(StartOFRMain.class, args);
		cam.setConfigDirProperty("./src/main/config");
		cam.setLogLevel(Level.FINE, Level.CONFIG, StartOFRMain.class);
		final PropertyController props = cam.getProperties();

		m_dataDir = props.getRequired("DATA_DIR");

		cam.startup();

		// Start up CP Server
		final Integer cpPort = props.getRequired(CONTROLPANEL_PORT, Integer.class);
		if (cpPort == null) {
			throw new IllegalArgumentException(String.format("You need to specify the control panel port [%s]", CONTROLPANEL_PORT));
		}

		// Create OFR Adapter and Execution Optimizer and OMS Proxy Connection
		final OfrAdapter ofrAdapter = new OfrAdapter(cpPort);
		final String shortSellCapable = props.getOptional(SJLS_SHORTSELLCAPABLE);
		if (shortSellCapable != null && shortSellCapable.equalsIgnoreCase("No")) {
			ofrAdapter.setShortSellCapable(false);
			m_logger.warn(String.format("%s property set to No in ofr/conf/root.poperties, so ShortSells will be Rejected!", SJLS_SHORTSELLCAPABLE));
		}

		// Create a tcp/ip server connection for the front end.
		final String host = props.getOptional(OMS_CONNECTION_HOST, "localhost");
		final int port = props.getOptional(OMS_CONNECTION_PORT, 4567);
		final OMSConnectionHandler omsProxy = new OMSConnectionHandler(cam, ofrAdapter, host, port);
		//
		final String eoClassStr = props.getRequired(EXECUTION_OPTIMIZER_CLASSNAME);
		final Constructor<?> ctor = Class.forName(eoClassStr).getConstructor(new Class[] { IEMSServices.class, Properties.class });
		//final IExecutionOptimizer optimizer = (IExecutionOptimizer) ctor.newInstance(new Object[] { ofrAdapter, props.getProperties() });

		//ofrAdapter.setExecutionOptimizer(optimizer);

		final DirectoryIterator diter = new DirectoryIterator(m_dataDir);
		ofrAdapter.setSecMaster(new PSStockFile(diter));
		ofrAdapter.setBrokerAllocMgr(new BrokerAllocMgr(m_dataDir));

		// Market Data Provider
		final String mktDataProvider = props.getRequired(MARKET_DATA_PROVIDER);
		if (mktDataProvider.equalsIgnoreCase(REUTERS)) {
			m_logger.info(String.format("Starting Reuters market data module because [%s] is set to %s", MARKET_DATA_PROVIDER, mktDataProvider));
			startrmds(omsProxy.getContainer(), ofrAdapter);
		} else if (mktDataProvider.equalsIgnoreCase(BLOOMBERG)) {
			m_logger.info(String.format("Starting BLOOMBERG market data module because [%s] is set to %s", MARKET_DATA_PROVIDER, mktDataProvider));
			startBloomberg(omsProxy.getContainer(), ofrAdapter);
		} else if (mktDataProvider.equalsIgnoreCase(SJLSTAQ)) {
			m_logger.info(String.format("Starting SJLS TAQSERVER market data module because [%s] is set to %s", MARKET_DATA_PROVIDER, mktDataProvider));
			startSjlsTAQ(omsProxy.getContainer(), ofrAdapter);
		} else if (mktDataProvider.equalsIgnoreCase(MKTDATA_PUMP)) {
			m_logger.info(String.format("Will use Market market PUMP module because [%s] is set to %s", MARKET_DATA_PROVIDER, mktDataProvider));
		} else {
			throw new Exception("Unknown market data provider [" + mktDataProvider + "]");
		}

		final String routeToBrokerTag = props.getOptional(ROUTE_TO_BROKER_TAG);
		if (routeToBrokerTag == null) {
			m_logger.info("[" + ROUTE_TO_BROKER_TAG + "] property was not set in root.properties");
		} else {
			try {
				m_logger.info(String.format("[%s] property was set to [%s] in root.properties", ROUTE_TO_BROKER_TAG, routeToBrokerTag));
				OfrAdapter.setRouteToBrokerTag(Integer.parseInt(routeToBrokerTag));
			} catch (Exception e) {
				m_logger.warn(String.format("[%s] property could not be set to [%s] in root.properties", ROUTE_TO_BROKER_TAG, routeToBrokerTag));
			}
		}

		final String routeToBrokerIgnoreValue = props.getOptional(ROUTE_TO_BROKER_IGNORE_VALUE);
		if (routeToBrokerIgnoreValue == null || routeToBrokerIgnoreValue.trim().length() == 0) {
			m_logger.info("[" + ROUTE_TO_BROKER_IGNORE_VALUE + "] property was not set in root.properties");
		} else {
			try {
				m_logger.info(ROUTE_TO_BROKER_IGNORE_VALUE + " property was set to " + routeToBrokerIgnoreValue + " in root.properties");
				OfrAdapter.setRouteToBrokerIgnoreValue(routeToBrokerIgnoreValue);
			} catch (Exception e) {
				m_logger.warn(ROUTE_TO_BROKER_IGNORE_VALUE + " property could not be set to " + routeToBrokerIgnoreValue + " in root.properties");
			}

		}

		m_logger.info("Calling omsProxy.init()...");
		omsProxy.init();
		cam.registerConsoleObject("omsp", omsProxy);
		//
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println("Flushing log4j...");
				flushLog4j();
				System.out.println("DONE flushing log4j...");
			}
		}, new DateTime().withTime(16, 1, 0, 0).toDate());
	}

	private static void initLog4j() {
		final String log4jFile = System.getProperty("log4j");
		if (log4jFile == null || log4jFile.trim().length() == 0) {
			BasicConfigurator.configure();
			m_logger.warn("'log4j' system property not specified or is blank. Using basic configurator...");
		} else {
			PropertyConfigurator.configure(log4jFile);
		}
	}

	private static void flushLog4j() {
		try {
			final Enumeration e = Logger.getRootLogger().getAllAppenders();
			while (e.hasMoreElements()) {
				final Object appender = e.nextElement();
				if (appender != null && appender instanceof WriterAppender) {
					WriterAppender app = (WriterAppender) appender;
					app.setImmediateFlush(true);
					m_logger.info("MANUAL FLUSH for LOG4J");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("ERROR while flushing log4j in method STARTOFRMain()");
		}
	}

	private static void startBloomberg(final Container container, final OfrAdapter ofrAdapter) {
		m_logger.info("startBloomberg called");

		final PropertyController props = container.getServices().getPropertyController();

		try {
			final String serverHosts = props.getRequired(OPTION_BLOOMBERG_HOST);
			final String serverPorts = props.getRequired(OPTION_BLOOMBERG_PORT);
			final int reconnectDelayInSecs = props.getOptional(OPTION_BLOOMBERG_RECONNECT_DELAY, 0);
			final int reconnectDurationInSecs = props.getOptional(OPTION_BLOOMBERG_RECONNECT_DURATION, 0);
			final boolean isUTC = props.getOptional(OPTION_BLOOMBERG_IS_UTC, true);

			//            BloombergConsumer bloombergConsumer = new BloombergConsumer(serverHosts, serverPorts, BloombergConsumer bloombergConsumer = new BloombergConsumer(serverHosts, serverPorts,
			//reconnectDelayInSecs, reconnectDurationInSecs, isUTC);

			if (m_logger.isDebugEnabled())
				m_logger.debug("ofrAdapter.setMarketDataManager called");
			//ofrAdapter.setMarketDataManager(bloombergConsumer);
		} catch (Exception ex) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("startbloomberg exception");
			throw OH.toRuntime(ex);
		}
	}

	private static void startSjlsTAQ(final Container container, final OfrAdapter ofrAdapter) {
		m_logger.info("startSjlsTAQ called");

		final PropertyController props = container.getServices().getPropertyController();

		try {
			final String serverHost = props.getRequired(OPTION_SJLSTAQ_HOST);
			final int serverPort = Integer.parseInt(props.getRequired(OPTION_SJLSTAQ_PORT));
			ofrAdapter.setMarketDataManager(new SjlsTAQConsumer(serverHost, serverPort));
			if (m_logger.isDebugEnabled())
				m_logger.debug("ofrAdapter.setMarketDataManager called");
		} catch (Exception ex) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("startbloomberg exception");
			throw OH.toRuntime(ex);
		}
	}

	/**
	 * Use RMDS if reuters is enabled, otherwise use home grown tick generator --Olu E. 2012-07-14
	 * 
	 * @param container
	 * @param refDataPort
	 */
	private static void startrmds(final Container container, final OfrAdapter ofrAdapter) {
		m_logger.info("startrmds called");

		final PropertyController props = container.getServices().getPropertyController();

		//Extract RFA properties from PropertyController so 3forge code doesn't touch RFA code
		final Properties prop = props.getProperties();
		final ConfigDb rfaConfig = new ConfigDb();
		for (Object k : prop.keySet()) {
			if (k.toString().startsWith("reuters.rfa.")) {
				String key = k.toString().replace("reuters.rfa.", "");
				String val = prop.getProperty(k.toString());
				rfaConfig.addVariable(key, val);
				if (m_logger.isDebugEnabled())
					m_logger.debug(String.format("DEBUG: %s=%s", key, val));
			}
		}

		try {
			Context.initialize(rfaConfig);
			final Session session = Session.acquire("myNamespace::consSession");
			if (session == null) {
				throw new Exception("Reuters configuration failure. Session.acquire(myNamespace::consSession) FAILED!");
			}
			final OMMConsumer consumer = (OMMConsumer) session.createEventSource(EventSource.OMM_CONSUMER, "myOMMConsumer", true);
			final FieldDictionary dictionary = FieldDictionary.create();

			final String fieldDictionaryFilename = props.getRequired(OPTION_REUTERS_FIELDS_FILE);
			final String enumDictionaryFilename = props.getRequired(OPTION_REUTERS_ENUMS_FILE);
			final String username = props.getRequired(OPTION_REUTERS_DACSID);
			final String serviceName = props.getRequired(OPTION_REUTERS_SERVICE);

			FieldDictionary.readRDMFieldDictionary(dictionary, fieldDictionaryFilename);
			FieldDictionary.readEnumTypeDef(dictionary, enumDictionaryFilename);
			ReutersMessageParser.initializeDictionary(dictionary);

			container.getServices().putService("RMDS_DICTIONARY", dictionary);
			//BasicReutersConsumer brc = new BasicReutersConsumer(consumer, serviceName, dictionary, container.getServices().getGenerator(), refDataPort);
			final BasicReutersConsumer brc = new BasicReutersConsumer(consumer, serviceName, dictionary);
			ofrAdapter.setMarketDataManager(brc);
			if (m_logger.isDebugEnabled())
				m_logger.debug("ofrAdapter.setMarketDataManager called");
			//
			brc.login(username);
			new Thread(brc).start(); //Dont really like this free standing thread--Need to Fix. Olu E.
		} catch (Exception e) {
			m_logger.error(getMessage(e), e);
			throw OH.toRuntime(e);
		}
	}

	/**
	 * Return a List of all valid start users. null if the START_USERS.txt file is not found
	 * */
	public static List<String> getStartUsers() {
		final String fileName = String.format("%s/START_USERS.txt", m_dataDir);
		try {
			final BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;
			final LinkedList<String> list = new LinkedList<String>();
			while ((line = br.readLine()) != null) {
				list.add(line.trim());
			}
			br.close();
			return list;
		} catch (FileNotFoundException e) {
			m_logger.warn(String.format("Cant locate START users file...looking for [%s]", fileName));
		} catch (Exception e) {
			m_logger.error(String.format("Error %s encountered while loading START users file [%s]", getMessage(e), fileName), e);
		}
		return null;
	}

	private static String getMessage(final Exception e) {
		return (e == null || e.getMessage() == null) ? "NullPointer" : e.getMessage();
	}
}
