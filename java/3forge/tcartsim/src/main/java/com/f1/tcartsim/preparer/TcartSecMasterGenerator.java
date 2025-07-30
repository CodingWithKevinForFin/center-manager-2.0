package com.f1.tcartsim.preparer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.utils.CH;
import com.f1.utils.FastBufferedOutputStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;

public class TcartSecMasterGenerator {
	private static final Logger log = LH.get();
	private static int davesSecMaster = 0;
	private static int kxData = 0;
	private static int both = 0;

	public static void main(String[] a) throws IOException {
		File secMasterFile = new File(a[0]);
		File tradesFile = new File(a[1]);
		File outFile = new File(a[2]);
		main(new TcartSimManager(), secMasterFile, tradesFile, outFile);
	}
	public static void main(TcartSimManager mgr, File secMasterInFile, File tradesInFile, File secMasterOutFile) throws IOException {
		HashMap<String, Tuple2<Double, Double>> tradesToOpenAndCap = new HashMap<String, Tuple2<Double, Double>>();
		HashMap<String, String[]> secmasterData = new HashMap<String, String[]>();
		try {
			for (String s : SH.splitLines(IOH.readText(secMasterInFile))) {
				String[] parts = SH.split('|', s);
				String symbol = parts[0];
				secmasterData.put(symbol, parts);
			}
		} catch (IOException e1) {
			LH.warning(log, "Error ", e1);
		}
		try {
			LineNumberReader r = new LineNumberReader(new FileReader(tradesInFile), 100000);
			for (int cnt = 0;; cnt++) {
				if (cnt % 1000000 == 0)
					LH.info(log, "Calculating Benchmarks..." + cnt / 200000 + "%");
				StringBuilder sb = new StringBuilder();
				try {
					String line = r.readLine();
					if (line == null)
						break;
					try {
						String sym = SH.trim(line.substring(10, 26));
						int size = SH.parseInt(line, 30, 39, 10);
						int price_w = SH.parseInt(line, 39, 46, 10);
						int price_d = SH.parseInt(line, 46, 50, 10);
						double price = price_w + price_d / 10000.0;
						double value = price * size;
						if (tradesToOpenAndCap.containsKey(sym)) {
							Tuple2<Double, Double> vals = tradesToOpenAndCap.get(sym);
							vals.setB(vals.getB() + value);
						} else {
							Tuple2<Double, Double> vals = new Tuple2<Double, Double>();
							vals.setAB(price, value);
							tradesToOpenAndCap.put(sym, vals);
						}
					} catch (NumberFormatException e) {
						LH.warning(log, "Error with line ", cnt, ": ", e.getMessage());
						continue;
					}

				} catch (IOException e) {
					LH.warning(log, "Error ", e);
				}
			}
		} catch (FileNotFoundException e) {
			LH.warning(log, "Error ", e);
		}
		//System.out.println("Writing file...");
		List<String> secmasterNames = CH.l(secmasterData.keySet());
		List<String> suffixes = CH.l("Company", "LLC.", "Industries", "Corp", "Inc", "& Sons");
		Map<String, Tuple2<String[], Tuple2<Double, Double>>> merged = CH.join(secmasterData, tradesToOpenAndCap);
		DecimalFormat df = new DecimalFormat();
		df.applyLocalizedPattern("######.##");
		FastBufferedOutputStream w = new FastBufferedOutputStream(new FileOutputStream(secMasterOutFile), 10000);
		for (Entry<String, Tuple2<String[], Tuple2<Double, Double>>> e : merged.entrySet()) {
			StringBuilder sb = new StringBuilder();
			String sym = e.getKey();
			String[] secmasterPart = e.getValue().getA();
			Tuple2<Double, Double> tradesPart = e.getValue().getB();
			if (tradesPart == null)
				continue;
			String symName = null;
			if (secmasterPart == null) {
				String name = "";
				while (name.equals("")) {
					name = mgr.getRandom(secmasterNames);
				}
				secmasterPart = secmasterData.get(name);
				symName = SH.trim(sym) + " " + mgr.getRandom(suffixes);
			} else
				symName = secmasterPart[1];
			String currency = mgr.getDefaultCurrency();
			//sb.append(sym + "||||" + df.format(tradesPart.getB()) + "|" + tradesPart.getA() + "\n");
			//kxData++;
			//			} else if (tradesPart == null) {
			//				sb.append(sym + "|" + secmasterPart[1] + "|" + secmasterPart[3] + "|" + secmasterPart[4] + "||\n");
			//				davesSecMaster++;
			//} else {
			sb.append("0|S|" + sym + "|" + symName + "|" + secmasterPart[3] + "|" + secmasterPart[4] + "|" + df.format(tradesPart.getB()) + "|" + tradesPart.getA() + "|"
					+ currency + "|" + "09:30:00 EST5EDT" + "\n");
			both++;
			//}
			try {
				w.write(sb.toString().getBytes());
			} catch (IOException e1) {
				LH.warning(log, "Error ", e1);
			}

		}
		w.close();
		LH.info(log, "MYSQL => " + davesSecMaster);
		LH.info(log, "KX => " + kxData);
		LH.info(log, "BOTH => " + both);
	}
}
