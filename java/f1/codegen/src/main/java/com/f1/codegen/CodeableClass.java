/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.codegen;

import java.util.List;
import java.util.Map;

import com.f1.utils.HashOptimizer;
import com.f1.utils.structs.MultiMap;

public interface CodeableClass {

	public Class getInnerClass();

	public List<CodeableParam> getParams();

	public String getPackageName();

	public String getClassName();

	public String getExtendsClause();

	public String getImplementsClause();

	public String getSimpleClassName();

	public boolean getSupportsPids();

	public MultiMap<String, CodeableParam, List<CodeableParam>> getParamsByType();

	public Map<String, String> getClassAnnotations();

	public List<CodeableParam> getParamsByPid();

	public HashOptimizer<String, CodeableParam> getBuckets();

	public Class getOrigClass();

	public long getVid();

	String getVin();

	List<CodeableParam> getUnsupportedParams();

}
