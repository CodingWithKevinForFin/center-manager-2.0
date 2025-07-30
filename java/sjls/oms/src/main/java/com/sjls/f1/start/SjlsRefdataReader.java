package com.sjls.f1.start;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import com.f1.base.Day;
import com.f1.base.IdeableGenerator;
import com.f1.base.DateNanos;
import com.f1.pofo.refdata.Security;
import com.f1.refdata.RefDataManager;
import com.f1.refdata.impl.BasicRefDataManager;
import com.f1.utils.AH;
import com.f1.utils.BasicDay;
import com.f1.utils.CH;
import com.f1.utils.DetailedException;
import com.f1.utils.IOH;
import com.f1.utils.SH;

public class SjlsRefdataReader {
	private static final Logger log = Logger.getLogger(SjlsRefdataReader.class.getName());

	public static RefDataManager read(File sourceMask, DateNanos now, TimeZone timezone, IdeableGenerator g) throws FileNotFoundException {

		File source = resolveFile(sourceMask);

		log.info("Reading sjls Security master from: " + IOH.getFullPath(source));
		BasicDay today = new BasicDay(timezone, now);
		BasicRefDataManager r = new BasicRefDataManager(today.getStartNanoDate(), today.getEndNanoDate());
		int nextSecurityId = 0;
		LineNumberReader lnr = null;
		String line = null;
		int count = 0;

		try {
			InputStream in = new FileInputStream(source);
			if (source.getName().endsWith("gz")) {
				log.info("treating security data as gzip");
				in = new GZIPInputStream(in);
			}
			lnr = new LineNumberReader(new BufferedReader(new InputStreamReader(in), 100000));
			while ((line = lnr.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("#")) {
					log.info("ignoring comment: " + line);
					continue;
				}
				String[] parts = SH.split(',', line);
				if (parts.length != 14) {
					throw new RuntimeException("Expecting 14 entries,not: " + parts.length);
				}
				final String ticker = trim(parts[0]);
				final String sjls_secid = parts[1];
				// final Day effective_date = parseDate(timezone, parts[1]);
				// final Day expire_date = parseDate(timezone, parts[2]);
				// final String sedol = null;// trim(parts[4]);
				final String cusip = trim(parts[2]);
				// final String isin = null;// trim(parts[6]);
				final String ric = parts[3];
				// final String exchange_code = trim(parts[7]);
				// if (!"US".equals(exchange_code)) {
				// log.fine("Skipping security (" + ticker + ") with non-US exchange (" + exchange_code + ") at line: " + lnr.getLineNumber());
				// continue;
				// }
				// if (!OH.isBetween(today, effective_date, expire_date)) {
				// log.fine("Skipping security (" + ticker + ") that inactive (" + effective_date + " - " + expire_date + ") at line: " + lnr.getLineNumber());
				// continue;
				// }
				Security security = g.nw(Security.class);
				security.setSymbol(ticker);
				// security.setSedol(sedol);
				security.setCusip(cusip);
				// security.setIsin(isin);
				security.setRic(ric);
				security.setInstrumentType("EQUITY");
				security.setCurrency("USD");
				security.setLastRefreshDate(today);
				security.setSecurityId(nextSecurityId++);
				security.setAttributes((byte) (Security.IS_COMPOSITE | Security.IS_PRIMARY));
				security.setVendorSymbologies((Map) CH.m("SJLS", sjls_secid));
				if (log.isLoggable(Level.FINE))
					log.fine("Security added: " + security);
				r.addSecurity(security);
				count++;
			}
		} catch (Exception e) {
			throw new DetailedException("Error processing SJLS File", e).set("file", source.getAbsoluteFile())
					.setIfPresent("Line Number", lnr == null ? null : lnr.getLineNumber()).setIfPresent("Line", line);
		} finally {
			IOH.close(lnr);
		}
		log.info("Loaded " + count + " securitie(s) from " + IOH.getFullPath(source));
		return r;
	}

	private static File resolveFile(File sourceMask) throws FileNotFoundException {
		File[] f = IOH.listFiles(sourceMask);
		File file = AH.max(f, SH.COMPARATOR_CASEINSENSITIVE);
		if (!IOH.isFile(file))
			throw new FileNotFoundException(IOH.getFullPath(sourceMask));
		return file;
	}

	private static String trim(String text) {
		if (SH.is(text))
			return text.trim();
		return null;
	}

	private static Day parseDate(TimeZone timezone, String yyyymmdd) {
		try {
			String parts[] = SH.split('-', yyyymmdd);
			return new BasicDay(timezone, Short.parseShort(parts[0]), Byte.parseByte(parts[1]), Byte.parseByte(parts[2]));
		} catch (Exception e) {
			throw new RuntimeException("date is invald (should be yyyy-mm-dd): " + yyyymmdd);
		}
	}
}
