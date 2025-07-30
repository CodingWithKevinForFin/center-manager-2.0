package com.f1.vortex.compiler;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

import com.f1.base.Valued;
import com.f1.codegen.impl.BasicCodeCompiler;
import com.f1.codegen.impl.BasicCodeGenerator;
import com.f1.utils.FastBufferedInputStream;
import com.f1.utils.MH;
import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.bytecode.ByteCodeClass;
import com.f1.utils.bytecode.ByteCodeConstClassRef;
import com.f1.utils.bytecode.ByteCodeConstants;
import com.f1.utils.bytecode.ByteCodeParser;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.HasherMap.Entry;

public class PreAutocoder {

	public static final String[] ignoredPackages = { com.f1.utils.AH.class.getPackage().getName(), com.f1.base.Valued.class.getName() };

	public static void main(String args[]) throws Exception {
		String[] jars = SH.split(':', args[0]);
		String tmpdir = args[1];
		ByteCodeParser bcp = new ByteCodeParser();
		//for (String jar : jars) {
		//ClassPathModifier.addFile(new File(jar));
		//}
		Map<String, ByteCodeClass> classes = new HashMap<String, ByteCodeClass>();
		for (String jar : jars) {
			JarInputStream jis = new JarInputStream(new FastBufferedInputStream(new FileInputStream(new File(jar)), 2048));
			for (;;) {
				ZipEntry entry = jis.getNextEntry();
				if (entry == null)
					break;
				if (entry.getName().endsWith(".class")) {
					DataInputStream dis = new DataInputStream(jis);
					try {
						ByteCodeClass clazz = bcp.parse(dis);
						classes.put(clazz.getThisClass().getClassNameText(), clazz);
					} catch (Exception e) {
						throw new RuntimeException("Error with '" + entry.getName() + "'( " + e.getMessage() + "): ", e);
					}
				}
			}
		}
		HasherMap<String, Boolean> isValuedCache = new HasherMap<String, Boolean>();
		List<Class> valued = new ArrayList<Class>();
		for (String clazz : classes.keySet()) {
			if (isValued(isValuedCache, classes, clazz)) {
				if (MH.allBits(classes.get(clazz).getAccessFlags(), ByteCodeConstants.ACC_ABSTRACT) && !isIgnoredPackage(clazz)) {
					valued.add(RH.getClass(clazz));
					System.out.println(clazz);
				}
			}
		}
		BasicCodeGenerator bcg = new BasicCodeGenerator(new BasicCodeCompiler(tmpdir), true);
		bcg.generateCode(valued);
	}
	private static boolean isIgnoredPackage(String clazz) {
		for (String p : ignoredPackages)
			if (clazz.startsWith(p))
				return true;
		return false;
	}
	private static Boolean isValued(HasherMap<String, Boolean> isValuedCache, Map<String, ByteCodeClass> classes, String name) {
		ByteCodeClass clazz = classes.get(name);
		if (clazz == null)
			return Boolean.FALSE;
		Entry<String, Boolean> e = isValuedCache.getOrCreateEntry(clazz.getThisClass().getClassNameText());
		if (e.getValue() == null) {
			if (Valued.class.getName().equals(name)) {
				e.setValue(Boolean.TRUE);
			} else if (clazz.getSuperClass() != null && isValued(isValuedCache, classes, clazz.getSuperClass().getClassNameText())) {
				e.setValue(Boolean.TRUE);
			} else {
				e.setValue(Boolean.FALSE);
				for (ByteCodeConstClassRef intrface : clazz.getInterfaces()) {
					if (isValued(isValuedCache, classes, intrface.getClassNameText())) {
						e.setValue(Boolean.TRUE);
						break;
					}
				}
			}
		}
		return e.getValue();
	}
}
