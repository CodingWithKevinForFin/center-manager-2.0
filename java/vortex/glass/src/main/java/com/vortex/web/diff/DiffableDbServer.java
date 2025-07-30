package com.vortex.web.diff;

import com.f1.utils.SH;
import com.vortex.client.VortexClientDbDatabase;
import com.vortex.client.VortexClientDbServer;

public class DiffableDbServer extends AbstractDiffableNode {

	private VortexClientDbServer server;

	public DiffableDbServer(VortexClientDbServer server) {
		super(DIFF_TYPE_DB_SERVER, server.getData().getUrl());
		this.server = server;
		for (VortexClientDbDatabase o : server.getDatabases())
			addChild(new DiffableDbDatabase(o));
	}
	@Override
	public boolean isEqualToNode(DiffableNode node) {
		DiffableDbServer n = (DiffableDbServer) node;
		return n.server.getData().getDbType() == server.getData().getDbType();
	}

	@Override
	public String getContents() {
		return "Type: " + server.getDbTypeString() + "Running on: " + SH.NEWLINE + server.getHostName();
	}

}
