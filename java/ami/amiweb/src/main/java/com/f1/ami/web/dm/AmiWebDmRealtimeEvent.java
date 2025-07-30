package com.f1.ami.web.dm;

import java.util.HashMap;
import java.util.Map;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.AmiWebObject;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.structs.CompactLongKeyMap;
import com.f1.utils.structs.CompactLongKeyMap.KeyGetter;
import com.f1.utils.structs.table.derived.ToDerivedString;

public class AmiWebDmRealtimeEvent implements ToDerivedString {
	public static final CompactLongKeyMap.KeyGetter<AmiWebDmRealtimeEvent> GETTER = new KeyGetter<AmiWebDmRealtimeEvent>() {
		@Override
		public long getKey(AmiWebDmRealtimeEvent object) {
			return object.data == null ? Long.MIN_VALUE : object.data.getUniqueId();
		}
	};
	public AmiWebDmRealtimeEvent next;
	public AmiWebDmRealtimeEvent prior;
	public static final byte SNAPSHOT = 0;
	public static final byte INSERT = 1;
	public static final byte UPDATE = 2;
	public static final byte DELETE = 3;
	public static final byte ADD_COLUMN = 4;
	public static final byte MODIFY_COLUMN = 5;
	public static final byte DROP_COLUMN = 6;
	public static final byte TRUNCATE = 7;
	public byte status;
	final private AmiWebObject data;
	final private String realtimeId;
	final private String tableName;
	final private Map<String, Object> columnDetails;//ONLY FOR ADD_COLUMN, DROP_COLUMN, MODIFY_COLUMN

	public AmiWebDmRealtimeEvent(String realtimeId, AmiWebObject data, byte status) {
		this.realtimeId = realtimeId;
		this.status = status;
		this.data = data;
		this.tableName = data.getTypeName();
		this.columnDetails = null;
	}
	public AmiWebDmRealtimeEvent(String realtimeId, String tableName, String columnName, Class<?> columnType, byte status) {
		OH.assertNotNull(tableName);
		this.realtimeId = realtimeId;
		this.status = status;
		this.data = null;
		this.tableName = tableName;
		this.columnDetails = CH.m("ColumnName", columnName, "ColumnType", columnType);
	}

	@Override
	public String toDerivedString() {
		return toDerivedString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toDerivedString(StringBuilder sb) {
		switch (status) {
			case AmiWebDmRealtimeEvent.SNAPSHOT:
				sb.append("SNAPSHOT-->");
				break;
			case AmiWebDmRealtimeEvent.INSERT:
				sb.append("INSERT-->");
				break;
			case AmiWebDmRealtimeEvent.UPDATE:
				sb.append("UPDATE-->");
				break;
			case AmiWebDmRealtimeEvent.DELETE:
				sb.append("DELETE-->");
				break;
			case AmiWebDmRealtimeEvent.ADD_COLUMN:
				sb.append("ADD_COLUMN-->");
				break;
			case AmiWebDmRealtimeEvent.DROP_COLUMN:
				sb.append("DROP_COLUMN-->");
				break;
			case AmiWebDmRealtimeEvent.MODIFY_COLUMN:
				sb.append("MODIFY_COLUMN-->");
				break;
			case AmiWebDmRealtimeEvent.TRUNCATE:
				sb.append("TRUNCATE-->");
				break;
		}
		sb.append(this.realtimeId);
		if (data != null) {
			sb.append("id=").append(data.getUniqueId());
			AmiUtils.s(data, sb);
		} else if (columnDetails != null)
			AmiUtils.s(this.columnDetails, sb);
		return sb;
	}

	public String getRealtimeId() {
		return this.realtimeId;
	}

	@Override
	public String toString() {
		return toDerivedString();
	}
	public String getTypeName() {
		return this.realtimeId;
	}
	public void fill(HashMap<String, Object> m) {
		if (data != null)
			this.data.fill(m);
		else
			m.putAll(this.columnDetails);
	}
	public Object getValue(String key) {
		if (data != null)
			return this.data.get(key);
		else
			return this.columnDetails.get(key);

	}
	public AmiWebObject getData() {
		return this.data;
	}

}