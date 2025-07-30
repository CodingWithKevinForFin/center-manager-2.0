package com.f1.ami.web;

import java.util.List;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.CH;

public class AmiWebTreemapRealtimeSettingsPortlet extends AmiWebTreemapSettingsPortlet {

	public AmiWebTreemapRealtimeSettingsPortlet(PortletConfig config, AmiWebTreemapRealtimePortlet treemapPortlet) {
		super(config, treemapPortlet);

		//		List<FormPortletField<?>> posUndefinedFields = form.getPosUndefinedFields();
		//		FormPortletField<?> f;
		//		topPosPx += FormPortletField.DEFAULT_HEIGHT;
		//		for (int i = 0; i < posUndefinedFields.size(); i++) {
		//			f = posUndefinedFields.get(i);
		//			f.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		//			f.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		//			f.setLeftPosPx(FormPortletField.DEFAULT_LEFT_POS_PX);
		//			f.setTopPosPx(topPosPx);
		//			topPosPx += FormPortletField.DEFAULT_HEIGHT + 10;
		//		}
		setSuggestedSize(300, 600);
	}

	@Override
	protected void initForms() {
		super.initForms();
		FormPortlet form = getSettingsForm();
		List<String> sourceList = CH.l(((AmiWebTreemapRealtimePortlet) this.getPortlet()).getLowerRealtimeIds());
		int numSources = sourceList.size();
		FormPortletField<?> sourceTableTitleField = form.addField(new FormPortletTitleField("Source Table" + (numSources == 1 ? "" : "s") + ": "), 0);
		//				.setCssStyle("style.text-transform=none").setHeightPx(10).setWidth(FormPortletField.DEFAULT_WIDTH).setTopPosPx(FormPortletField.DEFAULT_PADDING_PX - 4)
		//				.setLeftPosPx(FormPortletField.DEFAULT_LEFT_POS_PX);
		for (int i = 0; i < numSources; i++) {
			form.addField(new FormPortletTitleField(sourceList.get(i)), i + 1).setCssStyle("style.text-transform=none|_fm=");
			//.setCssStyle("style.text-transform=none").setHeightPx(10) .setLeftPosPx(FormPortletField.DEFAULT_LEFT_POS_PX + 21).setTopPosPx(topPosPx).setWidthPx(FormPortletField.DEFAULT_WIDTH);
		}
	}

}
