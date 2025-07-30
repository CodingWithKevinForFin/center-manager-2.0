/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.codegen;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CodeTemplate {

	public Class getType();

	public String createCode(CodeableClass existing, CodeableClass srcClass, boolean isAbstract);

	public List<CodeTemplate> getHierarchy(Set<Class> candidates, Map<Class, CodeTemplate> availableTemplates);
}
