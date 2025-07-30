package com.f1.ami.center;

import java.io.UTFDataFormatException;
import java.util.logging.Logger;

import com.f1.ami.amicommon.msg.AmiCenterChanges;
import com.f1.ami.amicommon.msg.AmiDataEntity;
import com.f1.ami.center.table.AmiColumnImpl;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.table.AmiTableImpl;
import com.f1.base.ObjectGeneratorForClass;
import com.f1.utils.ByteHelper;
import com.f1.utils.EH;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.OH;

public class AmiCenterChangesMessageBuilder {
	private static final Logger log = Logger.getLogger(AmiCenterChangesMessageBuilder.class.getName());

	private static final int MAX_BUFFER_SIZE = 16000;

	private static final int FULL_CUTOFF_BYTES = 1000 * 1000 * 100;//100 megs

	//ami
	final private FastByteArrayDataOutputStream amiEntityUpdatesOut;
	final private FastByteArrayDataOutputStream amiEntityAddsOut;
	final private FastByteArrayDataOutputStream amiEntityRemoves = new FastByteArrayDataOutputStream();
	final private FastByteArrayDataOutputStream stringPool = new FastByteArrayDataOutputStream();

	private ObjectGeneratorForClass<AmiCenterChanges> changesMsgGenerator;

	public AmiCenterChangesMessageBuilder(AmiCenterState state) {
		this.changesMsgGenerator = state.getTools().getGenerator(AmiCenterChanges.class);
		amiEntityUpdatesOut = new FastByteArrayDataOutputStream();

		amiEntityAddsOut = new FastByteArrayDataOutputStream();
	}

	public void reset() {
		amiEntityUpdatesOut.reset(MAX_BUFFER_SIZE);
		if (this.currentRowUpdate != null)
			throw new RuntimeException("Can not reset w/o flush");
		amiEntityAddsOut.reset(MAX_BUFFER_SIZE);
		amiEntityRemoves.reset(MAX_BUFFER_SIZE);
		stringPool.reset(MAX_BUFFER_SIZE);
	}
	public boolean hasChanges() {
		return (amiEntityAddsOut.getCount() > 0 || amiEntityUpdatesOut.getCount() > 0 || amiEntityRemoves.size() > 0 || stringPool.size() > 0);
	}

	//ami popping
	private byte[] popAmiEntityChanges() {
		closeCurrentRowUpdate();
		if (amiEntityUpdatesOut.size() == 0)
			return null;
		final byte[] r = amiEntityUpdatesOut.toByteArray();
		amiEntityUpdatesOut.reset();
		return r;
	}

	private byte[] popAmiEntityAdds() {
		if (amiEntityAddsOut.size() == 0)
			return null;
		final byte[] r = amiEntityAddsOut.toByteArray();
		amiEntityAddsOut.reset();
		return r;
	}

	private byte[] popAmiEntityRemoves() {
		if (amiEntityRemoves.size() == 0)
			return null;
		byte[] r = amiEntityRemoves.toByteArray();
		amiEntityRemoves.reset();
		return r;
	}

	// convenience method
	public AmiCenterChanges popToChangesMsg(long seqNum) {
		final AmiCenterChanges r = this.changesMsgGenerator.nw();

		r.setAmiValuesStringPoolMap(popStringPool());
		r.setAmiEntitiesAdded(popAmiEntityAdds());
		r.setAmiEntitiesRemoved(popAmiEntityRemoves());
		r.setAmiEntitiesUpdated(popAmiEntityChanges());

		r.setEyeProcessUid(EH.getProcessUid());
		r.setSeqNum(seqNum);

		reset();

		return r;
	}

	private byte[] popStringPool() {
		if (stringPool.size() == 0)
			return null;
		final byte[] r = stringPool.toByteArray();
		stringPool.reset();
		return r;
	}

	public void writeRemoveAmiEntity(short type, long id) {
		amiEntityRemoves.writeShort(type);
		amiEntityRemoves.writeLong(id);
	}
	public void writeAdd(AmiRowImpl nuw) {
		nuw.writeEntity(this.amiEntityAddsOut);
	}
	public int getTotalMessageSize() {
		return this.amiEntityAddsOut.getCount() + this.amiEntityUpdatesOut.getCount() + this.amiEntityRemoves.getCount();
	}

	public boolean gettingFull() {
		return this.getTotalMessageSize() > FULL_CUTOFF_BYTES;
	}

