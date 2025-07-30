package com.f1.ami.web.dm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebDmPortlet;
import com.f1.ami.web.AmiWebPanelsListener;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.utils.CH;
import com.f1.utils.LH;

public class AmiWebDmLayoutManagerImpl implements AmiWebDmManagerListener, AmiWebPanelsListener {

	private static final Logger log = LH.get();
	final private List<AmiWebDmLayoutListener> listeners = new ArrayList<AmiWebDmLayoutListener>();

	private Map<String, AmiWebPortlet> pt = new HashMap<String, AmiWebPortlet>();
	private AmiWebDmManager manager;
	private Set<String> visiblePanelIds = new HashSet<String>();
	private Set<String> visibleDmIds = new HashSet<String>();

	public AmiWebDmLayoutManagerImpl(AmiWebDmManager dmManager) {
		this.manager = dmManager;
	}

	public AmiWebDmManager getManager() {
		return this.manager;
	}

	public void clear() {
		this.pt.clear();
		this.visibleDmIds.clear();
		this.visiblePanelIds.clear();
	}

	private void fireVisibilityChanged(AmiWebDm node, boolean isNowVisible) {
		for (int i = 0, l = this.listeners.size(); i < l; i++)
			this.listeners.get(i).onVisibilityChanged(this, node, isNowVisible);
	}

	public void removeInvalidDatasourceByName(String dsName) {
	}

	///////////////////////
	// PORTLETS
	///////////////////////
	@Override
	public void onAmiWebPanelAdded(AmiWebPortlet portlet) {
		onAmiPortletLocationChanged(portlet);
	}
	@Override
	public void onAmiWebPanelRemoved(AmiWebPortlet portlet, boolean isHide) {
		onAmiPortletLocationChanged(portlet);
	}
	@Override
	public void onAmiWebPanelLocationChanged(AmiWebPortlet ap) {
		this.onAmiPortletLocationChanged(ap);
	}
	private void onAmiPortletLocationChanged(AmiWebPortlet p) {
		try {
			if (p instanceof AmiWebDmPortlet) {
				final String id = getId(p);
				if (id != null && (p.getVisible() ? visiblePanelIds.add(id) : visiblePanelIds.remove(id)))
					for (String dmid : ((AmiWebDmPortlet) p).getUsedDmAliasDotNames())
						onDmVisibilityMightHaveChanged(this.manager.getDmByAliasDotName(dmid));
			}
		} catch (Exception e) {
			LH.warning(log, this.manager.getService().getPortletManager().describeUser(), " Error: ", e);
		}
	}

	///////////////////////
	//DATAMODELS
	///////////////////////
	public static String getId(AmiWebDm object) {
		return object.getAmiLayoutFullAliasDotId();
	}
	public static String getId(AmiWebPortlet object) {
		return object.getAmiLayoutFullAliasDotId();
	}
	@Override
	public void onDmUpdated(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm dm) {
		onDmVisibilityMightHaveChanged(dm);
	}
	@Override
	public void onDmAdded(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm dm) {
		onDmVisibilityMightHaveChanged(dm);
	}
	@Override
	public void onDmRemoved(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm dm) {
		onDmVisibilityMightHaveChanged(dm);
	}

	@Override
	public void onDmDependencyAdded(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm upper, AmiWebDm lower) {
	}

	@Override
	public void onDmDependencyRemoved(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm upper, AmiWebDm lower) {
	}
	@Override
	public void onDmLinkAdded(AmiWebDmManager amiWebDmManagerImpl, AmiWebDmLink link) {
	}
	@Override
	public void onDmLinkRemoved(AmiWebDmManager amiWebDmManagerImpl, AmiWebDmLink link) {
	}
	@Override
	public void onDmManagerInitDone() {
	}

	public void addListener(AmiWebDmLayoutListener listener) {
		CH.addIdentityOrThrow(listeners, listener);
	}
	public void removeListener(AmiWebDmLayoutListener listener) {
		CH.removeOrThrow(listeners, listener);
	}
	private void onDmVisibilityMightHaveChanged(AmiWebDm dm) {
		if (dm == null)
			return;
		String dmid = getId(dm);
		boolean visible = dm.hasVisiblePortlet();
		if ((visible ? visibleDmIds.add(dmid) : visibleDmIds.remove(dmid))) {
			fireVisibilityChanged(dm, visible);
			for (String dmid2 : dm.getLowerDmAliasDotNames())
				onDmVisibilityMightHaveChanged(this.manager.getDmByAliasDotName(dmid2));
		}

	}
	@Override
	public void onDmDependencyAdded(AmiWebDmManager manager, AmiWebDmPortlet target, String aliasDotName, String tableName) {
	}
	@Override
	public void onDmDependencyRemoved(AmiWebDmManagerImpl amiWebDmManagerImpl, AmiWebDmPortlet target, String aliasDotName, String tableName) {
	}
	@Override
	public void onDmNameChanged(AmiWebDmManager amiWebDmManagerImpl, String oldAliasDotName, AmiWebDm dm) {
	}
	@Override
	public void onFilterDependencyAdded(AmiWebDmManagerImpl amiWebDmManagerImpl, AmiWebDmFilter target, String aliasDotName, String tableName) {
	}
	@Override
	public void onFilterDependencyRemoved(AmiWebDmManagerImpl amiWebDmManagerImpl, AmiWebDmFilter target, String aliasDotName, String tableName) {
	}
	@Override
	public void onAmiWebPanelIdChanged(AmiWebPortlet portlet, String oldAdn, String newAdn) {
	}
}
