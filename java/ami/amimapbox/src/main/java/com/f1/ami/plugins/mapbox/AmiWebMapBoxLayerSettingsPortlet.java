package com.f1.ami.plugins.mapbox;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebDesktopPortlet;
import com.f1.ami.web.AmiWebMenuUtils;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.base.Mapping;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;

public class AmiWebMapBoxLayerSettingsPortlet extends GridPortlet implements FormPortletContextMenuListener, FormPortletListener, FormPortletContextMenuFactory {
	public static Logger log = LH.get();
	final public static int DEFAULT_LABEL_LIMIT = 1000;
	final public static int DEFAULT_LABEL_FONT_SIZE = 12;
	final private FormPortlet fm;
	final private FormPortletTextField latitudeField;
	final private FormPortletTextField longitudeField;
	final private FormPortletTextField sizeField;
	final private FormPortletTextField borderColorField;
	final private FormPortletTextField fillColorField;
	final private FormPortletTextField opacityField;
	final private FormPortletTextField tooltipField;
	final private FormPortletTextField labelField;
	final private FormPortletTextField labelLimitField;
	final private FormPortletTextField labelFontSizeField;
	final private FormPortletTextField labelFontFamilyField;
	final private FormPortletTextField labelFontColorField;
	final private FormPortletTextField labelPositionField;

	final private AmiWebMapBoxPanel amiPortlet;
	final private AmiWebService service;
	final private AmiWebMapBoxLayer layer;
	final private FormPortletTextField titleField;
	final private boolean isEdit;
	final private FormPortletSelectField<AmiWebDmTableSchema> dmField;
	final private FormPortletButton addUpdateButton;

	public AmiWebMapBoxLayerSettingsPortlet(PortletConfig config, AmiWebMapBoxPanel portlet, AmiWebMapBoxLayer layer, boolean isEdit) {
		super(config);
		this.isEdit = isEdit;
		this.service = AmiWebUtils.getService(config.getPortletManager());
		this.layer = layer;
		String dmAliasDotName = layer.getDmAliasDotName();
		String dmTableName = layer.getDmTableName();
		this.amiPortlet = portlet;
		fm = addChild(new FormPortlet(generateConfig()), 0, 0);
		this.fm.getFormPortletStyle().setLabelsWidth(10);

		fm.addField(new FormPortletTitleField("Layer Title"));
		titleField = fm.addField(new FormPortletTextField("")).setWidth(200).setValue(layer.getTitle());

		fm.addField(new FormPortletTitleField("Datamodel"));
		dmField = fm.addField(new FormPortletSelectField<AmiWebDmTableSchema>(AmiWebDmTableSchema.class, ""));
		for (String i : portlet.getUsedDmAliasDotNames()) {
			AmiWebDm dm = portlet.getService().getDmManager().getDmByAliasDotName(i);
			Set<String> tables = portlet.getUsedDmTables(i);
			for (String s : tables) {
				AmiWebDmTableSchema table = dm.getResponseOutSchema().getTable(s);
				dmField.addOption(table, dm.getDmName() + " -> " + s);
				if (OH.eq(dm.getAmiLayoutFullAliasDotId(), dmAliasDotName) && OH.eq(s, dmTableName))
					dmField.setValue(table);
			}
		}
		RootPortlet root = (RootPortlet) this.service.getPortletManager().getRoot();
		int width = MH.min(AmiWebDesktopPortlet.MAX_WIDTH, (int) (root.getWidth() * 0.5));
		int height = MH.min(AmiWebDesktopPortlet.MAX_HEIGHT, (int) (root.getHeight() * 0.8));
		fm.addField(new FormPortletTitleField("Latitude (required)"));
		latitudeField = fm.addField(new FormPortletTextField("")).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true)
				.setValue(layer.getLatitudeFormula().getFormula(false));

