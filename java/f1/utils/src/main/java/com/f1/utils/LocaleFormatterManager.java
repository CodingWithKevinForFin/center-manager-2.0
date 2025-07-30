package com.f1.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import com.f1.utils.structs.Tuple2;

public class LocaleFormatterManager {

	private static final Logger log = Logger.getLogger(LocaleFormatterManager.class.getName());

	private File[] bundleTextFiles = null;
	private ConcurrentMap<Tuple2<Locale, TimeZone>, LocaleFormatter> cache = new CopyOnWriteHashMap<Tuple2<Locale, TimeZone>, LocaleFormatter>();
	private boolean messageFilesLocked = false;
	private Map<String, Formatter> customFormatters = new HashMap<String, Formatter>();
	private int bundleTextOptions;

	public LocaleFormatter getThreadSafeLocaleFormatter(Locale locale, TimeZone timeZone) {
		lockBundleTextFiles();
		messageFilesLocked = true;
		final Tuple2<Locale, TimeZone> key = new Tuple2<Locale, TimeZone>(locale, timeZone);
		LocaleFormatter r = cache.get(key);
		if (r != null)
			return r;
		r = new BasicLocaleFormatter(locale, timeZone, true, bundleTextFiles, customFormatters, bundleTextOptions);
		return OH.noNull(cache.putIfAbsent(key, r), r);
	}

	private void lockBundleTextFiles() {
		if (messageFilesLocked)
			return;
		if (bundleTextFiles == null)
			bundleTextFiles = OH.EMPTY_FILE_ARRAY;
		for (int i = 0; i < bundleTextFiles.length; i++)
			bundleTextFiles[i] = bundleTextFiles[i].getAbsoluteFile();
		LH.info(log,"Bundle Text Files: " , SH.join(',', bundleTextFiles));
		messageFilesLocked = true;
	}

	public LocaleFormatter createLocaleFormatter(Locale locale, TimeZone timeZone) {
		lockBundleTextFiles();
		return new BasicLocaleFormatter(locale, timeZone, false, bundleTextFiles, customFormatters, bundleTextOptions);
	}

	public void setBundleTextOptions(int options) {
		assertBundleTextFilesNotLocked();
		this.bundleTextOptions = options;
	}
	public void setBundleTextFiles(File[] bundleTextFiles) {
		assertBundleTextFilesNotLocked();
		this.bundleTextFiles = bundleTextFiles.clone();
	}

	private void assertBundleTextFilesNotLocked() {
		if (messageFilesLocked)
			throw new DetailedException("bundleTextFiles can no longer be changed because someone has already requested a locale formatter");
	}

	public void addCustomFormatter(String key, Formatter formatter) {
		CH.putOrThrow(customFormatters, key, formatter);
	}

	public void addMessageFile(File messageFile) {
		assertBundleTextFilesNotLocked();
		if (bundleTextFiles == null)
			bundleTextFiles = new File[] { messageFile };
		else
			bundleTextFiles = AH.insert(bundleTextFiles, bundleTextFiles.length, messageFile);
	}

}

