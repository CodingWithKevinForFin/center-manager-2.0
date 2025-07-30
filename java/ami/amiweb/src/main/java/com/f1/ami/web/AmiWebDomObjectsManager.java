package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.ami.web.charts.AmiWebChartAxisPortlet;
import com.f1.ami.web.charts.AmiWebChartEditAxisPortlet;
import com.f1.ami.web.charts.AmiWebChartEditLayersPortlet;
import com.f1.ami.web.charts.AmiWebChartPlotPortlet;
import com.f1.ami.web.charts.AmiWebChartRenderingLayer;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.ami.web.dm.portlets.AmiWebAddPanelPortlet;
import com.f1.ami.web.dm.portlets.AmiWebDmAddLinkPortlet;
import com.f1.ami.web.form.AmiWebButton;
import com.f1.ami.web.form.AmiWebQueryFieldWizardPortlet;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.EditWebButtonForm;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.menu.AmiWebCustomContextMenuSettingsPortlet;
import com.f1.ami.web.tree.AmiWebTreeColumn;
import com.f1.ami.web.tree.AmiWebTreeEditColumnPortlet;
import com.f1.ami.web.tree.AmiWebTreeEditGroupingsPortlet;
import com.f1.ami.web.tree.AmiWebTreeGroupBy;
import com.f1.ami.web.tree.AmiWebTreePortlet;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletManager;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.BasicMultiMap;

public class AmiWebDomObjectsManager {
	private static final Logger log = LH.get();
	private static final boolean FINE_LOGGING = log.isLoggable(Level.FINE);
	final private AmiWebService service;
	final private BasicMultiMap.List<String, AmiWebDomObjectDependency> dependencies = new BasicMultiMap.List<String, AmiWebDomObjectDependency>();
	final private HashMap<String, AmiWebDomObject> managedDomObjects = new HashMap<String, AmiWebDomObject>(); // Ari to DomObject
	final private List<AmiWebDomObjectDependency> globalListeners = new ArrayList<AmiWebDomObjectDependency>();

	//TODO: Remove once callbacks are added and cleaned up properly
	final private Set<String> callbacksIds = new HashSet<String>();

	public void initVariables() {
		//Go through global listeners
		if (CH.isntEmpty(this.globalListeners)) {
			for (int i = 0; i < this.globalListeners.size(); i++) {
				try {
					this.globalListeners.get(i).initLinkedVariables();
				} catch (Exception e) {
					LH.warning(log, "Error with initializing the callback with " + this.globalListeners.get(i), e);
				}
			}
		}

	};

	public void addCallback(AmiWebAmiScriptCallback cb) {
		String hashCode = Integer.toHexString(cb.hashCode());
		if (!callbacksIds.contains(hashCode)) {
			if (FINE_LOGGING)
				LH.fine(log, "CALLBACK ADDED : Count " + (callbacksIds.size() + 1) + " [+] " + cb + " " + cb.getCallbackType() + " " + cb.getName() + " ",
						Thread.currentThread().getStackTrace()[4].getClassName());
			callbacksIds.add(hashCode);
		} else
			LH.warning(log, "CALLBACK ALREADY ADDED : Count " + (callbacksIds.size()) + " " + hashCode);
	}
	public void removeCallback(AmiWebAmiScriptCallback cb) {
		String hashCode = Integer.toHexString(cb.hashCode());
		if (callbacksIds.contains(hashCode)) {
			if (FINE_LOGGING)
				LH.fine(log, "CALLBACK REMOVED : Count " + (callbacksIds.size() - 1) + " [-] " + cb + " " + cb.getCallbackType() + " " + cb.getName());
			callbacksIds.remove(hashCode);
		} else
			LH.warning(log, "CALLBACK ALREADY REMOVED : Count " + (callbacksIds.size() + " " + hashCode));
	}

	public AmiWebDomObjectsManager(AmiWebService amiWebService) {
		this.service = amiWebService;
	}
	public AmiWebDomObject getManagedDomObject(String ari) {
		return this.managedDomObjects.get(ari);
	}

