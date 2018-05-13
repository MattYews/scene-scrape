package com.scenescrape;

import com.amazonaws.services.lambda.runtime.Context;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Properties;

public class Main {

    private static final String URL = System.getenv("URL");
    private static final String HOST = System.getenv("HOST");
    private static final String PORT = System.getenv("PORT");
    private static final String USER = System.getenv("USER");
    private static final String PASSWORD = System.getenv("PASSWORD");
    private static final String RECIPIENT = System.getenv("RECIPIENT");

    public String handleRequest(Map<String, Object> input, Context context) {
        scrape();
        context.getLogger().log("Email sent successfully");

        return null;
    }

    private void scrape() {
        try {
            Document doc = Jsoup.connect(URL).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36").get();

            StringBuilder builder = new StringBuilder();
            builder.append("Here are the latest releases: \n\n\n");

            for (Element el : doc.select("h2")) {
                builder.append(el.text());
                builder.append("\n");
            }

            sendEmail(builder.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendEmail(final String messageText) {
        Properties properties = new Properties();
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.trust", HOST);
        properties.put("mail.smtp.host", HOST);
        properties.put("mail.smtp.port", PORT);
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USER, PASSWORD);
                    }
                });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USER));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(RECIPIENT));
            message.setSubject("Scene Release");
            message.setText(messageText);

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
