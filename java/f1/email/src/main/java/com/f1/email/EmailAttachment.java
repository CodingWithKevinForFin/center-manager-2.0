package com.f1.email;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import com.f1.utils.FastByteArrayInputStream;

public class EmailAttachment implements DataSource {

	private final String name;
	private final MimeType mimeType;
	private final byte[] data;
	private final String contentId;

	public EmailAttachment(byte data[], MimeType mimeType, String name) {
		this.name = name;
		this.data = data;
		this.mimeType = mimeType;
		this.contentId = null;
	}
	public EmailAttachment(byte data[], MimeType mimeType, String name, String contentId) {
		this.name = name;
		this.data = data;
		this.mimeType = mimeType;
		this.contentId = contentId;
	}

	public String getName() {
		return name;
	}
	public String getContentId() {
		return contentId;
	}

	public MimeType getMimeType() {
		return mimeType;
	}

	public byte[] getData() {
		return data;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new FastByteArrayInputStream(data);
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + getContentType() + " " + name + ": " + data.length + " byte(s)";
	}

	@Override
	public String getContentType() {
		return mimeType.getName();
	}

}