	private AmiRowImpl currentRowUpdate = null;
	private int currentRowStartPos, currentRowFieldsCountPos, currentRowFieldsCount;

	private void startCurrentRowUpdate(AmiRowImpl row) {
		this.currentRowUpdate = row;
		this.currentRowStartPos = amiEntityUpdatesOut.size();
		amiEntityUpdatesOut.writeInt(0);//placeholder for total size
		AmiTableImpl table = row.getAmiTable();
		amiEntityUpdatesOut.writeShort(table.getType());
		amiEntityUpdatesOut.writeLong(row.getAmiId());
		int maskPos = amiEntityUpdatesOut.size();
		amiEntityUpdatesOut.writeByte(0);//placeholder for fields mask
		byte mask = AmiDataEntity.MASK_PARAMS;
		if (table.hasReservedColumns()) {
			if (table.getReservedColumnModifiedOn() != null) {
				mask |= AmiDataEntity.MASK_MODIFIED_ON;
				amiEntityUpdatesOut.writeLong(table.getReservedColumnModifiedOn().getLong(row));
			}
			if (table.getReservedColumnRevision() != null) {
				mask |= AmiDataEntity.MASK_REVISION;
				amiEntityUpdatesOut.writeInt((int) table.getReservedColumnRevision().getLong(row));
			}
			if (table.getReservedColumnExpires() != null) {
				mask |= AmiDataEntity.MASK_EXPIRES_IN_MILLIS;
				long val = table.getReservedColumnExpires().getLong(row);
				if (val != AmiTable.NULL_NUMBER) {
					amiEntityUpdatesOut.writeLong(val);
				} else {
					amiEntityUpdatesOut.writeLong(0);
				}
			}
		}
		ByteHelper.writeByte(mask, amiEntityUpdatesOut.getBuffer(), maskPos);
		this.currentRowFieldsCountPos = amiEntityUpdatesOut.size();
		amiEntityUpdatesOut.writeShort(0);//place holder for fields count
		this.currentRowFieldsCount = 0;
	}
	private void closeCurrentRowUpdate() {
		if (this.currentRowUpdate == null)
			return;
		ByteHelper.writeShort(this.currentRowFieldsCount, amiEntityUpdatesOut.getBuffer(), this.currentRowFieldsCountPos);
		ByteHelper.writeInt(amiEntityUpdatesOut.size() - this.currentRowStartPos - 4, amiEntityUpdatesOut.getBuffer(), this.currentRowStartPos);
		this.currentRowStartPos = -1;
		this.currentRowFieldsCountPos = -1;
		this.currentRowUpdate = null;
	}
	private void writeCurrentRowUpdateField(AmiRowImpl row, AmiColumnImpl amiColumnImpl) {
		if (amiColumnImpl.isReserved())
			return;
		amiEntityUpdatesOut.writeShort(amiColumnImpl.getParamKey());
		this.currentRowFieldsCount++;
		AmiCenterAmiUtilsForTable.writeField(amiEntityUpdatesOut, amiColumnImpl, row);
	}
	public void writeUpdateRevision(AmiRowImpl row, AmiColumnImpl amiColumnImpl) {
		if (currentRowUpdate != row) {
			closeCurrentRowUpdate();
			if (row != null) {
				startCurrentRowUpdate(row);
				writeCurrentRowUpdateField(row, amiColumnImpl);
			}
		} else if (row != null)
			writeCurrentRowUpdateField(row, amiColumnImpl);
	}

	public void onStringPoolValueEntry(int r, String s) {
		writeStringPoolValueEntry(r, s, this.stringPool);
	}

	static public void writeStringPoolValueEntry(int r, String s, FastByteArrayDataOutputStream out) {
		out.writeBoolean(true);
		out.writeInt(r);
		try {
			out.writeUTF(s);
		} catch (UTFDataFormatException e) {
			throw OH.toRuntime(e);
		}
	}

	public void onStringPoolKeyEntry(short r, String text) {
		writeStringPoolKeyEntry(r, text, this.stringPool);
	}

	public static void writeStringPoolKeyEntry(short r, String text, FastByteArrayDataOutputStream out) {
		OH.assertNotNull(text);
		out.writeBoolean(false);
		out.writeShort(r);
		try {
			out.writeUTF(text);
		} catch (UTFDataFormatException e) {
			throw OH.toRuntime(e);
		}
	}

}
