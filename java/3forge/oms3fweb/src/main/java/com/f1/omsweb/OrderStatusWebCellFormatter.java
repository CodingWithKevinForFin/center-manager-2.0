/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.omsweb;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.f1.fixomsclient.OmsClientUtils;
import com.f1.pofo.fix.OrdStatus;
import com.f1.suite.web.table.WebCellEnumFormatter;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.utils.Formatter;
import com.f1.utils.FormatterHelper;
import com.f1.utils.structs.ComparableComparator;

public class OrderStatusWebCellFormatter extends BasicWebCellFormatter implements WebCellEnumFormatter {

	protected WebColumn column;
	protected int columnLoc;
	private Map<OrdStatus, String> enums = new HashMap<OrdStatus, String>();

	public OrderStatusWebCellFormatter(Formatter formatter) {
		enums = FormatterHelper.formatEnum(formatter, OrdStatus.class);
		setDefaultWidth(50);
	}

	@Override
	public StringBuilder formatCellToText(Object row, StringBuilder sb) {
		OrdStatus ordStatus = getStatus(row);
		String text;
		if (ordStatus == null)
			text = "";
		else
			switch (ordStatus) {
				case FILLED:
					text = "Filled";
					break;
				case REJECTED:
					text = "Rejected";
					break;
				case CANCELLED:
					text = "Cancelled";
					break;
				case REPLACED:
					text = "Replaced";
					break;
				case PARTIAL:
					text = "Partial";
					break;
				case ACKED:
					text = "Acked";
					break;
				case PENDING_ACK:
					text = "Pending Ack";
					break;
				case PENDING_CXL:
					text = "Pending Cxl";
					break;
				case PENDING_RPL:
					text = "Pending Replace";
					break;
				default:
					text = ordStatus.name();
			}
		return sb.append(text);
	}
	private OrdStatus getStatus(Object row) {
		int status = (Integer) row;
		return OmsClientUtils.getMostImportantOrdStatus(status, null);
	}

	@Override
	public void formatCellToHtml(Object row, StringBuilder sb, StringBuilder cellStyle) {
		OrdStatus ordStatus = getStatus(row);
		formatCellToText(row, sb);
		if (ordStatus != null) {
			switch (ordStatus) {
				case FILLED:
					cellStyle.append("style.color=blue");
					break;
				case REJECTED:
				case CANCELLED:
					cellStyle.append("style.color=red");
					break;
				case REPLACED:
				case PARTIAL:
					cellStyle.append("style.color=green");
					break;
				case ACKED:
				case PENDING_ACK:
				case PENDING_CXL:
				case PENDING_RPL:
					cellStyle.append("style.color=purple");
					break;
			}
		}
	}

	@Override
	public StringBuilder formatCellToExcel(Object row, StringBuilder sb) {
		return formatCellToText(row, sb);
	}

	@Override
	public boolean isString() {
		return false;
	}

	@Override
	public String formatCellToHtml(Object row) {
		StringBuilder sb = new StringBuilder();
		formatCellToText(row, sb);
		return sb.toString();
	}

	@Override
	public Comparator getComparator() {
		return ComparableComparator.INSTANCE;
	}

	@Override
	public Map<Object, String> getEnumValuesAsText() {
		return Collections.EMPTY_MAP;
	}

}
