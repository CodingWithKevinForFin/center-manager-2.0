package com.f1.ami.web.dm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.web.AmiWebAmiScriptCallbacks;
import com.f1.ami.web.AmiWebCenterListener;
import com.f1.ami.web.AmiWebDmPortlet;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebManager;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebSnapshotManager;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.portlets.AmiWebDmUtils;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.structs.Tuple3;

public class AmiWebDmManagerImpl implements AmiWebDmManager, AmiWebCenterListener, AmiWebDmLayoutListener {

	private static final Logger log = LH.get();

	private static final Comparator<AmiWebDm> LABEL_SORTER = new Comparator<AmiWebDm>() {

		@Override
		public int compare(AmiWebDm o1, AmiWebDm o2) {
			return OH.compare(o1.getDmName(), o2.getDmName());
		}
	};

	private AmiWebService service;
	private List<AmiWebDmManagerListener> listeners = new ArrayList<AmiWebDmManagerListener>();
	private Map<String, AmiWebDmsImpl> aliasDotNameToDm = new HashMap<String, AmiWebDmsImpl>();

	private Map<String, AmiWebDmLink> dmLinks = new HashMap<String, AmiWebDmLink>();
	private Map<String, AmiWebDmLink> aliasDotRedIdToRel = new HashMap<String, AmiWebDmLink>();

	public AmiWebDmManagerImpl(AmiWebService service) {
		this.service = service;
		this.service.getPrimaryWebManager().addClientConnectedListener(this);
	}

	@Override
	public AmiWebService getService() {
		return this.service;
	}

	@Override
	public List<AmiWebDmsImpl> getDmsSorted(String layoutAlias) {
		List<AmiWebDmsImpl> r = new ArrayList<AmiWebDmsImpl>();
		for (Entry<String, AmiWebDmsImpl> entry : aliasDotNameToDm.entrySet()) {
			String fullAlias = entry.getKey();
			boolean isParentOrChild = AmiWebUtils.isParentAliasOrSame(layoutAlias, fullAlias);
			if (isParentOrChild)
				r.add(entry.getValue());
		}
		Collections.sort(r, LABEL_SORTER);
		return r;
	}

	public String getDmAri(String dmName, String layoutAlias) {
		return AmiWebDomObject.ARI_TYPE_DATAMODEL + ":" + AmiWebUtils.getFullAlias(layoutAlias, dmName);
	}

	public boolean isDmRegistered(String dmName, String layoutAlias) {
		return this.aliasDotNameToDm.containsKey(AmiWebUtils.getFullAlias(layoutAlias, dmName));
	}

	@Override
	public String getNextDmName(String name, String layoutAlias) {
		Set<String> existingDmNames = this.getDmNames(layoutAlias);
		// Get the next available dmName
		String dmName = SH.getNextId(name, existingDmNames);
		String dmFullAri = this.getDmAri(dmName, layoutAlias);

		if (this.isDmRegistered(dmName, layoutAlias))
			throw new IllegalStateException("Dm name with layout alias already added to dm manager");
		// If dm is already managed 
		if (this.service.getDomObjectsManager().isManaged(dmFullAri))
			throw new IllegalStateException("Ari already added to dom objects manager");
		// Alternatively we could do the following keep generating new id's until we succeed
		/*
		while (this.service.getDomObjectsManager().isManaged(dmFullAri)) {
			existingDmNames.add(dmName);
			dmName = SH.getNextId(name, existingDmNames);
			dmFullAri = this.getDmAri(dmName, layoutAlias);
		}
		*/
		return dmName;
	}
	@Override
	public void addDm(AmiWebDmsImpl datamodel) {

		AmiWebDmsImpl amiWebDmsImpl = (AmiWebDmsImpl) datamodel;
		// Check if we can add dm before adding
		if (amiWebDmsImpl.isRegisteredWithDmManager())
			throw new IllegalStateException("Already added to dm manager");
		if (this.service.getDomObjectsManager().isManaged(datamodel.getAri()))
			throw new IllegalStateException("Ari already added to dom objects manager");

		CH.putOrThrow(this.aliasDotNameToDm, datamodel.getAmiLayoutFullAliasDotId(), amiWebDmsImpl);
		if (this.onInitDone)
			amiWebDmsImpl.setRegisteredWithDmManager();
		if (amiWebDmsImpl.getMaxRequeryMs() > 0 || amiWebDmsImpl.hasCrontab())
			this.autoRequeryRequests.add((AmiWebDmsImpl) datamodel);
		datamodel.addToDomManager();
		fireDmAdded(datamodel);
	}

