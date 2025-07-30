/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.codegen.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.f1.codegen.CodeTemplate;
import com.f1.codegen.CodeableClass;
import com.f1.stringmaker.StringMaker;
import com.f1.stringmaker.StringMakerSession;
import com.f1.stringmaker.impl.BasicStringMakerSession;
import com.f1.stringmaker.impl.StringMakerUtils;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.SH;

public abstract class StringTemplateCodeTemplate implements CodeTemplate {

	final private Class type;
	final private StringMaker maker;

	public StringTemplateCodeTemplate(Class type, File template) {
		try {
			this.type = type;
			String text = ("//Coded by " + getClass().getSimpleName() + SH.NEWLINE + IOH.readText(template, true));
			this.maker = StringMakerUtils.toMaker(text);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public StringTemplateCodeTemplate(Class type, String template) {
		this.type = type;
		String text = ("//Coded by " + getClass().getSimpleName() + SH.NEWLINE + template);
		this.maker = StringMakerUtils.toMaker(text);
	}

	@Override
	public String createCode(CodeableClass existing, CodeableClass srcClass, boolean isAbstract) {
		try {
			BasicStringMakerSession t = new BasicStringMakerSession(new HashMap<String, Object>());
			prepareTemplate(t, existing, srcClass, isAbstract);
			maker.toString(t);
			return t.getSink().toString();
		} catch (Exception e) {
			throw new RuntimeException("error processing template for class " + getClass().getName(), e);
		}
	}

	protected void prepareTemplate(StringMakerSession t, CodeableClass existing, CodeableClass srcClass, boolean isAbstract)
	{
		t.pushValue("c", existing);
		t.pushValue("s", srcClass);
		t.pushValue("isAbstract", isAbstract);
	}

	@Override
	public Class getType() {
		return type;
	}

	abstract public List<Class> getHierarchy();

	@Override
	public List<CodeTemplate> getHierarchy(Set<Class> candidates, Map<Class, CodeTemplate> availableTemplates) {
		List<CodeTemplate> r = new ArrayList<CodeTemplate>();
		for (Class c : getHierarchy()) {
			CodeTemplate t = CH.getOrThrow(availableTemplates, c);
			if (candidates.contains(c))
				r.add(0, t);
		}
		return r;
	}

}
