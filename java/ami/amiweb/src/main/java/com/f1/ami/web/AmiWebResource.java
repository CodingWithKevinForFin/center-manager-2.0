package com.f1.ami.web;

public class AmiWebResource {

	final private String name;
	final private boolean isCenter;
	final private int imageWidth;
	final private int imageHeight;
	final private long modified;
	final private long checksum;
	final private long size;
	final private byte[] bytes;
	private long cachedAt;

	public AmiWebResource(long now, boolean isCenter, String name, long size, long modified, long checksum, int imageWidth, int imageHeight, byte[] bytes) {
		this.setCachedAt(now);
		this.isCenter = isCenter;
		this.name = name;
		this.size = size;
		this.modified = modified;
		this.checksum = checksum;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.bytes = bytes;
	}
	public String getName() {
		return name;
	}
	public boolean isCenter() {
		return isCenter;
	}
	public int getImageWidth() {
		return imageWidth;
	}
	public int getImageHeight() {
		return imageHeight;
	}
	public long getChecksum() {
		return checksum;
	}
	public long getSize() {
		return size;
	}

	public long getModified() {
		return modified;
	}
	public byte[] getBytes() {
		return bytes;
	}
	public long getCachedAt() {
		return cachedAt;
	}
	public void setCachedAt(long cachedAt) {
		this.cachedAt = cachedAt;
	}
}
