package app;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.joda.time.DateTime;

import app.controlpanel.ControlPanel;
import app.controlpanel.IControlPanel;
import app.fix42.FIXEngine;
import app.fix42.NewOrderSingleMsg;
import app.fix42.OrderExecutor;

import com.sjls.algos.eo.common.AlertMsg;
import com.sjls.algos.eo.common.AmendOrderRequestMsg;
import com.sjls.algos.eo.common.BrokerInfoUpdateMsg;
import com.sjls.algos.eo.common.EOException;
import com.sjls.algos.eo.common.IBinStatistics;
import com.sjls.algos.eo.common.IBrokerInfoUpdateMsg;
import com.sjls.algos.eo.common.IEMSServices;
import com.sjls.algos.eo.common.IExecutionOptimizer;
import com.sjls.algos.eo.common.INewOrderRequestMsg;
import com.sjls.algos.eo.common.IParentOrderStatusUpdateMsg;
import com.sjls.algos.eo.common.IStrategyUpdateMsg;
import com.sjls.algos.eo.common.OrderModifyException;
import com.sjls.algos.eo.common.QueryKey;
import com.sjls.algos.eo.common.Side;
import com.sjls.algos.eo.common.StrategyUpdateMsg;
import com.sjls.algos.eo.main.robeco.RobecoExecutionOptimizer;
import com.sjls.algos.eo.schedulers.PS.PSStockFile;
import com.sjls.algos.eo.utils.DirectoryIterator;

public class SbApp implements IEMSServices {
	public final static String CVS_ID = "$Id: SbApp.java,v 1.3 2011/12/07 19:10:49 olu Exp $";

	private static Logger m_logger = Logger.getLogger(SbApp.class);

	private static String m_useGUICP = "No";

	private final static ScheduledExecutorService m_executor = Executors.newScheduledThreadPool(1);

	private volatile IControlPanel m_controlPanel;
	private volatile FIXEngine m_fixEngine;
	private volatile IExecutionOptimizer m_exectnOptimizer;
	private final BinStatsCache m_binStatsCache = new BinStatsCache();
	private final DataOutputStream m_binStatsStream;

	private static final String ORDER_ID_TO_UPDATE = "AAPL395kSS";

	private static final String USE_GUI_CONTROL_PANEL = "USE_GUI_CONTROL_PANEL";

	private void setExecutionOptimizer(final IExecutionOptimizer exectnOptimizer) {
		m_exectnOptimizer = exectnOptimizer;
		m_fixEngine = new FIXEngine(new FIX42Client(exectnOptimizer));
		m_controlPanel = m_useGUICP.equalsIgnoreCase("Yes") ? new ControlPanel(m_exectnOptimizer) : new DummyControlPanel();
	}

	public IExecutionOptimizer getExecutionOptimizer() {
		return m_exectnOptimizer;
	}

	private SbApp() throws IOException {
		m_binStatsStream = new DataOutputStream(new FileOutputStream("output/bin_statistics.dat")); // File that will contain statistics!
	}

