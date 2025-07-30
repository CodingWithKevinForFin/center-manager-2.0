package app.controlpanel;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.sjls.algos.eo.common.AlgoParams;
import com.sjls.algos.eo.common.AlgoParamsVWAP;
import com.sjls.algos.eo.common.BrokerInfoUpdateMsg;
import com.sjls.algos.eo.common.EOException;
import com.sjls.algos.eo.common.IBrokerInfoUpdateMsg;
import com.sjls.algos.eo.common.IStrategyUpdateMsg.UpdateType;
import com.sjls.algos.eo.common.ITradingParams;
import com.sjls.algos.eo.common.ModifyParentOrderEvent;
import com.sjls.algos.eo.common.NewParentOrderEvent;
import com.sjls.algos.eo.common.ParentOrderEventException;
import com.sjls.algos.eo.common.ParentOrderMsg;
import com.sjls.algos.eo.common.QuoteData;
import com.sjls.algos.eo.common.Side;
import com.sjls.algos.eo.common.Speed;
import com.sjls.algos.eo.common.StrategyUpdateMsg;
import com.sjls.algos.eo.common.TimeInForce;
import com.sjls.algos.eo.common.TradeData;
import com.sjls.algos.eo.utils.DateUtils;
import com.sjls.tcm.pretrade.RiskTolerance;

/**
 * 
 * Displays Order Trading Parameters about a selected Parent Order also can be use to update These parameters for selected order in Rules Engine
 * 
 * @author hsingh
 * 
 */
public class OrderTradingParamPanel extends JPanel {
	private static final long serialVersionUID = 14500011L;

	private static Logger m_logger = Logger.getLogger(OrderTradingParamPanel.class);
	private final JComboBox m_speedList = new JComboBox(Speed.values());// new
																		// String[]{"Default","VERY AGGRESSIVE","AGGRESSIVE","MEDIUM","PASSIVE","VERY PASSIVE"});
	private final JCheckBox m_chkAutotrade = new JCheckBox();
	private final JCheckBox m_chkAdminAutotrade = new JCheckBox();
	private final JCheckBox m_chkEod = new JCheckBox();
	private final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
	private final NumberFormat NUMBER_FORMAT = NumberFormat.getIntegerInstance();
	private final JTextField m_txtOrderSymbol = getTextFld("", 5);
	private final JComboBox m_txtOrderSide = new JComboBox(Side.values());// new String[]{"BUY","SELL","SHORT"});
	private final JComboBox m_cmbRiskTolerances = new JComboBox();// new String[]{"BUY","SELL","SHORT"});
	private final JTextField m_txtOrderPx = getDecimalFld(0, 5);
	private final JTextField m_txtOrderQty = getNumericFld(0, 5);

	private final JTextField m_txtQuoteSymbol = getTextFld("", 5);
	private final JTextField m_txtQuoteBidPx = getDecimalFld(0, 5);
	private final JTextField m_txtQuoteBidQty = getNumericFld(0, 5);
	private final JTextField m_txtQuoteAskPx = getDecimalFld(0, 5);
	private final JTextField m_txtQuoteAskQty = getNumericFld(0, 5);

	private final JTextField m_txtTradeSymbol = getTextFld("", 5);
	private final JTextField m_txtTradePx = getDecimalFld(0, 5);
	private final JTextField m_txtTradeQty = getNumericFld(0, 5);
	private final JTextField m_txtTradeVol = getNumericFld(0, 5);

	private final JTextField m_txtPriceChange = getDecimalFld(0, 5);

	private static AtomicInteger nextBlockID = new AtomicInteger();
	private final List<String> m_excludeBrokerList = Arrays.asList("GSCO", "WACH", "MSCO");
	private ParentOrderInfo m_SelectedParentOrd;

	private final JTextField m_txtBrokerID = getTextFld("", 5);
	private final JTextField m_txtBrokerInfo = getTextFld("", 15);
	private final Map<String, ParentOrderMsg> m_parentOrderMsgCache = new ConcurrentHashMap<String, ParentOrderMsg>();
	// private final SimpleDateFormat timeformater= new SimpleDateFormat("HH:mm:ss");

