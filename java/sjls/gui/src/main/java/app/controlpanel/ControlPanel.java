package app.controlpanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.swing.EventListModel;

import com.sjls.algos.eo.common.BrokerInfoUpdateMsg;
import com.sjls.algos.eo.common.EOException;
import com.sjls.algos.eo.common.IBinTradeData;
import com.sjls.algos.eo.common.IExecutionOptimizer;
import com.sjls.algos.eo.common.IParentOrderEvent;
import com.sjls.algos.eo.common.IParentOrderStatusUpdateMsg;
import com.sjls.algos.eo.common.IQuoteData;
import com.sjls.algos.eo.common.IStrategyUpdateMsg;
import com.sjls.algos.eo.common.ITradeData;
import com.sjls.algos.eo.common.ITradingParams;
import com.sjls.algos.eo.common.ParentOrderEventException;
import com.sjls.algos.eo.common.ParentOrderMsg;
import com.sjls.algos.eo.common.ReloadSJLSecMasterCmd;
import com.sjls.algos.eo.common.SchedulerAutoStartCmd;

/**
 * Main Control Panel GUI application that listens for order update messages from Rules engine The user of This class will be Rules Engine and register this class to listen for
 * order status updates
 * 
 * @author hsingh
 * 
 */
public class ControlPanel extends JFrame implements IControlPanel {

	private static Logger m_logger = Logger.getLogger(ControlPanel.class);

	private final OrderProgressChart m_orderChart;
	private final OrderTradingParamPanel m_stratInfo;
	private final EventList<String> m_ordersEventList;// = new
														// BasicEventList<String>();
	private final IExecutionOptimizer m_execOptzr;
	private final JTextArea m_ConsoleText;
	public static final int WINDOWSIZE_WIDTH = 960;
	public static final int WINDOWSIZE_HEIGHT = 900;
	private static final double chartWidthRatio = 0.8;
	private static final double chartHeightRatio = 0.75;

	private final Map<String, ParentOrderInfo> orderInfo = new ConcurrentHashMap<String, ParentOrderInfo>();
	private final int MAX_UPDATE_QUEUE_SIZE = 1000;
	private final LinkedBlockingQueue<IParentOrderStatusUpdateMsg> m_updateQueue = new LinkedBlockingQueue<IParentOrderStatusUpdateMsg>(MAX_UPDATE_QUEUE_SIZE);
	private String m_parenOrdSelected;// parent order id selected by user from
										// list

	private final static List<IBinTradeData> EMPTY_LIST = new LinkedList<IBinTradeData>();

