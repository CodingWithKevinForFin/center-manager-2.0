package com.f1.email;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.f1.utils.CH;
import com.f1.utils.SH;

//Use ContentType instead
@Deprecated
public class MimeTypeManager {
	private static final MimeTypeManager instance = new MimeTypeManager();

	protected MimeTypeManager() {
		register(new MimeType("application", "atom+xml", "atom"));
		register(new MimeType("application", "json", "json"));
		register(new MimeType("application", "java-archive", "jar"));
		register(new MimeType("application", "javascript", "js"));
		register(new MimeType("application", "ogg", "ogg,ogv"));
		register(new MimeType("application", "pdf", "pdf"));
		register(new MimeType("application", "postscript", "ps"));
		register(new MimeType("application", "x-woff", "woff"));
		register(new MimeType("application", "xhtml+xml", "xhtml,xht,xml"));
		register(new MimeType("application", "xml-dtd", "dtd"));
		register(new MimeType("application", "zip", "zip"));
		register(new MimeType("application", "x-gzip", "gz"));
		register(new MimeType("audio", "basic", "au,snd"));
		register(new MimeType("audio", "mid", "mid,rmi"));
		register(new MimeType("audio", "mpeg", "mp3"));
		register(new MimeType("audio", "x-aiff", "aif,aifc,aiff"));
		register(new MimeType("audio", "x-mpegurl", "m3u"));
		register(new MimeType("audio", "x-pn-realaudio", "ra,ram"));
		register(new MimeType("audio", "x-wav", "wav"));
		register(new MimeType("image", "bmp", "bmp"));
		register(new MimeType("image", "cis-cod", "cod"));
		register(new MimeType("image", "gif", "gif"));
		register(new MimeType("image", "ief", "ief"));
		register(new MimeType("image", "jpeg", "jpe,jpeg,jpg"));
		register(new MimeType("image", "pipeg", "jfif"));
		register(new MimeType("image", "png", "png"));
		register(new MimeType("image", "svg+xml", "svg"));
		register(new MimeType("image", "tiff", "tif,tiff"));
		register(new MimeType("image", "x-cmu-raster", "ras"));
		register(new MimeType("image", "x-cmx", "cmx"));
		register(new MimeType("image", "x-icon", "ico"));
		register(new MimeType("image", "x-portable-anymap", "pnm"));
		register(new MimeType("image", "x-portable-bitmap", "pbm"));
		register(new MimeType("image", "x-portable-graymap", "pgm"));
		register(new MimeType("image", "x-portable-pixmap", "ppm"));
		register(new MimeType("image", "x-rgb", "rgb"));
		register(new MimeType("image", "x-xbitmap", "xbm"));
		register(new MimeType("image", "x-xpixmap", "xpm"));
		register(new MimeType("image", "x-xwindowdump", "xwd"));
		register(new MimeType("message", "rfc822", "mht,mhtml,nws"));
		register(new MimeType("text", "calendar", "ics"));
		register(new MimeType("text", "css", "css"));
		register(new MimeType("text", "csv", "csv"));
		register(new MimeType("text", "h323", "323"));
		register(new MimeType("text", "html", "htm,html,stm"));
		register(new MimeType("text", "iuls", "uls"));
		register(new MimeType("text", "plain", "bas,c,h,txt,ami"));
		register(new MimeType("text", "richtext", "rtx"));
		register(new MimeType("text", "scriptlet", "sct"));
		register(new MimeType("text", "tab-seperated-values", "tsv"));
		register(new MimeType("text", "webviewhtml", "htt"));
		register(new MimeType("text", "x-component", "htc"));
		register(new MimeType("text", "x-setext", "etc"));
		register(new MimeType("text", "x-vard", "vcf"));
		register(new MimeType("video", "mpeg", "mp2,mpa,mpe,mpeg,mpg,mpv2"));
		register(new MimeType("video", "mp4", "mp4"));
		register(new MimeType("video", "quicktime", "mov,qt"));
		register(new MimeType("video", "x-la-asf", "lsf,lsx"));
		register(new MimeType("video", "x-ms-asf", "asf,asr,asx"));
		register(new MimeType("video", "x-msvideo", "avi"));
		register(new MimeType("video", "x-sgi-movie", "movie"));
		register(new MimeType("x-world", "x-vrml", "flr,vrml,wrl,wrz,xaf,xof"));
		register(new MimeType("application", "vnd.ms-excel", "xls"));
		register(new MimeType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx"));
		register(new MimeType("application", "vnd.ms-excel.sheet.macroEnabled.12", "xlsm"));
		register(new MimeType("application", "vnd.ms-excel.sheet.binary.macroenabled.12", "xlsb"));
		register(new MimeType("application", "msword", "doc"));
		register(new MimeType("application", "vnd.openxmlformats-officedocument.wordprocessingml.document", "docx"));
		register(new MimeType("application", "vnd.ms-powerpoint", "ppt"));
		register(new MimeType("application", "vnd.openxmlformats-officedocument.presentationml.presentation", "pptx"));
	}

	private Map<String, MimeType> extension2type = new HashMap<String, MimeType>();

	public void register(MimeType mimeType) {
		for (String s : mimeType.getFileExtensions())
			CH.putOrThrow(extension2type, s.toLowerCase(), mimeType);
	}

	public Set<String> getFileExtensions() {
		return extension2type.keySet();
	}

	public MimeType getMimeTypeForFileName(String fileName) {
		return CH.getOrThrow(extension2type, SH.afterLast(fileName.toLowerCase(), '.'), "file extension for mime type not found");
	}
	public MimeType getMimeTypeNoThrow(String fileName) {
		return extension2type.get(SH.afterLast(fileName.toLowerCase(), '.'));
	}

	public static MimeTypeManager getInstance() {
		return instance;
	}

}
