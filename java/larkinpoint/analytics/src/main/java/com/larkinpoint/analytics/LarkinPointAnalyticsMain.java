package com.larkinpoint.analytics;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import com.f1.base.Action;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.Suite;
import com.f1.container.impl.BasicContainer;
import com.f1.msg.impl.MsgConsole;
import com.f1.msgdirect.MsgDirectConnection;
import com.f1.msgdirect.MsgDirectConnectionConfiguration;
import com.f1.msgdirect.MsgDirectTopicConfiguration;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.suite.utils.ClassRoutingProcessor;
import com.f1.suite.utils.ParamRoutingProcessor;
import com.f1.suite.utils.msg.MsgSuite;
import com.f1.utils.BasicDay;
import com.f1.utils.DBH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.db.Database;
import com.f1.utils.db.DbService;
import com.f1.utils.ids.BasicNamespaceIdGenerator;
import com.f1.utils.ids.BatchIdGenerator;
import com.f1.utils.ids.DbBackedIdGenerator;
import com.larkinpoint.analytics.ivydb.IvyDbCurrencyFileProcessor;
import com.larkinpoint.analytics.ivydb.IvyDbExchangesFileProcessor;
import com.larkinpoint.analytics.ivydb.IvyDbHistoricalVolatilityFileProcessor;
import com.larkinpoint.analytics.ivydb.IvyDbIndexDividendsFileProcessor;
import com.larkinpoint.analytics.ivydb.IvyDbOptionPricesFileProcessor;
import com.larkinpoint.analytics.ivydb.IvyDbSecurityNamesFileProcessor;
import com.larkinpoint.analytics.ivydb.IvyDbSecurityPricesFileProcessor;
import com.larkinpoint.analytics.ivydb.IvyDbStdOptionPriceFileProcessor;
import com.larkinpoint.analytics.ivydb.IvyDbVolSurfaceFileProcessor;
import com.larkinpoint.analytics.ivydb.IvyDbZeroCurveFileProcessor;
import com.larkinpoint.analytics.ivydb.IvyFTPProcessor;
import com.larkinpoint.messages.ActionMessage;
import com.larkinpoint.messages.GetAllTradeDatesRequest;
import com.larkinpoint.messages.GetAllTradeDatesResponse;
import com.larkinpoint.messages.GetAvailableIvyFiles;
import com.larkinpoint.messages.GetLarkinBackTestingDataRequest;
import com.larkinpoint.messages.GetLarkinPutsRequest;
import com.larkinpoint.messages.GetOptionDataByOptionIDRequest;
import com.larkinpoint.messages.GetOptionDataBySymbolDateRequest;
import com.larkinpoint.messages.GetOptionDataResponse;
import com.larkinpoint.messages.GetUnderlyingDataBySymbolDateRequest;
import com.larkinpoint.messages.GetUnderlyingDataBySymbolDateResponse;
import com.larkinpoint.messages.LoadFileMessage;

//import com.vortex.web.TestTrackPortalHttpHandler;

public class LarkinPointAnalyticsMain {

