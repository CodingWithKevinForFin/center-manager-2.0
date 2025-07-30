package com.f1.ami.amicommon.centerclient;

import java.util.Map;

import com.f1.ami.amicommon.msg.AmiDataEntity;

public interface AmiCenterClientObjectMessage {
	public byte MASK_CREATED_ON = AmiDataEntity.MASK_CREATED_ON;
	public byte MASK_MODIFIED_ON = AmiDataEntity.MASK_MODIFIED_ON;
	public byte MASK_REVISION = AmiDataEntity.MASK_REVISION;
	public byte MASK_EXPIRES_IN_MILLIS = AmiDataEntity.MASK_EXPIRES_IN_MILLIS;
	public byte MASK_APPLICATION_ID = AmiDataEntity.MASK_APPLICATION_ID;
	public byte MASK_OBJECT_ID = AmiDataEntity.MASK_OBJECT_ID;
	public byte MASK_PARAMS = AmiDataEntity.MASK_PARAMS;

	public static final byte ACTION_ADD = 1;
	public static final byte ACTION_UPD = 2;
	public static final byte ACTION_DEL = 3;

	public String getAmiApplicationId();
	public String getObjectId();
	public short getType();
	public String getTypeName();
	public Long getExpiresInMillis();
	public String getParamName(int pos);
	public Object getParamValue(int pos);
	public Object findParamValue(String string);
	public Integer getRevision();
	public Long getModifiedOn();
	public Long getCreatedOn();
	public Long getId();
	public int getParamsCount();
	public byte getMask();
	public byte getAction();
	public long getUid();
	public String getCenterName();
	public String describe();
	public String toString();
	public void toParamsMap(Map<String, Object> sink);

}
