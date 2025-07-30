package com.f1.suite.web.portal.impl;

import java.util.Map;

import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.impl.CaseInsensitiveHasher;

public class FontMetricsManager {

	public static final FontMetricsManager INSTANCE = new FontMetricsManager();
	private Map<CharSequence, FontMetrics> fontMetrics = new HasherMap<CharSequence, FontMetrics>(CaseInsensitiveHasher.INSTANCE);

	public FontMetricsManager() {
		for (FontMetrics i : FontMetricsConsts.FONT_METRICS)
			fontMetrics.put(i.getFontName(), i);
	}

	public FontMetrics getFont(CharSequence fontName) {
		return fontMetrics.get(fontName);
	}
}
