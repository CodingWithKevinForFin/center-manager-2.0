/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class MergeSortedIterator<I extends Object> implements Iterator<I> {
	private Iterator<I> left, right;
	private Comparator<I> comparator;
	private I leftValue, rightValue;

	public MergeSortedIterator(Iterator<I> left, Iterator<I> right, Comparator<I> comparator) {
		this.left = left;
		this.right = right;
		this.comparator = comparator;
		nextLeft();
		nextRight();
	}

	private void nextRight() {
		if (right == null || !right.hasNext()) {
			right = null;
			rightValue = null;
		} else
			rightValue = right.next();

	}

	private void nextLeft() {
		if (left == null || !left.hasNext()) {
			left = null;
			leftValue = null;
		} else
			leftValue = left.next();
	}

	@Override
	public boolean hasNext() {
		return left != null || right != null;
	}

	@Override
	public I next() {
		I r;
		if (left == null) {
			if (right == null)
				throw new NoSuchElementException();
			r = rightValue;
			nextRight();
		} else if (right == null) {
			r = leftValue;
			nextLeft();
		} else {
			if (comparator.compare(leftValue, rightValue) < 0) {
				r = leftValue;
				nextLeft();
			} else {
				r = rightValue;
				nextRight();
			}
		}
		return r;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	public static void main(String a[]) {
		List<Integer> l1 = CH.l(1, 4, 6, 7, 8, 10);
		List<Integer> l2 = CH.l(3, 5, 7, 8, 11);
		MergeSortedIterator<Integer> j = new MergeSortedIterator<Integer>(l1.iterator(), l2.iterator(), (Comparator) CH.COMPARATOR);
		System.out.println(SH.join(',', new Iterator2Iterable<Integer>(j)));

	}
}
