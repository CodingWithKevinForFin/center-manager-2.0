package com.f1.suite.web.portal.impl;

import java.util.Map;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.CH;
import com.f1.utils.casters.Caster_String;

public class HtmlCustomPortlet extends HtmlPortlet {

	private boolean onUserClickEnabled = true;

	public HtmlCustomPortlet(PortletConfig manager) {
		super(manager,
				"<div style='cursor:pointer;text-align:center;width:100%;height:100%;font-weight:bold'>\n  Double click to Edit HTML<BR>\n  Then replace this text with something useful\n</div>",
				"comment_header");
	}

	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		super.init(configuration, origToNewIdMapping, sb);
		setHtml(CH.getOrThrow(Caster_String.INSTANCE, configuration, "html"));
		if (configuration.containsKey("cssclass"))
			setCssClass(CH.getOrThrow(Caster_String.INSTANCE, configuration, "cssclass"));
	}
	@Override
	public Map<String, Object> getConfiguration() {
		final Map<String, Object> r = super.getConfiguration();
		r.put("html", getHtml());
		if (getCssClass() != null)
			r.put("cssclass", getCssClass());
		return r;
	}

	protected void onUserClick() {
		if (onUserClickEnabled)
			showEditHtmlDialog();
	}

	public void showEditHtmlDialog() {
		getManager().showDialog("custom html", new HtmlFormPortlet(this, generateConfig()));
	}

	public static class Builder extends AbstractPortletBuilder<HtmlCustomPortlet> {

		public static final String ID = "html";

		public Builder() {
			super(HtmlCustomPortlet.class);
		}

		@Override
		public HtmlCustomPortlet buildPortlet(PortletConfig portletConfig) {
			HtmlCustomPortlet r = new HtmlCustomPortlet(portletConfig);
			return r;
		}

		@Override
		public String getPortletBuilderName() {
			return "html";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	private static class HtmlFormPortlet extends FormPortlet implements FormPortletListener {

		private FormPortletTextField css;
		private FormPortletTextAreaField html;
		private FormPortletButton submitButton;
		private HtmlCustomPortlet portlet;
		private FormPortletButton previewButton;
		private FormPortletButton cancelButton;
		private String originalHtml;
		private String originalCss;
		private boolean previewed;

		public HtmlFormPortlet(HtmlCustomPortlet portlet, PortletConfig config) {
			super(config);
			this.portlet = portlet;
			this.getFormPortletStyle().setLabelsWidth(15);
			addField(new FormPortletTitleField("HTML (Press alt-enter for preview)"));
			this.html = addField(new FormPortletTextAreaField("")).setValue(portlet.getHtml()).setHeight(500);
			this.html.setCssStyle("style.fontSize=12px");
			addField(new FormPortletTitleField("Css class"));
			this.css = addField(new FormPortletTextField("")).setValue(portlet.getCssClass()).setWidth(300);
			this.originalHtml = html.getValue();
			this.originalCss = css.getValue();
			cancelButton = addButton(new FormPortletButton("cancel"));
			previewButton = addButton(new FormPortletButton("preview html"));
			submitButton = addButton(new FormPortletButton("submit"));
			addFormPortletListener(this);
		}

		@Override
		public int getSuggestedHeight(PortletMetrics pm) {
			return 700;
		}
		@Override
		public int getSuggestedWidth(PortletMetrics pm) {
			return 850;
		}

		@Override
		public void onButtonPressed(FormPortlet portlet, FormPortletButton bttn) {
			if (bttn == submitButton) {
				this.portlet.setHtml(this.html.getValue(), false);
				this.portlet.setCssClass(this.css.getValue());
				close();
			} else if (bttn == previewButton) {
				this.portlet.setHtml(this.html.getValue());
				this.portlet.setCssClass(this.css.getValue());
				previewed = true;
			} else if (bttn == cancelButton) {
				if (previewed) {
					this.portlet.setHtml(this.originalHtml);
					this.portlet.setCssClass(this.originalCss);
				}
				close();
			}

		}

		@Override
		public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		}

		@Override
		public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
			if (keycode == 13 && mask != 0) {
				this.portlet.setHtml(this.html.getValue());
				this.portlet.setCssClass(this.css.getValue());
				previewed = true;
			}
		}
	}

	public void setOnUserClickEnabled(boolean enabled) {
		this.onUserClickEnabled = enabled;
	}
}
