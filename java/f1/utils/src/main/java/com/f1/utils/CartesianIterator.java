/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;
import com.f1.utils.structs.Tuple2;

public class CartesianIterator<L, R> implements Iterator<Tuple2<L, R>> {
	final private static byte START = 1;
	private static final byte RIGHT_NEEDS_NEXT = 2;
	final private static byte LEFT_NEEDS_NEXT = 3;
	final private static byte END = 4;

	private Iterator<L> leftIterator;
	private Iterator<R> rightIterator;
	final private Reiterable<R> rightIterable;
	private Reiterable<L> leftIterable;
	private L currentLeft;
	private byte stage;
	private R currentRight;

	public CartesianIterator(Reiterable<L> leftIterable, Reiterable<R> rightIterable) {
		this.leftIterable = leftIterable;
		this.rightIterable = rightIterable;
		resetIterator();
	}

	public void resetIterator() {
		leftIterator = leftIterator == null ? leftIterable.iterator() : leftIterable.iterator(leftIterator);
		rightIterator = rightIterator == null ? rightIterable.iterator() : rightIterable.iterator(rightIterator);
		stage = (rightIterator.hasNext() && leftIterator.hasNext()) ? START : END;
	}

	@Override
	public boolean hasNext() {
		return stage != END;
	}

	@Override
	public Tuple2<L, R> next() {
		switch (stage) {
			case START : {
				this.currentLeft = leftIterator.next();
				currentRight = rightIterator.next();
				stage = rightIterator.hasNext() ? RIGHT_NEEDS_NEXT : leftIterator.hasNext() ? LEFT_NEEDS_NEXT : END;
				break;
			}
			case LEFT_NEEDS_NEXT : {
				this.currentLeft = leftIterator.next();
				this.rightIterator = rightIterator == null ? rightIterable.iterator() : rightIterable.iterator(rightIterator);
				currentRight = rightIterator.next();
				stage = rightIterator.hasNext() ? RIGHT_NEEDS_NEXT : leftIterator.hasNext() ? LEFT_NEEDS_NEXT : END;
				break;
			}
			case RIGHT_NEEDS_NEXT : {
				currentRight = rightIterator.next();
				if (!rightIterator.hasNext())
					stage = leftIterator.hasNext() ? LEFT_NEEDS_NEXT : END;
				break;
			}
			default :
				throw new NoSuchElementException();
		}
		return new Tuple2<L, R>(this.currentLeft, currentRight);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
