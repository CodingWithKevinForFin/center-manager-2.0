package com.f1.utils.grpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.grpc.ProtobufUtils.ProtobufPrimaryKey;
import com.google.protobuf.MapEntry;

import junit.framework.Assert;

public class ProtobufUtilsTest {

	@SuppressWarnings("unchecked")
	private final Map<String, Object> m = CH.m("a", CH.l(1, 2, 3), "b", CH.m("1", 1, "2", 2), "c", CH.l(CH.m("a", 1), CH.m("b", 2)));

	private void handleJsonObject(final Object o, final ProtobufPrimaryKey primaryKey) {
		if (o instanceof MapEntry) {
			MapEntry<?, ?> entry = (MapEntry<?, ?>) o;
			if (primaryKey.shouldParse())
				primaryKey.setShouldSkip(primaryKey.checkKeyMatches(SH.toString(entry.getKey()), entry.getValue()));
		} else {
			if (primaryKey.shouldParse() && primaryKey.atRoot()) {
				primaryKey.setKey(o);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void toInnerMap(final Map<String, Object> m, final ProtobufPrimaryKey primaryKey) {
		final boolean shouldParse = primaryKey.shouldParse();
		for (final Entry<String, Object> es : m.entrySet()) {
			final Object o = es.getValue();
			if (shouldParse)
				primaryKey.setShouldSkip(!primaryKey.checkKeyMatches(es.getKey(), o));
			if (o instanceof List) {
				toInnerList((List) o, primaryKey);
			} else if (o instanceof Map) {
				toInnerMap((Map<String, Object>) o, primaryKey);
			} else {
				handleJsonObject(o, primaryKey);
			}
			primaryKey.setShouldSkip(!shouldParse);
		}
	}

	@SuppressWarnings("unchecked")
	private void toInnerList(final List<Object> l, final ProtobufPrimaryKey primaryKey) {
		boolean shouldParse = primaryKey.shouldParse();
		for (final Object o : l) {
			if (shouldParse)
				primaryKey.setShouldSkip(!primaryKey.parseList());
			if (o instanceof List) {
				toInnerList((List<Object>) o, primaryKey);
			} else if (o instanceof Map) {
				toInnerMap((Map<String, Object>) o, primaryKey);
			} else {
				handleJsonObject(o, primaryKey);
			}

			primaryKey.setShouldSkip(!shouldParse);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void parseMapInner(Map<String, Object> m, final ProtobufPrimaryKey primaryKey) {
		final boolean shouldParse = primaryKey.shouldParse();
		for (final Entry<String, Object> es : m.entrySet()) {
			final Object o = es.getValue();
			if (shouldParse)
				primaryKey.setShouldSkip(!primaryKey.checkKeyMatches(es.getKey(), o));
			if (o instanceof List) {

				toInnerList((List) o, primaryKey);
			} else if (o instanceof Map) {
				toInnerMap((Map<String, Object>) o, primaryKey);
			} else {
				handleJsonObject(o, primaryKey);

			}
			primaryKey.setShouldSkip(!shouldParse);
		}
	}

	//Valid index
	@Test
	public void TestPrimaryKeyParser() {
		final ProtobufPrimaryKey pk = new ProtobufPrimaryKey("a.2");
		parseMapInner(m, pk);
		Assert.assertEquals("2", pk.getParsedKey());
	}

	//Invalid index
	@Test
	public void TestPrimaryKeyParser2() {
		final ProtobufPrimaryKey pk = new ProtobufPrimaryKey("a.23");
		parseMapInner(m, pk);
		Assert.assertNull(pk.getParsedKey());
	}

	//Invalid key
	@Test
	public void TestPrimaryKeyParser3() {
		final ProtobufPrimaryKey pk = new ProtobufPrimaryKey("a.b");
		parseMapInner(m, pk);
		Assert.assertNull(pk.getParsedKey());
	}

	//Invalid key
	@Test
	public void TestPrimaryKeyParser4() {
		final ProtobufPrimaryKey pk = new ProtobufPrimaryKey("b.1");
		parseMapInner(m, pk);
		Assert.assertEquals("1", pk.getParsedKey());
	}

	//Invalid index
	@Test
	public void TestPrimaryKeyParser5() {
		final ProtobufPrimaryKey pk = new ProtobufPrimaryKey("a.0");
		parseMapInner(m, pk);
		Assert.assertNull(pk.getParsedKey());
	}

	//Invalid index
	@Test
	public void TestPrimaryKeyParser6() {
		final ProtobufPrimaryKey pk = new ProtobufPrimaryKey("a.1");
		parseMapInner(m, pk);
		Assert.assertEquals("1", pk.getParsedKey());
	}

	@Test
	public void TestPrimaryKeyParser7() {
		final ProtobufPrimaryKey pk = new ProtobufPrimaryKey("c.1.a");
		parseMapInner(m, pk);
		Assert.assertEquals("1", pk.getParsedKey());
	}

	@Test
	public void TestPrimaryKeyParser8() {
		final ProtobufPrimaryKey pk = new ProtobufPrimaryKey("c.1.b");
		parseMapInner(m, pk);
		Assert.assertNull(pk.getParsedKey());
	}

	@Test
	public void TestPrimaryKeyParser9() {
		final ProtobufPrimaryKey pk = new ProtobufPrimaryKey("c.2.b");
		parseMapInner(m, pk);
		Assert.assertEquals("2", pk.getParsedKey());
	}

	@Test
	public void TestPrimaryKeyParser10() {
		final List<Integer> l1 = CH.l(1, 2, 3), l2 = CH.l(2, 3, 4);
		final ArrayList<List<Integer>> l3 = new ArrayList<List<Integer>>();
		l3.add(l1);
		l3.add(l2);
		final Map<String, Object> testMap = CH.m("d", l3);

		final ProtobufPrimaryKey pk = new ProtobufPrimaryKey("d.1.1");
		parseMapInner(testMap, pk);
		Assert.assertEquals("1", pk.getParsedKey());
	}

}
