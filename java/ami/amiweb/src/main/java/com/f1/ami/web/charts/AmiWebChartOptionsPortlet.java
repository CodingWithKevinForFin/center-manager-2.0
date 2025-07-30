package com.f1.ami.web.charts;

import java.util.Map;

import com.f1.ami.web.AmiWebLockedPermissiblePortlet;
import com.f1.ami.web.AmiWebUtils;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.RootPortletDialog;
import com.f1.suite.web.portal.impl.RootPortletDialogListener;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;

public class AmiWebChartOptionsPortlet extends HtmlPortlet implements RootPortletDialogListener, AmiWebLockedPermissiblePortlet {

	private AmiWebChartGridPortlet chart;
	private OptionsPortlet configPortlet;
	private Callback cb_axis;
	private String bgColor;

	public AmiWebChartOptionsPortlet(AmiWebChartGridPortlet chart, PortletConfig manager) {
		super(manager);
		this.chart = chart;

		this.cb_axis = new Callback("axis_");
		setHtml("<div class='ami_chart_options' onclick='" + this.generateCallback(cb_axis) + "'>&alpha;</div>");
		this.bgColor = getChart().getBackgroundColor();
		super.setCssStyle("style.background-color=" + bgColor);
	}
	public AmiWebChartGridPortlet getChart() {
		return chart;
	}

	@Override
	protected void onUserCallback(String id, int mouseX, int mouseY, HtmlPortlet.Callback cb) {
		super.onUserCallback(id, mouseX, mouseY, cb);
		configPortlet = new OptionsPortlet(generateConfig());
		RootPortletDialog dialog = getManager().getCurrentRootPortlet().addDialog("Chart Options", configPortlet, configPortlet.getWidth(), configPortlet.getHeight(), true);
		dialog.setPosition(mouseX - configPortlet.getWidth(), mouseY - configPortlet.getHeight());
		dialog.setShadeOutside(false);
		dialog.setHasCloseButton(false);
		dialog.setEscapeKeyCloses(false);
		dialog.addListener(this);
		dialog.setStyle(AmiWebUtils.getService(getManager()).getUserDialogStyleManager());
		getManager().onPortletAdded(configPortlet);
	}

	@Override
	public void onDialogClickoutside(RootPortletDialog dialog) {
		dialog.close();
	}

	@Override
	public void onDialogVisible(RootPortletDialog rootPortletDialog, boolean b) {
	}

	@Override
	public void onDialogMoved(RootPortletDialog rootPortletDialog) {
	}

	public class OptionsPortlet extends GridPortlet implements FormPortletListener {

		public OptionsPortlet(PortletConfig config) {
			super(config);
			FormPortlet form = new FormPortlet(generateConfig());
			addChild(form, 0, 0);
			int h = 45;
			form.addField(new FormPortletTitleField("Transparency"));
			form.getFormPortletStyle().setLabelsWidth(200);
			String optionsSlidersColor = getChart().getOptionsSlidersColor();
			for (AmiWebChartPlotPortlet plot : chart.getPlots()) {
				for (AmiWebChartRenderingLayer layer : plot.getRenderyingLayers()) {
					form.addField(new FormPortletNumericRangeField(layer.getDescription(), 0, 100, 0).setWidth(102)).setCorrelationData(layer).setValue((double) layer.getOpacity())
							.setLeftScrollTrackColor(optionsSlidersColor).setScrollGripColor(optionsSlidersColor);
					h += 21;
				}
			}
			form.addFormPortletListener(this);
			form.setStyle(AmiWebUtils.getService(getManager()).getUserFormStyleManager());
			this.setSize(350, h);
		}

		@Override
		public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		}

		@Override
		public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
			Object cd = field.getCorrelationData();
			if (cd instanceof AmiWebChartRenderingLayer) {
				((AmiWebChartRenderingLayer) cd).setOpacity(((FormPortletNumericRangeField) field).getIntValue());
			}
		}
		@Override
		public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		}

	}

	@Override
	public void onDialogClosed(RootPortletDialog rootPortletDialog) {

	}
	@Override
	public void onUserCloseDialog(RootPortletDialog rootPortletDialog) {

	}
}
