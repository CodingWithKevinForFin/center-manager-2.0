package com.f1.ami.relay.fh;

import java.util.HashMap;
import java.util.Map;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.relay.AmiRelayHelper;
import com.f1.utils.ByteArray;
import com.f1.utils.ByteHelper;

public class AmiRelayBytesToMapConverter {

	public static Map<String, Object> read(byte[] buf) {
		int pos = 0;
		int fieldsCount = ByteHelper.readShort(buf, pos);
		String[] keys = new String[fieldsCount];
		pos += 2;
		for (int n = 0; n < fieldsCount; n++) {
			int len = ByteHelper.readByte(buf, pos);
			pos++;
			keys[n] = new String(buf, pos, len);
			pos += len;
		}
		HashMap<String, Object> r = new HashMap<String, Object>();
		for (int n = 0; n < fieldsCount; n++) {
			int len = AmiUtils.getDataLength(buf, pos);
			Object val = AmiRelayHelper.getValueAt(buf, pos);
			r.put(keys[n], val);
			pos += len;
		}
		return r;
	}

	public static void main(String a[]) {
		AmiRelayMapToBytesConverter c = new AmiRelayMapToBytesConverter();
		c.appendInt("test", 123);
		c.appendDouble("test2", 123);
		c.appendString("test3", "what");
		c.appendNull("test4");
		Map<String, Object> m = read(c.toBytes());
		System.out.println(m);
	}

	private AmiRelayMapToBytesConverter converter = new AmiRelayMapToBytesConverter();
	private int start[] = new int[128];
	private int length[] = new int[128];
	private ByteArray sb = new ByteArray();

	public byte[] mapKeys(byte[] buf, Map<CharSequence, String[]> src2tgt, boolean passthroughFields) {
		int pos = 0;
		converter.clear();
		int fieldsCount = ByteHelper.readShort(buf, pos);
		pos += 2;
		if (fieldsCount > start.length) {
			start = new int[fieldsCount];
			length = new int[fieldsCount];
		}
		for (int n = 0; n < fieldsCount; n++)//skip keys
			pos = pos + 1 + ByteHelper.readByte(buf, pos);

		for (int n = 0; n < fieldsCount; n++) {
			int len = AmiUtils.getDataLength(buf, pos);
			start[n] = pos;
			length[n] = len;
			pos += len;
		}
		pos = 2;
		for (int n = 0; n < fieldsCount; n++) {
			int len = ByteHelper.readByte(buf, pos);
			pos++;
			sb.reset(buf, pos, pos + len);
			String[] tgts = src2tgt.get(sb);
			if (tgts != null) {
				for (String tgt : tgts)
					converter.appendRaw(tgt, buf, start[n], length[n]);
			} else if (passthroughFields) {
				converter.appendRaw(sb, buf, start[n], length[n]);
			}
			pos += len;
		}
		return converter.toBytes();
	}

}
