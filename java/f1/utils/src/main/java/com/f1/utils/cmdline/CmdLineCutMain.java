package com.f1.utils.cmdline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;

import com.f1.utils.ArgParser;
import com.f1.utils.ArgParser.Arguments;
import com.f1.utils.EH;
import com.f1.utils.FastBufferedOutputStream;
import com.f1.utils.IOH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;

public class CmdLineCutMain {
	public static void main(String[] args) {
		ArgParser ap = new ArgParser("3Forge Cut Utility");
		ap.addSwitchRequired("f", "fields", "*", "Fields to display starting at 0. dash (-) indicates a blank field");
		ap.addSwitchRequired("d", "delimiter", "*", "delimiter to use");
		ap.addSwitchOptional("suf", "suffix", "*", "suffix to append to lines");
		ap.addSwitch("e", "delim", SH.m("*"), true, false, "delim at position");
		Arguments ar = ap.parseNoThrow(args);
		if (ar == null) {
			EH.systemExit(1);
			return;
		}
		String[] fields = SH.split(',', ar.getRequired("f", String.class));
		int[] fieldsInt = new int[fields.length];
		for (int i = 0; i < fields.length; i++) {
			String text = SH.trim(' ', fields[i]);
			if ("-".equals(text))
				fieldsInt[i] = -1;
			else
				fieldsInt[i] = SH.parseInt(text);
		}
		String delim = ar.getRequired("d", String.class);
		String suffix = ar.getOptional("suf", Caster_String.INSTANCE);
		char delimChar = ' ';
		if (delim.length() == 1)
			delimChar = delim.charAt(0);
		List<String> files = ar.getAdditionalArguments();
		if (files.isEmpty())
			files.add(null);
		FastBufferedOutputStream out = new FastBufferedOutputStream(System.out, 1024);
		List<String> delims = (List) ar.getProperties().get("e");
		for (String file : files) {
			final LineNumberReader lnr;
			try {
				if (file != null)
					lnr = new LineNumberReader(new FileReader(new File(file)), 1024);
				else
					lnr = new LineNumberReader(new InputStreamReader(System.in), 1024);
			} catch (FileNotFoundException e) {
				System.err.println("File not found: " + IOH.getFullPath(new File(file)));
				continue;
			}
			try {
				if (delim.length() > 0)
					for (;;) {
						String line = lnr.readLine();
						if (line == null)
							break;
						String[] parts = SH.split(delim, line);
						boolean needsDelim = false;
						int offset = 0;
						for (int pos : fieldsInt)
							if (parts.length > pos || pos == -1) {
								if (needsDelim) {
									if (delims == null)
										out.writeByte(delimChar);
									else
										out.writeBytes(offset >= delims.size() ? delims.get(delims.size() - 1) : delims.get(offset++));
								} else
									needsDelim = true;
								if (pos != -1)
									out.writeBytes(parts[pos]);
							}
						if (suffix != null)
							out.writeBytes(suffix);
						out.writeBytes(SH.NEWLINE);
					}
				else {
					for (;;) {
						String line = lnr.readLine();
						if (line == null)
							break;
						String parts[] = SH.split(delimChar, line);
						boolean needsDelim = false;
						int offset = 0;
						for (int pos : fieldsInt)
							if (parts.length > pos || pos == -1) {
								if (needsDelim) {
									if (delims == null)
										out.writeByte(delimChar);
									else
										out.writeBytes(offset >= delims.size() ? delims.get(delims.size() - 1) : delims.get(offset++));
								} else
									needsDelim = true;
								if (pos != -1)
									out.writeBytes(parts[pos]);
							}
						if (suffix != null)
							out.writeBytes(suffix);
						out.writeBytes(SH.NEWLINE);
					}
				}
				out.flush();
			} catch (IOException e) {
				if (file != null)
					System.err.println("Error with file: " + IOH.getFullPath(new File(file)));
				e.printStackTrace(System.err);
			}
		}
	}
}