	public boolean isManaged(String ari) {
		return this.managedDomObjects.containsKey(ari);
	}

	public void addManagedDomObject(AmiWebDomObject object) {
		String ari = object.getAri();
		if (ari == null) {
			LH.warning(log, "Adding Managed Dom Object (Failed): " + object.getAriType() + " : " + ari);
			return;
		} else if (this.managedDomObjects.containsKey(ari)) {
			LH.warning(log, "Adding Managed Dom Object with duplicate ari [++]: " + (managedDomObjects.size()) + " : " + object.getAriType() + " : " + ari);
			this.managedDomObjects.put(ari, object);
		} else {
			if (FINE_LOGGING)
				LH.fine(log, "Adding Managed Dom Object Count [+]: " + (managedDomObjects.size() + 1) + " : " + object.getAriType() + " : " + ari);
			this.managedDomObjects.put(ari, object);
		}
	}

	public void removeManagedDomObject(AmiWebDomObject object) {
		String ari = object.getAri();
		if (ari == null) {
			LH.warning(log, "Removing Managed Dom Object (Failed): " + object.getAriType() + " : " + ari);
			return;
		} else if (!this.managedDomObjects.containsKey(ari)) {
			LH.warning(log, "Removing Managed Dom Object - Object is not managed: " + object.getAriType() + " : " + ari);
		} else {
			if (FINE_LOGGING)
				LH.fine(log, "Removing Managed Dom Object Count [-]: " + (managedDomObjects.size() - 1) + " : " + object.getAri() + " : " + ari);
			this.managedDomObjects.remove(ari);
		}
	}

	public void removeManagedDomObjectFullAri(String ari) {
		if (ari == null) {
			LH.warning(log, "Removing Managed Dom Object (Failed): " + ari);
			return;
		} else if (!this.managedDomObjects.containsKey(ari)) {
			LH.warning(log, "Removing Managed Dom Object - Object is not managed: " + " : " + ari);
		}
		if (FINE_LOGGING)
			LH.fine(log, "Removing Managed Dom Object Count [-]: " + (managedDomObjects.size() - 1) + " : " + ari);
		this.managedDomObjects.remove(ari);
	}

	public void fireAriChanged(AmiWebDomObject object, String oldAri) {
		if (!this.managedDomObjects.containsKey(oldAri)) {
			if (FINE_LOGGING)
				LH.fine(log, "Ari Changed (unmanaged) ", oldAri, " ==> ", object.getAri());
		} else {
			this.removeManagedDomObjectFullAri(oldAri);
			this.addManagedDomObject(object);
		}
		String newAri = object.getAri();
		if (oldAri == null) {
			if (newAri != null)
				fireAdded(object);
		} else {
			if (SH.equals(oldAri, newAri))
				return;
			if (FINE_LOGGING)
				LH.fine(log, "Ari Changed ", oldAri, " ==> ", newAri);
			fireAriChanged(globalListeners, object, oldAri);
			fireAriChanged(dependencies.get(newAri), object, oldAri);
			fireAriChanged(dependencies.get(oldAri), object, oldAri);
		}
	}
	public void fireAdded(AmiWebDomObject object) {
		if (!this.managedDomObjects.containsKey(object.getAri())) {
			if (FINE_LOGGING)
				LH.fine(log, "Ari Added (unmanaged) ", object.getAri());
			return;
		}
		if (FINE_LOGGING)
			LH.fine(log, "Ari Added ", object.getAri());
		fireAdded(globalListeners, object);
		fireAdded(dependencies.get(object.getAri()), object);
	}
	public void fireEvent(AmiWebDomObject object, byte event) {
		if (!this.managedDomObjects.containsKey(object.getAri())) {
			if (FINE_LOGGING)
				LH.fine(log, "Event (unmanaged) ", object.getAri(), " ==> ", event);
			return;
		}
		if (FINE_LOGGING)
			LH.fine(log, "Event ", object.getAri(), " ==> ", event);
		fireEvent(globalListeners, object, event);
		fireEvent(dependencies.get(object.getAri()), object, event);
	}
	public void fireRemoved(AmiWebDomObject object) {
		if (!this.managedDomObjects.containsKey(object.getAri())) {
			if (FINE_LOGGING)
				LH.fine(log, "Removed (unmanaged) ", object.getAriType(), " ", object.getAri());
			return;
		}
		if (object == null || object.getAri() == null)
			return;
		if (FINE_LOGGING)
			LH.fine(log, "Removed ", object.getAri());
		fireRemoved(globalListeners, object);
		fireRemoved(dependencies.get(object.getAri()), object);
	}

