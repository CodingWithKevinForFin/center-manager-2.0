/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.codegen.templates;

import java.io.IOException;
import java.util.List;
import com.f1.base.Action;
import com.f1.base.Valued;
import com.f1.base.ValuedListenable;
import com.f1.codegen.CodeableClass;
import com.f1.codegen.impl.StringTemplateCodeTemplate;
import com.f1.stringmaker.StringMakerSession;
import com.f1.utils.CH;
import com.f1.utils.IOH;

public class ValuedListenableCodeTemplate extends StringTemplateCodeTemplate {
	public ValuedListenableCodeTemplate() throws IOException {
		super(ValuedListenable.class, IOH.readText(ValuedListenableCodeTemplate.class, ".st"));
	}

	@Override
	protected void prepareTemplate(StringMakerSession t, CodeableClass existing, CodeableClass srcClass, boolean isAbstract) {
		super.prepareTemplate(t, existing, srcClass, isAbstract);
		Class wrappedClass = ValuedWrapperCodeTemplate.find(srcClass.getInnerClass());
		t.pushValue("wrappedClass", wrappedClass.getName());
	}

	private List<Class> hierarchy = CH.l((Class) Valued.class, Action.class, ValuedListenable.class);

	@Override
	public List<Class> getHierarchy() {
		return hierarchy;
	}
}
