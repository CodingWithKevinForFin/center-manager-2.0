package com.f1.ami.web;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.TabManager;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.BasicMethodFactory;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;

public class AmiWebEditAmiScriptCallbacksPortlet extends GridPortlet implements TabManager, AmiWebCompilerListener {

	final private AmiWebService service;
	final private TabPortlet tabs;
	final private Map<String, AmiWebEditAmiScriptCallbackPortlet> scriptTabs = new LinkedHashMap<String, AmiWebEditAmiScriptCallbackPortlet>();
	private String layoutAlias;
	private AmiWebAmiScriptCallbacks callbacks;

	public AmiWebEditAmiScriptCallbacksPortlet(PortletConfig config, AmiWebAmiScriptCallbacks target) {
		super(config);
		this.service = AmiWebUtils.getService(config.getPortletManager());
		this.service.onAmiScriptEditorOpened(this);
		this.tabs = addChild(new TabPortlet(generateConfig()), 0, 0);
		this.tabs.setIsCustomizable(false);
		this.tabs.getTabPortletStyle().setTabPaddingTop(4);
		this.tabs.getTabPortletStyle().setTabPaddingBottom(0);
		this.tabs.getTabPortletStyle().setTabPaddingStart(0);
		this.tabs.getTabPortletStyle().setTabSpacing(4);
		this.tabs.getTabPortletStyle().setFontSize(14);
		this.tabs.getTabPortletStyle().setLeftRounding(3);
		this.tabs.getTabPortletStyle().setRightRounding(3);
		this.tabs.getTabPortletStyle().setTabHeight(22);
		this.tabs.getTabPortletStyle().setBackgroundColor("#AAAAAA");
		this.tabs.getTabPortletStyle().setHasMenuAlways(true);
		this.tabs.setTabManager(this);
		this.setRowSize(1, 40);
		this.service.addCompilerListener(this);
		setCallbacks(target);
	}

	public void setCallbacks(AmiWebAmiScriptCallbacks target) {
		if (target != null && target != this.callbacks && target.getThis() != null && target.getThis().isTransient()) {
			getManager().showAlert("You're editing a TRANSIENT object. This means your changes will not be saved in the layout");
		}
		this.callbacks = target;
		this.scriptTabs.clear();
		this.tabs.removeAndCloseAllChildren();
		if (target != null) {
			List<AmiWebEditAmiScriptCallbackPortlet> temp = new ArrayList<AmiWebEditAmiScriptCallbackPortlet>();
			this.layoutAlias = target.getAmiLayoutAlias();
			AmiWebScriptManagerForLayout sm = service.getScriptManager(this.layoutAlias);
			boolean isReadonly = this.callbacks.getThis().isTransient() || this.service.getLayoutFilesManager().getLayoutByFullAlias(layoutAlias).isReadonly();
			BasicMethodFactory amiScriptMethodFactory = sm.getMethodFactory();
			List<String> sorted = CH.sort(target.getAmiScriptCallbackDefinitions());
			for (int i = 0; i < sorted.size(); i++) {
				String s = sorted.get(i);
				AmiWebAmiScriptCallback callback = target.getCallback(s);
				// guaranteed to have called ensureCompiledInner
				AmiWebEditAmiScriptCallbackPortlet st = new AmiWebEditAmiScriptCallbackPortlet(generateConfig(), service, callback, this.layoutAlias, target.getThis());
				this.scriptTabs.put(s, st);
				byte lastCompiledResult = st.getCallback().getLastCompiledResult();
				if (lastCompiledResult != AmiWebAmiScriptCallback.COMPILE_NOCODE) {
					// add to tabs if this has some code (priority)
					String hover = (isReadonly ? " (<B>READONLY LAYOUT</B>) " : "") + callback.getParamsDef().toString(amiScriptMethodFactory);
					Tab t = this.tabs.addChild(s, st);
					t.setHover(hover);
					this.getManager().onPortletAdded(st);
					st.setOwningTab(t);
				} else {
					// otherwise put it to a list to add later (deferred)
					temp.add(st);
				}
			}

			for (AmiWebEditAmiScriptCallbackPortlet p : temp) {
				// add the rest of the callbacks
				String name = p.getName();
				AmiWebAmiScriptCallback callback = target.getCallback(name);
				String hover = (isReadonly ? " (<B>READONLY LAYOUT</B>) " : "") + callback.getParamsDef().toString(amiScriptMethodFactory);
				Tab t = this.tabs.addChild(name, p);
				t.setHover(hover);
				this.getManager().onPortletAdded(p);
				p.setOwningTab(t);
			}
		} else
			this.layoutAlias = null;
	}
	public AmiWebAmiScriptCallbacks getCallbacks() {
		return this.callbacks;
	}

	public void setActiveTab(String callback) {
		this.tabs.setActiveTab(this.scriptTabs.get(callback));
	}

	public void setCallback(String name, String string) {
		this.scriptTabs.get(name).setAmiScript(string);
	}

	public boolean hasChanged() {
		for (AmiWebEditAmiScriptCallbackPortlet i : this.scriptTabs.values()) {
			if (i.hasChanged()) {
				return true;
			}
		}
		return false;
	}
	public boolean hasPendingChanges() {
		for (AmiWebEditAmiScriptCallbackPortlet i : this.scriptTabs.values()) {
			if (i.isModified())
				return true;
		}
		return false;
	}

