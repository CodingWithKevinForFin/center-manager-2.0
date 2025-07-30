package com.f1.utils;

import org.junit.Test;

import com.f1.base.Complex;

import junit.framework.Assert;

public class ComplexTests {

	@Test
	public void parseTest() {
		Assert.assertEquals(new Complex(0, 0), SH.parseComplex("0"));
		Assert.assertEquals(new Complex(0, 0), SH.parseComplex("0i"));
		Assert.assertEquals(new Complex(0, 0), SH.parseComplex("+0i"));
		Assert.assertEquals(new Complex(0, 0), SH.parseComplex("-0i"));
		Assert.assertEquals(new Complex(0, 0), SH.parseComplex("0+0i"));
		Assert.assertEquals(new Complex(5, 0), SH.parseComplex("+5+0i"));
		Assert.assertEquals(new Complex(5, 0), SH.parseComplex("5+0i"));
		Assert.assertEquals(new Complex(-5, 0), SH.parseComplex("-5-0i"));

		Assert.assertEquals(new Complex(0, 7), SH.parseComplex("7i"));
		Assert.assertEquals(new Complex(0, 7), SH.parseComplex("+7i"));
		Assert.assertEquals(new Complex(0, -7), SH.parseComplex("-7i"));
		Assert.assertEquals(new Complex(0, 7), SH.parseComplex("0+7i"));
		Assert.assertEquals(new Complex(5, 7), SH.parseComplex("+5+7i"));
		Assert.assertEquals(new Complex(5, 7), SH.parseComplex("5+7i"));
		Assert.assertEquals(new Complex(-5, -7), SH.parseComplex("-5-7i"));
		Assert.assertEquals(new Complex(-5, -7), SH.parseComplex("-5-7j"));
	}

}
