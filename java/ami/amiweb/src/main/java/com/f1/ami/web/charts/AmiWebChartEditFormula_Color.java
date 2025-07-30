package com.f1.ami.web.charts;

import java.util.List;
import java.util.NoSuchElementException;

import com.f1.ami.web.AmiWebMenuUtils;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletColorField;
import com.f1.suite.web.portal.impl.form.FormPortletColorGradientField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextEditField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.ColorGradient;
import com.f1.utils.ColorHelper;
import com.f1.utils.SH;

public class AmiWebChartEditFormula_Color extends AmiWebChartEditFormula<AmiWebChartFormula_Color> {

	final private AmiWebChartFormula_Color formula;
	final private FormPortletSelectField<Byte> options;
	final private FormPortletTextField series = new FormPortletTextField("");
	final private FormPortletColorField color = new FormPortletColorField("");
	final private FormPortletColorGradientField gradient = new FormPortletColorGradientField("");
	final private FormPortletTextField field = new FormPortletTextField("");
	private AmiWebChartFormula_Color copyFromFormula;

	public AmiWebChartEditFormula_Color(int pos, AmiWebChartEditSeriesPortlet<?> target, AmiWebChartFormula_Color formula) {
		super(pos, target, formula);
		this.formula = formula;
		this.options = new FormPortletSelectField<Byte>(Byte.class, formula.getLabel());
		options.addOption(AmiWebChartFormula_Color.TYPE_COLOR_NONE, "No Color");
		options.addOption(AmiWebChartFormula_Color.TYPE_COLOR_CONST, "Color");
		options.addOption(AmiWebChartFormula_Color.TYPE_COLOR_CUSTOM, "Formula");
		options.addOption(AmiWebChartFormula_Color.TYPE_COLOR_DFLT_SERIES, "Series");
		options.addOption(AmiWebChartFormula_Color.TYPE_COLOR_DFLT_GRADIENT, "Gradient");
		options.addOption(AmiWebChartFormula_Color.TYPE_COLOR_CUSTOM_SERIES, "Custom Series");
		options.addOption(AmiWebChartFormula_Color.TYPE_COLOR_CUSTOM_GRADIENT, "Custom Gradient");
		field.setMaxChars(4048);
		field.setHasButton(true);
		field.setCorrelationData(this);
		series.setCorrelationData(this);
		options.setCorrelationData(this);
		gradient.setCorrelationData(this);
		color.setCorrelationData(this);
		this.color.setAllowNull(false);
		this.color.setHasButton(true);
		this.onFieldChanged(this.options);
		if (pos != -1) {
			options.setTopPosPx(pos).setWidthPx(70).setLeftPosPx(120).setHeightPx(20);
			this.gradient.setTopPosPx(pos).setLeftPosPx(195).setHeightPx(20).setWidthPx(100);
			this.color.setTopPosPx(pos).setLeftPosPx(195).setHeightPx(20).setWidthPx(100);
			this.series.setTopPosPx(pos).setLeftPosPx(195).setHeightPx(20).setWidthPx(100);
			this.field.setTopPosPx(pos).setRightPosPx(15).setLeftPosPx(300).setHeightPx(20);
			this.target.getForm().addField(color);
			this.target.getForm().addField(gradient);
			this.target.getForm().addField(options);
			this.target.getForm().addField(series);
			this.target.getForm().addField(field);
		}
		//		updateColorFields();
	}

	public ColorGradient getCustomGradient() {
		if (getColorType() == AmiWebChartFormula_Color.TYPE_COLOR_CUSTOM_GRADIENT)
			return this.gradient.getValue();
		return null;
	}

	public List<String> getCustomSeries() {
		if (getColorType() == AmiWebChartFormula_Color.TYPE_COLOR_CUSTOM_SERIES)
			return SH.splitToList(",", this.series.getValue());
		return null;
	}

	public String getVal() {
		switch (this.options.getValue()) {
			case AmiWebChartFormula_Color.TYPE_COLOR_NONE:
				return null;
			case AmiWebChartFormula_Color.TYPE_COLOR_DFLT_SERIES:
			case AmiWebChartFormula_Color.TYPE_COLOR_DFLT_GRADIENT:
			case AmiWebChartFormula_Color.TYPE_COLOR_CUSTOM:
			case AmiWebChartFormula_Color.TYPE_COLOR_CUSTOM_SERIES:
			case AmiWebChartFormula_Color.TYPE_COLOR_CUSTOM_GRADIENT:
				return this.field.getValue();
			case AmiWebChartFormula_Color.TYPE_COLOR_CONST:
				String color = this.color.getDisplayText();
				if (color != null)
					return color;
				color = this.color.getValue();
				return color != null ? SH.doubleQuote(color) : null;
			default:
				throw new NoSuchElementException(SH.toString(this.options.getValue()));
		}
	}

