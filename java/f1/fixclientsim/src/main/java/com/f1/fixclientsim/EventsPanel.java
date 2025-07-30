package com.f1.fixclientsim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.MultiMap;
import com.f1.utils.structs.Tuple2;

public class EventsPanel extends JPanel implements Comparator, MouseListener, TableModelListener, ActionListener {

	final private JTable table = new JTable();
	private ReadonlyTableModel tableModel;
	private Object sortCol = "Time";
	private boolean isSortAsc;
	private int sortIndex = -1;
	private JPopupMenu menu;
	private FixClientSim fixFront;
	private Map<String, MultiMap<String, OrderEntry, List<OrderEntry>>> entries = new HashMap<String, MultiMap<String, OrderEntry, List<OrderEntry>>>();
	private long uid = 0;

	public EventsPanel(FixClientSim fixFront) {
		this.fixFront = fixFront;
		setLayout(new BorderLayout());
		add(new JScrollPane(getTable()), BorderLayout.CENTER);
		this.tableModel = new ReadonlyTableModel(new Object[] { "Uid", "Dir", "Time", "SessionId", "Id", "Details" }, 0);
		tableModel.addTableModelListener(this);
		this.table.setModel(this.tableModel);
		this.table.setRowSelectionAllowed(true);
		this.table.setDefaultRenderer(Object.class, new Renderer());
		this.table.getTableHeader().addMouseListener(this);
		Enumeration<TableColumn> columns = this.table.getColumnModel().getColumns();
		while (columns.hasMoreElements()) {
			TableColumn column = columns.nextElement();
			column.setIdentifier(column.getHeaderValue());
		}
		menu = new JPopupMenu();
		menu.add(newJMenuItem("clear", "Clear"));
		menu.add(newJMenuItem("export", "Export to File"));
		menu.add(newJMenuItem("nw", "Prepare New Order "));
		menu.add(newJMenuItem("cl", "Prepare Cancel"));
		menu.add(newJMenuItem("cr", "Prepare Modify"));

		this.table.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == 3) {
					int r = table.rowAtPoint(e.getPoint());
					if (r != -1 && !table.getSelectionModel().isSelectedIndex(r))
						table.getSelectionModel().setSelectionInterval(r, r);
					menu.show(e.getComponent(), e.getX(), e.getY());
					menu.requestFocus();
				}
			}
		});
	}

	private JMenuItem newJMenuItem(String command, String text) {
		JMenuItem r = new JMenuItem(text);
		r.setActionCommand(command);
		r.addActionListener(this);
		return r;
	}

	public void addEvent(boolean incoming, String sessionId, String id, String origId, String details) {
		OrderEntry orderEntry = new OrderEntry(++uid, incoming, sessionId, id, origId, details, new Date());
		addEvent(orderEntry);
	}

	synchronized public void addEvent(OrderEntry orderEntry) {
		if (tableModel.getRowCount() > 1000)
			return;
		MultiMap<String, OrderEntry, List<OrderEntry>> bySession = entries.get(orderEntry.getSessionId());
		if (bySession == null)
			entries.put(orderEntry.getSessionId(), bySession = new BasicMultiMap.List<String, OrderEntry>());
		bySession.putMulti(orderEntry.getId(), orderEntry);
		if (orderEntry.getOrigId() != null)
			bySession.putMulti(orderEntry.getOrigId(), orderEntry);
		tableModel.addRow(new Object[] { orderEntry.getUid(), orderEntry.isIncoming() ? "<<" : ">>", orderEntry.getWhen(), orderEntry.getSessionId(), orderEntry.getId(),
				orderEntry.getDetails() });
		sort();
	}

	private void sort() {
		HashSet<Long> selected = new HashSet<Long>();
		for (int i : table.getSelectedRows()) {
			selected.add((Long) getValue(i, "Uid"));
		}
		Collections.sort(tableModel.getDataVector(), this);
		table.tableChanged(new TableModelEvent(tableModel));
		table.repaint();
		for (int i = 0; i < table.getRowCount(); i++) {
			if (selected.contains((Long) getValue(i, "Uid")))
				table.getSelectionModel().addSelectionInterval(i, i);
		}

	}

	public JTable getTable() {
		return table;
	}

	public TableModel getTableModel() {
		return tableModel;
	}

	public class Renderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			int loc = table.getColumnModel().getColumnIndex("Dir");
			Component r = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if ("<<".equals(table.getValueAt(row, loc)))
				r.setForeground(Color.BLUE);
			else
				r.setForeground(Color.BLACK);
			return r;
		}
	}

	@Override
	public int compare(Object o1, Object o2) {
		Vector<Comparable> v1 = (Vector) o1;
		Vector<Comparable> v2 = (Vector) o2;
		if (sortIndex == -1) {
			int loc = table.getColumnModel().getColumnIndex(sortCol);
			sortIndex = table.getColumnModel().getColumn(loc).getModelIndex();
		}
		int r = OH.compare(v1.get(sortIndex), v2.get(sortIndex));
		return isSortAsc ? r : -r;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		TableColumnModel colModel = table.getColumnModel();
		int columnModelIndex = colModel.getColumnIndexAtX(e.getX());
		Object newSortCol = colModel.getColumn(columnModelIndex).getIdentifier();

		if (columnModelIndex < 0)
			return;
		if (OH.eq(newSortCol, sortCol))
			isSortAsc = !isSortAsc;
		else {
			sortCol = newSortCol;
			isSortAsc = false;
		}

		for (int i = 0; i < colModel.getColumnCount(); i++) {
			TableColumn column = colModel.getColumn(i);
			Object identifier = column.getIdentifier();
			if (identifier.equals(sortCol))
				column.setHeaderValue(identifier.toString() + (isSortAsc ? " /\\" : " \\/"));
			else
				column.setHeaderValue(identifier.toString());
		}
		table.getTableHeader().repaint();

		sortIndex = -1;
		sort();

	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		sortIndex = -1;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (table.getSelectedRows().length == 0)
			return;
		String details = (String) getValue(table.getSelectedRow(), "Details");
		List<Tuple2<String, String>> tuples = new ArrayList<Tuple2<String, String>>();
		for (String part : SH.split(SH.CHAR_NUL, details)) {
			tuples.add(new Tuple2<String, String>(SH.beforeFirst(part, '='), SH.afterFirst(part, '=')));
		}
		if (cmd.equals("export")) {
			ExportDialog exportDialog = fixFront.getExportDialog();
			exportDialog.setVisible(true);
			if (exportDialog.isOkPressed()) {
				try {
					final String dir = exportDialog.getDirection();
					final List<Integer> rows = new ArrayList<Integer>();
					for (int row : table.getSelectedRows()) {
						if (dir.equals("both") || //
								(dir.equals("out") && ">>".equals(getValue(row, "Dir"))) || //
								(dir.equals("in") && "<<".equals(getValue(row, "Dir"))))
							rows.add(row);
					}
					export(rows, exportDialog.getPath(), exportDialog.getDelim());
				} catch (IOException e1) {
					SwingHelper.showDialog(e1);
				}
			}
		} else if (cmd.equals("clear")) {
			clearSelected();
		} else if (cmd.equals("cl")) {
			fixFront.prepareCancel(tuples);
		} else if (cmd.equals("nw")) {
			fixFront.prepareNewOrder(tuples);
		} else if (cmd.equals("cr")) {
			fixFront.prepareModify(tuples);
		}
	}

	private void clearSelected() {
		int[] rows = this.table.getSelectedRows();
		for (int i = rows.length - 1; i >= 0; i--) {
			int rowIndex = rows[i];
			long uid = (Long) getValue(rowIndex, "Uid");
			String oid = (String) getValue(rowIndex, "Id");
			String sessionId = (String) getValue(rowIndex, "SessionId");
			MultiMap<String, OrderEntry, List<OrderEntry>> bySession = CH.getOrThrow(entries, sessionId);
			{
				List<OrderEntry> entries = CH.getOrThrow(bySession, oid);
				boolean found = false;
				for (int j = 0; j < entries.size(); j++)
					if (uid == entries.get(j).getUid()) {
						OrderEntry e = entries.remove(j);
						if (e.getOrigId() != null) {
							List<OrderEntry> entries2 = CH.getOrThrow(bySession, oid);
							for (int k = 0; k < entries2.size(); k++)
								if (entries2.get(k).getUid() == uid) {
									entries2.remove(k);
									break;
								}
						}
						found = true;
						break;
					}
				if (!found)
					throw new RuntimeException("not found: " + uid);
			}
			this.tableModel.removeRow(rowIndex);
		}
	}

	public Object getValue(int i, String columnId) {
		TableColumnModel cm = table.getColumnModel();
		int loc = cm.getColumnIndex(columnId);
		loc = cm.getColumn(loc).getModelIndex();
		return tableModel.getValueAt(i, loc);
	}

	public static class OrderEntry {

		final private boolean incoming;
		final private String sessionId;
		final private String id;
		final private String origId;
		final private long uid;
		final private String details;
		final private Date when;

		public OrderEntry(long uid, boolean incoming, String sessionId, String id, String origId, String details, Date when) {
			this.uid = uid;
			this.incoming = incoming;
			this.sessionId = sessionId;
			this.id = id;
			this.origId = origId;
			this.details = details;
			this.when = when;
		}

		public long getUid() {
			return uid;
		}

		public String getOrigId() {
			return origId;
		}

		public boolean isIncoming() {
			return incoming;
		}

		public String getSessionId() {
			return sessionId;
		}

		public String getId() {
			return id;
		}

		public String getDetails() {
			return details;
		}

		public Date getWhen() {
			return when;
		}

	}

	public List<OrderEntry> getChain(String sessionId, Collection<String> oids) {
		final MultiMap<String, OrderEntry, List<OrderEntry>> bySession = CH.getOrThrow(entries, sessionId);
		final Set<String> toExploreIds = new HashSet<String>(oids);
		final Set<String> exploredIds = new HashSet<String>();
		final Set<Long> added = new HashSet<Long>();
		final List<OrderEntry> r = new ArrayList<OrderEntry>();
		while (!toExploreIds.isEmpty()) {
			final HashSet<String> t = new HashSet<String>(toExploreIds);
			toExploreIds.clear();
			for (String i : t) {
				final List<OrderEntry> list = bySession.get(i);
				exploredIds.add(i);
				if (list == null)
					continue;
				for (OrderEntry e : list)
					if (!added.contains(e.getUid())) {
						added.add(e.getUid());
						r.add(e);
						final String origId = e.getOrigId();
						if (origId != null && !exploredIds.contains(origId))
							toExploreIds.add(origId);
						final String id = e.getId();
						if (id != null && !exploredIds.contains(id))
							toExploreIds.add(id);
					}
			}
		}
		return r;
	}

	public void clear() {
		for (int row = this.tableModel.getRowCount() - 1; row >= 0; row--)
			this.tableModel.removeRow(row);
		this.entries.clear();
	}

	private void export(List<Integer> rows, String path, char delim) throws IOException {
		File file = new File(path);
		FileWriter out = new FileWriter(file);
		for (int row : rows) {
			String details = getValue(row, "Details").toString();
			out.write(SH.replaceAll(details, SH.CHAR_NUL, delim));
			out.write(SH.NEWLINE);
		}
		IOH.close(out);
		fixFront.addStatus("Exported " + rows.size() + " events (" + file.length() + " bytes) to " + IOH.getFullPath(file));
	}

}
