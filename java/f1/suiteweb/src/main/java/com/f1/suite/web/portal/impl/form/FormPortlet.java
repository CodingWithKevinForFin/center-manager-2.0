/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.portal.impl.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuLink;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.peripheral.MouseEvent;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.suite.web.portal.impl.AbstractPortlet;
import com.f1.suite.web.portal.impl.BasicPortletSchema;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.IndexedList;

public class FormPortlet extends AbstractPortlet implements Form {

	public static final int MASK_CLIP = 1;
	public static final int MASK_LAYOUT = 2;
	public static final int MASK_SCROLL_STYLE = 4;
	public static final int MASK_BUTTONS = 8;
	public static final int MASK_POSITIONS = 16;
	public static final int MASK_FIELDS = 32;
	public static final int MASK_STYLE = 64;
	public static final int MASK_REBUILD = 128;

	private static final Logger log = LH.get();

	public static final PortletSchema<FormPortlet> SCHEMA = new BasicPortletSchema<FormPortlet>("Form", "FormPortlet", FormPortlet.class, true, true);

	private static final Comparator<FormPortletField<?>> TAB_POSITION_ORDER = new Comparator<FormPortletField<?>>() {

		@Override
		public int compare(FormPortletField<?> o1, FormPortletField<?> o2) {
			final int n = OH.compare(o1.getCalculatedTopPosPx(), o2.getCalculatedTopPosPx());
			return n != 0 ? n : OH.compare(o1.getCalculatedLeftPosPx(), o2.getCalculatedLeftPosPx());
		}
	};
	private List<FormPortletListener> formListeners = new ArrayList<FormPortletListener>();
	private List<FormPortletContextMenuListener> menuListeners = new ArrayList<FormPortletContextMenuListener>();
	private List<FormPortletContextMenuForButtonListener> menuListenersForButtons = new ArrayList<FormPortletContextMenuForButtonListener>();
	private List<FormPortletFieldTitleListener> titleListeners = new ArrayList<FormPortletFieldTitleListener>();
	private List<FormPortletCustomCallbackListener> customCallbackListeners = new ArrayList<FormPortletCustomCallbackListener>();
	private FormPortletContextMenuFactory menuFactory;
	private FormPortletContextMenuForButtonFactory menuFactoryForButton;

	private IndexedList<String, FormPortletField<?>> fields = new BasicIndexedList<String, FormPortletField<?>>();
	private IndexedList<String, FormPortletButton> buttons = new BasicIndexedList<String, FormPortletButton>();
	private HasherSet<String> pendingFieldConfigChanges = new HasherSet<String>();
	private HasherMap<String, FormPortletField<?>> fieldsInFrontend = new HasherMap<String, FormPortletField<?>>();
	private FormPortletField<?> focusField;
	private FormPortletField<?> refocusField;//If the form is unfocused and refocused then refocus to this field
	private String htmlLayout;
	private int nextId = 1;
	private int clipTop;
	private int clipLeft;
	private PortletStyleManager_Form styleManager;
	final private FormPortletStyle formPortletStyle;
	final private FormPortletJs formPortletJs;
	private HashMap<String, FormPortletRadioButtonField> group2LastChecked = new HashMap<String, FormPortletRadioButtonField>(); // {"formPortletId_groupName" : field}

	private List<FormPortletField<?>> fieldsOrderByTab;
	private int innerWidth;
	private int innerHeight;

	private int changes;

	public FormPortlet(PortletConfig config) {
		super(config);
		this.styleManager = this.getManager().getStyleManager().getFormStyle();
		formPortletStyle = new FormPortletStyle(this, this.getStyleManager());
		formPortletJs = new FormPortletJs(this);
	}

	public FormPortlet setStyle(PortletStyleManager_Form styleManager) {
		this.formPortletStyle.setStyleManager(styleManager);
		this.styleManager = styleManager;
		return this;
	}
	public FormPortletStyle getFormPortletStyle() {
		return this.formPortletStyle;
	}

	public <T extends FormPortletField<?>> T addField(T field) {
		addField(field, fields.getSize());
		return field;
	}
	public boolean addFieldNoThrow(FormPortletField<?> field) {
		if (field.getForm() == this)
			return false;
		addField(field);
		return true;
	}
	public <T extends FormPortletField<?>> T addField(T field, int location) {
		if (field.getForm() == this)
			throw new IllegalStateException("already added");
		if (field.getId() == null)
			field.setId(generateFieldId());
		field.setForm(this);
		fields.add(field.getId(), field, location);
		flagChange(MASK_FIELDS);
		flagLayoutChanged();
		field.updateFormSize();
		return field;
	}

	public FormPortletField<?> getFieldAt(int i) {
		return this.fields.getAt(i);
	}