	public static void main(final String[] args) throws Exception {
		initLog4j();

		final String dataDir = System.getProperties().getProperty("DATA_DIR");
		m_useGUICP = System.getProperties().getProperty(USE_GUI_CONTROL_PANEL, "No");
		m_logger.info(USE_GUI_CONTROL_PANEL + " set to " + m_useGUICP);

		if (dataDir == null) {
			throw new InstantiationException("You need to specify DATA_DIR property!");
		}
		final DirectoryIterator dataDirIter = new DirectoryIterator(dataDir);
		OrderExecutor.init(dataDirIter); // so that the FIX engine can init with the list of Darkpools

		final PSStockFile stockFile = new PSStockFile(dataDirIter);

		final SbApp streamBaseApp = new SbApp();
		final IExecutionOptimizer exectnOptimizer = new RobecoExecutionOptimizer(streamBaseApp, System.getProperties()); // Supplied by SJLS
		streamBaseApp.setExecutionOptimizer(exectnOptimizer);

		final int durationInMins = args.length == 0 ? 15 : Integer.parseInt(args[0]);

		final LinkedList<TickAndOrderGenerator> listOfTasks = new LinkedList<TickAndOrderGenerator>();

		try {
			exectnOptimizer.startWork();

			final TickAndOrderGenerator jpmTask = TickAndOrderGenerator.newOrder("jpm1milla", exectnOptimizer, stockFile, "JPM", Side.Buy, 1000050);
			if (jpmTask != null) {
				jpmTask.setFuture(m_executor.scheduleAtFixedRate(jpmTask, 100, 500, TimeUnit.MILLISECONDS));
				listOfTasks.add(jpmTask);
			}
			final TickAndOrderGenerator brkbTask = TickAndOrderGenerator.newOrder("brkbOddLot", exectnOptimizer, stockFile, "BRK.B", Side.Buy, 922);
			if (brkbTask != null) {
				brkbTask.setFuture(m_executor.scheduleAtFixedRate(brkbTask, 15000, 1000, TimeUnit.MILLISECONDS));
				listOfTasks.add(brkbTask);
			}
			final TickAndOrderGenerator cienTask = TickAndOrderGenerator.newOrder("cien650K", exectnOptimizer, stockFile, "CIEN", Side.SellShort, 650255);
			if (cienTask != null) {
				final int delay = new DateTime().withTime(9, 35, 00, 0).getMillisOfDay() - new DateTime().getMillisOfDay(); // 5 min delay!!
				cienTask.setFuture(m_executor.scheduleAtFixedRate(cienTask, Math.max(5 * 1000 * 60, delay), 100, TimeUnit.MILLISECONDS));
				listOfTasks.add(cienTask);
			}
			final TickAndOrderGenerator gsTask = TickAndOrderGenerator.newOrder("gs15k", exectnOptimizer, stockFile, "GS", Side.SellShort, 15000);
			if (gsTask != null) {
				gsTask.setSendQuotes(false);
				gsTask.setFuture(m_executor.scheduleAtFixedRate(gsTask, 1000, 100, TimeUnit.MILLISECONDS));
				listOfTasks.add(gsTask);
			}
			//
			final TickAndOrderGenerator aapl2Task = TickAndOrderGenerator.newOrder(ORDER_ID_TO_UPDATE, exectnOptimizer, stockFile, "AAPL", Side.SellShort, 395001);
			if (aapl2Task != null) {
				aapl2Task.setFuture(m_executor.scheduleAtFixedRate(aapl2Task, 500, 500, TimeUnit.MILLISECONDS));
				listOfTasks.add(aapl2Task);
			}
			final TickAndOrderGenerator citiTask = TickAndOrderGenerator.newOrder("C400kBuy", exectnOptimizer, stockFile, "C", Side.Buy, 40003);
			if (citiTask != null) {
				final int delay = new DateTime().withTime(9, 30, 15, 0).getMillisOfDay() - new DateTime().getMillisOfDay(); // 15 sec delay!!
				citiTask.setFuture(m_executor.scheduleAtFixedRate(citiTask, Math.max(15000, delay), 25, TimeUnit.MILLISECONDS));
				listOfTasks.add(citiTask);
			}
			final TickAndOrderGenerator pruSellTask = TickAndOrderGenerator.newOrder("pruODDSell", exectnOptimizer, stockFile, "PRU", Side.Sell, 3);
			if (pruSellTask != null) {
				final int delay = new DateTime().withTime(10, 00, 00, 0).getMillisOfDay() - new DateTime().getMillisOfDay(); // 30 min delay!!
				pruSellTask.setFuture(m_executor.scheduleAtFixedRate(pruSellTask, Math.max(30 * 1000 * 60, delay), 100, TimeUnit.MILLISECONDS));
				listOfTasks.add(pruSellTask);
			}

			final TickAndOrderGenerator pruBuyTask = TickAndOrderGenerator.newOrder("pruBuy", exectnOptimizer, stockFile, "PRU", Side.Buy, 50011);
			if (pruBuyTask != null) {
				final int delay = new DateTime().withTime(9, 30, 00, 0).getMillisOfDay() - new DateTime().getMillisOfDay(); // 0 sec delay!!
				pruBuyTask.setFuture(m_executor.scheduleAtFixedRate(pruBuyTask, Math.max(0, delay), 100, TimeUnit.MILLISECONDS));
				listOfTasks.add(pruBuyTask);
			}

			m_executor.schedule(new Runnable() {
				@Override
				public void run() {
					try {
						makeSomeDummyChanges(exectnOptimizer);
					} catch (EOException e) {
						m_logger.error(e.getMessage(), e);
					}
				}
			}, 5 * 60, TimeUnit.SECONDS); // make changes after 5 mins

			final CountDownLatch latch = new CountDownLatch(1);

			// Schedule the killer
			m_executor.schedule(new Runnable() {
				public void run() {
					try {
						for (final TickAndOrderGenerator task : listOfTasks) {
							task.cancel();
						}
						exectnOptimizer.stopWork();
					} catch (Exception e) {
						m_logger.error(e.getMessage(), e);
					} finally {
						latch.countDown();
					}
				}
			}, durationInMins, TimeUnit.MINUTES); // Run for durationInMins mins

			latch.await();
			m_logger.info("End of DEMO");
		} catch (EOException e) {
			m_logger.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			m_logger.error(e.getMessage(), e);
		}
		System.exit(0);
	}

