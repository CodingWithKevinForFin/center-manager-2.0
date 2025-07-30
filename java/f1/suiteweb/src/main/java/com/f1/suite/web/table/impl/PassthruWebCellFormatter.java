/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.table.impl;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import com.f1.suite.web.table.WebCellFormatter;
import com.f1.suite.web.table.WebColumn;
import com.f1.utils.SH;
import com.f1.utils.structs.ComparableComparator;

public class PassthruWebCellFormatter extends BasicWebCellFormatter implements WebCellFormatter {

	protected WebColumn column;
	protected int columnLoc;

	@Override
	public StringBuilder formatCellToText(Object o, StringBuilder sb) {
		if (!(o instanceof Map))
			return sb;
		Map<Integer, String> passThruTags = (Map<Integer, String>) o;
		if (passThruTags == null)
			return sb;
		return SH.joinMap(',', '=', new TreeMap<Integer, String>(passThruTags), sb);
	}

	@Override
	public Comparable getOrdinalValue(Object row) {
		return null;
	}

	@Override
	public boolean isString() {
		return false;
	}

	@Override
	public Comparator getComparator() {
		return ComparableComparator.INSTANCE;
	}

}
