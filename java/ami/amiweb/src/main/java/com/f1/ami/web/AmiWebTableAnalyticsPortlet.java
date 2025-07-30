package com.f1.ami.web;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.f1.base.Row;
import com.f1.base.TableListenable;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.utils.SH;
import com.f1.utils.structs.table.BasicTable;

public class AmiWebTableAnalyticsPortlet extends GridPortlet implements AmiWebLockedPermissiblePortlet {

	private FastTablePortlet fastTable;

	public AmiWebTableAnalyticsPortlet(PortletConfig config, FastTablePortlet ftp, boolean rollupAll) {
		super(config);
		FastWebTable tablePortlet = ftp.getTable();
		TableListenable basic = new BasicTable(new String[] {});

		// Add initial column which will contain the 
		// operation performed for each row 
		basic.addColumn(Object.class, "Operation");

		// For each column in input, add a column to basic. 
		// In other words, copy each column from the input 
		// web table to a new basic table. 
		WebColumn webCol;
		for (int i = 0; i < tablePortlet.getVisibleColumnsCount(); i++) {
			webCol = tablePortlet.getVisibleColumn(i);
			basic.addColumn(Object.class, webCol.getColumnName());
		}

		// Create a new AmiWebAnalyzeColumn object for each 
		// column in input. 
		List<AmiWebAnalyzeColumn> colObjectList = new ArrayList<AmiWebAnalyzeColumn>();
		for (int k = 0; k < tablePortlet.getVisibleColumnsCount(); k++) {
			colObjectList.add(new AmiWebAnalyzeColumn());
		}

		WebColumn visibleColumn;
		AmiWebAnalyzeColumn analyzeCol;
		Comparable value;
		if (rollupAll) { // Analyze all rows
			for (int k = 0; k < tablePortlet.getVisibleColumnsCount(); k++) { // Iterate through columns of input
				analyzeCol = colObjectList.get(k);
				for (int j = 0; j < tablePortlet.getRowsCount(); j++) { // Iterate through all rows of input
					visibleColumn = tablePortlet.getVisibleColumn(k);
					value = visibleColumn.getCellFormatter().getOrdinalValue(visibleColumn.getData(tablePortlet.getRow(j)));
					analyzeCol.addValue(value);
				}
			}
		} else { // Analyze only selected rows
			for (int k = 0; k < tablePortlet.getVisibleColumnsCount(); k++) { // Iterate through columns of input
				analyzeCol = colObjectList.get(k);
				for (int j = 0; j < tablePortlet.getSelectedRows().size(); j++) { // Iterate through selected rows of input
					visibleColumn = tablePortlet.getVisibleColumn(k);
					value = visibleColumn.getCellFormatter().getOrdinalValue(visibleColumn.getData(tablePortlet.getSelectedRows().get(j)));
					analyzeCol.addValue(value);
				}
			}
		}

		// Add rows for calculations... 

		DecimalFormat df = new DecimalFormat("#.####");
		df.setRoundingMode(RoundingMode.HALF_UP);
		Row analyzeRow;
		String operations[] = { "Sum", "Minimum", "Maximum", "Count", "Average", "Std Dev", "Median", "Distinct" };
		String entry = null;

		for (int j = 0; j < 8; j++) { // Iterate through operations
			analyzeRow = basic.newEmptyRow();
			analyzeRow.putAt(0, operations[j]);
			for (int k = 0; k < tablePortlet.getVisibleColumnsCount(); k++) { // Iterate through columns 
				analyzeCol = colObjectList.get(k);
				switch (j) {
					case 0:
						entry = formatDouble(analyzeCol.getSum(), df);
						break;
					case 1:
						entry = formatComparable(analyzeCol.getMin(), df);
						break;
					case 2:
						entry = formatComparable(analyzeCol.getMax(), df);
						break;
					case 3:
						entry = SH.toString(analyzeCol.getCount());
						break;
					case 4:
						entry = formatDouble(analyzeCol.getAverage(), df);
						break;
					case 5:
						entry = formatDouble(analyzeCol.getStdDev(), df);
						break;
					case 6:
						entry = formatDouble(analyzeCol.getMedian(), df);
						break;
					case 7:
						entry = SH.toString(analyzeCol.getDistinct());
						break;
				}
				analyzeRow.putAt(k + 1, entry);
			}
			basic.getRows().addRow(analyzeRow.getValuesCloned());
		}

		this.fastTable = new FastTablePortlet(generateConfig(), basic, "Rollup");
		this.fastTable.addOption(FastTablePortlet.OPTION_MENU_BAR_HIDDEN, true); // Hide menu bar
		this.fastTable.addOption(FastTablePortlet.OPTION_TITLE_DIVIDER_HIDDEN, ftp.getOption(FastTablePortlet.OPTION_TITLE_DIVIDER_HIDDEN));

		// Specify options to be copied from original table 
		String options[] = { FastTablePortlet.OPTION_STYLE, FastTablePortlet.OPTION_BACKGROUND_STYLE, FastTablePortlet.OPTION_GREY_BAR_COLOR,
				FastTablePortlet.OPTION_DEFAULT_FONT_COLOR, FastTablePortlet.OPTION_CELL_BORDER_COLOR, FastTablePortlet.OPTION_HEADER_ROW_HEIGHT,
				FastTablePortlet.OPTION_HEADER_FONT_SIZE, FastTablePortlet.OPTION_TITLE_DIVIDER_HIDDEN, FastTablePortlet.OPTION_SELECTED_BG,
				FastTablePortlet.OPTION_SELECTED_CSS_CLASS, FastTablePortlet.OPTION_ACTIVE_BG, FastTablePortlet.OPTION_ACTIVE_CSS_CLASS, FastTablePortlet.OPTION_MENU_BAR_HIDDEN,
				FastTablePortlet.OPTION_MENU_BAR_COLOR, FastTablePortlet.OPTION_MENU_FONT_COLOR, FastTablePortlet.OPTION_SEARCH_BAR_COLOR,
				FastTablePortlet.OPTION_SEARCH_BAR_FONT_COLOR, FastTablePortlet.OPTION_FILTERED_COLUMN_BG_COLOR, FastTablePortlet.OPTION_FILTERED_COLUMN_FONT_COLOR,
				FastTablePortlet.OPTION_SEARCH_BAR_COLOR, FastTablePortlet.OPTION_TRACK_COLOR, FastTablePortlet.OPTION_TRACK_BUTTON_COLOR, FastTablePortlet.OPTION_SCROLL_BAR_WIDTH,
				FastTablePortlet.OPTION_ROW_HEIGHT, FastTablePortlet.OPTION_TITLE_BAR_COLOR, FastTablePortlet.OPTION_TITLE_BAR_FONT_COLOR, FastTablePortlet.OPTION_GRIP_COLOR,
				FastTablePortlet.OPTION_SCROLL_ICONS_COLOR, FastTablePortlet.OPTION_SEARCH_BUTTONS_COLOR };

		// Copy coloring options from original table 
		for (int i = 0; i < options.length; i++) {
			this.fastTable.addOption(options[i], ftp.getOption(options[i]));
		}

		BasicWebCellFormatter formatter = new BasicWebCellFormatter();
		formatter.addConditionalDefault("_fm=bold");
		this.fastTable.getTable().addColumn(true, "Operation", "Operation", formatter);

		for (int i = 0; i < tablePortlet.getVisibleColumnsCount(); i++) {
			webCol = tablePortlet.getVisibleColumn(i);
			this.fastTable.getTable().addColumn(true, webCol.getColumnName(), webCol.getColumnName(), new BasicWebCellFormatter());
		}

		// Set user style
		this.fastTable.setDialogStyle(AmiWebUtils.getService(getManager()).getUserDialogStyleManager());
		this.fastTable.setFormStyle(AmiWebUtils.getService(getManager()).getUserFormStyleManager());

		addChild(this.fastTable, 0, 0);
	}
	private String formatDouble(Double quantity, DecimalFormat df) {
		if (quantity == null)
			return null;
		return df.format(quantity);
	}

	private String formatComparable(Comparable comp, DecimalFormat df) {
		if (comp == null)
			return null;
		else {
			if (!(Number.class.isAssignableFrom(comp.getClass())))
				return SH.toString(comp);
			else
				return df.format(comp);
		}
	}

}
