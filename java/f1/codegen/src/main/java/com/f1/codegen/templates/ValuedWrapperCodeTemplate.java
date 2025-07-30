/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.codegen.templates;

import java.io.IOException;
import java.util.List;
import com.f1.base.Action;
import com.f1.base.Valued;
import com.f1.base.ValuedWrapper;
import com.f1.codegen.CodeableClass;
import com.f1.codegen.impl.StringTemplateCodeTemplate;
import com.f1.stringmaker.StringMakerSession;
import com.f1.utils.CH;
import com.f1.utils.IOH;

public class ValuedWrapperCodeTemplate extends StringTemplateCodeTemplate {
	public ValuedWrapperCodeTemplate() throws IOException {
		super(ValuedWrapper.class, IOH.readText(ValuedWrapperCodeTemplate.class, ".st"));
	}

	private List<Class> hierarchy = CH.l((Class) ValuedWrapper.class, Action.class);

	@Override
	public List<Class> getHierarchy() {
		return hierarchy;
	}

	@Override
	protected void prepareTemplate(StringMakerSession t, CodeableClass existing, CodeableClass srcClass, boolean isAbstract) {

		super.prepareTemplate(t, existing, srcClass, isAbstract);
		Class wrappedClass = find(srcClass.getInnerClass());
		t.pushValue("wrappedClass", wrappedClass.getName());
	}

	protected static Class find(Class c) {
		if (Valued.class.isAssignableFrom(c) && !ValuedWrapper.class.isAssignableFrom(c))
			return c;
		final Class[] interfaces = c.getInterfaces();
		for (Class i : interfaces)
			if (Valued.class.isAssignableFrom(i) && !ValuedWrapper.class.isAssignableFrom(i))
				return i;
		for (Class i : interfaces)
			if ((c = find(i)) != null)
				return c;
		return null;
	}
}
