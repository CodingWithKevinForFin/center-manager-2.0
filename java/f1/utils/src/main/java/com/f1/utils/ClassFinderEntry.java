package com.f1.utils;

import java.util.logging.Logger;
import com.f1.utils.mirror.ClassMirror;
import com.f1.utils.mirror.reflect.ReflectedClassMirror;

public class ClassFinderEntry {
	private static final Logger log = Logger.getLogger(ClassFinderEntry.class.getName());
	final private String classPath;
	final private ClassMirror classMirror;
	final private int type;
	private Class<?> classType;

	public ClassFinderEntry(int type, String classPath, ClassMirror classMirror) {
		this.classPath = classPath;
		this.classMirror = classMirror;
		this.type = type;
		if (classMirror instanceof ReflectedClassMirror)
			classType = ((ReflectedClassMirror) classMirror).getReflectedClass();
	}

	public int getType() {
		return type;
	}

	public String getClassPath() {
		return classPath;
	}

	public ClassMirror getClassMirror() {
		return classMirror;
	}

	@Override
	public String toString() {
		return (type == ClassFinder.TYPE_JAR ? "jar::" : "file::") + classPath + ":" + classMirror.getName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((classMirror == null) ? 0 : classMirror.hashCode());
		result = prime * result + ((classPath == null) ? 0 : classPath.hashCode());
		result = prime * result + type;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClassFinderEntry other = (ClassFinderEntry) obj;
		if (OH.ne(classMirror, other.classMirror))
			return false;
		if (OH.ne(classPath, other.classPath))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	public Class<?> getClassType() {
		if (classType != null)
			return classType;
		try {
			classType = Class.forName(classMirror.getName());
		} catch (Throwable e) {
		LH.severe(log,"Error instantiating class: " + this);
		}
		return classType;
	}

}
