package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiCenterDefinition;
import com.f1.ami.amicommon.centerclient.AmiCenterClientMsgStatusMessage;
import com.f1.ami.amicommon.centerclient.AmiCenterClientObjectMessages;
import com.f1.ami.amicommon.centerclient.AmiCenterClientSnapshot;
import com.f1.ami.web.datafilter.AmiWebDataFilter;
import com.f1.base.Action;
import com.f1.base.IterableAndSize;
import com.f1.base.MappingEntry;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletManager;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.EmptyIterable;
import com.f1.utils.IterableAndSizeIterator;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.HasherMap.Entry;
import com.f1.utils.impl.FastArrayList;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;

public class AmiWebManagers {

	public static final String FEED = "FEED:";
	public static final String PANEL = AmiWebDomObject.ARI_TYPE_PANEL + ":";
	public static final String PROCESSOR = AmiWebDomObject.ARI_TYPE_PROCESSOR + ":";

	private static final Logger log = LH.get();
	final private AmiWebManager[] managers;
	private AmiWebSystemObjectsManager[] objectManagers;
	private AmiWebSnapshotManager[] snapshotManagers;
	private AmiWebService service;

	private List<AmiWebManagersListener> listeners = new ArrayList<AmiWebManagersListener>();

	public AmiWebManagers(AmiWebService service) {
		this.service = service;
		AmiCenterDefinition[] centerDefs = service.getCenterIds();
		this.managers = new AmiWebManager[centerDefs.length];
		this.objectManagers = new AmiWebSystemObjectsManager[centerDefs.length];
		this.snapshotManagers = new AmiWebSnapshotManager[centerDefs.length];
		for (byte i = 0; i < managers.length; i++) {
			AmiWebManager amiWebManager = new AmiWebManager(this, centerDefs[i], service.getUserName());
			managers[i] = amiWebManager;
			AmiWebSnapshotManager snapshotManager = new AmiWebSnapshotManager(service, amiWebManager);
			objectManagers[i] = new AmiWebSystemObjectsManager(service, amiWebManager, snapshotManager);
			amiWebManager.init(snapshotManager, objectManagers[i]);
			snapshotManagers[i] = snapshotManager;
		}
	}

	public AmiWebManager getWebManager(byte centerId) {
		return this.managers[centerId];
	}
	public AmiWebManager getPrimaryWebManager() {
		return this.managers[0];
	}
	public AmiWebSystemObjectsManager getPrimarySystemObjectsManager() {
		return this.objectManagers[0];
	}
	public AmiWebSnapshotManager getPrimarySnapshotManager() {
		return this.snapshotManagers[0];
	}

	public AmiWebManager[] getManagers() {
		return this.managers;
	}
	public AmiWebSystemObjectsManager getSystemObjectsManager(byte centerId) {
		return this.objectManagers[centerId];
	}

	public void setDataFilter(AmiWebDataFilter filter) {
		for (AmiWebManager i : this.managers)
			i.setDataFilter(filter);
	}

	public void onClosed() {
		for (int i = this.managers.length - 1; i >= 0; i--)
			this.managers[i].onConnectionStateChanged(AmiWebSnapshotManager.STATE_DISCONNECTED);

	}

	//	private HasherMap<String, AmiWebObjects_Union> amiWebObjects = new HasherMap<String, AmiWebObjects_Union>();
	private HasherMap<String, AmiWebRealtimeProcessor> amiWebRealtimeProcessors = new HasherMap<String, AmiWebRealtimeProcessor>();
	private HasherMap<String, AmiWebRealtimeObjectManager> amiWebRealtimeManagers = new HasherMap<String, AmiWebRealtimeObjectManager>();

	public Set<String> getProcessorIds() {
		return this.amiWebRealtimeProcessors.keySet();
	}