	public List<AmiWebEditAmiScriptCallbackPortlet> getPortletsWithPendingChanges() {
		List<AmiWebEditAmiScriptCallbackPortlet> pendingPorlets = new ArrayList<AmiWebEditAmiScriptCallbackPortlet>();
		for (AmiWebEditAmiScriptCallbackPortlet i : this.scriptTabs.values()) {
			if (i.isModified())
				pendingPorlets.add(i);
		}
		return pendingPorlets;
	}

	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		if (KeyEvent.ESCAPE.equals(keyEvent.getKey()))
			return true;
		return super.onUserKeyEvent(keyEvent);
	}

	//	public void setThis(AmiWebDomObject o) {
	//		for (AmiWebEditAmiScriptCallbackPortlet i : this.scriptTabs.values())
	//			i.setThis(o);
	//	}

	public AmiWebEditAmiScriptCallbackPortlet getCallbackEditor(String callbackOnprocess) {
		return this.scriptTabs.get(callbackOnprocess);
	}

	public boolean apply() {
		for (String i : this.scriptTabs.keySet()) {
			AmiWebEditAmiScriptCallbackPortlet editor = getCallbackEditor(i);
			if (!editor.apply()) {
				PortletHelper.ensureVisible(editor);
				return false;
			}
		}
		return true;
	}

	public boolean submitChanges() {
		if (hasChanged() == false)
			return true;
		for (String i : this.scriptTabs.keySet()) {
			AmiWebEditAmiScriptCallbackPortlet editor = getCallbackEditor(i);
			if (editor.hasChanged())
				if (!editor.submitChanges())
					return false;
		}
		return true;

	}

	public void exportTo(AmiWebAmiScriptCallbacks sink, AmiWebDmsImpl dm) {
		for (String i : this.scriptTabs.keySet()) {
			AmiWebAmiScriptCallback callback = sink.getCallback(i);
			if (callback != null)
				getCallbackEditor(i).exportTo(callback, dm);
		}
	}

	public boolean applyTo(AmiWebAmiScriptCallbacks sink, AmiWebDmsImpl dm) {
		if (!apply())
			return false;
		for (String i : this.scriptTabs.keySet()) {
			AmiWebEditAmiScriptCallbackPortlet editor = getCallbackEditor(i);
			if (!editor.applyTo(sink.getCallback(i), dm))
				return false;
		}
		return true;
	}

	public Set<String> getUsedDatasources() {
		Set<String> r = new HashSet<String>();
		for (AmiWebEditAmiScriptCallbackPortlet i : this.scriptTabs.values())
			r.addAll(i.getUsedDatasources());
		return r;
	}

	public void setRestrictedDatamodels(Set<String> sink) {
		for (AmiWebEditAmiScriptCallbackPortlet i : this.scriptTabs.values())
			i.setRestrictedDatamodels(sink);
	}

	@Override
	public void onUserMenu(TabPortlet tabPortlet, Tab tab, String menuId) {
		if (SH.equals("config", menuId)) {
			AmiWebEditAmiScriptCallbackPortlet editPortlet = (AmiWebEditAmiScriptCallbackPortlet) tab.getPortlet();

			AmiWebViewCallbackConfigurationPortlet viewConfig = new AmiWebViewCallbackConfigurationPortlet(generateConfig(), this, editPortlet);
			getManager().showDialog("Export/Import configuration", viewConfig);
		}
	}

	@Override
	public WebMenu createMenu(TabPortlet tabPortlet, Tab tab) {
		BasicWebMenu r = new BasicWebMenu();
		r.addChild(new BasicWebMenuLink("Export/Import", true, "config"));
		return r;
	}

	@Override
	public void onUserAddTab(TabPortlet tabPortlet) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserRenamedTab(TabPortlet tabPortlet, Tab tab, String newName) {
		// TODO Auto-generated method stub

	}
	public String getAmiLayoutAlias() {
		return this.layoutAlias;
	}
	public void setAmiLayoutAlias(String amiLayoutAlias) {
		this.layoutAlias = amiLayoutAlias;
		this.callbacks.setAmiLayoutAlias(amiLayoutAlias);
		for (AmiWebEditAmiScriptCallbackPortlet tab : this.scriptTabs.values()) {
			tab.setAmiLayoutAlias(amiLayoutAlias);
		}
	}

	public void onClosed() {
		for (AmiWebEditAmiScriptCallbackPortlet tab : this.scriptTabs.values()) {
			tab.close();
		}
		this.service.removeCompilerListener(this);
		this.service.onAmiScriptEditorClosed(this);
		super.onClosed();
	}

	public void addVariableTreeToDomManager() {
		for (AmiWebEditAmiScriptCallbackPortlet tab : this.scriptTabs.values()) {
			tab.addVariablesPortletToDomManager();
		}
	}
	public void removeVariableTreeFromDomManager() {
		for (AmiWebEditAmiScriptCallbackPortlet tab : this.scriptTabs.values()) {
			tab.removeVariablesPortletFromDomManager();
		}
	}

	public Iterable<AmiWebEditAmiScriptCallbackPortlet> getEditors() {
		return this.scriptTabs.values();
	}

	public void recompileAmiScript() {
		for (AmiWebEditAmiScriptCallbackPortlet i : this.scriptTabs.values())
			i.recompileAmiscript();
	}

	@Override
	public void onFormulaChanged(AmiWebFormula formula, DerivedCellCalculator old, DerivedCellCalculator nuw) {
	}

	@Override
	public void onRecompiled() {
		recompileAmiScript();
	}

	@Override
	public void onCallbackChanged(AmiWebAmiScriptCallback callback, DerivedCellCalculator old, DerivedCellCalculator nuw) {

	}

}
