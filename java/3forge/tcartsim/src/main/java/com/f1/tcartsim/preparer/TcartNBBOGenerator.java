package com.f1.tcartsim.preparer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.logging.Logger;

import com.f1.utils.FastBufferedOutputStream;
import com.f1.utils.FastBufferedReader;
import com.f1.utils.LH;
import com.f1.utils.SH;

public class TcartNBBOGenerator {
	// STANDARD CONFIG:
	// /home/share/temp/realTimeTca/ext/nbbos.ext.txt
	// /home/share/temp/realTimeTca/mini/nbbos.prepared.unsorted.txt
	//.5 
	private static final Logger log = LH.get();
	public static void main(String a[]) throws IOException {
		File inFile = new File(a[0]);
		File outFile = new File(a[1]);
		double inflationRate = SH.parseDouble(a[2]);//5.4 for large files
		main(new TcartSimManager(), inFile, outFile, inflationRate);
	}
	public static void main(TcartSimManager mgr, File nbboInFile, File nbboOutFile, double inflationRate) throws IOException {
		FastBufferedReader reader = new FastBufferedReader(new FileReader(nbboInFile), 100000);
		FastBufferedOutputStream writer = new FastBufferedOutputStream(new FileOutputStream(nbboOutFile), 10000);
		HashMap<String, String> exMapping = new HashMap<String, String>();
		initExMapping(exMapping);
		StringBuilder buf = new StringBuilder();
		StringBuilder sb = new StringBuilder();
		int cnt = 0, read = 0;
		DecimalFormat df = new DecimalFormat("#.00");
		StringBuilder tmp = new StringBuilder();
		for (;;) {
			if (read % 10000000 == 0 && read > 0)
				LH.info(log, "Processed NBBO: ", read + "==> " + cnt + " (" + ((double) cnt / read) + ")");
			tmp.setLength(0);
			if (!reader.readLine(tmp))
				break;
			read++;

			//long size = Math.max(1, MH.round((1 + random.nextGaussian()) * inflationRate, MH.ROUND_HALF_EVEN));
			//long count = MH.round((1 + random.nextGaussian()) * inflationRate, MH.ROUND_HALF_EVEN);
			int needToAdd = needToAdd(read, cnt, inflationRate, mgr);
			if (needToAdd > 0) {
				final String line;
				try {
					line = toPipes(tmp, buf, mgr);
				} catch (Exception e) {
					LH.info(log, "Error processing line ", tmp, ": ", e.getMessage());
					//e.printStackTrace();
					continue;
				}
				SH.clear(sb);
				String[] fields = SH.split('|', line);
				long time = SH.parseLong(fields[0]);
				String symbol = fields[1];
				String currency = mgr.getCurrency();
				String ex = fields[2];
				ex = exMapping.get(ex);
				float bid = SH.parseFloat(fields[3]);
				int bidsize = SH.parseInt(fields[4]);
				float ask = SH.parseFloat(fields[5]);
				int asksize = SH.parseInt(fields[6]);
				for (int i = 0; i < needToAdd; i++) {
					cnt++;
					sb.append(time + mgr.nextInt(3000) - 1500);
					sb.append("|N|");
					sb.append(symbol);
					sb.append("|");
					sb.append(ex);
					sb.append("|");
					sb.append(bid == 0 ? 0 : df.format(mgr.getPrice(currency, bid - mgr.nextInt(3) / 100d)));
					sb.append("|");
					sb.append(bidsize);
					sb.append("|");
					sb.append(ask == 0 ? 0 : df.format(mgr.getPrice(currency, ask + mgr.nextInt(3) / 100d)));
					sb.append("|");
					sb.append(asksize);
					sb.append("|");
					sb.append(currency);
					sb.append("\n");
				}
				writer.write(sb.toString().getBytes());
			}
		}
		writer.flush();
		LH.info(log, "Created ", cnt, " NBBO events");
	}

	static public int needToAdd(int inCount, int outCount, double inflation, TcartSimManager mgr) {
		if (inflation == 1)
			return 1;//special case
		double shouldBeAt = inCount * inflation;
		double needToAdd = shouldBeAt - outCount;
		if (needToAdd <= 0) {
			if (needToAdd < 0)
				needToAdd = -1 / needToAdd;
			else
				needToAdd = .5;
		}

		int r = (int) (needToAdd * (1 + mgr.getRandom().nextGaussian()));
		if (inflation >= 1 && r < 1)
			return 1;
		return r;
	}
	private static void initExMapping(HashMap<String, String> exMapping) {
		exMapping.put("A", "NYSE MKT");
		exMapping.put("B", "NASDAQ BX");
		exMapping.put("C", "NSE");
		exMapping.put("D", "FINRA");
		exMapping.put("I", "ISE");
		exMapping.put("J", "EDGA");
		exMapping.put("K", "EDGX");
		exMapping.put("M", "CHX");
		exMapping.put("N", "NYSE");
		exMapping.put("P", "NYSE ARCA");
		exMapping.put("S", "CTS");
		exMapping.put("T", "NASDAQ");
		exMapping.put("Q", "NASDAQ");
		exMapping.put("W", "CBOE");
		exMapping.put("X", "NASDAQ PSX");
		exMapping.put("Y", "BATS Y");
		exMapping.put("Z", "BATS");
	}
	public static String toPipes(StringBuilder in, StringBuilder out, TcartSimManager mgr) {
		SH.clear(out);
		long time = mgr.parseTimeFromLine(in);

		out.append(time).append('|');
		//sym
		out.append(SH.trim(SH.substring(in, 10, 26))).append('|');
		//ex
		out.append(SH.trim(SH.substring(in, 9, 10))).append('|');
		//bid
		out.append(SH.parseInt(in, 26, 37, 10) / 10000.0).append('|');
		//bid size
		out.append(SH.parseInt(in, 37, 44, 10)).append('|');
		//ask
		out.append(SH.parseInt(in, 44, 55, 10) / 10000.0).append('|');
		//asksize
		out.append(SH.parseInt(in, 44, 55, 10));
		return out.toString();
	}
}