	private final JTextField m_txtOrderStartTime = getTextFld("", 8);

	// private final JFormattedTextField m_txtOrderEndTime = new JFormattedTextField(timeformater);

	private final ControlPanel m_cp;

	public OrderTradingParamPanel(ControlPanel cp) {
		super(new GridLayout(1, 1));
		m_cp = cp;
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Speed", null, createSpeedUpdatePanel(), "Adjust speed of execution");
		tabbedPane.addTab("Completion", null, createEODUpdatePanel(), "Complete the order at End of day");
		tabbedPane.addTab("AutoTrade", null, createAutotradeUpdatePanel(), "Auto/Manual Trade");
		tabbedPane.addTab("Change Px/Cancel", null, createCancelUpdatePanel());
		tabbedPane.addTab("BrokerUpdate", null, createUpdateBrokerInfoPanel(), "Update Usage frequency of Broker");
		tabbedPane.addTab("New Order", null, createTestOrderPanel(), "Send new Test Parent Order to EO");
		tabbedPane.addTab("Admin", null, createAdminPanel(), "Start/Stop Execution Optimizer");
		tabbedPane.addTab("Mkt Quote", null, createTestQuotePanel(), "Sends Test Mkt Quote");
		tabbedPane.addTab("Mkt Trade", null, createTestTradePanel(), "Sends Test Mkt Trade");
		add(tabbedPane);
		final DateTime now = new DateTime();
		m_txtOrderStartTime.setText(now.toString(DateUtils.TIME_FMT));
		// m_txtOrderEndTime.setValue(now.toDateMidnight().toDateTime().plusHours(16).toDate());
		// The following line enables to use scrolling tabs.
		m_cmbRiskTolerances.addItem(RiskTolerance.HIGHEST);
		m_cmbRiskTolerances.addItem(RiskTolerance.HIGH);
		m_cmbRiskTolerances.addItem(RiskTolerance.MEDIUM);
		m_cmbRiskTolerances.addItem(RiskTolerance.LOW);
		m_cmbRiskTolerances.addItem(RiskTolerance.LOWEST);

		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
	}

