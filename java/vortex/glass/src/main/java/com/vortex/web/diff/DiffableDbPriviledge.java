package com.vortex.web.diff;

import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.base.ValuedSchema;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.agent.VortexAgentDbPrivilege;
import com.vortex.client.VortexClientDbPrivilege;

public class DiffableDbPriviledge extends AbstractDiffableNode {

	public static final byte[] DIFF_PIDS = new byte[] { VortexAgentDbPrivilege.PID_USER, VortexAgentDbPrivilege.PID_TABLE_NAME, VortexAgentDbPrivilege.PID_TYPE, VortexAgentDbPrivilege.PID_DESCRIPTION };
	private VortexClientDbPrivilege privilege;

	public DiffableDbPriviledge(VortexClientDbPrivilege privilege) {
		super(DIFF_TYPE_DB_PRIVILEDGE, privilege.getData().getUser() + " " + privilege.getType() + " " + privilege.getData().getTableName());
		this.privilege = privilege;
	}

	@Override
	public boolean isEqualToNode(DiffableNode node) {
		final DiffableDbPriviledge n = (DiffableDbPriviledge) node;
		final VortexAgentDbPrivilege d1 = privilege.getData();
		final VortexAgentDbPrivilege d2 = n.privilege.getData();
		ValuedSchema<Valued> schema = d1.askSchema();
		for (byte pid : DIFF_PIDS)
			if (!schema.askValuedParam(pid).areEqual(d1, d2))
				return false;
		return true;

	}

	@Override
	public String getContents() {
		final VortexAgentDbPrivilege d1 = privilege.getData();
		ValuedSchema<Valued> schema = d1.askSchema();
		StringBuilder sb = new StringBuilder();
		for (byte pid : DIFF_PIDS) {
			ValuedParam<Valued> p = schema.askValuedParam(pid);
			sb.append(p.getName()).append(": ").append(p.getValue(d1)).append(SH.NEWLINE);
		}
		return sb.toString();
	}

}
