package com.f1.utils.cmdline;

import java.io.IOException;
import java.io.InputStreamReader;

import com.f1.utils.FastBufferedReader;
import com.f1.utils.SH;

public class CleanSvg {

	public static void main(String a[]) throws IOException {
		FastBufferedReader in = new FastBufferedReader(new InputStreamReader(System.in));
		StringBuilder buf = new StringBuilder();
		for (;;) {
			String line = in.readLine();
			if (SH.isnt(line)) {
				if (SH.is(buf))
					System.out.println(convert(buf.toString()));
				buf.setLength(0);
			} else {
				line = line.trim();
				if (SH.startsAndEndsWith(line, "<?", "?>"))
					continue;
				if (SH.startsAndEndsWith(line, "<!--", "->"))
					continue;
				if (SH.isnt(line))
					continue;
				if (buf.length() != 0)
					buf.append(' ');
				buf.append(line);
			}
		}
	}

	static final private String PREFIX = "background-image: url(\'data:image/svg+xml;utf8,";
	static final private String SUFFIX = "\');";

	private static String convert(String string) {

		string = SH.replaceAll(string, '\'', "\\\'");
		return PREFIX + string + SUFFIX;
	}

}
