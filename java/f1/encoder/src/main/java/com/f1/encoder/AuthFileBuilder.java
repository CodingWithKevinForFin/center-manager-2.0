package com.f1.encoder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;

import com.f1.utils.FastByteArrayOutputStream;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.encrypt.RsaEncryptUtils;

public class AuthFileBuilder {

	private static final String PREFIX_TOP = "3FTOP";
	private static final String PREFIX_APP = "3FAPP";
	private static final String PREFIX_END = "3FEND";
	private static final char DELIM = '|';
	private static final String WARNING[] = new String[]{"# MANTAINED BY FORGE FINANCIAL FRAMEWORKS LLC.",
			"# AUTO-GENERATED: DO NOT CHANGE. MODIFYING WILL RENDER THIS FILE INVALID. "};
	private RSAPublicKey publicKey;
	private List<String> lines = new ArrayList<String>();
	private String header;
	private String footer;

	public AuthFileBuilder(String publicKey) {
		this.publicKey = RsaEncryptUtils.stringToPublicKey(publicKey);
	}

	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}

	public String getFooter() {
		return footer;
	}
	public void setFooter(String footer) {
		this.footer = footer;
	}

	public void writeFile(File location) throws IOException {
		if (header == null)
			throw new NullPointerException("header not defined");
		if (footer == null)
			throw new NullPointerException("footer not defined");
		final PrintWriter writer = new PrintWriter(new FileWriter(location));
		try {
			final FastByteArrayOutputStream t = new FastByteArrayOutputStream();
			for (String s : WARNING)
				writer.println(s);
			writeLine(PREFIX_TOP, header, writer, t, true);
			for (String line : lines)
				writeLine(PREFIX_APP, line, writer, t, true);
			writeLine(PREFIX_END, footer, writer, t, false);
			writer.print(DELIM);
			String checksum = RsaEncryptUtils.checkSumString(RsaEncryptUtils.encrypt(publicKey, t.toByteArray(), false));
			writer.println(checksum);
		} finally {
			IOH.close(writer);
		}
	}
	private void writeLine(String prefix, String text, PrintWriter writer, FastByteArrayOutputStream buf, boolean endLine) {
		text = prefix + DELIM + text;
		buf.write(text.getBytes());
		buf.write(0);
		writer.print(text);
		if (endLine)
			writer.println();
	}
	private final static int STATE_TOP = 0;
	private final static int STATE_APP = 1;
	private final static int STATE_END = 2;

	public void readFile(File location) throws IOException {
		header = null;
		lines.clear();
		final FastByteArrayOutputStream t = new FastByteArrayOutputStream();
		int state = 0;
		String expectedChecksum = null;
		final LineNumberReader reader = new LineNumberReader(new FileReader(location));
		String calcChecksum = "";
		try {
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (SH.startsWith(line, '#'))
					continue;
				final String prefix = SH.beforeFirst(line, DELIM);
				final String data = SH.afterFirst(line, DELIM);
				if (PREFIX_TOP.equals(prefix)) {
					if (state != STATE_TOP)
						throw new RuntimeException(PREFIX_TOP + " only expected at first line");
					header = data;
					state = STATE_APP;
				} else if (PREFIX_APP.equals(prefix)) {
					if (state != STATE_APP)
						throw new RuntimeException(PREFIX_APP + " not expected here");
					lines.add(data);
				} else if (PREFIX_END.equals(prefix)) {
					if (state != STATE_APP)
						throw new RuntimeException(PREFIX_END + " not expected here");
					expectedChecksum = SH.afterLast(data, DELIM);
					footer = SH.beforeLast(data, DELIM);
					// don't want to include checksum in calculating the
					// checksum
					line = SH.beforeLast(line, DELIM);
					state = STATE_END;
				}
				t.write(line.getBytes());
				t.write(0);
			}
			if (state != STATE_END)
				throw new RuntimeException("missing " + PREFIX_END);
			calcChecksum = RsaEncryptUtils.checkSumString(RsaEncryptUtils.encrypt(publicKey, t.toByteArray(), false));
		} catch (Exception e) {
			throw new RuntimeException("Error at line " + reader.getLineNumber() + " in file: " + IOH.getFullPath(location), e);
		} finally {
			IOH.close(reader);
		}
		if (!OH.eq(calcChecksum, expectedChecksum))
			throw new RuntimeException("Invalid File: " + IOH.getFullPath(location));

	}

	public void appendLine(String text) {
		if (text.indexOf(SH.CHAR_CR) != -1)
			throw new IllegalArgumentException("may not have CR: " + text);
		if (text.indexOf(SH.CHAR_LF) != -1)
			throw new IllegalArgumentException("may not have LF: " + text);
		lines.add(text);
	}

	public Iterable<String> getLines() {
		return lines;
	}

	public static void main(String a[]) throws IOException {
		RSAPublicKey key = RsaEncryptUtils.generateKey("this is a test of some key 1234").getA();
		String keyStr = RsaEncryptUtils.publicKeyToString(key);
		AuthFileBuilder afb = new AuthFileBuilder(keyStr);
		File f = new File("/tmp/auth.txt");
		if (f.exists())
			afb.readFile(f);
		afb.appendLine("My First Line");
		afb.appendLine("My Second Line");
		afb.appendLine("My Third Line");
		afb.setFooter("my footer");
		afb.setHeader("my header");
		afb.writeFile(f);
	}
}
