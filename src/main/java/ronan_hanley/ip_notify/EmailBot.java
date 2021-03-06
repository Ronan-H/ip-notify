package ronan_hanley.ip_notify;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailBot {
    private String username;
    private String password;
    private String recipient;

    public EmailBot(String username, String password, String recipient) {
        this.username = username;
        this.password = password;
        this.recipient = recipient;
    }

    public void sendIPChangeNotification(String fromIP, String toIP, int retryTimeout) throws InterruptedException {
        String subject = "Notification of IP Address Change";

        String body = "Hello,\n\n"
                + "This is an automated email to inform you that an IP address change has been detected.\n"
                + String.format("It appears to have changed from %s to %s.\n", fromIP.replace(".", "-"), toIP.replace(".", "-"))
                + "Don't forget to re-launch IP Notify using the new IP address to get continued notifications of changes of IP.\n";

        sendEmail(username, password, recipient, subject, body, retryTimeout);
    }

    public void sendEmail(String from, String pass, String recipient, String subject, String body, int retryTimeout) throws InterruptedException {
        Properties props = System.getProperties();
        String host = "smtp.mail.com";
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", pass);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);

        boolean messageSent = false;
        while (!messageSent) {
            try {
                message.setFrom(new InternetAddress(from));
                InternetAddress toAddress = new InternetAddress(recipient);
                message.addRecipient(Message.RecipientType.TO, toAddress);

                message.setSubject(subject);
                message.setText(body);
                Transport transport = session.getTransport("smtp");
                transport.connect(host, from, pass);
                transport.sendMessage(message, message.getAllRecipients());
                transport.close();

                System.out.println("Successfully sent the notification email.");
                messageSent = true;
            }
            catch (MessagingException e) {
                System.out.printf("Some error occurred while trying to send the email, sleeping for %d second(s) before trying again...%n", retryTimeout);
                Thread.sleep(retryTimeout * 1000);
            }
        }
    }
}