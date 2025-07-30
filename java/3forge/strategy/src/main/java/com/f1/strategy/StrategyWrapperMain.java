package com.f1.strategy;

import java.util.logging.Logger;

import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.RequestOutputPort;
import com.f1.pofo.refdata.RefDataInfoMessage;
import com.f1.pofo.refdata.RefDataRequestMessage;
import com.f1.utils.PropertyController;

public class StrategyWrapperMain {
	public final static Logger m_logger = Logger.getLogger(StrategyWrapperMain.class.getName());

	/**
	 * port of the oms exhaust where snapshots and deltas of orders / executions will be sent
	 */
	public static final String OMS_CONNECTION_PORT = "oms.port";

	/**
	 * host of the oms exhaust where snapshots and deltas of orders / executions will be sent
	 */
	public static final String OMS_CONNECTION_HOST = "oms.host";

	/**
	 * true indicates the use of reuters, false indicates use of simulator
	 */
	public static final String OPTION_REUTERS_ENABLED = "reuters.enabled";

	/**
	 * system name to be assigned to generated orders. (must match system name in oms config)
	 */
	public static final String OPTION_SYSTEM_NAME = "systemname";

	/**
	 * number of ids to batch (for example, if 1000 then it will fetch a new batch of ids from the file system every 1000 ids)
	 */
	public static final String OPTION_IDFOUNTAIN_BATCHSIZE = "idfountan.batchsize";
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

	public static void main(String args[]) throws Exception {
		// /////////////////////////////////////
		// //////////////// INIT ///////////////

		// Bootstraping...
		final ContainerBootstrap cam = new ContainerBootstrap(StrategyWrapperMain.class, args);
		cam.setConfigDirProperty("./src/main/config");
		cam.setLoggingOverrideProperty("warning");
		//cam.setLogLevel(Level.FINE, Level.CONFIG, StrategyWrapperMain.class);
		final PropertyController props = cam.getProperties();
		cam.startup();
		// Create a tcp/ip server connection for the front end.
		String host = props.getOptional(OMS_CONNECTION_HOST, "localhost");
		int port = props.getOptional(OMS_CONNECTION_PORT, 4567);

		// Create OFR Adapter and Execution Optimizer and OMS Proxy Connection
		final OMSConnectionHandler omsProxy = new OMSConnectionHandler(cam, host, port);

		RequestOutputPort<RefDataRequestMessage, RefDataInfoMessage> refDataPort = omsProxy.getRefDataPort();
		// RMDS

		omsProxy.init();
		cam.registerConsoleObject("omsp", omsProxy);
	}

}
