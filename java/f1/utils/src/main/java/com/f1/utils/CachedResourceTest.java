package com.f1.utils;

public class CachedResourceTest {

	public static void main(String a[]) {
		CachedResource cr = new CachedResource("com/f1/utils/test.properties", 2000);
		while (true) {
			OH.sleep(1000);
			System.out.println(cr.getData().getText());
		}
	}
}
