package com.f1.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.utils.CharReader;
import com.f1.utils.ContentType;
import com.f1.utils.EmptyCollection;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.SingletonIterable;
import com.f1.utils.structs.ArrayIterator;

public class HttpUtils {

	private static final int MAX_FILE_NAME_LENGTH = 206;
	static final private int[] SPACE_CLOSE_OR_EOF = new int[] { ' ', '>', '/', CharReader.EOF };
	private static Logger log = LH.get();
	public static final String CONTENT_SECURITY_POLICY = "Content-Security-Policy";
	public static final String CSP_STRICT_IMG = "default-src 'none'; img-src 'self';";
	public static final String CSP_STRICT_WEB = "default-src 'none'; script-src 'self'; style-src 'self'; img-src 'self';";
	public static final String CSP_UNSAFE_WEB = "default-src 'none'; script-src 'unsafe-inline' 'self'; style-src 'unsafe-inline' 'self'; img-src 'self'; ";
	public static final String CSP_NONE = "default-src 'none';";
	public static final String CSP_DEFAULT = "img-src 'self' https://*.mapbox.com data: w3.org/svg/2000; default-src https://*.mapbox.com 'self' 'unsafe-inline' 'unsafe-eval' blob:;font-src 'self' data:";

	static public HttpTag parseTag(CharReader scr) {
		scr.expect('<');
		byte type = HttpTag.TYPE_OPEN;
		if (scr.peak() == '/') {
			scr.expect('/');
			type = HttpTag.TYPE_CLOSE;
		}
		StringBuilder sb = new StringBuilder();
		scr.readUntilAny(SPACE_CLOSE_OR_EOF, sb);
		String name = sb.toString();
		Map<String, String> attributes = new HashMap<String, String>();
		while (true) {
			scr.skip(' ');
			switch (scr.peakOrEof()) {
				case '>':
					scr.expect('>');
					return new HttpTag(type, name, attributes);
				case '/':
					scr.expect('/');
					scr.expect('>');
					if (type != HttpTag.TYPE_OPEN)
						throw new RuntimeException("not expecting: />");
					return new HttpTag(HttpTag.TYPE_SIMPLE, name, attributes);
			}
			scr.readUntil('=', SH.clear(sb));
			final String key = SH.trim(sb);
			scr.expect('=');
			scr.skip(' ');
			scr.expect('\"');
			scr.readUntilSkipEscaped('\"', '\\', SH.clear(sb));
			scr.expect('\"');
			final String value = SH.trim(sb);
			attributes.put(key, value);
			scr.skip(' ');
		}

	}

	public static void parseCookie(String cookie, Map<String, String> sink) {
		SH.splitToMapWithTrim(sink, ';', '=', cookie);
	}

	public static String parseContent(String text, Map<String, String> sink) {
		int i = text.indexOf(';');
		if (i == -1)
			return text;
		SH.splitToMapWithTrim(sink, ';', '=', text.substring(i + 1));
		return text.substring(0, i).trim();
	}
	public static Map<String, String> splitToMapWithTrim(Map<String, String> sink, char delim, char associator, String text) {
		if (text == null || text.length() == 0)
			return sink;
		for (int last = 0;; last++) {
			int i = text.indexOf(associator, last);
			if (i == -1) {
				LH.warning(log, "trailing text after char " + last + ": " + text);
				return sink;
			}
			final String key = text.substring(last, i).trim();
			last = text.indexOf(delim, ++i);
			if (last == -1) {
				sink.put(key, text.substring(i).trim());
				return sink;
			} else
				sink.put(key, text.substring(i, last).trim());
		}
	}

	public static String getCanonical(String pwd, String url) {
		if (url.startsWith("/"))
			return IOH.getCanonical(url);
		if (!pwd.startsWith("/"))
			pwd = "/" + pwd;
		if (!pwd.endsWith("/"))
			pwd = SH.beforeLast(pwd, "/") + '/';

		return IOH.getCanonical(pwd + url);
	}

