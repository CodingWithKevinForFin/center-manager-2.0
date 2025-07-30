package com.f1.tester;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.SH;

public class DeploymentBuilder {

	public static void main(String a[]) throws IOException {
		deploy("c:/upload/tds.zip", "motown.app.tdome.tds.TdsApp", "", "install/common/tdome,test");
	}

	static public void deploy(String output, String main, String args, String resources) throws IOException {

		OutputStream out = new FileOutputStream(output);
		ZipOutputStream z = new ZipOutputStream(out);

		for (String s : SH.split(",", resources)) {
			File f = new File(s);
			if (f.isDirectory()) {
				addDirectory(z, s, f, false);
				continue;
			}
			ZipEntry e = new ZipEntry(toFile(s));
			z.putNextEntry(e);
			System.out.println("Working on " + f);
			z.write(IOH.readData(f));
			z.closeEntry();
		}

		for (String s : EH.getJavaClassPath()) {
			File f = new File(s);
			if (f.isDirectory()) {
				addDirectory(z, s, f, true);
				continue;
			}
			ZipEntry e = new ZipEntry(toFile(s));
			z.putNextEntry(e);
			System.out.println("Working on " + f);
			z.write(IOH.readData(f));
			z.closeEntry();
		}
		StringBuilder sb = new StringBuilder("SEP=:\n\njava $* -cp \"");
		boolean first = true;
		for (String s : EH.getJavaClassPath()) {
			s = toFile(s);
			if (first)
				first = false;
			else
				sb.append("${SEP}");
			sb.append(s);
		}
		sb.append("\" ").append(main);
		sb.append(" ").append(args);
		sb.append("\n");
		System.out.println(sb);
		ZipEntry e = new ZipEntry("start.sh");
		z.putNextEntry(e);
		z.write(sb.toString().getBytes());
		z.closeEntry();
		z.close();
	}

	private static String toFile(String s) {
		return "./" + SH.stripPrefix(SH.replaceAll(SH.afterFirst(s, ":"), '\\', '/'), "/", false);
	}

	private static void addDirectory(ZipOutputStream z, String base, File dir, boolean onlyClasses) throws IOException {
		for (File f : dir.listFiles()) {
			if (f.isDirectory())
				addDirectory(z, base + File.separator + f.getName(), f, onlyClasses);
			else {
				ZipEntry e = new ZipEntry(toFile(base + File.separator + f.getName()));
				z.putNextEntry(e);
				System.out.println("Working on " + f);
				z.write(IOH.readData(f));
				z.closeEntry();
			}
		}
	}
}