	public boolean canRunDm() {
		return onInitDone && this.getService().getPrimaryWebManager().getIsEyeConnected();
	}

	@Override
	public AmiWebDmsImpl removeDm(String aliasDotName) {
		AmiWebDmsImpl r = this.aliasDotNameToDm.remove(aliasDotName);
		if (r != null) {
			r.close();
			fireDmRemoved(r);
			r.removeFromDomManager();
			queuedRequests.remove(r);
			autoRequeryRequests.remove(r);
		}
		return r;
	}

	private void fireDmAdded(AmiWebDm dm) {
		for (AmiWebDmManagerListener i : this.listeners)
			i.onDmAdded(this, dm);
	}
	protected void fireDmUpdated(AmiWebDmsImpl dm) {
		for (AmiWebDmManagerListener i : this.listeners)
			i.onDmUpdated(this, dm);
		AmiWebDmsImpl dms = (AmiWebDmsImpl) dm;
		if (dms.getMaxRequeryMs() > 0 || dms.hasCrontab())
			this.autoRequeryRequests.add((AmiWebDmsImpl) dm);
		else
			this.autoRequeryRequests.remove((AmiWebDmsImpl) dm);
	}
	private void fireDmRemoved(AmiWebDm dm) {
		for (AmiWebDmManagerListener i : this.listeners)
			i.onDmRemoved(this, dm);
	}
	private void fireDmLinkAdded(AmiWebDmLink dmLink) {
		for (AmiWebDmManagerListener i : this.listeners)
			i.onDmLinkAdded(this, dmLink);
		AmiWebPortlet p1 = dmLink.getSourcePanelNoThrow();
		AmiWebPortlet p2 = dmLink.getTargetPanelNoThrow();
		if (p1 != null)
			p1.onLinkingChanged(dmLink);
		if (p2 != null)
			p2.onLinkingChanged(dmLink);
		AmiWebDmsImpl dm = dmLink.getTargetDm();
	}
	private void fireDmLinkRemoved(AmiWebDmLink dmLink) {
		for (AmiWebDmManagerListener i : this.listeners)
			i.onDmLinkRemoved(this, dmLink);
		this.service.getDesktop().flagUpdateWindowLinks();
		AmiWebPortlet p1 = dmLink.getSourcePanelNoThrow();
		AmiWebPortlet p2 = dmLink.getTargetPanelNoThrow();
		if (p1 != null)
			p1.onLinkingChanged(dmLink);
		if (p2 != null)
			p2.onLinkingChanged(dmLink);
		AmiWebDmsImpl dm = dmLink.getTargetDm();
	}

	private IdentityHashSet<AmiWebDmsImpl> inflightDmsRequests = new IdentityHashSet<AmiWebDmsImpl>();
	private IdentityHashMap<AmiWebDmsImpl, Tuple3<AmiWebDmRequest, AmiDebugManager, Boolean>> queuedRequests = new IdentityHashMap<AmiWebDmsImpl, Tuple3<AmiWebDmRequest, AmiDebugManager, Boolean>>();
	private IdentityHashSet<AmiWebDmsImpl> autoRequeryRequests = new IdentityHashSet<AmiWebDmsImpl>();
	private boolean inInit;

	private boolean onInitDone;
	private boolean needsFireQueryOnStartup = false;

	private boolean canQueryNow(AmiWebDmsImpl dms, long now) {
		if (inflightDmsRequests.contains(dms))
			return false;
		else if (inInit)
			return false;
		else if (!dms.canQueryAtThisTime(now))
			return false;
		else if (!getService().getPrimaryWebManager().getIsEyeConnected())
			return false;
		return true;
	}

