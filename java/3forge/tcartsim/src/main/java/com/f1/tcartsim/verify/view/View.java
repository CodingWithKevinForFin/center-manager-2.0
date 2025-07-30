/**
 * 
 */
package com.f1.tcartsim.verify.view;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.f1.base.Column;
import com.f1.utils.IOH;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.structs.table.BasicTable;

/**
 * @author george
 * 
 */
public class View {
	private BasicTable view;
	private BasicTable reset;
	private HashMap<Object, View> groups;

	public View() {
		view = new BasicTable();
	}
	public View(View view) {
		this.view = new BasicTable(view.getTable().getColumns());
		view.setTitle(view.getTable().getTitle());
	}
	public BasicTable getTable() {
		return view;
	}

	public void print() {
		System.out.println(view);
	}

	public void sortDesc(String... columnIDs) {
		TableHelper.sortDesc(view, columnIDs);
	}
	public void sort(String... columnIDs) {
		TableHelper.sort(view, columnIDs);
	}

	public void setTitle(String title) {
		view.setTitle(title);
	}

	public final <T> Column addColumn(Class<T> clazz, String id, T defaultValue) {
		return view.addColumn(clazz, id, defaultValue);
	}

	public final <T> Column addColumn(Class<T> clazz, String id) {
		return view.addColumn(clazz, id);
	}

	public void addRow(Object... objects) {
		view.getRows().addRow(objects);
	}

	public void addRowsWithCast(Object[] objects) {
		TableHelper.addRowWithCast(view, objects);
	}

	public void clear() {
		view.clear();
	}

	public void filter(String columnid, String filter) {
		if (reset == null)
			reset = view;

		BasicTable previous = view;
		view = new BasicTable(previous.getColumns());
		for (int i = 0; i < previous.getSize(); i++) {
			if (SH.equals((String) previous.get(i, columnid), filter)) {
				view.getRows().add(view.newRow(previous.getRows().get(i).getValues()));
			}
		}
	}
	public boolean isFiltered() {
		return reset != null;
	}
	public void resetFilter() {
		view = reset;
		reset = null;
	}
	public Map<Object, View> getGroups() {
		return this.groups;
	}
	public void group(String columnid) {
		if (groups != null) {
			for (View v : groups.values()) {
				v.group(columnid);
			}
		} else {
			int column = view.getColumn(columnid).getLocation();
			groups = new HashMap<Object, View>();
			int nrows = view.getRows().size();
			for (int i = 0; i < nrows; i++) {
				Object key = view.getAt(i, column);
				View group = null;
				if (!groups.containsKey(key)) {
					group = new View(this);
					groups.put(key, group);
				} else {
					group = groups.get(key);
				}
				group.addRow(view.getRows().get(i).getValues());
			}
		}
	}
	public boolean isGrouped() {
		return groups != null;
	}
	public void resetGroups() {
		if (isGrouped()) {
			for (View v : groups.values()) {
				v.resetGroups();
			}
			groups = null;
		}
	}
	public void writeToFile(File f) {
		try {
			IOH.writeText(f, view.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void appendToFile(File f) {
		try {
			IOH.appendText(f, view.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
