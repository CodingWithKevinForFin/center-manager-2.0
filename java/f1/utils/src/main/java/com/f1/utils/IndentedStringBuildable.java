/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

public class IndentedStringBuildable extends BasicStringBuildable {

	@Override
	public BasicStringBuildable append(Object obj) {
		onAppend();
		return super.append(obj);
	}

	@Override
	public BasicStringBuildable append(String str) {
		onAppend();
		if (str != null && str.endsWith(SH.NEWLINE))
			newLine = true;
		return super.append(str);

	}

	@Override
	public BasicStringBuildable append(StringBuffer sb) {
		onAppend();
		return super.append(sb);
	}

	@Override
	public BasicStringBuildable append(CharSequence s) {
		onAppend();
		return super.append(s);
	}

	@Override
	public BasicStringBuildable append(CharSequence s, int start, int end) {
		onAppend();
		return super.append(s, start, end);
	}

	@Override
	public BasicStringBuildable append(char[] str) {
		onAppend();
		return super.append(str);
	}

	@Override
	public BasicStringBuildable append(char[] str, int offset, int len) {
		onAppend();
		return super.append(str, offset, len);
	}

	@Override
	public BasicStringBuildable append(boolean b) {
		onAppend();
		return super.append(b);
	}

	@Override
	public BasicStringBuildable append(char c) {
		onAppend();
		return super.append(c);
	}

	@Override
	public BasicStringBuildable append(int i) {
		onAppend();
		return super.append(i);
	}

	@Override
	public BasicStringBuildable append(long lng) {
		onAppend();
		return super.append(lng);
	}

	@Override
	public BasicStringBuildable append(float f) {
		onAppend();
		return super.append(f);
	}

	@Override
	public BasicStringBuildable append(double d) {
		onAppend();
		return super.append(d);
	}

	@Override
	public BasicStringBuildable appendCodePoint(int codePoint) {
		onAppend();
		return super.appendCodePoint(codePoint);
	}

	final private int tabSize;

	private int indent;

	private String indentString;

	private boolean newLine = true;;

	public IndentedStringBuildable(int tabSize) {
		this.tabSize = tabSize;
		this.indent = 0;
		this.indentString = "";
	}

	@Override
	public void appendNewLine() {
		onAppend();
		super.appendNewLine();
		newLine = true;
	}

	private void onAppend() {
		if (newLine)
			super.append(indentString);
		newLine = false;
	}

	public int getTabSize() {
		return tabSize;
	}

	public int getIndent() {
		return indent;
	}

	public int indent() {
		indent++;
		updateIndentString();
		return indent;
	}

	public int outdent() {
		if (indent == 0)
			throw new IllegalStateException("indent already at 0");
		indent--;
		updateIndentString();
		return indent;
	}

	public String getIndentString() {
		return indentString;
	}

	private void updateIndentString() {
		this.indentString = SH.repeat(' ', indent * tabSize);
	}

	public void dontIndent() {
		newLine = false;
	}

}
