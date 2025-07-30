package com.f1.vortex.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import com.f1.codegen.impl.BasicCodeCompiler;
import com.f1.http.handler.JspCompiler;
import com.f1.utils.AH;
import com.f1.utils.FastBufferedInputStream;
import com.f1.utils.IOH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;

public class PreJspCoder {

	public static void main(String args[]) throws Exception {
		String[] jars = SH.split(':', args[0]);
		String tmpdir = args[1];
		String[] patternsString = AH.subarray(args, 2, args.length - 2);
		Matcher patterns[] = new Matcher[patternsString.length];
		for (int i = 0; i < patternsString.length; i++)
			patterns[i] = Pattern.compile(patternsString[i]).matcher("");

		List<Tuple2<String, byte[]>> files = new ArrayList<Tuple2<String, byte[]>>();
		for (String jar : jars) {
			JarInputStream jis = new JarInputStream(new FastBufferedInputStream(new FileInputStream(new File(jar)), 2048));
			for (;;) {
				ZipEntry entry = jis.getNextEntry();
				if (entry == null)
					break;
				final String name = entry.getName();
				for (Matcher p : patterns) {
					if (p.reset(name).matches()) {
						files.add(new Tuple2<String, byte[]>(name, IOH.readData(jis)));
					}
				}
			}
		}
		if (files.isEmpty()) {
			System.exit(99);
		} else {
			BasicCodeCompiler cc = new BasicCodeCompiler(tmpdir);
			JspCompiler jc = new JspCompiler(cc);
			for (Tuple2<String, byte[]> i : files) {
				String file = jc.getFullClassName(i.getA(), 1);
				System.out.println("Working on " + i.getA() + " ==> " + file);
				if (jc.compile(new String(i.getB()), file) == null) {
					System.err.println("Failed TO COMPILE " + i.getB());
					System.exit(1);
				}
			}
			System.exit(0);
		}
	}
}
