package com.f1.fixclientsim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import quickfix.Field;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.SenderSubID;

import com.f1.bootstrap.Bootstrap;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.fixclientsim.EventsPanel.OrderEntry;
import com.f1.fixclientsim.FieldsPanel.ComboField;
import com.f1.speedlogger.SpeedLoggerLevels;
import com.f1.utils.BasicLocaleFormatter;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_File;
import com.f1.utils.ids.BatchIdGenerator;
import com.f1.utils.ids.FileBackedIdGenerator;
import com.f1.utils.ids.IdGenerator;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.MultiMap;
import com.f1.utils.structs.Tuple2;

public class FixClientSim extends JFrame implements ActionListener, ListSelectionListener, FixClientSimListener {
	public static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);
	public static final String PROPERTY_IDPATTERN = "id.pattern";
	private static final Logger log = Logger.getLogger(FixClientSim.class.getName());

	public static void main(String args[]) throws IOException {
		Bootstrap bs = new ContainerBootstrap(FixClientSim.class, args);
		bs.setLoggingOverrideProperty("quiet");
		bs.setConfigDirProperty("./src/main/config");
		bs.startup();
		FixClientSim fixFront = new FixClientSim(bs.getProperties());
		fixFront.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fixFront.setVisible(true);
	}

	final private IdGenerator<Long> idGenerator;
	final private JTextArea eventTextArea;
	final private EventsPanel eventsPanel;
	final private EventsPanel eventsChainPanel;
	final private JPanel inputPanel;
	final private JComboBox inputDropdown;
	final private FieldsPanel nwPanel, crPanel, clPanel;
	final private JTextArea newEventTextArea;
	final private JButton sendEventButton;
	final private FixClientSimManager sessionManager;
	final private JList statusPanel;
	private String idPattern;
	final private BasicLocaleFormatter formatter;
	private FieldsPanel currentInputPanel;
	final private JComboBox sessionDropdown;
	final private File baseDir;
	final private ExportDialog exportDialog;
	private ImportDialog importDialog;
	private String nowPattern;
	private JCheckBoxMenuItem showAdminMessagesMenuItem;

	private static final String[] SIDES = new String[] { "1", "buy", "2", "sell", "5", "Sell Short", "", "<none>" };
	private static final String[] ORDTYPE = new String[] { "1", "market", "2", "limit", "3", "stop", "4", "stop limit", "5", "MOC", "7", "Inside Limit", "", "<none>" };
	private static final String[] HANDLEINST = new String[] { "1", "Automated,No Broker", "2", "Automated, broker", "3", "Manual", "", "<none>" };
	private static final String[] IDSOURCE = new String[] { "1", "CUSIP", "2", "SEDOL", "3", "QUIK", "4", "ISIN", "5", "RIC", "8", "Exch. Sym.", "", "<NONE>" };
	private static final String[] TIF = new String[] { "0", "Day", "1", "GTC", "2", "OPG", "3", "IOC", "4", "FOK", "5", "GTX", "6", "GTD", "7", "Close", null, "<NULL>" };

	public FixClientSim(PropertyController properties) throws IOException {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			LH.info(log, "Could not set windows look and feel");
		}
		formatter = new BasicLocaleFormatter(Locale.getDefault(), TimeZone.getDefault(), true, OH.EMPTY_FILE_ARRAY, Collections.EMPTY_MAP);
		this.baseDir = properties.getRequired("output.dir", Caster_File.INSTANCE);
		IOH.ensureDir(this.baseDir);
		exportDialog = new ExportDialog(this, IOH.getFullPath(getBaseDir()));
		importDialog = new ImportDialog(this, IOH.getFullPath(getBaseDir()));
		sessionManager = new FixClientSimManager(new File(baseDir, "store"), SpeedLoggerLevels.INFO, SpeedLoggerLevels.INFO, SpeedLoggerLevels.INFO);
		setTitle("F1 Fix Front");
		idPattern = properties.getOptional(PROPERTY_IDPATTERN, "ID-%d%t-%i");
		nowPattern = "%d-%t.%m";
		IOH.ensureDir(new File(baseDir, "ids"));
		idGenerator = new BatchIdGenerator<Long>(new FileBackedIdGenerator(new File(baseDir, "ids/id.txt")), 100);
		Rectangle screen = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		Container pane = getContentPane();
		pane.setLayout(new BorderLayout());
		JMenuBar menu = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.setMnemonic('x');
		JMenuItem connectMenuItem = new JMenuItem("Connect");
		connectMenuItem.setMnemonic('c');
		JMenuItem importMenuItem = new JMenuItem("Import");
		importMenuItem.setMnemonic('i');
		JMenuItem aboutMenuItem = new JMenuItem("About");
		aboutMenuItem.setMnemonic('a');
		showAdminMessagesMenuItem = new JCheckBoxMenuItem("Show Admin Messages");
		showAdminMessagesMenuItem.setMnemonic('s');
		exitMenuItem.setActionCommand("exit");
		importMenuItem.setActionCommand("import");
		aboutMenuItem.setActionCommand("about");
		connectMenuItem.setActionCommand("connect");
		showAdminMessagesMenuItem.setActionCommand("heartbeats");
		fileMenu.add(exitMenuItem);
		fileMenu.add(importMenuItem);
		fileMenu.add(aboutMenuItem);
		fileMenu.add(connectMenuItem);
		fileMenu.add(showAdminMessagesMenuItem);
		importMenuItem.addActionListener(this);
		connectMenuItem.addActionListener(this);
		exitMenuItem.addActionListener(this);
		aboutMenuItem.addActionListener(this);
		menu.add(fileMenu);
		pane.add(menu, BorderLayout.NORTH);
		final JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		final JSplitPane upperSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		final JSplitPane eventsSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		final JSplitPane lowerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		statusPanel = new JList(new DefaultListModel());
		final JSplitPane statusSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		statusSplit.add(mainSplit);
		statusSplit.add(new JScrollPane(statusPanel));
		statusSplit.setBackground(Color.LIGHT_GRAY);
		pane.add(statusSplit, BorderLayout.CENTER);
		this.eventsPanel = new EventsPanel(this);
		this.eventsChainPanel = new EventsPanel(this);
		this.eventTextArea = new JTextArea();
		eventTextArea.setEditable(false);
		this.newEventTextArea = new JTextArea();
		this.eventsPanel.getTable().getSelectionModel().addListSelectionListener(this);
		this.eventsChainPanel.getTable().getSelectionModel().addListSelectionListener(this);
		mainSplit.add(upperSplit);
		mainSplit.add(lowerSplit);
		upperSplit.add(eventsSplit);
		eventsSplit.add(eventsPanel);
		eventsSplit.add(eventsChainPanel);
		upperSplit.add(new JScrollPane(eventTextArea));
		this.clPanel = new FieldsPanel();
		this.clPanel.addTextField("35", "Message Type", "F");
		this.clPanel.addTextField("55", "Symbol", "ZVZZT");
		this.clPanel.addTextField("65", "Symbol Suffix", "");
		this.clPanel.addTextField("11", "Order ID", idPattern);
		this.clPanel.addComboField("54", "Side", SIDES, "1");
		this.clPanel.addTextField("41", "Orig Order ID", "");
		this.clPanel.addTextField("60", "Transact Time", nowPattern);
		this.clPanel.addTextField("38", "Quantity", "10000");
		this.crPanel = new FieldsPanel();
		this.crPanel.addTextField("35", "Message Type", "G");
		this.crPanel.addTextField("38", "Quantity", "");
		this.crPanel.addComboField("22", "id source", IDSOURCE, "1");
		this.crPanel.addTextField("1", "Account", "3FORGE");
		this.crPanel.addTextField("48", "Security Id", "");
		this.crPanel.addTextField("55", "Symbol", "");
		this.crPanel.addTextField("65", "Symbol Suffix", "");
		this.crPanel.addComboField("59", "Time In Force", TIF, "0");
		this.crPanel.addTextField("60", "Transact Time", nowPattern);
		this.crPanel.addComboField("54", "Side", SIDES, "1");
		this.crPanel.addComboField("40", "Order Type", ORDTYPE, "1");
		this.crPanel.addTextField("44", "Limit Price", "");
		this.crPanel.addTextField("15", "Currency", "USD");
		this.crPanel.addTextField("11", "Order ID", idPattern);
		this.crPanel.addTextField("18", "Exec Instructions", "");
		this.crPanel.addTextField("100", "Destination", "");
		this.crPanel.addTextField("58", "Text", "Some Comment");
		this.crPanel.addComboField("21", "Handling Instructions", HANDLEINST, "1");
		this.crPanel.addTextField("41", "Orig Order ID", "");
		this.crPanel.addTextField("50", "Sender SubID", "");
		this.crPanel.addTextField("115", "OnBehalfOfCompId", "");
		this.nwPanel = new FieldsPanel();
		this.nwPanel.addTextField("35", "Message Type", "D");
		this.nwPanel.addTextField("38", "Quantity", "10000");
		this.nwPanel.addComboField("22", "id source", IDSOURCE, "1");
		this.nwPanel.addTextField("1", "Account", "3FORGE");
		this.nwPanel.addTextField("48", "Security Id", "ZVZZT");
		this.nwPanel.addTextField("55", "Symbol", "ZVZZT");
		this.nwPanel.addTextField("65", "Symbol Suffix", "");
		this.nwPanel.addComboField("59", "Time In Force", TIF, "0");
		this.nwPanel.addTextField("60", "Transact Time", nowPattern);
		this.nwPanel.addComboField("54", "Side", SIDES, "1");
		this.nwPanel.addComboField("40", "Order Type", ORDTYPE, "1");
		this.nwPanel.addTextField("44", "Limit Price", "");
		this.nwPanel.addTextField("15", "Currency", "USD");
		this.nwPanel.addComboField("21", "Handling Instructions", HANDLEINST, "1");
		this.nwPanel.addTextField("11", "Order ID", idPattern);
		this.nwPanel.addTextField("18", "Exec Instructions", "1 A");
		this.nwPanel.addTextField("100", "Destination", "NYSE");
		this.nwPanel.addTextField("58", "Text", "Some Comment");
		this.nwPanel.addTextField("repeat", "Repeat", "1");
		this.nwPanel.addTextField("50", "Sender SubID", "");
		this.nwPanel.addTextField("115", "OnBehalfOfCompId", "");
		long now = EH.currentTimeMillis();
		sendEventButton = new JButton("Send Event");
		sendEventButton.addActionListener(this);
		sendEventButton.setActionCommand("sendEvent");
		JPanel inputAndDropdownPanel = new JPanel(new BorderLayout());
		JPanel eventTypeAndSessionPanel = new JPanel(new FlowLayout());
		sessionDropdown = new JComboBox();
		sessionDropdown.addItem(new FieldsPanel.ComboField("-1", "<No sesions>"));
		sessionDropdown.setEnabled(false);
		inputDropdown = new JComboBox();
		inputDropdown.addItem(new FieldsPanel.ComboField("nw", "New Order"));
		inputDropdown.addItem(new FieldsPanel.ComboField("cl", "Cancel"));
		inputDropdown.addItem(new FieldsPanel.ComboField("cr", "Modify"));
		inputDropdown.addActionListener(this);
		eventTypeAndSessionPanel.add(sessionDropdown);
		eventTypeAndSessionPanel.add(inputDropdown);
		inputPanel = new JPanel(new BorderLayout());
		inputAndDropdownPanel.add(eventTypeAndSessionPanel, BorderLayout.NORTH);
		inputAndDropdownPanel.add(new JScrollPane(inputPanel));
		inputAndDropdownPanel.add(sendEventButton, BorderLayout.SOUTH);
		lowerSplit.add(inputAndDropdownPanel);
		lowerSplit.add(newEventTextArea);
		Rectangle location = SwingHelper.center(new Rectangle(0, 0, 800, 800), screen);
		setBounds(location);
		setVisible(true);
		validate();
		updateInputPanel();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				statusSplit.setDividerLocation(.95d);
				validate();
				mainSplit.setDividerLocation(.5d);
				validate();
				upperSplit.setDividerLocation(.7d);
				validate();
				lowerSplit.setDividerLocation(.5d);
				validate();
				upperSplit.setDividerLocation(.7d);
				validate();
				eventsSplit.setDividerLocation(.5d);
			}
		});
		validate();
		showConnectDialog();
	}

	public File getBaseDir() {
		return baseDir;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == inputDropdown) {
				updateInputPanel();
			} else if ("import".equals(e.getActionCommand())) {
				List<String> sessions = new ArrayList<String>();
				for (FixClientSimSession session : this.sessionManager.getSessions()) {
					sessions.add(Long.toString(session.getUid()));
					sessions.add(session.getSessionId().toString());
				}
				importDialog.setSessions(sessions);
				importDialog.setVisible(true);
				if (importDialog.isOkPressed()) {
					File file = new File(importDialog.getPath());
					char delim = importDialog.getDelim();
					int repeat = importDialog.getRepeat();
					int start = importDialog.getStart();
					importDialog.setStart(start + repeat);
					String prefix = importDialog.getPrefix();
					String[] lines = SH.splitLines(IOH.readText(file));
					for (int i = 0; i < repeat; i++) {
						String prefix2 = SH.replaceAll(prefix, "%i", SH.toString(i + start));
						for (String line : lines) {
							if (SH.isnt(line))
								continue;
							String[] parts = SH.split(delim, line);
							Map<Integer, String> map = new LinkedHashMap<Integer, String>();
							for (String part : parts) {
								try {
									if (SH.is(part))
										map.put(Integer.parseInt(SH.beforeFirst(part, '=')), SH.afterFirst(part, '='));
								} catch (Exception e2) {
									throw new RuntimeException("error parsing part:" + part, e2);
								}
							}
							String id = map.get(11);
							String origId = map.get(41);
							if (id != null) {
								map.put(11, prefix2 + id);
							}
							if (origId != null) {
								map.put(41, prefix2 + origId);
							}
							map.remove(8);
							map.remove(9);
							map.remove(34);
							map.remove(49);
							map.remove(10);
							map.remove(52);
							map.remove(56);
							sendMessage(this.getCurrentSession(), map);
						}
					}
				}
			} else if ("exit".equals(e.getActionCommand()))
				System.exit(1);
			else if ("about".equals(e.getActionCommand())) {
				JDialog dialog = new JDialog(this);
				dialog.setModal(true);
				dialog.setTitle("About");
				dialog.doLayout();
				dialog.add(new JLabel("Forge Financial Framework LLC."), BorderLayout.CENTER);
				dialog.setBounds(SwingHelper.center(new Rectangle(0, 0, 300, 150), getBounds()));
				dialog.setVisible(true);
			} else if ("connect".equals(e.getActionCommand())) {
				showConnectDialog();
			} else if ("sendEvent".equals(e.getActionCommand())) {
				FixClientSimSession session = getCurrentSession();
				if (session == null)
					throw new RuntimeException("not connected.  Use file -> connect");
				int count = 1;
				if (currentInputPanel == nwPanel)
					count = SH.parseInt(currentInputPanel.getValue("repeat"));
				for (int i = 0; i < count; i++) {
					long now = EH.currentTimeMillis();
					ValueFormatter valueFormatter = new ValueFormatter(now, idGenerator.createNextId());
					sendEvent(session, valueFormatter);
				}
				addStatus("Sent " + count + " event(s) to " + session.getSessionId());
			} else
				throw new RuntimeException("unknown command: " + e.getActionCommand());
		} catch (Exception ex) {
			SwingHelper.showDialog(ex);
		}
	}

	private ConnectDialog connectDialog;

	private void showConnectDialog() {
		if (connectDialog == null)
			connectDialog = new ConnectDialog(this, sessionManager);
		connectDialog.setVisible(true);
		FixClientSimSession s = connectDialog.getSession();
		if (s == null)
			return;
		s.addListener(new SwingSafeFixClientSimListener(this));
		addStatus("Connected to " + s.getSessionId());
		if (!sessionDropdown.isEnabled()) {
			sessionDropdown.removeAllItems();
			sessionDropdown.setEnabled(true);
		}
		sessionDropdown.addItem(new FieldsPanel.ComboField(Long.toString(s.getUid()), s.getSessionId().toString()));
	}

	private FixClientSimSession getCurrentSession() {
		FieldsPanel.ComboField field = (ComboField) sessionDropdown.getSelectedItem();
		if (field == null)
			return null;
		return sessionManager.getSessionByUid(Long.parseLong(field.getKey()));
	}

	public void addStatus(String message) {
		((DefaultListModel) this.statusPanel.getModel()).add(0, new Date().toString() + ": " + message);
	}

	public void updateInputPanel() {
		String key = ((FieldsPanel.ComboField) inputDropdown.getSelectedItem()).getKey();
		if (currentInputPanel != null)
			inputPanel.remove(currentInputPanel);
		currentInputPanel = null;
		if (key.equals("nw")) {
			currentInputPanel = nwPanel;
		} else if (key.equals("cl")) {
			currentInputPanel = clPanel;
		} else if (key.equals("cr")) {
			currentInputPanel = crPanel;
		}
		if (currentInputPanel != null) {
			inputPanel.add(currentInputPanel);
			inputPanel.doLayout();
			currentInputPanel.doLayout();
			currentInputPanel.repaint();
		}
		inputPanel.doLayout();
		inputPanel.repaint();
	}

	private void sendEvent(FixClientSimSession session, ValueFormatter valueFormatter) {
		if (currentInputPanel == null)
			throw new RuntimeException("no input panel");
		final List<Tuple2<String, String>> values = new ArrayList<Tuple2<String, String>>();
		for (String key : this.currentInputPanel.getFields().keySet()) {
			String value = this.currentInputPanel.getValue(key);
			if (SH.is(value)) {
				value = valueFormatter.format(value);
				value = value.trim();
				values.add(new Tuple2<String, String>(key, value.trim()));
			}
		}
		for (String part : SH.split(SH.CHAR_NEWLINE, this.newEventTextArea.getText())) {
			if (SH.is(part) && part.indexOf('=') != -1)
				values.add(new Tuple2<String, String>(SH.beforeFirst(part, '=').trim(), SH.afterFirst(part, '=').trim()));
		}
		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		for (Tuple2<String, String> e : values) {
			if ("repeat".equals(e.getA()))
				continue;
			map.put(Integer.parseInt(e.getA()), e.getB());
		}
		sendMessage(session, map);
	}

	public void sendMessage(FixClientSimSession session, Map<Integer, String> map) {
		String id = map.get(11);
		String origId = map.get(41);
		String msgType = map.remove(35);
		String senderSubId = map.remove(50);
		Map<Integer, String> header = CH.m(quickfix.field.MsgType.FIELD, msgType);
		if (SH.is(senderSubId))
			header.put(SenderSubID.FIELD, senderSubId);
		Message message = this.getCurrentSession().sendMessage(header, map);
		StringBuilder sb = new StringBuilder();
		append(sb, message.getHeader().iterator());
		append(sb, message.iterator());
		append(sb, message.getTrailer().iterator());
		this.eventsPanel.addEvent(false, session.getSessionId().toString(), id, origId, sb.toString());
	}

	public void prepareNewOrder(List<Tuple2<String, String>> tuples) {
		inputDropdown.setSelectedIndex(0);
		updateInputPanel();
		Set<String> keys = currentInputPanel.getFields().keySet();
		for (Tuple2<String, String> tuple : tuples)
			if (keys.contains(tuple.getA()))
				currentInputPanel.setValue(tuple.getA(), tuple.getB());
	}

	public void prepareCancel(List<Tuple2<String, String>> tuples) {
		inputDropdown.setSelectedIndex(1);
		updateInputPanel();
		Set<String> keys = currentInputPanel.getFields().keySet();
		for (Tuple2<String, String> tuple : tuples) {
			String key = tuple.getA();
			if ("35".equals(key))
				continue;
			if ("11".equals(tuple.getA()))
				key = "41";
			if (keys.contains(key))
				currentInputPanel.setValue(key, tuple.getB());
		}
	}

	public void prepareModify(List<Tuple2<String, String>> tuples) {
		inputDropdown.setSelectedIndex(2);
		updateInputPanel();
		Set<String> keys = currentInputPanel.getFields().keySet();
		for (Tuple2<String, String> tuple : tuples) {
			String key = tuple.getA();
			if ("35".equals(key) || "41".equals(key))
				continue;
			if ("11".equals(tuple.getA()))
				key = "41";
			if (keys.contains(key))
				currentInputPanel.setValue(key, tuple.getB());
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == this.eventsPanel.getTable().getSelectionModel()) {
			int i = this.eventsPanel.getTable().getSelectedRow();
			String details = "";
			if (i != -1) {
				details = this.eventsPanel.getValue(i, "Details").toString();
			}
			this.eventTextArea.setText(SH.replaceAll(details, SH.CHAR_NUL, SH.NEWLINE));
			MultiMap<String, String, Set<String>> s = new BasicMultiMap.Set<String, String>();
			for (int j : this.eventsPanel.getTable().getSelectedRows()) {
				String sessionId = (String) this.eventsPanel.getValue(j, "SessionId");
				String id = (String) this.eventsPanel.getValue(j, "Id");
				s.putMulti(sessionId, id);
			}
			final List<OrderEntry> l = new ArrayList<OrderEntry>();
			for (Map.Entry<String, Set<String>> e2 : s.entrySet())
				l.addAll(this.eventsPanel.getChain(e2.getKey(), e2.getValue()));
			this.eventsChainPanel.clear();
			for (OrderEntry oe : l)
				this.eventsChainPanel.addEvent(oe);
		} else {
			int i = this.eventsChainPanel.getTable().getSelectedRow();
			String details = "";
			if (i != -1) {
				details = this.eventsChainPanel.getValue(i, "Details").toString();
			}
			this.eventTextArea.setText(SH.replaceAll(details, SH.CHAR_NUL, SH.NEWLINE));
		}
	}

	public static void append(StringBuilder sb, Iterator<Field<?>> fields) {
		while (fields.hasNext()) {
			Field f = fields.next();
			sb.append(f.getTag()).append("=").append(f.getObject()).append(SH.CHAR_NUL);
		}
	}

	public ExportDialog getExportDialog() {
		return exportDialog;
	}

	public class ValueFormatter {

		final private String id;
		final private String date;
		final private String time;
		final private String millis;

		public ValueFormatter(long now, long id) {
			this.id = SH.rightAlign('0', Long.toString(id), 6, false);
			this.date = formatter.getDateFormatter(LocaleFormatter.DATE).format(now);
			this.time = formatter.getDateFormatter(LocaleFormatter.TIME).format(now);
			this.millis = SH.rightAlign('0', Long.toString(now % 1000), 3, true);
		}

		public String format(String s) {
			int i = s.indexOf('%');
			if (-1 == i)
				return s;
			// TODO:make more efficient using a single pass!
			s = SH.replaceAll(s, "%i", id);
			s = SH.replaceAll(s, "%d", date);
			s = SH.replaceAll(s, "%t", time);
			s = SH.replaceAll(s, "%m", millis);
			return s;
		}

	}

	@Override
	public void onAdminMessage(FixClientSimSession fixClientSimSession, Message message) {
		if (showAdminMessagesMenuItem.isSelected())
			onMessage(fixClientSimSession, message);
	}

	@Override
	public void onLogon(FixClientSimSession session) {
	}

	@Override
	public void onLogout(FixClientSimSession session) {
		String key = Long.toString(session.getUid());
		for (int i = 0; i < sessionDropdown.getItemCount(); i++) {
			FieldsPanel.ComboField cf = (ComboField) sessionDropdown.getItemAt(i);
			if (cf.getKey().equals(key)) {
				sessionDropdown.removeItemAt(i);
				break;
			}
		}
		if (sessionDropdown.getItemCount() == 0) {
			sessionDropdown.addItem(new FieldsPanel.ComboField("-1", "<No sessions>"));
			sessionDropdown.setEnabled(false);
		}
		addStatus("Disconnected from: " + session.getSessionId());
		try {
			this.sessionManager.closeSession(session);
		} catch (Exception e) {
			LH.warning(log, "error closing session", e);
		}
	}

	@Override
	public void onMessage(FixClientSimSession session, Message message) {
		try {
			StringBuilder sb = new StringBuilder();
			append(sb, message.getHeader().iterator());
			append(sb, message.iterator());
			append(sb, message.getTrailer().iterator());
			String id = message.isSetField(11) ? message.getString(11) : "n/a";
			String origId = message.isSetField(41) ? message.getString(41) : null;
			this.eventsPanel.addEvent(true, session.getSessionId().toString(), id, origId, sb.toString());
		} catch (FieldNotFound e) {
			LH.warning(log, "error on incoming message", e);
		}

	}

	@Override
	public void onText(String text) {
		addStatus(text);
	}

}