	// try request, then session, then server scope
	public Object getValue(HttpRequestResponse req, String key) {
		Object r = req.getAttributes().get(key);
		if (r == null) {
			final HttpSession session = req.getSession(false);
			if (session != null)
				r = session.getAttributes().get(key);
			if (r == null)
				r = req.getHttpServer().getAttributes().get(key);
		}
		return r;

	}

	public static Iterable<Object> toIterable(Object o) {
		if (o == null)
			return EmptyCollection.INSTANCE;
		else if (o instanceof Iterable)
			return (Iterable<Object>) o;
		else if (o.getClass().isArray())
			return new ArrayIterator<Object>((Object[]) o);
		else
			return new SingletonIterable<Object>(o);
	}

	static public boolean toBoolean(boolean o) {
		return o;
	}

	static public boolean toBoolean(Object o) {
		if (o == null)
			return false;
		else if (o instanceof Boolean)
			return ((Boolean) o).booleanValue();
		else if (o instanceof Number)
			return ((Number) o).doubleValue() != 0;
		else if (o instanceof CharSequence)
			return SH.is(o);
		else
			return toIterable(o).iterator().hasNext();
	}

	public static String buildUrl(boolean isSecure, String host, int port, String uri, String query) {
		return buildUrl(isSecure, host, port, uri, query, new StringBuilder()).toString();
	}

	public static StringBuilder buildUrl(boolean isSecure, String host, int port, String uri, String query, StringBuilder sink) {
		sink.append(isSecure ? "https://" : "http://").append(host);
		if (port > 0 && port != (isSecure ? 443 : 80))
			sink.append(':').append(port);
		sink.append(uri);
		if (SH.is(query))
			sink.append('?').append(query);
		return sink;
	}

	public static void respondWithFile(String fileName, byte[] data, HttpRequestResponse response) throws IOException {
		fileName = SH.replaceAll(fileName, '/', '_');
		fileName = SH.replaceAll(fileName, '\\', '_');
		fileName = SH.escape(fileName, '"', '\\');
		String extension = SH.afterLast(fileName, '.', "");
		final ContentType mimeType = ContentType.getTypeByFileExtension(extension, ContentType.BINARY);
		if (fileName.length() > 206) {
			String name = SH.beforeLast(fileName, '.', fileName);
			name = SH.substring(name, 0, Math.max(10, MAX_FILE_NAME_LENGTH - 1 - extension.length()));
			if (extension.length() != 0)
				fileName = name + "." + extension;
			else
				fileName = name;
		}
		response.setContentType(mimeType.getMimeType());
		response.putResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		response.getOutputStream().write(data);
	}

	public static String escapeHtml(CharSequence text) {
		return escapeHtml(text, new StringBuilder()).toString();
	}
	public static String escapeHtmlNewLineToBr(CharSequence text) {
		StringBuilder sb = new StringBuilder();
		escapeHtml(text, 0, text.length(), true, "<BR>", sb);
		return sb.toString();
	}
	public static StringBuilder escapeHtml(CharSequence text, StringBuilder sb) {
		escapeHtml(text, 0, text.length(), true, "\\n", sb);
		return sb;
	}
	public static StringBuilder escapeHtmlIncludeBackslash(CharSequence text, StringBuilder sb) {
		escapeHtml(text, 0, text.length(), true, "\\n", sb);
		return sb;
	}
	public static StringBuilder escapeHtml(CharSequence text, int start, int end, boolean includeBackslash, StringBuilder sb) {
		escapeHtml(text, start, end, includeBackslash, "\\n", sb);
		return sb;
	}

