package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.web.form.AmiWebQueryFormPortletUtils;
import com.f1.ami.web.style.AmiWebStyle;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyleImpl;
import com.f1.ami.web.style.AmiWebStyleManager;
import com.f1.ami.web.style.AmiWebStyleType;
import com.f1.ami.web.style.AmiWebStyledPortlet;
import com.f1.ami.web.style.AmiWebStyledPortletPeer;
import com.f1.ami.web.style.impl.AmiWebStyleOption;
import com.f1.ami.web.style.impl.AmiWebStyleOptionBoolean;
import com.f1.ami.web.style.impl.AmiWebStyleOptionChoices;
import com.f1.ami.web.style.impl.AmiWebStyleOptionRange;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.RootPortletDialog;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletColorField;
import com.f1.suite.web.portal.impl.form.FormPortletColorGradientField;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletExportImportManager;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.utils.CH;
import com.f1.utils.ColorHelper;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;

public class AmiWebEditStylePortlet extends GridPortlet
		implements FormPortletListener, FormPortletContextMenuFactory, FormPortletContextMenuListener, ConfirmDialogListener, FormPortletExportImportManager {

	//	private static final String IS_CSS = "IS_CSS";
	//	private static final String IS_COLORS = "IS_COLORS";
	//	private static final String IS_COLOR = "IS_COLOR";
	private static final String DIALOG_INHERITS_CASCADE = "DIALOG_INHERITS_CASCADE";
	final private FormPortlet styleForm;
	final private FormPortlet buttonsForm;
	private final FormPortletButton submit;
	private final FormPortletButton cancel;
	private final FormPortletButton exportImport;
	private AmiWebStyle stylePeer;
	private Map<Short, Object> origValues = new HashMap<Short, Object>();
	final private FormPortletSelectField<String> inheritFromField;
	final private AmiWebService service;
	private boolean hasButtonsForm;
	final private AmiWebStyleType styleType;
	private static final Logger log = LH.get();
	public static final String INHERIT_FROM_FIELD_NAME = "inheritFrom";
	public static final int BUTTONS_FORM_ROW_SIZE = 40;
	private List<FormPortletField> styleFields = new ArrayList<FormPortletField>();
	private boolean needsToWarnOverridesReset;

	public AmiWebEditStylePortlet(AmiWebStyle sp, PortletConfig config, String styleType) {
		super(config);
		this.service = AmiWebUtils.getService(getManager());
		this.styleType = this.service.getStyleManager().getStyleType(styleType);
		this.stylePeer = sp;
		this.styleForm = new FormPortlet(generateConfig());
		this.styleForm.setLabelsWidth(180);
		this.buttonsForm = new FormPortlet(generateConfig());

		this.inheritFromField = new FormPortletSelectField<String>(String.class, "Inherit From: ").setName(INHERIT_FROM_FIELD_NAME).setHasButton(true);
		this.styleForm.setMenuFactory(this);
		this.styleForm.addMenuListener(this);

		submit = this.buttonsForm.addButton(new FormPortletButton("Submit"));
		cancel = this.buttonsForm.addButton(new FormPortletButton("Cancel"));
		exportImport = this.buttonsForm.addButton(new FormPortletButton("Export/Import"));

		this.styleForm.addFormPortletListener(this);
		this.buttonsForm.addFormPortletListener(this);
		this.addChild(this.styleForm, 0, 0);
		this.addChild(this.buttonsForm, 0, 1);
		this.setRowSize(1, BUTTONS_FORM_ROW_SIZE);
		this.setSize(550, 500);
		this.hasButtonsForm = true;
		buildFields();
		if (sp != null) {
			Set<Short> t = sp.getOverrides(styleType);
			if (!t.isEmpty() || sp.isParentStyleOverride()) {
				this.needsToWarnOverridesReset = true;
				//				flagPendingAjax();
			}
			sp.resetOverrides();
			sp.resetParentStyleOverride();
		}
		setAmiWebStyle(sp);
		saveOriginalValues();
	}

	@Override
	public void drainJavascript() {
		if (this.needsToWarnOverridesReset) {
			this.needsToWarnOverridesReset = false;
			getManager().showAlert("Important Note: This style had override(s) which have been reset");
		}
		super.drainJavascript();
	}

	public AmiWebEditStylePortlet(AmiWebStyledPortletPeer sp, PortletConfig config) {
		this(sp, config, sp.getPortlet().getStyleType());
	}

	private void buildFields() {
		for (Entry<String, Map<String, AmiWebStyleOption>> group : this.styleType.getGroupLabels().entrySet()) {
			addTitleField(group.getKey());
			for (AmiWebStyleOption option : group.getValue().values()) {
				FormPortletField<?> f;
				switch (option.getType()) {
					case AmiWebStyleConsts.TYPE_COLOR_ARRAY:
						f = addColorsField(option.getKey(), option.getLabel());
						break;
					case AmiWebStyleConsts.TYPE_COLOR:
						f = addColorField(option.getKey(), option.getLabel());
						break;
					case AmiWebStyleConsts.TYPE_COLOR_GRADIENT:
						f = addColorGradientField(option.getKey(), option.getLabel());
						break;
					case AmiWebStyleConsts.TYPE_BOOLEAN: {
						AmiWebStyleOptionBoolean o = (AmiWebStyleOptionBoolean) option;
						if (o.getShowFalseFirst()) {
							FormPortletToggleButtonsField<Boolean> field = addFalseTrueToggleField(option.getKey(), option.getLabel(), o.getFalseLabel(), o.getTrueLabel());
							if (o.getFalseStyle() != null)
								field.setButtonStyleAtIndex(o.getFalseStyle(), 1);
							if (o.getTrueStyle() != null)
								field.setButtonStyleAtIndex(o.getTrueStyle(), 2);
							f = field;
						} else {
							FormPortletToggleButtonsField<Boolean> field = addTrueFalseToggleField(option.getKey(), option.getLabel(), o.getTrueLabel(), o.getFalseLabel());
							if (o.getTrueStyle() != null)
								field.setButtonStyleAtIndex(o.getTrueStyle(), 1);
							if (o.getFalseStyle() != null)
								field.setButtonStyleAtIndex(o.getFalseStyle(), 2);
							f = field;
						}
						break;
					}
					case AmiWebStyleConsts.TYPE_CSS_CLASS:
						f = addCssSelectField(option.getKey(), option.getLabel());
						break;
					case AmiWebStyleConsts.TYPE_ENUM: {
						AmiWebStyleOptionChoices o = (AmiWebStyleOptionChoices) option;
						if (o.isUseSelect()) {
							FormPortletSelectField field = addSelectField(o.getKey(), o.getLabel(), o.getValueType());
							for (Entry<Object, String> i : o.getOptionsToDisplayValue().getInnerKeyValueMap().entrySet())
								field.addOption(i.getKey(), i.getValue());
							f = field;
						} else {
							FormPortletToggleButtonsField field = addToggleField(o.getKey(), o.getLabel(), o.getValueType());
							if (o.getMinButtonWidth() >= 0)
								field.setMinButtonWidth(o.getMinButtonWidth());
							for (Entry<Object, String> i : o.getOptionsToDisplayValue().getInnerKeyValueMap().entrySet())
								field.addOption(i.getKey(), i.getValue());
							f = field;
						}
						break;
					}
					case AmiWebStyleConsts.TYPE_FONT:
						f = addFontField(option.getKey(), option.getLabel());
						break;
					case AmiWebStyleConsts.TYPE_NUMBER:
						AmiWebStyleOptionRange o = (AmiWebStyleOptionRange) option;
						f = addRangeField(option.getKey(), option.getLabel(), o.getMin(), o.getMax());
						break;
					default:
						throw new RuntimeException("Unknown type: " + option.getType());
				}
				if (option.getWidth() >= 0)
					f.setWidth(option.getWidth());
				if (option.getHeight() >= 0)
					f.setHeight(option.getHeight());
				f.setHelp(option.getVarname());
				f.setCorrelationData(option);
				styleFields.add(f);
			}
		}
	}

	public AmiWebEditStylePortlet hideCloseButtons(boolean hide) {
		this.buttonsForm.clearButtons();
		if (!hide) {
			this.buttonsForm.addButton(this.submit);
			this.buttonsForm.addButton(this.cancel);
		}
		this.buttonsForm.addButton(this.exportImport);
		return this;
	}
	public AmiWebEditStylePortlet hideButtonsForm(boolean hide) {
		this.hasButtonsForm = !hide;
		if (hide) {
			this.removeChild(this.buttonsForm.getPortletId());
			this.setRowSize(1, 0);
		} else {
			this.addChild(this.buttonsForm, 0, 1);
			this.setRowSize(1, BUTTONS_FORM_ROW_SIZE);
		}
		return this;
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.submit)
			close();
		else if (button == this.cancel) {
			for (FormPortletField<?> f : this.styleFields) {
				AmiWebStyleOption option = getStyleOption(f);
				Object o = getOrigValue(option.getKey());
				if (isColorsField(f))
					o = SH.isnt(o) ? null : CH.l(SH.trimStrings(SH.split(",", (String) o)));
				this.stylePeer.putValue(getStyleType(), option.getKey(), o);
			}
			close();
		} else if (button == this.exportImport)
			showExportImport();
	}

	private boolean isListType(String fieldName) {
		return fieldName.endsWith("Cls"); //TODO: should ask stylePeer (not hardcode)
	}

	public void showExportImport() {
		getManager().showDialog("Export/Import", new FormExportPortlet(this.styleForm, this, true));
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field instanceof FormPortletTextAreaField) {
			String s = ((FormPortletTextAreaField) field).getValue();
			if (SH.is(s)) {
				for (String c : SH.split(',', s)) {
					if (ColorHelper.parseColorNoThrow(c) == null)
						return;
				}
			}
		}
		if (field == this.inheritFromField) {
			String sid = this.inheritFromField.getValue();
			if (OH.ne(this.stylePeer.getParentStyle(), sid)) {
				if (this.stylePeer instanceof AmiWebStyledPortletPeer && ((AmiWebStyledPortletPeer) this.stylePeer).getPortlet() != null
						&& ((AmiWebStyledPortletPeer) this.stylePeer).getPortlet() instanceof AmiWebAbstractContainerPortlet) {
					ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(this.generateConfig(), "Do you want to cascade inherits to the child panels?",
							ConfirmDialogPortlet.TYPE_YES_NO).setCallback(DIALOG_INHERITS_CASCADE);
					getManager().showDialog("Inherit Styles", cdp);
					cdp.addDialogListener(this);
				} else {
					applyInheritsStyle(this.stylePeer, sid, false);
				}
			}

			for (int i = 0; i < this.styleForm.getFieldsCount(); i++) {
				FormPortletField f = this.styleForm.getFieldAt(i);
				try {
					if (isCssField(f)) {
						String val = (String) f.getValue();
						updateCssOptions((FormPortletSelectField<String>) f);
						if (!((FormPortletSelectField) f).setValueNoThrow(val))
							((FormPortletSelectField) f).addOption(val, val + " (INVALID)");
						f.setValue(val);
					}

				} catch (Exception e) {
					LH.warning(log, "Error with ", f.getName(), e);
				}
			}
			updateValues();
		} else if (field.getCorrelationData() instanceof AmiWebStyleOption) {
			AmiWebStyleOption option = getStyleOption(field);
			if (field instanceof FormPortletColorField) {
				FormPortletColorField cf = (FormPortletColorField) field;
				String o;
				if (cf.getDisplayText() != null) {
					o = cf.getDisplayText();
				} else
					o = cf.getValue();
				this.stylePeer.putValue(getStyleType(), option.getKey(), o);
			} else {
				Object o = field.getValue();
				if (isColorsField(field))
					o = SH.isnt(o) ? null : CH.l(SH.trimStrings(SH.split(",", (String) o)));
				this.stylePeer.putValue(getStyleType(), option.getKey(), o);
			}
		}
	}
	public void putOrigValue(Short key, Object value) {
		this.origValues.put(key, value);
	}
	public Object getOrigValue(Short key) {
		return this.origValues.get(key);
	}
	public void saveOriginalValues() {
		for (FormPortletField<?> f : this.styleFields) {
			AmiWebStyleOption option = getStyleOption(f);
			if (f instanceof FormPortletTextAreaField) {
				String val = (String) f.getValue();
				putOrigValue(option.getKey(), SH.is(val) ? val : null);
			} else {
				putOrigValue(option.getKey(), f.getValue());
			}
		}
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}

	public void setAmiWebStyle(AmiWebStyle style) {
		this.stylePeer = style;
		this.styleForm.removeFieldNoThrow(this.inheritFromField);
		if (style != null && style.getParentStyle() != null && (hasButtonsForm || style instanceof AmiWebStyledPortletPeer)) {
			this.styleForm.addField(this.inheritFromField, 0);
			this.inheritFromField.clearOptions();
			for (AmiWebStyle i : this.service.getStyleManager().getAllStyles()) {
				if (style == i) // prevent circular reference
					continue;
				this.inheritFromField.addOption(i.getId(), i.getLabel());
			}
			String id = style.getParentStyle();
			if (SH.isnt(id))
				this.inheritFromField.addOption("", "");
			this.inheritFromField.setValueNoThrow(id);
		}
		updateValues();
	}

	private void updateValues() {
		AmiWebStyle style = this.stylePeer;
		if (style == null)
			return;
		for (int i = 0; i < this.styleForm.getFieldsCount(); i++) {
			FormPortletField f = this.styleForm.getFieldAt(i);
			try {
				if (isCssField(f))
					updateCssOptions((FormPortletSelectField<String>) f);
				if (f.getCorrelationData() instanceof AmiWebStyleOption) {
					AmiWebStyleOption option = getStyleOption(f);
					Object val = style.getValue(getStyleType(), option.getKey());
					if (f instanceof FormPortletSelectField<?>) {
						if (!((FormPortletSelectField) f).setValueNoThrow(val))
							((FormPortletSelectField) f).addOption(val, val + " (INVALID)");
						f.setValue(val);
					} else if (isColorField(f)) {
						FormPortletColorField cf = (FormPortletColorField) f;
						cf.setNoColorText("Inherited");
						String s = (String) val;
						if (SH.startsWith(s, "$"))
							cf.setValue((String) style.getVarValues().get(s), s);
						else
							f.setValue(s);
					} else if (isColorsField(f)) {
						if (val != null)
							val = SH.join(',', (List<String>) val);
						f.setValue(val);
					} else {
						val = f.getCaster().cast(val);
						f.setValue(val);
					}
				}
			} catch (Exception e) {
				LH.warning(log, "Error with ", f.getName(), e);
			}
		}
		setStyleFieldsDisabled(getStylePeer().getReadOnly());
	}

	private boolean isColorsField(FormPortletField f) {
		AmiWebStyleOption option = getStyleOption(f);
		return option != null && option.getType() == AmiWebStyleConsts.TYPE_COLOR_ARRAY;
	}

	private AmiWebStyleOption getStyleOption(FormPortletField f) {
		return (AmiWebStyleOption) f.getCorrelationData();
	}
	private boolean isCssField(FormPortletField<?> field) {
		AmiWebStyleOption option = (AmiWebStyleOption) field.getCorrelationData();
		return option != null && option.getType() == AmiWebStyleConsts.TYPE_CSS_CLASS;
	}
	public AmiWebStyle getStylePeer() {
		return stylePeer;
	}

	public FormPortlet getStyleForm() {
		return styleForm;
	}
	public FormPortlet getButtonsForm() {
		return buttonsForm;
	}
	public void setStyleFieldsDisabled(Boolean disabled) {
		for (FormPortletField<?> f : this.styleForm.getFormFields())
			if (!(f instanceof FormPortletTitleField))
				f.setDisabled(disabled);
	}

	protected FormPortletButton getCancelButton() {
		return cancel;
	}

	@Override
	public BasicWebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		BasicWebMenu r = new BasicWebMenu();
		if (isCssField(field)) {
			r.add(new BasicWebMenuLink("Show CSS Class", true, "show_css"));
		} else if (isColorsField(field)) {
			AmiWebMenuUtils.createColorsMenu(r, false, this.stylePeer);
			AmiWebMenuUtils.createOperatorsMenu(r, this.getService(), "");
		} else if (isColorField(field)) {
			r = (BasicWebMenu) AmiWebMenuUtils.createColorFieldMenu(this.stylePeer, (FormPortletColorField) field);
			if (r.getChildren().isEmpty())
				r.addChild(new BasicWebMenuLink("(no custom variables defined)", false, ""));
		} else if (field == this.inheritFromField)
			r.add(new BasicWebMenuLink("Jump to this style... ", true, "edit_style"));
		return r;
	}

	private boolean isColorField(FormPortletField<?> f) {
		AmiWebStyleOption option = (AmiWebStyleOption) f.getCorrelationData();
		if (option != null)
			switch (option.getType()) {
				case AmiWebStyleConsts.TYPE_COLOR:
					//TODO:case AmiWebStyleConsts.TYPE_COLOR_GRADIENT:
					return true;
			}
		return false;
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		AmiWebStyleOption option = getStyleOption(node);
		if (isCssField(node)) {
			String id = this.stylePeer.getId();
			String css = AmiWebUtils.getService(getManager()).getCustomCssManager().getCssForClassName(id, Caster_String.INSTANCE.cast(node.getValue()));
			if (css == null)
				css = AmiWebUtils.getService(getManager()).getCustomCssManager().getCssForClassName(id,
						Caster_String.INSTANCE.cast(getStylePeer().getValue(getStyleType(), option.getKey())));
			if (css != null) {
				AmiWebQueryFormPortletUtils.showCustomCssDialog(css, getManager());
			} else {
				getManager().showAlert("CSS class not set for this field type.");
			}
		} else if (isColorsField(node)) {
			AmiWebMenuUtils.processContextMenuAction(getService(), action, node);
		} else if (isColorField(node)) {
			AmiWebMenuUtils.processContextMenuAction(getService(), action, node);
		} else if ("edit_style".equals(action)) {
			final AmiWebStyleManager sm = service.getStyleManager();
			final AmiWebStyle style = sm.getStyleById(this.inheritFromField.getValue());
			final AmiWebStyleType t = this.styleType;
			RootPortletDialog dialog = getManager().showDialog(t.getUserLabel() + " - " + style.getLabel(), new AmiWebEditStylePortlet(style, generateConfig(), t.getName()),
					getWidth(), getHeight());
			dialog.setShadeOutside(false);
			dialog.setPosition(PortletHelper.getAbsoluteLeft(this) + 20, PortletHelper.getAbsoluteTop(this) + 30);
		}
	}
	protected AmiWebService getService() {
		return this.service;

	}
	protected FormPortletSelectField<String> addFontField(short propertyCode, String name) {
		String propertyName = toName(propertyCode);
		FormPortletSelectField<String> r = this.getStyleForm().addField(new FormPortletSelectField<String>(String.class, name)).setName(propertyName);
		r.addDefaultOption();
		for (String font : AmiWebUtils.getFonts(this.getService())) {
			String uppercaseFont = SH.uppercaseFirstChar(font);
			r.addOption(font, uppercaseFont);
		}
		return r;
	}
	protected FormPortletNumericRangeField addRangeField(short propertyCode, String name, int min, int max) {
		String propertyName = toName(propertyCode);
		return getStyleForm().addField(new FormPortletNumericRangeField(name, min, max, 0)).setNullable(true).setName(propertyName).setWidth(200);
	}

	private String toName(short propertyCode) {
		String propertyName = SH.toString(propertyCode);
		return propertyName;
	}
	protected FormPortletColorField addColorField(short propertyCode, String name) {
		String propertyName = toName(propertyCode);
		FormPortletColorField field = new FormPortletColorField(name);
		//		field.setCorrelationData(IS_COLOR);
		field.setHasButton(true);
		return getStyleForm().addField(field).setName(propertyName).setAlphaEnabled(true);
	}
	protected FormPortletColorGradientField addColorGradientField(short propertyCode, String name) {
		String propertyName = toName(propertyCode);
		return getStyleForm().addField(new FormPortletColorGradientField(name)).setName(propertyName).setAlphaEnabled(false);
	}
	protected FormPortletTextAreaField addColorsField(short propertyCode, String name) {
		String propertyName = toName(propertyCode);
		FormPortletTextAreaField r = getStyleForm().addField(new FormPortletTextAreaField(name)).setName(propertyName);
		//		r.setCorrelationData(IS_COLORS);
		return r;
	}
	protected <T> FormPortletToggleButtonsField<T> addToggleField(short propertyCode, String name, Class<T> type, T k1, String v1, T k2, String v2, T k3, String v3) {
		String propertyName = toName(propertyCode);
		return getStyleForm().addField(new FormPortletToggleButtonsField<T>(type, name)).setName(propertyName).addDefaultOption().addOption(k1, v1).addOption(k2, v2).addOption(k3,
				v3);
	}
	protected <T> FormPortletToggleButtonsField<T> addToggleField(short propertyCode, String name, Class<T> type, T k1, String v1, T k2, String v2) {
		String propertyName = toName(propertyCode);
		return getStyleForm().addField(new FormPortletToggleButtonsField<T>(type, name)).setName(propertyName).addDefaultOption().addOption(k1, v1).addOption(k2, v2);
	}
	protected <T> FormPortletToggleButtonsField<T> addToggleField(short propertyCode, String name, Class<T> type) {
		String propertyName = toName(propertyCode);
		return getStyleForm().addField(new FormPortletToggleButtonsField<T>(type, name)).setName(propertyName).addDefaultOption();
	}
	protected FormPortletToggleButtonsField<Boolean> addTrueFalseToggleField(short propertyCode, String name, String truu, String falss) {
		String propertyName = toName(propertyCode);
		return getStyleForm().addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, name)).setName(propertyName).addDefaultOption().addOption(Boolean.TRUE, truu)
				.addOption(Boolean.FALSE, falss);
	}
	protected FormPortletToggleButtonsField<Boolean> addFalseTrueToggleField(short propertyCode, String name, String falss, String truu) {
		String propertyName = toName(propertyCode);
		return getStyleForm().addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, name)).setName(propertyName).addDefaultOption().addOption(Boolean.FALSE, falss)
				.addOption(Boolean.TRUE, truu);
	}
	protected <T> FormPortletSelectField<T> addSelectField(short propertyCode, String name, Class<T> type) {
		String propertyName = toName(propertyCode);
		return getStyleForm().addField(new FormPortletSelectField<T>(type, name)).setName(propertyName).addDefaultOption();
	}
	protected FormPortletSelectField<String> addCssSelectField(short propertyCode, String name) {
		String propertyName = toName(propertyCode);
		FormPortletSelectField<String> f = getStyleForm().addField(new FormPortletSelectField<String>(String.class, name)).setName(propertyName);
		f.setHasButton(true);
		//		f.setCorrelationData(IS_CSS);
		updateCssOptions(f);
		return f;

	}
	private void updateCssOptions(FormPortletSelectField<String> f) {
		f.clearOptions();
		f.addDefaultOption();
		f.addOption("", "<none>");
		Set<String> classNames = this.stylePeer == null ? null : AmiWebUtils.getService(getManager()).getCustomCssManager().getClassNames(this.stylePeer.getParentStyle());
		if (classNames != null)
			for (String className : classNames)
				f.addOption(className, className);
	}

	protected void addTitleField(String title) {
		getStyleForm().addField(new FormPortletTitleField(title));
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if (DIALOG_INHERITS_CASCADE.equals(source.getCallback())) {
			this.applyInheritsStyle(this.stylePeer, this.inheritFromField.getValue(), ConfirmDialogPortlet.ID_YES.equals(id));
			return true;
		}
		return false;
	}
	private void applyInheritsStyle(AmiWebStyle stylePeer, String inheritsFrom, boolean cascadeIfContainerPortlet) {
		// clicking the + button brings up AmiWebStyleImpl
		// clicking green button to style brings up AmiWebStyledPortletPeer
		if (stylePeer instanceof AmiWebStyleImpl && getService().getStyleManager().hasCircRef(inheritsFrom, stylePeer.getParentStyle())) {
			getService().getPortletManager().showAlert("Circular reference between <b>" + stylePeer.getParentStyle() + "</b> and <b>" + inheritsFrom + "</b>");
			return;
		}

		if (stylePeer.getParentStyle() != null)
			stylePeer.setParentStyle(inheritsFrom);

		if (!(stylePeer instanceof AmiWebStyledPortletPeer))
			return;

		AmiWebStyledPortletPeer stylePortletPeer = (AmiWebStyledPortletPeer) stylePeer;
		AmiWebStyledPortlet portlet = stylePortletPeer.getPortlet();

		if (portlet == null || !(portlet instanceof AmiWebAbstractContainerPortlet))
			return;
		if (cascadeIfContainerPortlet == false)
			return;

		AmiWebAbstractContainerPortlet container = (AmiWebAbstractContainerPortlet) portlet;
		List<AmiWebAliasPortlet> childs = CH.l(container.getAmiChildren());

		for (int i = 0; i < childs.size(); i++) {
			AmiWebAliasPortlet child = childs.get(i);

			if (!(child instanceof AmiWebStyledPortlet))
				continue;
			AmiWebStyledPortlet childStyledPortlet = (AmiWebStyledPortlet) child;
			if (childStyledPortlet.getStylePeer() != null)
				applyInheritsStyle(childStyledPortlet.getStylePeer(), inheritsFrom, cascadeIfContainerPortlet);

		}
	}
	public void addVisualizationFields() {
		addTitleField("Title");
		addRangeField(AmiWebStyleConsts.CODE_TITLE_PNL_FONT_SZ, "Font Size:", 0, 40);
		addToggleField(AmiWebStyleConsts.CODE_TITLE_PNL_ALIGN, "Alignment:", String.class, "left", "Left", "center", "Center", "right", "Right");
		addColorField(AmiWebStyleConsts.CODE_TITLE_PNL_FONT_CL, "Title Color:");

		addTitleField("Visualization Padding");
		addRangeField(AmiWebStyleConsts.CODE_PD_LF_PX, "Left:", 0, 100);
		addRangeField(AmiWebStyleConsts.CODE_PD_RT_PX, "Right:", 0, 100);
		addRangeField(AmiWebStyleConsts.CODE_PD_TP_PX, "Top:", 0, 100);
		addRangeField(AmiWebStyleConsts.CODE_PD_BTM_PX, "Bottom:", 0, 100);

		addRangeField(AmiWebStyleConsts.CODE_PD_RAD_TP_LF_PX, "Top-Left Radius (px):", 0, 30);
		addRangeField(AmiWebStyleConsts.CODE_PD_RAD_TP_RT_PX, "Top-Right Radius (px):", 0, 30);
		addRangeField(AmiWebStyleConsts.CODE_PD_RAD_BTM_LF_PX, "Bottom-Left Radius (px):", 0, 30);
		addRangeField(AmiWebStyleConsts.CODE_PD_RAD_BTM_RT_PX, "Bottom-Right Radius (px):", 0, 30);
		addColorField(AmiWebStyleConsts.CODE_PD_CL, "Color:");

		addTitleField("Visualization Shadow");
		addRangeField(AmiWebStyleConsts.CODE_PD_SHADOW_HZ_PX, "Horizontal:", -20, 20);
		addRangeField(AmiWebStyleConsts.CODE_PD_SHADOW_VT_PX, "Vertical:", -20, 20);
		addRangeField(AmiWebStyleConsts.CODE_PD_SHADOW_SZ_PX, "Size:", 0, 100);
		addColorField(AmiWebStyleConsts.CODE_PD_SHADOW_CL, "Color:");

		addTitleField("Visualization Border");
		addRangeField(AmiWebStyleConsts.CODE_PD_BDR_SZ_PX, "Size:", 0, 25);
		addColorField(AmiWebStyleConsts.CODE_PD_BDR_CL, "Color:");
	}

	public void addScrollbarFields() {
		addTitleField("Scrollbar Options");
		addRangeField(AmiWebStyleConsts.CODE_SCROLL_WD, "Width:", 5, 50);
		addColorField(AmiWebStyleConsts.CODE_SCROLL_GRIP_CL, "Grip Color:");
		addColorField(AmiWebStyleConsts.CODE_SCROLL_TRACK_CL, "Track Color:");
		addColorField(AmiWebStyleConsts.CODE_SCROLL_BTN_CL, "Button Color:");
		addColorField(AmiWebStyleConsts.CODE_SCROLL_ICONS_CL, "Icons Color:");
		addColorField(AmiWebStyleConsts.CODE_SCROLL_BDR_CL, "Border Color:");
	}

	public final String getStyleType() {
		return this.styleType.getName();
	}

	@Override
	public void importFromText(FormPortlet target, Map<String, Object> values, StringBuilder errorSink) {
		if (this.stylePeer.getReadOnly())
			return;
		OH.assertEqIdentity(this.styleForm, target);
		for (Entry<String, Object> i : values.entrySet()) {
			String styleKey = i.getKey();
			Object styleValue = i.getValue();
			if (styleKey.contains("calenday")) {
				// backwards fix for typo
				styleKey = styleKey.replace("calenday", "calendar");
			}
			if (this.stylePeer instanceof AmiWebStyledPortletPeer && INHERIT_FROM_FIELD_NAME.equals(styleKey)) {
				this.inheritFromField.setValue((String) styleValue);
				target.fireFieldValueChangedTolisteners(this.inheritFromField, Collections.EMPTY_MAP);
				continue;
			}
			AmiWebStyleOption option = this.service.getStyleManager().getOption(this.styleType.getName(), styleKey);
			if (option != null) {
				FormPortletField field = target.getFieldByName(toName(option.getKey()));
				if (styleValue == null) {
					field.setValueNoThrow(null);
					target.fireFieldValueChangedTolisteners(field, Collections.EMPTY_MAP);
				} else {
					Object value = option.toInternalStorageValue(service, styleValue);
					if (value != null) {
						// handle seriesCl
						if (value instanceof List<?>) {
							StringBuilder sb = new StringBuilder();
							for (Object o : (List<?>) value) {
								SH.appendWithDelim(',', o.toString(), sb);
							}
							field.setValueNoThrow(sb.toString()); // don't throw e.g. select field
						} else
							field.setValueNoThrow(value); // same as above
						target.fireFieldValueChangedTolisteners(field, Collections.EMPTY_MAP);
					} else
						errorSink.append("Invalid value for " + option.getSaveKey() + ": " + styleValue + "<BR>");
				}
			}

		}
	}

	@Override
	public Map<String, Object> exportToText(FormPortlet target) {
		OH.assertEqIdentity(this.styleForm, target);
		Map<String, Object> r = new LinkedHashMap<String, Object>();
		for (FormPortletField<?> i : target.getFormFields()) {
			AmiWebStyleOption option = (AmiWebStyleOption) i.getCorrelationData();
			String name = i.getName();
			if ("".equals(name))
				continue;
			Object value = i.getValue();
			if (INHERIT_FROM_FIELD_NAME.equals(name)) {
				r.put(INHERIT_FROM_FIELD_NAME, value);
				continue;
			}
			if (isColorsField(i))
				value = option.toInternalStorageValue(service, (String) value);
			else if (isColorField(i)) {
				FormPortletColorField cf = (FormPortletColorField) i;
				if (cf.getDisplayText() != null)
					value = cf.getDisplayText();
			}
			value = option.toExportValue(service, value);
			r.put(option.getSaveKey(), value);
		}
		return r;
	}

}
