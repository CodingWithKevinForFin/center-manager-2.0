package com.vortex.web.diff;

import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.base.ValuedSchema;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.agent.VortexAgentDbObject;
import com.vortex.client.VortexClientDbObject;

public class DiffableDbObject extends AbstractDiffableNode {

	public static final byte[] DIFF_PIDS = new byte[] { VortexAgentDbObject.PID_NAME, VortexAgentDbObject.PID_DEFINITION, VortexAgentDbObject.PID_TYPE };
	private VortexClientDbObject object;

	public DiffableDbObject(VortexClientDbObject object) {
		super(DIFF_TYPE_DB_OBJECT, object.getData().getName() + " " + object.getData().getType());
		this.object = object;
	}

	@Override
	public boolean isEqualToNode(DiffableNode node) {
		final DiffableDbObject n = (DiffableDbObject) node;
		final VortexAgentDbObject d1 = object.getData();
		final VortexAgentDbObject d2 = n.object.getData();
		ValuedSchema<Valued> schema = d1.askSchema();
		for (byte pid : DIFF_PIDS)
			if (!schema.askValuedParam(pid).areEqual(d1, d2))
				return false;
		return true;

	}

	@Override
	public String getContents() {
		final VortexAgentDbObject d1 = object.getData();
		ValuedSchema<Valued> schema = d1.askSchema();
		StringBuilder sb = new StringBuilder();
		for (byte pid : DIFF_PIDS) {
			ValuedParam<Valued> p = schema.askValuedParam(pid);
			sb.append(p.getName()).append(": ").append(p.getValue(d1)).append(SH.NEWLINE);
		}
		return sb.toString();
	}

}
