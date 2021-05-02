package com.gizlocorp.adm.utilitario;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.excepcion.MessageNotSentException;

public class MailDelivery {

	private static Logger log = Logger.getLogger(MailDelivery.class.getName());

	public static void main(String[] args) {
		// final String username = "jose.vinueza@gizlocorp.com";
		// final String password = "JoseVinueza872";

		// Properties props = new Properties();
		// props.put("mail.smtp.host", "smtp.gmail.com");
		// props.put("mail.smtp.socketFactory.port", "465");
		// props.put("mail.smtp.socketFactory.class",
		// "javax.net.ssl.SSLSocketFactory");
		// props.put("mail.smtp.auth", "true");
		// props.put("mail.smtp.port", "465");
		//
		// Session session = Session.getDefaultInstance(props,
		// new javax.mail.Authenticator() {
		// protected PasswordAuthentication getPasswordAuthentication() {
		// return new PasswordAuthentication(username, password);
		// }
		// });
		//
		// // Session session = Session.getInstance(props, null);
		//
		try {
			//
			// Message message = new MimeMessage(session);
			// message.setFrom(new
			// InternetAddress("jose.vinueza@gizlocorp.com"));
			// message.setRecipients(Message.RecipientType.TO,
			// InternetAddress.parse("jose.vinueza@gizlocorp.com"));
			// message.setSubject("Error en servicio Posicion Consolidada");
			// message.setText("Dear Mail Crawler,"
			// + "\n\n No spam to my email, please!");
			//
			// Transport.send(message);

			MailMessage mailMensaje = new MailMessage();
			mailMensaje.setSubject("prueba");
			mailMensaje.setFrom("jose.vinueza@gizlocorp.com");
			mailMensaje.setTo(Arrays.asList("carla.salvatierra@gizlocorp.com"));
			mailMensaje.setBody("prueba");
			MailDelivery.send(mailMensaje, "smtp.gmail.com", "465", "josevinueza87@gmail.com", "JoseVinueza872","nada");

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void send(final DeliveryMessage mailMessage, final String smtpHost, final String smtpPort,
			final String smtpUsername, final String smtpPassword, final String nombreEmisor)
					throws MessageNotSentException {

		//envioCorreo();

		if (!(mailMessage instanceof MailMessage)) {
			throw new MessageNotSentException("El mensaje no es de tipo mail");
		}

		try {
			Properties props = new Properties();
			props.put("mail.smtp.host", smtpHost);
			// props.put("mail.smtp.socketFactory.port", "25");

			// props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.port", smtpPort);

			Session session = null;
			if (smtpPassword != null && !smtpPassword.isEmpty()) {
				props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
				props.put("mail.smtp.auth", "true");
				session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(smtpUsername, smtpPassword);
					}
				});
			} else {
				props.put("mail.smtp.auth", "false");
				session = Session.getInstance(props, null);
			}
			//session.setDebug(true);
			Message message = new MimeMessage(session);

			if (nombreEmisor != null && !nombreEmisor.isEmpty()) {
				message.setFrom(
						new InternetAddress(mailMessage.getFrom(), nombreEmisor + " (Documentos Electronicos)"));

			} else {
				message.setFrom(new InternetAddress(mailMessage.getFrom()));

			}

			StringBuilder recipients = new StringBuilder();
			// recipients.append("andres.giler@gizlocorp.com");

			if (mailMessage.getTo() != null && !mailMessage.getTo().isEmpty()) {
				int counter = 1;
				for (String to : mailMessage.getTo()) {
					recipients.append(to);
					if (counter != mailMessage.getTo().size()) {
						recipients.append(",");
					}
					counter++;
				}
			}
			
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients.toString()));
			message.setSubject(mailMessage.getSubject());

			MailMessage mm = (MailMessage) mailMessage;
			prepareMessage(mm, message);
		
			Transport.send(message);
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new MessageNotSentException(e);
		}
	}

	/**
	 * Prepara el mensaje a ser enviado por correo electronico
	 * 
	 * @param mailMessage
	 * @param session
	 * @return mensaje
	 * @throws MessagingException
	 * @throws IOException
	 */
	private static void prepareMessage(final MailMessage mailMessage, final Message message)
			throws MessagingException, IOException {
		message.setSentDate(new Date());

		// email message text.
		MimeBodyPart messagePart = new MimeBodyPart();
		// messagePart.setText(mailMessage.getBody());
		// BodyPart bodyHtml = new MimeBodyPart();
		messagePart.setContent(mailMessage.getBody(), "text/html");

		// email attachment file
		MimeBodyPart attachmentPart = null, attachmentPDFPart = null;
		if (mailMessage.getAttachment() != null && !mailMessage.getAttachment().isEmpty()) {

			if (mailMessage.getAttachment().get(0) != null && !mailMessage.getAttachment().get(0).contains("null")) {
				FileDataSource fileDataSource = new FileDataSource(mailMessage.getAttachment().get(0)) {

					@Override
					public String getContentType() {
						return "text/xml";
					}
				};

				String[] fileNameXml = mailMessage.getAttachment().get(0).split("/");
				attachmentPart = new MimeBodyPart();
				attachmentPart.setDataHandler(new DataHandler(fileDataSource));
				attachmentPart.setFileName(fileNameXml[fileNameXml.length - 1]);
			}

			// PDF part
			log.warn("Mail: -> "+mailMessage.getAttachment().get(1));
			if (mailMessage.getAttachment().get(1) != null
					&& !mailMessage.getAttachment().get(1).contains("null.pdf")) {
				String[] fileNamePdf = mailMessage.getAttachment().get(1).split("/");

				if (!fileNamePdf[fileNamePdf.length - 1].equals("null.pdf")) {
					File pdf = new File(mailMessage.getAttachment().get(1));
					if (pdf.exists()) {
						attachmentPDFPart = new MimeBodyPart();
						FileDataSource filePDFDataSource = new FileDataSource(mailMessage.getAttachment().get(1)) {

							@Override
							public String getContentType() {
								return "application/pdf";
							}
						};

						attachmentPDFPart.setDataHandler(new DataHandler(filePDFDataSource));
						attachmentPDFPart.setFileName(fileNamePdf[fileNamePdf.length - 1]);

					}
				}
			}
		}

		Multipart multipart = new MimeMultipart();
		// multipart.addBodyPart(bodyHtml);

		if (messagePart != null) {
			multipart.addBodyPart(messagePart);
		}
		if (attachmentPart != null) {
			multipart.addBodyPart(attachmentPart);
		}
		if (attachmentPDFPart != null) {
			multipart.addBodyPart(attachmentPDFPart);
		}
		message.setContent(multipart);

	}

}
