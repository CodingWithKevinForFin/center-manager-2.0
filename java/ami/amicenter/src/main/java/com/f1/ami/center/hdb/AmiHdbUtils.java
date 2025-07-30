package com.f1.ami.center.hdb;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiStartup;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.center.hdb.col.AmiHdbColumnMarshaller;
import com.f1.ami.center.hdb.col.AmiHdbColumnMarshaller_Bitmap1String;
import com.f1.ami.center.hdb.col.AmiHdbColumnMarshaller_Bitmap2String;
import com.f1.ami.center.hdb.col.AmiHdbColumnMarshaller_Flat;
import com.f1.ami.center.hdb.col.AmiHdbColumnMarshaller_FlatBoolean;
import com.f1.ami.center.hdb.col.AmiHdbColumnMarshaller_FlatNoMin;
import com.f1.ami.center.hdb.col.AmiHdbColumnMarshaller_FlatNoNull;
import com.f1.ami.center.hdb.col.AmiHdbColumnMarshaller_VarSize;
import com.f1.ami.center.hdb.col.AmiHdbMarshaller;
import com.f1.ami.center.hdb.col.AmiHdbMarshallerFixedSize;
import com.f1.ami.center.hdb.col.AmiHdbMarshallerVarSize;
import com.f1.ami.center.hdb.col.AmiHdbMarshallers;
import com.f1.ami.center.table.AmiTable;
import com.f1.base.NameSpaceIdentifier;
import com.f1.utils.AH;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;
import com.f1.utils.FastRandomAccessFile;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.OneToOne;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.impl.ArrayHasher;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorRef;

public class AmiHdbUtils {
	public static final String OPTION_MAX_OPTIMIZE_SECONDS = "MaxOptimizeSeconds";
	public static final String OPTION_MIN_OPTIMIZE_PCT = "MinOptimizePct";
	private static final Logger log = LH.get();
	static {
		new AmiStartup();
	}
	public static final int CODE_LENGTH = 15;

	private static byte[] pad(String s) {
		OH.assertLe(s.length(), CODE_LENGTH);
		return SH.leftAlign('_', s, CODE_LENGTH, false).getBytes();
	}

	public static final byte[] HEADER_COLUMN = "3FHCOL.V".getBytes();
	public static final byte[] HEADER_INDEX = "3FHIDX.V".getBytes();
	public static final String HEADER_KEY = "3FHKEY.V";
	public static final String FILE_EXT_HCOL = ".3fhcol";
	public static final String FILE_EXT_HDAT = ".3fhdat";
	public static final String FILE_EXT_HIDX = ".3fhidx";
	public static final String FILE_EXT_HTAB = ".3fhtab";
	public static final String FILE_EXT_HPAR = ".3fhpar";
	public static final String FILE_EXT_HKEY = ".3fhkey";
	public static final String FILE_EXT_HCOL_REBUILD = ".3fhcol_rebuild";
	public static final String FILE_EXT_HDAT_REBUILD = ".3fhdat_rebuild";
	public static final String FILE_KEY = "KEY";
	public static final byte MODE_FLAT = 1;
	public static final byte MODE_FLAT_NONULL = 2;
	public static final byte MODE_FLAT_NOMIN = 3;

	//for Variable Size
	public static final byte MODE_VARSIZE3 = 4;//<=8MB
	public static final byte MODE_VARSIZE4 = 5;//<=2GB
	public static final byte MODE_VARSIZE5 = 6;//>2GB
	public static final byte MODE_BITMAP1 = 7;//<256 UNIQUE VALUES
	public static final byte MODE_BITMAP2 = 8;//65536 UNIQUE VALUES
	public static final byte MODE_PARTITION = 9;

	public static final String STRING_MODE_FLAT = "FLAT";
	public static final String STRING_MODE_FLAT_NONULL = "FLAT_NONULL";
	public static final String STRING_MODE_FLAT_NOMIN = "FLAT_NOMIN";

