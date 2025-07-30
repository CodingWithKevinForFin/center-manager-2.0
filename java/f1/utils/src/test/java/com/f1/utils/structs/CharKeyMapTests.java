package com.f1.utils.structs;

import static org.junit.Assert.*;
import org.junit.Test;
import com.f1.utils.structs.CharKeyMap.Node;
import com.f1.utils.structs.CharKeyMap.NodeIterator;

public class CharKeyMapTests {

	@Test
	public void testCharKeyMap() {
		CharKeyMap map = new CharKeyMap<Character>();
	}

	@Test
	public void testPut() {
		CharKeyMap map = new CharKeyMap<Character>();
		for (int i = 0; i < 65536; i++) {
			map.put((char) i, i * 10);
		}
	}

	@Test
	public void testRemove() {
		CharKeyMap map = new CharKeyMap<Character>();
		for (int i = 0; i < 65536; i++) {
			map.put((char) i, i * 10);
		}
		for (int i = 0; i < 65536; i++) {
			if (i % 3 == 0)
				assertEquals(i * 10, map.remove((char) i));
		}
		map.remove((char) -1);
	}

	@Test
	public void testGetNode() {
		CharKeyMap map = new CharKeyMap<Character>();
		for (int i = 0; i < 65536; i++) {
			map.put((char) i, i * 10);
			Node node = new Node<Integer>((char) i, i * 10, null);
			assertEquals(node.getValue(), map.getNode((char) i).getValue());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testGetNodeFail() {
		CharKeyMap map = new CharKeyMap<Character>();
		assertEquals(null, map.getNode('a').getValue());
	}

	@Test
	public void testGet() {
		CharKeyMap map = new CharKeyMap<Character>();
		for (int i = 0; i < 65536; i++) {
			map.put((char) i, i * 10);
			Node node = new Node<Integer>((char) i, i * 10, null);
			assertEquals(node.getValue(), map.get((char) i));
		}
		assertEquals(655350, map.get((char) -1));
	}

	@Test
	public void testGetNodeOrCreate() {
		CharKeyMap map = new CharKeyMap<Character>();
		for (int i = 0; i < 65536; i++) {
			map.put((char) i, i * 10);
			Node node = new Node<Integer>((char) i, i * 10, null);
			assertEquals(node.getValue(), map.getNodeOrCreate((char) i).getValue());
		}
		assertEquals(655350, map.get((char) -1));
	}

	@Test
	public void testNode() {
		Node node = new Node<Integer>('x', 10, null);
		Node node2 = new Node<Integer>('x', 10, new Node<Integer>('y', 20, null));
	}

	@Test
	public void testGetValue() {
		for (int i = 0; i < 65536; i++) {
			Node node = new Node<Integer>((char) i, i * 10, null);
			assertEquals(new Integer(i * 10), node.getValue());
		}
	}

	@Test
	public void testGetKey() {
		for (int i = 0; i < 65536; i++) {
			Node node = new Node<Integer>((char) i, i * 10, null);
			assertEquals((char) i, node.getCharKey());
		}
	}

	@Test
	public void testSetValue() {
		for (int i = 0; i < 65536; i++) {
			Node node = new Node<Integer>((char) i, i, null);
			node.setValue(i * 10);
			assertEquals(new Integer(i * 10), node.getValue());
		}
	}

	@Test
	public void testToString() {
		Node node = new Node<Integer>('x', 10, null);
		assertEquals("x=10", node.toString(new StringBuilder()).toString());
	}

	@Test
	public void testHasNext() {
		CharKeyMap map = new CharKeyMap<Character>();
		assertFalse(map.iterator().hasNext());
		map.put('x', 10);
		map.put('x', 100);
		map.put('y', 20);
		assertTrue(map.iterator().hasNext());
	}

	@Test
	public void testNext() {
		CharKeyMap map = new CharKeyMap<Character>();
		for (int i = 0; i < 65536; i++) {
			map.put((char) i, i * 10);
		}
		NodeIterator iterator = (NodeIterator) map.iterator();
		for (int i = 0; i < 65536; i++) {
			assertEquals(new Integer(i * 10), iterator.next().getValue());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testNextFail() {
		CharKeyMap map = new CharKeyMap<Character>();
		assertEquals(null, map.iterator().next());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testIteratorRemove() {
		CharKeyMap map = new CharKeyMap<Character>();
		map.iterator().remove();
	}

	@Test
	public void testSize() {
		CharKeyMap map = new CharKeyMap<Character>();
		assertEquals(0, map.size());
		int limit = 65536;
		for (int i = 0; i < limit; i++) {
			map.put((char) i, i * 10);
		}
		assertEquals(limit, map.size());
	}

	@Test
	public void testRemove2() {

		CharKeyMap map = new CharKeyMap<Character>();
		map.put('a', 123);
		map.put('e', 124);
		System.out.println(map);
		map.remove('a');
		System.out.println(map);
		assertEquals(1, map.size());
		assertEquals(124, map.get('e'));
	}

}