	public AmiWebRealtimeObjectManager getAmiObjectsByType(String rtid) {
		Entry<String, AmiWebRealtimeObjectManager> node = amiWebRealtimeManagers.getOrCreateEntry(rtid);
		AmiWebRealtimeObjectManager r = node.getValue();
		if (r != null)
			return r;
		if (rtid.startsWith(PANEL)) {
			String pnl = SH.stripPrefix(rtid, PANEL, true);
			AmiWebRealtimePortlet t = (AmiWebRealtimePortlet) service.getPortletByAliasDotPanelId(pnl);
			r = new Wrapper(rtid);
			((Wrapper) r).setInner(t);
		} else if (rtid.startsWith(FEED)) {
			String type = SH.stripPrefix(rtid, FEED, true);
			r = new AmiWebObjects_Union(this, rtid);//TODO:Should it be the primary manager
			for (AmiWebManager i : this.managers)
				((AmiWebObjects_Union) r).addInner(i.getAmiObjectsByType(type));
		} else if (rtid.startsWith(PROCESSOR)) {
			String pnl = SH.stripPrefix(rtid, PROCESSOR, true);
			AmiWebRealtimeProcessor t = amiWebRealtimeProcessors.get(pnl);
			//AmiWebRealtimePortlet t = (AmiWebRealtimePortlet) service.getPortletByAliasDotPanelId(pnl);
			r = new Wrapper(rtid);
			((Wrapper) r).setInner(t);
		} else
			throw new RuntimeException("invalid realtime feed: " + rtid);
		node.setValue(r);
		return r;
	}
	public AmiWebRealtimeObjectManager getAmiObjectsByTypeIfExists(String rtid) {
		Entry<String, AmiWebRealtimeObjectManager> node = amiWebRealtimeManagers.getOrCreateEntry(rtid);
		AmiWebRealtimeObjectManager r = node.getValue();
		if (r != null)
			return r;
		if (rtid.startsWith(PANEL)) {
			String pnl = SH.stripPrefix(rtid, PANEL, true);
			AmiWebRealtimePortlet t = (AmiWebRealtimePortlet) service.getPortletByAliasDotPanelId(pnl);
			if (t == null)
				return null;
			r = new Wrapper(rtid);
			((Wrapper) r).setInner(t);
		} else if (rtid.startsWith(FEED)) {
			return null;
		} else if (rtid.startsWith(PROCESSOR)) {
			String pnl = SH.stripPrefix(rtid, PROCESSOR, true);
			AmiWebRealtimeProcessor t = amiWebRealtimeProcessors.get(pnl);
			if (t == null)
				return null;
			//AmiWebRealtimePortlet t = (AmiWebRealtimePortlet) service.getPortletByAliasDotPanelId(pnl);
			r = new Wrapper(rtid);
			((Wrapper) r).setInner(t);
		} else
			throw new RuntimeException("invalid realtime feed: " + rtid);
		node.setValue(r);
		return r;
	}

	private class Wrapper implements AmiWebRealtimeObjectManager {

		private AmiWebRealtimeObjectManager inner;

		private AmiWebRealtimeObjectListener[] listeners = AmiWebRealtimeObjectListener.EMPTY_ARRAY;

		final private String id;

		private Set<String> upperRealtimeIds = new HashSet<String>();

		public Wrapper(String id) {
			this.id = id;
		}

		@Override
		public boolean removeAmiListener(AmiWebRealtimeObjectListener listener) {
			int i = AH.indexOf(listener, this.listeners);
			if (i == -1)
				return false;
			this.listeners = AH.remove(this.listeners, i);
			if (inner != null)
				inner.removeAmiListener(listener);
			return true;
		}

		@Override
		public boolean addAmiListener(AmiWebRealtimeObjectListener listener) {
			int i = AH.indexOf(listener, this.listeners);
			if (i != -1)
				return false;
			this.listeners = AH.append(this.listeners, listener);
			if (inner != null)
				inner.addAmiListener(listener);
			return true;
		}

		@Override
		public IterableAndSize<AmiWebObject> getAmiObjects() {
			return inner == null ? EmptyIterable.INSTANCE : inner.getAmiObjects();
		}

		public AmiWebRealtimeObjectManager getInner() {
			return inner;
		}

		public void setInner(AmiWebRealtimeObjectManager inner) {
			if (this.inner == inner)
				return;
			if (this.inner == null) {
				this.inner = inner;
				for (AmiWebRealtimeObjectListener i : this.listeners)
					this.inner.addAmiListener(i);
				for (AmiWebRealtimeObjectListener i : this.listeners)
					i.onAmiEntitiesReset(this);
			} else {
				OH.assertNull(inner);
				for (AmiWebRealtimeObjectListener i : this.listeners)
					this.inner.removeAmiListener(i);
				this.inner = null;
				for (AmiWebRealtimeObjectListener i : this.listeners)
					i.onAmiEntitiesReset(this);
			}
		}

		@Override
		public com.f1.base.CalcTypes getRealtimeObjectschema() {
			return this.inner == null ? EmptyCalcTypes.INSTANCE : this.inner.getRealtimeObjectschema();
		}

