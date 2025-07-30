package com.f1.ami.web.menu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebService;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiWebCustomContextMenuManager implements ConfirmDialogListener {
	public static final String ACTION_MENU_ADD = "menu_add";
	public static final String CUSTOM_MENU_ACTION_IMPORT = "cust_menu_import";
	public static final String CUSTOM_MENU_ACTION_EXPORT = "cust_menu_export";
	public static final String CUSTOM_MENU_ACTION_EDIT = "cust_menu_edit_";
	public static final String CUSTOM_MENU_ACTION_DELETE = "cust_menu_delete_";
	public static final String CUSTOM_MENU_ACTION_ARRANGE = "cust_menu_arrange_";
	final public static String CUSTOM_MENU_ACTION_PREFIX = "cust_menu_action_";

	private static final Comparator<AmiWebCustomContextMenu> POSITION_SORTER = new Comparator<AmiWebCustomContextMenu>() {

		@Override
		public int compare(AmiWebCustomContextMenu o1, AmiWebCustomContextMenu o2) {
			return OH.compare(o1.getPosition(), o2.getPosition());
		}
	};

	final private TreeMap<String, AmiWebCustomContextMenu> items = new TreeMap<String, AmiWebCustomContextMenu>();

	private AmiWebCustomContextMenu root;
	final private AmiWebDomObject targetPortlet;

	public AmiWebCustomContextMenuManager(AmiWebDomObject tp) {
		this.targetPortlet = tp;
		this.root = new AmiWebCustomContextMenu(this, true);
		this.root.setId("");
		addItem(root);
	}

	private void addItem(AmiWebCustomContextMenu item) {
		CH.putOrThrow(this.items, item.getId(), item);
		item.onAdded();
	}

	public AmiWebCustomContextMenu getRootMenu() {
		return this.root;
	}

	public List<Map<String, Object>> getConfiguration() {
		List<Map<String, Object>> r = new ArrayList<Map<String, Object>>();
		for (AmiWebCustomContextMenu i : this.items.values())
			if (!i.isRoot())
				r.add(i.getConfiguration());
		return r;
	}
	public void init(Object conf) {
		if (conf instanceof List)
			init((List) conf);
		else {
			List<AmiWebCustomContextMenu> sink;
			initBackwardsComp((Map) conf, "");
		}
		for (AmiWebCustomContextMenu i : CH.sort(this.items.values(), POSITION_SORTER)) {
			if (!i.isRoot()) {
				AmiWebCustomContextMenu parent = this.getMenu(i.getParentId());
				parent.addChild(i);
			}
		}
	}
	private void initBackwardsComp(Map<String, Object> conf, String parentId) {
		if (CH.isEmpty(conf))
			return;
		for (Entry<String, Object> e : conf.entrySet()) {
			Map<String, Object> innerConf = (Map<String, Object>) e.getValue();
			Map<String, Object> item = (Map<String, Object>) innerConf.get("item");
			if (item != null) {
				AmiWebCustomContextMenu menu = new AmiWebCustomContextMenu(this, false);
				menu.init(item);
				menu.setId(e.getKey());
				menu.setParentId(parentId);
				String script = (String) item.get("amiscript");
				menu.getAmiScript().setAmiScriptCallbackNoCompile(menu.ON_SELECTED, script);
				addItem(menu);
				initBackwardsComp((Map<String, Object>) innerConf.get("children"), menu.getId());
			}
		}

	}
	public void importConfig(String parentId, int position, List<Map<String, Object>> config) {
		HashSet<String> used = new HashSet<String>(this.items.keySet());
		Map<String, String> mapping = new HashMap<String, String>();

		List<AmiWebCustomContextMenu> items = new ArrayList<AmiWebCustomContextMenu>();
		for (Map<String, Object> i : config) {
			AmiWebCustomContextMenu menu = new AmiWebCustomContextMenu(this, false);
			menu.init(i);
			String replacement = SH.getNextId(menu.getId(), used);
			used.add(replacement);
			menu.setId(replacement);
			CH.putOrThrow(mapping, menu.getId(), replacement);
			items.add(menu);
		}
		for (AmiWebCustomContextMenu i : CH.sort(items, POSITION_SORTER)) {
			String replacement = mapping.get(i.getParentId());
			if (replacement != null)
				i.setParentId(replacement);
			else {
				i.setParentId(parentId);
				i.setPosition(position++);
			}
			addItem(i);
		}
		for (AmiWebCustomContextMenu i : CH.sort(items, POSITION_SORTER)) {
			AmiWebCustomContextMenu parent = this.getMenu(i.getParentId());
			parent.addChild(i);
			i.getAmiScript().recompileAmiscript();
			i.getFormulas().recompileAmiscript();
		}
	}

	public void init(List<Map<String, Object>> conf) {
		this.items.clear();
		this.items.put(root.getId(), root);
		for (Map<String, Object> i : conf) {
			AmiWebCustomContextMenu menu = new AmiWebCustomContextMenu(this, false);
			menu.init(i);
			addItem(menu);
		}
	}
	public AmiWebService getService() {
		return this.targetPortlet.getService();
	}
	public String getAmiLayoutFullAlias() {
		return this.targetPortlet.getAmiLayoutFullAlias();
	}
	public AmiWebDomObject getTargetPortlet() {
		return this.targetPortlet;
	}

	public AmiWebCustomContextMenu getMenu(String id) {
		return CH.getOrThrow(this.items, id);
	}

	public List<AmiWebCustomContextMenu> getChildren(boolean includeMe) {
		return this.root.getChildrenNested(includeMe, new ArrayList<AmiWebCustomContextMenu>());
	}

	public AmiWebCustomContextMenu getMenuNoThrow(String id) {
		return this.items.get(id);
	}

	public BasicWebMenu generateCustomizeMenu() {
		BasicWebMenu sink2 = new BasicWebMenu("Custom Menus", true);
		BasicWebMenu editMenu = new BasicWebMenu("Edit Menu Item", true, new ArrayList<WebMenuItem>());
		BasicWebMenu deleteMenu = new BasicWebMenu("Delete Menu Item", true, new ArrayList<WebMenuItem>());
		BasicWebMenu arrangeMenu = new BasicWebMenu("Arrange Menu Items", true, new ArrayList<WebMenuItem>());
		BasicWebMenu exportMenu = new BasicWebMenu("Export Menu Items", true, new ArrayList<WebMenuItem>());
		StringBuilder sink = new StringBuilder();
		for (AmiWebCustomContextMenu child : this.getChildren(true)) {
			String path = child.getPathDescription(SH.clear(sink)).toString();
			String id = child.getId();
			if (!child.isRoot()) {
				editMenu.add(new BasicWebMenuLink(path, true, CUSTOM_MENU_ACTION_EDIT + id));
				deleteMenu.add(new BasicWebMenuLink(path, true, CUSTOM_MENU_ACTION_DELETE + id));
			}
			if (child.getChildrenCount() > 1)
				arrangeMenu.add(new BasicWebMenuLink(path, true, CUSTOM_MENU_ACTION_ARRANGE + id));
			exportMenu.add(new BasicWebMenuLink(path, true, CUSTOM_MENU_ACTION_EXPORT + id));
		}
		sink2.add(new BasicWebMenuLink("Add Menu Item", true, ACTION_MENU_ADD));
		sink2.add(new BasicWebMenuLink("Import Menu(s)", true, CUSTOM_MENU_ACTION_IMPORT));
		if (editMenu.getChildrenCount() > 0)
			sink2.add(editMenu);
		if (deleteMenu.getChildrenCount() > 0)
			sink2.add(deleteMenu);
		if (arrangeMenu.getChildrenCount() > 0)
			sink2.add(arrangeMenu);
		if (exportMenu.getChildrenCount() > 0)
			sink2.add(exportMenu);
		return sink2;

	}

	public boolean handleCustomizeCallback(String id) {
		PortletManager pm = targetPortlet.getService().getPortletManager();
		if (SH.startsWith(id, CUSTOM_MENU_ACTION_DELETE)) {
			String itemId = SH.stripPrefix(id, CUSTOM_MENU_ACTION_DELETE, false);
			AmiWebCustomContextMenu toDelete = this.getMenu(itemId);
			String toDeleteId = toDelete.getId();
			FormPortletCheckboxField field;
			if (toDelete.getChildrenCount() > 0) {
				field = new FormPortletCheckboxField("Delete children too: ");
				field.setLeftPosPct(.50).setTopPosPx(20);
				field.setLabelWidthPx(300);
			} else
				field = null;
			ConfirmDialogPortlet dialog = new ConfirmDialogPortlet(generateConfig(), "Are you sure you want to delete custom menu item " + toDeleteId + "?",
					ConfirmDialogPortlet.TYPE_YES_NO, this, field);
			dialog.setCallback("confirm_menu_delete");
			dialog.setCorrelationData(itemId);
			dialog.updateButton(ConfirmDialogPortlet.ID_YES, "Delete");
			pm.showDialog("Confirm Delete", dialog);
			return true;
		} else if (SH.startsWith(id, CUSTOM_MENU_ACTION_EDIT)) {
			String itemId = SH.stripPrefix(id, CUSTOM_MENU_ACTION_EDIT, false);
			String ari = this.getMenu(itemId).getAri();
			this.getService().getAmiWebCustomContextMenuEditorsManager().showEditor(ari);
			return true;
		} else if (SH.startsWith(id, CUSTOM_MENU_ACTION_ARRANGE)) {
			String itemId = SH.stripPrefix(id, CUSTOM_MENU_ACTION_ARRANGE, false);
			pm.showDialog("Arrange Menu Items", new AmiWebArrangeCustomContextMenuItemsPortlet(generateConfig(), this, this.getMenu(itemId)));
			return true;
		} else if (SH.startsWith(id, CUSTOM_MENU_ACTION_EXPORT)) {
			String itemId = SH.stripPrefix(id, CUSTOM_MENU_ACTION_EXPORT, false);
			pm.showDialog("Export/Import Custom Menu", new AmiWebCustomContextMenuExportPortlet(generateConfig(), this.getMenu(itemId)));
			return true;
		} else if (SH.startsWith(id, CUSTOM_MENU_ACTION_IMPORT)) {
			pm.showDialog("Export/Import Custom Menu", new AmiWebCustomContextMenuImportPortlet(generateConfig(), this));
			return true;
		} else if (ACTION_MENU_ADD.equals(id)) {
			this.getService().getAmiWebCustomContextMenuEditorsManager().addEditor(this);
			return true;
		} else
			return false;
	}

	private PortletConfig generateConfig() {
		return targetPortlet.getService().getPortletManager().generateConfig();
	}
	public boolean processCustomContextMenuAction(String action) {
		if (!SH.startsWith(action, CUSTOM_MENU_ACTION_PREFIX))
			return false;
		String id = SH.stripPrefix(action, CUSTOM_MENU_ACTION_PREFIX, false);
		getMenu(id).processMenuAction();
		return true;
	}
	public boolean isCustomContextMenuAction(String action) {
		return SH.startsWith(action, CUSTOM_MENU_ACTION_PREFIX);
	}
	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if (ConfirmDialogPortlet.ID_YES.equals(id)) {
			if ("confirm_menu_delete".equals(source.getCallback())) {
				boolean deleteChildren = Boolean.TRUE.equals(source.getInputFieldValue());
				AmiWebCustomContextMenu item = getMenu((String) source.getCorrelationData());
				if (!deleteChildren) {
					AmiWebCustomContextMenu parent = item.getParent();
					List<AmiWebCustomContextMenu> children = CH.l(item.getChildren());
					this.items.remove(item.getId());
					this.targetPortlet.getService().getDomObjectsManager().fireRemoved(item);
					int pos = item.getPosition();
					parent.removeChild(item.getId());
					for (AmiWebCustomContextMenu i : children) {
						i.setParentId(parent.getId());
						i.setPosition(pos++);
						parent.addChild(i);
					}
					item.close();
				} else {
					this.removeMenu(item);
				}
			}
		}
		return true;
	}

	void removeMenu(AmiWebCustomContextMenu item) {
		for (AmiWebCustomContextMenu i : CH.l(item.getChildren()))
			removeMenu(i);
		this.items.remove(item.getId());
		this.targetPortlet.getService().getDomObjectsManager().fireRemoved(item);
		item.getParent().removeChild(item.getId());
		item.close();
	}

	public WebMenu generateMenu() {
		return root.generateMenu(CUSTOM_MENU_ACTION_PREFIX);
	}

	public void updateChild(String oldParentId, String oldId, AmiWebCustomContextMenu item) {
		String id = item.getId();
		if (OH.ne(item.getParentId(), oldParentId) || OH.ne(oldId, id)) {
			getMenu(oldParentId).removeChild(oldId);
			AmiWebCustomContextMenu newParent = getMenu(item.getParentId());
			newParent.addChild(item);
			if (OH.ne(oldId, id)) {
				this.items.remove(oldId);
				this.items.put(id, item);
				for (AmiWebCustomContextMenu i : item.getChildren())
					i.setParentId(id);
				this.targetPortlet.getService().getDomObjectsManager().fireAriChanged(item, oldId);
			}
		} else {
			AmiWebCustomContextMenu parent = item.getParent();
			parent.removeChild(item.getId());
			parent.addChild(item);
		}
	}

	public void addChild(AmiWebCustomContextMenu item) {
		AmiWebCustomContextMenu parent = getMenu(item.getParentId());
		parent.addChild(item);
		item.setParent(parent);
		addItem(item);
		this.targetPortlet.getService().getDomObjectsManager().fireAdded(item);
	}

	public String generateNextId(String string) {
		return SH.getNextId(string, this.items.keySet(), 2);
	}

	public void onInitDone() {
		for (AmiWebCustomContextMenu menu : this.items.values()) {
			menu.onInitDone();
		}
	}
	public void close() {
		for (AmiWebCustomContextMenu menu : this.items.values()) {
			menu.close();
		}
	}

	public void onPanelAriChanged() {
		for (AmiWebCustomContextMenu i : this.items.values()) {
			i.updateAri();
		}

	}

	//	public void recompileAmiscript() {
	//		root.recompileAmiscript();
	//	}

}