	public int getFieldsCount() {
		return this.fields.getSize();
	}

	public Set<String> getFieldsLabels() {
		Set<String> labels = new HashSet<String>();
		for (FormPortletField<?> f : this.fields.values()) {
			labels.add(f.getTitle());
		}
		return labels;
	}

	private String generateFieldId() {
		return SH.toString(nextId++);
	}

	public Set<String> getFields() {
		return fields.keySet();
	}
	public Iterable<FormPortletField<?>> getFormFields() {
		return fields.values();
	}

	public int getFieldLocation(FormPortletField<?> field) {
		return fields.getPosition(field.getId());
	}

	public boolean hasField(FormPortletField<?> field) {
		return field.getForm() == this && fields.containsKey(field.getId());
	}
	public FormPortletField<?> removeField(FormPortletField<?> field) {
		FormPortletField<?> r = fields.remove(field.getId());
		pendingFieldConfigChanges.remove(field.getId());
		if (r instanceof FormPortletRadioButtonField) {
			String gn = ((FormPortletRadioButtonField) r).getGroupNameWithFormPortletId();
			if (this.group2LastChecked.get(gn) == r)
				this.group2LastChecked.remove(gn);
		}
		r.setForm(null);
		if (r == this.focusField)
			this.focusField = null;
		if (r == this.refocusField)
			this.refocusField = null;
		flagChange(MASK_FIELDS);
		return r;
	}
	public FormPortletField<?> removeFieldNoThrow(FormPortletField<?> field) {
		FormPortletField<?> r = fields.removeNoThrow(field.getId());
		pendingFieldConfigChanges.remove(field.getId());
		if (r != null) {
			if (r instanceof FormPortletRadioButtonField) {
				String gn = ((FormPortletRadioButtonField) r).getGroupNameWithFormPortletId();
				if (this.group2LastChecked.get(gn) == r)
					this.group2LastChecked.remove(gn);
			}
			r.setForm(null);
			if (r == this.focusField)
				this.focusField = null;
			if (r == this.refocusField)
				this.refocusField = null;
			flagChange(MASK_FIELDS);
		}
		return r;
	}
	public FormPortletButton removeButton(FormPortletButton button) {
		FormPortletButton r = buttons.remove(button.getId());
		if (r == null)
			return null;
		r.setForm(null);
		flagButtonsChanged();
		return r;
	}
	public FormPortletButton removeButtonNoThrow(FormPortletButton button) {
		FormPortletButton r = buttons.removeNoThrow(button.getId());
		if (r == null)
			return null;
		r.setForm(null);
		flagButtonsChanged();
		return r;
	}
	public void clearButtons() {
		for (String button : CH.l(buttons.keySet()))
			removeButton(buttons.get(button));
	}
	public void clearFields() {
		for (String button : CH.l(fields.keySet()))
			removeField(fields.get(button));
	}

	public boolean hasButton(String id) {
		return buttons.containsKey(id);
	}

	public <T extends FormPortletButton> T addButton(T button, int location) {
		if (button.getId() == null)
			button.setId(generateFieldId());
		button.setForm(this);
		buttons.add(button.getId(), button, location);
		flagButtonsChanged();
		return button;
	}

	public <T extends FormPortletButton> T addButton(T button) {
		if (button.getId() == null)
			button.setId(generateFieldId());
		button.setForm(this);
		buttons.add(button.getId(), button);
		flagButtonsChanged();
		return button;
	}

	private IndexedList<String, FormPortletField<?>> getFieldsList() {
		return this.fields;
	}
	protected IndexedList<String, FormPortletButton> getButtonsList() {
		return this.buttons;
	}

