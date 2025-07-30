package com.f1.ami.amicommon.functions;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.f1.base.Bytes;
import com.f1.email.EmailAttachment;
import com.f1.email.EmailHelper;
import com.f1.email.MimeTypeManager;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculatorN;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionConstructEml extends AbstractMethodDerivedCellCalculatorN {
	private final static ParamsDefinition VERIFIER = new ParamsDefinition("constructEml", Bytes.class,
			"java.lang.String body,java.lang.String subject,java.util.List toList,java.lang.String from,java.lang.Boolean isHtml,java.util.List attachmentNames,java.util.List attachmentData");
	static {
		VERIFIER.addDesc("Creates a .eml Binary");
		VERIFIER.addParamDesc(0, "body");
		VERIFIER.addParamDesc(1, "subject");
		VERIFIER.addParamDesc(2, "toList");
		VERIFIER.addParamDesc(3, "from");
		VERIFIER.addParamDesc(4, "isHtml");
		VERIFIER.addParamDesc(5, "attachmentNames");
		VERIFIER.addParamDesc(6, "attachmentData");
	}

	public AmiWebFunctionConstructEml(int position, DerivedCellCalculator[] params) {
		super(position, params);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object[] o) {
		String body = (String) o[0];
		String subject = (String) o[1];
		List<String> toList = (List<String>) o[2];
		String from = (String) o[3];
		boolean isHtml = (Boolean) o[4];
		List<String> attachmentNames = (List<String>) o[5];
		List<Bytes> attachmentData = (List<Bytes>) o[6];

		try {
			MimeMessage message = constructMimeMessage(body, subject, toList, from, isHtml, attachmentNames, attachmentData);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			message.writeTo(buffer);
			byte[] targetArray = buffer.toByteArray();
			return new Bytes(targetArray);
		} catch (Exception e) {
			return e.getClass().toString();
		}
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator[] params2) {
		return new AmiWebFunctionConstructEml(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionConstructEml(position, calcs);
		}

	}

	//	:sendEmail(String fromAddress, List toAddresses, String subject, Boolean isHtml, String body, List attachmentNames, List attachmentDatas, String optionalUsername, String optionalPassword)
	public static MimeMessage constructMimeMessage(String body, String subject, List<String> toList, String from, boolean isHtml, List<String> attachmentNames,
			List<Bytes> attachmentData) throws MessagingException {
		List<EmailAttachment> attachments;
		try {
			attachments = convertDataToEmailAttachments(attachmentNames, attachmentData);
		} catch (Exception e) {
			throw new MessagingException(e.getMessage());
		}

		Properties properties = System.getProperties();
		Session session = Session.getDefaultInstance(properties);

		final MimeBodyPart mBody = new MimeBodyPart();
		final MimeMultipart mp = new MimeMultipart();
		final MimeBodyPart signature = new MimeBodyPart();
		String sign = "<br> <h2 style =\"color:#2B7FB5\";> <i> Powered by 3forge </i> </h2>";

		final MimeTypeManager mimeTypes = MimeTypeManager.getInstance();
		mBody.setText(body, "UTF-8", mimeTypes.getMimeTypeForFileName(isHtml ? "html" : "txt").getSubCatagory());
		signature.setText(sign, "UTF-8", mimeTypes.getMimeTypeForFileName("html").getSubCatagory());
		mp.addBodyPart(mBody);
		mp.addBodyPart(signature);

		if (attachments != null) {
			for (EmailAttachment a : attachments) {
				final MimeBodyPart attachmentBody = new MimeBodyPart();
				attachmentBody.setDataHandler(new DataHandler(a));
				attachmentBody.setFileName(a.getName());
				if (a.getContentId() != null)
					attachmentBody.setContentID(a.getContentId());
				mp.addBodyPart(attachmentBody);
			}
		}
		final MimeMessage message = new MimeMessage(session);
		message.setContent(mp);
		message.setSubject(subject, "UTF-8");
		message.setFrom(new InternetAddress(from));
		EmailHelper.assertEmailValid(from);
		for (String to : toList) {
			final RecipientType toType;
			if (SH.startsWithIgnoreCase(to, "cc:")) {
				toType = Message.RecipientType.CC;
				to = to.substring("cc:".length());
			} else if (SH.startsWithIgnoreCase(to, "bcc:")) {
				toType = Message.RecipientType.BCC;
				to = to.substring("bcc:".length());
			} else
				toType = Message.RecipientType.TO;
			EmailHelper.assertEmailValid(to);
			Address address = new InternetAddress(to);
			message.addRecipient(toType, address);
		}
		return message;
	}

	private static List<EmailAttachment> convertDataToEmailAttachments(List<String> attachmentNames, List<Bytes> attachmentDatas) throws Exception {
		ArrayList<EmailAttachment> attachments = new ArrayList<>();

		if (attachmentNames.size() != attachmentDatas.size()) {
			throw new Exception("Number of attachment names not equal to number of attachments");
		}

		for (int i = 0; i < attachmentDatas.size(); i++) {
			String filename = attachmentNames.get(i);
			byte[] data = attachmentDatas.get(i).getBytes();
			EmailAttachment toAdd = new EmailAttachment(data, MimeTypeManager.getInstance().getMimeTypeForFileName(filename), filename);
			attachments.add(toAdd);
		}
		return attachments;
	}

}
