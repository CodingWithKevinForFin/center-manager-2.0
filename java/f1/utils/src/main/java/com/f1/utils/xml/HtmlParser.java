package com.f1.utils.xml;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.f1.utils.CH;
import com.f1.utils.CharReader;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.impl.CharSequenceHasher;
import com.f1.utils.impl.StringCharReader;

public class HtmlParser {
	private static int[] OPEN = StringCharReader.toIntsAndEof("<&'\"/\\" + SH.UNICODE_LEFT_DOUBLE_QUOTATION + SH.UNICODE_LEFT_SINGLE_QUOTATION);
	private static int[] SPC_GT_FS = StringCharReader.toIntsAndEof(" \n\r\t>/");
	private static int[] SPC_GT_FS_EQ = StringCharReader.toIntsAndEof(" \n\r\t>/=");
	private static int[] WHITE_SPACE = StringCharReader.toIntsAndEof(" \n\r\t");

	private static final Map<CharSequence, String> SPECIAL_HTML = new HasherMap<CharSequence, String>(CharSequenceHasher.INSTANCE);
	private static final Set<String> NO_CLOSE_TAGS = new HashSet<String>();
	private static final Set<String> AUTO_CLOSE_TAGS = new HashSet<String>();
	static {
		SPECIAL_HTML.put("NBSP", " ");
		SPECIAL_HTML.put("QUOT", "\"");
		SPECIAL_HTML.put("AMP", "&");
		SPECIAL_HTML.put("GT", ">");
		CH.s(NO_CLOSE_TAGS, "IMG", "INPUT", "BR", "HR", "FRAME", "AREA", "BASE", "BASEFONT", "COL", "ISINDEX", "LINK", "META", "PARAM");
		CH.s(AUTO_CLOSE_TAGS, "HTML", "HEAD", "BODY", "P", "DT", "DD", "LI", "OPTION", "THEAD", "TH", "TBODY", "TR", "TD", "TFOOT", "COLGROUP");
	}
	private boolean includeStackTraces = true;

