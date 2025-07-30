package com.f1.ami.web.scm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.TabManager;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
import com.f1.utils.SH;

public class AmiWebScmEditorTabsPortlet extends GridPortlet implements TabManager, ConfirmDialogListener {
	private TabPortlet editorTabs;
	private Map<String, AmiWebScmEditorPortlet> editors = new HashMap<String, AmiWebScmEditorPortlet>();
	private AmiWebScmBasePortlet owner;

	public AmiWebScmEditorTabsPortlet(PortletConfig config, AmiWebScmBasePortlet owner) {
		super(config);
		this.owner = owner;
		this.editorTabs = new TabPortlet(generateConfig());
		this.editorTabs.setIsCustomizable(false);
		this.editorTabs.getTabPortletStyle().setHasMenuAlways(true);

		addChild(this.editorTabs, 0, 0);

		this.editorTabs.setTabManager(this);
	}
	public AmiWebScmEditorPortlet getEditor(String localName) {
		return this.editors.get(localName);
	}
	public void saveFiles(List<String> files) {
		for (String file : files) {
			AmiWebScmEditorPortlet editor = this.editors.get(file);
			if (editor != null && editor.isInEdit())
				editor.save();
		}
	}
	public void reloadFiles(List<String> files) {
		for (String file : files) {
			AmiWebScmEditorPortlet editor = this.editors.get(file);
			if (editor != null) {
				editor.reloadFromDisk();
			}
		}

	}
	public void showTab(String name, String fileString, byte statusType) {
		AmiWebScmEditorPortlet editor = this.editors.get(name);
		if (editor != null)
			this.editorTabs.selectTab(this.editorTabs.getTabForPortlet(editor).getLocation());
		else {
			editor = new AmiWebScmEditorPortlet(generateConfig(), owner, name, name, fileString, statusType);
			addTab(editor);
		}

	}
	private void addTab(AmiWebScmEditorPortlet editor) {
		this.getManager().onPortletAdded(editor);
		String name = editor.getName();
		this.editorTabs.addChild(SH.afterLast(name, '/', name), editor);
		this.editors.put(name, editor);
		this.editorTabs.setActiveTab(editor);
		editor.setTab(this.editorTabs.getTabForPortlet(editor));
	}
	private void closeTab(Tab tab) {
		this.editorTabs.removeTab(tab);
		AmiWebScmEditorPortlet editor = (AmiWebScmEditorPortlet) tab.getPortlet();
		editor.close();
		this.editors.remove(editor.getName());
	}
	@Override
	public void onUserMenu(TabPortlet tabPortlet, Tab tab, String menuId) {
		if (SH.equals("close", menuId)) {
			AmiWebScmEditorPortlet editor = (AmiWebScmEditorPortlet) tab.getPortlet();
			if (editor.isInEdit()) {
				getManager().showDialog(
						"Confirm",
						new ConfirmDialogPortlet(generateConfig(), "Save Changes?", ConfirmDialogPortlet.TYPE_YES_NO, this).setCallback("SAVE_AND_CLOSE_TAB")
								.addButton("CANCEL", "Cancel").setCorrelationData(tab));
			} else {
				closeTab(tab);
			}
		} else if (SH.equals("close_all_unmodified", menuId)) {
			for (AmiWebScmEditorPortlet editor : this.editors.values()) {
				if (editor.isInEdit()) {

				} else {
					closeTab(tab);
				}
			}
		} else if (SH.equals("save", menuId)) {
			AmiWebScmEditorPortlet editor = (AmiWebScmEditorPortlet) tab.getPortlet();
			editor.save();
		} else if (SH.equals("revert", menuId)) {
			AmiWebScmEditorPortlet editor = (AmiWebScmEditorPortlet) tab.getPortlet();
			editor.reloadFromDisk();
		}

	}
	@Override
	public WebMenu createMenu(TabPortlet tabPortlet, Tab tab) {
		AmiWebScmEditorPortlet editor = (AmiWebScmEditorPortlet) tab.getPortlet();
		BasicWebMenu r = new BasicWebMenu();
		if (editor.isInEdit())
			r.addChild(new BasicWebMenuLink("Save", true, "save"));
		r.addChild(new BasicWebMenuLink("Close", true, "close"));
		if (editors.size() > 1) {
			r.addChild(new BasicWebMenuLink("Close All Unmodified", true, "close_all_unmodified"));
		}
		if (editor.isInEdit())
			r.addChild(new BasicWebMenuLink("Revert", true, "revert"));
		return r;
	}

	@Override
	public void onUserAddTab(TabPortlet tabPortlet) {
	}

	@Override
	public void onUserRenamedTab(TabPortlet tabPortlet, Tab tab, String newName) {
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if ("SAVE_AND_CLOSE_TAB".equals(source.getCallback())) {
			Tab tab = (Tab) source.getCorrelationData();
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				AmiWebScmEditorPortlet editor = (AmiWebScmEditorPortlet) tab.getPortlet();
				editor.save();
				closeTab(tab);
			} else if (ConfirmDialogPortlet.ID_NO.equals(id)) {
				closeTab(tab);
			} else if ("CANCEL".equals(id)) {
				//nothing to do
			}
		}
		return true;
	}

}