		fm.addField(new FormPortletTitleField("Longitude (required)"));
		longitudeField = fm.addField(new FormPortletTextField("")).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true)
				.setValue(layer.getLongitudeFormula().getFormula(false));

		fm.addField(new FormPortletTitleField("Size (required)"));
		sizeField = fm.addField(new FormPortletTextField("")).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true).setValue(layer.getSizeFormula().getFormula(false));

		fm.addField(new FormPortletTitleField("Border Color"));
		borderColorField = fm.addField(new FormPortletTextField("")).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true)
				.setValue(layer.getBorderColorFormula().getFormula(false));

		fm.addField(new FormPortletTitleField("Fill Color"));
		fillColorField = fm.addField(new FormPortletTextField("")).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true)
				.setValue(layer.getFillColorFormula().getFormula(false));

		fm.addField(new FormPortletTitleField("Opacity (0.0=invisible, 1.0=visible) (required)"));
		opacityField = fm.addField(new FormPortletTextField("")).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true)
				.setValue(layer.getOpacityFormula().getFormula(false));

		fm.addField(new FormPortletTitleField("Tooltip"));
		tooltipField = fm.addField(new FormPortletTextField("")).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true)
				.setValue(layer.getTooltipFormula().getFormula(false));

		fm.addField(new FormPortletTitleField("Label"));
		labelField = fm.addField(new FormPortletTextField("")).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true).setValue(layer.getLabelFormula().getFormula(false));

		fm.addField(new FormPortletTitleField("Label Limit (Default Value=" + DEFAULT_LABEL_LIMIT + ")"));
		labelLimitField = fm.addField(new FormPortletTextField("")).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true)
				.setValue(Caster_String.INSTANCE.cast(layer.getLabelLimit()));

		fm.addField(new FormPortletTitleField("Label Font Family"));
		labelFontFamilyField = fm.addField(
				new FormPortletTextField("").setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true).setValue(layer.getLabelFontFamilyFormula().getFormula(false)));

		fm.addField(new FormPortletTitleField("Label Font Size"));
		labelFontSizeField = fm
				.addField(new FormPortletTextField("").setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true).setValue(layer.getLabelFontSizeFormula().getFormula(false)));

		fm.addField(new FormPortletTitleField("Label Font Color"));
		labelFontColorField = fm.addField(
				new FormPortletTextField("").setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true).setValue(layer.getLabelFontColorFormula().getFormula(false)));

		fm.addField(new FormPortletTitleField("Label Position"));
		labelPositionField = fm
				.addField(new FormPortletTextField("").setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true).setValue(layer.getLabelPositionFormula().getFormula(false)));

		latitudeField.setCssStyle("_fm=courier");
		longitudeField.setCssStyle("_fm=courier");
		sizeField.setCssStyle("_fm=courier");
		borderColorField.setCssStyle("_fm=courier");
		fillColorField.setCssStyle("_fm=courier");
		opacityField.setCssStyle("_fm=courier");
		tooltipField.setCssStyle("_fm=courier");

		this.addUpdateButton = fm.addButton(new FormPortletButton(!this.isEdit ? "Add Layer" : "Update Layer"));
		fm.addMenuListener(this);
		fm.setMenuFactory(this);
		fm.addFormPortletListener(this);
		setSuggestedSize(width, height);
	}
	public AmiWebMapBoxLayerSettingsPortlet hideButtonsForm(boolean hide) {
		this.fm.clearButtons();
		if (!hide)
			this.fm.addButton(this.addUpdateButton);
		return this;
	}

	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		BasicWebMenu r = new BasicWebMenu();
		if (field == this.borderColorField || field == this.fillColorField || field == this.labelFontColorField)
			AmiWebMenuUtils.createColorsMenu(r, this.amiPortlet.getStylePeer());
		if (field == this.labelPositionField)
			AmiWebMenuUtils.createPositionsMenu(r, false);
		if (field == this.labelFontFamilyField)
			AmiWebMenuUtils.createFormatsMenu(r, service);
		AmiWebMenuUtils.createOperatorsMenu(r, service, this.amiPortlet.getAmiLayoutFullAlias());
		r.add(new BasicWebMenuDivider());
		String dmAliasDotName = this.dmField.getValue().getDm().getAmiLayoutFullAliasDotId();
		String dmtb = this.dmField.getValue().getName();
		r.add(AmiWebMenuUtils.createVariablesMenu(this.amiPortlet.getService(), "Variables", "", this.amiPortlet, dmAliasDotName, dmtb));
		return r;
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (!applySettings())
			return;
		if (this.getParent() instanceof RootPortlet)
			close();
	}
	public boolean applySettings() {
		AmiWebDmTableSchema dm = this.dmField.getValue();

		com.f1.base.CalcTypes variables = dm.getClassTypes();
		String title = this.titleField.getValue();
		AmiWebMapBoxLayer layer2 = new AmiWebMapBoxLayer(this.amiPortlet, this.layer.getId(), dm.getDm().getAmiLayoutFullAliasDotId(), dm.getName());
		layer2.setTitle(title);
		try {
			layer2.getLatitudeFormula().setFormula(this.latitudeField.getValue(), false);
			layer2.getLongitudeFormula().setFormula(this.longitudeField.getValue(), false);
			layer2.getSizeFormula().setFormula(this.sizeField.getValue(), false);
			layer2.getOpacityFormula().setFormula(this.opacityField.getValue(), false);
			layer2.getBorderColorFormula().setFormula(this.borderColorField.getValue(), false);
			layer2.getFillColorFormula().setFormula(this.fillColorField.getValue(), false);
			layer2.getTooltipFormula().setFormula(this.tooltipField.getValue(), false);
			layer2.getLabelFormula().setFormula(this.labelField.getValue(), false);

			int limit = DEFAULT_LABEL_LIMIT;
			if (SH.isnt(this.labelLimitField.getValue()))
				limit = DEFAULT_LABEL_LIMIT;
			else {
				limit = Caster_Integer.INSTANCE.cast(this.labelLimitField.getValue());
				if (limit < 1) {
					getManager().showAlert("<b>Label Limit</b> must be a number greater than 0");
					return false;
				}
			}
			layer2.setLabelLimit(Caster_Integer.INSTANCE.cast(limit));
			layer2.getLabelFontFamilyFormula().setFormula(this.labelFontFamilyField.getValue(), false);
			layer2.getLabelFontSizeFormula().setFormula(this.labelFontSizeField.getValue(), false);
			layer2.getLabelFontColorFormula().setFormula(this.labelFontColorField.getValue(), false);
			layer2.getLabelPositionFormula().setFormula(this.labelPositionField.getValue(), false);
		} catch (Exception e) {
			getManager().showAlert(e.getMessage(), e);
			return false;
		}
		this.amiPortlet.addLayer(layer2);
		return true;
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		AmiWebMenuUtils.processContextMenuAction(service, action, node);
	}

	public AmiWebMapBoxLayerSettingsPortlet populateDefaultSettings() {
		this.titleField.setValue("Layer");
		this.sizeField.setValue("200");
		this.borderColorField.setValue("\"#000000\"");
		this.fillColorField.setValue("\"#ff0000\"");
		this.opacityField.setValue("1.0");
		return this;
	}

}
