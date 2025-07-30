package com.f1.http.handler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;

import com.f1.codegen.CodeCompiler;
import com.f1.http.HttpHandler;
import com.f1.http.HttpTag;
import com.f1.http.HttpUtils;
import com.f1.http.tag.ElseTag;
import com.f1.http.tag.EmbedTag;
import com.f1.http.tag.ForEachTag;
import com.f1.http.tag.FormatTextTag;
import com.f1.http.tag.IfTag;
import com.f1.http.tag.IncludeTag;
import com.f1.http.tag.OutTag;
import com.f1.http.tag.RedirectTag;
import com.f1.http.tag.ScriptTag;
import com.f1.http.tag.SecureTag;
import com.f1.http.tag.SetTag;
import com.f1.http.tag.UnsecureTag;
import com.f1.stringmaker.StringMaker;
import com.f1.stringmaker.impl.BasicStringMakerSession;
import com.f1.stringmaker.impl.StringMakerUtils;
import com.f1.utils.CH;
import com.f1.utils.CharReader;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.impl.StringCharReader;

public class JspCompiler {
	private static final Logger log = Logger.getLogger(JspCompiler.class.getName());
	private static final int[] BRACKET_OR_EOF = new int[] { '<', CharReader.EOF };

	private final CodeCompiler compiler;
	private final String pckage;
	private final Map<String, JspTagBuilder> tags = new HashMap<String, JspTagBuilder>();

	public JspCompiler(CodeCompiler compiler) {
		this(compiler, "com.f1._jsps");
	}
	public JspCompiler(CodeCompiler compiler, String pckage) {
		this.compiler = compiler;
		this.pckage = pckage;
		addTag("f1:forEach", new ForEachTag());
		addTag("f1:secure", new SecureTag());
		addTag("f1:unsecure", new UnsecureTag());
		addTag("f1:redirect", new RedirectTag());
		addTag("f1:include", new IncludeTag());
		addTag("f1:embed", new EmbedTag());
		addTag("f1:set", new SetTag());
		addTag("f1:if", new IfTag(true));
		addTag("f1:ifNot", new IfTag(false));
		addTag("f1:else", new ElseTag());
		addTag("f1:out", new OutTag());
		addTag("f1:txt", new FormatTextTag());
		addTag("f1:script", new ScriptTag());
	}

	public void addTag(String name, JspTagBuilder tag) {
		CH.putOrThrow(tags, name, tag);
	}

	public String getFullClassName(String fileName, int i) {
		int length = fileName.length();
		StringBuilder sb = new StringBuilder(pckage.length() + 1 + fileName.length() + 1);
		sb.append(pckage).append('.');
		if (OH.isntBetween(Character.toLowerCase(fileName.charAt(0)), 'a', 'z') && fileName.charAt(0) != '_')
			sb.append('_');
		for (int n = 0; n < length; n++) {
			char c = fileName.charAt(n);
			sb.append(OH.isBetween(Character.toLowerCase(c), 'a', 'z') || OH.isBetween(c, '0', '9') ? c : '_');
		}
		sb.append('_').append(i);
		return sb.toString();
	}
	/**
	 * 
	 * @return the classname
	 */
	public HttpHandler compile(String jsp, String fullClassName) {
		final JspBuilderSession jbs = new JspBuilderSession();
		try {
			final String template = IOH.readText(JspHttpHandler.class, ".st");
			final StringMaker maker = StringMakerUtils.toMaker(template);
			final StringCharReader cr = new StringCharReader(jsp);
			final StringBuilder sink = new StringBuilder();
			int indent = 10;
			final Stack<HttpTag> tagStack = new Stack<HttpTag>();
			while (true) {
				if (cr.readUntilAny(BRACKET_OR_EOF, sink) == '<') {
					if (cr.peakSequence("<f1") || cr.peakSequence("</f1")) {
						if (sink.length() > 0) {
							appendText(jbs, sink.toString(), indent);
							SH.clear(sink);
						}
						indent = processJspTag(jbs, cr, indent, tagStack);
					} else {
						sink.append(cr.expect('<'));
						continue;
					}
				} else {// EOF
					if (sink.length() > 0) {
						appendText(jbs, sink.toString(), indent);
						SH.clear(sink);
					}
					break;
				}
			}
			if (tagStack.size() != 0)
				throw new RuntimeException("missing closing tag for: " + tagStack.pop());
			final Map<String, Object> objects = new HashMap<String, Object>();
			objects.put("className", SH.afterLast(fullClassName, '.', fullClassName));
			objects.put("package", pckage);
			objects.put("body", jbs.getBody());
			BasicStringMakerSession session = new BasicStringMakerSession(objects);
			maker.toString(session);
			final String code = session.getSink().toString();
			if (compiler != null)
				compiler.compile(fullClassName, code);
			else
				LH.info(log, "Compiler not found, assuming pre-compiled jsp: ", fullClassName);
			final HttpHandler compiledHandler = (HttpHandler) RH.invokeConstructor(fullClassName);
			return compiledHandler;
		} catch (IOException e) {
			LH.info(log, "Error compiling file: ", fullClassName, e);
			return null;
		}

	}
	private int processJspTag(JspBuilderSession jbs, CharReader text, int indent, Stack<HttpTag> tagStack) {
		final HttpTag tag = HttpUtils.parseTag(text);
		JspTagBuilder tagBuilder = CH.getOrThrow(tags, tag.getName(), "invalid tag");

		switch (tag.getType()) {
			case HttpTag.TYPE_OPEN:
				tagBuilder.doStart(jbs, tag, indent);
				tagStack.push(tag);
				return indent + 2;
			case HttpTag.TYPE_CLOSE:
				HttpTag existing = tagStack.pop();
				if (OH.ne(existing.getName(), tag.getName()))
					throw new RuntimeException("near char " + text.getCountRead() + ": closing tag mismatch: " + existing.getName() + " != " + tag.getName());
				tagBuilder.doEnd(jbs, tag, indent - 2);
				return indent - 2;
			case HttpTag.TYPE_SIMPLE:
				tagBuilder.doSimple(jbs, tag, indent);
				return indent;
			default:
				throw new RuntimeException("near char " + text.getCountRead() + ": unknown tag type: " + tag.getType());
		}
	}

	static private void appendText(JspBuilderSession jbs, String text, int indent) {
		for (int start = 0, end, len = text.length(); start < len; start = end) {
			end = Math.min(text.length(), start + SH.MAX_STRING_CONST_LENGTH);
			appendText(jbs, text, indent, start, end);
		}
	}
	static private void appendText(JspBuilderSession jbs, String text, int indent, int start, int end) {
		if (start != 0 || end != text.length())
			text = text.substring(start, end);
		if (SH.isnt(text))
			return;

		text = SH.replaceAll(text, '\\', "\\\\");
		text = SH.replaceAll(text, '\"', "\\\"");
		text = SH.replaceAll(text, '\r', "");
		text = SH.replaceAll(text, '\n', "\\r\\n\"+\n" + SH.repeat(' ', indent + 2) + "\"");
		SH.repeat(' ', indent, jbs.getBody());
		jbs.getBody().append("out.print(" + SH.NEWLINE);
		SH.repeat(' ', indent + 2, jbs.getBody());
		jbs.getBody().append("\"" + text + "\");" + SH.NEWLINE);
	}
	public CodeCompiler getCompiler() {
		return this.compiler;
	}
	public File getSourceFile(String fullClassName) {
		return this.compiler == null ? new File(fullClassName) : this.compiler.getSourceFile(fullClassName);
	}

}
