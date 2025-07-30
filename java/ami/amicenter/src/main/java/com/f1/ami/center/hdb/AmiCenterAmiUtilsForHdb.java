package com.f1.ami.center.hdb;

import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiDataEntity;
import com.f1.base.Bytes;
import com.f1.base.Column;
import com.f1.base.Complex;
import com.f1.base.UUID;
import com.f1.utils.ByteHelper;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.columnar.ColumnarRow;
import com.f1.utils.structs.table.columnar.ColumnarTable;

public class AmiCenterAmiUtilsForHdb {

	private static final Logger log = LH.get();

	//	public static void onObjectOrig(AmiCenterState state, AmiHdbTable table, ArrayList<AmiRelayObjectMessage> sink, AmiCenterApplication eApp, long now, AmiImdbSession session) {
	//		try {
	//			//			FastDataOutput out = table.getRtBuffer();
	//			//			StringBuilder tmpbuf = new StringBuilder();
	//			//			for (int i = 0; i < sink.size(); i++) {
	//			//				byte[] data = sink.get(i).getParams();
	//			//				if (AH.isEmpty(data)) {
	//			//					out.write(data.length);
	//			//					out.write(data);
	//			//				}
	//			//			}
	//			//			if (LH.isFine(log))
	//			//				LH.fine(log, "Added ", sink.size(), " records to '", table.getName(), "' rt buffer");
	//		} catch (Exception e) {
	//			LH.warning(log, "Error writing realtime stream to '" + table.getName(), "' rt buffer", e);
	//		}
	//	}
	//	public static void onObject(AmiCenterState state, AmiHdbTable table, ArrayList<AmiRelayObjectMessage> sink, AmiCenterApplication eApp, long now, AmiImdbSession session) {
	//		ColumnarTable t = table.getTableBuffer();
	//		t.clear();
	//		try {
	//			StringBuilder tmpbuf = new StringBuilder();
	//			for (int i = 0; i < sink.size(); i++)
	//				updateRow(state, sink.get(i).getParams(), tmpbuf, t);
	//			table.addRows(t);
	//			if (LH.isFine(log))
	//				LH.fine(log, "Added ", sink.size(), " records to ", table.getName(), ", total size now: ", table.getRowsCountThreadSafe());
	//		} catch (Exception e) {
	//			LH.warning(log, "Error writing realtime stream to: " + table.getName(), " rows:\n " + t, e);
	//		} finally {
	//			t.clear();
	//		}
	//	}

