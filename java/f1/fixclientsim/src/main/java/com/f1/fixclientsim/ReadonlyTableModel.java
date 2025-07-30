package com.f1.fixclientsim;

import javax.swing.table.DefaultTableModel;

public class ReadonlyTableModel extends DefaultTableModel {

	public ReadonlyTableModel(Object[] objects, int i) {
		super(objects, i);
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}

}
