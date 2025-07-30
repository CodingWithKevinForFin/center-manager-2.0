package com.f1.ami.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.utils.AH;
import com.f1.utils.OH;
import com.f1.utils.css.CssParser;
import com.f1.utils.css.CssParser.CssAtRule;
import com.f1.utils.css.CssParser.CssRuleset;
import com.f1.utils.css.CssParser.CssSelector;
import com.f1.utils.css.CssParser.CssStatement;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.MapInMap;

public class AmiWebCss {

	private String customCss = "";

	final private Map<String, String> classNames = new TreeMap<String, String>();
	final private BasicMultiMap.List<String, String> classNamesToCss = new BasicMultiMap.List<String, String>();
	final private MapInMap<String, String, String> cssRules = new MapInMap<String, String, String>();//ex: keyframes -> slide -> @keyframes slide {...}

	final private AmiWebCustomCssManager manager;

	//	private String selector;

	private List<CssStatement> cssStatements = new ArrayList<CssStatement>();

	public AmiWebCss(AmiWebCustomCssManager manager) {
		OH.assertNotNull(manager);
		this.manager = manager;
		this.manager.flagNeedsRebuild();
	}

	private void clear() {
		this.cssStatements.clear();
		this.classNamesToCss.clear();
		this.classNames.clear();
		this.cssRules.clear();
	}

	public boolean setCustomCssForce(String customCss) {
		try {
			setCustomCss(customCss);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void setCustomCss(String customCss, AmiDebugManager debugManager) {
		if (customCss == null)
			customCss = "";
		try {
			this.setCustomCss(customCss);
		} catch (Exception e) {
			if (debugManager != null && debugManager.shouldDebug(AmiDebugMessage.SEVERITY_WARNING))
				debugManager.addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_WARNING, AmiDebugMessage.TYPE_LAYOUT, this.manager.getService().getAri(), null,
						"Custom CSS is not valid", null, e));
		}
	}
	private void setCustomCss(String customCss) {
		if (customCss == null)
			customCss = "";
		if (OH.eq(customCss, this.customCss))
			return;
		clear();
		MapInMap<String, String, String> cssRules = new MapInMap<String, String, String>();
		List<CssStatement> css = CssParser.parseCss(customCss);
		Map<String, String> classNames = new HashMap<String, String>();
		BasicMultiMap.List<String, String> classNamesToCss = new BasicMultiMap.List<String, String>();
		for (CssStatement cs : css) {
			if (cs instanceof CssRuleset) {
				CssRuleset rs = (CssRuleset) cs;
				String cssString = rs.toString();
				CssSelector[] selectors = rs.getSelectors();
				if (AH.isEmpty(selectors))
					throw new ExpressionParserException(customCss, cs.getPosition(), "CSS Ruleset selector is missing class");
				for (CssSelector selector : selectors) {
					String[] classes = selector.getClasses();
					if (AH.isEmpty(classes))
						throw new ExpressionParserException(customCss, selector.getPosition(), "CSS Ruleset selector is missing class");
					for (int i = 0; i < classes.length; i++) {
						String clazz = classes[i];
						String renamed = AmiWebCustomCssManager.prefix(clazz);
						classNames.put(clazz, renamed);
						classNamesToCss.putMulti(clazz, cssString);
					}
				}
			} else if (cs instanceof CssAtRule) {
				CssAtRule car = (CssAtRule) cs;
				if (car.getArguments().startsWith("__reservedami_"))
					throw new ExpressionParserException(customCss, cs.getPosition(), "at rule name is reserved: " + car.getRule());
				if (OH.ne("keyframes", car.getRule()))
					throw new ExpressionParserException(customCss, cs.getPosition(), "at rule not supported: " + car.getRule());
				String s = car.toString();
				cssRules.putMulti(car.getRule(), car.getArguments(), s);
			} else
				throw new ExpressionParserException(customCss, cs.getPosition(), "CSS not supported");
		}
		this.cssStatements = css;
		this.customCss = customCss;
		this.classNamesToCss.putAll(classNamesToCss);
		this.classNames.putAll(classNames);
		this.cssRules.putAll(cssRules);
		//		this.mcss.addAll(mcss);
		this.manager.flagNeedsRebuild();
	}

	public String getCustomCss() {
		return this.customCss;
	}
	public Map<String, String> getClassNames() {
		return classNames;
	}

	public BasicMultiMap.List<String, String> getClassNamesToCss() {
		return classNamesToCss;
	}

	public List<String> getCssForClassName(String cn) {
		return this.classNamesToCss.get(cn);
	}

	public String getPrefixedClassName(String className) {
		if (className == null)
			return null;
		return classNames.get(className);
	}

	public List<CssStatement> getCssStatement() {
		return this.cssStatements;
	}

	public MapInMap<String, String, String> getCssRules() {
		return this.cssRules;
	}

}
