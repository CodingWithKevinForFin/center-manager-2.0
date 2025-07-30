package com.f1.suite.web.portal.impl.form;

import java.util.Map;
import java.util.Set;

import com.f1.suite.web.portal.style.PortletStyleManager_Form;

public interface Form {
	public static final String CALLBACK_MENUBUTTONITEM = "menubuttonitem";
	public static final String CALLBACK_MENUITEM = "menuitem";
	public static final String CALLBACK_MENUBUTTON = "menubutton";
	public static final String CALLBACK_MENU2 = "menu";
	public static final String CALLBACK_ONCUSTOM = "oncustom";
	public static final String CALLBACK_ONCLICK = "onclick";
	public static final String CALLBACK_ONKEY = "onkey";
	public static final String CALLBACK_ONBUTTON = "onbutton";
	public static final String CALLBACK_ON_TITLE_CLICKED = "onTitleClicked";
	public static final String CALLBACK_ONCHANGE = "onchange";
	public static final String CALLBACK_ONFOCUS = "onFocus";
	public static final String CALLBACK_ONFIELDEVENT = "onFieldEvent";
	public static final String CALLBACK_ONFIELD_CALLBACK = "onFieldCB";
	public static final String CALLBACK_ONFIELD_EXT_CALLBACK = "onExtCB";
	public static final String CALLBACK_CUSTOMCALLBACK = "customCallback";
	public static final String CALLBACK_ONAUTOCOMPLETED = "onAutocompleted";
	public static final int KEY_CTRL = 1;
	public static final int KEY_SHIFT = 2;
	public static final int KEY_ALT = 4;
	public static final int KEYCODE_ENTER = 13;

	//Style
	public PortletStyleManager_Form getStyleManager();
	public FormPortlet setStyle(PortletStyleManager_Form styleManager);

	//Fields
	public <T extends FormPortletField<?>> T addField(T field);
	public boolean addFieldNoThrow(FormPortletField<?> field);
	public <T extends FormPortletField<?>> T addField(T field, int location);
	public <T extends FormPortletField<?>> T addFieldAfter(FormPortletField<?> existing, T toAdd);
	public <T extends FormPortletField<?>> T addFieldBefore(FormPortletField<?> existing, T toAdd);
	public FormPortletField<?> getFieldAt(int i);
	public FormPortletField<?> getField(String id);
	public <TYPE> FormPortletField<TYPE> getField(String id, Class<TYPE> castToType);
	public boolean hasField(FormPortletField<?> field);
	public boolean hasField(String id);
	public int getFieldLocation(FormPortletField<?> field);
	public Set<String> getFields();
	public Iterable<FormPortletField<?>> getFormFields();
	public int getFieldsCount();
	public FormPortletField<?> removeField(FormPortletField<?> field);
	public FormPortletField<?> removeFieldNoThrow(FormPortletField<?> field);
	public void clearFields();
	//Scroll
	public int getClipTop();
	public int getClipLeft();
	public void setClipTopNoFire(int top);
	public void setClipLeftNoFire(int left);
	//Focus
	public void focusField(FormPortletField<?> field);

	//Reset
	public void reset();

	//FieldLabels
	public Set<String> getFieldsLabels();

	//Buttons
	public <T extends FormPortletButton> T addButton(T button, int location);
	public <T extends FormPortletButton> T addButton(T button);
	public FormPortletButton getButton(String id);
	public boolean hasButton(String id);
	public int getButtonLocation(String id);
	public FormPortletButton removeButton(FormPortletButton button);
	public FormPortletButton removeButtonNoThrow(FormPortletButton button);
	public void clearButtons();
	public Set<String> getButtons();

	//Fire Events
	public void fireButtonClicked(FormPortletButton button);
	public void fireFieldValueChangedTolisteners(FormPortletField<?> field, Map<String, String> attributes);

	//Handle Events
	public void onButtonChanged(FormPortletButton formPortletButton);

	//Title
	public void addTitleListener(FormPortletFieldTitleListener formPortletListener);
	public void removeTitleListener(FormPortletFieldTitleListener formPortletListener);

	//Context Menu
	public FormPortletContextMenuFactory getMenuFactory();
	public FormPortletContextMenuForButtonFactory getMenuFactoryForButton();

	public void setMenuFactory(FormPortletContextMenuFactory menuFactory);
	public void setMenuFactoryForButton(FormPortletContextMenuForButtonFactory menuFactoryForButton);
	public void addMenuListener(FormPortletContextMenuListener listener);
	public void addMenuListenerForButton(FormPortletContextMenuForButtonListener listener);
	public void removeMenuListener(FormPortletContextMenuListener listener);

	//Layout
	public FormPortlet setHtmlLayout(String htmlLayout);
	public boolean hasLayoutChanged();

}
