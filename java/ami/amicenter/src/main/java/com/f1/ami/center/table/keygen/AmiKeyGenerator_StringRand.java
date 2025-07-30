package com.f1.ami.center.table.keygen;

import java.security.SecureRandom;
import java.util.List;

import com.f1.ami.center.table.AmiColumn;
import com.f1.ami.center.table.AmiColumnImpl;
import com.f1.ami.center.table.AmiIndexImpl;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiKeyGenerator_StringRand implements AmiKeyGenerator {

	private AmiColumn col;
	private static SecureRandom RAND = MH.RANDOM_SECURE;
	private StringBuilder buf = new StringBuilder(10);
	private AmiIndexImpl index;

	public AmiKeyGenerator_StringRand(AmiColumnImpl col, AmiIndexImpl amiIndexImpl) {
		OH.assertEq(col.getAmiType(), AmiTable.TYPE_STRING);
		this.index = amiIndexImpl;
		this.col = col;
	}

	@Override
	public boolean hasValue(AmiRow val) {
		return !val.getIsNull(col);
	}

	@Override
	public void onValue(AmiRow val) {
	}

	@Override
	public void getNextValue(AmiRow sink) {
		String v = nextRand(buf);
		while (this.index.getRootMap().getIndex(v) != null)
			v = nextRand(buf);

		sink.setString(col, v, null);
	}

	static private String nextRand(StringBuilder buf) {
		long n = Math.abs(RAND.nextLong());
		SH.toString(123L, 62);
		n = writeChar(buf, n);
		n = writeChar(buf, n);
		n = writeChar(buf, n);
		n = writeChar(buf, n);
		n = writeChar(buf, n);
		n = writeChar(buf, n);
		n = writeChar(buf, n);
		n = writeChar(buf, n);
		n = writeChar(buf, n);
		n = writeChar(buf, n);
		return SH.toStringAndClear(buf);
	}

	static private long writeChar(StringBuilder sink, long i) {
		sink.append(SH.getDigitToChar((int) (i % 62L)));
		return i / 62L;
	}

	@Override
	public void onValues(List<AmiRow> val) {
	}

	public static void main(String a[]) {
		StringBuilder sb = new StringBuilder();
		int n[] = new int[256];
		for (int i = 0; i < 1000000; i++) {
			String s = nextRand(sb);
			for (int j = 0; j < s.length(); j++)
				n[s.charAt(j)]++;
		}
		System.out.println(SH.join(",", n));
	}
}
