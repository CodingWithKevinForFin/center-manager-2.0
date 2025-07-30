package com.f1.ami.web;

import java.util.Collection;
import java.util.Map;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.DesktopPortlet;
import com.f1.suite.web.portal.impl.DesktopPortlet.Window;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;

public class AmiWebDesktopWindowDefaultSettingsPortlet extends GridPortlet implements FormPortletListener {

	private final FormPortlet form;
	private final Window window;
	private final DesktopPortlet desktop;
	private final FormPortletButton submitButton = new FormPortletButton("Submit");
	private final FormPortletButton cancelButton = new FormPortletButton("Cancel");
	private final FormPortletToggleButtonsField<Boolean> defaultLocationToggle;
	private final FormPortletNumericRangeField defaultLeftField, defaultTopField, defaultWidthField, defaultHeightField;
	private final FormPortletToggleButtonsField<Boolean> defaultZIndexToggle;
	private final FormPortletNumericRangeField defaultZIndexField;
	private final FormPortletToggleButtonsField<String> defaultWindowStateToggle;

	public AmiWebDesktopWindowDefaultSettingsPortlet(PortletConfig config, Window window) {
		super(config);
		this.window = window;
		this.desktop = window.getDesktop();
		this.form = new FormPortlet(generateConfig());

		this.defaultLocationToggle = this.form.addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Use Default Location:"));
		this.defaultLocationToggle.addOption(true, "Yes");
		this.defaultLocationToggle.addOption(false, "No");
		boolean hasDefaultLocation = this.window.hasDefaultLocation();
		this.defaultLocationToggle.setValue(hasDefaultLocation);

		// option to minimize window upon logging in.
		this.defaultWindowStateToggle = this.form.addField(new FormPortletToggleButtonsField<String>(String.class, "Default Window State:"));
		this.defaultWindowStateToggle.addOption("min", "Minimize");
		this.defaultWindowStateToggle.addOption("max", "Maximize");
		this.defaultWindowStateToggle.addOption("flt", "Float");
		this.defaultWindowStateToggle.setValue(this.window.getCurrentState());
		if (SH.is(this.window.getDefaultState()))
			this.defaultWindowStateToggle.setValue(this.window.getDefaultState());
		else
			this.defaultWindowStateToggle.setValue(this.window.getCurrentState());

		int desktopWidth = this.desktop.getWidth();
		int desktopHeight = this.desktop.getHeight();
		this.defaultLeftField = this.form.addField(new FormPortletNumericRangeField("Default Left (px):", 0, desktopWidth, 0));
		this.defaultTopField = this.form.addField(new FormPortletNumericRangeField("Default Top (px):", 0, desktopHeight, 0));
		this.defaultWidthField = this.form.addField(new FormPortletNumericRangeField("Default Width (px):", 0, desktopWidth, 0));
		this.defaultHeightField = this.form.addField(new FormPortletNumericRangeField("Default Height (px):", 0, desktopHeight, 0));
		if (hasDefaultLocation) {
			this.defaultLeftField.setValue(this.window.getDefaultLeft());
			this.defaultTopField.setValue(this.window.getDefaultTop());
			this.defaultWidthField.setValue(this.window.getDefaultWidth());
			this.defaultHeightField.setValue(this.window.getDefaultHeight());
		} else {
			this.defaultLeftField.setValue(this.window.getLeft());
			this.defaultTopField.setValue(this.window.getTop());
			this.defaultWidthField.setValue(this.window.getWidth());
			this.defaultHeightField.setValue(this.window.getHeight());
			this.defaultLeftField.setDisabled(true);
			this.defaultTopField.setDisabled(true);
			this.defaultWidthField.setDisabled(true);
			this.defaultHeightField.setDisabled(true);
			this.defaultWindowStateToggle.setDisabled(true);
		}

		this.form.addField(new FormPortletTitleField(""));
		this.defaultZIndexToggle = this.form.addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Use Default Z-Index:"));
		this.defaultZIndexToggle.addOption(true, "Yes");
		this.defaultZIndexToggle.addOption(false, "No");
		boolean hasDefaultZIndex = this.window.hasDefaultZIndex();
		this.defaultZIndexToggle.setValue(hasDefaultZIndex);

		Collection<Window> windows = desktop.getWindows();
		int numWindows = windows.size();
		this.defaultZIndexField = this.form.addField(new FormPortletNumericRangeField("Default Z-Index:", 1, numWindows, 0));
		this.defaultZIndexField.setSliderHidden(true);
		this.defaultZIndexField.setValue(hasDefaultZIndex ? this.window.getDefaultZIndex() : this.window.getZindex());
		this.defaultZIndexField.setDisabled(!hasDefaultZIndex);
		FormPortletTitleField zIndexNoteField = this.form.addField(new FormPortletTitleField(
				"Note: Default z-indexes will be applied to all windows that do not already have a default z-index."));
		zIndexNoteField.setCssStyle("style.text-transform=none");

		addChild(this.form, 0, 0);
		this.form.addButton(this.submitButton);
		this.form.addButton(this.cancelButton);
		this.form.addFormPortletListener(this);
		setSuggestedSize(500, 500);
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.submitButton) {
			if (this.defaultLocationToggle.getValue()) {
				this.window.setDefaultLocation(Caster_Integer.PRIMITIVE.cast(this.defaultLeftField.getValue()), Caster_Integer.PRIMITIVE.cast(this.defaultTopField.getValue()),
						Caster_Integer.PRIMITIVE.cast(this.defaultWidthField.getValue()), Caster_Integer.PRIMITIVE.cast(this.defaultHeightField.getValue()));
				this.window.setDefaultStateToCurrent();
			} else {
				this.window.clearDefaultLocation();
				this.window.clearDefaultState();
			}
			if (this.defaultZIndexToggle.getValue()) {
				this.window.setDefaultZIndex(Caster_Integer.PRIMITIVE.cast(this.defaultZIndexField.getValue()));
			} else {
				for (Window w : this.desktop.getWindows()) {
					w.clearDefaultZIndex();
				}
			}
			if (SH.isntEmpty(this.defaultWindowStateToggle.getValue())) {
				String windowState = this.defaultWindowStateToggle.getValue();
				if (OH.eq(windowState, "min"))
					this.window.setDefaultState("min");
				else if (OH.eq(windowState, "flt"))
					this.window.setDefaultState("flt");
				else if (OH.eq(windowState, "max"))
					this.window.setDefaultState("max");
				else
					System.out.println("Invalid Window State in toggle button");
			}
		}
		close();
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.defaultLocationToggle) {
			boolean disabled = !this.defaultLocationToggle.getValue();
			this.defaultLeftField.setDisabled(disabled);
			this.defaultTopField.setDisabled(disabled);
			this.defaultWidthField.setDisabled(disabled);
			this.defaultHeightField.setDisabled(disabled);
			this.defaultWindowStateToggle.setDisabled(disabled);
		} else if (field == this.defaultZIndexToggle) {
			this.defaultZIndexField.setDisabled(!this.defaultZIndexToggle.getValue());
		}
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}

}
