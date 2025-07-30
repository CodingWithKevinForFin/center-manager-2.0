package com.f1.ami.web.charts;

import java.util.Map;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.utils.OH;

public class AmiWebChartLayerExportPortlet<T extends AmiWebChartSeries> extends GridPortlet implements FormPortletListener {

	final private AmiWebChartRenderingLayer<?> target;
	final private FormPortlet form;
	final private FormPortletButton okButton;
	final private FormPortletButton cancelButton;
	final private FormPortletTextAreaField textAreaField;
	final private String origText;
	final private AmiWebChartEditLayerPortlet<?> editor;

	public AmiWebChartLayerExportPortlet(PortletConfig config, AmiWebChartRenderingLayer<T> target, AmiWebChartEditLayerPortlet<?> portlet) {
		super(config);
		this.target = target;
		this.editor = portlet;
		this.form = addChild(new FormPortlet(generateConfig()), 0, 0);
		this.okButton = this.form.addButton(new FormPortletButton("Apply"));
		this.cancelButton = this.form.addButton(new FormPortletButton("Cancel"));
		this.form.addFormPortletListener(this);
		this.textAreaField = this.form.addField(new FormPortletTextAreaField(""));
		this.textAreaField.setValue(this.origText = this.target.exportToText());
		this.textAreaField.setSelection(0, this.origText.length());
		this.textAreaField.setTopPosPx(15);
		this.textAreaField.setBottomPosPx(55);
		this.textAreaField.setLeftPosPx(15);
		this.textAreaField.setRightPosPx(15);
		this.form.getFormPortletStyle().setLabelsWidth(0);
		this.okButton.setEnabled(true);
		setSuggestedSize(400, 500);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.okButton) {
			if (OH.ne(this.textAreaField.getValue(), this.origText)) {
				StringBuilder errorSink = new StringBuilder();
				this.target.importFromText(this.textAreaField.getValue(), errorSink);
				this.editor.getActiveEditor().updateFields();
				this.editor.getEditRenderingLayerPortlet().getEditStylePortlet().setAmiWebStyle(this.target.getStylePeer());
				this.editor.getEditRenderingLayerPortlet().updateDmModelButton();
				this.editor.getActiveEditor().preview();
				if (errorSink.length() > 0) {
					getManager().showAlert(errorSink.toString());
				} else
					close();
			} else
				close();
		} else if (button == this.cancelButton)
			close();
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {

	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}

}
