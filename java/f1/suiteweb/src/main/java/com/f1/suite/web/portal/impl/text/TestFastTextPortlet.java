package com.f1.suite.web.portal.impl.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.base.Action;
import com.f1.http.HttpRequestResponse;
import com.f1.suite.web.HttpRequestAction;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.PortletManagerListener;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.utils.IntArrayList;
import com.f1.utils.structs.Tuple2;

public class TestFastTextPortlet extends GridPortlet implements TextModel, PortletManagerListener {

	private FastTextPortlet text;

	public TestFastTextPortlet(PortletConfig config) {
		super(config);
		text = new FastTextPortlet(generateConfig(), this);
		addChild(text, 0, 0);
		getManager().addPortletManagerListener(this);
	}

	@Override
	public int getNumberOfLines(FastTextPortlet target) {
		return 2000000000;
	}

	private List<Tuple2<Integer, Integer>> queue = new ArrayList<Tuple2<Integer, Integer>>();

	@Override
	public void prepareLines(FastTextPortlet target, int start, int count) {
		for (int i = start; i < start + count; i++) {
		}

		queue.add(new Tuple2<Integer, Integer>(start, count));

	}

	public static class Builder extends AbstractPortletBuilder<TestFastTextPortlet> {

		public static final String ID = "text";

		public Builder() {
			super(TestFastTextPortlet.class);
			setIcon("portlet_icon_text");
		}

		@Override
		public TestFastTextPortlet buildPortlet(PortletConfig portletConfig) {
			TestFastTextPortlet r = new TestFastTextPortlet(portletConfig);
			return r;
		}

		@Override
		public String getPortletBuilderName() {
			return "text";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onFrontendCalled(PortletManager manager, Map<String, String> attributes, HttpRequestAction action) {
		if (queue.isEmpty())
			return;
		Tuple2<Integer, Integer> t = queue.remove(0);
		Integer start = t.getA();
		Integer count = t.getB();
		IntArrayList t2 = new IntArrayList();
		for (int i = start; i < start + count; i++) {
			t2.add(i);
		}
		text.resetLinesAt(t2.toIntArray());
	}

	@Override
	public void onBackendCalled(PortletManager manager, Action action) {
	}

	@Override
	public void onInit(PortletManager manager, Map<String, Object> configuration, String rootId) {
	}

	@Override
	public WebMenu createMenu(FastTextPortlet fastTextPortlet) {
		return null;
	}

	@Override
	public int getLabelWidth(FastTextPortlet portlet) {
		return 0;
	}

	@Override
	public void formatText(FastTextPortlet portlet, int lineNumber, StringBuilder sink) {
	}

	@Override
	public void formatHtml(FastTextPortlet portlet, int lineNumber, StringBuilder sink) {
	}

	@Override
	public void formatLabel(FastTextPortlet portlet, int lineNumber, StringBuilder sink) {
	}

	@Override
	public void setColumnsVisible(int columns) {
	}

	@Override
	public void formatStyle(FastTextPortlet portlet, int lineNumber, StringBuilder sink) {
	}

	@Override
	public void onPortletManagerClosed() {
	}

	@Override
	public void onPageRefreshed(PortletManager basicPortletManager) {
	}
	@Override
	public void onMetadataChanged(PortletManager basicPortletManager) {
	}
	@Override
	public void onPageLoading(PortletManager basicPortletManager, Map<String, String> attributes, HttpRequestResponse action) {

	}

}
