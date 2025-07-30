package com.f1.email;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.f1.base.Password;
import com.f1.utils.LH;
import com.f1.utils.SH;

public class EmailClient {
	private static final Logger log = Logger.getLogger(EmailClient.class.getName());

	private Session session;
	private Transport transport;
	final private boolean isDebug;
	final private String username;
	final private Password password;
	final private String host;
	final private int retries;

	public static Logger getLog() {
		return log;
	}

	public Session getSession() {
		return session;
	}

	public Transport getTransport() {
		return transport;
	}

	public boolean isDebug() {
		return isDebug;
	}

	public String getUsername() {
		return username;
	}

	public Password getPassword() {
		return password;
	}

	public String getHost() {
		return host;
	}

	public int getRetries() {
		return retries;
	}

	public Properties getProperties() {
		return properties;
	}

	public BasicAuthenticator getAuthenticator() {
		return authenticator;
	}

	public int getPort() {
		return port;
	}

	final private Properties properties;

	private BasicAuthenticator authenticator;

	private int port;

	private boolean isClosed;

	public EmailClient(EmailClientConfig config) {
		this(config, true);
	}
	public EmailClient(EmailClientConfig config, boolean connectNow) {
		this.host = config.getHost();
		this.port = config.getPort();
		this.username = config.getUsername();
		this.password = config.getPassword();
		this.isDebug = config.getEnableDebug();
		this.retries = config.getRetriesCount();
		this.properties = config.toProperties();
		this.authenticator = new BasicAuthenticator();
		if (connectNow)
			reconnect();
	}

	public void sendTextEmail(String body, String subject, List<String> toList, String from) throws IOException {
		sendEmail(body, subject, toList, from, false, null);
	}

	public void sendHtmlEmail(String body, String subject, List<String> toList, String from, Iterable<EmailAttachment> attachments) throws IOException {
		sendEmail(body, subject, toList, from, true, attachments);
	}

	synchronized public void sendEmail(String body, String subject, List<String> toList, String from, boolean isHtml, Iterable<EmailAttachment> attachments) throws IOException {
		if (isClosed)
			throw new RuntimeException("already closed");
		int tryCount = 1;
		while (true) {
			try {
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
				transport.sendMessage(message, message.getAllRecipients());
				LH.info(log, "Sent email to: ", toList, " from: ", from, " subject: ", subject);
				return;
			} catch (Exception e) {
				if (tryCount < retries) {
					reconnect();
					tryCount++;
					LH.warning(log, "error sending email to: ", toList, " from: ", from, " subject: ", subject, ". Trying reconnect...");
				} else {
					LH.severe(log, "error sending email to: ", toList, " from: ", from, " subject: ", subject, " exception: ", e);
					throw new RuntimeException("error sending email to: " + toList + " from: " + from + " with subject: " + subject, e);
				}
			}
		}
	}

	synchronized public void reconnect() {
		closeInner();
		try {
			session = Session.getInstance(properties, authenticator);
			session.setDebug(isDebug);
			transport = session.getTransport("smtp");
			LH.info(log, "connecting to '", host, "' as '", username, "'");
			transport.connect(host, port, this.username, this.password.getPasswordString());
			LH.info(log, "connected.");
		} catch (Exception e) {
			throw new RuntimeException("Error connecting to '" + host + "' as '" + username + "'", e);
		}
	}

	synchronized public void close() {
		this.isClosed = true;
		closeInner();
	}
	synchronized public void closeInner() {
		if (transport != null) {
			try {
				transport.close();
			} catch (Exception e) {
			}
			transport = null;
		}
		session = null;
	}

	private final class BasicAuthenticator extends Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			if (username == null || password == null)
				return null;
			return new PasswordAuthentication(username, password.getPasswordString());
		}
	}

}
