package com.f1.ami.amicommon.centerclient;

import java.util.Map;

import com.f1.ami.amicommon.AmiCenterDefinition;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiCenterClientObjectMessageImpl implements AmiCenterClientObjectMessage {

	public static final byte ACTION_ADD = 1;
	public static final byte ACTION_UPD = 2;
	public static final byte ACTION_DEL = 3;
	private byte mask;
	private String amiApplicationId;
	private String objectId;
	private short type;
	private Integer revision;
	private Long expiresInMillis;
	private Long createdOn;
	private Long modifiedOn;
	private Long id;
	private short[] paramCodes = OH.EMPTY_SHORT_ARRAY;
	private Object[] paramValues = OH.EMPTY_OBJECT_ARRAY;
	private int paramsCount;
	private byte action;
	private long uid;
	private String typeName;
	private AmiCenterDefinition source;
	private AmiCenterClientObjectMessageImpl next;
	private String[] stringPool;

	public void reset(String[] stringPool, AmiCenterDefinition source, byte action, short type, String typeName, Long id, byte mask) {
		this.stringPool = stringPool;
		this.source = source;
		this.action = action;
		this.type = type;
		this.typeName = typeName;
		this.id = id;
		this.uid = this.id | ((long) this.source.getId() << 56L);
		this.mask = mask;
		this.next = null;
	}

	public void resetParamsCount(int count) {
		if (count > this.paramsCount) {
			if (count > this.paramCodes.length) {
				this.paramCodes = new short[count];
				this.paramValues = new Object[count];
			} else {
				for (int i = this.paramsCount; i < count; i++) {
					//					this.paramNames[i] = null;
					this.paramValues[i] = null;
				}
			}
		}
		this.paramsCount = count;
	}

	@Override
	public String getAmiApplicationId() {
		return this.amiApplicationId;
	}
	public void setAmiApplicationId(String string) {
		this.amiApplicationId = string;
	}

	@Override
	public String getObjectId() {
		return this.objectId;
	}
	void setObjectId(String id) {
		this.objectId = id;
	}

	@Override
	public short getType() {
		return this.type;
	}
	@Override
	public String getTypeName() {
		return this.typeName;
	}

	@Override
	public Long getExpiresInMillis() {
		return this.expiresInMillis;
	}
	void setExpiresInMillis(Long millis) {
		this.expiresInMillis = millis;
	}

	@Override
	public String getParamName(int pos) {
		return this.stringPool[this.paramCodes[pos]];
	}
	@Override
	public Object getParamValue(int pos) {
		return this.paramValues[pos];
	}
	public void setParamValue(int pos, short code, Object value) {
		this.paramCodes[pos] = code;
		this.paramValues[pos] = value;
	}

	@Override
	public Integer getRevision() {
		return this.revision;
	}
	public void setRevision(Integer revision) {
		this.revision = revision;
	}

	@Override
	public Long getModifiedOn() {
		return this.modifiedOn;
	}
	public void setModifiedOn(Long n) {
		this.modifiedOn = n;
	}

	@Override
	public Long getCreatedOn() {
		return this.createdOn;
	}
	public void setCreatedOn(Long n) {
		this.createdOn = n;
	}

	@Override
	public Long getId() {
		return this.id;
	}
	public int getParamsCount() {
		return paramsCount;
	}

	@Override
	public byte getMask() {
		return this.mask;
	}

	@Override
	public byte getAction() {
		return action;
	}

	@Override
	public long getUid() {
		return this.uid;
	}

	@Override
	public String getCenterName() {
		return this.source.getName();
	}

	@Override
	public String describe() {
		return getActionString() + ":" + this.typeName + "@" + this.id + "(" + paramsCount + " fields)";
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getActionString()).append(':').append(this.id).append(':').append(this.typeName).append('{');
		for (int i = 0; i < paramsCount; i++) {
			if (i > 0)
				sb.append(", ");
			sb.append(getParamName(i)).append('=').append(this.paramValues[i]);
		}
		return sb.append('}').toString();
	}

	private String getActionString() {
		switch (action) {
			case ACTION_ADD:
				return "ADD";
			case ACTION_UPD:
				return "UPD";
			case ACTION_DEL:
				return "DEL";
			default:
				return "ACTION-" + SH.toString(action);
		}
	}

	public byte getCenterId() {
		return this.source.getId();
	}

	public AmiCenterDefinition getSourceCenterDef() {
		return this.source;
	}

	@Override
	public void toParamsMap(Map<String, Object> sink) {
		for (int i = 0; i < this.paramsCount; i++)
			sink.put(getParamName(i), this.paramValues[i]);
	}

	public AmiCenterClientObjectMessageImpl getNext() {
		return next;
	}

	public void setNext(AmiCenterClientObjectMessageImpl next) {
		this.next = next;
	}

	@Override
	public Object findParamValue(String string) {
		for (int i = 0; i < paramsCount; i++)
			if (OH.eq(getParamName(i), string))
				return paramValues[i];
		return null;
	}

	public short getParamCode(int i) {
		return this.paramCodes[i];
	}
}
