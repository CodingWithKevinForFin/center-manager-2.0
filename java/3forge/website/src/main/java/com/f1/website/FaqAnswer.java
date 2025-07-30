package com.f1.website;

import com.f1.utils.SH;

public class FaqAnswer {

	final private int id;
	final private String question;

	public FaqAnswer(int id, String question) {
		this.id = id;
		this.question = question;
	}

	public int getId() {
		return id;
	}

	public String getQuestion() {
		return question;
	}

	public String toString() {
		return id + "-" + question;
	}

	public String getFormattedQuestion() {
		StringBuilder sb = new StringBuilder();
		sb.append("<h3>");
		sb.append(SH.beforeFirst(question, "<p>"));
		sb.append("</h3>");
		sb.append(SH.afterFirst(question, "</p>"));
		return sb.toString();

	}
}