	private static void initLog4j() {
		final String log4jFile = System.getProperty("log4j");
		if (log4jFile == null || log4jFile.trim().length() == 0) {
			BasicConfigurator.configure();
			m_logger.warn("'log4j' system property not specified or is empty. Will use basic configurator...");
		} else {
			PropertyConfigurator.configure(log4jFile);
		}
	}

	private static void makeSomeDummyChanges(final IExecutionOptimizer exectnOptimizer) throws EOException {
		exectnOptimizer.updateBrokerInfo( /* Change the broker usage freq */
		new BrokerInfoUpdateMsg("SJLSALGO").setUpdateType(IBrokerInfoUpdateMsg.UpdateType.USAGE_FREQ).setUsageFrequency(25.2));
		//

		exectnOptimizer.updateOrderStrategy( /* Set a new trading limit price for jpm95kBuy */
		new StrategyUpdateMsg(ORDER_ID_TO_UPDATE).setUpdateType(IStrategyUpdateMsg.UpdateType.LIMIT_PRICE).setLimitPrice(300.01));

		/*
		 * 
		 * exectnOptimizer.updateBrokerInfo( // change available venues new BrokerInfoUpdateMsg("SJLS"). setUpdateType(IBrokerInfoUpdateMsg.UpdateType.VENUES).
		 * setAvaliableVenues(Arrays.asList("ARCA", "NYSE", "INET")) );
		 * 
		 * exectnOptimizer.updateSecurityMaster( new ISecMasterUpdateMsg() { // Add/Update a security Master entry dynamically
		 * //7280931|CROX.O|22704610|B0T7Z62|UW|CROX|US2270461096|Consumer Cyclical|Light Cyclical|APPAREL public String getJpmSecurityID() { return "7280931"; } public String
		 * getRIC() {return "CROX.O"; } public String getCusip() {return "22704610"; } public String getSedol() {return "B0T7Z62"; } public String getExchange() {return "UW"; }
		 * public String getTicker() {return "CROX"; } public String getISIN() {return "US2270461096"; } public String getJpmSector() {return "Consumer Cyclical"; } public String
		 * getJpmSubSector() {return "Light Cyclical"; } public String getJpmIndustry() {return "APPAREL"; } });
		 */
	}

	/**
	 * Protocol: Create clOrdID, send out the FIX msg and then return
	 */
	@Override
	public String sendNewChildOrder(final INewOrderRequestMsg req) {
		m_logger.info(String.format("sendNewChildOrder() request from [%s]. BlockID=%s:  Got NewOrderRequestMsg-->%s<--", req.getRequestingPool(), req.getBlockID(), req.toString()));

		final String clOrdID = OrderIDGenerator.getInstance().genNewID(req.getRequestingPool());
		m_logger.info(String.format("sendNewChildOrder() request from [%s]. BlockID=%s:  Generated ClOrdID=[%s]", req.getRequestingPool(), req.getBlockID(), clOrdID));

		final NewOrderSingleMsg nosMsg = new NewOrderSingleMsg(clOrdID, req);
		m_fixEngine.onMsg(nosMsg); // send the order to FIX engine

		return clOrdID;
	}

