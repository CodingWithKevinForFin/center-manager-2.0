package com.vortex.web.diff;

import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.base.ValuedSchema;
import com.f1.utils.MH;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.agent.VortexAgentDbColumn;
import com.vortex.client.VortexClientDbColumn;

public class DiffableDbColumn extends AbstractDiffableNode {

	public static final byte[] DIFF_PIDS = new byte[] { VortexAgentDbColumn.PID_COMMENTS, VortexAgentDbColumn.PID_DESCRIPTION, VortexAgentDbColumn.PID_MASK,
			VortexAgentDbColumn.PID_NAME, VortexAgentDbColumn.PID_PERMISSIBLE_VALUES, VortexAgentDbColumn.PID_PRECISION, VortexAgentDbColumn.PID_SCALE,
			VortexAgentDbColumn.PID_SIZE, VortexAgentDbColumn.PID_TYPE };
	private VortexClientDbColumn column;

	public DiffableDbColumn(VortexClientDbColumn column) {
		super(DIFF_TYPE_DB_COLUMN, column.getData().getName());
		this.column = column;
	}

	@Override
	public boolean isEqualToNode(DiffableNode node) {
		final DiffableDbColumn n = (DiffableDbColumn) node;
		final VortexAgentDbColumn d1 = column.getData();
		final VortexAgentDbColumn d2 = n.column.getData();
		ValuedSchema<Valued> schema = d1.askSchema();
		for (byte pid : DIFF_PIDS)
			if (!schema.askValuedParam(pid).areEqual(d1, d2))
				return false;
		return true;

	}

	@Override
	public String getContents() {
		final VortexAgentDbColumn d1 = column.getData();
		ValuedSchema<Valued> schema = d1.askSchema();
		StringBuilder sb = new StringBuilder();
		byte mask = d1.getMask();
		switch (d1.getType()) {
			case VortexAgentDbColumn.TYPE_BLOB:
				sb.append("BLOB").append(SH.NEWLINE);
				break;
			case VortexAgentDbColumn.TYPE_BOOLEAN:
				sb.append("BOOLEAN").append(SH.NEWLINE);
				break;
			case VortexAgentDbColumn.TYPE_DATE:
				sb.append("DATE").append(SH.NEWLINE);
				break;
			case VortexAgentDbColumn.TYPE_DATETIME:
				sb.append("DATETIME").append(SH.NEWLINE);
				break;
			case VortexAgentDbColumn.TYPE_ENUM:
				sb.append("ENUM").append(SH.NEWLINE);
				break;
			case VortexAgentDbColumn.TYPE_FIXEDPOINT:
				sb.append("FIXEDPOINT").append(SH.NEWLINE);
				break;
			case VortexAgentDbColumn.TYPE_FLOAT:
				sb.append("FLOAT").append(SH.NEWLINE);
				break;
			case VortexAgentDbColumn.TYPE_INT:
				sb.append("INT").append(SH.NEWLINE);
				break;
			case VortexAgentDbColumn.TYPE_OTHER:
				sb.append("OTHER").append(SH.NEWLINE);
				break;
			case VortexAgentDbColumn.TYPE_SET:
				sb.append("SET").append(SH.NEWLINE);
				break;
			case VortexAgentDbColumn.TYPE_TIME:
				sb.append("TIME").append(SH.NEWLINE);
				break;
			case VortexAgentDbColumn.TYPE_TIMESTAMP:
				sb.append("TIMESTAMP").append(SH.NEWLINE);
				break;
			case VortexAgentDbColumn.TYPE_VARCHAR:
				sb.append("VARCHAR").append(SH.NEWLINE);
				break;
			case VortexAgentDbColumn.TYPE_YEAR:
				sb.append("YEAR").append(SH.NEWLINE);
				break;
		}
		if (!MH.allBits(mask, VortexAgentDbColumn.MASK_NULLABLE))
			sb.append("NO NULL").append(SH.NEWLINE);
		if (MH.allBits(mask, VortexAgentDbColumn.MASK_UNSIGNED))
			sb.append("UNSIGNED").append(SH.NEWLINE);
		for (byte pid : DIFF_PIDS) {
			switch (pid) {
				case VortexAgentDbColumn.PID_TYPE:
				case VortexAgentDbColumn.PID_MASK:
					continue;
			}
			ValuedParam<Valued> p = schema.askValuedParam(pid);
			Object v = p.getValue(d1);
			if (v != null)
				sb.append(p.getName()).append(": ").append(v).append(SH.NEWLINE);
		}
		return sb.toString();
	}

}
