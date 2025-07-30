package com.f1.ami.web.form;

import java.util.Map;
import java.util.Set;

import com.f1.ami.web.AmiWebConsts;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.charts.AmiWebContextMenuFactory;
import com.f1.ami.web.charts.AmiWebContextMenuListener;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.DesktopPortlet.Window;
import com.f1.utils.SH;

public class AmiWebQueryFormContextMenuManager implements AmiWebContextMenuFactory, AmiWebContextMenuListener {

	public static final String ADD = "addfield_";
	private AmiWebQueryFormPortlet queryFormPortlet;

	public AmiWebQueryFormContextMenuManager(AmiWebQueryFormPortlet amiWebQueryFormPortlet) {
		this.queryFormPortlet = amiWebQueryFormPortlet;
	}

	@Override
	public void populateLowerConfigMenu(WebMenu headMenu) {
	}

	@Override
	public void populateConfigMenu(WebMenu headMenu) {
		Map<String, QueryField<?>> fieldsById = this.queryFormPortlet.getFieldsById();
		Set<String> fields = fieldsById.keySet();

		BasicWebMenuLink resetFields = new BasicWebMenuLink("Reset All Field Positions", !this.queryFormPortlet.areAllFieldsAtDefaultPositions(), "reset_all_fields_pos");
		BasicWebMenu addField = new BasicWebMenu("Add Field/Button/Div", true).setBackgroundImage(AmiWebConsts.ICON_ADD_ORANGE);
		BasicWebMenu rem = new BasicWebMenu("Remove Field/Button/Div", fields.size() > 0).setBackgroundImage(AmiWebConsts.ICON_DELETE);
		BasicWebMenu edit = new BasicWebMenu("Edit Field/Button/Div", fields.size() > 0).setBackgroundImage(AmiWebConsts.ICON_PENCIL);

		BasicWebMenu addGuideMenu = new BasicWebMenu("Add Guide", true).setBackgroundImage(AmiWebConsts.ICON_ADD_ORANGE);
		BasicWebMenuLink addGuideH = new BasicWebMenuLink("Horizontal", true, "addguide_h");
		BasicWebMenuLink addGuideV = new BasicWebMenuLink("Vertical", true, "addguide_v");
		addGuideMenu.add(addGuideH);
		addGuideMenu.add(addGuideV);
		headMenu.add(resetFields);
		headMenu.add(addField);
		headMenu.add(addGuideMenu);
		headMenu.add(new BasicWebMenuLink("Edit HTML", true, "edit_html").setBackgroundImage(AmiWebConsts.ICON_HTML));
		headMenu.add(new BasicWebMenuLink((this.getEditableForm().getInEditorMode() ? "Exit" : "Enter") + " Form Design Mode", true, "enter_exit_design_mode"));
		headMenu.add(edit);
		headMenu.add(rem);
		headMenu.add(new BasicWebMenuLink("Reset Field Values", true, "reset_values"));
		headMenu.add(new BasicWebMenuLink("Reset Field Positions", true, "reset_positions"));
		BasicWebMenuLink addButton = new BasicWebMenuLink("Add Relationship Button", true, "addbutton").setBackgroundImage(AmiWebConsts.ICON_ADD_ORANGE);
		BasicWebMenu editButton = new BasicWebMenu("Edit Relationship Button", this.queryFormPortlet.buttons.size() > 0).setBackgroundImage(AmiWebConsts.ICON_PENCIL);
		BasicWebMenu remButton = new BasicWebMenu("Remove Relationship Button", this.queryFormPortlet.buttons.size() > 0).setBackgroundImage(AmiWebConsts.ICON_DELETE);
		headMenu.add(addButton);
		headMenu.add(editButton);
		headMenu.add(remButton);

		AmiWebQueryFormContextMenuManager.createAddFieldsMenu(addField);
		addField.sort();
		for (String i : fields) {
			QueryField<?> field = fieldsById.get(i);
			StringBuilder temp = new StringBuilder(field.getLabel()).append(" - <i>[").append(field.getName()).append("]</i>");
			if (!field.getField().isVisible()) {
				temp.append(" - <b>HIDDEN</b>");
			}
			String text = temp.toString();
			edit.add(new BasicWebMenuLink(text, true, "editfld_" + i));
			rem.add(new BasicWebMenuLink(text, true, "remfld_" + i));
		}

		for (String i : this.queryFormPortlet.buttons.keySet()) {
			AmiWebButton button = this.queryFormPortlet.buttons.get(i);
			editButton.add(new BasicWebMenuLink(button.getName(), true, "edit_button_" + i));
		}
		for (String i : this.queryFormPortlet.buttons.keySet()) {
			AmiWebButton button = this.queryFormPortlet.buttons.get(i);
			remButton.add(new BasicWebMenuLink(button.getName(), true, "remove_button_" + i));
		}
	}
	//	@Override
	//	public void populateConfigMenuReadonly(WebMenu headMenu) {
	//		boolean hasFilter = false;
	//		AmiWebDmPortlet dmp = (AmiWebDmPortlet) this.queryFormPortlet;
	//		AmiWebDmManager dmm = this.queryFormPortlet.getService().getDmManager();
	//		for (String dmName : dmp.getUsedDmAliasDotNames()) {
	//			AmiWebDm dm = dmm.getDmByAliasDotName(dmName);
	//			if (dm.getFilters().size() > 0) {
	//				hasFilter = true;
	//				break;
	//			}
	//		}
	//		boolean hasDm = !dmp.getUsedDmAliasDotNames().isEmpty();
	//		if (hasFilter) {
	//			BasicWebMenu viewMenu = new BasicWebMenu("View Underlying Data...", hasDm);
	//			viewMenu.add(new BasicWebMenuLink("Before Filters", hasDm, "view_data_before"));
	//			viewMenu.add(new BasicWebMenuLink("After Filters", hasDm, "view_data_after"));
	//			headMenu.add(viewMenu);
	//		} else {
	//			headMenu.add(new BasicWebMenuLink("View Underlying Data...", hasDm, "view_data"));
	//		}
	//
	//	}

