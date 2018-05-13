import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class Main {

    private static final String URL = "xxxx";
    private static final String HOST = "xxxx";
    private static final String PORT = "xxxx";
    private static final String USER = "xxxx";
    private static final String PASSWORD = "xxxx";
    private static final String RECIPIENT = "xxxx";

    public static void main(String[] args) {
        try {
            Document doc = Jsoup.connect(URL).get();

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

    private static void sendEmail(final String messageText) {
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

            System.out.print("Message sent");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}