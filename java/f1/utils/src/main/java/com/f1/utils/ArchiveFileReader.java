package com.f1.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.f1.utils.tar.TarEntry;
import com.f1.utils.tar.TarInputStream;

public class ArchiveFileReader {

	public static void main(String a[]) throws IOException {
		ArchiveFileReader afr = new ArchiveFileReader();
		ArchiveEntry ae = afr.read(new File("/tmp/Oms.1.1.tar.gz"));

		System.out.println(ae.getChecksum());
		for (ArchiveEntry e : ae.getChildren())
			System.out.println(e.getChecksum() + " " + e.getName());
	}
	public ArchiveEntry read(File name) throws IOException {
		return read(name.getName(), IOH.readData(name));
	}
	public ArchiveEntry read(String name, byte[] data) throws IOException {
		int type = FileMagic.getType(data);
		switch (type) {
			case FileMagic.FILE_TYPE_ZIP_ARCHIVE_DATA: {
				ArchiveEntry r = new ArchiveEntry(type, name, OH.EMPTY_BYTE_ARRAY);
				ZipInputStream zis = new ZipInputStream(new FastByteArrayInputStream(data));
				readZip(zis, r);
				return r;
			}
			case FileMagic.FILE_TYPE_GZIP_COMPRESSED_DATA: {
				ArchiveEntry r = new ArchiveEntry(type, name, OH.EMPTY_BYTE_ARRAY);
				GZIPInputStream zis = new GZIPInputStream(new FastByteArrayInputStream(data));
				readGzip(name, zis, r);
				return r;
			}
			case FileMagic.FILE_TYPE_GNU_TAR_ARCHIVE:
			case FileMagic.FILE_TYPE_POSIX_TAR_ARCHIVE:
				ArchiveEntry r = new ArchiveEntry(type, name, OH.EMPTY_BYTE_ARRAY);
				TarInputStream zis = new TarInputStream(new FastByteArrayInputStream(data));
				readTar(zis, r);
				return r;
			default:
				return new ArchiveEntry(type, name, data);
		}
	}

	private void readGzip(String name, GZIPInputStream zis, ArchiveEntry r) throws IOException {
		r.addChild(read("GZIPSTREAM", IOH.readData(zis)));
	}
	private void readZip(ZipInputStream zis, ArchiveEntry r) throws IOException {
		for (;;) {
			final ZipEntry entry = zis.getNextEntry();
			if (entry == null)
				break;
			if (entry.isDirectory())
				continue;
			addChildNestedInDirectory(r, entry.getName(), zis);
		}
	}
	private void readTar(TarInputStream tis, ArchiveEntry r) throws IOException {
		for (;;) {
			final TarEntry entry = tis.getNextEntry();
			if (entry == null)
				break;
			if (entry.isDirectory())
				continue;
			addChildNestedInDirectory(r, entry.getName(), tis);
		}
	}

	private void addChildNestedInDirectory(ArchiveEntry parent, String name, InputStream tis) throws IOException {
		String[] parts = SH.split('/', name);
		String filename = parts.length == 0 ? name : AH.last(parts);
		ArchiveEntry node = parent;
		for (int i = 0; i < parts.length - 1; i++) {
			String part = parts[i];
			ArchiveEntry t = node.getChildByName(part);
			if (t == null)
				node.addChild(t = new ArchiveEntry(FileMagic.FILE_TYPE_UNKNOWN, part, OH.EMPTY_BYTE_ARRAY));
			node = t;
		}
		node.addChild(read(filename, IOH.readData(tis)));
	}

	public static class ArchiveEntry {
		private final Map<String, ArchiveEntry> children = new HashMap<String, ArchiveEntry>();
		private int fileType;
		private byte[] data;
		private String fileName;
		private long checksum;
		private boolean needsChecksum;

		public ArchiveEntry(int type, String fileName, byte[] data) {
			this.fileType = type;
			this.fileName = fileName;
			this.data = data;
			needsChecksum = true;
		}

		public void buildChecksum() {
			long cs = 0;
			for (byte b : data)
				cs = IOH.applyChecksum64(cs, 0xff & b);
			if (!children.isEmpty()) {
				final byte[] buf = new byte[8];
				for (ArchiveEntry child : children.values()) {

					//read the checksum of the child into the first 8 bytes and apply
					ByteHelper.writeLong(child.getChecksum(), buf, 0);
					for (byte b : buf)
						cs = IOH.applyChecksum64(cs, 0xff & b);

					//handle name
					for (byte b : child.getName().getBytes())
						cs = IOH.applyChecksum64(cs, 0xff & b);
					//End of record
					cs = IOH.applyChecksum64(cs, 0);
				}
			}
			this.checksum = cs;
			this.needsChecksum = false;
		}
		public long getChecksum() {
			if (needsChecksum)
				buildChecksum();
			return checksum;
		}

		public void addChild(ArchiveEntry child) {
			needsChecksum = true;
			this.children.put(child.getName(), child);
		}

		public Iterable<ArchiveEntry> getChildren() {
			return children.values();
		}
		public int getChildrenCount() {
			return children.size();
		}

		public int getType() {
			return fileType;
		}

		public byte[] getData() {
			return data;
		}

		public String getName() {
			return this.fileName;
		}

		public StringBuilder toString(int tab, StringBuilder sb) {
			SH.repeat(' ', tab * 2, sb);
			sb.append(fileName).append(" (").append(data == null ? -1 : data.length).append(" bytes)").append(" cs=").append(getChecksum()).append(SH.NEWLINE);
			for (ArchiveEntry child : children.values())
				child.toString(tab + 1, sb);
			return sb;
		}

		public String getTypeAsString() {
			return FileMagic.typeToString(fileType);
		}

		public Map<String, ArchiveEntry> getChildrenByName() {
			return children;
		}

		public ArchiveEntry getChildByName(String name) {
			return children.get(name);
		}

	}

}
