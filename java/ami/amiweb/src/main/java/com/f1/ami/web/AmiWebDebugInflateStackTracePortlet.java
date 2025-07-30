package com.f1.ami.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.BasicPortletManager;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.utils.CH;
import com.f1.utils.ClassFinder;
import com.f1.utils.ClassFinderEntry;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.BasicMultiMap;

public class AmiWebDebugInflateStackTracePortlet extends GridPortlet implements FormPortletListener {

	private static final Logger log = LH.get();
	private FormPortlet inputForm;
	private FormPortlet outputForm;
	private FormPortletTextAreaField inField;
	private FormPortletTextAreaField outField;

	private static final BasicMultiMap.List<String, String> SHORTENED_NAMES = new BasicMultiMap.List<String, String>();
	static {
		ClassFinder cf;
		try {
			List<String> paths = new ArrayList<String>();
			CH.l(paths, EH.getJavaClassPath());
			CH.l(paths, EH.getBootClassPath());
			LH.info(log, "Building shortend classname mapping from ", paths.size(), " path(s)");

			for (int n = 0; n < paths.size(); n++) {
				String path = paths.get(n);
				cf = new ClassFinder().searchClasspath(ClassFinder.TYPE_ALL, path);
				StringBuilder sink = new StringBuilder();
				LH.info(log, "Working on '", path, "' with ", cf.getEntries().size(), " classes");
				for (ClassFinderEntry i : cf.getEntries()) {
					String name = i.getClassMirror().getSimpleName();
					BasicPortletManager.shortenClassname(name, sink);
					java.util.List<String> t = SHORTENED_NAMES.putMulti(SH.toStringAndClear(sink), i.getClassMirror().getName());
				}
			}
		} catch (IOException e) {
			LH.warning(log, "Could not init class: ", e);
		}

	}

	public AmiWebDebugInflateStackTracePortlet(PortletConfig config) {
		super(config);
		this.inputForm = new FormPortlet(generateConfig());
		this.outputForm = new FormPortlet(generateConfig());
		DividerPortlet div = new DividerPortlet(generateConfig(), false, inputForm, outputForm);
		div.setOffset(.2);
		addChild(div, 0, 0);
		this.inField = this.inputForm.addField(new FormPortletTextAreaField(""));
		this.outField = this.outputForm.addField(new FormPortletTextAreaField(""));
		inField.setLeftTopRightBottom(0, 0, 0, 0);
		outField.setLeftTopRightBottom(0, 0, 0, 0);
		RootPortlet root = (RootPortlet) getManager().getRoot();
		int width = (int) (root.getWidth() * 0.8);
		int height = (int) (root.getHeight() * 0.8);
		setSuggestedSize(width, height);
		this.inputForm.addFormPortletListener(this);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {

		if (field == this.inField) {
			String value = this.inField.getValue();
			String out = extracted(value, SHORTENED_NAMES);
			this.outField.setValue(out);
		}
	}
	private String extracted(String value, BasicMultiMap.List<String, String> shortenedNames) {
		StringBuilder sink = new StringBuilder();
		StringBuilder out = new StringBuilder();
		String clazz = "";
		outer: for (int pos = 0;;) {
			byte state = readToStateChange(value, pos, SH.clear(sink));
			pos += sink.length();
			switch (state) {
				case STATE_TEXT: {
					clazz = sink.toString();
					java.util.List<String> mapped = shortenedNames.get(clazz);
					if (CH.isntEmpty(mapped))
						clazz = toClassList(mapped);
					break;
				}
				case STATE_COLON:
					out.append(clazz).append('\n');
					break;
				case STATE_NUMBER:
					out.append("       ").append(clazz).append(':').append(sink).append('\n');
					break;
				case STATE_EOF:
					break outer;

			}
		}
		return out.toString();
	}
	private String toClassList(List<String> mapped) {
		if (mapped.size() == 1)
			return mapped.get(0);
		String r = null;
		for (String s : mapped)
			if (s.startsWith("com.f1.")) {
				if (r != null) {
					r = null;
					break;
				}
				r = s;
			}
		if (r != null)
			return r;
		return SH.join(",", mapped);
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	static final private byte STATE_COLON = 1;
	static final private byte STATE_COMMA = 2;
	static final private byte STATE_TEXT = 3;
	static final private byte STATE_NUMBER = 4;
	static final private byte STATE_EOF = 5;

	public static byte readToStateChange(String text, int position, StringBuilder sink) {
		if (position == text.length())
			return STATE_EOF;
		char c = text.charAt(position++);
		byte r = getState(c);
		sink.append(c);
		while (position < text.length()) {
			c = text.charAt(position++);
			if (getState(c) != r)
				return r;
			sink.append(c);
		}
		return r;
	}

	private static byte getState(char c) {
		switch (c) {
			case ':':
				return STATE_COLON;
			case ',':
				return STATE_COMMA;
			case ' ':
			case '\n':
			case '\r':
				return STATE_EOF;
			default:
				return OH.isBetween(c, '0', '9') ? STATE_NUMBER : STATE_TEXT;
		}
	}

}
