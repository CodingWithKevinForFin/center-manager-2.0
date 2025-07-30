package com.f1.ami.web;

import java.util.Map;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;

class AmiWebTreemapSettingsPortlet extends AmiWebPanelSettingsPortlet {

	private AmiWebTreemapPortlet p;
	private FormPortletNumericRangeField stickyness;
	private FormPortletNumericRangeField ratio;
	private FormPortletSelectField<Boolean> ratioType;
	private final FormPortletToggleButtonsField<Boolean> clearOnDataStale;

	public AmiWebTreemapSettingsPortlet(PortletConfig config, AmiWebTreemapPortlet amiWebTreemapPortlet) {
		super(config, amiWebTreemapPortlet);
		FormPortlet settingsForm = getSettingsForm();
		this.clearOnDataStale = new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Clear on data stale");
		this.clearOnDataStale.setHelp("On: clears the data and shows hourglass when the underlying data model is running.<br>Off: keeps old (stale) data on screen while data model is running.");
		p = amiWebTreemapPortlet;
		stickyness = settingsForm.addField(new FormPortletNumericRangeField("Stickyness", 0, 9, 0)).setValue(p.getStickyness(false));
		final double ratio2 = p.getRatio(false);
		ratioType = settingsForm.addField(new FormPortletSelectField<Boolean>(Boolean.class, "Ratio")).addOption(Boolean.TRUE, "Tall").addOption(Boolean.FALSE, "Wider")
				.setValue(ratio2 < 1);
		ratio = settingsForm.addField(new FormPortletNumericRangeField("Ratio", 1, 9, 1));
		if (ratio2 < 1) {
			ratio.setValue(1 / ratio2);
		} else {
			ratio.setValue(ratio2);
		}
		if (amiWebTreemapPortlet instanceof AmiWebTreemapStaticPortlet) {
			AmiWebTreemapStaticPortlet dmPortlet = (AmiWebTreemapStaticPortlet) this.p;
			this.clearOnDataStale.addOption(true, "On");
			this.clearOnDataStale.addOption(false, "Off");
			this.clearOnDataStale.setValue(dmPortlet.isClearOnDataStale());
			settingsForm.addField(this.clearOnDataStale);
		}
	}

	@Override
	protected void submitChanges() {
		if (this.p instanceof AmiWebTreemapStaticPortlet) {
			AmiWebTreemapStaticPortlet dmPortlet = (AmiWebTreemapStaticPortlet) p;
			dmPortlet.setClearOnDataStale(this.clearOnDataStale.getValue());
		}
		super.submitChanges();
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (stickyness.equals(field))
			p.setStickyness(stickyness.getIntValue(), false);
		if (ratio == field || ratioType == field)
			p.setRatio(!ratioType.getValue() ? ratio.getValue() : (1d / ratio.getValue()), false);
		super.onFieldValueChanged(portlet, field, attributes);
	}

	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 500;
	}
	@Override
	public int getSuggestedHeight(PortletMetrics pm) {
		return 250;
	}
}
