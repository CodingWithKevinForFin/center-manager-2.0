package com.f1.office.spreadsheet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.f1.utils.SH;
import com.f1.utils.XlsxHelper;
import com.f1.utils.structs.Tuple2;

public class SpreadSheetUtils {
	
	private static final char[] INVALID_TAB_CHARS = "/\\*[]:?.".toCharArray();
	
	public static String getSheetName(final SpreadSheetWorkbook workbook, String name) {
		name = SH.replaceAllForAny(name, INVALID_TAB_CHARS, '_');
		name = workbook.getUniqueSheetName(name);
		return name;
	}
	
	public static Tuple2<Integer,Integer> getPositionFromExcelDim(String dim) {
		dim = SH.toUpperCase(dim);
		Matcher matcher = Pattern.compile("\\d+").matcher(dim);
		matcher.find();
		int y = SH.parseInt(matcher.group());
		matcher = Pattern.compile("[A-Z]+").matcher(dim);
		matcher.find();
		String start_x_str = matcher.group();
		int x = XlsxHelper.excelStringDimToInt(start_x_str);
		return new Tuple2<Integer,Integer>(x, y);
	}
	
	//Expects dimension in the format: [A-Z,a-z]+[1-9][0-9]*
	public static boolean isValidExcelDimension(final String dimension) {
		if (SH.is(dimension)) {
			final Tuple2<Integer, Integer> parsedDim = getPositionFromExcelDim(dimension);
			return parsedDim.getA() != 0 && parsedDim.getB() != 0;
		}
		return false;
	}
	
	public static boolean isValidExcelDimensionRange(final String dimension) {
		int index = SH.indexOf(dimension, ':', 0);
		if (index != -1) {
			String startDim = SH.substring(dimension, 0, index);
			String endDim = SH.substring(dimension, index+1, dimension.length());
			final Tuple2<Integer, Integer> parsedStartDim = getPositionFromExcelDim(startDim);
			final Tuple2<Integer, Integer> parsedEndDim = getPositionFromExcelDim(endDim);
			return parsedStartDim.getA() != 0 && parsedStartDim.getB() != 0 &&
					parsedEndDim.getA() != 0 && parsedEndDim.getB() != 0;
			
		}
		return false;
	}
}