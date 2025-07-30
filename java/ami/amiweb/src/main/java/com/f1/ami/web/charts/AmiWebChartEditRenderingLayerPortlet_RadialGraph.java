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
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.SH;

public class AmiWebChartEditRenderingLayerPortlet_RadialGraph extends AmiWebChartEditRenderingLayerPortlet<AmiWebChartRenderingLayer_RadialGraph>
		implements FormPortletListener, FormPortletContextMenuFactory, FormPortletContextMenuListener, ChooseDmListener {

	final private FormPortlet form;
	private AmiWebChartRenderingLayer_RadialGraph existing;
	private AmiWebChartPlotPortlet plot;
	private AmiWebChartGridPortlet chart;
	private FormPortletCheckboxField flipXField;
	private FormPortletCheckboxField flipYField;
	//	private FormPortletTextField angleLabelFormula;
	//	private FormPortletTextField radiusLabelFormula;
	private FormPortletCheckboxField minValueAutoField;
	private FormPortletCheckboxField maxValueAutoField;
	final private FormPortletTextField minValueField;
	final private FormPortletTextField maxValueField;
	private FormPortletButtonField dataModelField;
	private final AmiWebEditStylePortlet editStylePortlet;

	public AmiWebChartEditRenderingLayerPortlet_RadialGraph(PortletConfig config, AmiWebChartPlotPortlet plot, AmiWebChartRenderingLayer_RadialGraph layer) {
		super(config, layer);
		this.plot = plot;
		this.chart = plot.getChart();
		this.existing = layer;
		this.form = new FormPortlet(generateConfig());
		this.dataModelField = this.form.addField(new FormPortletButtonField("Data Model:").setHeight(35));
		this.flipXField = this.form.addField(new FormPortletCheckboxField("Flip X:"));
		this.flipYField = this.form.addField(new FormPortletCheckboxField("Flip Y:"));

		this.minValueAutoField = this.form.addField(new FormPortletCheckboxField("Auto Min Value:"));
		this.minValueField = this.form.addField(new FormPortletTextField("Min Value:"));
		this.maxValueAutoField = this.form.addField(new FormPortletCheckboxField("Auto Max Value:"));
		this.maxValueField = form.addField(new FormPortletTextField("Max Value:"));

		//		this.angleLabelFormula = this.form.addField(new FormPortletTextField("Circumference Label:").setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true));
		//		this.radiusLabelFormula = this.form.addField(new FormPortletTextField("Radius Label:").setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true));
		this.form.setMenuFactory(this);
		this.form.addMenuListener(this);
		this.addChild(form, 0, 0);
		FormPortlet styleFormHeader = new FormPortlet(generateConfig());
		styleFormHeader.addField(new FormPortletTitleField("Style Options"));
		this.addChild(styleFormHeader, 0, 1);
		this.editStylePortlet = new AmiWebEditStylePortlet(layer.getStylePeer(), generateConfig());
		this.addChild(this.editStylePortlet, 0, 2);
		this.editStylePortlet.hideButtonsForm(true);
		this.setRowSize(0, 100);
		this.setRowSize(1, 35);

		if (existing != null) {
			updateDmModelButton();
			this.flipXField.setValue(existing.getFlipX());
			this.flipYField.setValue(existing.getFlipY());
			//			this.angleLabelFormula.setValue(existing.getAngleLabelFormula() == null ? "\"\"" : existing.getAngleLabelFormula(false));
			//			this.radiusLabelFormula.setValue(existing.getAngleLabelFormula() == null ? "\"\"" : existing.getRadiusLabelFormula(false));
			this.minValueAutoField.setValue(existing.getAutoMinValue());
			this.maxValueAutoField.setValue(existing.getAutoMaxValue());
			this.minValueField.setValue(SH.toString(existing.getMinValue()));
			this.maxValueField.setValue(SH.toString(existing.getMaxValue()));
		} else {
			this.flipXField.setValue(false);
			this.flipYField.setValue(false);
			this.minValueAutoField.setValue(true);
			this.maxValueAutoField.setValue(true);
			//			this.angleLabelFormula.setValue("\"\"");
			//			this.radiusLabelFormula.setValue("\"\"");
		}

		this.form.addFormPortletListener(this);
		this.setSuggestedSize(500, 700);
		updateDmModelButton();
	}
	@Override
	public void updateDmModelButton() {
		this.dataModelField.setValue(this.existing.getDm().getAmiLayoutFullAliasDotId() + ":" + this.existing.getDmTableName());
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

	private void applySettings(AmiWebChartRenderingLayer_RadialGraph layer) {
		layer.setFlipX(this.flipXField.getBooleanValue());
		layer.setFlipY(this.flipYField.getBooleanValue());
		//		try {
		//			layer.setAngleLabelFormula(this.angleLabelFormula.getValue(), false);
		//		} catch (Exception e) {
		//		}
		//		try {
		//			layer.setRadiusLabelFormula(this.radiusLabelFormula.getValue(), false);
		//		} catch (Exception e) {
		//		}
		layer.setAutoMinValue(this.minValueAutoField.getBooleanValue());
		try {
			if (!this.minValueAutoField.getBooleanValue())
				layer.setMinValue(SH.parseDouble(this.minValueField.getValue()));
		} catch (Exception e) {
		}

		layer.setAutoMaxValue(this.maxValueAutoField.getBooleanValue());
		try {
			if (!this.maxValueAutoField.getBooleanValue())
				layer.setMaxValue(SH.parseDouble(this.maxValueField.getValue()));
		} catch (Exception e) {
		}
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}
	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		//		if (field == this.angleLabelFormula || field == this.radiusLabelFormula) {
		//			BasicWebMenu r = new BasicWebMenu();
		//			r.add(new BasicWebMenuLink("n", true, "var_n"));
		//			r.add(new BasicWebMenuDivider());
		//			AmiWebMenuUtils.createOperatorsMenu(r, this.existing.getChart().getService(), this.existing.getChart().getAmiLayoutFullAlias());
		//			return r;
		//		} else
		return null;
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		AmiWebMenuUtils.processContextMenuAction(this.existing.getChart().getService(), action, node);
		if (node == this.dataModelField) {
			String dmName = null;
			if (this.existing != null)
				dmName = this.existing.getDmAliasDotName();
			getManager().showDialog("Choose Datamodel", new AmiWebDmChooseDmTablePorlet(generateConfig(), dmName, this, false, this.chart.getAmiLayoutFullAlias()));

		}
	}
	private void updateDisabledFields() {
		this.minValueField.setDisabled(this.minValueAutoField.getBooleanValue());
		this.maxValueField.setDisabled(this.maxValueAutoField.getBooleanValue());
	}
	@Override
	public void onDmSelected(AmiWebDmTableSchema selectedDmTable) {
		this.existing.setDm(selectedDmTable.getDm().getAmiLayoutFullAliasDotId(), selectedDmTable.getName());
		updateDmModelButton();
	}
	@Override
	public AmiWebEditStylePortlet getEditStylePortlet() {
		return this.editStylePortlet;
	}

}
