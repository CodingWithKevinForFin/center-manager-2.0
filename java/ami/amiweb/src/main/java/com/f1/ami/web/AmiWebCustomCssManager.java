package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.ami.web.style.AmiWebStyle;
import com.f1.ami.web.style.AmiWebStyleManager;
import com.f1.suite.web.JsFunction;
import com.f1.utils.AH;
import com.f1.utils.FastPrintStream;
import com.f1.utils.SH;
import com.f1.utils.css.CssParser.CssAttribute;
import com.f1.utils.css.CssParser.CssPseudoClass;
import com.f1.utils.css.CssParser.CssRuleset;
import com.f1.utils.css.CssParser.CssSelector;
import com.f1.utils.impl.BasicCharMatcher;
import com.f1.utils.impl.CharMatcher;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.json.JsonBuilder;
import com.f1.utils.structs.BasicMultiMap;

public class AmiWebCustomCssManager {

	public static final CharMatcher VAR = new BasicCharMatcher("$A-Za-z0-9_", false);
	public static final String EXTENDS_CSS = "extends-css";
	public static final String PUBLIC = "public_";
	public static final String PREFIX = "__custami_";

	final private HashMap<String, AmiWebCssList> syleId2list = new HashMap<String, AmiWebCssList>();
	final private AmiWebService service;
	private boolean pendingAjax;
	private boolean needsRebuild;

	public AmiWebCustomCssManager(AmiWebService service) {
		this.service = service;
	}

	public boolean hasPendingAjax() {
		return this.pendingAjax;
	}
	public void drainPendingAjax() {
		build();
		callJs();
		this.pendingAjax = false;
	}

	public static String toString(String selector, CssRuleset rs, BasicMultiMap.List<String, CssRuleset> classes2Rulesets, Map<String, String> varColors, Set<String> atRules) {
		return toString(selector, 0, new StringBuilder(), rs, classes2Rulesets, varColors, atRules).toString();
	}

	private void callJs() {
		JsFunction jsf = new JsFunction(this.service.getPortletManager().getPendingJs(), null, "amiSetCustomCss");
		createJson(jsf);
	}

	public void callJs(FastPrintStream out) {
		build();
		StringBuilder sb = new StringBuilder();
		JsFunction jsf = new JsFunction(sb, null, "amiSetCustomCssForWindow");
		jsf.addParam("window");
		createJson(jsf);
		out.append(sb);
		this.pendingAjax = false;

	}

	private void createJson(JsFunction jsf) {
		JsonBuilder json = jsf.startJson();
		json.startList();
		for (AmiWebCssList i : this.syleId2list.values())
			for (String s : i.getCustomCssMaterialized())
				json.addEntryQuoted(s);
		json.endList();
		json.end();
		jsf.end();
	}

	private void build() {
		if (!this.needsRebuild)
			return;
		clear();
		List<AmiWebCss> t = new ArrayList<AmiWebCss>();
		AmiWebStyleManager styleManager = this.service.getStyleManager();
		for (String s : this.service.getLayoutFilesManager().getFullAliasesByPriority())
			t.add(this.service.getLayoutFilesManager().getLayoutByFullAlias(s).getCss());
		for (String i : styleManager.getStyleIds()) {
			AmiWebCssList l = new AmiWebCssList(i);
			for (AmiWebCss css : t)
				l.addCss(css);
			Map<String, String> m = new HashMap<String, String>();
			Iterator<Entry<String, String>> iter = styleManager.getStyleById(i).getVarValues().iterator();
			while (iter.hasNext()) {
				Entry<String, String> j = iter.next();
				m.put(j.getKey(), j.getValue());
			}
			l.setColorVars(m);

			//TODO: I think we need to start at the parent and walk down so children can override paretns 
			while (i != null) {
				AmiWebStyle s = styleManager.getStyleById(i);
				if (s == null)
					break;
				l.addCss(s.getCss());
				i = s.getParentStyle();
			}
			this.syleId2list.put(l.getSelector(), l);
		}
		this.needsRebuild = false;
	}

	static private StringBuilder toString(StringBuilder sink, CssPseudoClass t) {
		sink.append(':').append(t.getElement());
		if (t.getArgument() != null) {
			sink.append('(');
			t.getArgument().toString(sink);
			sink.append(')');
		}
		return sink;
	}

