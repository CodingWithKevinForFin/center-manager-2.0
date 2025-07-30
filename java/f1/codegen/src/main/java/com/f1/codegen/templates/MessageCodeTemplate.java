/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.codegen.templates;

import java.io.IOException;
import java.util.List;
import com.f1.base.Action;
import com.f1.base.Valued;
import com.f1.codegen.impl.StringTemplateCodeTemplate;
import com.f1.utils.CH;
import com.f1.utils.IOH;

public class MessageCodeTemplate extends StringTemplateCodeTemplate {

	public MessageCodeTemplate() throws IOException {
		super(Action.class, IOH.readText(MessageCodeTemplate.class, ".st"));
	}

	private List<Class> hierarchy = CH.l((Class) Valued.class, Action.class);

	@Override
	public List<Class> getHierarchy() {
		return hierarchy;
	}

}