	@Override
	public void drainJavascript() {
		this.formPortletJs.initLcv();
		try {
			if (getVisible()) {
				int formWidth = this.getWidth();
				int maxX = getWidth();
				int maxY = getHeight();
				if (hasChanged(MASK_STYLE)) {
					int labelsWidth = this.formPortletStyle.getLabelsWidth();
					int labelPadding = this.formPortletStyle.getLabelPadding();
					String labelsStyle = this.formPortletStyle.getLabelsStyle();
					int fieldSpacing = this.formPortletStyle.getFieldSpacing();
					int widthStretchPadding = this.formPortletStyle.getWidthStretchPadding();
					int buttonHeight = this.formPortletStyle.getButtonHeight();
					int buttonPaddingT = this.formPortletStyle.getButtonPaddingT();
					int buttonPaddingB = this.formPortletStyle.getButtonPaddingB();
					String buttonPanelStyle = this.formPortletStyle.getButtonPanelStyle();
					String buttonsStyle = this.formPortletStyle.getButtonsStyle();
					int buttonsSpacing = this.formPortletStyle.getButtonsSpacing();
					this.formPortletJs.callJsFunction_setCssStyle(formPortletStyle.getCssStyle());
					this.formPortletJs.callJsFunction_setLabelWidth(labelsWidth, labelPadding, labelsStyle, fieldSpacing, widthStretchPadding);
					this.formPortletJs.callJsFunction_setButtonStyle(buttonHeight, buttonPaddingT, buttonPaddingB, buttonPanelStyle, buttonsStyle, buttonsSpacing);
					this.formPortletJs.callJsFunction_setScrollOptions();
				}
				if (hasChanged(MASK_REBUILD)) {
					int htmlRotate = formPortletStyle.getHtmlRotate();
					this.formPortletJs.callJsFunction_reset();
					this.formPortletJs.callJsFunction_setHtmlLayout(htmlLayout, htmlRotate);
				}

				if (hasChanged(MASK_BUTTONS)) {
					this.formPortletJs.buildButtons();
				}
				if (hasChanged(MASK_POSITIONS | MASK_FIELDS)) {
					this.fieldsOrderByTab = null;
					int totHeight = 0;
					int stretchCnt = 0;
					int fieldsCnt = 0;
					for (FormPortletField<?> e : fields.values()) {
						if (!e.isVisible())
							continue;
						if (e.isFixedPosition())
							continue;
						fieldsCnt++;
						int sh = e.getSuggestedHeight();
						if (sh == FormPortletField.USE_DEFAULT)
							sh = this.getStyleManager().getDefaultFormFieldHeight(e.getjsClassName());
						if (sh == FormPortletField.HEIGHT_STRETCH) {
							stretchCnt++;
						} else {
							totHeight += sh;
						}
					}
					totHeight += this.formPortletStyle.getFieldSpacing() * fieldsCnt;
					int stretchHeight = Math.max(22,
							(int) ((double) (getHeight() - totHeight
									- (this.getButtonsList().getSize() == 0 ? 0
											: (this.formPortletStyle.getButtonHeight() + this.formPortletStyle.getButtonPaddingB() + this.formPortletStyle.getButtonPaddingT())))
									/ stretchCnt));
					int runningTop = this.formPortletStyle.getFieldSpacing();
					int lw = this.formPortletStyle.getLabelsWidth();
					for (Map.Entry<String, FormPortletField<?>> e : fields) {
						int left = lw;
						int top = runningTop;
						FormPortletField<?> field = e.getValue();
						if (!field.isVisible())
							continue;
						int height;
						int width;
						field.updateFormSize();
						if (field.getHorizontalLocation().isDefined()) {
							left = field.getRealizedLeftPosPx();
							width = field.getRealizedWidthPx();
						} else {
							width = field.getSuggestedWidth();
							if (width == FormPortletField.USE_DEFAULT)
								width = this.getStyleManager().getDefaultFormFieldWidth(field.getjsClassName());
							if (width == FormPortletField.WIDTH_STRETCH)
								width = formWidth - this.formPortletStyle.getLabelsWidth() - formPortletStyle.getScrollBarWidth();
						}
						if (field.getVerticalLocation().isDefined()) {
							top = field.getRealizedTopPosPx();
							height = field.getRealizedHeightPx();
						} else {
							height = field.getSuggestedHeight();
							if (height == FormPortletField.USE_DEFAULT)
								height = this.getStyleManager().getDefaultFormFieldHeight(field.getjsClassName());
							if (height == FormPortletField.HEIGHT_STRETCH)
								height = stretchHeight;
							runningTop += height + this.formPortletStyle.getFieldSpacing();
						}
						field.setCalculatedPosition(left, top, width, height);
						maxX = Math.max(maxX, field.getCalculatedRightPx());
						maxY = Math.max(maxY, field.getCalculatedBottomPx());
					}
					this.setInnerSize(maxX, maxY);
				}
				if (hasChanged(MASK_CLIP))
					this.formPortletJs.callJsFunction_setScroll();
				if (hasChanged(MASK_SCROLL_STYLE))
					formPortletJs.callJsFunction_setScrollOptions();
				StringBuilder pendingJs = getManager().getPendingJs();
				if (hasChanged(MASK_FIELDS)) {
					HashSet<String> needsRemoving = new HashSet<String>(this.fieldsInFrontend.keySet());
					for (Map.Entry<String, FormPortletField<?>> e : getFieldsList()) {
						String key = e.getKey();
						FormPortletField<?> field = e.getValue();
						if (field.isVisible()) {
							needsRemoving.remove(key);
							FormPortletField<?> current = fieldsInFrontend.get(key);
							if (current == field)
								continue;
							else if (current != null)//the id has been assigned to a new field
								this.formPortletJs.runJs_removeField(key);
							this.formPortletJs.runJs_addField(field);
							this.formPortletJs.callJsFunction_UpdateJs(field, pendingJs);

							//							//Loop through every extension
							//							if (field.hasChanged(FormPortletField.MASK_EXTENSION_PENDINGJS_UPDATE)) {
							//								field.callExtensionUpdateJs(pendingJs);
							//							}
							this.pendingFieldConfigChanges.remove(key);
							//							this.formPortletJs.callJsFunction_setValue(field);
							this.fieldsInFrontend.put(key, field);
						}
					}
					for (String s : needsRemoving) {
						this.formPortletJs.runJs_removeField(s);
						this.fieldsInFrontend.remove(s);
					}
				}
				for (String entry : this.pendingFieldConfigChanges) {
					FormPortletField<?> f = getField(entry);
					if (f.isVisible()) {
						if (f.hasChanged(FormPortletField.MASK_REBUILD)) {
							this.formPortletJs.runJs_removeField(f.getId());
							this.formPortletJs.runJs_addField(f);
						}
						formPortletJs.callJsFunction_UpdateJs(f, pendingJs);
						//TODO: review; Loop through every extension
						if (f.hasChanged(FormPortletField.MASK_EXTENSION_PENDINGJS_UPDATE)) {
							f.callExtensionUpdateJs(pendingJs);
						}
					}
				}
				if (hasChanged(MASK_FIELDS | MASK_POSITIONS))
					this.formPortletJs.callJsFunction_repaint();
				this.pendingFieldConfigChanges.clear();
				changes = 0;
			}
		} finally {
			this.formPortletJs.endLcv();
		}
		super.drainJavascript();
	}

