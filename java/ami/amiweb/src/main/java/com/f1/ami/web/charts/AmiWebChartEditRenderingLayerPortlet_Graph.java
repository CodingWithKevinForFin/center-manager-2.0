package com.f1.ami.web.charts;

import java.util.Map;

import com.f1.ami.web.AmiWebEditStylePortlet;
import com.f1.ami.web.AmiWebMenuUtils;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.portlets.AmiWebDmChooseDmTablePorlet;
import com.f1.ami.web.dm.portlets.AmiWebDmChooseDmTablePorlet.ChooseDmListener;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.CH;

public class AmiWebChartEditRenderingLayerPortlet_Graph extends AmiWebChartEditRenderingLayerPortlet<AmiWebChartRenderingLayer_Graph>
		implements FormPortletListener, FormPortletContextMenuFactory, FormPortletContextMenuListener, ChooseDmListener {

	final private FormPortlet form;
	private AmiWebChartRenderingLayer_Graph existing;
	private AmiWebChartPlotPortlet plot;
	private FormPortletSelectField<Integer> xAxisField;
	private FormPortletSelectField<Integer> yAxisField;
	private FormPortletButtonField dataModelButton;
	private AmiWebChartGridPortlet chart;
	private final AmiWebEditStylePortlet editStylePortlet;

	public AmiWebChartEditRenderingLayerPortlet_Graph(PortletConfig config, AmiWebChartPlotPortlet plot, AmiWebChartRenderingLayer_Graph layer) {
		super(config, layer);
		this.plot = plot;
		this.chart = plot.getChart();
		this.existing = layer;
		this.form = new FormPortlet(generateConfig());
		this.dataModelButton = this.form.addField(new FormPortletButtonField("Data Model:")).setHeight(35);
		this.xAxisField = this.form.addField(new FormPortletSelectField<Integer>(Integer.class, "Horizontal Axis:"));
		this.yAxisField = this.form.addField(new FormPortletSelectField<Integer>(Integer.class, "Vertical Axis:"));
		this.addChild(form, 0, 0);
		FormPortlet styleFormHeader = new FormPortlet(generateConfig());
		styleFormHeader.addField(new FormPortletTitleField("Style Options"));
		this.addChild(styleFormHeader, 0, 1);
		this.editStylePortlet = new AmiWebEditStylePortlet(layer.getStylePeer(), generateConfig());
		this.editStylePortlet.hideButtonsForm(true);
		this.addChild(this.editStylePortlet, 0, 2);
		this.setRowSize(0, 110);
		this.setRowSize(1, 35);

		int tCnt = chart.getAxis(AmiWebChartGridPortlet.POS_T, plot.getCol()).size();
		int bCnt = chart.getAxis(AmiWebChartGridPortlet.POS_B, plot.getCol()).size();
		int lCnt = chart.getAxis(AmiWebChartGridPortlet.POS_L, plot.getRow()).size();
		int rCnt = chart.getAxis(AmiWebChartGridPortlet.POS_R, plot.getRow()).size();

		for (int i = 0; i < tCnt; i++)
			xAxisField.addOption(-1 - i, describeAxis(tCnt - i, tCnt, "Upper"));
		for (int i = 0; i < bCnt; i++)
			xAxisField.addOption(1 + i, describeAxis(i + 1, bCnt, "Lower"));

		for (int i = lCnt - 1; i >= 0; i--)
			yAxisField.addOption(-1 - i, describeAxis(i + 1, lCnt, "Left"));
		for (int i = 0; i < rCnt; i++)
			yAxisField.addOption(1 + i, describeAxis(i + 1, rCnt, "Right"));

		if (existing != null)
			updateDmModelButton();
		this.xAxisField.setValue(toInt(existing.getXAxis()));
		this.yAxisField.setValue(toInt(existing.getYAxis()));

		this.form.addFormPortletListener(this);
		this.form.setMenuFactory(this);
		this.form.addMenuListener(this);
		this.setSuggestedSize(500, 500);
		updateDmModelButton();
	}

	@Override
	public void updateDmModelButton() {
		if (this.existing.getDm() != null)
			this.dataModelButton.setValue(this.existing.getDm().getAmiLayoutFullAliasDotId() + ":" + this.existing.getDmTableName());
		else {
			this.dataModelButton.setValue("&lt;No datamodel&gt;");
		}
	}
	private int toInt(AmiWebChartAxisPortlet axis) {
		if (axis == null)
			return 0;
		switch (axis.getPosition()) {
			case AmiWebChartGridPortlet.POS_R:
			case AmiWebChartGridPortlet.POS_B:
				return 1 + axis.getOffset();
			case AmiWebChartGridPortlet.POS_T:
			case AmiWebChartGridPortlet.POS_L:
				return axis.getOffset() - chart.getAxis(axis.getPosition(), axis.getRowOrCol()).size();
			default:
				throw new RuntimeException("Bad position: " + axis.getPosition());
		}
	}

	private String describeAxis(int i, int cnt, String text) {
		if (cnt == 1)
			return text + " Axis";
		if (i == cnt)
			return text + " Axis #" + i + " (" + text + "-most)";
		if (i == 1)
			return text + " Axis #" + i + " (Closest)";
		return text + " Axis #" + i;
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (existing != null) {
			applySettings(existing);
		}
	}

	private void applySettings(AmiWebChartRenderingLayer_Graph layer) {
		Integer x = xAxisField.getValue();
		Integer y = yAxisField.getValue();

		AmiWebChartAxisPortlet xAxis = x == 0 ? null
				: CH.getAtMod(chart.getAxis(x < 0 ? AmiWebChartGridPortlet.POS_T : AmiWebChartGridPortlet.POS_B, this.plot.getCol()), x < 0 ? x : x - 1);
		AmiWebChartAxisPortlet yAxis = y == 0 ? null
				: CH.getAtMod(chart.getAxis(y < 0 ? AmiWebChartGridPortlet.POS_L : AmiWebChartGridPortlet.POS_R, this.plot.getRow()), y < 0 ? y : y - 1);

		layer.setXAxis(xAxis == null ? -1 : xAxis.getAxisId());
		layer.setYAxis(yAxis == null ? -1 : yAxis.getAxisId());
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	@Override
	public void onDmSelected(AmiWebDmTableSchema selectedDmTable) {
		this.existing.setDm(selectedDmTable.getDm().getAmiLayoutFullAliasDotId(), selectedDmTable.getName());
		updateDmModelButton();
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		AmiWebMenuUtils.processContextMenuAction(this.existing.getChart().getService(), action, node);
		if (node == this.dataModelButton) {
			String dmName = null;
			if (this.existing != null) {
				dmName = this.existing.getDmAliasDotName();
			}
			getManager().showDialog("Choose Datamodel", new AmiWebDmChooseDmTablePorlet(generateConfig(), dmName, this, false, this.chart.getAmiLayoutFullAlias()));

		}
	}
	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		return null;
	}

	@Override
	public AmiWebEditStylePortlet getEditStylePortlet() {
		return this.editStylePortlet;
	}

}