	public byte getColorType() {
		return this.options.getValue();
	}

	public void onFieldChanged(FormPortletField<?> field) {
		if (field == this.options) {
			// attempts to fill in valid values
			updateColorFields();
			switch (this.options.getValue()) {
				case AmiWebChartFormula_Color.TYPE_COLOR_CONST:
					if (SH.isnt(this.color.getValue())) {
						String value2 = SH.trim('"', (String) this.field.getValue());
						if (ColorHelper.isColor(value2)) {
							this.color.setValue(value2);
						}
					}
					break;
				case AmiWebChartFormula_Color.TYPE_COLOR_CUSTOM_GRADIENT:
					if (this.gradient.getValue() == null)
						this.gradient.setValue(this.target.getContainer().getStyleColorGradient());
					break;
				case AmiWebChartFormula_Color.TYPE_COLOR_CUSTOM_SERIES:
					if (SH.isnt(this.getVal()))
						this.field.setValue("__row_num");
					if (SH.isnt(this.series.getValue()))
						this.series.setValue(SH.join(',', this.target.getContainer().getStyleColorSeries()));
					break;
				case AmiWebChartFormula_Color.TYPE_COLOR_DFLT_SERIES:
					if (SH.isnt(this.getVal()))
						this.field.setValue("__row_num");
					break;
				case AmiWebChartFormula_Color.TYPE_COLOR_CUSTOM:
					if (SH.isnt(this.getVal()))
						this.field.setValue("__row_num");
					break;
			}
		}
	}

