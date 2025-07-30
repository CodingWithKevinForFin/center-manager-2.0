package com.f1.ami.web;

import java.util.Map;
import java.util.Set;

import com.f1.ami.web.form.AmiWebEditableFormPortletManager;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.utils.structs.MapInMap;

public class AmiWebSetFieldPositionPortlet extends FormPortlet implements FormPortletListener {

	private FormPortletNumericRangeField leftPosPxField;
	private final FormPortletNumericRangeField topPosPxField;
	private final FormPortletNumericRangeField widthPxField;
	private final FormPortletNumericRangeField heightPxField;
	private final Set<QueryField<?>> selectedFields;
	private final QueryField<?> clickedField;

	// Save original parameters

	private final MapInMap<QueryField<?>, Byte, Number> fields2OrigParams;

	private final static byte PARAM_REALIZED_LEFT_PX = 0;
	private final static byte PARAM_REALIZED_TOP_PX = 1;
	private final static byte PARAM_REALIZED_WIDTH_PX = 2;
	private final static byte PARAM_REALIZED_HEIGHT_PX = 3;

	private final FormPortletButton submitButton;
	private final FormPortletButton cancelButton;
	private final AmiWebEditableFormPortletManager editableFormManager;

	public AmiWebSetFieldPositionPortlet(PortletConfig config, AmiWebQueryFormPortlet queryFormPortlet, Set<QueryField<?>> selectedFields, QueryField<?> clickedField) {
		super(config);
		this.editableFormManager = queryFormPortlet.getEditableForm().getEditableManager();
		this.selectedFields = selectedFields;
		this.clickedField = clickedField;
		int queryFormWidth = queryFormPortlet.getWidth();
		int queryFormHeight = queryFormPortlet.getHeight();
		this.leftPosPxField = addField(new FormPortletNumericRangeField("Left Position (px):", 0, queryFormWidth, 0)).setNullable(true);
		this.topPosPxField = addField(new FormPortletNumericRangeField("Top Position (px):", 0, queryFormHeight, 0)).setNullable(true);
		this.widthPxField = addField(new FormPortletNumericRangeField("Width (px):", 0, queryFormWidth, 0)).setNullable(true);
		this.heightPxField = addField(new FormPortletNumericRangeField("Height (px):", 0, queryFormHeight, 0)).setNullable(true);

		// Save original parameters
		this.fields2OrigParams = new MapInMap<QueryField<?>, Byte, Number>();
		for (QueryField<?> q : selectedFields) {
			this.fields2OrigParams.putMulti(q, PARAM_REALIZED_LEFT_PX, q.getRealizedLeftPosPx());
			this.fields2OrigParams.putMulti(q, PARAM_REALIZED_TOP_PX, q.getRealizedTopPosPx());
			this.fields2OrigParams.putMulti(q, PARAM_REALIZED_WIDTH_PX, q.getRealizedWidthPx());
			this.fields2OrigParams.putMulti(q, PARAM_REALIZED_HEIGHT_PX, q.getRealizedHeightPx());
		}

		this.submitButton = addButton(new FormPortletButton("Submit"));
		this.cancelButton = addButton(new FormPortletButton("Cancel"));

		addFormPortletListener(this);
		initializeSliders();
	}
	private void initializeSliders() {
		this.leftPosPxField.setValue(this.clickedField.getRealizedLeftPosPx());
		this.topPosPxField.setValue(this.clickedField.getRealizedTopPosPx());
		this.widthPxField.setValue(this.clickedField.getRealizedWidthPx());
		this.heightPxField.setValue(this.clickedField.getRealizedHeightPx());
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.cancelButton) {
			for (QueryField<?> q : this.selectedFields) {
				q.setRealizedLeftPosPx((Integer) this.fields2OrigParams.getMulti(q, PARAM_REALIZED_LEFT_PX));
				q.setRealizedTopPosPx((Integer) this.fields2OrigParams.getMulti(q, PARAM_REALIZED_TOP_PX));
				q.setRealizedWidthPx((Integer) this.fields2OrigParams.getMulti(q, PARAM_REALIZED_WIDTH_PX));
				q.setRealizedHeightPx((Integer) this.fields2OrigParams.getMulti(q, PARAM_REALIZED_HEIGHT_PX));
			}
		}
		close();
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.leftPosPxField) {
			if (this.leftPosPxField.getValue() == null)
				for (QueryField<?> q : this.selectedFields)
					q.setRealizedLeftPosPx((Integer) this.fields2OrigParams.getMulti(q, PARAM_REALIZED_LEFT_PX));
			else
				for (QueryField<?> q : this.selectedFields)
					q.setRealizedLeftPosPx(this.leftPosPxField.getIntValue());
		} else if (field == this.topPosPxField) {
			if (this.topPosPxField.getValue() == null)
				for (QueryField<?> q : this.selectedFields)
					q.setRealizedTopPosPx((Integer) this.fields2OrigParams.getMulti(q, PARAM_REALIZED_TOP_PX));
			else
				for (QueryField<?> q : this.selectedFields)
					q.setRealizedTopPosPx(this.topPosPxField.getIntValue());
		} else if (field == this.widthPxField) {
			if (this.widthPxField.getValue() == null)
				for (QueryField<?> q : this.selectedFields)
					q.setRealizedWidthPx((Integer) this.fields2OrigParams.getMulti(q, PARAM_REALIZED_WIDTH_PX));
			else
				for (QueryField<?> q : this.selectedFields)
					q.setRealizedWidthPx(this.widthPxField.getIntValue());
		} else if (field == this.heightPxField) {
			if (this.heightPxField.getValue() == null)
				for (QueryField<?> q : this.selectedFields)
					q.setRealizedHeightPx((Integer) this.fields2OrigParams.getMulti(q, PARAM_REALIZED_HEIGHT_PX));
			else
				for (QueryField<?> q : this.selectedFields)
					q.setRealizedHeightPx(this.heightPxField.getIntValue());
		}
		for (QueryField<?> q : this.selectedFields)
			this.editableFormManager.updateRectangle(q);
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}

}
