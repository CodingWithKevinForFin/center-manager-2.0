package com.f1.ami.amicommon;

import com.f1.utils.OH;

public class AmiScmRevision implements Comparable<AmiScmRevision> {

	final private String changelistId;
	final private String revision;
	final private long time;
	final private String name;
	final private String user;
	final private String comment;
	public AmiScmRevision(String changelistId, String revision, long time, String name, String user, String comment) {
		super();
		this.changelistId = changelistId;
		this.revision = revision;
		this.time = time;
		this.name = name;
		this.user = user;
		this.comment = comment;
	}
	public String getChangelistId() {
		return changelistId;
	}
	public String getRevision() {
		return revision;
	}
	public long getTime() {
		return time;
	}
	public String getName() {
		return name;
	}
	public String getUser() {
		return user;
	}
	public String getComment() {
		return comment;
	}
	@Override
	public String toString() {
		return "[changelistId=" + changelistId + ", revision=" + revision + ", time=" + time + ", name=" + name + ", user=" + user + ", comment=" + comment + "]";
	}
	@Override
	public int compareTo(AmiScmRevision o) {
		return OH.compare(time, o.time);
	}

}