	public XmlElement parseHtml(String text) {
		return parseHtml(text, null);
	}
	public XmlElement parseHtml(String text, List<String> warningsSink) {
		XmlElement root = new XmlElement("");
		try {
			StringBuilder buf = new StringBuilder();
			StringBuilder out = new StringBuilder();
			StringCharReader scr = new StringCharReader(text);
			scr.setCaseInsensitive(true);
			Stack<XmlElement> stack = new Stack<XmlElement>();
			XmlElement el = root;

			boolean inScript = false;
			while (scr.peakOrEof() != CharReader.EOF) {
				switch (scr.readUntilAny(OPEN, out)) {
					case '\\': {
						out.append(scr.expect('\\')).append(scr.readChar());
						break;
					}
					case '/': {
						out.append(scr.expect('/'));
						if (inScript) {
							if (scr.peak() == '/') {
								out.append(scr.expect('/'));
								int from = out.length();
								int c = scr.readUntilAny(StringCharReader.NEWLINE, true, out);
								if (c != CharReader.EOF)
									out.append((char) c);
								if (out.indexOf("<!--", from) != -1) {//this is ugly, apparently html comments take precedence over javascript comments
									out.append(readUntilCloseComment(scr, out));
								} else if (out.indexOf("<![CDATA[", from) != -1) {
									if (-1 == scr.readUntilSequence("]]>".toCharArray(), out)) {
										if (-1 != scr.readUntilSequence("</script>".toCharArray(), out)) {
											scr.expectSequence("</script>");
											out.append("</script>");
										}
									} else {
										scr.expectSequence("]]>");
										out.append("]]>");
									}
								}
							} else if (scr.peak() == '*') {
								out.append(scr.expect('*'));
								scr.readUntilSequence("*/".toCharArray(), out);
								scr.expectSequence("*/");
								out.append("*/");
							} else if (out.length() > 1 && out.charAt(out.length() - 2) == '(') {//hacky, but handles regexes  /.../
								scr.readUntil('/', '\\', out);
								out.append(scr.expect('/'));
							}
						}
						break;
					}
					case '\'': {
						out.append(scr.expect('\''));
						if (inScript) {
							scr.readUntil('\'', '\\', out);
							out.append(scr.expect('\''));
						}
						break;
					}
					case '"': {
						out.append(scr.expect('"'));
						if (inScript) {
							scr.readUntil('"', '\\', out);
							out.append(scr.expect('"'));
						}
						break;
					}
					case SH.UNICODE_LEFT_SINGLE_QUOTATION: {
						out.append(scr.expect(SH.UNICODE_LEFT_SINGLE_QUOTATION));
						if (inScript) {
							scr.readUntil(SH.UNICODE_RIGHT_SINGLE_QUOTATION, '\\', out);
							out.append(scr.expect(SH.UNICODE_RIGHT_SINGLE_QUOTATION));
						}
						break;
					}
					case SH.UNICODE_LEFT_DOUBLE_QUOTATION: {
						out.append(scr.expect(SH.UNICODE_LEFT_DOUBLE_QUOTATION));
						if (inScript) {
							scr.readUntil(SH.UNICODE_RIGHT_DOUBLE_QUOTATION, '\\', out);
							out.append(scr.expect(SH.UNICODE_RIGHT_DOUBLE_QUOTATION));
						}
						break;
					}
					case '&': {
						scr.expect('&');
						SH.clear(buf);
						if (!inScript) {
							if (scr.peak() == '#') {
								scr.expect('#');
								if (SH.equalsIgnoreCase(scr.peak(), 'x')) {
									scr.expect('x');
									scr.readWhileAny(StringCharReader.ALPHA_NUM, buf);
									out.append((char) SH.parseInt(buf, 16));
								} else {
									scr.readWhileAny(StringCharReader.ALPHA_NUM, buf);
									out.append((char) SH.parseInt(buf));
								}
								if (scr.peak() == ';')
									scr.expect(';');
							} else {
								scr.readWhileAny(StringCharReader.ALPHA_NUM, buf);
								if (scr.peak() == ';') {
									scr.expect(';');
									SH.uppercaseInplace(buf);
									String val = SPECIAL_HTML.get(buf);
									if (val != null)
										out.append(val);
									else {
										if (warningsSink != null)
											warningsSink.add("Near byte " + scr.getCountRead() + ": Special char not recognized: &" + buf + ';');
										out.append('&').append(buf).append(';');
									}
								} else {
									out.append('&').append(buf);
								}
							}
						}
						SH.clear(buf);
						break;
					}
					case '<': {
						scr.expect('<');
						//reading a tag like: <name key=value key=value > 
						if (inScript) {
							if (!scr.peakSequence("/script".toCharArray()) && !scr.peakSequence("!--") && !scr.peakSequence("![CDATA[")) { //TODO:const
								out.append('<');
								break;
							}
						}
						if (SH.is(out)) {
							el.addChild(new XmlText(out.toString()));
						}
						SH.clear(out);
						if (scr.peakSequence("!--")) {
							scr.skipChars(3);
							readUntilCloseComment(scr, out);
							XmlElement comment = new XmlElement("!--");
							comment.addChild(new XmlText(SH.toStringAndClear(out)));
							el.addChild(comment);
							break;
						} else if (scr.peakSequence("![CDATA[")) {
							scr.skipChars(8);
							scr.readUntilSequence("]]>".toCharArray(), out);
							scr.skipChars(3);
							XmlElement comment = new XmlElement("![CDATA[");
							comment.addChild(new XmlText(SH.toStringAndClear(out)));
							el.addChild(comment);
							break;
						}
						boolean forceClose = false;
						boolean close;
						if (scr.peakOrEof() == '/') {
							close = true;
							scr.expect('/');
						} else
							close = false;
						scr.skipAny(WHITE_SPACE);
						final int t = scr.readUntilAny(SPC_GT_FS, out);
						if (t == '/') {
							scr.expect('/');
							scr.readUntil('>', null);
							close = true;
						}
						final String name;
						if (!close) {
							SH.uppercaseInplace(out);
							name = out.toString();
							int popCount = 0;
							XmlElement child = new XmlElement(name);
							stack.push(el);
							if (AUTO_CLOSE_TAGS.contains(name)) {
								for (int i = stack.size() - 1; i > 1; i--) {
									XmlElement e = stack.get(i);
									if (name.equals(e.getName())) {
										CH.removeAll(stack, i, stack.size() - i);
										el = stack.pop();
										break;
									}
								}
							} else if (NO_CLOSE_TAGS.contains(name)) {
								forceClose = true;
							}
							el.addChild(child);
							el = child;
						} else {
							SH.uppercaseInplace(out);
							while (el != root && !SH.equals(el.getName(), out))
								el = stack.pop();
							name = out.toString();
						}
						SH.clear(out);
						if (t == '>') {
							scr.expect('>');
							if (close && el != root)
								el = stack.pop();
						} else {
							phrase: for (;;) {
								scr.skipAny(WHITE_SPACE);
								String key;
								switch (scr.readUntilAny(SPC_GT_FS_EQ, out)) {
									case '=':
										scr.expect('=');
										SH.uppercaseInplace(out);
										key = SH.toStringAndClear(out);
										break;
									case '/':
										scr.expect('/');
										scr.readUntil('>', null);
										scr.expect('>');
										if (!close) {
											if ("SCRIPT".equals(name) && !inScript && el.getAttribute("SRC") == null) {
												if (warningsSink != null)
													warningsSink.add("Near byte " + scr.getCountRead() + ": SCRIPT tag closed w/o src attribute, not closing");
												break phrase;
											}
											el = stack.pop();
										} else if (forceClose)
											el = stack.pop();
										SH.clear(out);
										break phrase;
									case '>':
										if (warningsSink != null)
											if (SH.is(out))
												warningsSink.add("Near byte " + scr.getCountRead() + ": Trailing text: " + out + ';');
										scr.expect('>');
										SH.clear(out);
										if (forceClose)
											el = stack.pop();

										break phrase;
									default:
										scr.skipAny(WHITE_SPACE);
										if (scr.peak() == '=') {
											SH.uppercaseInplace(out);
											key = SH.toStringAndClear(out);
											scr.expect('=');
											break;
										} else {
											continue;
										}
								}

								//just read an equals at this point...
								scr.skipAny(WHITE_SPACE);
								switch (scr.peak()) {
									case '"': {
										scr.expect('"');
										scr.readUntil('"', '\\', out);
										scr.expect('"');
										el.addAttributeNotStrict(key, SH.toStringAndClear(out));
										break;
									}
									case '\'': {
										scr.expect('\'');
										scr.readUntil('\'', '\\', out);
										scr.expect('\'');
										el.addAttributeNotStrict(key, SH.toStringAndClear(out));
										break;
									}
									default:
										scr.readUntilAny(SPC_GT_FS, out);
										el.addAttributeNotStrict(key, SH.toStringAndClear(out));
										continue phrase;
								}
							}
						}
						if ("SCRIPT".equals(name)) {
							if (close)
								inScript = false;
							else
								inScript = true;
						}

					}
				}
			}
			if (SH.is(out)) {
				root.addChild(new XmlText(out.toString()));
			}
		} catch (RuntimeException e) {
			if (warningsSink != null) {
				if (includeStackTraces)
					warningsSink.add(SH.printStackTrace(e));
				else
					warningsSink.add("Exception " + e.getClass().getSimpleName() + " Found: " + e.getMessage());
			} else
				throw e;
		}

		return root;
	}
	private String readUntilCloseComment(StringCharReader scr, StringBuilder out) {
		for (;;) {
			if (scr.readUntilSequence("--".toCharArray(), out) == -1)
				return "";
			if (scr.peakSequence("-->")) {
				scr.expectSequence("-->");
				return "-->";
			} else if (scr.peakSequence("--!>")) {
				scr.expectSequence("--!>");
				return "--!>";
			} else {
				scr.skipChars(1);
				out.append("-");
			}

		}
	}
	public boolean getIncludeStackTraces() {
		return includeStackTraces;
	}
	public void setIncludeStackTraces(boolean includeStackTraces) {
		this.includeStackTraces = includeStackTraces;
	}

	public static void main(String a[]) {
		String txt = "Is there a way to extend out the <b style=\"background-color:yellow;\"><u>area</u></b> on a line chart so that it retains its previous y-axis level, out beyond the last point on the x-axis?";
		XmlElement html = new HtmlParser().parseHtml(txt);
		System.out.println(html);
	}

}