	private void fireAriChanged(List<AmiWebDomObjectDependency> listeners, AmiWebDomObject object, String oldAri) {
		if (CH.isntEmpty(listeners)) {
			for (AmiWebDomObjectDependency listener : toArray(listeners)) {
				try {
					listener.onDomObjectAriChanged(object, oldAri);
				} catch (Exception e) {
					LH.warning(log, "Error with on change with " + listener + " for " + object, e);
				}
			}
		}
	}

	private void fireAdded(List<AmiWebDomObjectDependency> listeners, AmiWebDomObject object) {
		if (CH.isntEmpty(listeners)) {
			listeners = new ArrayList<AmiWebDomObjectDependency>(listeners);
			for (AmiWebDomObjectDependency listener : toArray(listeners)) {
				try {
					listener.onDomObjectAdded(object);
				} catch (Exception e) {
					LH.warning(log, "Error with on add with " + listener + " for " + object, e);
				}
			}
		}
	}
	private void fireEvent(List<AmiWebDomObjectDependency> listeners, AmiWebDomObject object, byte event) {
		if (CH.isntEmpty(listeners)) {
			for (AmiWebDomObjectDependency listener : toArray(listeners)) {
				try {
					listener.onDomObjectEvent(object, event);
				} catch (Exception e) {
					LH.warning(log, "Error with on event with " + listener + " for " + object, e);
				}
			}
		}
	}
	private void fireRemoved(List<AmiWebDomObjectDependency> listeners, AmiWebDomObject object) {
		if (CH.isntEmpty(listeners)) {
			for (AmiWebDomObjectDependency listener : toArray(listeners)) {
				try {
					listener.onDomObjectRemoved(object);
				} catch (Exception e) {
					LH.warning(log, "Error with on remove with " + listener + " for " + object, e);
				}
			}
		}
	}
	public void addGlobalListener(AmiWebDomObjectDependency depenency) {
		if (FINE_LOGGING)
			LH.fine(log, "Adding global listener: Count " + (this.globalListeners.size() + 1) + " [+] " + depenency);
		CH.addIdentityOrThrow(this.globalListeners, depenency);
	}
	public void removeGlobalListener(AmiWebDomObjectDependency depenency) {
		if (FINE_LOGGING)
			LH.fine(log, "Removing global listener: Count " + (this.globalListeners.size() - 1) + " [-] " + depenency);
		CH.removeOrThrow(this.globalListeners, depenency);
	}

	public void addDomObjectDependency(String ari, AmiWebDomObjectDependency depenency) {
		this.dependencies.putMulti(ari, depenency);
		if (FINE_LOGGING)
			LH.fine(log, "Adding Dependency listener listener: " + ari + " [+] " + this.dependencies.get(ari).size());
	}
	public void removeDomObjectDependency(String ari, AmiWebDomObjectDependency depenency) {
		List<AmiWebDomObjectDependency> t = this.dependencies.get(ari);
		if (FINE_LOGGING)
			LH.fine(log, "Removing Dependency listener listener: " + ari + " [-] " + (t == null ? "missing" : (t.size() - 1)));
		this.dependencies.removeMultiAndKeyIfEmpty(ari, depenency);
	}

