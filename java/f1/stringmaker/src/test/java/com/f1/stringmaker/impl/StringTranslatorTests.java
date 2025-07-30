package com.f1.stringmaker.impl;

import junit.framework.Assert;

import org.junit.Test;

import com.f1.stringmaker.StringTranslator;

public class StringTranslatorTests {

	@Test
	public void test() {
		StringTranslator t = new StringTranslator(".*", "$0$");
		Assert.assertEquals("test", t.translate("test"));
		Assert.assertEquals("where is down", StringTranslator.translate("what is up", "what(.*)up", "where$1$down"));
		Assert.assertEquals(null, StringTranslator.translate("what is nuts", "what(.*)up", "where$1$up"));
		Assert.assertEquals(null, StringTranslator.translate("what is nuts", "what(.*)up", "where$1$up"));
		Assert.assertEquals(null, StringTranslator.translate("what is nuts", "what(.*)up", "where$1$up"));
	}
}
