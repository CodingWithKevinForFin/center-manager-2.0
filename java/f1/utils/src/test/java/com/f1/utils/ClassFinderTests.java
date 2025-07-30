package com.f1.utils;

import java.io.IOException;
import com.f1.base.Message;
import com.f1.utils.mirror.reflect.ReflectedClassMirror;

public class ClassFinderTests {

	public static void main(String a[]) throws IOException {
		ClassFinder cf = new ClassFinder();
		cf.searchClasspath(ClassFinder.TYPE_DIRECTORY);
		cf = cf.filterByExtends(ReflectedClassMirror.valueOf(Message.class));
		for (ClassFinderEntry o : cf) {
			System.out.println(o);
		}
	}
}