	@Override
	public void addDmManagerListener(AmiWebDmManagerListener listener) {
		OH.assertNotNull(listener);
		this.listeners.add(listener);
		if (LH.isFine(log)) {
			if (listener instanceof AmiWebAmiScriptCallbacks)
				LH.fine(log, service.getUserName(), ": ADDING  >>>>>>>>>>>>>>>>>>>>: ", SH.toObjectStringSimple(listener) + "  ",
						SH.toObjectString(((AmiWebAmiScriptCallbacks) listener).getThis()), " Count: ", this.listeners.size());
			else
				LH.fine(log, service.getUserName(), ": ADDING  >>>>>>>>>>>>>>>>>>>>: ", SH.toObjectStringSimple(listener), " Count: ", this.listeners.size());
		}
	}
	@Override
	public void removeDmManagerListener(AmiWebDmManagerListener listener) {
		this.listeners.remove(listener);
		if (LH.isFine(log)) {
			if (listener instanceof AmiWebAmiScriptCallbacks)
				LH.fine(log, service.getUserName(), ": REMOVING <<<<<<<<<<<<<<<<<<<: ", SH.toObjectStringSimple(listener) + "  ",
						SH.toObjectString(((AmiWebAmiScriptCallbacks) listener).getThis()), " Count: ", this.listeners.size());
			else
				LH.fine(log, service.getUserName(), ": REMOVING <<<<<<<<<<<<<<<<<<<: ", SH.toObjectStringSimple(listener), " Count: ", this.listeners.size());
		}
	}

	@Override
	public void addDmLink(AmiWebDmLink dmLink) {
		dmLink.bind();
		CH.putOrThrow(this.dmLinks, dmLink.getLinkUid(), dmLink);
		//		dmLink.setRelationshipId(getNextRelationshipId(dmLink.getAmiLayoutFullAlias(), AmiUtils.toValidVarName(dmLink.getRelationshipId())));
		this.aliasDotRedIdToRel.put(dmLink.getAmiLayoutFullAliasDotId(), dmLink);
		dmLink.updateAri();
		dmLink.addToDomManager();
		fireDmLinkAdded(dmLink);
	}
	@Override
	public AmiWebDmLink getDmLink(String linkId) {
		return this.dmLinks.get(linkId);
	}
	@Override
	public Collection<AmiWebDmLink> getDmLinks() {
		return this.dmLinks.values();
	}
	@Override
	public AmiWebDmLink removeDmLink(String linkId) {
		AmiWebDmLink r = this.dmLinks.remove(linkId);
		if (r != null) {
			AmiWebDmUtils.sendRequestDeleteRelationship(getService(), r, service.createStackFrame(service));
			this.aliasDotRedIdToRel.remove(r.getAmiLayoutFullAliasDotId());
			r.close();
			fireDmLinkRemoved(r);
			r.removeFromDomManager();
		}
		return r;
	}

