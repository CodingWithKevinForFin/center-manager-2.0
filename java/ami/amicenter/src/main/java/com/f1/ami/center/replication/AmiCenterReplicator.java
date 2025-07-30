package com.f1.ami.center.replication;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amicommon.AmiCenterDefinition;
import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.centerclient.AmiCenterClientListener;
import com.f1.ami.amicommon.centerclient.AmiCenterClientObjectMessage;
import com.f1.ami.amicommon.centerclient.AmiCenterClientObjectMessageImpl;
import com.f1.ami.center.sysschema.AmiSchema_CENTER;
import com.f1.ami.center.sysschema.AmiSchema_REPLICATION;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.ami.client.AmiCenterClient;
import com.f1.ami.client.AmiCenterClientConnection;
import com.f1.base.ObjectGeneratorForClass;
import com.f1.base.Password;
import com.f1.container.InputPort;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiCenterReplicator {

	private static final Set<String> MUST_SUBSCRIBE = CH.s(AmiConsts.TYPE_COLUMN, AmiConsts.TYPE_TABLE, AmiConsts.TYPE_INDEX);
	final private AmiCenterClient client;
	private AmiImdbImpl imdb;
	private ObjectGeneratorForClass<AmiCenterReplicationObjectsMessage> replicationObjectsMessageGenerator;
	private InputPort<AmiCenterReplicationObjectsMessage> objectsMessagePort;
	private ObjectGeneratorForClass<AmiCenterReplicationDisconnectedMessage> replicationDisconnectGenerator;
	private ObjectGeneratorForClass<AmiCenterReplicationConnectedMessage> replicationConnectGenerator;
	private InputPort<AmiCenterReplicationDisconnectedMessage> disconnectedPort;
	private InputPort<AmiCenterReplicationConnectedMessage> connectedPort;
	private AmiCenterReplicationCenter centersByCenterId[] = new AmiCenterReplicationCenter[128];
	private Map<String, AmiCenterReplicationCenter> centersByName = new HashMap<String, AmiCenterReplicationCenter>();
	private Map<String, AmiCenterReplication> replicationsByName = new HashMap<String, AmiCenterReplication>();

	public AmiCenterReplicator(AmiImdbImpl imdb, AmiCenterClient client) {
		super();
		this.imdb = imdb;
		this.client = client;
		this.replicationObjectsMessageGenerator = imdb.getTools().getGenerator(AmiCenterReplicationObjectsMessage.class);
		this.replicationDisconnectGenerator = imdb.getTools().getGenerator(AmiCenterReplicationDisconnectedMessage.class);
		this.replicationConnectGenerator = imdb.getTools().getGenerator(AmiCenterReplicationConnectedMessage.class);
	}

	public AmiImdbImpl getImdb() {
		return this.imdb;
	}

	public void addCenter(String centerName, String url, String certFile, String password, CalcFrameStack sf) {
		AmiSchema_CENTER rs = imdb.getSystemSchema().__CENTER;
		if (!AmiUtils.isValidVariableName(centerName, false, false))
			throw new RuntimeException("CenterName not valid: " + centerName);
		if (rs.index.getRootMap().getIndex(centerName) != null)
			throw new RuntimeException("CenterName already exists: " + centerName);
		try {
			String port = SH.afterFirst(url, ':', null);
			int portInt = SH.parseInt(port);
		} catch (Exception e) {
			throw new RuntimeException("URL must in format 'host:port'");
		}
		int centerId = 0;
		while (this.client.getClientConnection((byte) centerId) != null)
			centerId++;
		addCenterInner((byte) centerId, centerName, url, certFile, password);
		rs.addRow(null, (byte) centerId, centerName, url, certFile, password, sf);
	}
	public void addReplication(String name, String targetTable, String source, String sourceTable, String mapping, String options, CalcFrameStack sf) {
		AmiSchema_REPLICATION rs = imdb.getSystemSchema().__REPLICATION;
		addReplicationInner(name, targetTable, source, sourceTable, mapping, options);
		rs.addRow(null, name, targetTable, source, sourceTable, mapping, options, sf);

	}

	public AmiCenterReplication getReplicationBySourceTable(byte sourceCenter, String sourceTable) {
		AmiCenterReplicationCenter t = this.centersByCenterId[sourceCenter];
		return t == null ? null : t.getReplication(sourceTable);
	}

	private void addReplicationInner(String name, String targetTable, String source, String sourceTable, String mapping, String options) {
		if (this.replicationsByName.containsKey(name))
			throw new RuntimeException("REPLICATION Name already exists: " + name);
		if (targetTable.startsWith("__"))
			throw new RuntimeException("Can't replicate to system table: " + targetTable);
		if (sourceTable.startsWith("__"))
			throw new RuntimeException("Can't replicate from system table: " + targetTable);
		final AmiCenterClientConnection center = client.getClientConnection(source);
		if (center == null)
			throw new RuntimeException("CENTER not found: " + source);
		AmiCenterReplicationCenter t = this.centersByCenterId[center.getCenterDef().getId()];
		if (t.getReplication(sourceTable) != null)
			throw new RuntimeException("REPLICATION already exists for SourceCenter and SourceTable combination: " + source + "." + sourceTable);
		AmiCenterReplication settings = new AmiCenterReplication(imdb, t, name, targetTable, source, center.getCenterDef().getId(), sourceTable, mapping, options);
		this.replicationsByName.put(name, settings);
		t.putReplication(sourceTable, settings);
		this.client.subscribe(source, CH.s(sourceTable));
	}

	public void addCenterInner(byte centerId, String centerName, String url, String certFile, String password) {
		File file = certFile == null ? null : new File(certFile);
		String host = SH.beforeFirst(url, ':', null);
		String port = SH.afterFirst(url, ':', null);
		int portInt = SH.parseInt(port);
		AmiCenterDefinition center = new AmiCenterDefinition(centerId, centerName, host, portInt, file, null, Password.valueOf(password));
		AmiCenterClientConnection c = client.connect(center, new Listener(centerId));
		AmiCenterReplicationCenter rs = new AmiCenterReplicationCenter(this, c);
		this.centersByCenterId[centerId] = rs;
		this.centersByName.put(centerName, rs);
		c.subscribe(MUST_SUBSCRIBE);
	}

	public void removeCenter(String name, CalcFrameStack sf) {
		AmiCenterClientConnection c = client.getClientConnection(name); //TODO: change to remove
		if (c == null)
			throw new RuntimeException("CENTER not found: " + name);
		AmiCenterReplicationCenter rep = this.centersByCenterId[c.getCenterDef().getId()];
		AmiCenterReplication existing = CH.first(rep.getReplications());
		if (existing != null)
			throw new RuntimeException("CENTER has dependent REPLICATION: " + existing.getName());
		client.closeClientConnection(c);
		AmiSchema_CENTER rs = imdb.getSystemSchema().__CENTER;
		this.centersByCenterId[c.getCenterDef().getId()] = null;
		this.centersByName.remove(c.getCenterDef().getName());
		rs.removeRow(name, sf);
	}

	public void removeReplication(String name, CalcFrameStack sf) {
		AmiCenterReplication existing = this.replicationsByName.remove(name);
		if (existing == null)
			throw new RuntimeException("REPLICATION not found: " + name);
		AmiCenterClientConnection center = client.getClientConnection(existing.getSource());
		AmiCenterReplicationCenter t = this.centersByCenterId[existing.getSourceId()];
		AmiCenterReplication removed = t.removeReplication(existing.getSourceTable());
		center.unsubscribe(CH.s(existing.getSourceTable()));
		existing.clear(this.imdb, sf);
		AmiSchema_REPLICATION rs = imdb.getSystemSchema().__REPLICATION;
		rs.removeRow(name, sf);
	}

	public void resubscribe(String name) {
		AmiCenterReplication existing = this.replicationsByName.get(name);
		if (existing == null)
			throw new RuntimeException("REPLICATION not found: " + name);
		AmiCenterClientConnection center = client.getClientConnection(existing.getSource());
		AmiCenterReplicationCenter t = this.centersByCenterId[existing.getSourceId()];
		center.unsubscribe(CH.s(existing.getSourceTable()));
		center.subscribe(CH.s(existing.getSourceTable()));
	}

	public void onStaruptComplete() {
		AmiSchema_CENTER rs = imdb.getSystemSchema().__CENTER;
		for (int i = 0; i < rs.table.getRowsCount(); i++) {
			AmiRowImpl row = rs.table.getAmiRowAt(i);
			try {
				String centerName = row.get(rs.name.getName(), String.class);
				String url = row.get(rs.url.getName(), String.class);
				String certFile = row.get(rs.certFile.getName(), String.class);
				String encryptedPassword = row.get(rs.password.getName(), String.class);
				String password = encryptedPassword == null ? null : imdb.getState().decrypt(encryptedPassword);
				this.addCenterInner((byte) i, centerName, url, certFile, password);
			} catch (Exception e) {
				throw new RuntimeException("Error starting CENTER table from " + IOH.getFullPath(AmiTableUtils.getSystemPersisterFile(rs.table)) + ": for row " + row, e);
			}
		}
		AmiSchema_REPLICATION rep = imdb.getSystemSchema().__REPLICATION;
		for (int i = 0; i < rep.table.getRowsCount(); i++) {
			AmiRowImpl row = rep.table.getAmiRowAt(i);
			try {
				String replicationName = row.get(rep.replicationName.getName(), String.class);
				String targetTable = row.get(rep.targetTable.getName(), String.class);
				String sourceCenter = row.get(rep.sourceCenter.getName(), String.class);
				String sourceTable = row.get(rep.sourceTable.getName(), String.class);
				String mapping = row.get(rep.mapping.getName(), String.class);
				String options = row.get(rep.options.getName(), String.class);
				this.addReplicationInner(replicationName, targetTable, sourceCenter, sourceTable, mapping, options);
			} catch (Exception e) {
				throw new RuntimeException("Error starting REPLICATION table from " + IOH.getFullPath(AmiTableUtils.getSystemPersisterFile(rep.table)) + ": for row " + row, e);
			}
		}
	}

	public class Listener implements AmiCenterClientListener {

		private AmiCenterClientObjectMessageImpl head;
		private AmiCenterClientObjectMessageImpl tail;
		private AmiCenterClientObjectMessageImpl schemaHead;
		private AmiCenterClientObjectMessageImpl schemaTail;
		final private byte centerId;

		private boolean waitingForSchema = true;

		public Listener(byte centerId) {
			this.centerId = centerId;
		}

		@Override
		public void onCenterMessage(AmiCenterDefinition center, AmiCenterClientObjectMessage m) {
			AmiCenterClientObjectMessageImpl t = (AmiCenterClientObjectMessageImpl) m;
			if (t.getTypeName().startsWith("__")) {//We always want schema changes to get processed first
				if (schemaHead == null)
					schemaHead = schemaTail = t;
				else {
					schemaTail.setNext(t);
					schemaTail = t;
				}
				waitingForSchema = false;
				return;
			}
			if (head == null)
				head = tail = t;
			else {
				tail.setNext(t);
				tail = t;
			}
		}

		@Override
		public void onCenterDisconnect(AmiCenterDefinition center) {
			OH.assertEq(center.getId(), centerId);
			this.head = null;
			this.tail = null;
			this.schemaHead = null;
			this.schemaTail = null;
			AmiCenterReplicationDisconnectedMessage msg = replicationDisconnectGenerator.nw();
			msg.setCenterId(centerId);
			disconnectedPort.dispatch(msg);
			waitingForSchema = true;
		}

		@Override
		public void onCenterConnect(AmiCenterDefinition center) {
			AmiCenterReplicationConnectedMessage msg = replicationConnectGenerator.nw();
			msg.setCenterId(centerId);
			connectedPort.dispatch(msg);
			waitingForSchema = true;
		}

		@Override
		public void onCenterMessageBatchDone(AmiCenterDefinition center) {
			if (waitingForSchema)
				return;
			if (schemaHead == null && head == null)
				return;
			AmiCenterReplicationObjectsMessage msg = replicationObjectsMessageGenerator.nw();
			if (schemaHead != null) {
				msg.setSchemaHead(schemaHead);
				msg.setCenterId(centerId);
				schemaHead = schemaTail = null;
			}
			if (head != null) {
				msg.setHead(head);
				msg.setCenterId(centerId);
				head = tail = null;
			}
			objectsMessagePort.dispatch(msg);
		}

	}

	public void setToReplicationPort(InputPort<AmiCenterReplicationObjectsMessage> replicationPort, InputPort<AmiCenterReplicationDisconnectedMessage> dPort,
			InputPort<AmiCenterReplicationConnectedMessage> cPort) {
		this.objectsMessagePort = replicationPort;
		this.disconnectedPort = dPort;
		this.connectedPort = cPort;
	}
	public AmiCenterReplicationCenter getReplicationSource(byte centerId) {
		return this.centersByCenterId[centerId];
	}
	public AmiCenterReplicationCenter getCenter(String name) {
		return this.centersByName.get(name);
	}
	public Set<String> getReplicationNames() {
		return this.replicationsByName.keySet();
	}

	public void onSchemaChanged(CalcFrameStack sf) {
		for (AmiCenterReplication i : this.replicationsByName.values())
			i.onSchemaChanged(true, sf);

	}

	public Set<String> getCenterNames() {
		return this.centersByName.keySet();
	}

}