	public List<AmiWebDomObjectDependency> getDependencies(String ari) {
		List<AmiWebDomObjectDependency> dep = dependencies.get(ari);
		return dep == null ? Collections.EMPTY_LIST : dep;
	}

	public void clear() {
		this.dependencies.clear();
		this.globalListeners.clear();
		this.callbacksIds.clear();
		this.managedDomObjects.clear();
	}

	public AmiWebService getService() {
		return this.service;
	}
	public Portlet showFormulaEditor(String ari, String formula) {
		AmiWebDomObject r = AmiWebAmiObjectsVariablesHelper.getAmiWebDomObjectFromFullAri(ari, this.service);
		PortletManager manager = service.getPortletManager();
		if (r == null)
			return null;
		String ariType = r.getAriType();
		byte type = AmiWebAmiObjectsVariablesHelper.parseType(ariType);
		switch (type) {
			case AmiWebDomObject.ARI_CODE_FIELD: {
				AmiWebQueryFieldWizardPortlet wiz = service.getAmiQueryFormEditorsManager().showEditExistingFieldEditor((AmiWebQueryFormPortlet) r.getParentDomObject(),
						(QueryField<?>) r);
				if (wiz != null)
					PortletHelper.ensureVisible(wiz);
				return wiz;
			}
			case AmiWebDomObject.ARI_CODE_MENUITEM: {
				AmiWebCustomContextMenuSettingsPortlet wiz = service.getAmiWebCustomContextMenuEditorsManager().showEditor(ari);
				if (wiz != null)
					PortletHelper.ensureVisible(wiz);
				wiz.showFormulasEditor();
				return wiz;
			}
			case AmiWebDomObject.ARI_CODE_PANEL: {
				AmiWebPortlet p = (AmiWebPortlet) r;
				return p.showSettingsPortlet();
			}
			case AmiWebDomObject.ARI_CODE_RELATIONSHIP: {
				AmiWebDmAddLinkPortlet le = service.getDesktop().getLinkHelper().showEditRelationship((AmiWebDmLink) r);
				PortletHelper.ensureVisible(le);
				return le;
			}
			case AmiWebDomObject.ARI_CODE_FORMBUTTON: {
				AmiWebButton button = (AmiWebButton) r;
				AmiWebQueryFormPortlet portlet = (AmiWebQueryFormPortlet) button.getParentDomObject();
				EditWebButtonForm be = portlet.showEditButtonPortlet(button.getId(), true);
				PortletHelper.ensureVisible(be);
				return be;
			}
			case AmiWebDomObject.ARI_CODE_COLUMN: {
				if (r instanceof AmiWebCustomColumn) {
					AmiWebCustomColumn col = (AmiWebCustomColumn) r;
					AmiWebAbstractTablePortlet table = col.getTable();
					int pos = table.getTable().getColumnPosition(col.getColumnId());
					AmiWebAddObjectColumnFormPortlet addAmiObjectPortlet = col.getTable().newAddAmiObjectColumnFormPortlet(table.generateConfig(), table, pos, col);
					if (addAmiObjectPortlet != null)
						table.getManager().showDialog(addAmiObjectPortlet.getTitle(), addAmiObjectPortlet);
					return addAmiObjectPortlet;
				} else if (r instanceof AmiWebTreeColumn) {
					AmiWebTreeColumn col = (AmiWebTreeColumn) r;
					AmiWebTreePortlet tree = col.getTree();
					int pos = tree.getTree().getColumnPosition(col.getColumnId());
					AmiWebTreeEditColumnPortlet t = new AmiWebTreeEditColumnPortlet(tree, col, pos, false);
					manager.showDialog(t.getTitle(), t);
					return t;
				} else
					return null;
			}
			case AmiWebDomObject.ARI_CODE_GROUPING: {
				AmiWebTreeGroupBy groupBy = (AmiWebTreeGroupBy) r;
				AmiWebTreePortlet tree = groupBy.getTree();
				AmiWebTreeEditGroupingsPortlet t = new AmiWebTreeEditGroupingsPortlet(manager.generateConfig(), tree);
				manager.showDialog("Treegrid Settings", t);
				return t.showEditor(groupBy);
			}
			case AmiWebDomObject.ARI_CODE_CHART_PLOT: {
				AmiWebChartPlotPortlet plot = (AmiWebChartPlotPortlet) r;
				AmiWebChartEditLayersPortlet p = new AmiWebChartEditLayersPortlet(manager.generateConfig(), plot);
				service.getPortletManager().showDialog("Edit Rendering Layer", p).setShadeOutside(false);
				return p;
			}
			case AmiWebDomObject.ARI_CODE_TAB_ENTRY: {
				AmiWebTabEntry tabEntry = (AmiWebTabEntry) r;
				AmiWebTabEntrySettingsPortlet p = new AmiWebTabEntrySettingsPortlet(manager.generateConfig(), tabEntry.getOwner(), tabEntry);
				manager.showDialog("Tab Settings", p);
				return p;
			}
			case AmiWebDomObject.ARI_CODE_CHART_AXIS: {
				AmiWebChartAxisPortlet axis = (AmiWebChartAxisPortlet) r;
				AmiWebChartEditAxisPortlet p = new AmiWebChartEditAxisPortlet(manager.generateConfig(), axis);
				manager.showDialog("Edit Axis", p).setShadeOutside(false);
				return p;
			}
			case AmiWebDomObject.ARI_CODE_CHART_LAYER: {
				AmiWebChartRenderingLayer layer = (AmiWebChartRenderingLayer) r;
				AmiWebChartEditLayersPortlet p = new AmiWebChartEditLayersPortlet(manager.generateConfig(), layer.getPlot());
				service.getPortletManager().showDialog("Edit Rendering Layer", p).setShadeOutside(false);
				p.setActiveLayer(layer.getName());
				return p;
			}
			case AmiWebDomObject.ARI_CODE_LAYOUT:
			case AmiWebDomObject.ARI_CODE_PROCESSOR:
			case AmiWebDomObject.ARI_CODE_SESSION:
			case AmiWebDomObject.ARI_CODE_FIELD_VALUE:
			case AmiWebDomObject.ARI_CODE_DATAMODEL:
			default:
				return null;
		}
	}
	public AmiWebEditAmiScriptCallbackPortlet showCallbackEditor(String ari, String callback) {
		AmiWebDomObject r = AmiWebAmiObjectsVariablesHelper.getAmiWebDomObjectFromFullAri(ari, this.service);
		AmiWebEditAmiScriptCallbacksPortlet editor = null;
		Portlet settingsEditor = null;
		if (r != null) {
			String ariType = r.getAriType();
			byte type = AmiWebAmiObjectsVariablesHelper.parseType(ariType);
			switch (type) {
				case AmiWebDomObject.ARI_CODE_DATAMODEL: {
					AmiWebDmsImpl dms = (AmiWebDmsImpl) r;
					AmiWebAddPanelPortlet t = AmiWebUtils.showEditDmPortlet(service, dms, "Edit Datamodel");
					settingsEditor = t;
					if (t != null)
						editor = t.getCallbacksEditor();
					break;
				}
				case AmiWebDomObject.ARI_CODE_FIELD: {
					AmiWebQueryFieldWizardPortlet wiz = service.getAmiQueryFormEditorsManager().showEditExistingFieldEditor((AmiWebQueryFormPortlet) r.getParentDomObject(),
							(QueryField<?>) r);
					if (wiz != null) {
						settingsEditor = wiz;
						editor = wiz.getCallbacksEditor();
					}
					break;
				}
				case AmiWebDomObject.ARI_CODE_MENUITEM: {
					AmiWebCustomContextMenuSettingsPortlet wiz = service.getAmiWebCustomContextMenuEditorsManager().showEditor(ari);
					if (wiz != null) {
						settingsEditor = wiz;
						editor = wiz.getCallbacksEditor();
					}
					break;
				}
				case AmiWebDomObject.ARI_CODE_PANEL: {
					AmiWebPortlet p = (AmiWebPortlet) r;
					AmiWebAmiScriptCallbacks callbacks = p.getAmiScriptCallbacks();
					settingsEditor = p;
					AmiWebEditAmiScriptCallbackDialogPortlet editor2 = service.getAmiWebCallbackEditorsManager().showEditDmPortlet(service, callbacks);
					editor = editor2.getCallbacksEditor();
					break;
				}
				case AmiWebDomObject.ARI_CODE_LAYOUT: {
					AmiWebEditCustomCallbacksPortlet cc = service.getDesktop().showCustomCallbacksPortlet();
					settingsEditor = cc;
					editor = cc.getCallbacksEditor(SH.afterFirst(ari, ':'));
					break;
				}
				case AmiWebDomObject.ARI_CODE_RELATIONSHIP: {
					AmiWebDmAddLinkPortlet le = service.getDesktop().getLinkHelper().showEditRelationship((AmiWebDmLink) r);
					settingsEditor = le;
					editor = le.getCallbacksEditor();
					break;
				}
				case AmiWebDomObject.ARI_CODE_FORMBUTTON: {
					AmiWebButton button = (AmiWebButton) r;
					AmiWebQueryFormPortlet portlet = (AmiWebQueryFormPortlet) button.getParentDomObject();
					EditWebButtonForm be = portlet.showEditButtonPortlet(button.getId(), true);
					settingsEditor = be;
					editor = be.getCallbacksEditor();
					break;
				}
				case AmiWebDomObject.ARI_CODE_COLUMN:
				case AmiWebDomObject.ARI_CODE_GROUPING:
				case AmiWebDomObject.ARI_CODE_CHART_PLOT:
				case AmiWebDomObject.ARI_CODE_TAB_ENTRY:
				case AmiWebDomObject.ARI_CODE_CHART_AXIS:
				case AmiWebDomObject.ARI_CODE_CHART_LAYER:
				case AmiWebDomObject.ARI_CODE_PROCESSOR:
				case AmiWebDomObject.ARI_CODE_SESSION:
				case AmiWebDomObject.ARI_CODE_FIELD_VALUE:
					break;
			}
		}
		if (editor != null && callback != null) {
			AmiWebEditAmiScriptCallbackPortlet r2 = editor.getCallbackEditor(callback);
			if (r2 != null) {
				PortletHelper.ensureVisible(r2);
				return r2;
			}
		}
		if (settingsEditor != null)
			PortletHelper.ensureVisible(settingsEditor);

		return null;
	}