	// updates color fields, tries to fill in wherever valid
	private void updateColorFields() {
		switch (this.options.getValue()) {
			case AmiWebChartFormula_Color.TYPE_COLOR_DFLT_SERIES:
				this.series.setValue(SH.join(',', this.target.getContainer().getStyleColorSeries()));
				this.series.setDisabled(false);
				this.gradient.setVisible(false);
				this.color.setVisible(false);

				this.field.setTopPosPx(pos).setRightPosPx(15).setLeftPosPx(195).setHeightPx(20);
				this.field.setVisible(true);
				this.series.setVisible(true);
				break;
			case AmiWebChartFormula_Color.TYPE_COLOR_CUSTOM_SERIES:
				this.series.setValue(formula.getSeries() == null ? "" : SH.join(',', formula.getSeries()));
				this.series.setDisabled(false);
				this.gradient.setVisible(false);
				this.color.setVisible(false);

				this.field.setTopPosPx(pos).setRightPosPx(15).setLeftPosPx(300).setHeightPx(20);
				this.field.setVisible(true);
				this.series.setVisible(true);
				break;
			case AmiWebChartFormula_Color.TYPE_COLOR_DFLT_GRADIENT:
				this.gradient.setValue(this.target.getContainer().getStyleColorGradient());
				this.gradient.setDisabled(true);
				this.color.setVisible(false);
				this.series.setVisible(false);

				this.field.setTopPosPx(pos).setRightPosPx(15).setLeftPosPx(195).setHeightPx(20);
				this.field.setVisible(true);
				this.gradient.setVisible(false);
				break;
			case AmiWebChartFormula_Color.TYPE_COLOR_CUSTOM_GRADIENT:
				this.gradient.setValue(formula.getGradient());
				this.gradient.setDisabled(false);
				this.color.setVisible(false);
				this.series.setVisible(false);

				this.field.setTopPosPx(pos).setRightPosPx(15).setLeftPosPx(300).setHeightPx(20);
				this.field.setVisible(true);
				this.gradient.setVisible(true);
				break;
			case AmiWebChartFormula_Color.TYPE_COLOR_CONST:
				String value = formula.getValue();
				if (SH.startsWith(value, "$"))
					this.color.setValue(this.target.getContainer().getOwner().getStylePeer().getVarValues().get(value), value);
				else
					this.color.setValue(SH.trim('"', value != null ? value : "#1338BE"));
				this.gradient.setVisible(false);
				this.field.setVisible(false);
				this.series.setVisible(false);
				this.color.setVisible(true);
				break;
			case AmiWebChartFormula_Color.TYPE_COLOR_CUSTOM:
				this.gradient.setVisible(false);
				this.series.setVisible(false);
				this.color.setVisible(true);
				this.field.setTopPosPx(pos).setRightPosPx(15).setLeftPosPx(195).setHeightPx(20);
				this.field.setVisible(true);
				break;
			case AmiWebChartFormula_Color.TYPE_COLOR_NONE:
				this.gradient.setVisible(false);
				this.series.setVisible(false);
				this.color.setVisible(false);
				this.field.setVisible(false);
				break;
			default:
				throw new NoSuchElementException(SH.toString(this.options.getValue()));
		}
	}

	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		if (field == this.color) {
			return AmiWebMenuUtils.createColorFieldMenu(target.getContainer().getOwner().getStylePeer(), this.color);
		} else if (field == this.series && this.options.getValue() == AmiWebChartFormula_Color.TYPE_COLOR_CUSTOM_SERIES) {
			BasicWebMenu r = new BasicWebMenu();
			AmiWebMenuUtils.createColorsMenu(r, false, this.target.getContainer().getOwner().getStylePeer());
			return (BasicWebMenu) r.getChildren().get(0);
		} else {
			BasicWebMenu r = (BasicWebMenu) super.createMenu(formPortlet, field, cursorPosition);
			r.add(0, AmiWebMenuUtils.createColorsMenu(this.target.getContainer().getOwner().getStylePeer()));
			return r;
		}
	}
	public void onContextMenu(FormPortletField field, String action) {
		if (field == this.series && this.options.getValue() == AmiWebChartFormula_Color.TYPE_COLOR_CUSTOM_SERIES) {
			if (!AmiWebMenuUtils.processContextMenuAction(this.target.getService(), action, this.series))
				AmiWebMenuUtils.showCustomColorChooser(this.series, false);
		} else if ("_chooseColor".equals(action)) {
			AmiWebChartFormula_Color f = (AmiWebChartFormula_Color) getFormula();
			if (getColorType() == AmiWebChartFormula_Color.TYPE_COLOR_CUSTOM_SERIES) {
				AmiWebMenuUtils.showCustomColorChooser(series, false);
			} else
				AmiWebMenuUtils.showCustomColorChooser((FormPortletTextEditField) field);
			return;
		} else if (SH.startsWith(action, "conq_#")) {
			super.onContextMenu(this.series, action);
		} else {
			super.onContextMenu(field, action);
		}

	}

	public FormPortletField<String> getField() {
		return this.field;
	}

	@Override
	public boolean test(StringBuilder sb) {
		return getFormula().testColorValue(getColorType(), getCustomSeries(), getCustomGradient(), getVal(), sb);
	}

	@Override
	public void applyValue() {
		if (this.copyFromFormula != null)
			getFormula().setValue(this.copyFromFormula.getColorType(), this.copyFromFormula.getSeries(), this.copyFromFormula.getGradient(), this.copyFromFormula.getValue());
		else
			getFormula().setValue(getColorType(), getCustomSeries(), getCustomGradient(), getVal());
	}

	@Override
	public void resetFromFormula() {
		options.setValue(formula.getColorType());
		this.updateColorFields();
		this.field.setValue(this.formula.getValue());
	}

	@Override
	public void setTitle(String title) {
		this.options.setTitle(title);
	}
	public AmiWebChartEditFormula_Color setCopyFromFormula(AmiWebChartFormula_Color formula) {
		this.copyFromFormula = formula;
		return this;
	}

	public AmiWebChartEditFormula_Color setValueIfNotPopulated() {
		if (isPopulated())
			return this;
		this.options.setValue(AmiWebChartFormula_Color.TYPE_COLOR_CONST);
		this.gradient.setVisible(false);
		this.field.setVisible(false);
		this.series.setVisible(false);
		this.color.setVisible(true);
		this.color.setValue("#1338BE"); // Cobalt blue
		return this;
	}

	//	public String randomColorCode() {
	//		Random rand = new Random();
	//		StringBuilder res = new StringBuilder();
	//		res.append("#");
	//		int i = 0;
	//		while (i < 6) {
	//			int chance = rand.nextInt(2);
	//			if (chance == 1) {
	//				res.append(String.valueOf(rand.nextInt(10)));
	//			} else
	//				res.append((char) (rand.nextInt(6) + 'a'));
	//			i++;
	//		}
	//		return res.toString();
	//	}

	public AmiWebChartEditFormula_Color setSeriesIfNotPopulated(String string) {
		if (isPopulated())
			return this;
		this.options.setValue(AmiWebChartFormula_Color.TYPE_COLOR_DFLT_SERIES);
		this.field.setTopPosPx(pos).setRightPosPx(15).setLeftPosPx(195).setHeightPx(20);
		this.field.setVisible(true);
		this.field.setValue(string);
		this.gradient.setDisabled(true);
		this.color.setVisible(false);
		this.series.setVisible(false);
		this.gradient.setVisible(false);
		return this;
	}
	@Override
	public String getTitle() {
		return this.field.getTitle();
	}

	@Override
	public boolean isPopulated() {
		String val = this.getVal();
		if ("null".equals(val))
			return false;
		return SH.is(val);
	}

}