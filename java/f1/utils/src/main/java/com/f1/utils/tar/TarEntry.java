package com.f1.utils.tar;

import java.io.File;
import java.util.Date;

import com.f1.utils.MH;

public class TarEntry {
	private String magic;
	protected File file;

	private int mode;
	private long size;
	private int checkSum;
	private byte linkFlag;
	private String linkName;
	private int devMajor;
	private int devMinor;

	private String name;
	private int userId;
	private int groupId;
	private long modTime;
	private String userName;
	private String groupName;

	public TarEntry(String name, boolean isDirectory) {
		this.name = name;
		this.linkName = "";
		this.userName = "";
		this.groupName = "";
		this.magic = TarHelper.MAGIC;
		if (isDirectory) {
			this.mode = 040755;
			this.linkFlag = TarHelper.LF_DIR;
			if (this.name.charAt(this.name.length() - 1) != '/')
				this.name = name + "/";
			else
				this.name = name;
		} else {
			this.name = name;
			this.mode = 0100644;
			this.linkFlag = TarHelper.LF_NORMAL;
		}
	}
	public TarEntry(File file, String entryName) {
		this.file = file;
		String name = entryName;
		name = name.replace(File.separatorChar, '/');
		if (name.startsWith("/"))
			name = name.substring(1);
		this.linkName = "";
		if (file.isDirectory()) {
			this.mode = 040755;
			this.linkFlag = TarHelper.LF_DIR;
			if (name.charAt(name.length() - 1) != '/')
				this.name = name + "/";
			else
				this.name = name;
		} else {
			this.name = name;
			this.mode = 0100644;
			this.linkFlag = TarHelper.LF_NORMAL;
		}
		this.size = file.length();
		this.modTime = file.lastModified() / 1000;
		this.checkSum = 0;
		this.devMajor = 0;
		this.devMinor = 0;
		this.magic = TarHelper.TMAGIC;
	}

	public TarEntry(byte[] bh) {
		int offset = 0;
		this.name = TarHelper.parseName(bh, offset, TarHelper.NAMELEN);
		offset += TarHelper.NAMELEN;
		this.mode = (int) TarHelper.parseOctal(bh, offset, TarHelper.MODELEN);
		offset += TarHelper.MODELEN;
		this.userId = (int) TarHelper.parseOctal(bh, offset, TarHelper.UIDLEN);
		offset += TarHelper.UIDLEN;
		this.groupId = (int) TarHelper.parseOctal(bh, offset, TarHelper.GIDLEN);
		offset += TarHelper.GIDLEN;
		this.size = TarHelper.parseOctal(bh, offset, TarHelper.SIZELEN);
		offset += TarHelper.SIZELEN;
		this.modTime = TarHelper.parseOctal(bh, offset, TarHelper.MODTIMELEN);
		offset += TarHelper.MODTIMELEN;
		this.checkSum = (int) TarHelper.parseOctal(bh, offset, TarHelper.CHKSUMLEN);
		offset += TarHelper.CHKSUMLEN;
		this.linkFlag = bh[offset++];
		this.linkName = TarHelper.parseName(bh, offset, TarHelper.NAMELEN);
		offset += TarHelper.NAMELEN;
		this.magic = TarHelper.parseName(bh, offset, TarHelper.MAGICLEN);
		offset += TarHelper.MAGICLEN;
		this.userName = TarHelper.parseName(bh, offset, TarHelper.UNAMELEN);
		offset += TarHelper.UNAMELEN;
		this.groupName = TarHelper.parseName(bh, offset, TarHelper.GNAMELEN);
		offset += TarHelper.GNAMELEN;
		this.devMajor = (int) TarHelper.parseOctal(bh, offset, TarHelper.DEVLEN);
		offset += TarHelper.DEVLEN;
		this.devMinor = (int) TarHelper.parseOctal(bh, offset, TarHelper.DEVLEN);
	}

