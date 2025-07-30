package com.vortex.web.diff;

import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.base.ValuedSchema;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.agent.VortexAgentDbTable;
import com.vortex.client.VortexClientDbColumn;
import com.vortex.client.VortexClientDbTable;

public class DiffableDbTable extends AbstractDiffableNode {

	public static final byte[] DIFF_PIDS = new byte[] { VortexAgentDbTable.PID_COMMENTS, VortexAgentDbTable.PID_DESCRIPTION };
	private VortexClientDbTable table;

	public DiffableDbTable(VortexClientDbTable table) {
		super(DIFF_TYPE_DB_TABLE, table.getData().getName());
		this.table = table;
		for (VortexClientDbColumn o : table.getColumns())
			addChild(new DiffableDbColumn(o));
	}
	@Override
	public boolean isEqualToNode(DiffableNode node) {
		final DiffableDbTable n = (DiffableDbTable) node;
		final VortexAgentDbTable d1 = table.getData();
		final VortexAgentDbTable d2 = n.table.getData();
		ValuedSchema<Valued> schema = d1.askSchema();
		for (byte pid : DIFF_PIDS)
			if (!schema.askValuedParam(pid).areEqual(d1, d2))
				return false;
		return true;

	}

	@Override
	public String getContents() {
		final VortexAgentDbTable d1 = table.getData();
		ValuedSchema<Valued> schema = d1.askSchema();
		StringBuilder sb = new StringBuilder();
		for (byte pid : DIFF_PIDS) {
			ValuedParam<Valued> p = schema.askValuedParam(pid);
			sb.append(p.getName()).append(": ").append(p.getValue(d1)).append(SH.NEWLINE);
		}
		return sb.toString();
	}

}
