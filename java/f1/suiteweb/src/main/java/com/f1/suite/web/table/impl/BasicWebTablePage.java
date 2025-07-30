/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.table.impl;

import com.f1.suite.web.table.WebTablePage;

public class BasicWebTablePage implements WebTablePage {

	final public int id;
	final public int minRow;
	final public int maxRow;
	final public int pageNumber;
	final public String description;

	public BasicWebTablePage(int id, int minRow, int maxRow, int pageNumber, String description) {
		this.id = id;
		this.minRow = minRow;
		this.maxRow = maxRow;
		this.pageNumber = pageNumber;
		this.description = description;
	}

	public int getId() {
		return id;
	}
	public int getMinRow() {
		return minRow;
	}
	public int getMaxRow() {
		return maxRow;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public String getDescription() {
		return description;
	}

	@Override
	public String getText() {
		return "Pg " + pageNumber + ") " + minRow + " - " + maxRow;
	}

}
