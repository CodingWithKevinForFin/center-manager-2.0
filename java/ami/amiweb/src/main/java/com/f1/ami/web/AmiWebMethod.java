package com.f1.ami.web;

import java.util.ArrayList;
import java.util.List;

import com.f1.utils.CharReader;
import com.f1.utils.SH;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.Node;

public class AmiWebMethod {

	private static final int[] SPACE_OR_OPEN = StringCharReader.toInts(" \t\n\r(");
	private static final int[] SPACE_OR_COMMA_OR_CLOSE = StringCharReader.toInts(" \t\n\r,)");
	private AmiWebService service;
	private String retType;
	private String name;
	private List<String> paramNames = new ArrayList<String>();
	private List<String> paramTypes = new ArrayList<String>();
	private String declaration;
	private Node body;

	public AmiWebMethod(AmiWebService service, String declaration) {
		this.declaration = declaration;
		StringBuilder sink = new StringBuilder();
		StringCharReader cr = new StringCharReader(declaration);

		cr.readUntilAny(StringCharReader.WHITE_SPACE, false, sink);
		this.retType = SH.toStringAndClear(sink);

		cr.skip(StringCharReader.WHITE_SPACE);
		cr.readUntilAny(SPACE_OR_OPEN, sink);
		this.name = SH.toStringAndClear(sink);
		//TODO:ensure valid name

		cr.skip(StringCharReader.WHITE_SPACE);
		cr.expect('(');
		for (;;) {
			cr.skip(StringCharReader.WHITE_SPACE);
			if (cr.peak() == ')')
				break;
			cr.readUntilAny(SPACE_OR_OPEN, sink);
			String type = SH.toStringAndClear(sink);
			cr.skip(StringCharReader.WHITE_SPACE);
			cr.readUntilAny(SPACE_OR_COMMA_OR_CLOSE, sink);
			String name = SH.toStringAndClear(sink);
			this.paramNames.add(name);
			//TODO:ensure valid name

			this.paramTypes.add(type);
		}
		cr.expect(')');
		cr.skip(StringCharReader.WHITE_SPACE);
		cr.readUntil(CharReader.EOF, sink);
		int end = SH.lastIndexOf(sink, '}');
		if (end == -1)
			throw new ExpressionParserException(declaration.length(), "Missing closing: }");
		sink.setLength(end + 1);
		StringCharReader reader = new StringCharReader(sink);
	}

}