		@Override
		public com.f1.base.CalcTypes getRealtimeObjectsOutputSchema() {
			return this.inner == null ? EmptyCalcTypes.INSTANCE : this.inner.getRealtimeObjectsOutputSchema();
		}

		@Override
		public String getRealtimeId() {
			return id;
		}

		@Override
		public Set<String> getLowerRealtimeIds() {
			return this.inner == null ? Collections.EMPTY_SET : this.inner.getLowerRealtimeIds();
		}

		@Override
		public Set<String> getUpperRealtimeIds() {
			return AmiWebUtils.updateRealtimeIds(this.listeners, this.upperRealtimeIds);
		}
		@Override
		public boolean hasAmiListeners() {
			return this.listeners.length > 0;
		}

	}

	public void onRealtimePortelAdded(String adn, AmiWebRealtimePortlet p) {
		Wrapper wrapper = (Wrapper) this.amiWebRealtimeManagers.get(PANEL + adn);
		if (wrapper != null)
			wrapper.setInner(p);
	}
	public void onRealtimePortelRemoved(String adn) {
		Wrapper wrapper = (Wrapper) this.amiWebRealtimeManagers.get(PANEL + adn);
		if (wrapper != null)
			wrapper.setInner(null);
	}
	public void onRealtimePortelAdnChaned(String oldAdn, String newAdn, AmiWebRealtimePortlet portlet) {
		onRealtimePortelRemoved(oldAdn);
		onRealtimePortelAdded(newAdn, portlet);
	}

	public IterableAndSize<AmiWebObject> getAmiObjects(Set<String> types) {
		FastArrayList<IterableAndSize<AmiWebObject>> t = new FastArrayList<IterableAndSize<AmiWebObject>>(types.size());
		for (String i : types)
			t.add(getAmiObjectsByType(i).getAmiObjects());
		return IterableAndSizeIterator.create(t);
	}

	public void onInit(PortletManager manager, Map<String, Object> configuration, String rootId) {
		for (AmiWebSnapshotManager i : this.snapshotManagers)
			i.onInit(manager, configuration, rootId);
	}

	public void onInitDone() {
		for (AmiWebSnapshotManager i : this.snapshotManagers)
			i.onInitDone();

		for (AmiWebSystemObjectsManager i : this.objectManagers)
			i.onInitDone();
	}

	public Set<String> getAllTableTypes(String layoutAlias) {
		Set<String> r = new TreeSet<String>();
		for (AmiWebSystemObjectsManager i : this.objectManagers) {
			Set<String> t = i.getTableNames();
			if (t == null)
				continue;
			for (String s : t)
				r.add(FEED + s);
		}
		Collection<AmiWebRealtimePortlet> portlets = PortletHelper.findPortletsByType(service.getDesktop(), AmiWebRealtimePortlet.class);
		for (AmiWebRealtimePortlet p : portlets)
			if (AmiWebUtils.isParentAliasOrSame(layoutAlias, p.getAmiLayoutFullAlias()))
				r.add(p.getRealtimeId());
		for (MappingEntry<String, AmiWebRealtimeProcessor> i : this.amiWebRealtimeProcessors.entries())
			if (AmiWebUtils.isParentAliasOrSame(layoutAlias, i.getValue().getAlias()))
				r.add(i.getKey());
		return r;
	}

	public byte getConnectionState() {
		return this.getPrimarySnapshotManager().getConnectionState();
	}
	public void onBackendAction(Action action) {
		if (action instanceof AmiCenterClientMsgStatusMessage) {
			AmiCenterClientMsgStatusMessage msg = (AmiCenterClientMsgStatusMessage) action;
			this.snapshotManagers[msg.getCenterId()].onBackendAction(msg.getMsgStatusMessage());
		} else if (action instanceof AmiCenterClientObjectMessages) {
			AmiCenterClientObjectMessages msg = (AmiCenterClientObjectMessages) action;
			this.snapshotManagers[msg.getCenterId()].onBackendAction(msg);
		} else if (action instanceof AmiCenterClientSnapshot) {
			AmiCenterClientSnapshot msg = (AmiCenterClientSnapshot) action;
			this.snapshotManagers[msg.getCenterId()].onBackendAction(msg);
		} else
			LH.info(log, "Unexpected: " + action);
		// TODO Auto-generated method stub

	}

	public AmiWebSnapshotManager[] getSnapshotManagers() {
		return this.snapshotManagers;
	}

