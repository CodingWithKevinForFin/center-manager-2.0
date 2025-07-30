package com.f1.ami.amicommon.centerclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiEntityByteUtils;
import com.f1.ami.amicommon.msg.AmiCenterChanges;
import com.f1.ami.amicommon.msg.AmiDataEntity;
import com.f1.utils.AH;
import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.LH;

public class AmiCenterClientStateHelper {

	private static Logger log = LH.get();

	private static void processUpdates(byte actionType, AmiCenterClientState state, FastByteArrayDataInputStream in, List<AmiCenterClientObjectMessage> sink) throws IOException {
		while (in.available() > 0) {
			int size = in.readInt();
			short type = in.readShort();
			if (!state.isInterested(type)) {
				in.skip(size - 2);//we already type
				continue;
			}
			String typeName = state.getAmiKeyStringFromPool(type);
			AmiCenterClientObjectPool pool = state.getPool(typeName);
			AmiCenterClientObjectMessageImpl tmp = state.createObjectMessage();
			final long id = in.readLong();
			final byte mask = in.readByte();
			tmp.reset(state.getStringPoolArray(), state.getCenterDef(), actionType, type, typeName, id, mask);
			if ((mask & (AmiDataEntity.MASK_CREATED_ON | AmiDataEntity.MASK_MODIFIED_ON | AmiDataEntity.MASK_REVISION | AmiDataEntity.MASK_EXPIRES_IN_MILLIS
					| AmiDataEntity.MASK_APPLICATION_ID | AmiDataEntity.MASK_OBJECT_ID)) != 0) {
				if ((mask & AmiDataEntity.MASK_CREATED_ON) != 0)
					tmp.setCreatedOn(in.readLong());
				if ((mask & AmiDataEntity.MASK_MODIFIED_ON) != 0)
					tmp.setModifiedOn(in.readLong());
				if ((mask & AmiDataEntity.MASK_REVISION) != 0)
					tmp.setRevision(in.readInt());
				if ((mask & AmiDataEntity.MASK_EXPIRES_IN_MILLIS) != 0)
					tmp.setExpiresInMillis(in.readLong());
				if ((mask & AmiDataEntity.MASK_APPLICATION_ID) != 0)
					tmp.setAmiApplicationId(state.getAmiKeyStringFromPool(in.readShort()));
				if ((mask & AmiDataEntity.MASK_OBJECT_ID) != 0)
					tmp.setObjectId(in.readUTF());
			}
			if ((mask & AmiDataEntity.MASK_PARAMS) != 0) {
				int fieldsCount = in.readShort();
				tmp.resetParamsCount(fieldsCount);
				for (int i = 0; i < fieldsCount; i++) {
					short field = in.readShort();
					Object value = AmiEntityByteUtils.read(in, state.getValuesStringPoolMap());
					tmp.setParamValue(i, field, pool.poolObject(field, value));
				}
			} else
				tmp.resetParamsCount(0);
			sink.add(tmp);
		}
	}
	private static void processAmiStringPool(AmiCenterClientState state, FastByteArrayDataInputStream in) throws IOException {
		while (in.available() > 0) {
			boolean isValue = in.readBoolean();
			if (isValue) {
				int k = in.readInt();
				String v = in.readUTF();
				state.addAmiValuesStringPoolMappings(k, v);
			} else {
				short k = in.readShort();
				String v = in.readUTF();
				state.addAmiKeyStringPoolMapping(k, v);
			}
		}
	}
	private static void processDeletes(AmiCenterClientState state, FastByteArrayDataInputStream in, List<AmiCenterClientObjectMessage> sink) throws IOException {
		Set<String> interestedTypes = state.getInterestedTypes();
		while (in.available() > 0) {
			short type = in.readShort();
			long id = in.readLong();
			if (state.isInterested(type)) {
				String typeName = state.getAmiKeyStringFromPool(type);
				AmiCenterClientObjectMessageImpl tmp = state.createObjectMessage();
				tmp.reset(state.getStringPoolArray(), state.getCenterDef(), AmiCenterClientObjectMessage.ACTION_DEL, type, typeName, id, (byte) 0);
				tmp.resetParamsCount(0);
				sink.add(tmp);
			}
		}
	}

	static public List<AmiCenterClientObjectMessage> processAmiCenterChanges(AmiCenterClientState state, AmiCenterChanges action, FastByteArrayDataInputStream buf) {
		List<AmiCenterClientObjectMessage> sink = null;
		try {
			if (AH.isntEmpty(action.getAmiValuesStringPoolMap())) {
				buf.reset(action.getAmiValuesStringPoolMap());
				AmiCenterClientStateHelper.processAmiStringPool(state, buf);
			}
			if (AH.isntEmpty(action.getAmiEntitiesAdded())) {
				buf.reset(action.getAmiEntitiesAdded());
				if (sink == null)
					sink = new ArrayList<AmiCenterClientObjectMessage>();
				AmiCenterClientStateHelper.processUpdates(AmiCenterClientObjectMessage.ACTION_ADD, state, buf, sink);
			}
			if (AH.isntEmpty(action.getAmiEntitiesUpdated())) {
				buf.reset(action.getAmiEntitiesUpdated());
				if (sink == null)
					sink = new ArrayList<AmiCenterClientObjectMessage>();
				AmiCenterClientStateHelper.processUpdates(AmiCenterClientObjectMessage.ACTION_UPD, state, buf, sink);
			}
			if (AH.isntEmpty(action.getAmiEntitiesRemoved())) {
				buf.reset(action.getAmiEntitiesRemoved());
				if (sink == null)
					sink = new ArrayList<AmiCenterClientObjectMessage>();
				AmiCenterClientStateHelper.processDeletes(state, buf, sink);
			}
		} catch (IOException e) {
			LH.warning(log, "Error processing changes: ", action, e);
		}
		return sink == null ? Collections.EMPTY_LIST : sink;
	}
}
