package com.vortex.web.portlet.tables;

import java.util.Collections;

import com.f1.base.Row;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.WebTableFilteredSetFilter;
import com.f1.utils.CH;
import com.f1.utils.structs.LongKeyMap;
import com.vortex.client.VortexClientF1AppState;
import com.vortex.client.VortexClientF1AppState.AgentWebObject;
import com.vortex.client.VortexClientF1AppStateListener;
import com.vortex.ssoweb.NodeSelectionInterPortletMessage;
import com.vortex.ssoweb.SSoWebUtils;
import com.vortex.web.messages.VortexF1AppIdInterPortletMessage;

public abstract class VortexWebF1AppAbstractTablePortlet<T extends VortexClientF1AppState.AgentWebObject<?>> extends VortexWebTablePortlet implements
		VortexClientF1AppStateListener {
	final private BasicPortletSocket maskSocket;
	final private BasicPortletSocket f1appSocket;
	public static final String APPNAME = "appName";
	final private Class<T> f1AppEntityType;
	private final LongKeyMap<Row> objectsToRows = new LongKeyMap<Row>();

	public VortexWebF1AppAbstractTablePortlet(PortletConfig portletConfig, FastWebTable nodeType, Class<T> f1EntityType) {
		super(portletConfig, nodeType);
		this.f1AppEntityType = f1EntityType;
		this.maskSocket = addSocket(false, "selection", "Node Selection", false, null, CH.s(NodeSelectionInterPortletMessage.class));
		this.f1appSocket = addSocket(false, "f1appid", "F1 Application", false, null, CH.s(VortexF1AppIdInterPortletMessage.class));
	}
	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		if (localSocket == f1appSocket) {
			VortexF1AppIdInterPortletMessage msg = (VortexF1AppIdInterPortletMessage) message;
			getTable().setExternalFilter(new WebTableFilteredSetFilter(getTable().getColumn("apid"), msg.getAppIds()));
		} else if (localSocket == maskSocket) {
			NodeSelectionInterPortletMessage msg = (NodeSelectionInterPortletMessage) message;
			SSoWebUtils.applyFilter(msg, getTable(), Collections.EMPTY_MAP);
		} else
			super.onMessage(localSocket, remoteSocket, message);
	}
	public void setTable(FastWebTable table) {
		super.setTable(table);
		agentManager.addF1AppListener(this);
		for (VortexClientF1AppState app : agentManager.getJavaAppStates())
			this.onF1AppAdded(app);
	}

	public void onClosed() {
		agentManager.removeF1AppListener(this);
		super.onClosed();
	}

	public boolean isInterested(AgentWebObject<?> awo) {
		return f1AppEntityType.isAssignableFrom(awo.getClass());
	}

	abstract protected Row createAndAddRow(T node);
	abstract protected void updateRow(Row row, T node);
	abstract protected Iterable<T> getEntitiesForSnapshot(VortexClientF1AppState f1AppState);

	@Override
	public void onF1AppEntityAdded(AgentWebObject<?> added) {
		if (!isInterested(added))
			return;
		Row row = createAndAddRow(f1AppEntityType.cast(added));
		objectsToRows.putOrThrow(added.getId(), row);
		if (!getTable().hasSelectedRows())
			onVortexRowsChanged();
	}

	@Override
	public void onF1AppEntityUpdated(AgentWebObject<?> updated) {
		if (!isInterested(updated))
			return;
		Row row = objectsToRows.getOrThrow(updated.getId());
		updateRow(row, f1AppEntityType.cast(updated));
	}

	@Override
	public void onF1AppEntityRemoved(AgentWebObject<?> removed) {
		if (!isInterested(removed))
			return;
		Row row = objectsToRows.removeOrThrow(removed.getId());
		removeRow(row);
		if (!getTable().hasSelectedRows())
			onVortexRowsChanged();
	}
	@Override
	public void onF1AppAdded(VortexClientF1AppState appState) {
		Iterable<T> entities = getEntitiesForSnapshot(appState);
		for (T entity : entities)
			onF1AppEntityAdded(entity);
		if (!getTable().hasSelectedRows())
			onVortexRowsChanged();
	}
	@Override
	public void onF1AppRemoved(VortexClientF1AppState existing) {
		Iterable<T> entities = getEntitiesForSnapshot(existing);
		for (T entity : entities)
			onF1AppEntityRemoved(entity);
		if (!getTable().hasSelectedRows())
			onVortexRowsChanged();
	}
	@Override
	public void onEyeDisconnected() {
		objectsToRows.clear();
		getTable().clear();
		onVortexRowsChanged();
	}

	protected void onVortexRowsChanged() {
	}

	@Override
	public void onVisibleRowsChanged(FastWebTable fastWebTable) {
		super.onVisibleRowsChanged(fastWebTable);
		onVortexRowsChanged();
	}
	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
		super.onSelectedChanged(fastWebTable);
		onVortexRowsChanged();
	}

}