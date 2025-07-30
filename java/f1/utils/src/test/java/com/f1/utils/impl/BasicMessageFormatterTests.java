package com.f1.utils.impl;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

import com.f1.utils.AH;
import com.f1.utils.BasicLocaleFormatter;
import com.f1.utils.BundledTextFormatter;

public class BasicMessageFormatterTests {
	@Test
	public void test1() {
		BasicLocaleFormatter f = new BasicLocaleFormatter(Locale.getDefault(), TimeZone.getDefault(), false, AH.a(new File("test/messageformat.properties")), new HashMap());
		BundledTextFormatter formatter = f.getBundledTextFormatter();
		System.out.println(formatter.formatBundledText("rob"));
		System.out.println(formatter.formatBundledText("welcome.message", "robert", "cooke"));
		System.out.println("time=" + formatter.formatBundledText("time0", new Date()));
		System.out.println("time=" + formatter.formatBundledText("time1", new Date()));
		System.out.println("time=" + formatter.formatBundledText("time2", new Date()));
		System.out.println("price=" + formatter.formatBundledText("price0", 100.05));
		System.out.println("price=" + formatter.formatBundledText("price1", 100.05));
		System.out.println("price=" + formatter.formatBundledText("price2", 100.05));
		System.out.println("percent=" + formatter.formatBundledText("percent0", .055));
		System.out.println("percent=" + formatter.formatBundledText("percent1", BigDecimal.valueOf(.055)));
		System.out.println("percent=" + formatter.formatBundledText("percent2", .05));
		System.out.println("simple=" + formatter.formatBundledText("simple"));
	}
}

