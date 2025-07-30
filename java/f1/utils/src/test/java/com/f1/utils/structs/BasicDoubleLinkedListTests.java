package com.f1.utils.structs;

import static org.junit.Assert.*;
import org.junit.Test;

public class BasicDoubleLinkedListTests {

	@Test
	public void test1() {

		BasicDoubleLinkedList<AbstractDoubleLinkedListEntry> list = new BasicDoubleLinkedList<AbstractDoubleLinkedListEntry>();
		for (int i = 0; i < 5; i++) {
			assertList(list);
			System.out.println(list);
			list.add(new BasicDoubleLinkedListEntry<Integer>(i));
		}
		for (int i = 0; i < 5; i++) {
			assertList(list);
			System.out.println(list);
			list.remove(list.getHead());
		}
		for (int i = 0; i < 5; i++) {
			assertList(list);
			System.out.println(list);
			list.add(new BasicDoubleLinkedListEntry<Integer>(i));
		}
		for (int i = 0; i < 5; i++) {
			assertList(list);
			System.out.println(list);
			list.remove(list.getTail());
		}
		System.out.println(list);

	}

	private void assertList(BasicDoubleLinkedList<AbstractDoubleLinkedListEntry> list) {
		int size = 0;
		AbstractDoubleLinkedListEntry current = list.getHead();
		AbstractDoubleLinkedListEntry prior = null;

		while (current != null) {
			assertEquals(prior, current.getPrior());
			prior = current;
			current = current.getNext();
			size++;
		}
		assertEquals(prior, list.getTail());
	}
}
