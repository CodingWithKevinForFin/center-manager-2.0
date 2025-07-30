package com.f1.website;

import java.util.ArrayList;
import java.util.List;

import com.f1.utils.SH;

public class FaqState {

	public static final String ID = "faq";

	private String question = "What is AMI?";

	private List<FaqAnswer> faqAnswers = new ArrayList<FaqAnswer>();

	private boolean tooManyResults = false;

	private boolean noResults;

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public List<FaqAnswer> getFaqAnswers() {
		return faqAnswers;
	}

	public void setTooManyResults(boolean b) {
		this.tooManyResults = b;
	}

	public boolean getTooManyResults() {
		return tooManyResults;
	}

	public void setFaqAnswers(ArrayList<FaqAnswer> truncatedAnswers) {
		this.faqAnswers = truncatedAnswers;
	}

	public void setNoResults(boolean b) {
		this.noResults = b;
	}
	public boolean getQuestionExists() {
		return SH.is(question);
	}
}
