/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.codegen;

import java.util.List;
import com.f1.base.ObjectGenerator;
import com.f1.base.ObjectGeneratorForClass;

public interface CodeGenerator extends ObjectGenerator {

	public void setCodeCompiler(CodeCompiler codeCompiler);

	public CodeCompiler getCodeCompiler();

	public void addCodeTemplate(CodeTemplate template);

	public <T> ObjectGeneratorForClass<T> generateCode(Class<T> clazz) throws Exception;

	public List<ObjectGeneratorForClass> generateCode(List<Class> clazz) throws Exception;

}
