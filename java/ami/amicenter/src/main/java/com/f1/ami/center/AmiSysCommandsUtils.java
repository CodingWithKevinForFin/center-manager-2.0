package com.f1.ami.center;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiEncrypter;
import com.f1.ami.center.table.AmiColumn;
import com.f1.ami.center.table.AmiColumnImpl;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.base.Bytes;
import com.f1.base.UUID;
import com.f1.utils.AH;
import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.SafeFile;
import com.f1.utils.encrypt.EncoderUtils;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiSysCommandsUtils {

	private static final Logger log = LH.get();

	static public void writeObjectsToDisk(AmiCenterState state, AmiTable table, SafeFile sf, CalcFrameStack frameStack, AmiEncrypter encrypter) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cols = table.getColumnsCount();
		for (int x = 0; x < cols; x++)
			sb.append(table.getColunNameAt(x)).append('|');
		sb.append(SH.NEWLINE);
		for (int y = 0; y < table.getRowsCount(); y++) {
			AmiRow row = table.getAmiRowAt(y);
			for (int x = 0; x < cols; x++) {
				if (row.getIsNull(x))
					sb.append("|");
				else {
					AmiColumn columnAt = table.getColumnAt(x);
					if (columnAt.getIsOnDisk()) {
						long pointer = ((AmiColumnImpl<?>) columnAt).getOnDiskLong(row);
						sb.append(pointer).append("L|");
					} else {
						switch (columnAt.getAmiType()) {
							case AmiTable.TYPE_DOUBLE:
								sb.append(row.getDouble(x)).append("D|");
								break;
							case AmiTable.TYPE_FLOAT:
								sb.append(row.getDouble(x)).append("|");
								break;
							case AmiTable.TYPE_INT:
							case AmiTable.TYPE_SHORT:
							case AmiTable.TYPE_BYTE:
								sb.append(row.getLong(x)).append("|");
								break;
							case AmiTable.TYPE_LONG:
							case AmiTable.TYPE_UTC:
							case AmiTable.TYPE_UTCN:
								sb.append(row.getLong(x)).append("L|");
								break;
							case AmiTable.TYPE_BOOLEAN:
								sb.append(row.getString(x)).append('|');
								break;
							case AmiTable.TYPE_CHAR:
								char c = (char) row.getLong(x);
								if (c == '\'')
									sb.append("'\\''|");
								else
									sb.append('\'').append(c).append("\'|");
								break;
							case AmiTable.TYPE_ENUM:
							case AmiTable.TYPE_STRING:
								sb.append('"');
								SH.escape(row.getString(x), '"', '\\', sb);
								sb.append("\"|");
								break;
							case AmiTable.TYPE_BINARY:
								sb.append('"');
								EncoderUtils.encode64UrlSafe(((Bytes) row.getComparable(x)).getBytes(), sb);
								sb.append("\"|");
								break;
							case AmiTable.TYPE_COMPLEX:
								sb.append(row.getComparable(x)).append("|");
								break;
							case AmiTable.TYPE_BIGDEC:
								sb.append(row.getComparable(x)).append("|");
								break;
							case AmiTable.TYPE_BIGINT:
								sb.append(row.getComparable(x)).append("|");
								break;
							case AmiTable.TYPE_UUID:
								sb.append('"');
								SH.escape(row.getComparable(x).toString(), '"', '\\', sb);
								sb.append("\"|");
								break;
						}
					}
				}
			}
			sb.append(SH.NEWLINE);
		}
		if (encrypter == null)
			sf.setText(sb.toString());
		else
			sf.setData(encryptStream(encrypter, sb.toString().getBytes()));
	}
	public static void readObjectsFromDisk(AmiCenterState state, AmiTable target, SafeFile sf, CalcFrameStack stackframe, AmiEncrypter encrypter) throws IOException {
		String fullPath = IOH.getFullPath(sf.getFile());
		LH.info(log, "Restoring Table '", target.getName(), "' from file: ", fullPath);
		AmiCenterApplication sys = state.getAmiSystemApplication();
		String[] lines;
		if (encrypter == null)
			lines = SH.splitLines(sf.getText());
		else
			lines = SH.splitLines(new String(decryptStream(encrypter, sf.getData())));
		if (AH.isEmpty(lines))
			return;
		String[] cols = SH.split('|', lines[0]);
		StringCharReader scr = new StringCharReader();
		StringBuilder sink = new StringBuilder();
		AmiPreparedRow row = target.createAmiPreparedRow();
		Map<AmiColumnImpl, Long> onDiskPointers = new IdentityHashMap<AmiColumnImpl, Long>();
		for (int i = 1; i < lines.length; i++) {
			row.reset();
			String line = lines[i];
			scr.reset(line);
			long id = state.createNextId();
			int pos = 0;
			while (scr.peakOrEof() != StringCharReader.EOF) {
				String param = cols[pos++];
				if (target.getColumnLocation(param) == -1) {
					scr.readUntil('|', sink);
					scr.expect('|');
					continue;
				}
				switch (scr.peak()) {
					case '|':
						break;
					case '"':
						scr.expect('"');
						scr.readUntilSkipEscaped('"', '\\', SH.clear(sink));
						scr.expect('"');

						if (target.getColumnType(param) == AmiTable.TYPE_BINARY)
							row.setComparable(param, new Bytes(EncoderUtils.decode64(sink)));
						else if (target.getColumnType(param) == AmiTable.TYPE_UUID)
							row.setComparable(param, new UUID(sink.toString()));
						else
							row.setString(param, sink.toString());
						break;
					case '\'':
						scr.expect('\'');
						scr.readUntilSkipEscaped('\'', '\\', SH.clear(sink));
						scr.expect('\'');
						row.setLong(param, sink.charAt(0));
						break;
					case 't':
						scr.expectSequence("true");
						row.setLong(param, 1);
						break;
					case 'f':
						scr.expectSequence("false");
						row.setLong(param, 0);
						break;
					default:
						SH.clear(sink);
						if (target.getColumnLocation(param) != -1 && target.getColumnType(param) == AmiTable.TYPE_COMPLEX) {
							scr.readUntil('|', sink);
							row.setComparable(param, SH.parseComplex(sink));
							break;
						}
						boolean isDec = false, isBig = false;
						while (scr.peak() != '|') {
							char c = scr.readChar();
							switch (c) {
								case 'e':
								case 'E':
								case 'f':
								case 'F':
								case '.':
									isDec = true;
								case '0':
								case '1':
								case '2':
								case '3':
								case '4':
								case '5':
								case '6':
								case '7':
								case '8':
								case '9':
									sink.append((char) c);
									continue;
								case 'D':
								case 'd':
									isDec = true;
									isBig = true;
									break;
								case 'L':
								case 'l':
									isDec = false;
									isBig = true;
									break;
							}
						}
						try {
							AmiColumnImpl column = (AmiColumnImpl) target.getColumn(param);
							if (column != null && column.getIsOnDisk()) {
								column.setOnDiskEmptyValue(row);
								onDiskPointers.put(column, SH.parseLong(sink, 10));
							} else {
								if (target.getColumnType(param) == AmiTable.TYPE_BIGINT) {
									row.setComparable(param, new BigInteger(sink.toString()));
								} else if (target.getColumnType(param) == AmiTable.TYPE_BIGDEC) {
									row.setComparable(param, new BigDecimal(sink.toString()));
								} else if (isDec)
									row.setDouble(param, SH.parseDouble(sink));
								else
									row.setLong(param, SH.parseLong(sink, 10));
							}
						} catch (Exception ex) {
							//TODO:
						}
				}
				scr.expect('|');
			}
			AmiRow row2 = target.insertAmiRow(row, stackframe);
			if (row2 == null) {
				LH.warning(log, "Could not load line: ", fullPath, ":", i, " ==> ", line);
				continue;
			}
			if (!onDiskPointers.isEmpty()) {
				if (row2 != null)
					for (Entry<AmiColumnImpl, Long> en : onDiskPointers.entrySet())
						en.getKey().setOnDiskLong(row2, en.getValue());
				onDiskPointers.clear();
			}
		}
	}
	public static void removeFromDisk(AmiCenterState state, SafeFile sf) throws IOException {
		sf.deleteFile();
	}
	private static byte[] encryptStream(AmiEncrypter encrypter, byte[] bytes) throws IOException {
		FastByteArrayDataInputStream in = new FastByteArrayDataInputStream(bytes);
		FastByteArrayDataOutputStream out = new FastByteArrayDataOutputStream();
		IOH.pipe(in, encrypter.encryptStream(out), true);
		return out.toByteArray();
	}
	private static byte[] decryptStream(AmiEncrypter encrypter, byte[] bytes) throws IOException {
		FastByteArrayDataInputStream in = new FastByteArrayDataInputStream(bytes);
		FastByteArrayDataOutputStream out = new FastByteArrayDataOutputStream();
		IOH.pipe(encrypter.decryptStream(in), out, true);
		return out.toByteArray();
	}
}