	//for Variable Size
	public static final String STRING_MODE_VARSIZE3 = "VARSIZE3";//<=8MB
	public static final String STRING_MODE_VARSIZE4 = "VARSIZE4";//<=2GB
	public static final String STRING_MODE_VARSIZE5 = "VARSIZE5";//>2GB
	public static final String STRING_MODE_BITMAP1 = "BITMAP1";//<256 UNIQUE VALUES
	public static final String STRING_MODE_BITMAP2 = "BITMAP2";//65536 UNIQUE VALUES
	public static final String STRING_MODE_PARTITION = "PARTITION";

	public static final OneToOne<Byte, String> MODES = new OneToOne<Byte, String>();

	public static final byte[] OPTIMIZED_TRUE = pad("OPTIMIZED");
	public static final byte[] OPTIMIZED_FALSE = pad("NOT_OPTIMIZED");

	final private static byte[][] TYPES_ID2STR = new byte[256][];
	final private static Map<byte[], Byte> TYPES_STR2ID = new HasherMap<byte[], Byte>(ArrayHasher.BYTE_INSTANCE);
	final private static byte[][] MODES_ID2STR = new byte[256][];
	final private static Map<byte[], Byte> MODES_STR2ID = new HasherMap<byte[], Byte>(ArrayHasher.BYTE_INSTANCE);
	private static final byte[] PADDING = pad("");

	static {
		MODES.put(MODE_FLAT, STRING_MODE_FLAT);
		MODES.put(MODE_FLAT_NONULL, STRING_MODE_FLAT_NONULL);
		MODES.put(MODE_FLAT_NOMIN, STRING_MODE_FLAT_NOMIN);
		MODES.put(MODE_VARSIZE3, STRING_MODE_VARSIZE3);
		MODES.put(MODE_VARSIZE4, STRING_MODE_VARSIZE4);
		MODES.put(MODE_VARSIZE5, STRING_MODE_VARSIZE5);
		MODES.put(MODE_BITMAP1, STRING_MODE_BITMAP1);
		MODES.put(MODE_BITMAP2, STRING_MODE_BITMAP2);
		MODES.put(MODE_PARTITION, STRING_MODE_PARTITION);
		for (Entry<String, Byte> i : AmiUtils.getNamesToTypes()) {
			byte[] key = pad(i.getKey().toUpperCase());
			TYPES_ID2STR[i.getValue()] = key;
			TYPES_STR2ID.put(key, i.getValue());
		}
		for (Entry<Byte, String> i : MODES.getEntries()) {
			byte[] val = pad(i.getValue());
			MODES_ID2STR[i.getKey()] = val;
			MODES_STR2ID.put(val, i.getKey());
		}
	}

	public static void writeType(FastDataOutput out, byte type) throws IOException {
		byte r[] = TYPES_ID2STR[type];
		if (r == null)
			throw new RuntimeException("Invalid type: " + type);
		out.write(r);
	}
	public static byte readType(FastDataInput in) throws IOException {
		byte[] buf = new byte[CODE_LENGTH];
		in.readFully(buf);
		Byte r = TYPES_STR2ID.get(buf);
		if (r == null)
			throw new RuntimeException("Invalid type: " + new String(buf));
		return r.byteValue();
	}

	public static void writeMode(FastDataOutput out, byte mode) throws IOException {
		byte r[] = MODES_ID2STR[mode];
		if (r == null)
			throw new RuntimeException("Invalid mode: " + mode);
		out.write(r);
	}
	public static byte readMode(FastDataInput in) throws IOException {
		byte[] buf = new byte[CODE_LENGTH];
		in.readFully(buf);
		Byte r = MODES_STR2ID.get(buf);
		if (r == null)
			throw new RuntimeException("Invalid mode: " + new String(buf));
		return r.byteValue();
	}