	public boolean isDescendent(TarEntry desc) {
		return desc.getName().startsWith(getName());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getGroupId() {
		return this.groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getGroupName() {
		return this.groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setIds(int userId, int groupId) {
		this.setUserId(userId);
		this.setGroupId(groupId);
	}

	public TarEntry setModTime(long time) {
		this.modTime = time / 1000;
		return this;
	}

	public void setModTime(Date time) {
		this.modTime = time.getTime() / 1000;
	}

	public long getModTime() {
		return this.modTime * 1000;
	}

	public File getFile() {
		return this.file;
	}

	public long getSize() {
		return this.size;
	}

	/**
	 * Checks if the org.xeustechnologies.jtar entry is a directory
	 * 
	 * @return
	 */
	public boolean isDirectory() {
		if (this.file != null)
			return this.file.isDirectory();
		if (this.linkFlag == TarHelper.LF_DIR)
			return true;
		if (this.name.toString().endsWith("/"))
			return true;
		return false;
	}

	/**
	 * Writes the header to the byte buffer
	 * 
	 * @param outbuf
	 */
	public void writeEntryHeader(byte[] outbuf) {
		int offset = 0;
		offset = TarHelper.getNameBytes(this.name, outbuf, offset, TarHelper.NAMELEN);
		offset = TarHelper.getOctalBytes(this.mode, outbuf, offset, TarHelper.MODELEN);
		offset = TarHelper.getOctalBytes(this.userId, outbuf, offset, TarHelper.UIDLEN);
		offset = TarHelper.getOctalBytes(this.groupId, outbuf, offset, TarHelper.GIDLEN);
		long size = this.size;
		offset = TarHelper.getLongOctalBytes(size, outbuf, offset, TarHelper.SIZELEN);
		offset = TarHelper.getLongOctalBytes(this.modTime, outbuf, offset, TarHelper.MODTIMELEN);
		int csOffset = offset;
		for (int c = 0; c < TarHelper.CHKSUMLEN; ++c)
			outbuf[offset++] = (byte) ' ';
		outbuf[offset++] = this.linkFlag;
		offset = TarHelper.getNameBytes(this.linkName, outbuf, offset, TarHelper.NAMELEN);
		offset = TarHelper.getNameBytes(this.magic, outbuf, offset, TarHelper.MAGICLEN);
		offset = TarHelper.getNameBytes(this.userName, outbuf, offset, TarHelper.UNAMELEN);
		offset = TarHelper.getNameBytes(this.groupName, outbuf, offset, TarHelper.GNAMELEN);
		offset = TarHelper.getOctalBytes(this.devMajor, outbuf, offset, TarHelper.DEVLEN);
		offset = TarHelper.getOctalBytes(this.devMinor, outbuf, offset, TarHelper.DEVLEN);
		for (; offset < outbuf.length;)
			outbuf[offset++] = 0;
		final long checkSum = TarHelper.computeCheckSum(outbuf);
		TarHelper.getCheckSumOctalBytes(checkSum, outbuf, csOffset, TarHelper.CHKSUMLEN);
	}

	public int getMode() {
		return mode;
	}

	public boolean isModeExecuteByOwner() {
		return MH.allBits(mode, TarHelper.MODE_TUEXEC);
	}

	public boolean isModeReadableByOwner() {
		return MH.allBits(mode, TarHelper.MODE_TUREAD);
	}

	public boolean isModeWriteableByOwner() {
		return MH.allBits(mode, TarHelper.MODE_TUWRITE);
	}

	public void setIsModeReadableByOwner(boolean can) {
		mode = MH.setBits(mode, TarHelper.MODE_TUREAD, can);
	}
	public void setIsModeWriteableByOwner(boolean can) {
		mode = MH.setBits(mode, TarHelper.MODE_TUWRITE, can);
	}
	public void setIsModeExecutableByOwner(boolean can) {
		mode = MH.setBits(mode, TarHelper.MODE_TUEXEC, can);
	}

	public int getDevMajor() {
		return devMajor;
	}
	public void setDevMajor(int devMajor) {
		this.devMajor = devMajor;
	}
	public int getDevMinor() {
		return devMinor;
	}
	public void setDevMinor(int devMinor) {
		this.devMinor = devMinor;
	}
	public byte getLinkFlag() {
		return linkFlag;
	}
	public void setLinkFlag(byte linkFlag) {
		this.linkFlag = linkFlag;
	}
	public String getLinkName() {
		return linkName;
	}
	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}
	public String getMagic() {
		return magic;
	}

}
