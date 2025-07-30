package com.f1.ami.web.surface;

import com.f1.ami.web.charts.AmiWebChartEditSeriesPortlet;
import com.f1.ami.web.charts.AmiWebChartFormula_Color;
import com.f1.ami.web.charts.AmiWebChartFormula_Simple;
import com.f1.ami.web.charts.AmiWebChartSeriesContainer;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.portlets.AmiWebDmChooseDmTablePorlet;
import com.f1.ami.web.dm.portlets.AmiWebDmChooseDmTablePorlet.ChooseDmListener;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;

public class AmiWebSurfaceEditSeriesPortlet extends AmiWebChartEditSeriesPortlet<AmiWebSurfaceSeries> implements ChooseDmListener {

	private FormPortletToggleButtonsField<Byte> typeToggle;
	private FormPortletButtonField dataModelButton;

	public AmiWebSurfaceEditSeriesPortlet(PortletConfig config) {
		super(config);
		this.setSuggestedSize(600, 700);
		showButtons(true);
	}

	@Override
	public AmiWebChartEditSeriesPortlet<AmiWebSurfaceSeries> setContainer(AmiWebChartSeriesContainer<AmiWebSurfaceSeries> previewPortlet, AmiWebSurfaceSeries series) {
		super.setContainer(previewPortlet, series);
		updateFields();
		return this;
	}

	@Override
	public boolean preview() {
		existing.setType(this.typeToggle.getValue());
		return super.preview();
	}

	@Override
	protected void initForm() {
		AmiWebSurfaceSeries series = this.existing;
		this.dataModelButton = this.form.addField(new FormPortletButtonField("Data Model:"));
		this.dataModelButton.setLeftTopWidthHeightPx(this.viewDataButton.getLeftPosPx(), this.viewDataButton.getTopPosPx() + 30,
				this.viewDataButton.getWidthPx() + this.testDataButton.getWidthPx() + 10, 20);

		typeToggle = getForm().addField(new FormPortletToggleButtonsField<Byte>(Byte.class, "Type:"));
		typeToggle.addOption(AmiWebSurfaceSeries.TYPE_XYZ, "Scatter");
		typeToggle.addOption(AmiWebSurfaceSeries.TYPE_SURFACE, "Surface");
		typeToggle.setValue(series.getType());
		this.typeToggle.setLeftTopWidthHeightPx(this.viewDataButton.getLeftPosPx(), this.dataModelButton.getTopPosPx() + 30, 200, 20);
		incrementPosition(65);

		super.initForm();

		//		form.addField(new FormPortletTitleField("Axis"));
		//		FormPortletTextField field;
		//		field = form.addField(new FormPortletTextField("X: "));
		//		initField(field, series.getXField());
		//		field = form.addField(new FormPortletTextField("Y: "));
		//		initField(field, series.getYField());
		//		field = form.addField(new FormPortletTextField("Z: "));
		//		initField(field, series.getZField());
		//
		//		form.addField(new FormPortletTitleField("Labels"));
		//		field = form.addField(new FormPortletTextField("User Selectable: "));
		//		initField(field, series.getSelectableFormula());
		//		field = form.addField(new FormPortletTextField("Description: "));
		//		initField(field, series.getDescriptionFormula());
		//		field = form.addField(new FormPortletTextField("Hover Over:"));
		//		initField(field, series.getTooltipFormula());
		//
		//		form.addField(new FormPortletTitleField("Markers"));
		//		field = form.addField(new FormPortletTextField("Shape:"));
		//		initField(field, series.getmShapeFormula());
		//		field = form.addField(new FormPortletTextField("Color:"));
		//		initField(field, series.getmColorFormula());
		//		field = form.addField(new FormPortletTextField("Width (px):"));
		//		initField(field, series.getmWidthFormula());
		//		field = form.addField(new FormPortletTextField("Height (px):"));
		//		initField(field, series.getmHeightFormula());
		//		field = form.addField(new FormPortletTextField("Depth (px):"));
		//		initField(field, series.getmDepthField());
		//
		//		form.addField(new FormPortletTitleField("Marker Position Override"));
		//		field = form.addField(new FormPortletTextField("Top:"));
		//		initField(field, series.getTopField());
		//		field = form.addField(new FormPortletTextField("Bottom:"));
		//		initField(field, series.getBottomField());
		//		field = form.addField(new FormPortletTextField("Left:"));
		//		initField(field, series.getLeftField());
		//		field = form.addField(new FormPortletTextField("Right:"));
		//		initField(field, series.getRightField());
		//		field = form.addField(new FormPortletTextField("Front:"));
		//		initField(field, series.getFrontField());
		//		field = form.addField(new FormPortletTextField("Back:"));
		//		initField(field, series.getBackField());
		//
		//		form.addField(new FormPortletTitleField("Lines"));
		//		field = form.addField(new FormPortletTextField("Line Color:"));
		//		initField(field, series.getLineColorFormula());
		//		field = form.addField(new FormPortletTextField("Line Thickness:"));
		//		initField(field, series.getLineSizeFormula());
		//
		//		form.addField(new FormPortletTitleField("Partitioning (For Lines and Aggregates)"));
		//		field = form.addField(new FormPortletTextField("Partition By:"));
		//		initField(field, series.getNameFormula());
		//		field = form.addField(new FormPortletTextField("Order Partition By:"));
		//		initField(field, series.getOrderByFormula());
		if (existing != null)
			updateDmModelButton();

	}
	private void updateDmModelButton() {
		AmiWebDmTableSchema dm = this.existing.getDataModelSchema();
		this.dataModelButton.setValue(dm.getDm().getAmiLayoutFullAliasDotId() + ":" + dm.getName());
	}

	@Override
	public String getEditorLabel() {
		return "Advanced 3D Chart";
	}

	@Override
	public String getEditorTypeId() {
		return TYPE_3D_ADVANCED;
	}

	@Override
	public void onDmSelected(AmiWebDmTableSchema selectedDmTable) {
		this.existing.setDatamodel(selectedDmTable.getDm().getAmiLayoutFullAliasDotId(), selectedDmTable.getName());
		updateDmModelButton();
	}
	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		if (node == this.dataModelButton) {
			String dmName = null;
			if (this.existing != null) {
				dmName = this.existing.getDmAliasDotName();
			}
			getManager().showDialog("Choose Datamodel", new AmiWebDmChooseDmTablePorlet(generateConfig(), dmName, this, false, this.container.getAmiLayoutFullAlias()));

		} else
			super.onContextMenu(portlet, action, node);
	}

	@Override
	public void fillDefaultFields() {
		AmiWebSurfaceSeries series = this.existing;
		getEditor((AmiWebChartFormula_Simple) series.getmShapeFormula()).setValueIfNotPopulated("\"pyramid\"");
		getEditor((AmiWebChartFormula_Color) series.getmColorFormula()).setValueIfNotPopulated();
		getEditor((AmiWebChartFormula_Simple) series.getmWidthFormula()).setValueIfNotPopulated("1");
		getEditor((AmiWebChartFormula_Simple) series.getmHeightFormula()).setValueIfNotPopulated("1");
		getEditor((AmiWebChartFormula_Simple) series.getmDepthField()).setValueIfNotPopulated("1");
	}

}