	@Override
	public boolean onAmiContextMenu(String id) {
		if (id.startsWith(ADD)) {
			String fid = SH.stripPrefix(id, ADD, true);
			this.queryFormPortlet.addFieldMenuAction(fid);
			return true;
		} else if ("addguide_v".equals(id)) {
			this.getEditableForm().addVerticalGuide(this.getEditableForm().getWidth() / 2);
			return true;
		} else if ("addguide_h".equals(id)) {
			this.getEditableForm().addHorizontalGuide(this.getEditableForm().getHeight() / 2);
			return true;
		} else if ("addbutton_".equals(id)) {
			this.queryFormPortlet.showEditButtonPortlet("", false);
			return true;
		} else if ("addbutton".equals(id)) {
			this.queryFormPortlet.showEditButtonPortlet("", false);
			return true;
		} else if ("reset_values".equals(id)) {
			this.queryFormPortlet.resetFormFields();
			return true;
		} else if ("reset_positions".equals(id)) {
			this.queryFormPortlet.resetSelectedQueryFieldPositions();
			return true;
		} else if ("edit_html".equals(id)) {
			AmiWebQueryFormEditHtmlPortlet html = new AmiWebQueryFormEditHtmlPortlet(this.queryFormPortlet, generateConfig());
			this.queryFormPortlet.getEditableForm().setInEditorMode(false);
			AmiWebService service = this.queryFormPortlet.getService();
			Window w = service.getDesktop().getDesktop().addChild("Edit Html", html);
			service.getDesktop().applyEditModeStyle(w);
			service.getPortletManager().onPortletAdded(html);
			return true;
		} else if (id.startsWith("editfld_")) {
			this.queryFormPortlet.editFieldMenuAction(id);
			return true;
		} else if (id.startsWith("remfld_")) {
			this.queryFormPortlet.removeFieldMenuAction(id);
			return true;
		} else if (id.startsWith("edit_button_")) {
			String fid = SH.stripPrefix(id, "edit_button_", true);
			this.queryFormPortlet.showEditButtonPortlet(fid, true);
			return true;
		} else if (id.startsWith("remove_button")) {
			String fid = SH.stripPrefix(id, "remove_button_", true);
			this.queryFormPortlet.showRemoveButtonPortlet(fid);
			return true;
		} else if ("enter_exit_design_mode".equals(id)) {
			this.queryFormPortlet.enterExitDesignModeMenuAction();
			return true;
		} else if ("reset_all_fields_pos".equals(id)) {
			this.queryFormPortlet.resetAllQueryFieldPositions();
			return true;
		} else
			return false;

	}
	public static void createAddFieldsMenu(BasicWebMenu sink, int x, int y) {
		String mousePosition = "";
		if (x != -1 && y != -1)
			mousePosition = AmiWebQueryFormPortlet.MOUSE_POS_X_Y + x + "_" + y;
		sink.add(new BasicWebMenuLink("Text Field", true, ADD + QueryField.TYPE_ID_TEXT + mousePosition).setBackgroundImage(AmiWebConsts.ICON_FIELD_TEXT));
		sink.add(new BasicWebMenuLink("Text Area", true, ADD + QueryField.TYPE_ID_TEXT_AREA + mousePosition).setBackgroundImage(AmiWebConsts.ICON_FIELD_TEXT_AREA));
		sink.add(new BasicWebMenuLink("Numeric Slider", true, ADD + QueryField.TYPE_ID_RANGE + mousePosition).setBackgroundImage(AmiWebConsts.ICON_FIELD_NUMERIC_SLIDER));
		sink.add(new BasicWebMenuLink("Numeric Range", true, ADD + QueryField.TYPE_ID_SUBRANGE + mousePosition).setBackgroundImage(AmiWebConsts.ICON_FIELD_NUMERIC_RANGE));
		sink.add(new BasicWebMenuLink("Checkbox", true, ADD + QueryField.TYPE_ID_CHECKBOX + mousePosition).setBackgroundImage(AmiWebConsts.ICON_FIELD_CHECK_BOX));
		sink.add(new BasicWebMenuLink("Multi Checkbox", true, ADD + QueryField.TYPE_ID_MULTI_CHECKBOX + mousePosition).setBackgroundImage(AmiWebConsts.ICON_FIELD_MULTI_CHECKBOX));
		sink.add(new BasicWebMenuLink("Radio Button", true, ADD + QueryField.TYPE_ID_RADIO + mousePosition).setBackgroundImage(AmiWebConsts.ICON_FIELD_RADIO_BUTTON));
		sink.add(new BasicWebMenuLink("Color Picker", true, ADD + QueryField.TYPE_ID_COLOR_PICKER + mousePosition).setBackgroundImage(AmiWebConsts.ICON_FIELD_COLOR_PICKER));
		sink.add(new BasicWebMenuLink("Color Gradient Picker", true, ADD + QueryField.TYPE_ID_COLOR_GRADIENT_PICKER + mousePosition)
				.setBackgroundImage(AmiWebConsts.ICON_FIELD_COLOR_PICKER_GRADIENT));
		sink.add(new BasicWebMenuLink("Select Field", true, ADD + QueryField.TYPE_ID_SELECT + mousePosition).setBackgroundImage(AmiWebConsts.ICON_FIELD_SELECT));
		sink.add(new BasicWebMenuLink("Multi Select Field", true, ADD + QueryField.TYPE_ID_MULTI_SELECT + mousePosition).setBackgroundImage(AmiWebConsts.ICON_FIELD_MULTI_SELECT));
		sink.add(new BasicWebMenuLink("Date Field", true, ADD + QueryField.TYPE_ID_DATE + mousePosition).setBackgroundImage(AmiWebConsts.ICON_FIELD_DATE));
		sink.add(new BasicWebMenuLink("Date Range Field", true, ADD + QueryField.TYPE_ID_DATERANGE + mousePosition).setBackgroundImage(AmiWebConsts.ICON_FIELD_DATE));
		sink.add(new BasicWebMenuLink("Time Field", true, ADD + QueryField.TYPE_ID_TIME + mousePosition).setBackgroundImage(AmiWebConsts.ICON_FIELD_TIME));
		sink.add(new BasicWebMenuLink("Time Range Field", true, ADD + QueryField.TYPE_ID_TIMERANGE + mousePosition).setBackgroundImage(AmiWebConsts.ICON_FIELD_TIME));
		sink.add(new BasicWebMenuLink("Datetime Field", true, ADD + QueryField.TYPE_ID_DATETIME + mousePosition).setBackgroundImage(AmiWebConsts.ICON_FIELD_DATETIME));
		sink.add(new BasicWebMenuLink("File Upload Field", true, ADD + QueryField.TYPE_ID_FILE_UPLOAD + mousePosition).setBackgroundImage(AmiWebConsts.ICON_UPLOAD));
		sink.add(new BasicWebMenuLink("Button", true, ADD + QueryField.TYPE_ID_BUTTON + mousePosition).setBackgroundImage(AmiWebConsts.ICON_FIELD_BUTTON));
		sink.add(new BasicWebMenuLink("Password Field", true, ADD + QueryField.TYPE_ID_PASSWORD + mousePosition).setBackgroundImage(AmiWebConsts.ICON_FIELD_PASSWORD));
		sink.add(new BasicWebMenuLink("Div Field", true, ADD + QueryField.TYPE_ID_DIV + mousePosition).setBackgroundImage(AmiWebConsts.ICON_HTML));
		sink.add(new BasicWebMenuLink("Image Field", true, ADD + QueryField.TYPE_ID_IMAGE + mousePosition).setBackgroundImage(AmiWebConsts.ICON_FIELD_IMAGE));
	}
	static void createAddFieldsMenu(BasicWebMenu sink) {
		createAddFieldsMenu(sink, -1, -1);
	}

	private PortletConfig generateConfig() {
		return this.queryFormPortlet.generateConfig();
	}
	private AmiWebEditableFormPortlet getEditableForm() {
		return queryFormPortlet.getEditableForm();
	}
	private PortletManager getManager() {
		return queryFormPortlet.getManager();
	}

}