	public static boolean updateRow(byte[] data, AmiHdbTable table, ColumnarTable sink, StringBuilder tmpbuf) {
		if (data != null) {
			final int keysLength = ByteHelper.readShort(data, 0);
			ColumnarRow row = sink.newEmptyRow();
			for (int i = 0, valPos = (keysLength << 1) + 2, len; i < keysLength; i++, valPos += len - 1) {
				len = AmiUtils.getDataLength(data, valPos);
				final byte type = data[valPos];
				valPos++;
				final short key = ByteHelper.readShort(data, (i << 1) + 2);
				Column col = table.getColumnByAmiKey(key);
				if (col == null)
					continue;
				try {
					switch (type) {
						case AmiDataEntity.PARAM_TYPE_NULL:
							set(row, col, null);
							break;
						case AmiDataEntity.PARAM_TYPE_ASCII: {
							int len2 = ByteHelper.readInt(data, valPos);
							int pos = valPos + 4;
							SH.ensureExtraCapacity(tmpbuf, len2);
							final int last = pos + len2;
							while (pos < last)
								tmpbuf.append((char) ByteHelper.readByte(data, pos++));
							set(row, col, SH.toStringAndClear(tmpbuf));
							break;
						}
						case AmiDataEntity.PARAM_TYPE_ASCII_SMALL: {
							int len2 = ByteHelper.readByte(data, valPos);
							int pos = valPos + 1;
							SH.ensureExtraCapacity(tmpbuf, len2);
							final int last = pos + len2;
							while (pos < last)
								tmpbuf.append((char) ByteHelper.readByte(data, pos++));
							set(row, col, SH.toStringAndClear(tmpbuf));
							break;
						}
						case AmiDataEntity.PARAM_TYPE_STRING: {
							int len2 = ByteHelper.readInt(data, valPos);
							int pos = valPos + 4;
							SH.ensureExtraCapacity(tmpbuf, len2);
							final int last = pos + len2 * 2;
							while (pos < last) {
								tmpbuf.append(ByteHelper.readChar(data, pos));
								pos += 2;
							}
							set(row, col, SH.toStringAndClear(tmpbuf));
							break;
						}
						case AmiDataEntity.PARAM_TYPE_ASCII_ENUM: {
							int len2 = ByteHelper.readByte(data, valPos);
							int pos = valPos;//TODO: should be valPos+1 ?
							SH.ensureExtraCapacity(tmpbuf, len2);
							final int last = pos + len2;
							while (pos < last)
								tmpbuf.append((char) ByteHelper.readByte(data, pos++));
							set(row, col, SH.toStringAndClear(tmpbuf));
							//							}
							break;
						}
						case AmiDataEntity.PARAM_TYPE_BOOLEAN:
							set(row, col, data[valPos] == 1 ? 1 : 0);
							break;
						case AmiDataEntity.PARAM_TYPE_INT1:
							set(row, col, (int) ByteHelper.readByte(data, valPos));
							break;
						case AmiDataEntity.PARAM_TYPE_INT2:
							set(row, col, (int) ByteHelper.readShort(data, valPos));
							break;
						case AmiDataEntity.PARAM_TYPE_INT3:
							set(row, col, (int) ByteHelper.readInt3(data, valPos));
							break;
						case AmiDataEntity.PARAM_TYPE_INT4:
							set(row, col, (int) ByteHelper.readInt(data, valPos));
							break;
						case AmiDataEntity.PARAM_TYPE_CHAR:
							set(row, col, (char) ByteHelper.readChar(data, valPos));
							break;
						case AmiDataEntity.PARAM_TYPE_LONG1:
							set(row, col, ByteHelper.readByte(data, valPos));
							break;
						case AmiDataEntity.PARAM_TYPE_LONG2:
							set(row, col, ByteHelper.readShort(data, valPos));
							break;
						case AmiDataEntity.PARAM_TYPE_LONG3:
							set(row, col, ByteHelper.readInt3(data, valPos));
							break;
						case AmiDataEntity.PARAM_TYPE_LONG4:
							set(row, col, ByteHelper.readInt(data, valPos));
							break;
						case AmiDataEntity.PARAM_TYPE_LONG5:
							set(row, col, ByteHelper.readLong5(data, valPos));
							break;
						case AmiDataEntity.PARAM_TYPE_LONG6:
							set(row, col, ByteHelper.readLong6(data, valPos));
							break;
						case AmiDataEntity.PARAM_TYPE_LONG7:
							set(row, col, ByteHelper.readLong7(data, valPos));
							break;
						case AmiDataEntity.PARAM_TYPE_LONG8:
							set(row, col, ByteHelper.readLong(data, valPos));
							break;
						case AmiDataEntity.PARAM_TYPE_UTC6:
							set(row, col, ByteHelper.readLong6(data, valPos));
							break;
						case AmiDataEntity.PARAM_TYPE_UTCN:
							set(row, col, ByteHelper.readLong(data, valPos));
							break;
						case AmiDataEntity.PARAM_TYPE_DOUBLE:
							set(row, col, ByteHelper.readDouble(data, valPos));
							break;
						case AmiDataEntity.PARAM_TYPE_FLOAT:
							set(row, col, ByteHelper.readFloat(data, valPos));
							break;
						case AmiDataEntity.PARAM_TYPE_COMPLEX: {
							double r = ByteHelper.readDouble(data, valPos);
							double ii = ByteHelper.readDouble(data, valPos + 8);
							Complex value = new Complex(r, ii);
							set(row, col, value);
							break;
						}
						case AmiDataEntity.PARAM_TYPE_UUID: {
							long m = ByteHelper.readLong(data, valPos);
							long l = ByteHelper.readLong(data, valPos + 8);
							UUID value = new UUID(m, l);
							set(row, col, value);
							break;
						}
						case AmiDataEntity.PARAM_TYPE_BINARY: {
							int len2 = ByteHelper.readInt(data, valPos);
							byte data2[] = new byte[len2];
							System.arraycopy(data, valPos + 4, data2, 0, len2);
							Bytes value = new Bytes(data2);
							set(row, col, value);
							break;
						}
					}
				} catch (NumberFormatException e) {
					SH.clear(tmpbuf);
					LH.warning(log, "Error for column ", sink.getTitle(), ".", col.getId(), e.getMessage());
				} catch (Exception e) {
					SH.clear(tmpbuf);
					LH.warning(log, "Error for column ", sink.getTitle(), ".", col.getId(), e);
				}

			}
			sink.getRows().add(row);
		}
		return true;
	}

	private static void set(ColumnarRow row, Column c, Object value) {
		row.putAt(c.getLocation(), c.getTypeCaster().castNoThrow(value));
	}
}
