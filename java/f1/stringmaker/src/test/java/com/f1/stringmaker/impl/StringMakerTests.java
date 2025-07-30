package com.f1.stringmaker.impl;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.antlr.stringtemplate.StringTemplate;

import com.f1.stringmaker.StringMaker;
import com.f1.utils.CH;
import com.f1.utils.Duration;

public class StringMakerTests {

	//	@Test
	public void test1() {

		List<String> users = CH.l("peter", "steve", "john", null);
		List<String> users2 = CH.l("peter");
		Map<Object, Object> m = CH.m("rob", true, "!dave!", "price", "flag", true, "flag2", false, "users", users, "users2", users2, "a", "one");
		compare("$users:{s|$if(rob)$<li>$s$</li>$endif$  }$what", m);
		compare("test", m);
		compare("test$rob$", m);
		compare("test$rob$this", m);
		compare("test$rob$this$!dave!$here", m);
		compare("test$rob$this$!dave!$here$if(flag)$ok$endif$", m);
		compare("test$rob$this$!dave!$here$if(flag2)$ok$else$notOK$endif$", m);
		compare("test$rob$this$!dave!$here$if(rob)$ok$else$notOK$endif$", m);
		compare("$users:{s|<li>$s$</li>}$", m);
		compare("$users:{s|$if(rob)$<li>$s$</li>$endif$}$", m);
		compare("$users:{s|$if(rob)$<li>$s$</li>$endif$  }$", m);
		compare("$first(users):{s|$if(rob)$<li>$s$</li>$endif$  }$};", m);
		compare("$rest(users):{s|$if(rob)$<li>$s$</li>$endif$  }$  ", m);
		compare("$users2:{s|<li>$s$</li>}$", m);
		compare("$users2:{s|$if(rob)$<li>$s$</li>$endif$}$", m);
		compare("$users2:{s|$if(rob)$<li>$s$</li>$endif$  }$", m);
		compare("$first(users2):{s|$if(rob)$<li>$s$</li>$endif$  }$};", m);
		compare("$rest(users2):{s|$if(rob)$<li>$s$</li>$endif$  }$};", m);
		compare("test$rob$this$!dave!$here$if(rob)$ok$elseif(flag2)$notOK$endif$", m);
		compare("test$a$", m);
	}

	public void compare(String text, Map attributes) {
		StringTemplate stm = new StringTemplate(text);
		StringTemplate st2 = stm.getInstanceOf();
		st2.setAttributes(attributes);
		String st = st2.toString();
		String sm = StringMakerUtils.toString(text, attributes);
		System.out.println("----");
		System.out.println(st);
		System.out.println(sm);
		assertEquals(st, sm);
	}

	public void comparePerformance(String text, Map attributes) {
		System.out.println("-----");
		for (int j = 0; j < 3; j++) {
			Duration d = new Duration("Standard (kb):");
			int len = 0;
			for (int i = 0; i < 100000; i++) {
				StringTemplate stm = new StringTemplate(text);
				StringTemplate st2 = stm.getInstanceOf();
				st2.setAttributes(attributes);
				String st = st2.toString();
				len += st.length();
			}
			d.stampStdout(len / 1024);
		}

		for (int j = 0; j < 3; j++) {
			Duration d = new Duration("  3forge (kb):");
			int len = 0;
			for (int i = 0; i < 100000; i++) {
				String sm = StringMakerUtils.toString(text, attributes);
				len += sm.length();
			}
			d.stampStdout(len / 1024);
		}
		System.out.println("-----");
	}

	public void comparePerformance2(String text, Map attributes) {
		System.out.println("-----");
		StringTemplate stm = new StringTemplate(text);
		for (int j = 0; j < 3; j++) {
			Duration d = new Duration("Standard w/o parse(kb):");
			int len = 0;
			for (int i = 0; i < 100000; i++) {
				StringTemplate st2 = stm.getInstanceOf();
				st2.setAttributes(attributes);
				String st = st2.toString();
				len += st.length();
			}
			d.stampStdout(len / 1024);
		}

		StringMaker maker = StringMakerUtils.toMaker(text);
		for (int j = 0; j < 3; j++) {
			Duration d = new Duration("  3forge w/o parse(kb):");
			int len = 0;
			for (int i = 0; i < 100000; i++) {
				String sm = StringMakerUtils.toString(maker, attributes);
				len += sm.length();
			}
			d.stampStdout(len / 1024);
		}
		System.out.println("-----");
	}
}
