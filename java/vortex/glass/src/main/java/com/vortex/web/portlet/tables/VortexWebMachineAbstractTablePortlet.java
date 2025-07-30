package com.vortex.web.portlet.tables;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.base.Row;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.WebTableFilteredSetFilter;
import com.f1.utils.CH;
import com.f1.utils.LocalToolkit;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.table.RowFilter;
import com.f1.vortexcommon.msg.VortexEntity;
import com.vortex.client.VortexClientEntity;
import com.vortex.client.VortexClientExpectation;
import com.vortex.client.VortexClientMachine;
import com.vortex.client.VortexClientMachineListener;
import com.vortex.web.messages.VortexMachineIdInterPortletMessage;
import com.vortex.web.portlet.forms.VortexWebExpectationFormPortlet;

public abstract class VortexWebMachineAbstractTablePortlet<V extends VortexEntity, T extends VortexClientEntity<V>> extends VortexWebTablePortlet implements WebContextMenuFactory,
		VortexClientMachineListener {

	private static final Logger log = Logger.getLogger(VortexWebMachineAbstractTablePortlet.class.getName());
	final LongKeyMap<Row> agentToRow = new LongKeyMap<Row>();
	final private BasicPortletSocket miidSocket;
	final private byte nodeType;

	public VortexWebMachineAbstractTablePortlet(PortletConfig portletConfig, byte nodeType) {
		super(portletConfig, null);
		this.nodeType = nodeType;
		this.miidSocket = addSocket(false, "miid", "Machine ID", true, null, CH.s(VortexMachineIdInterPortletMessage.class));
	}

	@Override
	public void setTable(FastWebTable table) {
		super.setTable(table);
		agentManager.addMachineListener(this);
		for (VortexClientMachine i : agentManager.getAgentMachines())
			onMachineAdded(i);
	}

	public Row getRow(long id) {
		return agentToRow.get(id);
	}

	@Override
	public void onClosed() {
		agentManager.removeMachineListener(this);
		super.onClosed();
	}

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		if (localSocket == miidSocket) {
			VortexMachineIdInterPortletMessage msg = (VortexMachineIdInterPortletMessage) message;
			getTable().setExternalFilter(new WebTableFilteredSetFilter(getTable().getColumn(MIID), msg.getMiids()));
			if (getIsEyeConnected())
				onVortexRowsChanged();
			//} else if (localSocket == maskSocket) {
			//NodeSelectionInterPortletMessage msg = (NodeSelectionInterPortletMessage) message;
			//SSoWebUtils.applyFilter(msg, getTable(), Collections.EMPTY_MAP);
		} else
			super.onMessage(localSocket, remoteSocket, message);
	}

	public void remove(T node) {
		Row row = agentToRow.removeOrThrow(node.getId());
		removeRow(row);
		if (getIsEyeConnected())
			onVortexRowsChanged();
	}
	public void add(T node) {
		V a = node.getData();
		Row value = createAndAddRow(node);
		agentToRow.putOrThrow(node.getId(), value);
		if (getIsEyeConnected())
			onVortexRowsChanged();
	}

	public void update(T node) {
		V a = node.getData();
		Row row = agentToRow.getOrThrow(node.getId());
		updateRow(row, node);
	}
	@Override
	public void onMachineAdded(VortexClientMachine machine) {
		for (VortexClientEntity<?> node : machine.getNodes(nodeType)) {
			add((T) node);
		}
	}

	@Override
	public void onMachineUpdated(VortexClientMachine machine) {
	}

	@Override
	public void onMachineStale(VortexClientMachine machine) {
		for (VortexClientEntity<?> node : machine.getNodes(nodeType))
			remove((T) node);
	}
	@Override
	public void onMachineActive(VortexClientMachine machine) {
	}

	@Override
	public void onMachineEntityAdded(VortexClientEntity<?> node) {
		if (node.getType() == nodeType) {
			add((T) node);
		}
	}

	@Override
	public void onMachineEntityUpdated(VortexClientEntity<?> node) {
		if (node.getType() == nodeType) {
			update((T) node);
		}
	}

	@Override
	public void onMachineEntityRemoved(VortexClientEntity<?> node) {
		if (node.getType() == nodeType) {
			remove((T) node);
		}
	}

	@Override
	public void onEyeDisconnected() {
		agentToRow.clear();
		getTable().clear();
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		super.onContextMenu(table, action);
		if ("create_exp".equals(action)) {
			VortexWebExpectationFormPortlet last = null;
			for (Row row : table.getSelectedRows()) {
				VortexWebExpectationFormPortlet dialog = new VortexWebExpectationFormPortlet(generateConfig());
				VortexClientEntity t = (VortexClientEntity) row.get("data");
				if (t != null)
					dialog.setTemplate(t);
				if (last != null)
					last.setNextForm(dialog);
				else
					getManager().showDialog("Add Expectation", dialog);
				last = dialog;

			}
		}
		/*
		action = parseContext(action);
		List<Row> selectedRows = table.getSelectedRows();
		if ("history".equals(action)) {
			List<Long> ids = new ArrayList<Long>();
			AgentFileSystem a = null;
			for (Row row : selectedRows) {
				a = (AgentFileSystem) getIdToRevision().get(row.get("id"));
				ids.add(a.getId());
			}
			if (null != a)
				service.getHistory(AgentHistoryRequest.TYPE_FILE_SYSTEM, ids, getPortletId(), false, false);
		}
		*/

	}
	abstract protected Row createAndAddRow(T node);
	abstract protected void updateRow(Row row, T node);

	@Override
	public WebMenu createMenu(WebTable table) {
		List<WebMenuItem> children = new ArrayList<WebMenuItem>();

		children.add(new BasicWebMenuLink("Create Expectation", true, "create_exp"));
		BasicWebMenu r = new BasicWebMenu("", true, children);
		return r;

	}

	public class ExpectationFilter implements RowFilter {

		private Set<Long> expectationIds;

		public ExpectationFilter(Set<Long> expectationIds) {
			this.expectationIds = expectationIds;
		}

		@Override
		public boolean shouldKeep(Row row, LocalToolkit localToolkit) {
			VortexClientEntity<?> node = (VortexClientEntity<?>) row.get("data");
			if (node != null) {
				VortexClientExpectation exp = node.getMatchingExpectation();
				if (exp != null)
					return expectationIds.contains(exp.getId());

			}
			return false;
		}

	}

	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
		super.onSelectedChanged(fastWebTable);
		if (getIsEyeConnected())
			onVortexRowsChanged();
	}

	@Override
	public void onMachineRemoved(VortexClientMachine machine) {
		onMachineStale(machine);
	}

	protected void onVortexRowsChanged() {
	}
	@Override
	public void onVisibleRowsChanged(FastWebTable fastWebTable) {
		if (getIsEyeConnected())
			super.onVisibleRowsChanged(fastWebTable);
		//onVortexRowsChanged();
	}

}
