package com.vortex.web.messages;

import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.utils.structs.LongSet;
import com.vortex.web.portlet.tables.VortexWebTablePortlet;

public class VortexDbInterPortletMessage implements InterPortletMessage {

	final public static byte TYPE_DBSERVERID = 1;
	final public static byte TYPE_DATABASEID = 2;
	final public static byte TYPE_TABLEID = 3;

	final private LongSet ids;
	final private byte idType;

	public VortexDbInterPortletMessage(byte idType, LongSet ids) {
		this.ids = ids;
		this.idType = idType;
	}

	public LongSet getIds() {
		return ids;
	}

	public byte getIdType() {
		return idType;
	}

	public String getColumnIdForType() {
		switch (idType) {
			case TYPE_DBSERVERID:
				return VortexWebTablePortlet.DSID;
			case TYPE_DATABASEID:
				return VortexWebTablePortlet.DBID;
			case TYPE_TABLEID:
				return VortexWebTablePortlet.TBID;
			default:
				throw new RuntimeException("unknown type: " + idType);
		}
	}
}