	public static void writeHeader(FastDataOutput out, byte[] header, int version) throws IOException {
		out.write(header);
		if (version < 1 || version > 9)
			throw new RuntimeException("Invalid version: " + version);
		out.writeByte(('0' + version));
		out.write(PADDING, 0, CODE_LENGTH - header.length - 1);
	}
	public static int readHeader(FastDataInput in, byte[] header) throws IOException {
		byte[] buf = new byte[CODE_LENGTH];
		in.readFully(buf);
		if (AH.eq(buf, 0, header, 0, header.length)) {
			int version = buf[header.length] - '0';
			if (version < 1 || version > 9)
				throw new RuntimeException("Invalid version: " + new String(buf));
			return version;
		}
		throw new RuntimeException("Invalid column header: " + new String(buf));
	}
	public static void writeOptimized(FastDataOutput out, boolean op) throws IOException {
		out.write(op ? OPTIMIZED_TRUE : OPTIMIZED_FALSE);
	}
	public static boolean readOptimized(FastDataInput in) throws IOException {
		byte[] buf = new byte[CODE_LENGTH];
		in.readFully(buf);
		if (AH.eq(buf, OPTIMIZED_TRUE))
			return true;
		if (AH.eq(buf, OPTIMIZED_FALSE))
			return false;
		throw new RuntimeException("Invalid optimized status: " + new String(buf));
	}
	public static AmiHdbColumnMarshaller createMarshaller(AmiHdbPartitionColumn col, byte classType, byte mode) throws IOException {
		AmiHdbMarshaller<?> marshaller = null;
		switch (classType) {
			case AmiTable.TYPE_FLOAT:
				return toFlat(col, AmiHdbMarshallers.FLOAT_INSTANCE, mode);
			case AmiTable.TYPE_DOUBLE:
				return toFlat(col, AmiHdbMarshallers.DOUBLE_INSTANCE, mode);
			case AmiTable.TYPE_INT:
				return toFlat(col, AmiHdbMarshallers.INT_INSTANCE, mode);
			case AmiTable.TYPE_LONG:
				return toFlat(col, AmiHdbMarshallers.LONG_INSTANCE, mode);
			case AmiTable.TYPE_BYTE:
				return toFlat(col, AmiHdbMarshallers.BYTE_INSTANCE, mode);
			case AmiTable.TYPE_SHORT:
				return toFlat(col, AmiHdbMarshallers.SHORT_INSTANCE, mode);
			case AmiTable.TYPE_CHAR:
				return toFlat(col, AmiHdbMarshallers.CHAR_INSTANCE, mode);
			case AmiTable.TYPE_UTC:
				return toFlat(col, AmiHdbMarshallers.UTC_INSTANCE, mode);
			case AmiTable.TYPE_UTCN:
				return toFlat(col, AmiHdbMarshallers.UTCN_INSTANCE, mode);
			case AmiTable.TYPE_BOOLEAN:
				if (mode == MODE_FLAT)
					return new AmiHdbColumnMarshaller_FlatBoolean(col);
				else
					throw new RuntimeException("Invalid Mode: " + AmiHdbUtils.toStringForMode(mode) + " for Boolean (only FLAT supported)");
			case AmiTable.TYPE_UUID:
				return toFlat(col, AmiHdbMarshallers.UUID_INSTANCE, mode);
			case AmiTable.TYPE_COMPLEX:
				return toFlat(col, AmiHdbMarshallers.COMPLEX_INSTANCE, mode);
			case AmiTable.TYPE_BINARY:
				return toVarSize(col, AmiHdbMarshallers.BINARY_INSTANCE, mode);
			case AmiTable.TYPE_BIGINT:
				return toVarSize(col, AmiHdbMarshallers.BIGINT_INSTANCE, mode);
			case AmiTable.TYPE_BIGDEC:
				return toVarSize(col, AmiHdbMarshallers.BIGDEC_INSTANCE, mode);
			case AmiTable.TYPE_STRING:
				switch (mode) {
					case AmiHdbUtils.MODE_BITMAP1:
						return new AmiHdbColumnMarshaller_Bitmap1String(col);
					case AmiHdbUtils.MODE_BITMAP2:
						return new AmiHdbColumnMarshaller_Bitmap2String(col);
					default:
						return toVarSize(col, AmiHdbMarshallers.STRING_INSTANCE, mode);
				}
			default:
				throw new RuntimeException("Invalid ClassType: " + classType);
		}
	}
	private static <T extends Comparable> AmiHdbColumnMarshaller toVarSize(AmiHdbPartitionColumn col, AmiHdbMarshallerVarSize<T> m, byte mode) throws IOException {
		switch (mode) {
			case MODE_VARSIZE3:
			case MODE_VARSIZE4:
			case MODE_VARSIZE5:
				return new AmiHdbColumnMarshaller_VarSize<T>(col, m, mode);
			default:
				throw new RuntimeException("Invalid Mode: " + AmiHdbUtils.toStringForMode(mode) + " for " + AmiUtils.toTypeName(m.getType()));
		}
	}
	private static <T extends Comparable> AmiHdbColumnMarshaller toFlat(AmiHdbPartitionColumn col, AmiHdbMarshallerFixedSize<T> m, byte mode) throws IOException {
		switch (mode) {
			case MODE_FLAT:
				return new AmiHdbColumnMarshaller_Flat<T>(col, m);
			case MODE_FLAT_NOMIN:
				return new AmiHdbColumnMarshaller_FlatNoMin<T>(col, m);
			case MODE_FLAT_NONULL:
				return new AmiHdbColumnMarshaller_FlatNoNull<T>(col, m);
			default:
				throw new RuntimeException("Invalid Mode: " + AmiHdbUtils.toStringForMode(mode) + " for " + AmiUtils.toTypeName(m.getType()));
		}
	}
	public static String toStringForMode(byte mode) {
		return MODES.getValue(mode);
	}
	public static byte parseMode(String option) {
		Byte r = MODES.getKey(option);
		return r == null ? -1 : r.byteValue();
	}
	public static void removeRows(FastRandomAccessFile ds, int[] toRemove, int bytesPerRow, int totalRows, long headerSizeInBytes) throws IOException {
		if (toRemove.length == 0)
			return;
		{//asserts

			long expectedSize = totalRows * bytesPerRow + headerSizeInBytes;
			if ((long) expectedSize < ds.length()) {
				if (ds.length() - expectedSize < bytesPerRow) {
					LH.info(log, "Trailing bytes found in ", IOH.getFullPath(ds.getFile()), " reducing from ", ds.length(), " to ", expectedSize);
					ds.setLength(expectedSize);
				}
			}
			OH.assertEq(expectedSize, ds.length());
			OH.assertGe(toRemove[0], 0);
			OH.assertLt(toRemove[toRemove.length - 1], totalRows);
			for (int i = 1; i < toRemove.length; i++)
				OH.assertLt(toRemove[i - 1], toRemove[i]);
		}

		int last = toRemove.length - 1;
		int n = totalRows - 1;
		while (last > 0 && toRemove[last] == n) {
			last--;
			n--;
		}
		int start = toRemove[0];
		ds.seek(headerSizeInBytes + start * bytesPerRow);
		FastDataInput in = ds.getInput();
		int remaining = (int) (totalRows - start - toRemove.length) * bytesPerRow;
		byte[] buf = new byte[remaining];
		int bufPos = 0;
		int prior = start;
		for (int i = 1; i <= last; i++) {
			int remove = toRemove[i];
			IOH.skipBytes(in, bytesPerRow);
			int toKeep = (remove - prior - 1) * bytesPerRow;
			if (toKeep > 0) {
				in.readFully(buf, bufPos, toKeep);
				bufPos += toKeep;
			}
			prior = remove;
		}
		IOH.skipBytes(in, bytesPerRow);
		in.readFully(buf, bufPos, buf.length - bufPos);
		ds.seek(headerSizeInBytes + start * bytesPerRow);
		ds.getOutput().write(buf);
		ds.setLength(headerSizeInBytes + (totalRows - toRemove.length) * bytesPerRow);
	}