	public AmiWebSystemObjectsManager[] getSystemObjectManagers() {
		return this.objectManagers;
	}

	public void clear() {
		for (AmiWebRealtimeProcessor i : this.amiWebRealtimeProcessors.values())
			i.close();
		this.amiWebRealtimeManagers.clear();
		this.amiWebRealtimeProcessors.clear();
		for (AmiWebManager i : this.managers)
			i.clear();
	}

	Map<String, Object> getConfiguration(String alias) {
		final List<Map<String, Object>> processors = new ArrayList<Map<String, Object>>();
		for (MappingEntry<String, AmiWebRealtimeProcessor> i : this.amiWebRealtimeProcessors.entries())
			if (OH.eq(alias, i.getValue().getAlias())) {
				Map<String, Object> configuration = exportConfiguration(i.getValue());
				processors.add(configuration);
			}
		return processors.isEmpty() ? Collections.EMPTY_MAP : CH.m("processors", processors);

	}

	public Map<String, Object> exportConfiguration(AmiWebRealtimeProcessor p) {
		Map<String, Object> configuration = new HashMap<String, Object>();
		configuration.put("name", p.getName());
		configuration.put("type", p.getType());
		configuration.put("cfg", p.getConfiguration());
		return configuration;
	}
	void init(String fullAlias, Map<String, Object> config, StringBuilder warningsSink) {
		if (config != null) {
			final List<Map<String, Object>> processors = (List<Map<String, Object>>) config.get("processors");
			if (CH.isntEmpty(processors)) {
				for (Map m : processors) {
					importConfiguration(fullAlias, m);
				}
			}
		}
	}

	public AmiWebRealtimeProcessor importConfiguration(String fullAlias, Map m) {
		String name = (String) m.get("name");
		String type = (String) m.get("type");
		Map<String, Object> cfg = (Map<String, Object>) m.get("cfg");
		AmiWebRealtimeProcessorPlugin factory = getProcessorPlugin(type);
		AmiWebRealtimeProcessor processor = factory.create(this.service, fullAlias);
		String adn = AmiWebUtils.getFullAlias(fullAlias, name);
		adn = SH.stripPrefix(SH.getNextId(PROCESSOR + adn, this.getProcessorIds(), 2), PROCESSOR, true);
		processor.setAdn(adn);
		processor.init(fullAlias, cfg);
		addProcessor(processor);
		processor.rebuild();
		return processor;
	}

	public void addProcessor(AmiWebRealtimeProcessor plugin) {
		CH.putOrThrow(this.amiWebRealtimeProcessors, plugin.getRealtimeId(), plugin);
		Entry<String, AmiWebRealtimeObjectManager> t = this.amiWebRealtimeManagers.getOrCreateEntry(plugin.getRealtimeId());
		Wrapper r = (Wrapper) t.getValue();
		if (r == null)
			t.setValue(r = new Wrapper(plugin.getRealtimeId()));
		r.setInner(plugin);
		CH.putOrThrow(this.amiWebRealtimeManagers, r.getRealtimeId(), r);
		for (AmiWebManagersListener i : this.listeners)
			i.onProcesserAdded(plugin);
		plugin.addToDomManager();
	}
	public void removeProcessor(String realtimeId) {
		OH.assertTrue(SH.startsWith(realtimeId, PROCESSOR));
		CH.removeOrThrow(this.amiWebRealtimeManagers, realtimeId);
		AmiWebRealtimeProcessor rp = this.amiWebRealtimeProcessors.get(realtimeId);
		for (AmiWebManagersListener i : this.listeners)
			i.onProcesserRemoved(rp);
		this.amiWebRealtimeProcessors.remove(realtimeId);
		rp.close();
		rp.removeFromDomManager();
	}

	public void onProcessorRenamed(String oldAdn, String newAdn) {
		OH.assertTrue(SH.startsWith(oldAdn, PROCESSOR));
		OH.assertTrue(SH.startsWith(newAdn, PROCESSOR));
		CH.removeOrThrow(this.amiWebRealtimeManagers, oldAdn);
		AmiWebRealtimeProcessor processor = CH.removeOrThrow(this.amiWebRealtimeProcessors, oldAdn);
		CH.putOrThrow(this.amiWebRealtimeManagers, newAdn, processor);
		CH.putOrThrow(this.amiWebRealtimeProcessors, newAdn, processor);
		for (AmiWebManagersListener i : this.listeners)
			i.onProcesserRenamed(processor, oldAdn, newAdn);
	}