	private JComponent createCancelUpdatePanel() {
		JPanel panel = new JPanel(false);
		panel.setLayout(new FlowLayout());
		panel.add(getLabel("New Px", JLabel.CENTER));
		panel.add(m_txtPriceChange);
		panel.add(getButton("Change Price", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (!isParentOrdSelected())
						return;
					final StrategyUpdateMsg stgyUpdate = new StrategyUpdateMsg(m_SelectedParentOrd.getBlockId());
					stgyUpdate.setUpdateType(UpdateType.LIMIT_PRICE);
					stgyUpdate.setLimitPrice(validateAndGetDouble("New Price", m_txtPriceChange.getText()));
					m_cp.updateOrderStrategy(stgyUpdate);
				} catch (Exception e) {
					m_logger.error(e.getMessage(), e);
					handleError(e.getMessage());
				}
			}
		}));

		panel.add(getButton("Cancel Order", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					final StrategyUpdateMsg stgyUpdate = new StrategyUpdateMsg(m_SelectedParentOrd.getBlockId());
					stgyUpdate.setUpdateType(null);
					m_cp.updateOrderStrategy(stgyUpdate);
				} catch (EOException e) {
					m_logger.error(e.getMessage(), e);
					handleError(e.getMessage());
				}
				// handleError("Cancel Order Not yet Implemented");
			}
		}));
		return panel;
	}

	private JComponent createSpeedUpdatePanel() {
		JPanel panel = new JPanel(false);
		JLabel filler = new JLabel("Speed");
		filler.setHorizontalAlignment(JLabel.CENTER);
		panel.setLayout(new FlowLayout());
		panel.add(filler);
		panel.add(m_speedList);
		panel.add(getButton("Apply", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (!isParentOrdSelected())
						return;
					final StrategyUpdateMsg stgyUpdate = new StrategyUpdateMsg(m_SelectedParentOrd.getBlockId());
					stgyUpdate.setUpdateType(UpdateType.SPEED);
					stgyUpdate.setSpeed((Speed) m_speedList.getSelectedItem());
					m_cp.updateOrderStrategy(stgyUpdate);
				} catch (Exception e) {
					m_logger.error(e.getMessage(), e);
					handleError(e.getMessage());
				}
			}
		}));
		return panel;
	}

	private boolean isParentOrdSelected() {
		if (m_SelectedParentOrd == null) {
			handleError("Please select a Parent Order from the list to change the strategy.");
			return false;
		} else
			return true;
	}

	private JComponent createAutotradeUpdatePanel() {
		JPanel panel = new JPanel(false);
		panel.setLayout(new FlowLayout());
		panel.add(getLabel("AutoTrade", JLabel.CENTER));
		panel.add(m_chkAutotrade);
		panel.add(getButton("Apply", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (!isParentOrdSelected())
						return;
					final StrategyUpdateMsg stgyUpdate = new StrategyUpdateMsg(m_SelectedParentOrd.getBlockId());
					stgyUpdate.setUpdateType(UpdateType.AUTOTRADE);
					stgyUpdate.setAutotrade(m_chkAutotrade.isSelected());
					m_cp.updateOrderStrategy(stgyUpdate);
				} catch (Exception e) {
					m_logger.error(e.getMessage(), e);
					handleError(e.getMessage());
				}
			}
		}));
		return panel;
	}

	private JComponent createEODUpdatePanel() {
		final JPanel panel = new JPanel(false);
		panel.setLayout(new FlowLayout());
		panel.add(getLabel("Complete at EOD", JLabel.LEFT));
		panel.add(m_chkEod);
		panel.add(getButton("Apply", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (!isParentOrdSelected())
						return;
					final StrategyUpdateMsg stgyUpdate = new StrategyUpdateMsg(m_SelectedParentOrd.getBlockId());
					stgyUpdate.setUpdateType(UpdateType.COMPLETION_TIME);
					stgyUpdate.setMustCompleteByEODFlag(m_chkEod.isSelected());
					m_cp.updateOrderStrategy(stgyUpdate);
				} catch (Exception e) {
					m_logger.error(e.getMessage(), e);
					handleError(e.getMessage());
				}
			}
		}));
		return panel;
	}

	private JComponent createAdminPanel() {
		final JPanel panel = new JPanel(false);
		panel.setLayout(new FlowLayout());
		panel.add(m_chkAdminAutotrade);
		panel.add(getButton("Set AutoTrade", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					m_cp.setAutoTrade(m_chkAdminAutotrade.isSelected());
				} catch (Exception e) {
					m_logger.error(e.getMessage(), e);
					handleError(e.getMessage());
				}
			}
		}));
		panel.add(getButton("Reload SM", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					m_cp.reloadSecurityMaster();
				} catch (Exception e) {
					m_logger.error(e.getMessage(), e);
					handleError(e.getMessage());
				}
			}
		}));

		panel.add(getButton("Start", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					m_cp.startExecutionOptimizer();
				} catch (Exception e) {
					m_logger.error(e.getMessage(), e);
					handleError(e.getMessage());
				}
			}
		}));
		panel.add(getButton("Stop", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					m_cp.stopExecutionOptimizer();
				} catch (Exception e) {
					m_logger.error(e.getMessage(), e);
					handleError(e.getMessage());
				}
			}
		}));
		return panel;
	}

	private JComponent createTestOrderPanel() {
		final JPanel panel = new JPanel(false);
		panel.add(getLabel("StartTime", JLabel.LEFT));
		panel.add(m_txtOrderStartTime);
		// panel.add(getLabel("EndTime", JLabel.LEFT));
		// panel.add(m_txtOrderEndTime);
		panel.setLayout(new FlowLayout());
		panel.add(getLabel("Side", JLabel.LEFT));
		panel.add(m_txtOrderSide);
		panel.add(getLabel("Quantity", JLabel.LEFT));
		panel.add(m_txtOrderQty);
		panel.add(getLabel("Symbol", JLabel.LEFT));
		panel.add(m_txtOrderSymbol);
		panel.add(getLabel("Price", JLabel.LEFT));
		panel.add(m_txtOrderPx);
		panel.add(getLabel("Risk", JLabel.LEFT));
		panel.add(m_cmbRiskTolerances);
		panel.add(getButton("Send", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					final String blockID = "CPOrd" + nextBlockID.incrementAndGet();
					final String symbol = m_txtOrderSymbol.getText();
					final Side side = (Side) m_txtOrderSide.getSelectedItem();
					final int qty = validateAndGetInt("Order Qty", m_txtOrderQty.getText());
					final double px = validateAndGetDouble("Order Px", m_txtOrderPx.getText());
					final String timeStr = m_txtOrderStartTime.getText();
					final DateTime dt = DateTimeFormat.forPattern("HH:mm:ss").parseDateTime(timeStr);
					final Date argStartTime = new DateTime().withMillisOfDay(dt.getMillisOfDay()).toDate();
					final ParentOrderMsg poMsg = genParentOrderMsg(blockID, side, qty, symbol, px, argStartTime, (Integer) m_cmbRiskTolerances.getSelectedItem());
					m_cp.onParentOrderEvent(new NewParentOrderEvent(poMsg));
					m_parentOrderMsgCache.put(poMsg.blockID, poMsg);
				} catch (ParentOrderEventException e) {
					m_logger.error(e.getMessage(), e);
					handleError(e.getMessage());
				} catch (Exception e) {
					m_logger.error(e.getMessage(), e);
					handleError(e.getMessage());
				}

			}

		}));
		panel.add(getButton("Modify", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (!isParentOrdSelected())
						return;
					final ParentOrderMsg poMsg = m_parentOrderMsgCache.get(m_SelectedParentOrd.getBlockId());
					if (poMsg != null) {
						final int qty = validateAndGetInt("Order Qty", m_txtOrderQty.getText());
						final double px = validateAndGetDouble("Order Px", m_txtOrderPx.getText());
						poMsg.blockQty = qty;
						poMsg.limitPrice = px;
						m_cp.onParentOrderEvent(new ModifyParentOrderEvent(poMsg));
					} else {
						handleError("Order was not created by Control Panel,\nCann't Modify.");
					}
				} catch (ParentOrderEventException e) {
					m_logger.error(e.getMessage(), e);
					handleError(e.getMessage());
				} catch (Exception e) {
					m_logger.error(e.getMessage(), e);
					handleError(e.getMessage());
				}
			}
		}));
		return panel;
	}

	private JComponent createTestQuotePanel() {
		final JPanel panel = new JPanel(false);
		panel.setLayout(new FlowLayout());
		panel.add(getLabel("Symbol", JLabel.LEFT));
		panel.add(m_txtQuoteSymbol);
		panel.add(getLabel("Bidpx", JLabel.LEFT));
		panel.add(m_txtQuoteBidPx);
		panel.add(getLabel("BidQty", JLabel.LEFT));
		panel.add(m_txtQuoteBidQty);
		panel.add(getLabel("AskPx", JLabel.LEFT));
		panel.add(m_txtQuoteAskPx);
		panel.add(getLabel("AskQty", JLabel.LEFT));
		panel.add(m_txtQuoteAskQty);
		panel.add(getButton("Send", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					final QuoteData data = new QuoteData(m_txtQuoteSymbol.getText());
					data.setBidSize(validateAndGetInt("Bid Qty", m_txtQuoteBidQty.getText()));
					data.setAskSize(validateAndGetInt("Ask Qty", m_txtQuoteAskQty.getText()));
					data.setBidPrice(validateAndGetDouble("Bid Px", m_txtQuoteBidPx.getText()));
					data.setAskPrice(validateAndGetDouble("Ask Px", m_txtQuoteAskPx.getText()));
					m_cp.onQuote(data);

				} catch (Exception e) {
					m_logger.error(e.getMessage(), e);
					handleError(e.getMessage());
				}
			}

		}));
		return panel;
	}

	private JComponent createUpdateBrokerInfoPanel() {
		JPanel panel = new JPanel(false);
		panel.setLayout(new FlowLayout());
		panel.add(getLabel("BrokerID", JLabel.CENTER));
		panel.add(m_txtBrokerID);
		panel.add(getLabel("Set", JLabel.CENTER));

		final JComboBox updateTypes = new JComboBox();
		updateTypes.addItem(IBrokerInfoUpdateMsg.UpdateType.USAGE_FREQ);
		updateTypes.addItem(IBrokerInfoUpdateMsg.UpdateType.VENUES);
		updateTypes.addItem(IBrokerInfoUpdateMsg.UpdateType.DARKPOOLS);
		panel.add(updateTypes);
		panel.add(getLabel("=", JLabel.CENTER));
		panel.add(m_txtBrokerInfo);
		panel.add(getButton("Change", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (!isParentOrdSelected())
						return;
					final BrokerInfoUpdateMsg updateMsg = new BrokerInfoUpdateMsg(m_txtBrokerID.getText());
					final IBrokerInfoUpdateMsg.UpdateType updType = (IBrokerInfoUpdateMsg.UpdateType) updateTypes.getSelectedItem();
					updateMsg.setUpdateType(updType);
					if (updType == IBrokerInfoUpdateMsg.UpdateType.USAGE_FREQ) {
						updateMsg.setUsageFrequency(Double.parseDouble(m_txtBrokerInfo.getText()));
					} else if (updType == IBrokerInfoUpdateMsg.UpdateType.VENUES) {
						final List<String> venueList = Arrays.asList(m_txtBrokerInfo.getText().split(","));
						updateMsg.setAvaliableVenues(venueList);
					} else if (updType == IBrokerInfoUpdateMsg.UpdateType.DARKPOOLS) {
						final List<String> venueList = Arrays.asList(m_txtBrokerInfo.getText().split(","));
						updateMsg.setAvaliableDarkPools(venueList);
					}
					m_cp.updateBrokerInfo(updateMsg);

				} catch (Exception e) {
					m_logger.error(e.getMessage(), e);
					handleError(e.getMessage());
				}
			}
		}));

		return panel;
	}

	private JComponent createTestTradePanel() {
		final JPanel panel = new JPanel(false);
		panel.setLayout(new FlowLayout());
		panel.add(getLabel("Quantity", JLabel.LEFT));
		panel.add(m_txtTradeQty);
		panel.add(getLabel("Symbol", JLabel.LEFT));
		panel.add(m_txtTradeSymbol);
		panel.add(getLabel("Price", JLabel.LEFT));
		panel.add(m_txtTradePx);
		panel.add(getLabel("Day's Volume", JLabel.LEFT));
		panel.add(m_txtTradeVol);
		panel.add(getButton("Send", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					final TradeData data = new TradeData(m_txtTradeSymbol.getText());
					data.setTradePrice(validateAndGetDouble("Trade Px", m_txtTradePx.getText()));
					data.setTradeSize(validateAndGetInt("Trade Qty", m_txtTradeQty.getText()));
					data.setAccumShares(validateAndGetInt("Day's Vol", m_txtTradeVol.getText()));
					m_cp.onTrade(data);
				} catch (Exception e) {
					m_logger.error(e.getMessage(), e);
					handleError(e.getMessage());
				}

			}

		}));
		return panel;
	}

	private JLabel getLabel(final String txt, final int align) {
		final JLabel lbl = new JLabel(txt);
		lbl.setHorizontalAlignment(align);
		return lbl;
	}

	private JButton getButton(final String txt, final ActionListener actListener) {
		final JButton btn = new JButton(txt);
		btn.addActionListener(actListener);
		return btn;
	}

	private JTextField getTextFld(final String txtDef, final int size) {
		final JTextField txtFld = new JTextField(txtDef, size);
		return txtFld;
	}

	private JTextField getNumericFld(final int initVal, final int size) {
		final JFormattedTextField tft1 = new JFormattedTextField(NUMBER_FORMAT);
		tft1.setColumns(5);
		// tft1.setValue(new Integer(initVal));
		return tft1;
	}

	private JTextField getDecimalFld(final double initVal, final int size) {
		final JFormattedTextField tft1 = new JFormattedTextField(DECIMAL_FORMAT);
		tft1.setColumns(5);
		// tft1.setValue(new Double(initVal));
		return tft1;
	}

	private int validateAndGetInt(final String fieldName, final String input) throws IllegalArgumentException, ParseException {
		final int value = (input == null || input.trim().length() == 0) ? 0 : NUMBER_FORMAT.parse(input).intValue();
		if (value <= 0)
			throw new IllegalArgumentException(fieldName + " must be >0");
		return value;
	}

	private double validateAndGetDouble(final String fieldName, final String input) throws IllegalArgumentException, ParseException {
		final double value = (input == null || input.trim().length() == 0) ? 0 : DECIMAL_FORMAT.parse(input).doubleValue();
		if (value < 0)
			throw new IllegalArgumentException(fieldName + " must be >=0");
		return value;
	}

	private ParentOrderMsg genParentOrderMsg(final String blockID, final Side side, final int blockQty, final String ticker, final double limitPx, final Date startTime,
			final Integer riskTol) {
		final ParentOrderMsg poMsg = new ParentOrderMsg();
		poMsg.blockGenTime = new Date();
		poMsg.blockID = blockID;
		poMsg.side = side;
		poMsg.blockQty = blockQty;
		poMsg.securityID = ticker;
		poMsg.ticker = ticker;
		poMsg.limitPrice = limitPx;
		poMsg.borrowLocateString = null;
		poMsg.deskID = "desk1";
		poMsg.PM = "Denniss Kidd";
		poMsg.PMGroup = "group1";
		poMsg.restrictedBrokers = m_excludeBrokerList; // dont send this order to these brokers
		poMsg.timeInForce = TimeInForce.DAY;
		final AlgoParams p = new AlgoParamsVWAP();
		p.setStartTime(new DateTime(startTime));
		p.setEndTime(DateUtils.getUSMarketCloseTime());
		// p.setRiskTolerance(riskTol);
		p.setLowerPct(null); // Use default!
		p.setUpperPct(null); // Use default
		// TODO: use onOpenAmount(...)? p.setOpenPercent(null); // Use default
		p.setParticipateOnClose(true);
		p.setParticipateOnOpen(true);
		poMsg.algoParams = p;

		return poMsg;
	}

	public void setTradingParam(final ITradingParams tradingParam) {
		if (tradingParam != null) {
			m_speedList.setSelectedItem(tradingParam.getSpeed());
			m_chkAutotrade.setSelected(tradingParam.isAutotrade());
			m_txtPriceChange.setText(tradingParam.getLimitPrice().toString());
		}
	}

	private void handleError(final String errorMsg) {
		m_logger.error(errorMsg);
		JOptionPane.showMessageDialog(this, errorMsg, "Strategy Update", JOptionPane.ERROR_MESSAGE);
	}

	public ParentOrderMsg getParentOrderInfo(final String blockId) {
		return m_parentOrderMsgCache.get(blockId);
	}

	public void setSelectedParentOrd(final ParentOrderInfo m_SelectedParentOrd) {
		this.m_SelectedParentOrd = m_SelectedParentOrd;
		setTradingParam(m_SelectedParentOrd.getTradingParams());
		final ParentOrderMsg poMsg = m_parentOrderMsgCache.get(m_SelectedParentOrd.getBlockId());
		if (poMsg != null) {
			m_txtOrderSymbol.setText(poMsg.ticker);
			m_txtOrderSide.setSelectedItem(poMsg.side);
			m_txtOrderQty.setText(Long.toString(poMsg.blockQty));
			m_txtOrderPx.setText(Double.toString(poMsg.limitPrice));

		} else {
			m_txtOrderSymbol.setText("");
			m_txtOrderSide.setSelectedItem(Side.Buy);
			m_txtOrderQty.setText("");
			m_txtOrderPx.setText("");
		}

	}

}
