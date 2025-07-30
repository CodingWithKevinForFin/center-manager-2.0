/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.Map;

import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.impl.CaseInsensitiveHasher;

public class ContentType {

	public static final String TYPE_TEXT = "text";
	public static final String TYPE_APPLICATION = "application";
	public static final String TYPE_IMAGE = "image";
	public static final String TYPE_AUDIO = "audio";
	public static final String TYPE_VIDEO = "video";

	static private final Map<CharSequence, ContentType> ByFileExtension = new HasherMap<CharSequence, ContentType>(CaseInsensitiveHasher.INSTANCE);

	final private String mimeSuperType;
	final private String mimeSubType;
	final private String mimeType;
	final private String fileExtensions[];
	final private String description;
	final private byte[] mimeTypeBytes;

	public ContentType(String mimeSuperType, String mimeSubType, String description, String... fileExtensions) {
		this.mimeSuperType = mimeSuperType;
		this.mimeSubType = mimeSubType;
		this.mimeType = SH.path('/', mimeSuperType, mimeSubType);
		this.mimeTypeBytes = mimeType.getBytes();
		this.description = description;
		this.fileExtensions = fileExtensions;
	}

	public String getMimeSuperType() {
		return mimeSuperType;
	}

	public String getMimeSubType() {
		return mimeSubType;
	}

	public String getMimeType() {
		return mimeType;
	}

	public byte[] getMimeTypeAsBytes() {
		return mimeTypeBytes;
	}

	public String[] getFileExtensions() {
		return fileExtensions;
	}

	public String getDescription() {
		return description;
	}

	public String getDefaultFileExtension() {
		return fileExtensions.length > 0 ? fileExtensions[0] : null;
	}

	public static ContentType registerContentType(String mimeSuperType, String mimeSubType, String description, String... fileExtensions) {
		ContentType r = new ContentType(mimeSuperType, mimeSubType, description, fileExtensions);
		for (String f : fileExtensions)
			CH.putOrThrow(ByFileExtension, f, r, "file extension");
		return r;
	}

	// see http://www.utoronto.ca/web/htmldocs/book/book-3ed/appb/mimetype.html
	public static final ContentType SVG = registerContentType(TYPE_IMAGE, "svg+xml", "Scalable Vector Graphics", "svg");
	public static final ContentType HTML = registerContentType(TYPE_TEXT, "html;charset=utf-8", "HTML text data(RFC 1866)", "html", "htm", "jsp");
	public static final ContentType CSS = registerContentType(TYPE_TEXT, "css", "style sheet", "css");
	public static final ContentType JS = registerContentType(TYPE_APPLICATION, "javascript", "javascript", "js", "ajax", "jsonp", "map");
	public static final ContentType TEXT = registerContentType(TYPE_TEXT, "plain", "Plain text: documents; program listings", "txt", "c", "c++", "pl", "cc", "h");
	public static final ContentType CAPTION = registerContentType(TYPE_TEXT, "vtt", "Web Video Text Tracks", "vtt");
	public static final ContentType PNG = registerContentType(TYPE_IMAGE, "png", "Portable Network Graphics", "png", "x-png");
	public static final ContentType GIF = registerContentType(TYPE_IMAGE, "gif", "GIF", "gif");
	public static final ContentType JPEG = registerContentType(TYPE_IMAGE, "jpeg", "jpeg", "jpg", "jpe");
	public static final ContentType ICO = registerContentType(TYPE_IMAGE, "ico", "icon", "ico");
	public static final ContentType EXCEL = registerContentType(TYPE_APPLICATION, "ms-excel", "Excel spreadsheet(Microsoft)", "xls", "xlt", "xla", "xlsx");
	public static final ContentType DOC = registerContentType(TYPE_APPLICATION, "msword", "Word(Microsoft)", "doc", "dot", "docx", "dotx");
	public static final ContentType PDF = registerContentType(TYPE_APPLICATION, "pdf", "Adobe Acrobat PDF", "pdf");
	public static final ContentType BINARY = registerContentType(TYPE_APPLICATION, "octet-stream", "binary data", "gz", "exe", "db");
	public static final ContentType JSON = registerContentType(TYPE_APPLICATION, "json", "json", new String[] { "json" });
	public static final ContentType AUD = registerContentType(TYPE_AUDIO, "wav", "Waveform Audio", new String[] { "wav", "wave" });
	public static final ContentType MP3 = registerContentType(TYPE_AUDIO, "mpeg", "MP3 Audio", new String[] { "mp3" });
	public static final ContentType MP4 = registerContentType(TYPE_VIDEO, "mp4", "MP4 Video", new String[] { "mp4" });
	public static final ContentType ZIP = registerContentType(TYPE_APPLICATION, "zip", "zip files", "zip");

	public static ContentType getTypeByFileExtension(CharSequence fileExtension) {
		return CH.getOrThrow(ByFileExtension, fileExtension);
	}

	public static ContentType getTypeByFileExtension(CharSequence fileExtension, ContentType deflt) {
		return OH.noNull(ByFileExtension.get(fileExtension), deflt);
	}

}
