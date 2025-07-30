/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import com.f1.utils.mirror.ClassMirror;
import com.f1.utils.mirror.reflect.ReflectedClassMirror;
import com.f1.utils.mirror.simple.SimpleClassMirror;

public class ClassFinder implements Iterable<ClassFinderEntry> {

	private static final Logger log = Logger.getLogger(ClassFinder.class.getName());
	public static final int TYPE_UNKNOWN = 0;
	public static final int TYPE_DIRECTORY = 1;
	public static final int TYPE_JAR = 2;
	public static final int TYPE_CLASS = 4;
	private static final String JAR_SUFFIX = ".jar";
	private static final String CLASS_SUFFIX = ".class";
	public static final int TYPE_ALL = TYPE_DIRECTORY | TYPE_JAR;

	private Set<ClassFinderEntry> entries;

	public ClassFinder(Collection<ClassFinderEntry> entries) {
		this.entries = new LinkedHashSet<ClassFinderEntry>(entries);
	}

	public ClassFinder() {
		this.entries = new LinkedHashSet<ClassFinderEntry>();
	}

	@Override
	public Iterator<ClassFinderEntry> iterator() {
		return entries.iterator();
	}

	public ClassFinder searchClasspath(int typeMask) throws IOException {
		return searchClasspath(typeMask, System.getProperty(EH.PROPERTY_JAVA_CLASS_PATH));
	}

	public ClassFinder searchClasspath(int typeMask, String classPaths) throws IOException {
		Set<ClassFinderEntry> entries = new LinkedHashSet<ClassFinderEntry>(this.entries);
		final List<String> paths = SH.splitToList(File.pathSeparator, classPaths);
		for (String path : paths) {
			final File f = new File(path);
			final int type = getType(f);
			if ((type & typeMask) != type)
				continue;
			switch (type) {
				case TYPE_JAR:
					searchJar(entries, f);
					break;
				case TYPE_DIRECTORY:
					searchDirectory(entries, f);
					break;
				case TYPE_UNKNOWN:
				case TYPE_CLASS:
					continue;
				default:
					throw new RuntimeException("unknown type: " + type);
			}

		}
		return new ClassFinder(entries);
	}

	private void searchDirectory(Set<ClassFinderEntry> entries, File path) {
		processDirectory(entries, path.toString(), "", path);
	}

	private void searchJar(Set<ClassFinderEntry> entries, File path) throws IOException {
		JarFile jarFile = new JarFile(path);
		try {
			for (JarEntry jarEntry : new Iterator2Iterable<JarEntry>(jarFile.entries())) {
				if (jarEntry.getName().endsWith(CLASS_SUFFIX)) {
					String className = toClassName(jarEntry.getName());
					entries.add(new ClassFinderEntry(TYPE_JAR, path.toString(), new SimpleClassMirror(className)));
				}
			}
		} finally {
			try {
				if (jarFile != null)
					jarFile.close();
			} catch (Exception e) {
				LH.warning(log, "Closing: ", jarFile, e);
			}
		}
	}

	private void processDirectory(Set<ClassFinderEntry> entries, String path, String packageName, File directory) {
		for (File f : IOH.listFiles(directory, true))
			if (f.isDirectory())
				processDirectory(entries, path, SH.path('.', packageName, f.getName()), f);
			else if (isClassFile(f.getName()))
				entries.add(new ClassFinderEntry(TYPE_DIRECTORY, path, new SimpleClassMirror(SH.path('.', packageName, SH.stripSuffix(f.getName(), CLASS_SUFFIX, true)))));
	}

	private static boolean isClassFile(String name) {
		return name.endsWith(CLASS_SUFFIX) && name.indexOf('$') == -1;
	}

	private static String toClassName(String name) {
		name = SH.stripSuffix(name, CLASS_SUFFIX, true);
		name = SH.replaceAll(name, '/', ".");
		name = SH.replaceAll(name, '\\', ".");
		return name;
	}

	private static int getType(File file) {
		if (!file.exists())
			return TYPE_UNKNOWN;
		if (file.isDirectory())
			return TYPE_DIRECTORY;
		String name = file.getName().toLowerCase();
		if (name.endsWith(JAR_SUFFIX))
			return TYPE_JAR;
		if (isClassFile(name))
			return TYPE_CLASS;
		return TYPE_UNKNOWN;
	}

	public ClassFinder filterByPackage(String pckName) {
		List<ClassFinderEntry> l = new ArrayList<ClassFinderEntry>(entries.size());
		for (ClassFinderEntry e : this) {
			if (e.getClassMirror().getName().startsWith(pckName))
				l.add(e);
		}
		return new ClassFinder(l);
	}

	public ClassFinder filterByExtends(ClassMirror clazzOrInterface) {
		List<ClassFinderEntry> l = new ArrayList<ClassFinderEntry>(entries.size());
		for (ClassFinderEntry e : this) {
			if (clazzOrInterface.isAssignableFrom(e.getClassMirror()))
				l.add(e);
		}
		return new ClassFinder(l);
	}

	public ClassFinder filterOutExtends(ClassMirror clazzOrInterface) {
		List<ClassFinderEntry> l = new ArrayList<ClassFinderEntry>(entries.size());
		for (ClassFinderEntry e : this) {
			if (!clazzOrInterface.isAssignableFrom(e.getClassMirror()))
				l.add(e);
		}
		return new ClassFinder(l);
	}

	public ClassFinder toReflected() {
		List<ClassFinderEntry> l = new ArrayList<ClassFinderEntry>(entries.size());
		for (ClassFinderEntry e : this) {
			try {
				l.add(new ClassFinderEntry(e.getType(), e.getClassPath(), ReflectedClassMirror.valueOf(e.getClassMirror())));
			} catch (Exception ex) {
				throw OH.toRuntime(ex);
			}
		}
		return new ClassFinder(l);
	}

	public Collection<ClassFinderEntry> getEntries() {
		return entries;
	}

	public Collection<ClassMirror> getClassMirrors() {
		ArrayList<ClassMirror> r = new ArrayList<ClassMirror>(entries.size());
		for (ClassFinderEntry e : entries)
			r.add(e.getClassMirror());
		return r;
	}

	public Collection<Class> getClasses() {
		ArrayList<Class> r = new ArrayList<Class>(entries.size());
		for (ClassFinderEntry e : entries) {
			ClassMirror cm = e.getClassMirror();
			if (cm instanceof ReflectedClassMirror)
				r.add(((ReflectedClassMirror) cm).getReflectedClass());
			else
				try {
					r.add(Class.forName(cm.getName()));
				} catch (ClassNotFoundException ex) {
					throw OH.toRuntime(ex);
				}
		}
		return r;
	}

}
