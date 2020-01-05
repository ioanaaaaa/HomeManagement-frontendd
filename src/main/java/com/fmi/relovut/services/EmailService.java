package com.fmi.relovut.services;

import com.fmi.relovut.helpers.email.templates.RegisterEmailTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.util.Date;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
public class EmailService {
    private Boolean sendEmails;
    private String host;
    private String port;
    private String username;
    private String password;
    private String fromEmail;
    private String fromName;

    private String registerEmailHtmlContent;

    @Autowired
    EmailService(@Value("${com.fmi.relovut.email.send-emails}") Boolean sendEmails,
                 @Value("${com.fmi.relovut.email.host}") String host,
                 @Value("${com.fmi.relovut.email.port}") String port,
                 @Value("${com.fmi.relovut.email.username}") String username,
                 @Value("${com.fmi.relovut.email.password}") String password,
                 @Value("${com.fmi.relovut.email.from-email}") String fromEmail,
                 @Value("${com.fmi.relovut.email.from-name}") String fromName,
                 RegisterEmailTemplate registerEmailTemplate) {
        this.sendEmails = sendEmails;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.fromEmail = fromEmail;
        this.fromName = fromName;

        this.registerEmailHtmlContent = registerEmailTemplate.getTemplate();
    }

    public void sendRegisterEmail(String toEmail) {
        this.sendEmail(toEmail,
                "You have joined Relovut!",
                registerEmailHtmlContent,
                null,
                null);
    }

    private void sendEmail(String toEmail, String subject, String htmlContent, InputStream attachment, String attachmentName) {
        if (!sendEmails)
            return;

        Session session = getSession();
        MimeMessage message = new MimeMessage(session);

        try {
            message.addHeader("Content-Type", "text/html; charset=UTF-8");
            message.addHeader("format", "flowed");
            message.addHeader("Content-Transfer-Encoding", "8bit");

            message.setFrom(new InternetAddress(fromEmail, fromName));
            message.setReplyTo(InternetAddress.parse(fromEmail, false));
            message.setSubject(subject, "UTF-8");
            message.setSentDate(new Date());
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(htmlContent, "UTF-8", "html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            //Add attachment
            if (attachment != null) {
                messageBodyPart = new MimeBodyPart(attachment);
                messageBodyPart.setFileName(attachmentName);
                multipart.addBodyPart(messageBodyPart);
            }

            message.setContent(multipart);
            Transport.send(message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Session getSession() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);

        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };
        return Session.getInstance(properties, authenticator);
    }



}
