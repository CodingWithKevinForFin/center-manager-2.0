package com.f1.ami.center.table;

import java.util.List;

import com.f1.base.Column;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.MH;
import com.f1.utils.structs.table.columnar.ColumnarRow;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiRowImpl extends ColumnarRow implements AmiRow {

	//using bits for faster masking, but only one bit should be set at at time
	public static final byte STATUS_NO_CHANGE = 1;
	public static final byte STATUS_NOBROADCAST = 2;
	public static final byte STATUS_NOBROADCAST_NEEDS_DELETE = 4;
	public static final byte STATUS_NEEDS_INSERT = 8;
	public static final byte STATUS_NEEDS_UPDATE = 16;
	public static final byte STATUS_NEVERBROADCAST = 32;

	final private AmiTableImpl amiTable;
	final private long amiId;
	private byte status;
	private long changedColumnsMask0;//columns 0-63
	private long changedColumnsMask64;//extendedColumns

	protected AmiRowImpl(AmiTableImpl table, int uid, int arrayIndex, long amiId, boolean canFireOnChanges) {
		super(table.getTable(), uid, arrayIndex);
		this.status = canFireOnChanges ? STATUS_NOBROADCAST : STATUS_NEVERBROADCAST;
		this.amiTable = table;
		this.amiId = amiId;
	}

	@Override
	public boolean setNull(String col, CalcFrameStack fs) {
		return getColumn(col).setNull(this, fs);
	}
	@Override
	public boolean setLong(AmiColumn col, long value, CalcFrameStack fs) {
		return col.setLong(this, value, fs);
	}
	@Override
	public boolean setDouble(AmiColumn col, double value, CalcFrameStack fs) {
		return col.setDouble(this, value, fs);
	}
	@Override
	public boolean setString(AmiColumn col, String value, CalcFrameStack fs) {
		return col.setString(this, value, fs);
	}
	@Override
	public boolean setNull(AmiColumn col, CalcFrameStack fs) {
		return col.setNull(this, fs);
	}
	@Override
	public Object putAt(int key, Object value) {
		throw new UnsupportedOperationException();
	}
	@Override
	public Object putAt(int key, Object value, CalcFrameStack fs) {
		AmiColumnImpl<?> col = getColumn(key);
		return col.setComparable(this, value, fs);
	}
	@Override
	public Object put(String key, Object value) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean setNull(int colpos, CalcFrameStack fs) {
		return getColumn(colpos).setNull(this, fs);
	}

	@Override
	public long getLong(AmiColumn col) {
		return col.getLong(this);
	}
	@Override
	public double getDouble(AmiColumn col) {
		return col.getDouble(this);
	}
	@Override
	public String getString(AmiColumn col) {
		return col.getString(this);
	}
	@Override
	public boolean getIsNull(AmiColumn col) {
		return col.getIsNull(this);
	}

	@Override
	public int getRowNum() {
		return getLocation();
	}

	@Override
	public AmiTableImpl getAmiTable() {
		return amiTable;
	}

	private AmiColumnImpl<?> getColumn(String col) {
		return amiTable.getColumn(col);
	}

	private AmiColumnImpl<?> getColumn(int col) {
		return amiTable.getColumnAt(col);
	}

	@Override
	public boolean getIsNull(int colpos) {
		return getIsNull(getColumn(colpos));
	}

	@Override
	public boolean getIsNull(String col) {
		return getColumn(col).getIsNull(this);
	}

	public void writeEntity(FastByteArrayDataOutputStream buf) {
		amiTable.writeEntity(buf, this);
	}

	@Override
	public long getAmiId() {
		return this.amiId;
	}

	@Override
	public String getString(int colpos) {
		return getString(getColumn(colpos));
	}

	@Override
	public String getString(String col) {
		return getString(getColumn(col));
	}

	@Override
	public boolean setString(int colpos, String value, CalcFrameStack fs) {
		return setString(getColumn(colpos), value, fs);
	}

	@Override
	public boolean setString(String col, String value, CalcFrameStack fs) {
		return setString(getColumn(col), value, fs);
	}

	@Override
	public long getLong(int colpos) {
		return getLong(getColumn(colpos));
	}

	@Override
	public long getLong(String col) {
		return getLong(getColumn(col));
	}

	@Override
	public boolean setLong(int colpos, long value, CalcFrameStack fs) {
		return setLong(getColumn(colpos), value, fs);
	}

	@Override
	public boolean setLong(String col, long value, CalcFrameStack fs) {
		return setLong(getColumn(col), value, fs);
	}

	@Override
	public double getDouble(int colpos) {
		return getDouble(getColumn(colpos));
	}

	@Override
	public double getDouble(String col) {
		return getDouble(getColumn(col));
	}

	@Override
	public boolean setDouble(int colpos, double value, CalcFrameStack fs) {
		return setDouble(getColumn(colpos), value, fs);
	}

	@Override
	public boolean setDouble(String col, double value, CalcFrameStack fs) {
		return setDouble(getColumn(col), value, fs);
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		if (this.getArrayIndex() == -1)
			return sink.append("[not participating in table]");
		sink.append("[");
		List<Column> cols = getTable().getColumns();
		for (int i = 0; i < cols.size(); i++) {
			if (i > 0)
				sink.append(", ");
			AmiColumnImpl<?> col = amiTable.getColumnAt(i);
			sink.append(col.getName()).append("=");
			if (col.getAmiType() == AmiTable.TYPE_ENUM)
				sink.append(col.getString(this));
			else
				col.getColumn().toString(this, sink);
		}
		return sink.append("]");
	}

	public void applyValueChangeMask(long mask0, long mask64) {
		if (MH.anyBits(status, STATUS_NO_CHANGE | STATUS_NEEDS_UPDATE | STATUS_NOBROADCAST_NEEDS_DELETE | STATUS_NEEDS_INSERT)) {
			this.changedColumnsMask0 |= mask0;
			this.changedColumnsMask64 |= mask64;
			if (status == STATUS_NO_CHANGE)
				setStatus(STATUS_NEEDS_UPDATE);
		}
	}
	private void setStatus(byte status) {
		amiTable.onRowBroadcastStatusChange(this, this.status, this.status = status);
	}

	public void setBroadcasting(boolean broadcasting) {
		if (broadcasting) {
			if (MH.anyBits(status, STATUS_NOBROADCAST | STATUS_NOBROADCAST_NEEDS_DELETE))
				setStatus(status == STATUS_NOBROADCAST ? STATUS_NEEDS_INSERT
						: (this.changedColumnsMask0 == 0 && this.changedColumnsMask64 == 0 ? STATUS_NO_CHANGE : STATUS_NEEDS_UPDATE));
		} else {
			if (MH.anyBits(status, STATUS_NEEDS_INSERT | STATUS_NEEDS_UPDATE | STATUS_NO_CHANGE))
				setStatus(status == STATUS_NEEDS_INSERT ? STATUS_NOBROADCAST : STATUS_NOBROADCAST_NEEDS_DELETE);
		}

		/*   STATE TRANSITIONS:             BROADCAST                   NOBROADCAST
		 *   -----------------------------------------------------------------------------
		 *   NEEDS_INSERT                   NEEDS_INSERT                NO_BROADCAST
		 *   NEEDS_UPDATE                   NEEDS_UPDATE                NO_BROADCAST_NEEDS_DELETE
		 *   NO_CHANGE                      NO_CHANGE                   NO_BROADCAST_NEEDS_DELETE
		 *   NO_BROADCAST                   NEEDS_INSERT                NO_BROADCAST
		 *   NO_BROADCAST_NEEDS_DELETE      NEEDS_UPDATE/NO_CHANGE**    NO_BROADCAST_NEEDS_DELETE
		 *   
		 *   ** if the changed column mask is empty, then no change
		 */
	}
	public long getChangedColumnsMask0() {
		return changedColumnsMask0;
	}
	public long getChangedColumnsMask64() {
		return changedColumnsMask64;
	}

	public void flushPendingBroadcast() {
		changedColumnsMask0 = 0;
		changedColumnsMask64 = 0;
		if (MH.anyBits(status, STATUS_NEEDS_INSERT | STATUS_NEEDS_UPDATE)) {
			setStatus(STATUS_NO_CHANGE);
		} else if (status == STATUS_NOBROADCAST_NEEDS_DELETE)
			setStatus(STATUS_NOBROADCAST);
	}

	@Override
	public void setVisible(boolean b) {
		setBroadcasting(b);
	}
	@Override
	public boolean getVisible() {
		return !MH.anyBits(status, STATUS_NOBROADCAST | STATUS_NOBROADCAST_NEEDS_DELETE);
	}

	@Override
	public Comparable getComparable(AmiColumn amiColumn) {
		return amiColumn.getComparable(this);
	}
	@Override
	public boolean setComparable(AmiColumn amiColumn, Comparable value, CalcFrameStack fs) {
		return amiColumn.setComparable(this, value, fs);
	}

	@Override
	public boolean setComparable(int col, Comparable value, CalcFrameStack fs) {
		return setComparable(getColumn(col), value, fs);
	}

	@Override
	public boolean setComparable(String col, Comparable value, CalcFrameStack fs) {
		return setComparable(getColumn(col), value, fs);
	}

	@Override
	public Comparable getComparable(int col) {
		return getComparable(getColumn(col));
	}

	@Override
	public Comparable getComparable(String col) {
		return getComparable(getColumn(col));
	}

	@Override
	public boolean isSet(int colpos) {
		// TODO Auto-generated method stub
		return true;
	}

}