	public static byte getDefaultMode(byte type) {
		switch (type) {
			case AmiTable.TYPE_LONG:
			case AmiTable.TYPE_INT:
			case AmiTable.TYPE_SHORT:
			case AmiTable.TYPE_BYTE:
			case AmiTable.TYPE_DOUBLE:
			case AmiTable.TYPE_FLOAT:
			case AmiTable.TYPE_BOOLEAN:
			case AmiTable.TYPE_UTC:
			case AmiTable.TYPE_UTCN:
			case AmiTable.TYPE_CHAR:
			case AmiTable.TYPE_UUID:
			case AmiTable.TYPE_COMPLEX:
				return AmiHdbUtils.MODE_FLAT;
			case AmiTable.TYPE_STRING:
			case AmiTable.TYPE_BINARY:
			case AmiTable.TYPE_BIGINT:
			case AmiTable.TYPE_BIGDEC:
				return AmiHdbUtils.MODE_VARSIZE5;
			case AmiTable.TYPE_ENUM:
			case AmiTable.TYPE_NONE:
			default:
				throw new RuntimeException("Unsupported type: " + type);
		}
	}
	public static File newFile(File parent, String name, String extension) {
		StringBuilder sb = new StringBuilder();
		encodeFileName(name, sb).append(extension);
		return new File(parent, sb.toString());
	}
	private static StringBuilder encodeFileName(String name, StringBuilder sb) {
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == ' ' || c == '-' || c == '.' || c == '~' || c == '+' || c == '_')
				sb.append(c);
			else
				SH.encodeUrl(c, sb);
		}
		return sb;
	}
	public static void main(String a[]) {
	}
	public static boolean isValidTypeMode(byte type, byte mode) {
		switch (type) {
			case AmiTable.TYPE_LONG:
			case AmiTable.TYPE_INT:
			case AmiTable.TYPE_SHORT:
			case AmiTable.TYPE_BYTE:
			case AmiTable.TYPE_DOUBLE:
			case AmiTable.TYPE_FLOAT:
			case AmiTable.TYPE_BOOLEAN:
			case AmiTable.TYPE_UTC:
			case AmiTable.TYPE_UTCN:
			case AmiTable.TYPE_CHAR:
			case AmiTable.TYPE_UUID:
			case AmiTable.TYPE_COMPLEX:
				return mode == MODE_FLAT || mode == MODE_FLAT_NOMIN || mode == MODE_FLAT_NONULL || mode == MODE_PARTITION;
			case AmiTable.TYPE_STRING:
				return mode == MODE_VARSIZE3 || mode == MODE_VARSIZE4 || mode == MODE_VARSIZE5 || mode == MODE_PARTITION || mode == MODE_BITMAP1 || mode == MODE_BITMAP2;
			case AmiTable.TYPE_BINARY:
			case AmiTable.TYPE_BIGINT:
			case AmiTable.TYPE_BIGDEC:
				return mode == MODE_VARSIZE3 || mode == MODE_VARSIZE4 || mode == MODE_VARSIZE5 || mode == MODE_PARTITION;
			case AmiTable.TYPE_ENUM:
			case AmiTable.TYPE_NONE:
			default:
				return false;
		}
	}

	public static String getId(DerivedCellCalculatorRef ref, String tableName) {
		Object id = ref.getId();
		if (id instanceof NameSpaceIdentifier) {
			NameSpaceIdentifier nsi = (NameSpaceIdentifier) id;
			if (OH.ne(nsi.getNamespace(), tableName))
				throw new ExpressionParserException(ref.getPosition(), "Unknown table: " + nsi.getNamespace());
			return nsi.getVarName();
		} else
			return (String) id;
	}
}