	protected void flagScrollStyleChanged() {
		flagChange(MASK_SCROLL_STYLE);
	}
	protected void flagButtonsChanged() {
		flagChange(MASK_BUTTONS);
	}
	public void flagLayoutChanged() {
		flagChange(MASK_LAYOUT);
	}

	public void setVisible(boolean visible) {
		if (getVisible() != visible) {
			flagLayoutChanged();
			this.fieldsInFrontend.clear();
			flagChange(0xFFFFFFFF);
			super.setVisible(visible);
		}
	}

	@Override
	public void setSize(int width, int height) {
		if (this.getHeight() != height || this.getWidth() != width) {
			flagChange(MASK_POSITIONS);
		}
		super.setSize(width, height);
	}

	public int getButtonLocation(String id) {
		return buttons.getPosition(id);
	}

	public FormPortletButton getButton(String id) {
		return buttons.get(id);
	}

	@Override
	public PortletSchema<FormPortlet> getPortletSchema() {
		return SCHEMA;
	}

	@Override
	public void initJs() {
		super.initJs();
		flagLayoutChanged();
		flagPendingAjax();
	}

	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		final String fieldId = CH.getOr(Caster_String.INSTANCE, attributes, "fieldId", null);
		final FormPortletField<?> field;
		if (fieldId != null) {
			field = fields.getNoThrow(fieldId);
			if (field == null) {
				LH.warning(log, getManager().getUserName() + ": Referencing unknown field: " + attributes);
				return;
			}
			if (field.isDisabled()) {
				LH.warning(log, getManager().getUserName() + ": Potential Security violation, unexpected event: " + attributes);
				return;
			}
		} else
			field = null;
		if (CALLBACK_ONFIELDEVENT.equals(callback)) {
			final String eventType = CH.getOrThrow(Caster_String.INSTANCE, attributes, "feType");
			if ("onFocus".equals(eventType)) {
				this.getManager().onFieldFocused(this, field.getId());
				if (this.focusField != null)
					this.focusField.onFocused(false);
				this.focusField = field;
				this.focusField.onFocused(true);
				this.getManager().requestFocusOnField(null, null);
				this.refocusField = null;
			} else if ("onBlur".equals(eventType)) {
				this.getManager().onFieldBlured(this, field.getId());
				if (this.focusField != null) {
					if (getManager().getFocusedPortlet() != this)
						this.refocusField = this.focusField;
					this.focusField.onFocused(false);
					this.focusField = null;
				}
			}
			for (FormPortletListener listener : formListeners)
				if (listener instanceof FormPortletEventListener) {
					FormPortletEventListener eventListener = (FormPortletEventListener) listener;
					eventListener.onFieldEvent(this, field, eventType, attributes);
				}
		} else if (CALLBACK_ONCHANGE.equals(callback)) {
			final int modificationNumber = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "mid");
			if (!fields.containsKey(fieldId))
				return;
			if (modificationNumber == field.getModificationNumber()) {
				if (onUserChangedValue(field, attributes))
					fireFieldValueChangedTolisteners(field, attributes);
			} else {
				LH.warning(log, "Received Modification Number: ", modificationNumber, " Current Modification Number: ", field.getModificationNumber());
			}
		} else if (CALLBACK_ON_TITLE_CLICKED.equals(callback)) {
			final int mouseX = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "x");
			final int mouseY = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "y");
			fireOnTitleClicked(fieldId, mouseX, mouseY);
		} else if (CALLBACK_ONBUTTON.equals(callback)) {
			final String buttonId = CH.getOrThrow(Caster_String.INSTANCE, attributes, "buttonId");
			final FormPortletButton button = buttons.get(buttonId);
			fireButtonClicked(button);
		} else if (CALLBACK_ONKEY.equals(callback)) {

			int mask = 0;
			if (CH.getOrThrow(Caster_Boolean.INSTANCE, attributes, "ctrl"))
				mask |= KEY_CTRL;
			if (CH.getOrThrow(Caster_Boolean.INSTANCE, attributes, "shift"))
				mask |= KEY_SHIFT;
			if (CH.getOrThrow(Caster_Boolean.INSTANCE, attributes, "alt"))
				mask |= KEY_ALT;
			final int keycode = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "keycode");
			final int cursorPosition = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "pos");
			onUserChangedValue(field, attributes);
			fireFieldSpecialKeyPressed(field, keycode, mask, cursorPosition);
		} else if (CALLBACK_ONCLICK.equals(callback)) {
			if (field instanceof FormPortletColorField)
				((FormPortletColorField) field).onClicked(getManager().getPendingJs(), getJsObjectName());

			//TODO:onclick
		} else if (CALLBACK_ONCUSTOM.equals(callback)) {
			final String action = CH.getOrThrow(Caster_String.INSTANCE, attributes, "action");

			field.handleCallback(action, attributes);
		} else if (CALLBACK_ONFIELD_CALLBACK.equals(callback)) {
			final String action = CH.getOrThrow(Caster_String.INSTANCE, attributes, "action");

			field.handleCallback(action, attributes);
		} else if (CALLBACK_ONFIELD_EXT_CALLBACK.equals(callback)) {
			final String action = CH.getOrThrow(Caster_String.INSTANCE, attributes, "action");
			final int extId = CH.getOr(Caster_Integer.INSTANCE, attributes, "extId", -1);
			if (extId == -1)
				throw new IllegalStateException("Unknown field extension -1");

			FormPortletFieldExtension ext = field.getExtension(extId);
			ext.handleCallback(action, attributes);
		} else if (CALLBACK_MENU2.equals(callback)) {
			final int cursorPosition = CH.getOr(Caster_Integer.INSTANCE, attributes, "cursorPos", -1);
			if (field instanceof FormPortletTitleField)
				return;
			if (cursorPosition != -1) {
				if (field instanceof FormPortletTextEditField)
					((FormPortletTextEditField) field).setCursorPosition(cursorPosition);
			}
			if (this.menuFactory != null) {
				WebMenu menu = this.menuFactory.createMenu(this, field, cursorPosition);
				if (menu != null) {
					this.formPortletJs.runJs_showContextMenu(menu, field.getId());
				}
			}

		} else if (CALLBACK_MENUBUTTON.equals(callback)) {
			final String buttonId = CH.getOrThrow(Caster_String.INSTANCE, attributes, "buttonId");
			final int cursorPosition = CH.getOr(Caster_Integer.INSTANCE, attributes, "cursorPos", -1);
			FormPortletButton button = buttons.get(buttonId);

			if (this.menuFactoryForButton != null) {
				WebMenu menu = this.menuFactoryForButton.createMenu(this, button, cursorPosition);
				if (menu != null) {
					this.formPortletJs.runJs_showButtonContextMenu(menu);
				}
			}

		} else if (CALLBACK_MENUITEM.equals(callback)) {
			if (!field.onMenuItem(attributes))
				return;
			String action = CH.getOrThrow(attributes, "action");
			if (!"button_clicked".equals(action)) {
				WebMenuLink link = getManager().getMenuManager().fireLinkForId(action);
				if (link != null)
					fireContextMenu(link, field);
			} else {
				for (FormPortletContextMenuListener c : this.menuListeners)//TODO: legacy, needs to go away
					c.onContextMenu(this, "button_clicked", field);
				fireFieldValueChangedTolisteners(field, attributes);
			}
		} else if (CALLBACK_MENUBUTTONITEM.equals(callback)) {
			final String buttonId = CH.getOrThrow(Caster_String.INSTANCE, attributes, "buttonId");
			FormPortletButton button = buttons.get(buttonId);
			String action = CH.getOrThrow(attributes, "action");
			WebMenuLink link = getManager().getMenuManager().fireLinkForId(action);
			if (link != null)
				for (FormPortletContextMenuForButtonListener c : this.menuListenersForButtons)
					c.onContextMenu(this, link.getAction(), button);
		} else if (CALLBACK_CUSTOMCALLBACK.equals(callback)) {
			String customType = attributes.get("customType");
			Object params = ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject(attributes.get("customParams"));
			for (FormPortletCustomCallbackListener listener : this.customCallbackListeners) {
				listener.onCustomCallback(this, customType, params, attributes);
			}
		} else if (callback.equals(CALLBACK_ONAUTOCOMPLETED)) {
			for (FormPortletListener listener : formListeners)
				if (listener instanceof FormPortletEventListener) {
					FormPortletEventListener eventListener = (FormPortletEventListener) listener;
					eventListener.onFieldEvent(this, field, CALLBACK_ONAUTOCOMPLETED, attributes);
				}
		}

	}
	private void onTab(FormPortletField<?> field, boolean reverse) {
		buildFieldsOrderedByTab();
		if (fieldsOrderByTab.size() > 1) {
			int n = CH.indexOfIdentity(fieldsOrderByTab, field);
			n += reverse ? -1 : 1;
			FormPortletField<?> f2 = CH.getAtMod(fieldsOrderByTab, n);
			f2.focus();
		}
	}

	private void buildFieldsOrderedByTab() {
		if (fieldsOrderByTab == null) {
			List<FormPortletField<?>> candidates = new ArrayList<FormPortletField<?>>();
			for (FormPortletField<?> i : this.getFieldsList().values())
				if (i.canFocus() && i.isVisible() && !i.isDisabled())
					candidates.add(i);
			this.fieldsOrderByTab = candidates;
			Collections.sort(fieldsOrderByTab, TAB_POSITION_ORDER);
		}
	}

	protected void fireContextMenu(WebMenuLink link, FormPortletField<?> field) {
		for (FormPortletContextMenuListener c : this.menuListeners)
			c.onContextMenu(this, link.getAction(), field);
	}

	public void fireButtonClicked(FormPortletButton button) {
		onUserPressedButton(button);
		fireUserPressedButtonToListeners(button);
	}

	private void fireOnTitleClicked(String fieldId, int mouseX, int mouseY) {
		FormPortletField<?> field = fields.get(fieldId);
		if (field.getTitleIsClickable())
			for (FormPortletFieldTitleListener i : this.titleListeners)
				i.onTitleClicked(this, field, mouseX, mouseY);
	}

	private void fireFieldSpecialKeyPressed(FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		for (FormPortletListener listener : formListeners)
			listener.onSpecialKeyPressed(this, field, keycode, mask, cursorPosition);
	}

	private void fireUserPressedButtonToListeners(FormPortletButton button) {
		for (FormPortletListener listener : formListeners)
			listener.onButtonPressed(this, button);
	}

	public void fireFieldValueChangedTolisteners(FormPortletField<?> field, Map<String, String> attributes) {
		for (FormPortletListener listener : formListeners)
			listener.onFieldValueChanged(this, field, attributes);
	}

	protected void onUserPressedButton(FormPortletButton formPortletButton) {

	}

	protected boolean onUserChangedValue(FormPortletField<?> field, Map<String, String> attributes) {
		return field.onUserValueChanged(attributes);
	}

	public void onFieldChanged(FormPortletField<?> field) {
		if (field.getForm() != this || !fields.containsKey(field.getId()))
			throw new IllegalArgumentException("not member of this form");
		if (!getVisible())
			return;
		pendingFieldConfigChanges.add(field.getId());
		flagPendingAjax();
	}

	public void onButtonChanged(FormPortletButton formPortletButton) {
		flagButtonsChanged();
	}

	public void reset() {
		for (FormPortletField<?> e : fields.values())
			e.reset();
	}

	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		int w = (int) (this.formPortletStyle.getLabelsWidth() * 2.5);
		for (FormPortletField<?> field : this.fields.values()) {
			w = Math.max(w, this.formPortletStyle.getLabelsWidth() + field.getSuggestedWidth());
		}
		int w2 = 0;
		for (FormPortletButton btn : this.buttons.values()) {
			w2 += pm.getWidth(btn.getName());
		}
		return Math.max(w, w2) + 20;
	}
	@Override
	public int getSuggestedHeight(PortletMetrics pm) {
		int h = 0;
		for (FormPortletField<?> field : this.fields.values()) {
			int height = field.getSuggestedHeight();
			if (height == FormPortletField.HEIGHT_STRETCH) {
				height = 40;
			} else if (height == FormPortletField.USE_DEFAULT)
				height = getStyleManager().getDefaultFormFieldHeight(field.getjsClassName());
			h += height;
		}
		if (buttons.getSize() > 0)
			h += this.formPortletStyle.getButtonPaddingB() + this.formPortletStyle.getButtonPaddingT() + this.formPortletStyle.getButtonHeight();
		if (this.fields.getSize() > 1)
			h += this.fields.getSize() * this.formPortletStyle.getFieldSpacing();
		h += this.formPortletStyle.getFieldSpacing();
		return h > 10 ? h : -1;
	}
	public <T extends FormPortletField<?>> T addFieldAfter(FormPortletField<?> existing, T toAdd) {
		return addField(toAdd, getFieldLocation(existing) + 1);
	}
	public <T extends FormPortletField<?>> T addFieldBefore(FormPortletField<?> existing, T toAdd) {
		return addField(toAdd, getFieldLocation(existing));
	}

	public void addFormPortletListener(FormPortletListener formPortletListener) {
		formListeners.add(formPortletListener);
	}

	public void removeFormPortletListener(FormPortletListener formPortletListener) {
		formListeners.remove(formPortletListener);
	}
	public void addTitleListener(FormPortletFieldTitleListener formPortletListener) {
		titleListeners.add(formPortletListener);
	}

	public void removeTitleListener(FormPortletFieldTitleListener formPortletListener) {
		titleListeners.remove(formPortletListener);
	}
	public void addCustomCallbackListener(FormPortletCustomCallbackListener formPortletListener) {
		customCallbackListeners.add(formPortletListener);
	}

	public void removeCustomCallbackListener(FormPortletCustomCallbackListener formPortletListener) {
		customCallbackListeners.remove(formPortletListener);
	}

	public FormPortletContextMenuFactory getMenuFactory() {
		return menuFactory;
	}

	public void setMenuFactory(FormPortletContextMenuFactory menuFactory) {
		this.menuFactory = menuFactory;
	}

	public void addMenuListener(FormPortletContextMenuListener listener) {
		this.menuListeners.add(listener);
	}

	public void addMenuListenerForButton(FormPortletContextMenuForButtonListener listener) {
		this.menuListenersForButtons.add(listener);
	}

	public void removeMenuListener(FormPortletContextMenuListener listener) {
		this.menuListeners.remove(listener);
	}

	public FormPortletField<?> getField(String id) {
		return this.fields.get(id);
	}
	public <TYPE> FormPortletField<TYPE> getField(String id, Class<TYPE> castToType) {
		return (FormPortletField<TYPE>) this.fields.get(id);
	}

	@Override
	public void close() {
		super.close();
	}

	public boolean hasField(String id) {
		return getFields().contains(id);
	}

	public Set<String> getButtons() {
		return this.buttons.keySet();
	}

	public FormPortlet setHtmlLayout(String htmlLayout) {
		if (OH.eq(this.htmlLayout, htmlLayout))
			return this;
		this.htmlLayout = htmlLayout;
		this.fieldsInFrontend.clear();
		flagChange(MASK_REBUILD | MASK_FIELDS);
		return this;
	}

	public FormPortletContextMenuForButtonFactory getMenuFactoryForButton() {
		return menuFactoryForButton;
	}

	public void setMenuFactoryForButton(FormPortletContextMenuForButtonFactory menuFactoryForButton) {
		this.menuFactoryForButton = menuFactoryForButton;
	}

	public PortletStyleManager_Form getStyleManager() {
		return this.styleManager;
	}

	public int getButtonPanelHeight() {
		return this.formPortletStyle.getButtonHeight() + this.formPortletStyle.getButtonPaddingB() + this.formPortletStyle.getButtonPaddingT();
	}

	//Sends a message to the frontend requesting focus
	public void focusField(FormPortletField<?> field) {
		this.refocusField = null;
		if (field != null)
			OH.assertEqIdentity(this, field.getForm());
		getManager().requestFocusOnField(this, field.getId());
	}

	public FormPortletField<?> getFocusField() {
		return this.focusField;
	}

	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		if ("Tab".equals(keyEvent.getKey())) {
			if (keyEvent.getTargetPortlet() == this) {
				String fieldId = keyEvent.getTargetAttachmentId();
				if (fieldId != null) {
					FormPortletField<?> field = getField(fieldId);
					if (field != null) {
						onTab(field, keyEvent.isShiftKey());
						return super.onUserKeyEvent(keyEvent);
					}
				} else {
					buildFieldsOrderedByTab();
					if (this.fieldsOrderByTab.size() > 0)
						this.fieldsOrderByTab.get(0).focus();
				}
			}
		}
		for (FormPortletButton i : this.buttons.values())
			if (i.hasHotKey(keyEvent.getKey())) {
				fireButtonClicked(i);
				return true;
			}
		return super.onUserKeyEvent(keyEvent);
	}

	@Override
	final public int getClipTop() {
		return clipTop;
	}
	@Override
	final public int getClipLeft() {
		return clipLeft;
	}
	@Override
	final public void setClipTopNoFire(int top) {
		this.clipTop = top;
	}
	@Override
	final public void setClipLeftNoFire(int left) {
		this.clipLeft = left;
	}

	public FormPortletField<?> getFieldByName(String name) {
		if (name == null)
			return null;
		FormPortletField<?> f;
		for (int i = 0; i < this.fields.getSize(); i++) {
			f = this.fields.getAt(i);
			if (name.equals(f.getName())) {
				return f;
			}
		}
		return null;
	}
	public List<FormPortletField<?>> getPosUndefinedFields() {
		List<FormPortletField<?>> output = new ArrayList<FormPortletField<?>>();
		FormPortletField<?> f;
		for (int i = 0; i < getFieldsCount(); i++) {
			f = getFieldAt(i);
			if (!f.isFixedPosition()) {
				output.add(f);
			}
		}
		return output;
	}

	public void setClipTop(int clipTop) {
		if (this.clipTop == clipTop)
			return;
		this.clipTop = clipTop;
		flagChange(MASK_CLIP);

	}

	public void setClipLeft(int clipLeft) {
		if (this.clipLeft == clipLeft)
			return;
		this.clipLeft = clipLeft;
		flagChange(MASK_CLIP);
	}

	protected String getHtmlLayout() {
		return htmlLayout;
	}

	public FormPortlet setLabelsWidth(int labelSize) {
		return formPortletStyle.setLabelsWidth(labelSize);
	}

	@Override
	public void onUserRequestFocus(MouseEvent e) {
		super.onUserRequestFocus(e);
		FormPortletField<?> f = null;
		if (e != null && e.getMouseX() != -1 && e.getMouseY() != -1) {
			int x = e.getMouseX() - PortletHelper.getAbsoluteLeft(this);
			int y = e.getMouseY() - PortletHelper.getAbsoluteTop(this);
			for (FormPortletField<?> i : this.getFieldsList().values())
				if (i.canFocus() && i.isVisible() && !i.isDisabled())
					if (i.getRealizedLeftPosPx() <= x && x <= i.getRealizedRightPosPx() && i.getRealizedTopPosPx() <= y && y <= i.getRealizedBottomPosPx())
						if (f == null || f.getZIndex() < i.getZIndex())
							f = i;
		}
		if (f != null)
			this.getManager().requestFocusOnField(this, f.getId());
		else if (this.focusField != null)
			this.getManager().requestFocusOnField(this, focusField.getId());
		else if (this.refocusField != null)
			this.getManager().requestFocusOnField(this, refocusField.getId());
	}

	protected void onFieldFocusLost(FormPortletField<?> field) {
		if (field == this.focusField)
			this.focusField = null;
		if (field == this.refocusField)
			this.refocusField = null;
	}

	protected void setInnerSize(int innerWidth, int innerHeight) {
		this.innerWidth = innerWidth;
		this.innerHeight = innerHeight;
	}

	public int getClipBottom() {
		if (this.innerWidth > this.getWidth())
			return this.getClipTop() + this.getHeight() - this.formPortletStyle.getScrollBarWidth();
		else
			return this.getClipTop() + this.getHeight();
	}
	public int getClipRight() {
		if (this.innerHeight > this.getHeight())
			return this.getClipLeft() + this.getWidth() - this.formPortletStyle.getScrollBarWidth();
		else
			return this.getClipLeft() + this.getWidth();
	}

	public FormPortletRadioButtonField getLastCheckedForGroup(String fullName) {
		return this.group2LastChecked.get(fullName);
	}
	public FormPortletRadioButtonField putLastCheckedForGroup(String fullName, FormPortletRadioButtonField field) {
		return this.group2LastChecked.put(fullName, field);
	}

	public FormPortletRadioButtonField removeLastCheckedForGroup(String fullName) {
		return this.group2LastChecked.remove(fullName);
	}

	protected boolean hasChanged(int mask) {
		return MH.anyBits(this.changes, mask);
	}
	protected boolean flagChange(int mask) {
		this.changes |= mask;
		flagPendingAjax();
		return true;
	}

	@Override
	public boolean hasLayoutChanged() {
		return hasChanged(MASK_LAYOUT | MASK_POSITIONS);
	}

	public void flagStyleChanged() {
		flagChange(MASK_STYLE);
	}
}
