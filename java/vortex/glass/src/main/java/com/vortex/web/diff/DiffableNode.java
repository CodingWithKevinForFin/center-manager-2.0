package com.vortex.web.diff;

import java.util.Map;

import com.f1.utils.structs.Tuple2;

public interface DiffableNode {

	public static final byte DIFF_TYPE_FILE_GZIP = 1;
	public static final byte DIFF_TYPE_FILE_ZIP = 2;
	public static final byte DIFF_TYPE_FILE_TAR = 3;
	public static final byte DIFF_TYPE_FILE_UNKNOWN = 4;
	public static final byte DIFF_TYPE_FILE_JAVA_CLASS = 5;
	public static final byte DIFF_TYPE_JAVA_FIELD = 6;
	public static final byte DIFF_TYPE_JAVA_METHOD = 7;
	public static final byte DIFF_TYPE_DB_SERVER = 8;
	public static final byte DIFF_TYPE_DB_DATABASE = 9;
	public static final byte DIFF_TYPE_DB_TABLE = 10;
	public static final byte DIFF_TYPE_DB_COLUMN = 11;
	public static final byte DIFF_TYPE_DB_PRIVILEDGE = 12;
	public static final byte DIFF_TYPE_DB_OBJECT = 13;
	public static final byte DIFF_TYPE_APP = 14;
	public static final byte DIFF_TYPE_PROPERTIES = 15;
	public static final byte DIFF_TYPE_PROPERTY = 16;
	public static final byte DIFF_TYPE_BASIC = 100;

	public byte getDiffType();
	public String getDiffName();
	public Tuple2<Byte, String> getDiffKey();
	public Map<Tuple2<Byte, String>, DiffableNode> getDiffChildren();
	public boolean isEqualToNode(DiffableNode node);
	public DiffableNode getDiffChild(Tuple2<Byte, String> name);

	public String getContents();
	public String getIcon();
}
