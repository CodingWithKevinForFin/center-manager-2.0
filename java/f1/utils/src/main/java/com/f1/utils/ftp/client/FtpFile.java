package com.f1.utils.ftp.client;

public class FtpFile {

	public static final byte TYPE_PARENT_DIR = 1;
	public static final byte TYPE_CHILD_DIR = 2;
	public static final byte TYPE_FILE = 3;
	public static final byte TYPE_UNKNOWN = -1;

	public static long UNKNOWN = -1;

	private String name;
	private long size;
	private long date;
	private byte type;
	private String permissions;

	public FtpFile() {
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}

	public String toString() {
		return name;
	}

	public void setType(byte type) {
		this.type = type;
	}
	public byte getType() {
		return type;
	}

	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}
	public String getPermissions() {
		return permissions;
	}

	public String getTypeAsString() {
		switch (type) {
			case TYPE_CHILD_DIR:
				return "cdir";
			case TYPE_PARENT_DIR:
				return "pdir";
			case TYPE_FILE:
				return "file";
			default:
				return "unknown";
		}
	}

}
