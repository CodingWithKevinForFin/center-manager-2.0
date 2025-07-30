package com.f1.suite.web.table.impl;

import java.awt.Color;

import com.f1.suite.web.util.WebHelper;
import com.f1.utils.ColorHelper;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Color;

public class ColorWebCellFormatter extends BasicWebCellFormatter {

	final private String onErrorStyle;

	public ColorWebCellFormatter(String onErrorStyle) {
		this.onErrorStyle = onErrorStyle;
	}
	public ColorWebCellFormatter() {
		this.onErrorStyle = null;
	}

	@Override
	public String getDefaultHeaderStyle() {
		return "_fm=center";
	}

	@Override
	public String getDefaultColumnCssClass() {
		return "center";
	}
	@Override
	public void formatCellToHtml(Object value, StringBuilder sb, StringBuilder cellStyle) {
		if (value == null)
			return;
		Color color = Caster_Color.INSTANCE.cast(value, false, false);
		if (color == null) {
			WebHelper.escapeHtmlIncludeBackslash(SH.s(value), sb);
			if (getOnErrorStyle() != null)
				cellStyle.append(getOnErrorStyle());
		} else {
			ColorHelper.toString(color, sb);
			int dodged = ColorHelper.colorDodgeRgb(color.getRGB());
			cellStyle.append("_bg=");
			ColorHelper.toString(color, cellStyle);
			cellStyle.append("|_fg=");
			ColorHelper.toString(dodged, cellStyle);
		}
	}
	public String getOnErrorStyle() {
		return onErrorStyle;
	}

}