	private Map<String, AmiWebRealtimeProcessorPlugin> processorPlugins = new HashMap<String, AmiWebRealtimeProcessorPlugin>();

	private AmiWebRealtimeProcessorPlugin getProcessorPlugin(String type) {
		return CH.getOrThrow(processorPlugins, type);
	}

	public Map<String, AmiWebRealtimeProcessorPlugin> getProcessorPlugins() {
		return this.processorPlugins;
	}

	public void setRealtimeProcessorPlugins(Map<String, AmiWebRealtimeProcessorPlugin> realtimeProcessorPlugins) {
		this.processorPlugins.clear();
		this.processorPlugins.putAll(realtimeProcessorPlugins);
	}

	public boolean addListener(AmiWebManagersListener l) {
		if (this.listeners.contains(l))
			return false;
		boolean r = this.listeners.add(l);
		LH.fine(log, "SystemObjects Remove Listener: ", SH.toObjectStringSimple(l), " Count: ", this.listeners.size());
		return r;
	}

	public boolean removeListener(AmiWebManagersListener l) {
		boolean r = this.listeners.remove(l);
		LH.fine(log, "SystemObjects Remove Listener: ", SH.toObjectStringSimple(l), " Count: ", this.listeners.size());
		return r;
	}

	public AmiWebRealtimeProcessor getRealtimeProcessor(String typeAndName) {
		return this.amiWebRealtimeProcessors.get(typeAndName);
	}

	public Collection<AmiWebRealtimeProcessor> getRealtimeProcessors() {
		return this.amiWebRealtimeProcessors.values();
	}

	//	public Table executeQuery(String sql, SqlProcessor sqlProcessor, TimeoutController timeoutController, int limit) {
	//		Tableset tablesMap = new AmiWebRtTableSet(this);
	//		return sqlProcessor.process(sql, tablesMap, timeoutController, limit);
	//	}
	//
	//	public class AmiWebRtTableSet implements Tableset {
	//
	//		public AmiWebRtTableSet(AmiWebManagers amiWebManagers) {
	//			// TODO Auto-generated constructor stub
	//		}
	//
	//		@Override
	//		public Table getTable(String name) {
	//			Table r = getTableNoThrow(name);
	//			if (r == null)
	//				throw new RuntimeException("Table not found or not subscribed to: " + name);
	//			return r;
	//		}
	//
	//		@Override
	//		public Table getTableNoThrow(String name, Map<String, Object> vars) {
	//			Table r = getTableNoThrow(name);
	//			if (r != null)
	//				return r;
	//			Object o = vars == null ? null : vars.get(name);
	//			if (o instanceof Table)
	//				return (Table) o;
	//			return null;
	//		}
	//
	//		@Override
	//		public Table removeTable(String name) {
	//			throw new ExpressionParserException(-1, "Operation not supported");
	//		}
	//
	//		@Override
	//		public void putTable(String name, Table table) {
	//			throw new ExpressionParserException(-1, "Operation not supported");
	//		}
	//
	//		@Override
	//		public void putTable(Table table) {
	//			throw new ExpressionParserException(-1, "Operation not supported");
	//
	//		}
	//
	//		@Override
	//		public Iterable<String> getTableNamesSorted() {
	//			return CH.sort(getTableNames());
	//		}
	//
	//		@Override
	//		public Set<String> getTableNames() {
	//			return amiWebRealtimeManagers.keySet();
	//		}
	//		@Override
	//		public Table getTableNoThrow(String name) {
	//			AmiWebRealtimeObjectManager t = getAmiObjectsByTypeIfExists(name);
	//			return AmiWebUtils.toTable(t);
	//
	//		}
	//		@Override
	//		public void clearTables() {
	//			throw new ExpressionParserException(-1, "Operation not supported");
	//		}
	//
	//	}

	public void onListenerAdded(AmiWebRealtimeObjectManager objects, AmiWebRealtimeObjectListener listener) {
		for (AmiWebManagersListener i : this.listeners)
			i.onRealtimeListenerAdded(objects, listener);
	}

	public void onListenerRemoved(AmiWebRealtimeObjectManager objects, AmiWebRealtimeObjectListener listener) {
		for (AmiWebManagersListener i : this.listeners)
			i.onRealtimeListenerRemoved(objects, listener);
	}

	public AmiWebService getService() {
		return this.service;
	}
}
