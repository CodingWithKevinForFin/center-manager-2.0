/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.codegen.templates;

import java.io.IOException;
import java.util.List;

import com.f1.base.Ackable;
import com.f1.base.Lockable;
import com.f1.base.Valued;
import com.f1.codegen.CodeableClass;
import com.f1.codegen.impl.StringTemplateCodeTemplate;
import com.f1.stringmaker.StringMakerSession;
import com.f1.utils.CH;
import com.f1.utils.IOH;

public class ValuedCodeTemplate extends StringTemplateCodeTemplate {

	public ValuedCodeTemplate() throws IOException {
		super(Valued.class, IOH.readText(ValuedCodeTemplate.class, ".st"));
	}

	private List<Class> hierarchy = CH.l((Class) Valued.class);

	@Override
	public List<Class> getHierarchy() {
		return hierarchy;
	}

	@Override
	protected void prepareTemplate(StringMakerSession t, CodeableClass existing, CodeableClass srcClass, boolean isAbstract) {
		super.prepareTemplate(t, existing, srcClass, isAbstract);
		t.pushValue("isLockable", Lockable.class.isAssignableFrom(srcClass.getInnerClass()));
		t.pushValue("isAckable", Ackable.class.isAssignableFrom(srcClass.getInnerClass()));
	}
}
