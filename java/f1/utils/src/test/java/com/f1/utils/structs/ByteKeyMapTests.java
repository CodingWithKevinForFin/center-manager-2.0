package com.f1.utils.structs;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

import com.f1.utils.structs.ByteKeyMap.Node;
import com.f1.utils.structs.ByteKeyMap.NodeIterator;

public class ByteKeyMapTests {

	@Test
	public void testByteKeyMap() {
		ByteKeyMap map = new ByteKeyMap<Byte>();
	}

	@Test
	public void testPut() {
		ByteKeyMap map = new ByteKeyMap<Byte>();
		for (int i = 0; i < 128; i++) {
			map.put((byte) i, i * 10);
		}
	}

	@Test
	public void testRemove() {
		ByteKeyMap map = new ByteKeyMap<Byte>();
		for (int i = 0; i < 128; i++) {
			map.put((byte) i, i * 10);
		}
		for (int i = 0; i < 128; i++) {
			if (i % 3 == 0)
				assertEquals(i * 10, map.remove((byte) i));
		}
		map.remove((byte) -1);
	}

	@Test
	public void testGetNode() {
		ByteKeyMap map = new ByteKeyMap<Byte>();
		for (int i = 0; i < 128; i++) {
			map.put((byte) i, i * 10);
			Node node = new Node<Integer>((byte) i, i * 10, null);
			assertEquals(node.getValue(), map.getNode((byte) i).getValue());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testGetNodeFail() {
		ByteKeyMap map = new ByteKeyMap<Byte>();
		map.getNode((byte) 3).getValue();
	}

	@Test
	public void testGet() {
		ByteKeyMap map = new ByteKeyMap<Byte>();
		for (int i = 0; i < 128; i++) {
			map.put((byte) i, i * 10);
			Node node = new Node<Integer>((byte) i, i * 10, null);
			assertEquals(node.getValue(), map.get((byte) i));
		}
		assertEquals(null, map.get((byte) -1));
	}

	@Test
	public void testGetNodeOrCreate() {
		ByteKeyMap map = new ByteKeyMap<Byte>();
		for (int i = 0; i < 128; i++) {
			map.put((byte) i, i * 10);
			Node node = new Node<Integer>((byte) i, i * 10, null);
			assertEquals(node.getValue(), map.getNodeOrCreate((byte) i).getValue());
		}
		assertEquals(null, map.get((byte) -1));
	}

	@Test
	public void testNode() {
		Node node = new Node<Integer>((byte) 1, 10, null);
		Node node2 = new Node<Integer>((byte) 1, 10, new Node<Integer>((byte) 2, 20, null));
	}

	@Test
	public void testGetValue() {
		for (int i = 0; i < 128; i++) {
			Node node = new Node<Integer>((byte) i, i * 10, null);
			assertEquals(new Integer(i * 10), node.getValue());
		}
	}

	@Test
	public void testGetKey() {
		for (int i = 0; i < 128; i++) {
			Node node = new Node<Integer>((byte) i, i * 10, null);
			assertEquals((byte) i, node.getByteKey());
		}
	}

	@Test
	public void testSetValue() {
		for (int i = 0; i < 128; i++) {
			Node node = new Node<Integer>((byte) i, i, null);
			node.setValue(i * 10);
			assertEquals(new Integer(i * 10), node.getValue());
		}
	}

	@Test
	public void testHasNext() {
		ByteKeyMap map = new ByteKeyMap<Byte>();
		assertFalse(map.iterator().hasNext());
		map.put((byte) 1, 10);
		map.put((byte) 1, 100);
		map.put((byte) 2, 20);
		assertTrue(map.iterator().hasNext());
	}

	@Test
	public void testNext() {
		ByteKeyMap map = new ByteKeyMap<Byte>();
		for (int i = 0; i < 128; i++) {
			map.put((byte) i, i * 10);
		}
		NodeIterator iterator = (NodeIterator) map.iterator();
		for (int i = 0; i < 128; i++) {
			assertEquals(new Integer(i * 10), iterator.next().getValue());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testNextFail() {
		ByteKeyMap map = new ByteKeyMap<Byte>();
		assertEquals(null, map.iterator().next());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testIteratorRemove() {
		ByteKeyMap map = new ByteKeyMap<Byte>();
		map.iterator().remove();
	}

	@Test
	public void testSize() {
		ByteKeyMap map = new ByteKeyMap<Byte>();
		assertEquals(0, map.size());
		int limit = 128;
		for (int i = 0; i < limit; i++) {
			map.put((byte) i, i * 10);
		}
		assertEquals(limit, map.size());
	}

}
