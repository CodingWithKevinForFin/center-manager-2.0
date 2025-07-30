package com.f1.tester.json;

import java.io.IOException;
import com.f1.utils.IOH;
import com.f1.utils.assist.RootAssister;

public class TestingToJsonConverterTest {

	public static void main(String a[]) throws IOException {

		String data = IOH.readText(TestingToJsonConverterTest.class.getPackage(), "json.txt");
		TestingToJsonConverter converter = new TestingToJsonConverter();
		Object obj = converter.bytes2Object(data.getBytes());
		System.out.println(RootAssister.INSTANCE.toLegibleString(obj));

	}
}
