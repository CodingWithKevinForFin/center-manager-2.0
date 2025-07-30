package com.f1.ami.center.triggers.agg;

import com.f1.ami.center.table.AmiRowImpl;
import com.f1.utils.SH;
import com.f1.utils.concurrent.LinkedHasherMap;
import com.f1.utils.concurrent.LinkedHasherMap.Node;
import com.f1.utils.concurrent.LinkedHasherSet;
import com.f1.utils.mutable.Mutable;
import com.f1.utils.mutable.Mutable.Int;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiTriggerAgg_Cat extends AmiTriggerAgg {

	private static final int NULL = -1;

	public static class AggregateHelper {

		private StringBuilder buf = new StringBuilder();
		private LinkedHasherMap<Long, Mutable.Int> starts = new LinkedHasherMap<Long, Mutable.Int>();//char position of first char in the buf
		LinkedHasherMap<Long, String> extras = new LinkedHasherMap<Long, String>();//rows beyond the limit
		int count = 0;//number of non-null entries
	}

	private DerivedCellCalculator argTwo;
	private DerivedCellCalculator argThree;
	private String delimiter;
	private int limit;

	public AmiTriggerAgg_Cat(int position, DerivedCellCalculator inner, DerivedCellCalculator argTwo, DerivedCellCalculator argThree) {
		super(position, inner);
		this.argTwo = argTwo;
		this.argThree = argThree;

		if (argTwo.getReturnType() != String.class || !argTwo.isConst() || argTwo.get(null) == null)
			throw new ExpressionParserException(argTwo.getPosition(), "2nd argument must be a constant string");
		if (argThree.getReturnType() != Integer.class || !argThree.isConst() || argThree.get(null) == null)
			throw new ExpressionParserException(argThree.getPosition(), "3rd argument must be a constant Integer");
		this.delimiter = (String) argTwo.get(null);
		if (this.delimiter == null)
			throw new ExpressionParserException(argTwo.getPosition(), "2nd argument must be a constant string");
		this.limit = (Integer) argThree.get(null);
	}

	@Override
	public Class<?> getReturnType() {
		return Integer.class;
	}

	@Override
	public String getMethodName() {
		return "cat";
	}

	@Override
	public DerivedCellCalculator copy() {
		return new AmiTriggerAgg_Cat(getPosition(), getInner().copy(), argTwo.copy(), argThree.copy());
	}

	@Override
	protected Object calculateInsert(Object nuw, Object current, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper, CalcFrameStack sf) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Object calculateInsert(Object nuw, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper, CalcFrameStack sf) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		long amiId = causingSourceRow.getAmiId();
		if (ah.count == limit) {
			ah.extras.put(amiId, s(nuw));
			return current == null ? "" : current;
		}
		if (nuw == null) {
			ah.starts.put(amiId, new Mutable.Int(NULL));
			return current == null ? "" : current;
		}
		if (ah.count > 0)
			ah.buf.append(delimiter);
		ah.count++;
		int start = ah.buf.length();
		ah.starts.put(amiId, new Mutable.Int(start));
		append(ah.buf, nuw);

		return ah.buf.toString();
	}

	private String s(Object current) {
		if (current == null)
			return null;
		SH.clear(localBuf);
		append(localBuf, current);
		return localBuf.toString();
	}

	StringBuilder localBuf = new StringBuilder();

	@Override
	protected Object calculateUpdate(Object old, Object nuw, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper,
			CalcFrameStack sf) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		long amiId = causingSourceRow.getAmiId();
		if (old == nuw) {
			return current;
		} else if (old == null) {
			LinkedHasherMap.Node<Long, Int> node = ah.starts.getNode(amiId);
			if (node == null) {
				ah.extras.put(amiId, s(nuw));
				return current;
			}
			if (ah.count == limit) {
				moveLastToExtras(ah);
			}
			ah.count++;
			LinkedHasherMap.Node<Long, Int> nextNode = getNextNotNull(node);
			LinkedHasherMap.Node<Long, Int> priorNode = getPriorNotNull(node);
			if (priorNode == null) {
				if (nextNode == null) {//only node
					node.getValue().value = 0;
					append(ah.buf, nuw);
				} else {//is first
					node.getValue().value = 0;
					SH.clear(localBuf);
					append(localBuf, nuw);
					localBuf.append(delimiter);
					int delta = localBuf.length();
					SH.replaceInline(ah.buf, 0, 0, localBuf, 0, localBuf.length());
					adjustPositions(nextNode, delta);
				}
			} else if (nextNode == null) {//is last
				ah.buf.append(delimiter);
				node.getValue().value = ah.buf.length();
				append(ah.buf, nuw);
			} else {//is middle
				int start = nextNode.getValue().value;
				node.getValue().value = start;
				SH.clear(localBuf);
				append(localBuf, nuw);
				localBuf.append(delimiter);
				int delta = localBuf.length();
				SH.replaceInline(ah.buf, start, start, localBuf, 0, localBuf.length());
				adjustPositions(nextNode, delta);
			}
		} else if (nuw == null) {
			processDelete(current, amiId, ah);
			ah.starts.get(amiId).value = NULL;
			if (ah.count + 1 == limit)
				moveFromExtrasToLast(ah, sourceRows);
		} else {
			LinkedHasherMap.Node<Long, Int> node = ah.starts.getNode(amiId);
			if (node == null) {
				ah.extras.put(amiId, s(nuw));
				return current;
			}
			LinkedHasherMap.Node<Long, Int> nextNode = getNextNotNull(node);
			int startChar = node.getValue().value;
			if (nextNode == null) {
				ah.buf.setLength(startChar);
				append(ah.buf, nuw);
			} else {
				int endChar = nextNode.getValue().value - delimiter.length();
				SH.clear(localBuf);
				append(localBuf, nuw);
				int delta = localBuf.length() - (endChar - startChar);
				SH.replaceInline(ah.buf, startChar, endChar, localBuf, 0, localBuf.length());
				adjustPositions(nextNode, delta);
			}
		}
		return ah.buf.toString();
	}
	private void moveLastToExtras(AggregateHelper ah) {
		Node<Long, Int> t = ah.starts.getTailNode();
		for (;;) {
			ah.starts.remove(t.getKey());
			if (isNull(t)) {
				ah.extras.putAtHead(t.getKey(), null);
				t = t.getPriorNode();
			} else {
				int start = t.getValue().value;
				ah.extras.putAtHead(t.getKey(), ah.buf.substring(start));
				if (--ah.count == 0)
					ah.buf.setLength(0);
				else
					ah.buf.setLength(start - delimiter.length());
				break;
			}
		}
	}
	private boolean isNull(LinkedHasherMap.Node<Long, Int> n) {
		return n.getValue().value == NULL;
	}

	@Override
	protected Object calculateDelete(Object old, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper, CalcFrameStack sf) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		if (old == null)
			return current;
		long amiId = causingSourceRow.getAmiId();
		processDelete(current, amiId, ah);
		ah.starts.remove(amiId);
		if (ah.count + 1 == limit)
			moveFromExtrasToLast(ah, sourceRows);
		return ah.buf.toString();
	}

	private void moveFromExtrasToLast(AggregateHelper ah, LinkedHasherSet<AmiRowImpl> sourceRows) {
		for (;;) {
			Node<Long, String> t = ah.extras.getHeadNode();
			if (t == null)
				return;
			ah.extras.remove(t.getKey());
			if (t.getValue() == null)
				ah.starts.put(t.getKey(), new Mutable.Int(-1));
			else {
				if (ah.count++ != 0)
					ah.buf.append(delimiter);
				ah.starts.put(t.getKey(), new Mutable.Int(ah.buf.length()));
				append(ah.buf, t.getValue());
				return;
			}
		}
	}
	private void processDelete(Object current, long amiId, AggregateHelper ah) {
		LinkedHasherMap.Node<Long, Int> node = ah.starts.getNode(amiId);
		if (node == null) {
			ah.extras.remove(amiId);
			return;
		}
		LinkedHasherMap.Node<Long, Int> nextNode = getNextNotNull(node);
		LinkedHasherMap.Node<Long, Int> priorNode = getPriorNotNull(node);
		final int begin, end;
		if (priorNode == null) {
			if (nextNode == null) {//only node
				ah.buf.setLength(0);
				ah.count = 0;
				begin = end = 0;
			} else {//first node
				begin = 0;
				end = nextNode.getValue().value;
			}
		} else if (nextNode == null) {//last node
			begin = node.getValue().value - delimiter.length();
			end = ah.buf.length();
		} else {//middle node
			begin = node.getValue().value;
			end = nextNode.getValue().value;
		}
		int delta = end - begin;
		ah.count--;
		adjustPositions(nextNode, -delta);
		ah.buf.delete(begin, end);
	}

	private void adjustPositions(LinkedHasherMap.Node<Long, Int> nextNode, int delta) {
		if (delta != 0)
			for (; nextNode != null; nextNode = getNextNotNull(nextNode))
				nextNode.getValue().value += delta;
	}
	private LinkedHasherMap.Node<Long, Int> getNextNotNull(LinkedHasherMap.Node<Long, Int> node) {
		for (node = node.getNextNode(); node != null; node = node.getNextNode())
			if (!isNull(node))
				return node;
		return null;
	}
	private LinkedHasherMap.Node<Long, Int> getPriorNotNull(LinkedHasherMap.Node<Long, Int> node) {
		for (node = node.getPriorNode(); node != null; node = node.getPriorNode())
			if (!isNull(node))
				return node;
		return null;
	}

	private void append(StringBuilder buf, Object nuw) {
		buf.append(nuw);//TODO:more efficent based on inner return type
	}

	@Override
	public boolean needsHelper() {
		return true;
	}
	public Object initHelper() {
		return new AggregateHelper();
	};
}