	private static final String CON = "con";
	private static final String CONNECTION1 = "connection1";
	private static final String SRC_MAIN_CONFIG = "./src/main/config";
	public static OutputPort<LoadFileMessage> output1;
	public static OutputPort<Action> output;
	public static void main(String[] a) throws IOException, SQLException {

		//BOOTSTRAPPING

		ContainerBootstrap bs = new ContainerBootstrap(LarkinPointAnalyticsMain.class, a);
		bs.setConfigDirProperty(SRC_MAIN_CONFIG);

		//LOGGING

		//bs.setLoggingOverrideProperty(INFO);
		bs.setLoggingOverrideProperty("info");
		//bs.setLogLevel(Level.FINE, Level.FINE, OmsPortalHttpHandler.class);
		//bs.setLogLevel(Level.FINE, Level.FINE, DemoPortalHttpHandler.class);
		//bs.setLogPerformanceLevel(Level.INFO);

		bs.startup();
		bs.registerMessagesInPackages(F1AppInstance.class.getPackage());
		bs.registerMessagesInPackages("com.f1.pofo");
		bs.registerMessagesInPackages("com.larkinpoint.messages");

		//PROPERTIES 
		PropertyController props = bs.getProperties();

		BasicContainer container = new BasicContainer();
		bs.prepareContainer(container);
		//Database Properties
		String dburl = props.getRequired("db.url");
		int serverPort = props.getRequired("analytics.server.port", int.class);
		Database dbsource = DBH.createPooledDataSource(dburl, props.getRequired("db.password"));
		DbService dbservice = new DbService(dbsource, container.getGenerator());
		dbservice.add(new File(props.getRequired("sql.dir")), ".sql");
		BatchIdGenerator.Factory<Long> fountain = new BatchIdGenerator.Factory<Long>(new DbBackedIdGenerator.Factory(dbsource, "Id_Fountains", "next_id", "namespace", 100), 10000);
		container.getServices().setUidGenerator(new BasicNamespaceIdGenerator<Long>(fountain));
		container.getServices().putService("OPTIONSDB", dbservice);
		//Load Data Properties
		String optionSymbol = props.getOptional("options.load.symbols");
		String underSymbol = props.getOptional("options.load.unders");
		String startDate = props.getOptional("options.load.startdate");
		String endDate = props.getOptional("options.load.enddate");

		// Suites are collections of related processes so either get the default one or create a new one...
		Suite rs = container.getRootSuite();
		// So now create your processors...remember that Processors work on a particular type of Partition or State....so you need to bind these afterwards.
		final LarkinPointFileLoaderProcessor FLProc = new LarkinPointFileLoaderProcessor();
		final LarkinPointUnderlyingDataProcessor UDProc = new LarkinPointUnderlyingDataProcessor();
		final LarkinPointOptionDataProcessor ODProc = new LarkinPointOptionDataProcessor();
		final LarkinPointOptionDataByIDProcessor OIDProc = new LarkinPointOptionDataByIDProcessor();
		final LarkinPointTradeDatesBySymbolProcessor TDProc = new LarkinPointTradeDatesBySymbolProcessor();
		final LarkinPointBackTestingProcessor BackTestProc = new LarkinPointBackTestingProcessor();
		final LarkinPointBackTestingPutsProcessor PutsBackTestProc = new LarkinPointBackTestingPutsProcessor();

		final ClassRoutingProcessor<Action> routingProcessor = new ClassRoutingProcessor<Action>(Action.class);
		final ParamRoutingProcessor<LoadFileMessage> FRProc = new ParamRoutingProcessor<LoadFileMessage>(LoadFileMessage.class, LoadFileMessage.PID_LOAD_FILETYPE);
		final ParamRoutingProcessor<ActionMessage> ActionProc = new ParamRoutingProcessor<ActionMessage>(ActionMessage.class, ActionMessage.PID_ACTION_MESSAGE_TYPE);

		final IvyDbCurrencyFileProcessor IVYCurrencyProc = new IvyDbCurrencyFileProcessor();
		final IvyDbSecurityNamesFileProcessor IVYSecNamesProc = new IvyDbSecurityNamesFileProcessor();
		final IvyDbSecurityPricesFileProcessor IVYSecPricesProc = new IvyDbSecurityPricesFileProcessor();
		final IvyDbExchangesFileProcessor IVYExchangesProc = new IvyDbExchangesFileProcessor();
		final IvyDbHistoricalVolatilityFileProcessor IVYHistVolProc = new IvyDbHistoricalVolatilityFileProcessor();
		final IvyDbIndexDividendsFileProcessor IVYIndexDividendsProc = new IvyDbIndexDividendsFileProcessor();
		final IvyDbOptionPricesFileProcessor IVYOptionPricesProc = new IvyDbOptionPricesFileProcessor();
		final IvyDbStdOptionPriceFileProcessor IVYStdOptionProc = new IvyDbStdOptionPriceFileProcessor();
		final IvyDbVolSurfaceFileProcessor IVYVolSurfaceProc = new IvyDbVolSurfaceFileProcessor();
		final IvyDbZeroCurveFileProcessor IVYZeroCurveProc = new IvyDbZeroCurveFileProcessor();
		final IvyFTPProcessor IVYFTPFileProc = new IvyFTPProcessor();
		final LarkinPointCalculateReturnsProcessor LPCalcReturnsProc = new LarkinPointCalculateReturnsProcessor();

		//Add the processors to the Suite
		rs.addChild(routingProcessor);
		rs.addChild(FLProc);
		rs.addChild(UDProc);
		rs.addChild(ODProc);
		rs.addChild(OIDProc);
		rs.addChild(TDProc);
		rs.addChild(FRProc);
		rs.addChild(BackTestProc);
		rs.addChild(PutsBackTestProc);
		rs.addChild(ActionProc);
		rs.addChild(IVYCurrencyProc);
		rs.addChild(IVYSecNamesProc);
		rs.addChild(IVYSecPricesProc);
		rs.addChild(IVYExchangesProc);
		rs.addChild(IVYHistVolProc);
		rs.addChild(IVYIndexDividendsProc);
		rs.addChild(IVYOptionPricesProc);
		rs.addChild(IVYStdOptionProc);
		rs.addChild(IVYVolSurfaceProc);
		rs.addChild(IVYZeroCurveProc);
		rs.addChild(IVYFTPFileProc);
		rs.addChild(LPCalcReturnsProc);

		//Register which partition(s) you want the processors to work on
		routingProcessor.bindToPartition("LARKIN");
		FLProc.bindToPartition("LARKIN");
		UDProc.bindToPartition("LARKIN");
		ODProc.bindToPartition("LARKIN");
		OIDProc.bindToPartition("LARKIN");
		TDProc.bindToPartition("LARKIN");
		BackTestProc.bindToPartition("LARKIN");
		PutsBackTestProc.bindToPartition("LARKIN");
		FRProc.bindToPartition("LARKIN");
		ActionProc.bindToPartition("LARKIN");
		IVYCurrencyProc.bindToPartition("FILELOADER");
		IVYSecNamesProc.bindToPartition("LARKIN");
		IVYSecPricesProc.bindToPartition("LARKIN");
		IVYExchangesProc.bindToPartition("FILELOADER");
		IVYHistVolProc.bindToPartition("FILELOADER");
		IVYIndexDividendsProc.bindToPartition("FILELOADER");
		IVYOptionPricesProc.bindToPartition("LARKIN");
		IVYStdOptionProc.bindToPartition("FILELOADER");
		IVYVolSurfaceProc.bindToPartition("FILELOADER");
		IVYZeroCurveProc.bindToPartition("FILELOADER");
		LPCalcReturnsProc.bindToPartition("LARKIN");
		IVYFTPFileProc.bindToPartition("LARKIN");

		//Setup a Message Suite so that the application can send/receive messages with other applications
		//First Create a connection
		MsgDirectConnection connection = new MsgDirectConnection(new MsgDirectConnectionConfiguration(CONNECTION1));

		connection.addTopic(new MsgDirectTopicConfiguration("server.to.gui", serverPort, "server.to.gui"));
		connection.addTopic(new MsgDirectTopicConfiguration("gui.to.server", serverPort, "gui.to.server"));
		//Now create the Suite with the connection info, a Partition ID and some to and from topics
		MsgSuite clientMsgSuite = new MsgSuite("MSGIN", connection, "gui.to.server", "server.to.gui");
		clientMsgSuite.setSupportCircularReferences(true);
		//Add the resulting Suite back to your Master suite.
		rs.addChild(clientMsgSuite);

		//Connect the various ports together so that messages can flow.
		rs.wire(clientMsgSuite.inboundOutputPort, routingProcessor, true);
		rs.wire(routingProcessor.newOutputPort(LoadFileMessage.class), FRProc, true);
		rs.wire(routingProcessor.newOutputPort(ActionMessage.class), ActionProc, true);
		rs.wire(routingProcessor.newOutputPort(GetAvailableIvyFiles.class), IVYFTPFileProc, false);
		rs.wire(routingProcessor.newRequestOutputPort(GetOptionDataBySymbolDateRequest.class, GetOptionDataResponse.class), ODProc, true);
		rs.wire(routingProcessor.newRequestOutputPort(GetOptionDataByOptionIDRequest.class, GetOptionDataResponse.class), OIDProc, true);
		rs.wire(routingProcessor.newRequestOutputPort(GetUnderlyingDataBySymbolDateRequest.class, GetUnderlyingDataBySymbolDateResponse.class), UDProc, true);
		rs.wire(routingProcessor.newRequestOutputPort(GetAllTradeDatesRequest.class, GetAllTradeDatesResponse.class), TDProc, true);
		rs.wire(routingProcessor.newRequestOutputPort(GetLarkinBackTestingDataRequest.class, GetOptionDataResponse.class), BackTestProc, true);
		rs.wire(routingProcessor.newRequestOutputPort(GetLarkinPutsRequest.class, GetOptionDataResponse.class), PutsBackTestProc, true);
		rs.wire(FRProc.addOutputPortForValue(LoadFileMessage.TYPE_CURRENCY), IVYCurrencyProc, true);
		rs.wire(FRProc.addOutputPortForValue(LoadFileMessage.TYPE_SYMBOL_NAMES), IVYSecNamesProc, true);
		rs.wire(FRProc.addOutputPortForValue(LoadFileMessage.TYPE_SECURITY_PRICES), IVYSecPricesProc, false);
		rs.wire(FRProc.addOutputPortForValue(LoadFileMessage.TYPE_EXCHANGE_DATA), IVYExchangesProc, true);
		rs.wire(FRProc.addOutputPortForValue(LoadFileMessage.TYPE_HIST_VOL), IVYHistVolProc, true);
		rs.wire(FRProc.addOutputPortForValue(LoadFileMessage.TYPE_INDEX_DIV), IVYIndexDividendsProc, true);
		rs.wire(FRProc.addOutputPortForValue(LoadFileMessage.TYPE_DAILY_OPTION), IVYOptionPricesProc, false);
		rs.wire(FRProc.addOutputPortForValue(LoadFileMessage.TYPE_STD_OPTIONS), IVYStdOptionProc, true);
		rs.wire(FRProc.addOutputPortForValue(LoadFileMessage.TYPE_VOL_SURFACE), IVYVolSurfaceProc, true);
		rs.wire(FRProc.addOutputPortForValue(LoadFileMessage.TYPE_ZERO_CURVES), IVYZeroCurveProc, true);

		rs.wire(ActionProc.addOutputPortForValue(ActionMessage.TYPE_CALC_RETURNS), LPCalcReturnsProc, true);

		//Add support for a console connection
		bs.registerConsoleObject(CON, new MsgConsole(connection));

		output = rs.exposeInputPortAsOutput(routingProcessor, true);
		output1 = rs.exposeInputPortAsOutput(FRProc, true);
		IVYFTPFileProc.setOutput(output1);

		bs.startupContainer(container);

		TimeZone tz = container.getServices().getLocaleFormatter().getTimeZone();

		//In order to load options data properly we need the underlying data to be loaded first.
		if (SH.is(underSymbol)) {
			String[] symbols = SH.split(',', underSymbol);
			for (String s : symbols) {
				GetUnderlyingDataBySymbolDateRequest req = container.nw(GetUnderlyingDataBySymbolDateRequest.class);
				RequestMessage req2 = container.nw(RequestMessage.class);
				req.setUnderlyingSymbol(s);
				req.setQuoteDate1(new BasicDay(tz, Calendar.getInstance().getTime()));
				req.setQuoteDate2(new BasicDay(tz, Calendar.getInstance().getTime()));
				req2.setAction(req);
				output.send(req2, null);
			}
		}
		//Load all new data
		{
			GetAvailableIvyFiles msg = container.nw(GetAvailableIvyFiles.class);
			msg.setGetFiles((byte) 1);
			output.send(msg, null);
		}
		//Reload the underlyings because now it's missing a day ( or more) of data that was loaded in the previous stanza
		if (SH.is(underSymbol)) {
			String[] symbols = SH.split(',', underSymbol);
			for (String s : symbols) {
				GetUnderlyingDataBySymbolDateRequest req = container.nw(GetUnderlyingDataBySymbolDateRequest.class);
				RequestMessage req2 = container.nw(RequestMessage.class);
				req.setUnderlyingSymbol(s);
				req.setQuoteDate1(new BasicDay(tz, Calendar.getInstance().getTime()));
				req.setQuoteDate2(new BasicDay(tz, Calendar.getInstance().getTime()));
				req2.setAction(req);
				output.send(req2, null);
			}
		}
		if (SH.is(startDate) || SH.is(endDate) || SH.is(optionSymbol)) {

			SimpleDateFormat sdfSource = new SimpleDateFormat("yyyyMMdd");
			BasicDay date1 = null;
			BasicDay date2 = null;
			try {
				date1 = new BasicDay(tz, sdfSource.parse(startDate));
				date2 = new BasicDay(tz, sdfSource.parse(endDate));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String[] symbols = SH.split(',', optionSymbol);
			for (String s : symbols) {
				GetOptionDataBySymbolDateRequest req = container.nw(GetOptionDataBySymbolDateRequest.class);
				RequestMessage req2 = container.nw(RequestMessage.class);
				req.setUnderlyingSymbol(s);

				req.setQuoteDate1(date1);
				req.setQuoteDate2(date2);
				req.setQueryDatabase(true);

				req2.setAction(req);
				output.send(req2, null);
			}
			//	output.send(msg, null);
		}
		//output.send(fileNameTextMessage, null);

	}
}