	public ControlPanel(IExecutionOptimizer execOptzr) {
		m_execOptzr = execOptzr;
		m_stratInfo = new OrderTradingParamPanel(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int inset = 10;
		setBounds(inset, inset, (int) (0.75 * screenSize.width) - inset * 2, (int) (0.75 * screenSize.height) - inset * 2);

		// try to set look and feel same as that of native system
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			m_logger.error(e.getMessage(), e);
		}
		m_ordersEventList = GlazedLists.threadSafeList(new BasicEventList<String>());
		// initialize Glazed List event list that will update Jlist of Parent
		// OrderIDs
		final EventListModel<String> ordersListModel = new EventListModel<String>(m_ordersEventList);
		final JList ordersJList = new JList(ordersListModel);
		ordersJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ordersJList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				Object selection = ordersJList.getSelectedValue();
				if (selection != null) {// on select submit task to Swing event
										// Queue to change chart and trading
										// param on GUI
					m_parenOrdSelected = (String) selection;
					if (!e.getValueIsAdjusting())
						SwingUtilities.invokeLater(new OrderIDSelector(orderInfo.get(selection)));
				}
			}
		});
		final JScrollPane issuesListScrollPane = new JScrollPane(ordersJList);
		m_orderChart = new OrderProgressChart((int) (chartWidthRatio * WINDOWSIZE_WIDTH), (int) (chartHeightRatio * WINDOWSIZE_WIDTH));

		JPanel chartPanel = m_orderChart.getPanel();
		chartPanel.setVisible(true);
		final JPanel listPane = new JPanel();
		listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
		final JLabel lblParentOrd = new JLabel("Parent Orders");
		listPane.add(lblParentOrd);
		listPane.add(issuesListScrollPane);
		final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPane, chartPanel);
		splitPane.setResizeWeight(1 - 1.1 * chartWidthRatio);
		splitPane.setOneTouchExpandable(true);
		splitPane.setContinuousLayout(true);

		m_stratInfo.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		add(m_stratInfo, BorderLayout.PAGE_START);
		splitPane.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		add(splitPane, BorderLayout.CENTER);
		// add(m_stratInfo, BorderLayout.CENTER);
		m_ConsoleText = new JTextArea(5, 20);
		m_ConsoleText.setLineWrap(true);
		m_ConsoleText.setEditable(false);
		m_ConsoleText.setWrapStyleWord(true);
		m_ConsoleText.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		add(new JScrollPane(m_ConsoleText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), BorderLayout.PAGE_END);

		final Thread updateProcessor = new Thread(new OrderUpdateProcessor());
		updateProcessor.start();
		setTitle("SJLS Control Panel");
		setVisible(true);
	}

	void startExecutionOptimizer() throws EOException {
		m_ConsoleText.append("\nStarting Execution Optimizer.. ");
		m_execOptzr.startWork();
		m_ConsoleText.append("\nStarted. ");
	}

	void setAutoTrade(final boolean defualtAutoTrade) throws EOException {
		m_ConsoleText.append("\nSetting AutoTrade.. ");
		m_logger.info(" Setting AutoTrade " + defualtAutoTrade);
		m_execOptzr.onCommand(new SchedulerAutoStartCmd(defualtAutoTrade));
		m_ConsoleText.append("\n Done Setting AutoTrade.. ");
	}

	void reloadSecurityMaster() throws EOException {
		m_ConsoleText.append("\nReloading Security Master.. ");
		m_logger.info(" Reloading Security Master ");
		m_execOptzr.onCommand(new ReloadSJLSecMasterCmd());
		m_ConsoleText.append("\nDone Reloading Security Master. ");
	}

	void stopExecutionOptimizer() throws EOException {
		m_ConsoleText.append("\nStopping Execution Optimizer.. ");
		m_execOptzr.stopWork();
		m_ConsoleText.append("\nStopped. ");
	}

	void updateOrderStrategy(IStrategyUpdateMsg update) throws EOException {
		m_ConsoleText.append("\nSending Strat Update(type=" + update.getUpdateType() + ").. " + update.toString());
		m_execOptzr.updateOrderStrategy(update);
		m_ConsoleText.append("\nSent Strat Update.. ");
	}

	public void onParentOrderEvent(IParentOrderEvent event) throws ParentOrderEventException {
		m_ConsoleText.append("\nSending Order.. " + event.toString());
		m_execOptzr.onParentOrderEvent(event);
		m_ConsoleText.append("\nSent Order: ");
	}

	/**
	 * Receive and process a trade
	 */
	public void onTrade(ITradeData trade) {
		m_ConsoleText.append("\nSending Trade.. " + trade.toString());
		m_execOptzr.onTrade(trade);
		m_ConsoleText.append("\nSent Trade: ");
	}

	/**
	 * Receive and process a quote
	 */
	public void onQuote(IQuoteData quote) {
		m_ConsoleText.append("\nSending Quote.. " + quote.toString());
		m_execOptzr.onQuote(quote);
		m_ConsoleText.append("\nSent Quote.. ");

	}

	public void updateBrokerInfo(final BrokerInfoUpdateMsg updateMsg) {
		m_ConsoleText.append("\nUpdating Broker Info.. " + updateMsg.getBrokerID() + " Type=" + updateMsg.getUpdateType());
		try {
			m_execOptzr.updateBrokerInfo(updateMsg);
		} catch (EOException e) {
			m_logger.error("Error processing BrokerInfoUpdateMsg, Details: ", e);
		}
		m_ConsoleText.append("\nUpdated Broker Info!!!");
	}

	private void processUpdate(final IParentOrderStatusUpdateMsg message) {
		if (message.getTradingPlan() != null || message.getAlgoParams() != null) {// initial
																					// info
																					// about
																					// order
			// System.out.println(" INITIAL BLOCK INFO.... ID = "+message.getBlockID());
			final ParentOrderInfo oldValue = orderInfo.get(message.getBlockID());
			if (oldValue == null) {
				m_ordersEventList.add(message.getBlockID());
				orderInfo.put(message.getBlockID(), new ParentOrderInfo(null, message.getBlockID(), message.getTradingPlan()));
			} else {
				final ITradingParams oldParams = oldValue.getTradingParams();
				final List<IBinTradeData> oldPlan = oldValue.getInitialPlan();
				orderInfo.put(message.getBlockID(),
				// new ParentOrderInfo(message.getAlgoParams() != null ? message.getAlgoParams() : oldParams, message.getBlockID(), message
				// .getTradingPlan() != null ? message.getTradingPlan() : oldPlan));
						new ParentOrderInfo(null, message.getBlockID(), message.getTradingPlan() != null ? message.getTradingPlan() : oldPlan));
				if (message.getBlockID().equals(m_parenOrdSelected)) {
					final ParentOrderMsg poMsg = m_stratInfo.getParentOrderInfo(message.getBlockID());

					if (poMsg != null && message.getTradingPlan() != null) {
						// m_orderChart.setLiveData(message.getBlockID(),
						// message.getBinTradeInfo());
						m_orderChart.setInitialData(message.getBlockID(), poMsg.ticker, poMsg.side.name(), poMsg.blockQty, poMsg.limitPrice, message.getTradingPlan(), EMPTY_LIST);
					} else {
						m_orderChart.setInitialData(message.getBlockID(), message.getTradingPlan(), EMPTY_LIST);
					}
				}
			}
		} else {
			ParentOrderInfo order = orderInfo.get(message.getBlockID());
			if (order != null) {
				synchronized (order) {
					order.getLiveTrades().add(message.getBinTradeInfo());
				}
				// update update is for selected order id then send update to
				// Chart and Trading Parameter GUI
				if (m_parenOrdSelected != null && m_parenOrdSelected.equals(message.getBlockID())) {
					SwingUtilities.invokeLater(new OrderGUIUpdater(message.getBlockID(), message.getBinTradeInfo()));
				}
			} else {
				m_logger.warn(String.format("BlockID=%s: Received an update-->%s<--But initial data not present in cache. prob out of seq msg", message.getBlockID(),
						message.toString()));

			}
		}
		// m_orderChart.setLiveData(message.getBlockID(),
		// message.getBinTradeInfo());

	}

	// @Override
	public void onParentOrderStatsUpdate(final IParentOrderStatusUpdateMsg msg) {
		m_logger.info(String.format("BlockID=%s: Received an update-->%s<--", msg.getBlockID(), msg.toString()));
		boolean sucess = m_updateQueue.offer(msg);
		if (!sucess)
			throw new RuntimeException("Couldn't process Order Status QUEUE is full");
	}

	private class OrderIDSelector implements Runnable {
		private final ParentOrderInfo m_parentOrd;

		OrderIDSelector(final ParentOrderInfo parentOrd) {
			m_parentOrd = parentOrd;
		}

		@Override
		public void run() {
			// ParentOrderInfo parentOrd=orderInfo.get(orderid);
			if (m_parentOrd != null) {
				synchronized (m_parentOrd) {
					m_stratInfo.setSelectedParentOrd(m_parentOrd);
					final ParentOrderMsg poMsg = m_stratInfo.getParentOrderInfo(m_parentOrd.getBlockId());
					if (poMsg != null) {
						m_orderChart.setInitialData(m_parentOrd.getBlockId(), poMsg.ticker, poMsg.side.name(), poMsg.blockQty, poMsg.limitPrice, m_parentOrd.getInitialPlan(),
								EMPTY_LIST);
					} else {
						m_orderChart.setInitialData(m_parentOrd.getBlockId(), m_parentOrd.getInitialPlan(), EMPTY_LIST);
					}
				}
			}
		}

	}

	private class OrderGUIUpdater implements Runnable {
		private final String m_pOrdId;
		private final IBinTradeData m_bindata;

		OrderGUIUpdater(final String pOrdId, final IBinTradeData bindata) {
			m_pOrdId = pOrdId;
			m_bindata = bindata;
		}

		@Override
		public void run() {
			m_orderChart.setLiveData(m_pOrdId, m_bindata);
		}

	}

	private class OrderUpdateProcessor implements Runnable {
		public void run() {

			try {
				while (true) {
					// if update available then process else wait for update
					IParentOrderStatusUpdateMsg message = m_updateQueue.take();
					processUpdate(message);
				}
			} catch (Exception e) {
				m_logger.error("Error processing IParentOrderStatusUpdateMsg, Details: ", e);
			}

		}

	}

	@Override
	public void onAlert(final String msg) {
		m_ConsoleText.append("\n" + msg);
	}
}
