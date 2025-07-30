/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.ack;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import com.f1.base.Ackable;
import com.f1.utils.IOH;

public class FileAckerReader {
	private final List<Integer> missing = new ArrayList<Integer>();

	public FileAckerReader(File file) throws IOException {
		DataInputStream dos = null;
		try {
			if (file.exists()) {
				int length = (int) file.length() / 4;
				if (length == 0)
					return;
				dos = new DataInputStream(new FileInputStream(file));
				int start = dos.readInt();
				NavigableSet<Integer> acked = new TreeSet<Integer>();
				acked.add(start);
				for (int i = 0; i < length; i++) {
					int id = dos.readInt();
					if (id == Ackable.NO_ACK_ID)
						break;
					if (id > start)
						acked.add(id);
				}
				Integer last = acked.first();
				for (Integer id : acked) {
					for (int i = last + 1; i < id; i++)
						missing.add(i);
					last = id;
				}
				missing.add(acked.last() + 1);
			}
		} catch (Exception e) {
			throw new IOException("error reading " + IOH.getFullPath(file), e);
		} finally {
			IOH.close(dos);
		}
	}

	public List<Integer> getMissing() {
		return missing;
	}
}
