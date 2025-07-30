package com.f1.tester.json;

import com.f1.utils.string.Node;

public class TestingExpression {

	final private Node node;
	final private String text;

	public TestingExpression(Node node, String text) {
		this.node = node;
		this.text = text;
	}

	public Node getNode() {
		return node;
	}

	@Override
	public String toString() {
		return ";" + text;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestingExpression other = (TestingExpression) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}

}
