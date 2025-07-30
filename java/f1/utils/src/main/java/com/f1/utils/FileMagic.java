package com.f1.utils;

import java.util.ArrayList;
import java.util.List;

///referenced /usr/share/misc/magic
public class FileMagic {

	public static final byte FILE_TYPE_UNKNOWN = 0;
	public static final byte FILE_TYPE_GZIP_COMPRESSED_DATA = 1;
	public static final byte FILE_TYPE_ZIP_ARCHIVE_DATA = 2;
	public static final byte FILE_TYPE_POSIX_TAR_ARCHIVE = 3;
	public static final byte FILE_TYPE_GNU_TAR_ARCHIVE = 4;
	public static final byte FILE_TYPE_JAVA_CLASS = 5;
	public static final byte FILE_TYPE_DEFLATE_COMPRESSED_DATA = 6;

	public static String typeToString(int type) {
		switch (type) {
			case FILE_TYPE_UNKNOWN:
				return "unknown";
			case FILE_TYPE_GZIP_COMPRESSED_DATA:
				return "gzip_compressed_data";
			case FILE_TYPE_ZIP_ARCHIVE_DATA:
				return "zip_archive_data";
			case FILE_TYPE_POSIX_TAR_ARCHIVE:
				return "posix_tar_archive";
			case FILE_TYPE_GNU_TAR_ARCHIVE:
				return "gnu_tar_archive";
			case FILE_TYPE_JAVA_CLASS:
				return "java_class";
			case FILE_TYPE_DEFLATE_COMPRESSED_DATA:
				return "deflate_compressed_data";
			default:
				return SH.toString(type);
		}
	}

	public static final FileMagic INSTANCE = new FileMagic();

	final private MagicTest rootTest;

	public FileMagic() {
		rootTest = new MagicTest(FILE_TYPE_UNKNOWN);
		init();
	}
	public void init() {
		rootTest.addTest(new StringTest(FILE_TYPE_GZIP_COMPRESSED_DATA, 0, "\037\213"));
		rootTest.addTest(new StringTest(FILE_TYPE_DEFLATE_COMPRESSED_DATA, 0, "\170\001"));
		rootTest.addTest(new StringTest(FILE_TYPE_DEFLATE_COMPRESSED_DATA, 0, "\170\234"));
		rootTest.addTest(new StringTest(FILE_TYPE_DEFLATE_COMPRESSED_DATA, 0, "\170\332"));
		rootTest.addTest(new StringTest(FILE_TYPE_ZIP_ARCHIVE_DATA, 0, "PK\003\004"));
		rootTest.addTest(new StringTest(FILE_TYPE_POSIX_TAR_ARCHIVE, 257, "ustar\0"));
		rootTest.addTest(new StringTest(FILE_TYPE_GNU_TAR_ARCHIVE, 257, "ustar\040\040\0"));
		rootTest.addTest(new StringTest(FILE_TYPE_JAVA_CLASS, 0, "\u00CA\u00FE\u00BA\u00BE"));
	}

	public int getRootType(byte data[]) {
		return rootTest.test(data);
	}

	private static class StringTest extends MagicTest {
		final int offset;
		final byte[] data;

		public StringTest(int fileType, int offset, String text) {
			super(fileType);
			this.offset = offset;
			this.data = AH.castToBytes(text.toCharArray());
		}

		public int test(byte data[]) {
			return AH.startsWith(data, this.data, offset) ? super.test(data) : FILE_TYPE_UNKNOWN;
		}
	}

	public static class MagicTest {
		final private List<MagicTest> subtests = new ArrayList<MagicTest>();
		private int fileType;

		public MagicTest(int fileType) {
			this.fileType = fileType;
		}

		public <T extends MagicTest> T addTest(T test) {
			this.subtests.add(test);
			return test;
		}

		public int test(byte[] data) {
			for (MagicTest test : subtests) {
				final int r = test.test(data);
				if (r != FILE_TYPE_UNKNOWN)
					return r;
			}
			return fileType;
		}

	}

	public static int getType(byte data[]) {
		return INSTANCE.getRootType(data);
	}

}
