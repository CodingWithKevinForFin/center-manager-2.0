package com.f1.utils;

import static org.junit.Assert.*;
import java.util.List;
import org.junit.Test;
import com.f1.utils.impl.SimpleReiterable;
import com.f1.utils.structs.Tuple2;

public class IteratorTests {
	@Test
	public void testCartesianIterator() {
		Reiterable<Integer> l = new SimpleReiterable(CH.l(1, 2, 3));
		CartesianIterator<Integer, Integer> i = new CartesianIterator<Integer, Integer>(l, l);
		assertTrue(i.hasNext());
		assertEquals(new Tuple2<Integer, Integer>(1, 1), i.next());
		assertTrue(i.hasNext());
		assertEquals(new Tuple2<Integer, Integer>(1, 2), i.next());
		assertTrue(i.hasNext());
		assertEquals(new Tuple2<Integer, Integer>(1, 3), i.next());
		assertTrue(i.hasNext());
		assertEquals(new Tuple2<Integer, Integer>(2, 1), i.next());
		assertTrue(i.hasNext());
		assertEquals(new Tuple2<Integer, Integer>(2, 2), i.next());
		assertTrue(i.hasNext());
		assertEquals(new Tuple2<Integer, Integer>(2, 3), i.next());
		assertTrue(i.hasNext());
		assertEquals(new Tuple2<Integer, Integer>(3, 1), i.next());
		assertTrue(i.hasNext());
		assertEquals(new Tuple2<Integer, Integer>(3, 2), i.next());
		assertTrue(i.hasNext());
		assertEquals(new Tuple2<Integer, Integer>(3, 3), i.next());
		assertFalse(i.hasNext());

		Reiterable<Integer> empty = new SimpleReiterable<Integer>((List) CH.l());
		i = new CartesianIterator<Integer, Integer>(empty, empty);
		assertFalse(i.hasNext());

		i = new CartesianIterator<Integer, Integer>(empty, l);
		assertFalse(i.hasNext());

		i = new CartesianIterator<Integer, Integer>(l, empty);
		assertFalse(i.hasNext());

		l = new SimpleReiterable<Integer>(CH.l(1));
		i = new CartesianIterator<Integer, Integer>(l, l);
		assertTrue(i.hasNext());
		assertEquals(new Tuple2<Integer, Integer>(1, 1), i.next());
		assertFalse(i.hasNext());
	}

}
