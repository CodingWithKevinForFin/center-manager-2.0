package com.f1.utils;

import org.junit.Test;

import junit.framework.Assert;

public class JsonExtactTests {

	@Test
	public void test() {
		test("  {   0 :  1  }  ", "0", "1");
		test("  ['rob','dave','steve\\'s',{e:1,f:2}]", "2.f", null);
		test("  ['rob','dave','steve\\'s',{e:1,f:2}]", "2", "steve's");
		test("  {a:1,b:['rob','dave','steve\\'s',{e:1,f:2}]}", "b.2.f", null);
		test("  {me:-123,you:'where'}  ", "me", "-123");
		test("  {me:123,you:'where'}  ", "us", null);
		test("  {me:123,you:'where'}  ", "", "{me:123,you:'where'}");
		test("  {\"nonce\":\"asdf\",\"LAT\":123,LON:'where'}  ", "LAT", "123");
		test("  {\"nonce\":\"asdf\",\'LAT\':123,LON:'where'}  ", "LAT", "123");
		test("  {\"nonce\":\"asdf\",  LAT  :123,LON:'where'}  ", "LAT", "123");
		test("\"me\\\"here\"", "", "me\"here");
		test("  {me:123,you:'where'}  ", "me", "123");
		test("  {me:[4,5,6,72],you:'where'}  ", "you", "where");
		test("  {me:[4,5,6,{when:34,where:{this:\"that\"}}],you:'where'}  ", "me.3.where", "{this:\"that\"}");
		test("  {me:[4,5,6,{when:34,where:{this:\"that\"}}],you:'where'}  ", "you", "where");
		test("  {me:[4,5,6,{when:34,where:{this:\"that\"}}],you:null}  ", "me.2", "6");
		test("  {me:[4,5,6,{when:34,where:{this:\"that\"}}],you:null}  ", "me.2a", null);
		test("  {me:[4,5,6,{when:34,where:{this:\"that\"}}],you:true,me:false}  ", "you", "true");
		test("  {me:[4,5,6,{when:34,where:{this:\"that\"}}],you:true,us:false}  ", "us", "false");
		test("  {me:[4,5,6,{when:34,where:{this:\"that\"}}],you:true,us:false}  ", "us", "false");
		test("  {me:[4,5,6,{when:34,where:{this:\"that\"}}],you:true,us:false}  ", "them", null);
		test("  [0,1,2,3,4]  ", "0", "0");
		test("  [0,1,2,3,4]  ", "4", "4");
		test("  [0,1,2,3,4]  ", "-4", null);
		test("  [0,1,2,3,4]  ", "5", null);
		test("  {'5':'five'}", "5", "five");
		test("  {  me : [ 4 , 5,6, 72 ]   , you  \n   : 'where' \n\n\n }  ", "you", "where");
		test("  123.2323 ", "", "123.2323");
		test("  [123.2323, 32.3,null,4] ", "0", "123.2323");
		test("  [123.2323, 32.3,null,4] ", "1", "32.3");
		test("  [123.2323, 32.3,null,4] ", "2", null);
		test("  [123.2323, 32.3,null,4] ", "3", "4");
		test("  [123.2323, 32.3,null,4] ", "1.1", null);
		test("[0,[0,123.2323], 32.3,null,4] ", "1.1", "123.2323");
		test("{flow:[0,[0,123.2323], 32.3,null,4]} ", "flow.1.1", "123.2323");
		test("{'flow':[0,[0,123.2323], 32.3,null,4]} ", "flow.1.1", "123.2323");
	}

	public void test(String json, String path, String expect) {
		String actual = JsonUtils.extractFromJson(json, SH.split('.', path), false);
		Assert.assertEquals(expect, actual);

	}
}
