package com.f1.website;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.xml.HtmlParser;
import com.f1.utils.xml.XmlElement;
import com.f1.utils.xml.XmlNode;
import com.f1.utils.xml.XmlText;

public class FaqManager {

	private static final String CLOSE_HIGHLIGHT = "</b>";
	private static final String HIGHLIGHT = "<b style=\"background-color:#FFFFB2;\">";
	private static final Logger log = LH.get(FaqManager.class);
	volatile private TreeMap<Integer, FaqAnswer> answers = new TreeMap<Integer, FaqAnswer>();
	private File root;
	private int searchTime = 0;

	public FaqManager(File root) {
		this.root = root;
	}

	public void search(String text, List<FaqAnswer> sink, String remoteHost) {
		if ("".equals(text))
			return;
		text = removePunctuation(text);
		searchTime = 0;
		TreeMap<Integer, HashSet<String>> scores = new TreeMap<Integer, HashSet<String>>();
		ArrayList<String> tags = new ArrayList<String>();
		int files = 0;
		int returnedFiles = 0;
		for (String id : root.list()) {
			files++;
			tags.clear();
			if (!SH.areBetween(id, '0', '9') || SH.isnt(id))
				continue;
			File base = new File(root, id);
			if (!base.isDirectory())
				continue;
			File index = new File(base, "index.html");
			String indexContents;
			try {
				indexContents = IOH.readText(index, true);
			} catch (IOException e1) {
				LH.warning(log, "BAD_FILE_INDEX|faq ==> ", IOH.getFullPath(index), e1);
				continue;
			}

			int score = 0;
			for (File file : base.listFiles()) {
				String name = file.getName();
				if (!name.endsWith(".html") && !name.endsWith(".htm"))
					continue;
				try {
					String fileText = name.equals("index.html") ? indexContents : IOH.readText(file);
					String[] sections = SH.split("{section}", fileText);
					if (sections.length != 3) {
						LH.warning(log, "BAD_FILE_SECTIONS|faq ==> ", id);
						continue;
					}
					String shortQuestion = sections[0];
					String fullQuestion = sections[1];
					String answer = sections[2];
					score = computeScore(shortQuestion, answer, text, fullQuestion);

				} catch (IOException e) {
					LH.warning(log, "BAD_FILE|faq ==> ", IOH.getFullPath(file), e);
					continue;
				}
			}
			if (score > 0) {
				returnedFiles++;
				String question = SH.beforeFirst(indexContents, "{section}");
				if (scores.containsKey(score)) {
					scores.get(score).add(id + "|" + question);
				} else {
					HashSet<String> faqs = new HashSet<String>();
					faqs.add(id + "|" + question);
					scores.put(score, faqs);
				}
			}
		}
		while (!scores.isEmpty()) {
			Entry<Integer, HashSet<String>> data = scores.pollLastEntry();
			for (String val : data.getValue()) {
				String question = SH.afterFirst(val, "|");
				String formattedQuestion = "";
				if (SH.trim(text).equals(""))
					formattedQuestion = question;
				else
					formattedQuestion = prepareHtml(question, text);
				sink.add(new FaqAnswer(SH.parseInt(SH.beforeFirst(val, "|")), SH.beforeFirst("<h3>" + formattedQuestion + "</h3>", "<p>")));
			}
		}
		for (FaqAnswer i : answers.values()) {
			String q = i.getFormattedQuestion();
		}
		LH.info(log, "FAQ_SEARCH|Returned ", returnedFiles, "/", files, " in ", searchTime / 1000000.0, "(ms) for ", remoteHost, "|Search ==>", text);
	}
	private String removePunctuation(String text) {
		text = SH.replaceAll(text, "?", " ");
		text = SH.replaceAll(text, ",", " ");
		text = SH.replaceAll(text, ".", " ");
		text = SH.replaceAll(text, ":", " ");
		text = SH.replaceAll(text, ";", " ");
		return text;
	}

	public void reset(List<FaqAnswer> answers) {
		TreeMap<Integer, FaqAnswer> t = new TreeMap<Integer, FaqAnswer>();
		for (FaqAnswer i : answers)
			t.put(i.getId(), i);
		this.answers = t;
	}

	public FaqAnswer getAnswer(int i) {
		return answers.get(i);
	}

	public int computeScore(String shortQuestion, String answer, String text, String fullQuestion) {
		Set<String> keywords = removeBoringWords(text);
		long time = System.nanoTime();
		int score = 0;
		if (SH.indexOfIgnoreCase(shortQuestion, text, 0) != -1) {
			score += 10;
		}
		if (SH.indexOfIgnoreCase(fullQuestion, text, 0) != -1) {
			score += 5;
		}
		if (SH.indexOfIgnoreCase(answer, text, 0) != -1) {
			score += 2;
		}
		//		for (int i = 0; i < keywords.length; i++) {
		for (String keyWord : keywords) {
			if (SH.indexOfIgnoreCase(shortQuestion, keyWord, 0) != -1) {
				score += 6;
			}
			if (SH.indexOfIgnoreCase(fullQuestion, keyWord, 0) != -1) {
				score += 3;
			}
			if (SH.indexOfIgnoreCase(answer, keyWord, 0) != -1) {
				score += 1;
			}
		}
		time = System.nanoTime() - time;
		searchTime += time;
		return score;
	}
	public String prepareHtml(String text, String userInput) {
		Set<String> keywords = CH.s(userInput.split(" "));
		HtmlParser hp = new HtmlParser();
		XmlElement html = hp.parseHtml(text);
		String formattedText = "";
		boolean highlighting = false;
		for (XmlNode xmlNode : html.getChildren()) {
			if (xmlNode instanceof XmlText) {
				formattedText += highlightText(((XmlText) xmlNode).getText(), userInput);
			} else {
				formattedText += xmlNode.toString();
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
			Tuple2<Integer, String> highlightData = highlight(start, text, userInput);
			start += highlightData.getA() + CLOSE_HIGHLIGHT.length();
			text = highlightData.getB();
		}
		return text;
	}
	private Tuple2<Integer, String> highlight(int start, String text, String userInput) {
		StringBuilder sb = new StringBuilder();
		sb.append(text);
		int highlightLength = 1;
		for (;;) {
			String c = SH.substring(text, start, start + highlightLength);
			int i = SH.indexOfIgnoreCase(userInput, SH.substring(text, start, start + highlightLength), 0);
			if (i == -1) {
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
