package com.f1.stringmaker;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.f1.stringmaker.impl.BasicStringMakerSession;
import com.f1.stringmaker.impl.StringMakerUtils;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.IntKeyMap.Node;

public class StringTranslator {

	private Pattern pattern;
	private Matcher matcher;
	private StringMaker maker;
	private HasherMap<String, Object> values = new HasherMap<String, Object>();
	private IntKeyMap<Map.Entry<String, Object>> entriesByNumber = new IntKeyMap<Map.Entry<String, Object>>();
	private StringMakerSession session;

	public StringTranslator(String regexPattern, String template) {
		this(regexPattern, 0, template);
	}
	public StringTranslator(String regexPattern, int flags, String template) {
		this.pattern = Pattern.compile(regexPattern, flags);
		this.matcher = this.pattern.matcher("");
		this.maker = StringMakerUtils.toMaker(template);
		Set<String> references = new HashSet<String>();
		this.maker.getReferences(references);
		for (String i : references) {
			int v = SH.parseInt(i);
			this.entriesByNumber.put(v, this.values.getOrCreateEntry(i));
		}
		this.session = new BasicStringMakerSession(values);
	}
	public String translate(CharSequence text) {
		this.matcher.reset(text);
		if (!this.matcher.matches())
			return null;
		for (Node<Entry<String, Object>> i : entriesByNumber)
			i.getValue().setValue(this.matcher.group(i.getIntKey()));
		this.session.getSink().setLength(0);
		this.maker.toString(this.session);
		return SH.toStringAndClear(this.session.getSink());
	}

	public static String translate(String text, String regexPattern, String template) {
		return new StringTranslator(regexPattern, template).translate(text);
	}

}