	public static Comparator<Map<String, Object>> LBL_COMPARATOR = new Comparator<Map<String, Object>>() {

		@Override
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			return OH.compare((String) o1.get("lbl"), (String) o2.get("lbl"));
		}
	};
	public static Comparator<Map<String, Object>> RELID_COMPARATOR = new Comparator<Map<String, Object>>() {

		@Override
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			return OH.compare((String) o1.get("relid"), (String) o2.get("relid"));
		}
	};

	@Override
	public AmiWebDmsImpl importDms(String layoutAlias, Map<String, Object> config, StringBuilder warningsSink, boolean isTransient) {
		List<Map<String, Object>> dms = (List<Map<String, Object>>) CH.getOr(config, "dms", null);
		// If dms isn't null has multiple datamodels
		if (dms != null) {
			this.init(layoutAlias, config, null, warningsSink);
			return null;
		} else {
			String dmLbl = Caster_String.INSTANCE.cast(config.get("lbl"));
			if (isTransient)
				dmLbl = service.getVarsManager().toTransientId(dmLbl);
			else
				dmLbl = service.getVarsManager().fromTransientId(dmLbl);
			dmLbl = this.getNextDmName(dmLbl, layoutAlias);
			config.put("lbl", dmLbl);
			AmiWebDmsImpl t = new AmiWebDmsImpl(this, layoutAlias, dmLbl);
			t.setTransient(isTransient);
			StringBuilder sink = new StringBuilder();
			t.init(layoutAlias, config, null, sink);
			this.addDm(t);
			t.getCallbacks().recompileAmiscript();
			if (this.canRunDm())
				t.doStartup();
			return t;
		}
	}
	@Override
	public void init(String alias, Map<String, Object> val, Map<String, String> origToNewPortletIdMapping, StringBuilder warningsSink) {
		if (CH.isEmpty(val))
			return;
		this.inInit = true;
		try {

			List<Map<String, Object>> links = (List<Map<String, Object>>) val.get("lnk");
			List<Map<String, Object>> dms = (List<Map<String, Object>>) val.get("dms");
			List<Map<String, Object>> ds = (List<Map<String, Object>>) val.get("ds");
			if (links != null)
				for (Map<String, Object> link : links) {
					AmiWebDmLinkImpl dmlink = new AmiWebDmLinkImpl(this.service.getDmManager());
					dmlink.setAmiLayoutFullAlias(alias);
					dmlink.init(link, origToNewPortletIdMapping, warningsSink);
					addDmLink(dmlink);
				}

			if (dms != null)
				for (Map<String, Object> i : dms) {
					AmiWebDmsImpl dm = new AmiWebDmsImpl(this, alias, null);
					try {
						dm.init(alias, i, origToNewPortletIdMapping, warningsSink);
					} catch (Exception e) {
						throw new RuntimeException("Error for dm: " + dm.getDmName(), e);
					}
					this.addDm(dm);
					if (this.canRunDm())
						dm.doStartup();
				}
			//Backwards compatibility for loading dmts
			{
				List<Map<String, Object>> dmt = (List<Map<String, Object>>) val.get("dmt");
				if (dmt != null)
					for (Map<String, Object> i : dmt) {
						AmiWebDmsImpl dm = new AmiWebDmsImpl(this, alias, null);
						dm.init(alias, i, origToNewPortletIdMapping, warningsSink);
						this.addDm(dm);
						if (this.canRunDm())
							dm.doStartup();
					}
			}

		} catch (Exception e) {
			LH.warning(log, "Error initializing dms: ", e);
		} finally {
			this.inInit = false;
		}
	}
	@Override
	public Map<String, Object> getConfiguration(String alias) {
		final Map<String, Object> r = new HashMap<String, Object>();
		final List<Map<String, Object>> links = new ArrayList<Map<String, Object>>(this.dmLinks.size());
		final List<Map<String, Object>> dms = new ArrayList<Map<String, Object>>(this.aliasDotNameToDm.size());
		//		final List<Map<String, Object>> dmt = new ArrayList<Map<String, Object>>(this.uidToDm.size());
		final List<Map<String, Object>> ds = new ArrayList<Map<String, Object>>();

		for (AmiWebDmLink link : this.dmLinks.values()) {
			if (!link.isTransient() && OH.eq(alias, link.getAmiLayoutFullAlias()))
				links.add(link.getConfiguration());
		}
		for (AmiWebDmsImpl dm : this.aliasDotNameToDm.values()) {
			if (dm.isTransient())
				continue;
			if (OH.ne(alias, dm.getAmiLayoutFullAlias()))
				continue;
			dms.add(dm.getConfiguration());
		}
		// Get Used Datasources better for source control different users can have different datasources
		Set<String> usedDatasources = new HashSet<String>();
		for (AmiWebDmsImpl dm : this.aliasDotNameToDm.values()) {
			usedDatasources.add(dm.getDefaultDatasource());
		}
		//		Collections.sort(dmt, LBL_COMPARATOR);
		Collections.sort(dms, LBL_COMPARATOR);
		Collections.sort(links, RELID_COMPARATOR);

		//		if (!dmt.isEmpty())
		//			r.put("dmt", AmiWebUtils.sortJsonList(dmt, "lbl"));
		if (!dms.isEmpty())
			r.put("dms", AmiWebUtils.sortJsonList(dms, "lbl"));
		if (!links.isEmpty())
			r.put("lnk", AmiWebUtils.sortJsonList(links, "relid"));
		if (!ds.isEmpty())
			r.put("ds", AmiWebUtils.sortJsonList(ds, "name"));
		return r;
	}

	public void onAmiPortletClosed(AmiWebPortlet p) {
		if (p instanceof AmiWebDmPortlet) {
			AmiWebDmPortlet dmp = (AmiWebDmPortlet) p;
			for (String i : CH.l(dmp.getUsedDmAliasDotNames())) {
				AmiWebDmsImpl dm = getDmByAliasDotName(i);
				if (dm != null)
					dm.removeDmListener(dmp);
			}
		}
	}
	@Override
	public void clear() {
		//Strategy: keep removing dms that have no dependencies until all are removed.
		while (!this.aliasDotNameToDm.isEmpty()) {
			int size = this.aliasDotNameToDm.size();
			for (AmiWebDmsImpl t : CH.l(this.aliasDotNameToDm.values())) {
				if (t.getUpperDmAliasDotNames().isEmpty()) {
					try {
						t.close();
					} catch (Exception e) {
						LH.warning(log, service.getUserDialogStyleManager(), ": Error closing dm: ", e);
					}
					this.aliasDotNameToDm.remove(t.getAmiLayoutFullAliasDotId());
				}
			}
			if (size == this.aliasDotNameToDm.size()) {
				LH.warning(log, service.getUserName(), ": ", service.getUserDialogStyleManager(), ": Error closing dms because of circular dependency: ", this.aliasDotNameToDm);
				break;
			}
		}
		this.aliasDotNameToDm.clear();
		this.inflightDmsRequests.clear();
		this.queuedRequests.clear();
		this.autoRequeryRequests.clear();
		this.dmLinks.clear();
		this.aliasDotRedIdToRel.clear();
		this.onInitDone = false;
		onCurrentlyRunningQueriesCountChanged();
		for (AmiWebDmManagerListener listener : this.listeners) {
			if (listener instanceof AmiWebAmiScriptCallbacks)
				LH.warning(log, service.getUserName(), ": ###  STILL  ################: ", SH.toObjectStringSimple(listener) + "  ",
						SH.toObjectString(((AmiWebAmiScriptCallbacks) listener).getThis()), " Count: ", this.listeners.size());
			else
				LH.warning(log, service.getUserName(), ": ###  STILL  ################: ", SH.toObjectStringSimple(listener), " Count: ", this.listeners.size());
		}
		this.listeners.clear();
	}
	@Override
	public Set<String> getDmNames(String alias) {
		Set<String> r = new HashSet<String>(this.aliasDotNameToDm.size());
		for (AmiWebDm i : this.aliasDotNameToDm.values())
			if (OH.eq(alias, i.getAmiLayoutFullAlias()))
				r.add(i.getDmName());
		return r;
	}
	@Override
	public Collection<AmiWebDmsImpl> getDatamodels() {
		return this.aliasDotNameToDm.values();
	}

	public void queueRequest(AmiWebDmsImpl dms, Tuple3<AmiWebDmRequest, AmiDebugManager, Boolean> queued) {
		this.queuedRequests.put(dms, queued);
	}

	private ArrayList<AmiWebDmsImpl> toQueryBuf = new ArrayList<AmiWebDmsImpl>();

	@Override
	public void onFrontendCalled(boolean isFirst) {
		if (isFirst) {
			this.needsFireQueryOnStartup = true;
			fireQueryOnStartups();
		} else if (!autoRequeryRequests.isEmpty() || !this.queuedRequests.isEmpty()) {
			long now = getService().getPortletManager().getNow();
			boolean isLocked = service.getDesktop().getIsLocked();
			toQueryBuf.clear();
			for (AmiWebDmsImpl i : this.autoRequeryRequests) {
				if (this.inflightDmsRequests.contains(i))
					continue;
				if ((shouldRun(i.getNextRunTime(), now) && (i.getQueryOnMode() == AmiWebDmsImpl.QUERY_ON_STARTUP || i.hasVisiblePortlet()))
						|| shouldRun(i.getNextCrontabTime(), now))
					if (isLocked || service.getAmiWebDmEditorsManager().getDmEditor(i.getAri()) == null)
						toQueryBuf.add(i);
			}
			for (int n = 0; n < toQueryBuf.size(); n++)
				try {
					toQueryBuf.get(n).processAutoRequery();
				} catch (Exception e) {
					LH.warning(log, "error running query", e);
				}
			toQueryBuf.clear();
			for (AmiWebDmsImpl i : this.queuedRequests.keySet())
				if (canQueryNow(i, now))
					toQueryBuf.add(i);
			for (int n = 0; n < toQueryBuf.size(); n++) {
				AmiWebDmsImpl i = toQueryBuf.get(n);
				Tuple3<AmiWebDmRequest, AmiDebugManager, Boolean> runner = queuedRequests.remove(i);
				i.processRequestNow(runner.getA(), runner.getB(), runner.getC());
			}
			toQueryBuf.clear();
		}

	}

	private boolean shouldRun(long nextRunTime, long now) {
		return nextRunTime != -1L && nextRunTime <= now;
	}

	@Override
	public void onInitDone() {
		for (AmiWebDmsImpl i : this.aliasDotNameToDm.values())
			try {
				i.onInitDone();
				i.setRegisteredWithDmManager();
			} catch (Exception e) {
				LH.warning(log, "Error processing init done for: ", i, e);
			}

		for (AmiWebDmManagerListener i : this.listeners)
			try {
				i.onDmManagerInitDone();
			} catch (Exception e) {
				LH.warning(log, "Error processing init done for: ", i, e);
			}
		for (AmiWebDmLink i : this.dmLinks.values()) {
			i.onInitDone();
			i.bind();
		}
		this.onInitDone = true;
		verifyDms();
	}
	public int getCurrentlyRunningQueriesCount() {
		return this.inflightDmsRequests.size();
	}

	public void onDmsRunning(AmiWebDmsImpl dms) {
		if (!this.inflightDmsRequests.add(dms))
			throw new IllegalStateException("already running: " + dms.getDmUid());
		onCurrentlyRunningQueriesCountChanged();
	}
	public void onDmsFinished(AmiWebDmsImpl dms) {
		if (!this.inflightDmsRequests.remove(dms))
			throw new IllegalStateException("not running: " + dms.getDmUid());
		onCurrentlyRunningQueriesCountChanged();
	}

	@Override
	public Set<AmiWebDmsImpl> getCurrentlyRunningDms() {
		return this.inflightDmsRequests;
	}
	private void onCurrentlyRunningQueriesCountChanged() {
		if (getService().getDesktop() != null)
			getService().getDesktop().onDmsQueriesCountChanged(this.inflightDmsRequests.size());
	}
	@Override
	public List<AmiWebDmsImpl> getAndPausePlayingDatamodels() {
		List dms = new ArrayList<AmiWebDm>();
		for (AmiWebDm dm : this.getDatamodels()) {
			if (dm.isPlaying()) {
				dm.setIsPlay(false);
				dms.add(dm);
			}
		}
		return dms;

	}

	@Override
	public String getNextRelationshipId(String fullAlias, String suggested) {
		if (suggested == null)
			suggested = "REL1";
		suggested = AmiWebUtils.getFullAlias(fullAlias, suggested);

		return AmiWebUtils.getRelativeAlias(fullAlias, SH.getNextId(suggested, this.aliasDotRedIdToRel.keySet()));
	}
	@Override
	public void onPanelDmDependencyChanged(AmiWebDmPortlet target, String dmName, String tableName, boolean isAdd) {
		for (int i = 0, l = this.listeners.size(); i < l; i++)
			if (isAdd)
				this.listeners.get(i).onDmDependencyAdded(this, target, dmName, tableName);
			else
				this.listeners.get(i).onDmDependencyRemoved(this, target, dmName, tableName);
	}
	@Override
	public void onFilterDependencyChanged(AmiWebDmFilter target, String dmName, String tableName, boolean isAdd) {
		for (int i = 0, l = this.listeners.size(); i < l; i++)
			if (isAdd)
				this.listeners.get(i).onFilterDependencyAdded(this, target, dmName, tableName);
			else
				this.listeners.get(i).onFilterDependencyRemoved(this, target, dmName, tableName);
	}
	//	@Override
	//	public void onCenterDisconnected(AmiWebManager manager, IterableAndSize<AmiWebObject> objects) {
	//	}
	//	@Override
	//	public void onCenterSnapshotProcessed(AmiWebManager manager) {
	//		if (manager.getCenterDef().isPrimary())
	//			fireQueryOnStartups();
	//
	//	}

	@Override
	public List<AmiWebDmLink> getDmLinksToDmAliasDotName(String dmAliasDotName) {
		final List<AmiWebDmLink> r = new ArrayList<AmiWebDmLink>();
		final AmiWebDm dm = this.getDmByAliasDotName(dmAliasDotName);
		if (dm != null)
			for (AmiWebDmListener i : dm.getDmListeners())
				if (i instanceof AmiWebDmPortlet)
					r.addAll(((AmiWebDmPortlet) i).getDmLinksToThisPortlet());
		return r;
	}
	@Override
	public List<AmiWebDmPortlet> getPanelsForDmAliasDotName(String dmAliasDotName) {
		final List<AmiWebDmPortlet> r = new ArrayList<AmiWebDmPortlet>();
		final AmiWebDm dm = this.getDmByAliasDotName(dmAliasDotName);
		if (dm != null)
			for (AmiWebDmListener i : dm.getDmListeners()) {
				if (i instanceof AmiWebDmPortlet)
					r.add((AmiWebDmPortlet) i);
			}
		return r;
	}
	@Override
	public void onCenterConnectionStateChanged(AmiWebManager amiClientManager, byte state) {
		if (amiClientManager.getCenterDef().isPrimary()) {
			if (state == AmiWebSnapshotManager.STATE_CONNECTED)
				fireQueryOnStartups();
			else if (state == AmiWebSnapshotManager.STATE_DISCONNECTED)
				this.needsFireQueryOnStartup = true;
		}
	}
	private void fireQueryOnStartups() {
		if (!this.needsFireQueryOnStartup || !canRunDm())
			return;
		this.needsFireQueryOnStartup = false;
		// 1.rebuild
		// 2.login
		// 3.center goes from disconnected to connected
		for (AmiWebDm dms : this.aliasDotNameToDm.values()) {
			if (dms.getQueryOnStartup() || (dms.hasVisiblePortlet()
					&& (dms.getQueryOnMode() == AmiWebDmsImpl.QUERY_ON_VISIBLE || dms.getQueryOnMode() == AmiWebDmsImpl.QUERY_ON_VISIBLE_ONCE) && !dms.hasRanOnStartUp())) {
				dms.processRequest(dms.getRequestTableset(), getService().getDebugManager());
				this.queuedRequests.remove(dms);
			}
		}
	}

	@Override
	public void onVisibilityChanged(AmiWebDmLayoutManagerImpl manager, AmiWebDm dms, boolean isNowVisible) {
		if (!canRunDm())
			return;
		if (isNowVisible && dms != null) {
			switch (dms.getQueryOnMode()) {
				case AmiWebDmsImpl.QUERY_ON_STARTUP_AND_VISIBLE:
				case AmiWebDmsImpl.QUERY_ON_VISIBLE:
					dms.processRequest(dms.getRequestTableset(), this.service.getDebugManager());
					break;
				case AmiWebDmsImpl.QUERY_ON_VISIBLE_ONCE:
					if (dms.getStatisticEvals() == 0)
						dms.processRequest(dms.getRequestTableset(), this.service.getDebugManager());
					break;
			}
		}
	}

	@Override
	public AmiWebDmsImpl getDmByAliasDotName(String datamodelName) {
		return this.aliasDotNameToDm.get(datamodelName);
	}
	@Override
	public AmiWebDmLink getDmLinkByAliasDotRelationshipId(String id) {
		return this.aliasDotRedIdToRel.get(id);
	}

	public void onDmNameChanged(String oldAliasDotName, AmiWebDm dm) {
		CH.removeOrThrow(this.aliasDotNameToDm, oldAliasDotName);
		CH.putOrThrow(this.aliasDotNameToDm, dm.getAmiLayoutFullAliasDotId(), (AmiWebDmsImpl) dm);
		for (AmiWebDmManagerListener i : this.listeners)
			i.onDmNameChanged(this, oldAliasDotName, dm);
		for (AmiWebDmLink i : this.dmLinks.values()) {
			if (OH.eq(oldAliasDotName, i.getSourceDmAliasDotName()))
				i.setSourceDm(dm.getAmiLayoutFullAliasDotId(), i.getSourceDmTableName());
			if (OH.eq(oldAliasDotName, i.getTargetDmAliasDotName()))
				i.setTargetDm(dm.getAmiLayoutFullAliasDotId());
		}
	}

	@Override
	public Set<String> getDmLinkIdsByFullAlias(String fullAlias) {
		Set<String> r = new HashSet<String>();
		for (AmiWebDmLink i : this.dmLinks.values()) {
			if (OH.eq(fullAlias, i.getAmiLayoutFullAlias()))
				r.add(i.getRelationshipId());
		}
		return r;
	}

	public void recompileAmiscript() {
		for (AmiWebDmLink i : this.dmLinks.values())
			i.getAmiScript().recompileAmiscript();
		for (AmiWebDm i : this.aliasDotNameToDm.values())
			i.getCallbacks().recompileAmiscript();
	}

	@Override
	public void verifyDms() {
		for (AmiWebDmsImpl i : this.getDatamodels()) {
			i.verify();
		}
	}

}
