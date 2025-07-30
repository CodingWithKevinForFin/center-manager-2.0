package com.vortex.web.portlet.tables;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.suite.web.table.impl.BasicWebColumn;
import com.f1.utils.CH;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.vortex.client.VortexClientEntity;
import com.vortex.client.VortexClientManager;
import com.vortex.client.VortexClientMetadataField;

public class VortexWebMetadataColumnsManager {

	final private Set<String> metadataColumns = new HashSet<String>();
	final private FastWebTable table;
	final private byte agentEntityType;
	final private String metadataColumnId;

	public VortexWebMetadataColumnsManager(FastWebTable table, byte agentEntityType, String metadataColumId) {
		this.agentEntityType = agentEntityType;
		this.metadataColumnId = metadataColumId;
		this.table = table;
	}
	public void onMachineEntityAdded(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_METADATA_FIELD) {
			VortexClientMetadataField metadata = (VortexClientMetadataField) node;
			if (metadata.appliesTo(agentEntityType)) {
				WebColumn column = metadata.toWebColumn(metadataColumnId);
				if (table.getColumnNoThrow(column.getColumnId()) != null)
					table.updateColumn(column);
				else
					table.addHiddenColumn(column);
				metadataColumns.add(column.getColumnId());
			}
		}
	}
	public void onMachineEntityUpdated(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_METADATA_FIELD) {
			VortexClientMetadataField metadata = (VortexClientMetadataField) node;
			if (metadataColumns.contains(metadata.getColumnId())) {
				if (metadata.appliesTo(agentEntityType)) {
					table.updateColumn(metadata.toWebColumn(metadataColumnId));
				} else {
					table.removeColumn(metadata.getColumnId());
					metadataColumns.remove(metadata.getColumnId());
				}
			} else if (metadata.appliesTo(agentEntityType)) {
				table.addHiddenColumn(metadata.toWebColumn(metadataColumnId));
				metadataColumns.add(metadata.getColumnId());
			}
		}
	}
	public void onMachineEntityRemoved(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_METADATA_FIELD) {
			VortexClientMetadataField metadata = (VortexClientMetadataField) node;
			if (metadata.appliesTo(agentEntityType)) {
				BasicWebColumn col = (BasicWebColumn) table.getColumn(metadata.getColumnId());
				table.removeColumn(metadata.getColumnId());
				metadataColumns.remove(metadata.getColumnId());
			}
		}
	}

	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		Map<String, String> mColumns = CH.getOr(Map.class, configuration, "metadataColumns", null);
		if (mColumns != null) {
			for (String addId : CH.comm(mColumns.keySet(), metadataColumns, true, false, false)) {
				table.addHiddenColumn(new BasicWebColumn(addId, mColumns.get(addId), new BasicWebCellFormatter(), new Object[] { metadataColumnId }));
				metadataColumns.add(addId);
			}
		}
	}

	public Map<String, Object> getConfiguration(Map<String, Object> r) {
		final Map<String, String> cols = new HashMap<String, String>();
		for (String s : this.metadataColumns)
			cols.put(s, table.getColumn(s).getColumnName());
		r.put("metadataColumns", cols);
		return r;
	}

	public void onEyeSnapshotProcessed(VortexClientManager am) {
		for (String s : CH.l(metadataColumns)) {
			VortexClientMetadataField field = am.getMetadataFieldByKeyCode(s.substring(VortexClientMetadataField.COLUMNID_PREFIX.length()));
			if (field == null || !field.appliesTo(agentEntityType)) {
				metadataColumns.remove(s);
				table.removeColumn(s);
			}
		}
	}
}
