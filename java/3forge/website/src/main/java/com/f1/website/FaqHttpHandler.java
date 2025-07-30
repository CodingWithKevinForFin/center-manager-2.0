package com.f1.website;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.utils.LH;
import com.f1.utils.SH;

public abstract class FaqHttpHandler extends AbstractHttpHandler {

	private static final int MAX_NUMBER_RETURNED_FAQS = 10;
	private static final Logger log = LH.get(FaqHttpHandler.class);
	private FaqManager fm;

	public FaqHttpHandler(FaqManager fm) {
		this.fm = fm;
	}

	@Override
	public void handle(HttpRequestResponse req) throws IOException {
		String question = req.getParams().get("question");
		question = question.trim().replaceAll("\\s+", " ");
		HttpSession session = req.getSession(true);
		Map<String, Object> attributes = session.getAttributes();
		FaqState faqState = (FaqState) attributes.get(FaqState.ID);
		if (faqState == null)
			attributes.put(FaqState.ID, faqState = new FaqState());
		faqState.setQuestion(question);
		req.sendRedirect("faq.html?faq_question=" + question);
		faqState.getFaqAnswers().clear();
		fm.search(question, faqState.getFaqAnswers(), req.getRemoteHost());
		if (faqState.getFaqAnswers().size() > MAX_NUMBER_RETURNED_FAQS)
			trunctateFaqAnswers(faqState);
		else
			faqState.setTooManyResults(false);
		if (faqState.getFaqAnswers().size() == 0)
			faqState.setNoResults(true);
		else
			faqState.setNoResults(false);
		String id = (String) req.getParams().get("id");
		if (id == null) {
			req.getAttributes().put("answer", null);
		} else {
			int i = SH.parseInt(id);
			FaqAnswer fa = fm.getAnswer(i);
			req.getAttributes().put("answer", fa);
		}
	}

	private void trunctateFaqAnswers(FaqState faqState) {
		faqState.setTooManyResults(true);
		ArrayList<FaqAnswer> truncatedAnswers = new ArrayList<FaqAnswer>();
		for (int i = 0; i < MAX_NUMBER_RETURNED_FAQS; i++)
			truncatedAnswers.add(faqState.getFaqAnswers().get(i));
		faqState.setFaqAnswers(truncatedAnswers);
	}
}
