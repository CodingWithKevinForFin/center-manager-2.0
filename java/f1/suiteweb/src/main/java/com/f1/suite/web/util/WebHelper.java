/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.util;

import java.awt.Color;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpUtils;
import com.f1.utils.CH;
import com.f1.utils.ColorHelper;
import com.f1.utils.MH;
import com.f1.utils.SH;
import com.f1.utils.encrypt.EncoderUtils;

public class WebHelper {

	public static String[] COMMON_COLORS = { "white", "silver", "gray", "black", "red", "maroon", "yellow", "olive", "lime", "green", "aqua", "teal", "blue", "navy", "fuchsia",
			"purple" };
	public static String[] COMMON_FONTS = { "Arial", "Courier", "Georgia", "Impact", "Lucida", "Times New Roman", "Verdana" };

	private static String[] DISTINCT_COLORS = { "#000000", "#011aff", "#ff001b", "#00a600", "#730081", "#005067", "#873d00", "#00009d", "#00ff02", "#7200f1", "#1b5306", "#d4007c",
			"#0050c0", "#5d0025", "#5f3f55", "#e24400", "#648a00", "#e000f6", "#00a462", "#5833b9", "#4cd300", "#00ea51", "#b73a51", "#007fff", "#0095b2", "#b00000", "#bd8400",
			"#b12bbe", "#568656", "#0e0e4e", "#00dba1", "#545cff", "#ff3d88", "#00c9f5", "#9eff00", "#5778ab", "#58ff40", "#ffb100", "#ff773c", "#96bb2e", "#ff43ff", "#b0815f",
			"#f5fa00", "#56c87c", "#ad5dff", "#4da2ec", "#2fffd8", "#cc69ad", "#8d528e", "#40ff91", "#daaf47", "#8aa094", "#007b32", "#3cb83c", "#c7e533", "#a10449", "#50cfc1",
			"#8a86db", "#85682f", };
	private static final Set<String> FONTS;

	static {
		FONTS = CH.s(COMMON_FONTS);
	}

	static public String[] getDistinctColors() {
		return DISTINCT_COLORS;
	}

	public static String getUniqueColorNoBlack(int index) {
		return DISTINCT_COLORS[(Math.abs(index) + 1) % (DISTINCT_COLORS.length - 1)];
	}
	public static String getUniqueColor(int index) {
		return DISTINCT_COLORS[Math.abs(index) % DISTINCT_COLORS.length];
	}

	public static String getCookie(HttpRequestResponse request, String cookieName, boolean required) {
		return request.getCookies().get(cookieName);
	}

	public static String appendParamToUrl(String target, String key, String value) {
		if (target.indexOf('?') == -1)
			return target += '?' + key + '=' + value;
		else
			return target += '&' + key + '=' + value;
	}

	public static String toUrl(String url, Map<String, Object> attributes) {
		if (attributes == null)
			return url;
		StringBuilder sb = new StringBuilder(url);
		sb.append("?");
		SH.join("&", "=", attributes, sb);
		return sb.toString();
	}

	public static StringBuilder quote(CharSequence text, StringBuilder sb) {
		sb.append('\'');
		for (int i = 0, l = text.length(); i < l; i++) {
			char c = text.charAt(i);
			switch (c) {
				case '\"':
					sb.append("\\\"");
					break;
				case '\'':
					sb.append("\\\'");
					break;
				case '\\':
					sb.append("\\\\");
					break;
				case '\n':
					sb.append("\\n");
					break;
				case '\r':
					break;
				default:
					if (c > 0x7e)
						SH.toUnicodeHex(sb, c);
					else
						sb.append(c);
			}
		}
		sb.append('\'');
		return sb;
	}
	public static StringBuilder quoteHtml(CharSequence text, StringBuilder sb) {
		sb.append('\'');
		escapeHtml(text, sb);
		sb.append('\'');
		return sb;
	}

	public static String escapeHtml(CharSequence text) {
		if (text == null)
			return null;
		return escapeHtml(text, new StringBuilder()).toString();
	}
	public static String escapeHtmlNewLineToBr(CharSequence text) {
		return escapeHtmlNewLineToBr(text, new StringBuilder()).toString();
	}
	public static StringBuilder escapeHtmlNewLineToBr(CharSequence text, StringBuilder sb) {
		escapeHtml(text, 0, text.length(), true, "<BR>", sb);
		return sb;
	}
	public static StringBuilder escapeHtml(CharSequence text, StringBuilder sb) {
		escapeHtml(text, 0, text.length(), true, "\\n", sb);
		return sb;
	}
	public static StringBuilder escapeHtmlIncludeBackslash(CharSequence text, StringBuilder sb) {
		escapeHtml(text, 0, text.length(), true, "\\n", sb);
		return sb;
	}
	public static StringBuilder escapeHtml(CharSequence text, int start, int end, boolean includeBackslash, StringBuilder sb) {
		escapeHtml(text, start, end, includeBackslash, "\\n", sb);
		return sb;
	}
	public static void escapeHtml(CharSequence text, int start, int end, boolean includeBackslash, String replaceNewLineWith, Appendable sb) {
		HttpUtils.escapeHtml(text, start, end, includeBackslash, replaceNewLineWith, sb);
	}