	public static <T extends Appendable> T escapeHtml(CharSequence text, int start, int end, boolean includeBackslash, String replaceNewLineWith, T sb) {
		try {
			for (int i = start; i < end; i++) {
				char c = text.charAt(i);
				switch (c) {
					case '\'':
						sb.append("&#39;");
						break;
					case '"':
						sb.append("&#34;");
						break;
					case '\\':
						if (includeBackslash) {
							sb.append("&#92;");
						} else {
							if (++i < end) {
								if (text.charAt(i) == '\\') {
									sb.append("&#92;");
								} else {
									sb.append(text.charAt(i));
								}
							}
						}
						break;
					case '\n':
						sb.append(replaceNewLineWith);
						break;
					case '\r':
						break;
					case ' ':
						sb.append("&nbsp;");
						break;
					case '\t':
						sb.append("&nbsp;&nbsp;");
						break;
					case '>':
						sb.append("&gt;");
						break;
					case '<':
						sb.append("&lt;");
						break;
					case '&':
						sb.append("&amp;");
						break;
					case 0:
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
					case 6:
					case 7:
					case 8:
						sb.append("&#191;");
						break;
					default:
						sb.append(c);
				}
			}
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
		return sb;
	}

	public static <T extends Appendable> T htmlToText(CharSequence text, int start, int end, T sb, boolean brToNewline) {
		boolean hadText = false;
		try {
			for (int i = start; i < end;) {
				char c = text.charAt(i++);
				switch (c) {
					case '<':
						if (brToNewline && SH.equalsIgnoreCase("br>", text, i)) {
							sb.append('\n');
							hadText = false;
							i += 3;
							continue;
						}
						tag: while (i < end) {
							c = text.charAt(i++);
							switch (c) {
								case '>':
									break tag;
								case '\'': {
									while (i < end) {
										c = text.charAt(i++);
										if (c == '\'')
											break;
										if (c == '\\')
											i++;
									}
									break;
								}
								case '\"': {
									while (i < end) {
										c = text.charAt(i++);
										if (c == '\"')
											break;
										if (c == '\\')
											i++;
									}
									break;
								}
							}
						}
						if (hadText) {
							sb.append(' ');
							hadText = false;
						}
						break;
					case '&':
						if (SH.equalsIgnoreCase("nbsp;", text, i)) {
							sb.append(' ');
							hadText = false;
							i += 5;
						} else if (SH.equalsIgnoreCase("gt;", text, i)) {
							sb.append('>');
							hadText = true;
							i += 3;
						} else if (SH.equalsIgnoreCase("lt;", text, i)) {
							sb.append('<');
							hadText = true;
							i += 3;
						} else if (SH.equalsIgnoreCase("amp;", text, i)) {
							sb.append('&');
							hadText = true;
							i += 3;
						} else {
							while (i < end)
								if (text.charAt(i++) == ';')
									break;
						}
						break;
					case ' ':
						hadText = false;
						sb.append(c);
						break;
					default:
						hadText = true;
						sb.append(c);

				}
			}
		} catch (IOException e) {
			throw OH.toRuntime(e);

		}
		return sb;
	}

	public static String getParamsAsString(Map<String, String> params) {
		if (params.isEmpty())
			return "";
		boolean first = true;
		StringBuilder sink = new StringBuilder();
		for (Entry<String, String> e : params.entrySet()) {
			if (first)
				first = false;
			else
				sink.append('&');
			SH.encodeUrl(e.getKey(), sink);
			sink.append('=');
			SH.encodeUrl(e.getValue(), sink);
		}
		return sink.toString();
	}

	//	public static void main(String a[]) {
	//		System.out.println(htmlToText("<this>test</that>", true));
	//		System.out.println(htmlToText("<this onscript='what!'>test", true));
	//		System.out.println(htmlToText("<this onscript='w\\'hat!'>test", true));
	//		System.out.println(htmlToText("<this onscript='w\\'h>at!'>test<BR>okay", true));
	//		System.out.println(htmlToText("<this onscript=\"w\\'h>at!\">test<BR>okay", true));
	//		System.out.println(htmlToText("<this onscript=\"w\\'h>at!\">test<BR>okay", true));
	//		System.out.println(htmlToText("<this onscript=\"w\\'h>at!\">test&nbsp;okay", true));
	//	}

}
