package com.f1.tcartsim.preparer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.logging.Logger;

import com.f1.utils.FastBufferedOutputStream;
import com.f1.utils.LH;
import com.f1.utils.SH;

public class TcartTradeGenerator {

	private static final Logger log = LH.get();
	public static void main(String a[]) throws IOException {
		File inFile = new File(a[0]);
		File outFile = new File(a[1]);
		int inflationRate = SH.parseInt(a[2]);//5
	}

	public static void main(TcartSimManager mgr, File tradesInFile, File tradesOutFile, double inflationRate) throws IOException {
		LineNumberReader reader = new LineNumberReader(new FileReader(tradesInFile), 100000);
		FastBufferedOutputStream writer = new FastBufferedOutputStream(new FileOutputStream(tradesOutFile), 10000);
		HashMap<String, String> exMapping = new HashMap<String, String>();
		initExMapping(exMapping);
		StringBuilder buf = new StringBuilder();
		int cnt = 0, read = 0;
		DecimalFormat df = new DecimalFormat("#.00");
		StringBuilder sb = new StringBuilder();
		for (;;) {
			//System.out.println(cnt);
			String line = reader.readLine();
			read++;
			if (line == null)
				break;
			int needToAdd = TcartNBBOGenerator.needToAdd(read, cnt, inflationRate, mgr);
			if (needToAdd > 0) {
				sb.setLength(0);
				try {
					line = toPipes(line, buf, mgr);
				} catch (Exception e) {
					LH.info(log, "Error processing line ", line, " ==> ", e.getMessage());
					//System.out.println("Execption at " + cnt);
					continue;
				}
				String[] fields = SH.split("|", line);
				long time = SH.parseLong(fields[0]);
				String symbol = fields[1];
				String currency = mgr.getCurrency();
				String ex = fields[2];
				float px;
				try {
					px = SH.parseFloat(fields[3]);
				} catch (NumberFormatException e) {
					LH.info(log, "Error processing px on line ", line, " ==> ", e.getMessage());
					continue;
				}
				int size = 0;
				try {
					size = SH.parseInt(fields[4]);
				} catch (ArrayIndexOutOfBoundsException e) {
					LH.info(log, "Error processing size on line ", line, " ==> ", e.getMessage());
					continue;
				}
				for (int i = 0; i < needToAdd; i++) {
					cnt++;
					sb.append(time + mgr.nextInt(3001) - 1500);
					sb.append("|T|");
					sb.append(symbol);
					sb.append("|");
					sb.append(ex);
					sb.append("|");
					sb.append(df.format(mgr.getPrice(currency, px - mgr.nextInt(3) / 100d)));
					sb.append("|");
					sb.append(size + 100 * mgr.nextInt(10));
					sb.append("|");
					sb.append(currency);
					sb.append("\n");
					if (cnt % 10000000 == 0)
						LH.info(log, "Generated ", cnt, " trades");
				}
				writer.write(sb.toString().getBytes());
			}
		}
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

	static public String toPipes(String line, StringBuilder sb, TcartSimManager mgr) {
		SH.clear(sb);
		long time = mgr.parseTimeFromLine(line);
		String ex = line.substring(9, 10);
		String sym = SH.trim(line.substring(10, 26));
		int size = SH.parseInt(line, 30, 39, 10);
		int price_w = SH.parseInt(line, 39, 46, 10);
		int price_d = SH.parseInt(line, 46, 50, 10);

		sb.append(time + "|");
		sb.append(sym + "|");
		sb.append(ex + "|");
		sb.append(price_w + price_d / 10000.0 + "|");
		sb.append(size);
		return sb.toString();
	}
}