	private AmiWebDomObjectDependency[] singleton = new AmiWebDomObjectDependency[1];

	private AmiWebDomObjectDependency[] toArray(List<AmiWebDomObjectDependency> listeners) {
		int size = listeners.size();
		if (size == 1) {
			singleton[0] = listeners.get(0);
			return singleton;
		}
		return listeners.toArray(new AmiWebDomObjectDependency[size]);
	}

	public Collection<AmiWebDomObject> getManagedDomObject() {
		return this.managedDomObjects.values();
	}

	public AmiWebFormula getFormula(String ari) {
		String prefix = SH.beforeLast(ari, AmiWebConsts.FORMULA_PREFIX_DELIM);
		String formulaId = SH.afterLast(ari, AmiWebConsts.FORMULA_PREFIX_DELIM);
		AmiWebDomObject o = getManagedDomObject(prefix);
		return o == null ? null : o.getFormulas().getFormula(formulaId);
	}
	public AmiWebAmiScriptCallback getCallback(String ari) {
		String prefix = SH.beforeLast(ari, AmiWebConsts.CALLBACK_PREFIX_DELIM);
		String callbackId = SH.afterLast(ari, AmiWebConsts.CALLBACK_PREFIX_DELIM);
		AmiWebDomObject o = getManagedDomObject(prefix);
		return o == null ? null : o.getAmiScriptCallbacks().getCallback(callbackId);
	}

}
