/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.codegen.templates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.Action;
import com.f1.base.PartialMessage;
import com.f1.base.Valued;
import com.f1.base.ValuedListenable;
import com.f1.codegen.CodeTemplate;
import com.f1.codegen.impl.StringTemplateCodeTemplate;
import com.f1.utils.CH;
import com.f1.utils.IOH;

public class PartialMessageCodeTemplate extends StringTemplateCodeTemplate {

	public PartialMessageCodeTemplate() throws IOException {
		super(PartialMessage.class, IOH.readText(PartialMessageCodeTemplate.class, ".st"));
	}

	private List<Class> hierarchy = CH.l((Class) Valued.class, Action.class, ValuedListenable.class, PartialMessage.class);
	private List<Class> hierarchy2 = CH.l((Class) Valued.class, Action.class, PartialMessage.class);
	@Override
	public List<Class> getHierarchy() {
		throw new UnsupportedOperationException();
	}

	public List<CodeTemplate> getHierarchy(Set<Class> candidates, Map<Class, CodeTemplate> availableTemplates) {
		List<CodeTemplate> r = new ArrayList<CodeTemplate>();
		List<Class> h = availableTemplates.containsKey(PartialMessage.class) ? hierarchy : hierarchy2;
		for (Class c : h) {
			CodeTemplate t = CH.getOrThrow(availableTemplates, c);
			if (candidates.contains(c))
				r.add(0, t);
		}
		return r;
	}
}
