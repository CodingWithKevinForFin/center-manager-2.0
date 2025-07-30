package com.vortex.web.diff;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.utils.ArchiveFileReader;
import com.f1.utils.ArchiveFileReader.ArchiveEntry;
import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.FileMagic;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.bytecode.ByteCodeClass;
import com.f1.utils.bytecode.ByteCodeField;
import com.f1.utils.bytecode.ByteCodeMethod;
import com.f1.utils.bytecode.ByteCodeParser;

public class DiffableArchiveNode extends AbstractDiffableNode {
	private static final Logger log = LH.get(DiffableArchiveNode.class);

	final private ArchiveEntry entry;
	public DiffableArchiveNode(ArchiveFileReader.ArchiveEntry entry) {
		super(magicTypeToDiffType(entry.getType()), entry.getName());
		this.entry = entry;
		for (ArchiveEntry child : entry.getChildren())
			addChild(new DiffableArchiveNode(child));
		if (entry.getType() == FileMagic.FILE_TYPE_JAVA_CLASS) {
			try {
				ByteCodeClass value = new ByteCodeParser().parse(new FastByteArrayDataInputStream(entry.getData()));
				try {
					for (ByteCodeField field : value.getFields())
						addChild(new DiffableJavaFieldNode(field));
					for (ByteCodeMethod method : value.getMethods())
						addChild(new DiffableJavaMethodNode(method));
				} catch (Exception e) {
					LH.log(log, Level.WARNING, "Could not traverse java bytecode for ", entry.getName(), e);
				}
			} catch (Exception e) {
				LH.log(log, Level.WARNING, "Could not traverse java bytecode for ", entry.getName(), e);
			}
		}
	}

	private static byte magicTypeToDiffType(int type) {
		switch (type) {
			case FileMagic.FILE_TYPE_GNU_TAR_ARCHIVE:
				return DiffableNode.DIFF_TYPE_FILE_TAR;
			case FileMagic.FILE_TYPE_GZIP_COMPRESSED_DATA:
				return DiffableNode.DIFF_TYPE_FILE_GZIP;
			case FileMagic.FILE_TYPE_JAVA_CLASS:
				return DiffableNode.DIFF_TYPE_FILE_JAVA_CLASS;
			case FileMagic.FILE_TYPE_POSIX_TAR_ARCHIVE:
				return DiffableNode.DIFF_TYPE_FILE_TAR;
			case FileMagic.FILE_TYPE_UNKNOWN:
				return DiffableNode.DIFF_TYPE_FILE_UNKNOWN;
			case FileMagic.FILE_TYPE_ZIP_ARCHIVE_DATA:
				return DiffableNode.DIFF_TYPE_FILE_ZIP;
		}
		return 0;
	}

	public byte[] getData() {
		return entry.getData();
	}

	@Override
	public boolean isEqualToNode(DiffableNode node) {
		DiffableArchiveNode n = (DiffableArchiveNode) node;
		return Arrays.equals(n.getData(), getData());
	}

	@Override
	public String getContents() {
		byte[] data = getData();
		if (data == null || data.length == 0)
			return "";
		if (SH.isAscii(data))
			return new String(data);
		else
			return getData().length + " Byte(s)";
	}
}