	@Override
	public void onParentOrderStatusUpdate(final IParentOrderStatusUpdateMsg msg) {
		m_logger.info("Got IParentOrderStatusUpdateMsg: ==>" + msg + "<==");
		if (m_controlPanel != null)
			m_controlPanel.onParentOrderStatsUpdate(msg);
	}

	@Override
	public void storeBinStatistics(final List<IBinStatistics> list) {
		m_logger.info("storeBinStatistics(): ==>" + list.toString());
		for (final IBinStatistics binStats : list) {
			m_binStatsCache.add(binStats);
			// Also, write out to disk!
			try {
				final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
				final ObjectOutputStream outStream = new ObjectOutputStream(byteStream);
				outStream.writeObject(binStats);
				outStream.close();
				m_binStatsStream.write(byteStream.toByteArray());
				m_binStatsStream.flush();
			} catch (final Exception e) {
				m_logger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Returns a list of bins that match the query key (list may be empty if no matches!!) If only a ticker is specified, returns ListOfBinStsts for that ticker If only a binNumber
	 * is specified, returns ListOfBinStsts for that bin Number If both are specified, returns a list containing at most one BinStatisticsn object Otherwise, (if ticker and bin
	 * number are both null) throws IOException
	 * 
	 */
	@Override
	public List<IBinStatistics> getBinStatistics(final QueryKey key) throws IOException {
		if (key == null) {
			throw new IOException("getBinStatistics():  QueryKey is null!!");
		}

		final String ticker = key.getTicker();
		final Integer binNum = key.getBinNumber();

		if (ticker == null && binNum == null) {
			throw new IOException("getBinStatistics(): Bad QueryKey. ticker AND binNumer are both NULL!!");
		}
		if (ticker == null) {
			return m_binStatsCache.getByBin(binNum);
		}
		if (binNum == null) {
			return m_binStatsCache.getByTicker(ticker);
		}
		// Both ticker and bin are specified
		final List<IBinStatistics> results = new LinkedList<IBinStatistics>();
		final List<IBinStatistics> list = m_binStatsCache.getByTicker(ticker);
		if (list == null) {
			throw new IOException(String.format("getBinStatistics(): Logic ERROR!! m_binStatsCache.getByTicker(%s) returned null!!!", ticker));
		}
		for (IBinStatistics stats : list) {
			if (stats.getBinNumber() == binNum.intValue()) {
				results.add(stats);
			}
		}
		return results;
	}

	@Override
	public void onControlPanelAlert(final AlertMsg msg) {
		m_logger.info("Msg from EO:-->" + msg);
		if (m_controlPanel != null)
			m_controlPanel.onAlert(msg.toString());
	}

	@Override
	public void cancelChildOrder(final String blockID, final String clOrdID) {
		m_logger.info(String.format("BlockID=%s: Requested cancel of child order %s", blockID, clOrdID));
		//
		try {
			m_fixEngine.cancel(blockID, clOrdID); // send the order to FIX engine
		} catch (OrderModifyException e) {
			m_logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Handle cxl/rpl.
	 * 
	 * Generate the new ClOrdID. Schedule the cxl/rpl operation for 1 sec in the future Return the new ClOrdID
	 */
	@Override
	public String amendChildOrder(final AmendOrderRequestMsg request) // throws OrderModifyException
	{
		m_logger.info(String.format("BlockID=%s: Requested CXL/RPL of child order %s", request.getBlockID(), request.getOrigClOrdID()));
		final String clOrdID = OrderIDGenerator.getInstance().genNewID(request.getRequestingPool());
		//
		m_executor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					m_fixEngine.cxlRpl(request, clOrdID); // send the order to FIX engine
				} catch (OrderModifyException e) {
					m_logger.error(e.getMessage(), e);
				}
			}
		}, 1, TimeUnit.SECONDS);
		return clOrdID;
	}

}