	static private StringBuilder toString(StringBuilder sink, CssAttribute t) {
		sink.append('[');
		sink.append(t.getAttribute());
		if (t.getOperator() != null)
			sink.append(t.getOperator());
		if (t.getValue() != null)
			SH.quoteToJavaConst('"', t.getValue(), sink);
		return sink.append(']');
	}
	static private StringBuilder toString(String selector, StringBuilder sink, CssSelector t) {
		if (t.getElement() != null)
			sink.append(t.getElement());
		if (AH.isntEmpty(t.getClasses()))
			for (String clazz : t.getClasses()) {
				if (selector != null)
					sink.append('.').append(selector).append(' ');
				sink.append('.');
				prefix(clazz, sink);
			}
		if (t.getId() != null)
			sink.append('#').append(t.getId());
		for (CssAttribute attribute : t.getAttributes())
			toString(sink, attribute);
		for (CssPseudoClass pc : t.getPseudoClasses())
			toString(sink, pc);
		if (t.getPseudoElement() != null)
			sink.append("::").append(t.getPseudoElement());
		if (t.getCombinatorOperation() != null)
			sink.append(t.getCombinatorOperation());
		if (t.getCombinatorSelector() != null)
			toString(selector, sink, t.getCombinatorSelector());
		return sink;
	}
	public static void prefix(String clazz, StringBuilder sink) {
		if (!SH.startsWith(clazz, PUBLIC))
			sink.append(PREFIX);
		sink.append(clazz);
	}
	public static String prefix(String clazz) {
		if (!SH.startsWith(clazz, PUBLIC))
			return PREFIX + clazz;
		return clazz;
	}

	static private StringBuilder toString(String selector, int indent, StringBuilder sink, CssRuleset t, BasicMultiMap.List<String, CssRuleset> classes2Rulesets,
			Map<String, String> varColors, Set<String> atRules) {
		SH.repeat(' ', indent * 2, sink);
		boolean first = true;
		for (CssSelector i : t.getSelectors()) {
			if (first)
				first = false;
			else
				sink.append(", ");
			toString(selector, sink, i);
		}
		sink.append(" {\n");
		toDeclarationString(selector, indent, sink, t, classes2Rulesets, varColors, atRules, new HashSet<String>());
		SH.repeat(' ', indent * 2, sink);
		sink.append("}\n");
		return sink;
	}

	static private void toDeclarationString(String selector, int indent, StringBuilder sink, CssRuleset t, BasicMultiMap.List<String, CssRuleset> classes2Rulesets,
			Map<String, String> varColors, Set<String> atRules, Set<String> visited) {
		for (Entry<String, String> e : t.getDeclaration().entrySet()) {
			if (EXTENDS_CSS.equals(e.getKey())) {
				for (String s : SH.split(",", e.getValue())) {
					s = SH.trim(s);
					if (visited.add(s)) {
						List<CssRuleset> existing = classes2Rulesets.get(SH.trim(s));
						if (existing != null)
							for (CssRuleset i : existing)
								toDeclarationString(selector, indent, sink, i, classes2Rulesets, varColors, atRules, visited);
					}
				}
			} else {
				SH.repeat(' ', indent * 2, sink);
				sink.append("  ").append(e.getKey()).append(": ");
				applyVars(e.getValue(), sink, selector, varColors, atRules);

				if (!SH.endsWithIgnoreCase(e.getValue(), "!important"))
					sink.append(" !important");
				sink.append(";\n");
			}
		}
	}

	private static StringBuilder applyVars(String value, StringBuilder sink, String selector, Map<String, String> varColors, Set<String> atRules) {
		if (atRules.isEmpty() && (varColors.isEmpty() || value.indexOf('$') == -1))
			return sink.append(value);
		StringBuilder tmp = new StringBuilder();
		StringCharReader scr = new StringCharReader(value);
		while (!scr.isEof()) {
			scr.readUntilAny(VAR, true, sink);
			if (scr.isEof())
				break;
			tmp.setLength(0);
			scr.readWhileAny(VAR, tmp);
			String var = tmp.toString();
			if (varColors.containsKey(var))
				sink.append(varColors.get(var));
			else if (atRules.contains(var))
				sink.append(selector).append('_').append(var);
			else
				sink.append(var);
		}
		return sink;

	}

	public void clear() {
		this.syleId2list.clear();
	}

	public Set<String> getClassNames(String styleId) {
		AmiWebCssList t = this.syleId2list.get(styleId);
		return t == null ? Collections.EMPTY_SET : t.getClassNames();
	}

	public String getCssForClassName(String styleId, String cn) {
		AmiWebCssList t = this.syleId2list.get(styleId);
		if (t == null)
			return null;
		List<String> r = t.getCssForClassName(cn);
		return r == null ? null : SH.join('\n', r, new StringBuilder()).append('\n').toString();
	}

	public String getPrefixedClassName(String styleId, String className) {
		AmiWebCssList t = this.syleId2list.get(styleId);
		return t == null ? null : t.getPrefixedClassName(className);
	}

	public void flagNeedsRebuild() {
		this.needsRebuild = true;
		this.pendingAjax = true;
	}

	public AmiWebService getService() {
		return this.service;
	}

	public void onVarChanged(String styleId, String key) {
		if (this.needsRebuild)
			return;
		AmiWebCssList t = this.syleId2list.get(styleId);
		if (t != null && t.referencesVar(key))
			flagNeedsRebuild();
	}

}
