package com.f1.ami.amicommon.customobjects;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.container.ContainerTools;
import com.f1.utils.MH;
import com.f1.utils.RH;
import com.f1.utils.string.SqlExpressionParser;

public class AmiScriptClassPluginWrapper {

	final private Class clazz;
	final private List<AmiReflectionMemberMethod> methods;
	final private String name;
	final private List<AmiReflectionMemberConstructor> constructors;

	public AmiScriptClassPluginWrapper(ContainerTools tools, Class<?> clazz) {

		////////////////////////
		//Process Class
		this.clazz = clazz;
		AmiScriptAccessible annotation = clazz.getAnnotation(AmiScriptAccessible.class);
		if (annotation == null)
			throw new RuntimeException("Class must have @" + AmiScriptAccessible.class.getSimpleName() + " annotation: " + clazz.getName());
		String className = annotation.name();
		if (className == null)
			className = clazz.getSimpleName();
		verifyName(className, "className");
		this.name = className;

		////////////////////////
		//Process Methods
		List<Method> methods = RH.getMethods(clazz);
		this.methods = new ArrayList<AmiReflectionMemberMethod>();
		for (Method m : methods) {
			annotation = m.getAnnotation(AmiScriptAccessible.class);
			if (annotation == null)
				continue;
			String help = annotation.help();
			String[] paramNames = annotation.params();
			Class<?>[] paramTypes = m.getParameterTypes();
			String name = annotation.name();
			if ("".equals(name))
				name = m.getName();
			verifyName(name, "method");
			if (paramNames.length != 0) {
				if (paramNames.length != paramTypes.length)
					throw new RuntimeException("Param count mismatch in annotation on Method '" + m.getName() + " for '" + clazz.getName() + "'. Expecting " + paramTypes.length
							+ " but annotaion supplied " + paramNames.length);
			} else if (paramTypes.length > 0) {
				paramNames = new String[paramTypes.length];
				for (int i = 0; i < paramNames.length; i++)
					paramNames[i] = "arg" + i;
			}
			if (!MH.anyBits(m.getModifiers(), java.lang.reflect.Modifier.PUBLIC))
				throw new RuntimeException("Method '" + m.getName() + "' not public for class '" + clazz.getName() + "'");
			if (MH.anyBits(m.getModifiers(), java.lang.reflect.Modifier.ABSTRACT))
				throw new RuntimeException("Method '" + m.getName() + "' is abstract for class '" + clazz.getName() + "'");
			if (MH.anyBits(m.getModifiers(), java.lang.reflect.Modifier.STATIC))
				throw new RuntimeException("Method '" + m.getName() + "' is static for class '" + clazz.getName() + "'");
			this.methods.add(new AmiReflectionMemberMethod(m, name, paramNames, help, annotation.readonly()));
		}

		////////////////////////
		//Process constructors
		Constructor[] constructors = clazz.getConstructors();
		this.constructors = new ArrayList<AmiReflectionMemberConstructor>();
		for (Constructor<?> m : constructors) {
			annotation = m.getAnnotation(AmiScriptAccessible.class);
			String help;
			String[] paramNames;
			if (annotation == null) {
				//				if (m.getParameterTypes().length == 0 && constructors.length == 1) {
				//					help = "";
				//					paramNames = OH.EMPTY_STRING_ARRAY;
				//				} else
				continue;
			} else {
				help = annotation.help();
				paramNames = annotation.params();
			}
			Class<?>[] paramTypes = m.getParameterTypes();
			if (paramNames.length != 0) {
				if (paramNames.length != paramTypes.length)
					throw new RuntimeException("Param count mismatch in annotation on Constructor for '" + clazz.getName() + "'. Expecting " + paramTypes.length
							+ " but annotaion supplied " + paramNames.length);
			} else if (paramTypes.length > 0) {
				paramNames = new String[paramTypes.length];
				for (int i = 0; i < paramNames.length; i++)
					paramNames[i] = "arg" + i;
			}
			if (!MH.anyBits(m.getModifiers(), java.lang.reflect.Modifier.PUBLIC))
				throw new RuntimeException("Constructor '" + m.getName() + "' not public for class '" + clazz.getName() + "'");
			if (MH.anyBits(m.getModifiers(), java.lang.reflect.Modifier.ABSTRACT))
				throw new RuntimeException("Constructor '" + m.getName() + "' is abstract for class '" + clazz.getName() + "'");
			if (MH.anyBits(m.getModifiers(), java.lang.reflect.Modifier.STATIC))
				throw new RuntimeException("Constructor '" + m.getName() + "' is static for class '" + clazz.getName() + "'");
			this.constructors.add(new AmiReflectionMemberConstructor(m, null, paramNames, help, annotation != null && annotation.readonly()));
		}
		//		if (this.constructors.isEmpty())
		//			throw new RuntimeException("There must be a default constructor or at least one constructor must have an @" + AmiScriptAccessible.class.getSimpleName()
		//					+ " annotation for class: " + clazz.getName());

	}
	private void verifyName(String name, String description) {
		if (SqlExpressionParser.isReserved(name))
			throw new RuntimeException("Invalid " + description + " name for class '" + clazz.getName() + "': " + name + "(" + name.toUpperCase() + " is a reserved word)");
		if (!AmiUtils.isValidVariableName(name, false, false))
			throw new RuntimeException("Invalid " + description + " name for class '" + clazz.getName() + "': " + name);
	}
	public Class getClazz() {
		return clazz;
	}

	public List<AmiReflectionMemberMethod> getMethods() {
		return methods;
	}

	public String getName() {
		return name;
	}

	public List<AmiReflectionMemberConstructor> getConstructors() {
		return constructors;
	}
}
