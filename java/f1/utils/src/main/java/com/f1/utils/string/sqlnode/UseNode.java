package com.f1.utils.string.sqlnode;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;

public class UseNode extends SqlNode {
	final private Map<String, Node> options;

	public UseNode(int pos, Map<String, Node> options, Node next) {
		super(pos, next, SqlExpressionParser.ID_USE);
		this.options = options;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append(" USE ");
		SH.joinMap(",", "=", options, sink);
		if (getNext() != null)
			getNext().toString(sink.append(' '));
		return sink;
	}

	public Set<String> getOptions() {
		return this.options.keySet();
	}
	public Map<String, Node> getOptionsMap() {
		return this.options;
	}
	public <T> T getOption(Class<T> retType, String string, T defaultValue) {
		Node r = this.options.get(string);
		if (r == null)
			return defaultValue;
		T r2 = OH.cast(r.toString(), retType, false, false);
		if (r2 == null)
			throw new ExpressionParserException(r.getPosition(), "Expecting " + retType.getSimpleName() + " for option " + string);
		return r2;
	}
	public Node getOption(String string) {
		return this.options.get(string);
	}

	public void assertValidOptions(Set<String> s) {
		for (Entry<String, Node> e : this.options.entrySet())
			if (!s.contains(e.getKey()))
				throw new ExpressionParserException(e.getValue().getPosition(), "Invalid USE option: '" + e.getKey() + "'   (valid options include " + SH.join(',', s) + ")");
	}

	@Override
	public int getInnerNodesCount() {
		return getNext() == null ? options.size() : (options.size() + 1);
	}

	@Override
	public Node getInnerNode(int n) {
		if (n == options.size())
			return getNext();
		for (Entry<String, Node> i : options.entrySet())
			if (n-- == 0)
				return i.getValue();
		throw new IndexOutOfBoundsException();
	}
}
