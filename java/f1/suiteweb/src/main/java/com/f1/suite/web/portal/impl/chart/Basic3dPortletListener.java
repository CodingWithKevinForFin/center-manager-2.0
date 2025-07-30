package com.f1.suite.web.portal.impl.chart;

import com.f1.suite.web.portal.impl.chart.Basic3dPortlet.Triangle;

public interface Basic3dPortletListener {

	public void onPerspective(Basic3dPortlet portlet);

	public void onSelectionChanged(Basic3dPortlet basic3dPortlet);

	public void onContextMenu(Basic3dPortlet basic3dPortlet);

	public void onHover(Basic3dPortlet basic3dPortlet, int x, int y, int selectId, Triangle triangle);

}
