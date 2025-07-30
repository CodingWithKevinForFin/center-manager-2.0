package com.f1.website;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.handler.FileSystemHttpHandler;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.xml.HtmlParser;
import com.f1.utils.xml.XmlElement;
import com.f1.utils.xml.XmlNode;
import com.f1.utils.xml.XmlText;

public class FaqAnswerHttpHandler extends FileSystemHttpHandler {
	private static final String CLOSE_HIGHLIGHT = "</span>";
	private static final String HIGHLIGHT = "<span style=\"background-color:#FFFFE6;\">";
	private static final Logger log = LH.get(FaqManager.class);

	public FaqAnswerHttpHandler(boolean isResource, File base, String baseUrl, long cacheTimeMs, String indexPage) {
		super(isResource, base, baseUrl, cacheTimeMs, indexPage);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void writeOut(HttpRequestResponse request, FileInstance file) throws IOException {
		Map<String, String> params = request.getParams();
		request.getAttributes();
		String name = file.getName();
		if (name.endsWith("index.html")) {
			String text = new String(file.getData());
			String id = request.getParams().get("id");
			String[] sections = SH.split("{section}", text);
			LH.info(log, "IP ==>" + request.getRemoteHost() + " | SELECTED FAQ ID  ==> " + id + " | Title ==> " + sections[0]);
			if (sections.length != 3) {
				request.getOutputStream().print("<h1 style=\"color:red; padding-top:20px; padding-left:20px;\"> This FAQ is currently under construction </h1>");
			}
			String formattedHtml = formatSections(sections, request);
			formattedHtml = formattedHtml.replaceAll("<img +src=\"", "<img src=\"./faqanswer/" + id + "/");
			request.getOutputStream().print(formattedHtml);
		} else
			super.writeOut(request, file);
	}
	private String formatSections(String[] sections, HttpRequestResponse request) {
		String url = request.getRequestUrl();
		String shortQuestion = sections[0];
		String fullQuestion = sections[1];
		String answer = sections[2];
		if (!SH.afterFirst(request.getRequestUrl(), "question=").equals("")) {
			shortQuestion = searchForKeywords(url, shortQuestion);
			fullQuestion = searchForKeywords(url, fullQuestion);
			answer = searchForKeywords(url, answer);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<h2 style=\"background-color:#f9f9f9;padding:5px 20px 5px 20px;border-radius:4px;margin-bottom:0px;\">" + shortQuestion + "</h2>");
		sb.append("<p style=\"background-color:#f9f9f9;padding:0px 20px 0px 20px;font-style:italic;padding-bottom:15px;\"> Q: " + fullQuestion + "</p>");
		sb.append("<div style=\"padding:0px 20px 17px 20px;\">" + answer + "</div>");
		return sb.toString();
	}

	public String highlightKeyWords(String text, String userInput) {
		HtmlParser hp = new HtmlParser();
		return prepareHtml(hp.parseHtml(text), userInput);
	}

	public String prepareHtml(XmlElement html, String userInput) {
		String formattedText = "";
		for (XmlNode xmlNode : html.getChildren()) {
			if (xmlNode instanceof XmlText) {
				formattedText += highlightText(((XmlText) xmlNode).getText(), userInput);
			} else if (xmlNode instanceof XmlElement) {
				if ("IMG".equals(((XmlElement) xmlNode).getName()))
					formattedText += "<img  src=\"" + ((XmlElement) xmlNode).getAttribute("SRC") + "\" style = \" " + ((XmlElement) xmlNode).getAttribute("STYLE") + "\">";
				else if ("A".equals(((XmlElement) xmlNode).getName()))
					formattedText += "<a href= \"" + ((XmlElement) xmlNode).getAttribute("HREF") + "\" target=\"" + ((XmlElement) xmlNode).getAttribute("TARGET") + "\" "
							+ "\" style = \" " + ((XmlElement) xmlNode).getAttribute("STYLE") + "\">";
				else
					formattedText += "<" + ((XmlElement) xmlNode).getName() + " style = \" " + ((XmlElement) xmlNode).getAttribute("STYLE") + "\">";
				formattedText += prepareHtml((XmlElement) xmlNode, userInput);
				if (!((XmlElement) xmlNode).getName().equals("BR"))
					formattedText += "</" + ((XmlElement) xmlNode).getName() + ">";
			}
		}
		return formattedText;
	}
	private String highlightText(String text, String userInput) {
		int i = 0;
		int start = 0;
		for (;;) {
			i = findNextIndex(text, userInput, start);
			if (i == -1)
				break;
			text = startHighlight(i, text);
			start = i + HIGHLIGHT.length();
			Tuple2<Integer, String> tuple = highlight(start, text, userInput);
			start += tuple.getA() + CLOSE_HIGHLIGHT.length();
			text = tuple.getB();
		}
		return text;
	}
	private Tuple2<Integer, String> highlight(int start, String text, String userInput) {
		int length = text.length();
		StringBuilder sb = new StringBuilder();
		sb.append(text);
		int highlightLength = 1;
		for (;;) {
			String c = SH.substring(text, start, start + highlightLength);
			int i = SH.indexOfIgnoreCase(userInput, SH.substring(text, start, start + highlightLength), 0);
			if (highlightLength + start == length) {
				sb.insert(start + highlightLength, CLOSE_HIGHLIGHT);
				break;
			}
			if (i == -1 || highlightLength + start == length) {
				sb.insert(start + highlightLength - 1, CLOSE_HIGHLIGHT);
				break;
			} else
				highlightLength++;
		}
		Tuple2<Integer, String> tuple = new Tuple2<Integer, String>();
		tuple.setAB(highlightLength, sb.toString());
		return tuple;
	}

	private String startHighlight(int startPosition, String text) {
		StringBuilder sb = new StringBuilder();
		sb.append(text);
		sb.insert(startPosition, HIGHLIGHT);
		return sb.toString();
	}

	private int findNextIndex(String text, String userInput, int start) {
		Set<String> keywords = removeBoringWords(userInput);
		int nextIndex = SH.indexOfIgnoreCase(text, userInput, start);
		for (String keyword : keywords) {
			int n = SH.indexOfIgnoreCase(text, keyword, start);
			if (n >= 0) {
				if (nextIndex < 0)
					nextIndex = n;
				else if (n < nextIndex)
					nextIndex = n;

			}
		}
		return nextIndex;
	}

	public String searchForKeywords(String url, String text) {
		String keys = SH.afterFirst(url, "question=");
		String userInput = SH.join(" ", keys.split("%20"));
		HtmlParser hp = new HtmlParser();
		return prepareHtml(hp.parseHtml(text), userInput);
	}

	private Set<String> removeBoringWords(String userInput) {
		String boringWords = " how the be to of and a in that hava i it for not on with he as you do at this but his by from they we say her she or an will my one all would there their what so up out if about who get which go me when make can like time no just him know take people into year your good some could them see other than then now look only come its  ";
		Set<String> keywords = CH.s(SH.split(" ", userInput));
		Set<String> keys = new HashSet<String>();
		CH.addAll(keys, keywords);
		for (String key : keys) {
			if (SH.indexOfIgnoreCase(boringWords, " " + key + " ", 0) != -1)
				CH.removeOrThrow(keywords, key);
		}
		return keywords;
	}
}
