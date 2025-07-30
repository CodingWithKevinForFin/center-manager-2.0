package com.vortex.web.diff;

import com.f1.vortexcommon.msg.agent.VortexAgentDbObject;
import com.vortex.client.VortexClientDbDatabase;
import com.vortex.client.VortexClientDbObject;
import com.vortex.client.VortexClientDbTable;

public class DiffableDbDatabase extends AbstractDiffableNode {

	private VortexClientDbDatabase database;

	public DiffableDbDatabase(VortexClientDbDatabase database) {
		super(DIFF_TYPE_DB_DATABASE, database.getData().getName());
		this.database = database;
		BasicDiffableNode constraints = addChild(new BasicDiffableNode("Constraints"));
		BasicDiffableNode indexes = addChild(new BasicDiffableNode("Indexes"));
		BasicDiffableNode tables = addChild(new BasicDiffableNode("Tables"));
		BasicDiffableNode triggers = addChild(new BasicDiffableNode("Triggers"));
		BasicDiffableNode procs = addChild(new BasicDiffableNode("Stored Procedures"));
		for (VortexClientDbObject o : database.getObjects()) {
			switch (o.getData().getType()) {
				case VortexAgentDbObject.TRIGGER:
					triggers.addChild(new DiffableDbObject(o));
					break;
				case VortexAgentDbObject.INDEX:
					indexes.addChild(new DiffableDbObject(o));
					break;
				case VortexAgentDbObject.CONSTRAINT:
					constraints.addChild(new DiffableDbObject(o));
					break;
				case VortexAgentDbObject.PROCEDURE:
					procs.addChild(new DiffableDbObject(o));
					break;
				default:
					throw new RuntimeException("unknown type: " + o);
			}
		}
		for (VortexClientDbTable o : database.getTables())
			tables.addChild(new DiffableDbTable(o));
		//for (VortexClientDbPrivilege o : database.getPrivileges())
		//addChild(new DiffableDbPriviledge(o));
	}
	@Override
	public boolean isEqualToNode(DiffableNode node) {
		DiffableDbDatabase n = (DiffableDbDatabase) node;
		return true;
	}

	@Override
	public String getContents() {
		return "";
	}

}
