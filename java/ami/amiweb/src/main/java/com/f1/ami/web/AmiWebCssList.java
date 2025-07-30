package com.f1.ami.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.f1.utils.SH;
import com.f1.utils.css.CssParser.CssAtRule;
import com.f1.utils.css.CssParser.CssRuleset;
import com.f1.utils.css.CssParser.CssSelector;
import com.f1.utils.css.CssParser.CssStatement;
import com.f1.utils.structs.BasicMultiMap;

public class AmiWebCssList {
	final private Map<String, String> classNames = new TreeMap<String, String>();
	//	final private MapInMap<String, String, String> cssRules = new MapInMap<String, String, String>();//ex: keyframes -> slide -> @keyframes slide {...}
	final private BasicMultiMap.List<String, String> classNamesToCss = new BasicMultiMap.List<String, String>();

	private List<String> materializedCustomCss = null;//new ArrayList<String>();
	final private String selector;
	final private List<AmiWebCss> css = new ArrayList<AmiWebCss>();
	final private Map<String, String> colorVars = new HashMap<String, String>();

	public AmiWebCssList(String selector) {
		this.selector = selector;
	}

	public void addCss(AmiWebCss l) {
		this.css.add(l);
		this.classNames.putAll(l.getClassNames());
		this.classNamesToCss.putAllMulti(l.getClassNamesToCss());
		this.materializedCustomCss = null;
	}

	public List<String> getCustomCssMaterialized() {
		if (this.materializedCustomCss == null)
			this.materializedCustomCss = getMcss();
		return this.materializedCustomCss;

	}
	public Set<String> getClassNames() {
		return classNames.keySet();
	}
	public String getPrefixedClassName(String className) {
		if (className == null)
			return null;
		return classNames.get(className);
	}

	public List<String> getCssForClassName(String cn) {
		return this.classNamesToCss.get(cn);
	}

	public String getSelector() {
		return this.selector;
	}

	public List<String> getMcss() {
		BasicMultiMap.List<String, CssRuleset> classes2Rulesets = new BasicMultiMap.List<String, CssRuleset>();
		Set<String> atRuleNames = new HashSet<String>();
		for (AmiWebCss l : this.css)
			for (CssStatement cs : l.getCssStatement())
				if (cs instanceof CssRuleset) {
					CssRuleset rs = (CssRuleset) cs;
					for (CssSelector selector : rs.getSelectors())
						for (String clazz : selector.getClasses())
							classes2Rulesets.putMulti(clazz, rs);
				} else if (cs instanceof CssAtRule) {
					CssAtRule rs = (CssAtRule) cs;
					atRuleNames.add(SH.trim(rs.getArguments()));
				}
		final List<String> mcss = new ArrayList<String>();
		for (AmiWebCss l : this.css)
			for (CssStatement cs : l.getCssStatement()) {
				if (cs instanceof CssRuleset) {
					CssRuleset rs = (CssRuleset) cs;
					mcss.add(AmiWebCustomCssManager.toString(this.selector, rs, classes2Rulesets, this.colorVars, atRuleNames));
				}
			}
		for (AmiWebCss l : this.css)
			for (CssStatement cs : l.getCssStatement())
				if (cs instanceof CssAtRule) {
					CssAtRule rs = (CssAtRule) cs;
					mcss.add(rs.toString(0, new StringBuilder(), this.selector).toString());
				}
		return mcss;
	}

	public void setColorVars(Map<String, String> varColors) {
		this.colorVars.clear();
		this.colorVars.putAll(varColors);
	}

	public boolean referencesVar(String key) {
		for (AmiWebCss l : this.css)
			for (CssStatement cs : l.getCssStatement())
				if (cs.referencesVar(key))
					return true;
		return false;
	}

}
