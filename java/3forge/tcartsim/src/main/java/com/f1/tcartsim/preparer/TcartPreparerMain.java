package com.f1.tcartsim.preparer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import com.f1.bootstrap.Bootstrap;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.concurrent.SimpleExecutor;
import com.f1.utils.structs.table.BasicTable;

public class TcartPreparerMain {

	private static final Logger log = LH.get();
	public static final String SRC_MAIN_CONFIG = "./src/main/config";
	private static boolean lockTimeOffset;

	public static void main(String a[]) throws IOException {
		Bootstrap bs = new Bootstrap(TcartSimManager.class, a);
		bs.setConfigDirProperty(SRC_MAIN_CONFIG);
		bs.setLoggingOverrideProperty("info");
		PropertyController p = bs.getProperties();
		bs.startup();
		File manifestOutFile = p.getOptional("manifest.file.out", File.class);

		File tradeInFile = p.getRequired("trade.file.in", File.class);
		File tradeOutFile = p.getOptional("trade.file.out", File.class);

		File nbboInFile = p.getRequired("nbbo.file.in", File.class);
		File nbboOutFile = p.getOptional("nbbo.file.out", File.class);

		File secMasterInFile = p.getRequired("secmaster.file.in", File.class);
		File secMasterOutFile = p.getOptional("secmaster.file.out", File.class);

		File accountInFile = p.getRequired("account.file.in", File.class);
		File orderOutFile = p.getOptional("order.file.out", File.class);
		File fxOutFile = p.getOptional("fx.file.out", File.class);

		double pctAccounts = p.getRequired("accounts.percent", double.class);
		double pctOrders = p.getRequired("order.percent", double.class);
		double pctExecs = p.getRequired("exec.percent", double.class);
		double pctOrdersWithNoChildren = p.getRequired("order.without.child.percent", double.class);
		double tradesInflationRate = p.getRequired("trade.inflation", double.class);
		double nbboInflationRate = p.getRequired("nbbo.inflation", double.class);
		double pctBustsCorrects = p.getRequired("bust.percent", double.class);
		String defaultCurrency = p.getOptional("currency.default", "USD");
		PropertyController pCurRates = p.getSubPropertyController("currency.rates.");
		String date = p.getOptional("sim.date", "20160503 EST5EDT");
		Map<String, Double> currencyRates = new HashMap<String, Double>();
		StringBuilder fxRatesOut = new StringBuilder();
		for (String i : pCurRates.getKeys()) {
			Double rate = pCurRates.getRequired(i, Double.class);
			currencyRates.put(i, rate);
			fxRatesOut.append("0|X|" + defaultCurrency + "|" + i + "|" + rate).append(SH.NEWLINE);
		}
		int seed = p.getOptional("random.seed", 123);
		TcartSimManager t = new TcartSimManager(new Random(seed), new Random(seed), defaultCurrency, currencyRates, date);

		IOH.ensureDir(manifestOutFile.getParentFile());
		IOH.ensureDir(tradeOutFile.getParentFile());
		IOH.ensureDir(nbboOutFile.getParentFile());
		IOH.ensureDir(secMasterOutFile.getParentFile());
		IOH.ensureDir(orderOutFile.getParentFile());
		IOH.ensureDir(fxOutFile.getParentFile());

		IOH.ensureReadable(tradeInFile);
		IOH.ensureReadable(nbboInFile);
		IOH.ensureReadable(secMasterInFile);
		IOH.ensureReadable(accountInFile);
		IOH.writeText(manifestOutFile, p.toProperiesManifest());
		IOH.writeText(fxOutFile, fxRatesOut.toString());

		LH.info(log, "%  Orders=" + pctOrders + ", % Execs=" + pctExecs + ", % OrdersWithoutChild=" + pctOrdersWithNoChildren + ", tradeInflateRate=" + tradesInflationRate
				+ ", nbboInflateRate=" + nbboInflationRate);
		LH.info(log, "Trade Input: " + tradeInFile);
		LH.info(log, "Nbbos Input: " + nbboInFile);
		LH.info(log, "Secms Input: " + secMasterInFile);
		LH.info(log, "Accnt Input: " + accountInFile);

		if (secMasterOutFile != null) {
			LH.info(log, "WORKING ON SEC MASTER...");
			TcartSecMasterGenerator.main(t, secMasterInFile, tradeInFile, secMasterOutFile);
		}
		if (orderOutFile != null) {
			LH.info(log, "WORKING ON ORDERS/EXECS...");
			TcartOrderExecutionGenerator.main(t, tradeInFile, accountInFile, orderOutFile, pctOrders, pctExecs, pctBustsCorrects, pctOrdersWithNoChildren, pctAccounts);
		}
		if (tradeOutFile != null) {
			LH.info(log, "WORKING ON TRADES...");
			TcartTradeGenerator.main(t, tradeInFile, tradeOutFile, tradesInflationRate);
		}
		if (nbboOutFile != null) {
			LH.info(log, "WORKING ON NBBOS...");
			TcartNBBOGenerator.main(t, nbboInFile, nbboOutFile, nbboInflationRate);
		}
		sort(orderOutFile);
		sort(nbboOutFile);
		sort(tradeOutFile);

		LH.info(log, "\n", toTable("input", tradeInFile, nbboInFile, secMasterInFile, accountInFile));
		LH.info(log, "\n", toTable("Output", tradeOutFile, nbboOutFile, secMasterOutFile, orderOutFile));

	}

	private static BasicTable toTable(String title, File... files) {
		BasicTable bt = new BasicTable(new String[] { "File", "Size", "Lines" });
		for (File file : files)
			if (file != null)
				bt.getRows().addRow(file.toString(), SH.comma(file.length()), lineCount(file));
		bt.setTitle(title);
		return bt;

	}
	private static String lineCount(File file) {
		return SH.comma(SH.parseInt(SH.beforeFirst(EH.exec(SimpleExecutor.DEFAULT_DEAMON, new String[] { "wc", "-l", file.toString() }).getStdoutText(), " ")));
	}
	private static void sort(File file) {
		LH.info(log, "Sorting ", file + " (", SH.comma(file.length()), " bytes) - ");
		File tmp = new File(file.toString() + ".tmp");
		EH.exec(SimpleExecutor.DEFAULT_DEAMON, new String[] { "sort", file.toString(), "-o", tmp.toString() });
		LH.info(log, "Sorted ", file + " (", SH.comma(file.length()), " bytes)");
		tmp.renameTo(file);
	}

}
