package com.f1.ami.amicommon.webfilespecial;

import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.base.Table;
import com.f1.utils.CH;
import com.f1.utils.FastBufferedReader;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.columnar.ColumnarTable;

public class AmiSpecialFileProcessor {
	private static final Logger log = LH.get();
	public static final String GET_AUTOSAVE_LIST = "GET_AUTOSAVE_LIST";
	public static final String GET_AUTOSAVE_INSTANCE = "GET_AUTOSAVE_INSTANCE";

	static public Table processSpecial(File file, String instruction, Map<String, ?> params) {
		if (GET_AUTOSAVE_LIST.equals(instruction)) {
			return readAutosaveList(file);
		} else if (GET_AUTOSAVE_INSTANCE.equals(instruction)) {
			int id = CH.getOrThrow(Integer.class, params, "id");
			return readAutosaveInstance(file, id);
		} else
			throw new RuntimeException("Unknown instruction: " + instruction);
	}

	static public Table readAutosaveList(File file) {
		ColumnarTable r = new ColumnarTable(Integer.class, "id", Long.class, "now", String.class, "reason", String.class, "name", String.class, "layout");
		StringBuilder sink = new StringBuilder();
		FastBufferedReader reader = null;
		if (file.exists())
			try {
				reader = new FastBufferedReader(new FileReader(file));
				int errorsCount = 0;
				while (reader.readLine(SH.clear(sink))) {
					try {
						int p1 = sink.indexOf("|");
						int p2 = sink.indexOf("|", p1 + 1);
						int p3 = sink.indexOf("|", p2 + 1);
						int p4 = sink.indexOf("|", p3 + 1);
						int id = SH.parseInt(sink, 0, p1, 10);
						long now = SH.parseLong(sink, p1 + 1, p2, 10);
						String reason = sink.substring(p2 + 1, p3);
						String layoutName = sink.substring(p3 + 1, p4);
						r.getRows().addRow(id, now, reason, layoutName, null);
					} catch (Exception e) {
						errorsCount++;
						if (errorsCount < 100)
							LH.warning(log, "Bad line in '", IOH.getFullPath(file), "': ", sink, e);
						else if (errorsCount == 100)
							LH.warning(log, "Too many errors, not logging");

					}
				}
			} catch (EOFException e) {
				LH.warning(log, "End of file reached", e);
			} catch (IOException e) {
				throw new RuntimeException("Error reading " + IOH.getFullPath(file), e);
			} finally {
				IOH.close(reader);
			}
		return r;
	}

	static public Table readAutosaveInstance(File file, int id) {
		ColumnarTable r = new ColumnarTable(Integer.class, "id", Long.class, "now", String.class, "reason", String.class, "name", String.class, "layout");
		StringBuilder sink = new StringBuilder();
		FastBufferedReader reader = null;
		if (file.exists())
			try {
				reader = new FastBufferedReader(new FileReader(file));
				int errorsCount = 0;
				while (reader.readLine(SH.clear(sink))) {
					try {
						int p1 = sink.indexOf("|");
						int id2 = SH.parseInt(sink, 0, p1, 10);
						if (id2 == id) {
							int p2 = sink.indexOf("|", p1 + 1);
							int p3 = sink.indexOf("|", p2 + 1);
							int p4 = sink.indexOf("|", p3 + 1);
							int p5 = sink.indexOf("|", p4 + 1);
							int p6 = sink.indexOf("|", p5 + 1);
							long now = SH.parseLong(sink, p1 + 1, p2, 10);
							String reason = sink.substring(p2 + 1, p3);
							String layoutName = sink.substring(p3 + 1, p4);
							String layout = sink.substring(p6 + 1);
							r.getRows().addRow(id, now, reason, layoutName, layout);
						}
					} catch (Exception e) {
						errorsCount++;
						if (errorsCount < 100)
							LH.warning(log, "Bad line in '", IOH.getFullPath(file), "': ", sink, e);
						else if (errorsCount == 100)
							LH.warning(log, "Too many errors, not logging");
					}
				}
			} catch (IOException e) {
				throw new RuntimeException("Error reading " + IOH.getFullPath(file), e);
			} finally {
				IOH.close(reader);
			}
		return r;
	}
}
