package com.f1.utils.string.node;

import com.f1.base.Caster;
import com.f1.utils.SH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.Node;

final public class VariableNode implements Node {

	public static final byte CODE = Node.VARIABLE;
	private String varname;
	final private boolean backtick;

	public VariableNode(int position, String varname) {
		this.position = position;
		this.varname = varname;
		this.backtick = false;
	}

	public VariableNode(int position, String varname, boolean backtick) {
		this.position = position;
		this.varname = varname;
		this.backtick = backtick;
	}

	@Override
	public String toString() {
		return varname;
	}

	final private int position;

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		if (backtick) {
			sink.append('`');
			SH.escape(varname, '`', '\\', sink);
			return sink.append('`');
		}

		return sink.append(varname);
	}

	public boolean hasBacktick() {
		return this.backtick;
	}

	public String getValue() {
		return varname;
		//		if (varname.startsWith("`") && varname.endsWith("`"))
		//			return varname.substring(1, varname.length() - 1);
		//		else
		//			return varname;
	}

	public <T> T castTo(Caster<T> c) {
		try {
			return c.cast(this.varname);
		} catch (Exception e) {
			throw new ExpressionParserException(position, "Not a  valid " + c.getCastToClass().getSimpleName() + " '" + varname + "' ");
		}
	}

	@Deprecated
	public void appendToVarname(String string) {
		this.varname += string;
	}

	public String getVarname() {
		return this.varname;
	}

	@Override
	public byte getNodeCode() {
		return CODE;
	}

	@Override
	public int getInnerNodesCount() {
		return 0;
	}

	@Override
	public Node getInnerNode(int n) {
		return null;
	}
}