	//TODO this will break if a css class name is a substring of another css classname!
	public static String applyCssClass(String existingCssClasses, String classToAdd) {
		if (existingCssClasses == null || existingCssClasses.length() == 0)
			return classToAdd;
		else if (existingCssClasses.indexOf(classToAdd) == -1)
			return existingCssClasses + " " + classToAdd;
		return existingCssClasses;
	}

	//TODO this will break if a css class name is a substring of another css classname!
	public static String removeCssClass(String existingCssClasses, String classToRemove) {
		if (existingCssClasses == null)
			return null;
		int i = existingCssClasses.indexOf(classToRemove);
		if (i == -1)
			return existingCssClasses;
		if (i == 0) {
			if (classToRemove.length() == existingCssClasses.length())
				return "";
			return existingCssClasses.substring(classToRemove.length() + 1);//account for space
		}
		if (classToRemove.length() + i == existingCssClasses.length()) {
			return existingCssClasses.substring(0, i - 1);//account for space
		} else {

			return existingCssClasses.substring(0, i - 1) + existingCssClasses.substring(i + classToRemove.length());//account for space
		}
	}

	public static boolean isColor(String text) {
		return ColorHelper.isColor(text);
	}

	public static Color parseColorNoThrow(String rrggbb) {
		return ColorHelper.parseColorNoThrow(rrggbb);
	}
	public static Color parseColor(String rrggbb) {
		return ColorHelper.parseColor(rrggbb);
	}

	public static String toString(Color color) {
		return ColorHelper.toString(color);
	}

	public static String formatForPre(byte[] data) {
		if (data == null)
			return "";
		int size = data.length;
		for (int i = 0; i < data.length; i++) {
			final byte c = data[i];
			if (c == '<' || c == '>')
				size += 3;//&lt;
		}
		if (size == data.length)
			return new String(data);
		StringBuilder sb = new StringBuilder(size);
		for (int i = 0; i < data.length; i++) {
			final char c = (char) data[i];
			if (c == '<')
				sb.append("&lt;");
			else if (c == '>')
				sb.append("&gt;");
			else
				sb.append(c);
		}
		return sb.toString();
	}
	public static Color getContrastingColor(Color col) {
		return (col.getRed() + col.getGreen() + col.getBlue()) > 384 ? Color.BLACK : Color.WHITE;
	}
	public static boolean isFont(String part) {
		return FONTS.contains(part);
	}

	public static void main2(String a[]) throws IOException {
		//		WebImage orig = readImage(new File("/home/rcooke/smartfin/FamilyDollar_img1.jpg"));
		//		int height = 1239;
		//		int width = 2337;
		//		double n = 0.58630113;
		//
		//		orig = scaleImage(orig, (int) (orig.getWidth() * n), (int) (orig.getHeight() * n), MODE_STRETCH);
		//		System.out.println(orig.getWidth() + " x " + orig.getHeight());
		//		orig = scaleImage(orig, (int) (orig.getWidth() * n), (int) (orig.getHeight() * n), MODE_STRETCH);
		//		orig = scaleImage(orig, 471, 250, MODE_STRETCH);
		//
		//		writeImage(new File("/home/rcooke/smartfin/test5.jpg"), orig);
	}

	public static Map<String, String> TOHEX = CH.m("white", "#ffffff", "silver", "#C0C0C0", "gray", "#808080", "black", "#000000", "red", "#ff0000", "maroon", "#800000", "yellow",
			"#FFFF00", "olive", "#979735", "lime", "#32cd32", "green", "#008000", "aqua", "#00FFFF", "teal", "#008080", "blue", "#0000FF", "navy", "#000080", "fuchsia", "#FF69B4",
			"purple", "#800080");

	public static String toHex(String s) {
		return TOHEX.get(s);
	}
	public static String generateUrlSafeString(int byteLength) {
		byte[] code = new byte[byteLength];
		MH.RANDOM_SECURE.nextBytes(code);
		//		String randomStr = Base64.getUrlEncoder().withoutPadding().encodeToString(code);
		String randomStr = SH.trim('=', EncoderUtils.encode64UrlSafe(code));
		return randomStr;
	}
	/*
	 *  Generates a urlSafeString of at least strLength
	 */
	public static String generateUrlSafeStringLen(int strLength) {
		int byteLength = (int) Math.round(3.0 / 4.0 * strLength);
		byte[] code = new byte[byteLength];
		MH.RANDOM_SECURE.nextBytes(code);
		//		String randomStr = Base64.getUrlEncoder().withoutPadding().encodeToString(code);
		String randomStr = SH.trim('=', EncoderUtils.encode64UrlSafe(code));
		return randomStr;
	}
	public static void main(String a[]) {
		String s = "\"><script>alert(1);<script> ";
		System.out.println(s);
		s = escapeHtml(s);
		System.out.println(s);
		s = escapeHtml(s);
		System.out.println(s);
		s = escapeHtml(s);
		System.out.println(s);
	}
	public static String htmlToText(String s, boolean brToNewline) {
		if (s.indexOf('<') == -1 && s.indexOf('&') == -1)
			return s;
		return HttpUtils.htmlToText(s, 0, s.length(), new StringBuilder(s.length()), brToNewline).toString();
	}
}
