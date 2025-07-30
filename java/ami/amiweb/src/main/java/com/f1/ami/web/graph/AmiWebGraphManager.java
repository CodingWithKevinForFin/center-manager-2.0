package com.f1.ami.web.graph;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.f1.ami.web.AmiWebAbstractContainerPortlet;
import com.f1.ami.web.AmiWebAliasPortlet;
import com.f1.ami.web.AmiWebDatasourceWrapper;
import com.f1.ami.web.AmiWebDmPortlet;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebManagers;
import com.f1.ami.web.AmiWebManagersListener;
import com.f1.ami.web.AmiWebObjects_Union;
import com.f1.ami.web.AmiWebPanelsListener;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.ami.web.AmiWebRealtimeObjectListener;
import com.f1.ami.web.AmiWebRealtimeObjectManager;
import com.f1.ami.web.AmiWebRealtimeProcessor;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebSystemObjectsListener;
import com.f1.ami.web.AmiWebSystemObjectsManager;
import com.f1.ami.web.AmiWebTableSchemaWrapper;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmFilter;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmListener;
import com.f1.ami.web.dm.AmiWebDmManager;
import com.f1.ami.web.dm.AmiWebDmManagerImpl;
import com.f1.ami.web.dm.AmiWebDmManagerListener;
import com.f1.ami.web.dm.AmiWebDmRealtimeQueue;
import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.ami.web.filter.AmiWebFilterPortlet;
import com.f1.ami.web.rt.AmiWebRealtimeProcessor_Decorate;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.suite.web.portal.PortletListener;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.utils.AH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiWebGraphManager implements AmiWebSystemObjectsListener, AmiWebDmManagerListener, AmiWebPanelsListener, PortletListener, AmiWebManagersListener {
	private static final String AMI = "AMI";
	private static final AmiWebGraphListener[] EMPTY = new AmiWebGraphListener[0];
	public static final Comparator<? super AmiWebGraphNode<?>> COMPARATOR_ID = new Comparator<AmiWebGraphNode<?>>() {

		@Override
		public int compare(AmiWebGraphNode<?> o1, AmiWebGraphNode<?> o2) {
			return OH.compare(o1.getId(), o2.getId(), false, SH.COMPARATOR_CASEINSENSITIVE_STRING);
		}
	};
	final private AmiWebService service;
	final private AmiWebDmManager manager;
	private Map<String, AmiWebGraphNode_Datasource> dsNodes = new HashMap<String, AmiWebGraphNode_Datasource>();
	private Map<String, AmiWebGraphNode_Datamodel> dmNodes = new HashMap<String, AmiWebGraphNode_Datamodel>();
	private Map<String, AmiWebGraphNode_Panel> pnNodes = new HashMap<String, AmiWebGraphNode_Panel>();
	private Map<String, AmiWebGraphNode_Link> lnNodes = new HashMap<String, AmiWebGraphNode_Link>();
	private Map<String, AmiWebGraphNode_Realtime> rtNodes = new HashMap<String, AmiWebGraphNode_Realtime>();
	private AmiWebGraphListener listeners[] = EMPTY;
	private long nextUid = 1;

	public AmiWebGraphManager(AmiWebService service) {
		this.service = service;
		this.manager = service.getDmManager();
	}
	public void init() {
		// the first two for loops only check the primary center, ignores secondary an etc...
		// it is working fine because web receives snapshots from other centers later in the process
		for (AmiWebDatasourceWrapper i : this.service.getSystemObjectsManager().getDatasources())
			onDatasourceAdded(i);
		for (AmiWebTableSchemaWrapper i : this.service.getSystemObjectsManager().getTables().values())
			onTableAdded(i);
		for (AmiWebDmsImpl i : this.manager.getDatamodels())
			onDmAdded(this.manager, i);
		for (AmiWebDmLink i : this.manager.getDmLinks())
			onDmLinkAdded(this.manager, i);
		for (AmiWebPortlet i : this.service.getAmiPanelManager().getAmiPanels())
			onAmiWebPanelAdded(i);
	}
	//TODO: Warning never called?
	@Deprecated
	public void close() {
		//Check moved to AmiWebService clearManagers()
		this.manager.removeDmManagerListener(this);
		this.service.getSystemObjectsManager().removeListener(this);
		this.service.removeAmiWebPanelsListener(this);
	}

	public void addListener(AmiWebGraphListener listener) {
		this.listeners = AH.append(this.listeners, listener);
	}
	public boolean removeListener(AmiWebGraphListener listener) {
		int i = AH.indexOf(listener, this.listeners);
		if (i == -1)
			return false;
		this.listeners = AH.remove(this.listeners, i);
		return true;
	}

	//================= DATASOURCES ======================

	@Override
	public void onDatasourceAdded(AmiWebDatasourceWrapper ds) {
		if (!AmiWebUtils.isFromPrimaryCenter(ds.getObject()))
			return;
		getOrCreateDs(ds);
	}
	@Override
	public void onDatasourceRemoved(AmiWebDatasourceWrapper ds) {
		String id = ds.getName();
		AmiWebGraphNode_Datasource node = getDs(id);
		if (node != null) {
			if (node.hasDependencies()) {
				node.setInner(null);
			} else {
				this.dsNodes.remove(id);
				fireRemoved(node);
			}
		}
	}

	//================= DATAMODELS ======================
	@Override
	public void onDmUpdated(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm dm) {
		onDmRemoved(amiWebDmManagerImpl, dm);
		onDmAdded(amiWebDmManagerImpl, dm);
	}
	@Override
	public void onDmAdded(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm dm) {
		AmiWebGraphNode_Datamodel node = getOrCreateDm(dm);
		AmiWebDmsImpl dm2 = (AmiWebDmsImpl) dm;
		node.setInner(dm2);
		for (String i : dm.getLowerDmAliasDotNames()) {
			AmiWebGraphNode_Datamodel lower = getOrCreateDm(i);
			node.addSourceDatamodel(lower);
			lower.addTargetDatamodel(node);
		}
		for (String i : dm.getUpperDmAliasDotNames()) {
			AmiWebGraphNode_Datamodel upper = this.getOrCreateDm(i);
			node.addTargetDatamodel(upper);
			upper.addSourceDatamodel(node);
		}
		for (String i : dm.getUsedDatasources()) {
			AmiWebGraphNode_Datasource lower = this.getOrCreateDs(i);
			node.addSourceDatasource(lower);
			lower.addTargetDatamodel(node);
		}
		for (AmiWebDmFilter i : dm.getFilters()) {
			AmiWebGraphNode_Panel lower = this.getOrCreatePn(i.getSourcePanel());
			node.addSourceFilterPanel(lower);
			lower.addTargetFilterDatamodel(node);
		}
		for (AmiWebDmListener i : dm.getDmListeners()) {
			if (i instanceof AmiWebDomObject) {
				for (AmiWebDomObject dom = (AmiWebDomObject) i; dom != null; dom = dom.getParentDomObject()) {
					if (dom instanceof AmiWebDmPortlet) {
						AmiWebGraphNode_Panel lower = this.getOrCreatePn((AmiWebDmPortlet) dom);
						node.addTargetPanel(lower);
						lower.addSourceDatamodel(node);
						break;
					}
				}
			}
		}
		for (String i : dm2.getRtSources()) {
			AmiWebGraphNodeRt<?> rt = this.getOrCreateRt(i);
			node.addSourceRealtime(rt);
			rt.addTargetRealtime(node);
		}
	}

	@Override
	public void onDmRemoved(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm dm) {
		String id = getId(dm);
		AmiWebGraphNode_Datamodel node = getDm(id);
		if (node != null) {
			this.dmNodes.remove(id);
			for (AmiWebGraphNode_Datamodel e : node.getSourceDatamodels().values()) {
				e.removeTargetDatamodel(id);
			}
			for (AmiWebGraphNode_Datamodel e : node.getTargetDatamodels().values()) {
				e.removeSourceDatamodel(id);
			}
			for (AmiWebGraphNode_Datasource e : node.getSourceDatasources().values()) {
				e.removeTargetDatamodel(id);
			}
			for (AmiWebGraphNode_Panel e : node.getTargetPanels().values()) {
				e.removeSourceDatamodel(id);
			}
			for (AmiWebGraphNode_Panel e : node.getSourceFilterPanels().values()) {
				e.removeTargetFilterDatamodel(id);
			}
			for (AmiWebGraphNodeRt<?> e : node.getSourceRealtimes().values()) {
				e.removeTargetRealtime(id);
			}
			fireRemoved(node);
		}

	}

	//================= LINKS (RELATIONSHIPS) ======================
	@Override
	public void onDmLinkAdded(AmiWebDmManager amiWebDmManagerImpl, AmiWebDmLink link) {
		final AmiWebGraphNode_Link node = this.getOrCreateLn(link);
		node.setInner(link);
		if (SH.is(link.getTargetDmAliasDotName())) {
			AmiWebGraphNode_Datamodel dm = this.getOrCreateDm(link.getTargetDmAliasDotName());
			dm.addSourceLink(node);
			node.setTargetDm(dm);
		}
		if (SH.is(link.getTargetPanelAliasDotId())) {
			AmiWebGraphNode_Panel pn = this.getOrCreatePn(link.getTargetPanelAliasDotId());
			pn.addSourceLink(node);
			node.setTargetPanel(pn);
		}
		if (SH.is(link.getSourcePanelAliasDotId())) {
			AmiWebGraphNode_Panel pn = this.getOrCreatePn(link.getSourcePanelAliasDotId());
			pn.addTargetLinks(node);
			node.setSourcePanel(pn);
		}
	}
	@Override
	public void onDmLinkRemoved(AmiWebDmManager amiWebDmManagerImpl, AmiWebDmLink link) {
		final String id = link.getAmiLayoutFullAliasDotId();
		final AmiWebGraphNode_Link node = this.lnNodes.remove(id);
		if (node != null) {
			if (node.getTargetDm() != null) {
				node.getTargetDm().removeSourceLink(id);
				node.setTargetDm(null);
			}
			if (node.getTargetPanel() != null) {
				node.getTargetPanel().removeSourceLink(node.getId());
				node.setTargetPanel(null);
			}
			if (node.getSourcePanel() != null) {
				node.getSourcePanel().removeTargetLinks(node.getId());
				node.setSourcePanel(null);
			}
			fireRemoved(node);
		}
	}

	//================= PANELS (RELATIONSHIPS) ======================
	@Override
	public void onAmiWebPanelAdded(AmiWebPortlet portlet) {
		AmiWebGraphNode_Panel node = getOrCreatePn(portlet);
		node.setInner(portlet);
		for (AmiWebDmLink i : portlet.getDmLinksFromThisPortlet()) {
			AmiWebGraphNode_Link link = this.getOrCreateLn(i.getAmiLayoutFullAliasDotId());
			node.addTargetLinks(link);
			link.setSourcePanel(node);
		}
		for (AmiWebDmLink i : portlet.getDmLinksToThisPortlet()) {
			AmiWebGraphNode_Link link = this.getOrCreateLn(i.getAmiLayoutFullAliasDotId());
			node.addSourceLink(link);
			link.setTargetPanel(node);
		}
		if (portlet.getParent() instanceof AmiWebPortlet) {
			AmiWebPortlet parent = (AmiWebPortlet) portlet.getParent();
			AmiWebGraphNode_Panel pnode = getOrCreatePn(parent);
			pnode.addChildPanel(node);
			node.setParentPanel(pnode);
		}
		if (portlet instanceof AmiWebRealtimeObjectManager) {
			AmiWebRealtimeObjectManager rt = (AmiWebRealtimeObjectManager) portlet;
			for (String i : rt.getLowerRealtimeIds()) {
				AmiWebGraphNodeRt<?> fnode = getOrCreateRt(i);
				node.addSourceRealtime(fnode);
				fnode.addTargetRealtime(node);
			}
		}
		if (portlet instanceof AmiWebAbstractContainerPortlet) {
			AmiWebAbstractContainerPortlet container = (AmiWebAbstractContainerPortlet) portlet;
			for (AmiWebAliasPortlet i : container.getAmiChildren()) {
				AmiWebGraphNode_Panel cnode = getOrCreatePn((AmiWebPortlet) i);
				node.addChildPanel(cnode);
				cnode.setParentPanel(node);
			}
		}
		if (portlet instanceof AmiWebDmPortlet) {
			AmiWebDmPortlet dmPortlet = (AmiWebDmPortlet) portlet;
			for (String i : dmPortlet.getUsedDmAliasDotNames()) {
				AmiWebGraphNode_Datamodel dnode = getOrCreateDm(i);
				node.addSourceDatamodel(dnode);
				dnode.addTargetPanel(node);
			}
		}
	}
	@Override
	public void onAmiWebPanelRemoved(AmiWebPortlet portlet, boolean isHide) {
		//		if (portlet instanceof AmiWebDmPortlet) {
		//			AmiWebDmPortlet dmPortlet = (AmiWebDmPortlet) portlet;
		String id = getId(portlet);
		AmiWebGraphNode_Panel node = this.pnNodes.get(id);
		if (node == null)
			return;
		for (AmiWebGraphNode_Link i : node.getSourceLinks().values()) {
			if (i.getTargetPanel() == node)
				i.setTargetPanel(null);
		}
		for (AmiWebGraphNode_Link i : node.getTargetLinks().values()) {
			if (i.getSourcePanel() == node)
				i.setSourcePanel(null);
		}
		if (node.getParentPanel() != null) {
			node.getParentPanel().removeChildPanel(id);
		}
		for (AmiWebGraphNode_Panel i : node.getChildrenPanels().values()) {
			i.setParentPanel(null);
		}
		for (AmiWebGraphNode_Datamodel i : node.getSourceDatamodels().values()) {
			i.removeTargetPanel(id);
		}
		for (AmiWebGraphNode_Datamodel i : node.getTargetFilterDatamodels().values()) {
			i.removeSourceFilterPanel(id);
		}
		for (AmiWebGraphNodeRt<?> i : node.getSourceRealtimes().values()) {
			i.removeTargetRealtime(id);
		}
		for (AmiWebGraphNodeRt<?> i : node.getTargetRealtimes().values()) {
			i.removeSourceRealtime(id);
		}
		this.pnNodes.remove(id);
		fireRemoved(node);
	}
	@Override
	public void onAmiWebPanelIdChanged(AmiWebPortlet portlet, String oldAdn, String newAdn) {
		OH.assertEq(getId(portlet), newAdn);
		AmiWebGraphNode_Panel node = this.pnNodes.remove(oldAdn);
		node.setId(newAdn);
		this.pnNodes.put(newAdn, node);
		AmiWebGraphNode_Panel parent = node.getParentPanel();
		if (parent != null) {
			parent.onChildPanelIdChanged(oldAdn, node);
		}
		for (AmiWebGraphNode_Datamodel i : node.getSourceDatamodels().values()) {
			i.onPanelIdChanged(oldAdn, node);
		}
		for (AmiWebGraphNode_Datamodel i : node.getTargetFilterDatamodels().values()) {
			i.onSourceFilterPanelIdChanged(oldAdn, node);
		}
		for (AmiWebGraphNodeRt<?> i : node.getTargetRealtimes().values()) {
			i.onSourceRealtimeIdChanged(oldAdn, node);
		}
		for (AmiWebGraphNodeRt<?> i : node.getSourceRealtimes().values()) {
			i.onTargetRealtimeIdChanged(oldAdn, node);
		}
	}
	@Override
	public void onDmNameChanged(AmiWebDmManager amiWebDmManagerImpl, String oldAliasDotName, AmiWebDm dm) {
		AmiWebGraphNode_Datamodel node = this.dmNodes.remove(oldAliasDotName);
		if (node != null) {
			String id = getId(dm);
			this.dmNodes.put(id, node);
			node.setId(id);
			for (AmiWebGraphNode_Datamodel i : node.getSourceDatamodels().values()) {
				i.onTargetDatamodelIdChanged(oldAliasDotName, node);
			}
			for (AmiWebGraphNode_Datasource i : node.getSourceDatasources().values()) {
				i.onTargetDatamodelIdChanged(oldAliasDotName, node);
			}
			for (AmiWebGraphNode_Panel i : node.getSourceFilterPanels().values()) {
				i.onTargetFilterDatamodelIdChanged(oldAliasDotName, node);
			}
			for (AmiWebGraphNodeRt<?> i : node.getSourceRealtimes().values()) {
				i.onTargetRealtimeIdChanged(oldAliasDotName, node);
			}

			for (AmiWebGraphNode_Datamodel i : node.getTargetDatamodels().values()) {
				i.onSourceDatamodelIdChanged(oldAliasDotName, node);
			}
			for (AmiWebGraphNode_Panel i : node.getTargetPanels().values()) {
				i.onSourceDatamodelIdChanged(oldAliasDotName, node);
			}
		}
	}
	@Override
	public void onDmDependencyAdded(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm upper, AmiWebDm lower) {
		AmiWebGraphNode_Datamodel unode = getOrCreateDm(upper);
		AmiWebGraphNode_Datamodel lnode = getOrCreateDm(lower);
		lnode.addTargetDatamodel(unode);
		unode.addSourceDatamodel(lnode);
	}
	@Override
	public void onDmDependencyRemoved(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm upper, AmiWebDm lower) {
		AmiWebGraphNode_Datamodel unode = getDm(getId(upper));
		AmiWebGraphNode_Datamodel lnode = getDm(getId(lower));
		if (unode != null && lnode != null) {
			lnode.removeTargetDatamodel(unode.getId());
			unode.removeSourceDatamodel(lnode.getId());
		}
	}
	@Override
	public void onDmDependencyAdded(AmiWebDmManager manager, AmiWebDmPortlet target, String dmName, String tableName) {
		AmiWebGraphNode_Panel pnode = getOrCreatePn(target);
		AmiWebGraphNode_Datamodel dnode = getOrCreateDm(dmName);
		pnode.addSourceDatamodel(dnode);
		dnode.addTargetPanel(pnode);
	}
	@Override
	public void onDmDependencyRemoved(AmiWebDmManagerImpl amiWebDmManagerImpl, AmiWebDmPortlet target, String dmName, String tableName) {
		AmiWebGraphNode_Panel pnode = getPn(getId(target));
		AmiWebGraphNode_Datamodel dnode = getDm(dmName);
		if (pnode != null && dnode != null) {
			pnode.removeSourceDatamodel(dnode.getId());
			dnode.removeTargetPanel(pnode.getId());
		}
	}
	@Override
	public void onFilterDependencyAdded(AmiWebDmManagerImpl amiWebDmManagerImpl, AmiWebDmFilter filter, String dmName, String tableName) {
		AmiWebFilterPortlet target = filter.getSourcePanel();
		AmiWebGraphNode_Panel pnode = getPn(getId(target));
		AmiWebGraphNode_Datamodel dnode = getDm(dmName);
		if (pnode != null && dnode != null) {
			pnode.addTargetFilterDatamodel(dnode);
			dnode.addSourceFilterPanel(pnode);
		}
	}
	@Override
	public void onFilterDependencyRemoved(AmiWebDmManagerImpl amiWebDmManagerImpl, AmiWebDmFilter filter, String dmName, String tableName) {
		AmiWebFilterPortlet target = filter.getSourcePanel();
		AmiWebGraphNode_Panel pnode = getPn(getId(target));
		AmiWebGraphNode_Datamodel dnode = getDm(dmName);
		if (pnode != null && dnode != null) {
			pnode.removeTargetFilterDatamodel(dnode.getId());
			dnode.removeSourceFilterPanel(pnode.getId());
		}
	}

	private AmiWebGraphNode_Datasource getOrCreateDs(String id) {
		AmiWebGraphNode_Datasource r = this.dsNodes.get(id);
		if (r == null) {
			this.dsNodes.put(id, r = new AmiWebGraphNode_Datasource(this, nextUid(), id, null));
			fireAdded(r);
		}
		return r;
	}
	private AmiWebGraphNode_Link getOrCreateLn(String id) {
		AmiWebGraphNode_Link r = this.lnNodes.get(id);
		if (r == null) {
			this.lnNodes.put(id, r = new AmiWebGraphNode_Link(this, nextUid(), id, null));
			fireAdded(r);
		}
		return r;
	}

	private AmiWebGraphNodeRt<?> getOrCreateRt(String id) {
		if (id.startsWith(AmiWebManagers.FEED) || id.startsWith(AmiWebManagers.PROCESSOR)) {
			AmiWebGraphNode_Realtime r = this.rtNodes.get(id);
			if (r == null) {
				AmiWebRealtimeObjectManager obj = this.service.getWebManagers().getAmiObjectsByTypeIfExists(id);
				this.rtNodes.put(id, r = new AmiWebGraphNode_Realtime(this, nextUid(), id, obj));
				fireAdded(r);
			}
			return r;
		} else if (id.startsWith(AmiWebManagers.PANEL)) {
			return getOrCreatePn(SH.stripPrefix(id, AmiWebManagers.PANEL, true));
		} else
			throw new RuntimeException("Not a realtime feed:" + id);
	}
	private AmiWebGraphNode_Panel getOrCreatePn(String id) {
		AmiWebGraphNode_Panel r = this.pnNodes.get(id);
		if (r == null) {
			this.pnNodes.put(id, r = new AmiWebGraphNode_Panel(this, nextUid(), id, null));
			fireAdded(r);

		}
		return r;
	}
	private AmiWebGraphNode_Datamodel getOrCreateDm(String id) {
		AmiWebGraphNode_Datamodel r = this.dmNodes.get(id);
		if (r == null) {
			this.dmNodes.put(id, r = new AmiWebGraphNode_Datamodel(this, nextUid(), id, null));
			fireAdded(r);
		}
		return r;
	}

	private AmiWebGraphNode_Datasource getOrCreateDs(AmiWebDatasourceWrapper o) {
		String id = getId(o);
		AmiWebGraphNode_Datasource r = this.dsNodes.get(id);
		if (r == null) {
			this.dsNodes.put(id, r = new AmiWebGraphNode_Datasource(this, nextUid(), id, o));
			fireAdded(r);
		} else
			r.setInner(o);
		return r;
	}
	static public String getId(AmiWebDatasourceWrapper o) {
		return o.getName();
	}
	private AmiWebGraphNode_Link getOrCreateLn(AmiWebDmLink o) {
		String id = getId(o);
		AmiWebGraphNode_Link r = this.lnNodes.get(id);
		if (r == null) {
			this.lnNodes.put(id, r = new AmiWebGraphNode_Link(this, nextUid(), id, o));
			fireAdded(r);
		}
		return r;
	}
	private AmiWebGraphNode_Realtime getOrCreateRt(AmiWebTableSchemaWrapper o) {
		String id = getId(o);
		AmiWebGraphNode_Realtime r = this.rtNodes.get(id);
		if (r == null) {
			AmiWebRealtimeObjectManager obj = this.service.getWebManagers().getAmiObjectsByTypeIfExists(AmiWebManagers.FEED + o.getName());
			if (!id.startsWith(AmiWebManagers.FEED) && !id.startsWith(AmiWebManagers.PROCESSOR))
				throw new RuntimeException("Not a realtime feed:" + id);
			this.rtNodes.put(id, r = new AmiWebGraphNode_Realtime(this, nextUid(), id, obj));
			fireAdded(r);
		}
		return r;
	}
	public static String getId(AmiWebDmLink o) {
		return o.getAmiLayoutFullAliasDotId();
	}
	public static String getId(AmiWebTableSchemaWrapper o) {
		return AmiWebManagers.FEED + o.getName();
	}
	private AmiWebGraphNode_Panel getOrCreatePn(AmiWebPortlet o) {
		String id = getId(o);
		AmiWebGraphNode_Panel r = this.pnNodes.get(id);
		if (r == null) {
			this.pnNodes.put(id, r = new AmiWebGraphNode_Panel(this, nextUid(), id, o));
			r.setInner(o);
			fireAdded(r);
		}
		return r;
	}
	public static String getId(AmiWebAliasPortlet o) {
		return o.getAmiLayoutFullAliasDotId();
	}
	private AmiWebGraphNode_Datamodel getOrCreateDm(AmiWebDm o) {
		String id = getId(o);
		AmiWebGraphNode_Datamodel r = this.dmNodes.get(id);
		if (r == null) {
			this.dmNodes.put(id, r = new AmiWebGraphNode_Datamodel(this, nextUid(), id, (AmiWebDmsImpl) o));
			fireAdded(r);
		}
		return r;
	}
	private long nextUid() {
		return this.nextUid++;
	}
	public static String getId(AmiWebDm o) {
		return o.getAmiLayoutFullAliasDotId();
	}
	private AmiWebGraphNode_Datasource getDs(String id) {
		return this.dsNodes.get(id);
	}
	private AmiWebGraphNode_Link getLn(String id) {
		return this.lnNodes.get(id);
	}
	private AmiWebGraphNode_Realtime getRt(String id) {
		return this.rtNodes.get(id);
	}
	private AmiWebGraphNode_Panel getPn(String id) {
		return this.pnNodes.get(id);
	}
	private AmiWebGraphNode_Datamodel getDm(String id) {
		return this.dmNodes.get(id);
	}

	@Override
	public void onDatasourceUpdated(AmiWebDatasourceWrapper ds) {
	}
	@Override
	public void onGuiClearing(AmiWebSystemObjectsManager gui) {
	}
	@Override
	public void onDmManagerInitDone() {
	}
	@Override
	public void onAmiWebPanelLocationChanged(AmiWebPortlet portlet) {
	}

	private void fireAdded(AmiWebGraphNode node) {
		for (AmiWebGraphListener i : this.listeners)
			i.onAdded(node);
	}
	private void fireRemoved(AmiWebGraphNode removed) {
		for (AmiWebGraphListener i : this.listeners)
			i.onRemoved(removed);
	}
	public void fireIdChanged(AmiWebGraphNode node, String oldId, String newId) {
		for (AmiWebGraphListener i : this.listeners)
			i.onIdChanged(node, oldId, newId);
	}
	public void fireChanged(AmiWebGraphNode node, Object old, Object nuw) {
		for (AmiWebGraphListener i : this.listeners)
			i.onInnerChanged(node, old, nuw);
	}
	public void fireOnEdgeAdded(byte type, AmiWebGraphNode src, AmiWebGraphNode tgt) {
		for (AmiWebGraphListener i : this.listeners)
			i.onEdgeAdded(type, src, tgt);
	}
	public void fireOnEdgeRemoved(byte type, AmiWebGraphNode src, AmiWebGraphNode tgt) {
		for (AmiWebGraphListener i : this.listeners)
			i.onEdgeRemoved(type, src, tgt);
	}
	public void fireOnEdgeChanged(byte type, AmiWebGraphNode src, AmiWebGraphNode oldTgt, AmiWebGraphNode nuwTgt) {
		if (oldTgt == nuwTgt)
			return;
		if (oldTgt != null)
			fireOnEdgeRemoved(type, src, oldTgt);
		if (nuwTgt != null)
			fireOnEdgeAdded(type, src, nuwTgt);
	}

	public static String formatType(byte type) {
		switch (type) {
			case AmiWebGraphNode.TYPE_DATAMODEL:
				return "DATAMODEL";
			case AmiWebGraphNode.TYPE_DATASOURCE:
				return "DATASOURCE";
			case AmiWebGraphNode.TYPE_LINK:
				return "LINK";
			case AmiWebGraphNode.TYPE_PANEL:
				return "PANEL";
			case AmiWebGraphNode.TYPE_FEED:
				return "FEED";
			case AmiWebGraphNode.TYPE_PROCESSOR:
				return "PROCESSOR";
			default:
				return "UNKNOWN:" + type;
		}
	}
	public static String formatEdgeType(byte type) {
		switch (type) {
			case AmiWebGraphNode.EDGE_SOURCE_DATASOURCE:
				return "SOURCE_DATASOURCE";
			case AmiWebGraphNode.EDGE_SOURCE_DATAMODEL:
				return "SOURCE_DATAMODEL";
			case AmiWebGraphNode.EDGE_TARGET_DATAMODEL:
				return "TARGET_DATAMODEL";
			case AmiWebGraphNode.EDGE_TARGET_PANEL:
				return "TARGET_PANEL";
			case AmiWebGraphNode.EDGE_SOURCE_PANEL:
				return "SOURCE_PANEL";
			case AmiWebGraphNode.EDGE_TARGET_LINK:
				return "TARGET_LINK";
			case AmiWebGraphNode.EDGE_SOURCE_LINK:
				return "SOURCE_LINK";
			case AmiWebGraphNode.EDGE_PARENT_PANEL:
				return "PARENT_PANEL";
			case AmiWebGraphNode.EDGE_CHILD_PANEL:
				return "CHILD_PANEL";
			case AmiWebGraphNode.EDGE_TARGET_FILTER_DATAMODEL:
				return "TARGET_FILTER_DATAMODEL";
			case AmiWebGraphNode.EDGE_SOURCE_FILTER_PANEL:
				return "TARGET_FILTER_PANEL";
			case AmiWebGraphNode.EDGE_SOURCE_REALTIME:
				return "SOURCE_REALTIME";
			case AmiWebGraphNode.EDGE_TARGET_REALTIME:
				return "TARGET_REALTIME";
			default:
				return "UNKNOWN:" + type;
		}
	}
	public void clear() {
		this.dmNodes.clear();
		this.dsNodes.clear();
		this.lnNodes.clear();
		this.pnNodes.clear();
		this.rtNodes.clear();
		for (AmiWebTableSchemaWrapper i : this.service.getSystemObjectsManager().getTables().values())
			onTableAdded(i);
	}
	public Map<String, AmiWebGraphNode_Datasource> getDatasources() {
		return this.dsNodes;
	}
	public Map<String, AmiWebGraphNode_Datamodel> getDatamodels() {
		return this.dmNodes;
	}
	public Map<String, AmiWebGraphNode_Link> getLinks() {
		return this.lnNodes;
	}
	public Map<String, AmiWebGraphNode_Panel> getPanels() {
		return this.pnNodes;
	}
	public Map<String, AmiWebGraphNode_Realtime> getRealtimes() {
		return this.rtNodes;
	}
	@Override
	public void onPortletAdded(Portlet newPortlet) {
	}
	@Override
	public void onPortletClosed(Portlet oldPortlet) {
	}
	@Override
	public void onSocketConnected(PortletSocket initiator, PortletSocket remoteSocket) {
	}
	@Override
	public void onSocketDisconnected(PortletSocket initiator, PortletSocket remoteSocket) {
	}
	@Override
	public void onPortletParentChanged(Portlet newPortlet, PortletContainer oldParent) {
		if (newPortlet instanceof AmiWebPortlet) {
			AmiWebPortlet portlet = (AmiWebPortlet) newPortlet;
			AmiWebGraphNode_Panel node = getPn(portlet.getAmiLayoutFullAliasDotId());
			if (node != null) {
				AmiWebGraphNode_Panel existingParent = node.getParentPanel();
				if (existingParent != null) {
					existingParent.removeChildPanel(node.getId());
				}
				AmiWebAliasPortlet newParent = portlet.getAmiParent();
				//				if (newParent instanceof AmiWebAbstractContainerPortlet) {
				//					AmiWebAbstractContainerPortlet t = (AmiWebAbstractContainerPortlet) newParent;
				if (newParent == null)
					node.setParentPanel(null);
				else {
					AmiWebGraphNode_Panel pn = getPn(getId(newParent));
					if (pn != null) {
						node.setParentPanel(pn);
						pn.addChildPanel(node);
					}
				}
				//				} else
				//					node.setParentPanel(null);

			}
		}
	}
	@Override
	public void onJavascriptQueued(Portlet portlet) {
	}
	@Override
	public void onPortletRenamed(Portlet portlet, String oldName, String newName) {
	}
	@Override
	public void onLocationChanged(Portlet portlet) {
	}
	public static String toString(AmiWebGraphNode o) {
		if (o.getInner() == null)
			return AmiWebGraphManager.formatType(o.getType()) + "-" + o.getUid() + ":" + o.getId();
		else
			return AmiWebGraphManager.formatType(o.getType()) + "-" + o.getUid() + ":" + o.getId() + " (" + o.getInner().getClass().getSimpleName() + ")";
	}
	public AmiWebGraphNode_Panel getNode(AmiWebAliasPortlet p) {
		return this.pnNodes.get(getId(p));
	}
	public AmiWebGraphNode_Datasource getNode(AmiWebDatasourceWrapper p) {
		return this.dsNodes.get(getId(p));
	}
	public AmiWebGraphNode_Datamodel getNode(AmiWebDm p) {
		return this.dmNodes.get(getId(p));
	}
	public AmiWebGraphNode_Link getNode(AmiWebDmLink p) {
		return this.lnNodes.get(getId(p));
	}
	public AmiWebGraphNode_Realtime getNode(AmiWebTableSchemaWrapper p) {
		return this.rtNodes.get(getId(p));
	}
	//these are realtime feeds
	@Override
	public void onTableAdded(AmiWebTableSchemaWrapper tableName) {
		if (!tableName.isBroadcast())
			return;
		AmiWebGraphNode_Realtime obj = getOrCreateRt(tableName);
		AmiWebRealtimeObjectManager inner = obj.getInner();
		if (inner != null) {
			// rt datamodel is of this type
			if (inner instanceof AmiWebObjects_Union) {
				AmiWebObjects_Union unionInner = (AmiWebObjects_Union) inner;
				AmiWebRealtimeObjectListener[] amiListeners = unionInner.getAMIListeners();
				for (AmiWebRealtimeObjectListener arl : amiListeners) {
					// re-associate rt dm with the newly table if table name is the same
					if (arl instanceof AmiWebDmRealtimeQueue) {
						AmiWebDmRealtimeQueue rtDm = (AmiWebDmRealtimeQueue) arl;
						AmiWebDmsImpl dm = (AmiWebDmsImpl) rtDm.getDm();
						AmiWebGraphNode_Datamodel nodeDm = getOrCreateDm(dm.getAmiLayoutFullAliasDotId());
						obj.addTargetRealtime(nodeDm);
						nodeDm.addSourceRealtime(obj);
					}
				}

			}
			for (String i : inner.getUpperRealtimeIds()) {
				if (i.startsWith(AmiWebManagers.PANEL)) {
					AmiWebGraphNode_Panel t = getOrCreatePn(SH.stripPrefix(i, AmiWebManagers.PANEL, true));
					obj.addTargetRealtime(t);
					t.addSourceRealtime(obj);
				} else {
					AmiWebGraphNodeRt<?> t = getOrCreateRt(i);
					obj.addTargetRealtime(t);
					t.addSourceRealtime(obj);
				}
			}
			for (String i : inner.getLowerRealtimeIds()) {
				if (i.startsWith(AmiWebManagers.PANEL)) {
					AmiWebGraphNode_Panel t = getOrCreatePn(SH.stripPrefix(i, AmiWebManagers.PANEL, true));
					obj.addSourceRealtime(t);
					t.addTargetRealtime(obj);
				} else {
					AmiWebGraphNodeRt<?> t = getOrCreateRt(i);
					obj.addSourceRealtime(t);
					t.addTargetRealtime(obj);
				}
			}
		}
	}
	//these are realtime feeds
	@Override
	public void onTableRemoved(AmiWebTableSchemaWrapper tableName) {
		if (!tableName.isBroadcast())
			return;
		String id = getId(tableName);
		AmiWebGraphNode_Realtime node = this.rtNodes.get(id);
		if (node != null) {
			// removes the node on frontend regardless of any existing rt panel built on top of it
			// we will have an dangling rt panel if we removed the db table with rt dependency
			this.rtNodes.remove(id);
			for (AmiWebGraphNodeRt<?> i : node.getSourceRealtimes().values()) {
				i.removeTargetRealtime(id);
			}
			// we need to remove source rt so dm doesn't link to non-existing rt feed when we rebuild the data modeler view
			// see AmiWebDmSmartGraph::walkNodes last part
			for (AmiWebGraphNodeRt<?> i : node.getTargetRealtimes().values()) {
				i.removeSourceRealtime(id);
			}
			fireRemoved(node);
		}
	}
	@Override
	public void onProcesserAdded(AmiWebRealtimeProcessor processor) {
		AmiWebGraphNode_Realtime r = (AmiWebGraphNode_Realtime) getOrCreateRt(processor.getRealtimeId());
		r.setInner(processor);
	}
	@Override
	public void onProcesserRemoved(AmiWebRealtimeProcessor processor) {
		String id = processor.getRealtimeId();
		AmiWebGraphNode_Realtime node = this.rtNodes.remove(id);
		if (node != null) {
			if (node.hasDependencies()) {
				node.setInner(null);
			} else {
				if (processor.getType() != "BPIPE")
					// remove references to this rt source from downstream
					for (String rtId : processor.getLowerRealtimeIds()) {
						if (rtId.startsWith(AmiWebManagers.PANEL)) {
							rtId = SH.afterFirst(rtId, AmiWebManagers.PANEL);
							AmiWebGraphNode_Panel tmpNode = this.pnNodes.get(rtId);
							// no op if we can't find it. One of the reasons is creating a processor using child layout's panel as source. After unlinking the child layout, AMI won't be able to find the panel
							if (tmpNode != null)
								tmpNode.removeTargetRealtime(id);
						} else {
							AmiWebGraphNode_Realtime tmpNode = this.rtNodes.get(rtId);
							tmpNode.removeTargetRealtime(id);
						}
					}
				fireRemoved(node);
			}
		}
	}
	@Override
	public void onRealtimeListenerAdded(AmiWebRealtimeObjectManager object, AmiWebRealtimeObjectListener listener) {
		AmiWebGraphNodeRt<?> source;
		AmiWebGraphNodeRt<?> target;
		if (object instanceof AmiWebObjects_Union) {
			AmiWebObjects_Union feed = (AmiWebObjects_Union) object;
			AmiWebGraphNode_Realtime feedNode = (AmiWebGraphNode_Realtime) getOrCreateRt(feed.getRealtimeId());
			if (feedNode.getInner() == null)
				feedNode.setInner(object);
			source = feedNode;
		} else if (object instanceof AmiWebRealtimeProcessor) {
			AmiWebRealtimeProcessor processor = (AmiWebRealtimeProcessor) object;
			source = getOrCreateRt(processor.getRealtimeId());
		} else if (object instanceof AmiWebPortlet) {
			AmiWebPortlet panel = (AmiWebPortlet) object;
			source = getOrCreatePn(panel);
		} else
			return;
		if (listener instanceof AmiWebPortlet) { //feed -> panel
			AmiWebPortlet panel = (AmiWebPortlet) listener;
			target = getOrCreatePn(panel);
		} else if (listener instanceof AmiWebRealtimeProcessor) { //feed -> Processor
			AmiWebRealtimeProcessor processor = (AmiWebRealtimeProcessor) listener;
			target = getOrCreateRt(processor.getRealtimeId());
		} else if (listener instanceof AmiWebRealtimeProcessor_Decorate.Index) { //feed -> Processor
			AmiWebRealtimeProcessor_Decorate.Index index = (AmiWebRealtimeProcessor_Decorate.Index) listener;
			target = getOrCreateRt(index.getProcessor().getRealtimeId());
		} else if (listener instanceof AmiWebDmRealtimeQueue) { //feed -> Datamodel
			AmiWebDmRealtimeQueue queue = (AmiWebDmRealtimeQueue) listener;
			AmiWebDm dm = queue.getDm();
			target = getOrCreateDm(dm);
		} else
			return;
		target.addSourceRealtime(source);
		source.addTargetRealtime(target);

	}
	@Override
	public void onRealtimeListenerRemoved(AmiWebRealtimeObjectManager objects, AmiWebRealtimeObjectListener listener) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onProcesserRenamed(AmiWebRealtimeProcessor processor, String oldAri, String newAri) {
		//		OH.assertEq(getId(processor), newAri);
		AmiWebGraphNode_Realtime node = this.rtNodes.remove(oldAri);
		node.setId(newAri);
		this.rtNodes.put(newAri, node);

		for (AmiWebGraphNodeRt<?> i : node.getSourceRealtimes().values()) {
			i.onTargetRealtimeIdChanged(oldAri, node);
		}
		for (AmiWebGraphNodeRt<?> i : node.getTargetRealtimes().values()) {
			i.onSourceRealtimeIdChanged(oldAri, node);
		}
	}
	public AmiWebService getService() {
		return service;
	}
}
